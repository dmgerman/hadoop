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
name|Evolving
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
comment|/**  * {@link ReservationDefinition} captures the set of resource and time  * constraints the user cares about regarding a reservation.  *   * @see ResourceRequest  *   */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ReservationDefinition
specifier|public
specifier|abstract
class|class
name|ReservationDefinition
block|{
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance (long arrival, long deadline, ReservationRequests reservationRequests, String name)
specifier|public
specifier|static
name|ReservationDefinition
name|newInstance
parameter_list|(
name|long
name|arrival
parameter_list|,
name|long
name|deadline
parameter_list|,
name|ReservationRequests
name|reservationRequests
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|ReservationDefinition
name|rDefinition
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ReservationDefinition
operator|.
name|class
argument_list|)
decl_stmt|;
name|rDefinition
operator|.
name|setArrival
argument_list|(
name|arrival
argument_list|)
expr_stmt|;
name|rDefinition
operator|.
name|setDeadline
argument_list|(
name|deadline
argument_list|)
expr_stmt|;
name|rDefinition
operator|.
name|setReservationRequests
argument_list|(
name|reservationRequests
argument_list|)
expr_stmt|;
name|rDefinition
operator|.
name|setReservationName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|rDefinition
return|;
block|}
comment|/**    * Get the arrival time or the earliest time from which the resource(s) can be    * allocated. Time expressed as UTC.    *     * @return the earliest valid time for this reservation    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getArrival ()
specifier|public
specifier|abstract
name|long
name|getArrival
parameter_list|()
function_decl|;
comment|/**    * Set the arrival time or the earliest time from which the resource(s) can be    * allocated. Time expressed as UTC.    *     * @param earliestStartTime the earliest valid time for this reservation    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setArrival (long earliestStartTime)
specifier|public
specifier|abstract
name|void
name|setArrival
parameter_list|(
name|long
name|earliestStartTime
parameter_list|)
function_decl|;
comment|/**    * Get the deadline or the latest time by when the resource(s) must be    * allocated. Time expressed as UTC.    *     * @return the deadline or the latest time by when the resource(s) must be    *         allocated    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getDeadline ()
specifier|public
specifier|abstract
name|long
name|getDeadline
parameter_list|()
function_decl|;
comment|/**    * Set the deadline or the latest time by when the resource(s) must be    * allocated. Time expressed as UTC.    *     * @param latestEndTime the deadline or the latest time by when the    *          resource(s) should be allocated    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setDeadline (long latestEndTime)
specifier|public
specifier|abstract
name|void
name|setDeadline
parameter_list|(
name|long
name|latestEndTime
parameter_list|)
function_decl|;
comment|/**    * Get the list of {@link ReservationRequests} representing the resources    * required by the application    *     * @return the list of {@link ReservationRequests}    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getReservationRequests ()
specifier|public
specifier|abstract
name|ReservationRequests
name|getReservationRequests
parameter_list|()
function_decl|;
comment|/**    * Set the list of {@link ReservationRequests} representing the resources    * required by the application    *     * @param reservationRequests the list of {@link ReservationRequests}    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setReservationRequests ( ReservationRequests reservationRequests)
specifier|public
specifier|abstract
name|void
name|setReservationRequests
parameter_list|(
name|ReservationRequests
name|reservationRequests
parameter_list|)
function_decl|;
comment|/**    * Get the name for this reservation. The name need-not be unique, and it is    * just a mnemonic for the user (akin to job names). Accepted reservations are    * uniquely identified by a system-generated ReservationId.    *     * @return string representing the name of the corresponding reserved resource    *         allocation in the scheduler    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getReservationName ()
specifier|public
specifier|abstract
name|String
name|getReservationName
parameter_list|()
function_decl|;
comment|/**    * Set the name for this reservation. The name need-not be unique, and it is    * just a mnemonic for the user (akin to job names). Accepted reservations are    * uniquely identified by a system-generated ReservationId.    *     * @param name representing the name of the corresponding reserved resource    *          allocation in the scheduler    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setReservationName (String name)
specifier|public
specifier|abstract
name|void
name|setReservationName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_class

end_unit

