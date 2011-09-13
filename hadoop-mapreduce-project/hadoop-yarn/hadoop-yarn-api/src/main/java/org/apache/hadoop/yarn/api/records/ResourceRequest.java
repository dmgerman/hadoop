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
name|AMRMProtocol
import|;
end_import

begin_comment
comment|/**  *<p><code>ResourceRequest</code> represents the request made by an  * application to the<code>ResourceManager</code> to obtain various   *<code>Container</code> allocations.</p>  *   *<p>It includes:  *<ul>  *<li>{@link Priority} of the request.</li>  *<li>  *       The<em>name</em> of the machine or rack on which the allocation is   *       desired. A special value of<em>*</em> signifies that   *<em>any</em> host/rack is acceptable to the application.  *</li>  *<li>{@link Resource} required for each request.</li>  *<li>  *       Number of containers of such specifications which are required   *       by the application.  *</li>  *</ul>  *</p>  *   * @see Resource  * @see AMRMProtocol#allocate(org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ResourceRequest
specifier|public
interface|interface
name|ResourceRequest
extends|extends
name|Comparable
argument_list|<
name|ResourceRequest
argument_list|>
block|{
comment|/**    * Get the<code>Priority</code> of the request.    * @return<code>Priority</code> of the request    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|Priority
name|getPriority
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Priority</code> of the request    * @param priority<code>Priority</code> of the request    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setPriority (Priority priority)
specifier|public
specifier|abstract
name|void
name|setPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
comment|/**    * Get the<em>host/rack</em> on which the allocation is desired.    *     * A special value of<em>*</em> signifies that<em>any</em> host/rack is     * acceptable.    *     * @return<em>host/rack</em> on which the allocation is desired    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHostName ()
specifier|public
specifier|abstract
name|String
name|getHostName
parameter_list|()
function_decl|;
comment|/**    * Set<em>host/rack</em> on which the allocation is desired.    *     * A special value of<em>*</em> signifies that<em>any</em> host/rack is     * acceptable.    *     * @param hostName<em>host/rack</em> on which the allocation is desired    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setHostName (String hostName)
specifier|public
specifier|abstract
name|void
name|setHostName
parameter_list|(
name|String
name|hostName
parameter_list|)
function_decl|;
comment|/**    * Get the<code>Resource</code> capability of the request.    * @return<code>Resource</code> capability of the request    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCapability ()
specifier|public
specifier|abstract
name|Resource
name|getCapability
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Resource</code> capability of the request    * @param capability<code>Resource</code> capability of the request    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setCapability (Resource capability)
specifier|public
specifier|abstract
name|void
name|setCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
comment|/**    * Get the number of containers required with the given specifications.    * @return number of containers required with the given specifications    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumContainers ()
specifier|public
specifier|abstract
name|int
name|getNumContainers
parameter_list|()
function_decl|;
comment|/**    * Set the number of containers required with the given specifications    * @param numContainers number of containers required with the given     *                      specifications    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setNumContainers (int numContainers)
specifier|public
specifier|abstract
name|void
name|setNumContainers
parameter_list|(
name|int
name|numContainers
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

