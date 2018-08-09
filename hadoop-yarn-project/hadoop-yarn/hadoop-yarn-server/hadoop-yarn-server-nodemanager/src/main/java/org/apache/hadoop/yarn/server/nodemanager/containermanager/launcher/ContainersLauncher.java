begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|launcher
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ExecutorService
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
name|Shell
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
name|records
operator|.
name|ContainerExitStatus
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerEventType
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerExitEvent
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
name|FileContext
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
name|UnsupportedFileSystemException
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
name|util
operator|.
name|concurrent
operator|.
name|HadoopExecutors
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
name|records
operator|.
name|ContainerId
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
name|Dispatcher
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
name|nodemanager
operator|.
name|ContainerExecutor
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
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|LocalDirsHandlerService
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|ContainerManagerImpl
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|ResourceLocalizationService
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

begin_comment
comment|/**  * The launcher for the containers. This service should be started only after  * the {@link ResourceLocalizationService} is started as it depends on creation  * of system directories on the local file-system.  *   */
end_comment

begin_class
DECL|class|ContainersLauncher
specifier|public
class|class
name|ContainersLauncher
extends|extends
name|AbstractService
implements|implements
name|EventHandler
argument_list|<
name|ContainersLauncherEvent
argument_list|>
block|{
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
name|ContainersLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|exec
specifier|private
specifier|final
name|ContainerExecutor
name|exec
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManagerImpl
name|containerManager
decl_stmt|;
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|containerLauncher
specifier|public
name|ExecutorService
name|containerLauncher
init|=
name|HadoopExecutors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"ContainersLauncher #%d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|running
specifier|public
specifier|final
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
name|running
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerLaunch
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|ContainersLauncher (Context context, Dispatcher dispatcher, ContainerExecutor exec, LocalDirsHandlerService dirsHandler, ContainerManagerImpl containerManager)
specifier|public
name|ContainersLauncher
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|ContainerExecutor
name|exec
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|,
name|ContainerManagerImpl
name|containerManager
parameter_list|)
block|{
name|super
argument_list|(
literal|"containers-launcher"
argument_list|)
expr_stmt|;
name|this
operator|.
name|exec
operator|=
name|exec
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|dirsHandler
operator|=
name|dirsHandler
expr_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
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
try|try
block|{
comment|//TODO Is this required?
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedFileSystemException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Failed to start ContainersLauncher"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|containerLauncher
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (ContainersLauncherEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|ContainersLauncherEvent
name|event
parameter_list|)
block|{
comment|// TODO: ContainersLauncher launches containers one by one!!
name|Container
name|container
init|=
name|event
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|container
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|LAUNCH_CONTAINER
case|:
name|Application
name|app
init|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerLaunch
name|launch
init|=
operator|new
name|ContainerLaunch
argument_list|(
name|context
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|dispatcher
argument_list|,
name|exec
argument_list|,
name|app
argument_list|,
name|event
operator|.
name|getContainer
argument_list|()
argument_list|,
name|dirsHandler
argument_list|,
name|containerManager
argument_list|)
decl_stmt|;
name|containerLauncher
operator|.
name|submit
argument_list|(
name|launch
argument_list|)
expr_stmt|;
name|running
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|launch
argument_list|)
expr_stmt|;
break|break;
case|case
name|RELAUNCH_CONTAINER
case|:
name|app
operator|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerRelaunch
name|relaunch
init|=
operator|new
name|ContainerRelaunch
argument_list|(
name|context
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|dispatcher
argument_list|,
name|exec
argument_list|,
name|app
argument_list|,
name|event
operator|.
name|getContainer
argument_list|()
argument_list|,
name|dirsHandler
argument_list|,
name|containerManager
argument_list|)
decl_stmt|;
name|containerLauncher
operator|.
name|submit
argument_list|(
name|relaunch
argument_list|)
expr_stmt|;
name|running
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|relaunch
argument_list|)
expr_stmt|;
break|break;
case|case
name|RECOVER_CONTAINER
case|:
name|app
operator|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|launch
operator|=
operator|new
name|RecoveredContainerLaunch
argument_list|(
name|context
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|dispatcher
argument_list|,
name|exec
argument_list|,
name|app
argument_list|,
name|event
operator|.
name|getContainer
argument_list|()
argument_list|,
name|dirsHandler
argument_list|,
name|containerManager
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|submit
argument_list|(
name|launch
argument_list|)
expr_stmt|;
name|running
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|launch
argument_list|)
expr_stmt|;
break|break;
case|case
name|RECOVER_PAUSED_CONTAINER
case|:
name|app
operator|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|launch
operator|=
operator|new
name|RecoverPausedContainerLaunch
argument_list|(
name|context
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|dispatcher
argument_list|,
name|exec
argument_list|,
name|app
argument_list|,
name|event
operator|.
name|getContainer
argument_list|()
argument_list|,
name|dirsHandler
argument_list|,
name|containerManager
argument_list|)
expr_stmt|;
name|containerLauncher
operator|.
name|submit
argument_list|(
name|launch
argument_list|)
expr_stmt|;
break|break;
case|case
name|CLEANUP_CONTAINER
case|:
case|case
name|CLEANUP_CONTAINER_FOR_REINIT
case|:
name|ContainerLaunch
name|launcher
init|=
name|running
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|launcher
operator|==
literal|null
condition|)
block|{
comment|// Container not launched.
comment|// triggering KILLING to CONTAINER_CLEANEDUP_AFTER_KILL transition.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerExitEvent
argument_list|(
name|containerId
argument_list|,
name|ContainerEventType
operator|.
name|CONTAINER_KILLED_ON_REQUEST
argument_list|,
name|Shell
operator|.
name|WINDOWS
condition|?
name|ContainerExecutor
operator|.
name|ExitCode
operator|.
name|FORCE_KILLED
operator|.
name|getExitCode
argument_list|()
else|:
name|ContainerExecutor
operator|.
name|ExitCode
operator|.
name|TERMINATED
operator|.
name|getExitCode
argument_list|()
argument_list|,
literal|"Container terminated before launch."
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Cleanup a container whether it is running/killed/completed, so that
comment|// no sub-processes are alive.
try|try
block|{
name|launcher
operator|.
name|cleanupContainer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got exception while cleaning container "
operator|+
name|containerId
operator|+
literal|". Ignoring."
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|SIGNAL_CONTAINER
case|:
name|SignalContainersLauncherEvent
name|signalEvent
init|=
operator|(
name|SignalContainersLauncherEvent
operator|)
name|event
decl_stmt|;
name|ContainerLaunch
name|runningContainer
init|=
name|running
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|runningContainer
operator|==
literal|null
condition|)
block|{
comment|// Container not launched. So nothing needs to be done.
name|LOG
operator|.
name|info
argument_list|(
literal|"Container "
operator|+
name|containerId
operator|+
literal|" not running, nothing to signal."
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|runningContainer
operator|.
name|signalContainer
argument_list|(
name|signalEvent
operator|.
name|getCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got exception while signaling container "
operator|+
name|containerId
operator|+
literal|" with command "
operator|+
name|signalEvent
operator|.
name|getCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|PAUSE_CONTAINER
case|:
name|ContainerLaunch
name|launchedContainer
init|=
name|running
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|launchedContainer
operator|==
literal|null
condition|)
block|{
comment|// Container not launched. So nothing needs to be done.
return|return;
block|}
comment|// Pause the container
try|try
block|{
name|launchedContainer
operator|.
name|pauseContainer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got exception while pausing container: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|RESUME_CONTAINER
case|:
name|ContainerLaunch
name|launchCont
init|=
name|running
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|launchCont
operator|==
literal|null
condition|)
block|{
comment|// Container not launched. So nothing needs to be done.
return|return;
block|}
comment|// Resume the container.
try|try
block|{
name|launchCont
operator|.
name|resumeContainer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got exception while resuming container: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
end_class

end_unit

