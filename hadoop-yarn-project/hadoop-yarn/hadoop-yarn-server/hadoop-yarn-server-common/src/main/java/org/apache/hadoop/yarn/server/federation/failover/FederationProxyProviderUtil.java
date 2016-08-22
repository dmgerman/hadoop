begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.failover
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|failover
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
operator|.
name|Unstable
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
name|security
operator|.
name|SaslRpcServer
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
name|ClientRMProxy
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
name|RMFailoverProxyProvider
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterId
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
comment|/**  * Utility class that creates proxy for specified protocols when federation is  * enabled. The class creates a federation aware failover provider, i.e. the  * failover provider uses the {@code FederationStateStore} to determine the  * current active ResourceManager  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FederationProxyProviderUtil
specifier|public
specifier|final
class|class
name|FederationProxyProviderUtil
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
name|FederationProxyProviderUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create a proxy for the specified protocol. For non-HA, this is a direct    * connection to the ResourceManager address. When HA is enabled, the proxy    * handles the failover between the ResourceManagers as well.    *    * @param configuration Configuration to generate {@link ClientRMProxy}    * @param protocol Protocol for the proxy    * @param subClusterId the unique identifier or the sub-cluster    * @param user the user on whose behalf the proxy is being created    * @param<T> Type information of the proxy    * @return Proxy to the RM    * @throws IOException on failure    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|createRMProxy (Configuration configuration, final Class<T> protocol, SubClusterId subClusterId, UserGroupInformation user)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|createRMProxy
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
name|SubClusterId
name|subClusterId
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createRMProxy
argument_list|(
name|configuration
argument_list|,
name|protocol
argument_list|,
name|subClusterId
argument_list|,
name|user
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Create a proxy for the specified protocol. For non-HA, this is a direct    * connection to the ResourceManager address. When HA is enabled, the proxy    * handles the failover between the ResourceManagers as well.    *    * @param configuration Configuration to generate {@link ClientRMProxy}    * @param protocol Protocol for the proxy    * @param subClusterId the unique identifier or the sub-cluster    * @param user the user on whose behalf the proxy is being created    * @param token the auth token to use for connection    * @param<T> Type information of the proxy    * @return Proxy to the RM    * @throws IOException on failure    */
annotation|@
name|Public
annotation|@
name|Unstable
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createRMProxy (final Configuration configuration, final Class<T> protocol, SubClusterId subClusterId, UserGroupInformation user, final Token token)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|createRMProxy
parameter_list|(
specifier|final
name|Configuration
name|configuration
parameter_list|,
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
name|SubClusterId
name|subClusterId
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|,
specifier|final
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
specifier|final
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|updateConf
argument_list|(
name|conf
argument_list|,
name|subClusterId
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating RMProxy with a token: {} to subcluster: {}"
operator|+
literal|" for protocol: {}"
argument_list|,
name|token
argument_list|,
name|subClusterId
argument_list|,
name|protocol
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|user
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|setAuthModeInConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating RMProxy without a token to subcluster: {}"
operator|+
literal|" for protocol: {}"
argument_list|,
name|subClusterId
argument_list|,
name|protocol
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|T
name|proxyConnection
init|=
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|ClientRMProxy
operator|.
name|createRMProxy
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|proxyConnection
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Error while creating of RM application master service proxy for"
operator|+
literal|" appAttemptId: "
operator|+
name|user
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|setAuthModeInConf (Configuration conf)
specifier|private
specifier|static
name|void
name|setAuthModeInConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
name|SaslRpcServer
operator|.
name|AuthMethod
operator|.
name|TOKEN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// updating the conf with the refreshed RM addresses as proxy creations
comment|// are based out of conf
DECL|method|updateConf (Configuration conf, SubClusterId subClusterId)
specifier|private
specifier|static
name|void
name|updateConf
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|SubClusterId
name|subClusterId
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_SUBCLUSTER_ID
argument_list|,
name|subClusterId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// In a Federation setting, we will connect to not just the local cluster RM
comment|// but also multiple external RMs. The membership information of all the RMs
comment|// that are currently
comment|// participating in Federation is available in the central
comment|// FederationStateStore.
comment|// So we will:
comment|// 1. obtain the RM service addresses from FederationStateStore using the
comment|// FederationRMFailoverProxyProvider.
comment|// 2. disable traditional HA as that depends on local configuration lookup
comment|// for RMs using indexes.
comment|// 3. we will enable federation failover IF traditional HA is enabled so
comment|// that the appropriate failover RetryPolicy is initialized.
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_PROXY_PROVIDER
argument_list|,
name|FederationRMFailoverProxyProvider
operator|.
name|class
argument_list|,
name|RMFailoverProxyProvider
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_FAILOVER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|// disable instantiation
DECL|method|FederationProxyProviderUtil ()
specifier|private
name|FederationProxyProviderUtil
parameter_list|()
block|{   }
block|}
end_class

end_unit

