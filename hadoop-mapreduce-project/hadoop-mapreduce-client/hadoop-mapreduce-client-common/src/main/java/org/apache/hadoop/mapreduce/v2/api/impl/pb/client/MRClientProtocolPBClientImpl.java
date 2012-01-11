begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.impl.pb.client
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
name|client
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
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
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
name|ipc
operator|.
name|RPC
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRemoteException
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
name|ProtoOverHadoopRpcEngine
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
name|proto
operator|.
name|MRClientProtocol
operator|.
name|MRClientProtocolService
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
DECL|class|MRClientProtocolPBClientImpl
specifier|public
class|class
name|MRClientProtocolPBClientImpl
implements|implements
name|MRClientProtocol
block|{
DECL|field|proxy
specifier|private
name|MRClientProtocolService
operator|.
name|BlockingInterface
name|proxy
decl_stmt|;
DECL|method|MRClientProtocolPBClientImpl (long clientVersion, InetSocketAddress addr, Configuration conf)
specifier|public
name|MRClientProtocolPBClientImpl
parameter_list|(
name|long
name|clientVersion
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|MRClientProtocolService
operator|.
name|BlockingInterface
operator|.
name|class
argument_list|,
name|ProtoOverHadoopRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|proxy
operator|=
operator|(
name|MRClientProtocolService
operator|.
name|BlockingInterface
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|MRClientProtocolService
operator|.
name|BlockingInterface
operator|.
name|class
argument_list|,
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
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
name|YarnRemoteException
block|{
name|GetJobReportRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetJobReportRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|GetJobReportResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getJobReport
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|GetTaskReportRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetTaskReportRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|GetTaskReportResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getTaskReport
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|GetTaskAttemptReportRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetTaskAttemptReportRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|GetTaskAttemptReportResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getTaskAttemptReport
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|GetCountersRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetCountersRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|GetCountersResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getCounters
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|GetTaskAttemptCompletionEventsRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetTaskAttemptCompletionEventsRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|GetTaskAttemptCompletionEventsResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getTaskAttemptCompletionEvents
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|GetTaskReportsRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetTaskReportsRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|GetTaskReportsResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getTaskReports
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|GetDiagnosticsRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetDiagnosticsRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|GetDiagnosticsResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getDiagnostics
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|GetDelegationTokenRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetDelegationTokenRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|GetDelegationTokenResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getDelegationToken
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|KillJobRequestProto
name|requestProto
init|=
operator|(
operator|(
name|KillJobRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|KillJobResponsePBImpl
argument_list|(
name|proxy
operator|.
name|killJob
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
name|YarnRemoteException
block|{
name|KillTaskRequestProto
name|requestProto
init|=
operator|(
operator|(
name|KillTaskRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|KillTaskResponsePBImpl
argument_list|(
name|proxy
operator|.
name|killTask
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
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
block|{
name|KillTaskAttemptRequestProto
name|requestProto
init|=
operator|(
operator|(
name|KillTaskAttemptRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|KillTaskAttemptResponsePBImpl
argument_list|(
name|proxy
operator|.
name|killTaskAttempt
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
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
block|{
name|FailTaskAttemptRequestProto
name|requestProto
init|=
operator|(
operator|(
name|FailTaskAttemptRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|FailTaskAttemptResponsePBImpl
argument_list|(
name|proxy
operator|.
name|failTaskAttempt
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|UndeclaredThrowableException
condition|)
block|{
throw|throw
operator|(
name|UndeclaredThrowableException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UndeclaredThrowableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

