begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  *<p><code>ApplicationSubmissionContext</code> represents all of the  * information needed by the<code>ResourceManager</code> to launch   * the<code>ApplicationMaster</code> for an application.</p>  *   *<p>It includes details such as:  *<ul>  *<li>{@link ApplicationId} of the application.</li>  *<li>Application user.</li>  *<li>Application name.</li>  *<li>{@link Priority} of the application.</li>  *<li>  *       {@link ContainerLaunchContext} of the container in which the   *<code>ApplicationMaster</code> is executed.  *</li>  *</ul>  *</p>  *   * @see ContainerLaunchContext  * @see ClientRMProtocol#submitApplication(org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ApplicationSubmissionContext
specifier|public
interface|interface
name|ApplicationSubmissionContext
block|{
comment|/**    * Get the<code>ApplicationId</code> of the submitted application.    * @return<code>ApplicationId</code> of the submitted application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationId</code> of the submitted application.    * @param appplicationId<code>ApplicationId</code> of the submitted     *                       application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setApplicationId (ApplicationId appplicationId)
specifier|public
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|appplicationId
parameter_list|)
function_decl|;
comment|/**    * Get the application<em>name</em>.    * @return application name    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationName ()
specifier|public
name|String
name|getApplicationName
parameter_list|()
function_decl|;
comment|/**    * Set the application<em>name</em>.    * @param applicationName application name    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setApplicationName (String applicationName)
specifier|public
name|void
name|setApplicationName
parameter_list|(
name|String
name|applicationName
parameter_list|)
function_decl|;
comment|/**    * Get the<em>queue</em> to which the application is being submitted.    * @return<em>queue</em> to which the application is being submitted    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
function_decl|;
comment|/**    * Set the<em>queue</em> to which the application is being submitted    * @param queue<em>queue</em> to which the application is being submitted    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setQueue (String queue)
specifier|public
name|void
name|setQueue
parameter_list|(
name|String
name|queue
parameter_list|)
function_decl|;
comment|/**    * Get the<code>Priority</code> of the application.    * @return<code>Priority</code> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Priority</code> of the application.    * @param priority<code>Priority</code> of the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setPriority (Priority priority)
specifier|public
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
comment|/**    * Get the<em>user</em> submitting the application.    * @return<em>user</em> submitting the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Set the<em>user</em> submitting the application.    * @param user<em>user</em> submitting the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ContainerLaunchContext</code> to describe the     *<code>Container</code> with which the<code>ApplicationMaster</code> is    * launched.    * @return<code>ContainerLaunchContext</code> for the     *<code>ApplicationMaster</code> container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAMContainerSpec ()
specifier|public
name|ContainerLaunchContext
name|getAMContainerSpec
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ContainerLaunchContext</code> to describe the     *<code>Container</code> with which the<code>ApplicationMaster</code> is    * launched.    * @param amContainer<code>ContainerLaunchContext</code> for the     *<code>ApplicationMaster</code> container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setAMContainerSpec (ContainerLaunchContext amContainer)
specifier|public
name|void
name|setAMContainerSpec
parameter_list|(
name|ContainerLaunchContext
name|amContainer
parameter_list|)
function_decl|;
comment|/**    * @return true if tokens should be canceled when the app completes.    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"mapreduce"
argument_list|)
annotation|@
name|Unstable
DECL|method|getCancelTokensWhenComplete ()
specifier|public
name|boolean
name|getCancelTokensWhenComplete
parameter_list|()
function_decl|;
comment|/**    * Set to false if tokens should not be canceled when the app finished else    * false.  WARNING: this is not recommended unless you want your single job    * tokens to be reused by others jobs.    * @param cancel true if tokens should be canceled when the app finishes.     */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"mapreduce"
argument_list|)
annotation|@
name|Unstable
DECL|method|setCancelTokensWhenComplete (boolean cancel)
specifier|public
name|void
name|setCancelTokensWhenComplete
parameter_list|(
name|boolean
name|cancel
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

