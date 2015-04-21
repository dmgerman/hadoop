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
name|timelineservice
operator|.
name|TimelineEvent
import|;
end_import

begin_comment
comment|/**  * Event to record the start of a task  *  */
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
DECL|class|TaskStartedEvent
specifier|public
class|class
name|TaskStartedEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|TaskStarted
name|datum
init|=
operator|new
name|TaskStarted
argument_list|()
decl_stmt|;
comment|/**    * Create an event to record start of a task    * @param id Task Id    * @param startTime Start time of the task    * @param taskType Type of the task    * @param splitLocations Split locations, applicable for map tasks    */
DECL|method|TaskStartedEvent (TaskID id, long startTime, TaskType taskType, String splitLocations)
specifier|public
name|TaskStartedEvent
parameter_list|(
name|TaskID
name|id
parameter_list|,
name|long
name|startTime
parameter_list|,
name|TaskType
name|taskType
parameter_list|,
name|String
name|splitLocations
parameter_list|)
block|{
name|datum
operator|.
name|setTaskid
argument_list|(
operator|new
name|Utf8
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|datum
operator|.
name|setSplitLocations
argument_list|(
operator|new
name|Utf8
argument_list|(
name|splitLocations
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
block|}
DECL|method|TaskStartedEvent ()
name|TaskStartedEvent
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
name|TaskStarted
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
comment|/** Get the split locations, applicable for map tasks */
DECL|method|getSplitLocations ()
specifier|public
name|String
name|getSplitLocations
parameter_list|()
block|{
return|return
name|datum
operator|.
name|getSplitLocations
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Get the start time of the task */
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
comment|/** Get the event type */
DECL|method|getEventType ()
specifier|public
name|EventType
name|getEventType
parameter_list|()
block|{
return|return
name|EventType
operator|.
name|TASK_STARTED
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
literal|"SPLIT_LOCATIONS"
argument_list|,
name|getSplitLocations
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|tEvent
return|;
block|}
block|}
end_class

end_unit

