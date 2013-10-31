begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery.records
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
name|recovery
operator|.
name|records
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptState
import|;
end_import

begin_comment
comment|/*  * Contains the state data that needs to be persisted for an ApplicationAttempt  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Unstable
DECL|interface|ApplicationAttemptStateData
specifier|public
interface|interface
name|ApplicationAttemptStateData
block|{
comment|/**    * The ApplicationAttemptId for the application attempt    * @return ApplicationAttemptId for the application attempt    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getAttemptId
parameter_list|()
function_decl|;
DECL|method|setAttemptId (ApplicationAttemptId attemptId)
specifier|public
name|void
name|setAttemptId
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
function_decl|;
comment|/*    * The master container running the application attempt    * @return Container that hosts the attempt    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getMasterContainer ()
specifier|public
name|Container
name|getMasterContainer
parameter_list|()
function_decl|;
DECL|method|setMasterContainer (Container container)
specifier|public
name|void
name|setMasterContainer
parameter_list|(
name|Container
name|container
parameter_list|)
function_decl|;
comment|/**    * The application attempt tokens that belong to this attempt    * @return The application attempt tokens that belong to this attempt    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAppAttemptTokens ()
specifier|public
name|ByteBuffer
name|getAppAttemptTokens
parameter_list|()
function_decl|;
DECL|method|setAppAttemptTokens (ByteBuffer attemptTokens)
specifier|public
name|void
name|setAppAttemptTokens
parameter_list|(
name|ByteBuffer
name|attemptTokens
parameter_list|)
function_decl|;
comment|/**    * Get the final state of the application attempt.    * @return the final state of the application attempt.    */
DECL|method|getState ()
specifier|public
name|RMAppAttemptState
name|getState
parameter_list|()
function_decl|;
DECL|method|setState (RMAppAttemptState state)
specifier|public
name|void
name|setState
parameter_list|(
name|RMAppAttemptState
name|state
parameter_list|)
function_decl|;
comment|/**    * Get the original not-proxied<em>final tracking url</em> for the    * application. This is intended to only be used by the proxy itself.    *     * @return the original not-proxied<em>final tracking url</em> for the    *         application    */
DECL|method|getFinalTrackingUrl ()
specifier|public
name|String
name|getFinalTrackingUrl
parameter_list|()
function_decl|;
comment|/**    * Set the final tracking Url of the AM.    * @param url    */
DECL|method|setFinalTrackingUrl (String url)
specifier|public
name|void
name|setFinalTrackingUrl
parameter_list|(
name|String
name|url
parameter_list|)
function_decl|;
comment|/**    * Get the<em>diagnositic information</em> of the attempt     * @return<em>diagnositic information</em> of the attempt    */
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|setDiagnostics (String diagnostics)
specifier|public
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
function_decl|;
comment|/**    * Get the<em>start time</em> of the application.    * @return<em>start time</em> of the application    */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
function_decl|;
DECL|method|setStartTime (long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
function_decl|;
comment|/**    * Get the<em>final finish status</em> of the application.    * @return<em>final finish status</em> of the application    */
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
function_decl|;
DECL|method|setFinalApplicationStatus (FinalApplicationStatus finishState)
specifier|public
name|void
name|setFinalApplicationStatus
parameter_list|(
name|FinalApplicationStatus
name|finishState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

