begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements. See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership. The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License. You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged
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
name|linux
operator|.
name|privileged
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
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
name|InterfaceStability
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
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
name|Map
import|;
end_import

begin_comment
comment|/**  * provides mechanisms to execute PrivilegedContainerOperations *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|PrivilegedOperationExecutor
specifier|public
class|class
name|PrivilegedOperationExecutor
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
name|PrivilegedOperationExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|instance
specifier|private
specifier|volatile
specifier|static
name|PrivilegedOperationExecutor
name|instance
decl_stmt|;
DECL|field|containerExecutorExe
specifier|private
name|String
name|containerExecutorExe
decl_stmt|;
DECL|method|getContainerExecutorExecutablePath (Configuration conf)
specifier|public
specifier|static
name|String
name|getContainerExecutorExecutablePath
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|yarnHomeEnvVar
init|=
name|System
operator|.
name|getenv
argument_list|(
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|HADOOP_YARN_HOME
operator|.
name|key
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|hadoopBin
init|=
operator|new
name|File
argument_list|(
name|yarnHomeEnvVar
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
DECL|method|init (Configuration conf)
specifier|private
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|containerExecutorExe
operator|=
name|getContainerExecutorExecutablePath
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|PrivilegedOperationExecutor (Configuration conf)
specifier|private
name|PrivilegedOperationExecutor
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getInstance (Configuration conf)
specifier|public
specifier|static
name|PrivilegedOperationExecutor
name|getInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|PrivilegedOperationExecutor
operator|.
name|class
init|)
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|PrivilegedOperationExecutor
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|instance
return|;
block|}
comment|/**    * @param prefixCommands in some cases ( e.g priorities using nice ),    *                       prefix commands are necessary    * @param operation      the type and arguments for the operation to be    *                       executed    * @return execution string array for priviledged operation    */
DECL|method|getPrivilegedOperationExecutionCommand (List<String> prefixCommands, PrivilegedOperation operation)
specifier|public
name|String
index|[]
name|getPrivilegedOperationExecutionCommand
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|prefixCommands
parameter_list|,
name|PrivilegedOperation
name|operation
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fullCommand
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
name|prefixCommands
operator|!=
literal|null
operator|&&
operator|!
name|prefixCommands
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fullCommand
operator|.
name|addAll
argument_list|(
name|prefixCommands
argument_list|)
expr_stmt|;
block|}
name|fullCommand
operator|.
name|add
argument_list|(
name|containerExecutorExe
argument_list|)
expr_stmt|;
name|fullCommand
operator|.
name|add
argument_list|(
name|operation
operator|.
name|getOperationType
argument_list|()
operator|.
name|getOption
argument_list|()
argument_list|)
expr_stmt|;
name|fullCommand
operator|.
name|addAll
argument_list|(
name|operation
operator|.
name|getArguments
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|fullCommandArray
init|=
name|fullCommand
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fullCommand
operator|.
name|size
argument_list|()
index|]
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
literal|"Privileged Execution Command Array: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|fullCommandArray
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fullCommandArray
return|;
block|}
comment|/**    * Executes a privileged operation. It is up to the callers to ensure that    * each privileged operation's parameters are constructed correctly. The    * parameters are passed verbatim to the container-executor binary.    *    * @param prefixCommands in some cases ( e.g priorities using nice ),    *                       prefix commands are necessary    * @param operation      the type and arguments for the operation to be executed    * @param workingDir     (optional) working directory for execution    * @param env            (optional) env of the command will include specified vars    * @param grabOutput     return (possibly large) shell command output    * @return stdout contents from shell executor - useful for some privileged    * operations - e.g --tc_read    * @throws org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationException    */
DECL|method|executePrivilegedOperation (List<String> prefixCommands, PrivilegedOperation operation, File workingDir, Map<String, String> env, boolean grabOutput)
specifier|public
name|String
name|executePrivilegedOperation
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|prefixCommands
parameter_list|,
name|PrivilegedOperation
name|operation
parameter_list|,
name|File
name|workingDir
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|,
name|boolean
name|grabOutput
parameter_list|)
throws|throws
name|PrivilegedOperationException
block|{
name|String
index|[]
name|fullCommandArray
init|=
name|getPrivilegedOperationExecutionCommand
argument_list|(
name|prefixCommands
argument_list|,
name|operation
argument_list|)
decl_stmt|;
name|ShellCommandExecutor
name|exec
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|fullCommandArray
argument_list|,
name|workingDir
argument_list|,
name|env
argument_list|)
decl_stmt|;
try|try
block|{
name|exec
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Privileged Execution Operation Output:"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|exec
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
name|String
name|logLine
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"Shell execution returned exit code: "
argument_list|)
operator|.
name|append
argument_list|(
name|exec
operator|.
name|getExitCode
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|". Privileged Execution Operation Output: "
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|exec
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|logLine
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PrivilegedOperationException
argument_list|(
name|e
argument_list|)
throw|;
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
literal|"IOException executing command: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PrivilegedOperationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|grabOutput
condition|)
block|{
return|return
name|exec
operator|.
name|getOutput
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Executes a privileged operation. It is up to the callers to ensure that    * each privileged operation's parameters are constructed correctly. The    * parameters are passed verbatim to the container-executor binary.    *    * @param operation  the type and arguments for the operation to be executed    * @param grabOutput return (possibly large) shell command output    * @return stdout contents from shell executor - useful for some privileged    * operations - e.g --tc_read    * @throws org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationException    */
DECL|method|executePrivilegedOperation (PrivilegedOperation operation, boolean grabOutput)
specifier|public
name|String
name|executePrivilegedOperation
parameter_list|(
name|PrivilegedOperation
name|operation
parameter_list|,
name|boolean
name|grabOutput
parameter_list|)
throws|throws
name|PrivilegedOperationException
block|{
return|return
name|executePrivilegedOperation
argument_list|(
literal|null
argument_list|,
name|operation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|grabOutput
argument_list|)
return|;
block|}
comment|//Utility functions for squashing together operations in supported ways
comment|//At some point, we need to create a generalized mechanism that uses a set
comment|//of squashing 'rules' to squash an set of PrivilegedOperations of varying
comment|//types - e.g Launch Container + Add Pid to CGroup(s) + TC rules
comment|/**    * Squash operations for cgroups - e.g mount, add pid to cgroup etc .,    * For now, we only implement squashing for 'add pid to cgroup' since this    * is the only optimization relevant to launching containers    *    * @return single squashed cgroup operation. Null on failure.    */
DECL|method|squashCGroupOperations (List<PrivilegedOperation> ops)
specifier|public
specifier|static
name|PrivilegedOperation
name|squashCGroupOperations
parameter_list|(
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|ops
parameter_list|)
throws|throws
name|PrivilegedOperationException
block|{
if|if
condition|(
name|ops
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StringBuffer
name|finalOpArg
init|=
operator|new
name|StringBuffer
argument_list|(
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_PREFIX
argument_list|)
decl_stmt|;
name|boolean
name|noneArgsOnly
init|=
literal|true
decl_stmt|;
for|for
control|(
name|PrivilegedOperation
name|op
range|:
name|ops
control|)
block|{
if|if
condition|(
operator|!
name|op
operator|.
name|getOperationType
argument_list|()
operator|.
name|equals
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|ADD_PID_TO_CGROUP
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unsupported operation type: "
operator|+
name|op
operator|.
name|getOperationType
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PrivilegedOperationException
argument_list|(
literal|"Unsupported operation type:"
operator|+
name|op
operator|.
name|getOperationType
argument_list|()
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
name|op
operator|.
name|getArguments
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid number of args: "
operator|+
name|args
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PrivilegedOperationException
argument_list|(
literal|"Invalid number of args: "
operator|+
name|args
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|arg
init|=
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|tasksFile
init|=
name|StringUtils
operator|.
name|substringAfter
argument_list|(
name|arg
argument_list|,
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_PREFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|tasksFile
operator|==
literal|null
operator|||
name|tasksFile
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid argument: "
operator|+
name|arg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PrivilegedOperationException
argument_list|(
literal|"Invalid argument: "
operator|+
name|arg
argument_list|)
throw|;
block|}
if|if
condition|(
name|tasksFile
operator|.
name|equals
argument_list|(
literal|"none"
argument_list|)
condition|)
block|{
comment|//Don't append to finalOpArg
continue|continue;
block|}
if|if
condition|(
name|noneArgsOnly
operator|==
literal|false
condition|)
block|{
comment|//We have already appended at least one tasks file.
name|finalOpArg
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|finalOpArg
operator|.
name|append
argument_list|(
name|tasksFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|finalOpArg
operator|.
name|append
argument_list|(
name|tasksFile
argument_list|)
expr_stmt|;
name|noneArgsOnly
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|noneArgsOnly
condition|)
block|{
name|finalOpArg
operator|.
name|append
argument_list|(
literal|"none"
argument_list|)
expr_stmt|;
comment|//there were no tasks file to append
block|}
name|PrivilegedOperation
name|finalOp
init|=
operator|new
name|PrivilegedOperation
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|ADD_PID_TO_CGROUP
argument_list|,
name|finalOpArg
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|finalOp
return|;
block|}
block|}
end_class

end_unit

