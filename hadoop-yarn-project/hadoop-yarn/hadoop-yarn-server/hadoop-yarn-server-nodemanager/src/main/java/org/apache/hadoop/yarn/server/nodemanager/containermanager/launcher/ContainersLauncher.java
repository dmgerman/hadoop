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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|Future
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
name|localizer
operator|.
name|ResourceLocalizationService
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
name|AbstractService
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|containerLauncher
specifier|private
specifier|final
name|ExecutorService
name|containerLauncher
init|=
name|Executors
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
DECL|field|running
specifier|private
specifier|final
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|RunningContainer
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
name|RunningContainer
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|class|RunningContainer
specifier|private
specifier|static
specifier|final
class|class
name|RunningContainer
block|{
DECL|method|RunningContainer (Future<Integer> submit, ContainerLaunch launcher)
specifier|public
name|RunningContainer
parameter_list|(
name|Future
argument_list|<
name|Integer
argument_list|>
name|submit
parameter_list|,
name|ContainerLaunch
name|launcher
parameter_list|)
block|{
name|this
operator|.
name|runningcontainer
operator|=
name|submit
expr_stmt|;
name|this
operator|.
name|launcher
operator|=
name|launcher
expr_stmt|;
block|}
DECL|field|runningcontainer
name|Future
argument_list|<
name|Integer
argument_list|>
name|runningcontainer
decl_stmt|;
DECL|field|launcher
name|ContainerLaunch
name|launcher
decl_stmt|;
block|}
DECL|method|ContainersLauncher (Context context, Dispatcher dispatcher, ContainerExecutor exec, LocalDirsHandlerService dirsHandler)
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
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
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
name|YarnException
argument_list|(
literal|"Failed to start ContainersLauncher"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|containerLauncher
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
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
argument_list|)
decl_stmt|;
name|running
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
operator|new
name|RunningContainer
argument_list|(
name|containerLauncher
operator|.
name|submit
argument_list|(
name|launch
argument_list|)
argument_list|,
name|launch
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|CLEANUP_CONTAINER
case|:
name|RunningContainer
name|rContainerDatum
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
name|rContainerDatum
operator|==
literal|null
condition|)
block|{
comment|// Container not launched. So nothing needs to be done.
return|return;
block|}
name|Future
argument_list|<
name|Integer
argument_list|>
name|rContainer
init|=
name|rContainerDatum
operator|.
name|runningcontainer
decl_stmt|;
if|if
condition|(
name|rContainer
operator|!=
literal|null
operator|&&
operator|!
name|rContainer
operator|.
name|isDone
argument_list|()
condition|)
block|{
comment|// Cancel the future so that it won't be launched
comment|// if it isn't already.
name|rContainer
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Cleanup a container whether it is running/killed/completed, so that
comment|// no sub-processes are alive.
try|try
block|{
name|rContainerDatum
operator|.
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
block|}
block|}
block|}
end_class

end_unit

