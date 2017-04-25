begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_comment
comment|/**  * Implement this interface to be used for RPC scheduling and backoff.  *  */
end_comment

begin_interface
DECL|interface|RpcScheduler
specifier|public
interface|interface
name|RpcScheduler
block|{
comment|/**    * Returns priority level greater than zero as a hint for scheduling.    */
DECL|method|getPriorityLevel (Schedulable obj)
name|int
name|getPriorityLevel
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
function_decl|;
DECL|method|shouldBackOff (Schedulable obj)
name|boolean
name|shouldBackOff
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
function_decl|;
DECL|method|addResponseTime (String name, int priorityLevel, int queueTime, int processingTime)
name|void
name|addResponseTime
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|priorityLevel
parameter_list|,
name|int
name|queueTime
parameter_list|,
name|int
name|processingTime
parameter_list|)
function_decl|;
DECL|method|stop ()
name|void
name|stop
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

