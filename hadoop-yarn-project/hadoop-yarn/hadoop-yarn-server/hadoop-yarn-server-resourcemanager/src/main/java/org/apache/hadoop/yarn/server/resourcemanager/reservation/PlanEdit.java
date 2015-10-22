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

begin_comment
comment|/**  * This interface groups the methods used to modify the state of a Plan.  */
end_comment

begin_interface
DECL|interface|PlanEdit
specifier|public
interface|interface
name|PlanEdit
extends|extends
name|PlanContext
extends|,
name|PlanView
block|{
comment|/**    * Add a new {@link ReservationAllocation} to the plan    *     * @param reservation the {@link ReservationAllocation} to be added to the    *          plan    * @param isRecovering flag to indicate if reservation is being added as part    *          of failover or not    * @return true if addition is successful, false otherwise    */
DECL|method|addReservation (ReservationAllocation reservation, boolean isRecovering)
specifier|public
name|boolean
name|addReservation
parameter_list|(
name|ReservationAllocation
name|reservation
parameter_list|,
name|boolean
name|isRecovering
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
comment|/**    * Updates an existing {@link ReservationAllocation} in the plan. This is    * required for re-negotiation    *     * @param reservation the {@link ReservationAllocation} to be updated the plan    * @return true if update is successful, false otherwise    */
DECL|method|updateReservation (ReservationAllocation reservation)
specifier|public
name|boolean
name|updateReservation
parameter_list|(
name|ReservationAllocation
name|reservation
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
comment|/**    * Delete an existing {@link ReservationAllocation} from the plan identified    * uniquely by its {@link ReservationId}. This will generally be used for    * garbage collection    *     * @param reservationID the {@link ReservationAllocation} to be deleted from    *          the plan identified uniquely by its {@link ReservationId}    * @return true if delete is successful, false otherwise    */
DECL|method|deleteReservation (ReservationId reservationID)
specifier|public
name|boolean
name|deleteReservation
parameter_list|(
name|ReservationId
name|reservationID
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
comment|/**    * Method invoked to garbage collect old reservations. It cleans up expired    * reservations that have fallen out of the sliding archival window    *     * @param tick the current time from which the archival window is computed    */
DECL|method|archiveCompletedReservations (long tick)
specifier|public
name|void
name|archiveCompletedReservations
parameter_list|(
name|long
name|tick
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
comment|/**    * Sets the overall capacity in terms of {@link Resource} assigned to this    * plan    *     * @param capacity the overall capacity in terms of {@link Resource} assigned    *          to this plan    */
DECL|method|setTotalCapacity (Resource capacity)
specifier|public
name|void
name|setTotalCapacity
parameter_list|(
name|Resource
name|capacity
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

