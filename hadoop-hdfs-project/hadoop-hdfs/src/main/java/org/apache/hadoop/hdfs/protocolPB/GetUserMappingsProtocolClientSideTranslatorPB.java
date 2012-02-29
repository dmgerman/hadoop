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
name|GetUserMappingsProtocolProtos
operator|.
name|GetGroupsForUserRequestProto
import|;
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
name|GetUserMappingsProtocolProtos
operator|.
name|GetGroupsForUserResponseProto
import|;
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
name|ProtocolMetaInterface
import|;
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
name|ipc
operator|.
name|RpcClientUtil
import|;
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
name|RpcPayloadHeader
operator|.
name|RpcKind
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|GetUserMappingsProtocol
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
DECL|class|GetUserMappingsProtocolClientSideTranslatorPB
specifier|public
class|class
name|GetUserMappingsProtocolClientSideTranslatorPB
implements|implements
name|ProtocolMetaInterface
implements|,
name|GetUserMappingsProtocol
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
name|GetUserMappingsProtocolPB
name|rpcProxy
decl_stmt|;
DECL|method|GetUserMappingsProtocolClientSideTranslatorPB ( GetUserMappingsProtocolPB rpcProxy)
specifier|public
name|GetUserMappingsProtocolClientSideTranslatorPB
parameter_list|(
name|GetUserMappingsProtocolPB
name|rpcProxy
parameter_list|)
block|{
name|this
operator|.
name|rpcProxy
operator|=
name|rpcProxy
expr_stmt|;
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
DECL|method|getGroupsForUser (String user)
specifier|public
name|String
index|[]
name|getGroupsForUser
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|GetGroupsForUserRequestProto
name|request
init|=
name|GetGroupsForUserRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|GetGroupsForUserResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|getGroupsForUser
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
return|return
name|resp
operator|.
name|getGroupsList
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|resp
operator|.
name|getGroupsCount
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isMethodSupported (String methodName)
specifier|public
name|boolean
name|isMethodSupported
parameter_list|(
name|String
name|methodName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|RpcClientUtil
operator|.
name|isMethodSupported
argument_list|(
name|rpcProxy
argument_list|,
name|GetUserMappingsProtocolPB
operator|.
name|class
argument_list|,
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|GetUserMappingsProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|methodName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

