begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmcontainer
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
name|resourcemanager
operator|.
name|rmcontainer
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
name|ApplicationAttemptId
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
name|ContainerReport
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
name|NodeId
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
name|event
operator|.
name|EventHandler
import|;
end_import

begin_comment
comment|/**  * Represents the ResourceManager's view of an application container. See   * {@link RMContainerImpl} for an implementation. Containers may be in one  * of several states, given in {@link RMContainerState}. An RMContainer  * instance may exist even if there is no actual running container, such as   * when resources are being reserved to fill space for a future container   * allocation.  */
end_comment

begin_interface
DECL|interface|RMContainer
specifier|public
interface|interface
name|RMContainer
extends|extends
name|EventHandler
argument_list|<
name|RMContainerEvent
argument_list|>
block|{
DECL|method|getContainerId ()
name|ContainerId
name|getContainerId
parameter_list|()
function_decl|;
DECL|method|getApplicationAttemptId ()
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
DECL|method|getState ()
name|RMContainerState
name|getState
parameter_list|()
function_decl|;
DECL|method|getContainer ()
name|Container
name|getContainer
parameter_list|()
function_decl|;
DECL|method|getReservedResource ()
name|Resource
name|getReservedResource
parameter_list|()
function_decl|;
DECL|method|getReservedNode ()
name|NodeId
name|getReservedNode
parameter_list|()
function_decl|;
DECL|method|getReservedPriority ()
name|Priority
name|getReservedPriority
parameter_list|()
function_decl|;
DECL|method|getAllocatedResource ()
name|Resource
name|getAllocatedResource
parameter_list|()
function_decl|;
DECL|method|getAllocatedNode ()
name|NodeId
name|getAllocatedNode
parameter_list|()
function_decl|;
DECL|method|getAllocatedPriority ()
name|Priority
name|getAllocatedPriority
parameter_list|()
function_decl|;
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
DECL|method|getFinishTime ()
name|long
name|getFinishTime
parameter_list|()
function_decl|;
DECL|method|getDiagnosticsInfo ()
name|String
name|getDiagnosticsInfo
parameter_list|()
function_decl|;
DECL|method|getLogURL ()
name|String
name|getLogURL
parameter_list|()
function_decl|;
DECL|method|getContainerExitStatus ()
name|int
name|getContainerExitStatus
parameter_list|()
function_decl|;
DECL|method|getContainerState ()
name|ContainerState
name|getContainerState
parameter_list|()
function_decl|;
DECL|method|createContainerReport ()
name|ContainerReport
name|createContainerReport
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

