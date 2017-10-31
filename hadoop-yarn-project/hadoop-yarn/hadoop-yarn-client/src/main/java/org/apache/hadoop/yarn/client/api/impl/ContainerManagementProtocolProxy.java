begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|LimitedPrivate
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
operator|.
name|Private
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
name|token
operator|.
name|SecretManager
operator|.
name|InvalidToken
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
name|api
operator|.
name|ContainerManagementProtocol
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|api
operator|.
name|records
operator|.
name|Token
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
name|client
operator|.
name|NMProxy
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
name|client
operator|.
name|api
operator|.
name|NMTokenCache
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
name|conf
operator|.
name|YarnConfiguration
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
name|YarnRuntimeException
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
name|YarnRPC
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
name|security
operator|.
name|NMTokenIdentifier
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
name|util
operator|.
name|ConverterUtils
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

begin_comment
comment|/**  * Helper class to manage container manager proxies  */
end_comment

begin_class
annotation|@
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|,
literal|"YARN"
block|}
argument_list|)
DECL|class|ContainerManagementProtocolProxy
specifier|public
class|class
name|ContainerManagementProtocolProxy
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ContainerManagementProtocolProxy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxConnectedNMs
specifier|private
specifier|final
name|int
name|maxConnectedNMs
decl_stmt|;
DECL|field|cmProxy
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ContainerManagementProtocolProxyData
argument_list|>
name|cmProxy
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|rpc
specifier|private
specifier|final
name|YarnRPC
name|rpc
decl_stmt|;
DECL|field|nmTokenCache
specifier|private
name|NMTokenCache
name|nmTokenCache
decl_stmt|;
DECL|method|ContainerManagementProtocolProxy (Configuration conf)
specifier|public
name|ContainerManagementProtocolProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|NMTokenCache
operator|.
name|getSingleton
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ContainerManagementProtocolProxy (Configuration conf, NMTokenCache nmTokenCache)
specifier|public
name|ContainerManagementProtocolProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NMTokenCache
name|nmTokenCache
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
name|nmTokenCache
operator|=
name|nmTokenCache
expr_stmt|;
name|maxConnectedNMs
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CLIENT_MAX_NM_PROXIES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CLIENT_MAX_NM_PROXIES
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxConnectedNMs
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CLIENT_MAX_NM_PROXIES
operator|+
literal|" ("
operator|+
name|maxConnectedNMs
operator|+
literal|") can not be less than 0."
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
name|LOG
operator|.
name|debug
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CLIENT_MAX_NM_PROXIES
operator|+
literal|" : "
operator|+
name|maxConnectedNMs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxConnectedNMs
operator|>
literal|0
condition|)
block|{
name|cmProxy
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|ContainerManagementProtocolProxyData
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|cmProxy
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
comment|// Connections are not being cached so ensure connections close quickly
comment|// to avoid creating thousands of RPC client threads on large clusters.
name|this
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|rpc
operator|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getProxy ( String containerManagerBindAddr, ContainerId containerId)
specifier|public
specifier|synchronized
name|ContainerManagementProtocolProxyData
name|getProxy
parameter_list|(
name|String
name|containerManagerBindAddr
parameter_list|,
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|InvalidToken
block|{
comment|// This get call will update the map which is working as LRU cache.
name|ContainerManagementProtocolProxyData
name|proxy
init|=
name|cmProxy
operator|.
name|get
argument_list|(
name|containerManagerBindAddr
argument_list|)
decl_stmt|;
while|while
condition|(
name|proxy
operator|!=
literal|null
operator|&&
operator|!
name|proxy
operator|.
name|token
operator|.
name|getIdentifier
argument_list|()
operator|.
name|equals
argument_list|(
name|nmTokenCache
operator|.
name|getToken
argument_list|(
name|containerManagerBindAddr
argument_list|)
operator|.
name|getIdentifier
argument_list|()
argument_list|)
condition|)
block|{
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
literal|"Refreshing proxy as NMToken got updated for node : "
operator|+
name|containerManagerBindAddr
argument_list|)
expr_stmt|;
block|}
comment|// Token is updated. check if anyone has already tried closing it.
if|if
condition|(
operator|!
name|proxy
operator|.
name|scheduledForClose
condition|)
block|{
comment|// try closing the proxy. Here if someone is already using it
comment|// then we might not close it. In which case we will wait.
name|removeProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|this
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|proxy
operator|.
name|activeCallers
operator|<
literal|0
condition|)
block|{
name|proxy
operator|=
name|cmProxy
operator|.
name|get
argument_list|(
name|containerManagerBindAddr
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|proxy
operator|==
literal|null
condition|)
block|{
name|proxy
operator|=
operator|new
name|ContainerManagementProtocolProxyData
argument_list|(
name|rpc
argument_list|,
name|containerManagerBindAddr
argument_list|,
name|containerId
argument_list|,
name|nmTokenCache
operator|.
name|getToken
argument_list|(
name|containerManagerBindAddr
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxConnectedNMs
operator|>
literal|0
condition|)
block|{
name|addProxyToCache
argument_list|(
name|containerManagerBindAddr
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
comment|// This is to track active users of this proxy.
name|proxy
operator|.
name|activeCallers
operator|++
expr_stmt|;
name|updateLRUCache
argument_list|(
name|containerManagerBindAddr
argument_list|)
expr_stmt|;
return|return
name|proxy
return|;
block|}
DECL|method|addProxyToCache (String containerManagerBindAddr, ContainerManagementProtocolProxyData proxy)
specifier|private
name|void
name|addProxyToCache
parameter_list|(
name|String
name|containerManagerBindAddr
parameter_list|,
name|ContainerManagementProtocolProxyData
name|proxy
parameter_list|)
block|{
while|while
condition|(
name|cmProxy
operator|.
name|size
argument_list|()
operator|>=
name|maxConnectedNMs
condition|)
block|{
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
literal|"Cleaning up the proxy cache, size="
operator|+
name|cmProxy
operator|.
name|size
argument_list|()
operator|+
literal|" max="
operator|+
name|maxConnectedNMs
argument_list|)
expr_stmt|;
block|}
name|boolean
name|removedProxy
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ContainerManagementProtocolProxyData
name|otherProxy
range|:
name|cmProxy
operator|.
name|values
argument_list|()
control|)
block|{
name|removedProxy
operator|=
name|removeProxy
argument_list|(
name|otherProxy
argument_list|)
expr_stmt|;
if|if
condition|(
name|removedProxy
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|removedProxy
condition|)
block|{
comment|// all of the proxies are currently in use and already scheduled
comment|// for removal, so we need to wait until at least one of them closes
try|try
block|{
name|this
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|maxConnectedNMs
operator|>
literal|0
condition|)
block|{
name|cmProxy
operator|.
name|put
argument_list|(
name|containerManagerBindAddr
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updateLRUCache (String containerManagerBindAddr)
specifier|private
name|void
name|updateLRUCache
parameter_list|(
name|String
name|containerManagerBindAddr
parameter_list|)
block|{
if|if
condition|(
name|maxConnectedNMs
operator|>
literal|0
condition|)
block|{
name|ContainerManagementProtocolProxyData
name|proxy
init|=
name|cmProxy
operator|.
name|remove
argument_list|(
name|containerManagerBindAddr
argument_list|)
decl_stmt|;
name|cmProxy
operator|.
name|put
argument_list|(
name|containerManagerBindAddr
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|mayBeCloseProxy ( ContainerManagementProtocolProxyData proxy)
specifier|public
specifier|synchronized
name|void
name|mayBeCloseProxy
parameter_list|(
name|ContainerManagementProtocolProxyData
name|proxy
parameter_list|)
block|{
name|tryCloseProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
DECL|method|tryCloseProxy ( ContainerManagementProtocolProxyData proxy)
specifier|private
name|boolean
name|tryCloseProxy
parameter_list|(
name|ContainerManagementProtocolProxyData
name|proxy
parameter_list|)
block|{
name|proxy
operator|.
name|activeCallers
operator|--
expr_stmt|;
if|if
condition|(
name|proxy
operator|.
name|scheduledForClose
operator|&&
name|proxy
operator|.
name|activeCallers
operator|<
literal|0
condition|)
block|{
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
literal|"Closing proxy : "
operator|+
name|proxy
operator|.
name|containerManagerBindAddr
argument_list|)
expr_stmt|;
block|}
name|cmProxy
operator|.
name|remove
argument_list|(
name|proxy
operator|.
name|containerManagerBindAddr
argument_list|)
expr_stmt|;
try|try
block|{
name|rpc
operator|.
name|stopProxy
argument_list|(
name|proxy
operator|.
name|getContainerManagementProtocol
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|removeProxy ( ContainerManagementProtocolProxyData proxy)
specifier|private
specifier|synchronized
name|boolean
name|removeProxy
parameter_list|(
name|ContainerManagementProtocolProxyData
name|proxy
parameter_list|)
block|{
if|if
condition|(
operator|!
name|proxy
operator|.
name|scheduledForClose
condition|)
block|{
name|proxy
operator|.
name|scheduledForClose
operator|=
literal|true
expr_stmt|;
return|return
name|tryCloseProxy
argument_list|(
name|proxy
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|stopAllProxies ()
specifier|public
specifier|synchronized
name|void
name|stopAllProxies
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nodeIds
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|nodeIds
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|cmProxy
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nodeId
range|:
name|nodeIds
control|)
block|{
name|ContainerManagementProtocolProxyData
name|proxy
init|=
name|cmProxy
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
comment|// Explicitly reducing the proxy count to allow stopping proxy.
name|proxy
operator|.
name|activeCallers
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|removeProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error closing connection"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
name|cmProxy
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|class|ContainerManagementProtocolProxyData
specifier|public
class|class
name|ContainerManagementProtocolProxyData
block|{
DECL|field|containerManagerBindAddr
specifier|private
specifier|final
name|String
name|containerManagerBindAddr
decl_stmt|;
DECL|field|proxy
specifier|private
specifier|final
name|ContainerManagementProtocol
name|proxy
decl_stmt|;
DECL|field|activeCallers
specifier|private
name|int
name|activeCallers
decl_stmt|;
DECL|field|scheduledForClose
specifier|private
name|boolean
name|scheduledForClose
decl_stmt|;
DECL|field|token
specifier|private
specifier|final
name|Token
name|token
decl_stmt|;
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|ContainerManagementProtocolProxyData (YarnRPC rpc, String containerManagerBindAddr, ContainerId containerId, Token token)
specifier|public
name|ContainerManagementProtocolProxyData
parameter_list|(
name|YarnRPC
name|rpc
parameter_list|,
name|String
name|containerManagerBindAddr
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Token
name|token
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|this
operator|.
name|containerManagerBindAddr
operator|=
name|containerManagerBindAddr
expr_stmt|;
empty_stmt|;
name|this
operator|.
name|activeCallers
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|scheduledForClose
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|proxy
operator|=
name|newProxy
argument_list|(
name|rpc
argument_list|,
name|containerManagerBindAddr
argument_list|,
name|containerId
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|newProxy (final YarnRPC rpc, String containerManagerBindAddr, ContainerId containerId, Token token)
specifier|protected
name|ContainerManagementProtocol
name|newProxy
parameter_list|(
specifier|final
name|YarnRPC
name|rpc
parameter_list|,
name|String
name|containerManagerBindAddr
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|Token
name|token
parameter_list|)
throws|throws
name|InvalidToken
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"No NMToken sent for "
operator|+
name|containerManagerBindAddr
argument_list|)
throw|;
block|}
specifier|final
name|InetSocketAddress
name|cmAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|containerManagerBindAddr
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
literal|"Opening proxy : "
operator|+
name|containerManagerBindAddr
argument_list|)
expr_stmt|;
block|}
comment|// the user in createRemoteUser in this context has to be ContainerID
name|UserGroupInformation
name|user
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
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
name|Token
argument_list|<
name|NMTokenIdentifier
argument_list|>
name|nmToken
init|=
name|ConverterUtils
operator|.
name|convertFromYarn
argument_list|(
name|token
argument_list|,
name|cmAddr
argument_list|)
decl_stmt|;
name|user
operator|.
name|addToken
argument_list|(
name|nmToken
argument_list|)
expr_stmt|;
return|return
name|NMProxy
operator|.
name|createNMProxy
argument_list|(
name|conf
argument_list|,
name|ContainerManagementProtocol
operator|.
name|class
argument_list|,
name|user
argument_list|,
name|rpc
argument_list|,
name|cmAddr
argument_list|)
return|;
block|}
DECL|method|getContainerManagementProtocol ()
specifier|public
name|ContainerManagementProtocol
name|getContainerManagementProtocol
parameter_list|()
block|{
return|return
name|proxy
return|;
block|}
block|}
block|}
end_class

end_unit

