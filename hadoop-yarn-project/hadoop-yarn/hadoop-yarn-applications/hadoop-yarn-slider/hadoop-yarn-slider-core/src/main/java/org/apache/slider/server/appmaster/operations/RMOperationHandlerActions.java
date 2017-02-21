begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.operations
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
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
name|client
operator|.
name|api
operator|.
name|AMRMClient
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_interface
DECL|interface|RMOperationHandlerActions
specifier|public
interface|interface
name|RMOperationHandlerActions
block|{
comment|/**    * Release an assigned container.    * @param containerId container    */
DECL|method|releaseAssignedContainer (ContainerId containerId)
name|void
name|releaseAssignedContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Issue a container request.    * @param request    */
DECL|method|addContainerRequest (AMRMClient.ContainerRequest request)
name|void
name|addContainerRequest
parameter_list|(
name|AMRMClient
operator|.
name|ContainerRequest
name|request
parameter_list|)
function_decl|;
comment|/**    * Cancel a specific request.    * @param request request to cancel    */
DECL|method|cancelSingleRequest (AMRMClient.ContainerRequest request)
name|void
name|cancelSingleRequest
parameter_list|(
name|AMRMClient
operator|.
name|ContainerRequest
name|request
parameter_list|)
function_decl|;
comment|/**    * Remove a container request.    * @param priority1 priority to remove at    * @param priority2 second priority to target    * @param count number to remove    */
DECL|method|cancelContainerRequests (Priority priority1, Priority priority2, int count)
name|int
name|cancelContainerRequests
parameter_list|(
name|Priority
name|priority1
parameter_list|,
name|Priority
name|priority2
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
comment|/**    * Blacklist resources.    * @param blacklistAdditions resources to add to the blacklist    * @param blacklistRemovals resources to remove from the blacklist    */
DECL|method|updateBlacklist (List<String> blacklistAdditions, List<String> blacklistRemovals)
name|void
name|updateBlacklist
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|blacklistAdditions
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|blacklistRemovals
parameter_list|)
function_decl|;
comment|/**    * Execute an entire list of operations.    * @param operations ops    */
DECL|method|execute (List<AbstractRMOperation> operations)
name|void
name|execute
parameter_list|(
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|operations
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

