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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|timelineservice
operator|.
name|RMTimelineCollectorManager
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
name|util
operator|.
name|Clock
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
name|util
operator|.
name|SystemClock
import|;
end_import

begin_comment
comment|/**  * The RMActiveServiceContext is the class that maintains all the  * RMActiveService contexts.This is expected to be used only by ResourceManager  * and RMContext.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|RMActiveServiceContext
specifier|public
class|class
name|RMActiveServiceContext
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RMActiveServiceContext
operator|.
name|class
argument_list|)
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
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|inactiveNodes
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
DECL|field|systemCredentials
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|systemCredentials
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|isWorkPreservingRecoveryEnabled
specifier|private
name|boolean
name|isWorkPreservingRecoveryEnabled
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
DECL|field|reservationSystem
specifier|private
name|ReservationSystem
name|reservationSystem
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
DECL|field|systemMetricsPublisher
specifier|private
name|SystemMetricsPublisher
name|systemMetricsPublisher
decl_stmt|;
DECL|field|timelineCollectorManager
specifier|private
name|RMTimelineCollectorManager
name|timelineCollectorManager
decl_stmt|;
DECL|field|nodeLabelManager
specifier|private
name|RMNodeLabelsManager
name|nodeLabelManager
decl_stmt|;
DECL|field|rmDelegatedNodeLabelsUpdater
specifier|private
name|RMDelegatedNodeLabelsUpdater
name|rmDelegatedNodeLabelsUpdater
decl_stmt|;
DECL|field|epoch
specifier|private
name|long
name|epoch
decl_stmt|;
DECL|field|systemClock
specifier|private
name|Clock
name|systemClock
init|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|field|schedulerRecoveryStartTime
specifier|private
name|long
name|schedulerRecoveryStartTime
init|=
literal|0
decl_stmt|;
DECL|field|schedulerRecoveryWaitTime
specifier|private
name|long
name|schedulerRecoveryWaitTime
init|=
literal|0
decl_stmt|;
DECL|field|printLog
specifier|private
name|boolean
name|printLog
init|=
literal|true
decl_stmt|;
DECL|field|isSchedulerReady
specifier|private
name|boolean
name|isSchedulerReady
init|=
literal|false
decl_stmt|;
DECL|field|queuePlacementManager
specifier|private
name|PlacementManager
name|queuePlacementManager
init|=
literal|null
decl_stmt|;
DECL|method|RMActiveServiceContext ()
specifier|public
name|RMActiveServiceContext
parameter_list|()
block|{
name|queuePlacementManager
operator|=
operator|new
name|PlacementManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|RMActiveServiceContext (Dispatcher rmDispatcher, ContainerAllocationExpirer containerAllocationExpirer, AMLivelinessMonitor amLivelinessMonitor, AMLivelinessMonitor amFinishingMonitor, DelegationTokenRenewer delegationTokenRenewer, AMRMTokenSecretManager appTokenSecretManager, RMContainerTokenSecretManager containerTokenSecretManager, NMTokenSecretManagerInRM nmTokenSecretManager, ClientToAMTokenSecretManagerInRM clientToAMTokenSecretManager, ResourceScheduler scheduler)
specifier|public
name|RMActiveServiceContext
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
name|ResourceScheduler
name|scheduler
parameter_list|)
block|{
name|this
argument_list|()
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
name|setScheduler
argument_list|(
name|scheduler
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
block|}
annotation|@
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
DECL|method|getClientRMService ()
specifier|public
name|ClientRMService
name|getClientRMService
parameter_list|()
block|{
return|return
name|clientRMService
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
DECL|method|getInactiveRMNodes ()
specifier|public
name|ConcurrentMap
argument_list|<
name|NodeId
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
DECL|method|getReservationSystem ()
specifier|public
name|ReservationSystem
name|getReservationSystem
parameter_list|()
block|{
return|return
name|this
operator|.
name|reservationSystem
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNMTokenSecretManager (NMTokenSecretManagerInRM nmTokenSecretManager)
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
annotation|@
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setReservationSystem (ReservationSystem reservationSystem)
name|void
name|setReservationSystem
parameter_list|(
name|ReservationSystem
name|reservationSystem
parameter_list|)
block|{
name|this
operator|.
name|reservationSystem
operator|=
name|reservationSystem
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setDelegationTokenRenewer (DelegationTokenRenewer delegationTokenRenewer)
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
annotation|@
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAMRMTokenSecretManager (AMRMTokenSecretManager amRMTokenSecretManager)
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
annotation|@
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResourceTrackerService (ResourceTrackerService resourceTrackerService)
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
DECL|method|getRMTimelineCollectorManager ()
specifier|public
name|RMTimelineCollectorManager
name|getRMTimelineCollectorManager
parameter_list|()
block|{
return|return
name|timelineCollectorManager
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRMTimelineCollectorManager ( RMTimelineCollectorManager collectorManager)
specifier|public
name|void
name|setRMTimelineCollectorManager
parameter_list|(
name|RMTimelineCollectorManager
name|collectorManager
parameter_list|)
block|{
name|this
operator|.
name|timelineCollectorManager
operator|=
name|collectorManager
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setSystemMetricsPublisher ( SystemMetricsPublisher metricsPublisher)
specifier|public
name|void
name|setSystemMetricsPublisher
parameter_list|(
name|SystemMetricsPublisher
name|metricsPublisher
parameter_list|)
block|{
name|this
operator|.
name|systemMetricsPublisher
operator|=
name|metricsPublisher
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getSystemMetricsPublisher ()
specifier|public
name|SystemMetricsPublisher
name|getSystemMetricsPublisher
parameter_list|()
block|{
return|return
name|systemMetricsPublisher
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
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
name|Private
annotation|@
name|Unstable
DECL|method|getEpoch ()
specifier|public
name|long
name|getEpoch
parameter_list|()
block|{
return|return
name|this
operator|.
name|epoch
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setEpoch (long epoch)
name|void
name|setEpoch
parameter_list|(
name|long
name|epoch
parameter_list|)
block|{
name|this
operator|.
name|epoch
operator|=
name|epoch
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getNodeLabelManager ()
specifier|public
name|RMNodeLabelsManager
name|getNodeLabelManager
parameter_list|()
block|{
return|return
name|nodeLabelManager
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNodeLabelManager (RMNodeLabelsManager mgr)
specifier|public
name|void
name|setNodeLabelManager
parameter_list|(
name|RMNodeLabelsManager
name|mgr
parameter_list|)
block|{
name|nodeLabelManager
operator|=
name|mgr
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getRMDelegatedNodeLabelsUpdater ()
specifier|public
name|RMDelegatedNodeLabelsUpdater
name|getRMDelegatedNodeLabelsUpdater
parameter_list|()
block|{
return|return
name|rmDelegatedNodeLabelsUpdater
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRMDelegatedNodeLabelsUpdater ( RMDelegatedNodeLabelsUpdater nodeLablesUpdater)
specifier|public
name|void
name|setRMDelegatedNodeLabelsUpdater
parameter_list|(
name|RMDelegatedNodeLabelsUpdater
name|nodeLablesUpdater
parameter_list|)
block|{
name|rmDelegatedNodeLabelsUpdater
operator|=
name|nodeLablesUpdater
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setSchedulerRecoveryStartAndWaitTime (long waitTime)
specifier|public
name|void
name|setSchedulerRecoveryStartAndWaitTime
parameter_list|(
name|long
name|waitTime
parameter_list|)
block|{
name|this
operator|.
name|schedulerRecoveryStartTime
operator|=
name|systemClock
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|schedulerRecoveryWaitTime
operator|=
name|waitTime
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|isSchedulerReadyForAllocatingContainers ()
specifier|public
name|boolean
name|isSchedulerReadyForAllocatingContainers
parameter_list|()
block|{
if|if
condition|(
name|isSchedulerReady
condition|)
block|{
return|return
name|isSchedulerReady
return|;
block|}
name|isSchedulerReady
operator|=
operator|(
name|systemClock
operator|.
name|getTime
argument_list|()
operator|-
name|schedulerRecoveryStartTime
operator|)
operator|>
name|schedulerRecoveryWaitTime
expr_stmt|;
if|if
condition|(
operator|!
name|isSchedulerReady
operator|&&
name|printLog
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Skip allocating containers. Scheduler is waiting for recovery."
argument_list|)
expr_stmt|;
name|printLog
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|isSchedulerReady
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduler recovery is done. Start allocating new containers."
argument_list|)
expr_stmt|;
block|}
return|return
name|isSchedulerReady
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setSystemClock (Clock clock)
specifier|public
name|void
name|setSystemClock
parameter_list|(
name|Clock
name|clock
parameter_list|)
block|{
name|this
operator|.
name|systemClock
operator|=
name|clock
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getSystemCredentialsForApps ()
specifier|public
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|getSystemCredentialsForApps
parameter_list|()
block|{
return|return
name|systemCredentials
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getQueuePlacementManager ()
specifier|public
name|PlacementManager
name|getQueuePlacementManager
parameter_list|()
block|{
return|return
name|queuePlacementManager
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueuePlacementManager (PlacementManager placementMgr)
specifier|public
name|void
name|setQueuePlacementManager
parameter_list|(
name|PlacementManager
name|placementMgr
parameter_list|)
block|{
name|this
operator|.
name|queuePlacementManager
operator|=
name|placementMgr
expr_stmt|;
block|}
block|}
end_class

end_unit

