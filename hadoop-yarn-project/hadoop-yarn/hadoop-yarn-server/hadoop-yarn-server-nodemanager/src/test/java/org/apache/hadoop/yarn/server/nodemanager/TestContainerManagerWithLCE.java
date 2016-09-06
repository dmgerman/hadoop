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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|TestContainerManager
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

begin_class
DECL|class|TestContainerManagerWithLCE
specifier|public
class|class
name|TestContainerManagerWithLCE
extends|extends
name|TestContainerManager
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
name|TestContainerManagerWithLCE
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TestContainerManagerWithLCE ()
specifier|public
name|TestContainerManagerWithLCE
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
name|localDir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestContainerManagerWithLCE
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-localDir"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|tmpDir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestContainerManagerWithLCE
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|localFS
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
name|localDir
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
name|localFS
operator|.
name|setPermission
argument_list|(
operator|new
name|Path
argument_list|(
name|tmpDir
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|shouldRunTest
argument_list|()
condition|)
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|testContainerSetup ()
specifier|public
name|void
name|testContainerSetup
parameter_list|()
throws|throws
name|Exception
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerSetup"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testContainerSetup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testContainerManagerInitialization ()
specifier|public
name|void
name|testContainerManagerInitialization
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerManagerInitialization"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testContainerManagerInitialization
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testContainerLaunchAndStop ()
specifier|public
name|void
name|testContainerLaunchAndStop
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerLaunchAndStop"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testContainerLaunchAndStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testContainerLaunchAndExitSuccess ()
specifier|public
name|void
name|testContainerLaunchAndExitSuccess
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerLaunchAndExitSuccess"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testContainerLaunchAndExitSuccess
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testContainerLaunchAndExitFailure ()
specifier|public
name|void
name|testContainerLaunchAndExitFailure
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerLaunchAndExitFailure"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testContainerLaunchAndExitFailure
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testLocalingResourceWhileContainerRunning ()
specifier|public
name|void
name|testLocalingResourceWhileContainerRunning
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|super
operator|.
name|testLocalingResourceWhileContainerRunning
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testLocalFilesCleanup ()
specifier|public
name|void
name|testLocalFilesCleanup
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testLocalFilesCleanup"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testLocalFilesCleanup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testContainerLaunchFromPreviousRM ()
specifier|public
name|void
name|testContainerLaunchFromPreviousRM
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerLaunchFromPreviousRM"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testContainerLaunchFromPreviousRM
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testMultipleContainersLaunch ()
specifier|public
name|void
name|testMultipleContainersLaunch
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerLaunchFromPreviousRM"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testMultipleContainersLaunch
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testMultipleContainersStopAndGetStatus ()
specifier|public
name|void
name|testMultipleContainersStopAndGetStatus
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerLaunchFromPreviousRM"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testMultipleContainersStopAndGetStatus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testStartContainerFailureWithUnknownAuxService ()
specifier|public
name|void
name|testStartContainerFailureWithUnknownAuxService
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testContainerLaunchFromPreviousRM"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testStartContainerFailureWithUnknownAuxService
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testIncreaseContainerResourceWithInvalidRequests ()
specifier|public
name|void
name|testIncreaseContainerResourceWithInvalidRequests
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testIncreaseContainerResourceWithInvalidRequests"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testIncreaseContainerResourceWithInvalidRequests
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testIncreaseContainerResourceWithInvalidResource ()
specifier|public
name|void
name|testIncreaseContainerResourceWithInvalidResource
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testIncreaseContainerResourceWithInvalidResource"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testIncreaseContainerResourceWithInvalidResource
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testChangeContainerResource ()
specifier|public
name|void
name|testChangeContainerResource
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testChangeContainerResource"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testChangeContainerResource
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testOutputThreadDumpSignal ()
specifier|public
name|void
name|testOutputThreadDumpSignal
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testOutputThreadDumpSignal"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testOutputThreadDumpSignal
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testGracefulShutdownSignal ()
specifier|public
name|void
name|testGracefulShutdownSignal
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testGracefulShutdownSignal"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testGracefulShutdownSignal
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testForcefulShutdownSignal ()
specifier|public
name|void
name|testForcefulShutdownSignal
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
comment|// Don't run the test if the binary is not available.
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LCE binary path is not passed. Not running the test"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Running testForcefulShutdownSignal"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testForcefulShutdownSignal
argument_list|()
expr_stmt|;
block|}
DECL|method|shouldRunTest ()
specifier|private
name|boolean
name|shouldRunTest
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_EXECUTOR_PATH
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|createContainerExecutor ()
specifier|protected
name|ContainerExecutor
name|createContainerExecutor
parameter_list|()
block|{
name|super
operator|.
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_EXECUTOR_PATH
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_EXECUTOR_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|LinuxContainerExecutor
name|linuxContainerExecutor
init|=
operator|new
name|LinuxContainerExecutor
argument_list|()
decl_stmt|;
name|linuxContainerExecutor
operator|.
name|setConf
argument_list|(
name|super
operator|.
name|conf
argument_list|)
expr_stmt|;
return|return
name|linuxContainerExecutor
return|;
block|}
block|}
end_class

end_unit

