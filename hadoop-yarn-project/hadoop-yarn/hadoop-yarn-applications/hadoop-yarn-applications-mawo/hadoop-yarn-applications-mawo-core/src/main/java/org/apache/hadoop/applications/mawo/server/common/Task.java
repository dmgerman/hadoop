begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.applications.mawo.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|applications
operator|.
name|mawo
operator|.
name|server
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/**  * Define Task Interface.  */
end_comment

begin_interface
DECL|interface|Task
specifier|public
interface|interface
name|Task
extends|extends
name|Writable
block|{
comment|/**    * Get TaskId of a Task.    * @return value of TaskId    */
DECL|method|getTaskId ()
name|TaskId
name|getTaskId
parameter_list|()
function_decl|;
comment|/**    * Get Environment of Task.    * @return map of environment    */
DECL|method|getEnvironment ()
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnvironment
parameter_list|()
function_decl|;
comment|/**    * Get Task cmd.    * @return value of Task cmd such "sleep 1"    */
DECL|method|getTaskCmd ()
name|String
name|getTaskCmd
parameter_list|()
function_decl|;
comment|/**    * Get Task type such as Simple, Composite.    * @return value of TaskType    */
DECL|method|getTaskType ()
name|TaskType
name|getTaskType
parameter_list|()
function_decl|;
comment|/**    * Set TaskId.    * @param taskId : Task identifier    */
DECL|method|setTaskId (TaskId taskId)
name|void
name|setTaskId
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
function_decl|;
comment|/**    * Set Task environment such as {"HOME":"/user/A"}.    * @param environment : Map of environment variables    */
DECL|method|setEnvironment (Map<String, String> environment)
name|void
name|setEnvironment
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|)
function_decl|;
comment|/**    * Set Task command.    * @param taskCMD : Task command to be executed    */
DECL|method|setTaskCmd (String taskCMD)
name|void
name|setTaskCmd
parameter_list|(
name|String
name|taskCMD
parameter_list|)
function_decl|;
comment|/**    * Get Task Timeout in seconds.    * @return value of TaskTimeout    */
DECL|method|getTimeout ()
name|long
name|getTimeout
parameter_list|()
function_decl|;
comment|/**    * Set Task Timeout.    * @param timeout : value of Task Timeout    */
DECL|method|setTimeout (long timeout)
name|void
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

