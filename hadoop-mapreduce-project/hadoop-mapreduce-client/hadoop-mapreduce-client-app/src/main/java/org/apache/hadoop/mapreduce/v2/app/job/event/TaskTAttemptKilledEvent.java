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
comment|/**  * Task Attempt killed event.  */
end_comment

begin_class
DECL|class|TaskTAttemptKilledEvent
specifier|public
class|class
name|TaskTAttemptKilledEvent
extends|extends
name|TaskTAttemptEvent
block|{
comment|// Next map attempt will be rescheduled(i.e. updated in ask with
comment|// higher priority equivalent to that of a fast fail map)
DECL|field|rescheduleAttempt
specifier|private
specifier|final
name|boolean
name|rescheduleAttempt
decl_stmt|;
DECL|method|TaskTAttemptKilledEvent (TaskAttemptId id, boolean rescheduleAttempt)
specifier|public
name|TaskTAttemptKilledEvent
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|,
name|boolean
name|rescheduleAttempt
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|TaskEventType
operator|.
name|T_ATTEMPT_KILLED
argument_list|)
expr_stmt|;
name|this
operator|.
name|rescheduleAttempt
operator|=
name|rescheduleAttempt
expr_stmt|;
block|}
DECL|method|getRescheduleAttempt ()
specifier|public
name|boolean
name|getRescheduleAttempt
parameter_list|()
block|{
return|return
name|rescheduleAttempt
return|;
block|}
block|}
end_class

end_unit

