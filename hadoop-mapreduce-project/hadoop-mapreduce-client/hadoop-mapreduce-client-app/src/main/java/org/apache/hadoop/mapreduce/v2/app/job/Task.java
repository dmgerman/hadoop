begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job
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
operator|.
name|job
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
name|mapreduce
operator|.
name|Counters
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
name|TaskId
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
name|TaskReport
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
name|TaskState
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
name|TaskType
import|;
end_import

begin_comment
comment|/**  * Read only view of Task.  */
end_comment

begin_interface
DECL|interface|Task
specifier|public
interface|interface
name|Task
block|{
DECL|method|getID ()
name|TaskId
name|getID
parameter_list|()
function_decl|;
DECL|method|getReport ()
name|TaskReport
name|getReport
parameter_list|()
function_decl|;
DECL|method|getState ()
name|TaskState
name|getState
parameter_list|()
function_decl|;
DECL|method|getCounters ()
name|Counters
name|getCounters
parameter_list|()
function_decl|;
DECL|method|getProgress ()
name|float
name|getProgress
parameter_list|()
function_decl|;
DECL|method|getType ()
name|TaskType
name|getType
parameter_list|()
function_decl|;
DECL|method|getAttempts ()
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|TaskAttempt
argument_list|>
name|getAttempts
parameter_list|()
function_decl|;
DECL|method|getAttempt (TaskAttemptId attemptID)
name|TaskAttempt
name|getAttempt
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
function_decl|;
comment|/** Has Task reached the final state or not.    */
DECL|method|isFinished ()
name|boolean
name|isFinished
parameter_list|()
function_decl|;
comment|/**    * Can the output of the taskAttempt be committed. Note that once the task    * gives a go for a commit, further canCommit requests from any other attempts    * should return false.    *     * @param taskAttemptID    * @return whether the attempt's output can be committed or not.    */
DECL|method|canCommit (TaskAttemptId taskAttemptID)
name|boolean
name|canCommit
parameter_list|(
name|TaskAttemptId
name|taskAttemptID
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

