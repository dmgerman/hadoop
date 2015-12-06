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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|source
operator|.
name|JvmMetrics
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
name|service
operator|.
name|CompositeService
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
name|ExitUtil
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
name|GenericOptionsParser
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
name|JvmPauseMonitor
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
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
DECL|field|pauseMonitor
specifier|private
name|JvmPauseMonitor
name|pauseMonitor
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
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
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
name|doSecureLogin
argument_list|(
name|conf
argument_list|)
expr_stmt|;
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
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"WebAppProxyServer"
argument_list|)
expr_stmt|;
name|JvmMetrics
name|jm
init|=
name|JvmMetrics
operator|.
name|initSingleton
argument_list|(
literal|"WebAppProxyServer"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|pauseMonitor
operator|=
operator|new
name|JvmPauseMonitor
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|pauseMonitor
argument_list|)
expr_stmt|;
name|jm
operator|.
name|setPauseMonitor
argument_list|(
name|pauseMonitor
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
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
name|InetSocketAddress
name|socAddr
init|=
name|getBindAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
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
argument_list|,
name|socAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Retrieve PROXY bind address from configuration    *    * @param conf    * @return InetSocketAddress    */
DECL|method|getBindAddress (Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getBindAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_PROXY_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_PROXY_PORT
argument_list|)
return|;
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
name|YarnConfiguration
name|configuration
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
operator|new
name|GenericOptionsParser
argument_list|(
name|configuration
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|WebAppProxyServer
name|proxyServer
init|=
name|startServer
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|proxyServer
operator|.
name|proxy
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|ExitUtil
operator|.
name|terminate
argument_list|(
operator|-
literal|1
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Start proxy server.    *     * @return proxy server instance.    */
DECL|method|startServer (Configuration configuration)
specifier|protected
specifier|static
name|WebAppProxyServer
name|startServer
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|Exception
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
name|proxy
operator|.
name|init
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|proxy
return|;
block|}
block|}
end_class

end_unit

