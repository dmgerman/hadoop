begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|ReadLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
operator|.
name|WriteLock
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
name|io
operator|.
name|FileUtils
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
name|Configurable
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|launcher
operator|.
name|ContainerLaunch
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
name|util
operator|.
name|ProcessIdFileReader
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

begin_class
DECL|class|ContainerExecutor
specifier|public
specifier|abstract
class|class
name|ContainerExecutor
implements|implements
name|Configurable
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
name|ContainerExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TASK_LAUNCH_SCRIPT_PERMISSION
specifier|final
specifier|public
specifier|static
name|FsPermission
name|TASK_LAUNCH_SCRIPT_PERMISSION
init|=
name|FsPermission
operator|.
name|createImmutable
argument_list|(
operator|(
name|short
operator|)
literal|0700
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|pidFiles
specifier|private
name|ConcurrentMap
argument_list|<
name|ContainerId
argument_list|,
name|Path
argument_list|>
name|pidFiles
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ContainerId
argument_list|,
name|Path
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|lock
specifier|private
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|readLock
specifier|private
specifier|final
name|ReadLock
name|readLock
init|=
name|lock
operator|.
name|readLock
argument_list|()
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|WriteLock
name|writeLock
init|=
name|lock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Run the executor initialization steps.     * Verify that the necessary configs, permissions are in place.    * @throws IOException    */
DECL|method|init ()
specifier|public
specifier|abstract
name|void
name|init
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * On Windows the ContainerLaunch creates a temporary empty jar to workaround the CLASSPATH length    * In a  secure cluster this jar must be localized so that the container has access to it    * This function localizes on-demand the jar.    *     * @param classPathJar    * @param owner    * @throws IOException    */
DECL|method|localizeClasspathJar (Path classPathJar, String owner)
specifier|public
name|void
name|localizeClasspathJar
parameter_list|(
name|Path
name|classPathJar
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
comment|// For the default container this is a no-op
comment|// The WindowsSecureContainerExecutor overrides this
block|}
comment|/**    * Prepare the environment for containers in this application to execute.    * For $x in local.dirs    *   create $x/$user/$appId    * Copy $nmLocal/appTokens -> $N/$user/$appId    * For $rsrc in private resources    *   Copy $rsrc -> $N/$user/filecache/[idef]    * For $rsrc in job resources    *   Copy $rsrc -> $N/$user/$appId/filecache/idef    * @param user user name of application owner    * @param appId id of the application    * @param nmPrivateContainerTokens path to localized credentials, rsrc by NM    * @param nmAddr RPC address to contact NM    * @param localDirs nm-local-dirs    * @param logDirs nm-log-dirs    * @throws IOException For most application init failures    * @throws InterruptedException If application init thread is halted by NM    */
DECL|method|startLocalizer (Path nmPrivateContainerTokens, InetSocketAddress nmAddr, String user, String appId, String locId, List<String> localDirs, List<String> logDirs)
specifier|public
specifier|abstract
name|void
name|startLocalizer
parameter_list|(
name|Path
name|nmPrivateContainerTokens
parameter_list|,
name|InetSocketAddress
name|nmAddr
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|locId
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Launch the container on the node. This is a blocking call and returns only    * when the container exits.    * @param container the container to be launched    * @param nmPrivateContainerScriptPath the path for launch script    * @param nmPrivateTokensPath the path for tokens for the container    * @param user the user of the container    * @param appId the appId of the container    * @param containerWorkDir the work dir for the container    * @param localDirs nm-local-dirs to be used for this container    * @param logDirs nm-log-dirs to be used for this container    * @return the return status of the launch    * @throws IOException    */
DECL|method|launchContainer (Container container, Path nmPrivateContainerScriptPath, Path nmPrivateTokensPath, String user, String appId, Path containerWorkDir, List<String> localDirs, List<String> logDirs)
specifier|public
specifier|abstract
name|int
name|launchContainer
parameter_list|(
name|Container
name|container
parameter_list|,
name|Path
name|nmPrivateContainerScriptPath
parameter_list|,
name|Path
name|nmPrivateTokensPath
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|appId
parameter_list|,
name|Path
name|containerWorkDir
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|signalContainer (String user, String pid, Signal signal)
specifier|public
specifier|abstract
name|boolean
name|signalContainer
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|pid
parameter_list|,
name|Signal
name|signal
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|deleteAsUser (String user, Path subDir, Path... basedirs)
specifier|public
specifier|abstract
name|void
name|deleteAsUser
parameter_list|(
name|String
name|user
parameter_list|,
name|Path
name|subDir
parameter_list|,
name|Path
modifier|...
name|basedirs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
DECL|method|isContainerProcessAlive (String user, String pid)
specifier|public
specifier|abstract
name|boolean
name|isContainerProcessAlive
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|pid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Recover an already existing container. This is a blocking call and returns    * only when the container exits.  Note that the container must have been    * activated prior to this call.    * @param user the user of the container    * @param containerId The ID of the container to reacquire    * @return The exit code of the pre-existing container    * @throws IOException    */
DECL|method|reacquireContainer (String user, ContainerId containerId)
specifier|public
name|int
name|reacquireContainer
parameter_list|(
name|String
name|user
parameter_list|,
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|pidPath
init|=
name|getPidFilePath
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|pidPath
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|containerId
operator|+
literal|" is not active, returning terminated error"
argument_list|)
expr_stmt|;
return|return
name|ExitCode
operator|.
name|TERMINATED
operator|.
name|getExitCode
argument_list|()
return|;
block|}
name|String
name|pid
init|=
literal|null
decl_stmt|;
name|pid
operator|=
name|ProcessIdFileReader
operator|.
name|getProcessId
argument_list|(
name|pidPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|pid
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to determine pid for "
operator|+
name|containerId
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Reacquiring "
operator|+
name|containerId
operator|+
literal|" with pid "
operator|+
name|pid
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|isContainerProcessAlive
argument_list|(
name|user
argument_list|,
name|pid
argument_list|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted while waiting for process "
operator|+
name|pid
operator|+
literal|" to exit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// wait for exit code file to appear
name|String
name|exitCodeFile
init|=
name|ContainerLaunch
operator|.
name|getExitCodeFile
argument_list|(
name|pidPath
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|exitCodeFile
argument_list|)
decl_stmt|;
specifier|final
name|int
name|sleepMsec
init|=
literal|100
decl_stmt|;
name|int
name|msecLeft
init|=
literal|2000
decl_stmt|;
while|while
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|&&
name|msecLeft
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|isContainerActive
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|containerId
operator|+
literal|" was deactivated"
argument_list|)
expr_stmt|;
return|return
name|ExitCode
operator|.
name|TERMINATED
operator|.
name|getExitCode
argument_list|()
return|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMsec
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted while waiting for exit code from "
operator|+
name|containerId
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|msecLeft
operator|-=
name|sleepMsec
expr_stmt|;
block|}
if|if
condition|(
name|msecLeft
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Timeout while waiting for exit code from "
operator|+
name|containerId
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|file
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error parsing exit code from pid "
operator|+
name|pid
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|enum|ExitCode
specifier|public
enum|enum
name|ExitCode
block|{
DECL|enumConstant|FORCE_KILLED
name|FORCE_KILLED
argument_list|(
literal|137
argument_list|)
block|,
DECL|enumConstant|TERMINATED
name|TERMINATED
argument_list|(
literal|143
argument_list|)
block|,
DECL|enumConstant|LOST
name|LOST
argument_list|(
literal|154
argument_list|)
block|;
DECL|field|code
specifier|private
specifier|final
name|int
name|code
decl_stmt|;
DECL|method|ExitCode (int exitCode)
specifier|private
name|ExitCode
parameter_list|(
name|int
name|exitCode
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|exitCode
expr_stmt|;
block|}
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|code
argument_list|)
return|;
block|}
block|}
comment|/**    * The constants for the signals.    */
DECL|enum|Signal
specifier|public
enum|enum
name|Signal
block|{
DECL|enumConstant|NULL
DECL|enumConstant|QUIT
name|NULL
argument_list|(
literal|0
argument_list|,
literal|"NULL"
argument_list|)
block|,
name|QUIT
argument_list|(
literal|3
argument_list|,
literal|"SIGQUIT"
argument_list|)
block|,
DECL|enumConstant|KILL
DECL|enumConstant|TERM
name|KILL
argument_list|(
literal|9
argument_list|,
literal|"SIGKILL"
argument_list|)
block|,
name|TERM
argument_list|(
literal|15
argument_list|,
literal|"SIGTERM"
argument_list|)
block|;
DECL|field|value
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
DECL|field|str
specifier|private
specifier|final
name|String
name|str
decl_stmt|;
DECL|method|Signal (int value, String str)
specifier|private
name|Signal
parameter_list|(
name|int
name|value
parameter_list|,
name|String
name|str
parameter_list|)
block|{
name|this
operator|.
name|str
operator|=
name|str
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|str
return|;
block|}
block|}
DECL|method|logOutput (String output)
specifier|protected
name|void
name|logOutput
parameter_list|(
name|String
name|output
parameter_list|)
block|{
name|String
name|shExecOutput
init|=
name|output
decl_stmt|;
if|if
condition|(
name|shExecOutput
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|str
range|:
name|shExecOutput
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get the pidFile of the container.    * @param containerId    * @return the path of the pid-file for the given containerId.    */
DECL|method|getPidFilePath (ContainerId containerId)
specifier|protected
name|Path
name|getPidFilePath
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
operator|(
name|this
operator|.
name|pidFiles
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
operator|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**     *  Return a command to execute the given command in OS shell.    *  On Windows, the passed in groupId can be used to launch    *  and associate the given groupId in a process group. On    *  non-Windows, groupId is ignored.     */
DECL|method|getRunCommand (String command, String groupId, String userName, Path pidFile, Configuration conf)
specifier|protected
name|String
index|[]
name|getRunCommand
parameter_list|(
name|String
name|command
parameter_list|,
name|String
name|groupId
parameter_list|,
name|String
name|userName
parameter_list|,
name|Path
name|pidFile
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|boolean
name|containerSchedPriorityIsSet
init|=
literal|false
decl_stmt|;
name|int
name|containerSchedPriorityAdjustment
init|=
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_EXECUTOR_SCHED_PRIORITY
decl_stmt|;
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_EXECUTOR_SCHED_PRIORITY
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|containerSchedPriorityIsSet
operator|=
literal|true
expr_stmt|;
name|containerSchedPriorityAdjustment
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_EXECUTOR_SCHED_PRIORITY
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_CONTAINER_EXECUTOR_SCHED_PRIORITY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
name|Shell
operator|.
name|WINUTILS
block|,
literal|"task"
block|,
literal|"create"
block|,
name|groupId
block|,
literal|"cmd /c "
operator|+
name|command
block|}
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|retCommand
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|containerSchedPriorityIsSet
condition|)
block|{
name|retCommand
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"nice"
argument_list|,
literal|"-n"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|containerSchedPriorityAdjustment
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|retCommand
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"bash"
argument_list|,
name|command
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|retCommand
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|retCommand
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
comment|/**    * Is the container still active?    * @param containerId    * @return true if the container is active else false.    */
DECL|method|isContainerActive (ContainerId containerId)
specifier|protected
name|boolean
name|isContainerActive
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
operator|(
name|this
operator|.
name|pidFiles
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
operator|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Mark the container as active    *     * @param containerId    *          the ContainerId    * @param pidFilePath    *          Path where the executor should write the pid of the launched    *          process    */
DECL|method|activateContainer (ContainerId containerId, Path pidFilePath)
specifier|public
name|void
name|activateContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Path
name|pidFilePath
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|this
operator|.
name|pidFiles
operator|.
name|put
argument_list|(
name|containerId
argument_list|,
name|pidFilePath
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Mark the container as inactive.    * Done iff the container is still active. Else treat it as    * a no-op    */
DECL|method|deactivateContainer (ContainerId containerId)
specifier|public
name|void
name|deactivateContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|this
operator|.
name|pidFiles
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get the process-identifier for the container    *     * @param containerID    * @return the processid of the container if it has already launched,    *         otherwise return null    */
DECL|method|getProcessId (ContainerId containerID)
specifier|public
name|String
name|getProcessId
parameter_list|(
name|ContainerId
name|containerID
parameter_list|)
block|{
name|String
name|pid
init|=
literal|null
decl_stmt|;
name|Path
name|pidFile
init|=
name|pidFiles
operator|.
name|get
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
if|if
condition|(
name|pidFile
operator|==
literal|null
condition|)
block|{
comment|// This container isn't even launched yet.
return|return
name|pid
return|;
block|}
try|try
block|{
name|pid
operator|=
name|ProcessIdFileReader
operator|.
name|getProcessId
argument_list|(
name|pidFile
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
literal|"Got exception reading pid from pid-file "
operator|+
name|pidFile
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|pid
return|;
block|}
DECL|class|DelayedProcessKiller
specifier|public
specifier|static
class|class
name|DelayedProcessKiller
extends|extends
name|Thread
block|{
DECL|field|container
specifier|private
name|Container
name|container
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|pid
specifier|private
specifier|final
name|String
name|pid
decl_stmt|;
DECL|field|delay
specifier|private
specifier|final
name|long
name|delay
decl_stmt|;
DECL|field|signal
specifier|private
specifier|final
name|Signal
name|signal
decl_stmt|;
DECL|field|containerExecutor
specifier|private
specifier|final
name|ContainerExecutor
name|containerExecutor
decl_stmt|;
DECL|method|DelayedProcessKiller (Container container, String user, String pid, long delay, Signal signal, ContainerExecutor containerExecutor)
specifier|public
name|DelayedProcessKiller
parameter_list|(
name|Container
name|container
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|pid
parameter_list|,
name|long
name|delay
parameter_list|,
name|Signal
name|signal
parameter_list|,
name|ContainerExecutor
name|containerExecutor
parameter_list|)
block|{
name|this
operator|.
name|container
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|pid
operator|=
name|pid
expr_stmt|;
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
name|this
operator|.
name|signal
operator|=
name|signal
expr_stmt|;
name|this
operator|.
name|containerExecutor
operator|=
name|containerExecutor
expr_stmt|;
name|setName
argument_list|(
literal|"Task killer for "
operator|+
name|pid
argument_list|)
expr_stmt|;
name|setDaemon
argument_list|(
literal|false
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
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|delay
argument_list|)
expr_stmt|;
name|containerExecutor
operator|.
name|signalContainer
argument_list|(
name|user
argument_list|,
name|pid
argument_list|,
name|signal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Exception when user "
operator|+
name|user
operator|+
literal|" killing task "
operator|+
name|pid
operator|+
literal|" in DelayedProcessKiller: "
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
name|container
operator|.
name|handle
argument_list|(
operator|new
name|ContainerDiagnosticsUpdateEvent
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

