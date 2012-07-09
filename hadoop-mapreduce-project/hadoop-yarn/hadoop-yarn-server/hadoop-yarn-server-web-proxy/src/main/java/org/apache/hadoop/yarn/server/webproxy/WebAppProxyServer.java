begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy
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
name|webproxy
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
name|util
operator|.
name|ShutdownHookManager
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
name|util
operator|.
name|StringUtils
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
name|YarnException
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
name|YarnUncaughtExceptionHandler
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
name|service
operator|.
name|CompositeService
import|;
end_import

begin_comment
comment|/**  * ProxyServer will sit in between the end user and AppMaster  * web interfaces.   */
end_comment

begin_class
DECL|class|WebAppProxyServer
specifier|public
class|class
name|WebAppProxyServer
extends|extends
name|CompositeService
block|{
comment|/**    * Priority of the ResourceManager shutdown hook.    */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|public
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|30
decl_stmt|;
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
name|WebAppProxyServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|proxy
specifier|private
name|WebAppProxy
name|proxy
init|=
literal|null
decl_stmt|;
DECL|method|WebAppProxyServer ()
specifier|public
name|WebAppProxyServer
parameter_list|()
block|{
name|super
argument_list|(
name|WebAppProxyServer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Configuration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|doSecureLogin
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Proxy Server Failed to login"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
name|proxy
operator|=
operator|new
name|WebAppProxy
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**    * Log in as the Kerberose principal designated for the proxy    * @param conf the configuration holding this information in it.    * @throws IOException on any error.    */
DECL|method|doSecureLogin (Configuration conf)
specifier|protected
name|void
name|doSecureLogin
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|PROXY_KEYTAB
argument_list|,
name|YarnConfiguration
operator|.
name|PROXY_PRINCIPAL
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|YarnUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|WebAppProxyServer
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
try|try
block|{
name|WebAppProxyServer
name|proxy
init|=
operator|new
name|WebAppProxyServer
argument_list|()
decl_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|CompositeServiceShutdownHook
argument_list|(
name|proxy
argument_list|)
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|proxy
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|start
argument_list|()
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
name|fatal
argument_list|(
literal|"Error starting Proxy server"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

