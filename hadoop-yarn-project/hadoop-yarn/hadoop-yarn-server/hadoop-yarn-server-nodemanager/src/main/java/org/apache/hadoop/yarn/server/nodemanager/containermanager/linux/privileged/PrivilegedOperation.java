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
name|Collections
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

begin_comment
comment|/**  * Represents operations that require higher system privileges - e.g  * creating cgroups, launching containers as specified users, 'tc' commands etc  * that are completed using the container-executor binary  */
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
DECL|class|PrivilegedOperation
specifier|public
class|class
name|PrivilegedOperation
block|{
DECL|field|LINUX_FILE_PATH_SEPARATOR
specifier|public
specifier|final
specifier|static
name|char
name|LINUX_FILE_PATH_SEPARATOR
init|=
literal|'%'
decl_stmt|;
DECL|enum|OperationType
specifier|public
enum|enum
name|OperationType
block|{
DECL|enumConstant|CHECK_SETUP
name|CHECK_SETUP
argument_list|(
literal|"--checksetup"
argument_list|)
block|,
DECL|enumConstant|MOUNT_CGROUPS
name|MOUNT_CGROUPS
argument_list|(
literal|"--mount-cgroups"
argument_list|)
block|,
DECL|enumConstant|INITIALIZE_CONTAINER
name|INITIALIZE_CONTAINER
argument_list|(
literal|""
argument_list|)
block|,
comment|//no CLI switch supported yet
DECL|enumConstant|LAUNCH_CONTAINER
name|LAUNCH_CONTAINER
argument_list|(
literal|""
argument_list|)
block|,
comment|//no CLI switch supported yet
DECL|enumConstant|SIGNAL_CONTAINER
name|SIGNAL_CONTAINER
argument_list|(
literal|""
argument_list|)
block|,
comment|//no CLI switch supported yet
DECL|enumConstant|DELETE_AS_USER
name|DELETE_AS_USER
argument_list|(
literal|""
argument_list|)
block|,
comment|//no CLI switch supported yet
DECL|enumConstant|LAUNCH_DOCKER_CONTAINER
name|LAUNCH_DOCKER_CONTAINER
argument_list|(
literal|""
argument_list|)
block|,
comment|//no CLI switch supported yet
DECL|enumConstant|TC_MODIFY_STATE
name|TC_MODIFY_STATE
argument_list|(
literal|"--tc-modify-state"
argument_list|)
block|,
DECL|enumConstant|TC_READ_STATE
name|TC_READ_STATE
argument_list|(
literal|"--tc-read-state"
argument_list|)
block|,
DECL|enumConstant|TC_READ_STATS
name|TC_READ_STATS
argument_list|(
literal|"--tc-read-stats"
argument_list|)
block|,
DECL|enumConstant|ADD_PID_TO_CGROUP
name|ADD_PID_TO_CGROUP
argument_list|(
literal|""
argument_list|)
block|,
comment|//no CLI switch supported yet.
DECL|enumConstant|RUN_DOCKER_CMD
name|RUN_DOCKER_CMD
argument_list|(
literal|"--run-docker"
argument_list|)
block|,
DECL|enumConstant|GPU
name|GPU
argument_list|(
literal|"--module-gpu"
argument_list|)
block|,
DECL|enumConstant|LIST_AS_USER
name|LIST_AS_USER
argument_list|(
literal|""
argument_list|)
block|;
comment|//no CLI switch supported yet.
DECL|field|option
specifier|private
specifier|final
name|String
name|option
decl_stmt|;
DECL|method|OperationType (String option)
name|OperationType
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|this
operator|.
name|option
operator|=
name|option
expr_stmt|;
block|}
DECL|method|getOption ()
specifier|public
name|String
name|getOption
parameter_list|()
block|{
return|return
name|option
return|;
block|}
block|}
DECL|field|CGROUP_ARG_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CGROUP_ARG_PREFIX
init|=
literal|"cgroups="
decl_stmt|;
DECL|field|CGROUP_ARG_NO_TASKS
specifier|public
specifier|static
specifier|final
name|String
name|CGROUP_ARG_NO_TASKS
init|=
literal|"none"
decl_stmt|;
DECL|field|opType
specifier|private
specifier|final
name|OperationType
name|opType
decl_stmt|;
DECL|field|args
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
decl_stmt|;
DECL|field|failureLogging
specifier|private
name|boolean
name|failureLogging
decl_stmt|;
DECL|method|PrivilegedOperation (OperationType opType)
specifier|public
name|PrivilegedOperation
parameter_list|(
name|OperationType
name|opType
parameter_list|)
block|{
name|this
operator|.
name|opType
operator|=
name|opType
expr_stmt|;
name|this
operator|.
name|args
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|failureLogging
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|PrivilegedOperation (OperationType opType, String arg)
specifier|public
name|PrivilegedOperation
parameter_list|(
name|OperationType
name|opType
parameter_list|,
name|String
name|arg
parameter_list|)
block|{
name|this
argument_list|(
name|opType
argument_list|)
expr_stmt|;
if|if
condition|(
name|arg
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|args
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|PrivilegedOperation (OperationType opType, List<String> args)
specifier|public
name|PrivilegedOperation
parameter_list|(
name|OperationType
name|opType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|this
argument_list|(
name|opType
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|args
operator|.
name|addAll
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|appendArgs (String... args)
specifier|public
name|void
name|appendArgs
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
name|this
operator|.
name|args
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|appendArgs (List<String> args)
specifier|public
name|void
name|appendArgs
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|.
name|addAll
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|enableFailureLogging ()
specifier|public
name|void
name|enableFailureLogging
parameter_list|()
block|{
name|this
operator|.
name|failureLogging
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|disableFailureLogging ()
specifier|public
name|void
name|disableFailureLogging
parameter_list|()
block|{
name|this
operator|.
name|failureLogging
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|isFailureLoggingEnabled ()
specifier|public
name|boolean
name|isFailureLoggingEnabled
parameter_list|()
block|{
return|return
name|failureLogging
return|;
block|}
DECL|method|getOperationType ()
specifier|public
name|OperationType
name|getOperationType
parameter_list|()
block|{
return|return
name|opType
return|;
block|}
DECL|method|getArguments ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getArguments
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|this
operator|.
name|args
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
operator|||
operator|!
operator|(
name|other
operator|instanceof
name|PrivilegedOperation
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PrivilegedOperation
name|otherOp
init|=
operator|(
name|PrivilegedOperation
operator|)
name|other
decl_stmt|;
return|return
name|otherOp
operator|.
name|opType
operator|.
name|equals
argument_list|(
name|opType
argument_list|)
operator|&&
name|otherOp
operator|.
name|args
operator|.
name|equals
argument_list|(
name|args
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|opType
operator|.
name|hashCode
argument_list|()
operator|+
literal|97
operator|*
name|args
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * List of commands that the container-executor will execute.    */
DECL|enum|RunAsUserCommand
specifier|public
enum|enum
name|RunAsUserCommand
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
block|,
DECL|enumConstant|LAUNCH_DOCKER_CONTAINER
name|LAUNCH_DOCKER_CONTAINER
argument_list|(
literal|4
argument_list|)
block|,
DECL|enumConstant|LIST_AS_USER
name|LIST_AS_USER
argument_list|(
literal|5
argument_list|)
block|;
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
DECL|method|RunAsUserCommand (int value)
name|RunAsUserCommand
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
specifier|public
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
specifier|public
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
block|,
DECL|enumConstant|WRITE_CGROUP_FAILED
name|WRITE_CGROUP_FAILED
argument_list|(
literal|27
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
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
block|}
end_class

end_unit

