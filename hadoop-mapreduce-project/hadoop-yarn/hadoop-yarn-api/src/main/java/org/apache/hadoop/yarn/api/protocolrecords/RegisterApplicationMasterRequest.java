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
name|AMRMProtocol
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

begin_comment
comment|/**  *<p>The request sent by the<code>ApplicationMaster</code> to   *<code>ResourceManager</code> on registration.</p>  *   *<p>The registration includes details such as:  *<ul>  *<li>  *         {@link ApplicationAttemptId} being managed by the   *<code>ApplicationMaster</code>  *</li>  *<li>Hostname on which the AM is running.</li>  *<li>RPC Port</li>  *<li>Tracking URL</li>  *</ul>  *</p>  *   * @see AMRMProtocol#registerApplicationMaster(RegisterApplicationMasterRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|RegisterApplicationMasterRequest
specifier|public
interface|interface
name|RegisterApplicationMasterRequest
block|{
comment|/**    * Get the<code>ApplicationAttemptId</code> being managed by the     *<code>ApplicationMaster</code>.    * @return<code>ApplicationAttemptId</code> being managed by the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationAttemptId ()
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationAttemptId</code> being managed by the     *<code>ApplicationMaster</code>.    * @param applicationAttemptId<code>ApplicationAttemptId</code> being managed      *                             by the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setApplicationAttemptId (ApplicationAttemptId applicationAttemptId)
name|void
name|setApplicationAttemptId
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|)
function_decl|;
comment|/**    * Get the<em>host</em> on which the<code>ApplicationMaster</code> is     * running.    * @return<em>host</em> on which the<code>ApplicationMaster</code> is running    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHost ()
name|String
name|getHost
parameter_list|()
function_decl|;
comment|/**    * Set the<em>host</em> on which the<code>ApplicationMaster</code> is     * running.    * @param host<em>host</em> on which the<code>ApplicationMaster</code>     *             is running    */
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
comment|/**    * Get the<em>RPC port</em> on which the<code>ApplicationMaster</code>     * is responding.     * @return the<em>RPC port<em> on which the<code>ApplicationMaster</code> is     *         responding    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getRpcPort ()
name|int
name|getRpcPort
parameter_list|()
function_decl|;
comment|/**    * Set the<em>RPC port<em> on which the<code>ApplicationMaster</code> is     * responding.    * @param port<em>RPC port<em> on which the<code>ApplicationMaster</code> is     *             responding    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setRpcPort (int port)
name|void
name|setRpcPort
parameter_list|(
name|int
name|port
parameter_list|)
function_decl|;
comment|/**    * Get the<em>tracking URL</em> for the<code>ApplicationMaster</code>.    * @return<em>tracking URL</em> for the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getTrackingUrl ()
name|String
name|getTrackingUrl
parameter_list|()
function_decl|;
comment|/**    * Set the<em>tracking URL</em> for the<code>ApplicationMaster</code>.    * @param trackingUrl<em>tracking URL</em> for the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setTrackingUrl (String trackingUrl)
name|void
name|setTrackingUrl
parameter_list|(
name|String
name|trackingUrl
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

