begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt
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
name|rmapp
operator|.
name|attempt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptReport
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
name|ApplicationResourceUsageReport
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
name|ApplicationSubmissionContext
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
name|Container
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
name|ContainerStatus
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
name|FinalApplicationStatus
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
name|NodeId
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
name|YarnApplicationAttemptState
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
name|conf
operator|.
name|YarnConfiguration
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
name|event
operator|.
name|EventHandler
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
name|security
operator|.
name|AMRMTokenIdentifier
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
name|security
operator|.
name|client
operator|.
name|ClientToAMTokenIdentifier
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
name|blacklist
operator|.
name|BlacklistManager
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
name|rmapp
operator|.
name|RMApp
import|;
end_import

begin_comment
comment|/**  * Interface to an Application Attempt in the Resource Manager.  * A {@link RMApp} can have multiple app attempts based on  * {@link YarnConfiguration#RM_AM_MAX_ATTEMPTS}. For specific  * implementation take a look at {@link RMAppAttemptImpl}.  */
end_comment

begin_interface
DECL|interface|RMAppAttempt
specifier|public
interface|interface
name|RMAppAttempt
extends|extends
name|EventHandler
argument_list|<
name|RMAppAttemptEvent
argument_list|>
block|{
comment|/**    * Get the application attempt id for this {@link RMAppAttempt}.    * @return the {@link ApplicationAttemptId} for this RM attempt.    */
DECL|method|getAppAttemptId ()
name|ApplicationAttemptId
name|getAppAttemptId
parameter_list|()
function_decl|;
comment|/**    * The state of the {@link RMAppAttempt}.    * @return the state {@link RMAppAttemptState} of this {@link RMAppAttempt}    */
DECL|method|getAppAttemptState ()
name|RMAppAttemptState
name|getAppAttemptState
parameter_list|()
function_decl|;
comment|/**    * The host on which the {@link RMAppAttempt} is running/ran on.    * @return the host on which the {@link RMAppAttempt} ran/is running on.    */
DECL|method|getHost ()
name|String
name|getHost
parameter_list|()
function_decl|;
comment|/**    * The rpc port of the {@link RMAppAttempt}.    * @return the rpc port of the {@link RMAppAttempt} to which the clients can connect    * to.    */
DECL|method|getRpcPort ()
name|int
name|getRpcPort
parameter_list|()
function_decl|;
comment|/**    * The url at which the status of the application attempt can be accessed.    * @return the url at which the status of the attempt can be accessed.    */
DECL|method|getTrackingUrl ()
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
comment|/**    * The original url at which the status of the application attempt can be     * accessed. This url is not fronted by a proxy. This is only intended to be    * used by the proxy.    * @return the url at which the status of the attempt can be accessed and is    * not fronted by a proxy.    */
DECL|method|getOriginalTrackingUrl ()
name|String
name|getOriginalTrackingUrl
parameter_list|()
function_decl|;
comment|/**    * The base to be prepended to web URLs that are not relative, and the user    * has been checked.    * @return the base URL to be prepended to web URLs that are not relative.    */
DECL|method|getWebProxyBase ()
name|String
name|getWebProxyBase
parameter_list|()
function_decl|;
comment|/**    * Diagnostics information for the application attempt.    * @return diagnostics information for the application attempt.    */
DECL|method|getDiagnostics ()
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
comment|/**    * Progress for the application attempt.    * @return the progress for this {@link RMAppAttempt}    */
DECL|method|getProgress ()
name|float
name|getProgress
parameter_list|()
function_decl|;
comment|/**    * The final status set by the AM.    * @return the final status that is set by the AM when unregistering itself. Can return a null     * if the AM has not unregistered itself.     */
DECL|method|getFinalApplicationStatus ()
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
function_decl|;
comment|/**    * Return a list of the last set of finished containers, resetting the    * finished containers to empty.    * @return the list of just finished containers, re setting the finished containers.    */
DECL|method|pullJustFinishedContainers ()
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|pullJustFinishedContainers
parameter_list|()
function_decl|;
comment|/**    * Returns a reference to the map of last set of finished containers to the    * corresponding node. This does not reset the finished containers.    * @return the list of just finished containers, this does not reset the    * finished containers.    */
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
DECL|method|getJustFinishedContainersReference ()
name|getJustFinishedContainersReference
parameter_list|()
function_decl|;
comment|/**    * Return the list of last set of finished containers. This does not reset    * the finished containers.    * @return the list of just finished containers    */
DECL|method|getJustFinishedContainers ()
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getJustFinishedContainers
parameter_list|()
function_decl|;
comment|/**    * The map of conatiners per Node that are already sent to the AM.    * @return map of per node list of finished container status sent to AM    */
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|>
DECL|method|getFinishedContainersSentToAMReference ()
name|getFinishedContainersSentToAMReference
parameter_list|()
function_decl|;
comment|/**    * The container on which the Application Master is running.    * @return the {@link Container} on which the application master is running.    */
DECL|method|getMasterContainer ()
name|Container
name|getMasterContainer
parameter_list|()
function_decl|;
comment|/**    * The application submission context for this {@link RMAppAttempt}.    * @return the application submission context for this Application.    */
DECL|method|getSubmissionContext ()
name|ApplicationSubmissionContext
name|getSubmissionContext
parameter_list|()
function_decl|;
comment|/**    * The AMRMToken belonging to this app attempt    * @return The AMRMToken belonging to this app attempt    */
DECL|method|getAMRMToken ()
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|getAMRMToken
parameter_list|()
function_decl|;
comment|/**    * The master key for client-to-AM tokens for this app attempt. This is only    * used for RMStateStore. Normal operation must invoke the secret manager to    * get the key and not use the local key directly.    * @return The master key for client-to-AM tokens for this app attempt    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"RMStateStore"
argument_list|)
DECL|method|getClientTokenMasterKey ()
name|SecretKey
name|getClientTokenMasterKey
parameter_list|()
function_decl|;
comment|/**    * Create a token for authenticating a client connection to the app attempt    * @param clientName the name of the client requesting the token    * @return the token or null if the attempt is not running    */
DECL|method|createClientToken (String clientName)
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
name|createClientToken
parameter_list|(
name|String
name|clientName
parameter_list|)
function_decl|;
comment|/**    * Get application container and resource usage information.    * @return an ApplicationResourceUsageReport object.    */
DECL|method|getApplicationResourceUsageReport ()
name|ApplicationResourceUsageReport
name|getApplicationResourceUsageReport
parameter_list|()
function_decl|;
comment|/**    * Get the {@link BlacklistManager} that manages blacklists for AM failures    * @return the {@link BlacklistManager} that tracks AM failures.    */
DECL|method|getAMBlacklist ()
name|BlacklistManager
name|getAMBlacklist
parameter_list|()
function_decl|;
comment|/**    * the start time of the application.    * @return the start time of the application.    */
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/**    * The current state of the {@link RMAppAttempt}.    *     * @return the current state {@link RMAppAttemptState} for this application    *         attempt.    */
DECL|method|getState ()
name|RMAppAttemptState
name|getState
parameter_list|()
function_decl|;
comment|/**    * Create the external user-facing state of the attempt of ApplicationMaster    * from the current state of the {@link RMAppAttempt}.    *     * @return the external user-facing state of the attempt ApplicationMaster.    */
DECL|method|createApplicationAttemptState ()
name|YarnApplicationAttemptState
name|createApplicationAttemptState
parameter_list|()
function_decl|;
comment|/**    * Create the Application attempt report from the {@link RMAppAttempt}    *     * @return {@link ApplicationAttemptReport}    */
DECL|method|createApplicationAttemptReport ()
name|ApplicationAttemptReport
name|createApplicationAttemptReport
parameter_list|()
function_decl|;
comment|/**    * Return the flag which indicates whether the attempt failure should be    * counted to attempt retry count.    *<p>    * There failure types should not be counted to attempt retry count:    *<ul>    *<li>preempted by the scheduler.</li>    *<li>    *     hardware failures, such as NM failing, lost NM and NM disk errors.    *</li>    *<li>killed by RM because of RM restart or failover.</li>    *</ul>    */
DECL|method|shouldCountTowardsMaxAttemptRetry ()
name|boolean
name|shouldCountTowardsMaxAttemptRetry
parameter_list|()
function_decl|;
comment|/**    * Get metrics from the {@link RMAppAttempt}    * @return metrics    */
DECL|method|getRMAppAttemptMetrics ()
name|RMAppAttemptMetrics
name|getRMAppAttemptMetrics
parameter_list|()
function_decl|;
comment|/**    * the finish time of the application attempt.    * @return the finish time of the application attempt.    */
DECL|method|getFinishTime ()
name|long
name|getFinishTime
parameter_list|()
function_decl|;
comment|/**    * To capture Launch diagnostics of the app.    * @param amLaunchDiagnostics    */
DECL|method|updateAMLaunchDiagnostics (String amLaunchDiagnostics)
name|void
name|updateAMLaunchDiagnostics
parameter_list|(
name|String
name|amLaunchDiagnostics
parameter_list|)
function_decl|;
comment|/**    * @return Set of nodes which are blacklisted by the application    */
DECL|method|getBlacklistedNodes ()
name|Set
argument_list|<
name|String
argument_list|>
name|getBlacklistedNodes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

