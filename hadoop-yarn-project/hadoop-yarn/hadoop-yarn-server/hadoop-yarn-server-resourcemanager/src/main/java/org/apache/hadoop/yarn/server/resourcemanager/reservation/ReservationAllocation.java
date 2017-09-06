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
name|Map
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
name|Resource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * A ReservationAllocation represents a concrete allocation of resources over  * time that satisfy a certain {@link ReservationDefinition}. This is used  * internally by a {@link Plan} to store information about how each of the  * accepted {@link ReservationDefinition} have been allocated.  */
end_comment

begin_interface
DECL|interface|ReservationAllocation
specifier|public
interface|interface
name|ReservationAllocation
extends|extends
name|Comparable
argument_list|<
name|ReservationAllocation
argument_list|>
block|{
comment|/**    * Returns the unique identifier {@link ReservationId} that represents the    * reservation    *     * @return reservationId the unique identifier {@link ReservationId} that    *         represents the reservation    */
DECL|method|getReservationId ()
name|ReservationId
name|getReservationId
parameter_list|()
function_decl|;
comment|/**    * Returns the original {@link ReservationDefinition} submitted by the client    *     * @return the {@link ReservationDefinition} submitted by the client    */
DECL|method|getReservationDefinition ()
name|ReservationDefinition
name|getReservationDefinition
parameter_list|()
function_decl|;
comment|/**    * Returns the time at which the reservation is activated.    *     * @return the time at which the reservation is activated    */
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/**    * Returns the time at which the reservation terminates.    *     * @return the time at which the reservation terminates    */
DECL|method|getEndTime ()
name|long
name|getEndTime
parameter_list|()
function_decl|;
comment|/**    * Returns the map of resources requested against the time interval for which    * they were.    *     * @return the allocationRequests the map of resources requested against the    *         time interval for which they were    */
DECL|method|getAllocationRequests ()
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|getAllocationRequests
parameter_list|()
function_decl|;
comment|/**    * Return a string identifying the plan to which the reservation belongs    *     * @return the plan to which the reservation belongs    */
DECL|method|getPlanName ()
name|String
name|getPlanName
parameter_list|()
function_decl|;
comment|/**    * Returns the user who requested the reservation    *     * @return the user who requested the reservation    */
DECL|method|getUser ()
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Returns whether the reservation has gang semantics or not    *     * @return true if there is a gang request, false otherwise    */
DECL|method|containsGangs ()
name|boolean
name|containsGangs
parameter_list|()
function_decl|;
comment|/**    * Sets the time at which the reservation was accepted by the system    *     * @param acceptedAt the time at which the reservation was accepted by the    *          system    */
DECL|method|setAcceptanceTimestamp (long acceptedAt)
name|void
name|setAcceptanceTimestamp
parameter_list|(
name|long
name|acceptedAt
parameter_list|)
function_decl|;
comment|/**    * Returns the time at which the reservation was accepted by the system    *     * @return the time at which the reservation was accepted by the system    */
DECL|method|getAcceptanceTime ()
name|long
name|getAcceptanceTime
parameter_list|()
function_decl|;
comment|/**    * Returns the capacity represented by cumulative resources reserved by the    * reservation at the specified point of time    *     * @param tick the time (UTC in ms) for which the reserved resources are    *          requested    * @return the resources reserved at the specified time    */
DECL|method|getResourcesAtTime (long tick)
name|Resource
name|getResourcesAtTime
parameter_list|(
name|long
name|tick
parameter_list|)
function_decl|;
comment|/**    * Return a RLE representation of used resources.    *    * @return a RLE encoding of resources allocated over time.    */
DECL|method|getResourcesOverTime ()
name|RLESparseResourceAllocation
name|getResourcesOverTime
parameter_list|()
function_decl|;
comment|/**    * Return a RLE representation of used resources.    *    * @param start start of the time interval.    * @param end end of the time interval.    * @return a RLE encoding of resources allocated over time.    */
DECL|method|getResourcesOverTime (long start, long end)
name|RLESparseResourceAllocation
name|getResourcesOverTime
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
function_decl|;
comment|/**    * Get the periodicity of this reservation representing the time period of the    * periodic job. Period is represented in milliseconds for periodic jobs.    * Period is 0 for non-periodic jobs.    *    * @return periodicity of this reservation    */
DECL|method|getPeriodicity ()
name|long
name|getPeriodicity
parameter_list|()
function_decl|;
comment|/**    * Set the periodicity of this reservation representing the time period of the    * periodic job. Period is represented in milliseconds for periodic jobs.    * Period is 0 for non-periodic jobs.    *    * @param period periodicity of this reservation    */
annotation|@
name|VisibleForTesting
DECL|method|setPeriodicity (long period)
name|void
name|setPeriodicity
parameter_list|(
name|long
name|period
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

