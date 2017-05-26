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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|utils
operator|.
name|AMRMClientUtils
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
comment|// Disable constructor
DECL|method|FederationProxyProviderUtil ()
specifier|private
name|FederationProxyProviderUtil
parameter_list|()
block|{   }
comment|/**    * Create a proxy for the specified protocol in the context of Federation. For    * non-HA, this is a direct connection to the ResourceManager address. When HA    * is enabled, the proxy handles the failover between the ResourceManagers as    * well.    *    * @param configuration Configuration to generate {@link ClientRMProxy}    * @param protocol Protocol for the proxy    * @param subClusterId the unique identifier or the sub-cluster    * @param user the user on whose behalf the proxy is being created    * @param<T> Type information of the proxy    * @return Proxy to the RM    * @throws IOException on failure    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|createRMProxy (Configuration configuration, Class<T> protocol, SubClusterId subClusterId, UserGroupInformation user)
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
comment|/**    * Create a proxy for the specified protocol in the context of Federation. For    * non-HA, this is a direct connection to the ResourceManager address. When HA    * is enabled, the proxy handles the failover between the ResourceManagers as    * well.    *    * @param configuration Configuration to generate {@link ClientRMProxy}    * @param protocol Protocol for the proxy    * @param subClusterId the unique identifier or the sub-cluster    * @param user the user on whose behalf the proxy is being created    * @param token the auth token to use for connection    * @param<T> Type information of the proxy    * @return Proxy to the RM    * @throws IOException on failure    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|createRMProxy (Configuration configuration, final Class<T> protocol, SubClusterId subClusterId, UserGroupInformation user, Token<? extends TokenIdentifier> token)
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
parameter_list|,
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|YarnConfiguration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|updateConfForFederation
argument_list|(
name|config
argument_list|,
name|subClusterId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|AMRMClientUtils
operator|.
name|createRMProxy
argument_list|(
name|config
argument_list|,
name|protocol
argument_list|,
name|user
argument_list|,
name|token
argument_list|)
return|;
block|}
comment|/**    * Updating the conf with Federation as long as certain subclusterId.    *    * @param conf configuration    * @param subClusterId subclusterId for the conf    */
DECL|method|updateConfForFederation (Configuration conf, String subClusterId)
specifier|public
specifier|static
name|void
name|updateConfForFederation
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|subClusterId
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
name|subClusterId
argument_list|)
expr_stmt|;
comment|/*      * In a Federation setting, we will connect to not just the local cluster RM      * but also multiple external RMs. The membership information of all the RMs      * that are currently participating in Federation is available in the      * central FederationStateStore. So we will: 1. obtain the RM service      * addresses from FederationStateStore using the      * FederationRMFailoverProxyProvider. 2. disable traditional HA as that      * depends on local configuration lookup for RMs using indexes. 3. we will      * enable federation failover IF traditional HA is enabled so that the      * appropriate failover RetryPolicy is initialized.      */
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
block|}
end_class

end_unit

