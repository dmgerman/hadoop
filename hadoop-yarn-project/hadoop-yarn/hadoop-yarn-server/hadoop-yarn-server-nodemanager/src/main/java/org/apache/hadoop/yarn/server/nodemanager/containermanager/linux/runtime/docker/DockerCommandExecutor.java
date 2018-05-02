begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime.docker
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
name|runtime
operator|.
name|docker
package|;
end_package

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
name|containermanager
operator|.
name|linux
operator|.
name|privileged
operator|.
name|PrivilegedOperation
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
name|privileged
operator|.
name|PrivilegedOperationException
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
name|privileged
operator|.
name|PrivilegedOperationExecutor
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
name|runtime
operator|.
name|ContainerExecutionException
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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Utility class for executing common docker operations.  */
end_comment

begin_class
DECL|class|DockerCommandExecutor
specifier|public
specifier|final
class|class
name|DockerCommandExecutor
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
name|DockerCommandExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Potential states that the docker status can return.    */
DECL|enum|DockerContainerStatus
specifier|public
enum|enum
name|DockerContainerStatus
block|{
DECL|enumConstant|CREATED
name|CREATED
argument_list|(
literal|"created"
argument_list|)
block|,
DECL|enumConstant|RUNNING
name|RUNNING
argument_list|(
literal|"running"
argument_list|)
block|,
DECL|enumConstant|STOPPED
name|STOPPED
argument_list|(
literal|"stopped"
argument_list|)
block|,
DECL|enumConstant|RESTARTING
name|RESTARTING
argument_list|(
literal|"restarting"
argument_list|)
block|,
DECL|enumConstant|REMOVING
name|REMOVING
argument_list|(
literal|"removing"
argument_list|)
block|,
DECL|enumConstant|DEAD
name|DEAD
argument_list|(
literal|"dead"
argument_list|)
block|,
DECL|enumConstant|EXITED
name|EXITED
argument_list|(
literal|"exited"
argument_list|)
block|,
DECL|enumConstant|NONEXISTENT
name|NONEXISTENT
argument_list|(
literal|"nonexistent"
argument_list|)
block|,
DECL|enumConstant|UNKNOWN
name|UNKNOWN
argument_list|(
literal|"unknown"
argument_list|)
block|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|DockerContainerStatus (String name)
name|DockerContainerStatus
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
DECL|method|DockerCommandExecutor ()
specifier|private
name|DockerCommandExecutor
parameter_list|()
block|{   }
comment|/**    * Execute a docker command and return the output.    *    * @param dockerCommand               the docker command to run.    * @param containerId                 the id of the container.    * @param env                         environment for the container.    * @param conf                        the hadoop configuration.    * @param privilegedOperationExecutor the privileged operations executor.    * @param disableFailureLogging       disable logging for known rc failures.    * @return the output of the operation.    * @throws ContainerExecutionException if the operation fails.    */
DECL|method|executeDockerCommand (DockerCommand dockerCommand, String containerId, Map<String, String> env, Configuration conf, PrivilegedOperationExecutor privilegedOperationExecutor, boolean disableFailureLogging, Context nmContext)
specifier|public
specifier|static
name|String
name|executeDockerCommand
parameter_list|(
name|DockerCommand
name|dockerCommand
parameter_list|,
name|String
name|containerId
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|,
name|boolean
name|disableFailureLogging
parameter_list|,
name|Context
name|nmContext
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|PrivilegedOperation
name|dockerOp
init|=
name|dockerCommand
operator|.
name|preparePrivilegedOperation
argument_list|(
name|dockerCommand
argument_list|,
name|containerId
argument_list|,
name|env
argument_list|,
name|conf
argument_list|,
name|nmContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|disableFailureLogging
condition|)
block|{
name|dockerOp
operator|.
name|disableFailureLogging
argument_list|()
expr_stmt|;
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
literal|"Running docker command: "
operator|+
name|dockerCommand
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|String
name|result
init|=
name|privilegedOperationExecutor
operator|.
name|executePrivilegedOperation
argument_list|(
literal|null
argument_list|,
name|dockerOp
argument_list|,
literal|null
argument_list|,
name|env
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
operator|&&
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|result
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|PrivilegedOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Docker operation failed"
argument_list|,
name|e
operator|.
name|getExitCode
argument_list|()
argument_list|,
name|e
operator|.
name|getOutput
argument_list|()
argument_list|,
name|e
operator|.
name|getErrorOutput
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get the status of the docker container. This runs a docker inspect to    * get the status. If the container no longer exists, docker inspect throws    * an exception and the nonexistent status is returned.    *    * @param containerId                 the id of the container.    * @param conf                        the hadoop configuration.    * @param privilegedOperationExecutor the privileged operations executor.    * @return a {@link DockerContainerStatus} representing the current status.    */
DECL|method|getContainerStatus (String containerId, Configuration conf, PrivilegedOperationExecutor privilegedOperationExecutor, Context nmContext)
specifier|public
specifier|static
name|DockerContainerStatus
name|getContainerStatus
parameter_list|(
name|String
name|containerId
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|,
name|Context
name|nmContext
parameter_list|)
block|{
try|try
block|{
name|DockerContainerStatus
name|dockerContainerStatus
decl_stmt|;
name|String
name|currentContainerStatus
init|=
name|executeStatusCommand
argument_list|(
name|containerId
argument_list|,
name|conf
argument_list|,
name|privilegedOperationExecutor
argument_list|,
name|nmContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentContainerStatus
operator|==
literal|null
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|UNKNOWN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentContainerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|CREATED
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|CREATED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentContainerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|RUNNING
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|RUNNING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentContainerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|STOPPED
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|STOPPED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentContainerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|RESTARTING
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|RESTARTING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentContainerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|REMOVING
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|REMOVING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentContainerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|DEAD
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|DEAD
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentContainerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|EXITED
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|EXITED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentContainerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|NONEXISTENT
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|NONEXISTENT
expr_stmt|;
block|}
else|else
block|{
name|dockerContainerStatus
operator|=
name|DockerContainerStatus
operator|.
name|UNKNOWN
expr_stmt|;
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
literal|"Container Status: "
operator|+
name|dockerContainerStatus
operator|.
name|getName
argument_list|()
operator|+
literal|" ContainerId: "
operator|+
name|containerId
argument_list|)
expr_stmt|;
block|}
return|return
name|dockerContainerStatus
return|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
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
literal|"Container Status: "
operator|+
name|DockerContainerStatus
operator|.
name|NONEXISTENT
operator|.
name|getName
argument_list|()
operator|+
literal|" ContainerId: "
operator|+
name|containerId
argument_list|)
expr_stmt|;
block|}
return|return
name|DockerContainerStatus
operator|.
name|NONEXISTENT
return|;
block|}
block|}
comment|/**    * Execute the docker inspect command to retrieve the docker container's    * status.    *    * @param containerId                 the id of the container.    * @param conf                        the hadoop configuration.    * @param privilegedOperationExecutor the privileged operations executor.    * @return the current container status.    * @throws ContainerExecutionException if the docker operation fails to run.    */
DECL|method|executeStatusCommand (String containerId, Configuration conf, PrivilegedOperationExecutor privilegedOperationExecutor, Context nmContext)
specifier|private
specifier|static
name|String
name|executeStatusCommand
parameter_list|(
name|String
name|containerId
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|,
name|Context
name|nmContext
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|DockerInspectCommand
name|dockerInspectCommand
init|=
operator|new
name|DockerInspectCommand
argument_list|(
name|containerId
argument_list|)
operator|.
name|getContainerStatus
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|DockerCommandExecutor
operator|.
name|executeDockerCommand
argument_list|(
name|dockerInspectCommand
argument_list|,
name|containerId
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|,
name|privilegedOperationExecutor
argument_list|,
literal|true
argument_list|,
name|nmContext
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Is the container in a stoppable state?    *    * @param containerStatus   the container's {@link DockerContainerStatus}.    * @return                  is the container in a stoppable state.    */
DECL|method|isStoppable (DockerContainerStatus containerStatus)
specifier|public
specifier|static
name|boolean
name|isStoppable
parameter_list|(
name|DockerContainerStatus
name|containerStatus
parameter_list|)
block|{
if|if
condition|(
name|containerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|RUNNING
argument_list|)
operator|||
name|containerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|RESTARTING
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Is the container in a killable state?    *    * @param containerStatus   the container's {@link DockerContainerStatus}.    * @return                  is the container in a killable state.    */
DECL|method|isKillable (DockerContainerStatus containerStatus)
specifier|public
specifier|static
name|boolean
name|isKillable
parameter_list|(
name|DockerContainerStatus
name|containerStatus
parameter_list|)
block|{
return|return
name|isStoppable
argument_list|(
name|containerStatus
argument_list|)
return|;
block|}
comment|/**    * Is the container in a removable state?    *    * @param containerStatus   the container's {@link DockerContainerStatus}.    * @return                  is the container in a removable state.    */
DECL|method|isRemovable (DockerContainerStatus containerStatus)
specifier|public
specifier|static
name|boolean
name|isRemovable
parameter_list|(
name|DockerContainerStatus
name|containerStatus
parameter_list|)
block|{
return|return
operator|!
name|containerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|NONEXISTENT
argument_list|)
operator|&&
operator|!
name|containerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|UNKNOWN
argument_list|)
operator|&&
operator|!
name|containerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|REMOVING
argument_list|)
operator|&&
operator|!
name|containerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|RUNNING
argument_list|)
return|;
block|}
comment|/**    * Is the container in a startable state?    *    * @param containerStatus   the container's {@link DockerContainerStatus}.    * @return                  is the container in a startable state.    */
DECL|method|isStartable (DockerContainerStatus containerStatus)
specifier|public
specifier|static
name|boolean
name|isStartable
parameter_list|(
name|DockerContainerStatus
name|containerStatus
parameter_list|)
block|{
if|if
condition|(
name|containerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|EXITED
argument_list|)
operator|||
name|containerStatus
operator|.
name|equals
argument_list|(
name|DockerContainerStatus
operator|.
name|STOPPED
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

