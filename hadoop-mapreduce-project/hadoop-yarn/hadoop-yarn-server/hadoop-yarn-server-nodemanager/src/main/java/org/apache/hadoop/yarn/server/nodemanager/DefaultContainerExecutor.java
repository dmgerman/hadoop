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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CreateFlag
operator|.
name|CREATE
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
name|fs
operator|.
name|CreateFlag
operator|.
name|OVERWRITE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|io
operator|.
name|PrintStream
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
name|EnumSet
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
DECL|class|DefaultContainerExecutor
specifier|public
class|class
name|DefaultContainerExecutor
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
name|DefaultContainerExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|lfs
specifier|private
specifier|final
name|FileContext
name|lfs
decl_stmt|;
DECL|field|WRAPPER_LAUNCH_SCRIPT
specifier|private
specifier|static
specifier|final
name|String
name|WRAPPER_LAUNCH_SCRIPT
init|=
literal|"default_container_executor.sh"
decl_stmt|;
DECL|method|DefaultContainerExecutor ()
specifier|public
name|DefaultContainerExecutor
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|lfs
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|DefaultContainerExecutor (FileContext lfs)
name|DefaultContainerExecutor
parameter_list|(
name|FileContext
name|lfs
parameter_list|)
block|{
name|this
operator|.
name|lfs
operator|=
name|lfs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do or verify here
block|}
annotation|@
name|Override
DECL|method|startLocalizer (Path nmPrivateContainerTokensPath, InetSocketAddress nmAddr, String user, String appId, String locId, List<String> localDirs, List<String> logDirs)
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
block|{
name|ContainerLocalizer
name|localizer
init|=
operator|new
name|ContainerLocalizer
argument_list|(
name|lfs
argument_list|,
name|user
argument_list|,
name|appId
argument_list|,
name|locId
argument_list|,
name|getPaths
argument_list|(
name|localDirs
argument_list|)
argument_list|,
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|getConf
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|createUserLocalDirs
argument_list|(
name|localDirs
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|createUserCacheDirs
argument_list|(
name|localDirs
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|createAppDirs
argument_list|(
name|localDirs
argument_list|,
name|user
argument_list|,
name|appId
argument_list|)
expr_stmt|;
name|createAppLogDirs
argument_list|(
name|appId
argument_list|,
name|logDirs
argument_list|)
expr_stmt|;
comment|// TODO: Why pick first app dir. The same in LCE why not random?
name|Path
name|appStorageDir
init|=
name|getFirstApplicationDir
argument_list|(
name|localDirs
argument_list|,
name|user
argument_list|,
name|appId
argument_list|)
decl_stmt|;
name|String
name|tokenFn
init|=
name|String
operator|.
name|format
argument_list|(
name|ContainerLocalizer
operator|.
name|TOKEN_FILE_NAME_FMT
argument_list|,
name|locId
argument_list|)
decl_stmt|;
name|Path
name|tokenDst
init|=
operator|new
name|Path
argument_list|(
name|appStorageDir
argument_list|,
name|tokenFn
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|util
argument_list|()
operator|.
name|copy
argument_list|(
name|nmPrivateContainerTokensPath
argument_list|,
name|tokenDst
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Copying from "
operator|+
name|nmPrivateContainerTokensPath
operator|+
literal|" to "
operator|+
name|tokenDst
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|setWorkingDirectory
argument_list|(
name|appStorageDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"CWD set to "
operator|+
name|appStorageDir
operator|+
literal|" = "
operator|+
name|lfs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: DO it over RPC for maintaining similarity?
name|localizer
operator|.
name|runLocalization
argument_list|(
name|nmAddr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|launchContainer (Container container, Path nmPrivateContainerScriptPath, Path nmPrivateTokensPath, String userName, String appId, Path containerWorkDir, List<String> localDirs, List<String> logDirs)
specifier|public
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
name|userName
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
block|{
name|ContainerId
name|containerId
init|=
name|container
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
comment|// create container dirs on all disks
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
name|String
name|appIdStr
init|=
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|container
operator|.
name|getContainerID
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|sLocalDir
range|:
name|localDirs
control|)
block|{
name|Path
name|usersdir
init|=
operator|new
name|Path
argument_list|(
name|sLocalDir
argument_list|,
name|ContainerLocalizer
operator|.
name|USERCACHE
argument_list|)
decl_stmt|;
name|Path
name|userdir
init|=
operator|new
name|Path
argument_list|(
name|usersdir
argument_list|,
name|userName
argument_list|)
decl_stmt|;
name|Path
name|appCacheDir
init|=
operator|new
name|Path
argument_list|(
name|userdir
argument_list|,
name|ContainerLocalizer
operator|.
name|APPCACHE
argument_list|)
decl_stmt|;
name|Path
name|appDir
init|=
operator|new
name|Path
argument_list|(
name|appCacheDir
argument_list|,
name|appIdStr
argument_list|)
decl_stmt|;
name|Path
name|containerDir
init|=
operator|new
name|Path
argument_list|(
name|appDir
argument_list|,
name|containerIdStr
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|mkdir
argument_list|(
name|containerDir
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// Create the container log-dirs on all disks
name|createContainerLogDirs
argument_list|(
name|appIdStr
argument_list|,
name|containerIdStr
argument_list|,
name|logDirs
argument_list|)
expr_stmt|;
comment|// copy launch script to work dir
name|Path
name|launchDst
init|=
operator|new
name|Path
argument_list|(
name|containerWorkDir
argument_list|,
name|ContainerLaunch
operator|.
name|CONTAINER_SCRIPT
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|util
argument_list|()
operator|.
name|copy
argument_list|(
name|nmPrivateContainerScriptPath
argument_list|,
name|launchDst
argument_list|)
expr_stmt|;
comment|// copy container tokens to work dir
name|Path
name|tokenDst
init|=
operator|new
name|Path
argument_list|(
name|containerWorkDir
argument_list|,
name|ContainerLaunch
operator|.
name|FINAL_CONTAINER_TOKENS_FILE
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|util
argument_list|()
operator|.
name|copy
argument_list|(
name|nmPrivateTokensPath
argument_list|,
name|tokenDst
argument_list|)
expr_stmt|;
comment|// Create new local launch wrapper script
name|Path
name|wrapperScriptDst
init|=
operator|new
name|Path
argument_list|(
name|containerWorkDir
argument_list|,
name|WRAPPER_LAUNCH_SCRIPT
argument_list|)
decl_stmt|;
name|DataOutputStream
name|wrapperScriptOutStream
init|=
name|lfs
operator|.
name|create
argument_list|(
name|wrapperScriptDst
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CREATE
argument_list|,
name|OVERWRITE
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|pidFile
init|=
name|getPidFilePath
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|pidFile
operator|!=
literal|null
condition|)
block|{
name|writeLocalWrapperScript
argument_list|(
name|wrapperScriptOutStream
argument_list|,
name|launchDst
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
name|pidFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Container "
operator|+
name|containerIdStr
operator|+
literal|" was marked as inactive. Returning terminated error"
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
comment|// create log dir under app
comment|// fork script
name|ShellCommandExecutor
name|shExec
init|=
literal|null
decl_stmt|;
try|try
block|{
name|lfs
operator|.
name|setPermission
argument_list|(
name|launchDst
argument_list|,
name|ContainerExecutor
operator|.
name|TASK_LAUNCH_SCRIPT_PERMISSION
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|setPermission
argument_list|(
name|wrapperScriptDst
argument_list|,
name|ContainerExecutor
operator|.
name|TASK_LAUNCH_SCRIPT_PERMISSION
argument_list|)
expr_stmt|;
comment|// Setup command to run
name|String
index|[]
name|command
init|=
block|{
literal|"bash"
block|,
literal|"-c"
block|,
name|wrapperScriptDst
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
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
name|command
argument_list|)
argument_list|)
expr_stmt|;
name|shExec
operator|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|command
argument_list|,
operator|new
name|File
argument_list|(
name|containerWorkDir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
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
if|if
condition|(
name|isContainerActive
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|shExec
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Container "
operator|+
name|containerIdStr
operator|+
literal|" was marked as inactive. Returning terminated error"
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
name|IOException
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
literal|"Exit code from task is : "
operator|+
name|exitCode
argument_list|)
expr_stmt|;
name|String
name|message
init|=
name|shExec
operator|.
name|getOutput
argument_list|()
decl_stmt|;
name|logOutput
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
name|containerId
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|exitCode
return|;
block|}
finally|finally
block|{
empty_stmt|;
comment|//
block|}
return|return
literal|0
return|;
block|}
DECL|method|writeLocalWrapperScript (DataOutputStream out, String launchScriptDst, String pidFilePath)
specifier|private
name|void
name|writeLocalWrapperScript
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|String
name|launchScriptDst
parameter_list|,
name|String
name|pidFilePath
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We need to do a move as writing to a file is not atomic
comment|// Process reading a file being written to may get garbled data
comment|// hence write pid to tmp file first followed by a mv
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"#!/bin/bash\n\n"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"echo $$> "
operator|+
name|pidFilePath
operator|+
literal|".tmp\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"/bin/mv -f "
operator|+
name|pidFilePath
operator|+
literal|".tmp "
operator|+
name|pidFilePath
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ContainerExecutor
operator|.
name|isSetsidAvailable
condition|?
literal|"exec setsid"
else|:
literal|"exec"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" /bin/bash "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"-c "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|launchScriptDst
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\"\n"
argument_list|)
expr_stmt|;
name|PrintStream
name|pout
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pout
operator|=
operator|new
name|PrintStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|pout
operator|.
name|append
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
specifier|final
name|String
name|sigpid
init|=
name|ContainerExecutor
operator|.
name|isSetsidAvailable
condition|?
literal|"-"
operator|+
name|pid
else|:
name|pid
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending signal "
operator|+
name|signal
operator|.
name|getValue
argument_list|()
operator|+
literal|" to pid "
operator|+
name|sigpid
operator|+
literal|" as user "
operator|+
name|user
argument_list|)
expr_stmt|;
try|try
block|{
name|sendSignal
argument_list|(
name|sigpid
argument_list|,
name|Signal
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExitCodeException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|sendSignal
argument_list|(
name|sigpid
argument_list|,
name|signal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
try|try
block|{
name|sendSignal
argument_list|(
name|sigpid
argument_list|,
name|Signal
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
throw|throw
name|e
throw|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Send a specified signal to the specified pid    *    * @param pid the pid of the process [group] to signal.    * @param signal signal to send    * (for logging).    */
DECL|method|sendSignal (String pid, Signal signal)
specifier|protected
name|void
name|sendSignal
parameter_list|(
name|String
name|pid
parameter_list|,
name|Signal
name|signal
parameter_list|)
throws|throws
name|IOException
block|{
name|ShellCommandExecutor
name|shexec
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|arg
init|=
block|{
literal|"kill"
block|,
literal|"-"
operator|+
name|signal
operator|.
name|getValue
argument_list|()
block|,
name|pid
block|}
decl_stmt|;
name|shexec
operator|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|arg
argument_list|)
expr_stmt|;
name|shexec
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteAsUser (String user, Path subDir, Path... baseDirs)
specifier|public
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
name|baseDirs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
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
name|subDir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|lfs
operator|.
name|delete
argument_list|(
name|subDir
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|//Maybe retry
name|LOG
operator|.
name|warn
argument_list|(
literal|"delete returned false for path: ["
operator|+
name|subDir
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
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
name|subDir
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
name|subDir
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
if|if
condition|(
operator|!
name|lfs
operator|.
name|delete
argument_list|(
name|del
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"delete returned false for path: ["
operator|+
name|del
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Permissions for user dir.    * $loaal.dir/usercache/$user */
DECL|field|USER_PERM
specifier|private
specifier|static
specifier|final
name|short
name|USER_PERM
init|=
operator|(
name|short
operator|)
literal|0750
decl_stmt|;
comment|/** Permissions for user appcache dir.    * $loaal.dir/usercache/$user/appcache */
DECL|field|APPCACHE_PERM
specifier|private
specifier|static
specifier|final
name|short
name|APPCACHE_PERM
init|=
operator|(
name|short
operator|)
literal|0710
decl_stmt|;
comment|/** Permissions for user filecache dir.    * $loaal.dir/usercache/$user/filecache */
DECL|field|FILECACHE_PERM
specifier|private
specifier|static
specifier|final
name|short
name|FILECACHE_PERM
init|=
operator|(
name|short
operator|)
literal|0710
decl_stmt|;
comment|/** Permissions for user app dir.    * $loaal.dir/usercache/$user/filecache */
DECL|field|APPDIR_PERM
specifier|private
specifier|static
specifier|final
name|short
name|APPDIR_PERM
init|=
operator|(
name|short
operator|)
literal|0710
decl_stmt|;
comment|/** Permissions for user log dir.    * $logdir/$user/$appId */
DECL|field|LOGDIR_PERM
specifier|private
specifier|static
specifier|final
name|short
name|LOGDIR_PERM
init|=
operator|(
name|short
operator|)
literal|0710
decl_stmt|;
DECL|method|getFirstApplicationDir (List<String> localDirs, String user, String appId)
specifier|private
name|Path
name|getFirstApplicationDir
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|appId
parameter_list|)
block|{
return|return
name|getApplicationDir
argument_list|(
operator|new
name|Path
argument_list|(
name|localDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|user
argument_list|,
name|appId
argument_list|)
return|;
block|}
DECL|method|getApplicationDir (Path base, String user, String appId)
specifier|private
name|Path
name|getApplicationDir
parameter_list|(
name|Path
name|base
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|appId
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getAppcacheDir
argument_list|(
name|base
argument_list|,
name|user
argument_list|)
argument_list|,
name|appId
argument_list|)
return|;
block|}
DECL|method|getUserCacheDir (Path base, String user)
specifier|private
name|Path
name|getUserCacheDir
parameter_list|(
name|Path
name|base
parameter_list|,
name|String
name|user
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|base
argument_list|,
name|ContainerLocalizer
operator|.
name|USERCACHE
argument_list|)
argument_list|,
name|user
argument_list|)
return|;
block|}
DECL|method|getAppcacheDir (Path base, String user)
specifier|private
name|Path
name|getAppcacheDir
parameter_list|(
name|Path
name|base
parameter_list|,
name|String
name|user
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getUserCacheDir
argument_list|(
name|base
argument_list|,
name|user
argument_list|)
argument_list|,
name|ContainerLocalizer
operator|.
name|APPCACHE
argument_list|)
return|;
block|}
DECL|method|getFileCacheDir (Path base, String user)
specifier|private
name|Path
name|getFileCacheDir
parameter_list|(
name|Path
name|base
parameter_list|,
name|String
name|user
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getUserCacheDir
argument_list|(
name|base
argument_list|,
name|user
argument_list|)
argument_list|,
name|ContainerLocalizer
operator|.
name|FILECACHE
argument_list|)
return|;
block|}
comment|/**    * Initialize the local directories for a particular user.    *<ul>    *<li>$local.dir/usercache/$user</li>    *</ul>    */
DECL|method|createUserLocalDirs (List<String> localDirs, String user)
specifier|private
name|void
name|createUserLocalDirs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|userDirStatus
init|=
literal|false
decl_stmt|;
name|FsPermission
name|userperms
init|=
operator|new
name|FsPermission
argument_list|(
name|USER_PERM
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|localDir
range|:
name|localDirs
control|)
block|{
comment|// create $local.dir/usercache/$user and its immediate parent
try|try
block|{
name|lfs
operator|.
name|mkdir
argument_list|(
name|getUserCacheDir
argument_list|(
operator|new
name|Path
argument_list|(
name|localDir
argument_list|)
argument_list|,
name|user
argument_list|)
argument_list|,
name|userperms
argument_list|,
literal|true
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
literal|"Unable to create the user directory : "
operator|+
name|localDir
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|userDirStatus
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|userDirStatus
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to initialize user directories "
operator|+
literal|"in any of the configured local directories for user "
operator|+
name|user
argument_list|)
throw|;
block|}
block|}
comment|/**    * Initialize the local cache directories for a particular user.    *<ul>    *<li>$local.dir/usercache/$user</li>    *<li>$local.dir/usercache/$user/appcache</li>    *<li>$local.dir/usercache/$user/filecache</li>    *</ul>    */
DECL|method|createUserCacheDirs (List<String> localDirs, String user)
specifier|private
name|void
name|createUserCacheDirs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing user "
operator|+
name|user
argument_list|)
expr_stmt|;
name|boolean
name|appcacheDirStatus
init|=
literal|false
decl_stmt|;
name|boolean
name|distributedCacheDirStatus
init|=
literal|false
decl_stmt|;
name|FsPermission
name|appCachePerms
init|=
operator|new
name|FsPermission
argument_list|(
name|APPCACHE_PERM
argument_list|)
decl_stmt|;
name|FsPermission
name|fileperms
init|=
operator|new
name|FsPermission
argument_list|(
name|FILECACHE_PERM
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|localDir
range|:
name|localDirs
control|)
block|{
comment|// create $local.dir/usercache/$user/appcache
name|Path
name|localDirPath
init|=
operator|new
name|Path
argument_list|(
name|localDir
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|appDir
init|=
name|getAppcacheDir
argument_list|(
name|localDirPath
argument_list|,
name|user
argument_list|)
decl_stmt|;
try|try
block|{
name|lfs
operator|.
name|mkdir
argument_list|(
name|appDir
argument_list|,
name|appCachePerms
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|appcacheDirStatus
operator|=
literal|true
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
literal|"Unable to create app cache directory : "
operator|+
name|appDir
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// create $local.dir/usercache/$user/filecache
specifier|final
name|Path
name|distDir
init|=
name|getFileCacheDir
argument_list|(
name|localDirPath
argument_list|,
name|user
argument_list|)
decl_stmt|;
try|try
block|{
name|lfs
operator|.
name|mkdir
argument_list|(
name|distDir
argument_list|,
name|fileperms
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|distributedCacheDirStatus
operator|=
literal|true
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
literal|"Unable to create file cache directory : "
operator|+
name|distDir
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|appcacheDirStatus
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to initialize app-cache directories "
operator|+
literal|"in any of the configured local directories for user "
operator|+
name|user
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|distributedCacheDirStatus
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to initialize distributed-cache directories "
operator|+
literal|"in any of the configured local directories for user "
operator|+
name|user
argument_list|)
throw|;
block|}
block|}
comment|/**    * Initialize the local directories for a particular user.    *<ul>    *<li>$local.dir/usercache/$user/appcache/$appid</li>    *</ul>    * @param localDirs     */
DECL|method|createAppDirs (List<String> localDirs, String user, String appId)
specifier|private
name|void
name|createAppDirs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|initAppDirStatus
init|=
literal|false
decl_stmt|;
name|FsPermission
name|appperms
init|=
operator|new
name|FsPermission
argument_list|(
name|APPDIR_PERM
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|localDir
range|:
name|localDirs
control|)
block|{
name|Path
name|fullAppDir
init|=
name|getApplicationDir
argument_list|(
operator|new
name|Path
argument_list|(
name|localDir
argument_list|)
argument_list|,
name|user
argument_list|,
name|appId
argument_list|)
decl_stmt|;
comment|// create $local.dir/usercache/$user/appcache/$appId
try|try
block|{
name|lfs
operator|.
name|mkdir
argument_list|(
name|fullAppDir
argument_list|,
name|appperms
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|initAppDirStatus
operator|=
literal|true
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
literal|"Unable to create app directory "
operator|+
name|fullAppDir
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|initAppDirStatus
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to initialize app directories "
operator|+
literal|"in any of the configured local directories for app "
operator|+
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create application log directories on all disks.    */
DECL|method|createAppLogDirs (String appId, List<String> logDirs)
specifier|private
name|void
name|createAppLogDirs
parameter_list|(
name|String
name|appId
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|appLogDirStatus
init|=
literal|false
decl_stmt|;
name|FsPermission
name|appLogDirPerms
init|=
operator|new
name|FsPermission
argument_list|(
name|LOGDIR_PERM
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|rootLogDir
range|:
name|logDirs
control|)
block|{
comment|// create $log.dir/$appid
name|Path
name|appLogDir
init|=
operator|new
name|Path
argument_list|(
name|rootLogDir
argument_list|,
name|appId
argument_list|)
decl_stmt|;
try|try
block|{
name|lfs
operator|.
name|mkdir
argument_list|(
name|appLogDir
argument_list|,
name|appLogDirPerms
argument_list|,
literal|true
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
literal|"Unable to create the app-log directory : "
operator|+
name|appLogDir
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|appLogDirStatus
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|appLogDirStatus
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to initialize app-log directories "
operator|+
literal|"in any of the configured local directories for app "
operator|+
name|appId
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create application log directories on all disks.    */
DECL|method|createContainerLogDirs (String appId, String containerId, List<String> logDirs)
specifier|private
name|void
name|createContainerLogDirs
parameter_list|(
name|String
name|appId
parameter_list|,
name|String
name|containerId
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|containerLogDirStatus
init|=
literal|false
decl_stmt|;
name|FsPermission
name|containerLogDirPerms
init|=
operator|new
name|FsPermission
argument_list|(
name|LOGDIR_PERM
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|rootLogDir
range|:
name|logDirs
control|)
block|{
comment|// create $log.dir/$appid/$containerid
name|Path
name|appLogDir
init|=
operator|new
name|Path
argument_list|(
name|rootLogDir
argument_list|,
name|appId
argument_list|)
decl_stmt|;
name|Path
name|containerLogDir
init|=
operator|new
name|Path
argument_list|(
name|appLogDir
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
try|try
block|{
name|lfs
operator|.
name|mkdir
argument_list|(
name|containerLogDir
argument_list|,
name|containerLogDirPerms
argument_list|,
literal|true
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
literal|"Unable to create the container-log directory : "
operator|+
name|appLogDir
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|containerLogDirStatus
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|containerLogDirStatus
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to initialize container-log directories "
operator|+
literal|"in any of the configured local directories for container "
operator|+
name|containerId
argument_list|)
throw|;
block|}
block|}
comment|/**    * @return the list of paths of given local directories    */
DECL|method|getPaths (List<String> dirs)
specifier|private
specifier|static
name|List
argument_list|<
name|Path
argument_list|>
name|getPaths
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|dirs
parameter_list|)
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|(
name|dirs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dirs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|dirs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|paths
return|;
block|}
block|}
end_class

end_unit

