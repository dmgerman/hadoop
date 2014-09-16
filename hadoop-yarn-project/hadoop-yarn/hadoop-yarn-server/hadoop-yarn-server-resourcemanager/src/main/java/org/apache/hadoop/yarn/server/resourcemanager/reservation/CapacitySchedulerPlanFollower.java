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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|SchedulerDynamicEditException
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
name|CSQueue
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
name|CapacityScheduler
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
name|PlanQueue
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
name|ReservationQueue
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

begin_comment
comment|/**  * This class implements a {@link PlanFollower}. This is invoked on a timer, and  * it is in charge to publish the state of the {@link Plan}s to the underlying  * {@link CapacityScheduler}. This implementation does so, by  * adding/removing/resizing leaf queues in the scheduler, thus affecting the  * dynamic behavior of the scheduler in a way that is consistent with the  * content of the plan. It also updates the plan's view on how much resources  * are available in the cluster.  *   * This implementation of PlanFollower is relatively stateless, and it can  * synchronize schedulers and Plans that have arbitrary changes (performing set  * differences among existing queues). This makes it resilient to frequency of  * synchronization, and RM restart issues (no "catch up" is necessary).  */
end_comment

begin_class
DECL|class|CapacitySchedulerPlanFollower
specifier|public
class|class
name|CapacitySchedulerPlanFollower
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
name|CapacitySchedulerPlanFollower
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|plans
specifier|private
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
DECL|field|clock
specifier|private
name|Clock
name|clock
decl_stmt|;
DECL|field|scheduler
specifier|private
name|CapacityScheduler
name|scheduler
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing Plan Follower Policy:"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|sched
operator|instanceof
name|CapacityScheduler
operator|)
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"CapacitySchedulerPlanFollower can only work with CapacityScheduler"
argument_list|)
throw|;
block|}
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
operator|(
name|CapacityScheduler
operator|)
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
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|synchronizePlan (Plan plan)
specifier|public
specifier|synchronized
name|void
name|synchronizePlan
parameter_list|(
name|Plan
name|plan
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
literal|"Running plan follower edit policy for plan: "
operator|+
name|planQueueName
argument_list|)
expr_stmt|;
block|}
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
name|CSQueue
name|queue
init|=
name|scheduler
operator|.
name|getQueue
argument_list|(
name|planQueueName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|queue
operator|instanceof
name|PlanQueue
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The Plan is not an PlanQueue!"
argument_list|)
expr_stmt|;
return|return;
block|}
name|PlanQueue
name|planQueue
init|=
operator|(
name|PlanQueue
operator|)
name|queue
decl_stmt|;
comment|// first we publish to the plan the current availability of resources
name|Resource
name|clusterResources
init|=
name|scheduler
operator|.
name|getClusterResource
argument_list|()
decl_stmt|;
name|float
name|planAbsCap
init|=
name|planQueue
operator|.
name|getAbsoluteCapacity
argument_list|()
decl_stmt|;
name|Resource
name|planResources
init|=
name|Resources
operator|.
name|multiply
argument_list|(
name|clusterResources
argument_list|,
name|planAbsCap
argument_list|)
decl_stmt|;
name|plan
operator|.
name|setTotalCapacity
argument_list|(
name|planResources
argument_list|)
expr_stmt|;
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
comment|// create the default reservation queue if it doesnt exist
name|String
name|defReservationQueue
init|=
name|planQueueName
operator|+
name|PlanQueue
operator|.
name|DEFAULT_QUEUE_SUFFIX
decl_stmt|;
if|if
condition|(
name|scheduler
operator|.
name|getQueue
argument_list|(
name|defReservationQueue
argument_list|)
operator|==
literal|null
condition|)
block|{
name|ReservationQueue
name|defQueue
init|=
operator|new
name|ReservationQueue
argument_list|(
name|scheduler
argument_list|,
name|defReservationQueue
argument_list|,
name|planQueue
argument_list|)
decl_stmt|;
try|try
block|{
name|scheduler
operator|.
name|addQueue
argument_list|(
name|defQueue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerDynamicEditException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while trying to create default reservation queue for plan: {}"
argument_list|,
name|planQueueName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|curReservationNames
operator|.
name|add
argument_list|(
name|defReservationQueue
argument_list|)
expr_stmt|;
comment|// if the resources dedicated to this plan has shrunk invoke replanner
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|scheduler
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|clusterResources
argument_list|,
name|reservedResources
argument_list|,
name|planResources
argument_list|)
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
comment|// identify the reservations that have expired and new reservations that
comment|// have to be activated
name|List
argument_list|<
name|CSQueue
argument_list|>
name|resQueues
init|=
name|planQueue
operator|.
name|getChildQueues
argument_list|()
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
name|CSQueue
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
if|if
condition|(
name|curReservationNames
operator|.
name|contains
argument_list|(
name|resQueueName
argument_list|)
condition|)
block|{
comment|// it is already existing reservation, so needed not create new
comment|// reservation queue
name|curReservationNames
operator|.
name|remove
argument_list|(
name|resQueueName
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
name|resQueueName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// garbage collect expired reservations
name|cleanupExpiredQueues
argument_list|(
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
name|scheduler
operator|.
name|setEntitlement
argument_list|(
name|defReservationQueue
argument_list|,
operator|new
name|QueueEntitlement
argument_list|(
literal|0f
argument_list|,
literal|1.0f
argument_list|)
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
comment|// one asking for the most
comment|// avoid order-of-operation errors that temporarily violate 100%
comment|// capacity bound
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
name|ReservationQueue
name|resQueue
init|=
operator|new
name|ReservationQueue
argument_list|(
name|scheduler
argument_list|,
name|currResId
argument_list|,
name|planQueue
argument_list|)
decl_stmt|;
try|try
block|{
name|scheduler
operator|.
name|addQueue
argument_list|(
name|resQueue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerDynamicEditException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while trying to activate reservation: {} for plan: {}"
argument_list|,
name|currResId
argument_list|,
name|planQueueName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
name|getMemory
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
name|targetCapacity
operator|=
name|Resources
operator|.
name|divide
argument_list|(
name|scheduler
operator|.
name|getResourceCalculator
argument_list|()
argument_list|,
name|clusterResources
argument_list|,
name|capToAssign
argument_list|,
name|planResources
argument_list|)
expr_stmt|;
block|}
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
literal|"Assigning capacity of {} to queue {} with target capacity {}"
argument_list|,
name|capToAssign
argument_list|,
name|currResId
argument_list|,
name|targetCapacity
argument_list|)
expr_stmt|;
block|}
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
name|scheduler
operator|.
name|setEntitlement
argument_list|(
name|currResId
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
block|}
comment|// set the default queue to eat-up all remaining capacity
try|try
block|{
name|scheduler
operator|.
name|setEntitlement
argument_list|(
name|defReservationQueue
argument_list|,
operator|new
name|QueueEntitlement
argument_list|(
name|defQCap
argument_list|,
literal|1.0f
argument_list|)
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
literal|"Encountered unexpected error during migration of application: {} from reservation: {}"
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
comment|/**    * First sets entitlement of queues to zero to prevent new app submission.    * Then move all apps in the set of queues to the parent plan queue's default    * reservation queue if move is enabled. Finally cleanups the queue by killing    * any apps (if move is disabled or move failed) and removing the queue    */
DECL|method|cleanupExpiredQueues (boolean shouldMove, Set<String> toRemove, String defReservationQueue)
specifier|private
name|void
name|cleanupExpiredQueues
parameter_list|(
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
name|expiredReservation
range|:
name|toRemove
control|)
block|{
try|try
block|{
comment|// reduce entitlement to 0
name|scheduler
operator|.
name|setEntitlement
argument_list|(
name|expiredReservation
argument_list|,
operator|new
name|QueueEntitlement
argument_list|(
literal|0.0f
argument_list|,
literal|0.0f
argument_list|)
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
if|if
condition|(
name|scheduler
operator|.
name|getAppsInQueue
argument_list|(
name|expiredReservation
argument_list|)
operator|.
name|size
argument_list|()
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
name|expiredReservation
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Sort in the order from the least new amount of resources asked (likely    * negative) to the highest. This prevents "order-of-operation" errors related    * to exceeding 100% capacity temporarily.    */
DECL|method|sortByDelta ( List<ReservationAllocation> currentReservations, long now)
specifier|private
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
name|scheduler
argument_list|,
name|now
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentReservations
return|;
block|}
DECL|class|ReservationAllocationComparator
specifier|private
class|class
name|ReservationAllocationComparator
implements|implements
name|Comparator
argument_list|<
name|ReservationAllocation
argument_list|>
block|{
DECL|field|scheduler
name|CapacityScheduler
name|scheduler
decl_stmt|;
DECL|field|now
name|long
name|now
decl_stmt|;
DECL|method|ReservationAllocationComparator (CapacityScheduler scheduler, long now)
name|ReservationAllocationComparator
parameter_list|(
name|CapacityScheduler
name|scheduler
parameter_list|,
name|long
name|now
parameter_list|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|now
operator|=
name|now
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
name|CSQueue
name|resQueue
init|=
name|scheduler
operator|.
name|getQueue
argument_list|(
name|reservation
operator|.
name|getReservationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|resQueue
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
name|Resources
operator|.
name|multiply
argument_list|(
name|scheduler
operator|.
name|getClusterResource
argument_list|()
argument_list|,
name|resQueue
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|)
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

