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
operator|.
name|Namesystem
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_comment
comment|/**  * Interface used to implement a decommission and maintenance monitor class,  * which is instantiated by the DatanodeAdminManager class.  */
end_comment

begin_interface
DECL|interface|DatanodeAdminMonitorInterface
specifier|public
interface|interface
name|DatanodeAdminMonitorInterface
extends|extends
name|Runnable
block|{
DECL|method|stopTrackingNode (DatanodeDescriptor dn)
name|void
name|stopTrackingNode
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|)
function_decl|;
DECL|method|startTrackingNode (DatanodeDescriptor dn)
name|void
name|startTrackingNode
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|)
function_decl|;
DECL|method|getPendingNodeCount ()
name|int
name|getPendingNodeCount
parameter_list|()
function_decl|;
DECL|method|getTrackedNodeCount ()
name|int
name|getTrackedNodeCount
parameter_list|()
function_decl|;
DECL|method|getNumNodesChecked ()
name|int
name|getNumNodesChecked
parameter_list|()
function_decl|;
DECL|method|getPendingNodes ()
name|Queue
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|getPendingNodes
parameter_list|()
function_decl|;
DECL|method|setBlockManager (BlockManager bm)
name|void
name|setBlockManager
parameter_list|(
name|BlockManager
name|bm
parameter_list|)
function_decl|;
DECL|method|setDatanodeAdminManager (DatanodeAdminManager dnm)
name|void
name|setDatanodeAdminManager
parameter_list|(
name|DatanodeAdminManager
name|dnm
parameter_list|)
function_decl|;
DECL|method|setNameSystem (Namesystem ns)
name|void
name|setNameSystem
parameter_list|(
name|Namesystem
name|ns
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

