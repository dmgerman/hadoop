begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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
name|mapred
operator|.
name|TaskStatus
operator|.
name|State
import|;
end_import

begin_comment
comment|/**  * {@link TaskAttemptInfo} is a collection of statistics about a particular  * task-attempt gleaned from job-history of the job.  */
end_comment

begin_class
DECL|class|TaskAttemptInfo
specifier|public
specifier|abstract
class|class
name|TaskAttemptInfo
block|{
DECL|field|state
specifier|protected
specifier|final
name|State
name|state
decl_stmt|;
DECL|field|taskInfo
specifier|protected
specifier|final
name|TaskInfo
name|taskInfo
decl_stmt|;
DECL|method|TaskAttemptInfo (State state, TaskInfo taskInfo)
specifier|protected
name|TaskAttemptInfo
parameter_list|(
name|State
name|state
parameter_list|,
name|TaskInfo
name|taskInfo
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
name|State
operator|.
name|SUCCEEDED
operator|||
name|state
operator|==
name|State
operator|.
name|FAILED
condition|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"status cannot be "
operator|+
name|state
argument_list|)
throw|;
block|}
name|this
operator|.
name|taskInfo
operator|=
name|taskInfo
expr_stmt|;
block|}
comment|/**    * Get the final {@link State} of the task-attempt.    *     * @return the final<code>State</code> of the task-attempt    */
DECL|method|getRunState ()
specifier|public
name|State
name|getRunState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**    * Get the total runtime for the task-attempt.    *     * @return the total runtime for the task-attempt    */
DECL|method|getRuntime ()
specifier|public
specifier|abstract
name|long
name|getRuntime
parameter_list|()
function_decl|;
comment|/**    * Get the {@link TaskInfo} for the given task-attempt.    *     * @return the<code>TaskInfo</code> for the given task-attempt    */
DECL|method|getTaskInfo ()
specifier|public
name|TaskInfo
name|getTaskInfo
parameter_list|()
block|{
return|return
name|taskInfo
return|;
block|}
block|}
end_class

end_unit

