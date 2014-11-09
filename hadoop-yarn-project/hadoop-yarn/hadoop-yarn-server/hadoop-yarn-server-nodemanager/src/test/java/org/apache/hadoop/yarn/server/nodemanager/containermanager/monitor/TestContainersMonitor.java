begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor
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
name|monitor
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
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
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|PrintWriter
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
name|HashMap
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
name|regex
operator|.
name|Pattern
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetContainerStatusesRequest
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
name|protocolrecords
operator|.
name|StartContainerRequest
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
name|protocolrecords
operator|.
name|StartContainersRequest
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
name|ContainerExitStatus
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
name|api
operator|.
name|records
operator|.
name|ContainerState
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
name|ContainerStatus
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
name|LocalResource
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
name|LocalResourceType
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
name|LocalResourceVisibility
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
name|Priority
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
name|Resource
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
name|Token
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
name|URL
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
name|event
operator|.
name|AsyncDispatcher
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
name|exceptions
operator|.
name|YarnException
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|ContainerExecutor
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
name|ContainerExecutor
operator|.
name|Signal
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
name|BaseContainerManagerTest
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
name|utils
operator|.
name|BuilderUtils
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
name|LinuxResourceCalculatorPlugin
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
name|ProcfsBasedProcessTree
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
name|ResourceCalculatorPlugin
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
name|TestProcfsBasedProcessTree
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

begin_class
DECL|class|TestContainersMonitor
specifier|public
class|class
name|TestContainersMonitor
extends|extends
name|BaseContainerManagerTest
block|{
DECL|method|TestContainersMonitor ()
specifier|public
name|TestContainersMonitor
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
static|static
block|{
name|LOG
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestContainersMonitor
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_RESOURCE_CALCULATOR
argument_list|,
name|LinuxResourceCalculatorPlugin
operator|.
name|class
argument_list|,
name|ResourceCalculatorPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test to verify the check for whether a process tree is over limit or not.    *    * @throws IOException    *           if there was a problem setting up the fake procfs directories or    *           files.    */
annotation|@
name|Test
DECL|method|testProcessTreeLimits ()
specifier|public
name|void
name|testProcessTreeLimits
parameter_list|()
throws|throws
name|IOException
block|{
comment|// set up a dummy proc file system
name|File
name|procfsRootDir
init|=
operator|new
name|File
argument_list|(
name|localDir
argument_list|,
literal|"proc"
argument_list|)
decl_stmt|;
name|String
index|[]
name|pids
init|=
block|{
literal|"100"
block|,
literal|"200"
block|,
literal|"300"
block|,
literal|"400"
block|,
literal|"500"
block|,
literal|"600"
block|,
literal|"700"
block|}
decl_stmt|;
try|try
block|{
name|TestProcfsBasedProcessTree
operator|.
name|setupProcfsRootDir
argument_list|(
name|procfsRootDir
argument_list|)
expr_stmt|;
comment|// create pid dirs.
name|TestProcfsBasedProcessTree
operator|.
name|setupPidDirs
argument_list|(
name|procfsRootDir
argument_list|,
name|pids
argument_list|)
expr_stmt|;
comment|// create process infos.
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
index|[]
name|procs
init|=
operator|new
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
index|[
literal|7
index|]
decl_stmt|;
comment|// assume pids 100, 500 are in 1 tree
comment|// 200,300,400 are in another
comment|// 600,700 are in a third
name|procs
index|[
literal|0
index|]
operator|=
operator|new
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"100"
block|,
literal|"proc1"
block|,
literal|"1"
block|,
literal|"100"
block|,
literal|"100"
block|,
literal|"100000"
block|}
argument_list|)
expr_stmt|;
name|procs
index|[
literal|1
index|]
operator|=
operator|new
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"200"
block|,
literal|"proc2"
block|,
literal|"1"
block|,
literal|"200"
block|,
literal|"200"
block|,
literal|"200000"
block|}
argument_list|)
expr_stmt|;
name|procs
index|[
literal|2
index|]
operator|=
operator|new
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"300"
block|,
literal|"proc3"
block|,
literal|"200"
block|,
literal|"200"
block|,
literal|"200"
block|,
literal|"300000"
block|}
argument_list|)
expr_stmt|;
name|procs
index|[
literal|3
index|]
operator|=
operator|new
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"400"
block|,
literal|"proc4"
block|,
literal|"200"
block|,
literal|"200"
block|,
literal|"200"
block|,
literal|"400000"
block|}
argument_list|)
expr_stmt|;
name|procs
index|[
literal|4
index|]
operator|=
operator|new
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"500"
block|,
literal|"proc5"
block|,
literal|"100"
block|,
literal|"100"
block|,
literal|"100"
block|,
literal|"1500000"
block|}
argument_list|)
expr_stmt|;
name|procs
index|[
literal|5
index|]
operator|=
operator|new
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"600"
block|,
literal|"proc6"
block|,
literal|"1"
block|,
literal|"600"
block|,
literal|"600"
block|,
literal|"100000"
block|}
argument_list|)
expr_stmt|;
name|procs
index|[
literal|6
index|]
operator|=
operator|new
name|TestProcfsBasedProcessTree
operator|.
name|ProcessStatInfo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"700"
block|,
literal|"proc7"
block|,
literal|"600"
block|,
literal|"600"
block|,
literal|"600"
block|,
literal|"100000"
block|}
argument_list|)
expr_stmt|;
comment|// write stat files.
name|TestProcfsBasedProcessTree
operator|.
name|writeStatFiles
argument_list|(
name|procfsRootDir
argument_list|,
name|pids
argument_list|,
name|procs
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// vmem limit
name|long
name|limit
init|=
literal|700000
decl_stmt|;
name|ContainersMonitorImpl
name|test
init|=
operator|new
name|ContainersMonitorImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// create process trees
comment|// tree rooted at 100 is over limit immediately, as it is
comment|// twice over the mem limit.
name|ProcfsBasedProcessTree
name|pTree
init|=
operator|new
name|ProcfsBasedProcessTree
argument_list|(
literal|"100"
argument_list|,
name|procfsRootDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tree rooted at 100 should be over limit "
operator|+
literal|"after first iteration."
argument_list|,
name|test
operator|.
name|isProcessTreeOverLimit
argument_list|(
name|pTree
argument_list|,
literal|"dummyId"
argument_list|,
name|limit
argument_list|)
argument_list|)
expr_stmt|;
comment|// the tree rooted at 200 is initially below limit.
name|pTree
operator|=
operator|new
name|ProcfsBasedProcessTree
argument_list|(
literal|"200"
argument_list|,
name|procfsRootDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"tree rooted at 200 shouldn't be over limit "
operator|+
literal|"after one iteration."
argument_list|,
name|test
operator|.
name|isProcessTreeOverLimit
argument_list|(
name|pTree
argument_list|,
literal|"dummyId"
argument_list|,
name|limit
argument_list|)
argument_list|)
expr_stmt|;
comment|// second iteration - now the tree has been over limit twice,
comment|// hence it should be declared over limit.
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tree rooted at 200 should be over limit after 2 iterations"
argument_list|,
name|test
operator|.
name|isProcessTreeOverLimit
argument_list|(
name|pTree
argument_list|,
literal|"dummyId"
argument_list|,
name|limit
argument_list|)
argument_list|)
expr_stmt|;
comment|// the tree rooted at 600 is never over limit.
name|pTree
operator|=
operator|new
name|ProcfsBasedProcessTree
argument_list|(
literal|"600"
argument_list|,
name|procfsRootDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"tree rooted at 600 should never be over limit."
argument_list|,
name|test
operator|.
name|isProcessTreeOverLimit
argument_list|(
name|pTree
argument_list|,
literal|"dummyId"
argument_list|,
name|limit
argument_list|)
argument_list|)
expr_stmt|;
comment|// another iteration does not make any difference.
name|pTree
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"tree rooted at 600 should never be over limit."
argument_list|,
name|test
operator|.
name|isProcessTreeOverLimit
argument_list|(
name|pTree
argument_list|,
literal|"dummyId"
argument_list|,
name|limit
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|procfsRootDir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testContainerKillOnMemoryOverflow ()
specifier|public
name|void
name|testContainerKillOnMemoryOverflow
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
if|if
condition|(
operator|!
name|ProcfsBasedProcessTree
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
return|return;
block|}
name|containerManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|File
name|scriptFile
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"scriptFile.sh"
argument_list|)
decl_stmt|;
name|PrintWriter
name|fileWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|scriptFile
argument_list|)
decl_stmt|;
name|File
name|processStartFile
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"start_file.txt"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"\numask 0"
argument_list|)
expr_stmt|;
comment|// So that start file is readable by the
comment|// test.
name|fileWriter
operator|.
name|write
argument_list|(
literal|"\necho Hello World!> "
operator|+
name|processStartFile
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"\necho $$>> "
operator|+
name|processStartFile
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
literal|"\nsleep 15"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// ////// Construct the Container-id
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
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
name|cId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|port
init|=
literal|12345
decl_stmt|;
name|URL
name|resource_alpha
init|=
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
name|localFS
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|scriptFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|LocalResource
name|rsrc_alpha
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|rsrc_alpha
operator|.
name|setResource
argument_list|(
name|resource_alpha
argument_list|)
expr_stmt|;
name|rsrc_alpha
operator|.
name|setSize
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|rsrc_alpha
operator|.
name|setVisibility
argument_list|(
name|LocalResourceVisibility
operator|.
name|APPLICATION
argument_list|)
expr_stmt|;
name|rsrc_alpha
operator|.
name|setType
argument_list|(
name|LocalResourceType
operator|.
name|FILE
argument_list|)
expr_stmt|;
name|rsrc_alpha
operator|.
name|setTimestamp
argument_list|(
name|scriptFile
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|destinationFile
init|=
literal|"dest_file"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
argument_list|()
decl_stmt|;
name|localResources
operator|.
name|put
argument_list|(
name|destinationFile
argument_list|,
name|rsrc_alpha
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setLocalResources
argument_list|(
name|localResources
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|commands
operator|.
name|add
argument_list|(
literal|"/bin/bash"
argument_list|)
expr_stmt|;
name|commands
operator|.
name|add
argument_list|(
name|scriptFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setCommands
argument_list|(
name|commands
argument_list|)
expr_stmt|;
name|Resource
name|r
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|8
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerTokenIdentifier
name|containerIdentifier
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|(
name|cId
argument_list|,
name|context
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|user
argument_list|,
name|r
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|120000
argument_list|,
literal|123
argument_list|,
name|DUMMY_RM_IDENTIFIER
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Token
name|containerToken
init|=
name|BuilderUtils
operator|.
name|newContainerToken
argument_list|(
name|context
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|containerManager
operator|.
name|getContext
argument_list|()
operator|.
name|getContainerTokenSecretManager
argument_list|()
operator|.
name|createPassword
argument_list|(
name|containerIdentifier
argument_list|)
argument_list|,
name|containerIdentifier
argument_list|)
decl_stmt|;
name|StartContainerRequest
name|scRequest
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|containerLaunchContext
argument_list|,
name|containerToken
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|StartContainerRequest
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|scRequest
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|containerManager
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
expr_stmt|;
name|int
name|timeoutSecs
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|processStartFile
operator|.
name|exists
argument_list|()
operator|&&
name|timeoutSecs
operator|++
operator|<
literal|20
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for process start-file to be created"
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"ProcessStartFile doesn't exist!"
argument_list|,
name|processStartFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now verify the contents of the file
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|processStartFile
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Hello World!"
argument_list|,
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get the pid of the process
name|String
name|pid
init|=
name|reader
operator|.
name|readLine
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// No more lines
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
name|BaseContainerManagerTest
operator|.
name|waitForContainerState
argument_list|(
name|containerManager
argument_list|,
name|cId
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
literal|60
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|containerIds
operator|.
name|add
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|GetContainerStatusesRequest
name|gcsRequest
init|=
name|GetContainerStatusesRequest
operator|.
name|newInstance
argument_list|(
name|containerIds
argument_list|)
decl_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|containerManager
operator|.
name|getContainerStatuses
argument_list|(
name|gcsRequest
argument_list|)
operator|.
name|getContainerStatuses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerExitStatus
operator|.
name|KILLED_EXCEEDED_VMEM
argument_list|,
name|containerStatus
operator|.
name|getExitStatus
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|expectedMsgPattern
init|=
literal|"Container \\[pid="
operator|+
name|pid
operator|+
literal|",containerID="
operator|+
name|cId
operator|+
literal|"\\] is running beyond virtual memory limits. Current usage: "
operator|+
literal|"[0-9.]+ ?[KMGTPE]?B of [0-9.]+ ?[KMGTPE]?B physical memory used; "
operator|+
literal|"[0-9.]+ ?[KMGTPE]?B of [0-9.]+ ?[KMGTPE]?B virtual memory used. "
operator|+
literal|"Killing container.\nDump of the process-tree for "
operator|+
name|cId
operator|+
literal|" :\n"
decl_stmt|;
name|Pattern
name|pat
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|expectedMsgPattern
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Expected message pattern is: "
operator|+
name|expectedMsgPattern
operator|+
literal|"\n\nObserved message is: "
operator|+
name|containerStatus
operator|.
name|getDiagnostics
argument_list|()
argument_list|,
literal|true
argument_list|,
name|pat
operator|.
name|matcher
argument_list|(
name|containerStatus
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
comment|// Assert that the process is not alive anymore
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Process is still alive!"
argument_list|,
name|exec
operator|.
name|signalContainer
argument_list|(
name|user
argument_list|,
name|pid
argument_list|,
name|Signal
operator|.
name|NULL
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testContainerMonitorMemFlags ()
specifier|public
name|void
name|testContainerMonitorMemFlags
parameter_list|()
block|{
name|ContainersMonitor
name|cm
init|=
literal|null
decl_stmt|;
name|long
name|expPmem
init|=
literal|8192
operator|*
literal|1024
operator|*
literal|1024l
decl_stmt|;
name|long
name|expVmem
init|=
call|(
name|long
call|)
argument_list|(
name|expPmem
operator|*
literal|2.1f
argument_list|)
decl_stmt|;
name|cm
operator|=
operator|new
name|ContainersMonitorImpl
argument_list|(
name|mock
argument_list|(
name|ContainerExecutor
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|AsyncDispatcher
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|init
argument_list|(
name|getConfForCM
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|8192
argument_list|,
literal|2.1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expPmem
argument_list|,
name|cm
operator|.
name|getPmemAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expVmem
argument_list|,
name|cm
operator|.
name|getVmemAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|cm
operator|.
name|isPmemCheckEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|cm
operator|.
name|isVmemCheckEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|=
operator|new
name|ContainersMonitorImpl
argument_list|(
name|mock
argument_list|(
name|ContainerExecutor
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|AsyncDispatcher
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|init
argument_list|(
name|getConfForCM
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|8192
argument_list|,
literal|2.1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expPmem
argument_list|,
name|cm
operator|.
name|getPmemAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expVmem
argument_list|,
name|cm
operator|.
name|getVmemAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|cm
operator|.
name|isPmemCheckEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|cm
operator|.
name|isVmemCheckEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|=
operator|new
name|ContainersMonitorImpl
argument_list|(
name|mock
argument_list|(
name|ContainerExecutor
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|AsyncDispatcher
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|init
argument_list|(
name|getConfForCM
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|8192
argument_list|,
literal|2.1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expPmem
argument_list|,
name|cm
operator|.
name|getPmemAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expVmem
argument_list|,
name|cm
operator|.
name|getVmemAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|cm
operator|.
name|isPmemCheckEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|cm
operator|.
name|isVmemCheckEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|=
operator|new
name|ContainersMonitorImpl
argument_list|(
name|mock
argument_list|(
name|ContainerExecutor
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|AsyncDispatcher
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|init
argument_list|(
name|getConfForCM
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|8192
argument_list|,
literal|2.1f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expPmem
argument_list|,
name|cm
operator|.
name|getPmemAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expVmem
argument_list|,
name|cm
operator|.
name|getVmemAllocatedForContainers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|cm
operator|.
name|isPmemCheckEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|cm
operator|.
name|isVmemCheckEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getConfForCM (boolean pMemEnabled, boolean vMemEnabled, int nmPmem, float vMemToPMemRatio)
specifier|private
name|YarnConfiguration
name|getConfForCM
parameter_list|(
name|boolean
name|pMemEnabled
parameter_list|,
name|boolean
name|vMemEnabled
parameter_list|,
name|int
name|nmPmem
parameter_list|,
name|float
name|vMemToPMemRatio
parameter_list|)
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_MB
argument_list|,
name|nmPmem
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
argument_list|,
name|pMemEnabled
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
argument_list|,
name|vMemEnabled
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_PMEM_RATIO
argument_list|,
name|vMemToPMemRatio
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

