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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|DFSUtilClient
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
name|HAUtilClient
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
name|client
operator|.
name|HdfsClientConfigKeys
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
name|net
operator|.
name|DomainNameResolver
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
name|DomainNameResolverFactory
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|AbstractNNFailoverProxyProvider
specifier|public
specifier|abstract
class|class
name|AbstractNNFailoverProxyProvider
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
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractNNFailoverProxyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|xface
specifier|protected
name|Class
argument_list|<
name|T
argument_list|>
name|xface
decl_stmt|;
DECL|field|factory
specifier|protected
name|HAProxyFactory
argument_list|<
name|T
argument_list|>
name|factory
decl_stmt|;
DECL|field|ugi
specifier|protected
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|fallbackToSimpleAuth
specifier|protected
name|AtomicBoolean
name|fallbackToSimpleAuth
decl_stmt|;
DECL|method|AbstractNNFailoverProxyProvider ()
specifier|protected
name|AbstractNNFailoverProxyProvider
parameter_list|()
block|{   }
DECL|method|AbstractNNFailoverProxyProvider (Configuration conf, URI uri, Class<T> xface, HAProxyFactory<T> factory)
specifier|protected
name|AbstractNNFailoverProxyProvider
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
parameter_list|,
name|HAProxyFactory
argument_list|<
name|T
argument_list|>
name|factory
parameter_list|)
block|{
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
name|this
operator|.
name|xface
operator|=
name|xface
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
try|try
block|{
name|this
operator|.
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
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
name|int
name|maxRetries
init|=
name|this
operator|.
name|conf
operator|.
name|getInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|CONNECTION_RETRIES_KEY
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|CONNECTION_RETRIES_DEFAULT
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
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|CONNECTION_RETRIES_ON_SOCKET_TIMEOUTS_KEY
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|CONNECTION_RETRIES_ON_SOCKET_TIMEOUTS_DEFAULT
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
block|}
comment|/**    * Inquire whether logical HA URI is used for the implementation. If it is    * used, a special token handling may be needed to make sure a token acquired    * from a node in the HA pair can be used against the other node.    *    * @return true if logical HA URI is used. false, if not used.    */
DECL|method|useLogicalURI ()
specifier|public
specifier|abstract
name|boolean
name|useLogicalURI
parameter_list|()
function_decl|;
comment|/**    * Set for tracking if a secure client falls back to simple auth.  This method    * is synchronized only to stifle a Findbugs warning.    *    * @param fallbackToSimpleAuth - set to true or false during this method to    *   indicate if a secure client falls back to simple auth    */
DECL|method|setFallbackToSimpleAuth ( AtomicBoolean fallbackToSimpleAuth)
specifier|public
specifier|synchronized
name|void
name|setFallbackToSimpleAuth
parameter_list|(
name|AtomicBoolean
name|fallbackToSimpleAuth
parameter_list|)
block|{
name|this
operator|.
name|fallbackToSimpleAuth
operator|=
name|fallbackToSimpleAuth
expr_stmt|;
block|}
DECL|method|getFallbackToSimpleAuth ()
specifier|public
specifier|synchronized
name|AtomicBoolean
name|getFallbackToSimpleAuth
parameter_list|()
block|{
return|return
name|fallbackToSimpleAuth
return|;
block|}
comment|/**    * ProxyInfo to a NameNode. Includes its address.    */
DECL|class|NNProxyInfo
specifier|public
specifier|static
class|class
name|NNProxyInfo
parameter_list|<
name|T
parameter_list|>
extends|extends
name|ProxyInfo
argument_list|<
name|T
argument_list|>
block|{
DECL|field|address
specifier|private
name|InetSocketAddress
name|address
decl_stmt|;
comment|/**      * The currently known state of the NameNode represented by this ProxyInfo.      * This may be out of date if the NameNode has changed state since the last      * time the state was checked. If the NameNode could not be contacted, this      * will store null to indicate an unknown state.      */
DECL|field|cachedState
specifier|private
name|HAServiceState
name|cachedState
decl_stmt|;
DECL|method|NNProxyInfo (InetSocketAddress address)
specifier|public
name|NNProxyInfo
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|address
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
block|}
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|address
return|;
block|}
DECL|method|setCachedState (HAServiceState state)
specifier|public
name|void
name|setCachedState
parameter_list|(
name|HAServiceState
name|state
parameter_list|)
block|{
name|cachedState
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getCachedState ()
specifier|public
name|HAServiceState
name|getCachedState
parameter_list|()
block|{
return|return
name|cachedState
return|;
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
comment|/**    * Create a proxy if it has not been created yet.    */
DECL|method|createProxyIfNeeded (NNProxyInfo<T> pi)
specifier|protected
name|NNProxyInfo
argument_list|<
name|T
argument_list|>
name|createProxyIfNeeded
parameter_list|(
name|NNProxyInfo
argument_list|<
name|T
argument_list|>
name|pi
parameter_list|)
block|{
if|if
condition|(
name|pi
operator|.
name|proxy
operator|==
literal|null
condition|)
block|{
assert|assert
name|pi
operator|.
name|getAddress
argument_list|()
operator|!=
literal|null
operator|:
literal|"Proxy address is null"
assert|;
try|try
block|{
name|pi
operator|.
name|proxy
operator|=
name|factory
operator|.
name|createProxy
argument_list|(
name|conf
argument_list|,
name|pi
operator|.
name|getAddress
argument_list|()
argument_list|,
name|xface
argument_list|,
name|ugi
argument_list|,
literal|false
argument_list|,
name|getFallbackToSimpleAuth
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"{} Failed to create RPC proxy to NameNode at {}"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|pi
operator|.
name|address
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
return|return
name|pi
return|;
block|}
comment|/**    * Get list of configured NameNode proxy addresses.    * Randomize the list if requested.    */
DECL|method|getProxyAddresses (URI uri, String addressKey)
specifier|protected
name|List
argument_list|<
name|NNProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|getProxyAddresses
parameter_list|(
name|URI
name|uri
parameter_list|,
name|String
name|addressKey
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|NNProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|>
name|proxies
init|=
operator|new
name|ArrayList
argument_list|<
name|NNProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
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
name|DFSUtilClient
operator|.
name|getAddresses
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|,
name|addressKey
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
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|addressesOfNns
init|=
name|addressesInNN
operator|.
name|values
argument_list|()
decl_stmt|;
try|try
block|{
name|addressesOfNns
operator|=
name|getResolvedHostsIfNecessary
argument_list|(
name|addressesOfNns
argument_list|,
name|uri
argument_list|)
expr_stmt|;
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
for|for
control|(
name|InetSocketAddress
name|address
range|:
name|addressesOfNns
control|)
block|{
name|proxies
operator|.
name|add
argument_list|(
operator|new
name|NNProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Randomize the list to prevent all clients pointing to the same one
name|boolean
name|randomized
init|=
name|getRandomOrder
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomized
condition|)
block|{
name|Collections
operator|.
name|shuffle
argument_list|(
name|proxies
argument_list|)
expr_stmt|;
block|}
comment|// The client may have a delegation token set for the logical
comment|// URI of the cluster. Clone this token to apply to each of the
comment|// underlying IPC addresses so that the IPC code can find it.
name|HAUtilClient
operator|.
name|cloneDelegationTokenForLogicalUri
argument_list|(
name|ugi
argument_list|,
name|uri
argument_list|,
name|addressesOfNns
argument_list|)
expr_stmt|;
return|return
name|proxies
return|;
block|}
comment|/**    * If resolved is needed: for every domain name in the parameter list,    * resolve them into the actual IP addresses.    *    * @param addressesOfNns The domain name list from config.    * @param nameNodeUri The URI of namenode/nameservice.    * @return The collection of resolved IP addresses.    * @throws IOException If there are issues resolving the addresses.    */
DECL|method|getResolvedHostsIfNecessary ( Collection<InetSocketAddress> addressesOfNns, URI nameNodeUri)
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|getResolvedHostsIfNecessary
parameter_list|(
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|addressesOfNns
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|)
throws|throws
name|IOException
block|{
comment|// 'host' here is usually the ID of the nameservice when address
comment|// resolving is needed.
name|String
name|host
init|=
name|nameNodeUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|configKeyWithHost
init|=
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RESOLVE_ADDRESS_NEEDED_KEY
operator|+
literal|"."
operator|+
name|host
decl_stmt|;
name|boolean
name|resolveNeeded
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|configKeyWithHost
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RESOLVE_ADDRESS_NEEDED_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|resolveNeeded
condition|)
block|{
comment|// Early return is no resolve is necessary
return|return
name|addressesOfNns
return|;
block|}
comment|// decide whether to access server by IP or by host name
name|String
name|useFQDNKeyWithHost
init|=
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RESOLVE_ADDRESS_TO_FQDN
operator|+
literal|"."
operator|+
name|host
decl_stmt|;
name|boolean
name|requireFQDN
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|useFQDNKeyWithHost
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RESOLVE_ADDRESS_TO_FQDN_DEFAULT
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|InetSocketAddress
argument_list|>
name|addressOfResolvedNns
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|DomainNameResolver
name|dnr
init|=
name|DomainNameResolverFactory
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RESOLVE_SERVICE_KEY
argument_list|)
decl_stmt|;
comment|// If the address needs to be resolved, get all of the IP addresses
comment|// from this address and pass them into the proxy
name|LOG
operator|.
name|debug
argument_list|(
literal|"Namenode domain name will be resolved with {}"
argument_list|,
name|dnr
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|InetSocketAddress
name|address
range|:
name|addressesOfNns
control|)
block|{
name|String
index|[]
name|resolvedHostNames
init|=
name|dnr
operator|.
name|getAllResolvedHostnameByDomainName
argument_list|(
name|address
operator|.
name|getHostName
argument_list|()
argument_list|,
name|requireFQDN
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|address
operator|.
name|getPort
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|hostname
range|:
name|resolvedHostNames
control|)
block|{
name|InetSocketAddress
name|resolvedAddress
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|hostname
argument_list|,
name|port
argument_list|)
decl_stmt|;
name|addressOfResolvedNns
operator|.
name|add
argument_list|(
name|resolvedAddress
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|addressOfResolvedNns
return|;
block|}
comment|/**    * Check whether random order is configured for failover proxy provider    * for the namenode/nameservice.    *    * @param conf Configuration    * @param nameNodeUri The URI of namenode/nameservice    * @return random order configuration    */
DECL|method|getRandomOrder ( Configuration conf, URI nameNodeUri)
specifier|public
specifier|static
name|boolean
name|getRandomOrder
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|)
block|{
name|String
name|host
init|=
name|nameNodeUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|configKeyWithHost
init|=
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RANDOM_ORDER
operator|+
literal|"."
operator|+
name|host
decl_stmt|;
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|configKeyWithHost
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|configKeyWithHost
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RANDOM_ORDER_DEFAULT
argument_list|)
return|;
block|}
return|return
name|conf
operator|.
name|getBoolean
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RANDOM_ORDER
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|RANDOM_ORDER_DEFAULT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

