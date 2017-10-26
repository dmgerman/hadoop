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
name|util
operator|.
name|resource
operator|.
name|ResourceCalculator
import|;
end_import

begin_comment
comment|/**  * This interface provides read-only access to configuration-type parameter for  * a plan.  *   */
end_comment

begin_interface
DECL|interface|PlanContext
specifier|public
interface|interface
name|PlanContext
block|{
comment|/**    * Returns the configured "step" or granularity of time of the plan in millis.    *     * @return plan step in millis    */
DECL|method|getStep ()
specifier|public
name|long
name|getStep
parameter_list|()
function_decl|;
comment|/**    * Return the {@link ReservationAgent} configured for this plan that is    * responsible for optimally placing various reservation requests    *     * @return the {@link ReservationAgent} configured for this plan    */
DECL|method|getReservationAgent ()
specifier|public
name|ReservationAgent
name|getReservationAgent
parameter_list|()
function_decl|;
comment|/**    * Return an instance of a {@link Planner}, which will be invoked in response    * to unexpected reduction in the resources of this plan    *     * @return an instance of a {@link Planner}, which will be invoked in response    *         to unexpected reduction in the resources of this plan    */
DECL|method|getReplanner ()
specifier|public
name|Planner
name|getReplanner
parameter_list|()
function_decl|;
comment|/**    * Return the configured {@link SharingPolicy} that governs the sharing of the    * resources of the plan between its various users    *     * @return the configured {@link SharingPolicy} that governs the sharing of    *         the resources of the plan between its various users    */
DECL|method|getSharingPolicy ()
specifier|public
name|SharingPolicy
name|getSharingPolicy
parameter_list|()
function_decl|;
comment|/**    * Returns the system {@link ResourceCalculator}    *     * @return the system {@link ResourceCalculator}    */
DECL|method|getResourceCalculator ()
specifier|public
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
function_decl|;
comment|/**    * Returns the single smallest {@link Resource} allocation that can be    * reserved in this plan    *     * @return the single smallest {@link Resource} allocation that can be    *         reserved in this plan    */
DECL|method|getMinimumAllocation ()
specifier|public
name|Resource
name|getMinimumAllocation
parameter_list|()
function_decl|;
comment|/**    * Returns the single largest {@link Resource} allocation that can be reserved    * in this plan    *     * @return the single largest {@link Resource} allocation that can be reserved    *         in this plan    */
DECL|method|getMaximumAllocation ()
specifier|public
name|Resource
name|getMaximumAllocation
parameter_list|()
function_decl|;
comment|/**    * Returns the maximum periodicity allowed in a recurrence expression    * for reservations of a particular plan. This value must be divisible by    * the recurrence expression of a newly submitted reservation. Otherwise, the    * reservation submission will fail.    *    * @return the maximum periodicity allowed in a recurrence expression for    * reservations of a particular plan.    */
DECL|method|getMaximumPeriodicity ()
name|long
name|getMaximumPeriodicity
parameter_list|()
function_decl|;
comment|/**    * Return the name of the queue in the {@link ResourceScheduler} corresponding    * to this plan    *     * @return the name of the queue in the {@link ResourceScheduler}    *         corresponding to this plan    */
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
function_decl|;
comment|/**    * Return the {@link QueueMetrics} for the queue in the    * {@link ResourceScheduler} corresponding to this plan    *     * @return the {@link QueueMetrics} for the queue in the    *         {@link ResourceScheduler} corresponding to this plan    */
DECL|method|getQueueMetrics ()
specifier|public
name|QueueMetrics
name|getQueueMetrics
parameter_list|()
function_decl|;
comment|/**    * Instructs the {@link PlanFollower} on what to do for applications    * which are still running when the reservation is expiring (move-to-default    * vs kill)    *     * @return true if remaining applications have to be killed, false if they    *         have to migrated    */
DECL|method|getMoveOnExpiry ()
specifier|public
name|boolean
name|getMoveOnExpiry
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

