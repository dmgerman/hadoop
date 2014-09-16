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
name|LimitedPrivate
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
comment|/**  * This is the interface for policy that validate new  * {@link ReservationAllocation}s for allocations being added to a {@link Plan}.  * Individual policies will be enforcing different invariants.  */
end_comment

begin_interface
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Unstable
DECL|interface|SharingPolicy
specifier|public
interface|interface
name|SharingPolicy
block|{
comment|/**    * Initialize this policy    *     * @param inventoryQueuePath the name of the queue for this plan    * @param conf the system configuration    */
DECL|method|init (String inventoryQueuePath, Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|inventoryQueuePath
parameter_list|,
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * This method runs the policy validation logic, and return true/false on    * whether the {@link ReservationAllocation} is acceptable according to this    * sharing policy.    *     * @param plan the {@link Plan} we validate against    * @param newAllocation the allocation proposed to be added to the    *          {@link Plan}    * @throws PlanningException if the policy is respected if we add this    *           {@link ReservationAllocation} to the {@link Plan}    */
DECL|method|validate (Plan plan, ReservationAllocation newAllocation)
specifier|public
name|void
name|validate
parameter_list|(
name|Plan
name|plan
parameter_list|,
name|ReservationAllocation
name|newAllocation
parameter_list|)
throws|throws
name|PlanningException
function_decl|;
comment|/**    * Returns the time range before and after the current reservation considered    * by this policy. In particular, this informs the archival process for the    * {@link Plan}, i.e., reservations regarding times before (now - validWindow)    * can be deleted.    *     * @return validWindow the window of validity considered by the policy.    */
DECL|method|getValidWindow ()
specifier|public
name|long
name|getValidWindow
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

