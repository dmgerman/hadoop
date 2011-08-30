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
name|ContainerManager
import|;
end_import

begin_comment
comment|/**  *<p><code>ContainerLaunchContext</code> represents the all of the information  * needed by the<code>NodeManager</code> to launch a container.</p>  *   *<p>It includes details such as:  *<ul>  *<li>{@link ContainerId} of the container.</li>  *<li>{@link Resource} allocated to the container.</li>  *<li>User to whom the container is allocated.</li>  *<li>Security tokens (if security is enabled).</li>  *<li>  *       {@link LocalResource} necessary for running the container such  *       as binaries, jar, shared-objects, side-files etc.   *</li>  *<li>Optional, application-specific binary service data.</li>  *<li>Environment variables for the launched process.</li>  *<li>Command to launch the container.</li>  *</ul>  *</p>  *   * @see ContainerManager#startContainer(org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ContainerLaunchContext
specifier|public
interface|interface
name|ContainerLaunchContext
block|{
comment|/**    * Get<code>ContainerId</code> of container to be launched.    * @return<code>ContainerId</code> of container to be launched    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getContainerId ()
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
comment|/**    * Set<code>ContainerId</code> of container to be launched.    * @param containerId et<code>ContainerId</code> of container to be launched    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setContainerId (ContainerId containerId)
name|void
name|setContainerId
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Get the<em>user</em> to whom the container has been allocated.    * @return the<em>user</em> to whom the container has been allocated    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUser ()
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
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * Get the<code>Resource</code> allocated to the container by the    *<code>ResourceManager</code>.    * @return<code>Resource</code> allocated to the container by the    *<code>ResourceManager</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getResource ()
name|Resource
name|getResource
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Resource</code> allocated to the container by the    *<code>ResourceManager</code>.    * @param resource allocated resource    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setResource (Resource resource)
name|void
name|setResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
function_decl|;
comment|/**    * Get security tokens (if security is enabled).    * @return security tokens (if security is enabled)    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getContainerTokens ()
name|ByteBuffer
name|getContainerTokens
parameter_list|()
function_decl|;
comment|/**    * Set security tokens (if security is enabled).    * @param containerToken security tokens     */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setContainerTokens (ByteBuffer containerToken)
name|void
name|setContainerTokens
parameter_list|(
name|ByteBuffer
name|containerToken
parameter_list|)
function_decl|;
comment|/**    * Get all<code>LocalResource</code> required by the container.    * @return all<code>LocalResource</code> required by the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAllLocalResources ()
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|getAllLocalResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getLocalResource (String key)
name|LocalResource
name|getLocalResource
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Add all<code>LocalResource</code> required by the container.    * @param localResources<code>LocalResource</code> required by the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllLocalResources (Map<String, LocalResource> localResources)
name|void
name|addAllLocalResources
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setLocalResource (String key, LocalResource value)
name|void
name|setLocalResource
parameter_list|(
name|String
name|key
parameter_list|,
name|LocalResource
name|value
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeLocalResource (String key)
name|void
name|removeLocalResource
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearLocalResources ()
name|void
name|clearLocalResources
parameter_list|()
function_decl|;
comment|/**    * Get application-specific binary service data.    * @return application-specific binary service data    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAllServiceData ()
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|getAllServiceData
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getServiceData (String key)
name|ByteBuffer
name|getServiceData
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Add add application-specific binary service data.    * @param serviceData application-specific binary service data    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllServiceData (Map<String, ByteBuffer> serviceData)
name|void
name|addAllServiceData
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
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setServiceData (String key, ByteBuffer value)
name|void
name|setServiceData
parameter_list|(
name|String
name|key
parameter_list|,
name|ByteBuffer
name|value
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeServiceData (String key)
name|void
name|removeServiceData
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearServiceData ()
name|void
name|clearServiceData
parameter_list|()
function_decl|;
comment|/**    * Get<em>environment variables</em> for the launched container.    * @return<em>environment variables</em> for the launched container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAllEnv ()
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAllEnv
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getEnv (String key)
name|String
name|getEnv
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Add<em>environment variables</em> for the launched container.    * @param env<em>environment variables</em> for the launched container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllEnv (Map<String, String> env)
name|void
name|addAllEnv
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setEnv (String key, String value)
name|void
name|setEnv
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|removeEnv (String key)
name|void
name|removeEnv
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|clearEnv ()
name|void
name|clearEnv
parameter_list|()
function_decl|;
comment|/**    * Get the list of<em>commands</em> for launching the container.    * @return the list of<em>commands</em> for launching the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCommandList ()
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
name|int
name|getCommandCount
parameter_list|()
function_decl|;
comment|/**    * Add the list of<em>commands</em> for launching the container.    * @param commands the list of<em>commands</em> for launching the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|addAllCommands (List<String> commands)
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
name|void
name|clearCommands
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

