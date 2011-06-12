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
name|TaskID
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
comment|/**  * Event to record updates to a task  *  */
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
DECL|class|TaskUpdatedEvent
specifier|public
class|class
name|TaskUpdatedEvent
implements|implements
name|HistoryEvent
block|{
DECL|field|datum
specifier|private
name|TaskUpdated
name|datum
init|=
operator|new
name|TaskUpdated
argument_list|()
decl_stmt|;
comment|/**    * Create an event to record task updates    * @param id Id of the task    * @param finishTime Finish time of the task    */
DECL|method|TaskUpdatedEvent (TaskID id, long finishTime)
specifier|public
name|TaskUpdatedEvent
parameter_list|(
name|TaskID
name|id
parameter_list|,
name|long
name|finishTime
parameter_list|)
block|{
name|datum
operator|.
name|taskid
operator|=
operator|new
name|Utf8
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|datum
operator|.
name|finishTime
operator|=
name|finishTime
expr_stmt|;
block|}
DECL|method|TaskUpdatedEvent ()
name|TaskUpdatedEvent
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
name|TaskUpdated
operator|)
name|datum
expr_stmt|;
block|}
comment|/** Get the task ID */
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
comment|/** Get the task finish time */
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|datum
operator|.
name|finishTime
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
name|TASK_UPDATED
return|;
block|}
block|}
end_class

end_unit

