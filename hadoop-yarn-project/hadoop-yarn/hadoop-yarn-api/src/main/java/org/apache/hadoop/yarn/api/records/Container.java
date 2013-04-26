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
name|ContainerManager
import|;
end_import

begin_comment
comment|/**  *<p><code>Container</code> represents an allocated resource in the cluster.  *</p>  *   *<p>The<code>ResourceManager</code> is the sole authority to allocate any  *<code>Container</code> to applications. The allocated<code>Container</code>  * is always on a single node and has a unique {@link ContainerId}. It has  * a specific amount of {@link Resource} allocated.</p>  *   *<p>It includes details such as:  *<ul>  *<li>{@link ContainerId} for the container, which is globally unique.</li>  *<li>  *       {@link NodeId} of the node on which it is allocated.  *</li>  *<li>HTTP uri of the node.</li>  *<li>{@link Resource} allocated to the container.</li>  *<li>{@link Priority} at which the container was allocated.</li>  *<li>{@link ContainerState} of the container.</li>  *<li>  *       {@link ContainerToken} of the container, used to securely verify   *       authenticity of the allocation.   *</li>  *<li>{@link ContainerStatus} of the container.</li>  *</ul>  *</p>  *   *<p>Typically, an<code>ApplicationMaster</code> receives the   *<code>Container</code> from the<code>ResourceManager</code> during  * resource-negotiation and then talks to the<code>NodManager</code> to   * start/stop containers.</p>  *   * @see AMRMProtocol#allocate(org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest)  * @see ContainerManager#startContainer(org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest)  * @see ContainerManager#stopContainer(org.apache.hadoop.yarn.api.protocolrecords.StopContainerRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|Container
specifier|public
interface|interface
name|Container
extends|extends
name|Comparable
argument_list|<
name|Container
argument_list|>
block|{
comment|/**    * Get the globally unique identifier for the container.    * @return globally unique identifier for the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getId ()
name|ContainerId
name|getId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setId (ContainerId id)
name|void
name|setId
parameter_list|(
name|ContainerId
name|id
parameter_list|)
function_decl|;
comment|/**    * Get the identifier of the node on which the container is allocated.    * @return identifier of the node on which the container is allocated    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNodeId ()
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNodeId (NodeId nodeId)
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
comment|/**    * Get the http uri of the node on which the container is allocated.    * @return http uri of the node on which the container is allocated    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNodeHttpAddress ()
name|String
name|getNodeHttpAddress
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNodeHttpAddress (String nodeHttpAddress)
name|void
name|setNodeHttpAddress
parameter_list|(
name|String
name|nodeHttpAddress
parameter_list|)
function_decl|;
comment|/**    * Get the<code>Resource</code> allocated to the container.    * @return<code>Resource</code> allocated to the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getResource ()
name|Resource
name|getResource
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResource (Resource resource)
name|void
name|setResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
function_decl|;
comment|/**    * Get the<code>Priority</code> at which the<code>Container</code> was    * allocated.    * @return<code>Priority</code> at which the<code>Container</code> was    *         allocated    */
DECL|method|getPriority ()
name|Priority
name|getPriority
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setPriority (Priority priority)
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ContainerToken</code> for the container.    * @return<code>ContainerToken</code> for the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getContainerToken ()
name|ContainerToken
name|getContainerToken
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setContainerToken (ContainerToken containerToken)
name|void
name|setContainerToken
parameter_list|(
name|ContainerToken
name|containerToken
parameter_list|)
function_decl|;
comment|/**    * Get the RMIdentifier of RM in which containers are allocated    * @return RMIdentifier    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getRMIdentifer ()
name|long
name|getRMIdentifer
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRMIdentifier (long rmIdentifier)
name|void
name|setRMIdentifier
parameter_list|(
name|long
name|rmIdentifier
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

