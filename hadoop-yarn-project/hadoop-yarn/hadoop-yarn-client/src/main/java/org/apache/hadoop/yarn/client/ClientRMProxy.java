begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|api
operator|.
name|ApplicationClientProtocol
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
name|ApplicationMasterProtocol
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
name|security
operator|.
name|AMRMTokenIdentifier
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
name|api
operator|.
name|ResourceManagerAdministrationProtocol
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

begin_class
DECL|class|ClientRMProxy
specifier|public
class|class
name|ClientRMProxy
parameter_list|<
name|T
parameter_list|>
extends|extends
name|RMProxy
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
name|ClientRMProxy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|ClientRMProxy
name|INSTANCE
init|=
operator|new
name|ClientRMProxy
argument_list|()
decl_stmt|;
DECL|interface|ClientRMProtocols
specifier|private
interface|interface
name|ClientRMProtocols
extends|extends
name|ApplicationClientProtocol
extends|,
name|ApplicationMasterProtocol
extends|,
name|ResourceManagerAdministrationProtocol
block|{
comment|// Add nothing
block|}
DECL|method|ClientRMProxy ()
specifier|private
name|ClientRMProxy
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a proxy to the ResourceManager for the specified protocol.    * @param configuration Configuration with all the required information.    * @param protocol Client protocol for which proxy is being requested.    * @param<T> Type of proxy.    * @return Proxy to the ResourceManager for the specified client protocol.    * @throws IOException    */
DECL|method|createRMProxy (final Configuration configuration, final Class<T> protocol)
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
name|INSTANCE
argument_list|)
return|;
block|}
DECL|method|setupTokens (InetSocketAddress resourceManagerAddress)
specifier|private
specifier|static
name|void
name|setupTokens
parameter_list|(
name|InetSocketAddress
name|resourceManagerAddress
parameter_list|)
throws|throws
name|IOException
block|{
comment|// It is assumed for now that the only AMRMToken in AM's UGI is for this
comment|// cluster/RM. TODO: Fix later when we have some kind of cluster-ID as
comment|// default service-address, see YARN-986.
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
range|:
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|token
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|AMRMTokenIdentifier
operator|.
name|KIND_NAME
argument_list|)
condition|)
block|{
comment|// This token needs to be directly provided to the AMs, so set the
comment|// appropriate service-name. We'll need more infrastructure when we
comment|// need to set it in HA case.
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|token
argument_list|,
name|resourceManagerAddress
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Override
DECL|method|getRMAddress (YarnConfiguration conf, Class<?> protocol)
specifier|protected
name|InetSocketAddress
name|getRMAddress
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|protocol
operator|==
name|ApplicationClientProtocol
operator|.
name|class
condition|)
block|{
return|return
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_PORT
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|protocol
operator|==
name|ResourceManagerAdministrationProtocol
operator|.
name|class
condition|)
block|{
return|return
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADMIN_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADMIN_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADMIN_PORT
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|protocol
operator|==
name|ApplicationMasterProtocol
operator|.
name|class
condition|)
block|{
name|InetSocketAddress
name|serviceAddr
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_PORT
argument_list|)
decl_stmt|;
name|setupTokens
argument_list|(
name|serviceAddr
argument_list|)
expr_stmt|;
return|return
name|serviceAddr
return|;
block|}
else|else
block|{
name|String
name|message
init|=
literal|"Unsupported protocol found when creating the proxy "
operator|+
literal|"connection to ResourceManager: "
operator|+
operator|(
operator|(
name|protocol
operator|!=
literal|null
operator|)
condition|?
name|protocol
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
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Override
DECL|method|checkAllowedProtocols (Class<?> protocol)
specifier|protected
name|void
name|checkAllowedProtocols
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|protocol
operator|.
name|isAssignableFrom
argument_list|(
name|ClientRMProtocols
operator|.
name|class
argument_list|)
argument_list|,
literal|"RM does not support this client protocol"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

