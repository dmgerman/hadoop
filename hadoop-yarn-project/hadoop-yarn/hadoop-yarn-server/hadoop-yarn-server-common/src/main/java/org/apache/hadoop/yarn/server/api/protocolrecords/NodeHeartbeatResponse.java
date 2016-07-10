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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Map
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
name|protocolrecords
operator|.
name|SignalContainerRequest
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
name|Container
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|ContainerQueuingLimit
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
DECL|method|getContainersToBeRemovedFromNM ()
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getContainersToBeRemovedFromNM
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
comment|// This tells NM the collectors' address info of related apps
DECL|method|getAppCollectorsMap ()
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|String
argument_list|>
name|getAppCollectorsMap
parameter_list|()
function_decl|;
DECL|method|setAppCollectorsMap (Map<ApplicationId, String> appCollectorsMap)
name|void
name|setAppCollectorsMap
parameter_list|(
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|String
argument_list|>
name|appCollectorsMap
parameter_list|)
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
DECL|method|getContainerTokenMasterKey ()
name|MasterKey
name|getContainerTokenMasterKey
parameter_list|()
function_decl|;
DECL|method|setContainerTokenMasterKey (MasterKey secretKey)
name|void
name|setContainerTokenMasterKey
parameter_list|(
name|MasterKey
name|secretKey
parameter_list|)
function_decl|;
DECL|method|getNMTokenMasterKey ()
name|MasterKey
name|getNMTokenMasterKey
parameter_list|()
function_decl|;
DECL|method|setNMTokenMasterKey (MasterKey secretKey)
name|void
name|setNMTokenMasterKey
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
comment|// This tells NM to remove finished containers from its context. Currently, NM
comment|// will remove finished containers from its context only after AM has actually
comment|// received the finished containers in a previous allocate response
DECL|method|addContainersToBeRemovedFromNM (List<ContainerId> containers)
name|void
name|addContainersToBeRemovedFromNM
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
DECL|method|getContainersToSignalList ()
name|List
argument_list|<
name|SignalContainerRequest
argument_list|>
name|getContainersToSignalList
parameter_list|()
function_decl|;
DECL|method|addAllContainersToSignal (List<SignalContainerRequest> containers)
name|void
name|addAllContainersToSignal
parameter_list|(
name|List
argument_list|<
name|SignalContainerRequest
argument_list|>
name|containers
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
comment|// Credentials (i.e. hdfs tokens) needed by NodeManagers for application
comment|// localizations and logAggreations.
DECL|method|getSystemCredentialsForApps ()
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|getSystemCredentialsForApps
parameter_list|()
function_decl|;
DECL|method|setSystemCredentialsForApps ( Map<ApplicationId, ByteBuffer> systemCredentials)
name|void
name|setSystemCredentialsForApps
parameter_list|(
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|systemCredentials
parameter_list|)
function_decl|;
DECL|method|getAreNodeLabelsAcceptedByRM ()
name|boolean
name|getAreNodeLabelsAcceptedByRM
parameter_list|()
function_decl|;
DECL|method|setAreNodeLabelsAcceptedByRM (boolean areNodeLabelsAcceptedByRM)
name|void
name|setAreNodeLabelsAcceptedByRM
parameter_list|(
name|boolean
name|areNodeLabelsAcceptedByRM
parameter_list|)
function_decl|;
DECL|method|getResource ()
name|Resource
name|getResource
parameter_list|()
function_decl|;
DECL|method|setResource (Resource resource)
name|void
name|setResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
function_decl|;
DECL|method|getContainersToDecrease ()
name|List
argument_list|<
name|Container
argument_list|>
name|getContainersToDecrease
parameter_list|()
function_decl|;
DECL|method|addAllContainersToDecrease (Collection<Container> containersToDecrease)
name|void
name|addAllContainersToDecrease
parameter_list|(
name|Collection
argument_list|<
name|Container
argument_list|>
name|containersToDecrease
parameter_list|)
function_decl|;
DECL|method|getContainerQueuingLimit ()
name|ContainerQueuingLimit
name|getContainerQueuingLimit
parameter_list|()
function_decl|;
DECL|method|setContainerQueuingLimit (ContainerQueuingLimit containerQueuingLimit)
name|void
name|setContainerQueuingLimit
parameter_list|(
name|ContainerQueuingLimit
name|containerQueuingLimit
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

