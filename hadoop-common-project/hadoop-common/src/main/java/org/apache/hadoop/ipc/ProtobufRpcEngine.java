begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|DataOutput
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Proxy
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
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
import|;
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
name|InterfaceStability
import|;
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
name|io
operator|.
name|DataOutputOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Writable
import|;
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
name|Client
operator|.
name|ConnectionId
import|;
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
operator|.
name|RpcInvoker
import|;
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
name|ipc
operator|.
name|protobuf
operator|.
name|HadoopRpcProtos
operator|.
name|HadoopRpcRequestProto
import|;
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
name|security
operator|.
name|token
operator|.
name|SecretManager
import|;
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
name|token
operator|.
name|TokenIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ProtoUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|BlockingService
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
name|Descriptors
operator|.
name|MethodDescriptor
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
name|Message
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

begin_comment
comment|/**  * RPC Engine for for protobuf based RPCs.  */
end_comment

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ProtobufRpcEngine
specifier|public
class|class
name|ProtobufRpcEngine
implements|implements
name|RpcEngine
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
comment|// Register the rpcRequest deserializer for WritableRpcEngine
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|Server
operator|.
name|registerProtocolEngine
argument_list|(
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|RpcRequestWritable
operator|.
name|class
argument_list|,
operator|new
name|Server
operator|.
name|ProtoBufRpcInvoker
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|CLIENTS
specifier|private
specifier|static
specifier|final
name|ClientCache
name|CLIENTS
init|=
operator|new
name|ClientCache
argument_list|()
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getProxy (Class<T> protocol, long clientVersion, InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory, int rpcTimeout)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|ProtocolProxy
argument_list|<
name|T
argument_list|>
name|getProxy
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|,
name|int
name|rpcTimeout
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ProtocolProxy
argument_list|<
name|T
argument_list|>
argument_list|(
name|protocol
argument_list|,
operator|(
name|T
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|protocol
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|protocol
block|}
argument_list|,
operator|new
name|Invoker
argument_list|(
name|protocol
argument_list|,
name|addr
argument_list|,
name|ticket
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|,
name|rpcTimeout
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolMetaInfoProxy ( ConnectionId connId, Configuration conf, SocketFactory factory)
specifier|public
name|ProtocolProxy
argument_list|<
name|ProtocolMetaInfoPB
argument_list|>
name|getProtocolMetaInfoProxy
parameter_list|(
name|ConnectionId
name|connId
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|ProtocolMetaInfoPB
argument_list|>
name|protocol
init|=
name|ProtocolMetaInfoPB
operator|.
name|class
decl_stmt|;
return|return
operator|new
name|ProtocolProxy
argument_list|<
name|ProtocolMetaInfoPB
argument_list|>
argument_list|(
name|protocol
argument_list|,
operator|(
name|ProtocolMetaInfoPB
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|protocol
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|protocol
block|}
argument_list|,
operator|new
name|Invoker
argument_list|(
name|protocol
argument_list|,
name|connId
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|class|Invoker
specifier|private
specifier|static
class|class
name|Invoker
implements|implements
name|RpcInvocationHandler
block|{
DECL|field|returnTypes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Message
argument_list|>
name|returnTypes
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Message
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|isClosed
specifier|private
name|boolean
name|isClosed
init|=
literal|false
decl_stmt|;
DECL|field|remoteId
specifier|private
specifier|final
name|Client
operator|.
name|ConnectionId
name|remoteId
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|field|clientProtocolVersion
specifier|private
specifier|final
name|long
name|clientProtocolVersion
decl_stmt|;
DECL|field|protocolName
specifier|private
specifier|final
name|String
name|protocolName
decl_stmt|;
DECL|method|Invoker (Class<?> protocol, InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory, int rpcTimeout)
specifier|public
name|Invoker
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|,
name|int
name|rpcTimeout
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|protocol
argument_list|,
name|Client
operator|.
name|ConnectionId
operator|.
name|getConnectionId
argument_list|(
name|addr
argument_list|,
name|protocol
argument_list|,
name|ticket
argument_list|,
name|rpcTimeout
argument_list|,
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
comment|/**      * This constructor takes a connectionId, instead of creating a new one.      */
DECL|method|Invoker (Class<?> protocol, Client.ConnectionId connId, Configuration conf, SocketFactory factory)
specifier|public
name|Invoker
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Client
operator|.
name|ConnectionId
name|connId
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|remoteId
operator|=
name|connId
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|CLIENTS
operator|.
name|getClient
argument_list|(
name|conf
argument_list|,
name|factory
argument_list|,
name|RpcResponseWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|protocolName
operator|=
name|RPC
operator|.
name|getProtocolName
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
name|this
operator|.
name|clientProtocolVersion
operator|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
block|}
DECL|method|constructRpcRequest (Method method, Object[] params)
specifier|private
name|HadoopRpcRequestProto
name|constructRpcRequest
parameter_list|(
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|params
parameter_list|)
throws|throws
name|ServiceException
block|{
name|HadoopRpcRequestProto
name|rpcRequest
decl_stmt|;
name|HadoopRpcRequestProto
operator|.
name|Builder
name|builder
init|=
name|HadoopRpcRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setMethodName
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
comment|// RpcController + Message
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"Too many parameters for request. Method: ["
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
operator|+
literal|", Expected: 2, Actual: "
operator|+
name|params
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|params
index|[
literal|1
index|]
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
literal|"null param while calling Method: ["
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Message
name|param
init|=
operator|(
name|Message
operator|)
name|params
index|[
literal|1
index|]
decl_stmt|;
name|builder
operator|.
name|setRequest
argument_list|(
name|param
operator|.
name|toByteString
argument_list|()
argument_list|)
expr_stmt|;
comment|// For protobuf, {@code protocol} used when creating client side proxy is
comment|// the interface extending BlockingInterface, which has the annotations
comment|// such as ProtocolName etc.
comment|//
comment|// Using Method.getDeclaringClass(), as in WritableEngine to get at
comment|// the protocol interface will return BlockingInterface, from where
comment|// the annotation ProtocolName and Version cannot be
comment|// obtained.
comment|//
comment|// Hence we simply use the protocol class used to create the proxy.
comment|// For PB this may limit the use of mixins on client side.
name|builder
operator|.
name|setDeclaringClassProtocolName
argument_list|(
name|protocolName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setClientProtocolVersion
argument_list|(
name|clientProtocolVersion
argument_list|)
expr_stmt|;
name|rpcRequest
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|rpcRequest
return|;
block|}
comment|/**      * This is the client side invoker of RPC method. It only throws      * ServiceException, since the invocation proxy expects only      * ServiceException to be thrown by the method in case protobuf service.      *       * ServiceException has the following causes:      *<ol>      *<li>Exceptions encountered on the client side in this method are       * set as cause in ServiceException as is.</li>      *<li>Exceptions from the server are wrapped in RemoteException and are      * set as cause in ServiceException</li>      *</ol>      *       * Note that the client calling protobuf RPC methods, must handle      * ServiceException by getting the cause from the ServiceException. If the      * cause is RemoteException, then unwrap it to get the exception thrown by      * the server.      */
annotation|@
name|Override
DECL|method|invoke (Object proxy, Method method, Object[] args)
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|ServiceException
block|{
name|long
name|startTime
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
name|HadoopRpcRequestProto
name|rpcRequest
init|=
name|constructRpcRequest
argument_list|(
name|method
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|RpcResponseWritable
name|val
init|=
literal|null
decl_stmt|;
try|try
block|{
name|val
operator|=
operator|(
name|RpcResponseWritable
operator|)
name|client
operator|.
name|call
argument_list|(
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
operator|new
name|RpcRequestWritable
argument_list|(
name|rpcRequest
argument_list|)
argument_list|,
name|remoteId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|long
name|callTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Call: "
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|callTime
argument_list|)
expr_stmt|;
block|}
name|Message
name|prototype
init|=
literal|null
decl_stmt|;
try|try
block|{
name|prototype
operator|=
name|getReturnProtoType
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|Message
name|returnMessage
decl_stmt|;
try|try
block|{
name|returnMessage
operator|=
name|prototype
operator|.
name|newBuilderForType
argument_list|()
operator|.
name|mergeFrom
argument_list|(
name|val
operator|.
name|responseMessage
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
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
return|return
name|returnMessage
return|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isClosed
condition|)
block|{
name|isClosed
operator|=
literal|true
expr_stmt|;
name|CLIENTS
operator|.
name|stopClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getReturnProtoType (Method method)
specifier|private
name|Message
name|getReturnProtoType
parameter_list|(
name|Method
name|method
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|returnTypes
operator|.
name|containsKey
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|returnTypes
operator|.
name|get
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|returnType
init|=
name|method
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
name|Method
name|newInstMethod
init|=
name|returnType
operator|.
name|getMethod
argument_list|(
literal|"getDefaultInstance"
argument_list|)
decl_stmt|;
name|newInstMethod
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Message
name|prototype
init|=
operator|(
name|Message
operator|)
name|newInstMethod
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|returnTypes
operator|.
name|put
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|prototype
argument_list|)
expr_stmt|;
return|return
name|prototype
return|;
block|}
annotation|@
name|Override
comment|//RpcInvocationHandler
DECL|method|getConnectionId ()
specifier|public
name|ConnectionId
name|getConnectionId
parameter_list|()
block|{
return|return
name|remoteId
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|call (Method method, Object[][] params, InetSocketAddress[] addrs, UserGroupInformation ticket, Configuration conf)
specifier|public
name|Object
index|[]
name|call
parameter_list|(
name|Method
name|method
parameter_list|,
name|Object
index|[]
index|[]
name|params
parameter_list|,
name|InetSocketAddress
index|[]
name|addrs
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Writable Wrapper for Protocol Buffer Requests    */
DECL|class|RpcRequestWritable
specifier|private
specifier|static
class|class
name|RpcRequestWritable
implements|implements
name|Writable
block|{
DECL|field|message
name|HadoopRpcRequestProto
name|message
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|RpcRequestWritable ()
specifier|public
name|RpcRequestWritable
parameter_list|()
block|{     }
DECL|method|RpcRequestWritable (HadoopRpcRequestProto message)
name|RpcRequestWritable
parameter_list|(
name|HadoopRpcRequestProto
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|Message
operator|)
name|message
operator|)
operator|.
name|writeDelimitedTo
argument_list|(
name|DataOutputOutputStream
operator|.
name|constructOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|ProtoUtil
operator|.
name|readRawVarint32
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|message
operator|=
name|HadoopRpcRequestProto
operator|.
name|parseFrom
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|message
operator|.
name|getDeclaringClassProtocolName
argument_list|()
operator|+
literal|"."
operator|+
name|message
operator|.
name|getMethodName
argument_list|()
return|;
block|}
block|}
comment|/**    * Writable Wrapper for Protocol Buffer Responses    */
DECL|class|RpcResponseWritable
specifier|private
specifier|static
class|class
name|RpcResponseWritable
implements|implements
name|Writable
block|{
DECL|field|responseMessage
name|byte
index|[]
name|responseMessage
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|RpcResponseWritable ()
specifier|public
name|RpcResponseWritable
parameter_list|()
block|{     }
DECL|method|RpcResponseWritable (Message message)
specifier|public
name|RpcResponseWritable
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|this
operator|.
name|responseMessage
operator|=
name|message
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|responseMessage
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|responseMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|responseMessage
operator|=
name|bytes
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getClient (Configuration conf)
specifier|static
name|Client
name|getClient
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|CLIENTS
operator|.
name|getClient
argument_list|(
name|conf
argument_list|,
name|SocketFactory
operator|.
name|getDefault
argument_list|()
argument_list|,
name|RpcResponseWritable
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getServer (Class<?> protocol, Object protocolImpl, String bindAddress, int port, int numHandlers, int numReaders, int queueSizePerHandler, boolean verbose, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager, String portRangeConfig)
specifier|public
name|RPC
operator|.
name|Server
name|getServer
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Object
name|protocolImpl
parameter_list|,
name|String
name|bindAddress
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|numHandlers
parameter_list|,
name|int
name|numReaders
parameter_list|,
name|int
name|queueSizePerHandler
parameter_list|,
name|boolean
name|verbose
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SecretManager
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|,
name|String
name|portRangeConfig
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Server
argument_list|(
name|protocol
argument_list|,
name|protocolImpl
argument_list|,
name|conf
argument_list|,
name|bindAddress
argument_list|,
name|port
argument_list|,
name|numHandlers
argument_list|,
name|numReaders
argument_list|,
name|queueSizePerHandler
argument_list|,
name|verbose
argument_list|,
name|secretManager
argument_list|,
name|portRangeConfig
argument_list|)
return|;
block|}
DECL|class|Server
specifier|public
specifier|static
class|class
name|Server
extends|extends
name|RPC
operator|.
name|Server
block|{
comment|/**      * Construct an RPC server.      *       * @param protocolClass the class of protocol      * @param protocolImpl the protocolImpl whose methods will be called      * @param conf the configuration to use      * @param bindAddress the address to bind on to listen for connection      * @param port the port to listen for connections on      * @param numHandlers the number of method handler threads to run      * @param verbose whether each call should be logged      * @param portRangeConfig A config parameter that can be used to restrict      * the range of ports used when port is 0 (an ephemeral port)      */
DECL|method|Server (Class<?> protocolClass, Object protocolImpl, Configuration conf, String bindAddress, int port, int numHandlers, int numReaders, int queueSizePerHandler, boolean verbose, SecretManager<? extends TokenIdentifier> secretManager, String portRangeConfig)
specifier|public
name|Server
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocolClass
parameter_list|,
name|Object
name|protocolImpl
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|bindAddress
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|numHandlers
parameter_list|,
name|int
name|numReaders
parameter_list|,
name|int
name|queueSizePerHandler
parameter_list|,
name|boolean
name|verbose
parameter_list|,
name|SecretManager
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|,
name|String
name|portRangeConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|bindAddress
argument_list|,
name|port
argument_list|,
literal|null
argument_list|,
name|numHandlers
argument_list|,
name|numReaders
argument_list|,
name|queueSizePerHandler
argument_list|,
name|conf
argument_list|,
name|classNameBase
argument_list|(
name|protocolImpl
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|secretManager
argument_list|,
name|portRangeConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
name|registerProtocolAndImpl
argument_list|(
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|protocolClass
argument_list|,
name|protocolImpl
argument_list|)
expr_stmt|;
block|}
comment|/**      * Protobuf invoker for {@link RpcInvoker}      */
DECL|class|ProtoBufRpcInvoker
specifier|static
class|class
name|ProtoBufRpcInvoker
implements|implements
name|RpcInvoker
block|{
DECL|method|getProtocolImpl (RPC.Server server, String protoName, long version)
specifier|private
specifier|static
name|ProtoClassProtoImpl
name|getProtocolImpl
parameter_list|(
name|RPC
operator|.
name|Server
name|server
parameter_list|,
name|String
name|protoName
parameter_list|,
name|long
name|version
parameter_list|)
throws|throws
name|IOException
block|{
name|ProtoNameVer
name|pv
init|=
operator|new
name|ProtoNameVer
argument_list|(
name|protoName
argument_list|,
name|version
argument_list|)
decl_stmt|;
name|ProtoClassProtoImpl
name|impl
init|=
name|server
operator|.
name|getProtocolImplMap
argument_list|(
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|)
operator|.
name|get
argument_list|(
name|pv
argument_list|)
decl_stmt|;
if|if
condition|(
name|impl
operator|==
literal|null
condition|)
block|{
comment|// no match for Protocol AND Version
name|VerProtocolImpl
name|highest
init|=
name|server
operator|.
name|getHighestSupportedProtocol
argument_list|(
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|protoName
argument_list|)
decl_stmt|;
if|if
condition|(
name|highest
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown protocol: "
operator|+
name|protoName
argument_list|)
throw|;
block|}
comment|// protocol supported but not the version that client wants
throw|throw
operator|new
name|RPC
operator|.
name|VersionMismatch
argument_list|(
name|protoName
argument_list|,
name|version
argument_list|,
name|highest
operator|.
name|version
argument_list|)
throw|;
block|}
return|return
name|impl
return|;
block|}
annotation|@
name|Override
comment|/**        * This is a server side method, which is invoked over RPC. On success        * the return response has protobuf response payload. On failure, the        * exception name and the stack trace are return in the resposne.        * See {@link HadoopRpcResponseProto}        *         * In this method there three types of exceptions possible and they are        * returned in response as follows.        *<ol>        *<li> Exceptions encountered in this method that are returned         * as {@link RpcServerException}</li>        *<li> Exceptions thrown by the service is wrapped in ServiceException.         * In that this method returns in response the exception thrown by the         * service.</li>        *<li> Other exceptions thrown by the service. They are returned as        * it is.</li>        *</ol>        */
DECL|method|call (RPC.Server server, String protocol, Writable writableRequest, long receiveTime)
specifier|public
name|Writable
name|call
parameter_list|(
name|RPC
operator|.
name|Server
name|server
parameter_list|,
name|String
name|protocol
parameter_list|,
name|Writable
name|writableRequest
parameter_list|,
name|long
name|receiveTime
parameter_list|)
throws|throws
name|Exception
block|{
name|RpcRequestWritable
name|request
init|=
operator|(
name|RpcRequestWritable
operator|)
name|writableRequest
decl_stmt|;
name|HadoopRpcRequestProto
name|rpcRequest
init|=
name|request
operator|.
name|message
decl_stmt|;
name|String
name|methodName
init|=
name|rpcRequest
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|String
name|protoName
init|=
name|rpcRequest
operator|.
name|getDeclaringClassProtocolName
argument_list|()
decl_stmt|;
name|long
name|clientVersion
init|=
name|rpcRequest
operator|.
name|getClientProtocolVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|server
operator|.
name|verbose
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Call: protocol="
operator|+
name|protocol
operator|+
literal|", method="
operator|+
name|methodName
argument_list|)
expr_stmt|;
name|ProtoClassProtoImpl
name|protocolImpl
init|=
name|getProtocolImpl
argument_list|(
name|server
argument_list|,
name|protoName
argument_list|,
name|clientVersion
argument_list|)
decl_stmt|;
name|BlockingService
name|service
init|=
operator|(
name|BlockingService
operator|)
name|protocolImpl
operator|.
name|protocolImpl
decl_stmt|;
name|MethodDescriptor
name|methodDescriptor
init|=
name|service
operator|.
name|getDescriptorForType
argument_list|()
operator|.
name|findMethodByName
argument_list|(
name|methodName
argument_list|)
decl_stmt|;
if|if
condition|(
name|methodDescriptor
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"Unknown method "
operator|+
name|methodName
operator|+
literal|" called on "
operator|+
name|protocol
operator|+
literal|" protocol."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RpcServerException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|Message
name|prototype
init|=
name|service
operator|.
name|getRequestPrototype
argument_list|(
name|methodDescriptor
argument_list|)
decl_stmt|;
name|Message
name|param
init|=
name|prototype
operator|.
name|newBuilderForType
argument_list|()
operator|.
name|mergeFrom
argument_list|(
name|rpcRequest
operator|.
name|getRequest
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Message
name|result
decl_stmt|;
try|try
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|server
operator|.
name|rpcDetailedMetrics
operator|.
name|init
argument_list|(
name|protocolImpl
operator|.
name|protocolClass
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|callBlockingMethod
argument_list|(
name|methodDescriptor
argument_list|,
literal|null
argument_list|,
name|param
argument_list|)
expr_stmt|;
name|int
name|processingTime
init|=
call|(
name|int
call|)
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
decl_stmt|;
name|int
name|qTime
init|=
call|(
name|int
call|)
argument_list|(
name|startTime
operator|-
name|receiveTime
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Served: "
operator|+
name|methodName
operator|+
literal|" queueTime= "
operator|+
name|qTime
operator|+
literal|" procesingTime= "
operator|+
name|processingTime
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|rpcMetrics
operator|.
name|addRpcQueueTime
argument_list|(
name|qTime
argument_list|)
expr_stmt|;
name|server
operator|.
name|rpcMetrics
operator|.
name|addRpcProcessingTime
argument_list|(
name|processingTime
argument_list|)
expr_stmt|;
name|server
operator|.
name|rpcDetailedMetrics
operator|.
name|addProcessingTime
argument_list|(
name|methodName
argument_list|,
name|processingTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|Exception
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
return|return
operator|new
name|RpcResponseWritable
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

