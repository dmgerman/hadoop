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
name|worker
operator|.
name|WorkerId
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
name|Text
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
name|ipc
operator|.
name|ProtocolInfo
import|;
end_import

begin_comment
comment|/**  * Define work assignment protocol.  */
end_comment

begin_interface
annotation|@
name|ProtocolInfo
argument_list|(
name|protocolName
operator|=
literal|"WorkAssignmentProtocol"
argument_list|,
name|protocolVersion
operator|=
literal|1
argument_list|)
DECL|interface|WorkAssignmentProtocol
specifier|public
interface|interface
name|WorkAssignmentProtocol
block|{
comment|/**    * Get next workerId to which new task will be assigned.    * @return return workerId text    */
DECL|method|getNewWorkerId ()
name|Text
name|getNewWorkerId
parameter_list|()
function_decl|;
comment|/**    * Register Worker.    * When worker will be launched first, it needs to be registered with Master.    * @param workerId : Worker Id    * @return Task instance    */
DECL|method|registerWorker (WorkerId workerId)
name|Task
name|registerWorker
parameter_list|(
name|WorkerId
name|workerId
parameter_list|)
function_decl|;
comment|/**    * De Register worker.    * When worker is de-registered, no new task will be assigned to this worker.    * @param workerId : Worker identifier    */
DECL|method|deRegisterWorker (WorkerId workerId)
name|void
name|deRegisterWorker
parameter_list|(
name|WorkerId
name|workerId
parameter_list|)
function_decl|;
comment|/**    * Worker sends heartbeat to Master.    * @param workerId : Worker Id    * @param taskStatusList : TaskStatus list of all tasks assigned to worker.    * @return Task instance    */
DECL|method|sendHeartbeat (WorkerId workerId, TaskStatus[] taskStatusList)
name|Task
name|sendHeartbeat
parameter_list|(
name|WorkerId
name|workerId
parameter_list|,
name|TaskStatus
index|[]
name|taskStatusList
parameter_list|)
function_decl|;
comment|/**    * Add Task to the list.    * @param task : Task object    */
DECL|method|addTask (Task task)
name|void
name|addTask
parameter_list|(
name|Task
name|task
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

