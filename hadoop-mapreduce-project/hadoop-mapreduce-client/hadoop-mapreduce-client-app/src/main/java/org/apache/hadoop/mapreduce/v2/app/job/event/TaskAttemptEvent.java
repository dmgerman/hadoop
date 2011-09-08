begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job.event
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
operator|.
name|event
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
name|yarn
operator|.
name|event
operator|.
name|AbstractEvent
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

begin_comment
comment|/**  * This class encapsulates task attempt related events.  *  */
end_comment

begin_class
DECL|class|TaskAttemptEvent
specifier|public
class|class
name|TaskAttemptEvent
extends|extends
name|AbstractEvent
argument_list|<
name|TaskAttemptEventType
argument_list|>
block|{
DECL|field|attemptID
specifier|private
name|TaskAttemptId
name|attemptID
decl_stmt|;
comment|/**    * Create a new TaskAttemptEvent.    * @param id the id of the task attempt    * @param type the type of event that happened.    */
DECL|method|TaskAttemptEvent (TaskAttemptId id, TaskAttemptEventType type)
specifier|public
name|TaskAttemptEvent
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|,
name|TaskAttemptEventType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|attemptID
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getTaskAttemptID ()
specifier|public
name|TaskAttemptId
name|getTaskAttemptID
parameter_list|()
block|{
return|return
name|attemptID
return|;
block|}
block|}
end_class

end_unit

