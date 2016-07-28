begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
comment|/**  * An enumeration of logs available on a remote NameNode.  */
end_comment

begin_class
DECL|class|RemoteEditLogManifest
specifier|public
class|class
name|RemoteEditLogManifest
block|{
DECL|field|logs
specifier|private
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|logs
decl_stmt|;
DECL|field|committedTxnId
specifier|private
name|long
name|committedTxnId
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|RemoteEditLogManifest ()
specifier|public
name|RemoteEditLogManifest
parameter_list|()
block|{   }
DECL|method|RemoteEditLogManifest (List<RemoteEditLog> logs, long committedTxnId)
specifier|public
name|RemoteEditLogManifest
parameter_list|(
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|logs
parameter_list|,
name|long
name|committedTxnId
parameter_list|)
block|{
name|this
operator|.
name|logs
operator|=
name|logs
expr_stmt|;
name|this
operator|.
name|committedTxnId
operator|=
name|committedTxnId
expr_stmt|;
name|checkState
argument_list|()
expr_stmt|;
block|}
comment|/**    * Check that the logs are non-overlapping sequences of transactions,    * in sorted order. They do not need to be contiguous.    * @throws IllegalStateException if incorrect    */
DECL|method|checkState ()
specifier|private
name|void
name|checkState
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|logs
argument_list|)
expr_stmt|;
name|RemoteEditLog
name|prev
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RemoteEditLog
name|log
range|:
name|logs
control|)
block|{
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|getStartTxId
argument_list|()
operator|<=
name|prev
operator|.
name|getEndTxId
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Invalid log manifest (log "
operator|+
name|log
operator|+
literal|" overlaps "
operator|+
name|prev
operator|+
literal|")\n"
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
name|prev
operator|=
name|log
expr_stmt|;
block|}
block|}
DECL|method|getLogs ()
specifier|public
name|List
argument_list|<
name|RemoteEditLog
argument_list|>
name|getLogs
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|logs
argument_list|)
return|;
block|}
DECL|method|getCommittedTxnId ()
specifier|public
name|long
name|getCommittedTxnId
parameter_list|()
block|{
return|return
name|committedTxnId
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|logs
argument_list|)
operator|+
literal|"]"
operator|+
literal|" CommittedTxId: "
operator|+
name|committedTxnId
return|;
block|}
block|}
end_class

end_unit

