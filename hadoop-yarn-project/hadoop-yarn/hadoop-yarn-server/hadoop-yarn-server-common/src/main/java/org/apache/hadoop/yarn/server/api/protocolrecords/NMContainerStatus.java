begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|ContainerState
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
name|ContainerStatus
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
name|ExecutionType
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
name|Priority
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * NMContainerStatus includes the current information of a container. This  * record is used by YARN only, whereas {@link ContainerStatus} is used both  * inside YARN and by end-users.  */
end_comment

begin_class
DECL|class|NMContainerStatus
specifier|public
specifier|abstract
class|class
name|NMContainerStatus
block|{
comment|// Used by tests only
DECL|method|newInstance (ContainerId containerId, int version, ContainerState containerState, Resource allocatedResource, String diagnostics, int containerExitStatus, Priority priority, long creationTime)
specifier|public
specifier|static
name|NMContainerStatus
name|newInstance
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|int
name|version
parameter_list|,
name|ContainerState
name|containerState
parameter_list|,
name|Resource
name|allocatedResource
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|int
name|containerExitStatus
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|creationTime
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|containerId
argument_list|,
name|version
argument_list|,
name|containerState
argument_list|,
name|allocatedResource
argument_list|,
name|diagnostics
argument_list|,
name|containerExitStatus
argument_list|,
name|priority
argument_list|,
name|creationTime
argument_list|,
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|,
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|newInstance (ContainerId containerId, int version, ContainerState containerState, Resource allocatedResource, String diagnostics, int containerExitStatus, Priority priority, long creationTime, String nodeLabelExpression, ExecutionType executionType, long allocationRequestId)
specifier|public
specifier|static
name|NMContainerStatus
name|newInstance
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|int
name|version
parameter_list|,
name|ContainerState
name|containerState
parameter_list|,
name|Resource
name|allocatedResource
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|int
name|containerExitStatus
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|String
name|nodeLabelExpression
parameter_list|,
name|ExecutionType
name|executionType
parameter_list|,
name|long
name|allocationRequestId
parameter_list|)
block|{
name|NMContainerStatus
name|status
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NMContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|status
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|status
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|status
operator|.
name|setContainerState
argument_list|(
name|containerState
argument_list|)
expr_stmt|;
name|status
operator|.
name|setAllocatedResource
argument_list|(
name|allocatedResource
argument_list|)
expr_stmt|;
name|status
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
name|status
operator|.
name|setContainerExitStatus
argument_list|(
name|containerExitStatus
argument_list|)
expr_stmt|;
name|status
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|status
operator|.
name|setCreationTime
argument_list|(
name|creationTime
argument_list|)
expr_stmt|;
name|status
operator|.
name|setNodeLabelExpression
argument_list|(
name|nodeLabelExpression
argument_list|)
expr_stmt|;
name|status
operator|.
name|setExecutionType
argument_list|(
name|executionType
argument_list|)
expr_stmt|;
name|status
operator|.
name|setAllocationRequestId
argument_list|(
name|allocationRequestId
argument_list|)
expr_stmt|;
return|return
name|status
return|;
block|}
comment|/**    * Get the<code>ContainerId</code> of the container.    *     * @return<code>ContainerId</code> of the container.    */
DECL|method|getContainerId ()
specifier|public
specifier|abstract
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
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
DECL|method|getAllocatedResource ()
specifier|public
specifier|abstract
name|Resource
name|getAllocatedResource
parameter_list|()
function_decl|;
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
comment|/**    * Get the DiagnosticsInfo of the container.    *     * @return DiagnosticsInfo of the container    */
DECL|method|getDiagnostics ()
specifier|public
specifier|abstract
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
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
DECL|method|getContainerState ()
specifier|public
specifier|abstract
name|ContainerState
name|getContainerState
parameter_list|()
function_decl|;
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
DECL|method|getContainerExitStatus ()
specifier|public
specifier|abstract
name|int
name|getContainerExitStatus
parameter_list|()
function_decl|;
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
comment|/**    * Get the<code>Priority</code> of the request.    * @return<code>Priority</code> of the request    */
DECL|method|getPriority ()
specifier|public
specifier|abstract
name|Priority
name|getPriority
parameter_list|()
function_decl|;
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
comment|/**    * Get the time when the container is created    */
DECL|method|getCreationTime ()
specifier|public
specifier|abstract
name|long
name|getCreationTime
parameter_list|()
function_decl|;
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
comment|/**    * Get the node-label-expression in the original ResourceRequest    */
DECL|method|getNodeLabelExpression ()
specifier|public
specifier|abstract
name|String
name|getNodeLabelExpression
parameter_list|()
function_decl|;
DECL|method|setNodeLabelExpression ( String nodeLabelExpression)
specifier|public
specifier|abstract
name|void
name|setNodeLabelExpression
parameter_list|(
name|String
name|nodeLabelExpression
parameter_list|)
function_decl|;
comment|/**    * @return the<em>ID</em> corresponding to the original allocation request.    */
DECL|method|getAllocationRequestId ()
specifier|public
specifier|abstract
name|long
name|getAllocationRequestId
parameter_list|()
function_decl|;
comment|/**    * Set the<em>ID</em> corresponding to the original allocation request.    *    * @param allocationRequestId the<em>ID</em> corresponding to the original    *                            allocation request.    */
DECL|method|setAllocationRequestId (long allocationRequestId)
specifier|public
specifier|abstract
name|void
name|setAllocationRequestId
parameter_list|(
name|long
name|allocationRequestId
parameter_list|)
function_decl|;
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|setVersion (int version)
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{    }
comment|/**    * Get the<code>ExecutionType</code> of the container.    * @return<code>ExecutionType</code> of the container    */
DECL|method|getExecutionType ()
specifier|public
name|ExecutionType
name|getExecutionType
parameter_list|()
block|{
return|return
name|ExecutionType
operator|.
name|GUARANTEED
return|;
block|}
DECL|method|setExecutionType (ExecutionType executionType)
specifier|public
name|void
name|setExecutionType
parameter_list|(
name|ExecutionType
name|executionType
parameter_list|)
block|{ }
comment|/**    * Get and set the Allocation tags associated with the container.    */
DECL|method|getAllocationTags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAllocationTags
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_SET
return|;
block|}
DECL|method|setAllocationTags (Set<String> allocationTags)
specifier|public
name|void
name|setAllocationTags
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|)
block|{    }
block|}
end_class

end_unit

