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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_interface
DECL|interface|QueueInfo
specifier|public
interface|interface
name|QueueInfo
block|{
DECL|method|getQueueName ()
name|String
name|getQueueName
parameter_list|()
function_decl|;
DECL|method|setQueueName (String queueName)
name|void
name|setQueueName
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
DECL|method|getCapacity ()
name|float
name|getCapacity
parameter_list|()
function_decl|;
DECL|method|setCapacity (float capacity)
name|void
name|setCapacity
parameter_list|(
name|float
name|capacity
parameter_list|)
function_decl|;
DECL|method|getMaximumCapacity ()
name|float
name|getMaximumCapacity
parameter_list|()
function_decl|;
DECL|method|setMaximumCapacity (float maximumCapacity)
name|void
name|setMaximumCapacity
parameter_list|(
name|float
name|maximumCapacity
parameter_list|)
function_decl|;
DECL|method|getCurrentCapacity ()
name|float
name|getCurrentCapacity
parameter_list|()
function_decl|;
DECL|method|setCurrentCapacity (float currentCapacity)
name|void
name|setCurrentCapacity
parameter_list|(
name|float
name|currentCapacity
parameter_list|)
function_decl|;
DECL|method|getChildQueues ()
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|getChildQueues
parameter_list|()
function_decl|;
DECL|method|setChildQueues (List<QueueInfo> childQueues)
name|void
name|setChildQueues
parameter_list|(
name|List
argument_list|<
name|QueueInfo
argument_list|>
name|childQueues
parameter_list|)
function_decl|;
DECL|method|getApplications ()
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplications
parameter_list|()
function_decl|;
DECL|method|setApplications (List<ApplicationReport> applications)
name|void
name|setApplications
parameter_list|(
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|applications
parameter_list|)
function_decl|;
DECL|method|getQueueState ()
name|QueueState
name|getQueueState
parameter_list|()
function_decl|;
DECL|method|setQueueState (QueueState queueState)
name|void
name|setQueueState
parameter_list|(
name|QueueState
name|queueState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

