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
name|yarn
operator|.
name|api
operator|.
name|ClientSCMProtocol
import|;
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
name|ClientSCMProtocolPB
import|;
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
name|ReleaseSharedCacheResourceRequest
import|;
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
name|ReleaseSharedCacheResourceResponse
import|;
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
name|UseSharedCacheResourceRequest
import|;
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
name|UseSharedCacheResourceResponse
import|;
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
name|ReleaseSharedCacheResourceRequestPBImpl
import|;
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
name|ReleaseSharedCacheResourceResponsePBImpl
import|;
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
name|UseSharedCacheResourceRequestPBImpl
import|;
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
name|UseSharedCacheResourceResponsePBImpl
import|;
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
name|ReleaseSharedCacheResourceRequestProto
import|;
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
name|UseSharedCacheResourceRequestProto
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
DECL|class|ClientSCMProtocolPBClientImpl
specifier|public
class|class
name|ClientSCMProtocolPBClientImpl
implements|implements
name|ClientSCMProtocol
implements|,
name|Closeable
block|{
DECL|field|proxy
specifier|private
name|ClientSCMProtocolPB
name|proxy
decl_stmt|;
DECL|method|ClientSCMProtocolPBClientImpl (long clientVersion, InetSocketAddress addr, Configuration conf)
specifier|public
name|ClientSCMProtocolPBClientImpl
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
name|ClientSCMProtocolPB
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
name|ClientSCMProtocolPB
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
name|this
operator|.
name|proxy
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|use ( UseSharedCacheResourceRequest request)
specifier|public
name|UseSharedCacheResourceResponse
name|use
parameter_list|(
name|UseSharedCacheResourceRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|UseSharedCacheResourceRequestProto
name|requestProto
init|=
operator|(
operator|(
name|UseSharedCacheResourceRequestPBImpl
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
name|UseSharedCacheResourceResponsePBImpl
argument_list|(
name|proxy
operator|.
name|use
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
DECL|method|release ( ReleaseSharedCacheResourceRequest request)
specifier|public
name|ReleaseSharedCacheResourceResponse
name|release
parameter_list|(
name|ReleaseSharedCacheResourceRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|ReleaseSharedCacheResourceRequestProto
name|requestProto
init|=
operator|(
operator|(
name|ReleaseSharedCacheResourceRequestPBImpl
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
name|ReleaseSharedCacheResourceResponsePBImpl
argument_list|(
name|proxy
operator|.
name|release
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

