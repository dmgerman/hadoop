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
name|ContainerManager
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
comment|/**  *<p><code>ContainerLaunchContext</code> represents all of the information  * needed by the<code>NodeManager</code> to launch a container.</p>  *   *<p>It includes details such as:  *<ul>  *<li>{@link ContainerId} of the container.</li>  *<li>{@link Resource} allocated to the container.</li>  *<li>User to whom the container is allocated.</li>  *<li>Security tokens (if security is enabled).</li>  *<li>  *       {@link LocalResource} necessary for running the container such  *       as binaries, jar, shared-objects, side-files etc.   *</li>  *<li>Optional, application-specific binary service data.</li>  *<li>Environment variables for the launched process.</li>  *<li>Command to launch the container.</li>  *</ul>  *</p>  *   * @see ContainerManager#startContainer(org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ContainerLaunchContext
specifier|public
specifier|abstract
class|class
name|ContainerLaunchContext
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|newInstance ( String user, Map<String, LocalResource> localResources, Map<String, String> environment, List<String> commands, Map<String, ByteBuffer> serviceData, ByteBuffer tokens, Map<ApplicationAccessType, String> acls)
specifier|public
specifier|static
name|ContainerLaunchContext
name|newInstance
parameter_list|(
name|String
name|user
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|commands
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceData
parameter_list|,
name|ByteBuffer
name|tokens
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|)
block|{
name|ContainerLaunchContext
name|container
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|container
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|container
operator|.
name|setLocalResources
argument_list|(
name|localResources
argument_list|)
expr_stmt|;
name|container
operator|.
name|setEnvironment
argument_list|(
name|environment
argument_list|)
expr_stmt|;
name|container
operator|.
name|setCommands
argument_list|(
name|commands
argument_list|)
expr_stmt|;
name|container
operator|.
name|setServiceData
argument_list|(
name|serviceData
argument_list|)
expr_stmt|;
name|container
operator|.
name|setTokens
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|container
operator|.
name|setApplicationACLs
argument_list|(
name|acls
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
comment|/**    * Get the<em>user</em> to whom the container has been allocated.    * @return the<em>user</em> to whom the container has been allocated    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUser ()
specifier|public
specifier|abstract
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Set the<em>user</em> to whom the container has been allocated    * @param user<em>user</em> to whom the container has been allocated    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setUser (String user)
specifier|public
specifier|abstract
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * Get all the tokens needed by this container. It may include file-system    * tokens, ApplicationMaster related tokens if this container is an    * ApplicationMaster or framework level tokens needed by this container to    * communicate to various services in a secure manner.    *     * @return tokens needed by this container.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getTokens ()
specifier|public
specifier|abstract
name|ByteBuffer
name|getTokens
parameter_list|()
function_decl|;
comment|/**    * Set security tokens needed by this container.    * @param tokens security tokens     */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setTokens (ByteBuffer tokens)
specifier|public
specifier|abstract
name|void
name|setTokens
parameter_list|(
name|ByteBuffer
name|tokens
parameter_list|)
function_decl|;
comment|/**    * Get<code>LocalResource</code> required by the container.    * @return all<code>LocalResource</code> required by the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getLocalResources ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|getLocalResources
parameter_list|()
function_decl|;
comment|/**    * Set<code>LocalResource</code> required by the container. All pre-existing    * Map entries are cleared before adding the new Map    * @param localResources<code>LocalResource</code> required by the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setLocalResources (Map<String, LocalResource> localResources)
specifier|public
specifier|abstract
name|void
name|setLocalResources
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
parameter_list|)
function_decl|;
comment|/**    * Get application-specific binary<em>service data</em>.    * @return application-specific binary<em>service data</em>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getServiceData ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|getServiceData
parameter_list|()
function_decl|;
comment|/**    * Set application-specific binary<em>service data</em>. All pre-existing Map    * entries are preserved.    * @param serviceData application-specific binary<em>service data</em>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setServiceData (Map<String, ByteBuffer> serviceData)
specifier|public
specifier|abstract
name|void
name|setServiceData
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceData
parameter_list|)
function_decl|;
comment|/**    * Get<em>environment variables</em> for the container.    * @return<em>environment variables</em> for the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getEnvironment ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnvironment
parameter_list|()
function_decl|;
comment|/**    * Add<em>environment variables</em> for the container. All pre-existing Map    * entries are cleared before adding the new Map    * @param environment<em>environment variables</em> for the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setEnvironment (Map<String, String> environment)
specifier|public
specifier|abstract
name|void
name|setEnvironment
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
comment|/**    * Get the list of<em>commands</em> for launching the container.    * @return the list of<em>commands</em> for launching the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCommands ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getCommands
parameter_list|()
function_decl|;
comment|/**    * Add the list of<em>commands</em> for launching the container. All    * pre-existing List entries are cleared before adding the new List    * @param commands the list of<em>commands</em> for launching the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setCommands (List<String> commands)
specifier|public
specifier|abstract
name|void
name|setCommands
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|commands
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ApplicationACL</code>s for the application.     * @return all the<code>ApplicationACL</code>s    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getApplicationACLs ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|getApplicationACLs
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ApplicationACL</code>s for the application. All pre-existing    * Map entries are cleared before adding the new Map    * @param acls<code>ApplicationACL</code>s for the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setApplicationACLs (Map<ApplicationAccessType, String> acls)
specifier|public
specifier|abstract
name|void
name|setApplicationACLs
parameter_list|(
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|)
function_decl|;
block|}
end_class

end_unit

