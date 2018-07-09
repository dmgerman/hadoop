begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|HashMap
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
name|TimeUnit
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
name|NameNodeProxiesClient
operator|.
name|ProxyAndInfo
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
name|protocolPB
operator|.
name|AliasMapProtocolPB
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
name|protocolPB
operator|.
name|InMemoryAliasMapProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|JournalProtocolPB
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
name|protocolPB
operator|.
name|JournalProtocolTranslatorPB
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
name|protocolPB
operator|.
name|NamenodeProtocolPB
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
name|protocolPB
operator|.
name|NamenodeProtocolTranslatorPB
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
name|aliasmap
operator|.
name|InMemoryAliasMapProtocol
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
name|ha
operator|.
name|AbstractNNFailoverProxyProvider
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
name|ha
operator|.
name|NameNodeHAProxyFactory
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
name|JournalProtocol
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
name|io
operator|.
name|Text
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
name|RetryPolicies
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
name|RetryPolicy
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
name|RetryProxy
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
name|ipc
operator|.
name|RefreshCallQueueProtocol
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
name|protocolPB
operator|.
name|RefreshCallQueueProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|RefreshCallQueueProtocolPB
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
name|SecurityUtil
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
name|RefreshAuthorizationPolicyProtocol
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
name|protocolPB
operator|.
name|RefreshAuthorizationPolicyProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|RefreshAuthorizationPolicyProtocolPB
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
name|protocolPB
operator|.
name|RefreshUserMappingsProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|RefreshUserMappingsProtocolPB
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|protocolPB
operator|.
name|GetUserMappingsProtocolClientSideTranslatorPB
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
name|protocolPB
operator|.
name|GetUserMappingsProtocolPB
import|;
end_import

begin_comment
comment|/**  * Create proxy objects to communicate with a remote NN. All remote access to an  * NN should be funneled through this class. Most of the time you'll want to use  * {@link NameNodeProxies#createProxy(Configuration, URI, Class)}, which will  * create either an HA- or non-HA-enabled client proxy as appropriate.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NameNodeProxies
specifier|public
class|class
name|NameNodeProxies
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
name|NameNodeProxies
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Creates the namenode proxy with the passed protocol. This will handle    * creation of either HA- or non-HA-enabled proxy objects, depending upon    * if the provided URI is a configured logical URI.    *     * @param conf the configuration containing the required IPC    *        properties, client failover configurations, etc.    * @param nameNodeUri the URI pointing either to a specific NameNode    *        or to a logical nameservice.    * @param xface the IPC interface which should be created    * @return an object containing both the proxy and the associated    *         delegation token service it corresponds to    * @throws IOException if there is an error creating the proxy    **/
DECL|method|createProxy (Configuration conf, URI nameNodeUri, Class<T> xface)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
name|createProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createProxy
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|xface
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Creates the namenode proxy with the passed protocol. This will handle    * creation of either HA- or non-HA-enabled proxy objects, depending upon    * if the provided URI is a configured logical URI.    *    * @param conf the configuration containing the required IPC    *        properties, client failover configurations, etc.    * @param nameNodeUri the URI pointing either to a specific NameNode    *        or to a logical nameservice.    * @param xface the IPC interface which should be created    * @param fallbackToSimpleAuth set to true or false during calls to indicate if    *   a secure client falls back to simple auth    * @return an object containing both the proxy and the associated    *         delegation token service it corresponds to    * @throws IOException if there is an error creating the proxy    **/
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createProxy (Configuration conf, URI nameNodeUri, Class<T> xface, AtomicBoolean fallbackToSimpleAuth)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
name|createProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|AtomicBoolean
name|fallbackToSimpleAuth
parameter_list|)
throws|throws
name|IOException
block|{
name|AbstractNNFailoverProxyProvider
argument_list|<
name|T
argument_list|>
name|failoverProxyProvider
init|=
name|NameNodeProxiesClient
operator|.
name|createFailoverProxyProvider
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|xface
argument_list|,
literal|true
argument_list|,
name|fallbackToSimpleAuth
argument_list|,
operator|new
name|NameNodeHAProxyFactory
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|failoverProxyProvider
operator|==
literal|null
condition|)
block|{
return|return
name|createNonHAProxy
argument_list|(
name|conf
argument_list|,
name|DFSUtilClient
operator|.
name|getNNAddress
argument_list|(
name|nameNodeUri
argument_list|)
argument_list|,
name|xface
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
literal|true
argument_list|,
name|fallbackToSimpleAuth
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|NameNodeProxiesClient
operator|.
name|createHAProxy
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|xface
argument_list|,
name|failoverProxyProvider
argument_list|)
return|;
block|}
block|}
comment|/**    * Creates an explicitly non-HA-enabled proxy object. Most of the time you    * don't want to use this, and should instead use {@link NameNodeProxies#createProxy}.    *     * @param conf the configuration object    * @param nnAddr address of the remote NN to connect to    * @param xface the IPC interface which should be created    * @param ugi the user who is making the calls on the proxy object    * @param withRetries certain interfaces have a non-standard retry policy    * @return an object containing both the proxy and the associated    *         delegation token service it corresponds to    * @throws IOException    */
DECL|method|createNonHAProxy ( Configuration conf, InetSocketAddress nnAddr, Class<T> xface, UserGroupInformation ugi, boolean withRetries)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
name|createNonHAProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|nnAddr
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|boolean
name|withRetries
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createNonHAProxy
argument_list|(
name|conf
argument_list|,
name|nnAddr
argument_list|,
name|xface
argument_list|,
name|ugi
argument_list|,
name|withRetries
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Creates an explicitly non-HA-enabled proxy object. Most of the time you    * don't want to use this, and should instead use {@link NameNodeProxies#createProxy}.    *    * @param conf the configuration object    * @param nnAddr address of the remote NN to connect to    * @param xface the IPC interface which should be created    * @param ugi the user who is making the calls on the proxy object    * @param withRetries certain interfaces have a non-standard retry policy    * @param fallbackToSimpleAuth - set to true or false during this method to    *   indicate if a secure client falls back to simple auth    * @return an object containing both the proxy and the associated    *         delegation token service it corresponds to    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createNonHAProxy ( Configuration conf, InetSocketAddress nnAddr, Class<T> xface, UserGroupInformation ugi, boolean withRetries, AtomicBoolean fallbackToSimpleAuth)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
name|createNonHAProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|InetSocketAddress
name|nnAddr
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|boolean
name|withRetries
parameter_list|,
name|AtomicBoolean
name|fallbackToSimpleAuth
parameter_list|)
throws|throws
name|IOException
block|{
name|Text
name|dtService
init|=
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|nnAddr
argument_list|)
decl_stmt|;
name|T
name|proxy
decl_stmt|;
if|if
condition|(
name|xface
operator|==
name|ClientProtocol
operator|.
name|class
condition|)
block|{
name|proxy
operator|=
operator|(
name|T
operator|)
name|NameNodeProxiesClient
operator|.
name|createNonHAProxyWithClientProtocol
argument_list|(
name|nnAddr
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|withRetries
argument_list|,
name|fallbackToSimpleAuth
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|xface
operator|==
name|JournalProtocol
operator|.
name|class
condition|)
block|{
name|proxy
operator|=
operator|(
name|T
operator|)
name|createNNProxyWithJournalProtocol
argument_list|(
name|nnAddr
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
name|xface
operator|==
name|NamenodeProtocol
operator|.
name|class
condition|)
block|{
name|proxy
operator|=
operator|(
name|T
operator|)
name|createNNProxyWithNamenodeProtocol
argument_list|(
name|nnAddr
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|withRetries
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|xface
operator|==
name|GetUserMappingsProtocol
operator|.
name|class
condition|)
block|{
name|proxy
operator|=
operator|(
name|T
operator|)
name|createNNProxyWithGetUserMappingsProtocol
argument_list|(
name|nnAddr
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
name|xface
operator|==
name|RefreshUserMappingsProtocol
operator|.
name|class
condition|)
block|{
name|proxy
operator|=
operator|(
name|T
operator|)
name|createNNProxyWithRefreshUserMappingsProtocol
argument_list|(
name|nnAddr
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
name|xface
operator|==
name|RefreshAuthorizationPolicyProtocol
operator|.
name|class
condition|)
block|{
name|proxy
operator|=
operator|(
name|T
operator|)
name|createNNProxyWithRefreshAuthorizationPolicyProtocol
argument_list|(
name|nnAddr
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
name|xface
operator|==
name|RefreshCallQueueProtocol
operator|.
name|class
condition|)
block|{
name|proxy
operator|=
operator|(
name|T
operator|)
name|createNNProxyWithRefreshCallQueueProtocol
argument_list|(
name|nnAddr
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
name|xface
operator|==
name|InMemoryAliasMapProtocol
operator|.
name|class
condition|)
block|{
name|proxy
operator|=
operator|(
name|T
operator|)
name|createNNProxyWithInMemoryAliasMapProtocol
argument_list|(
name|nnAddr
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|message
init|=
literal|"Unsupported protocol found when creating the proxy "
operator|+
literal|"connection to NameNode: "
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
literal|"null"
operator|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
operator|new
name|ProxyAndInfo
argument_list|<
name|T
argument_list|>
argument_list|(
name|proxy
argument_list|,
name|dtService
argument_list|,
name|nnAddr
argument_list|)
return|;
block|}
DECL|method|createNNProxyWithInMemoryAliasMapProtocol ( InetSocketAddress address, Configuration conf, UserGroupInformation ugi)
specifier|private
specifier|static
name|InMemoryAliasMapProtocol
name|createNNProxyWithInMemoryAliasMapProtocol
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|AliasMapProtocolPB
name|proxy
init|=
operator|(
name|AliasMapProtocolPB
operator|)
name|createNameNodeProxy
argument_list|(
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|AliasMapProtocolPB
operator|.
name|class
argument_list|,
literal|30000
argument_list|)
decl_stmt|;
return|return
operator|new
name|InMemoryAliasMapProtocolClientSideTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
DECL|method|createNNProxyWithJournalProtocol ( InetSocketAddress address, Configuration conf, UserGroupInformation ugi)
specifier|private
specifier|static
name|JournalProtocol
name|createNNProxyWithJournalProtocol
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|JournalProtocolPB
name|proxy
init|=
operator|(
name|JournalProtocolPB
operator|)
name|createNameNodeProxy
argument_list|(
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|JournalProtocolPB
operator|.
name|class
argument_list|,
literal|30000
argument_list|)
decl_stmt|;
return|return
operator|new
name|JournalProtocolTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RefreshAuthorizationPolicyProtocol
DECL|method|createNNProxyWithRefreshAuthorizationPolicyProtocol (InetSocketAddress address, Configuration conf, UserGroupInformation ugi)
name|createNNProxyWithRefreshAuthorizationPolicyProtocol
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|RefreshAuthorizationPolicyProtocolPB
name|proxy
init|=
operator|(
name|RefreshAuthorizationPolicyProtocolPB
operator|)
name|createNameNodeProxy
argument_list|(
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|RefreshAuthorizationPolicyProtocolPB
operator|.
name|class
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|RefreshAuthorizationPolicyProtocolClientSideTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RefreshUserMappingsProtocol
DECL|method|createNNProxyWithRefreshUserMappingsProtocol (InetSocketAddress address, Configuration conf, UserGroupInformation ugi)
name|createNNProxyWithRefreshUserMappingsProtocol
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|RefreshUserMappingsProtocolPB
name|proxy
init|=
operator|(
name|RefreshUserMappingsProtocolPB
operator|)
name|createNameNodeProxy
argument_list|(
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|RefreshUserMappingsProtocolPB
operator|.
name|class
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|RefreshUserMappingsProtocolClientSideTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RefreshCallQueueProtocol
DECL|method|createNNProxyWithRefreshCallQueueProtocol (InetSocketAddress address, Configuration conf, UserGroupInformation ugi)
name|createNNProxyWithRefreshCallQueueProtocol
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|RefreshCallQueueProtocolPB
name|proxy
init|=
operator|(
name|RefreshCallQueueProtocolPB
operator|)
name|createNameNodeProxy
argument_list|(
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|RefreshCallQueueProtocolPB
operator|.
name|class
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|RefreshCallQueueProtocolClientSideTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
DECL|method|createNNProxyWithGetUserMappingsProtocol ( InetSocketAddress address, Configuration conf, UserGroupInformation ugi)
specifier|private
specifier|static
name|GetUserMappingsProtocol
name|createNNProxyWithGetUserMappingsProtocol
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|GetUserMappingsProtocolPB
name|proxy
init|=
operator|(
name|GetUserMappingsProtocolPB
operator|)
name|createNameNodeProxy
argument_list|(
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|GetUserMappingsProtocolPB
operator|.
name|class
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|GetUserMappingsProtocolClientSideTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
DECL|method|createNNProxyWithNamenodeProtocol ( InetSocketAddress address, Configuration conf, UserGroupInformation ugi, boolean withRetries)
specifier|private
specifier|static
name|NamenodeProtocol
name|createNNProxyWithNamenodeProtocol
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|boolean
name|withRetries
parameter_list|)
throws|throws
name|IOException
block|{
name|NamenodeProtocolPB
name|proxy
init|=
operator|(
name|NamenodeProtocolPB
operator|)
name|createNameNodeProxy
argument_list|(
name|address
argument_list|,
name|conf
argument_list|,
name|ugi
argument_list|,
name|NamenodeProtocolPB
operator|.
name|class
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|withRetries
condition|)
block|{
comment|// create the proxy with retries
name|RetryPolicy
name|timeoutPolicy
init|=
name|RetryPolicies
operator|.
name|exponentialBackoffRetry
argument_list|(
literal|5
argument_list|,
literal|200
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RetryPolicy
argument_list|>
name|methodNameToPolicyMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RetryPolicy
argument_list|>
argument_list|()
decl_stmt|;
name|methodNameToPolicyMap
operator|.
name|put
argument_list|(
literal|"getBlocks"
argument_list|,
name|timeoutPolicy
argument_list|)
expr_stmt|;
name|methodNameToPolicyMap
operator|.
name|put
argument_list|(
literal|"getAccessKeys"
argument_list|,
name|timeoutPolicy
argument_list|)
expr_stmt|;
name|NamenodeProtocol
name|translatorProxy
init|=
operator|new
name|NamenodeProtocolTranslatorPB
argument_list|(
name|proxy
argument_list|)
decl_stmt|;
return|return
operator|(
name|NamenodeProtocol
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|NamenodeProtocol
operator|.
name|class
argument_list|,
name|translatorProxy
argument_list|,
name|methodNameToPolicyMap
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|NamenodeProtocolTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
block|}
DECL|method|createNameNodeProxy (InetSocketAddress address, Configuration conf, UserGroupInformation ugi, Class<?> xface, int rpcTimeout)
specifier|private
specifier|static
name|Object
name|createNameNodeProxy
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|xface
parameter_list|,
name|int
name|rpcTimeout
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
name|xface
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|Object
name|proxy
init|=
name|RPC
operator|.
name|getProxy
argument_list|(
name|xface
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|xface
argument_list|)
argument_list|,
name|address
argument_list|,
name|ugi
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
name|rpcTimeout
argument_list|)
decl_stmt|;
return|return
name|proxy
return|;
block|}
block|}
end_class

end_unit

