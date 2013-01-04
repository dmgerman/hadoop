begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api
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
package|;
end_package

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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRemoteException
import|;
end_import

begin_interface
DECL|interface|MRClientProtocol
specifier|public
interface|interface
name|MRClientProtocol
block|{
comment|/**    * Address to which the client is connected    * @return InetSocketAddress    */
DECL|method|getConnectAddress ()
specifier|public
name|InetSocketAddress
name|getConnectAddress
parameter_list|()
function_decl|;
DECL|method|getJobReport (GetJobReportRequest request)
specifier|public
name|GetJobReportResponse
name|getJobReport
parameter_list|(
name|GetJobReportRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|getTaskReport (GetTaskReportRequest request)
specifier|public
name|GetTaskReportResponse
name|getTaskReport
parameter_list|(
name|GetTaskReportRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|getTaskAttemptReport (GetTaskAttemptReportRequest request)
specifier|public
name|GetTaskAttemptReportResponse
name|getTaskAttemptReport
parameter_list|(
name|GetTaskAttemptReportRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|getCounters (GetCountersRequest request)
specifier|public
name|GetCountersResponse
name|getCounters
parameter_list|(
name|GetCountersRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|getTaskAttemptCompletionEvents (GetTaskAttemptCompletionEventsRequest request)
specifier|public
name|GetTaskAttemptCompletionEventsResponse
name|getTaskAttemptCompletionEvents
parameter_list|(
name|GetTaskAttemptCompletionEventsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|getTaskReports (GetTaskReportsRequest request)
specifier|public
name|GetTaskReportsResponse
name|getTaskReports
parameter_list|(
name|GetTaskReportsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|getDiagnostics (GetDiagnosticsRequest request)
specifier|public
name|GetDiagnosticsResponse
name|getDiagnostics
parameter_list|(
name|GetDiagnosticsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|killJob (KillJobRequest request)
specifier|public
name|KillJobResponse
name|killJob
parameter_list|(
name|KillJobRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|killTask (KillTaskRequest request)
specifier|public
name|KillTaskResponse
name|killTask
parameter_list|(
name|KillTaskRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|killTaskAttempt (KillTaskAttemptRequest request)
specifier|public
name|KillTaskAttemptResponse
name|killTaskAttempt
parameter_list|(
name|KillTaskAttemptRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|failTaskAttempt (FailTaskAttemptRequest request)
specifier|public
name|FailTaskAttemptResponse
name|failTaskAttempt
parameter_list|(
name|FailTaskAttemptRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
DECL|method|getDelegationToken (GetDelegationTokenRequest request)
specifier|public
name|GetDelegationTokenResponse
name|getDelegationToken
parameter_list|(
name|GetDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
comment|/**    * Renew an existing delegation token.    *     * @param request the delegation token to be renewed.    * @return the new expiry time for the delegation token.    * @throws YarnRemoteException    */
DECL|method|renewDelegationToken ( RenewDelegationTokenRequest request)
specifier|public
name|RenewDelegationTokenResponse
name|renewDelegationToken
parameter_list|(
name|RenewDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
comment|/**    * Cancel an existing delegation token.    *     * @param request the delegation token to be cancelled.    * @return an empty response.    * @throws YarnRemoteException    */
DECL|method|cancelDelegationToken ( CancelDelegationTokenRequest request)
specifier|public
name|CancelDelegationTokenResponse
name|cancelDelegationToken
parameter_list|(
name|CancelDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
function_decl|;
block|}
end_interface

end_unit

