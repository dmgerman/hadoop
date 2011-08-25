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

begin_interface
DECL|interface|TaskAttemptReport
specifier|public
interface|interface
name|TaskAttemptReport
block|{
DECL|method|getTaskAttemptId ()
specifier|public
specifier|abstract
name|TaskAttemptId
name|getTaskAttemptId
parameter_list|()
function_decl|;
DECL|method|getTaskAttemptState ()
specifier|public
specifier|abstract
name|TaskAttemptState
name|getTaskAttemptState
parameter_list|()
function_decl|;
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
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
DECL|method|getDiagnosticInfo ()
specifier|public
specifier|abstract
name|String
name|getDiagnosticInfo
parameter_list|()
function_decl|;
DECL|method|getStateString ()
specifier|public
specifier|abstract
name|String
name|getStateString
parameter_list|()
function_decl|;
DECL|method|getPhase ()
specifier|public
specifier|abstract
name|Phase
name|getPhase
parameter_list|()
function_decl|;
DECL|method|setTaskAttemptId (TaskAttemptId taskAttemptId)
specifier|public
specifier|abstract
name|void
name|setTaskAttemptId
parameter_list|(
name|TaskAttemptId
name|taskAttemptId
parameter_list|)
function_decl|;
DECL|method|setTaskAttemptState (TaskAttemptState taskAttemptState)
specifier|public
specifier|abstract
name|void
name|setTaskAttemptState
parameter_list|(
name|TaskAttemptState
name|taskAttemptState
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
DECL|method|setDiagnosticInfo (String diagnosticInfo)
specifier|public
specifier|abstract
name|void
name|setDiagnosticInfo
parameter_list|(
name|String
name|diagnosticInfo
parameter_list|)
function_decl|;
DECL|method|setStateString (String stateString)
specifier|public
specifier|abstract
name|void
name|setStateString
parameter_list|(
name|String
name|stateString
parameter_list|)
function_decl|;
DECL|method|setPhase (Phase phase)
specifier|public
specifier|abstract
name|void
name|setPhase
parameter_list|(
name|Phase
name|phase
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

