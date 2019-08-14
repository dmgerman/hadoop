begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Path
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
name|DeletionService
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
name|ContainerDiagnosticsUpdateEvent
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
name|deletion
operator|.
name|task
operator|.
name|DockerContainerDeletionTask
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
name|linux
operator|.
name|runtime
operator|.
name|OCIContainerRuntime
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
name|executor
operator|.
name|ContainerReapContext
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
name|executor
operator|.
name|ContainerSignalContext
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import static
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
operator|.
name|ContainerLaunch
operator|.
name|EXIT_CODE_FILE_SUFFIX
import|;
end_import

begin_comment
comment|/**  * Cleanup the container.  * Cancels the launch if launch has not started yet or signals  * the executor to not execute the process if not already done so.  * Also, sends a SIGTERM followed by a SIGKILL to the process if  * the process id is available.  */
end_comment

begin_class
DECL|class|ContainerCleanup
specifier|public
class|class
name|ContainerCleanup
implements|implements
name|Runnable
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
name|ContainerCleanup
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
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|exec
specifier|private
specifier|final
name|ContainerExecutor
name|exec
decl_stmt|;
DECL|field|container
specifier|private
specifier|final
name|Container
name|container
decl_stmt|;
DECL|field|launch
specifier|private
specifier|final
name|ContainerLaunch
name|launch
decl_stmt|;
DECL|field|sleepDelayBeforeSigKill
specifier|private
specifier|final
name|long
name|sleepDelayBeforeSigKill
decl_stmt|;
DECL|method|ContainerCleanup (Context context, Configuration configuration, Dispatcher dispatcher, ContainerExecutor exec, Container container, ContainerLaunch containerLaunch)
specifier|public
name|ContainerCleanup
parameter_list|(
name|Context
name|context
parameter_list|,
name|Configuration
name|configuration
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|ContainerExecutor
name|exec
parameter_list|,
name|Container
name|container
parameter_list|,
name|ContainerLaunch
name|containerLaunch
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|context
argument_list|,
literal|"context"
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|configuration
argument_list|,
literal|"config"
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|dispatcher
argument_list|,
literal|"dispatcher"
argument_list|)
expr_stmt|;
name|this
operator|.
name|exec
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|exec
argument_list|,
literal|"exec"
argument_list|)
expr_stmt|;
name|this
operator|.
name|container
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|container
argument_list|,
literal|"container"
argument_list|)
expr_stmt|;
name|this
operator|.
name|launch
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerLaunch
argument_list|,
literal|"launch"
argument_list|)
expr_stmt|;
name|this
operator|.
name|sleepDelayBeforeSigKill
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
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
name|ContainerId
name|containerId
init|=
name|container
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|String
name|containerIdStr
init|=
name|containerId
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Cleaning up container "
operator|+
name|containerIdStr
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|getNMStateStore
argument_list|()
operator|.
name|storeContainerKilled
argument_list|(
name|containerId
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
name|error
argument_list|(
literal|"Unable to mark container "
operator|+
name|containerId
operator|+
literal|" killed in store"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// launch flag will be set to true if process already launched,
comment|// in process of launching, or failed to launch.
name|boolean
name|alreadyLaunched
init|=
operator|!
name|launch
operator|.
name|markLaunched
argument_list|()
operator|||
name|launch
operator|.
name|isLaunchCompleted
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|alreadyLaunched
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Container "
operator|+
name|containerIdStr
operator|+
literal|" not launched."
operator|+
literal|" No cleanup needed to be done"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Marking container {} as inactive"
argument_list|,
name|containerIdStr
argument_list|)
expr_stmt|;
comment|// this should ensure that if the container process has not launched
comment|// by this time, it will never be launched
name|exec
operator|.
name|deactivateContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|Path
name|pidFilePath
init|=
name|launch
operator|.
name|getPidFilePath
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Getting pid for container {} to kill"
operator|+
literal|" from pid file {}"
argument_list|,
name|containerIdStr
argument_list|,
name|pidFilePath
operator|!=
literal|null
condition|?
name|pidFilePath
else|:
literal|"null"
argument_list|)
expr_stmt|;
comment|// however the container process may have already started
try|try
block|{
comment|// get process id from pid file if available
comment|// else if shell is still active, get it from the shell
name|String
name|processId
init|=
name|launch
operator|.
name|getContainerPid
argument_list|()
decl_stmt|;
comment|// kill process
name|String
name|user
init|=
name|container
operator|.
name|getUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|processId
operator|!=
literal|null
condition|)
block|{
name|signalProcess
argument_list|(
name|processId
argument_list|,
name|user
argument_list|,
name|containerIdStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Normally this means that the process was notified about
comment|// deactivateContainer above and did not start.
comment|// Since we already set the state to RUNNING or REINITIALIZING
comment|// we have to send a killed event to continue.
if|if
condition|(
operator|!
name|launch
operator|.
name|isLaunchCompleted
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Container clean up before pid file created "
operator|+
name|containerIdStr
argument_list|)
expr_stmt|;
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
name|container
operator|.
name|getContainerId
argument_list|()
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
literal|"Container terminated before pid file created."
argument_list|)
argument_list|)
expr_stmt|;
comment|// There is a possibility that the launch grabbed the file name before
comment|// the deactivateContainer above but it was slow enough to avoid
comment|// getContainerPid.
comment|// Increasing YarnConfiguration.NM_PROCESS_KILL_WAIT_MS
comment|// reduces the likelihood of this race condition and process leak.
block|}
block|}
comment|// rm container in docker
if|if
condition|(
name|OCIContainerRuntime
operator|.
name|isOCICompliantContainerRequested
argument_list|(
name|conf
argument_list|,
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
argument_list|)
condition|)
block|{
name|rmDockerContainerDelayed
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Exception when trying to cleanup container "
operator|+
name|containerIdStr
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ContainerDiagnosticsUpdateEvent
argument_list|(
name|containerId
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// cleanup pid file if present
if|if
condition|(
name|pidFilePath
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|FileContext
name|lfs
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
name|lfs
operator|.
name|delete
argument_list|(
name|pidFilePath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|delete
argument_list|(
name|pidFilePath
operator|.
name|suffix
argument_list|(
name|EXIT_CODE_FILE_SUFFIX
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} exception trying to delete pid file {}. Ignoring."
argument_list|,
name|containerId
argument_list|,
name|pidFilePath
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
comment|// Reap the container
name|launch
operator|.
name|reapContainer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} exception trying to reap container. Ignoring."
argument_list|,
name|containerId
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|rmDockerContainerDelayed ()
specifier|private
name|void
name|rmDockerContainerDelayed
parameter_list|()
block|{
name|DeletionService
name|deletionService
init|=
name|context
operator|.
name|getDeletionService
argument_list|()
decl_stmt|;
name|DockerContainerDeletionTask
name|deletionTask
init|=
operator|new
name|DockerContainerDeletionTask
argument_list|(
name|deletionService
argument_list|,
name|container
operator|.
name|getUser
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|deletionService
operator|.
name|delete
argument_list|(
name|deletionTask
argument_list|)
expr_stmt|;
block|}
DECL|method|signalProcess (String processId, String user, String containerIdStr)
specifier|private
name|void
name|signalProcess
parameter_list|(
name|String
name|processId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|containerIdStr
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending signal to pid {} as user {} for container {}"
argument_list|,
name|processId
argument_list|,
name|user
argument_list|,
name|containerIdStr
argument_list|)
expr_stmt|;
specifier|final
name|ContainerExecutor
operator|.
name|Signal
name|signal
init|=
name|sleepDelayBeforeSigKill
operator|>
literal|0
condition|?
name|ContainerExecutor
operator|.
name|Signal
operator|.
name|TERM
else|:
name|ContainerExecutor
operator|.
name|Signal
operator|.
name|KILL
decl_stmt|;
name|boolean
name|result
init|=
name|sendSignal
argument_list|(
name|user
argument_list|,
name|processId
argument_list|,
name|signal
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sent signal {} to pid {} as user {} for container {},"
operator|+
literal|" result={}"
argument_list|,
name|signal
argument_list|,
name|processId
argument_list|,
name|user
argument_list|,
name|containerIdStr
argument_list|,
operator|(
name|result
condition|?
literal|"success"
else|:
literal|"failed"
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sleepDelayBeforeSigKill
operator|>
literal|0
condition|)
block|{
operator|new
name|ContainerExecutor
operator|.
name|DelayedProcessKiller
argument_list|(
name|container
argument_list|,
name|user
argument_list|,
name|processId
argument_list|,
name|sleepDelayBeforeSigKill
argument_list|,
name|ContainerExecutor
operator|.
name|Signal
operator|.
name|KILL
argument_list|,
name|exec
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|sendSignal (String user, String processId, ContainerExecutor.Signal signal)
specifier|private
name|boolean
name|sendSignal
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|processId
parameter_list|,
name|ContainerExecutor
operator|.
name|Signal
name|signal
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|exec
operator|.
name|signalContainer
argument_list|(
operator|new
name|ContainerSignalContext
operator|.
name|Builder
argument_list|()
operator|.
name|setContainer
argument_list|(
name|container
argument_list|)
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
operator|.
name|setPid
argument_list|(
name|processId
argument_list|)
operator|.
name|setSignal
argument_list|(
name|signal
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

