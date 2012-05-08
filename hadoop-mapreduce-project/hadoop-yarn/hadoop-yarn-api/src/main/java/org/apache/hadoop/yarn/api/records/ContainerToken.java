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
comment|/**  *<p><code>ContainerToken</code> is the security token used by the framework  * to verify authenticity of any<code>Container</code>.</p>  *  *<p>The<code>ResourceManager</code>, on container allocation provides a  * secure token which is verified by the<code>NodeManager</code> on   * container launch.</p>  *   *<p>Applications do not need to care about<code>ContainerToken</code>, they  * are transparently handled by the framework - the allocated   *<code>Container</code> includes the<code>ContainerToken</code>.</p>  *   * @see AMRMProtocol#allocate(org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest)  * @see ContainerManager#startContainer(org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ContainerToken
specifier|public
interface|interface
name|ContainerToken
extends|extends
name|DelegationToken
block|{
comment|/**    * Get the token identifier.    * @return token identifier    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getIdentifier ()
specifier|public
specifier|abstract
name|ByteBuffer
name|getIdentifier
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Stable
DECL|method|setIdentifier (ByteBuffer identifier)
specifier|public
specifier|abstract
name|void
name|setIdentifier
parameter_list|(
name|ByteBuffer
name|identifier
parameter_list|)
function_decl|;
comment|/**    * Get the token password    * @return token password    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getPassword ()
specifier|public
specifier|abstract
name|ByteBuffer
name|getPassword
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Stable
DECL|method|setPassword (ByteBuffer password)
specifier|public
specifier|abstract
name|void
name|setPassword
parameter_list|(
name|ByteBuffer
name|password
parameter_list|)
function_decl|;
comment|/**    * Get the token kind.    * @return token kind    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getKind ()
specifier|public
specifier|abstract
name|String
name|getKind
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Stable
DECL|method|setKind (String kind)
specifier|public
specifier|abstract
name|void
name|setKind
parameter_list|(
name|String
name|kind
parameter_list|)
function_decl|;
comment|/**    * Get the service to which the token is allocated.    * @return service to which the token is allocated    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getService ()
specifier|public
specifier|abstract
name|String
name|getService
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Stable
DECL|method|setService (String service)
specifier|public
specifier|abstract
name|void
name|setService
parameter_list|(
name|String
name|service
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

