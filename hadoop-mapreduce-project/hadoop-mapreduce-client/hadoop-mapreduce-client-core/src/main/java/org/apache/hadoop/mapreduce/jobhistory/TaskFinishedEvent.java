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

begin_comment
comment|/**  * Event to record the successful completion of a task  *  */
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
DECL|class|TaskFinishedEvent
specifier|public
class|class
name|TaskFinishedEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|TaskFinished
name|datum
init|=
literal|null
decl_stmt|;
DECL|field|taskid
specifier|private
name|TaskID
name|taskid
decl_stmt|;
DECL|field|successfulAttemptId
specifier|private
name|TaskAttemptID
name|successfulAttemptId
decl_stmt|;
DECL|field|finishTime
specifier|private
name|long
name|finishTime
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
DECL|field|counters
specifier|private
name|Counters
name|counters
decl_stmt|;
comment|/**    * Create an event to record the successful completion of a task    * @param id Task ID    * @param attemptId Task Attempt ID of the successful attempt for this task    * @param finishTime Finish time of the task    * @param taskType Type of the task    * @param status Status string    * @param counters Counters for the task    */
DECL|method|TaskFinishedEvent (TaskID id, TaskAttemptID attemptId, long finishTime, TaskType taskType, String status, Counters counters)
specifier|public
name|TaskFinishedEvent
parameter_list|(
name|TaskID
name|id
parameter_list|,
name|TaskAttemptID
name|attemptId
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|String
name|status
parameter_list|,
name|Counters
name|counters
parameter_list|)
block|{
name|this
operator|.
name|taskid
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|successfulAttemptId
operator|=
name|attemptId
expr_stmt|;
name|this
operator|.
name|finishTime
operator|=
name|finishTime
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
name|counters
operator|=
name|counters
expr_stmt|;
block|}
DECL|method|TaskFinishedEvent ()
name|TaskFinishedEvent
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
name|TaskFinished
argument_list|()
expr_stmt|;
name|datum
operator|.
name|setTaskid
argument_list|(
operator|new
name|Utf8
argument_list|(
name|taskid
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|successfulAttemptId
operator|!=
literal|null
condition|)
block|{
name|datum
operator|.
name|setSuccessfulAttemptId
argument_list|(
operator|new
name|Utf8
argument_list|(
name|successfulAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|datum
operator|.
name|setFinishTime
argument_list|(
name|finishTime
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
name|setStatus
argument_list|(
operator|new
name|Utf8
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|datum
return|;
block|}
DECL|method|setDatum (Object oDatum)
specifier|public
name|void
name|setDatum
parameter_list|(
name|Object
name|oDatum
parameter_list|)
block|{
name|this
operator|.
name|datum
operator|=
operator|(
name|TaskFinished
operator|)
name|oDatum
expr_stmt|;
name|this
operator|.
name|taskid
operator|=
name|TaskID
operator|.
name|forName
argument_list|(
name|datum
operator|.
name|getTaskid
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|datum
operator|.
name|getSuccessfulAttemptId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|successfulAttemptId
operator|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|datum
operator|.
name|getSuccessfulAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
block|}
comment|/** Get task id */
DECL|method|getTaskId ()
specifier|public
name|TaskID
name|getTaskId
parameter_list|()
block|{
return|return
name|taskid
return|;
block|}
comment|/** Get successful task attempt id */
DECL|method|getSuccessfulTaskAttemptId ()
specifier|public
name|TaskAttemptID
name|getSuccessfulTaskAttemptId
parameter_list|()
block|{
return|return
name|successfulAttemptId
return|;
block|}
comment|/** Get the task finish time */
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
comment|/** Get task counters */
DECL|method|getCounters ()
specifier|public
name|Counters
name|getCounters
parameter_list|()
block|{
return|return
name|counters
return|;
block|}
comment|/** Get task type */
DECL|method|getTaskType ()
specifier|public
name|TaskType
name|getTaskType
parameter_list|()
block|{
return|return
name|taskType
return|;
block|}
comment|/** Get task status */
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
comment|/** Get event type */
DECL|method|getEventType ()
specifier|public
name|EventType
name|getEventType
parameter_list|()
block|{
return|return
name|EventType
operator|.
name|TASK_FINISHED
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
literal|"STATUS"
argument_list|,
name|TaskStatus
operator|.
name|State
operator|.
name|SUCCEEDED
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"SUCCESSFUL_TASK_ATTEMPT_ID"
argument_list|,
name|getSuccessfulTaskAttemptId
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|getSuccessfulTaskAttemptId
argument_list|()
operator|.
name|toString
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
name|jobMetrics
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
name|jobMetrics
return|;
block|}
block|}
end_class

end_unit

