begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.speculate
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
name|speculate
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
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptStatusUpdateEvent
operator|.
name|TaskAttemptStatus
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
name|event
operator|.
name|EventHandler
import|;
end_import

begin_comment
comment|/**  * Speculator component. Task Attempts' status updates are sent to this  * component. Concrete implementation runs the speculative algorithm and  * sends the TaskEventType.T_ADD_ATTEMPT.  *  * An implementation also has to arrange for the jobs to be scanned from  * time to time, to launch the speculations.  */
end_comment

begin_interface
DECL|interface|Speculator
specifier|public
interface|interface
name|Speculator
extends|extends
name|EventHandler
argument_list|<
name|SpeculatorEvent
argument_list|>
block|{
DECL|enum|EventType
enum|enum
name|EventType
block|{
DECL|enumConstant|ATTEMPT_STATUS_UPDATE
name|ATTEMPT_STATUS_UPDATE
block|,
DECL|enumConstant|ATTEMPT_START
name|ATTEMPT_START
block|,
DECL|enumConstant|TASK_CONTAINER_NEED_UPDATE
name|TASK_CONTAINER_NEED_UPDATE
block|,
DECL|enumConstant|JOB_CREATE
name|JOB_CREATE
block|}
comment|// This will be implemented if we go to a model where the events are
comment|//  processed within the TaskAttempts' state transitions' code.
DECL|method|handleAttempt (TaskAttemptStatus status)
specifier|public
name|void
name|handleAttempt
parameter_list|(
name|TaskAttemptStatus
name|status
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

