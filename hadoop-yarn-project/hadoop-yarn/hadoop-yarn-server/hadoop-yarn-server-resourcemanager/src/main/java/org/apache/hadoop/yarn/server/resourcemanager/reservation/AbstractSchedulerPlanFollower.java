begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation
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
name|Collection
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|Queue
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
name|common
operator|.
name|QueueEntitlement
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

begin_class
DECL|class|AbstractSchedulerPlanFollower
specifier|public
specifier|abstract
class|class
name|AbstractSchedulerPlanFollower
implements|implements
name|PlanFollower
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
name|AbstractSchedulerPlanFollower
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|plans
specifier|protected
name|Collection
argument_list|<
name|Plan
argument_list|>
name|plans
init|=
operator|new
name|ArrayList
argument_list|<
name|Plan
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|scheduler
specifier|protected
name|YarnScheduler
name|scheduler
decl_stmt|;
DECL|field|clock
specifier|protected
name|Clock
name|clock
decl_stmt|;
annotation|@
name|Override
DECL|method|init (Clock clock, ResourceScheduler sched, Collection<Plan> plans)
specifier|public
name|void
name|init
parameter_list|(
name|Clock
name|clock
parameter_list|,
name|ResourceScheduler
name|sched
parameter_list|,
name|Collection
argument_list|<
name|Plan
argument_list|>
name|plans
parameter_list|)
block|{
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|sched
expr_stmt|;
name|this
operator|.
name|plans
operator|.
name|addAll
argument_list|(
name|plans
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|Plan
name|plan
range|:
name|plans
control|)
block|{
name|synchronizePlan
argument_list|(
name|plan
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setPlans (Collection<Plan> plans)
specifier|public
specifier|synchronized
name|void
name|setPlans
parameter_list|(
name|Collection
argument_list|<
name|Plan
argument_list|>
name|plans
parameter_list|)
block|{
name|this
operator|.
name|plans
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|plans
operator|.
name|addAll
argument_list|(
name|plans
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|synchronizePlan (Plan plan, boolean shouldReplan)
specifier|public
specifier|synchronized
name|void
name|synchronizePlan
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|boolean
name|shouldReplan
parameter_list|)
block|{
name|String
name|planQueueName
init|=
name|plan
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running plan follower edit policy for plan: {}"
argument_list|,
name|planQueueName
argument_list|)
expr_stmt|;
comment|// align with plan step
name|long
name|step
init|=
name|plan
operator|.
name|getStep
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|%
name|step
operator|!=
literal|0
condition|)
block|{
name|now
operator|+=
name|step
operator|-
operator|(
name|now
operator|%
name|step
operator|)
expr_stmt|;
block|}
name|Queue
name|planQueue
init|=
name|getPlanQueue
argument_list|(
name|planQueueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|planQueue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// first we publish to the plan the current availability of resources
name|Resource
name|clusterResources
init|=
name|scheduler
operator|.
name|getClusterResource
argument_list|()
decl_stmt|;
name|Resource
name|planResources
init|=
name|getPlanResources
argument_list|(
name|plan
argument_list|,
name|planQueue
argument_list|,
name|clusterResources
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ReservationAllocation
argument_list|>
name|currentReservations
init|=
name|plan
operator|.
name|getReservationsAtTime
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|curReservationNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Resource
name|reservedResources
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|numRes
init|=
name|getReservedResources
argument_list|(
name|now
argument_list|,
name|currentReservations
argument_list|,
name|curReservationNames
argument_list|,
name|reservedResources
argument_list|)
decl_stmt|;
comment|// create the default reservation queue if it doesnt exist
name|String
name|defReservationId
init|=
name|getReservationIdFromQueueName
argument_list|(
name|planQueueName
argument_list|)
operator|+
name|ReservationConstants
operator|.
name|DEFAULT_QUEUE_SUFFIX
decl_stmt|;
name|String
name|defReservationQueue
init|=
name|getReservationQueueName
argument_list|(
name|planQueueName
argument_list|,
name|defReservationId
argument_list|)
decl_stmt|;
name|createDefaultReservationQueue
argument_list|(
name|planQueueName
argument_list|,
name|planQueue
argument_list|,
name|defReservationId
argument_list|)
expr_stmt|;
name|curReservationNames
operator|.
name|add
argument_list|(
name|defReservationId
argument_list|)
expr_stmt|;
comment|// if the resources dedicated to this plan has shrunk invoke replanner
name|boolean
name|shouldResize
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|arePlanResourcesLessThanReservations
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|clusterResources
argument_list|,
name|planResources
argument_list|,
name|reservedResources
argument_list|)
condition|)
block|{
if|if
condition|(
name|shouldReplan
condition|)
block|{
try|try
block|{
name|plan
operator|.
name|getReplanner
argument_list|()
operator|.
name|plan
argument_list|(
name|plan
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while trying to replan: {}"
argument_list|,
name|planQueueName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|shouldResize
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// identify the reservations that have expired and new reservations that
comment|// have to be activated
name|List
argument_list|<
name|?
extends|extends
name|Queue
argument_list|>
name|resQueues
init|=
name|getChildReservationQueues
argument_list|(
name|planQueue
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expired
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Queue
name|resQueue
range|:
name|resQueues
control|)
block|{
name|String
name|resQueueName
init|=
name|resQueue
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|String
name|reservationId
init|=
name|getReservationIdFromQueueName
argument_list|(
name|resQueueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|curReservationNames
operator|.
name|contains
argument_list|(
name|reservationId
argument_list|)
condition|)
block|{
comment|// it is already existing reservation, so needed not create new
comment|// reservation queue
name|curReservationNames
operator|.
name|remove
argument_list|(
name|reservationId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the reservation has termination, mark for cleanup
name|expired
operator|.
name|add
argument_list|(
name|reservationId
argument_list|)
expr_stmt|;
block|}
block|}
comment|// garbage collect expired reservations
name|cleanupExpiredQueues
argument_list|(
name|planQueueName
argument_list|,
name|plan
operator|.
name|getMoveOnExpiry
argument_list|()
argument_list|,
name|expired
argument_list|,
name|defReservationQueue
argument_list|)
expr_stmt|;
comment|// Add new reservations and update existing ones
name|float
name|totalAssignedCapacity
init|=
literal|0f
decl_stmt|;
if|if
condition|(
name|currentReservations
operator|!=
literal|null
condition|)
block|{
comment|// first release all excess capacity in default queue
try|try
block|{
name|setQueueEntitlement
argument_list|(
name|planQueueName
argument_list|,
name|defReservationQueue
argument_list|,
literal|0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while trying to release default queue capacity for plan: {}"
argument_list|,
name|planQueueName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// sort allocations from the one giving up the most resources, to the
comment|// one asking for the most avoid order-of-operation errors that
comment|// temporarily violate 100% capacity bound
name|List
argument_list|<
name|ReservationAllocation
argument_list|>
name|sortedAllocations
init|=
name|sortByDelta
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ReservationAllocation
argument_list|>
argument_list|(
name|currentReservations
argument_list|)
argument_list|,
name|now
argument_list|,
name|plan
argument_list|)
decl_stmt|;
for|for
control|(
name|ReservationAllocation
name|res
range|:
name|sortedAllocations
control|)
block|{
name|String
name|currResId
init|=
name|res
operator|.
name|getReservationId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|curReservationNames
operator|.
name|contains
argument_list|(
name|currResId
argument_list|)
condition|)
block|{
name|addReservationQueue
argument_list|(
name|planQueueName
argument_list|,
name|planQueue
argument_list|,
name|currResId
argument_list|)
expr_stmt|;
block|}
name|Resource
name|capToAssign
init|=
name|res
operator|.
name|getResourcesAtTime
argument_list|(
name|now
argument_list|)
decl_stmt|;
name|float
name|targetCapacity
init|=
literal|0f
decl_stmt|;
if|if
condition|(
name|planResources
operator|.
name|getMemorySize
argument_list|()
operator|>
literal|0
operator|&&
name|planResources
operator|.
name|getVirtualCores
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|shouldResize
condition|)
block|{
name|capToAssign
operator|=
name|calculateReservationToPlanProportion
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|planResources
argument_list|,
name|reservedResources
argument_list|,
name|capToAssign
argument_list|)
expr_stmt|;
block|}
name|targetCapacity
operator|=
name|calculateReservationToPlanRatio
argument_list|(
name|plan
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|clusterResources
argument_list|,
name|planResources
argument_list|,
name|capToAssign
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Assigning capacity of {} to queue {} with target capacity {}"
argument_list|,
name|capToAssign
argument_list|,
name|currResId
argument_list|,
name|targetCapacity
argument_list|)
expr_stmt|;
comment|// set maxCapacity to 100% unless the job requires gang, in which
comment|// case we stick to capacity (as running early/before is likely a
comment|// waste of resources)
name|float
name|maxCapacity
init|=
literal|1.0f
decl_stmt|;
if|if
condition|(
name|res
operator|.
name|containsGangs
argument_list|()
condition|)
block|{
name|maxCapacity
operator|=
name|targetCapacity
expr_stmt|;
block|}
try|try
block|{
name|setQueueEntitlement
argument_list|(
name|planQueueName
argument_list|,
name|currResId
argument_list|,
name|targetCapacity
argument_list|,
name|maxCapacity
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while trying to size reservation for plan: {}"
argument_list|,
name|currResId
argument_list|,
name|planQueueName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|totalAssignedCapacity
operator|+=
name|targetCapacity
expr_stmt|;
block|}
block|}
comment|// compute the default queue capacity
name|float
name|defQCap
init|=
literal|1.0f
operator|-
name|totalAssignedCapacity
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"PlanFollowerEditPolicyTask: total Plan Capacity: {} "
operator|+
literal|"currReservation: {} default-queue capacity: {}"
argument_list|,
name|planResources
argument_list|,
name|numRes
argument_list|,
name|defQCap
argument_list|)
expr_stmt|;
comment|// set the default queue to eat-up all remaining capacity
try|try
block|{
name|setQueueEntitlement
argument_list|(
name|planQueueName
argument_list|,
name|defReservationQueue
argument_list|,
name|defQCap
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while trying to reclaim default queue capacity for plan: {}"
argument_list|,
name|planQueueName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// garbage collect finished reservations from plan
try|try
block|{
name|plan
operator|.
name|archiveCompletedReservations
argument_list|(
name|now
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PlanningException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception in archiving completed reservations: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Finished iteration of plan follower edit policy for plan: "
operator|+
name|planQueueName
argument_list|)
expr_stmt|;
comment|// Extension: update plan with app states,
comment|// useful to support smart replanning
block|}
DECL|method|getReservationIdFromQueueName (String resQueueName)
specifier|protected
name|String
name|getReservationIdFromQueueName
parameter_list|(
name|String
name|resQueueName
parameter_list|)
block|{
return|return
name|resQueueName
return|;
block|}
DECL|method|setQueueEntitlement (String planQueueName, String currResId, float targetCapacity, float maxCapacity)
specifier|protected
name|void
name|setQueueEntitlement
parameter_list|(
name|String
name|planQueueName
parameter_list|,
name|String
name|currResId
parameter_list|,
name|float
name|targetCapacity
parameter_list|,
name|float
name|maxCapacity
parameter_list|)
throws|throws
name|YarnException
block|{
name|String
name|reservationQueueName
init|=
name|getReservationQueueName
argument_list|(
name|planQueueName
argument_list|,
name|currResId
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|setEntitlement
argument_list|(
name|reservationQueueName
argument_list|,
operator|new
name|QueueEntitlement
argument_list|(
name|targetCapacity
argument_list|,
name|maxCapacity
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Schedulers have different ways of naming queues. See YARN-2773
DECL|method|getReservationQueueName (String planQueueName, String reservationId)
specifier|protected
name|String
name|getReservationQueueName
parameter_list|(
name|String
name|planQueueName
parameter_list|,
name|String
name|reservationId
parameter_list|)
block|{
return|return
name|reservationId
return|;
block|}
comment|/**    * First sets entitlement of queues to zero to prevent new app submission.    * Then move all apps in the set of queues to the parent plan queue's default    * reservation queue if move is enabled. Finally cleanups the queue by killing    * any apps (if move is disabled or move failed) and removing the queue    *    * @param planQueueName the name of {@code PlanQueue}    * @param shouldMove flag to indicate if any running apps should be moved or    *          killed    * @param toRemove the remnant apps to clean up    * @param defReservationQueue the default {@code ReservationQueue} of the    *          {@link Plan}    */
DECL|method|cleanupExpiredQueues (String planQueueName, boolean shouldMove, Set<String> toRemove, String defReservationQueue)
specifier|protected
name|void
name|cleanupExpiredQueues
parameter_list|(
name|String
name|planQueueName
parameter_list|,
name|boolean
name|shouldMove
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|toRemove
parameter_list|,
name|String
name|defReservationQueue
parameter_list|)
block|{
for|for
control|(
name|String
name|expiredReservationId
range|:
name|toRemove
control|)
block|{
try|try
block|{
comment|// reduce entitlement to 0
name|String
name|expiredReservation
init|=
name|getReservationQueueName
argument_list|(
name|planQueueName
argument_list|,
name|expiredReservationId
argument_list|)
decl_stmt|;
name|setQueueEntitlement
argument_list|(
name|planQueueName
argument_list|,
name|expiredReservation
argument_list|,
literal|0.0f
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldMove
condition|)
block|{
name|moveAppsInQueueSync
argument_list|(
name|expiredReservation
argument_list|,
name|defReservationQueue
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|appsInQueue
init|=
name|scheduler
operator|.
name|getAppsInQueue
argument_list|(
name|expiredReservation
argument_list|)
decl_stmt|;
name|int
name|size
init|=
operator|(
name|appsInQueue
operator|==
literal|null
condition|?
literal|0
else|:
name|appsInQueue
operator|.
name|size
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|scheduler
operator|.
name|killAllAppsInQueue
argument_list|(
name|expiredReservation
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Killing applications in queue: {}"
argument_list|,
name|expiredReservation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scheduler
operator|.
name|removeQueue
argument_list|(
name|expiredReservation
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Queue: "
operator|+
name|expiredReservation
operator|+
literal|" removed"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while trying to expire reservation: {}"
argument_list|,
name|expiredReservationId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Move all apps in the set of queues to the parent plan queue's default    * reservation queue in a synchronous fashion    */
DECL|method|moveAppsInQueueSync (String expiredReservation, String defReservationQueue)
specifier|private
name|void
name|moveAppsInQueueSync
parameter_list|(
name|String
name|expiredReservation
parameter_list|,
name|String
name|defReservationQueue
parameter_list|)
block|{
name|List
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|activeApps
init|=
name|scheduler
operator|.
name|getAppsInQueue
argument_list|(
name|expiredReservation
argument_list|)
decl_stmt|;
if|if
condition|(
name|activeApps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|ApplicationAttemptId
name|app
range|:
name|activeApps
control|)
block|{
comment|// fallback to parent's default queue
try|try
block|{
name|scheduler
operator|.
name|moveApplication
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|defReservationQueue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Encountered unexpected error during migration of application: {}"
operator|+
literal|" from reservation: {}"
argument_list|,
name|app
argument_list|,
name|expiredReservation
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getReservedResources (long now, Set<ReservationAllocation> currentReservations, Set<String> curReservationNames, Resource reservedResources)
specifier|protected
name|int
name|getReservedResources
parameter_list|(
name|long
name|now
parameter_list|,
name|Set
argument_list|<
name|ReservationAllocation
argument_list|>
name|currentReservations
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|curReservationNames
parameter_list|,
name|Resource
name|reservedResources
parameter_list|)
block|{
name|int
name|numRes
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|currentReservations
operator|!=
literal|null
condition|)
block|{
name|numRes
operator|=
name|currentReservations
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|ReservationAllocation
name|reservation
range|:
name|currentReservations
control|)
block|{
name|curReservationNames
operator|.
name|add
argument_list|(
name|reservation
operator|.
name|getReservationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|reservedResources
argument_list|,
name|reservation
operator|.
name|getResourcesAtTime
argument_list|(
name|now
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|numRes
return|;
block|}
comment|/**    * Sort in the order from the least new amount of resources asked (likely    * negative) to the highest. This prevents "order-of-operation" errors related    * to exceeding 100% capacity temporarily.    *    * @param currentReservations the currently active reservations    * @param now the current time    * @param plan the {@link Plan} that is being considered    *    * @return the sorted list of {@link ReservationAllocation}s    */
DECL|method|sortByDelta ( List<ReservationAllocation> currentReservations, long now, Plan plan)
specifier|protected
name|List
argument_list|<
name|ReservationAllocation
argument_list|>
name|sortByDelta
parameter_list|(
name|List
argument_list|<
name|ReservationAllocation
argument_list|>
name|currentReservations
parameter_list|,
name|long
name|now
parameter_list|,
name|Plan
name|plan
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|currentReservations
argument_list|,
operator|new
name|ReservationAllocationComparator
argument_list|(
name|now
argument_list|,
name|this
argument_list|,
name|plan
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentReservations
return|;
block|}
comment|/**    * Get queue associated with reservable queue named.    *    * @param planQueueName name of the reservable queue    * @return queue associated with the reservable queue    */
DECL|method|getPlanQueue (String planQueueName)
specifier|protected
specifier|abstract
name|Queue
name|getPlanQueue
parameter_list|(
name|String
name|planQueueName
parameter_list|)
function_decl|;
comment|/**    * Resizes reservations based on currently available resources.    */
DECL|method|calculateReservationToPlanProportion ( ResourceCalculator rescCalculator, Resource availablePlanResources, Resource totalReservationResources, Resource reservationResources)
specifier|private
name|Resource
name|calculateReservationToPlanProportion
parameter_list|(
name|ResourceCalculator
name|rescCalculator
parameter_list|,
name|Resource
name|availablePlanResources
parameter_list|,
name|Resource
name|totalReservationResources
parameter_list|,
name|Resource
name|reservationResources
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|multiply
argument_list|(
name|availablePlanResources
argument_list|,
name|Resources
operator|.
name|ratio
argument_list|(
name|rescCalculator
argument_list|,
name|reservationResources
argument_list|,
name|totalReservationResources
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Calculates ratio of reservationResources to planResources.    */
DECL|method|calculateReservationToPlanRatio ( ResourceCalculator rescCalculator, Resource clusterResources, Resource planResources, Resource reservationResources)
specifier|private
name|float
name|calculateReservationToPlanRatio
parameter_list|(
name|ResourceCalculator
name|rescCalculator
parameter_list|,
name|Resource
name|clusterResources
parameter_list|,
name|Resource
name|planResources
parameter_list|,
name|Resource
name|reservationResources
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|divide
argument_list|(
name|rescCalculator
argument_list|,
name|clusterResources
argument_list|,
name|reservationResources
argument_list|,
name|planResources
argument_list|)
return|;
block|}
comment|/**    * Check if plan resources are less than expected reservation resources.    */
DECL|method|arePlanResourcesLessThanReservations ( ResourceCalculator rescCalculator, Resource clusterResources, Resource planResources, Resource reservedResources)
specifier|private
name|boolean
name|arePlanResourcesLessThanReservations
parameter_list|(
name|ResourceCalculator
name|rescCalculator
parameter_list|,
name|Resource
name|clusterResources
parameter_list|,
name|Resource
name|planResources
parameter_list|,
name|Resource
name|reservedResources
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rescCalculator
argument_list|,
name|clusterResources
argument_list|,
name|reservedResources
argument_list|,
name|planResources
argument_list|)
return|;
block|}
comment|/**    * Get a list of reservation queues for this planQueue.    *    * @param planQueue the queue for the current {@link Plan}    *    * @return the queues corresponding to the reservations    */
DECL|method|getChildReservationQueues ( Queue planQueue)
specifier|protected
specifier|abstract
name|List
argument_list|<
name|?
extends|extends
name|Queue
argument_list|>
name|getChildReservationQueues
parameter_list|(
name|Queue
name|planQueue
parameter_list|)
function_decl|;
comment|/**    * Add a new reservation queue for reservation currResId for this planQueue.    */
DECL|method|addReservationQueue (String planQueueName, Queue queue, String currResId)
specifier|protected
specifier|abstract
name|void
name|addReservationQueue
parameter_list|(
name|String
name|planQueueName
parameter_list|,
name|Queue
name|queue
parameter_list|,
name|String
name|currResId
parameter_list|)
function_decl|;
comment|/**    * Creates the default reservation queue for use when no reservation is used    * for applications submitted to this planQueue.    *    * @param planQueueName name of the reservable queue    * @param queue the queue for the current {@link Plan}    * @param defReservationQueue name of the default {@code ReservationQueue}    */
DECL|method|createDefaultReservationQueue (String planQueueName, Queue queue, String defReservationQueue)
specifier|protected
specifier|abstract
name|void
name|createDefaultReservationQueue
parameter_list|(
name|String
name|planQueueName
parameter_list|,
name|Queue
name|queue
parameter_list|,
name|String
name|defReservationQueue
parameter_list|)
function_decl|;
comment|/**    * Get plan resources for this planQueue.    *    * @param plan the current {@link Plan} being considered    * @param clusterResources the resources available in the cluster    *    * @return the resources allocated to the specified {@link Plan}    */
DECL|method|getPlanResources (Plan plan, Queue queue, Resource clusterResources)
specifier|protected
specifier|abstract
name|Resource
name|getPlanResources
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|Queue
name|queue
parameter_list|,
name|Resource
name|clusterResources
parameter_list|)
function_decl|;
comment|/**    * Get reservation queue resources if it exists otherwise return null.    *    * @param plan the current {@link Plan} being considered    * @param reservationId the identifier of the reservation    *    * @return the resources allocated to the specified reservation    */
DECL|method|getReservationQueueResourceIfExists (Plan plan, ReservationId reservationId)
specifier|protected
specifier|abstract
name|Resource
name|getReservationQueueResourceIfExists
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationId
name|reservationId
parameter_list|)
function_decl|;
DECL|class|ReservationAllocationComparator
specifier|private
specifier|static
class|class
name|ReservationAllocationComparator
implements|implements
name|Comparator
argument_list|<
name|ReservationAllocation
argument_list|>
block|{
DECL|field|planFollower
name|AbstractSchedulerPlanFollower
name|planFollower
decl_stmt|;
DECL|field|now
name|long
name|now
decl_stmt|;
DECL|field|plan
name|Plan
name|plan
decl_stmt|;
DECL|method|ReservationAllocationComparator (long now, AbstractSchedulerPlanFollower planFollower, Plan plan)
name|ReservationAllocationComparator
parameter_list|(
name|long
name|now
parameter_list|,
name|AbstractSchedulerPlanFollower
name|planFollower
parameter_list|,
name|Plan
name|plan
parameter_list|)
block|{
name|this
operator|.
name|now
operator|=
name|now
expr_stmt|;
name|this
operator|.
name|planFollower
operator|=
name|planFollower
expr_stmt|;
name|this
operator|.
name|plan
operator|=
name|plan
expr_stmt|;
block|}
DECL|method|getUnallocatedReservedResources ( ReservationAllocation reservation)
specifier|private
name|Resource
name|getUnallocatedReservedResources
parameter_list|(
name|ReservationAllocation
name|reservation
parameter_list|)
block|{
name|Resource
name|resResource
decl_stmt|;
name|Resource
name|reservationResource
init|=
name|planFollower
operator|.
name|getReservationQueueResourceIfExists
argument_list|(
name|plan
argument_list|,
name|reservation
operator|.
name|getReservationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|reservationResource
operator|!=
literal|null
condition|)
block|{
name|resResource
operator|=
name|Resources
operator|.
name|subtract
argument_list|(
name|reservation
operator|.
name|getResourcesAtTime
argument_list|(
name|now
argument_list|)
argument_list|,
name|reservationResource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resResource
operator|=
name|reservation
operator|.
name|getResourcesAtTime
argument_list|(
name|now
argument_list|)
expr_stmt|;
block|}
return|return
name|resResource
return|;
block|}
annotation|@
name|Override
DECL|method|compare (ReservationAllocation lhs, ReservationAllocation rhs)
specifier|public
name|int
name|compare
parameter_list|(
name|ReservationAllocation
name|lhs
parameter_list|,
name|ReservationAllocation
name|rhs
parameter_list|)
block|{
comment|// compute delta between current and previous reservation, and compare
comment|// based on that
name|Resource
name|lhsRes
init|=
name|getUnallocatedReservedResources
argument_list|(
name|lhs
argument_list|)
decl_stmt|;
name|Resource
name|rhsRes
init|=
name|getUnallocatedReservedResources
argument_list|(
name|rhs
argument_list|)
decl_stmt|;
return|return
name|lhsRes
operator|.
name|compareTo
argument_list|(
name|rhsRes
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

