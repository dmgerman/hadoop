begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity
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
operator|.
name|monitor
operator|.
name|capacity
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
name|Priority
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
name|resourcemanager
operator|.
name|monitor
operator|.
name|capacity
operator|.
name|ProportionalCapacityPreemptionPolicy
operator|.
name|IntraQueuePreemptionOrderPolicy
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
name|scheduler
operator|.
name|capacity
operator|.
name|LeafQueue
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
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerApp
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
name|resource
operator|.
name|Resources
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Identifies over utilized resources within a queue and tries to normalize  * them to resolve resource allocation anomalies w.r.t priority and user-limit.  */
end_comment

begin_class
DECL|class|IntraQueueCandidatesSelector
specifier|public
class|class
name|IntraQueueCandidatesSelector
extends|extends
name|PreemptionCandidatesSelector
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|TAPriorityComparator
specifier|static
class|class
name|TAPriorityComparator
implements|implements
name|Serializable
implements|,
name|Comparator
argument_list|<
name|TempAppPerPartition
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (TempAppPerPartition ta1, TempAppPerPartition ta2)
specifier|public
name|int
name|compare
parameter_list|(
name|TempAppPerPartition
name|ta1
parameter_list|,
name|TempAppPerPartition
name|ta2
parameter_list|)
block|{
name|Priority
name|p1
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
name|ta1
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
name|Priority
name|p2
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
name|ta2
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|p1
operator|.
name|equals
argument_list|(
name|p2
argument_list|)
condition|)
block|{
return|return
name|p1
operator|.
name|compareTo
argument_list|(
name|p2
argument_list|)
return|;
block|}
return|return
name|ta1
operator|.
name|getApplicationId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|ta2
operator|.
name|getApplicationId
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|field|fifoPreemptionComputePlugin
name|IntraQueuePreemptionComputePlugin
name|fifoPreemptionComputePlugin
init|=
literal|null
decl_stmt|;
DECL|field|context
specifier|final
name|CapacitySchedulerPreemptionContext
name|context
decl_stmt|;
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
name|IntraQueueCandidatesSelector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|IntraQueueCandidatesSelector ( CapacitySchedulerPreemptionContext preemptionContext)
name|IntraQueueCandidatesSelector
parameter_list|(
name|CapacitySchedulerPreemptionContext
name|preemptionContext
parameter_list|)
block|{
name|super
argument_list|(
name|preemptionContext
argument_list|)
expr_stmt|;
name|fifoPreemptionComputePlugin
operator|=
operator|new
name|FifoIntraQueuePreemptionPlugin
argument_list|(
name|rc
argument_list|,
name|preemptionContext
argument_list|)
expr_stmt|;
name|context
operator|=
name|preemptionContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|selectCandidates ( Map<ApplicationAttemptId, Set<RMContainer>> selectedCandidates, Resource clusterResource, Resource totalPreemptedResourceAllowed)
specifier|public
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|Set
argument_list|<
name|RMContainer
argument_list|>
argument_list|>
name|selectCandidates
parameter_list|(
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|Set
argument_list|<
name|RMContainer
argument_list|>
argument_list|>
name|selectedCandidates
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|totalPreemptedResourceAllowed
parameter_list|)
block|{
comment|// 1. Calculate the abnormality within each queue one by one.
name|computeIntraQueuePreemptionDemand
argument_list|(
name|clusterResource
argument_list|,
name|totalPreemptedResourceAllowed
argument_list|,
name|selectedCandidates
argument_list|)
expr_stmt|;
comment|// 2. Previous selectors (with higher priority) could have already
comment|// selected containers. We need to deduct pre-emptable resources
comment|// based on already selected candidates.
name|CapacitySchedulerPreemptionUtils
operator|.
name|deductPreemptableResourcesBasedSelectedCandidates
argument_list|(
name|preemptionContext
argument_list|,
name|selectedCandidates
argument_list|)
expr_stmt|;
comment|// 3. Loop through all partitions to select containers for preemption.
for|for
control|(
name|String
name|partition
range|:
name|preemptionContext
operator|.
name|getAllPartitions
argument_list|()
control|)
block|{
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
name|queueNames
init|=
name|preemptionContext
operator|.
name|getUnderServedQueuesPerPartition
argument_list|(
name|partition
argument_list|)
decl_stmt|;
comment|// Error check to handle non-mapped labels to queue.
if|if
condition|(
literal|null
operator|==
name|queueNames
condition|)
block|{
continue|continue;
block|}
comment|// 4. Iterate from most under-served queue in order.
for|for
control|(
name|String
name|queueName
range|:
name|queueNames
control|)
block|{
name|LeafQueue
name|leafQueue
init|=
name|preemptionContext
operator|.
name|getQueueByPartition
argument_list|(
name|queueName
argument_list|,
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
operator|.
name|leafQueue
decl_stmt|;
comment|// skip if not a leafqueue
if|if
condition|(
literal|null
operator|==
name|leafQueue
condition|)
block|{
continue|continue;
block|}
comment|// Don't preempt if disabled for this queue.
if|if
condition|(
name|leafQueue
operator|.
name|getPreemptionDisabled
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// 5. Calculate the resource to obtain per partition
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|resToObtainByPartition
init|=
name|fifoPreemptionComputePlugin
operator|.
name|getResourceDemandFromAppsPerQueue
argument_list|(
name|queueName
argument_list|,
name|partition
argument_list|)
decl_stmt|;
comment|// Default preemption iterator considers only FIFO+priority. For
comment|// userlimit preemption, its possible that some lower priority apps
comment|// needs from high priority app of another user. Hence use apps
comment|// ordered by userlimit starvation as well.
name|Collection
argument_list|<
name|FiCaSchedulerApp
argument_list|>
name|apps
init|=
name|fifoPreemptionComputePlugin
operator|.
name|getPreemptableApps
argument_list|(
name|queueName
argument_list|,
name|partition
argument_list|)
decl_stmt|;
comment|// 6. Get user-limit to ensure that we do not preempt resources which
comment|// will force user's resource to come under its UL.
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|rollingResourceUsagePerUser
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|initializeUsageAndUserLimitForCompute
argument_list|(
name|clusterResource
argument_list|,
name|partition
argument_list|,
name|leafQueue
argument_list|,
name|rollingResourceUsagePerUser
argument_list|)
expr_stmt|;
comment|// 7. Based on the selected resource demand per partition, select
comment|// containers with known policy from inter-queue preemption.
try|try
block|{
name|leafQueue
operator|.
name|getReadLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
for|for
control|(
name|FiCaSchedulerApp
name|app
range|:
name|apps
control|)
block|{
name|preemptFromLeastStarvedApp
argument_list|(
name|leafQueue
argument_list|,
name|app
argument_list|,
name|selectedCandidates
argument_list|,
name|clusterResource
argument_list|,
name|totalPreemptedResourceAllowed
argument_list|,
name|resToObtainByPartition
argument_list|,
name|rollingResourceUsagePerUser
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|leafQueue
operator|.
name|getReadLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|selectedCandidates
return|;
block|}
DECL|method|initializeUsageAndUserLimitForCompute (Resource clusterResource, String partition, LeafQueue leafQueue, Map<String, Resource> rollingResourceUsagePerUser)
specifier|private
name|void
name|initializeUsageAndUserLimitForCompute
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|String
name|partition
parameter_list|,
name|LeafQueue
name|leafQueue
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|rollingResourceUsagePerUser
parameter_list|)
block|{
for|for
control|(
name|String
name|user
range|:
name|leafQueue
operator|.
name|getAllUsers
argument_list|()
control|)
block|{
comment|// Initialize used resource of a given user for rolling computation.
name|rollingResourceUsagePerUser
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|leafQueue
operator|.
name|getUser
argument_list|(
name|user
argument_list|)
operator|.
name|getResourceUsage
argument_list|()
operator|.
name|getUsed
argument_list|(
name|partition
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Rolling resource usage for user:"
operator|+
name|user
operator|+
literal|" is : "
operator|+
name|rollingResourceUsagePerUser
operator|.
name|get
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|preemptFromLeastStarvedApp (LeafQueue leafQueue, FiCaSchedulerApp app, Map<ApplicationAttemptId, Set<RMContainer>> selectedCandidates, Resource clusterResource, Resource totalPreemptedResourceAllowed, Map<String, Resource> resToObtainByPartition, Map<String, Resource> rollingResourceUsagePerUser)
specifier|private
name|void
name|preemptFromLeastStarvedApp
parameter_list|(
name|LeafQueue
name|leafQueue
parameter_list|,
name|FiCaSchedulerApp
name|app
parameter_list|,
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|Set
argument_list|<
name|RMContainer
argument_list|>
argument_list|>
name|selectedCandidates
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|totalPreemptedResourceAllowed
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|resToObtainByPartition
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|rollingResourceUsagePerUser
parameter_list|)
block|{
comment|// ToDo: Reuse reservation selector here.
name|List
argument_list|<
name|RMContainer
argument_list|>
name|liveContainers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|app
operator|.
name|getLiveContainers
argument_list|()
argument_list|)
decl_stmt|;
name|sortContainers
argument_list|(
name|liveContainers
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"totalPreemptedResourceAllowed for preemption at this round is :"
operator|+
name|totalPreemptedResourceAllowed
argument_list|)
expr_stmt|;
block|}
name|Resource
name|rollingUsedResourcePerUser
init|=
name|rollingResourceUsagePerUser
operator|.
name|get
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RMContainer
name|c
range|:
name|liveContainers
control|)
block|{
comment|// if there are no demand, return.
if|if
condition|(
name|resToObtainByPartition
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// skip preselected containers.
if|if
condition|(
name|CapacitySchedulerPreemptionUtils
operator|.
name|isContainerAlreadySelected
argument_list|(
name|c
argument_list|,
name|selectedCandidates
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Skip already marked to killable containers
if|if
condition|(
literal|null
operator|!=
name|preemptionContext
operator|.
name|getKillableContainers
argument_list|()
operator|&&
name|preemptionContext
operator|.
name|getKillableContainers
argument_list|()
operator|.
name|contains
argument_list|(
name|c
operator|.
name|getContainerId
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Skip AM Container from preemption for now.
if|if
condition|(
name|c
operator|.
name|isAMContainer
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// If selected container brings down resource usage under its user's
comment|// UserLimit (or equals to), we must skip such containers.
if|if
condition|(
name|fifoPreemptionComputePlugin
operator|.
name|skipContainerBasedOnIntraQueuePolicy
argument_list|(
name|app
argument_list|,
name|clusterResource
argument_list|,
name|rollingUsedResourcePerUser
argument_list|,
name|c
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping container: "
operator|+
name|c
operator|.
name|getContainerId
argument_list|()
operator|+
literal|" with resource:"
operator|+
name|c
operator|.
name|getAllocatedResource
argument_list|()
operator|+
literal|" as UserLimit for user:"
operator|+
name|app
operator|.
name|getUser
argument_list|()
operator|+
literal|" with resource usage: "
operator|+
name|rollingUsedResourcePerUser
operator|+
literal|" is going under UL"
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
comment|// Try to preempt this container
name|boolean
name|ret
init|=
name|CapacitySchedulerPreemptionUtils
operator|.
name|tryPreemptContainerAndDeductResToObtain
argument_list|(
name|rc
argument_list|,
name|preemptionContext
argument_list|,
name|resToObtainByPartition
argument_list|,
name|c
argument_list|,
name|clusterResource
argument_list|,
name|selectedCandidates
argument_list|,
name|totalPreemptedResourceAllowed
argument_list|)
decl_stmt|;
comment|// Subtract from respective user's resource usage once a container is
comment|// selected for preemption.
if|if
condition|(
name|ret
operator|&&
name|preemptionContext
operator|.
name|getIntraQueuePreemptionOrderPolicy
argument_list|()
operator|.
name|equals
argument_list|(
name|IntraQueuePreemptionOrderPolicy
operator|.
name|USERLIMIT_FIRST
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|rollingUsedResourcePerUser
argument_list|,
name|c
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|computeIntraQueuePreemptionDemand (Resource clusterResource, Resource totalPreemptedResourceAllowed, Map<ApplicationAttemptId, Set<RMContainer>> selectedCandidates)
specifier|private
name|void
name|computeIntraQueuePreemptionDemand
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|totalPreemptedResourceAllowed
parameter_list|,
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|Set
argument_list|<
name|RMContainer
argument_list|>
argument_list|>
name|selectedCandidates
parameter_list|)
block|{
comment|// 1. Iterate through all partition to calculate demand within a partition.
for|for
control|(
name|String
name|partition
range|:
name|context
operator|.
name|getAllPartitions
argument_list|()
control|)
block|{
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
name|queueNames
init|=
name|context
operator|.
name|getUnderServedQueuesPerPartition
argument_list|(
name|partition
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|queueNames
condition|)
block|{
continue|continue;
block|}
comment|// 2. loop through all queues corresponding to a partition.
for|for
control|(
name|String
name|queueName
range|:
name|queueNames
control|)
block|{
name|TempQueuePerPartition
name|tq
init|=
name|context
operator|.
name|getQueueByPartition
argument_list|(
name|queueName
argument_list|,
name|partition
argument_list|)
decl_stmt|;
name|LeafQueue
name|leafQueue
init|=
name|tq
operator|.
name|leafQueue
decl_stmt|;
comment|// skip if its parent queue
if|if
condition|(
literal|null
operator|==
name|leafQueue
condition|)
block|{
continue|continue;
block|}
comment|// 3. Consider reassignableResource as (used - actuallyToBePreempted).
comment|// This provides as upper limit to split apps quota in a queue.
name|Resource
name|queueReassignableResource
init|=
name|Resources
operator|.
name|subtract
argument_list|(
name|tq
operator|.
name|getUsed
argument_list|()
argument_list|,
name|tq
operator|.
name|getActuallyToBePreempted
argument_list|()
argument_list|)
decl_stmt|;
comment|// 4. Check queue's used capacity. Make sure that the used capacity is
comment|// above certain limit to consider for intra queue preemption.
if|if
condition|(
name|leafQueue
operator|.
name|getQueueCapacities
argument_list|()
operator|.
name|getUsedCapacity
argument_list|(
name|partition
argument_list|)
operator|<
name|context
operator|.
name|getMinimumThresholdForIntraQueuePreemption
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// 5. compute the allocation of all apps based on queue's unallocated
comment|// capacity
name|fifoPreemptionComputePlugin
operator|.
name|computeAppsIdealAllocation
argument_list|(
name|clusterResource
argument_list|,
name|tq
argument_list|,
name|selectedCandidates
argument_list|,
name|totalPreemptedResourceAllowed
argument_list|,
name|queueReassignableResource
argument_list|,
name|context
operator|.
name|getMaxAllowableLimitForIntraQueuePreemption
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

