begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.allocator
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
name|scheduler
operator|.
name|capacity
operator|.
name|allocator
package|;
end_package

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
name|Iterator
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
name|Map
operator|.
name|Entry
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
name|ContainerState
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
name|RMContext
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
name|AppSchedulingInfo
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
name|NodeType
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
name|ResourceLimits
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
name|SchedContainerChangeRequest
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
name|capacity
operator|.
name|CSAssignment
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
name|SchedulingMode
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

begin_class
DECL|class|IncreaseContainerAllocator
specifier|public
class|class
name|IncreaseContainerAllocator
extends|extends
name|AbstractContainerAllocator
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
name|IncreaseContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|IncreaseContainerAllocator (FiCaSchedulerApp application, ResourceCalculator rc, RMContext rmContext)
specifier|public
name|IncreaseContainerAllocator
parameter_list|(
name|FiCaSchedulerApp
name|application
parameter_list|,
name|ResourceCalculator
name|rc
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|super
argument_list|(
name|application
argument_list|,
name|rc
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * Quick check if we can allocate anything here:    * We will not continue if:     * - Headroom doesn't support allocate minimumAllocation    * -     */
DECL|method|checkHeadroom (Resource clusterResource, ResourceLimits currentResourceLimits, Resource required)
specifier|private
name|boolean
name|checkHeadroom
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|ResourceLimits
name|currentResourceLimits
parameter_list|,
name|Resource
name|required
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|greaterThanOrEqual
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|currentResourceLimits
operator|.
name|getHeadroom
argument_list|()
argument_list|,
name|required
argument_list|)
return|;
block|}
DECL|method|createReservedIncreasedCSAssignment ( SchedContainerChangeRequest request)
specifier|private
name|CSAssignment
name|createReservedIncreasedCSAssignment
parameter_list|(
name|SchedContainerChangeRequest
name|request
parameter_list|)
block|{
name|CSAssignment
name|assignment
init|=
operator|new
name|CSAssignment
argument_list|(
name|request
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|,
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
literal|null
argument_list|,
name|application
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|getReserved
argument_list|()
argument_list|,
name|request
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|incrReservations
argument_list|()
expr_stmt|;
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|addReservationDetails
argument_list|(
name|request
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|application
operator|.
name|getCSLeafQueue
argument_list|()
operator|.
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|setIncreasedAllocation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reserved increase container request:"
operator|+
name|request
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|assignment
return|;
block|}
DECL|method|createSuccessfullyIncreasedCSAssignment ( SchedContainerChangeRequest request, boolean fromReservation)
specifier|private
name|CSAssignment
name|createSuccessfullyIncreasedCSAssignment
parameter_list|(
name|SchedContainerChangeRequest
name|request
parameter_list|,
name|boolean
name|fromReservation
parameter_list|)
block|{
name|CSAssignment
name|assignment
init|=
operator|new
name|CSAssignment
argument_list|(
name|request
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|,
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
literal|null
argument_list|,
name|application
argument_list|,
literal|false
argument_list|,
name|fromReservation
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|getAllocated
argument_list|()
argument_list|,
name|request
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|incrAllocations
argument_list|()
expr_stmt|;
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|addAllocationDetails
argument_list|(
name|request
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|application
operator|.
name|getCSLeafQueue
argument_list|()
operator|.
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|setIncreasedAllocation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// notify application
name|application
operator|.
name|getCSLeafQueue
argument_list|()
operator|.
name|getOrderingPolicy
argument_list|()
operator|.
name|containerAllocated
argument_list|(
name|application
argument_list|,
name|application
operator|.
name|getRMContainer
argument_list|(
name|request
operator|.
name|getContainerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Approved increase container request:"
operator|+
name|request
operator|.
name|toString
argument_list|()
operator|+
literal|" fromReservation="
operator|+
name|fromReservation
argument_list|)
expr_stmt|;
return|return
name|assignment
return|;
block|}
DECL|method|allocateIncreaseRequestFromReservedContainer ( SchedulerNode node, Resource cluster, SchedContainerChangeRequest increaseRequest)
specifier|private
name|CSAssignment
name|allocateIncreaseRequestFromReservedContainer
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|Resource
name|cluster
parameter_list|,
name|SchedContainerChangeRequest
name|increaseRequest
parameter_list|)
block|{
if|if
condition|(
name|Resources
operator|.
name|fitsIn
argument_list|(
name|rc
argument_list|,
name|cluster
argument_list|,
name|increaseRequest
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|,
name|node
operator|.
name|getUnallocatedResource
argument_list|()
argument_list|)
condition|)
block|{
comment|// OK, we can allocate this increase request
comment|// Unreserve it first
name|application
operator|.
name|unreserve
argument_list|(
name|increaseRequest
operator|.
name|getPriority
argument_list|()
argument_list|,
operator|(
name|FiCaSchedulerNode
operator|)
name|node
argument_list|,
name|increaseRequest
operator|.
name|getRMContainer
argument_list|()
argument_list|)
expr_stmt|;
comment|// Notify application
name|application
operator|.
name|increaseContainer
argument_list|(
name|increaseRequest
argument_list|)
expr_stmt|;
comment|// Notify node
name|node
operator|.
name|increaseContainer
argument_list|(
name|increaseRequest
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|increaseRequest
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|createSuccessfullyIncreasedCSAssignment
argument_list|(
name|increaseRequest
argument_list|,
literal|true
argument_list|)
return|;
block|}
else|else
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
literal|"Failed to allocate reserved increase request:"
operator|+
name|increaseRequest
operator|.
name|toString
argument_list|()
operator|+
literal|". There's no enough available resource"
argument_list|)
expr_stmt|;
block|}
comment|// We still cannot allocate this container, will wait for next turn
return|return
name|CSAssignment
operator|.
name|SKIP_ASSIGNMENT
return|;
block|}
block|}
DECL|method|allocateIncreaseRequest (FiCaSchedulerNode node, Resource cluster, SchedContainerChangeRequest increaseRequest)
specifier|private
name|CSAssignment
name|allocateIncreaseRequest
parameter_list|(
name|FiCaSchedulerNode
name|node
parameter_list|,
name|Resource
name|cluster
parameter_list|,
name|SchedContainerChangeRequest
name|increaseRequest
parameter_list|)
block|{
if|if
condition|(
name|Resources
operator|.
name|fitsIn
argument_list|(
name|rc
argument_list|,
name|cluster
argument_list|,
name|increaseRequest
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|,
name|node
operator|.
name|getUnallocatedResource
argument_list|()
argument_list|)
condition|)
block|{
comment|// Notify node
name|node
operator|.
name|increaseContainer
argument_list|(
name|increaseRequest
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|increaseRequest
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|)
expr_stmt|;
comment|// OK, we can allocate this increase request
comment|// Notify application
name|application
operator|.
name|increaseContainer
argument_list|(
name|increaseRequest
argument_list|)
expr_stmt|;
return|return
name|createSuccessfullyIncreasedCSAssignment
argument_list|(
name|increaseRequest
argument_list|,
literal|false
argument_list|)
return|;
block|}
else|else
block|{
name|boolean
name|reservationSucceeded
init|=
name|application
operator|.
name|reserveIncreasedContainer
argument_list|(
name|increaseRequest
operator|.
name|getPriority
argument_list|()
argument_list|,
name|node
argument_list|,
name|increaseRequest
operator|.
name|getRMContainer
argument_list|()
argument_list|,
name|increaseRequest
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|reservationSucceeded
condition|)
block|{
comment|// We cannot allocate this container, but since queue capacity /
comment|// user-limit matches, we can reserve this container on this node.
return|return
name|createReservedIncreasedCSAssignment
argument_list|(
name|increaseRequest
argument_list|)
return|;
block|}
else|else
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
literal|"Reserve increase request="
operator|+
name|increaseRequest
operator|.
name|toString
argument_list|()
operator|+
literal|" failed. Skipping.."
argument_list|)
expr_stmt|;
block|}
return|return
name|CSAssignment
operator|.
name|SKIP_ASSIGNMENT
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|assignContainers (Resource clusterResource, FiCaSchedulerNode node, SchedulingMode schedulingMode, ResourceLimits resourceLimits, RMContainer reservedContainer)
specifier|public
name|CSAssignment
name|assignContainers
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|,
name|ResourceLimits
name|resourceLimits
parameter_list|,
name|RMContainer
name|reservedContainer
parameter_list|)
block|{
name|AppSchedulingInfo
name|sinfo
init|=
name|application
operator|.
name|getAppSchedulingInfo
argument_list|()
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|node
operator|.
name|getNodeID
argument_list|()
decl_stmt|;
if|if
condition|(
name|reservedContainer
operator|==
literal|null
condition|)
block|{
comment|// Do we have increase request on this node?
if|if
condition|(
operator|!
name|sinfo
operator|.
name|hasIncreaseRequest
argument_list|(
name|nodeId
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
literal|"Skip allocating increase request since we don't have any"
operator|+
literal|" increase request on this node="
operator|+
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|CSAssignment
operator|.
name|SKIP_ASSIGNMENT
return|;
block|}
comment|// Check if we need to unreserve something, note that we don't support
comment|// continuousReservationLooking now. TODO, need think more about how to
comment|// support it.
name|boolean
name|shouldUnreserve
init|=
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|resourceLimits
operator|.
name|getAmountNeededUnreserve
argument_list|()
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
decl_stmt|;
comment|// Check if we can allocate minimum resource according to headroom
name|boolean
name|cannotAllocateAnything
init|=
operator|!
name|checkHeadroom
argument_list|(
name|clusterResource
argument_list|,
name|resourceLimits
argument_list|,
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|)
decl_stmt|;
comment|// Skip the app if we failed either of above check
if|if
condition|(
name|cannotAllocateAnything
operator|||
name|shouldUnreserve
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
if|if
condition|(
name|shouldUnreserve
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cannot continue since we have to unreserve some resource"
operator|+
literal|", now increase container allocation doesn't "
operator|+
literal|"support continuous reservation looking.."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cannotAllocateAnything
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"We cannot allocate anything because of low headroom, "
operator|+
literal|"headroom="
operator|+
name|resourceLimits
operator|.
name|getHeadroom
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|CSAssignment
operator|.
name|SKIP_ASSIGNMENT
return|;
block|}
name|CSAssignment
name|assigned
init|=
literal|null
decl_stmt|;
comment|/*        * Loop each priority, and containerId. Container priority is not        * equivalent to request priority, application master can run an important        * task on a less prioritized container.        *         * So behavior here is, we still try to increase container with higher        * priority, but will skip increase request and move to next increase        * request if queue-limit or user-limit aren't satisfied         */
for|for
control|(
name|Priority
name|priority
range|:
name|application
operator|.
name|getPriorities
argument_list|()
control|)
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
literal|"Looking at increase request for application="
operator|+
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
operator|+
literal|" priority="
operator|+
name|priority
argument_list|)
expr_stmt|;
block|}
comment|/*          * If we have multiple to-be-increased containers under same priority on          * a same host, we will try to increase earlier launched container          * first. And again - we will skip a request and move to next if it          * cannot be allocated.          */
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|SchedContainerChangeRequest
argument_list|>
name|increaseRequestMap
init|=
name|sinfo
operator|.
name|getIncreaseRequests
argument_list|(
name|nodeId
argument_list|,
name|priority
argument_list|)
decl_stmt|;
comment|// We don't have more increase request on this priority, skip..
if|if
condition|(
literal|null
operator|==
name|increaseRequestMap
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
literal|"There's no increase request for "
operator|+
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
operator|+
literal|" priority="
operator|+
name|priority
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|SchedContainerChangeRequest
argument_list|>
argument_list|>
name|iter
init|=
name|increaseRequestMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SchedContainerChangeRequest
argument_list|>
name|toBeRemovedRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|SchedContainerChangeRequest
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|SchedContainerChangeRequest
name|increaseRequest
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
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
literal|"Looking at increase request="
operator|+
name|increaseRequest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|headroomSatisifed
init|=
name|checkHeadroom
argument_list|(
name|clusterResource
argument_list|,
name|resourceLimits
argument_list|,
name|increaseRequest
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|headroomSatisifed
condition|)
block|{
comment|// skip if doesn't satisfy headroom limit
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
literal|" Headroom is not satisfied, skip.."
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
name|RMContainer
name|rmContainer
init|=
name|increaseRequest
operator|.
name|getRMContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|rmContainer
operator|.
name|getContainerState
argument_list|()
operator|!=
name|ContainerState
operator|.
name|RUNNING
condition|)
block|{
comment|// if the container is not running, we should remove the
comment|// increaseRequest and continue;
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
literal|"  Container is not running any more, skip..."
argument_list|)
expr_stmt|;
block|}
name|toBeRemovedRequests
operator|.
name|add
argument_list|(
name|increaseRequest
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|Resources
operator|.
name|fitsIn
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|increaseRequest
operator|.
name|getTargetCapacity
argument_list|()
argument_list|,
name|node
operator|.
name|getTotalResource
argument_list|()
argument_list|)
condition|)
block|{
comment|// if the target capacity is more than what the node can offer, we
comment|// will simply remove and skip it.
comment|// The reason of doing check here instead of adding increase request
comment|// to scheduler because node's resource could be updated after
comment|// request added.
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
literal|"  Target capacity is more than what node can offer,"
operator|+
literal|" node.resource="
operator|+
name|node
operator|.
name|getTotalResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|toBeRemovedRequests
operator|.
name|add
argument_list|(
name|increaseRequest
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// Try to allocate the increase request
name|assigned
operator|=
name|allocateIncreaseRequest
argument_list|(
name|node
argument_list|,
name|clusterResource
argument_list|,
name|increaseRequest
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|assigned
operator|.
name|getSkipped
argument_list|()
condition|)
block|{
comment|// When we don't skip this request, which means we either allocated
comment|// OR reserved this request. We will break
break|break;
block|}
block|}
comment|// Remove invalid in request requests
if|if
condition|(
operator|!
name|toBeRemovedRequests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|SchedContainerChangeRequest
name|req
range|:
name|toBeRemovedRequests
control|)
block|{
name|sinfo
operator|.
name|removeIncreaseRequest
argument_list|(
name|req
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|req
operator|.
name|getPriority
argument_list|()
argument_list|,
name|req
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// We may have allocated something
if|if
condition|(
name|assigned
operator|!=
literal|null
operator|&&
operator|!
name|assigned
operator|.
name|getSkipped
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
return|return
name|assigned
operator|==
literal|null
condition|?
name|CSAssignment
operator|.
name|SKIP_ASSIGNMENT
else|:
name|assigned
return|;
block|}
else|else
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
literal|"Trying to allocate reserved increase container request.."
argument_list|)
expr_stmt|;
block|}
comment|// We already reserved this increase container
name|SchedContainerChangeRequest
name|request
init|=
name|sinfo
operator|.
name|getIncreaseRequest
argument_list|(
name|nodeId
argument_list|,
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|,
name|reservedContainer
operator|.
name|getContainerId
argument_list|()
argument_list|)
decl_stmt|;
comment|// We will cancel the reservation any of following happens
comment|// - Container finished
comment|// - No increase request needed
comment|// - Target resource updated
if|if
condition|(
literal|null
operator|==
name|request
operator|||
name|reservedContainer
operator|.
name|getContainerState
argument_list|()
operator|!=
name|ContainerState
operator|.
name|RUNNING
operator|||
operator|(
operator|!
name|Resources
operator|.
name|equals
argument_list|(
name|reservedContainer
operator|.
name|getReservedResource
argument_list|()
argument_list|,
name|request
operator|.
name|getDeltaCapacity
argument_list|()
argument_list|)
operator|)
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
literal|"We don't need reserved increase container request "
operator|+
literal|"for container="
operator|+
name|reservedContainer
operator|.
name|getContainerId
argument_list|()
operator|+
literal|". Unreserving and return..."
argument_list|)
expr_stmt|;
block|}
comment|// We don't need this container now, just return excessive reservation
return|return
operator|new
name|CSAssignment
argument_list|(
name|application
argument_list|,
name|reservedContainer
argument_list|)
return|;
block|}
return|return
name|allocateIncreaseRequestFromReservedContainer
argument_list|(
name|node
argument_list|,
name|clusterResource
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

