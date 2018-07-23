begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.component
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|component
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
name|service
operator|.
name|component
operator|.
name|instance
operator|.
name|ComponentInstance
import|;
end_import

begin_comment
comment|/**  * Interface for Component Restart policies.  * Which is used to make decisions on termination/restart of components and  * their instances.  */
end_comment

begin_interface
DECL|interface|ComponentRestartPolicy
specifier|public
interface|interface
name|ComponentRestartPolicy
block|{
DECL|method|isLongLived ()
name|boolean
name|isLongLived
parameter_list|()
function_decl|;
DECL|method|hasCompleted (Component component)
name|boolean
name|hasCompleted
parameter_list|(
name|Component
name|component
parameter_list|)
function_decl|;
DECL|method|hasCompletedSuccessfully (Component component)
name|boolean
name|hasCompletedSuccessfully
parameter_list|(
name|Component
name|component
parameter_list|)
function_decl|;
DECL|method|shouldRelaunchInstance (ComponentInstance componentInstance, ContainerStatus containerStatus)
name|boolean
name|shouldRelaunchInstance
parameter_list|(
name|ComponentInstance
name|componentInstance
parameter_list|,
name|ContainerStatus
name|containerStatus
parameter_list|)
function_decl|;
DECL|method|isReadyForDownStream (Component component)
name|boolean
name|isReadyForDownStream
parameter_list|(
name|Component
name|component
parameter_list|)
function_decl|;
DECL|method|allowUpgrades ()
name|boolean
name|allowUpgrades
parameter_list|()
function_decl|;
DECL|method|shouldTerminate (Component component)
name|boolean
name|shouldTerminate
parameter_list|(
name|Component
name|component
parameter_list|)
function_decl|;
DECL|method|allowContainerRetriesForInstance (ComponentInstance componentInstance)
name|boolean
name|allowContainerRetriesForInstance
parameter_list|(
name|ComponentInstance
name|componentInstance
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

