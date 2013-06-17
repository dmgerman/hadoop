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
comment|/**    * The token required by the clients to talk to the application attempt    * @return the token required by the clients to talk to the application attempt    */
DECL|method|getClientToAMToken ()
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
name|getClientToAMToken
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
comment|/**    * Nodes on which the containers for this {@link RMAppAttempt} ran.    * @return the set of nodes that ran any containers from this {@link RMAppAttempt}    */
DECL|method|getRanNodes ()
name|Set
argument_list|<
name|NodeId
argument_list|>
name|getRanNodes
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
comment|/**    * Return the list of last set of finished containers. This does not reset the    * finished containers.    * @return the list of just finished contianers, this does not reset the    * finished containers.    */
DECL|method|getJustFinishedContainers ()
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getJustFinishedContainers
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
comment|/**    * Get application container and resource usage information.    * @return an ApplicationResourceUsageReport object.    */
DECL|method|getApplicationResourceUsageReport ()
name|ApplicationResourceUsageReport
name|getApplicationResourceUsageReport
parameter_list|()
function_decl|;
comment|/**    * the start time of the application.    * @return the start time of the application.    */
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

