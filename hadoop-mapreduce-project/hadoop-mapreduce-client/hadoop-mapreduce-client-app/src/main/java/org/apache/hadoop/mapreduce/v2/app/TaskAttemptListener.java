begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|mapred
operator|.
name|Task
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
name|mapred
operator|.
name|WrappedJvmID
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
import|;
end_import

begin_comment
comment|/**  * This class listens for changes to the state of a Task.  */
end_comment

begin_interface
DECL|interface|TaskAttemptListener
specifier|public
interface|interface
name|TaskAttemptListener
block|{
DECL|method|getAddress ()
name|InetSocketAddress
name|getAddress
parameter_list|()
function_decl|;
comment|/**    * Register a JVM with the listener.  This should be called as soon as a     * JVM ID is assigned to a task attempt, before it has been launched.    * @param task the task itself for this JVM.    * @param jvmID The ID of the JVM .    */
DECL|method|registerPendingTask (Task task, WrappedJvmID jvmID)
name|void
name|registerPendingTask
parameter_list|(
name|Task
name|task
parameter_list|,
name|WrappedJvmID
name|jvmID
parameter_list|)
function_decl|;
comment|/**    * Register task attempt. This should be called when the JVM has been    * launched.    *     * @param attemptID    *          the id of the attempt for this JVM.    */
DECL|method|registerLaunchedTask (TaskAttemptId attemptID)
name|void
name|registerLaunchedTask
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
function_decl|;
comment|/**    * Unregister the JVM and the attempt associated with it.  This should be     * called when the attempt/JVM has finished executing and is being cleaned up.    * @param attemptID the ID of the attempt.    * @param jvmID the ID of the JVM for that attempt.    */
DECL|method|unregister (TaskAttemptId attemptID, WrappedJvmID jvmID)
name|void
name|unregister
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|,
name|WrappedJvmID
name|jvmID
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

