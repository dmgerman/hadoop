begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|NotImplementedException
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
name|MRClientProtocol
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
name|protocolrecords
operator|.
name|CancelDelegationTokenRequest
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
name|protocolrecords
operator|.
name|CancelDelegationTokenResponse
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
name|protocolrecords
operator|.
name|FailTaskAttemptRequest
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
name|protocolrecords
operator|.
name|FailTaskAttemptResponse
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
name|protocolrecords
operator|.
name|GetCountersRequest
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
name|protocolrecords
operator|.
name|GetCountersResponse
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
name|protocolrecords
operator|.
name|GetDelegationTokenRequest
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
name|protocolrecords
operator|.
name|GetDelegationTokenResponse
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
name|protocolrecords
operator|.
name|GetDiagnosticsRequest
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
name|protocolrecords
operator|.
name|GetDiagnosticsResponse
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
name|protocolrecords
operator|.
name|GetJobReportRequest
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
name|protocolrecords
operator|.
name|GetJobReportResponse
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
name|protocolrecords
operator|.
name|GetTaskAttemptCompletionEventsRequest
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
name|protocolrecords
operator|.
name|GetTaskAttemptCompletionEventsResponse
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
name|protocolrecords
operator|.
name|GetTaskAttemptReportRequest
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
name|protocolrecords
operator|.
name|GetTaskAttemptReportResponse
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
name|protocolrecords
operator|.
name|GetTaskReportRequest
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
name|protocolrecords
operator|.
name|GetTaskReportResponse
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
name|protocolrecords
operator|.
name|GetTaskReportsRequest
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
name|protocolrecords
operator|.
name|GetTaskReportsResponse
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
name|protocolrecords
operator|.
name|KillJobRequest
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
name|protocolrecords
operator|.
name|KillJobResponse
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
name|protocolrecords
operator|.
name|KillTaskAttemptRequest
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
name|protocolrecords
operator|.
name|KillTaskAttemptResponse
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
name|protocolrecords
operator|.
name|KillTaskRequest
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
name|protocolrecords
operator|.
name|KillTaskResponse
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
name|protocolrecords
operator|.
name|RenewDelegationTokenRequest
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
name|protocolrecords
operator|.
name|RenewDelegationTokenResponse
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
name|CounterGroup
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
name|TaskReport
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
name|TaskState
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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|ApplicationReport
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
name|FinalApplicationStatus
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
name|YarnApplicationState
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
name|conf
operator|.
name|YarnConfiguration
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
import|;
end_import

begin_class
DECL|class|NotRunningJob
specifier|public
class|class
name|NotRunningJob
implements|implements
name|MRClientProtocol
block|{
DECL|field|recordFactory
specifier|private
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|jobState
specifier|private
specifier|final
name|JobState
name|jobState
decl_stmt|;
DECL|field|applicationReport
specifier|private
specifier|final
name|ApplicationReport
name|applicationReport
decl_stmt|;
DECL|method|getUnknownApplicationReport ()
specifier|private
name|ApplicationReport
name|getUnknownApplicationReport
parameter_list|()
block|{
name|ApplicationId
name|unknownAppId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|unknownAttemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Setting AppState to NEW and finalStatus to UNDEFINED as they are never
comment|// used for a non running job
return|return
name|ApplicationReport
operator|.
name|newInstance
argument_list|(
name|unknownAppId
argument_list|,
name|unknownAttemptId
argument_list|,
literal|"N/A"
argument_list|,
literal|"N/A"
argument_list|,
literal|"N/A"
argument_list|,
literal|"N/A"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|YarnApplicationState
operator|.
name|NEW
argument_list|,
literal|"N/A"
argument_list|,
literal|"N/A"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|FinalApplicationStatus
operator|.
name|UNDEFINED
argument_list|,
literal|null
argument_list|,
literal|"N/A"
argument_list|,
literal|0.0f
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_APPLICATION_TYPE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|NotRunningJob (ApplicationReport applicationReport, JobState jobState)
name|NotRunningJob
parameter_list|(
name|ApplicationReport
name|applicationReport
parameter_list|,
name|JobState
name|jobState
parameter_list|)
block|{
name|this
operator|.
name|applicationReport
operator|=
operator|(
name|applicationReport
operator|==
literal|null
operator|)
condition|?
name|getUnknownApplicationReport
argument_list|()
else|:
name|applicationReport
expr_stmt|;
name|this
operator|.
name|jobState
operator|=
name|jobState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|failTaskAttempt ( FailTaskAttemptRequest request)
specifier|public
name|FailTaskAttemptResponse
name|failTaskAttempt
parameter_list|(
name|FailTaskAttemptRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|FailTaskAttemptResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|FailTaskAttemptResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|getCounters (GetCountersRequest request)
specifier|public
name|GetCountersResponse
name|getCounters
parameter_list|(
name|GetCountersRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetCountersResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetCountersResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Counters
name|counters
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Counters
operator|.
name|class
argument_list|)
decl_stmt|;
name|counters
operator|.
name|addAllCounterGroups
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CounterGroup
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics (GetDiagnosticsRequest request)
specifier|public
name|GetDiagnosticsResponse
name|getDiagnostics
parameter_list|(
name|GetDiagnosticsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetDiagnosticsResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetDiagnosticsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|resp
operator|.
name|addDiagnostics
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|getJobReport (GetJobReportRequest request)
specifier|public
name|GetJobReportResponse
name|getJobReport
parameter_list|(
name|GetJobReportRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|JobReport
name|jobReport
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|JobReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|jobReport
operator|.
name|setJobId
argument_list|(
name|request
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setJobState
argument_list|(
name|jobState
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setUser
argument_list|(
name|applicationReport
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setStartTime
argument_list|(
name|applicationReport
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setDiagnostics
argument_list|(
name|applicationReport
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setJobName
argument_list|(
name|applicationReport
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setTrackingUrl
argument_list|(
name|applicationReport
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setFinishTime
argument_list|(
name|applicationReport
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|GetJobReportResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetJobReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|resp
operator|.
name|setJobReport
argument_list|(
name|jobReport
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptCompletionEvents ( GetTaskAttemptCompletionEventsRequest request)
specifier|public
name|GetTaskAttemptCompletionEventsResponse
name|getTaskAttemptCompletionEvents
parameter_list|(
name|GetTaskAttemptCompletionEventsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetTaskAttemptCompletionEventsResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskAttemptCompletionEventsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|resp
operator|.
name|addAllCompletionEvents
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|TaskAttemptCompletionEvent
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptReport ( GetTaskAttemptReportRequest request)
specifier|public
name|GetTaskAttemptReportResponse
name|getTaskAttemptReport
parameter_list|(
name|GetTaskAttemptReportRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
comment|//not invoked by anybody
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getTaskReport (GetTaskReportRequest request)
specifier|public
name|GetTaskReportResponse
name|getTaskReport
parameter_list|(
name|GetTaskReportRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetTaskReportResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|TaskReport
name|report
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|TaskReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|report
operator|.
name|setTaskId
argument_list|(
name|request
operator|.
name|getTaskId
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setTaskState
argument_list|(
name|TaskState
operator|.
name|NEW
argument_list|)
expr_stmt|;
name|Counters
name|counters
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Counters
operator|.
name|class
argument_list|)
decl_stmt|;
name|counters
operator|.
name|addAllCounterGroups
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CounterGroup
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setCounters
argument_list|(
name|counters
argument_list|)
expr_stmt|;
name|report
operator|.
name|addAllRunningAttempts
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|TaskAttemptId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskReports (GetTaskReportsRequest request)
specifier|public
name|GetTaskReportsResponse
name|getTaskReports
parameter_list|(
name|GetTaskReportsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|GetTaskReportsResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskReportsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|resp
operator|.
name|addAllTaskReports
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|TaskReport
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|killJob (KillJobRequest request)
specifier|public
name|KillJobResponse
name|killJob
parameter_list|(
name|KillJobRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|KillJobResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillJobResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|killTask (KillTaskRequest request)
specifier|public
name|KillTaskResponse
name|killTask
parameter_list|(
name|KillTaskRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|KillTaskResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillTaskResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|killTaskAttempt ( KillTaskAttemptRequest request)
specifier|public
name|KillTaskAttemptResponse
name|killTaskAttempt
parameter_list|(
name|KillTaskAttemptRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|KillTaskAttemptResponse
name|resp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillTaskAttemptResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|resp
return|;
block|}
annotation|@
name|Override
DECL|method|getDelegationToken ( GetDelegationTokenRequest request)
specifier|public
name|GetDelegationTokenResponse
name|getDelegationToken
parameter_list|(
name|GetDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* Should not be invoked by anyone. */
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|renewDelegationToken ( RenewDelegationTokenRequest request)
specifier|public
name|RenewDelegationTokenResponse
name|renewDelegationToken
parameter_list|(
name|RenewDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* Should not be invoked by anyone. */
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|cancelDelegationToken ( CancelDelegationTokenRequest request)
specifier|public
name|CancelDelegationTokenResponse
name|cancelDelegationToken
parameter_list|(
name|CancelDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* Should not be invoked by anyone. */
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getConnectAddress ()
specifier|public
name|InetSocketAddress
name|getConnectAddress
parameter_list|()
block|{
comment|/* Should not be invoked by anyone.  Normally used to set token service */
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

