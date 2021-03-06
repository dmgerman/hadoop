begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
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
name|ipc
operator|.
name|AlignmentContext
import|;
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
name|CallerContext
import|;
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
name|protobuf
operator|.
name|IpcConnectionContextProtos
operator|.
name|IpcConnectionContextProto
import|;
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
name|protobuf
operator|.
name|IpcConnectionContextProtos
operator|.
name|UserInformationProto
import|;
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
name|protobuf
operator|.
name|RpcHeaderProtos
operator|.
name|*
import|;
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
name|SaslRpcServer
operator|.
name|AuthMethod
import|;
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
name|htrace
operator|.
name|core
operator|.
name|Span
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|core
operator|.
name|Tracer
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
name|ByteString
import|;
end_import

begin_class
DECL|class|ProtoUtil
specifier|public
specifier|abstract
class|class
name|ProtoUtil
block|{
comment|/**    * Read a variable length integer in the same format that ProtoBufs encodes.    * @param in the input stream to read from    * @return the integer    * @throws IOException if it is malformed or EOF.    */
DECL|method|readRawVarint32 (DataInput in)
specifier|public
specifier|static
name|int
name|readRawVarint32
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|tmp
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|tmp
operator|>=
literal|0
condition|)
block|{
return|return
name|tmp
return|;
block|}
name|int
name|result
init|=
name|tmp
operator|&
literal|0x7f
decl_stmt|;
if|if
condition|(
operator|(
name|tmp
operator|=
name|in
operator|.
name|readByte
argument_list|()
operator|)
operator|>=
literal|0
condition|)
block|{
name|result
operator||=
name|tmp
operator|<<
literal|7
expr_stmt|;
block|}
else|else
block|{
name|result
operator||=
operator|(
name|tmp
operator|&
literal|0x7f
operator|)
operator|<<
literal|7
expr_stmt|;
if|if
condition|(
operator|(
name|tmp
operator|=
name|in
operator|.
name|readByte
argument_list|()
operator|)
operator|>=
literal|0
condition|)
block|{
name|result
operator||=
name|tmp
operator|<<
literal|14
expr_stmt|;
block|}
else|else
block|{
name|result
operator||=
operator|(
name|tmp
operator|&
literal|0x7f
operator|)
operator|<<
literal|14
expr_stmt|;
if|if
condition|(
operator|(
name|tmp
operator|=
name|in
operator|.
name|readByte
argument_list|()
operator|)
operator|>=
literal|0
condition|)
block|{
name|result
operator||=
name|tmp
operator|<<
literal|21
expr_stmt|;
block|}
else|else
block|{
name|result
operator||=
operator|(
name|tmp
operator|&
literal|0x7f
operator|)
operator|<<
literal|21
expr_stmt|;
name|result
operator||=
operator|(
name|tmp
operator|=
name|in
operator|.
name|readByte
argument_list|()
operator|)
operator|<<
literal|28
expr_stmt|;
if|if
condition|(
name|tmp
operator|<
literal|0
condition|)
block|{
comment|// Discard upper 32 bits.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|in
operator|.
name|readByte
argument_list|()
operator|>=
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Malformed varint"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**     * This method creates the connection context  using exactly the same logic    * as the old connection context as was done for writable where    * the effective and real users are set based on the auth method.    *    */
DECL|method|makeIpcConnectionContext ( final String protocol, final UserGroupInformation ugi, final AuthMethod authMethod)
specifier|public
specifier|static
name|IpcConnectionContextProto
name|makeIpcConnectionContext
parameter_list|(
specifier|final
name|String
name|protocol
parameter_list|,
specifier|final
name|UserGroupInformation
name|ugi
parameter_list|,
specifier|final
name|AuthMethod
name|authMethod
parameter_list|)
block|{
name|IpcConnectionContextProto
operator|.
name|Builder
name|result
init|=
name|IpcConnectionContextProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|protocol
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|setProtocol
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
block|}
name|UserInformationProto
operator|.
name|Builder
name|ugiProto
init|=
name|UserInformationProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|ugi
operator|!=
literal|null
condition|)
block|{
comment|/*        * In the connection context we send only additional user info that        * is not derived from the authentication done during connection setup.        */
if|if
condition|(
name|authMethod
operator|==
name|AuthMethod
operator|.
name|KERBEROS
condition|)
block|{
comment|// Real user was established as part of the connection.
comment|// Send effective user only.
name|ugiProto
operator|.
name|setEffectiveUser
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|authMethod
operator|==
name|AuthMethod
operator|.
name|TOKEN
condition|)
block|{
comment|// With token, the connection itself establishes
comment|// both real and effective user. Hence send none in header.
block|}
else|else
block|{
comment|// Simple authentication
comment|// No user info is established as part of the connection.
comment|// Send both effective user and real user
name|ugiProto
operator|.
name|setEffectiveUser
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ugiProto
operator|.
name|setRealUser
argument_list|(
name|ugi
operator|.
name|getRealUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|.
name|setUserInfo
argument_list|(
name|ugiProto
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getUgi (IpcConnectionContextProto context)
specifier|public
specifier|static
name|UserGroupInformation
name|getUgi
parameter_list|(
name|IpcConnectionContextProto
name|context
parameter_list|)
block|{
if|if
condition|(
name|context
operator|.
name|hasUserInfo
argument_list|()
condition|)
block|{
name|UserInformationProto
name|userInfo
init|=
name|context
operator|.
name|getUserInfo
argument_list|()
decl_stmt|;
return|return
name|getUgi
argument_list|(
name|userInfo
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|getUgi (UserInformationProto userInfo)
specifier|public
specifier|static
name|UserGroupInformation
name|getUgi
parameter_list|(
name|UserInformationProto
name|userInfo
parameter_list|)
block|{
name|UserGroupInformation
name|ugi
init|=
literal|null
decl_stmt|;
name|String
name|effectiveUser
init|=
name|userInfo
operator|.
name|hasEffectiveUser
argument_list|()
condition|?
name|userInfo
operator|.
name|getEffectiveUser
argument_list|()
else|:
literal|null
decl_stmt|;
name|String
name|realUser
init|=
name|userInfo
operator|.
name|hasRealUser
argument_list|()
condition|?
name|userInfo
operator|.
name|getRealUser
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|effectiveUser
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|realUser
operator|!=
literal|null
condition|)
block|{
name|UserGroupInformation
name|realUserUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|realUser
argument_list|)
decl_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|effectiveUser
argument_list|,
name|realUserUgi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ugi
operator|=
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|effectiveUser
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ugi
return|;
block|}
DECL|method|convert (RPC.RpcKind kind)
specifier|static
name|RpcKindProto
name|convert
parameter_list|(
name|RPC
operator|.
name|RpcKind
name|kind
parameter_list|)
block|{
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|RPC_BUILTIN
case|:
return|return
name|RpcKindProto
operator|.
name|RPC_BUILTIN
return|;
case|case
name|RPC_WRITABLE
case|:
return|return
name|RpcKindProto
operator|.
name|RPC_WRITABLE
return|;
case|case
name|RPC_PROTOCOL_BUFFER
case|:
return|return
name|RpcKindProto
operator|.
name|RPC_PROTOCOL_BUFFER
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|convert ( RpcKindProto kind)
specifier|public
specifier|static
name|RPC
operator|.
name|RpcKind
name|convert
parameter_list|(
name|RpcKindProto
name|kind
parameter_list|)
block|{
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|RPC_BUILTIN
case|:
return|return
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_BUILTIN
return|;
case|case
name|RPC_WRITABLE
case|:
return|return
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_WRITABLE
return|;
case|case
name|RPC_PROTOCOL_BUFFER
case|:
return|return
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|makeRpcRequestHeader (RPC.RpcKind rpcKind, RpcRequestHeaderProto.OperationProto operation, int callId, int retryCount, byte[] uuid)
specifier|public
specifier|static
name|RpcRequestHeaderProto
name|makeRpcRequestHeader
parameter_list|(
name|RPC
operator|.
name|RpcKind
name|rpcKind
parameter_list|,
name|RpcRequestHeaderProto
operator|.
name|OperationProto
name|operation
parameter_list|,
name|int
name|callId
parameter_list|,
name|int
name|retryCount
parameter_list|,
name|byte
index|[]
name|uuid
parameter_list|)
block|{
return|return
name|makeRpcRequestHeader
argument_list|(
name|rpcKind
argument_list|,
name|operation
argument_list|,
name|callId
argument_list|,
name|retryCount
argument_list|,
name|uuid
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|makeRpcRequestHeader (RPC.RpcKind rpcKind, RpcRequestHeaderProto.OperationProto operation, int callId, int retryCount, byte[] uuid, AlignmentContext alignmentContext)
specifier|public
specifier|static
name|RpcRequestHeaderProto
name|makeRpcRequestHeader
parameter_list|(
name|RPC
operator|.
name|RpcKind
name|rpcKind
parameter_list|,
name|RpcRequestHeaderProto
operator|.
name|OperationProto
name|operation
parameter_list|,
name|int
name|callId
parameter_list|,
name|int
name|retryCount
parameter_list|,
name|byte
index|[]
name|uuid
parameter_list|,
name|AlignmentContext
name|alignmentContext
parameter_list|)
block|{
name|RpcRequestHeaderProto
operator|.
name|Builder
name|result
init|=
name|RpcRequestHeaderProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|setRpcKind
argument_list|(
name|convert
argument_list|(
name|rpcKind
argument_list|)
argument_list|)
operator|.
name|setRpcOp
argument_list|(
name|operation
argument_list|)
operator|.
name|setCallId
argument_list|(
name|callId
argument_list|)
operator|.
name|setRetryCount
argument_list|(
name|retryCount
argument_list|)
operator|.
name|setClientId
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add tracing info if we are currently tracing.
name|Span
name|span
init|=
name|Tracer
operator|.
name|getCurrentSpan
argument_list|()
decl_stmt|;
if|if
condition|(
name|span
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|setTraceInfo
argument_list|(
name|RPCTraceInfoProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceId
argument_list|(
name|span
operator|.
name|getSpanId
argument_list|()
operator|.
name|getHigh
argument_list|()
argument_list|)
operator|.
name|setParentId
argument_list|(
name|span
operator|.
name|getSpanId
argument_list|()
operator|.
name|getLow
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Add caller context if it is not null
name|CallerContext
name|callerContext
init|=
name|CallerContext
operator|.
name|getCurrent
argument_list|()
decl_stmt|;
if|if
condition|(
name|callerContext
operator|!=
literal|null
operator|&&
name|callerContext
operator|.
name|isContextValid
argument_list|()
condition|)
block|{
name|RPCCallerContextProto
operator|.
name|Builder
name|contextBuilder
init|=
name|RPCCallerContextProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContext
argument_list|(
name|callerContext
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|callerContext
operator|.
name|getSignature
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|contextBuilder
operator|.
name|setSignature
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|callerContext
operator|.
name|getSignature
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|setCallerContext
argument_list|(
name|contextBuilder
argument_list|)
expr_stmt|;
block|}
comment|// Add alignment context if it is not null
if|if
condition|(
name|alignmentContext
operator|!=
literal|null
condition|)
block|{
name|alignmentContext
operator|.
name|updateRequestState
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

