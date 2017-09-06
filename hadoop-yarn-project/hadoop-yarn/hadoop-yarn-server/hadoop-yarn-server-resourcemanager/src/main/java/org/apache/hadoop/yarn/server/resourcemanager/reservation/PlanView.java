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
comment|/**  * This interface provides a read-only view on the allocations made in this  * plan. This methods are used for example by {@code ReservationAgent}s to  * determine the free resources in a certain point in time, and by  * PlanFollowerPolicy to publish this plan to the scheduler.  */
end_comment

begin_interface
DECL|interface|PlanView
interface|interface
name|PlanView
extends|extends
name|PlanContext
block|{
comment|/**    * Return a set of {@link ReservationAllocation} identified by the user who    * made the reservation.    *    * @param reservationID the unqiue id to identify the    *          {@link ReservationAllocation}    * @param interval the time interval used to retrieve the reservation    *          allocations from. Only reservations with start time no greater    *          than the interval end time, and end time no less than the interval    *          start time will be selected.    * @param user the user to retrieve the reservation allocation from.    * @return a set of {@link ReservationAllocation} identified by the user who    *         made the reservation    */
DECL|method|getReservations (ReservationId reservationID, ReservationInterval interval, String user)
name|Set
argument_list|<
name|ReservationAllocation
argument_list|>
name|getReservations
parameter_list|(
name|ReservationId
name|reservationID
parameter_list|,
name|ReservationInterval
name|interval
parameter_list|,
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * Return a set of {@link ReservationAllocation} identified by any user.    *    * @param reservationID the unqiue id to identify the    *          {@link ReservationAllocation}    * @param interval the time interval used to retrieve the reservation    *          allocations from. Only reservations with start time no greater    *          than the interval end time, and end time no less than the interval    *          start time will be selected.    * @return a set of {@link ReservationAllocation} identified by any user    */
DECL|method|getReservations (ReservationId reservationID, ReservationInterval interval)
name|Set
argument_list|<
name|ReservationAllocation
argument_list|>
name|getReservations
parameter_list|(
name|ReservationId
name|reservationID
parameter_list|,
name|ReservationInterval
name|interval
parameter_list|)
function_decl|;
comment|/**    * Return a {@link ReservationAllocation} identified by its    * {@link ReservationId}    *     * @param reservationID the unique id to identify the    *          {@link ReservationAllocation}    * @return {@link ReservationAllocation} identified by the specified id    */
DECL|method|getReservationById (ReservationId reservationID)
name|ReservationAllocation
name|getReservationById
parameter_list|(
name|ReservationId
name|reservationID
parameter_list|)
function_decl|;
comment|/**    * Return a set of {@link ReservationAllocation} that belongs to a certain    * user and overlaps time t.    *    * @param user the user being considered    * @param t the instant in time being considered    * @return set of active {@link ReservationAllocation}s for this user at this    *         time    */
DECL|method|getReservationByUserAtTime (String user, long t)
name|Set
argument_list|<
name|ReservationAllocation
argument_list|>
name|getReservationByUserAtTime
parameter_list|(
name|String
name|user
parameter_list|,
name|long
name|t
parameter_list|)
function_decl|;
comment|/**    * Gets all the active reservations at the specified point of time    *     * @param tick the time (UTC in ms) for which the active reservations are    *          requested    * @return set of active reservations at the specified time    */
DECL|method|getReservationsAtTime (long tick)
name|Set
argument_list|<
name|ReservationAllocation
argument_list|>
name|getReservationsAtTime
parameter_list|(
name|long
name|tick
parameter_list|)
function_decl|;
comment|/**    * Gets all the reservations in the plan    *     * @return set of all reservations handled by this Plan    */
DECL|method|getAllReservations ()
name|Set
argument_list|<
name|ReservationAllocation
argument_list|>
name|getAllReservations
parameter_list|()
function_decl|;
comment|/**    * Returns the total {@link Resource} reserved for all users at the specified    * time    *     * @param tick the time (UTC in ms) for which the reserved resources are    *          requested    * @return the total {@link Resource} reserved for all users at the specified    *         time    */
DECL|method|getTotalCommittedResources (long tick)
name|Resource
name|getTotalCommittedResources
parameter_list|(
name|long
name|tick
parameter_list|)
function_decl|;
comment|/**    * Returns the overall capacity in terms of {@link Resource} assigned to this    * plan (typically will correspond to the absolute capacity of the    * corresponding queue).    *     * @return the overall capacity in terms of {@link Resource} assigned to this    *         plan    */
DECL|method|getTotalCapacity ()
name|Resource
name|getTotalCapacity
parameter_list|()
function_decl|;
comment|/**    * Gets the time (UTC in ms) at which the first reservation starts    *     * @return the time (UTC in ms) at which the first reservation starts    */
DECL|method|getEarliestStartTime ()
name|long
name|getEarliestStartTime
parameter_list|()
function_decl|;
comment|/**    * Returns the time (UTC in ms) at which the last reservation terminates    *    * @return the time (UTC in ms) at which the last reservation terminates    */
DECL|method|getLastEndTime ()
name|long
name|getLastEndTime
parameter_list|()
function_decl|;
comment|/**    * This method returns the amount of resources available to a given user    * (optionally if removing a certain reservation) over the start-end time    * range. If the request is periodic (period is non-zero) we return the    * minimum amount of resources available to periodic reservations (in all    * "period" windows within the system maxPeriod / LCM).    *    * @param user the user being considered    * @param oldId the identifier of the existing reservation    * @param start start of the time interval.    * @param end end of the time interval.    * @param period the ms periodicty for this request (loop and pick min till    *          maxPeriodicity)    * @return a view of the plan as it is available to this user    * @throws PlanningException if operation is unsuccessful    */
DECL|method|getAvailableResourceOverTime (String user, ReservationId oldId, long start, long end, long period)
name|RLESparseResourceAllocation
name|getAvailableResourceOverTime
parameter_list|(
name|String
name|user
parameter_list|,
name|ReservationId
name|oldId
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|,
name|long
name|period
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
comment|/**    * This method returns a RLE encoded view of the user reservation count    * utilization between start and end time.    *    * @param user the user being considered    * @param start start of the time interval.    * @param end end of the time interval.    * @return RLE encoded view of reservation used over time    */
DECL|method|getReservationCountForUserOverTime (String user, long start, long end)
name|RLESparseResourceAllocation
name|getReservationCountForUserOverTime
parameter_list|(
name|String
name|user
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
function_decl|;
comment|/**    * This method returns a RLE encoded view of the user reservation utilization    * between start and end time.    *    * @param user the user being considered    * @param start start of the time interval.    * @param end end of the time interval.    * @return RLE encoded view of resources used over time    */
DECL|method|getConsumptionForUserOverTime (String user, long start, long end)
name|RLESparseResourceAllocation
name|getConsumptionForUserOverTime
parameter_list|(
name|String
name|user
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
function_decl|;
comment|/**    * Get the cumulative load over a time interval.    *    * @param start start of the time interval.    * @param end end of the time interval.    * @return RLE sparse allocation.    * @throws PlanningException if operation is unsuccessful    */
DECL|method|getCumulativeLoadOverTime (long start, long end)
name|RLESparseResourceAllocation
name|getCumulativeLoadOverTime
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
block|}
end_interface

end_unit

