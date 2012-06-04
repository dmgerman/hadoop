begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|ApplicationId
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
name|ApplicationReport
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
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|ApplicationsStore
operator|.
name|ApplicationStore
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
name|attempt
operator|.
name|RMAppAttempt
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
name|rmnode
operator|.
name|RMNode
import|;
end_import

begin_comment
comment|/**  * The read interface to an Application in the ResourceManager. Take a  * look at {@link RMAppImpl} for its implementation. This interface  * exposes methods to access various updates in application status/report.  */
end_comment

begin_interface
DECL|interface|RMApp
specifier|public
interface|interface
name|RMApp
extends|extends
name|EventHandler
argument_list|<
name|RMAppEvent
argument_list|>
block|{
comment|/**    * The application id for this {@link RMApp}.    * @return the {@link ApplicationId} for this {@link RMApp}.    */
DECL|method|getApplicationId ()
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
comment|/**    * The current state of the {@link RMApp}.    * @return the current state {@link RMAppState} for this application.    */
DECL|method|getState ()
name|RMAppState
name|getState
parameter_list|()
function_decl|;
comment|/**    * The user who submitted this application.    * @return the user who submitted the application.    */
DECL|method|getUser ()
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Progress of application.    * @return the progress of the {@link RMApp}.    */
DECL|method|getProgress ()
name|float
name|getProgress
parameter_list|()
function_decl|;
comment|/**    * {@link RMApp} can have multiple application attempts {@link RMAppAttempt}.    * This method returns the {@link RMAppAttempt} corresponding to    *  {@link ApplicationAttemptId}.    * @param appAttemptId the application attempt id    * @return  the {@link RMAppAttempt} corresponding to the {@link ApplicationAttemptId}.    */
DECL|method|getRMAppAttempt (ApplicationAttemptId appAttemptId)
name|RMAppAttempt
name|getRMAppAttempt
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
function_decl|;
comment|/**    * Each Application is submitted to a queue decided by {@link    * ApplicationSubmissionContext#setQueue(String)}.    * This method returns the queue to which an application was submitted.    * @return the queue to which the application was submitted to.    */
DECL|method|getQueue ()
name|String
name|getQueue
parameter_list|()
function_decl|;
comment|/**    * The name of the application as set in {@link    * ApplicationSubmissionContext#setApplicationName(String)}.    * @return the name of the application.    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * {@link RMApp} can have multiple application attempts {@link RMAppAttempt}.    * This method returns the current {@link RMAppAttempt}.    * @return the current {@link RMAppAttempt}    */
DECL|method|getCurrentAppAttempt ()
name|RMAppAttempt
name|getCurrentAppAttempt
parameter_list|()
function_decl|;
comment|/**    * {@link RMApp} can have multiple application attempts {@link RMAppAttempt}.    * This method returns the all {@link RMAppAttempt}s for the RMApp.    * @return all {@link RMAppAttempt}s for the RMApp.    */
DECL|method|getAppAttempts ()
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|RMAppAttempt
argument_list|>
name|getAppAttempts
parameter_list|()
function_decl|;
comment|/**    * To get the status of an application in the RM, this method can be used.    * If full access is not allowed then the following fields in the report    * will be stubbed:    *<ul>    *<li>host - set to "N/A"</li>    *<li>RPC port - set to -1</li>    *<li>client token - set to "N/A"</li>    *<li>diagnostics - set to "N/A"</li>    *<li>tracking URL - set to "N/A"</li>    *<li>original tracking URL - set to "N/A"</li>    *<li>resource usage report - all values are -1</li>    *</ul>    *    * @param allowAccess whether to allow full access to the report    * @return the {@link ApplicationReport} detailing the status of the application.    */
DECL|method|createAndGetApplicationReport (boolean allowAccess)
name|ApplicationReport
name|createAndGetApplicationReport
parameter_list|(
name|boolean
name|allowAccess
parameter_list|)
function_decl|;
comment|/**    * To receive the collection of all {@link RMNode}s whose updates have been    * received by the RMApp. Updates can be node becoming lost or becoming    * healthy etc. The method clears the information from the {@link RMApp}. So    * each call to this method gives the delta from the previous call.    * @param updatedNodes Collection into which the updates are transferred    * @return the number of nodes added to the {@link Collection}    */
DECL|method|pullRMNodeUpdates (Collection<RMNode> updatedNodes)
name|int
name|pullRMNodeUpdates
parameter_list|(
name|Collection
argument_list|<
name|RMNode
argument_list|>
name|updatedNodes
parameter_list|)
function_decl|;
comment|/**    * Application level metadata is stored in {@link ApplicationStore} which    * can persist the information.    * @return the {@link ApplicationStore}  for this {@link RMApp}.    */
DECL|method|getApplicationStore ()
name|ApplicationStore
name|getApplicationStore
parameter_list|()
function_decl|;
comment|/**    * The finish time of the {@link RMApp}    * @return the finish time of the application.,    */
DECL|method|getFinishTime ()
name|long
name|getFinishTime
parameter_list|()
function_decl|;
comment|/**    * the start time of the application.    * @return the start time of the application.    */
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/**    * the submit time of the application.    * @return the submit time of the application.    */
DECL|method|getSubmitTime ()
name|long
name|getSubmitTime
parameter_list|()
function_decl|;
comment|/**    * The tracking url for the application master.    * @return the tracking url for the application master.    */
DECL|method|getTrackingUrl ()
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
comment|/**    * the diagnostics information for the application master.    * @return the diagnostics information for the application master.    */
DECL|method|getDiagnostics ()
name|StringBuilder
name|getDiagnostics
parameter_list|()
function_decl|;
comment|/**    * The final finish state of the AM when unregistering as in    * {@link FinishApplicationMasterRequest#setFinishApplicationStatus(FinalApplicationStatus)}.    * @return the final finish state of the AM as set in    * {@link FinishApplicationMasterRequest#setFinishApplicationStatus(FinalApplicationStatus)}.    */
DECL|method|getFinalApplicationStatus ()
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

