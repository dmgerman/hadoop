begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
DECL|interface|NodeReport
specifier|public
interface|interface
name|NodeReport
block|{
DECL|method|getNodeId ()
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
DECL|method|setNodeId (NodeId nodeId)
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
DECL|method|getHttpAddress ()
name|String
name|getHttpAddress
parameter_list|()
function_decl|;
DECL|method|setHttpAddress (String httpAddress)
name|void
name|setHttpAddress
parameter_list|(
name|String
name|httpAddress
parameter_list|)
function_decl|;
DECL|method|getRackName ()
name|String
name|getRackName
parameter_list|()
function_decl|;
DECL|method|setRackName (String rackName)
name|void
name|setRackName
parameter_list|(
name|String
name|rackName
parameter_list|)
function_decl|;
DECL|method|getUsed ()
name|Resource
name|getUsed
parameter_list|()
function_decl|;
DECL|method|setUsed (Resource used)
name|void
name|setUsed
parameter_list|(
name|Resource
name|used
parameter_list|)
function_decl|;
DECL|method|getCapability ()
name|Resource
name|getCapability
parameter_list|()
function_decl|;
DECL|method|setCapability (Resource capability)
name|void
name|setCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
DECL|method|getNumContainers ()
name|int
name|getNumContainers
parameter_list|()
function_decl|;
DECL|method|setNumContainers (int numContainers)
name|void
name|setNumContainers
parameter_list|(
name|int
name|numContainers
parameter_list|)
function_decl|;
DECL|method|getNodeHealthStatus ()
name|NodeHealthStatus
name|getNodeHealthStatus
parameter_list|()
function_decl|;
DECL|method|setNodeHealthStatus (NodeHealthStatus nodeHealthStatus)
name|void
name|setNodeHealthStatus
parameter_list|(
name|NodeHealthStatus
name|nodeHealthStatus
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

