begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|conf
operator|.
name|Configuration
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
name|records
operator|.
name|AMInfo
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
name|TaskAttemptReport
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|client
operator|.
name|ClientService
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
name|app
operator|.
name|client
operator|.
name|MRClientService
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
name|app
operator|.
name|job
operator|.
name|Job
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
name|app
operator|.
name|job
operator|.
name|Task
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
name|app
operator|.
name|job
operator|.
name|TaskAttempt
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
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptDiagnosticsUpdateEvent
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
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEvent
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
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEventType
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
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptStatusUpdateEvent
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
name|ipc
operator|.
name|YarnRPC
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestMRClientService
specifier|public
class|class
name|TestMRClientService
block|{
DECL|field|recordFactory
specifier|private
specifier|static
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
annotation|@
name|Test
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|MRAppWithClientService
name|app
init|=
operator|new
name|MRAppWithClientService
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num tasks not correct"
argument_list|,
literal|1
argument_list|,
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Task
argument_list|>
name|it
init|=
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Task
name|task
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|task
argument_list|,
name|TaskState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|TaskAttempt
name|attempt
init|=
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|attempt
argument_list|,
name|TaskAttemptState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// send the diagnostic
name|String
name|diagnostic1
init|=
literal|"Diagnostic1"
decl_stmt|;
name|String
name|diagnostic2
init|=
literal|"Diagnostic2"
decl_stmt|;
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptDiagnosticsUpdateEvent
argument_list|(
name|attempt
operator|.
name|getID
argument_list|()
argument_list|,
name|diagnostic1
argument_list|)
argument_list|)
expr_stmt|;
comment|// send the status update
name|TaskAttemptStatus
name|taskAttemptStatus
init|=
operator|new
name|TaskAttemptStatus
argument_list|()
decl_stmt|;
name|taskAttemptStatus
operator|.
name|id
operator|=
name|attempt
operator|.
name|getID
argument_list|()
expr_stmt|;
name|taskAttemptStatus
operator|.
name|progress
operator|=
literal|0.5f
expr_stmt|;
name|taskAttemptStatus
operator|.
name|stateString
operator|=
literal|"RUNNING"
expr_stmt|;
name|taskAttemptStatus
operator|.
name|taskState
operator|=
name|TaskAttemptState
operator|.
name|RUNNING
expr_stmt|;
name|taskAttemptStatus
operator|.
name|phase
operator|=
name|Phase
operator|.
name|MAP
expr_stmt|;
name|taskAttemptStatus
operator|.
name|outputSize
operator|=
literal|3
expr_stmt|;
comment|// send the status update
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptStatusUpdateEvent
argument_list|(
name|attempt
operator|.
name|getID
argument_list|()
argument_list|,
name|taskAttemptStatus
argument_list|)
argument_list|)
expr_stmt|;
comment|//verify that all object are fully populated by invoking RPCs.
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|MRClientProtocol
name|proxy
init|=
operator|(
name|MRClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|MRClientProtocol
operator|.
name|class
argument_list|,
name|app
operator|.
name|clientService
operator|.
name|getBindAddress
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|GetCountersRequest
name|gcRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetCountersRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gcRequest
operator|.
name|setJobId
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Counters is null"
argument_list|,
name|proxy
operator|.
name|getCounters
argument_list|(
name|gcRequest
argument_list|)
operator|.
name|getCounters
argument_list|()
argument_list|)
expr_stmt|;
name|GetJobReportRequest
name|gjrRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetJobReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gjrRequest
operator|.
name|setJobId
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|JobReport
name|jr
init|=
name|proxy
operator|.
name|getJobReport
argument_list|(
name|gjrRequest
argument_list|)
operator|.
name|getJobReport
argument_list|()
decl_stmt|;
name|verifyJobReport
argument_list|(
name|jr
argument_list|)
expr_stmt|;
name|GetTaskAttemptCompletionEventsRequest
name|gtaceRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskAttemptCompletionEventsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gtaceRequest
operator|.
name|setJobId
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|gtaceRequest
operator|.
name|setFromEventId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|gtaceRequest
operator|.
name|setMaxEvents
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"TaskCompletionEvents is null"
argument_list|,
name|proxy
operator|.
name|getTaskAttemptCompletionEvents
argument_list|(
name|gtaceRequest
argument_list|)
operator|.
name|getCompletionEventList
argument_list|()
argument_list|)
expr_stmt|;
name|GetDiagnosticsRequest
name|gdRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetDiagnosticsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gdRequest
operator|.
name|setTaskAttemptId
argument_list|(
name|attempt
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Diagnostics is null"
argument_list|,
name|proxy
operator|.
name|getDiagnostics
argument_list|(
name|gdRequest
argument_list|)
operator|.
name|getDiagnosticsList
argument_list|()
argument_list|)
expr_stmt|;
name|GetTaskAttemptReportRequest
name|gtarRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskAttemptReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gtarRequest
operator|.
name|setTaskAttemptId
argument_list|(
name|attempt
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|TaskAttemptReport
name|tar
init|=
name|proxy
operator|.
name|getTaskAttemptReport
argument_list|(
name|gtarRequest
argument_list|)
operator|.
name|getTaskAttemptReport
argument_list|()
decl_stmt|;
name|verifyTaskAttemptReport
argument_list|(
name|tar
argument_list|)
expr_stmt|;
name|GetTaskReportRequest
name|gtrRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gtrRequest
operator|.
name|setTaskId
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"TaskReport is null"
argument_list|,
name|proxy
operator|.
name|getTaskReport
argument_list|(
name|gtrRequest
argument_list|)
operator|.
name|getTaskReport
argument_list|()
argument_list|)
expr_stmt|;
name|GetTaskReportsRequest
name|gtreportsRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskReportsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gtreportsRequest
operator|.
name|setJobId
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|gtreportsRequest
operator|.
name|setTaskType
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"TaskReports for map is null"
argument_list|,
name|proxy
operator|.
name|getTaskReports
argument_list|(
name|gtreportsRequest
argument_list|)
operator|.
name|getTaskReportList
argument_list|()
argument_list|)
expr_stmt|;
name|gtreportsRequest
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskReportsRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|gtreportsRequest
operator|.
name|setJobId
argument_list|(
name|job
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|gtreportsRequest
operator|.
name|setTaskType
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"TaskReports for reduce is null"
argument_list|,
name|proxy
operator|.
name|getTaskReports
argument_list|(
name|gtreportsRequest
argument_list|)
operator|.
name|getTaskReportList
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|diag
init|=
name|proxy
operator|.
name|getDiagnostics
argument_list|(
name|gdRequest
argument_list|)
operator|.
name|getDiagnosticsList
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num diagnostics not correct"
argument_list|,
literal|1
argument_list|,
name|diag
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Diag 1 not correct"
argument_list|,
name|diagnostic1
argument_list|,
name|diag
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|TaskReport
name|taskReport
init|=
name|proxy
operator|.
name|getTaskReport
argument_list|(
name|gtrRequest
argument_list|)
operator|.
name|getTaskReport
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num diagnostics not correct"
argument_list|,
literal|1
argument_list|,
name|taskReport
operator|.
name|getDiagnosticsCount
argument_list|()
argument_list|)
expr_stmt|;
comment|//send the done signal to the task
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_DONE
argument_list|)
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyJobReport (JobReport jr)
specifier|private
name|void
name|verifyJobReport
parameter_list|(
name|JobReport
name|jr
parameter_list|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"JobReport is null"
argument_list|,
name|jr
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AMInfo
argument_list|>
name|amInfos
init|=
name|jr
operator|.
name|getAMInfos
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|amInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JobState
operator|.
name|RUNNING
argument_list|,
name|jr
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|AMInfo
name|amInfo
init|=
name|amInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_HOST
argument_list|,
name|amInfo
operator|.
name|getNodeManagerHost
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_PORT
argument_list|,
name|amInfo
operator|.
name|getNodeManagerPort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_HTTP_PORT
argument_list|,
name|amInfo
operator|.
name|getNodeManagerHttpPort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|amInfo
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|amInfo
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|amInfo
operator|.
name|getStartTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyTaskAttemptReport (TaskAttemptReport tar)
specifier|private
name|void
name|verifyTaskAttemptReport
parameter_list|(
name|TaskAttemptReport
name|tar
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TaskAttemptState
operator|.
name|RUNNING
argument_list|,
name|tar
operator|.
name|getTaskAttemptState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"TaskAttemptReport is null"
argument_list|,
name|tar
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_HOST
argument_list|,
name|tar
operator|.
name|getNodeManagerHost
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_PORT
argument_list|,
name|tar
operator|.
name|getNodeManagerPort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_HTTP_PORT
argument_list|,
name|tar
operator|.
name|getNodeManagerHttpPort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tar
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|MRAppWithClientService
class|class
name|MRAppWithClientService
extends|extends
name|MRApp
block|{
DECL|field|clientService
name|MRClientService
name|clientService
init|=
literal|null
decl_stmt|;
DECL|method|MRAppWithClientService (int maps, int reduces, boolean autoComplete)
name|MRAppWithClientService
parameter_list|(
name|int
name|maps
parameter_list|,
name|int
name|reduces
parameter_list|,
name|boolean
name|autoComplete
parameter_list|)
block|{
name|super
argument_list|(
name|maps
argument_list|,
name|reduces
argument_list|,
name|autoComplete
argument_list|,
literal|"MRAppWithClientService"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createClientService (AppContext context)
specifier|protected
name|ClientService
name|createClientService
parameter_list|(
name|AppContext
name|context
parameter_list|)
block|{
name|clientService
operator|=
operator|new
name|MRClientService
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|clientService
return|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|TestMRClientService
name|t
init|=
operator|new
name|TestMRClientService
argument_list|()
decl_stmt|;
name|t
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

