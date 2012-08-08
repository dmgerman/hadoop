begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|scheduler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|rmcontainer
operator|.
name|RMContainer
import|;
end_import

begin_comment
comment|/**  * Represents an Application from the viewpoint of the scheduler.  * Each running Application in the RM corresponds to one instance  * of this class.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SchedulerApplication
specifier|public
specifier|abstract
class|class
name|SchedulerApplication
block|{
comment|/**    * Get {@link ApplicationAttemptId} of the application master.    * @return<code>ApplicationAttemptId</code> of the application master    */
DECL|method|getApplicationAttemptId ()
specifier|public
specifier|abstract
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
comment|/**    * Get the live containers of the application.    * @return live containers of the application    */
DECL|method|getLiveContainers ()
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|RMContainer
argument_list|>
name|getLiveContainers
parameter_list|()
function_decl|;
comment|/**    * Get the reserved containers of the application.    * @return the reserved containers of the application    */
DECL|method|getReservedContainers ()
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|RMContainer
argument_list|>
name|getReservedContainers
parameter_list|()
function_decl|;
comment|/**    * Is this application pending?    * @return true if it is else false.    */
DECL|method|isPending ()
specifier|public
specifier|abstract
name|boolean
name|isPending
parameter_list|()
function_decl|;
block|}
end_class

end_unit

