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
name|Resource
import|;
end_import

begin_comment
comment|/**  *<p>The response sent by the<code>ResourceManager</code> to a new   *<code>ApplicationMaster</code> on registration via the   * {@link AMRMProtocol#registerApplicationMaster(RegisterApplicationMasterRequest)}   * api.</p>  *   *<p>The response contains critical details such as:  *<ul>  *<li>Minimum capability for allocated resources in the cluster.</li>  *<li>Maximum capability for allocated resources in the cluster.</li>  *</ul>  *</p>  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|RegisterApplicationMasterResponse
specifier|public
interface|interface
name|RegisterApplicationMasterResponse
block|{
comment|/**    * Get the minimum capability for any {@link Resource} allocated by the     *<code>ResourceManager</code> in the cluster.    * @return minimum capability of allocated resources in the cluster    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMinimumResourceCapability ()
specifier|public
name|Resource
name|getMinimumResourceCapability
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setMinimumResourceCapability (Resource capability)
specifier|public
name|void
name|setMinimumResourceCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
comment|/**    * Get the maximum capability for any {@link Resource} allocated by the     *<code>ResourceManager</code> in the cluster.    * @return maximum capability of allocated resources in the cluster    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMaximumResourceCapability ()
specifier|public
name|Resource
name|getMaximumResourceCapability
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setMaximumResourceCapability (Resource capability)
specifier|public
name|void
name|setMaximumResourceCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

