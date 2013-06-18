begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
name|protocolrecords
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationMasterProtocol
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>The finalization request sent by the<code>ApplicationMaster</code> to  * inform the<code>ResourceManager</code> about its completion.</p>  *  *<p>The final request includes details such:  *<ul>  *<li>  *         {@link ApplicationAttemptId} being managed by the  *<code>ApplicationMaster</code>  *</li>  *<li>Final state of the<code>ApplicationMaster</code></li>  *<li>  *       Diagnostic information in case of failure of the  *<code>ApplicationMaster</code>  *</li>  *<li>Tracking URL</li>  *</ul>  *</p>  *  * @see ApplicationMasterProtocol#finishApplicationMaster(FinishApplicationMasterRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|FinishApplicationMasterRequest
specifier|public
specifier|abstract
class|class
name|FinishApplicationMasterRequest
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance ( ApplicationAttemptId appAttemptId, FinalApplicationStatus finalAppStatus, String diagnostics, String url)
specifier|public
specifier|static
name|FinishApplicationMasterRequest
name|newInstance
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|FinalApplicationStatus
name|finalAppStatus
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|FinishApplicationMasterRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setFinalApplicationStatus
argument_list|(
name|finalAppStatus
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTrackingUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Get the<code>ApplicationAttemptId</code> being managed by the    *<code>ApplicationMaster</code>.    * @return<code>ApplicationAttemptId</code> being managed by the    *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationAttemptId ()
specifier|public
specifier|abstract
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationAttemptId</code> being managed by the    *<code>ApplicationMaster</code>.    * @param applicationAttemptId<code>ApplicationAttemptId</code> being managed    *                             by the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setAppAttemptId (ApplicationAttemptId applicationAttemptId)
specifier|public
specifier|abstract
name|void
name|setAppAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
function_decl|;
comment|/**    * Get<em>final state</em> of the<code>ApplicationMaster</code>.    * @return<em>final state</em> of the<code>ApplicationMaster</code>    */
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
comment|/**    * Set the<em>final state</em> of the<code>ApplicationMaster</code>    * @param finalState<em>final state</em> of the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setFinalApplicationStatus (FinalApplicationStatus finalState)
specifier|public
specifier|abstract
name|void
name|setFinalApplicationStatus
parameter_list|(
name|FinalApplicationStatus
name|finalState
parameter_list|)
function_decl|;
comment|/**    * Get<em>diagnostic information</em> on application failure.    * @return<em>diagnostic information</em> on application failure    */
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
comment|/**    * Set<em>diagnostic information</em> on application failure.    * @param diagnostics<em>diagnostic information</em> on application failure    */
annotation|@
name|Public
annotation|@
name|Stable
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
comment|/**    * Get the<em>tracking URL</em> for the<code>ApplicationMaster</code>.    * @return<em>tracking URL</em>for the<code>ApplicationMaster</code>    */
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
comment|/**    * Set the<em>tracking URL</em>for the<code>ApplicationMaster</code>    * @param url<em>tracking URL</em>for the    *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
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
block|}
end_class

end_unit

