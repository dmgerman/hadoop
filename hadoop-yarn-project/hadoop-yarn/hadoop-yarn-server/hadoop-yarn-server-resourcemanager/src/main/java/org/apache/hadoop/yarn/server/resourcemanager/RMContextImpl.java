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
name|rmapp
operator|.
name|monitor
operator|.
name|RMAppLifetimeMonitor
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
name|scheduler
operator|.
name|distributed
operator|.
name|QueueLimitCalculator
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

begin_comment
comment|/**  * RMContextImpl class holds two services context.  *<ul>  *<li>serviceContext : These services called as<b>Always On</b> services.  * Services that need to run always irrespective of the HA state of the RM.</li>  *<li>activeServiceCotext : Active services context. Services that need to run  * only on the Active RM.</li>  *</ul>  *<p>  *<b>Note:</b> If any new service to be added to context, add it to a right  * context as per above description.  */
end_comment

begin_class
DECL|class|RMContextImpl
specifier|public
class|class
name|RMContextImpl
implements|implements
name|RMContext
block|{
comment|/**    * RM service contexts which runs through out RM life span. These are created    * once during start of RM.    */
DECL|field|serviceContext
specifier|private
name|RMServiceContext
name|serviceContext
decl_stmt|;
comment|/**    * RM Active service context. This will be recreated for every transition from    * ACTIVE->STANDBY.    */
DECL|field|activeServiceContext
specifier|private
name|RMActiveServiceContext
name|activeServiceContext
decl_stmt|;
comment|/**    * Default constructor. To be used in conjunction with setter methods for    * individual fields.    */
DECL|method|RMContextImpl ()
specifier|public
name|RMContextImpl
parameter_list|()
block|{
name|this
operator|.
name|serviceContext
operator|=
operator|new
name|RMServiceContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|activeServiceContext
operator|=
operator|new
name|RMActiveServiceContext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
comment|// helper constructor for tests
DECL|method|RMContextImpl (Dispatcher rmDispatcher, ContainerAllocationExpirer containerAllocationExpirer, AMLivelinessMonitor amLivelinessMonitor, AMLivelinessMonitor amFinishingMonitor, DelegationTokenRenewer delegationTokenRenewer, AMRMTokenSecretManager appTokenSecretManager, RMContainerTokenSecretManager containerTokenSecretManager, NMTokenSecretManagerInRM nmTokenSecretManager, ClientToAMTokenSecretManagerInRM clientToAMTokenSecretManager, ResourceScheduler scheduler)
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
name|ResourceScheduler
name|scheduler
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
name|setActiveServiceContext
argument_list|(
operator|new
name|RMActiveServiceContext
argument_list|(
name|rmDispatcher
argument_list|,
name|containerAllocationExpirer
argument_list|,
name|amLivelinessMonitor
argument_list|,
name|amFinishingMonitor
argument_list|,
name|delegationTokenRenewer
argument_list|,
name|appTokenSecretManager
argument_list|,
name|containerTokenSecretManager
argument_list|,
name|nmTokenSecretManager
argument_list|,
name|clientToAMTokenSecretManager
argument_list|,
name|scheduler
argument_list|)
argument_list|)
expr_stmt|;
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
name|VisibleForTesting
comment|// helper constructor for tests
DECL|method|RMContextImpl (Dispatcher rmDispatcher, ContainerAllocationExpirer containerAllocationExpirer, AMLivelinessMonitor amLivelinessMonitor, AMLivelinessMonitor amFinishingMonitor, DelegationTokenRenewer delegationTokenRenewer, AMRMTokenSecretManager appTokenSecretManager, RMContainerTokenSecretManager containerTokenSecretManager, NMTokenSecretManagerInRM nmTokenSecretManager, ClientToAMTokenSecretManagerInRM clientToAMTokenSecretManager)
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
parameter_list|)
block|{
name|this
argument_list|(
name|rmDispatcher
argument_list|,
name|containerAllocationExpirer
argument_list|,
name|amLivelinessMonitor
argument_list|,
name|amFinishingMonitor
argument_list|,
name|delegationTokenRenewer
argument_list|,
name|appTokenSecretManager
argument_list|,
name|containerTokenSecretManager
argument_list|,
name|nmTokenSecretManager
argument_list|,
name|clientToAMTokenSecretManager
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * RM service contexts which runs through out JVM life span. These are created    * once during start of RM.    * @return serviceContext of RM    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getServiceContext ()
specifier|public
name|RMServiceContext
name|getServiceContext
parameter_list|()
block|{
return|return
name|serviceContext
return|;
block|}
comment|/**    *<b>Note:</b> setting service context clears all services embedded with it.    * @param context rm service context    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setServiceContext (RMServiceContext context)
specifier|public
name|void
name|setServiceContext
parameter_list|(
name|RMServiceContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|serviceContext
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResourceManager ()
specifier|public
name|ResourceManager
name|getResourceManager
parameter_list|()
block|{
return|return
name|serviceContext
operator|.
name|getResourceManager
argument_list|()
return|;
block|}
DECL|method|setResourceManager (ResourceManager rm)
specifier|public
name|void
name|setResourceManager
parameter_list|(
name|ResourceManager
name|rm
parameter_list|)
block|{
name|serviceContext
operator|.
name|setResourceManager
argument_list|(
name|rm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeaderElectorService ()
specifier|public
name|EmbeddedElector
name|getLeaderElectorService
parameter_list|()
block|{
return|return
name|serviceContext
operator|.
name|getLeaderElectorService
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setLeaderElectorService (EmbeddedElector elector)
specifier|public
name|void
name|setLeaderElectorService
parameter_list|(
name|EmbeddedElector
name|elector
parameter_list|)
block|{
name|serviceContext
operator|.
name|setLeaderElectorService
argument_list|(
name|elector
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
name|serviceContext
operator|.
name|getDispatcher
argument_list|()
return|;
block|}
DECL|method|setDispatcher (Dispatcher dispatcher)
name|void
name|setDispatcher
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|)
block|{
name|serviceContext
operator|.
name|setDispatcher
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
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
name|serviceContext
operator|.
name|getRMAdminService
argument_list|()
return|;
block|}
DECL|method|setRMAdminService (AdminService adminService)
name|void
name|setRMAdminService
parameter_list|(
name|AdminService
name|adminService
parameter_list|)
block|{
name|serviceContext
operator|.
name|setRMAdminService
argument_list|(
name|adminService
argument_list|)
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
name|serviceContext
operator|.
name|isHAEnabled
argument_list|()
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
name|serviceContext
operator|.
name|setHAEnabled
argument_list|(
name|isHAEnabled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getHAServiceState ()
specifier|public
name|HAServiceState
name|getHAServiceState
parameter_list|()
block|{
return|return
name|serviceContext
operator|.
name|getHAServiceState
argument_list|()
return|;
block|}
DECL|method|setHAServiceState (HAServiceState serviceState)
name|void
name|setHAServiceState
parameter_list|(
name|HAServiceState
name|serviceState
parameter_list|)
block|{
name|serviceContext
operator|.
name|setHAServiceState
argument_list|(
name|serviceState
argument_list|)
expr_stmt|;
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
name|serviceContext
operator|.
name|getRMApplicationHistoryWriter
argument_list|()
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
name|serviceContext
operator|.
name|setRMApplicationHistoryWriter
argument_list|(
name|rmApplicationHistoryWriter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSystemMetricsPublisher ()
specifier|public
name|SystemMetricsPublisher
name|getSystemMetricsPublisher
parameter_list|()
block|{
return|return
name|serviceContext
operator|.
name|getSystemMetricsPublisher
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setSystemMetricsPublisher ( SystemMetricsPublisher metricsPublisher)
specifier|public
name|void
name|setSystemMetricsPublisher
parameter_list|(
name|SystemMetricsPublisher
name|metricsPublisher
parameter_list|)
block|{
name|serviceContext
operator|.
name|setSystemMetricsPublisher
argument_list|(
name|metricsPublisher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRMTimelineCollectorManager ()
specifier|public
name|RMTimelineCollectorManager
name|getRMTimelineCollectorManager
parameter_list|()
block|{
return|return
name|serviceContext
operator|.
name|getRMTimelineCollectorManager
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setRMTimelineCollectorManager ( RMTimelineCollectorManager timelineCollectorManager)
specifier|public
name|void
name|setRMTimelineCollectorManager
parameter_list|(
name|RMTimelineCollectorManager
name|timelineCollectorManager
parameter_list|)
block|{
name|serviceContext
operator|.
name|setRMTimelineCollectorManager
argument_list|(
name|timelineCollectorManager
argument_list|)
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
name|serviceContext
operator|.
name|getConfigurationProvider
argument_list|()
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
name|serviceContext
operator|.
name|setConfigurationProvider
argument_list|(
name|configurationProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getYarnConfiguration ()
specifier|public
name|Configuration
name|getYarnConfiguration
parameter_list|()
block|{
return|return
name|serviceContext
operator|.
name|getYarnConfiguration
argument_list|()
return|;
block|}
DECL|method|setYarnConfiguration (Configuration yarnConfiguration)
specifier|public
name|void
name|setYarnConfiguration
parameter_list|(
name|Configuration
name|yarnConfiguration
parameter_list|)
block|{
name|serviceContext
operator|.
name|setYarnConfiguration
argument_list|(
name|yarnConfiguration
argument_list|)
expr_stmt|;
block|}
DECL|method|getHAZookeeperConnectionState ()
specifier|public
name|String
name|getHAZookeeperConnectionState
parameter_list|()
block|{
return|return
name|serviceContext
operator|.
name|getHAZookeeperConnectionState
argument_list|()
return|;
block|}
comment|// ==========================================================================
comment|/**    * RM Active service context. This will be recreated for every transition from    * ACTIVE to STANDBY.    * @return activeServiceContext of active services    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getActiveServiceContext ()
specifier|public
name|RMActiveServiceContext
name|getActiveServiceContext
parameter_list|()
block|{
return|return
name|activeServiceContext
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setActiveServiceContext (RMActiveServiceContext activeServiceContext)
name|void
name|setActiveServiceContext
parameter_list|(
name|RMActiveServiceContext
name|activeServiceContext
parameter_list|)
block|{
name|this
operator|.
name|activeServiceContext
operator|=
name|activeServiceContext
expr_stmt|;
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
name|activeServiceContext
operator|.
name|getStateStore
argument_list|()
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
name|activeServiceContext
operator|.
name|getRMApps
argument_list|()
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
name|activeServiceContext
operator|.
name|getRMNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|activeServiceContext
operator|.
name|getInactiveRMNodes
argument_list|()
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
name|activeServiceContext
operator|.
name|getContainerAllocationExpirer
argument_list|()
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
name|activeServiceContext
operator|.
name|getAMLivelinessMonitor
argument_list|()
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
name|activeServiceContext
operator|.
name|getAMFinishingMonitor
argument_list|()
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
name|activeServiceContext
operator|.
name|getDelegationTokenRenewer
argument_list|()
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
name|activeServiceContext
operator|.
name|getAMRMTokenSecretManager
argument_list|()
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
name|activeServiceContext
operator|.
name|getContainerTokenSecretManager
argument_list|()
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
name|activeServiceContext
operator|.
name|getNMTokenSecretManager
argument_list|()
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
name|activeServiceContext
operator|.
name|getScheduler
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReservationSystem ()
specifier|public
name|ReservationSystem
name|getReservationSystem
parameter_list|()
block|{
return|return
name|activeServiceContext
operator|.
name|getReservationSystem
argument_list|()
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
name|activeServiceContext
operator|.
name|getNodesListManager
argument_list|()
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
name|activeServiceContext
operator|.
name|getClientToAMTokenSecretManager
argument_list|()
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
name|activeServiceContext
operator|.
name|setStateStore
argument_list|(
name|store
argument_list|)
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
name|activeServiceContext
operator|.
name|getClientRMService
argument_list|()
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
name|activeServiceContext
operator|.
name|getApplicationMasterService
argument_list|()
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
name|activeServiceContext
operator|.
name|getResourceTrackerService
argument_list|()
return|;
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
name|activeServiceContext
operator|.
name|setClientRMService
argument_list|(
name|clientRMService
argument_list|)
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
name|activeServiceContext
operator|.
name|getRMDelegationTokenSecretManager
argument_list|()
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
name|activeServiceContext
operator|.
name|setRMDelegationTokenSecretManager
argument_list|(
name|delegationTokenSecretManager
argument_list|)
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
name|activeServiceContext
operator|.
name|setContainerAllocationExpirer
argument_list|(
name|containerAllocationExpirer
argument_list|)
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
name|activeServiceContext
operator|.
name|setAMLivelinessMonitor
argument_list|(
name|amLivelinessMonitor
argument_list|)
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
name|activeServiceContext
operator|.
name|setAMFinishingMonitor
argument_list|(
name|amFinishingMonitor
argument_list|)
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
name|activeServiceContext
operator|.
name|setContainerTokenSecretManager
argument_list|(
name|containerTokenSecretManager
argument_list|)
expr_stmt|;
block|}
DECL|method|setNMTokenSecretManager (NMTokenSecretManagerInRM nmTokenSecretManager)
name|void
name|setNMTokenSecretManager
parameter_list|(
name|NMTokenSecretManagerInRM
name|nmTokenSecretManager
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setNMTokenSecretManager
argument_list|(
name|nmTokenSecretManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setScheduler (ResourceScheduler scheduler)
specifier|public
name|void
name|setScheduler
parameter_list|(
name|ResourceScheduler
name|scheduler
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setScheduler
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
block|}
DECL|method|setReservationSystem (ReservationSystem reservationSystem)
name|void
name|setReservationSystem
parameter_list|(
name|ReservationSystem
name|reservationSystem
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setReservationSystem
argument_list|(
name|reservationSystem
argument_list|)
expr_stmt|;
block|}
DECL|method|setDelegationTokenRenewer (DelegationTokenRenewer delegationTokenRenewer)
name|void
name|setDelegationTokenRenewer
parameter_list|(
name|DelegationTokenRenewer
name|delegationTokenRenewer
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setDelegationTokenRenewer
argument_list|(
name|delegationTokenRenewer
argument_list|)
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
name|activeServiceContext
operator|.
name|setClientToAMTokenSecretManager
argument_list|(
name|clientToAMTokenSecretManager
argument_list|)
expr_stmt|;
block|}
DECL|method|setAMRMTokenSecretManager (AMRMTokenSecretManager amRMTokenSecretManager)
name|void
name|setAMRMTokenSecretManager
parameter_list|(
name|AMRMTokenSecretManager
name|amRMTokenSecretManager
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setAMRMTokenSecretManager
argument_list|(
name|amRMTokenSecretManager
argument_list|)
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
name|activeServiceContext
operator|.
name|setNodesListManager
argument_list|(
name|nodesListManager
argument_list|)
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
name|activeServiceContext
operator|.
name|setApplicationMasterService
argument_list|(
name|applicationMasterService
argument_list|)
expr_stmt|;
block|}
DECL|method|setResourceTrackerService (ResourceTrackerService resourceTrackerService)
name|void
name|setResourceTrackerService
parameter_list|(
name|ResourceTrackerService
name|resourceTrackerService
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setResourceTrackerService
argument_list|(
name|resourceTrackerService
argument_list|)
expr_stmt|;
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
name|activeServiceContext
operator|.
name|setWorkPreservingRecoveryEnabled
argument_list|(
name|enabled
argument_list|)
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
name|activeServiceContext
operator|.
name|isWorkPreservingRecoveryEnabled
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getEpoch ()
specifier|public
name|long
name|getEpoch
parameter_list|()
block|{
return|return
name|activeServiceContext
operator|.
name|getEpoch
argument_list|()
return|;
block|}
DECL|method|setEpoch (long epoch)
name|void
name|setEpoch
parameter_list|(
name|long
name|epoch
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setEpoch
argument_list|(
name|epoch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeLabelManager ()
specifier|public
name|RMNodeLabelsManager
name|getNodeLabelManager
parameter_list|()
block|{
return|return
name|activeServiceContext
operator|.
name|getNodeLabelManager
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeLabelManager (RMNodeLabelsManager mgr)
specifier|public
name|void
name|setNodeLabelManager
parameter_list|(
name|RMNodeLabelsManager
name|mgr
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setNodeLabelManager
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRMDelegatedNodeLabelsUpdater ()
specifier|public
name|RMDelegatedNodeLabelsUpdater
name|getRMDelegatedNodeLabelsUpdater
parameter_list|()
block|{
return|return
name|activeServiceContext
operator|.
name|getRMDelegatedNodeLabelsUpdater
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setRMDelegatedNodeLabelsUpdater ( RMDelegatedNodeLabelsUpdater delegatedNodeLabelsUpdater)
specifier|public
name|void
name|setRMDelegatedNodeLabelsUpdater
parameter_list|(
name|RMDelegatedNodeLabelsUpdater
name|delegatedNodeLabelsUpdater
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setRMDelegatedNodeLabelsUpdater
argument_list|(
name|delegatedNodeLabelsUpdater
argument_list|)
expr_stmt|;
block|}
DECL|method|setSchedulerRecoveryStartAndWaitTime (long waitTime)
specifier|public
name|void
name|setSchedulerRecoveryStartAndWaitTime
parameter_list|(
name|long
name|waitTime
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setSchedulerRecoveryStartAndWaitTime
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
block|}
DECL|method|isSchedulerReadyForAllocatingContainers ()
specifier|public
name|boolean
name|isSchedulerReadyForAllocatingContainers
parameter_list|()
block|{
return|return
name|activeServiceContext
operator|.
name|isSchedulerReadyForAllocatingContainers
argument_list|()
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|setSystemClock (Clock clock)
specifier|public
name|void
name|setSystemClock
parameter_list|(
name|Clock
name|clock
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setSystemClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
block|}
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
name|activeServiceContext
operator|.
name|getSystemCredentialsForApps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getQueuePlacementManager ()
specifier|public
name|PlacementManager
name|getQueuePlacementManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|activeServiceContext
operator|.
name|getQueuePlacementManager
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|activeServiceContext
operator|.
name|setQueuePlacementManager
argument_list|(
name|placementMgr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeManagerQueueLimitCalculator ()
specifier|public
name|QueueLimitCalculator
name|getNodeManagerQueueLimitCalculator
parameter_list|()
block|{
return|return
name|activeServiceContext
operator|.
name|getNodeManagerQueueLimitCalculator
argument_list|()
return|;
block|}
DECL|method|setContainerQueueLimitCalculator ( QueueLimitCalculator limitCalculator)
specifier|public
name|void
name|setContainerQueueLimitCalculator
parameter_list|(
name|QueueLimitCalculator
name|limitCalculator
parameter_list|)
block|{
name|activeServiceContext
operator|.
name|setContainerQueueLimitCalculator
argument_list|(
name|limitCalculator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setRMAppLifetimeMonitor ( RMAppLifetimeMonitor rmAppLifetimeMonitor)
specifier|public
name|void
name|setRMAppLifetimeMonitor
parameter_list|(
name|RMAppLifetimeMonitor
name|rmAppLifetimeMonitor
parameter_list|)
block|{
name|this
operator|.
name|activeServiceContext
operator|.
name|setRMAppLifetimeMonitor
argument_list|(
name|rmAppLifetimeMonitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRMAppLifetimeMonitor ()
specifier|public
name|RMAppLifetimeMonitor
name|getRMAppLifetimeMonitor
parameter_list|()
block|{
return|return
name|this
operator|.
name|activeServiceContext
operator|.
name|getRMAppLifetimeMonitor
argument_list|()
return|;
block|}
comment|// Note: Read java doc before adding any services over here.
block|}
end_class

end_unit

