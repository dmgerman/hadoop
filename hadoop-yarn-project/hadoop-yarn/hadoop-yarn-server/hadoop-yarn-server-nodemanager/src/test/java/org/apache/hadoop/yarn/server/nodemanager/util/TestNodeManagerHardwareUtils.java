begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Mockito
import|;
end_import

begin_comment
comment|/**  * Test the various functions provided by the NodeManagerHardwareUtils class.  */
end_comment

begin_class
DECL|class|TestNodeManagerHardwareUtils
specifier|public
class|class
name|TestNodeManagerHardwareUtils
block|{
DECL|class|TestResourceCalculatorPlugin
specifier|static
class|class
name|TestResourceCalculatorPlugin
extends|extends
name|ResourceCalculatorPlugin
block|{
DECL|method|TestResourceCalculatorPlugin ()
name|TestResourceCalculatorPlugin
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getVirtualMemorySize ()
specifier|public
name|long
name|getVirtualMemorySize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getPhysicalMemorySize ()
specifier|public
name|long
name|getPhysicalMemorySize
parameter_list|()
block|{
name|long
name|ret
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|maxMemory
argument_list|()
operator|*
literal|2
decl_stmt|;
name|ret
operator|=
name|ret
operator|+
operator|(
literal|4L
operator|*
literal|1024
operator|*
literal|1024
operator|*
literal|1024
operator|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getAvailableVirtualMemorySize ()
specifier|public
name|long
name|getAvailableVirtualMemorySize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getAvailablePhysicalMemorySize ()
specifier|public
name|long
name|getAvailablePhysicalMemorySize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumProcessors ()
specifier|public
name|int
name|getNumProcessors
parameter_list|()
block|{
return|return
literal|8
return|;
block|}
annotation|@
name|Override
DECL|method|getCpuFrequency ()
specifier|public
name|long
name|getCpuFrequency
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCumulativeCpuTime ()
specifier|public
name|long
name|getCumulativeCpuTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getCpuUsagePercentage ()
specifier|public
name|float
name|getCpuUsagePercentage
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getNumCores ()
specifier|public
name|int
name|getNumCores
parameter_list|()
block|{
return|return
literal|4
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetContainerCPU ()
specifier|public
name|void
name|testGetContainerCPU
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|float
name|ret
decl_stmt|;
specifier|final
name|int
name|numProcessors
init|=
literal|8
decl_stmt|;
specifier|final
name|int
name|numCores
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
name|Mockito
operator|.
name|doReturn
argument_list|(
name|numCores
argument_list|)
operator|.
name|when
argument_list|(
name|plugin
argument_list|)
operator|.
name|getNumCores
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|catchFlag
init|=
literal|false
decl_stmt|;
try|try
block|{
name|NodeManagerHardwareUtils
operator|.
name|getContainersCPUs
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"getContainerCores should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ie
parameter_list|)
block|{
name|catchFlag
operator|=
literal|true
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|catchFlag
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
literal|100
argument_list|)
expr_stmt|;
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainersCPUs
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
operator|(
name|int
operator|)
name|ret
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
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainersCPUs
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
name|int
operator|)
name|ret
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
literal|75
argument_list|)
expr_stmt|;
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainersCPUs
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
name|int
operator|)
name|ret
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
literal|85
argument_list|)
expr_stmt|;
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainersCPUs
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3.4
argument_list|,
name|ret
argument_list|,
literal|0.1
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
literal|110
argument_list|)
expr_stmt|;
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainersCPUs
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
operator|(
name|int
operator|)
name|ret
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetVCores ()
specifier|public
name|void
name|testGetVCores
parameter_list|()
block|{
name|ResourceCalculatorPlugin
name|plugin
init|=
operator|new
name|TestResourceCalculatorPlugin
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PCORES_VCORES_MULTIPLIER
argument_list|,
literal|1.25f
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
name|NodeManagerHardwareUtils
operator|.
name|getVCores
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_NM_VCORES
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ENABLE_HARDWARE_CAPABILITY_DETECTION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getVCores
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_COUNT_LOGICAL_PROCESSORS_AS_CORES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getVCores
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VCORES
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getVCores
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|ret
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf1
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf1
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ENABLE_HARDWARE_CAPABILITY_DETECTION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VCORES
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|ret
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getVCores
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetContainerMemoryMB ()
specifier|public
name|void
name|testGetContainerMemoryMB
parameter_list|()
throws|throws
name|Exception
block|{
name|ResourceCalculatorPlugin
name|plugin
init|=
operator|new
name|TestResourceCalculatorPlugin
argument_list|()
decl_stmt|;
name|long
name|physicalMemMB
init|=
name|plugin
operator|.
name|getPhysicalMemorySize
argument_list|()
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
decl_stmt|;
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
name|NM_ENABLE_HARDWARE_CAPABILITY_DETECTION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|long
name|mem
init|=
name|NodeManagerHardwareUtils
operator|.
name|getContainerMemoryMB
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PMEM_MB
argument_list|,
name|mem
argument_list|)
expr_stmt|;
name|mem
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainerMemoryMB
argument_list|(
name|plugin
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|int
name|hadoopHeapSizeMB
init|=
call|(
name|int
call|)
argument_list|(
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|maxMemory
argument_list|()
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|)
decl_stmt|;
name|int
name|calculatedMemMB
init|=
call|(
name|int
call|)
argument_list|(
literal|0.8
operator|*
operator|(
name|physicalMemMB
operator|-
operator|(
literal|2
operator|*
name|hadoopHeapSizeMB
operator|)
operator|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|calculatedMemMB
argument_list|,
name|mem
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_MB
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|mem
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainerMemoryMB
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|mem
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ENABLE_HARDWARE_CAPABILITY_DETECTION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|mem
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainerMemoryMB
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PMEM_MB
argument_list|,
name|mem
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_MB
argument_list|,
literal|10
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|mem
operator|=
name|NodeManagerHardwareUtils
operator|.
name|getContainerMemoryMB
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10
operator|*
literal|1024
argument_list|,
name|mem
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

