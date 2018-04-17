begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|api
operator|.
name|ApplicationClientProtocol
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * {@code ApplicationReport} is a report of an application.  *<p>  * It includes details such as:  *<ul>  *<li>{@link ApplicationId} of the application.</li>  *<li>Applications user.</li>  *<li>Application queue.</li>  *<li>Application name.</li>  *<li>Host on which the<code>ApplicationMaster</code> is running.</li>  *<li>RPC port of the<code>ApplicationMaster</code>.</li>  *<li>Tracking URL.</li>  *<li>{@link YarnApplicationState} of the application.</li>  *<li>Diagnostic information in case of errors.</li>  *<li>Start time of the application.</li>  *<li>Client {@link Token} of the application (if security is enabled).</li>  *</ul>  *  * @see ApplicationClientProtocol#getApplicationReport(org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ApplicationReport
specifier|public
specifier|abstract
class|class
name|ApplicationReport
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (ApplicationId applicationId, ApplicationAttemptId applicationAttemptId, String user, String queue, String name, String host, int rpcPort, Token clientToAMToken, YarnApplicationState state, String diagnostics, String url, long startTime, long launchTime, long finishTime, FinalApplicationStatus finalStatus, ApplicationResourceUsageReport appResources, String origTrackingUrl, float progress, String applicationType, Token amRmToken)
specifier|public
specifier|static
name|ApplicationReport
name|newInstance
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|rpcPort
parameter_list|,
name|Token
name|clientToAMToken
parameter_list|,
name|YarnApplicationState
name|state
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|String
name|url
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|launchTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|FinalApplicationStatus
name|finalStatus
parameter_list|,
name|ApplicationResourceUsageReport
name|appResources
parameter_list|,
name|String
name|origTrackingUrl
parameter_list|,
name|float
name|progress
parameter_list|,
name|String
name|applicationType
parameter_list|,
name|Token
name|amRmToken
parameter_list|)
block|{
name|ApplicationReport
name|report
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|report
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|report
operator|.
name|setCurrentApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|report
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|report
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|report
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|report
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|report
operator|.
name|setRpcPort
argument_list|(
name|rpcPort
argument_list|)
expr_stmt|;
name|report
operator|.
name|setClientToAMToken
argument_list|(
name|clientToAMToken
argument_list|)
expr_stmt|;
name|report
operator|.
name|setYarnApplicationState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|report
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
name|report
operator|.
name|setTrackingUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|report
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|report
operator|.
name|setLaunchTime
argument_list|(
name|launchTime
argument_list|)
expr_stmt|;
name|report
operator|.
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
name|report
operator|.
name|setFinalApplicationStatus
argument_list|(
name|finalStatus
argument_list|)
expr_stmt|;
name|report
operator|.
name|setApplicationResourceUsageReport
argument_list|(
name|appResources
argument_list|)
expr_stmt|;
name|report
operator|.
name|setOriginalTrackingUrl
argument_list|(
name|origTrackingUrl
argument_list|)
expr_stmt|;
name|report
operator|.
name|setProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|report
operator|.
name|setApplicationType
argument_list|(
name|applicationType
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAMRMToken
argument_list|(
name|amRmToken
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (ApplicationId applicationId, ApplicationAttemptId applicationAttemptId, String user, String queue, String name, String host, int rpcPort, Token clientToAMToken, YarnApplicationState state, String diagnostics, String url, long startTime, long finishTime, FinalApplicationStatus finalStatus, ApplicationResourceUsageReport appResources, String origTrackingUrl, float progress, String applicationType, Token amRmToken, Set<String> tags, boolean unmanagedApplication, Priority priority, String appNodeLabelExpression, String amNodeLabelExpression)
specifier|public
specifier|static
name|ApplicationReport
name|newInstance
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|rpcPort
parameter_list|,
name|Token
name|clientToAMToken
parameter_list|,
name|YarnApplicationState
name|state
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|String
name|url
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|FinalApplicationStatus
name|finalStatus
parameter_list|,
name|ApplicationResourceUsageReport
name|appResources
parameter_list|,
name|String
name|origTrackingUrl
parameter_list|,
name|float
name|progress
parameter_list|,
name|String
name|applicationType
parameter_list|,
name|Token
name|amRmToken
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|,
name|boolean
name|unmanagedApplication
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|String
name|appNodeLabelExpression
parameter_list|,
name|String
name|amNodeLabelExpression
parameter_list|)
block|{
name|ApplicationReport
name|report
init|=
name|newInstance
argument_list|(
name|applicationId
argument_list|,
name|applicationAttemptId
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
name|name
argument_list|,
name|host
argument_list|,
name|rpcPort
argument_list|,
name|clientToAMToken
argument_list|,
name|state
argument_list|,
name|diagnostics
argument_list|,
name|url
argument_list|,
name|startTime
argument_list|,
literal|0
argument_list|,
name|finishTime
argument_list|,
name|finalStatus
argument_list|,
name|appResources
argument_list|,
name|origTrackingUrl
argument_list|,
name|progress
argument_list|,
name|applicationType
argument_list|,
name|amRmToken
argument_list|)
decl_stmt|;
name|report
operator|.
name|setApplicationTags
argument_list|(
name|tags
argument_list|)
expr_stmt|;
name|report
operator|.
name|setUnmanagedApp
argument_list|(
name|unmanagedApplication
argument_list|)
expr_stmt|;
name|report
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAppNodeLabelExpression
argument_list|(
name|appNodeLabelExpression
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAmNodeLabelExpression
argument_list|(
name|amNodeLabelExpression
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (ApplicationId applicationId, ApplicationAttemptId applicationAttemptId, String user, String queue, String name, String host, int rpcPort, Token clientToAMToken, YarnApplicationState state, String diagnostics, String url, long startTime, long launchTime, long finishTime, FinalApplicationStatus finalStatus, ApplicationResourceUsageReport appResources, String origTrackingUrl, float progress, String applicationType, Token amRmToken, Set<String> tags, boolean unmanagedApplication, Priority priority, String appNodeLabelExpression, String amNodeLabelExpression)
specifier|public
specifier|static
name|ApplicationReport
name|newInstance
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|rpcPort
parameter_list|,
name|Token
name|clientToAMToken
parameter_list|,
name|YarnApplicationState
name|state
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|String
name|url
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|launchTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|FinalApplicationStatus
name|finalStatus
parameter_list|,
name|ApplicationResourceUsageReport
name|appResources
parameter_list|,
name|String
name|origTrackingUrl
parameter_list|,
name|float
name|progress
parameter_list|,
name|String
name|applicationType
parameter_list|,
name|Token
name|amRmToken
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|,
name|boolean
name|unmanagedApplication
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|String
name|appNodeLabelExpression
parameter_list|,
name|String
name|amNodeLabelExpression
parameter_list|)
block|{
name|ApplicationReport
name|report
init|=
name|newInstance
argument_list|(
name|applicationId
argument_list|,
name|applicationAttemptId
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
name|name
argument_list|,
name|host
argument_list|,
name|rpcPort
argument_list|,
name|clientToAMToken
argument_list|,
name|state
argument_list|,
name|diagnostics
argument_list|,
name|url
argument_list|,
name|startTime
argument_list|,
name|launchTime
argument_list|,
name|finishTime
argument_list|,
name|finalStatus
argument_list|,
name|appResources
argument_list|,
name|origTrackingUrl
argument_list|,
name|progress
argument_list|,
name|applicationType
argument_list|,
name|amRmToken
argument_list|)
decl_stmt|;
name|report
operator|.
name|setApplicationTags
argument_list|(
name|tags
argument_list|)
expr_stmt|;
name|report
operator|.
name|setUnmanagedApp
argument_list|(
name|unmanagedApplication
argument_list|)
expr_stmt|;
name|report
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAppNodeLabelExpression
argument_list|(
name|appNodeLabelExpression
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAmNodeLabelExpression
argument_list|(
name|amNodeLabelExpression
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
comment|/**    * Get the<code>ApplicationId</code> of the application.    * @return<code>ApplicationId</code> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationId (ApplicationId applicationId)
specifier|public
specifier|abstract
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ApplicationAttemptId</code> of the current    * attempt of the application    * @return<code>ApplicationAttemptId</code> of the attempt    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCurrentApplicationAttemptId ()
specifier|public
specifier|abstract
name|ApplicationAttemptId
name|getCurrentApplicationAttemptId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCurrentApplicationAttemptId (ApplicationAttemptId applicationAttemptId)
specifier|public
specifier|abstract
name|void
name|setCurrentApplicationAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
function_decl|;
comment|/**    * Get the<em>user</em> who submitted the application.    * @return<em>user</em> who submitted the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUser ()
specifier|public
specifier|abstract
name|String
name|getUser
parameter_list|()
function_decl|;
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
comment|/**    * Get the<em>queue</em> to which the application was submitted.    * @return<em>queue</em> to which the application was submitted    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueue ()
specifier|public
specifier|abstract
name|String
name|getQueue
parameter_list|()
function_decl|;
annotation|@
name|Private
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
comment|/**    * Get the user-defined<em>name</em> of the application.    * @return<em>name</em> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setName (String name)
specifier|public
specifier|abstract
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Get the<em>host</em> on which the<code>ApplicationMaster</code>    * is running.    * @return<em>host</em> on which the<code>ApplicationMaster</code>    *         is running    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHost ()
specifier|public
specifier|abstract
name|String
name|getHost
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setHost (String host)
specifier|public
specifier|abstract
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
function_decl|;
comment|/**    * Get the<em>RPC port</em> of the<code>ApplicationMaster</code>.    * @return<em>RPC port</em> of the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getRpcPort ()
specifier|public
specifier|abstract
name|int
name|getRpcPort
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRpcPort (int rpcPort)
specifier|public
specifier|abstract
name|void
name|setRpcPort
parameter_list|(
name|int
name|rpcPort
parameter_list|)
function_decl|;
comment|/**    * Get the<em>client token</em> for communicating with the    *<code>ApplicationMaster</code>.    *<p>    *<em>ClientToAMToken</em> is the security token used by the AMs to verify    * authenticity of any<code>client</code>.    *</p>    *    *<p>    * The<code>ResourceManager</code>, provides a secure token (via    * {@link ApplicationReport#getClientToAMToken()}) which is verified by the    * ApplicationMaster when the client directly talks to an AM.    *</p>    * @return<em>client token</em> for communicating with the    *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getClientToAMToken ()
specifier|public
specifier|abstract
name|Token
name|getClientToAMToken
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setClientToAMToken (Token clientToAMToken)
specifier|public
specifier|abstract
name|void
name|setClientToAMToken
parameter_list|(
name|Token
name|clientToAMToken
parameter_list|)
function_decl|;
comment|/**    * Get the<code>YarnApplicationState</code> of the application.    * @return<code>YarnApplicationState</code> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getYarnApplicationState ()
specifier|public
specifier|abstract
name|YarnApplicationState
name|getYarnApplicationState
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setYarnApplicationState (YarnApplicationState state)
specifier|public
specifier|abstract
name|void
name|setYarnApplicationState
parameter_list|(
name|YarnApplicationState
name|state
parameter_list|)
function_decl|;
comment|/**    * Get  the<em>diagnositic information</em> of the application in case of    * errors.    * @return<em>diagnositic information</em> of the application in case    *         of errors    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getDiagnostics ()
specifier|public
specifier|abstract
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setDiagnostics (String diagnostics)
specifier|public
specifier|abstract
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
function_decl|;
comment|/**    * Get the<em>tracking url</em> for the application.    * @return<em>tracking url</em> for the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getTrackingUrl ()
specifier|public
specifier|abstract
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setTrackingUrl (String url)
specifier|public
specifier|abstract
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|url
parameter_list|)
function_decl|;
comment|/**    * Get the original not-proxied<em>tracking url</em> for the application.    * This is intended to only be used by the proxy itself.    * @return the original not-proxied<em>tracking url</em> for the application    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getOriginalTrackingUrl ()
specifier|public
specifier|abstract
name|String
name|getOriginalTrackingUrl
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setOriginalTrackingUrl (String url)
specifier|public
specifier|abstract
name|void
name|setOriginalTrackingUrl
parameter_list|(
name|String
name|url
parameter_list|)
function_decl|;
comment|/**    * Get the<em>start time</em> of the application.    * @return<em>start time</em> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getStartTime ()
specifier|public
specifier|abstract
name|long
name|getStartTime
parameter_list|()
function_decl|;
annotation|@
name|Private
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setLaunchTime (long setLaunchTime)
specifier|public
specifier|abstract
name|void
name|setLaunchTime
parameter_list|(
name|long
name|setLaunchTime
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getLaunchTime ()
specifier|public
specifier|abstract
name|long
name|getLaunchTime
parameter_list|()
function_decl|;
comment|/**    * Get the<em>finish time</em> of the application.    * @return<em>finish time</em> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getFinishTime ()
specifier|public
specifier|abstract
name|long
name|getFinishTime
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setFinishTime (long finishTime)
specifier|public
specifier|abstract
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
function_decl|;
comment|/**    * Get the<em>final finish status</em> of the application.    * @return<em>final finish status</em> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getFinalApplicationStatus ()
specifier|public
specifier|abstract
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setFinalApplicationStatus (FinalApplicationStatus finishState)
specifier|public
specifier|abstract
name|void
name|setFinalApplicationStatus
parameter_list|(
name|FinalApplicationStatus
name|finishState
parameter_list|)
function_decl|;
comment|/**    * Retrieve the structure containing the job resources for this application    * @return the job resources structure for this application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationResourceUsageReport ()
specifier|public
specifier|abstract
name|ApplicationResourceUsageReport
name|getApplicationResourceUsageReport
parameter_list|()
function_decl|;
comment|/**    * Store the structure containing the job resources for this application    * @param appResources structure for this application    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationResourceUsageReport (ApplicationResourceUsageReport appResources)
specifier|public
specifier|abstract
name|void
name|setApplicationResourceUsageReport
parameter_list|(
name|ApplicationResourceUsageReport
name|appResources
parameter_list|)
function_decl|;
comment|/**    * Get the application's progress ( range 0.0 to 1.0 )    * @return application's progress    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setProgress (float progress)
specifier|public
specifier|abstract
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
function_decl|;
comment|/**    * Get the application's Type     * @return application's Type    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationType ()
specifier|public
specifier|abstract
name|String
name|getApplicationType
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationType (String applicationType)
specifier|public
specifier|abstract
name|void
name|setApplicationType
parameter_list|(
name|String
name|applicationType
parameter_list|)
function_decl|;
comment|/**    * Get all tags corresponding to the application    * @return Application's tags    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationTags ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getApplicationTags
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationTags (Set<String> tags)
specifier|public
specifier|abstract
name|void
name|setApplicationTags
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Stable
DECL|method|setAMRMToken (Token amRmToken)
specifier|public
specifier|abstract
name|void
name|setAMRMToken
parameter_list|(
name|Token
name|amRmToken
parameter_list|)
function_decl|;
comment|/**    * Get the AMRM token of the application.    *<p>    * The AMRM token is required for AM to RM scheduling operations. For     * managed Application Masters YARN takes care of injecting it. For unmanaged    * Applications Masters, the token must be obtained via this method and set    * in the {@link org.apache.hadoop.security.UserGroupInformation} of the    * current user.    *<p>    * The AMRM token will be returned only if all the following conditions are    * met:    *<ul>    *<li>the requester is the owner of the ApplicationMaster</li>    *<li>the application master is an unmanaged ApplicationMaster</li>    *<li>the application master is in ACCEPTED state</li>    *</ul>    * Else this method returns NULL.    *     * @return the AM to RM token if available.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAMRMToken ()
specifier|public
specifier|abstract
name|Token
name|getAMRMToken
parameter_list|()
function_decl|;
comment|/**    * Get log aggregation status for the application    * @return Application's log aggregation status    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getLogAggregationStatus ()
specifier|public
specifier|abstract
name|LogAggregationStatus
name|getLogAggregationStatus
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setLogAggregationStatus ( LogAggregationStatus logAggregationStatus)
specifier|public
specifier|abstract
name|void
name|setLogAggregationStatus
parameter_list|(
name|LogAggregationStatus
name|logAggregationStatus
parameter_list|)
function_decl|;
comment|/**    * @return true if the AM is not managed by the RM    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|isUnmanagedApp ()
specifier|public
specifier|abstract
name|boolean
name|isUnmanagedApp
parameter_list|()
function_decl|;
comment|/**    * @param unmanagedApplication true if RM should not manage the AM    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setUnmanagedApp (boolean unmanagedApplication)
specifier|public
specifier|abstract
name|void
name|setUnmanagedApp
parameter_list|(
name|boolean
name|unmanagedApplication
parameter_list|)
function_decl|;
comment|/**    * Get priority of the application    *    * @return Application's priority    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|Priority
name|getPriority
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPriority (Priority priority)
specifier|public
specifier|abstract
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
comment|/**    * Get the default Node Label expression for all the application's containers    *    * @return Application's NodeLabelExpression    */
annotation|@
name|Unstable
DECL|method|getAppNodeLabelExpression ()
specifier|public
specifier|abstract
name|String
name|getAppNodeLabelExpression
parameter_list|()
function_decl|;
annotation|@
name|Unstable
DECL|method|setAppNodeLabelExpression (String appNodeLabelExpression)
specifier|public
specifier|abstract
name|void
name|setAppNodeLabelExpression
parameter_list|(
name|String
name|appNodeLabelExpression
parameter_list|)
function_decl|;
comment|/**    * Get the default Node Label expression for all the application's containers    *    * @return Application's NodeLabelExpression    */
annotation|@
name|Unstable
DECL|method|getAmNodeLabelExpression ()
specifier|public
specifier|abstract
name|String
name|getAmNodeLabelExpression
parameter_list|()
function_decl|;
annotation|@
name|Unstable
DECL|method|setAmNodeLabelExpression (String amNodeLabelExpression)
specifier|public
specifier|abstract
name|void
name|setAmNodeLabelExpression
parameter_list|(
name|String
name|amNodeLabelExpression
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getApplicationTimeouts ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|ApplicationTimeout
argument_list|>
name|getApplicationTimeouts
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationTimeouts ( Map<ApplicationTimeoutType, ApplicationTimeout> timeouts)
specifier|public
specifier|abstract
name|void
name|setApplicationTimeouts
parameter_list|(
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|ApplicationTimeout
argument_list|>
name|timeouts
parameter_list|)
function_decl|;
block|}
end_class

end_unit

