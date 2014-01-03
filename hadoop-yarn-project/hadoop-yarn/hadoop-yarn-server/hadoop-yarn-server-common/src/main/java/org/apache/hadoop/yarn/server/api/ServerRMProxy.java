begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api
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
name|api
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
name|yarn
operator|.
name|client
operator|.
name|RMProxy
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
DECL|class|ServerRMProxy
specifier|public
class|class
name|ServerRMProxy
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
name|ServerRMProxy
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|INSTANCE
operator|=
operator|new
name|ServerRMProxy
argument_list|()
expr_stmt|;
block|}
DECL|method|ServerRMProxy ()
specifier|private
name|ServerRMProxy
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a proxy to the ResourceManager for the specified protocol.    * @param configuration Configuration with all the required information.    * @param protocol Server protocol for which proxy is being requested.    * @param<T> Type of proxy.    * @return Proxy to the ResourceManager for the specified server protocol.    * @throws IOException    */
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
comment|// This method exists only to initiate this class' static INSTANCE. TODO:
comment|// FIX if possible
return|return
name|RMProxy
operator|.
name|createRMProxy
argument_list|(
name|configuration
argument_list|,
name|protocol
argument_list|)
return|;
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
block|{
if|if
condition|(
name|protocol
operator|==
name|ResourceTracker
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
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
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
name|ResourceTracker
operator|.
name|class
argument_list|)
argument_list|,
literal|"ResourceManager does not support this protocol"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

