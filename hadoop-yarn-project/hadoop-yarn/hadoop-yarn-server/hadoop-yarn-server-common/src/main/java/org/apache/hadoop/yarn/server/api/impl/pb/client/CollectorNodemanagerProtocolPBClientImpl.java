begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.impl.pb.client
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
name|net
operator|.
name|NetUtils
import|;
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
name|GetTimelineCollectorContextRequest
import|;
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
name|ReportNewCollectorInfoRequest
import|;
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
name|ServiceException
import|;
end_import

begin_class
DECL|class|CollectorNodemanagerProtocolPBClientImpl
specifier|public
class|class
name|CollectorNodemanagerProtocolPBClientImpl
implements|implements
name|CollectorNodemanagerProtocol
implements|,
name|Closeable
block|{
comment|// Not a documented config. Only used for tests internally
DECL|field|NM_COMMAND_TIMEOUT
specifier|static
specifier|final
name|String
name|NM_COMMAND_TIMEOUT
init|=
name|YarnConfiguration
operator|.
name|YARN_PREFIX
operator|+
literal|"rpc.nm-command-timeout"
decl_stmt|;
comment|/**    * Maximum of 1 minute timeout for a Node to react to the command.    */
DECL|field|DEFAULT_COMMAND_TIMEOUT
specifier|static
specifier|final
name|int
name|DEFAULT_COMMAND_TIMEOUT
init|=
literal|60000
decl_stmt|;
DECL|field|proxy
specifier|private
name|CollectorNodemanagerProtocolPB
name|proxy
decl_stmt|;
annotation|@
name|Private
DECL|method|CollectorNodemanagerProtocolPBClientImpl (long clientVersion, InetSocketAddress addr, Configuration conf)
specifier|public
name|CollectorNodemanagerProtocolPBClientImpl
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
name|CollectorNodemanagerProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|int
name|expireIntvl
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|NM_COMMAND_TIMEOUT
argument_list|,
name|DEFAULT_COMMAND_TIMEOUT
argument_list|)
decl_stmt|;
name|proxy
operator|=
operator|(
name|CollectorNodemanagerProtocolPB
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|CollectorNodemanagerProtocolPB
operator|.
name|class
argument_list|,
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
name|expireIntvl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reportNewCollectorInfo ( ReportNewCollectorInfoRequest request)
specifier|public
name|ReportNewCollectorInfoResponse
name|reportNewCollectorInfo
parameter_list|(
name|ReportNewCollectorInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|ReportNewCollectorInfoRequestProto
name|requestProto
init|=
operator|(
operator|(
name|ReportNewCollectorInfoRequestPBImpl
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
name|ReportNewCollectorInfoResponsePBImpl
argument_list|(
name|proxy
operator|.
name|reportNewCollectorInfo
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
DECL|method|getTimelineCollectorContext ( GetTimelineCollectorContextRequest request)
specifier|public
name|GetTimelineCollectorContextResponse
name|getTimelineCollectorContext
parameter_list|(
name|GetTimelineCollectorContextRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetTimelineCollectorContextRequestProto
name|requestProto
init|=
operator|(
operator|(
name|GetTimelineCollectorContextRequestPBImpl
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
name|GetTimelineCollectorContextResponsePBImpl
argument_list|(
name|proxy
operator|.
name|getTimelineCollectorContext
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
block|}
end_class

end_unit

