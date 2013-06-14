begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.records
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
name|api
operator|.
name|records
package|;
end_package

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
name|ApplicationId
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
name|api
operator|.
name|records
operator|.
name|ContainerStatus
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
name|api
operator|.
name|records
operator|.
name|NodeId
import|;
end_import

begin_interface
DECL|interface|NodeStatus
specifier|public
interface|interface
name|NodeStatus
block|{
DECL|method|getNodeId ()
specifier|public
specifier|abstract
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
DECL|method|getResponseId ()
specifier|public
specifier|abstract
name|int
name|getResponseId
parameter_list|()
function_decl|;
DECL|method|getContainersStatuses ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getContainersStatuses
parameter_list|()
function_decl|;
DECL|method|setContainersStatuses ( List<ContainerStatus> containersStatuses)
specifier|public
specifier|abstract
name|void
name|setContainersStatuses
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|containersStatuses
parameter_list|)
function_decl|;
DECL|method|getKeepAliveApplications ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getKeepAliveApplications
parameter_list|()
function_decl|;
DECL|method|setKeepAliveApplications (List<ApplicationId> appIds)
specifier|public
specifier|abstract
name|void
name|setKeepAliveApplications
parameter_list|(
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|appIds
parameter_list|)
function_decl|;
DECL|method|getNodeHealthStatus ()
name|NodeHealthStatus
name|getNodeHealthStatus
parameter_list|()
function_decl|;
DECL|method|setNodeHealthStatus (NodeHealthStatus healthStatus)
name|void
name|setNodeHealthStatus
parameter_list|(
name|NodeHealthStatus
name|healthStatus
parameter_list|)
function_decl|;
DECL|method|setNodeId (NodeId nodeId)
specifier|public
specifier|abstract
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
DECL|method|setResponseId (int responseId)
specifier|public
specifier|abstract
name|void
name|setResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

