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
name|TimelineAuthenticationFilterInitializer
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
name|TimelineDelegationTokenSecretManagerService
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ApplicationHistoryServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ahsClientService
specifier|protected
name|ApplicationHistoryClientService
name|ahsClientService
decl_stmt|;
DECL|field|historyManager
specifier|protected
name|ApplicationHistoryManager
name|historyManager
decl_stmt|;
DECL|field|timelineStore
specifier|protected
name|TimelineStore
name|timelineStore
decl_stmt|;
DECL|field|secretManagerService
specifier|protected
name|TimelineDelegationTokenSecretManagerService
name|secretManagerService
decl_stmt|;
DECL|field|timelineACLsManager
specifier|protected
name|TimelineACLsManager
name|timelineACLsManager
decl_stmt|;
DECL|field|webApp
specifier|protected
name|WebApp
name|webApp
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
name|historyManager
operator|=
name|createApplicationHistory
argument_list|()
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
name|timelineACLsManager
operator|=
name|createTimelineACLsManager
argument_list|(
name|conf
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
operator|.
name|initSingleton
argument_list|(
literal|"ApplicationHistoryServer"
argument_list|,
literal|null
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
try|try
block|{
name|doSecureLogin
argument_list|(
name|getConfig
argument_list|()
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
name|startWebApp
argument_list|()
expr_stmt|;
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
specifier|public
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
specifier|protected
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
DECL|method|createApplicationHistory ()
specifier|protected
name|ApplicationHistoryManager
name|createApplicationHistory
parameter_list|()
block|{
return|return
operator|new
name|ApplicationHistoryManagerImpl
argument_list|()
return|;
block|}
DECL|method|getApplicationHistory ()
specifier|protected
name|ApplicationHistoryManager
name|getApplicationHistory
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
name|fatal
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
DECL|method|createApplicationHistoryManager ( Configuration conf)
specifier|protected
name|ApplicationHistoryManager
name|createApplicationHistoryManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|ApplicationHistoryManagerImpl
argument_list|()
return|;
block|}
DECL|method|createTimelineStore ( Configuration conf)
specifier|protected
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
specifier|protected
name|TimelineDelegationTokenSecretManagerService
DECL|method|createTimelineDelegationTokenSecretManagerService (Configuration conf)
name|createTimelineDelegationTokenSecretManagerService
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|TimelineDelegationTokenSecretManagerService
argument_list|()
return|;
block|}
DECL|method|createTimelineACLsManager (Configuration conf)
specifier|protected
name|TimelineACLsManager
name|createTimelineACLsManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|TimelineACLsManager
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|startWebApp ()
specifier|protected
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
comment|// Play trick to make the customized filter will only be loaded by the
comment|// timeline server when security is enabled and Kerberos authentication
comment|// is used.
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
operator|&&
name|conf
operator|.
name|get
argument_list|(
name|TimelineAuthenticationFilterInitializer
operator|.
name|PREFIX
operator|+
literal|"type"
argument_list|,
literal|""
argument_list|)
operator|.
name|equals
argument_list|(
literal|"kerberos"
argument_list|)
condition|)
block|{
name|String
name|initializers
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|)
decl_stmt|;
name|initializers
operator|=
name|initializers
operator|==
literal|null
operator|||
name|initializers
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|""
else|:
literal|","
operator|+
name|initializers
expr_stmt|;
if|if
condition|(
operator|!
name|initializers
operator|.
name|contains
argument_list|(
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|,
name|TimelineAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
name|initializers
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|bindAddress
init|=
name|WebAppUtils
operator|.
name|getAHSWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiating AHSWebApp at "
operator|+
name|bindAddress
argument_list|)
expr_stmt|;
try|try
block|{
name|AHSWebApp
name|ahsWebApp
init|=
name|AHSWebApp
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|ahsWebApp
operator|.
name|setApplicationHistoryManager
argument_list|(
name|historyManager
argument_list|)
expr_stmt|;
name|ahsWebApp
operator|.
name|setTimelineStore
argument_list|(
name|timelineStore
argument_list|)
expr_stmt|;
name|ahsWebApp
operator|.
name|setTimelineDelegationTokenSecretManagerService
argument_list|(
name|secretManagerService
argument_list|)
expr_stmt|;
name|ahsWebApp
operator|.
name|setTimelineACLsManager
argument_list|(
name|timelineACLsManager
argument_list|)
expr_stmt|;
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
name|at
argument_list|(
name|bindAddress
argument_list|)
operator|.
name|start
argument_list|(
name|ahsWebApp
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

