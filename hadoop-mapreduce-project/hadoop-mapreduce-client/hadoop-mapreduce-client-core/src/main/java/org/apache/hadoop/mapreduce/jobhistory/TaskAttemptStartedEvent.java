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
name|io
operator|.
name|IOException
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
name|avro
operator|.
name|util
operator|.
name|Utf8
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
comment|/**    * Create an event to record the start of an attempt    * @param attemptId Id of the attempt    * @param taskType Type of task    * @param startTime Start time of the attempt    * @param trackerName Name of the Task Tracker where attempt is running    * @param httpPort The port number of the tracker    * @param shufflePort The shuffle port number of the container    */
DECL|method|TaskAttemptStartedEvent ( TaskAttemptID attemptId, TaskType taskType, long startTime, String trackerName, int httpPort, int shufflePort)
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
parameter_list|)
block|{
name|datum
operator|.
name|attemptId
operator|=
operator|new
name|Utf8
argument_list|(
name|attemptId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|datum
operator|.
name|taskid
operator|=
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
expr_stmt|;
name|datum
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|datum
operator|.
name|taskType
operator|=
operator|new
name|Utf8
argument_list|(
name|taskType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|datum
operator|.
name|trackerName
operator|=
operator|new
name|Utf8
argument_list|(
name|trackerName
argument_list|)
expr_stmt|;
name|datum
operator|.
name|httpPort
operator|=
name|httpPort
expr_stmt|;
name|datum
operator|.
name|shufflePort
operator|=
name|shufflePort
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
name|taskid
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
name|trackerName
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
name|startTime
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
name|taskType
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
name|httpPort
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
name|shufflePort
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
name|attemptId
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
block|}
end_class

end_unit

