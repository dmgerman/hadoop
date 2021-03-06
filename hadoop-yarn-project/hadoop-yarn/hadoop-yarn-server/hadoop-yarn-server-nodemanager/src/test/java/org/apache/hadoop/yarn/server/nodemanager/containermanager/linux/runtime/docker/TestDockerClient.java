begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|fs
operator|.
name|FileUtil
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
name|test
operator|.
name|GenericTestUtils
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
name|LocalDirsHandlerService
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
name|Arrays
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|doReturn
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

begin_comment
comment|/** Unit tests for DockerClient. */
end_comment

begin_class
DECL|class|TestDockerClient
specifier|public
class|class
name|TestDockerClient
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_ROOT_DIR
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
name|TestDockerClient
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|TEST_ROOT_DIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteCommandToTempFile ()
specifier|public
name|void
name|testWriteCommandToTempFile
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|absRoot
init|=
name|TEST_ROOT_DIR
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|cid
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|attemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|DockerCommand
name|dockerCmd
init|=
operator|new
name|DockerInspectCommand
argument_list|(
name|cid
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|,
name|absRoot
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|absRoot
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|absRoot
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirsHandler
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
name|Context
name|mockContext
init|=
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|mockContext
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|dirsHandler
argument_list|)
operator|.
name|when
argument_list|(
name|mockContext
argument_list|)
operator|.
name|getLocalDirsHandler
argument_list|()
expr_stmt|;
name|DockerClient
name|dockerClient
init|=
operator|new
name|DockerClient
argument_list|()
decl_stmt|;
name|dirsHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dirsHandler
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|tmpPath
init|=
name|dockerClient
operator|.
name|writeCommandToTempFile
argument_list|(
name|dockerCmd
argument_list|,
name|cid
argument_list|,
name|mockContext
argument_list|)
decl_stmt|;
name|dirsHandler
operator|.
name|stop
argument_list|()
expr_stmt|;
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|tmpPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tmpFile
operator|+
literal|" was not created"
argument_list|,
name|tmpFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommandValidation ()
specifier|public
name|void
name|testCommandValidation
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|absRoot
init|=
name|TEST_ROOT_DIR
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|cid
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|attemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|,
name|absRoot
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|absRoot
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|absRoot
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirsHandler
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
name|Context
name|mockContext
init|=
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|mockContext
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|dirsHandler
argument_list|)
operator|.
name|when
argument_list|(
name|mockContext
argument_list|)
operator|.
name|getLocalDirsHandler
argument_list|()
expr_stmt|;
name|DockerClient
name|dockerClient
init|=
operator|new
name|DockerClient
argument_list|()
decl_stmt|;
name|dirsHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dirsHandler
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|DockerRunCommand
name|dockerCmd
init|=
operator|new
name|DockerRunCommand
argument_list|(
name|cid
operator|.
name|toString
argument_list|()
argument_list|,
literal|"user"
argument_list|,
literal|"image"
argument_list|)
decl_stmt|;
name|dockerCmd
operator|.
name|addCommandArguments
argument_list|(
literal|"prop=bad"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|dockerClient
operator|.
name|writeCommandToTempFile
argument_list|(
name|dockerCmd
argument_list|,
name|cid
argument_list|,
name|mockContext
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception writing command file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Expected key validation error"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"'=' found in entry for docker command file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|DockerRunCommand
name|dockerCmd
init|=
operator|new
name|DockerRunCommand
argument_list|(
name|cid
operator|.
name|toString
argument_list|()
argument_list|,
literal|"user"
argument_list|,
literal|"image"
argument_list|)
decl_stmt|;
name|dockerCmd
operator|.
name|setOverrideCommandWithArgs
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"sleep"
argument_list|,
literal|"1000\n"
argument_list|)
argument_list|)
expr_stmt|;
name|dockerClient
operator|.
name|writeCommandToTempFile
argument_list|(
name|dockerCmd
argument_list|,
name|cid
argument_list|,
name|mockContext
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception writing command file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ContainerExecutionException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Expected value validation error"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"'\\n' found in entry for docker command file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dirsHandler
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

