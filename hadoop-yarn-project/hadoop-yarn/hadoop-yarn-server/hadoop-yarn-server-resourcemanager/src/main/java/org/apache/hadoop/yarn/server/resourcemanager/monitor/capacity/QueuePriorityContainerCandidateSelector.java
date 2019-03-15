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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|HashBasedTable
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
name|collect
operator|.
name|Table
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
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|api
operator|.
name|records
operator|.
name|ResourceRequest
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
name|CapacitySchedulerConfiguration
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
name|FiCaSchedulerNode
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
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

begin_class
DECL|class|QueuePriorityContainerCandidateSelector
specifier|public
class|class
name|QueuePriorityContainerCandidateSelector
extends|extends
name|PreemptionCandidatesSelector
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|QueuePriorityContainerCandidateSelector
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Configured timeout before doing reserved container preemption
DECL|field|minTimeout
specifier|private
name|long
name|minTimeout
decl_stmt|;
comment|// Allow move reservation around for better placement?
DECL|field|allowMoveReservation
specifier|private
name|boolean
name|allowMoveReservation
decl_stmt|;
comment|// All the reserved containers of the system which could possible preempt from
comment|// queue with lower priorities
DECL|field|reservedContainers
specifier|private
name|List
argument_list|<
name|RMContainer
argument_list|>
name|reservedContainers
decl_stmt|;
comment|// From -> To
comment|// A digraph to represent if one queue has higher priority than another.
comment|// For example, a->b means queue=a has higher priority than queue=b
DECL|field|priorityDigraph
specifier|private
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|,
name|Boolean
argument_list|>
name|priorityDigraph
init|=
name|HashBasedTable
operator|.
name|create
argument_list|()
decl_stmt|;
DECL|field|clusterResource
specifier|private
name|Resource
name|clusterResource
decl_stmt|;
DECL|field|selectedCandidates
specifier|private
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
decl_stmt|;
DECL|field|totalPreemptionAllowed
specifier|private
name|Resource
name|totalPreemptionAllowed
decl_stmt|;
comment|// A cached scheduler node map, will be refreshed each round.
DECL|field|tempSchedulerNodeMap
specifier|private
name|Map
argument_list|<
name|NodeId
argument_list|,
name|TempSchedulerNode
argument_list|>
name|tempSchedulerNodeMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Have we touched (make any changes to the node) for this round
comment|// Once a node is touched, we will not try to move reservations to the node
DECL|field|touchedNodes
specifier|private
name|Set
argument_list|<
name|NodeId
argument_list|>
name|touchedNodes
decl_stmt|;
comment|// Resource which marked to preempt from other queues.
comment|//<Queue, Partition, Resource-marked-to-be-preempted-from-other-queue>
DECL|field|toPreemptedFromOtherQueues
specifier|private
name|Table
argument_list|<
name|String
argument_list|,
name|String
argument_list|,
name|Resource
argument_list|>
name|toPreemptedFromOtherQueues
init|=
name|HashBasedTable
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Comparator
argument_list|<
name|RMContainer
argument_list|>
DECL|field|CONTAINER_CREATION_TIME_COMPARATOR
name|CONTAINER_CREATION_TIME_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|RMContainer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|RMContainer
name|o1
parameter_list|,
name|RMContainer
name|o2
parameter_list|)
block|{
if|if
condition|(
name|preemptionAllowed
argument_list|(
name|o1
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|o2
operator|.
name|getQueueName
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|preemptionAllowed
argument_list|(
name|o2
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|o1
operator|.
name|getQueueName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
comment|// If two queues cannot preempt each other, compare creation time.
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getCreationTime
argument_list|()
argument_list|,
name|o2
operator|.
name|getCreationTime
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|QueuePriorityContainerCandidateSelector ( CapacitySchedulerPreemptionContext preemptionContext)
name|QueuePriorityContainerCandidateSelector
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
comment|// Initialize parameters
name|CapacitySchedulerConfiguration
name|csc
init|=
name|preemptionContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|minTimeout
operator|=
name|csc
operator|.
name|getPUOrderingPolicyUnderUtilizedPreemptionDelay
argument_list|()
expr_stmt|;
name|allowMoveReservation
operator|=
name|csc
operator|.
name|getPUOrderingPolicyUnderUtilizedPreemptionMoveReservation
argument_list|()
expr_stmt|;
block|}
DECL|method|getPathToRoot (TempQueuePerPartition tq)
specifier|private
name|List
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|getPathToRoot
parameter_list|(
name|TempQueuePerPartition
name|tq
parameter_list|)
block|{
name|List
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|tq
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|tq
argument_list|)
expr_stmt|;
name|tq
operator|=
name|tq
operator|.
name|parent
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|initializePriorityDigraph ()
specifier|private
name|void
name|initializePriorityDigraph
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing priority preemption directed graph:"
argument_list|)
expr_stmt|;
comment|// Make sure we iterate all leaf queue combinations
for|for
control|(
name|String
name|q1
range|:
name|preemptionContext
operator|.
name|getLeafQueueNames
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|q2
range|:
name|preemptionContext
operator|.
name|getLeafQueueNames
argument_list|()
control|)
block|{
comment|// Make sure we only calculate each combination once instead of all
comment|// permutations
if|if
condition|(
name|q1
operator|.
name|compareTo
argument_list|(
name|q2
argument_list|)
operator|<
literal|0
condition|)
block|{
name|TempQueuePerPartition
name|tq1
init|=
name|preemptionContext
operator|.
name|getQueueByPartition
argument_list|(
name|q1
argument_list|,
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
decl_stmt|;
name|TempQueuePerPartition
name|tq2
init|=
name|preemptionContext
operator|.
name|getQueueByPartition
argument_list|(
name|q2
argument_list|,
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|path1
init|=
name|getPathToRoot
argument_list|(
name|tq1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|path2
init|=
name|getPathToRoot
argument_list|(
name|tq2
argument_list|)
decl_stmt|;
comment|// Get direct ancestor below LCA (Lowest common ancestor)
name|int
name|i
init|=
name|path1
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
name|int
name|j
init|=
name|path2
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|path1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|queueName
operator|.
name|equals
argument_list|(
name|path2
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|queueName
argument_list|)
condition|)
block|{
name|i
operator|--
expr_stmt|;
name|j
operator|--
expr_stmt|;
block|}
comment|// compare priority of path1[i] and path2[j]
name|int
name|p1
init|=
name|path1
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|relativePriority
decl_stmt|;
name|int
name|p2
init|=
name|path2
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|relativePriority
decl_stmt|;
if|if
condition|(
name|p1
operator|<
name|p2
condition|)
block|{
name|priorityDigraph
operator|.
name|put
argument_list|(
name|q2
argument_list|,
name|q1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"- Added priority ordering edge: {}>> {}"
argument_list|,
name|q2
argument_list|,
name|q1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|p2
operator|<
name|p1
condition|)
block|{
name|priorityDigraph
operator|.
name|put
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"- Added priority ordering edge: {}>> {}"
argument_list|,
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Do we allow demandingQueue preempt resource from toBePreemptedQueue    *    * @param demandingQueue demandingQueue    * @param toBePreemptedQueue toBePreemptedQueue    * @return can/cannot    */
DECL|method|preemptionAllowed (String demandingQueue, String toBePreemptedQueue)
specifier|private
name|boolean
name|preemptionAllowed
parameter_list|(
name|String
name|demandingQueue
parameter_list|,
name|String
name|toBePreemptedQueue
parameter_list|)
block|{
return|return
name|priorityDigraph
operator|.
name|contains
argument_list|(
name|demandingQueue
argument_list|,
name|toBePreemptedQueue
argument_list|)
return|;
block|}
comment|/**    * Can we preempt enough resource for given:    *    * @param requiredResource askedResource    * @param demandingQueue demandingQueue    * @param schedulerNode node    * @param lookingForNewReservationPlacement Are we trying to look for move    *        reservation to the node    * @param newlySelectedContainers newly selected containers, will be set when    *        we can preempt enough resources from the node.    *    * @return can/cannot    */
DECL|method|canPreemptEnoughResourceForAsked (Resource requiredResource, String demandingQueue, FiCaSchedulerNode schedulerNode, boolean lookingForNewReservationPlacement, List<RMContainer> newlySelectedContainers)
specifier|private
name|boolean
name|canPreemptEnoughResourceForAsked
parameter_list|(
name|Resource
name|requiredResource
parameter_list|,
name|String
name|demandingQueue
parameter_list|,
name|FiCaSchedulerNode
name|schedulerNode
parameter_list|,
name|boolean
name|lookingForNewReservationPlacement
parameter_list|,
name|List
argument_list|<
name|RMContainer
argument_list|>
name|newlySelectedContainers
parameter_list|)
block|{
comment|// Do not check touched nodes again.
if|if
condition|(
name|touchedNodes
operator|.
name|contains
argument_list|(
name|schedulerNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TempSchedulerNode
name|node
init|=
name|tempSchedulerNodeMap
operator|.
name|get
argument_list|(
name|schedulerNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|node
condition|)
block|{
name|node
operator|=
name|TempSchedulerNode
operator|.
name|fromSchedulerNode
argument_list|(
name|schedulerNode
argument_list|)
expr_stmt|;
name|tempSchedulerNodeMap
operator|.
name|put
argument_list|(
name|schedulerNode
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|schedulerNode
operator|.
name|getReservedContainer
argument_list|()
operator|&&
name|lookingForNewReservationPlacement
condition|)
block|{
comment|// Node reserved by the others, skip this node
comment|// We will not try to move the reservation to node which reserved already.
return|return
literal|false
return|;
block|}
comment|// Need to preemption = asked - (node.total - node.allocated)
name|Resource
name|lacking
init|=
name|Resources
operator|.
name|subtract
argument_list|(
name|requiredResource
argument_list|,
name|Resources
operator|.
name|subtract
argument_list|(
name|node
operator|.
name|getTotalResource
argument_list|()
argument_list|,
name|node
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// On each host, simply check if we could preempt containers from
comment|// lower-prioritized queues or not
name|List
argument_list|<
name|RMContainer
argument_list|>
name|runningContainers
init|=
name|node
operator|.
name|getRunningContainers
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|runningContainers
argument_list|,
name|CONTAINER_CREATION_TIME_COMPARATOR
argument_list|)
expr_stmt|;
comment|// First of all, consider already selected containers
for|for
control|(
name|RMContainer
name|runningContainer
range|:
name|runningContainers
control|)
block|{
if|if
condition|(
name|CapacitySchedulerPreemptionUtils
operator|.
name|isContainerAlreadySelected
argument_list|(
name|runningContainer
argument_list|,
name|selectedCandidates
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|lacking
argument_list|,
name|runningContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If we already can allocate the reserved container after preemption,
comment|// skip following steps
if|if
condition|(
name|Resources
operator|.
name|fitsIn
argument_list|(
name|rc
argument_list|,
name|lacking
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Resource
name|allowed
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|totalPreemptionAllowed
argument_list|)
decl_stmt|;
name|Resource
name|selected
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|RMContainer
name|runningContainer
range|:
name|runningContainers
control|)
block|{
if|if
condition|(
name|CapacitySchedulerPreemptionUtils
operator|.
name|isContainerAlreadySelected
argument_list|(
name|runningContainer
argument_list|,
name|selectedCandidates
argument_list|)
condition|)
block|{
comment|// ignore selected containers
continue|continue;
block|}
comment|// Only preempt resource from queue with lower priority
if|if
condition|(
operator|!
name|preemptionAllowed
argument_list|(
name|demandingQueue
argument_list|,
name|runningContainer
operator|.
name|getQueueName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Don't preempt AM container
if|if
condition|(
name|runningContainer
operator|.
name|isAMContainer
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// Not allow to preempt more than limit
if|if
condition|(
name|Resources
operator|.
name|greaterThanOrEqual
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|allowed
argument_list|,
name|runningContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|allowed
argument_list|,
name|runningContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|lacking
argument_list|,
name|runningContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|selected
argument_list|,
name|runningContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|newlySelectedContainers
condition|)
block|{
name|newlySelectedContainers
operator|.
name|add
argument_list|(
name|runningContainer
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Lacking<= 0 means we can allocate the reserved container
if|if
condition|(
name|Resources
operator|.
name|fitsIn
argument_list|(
name|rc
argument_list|,
name|lacking
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|preChecksForMovingReservedContainerToNode ( RMContainer reservedContainer, FiCaSchedulerNode newNode)
specifier|private
name|boolean
name|preChecksForMovingReservedContainerToNode
parameter_list|(
name|RMContainer
name|reservedContainer
parameter_list|,
name|FiCaSchedulerNode
name|newNode
parameter_list|)
block|{
comment|// Don't do this if it has hard-locality preferences
if|if
condition|(
name|reservedContainer
operator|.
name|getReservedSchedulerKey
argument_list|()
operator|.
name|getContainerToUpdate
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// This means a container update request (like increase / promote)
return|return
literal|false
return|;
block|}
comment|// For normal requests
name|FiCaSchedulerApp
name|app
init|=
name|preemptionContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getApplicationAttempt
argument_list|(
name|reservedContainer
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|app
operator|.
name|getAppSchedulingInfo
argument_list|()
operator|.
name|canDelayTo
argument_list|(
name|reservedContainer
operator|.
name|getAllocatedSchedulerKey
argument_list|()
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|)
condition|)
block|{
comment|// This is a hard locality request
return|return
literal|false
return|;
block|}
comment|// Check if newNode's partition matches requested partition
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|equals
argument_list|(
name|reservedContainer
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|,
name|newNode
operator|.
name|getPartition
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|tryToMakeBetterReservationPlacement ( RMContainer reservedContainer, List<FiCaSchedulerNode> allSchedulerNodes)
specifier|private
name|void
name|tryToMakeBetterReservationPlacement
parameter_list|(
name|RMContainer
name|reservedContainer
parameter_list|,
name|List
argument_list|<
name|FiCaSchedulerNode
argument_list|>
name|allSchedulerNodes
parameter_list|)
block|{
for|for
control|(
name|FiCaSchedulerNode
name|targetNode
range|:
name|allSchedulerNodes
control|)
block|{
comment|// Precheck if we can move the rmContainer to the new targetNode
if|if
condition|(
operator|!
name|preChecksForMovingReservedContainerToNode
argument_list|(
name|reservedContainer
argument_list|,
name|targetNode
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|canPreemptEnoughResourceForAsked
argument_list|(
name|reservedContainer
operator|.
name|getReservedResource
argument_list|()
argument_list|,
name|reservedContainer
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|targetNode
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|NodeId
name|fromNode
init|=
name|reservedContainer
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
comment|// We can place container to this targetNode, so just go ahead and notify
comment|// scheduler
if|if
condition|(
name|preemptionContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|moveReservedContainer
argument_list|(
name|reservedContainer
argument_list|,
name|targetNode
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully moved reserved container="
operator|+
name|reservedContainer
operator|.
name|getContainerId
argument_list|()
operator|+
literal|" from targetNode="
operator|+
name|fromNode
operator|+
literal|" to targetNode="
operator|+
name|targetNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|touchedNodes
operator|.
name|add
argument_list|(
name|targetNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Do we allow the demanding queue preempt resource from other queues?    * A satisfied queue is not allowed to preempt resource from other queues.    * @param demandingQueue    * @return allowed/not    */
DECL|method|isQueueSatisfied (String demandingQueue, String partition)
specifier|private
name|boolean
name|isQueueSatisfied
parameter_list|(
name|String
name|demandingQueue
parameter_list|,
name|String
name|partition
parameter_list|)
block|{
name|TempQueuePerPartition
name|tq
init|=
name|preemptionContext
operator|.
name|getQueueByPartition
argument_list|(
name|demandingQueue
argument_list|,
name|partition
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|tq
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Resource
name|guaranteed
init|=
name|tq
operator|.
name|getGuaranteed
argument_list|()
decl_stmt|;
name|Resource
name|usedDeductReservd
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
name|getReserved
argument_list|()
argument_list|)
decl_stmt|;
name|Resource
name|markedToPreemptFromOtherQueue
init|=
name|toPreemptedFromOtherQueues
operator|.
name|get
argument_list|(
name|demandingQueue
argument_list|,
name|partition
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|markedToPreemptFromOtherQueue
condition|)
block|{
name|markedToPreemptFromOtherQueue
operator|=
name|Resources
operator|.
name|none
argument_list|()
expr_stmt|;
block|}
comment|// return Used - reserved + to-preempt-from-other-queue>= guaranteed
name|boolean
name|flag
init|=
name|Resources
operator|.
name|greaterThanOrEqual
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|Resources
operator|.
name|add
argument_list|(
name|usedDeductReservd
argument_list|,
name|markedToPreemptFromOtherQueue
argument_list|)
argument_list|,
name|guaranteed
argument_list|)
decl_stmt|;
return|return
name|flag
return|;
block|}
DECL|method|incToPreempt (String queue, String partition, Resource allocated)
specifier|private
name|void
name|incToPreempt
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|partition
parameter_list|,
name|Resource
name|allocated
parameter_list|)
block|{
name|Resource
name|total
init|=
name|toPreemptedFromOtherQueues
operator|.
name|get
argument_list|(
name|queue
argument_list|,
name|partition
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|total
condition|)
block|{
name|total
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|toPreemptedFromOtherQueues
operator|.
name|put
argument_list|(
name|queue
argument_list|,
name|partition
argument_list|,
name|total
argument_list|)
expr_stmt|;
block|}
name|Resources
operator|.
name|addTo
argument_list|(
name|total
argument_list|,
name|allocated
argument_list|)
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
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|Set
argument_list|<
name|RMContainer
argument_list|>
argument_list|>
name|curCandidates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Initialize digraph from queues
comment|// TODO (wangda): only do this when queue refreshed.
name|priorityDigraph
operator|.
name|clear
argument_list|()
expr_stmt|;
name|initializePriorityDigraph
argument_list|()
expr_stmt|;
comment|// When all queues are set to same priority, or priority is not respected,
comment|// direct return.
if|if
condition|(
name|priorityDigraph
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|curCandidates
return|;
block|}
comment|// Save parameters to be shared by other methods
name|this
operator|.
name|selectedCandidates
operator|=
name|selectedCandidates
expr_stmt|;
name|this
operator|.
name|clusterResource
operator|=
name|clusterResource
expr_stmt|;
name|this
operator|.
name|totalPreemptionAllowed
operator|=
name|totalPreemptedResourceAllowed
expr_stmt|;
name|toPreemptedFromOtherQueues
operator|.
name|clear
argument_list|()
expr_stmt|;
name|reservedContainers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
comment|// Clear temp-scheduler-node-map every time when doing selection of
comment|// containers.
name|tempSchedulerNodeMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|touchedNodes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
comment|// Add all reserved containers for analysis
name|List
argument_list|<
name|FiCaSchedulerNode
argument_list|>
name|allSchedulerNodes
init|=
name|preemptionContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getAllNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|FiCaSchedulerNode
name|node
range|:
name|allSchedulerNodes
control|)
block|{
name|RMContainer
name|reservedContainer
init|=
name|node
operator|.
name|getReservedContainer
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|reservedContainer
condition|)
block|{
comment|// Add to reservedContainers list if the queue that the reserved
comment|// container belongs to has high priority than at least one queue
if|if
condition|(
name|priorityDigraph
operator|.
name|containsRow
argument_list|(
name|reservedContainer
operator|.
name|getQueueName
argument_list|()
argument_list|)
condition|)
block|{
name|reservedContainers
operator|.
name|add
argument_list|(
name|reservedContainer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Sort reserved container by creation time
name|Collections
operator|.
name|sort
argument_list|(
name|reservedContainers
argument_list|,
name|CONTAINER_CREATION_TIME_COMPARATOR
argument_list|)
expr_stmt|;
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// From the beginning of the list
for|for
control|(
name|RMContainer
name|reservedContainer
range|:
name|reservedContainers
control|)
block|{
comment|// Only try to preempt reserved container after reserved container created
comment|// and cannot be allocated after minTimeout
if|if
condition|(
name|currentTime
operator|-
name|reservedContainer
operator|.
name|getCreationTime
argument_list|()
operator|<
name|minTimeout
condition|)
block|{
continue|continue;
block|}
name|FiCaSchedulerNode
name|node
init|=
name|preemptionContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getNode
argument_list|(
name|reservedContainer
operator|.
name|getReservedNode
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|node
condition|)
block|{
comment|// Something is wrong, ignore
continue|continue;
block|}
name|List
argument_list|<
name|RMContainer
argument_list|>
name|newlySelectedToBePreemptContainers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Check if we can preempt for this queue
comment|// We will skip if the demanding queue is already satisfied.
name|String
name|demandingQueueName
init|=
name|reservedContainer
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|boolean
name|demandingQueueSatisfied
init|=
name|isQueueSatisfied
argument_list|(
name|demandingQueueName
argument_list|,
name|node
operator|.
name|getPartition
argument_list|()
argument_list|)
decl_stmt|;
comment|// We will continue check if it is possible to preempt reserved container
comment|// from the node.
name|boolean
name|canPreempt
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|demandingQueueSatisfied
condition|)
block|{
name|canPreempt
operator|=
name|canPreemptEnoughResourceForAsked
argument_list|(
name|reservedContainer
operator|.
name|getReservedResource
argument_list|()
argument_list|,
name|demandingQueueName
argument_list|,
name|node
argument_list|,
literal|false
argument_list|,
name|newlySelectedToBePreemptContainers
argument_list|)
expr_stmt|;
block|}
comment|// Add selected container if we can allocate reserved container by
comment|// preemption others
if|if
condition|(
name|canPreempt
condition|)
block|{
name|touchedNodes
operator|.
name|add
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Trying to preempt following containers to make reserved "
operator|+
literal|"container={} on node={} can be allocated:"
argument_list|,
name|reservedContainer
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update to-be-preempt
name|incToPreempt
argument_list|(
name|demandingQueueName
argument_list|,
name|node
operator|.
name|getPartition
argument_list|()
argument_list|,
name|reservedContainer
operator|.
name|getReservedResource
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RMContainer
name|c
range|:
name|newlySelectedToBePreemptContainers
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|" --container={} resource={}"
argument_list|,
name|c
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|c
operator|.
name|getReservedResource
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add to preemptMap
name|CapacitySchedulerPreemptionUtils
operator|.
name|addToPreemptMap
argument_list|(
name|selectedCandidates
argument_list|,
name|curCandidates
argument_list|,
name|c
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
comment|// Update totalPreemptionResourceAllowed
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|totalPreemptedResourceAllowed
argument_list|,
name|c
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|demandingQueueSatisfied
condition|)
block|{
comment|// We failed to get enough resource to allocate the container
comment|// This typically happens when the reserved node is proper, will
comment|// try to see if we can reserve the container on a better host.
comment|// Only do this if the demanding queue is not satisfied.
comment|//
comment|// TODO (wangda): do more tests before making it usable
comment|//
if|if
condition|(
name|allowMoveReservation
condition|)
block|{
name|tryToMakeBetterReservationPlacement
argument_list|(
name|reservedContainer
argument_list|,
name|allSchedulerNodes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|curCandidates
return|;
block|}
block|}
end_class

end_unit

