begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapreduce.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|util
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
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|HostUtil
specifier|public
class|class
name|HostUtil
block|{
comment|/**    * Construct the taskLogUrl    * @param taskTrackerHostName    * @param httpPort    * @param taskAttemptID    * @return the taskLogUrl    */
DECL|method|getTaskLogUrl (String taskTrackerHostName, String httpPort, String taskAttemptID)
specifier|public
specifier|static
name|String
name|getTaskLogUrl
parameter_list|(
name|String
name|taskTrackerHostName
parameter_list|,
name|String
name|httpPort
parameter_list|,
name|String
name|taskAttemptID
parameter_list|)
block|{
return|return
operator|(
literal|"http://"
operator|+
name|taskTrackerHostName
operator|+
literal|":"
operator|+
name|httpPort
operator|+
literal|"/tasklog?attemptid="
operator|+
name|taskAttemptID
operator|)
return|;
block|}
DECL|method|convertTrackerNameToHostName (String trackerName)
specifier|public
specifier|static
name|String
name|convertTrackerNameToHostName
parameter_list|(
name|String
name|trackerName
parameter_list|)
block|{
comment|// Ugly!
comment|// Convert the trackerName to its host name
name|int
name|indexOfColon
init|=
name|trackerName
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|trackerHostName
init|=
operator|(
name|indexOfColon
operator|==
operator|-
literal|1
operator|)
condition|?
name|trackerName
else|:
name|trackerName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|indexOfColon
argument_list|)
decl_stmt|;
return|return
name|trackerHostName
operator|.
name|substring
argument_list|(
literal|"tracker_"
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

