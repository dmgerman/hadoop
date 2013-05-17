begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|Arrays
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
name|io
operator|.
name|Writable
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
name|WritableUtils
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
name|TIPStatus
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
name|TaskID
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
name|util
operator|.
name|StringInterner
import|;
end_import

begin_comment
comment|/** A report on the state of a task. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|TaskReport
specifier|public
class|class
name|TaskReport
implements|implements
name|Writable
block|{
DECL|field|taskid
specifier|private
name|TaskID
name|taskid
decl_stmt|;
DECL|field|progress
specifier|private
name|float
name|progress
decl_stmt|;
DECL|field|state
specifier|private
name|String
name|state
decl_stmt|;
DECL|field|diagnostics
specifier|private
name|String
index|[]
name|diagnostics
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|finishTime
specifier|private
name|long
name|finishTime
decl_stmt|;
DECL|field|counters
specifier|private
name|Counters
name|counters
decl_stmt|;
DECL|field|currentStatus
specifier|private
name|TIPStatus
name|currentStatus
decl_stmt|;
DECL|field|runningAttempts
specifier|private
name|Collection
argument_list|<
name|TaskAttemptID
argument_list|>
name|runningAttempts
init|=
operator|new
name|ArrayList
argument_list|<
name|TaskAttemptID
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|successfulAttempt
specifier|private
name|TaskAttemptID
name|successfulAttempt
init|=
operator|new
name|TaskAttemptID
argument_list|()
decl_stmt|;
DECL|method|TaskReport ()
specifier|public
name|TaskReport
parameter_list|()
block|{
name|taskid
operator|=
operator|new
name|TaskID
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new TaskReport object    * @param taskid    * @param progress    * @param state    * @param diagnostics    * @param currentStatus    * @param startTime    * @param finishTime    * @param counters    */
DECL|method|TaskReport (TaskID taskid, float progress, String state, String[] diagnostics, TIPStatus currentStatus, long startTime, long finishTime, Counters counters)
specifier|public
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
name|this
operator|.
name|taskid
operator|=
name|taskid
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
name|diagnostics
expr_stmt|;
name|this
operator|.
name|currentStatus
operator|=
name|currentStatus
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
name|this
operator|.
name|counters
operator|=
name|counters
expr_stmt|;
block|}
comment|/** The string of the task ID. */
DECL|method|getTaskId ()
specifier|public
name|String
name|getTaskId
parameter_list|()
block|{
return|return
name|taskid
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** The ID of the task. */
DECL|method|getTaskID ()
specifier|public
name|TaskID
name|getTaskID
parameter_list|()
block|{
return|return
name|taskid
return|;
block|}
comment|/** The amount completed, between zero and one. */
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|progress
return|;
block|}
comment|/** The most recent state, reported by the Reporter. */
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/** A list of error messages. */
DECL|method|getDiagnostics ()
specifier|public
name|String
index|[]
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
comment|/** A table of counters. */
DECL|method|getTaskCounters ()
specifier|public
name|Counters
name|getTaskCounters
parameter_list|()
block|{
return|return
name|counters
return|;
block|}
comment|/** The current status */
DECL|method|getCurrentStatus ()
specifier|public
name|TIPStatus
name|getCurrentStatus
parameter_list|()
block|{
return|return
name|currentStatus
return|;
block|}
comment|/**    * Get finish time of task.     * @return 0, if finish time was not set else returns finish time.    */
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|finishTime
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
name|this
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
block|}
comment|/**    * Get start time of task.     * @return 0 if start time was not set, else start time.     */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
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
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
comment|/**     * set successful attempt ID of the task.     */
DECL|method|setSuccessfulAttemptId (TaskAttemptID t)
specifier|protected
name|void
name|setSuccessfulAttemptId
parameter_list|(
name|TaskAttemptID
name|t
parameter_list|)
block|{
name|successfulAttempt
operator|=
name|t
expr_stmt|;
block|}
comment|/**    * Get the attempt ID that took this task to completion    */
DECL|method|getSuccessfulTaskAttemptId ()
specifier|public
name|TaskAttemptID
name|getSuccessfulTaskAttemptId
parameter_list|()
block|{
return|return
name|successfulAttempt
return|;
block|}
comment|/**     * set running attempt(s) of the task.     */
DECL|method|setRunningTaskAttemptIds ( Collection<TaskAttemptID> runningAttempts)
specifier|protected
name|void
name|setRunningTaskAttemptIds
parameter_list|(
name|Collection
argument_list|<
name|TaskAttemptID
argument_list|>
name|runningAttempts
parameter_list|)
block|{
name|this
operator|.
name|runningAttempts
operator|=
name|runningAttempts
expr_stmt|;
block|}
comment|/**    * Get the running task attempt IDs for this task    */
DECL|method|getRunningTaskAttemptIds ()
specifier|public
name|Collection
argument_list|<
name|TaskAttemptID
argument_list|>
name|getRunningTaskAttemptIds
parameter_list|()
block|{
return|return
name|runningAttempts
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|TaskReport
name|report
init|=
operator|(
name|TaskReport
operator|)
name|o
decl_stmt|;
return|return
name|counters
operator|.
name|equals
argument_list|(
name|report
operator|.
name|getTaskCounters
argument_list|()
argument_list|)
operator|&&
name|Arrays
operator|.
name|toString
argument_list|(
name|this
operator|.
name|diagnostics
argument_list|)
operator|.
name|equals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|report
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
argument_list|)
operator|&&
name|this
operator|.
name|finishTime
operator|==
name|report
operator|.
name|getFinishTime
argument_list|()
operator|&&
name|this
operator|.
name|progress
operator|==
name|report
operator|.
name|getProgress
argument_list|()
operator|&&
name|this
operator|.
name|startTime
operator|==
name|report
operator|.
name|getStartTime
argument_list|()
operator|&&
name|this
operator|.
name|state
operator|.
name|equals
argument_list|(
name|report
operator|.
name|getState
argument_list|()
argument_list|)
operator|&&
name|this
operator|.
name|taskid
operator|.
name|equals
argument_list|(
name|report
operator|.
name|getTaskID
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|counters
operator|.
name|toString
argument_list|()
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|this
operator|.
name|diagnostics
argument_list|)
operator|+
name|this
operator|.
name|finishTime
operator|+
name|this
operator|.
name|progress
operator|+
name|this
operator|.
name|startTime
operator|+
name|this
operator|.
name|state
operator|+
name|this
operator|.
name|taskid
operator|.
name|toString
argument_list|()
operator|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|//////////////////////////////////////////////
comment|// Writable
comment|//////////////////////////////////////////////
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|taskid
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeStringArray
argument_list|(
name|out
argument_list|,
name|diagnostics
argument_list|)
expr_stmt|;
name|counters
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|currentStatus
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentStatus
operator|==
name|TIPStatus
operator|.
name|RUNNING
condition|)
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|runningAttempts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TaskAttemptID
name|t
index|[]
init|=
operator|new
name|TaskAttemptID
index|[
literal|0
index|]
decl_stmt|;
name|t
operator|=
name|runningAttempts
operator|.
name|toArray
argument_list|(
name|t
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
index|[
name|i
index|]
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|currentStatus
operator|==
name|TIPStatus
operator|.
name|COMPLETE
condition|)
block|{
name|successfulAttempt
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|taskid
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|diagnostics
operator|=
name|WritableUtils
operator|.
name|readStringArray
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|counters
operator|=
operator|new
name|Counters
argument_list|()
expr_stmt|;
name|counters
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|currentStatus
operator|=
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|TIPStatus
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentStatus
operator|==
name|TIPStatus
operator|.
name|RUNNING
condition|)
block|{
name|int
name|num
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|TaskAttemptID
name|t
init|=
operator|new
name|TaskAttemptID
argument_list|()
decl_stmt|;
name|t
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|runningAttempts
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|currentStatus
operator|==
name|TIPStatus
operator|.
name|COMPLETE
condition|)
block|{
name|successfulAttempt
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

