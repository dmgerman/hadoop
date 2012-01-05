begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolPB
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
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|RefreshUserMappingsProtocolProtos
operator|.
name|RefreshSuperUserGroupsConfigurationRequestProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|RefreshUserMappingsProtocolProtos
operator|.
name|RefreshUserToGroupsMappingsRequestProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolR23Compatible
operator|.
name|ProtocolSignatureWritable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
import|;
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
name|ProtobufHelper
import|;
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
name|ProtocolSignature
import|;
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
name|RefreshUserMappingsProtocol
import|;
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
DECL|class|RefreshUserMappingsProtocolClientSideTranslatorPB
specifier|public
class|class
name|RefreshUserMappingsProtocolClientSideTranslatorPB
implements|implements
name|RefreshUserMappingsProtocol
implements|,
name|Closeable
block|{
comment|/** RpcController is not used and hence is set to null */
DECL|field|NULL_CONTROLLER
specifier|private
specifier|final
specifier|static
name|RpcController
name|NULL_CONTROLLER
init|=
literal|null
decl_stmt|;
DECL|field|rpcProxy
specifier|private
specifier|final
name|RefreshUserMappingsProtocolPB
name|rpcProxy
decl_stmt|;
DECL|method|RefreshUserMappingsProtocolClientSideTranslatorPB ( InetSocketAddress nameNodeAddr, UserGroupInformation ugi, Configuration conf)
specifier|public
name|RefreshUserMappingsProtocolClientSideTranslatorPB
parameter_list|(
name|InetSocketAddress
name|nameNodeAddr
parameter_list|,
name|UserGroupInformation
name|ugi
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
name|RefreshUserMappingsProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|rpcProxy
operator|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|RefreshUserMappingsProtocolPB
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|RefreshUserMappingsProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|NameNode
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getSocketFactory
argument_list|(
name|conf
argument_list|,
name|RefreshUserMappingsProtocol
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|rpcProxy
operator|.
name|getProtocolVersion
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHash
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ProtocolSignatureWritable
operator|.
name|convert
argument_list|(
name|rpcProxy
operator|.
name|getProtocolSignature2
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|clientMethodsHash
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|rpcProxy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refreshUserToGroupsMappings ()
specifier|public
name|void
name|refreshUserToGroupsMappings
parameter_list|()
throws|throws
name|IOException
block|{
name|RefreshUserToGroupsMappingsRequestProto
name|request
init|=
name|RefreshUserToGroupsMappingsRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcProxy
operator|.
name|refreshUserToGroupsMappings
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshSuperUserGroupsConfiguration ()
specifier|public
name|void
name|refreshSuperUserGroupsConfiguration
parameter_list|()
throws|throws
name|IOException
block|{
name|RefreshSuperUserGroupsConfigurationRequestProto
name|request
init|=
name|RefreshSuperUserGroupsConfigurationRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|rpcProxy
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

