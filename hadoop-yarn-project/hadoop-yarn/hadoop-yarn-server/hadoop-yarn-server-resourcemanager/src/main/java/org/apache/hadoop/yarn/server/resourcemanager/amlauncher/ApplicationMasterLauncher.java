begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.amlauncher
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
name|resourcemanager
operator|.
name|amlauncher
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|service
operator|.
name|AbstractService
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
name|event
operator|.
name|EventHandler
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
name|resourcemanager
operator|.
name|RMContext
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
import|;
end_import

begin_class
DECL|class|ApplicationMasterLauncher
specifier|public
class|class
name|ApplicationMasterLauncher
extends|extends
name|AbstractService
implements|implements
name|EventHandler
argument_list|<
name|AMLauncherEvent
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
name|ApplicationMasterLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|launcherPool
specifier|private
name|ThreadPoolExecutor
name|launcherPool
decl_stmt|;
DECL|field|launcherHandlingThread
specifier|private
name|LauncherThread
name|launcherHandlingThread
decl_stmt|;
DECL|field|masterEvents
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|masterEvents
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|context
specifier|protected
specifier|final
name|RMContext
name|context
decl_stmt|;
DECL|method|ApplicationMasterLauncher (RMContext context)
specifier|public
name|ApplicationMasterLauncher
parameter_list|(
name|RMContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|ApplicationMasterLauncher
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|launcherHandlingThread
operator|=
operator|new
name|LauncherThread
argument_list|()
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
name|int
name|threadCount
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AMLAUNCHER_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AMLAUNCHER_THREAD_COUNT
argument_list|)
decl_stmt|;
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"ApplicationMasterLauncher #%d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launcherPool
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|threadCount
argument_list|,
name|threadCount
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|launcherPool
operator|.
name|setThreadFactory
argument_list|(
name|tf
argument_list|)
expr_stmt|;
name|Configuration
name|newConf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|newConf
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
name|RM_NODEMANAGER_CONNECT_RETRIES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NODEMANAGER_CONNECT_RETRIES
argument_list|)
argument_list|)
expr_stmt|;
name|setConfig
argument_list|(
name|newConf
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|newConf
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
name|launcherHandlingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|createRunnableLauncher (RMAppAttempt application, AMLauncherEventType event)
specifier|protected
name|Runnable
name|createRunnableLauncher
parameter_list|(
name|RMAppAttempt
name|application
parameter_list|,
name|AMLauncherEventType
name|event
parameter_list|)
block|{
name|Runnable
name|launcher
init|=
operator|new
name|AMLauncher
argument_list|(
name|context
argument_list|,
name|application
argument_list|,
name|event
argument_list|,
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|launcher
return|;
block|}
DECL|method|launch (RMAppAttempt application)
specifier|private
name|void
name|launch
parameter_list|(
name|RMAppAttempt
name|application
parameter_list|)
block|{
name|Runnable
name|launcher
init|=
name|createRunnableLauncher
argument_list|(
name|application
argument_list|,
name|AMLauncherEventType
operator|.
name|LAUNCH
argument_list|)
decl_stmt|;
name|masterEvents
operator|.
name|add
argument_list|(
name|launcher
argument_list|)
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
name|launcherHandlingThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|launcherHandlingThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|launcherHandlingThread
operator|.
name|getName
argument_list|()
operator|+
literal|" interrupted during join "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
name|launcherPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|class|LauncherThread
specifier|private
class|class
name|LauncherThread
extends|extends
name|Thread
block|{
DECL|method|LauncherThread ()
specifier|public
name|LauncherThread
parameter_list|()
block|{
name|super
argument_list|(
literal|"ApplicationMaster Launcher"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|this
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
name|Runnable
name|toLaunch
decl_stmt|;
try|try
block|{
name|toLaunch
operator|=
name|masterEvents
operator|.
name|take
argument_list|()
expr_stmt|;
name|launcherPool
operator|.
name|execute
argument_list|(
name|toLaunch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" interrupted. Returning."
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
DECL|method|cleanup (RMAppAttempt application)
specifier|private
name|void
name|cleanup
parameter_list|(
name|RMAppAttempt
name|application
parameter_list|)
block|{
name|Runnable
name|launcher
init|=
name|createRunnableLauncher
argument_list|(
name|application
argument_list|,
name|AMLauncherEventType
operator|.
name|CLEANUP
argument_list|)
decl_stmt|;
name|masterEvents
operator|.
name|add
argument_list|(
name|launcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (AMLauncherEvent appEvent)
specifier|public
specifier|synchronized
name|void
name|handle
parameter_list|(
name|AMLauncherEvent
name|appEvent
parameter_list|)
block|{
name|AMLauncherEventType
name|event
init|=
name|appEvent
operator|.
name|getType
argument_list|()
decl_stmt|;
name|RMAppAttempt
name|application
init|=
name|appEvent
operator|.
name|getAppAttempt
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|LAUNCH
case|:
name|launch
argument_list|(
name|application
argument_list|)
expr_stmt|;
break|break;
case|case
name|CLEANUP
case|:
name|cleanup
argument_list|(
name|application
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
block|}
end_class

end_unit

