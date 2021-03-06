begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation.planning
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
name|reservation
operator|.
name|planning
package|;
end_package

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
name|ListIterator
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|ReservationDefinition
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
name|ReservationId
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
name|ReservationRequest
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
name|ReservationRequestInterpreter
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
name|reservation
operator|.
name|Plan
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
name|RLESparseResourceAllocation
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
name|RLESparseResourceAllocation
operator|.
name|RLEOperator
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
name|ReservationAllocation
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
name|ReservationInterval
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
name|exceptions
operator|.
name|ContractValidationException
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
name|exceptions
operator|.
name|PlanningException
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

begin_comment
comment|/**  * A planning algorithm consisting of two main phases. The algorithm iterates  * over the job stages in ascending/descending order, depending on the flag  * allocateLeft. For each stage, the algorithm: 1. Determines an interval  * [stageArrival, stageDeadline) in which the stage is allocated. 2. Computes an  * allocation for the stage inside the interval. For ANY and ALL jobs, phase 1  * sets the allocation window of each stage to be [jobArrival, jobDeadline]. For  * ORDER and ORDER_NO_GAP jobs, the deadline of each stage is set as  * succcessorStartTime - the starting time of its succeeding stage (or  * jobDeadline if it is the last stage). The phases are set using the two  * functions: 1. setAlgStageExecutionInterval 2.setAlgStageAllocator  */
end_comment

begin_class
DECL|class|IterativePlanner
specifier|public
class|class
name|IterativePlanner
extends|extends
name|PlanningAlgorithm
block|{
comment|// Modifications performed by the algorithm that are not been reflected in the
comment|// actual plan while a request is still pending.
DECL|field|planModifications
specifier|private
name|RLESparseResourceAllocation
name|planModifications
decl_stmt|;
comment|// Data extracted from plan
DECL|field|planLoads
specifier|private
name|RLESparseResourceAllocation
name|planLoads
decl_stmt|;
DECL|field|capacity
specifier|private
name|Resource
name|capacity
decl_stmt|;
DECL|field|step
specifier|private
name|long
name|step
decl_stmt|;
comment|// Job parameters
DECL|field|jobType
specifier|private
name|ReservationRequestInterpreter
name|jobType
decl_stmt|;
DECL|field|jobArrival
specifier|private
name|long
name|jobArrival
decl_stmt|;
DECL|field|jobDeadline
specifier|private
name|long
name|jobDeadline
decl_stmt|;
comment|// Phase algorithms
DECL|field|algStageExecutionInterval
specifier|private
name|StageExecutionInterval
name|algStageExecutionInterval
init|=
literal|null
decl_stmt|;
DECL|field|algStageAllocator
specifier|private
name|StageAllocator
name|algStageAllocator
init|=
literal|null
decl_stmt|;
DECL|field|allocateLeft
specifier|private
specifier|final
name|boolean
name|allocateLeft
decl_stmt|;
comment|// Constructor
DECL|method|IterativePlanner (StageExecutionInterval algStageExecutionInterval, StageAllocator algStageAllocator, boolean allocateLeft)
specifier|public
name|IterativePlanner
parameter_list|(
name|StageExecutionInterval
name|algStageExecutionInterval
parameter_list|,
name|StageAllocator
name|algStageAllocator
parameter_list|,
name|boolean
name|allocateLeft
parameter_list|)
block|{
name|this
operator|.
name|allocateLeft
operator|=
name|allocateLeft
expr_stmt|;
name|setAlgStageExecutionInterval
argument_list|(
name|algStageExecutionInterval
argument_list|)
expr_stmt|;
name|setAlgStageAllocator
argument_list|(
name|algStageAllocator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeJobAllocation (Plan plan, ReservationId reservationId, ReservationDefinition reservation, String user)
specifier|public
name|RLESparseResourceAllocation
name|computeJobAllocation
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationId
name|reservationId
parameter_list|,
name|ReservationDefinition
name|reservation
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|PlanningException
block|{
comment|// Initialize
name|initialize
argument_list|(
name|plan
argument_list|,
name|reservationId
argument_list|,
name|reservation
argument_list|)
expr_stmt|;
comment|// Create the allocations data structure
name|RLESparseResourceAllocation
name|allocations
init|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
decl_stmt|;
name|StageProvider
name|stageProvider
init|=
operator|new
name|StageProvider
argument_list|(
name|allocateLeft
argument_list|,
name|reservation
argument_list|)
decl_stmt|;
comment|// Current stage
name|ReservationRequest
name|currentReservationStage
decl_stmt|;
comment|// initialize periodicity
name|long
name|period
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|reservation
operator|.
name|getRecurrenceExpression
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|period
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|reservation
operator|.
name|getRecurrenceExpression
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Iterate the stages in reverse order
while|while
condition|(
name|stageProvider
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// Get current stage
name|currentReservationStage
operator|=
name|stageProvider
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Validate that the ReservationRequest respects basic constraints
name|validateInputStage
argument_list|(
name|plan
argument_list|,
name|currentReservationStage
argument_list|)
expr_stmt|;
comment|// Set the stageArrival and stageDeadline
name|ReservationInterval
name|stageInterval
init|=
name|setStageExecutionInterval
argument_list|(
name|plan
argument_list|,
name|reservation
argument_list|,
name|currentReservationStage
argument_list|,
name|allocations
argument_list|)
decl_stmt|;
name|Long
name|stageArrival
init|=
name|stageInterval
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|Long
name|stageDeadline
init|=
name|stageInterval
operator|.
name|getEndTime
argument_list|()
decl_stmt|;
comment|// Compute stage allocation
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|curAlloc
init|=
name|computeStageAllocation
argument_list|(
name|plan
argument_list|,
name|currentReservationStage
argument_list|,
name|stageArrival
argument_list|,
name|stageDeadline
argument_list|,
name|period
argument_list|,
name|user
argument_list|,
name|reservationId
argument_list|)
decl_stmt|;
comment|// If we did not find an allocation, return NULL
comment|// (unless it's an ANY job, then we simply continue).
if|if
condition|(
name|curAlloc
operator|==
literal|null
condition|)
block|{
comment|// If it's an ANY job, we can move to the next possible request
if|if
condition|(
name|jobType
operator|==
name|ReservationRequestInterpreter
operator|.
name|R_ANY
condition|)
block|{
continue|continue;
block|}
comment|// Otherwise, the job cannot be allocated
throw|throw
operator|new
name|PlanningException
argument_list|(
literal|"The request cannot be satisfied"
argument_list|)
throw|;
block|}
comment|// Validate ORDER_NO_GAP
if|if
condition|(
name|jobType
operator|==
name|ReservationRequestInterpreter
operator|.
name|R_ORDER_NO_GAP
condition|)
block|{
if|if
condition|(
operator|!
name|validateOrderNoGap
argument_list|(
name|allocations
argument_list|,
name|curAlloc
argument_list|,
name|allocateLeft
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PlanningException
argument_list|(
literal|"The allocation found does not respect ORDER_NO_GAP"
argument_list|)
throw|;
block|}
block|}
comment|// If we did find an allocation for the stage, add it
for|for
control|(
name|Entry
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|entry
range|:
name|curAlloc
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|allocations
operator|.
name|addInterval
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// If this is an ANY clause, we have finished
if|if
condition|(
name|jobType
operator|==
name|ReservationRequestInterpreter
operator|.
name|R_ANY
condition|)
block|{
break|break;
block|}
block|}
comment|// If the allocation is empty, return an error
if|if
condition|(
name|allocations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PlanningException
argument_list|(
literal|"The request cannot be satisfied"
argument_list|)
throw|;
block|}
return|return
name|allocations
return|;
block|}
DECL|method|validateOrderNoGap ( RLESparseResourceAllocation allocations, Map<ReservationInterval, Resource> curAlloc, boolean allocateLeft)
specifier|protected
specifier|static
name|boolean
name|validateOrderNoGap
parameter_list|(
name|RLESparseResourceAllocation
name|allocations
parameter_list|,
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|curAlloc
parameter_list|,
name|boolean
name|allocateLeft
parameter_list|)
block|{
comment|// Left to right
if|if
condition|(
name|allocateLeft
condition|)
block|{
name|Long
name|stageStartTime
init|=
name|findEarliestTime
argument_list|(
name|curAlloc
argument_list|)
decl_stmt|;
name|Long
name|allocationEndTime
init|=
name|allocations
operator|.
name|getLatestNonNullTime
argument_list|()
decl_stmt|;
comment|// Check that there is no gap between stages
if|if
condition|(
operator|(
name|allocationEndTime
operator|!=
operator|-
literal|1
operator|)
operator|&&
operator|(
name|allocationEndTime
operator|<
name|stageStartTime
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Right to left
block|}
else|else
block|{
name|Long
name|stageEndTime
init|=
name|findLatestTime
argument_list|(
name|curAlloc
argument_list|)
decl_stmt|;
name|Long
name|allocationStartTime
init|=
name|allocations
operator|.
name|getEarliestStartTime
argument_list|()
decl_stmt|;
comment|// Check that there is no gap between stages
if|if
condition|(
operator|(
name|allocationStartTime
operator|!=
operator|-
literal|1
operator|)
operator|&&
operator|(
name|stageEndTime
operator|<
name|allocationStartTime
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Check that the stage allocation does not violate ORDER_NO_GAP
if|if
condition|(
operator|!
name|isNonPreemptiveAllocation
argument_list|(
name|curAlloc
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// The allocation is legal
return|return
literal|true
return|;
block|}
DECL|method|initialize (Plan plan, ReservationId reservationId, ReservationDefinition reservation)
specifier|protected
name|void
name|initialize
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationId
name|reservationId
parameter_list|,
name|ReservationDefinition
name|reservation
parameter_list|)
throws|throws
name|PlanningException
block|{
comment|// Get plan step& capacity
name|capacity
operator|=
name|plan
operator|.
name|getTotalCapacity
argument_list|()
expr_stmt|;
name|step
operator|=
name|plan
operator|.
name|getStep
argument_list|()
expr_stmt|;
comment|// Get job parameters (type, arrival time& deadline)
name|jobType
operator|=
name|reservation
operator|.
name|getReservationRequests
argument_list|()
operator|.
name|getInterpreter
argument_list|()
expr_stmt|;
name|jobArrival
operator|=
name|stepRoundUp
argument_list|(
name|reservation
operator|.
name|getArrival
argument_list|()
argument_list|,
name|step
argument_list|)
expr_stmt|;
name|jobDeadline
operator|=
name|stepRoundDown
argument_list|(
name|reservation
operator|.
name|getDeadline
argument_list|()
argument_list|,
name|step
argument_list|)
expr_stmt|;
comment|// Initialize the plan modifications
name|planModifications
operator|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
expr_stmt|;
comment|// Dirty read of plan load
comment|// planLoads are not used by other StageAllocators... and don't deal
comment|// well with huge reservation ranges
name|planLoads
operator|=
name|plan
operator|.
name|getCumulativeLoadOverTime
argument_list|(
name|jobArrival
argument_list|,
name|jobDeadline
argument_list|)
expr_stmt|;
name|ReservationAllocation
name|oldRes
init|=
name|plan
operator|.
name|getReservationById
argument_list|(
name|reservationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldRes
operator|!=
literal|null
condition|)
block|{
name|planLoads
operator|=
name|RLESparseResourceAllocation
operator|.
name|merge
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
argument_list|,
name|planLoads
argument_list|,
name|oldRes
operator|.
name|getResourcesOverTime
argument_list|(
name|jobArrival
argument_list|,
name|jobDeadline
argument_list|)
argument_list|,
name|RLEOperator
operator|.
name|subtract
argument_list|,
name|jobArrival
argument_list|,
name|jobDeadline
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validateInputStage (Plan plan, ReservationRequest rr)
specifier|private
name|void
name|validateInputStage
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationRequest
name|rr
parameter_list|)
throws|throws
name|ContractValidationException
block|{
comment|// Validate concurrency
if|if
condition|(
name|rr
operator|.
name|getConcurrency
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|ContractValidationException
argument_list|(
literal|"Gang Size should be>= 1"
argument_list|)
throw|;
block|}
comment|// Validate number of containers
if|if
condition|(
name|rr
operator|.
name|getNumContainers
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|ContractValidationException
argument_list|(
literal|"Num containers should be> 0"
argument_list|)
throw|;
block|}
comment|// Check that gangSize and numContainers are compatible
if|if
condition|(
name|rr
operator|.
name|getNumContainers
argument_list|()
operator|%
name|rr
operator|.
name|getConcurrency
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|ContractValidationException
argument_list|(
literal|"Parallelism must be an exact multiple of gang size"
argument_list|)
throw|;
block|}
comment|// Check that the largest container request does not exceed the cluster-wide
comment|// limit for container sizes
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|capacity
argument_list|,
name|rr
operator|.
name|getCapability
argument_list|()
argument_list|,
name|plan
operator|.
name|getMaximumAllocation
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ContractValidationException
argument_list|(
literal|"Individual capability requests should not exceed cluster's "
operator|+
literal|"maxAlloc"
argument_list|)
throw|;
block|}
block|}
DECL|method|isNonPreemptiveAllocation ( Map<ReservationInterval, Resource> curAlloc)
specifier|private
specifier|static
name|boolean
name|isNonPreemptiveAllocation
parameter_list|(
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|curAlloc
parameter_list|)
block|{
comment|// Checks whether a stage allocation is non preemptive or not.
comment|// Assumption: the intervals are non-intersecting (as returned by
comment|// computeStageAllocation()).
comment|// For a non-preemptive allocation, only two end points appear exactly once
name|Set
argument_list|<
name|Long
argument_list|>
name|endPoints
init|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|(
literal|2
operator|*
name|curAlloc
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|entry
range|:
name|curAlloc
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ReservationInterval
name|interval
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Resource
name|resource
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Ignore intervals with no allocation
if|if
condition|(
name|Resources
operator|.
name|equals
argument_list|(
name|resource
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// Get endpoints
name|Long
name|left
init|=
name|interval
operator|.
name|getStartTime
argument_list|()
decl_stmt|;
name|Long
name|right
init|=
name|interval
operator|.
name|getEndTime
argument_list|()
decl_stmt|;
comment|// Add left endpoint if we haven't seen it before, remove otherwise
if|if
condition|(
operator|!
name|endPoints
operator|.
name|contains
argument_list|(
name|left
argument_list|)
condition|)
block|{
name|endPoints
operator|.
name|add
argument_list|(
name|left
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|endPoints
operator|.
name|remove
argument_list|(
name|left
argument_list|)
expr_stmt|;
block|}
comment|// Add right endpoint if we haven't seen it before, remove otherwise
if|if
condition|(
operator|!
name|endPoints
operator|.
name|contains
argument_list|(
name|right
argument_list|)
condition|)
block|{
name|endPoints
operator|.
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|endPoints
operator|.
name|remove
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Non-preemptive only if endPoints is of size 2
return|return
operator|(
name|endPoints
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|)
return|;
block|}
comment|// Call setStageExecutionInterval()
DECL|method|setStageExecutionInterval (Plan plan, ReservationDefinition reservation, ReservationRequest currentReservationStage, RLESparseResourceAllocation allocations)
specifier|protected
name|ReservationInterval
name|setStageExecutionInterval
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationDefinition
name|reservation
parameter_list|,
name|ReservationRequest
name|currentReservationStage
parameter_list|,
name|RLESparseResourceAllocation
name|allocations
parameter_list|)
block|{
return|return
name|algStageExecutionInterval
operator|.
name|computeExecutionInterval
argument_list|(
name|plan
argument_list|,
name|reservation
argument_list|,
name|currentReservationStage
argument_list|,
name|allocateLeft
argument_list|,
name|allocations
argument_list|)
return|;
block|}
comment|// Call algStageAllocator
DECL|method|computeStageAllocation (Plan plan, ReservationRequest rr, long stageArrivalTime, long stageDeadline, long period, String user, ReservationId oldId)
specifier|protected
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|computeStageAllocation
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationRequest
name|rr
parameter_list|,
name|long
name|stageArrivalTime
parameter_list|,
name|long
name|stageDeadline
parameter_list|,
name|long
name|period
parameter_list|,
name|String
name|user
parameter_list|,
name|ReservationId
name|oldId
parameter_list|)
throws|throws
name|PlanningException
block|{
return|return
name|algStageAllocator
operator|.
name|computeStageAllocation
argument_list|(
name|plan
argument_list|,
name|planLoads
argument_list|,
name|planModifications
argument_list|,
name|rr
argument_list|,
name|stageArrivalTime
argument_list|,
name|stageDeadline
argument_list|,
name|period
argument_list|,
name|user
argument_list|,
name|oldId
argument_list|)
return|;
block|}
comment|// Set the algorithm: algStageExecutionInterval
DECL|method|setAlgStageExecutionInterval ( StageExecutionInterval alg)
specifier|public
name|IterativePlanner
name|setAlgStageExecutionInterval
parameter_list|(
name|StageExecutionInterval
name|alg
parameter_list|)
block|{
name|this
operator|.
name|algStageExecutionInterval
operator|=
name|alg
expr_stmt|;
return|return
name|this
return|;
comment|// To allow concatenation of setAlg() functions
block|}
comment|// Set the algorithm: algStageAllocator
DECL|method|setAlgStageAllocator (StageAllocator alg)
specifier|public
name|IterativePlanner
name|setAlgStageAllocator
parameter_list|(
name|StageAllocator
name|alg
parameter_list|)
block|{
name|this
operator|.
name|algStageAllocator
operator|=
name|alg
expr_stmt|;
return|return
name|this
return|;
comment|// To allow concatenation of setAlg() functions
block|}
comment|/**    * Helper class that provide a list of ReservationRequests and iterates    * forward or backward depending whether we are allocating left-to-right or    * right-to-left.    */
DECL|class|StageProvider
specifier|public
specifier|static
class|class
name|StageProvider
block|{
DECL|field|allocateLeft
specifier|private
specifier|final
name|boolean
name|allocateLeft
decl_stmt|;
DECL|field|li
specifier|private
specifier|final
name|ListIterator
argument_list|<
name|ReservationRequest
argument_list|>
name|li
decl_stmt|;
DECL|method|StageProvider (boolean allocateLeft, ReservationDefinition reservation)
specifier|public
name|StageProvider
parameter_list|(
name|boolean
name|allocateLeft
parameter_list|,
name|ReservationDefinition
name|reservation
parameter_list|)
block|{
name|this
operator|.
name|allocateLeft
operator|=
name|allocateLeft
expr_stmt|;
name|int
name|startingIndex
decl_stmt|;
if|if
condition|(
name|allocateLeft
condition|)
block|{
name|startingIndex
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|startingIndex
operator|=
name|reservation
operator|.
name|getReservationRequests
argument_list|()
operator|.
name|getReservationResources
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|// Get a reverse iterator for the set of stages
name|li
operator|=
name|reservation
operator|.
name|getReservationRequests
argument_list|()
operator|.
name|getReservationResources
argument_list|()
operator|.
name|listIterator
argument_list|(
name|startingIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|allocateLeft
condition|)
block|{
return|return
name|li
operator|.
name|hasNext
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|li
operator|.
name|hasPrevious
argument_list|()
return|;
block|}
block|}
DECL|method|next ()
specifier|public
name|ReservationRequest
name|next
parameter_list|()
block|{
if|if
condition|(
name|allocateLeft
condition|)
block|{
return|return
name|li
operator|.
name|next
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|li
operator|.
name|previous
argument_list|()
return|;
block|}
block|}
DECL|method|getCurrentIndex ()
specifier|public
name|int
name|getCurrentIndex
parameter_list|()
block|{
if|if
condition|(
name|allocateLeft
condition|)
block|{
return|return
name|li
operator|.
name|nextIndex
argument_list|()
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|li
operator|.
name|previousIndex
argument_list|()
operator|+
literal|1
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

