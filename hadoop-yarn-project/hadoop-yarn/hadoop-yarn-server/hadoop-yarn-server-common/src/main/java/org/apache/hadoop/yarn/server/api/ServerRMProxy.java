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
DECL|method|createRMProxy (final Configuration conf, final Class<T> protocol)
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
name|conf
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
name|InetSocketAddress
name|rmAddress
init|=
name|getRMAddress
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|)
decl_stmt|;
return|return
name|createRMProxy
argument_list|(
name|conf
argument_list|,
name|protocol
argument_list|,
name|rmAddress
argument_list|)
return|;
block|}
DECL|method|getRMAddress (Configuration conf, Class<?> protocol)
specifier|private
specifier|static
name|InetSocketAddress
name|getRMAddress
parameter_list|(
name|Configuration
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
block|}
end_class

end_unit

