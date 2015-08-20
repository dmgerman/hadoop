begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.records
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
name|api
operator|.
name|records
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

begin_interface
DECL|interface|TaskReport
specifier|public
interface|interface
name|TaskReport
block|{
DECL|method|getTaskId ()
specifier|public
specifier|abstract
name|TaskId
name|getTaskId
parameter_list|()
function_decl|;
DECL|method|getTaskState ()
specifier|public
specifier|abstract
name|TaskState
name|getTaskState
parameter_list|()
function_decl|;
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
parameter_list|()
function_decl|;
DECL|method|getStatus ()
specifier|public
specifier|abstract
name|String
name|getStatus
parameter_list|()
function_decl|;
DECL|method|getStartTime ()
specifier|public
specifier|abstract
name|long
name|getStartTime
parameter_list|()
function_decl|;
DECL|method|getFinishTime ()
specifier|public
specifier|abstract
name|long
name|getFinishTime
parameter_list|()
function_decl|;
DECL|method|getCounters ()
specifier|public
specifier|abstract
name|Counters
name|getCounters
parameter_list|()
function_decl|;
DECL|method|getRawCounters ()
specifier|public
specifier|abstract
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Counters
name|getRawCounters
parameter_list|()
function_decl|;
DECL|method|getRunningAttemptsList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|getRunningAttemptsList
parameter_list|()
function_decl|;
DECL|method|getRunningAttempt (int index)
specifier|public
specifier|abstract
name|TaskAttemptId
name|getRunningAttempt
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getRunningAttemptsCount ()
specifier|public
specifier|abstract
name|int
name|getRunningAttemptsCount
parameter_list|()
function_decl|;
DECL|method|getSuccessfulAttempt ()
specifier|public
specifier|abstract
name|TaskAttemptId
name|getSuccessfulAttempt
parameter_list|()
function_decl|;
DECL|method|getDiagnosticsList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|getDiagnosticsList
parameter_list|()
function_decl|;
DECL|method|getDiagnostics (int index)
specifier|public
specifier|abstract
name|String
name|getDiagnostics
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getDiagnosticsCount ()
specifier|public
specifier|abstract
name|int
name|getDiagnosticsCount
parameter_list|()
function_decl|;
DECL|method|setTaskId (TaskId taskId)
specifier|public
specifier|abstract
name|void
name|setTaskId
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
function_decl|;
DECL|method|setTaskState (TaskState taskState)
specifier|public
specifier|abstract
name|void
name|setTaskState
parameter_list|(
name|TaskState
name|taskState
parameter_list|)
function_decl|;
DECL|method|setProgress (float progress)
specifier|public
specifier|abstract
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
function_decl|;
DECL|method|setStatus (String status)
specifier|public
specifier|abstract
name|void
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
function_decl|;
DECL|method|setStartTime (long startTime)
specifier|public
specifier|abstract
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
function_decl|;
DECL|method|setFinishTime (long finishTime)
specifier|public
specifier|abstract
name|void
name|setFinishTime
parameter_list|(
name|long
name|finishTime
parameter_list|)
function_decl|;
DECL|method|setCounters (Counters counters)
specifier|public
specifier|abstract
name|void
name|setCounters
parameter_list|(
name|Counters
name|counters
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
DECL|method|setRawCounters (org.apache.hadoop.mapreduce.Counters rCounters)
name|setRawCounters
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Counters
name|rCounters
parameter_list|)
function_decl|;
DECL|method|addAllRunningAttempts (List<TaskAttemptId> taskAttempts)
specifier|public
specifier|abstract
name|void
name|addAllRunningAttempts
parameter_list|(
name|List
argument_list|<
name|TaskAttemptId
argument_list|>
name|taskAttempts
parameter_list|)
function_decl|;
DECL|method|addRunningAttempt (TaskAttemptId taskAttempt)
specifier|public
specifier|abstract
name|void
name|addRunningAttempt
parameter_list|(
name|TaskAttemptId
name|taskAttempt
parameter_list|)
function_decl|;
DECL|method|removeRunningAttempt (int index)
specifier|public
specifier|abstract
name|void
name|removeRunningAttempt
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearRunningAttempts ()
specifier|public
specifier|abstract
name|void
name|clearRunningAttempts
parameter_list|()
function_decl|;
DECL|method|setSuccessfulAttempt (TaskAttemptId taskAttempt)
specifier|public
specifier|abstract
name|void
name|setSuccessfulAttempt
parameter_list|(
name|TaskAttemptId
name|taskAttempt
parameter_list|)
function_decl|;
DECL|method|addAllDiagnostics (List<String> diagnostics)
specifier|public
specifier|abstract
name|void
name|addAllDiagnostics
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
parameter_list|)
function_decl|;
DECL|method|addDiagnostics (String diagnostics)
specifier|public
specifier|abstract
name|void
name|addDiagnostics
parameter_list|(
name|String
name|diagnostics
parameter_list|)
function_decl|;
DECL|method|removeDiagnostics (int index)
specifier|public
specifier|abstract
name|void
name|removeDiagnostics
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearDiagnostics ()
specifier|public
specifier|abstract
name|void
name|clearDiagnostics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

