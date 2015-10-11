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
name|concurrent
operator|.
name|ConcurrentMap
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
name|conf
operator|.
name|Configuration
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|NodeId
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
name|conf
operator|.
name|ConfigurationProvider
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
name|event
operator|.
name|Dispatcher
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
name|ahs
operator|.
name|RMApplicationHistoryWriter
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
name|metrics
operator|.
name|SystemMetricsPublisher
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|nodelabels
operator|.
name|RMDelegatedNodeLabelsUpdater
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
name|placement
operator|.
name|PlacementManager
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
name|recovery
operator|.
name|RMStateStore
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
name|reservation
operator|.
name|ReservationSystem
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
name|rmapp
operator|.
name|RMApp
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
name|rmapp
operator|.
name|attempt
operator|.
name|AMLivelinessMonitor
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
name|rmcontainer
operator|.
name|ContainerAllocationExpirer
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
name|scheduler
operator|.
name|ResourceScheduler
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
name|security
operator|.
name|AMRMTokenSecretManager
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
name|security
operator|.
name|ClientToAMTokenSecretManagerInRM
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
name|security
operator|.
name|DelegationTokenRenewer
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
name|security
operator|.
name|NMTokenSecretManagerInRM
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
name|security
operator|.
name|RMContainerTokenSecretManager
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
name|security
operator|.
name|RMDelegationTokenSecretManager
import|;
end_import

begin_comment
comment|/**  * Context of the ResourceManager.  */
end_comment

begin_interface
DECL|interface|RMContext
specifier|public
interface|interface
name|RMContext
block|{
DECL|method|getDispatcher ()
name|Dispatcher
name|getDispatcher
parameter_list|()
function_decl|;
DECL|method|isHAEnabled ()
name|boolean
name|isHAEnabled
parameter_list|()
function_decl|;
DECL|method|getHAServiceState ()
name|HAServiceState
name|getHAServiceState
parameter_list|()
function_decl|;
DECL|method|getStateStore ()
name|RMStateStore
name|getStateStore
parameter_list|()
function_decl|;
DECL|method|getRMApps ()
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|getRMApps
parameter_list|()
function_decl|;
DECL|method|getSystemCredentialsForApps ()
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|getSystemCredentialsForApps
parameter_list|()
function_decl|;
DECL|method|getInactiveRMNodes ()
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|getInactiveRMNodes
parameter_list|()
function_decl|;
DECL|method|getRMNodes ()
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|getRMNodes
parameter_list|()
function_decl|;
DECL|method|getAMLivelinessMonitor ()
name|AMLivelinessMonitor
name|getAMLivelinessMonitor
parameter_list|()
function_decl|;
DECL|method|getAMFinishingMonitor ()
name|AMLivelinessMonitor
name|getAMFinishingMonitor
parameter_list|()
function_decl|;
DECL|method|getContainerAllocationExpirer ()
name|ContainerAllocationExpirer
name|getContainerAllocationExpirer
parameter_list|()
function_decl|;
DECL|method|getDelegationTokenRenewer ()
name|DelegationTokenRenewer
name|getDelegationTokenRenewer
parameter_list|()
function_decl|;
DECL|method|getAMRMTokenSecretManager ()
name|AMRMTokenSecretManager
name|getAMRMTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|getContainerTokenSecretManager ()
name|RMContainerTokenSecretManager
name|getContainerTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|getNMTokenSecretManager ()
name|NMTokenSecretManagerInRM
name|getNMTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|getScheduler ()
name|ResourceScheduler
name|getScheduler
parameter_list|()
function_decl|;
DECL|method|getNodesListManager ()
name|NodesListManager
name|getNodesListManager
parameter_list|()
function_decl|;
DECL|method|getClientToAMTokenSecretManager ()
name|ClientToAMTokenSecretManagerInRM
name|getClientToAMTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|getRMAdminService ()
name|AdminService
name|getRMAdminService
parameter_list|()
function_decl|;
DECL|method|getClientRMService ()
name|ClientRMService
name|getClientRMService
parameter_list|()
function_decl|;
DECL|method|getApplicationMasterService ()
name|ApplicationMasterService
name|getApplicationMasterService
parameter_list|()
function_decl|;
DECL|method|getResourceTrackerService ()
name|ResourceTrackerService
name|getResourceTrackerService
parameter_list|()
function_decl|;
DECL|method|setClientRMService (ClientRMService clientRMService)
name|void
name|setClientRMService
parameter_list|(
name|ClientRMService
name|clientRMService
parameter_list|)
function_decl|;
DECL|method|getRMDelegationTokenSecretManager ()
name|RMDelegationTokenSecretManager
name|getRMDelegationTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|setRMDelegationTokenSecretManager ( RMDelegationTokenSecretManager delegationTokenSecretManager)
name|void
name|setRMDelegationTokenSecretManager
parameter_list|(
name|RMDelegationTokenSecretManager
name|delegationTokenSecretManager
parameter_list|)
function_decl|;
DECL|method|getRMApplicationHistoryWriter ()
name|RMApplicationHistoryWriter
name|getRMApplicationHistoryWriter
parameter_list|()
function_decl|;
DECL|method|setRMApplicationHistoryWriter ( RMApplicationHistoryWriter rmApplicationHistoryWriter)
name|void
name|setRMApplicationHistoryWriter
parameter_list|(
name|RMApplicationHistoryWriter
name|rmApplicationHistoryWriter
parameter_list|)
function_decl|;
DECL|method|setSystemMetricsPublisher (SystemMetricsPublisher systemMetricsPublisher)
name|void
name|setSystemMetricsPublisher
parameter_list|(
name|SystemMetricsPublisher
name|systemMetricsPublisher
parameter_list|)
function_decl|;
DECL|method|getSystemMetricsPublisher ()
name|SystemMetricsPublisher
name|getSystemMetricsPublisher
parameter_list|()
function_decl|;
DECL|method|getConfigurationProvider ()
name|ConfigurationProvider
name|getConfigurationProvider
parameter_list|()
function_decl|;
DECL|method|isWorkPreservingRecoveryEnabled ()
name|boolean
name|isWorkPreservingRecoveryEnabled
parameter_list|()
function_decl|;
DECL|method|getNodeLabelManager ()
name|RMNodeLabelsManager
name|getNodeLabelManager
parameter_list|()
function_decl|;
DECL|method|setNodeLabelManager (RMNodeLabelsManager mgr)
specifier|public
name|void
name|setNodeLabelManager
parameter_list|(
name|RMNodeLabelsManager
name|mgr
parameter_list|)
function_decl|;
DECL|method|getRMDelegatedNodeLabelsUpdater ()
name|RMDelegatedNodeLabelsUpdater
name|getRMDelegatedNodeLabelsUpdater
parameter_list|()
function_decl|;
DECL|method|setRMDelegatedNodeLabelsUpdater ( RMDelegatedNodeLabelsUpdater nodeLabelsUpdater)
name|void
name|setRMDelegatedNodeLabelsUpdater
parameter_list|(
name|RMDelegatedNodeLabelsUpdater
name|nodeLabelsUpdater
parameter_list|)
function_decl|;
DECL|method|getEpoch ()
name|long
name|getEpoch
parameter_list|()
function_decl|;
DECL|method|getReservationSystem ()
name|ReservationSystem
name|getReservationSystem
parameter_list|()
function_decl|;
DECL|method|isSchedulerReadyForAllocatingContainers ()
name|boolean
name|isSchedulerReadyForAllocatingContainers
parameter_list|()
function_decl|;
DECL|method|getYarnConfiguration ()
name|Configuration
name|getYarnConfiguration
parameter_list|()
function_decl|;
DECL|method|getQueuePlacementManager ()
name|PlacementManager
name|getQueuePlacementManager
parameter_list|()
function_decl|;
DECL|method|setQueuePlacementManager (PlacementManager placementMgr)
name|void
name|setQueuePlacementManager
parameter_list|(
name|PlacementManager
name|placementMgr
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

