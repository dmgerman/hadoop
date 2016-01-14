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
name|api
operator|.
name|records
operator|.
name|ContainerLaunchContext
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
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|HashMap
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestDockerContainerRuntime
specifier|public
class|class
name|TestDockerContainerRuntime
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
name|TestDockerContainerRuntime
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|mockExecutor
name|PrivilegedOperationExecutor
name|mockExecutor
decl_stmt|;
DECL|field|mockCGroupsHandler
name|CGroupsHandler
name|mockCGroupsHandler
decl_stmt|;
DECL|field|containerId
name|String
name|containerId
decl_stmt|;
DECL|field|container
name|Container
name|container
decl_stmt|;
DECL|field|cId
name|ContainerId
name|cId
decl_stmt|;
DECL|field|context
name|ContainerLaunchContext
name|context
decl_stmt|;
DECL|field|env
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
decl_stmt|;
DECL|field|image
name|String
name|image
decl_stmt|;
DECL|field|runAsUser
name|String
name|runAsUser
decl_stmt|;
DECL|field|user
name|String
name|user
decl_stmt|;
DECL|field|appId
name|String
name|appId
decl_stmt|;
DECL|field|containerIdStr
name|String
name|containerIdStr
init|=
name|containerId
decl_stmt|;
DECL|field|containerWorkDir
name|Path
name|containerWorkDir
decl_stmt|;
DECL|field|nmPrivateContainerScriptPath
name|Path
name|nmPrivateContainerScriptPath
decl_stmt|;
DECL|field|nmPrivateTokensPath
name|Path
name|nmPrivateTokensPath
decl_stmt|;
DECL|field|pidFilePath
name|Path
name|pidFilePath
decl_stmt|;
DECL|field|localDirs
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
decl_stmt|;
DECL|field|logDirs
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
decl_stmt|;
DECL|field|resourcesOptions
name|String
name|resourcesOptions
decl_stmt|;
DECL|field|builder
name|ContainerRuntimeContext
operator|.
name|Builder
name|builder
decl_stmt|;
DECL|field|submittingUser
name|String
name|submittingUser
init|=
literal|"anakin"
decl_stmt|;
DECL|field|whitelistedUser
name|String
name|whitelistedUser
init|=
literal|"yoda"
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|String
name|tmpPath
init|=
operator|new
name|StringBuffer
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|,
name|tmpPath
argument_list|)
expr_stmt|;
name|mockExecutor
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|PrivilegedOperationExecutor
operator|.
name|class
argument_list|)
expr_stmt|;
name|mockCGroupsHandler
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|CGroupsHandler
operator|.
name|class
argument_list|)
expr_stmt|;
name|containerId
operator|=
literal|"container_id"
expr_stmt|;
name|container
operator|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
expr_stmt|;
name|cId
operator|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
expr_stmt|;
name|context
operator|=
name|mock
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|env
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|image
operator|=
literal|"busybox:latest"
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|DockerLinuxContainerRuntime
operator|.
name|ENV_DOCKER_CONTAINER_IMAGE
argument_list|,
name|image
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|cId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getLaunchContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getEnvironment
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|env
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|submittingUser
argument_list|)
expr_stmt|;
name|runAsUser
operator|=
literal|"run_as_user"
expr_stmt|;
name|user
operator|=
literal|"user"
expr_stmt|;
name|appId
operator|=
literal|"app_id"
expr_stmt|;
name|containerIdStr
operator|=
name|containerId
expr_stmt|;
name|containerWorkDir
operator|=
operator|new
name|Path
argument_list|(
literal|"/test_container_work_dir"
argument_list|)
expr_stmt|;
name|nmPrivateContainerScriptPath
operator|=
operator|new
name|Path
argument_list|(
literal|"/test_script_path"
argument_list|)
expr_stmt|;
name|nmPrivateTokensPath
operator|=
operator|new
name|Path
argument_list|(
literal|"/test_private_tokens_path"
argument_list|)
expr_stmt|;
name|pidFilePath
operator|=
operator|new
name|Path
argument_list|(
literal|"/test_pid_file_path"
argument_list|)
expr_stmt|;
name|localDirs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|logDirs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|resourcesOptions
operator|=
literal|"cgroups=none"
expr_stmt|;
name|localDirs
operator|.
name|add
argument_list|(
literal|"/test_local_dir"
argument_list|)
expr_stmt|;
name|logDirs
operator|.
name|add
argument_list|(
literal|"/test_log_dir"
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|ContainerRuntimeContext
operator|.
name|Builder
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setExecutionAttribute
argument_list|(
name|RUN_AS_USER
argument_list|,
name|runAsUser
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|USER
argument_list|,
name|user
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|APPID
argument_list|,
name|appId
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|CONTAINER_ID_STR
argument_list|,
name|containerIdStr
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|CONTAINER_WORK_DIR
argument_list|,
name|containerWorkDir
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|NM_PRIVATE_CONTAINER_SCRIPT_PATH
argument_list|,
name|nmPrivateContainerScriptPath
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|NM_PRIVATE_TOKENS_PATH
argument_list|,
name|nmPrivateTokensPath
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|PID_FILE_PATH
argument_list|,
name|pidFilePath
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|LOCAL_DIRS
argument_list|,
name|localDirs
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|LOG_DIRS
argument_list|,
name|logDirs
argument_list|)
operator|.
name|setExecutionAttribute
argument_list|(
name|RESOURCES_OPTIONS
argument_list|,
name|resourcesOptions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSelectDockerContainerType ()
specifier|public
name|void
name|testSelectDockerContainerType
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|envDockerType
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|envOtherType
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|envDockerType
operator|.
name|put
argument_list|(
name|ContainerRuntimeConstants
operator|.
name|ENV_CONTAINER_TYPE
argument_list|,
literal|"docker"
argument_list|)
expr_stmt|;
name|envOtherType
operator|.
name|put
argument_list|(
name|ContainerRuntimeConstants
operator|.
name|ENV_CONTAINER_TYPE
argument_list|,
literal|"other"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|DockerLinuxContainerRuntime
operator|.
name|isDockerContainerRequested
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|DockerLinuxContainerRuntime
operator|.
name|isDockerContainerRequested
argument_list|(
name|envDockerType
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|DockerLinuxContainerRuntime
operator|.
name|isDockerContainerRequested
argument_list|(
name|envOtherType
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|capturePrivilegedOperationAndVerifyArgs ()
specifier|private
name|PrivilegedOperation
name|capturePrivilegedOperationAndVerifyArgs
parameter_list|()
throws|throws
name|PrivilegedOperationException
block|{
name|ArgumentCaptor
argument_list|<
name|PrivilegedOperation
argument_list|>
name|opCaptor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|PrivilegedOperation
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//single invocation expected
comment|//due to type erasure + mocking, this verification requires a suppress
comment|// warning annotation on the entire method
name|verify
argument_list|(
name|mockExecutor
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|executePrivilegedOperation
argument_list|(
name|anyList
argument_list|()
argument_list|,
name|opCaptor
operator|.
name|capture
argument_list|()
argument_list|,
name|any
argument_list|(
name|File
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|PrivilegedOperation
name|op
init|=
name|opCaptor
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PrivilegedOperation
operator|.
name|OperationType
operator|.
name|LAUNCH_DOCKER_CONTAINER
argument_list|,
name|op
operator|.
name|getOperationType
argument_list|()
argument_list|)
expr_stmt|;
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
comment|//This invocation of container-executor should use 13 arguments in a
comment|// specific order (sigh.)
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|args
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//verify arguments
name|Assert
operator|.
name|assertEquals
argument_list|(
name|runAsUser
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|user
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
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
name|args
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appId
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerWorkDir
operator|.
name|toString
argument_list|()
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nmPrivateContainerScriptPath
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nmPrivateTokensPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|pidFilePath
operator|.
name|toString
argument_list|()
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|localDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|logDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resourcesOptions
argument_list|,
name|args
operator|.
name|get
argument_list|(
literal|12
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|op
return|;
block|}
annotation|@
name|Test
DECL|method|testDockerContainerLaunch ()
specifier|public
name|void
name|testDockerContainerLaunch
parameter_list|()
throws|throws
name|ContainerExecutionException
throws|,
name|PrivilegedOperationException
throws|,
name|IOException
block|{
name|DockerLinuxContainerRuntime
name|runtime
init|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|mockExecutor
argument_list|,
name|mockCGroupsHandler
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
index|[]
name|testCapabilities
init|=
block|{
literal|"NET_BIND_SERVICE"
block|,
literal|"SYS_CHROOT"
block|}
decl_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_CONTAINER_CAPABILITIES
argument_list|,
name|testCapabilities
argument_list|)
expr_stmt|;
name|runtime
operator|.
name|launchContainer
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|PrivilegedOperation
name|op
init|=
name|capturePrivilegedOperationAndVerifyArgs
argument_list|()
decl_stmt|;
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
name|String
name|dockerCommandFile
init|=
name|args
operator|.
name|get
argument_list|(
literal|11
argument_list|)
decl_stmt|;
comment|/* Ordering of capabilities depends on HashSet ordering. */
name|Set
argument_list|<
name|String
argument_list|>
name|capabilitySet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|testCapabilities
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|expectedCapabilitiesString
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"--cap-drop=ALL "
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|capability
range|:
name|capabilitySet
control|)
block|{
name|expectedCapabilitiesString
operator|.
name|append
argument_list|(
literal|"--cap-add="
argument_list|)
operator|.
name|append
argument_list|(
name|capability
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
comment|//This is the expected docker invocation for this case
name|StringBuffer
name|expectedCommandTemplate
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"run --name=%1$s "
argument_list|)
operator|.
name|append
argument_list|(
literal|"--user=%2$s -d "
argument_list|)
operator|.
name|append
argument_list|(
literal|"--workdir=%3$s "
argument_list|)
operator|.
name|append
argument_list|(
literal|"--net=host "
argument_list|)
operator|.
name|append
argument_list|(
name|expectedCapabilitiesString
argument_list|)
operator|.
name|append
argument_list|(
literal|"-v /etc/passwd:/etc/password:ro "
argument_list|)
operator|.
name|append
argument_list|(
literal|"-v %4$s:%4$s "
argument_list|)
operator|.
name|append
argument_list|(
literal|"-v %5$s:%5$s "
argument_list|)
operator|.
name|append
argument_list|(
literal|"-v %6$s:%6$s "
argument_list|)
operator|.
name|append
argument_list|(
literal|"%7$s "
argument_list|)
operator|.
name|append
argument_list|(
literal|"bash %8$s/launch_container.sh"
argument_list|)
decl_stmt|;
name|String
name|expectedCommand
init|=
name|String
operator|.
name|format
argument_list|(
name|expectedCommandTemplate
operator|.
name|toString
argument_list|()
argument_list|,
name|containerId
argument_list|,
name|runAsUser
argument_list|,
name|containerWorkDir
argument_list|,
name|localDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|containerWorkDir
argument_list|,
name|logDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|image
argument_list|,
name|containerWorkDir
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dockerCommands
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dockerCommandFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dockerCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedCommand
argument_list|,
name|dockerCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLaunchPrivilegedContainersInvalidEnvVar ()
specifier|public
name|void
name|testLaunchPrivilegedContainersInvalidEnvVar
parameter_list|()
throws|throws
name|ContainerExecutionException
throws|,
name|PrivilegedOperationException
throws|,
name|IOException
block|{
name|DockerLinuxContainerRuntime
name|runtime
init|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|mockExecutor
argument_list|,
name|mockCGroupsHandler
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_PRIVILEGED_CONTAINER"
argument_list|,
literal|"invalid-value"
argument_list|)
expr_stmt|;
name|runtime
operator|.
name|launchContainer
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|PrivilegedOperation
name|op
init|=
name|capturePrivilegedOperationAndVerifyArgs
argument_list|()
decl_stmt|;
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
name|String
name|dockerCommandFile
init|=
name|args
operator|.
name|get
argument_list|(
literal|11
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dockerCommands
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dockerCommandFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dockerCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|command
init|=
name|dockerCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//ensure --privileged isn't in the invocation
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Unexpected --privileged in docker run args : "
operator|+
name|command
argument_list|,
operator|!
name|command
operator|.
name|contains
argument_list|(
literal|"--privileged"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLaunchPrivilegedContainersWithDisabledSetting ()
specifier|public
name|void
name|testLaunchPrivilegedContainersWithDisabledSetting
parameter_list|()
throws|throws
name|ContainerExecutionException
throws|,
name|PrivilegedOperationException
throws|,
name|IOException
block|{
name|DockerLinuxContainerRuntime
name|runtime
init|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|mockExecutor
argument_list|,
name|mockCGroupsHandler
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_PRIVILEGED_CONTAINER"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
try|try
block|{
name|runtime
operator|.
name|launchContainer
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected a privileged launch container failure."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught expected exception : "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLaunchPrivilegedContainersWithEnabledSettingAndDefaultACL ()
specifier|public
name|void
name|testLaunchPrivilegedContainersWithEnabledSettingAndDefaultACL
parameter_list|()
throws|throws
name|ContainerExecutionException
throws|,
name|PrivilegedOperationException
throws|,
name|IOException
block|{
comment|//Enable privileged containers.
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DockerLinuxContainerRuntime
name|runtime
init|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|mockExecutor
argument_list|,
name|mockCGroupsHandler
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_PRIVILEGED_CONTAINER"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//By default
comment|// yarn.nodemanager.runtime.linux.docker.privileged-containers.acl
comment|// is empty. So we expect this launch to fail.
try|try
block|{
name|runtime
operator|.
name|launchContainer
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected a privileged launch container failure."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught expected exception : "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
DECL|method|testLaunchPrivilegedContainersEnabledAndUserNotInWhitelist ()
name|testLaunchPrivilegedContainersEnabledAndUserNotInWhitelist
parameter_list|()
throws|throws
name|ContainerExecutionException
throws|,
name|PrivilegedOperationException
throws|,
name|IOException
block|{
comment|//Enable privileged containers.
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//set whitelist of users.
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_PRIVILEGED_CONTAINERS_ACL
argument_list|,
name|whitelistedUser
argument_list|)
expr_stmt|;
name|DockerLinuxContainerRuntime
name|runtime
init|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|mockExecutor
argument_list|,
name|mockCGroupsHandler
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_PRIVILEGED_CONTAINER"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
try|try
block|{
name|runtime
operator|.
name|launchContainer
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected a privileged launch container failure."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught expected exception : "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
DECL|method|testLaunchPrivilegedContainersEnabledAndUserInWhitelist ()
name|testLaunchPrivilegedContainersEnabledAndUserInWhitelist
parameter_list|()
throws|throws
name|ContainerExecutionException
throws|,
name|PrivilegedOperationException
throws|,
name|IOException
block|{
comment|//Enable privileged containers.
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_ALLOW_PRIVILEGED_CONTAINERS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//Add submittingUser to whitelist.
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DOCKER_PRIVILEGED_CONTAINERS_ACL
argument_list|,
name|submittingUser
argument_list|)
expr_stmt|;
name|DockerLinuxContainerRuntime
name|runtime
init|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|mockExecutor
argument_list|,
name|mockCGroupsHandler
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_PRIVILEGED_CONTAINER"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|runtime
operator|.
name|launchContainer
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|PrivilegedOperation
name|op
init|=
name|capturePrivilegedOperationAndVerifyArgs
argument_list|()
decl_stmt|;
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
name|String
name|dockerCommandFile
init|=
name|args
operator|.
name|get
argument_list|(
literal|11
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dockerCommands
init|=
name|Files
operator|.
name|readAllLines
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dockerCommandFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dockerCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|command
init|=
name|dockerCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//submitting user is whitelisted. ensure --privileged is in the invocation
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Did not find expected '--privileged' in docker run args "
operator|+
literal|": "
operator|+
name|command
argument_list|,
name|command
operator|.
name|contains
argument_list|(
literal|"--privileged"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCGroupParent ()
specifier|public
name|void
name|testCGroupParent
parameter_list|()
throws|throws
name|ContainerExecutionException
block|{
name|String
name|hierarchy
init|=
literal|"hadoop-yarn-test"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_HIERARCHY
argument_list|,
name|hierarchy
argument_list|)
expr_stmt|;
name|DockerLinuxContainerRuntime
name|runtime
init|=
operator|new
name|DockerLinuxContainerRuntime
argument_list|(
name|mockExecutor
argument_list|,
name|mockCGroupsHandler
argument_list|)
decl_stmt|;
name|runtime
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|resourceOptionsNone
init|=
literal|"cgroups=none"
decl_stmt|;
name|DockerRunCommand
name|command
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DockerRunCommand
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockCGroupsHandler
operator|.
name|getRelativePathForCGroup
argument_list|(
name|containerId
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|hierarchy
operator|+
literal|"/"
operator|+
name|containerIdStr
argument_list|)
expr_stmt|;
name|runtime
operator|.
name|addCGroupParentIfRequired
argument_list|(
name|resourceOptionsNone
argument_list|,
name|containerIdStr
argument_list|,
name|command
argument_list|)
expr_stmt|;
comment|//no --cgroup-parent should be added here
name|Mockito
operator|.
name|verifyZeroInteractions
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|String
name|resourceOptionsCpu
init|=
literal|"/sys/fs/cgroup/cpu/"
operator|+
name|hierarchy
operator|+
name|containerIdStr
decl_stmt|;
name|runtime
operator|.
name|addCGroupParentIfRequired
argument_list|(
name|resourceOptionsCpu
argument_list|,
name|containerIdStr
argument_list|,
name|command
argument_list|)
expr_stmt|;
comment|//--cgroup-parent should be added for the containerId in question
name|String
name|expectedPath
init|=
literal|"/"
operator|+
name|hierarchy
operator|+
literal|"/"
operator|+
name|containerIdStr
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|command
argument_list|)
operator|.
name|setCGroupParent
argument_list|(
name|expectedPath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

