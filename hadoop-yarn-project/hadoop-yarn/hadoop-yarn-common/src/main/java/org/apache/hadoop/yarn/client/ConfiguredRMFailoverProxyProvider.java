begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
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
name|Map
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
name|yarn
operator|.
name|conf
operator|.
name|HAUtil
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|ConfiguredRMFailoverProxyProvider
specifier|public
class|class
name|ConfiguredRMFailoverProxyProvider
parameter_list|<
name|T
parameter_list|>
implements|implements
name|RMFailoverProxyProvider
argument_list|<
name|T
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConfiguredRMFailoverProxyProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|currentProxyIndex
specifier|private
name|int
name|currentProxyIndex
init|=
literal|0
decl_stmt|;
DECL|field|proxies
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|proxies
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rmProxy
specifier|protected
name|RMProxy
argument_list|<
name|T
argument_list|>
name|rmProxy
decl_stmt|;
DECL|field|protocol
specifier|protected
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
decl_stmt|;
DECL|field|conf
specifier|protected
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|rmServiceIds
specifier|protected
name|String
index|[]
name|rmServiceIds
decl_stmt|;
annotation|@
name|Override
DECL|method|init (Configuration configuration, RMProxy<T> rmProxy, Class<T> protocol)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|RMProxy
argument_list|<
name|T
argument_list|>
name|rmProxy
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|)
block|{
name|this
operator|.
name|rmProxy
operator|=
name|rmProxy
expr_stmt|;
name|this
operator|.
name|protocol
operator|=
name|protocol
expr_stmt|;
name|this
operator|.
name|rmProxy
operator|.
name|checkAllowedProtocols
argument_list|(
name|this
operator|.
name|protocol
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|rmIds
init|=
name|HAUtil
operator|.
name|getRMHAIds
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|rmServiceIds
operator|=
name|rmIds
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|rmIds
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|rmServiceIds
index|[
name|currentProxyIndex
index|]
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_RETRIES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_CLIENT_FAILOVER_RETRIES
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_RETRIES_ON_SOCKET_TIMEOUTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_CLIENT_FAILOVER_RETRIES_ON_SOCKET_TIMEOUTS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getProxyInternal ()
specifier|protected
name|T
name|getProxyInternal
parameter_list|()
block|{
try|try
block|{
specifier|final
name|InetSocketAddress
name|rmAddress
init|=
name|rmProxy
operator|.
name|getRMAddress
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|)
decl_stmt|;
return|return
name|rmProxy
operator|.
name|getProxy
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|,
name|rmAddress
argument_list|)
return|;
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
literal|"Unable to create proxy to the ResourceManager "
operator|+
name|rmServiceIds
index|[
name|currentProxyIndex
index|]
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getProxy ()
specifier|public
specifier|synchronized
name|ProxyInfo
argument_list|<
name|T
argument_list|>
name|getProxy
parameter_list|()
block|{
name|String
name|rmId
init|=
name|rmServiceIds
index|[
name|currentProxyIndex
index|]
decl_stmt|;
name|T
name|current
init|=
name|proxies
operator|.
name|get
argument_list|(
name|rmId
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|current
operator|=
name|getProxyInternal
argument_list|()
expr_stmt|;
name|proxies
operator|.
name|put
argument_list|(
name|rmId
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ProxyInfo
argument_list|<
name|T
argument_list|>
argument_list|(
name|current
argument_list|,
name|rmId
argument_list|)
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
name|rmServiceIds
operator|.
name|length
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|rmServiceIds
index|[
name|currentProxyIndex
index|]
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Failing over to "
operator|+
name|rmServiceIds
index|[
name|currentProxyIndex
index|]
argument_list|)
expr_stmt|;
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
name|protocol
return|;
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
name|T
name|proxy
range|:
name|proxies
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|proxy
operator|instanceof
name|Closeable
condition|)
block|{
operator|(
operator|(
name|Closeable
operator|)
name|proxy
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
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

