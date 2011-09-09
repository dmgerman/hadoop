begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|Closeable
import|;
end_import

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
name|InvocationHandler
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
import|;
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
name|ProtocolProxy
import|;
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
name|RpcEngine
import|;
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
name|ClientCache
import|;
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
name|yarn
operator|.
name|exceptions
operator|.
name|impl
operator|.
name|pb
operator|.
name|YarnRemoteExceptionPBImpl
import|;
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
name|RpcProtos
operator|.
name|ProtoSpecificRpcRequest
import|;
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
name|RpcProtos
operator|.
name|ProtoSpecificRpcResponse
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

begin_class
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ProtoOverHadoopRpcEngine
specifier|public
class|class
name|ProtoOverHadoopRpcEngine
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
name|RPC
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|class|Invoker
specifier|private
specifier|static
class|class
name|Invoker
implements|implements
name|InvocationHandler
implements|,
name|Closeable
block|{
DECL|field|returnTypes
specifier|private
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
name|Client
operator|.
name|ConnectionId
name|remoteId
decl_stmt|;
DECL|field|client
specifier|private
name|Client
name|client
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
operator|.
name|remoteId
operator|=
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
name|ProtoSpecificResponseWritable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|constructRpcRequest (Method method, Object[] params)
specifier|private
name|ProtoSpecificRpcRequest
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
name|ProtoSpecificRpcRequest
name|rpcRequest
decl_stmt|;
name|ProtoSpecificRpcRequest
operator|.
name|Builder
name|builder
decl_stmt|;
name|builder
operator|=
name|ProtoSpecificRpcRequest
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
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
name|setRequestProto
argument_list|(
name|param
operator|.
name|toByteString
argument_list|()
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
name|Throwable
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
name|ProtoSpecificRpcRequest
name|rpcRequest
init|=
name|constructRpcRequest
argument_list|(
name|method
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|ProtoSpecificResponseWritable
name|val
init|=
literal|null
decl_stmt|;
try|try
block|{
name|val
operator|=
operator|(
name|ProtoSpecificResponseWritable
operator|)
name|client
operator|.
name|call
argument_list|(
operator|new
name|ProtoSpecificRequestWritable
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
name|ProtoSpecificRpcResponse
name|response
init|=
name|val
operator|.
name|message
decl_stmt|;
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
if|if
condition|(
name|response
operator|.
name|hasIsError
argument_list|()
operator|&&
name|response
operator|.
name|getIsError
argument_list|()
operator|==
literal|true
condition|)
block|{
name|YarnRemoteExceptionPBImpl
name|exception
init|=
operator|new
name|YarnRemoteExceptionPBImpl
argument_list|(
name|response
operator|.
name|getException
argument_list|()
argument_list|)
decl_stmt|;
name|exception
operator|.
name|fillInStackTrace
argument_list|()
expr_stmt|;
name|ServiceException
name|se
init|=
operator|new
name|ServiceException
argument_list|(
name|exception
argument_list|)
decl_stmt|;
throw|throw
name|se
throw|;
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
name|actualReturnMessage
init|=
name|prototype
operator|.
name|newBuilderForType
argument_list|()
operator|.
name|mergeFrom
argument_list|(
name|response
operator|.
name|getResponseProto
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|actualReturnMessage
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
else|else
block|{
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
block|}
block|}
comment|/**    * Writable Wrapper for Protocol Buffer Requests    */
DECL|class|ProtoSpecificRequestWritable
specifier|private
specifier|static
class|class
name|ProtoSpecificRequestWritable
implements|implements
name|Writable
block|{
DECL|field|message
name|ProtoSpecificRpcRequest
name|message
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|ProtoSpecificRequestWritable ()
specifier|public
name|ProtoSpecificRequestWritable
parameter_list|()
block|{     }
DECL|method|ProtoSpecificRequestWritable (ProtoSpecificRpcRequest message)
name|ProtoSpecificRequestWritable
parameter_list|(
name|ProtoSpecificRpcRequest
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
name|out
operator|.
name|writeInt
argument_list|(
name|message
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|message
operator|.
name|toByteArray
argument_list|()
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
name|message
operator|=
name|ProtoSpecificRpcRequest
operator|.
name|parseFrom
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Writable Wrapper for Protocol Buffer Responses    */
DECL|class|ProtoSpecificResponseWritable
specifier|public
specifier|static
class|class
name|ProtoSpecificResponseWritable
implements|implements
name|Writable
block|{
DECL|field|message
name|ProtoSpecificRpcResponse
name|message
decl_stmt|;
DECL|method|ProtoSpecificResponseWritable ()
specifier|public
name|ProtoSpecificResponseWritable
parameter_list|()
block|{     }
DECL|method|ProtoSpecificResponseWritable (ProtoSpecificRpcResponse message)
specifier|public
name|ProtoSpecificResponseWritable
parameter_list|(
name|ProtoSpecificRpcResponse
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
name|out
operator|.
name|writeInt
argument_list|(
name|message
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|message
operator|.
name|toByteArray
argument_list|()
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
name|message
operator|=
name|ProtoSpecificRpcResponse
operator|.
name|parseFrom
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
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
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// for unit testing only
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
name|ProtoSpecificResponseWritable
operator|.
name|class
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
DECL|field|service
specifier|private
name|BlockingService
name|service
decl_stmt|;
DECL|field|verbose
specifier|private
name|boolean
name|verbose
decl_stmt|;
comment|//
comment|//    /**
comment|//     * Construct an RPC server.
comment|//     *
comment|//     * @param instance
comment|//     *          the instance whose methods will be called
comment|//     * @param conf
comment|//     *          the configuration to use
comment|//     * @param bindAddress
comment|//     *          the address to bind on to listen for connection
comment|//     * @param port
comment|//     *          the port to listen for connections on
comment|//     */
comment|//    public Server(Object instance, Configuration conf, String bindAddress,
comment|//        int port) throws IOException {
comment|//      this(instance, conf, bindAddress, port, 1, false, null);
comment|//    }
DECL|method|classNameBase (String className)
specifier|private
specifier|static
name|String
name|classNameBase
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|String
index|[]
name|names
init|=
name|className
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|==
literal|null
operator|||
name|names
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|className
return|;
block|}
return|return
name|names
index|[
name|names
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
comment|/**      * Construct an RPC server.      *       * @param instance      *          the instance whose methods will be called      * @param conf      *          the configuration to use      * @param bindAddress      *          the address to bind on to listen for connection      * @param port      *          the port to listen for connections on      * @param numHandlers      *          the number of method handler threads to run      * @param verbose      *          whether each call should be logged      */
DECL|method|Server (Object instance, Configuration conf, String bindAddress, int port, int numHandlers, int numReaders, int queueSizePerHandler, boolean verbose, SecretManager<? extends TokenIdentifier> secretManager)
specifier|public
name|Server
parameter_list|(
name|Object
name|instance
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
name|ProtoSpecificRequestWritable
operator|.
name|class
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
name|instance
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|secretManager
argument_list|)
expr_stmt|;
name|this
operator|.
name|service
operator|=
operator|(
name|BlockingService
operator|)
name|instance
expr_stmt|;
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call (String protocol, Writable writableRequest, long receiveTime)
specifier|public
name|Writable
name|call
parameter_list|(
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
name|IOException
block|{
name|ProtoSpecificRequestWritable
name|request
init|=
operator|(
name|ProtoSpecificRequestWritable
operator|)
name|writableRequest
decl_stmt|;
name|ProtoSpecificRpcRequest
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
name|System
operator|.
name|out
operator|.
name|println
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
if|if
condition|(
name|verbose
condition|)
name|log
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
name|getRequestProto
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
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
name|handleException
argument_list|(
name|e
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|handleException
argument_list|(
name|e
argument_list|)
return|;
block|}
name|ProtoSpecificRpcResponse
name|response
init|=
name|constructProtoSpecificRpcSuccessResponse
argument_list|(
name|result
argument_list|)
decl_stmt|;
return|return
operator|new
name|ProtoSpecificResponseWritable
argument_list|(
name|response
argument_list|)
return|;
block|}
DECL|method|handleException (Throwable e)
specifier|private
name|ProtoSpecificResponseWritable
name|handleException
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|ProtoSpecificRpcResponse
operator|.
name|Builder
name|builder
init|=
name|ProtoSpecificRpcResponse
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setIsError
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|YarnRemoteExceptionPBImpl
condition|)
block|{
name|builder
operator|.
name|setException
argument_list|(
operator|(
operator|(
name|YarnRemoteExceptionPBImpl
operator|)
name|e
operator|.
name|getCause
argument_list|()
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setException
argument_list|(
operator|new
name|YarnRemoteExceptionPBImpl
argument_list|(
name|e
argument_list|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ProtoSpecificRpcResponse
name|response
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|ProtoSpecificResponseWritable
argument_list|(
name|response
argument_list|)
return|;
block|}
DECL|method|constructProtoSpecificRpcSuccessResponse ( Message message)
specifier|private
name|ProtoSpecificRpcResponse
name|constructProtoSpecificRpcSuccessResponse
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|ProtoSpecificRpcResponse
name|res
init|=
name|ProtoSpecificRpcResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setResponseProto
argument_list|(
name|message
operator|.
name|toByteString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|res
return|;
block|}
block|}
DECL|method|log (String value)
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|55
condition|)
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|55
argument_list|)
operator|+
literal|"..."
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getServer (Class<?> protocol, Object instance, String bindAddress, int port, int numHandlers,int numReaders, int queueSizePerHandler, boolean verbose, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager)
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
name|instance
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Server
argument_list|(
name|instance
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
argument_list|)
return|;
block|}
block|}
end_class

end_unit

