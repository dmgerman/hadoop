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
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|ProviderService
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

begin_class
DECL|class|ProviderNotifyingOperationHandler
specifier|public
class|class
name|ProviderNotifyingOperationHandler
extends|extends
name|RMOperationHandler
block|{
DECL|field|providerService
specifier|private
specifier|final
name|ProviderService
name|providerService
decl_stmt|;
DECL|method|ProviderNotifyingOperationHandler (ProviderService providerService)
specifier|public
name|ProviderNotifyingOperationHandler
parameter_list|(
name|ProviderService
name|providerService
parameter_list|)
block|{
name|this
operator|.
name|providerService
operator|=
name|providerService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|releaseAssignedContainer (ContainerId containerId)
specifier|public
name|void
name|releaseAssignedContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|providerService
operator|.
name|releaseAssignedContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addContainerRequest (AMRMClient.ContainerRequest req)
specifier|public
name|void
name|addContainerRequest
parameter_list|(
name|AMRMClient
operator|.
name|ContainerRequest
name|req
parameter_list|)
block|{
name|providerService
operator|.
name|addContainerRequest
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cancelContainerRequests (Priority priority1, Priority priority2, int count)
specifier|public
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
block|{
return|return
name|providerService
operator|.
name|cancelContainerRequests
argument_list|(
name|priority1
argument_list|,
name|priority2
argument_list|,
name|count
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cancelSingleRequest (AMRMClient.ContainerRequest request)
specifier|public
name|void
name|cancelSingleRequest
parameter_list|(
name|AMRMClient
operator|.
name|ContainerRequest
name|request
parameter_list|)
block|{
name|providerService
operator|.
name|cancelSingleRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateBlacklist (List<String> blacklistAdditions, List<String> blacklistRemovals)
specifier|public
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
block|{
name|providerService
operator|.
name|updateBlacklist
argument_list|(
name|blacklistAdditions
argument_list|,
name|blacklistRemovals
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

