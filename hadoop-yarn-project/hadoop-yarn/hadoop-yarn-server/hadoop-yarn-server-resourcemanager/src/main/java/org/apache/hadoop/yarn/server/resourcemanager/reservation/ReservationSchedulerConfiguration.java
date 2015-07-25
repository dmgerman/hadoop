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
name|reservation
operator|.
name|planning
operator|.
name|Planner
import|;
end_import

begin_class
DECL|class|ReservationSchedulerConfiguration
specifier|public
specifier|abstract
class|class
name|ReservationSchedulerConfiguration
extends|extends
name|Configuration
block|{
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|DEFAULT_RESERVATION_WINDOW
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_RESERVATION_WINDOW
init|=
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 day in msec
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|DEFAULT_RESERVATION_ADMISSION_POLICY
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RESERVATION_ADMISSION_POLICY
init|=
literal|"org.apache.hadoop.yarn.server.resourcemanager.reservation.CapacityOverTimePolicy"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|DEFAULT_RESERVATION_AGENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RESERVATION_AGENT_NAME
init|=
literal|"org.apache.hadoop.yarn.server.resourcemanager.reservation.planning.AlignedPlannerWithGreedy"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|DEFAULT_RESERVATION_PLANNER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_RESERVATION_PLANNER_NAME
init|=
literal|"org.apache.hadoop.yarn.server.resourcemanager.reservation.planning.SimpleCapacityReplanner"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|DEFAULT_RESERVATION_MOVE_ON_EXPIRY
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_RESERVATION_MOVE_ON_EXPIRY
init|=
literal|true
decl_stmt|;
comment|// default to 1h lookahead enforcement
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|DEFAULT_RESERVATION_ENFORCEMENT_WINDOW
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_RESERVATION_ENFORCEMENT_WINDOW
init|=
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 hour
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|DEFAULT_SHOW_RESERVATIONS_AS_QUEUES
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_SHOW_RESERVATIONS_AS_QUEUES
init|=
literal|false
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|DEFAULT_CAPACITY_OVER_TIME_MULTIPLIER
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_CAPACITY_OVER_TIME_MULTIPLIER
init|=
literal|1
decl_stmt|;
DECL|method|ReservationSchedulerConfiguration ()
specifier|public
name|ReservationSchedulerConfiguration
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|ReservationSchedulerConfiguration ( Configuration configuration)
specifier|public
name|ReservationSchedulerConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks if the queue participates in reservation based scheduling    * @param queue    * @return true if the queue participates in reservation based scheduling    */
DECL|method|isReservable (String queue)
specifier|public
specifier|abstract
name|boolean
name|isReservable
parameter_list|(
name|String
name|queue
parameter_list|)
function_decl|;
comment|/**    * Gets the length of time in milliseconds for which the {@link SharingPolicy}    * checks for validity    * @param queue name of the queue    * @return length in time in milliseconds for which to check the    * {@link SharingPolicy}    */
DECL|method|getReservationWindow (String queue)
specifier|public
name|long
name|getReservationWindow
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|DEFAULT_RESERVATION_WINDOW
return|;
block|}
comment|/**    * Gets the average allowed capacity which will aggregated over the    * {@link ReservationSchedulerConfiguration#getReservationWindow} by the    * the {@link SharingPolicy} to check aggregate used capacity    * @param queue name of the queue    * @return average capacity allowed by the {@link SharingPolicy}    */
DECL|method|getAverageCapacity (String queue)
specifier|public
name|float
name|getAverageCapacity
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|DEFAULT_CAPACITY_OVER_TIME_MULTIPLIER
return|;
block|}
comment|/**    * Gets the maximum capacity at any time that the {@link SharingPolicy} allows    * @param queue name of the queue    * @return maximum allowed capacity at any time    */
DECL|method|getInstantaneousMaxCapacity (String queue)
specifier|public
name|float
name|getInstantaneousMaxCapacity
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|DEFAULT_CAPACITY_OVER_TIME_MULTIPLIER
return|;
block|}
comment|/**    * Gets the name of the {@link SharingPolicy} class associated with the queue    * @param queue name of the queue    * @return the class name of the {@link SharingPolicy}    */
DECL|method|getReservationAdmissionPolicy (String queue)
specifier|public
name|String
name|getReservationAdmissionPolicy
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|DEFAULT_RESERVATION_ADMISSION_POLICY
return|;
block|}
comment|/**    * Gets the name of the {@link ReservationAgent} class associated with the    * queue    * @param queue name of the queue    * @return the class name of the {@link ReservationAgent}    */
DECL|method|getReservationAgent (String queue)
specifier|public
name|String
name|getReservationAgent
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|DEFAULT_RESERVATION_AGENT_NAME
return|;
block|}
comment|/**    * Checks whether the reservation queues be hidden or visible    * @param queuePath name of the queue    * @return true if reservation queues should be visible    */
DECL|method|getShowReservationAsQueues (String queuePath)
specifier|public
name|boolean
name|getShowReservationAsQueues
parameter_list|(
name|String
name|queuePath
parameter_list|)
block|{
return|return
name|DEFAULT_SHOW_RESERVATIONS_AS_QUEUES
return|;
block|}
comment|/**    * Gets the name of the {@link Planner} class associated with the    * queue    * @param queue name of the queue    * @return the class name of the {@link Planner}    */
DECL|method|getReplanner (String queue)
specifier|public
name|String
name|getReplanner
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|DEFAULT_RESERVATION_PLANNER_NAME
return|;
block|}
comment|/**    * Gets whether the applications should be killed or moved to the parent queue    * when the {@link ReservationDefinition} expires    * @param queue name of the queue    * @return true if application should be moved, false if they need to be    * killed    */
DECL|method|getMoveOnExpiry (String queue)
specifier|public
name|boolean
name|getMoveOnExpiry
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|DEFAULT_RESERVATION_MOVE_ON_EXPIRY
return|;
block|}
comment|/**    * Gets the time in milliseconds for which the {@link Planner} will verify    * the {@link Plan}s satisfy the constraints    * @param queue name of the queue    * @return the time in milliseconds for which to check constraints    */
DECL|method|getEnforcementWindow (String queue)
specifier|public
name|long
name|getEnforcementWindow
parameter_list|(
name|String
name|queue
parameter_list|)
block|{
return|return
name|DEFAULT_RESERVATION_ENFORCEMENT_WINDOW
return|;
block|}
block|}
end_class

end_unit

