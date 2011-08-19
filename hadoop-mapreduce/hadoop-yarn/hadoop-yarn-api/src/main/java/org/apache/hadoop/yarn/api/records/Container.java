begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
package|;
end_package

begin_interface
DECL|interface|Container
specifier|public
interface|interface
name|Container
extends|extends
name|Comparable
argument_list|<
name|Container
argument_list|>
block|{
DECL|method|getId ()
name|ContainerId
name|getId
parameter_list|()
function_decl|;
DECL|method|getNodeId ()
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
DECL|method|getNodeHttpAddress ()
name|String
name|getNodeHttpAddress
parameter_list|()
function_decl|;
DECL|method|getResource ()
name|Resource
name|getResource
parameter_list|()
function_decl|;
DECL|method|getState ()
name|ContainerState
name|getState
parameter_list|()
function_decl|;
DECL|method|getContainerToken ()
name|ContainerToken
name|getContainerToken
parameter_list|()
function_decl|;
DECL|method|getContainerStatus ()
name|ContainerStatus
name|getContainerStatus
parameter_list|()
function_decl|;
DECL|method|setId (ContainerId id)
name|void
name|setId
parameter_list|(
name|ContainerId
name|id
parameter_list|)
function_decl|;
DECL|method|setNodeId (NodeId nodeId)
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
DECL|method|setNodeHttpAddress (String nodeHttpAddress)
name|void
name|setNodeHttpAddress
parameter_list|(
name|String
name|nodeHttpAddress
parameter_list|)
function_decl|;
DECL|method|setResource (Resource resource)
name|void
name|setResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
function_decl|;
DECL|method|setState (ContainerState state)
name|void
name|setState
parameter_list|(
name|ContainerState
name|state
parameter_list|)
function_decl|;
DECL|method|setContainerToken (ContainerToken containerToken)
name|void
name|setContainerToken
parameter_list|(
name|ContainerToken
name|containerToken
parameter_list|)
function_decl|;
DECL|method|setContainerStatus (ContainerStatus containerStatus)
name|void
name|setContainerStatus
parameter_list|(
name|ContainerStatus
name|containerStatus
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

