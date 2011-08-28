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
name|AMResponse
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
name|Container
import|;
end_import

begin_comment
comment|/**  *<p>The response sent by the<code>ResourceManager</code> the    *<code>ApplicationMaster</code> during resource negotiation via  * {@link AMRMProtocol#allocate(AllocateRequest)}.</p>  *  *<p>The response, via {@link AMResponse}, includes:  *<ul>  *<li>Response ID to track duplicate responses.</li>  *<li>  *       A reboot flag to let the<code>ApplicationMaster</code> that its   *       horribly out of sync and needs to reboot.</li>  *<li>A list of newly allocated {@link Container}.</li>  *<li>A list of completed {@link Container}.</li>  *<li>  *       The available headroom for resources in the cluster for the  *       application.   *</li>  *</ul>  *</p>  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|AllocateResponse
specifier|public
interface|interface
name|AllocateResponse
block|{
comment|/**    * Get the {@link AMResponse} sent by the<code>ResourceManager</code>.    * @return<code>AMResponse</code> sent by the<code>ResourceManager</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getAMResponse ()
specifier|public
specifier|abstract
name|AMResponse
name|getAMResponse
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setAMResponse (AMResponse amResponse)
specifier|public
specifier|abstract
name|void
name|setAMResponse
parameter_list|(
name|AMResponse
name|amResponse
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

