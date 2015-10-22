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
name|LimitedPrivate
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
name|service
operator|.
name|AbstractService
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
name|util
operator|.
name|ReflectionUtils
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
name|proto
operator|.
name|YarnServerResourceManagerRecoveryProtos
operator|.
name|ReservationAllocationStateProto
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
name|ResourceManager
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
operator|.
name|RMState
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
name|reservation
operator|.
name|planning
operator|.
name|Planner
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
name|planning
operator|.
name|ReservationAgent
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
name|QueueMetrics
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
name|fair
operator|.
name|FairScheduler
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
name|UTCClock
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
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
name|ScheduledThreadPoolExecutor
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicLong
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_comment
comment|/**  * This is the implementation of {@link ReservationSystem} based on the  * {@link ResourceScheduler}  */
end_comment

begin_class
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Unstable
DECL|class|AbstractReservationSystem
specifier|public
specifier|abstract
class|class
name|AbstractReservationSystem
extends|extends
name|AbstractService
implements|implements
name|ReservationSystem
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
name|AbstractReservationSystem
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// private static final String DEFAULT_CAPACITY_SCHEDULER_PLAN
DECL|field|readWriteLock
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|readLock
specifier|private
specifier|final
name|Lock
name|readLock
init|=
name|readWriteLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|Lock
name|writeLock
init|=
name|readWriteLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
DECL|field|initialized
specifier|private
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
init|=
operator|new
name|UTCClock
argument_list|()
decl_stmt|;
DECL|field|resCounter
specifier|private
name|AtomicLong
name|resCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|plans
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Plan
argument_list|>
name|plans
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Plan
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|resQMap
specifier|private
name|Map
argument_list|<
name|ReservationId
argument_list|,
name|String
argument_list|>
name|resQMap
init|=
operator|new
name|HashMap
argument_list|<
name|ReservationId
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ResourceScheduler
name|scheduler
decl_stmt|;
DECL|field|scheduledExecutorService
specifier|private
name|ScheduledExecutorService
name|scheduledExecutorService
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|planStepSize
specifier|protected
name|long
name|planStepSize
decl_stmt|;
DECL|field|planFollower
specifier|private
name|PlanFollower
name|planFollower
decl_stmt|;
DECL|field|isRecoveryEnabled
specifier|private
name|boolean
name|isRecoveryEnabled
init|=
literal|false
decl_stmt|;
comment|/**    * Construct the service.    *     * @param name service name    */
DECL|method|AbstractReservationSystem (String name)
specifier|public
name|AbstractReservationSystem
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setRMContext (RMContext rmContext)
specifier|public
name|void
name|setRMContext
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reinitialize (Configuration conf, RMContext rmContext)
specifier|public
name|void
name|reinitialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|YarnException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|initialized
condition|)
block|{
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|initializeNewPlans
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initialize (Configuration conf)
specifier|private
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing Reservation system"
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|scheduler
operator|=
name|rmContext
operator|.
name|getScheduler
argument_list|()
expr_stmt|;
comment|// Get the plan step size
name|planStepSize
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESERVATION_SYSTEM_PLAN_FOLLOWER_TIME_STEP
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESERVATION_SYSTEM_PLAN_FOLLOWER_TIME_STEP
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|planStepSize
operator|<
literal|0
condition|)
block|{
name|planStepSize
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESERVATION_SYSTEM_PLAN_FOLLOWER_TIME_STEP
expr_stmt|;
block|}
comment|// Create a plan corresponding to every reservable queue
name|Set
argument_list|<
name|String
argument_list|>
name|planQueueNames
init|=
name|scheduler
operator|.
name|getPlanQueues
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|planQueueName
range|:
name|planQueueNames
control|)
block|{
name|Plan
name|plan
init|=
name|initializePlan
argument_list|(
name|planQueueName
argument_list|)
decl_stmt|;
name|plans
operator|.
name|put
argument_list|(
name|planQueueName
argument_list|,
name|plan
argument_list|)
expr_stmt|;
block|}
name|isRecoveryEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RECOVERY_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RECOVERY_ENABLED
argument_list|)
expr_stmt|;
block|}
DECL|method|loadPlan (String planName, Map<ReservationId, ReservationAllocationStateProto> reservations)
specifier|private
name|void
name|loadPlan
parameter_list|(
name|String
name|planName
parameter_list|,
name|Map
argument_list|<
name|ReservationId
argument_list|,
name|ReservationAllocationStateProto
argument_list|>
name|reservations
parameter_list|)
throws|throws
name|PlanningException
block|{
name|Plan
name|plan
init|=
name|plans
operator|.
name|get
argument_list|(
name|planName
argument_list|)
decl_stmt|;
name|Resource
name|minAllocation
init|=
name|getMinAllocation
argument_list|()
decl_stmt|;
name|ResourceCalculator
name|rescCalculator
init|=
name|getResourceCalculator
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ReservationId
argument_list|,
name|ReservationAllocationStateProto
argument_list|>
name|currentReservation
range|:
name|reservations
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|plan
operator|.
name|addReservation
argument_list|(
name|ReservationSystemUtil
operator|.
name|toInMemoryAllocation
argument_list|(
name|planName
argument_list|,
name|currentReservation
operator|.
name|getKey
argument_list|()
argument_list|,
name|currentReservation
operator|.
name|getValue
argument_list|()
argument_list|,
name|minAllocation
argument_list|,
name|rescCalculator
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|resQMap
operator|.
name|put
argument_list|(
name|currentReservation
operator|.
name|getKey
argument_list|()
argument_list|,
name|planName
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovered reservations for Plan: {}"
argument_list|,
name|planName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recover (RMState state)
specifier|public
name|void
name|recover
parameter_list|(
name|RMState
name|state
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering Reservation system"
argument_list|)
expr_stmt|;
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|ReservationId
argument_list|,
name|ReservationAllocationStateProto
argument_list|>
argument_list|>
name|reservationSystemState
init|=
name|state
operator|.
name|getReservationState
argument_list|()
decl_stmt|;
if|if
condition|(
name|planFollower
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|plan
range|:
name|plans
operator|.
name|keySet
argument_list|()
control|)
block|{
comment|// recover reservations if any from state store
if|if
condition|(
name|reservationSystemState
operator|.
name|containsKey
argument_list|(
name|plan
argument_list|)
condition|)
block|{
name|loadPlan
argument_list|(
name|plan
argument_list|,
name|reservationSystemState
operator|.
name|get
argument_list|(
name|plan
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|synchronizePlan
argument_list|(
name|plan
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|startPlanFollower
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initializeNewPlans (Configuration conf)
specifier|private
name|void
name|initializeNewPlans
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Refreshing Reservation system"
argument_list|)
expr_stmt|;
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Create a plan corresponding to every new reservable queue
name|Set
argument_list|<
name|String
argument_list|>
name|planQueueNames
init|=
name|scheduler
operator|.
name|getPlanQueues
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|planQueueName
range|:
name|planQueueNames
control|)
block|{
if|if
condition|(
operator|!
name|plans
operator|.
name|containsKey
argument_list|(
name|planQueueName
argument_list|)
condition|)
block|{
name|Plan
name|plan
init|=
name|initializePlan
argument_list|(
name|planQueueName
argument_list|)
decl_stmt|;
name|plans
operator|.
name|put
argument_list|(
name|planQueueName
argument_list|,
name|plan
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Plan based on reservation queue {} already exists."
argument_list|,
name|planQueueName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Update the plan follower with the active plans
if|if
condition|(
name|planFollower
operator|!=
literal|null
condition|)
block|{
name|planFollower
operator|.
name|setPlans
argument_list|(
name|plans
operator|.
name|values
argument_list|()
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
literal|"Exception while trying to refresh reservable queues"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createPlanFollower ()
specifier|private
name|PlanFollower
name|createPlanFollower
parameter_list|()
block|{
name|String
name|planFollowerPolicyClassName
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESERVATION_SYSTEM_PLAN_FOLLOWER
argument_list|,
name|getDefaultPlanFollower
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|planFollowerPolicyClassName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Using PlanFollowerPolicy: "
operator|+
name|planFollowerPolicyClassName
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|planFollowerPolicyClazz
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|planFollowerPolicyClassName
argument_list|)
decl_stmt|;
if|if
condition|(
name|PlanFollower
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|planFollowerPolicyClazz
argument_list|)
condition|)
block|{
return|return
operator|(
name|PlanFollower
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|planFollowerPolicyClazz
argument_list|,
name|conf
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Class: "
operator|+
name|planFollowerPolicyClassName
operator|+
literal|" not instance of "
operator|+
name|PlanFollower
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Could not instantiate PlanFollowerPolicy: "
operator|+
name|planFollowerPolicyClassName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getDefaultPlanFollower ()
specifier|private
name|String
name|getDefaultPlanFollower
parameter_list|()
block|{
comment|// currently only capacity scheduler is supported
if|if
condition|(
name|scheduler
operator|instanceof
name|CapacityScheduler
condition|)
block|{
return|return
name|CapacitySchedulerPlanFollower
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|scheduler
operator|instanceof
name|FairScheduler
condition|)
block|{
return|return
name|FairSchedulerPlanFollower
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getPlan (String planName)
specifier|public
name|Plan
name|getPlan
parameter_list|(
name|String
name|planName
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|plans
operator|.
name|get
argument_list|(
name|planName
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * @return the planStepSize    */
annotation|@
name|Override
DECL|method|getPlanFollowerTimeStep ()
specifier|public
name|long
name|getPlanFollowerTimeStep
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|planStepSize
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|synchronizePlan (String planName, boolean shouldReplan)
specifier|public
name|void
name|synchronizePlan
parameter_list|(
name|String
name|planName
parameter_list|,
name|boolean
name|shouldReplan
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Plan
name|plan
init|=
name|plans
operator|.
name|get
argument_list|(
name|planName
argument_list|)
decl_stmt|;
if|if
condition|(
name|plan
operator|!=
literal|null
condition|)
block|{
name|planFollower
operator|.
name|synchronizePlan
argument_list|(
name|plan
argument_list|,
name|shouldReplan
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|startPlanFollower (long initialDelay)
specifier|private
name|void
name|startPlanFollower
parameter_list|(
name|long
name|initialDelay
parameter_list|)
block|{
if|if
condition|(
name|planFollower
operator|!=
literal|null
condition|)
block|{
name|scheduledExecutorService
operator|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|scheduledExecutorService
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|planFollower
argument_list|,
name|initialDelay
argument_list|,
name|planStepSize
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|reinitialize
argument_list|(
name|configuration
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
comment|// Create the plan follower with the active plans
name|planFollower
operator|=
name|createPlanFollower
argument_list|()
expr_stmt|;
if|if
condition|(
name|planFollower
operator|!=
literal|null
condition|)
block|{
name|planFollower
operator|.
name|init
argument_list|(
name|clock
argument_list|,
name|scheduler
argument_list|,
name|plans
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isRecoveryEnabled
condition|)
block|{
name|startPlanFollower
argument_list|(
name|planStepSize
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
block|{
comment|// Stop the plan follower
if|if
condition|(
name|scheduledExecutorService
operator|!=
literal|null
operator|&&
operator|!
name|scheduledExecutorService
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
name|scheduledExecutorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// Clear the plans
name|plans
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQueueForReservation (ReservationId reservationId)
specifier|public
name|String
name|getQueueForReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|resQMap
operator|.
name|get
argument_list|(
name|reservationId
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setQueueForReservation (ReservationId reservationId, String queueName)
specifier|public
name|void
name|setQueueForReservation
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|,
name|String
name|queueName
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|resQMap
operator|.
name|put
argument_list|(
name|reservationId
argument_list|,
name|queueName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNewReservationId ()
specifier|public
name|ReservationId
name|getNewReservationId
parameter_list|()
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ReservationId
name|resId
init|=
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|ResourceManager
operator|.
name|getClusterTimeStamp
argument_list|()
argument_list|,
name|resCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allocated new reservationId: "
operator|+
name|resId
argument_list|)
expr_stmt|;
return|return
name|resId
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAllPlans ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Plan
argument_list|>
name|getAllPlans
parameter_list|()
block|{
return|return
name|plans
return|;
block|}
comment|/**    * Get the default reservation system corresponding to the scheduler    *     * @param scheduler the scheduler for which the reservation system is required    */
DECL|method|getDefaultReservationSystem (ResourceScheduler scheduler)
specifier|public
specifier|static
name|String
name|getDefaultReservationSystem
parameter_list|(
name|ResourceScheduler
name|scheduler
parameter_list|)
block|{
if|if
condition|(
name|scheduler
operator|instanceof
name|CapacityScheduler
condition|)
block|{
return|return
name|CapacityReservationSystem
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|scheduler
operator|instanceof
name|FairScheduler
condition|)
block|{
return|return
name|FairReservationSystem
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|initializePlan (String planQueueName)
specifier|protected
name|Plan
name|initializePlan
parameter_list|(
name|String
name|planQueueName
parameter_list|)
throws|throws
name|YarnException
block|{
name|String
name|planQueuePath
init|=
name|getPlanQueuePath
argument_list|(
name|planQueueName
argument_list|)
decl_stmt|;
name|SharingPolicy
name|adPolicy
init|=
name|getAdmissionPolicy
argument_list|(
name|planQueuePath
argument_list|)
decl_stmt|;
name|adPolicy
operator|.
name|init
argument_list|(
name|planQueuePath
argument_list|,
name|getReservationSchedulerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
comment|// Calculate the max plan capacity
name|Resource
name|minAllocation
init|=
name|getMinAllocation
argument_list|()
decl_stmt|;
name|Resource
name|maxAllocation
init|=
name|getMaxAllocation
argument_list|()
decl_stmt|;
name|ResourceCalculator
name|rescCalc
init|=
name|getResourceCalculator
argument_list|()
decl_stmt|;
name|Resource
name|totCap
init|=
name|getPlanQueueCapacity
argument_list|(
name|planQueueName
argument_list|)
decl_stmt|;
name|Plan
name|plan
init|=
operator|new
name|InMemoryPlan
argument_list|(
name|getRootQueueMetrics
argument_list|()
argument_list|,
name|adPolicy
argument_list|,
name|getAgent
argument_list|(
name|planQueuePath
argument_list|)
argument_list|,
name|totCap
argument_list|,
name|planStepSize
argument_list|,
name|rescCalc
argument_list|,
name|minAllocation
argument_list|,
name|maxAllocation
argument_list|,
name|planQueueName
argument_list|,
name|getReplanner
argument_list|(
name|planQueuePath
argument_list|)
argument_list|,
name|getReservationSchedulerConfiguration
argument_list|()
operator|.
name|getMoveOnExpiry
argument_list|(
name|planQueuePath
argument_list|)
argument_list|,
name|rmContext
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Intialized plan {} based on reservable queue {}"
argument_list|,
name|plan
operator|.
name|toString
argument_list|()
argument_list|,
name|planQueueName
argument_list|)
expr_stmt|;
return|return
name|plan
return|;
block|}
DECL|method|getReplanner (String planQueueName)
specifier|protected
name|Planner
name|getReplanner
parameter_list|(
name|String
name|planQueueName
parameter_list|)
block|{
name|ReservationSchedulerConfiguration
name|reservationConfig
init|=
name|getReservationSchedulerConfiguration
argument_list|()
decl_stmt|;
name|String
name|plannerClassName
init|=
name|reservationConfig
operator|.
name|getReplanner
argument_list|(
name|planQueueName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using Replanner: "
operator|+
name|plannerClassName
operator|+
literal|" for queue: "
operator|+
name|planQueueName
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|plannerClazz
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|plannerClassName
argument_list|)
decl_stmt|;
if|if
condition|(
name|Planner
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|plannerClazz
argument_list|)
condition|)
block|{
name|Planner
name|planner
init|=
operator|(
name|Planner
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|plannerClazz
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|planner
operator|.
name|init
argument_list|(
name|planQueueName
argument_list|,
name|reservationConfig
argument_list|)
expr_stmt|;
return|return
name|planner
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Class: "
operator|+
name|plannerClazz
operator|+
literal|" not instance of "
operator|+
name|Planner
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Could not instantiate Planner: "
operator|+
name|plannerClassName
operator|+
literal|" for queue: "
operator|+
name|planQueueName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getAgent (String queueName)
specifier|protected
name|ReservationAgent
name|getAgent
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|ReservationSchedulerConfiguration
name|reservationConfig
init|=
name|getReservationSchedulerConfiguration
argument_list|()
decl_stmt|;
name|String
name|agentClassName
init|=
name|reservationConfig
operator|.
name|getReservationAgent
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using Agent: "
operator|+
name|agentClassName
operator|+
literal|" for queue: "
operator|+
name|queueName
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|agentClazz
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|agentClassName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ReservationAgent
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|agentClazz
argument_list|)
condition|)
block|{
return|return
operator|(
name|ReservationAgent
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|agentClazz
argument_list|,
name|conf
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Class: "
operator|+
name|agentClassName
operator|+
literal|" not instance of "
operator|+
name|ReservationAgent
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Could not instantiate Agent: "
operator|+
name|agentClassName
operator|+
literal|" for queue: "
operator|+
name|queueName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getAdmissionPolicy (String queueName)
specifier|protected
name|SharingPolicy
name|getAdmissionPolicy
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|ReservationSchedulerConfiguration
name|reservationConfig
init|=
name|getReservationSchedulerConfiguration
argument_list|()
decl_stmt|;
name|String
name|admissionPolicyClassName
init|=
name|reservationConfig
operator|.
name|getReservationAdmissionPolicy
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using AdmissionPolicy: "
operator|+
name|admissionPolicyClassName
operator|+
literal|" for queue: "
operator|+
name|queueName
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|admissionPolicyClazz
init|=
name|conf
operator|.
name|getClassByName
argument_list|(
name|admissionPolicyClassName
argument_list|)
decl_stmt|;
if|if
condition|(
name|SharingPolicy
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|admissionPolicyClazz
argument_list|)
condition|)
block|{
return|return
operator|(
name|SharingPolicy
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|admissionPolicyClazz
argument_list|,
name|conf
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Class: "
operator|+
name|admissionPolicyClassName
operator|+
literal|" not instance of "
operator|+
name|SharingPolicy
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Could not instantiate AdmissionPolicy: "
operator|+
name|admissionPolicyClassName
operator|+
literal|" for queue: "
operator|+
name|queueName
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|abstract
name|ReservationSchedulerConfiguration
DECL|method|getReservationSchedulerConfiguration ()
name|getReservationSchedulerConfiguration
parameter_list|()
function_decl|;
DECL|method|getPlanQueuePath (String planQueueName)
specifier|protected
specifier|abstract
name|String
name|getPlanQueuePath
parameter_list|(
name|String
name|planQueueName
parameter_list|)
function_decl|;
DECL|method|getPlanQueueCapacity (String planQueueName)
specifier|protected
specifier|abstract
name|Resource
name|getPlanQueueCapacity
parameter_list|(
name|String
name|planQueueName
parameter_list|)
function_decl|;
DECL|method|getMinAllocation ()
specifier|protected
specifier|abstract
name|Resource
name|getMinAllocation
parameter_list|()
function_decl|;
DECL|method|getMaxAllocation ()
specifier|protected
specifier|abstract
name|Resource
name|getMaxAllocation
parameter_list|()
function_decl|;
DECL|method|getResourceCalculator ()
specifier|protected
specifier|abstract
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
function_decl|;
DECL|method|getRootQueueMetrics ()
specifier|protected
specifier|abstract
name|QueueMetrics
name|getRootQueueMetrics
parameter_list|()
function_decl|;
block|}
end_class

end_unit

