begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
name|Private
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
name|InterfaceAudience
operator|.
name|Public
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
name|Stable
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
name|yarn
operator|.
name|util
operator|.
name|Records
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

begin_comment
comment|/**  * {@code ReservationAllocationState} represents the reservation that is  * made by a user.  *<p>  * It includes:  *<ul>  *<li>Duration of the reservation.</li>  *<li>Acceptance time of the duration.</li>  *<li>  *       List of {@link ResourceAllocationRequest}, which includes the time  *       interval, and capability of the allocation.  *       {@code ResourceAllocationRequest} represents an allocation  *       made for a reservation for the current state of the queue. This can be  *       changed for reasons such as re-planning, but will always be subject to  *       the constraints of the user contract as described by  *       {@link ReservationDefinition}  *</li>  *<li>{@link ReservationId} of the reservation.</li>  *<li>{@link ReservationDefinition} used to make the reservation.</li>  *</ul>  *  * @see ResourceAllocationRequest  * @see ReservationId  * @see ReservationDefinition  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ReservationAllocationState
specifier|public
specifier|abstract
class|class
name|ReservationAllocationState
block|{
comment|/**    *    * @param acceptanceTime The acceptance time of the reservation.    * @param user The username of the user who made the reservation.    * @param resourceAllocations List of {@link ResourceAllocationRequest}    *                            representing the current state of the    *                            reservation resource allocations. This is    *                            subject to change in the event of re-planning.    * @param reservationId {@link ReservationId } of the reservation being    *                                            listed.    * @param reservationDefinition {@link ReservationDefinition} used to make    *                              the reservation.    * @return {@code ReservationAllocationState} that represents the state of    * the reservation.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance (long acceptanceTime, String user, List<ResourceAllocationRequest> resourceAllocations, ReservationId reservationId, ReservationDefinition reservationDefinition)
specifier|public
specifier|static
name|ReservationAllocationState
name|newInstance
parameter_list|(
name|long
name|acceptanceTime
parameter_list|,
name|String
name|user
parameter_list|,
name|List
argument_list|<
name|ResourceAllocationRequest
argument_list|>
name|resourceAllocations
parameter_list|,
name|ReservationId
name|reservationId
parameter_list|,
name|ReservationDefinition
name|reservationDefinition
parameter_list|)
block|{
name|ReservationAllocationState
name|ri
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ReservationAllocationState
operator|.
name|class
argument_list|)
decl_stmt|;
name|ri
operator|.
name|setAcceptanceTime
argument_list|(
name|acceptanceTime
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setResourceAllocationRequests
argument_list|(
name|resourceAllocations
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setReservationId
argument_list|(
name|reservationId
argument_list|)
expr_stmt|;
name|ri
operator|.
name|setReservationDefinition
argument_list|(
name|reservationDefinition
argument_list|)
expr_stmt|;
return|return
name|ri
return|;
block|}
comment|/**    * Get the acceptance time of the reservation.    *    * @return the time that the reservation was accepted.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAcceptanceTime ()
specifier|public
specifier|abstract
name|long
name|getAcceptanceTime
parameter_list|()
function_decl|;
comment|/**    * Set the time that the reservation was accepted.    *    * @param acceptanceTime The acceptance time of the reservation.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAcceptanceTime (long acceptanceTime)
specifier|public
specifier|abstract
name|void
name|setAcceptanceTime
parameter_list|(
name|long
name|acceptanceTime
parameter_list|)
function_decl|;
comment|/**    * Get the user who made the reservation.    *    * @return the name of the user who made the reservation.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getUser ()
specifier|public
specifier|abstract
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Set the user who made the reservation.    *    * @param user The username of the user who made the reservation.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setUser (String user)
specifier|public
specifier|abstract
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * Get the Resource allocations of the reservation based on the current state    * of the plan. This is subject to change in the event of re-planning.    * The allocations will be constraint to the user contract as described by    * the {@link ReservationDefinition}    *    * @return a list of resource allocations for the reservation.    */
annotation|@
name|Public
annotation|@
name|Unstable
specifier|public
specifier|abstract
name|List
argument_list|<
name|ResourceAllocationRequest
argument_list|>
DECL|method|getResourceAllocationRequests ()
name|getResourceAllocationRequests
parameter_list|()
function_decl|;
comment|/**    * Set the list of resource allocations made for the reservation.    *    * @param resourceAllocations List of {@link ResourceAllocationRequest}    *                            representing the current state of the    *                            reservation resource allocations. This is    *                            subject to change in the event of re-planning.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResourceAllocationRequests ( List<ResourceAllocationRequest> resourceAllocations)
specifier|public
specifier|abstract
name|void
name|setResourceAllocationRequests
parameter_list|(
name|List
argument_list|<
name|ResourceAllocationRequest
argument_list|>
name|resourceAllocations
parameter_list|)
function_decl|;
comment|/**    * Get the id of the reservation.    *    * @return the reservation id corresponding to the reservation.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getReservationId ()
specifier|public
specifier|abstract
name|ReservationId
name|getReservationId
parameter_list|()
function_decl|;
comment|/**    * Set the id corresponding to the reservation.    * `    * @param reservationId {@link ReservationId } of the reservation being    *                                            listed.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setReservationId (ReservationId reservationId)
specifier|public
specifier|abstract
name|void
name|setReservationId
parameter_list|(
name|ReservationId
name|reservationId
parameter_list|)
function_decl|;
comment|/**    * Get the reservation definition used to make the reservation.    *    * @return the reservation definition used to make the reservation.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getReservationDefinition ()
specifier|public
specifier|abstract
name|ReservationDefinition
name|getReservationDefinition
parameter_list|()
function_decl|;
comment|/**    * Set the definition of the reservation.    *    * @param reservationDefinition {@link ReservationDefinition} used to make    *                              the reservation.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setReservationDefinition (ReservationDefinition reservationDefinition)
specifier|public
specifier|abstract
name|void
name|setReservationDefinition
parameter_list|(
name|ReservationDefinition
name|reservationDefinition
parameter_list|)
function_decl|;
block|}
end_class

end_unit

