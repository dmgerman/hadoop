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
name|api
operator|.
name|records
operator|.
name|ContainerLaunchContext
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
name|Token
import|;
end_import

begin_comment
comment|/**  *<p>The request sent by the<code>ApplicationMaster</code> to the  *<code>NodeManager</code> to<em>start</em> a container.</p>  *   *<p>The<code>ApplicationMaster</code> has to provide details such as  * allocated resource capability, security tokens (if enabled), command  * to be executed to start the container, environment for the process,   * necessary binaries/jar/shared-objects etc. via the   * {@link ContainerLaunchContext}.</p>  *  * @see ContainerManager#startContainer(StartContainerRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|StartContainerRequest
specifier|public
interface|interface
name|StartContainerRequest
block|{
comment|/**    * Get the<code>ContainerLaunchContext</code> for the container to be started    * by the<code>NodeManager</code>.    *     * @return<code>ContainerLaunchContext</code> for the container to be started    *         by the<code>NodeManager</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getContainerLaunchContext ()
specifier|public
specifier|abstract
name|ContainerLaunchContext
name|getContainerLaunchContext
parameter_list|()
function_decl|;
comment|/**    * Set the<code>ContainerLaunchContext</code> for the container to be started    * by the<code>NodeManager</code>    * @param context<code>ContainerLaunchContext</code> for the container to be     *                started by the<code>NodeManager</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setContainerLaunchContext (ContainerLaunchContext context)
specifier|public
specifier|abstract
name|void
name|setContainerLaunchContext
parameter_list|(
name|ContainerLaunchContext
name|context
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getContainerToken ()
specifier|public
name|Token
name|getContainerToken
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setContainerToken (Token container)
specifier|public
name|void
name|setContainerToken
parameter_list|(
name|Token
name|container
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

