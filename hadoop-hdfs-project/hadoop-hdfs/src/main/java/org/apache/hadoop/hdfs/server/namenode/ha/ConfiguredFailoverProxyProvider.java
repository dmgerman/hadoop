begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
package|package
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
name|ha
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Configurable
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|DFSConfigKeys
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
name|DFSUtil
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
name|ClientProtocol
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
name|protocol
operator|.
name|NamenodeProtocol
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
name|protocol
operator|.
name|NamenodeProtocols
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
name|retry
operator|.
name|FailoverProxyProvider
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
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * A FailoverProxyProvider implementation which allows one to configure two URIs  * to connect to during fail-over. The first configured address is tried first,  * and on a fail-over event the other address is tried.  */
end_comment

begin_class
DECL|class|ConfiguredFailoverProxyProvider
specifier|public
class|class
name|ConfiguredFailoverProxyProvider
parameter_list|<
name|T
parameter_list|>
implements|implements
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
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
name|ConfiguredFailoverProxyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|proxies
specifier|private
specifier|final
name|List
argument_list|<
name|AddressRpcProxyPair
argument_list|<
name|T
argument_list|>
argument_list|>
name|proxies
init|=
operator|new
name|ArrayList
argument_list|<
name|AddressRpcProxyPair
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|ugi
specifier|private
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|xface
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|xface
decl_stmt|;
DECL|field|currentProxyIndex
specifier|private
name|int
name|currentProxyIndex
init|=
literal|0
decl_stmt|;
DECL|method|ConfiguredFailoverProxyProvider (Configuration conf, URI uri, Class<T> xface)
specifier|public
name|ConfiguredFailoverProxyProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|xface
operator|.
name|isAssignableFrom
argument_list|(
name|NamenodeProtocols
operator|.
name|class
argument_list|)
argument_list|,
literal|"Interface class %s is not a valid NameNode protocol!"
argument_list|)
expr_stmt|;
name|this
operator|.
name|xface
operator|=
name|xface
expr_stmt|;
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|maxRetries
init|=
name|this
operator|.
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_FAILOVER_CONNECTION_RETRIES_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_FAILOVER_CONNECTION_RETRIES_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|int
name|maxRetriesOnSocketTimeouts
init|=
name|this
operator|.
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_FAILOVER_CONNECTION_RETRIES_ON_SOCKET_TIMEOUTS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_FAILOVER_CONNECTION_RETRIES_ON_SOCKET_TIMEOUTS_DEFAULT
argument_list|)
decl_stmt|;
name|this
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
argument_list|,
name|maxRetriesOnSocketTimeouts
argument_list|)
expr_stmt|;
try|try
block|{
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
argument_list|>
name|map
init|=
name|DFSUtil
operator|.
name|getHaNnRpcAddresses
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|InetSocketAddress
argument_list|>
name|addressesInNN
init|=
name|map
operator|.
name|get
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|addressesInNN
operator|==
literal|null
operator|||
name|addressesInNN
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not find any configured addresses "
operator|+
literal|"for URI "
operator|+
name|uri
argument_list|)
throw|;
block|}
for|for
control|(
name|InetSocketAddress
name|address
range|:
name|addressesInNN
operator|.
name|values
argument_list|()
control|)
block|{
name|proxies
operator|.
name|add
argument_list|(
operator|new
name|AddressRpcProxyPair
argument_list|<
name|T
argument_list|>
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
argument_list|<
name|T
argument_list|>
name|getInterface
parameter_list|()
block|{
return|return
name|xface
return|;
block|}
comment|/**    * Lazily initialize the RPC proxy object.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|getProxy ()
specifier|public
specifier|synchronized
name|T
name|getProxy
parameter_list|()
block|{
name|AddressRpcProxyPair
name|current
init|=
name|proxies
operator|.
name|get
argument_list|(
name|currentProxyIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|namenode
operator|==
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
name|NamenodeProtocol
operator|.
name|class
operator|.
name|equals
argument_list|(
name|xface
argument_list|)
condition|)
block|{
name|current
operator|.
name|namenode
operator|=
name|DFSUtil
operator|.
name|createNNProxyWithNamenodeProtocol
argument_list|(
name|current
operator|.
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ClientProtocol
operator|.
name|class
operator|.
name|equals
argument_list|(
name|xface
argument_list|)
condition|)
block|{
comment|// TODO(HA): This will create a NN proxy with an underlying retry
comment|// proxy. We don't want this.
name|current
operator|.
name|namenode
operator|=
name|DFSUtil
operator|.
name|createNamenode
argument_list|(
name|current
operator|.
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Upsupported protocol found when creating the proxy conection to NameNode. "
operator|+
operator|(
operator|(
name|xface
operator|!=
literal|null
operator|)
condition|?
name|xface
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
else|:
name|xface
operator|)
operator|+
literal|" is not supported by "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create RPC proxy to NameNode"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|(
name|T
operator|)
name|current
operator|.
name|namenode
return|;
block|}
annotation|@
name|Override
DECL|method|performFailover (T currentProxy)
specifier|public
specifier|synchronized
name|void
name|performFailover
parameter_list|(
name|T
name|currentProxy
parameter_list|)
block|{
name|currentProxyIndex
operator|=
operator|(
name|currentProxyIndex
operator|+
literal|1
operator|)
operator|%
name|proxies
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|/**    * A little pair object to store the address and connected RPC proxy object to    * an NN. Note that {@link AddressRpcProxyPair#namenode} may be null.    */
DECL|class|AddressRpcProxyPair
specifier|private
specifier|static
class|class
name|AddressRpcProxyPair
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|address
specifier|public
name|InetSocketAddress
name|address
decl_stmt|;
DECL|field|namenode
specifier|public
name|T
name|namenode
decl_stmt|;
DECL|method|AddressRpcProxyPair (InetSocketAddress address)
specifier|public
name|AddressRpcProxyPair
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
block|}
block|}
comment|/**    * Close all the proxy objects which have been opened over the lifetime of    * this proxy provider.    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|AddressRpcProxyPair
argument_list|<
name|T
argument_list|>
name|proxy
range|:
name|proxies
control|)
block|{
if|if
condition|(
name|proxy
operator|.
name|namenode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|proxy
operator|.
name|namenode
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|proxy
operator|.
name|namenode
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
operator|.
name|namenode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

