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
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/** A simple RPC mechanism.  *  * A<i>protocol</i> is a Java interface.  All parameters and return types must  * be one of:  *  *<ul><li>a primitive type,<code>boolean</code>,<code>byte</code>,  *<code>char</code>,<code>short</code>,<code>int</code>,<code>long</code>,  *<code>float</code>,<code>double</code>, or<code>void</code>; or</li>  *  *<li>a {@link String}; or</li>  *  *<li>a {@link Writable}; or</li>  *  *<li>an array of the above types</li></ul>  *  * All methods in the protocol should throw only IOException.  No field data of  * the protocol instance is transmitted.  */
end_comment

begin_class
DECL|class|RPC
specifier|public
class|class
name|RPC
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
DECL|method|RPC ()
specifier|private
name|RPC
parameter_list|()
block|{}
comment|// no public ctor
comment|// cache of RpcEngines by protocol
DECL|field|PROTOCOL_ENGINES
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|RpcEngine
argument_list|>
name|PROTOCOL_ENGINES
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|RpcEngine
argument_list|>
argument_list|()
decl_stmt|;
comment|// track what RpcEngine is used by a proxy class, for stopProxy()
DECL|field|PROXY_ENGINES
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|,
name|RpcEngine
argument_list|>
name|PROXY_ENGINES
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|,
name|RpcEngine
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|ENGINE_PROP
specifier|private
specifier|static
specifier|final
name|String
name|ENGINE_PROP
init|=
literal|"rpc.engine"
decl_stmt|;
comment|// set a protocol to use a non-default RpcEngine
DECL|method|setProtocolEngine (Configuration conf, Class protocol, Class engine)
specifier|static
name|void
name|setProtocolEngine
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Class
name|protocol
parameter_list|,
name|Class
name|engine
parameter_list|)
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|ENGINE_PROP
operator|+
literal|"."
operator|+
name|protocol
operator|.
name|getName
argument_list|()
argument_list|,
name|engine
argument_list|,
name|RpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// return the RpcEngine configured to handle a protocol
DECL|method|getProtocolEngine (Class protocol, Configuration conf)
specifier|private
specifier|static
specifier|synchronized
name|RpcEngine
name|getProtocolEngine
parameter_list|(
name|Class
name|protocol
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|RpcEngine
name|engine
init|=
name|PROTOCOL_ENGINES
operator|.
name|get
argument_list|(
name|protocol
argument_list|)
decl_stmt|;
if|if
condition|(
name|engine
operator|==
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|impl
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|ENGINE_PROP
operator|+
literal|"."
operator|+
name|protocol
operator|.
name|getName
argument_list|()
argument_list|,
name|WritableRpcEngine
operator|.
name|class
argument_list|)
decl_stmt|;
name|engine
operator|=
operator|(
name|RpcEngine
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|impl
argument_list|,
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|protocol
operator|.
name|isInterface
argument_list|()
condition|)
name|PROXY_ENGINES
operator|.
name|put
argument_list|(
name|Proxy
operator|.
name|getProxyClass
argument_list|(
name|protocol
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|protocol
argument_list|)
argument_list|,
name|engine
argument_list|)
expr_stmt|;
name|PROTOCOL_ENGINES
operator|.
name|put
argument_list|(
name|protocol
argument_list|,
name|engine
argument_list|)
expr_stmt|;
block|}
return|return
name|engine
return|;
block|}
comment|// return the RpcEngine that handles a proxy object
DECL|method|getProxyEngine (Object proxy)
specifier|private
specifier|static
specifier|synchronized
name|RpcEngine
name|getProxyEngine
parameter_list|(
name|Object
name|proxy
parameter_list|)
block|{
return|return
name|PROXY_ENGINES
operator|.
name|get
argument_list|(
name|proxy
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * A version mismatch for the RPC protocol.    */
DECL|class|VersionMismatch
specifier|public
specifier|static
class|class
name|VersionMismatch
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0
decl_stmt|;
DECL|field|interfaceName
specifier|private
name|String
name|interfaceName
decl_stmt|;
DECL|field|clientVersion
specifier|private
name|long
name|clientVersion
decl_stmt|;
DECL|field|serverVersion
specifier|private
name|long
name|serverVersion
decl_stmt|;
comment|/**      * Create a version mismatch exception      * @param interfaceName the name of the protocol mismatch      * @param clientVersion the client's version of the protocol      * @param serverVersion the server's version of the protocol      */
DECL|method|VersionMismatch (String interfaceName, long clientVersion, long serverVersion)
specifier|public
name|VersionMismatch
parameter_list|(
name|String
name|interfaceName
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|long
name|serverVersion
parameter_list|)
block|{
name|super
argument_list|(
literal|"Protocol "
operator|+
name|interfaceName
operator|+
literal|" version mismatch. (client = "
operator|+
name|clientVersion
operator|+
literal|", server = "
operator|+
name|serverVersion
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|interfaceName
operator|=
name|interfaceName
expr_stmt|;
name|this
operator|.
name|clientVersion
operator|=
name|clientVersion
expr_stmt|;
name|this
operator|.
name|serverVersion
operator|=
name|serverVersion
expr_stmt|;
block|}
comment|/**      * Get the interface name      * @return the java class name       *          (eg. org.apache.hadoop.mapred.InterTrackerProtocol)      */
DECL|method|getInterfaceName ()
specifier|public
name|String
name|getInterfaceName
parameter_list|()
block|{
return|return
name|interfaceName
return|;
block|}
comment|/**      * Get the client's preferred version      */
DECL|method|getClientVersion ()
specifier|public
name|long
name|getClientVersion
parameter_list|()
block|{
return|return
name|clientVersion
return|;
block|}
comment|/**      * Get the server's agreed to version.      */
DECL|method|getServerVersion ()
specifier|public
name|long
name|getServerVersion
parameter_list|()
block|{
return|return
name|serverVersion
return|;
block|}
block|}
DECL|method|waitForProxy ( Class protocol, long clientVersion, InetSocketAddress addr, Configuration conf )
specifier|public
specifier|static
name|Object
name|waitForProxy
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
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|waitForProxy
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Get a proxy connection to a remote server    * @param protocol protocol class    * @param clientVersion client version    * @param addr remote address    * @param conf configuration to use    * @param timeout time in milliseconds before giving up    * @return the proxy    * @throws IOException if the far end through a RemoteException    */
DECL|method|waitForProxy (Class protocol, long clientVersion, InetSocketAddress addr, Configuration conf, long timeout)
specifier|public
specifier|static
name|Object
name|waitForProxy
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
name|Configuration
name|conf
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|IOException
name|ioe
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
return|return
name|getProxy
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ConnectException
name|se
parameter_list|)
block|{
comment|// namenode has not been started
name|LOG
operator|.
name|info
argument_list|(
literal|"Server at "
operator|+
name|addr
operator|+
literal|" not available yet, Zzzzz..."
argument_list|)
expr_stmt|;
name|ioe
operator|=
name|se
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|te
parameter_list|)
block|{
comment|// namenode is busy
name|LOG
operator|.
name|info
argument_list|(
literal|"Problem connecting to server: "
operator|+
name|addr
argument_list|)
expr_stmt|;
name|ioe
operator|=
name|te
expr_stmt|;
block|}
comment|// check if timed out
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|timeout
operator|>=
name|startTime
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
comment|// wait for retry
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// IGNORE
block|}
block|}
block|}
comment|/** Construct a client-side proxy object that implements the named protocol,    * talking to a server at the named address. */
DECL|method|getProxy (Class protocol, long clientVersion, InetSocketAddress addr, Configuration conf, SocketFactory factory)
specifier|public
specifier|static
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
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
return|return
name|getProxy
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|)
return|;
block|}
comment|/** Construct a client-side proxy object that implements the named protocol,    * talking to a server at the named address. */
DECL|method|getProxy (Class protocol, long clientVersion, InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory)
specifier|public
specifier|static
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
return|return
name|getProtocolEngine
argument_list|(
name|protocol
argument_list|,
name|conf
argument_list|)
operator|.
name|getProxy
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|ticket
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|)
return|;
block|}
comment|/**    * Construct a client-side proxy object with the default SocketFactory    *     * @param protocol    * @param clientVersion    * @param addr    * @param conf    * @return a proxy instance    * @throws IOException    */
DECL|method|getProxy (Class protocol, long clientVersion, InetSocketAddress addr, Configuration conf)
specifier|public
specifier|static
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
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getProxy
argument_list|(
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Stop this proxy and release its invoker's resource    * @param proxy the proxy to be stopped    */
DECL|method|stopProxy (Object proxy)
specifier|public
specifier|static
name|void
name|stopProxy
parameter_list|(
name|Object
name|proxy
parameter_list|)
block|{
name|RpcEngine
name|rpcEngine
decl_stmt|;
if|if
condition|(
name|proxy
operator|!=
literal|null
operator|&&
operator|(
name|rpcEngine
operator|=
name|getProxyEngine
argument_list|(
name|proxy
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|rpcEngine
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Expert: Make multiple, parallel calls to a set of servers.    * @deprecated Use {@link #call(Method, Object[][], InetSocketAddress[], UserGroupInformation, Configuration)} instead     */
annotation|@
name|Deprecated
DECL|method|call (Method method, Object[][] params, InetSocketAddress[] addrs, Configuration conf)
specifier|public
specifier|static
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
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|call
argument_list|(
name|method
argument_list|,
name|params
argument_list|,
name|addrs
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Expert: Make multiple, parallel calls to a set of servers. */
DECL|method|call (Method method, Object[][] params, InetSocketAddress[] addrs, UserGroupInformation ticket, Configuration conf)
specifier|public
specifier|static
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
return|return
name|getProtocolEngine
argument_list|(
name|method
operator|.
name|getDeclaringClass
argument_list|()
argument_list|,
name|conf
argument_list|)
operator|.
name|call
argument_list|(
name|method
argument_list|,
name|params
argument_list|,
name|addrs
argument_list|,
name|ticket
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Construct a server for a protocol implementation instance listening on a    * port and address.    * @deprecated protocol interface should be passed.    */
annotation|@
name|Deprecated
DECL|method|getServer (final Object instance, final String bindAddress, final int port, Configuration conf)
specifier|public
specifier|static
name|Server
name|getServer
parameter_list|(
specifier|final
name|Object
name|instance
parameter_list|,
specifier|final
name|String
name|bindAddress
parameter_list|,
specifier|final
name|int
name|port
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getServer
argument_list|(
name|instance
argument_list|,
name|bindAddress
argument_list|,
name|port
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
return|;
block|}
comment|/** Construct a server for a protocol implementation instance listening on a    * port and address.    * @deprecated protocol interface should be passed.    */
annotation|@
name|Deprecated
DECL|method|getServer (final Object instance, final String bindAddress, final int port, final int numHandlers, final boolean verbose, Configuration conf)
specifier|public
specifier|static
name|Server
name|getServer
parameter_list|(
specifier|final
name|Object
name|instance
parameter_list|,
specifier|final
name|String
name|bindAddress
parameter_list|,
specifier|final
name|int
name|port
parameter_list|,
specifier|final
name|int
name|numHandlers
parameter_list|,
specifier|final
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
name|getServer
argument_list|(
name|instance
operator|.
name|getClass
argument_list|()
argument_list|,
comment|// use impl class for protocol
name|instance
argument_list|,
name|bindAddress
argument_list|,
name|port
argument_list|,
name|numHandlers
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Construct a server for a protocol implementation instance. */
DECL|method|getServer (Class protocol, Object instance, String bindAddress, int port, Configuration conf)
specifier|public
specifier|static
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
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getServer
argument_list|(
name|protocol
argument_list|,
name|instance
argument_list|,
name|bindAddress
argument_list|,
name|port
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Construct a server for a protocol implementation instance.    * @deprecated secretManager should be passed.    */
annotation|@
name|Deprecated
DECL|method|getServer (Class protocol, Object instance, String bindAddress, int port, int numHandlers, boolean verbose, Configuration conf)
specifier|public
specifier|static
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
name|getServer
argument_list|(
name|protocol
argument_list|,
name|instance
argument_list|,
name|bindAddress
argument_list|,
name|port
argument_list|,
name|numHandlers
argument_list|,
name|verbose
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Construct a server for a protocol implementation instance. */
DECL|method|getServer (Class<?> protocol, Object instance, String bindAddress, int port, int numHandlers, boolean verbose, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager)
specifier|public
specifier|static
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
name|getProtocolEngine
argument_list|(
name|protocol
argument_list|,
name|conf
argument_list|)
operator|.
name|getServer
argument_list|(
name|protocol
argument_list|,
name|instance
argument_list|,
name|bindAddress
argument_list|,
name|port
argument_list|,
name|numHandlers
argument_list|,
name|verbose
argument_list|,
name|conf
argument_list|,
name|secretManager
argument_list|)
return|;
block|}
comment|/** An RPC Server. */
DECL|class|Server
specifier|public
specifier|abstract
specifier|static
class|class
name|Server
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|Server
block|{
DECL|method|Server (String bindAddress, int port, Class<? extends Writable> paramClass, int handlerCount, Configuration conf, String serverName, SecretManager<? extends TokenIdentifier> secretManager)
specifier|protected
name|Server
parameter_list|(
name|String
name|bindAddress
parameter_list|,
name|int
name|port
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Writable
argument_list|>
name|paramClass
parameter_list|,
name|int
name|handlerCount
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|serverName
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
name|paramClass
argument_list|,
name|handlerCount
argument_list|,
name|conf
argument_list|,
name|serverName
argument_list|,
name|secretManager
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

