begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.impl.pb.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
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
name|YarnServerCommonServiceProtos
operator|.
name|GetTimelineCollectorContextRequestProto
import|;
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
name|YarnServerCommonServiceProtos
operator|.
name|GetTimelineCollectorContextResponseProto
import|;
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
name|YarnServerCommonServiceProtos
operator|.
name|ReportNewCollectorInfoRequestProto
import|;
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
name|YarnServerCommonServiceProtos
operator|.
name|ReportNewCollectorInfoResponseProto
import|;
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
name|server
operator|.
name|api
operator|.
name|CollectorNodemanagerProtocol
import|;
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
name|server
operator|.
name|api
operator|.
name|CollectorNodemanagerProtocolPB
import|;
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetTimelineCollectorContextResponse
import|;
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ReportNewCollectorInfoResponse
import|;
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetTimelineCollectorContextRequestPBImpl
import|;
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|GetTimelineCollectorContextResponsePBImpl
import|;
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|ReportNewCollectorInfoRequestPBImpl
import|;
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|ReportNewCollectorInfoResponsePBImpl
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
DECL|class|CollectorNodemanagerProtocolPBServiceImpl
specifier|public
class|class
name|CollectorNodemanagerProtocolPBServiceImpl
implements|implements
name|CollectorNodemanagerProtocolPB
block|{
DECL|field|real
specifier|private
name|CollectorNodemanagerProtocol
name|real
decl_stmt|;
DECL|method|CollectorNodemanagerProtocolPBServiceImpl ( CollectorNodemanagerProtocol impl)
specifier|public
name|CollectorNodemanagerProtocolPBServiceImpl
parameter_list|(
name|CollectorNodemanagerProtocol
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
DECL|method|reportNewCollectorInfo ( RpcController arg0, ReportNewCollectorInfoRequestProto proto)
specifier|public
name|ReportNewCollectorInfoResponseProto
name|reportNewCollectorInfo
parameter_list|(
name|RpcController
name|arg0
parameter_list|,
name|ReportNewCollectorInfoRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|ReportNewCollectorInfoRequestPBImpl
name|request
init|=
operator|new
name|ReportNewCollectorInfoRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|ReportNewCollectorInfoResponse
name|response
init|=
name|real
operator|.
name|reportNewCollectorInfo
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|ReportNewCollectorInfoResponsePBImpl
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
DECL|method|getTimelineCollectorContext ( RpcController controller, GetTimelineCollectorContextRequestProto proto)
specifier|public
name|GetTimelineCollectorContextResponseProto
name|getTimelineCollectorContext
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetTimelineCollectorContextRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|GetTimelineCollectorContextRequestPBImpl
name|request
init|=
operator|new
name|GetTimelineCollectorContextRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|GetTimelineCollectorContextResponse
name|response
init|=
name|real
operator|.
name|getTimelineCollectorContext
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|GetTimelineCollectorContextResponsePBImpl
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

