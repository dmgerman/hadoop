begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|scheduler
operator|.
name|capacity
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|security
operator|.
name|ContainerTokenSecretManager
import|;
end_import

begin_comment
comment|/**  * Read-only interface to {@link CapacityScheduler} context.  */
end_comment

begin_interface
DECL|interface|CapacitySchedulerContext
specifier|public
interface|interface
name|CapacitySchedulerContext
block|{
DECL|method|getConfiguration ()
name|CapacitySchedulerConfiguration
name|getConfiguration
parameter_list|()
function_decl|;
DECL|method|getMinimumResourceCapability ()
name|Resource
name|getMinimumResourceCapability
parameter_list|()
function_decl|;
DECL|method|getMaximumResourceCapability ()
name|Resource
name|getMaximumResourceCapability
parameter_list|()
function_decl|;
DECL|method|getContainerTokenSecretManager ()
name|ContainerTokenSecretManager
name|getContainerTokenSecretManager
parameter_list|()
function_decl|;
DECL|method|getNumClusterNodes ()
name|int
name|getNumClusterNodes
parameter_list|()
function_decl|;
DECL|method|getRMContext ()
name|RMContext
name|getRMContext
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

