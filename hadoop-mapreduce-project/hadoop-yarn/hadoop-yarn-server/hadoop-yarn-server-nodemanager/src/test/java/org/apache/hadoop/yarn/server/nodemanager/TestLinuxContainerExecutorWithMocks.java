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
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
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
name|FileReader
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
name|LineNumberReader
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
name|LinkedList
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

begin_class
DECL|class|TestLinuxContainerExecutorWithMocks
specifier|public
class|class
name|TestLinuxContainerExecutorWithMocks
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
name|TestLinuxContainerExecutorWithMocks
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|mockExec
specifier|private
name|LinuxContainerExecutor
name|mockExec
init|=
literal|null
decl_stmt|;
DECL|field|mockParamFile
specifier|private
specifier|final
name|File
name|mockParamFile
init|=
operator|new
name|File
argument_list|(
literal|"./params.txt"
argument_list|)
decl_stmt|;
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|method|deleteMockParamFile ()
specifier|private
name|void
name|deleteMockParamFile
parameter_list|()
block|{
if|if
condition|(
name|mockParamFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|mockParamFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readMockParams ()
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readMockParams
parameter_list|()
throws|throws
name|IOException
block|{
name|LinkedList
argument_list|<
name|String
argument_list|>
name|ret
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|LineNumberReader
name|reader
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|mockParamFile
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|ret
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"./src/test/resources/mock-container-executor"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|canExecute
argument_list|()
condition|)
block|{
name|f
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|String
name|executorPath
init|=
name|f
operator|.
name|getAbsolutePath
argument_list|()
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
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_EXECUTOR_PATH
argument_list|,
name|executorPath
argument_list|)
expr_stmt|;
name|mockExec
operator|=
operator|new
name|LinuxContainerExecutor
argument_list|()
expr_stmt|;
name|dirsHandler
operator|=
operator|new
name|LocalDirsHandlerService
argument_list|()
expr_stmt|;
name|dirsHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mockExec
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|deleteMockParamFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerLaunch ()
specifier|public
name|void
name|testContainerLaunch
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|appSubmitter
init|=
literal|"nobody"
decl_stmt|;
name|String
name|cmd
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|LinuxContainerExecutor
operator|.
name|Commands
operator|.
name|LAUNCH_CONTAINER
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|appId
init|=
literal|"APP_ID"
decl_stmt|;
name|String
name|containerId
init|=
literal|"CONTAINER_ID"
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
name|cId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerLaunchContext
name|context
init|=
name|mock
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getContainerID
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
name|Path
name|scriptPath
init|=
operator|new
name|Path
argument_list|(
literal|"file:///bin/echo"
argument_list|)
decl_stmt|;
name|Path
name|tokensPath
init|=
operator|new
name|Path
argument_list|(
literal|"file:///dev/null"
argument_list|)
decl_stmt|;
name|Path
name|workDir
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp"
argument_list|)
decl_stmt|;
name|Path
name|pidFile
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"pid.txt"
argument_list|)
decl_stmt|;
name|mockExec
operator|.
name|activateContainer
argument_list|(
name|cId
argument_list|,
name|pidFile
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
name|mockExec
operator|.
name|launchContainer
argument_list|(
name|container
argument_list|,
name|scriptPath
argument_list|,
name|tokensPath
argument_list|,
name|appSubmitter
argument_list|,
name|appId
argument_list|,
name|workDir
argument_list|,
name|dirsHandler
operator|.
name|getLocalDirs
argument_list|()
argument_list|,
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|appSubmitter
argument_list|,
name|cmd
argument_list|,
name|appId
argument_list|,
name|containerId
argument_list|,
name|workDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|"/bin/echo"
argument_list|,
literal|"/dev/null"
argument_list|,
name|pidFile
operator|.
name|toString
argument_list|()
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|dirsHandler
operator|.
name|getLocalDirs
argument_list|()
argument_list|)
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|readMockParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerKill ()
specifier|public
name|void
name|testContainerKill
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|appSubmitter
init|=
literal|"nobody"
decl_stmt|;
name|String
name|cmd
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|LinuxContainerExecutor
operator|.
name|Commands
operator|.
name|SIGNAL_CONTAINER
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerExecutor
operator|.
name|Signal
name|signal
init|=
name|ContainerExecutor
operator|.
name|Signal
operator|.
name|QUIT
decl_stmt|;
name|String
name|sigVal
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|signal
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|mockExec
operator|.
name|signalContainer
argument_list|(
name|appSubmitter
argument_list|,
literal|"1000"
argument_list|,
name|signal
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|appSubmitter
argument_list|,
name|cmd
argument_list|,
literal|"1000"
argument_list|,
name|sigVal
argument_list|)
argument_list|,
name|readMockParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteAsUser ()
specifier|public
name|void
name|testDeleteAsUser
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|appSubmitter
init|=
literal|"nobody"
decl_stmt|;
name|String
name|cmd
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|LinuxContainerExecutor
operator|.
name|Commands
operator|.
name|DELETE_AS_USER
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|dir
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp/testdir"
argument_list|)
decl_stmt|;
name|mockExec
operator|.
name|deleteAsUser
argument_list|(
name|appSubmitter
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|appSubmitter
argument_list|,
name|cmd
argument_list|,
literal|"/tmp/testdir"
argument_list|)
argument_list|,
name|readMockParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

