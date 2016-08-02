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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|ContainerPriority
import|;
end_import

begin_comment
comment|/**  * A container request operation  */
end_comment

begin_class
DECL|class|ContainerRequestOperation
specifier|public
class|class
name|ContainerRequestOperation
extends|extends
name|AbstractRMOperation
block|{
DECL|field|request
specifier|private
specifier|final
name|AMRMClient
operator|.
name|ContainerRequest
name|request
decl_stmt|;
DECL|method|ContainerRequestOperation (AMRMClient.ContainerRequest request)
specifier|public
name|ContainerRequestOperation
parameter_list|(
name|AMRMClient
operator|.
name|ContainerRequest
name|request
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|request
operator|!=
literal|null
argument_list|,
literal|"Null container request"
argument_list|)
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
DECL|method|getRequest ()
specifier|public
name|AMRMClient
operator|.
name|ContainerRequest
name|getRequest
parameter_list|()
block|{
return|return
name|request
return|;
block|}
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|request
operator|.
name|getPriority
argument_list|()
return|;
block|}
DECL|method|getRelaxLocality ()
specifier|public
name|boolean
name|getRelaxLocality
parameter_list|()
block|{
return|return
name|request
operator|.
name|getRelaxLocality
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|execute (RMOperationHandlerActions handler)
specifier|public
name|void
name|execute
parameter_list|(
name|RMOperationHandlerActions
name|handler
parameter_list|)
block|{
name|handler
operator|.
name|addContainerRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"request container for role "
operator|+
name|ContainerPriority
operator|.
name|toString
argument_list|(
name|getPriority
argument_list|()
argument_list|)
operator|+
literal|" request "
operator|+
name|request
operator|+
literal|" relaxLocality="
operator|+
name|getRelaxLocality
argument_list|()
return|;
block|}
block|}
end_class

end_unit

