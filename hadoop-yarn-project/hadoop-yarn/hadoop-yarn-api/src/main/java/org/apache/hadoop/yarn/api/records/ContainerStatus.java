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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * {@code ContainerStatus} represents the current status of a  * {@code Container}.  *<p>  * It provides details such as:  *<ul>  *<li>{@code ContainerId} of the container.</li>  *<li>{@code ContainerState} of the container.</li>  *<li><em>Exit status</em> of a completed container.</li>  *<li><em>Diagnostic</em> message for a failed container.</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|ContainerStatus
specifier|public
specifier|abstract
class|class
name|ContainerStatus
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (ContainerId containerId, ContainerState containerState, String diagnostics, int exitStatus)
specifier|public
specifier|static
name|ContainerStatus
name|newInstance
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|ContainerState
name|containerState
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|int
name|exitStatus
parameter_list|)
block|{
name|ContainerStatus
name|containerStatus
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerStatus
operator|.
name|setState
argument_list|(
name|containerState
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setExitStatus
argument_list|(
name|exitStatus
argument_list|)
expr_stmt|;
return|return
name|containerStatus
return|;
block|}
comment|/**    * Get the<code>ContainerId</code> of the container.    * @return<code>ContainerId</code> of the container    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getContainerId ()
specifier|public
specifier|abstract
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setContainerId (ContainerId containerId)
specifier|public
specifier|abstract
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
specifier|public
specifier|abstract
name|ContainerState
name|getState
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setState (ContainerState state)
specifier|public
specifier|abstract
name|void
name|setState
parameter_list|(
name|ContainerState
name|state
parameter_list|)
function_decl|;
comment|/**    *<p>Get the<em>exit status</em> for the container.</p>    *      *<p>Note: This is valid only for completed containers i.e. containers    * with state {@link ContainerState#COMPLETE}.     * Otherwise, it returns an ContainerExitStatus.INVALID.    *</p>    *     *<p>Containers killed by the framework, either due to being released by    * the application or being 'lost' due to node failures etc. have a special    * exit code of ContainerExitStatus.ABORTED.</p>    *     *<p>When threshold number of the nodemanager-local-directories or    * threshold number of the nodemanager-log-directories become bad, then    * container is not launched and is exited with ContainersExitStatus.DISKS_FAILED.    *</p>    *      * @return<em>exit status</em> for the container    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getExitStatus ()
specifier|public
specifier|abstract
name|int
name|getExitStatus
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setExitStatus (int exitStatus)
specifier|public
specifier|abstract
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
specifier|public
specifier|abstract
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setDiagnostics (String diagnostics)
specifier|public
specifier|abstract
name|void
name|setDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
function_decl|;
block|}
end_class

end_unit

