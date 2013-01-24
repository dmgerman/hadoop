begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.impl.pb.service
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
name|impl
operator|.
name|pb
operator|.
name|service
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
name|MRClientProtocolPB
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|CancelDelegationTokenRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|CancelDelegationTokenResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|FailTaskAttemptRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|FailTaskAttemptResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetCountersRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetCountersResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetDelegationTokenRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetDelegationTokenResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetDiagnosticsRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetDiagnosticsResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetJobReportRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetJobReportResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetTaskAttemptCompletionEventsRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetTaskAttemptCompletionEventsResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetTaskAttemptReportRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetTaskAttemptReportResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetTaskReportRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetTaskReportResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetTaskReportsRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|GetTaskReportsResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|KillJobRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|KillJobResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|KillTaskAttemptRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|KillTaskAttemptResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|KillTaskRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|KillTaskResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RenewDelegationTokenRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RenewDelegationTokenResponsePBImpl
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|FailTaskAttemptRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|FailTaskAttemptResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetCountersRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetCountersResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetDiagnosticsRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetDiagnosticsResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetJobReportRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetJobReportResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetTaskAttemptCompletionEventsRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetTaskAttemptCompletionEventsResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetTaskAttemptReportRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetTaskAttemptReportResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetTaskReportRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetTaskReportResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetTaskReportsRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|GetTaskReportsResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|KillJobRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|KillJobResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|KillTaskAttemptRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|KillTaskAttemptResponseProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|KillTaskRequestProto
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
name|proto
operator|.
name|MRServiceProtos
operator|.
name|KillTaskResponseProto
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
name|proto
operator|.
name|SecurityProtos
operator|.
name|CancelDelegationTokenRequestProto
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
name|proto
operator|.
name|SecurityProtos
operator|.
name|CancelDelegationTokenResponseProto
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
name|proto
operator|.
name|SecurityProtos
operator|.
name|GetDelegationTokenRequestProto
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
name|proto
operator|.
name|SecurityProtos
operator|.
name|GetDelegationTokenResponseProto
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
name|proto
operator|.
name|SecurityProtos
operator|.
name|RenewDelegationTokenRequestProto
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
name|proto
operator|.
name|SecurityProtos
operator|.
name|RenewDelegationTokenResponseProto
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
name|exceptions
operator|.
name|YarnRemoteException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|RpcController
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
import|;
end_import

begin_class
DECL|class|MRClientProtocolPBServiceImpl
specifier|public
class|class
name|MRClientProtocolPBServiceImpl
implements|implements
name|MRClientProtocolPB
block|{
DECL|field|real
specifier|private
name|MRClientProtocol
name|real
decl_stmt|;
DECL|method|MRClientProtocolPBServiceImpl (MRClientProtocol impl)
specifier|public
name|MRClientProtocolPBServiceImpl
parameter_list|(
name|MRClientProtocol
name|impl
parameter_list|)
block|{
name|this
operator|.
name|real
operator|=
name|impl
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getJobReport (RpcController controller, GetJobReportRequestProto proto)
specifier|public
name|GetJobReportResponseProto
name|getJobReport
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetJobReportRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetJobReportRequestPBImpl
name|request
init|=
operator|new
name|GetJobReportRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetJobReportResponse
name|response
init|=
name|real
operator|.
name|getJobReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetJobReportResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTaskReport (RpcController controller, GetTaskReportRequestProto proto)
specifier|public
name|GetTaskReportResponseProto
name|getTaskReport
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetTaskReportRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetTaskReportRequest
name|request
init|=
operator|new
name|GetTaskReportRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetTaskReportResponse
name|response
init|=
name|real
operator|.
name|getTaskReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetTaskReportResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptReport ( RpcController controller, GetTaskAttemptReportRequestProto proto)
specifier|public
name|GetTaskAttemptReportResponseProto
name|getTaskAttemptReport
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetTaskAttemptReportRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetTaskAttemptReportRequest
name|request
init|=
operator|new
name|GetTaskAttemptReportRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetTaskAttemptReportResponse
name|response
init|=
name|real
operator|.
name|getTaskAttemptReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetTaskAttemptReportResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCounters (RpcController controller, GetCountersRequestProto proto)
specifier|public
name|GetCountersResponseProto
name|getCounters
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetCountersRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetCountersRequest
name|request
init|=
operator|new
name|GetCountersRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetCountersResponse
name|response
init|=
name|real
operator|.
name|getCounters
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetCountersResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptCompletionEvents ( RpcController controller, GetTaskAttemptCompletionEventsRequestProto proto)
specifier|public
name|GetTaskAttemptCompletionEventsResponseProto
name|getTaskAttemptCompletionEvents
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetTaskAttemptCompletionEventsRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetTaskAttemptCompletionEventsRequest
name|request
init|=
operator|new
name|GetTaskAttemptCompletionEventsRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetTaskAttemptCompletionEventsResponse
name|response
init|=
name|real
operator|.
name|getTaskAttemptCompletionEvents
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetTaskAttemptCompletionEventsResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTaskReports (RpcController controller, GetTaskReportsRequestProto proto)
specifier|public
name|GetTaskReportsResponseProto
name|getTaskReports
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetTaskReportsRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetTaskReportsRequest
name|request
init|=
operator|new
name|GetTaskReportsRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetTaskReportsResponse
name|response
init|=
name|real
operator|.
name|getTaskReports
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetTaskReportsResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDiagnostics (RpcController controller, GetDiagnosticsRequestProto proto)
specifier|public
name|GetDiagnosticsResponseProto
name|getDiagnostics
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetDiagnosticsRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetDiagnosticsRequest
name|request
init|=
operator|new
name|GetDiagnosticsRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetDiagnosticsResponse
name|response
init|=
name|real
operator|.
name|getDiagnostics
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetDiagnosticsResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDelegationToken ( RpcController controller, GetDelegationTokenRequestProto proto)
specifier|public
name|GetDelegationTokenResponseProto
name|getDelegationToken
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetDelegationTokenRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetDelegationTokenRequest
name|request
init|=
operator|new
name|GetDelegationTokenRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetDelegationTokenResponse
name|response
init|=
name|real
operator|.
name|getDelegationToken
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetDelegationTokenResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|killJob (RpcController controller, KillJobRequestProto proto)
specifier|public
name|KillJobResponseProto
name|killJob
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|KillJobRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|KillJobRequest
name|request
init|=
operator|new
name|KillJobRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|KillJobResponse
name|response
init|=
name|real
operator|.
name|killJob
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|KillJobResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|killTask (RpcController controller, KillTaskRequestProto proto)
specifier|public
name|KillTaskResponseProto
name|killTask
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|KillTaskRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|KillTaskRequest
name|request
init|=
operator|new
name|KillTaskRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|KillTaskResponse
name|response
init|=
name|real
operator|.
name|killTask
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|KillTaskResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|killTaskAttempt (RpcController controller, KillTaskAttemptRequestProto proto)
specifier|public
name|KillTaskAttemptResponseProto
name|killTaskAttempt
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|KillTaskAttemptRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|KillTaskAttemptRequest
name|request
init|=
operator|new
name|KillTaskAttemptRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|KillTaskAttemptResponse
name|response
init|=
name|real
operator|.
name|killTaskAttempt
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|KillTaskAttemptResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|failTaskAttempt (RpcController controller, FailTaskAttemptRequestProto proto)
specifier|public
name|FailTaskAttemptResponseProto
name|failTaskAttempt
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|FailTaskAttemptRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|FailTaskAttemptRequest
name|request
init|=
operator|new
name|FailTaskAttemptRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|FailTaskAttemptResponse
name|response
init|=
name|real
operator|.
name|failTaskAttempt
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|FailTaskAttemptResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|renewDelegationToken ( RpcController controller, RenewDelegationTokenRequestProto proto)
specifier|public
name|RenewDelegationTokenResponseProto
name|renewDelegationToken
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RenewDelegationTokenRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RenewDelegationTokenRequestPBImpl
name|request
init|=
operator|new
name|RenewDelegationTokenRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|RenewDelegationTokenResponse
name|response
init|=
name|real
operator|.
name|renewDelegationToken
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RenewDelegationTokenResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|cancelDelegationToken ( RpcController controller, CancelDelegationTokenRequestProto proto)
specifier|public
name|CancelDelegationTokenResponseProto
name|cancelDelegationToken
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|CancelDelegationTokenRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|CancelDelegationTokenRequestPBImpl
name|request
init|=
operator|new
name|CancelDelegationTokenRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|CancelDelegationTokenResponse
name|response
init|=
name|real
operator|.
name|cancelDelegationToken
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|CancelDelegationTokenResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

