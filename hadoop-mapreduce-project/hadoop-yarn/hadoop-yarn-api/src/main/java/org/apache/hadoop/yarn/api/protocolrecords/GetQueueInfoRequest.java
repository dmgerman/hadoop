begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
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
name|protocolrecords
package|;
end_package

begin_interface
DECL|interface|GetQueueInfoRequest
specifier|public
interface|interface
name|GetQueueInfoRequest
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
DECL|method|getIncludeApplications ()
name|boolean
name|getIncludeApplications
parameter_list|()
function_decl|;
DECL|method|setIncludeApplications (boolean includeApplications)
name|void
name|setIncludeApplications
parameter_list|(
name|boolean
name|includeApplications
parameter_list|)
function_decl|;
DECL|method|getIncludeChildQueues ()
name|boolean
name|getIncludeChildQueues
parameter_list|()
function_decl|;
DECL|method|setIncludeChildQueues (boolean includeChildQueues)
name|void
name|setIncludeChildQueues
parameter_list|(
name|boolean
name|includeChildQueues
parameter_list|)
function_decl|;
DECL|method|getRecursive ()
name|boolean
name|getRecursive
parameter_list|()
function_decl|;
DECL|method|setRecursive (boolean recursive)
name|void
name|setRecursive
parameter_list|(
name|boolean
name|recursive
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

