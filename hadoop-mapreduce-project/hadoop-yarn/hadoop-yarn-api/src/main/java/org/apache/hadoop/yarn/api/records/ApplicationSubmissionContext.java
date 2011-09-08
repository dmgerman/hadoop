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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
comment|/**  *<p><code>ApplicationSubmissionContext</code> represents the all of the   * information needed by the<code>ResourceManager</code> to launch   * the<code>ApplicationMaster</code> for an application.</p>  *   *<p>It includes details such as:  *<ul>  *<li>{@link ApplicationId} of the application.</li>  *<li>  *       {@link Resource} necessary to run the<code>ApplicationMaster</code>.  *</li>  *<li>Application user.</li>  *<li>Application name.</li>  *<li>{@link Priority} of the application.</li>  *<li>Security tokens (if security is enabled).</li>  *<li>  *       {@link LocalResource} necessary for running the   *<code>ApplicationMaster</code> container such  *       as binaries, jar, shared-objects, side-files etc.   *</li>  *<li>  *       Environment variables for the launched<code>ApplicationMaster</code>   *       process.  *</li>  *<li>Command to launch the<code>ApplicationMaster</code>.</li>  *</ul>  *</p>  *   * @see ClientRMProtocol#submitApplication(org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest)  */
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
comment|/**    * Get the<code>Resource</code> required to run the     *<code>ApplicationMaster</code>.    * @return<code>Resource</code> required to run the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMasterCapability ()
specifier|public
name|Resource
name|getMasterCapability
parameter_list|()
function_decl|;
comment|/**    * Set<code>Resource</code> required to run the     *<code>ApplicationMaster</code>.    * @param masterCapability<code>Resource</code> required to run the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setMasterCapability (Resource masterCapability)
specifier|public
name|void
name|setMasterCapability
parameter_list|(
name|Resource
name|masterCapability
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getAllResources ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|URL
argument_list|>
name|getAllResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getResource (String key)
specifier|public
name|URL
name|getResource
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|addAllResources (Map<String, URL> resources)
specifier|public
name|void
name|addAllResources
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|URL
argument_list|>
name|resources
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResource (String key, URL url)
specifier|public
name|void
name|setResource
parameter_list|(
name|String
name|key
parameter_list|,
name|URL
name|url
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeResource (String key)
specifier|public
name|void
name|removeResource
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearResources ()
specifier|public
name|void
name|clearResources
parameter_list|()
function_decl|;
comment|/**    * Get all the<code>LocalResource</code> required to run the     *<code>ApplicationMaster</code>.    * @return<code>LocalResource</code> required to run the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAllResourcesTodo ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|getAllResourcesTodo
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getResourceTodo (String key)
specifier|public
name|LocalResource
name|getResourceTodo
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Add all the<code>LocalResource</code> required to run the     *<code>ApplicationMaster</code>.    * @param resources all<code>LocalResource</code> required to run the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllResourcesTodo (Map<String, LocalResource> resources)
specifier|public
name|void
name|addAllResourcesTodo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|resources
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResourceTodo (String key, LocalResource localResource)
specifier|public
name|void
name|setResourceTodo
parameter_list|(
name|String
name|key
parameter_list|,
name|LocalResource
name|localResource
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeResourceTodo (String key)
specifier|public
name|void
name|removeResourceTodo
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearResourcesTodo ()
specifier|public
name|void
name|clearResourcesTodo
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getFsTokenList ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getFsTokenList
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getFsToken (int index)
specifier|public
name|String
name|getFsToken
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getFsTokenCount ()
specifier|public
name|int
name|getFsTokenCount
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|addAllFsTokens (List<String> fsTokens)
specifier|public
name|void
name|addAllFsTokens
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fsTokens
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|addFsToken (String fsToken)
specifier|public
name|void
name|addFsToken
parameter_list|(
name|String
name|fsToken
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeFsToken (int index)
specifier|public
name|void
name|removeFsToken
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearFsTokens ()
specifier|public
name|void
name|clearFsTokens
parameter_list|()
function_decl|;
comment|/**    * Get<em>file-system tokens</em> for the<code>ApplicationMaster</code>.    * @return file-system tokens for the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getFsTokensTodo ()
specifier|public
name|ByteBuffer
name|getFsTokensTodo
parameter_list|()
function_decl|;
comment|/**    * Set<em>file-system tokens</em> for the<code>ApplicationMaster</code>.    * @param fsTokens file-system tokens for the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setFsTokensTodo (ByteBuffer fsTokens)
specifier|public
name|void
name|setFsTokensTodo
parameter_list|(
name|ByteBuffer
name|fsTokens
parameter_list|)
function_decl|;
comment|/**    * Get the<em>environment variables</em> for the     *<code>ApplicationMaster</code>.    * @return environment variables for the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAllEnvironment ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAllEnvironment
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getEnvironment (String key)
specifier|public
name|String
name|getEnvironment
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Add all of the<em>environment variables</em> for the     *<code>ApplicationMaster</code>.    * @param environment environment variables for the     *<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllEnvironment (Map<String, String> environment)
specifier|public
name|void
name|addAllEnvironment
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setEnvironment (String key, String env)
specifier|public
name|void
name|setEnvironment
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|env
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeEnvironment (String key)
specifier|public
name|void
name|removeEnvironment
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearEnvironment ()
specifier|public
name|void
name|clearEnvironment
parameter_list|()
function_decl|;
comment|/**    * Get the<em>commands</em> to launch the<code>ApplicationMaster</code>.    * @return commands to launch the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCommandList ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getCommandList
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getCommand (int index)
specifier|public
name|String
name|getCommand
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getCommandCount ()
specifier|public
name|int
name|getCommandCount
parameter_list|()
function_decl|;
comment|/**    * Add all of the<em>commands</em> to launch the     *<code>ApplicationMaster</code>.    * @param commands commands to launch the<code>ApplicationMaster</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllCommands (List<String> commands)
specifier|public
name|void
name|addAllCommands
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|commands
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|addCommand (String command)
specifier|public
name|void
name|addCommand
parameter_list|(
name|String
name|command
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeCommand (int index)
specifier|public
name|void
name|removeCommand
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearCommands ()
specifier|public
name|void
name|clearCommands
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

