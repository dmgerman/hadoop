begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ResourceOption
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
name|yarn
operator|.
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|NMContainerStatus
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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

begin_comment
comment|/**  * Implementations of this class are notified of changes to the cluster's state,  * such as node addition, removal and updates.  */
end_comment

begin_interface
DECL|interface|ClusterMonitor
specifier|public
interface|interface
name|ClusterMonitor
block|{
DECL|method|addNode (List<NMContainerStatus> containerStatuses, RMNode rmNode)
name|void
name|addNode
parameter_list|(
name|List
argument_list|<
name|NMContainerStatus
argument_list|>
name|containerStatuses
parameter_list|,
name|RMNode
name|rmNode
parameter_list|)
function_decl|;
DECL|method|removeNode (RMNode removedRMNode)
name|void
name|removeNode
parameter_list|(
name|RMNode
name|removedRMNode
parameter_list|)
function_decl|;
DECL|method|updateNode (RMNode rmNode)
name|void
name|updateNode
parameter_list|(
name|RMNode
name|rmNode
parameter_list|)
function_decl|;
DECL|method|updateNodeResource (RMNode rmNode, ResourceOption resourceOption)
name|void
name|updateNodeResource
parameter_list|(
name|RMNode
name|rmNode
parameter_list|,
name|ResourceOption
name|resourceOption
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

