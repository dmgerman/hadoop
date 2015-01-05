begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
package|;
end_package

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
name|Iterator
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
name|scheduler
operator|.
name|SchedulerDynamicEditException
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
comment|/**  * This represents a dynamic queue managed by the {@link ReservationSystem}.  * From the user perspective this is equivalent to a LeafQueue that respect  * reservations, but functionality wise is a sub-class of ParentQueue  *  */
end_comment

begin_class
DECL|class|PlanQueue
specifier|public
class|class
name|PlanQueue
extends|extends
name|ParentQueue
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
name|PlanQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxAppsForReservation
specifier|private
name|int
name|maxAppsForReservation
decl_stmt|;
DECL|field|maxAppsPerUserForReservation
specifier|private
name|int
name|maxAppsPerUserForReservation
decl_stmt|;
DECL|field|userLimit
specifier|private
name|int
name|userLimit
decl_stmt|;
DECL|field|userLimitFactor
specifier|private
name|float
name|userLimitFactor
decl_stmt|;
DECL|field|schedulerContext
specifier|protected
name|CapacitySchedulerContext
name|schedulerContext
decl_stmt|;
DECL|field|showReservationsAsQueues
specifier|private
name|boolean
name|showReservationsAsQueues
decl_stmt|;
DECL|method|PlanQueue (CapacitySchedulerContext cs, String queueName, CSQueue parent, CSQueue old)
specifier|public
name|PlanQueue
parameter_list|(
name|CapacitySchedulerContext
name|cs
parameter_list|,
name|String
name|queueName
parameter_list|,
name|CSQueue
name|parent
parameter_list|,
name|CSQueue
name|old
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|cs
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|old
argument_list|)
expr_stmt|;
name|this
operator|.
name|schedulerContext
operator|=
name|cs
expr_stmt|;
comment|// Set the reservation queue attributes for the Plan
name|CapacitySchedulerConfiguration
name|conf
init|=
name|cs
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|String
name|queuePath
init|=
name|super
operator|.
name|getQueuePath
argument_list|()
decl_stmt|;
name|int
name|maxAppsForReservation
init|=
name|conf
operator|.
name|getMaximumApplicationsPerQueue
argument_list|(
name|queuePath
argument_list|)
decl_stmt|;
name|showReservationsAsQueues
operator|=
name|conf
operator|.
name|getShowReservationAsQueues
argument_list|(
name|queuePath
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxAppsForReservation
operator|<
literal|0
condition|)
block|{
name|maxAppsForReservation
operator|=
call|(
name|int
call|)
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS
operator|*
name|super
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|userLimit
init|=
name|conf
operator|.
name|getUserLimit
argument_list|(
name|queuePath
argument_list|)
decl_stmt|;
name|float
name|userLimitFactor
init|=
name|conf
operator|.
name|getUserLimitFactor
argument_list|(
name|queuePath
argument_list|)
decl_stmt|;
name|int
name|maxAppsPerUserForReservation
init|=
call|(
name|int
call|)
argument_list|(
name|maxAppsForReservation
operator|*
operator|(
name|userLimit
operator|/
literal|100.0f
operator|)
operator|*
name|userLimitFactor
argument_list|)
decl_stmt|;
name|updateQuotas
argument_list|(
name|userLimit
argument_list|,
name|userLimitFactor
argument_list|,
name|maxAppsForReservation
argument_list|,
name|maxAppsPerUserForReservation
argument_list|)
expr_stmt|;
name|StringBuffer
name|queueInfo
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|queueInfo
operator|.
name|append
argument_list|(
literal|"Created Plan Queue: "
argument_list|)
operator|.
name|append
argument_list|(
name|queueName
argument_list|)
operator|.
name|append
argument_list|(
literal|"\nwith capacity: ["
argument_list|)
operator|.
name|append
argument_list|(
name|super
operator|.
name|getCapacity
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\nwith max capacity: ["
argument_list|)
operator|.
name|append
argument_list|(
name|super
operator|.
name|getMaximumCapacity
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\nwith max reservation apps: ["
argument_list|)
operator|.
name|append
argument_list|(
name|maxAppsForReservation
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\nwith max reservation apps per user: ["
argument_list|)
operator|.
name|append
argument_list|(
name|maxAppsPerUserForReservation
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\nwith user limit: ["
argument_list|)
operator|.
name|append
argument_list|(
name|userLimit
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\nwith user limit factor: ["
argument_list|)
operator|.
name|append
argument_list|(
name|userLimitFactor
argument_list|)
operator|.
name|append
argument_list|(
literal|"]."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|queueInfo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reinitialize (CSQueue newlyParsedQueue, Resource clusterResource)
specifier|public
specifier|synchronized
name|void
name|reinitialize
parameter_list|(
name|CSQueue
name|newlyParsedQueue
parameter_list|,
name|Resource
name|clusterResource
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Sanity check
if|if
condition|(
operator|!
operator|(
name|newlyParsedQueue
operator|instanceof
name|PlanQueue
operator|)
operator|||
operator|!
name|newlyParsedQueue
operator|.
name|getQueuePath
argument_list|()
operator|.
name|equals
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Trying to reinitialize "
operator|+
name|getQueuePath
argument_list|()
operator|+
literal|" from "
operator|+
name|newlyParsedQueue
operator|.
name|getQueuePath
argument_list|()
argument_list|)
throw|;
block|}
name|PlanQueue
name|newlyParsedParentQueue
init|=
operator|(
name|PlanQueue
operator|)
name|newlyParsedQueue
decl_stmt|;
if|if
condition|(
name|newlyParsedParentQueue
operator|.
name|getChildQueues
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Reservable Queue should not have sub-queues in the"
operator|+
literal|"configuration"
argument_list|)
throw|;
block|}
comment|// Set new configs
name|setupQueueConfigs
argument_list|(
name|clusterResource
argument_list|,
name|newlyParsedParentQueue
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|newlyParsedParentQueue
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|,
name|newlyParsedParentQueue
operator|.
name|getMaximumCapacity
argument_list|()
argument_list|,
name|newlyParsedParentQueue
operator|.
name|getAbsoluteMaximumCapacity
argument_list|()
argument_list|,
name|newlyParsedParentQueue
operator|.
name|getState
argument_list|()
argument_list|,
name|newlyParsedParentQueue
operator|.
name|getACLs
argument_list|()
argument_list|,
name|newlyParsedParentQueue
operator|.
name|accessibleLabels
argument_list|,
name|newlyParsedParentQueue
operator|.
name|defaultLabelExpression
argument_list|,
name|newlyParsedParentQueue
operator|.
name|capacitiyByNodeLabels
argument_list|,
name|newlyParsedParentQueue
operator|.
name|maxCapacityByNodeLabels
argument_list|,
name|newlyParsedParentQueue
operator|.
name|getReservationContinueLooking
argument_list|()
argument_list|)
expr_stmt|;
name|updateQuotas
argument_list|(
name|newlyParsedParentQueue
operator|.
name|userLimit
argument_list|,
name|newlyParsedParentQueue
operator|.
name|userLimitFactor
argument_list|,
name|newlyParsedParentQueue
operator|.
name|maxAppsForReservation
argument_list|,
name|newlyParsedParentQueue
operator|.
name|maxAppsPerUserForReservation
argument_list|)
expr_stmt|;
comment|// run reinitialize on each existing queue, to trigger absolute cap
comment|// recomputations
for|for
control|(
name|CSQueue
name|res
range|:
name|this
operator|.
name|getChildQueues
argument_list|()
control|)
block|{
name|res
operator|.
name|reinitialize
argument_list|(
name|res
argument_list|,
name|clusterResource
argument_list|)
expr_stmt|;
block|}
name|showReservationsAsQueues
operator|=
name|newlyParsedParentQueue
operator|.
name|showReservationsAsQueues
expr_stmt|;
block|}
DECL|method|addChildQueue (CSQueue newQueue)
specifier|synchronized
name|void
name|addChildQueue
parameter_list|(
name|CSQueue
name|newQueue
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
block|{
if|if
condition|(
name|newQueue
operator|.
name|getCapacity
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Queue "
operator|+
name|newQueue
operator|+
literal|" being added has non zero capacity."
argument_list|)
throw|;
block|}
name|boolean
name|added
init|=
name|this
operator|.
name|childQueues
operator|.
name|add
argument_list|(
name|newQueue
argument_list|)
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
literal|"updateChildQueues (action: add queue): "
operator|+
name|added
operator|+
literal|" "
operator|+
name|getChildQueuesToPrint
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|removeChildQueue (CSQueue remQueue)
specifier|synchronized
name|void
name|removeChildQueue
parameter_list|(
name|CSQueue
name|remQueue
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
block|{
if|if
condition|(
name|remQueue
operator|.
name|getCapacity
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Queue "
operator|+
name|remQueue
operator|+
literal|" being removed has non zero capacity."
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|CSQueue
argument_list|>
name|qiter
init|=
name|childQueues
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|qiter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|CSQueue
name|cs
init|=
name|qiter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|cs
operator|.
name|equals
argument_list|(
name|remQueue
argument_list|)
condition|)
block|{
name|qiter
operator|.
name|remove
argument_list|()
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
literal|"Removed child queue: {}"
argument_list|,
name|cs
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|sumOfChildCapacities ()
specifier|protected
specifier|synchronized
name|float
name|sumOfChildCapacities
parameter_list|()
block|{
name|float
name|ret
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CSQueue
name|l
range|:
name|childQueues
control|)
block|{
name|ret
operator|+=
name|l
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|updateQuotas (int userLimit, float userLimitFactor, int maxAppsForReservation, int maxAppsPerUserForReservation)
specifier|private
name|void
name|updateQuotas
parameter_list|(
name|int
name|userLimit
parameter_list|,
name|float
name|userLimitFactor
parameter_list|,
name|int
name|maxAppsForReservation
parameter_list|,
name|int
name|maxAppsPerUserForReservation
parameter_list|)
block|{
name|this
operator|.
name|userLimit
operator|=
name|userLimit
expr_stmt|;
name|this
operator|.
name|userLimitFactor
operator|=
name|userLimitFactor
expr_stmt|;
name|this
operator|.
name|maxAppsForReservation
operator|=
name|maxAppsForReservation
expr_stmt|;
name|this
operator|.
name|maxAppsPerUserForReservation
operator|=
name|maxAppsPerUserForReservation
expr_stmt|;
block|}
comment|/**    * Number of maximum applications for each of the reservations in this Plan.    *    * @return maxAppsForreservation    */
DECL|method|getMaxApplicationsForReservations ()
specifier|public
name|int
name|getMaxApplicationsForReservations
parameter_list|()
block|{
return|return
name|maxAppsForReservation
return|;
block|}
comment|/**    * Number of maximum applications per user for each of the reservations in    * this Plan.    *    * @return maxAppsPerUserForreservation    */
DECL|method|getMaxApplicationsPerUserForReservation ()
specifier|public
name|int
name|getMaxApplicationsPerUserForReservation
parameter_list|()
block|{
return|return
name|maxAppsPerUserForReservation
return|;
block|}
comment|/**    * User limit value for each of the reservations in this Plan.    *    * @return userLimit    */
DECL|method|getUserLimitForReservation ()
specifier|public
name|int
name|getUserLimitForReservation
parameter_list|()
block|{
return|return
name|userLimit
return|;
block|}
comment|/**    * User limit factor value for each of the reservations in this Plan.    *    * @return userLimitFactor    */
DECL|method|getUserLimitFactor ()
specifier|public
name|float
name|getUserLimitFactor
parameter_list|()
block|{
return|return
name|userLimitFactor
return|;
block|}
comment|/**    * Determine whether to hide/show the ReservationQueues    */
DECL|method|showReservationsAsQueues ()
specifier|public
name|boolean
name|showReservationsAsQueues
parameter_list|()
block|{
return|return
name|showReservationsAsQueues
return|;
block|}
block|}
end_class

end_unit

