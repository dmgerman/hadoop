begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|protocolrecords
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
name|ContainerId
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
name|records
operator|.
name|MasterKey
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
name|records
operator|.
name|NodeAction
import|;
end_import

begin_interface
DECL|interface|NodeHeartbeatResponse
specifier|public
interface|interface
name|NodeHeartbeatResponse
block|{
DECL|method|getResponseId ()
name|int
name|getResponseId
parameter_list|()
function_decl|;
DECL|method|getNodeAction ()
name|NodeAction
name|getNodeAction
parameter_list|()
function_decl|;
DECL|method|getContainersToCleanup ()
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getContainersToCleanup
parameter_list|()
function_decl|;
DECL|method|getApplicationsToCleanup ()
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getApplicationsToCleanup
parameter_list|()
function_decl|;
DECL|method|setResponseId (int responseId)
name|void
name|setResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
function_decl|;
DECL|method|setNodeAction (NodeAction action)
name|void
name|setNodeAction
parameter_list|(
name|NodeAction
name|action
parameter_list|)
function_decl|;
DECL|method|getMasterKey ()
name|MasterKey
name|getMasterKey
parameter_list|()
function_decl|;
DECL|method|setMasterKey (MasterKey secretKey)
name|void
name|setMasterKey
parameter_list|(
name|MasterKey
name|secretKey
parameter_list|)
function_decl|;
DECL|method|addAllContainersToCleanup (List<ContainerId> containers)
name|void
name|addAllContainersToCleanup
parameter_list|(
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containers
parameter_list|)
function_decl|;
DECL|method|addAllApplicationsToCleanup (List<ApplicationId> applications)
name|void
name|addAllApplicationsToCleanup
parameter_list|(
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|applications
parameter_list|)
function_decl|;
DECL|method|getNextHeartBeatInterval ()
name|long
name|getNextHeartBeatInterval
parameter_list|()
function_decl|;
DECL|method|setNextHeartBeatInterval (long nextHeartBeatInterval)
name|void
name|setNextHeartBeatInterval
parameter_list|(
name|long
name|nextHeartBeatInterval
parameter_list|)
function_decl|;
DECL|method|getDiagnosticsMessage ()
name|String
name|getDiagnosticsMessage
parameter_list|()
function_decl|;
DECL|method|setDiagnosticsMessage (String diagnosticsMessage)
name|void
name|setDiagnosticsMessage
parameter_list|(
name|String
name|diagnosticsMessage
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

