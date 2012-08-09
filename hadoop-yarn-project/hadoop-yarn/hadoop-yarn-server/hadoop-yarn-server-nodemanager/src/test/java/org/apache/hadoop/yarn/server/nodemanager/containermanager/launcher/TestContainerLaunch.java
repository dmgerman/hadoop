begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher
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
name|launcher
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
name|*
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
name|FileOutputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|Collections
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
name|util
operator|.
name|Shell
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
operator|.
name|Environment
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
name|GetContainerStatusRequest
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
name|StopContainerRequest
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
name|server
operator|.
name|nodemanager
operator|.
name|ContainerExecutor
operator|.
name|ExitCode
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
name|ResourceCalculatorPlugin
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|TestContainerLaunch
specifier|public
class|class
name|TestContainerLaunch
extends|extends
name|BaseContainerManagerTest
block|{
DECL|method|TestContainerLaunch ()
specifier|public
name|TestContainerLaunch
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
block|{
name|super
argument_list|()
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
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_SLEEP_DELAY_BEFORE_SIGKILL_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSpecialCharSymlinks ()
specifier|public
name|void
name|testSpecialCharSymlinks
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|shellFile
init|=
literal|null
decl_stmt|;
name|File
name|tempFile
init|=
literal|null
decl_stmt|;
name|String
name|badSymlink
init|=
literal|"foo@zz%_#*&!-+= bar()"
decl_stmt|;
name|File
name|symLinkFile
init|=
literal|null
decl_stmt|;
try|try
block|{
name|shellFile
operator|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"hello.sh"
argument_list|)
expr_stmt|;
name|tempFile
operator|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"temp.sh"
argument_list|)
expr_stmt|;
name|String
name|timeoutCommand
init|=
literal|"echo \"hello\""
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|shellFile
argument_list|)
argument_list|)
decl_stmt|;
name|shellFile
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
name|timeoutCommand
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|resources
init|=
operator|new
name|HashMap
argument_list|<
name|Path
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|shellFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|resources
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|badSymlink
argument_list|)
argument_list|)
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tempFile
argument_list|)
decl_stmt|;
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
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
literal|"/bin/sh ./\\\""
operator|+
name|badSymlink
operator|+
literal|"\\\""
argument_list|)
expr_stmt|;
name|ContainerLaunch
operator|.
name|writeLaunchEnv
argument_list|(
name|fos
argument_list|,
name|env
argument_list|,
name|resources
argument_list|,
name|commands
argument_list|)
expr_stmt|;
name|fos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|tempFile
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Shell
operator|.
name|ShellCommandExecutor
name|shexc
init|=
operator|new
name|Shell
operator|.
name|ShellCommandExecutor
argument_list|(
operator|new
name|String
index|[]
block|{
name|tempFile
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|,
name|tmpDir
argument_list|)
decl_stmt|;
name|shexc
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|shexc
operator|.
name|getExitCode
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|shexc
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
literal|"hello"
argument_list|)
operator|)
assert|;
name|symLinkFile
operator|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
name|badSymlink
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// cleanup
if|if
condition|(
name|shellFile
operator|!=
literal|null
operator|&&
name|shellFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|shellFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tempFile
operator|!=
literal|null
operator|&&
name|tempFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|tempFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|symLinkFile
operator|!=
literal|null
operator|&&
name|symLinkFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|symLinkFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// this is a dirty hack - but should be ok for a unittest.
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|setNewEnvironmentHack (Map<String, String> newenv)
specifier|public
specifier|static
name|void
name|setNewEnvironmentHack
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|newenv
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
index|[]
name|classes
init|=
name|Collections
operator|.
name|class
operator|.
name|getDeclaredClasses
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
name|System
operator|.
name|getenv
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
name|cl
range|:
name|classes
control|)
block|{
if|if
condition|(
literal|"java.util.Collections$UnmodifiableMap"
operator|.
name|equals
argument_list|(
name|cl
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|Field
name|field
init|=
name|cl
operator|.
name|getDeclaredField
argument_list|(
literal|"m"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|obj
init|=
name|field
operator|.
name|get
argument_list|(
name|env
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|obj
decl_stmt|;
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
name|map
operator|.
name|putAll
argument_list|(
name|newenv
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * See if environment variable is forwarded using sanitizeEnv.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testContainerEnvVariables ()
specifier|public
name|void
name|testContainerEnvVariables
parameter_list|()
throws|throws
name|Exception
block|{
name|containerManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|envWithDummy
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
name|envWithDummy
operator|.
name|putAll
argument_list|(
name|System
operator|.
name|getenv
argument_list|()
argument_list|)
expr_stmt|;
name|envWithDummy
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|MALLOC_ARENA_MAX
operator|.
name|name
argument_list|()
argument_list|,
literal|"99"
argument_list|)
expr_stmt|;
name|setNewEnvironmentHack
argument_list|(
name|envWithDummy
argument_list|)
expr_stmt|;
name|String
name|malloc
init|=
name|System
operator|.
name|getenv
argument_list|(
name|Environment
operator|.
name|MALLOC_ARENA_MAX
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"env_vars.txt"
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
comment|// So that start file is readable by the test
name|fileWriter
operator|.
name|write
argument_list|(
literal|"\necho $"
operator|+
name|Environment
operator|.
name|MALLOC_ARENA_MAX
operator|.
name|name
argument_list|()
operator|+
literal|"> "
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
literal|"\nexec sleep 100"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|close
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|malloc
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|malloc
argument_list|)
operator|)
assert|;
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
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appId
operator|.
name|setClusterTimestamp
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appAttemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setAttemptId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ContainerId
name|cId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|cId
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|// upload the script file so that the container can run it
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
comment|// set up the rest of the container
name|containerLaunchContext
operator|.
name|setUser
argument_list|(
name|containerLaunchContext
operator|.
name|getUser
argument_list|()
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
name|containerLaunchContext
operator|.
name|setResource
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|getResource
argument_list|()
operator|.
name|setMemory
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|StartContainerRequest
name|startRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|startRequest
operator|.
name|setContainerLaunchContext
argument_list|(
name|containerLaunchContext
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|startContainer
argument_list|(
name|startRequest
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
name|malloc
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
comment|// Now test the stop functionality.
comment|// Assert that the process is alive
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Process is not alive!"
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
comment|// Once more
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Process is not alive!"
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
name|StopContainerRequest
name|stopRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StopContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|stopRequest
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|stopContainer
argument_list|(
name|stopRequest
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
argument_list|)
expr_stmt|;
name|GetContainerStatusRequest
name|gcsRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetContainerStatusRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gcsRequest
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|containerManager
operator|.
name|getContainerStatus
argument_list|(
name|gcsRequest
argument_list|)
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ExitCode
operator|.
name|TERMINATED
operator|.
name|getExitCode
argument_list|()
argument_list|,
name|containerStatus
operator|.
name|getExitStatus
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
DECL|method|testDelayedKill ()
specifier|public
name|void
name|testDelayedKill
parameter_list|()
throws|throws
name|Exception
block|{
name|containerManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|File
name|processStartFile
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"pid.txt"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
comment|// setup a script that can handle sigterm gracefully
name|File
name|scriptFile
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"testscript.sh"
argument_list|)
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|scriptFile
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"#!/bin/bash\n\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"echo \"Running testscript for delayed kill\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"hello=\"Got SIGTERM\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"umask 0"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"trap \"echo $hello>> "
operator|+
name|processStartFile
operator|+
literal|"\" SIGTERM"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"echo \"Writing pid to start file\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"echo $$>> "
operator|+
name|processStartFile
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"while true; do\nsleep 1s;\ndone"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|scriptFile
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
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
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appId
operator|.
name|setClusterTimestamp
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|appAttemptId
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|appAttemptId
operator|.
name|setAttemptId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ContainerId
name|cId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|cId
operator|.
name|setApplicationAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|// upload the script file so that the container can run it
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
literal|"dest_file.sh"
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
comment|// set up the rest of the container
name|containerLaunchContext
operator|.
name|setUser
argument_list|(
name|containerLaunchContext
operator|.
name|getUser
argument_list|()
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
name|containerLaunchContext
operator|.
name|setResource
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|containerLaunchContext
operator|.
name|getResource
argument_list|()
operator|.
name|setMemory
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|StartContainerRequest
name|startRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|startRequest
operator|.
name|setContainerLaunchContext
argument_list|(
name|containerLaunchContext
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|startContainer
argument_list|(
name|startRequest
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
comment|// Now test the stop functionality.
name|StopContainerRequest
name|stopRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StopContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|stopRequest
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|containerManager
operator|.
name|stopContainer
argument_list|(
name|stopRequest
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
argument_list|)
expr_stmt|;
comment|// container stop sends a sigterm followed by a sigkill
name|GetContainerStatusRequest
name|gcsRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetContainerStatusRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gcsRequest
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|containerManager
operator|.
name|getContainerStatus
argument_list|(
name|gcsRequest
argument_list|)
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ExitCode
operator|.
name|FORCE_KILLED
operator|.
name|getExitCode
argument_list|()
argument_list|,
name|containerStatus
operator|.
name|getExitStatus
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now verify the contents of the file
comment|// Script generates a message when it receives a sigterm
comment|// so we look for that
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
name|boolean
name|foundSigTermMessage
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|line
operator|.
name|contains
argument_list|(
literal|"SIGTERM"
argument_list|)
condition|)
block|{
name|foundSigTermMessage
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Did not find sigterm message"
argument_list|,
name|foundSigTermMessage
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

