begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|annotations
operator|.
name|VisibleForTesting
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
name|protocol
operator|.
name|DatanodeID
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
name|protocol
operator|.
name|BlockReportContext
import|;
end_import

begin_comment
comment|/**  * Used to inject certain faults for testing.  */
end_comment

begin_class
DECL|class|BlockManagerFaultInjector
specifier|public
class|class
name|BlockManagerFaultInjector
block|{
annotation|@
name|VisibleForTesting
DECL|field|instance
specifier|public
specifier|static
name|BlockManagerFaultInjector
name|instance
init|=
operator|new
name|BlockManagerFaultInjector
argument_list|()
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|getInstance ()
specifier|public
specifier|static
name|BlockManagerFaultInjector
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|incomingBlockReportRpc (DatanodeID nodeID, BlockReportContext context)
specifier|public
name|void
name|incomingBlockReportRpc
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|BlockReportContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{    }
annotation|@
name|VisibleForTesting
DECL|method|requestBlockReportLease (DatanodeDescriptor node, long leaseId)
specifier|public
name|void
name|requestBlockReportLease
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|,
name|long
name|leaseId
parameter_list|)
block|{   }
annotation|@
name|VisibleForTesting
DECL|method|removeBlockReportLease (DatanodeDescriptor node, long leaseId)
specifier|public
name|void
name|removeBlockReportLease
parameter_list|(
name|DatanodeDescriptor
name|node
parameter_list|,
name|long
name|leaseId
parameter_list|)
block|{   }
block|}
end_class

end_unit

