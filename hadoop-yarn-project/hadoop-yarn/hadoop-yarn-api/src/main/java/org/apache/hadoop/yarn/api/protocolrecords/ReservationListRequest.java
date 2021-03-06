begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
name|protocolrecords
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

begin_comment
comment|/**  * {@link ReservationListRequest} captures the set of requirements the  * user has to list reservations.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ReservationListRequest
specifier|public
specifier|abstract
class|class
name|ReservationListRequest
block|{
comment|/**    * The {@link ReservationListRequest} will use the reservationId to search for    * reservations to list if it is provided. Otherwise, it will select active    * reservations within the startTime and endTime (inclusive).    *    * @param queue Required. Cannot be null or empty. Refers to the reservable    *              queue in the scheduler that was selected when creating a    *              reservation submission {@link ReservationSubmissionRequest}.    * @param reservationId Optional. String representation of    *                     {@code ReservationId} If provided, other fields will    *                     be ignored.    * @param startTime Optional. If provided, only reservations that    *                end after the startTime will be selected. This defaults    *                to 0 if an invalid number is used.    * @param endTime Optional. If provided, only reservations that    *                start on or before endTime will be selected. This defaults    *                to Long.MAX_VALUE if an invalid number is used.    * @param includeReservationAllocations Optional. Flag that    *                determines whether the entire reservation allocations are    *                to be returned. Reservation allocations are subject to    *                change in the event of re-planning as described by    *                {@code ReservationDefinition}.    * @return the list of reservations via  {@link ReservationListRequest}    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance ( String queue, String reservationId, long startTime, long endTime, boolean includeReservationAllocations)
specifier|public
specifier|static
name|ReservationListRequest
name|newInstance
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|reservationId
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|endTime
parameter_list|,
name|boolean
name|includeReservationAllocations
parameter_list|)
block|{
name|ReservationListRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ReservationListRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|request
operator|.
name|setReservationId
argument_list|(
name|reservationId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|request
operator|.
name|setEndTime
argument_list|(
name|endTime
argument_list|)
expr_stmt|;
name|request
operator|.
name|setIncludeResourceAllocations
argument_list|(
name|includeReservationAllocations
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * The {@link ReservationListRequest} will use the reservationId to search for    * reservations to list if it is provided. Otherwise, it will select active    * reservations within the startTime and endTime (inclusive).    *    * @param queue Required. Cannot be null or empty. Refers to the reservable    *              queue in the scheduler that was selected when creating a    *              reservation submission {@link ReservationSubmissionRequest}.    * @param reservationId Optional. String representation of    *                     {@code ReservationId} If provided, other fields will    *                     be ignored.    * @param includeReservationAllocations Optional. Flag that    *                determines whether the entire reservation allocations are    *                to be returned. Reservation allocations are subject to    *                change in the event of re-planning as described by    *                {@code ReservationDefinition}.    * @return the list of reservations via {@link ReservationListRequest}    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance ( String queue, String reservationId, boolean includeReservationAllocations)
specifier|public
specifier|static
name|ReservationListRequest
name|newInstance
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|reservationId
parameter_list|,
name|boolean
name|includeReservationAllocations
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|queue
argument_list|,
name|reservationId
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|includeReservationAllocations
argument_list|)
return|;
block|}
comment|/**    * The {@link ReservationListRequest} will use the reservationId to search for    * reservations to list if it is provided. Otherwise, it will select active    * reservations within the startTime and endTime (inclusive).    *    * @param queue Required. Cannot be null or empty. Refers to the reservable    *              queue in the scheduler that was selected when creating a    *              reservation submission {@link ReservationSubmissionRequest}.    * @param reservationId Optional. String representation of    *                     {@code ReservationId} If provided, other fields will    *                     be ignored.    * @return the list of reservations via {@link ReservationListRequest}    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance ( String queue, String reservationId)
specifier|public
specifier|static
name|ReservationListRequest
name|newInstance
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|reservationId
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|queue
argument_list|,
name|reservationId
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Get queue name to use to find reservations.    *    * @return the queue name to use to find reservations.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getQueue ()
specifier|public
specifier|abstract
name|String
name|getQueue
parameter_list|()
function_decl|;
comment|/**    * Set queue name to use to find resource allocations.    *    * @param queue Required. Cannot be null or empty.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setQueue (String queue)
specifier|public
specifier|abstract
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
function_decl|;
comment|/**    * Get the reservation id to use to find a reservation.    *    * @return the reservation id of the reservation.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getReservationId ()
specifier|public
specifier|abstract
name|String
name|getReservationId
parameter_list|()
function_decl|;
comment|/**    * Set the reservation id to use to find a reservation.    *    * @param reservationId Optional. String representation of    *                     {@code ReservationId} If provided, other fields will    *                     be ignored.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setReservationId (String reservationId)
specifier|public
specifier|abstract
name|void
name|setReservationId
parameter_list|(
name|String
name|reservationId
parameter_list|)
function_decl|;
comment|/**    * Get the start time to use to search for reservations.    * When this is set, reservations that start before this start    * time are ignored.    *    * @return the start time to use to search for reservations.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getStartTime ()
specifier|public
specifier|abstract
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/**    * Set the start time to use to search for reservations.    * When this is set, reservations that start before this start    * time are ignored.    *    * @param startTime Optional. If provided, only reservations that    *                end after the startTime will be selected. This defaults    *                to 0 if an invalid number is used.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setStartTime (long startTime)
specifier|public
specifier|abstract
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
function_decl|;
comment|/**    * Get the end time to use to search for reservations.    * When this is set, reservations that start after this end    * time are ignored.    *    * @return the end time to use to search for reservations.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getEndTime ()
specifier|public
specifier|abstract
name|long
name|getEndTime
parameter_list|()
function_decl|;
comment|/**    * Set the end time to use to search for reservations.    * When this is set, reservations that start after this end    * time are ignored.    *    * @param endTime Optional. If provided, only reservations that    *                start before endTime will be selected. This defaults    *                to Long.MAX_VALUE if an invalid number is used.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setEndTime (long endTime)
specifier|public
specifier|abstract
name|void
name|setEndTime
parameter_list|(
name|long
name|endTime
parameter_list|)
function_decl|;
comment|/**    * Get the boolean representing whether or not the user    * is requesting the full resource allocation.    * If this is true, the full resource allocation will    * be included in the response.    *    * @return the end time to use to search for reservations.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getIncludeResourceAllocations ()
specifier|public
specifier|abstract
name|boolean
name|getIncludeResourceAllocations
parameter_list|()
function_decl|;
comment|/**    * Set the boolean representing whether or not the user    * is requesting the full resource allocation.    * If this is true, the full resource allocation will    * be included in the response.    *    * @param includeReservationAllocations Optional. Flag that    *                determines whether the entire list of    *                {@code ResourceAllocationRequest} will be returned.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setIncludeResourceAllocations ( boolean includeReservationAllocations)
specifier|public
specifier|abstract
name|void
name|setIncludeResourceAllocations
parameter_list|(
name|boolean
name|includeReservationAllocations
parameter_list|)
function_decl|;
block|}
end_class

end_unit

