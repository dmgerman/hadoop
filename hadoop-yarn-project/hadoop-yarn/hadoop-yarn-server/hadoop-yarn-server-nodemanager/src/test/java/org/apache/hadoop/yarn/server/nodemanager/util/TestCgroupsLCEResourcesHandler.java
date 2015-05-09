begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.util
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
name|util
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
name|server
operator|.
name|nodemanager
operator|.
name|LinuxContainerExecutor
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
name|TestCGroupsHandlerImpl
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
name|ControlledClock
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
name|Assert
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
name|junit
operator|.
name|Test
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
name|*
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
name|Scanner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_class
DECL|class|TestCgroupsLCEResourcesHandler
specifier|public
class|class
name|TestCgroupsLCEResourcesHandler
block|{
DECL|field|cgroupDir
specifier|static
name|File
name|cgroupDir
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|cgroupDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|,
literal|"target"
argument_list|)
argument_list|)
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|cgroupDir
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
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|cgroupDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testcheckAndDeleteCgroup ()
specifier|public
name|void
name|testcheckAndDeleteCgroup
parameter_list|()
throws|throws
name|Exception
block|{
name|CgroupsLCEResourcesHandler
name|handler
init|=
operator|new
name|CgroupsLCEResourcesHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|setConf
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|initConfig
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|cgroupDir
argument_list|)
expr_stmt|;
comment|// Test 0
comment|// tasks file not present, should return false
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|checkAndDeleteCgroup
argument_list|(
name|cgroupDir
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|tfile
init|=
operator|new
name|File
argument_list|(
name|cgroupDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"tasks"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
name|FileUtils
operator|.
name|openOutputStream
argument_list|(
name|tfile
argument_list|)
decl_stmt|;
name|File
name|fspy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|cgroupDir
argument_list|)
decl_stmt|;
comment|// Test 1, tasks file is empty
comment|// tasks file has no data, should return true
name|Mockito
operator|.
name|stub
argument_list|(
name|fspy
operator|.
name|delete
argument_list|()
argument_list|)
operator|.
name|toReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|handler
operator|.
name|checkAndDeleteCgroup
argument_list|(
name|fspy
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test 2, tasks file has data
name|fos
operator|.
name|write
argument_list|(
literal|"1234"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// tasks has data, would not be able to delete, should return false
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|checkAndDeleteCgroup
argument_list|(
name|fspy
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|cgroupDir
argument_list|)
expr_stmt|;
block|}
comment|// Verify DeleteCgroup times out if "tasks" file contains data
annotation|@
name|Test
DECL|method|testDeleteCgroup ()
specifier|public
name|void
name|testDeleteCgroup
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ControlledClock
name|clock
init|=
operator|new
name|ControlledClock
argument_list|()
decl_stmt|;
name|CgroupsLCEResourcesHandler
name|handler
init|=
operator|new
name|CgroupsLCEResourcesHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|setConf
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|initConfig
argument_list|()
expr_stmt|;
name|handler
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|cgroupDir
argument_list|)
expr_stmt|;
comment|// Create a non-empty tasks file
name|File
name|tfile
init|=
operator|new
name|File
argument_list|(
name|cgroupDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"tasks"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
name|FileUtils
operator|.
name|openOutputStream
argument_list|(
name|tfile
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
literal|"1234"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|//NOP
block|}
name|clock
operator|.
name|tickMsec
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LINUX_CONTAINER_CGROUPS_DELETE_TIMEOUT
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|deleteCgroup
argument_list|(
name|cgroupDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|cgroupDir
argument_list|)
expr_stmt|;
block|}
DECL|class|MockLinuxContainerExecutor
specifier|static
class|class
name|MockLinuxContainerExecutor
extends|extends
name|LinuxContainerExecutor
block|{
annotation|@
name|Override
DECL|method|mountCgroups (List<String> x, String y)
specifier|public
name|void
name|mountCgroups
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|x
parameter_list|,
name|String
name|y
parameter_list|)
block|{     }
block|}
DECL|class|CustomCgroupsLCEResourceHandler
specifier|static
class|class
name|CustomCgroupsLCEResourceHandler
extends|extends
name|CgroupsLCEResourcesHandler
block|{
DECL|field|mtabFile
name|String
name|mtabFile
decl_stmt|;
DECL|field|limits
name|int
index|[]
name|limits
init|=
operator|new
name|int
index|[
literal|2
index|]
decl_stmt|;
DECL|field|generateLimitsMode
name|boolean
name|generateLimitsMode
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|getOverallLimits (float x)
name|int
index|[]
name|getOverallLimits
parameter_list|(
name|float
name|x
parameter_list|)
block|{
if|if
condition|(
name|generateLimitsMode
condition|)
block|{
return|return
name|super
operator|.
name|getOverallLimits
argument_list|(
name|x
argument_list|)
return|;
block|}
return|return
name|limits
return|;
block|}
DECL|method|setMtabFile (String file)
name|void
name|setMtabFile
parameter_list|(
name|String
name|file
parameter_list|)
block|{
name|mtabFile
operator|=
name|file
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMtabFileName ()
name|String
name|getMtabFileName
parameter_list|()
block|{
return|return
name|mtabFile
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInit ()
specifier|public
name|void
name|testInit
parameter_list|()
throws|throws
name|IOException
block|{
name|LinuxContainerExecutor
name|mockLCE
init|=
operator|new
name|MockLinuxContainerExecutor
argument_list|()
decl_stmt|;
name|CustomCgroupsLCEResourceHandler
name|handler
init|=
operator|new
name|CustomCgroupsLCEResourceHandler
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numProcessors
init|=
literal|4
decl_stmt|;
name|ResourceCalculatorPlugin
name|plugin
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ResourceCalculatorPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|numProcessors
argument_list|)
operator|.
name|when
argument_list|(
name|plugin
argument_list|)
operator|.
name|getNumProcessors
argument_list|()
expr_stmt|;
name|handler
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|handler
operator|.
name|initConfig
argument_list|()
expr_stmt|;
comment|// create mock cgroup
name|File
name|cpuCgroupMountDir
init|=
name|TestCGroupsHandlerImpl
operator|.
name|createMockCgroupMount
argument_list|(
name|cgroupDir
argument_list|,
literal|"cpu"
argument_list|)
decl_stmt|;
comment|// create mock mtab
name|File
name|mockMtab
init|=
name|TestCGroupsHandlerImpl
operator|.
name|createMockMTab
argument_list|(
name|cgroupDir
argument_list|)
decl_stmt|;
comment|// setup our handler and call init()
name|handler
operator|.
name|setMtabFile
argument_list|(
name|mockMtab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// check values
comment|// in this case, we're using all cpu so the files
comment|// shouldn't exist(because init won't create them
name|handler
operator|.
name|init
argument_list|(
name|mockLCE
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
name|File
name|periodFile
init|=
operator|new
name|File
argument_list|(
name|cpuCgroupMountDir
argument_list|,
literal|"cpu.cfs_period_us"
argument_list|)
decl_stmt|;
name|File
name|quotaFile
init|=
operator|new
name|File
argument_list|(
name|cpuCgroupMountDir
argument_list|,
literal|"cpu.cfs_quota_us"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|periodFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|quotaFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// subset of cpu being used, files should be created
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT
argument_list|,
literal|75
argument_list|)
expr_stmt|;
name|handler
operator|.
name|limits
index|[
literal|0
index|]
operator|=
literal|100
operator|*
literal|1000
expr_stmt|;
name|handler
operator|.
name|limits
index|[
literal|1
index|]
operator|=
literal|1000
operator|*
literal|1000
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|mockLCE
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
name|int
name|period
init|=
name|readIntFromFile
argument_list|(
name|periodFile
argument_list|)
decl_stmt|;
name|int
name|quota
init|=
name|readIntFromFile
argument_list|(
name|quotaFile
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
operator|*
literal|1000
argument_list|,
name|period
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000
operator|*
literal|1000
argument_list|,
name|quota
argument_list|)
expr_stmt|;
comment|// set cpu back to 100, quota should be -1
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|handler
operator|.
name|limits
index|[
literal|0
index|]
operator|=
literal|100
operator|*
literal|1000
expr_stmt|;
name|handler
operator|.
name|limits
index|[
literal|1
index|]
operator|=
literal|1000
operator|*
literal|1000
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|mockLCE
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
name|quota
operator|=
name|readIntFromFile
argument_list|(
name|quotaFile
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|quota
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|cgroupDir
argument_list|)
expr_stmt|;
block|}
DECL|method|readIntFromFile (File targetFile)
specifier|private
name|int
name|readIntFromFile
parameter_list|(
name|File
name|targetFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|scanner
operator|.
name|hasNextInt
argument_list|()
condition|?
name|scanner
operator|.
name|nextInt
argument_list|()
else|:
operator|-
literal|1
return|;
block|}
finally|finally
block|{
name|scanner
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetOverallLimits ()
specifier|public
name|void
name|testGetOverallLimits
parameter_list|()
block|{
name|int
name|expectedQuota
init|=
literal|1000
operator|*
literal|1000
decl_stmt|;
name|CgroupsLCEResourcesHandler
name|handler
init|=
operator|new
name|CgroupsLCEResourcesHandler
argument_list|()
decl_stmt|;
name|int
index|[]
name|ret
init|=
name|handler
operator|.
name|getOverallLimits
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedQuota
operator|/
literal|2
argument_list|,
name|ret
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedQuota
argument_list|,
name|ret
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|ret
operator|=
name|handler
operator|.
name|getOverallLimits
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedQuota
argument_list|,
name|ret
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|ret
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|int
index|[]
name|params
init|=
block|{
literal|0
block|,
operator|-
literal|1
block|}
decl_stmt|;
for|for
control|(
name|int
name|cores
range|:
name|params
control|)
block|{
try|try
block|{
name|handler
operator|.
name|getOverallLimits
argument_list|(
name|cores
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Function call should throw error."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ie
parameter_list|)
block|{
comment|// expected
block|}
block|}
comment|// test minimums
name|ret
operator|=
name|handler
operator|.
name|getOverallLimits
argument_list|(
literal|1000
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000
operator|*
literal|1000
argument_list|,
name|ret
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|ret
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerLimits ()
specifier|public
name|void
name|testContainerLimits
parameter_list|()
throws|throws
name|IOException
block|{
name|LinuxContainerExecutor
name|mockLCE
init|=
operator|new
name|MockLinuxContainerExecutor
argument_list|()
decl_stmt|;
name|CustomCgroupsLCEResourceHandler
name|handler
init|=
operator|new
name|CustomCgroupsLCEResourceHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|generateLimitsMode
operator|=
literal|true
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DISK_RESOURCE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numProcessors
init|=
literal|4
decl_stmt|;
name|ResourceCalculatorPlugin
name|plugin
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ResourceCalculatorPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|numProcessors
argument_list|)
operator|.
name|when
argument_list|(
name|plugin
argument_list|)
operator|.
name|getNumProcessors
argument_list|()
expr_stmt|;
name|handler
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|handler
operator|.
name|initConfig
argument_list|()
expr_stmt|;
comment|// create mock cgroup
name|File
name|cpuCgroupMountDir
init|=
name|TestCGroupsHandlerImpl
operator|.
name|createMockCgroupMount
argument_list|(
name|cgroupDir
argument_list|,
literal|"cpu"
argument_list|)
decl_stmt|;
comment|// create mock mtab
name|File
name|mockMtab
init|=
name|TestCGroupsHandlerImpl
operator|.
name|createMockMTab
argument_list|(
name|cgroupDir
argument_list|)
decl_stmt|;
comment|// setup our handler and call init()
name|handler
operator|.
name|setMtabFile
argument_list|(
name|mockMtab
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|mockLCE
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
comment|// check the controller paths map isn't empty
name|ContainerId
name|id
init|=
name|ContainerId
operator|.
name|fromString
argument_list|(
literal|"container_1_1_1_1"
argument_list|)
decl_stmt|;
name|handler
operator|.
name|preExecute
argument_list|(
name|id
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|handler
operator|.
name|getControllerPaths
argument_list|()
argument_list|)
expr_stmt|;
comment|// check values
comment|// default case - files shouldn't exist, strict mode off by default
name|File
name|containerCpuDir
init|=
operator|new
name|File
argument_list|(
name|cpuCgroupMountDir
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerCpuDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerCpuDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|periodFile
init|=
operator|new
name|File
argument_list|(
name|containerCpuDir
argument_list|,
literal|"cpu.cfs_period_us"
argument_list|)
decl_stmt|;
name|File
name|quotaFile
init|=
operator|new
name|File
argument_list|(
name|containerCpuDir
argument_list|,
literal|"cpu.cfs_quota_us"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|periodFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|quotaFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// no files created because we're using all cpu
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|containerCpuDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|handler
operator|.
name|initConfig
argument_list|()
expr_stmt|;
name|handler
operator|.
name|preExecute
argument_list|(
name|id
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_VCORES
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerCpuDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerCpuDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|periodFile
operator|=
operator|new
name|File
argument_list|(
name|containerCpuDir
argument_list|,
literal|"cpu.cfs_period_us"
argument_list|)
expr_stmt|;
name|quotaFile
operator|=
operator|new
name|File
argument_list|(
name|containerCpuDir
argument_list|,
literal|"cpu.cfs_quota_us"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|periodFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|quotaFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// 50% of CPU
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|containerCpuDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|handler
operator|.
name|initConfig
argument_list|()
expr_stmt|;
name|handler
operator|.
name|preExecute
argument_list|(
name|id
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_VCORES
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerCpuDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerCpuDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|periodFile
operator|=
operator|new
name|File
argument_list|(
name|containerCpuDir
argument_list|,
literal|"cpu.cfs_period_us"
argument_list|)
expr_stmt|;
name|quotaFile
operator|=
operator|new
name|File
argument_list|(
name|containerCpuDir
argument_list|,
literal|"cpu.cfs_quota_us"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|periodFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|quotaFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|500
operator|*
literal|1000
argument_list|,
name|readIntFromFile
argument_list|(
name|periodFile
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000
operator|*
literal|1000
argument_list|,
name|readIntFromFile
argument_list|(
name|quotaFile
argument_list|)
argument_list|)
expr_stmt|;
comment|// CGroups set to 50% of CPU, container set to 50% of YARN CPU
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|containerCpuDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|handler
operator|.
name|initConfig
argument_list|()
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|mockLCE
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
name|handler
operator|.
name|preExecute
argument_list|(
name|id
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_VCORES
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerCpuDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerCpuDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|periodFile
operator|=
operator|new
name|File
argument_list|(
name|containerCpuDir
argument_list|,
literal|"cpu.cfs_period_us"
argument_list|)
expr_stmt|;
name|quotaFile
operator|=
operator|new
name|File
argument_list|(
name|containerCpuDir
argument_list|,
literal|"cpu.cfs_quota_us"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|periodFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|quotaFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000
operator|*
literal|1000
argument_list|,
name|readIntFromFile
argument_list|(
name|periodFile
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1000
operator|*
literal|1000
argument_list|,
name|readIntFromFile
argument_list|(
name|quotaFile
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|cgroupDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

