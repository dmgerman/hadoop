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

begin_comment
comment|/**  *<p><code>ContainerStatus</code> represents the current status of a   *<code>Container</code>.</p>  *   *<p>It provides details such as:  *<ul>  *<li><code>ContainerId</code> of the container.</li>  *<li><code>ContainerState</code> of the container.</li>  *<li><em>Exit status</em> of a completed container.</li>  *<li><em>Diagnostic</em> message for a failed container.</li>  *</ul>  *</p>  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ContainerStatus
specifier|public
interface|interface
name|ContainerStatus
block|{
comment|/**    * Get the<code>ContainerId</code> of the container.    * @return<code>ContainerId</code> of the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getContainerId ()
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setContainerId (ContainerId containerId)
name|void
name|setContainerId
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Get the<code>ContainerState</code> of the container.    * @return<code>ContainerState</code> of the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getState ()
name|ContainerState
name|getState
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setState (ContainerState state)
name|void
name|setState
parameter_list|(
name|ContainerState
name|state
parameter_list|)
function_decl|;
comment|/**    *<p>Get the<em>exit status</em> for the container.</p>    *      *<p>Note: This is valid only for completed containers i.e. containers    * with state {@link ContainerState#COMPLETE}.     * Otherwise, it returns an invalid exit code equal to {@literal -1000};</p>    *     *<p>Container killed by the framework, either due to being released by    * the application or being 'lost' due to node failures etc. have a special    * exit code of {@literal -100}.</p>    *      * @return<em>exit status</em> for the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getExitStatus ()
name|int
name|getExitStatus
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setExitStatus (int exitStatus)
name|void
name|setExitStatus
parameter_list|(
name|int
name|exitStatus
parameter_list|)
function_decl|;
comment|/**    * Get<em>diagnostic messages</em> for failed containers.    * @return<em>diagnostic messages</em> for failed containers    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getDiagnostics ()
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setDiagnostics (String diagnostics)
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

