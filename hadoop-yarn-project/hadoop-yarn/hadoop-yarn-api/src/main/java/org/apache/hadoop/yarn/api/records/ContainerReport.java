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
comment|/**  *<p>  *<code>ContainerReport</code> is a report of an container.  *</p>  *   *<p>  * It includes details such as:  *<ul>  *<li>{@link ContainerId} of the container.</li>  *<li>Allocated Resources to the container.</li>  *<li>Assigned Node id.</li>  *<li>Assigned Priority.</li>  *<li>Creation Time.</li>  *<li>Finish Time.</li>  *<li>Container Exit Status.</li>  *<li>{@link ContainerState} of the container.</li>  *<li>Diagnostic information in case of errors.</li>  *<li>Log URL.</li>  *</ul>  *</p>  *   */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ContainerReport
specifier|public
specifier|abstract
class|class
name|ContainerReport
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (ContainerId containerId, Resource allocatedResource, NodeId assignedNode, Priority priority, long creationTime, long finishTime, String diagnosticInfo, String logUrl, int containerExitStatus, ContainerState containerState)
specifier|public
specifier|static
name|ContainerReport
name|newInstance
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|allocatedResource
parameter_list|,
name|NodeId
name|assignedNode
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|diagnosticInfo
parameter_list|,
name|String
name|logUrl
parameter_list|,
name|int
name|containerExitStatus
parameter_list|,
name|ContainerState
name|containerState
parameter_list|)
block|{
name|ContainerReport
name|report
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|report
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAllocatedResource
argument_list|(
name|allocatedResource
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAssignedNode
argument_list|(
name|assignedNode
argument_list|)
expr_stmt|;
name|report
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|report
operator|.
name|setCreationTime
argument_list|(
name|creationTime
argument_list|)
expr_stmt|;
name|report
operator|.
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
name|report
operator|.
name|setDiagnosticsInfo
argument_list|(
name|diagnosticInfo
argument_list|)
expr_stmt|;
name|report
operator|.
name|setLogUrl
argument_list|(
name|logUrl
argument_list|)
expr_stmt|;
name|report
operator|.
name|setContainerExitStatus
argument_list|(
name|containerExitStatus
argument_list|)
expr_stmt|;
name|report
operator|.
name|setContainerState
argument_list|(
name|containerState
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
comment|/**    * Get the<code>ContainerId</code> of the container.    *     * @return<code>ContainerId</code> of the container.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getContainerId ()
specifier|public
specifier|abstract
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
annotation|@
name|Public
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
comment|/**    * Get the allocated<code>Resource</code> of the container.    *     * @return allocated<code>Resource</code> of the container.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAllocatedResource ()
specifier|public
specifier|abstract
name|Resource
name|getAllocatedResource
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setAllocatedResource (Resource resource)
specifier|public
specifier|abstract
name|void
name|setAllocatedResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
function_decl|;
comment|/**    * Get the allocated<code>NodeId</code> where container is running.    *     * @return allocated<code>NodeId</code> where container is running.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAssignedNode ()
specifier|public
specifier|abstract
name|NodeId
name|getAssignedNode
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setAssignedNode (NodeId nodeId)
specifier|public
specifier|abstract
name|void
name|setAssignedNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
comment|/**    * Get the allocated<code>Priority</code> of the container.    *     * @return allocated<code>Priority</code> of the container.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|Priority
name|getPriority
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
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
comment|/**    * Get the creation time of the container.    *     * @return creation time of the container    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getCreationTime ()
specifier|public
specifier|abstract
name|long
name|getCreationTime
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setCreationTime (long creationTime)
specifier|public
specifier|abstract
name|void
name|setCreationTime
parameter_list|(
name|long
name|creationTime
parameter_list|)
function_decl|;
comment|/**    * Get the Finish time of the container.    *     * @return Finish time of the container    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getFinishTime ()
specifier|public
specifier|abstract
name|long
name|getFinishTime
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setFinishTime (long finishTime)
specifier|public
specifier|abstract
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
function_decl|;
comment|/**    * Get the DiagnosticsInfo of the container.    *     * @return DiagnosticsInfo of the container    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getDiagnosticsInfo ()
specifier|public
specifier|abstract
name|String
name|getDiagnosticsInfo
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setDiagnosticsInfo (String diagnosticsInfo)
specifier|public
specifier|abstract
name|void
name|setDiagnosticsInfo
parameter_list|(
name|String
name|diagnosticsInfo
parameter_list|)
function_decl|;
comment|/**    * Get the LogURL of the container.    *     * @return LogURL of the container    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getLogUrl ()
specifier|public
specifier|abstract
name|String
name|getLogUrl
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setLogUrl (String logUrl)
specifier|public
specifier|abstract
name|void
name|setLogUrl
parameter_list|(
name|String
name|logUrl
parameter_list|)
function_decl|;
comment|/**    * Get the final<code>ContainerState</code> of the container.    *     * @return final<code>ContainerState</code> of the container.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getContainerState ()
specifier|public
specifier|abstract
name|ContainerState
name|getContainerState
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setContainerState (ContainerState containerState)
specifier|public
specifier|abstract
name|void
name|setContainerState
parameter_list|(
name|ContainerState
name|containerState
parameter_list|)
function_decl|;
comment|/**    * Get the final<code>exit status</code> of the container.    *     * @return final<code>exit status</code> of the container.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getContainerExitStatus ()
specifier|public
specifier|abstract
name|int
name|getContainerExitStatus
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setContainerExitStatus (int containerExitStatus)
specifier|public
specifier|abstract
name|void
name|setContainerExitStatus
parameter_list|(
name|int
name|containerExitStatus
parameter_list|)
function_decl|;
block|}
end_class

end_unit

