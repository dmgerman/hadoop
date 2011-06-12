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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/** A report on the state of a task.   * @deprecated Use {@link org.apache.hadoop.mapreduce.TaskReport} instead  **/
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|TaskReport
specifier|public
class|class
name|TaskReport
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskReport
block|{
DECL|method|TaskReport ()
specifier|public
name|TaskReport
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new TaskReport object    * @param taskid    * @param progress    * @param state    * @param diagnostics    * @param startTime    * @param finishTime    * @param counters    * @deprecated    */
annotation|@
name|Deprecated
DECL|method|TaskReport (TaskID taskid, float progress, String state, String[] diagnostics, long startTime, long finishTime, Counters counters)
name|TaskReport
parameter_list|(
name|TaskID
name|taskid
parameter_list|,
name|float
name|progress
parameter_list|,
name|String
name|state
parameter_list|,
name|String
index|[]
name|diagnostics
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|Counters
name|counters
parameter_list|)
block|{
name|this
argument_list|(
name|taskid
argument_list|,
name|progress
argument_list|,
name|state
argument_list|,
name|diagnostics
argument_list|,
literal|null
argument_list|,
name|startTime
argument_list|,
name|finishTime
argument_list|,
name|counters
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new TaskReport object    * @param taskid    * @param progress    * @param state    * @param diagnostics    * @param currentStatus    * @param startTime    * @param finishTime    * @param counters    */
DECL|method|TaskReport (TaskID taskid, float progress, String state, String[] diagnostics, TIPStatus currentStatus, long startTime, long finishTime, Counters counters)
name|TaskReport
parameter_list|(
name|TaskID
name|taskid
parameter_list|,
name|float
name|progress
parameter_list|,
name|String
name|state
parameter_list|,
name|String
index|[]
name|diagnostics
parameter_list|,
name|TIPStatus
name|currentStatus
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|Counters
name|counters
parameter_list|)
block|{
name|super
argument_list|(
name|taskid
argument_list|,
name|progress
argument_list|,
name|state
argument_list|,
name|diagnostics
argument_list|,
name|currentStatus
argument_list|,
name|startTime
argument_list|,
name|finishTime
argument_list|,
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Counters
argument_list|(
name|counters
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|downgrade ( org.apache.hadoop.mapreduce.TaskReport report)
specifier|static
name|TaskReport
name|downgrade
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskReport
name|report
parameter_list|)
block|{
return|return
operator|new
name|TaskReport
argument_list|(
name|TaskID
operator|.
name|downgrade
argument_list|(
name|report
operator|.
name|getTaskId
argument_list|()
argument_list|)
argument_list|,
name|report
operator|.
name|getProgress
argument_list|()
argument_list|,
name|report
operator|.
name|getState
argument_list|()
argument_list|,
name|report
operator|.
name|getDiagnostics
argument_list|()
argument_list|,
name|report
operator|.
name|getCurrentStatus
argument_list|()
argument_list|,
name|report
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|report
operator|.
name|getFinishTime
argument_list|()
argument_list|,
name|Counters
operator|.
name|downgrade
argument_list|(
name|report
operator|.
name|getTaskCounters
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|downgradeArray (org.apache.hadoop. mapreduce.TaskReport[] reports)
specifier|static
name|TaskReport
index|[]
name|downgradeArray
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskReport
index|[]
name|reports
parameter_list|)
block|{
name|List
argument_list|<
name|TaskReport
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|TaskReport
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskReport
name|report
range|:
name|reports
control|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|downgrade
argument_list|(
name|report
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|toArray
argument_list|(
operator|new
name|TaskReport
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/** The id of the task. */
DECL|method|getTaskID ()
specifier|public
name|TaskID
name|getTaskID
parameter_list|()
block|{
return|return
name|TaskID
operator|.
name|downgrade
argument_list|(
name|super
operator|.
name|getTaskId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getCounters ()
specifier|public
name|Counters
name|getCounters
parameter_list|()
block|{
return|return
name|Counters
operator|.
name|downgrade
argument_list|(
name|super
operator|.
name|getTaskCounters
argument_list|()
argument_list|)
return|;
block|}
comment|/**     * set successful attempt ID of the task.     */
DECL|method|setSuccessfulAttempt (TaskAttemptID t)
specifier|public
name|void
name|setSuccessfulAttempt
parameter_list|(
name|TaskAttemptID
name|t
parameter_list|)
block|{
name|super
operator|.
name|setSuccessfulAttemptId
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the attempt ID that took this task to completion    */
DECL|method|getSuccessfulTaskAttempt ()
specifier|public
name|TaskAttemptID
name|getSuccessfulTaskAttempt
parameter_list|()
block|{
return|return
name|TaskAttemptID
operator|.
name|downgrade
argument_list|(
name|super
operator|.
name|getSuccessfulTaskAttemptId
argument_list|()
argument_list|)
return|;
block|}
comment|/**     * set running attempt(s) of the task.     */
DECL|method|setRunningTaskAttempts ( Collection<TaskAttemptID> runningAttempts)
specifier|public
name|void
name|setRunningTaskAttempts
parameter_list|(
name|Collection
argument_list|<
name|TaskAttemptID
argument_list|>
name|runningAttempts
parameter_list|)
block|{
name|Collection
argument_list|<
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptID
argument_list|>
name|attempts
init|=
operator|new
name|ArrayList
argument_list|<
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptID
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskAttemptID
name|id
range|:
name|runningAttempts
control|)
block|{
name|attempts
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setRunningTaskAttemptIds
argument_list|(
name|attempts
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the running task attempt IDs for this task    */
DECL|method|getRunningTaskAttempts ()
specifier|public
name|Collection
argument_list|<
name|TaskAttemptID
argument_list|>
name|getRunningTaskAttempts
parameter_list|()
block|{
name|Collection
argument_list|<
name|TaskAttemptID
argument_list|>
name|attempts
init|=
operator|new
name|ArrayList
argument_list|<
name|TaskAttemptID
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptID
name|id
range|:
name|super
operator|.
name|getRunningTaskAttemptIds
argument_list|()
control|)
block|{
name|attempts
operator|.
name|add
argument_list|(
name|TaskAttemptID
operator|.
name|downgrade
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|attempts
return|;
block|}
comment|/**     * set finish time of task.     * @param finishTime finish time of task.     */
DECL|method|setFinishTime (long finishTime)
specifier|protected
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
block|{
name|super
operator|.
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
block|}
comment|/**     * set start time of the task.     */
DECL|method|setStartTime (long startTime)
specifier|protected
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|super
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

