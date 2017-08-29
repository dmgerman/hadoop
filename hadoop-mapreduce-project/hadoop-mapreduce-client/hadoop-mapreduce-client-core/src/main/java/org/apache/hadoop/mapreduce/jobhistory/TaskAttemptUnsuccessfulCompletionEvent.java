begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
operator|.
name|util
operator|.
name|Utf8
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
name|mapred
operator|.
name|ProgressSplitsBlock
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
name|TaskStatus
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
name|TaskAttemptID
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
name|mapreduce
operator|.
name|TaskType
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
name|util
operator|.
name|JobHistoryEventUtils
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
name|StringUtils
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEvent
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineMetric
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
name|yarn
operator|.
name|util
operator|.
name|SystemClock
import|;
end_import

begin_comment
comment|/**  * Event to record unsuccessful (Killed/Failed) completion of task attempts  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TaskAttemptUnsuccessfulCompletionEvent
specifier|public
class|class
name|TaskAttemptUnsuccessfulCompletionEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|TaskAttemptUnsuccessfulCompletion
name|datum
init|=
literal|null
decl_stmt|;
DECL|field|attemptId
specifier|private
name|TaskAttemptID
name|attemptId
decl_stmt|;
DECL|field|taskType
specifier|private
name|TaskType
name|taskType
decl_stmt|;
DECL|field|status
specifier|private
name|String
name|status
decl_stmt|;
DECL|field|finishTime
specifier|private
name|long
name|finishTime
decl_stmt|;
DECL|field|hostname
specifier|private
name|String
name|hostname
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|rackName
specifier|private
name|String
name|rackName
decl_stmt|;
DECL|field|error
specifier|private
name|String
name|error
decl_stmt|;
DECL|field|counters
specifier|private
name|Counters
name|counters
decl_stmt|;
DECL|field|allSplits
name|int
index|[]
index|[]
name|allSplits
decl_stmt|;
DECL|field|clockSplits
name|int
index|[]
name|clockSplits
decl_stmt|;
DECL|field|cpuUsages
name|int
index|[]
name|cpuUsages
decl_stmt|;
DECL|field|vMemKbytes
name|int
index|[]
name|vMemKbytes
decl_stmt|;
DECL|field|physMemKbytes
name|int
index|[]
name|physMemKbytes
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|EMPTY_COUNTERS
specifier|private
specifier|static
specifier|final
name|Counters
name|EMPTY_COUNTERS
init|=
operator|new
name|Counters
argument_list|()
decl_stmt|;
comment|/**    * Create an event to record the unsuccessful completion of attempts.    * @param id Attempt ID    * @param taskType Type of the task    * @param status Status of the attempt    * @param finishTime Finish time of the attempt    * @param hostname Name of the host where the attempt executed    * @param port rpc port for for the tracker    * @param rackName Name of the rack where the attempt executed    * @param error Error string    * @param counters Counters for the attempt    * @param allSplits the "splits", or a pixelated graph of various    *        measurable worker node state variables against progress.    *        Currently there are four; wallclock time, CPU time,    *        virtual memory and physical memory.    * @param startTs Task start time to be used for writing entity to ATSv2.    */
DECL|method|TaskAttemptUnsuccessfulCompletionEvent (TaskAttemptID id, TaskType taskType, String status, long finishTime, String hostname, int port, String rackName, String error, Counters counters, int[][] allSplits, long startTs)
specifier|public
name|TaskAttemptUnsuccessfulCompletionEvent
parameter_list|(
name|TaskAttemptID
name|id
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|String
name|status
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|rackName
parameter_list|,
name|String
name|error
parameter_list|,
name|Counters
name|counters
parameter_list|,
name|int
index|[]
index|[]
name|allSplits
parameter_list|,
name|long
name|startTs
parameter_list|)
block|{
name|this
operator|.
name|attemptId
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|taskType
operator|=
name|taskType
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
name|this
operator|.
name|hostname
operator|=
name|hostname
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|rackName
operator|=
name|rackName
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
name|this
operator|.
name|counters
operator|=
name|counters
expr_stmt|;
name|this
operator|.
name|allSplits
operator|=
name|allSplits
expr_stmt|;
name|this
operator|.
name|clockSplits
operator|=
name|ProgressSplitsBlock
operator|.
name|arrayGetWallclockTime
argument_list|(
name|allSplits
argument_list|)
expr_stmt|;
name|this
operator|.
name|cpuUsages
operator|=
name|ProgressSplitsBlock
operator|.
name|arrayGetCPUTime
argument_list|(
name|allSplits
argument_list|)
expr_stmt|;
name|this
operator|.
name|vMemKbytes
operator|=
name|ProgressSplitsBlock
operator|.
name|arrayGetVMemKbytes
argument_list|(
name|allSplits
argument_list|)
expr_stmt|;
name|this
operator|.
name|physMemKbytes
operator|=
name|ProgressSplitsBlock
operator|.
name|arrayGetPhysMemKbytes
argument_list|(
name|allSplits
argument_list|)
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|startTs
expr_stmt|;
block|}
DECL|method|TaskAttemptUnsuccessfulCompletionEvent (TaskAttemptID id, TaskType taskType, String status, long finishTime, String hostname, int port, String rackName, String error, Counters counters, int[][] allSplits)
specifier|public
name|TaskAttemptUnsuccessfulCompletionEvent
parameter_list|(
name|TaskAttemptID
name|id
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|String
name|status
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|rackName
parameter_list|,
name|String
name|error
parameter_list|,
name|Counters
name|counters
parameter_list|,
name|int
index|[]
index|[]
name|allSplits
parameter_list|)
block|{
name|this
argument_list|(
name|id
argument_list|,
name|taskType
argument_list|,
name|status
argument_list|,
name|finishTime
argument_list|,
name|hostname
argument_list|,
name|port
argument_list|,
name|rackName
argument_list|,
name|error
argument_list|,
name|counters
argument_list|,
name|allSplits
argument_list|,
name|SystemClock
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated please use the constructor with an additional    *              argument, an array of splits arrays instead.  See    *              {@link org.apache.hadoop.mapred.ProgressSplitsBlock}    *              for an explanation of the meaning of that parameter.    *    * Create an event to record the unsuccessful completion of attempts    * @param id Attempt ID    * @param taskType Type of the task    * @param status Status of the attempt    * @param finishTime Finish time of the attempt    * @param hostname Name of the host where the attempt executed    * @param error Error string    */
DECL|method|TaskAttemptUnsuccessfulCompletionEvent (TaskAttemptID id, TaskType taskType, String status, long finishTime, String hostname, String error)
specifier|public
name|TaskAttemptUnsuccessfulCompletionEvent
parameter_list|(
name|TaskAttemptID
name|id
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|String
name|status
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|String
name|error
parameter_list|)
block|{
name|this
argument_list|(
name|id
argument_list|,
name|taskType
argument_list|,
name|status
argument_list|,
name|finishTime
argument_list|,
name|hostname
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
name|error
argument_list|,
name|EMPTY_COUNTERS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskAttemptUnsuccessfulCompletionEvent (TaskAttemptID id, TaskType taskType, String status, long finishTime, String hostname, int port, String rackName, String error, int[][] allSplits)
specifier|public
name|TaskAttemptUnsuccessfulCompletionEvent
parameter_list|(
name|TaskAttemptID
name|id
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|String
name|status
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|String
name|hostname
parameter_list|,
name|int
name|port
parameter_list|,
name|String
name|rackName
parameter_list|,
name|String
name|error
parameter_list|,
name|int
index|[]
index|[]
name|allSplits
parameter_list|)
block|{
name|this
argument_list|(
name|id
argument_list|,
name|taskType
argument_list|,
name|status
argument_list|,
name|finishTime
argument_list|,
name|hostname
argument_list|,
name|port
argument_list|,
name|rackName
argument_list|,
name|error
argument_list|,
name|EMPTY_COUNTERS
argument_list|,
name|allSplits
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskAttemptUnsuccessfulCompletionEvent ()
name|TaskAttemptUnsuccessfulCompletionEvent
parameter_list|()
block|{}
DECL|method|getDatum ()
specifier|public
name|Object
name|getDatum
parameter_list|()
block|{
if|if
condition|(
name|datum
operator|==
literal|null
condition|)
block|{
name|datum
operator|=
operator|new
name|TaskAttemptUnsuccessfulCompletion
argument_list|()
expr_stmt|;
name|datum
operator|.
name|setTaskid
argument_list|(
operator|new
name|Utf8
argument_list|(
name|attemptId
operator|.
name|getTaskID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setTaskType
argument_list|(
operator|new
name|Utf8
argument_list|(
name|taskType
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setAttemptId
argument_list|(
operator|new
name|Utf8
argument_list|(
name|attemptId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setHostname
argument_list|(
operator|new
name|Utf8
argument_list|(
name|hostname
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rackName
operator|!=
literal|null
condition|)
block|{
name|datum
operator|.
name|setRackname
argument_list|(
operator|new
name|Utf8
argument_list|(
name|rackName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|datum
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setError
argument_list|(
operator|new
name|Utf8
argument_list|(
name|error
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setStatus
argument_list|(
operator|new
name|Utf8
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setCounters
argument_list|(
name|EventWriter
operator|.
name|toAvro
argument_list|(
name|counters
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setClockSplits
argument_list|(
name|AvroArrayUtils
operator|.
name|toAvro
argument_list|(
name|ProgressSplitsBlock
operator|.
name|arrayGetWallclockTime
argument_list|(
name|allSplits
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setCpuUsages
argument_list|(
name|AvroArrayUtils
operator|.
name|toAvro
argument_list|(
name|ProgressSplitsBlock
operator|.
name|arrayGetCPUTime
argument_list|(
name|allSplits
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setVMemKbytes
argument_list|(
name|AvroArrayUtils
operator|.
name|toAvro
argument_list|(
name|ProgressSplitsBlock
operator|.
name|arrayGetVMemKbytes
argument_list|(
name|allSplits
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setPhysMemKbytes
argument_list|(
name|AvroArrayUtils
operator|.
name|toAvro
argument_list|(
name|ProgressSplitsBlock
operator|.
name|arrayGetPhysMemKbytes
argument_list|(
name|allSplits
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|datum
return|;
block|}
DECL|method|setDatum (Object odatum)
specifier|public
name|void
name|setDatum
parameter_list|(
name|Object
name|odatum
parameter_list|)
block|{
name|this
operator|.
name|datum
operator|=
operator|(
name|TaskAttemptUnsuccessfulCompletion
operator|)
name|odatum
expr_stmt|;
name|this
operator|.
name|attemptId
operator|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|datum
operator|.
name|getAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|taskType
operator|=
name|TaskType
operator|.
name|valueOf
argument_list|(
name|datum
operator|.
name|getTaskType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|datum
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|hostname
operator|=
name|datum
operator|.
name|getHostname
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|rackName
operator|=
name|datum
operator|.
name|getRackname
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|datum
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|datum
operator|.
name|getStatus
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|datum
operator|.
name|getError
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|counters
operator|=
name|EventReader
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|getCounters
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|clockSplits
operator|=
name|AvroArrayUtils
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|getClockSplits
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|cpuUsages
operator|=
name|AvroArrayUtils
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|getCpuUsages
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|vMemKbytes
operator|=
name|AvroArrayUtils
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|getVMemKbytes
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|physMemKbytes
operator|=
name|AvroArrayUtils
operator|.
name|fromAvro
argument_list|(
name|datum
operator|.
name|getPhysMemKbytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Gets the task id. */
DECL|method|getTaskId ()
specifier|public
name|TaskID
name|getTaskId
parameter_list|()
block|{
return|return
name|attemptId
operator|.
name|getTaskID
argument_list|()
return|;
block|}
comment|/** Gets the task type. */
DECL|method|getTaskType ()
specifier|public
name|TaskType
name|getTaskType
parameter_list|()
block|{
return|return
name|TaskType
operator|.
name|valueOf
argument_list|(
name|taskType
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** Gets the attempt id. */
DECL|method|getTaskAttemptId ()
specifier|public
name|TaskAttemptID
name|getTaskAttemptId
parameter_list|()
block|{
return|return
name|attemptId
return|;
block|}
comment|/** Gets the finish time. */
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
comment|/**    * Gets the task attempt start time to be used while publishing to ATSv2.    * @return task attempt start time.    */
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
comment|/** Gets the name of the host where the attempt executed. */
DECL|method|getHostname ()
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|hostname
return|;
block|}
comment|/** Gets the rpc port for the host where the attempt executed. */
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|port
return|;
block|}
comment|/** Gets the rack name of the node where the attempt ran. */
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
block|{
return|return
name|rackName
operator|==
literal|null
condition|?
literal|null
else|:
name|rackName
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Gets the error string. */
DECL|method|getError ()
specifier|public
name|String
name|getError
parameter_list|()
block|{
return|return
name|error
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Gets the task attempt status.    * @return task attempt status.    */
DECL|method|getTaskStatus ()
specifier|public
name|String
name|getTaskStatus
parameter_list|()
block|{
return|return
name|status
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Gets the counters. */
DECL|method|getCounters ()
name|Counters
name|getCounters
parameter_list|()
block|{
return|return
name|counters
return|;
block|}
comment|/** Gets the event type. */
DECL|method|getEventType ()
specifier|public
name|EventType
name|getEventType
parameter_list|()
block|{
comment|// Note that the task type can be setup/map/reduce/cleanup but the
comment|// attempt-type can only be map/reduce.
comment|// find out if the task failed or got killed
name|boolean
name|failed
init|=
name|TaskStatus
operator|.
name|State
operator|.
name|FAILED
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|getTaskStatus
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
operator|==
name|TaskType
operator|.
name|MAP
condition|?
operator|(
name|failed
condition|?
name|EventType
operator|.
name|MAP_ATTEMPT_FAILED
else|:
name|EventType
operator|.
name|MAP_ATTEMPT_KILLED
operator|)
else|:
operator|(
name|failed
condition|?
name|EventType
operator|.
name|REDUCE_ATTEMPT_FAILED
else|:
name|EventType
operator|.
name|REDUCE_ATTEMPT_KILLED
operator|)
return|;
block|}
DECL|method|getClockSplits ()
specifier|public
name|int
index|[]
name|getClockSplits
parameter_list|()
block|{
return|return
name|clockSplits
return|;
block|}
DECL|method|getCpuUsages ()
specifier|public
name|int
index|[]
name|getCpuUsages
parameter_list|()
block|{
return|return
name|cpuUsages
return|;
block|}
DECL|method|getVMemKbytes ()
specifier|public
name|int
index|[]
name|getVMemKbytes
parameter_list|()
block|{
return|return
name|vMemKbytes
return|;
block|}
DECL|method|getPhysMemKbytes ()
specifier|public
name|int
index|[]
name|getPhysMemKbytes
parameter_list|()
block|{
return|return
name|physMemKbytes
return|;
block|}
annotation|@
name|Override
DECL|method|toTimelineEvent ()
specifier|public
name|TimelineEvent
name|toTimelineEvent
parameter_list|()
block|{
name|TimelineEvent
name|tEvent
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|tEvent
operator|.
name|setId
argument_list|(
name|StringUtils
operator|.
name|toUpperCase
argument_list|(
name|getEventType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"TASK_TYPE"
argument_list|,
name|getTaskType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"TASK_ATTEMPT_ID"
argument_list|,
name|getTaskAttemptId
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|getTaskAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"FINISH_TIME"
argument_list|,
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"ERROR"
argument_list|,
name|getError
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"STATUS"
argument_list|,
name|getTaskStatus
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"HOSTNAME"
argument_list|,
name|getHostname
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"PORT"
argument_list|,
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"RACK_NAME"
argument_list|,
name|getRackName
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"SHUFFLE_FINISH_TIME"
argument_list|,
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"SORT_FINISH_TIME"
argument_list|,
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"MAP_FINISH_TIME"
argument_list|,
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|tEvent
return|;
block|}
annotation|@
name|Override
DECL|method|getTimelineMetrics ()
specifier|public
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|getTimelineMetrics
parameter_list|()
block|{
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
init|=
name|JobHistoryEventUtils
operator|.
name|countersToTimelineMetric
argument_list|(
name|getCounters
argument_list|()
argument_list|,
name|finishTime
argument_list|)
decl_stmt|;
return|return
name|metrics
return|;
block|}
block|}
end_class

end_unit

