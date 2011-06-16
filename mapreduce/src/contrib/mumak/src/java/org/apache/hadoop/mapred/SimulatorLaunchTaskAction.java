begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|tools
operator|.
name|rumen
operator|.
name|TaskAttemptInfo
import|;
end_import

begin_comment
comment|/**  * This class is used to augment {@link LaunchTaskAction} with run time statistics   * and the final task state (successful or failed).  */
end_comment

begin_class
DECL|class|SimulatorLaunchTaskAction
class|class
name|SimulatorLaunchTaskAction
extends|extends
name|LaunchTaskAction
block|{
comment|/**    * Run time resource usage of the task.    */
DECL|field|taskAttemptInfo
specifier|private
name|TaskAttemptInfo
name|taskAttemptInfo
decl_stmt|;
comment|/**    * Constructs a SimulatorLaunchTaskAction object for a {@link Task}.    * @param task Task task to be launched    * @param taskAttemptInfo resource usage model for task execution    */
DECL|method|SimulatorLaunchTaskAction (Task task, TaskAttemptInfo taskAttemptInfo)
specifier|public
name|SimulatorLaunchTaskAction
parameter_list|(
name|Task
name|task
parameter_list|,
name|TaskAttemptInfo
name|taskAttemptInfo
parameter_list|)
block|{
name|super
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskAttemptInfo
operator|=
name|taskAttemptInfo
expr_stmt|;
block|}
comment|/** Get the resource usage model for the task. */
DECL|method|getTaskAttemptInfo ()
specifier|public
name|TaskAttemptInfo
name|getTaskAttemptInfo
parameter_list|()
block|{
return|return
name|taskAttemptInfo
return|;
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
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"[taskID="
operator|+
name|this
operator|.
name|getTask
argument_list|()
operator|.
name|getTaskID
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

