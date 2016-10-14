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
name|List
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
name|ipc
operator|.
name|CallerContext
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
name|api
operator|.
name|records
operator|.
name|ApplicationTimeoutType
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
name|LogAggregationStatus
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
name|Priority
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
name|ResourceRequest
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
name|YarnApplicationState
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
name|api
operator|.
name|protocolrecords
operator|.
name|LogAggregationReport
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
name|api
operator|.
name|records
operator|.
name|AppCollectorData
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
comment|/**  * The interface to an Application in the ResourceManager. Take a  * look at {@link RMAppImpl} for its implementation. This interface  * exposes methods to access various updates in application status/report.  */
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
comment|/**    * The application submission context for this {@link RMApp}    * @return the {@link ApplicationSubmissionContext} for this {@link RMApp}    */
DECL|method|getApplicationSubmissionContext ()
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
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
comment|/**    * Reflects a change in the application's queue from the one specified in the    * {@link ApplicationSubmissionContext}.    * @param name the new queue name    */
DECL|method|setQueue (String name)
name|void
name|setQueue
parameter_list|(
name|String
name|name
parameter_list|)
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
comment|/**    * To get the status of an application in the RM, this method can be used.    * If full access is not allowed then the following fields in the report    * will be stubbed:    *<ul>    *<li>host - set to "N/A"</li>    *<li>RPC port - set to -1</li>    *<li>client token - set to "N/A"</li>    *<li>diagnostics - set to "N/A"</li>    *<li>tracking URL - set to "N/A"</li>    *<li>original tracking URL - set to "N/A"</li>    *<li>resource usage report - all values are -1</li>    *</ul>    *    * @param clientUserName the user name of the client requesting the report    * @param allowAccess whether to allow full access to the report    * @return the {@link ApplicationReport} detailing the status of the application.    */
DECL|method|createAndGetApplicationReport (String clientUserName, boolean allowAccess)
name|ApplicationReport
name|createAndGetApplicationReport
parameter_list|(
name|String
name|clientUserName
parameter_list|,
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
comment|/**    * The timeline collector information for the application. It should be used    * only if the timeline service v.2 is enabled.    *    * @return the data for the application's collector, including collector    * address, collector ID. Return null if the timeline service v.2 is not    * enabled.    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getCollectorData ()
name|AppCollectorData
name|getCollectorData
parameter_list|()
function_decl|;
comment|/**    * The original tracking url for the application master.    * @return the original tracking url for the application master.    */
DECL|method|getOriginalTrackingUrl ()
name|String
name|getOriginalTrackingUrl
parameter_list|()
function_decl|;
comment|/**    * the diagnostics information for the application master.    * @return the diagnostics information for the application master.    */
DECL|method|getDiagnostics ()
name|StringBuilder
name|getDiagnostics
parameter_list|()
function_decl|;
comment|/**    * The final finish state of the AM when unregistering as in    * {@link FinishApplicationMasterRequest#setFinalApplicationStatus(FinalApplicationStatus)}.    * @return the final finish state of the AM as set in    * {@link FinishApplicationMasterRequest#setFinalApplicationStatus(FinalApplicationStatus)}.    */
DECL|method|getFinalApplicationStatus ()
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
function_decl|;
comment|/**    * The number of max attempts of the application.    * @return the number of max attempts of the application.    */
DECL|method|getMaxAppAttempts ()
name|int
name|getMaxAppAttempts
parameter_list|()
function_decl|;
comment|/**    * Returns the application type    * @return the application type.    */
DECL|method|getApplicationType ()
name|String
name|getApplicationType
parameter_list|()
function_decl|;
comment|/**    * Get tags for the application    * @return tags corresponding to the application    */
DECL|method|getApplicationTags ()
name|Set
argument_list|<
name|String
argument_list|>
name|getApplicationTags
parameter_list|()
function_decl|;
comment|/**    * Check whether this application's state has been saved to the state store.    * @return the flag indicating whether the applications's state is stored.    */
DECL|method|isAppFinalStateStored ()
name|boolean
name|isAppFinalStateStored
parameter_list|()
function_decl|;
comment|/**    * Nodes on which the containers for this {@link RMApp} ran.    * @return the set of nodes that ran any containers from this {@link RMApp}    * Add more node on which containers for this {@link RMApp} ran    */
DECL|method|getRanNodes ()
name|Set
argument_list|<
name|NodeId
argument_list|>
name|getRanNodes
parameter_list|()
function_decl|;
comment|/**    * Create the external user-facing state of ApplicationMaster from the    * current state of the {@link RMApp}.    * @return the external user-facing state of ApplicationMaster.    */
DECL|method|createApplicationState ()
name|YarnApplicationState
name|createApplicationState
parameter_list|()
function_decl|;
comment|/**    * Get RMAppMetrics of the {@link RMApp}.    *     * @return metrics    */
DECL|method|getRMAppMetrics ()
name|RMAppMetrics
name|getRMAppMetrics
parameter_list|()
function_decl|;
DECL|method|getReservationId ()
name|ReservationId
name|getReservationId
parameter_list|()
function_decl|;
DECL|method|getAMResourceRequests ()
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getAMResourceRequests
parameter_list|()
function_decl|;
DECL|method|getLogAggregationReportsForApp ()
name|Map
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|getLogAggregationReportsForApp
parameter_list|()
function_decl|;
DECL|method|getLogAggregationStatusForAppReport ()
name|LogAggregationStatus
name|getLogAggregationStatusForAppReport
parameter_list|()
function_decl|;
comment|/**    * Return the node label expression of the AM container.    */
DECL|method|getAmNodeLabelExpression ()
name|String
name|getAmNodeLabelExpression
parameter_list|()
function_decl|;
DECL|method|getAppNodeLabelExpression ()
name|String
name|getAppNodeLabelExpression
parameter_list|()
function_decl|;
DECL|method|getCallerContext ()
name|CallerContext
name|getCallerContext
parameter_list|()
function_decl|;
DECL|method|getApplicationTimeouts ()
name|Map
argument_list|<
name|ApplicationTimeoutType
argument_list|,
name|Long
argument_list|>
name|getApplicationTimeouts
parameter_list|()
function_decl|;
comment|/**    * Get priority of the application.    * @return priority    */
DECL|method|getApplicationPriority ()
name|Priority
name|getApplicationPriority
parameter_list|()
function_decl|;
comment|/**    * To verify whether app has reached in its completing/completed states.    *    * @return True/False to confirm whether app is in final states    */
DECL|method|isAppInCompletedStates ()
name|boolean
name|isAppInCompletedStates
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

