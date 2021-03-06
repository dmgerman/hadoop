begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.provider
package|package
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
name|provider
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
name|collect
operator|.
name|Lists
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
name|ApplicationAttemptId
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
name|ApplicationId
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
name|service
operator|.
name|MockRunningServiceContext
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
name|ServiceContext
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
name|ServiceTestUtils
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
name|TestServiceManager
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
name|api
operator|.
name|records
operator|.
name|Artifact
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
name|api
operator|.
name|records
operator|.
name|Service
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
name|component
operator|.
name|Component
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
name|component
operator|.
name|instance
operator|.
name|ComponentInstance
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
name|containerlaunch
operator|.
name|AbstractLauncher
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
name|containerlaunch
operator|.
name|ContainerLaunchService
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
name|provider
operator|.
name|docker
operator|.
name|DockerProviderService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Rule
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|when
import|;
end_import

begin_comment
comment|/**  * Tests for {@link AbstractProviderService}  */
end_comment

begin_class
DECL|class|TestAbstractProviderService
specifier|public
class|class
name|TestAbstractProviderService
block|{
DECL|field|serviceContext
specifier|private
name|ServiceContext
name|serviceContext
decl_stmt|;
DECL|field|testService
specifier|private
name|Service
name|testService
decl_stmt|;
DECL|field|launcher
specifier|private
name|AbstractLauncher
name|launcher
decl_stmt|;
annotation|@
name|Rule
DECL|field|rule
specifier|public
name|ServiceTestUtils
operator|.
name|ServiceFSWatcher
name|rule
init|=
operator|new
name|ServiceTestUtils
operator|.
name|ServiceFSWatcher
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|testService
operator|=
name|TestServiceManager
operator|.
name|createBaseDef
argument_list|(
literal|"testService"
argument_list|)
expr_stmt|;
name|serviceContext
operator|=
operator|new
name|MockRunningServiceContext
argument_list|(
name|rule
argument_list|,
name|testService
argument_list|)
expr_stmt|;
name|launcher
operator|=
operator|new
name|AbstractLauncher
argument_list|(
name|serviceContext
argument_list|)
expr_stmt|;
name|rule
operator|.
name|getFs
argument_list|()
operator|.
name|setAppDir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"target/testAbstractProviderService"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
operator|new
name|File
argument_list|(
name|rule
operator|.
name|getFs
argument_list|()
operator|.
name|getAppDir
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildContainerLaunchCommand ()
specifier|public
name|void
name|testBuildContainerLaunchCommand
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractProviderService
name|providerService
init|=
operator|new
name|DockerProviderService
argument_list|()
decl_stmt|;
name|Component
name|component
init|=
name|serviceContext
operator|.
name|scheduler
operator|.
name|getAllComponents
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
name|clc
init|=
name|createEntryPointCLCFor
argument_list|(
name|testService
argument_list|,
name|component
argument_list|,
literal|"sleep,9000"
argument_list|)
decl_stmt|;
name|ComponentInstance
name|instance
init|=
name|component
operator|.
name|getAllComponentInstances
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|providerService
operator|.
name|buildContainerLaunchCommand
argument_list|(
name|launcher
argument_list|,
name|testService
argument_list|,
name|instance
argument_list|,
name|rule
operator|.
name|getFs
argument_list|()
argument_list|,
name|serviceContext
operator|.
name|scheduler
operator|.
name|getConfig
argument_list|()
argument_list|,
name|container
argument_list|,
name|clc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"commands"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|clc
operator|.
name|getLaunchCommand
argument_list|()
argument_list|)
argument_list|,
name|launcher
operator|.
name|getCommands
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildContainerLaunchCommandWithSpace ()
specifier|public
name|void
name|testBuildContainerLaunchCommandWithSpace
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractProviderService
name|providerService
init|=
operator|new
name|DockerProviderService
argument_list|()
decl_stmt|;
name|Component
name|component
init|=
name|serviceContext
operator|.
name|scheduler
operator|.
name|getAllComponents
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
name|clc
init|=
name|createEntryPointCLCFor
argument_list|(
name|testService
argument_list|,
name|component
argument_list|,
literal|"ls -l \" space\""
argument_list|)
decl_stmt|;
name|ComponentInstance
name|instance
init|=
name|component
operator|.
name|getAllComponentInstances
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|providerService
operator|.
name|buildContainerLaunchCommand
argument_list|(
name|launcher
argument_list|,
name|testService
argument_list|,
name|instance
argument_list|,
name|rule
operator|.
name|getFs
argument_list|()
argument_list|,
name|serviceContext
operator|.
name|scheduler
operator|.
name|getConfig
argument_list|()
argument_list|,
name|container
argument_list|,
name|clc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"commands don't match."
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"ls,-l, space"
argument_list|)
argument_list|,
name|launcher
operator|.
name|getCommands
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuildContainerLaunchContext ()
specifier|public
name|void
name|testBuildContainerLaunchContext
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractProviderService
name|providerService
init|=
operator|new
name|DockerProviderService
argument_list|()
decl_stmt|;
name|Component
name|component
init|=
name|serviceContext
operator|.
name|scheduler
operator|.
name|getAllComponents
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
name|clc
init|=
name|createEntryPointCLCFor
argument_list|(
name|testService
argument_list|,
name|component
argument_list|,
literal|"sleep,9000"
argument_list|)
decl_stmt|;
name|ComponentInstance
name|instance
init|=
name|component
operator|.
name|getAllComponentInstances
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|providerService
operator|.
name|buildContainerLaunchContext
argument_list|(
name|launcher
argument_list|,
name|testService
argument_list|,
name|instance
argument_list|,
name|rule
operator|.
name|getFs
argument_list|()
argument_list|,
name|serviceContext
operator|.
name|scheduler
operator|.
name|getConfig
argument_list|()
argument_list|,
name|container
argument_list|,
name|clc
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"artifact"
argument_list|,
name|clc
operator|.
name|getArtifact
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|launcher
operator|.
name|getDockerImage
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
DECL|method|createEntryPointCLCFor (Service service, Component component, String launchCmd)
name|createEntryPointCLCFor
parameter_list|(
name|Service
name|service
parameter_list|,
name|Component
name|component
parameter_list|,
name|String
name|launchCmd
parameter_list|)
block|{
name|Artifact
name|artifact
init|=
operator|new
name|Artifact
argument_list|()
decl_stmt|;
name|artifact
operator|.
name|setType
argument_list|(
name|Artifact
operator|.
name|TypeEnum
operator|.
name|DOCKER
argument_list|)
expr_stmt|;
name|artifact
operator|.
name|setId
argument_list|(
literal|"example"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_DELAYED_REMOVAL"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"YARN_CONTAINER_RUNTIME_DOCKER_RUN_OVERRIDE_DISABLE"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|component
operator|.
name|getComponentSpec
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setEnv
argument_list|(
name|env
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
argument_list|(
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|service
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|setArtifact
argument_list|(
name|artifact
argument_list|)
operator|.
name|setConfiguration
argument_list|(
name|component
operator|.
name|getComponentSpec
argument_list|()
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|setLaunchCommand
argument_list|(
name|launchCmd
argument_list|)
return|;
block|}
block|}
end_class

end_unit

