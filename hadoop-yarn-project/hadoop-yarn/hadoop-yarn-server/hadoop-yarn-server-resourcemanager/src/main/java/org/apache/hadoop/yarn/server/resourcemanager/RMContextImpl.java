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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|ha
operator|.
name|HAServiceProtocol
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
name|LocalConfigurationProvider
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
name|conf
operator|.
name|YarnConfiguration
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
name|recovery
operator|.
name|NullRMStateStore
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

begin_class
DECL|class|RMContextImpl
specifier|public
class|class
name|RMContextImpl
implements|implements
name|RMContext
block|{
DECL|field|rmDispatcher
specifier|private
name|Dispatcher
name|rmDispatcher
decl_stmt|;
DECL|field|applications
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|applications
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|nodes
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|inactiveNodes
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|RMNode
argument_list|>
name|inactiveNodes
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|RMNode
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|isHAEnabled
specifier|private
name|boolean
name|isHAEnabled
decl_stmt|;
DECL|field|isWorkPreservingRecoveryEnabled
specifier|private
name|boolean
name|isWorkPreservingRecoveryEnabled
decl_stmt|;
DECL|field|haServiceState
specifier|private
name|HAServiceState
name|haServiceState
init|=
name|HAServiceProtocol
operator|.
name|HAServiceState
operator|.
name|INITIALIZING
decl_stmt|;
DECL|field|amLivelinessMonitor
specifier|private
name|AMLivelinessMonitor
name|amLivelinessMonitor
decl_stmt|;
DECL|field|amFinishingMonitor
specifier|private
name|AMLivelinessMonitor
name|amFinishingMonitor
decl_stmt|;
DECL|field|stateStore
specifier|private
name|RMStateStore
name|stateStore
init|=
literal|null
decl_stmt|;
DECL|field|containerAllocationExpirer
specifier|private
name|ContainerAllocationExpirer
name|containerAllocationExpirer
decl_stmt|;
DECL|field|delegationTokenRenewer
specifier|private
name|DelegationTokenRenewer
name|delegationTokenRenewer
decl_stmt|;
DECL|field|amRMTokenSecretManager
specifier|private
name|AMRMTokenSecretManager
name|amRMTokenSecretManager
decl_stmt|;
DECL|field|containerTokenSecretManager
specifier|private
name|RMContainerTokenSecretManager
name|containerTokenSecretManager
decl_stmt|;
DECL|field|nmTokenSecretManager
specifier|private
name|NMTokenSecretManagerInRM
name|nmTokenSecretManager
decl_stmt|;
DECL|field|clientToAMTokenSecretManager
specifier|private
name|ClientToAMTokenSecretManagerInRM
name|clientToAMTokenSecretManager
decl_stmt|;
DECL|field|adminService
specifier|private
name|AdminService
name|adminService
decl_stmt|;
DECL|field|clientRMService
specifier|private
name|ClientRMService
name|clientRMService
decl_stmt|;
DECL|field|rmDelegationTokenSecretManager
specifier|private
name|RMDelegationTokenSecretManager
name|rmDelegationTokenSecretManager
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ResourceScheduler
name|scheduler
decl_stmt|;
DECL|field|nodesListManager
specifier|private
name|NodesListManager
name|nodesListManager
decl_stmt|;
DECL|field|resourceTrackerService
specifier|private
name|ResourceTrackerService
name|resourceTrackerService
decl_stmt|;
DECL|field|applicationMasterService
specifier|private
name|ApplicationMasterService
name|applicationMasterService
decl_stmt|;
DECL|field|rmApplicationHistoryWriter
specifier|private
name|RMApplicationHistoryWriter
name|rmApplicationHistoryWriter
decl_stmt|;
DECL|field|configurationProvider
specifier|private
name|ConfigurationProvider
name|configurationProvider
decl_stmt|;
comment|/**    * Default constructor. To be used in conjunction with setter methods for    * individual fields.    */
DECL|method|RMContextImpl ()
specifier|public
name|RMContextImpl
parameter_list|()
block|{    }
annotation|@
name|VisibleForTesting
comment|// helper constructor for tests
DECL|method|RMContextImpl (Dispatcher rmDispatcher, ContainerAllocationExpirer containerAllocationExpirer, AMLivelinessMonitor amLivelinessMonitor, AMLivelinessMonitor amFinishingMonitor, DelegationTokenRenewer delegationTokenRenewer, AMRMTokenSecretManager appTokenSecretManager, RMContainerTokenSecretManager containerTokenSecretManager, NMTokenSecretManagerInRM nmTokenSecretManager, ClientToAMTokenSecretManagerInRM clientToAMTokenSecretManager, RMApplicationHistoryWriter rmApplicationHistoryWriter)
specifier|public
name|RMContextImpl
parameter_list|(
name|Dispatcher
name|rmDispatcher
parameter_list|,
name|ContainerAllocationExpirer
name|containerAllocationExpirer
parameter_list|,
name|AMLivelinessMonitor
name|amLivelinessMonitor
parameter_list|,
name|AMLivelinessMonitor
name|amFinishingMonitor
parameter_list|,
name|DelegationTokenRenewer
name|delegationTokenRenewer
parameter_list|,
name|AMRMTokenSecretManager
name|appTokenSecretManager
parameter_list|,
name|RMContainerTokenSecretManager
name|containerTokenSecretManager
parameter_list|,
name|NMTokenSecretManagerInRM
name|nmTokenSecretManager
parameter_list|,
name|ClientToAMTokenSecretManagerInRM
name|clientToAMTokenSecretManager
parameter_list|,
name|RMApplicationHistoryWriter
name|rmApplicationHistoryWriter
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|setDispatcher
argument_list|(
name|rmDispatcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|setContainerAllocationExpirer
argument_list|(
name|containerAllocationExpirer
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAMLivelinessMonitor
argument_list|(
name|amLivelinessMonitor
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAMFinishingMonitor
argument_list|(
name|amFinishingMonitor
argument_list|)
expr_stmt|;
name|this
operator|.
name|setDelegationTokenRenewer
argument_list|(
name|delegationTokenRenewer
argument_list|)
expr_stmt|;
name|this
operator|.
name|setAMRMTokenSecretManager
argument_list|(
name|appTokenSecretManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|setContainerTokenSecretManager
argument_list|(
name|containerTokenSecretManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNMTokenSecretManager
argument_list|(
name|nmTokenSecretManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|setClientToAMTokenSecretManager
argument_list|(
name|clientToAMTokenSecretManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|setRMApplicationHistoryWriter
argument_list|(
name|rmApplicationHistoryWriter
argument_list|)
expr_stmt|;
name|RMStateStore
name|nullStore
init|=
operator|new
name|NullRMStateStore
argument_list|()
decl_stmt|;
name|nullStore
operator|.
name|setRMDispatcher
argument_list|(
name|rmDispatcher
argument_list|)
expr_stmt|;
try|try
block|{
name|nullStore
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|setStateStore
argument_list|(
name|nullStore
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
assert|assert
literal|false
assert|;
block|}
name|ConfigurationProvider
name|provider
init|=
operator|new
name|LocalConfigurationProvider
argument_list|()
decl_stmt|;
name|setConfigurationProvider
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDispatcher ()
specifier|public
name|Dispatcher
name|getDispatcher
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmDispatcher
return|;
block|}
annotation|@
name|Override
DECL|method|getStateStore ()
specifier|public
name|RMStateStore
name|getStateStore
parameter_list|()
block|{
return|return
name|stateStore
return|;
block|}
annotation|@
name|Override
DECL|method|getRMApps ()
specifier|public
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|getRMApps
parameter_list|()
block|{
return|return
name|this
operator|.
name|applications
return|;
block|}
annotation|@
name|Override
DECL|method|getRMNodes ()
specifier|public
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|getRMNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodes
return|;
block|}
annotation|@
name|Override
DECL|method|getInactiveRMNodes ()
specifier|public
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|RMNode
argument_list|>
name|getInactiveRMNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|inactiveNodes
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerAllocationExpirer ()
specifier|public
name|ContainerAllocationExpirer
name|getContainerAllocationExpirer
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerAllocationExpirer
return|;
block|}
annotation|@
name|Override
DECL|method|getAMLivelinessMonitor ()
specifier|public
name|AMLivelinessMonitor
name|getAMLivelinessMonitor
parameter_list|()
block|{
return|return
name|this
operator|.
name|amLivelinessMonitor
return|;
block|}
annotation|@
name|Override
DECL|method|getAMFinishingMonitor ()
specifier|public
name|AMLivelinessMonitor
name|getAMFinishingMonitor
parameter_list|()
block|{
return|return
name|this
operator|.
name|amFinishingMonitor
return|;
block|}
annotation|@
name|Override
DECL|method|getDelegationTokenRenewer ()
specifier|public
name|DelegationTokenRenewer
name|getDelegationTokenRenewer
parameter_list|()
block|{
return|return
name|delegationTokenRenewer
return|;
block|}
annotation|@
name|Override
DECL|method|getAMRMTokenSecretManager ()
specifier|public
name|AMRMTokenSecretManager
name|getAMRMTokenSecretManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|amRMTokenSecretManager
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerTokenSecretManager ()
specifier|public
name|RMContainerTokenSecretManager
name|getContainerTokenSecretManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerTokenSecretManager
return|;
block|}
annotation|@
name|Override
DECL|method|getNMTokenSecretManager ()
specifier|public
name|NMTokenSecretManagerInRM
name|getNMTokenSecretManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|nmTokenSecretManager
return|;
block|}
annotation|@
name|Override
DECL|method|getScheduler ()
specifier|public
name|ResourceScheduler
name|getScheduler
parameter_list|()
block|{
return|return
name|this
operator|.
name|scheduler
return|;
block|}
annotation|@
name|Override
DECL|method|getNodesListManager ()
specifier|public
name|NodesListManager
name|getNodesListManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodesListManager
return|;
block|}
annotation|@
name|Override
DECL|method|getClientToAMTokenSecretManager ()
specifier|public
name|ClientToAMTokenSecretManagerInRM
name|getClientToAMTokenSecretManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientToAMTokenSecretManager
return|;
block|}
annotation|@
name|Override
DECL|method|getRMAdminService ()
specifier|public
name|AdminService
name|getRMAdminService
parameter_list|()
block|{
return|return
name|this
operator|.
name|adminService
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setStateStore (RMStateStore store)
specifier|public
name|void
name|setStateStore
parameter_list|(
name|RMStateStore
name|store
parameter_list|)
block|{
name|stateStore
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getClientRMService ()
specifier|public
name|ClientRMService
name|getClientRMService
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientRMService
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationMasterService ()
specifier|public
name|ApplicationMasterService
name|getApplicationMasterService
parameter_list|()
block|{
return|return
name|applicationMasterService
return|;
block|}
annotation|@
name|Override
DECL|method|getResourceTrackerService ()
specifier|public
name|ResourceTrackerService
name|getResourceTrackerService
parameter_list|()
block|{
return|return
name|resourceTrackerService
return|;
block|}
DECL|method|setHAEnabled (boolean isHAEnabled)
name|void
name|setHAEnabled
parameter_list|(
name|boolean
name|isHAEnabled
parameter_list|)
block|{
name|this
operator|.
name|isHAEnabled
operator|=
name|isHAEnabled
expr_stmt|;
block|}
DECL|method|setHAServiceState (HAServiceState haServiceState)
name|void
name|setHAServiceState
parameter_list|(
name|HAServiceState
name|haServiceState
parameter_list|)
block|{
synchronized|synchronized
init|(
name|haServiceState
init|)
block|{
name|this
operator|.
name|haServiceState
operator|=
name|haServiceState
expr_stmt|;
block|}
block|}
DECL|method|setDispatcher (Dispatcher dispatcher)
name|void
name|setDispatcher
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|)
block|{
name|this
operator|.
name|rmDispatcher
operator|=
name|dispatcher
expr_stmt|;
block|}
DECL|method|setRMAdminService (AdminService adminService)
name|void
name|setRMAdminService
parameter_list|(
name|AdminService
name|adminService
parameter_list|)
block|{
name|this
operator|.
name|adminService
operator|=
name|adminService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setClientRMService (ClientRMService clientRMService)
specifier|public
name|void
name|setClientRMService
parameter_list|(
name|ClientRMService
name|clientRMService
parameter_list|)
block|{
name|this
operator|.
name|clientRMService
operator|=
name|clientRMService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRMDelegationTokenSecretManager ()
specifier|public
name|RMDelegationTokenSecretManager
name|getRMDelegationTokenSecretManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmDelegationTokenSecretManager
return|;
block|}
annotation|@
name|Override
DECL|method|setRMDelegationTokenSecretManager ( RMDelegationTokenSecretManager delegationTokenSecretManager)
specifier|public
name|void
name|setRMDelegationTokenSecretManager
parameter_list|(
name|RMDelegationTokenSecretManager
name|delegationTokenSecretManager
parameter_list|)
block|{
name|this
operator|.
name|rmDelegationTokenSecretManager
operator|=
name|delegationTokenSecretManager
expr_stmt|;
block|}
DECL|method|setContainerAllocationExpirer ( ContainerAllocationExpirer containerAllocationExpirer)
name|void
name|setContainerAllocationExpirer
parameter_list|(
name|ContainerAllocationExpirer
name|containerAllocationExpirer
parameter_list|)
block|{
name|this
operator|.
name|containerAllocationExpirer
operator|=
name|containerAllocationExpirer
expr_stmt|;
block|}
DECL|method|setAMLivelinessMonitor (AMLivelinessMonitor amLivelinessMonitor)
name|void
name|setAMLivelinessMonitor
parameter_list|(
name|AMLivelinessMonitor
name|amLivelinessMonitor
parameter_list|)
block|{
name|this
operator|.
name|amLivelinessMonitor
operator|=
name|amLivelinessMonitor
expr_stmt|;
block|}
DECL|method|setAMFinishingMonitor (AMLivelinessMonitor amFinishingMonitor)
name|void
name|setAMFinishingMonitor
parameter_list|(
name|AMLivelinessMonitor
name|amFinishingMonitor
parameter_list|)
block|{
name|this
operator|.
name|amFinishingMonitor
operator|=
name|amFinishingMonitor
expr_stmt|;
block|}
DECL|method|setContainerTokenSecretManager ( RMContainerTokenSecretManager containerTokenSecretManager)
name|void
name|setContainerTokenSecretManager
parameter_list|(
name|RMContainerTokenSecretManager
name|containerTokenSecretManager
parameter_list|)
block|{
name|this
operator|.
name|containerTokenSecretManager
operator|=
name|containerTokenSecretManager
expr_stmt|;
block|}
DECL|method|setNMTokenSecretManager ( NMTokenSecretManagerInRM nmTokenSecretManager)
name|void
name|setNMTokenSecretManager
parameter_list|(
name|NMTokenSecretManagerInRM
name|nmTokenSecretManager
parameter_list|)
block|{
name|this
operator|.
name|nmTokenSecretManager
operator|=
name|nmTokenSecretManager
expr_stmt|;
block|}
DECL|method|setScheduler (ResourceScheduler scheduler)
name|void
name|setScheduler
parameter_list|(
name|ResourceScheduler
name|scheduler
parameter_list|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
block|}
DECL|method|setDelegationTokenRenewer ( DelegationTokenRenewer delegationTokenRenewer)
name|void
name|setDelegationTokenRenewer
parameter_list|(
name|DelegationTokenRenewer
name|delegationTokenRenewer
parameter_list|)
block|{
name|this
operator|.
name|delegationTokenRenewer
operator|=
name|delegationTokenRenewer
expr_stmt|;
block|}
DECL|method|setClientToAMTokenSecretManager ( ClientToAMTokenSecretManagerInRM clientToAMTokenSecretManager)
name|void
name|setClientToAMTokenSecretManager
parameter_list|(
name|ClientToAMTokenSecretManagerInRM
name|clientToAMTokenSecretManager
parameter_list|)
block|{
name|this
operator|.
name|clientToAMTokenSecretManager
operator|=
name|clientToAMTokenSecretManager
expr_stmt|;
block|}
DECL|method|setAMRMTokenSecretManager ( AMRMTokenSecretManager amRMTokenSecretManager)
name|void
name|setAMRMTokenSecretManager
parameter_list|(
name|AMRMTokenSecretManager
name|amRMTokenSecretManager
parameter_list|)
block|{
name|this
operator|.
name|amRMTokenSecretManager
operator|=
name|amRMTokenSecretManager
expr_stmt|;
block|}
DECL|method|setNodesListManager (NodesListManager nodesListManager)
name|void
name|setNodesListManager
parameter_list|(
name|NodesListManager
name|nodesListManager
parameter_list|)
block|{
name|this
operator|.
name|nodesListManager
operator|=
name|nodesListManager
expr_stmt|;
block|}
DECL|method|setApplicationMasterService ( ApplicationMasterService applicationMasterService)
name|void
name|setApplicationMasterService
parameter_list|(
name|ApplicationMasterService
name|applicationMasterService
parameter_list|)
block|{
name|this
operator|.
name|applicationMasterService
operator|=
name|applicationMasterService
expr_stmt|;
block|}
DECL|method|setResourceTrackerService ( ResourceTrackerService resourceTrackerService)
name|void
name|setResourceTrackerService
parameter_list|(
name|ResourceTrackerService
name|resourceTrackerService
parameter_list|)
block|{
name|this
operator|.
name|resourceTrackerService
operator|=
name|resourceTrackerService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isHAEnabled ()
specifier|public
name|boolean
name|isHAEnabled
parameter_list|()
block|{
return|return
name|isHAEnabled
return|;
block|}
annotation|@
name|Override
DECL|method|getHAServiceState ()
specifier|public
name|HAServiceState
name|getHAServiceState
parameter_list|()
block|{
synchronized|synchronized
init|(
name|haServiceState
init|)
block|{
return|return
name|haServiceState
return|;
block|}
block|}
DECL|method|setWorkPreservingRecoveryEnabled (boolean enabled)
specifier|public
name|void
name|setWorkPreservingRecoveryEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|isWorkPreservingRecoveryEnabled
operator|=
name|enabled
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isWorkPreservingRecoveryEnabled ()
specifier|public
name|boolean
name|isWorkPreservingRecoveryEnabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|isWorkPreservingRecoveryEnabled
return|;
block|}
annotation|@
name|Override
DECL|method|getRMApplicationHistoryWriter ()
specifier|public
name|RMApplicationHistoryWriter
name|getRMApplicationHistoryWriter
parameter_list|()
block|{
return|return
name|rmApplicationHistoryWriter
return|;
block|}
annotation|@
name|Override
DECL|method|setRMApplicationHistoryWriter ( RMApplicationHistoryWriter rmApplicationHistoryWriter)
specifier|public
name|void
name|setRMApplicationHistoryWriter
parameter_list|(
name|RMApplicationHistoryWriter
name|rmApplicationHistoryWriter
parameter_list|)
block|{
name|this
operator|.
name|rmApplicationHistoryWriter
operator|=
name|rmApplicationHistoryWriter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConfigurationProvider ()
specifier|public
name|ConfigurationProvider
name|getConfigurationProvider
parameter_list|()
block|{
return|return
name|this
operator|.
name|configurationProvider
return|;
block|}
DECL|method|setConfigurationProvider ( ConfigurationProvider configurationProvider)
specifier|public
name|void
name|setConfigurationProvider
parameter_list|(
name|ConfigurationProvider
name|configurationProvider
parameter_list|)
block|{
name|this
operator|.
name|configurationProvider
operator|=
name|configurationProvider
expr_stmt|;
block|}
block|}
end_class

end_unit

