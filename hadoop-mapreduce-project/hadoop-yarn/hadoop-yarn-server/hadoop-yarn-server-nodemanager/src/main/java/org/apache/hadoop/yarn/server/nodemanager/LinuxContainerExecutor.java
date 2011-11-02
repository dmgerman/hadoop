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
operator|.
name|ExitCodeException
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
operator|.
name|ShellCommandExecutor
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
name|localizer
operator|.
name|ContainerLocalizer
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
name|util
operator|.
name|ConverterUtils
import|;
end_import

begin_class
DECL|class|LinuxContainerExecutor
specifier|public
class|class
name|LinuxContainerExecutor
extends|extends
name|ContainerExecutor
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
name|LinuxContainerExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerExecutorExe
specifier|private
name|String
name|containerExecutorExe
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
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|containerExecutorExe
operator|=
name|getContainerExecutorExecutablePath
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * List of commands that the setuid script will execute.    */
DECL|enum|Commands
enum|enum
name|Commands
block|{
DECL|enumConstant|INITIALIZE_CONTAINER
name|INITIALIZE_CONTAINER
argument_list|(
literal|0
argument_list|)
block|,
DECL|enumConstant|LAUNCH_CONTAINER
name|LAUNCH_CONTAINER
argument_list|(
literal|1
argument_list|)
block|,
DECL|enumConstant|SIGNAL_CONTAINER
name|SIGNAL_CONTAINER
argument_list|(
literal|2
argument_list|)
block|,
DECL|enumConstant|DELETE_AS_USER
name|DELETE_AS_USER
argument_list|(
literal|3
argument_list|)
block|;
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
DECL|method|Commands (int value)
name|Commands
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
comment|/**    * Result codes returned from the C container-executor.    * These must match the values in container-executor.h.    */
DECL|enum|ResultCode
enum|enum
name|ResultCode
block|{
DECL|enumConstant|OK
name|OK
argument_list|(
literal|0
argument_list|)
block|,
DECL|enumConstant|INVALID_USER_NAME
name|INVALID_USER_NAME
argument_list|(
literal|2
argument_list|)
block|,
DECL|enumConstant|UNABLE_TO_EXECUTE_CONTAINER_SCRIPT
name|UNABLE_TO_EXECUTE_CONTAINER_SCRIPT
argument_list|(
literal|7
argument_list|)
block|,
DECL|enumConstant|INVALID_CONTAINER_PID
name|INVALID_CONTAINER_PID
argument_list|(
literal|9
argument_list|)
block|,
DECL|enumConstant|INVALID_CONTAINER_EXEC_PERMISSIONS
name|INVALID_CONTAINER_EXEC_PERMISSIONS
argument_list|(
literal|22
argument_list|)
block|,
DECL|enumConstant|INVALID_CONFIG_FILE
name|INVALID_CONFIG_FILE
argument_list|(
literal|24
argument_list|)
block|;
DECL|field|value
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
DECL|method|ResultCode (int value)
name|ResultCode
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|method|getContainerExecutorExecutablePath (Configuration conf)
specifier|protected
name|String
name|getContainerExecutorExecutablePath
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|File
name|hadoopBin
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getenv
argument_list|(
literal|"YARN_HOME"
argument_list|)
argument_list|,
literal|"bin"
argument_list|)
decl_stmt|;
name|String
name|defaultPath
init|=
operator|new
name|File
argument_list|(
name|hadoopBin
argument_list|,
literal|"container-executor"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
return|return
literal|null
operator|==
name|conf
condition|?
name|defaultPath
else|:
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_EXECUTOR_PATH
argument_list|,
name|defaultPath
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|startLocalizer (Path nmPrivateContainerTokensPath, InetSocketAddress nmAddr, String user, String appId, String locId, List<Path> localDirs)
specifier|public
name|void
name|startLocalizer
parameter_list|(
name|Path
name|nmPrivateContainerTokensPath
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
name|Path
argument_list|>
name|localDirs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|command
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|containerExecutorExe
argument_list|,
name|user
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|Commands
operator|.
name|INITIALIZE_CONTAINER
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|appId
argument_list|,
name|nmPrivateContainerTokensPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|jvm
init|=
comment|// use same jvm as parent
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|)
argument_list|,
literal|"bin"
argument_list|)
argument_list|,
literal|"java"
argument_list|)
decl_stmt|;
name|command
operator|.
name|add
argument_list|(
name|jvm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
literal|"-classpath"
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|javaLibPath
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.library.path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|javaLibPath
operator|!=
literal|null
condition|)
block|{
name|command
operator|.
name|add
argument_list|(
literal|"-Djava.library.path="
operator|+
name|javaLibPath
argument_list|)
expr_stmt|;
block|}
name|command
operator|.
name|add
argument_list|(
name|ContainerLocalizer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|locId
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|nmAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|nmAddr
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|p
range|:
name|localDirs
control|)
block|{
name|command
operator|.
name|add
argument_list|(
name|p
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|commandArray
init|=
name|command
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|command
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|ShellCommandExecutor
name|shExec
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|commandArray
argument_list|)
decl_stmt|;
comment|// TODO: DEBUG
name|LOG
operator|.
name|info
argument_list|(
literal|"initApplication: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|commandArray
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"initApplication: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|commandArray
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|shExec
operator|.
name|execute
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logOutput
argument_list|(
name|shExec
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ExitCodeException
name|e
parameter_list|)
block|{
name|int
name|exitCode
init|=
name|shExec
operator|.
name|getExitCode
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exit code from container is : "
operator|+
name|exitCode
argument_list|)
expr_stmt|;
name|logOutput
argument_list|(
name|shExec
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"App initialization failed ("
operator|+
name|exitCode
operator|+
literal|")"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|launchContainer (Container container, Path nmPrivateCotainerScriptPath, Path nmPrivateTokensPath, String user, String appId, Path containerWorkDir)
specifier|public
name|int
name|launchContainer
parameter_list|(
name|Container
name|container
parameter_list|,
name|Path
name|nmPrivateCotainerScriptPath
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
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerId
name|containerId
init|=
name|container
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|String
name|containerIdStr
init|=
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|ShellCommandExecutor
name|shExec
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Path
name|pidFilePath
init|=
name|getPidFilePath
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|pidFilePath
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|command
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|containerExecutorExe
argument_list|,
name|user
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|Commands
operator|.
name|LAUNCH_CONTAINER
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|appId
argument_list|,
name|containerIdStr
argument_list|,
name|containerWorkDir
operator|.
name|toString
argument_list|()
argument_list|,
name|nmPrivateCotainerScriptPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|nmPrivateTokensPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|pidFilePath
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|commandArray
init|=
name|command
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|command
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|shExec
operator|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|commandArray
argument_list|,
literal|null
argument_list|,
comment|// NM's cwd
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
argument_list|)
expr_stmt|;
comment|// sanitized env
comment|// DEBUG
name|LOG
operator|.
name|info
argument_list|(
literal|"launchContainer: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|commandArray
argument_list|)
argument_list|)
expr_stmt|;
name|shExec
operator|.
name|execute
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logOutput
argument_list|(
name|shExec
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Container was marked as inactive. Returning terminated error"
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
block|}
catch|catch
parameter_list|(
name|ExitCodeException
name|e
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|shExec
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|exitCode
init|=
name|shExec
operator|.
name|getExitCode
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exit code from container is : "
operator|+
name|exitCode
argument_list|)
expr_stmt|;
comment|// 143 (SIGTERM) and 137 (SIGKILL) exit codes means the container was
comment|// terminated/killed forcefully. In all other cases, log the
comment|// container-executor's output
if|if
condition|(
name|exitCode
operator|!=
name|ExitCode
operator|.
name|FORCE_KILLED
operator|.
name|getExitCode
argument_list|()
operator|&&
name|exitCode
operator|!=
name|ExitCode
operator|.
name|TERMINATED
operator|.
name|getExitCode
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception from container-launch : "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|logOutput
argument_list|(
name|shExec
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|diagnostics
init|=
literal|"Exception from container-launch: \n"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
operator|+
literal|"\n"
operator|+
name|shExec
operator|.
name|getOutput
argument_list|()
decl_stmt|;
name|container
operator|.
name|handle
argument_list|(
operator|new
name|ContainerDiagnosticsUpdateEvent
argument_list|(
name|containerId
argument_list|,
name|diagnostics
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|container
operator|.
name|handle
argument_list|(
operator|new
name|ContainerDiagnosticsUpdateEvent
argument_list|(
name|containerId
argument_list|,
literal|"Container killed on request. Exit code is "
operator|+
name|exitCode
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|exitCode
return|;
block|}
finally|finally
block|{
empty_stmt|;
comment|//
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Output from LinuxContainerExecutor's launchContainer follows:"
argument_list|)
expr_stmt|;
name|logOutput
argument_list|(
name|shExec
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|signalContainer (String user, String pid, Signal signal)
specifier|public
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
block|{
name|String
index|[]
name|command
init|=
operator|new
name|String
index|[]
block|{
name|containerExecutorExe
block|,
name|user
block|,
name|Integer
operator|.
name|toString
argument_list|(
name|Commands
operator|.
name|SIGNAL_CONTAINER
operator|.
name|getValue
argument_list|()
argument_list|)
block|,
name|pid
block|,
name|Integer
operator|.
name|toString
argument_list|(
name|signal
operator|.
name|getValue
argument_list|()
argument_list|)
block|}
decl_stmt|;
name|ShellCommandExecutor
name|shExec
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"signalContainer: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|command
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|shExec
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitCodeException
name|e
parameter_list|)
block|{
name|int
name|ret_code
init|=
name|shExec
operator|.
name|getExitCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|ret_code
operator|==
name|ResultCode
operator|.
name|INVALID_CONTAINER_PID
operator|.
name|getValue
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|logOutput
argument_list|(
name|shExec
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Problem signalling container "
operator|+
name|pid
operator|+
literal|" with "
operator|+
name|signal
operator|+
literal|"; exit = "
operator|+
name|ret_code
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|deleteAsUser (String user, Path dir, Path... baseDirs)
specifier|public
name|void
name|deleteAsUser
parameter_list|(
name|String
name|user
parameter_list|,
name|Path
name|dir
parameter_list|,
name|Path
modifier|...
name|baseDirs
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|command
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|containerExecutorExe
argument_list|,
name|user
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|Commands
operator|.
name|DELETE_AS_USER
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|dir
operator|==
literal|null
condition|?
literal|""
else|:
name|dir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseDirs
operator|==
literal|null
operator|||
name|baseDirs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting absolute path : "
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Path
name|baseDir
range|:
name|baseDirs
control|)
block|{
name|Path
name|del
init|=
name|dir
operator|==
literal|null
condition|?
name|baseDir
else|:
operator|new
name|Path
argument_list|(
name|baseDir
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting path : "
operator|+
name|del
argument_list|)
expr_stmt|;
name|command
operator|.
name|add
argument_list|(
name|baseDir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|commandArray
init|=
name|command
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|command
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|ShellCommandExecutor
name|shExec
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|commandArray
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|" -- DEBUG -- deleteAsUser: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|commandArray
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"deleteAsUser: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|commandArray
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|shExec
operator|.
name|execute
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logOutput
argument_list|(
name|shExec
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|int
name|exitCode
init|=
name|shExec
operator|.
name|getExitCode
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exit code from container is : "
operator|+
name|exitCode
argument_list|)
expr_stmt|;
if|if
condition|(
name|exitCode
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"DeleteAsUser for "
operator|+
name|dir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|+
literal|" returned with non-zero exit code"
operator|+
name|exitCode
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Output from LinuxContainerExecutor's deleteAsUser follows:"
argument_list|)
expr_stmt|;
name|logOutput
argument_list|(
name|shExec
operator|.
name|getOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

