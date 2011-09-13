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
name|util
operator|.
name|List
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
name|protocolrecords
operator|.
name|AllocateRequest
import|;
end_import

begin_comment
comment|/**  *<p>The response sent by the<code>ResourceManager</code> the    *<code>ApplicationMaster</code> during resource negotiation.</p>  *  *<p>The response includes:  *<ul>  *<li>Response ID to track duplicate responses.</li>  *<li>  *       A reboot flag to let the<code>ApplicationMaster</code> that its   *       horribly out of sync and needs to reboot.</li>  *<li>A list of newly allocated {@link Container}.</li>  *<li>A list of completed {@link Container}.</li>  *<li>  *       The available headroom for resources in the cluster for the  *       application.   *</li>  *</ul>  *</p>  *   * @see AMRMProtocol#allocate(AllocateRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Unstable
DECL|interface|AMResponse
specifier|public
interface|interface
name|AMResponse
block|{
comment|/**    * Should the<code>ApplicationMaster</code> reboot for being horribly     * out-of-sync with the<code>ResourceManager</code> as deigned by     * {@link #getResponseId()}?    *     * @return<code>true</code> if the<code>ApplicationMaster</code> should    *         reboot,<code>false</code> otherwise    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getReboot ()
specifier|public
name|boolean
name|getReboot
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setReboot (boolean reboot)
specifier|public
name|void
name|setReboot
parameter_list|(
name|boolean
name|reboot
parameter_list|)
function_decl|;
comment|/**    * Get the<em>last response id</em>.    * @return<em>last response id</em>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getResponseId ()
specifier|public
name|int
name|getResponseId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResponseId (int responseId)
specifier|public
name|void
name|setResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
function_decl|;
comment|/**    * Get the list of<em>newly allocated</em><code>Container</code> by the     *<code>ResourceManager</code>.    * @return list of<em>newly allocated</em><code>Container</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAllocatedContainers ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getAllocatedContainers
parameter_list|()
function_decl|;
comment|/**    * Set the list of<em>newly allocated</em><code>Container</code> by the     *<code>ResourceManager</code>.    * @param containers list of<em>newly allocated</em><code>Container</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setAllocatedContainers (List<Container> containers)
specifier|public
name|void
name|setAllocatedContainers
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
function_decl|;
comment|/**    * Get the<em>available headroom</em> for resources in the cluster for the     * application.    * @return limit available headroom for resources in the cluster for the     * application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAvailableResources ()
specifier|public
name|Resource
name|getAvailableResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAvailableResources (Resource limit)
specifier|public
name|void
name|setAvailableResources
parameter_list|(
name|Resource
name|limit
parameter_list|)
function_decl|;
comment|/**    * Get the list of<em>completed containers' statuses</em>.    * @return the list of<em>completed containers' statuses</em>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCompletedContainersStatuses ()
specifier|public
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getCompletedContainersStatuses
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCompletedContainersStatuses (List<ContainerStatus> containers)
specifier|public
name|void
name|setCompletedContainersStatuses
parameter_list|(
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|containers
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

