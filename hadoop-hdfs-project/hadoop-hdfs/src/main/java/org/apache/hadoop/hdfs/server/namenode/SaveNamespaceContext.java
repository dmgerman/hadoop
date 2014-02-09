begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
operator|.
name|Canceler
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Context for an ongoing SaveNamespace operation. This class  * allows cancellation, and also is responsible for accumulating  * failed storage directories.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SaveNamespaceContext
specifier|public
class|class
name|SaveNamespaceContext
block|{
DECL|field|sourceNamesystem
specifier|private
specifier|final
name|FSNamesystem
name|sourceNamesystem
decl_stmt|;
DECL|field|txid
specifier|private
specifier|final
name|long
name|txid
decl_stmt|;
DECL|field|errorSDs
specifier|private
specifier|final
name|List
argument_list|<
name|StorageDirectory
argument_list|>
name|errorSDs
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|StorageDirectory
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|canceller
specifier|private
specifier|final
name|Canceler
name|canceller
decl_stmt|;
DECL|field|completionLatch
specifier|private
name|CountDownLatch
name|completionLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|SaveNamespaceContext ( FSNamesystem sourceNamesystem, long txid, Canceler canceller)
name|SaveNamespaceContext
parameter_list|(
name|FSNamesystem
name|sourceNamesystem
parameter_list|,
name|long
name|txid
parameter_list|,
name|Canceler
name|canceller
parameter_list|)
block|{
name|this
operator|.
name|sourceNamesystem
operator|=
name|sourceNamesystem
expr_stmt|;
name|this
operator|.
name|txid
operator|=
name|txid
expr_stmt|;
name|this
operator|.
name|canceller
operator|=
name|canceller
expr_stmt|;
block|}
DECL|method|getSourceNamesystem ()
name|FSNamesystem
name|getSourceNamesystem
parameter_list|()
block|{
return|return
name|sourceNamesystem
return|;
block|}
DECL|method|getTxId ()
name|long
name|getTxId
parameter_list|()
block|{
return|return
name|txid
return|;
block|}
DECL|method|reportErrorOnStorageDirectory (StorageDirectory sd)
name|void
name|reportErrorOnStorageDirectory
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
block|{
name|errorSDs
operator|.
name|add
argument_list|(
name|sd
argument_list|)
expr_stmt|;
block|}
DECL|method|getErrorSDs ()
name|List
argument_list|<
name|StorageDirectory
argument_list|>
name|getErrorSDs
parameter_list|()
block|{
return|return
name|errorSDs
return|;
block|}
DECL|method|markComplete ()
name|void
name|markComplete
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|completionLatch
operator|.
name|getCount
argument_list|()
operator|==
literal|1
argument_list|,
literal|"Context already completed!"
argument_list|)
expr_stmt|;
name|completionLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
DECL|method|checkCancelled ()
specifier|public
name|void
name|checkCancelled
parameter_list|()
throws|throws
name|SaveNamespaceCancelledException
block|{
if|if
condition|(
name|canceller
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SaveNamespaceCancelledException
argument_list|(
name|canceller
operator|.
name|getCancellationReason
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

