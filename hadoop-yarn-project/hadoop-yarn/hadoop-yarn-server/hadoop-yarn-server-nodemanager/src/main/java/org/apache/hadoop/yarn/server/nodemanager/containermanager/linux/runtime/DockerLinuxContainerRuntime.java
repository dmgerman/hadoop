begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|linux
operator|.
name|resources
operator|.
name|CGroupsHandler
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
name|docker
operator|.
name|DockerClient
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
name|docker
operator|.
name|DockerRunCommand
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
name|ContainerRuntimeConstants
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
name|ContainerRuntimeContext
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|linux
operator|.
name|runtime
operator|.
name|LinuxContainerRuntimeConstants
operator|.
name|*
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|DockerLinuxContainerRuntime
specifier|public
class|class
name|DockerLinuxContainerRuntime
implements|implements
name|LinuxContainerRuntime
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
name|DockerLinuxContainerRuntime
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|ENV_DOCKER_CONTAINER_IMAGE
specifier|public
specifier|static
specifier|final
name|String
name|ENV_DOCKER_CONTAINER_IMAGE
init|=
literal|"YARN_CONTAINER_RUNTIME_DOCKER_IMAGE"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|ENV_DOCKER_CONTAINER_IMAGE_FILE
specifier|public
specifier|static
specifier|final
name|String
name|ENV_DOCKER_CONTAINER_IMAGE_FILE
init|=
literal|"YARN_CONTAINER_RUNTIME_DOCKER_IMAGE_FILE"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|ENV_DOCKER_CONTAINER_RUN_OVERRIDE_DISABLE
specifier|public
specifier|static
specifier|final
name|String
name|ENV_DOCKER_CONTAINER_RUN_OVERRIDE_DISABLE
init|=
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER
init|=
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_PRIVILEGED_CONTAINER"
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|dockerClient
specifier|private
name|DockerClient
name|dockerClient
decl_stmt|;
DECL|field|privilegedOperationExecutor
specifier|private
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
decl_stmt|;
DECL|field|cGroupsHandler
specifier|private
name|CGroupsHandler
name|cGroupsHandler
decl_stmt|;
DECL|field|privilegedContainersAcl
specifier|private
name|AccessControlList
name|privilegedContainersAcl
decl_stmt|;
DECL|method|isDockerContainerRequested ( Map<String, String> env)
specifier|public
specifier|static
name|boolean
name|isDockerContainerRequested
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
block|{
if|if
condition|(
name|env
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|type
init|=
name|env
operator|.
name|get
argument_list|(
name|ContainerRuntimeConstants
operator|.
name|ENV_CONTAINER_TYPE
argument_list|)
decl_stmt|;
return|return
name|type
operator|!=
literal|null
operator|&&
name|type
operator|.
name|equals
argument_list|(
literal|"docker"
argument_list|)
return|;
block|}
DECL|method|DockerLinuxContainerRuntime (PrivilegedOperationExecutor privilegedOperationExecutor, CGroupsHandler cGroupsHandler)
specifier|public
name|DockerLinuxContainerRuntime
parameter_list|(
name|PrivilegedOperationExecutor
name|privilegedOperationExecutor
parameter_list|,
name|CGroupsHandler
name|cGroupsHandler
parameter_list|)
block|{
name|this
operator|.
name|privilegedOperationExecutor
operator|=
name|privilegedOperationExecutor
expr_stmt|;
name|this
operator|.
name|cGroupsHandler
operator|=
name|cGroupsHandler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|dockerClient
operator|=
operator|new
name|DockerClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|privilegedContainersAcl
operator|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_PRIVILEGED_CONTAINERS_ACL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_DOCKER_PRIVILEGED_CONTAINERS_ACL
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|prepareContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{    }
DECL|method|addCGroupParentIfRequired (String resourcesOptions, String containerIdStr, DockerRunCommand runCommand)
specifier|public
name|void
name|addCGroupParentIfRequired
parameter_list|(
name|String
name|resourcesOptions
parameter_list|,
name|String
name|containerIdStr
parameter_list|,
name|DockerRunCommand
name|runCommand
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
if|if
condition|(
name|resourcesOptions
operator|.
name|equals
argument_list|(
operator|(
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_PREFIX
operator|+
name|PrivilegedOperation
operator|.
name|CGROUP_ARG_NO_TASKS
operator|)
argument_list|)
condition|)
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
literal|"no resource restrictions specified. not using docker's "
operator|+
literal|"cgroup options"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
literal|"using docker's cgroups options"
argument_list|)
expr_stmt|;
block|}
name|String
name|cGroupPath
init|=
literal|"/"
operator|+
name|cGroupsHandler
operator|.
name|getRelativePathForCGroup
argument_list|(
name|containerIdStr
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
literal|"using cgroup parent: "
operator|+
name|cGroupPath
argument_list|)
expr_stmt|;
block|}
name|runCommand
operator|.
name|setCGroupParent
argument_list|(
name|cGroupPath
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|allowPrivilegedContainerExecution (Container container)
specifier|private
name|boolean
name|allowPrivilegedContainerExecution
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
comment|//For a privileged container to be run all of the following three conditions
comment|// must be satisfied:
comment|//1) Submitting user must request for a privileged container
comment|//2) Privileged containers must be enabled on the cluster
comment|//3) Submitting user must be whitelisted to run a privileged container
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
name|String
name|runPrivilegedContainerEnvVar
init|=
name|environment
operator|.
name|get
argument_list|(
name|ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER
argument_list|)
decl_stmt|;
if|if
condition|(
name|runPrivilegedContainerEnvVar
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|runPrivilegedContainerEnvVar
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"NOT running a privileged container. Value of "
operator|+
name|ENV_DOCKER_CONTAINER_RUN_PRIVILEGED_CONTAINER
operator|+
literal|"is invalid: "
operator|+
name|runPrivilegedContainerEnvVar
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Privileged container requested for : "
operator|+
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//Ok, so we have been asked to run a privileged container. Security
comment|// checks need to be run. Each violation is an error.
comment|//check if privileged containers are enabled.
name|boolean
name|privilegedContainersEnabledOnCluster
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|privilegedContainersEnabledOnCluster
condition|)
block|{
name|String
name|message
init|=
literal|"Privileged container being requested but privileged "
operator|+
literal|"containers are not enabled on this cluster"
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|//check if submitting user is in the whitelist.
name|String
name|submittingUser
init|=
name|container
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|submitterUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|submittingUser
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|privilegedContainersAcl
operator|.
name|isUserAllowed
argument_list|(
name|submitterUgi
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Cannot launch privileged container. Submitting user ("
operator|+
name|submittingUser
operator|+
literal|") fails ACL check."
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|message
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"All checks pass. Launching privileged container for : "
operator|+
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|launchContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|launchContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|Container
name|container
init|=
name|ctx
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
name|String
name|imageName
init|=
name|environment
operator|.
name|get
argument_list|(
name|ENV_DOCKER_CONTAINER_IMAGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|imageName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
name|ENV_DOCKER_CONTAINER_IMAGE
operator|+
literal|" not set!"
argument_list|)
throw|;
block|}
name|String
name|containerIdStr
init|=
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|runAsUser
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|RUN_AS_USER
argument_list|)
decl_stmt|;
name|Path
name|containerWorkDir
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|CONTAINER_WORK_DIR
argument_list|)
decl_stmt|;
comment|//List<String> -> stored as List -> fetched/converted to List<String>
comment|//we can't do better here thanks to type-erasure
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|LOCAL_DIRS
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|LOG_DIRS
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|containerLocalDirs
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|CONTAINER_LOCAL_DIRS
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|containerLogDirs
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|CONTAINER_LOG_DIRS
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|capabilities
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|conf
operator|.
name|getStrings
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_CONTAINER_CAPABILITIES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_DOCKER_CONTAINER_CAPABILITIES
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|DockerRunCommand
name|runCommand
init|=
operator|new
name|DockerRunCommand
argument_list|(
name|containerIdStr
argument_list|,
name|runAsUser
argument_list|,
name|imageName
argument_list|)
operator|.
name|detachOnRun
argument_list|()
operator|.
name|setContainerWorkDir
argument_list|(
name|containerWorkDir
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setNetworkType
argument_list|(
literal|"host"
argument_list|)
operator|.
name|setCapabilities
argument_list|(
name|capabilities
argument_list|)
operator|.
name|addMountLocation
argument_list|(
literal|"/etc/passwd"
argument_list|,
literal|"/etc/password:ro"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|allDirs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|containerLocalDirs
argument_list|)
decl_stmt|;
name|allDirs
operator|.
name|add
argument_list|(
name|containerWorkDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|allDirs
operator|.
name|addAll
argument_list|(
name|containerLogDirs
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|dir
range|:
name|allDirs
control|)
block|{
name|runCommand
operator|.
name|addMountLocation
argument_list|(
name|dir
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allowPrivilegedContainerExecution
argument_list|(
name|container
argument_list|)
condition|)
block|{
name|runCommand
operator|.
name|setPrivileged
argument_list|()
expr_stmt|;
block|}
name|String
name|resourcesOpts
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|RESOURCES_OPTIONS
argument_list|)
decl_stmt|;
name|addCGroupParentIfRequired
argument_list|(
name|resourcesOpts
argument_list|,
name|containerIdStr
argument_list|,
name|runCommand
argument_list|)
expr_stmt|;
name|Path
name|nmPrivateContainerScriptPath
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|NM_PRIVATE_CONTAINER_SCRIPT_PATH
argument_list|)
decl_stmt|;
name|String
name|disableOverride
init|=
name|environment
operator|.
name|get
argument_list|(
name|ENV_DOCKER_CONTAINER_RUN_OVERRIDE_DISABLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|disableOverride
operator|!=
literal|null
operator|&&
name|disableOverride
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"command override disabled"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|overrideCommands
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
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
name|overrideCommands
operator|.
name|add
argument_list|(
literal|"bash"
argument_list|)
expr_stmt|;
name|overrideCommands
operator|.
name|add
argument_list|(
name|launchDst
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|runCommand
operator|.
name|setOverrideCommandWithArgs
argument_list|(
name|overrideCommands
argument_list|)
expr_stmt|;
block|}
name|String
name|commandFile
init|=
name|dockerClient
operator|.
name|writeCommandToTempFile
argument_list|(
name|runCommand
argument_list|,
name|containerIdStr
argument_list|)
decl_stmt|;
name|PrivilegedOperation
name|launchOp
init|=
operator|new
name|PrivilegedOperation
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|LAUNCH_DOCKER_CONTAINER
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
decl_stmt|;
name|launchOp
operator|.
name|appendArgs
argument_list|(
name|runAsUser
argument_list|,
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|USER
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|PrivilegedOperation
operator|.
name|RunAsUserCommand
operator|.
name|LAUNCH_DOCKER_CONTAINER
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|APPID
argument_list|)
argument_list|,
name|containerIdStr
argument_list|,
name|containerWorkDir
operator|.
name|toString
argument_list|()
argument_list|,
name|nmPrivateContainerScriptPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|NM_PRIVATE_TOKENS_PATH
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|PID_FILE_PATH
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
name|PrivilegedOperation
operator|.
name|LINUX_FILE_PATH_SEPARATOR
argument_list|,
name|localDirs
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
name|PrivilegedOperation
operator|.
name|LINUX_FILE_PATH_SEPARATOR
argument_list|,
name|logDirs
argument_list|)
argument_list|,
name|commandFile
argument_list|,
name|resourcesOpts
argument_list|)
expr_stmt|;
name|String
name|tcCommandFile
init|=
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|TC_COMMAND_FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|tcCommandFile
operator|!=
literal|null
condition|)
block|{
name|launchOp
operator|.
name|appendArgs
argument_list|(
name|tcCommandFile
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|privilegedOperationExecutor
operator|.
name|executePrivilegedOperation
argument_list|(
literal|null
argument_list|,
name|launchOp
argument_list|,
literal|null
argument_list|,
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Launch container failed. Exception: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Launch container failed"
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
annotation|@
name|Override
DECL|method|signalContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|signalContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{
name|Container
name|container
init|=
name|ctx
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|PrivilegedOperation
name|signalOp
init|=
operator|new
name|PrivilegedOperation
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|SIGNAL_CONTAINER
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
decl_stmt|;
name|signalOp
operator|.
name|appendArgs
argument_list|(
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|RUN_AS_USER
argument_list|)
argument_list|,
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|USER
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|PrivilegedOperation
operator|.
name|RunAsUserCommand
operator|.
name|SIGNAL_CONTAINER
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|PID
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|ctx
operator|.
name|getExecutionAttribute
argument_list|(
name|SIGNAL
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|PrivilegedOperationExecutor
name|executor
init|=
name|PrivilegedOperationExecutor
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|executor
operator|.
name|executePrivilegedOperation
argument_list|(
literal|null
argument_list|,
name|signalOp
argument_list|,
literal|null
argument_list|,
name|container
operator|.
name|getLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PrivilegedOperationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Signal container failed. Exception: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ContainerExecutionException
argument_list|(
literal|"Signal container failed"
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
annotation|@
name|Override
DECL|method|reapContainer (ContainerRuntimeContext ctx)
specifier|public
name|void
name|reapContainer
parameter_list|(
name|ContainerRuntimeContext
name|ctx
parameter_list|)
throws|throws
name|ContainerExecutionException
block|{    }
block|}
end_class

end_unit

