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
name|ClientRMProtocol
import|;
end_import

begin_comment
comment|/**  *<p><code>ApplicationReport</code> is a report of an application.</p>  *  *<p>It includes details such as:  *<ul>  *<li>{@link ApplicationId} of the application.</li>  *<li>Applications user.</li>  *<li>Application queue.</li>  *<li>Application name.</li>  *<li>Host on which the<code>ApplicationMaster</code> is running.</li>  *<li>RPC port of the<code>ApplicationMaster</code>.</li>  *<li>Tracking URL.</li>  *<li>{@link YarnApplicationState} of the application.</li>  *<li>Diagnostic information in case of errors.</li>  *<li>Start time of the application.</li>  *<li>Client token of the application (if security is enabled).</li>  *</ul>  *</p>  *  * @see ClientRMProtocol#getApplicationReport(org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ApplicationReport
specifier|public
interface|interface
name|ApplicationReport
block|{
comment|/**    * Get the<code>ApplicationId</code> of the application.    * @return<code>ApplicationId</code> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationId ()
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationId (ApplicationId applicationId)
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ApplicationAttemptId</code> of the current    * attempt of the application    * @return<code>ApplicationAttemptId</code> of the attempt    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getCurrentApplicationAttemptId ()
name|ApplicationAttemptId
name|getCurrentApplicationAttemptId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCurrentApplicationAttemptId (ApplicationAttemptId applicationAttemptId)
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
name|String
name|getUser
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setUser (String user)
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
name|String
name|getQueue
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueue (String queue)
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
name|String
name|getName
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setName (String name)
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
name|String
name|getHost
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setHost (String host)
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
name|int
name|getRpcPort
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRpcPort (int rpcPort)
name|void
name|setRpcPort
parameter_list|(
name|int
name|rpcPort
parameter_list|)
function_decl|;
comment|/**    * Get the<em>client token</em> for communicating with the    *<code>ApplicationMaster</code>.    * @return<em>client token</em> for communicating with the    *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getClientToken ()
name|ClientToken
name|getClientToken
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setClientToken (ClientToken clientToken)
name|void
name|setClientToken
parameter_list|(
name|ClientToken
name|clientToken
parameter_list|)
function_decl|;
comment|/**    * Get the<code>YarnApplicationState</code> of the application.    * @return<code>YarnApplicationState</code> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getYarnApplicationState ()
name|YarnApplicationState
name|getYarnApplicationState
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setYarnApplicationState (YarnApplicationState state)
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
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setDiagnostics (String diagnostics)
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
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setTrackingUrl (String url)
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
name|String
name|getOriginalTrackingUrl
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setOriginalTrackingUrl (String url)
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
name|long
name|getStartTime
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setStartTime (long startTime)
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
function_decl|;
comment|/**    * Get the<em>finish time</em> of the application.    * @return<em>finish time</em> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getFinishTime ()
name|long
name|getFinishTime
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setFinishTime (long finishTime)
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
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setFinalApplicationStatus (FinalApplicationStatus finishState)
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
name|float
name|getProgress
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setProgress (float progress)
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
name|String
name|getApplicationType
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationType (String applicationType)
name|void
name|setApplicationType
parameter_list|(
name|String
name|applicationType
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

