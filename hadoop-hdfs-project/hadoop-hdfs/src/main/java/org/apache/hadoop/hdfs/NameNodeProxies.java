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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
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
name|Constructor
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
name|hdfs
operator|.
name|DFSClient
operator|.
name|Conf
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
name|AlreadyBeingCreatedException
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
name|protocol
operator|.
name|HdfsConstants
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
name|ClientNamenodeProtocolPB
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
name|ClientNamenodeProtocolTranslatorPB
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|SafeModeException
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
name|DefaultFailoverProxyProvider
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
name|io
operator|.
name|retry
operator|.
name|RetryUtils
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
name|RemoteException
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
comment|/**  * Create proxy objects to communicate with a remote NN. All remote access to an  * NN should be funneled through this class. Most of the time you'll want to use  * {@link NameNodeProxies#createProxy(Configuration, URI, Class)}, which will  * create either an HA- or non-HA-enabled client proxy as appropriate.  */
end_comment

begin_class
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
comment|/**    * Wrapper for a client proxy as well as its associated service ID.    * This is simply used as a tuple-like return type for    * {@link NameNodeProxies#createProxy} and    * {@link NameNodeProxies#createNonHAProxy}.    */
DECL|class|ProxyAndInfo
specifier|public
specifier|static
class|class
name|ProxyAndInfo
parameter_list|<
name|PROXYTYPE
parameter_list|>
block|{
DECL|field|proxy
specifier|private
specifier|final
name|PROXYTYPE
name|proxy
decl_stmt|;
DECL|field|dtService
specifier|private
specifier|final
name|Text
name|dtService
decl_stmt|;
DECL|method|ProxyAndInfo (PROXYTYPE proxy, Text dtService)
specifier|public
name|ProxyAndInfo
parameter_list|(
name|PROXYTYPE
name|proxy
parameter_list|,
name|Text
name|dtService
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|this
operator|.
name|dtService
operator|=
name|dtService
expr_stmt|;
block|}
DECL|method|getProxy ()
specifier|public
name|PROXYTYPE
name|getProxy
parameter_list|()
block|{
return|return
name|proxy
return|;
block|}
DECL|method|getDelegationTokenService ()
specifier|public
name|Text
name|getDelegationTokenService
parameter_list|()
block|{
return|return
name|dtService
return|;
block|}
block|}
comment|/**    * Creates the namenode proxy with the passed protocol. This will handle    * creation of either HA- or non-HA-enabled proxy objects, depending upon    * if the provided URI is a configured logical URI.    *     * @param conf the configuration containing the required IPC    *        properties, client failover configurations, etc.    * @param nameNodeUri the URI pointing either to a specific NameNode    *        or to a logical nameservice.    * @param xface the IPC interface which should be created    * @return an object containing both the proxy and the associated    *         delegation token service it corresponds to    * @throws IOException if there is an error creating the proxy    **/
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|Class
argument_list|<
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
argument_list|>
name|failoverProxyProviderClass
init|=
name|getFailoverProxyProviderClass
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|xface
argument_list|)
decl_stmt|;
if|if
condition|(
name|failoverProxyProviderClass
operator|==
literal|null
condition|)
block|{
comment|// Non-HA case
return|return
name|createNonHAProxy
argument_list|(
name|conf
argument_list|,
name|NameNode
operator|.
name|getAddress
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
argument_list|)
return|;
block|}
else|else
block|{
comment|// HA case
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
name|failoverProxyProvider
init|=
name|NameNodeProxies
operator|.
name|createFailoverProxyProvider
argument_list|(
name|conf
argument_list|,
name|failoverProxyProviderClass
argument_list|,
name|xface
argument_list|,
name|nameNodeUri
argument_list|)
decl_stmt|;
name|Conf
name|config
init|=
operator|new
name|Conf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|T
name|proxy
init|=
operator|(
name|T
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|xface
argument_list|,
name|failoverProxyProvider
argument_list|,
name|RetryPolicies
operator|.
name|failoverOnNetworkException
argument_list|(
name|RetryPolicies
operator|.
name|TRY_ONCE_THEN_FAIL
argument_list|,
name|config
operator|.
name|maxFailoverAttempts
argument_list|,
name|config
operator|.
name|failoverSleepBaseMillis
argument_list|,
name|config
operator|.
name|failoverSleepMaxMillis
argument_list|)
argument_list|)
decl_stmt|;
name|Text
name|dtService
init|=
name|HAUtil
operator|.
name|buildTokenServiceForLogicalUri
argument_list|(
name|nameNodeUri
argument_list|)
decl_stmt|;
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
argument_list|)
return|;
block|}
block|}
comment|/**    * Creates an explicitly non-HA-enabled proxy object. Most of the time you    * don't want to use this, and should instead use {@link NameNodeProxies#createProxy}.    *     * @param conf the configuration object    * @param nnAddr address of the remote NN to connect to    * @param xface the IPC interface which should be created    * @param ugi the user who is making the calls on the proxy object    * @param withRetries certain interfaces have a non-standard retry policy    * @return an object containing both the proxy and the associated    *         delegation token service it corresponds to    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|createNNProxyWithClientProtocol
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
else|else
block|{
name|String
name|message
init|=
literal|"Upsupported protocol found when creating the proxy "
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
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
name|exceptionToPolicyMap
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
argument_list|()
decl_stmt|;
name|RetryPolicy
name|methodPolicy
init|=
name|RetryPolicies
operator|.
name|retryByException
argument_list|(
name|timeoutPolicy
argument_list|,
name|exceptionToPolicyMap
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
name|methodPolicy
argument_list|)
expr_stmt|;
name|methodNameToPolicyMap
operator|.
name|put
argument_list|(
literal|"getAccessKeys"
argument_list|,
name|methodPolicy
argument_list|)
expr_stmt|;
name|proxy
operator|=
operator|(
name|NamenodeProtocolPB
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|NamenodeProtocolPB
operator|.
name|class
argument_list|,
name|proxy
argument_list|,
name|methodNameToPolicyMap
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NamenodeProtocolTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
block|}
DECL|method|createNNProxyWithClientProtocol ( InetSocketAddress address, Configuration conf, UserGroupInformation ugi, boolean withRetries)
specifier|private
specifier|static
name|ClientProtocol
name|createNNProxyWithClientProtocol
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
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|ClientNamenodeProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|RetryPolicy
name|defaultPolicy
init|=
name|RetryUtils
operator|.
name|getDefaultRetryPolicy
argument_list|(
name|conf
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_RETRY_POLICY_ENABLED_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_RETRY_POLICY_ENABLED_DEFAULT
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_RETRY_POLICY_SPEC_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_RETRY_POLICY_SPEC_DEFAULT
argument_list|,
name|SafeModeException
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|long
name|version
init|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|ClientNamenodeProtocolPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|ClientNamenodeProtocolPB
name|proxy
init|=
name|RPC
operator|.
name|getProtocolProxy
argument_list|(
name|ClientNamenodeProtocolPB
operator|.
name|class
argument_list|,
name|version
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
name|getTimeout
argument_list|(
name|conf
argument_list|)
argument_list|,
name|defaultPolicy
argument_list|)
operator|.
name|getProxy
argument_list|()
decl_stmt|;
if|if
condition|(
name|withRetries
condition|)
block|{
comment|// create the proxy with retries
name|RetryPolicy
name|createPolicy
init|=
name|RetryPolicies
operator|.
name|retryUpToMaximumCountWithFixedSleep
argument_list|(
literal|5
argument_list|,
name|HdfsConstants
operator|.
name|LEASE_SOFTLIMIT_PERIOD
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
name|remoteExceptionToPolicyMap
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
argument_list|()
decl_stmt|;
name|remoteExceptionToPolicyMap
operator|.
name|put
argument_list|(
name|AlreadyBeingCreatedException
operator|.
name|class
argument_list|,
name|createPolicy
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
name|exceptionToPolicyMap
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
argument_list|()
decl_stmt|;
name|exceptionToPolicyMap
operator|.
name|put
argument_list|(
name|RemoteException
operator|.
name|class
argument_list|,
name|RetryPolicies
operator|.
name|retryByRemoteException
argument_list|(
name|defaultPolicy
argument_list|,
name|remoteExceptionToPolicyMap
argument_list|)
argument_list|)
expr_stmt|;
name|RetryPolicy
name|methodPolicy
init|=
name|RetryPolicies
operator|.
name|retryByException
argument_list|(
name|defaultPolicy
argument_list|,
name|exceptionToPolicyMap
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
literal|"create"
argument_list|,
name|methodPolicy
argument_list|)
expr_stmt|;
name|proxy
operator|=
operator|(
name|ClientNamenodeProtocolPB
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|ClientNamenodeProtocolPB
operator|.
name|class
argument_list|,
operator|new
name|DefaultFailoverProxyProvider
argument_list|<
name|ClientNamenodeProtocolPB
argument_list|>
argument_list|(
name|ClientNamenodeProtocolPB
operator|.
name|class
argument_list|,
name|proxy
argument_list|)
argument_list|,
name|methodNameToPolicyMap
argument_list|,
name|defaultPolicy
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ClientNamenodeProtocolTranslatorPB
argument_list|(
name|proxy
argument_list|)
return|;
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
comment|/** Gets the configured Failover proxy provider's class */
DECL|method|getFailoverProxyProviderClass ( Configuration conf, URI nameNodeUri, Class<T> xface)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
argument_list|>
name|getFailoverProxyProviderClass
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
if|if
condition|(
name|nameNodeUri
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|host
init|=
name|nameNodeUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|configKey
init|=
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
operator|+
literal|"."
operator|+
name|host
decl_stmt|;
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
argument_list|>
name|ret
init|=
operator|(
name|Class
argument_list|<
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
argument_list|>
operator|)
name|conf
operator|.
name|getClass
argument_list|(
name|configKey
argument_list|,
literal|null
argument_list|,
name|FailoverProxyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|null
condition|)
block|{
comment|// If we found a proxy provider, then this URI should be a logical NN.
comment|// Given that, it shouldn't have a non-default port number.
name|int
name|port
init|=
name|nameNodeUri
operator|.
name|getPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|>
literal|0
operator|&&
name|port
operator|!=
name|NameNode
operator|.
name|DEFAULT_PORT
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Port "
operator|+
name|port
operator|+
literal|" specified in URI "
operator|+
name|nameNodeUri
operator|+
literal|" but host '"
operator|+
name|host
operator|+
literal|"' is a logical (HA) namenode"
operator|+
literal|" and does not use port information."
argument_list|)
throw|;
block|}
block|}
return|return
name|ret
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ClassNotFoundException
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not load failover proxy provider class "
operator|+
name|conf
operator|.
name|get
argument_list|(
name|configKey
argument_list|)
operator|+
literal|" which is configured for authority "
operator|+
name|nameNodeUri
argument_list|,
name|e
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|/** Creates the Failover proxy provider instance*/
DECL|method|createFailoverProxyProvider ( Configuration conf, Class<FailoverProxyProvider<T>> failoverProxyProviderClass, Class<T> xface, URI nameNodeUri)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
name|createFailoverProxyProvider
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Class
argument_list|<
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
argument_list|>
name|failoverProxyProviderClass
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|URI
name|nameNodeUri
parameter_list|)
throws|throws
name|IOException
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
literal|"Interface %s is not a NameNode protocol"
argument_list|,
name|xface
argument_list|)
expr_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
argument_list|>
name|ctor
init|=
name|failoverProxyProviderClass
operator|.
name|getConstructor
argument_list|(
name|Configuration
operator|.
name|class
argument_list|,
name|URI
operator|.
name|class
argument_list|,
name|Class
operator|.
name|class
argument_list|)
decl_stmt|;
name|FailoverProxyProvider
argument_list|<
name|T
argument_list|>
name|provider
init|=
name|ctor
operator|.
name|newInstance
argument_list|(
name|conf
argument_list|,
name|nameNodeUri
argument_list|,
name|xface
argument_list|)
decl_stmt|;
return|return
name|provider
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Couldn't create proxy provider "
operator|+
name|failoverProxyProviderClass
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
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

