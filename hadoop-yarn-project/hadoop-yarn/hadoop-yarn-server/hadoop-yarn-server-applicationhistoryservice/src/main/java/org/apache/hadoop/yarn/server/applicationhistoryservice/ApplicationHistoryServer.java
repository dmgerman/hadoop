begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
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
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|http
operator|.
name|HttpServer2
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
name|HttpCrossOriginFilterInitializer
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
name|service
operator|.
name|Service
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
name|ReflectionUtils
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
name|applicationhistoryservice
operator|.
name|webapp
operator|.
name|AHSWebApp
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
name|security
operator|.
name|ApplicationACLsManager
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
name|timeline
operator|.
name|LeveldbTimelineStore
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
name|timeline
operator|.
name|TimelineDataManager
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
name|timeline
operator|.
name|TimelineStore
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
name|timeline
operator|.
name|security
operator|.
name|TimelineACLsManager
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
name|timeline
operator|.
name|security
operator|.
name|TimelineV1DelegationTokenSecretManagerService
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
name|timeline
operator|.
name|webapp
operator|.
name|CrossOriginFilterInitializer
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
name|util
operator|.
name|timeline
operator|.
name|TimelineServerUtils
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
name|webapp
operator|.
name|WebApp
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
name|webapp
operator|.
name|WebApps
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
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|FilterHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|webapp
operator|.
name|WebAppContext
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
comment|/**  * History server that keeps track of all types of history in the cluster.  * Application specific history to start with.  */
end_comment

begin_class
DECL|class|ApplicationHistoryServer
specifier|public
class|class
name|ApplicationHistoryServer
extends|extends
name|CompositeService
block|{
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
name|ApplicationHistoryServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ahsClientService
specifier|private
name|ApplicationHistoryClientService
name|ahsClientService
decl_stmt|;
DECL|field|aclsManager
specifier|private
name|ApplicationACLsManager
name|aclsManager
decl_stmt|;
DECL|field|historyManager
specifier|private
name|ApplicationHistoryManager
name|historyManager
decl_stmt|;
DECL|field|timelineStore
specifier|private
name|TimelineStore
name|timelineStore
decl_stmt|;
DECL|field|secretManagerService
specifier|private
name|TimelineV1DelegationTokenSecretManagerService
name|secretManagerService
decl_stmt|;
DECL|field|timelineDataManager
specifier|private
name|TimelineDataManager
name|timelineDataManager
decl_stmt|;
DECL|field|webApp
specifier|private
name|WebApp
name|webApp
decl_stmt|;
DECL|field|pauseMonitor
specifier|private
name|JvmPauseMonitor
name|pauseMonitor
decl_stmt|;
DECL|method|ApplicationHistoryServer ()
specifier|public
name|ApplicationHistoryServer
parameter_list|()
block|{
name|super
argument_list|(
name|ApplicationHistoryServer
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
comment|// do security login first.
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
name|YarnRuntimeException
argument_list|(
literal|"Failed to login"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
comment|// init timeline services
name|timelineStore
operator|=
name|createTimelineStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|addIfService
argument_list|(
name|timelineStore
argument_list|)
expr_stmt|;
name|secretManagerService
operator|=
name|createTimelineDelegationTokenSecretManagerService
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|secretManagerService
argument_list|)
expr_stmt|;
name|timelineDataManager
operator|=
name|createTimelineDataManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|timelineDataManager
argument_list|)
expr_stmt|;
comment|// init generic history service afterwards
name|aclsManager
operator|=
name|createApplicationACLsManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|historyManager
operator|=
name|createApplicationHistoryManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ahsClientService
operator|=
name|createApplicationHistoryClientService
argument_list|(
name|historyManager
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|ahsClientService
argument_list|)
expr_stmt|;
name|addService
argument_list|(
operator|(
name|Service
operator|)
name|historyManager
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"ApplicationHistoryServer"
argument_list|)
expr_stmt|;
name|JvmMetrics
name|jm
init|=
name|JvmMetrics
operator|.
name|initSingleton
argument_list|(
literal|"ApplicationHistoryServer"
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
name|conf
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
name|startWebApp
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
if|if
condition|(
name|webApp
operator|!=
literal|null
condition|)
block|{
name|webApp
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getClientService ()
name|ApplicationHistoryClientService
name|getClientService
parameter_list|()
block|{
return|return
name|this
operator|.
name|ahsClientService
return|;
block|}
DECL|method|getListenerAddress ()
specifier|private
name|InetSocketAddress
name|getListenerAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|webApp
operator|.
name|httpServer
argument_list|()
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getPort ()
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|this
operator|.
name|getListenerAddress
argument_list|()
operator|.
name|getPort
argument_list|()
return|;
block|}
comment|/**    * @return ApplicationTimelineStore    */
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getTimelineStore ()
specifier|public
name|TimelineStore
name|getTimelineStore
parameter_list|()
block|{
return|return
name|timelineStore
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getApplicationHistoryManager ()
name|ApplicationHistoryManager
name|getApplicationHistoryManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|historyManager
return|;
block|}
DECL|method|launchAppHistoryServer (String[] args)
specifier|static
name|ApplicationHistoryServer
name|launchAppHistoryServer
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
name|ApplicationHistoryServer
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|ApplicationHistoryServer
name|appHistoryServer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|appHistoryServer
operator|=
operator|new
name|ApplicationHistoryServer
argument_list|()
expr_stmt|;
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
name|appHistoryServer
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
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|appHistoryServer
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|appHistoryServer
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
name|error
argument_list|(
literal|"Error starting ApplicationHistoryServer"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
operator|-
literal|1
argument_list|,
literal|"Error starting ApplicationHistoryServer"
argument_list|)
expr_stmt|;
block|}
return|return
name|appHistoryServer
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
name|launchAppHistoryServer
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ApplicationHistoryClientService
DECL|method|createApplicationHistoryClientService ( ApplicationHistoryManager historyManager)
name|createApplicationHistoryClientService
parameter_list|(
name|ApplicationHistoryManager
name|historyManager
parameter_list|)
block|{
return|return
operator|new
name|ApplicationHistoryClientService
argument_list|(
name|historyManager
argument_list|)
return|;
block|}
DECL|method|createApplicationACLsManager ( Configuration conf)
specifier|private
name|ApplicationACLsManager
name|createApplicationACLsManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|createApplicationHistoryManager ( Configuration conf)
specifier|private
name|ApplicationHistoryManager
name|createApplicationHistoryManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Backward compatibility:
comment|// APPLICATION_HISTORY_STORE is neither null nor empty, it means that the
comment|// user has enabled it explicitly.
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_STORE
argument_list|)
operator|==
literal|null
operator|||
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_STORE
argument_list|)
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_STORE
argument_list|)
operator|.
name|equals
argument_list|(
name|NullApplicationHistoryStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|ApplicationHistoryManagerOnTimelineStore
argument_list|(
name|timelineDataManager
argument_list|,
name|aclsManager
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The filesystem based application history store is deprecated."
argument_list|)
expr_stmt|;
return|return
operator|new
name|ApplicationHistoryManagerImpl
argument_list|()
return|;
block|}
block|}
DECL|method|createTimelineStore ( Configuration conf)
specifier|private
name|TimelineStore
name|createTimelineStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STORE
argument_list|,
name|LeveldbTimelineStore
operator|.
name|class
argument_list|,
name|TimelineStore
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
specifier|private
name|TimelineV1DelegationTokenSecretManagerService
DECL|method|createTimelineDelegationTokenSecretManagerService (Configuration conf)
name|createTimelineDelegationTokenSecretManagerService
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|TimelineV1DelegationTokenSecretManagerService
argument_list|()
return|;
block|}
DECL|method|createTimelineDataManager (Configuration conf)
specifier|private
name|TimelineDataManager
name|createTimelineDataManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|TimelineACLsManager
name|aclsMgr
init|=
operator|new
name|TimelineACLsManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|aclsMgr
operator|.
name|setTimelineStore
argument_list|(
name|timelineStore
argument_list|)
expr_stmt|;
return|return
operator|new
name|TimelineDataManager
argument_list|(
name|timelineStore
argument_list|,
name|aclsMgr
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|startWebApp ()
specifier|private
name|void
name|startWebApp
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
comment|// Always load pseudo authentication filter to parse "user.name" in an URL
comment|// to identify a HTTP request's user in insecure mode.
comment|// When Kerberos authentication type is set (i.e., secure mode is turned on),
comment|// the customized filter will be loaded by the timeline server to do Kerberos
comment|// + DT authentication.
name|String
name|initializers
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|defaultInitializers
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Add CORS filter
if|if
condition|(
operator|!
name|initializers
operator|.
name|contains
argument_list|(
name|CrossOriginFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_HTTP_CROSS_ORIGIN_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_HTTP_CROSS_ORIGIN_ENABLED_DEFAULT
argument_list|)
condition|)
block|{
if|if
condition|(
name|initializers
operator|.
name|contains
argument_list|(
name|HttpCrossOriginFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|initializers
operator|=
name|initializers
operator|.
name|replaceAll
argument_list|(
name|HttpCrossOriginFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|CrossOriginFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|defaultInitializers
operator|.
name|add
argument_list|(
name|CrossOriginFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|TimelineServerUtils
operator|.
name|addTimelineAuthFilter
argument_list|(
name|initializers
argument_list|,
name|defaultInitializers
argument_list|,
name|secretManagerService
argument_list|)
expr_stmt|;
name|TimelineServerUtils
operator|.
name|setTimelineFilters
argument_list|(
name|conf
argument_list|,
name|initializers
argument_list|,
name|defaultInitializers
argument_list|)
expr_stmt|;
name|String
name|bindAddress
init|=
name|WebAppUtils
operator|.
name|getWebAppBindURL
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
name|WebAppUtils
operator|.
name|getAHSWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|AHSWebApp
name|ahsWebApp
init|=
operator|new
name|AHSWebApp
argument_list|(
name|timelineDataManager
argument_list|,
name|ahsClientService
argument_list|)
decl_stmt|;
name|webApp
operator|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"applicationhistory"
argument_list|,
name|ApplicationHistoryClientService
operator|.
name|class
argument_list|,
name|ahsClientService
argument_list|,
literal|"ws"
argument_list|)
operator|.
name|with
argument_list|(
name|conf
argument_list|)
operator|.
name|withAttribute
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|)
argument_list|)
operator|.
name|withCSRFProtection
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_CSRF_PREFIX
argument_list|)
operator|.
name|withXFSProtection
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_XFS_PREFIX
argument_list|)
operator|.
name|at
argument_list|(
name|bindAddress
argument_list|)
operator|.
name|build
argument_list|(
name|ahsWebApp
argument_list|)
expr_stmt|;
name|HttpServer2
name|httpServer
init|=
name|webApp
operator|.
name|httpServer
argument_list|()
decl_stmt|;
name|String
index|[]
name|names
init|=
name|conf
operator|.
name|getTrimmedStrings
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_UI_NAMES
argument_list|)
decl_stmt|;
name|WebAppContext
name|webAppContext
init|=
name|httpServer
operator|.
name|getWebAppContext
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|String
name|webPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_UI_WEB_PATH_PREFIX
operator|+
name|name
argument_list|)
decl_stmt|;
name|String
name|onDiskPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_UI_ON_DISK_PATH_PREFIX
operator|+
name|name
argument_list|)
decl_stmt|;
name|WebAppContext
name|uiWebAppContext
init|=
operator|new
name|WebAppContext
argument_list|()
decl_stmt|;
name|uiWebAppContext
operator|.
name|setContextPath
argument_list|(
name|webPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|onDiskPath
operator|.
name|endsWith
argument_list|(
literal|".war"
argument_list|)
condition|)
block|{
name|uiWebAppContext
operator|.
name|setWar
argument_list|(
name|onDiskPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|uiWebAppContext
operator|.
name|setResourceBase
argument_list|(
name|onDiskPath
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
index|[]
name|ALL_URLS
init|=
block|{
literal|"/*"
block|}
decl_stmt|;
name|FilterHolder
index|[]
name|filterHolders
init|=
name|webAppContext
operator|.
name|getServletHandler
argument_list|()
operator|.
name|getFilters
argument_list|()
decl_stmt|;
for|for
control|(
name|FilterHolder
name|filterHolder
range|:
name|filterHolders
control|)
block|{
if|if
condition|(
operator|!
literal|"guice"
operator|.
name|equals
argument_list|(
name|filterHolder
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|HttpServer2
operator|.
name|defineFilter
argument_list|(
name|uiWebAppContext
argument_list|,
name|filterHolder
operator|.
name|getName
argument_list|()
argument_list|,
name|filterHolder
operator|.
name|getClassName
argument_list|()
argument_list|,
name|filterHolder
operator|.
name|getInitParameters
argument_list|()
argument_list|,
name|ALL_URLS
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Hosting "
operator|+
name|name
operator|+
literal|" from "
operator|+
name|onDiskPath
operator|+
literal|" at "
operator|+
name|webPath
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|addHandlerAtFront
argument_list|(
name|uiWebAppContext
argument_list|)
expr_stmt|;
block|}
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|conf
operator|.
name|updateConnectAddr
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
name|this
operator|.
name|getListenerAddress
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiating AHSWebApp at "
operator|+
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"AHSWebApp failed to start."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|doSecureLogin (Configuration conf)
specifier|private
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
name|TIMELINE_SERVICE_KEYTAB
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PRINCIPAL
argument_list|,
name|socAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Retrieve the timeline server bind address from configuration    *    * @param conf    * @return InetSocketAddress    */
DECL|method|getBindAddress (Configuration conf)
specifier|private
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
name|TIMELINE_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_PORT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

