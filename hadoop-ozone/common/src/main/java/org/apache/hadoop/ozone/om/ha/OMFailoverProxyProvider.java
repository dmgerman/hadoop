begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|ha
package|;
end_package

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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|OmUtils
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|om
operator|.
name|protocolPB
operator|.
name|OzoneManagerProtocolPB
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
name|HashMap
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_ADDRESS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_OM_SERVICE_IDS_KEY
import|;
end_import

begin_comment
comment|/**  * A failover proxy provider implementation which allows clients to configure  * multiple OMs to connect to. In case of OM failover, client can try  * connecting to another OM node from the list of proxies.  */
end_comment

begin_class
DECL|class|OMFailoverProxyProvider
specifier|public
class|class
name|OMFailoverProxyProvider
implements|implements
name|FailoverProxyProvider
argument_list|<
name|OzoneManagerProtocolPB
argument_list|>
implements|,
name|Closeable
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OMFailoverProxyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Map of OMNodeID to its proxy
DECL|field|omProxies
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ProxyInfo
argument_list|<
name|OzoneManagerProtocolPB
argument_list|>
argument_list|>
name|omProxies
decl_stmt|;
DECL|field|omProxyInfos
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|OMProxyInfo
argument_list|>
name|omProxyInfos
decl_stmt|;
DECL|field|omNodeIDList
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|omNodeIDList
decl_stmt|;
DECL|field|currentProxyOMNodeId
specifier|private
name|String
name|currentProxyOMNodeId
decl_stmt|;
DECL|field|currentProxyIndex
specifier|private
name|int
name|currentProxyIndex
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|omVersion
specifier|private
specifier|final
name|long
name|omVersion
decl_stmt|;
DECL|field|ugi
specifier|private
specifier|final
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|method|OMFailoverProxyProvider (OzoneConfiguration configuration, UserGroupInformation ugi)
specifier|public
name|OMFailoverProxyProvider
parameter_list|(
name|OzoneConfiguration
name|configuration
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|configuration
expr_stmt|;
name|this
operator|.
name|omVersion
operator|=
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|OzoneManagerProtocolPB
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|ugi
operator|=
name|ugi
expr_stmt|;
name|loadOMClientConfigs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|currentProxyIndex
operator|=
literal|0
expr_stmt|;
name|currentProxyOMNodeId
operator|=
name|omNodeIDList
operator|.
name|get
argument_list|(
name|currentProxyIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|loadOMClientConfigs (Configuration config)
specifier|private
name|void
name|loadOMClientConfigs
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|omProxies
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|omProxyInfos
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|omNodeIDList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|omServiceIds
init|=
name|config
operator|.
name|getTrimmedStringCollection
argument_list|(
name|OZONE_OM_SERVICE_IDS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|omServiceIds
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Multi-OM Services is not supported."
operator|+
literal|" Please configure only one OM Service ID in "
operator|+
name|OZONE_OM_SERVICE_IDS_KEY
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|serviceId
range|:
name|OmUtils
operator|.
name|emptyAsSingletonNull
argument_list|(
name|omServiceIds
argument_list|)
control|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|omNodeIds
init|=
name|OmUtils
operator|.
name|getOMNodeIds
argument_list|(
name|config
argument_list|,
name|serviceId
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|nodeId
range|:
name|OmUtils
operator|.
name|emptyAsSingletonNull
argument_list|(
name|omNodeIds
argument_list|)
control|)
block|{
name|String
name|rpcAddrKey
init|=
name|OmUtils
operator|.
name|addKeySuffixes
argument_list|(
name|OZONE_OM_ADDRESS_KEY
argument_list|,
name|serviceId
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
name|String
name|rpcAddrStr
init|=
name|OmUtils
operator|.
name|getOmRpcAddress
argument_list|(
name|config
argument_list|,
name|rpcAddrKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|rpcAddrStr
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|OMProxyInfo
name|omProxyInfo
init|=
operator|new
name|OMProxyInfo
argument_list|(
name|nodeId
argument_list|,
name|rpcAddrStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|omProxyInfo
operator|.
name|getAddress
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ProxyInfo
argument_list|<
name|OzoneManagerProtocolPB
argument_list|>
name|proxyInfo
init|=
operator|new
name|ProxyInfo
argument_list|(
literal|null
argument_list|,
name|omProxyInfo
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// For a non-HA OM setup, nodeId might be null. If so, we assign it
comment|// a dummy value
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
block|{
name|nodeId
operator|=
name|OzoneConsts
operator|.
name|OM_NODE_ID_DUMMY
expr_stmt|;
block|}
name|omProxies
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|proxyInfo
argument_list|)
expr_stmt|;
name|omProxyInfos
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|omProxyInfo
argument_list|)
expr_stmt|;
name|omNodeIDList
operator|.
name|add
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create OM proxy for {} at address {}"
argument_list|,
name|nodeId
argument_list|,
name|rpcAddrStr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|omProxies
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not find any configured "
operator|+
literal|"addresses for OM. Please configure the system with "
operator|+
name|OZONE_OM_ADDRESS_KEY
argument_list|)
throw|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCurrentProxyOMNodeId ()
specifier|public
specifier|synchronized
name|String
name|getCurrentProxyOMNodeId
parameter_list|()
block|{
return|return
name|currentProxyOMNodeId
return|;
block|}
DECL|method|createOMProxy (InetSocketAddress omAddress)
specifier|private
name|OzoneManagerProtocolPB
name|createOMProxy
parameter_list|(
name|InetSocketAddress
name|omAddress
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
name|OzoneManagerProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|RPC
operator|.
name|getProxy
argument_list|(
name|OzoneManagerProtocolPB
operator|.
name|class
argument_list|,
name|omVersion
argument_list|,
name|omAddress
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
name|Client
operator|.
name|getRpcTimeout
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the proxy object which should be used until the next failover event    * occurs. RPC proxy object is intialized lazily.    * @return the OM proxy object to invoke methods upon    */
annotation|@
name|Override
DECL|method|getProxy ()
specifier|public
specifier|synchronized
name|ProxyInfo
name|getProxy
parameter_list|()
block|{
name|ProxyInfo
name|currentProxyInfo
init|=
name|omProxies
operator|.
name|get
argument_list|(
name|currentProxyOMNodeId
argument_list|)
decl_stmt|;
name|createOMProxyIfNeeded
argument_list|(
name|currentProxyInfo
argument_list|,
name|currentProxyOMNodeId
argument_list|)
expr_stmt|;
return|return
name|currentProxyInfo
return|;
block|}
comment|/**    * Creates proxy object if it does not already exist.    */
DECL|method|createOMProxyIfNeeded (ProxyInfo proxyInfo, String nodeId)
specifier|private
name|void
name|createOMProxyIfNeeded
parameter_list|(
name|ProxyInfo
name|proxyInfo
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|proxyInfo
operator|.
name|proxy
operator|==
literal|null
condition|)
block|{
name|InetSocketAddress
name|address
init|=
name|omProxyInfos
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|.
name|getAddress
argument_list|()
decl_stmt|;
try|try
block|{
name|proxyInfo
operator|.
name|proxy
operator|=
name|createOMProxy
argument_list|(
name|address
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
literal|"{} Failed to create RPC proxy to OM at {}"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
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
block|}
DECL|method|getCurrentProxyDelegationToken ()
specifier|public
specifier|synchronized
name|Text
name|getCurrentProxyDelegationToken
parameter_list|()
block|{
return|return
name|omProxyInfos
operator|.
name|get
argument_list|(
name|currentProxyOMNodeId
argument_list|)
operator|.
name|getDelegationTokenService
argument_list|()
return|;
block|}
comment|/**    * Called whenever an error warrants failing over. It is determined by the    * retry policy.    */
annotation|@
name|Override
DECL|method|performFailover (OzoneManagerProtocolPB currentProxy)
specifier|public
name|void
name|performFailover
parameter_list|(
name|OzoneManagerProtocolPB
name|currentProxy
parameter_list|)
block|{
name|int
name|newProxyIndex
init|=
name|incrementProxyIndex
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failing over OM proxy to index: {}, nodeId: {}"
argument_list|,
name|newProxyIndex
argument_list|,
name|omNodeIDList
operator|.
name|get
argument_list|(
name|newProxyIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update the proxy index to the next proxy in the list.    * @return the new proxy index    */
DECL|method|incrementProxyIndex ()
specifier|private
specifier|synchronized
name|int
name|incrementProxyIndex
parameter_list|()
block|{
name|currentProxyIndex
operator|=
operator|(
name|currentProxyIndex
operator|+
literal|1
operator|)
operator|%
name|omProxies
operator|.
name|size
argument_list|()
expr_stmt|;
name|currentProxyOMNodeId
operator|=
name|omNodeIDList
operator|.
name|get
argument_list|(
name|currentProxyIndex
argument_list|)
expr_stmt|;
return|return
name|currentProxyIndex
return|;
block|}
annotation|@
name|Override
DECL|method|getInterface ()
specifier|public
name|Class
argument_list|<
name|OzoneManagerProtocolPB
argument_list|>
name|getInterface
parameter_list|()
block|{
return|return
name|OzoneManagerProtocolPB
operator|.
name|class
return|;
block|}
comment|/**    * Performs failover if the leaderOMNodeId returned through OMReponse does    * not match the current leaderOMNodeId cached by the proxy provider.    */
DECL|method|performFailoverIfRequired (String newLeaderOMNodeId)
specifier|public
name|void
name|performFailoverIfRequired
parameter_list|(
name|String
name|newLeaderOMNodeId
parameter_list|)
block|{
if|if
condition|(
name|newLeaderOMNodeId
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No suggested leader nodeId. Performing failover to next peer"
operator|+
literal|" node"
argument_list|)
expr_stmt|;
name|performFailover
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|updateLeaderOMNodeId
argument_list|(
name|newLeaderOMNodeId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failing over OM proxy to nodeId: {}"
argument_list|,
name|newLeaderOMNodeId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Failover to the OM proxy specified by the new leader OMNodeId.    * @param newLeaderOMNodeId OMNodeId to failover to.    * @return true if failover is successful, false otherwise.    */
DECL|method|updateLeaderOMNodeId (String newLeaderOMNodeId)
specifier|synchronized
name|boolean
name|updateLeaderOMNodeId
parameter_list|(
name|String
name|newLeaderOMNodeId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|currentProxyOMNodeId
operator|.
name|equals
argument_list|(
name|newLeaderOMNodeId
argument_list|)
condition|)
block|{
if|if
condition|(
name|omProxies
operator|.
name|containsKey
argument_list|(
name|newLeaderOMNodeId
argument_list|)
condition|)
block|{
name|currentProxyOMNodeId
operator|=
name|newLeaderOMNodeId
expr_stmt|;
name|currentProxyIndex
operator|=
name|omNodeIDList
operator|.
name|indexOf
argument_list|(
name|currentProxyOMNodeId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Close all the proxy objects which have been opened over the lifetime of    * the proxy provider.    */
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
name|ProxyInfo
argument_list|<
name|OzoneManagerProtocolPB
argument_list|>
name|proxy
range|:
name|omProxies
operator|.
name|values
argument_list|()
control|)
block|{
name|OzoneManagerProtocolPB
name|omProxy
init|=
name|proxy
operator|.
name|proxy
decl_stmt|;
if|if
condition|(
name|omProxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|omProxy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getOMProxies ()
specifier|public
name|List
argument_list|<
name|ProxyInfo
argument_list|>
name|getOMProxies
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|ProxyInfo
argument_list|>
argument_list|(
name|omProxies
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getOMProxyInfos ()
specifier|public
name|List
argument_list|<
name|OMProxyInfo
argument_list|>
name|getOMProxyInfos
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|OMProxyInfo
argument_list|>
argument_list|(
name|omProxyInfos
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

