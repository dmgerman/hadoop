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
name|ContainerId
import|;
end_import

begin_interface
DECL|interface|HeartbeatResponse
specifier|public
interface|interface
name|HeartbeatResponse
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
DECL|method|getContainersToCleanupList ()
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getContainersToCleanupList
parameter_list|()
function_decl|;
DECL|method|getContainerToCleanup (int index)
name|ContainerId
name|getContainerToCleanup
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getContainersToCleanupCount ()
name|int
name|getContainersToCleanupCount
parameter_list|()
function_decl|;
DECL|method|getApplicationsToCleanupList ()
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getApplicationsToCleanupList
parameter_list|()
function_decl|;
DECL|method|getApplicationsToCleanup (int index)
name|ApplicationId
name|getApplicationsToCleanup
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getApplicationsToCleanupCount ()
name|int
name|getApplicationsToCleanupCount
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
DECL|method|addContainerToCleanup (ContainerId container)
name|void
name|addContainerToCleanup
parameter_list|(
name|ContainerId
name|container
parameter_list|)
function_decl|;
DECL|method|removeContainerToCleanup (int index)
name|void
name|removeContainerToCleanup
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearContainersToCleanup ()
name|void
name|clearContainersToCleanup
parameter_list|()
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
DECL|method|addApplicationToCleanup (ApplicationId applicationId)
name|void
name|addApplicationToCleanup
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
function_decl|;
DECL|method|removeApplicationToCleanup (int index)
name|void
name|removeApplicationToCleanup
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearApplicationsToCleanup ()
name|void
name|clearApplicationsToCleanup
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

