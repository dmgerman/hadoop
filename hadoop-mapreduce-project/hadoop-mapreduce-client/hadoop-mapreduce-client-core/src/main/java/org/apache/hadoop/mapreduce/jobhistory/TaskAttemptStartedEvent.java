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
name|ContainerId
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
name|ConverterUtils
import|;
end_import

begin_comment
comment|/**  * Event to record start of a task attempt  *  */
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
DECL|class|TaskAttemptStartedEvent
specifier|public
class|class
name|TaskAttemptStartedEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|TaskAttemptStarted
name|datum
init|=
operator|new
name|TaskAttemptStarted
argument_list|()
decl_stmt|;
comment|/**    * Create an event to record the start of an attempt    * @param attemptId Id of the attempt    * @param taskType Type of task    * @param startTime Start time of the attempt    * @param trackerName Name of the Task Tracker where attempt is running    * @param httpPort The port number of the tracker    * @param shufflePort The shuffle port number of the container    * @param containerId The containerId for the task attempt.    * @param locality The locality of the task attempt    * @param avataar The avataar of the task attempt    */
DECL|method|TaskAttemptStartedEvent ( TaskAttemptID attemptId, TaskType taskType, long startTime, String trackerName, int httpPort, int shufflePort, ContainerId containerId, String locality, String avataar)
specifier|public
name|TaskAttemptStartedEvent
parameter_list|(
name|TaskAttemptID
name|attemptId
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|long
name|startTime
parameter_list|,
name|String
name|trackerName
parameter_list|,
name|int
name|httpPort
parameter_list|,
name|int
name|shufflePort
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|String
name|locality
parameter_list|,
name|String
name|avataar
parameter_list|)
block|{
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
name|setStartTime
argument_list|(
name|startTime
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
name|setTrackerName
argument_list|(
operator|new
name|Utf8
argument_list|(
name|trackerName
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setHttpPort
argument_list|(
name|httpPort
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setShufflePort
argument_list|(
name|shufflePort
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setContainerId
argument_list|(
operator|new
name|Utf8
argument_list|(
name|containerId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|locality
operator|!=
literal|null
condition|)
block|{
name|datum
operator|.
name|setLocality
argument_list|(
operator|new
name|Utf8
argument_list|(
name|locality
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|avataar
operator|!=
literal|null
condition|)
block|{
name|datum
operator|.
name|setAvataar
argument_list|(
operator|new
name|Utf8
argument_list|(
name|avataar
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO Remove after MrV1 is removed.
comment|// Using a dummy containerId to prevent jobHistory parse failures.
DECL|method|TaskAttemptStartedEvent (TaskAttemptID attemptId, TaskType taskType, long startTime, String trackerName, int httpPort, int shufflePort, String locality, String avataar)
specifier|public
name|TaskAttemptStartedEvent
parameter_list|(
name|TaskAttemptID
name|attemptId
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|long
name|startTime
parameter_list|,
name|String
name|trackerName
parameter_list|,
name|int
name|httpPort
parameter_list|,
name|int
name|shufflePort
parameter_list|,
name|String
name|locality
parameter_list|,
name|String
name|avataar
parameter_list|)
block|{
name|this
argument_list|(
name|attemptId
argument_list|,
name|taskType
argument_list|,
name|startTime
argument_list|,
name|trackerName
argument_list|,
name|httpPort
argument_list|,
name|shufflePort
argument_list|,
name|ContainerId
operator|.
name|fromString
argument_list|(
literal|"container_-1_-1_-1_-1"
argument_list|)
argument_list|,
name|locality
argument_list|,
name|avataar
argument_list|)
expr_stmt|;
block|}
DECL|method|TaskAttemptStartedEvent ()
name|TaskAttemptStartedEvent
parameter_list|()
block|{}
DECL|method|getDatum ()
specifier|public
name|Object
name|getDatum
parameter_list|()
block|{
return|return
name|datum
return|;
block|}
DECL|method|setDatum (Object datum)
specifier|public
name|void
name|setDatum
parameter_list|(
name|Object
name|datum
parameter_list|)
block|{
name|this
operator|.
name|datum
operator|=
operator|(
name|TaskAttemptStarted
operator|)
name|datum
expr_stmt|;
block|}
comment|/** Get the task id */
DECL|method|getTaskId ()
specifier|public
name|TaskID
name|getTaskId
parameter_list|()
block|{
return|return
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
return|;
block|}
comment|/** Get the tracker name */
DECL|method|getTrackerName ()
specifier|public
name|String
name|getTrackerName
parameter_list|()
block|{
return|return
name|datum
operator|.
name|getTrackerName
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Get the start time */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|datum
operator|.
name|getStartTime
argument_list|()
return|;
block|}
comment|/** Get the task type */
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
name|datum
operator|.
name|getTaskType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** Get the HTTP port */
DECL|method|getHttpPort ()
specifier|public
name|int
name|getHttpPort
parameter_list|()
block|{
return|return
name|datum
operator|.
name|getHttpPort
argument_list|()
return|;
block|}
comment|/** Get the shuffle port */
DECL|method|getShufflePort ()
specifier|public
name|int
name|getShufflePort
parameter_list|()
block|{
return|return
name|datum
operator|.
name|getShufflePort
argument_list|()
return|;
block|}
comment|/** Get the attempt id */
DECL|method|getTaskAttemptId ()
specifier|public
name|TaskAttemptID
name|getTaskAttemptId
parameter_list|()
block|{
return|return
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
return|;
block|}
comment|/** Get the event type */
DECL|method|getEventType ()
specifier|public
name|EventType
name|getEventType
parameter_list|()
block|{
comment|// Note that the task type can be setup/map/reduce/cleanup but the
comment|// attempt-type can only be map/reduce.
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
name|EventType
operator|.
name|MAP_ATTEMPT_STARTED
else|:
name|EventType
operator|.
name|REDUCE_ATTEMPT_STARTED
return|;
block|}
comment|/** Get the ContainerId */
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|ContainerId
operator|.
name|fromString
argument_list|(
name|datum
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** Get the locality */
DECL|method|getLocality ()
specifier|public
name|String
name|getLocality
parameter_list|()
block|{
if|if
condition|(
name|datum
operator|.
name|getLocality
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|datum
operator|.
name|getLocality
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Get the avataar */
DECL|method|getAvataar ()
specifier|public
name|String
name|getAvataar
parameter_list|()
block|{
if|if
condition|(
name|datum
operator|.
name|getAvataar
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|datum
operator|.
name|getAvataar
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
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
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"START_TIME"
argument_list|,
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"HTTP_PORT"
argument_list|,
name|getHttpPort
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"TRACKER_NAME"
argument_list|,
name|getTrackerName
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"SHUFFLE_PORT"
argument_list|,
name|getShufflePort
argument_list|()
argument_list|)
expr_stmt|;
name|tEvent
operator|.
name|addInfo
argument_list|(
literal|"CONTAINER_ID"
argument_list|,
name|getContainerId
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|getContainerId
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
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

