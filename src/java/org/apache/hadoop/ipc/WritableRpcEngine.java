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
name|Array
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
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
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
name|net
operator|.
name|SocketTimeoutException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|HashMap
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|io
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
name|security
operator|.
name|authorize
operator|.
name|AuthorizationException
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
name|authorize
operator|.
name|ServiceAuthorizationManager
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
name|metrics
operator|.
name|util
operator|.
name|MetricsTimeVaryingRate
import|;
end_import

begin_comment
comment|/** An RpcEngine implementation for Writable data. */
end_comment

begin_class
DECL|class|WritableRpcEngine
class|class
name|WritableRpcEngine
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
comment|/** A method invocation, including the method name and its parameters.*/
DECL|class|Invocation
specifier|private
specifier|static
class|class
name|Invocation
implements|implements
name|Writable
implements|,
name|Configurable
block|{
DECL|field|methodName
specifier|private
name|String
name|methodName
decl_stmt|;
DECL|field|parameterClasses
specifier|private
name|Class
index|[]
name|parameterClasses
decl_stmt|;
DECL|field|parameters
specifier|private
name|Object
index|[]
name|parameters
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|Invocation ()
specifier|public
name|Invocation
parameter_list|()
block|{}
DECL|method|Invocation (Method method, Object[] parameters)
specifier|public
name|Invocation
parameter_list|(
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|methodName
operator|=
name|method
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|parameterClasses
operator|=
name|method
operator|.
name|getParameterTypes
argument_list|()
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
comment|/** The name of the method invoked. */
DECL|method|getMethodName ()
specifier|public
name|String
name|getMethodName
parameter_list|()
block|{
return|return
name|methodName
return|;
block|}
comment|/** The parameter classes. */
DECL|method|getParameterClasses ()
specifier|public
name|Class
index|[]
name|getParameterClasses
parameter_list|()
block|{
return|return
name|parameterClasses
return|;
block|}
comment|/** The parameter instances. */
DECL|method|getParameters ()
specifier|public
name|Object
index|[]
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
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
name|methodName
operator|=
name|UTF8
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|parameters
operator|=
operator|new
name|Object
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
expr_stmt|;
name|parameterClasses
operator|=
operator|new
name|Class
index|[
name|parameters
operator|.
name|length
index|]
expr_stmt|;
name|ObjectWritable
name|objectWritable
init|=
operator|new
name|ObjectWritable
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parameters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|parameters
index|[
name|i
index|]
operator|=
name|ObjectWritable
operator|.
name|readObject
argument_list|(
name|in
argument_list|,
name|objectWritable
argument_list|,
name|this
operator|.
name|conf
argument_list|)
expr_stmt|;
name|parameterClasses
index|[
name|i
index|]
operator|=
name|objectWritable
operator|.
name|getDeclaredClass
argument_list|()
expr_stmt|;
block|}
block|}
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
name|UTF8
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|methodName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|parameterClasses
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parameterClasses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ObjectWritable
operator|.
name|writeObject
argument_list|(
name|out
argument_list|,
name|parameters
index|[
name|i
index|]
argument_list|,
name|parameterClasses
index|[
name|i
index|]
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|methodName
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parameters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|parameters
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
block|}
comment|/* Cache a client using its socket factory as the hash key */
DECL|class|ClientCache
specifier|static
specifier|private
class|class
name|ClientCache
block|{
DECL|field|clients
specifier|private
name|Map
argument_list|<
name|SocketFactory
argument_list|,
name|Client
argument_list|>
name|clients
init|=
operator|new
name|HashMap
argument_list|<
name|SocketFactory
argument_list|,
name|Client
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Construct& cache an IPC client with the user-provided SocketFactory       * if no cached client exists.      *       * @param conf Configuration      * @return an IPC client      */
DECL|method|getClient (Configuration conf, SocketFactory factory)
specifier|private
specifier|synchronized
name|Client
name|getClient
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|)
block|{
comment|// Construct& cache client.  The configuration is only used for timeout,
comment|// and Clients have connection pools.  So we can either (a) lose some
comment|// connection pooling and leak sockets, or (b) use the same timeout for all
comment|// configurations.  Since the IPC is usually intended globally, not
comment|// per-job, we choose (a).
name|Client
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
name|factory
argument_list|)
decl_stmt|;
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
name|client
operator|=
operator|new
name|Client
argument_list|(
name|ObjectWritable
operator|.
name|class
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|clients
operator|.
name|put
argument_list|(
name|factory
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|client
operator|.
name|incCount
argument_list|()
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
comment|/**      * Construct& cache an IPC client with the default SocketFactory       * if no cached client exists.      *       * @param conf Configuration      * @return an IPC client      */
DECL|method|getClient (Configuration conf)
specifier|private
specifier|synchronized
name|Client
name|getClient
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getClient
argument_list|(
name|conf
argument_list|,
name|SocketFactory
operator|.
name|getDefault
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Stop a RPC client connection       * A RPC client is closed only when its reference count becomes zero.      */
DECL|method|stopClient (Client client)
specifier|private
name|void
name|stopClient
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|client
operator|.
name|decCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|client
operator|.
name|isZeroReference
argument_list|()
condition|)
block|{
name|clients
operator|.
name|remove
argument_list|(
name|client
operator|.
name|getSocketFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|client
operator|.
name|isZeroReference
argument_list|()
condition|)
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|CLIENTS
specifier|private
specifier|static
name|ClientCache
name|CLIENTS
init|=
operator|new
name|ClientCache
argument_list|()
decl_stmt|;
DECL|class|Invoker
specifier|private
specifier|static
class|class
name|Invoker
implements|implements
name|InvocationHandler
block|{
DECL|field|protocol
specifier|private
name|Class
name|protocol
decl_stmt|;
DECL|field|address
specifier|private
name|InetSocketAddress
name|address
decl_stmt|;
DECL|field|ticket
specifier|private
name|UserGroupInformation
name|ticket
decl_stmt|;
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
DECL|field|isClosed
specifier|private
name|boolean
name|isClosed
init|=
literal|false
decl_stmt|;
DECL|method|Invoker (Class protocol, InetSocketAddress address, UserGroupInformation ticket, Configuration conf, SocketFactory factory)
specifier|public
name|Invoker
parameter_list|(
name|Class
name|protocol
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|,
name|UserGroupInformation
name|ticket
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
name|protocol
operator|=
name|protocol
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
name|this
operator|.
name|ticket
operator|=
name|ticket
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
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|boolean
name|logDebug
init|=
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|logDebug
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
name|ObjectWritable
name|value
init|=
operator|(
name|ObjectWritable
operator|)
name|client
operator|.
name|call
argument_list|(
operator|new
name|Invocation
argument_list|(
name|method
argument_list|,
name|args
argument_list|)
argument_list|,
name|address
argument_list|,
name|protocol
argument_list|,
name|ticket
argument_list|)
decl_stmt|;
if|if
condition|(
name|logDebug
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
return|return
name|value
operator|.
name|get
argument_list|()
return|;
block|}
comment|/* close the IPC client that's responsible for this invoker's RPCs */
DECL|method|close ()
specifier|synchronized
specifier|private
name|void
name|close
parameter_list|()
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
block|}
comment|/** Construct a client-side proxy object that implements the named protocol,    * talking to a server at the named address. */
DECL|method|getProxy (Class protocol, long clientVersion, InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory)
specifier|public
name|Object
name|getProxy
parameter_list|(
name|Class
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
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|proxy
init|=
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
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|proxy
operator|instanceof
name|VersionedProtocol
condition|)
block|{
name|long
name|serverVersion
init|=
operator|(
operator|(
name|VersionedProtocol
operator|)
name|proxy
operator|)
operator|.
name|getProtocolVersion
argument_list|(
name|protocol
operator|.
name|getName
argument_list|()
argument_list|,
name|clientVersion
argument_list|)
decl_stmt|;
if|if
condition|(
name|serverVersion
operator|!=
name|clientVersion
condition|)
block|{
throw|throw
operator|new
name|RPC
operator|.
name|VersionMismatch
argument_list|(
name|protocol
operator|.
name|getName
argument_list|()
argument_list|,
name|clientVersion
argument_list|,
name|serverVersion
argument_list|)
throw|;
block|}
block|}
return|return
name|proxy
return|;
block|}
comment|/**    * Stop this proxy and release its invoker's resource    * @param proxy the proxy to be stopped    */
DECL|method|stopProxy (Object proxy)
specifier|public
name|void
name|stopProxy
parameter_list|(
name|Object
name|proxy
parameter_list|)
block|{
operator|(
operator|(
name|Invoker
operator|)
name|Proxy
operator|.
name|getInvocationHandler
argument_list|(
name|proxy
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Expert: Make multiple, parallel calls to a set of servers. */
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
block|{
name|Invocation
index|[]
name|invocations
init|=
operator|new
name|Invocation
index|[
name|params
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|invocations
index|[
name|i
index|]
operator|=
operator|new
name|Invocation
argument_list|(
name|method
argument_list|,
name|params
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
name|CLIENTS
operator|.
name|getClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|Writable
index|[]
name|wrappedValues
init|=
name|client
operator|.
name|call
argument_list|(
name|invocations
argument_list|,
name|addrs
argument_list|,
name|method
operator|.
name|getDeclaringClass
argument_list|()
argument_list|,
name|ticket
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|==
name|Void
operator|.
name|TYPE
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Object
index|[]
name|values
init|=
operator|(
name|Object
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|wrappedValues
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|wrappedValues
index|[
name|i
index|]
operator|!=
literal|null
condition|)
name|values
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|ObjectWritable
operator|)
name|wrappedValues
index|[
name|i
index|]
operator|)
operator|.
name|get
argument_list|()
expr_stmt|;
return|return
name|values
return|;
block|}
finally|finally
block|{
name|CLIENTS
operator|.
name|stopClient
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Construct a server for a protocol implementation instance listening on a    * port and address. */
DECL|method|getServer (Class protocol, Object instance, String bindAddress, int port, int numHandlers, boolean verbose, Configuration conf)
specifier|public
name|Server
name|getServer
parameter_list|(
name|Class
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
name|boolean
name|verbose
parameter_list|,
name|Configuration
name|conf
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
name|verbose
argument_list|)
return|;
block|}
comment|/** An RPC Server. */
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
DECL|field|instance
specifier|private
name|Object
name|instance
decl_stmt|;
DECL|field|verbose
specifier|private
name|boolean
name|verbose
decl_stmt|;
DECL|field|authorize
specifier|private
name|boolean
name|authorize
init|=
literal|false
decl_stmt|;
comment|/** Construct an RPC server.      * @param instance the instance whose methods will be called      * @param conf the configuration to use      * @param bindAddress the address to bind on to listen for connection      * @param port the port to listen for connections on      */
DECL|method|Server (Object instance, Configuration conf, String bindAddress, int port)
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
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|instance
argument_list|,
name|conf
argument_list|,
name|bindAddress
argument_list|,
name|port
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
comment|/** Construct an RPC server.      * @param instance the instance whose methods will be called      * @param conf the configuration to use      * @param bindAddress the address to bind on to listen for connection      * @param port the port to listen for connections on      * @param numHandlers the number of method handler threads to run      * @param verbose whether each call should be logged      */
DECL|method|Server (Object instance, Configuration conf, String bindAddress, int port, int numHandlers, boolean verbose)
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
name|boolean
name|verbose
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
name|Invocation
operator|.
name|class
argument_list|,
name|numHandlers
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|instance
operator|=
name|instance
expr_stmt|;
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
name|this
operator|.
name|authorize
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|ServiceAuthorizationManager
operator|.
name|SERVICE_AUTHORIZATION_CONFIG
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|call (Class<?> protocol, Writable param, long receivedTime)
specifier|public
name|Writable
name|call
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Writable
name|param
parameter_list|,
name|long
name|receivedTime
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Invocation
name|call
init|=
operator|(
name|Invocation
operator|)
name|param
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
name|log
argument_list|(
literal|"Call: "
operator|+
name|call
argument_list|)
expr_stmt|;
name|Method
name|method
init|=
name|protocol
operator|.
name|getMethod
argument_list|(
name|call
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|call
operator|.
name|getParameterClasses
argument_list|()
argument_list|)
decl_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|method
operator|.
name|invoke
argument_list|(
name|instance
argument_list|,
name|call
operator|.
name|getParameters
argument_list|()
argument_list|)
decl_stmt|;
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
name|receivedTime
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
name|debug
argument_list|(
literal|"Served: "
operator|+
name|call
operator|.
name|getMethodName
argument_list|()
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
name|rpcMetrics
operator|.
name|rpcQueueTime
operator|.
name|inc
argument_list|(
name|qTime
argument_list|)
expr_stmt|;
name|rpcMetrics
operator|.
name|rpcProcessingTime
operator|.
name|inc
argument_list|(
name|processingTime
argument_list|)
expr_stmt|;
name|MetricsTimeVaryingRate
name|m
init|=
operator|(
name|MetricsTimeVaryingRate
operator|)
name|rpcMetrics
operator|.
name|registry
operator|.
name|get
argument_list|(
name|call
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|m
operator|=
operator|new
name|MetricsTimeVaryingRate
argument_list|(
name|call
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|rpcMetrics
operator|.
name|registry
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// the metrics has been registered; re-fetch the handle
name|LOG
operator|.
name|info
argument_list|(
literal|"Error register "
operator|+
name|call
operator|.
name|getMethodName
argument_list|()
argument_list|,
name|iae
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|MetricsTimeVaryingRate
operator|)
name|rpcMetrics
operator|.
name|registry
operator|.
name|get
argument_list|(
name|call
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|m
operator|.
name|inc
argument_list|(
name|processingTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|verbose
condition|)
name|log
argument_list|(
literal|"Return: "
operator|+
name|value
argument_list|)
expr_stmt|;
return|return
operator|new
name|ObjectWritable
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|Throwable
name|target
init|=
name|e
operator|.
name|getTargetException
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|target
throw|;
block|}
else|else
block|{
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
name|target
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|setStackTrace
argument_list|(
name|target
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|e
operator|instanceof
name|IOException
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected throwable object "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|setStackTrace
argument_list|(
name|e
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|authorize (Subject user, ConnectionHeader connection)
specifier|public
name|void
name|authorize
parameter_list|(
name|Subject
name|user
parameter_list|,
name|ConnectionHeader
name|connection
parameter_list|)
throws|throws
name|AuthorizationException
block|{
if|if
condition|(
name|authorize
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
init|=
literal|null
decl_stmt|;
try|try
block|{
name|protocol
operator|=
name|getProtocolClass
argument_list|(
name|connection
operator|.
name|getProtocol
argument_list|()
argument_list|,
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cfne
parameter_list|)
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
literal|"Unknown protocol: "
operator|+
name|connection
operator|.
name|getProtocol
argument_list|()
argument_list|)
throw|;
block|}
name|ServiceAuthorizationManager
operator|.
name|authorize
argument_list|(
name|user
argument_list|,
name|protocol
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

