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
name|util
operator|.
name|resource
operator|.
name|ResourceCalculator
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_class
DECL|class|CapacitySchedulerPreemptionUtils
specifier|public
class|class
name|CapacitySchedulerPreemptionUtils
block|{
DECL|method|getResToObtainByPartitionForLeafQueue ( CapacitySchedulerPreemptionContext context, String queueName, Resource clusterResource)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|getResToObtainByPartitionForLeafQueue
parameter_list|(
name|CapacitySchedulerPreemptionContext
name|context
parameter_list|,
name|String
name|queueName
parameter_list|,
name|Resource
name|clusterResource
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|resToObtainByPartition
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// compute resToObtainByPartition considered inter-queue preemption
for|for
control|(
name|TempQueuePerPartition
name|qT
range|:
name|context
operator|.
name|getQueuePartitions
argument_list|(
name|queueName
argument_list|)
control|)
block|{
if|if
condition|(
name|qT
operator|.
name|preemptionDisabled
condition|)
block|{
continue|continue;
block|}
comment|// Only add resToObtainByPartition when actuallyToBePreempted resource>=
comment|// 0
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|context
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|clusterResource
argument_list|,
name|qT
operator|.
name|getActuallyToBePreempted
argument_list|()
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
name|resToObtainByPartition
operator|.
name|put
argument_list|(
name|qT
operator|.
name|partition
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|qT
operator|.
name|getActuallyToBePreempted
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|resToObtainByPartition
return|;
block|}
DECL|method|isContainerAlreadySelected (RMContainer container, Map<ApplicationAttemptId, Set<RMContainer>> selectedCandidates)
specifier|public
specifier|static
name|boolean
name|isContainerAlreadySelected
parameter_list|(
name|RMContainer
name|container
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
if|if
condition|(
literal|null
operator|==
name|selectedCandidates
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Set
argument_list|<
name|RMContainer
argument_list|>
name|containers
init|=
name|selectedCandidates
operator|.
name|get
argument_list|(
name|container
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|containers
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|containers
operator|.
name|contains
argument_list|(
name|container
argument_list|)
return|;
block|}
DECL|method|deductPreemptableResourcesBasedSelectedCandidates ( CapacitySchedulerPreemptionContext context, Map<ApplicationAttemptId, Set<RMContainer>> selectedCandidates)
specifier|public
specifier|static
name|void
name|deductPreemptableResourcesBasedSelectedCandidates
parameter_list|(
name|CapacitySchedulerPreemptionContext
name|context
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
for|for
control|(
name|Set
argument_list|<
name|RMContainer
argument_list|>
name|containers
range|:
name|selectedCandidates
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|RMContainer
name|c
range|:
name|containers
control|)
block|{
name|SchedulerNode
name|schedulerNode
init|=
name|context
operator|.
name|getScheduler
argument_list|()
operator|.
name|getSchedulerNode
argument_list|(
name|c
operator|.
name|getAllocatedNode
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|schedulerNode
condition|)
block|{
continue|continue;
block|}
name|String
name|partition
init|=
name|schedulerNode
operator|.
name|getPartition
argument_list|()
decl_stmt|;
name|String
name|queue
init|=
name|c
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|TempQueuePerPartition
name|tq
init|=
name|context
operator|.
name|getQueueByPartition
argument_list|(
name|queue
argument_list|,
name|partition
argument_list|)
decl_stmt|;
name|Resource
name|res
init|=
name|c
operator|.
name|getReservedResource
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|res
condition|)
block|{
name|res
operator|=
name|c
operator|.
name|getAllocatedResource
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|res
condition|)
block|{
name|tq
operator|.
name|deductActuallyToBePreempted
argument_list|(
name|context
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|tq
operator|.
name|totalPartitionResource
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|TempAppPerPartition
argument_list|>
name|tas
init|=
name|tq
operator|.
name|getApps
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|tas
operator|||
name|tas
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|deductPreemptableResourcePerApp
argument_list|(
name|context
argument_list|,
name|tq
operator|.
name|totalPartitionResource
argument_list|,
name|tas
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|deductPreemptableResourcePerApp ( CapacitySchedulerPreemptionContext context, Resource totalPartitionResource, Collection<TempAppPerPartition> tas, Resource res)
specifier|private
specifier|static
name|void
name|deductPreemptableResourcePerApp
parameter_list|(
name|CapacitySchedulerPreemptionContext
name|context
parameter_list|,
name|Resource
name|totalPartitionResource
parameter_list|,
name|Collection
argument_list|<
name|TempAppPerPartition
argument_list|>
name|tas
parameter_list|,
name|Resource
name|res
parameter_list|)
block|{
for|for
control|(
name|TempAppPerPartition
name|ta
range|:
name|tas
control|)
block|{
name|ta
operator|.
name|deductActuallyToBePreempted
argument_list|(
name|context
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|totalPartitionResource
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Invoke this method to preempt container based on resToObtain.    *    * @param rc    *          resource calculator    * @param context    *          preemption context    * @param resourceToObtainByPartitions    *          map to hold resource to obtain per partition    * @param rmContainer    *          container    * @param clusterResource    *          total resource    * @param preemptMap    *          map to hold preempted containers    * @param totalPreemptionAllowed    *          total preemption allowed per round    * @return should we preempt rmContainer. If we should, deduct from    *<code>resourceToObtainByPartition</code>    */
DECL|method|tryPreemptContainerAndDeductResToObtain ( ResourceCalculator rc, CapacitySchedulerPreemptionContext context, Map<String, Resource> resourceToObtainByPartitions, RMContainer rmContainer, Resource clusterResource, Map<ApplicationAttemptId, Set<RMContainer>> preemptMap, Resource totalPreemptionAllowed)
specifier|public
specifier|static
name|boolean
name|tryPreemptContainerAndDeductResToObtain
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|CapacitySchedulerPreemptionContext
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|resourceToObtainByPartitions
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|,
name|Resource
name|clusterResource
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
name|preemptMap
parameter_list|,
name|Resource
name|totalPreemptionAllowed
parameter_list|)
block|{
name|ApplicationAttemptId
name|attemptId
init|=
name|rmContainer
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
comment|// We will not account resource of a container twice or more
if|if
condition|(
name|preemptMapContains
argument_list|(
name|preemptMap
argument_list|,
name|attemptId
argument_list|,
name|rmContainer
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|nodePartition
init|=
name|getPartitionByNodeId
argument_list|(
name|context
argument_list|,
name|rmContainer
operator|.
name|getAllocatedNode
argument_list|()
argument_list|)
decl_stmt|;
name|Resource
name|toObtainByPartition
init|=
name|resourceToObtainByPartitions
operator|.
name|get
argument_list|(
name|nodePartition
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|toObtainByPartition
operator|&&
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|toObtainByPartition
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
operator|&&
name|Resources
operator|.
name|fitsIn
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|rmContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|,
name|totalPreemptionAllowed
argument_list|)
operator|&&
operator|!
name|Resources
operator|.
name|isAnyMajorResourceZero
argument_list|(
name|rc
argument_list|,
name|toObtainByPartition
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|toObtainByPartition
argument_list|,
name|rmContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|totalPreemptionAllowed
argument_list|,
name|rmContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
comment|// When we have no more resource need to obtain, remove from map.
if|if
condition|(
name|Resources
operator|.
name|lessThanOrEqual
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|toObtainByPartition
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
name|resourceToObtainByPartitions
operator|.
name|remove
argument_list|(
name|nodePartition
argument_list|)
expr_stmt|;
block|}
comment|// Add to preemptMap
name|addToPreemptMap
argument_list|(
name|preemptMap
argument_list|,
name|attemptId
argument_list|,
name|rmContainer
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|getPartitionByNodeId ( CapacitySchedulerPreemptionContext context, NodeId nodeId)
specifier|private
specifier|static
name|String
name|getPartitionByNodeId
parameter_list|(
name|CapacitySchedulerPreemptionContext
name|context
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
block|{
return|return
name|context
operator|.
name|getScheduler
argument_list|()
operator|.
name|getSchedulerNode
argument_list|(
name|nodeId
argument_list|)
operator|.
name|getPartition
argument_list|()
return|;
block|}
DECL|method|addToPreemptMap ( Map<ApplicationAttemptId, Set<RMContainer>> preemptMap, ApplicationAttemptId appAttemptId, RMContainer containerToPreempt)
specifier|private
specifier|static
name|void
name|addToPreemptMap
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
name|preemptMap
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|RMContainer
name|containerToPreempt
parameter_list|)
block|{
name|Set
argument_list|<
name|RMContainer
argument_list|>
name|set
init|=
name|preemptMap
operator|.
name|get
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|set
condition|)
block|{
name|set
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|preemptMap
operator|.
name|put
argument_list|(
name|appAttemptId
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
name|set
operator|.
name|add
argument_list|(
name|containerToPreempt
argument_list|)
expr_stmt|;
block|}
DECL|method|preemptMapContains ( Map<ApplicationAttemptId, Set<RMContainer>> preemptMap, ApplicationAttemptId attemptId, RMContainer rmContainer)
specifier|private
specifier|static
name|boolean
name|preemptMapContains
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
name|preemptMap
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|)
block|{
name|Set
argument_list|<
name|RMContainer
argument_list|>
name|rmContainers
init|=
name|preemptMap
operator|.
name|get
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rmContainers
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|rmContainers
operator|.
name|contains
argument_list|(
name|rmContainer
argument_list|)
return|;
block|}
block|}
end_class

end_unit

