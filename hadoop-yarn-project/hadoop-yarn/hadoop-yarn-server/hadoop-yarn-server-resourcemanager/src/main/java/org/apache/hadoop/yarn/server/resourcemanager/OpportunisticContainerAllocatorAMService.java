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
name|ipc
operator|.
name|RPC
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
name|ipc
operator|.
name|Server
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
name|ams
operator|.
name|ApplicationMasterServiceProcessor
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
name|ams
operator|.
name|ApplicationMasterServiceUtils
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
name|ApplicationMasterProtocolPB
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
name|ApplicationAttemptId
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
name|event
operator|.
name|EventHandler
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
name|security
operator|.
name|AMRMTokenIdentifier
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
name|DistributedSchedulingAMProtocol
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
name|impl
operator|.
name|pb
operator|.
name|service
operator|.
name|ApplicationMasterProtocolPBServiceImpl
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
name|AllocateRequest
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
name|AllocateResponse
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
name|protocolrecords
operator|.
name|DistributedSchedulingAllocateRequest
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
name|protocolrecords
operator|.
name|DistributedSchedulingAllocateResponse
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
name|protocolrecords
operator|.
name|RegisterDistributedSchedulingAMResponse
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
name|RegisterApplicationMasterRequest
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
name|RegisterApplicationMasterResponse
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
name|exceptions
operator|.
name|YarnException
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
name|ipc
operator|.
name|YarnRPC
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
name|proto
operator|.
name|ApplicationMasterProtocol
operator|.
name|ApplicationMasterProtocolService
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
name|protocolrecords
operator|.
name|RemoteNode
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
name|RMContainer
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
name|RMContainerEvent
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
name|RMContainerEventType
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
name|AbstractYarnScheduler
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
name|SchedulerApplicationAttempt
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
name|SchedulerNode
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
name|SchedulerUtils
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
name|YarnScheduler
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
name|NodeQueueLoadMonitor
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
name|scheduler
operator|.
name|event
operator|.
name|NodeAddedSchedulerEvent
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
name|event
operator|.
name|NodeRemovedSchedulerEvent
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
name|event
operator|.
name|NodeResourceUpdateSchedulerEvent
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
name|event
operator|.
name|NodeUpdateSchedulerEvent
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
name|event
operator|.
name|SchedulerEvent
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
name|scheduler
operator|.
name|OpportunisticContainerAllocator
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
name|scheduler
operator|.
name|OpportunisticContainerContext
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
name|utils
operator|.
name|YarnServerSecurityUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  * The OpportunisticContainerAllocatorAMService is started instead of the  * ApplicationMasterService if opportunistic scheduling is enabled for the YARN  * cluster (either centralized or distributed opportunistic scheduling).  *  * It extends the functionality of the ApplicationMasterService by servicing  * clients (AMs and AMRMProxy request interceptors) that understand the  * DistributedSchedulingProtocol.  */
end_comment

begin_class
DECL|class|OpportunisticContainerAllocatorAMService
specifier|public
class|class
name|OpportunisticContainerAllocatorAMService
extends|extends
name|ApplicationMasterService
implements|implements
name|DistributedSchedulingAMProtocol
implements|,
name|EventHandler
argument_list|<
name|SchedulerEvent
argument_list|>
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
name|OpportunisticContainerAllocatorAMService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nodeMonitor
specifier|private
specifier|final
name|NodeQueueLoadMonitor
name|nodeMonitor
decl_stmt|;
DECL|field|oppContainerAllocator
specifier|private
specifier|final
name|OpportunisticContainerAllocator
name|oppContainerAllocator
decl_stmt|;
DECL|field|k
specifier|private
specifier|final
name|int
name|k
decl_stmt|;
DECL|field|cacheRefreshInterval
specifier|private
specifier|final
name|long
name|cacheRefreshInterval
decl_stmt|;
DECL|field|cachedNodes
specifier|private
specifier|volatile
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|cachedNodes
decl_stmt|;
DECL|field|lastCacheUpdateTime
specifier|private
specifier|volatile
name|long
name|lastCacheUpdateTime
decl_stmt|;
DECL|class|OpportunisticAMSProcessor
class|class
name|OpportunisticAMSProcessor
extends|extends
name|DefaultAMSProcessor
block|{
DECL|method|OpportunisticAMSProcessor (RMContext rmContext, YarnScheduler scheduler)
name|OpportunisticAMSProcessor
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|)
block|{
name|super
argument_list|(
name|rmContext
argument_list|,
name|scheduler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|registerApplicationMaster ( ApplicationAttemptId applicationAttemptId, RegisterApplicationMasterRequest request)
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|SchedulerApplicationAttempt
name|appAttempt
init|=
operator|(
operator|(
name|AbstractYarnScheduler
operator|)
name|getScheduler
argument_list|()
operator|)
operator|.
name|getApplicationAttempt
argument_list|(
name|applicationAttemptId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appAttempt
operator|.
name|getOpportunisticContainerContext
argument_list|()
operator|==
literal|null
condition|)
block|{
name|OpportunisticContainerContext
name|opCtx
init|=
operator|new
name|OpportunisticContainerContext
argument_list|()
decl_stmt|;
name|opCtx
operator|.
name|setContainerIdGenerator
argument_list|(
operator|new
name|OpportunisticContainerAllocator
operator|.
name|ContainerIdGenerator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|generateContainerId
parameter_list|()
block|{
return|return
name|appAttempt
operator|.
name|getAppSchedulingInfo
argument_list|()
operator|.
name|getNewContainerId
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|int
name|tokenExpiryInterval
init|=
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
argument_list|)
decl_stmt|;
name|opCtx
operator|.
name|updateAllocationParams
argument_list|(
name|getScheduler
argument_list|()
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|,
name|getScheduler
argument_list|()
operator|.
name|getMaximumResourceCapability
argument_list|()
argument_list|,
name|getScheduler
argument_list|()
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|,
name|tokenExpiryInterval
argument_list|)
expr_stmt|;
name|appAttempt
operator|.
name|setOpportunisticContainerContext
argument_list|(
name|opCtx
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|registerApplicationMaster
argument_list|(
name|applicationAttemptId
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|allocate (ApplicationAttemptId appAttemptId, AllocateRequest request)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
comment|// Partition requests to GUARANTEED and OPPORTUNISTIC.
name|OpportunisticContainerAllocator
operator|.
name|PartitionedResourceRequests
name|partitionedAsks
init|=
name|oppContainerAllocator
operator|.
name|partitionAskList
argument_list|(
name|request
operator|.
name|getAskList
argument_list|()
argument_list|)
decl_stmt|;
comment|// Allocate OPPORTUNISTIC containers.
name|SchedulerApplicationAttempt
name|appAttempt
init|=
operator|(
operator|(
name|AbstractYarnScheduler
operator|)
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|)
operator|.
name|getApplicationAttempt
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|OpportunisticContainerContext
name|oppCtx
init|=
name|appAttempt
operator|.
name|getOpportunisticContainerContext
argument_list|()
decl_stmt|;
name|oppCtx
operator|.
name|updateNodeList
argument_list|(
name|getLeastLoadedNodes
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|oppContainers
init|=
name|oppContainerAllocator
operator|.
name|allocateContainers
argument_list|(
name|request
operator|.
name|getResourceBlacklistRequest
argument_list|()
argument_list|,
name|partitionedAsks
operator|.
name|getOpportunistic
argument_list|()
argument_list|,
name|appAttemptId
argument_list|,
name|oppCtx
argument_list|,
name|ResourceManager
operator|.
name|getClusterTimeStamp
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create RMContainers and update the NMTokens.
if|if
condition|(
operator|!
name|oppContainers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|handleNewContainers
argument_list|(
name|oppContainers
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|appAttempt
operator|.
name|updateNMTokens
argument_list|(
name|oppContainers
argument_list|)
expr_stmt|;
block|}
comment|// Allocate GUARANTEED containers.
name|request
operator|.
name|setAskList
argument_list|(
name|partitionedAsks
operator|.
name|getGuaranteed
argument_list|()
argument_list|)
expr_stmt|;
name|AllocateResponse
name|response
init|=
name|super
operator|.
name|allocate
argument_list|(
name|appAttemptId
argument_list|,
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|oppContainers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ApplicationMasterServiceUtils
operator|.
name|addToAllocatedContainers
argument_list|(
name|response
argument_list|,
name|oppContainers
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
block|}
DECL|method|OpportunisticContainerAllocatorAMService (RMContext rmContext, YarnScheduler scheduler)
specifier|public
name|OpportunisticContainerAllocatorAMService
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|)
block|{
name|super
argument_list|(
name|OpportunisticContainerAllocatorAMService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|rmContext
argument_list|,
name|scheduler
argument_list|)
expr_stmt|;
name|this
operator|.
name|oppContainerAllocator
operator|=
operator|new
name|OpportunisticContainerAllocator
argument_list|(
name|rmContext
operator|.
name|getContainerTokenSecretManager
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|k
operator|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|OPP_CONTAINER_ALLOCATION_NODES_NUMBER_USED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_OPP_CONTAINER_ALLOCATION_NODES_NUMBER_USED
argument_list|)
expr_stmt|;
name|long
name|nodeSortInterval
init|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_QUEUING_SORTING_NODES_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_QUEUING_SORTING_NODES_INTERVAL_MS
argument_list|)
decl_stmt|;
name|this
operator|.
name|cacheRefreshInterval
operator|=
name|nodeSortInterval
expr_stmt|;
name|this
operator|.
name|lastCacheUpdateTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|NodeQueueLoadMonitor
operator|.
name|LoadComparator
name|comparator
init|=
name|NodeQueueLoadMonitor
operator|.
name|LoadComparator
operator|.
name|valueOf
argument_list|(
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_QUEUING_LOAD_COMPARATOR
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_QUEUING_LOAD_COMPARATOR
argument_list|)
argument_list|)
decl_stmt|;
name|NodeQueueLoadMonitor
name|topKSelector
init|=
operator|new
name|NodeQueueLoadMonitor
argument_list|(
name|nodeSortInterval
argument_list|,
name|comparator
argument_list|)
decl_stmt|;
name|float
name|sigma
init|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getFloat
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_QUEUING_LIMIT_STDEV
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_QUEUING_LIMIT_STDEV
argument_list|)
decl_stmt|;
name|int
name|limitMin
decl_stmt|,
name|limitMax
decl_stmt|;
if|if
condition|(
name|comparator
operator|==
name|NodeQueueLoadMonitor
operator|.
name|LoadComparator
operator|.
name|QUEUE_LENGTH
condition|)
block|{
name|limitMin
operator|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_QUEUING_MIN_QUEUE_LENGTH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_QUEUING_MIN_QUEUE_LENGTH
argument_list|)
expr_stmt|;
name|limitMax
operator|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_QUEUING_MAX_QUEUE_LENGTH
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_QUEUING_MAX_QUEUE_LENGTH
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|limitMin
operator|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_QUEUING_MIN_QUEUE_WAIT_TIME_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_QUEUING_MIN_QUEUE_WAIT_TIME_MS
argument_list|)
expr_stmt|;
name|limitMax
operator|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_QUEUING_MAX_QUEUE_WAIT_TIME_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_QUEUING_MAX_QUEUE_WAIT_TIME_MS
argument_list|)
expr_stmt|;
block|}
name|topKSelector
operator|.
name|initThresholdCalculator
argument_list|(
name|sigma
argument_list|,
name|limitMin
argument_list|,
name|limitMax
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeMonitor
operator|=
name|topKSelector
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createProcessor ()
specifier|protected
name|ApplicationMasterServiceProcessor
name|createProcessor
parameter_list|()
block|{
return|return
operator|new
name|OpportunisticAMSProcessor
argument_list|(
name|rmContext
argument_list|,
name|rmContext
operator|.
name|getScheduler
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getServer (YarnRPC rpc, Configuration serverConf, InetSocketAddress addr, AMRMTokenSecretManager secretManager)
specifier|public
name|Server
name|getServer
parameter_list|(
name|YarnRPC
name|rpc
parameter_list|,
name|Configuration
name|serverConf
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|AMRMTokenSecretManager
name|secretManager
parameter_list|)
block|{
if|if
condition|(
name|YarnConfiguration
operator|.
name|isDistSchedulingEnabled
argument_list|(
name|serverConf
argument_list|)
condition|)
block|{
name|Server
name|server
init|=
name|rpc
operator|.
name|getServer
argument_list|(
name|DistributedSchedulingAMProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|addr
argument_list|,
name|serverConf
argument_list|,
name|secretManager
argument_list|,
name|serverConf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_CLIENT_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_CLIENT_THREAD_COUNT
argument_list|)
argument_list|)
decl_stmt|;
comment|// To support application running on NMs that DO NOT support
comment|// Dist Scheduling... The server multiplexes both the
comment|// ApplicationMasterProtocol as well as the DistributedSchedulingProtocol
operator|(
operator|(
name|RPC
operator|.
name|Server
operator|)
name|server
operator|)
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|ApplicationMasterProtocolPB
operator|.
name|class
argument_list|,
name|ApplicationMasterProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
operator|new
name|ApplicationMasterProtocolPBServiceImpl
argument_list|(
name|this
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|server
return|;
block|}
return|return
name|super
operator|.
name|getServer
argument_list|(
name|rpc
argument_list|,
name|serverConf
argument_list|,
name|addr
argument_list|,
name|secretManager
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RegisterDistributedSchedulingAMResponse
DECL|method|registerApplicationMasterForDistributedScheduling ( RegisterApplicationMasterRequest request)
name|registerApplicationMasterForDistributedScheduling
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|RegisterApplicationMasterResponse
name|response
init|=
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|RegisterDistributedSchedulingAMResponse
name|dsResp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterDistributedSchedulingAMResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|dsResp
operator|.
name|setRegisterResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setMinContainerResource
argument_list|(
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setMaxContainerResource
argument_list|(
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getMaximumResourceCapability
argument_list|()
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setContainerTokenExpiryInterval
argument_list|(
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
argument_list|)
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setContainerIdStart
argument_list|(
name|this
operator|.
name|rmContext
operator|.
name|getEpoch
argument_list|()
operator|<<
name|ResourceManager
operator|.
name|EPOCH_BIT_SHIFT
argument_list|)
expr_stmt|;
comment|// Set nodes to be used for scheduling
name|dsResp
operator|.
name|setNodesForScheduling
argument_list|(
name|getLeastLoadedNodes
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dsResp
return|;
block|}
annotation|@
name|Override
DECL|method|allocateForDistributedScheduling ( DistributedSchedulingAllocateRequest request)
specifier|public
name|DistributedSchedulingAllocateResponse
name|allocateForDistributedScheduling
parameter_list|(
name|DistributedSchedulingAllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|Container
argument_list|>
name|distAllocContainers
init|=
name|request
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|handleNewContainers
argument_list|(
name|distAllocContainers
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|AllocateResponse
name|response
init|=
name|allocate
argument_list|(
name|request
operator|.
name|getAllocateRequest
argument_list|()
argument_list|)
decl_stmt|;
name|DistributedSchedulingAllocateResponse
name|dsResp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|DistributedSchedulingAllocateResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|dsResp
operator|.
name|setAllocateResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setNodesForScheduling
argument_list|(
name|getLeastLoadedNodes
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dsResp
return|;
block|}
DECL|method|handleNewContainers (List<Container> allocContainers, boolean isRemotelyAllocated)
specifier|private
name|void
name|handleNewContainers
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|allocContainers
parameter_list|,
name|boolean
name|isRemotelyAllocated
parameter_list|)
block|{
for|for
control|(
name|Container
name|container
range|:
name|allocContainers
control|)
block|{
comment|// Create RMContainer
name|RMContainer
name|rmContainer
init|=
name|SchedulerUtils
operator|.
name|createOpportunisticRmContainer
argument_list|(
name|rmContext
argument_list|,
name|container
argument_list|,
name|isRemotelyAllocated
argument_list|)
decl_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|RMContainerEventType
operator|.
name|ACQUIRED
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|handle (SchedulerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|SchedulerEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|NODE_ADDED
case|:
if|if
condition|(
operator|!
operator|(
name|event
operator|instanceof
name|NodeAddedSchedulerEvent
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected event type: "
operator|+
name|event
argument_list|)
throw|;
block|}
name|NodeAddedSchedulerEvent
name|nodeAddedEvent
init|=
operator|(
name|NodeAddedSchedulerEvent
operator|)
name|event
decl_stmt|;
name|nodeMonitor
operator|.
name|addNode
argument_list|(
name|nodeAddedEvent
operator|.
name|getContainerReports
argument_list|()
argument_list|,
name|nodeAddedEvent
operator|.
name|getAddedRMNode
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NODE_REMOVED
case|:
if|if
condition|(
operator|!
operator|(
name|event
operator|instanceof
name|NodeRemovedSchedulerEvent
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected event type: "
operator|+
name|event
argument_list|)
throw|;
block|}
name|NodeRemovedSchedulerEvent
name|nodeRemovedEvent
init|=
operator|(
name|NodeRemovedSchedulerEvent
operator|)
name|event
decl_stmt|;
name|nodeMonitor
operator|.
name|removeNode
argument_list|(
name|nodeRemovedEvent
operator|.
name|getRemovedRMNode
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NODE_UPDATE
case|:
if|if
condition|(
operator|!
operator|(
name|event
operator|instanceof
name|NodeUpdateSchedulerEvent
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected event type: "
operator|+
name|event
argument_list|)
throw|;
block|}
name|NodeUpdateSchedulerEvent
name|nodeUpdatedEvent
init|=
operator|(
name|NodeUpdateSchedulerEvent
operator|)
name|event
decl_stmt|;
name|nodeMonitor
operator|.
name|updateNode
argument_list|(
name|nodeUpdatedEvent
operator|.
name|getRMNode
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NODE_RESOURCE_UPDATE
case|:
if|if
condition|(
operator|!
operator|(
name|event
operator|instanceof
name|NodeResourceUpdateSchedulerEvent
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected event type: "
operator|+
name|event
argument_list|)
throw|;
block|}
name|NodeResourceUpdateSchedulerEvent
name|nodeResourceUpdatedEvent
init|=
operator|(
name|NodeResourceUpdateSchedulerEvent
operator|)
name|event
decl_stmt|;
name|nodeMonitor
operator|.
name|updateNodeResource
argument_list|(
name|nodeResourceUpdatedEvent
operator|.
name|getRMNode
argument_list|()
argument_list|,
name|nodeResourceUpdatedEvent
operator|.
name|getResourceOption
argument_list|()
argument_list|)
expr_stmt|;
break|break;
comment|//<-- IGNORED EVENTS : START -->
case|case
name|APP_ADDED
case|:
break|break;
case|case
name|APP_REMOVED
case|:
break|break;
case|case
name|APP_ATTEMPT_ADDED
case|:
break|break;
case|case
name|APP_ATTEMPT_REMOVED
case|:
break|break;
case|case
name|CONTAINER_EXPIRED
case|:
break|break;
case|case
name|NODE_LABELS_UPDATE
case|:
break|break;
comment|//<-- IGNORED EVENTS : END -->
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown event arrived at"
operator|+
literal|"OpportunisticContainerAllocatorAMService: "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getNodeManagerQueueLimitCalculator ()
specifier|public
name|QueueLimitCalculator
name|getNodeManagerQueueLimitCalculator
parameter_list|()
block|{
return|return
name|nodeMonitor
operator|.
name|getThresholdCalculator
argument_list|()
return|;
block|}
DECL|method|getLeastLoadedNodes ()
specifier|private
specifier|synchronized
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|getLeastLoadedNodes
parameter_list|()
block|{
name|long
name|currTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|currTime
operator|-
name|lastCacheUpdateTime
operator|>
name|cacheRefreshInterval
operator|)
operator|||
operator|(
name|cachedNodes
operator|==
literal|null
operator|)
condition|)
block|{
name|cachedNodes
operator|=
name|convertToRemoteNodes
argument_list|(
name|this
operator|.
name|nodeMonitor
operator|.
name|selectLeastLoadedNodes
argument_list|(
name|this
operator|.
name|k
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|cachedNodes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lastCacheUpdateTime
operator|=
name|currTime
expr_stmt|;
block|}
block|}
return|return
name|cachedNodes
return|;
block|}
DECL|method|convertToRemoteNodes (List<NodeId> nodeIds)
specifier|private
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|convertToRemoteNodes
parameter_list|(
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodeIds
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|RemoteNode
argument_list|>
name|retNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeId
name|nId
range|:
name|nodeIds
control|)
block|{
name|RemoteNode
name|remoteNode
init|=
name|convertToRemoteNode
argument_list|(
name|nId
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|remoteNode
condition|)
block|{
name|retNodes
operator|.
name|add
argument_list|(
name|remoteNode
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|retNodes
return|;
block|}
DECL|method|convertToRemoteNode (NodeId nodeId)
specifier|private
name|RemoteNode
name|convertToRemoteNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|SchedulerNode
name|node
init|=
operator|(
operator|(
name|AbstractYarnScheduler
operator|)
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
return|return
name|node
operator|!=
literal|null
condition|?
name|RemoteNode
operator|.
name|newInstance
argument_list|(
name|nodeId
argument_list|,
name|node
operator|.
name|getHttpAddress
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|getAppAttemptId ()
specifier|private
specifier|static
name|ApplicationAttemptId
name|getAppAttemptId
parameter_list|()
throws|throws
name|YarnException
block|{
name|AMRMTokenIdentifier
name|amrmTokenIdentifier
init|=
name|YarnServerSecurityUtils
operator|.
name|authorizeRequest
argument_list|()
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|amrmTokenIdentifier
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
return|return
name|applicationAttemptId
return|;
block|}
block|}
end_class

end_unit

