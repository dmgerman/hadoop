begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.impl.pb.client
package|package
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
name|Closeable
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
import|;
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
name|ProtobufRpcEngine
import|;
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationClientProtocol
import|;
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
name|ApplicationClientProtocolPB
import|;
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
name|yarn
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetAllApplicationsRequest
import|;
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
name|protocolrecords
operator|.
name|GetAllApplicationsResponse
import|;
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
name|protocolrecords
operator|.
name|GetApplicationReportRequest
import|;
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
name|protocolrecords
operator|.
name|GetApplicationReportResponse
import|;
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
name|protocolrecords
operator|.
name|GetClusterMetricsRequest
import|;
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
name|protocolrecords
operator|.
name|GetClusterMetricsResponse
import|;
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
name|protocolrecords
operator|.
name|GetClusterNodesRequest
import|;
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
name|protocolrecords
operator|.
name|GetClusterNodesResponse
import|;
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
name|yarn
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetNewApplicationRequest
import|;
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
name|protocolrecords
operator|.
name|GetNewApplicationResponse
import|;
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
name|protocolrecords
operator|.
name|GetQueueInfoRequest
import|;
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
name|protocolrecords
operator|.
name|GetQueueInfoResponse
import|;
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
name|protocolrecords
operator|.
name|GetQueueUserAclsInfoRequest
import|;
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
name|protocolrecords
operator|.
name|GetQueueUserAclsInfoResponse
import|;
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
name|protocolrecords
operator|.
name|KillApplicationRequest
import|;
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
name|protocolrecords
operator|.
name|KillApplicationResponse
import|;
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
name|yarn
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
name|api
operator|.
name|protocolrecords
operator|.
name|SubmitApplicationRequest
import|;
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
name|protocolrecords
operator|.
name|SubmitApplicationResponse
import|;
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
name|yarn
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetAllApplicationsRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetAllApplicationsResponsePBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetApplicationReportRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetApplicationReportResponsePBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetClusterMetricsRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetClusterMetricsResponsePBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetClusterNodesRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetClusterNodesResponsePBImpl
import|;
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
name|yarn
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetNewApplicationRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetNewApplicationResponsePBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetQueueInfoRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetQueueInfoResponsePBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetQueueUserAclsInfoRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetQueueUserAclsInfoResponsePBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|KillApplicationRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|KillApplicationResponsePBImpl
import|;
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
name|yarn
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubmitApplicationRequestPBImpl
import|;
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
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|SubmitApplicationResponsePBImpl
import|;
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
name|YarnException
import|;
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
name|RPCUtil
import|;
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
name|YarnServiceProtos
operator|.
name|GetAllApplicationsRequestProto
import|;
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
name|YarnServiceProtos
operator|.
name|GetApplicationReportRequestProto
import|;
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
name|YarnServiceProtos
operator|.
name|GetClusterMetricsRequestProto
import|;
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
name|YarnServiceProtos
operator|.
name|GetClusterNodesRequestProto
import|;
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
name|YarnServiceProtos
operator|.
name|GetNewApplicationRequestProto
import|;
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
name|YarnServiceProtos
operator|.
name|GetQueueInfoRequestProto
import|;
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
name|YarnServiceProtos
operator|.
name|GetQueueUserAclsInfoRequestProto
import|;
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
name|YarnServiceProtos
operator|.
name|KillApplicationRequestProto
import|;
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
name|YarnServiceProtos
operator|.
name|SubmitApplicationRequestProto
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
annotation|@
name|Private
DECL|class|ApplicationClientProtocolPBClientImpl
specifier|public
class|class
name|ApplicationClientProtocolPBClientImpl
implements|implements
name|ApplicationClientProtocol
implements|,
name|Closeable
block|{
DECL|field|proxy
specifier|private
name|ApplicationClientProtocolPB
name|proxy
decl_stmt|;
DECL|method|ApplicationClientProtocolPBClientImpl (long clientVersion, InetSocketAddress addr, Configuration conf)
specifier|public
name|ApplicationClientProtocolPBClientImpl
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
name|ApplicationClientProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|proxy
operator|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|ApplicationClientProtocolPB
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
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|this
operator|.
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|forceKillApplication ( KillApplicationRequest request)
specifier|public
name|KillApplicationResponse
name|forceKillApplication
parameter_list|(
name|KillApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|KillApplicationRequestProto
name|requestProto
init|=
operator|(
operator|(
name|KillApplicationRequestPBImpl
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
name|KillApplicationResponsePBImpl
argument_list|(
name|proxy
operator|.
name|forceKillApplication
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getApplicationReport ( GetApplicationReportRequest request)
specifier|public
name|GetApplicationReportResponse
name|getApplicationReport
parameter_list|(
name|GetApplicationReportRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetApplicationReportRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetApplicationReportRequestPBImpl
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
name|GetApplicationReportResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getApplicationReport
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getClusterMetrics ( GetClusterMetricsRequest request)
specifier|public
name|GetClusterMetricsResponse
name|getClusterMetrics
parameter_list|(
name|GetClusterMetricsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetClusterMetricsRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetClusterMetricsRequestPBImpl
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
name|GetClusterMetricsResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getClusterMetrics
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNewApplication ( GetNewApplicationRequest request)
specifier|public
name|GetNewApplicationResponse
name|getNewApplication
parameter_list|(
name|GetNewApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetNewApplicationRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetNewApplicationRequestPBImpl
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
name|GetNewApplicationResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getNewApplication
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|submitApplication ( SubmitApplicationRequest request)
specifier|public
name|SubmitApplicationResponse
name|submitApplication
parameter_list|(
name|SubmitApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|SubmitApplicationRequestProto
name|requestProto
init|=
operator|(
operator|(
name|SubmitApplicationRequestPBImpl
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
name|SubmitApplicationResponsePBImpl
argument_list|(
name|proxy
operator|.
name|submitApplication
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAllApplications ( GetAllApplicationsRequest request)
specifier|public
name|GetAllApplicationsResponse
name|getAllApplications
parameter_list|(
name|GetAllApplicationsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetAllApplicationsRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetAllApplicationsRequestPBImpl
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
name|GetAllApplicationsResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getAllApplications
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|GetClusterNodesResponse
DECL|method|getClusterNodes (GetClusterNodesRequest request)
name|getClusterNodes
parameter_list|(
name|GetClusterNodesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetClusterNodesRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetClusterNodesRequestPBImpl
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
name|GetClusterNodesResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getClusterNodes
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getQueueInfo (GetQueueInfoRequest request)
specifier|public
name|GetQueueInfoResponse
name|getQueueInfo
parameter_list|(
name|GetQueueInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetQueueInfoRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetQueueInfoRequestPBImpl
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
name|GetQueueInfoResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getQueueInfo
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getQueueUserAcls ( GetQueueUserAclsInfoRequest request)
specifier|public
name|GetQueueUserAclsInfoResponse
name|getQueueUserAcls
parameter_list|(
name|GetQueueUserAclsInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetQueueUserAclsInfoRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetQueueUserAclsInfoRequestPBImpl
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
name|GetQueueUserAclsInfoResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getQueueUserAcls
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
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
name|YarnException
throws|,
name|IOException
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
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
name|YarnException
throws|,
name|IOException
block|{
name|RenewDelegationTokenRequestProto
name|requestProto
init|=
operator|(
operator|(
name|RenewDelegationTokenRequestPBImpl
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
name|RenewDelegationTokenResponsePBImpl
argument_list|(
name|proxy
operator|.
name|renewDelegationToken
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
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
name|YarnException
throws|,
name|IOException
block|{
name|CancelDelegationTokenRequestProto
name|requestProto
init|=
operator|(
operator|(
name|CancelDelegationTokenRequestPBImpl
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
name|CancelDelegationTokenResponsePBImpl
argument_list|(
name|proxy
operator|.
name|cancelDelegationToken
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
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

