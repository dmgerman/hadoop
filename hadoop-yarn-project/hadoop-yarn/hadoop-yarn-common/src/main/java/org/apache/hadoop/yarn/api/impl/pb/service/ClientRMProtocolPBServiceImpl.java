begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.impl.pb.service
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
name|service
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
name|api
operator|.
name|ClientRMProtocol
import|;
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
name|ClientRMProtocolPB
import|;
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
name|GetAllApplicationsResponseProto
import|;
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
name|GetApplicationReportResponseProto
import|;
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
name|GetClusterMetricsResponseProto
import|;
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
name|GetClusterNodesResponseProto
import|;
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
name|GetNewApplicationResponseProto
import|;
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
name|GetQueueInfoResponseProto
import|;
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
name|GetQueueUserAclsInfoResponseProto
import|;
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
name|KillApplicationResponseProto
import|;
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
name|SubmitApplicationResponseProto
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
DECL|class|ClientRMProtocolPBServiceImpl
specifier|public
class|class
name|ClientRMProtocolPBServiceImpl
implements|implements
name|ClientRMProtocolPB
block|{
DECL|field|real
specifier|private
name|ClientRMProtocol
name|real
decl_stmt|;
DECL|method|ClientRMProtocolPBServiceImpl (ClientRMProtocol impl)
specifier|public
name|ClientRMProtocolPBServiceImpl
parameter_list|(
name|ClientRMProtocol
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
DECL|method|forceKillApplication (RpcController arg0, KillApplicationRequestProto proto)
specifier|public
name|KillApplicationResponseProto
name|forceKillApplication
parameter_list|(
name|RpcController
name|arg0
parameter_list|,
name|KillApplicationRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|KillApplicationRequestPBImpl
name|request
init|=
operator|new
name|KillApplicationRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|KillApplicationResponse
name|response
init|=
name|real
operator|.
name|forceKillApplication
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|KillApplicationResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
DECL|method|getApplicationReport ( RpcController arg0, GetApplicationReportRequestProto proto)
specifier|public
name|GetApplicationReportResponseProto
name|getApplicationReport
parameter_list|(
name|RpcController
name|arg0
parameter_list|,
name|GetApplicationReportRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetApplicationReportRequestPBImpl
name|request
init|=
operator|new
name|GetApplicationReportRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetApplicationReportResponse
name|response
init|=
name|real
operator|.
name|getApplicationReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetApplicationReportResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
DECL|method|getClusterMetrics (RpcController arg0, GetClusterMetricsRequestProto proto)
specifier|public
name|GetClusterMetricsResponseProto
name|getClusterMetrics
parameter_list|(
name|RpcController
name|arg0
parameter_list|,
name|GetClusterMetricsRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetClusterMetricsRequestPBImpl
name|request
init|=
operator|new
name|GetClusterMetricsRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetClusterMetricsResponse
name|response
init|=
name|real
operator|.
name|getClusterMetrics
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetClusterMetricsResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
DECL|method|getNewApplication ( RpcController arg0, GetNewApplicationRequestProto proto)
specifier|public
name|GetNewApplicationResponseProto
name|getNewApplication
parameter_list|(
name|RpcController
name|arg0
parameter_list|,
name|GetNewApplicationRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetNewApplicationRequestPBImpl
name|request
init|=
operator|new
name|GetNewApplicationRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetNewApplicationResponse
name|response
init|=
name|real
operator|.
name|getNewApplication
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetNewApplicationResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
DECL|method|submitApplication (RpcController arg0, SubmitApplicationRequestProto proto)
specifier|public
name|SubmitApplicationResponseProto
name|submitApplication
parameter_list|(
name|RpcController
name|arg0
parameter_list|,
name|SubmitApplicationRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|SubmitApplicationRequestPBImpl
name|request
init|=
operator|new
name|SubmitApplicationRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|SubmitApplicationResponse
name|response
init|=
name|real
operator|.
name|submitApplication
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|SubmitApplicationResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
DECL|method|getAllApplications ( RpcController controller, GetAllApplicationsRequestProto proto)
specifier|public
name|GetAllApplicationsResponseProto
name|getAllApplications
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetAllApplicationsRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetAllApplicationsRequestPBImpl
name|request
init|=
operator|new
name|GetAllApplicationsRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetAllApplicationsResponse
name|response
init|=
name|real
operator|.
name|getAllApplications
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetAllApplicationsResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
DECL|method|getClusterNodes (RpcController controller, GetClusterNodesRequestProto proto)
specifier|public
name|GetClusterNodesResponseProto
name|getClusterNodes
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetClusterNodesRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetClusterNodesRequestPBImpl
name|request
init|=
operator|new
name|GetClusterNodesRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetClusterNodesResponse
name|response
init|=
name|real
operator|.
name|getClusterNodes
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetClusterNodesResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
DECL|method|getQueueInfo (RpcController controller, GetQueueInfoRequestProto proto)
specifier|public
name|GetQueueInfoResponseProto
name|getQueueInfo
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetQueueInfoRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetQueueInfoRequestPBImpl
name|request
init|=
operator|new
name|GetQueueInfoRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetQueueInfoResponse
name|response
init|=
name|real
operator|.
name|getQueueInfo
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetQueueInfoResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
DECL|method|getQueueUserAcls ( RpcController controller, GetQueueUserAclsInfoRequestProto proto)
specifier|public
name|GetQueueUserAclsInfoResponseProto
name|getQueueUserAcls
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetQueueUserAclsInfoRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetQueueUserAclsInfoRequestPBImpl
name|request
init|=
operator|new
name|GetQueueUserAclsInfoRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetQueueUserAclsInfoResponse
name|response
init|=
name|real
operator|.
name|getQueueUserAcls
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetQueueUserAclsInfoResponsePBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
name|GetDelegationTokenRequestPBImpl
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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
name|YarnException
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
catch|catch
parameter_list|(
name|IOException
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

