begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources
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
name|resources
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
name|lang3
operator|.
name|SystemUtils
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
name|ResourceCalculatorProcessTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
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
name|Random
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
comment|/**  * Functional test for CGroupsResourceCalculator to compare two resource  * calculators. It is OS dependent.  * Ignored in automated tests due to flakiness by design.  */
end_comment

begin_class
DECL|class|TestCompareResourceCalculators
specifier|public
class|class
name|TestCompareResourceCalculators
block|{
DECL|field|target
specifier|private
name|Process
name|target
init|=
literal|null
decl_stmt|;
DECL|field|cgroup
specifier|private
name|String
name|cgroup
init|=
literal|null
decl_stmt|;
DECL|field|cgroupCPU
specifier|private
name|String
name|cgroupCPU
init|=
literal|null
decl_stmt|;
DECL|field|cgroupMemory
specifier|private
name|String
name|cgroupMemory
init|=
literal|null
decl_stmt|;
DECL|field|SHMEM_KB
specifier|public
specifier|static
specifier|final
name|long
name|SHMEM_KB
init|=
literal|100
operator|*
literal|1024
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|SystemUtils
operator|.
name|IS_OS_LINUX
argument_list|)
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
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_HIERARCHY
argument_list|,
literal|"TestCompareResourceCalculators"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_MOUNT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LINUX_CONTAINER_CGROUPS_MOUNT_PATH
argument_list|,
literal|"/sys/fs/cgroup"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CPU_RESOURCE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ResourceHandlerChain
name|module
init|=
literal|null
decl_stmt|;
try|try
block|{
name|module
operator|=
name|ResourceHandlerModule
operator|.
name|getConfiguredResourceHandlerChain
argument_list|(
name|conf
argument_list|,
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceHandlerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Cannot access cgroups"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|module
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|ResourceHandlerModule
operator|.
name|getCGroupsHandler
argument_list|()
operator|.
name|getControllerPath
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|CPU
argument_list|)
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|ResourceHandlerModule
operator|.
name|getCGroupsHandler
argument_list|()
operator|.
name|getControllerPath
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|)
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|cgroup
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|cgroupCPU
operator|=
name|ResourceHandlerModule
operator|.
name|getCGroupsHandler
argument_list|()
operator|.
name|getPathForCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|CPU
argument_list|,
name|cgroup
argument_list|)
expr_stmt|;
name|cgroupMemory
operator|=
name|ResourceHandlerModule
operator|.
name|getCGroupsHandler
argument_list|()
operator|.
name|getPathForCGroup
argument_list|(
name|CGroupsHandler
operator|.
name|CGroupController
operator|.
name|MEMORY
argument_list|,
name|cgroup
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
name|YarnException
block|{
name|stopTestProcess
argument_list|()
expr_stmt|;
block|}
comment|// Ignored in automated tests due to flakiness by design
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testCompareResults ()
specifier|public
name|void
name|testCompareResults
parameter_list|()
throws|throws
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|startTestProcess
argument_list|()
expr_stmt|;
name|ProcfsBasedProcessTree
name|legacyCalculator
init|=
operator|new
name|ProcfsBasedProcessTree
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|getPid
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|CGroupsResourceCalculator
name|cgroupsCalculator
init|=
operator|new
name|CGroupsResourceCalculator
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|getPid
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|cgroupsCalculator
operator|.
name|setCGroupFilePaths
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
operator|++
name|i
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|compareMetrics
argument_list|(
name|legacyCalculator
argument_list|,
name|cgroupsCalculator
argument_list|)
expr_stmt|;
block|}
name|stopTestProcess
argument_list|()
expr_stmt|;
name|ensureCleanedUp
argument_list|(
name|legacyCalculator
argument_list|,
name|cgroupsCalculator
argument_list|)
expr_stmt|;
block|}
DECL|method|ensureCleanedUp ( ResourceCalculatorProcessTree metric1, ResourceCalculatorProcessTree metric2)
specifier|private
name|void
name|ensureCleanedUp
parameter_list|(
name|ResourceCalculatorProcessTree
name|metric1
parameter_list|,
name|ResourceCalculatorProcessTree
name|metric2
parameter_list|)
block|{
name|metric1
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|metric2
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|long
name|pmem1
init|=
name|metric1
operator|.
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|pmem2
init|=
name|metric2
operator|.
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|pmem1
operator|+
literal|" "
operator|+
name|pmem2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"pmem should be invalid "
operator|+
name|pmem1
operator|+
literal|" "
operator|+
name|pmem2
argument_list|,
name|pmem1
operator|==
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
operator|&&
name|pmem2
operator|==
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
argument_list|)
expr_stmt|;
name|long
name|vmem1
init|=
name|metric1
operator|.
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|vmem2
init|=
name|metric2
operator|.
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|vmem1
operator|+
literal|" "
operator|+
name|vmem2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"vmem Error outside range "
operator|+
name|vmem1
operator|+
literal|" "
operator|+
name|vmem2
argument_list|,
name|vmem1
operator|==
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
operator|&&
name|vmem2
operator|==
name|ResourceCalculatorProcessTree
operator|.
name|UNAVAILABLE
argument_list|)
expr_stmt|;
name|float
name|cpu1
init|=
name|metric1
operator|.
name|getCpuUsagePercent
argument_list|()
decl_stmt|;
name|float
name|cpu2
init|=
name|metric2
operator|.
name|getCpuUsagePercent
argument_list|()
decl_stmt|;
comment|// TODO ProcfsBasedProcessTree may report negative on process exit
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"CPU% Error outside range "
operator|+
name|cpu1
operator|+
literal|" "
operator|+
name|cpu2
argument_list|,
name|cpu1
operator|==
literal|0
operator|&&
name|cpu2
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|compareMetrics ( ResourceCalculatorProcessTree metric1, ResourceCalculatorProcessTree metric2)
specifier|private
name|void
name|compareMetrics
parameter_list|(
name|ResourceCalculatorProcessTree
name|metric1
parameter_list|,
name|ResourceCalculatorProcessTree
name|metric2
parameter_list|)
block|{
name|metric1
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|metric2
operator|.
name|updateProcessTree
argument_list|()
expr_stmt|;
name|long
name|pmem1
init|=
name|metric1
operator|.
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|pmem2
init|=
name|metric2
operator|.
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// TODO The calculation is different and cgroup
comment|// can report a small amount after process stop
comment|// This is not an issue since the cgroup is deleted
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|pmem1
operator|+
literal|" "
operator|+
operator|(
name|pmem2
operator|-
name|SHMEM_KB
operator|*
literal|1024
operator|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"pmem Error outside range "
operator|+
name|pmem1
operator|+
literal|" "
operator|+
name|pmem2
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|pmem1
operator|-
operator|(
name|pmem2
operator|-
name|SHMEM_KB
operator|*
literal|1024
operator|)
argument_list|)
operator|<
literal|5000000
argument_list|)
expr_stmt|;
name|long
name|vmem1
init|=
name|metric1
operator|.
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|vmem2
init|=
name|metric2
operator|.
name|getRssMemorySize
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|vmem1
operator|+
literal|" "
operator|+
operator|(
name|vmem2
operator|-
name|SHMEM_KB
operator|*
literal|1024
operator|)
argument_list|)
expr_stmt|;
comment|// TODO The calculation is different and cgroup
comment|// can report a small amount after process stop
comment|// This is not an issue since the cgroup is deleted
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"vmem Error outside range "
operator|+
name|vmem1
operator|+
literal|" "
operator|+
name|vmem2
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|vmem1
operator|-
operator|(
name|vmem2
operator|-
name|SHMEM_KB
operator|*
literal|1024
operator|)
argument_list|)
operator|<
literal|5000000
argument_list|)
expr_stmt|;
name|float
name|cpu1
init|=
name|metric1
operator|.
name|getCpuUsagePercent
argument_list|()
decl_stmt|;
name|float
name|cpu2
init|=
name|metric2
operator|.
name|getCpuUsagePercent
argument_list|()
decl_stmt|;
if|if
condition|(
name|cpu1
operator|>
literal|0
condition|)
block|{
comment|// TODO ProcfsBasedProcessTree may report negative on process exit
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"CPU% Error outside range "
operator|+
name|cpu1
operator|+
literal|" "
operator|+
name|cpu2
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|cpu2
operator|-
name|cpu1
argument_list|)
operator|<
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|startTestProcess ()
specifier|private
name|void
name|startTestProcess
parameter_list|()
throws|throws
name|IOException
block|{
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|()
decl_stmt|;
name|String
name|script
init|=
literal|"mkdir -p "
operator|+
name|cgroupCPU
operator|+
literal|";"
operator|+
literal|"echo $$>"
operator|+
name|cgroupCPU
operator|+
literal|"/tasks;"
operator|+
literal|"mkdir -p "
operator|+
name|cgroupMemory
operator|+
literal|";"
operator|+
literal|"echo $$>"
operator|+
name|cgroupMemory
operator|+
literal|"/tasks;"
operator|+
literal|"dd if=/dev/zero of=/dev/shm/"
operator|+
name|cgroup
operator|+
literal|" bs=1k count="
operator|+
name|SHMEM_KB
operator|+
literal|";"
operator|+
literal|"dd if=/dev/zero of=/dev/null bs=1k&"
operator|+
literal|"echo $!>/tmp/\" + cgroup + \".pid;"
operator|+
comment|//"echo while [ -f /tmp/" + cgroup + ".pid ]; do sleep 1; done;" +
literal|"sleep 10000;"
operator|+
literal|"echo kill $(jobs -p);"
decl_stmt|;
name|builder
operator|.
name|command
argument_list|(
literal|"bash"
argument_list|,
literal|"-c"
argument_list|,
name|script
argument_list|)
expr_stmt|;
name|builder
operator|.
name|redirectError
argument_list|(
operator|new
name|File
argument_list|(
literal|"/tmp/a.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|redirectOutput
argument_list|(
operator|new
name|File
argument_list|(
literal|"/tmp/b.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|target
operator|=
name|builder
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stopTestProcess ()
specifier|private
name|void
name|stopTestProcess
parameter_list|()
throws|throws
name|YarnException
block|{
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
block|{
name|target
operator|.
name|destroyForcibly
argument_list|()
expr_stmt|;
name|target
operator|=
literal|null
expr_stmt|;
block|}
try|try
block|{
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|()
decl_stmt|;
name|String
name|script
init|=
literal|"rm -f /dev/shm/"
operator|+
name|cgroup
operator|+
literal|";"
operator|+
literal|"cat "
operator|+
name|cgroupCPU
operator|+
literal|"/tasks | xargs kill;"
operator|+
literal|"rm -f /tmp/"
operator|+
name|cgroup
operator|+
literal|".pid;"
operator|+
literal|"sleep 4;"
operator|+
literal|"rmdir "
operator|+
name|cgroupCPU
operator|+
literal|";"
operator|+
literal|"rmdir "
operator|+
name|cgroupMemory
operator|+
literal|";"
decl_stmt|;
name|builder
operator|.
name|command
argument_list|(
literal|"bash"
argument_list|,
literal|"-c"
argument_list|,
name|script
argument_list|)
expr_stmt|;
name|Process
name|cleanup
init|=
name|builder
operator|.
name|start
argument_list|()
decl_stmt|;
name|cleanup
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Could not clean up"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getPid ()
specifier|private
name|long
name|getPid
parameter_list|()
throws|throws
name|YarnException
block|{
name|Class
name|processClass
init|=
name|target
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|processClass
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"java.lang.UNIXProcess"
argument_list|)
condition|)
block|{
try|try
block|{
name|Field
name|pidField
init|=
name|processClass
operator|.
name|getDeclaredField
argument_list|(
literal|"pid"
argument_list|)
decl_stmt|;
name|pidField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|long
name|pid
init|=
name|pidField
operator|.
name|getLong
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|pidField
operator|.
name|setAccessible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|pid
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Reflection error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Not Unix "
operator|+
name|processClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

