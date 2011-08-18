begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|JobACL
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
name|JobId
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
name|JobReport
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
name|JobState
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
name|TaskAttemptCompletionEvent
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
name|TaskId
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * Main interface to interact with the job. Provides only getters.   */
end_comment

begin_interface
DECL|interface|Job
specifier|public
interface|interface
name|Job
block|{
DECL|method|getID ()
name|JobId
name|getID
parameter_list|()
function_decl|;
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getState ()
name|JobState
name|getState
parameter_list|()
function_decl|;
DECL|method|getReport ()
name|JobReport
name|getReport
parameter_list|()
function_decl|;
DECL|method|getCounters ()
name|Counters
name|getCounters
parameter_list|()
function_decl|;
DECL|method|getTasks ()
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|getTasks
parameter_list|()
function_decl|;
DECL|method|getTasks (TaskType taskType)
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|getTasks
parameter_list|(
name|TaskType
name|taskType
parameter_list|)
function_decl|;
DECL|method|getTask (TaskId taskID)
name|Task
name|getTask
parameter_list|(
name|TaskId
name|taskID
parameter_list|)
function_decl|;
DECL|method|getDiagnostics ()
name|List
argument_list|<
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|getTotalMaps ()
name|int
name|getTotalMaps
parameter_list|()
function_decl|;
DECL|method|getTotalReduces ()
name|int
name|getTotalReduces
parameter_list|()
function_decl|;
DECL|method|getCompletedMaps ()
name|int
name|getCompletedMaps
parameter_list|()
function_decl|;
DECL|method|getCompletedReduces ()
name|int
name|getCompletedReduces
parameter_list|()
function_decl|;
DECL|method|isUber ()
name|boolean
name|isUber
parameter_list|()
function_decl|;
name|TaskAttemptCompletionEvent
index|[]
DECL|method|getTaskAttemptCompletionEvents (int fromEventId, int maxEvents)
name|getTaskAttemptCompletionEvents
parameter_list|(
name|int
name|fromEventId
parameter_list|,
name|int
name|maxEvents
parameter_list|)
function_decl|;
DECL|method|checkAccess (UserGroupInformation callerUGI, JobACL jobOperation)
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|JobACL
name|jobOperation
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

