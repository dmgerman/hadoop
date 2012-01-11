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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|Phase
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
name|TaskAttemptState
import|;
end_import

begin_class
DECL|class|TaskAttemptStatusUpdateEvent
specifier|public
class|class
name|TaskAttemptStatusUpdateEvent
extends|extends
name|TaskAttemptEvent
block|{
DECL|field|reportedTaskAttemptStatus
specifier|private
name|TaskAttemptStatus
name|reportedTaskAttemptStatus
decl_stmt|;
DECL|method|TaskAttemptStatusUpdateEvent (TaskAttemptId id, TaskAttemptStatus taskAttemptStatus)
specifier|public
name|TaskAttemptStatusUpdateEvent
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|,
name|TaskAttemptStatus
name|taskAttemptStatus
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_UPDATE
argument_list|)
expr_stmt|;
name|this
operator|.
name|reportedTaskAttemptStatus
operator|=
name|taskAttemptStatus
expr_stmt|;
block|}
DECL|method|getReportedTaskAttemptStatus ()
specifier|public
name|TaskAttemptStatus
name|getReportedTaskAttemptStatus
parameter_list|()
block|{
return|return
name|reportedTaskAttemptStatus
return|;
block|}
comment|/**    * The internal TaskAttemptStatus object corresponding to remote Task status.    *     */
DECL|class|TaskAttemptStatus
specifier|public
specifier|static
class|class
name|TaskAttemptStatus
block|{
DECL|field|id
specifier|public
name|TaskAttemptId
name|id
decl_stmt|;
DECL|field|progress
specifier|public
name|float
name|progress
decl_stmt|;
DECL|field|counters
specifier|public
name|Counters
name|counters
decl_stmt|;
DECL|field|stateString
specifier|public
name|String
name|stateString
decl_stmt|;
DECL|field|phase
specifier|public
name|Phase
name|phase
decl_stmt|;
DECL|field|outputSize
specifier|public
name|long
name|outputSize
decl_stmt|;
DECL|field|fetchFailedMaps
specifier|public
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|fetchFailedMaps
decl_stmt|;
DECL|field|mapFinishTime
specifier|public
name|long
name|mapFinishTime
decl_stmt|;
DECL|field|shuffleFinishTime
specifier|public
name|long
name|shuffleFinishTime
decl_stmt|;
DECL|field|sortFinishTime
specifier|public
name|long
name|sortFinishTime
decl_stmt|;
DECL|field|taskState
specifier|public
name|TaskAttemptState
name|taskState
decl_stmt|;
block|}
block|}
end_class

end_unit

