begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * This interface provides a read-only view on the allocations made in this  * plan. This methods are used for example by {@link ReservationAgent}s to  * determine the free resources in a certain point in time, and by  * PlanFollowerPolicy to publish this plan to the scheduler.  */
end_comment

begin_interface
DECL|interface|PlanView
specifier|public
interface|interface
name|PlanView
extends|extends
name|PlanContext
block|{
comment|/**    * Return a {@link ReservationAllocation} identified by its    * {@link ReservationId}    *     * @param reservationID the unique id to identify the    *          {@link ReservationAllocation}    * @return {@link ReservationAllocation} identified by the specified id    */
DECL|method|getReservationById (ReservationId reservationID)
specifier|public
name|ReservationAllocation
name|getReservationById
parameter_list|(
name|ReservationId
name|reservationID
parameter_list|)
function_decl|;
comment|/**    * Gets all the active reservations at the specified point of time    *     * @param tick the time (UTC in ms) for which the active reservations are    *          requested    * @return set of active reservations at the specified time    */
DECL|method|getReservationsAtTime (long tick)
specifier|public
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
specifier|public
name|Set
argument_list|<
name|ReservationAllocation
argument_list|>
name|getAllReservations
parameter_list|()
function_decl|;
comment|/**    * Returns the total {@link Resource} reserved for all users at the specified    * time    *     * @param tick the time (UTC in ms) for which the reserved resources are    *          requested    * @return the total {@link Resource} reserved for all users at the specified    *         time    */
DECL|method|getTotalCommittedResources (long tick)
specifier|public
name|Resource
name|getTotalCommittedResources
parameter_list|(
name|long
name|tick
parameter_list|)
function_decl|;
comment|/**    * Returns the total {@link Resource} reserved for a given user at the    * specified time    *     * @param user the user who made the reservation(s)    * @param tick the time (UTC in ms) for which the reserved resources are    *          requested    * @return the total {@link Resource} reserved for a given user at the    *         specified time    */
DECL|method|getConsumptionForUser (String user, long tick)
specifier|public
name|Resource
name|getConsumptionForUser
parameter_list|(
name|String
name|user
parameter_list|,
name|long
name|tick
parameter_list|)
function_decl|;
comment|/**    * Returns the overall capacity in terms of {@link Resource} assigned to this    * plan (typically will correspond to the absolute capacity of the    * corresponding queue).    *     * @return the overall capacity in terms of {@link Resource} assigned to this    *         plan    */
DECL|method|getTotalCapacity ()
specifier|public
name|Resource
name|getTotalCapacity
parameter_list|()
function_decl|;
comment|/**    * Gets the time (UTC in ms) at which the first reservation starts    *     * @return the time (UTC in ms) at which the first reservation starts    */
DECL|method|getEarliestStartTime ()
specifier|public
name|long
name|getEarliestStartTime
parameter_list|()
function_decl|;
comment|/**    * Returns the time (UTC in ms) at which the last reservation terminates    *     * @return the time (UTC in ms) at which the last reservation terminates    */
DECL|method|getLastEndTime ()
specifier|public
name|long
name|getLastEndTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

