begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
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
name|api
operator|.
name|protocolrecords
operator|.
name|ResourceTypes
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
name|ResourceInformation
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
name|ResourceRequest
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
name|YarnRuntimeException
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
name|resourcemanager
operator|.
name|MockAM
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
name|resourcemanager
operator|.
name|MockNM
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
name|resourcemanager
operator|.
name|MockRM
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
name|resourcemanager
operator|.
name|resource
operator|.
name|TestResourceProfiles
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|resourcemanager
operator|.
name|rmcontainer
operator|.
name|RMContainerState
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|SchedulerNodeReport
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
name|resource
operator|.
name|DefaultResourceCalculator
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
name|resource
operator|.
name|DominantResourceCalculator
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
name|resource
operator|.
name|ResourceUtils
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Test Capacity Scheduler with multiple resource types.  */
end_comment

begin_class
DECL|class|TestCapacitySchedulerWithMultiResourceTypes
specifier|public
class|class
name|TestCapacitySchedulerWithMultiResourceTypes
block|{
DECL|field|RESOURCE_1
specifier|private
specifier|static
name|String
name|RESOURCE_1
init|=
literal|"res1"
decl_stmt|;
DECL|field|A_QUEUE
specifier|private
specifier|static
specifier|final
name|String
name|A_QUEUE
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a"
decl_stmt|;
DECL|field|B_QUEUE
specifier|private
specifier|static
specifier|final
name|String
name|B_QUEUE
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".b"
decl_stmt|;
DECL|field|A_CAPACITY
specifier|private
specifier|static
name|float
name|A_CAPACITY
init|=
literal|50.0f
decl_stmt|;
DECL|field|B_CAPACITY
specifier|private
specifier|static
name|float
name|B_CAPACITY
init|=
literal|50.0f
decl_stmt|;
DECL|method|setupResources (boolean withGpu)
specifier|private
name|void
name|setupResources
parameter_list|(
name|boolean
name|withGpu
parameter_list|)
block|{
comment|// Initialize resource map
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceInformation
argument_list|>
name|riMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Initialize mandatory resources
name|ResourceInformation
name|memory
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getUnits
argument_list|()
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|)
decl_stmt|;
name|ResourceInformation
name|vcores
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|,
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getUnits
argument_list|()
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|)
decl_stmt|;
name|riMap
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|,
name|memory
argument_list|)
expr_stmt|;
name|riMap
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|,
name|vcores
argument_list|)
expr_stmt|;
if|if
condition|(
name|withGpu
condition|)
block|{
name|riMap
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|,
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|,
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|,
literal|0
argument_list|,
literal|3333L
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|riMap
operator|.
name|put
argument_list|(
name|RESOURCE_1
argument_list|,
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|RESOURCE_1
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|,
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|,
literal|0
argument_list|,
literal|3333L
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ResourceUtils
operator|.
name|initializeResourcesFromResourceInformationMap
argument_list|(
name|riMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaximumAllocationRefreshWithMultipleResourceTypes ()
specifier|public
name|void
name|testMaximumAllocationRefreshWithMultipleResourceTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|setupResources
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|CapacitySchedulerConfiguration
name|csconf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|csconf
operator|.
name|setMaximumApplicationMasterResourcePerQueuePercent
argument_list|(
literal|"root"
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setMaximumAMResourcePercentPerPartition
argument_list|(
literal|"root"
argument_list|,
literal|""
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setMaximumApplicationMasterResourcePerQueuePercent
argument_list|(
literal|"root.default"
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setMaximumAMResourcePercentPerPartition
argument_list|(
literal|"root.default"
argument_list|,
literal|""
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setResourceComparator
argument_list|(
name|DominantResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES
argument_list|,
name|RESOURCE_1
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES
operator|+
literal|"."
operator|+
name|RESOURCE_1
operator|+
literal|".maximum-allocation"
argument_list|,
literal|3333
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|csconf
argument_list|)
decl_stmt|;
comment|// Don't reset resource types since we have already configured resource
comment|// types
name|conf
operator|.
name|setBoolean
argument_list|(
name|TestResourceProfiles
operator|.
name|TEST_CONF_RESET_RESOURCE_TYPES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|CapacityScheduler
name|cs
init|=
operator|(
name|CapacityScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3333L
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|RESOURCE_1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3333L
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|RESOURCE_1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set RES_1 to 3332 (less than 3333) and refresh CS, failures expected.
name|csconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES
argument_list|,
name|RESOURCE_1
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES
operator|+
literal|"."
operator|+
name|RESOURCE_1
operator|+
literal|".maximum-allocation"
argument_list|,
literal|3332
argument_list|)
expr_stmt|;
name|boolean
name|exception
init|=
literal|false
decl_stmt|;
try|try
block|{
name|cs
operator|.
name|reinitialize
argument_list|(
name|csconf
argument_list|,
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
literal|true
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should have exception in CS"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
comment|// Maximum allocation won't be updated
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3333L
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|RESOURCE_1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3333L
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|RESOURCE_1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set RES_1 to 3334 and refresh CS, should success
name|csconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES
argument_list|,
name|RESOURCE_1
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES
operator|+
literal|"."
operator|+
name|RESOURCE_1
operator|+
literal|".maximum-allocation"
argument_list|,
literal|3334
argument_list|)
expr_stmt|;
name|cs
operator|.
name|reinitialize
argument_list|(
name|csconf
argument_list|,
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
expr_stmt|;
comment|// Maximum allocation will be updated
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3334
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|RESOURCE_1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Since we haven't updated the real configuration of ResourceUtils,
comment|// cs.getMaximumAllocation won't be updated.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3333
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|RESOURCE_1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|,
name|cs
operator|.
name|getMaximumAllocation
argument_list|()
operator|.
name|getResourceValue
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|)
argument_list|)
expr_stmt|;
name|rm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultResourceCalculatorWithThirdResourceTypes ()
specifier|public
name|void
name|testDefaultResourceCalculatorWithThirdResourceTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|csconf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|csconf
operator|.
name|setResourceComparator
argument_list|(
name|DefaultResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|csconf
argument_list|)
decl_stmt|;
name|String
index|[]
name|res1
init|=
block|{
literal|"resource1"
block|,
literal|"M"
block|}
decl_stmt|;
name|String
index|[]
name|res2
init|=
block|{
literal|"resource2"
block|,
literal|"G"
block|}
decl_stmt|;
name|String
index|[]
name|res3
init|=
block|{
literal|"resource3"
block|,
literal|"H"
block|}
decl_stmt|;
name|String
index|[]
index|[]
name|test
init|=
block|{
name|res1
block|,
name|res2
block|,
name|res3
block|}
decl_stmt|;
name|String
name|resSt
init|=
literal|""
decl_stmt|;
for|for
control|(
name|String
index|[]
name|resources
range|:
name|test
control|)
block|{
name|resSt
operator|+=
operator|(
name|resources
index|[
literal|0
index|]
operator|+
literal|","
operator|)
expr_stmt|;
block|}
name|resSt
operator|=
name|resSt
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|resSt
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES
argument_list|,
name|resSt
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|boolean
name|exception
init|=
literal|false
decl_stmt|;
try|try
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|exception
operator|=
literal|true
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Should have exception in CS"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxLimitsOfQueueWithMultipleResources ()
specifier|public
name|void
name|testMaxLimitsOfQueueWithMultipleResources
parameter_list|()
throws|throws
name|Exception
block|{
name|setupResources
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|GB
init|=
literal|1024
decl_stmt|;
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|csConf
operator|.
name|setMaximumApplicationMasterResourcePerQueuePercent
argument_list|(
literal|"root"
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setMaximumAMResourcePercentPerPartition
argument_list|(
literal|"root"
argument_list|,
literal|""
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setMaximumApplicationMasterResourcePerQueuePercent
argument_list|(
literal|"root.default"
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setMaximumAMResourcePercentPerPartition
argument_list|(
literal|"root.default"
argument_list|,
literal|""
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setResourceComparator
argument_list|(
name|DominantResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCE_TYPES
argument_list|,
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
expr_stmt|;
comment|// Define top-level queues
name|csConf
operator|.
name|setQueues
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|)
expr_stmt|;
comment|// Set each queue to consider 50% each.
name|csConf
operator|.
name|setCapacity
argument_list|(
name|A_QUEUE
argument_list|,
name|A_CAPACITY
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setCapacity
argument_list|(
name|B_QUEUE
argument_list|,
name|B_CAPACITY
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setMaximumCapacity
argument_list|(
name|A_QUEUE
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setUserLimitFactor
argument_list|(
name|A_QUEUE
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|csConf
argument_list|)
decl_stmt|;
comment|// Don't reset resource types since we have already configured resource
comment|// types
name|conf
operator|.
name|setBoolean
argument_list|(
name|TestResourceProfiles
operator|.
name|TEST_CONF_RESET_RESOURCE_TYPES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nameToValues
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|nameToValues
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|// Register NM1 with 10GB memory, 4 CPU and 4 GPU
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
name|TestUtils
operator|.
name|createResource
argument_list|(
literal|10
operator|*
name|GB
argument_list|,
literal|4
argument_list|,
name|nameToValues
argument_list|)
argument_list|)
decl_stmt|;
name|nameToValues
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Register NM2 with 10GB memory, 4 CPU and 0 GPU
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1235"
argument_list|,
name|TestUtils
operator|.
name|createResource
argument_list|(
literal|10
operator|*
name|GB
argument_list|,
literal|4
argument_list|,
name|nameToValues
argument_list|)
argument_list|)
expr_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1024
argument_list|,
literal|"app-1"
argument_list|,
literal|"user1"
argument_list|,
literal|null
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|SchedulerNodeReport
name|report_nm1
init|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
comment|// check node report
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|9
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|nameToValues
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|Resource
name|containerGpuResource
init|=
name|TestUtils
operator|.
name|createResource
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|,
name|nameToValues
argument_list|)
decl_stmt|;
comment|// Allocate one container which takes all 4 GPU
name|am1
operator|.
name|allocate
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"*"
argument_list|,
name|containerGpuResource
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId2
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId2
argument_list|,
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|)
argument_list|)
expr_stmt|;
comment|// Acquire this container
name|am1
operator|.
name|allocate
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|report_nm1
operator|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|nameToValues
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Resource
name|containerResource
init|=
name|TestUtils
operator|.
name|createResource
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|,
name|nameToValues
argument_list|)
decl_stmt|;
comment|// Allocate one more container which doesnt need GPU
name|am1
operator|.
name|allocate
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"*"
argument_list|,
name|containerResource
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId3
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId3
argument_list|,
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|)
argument_list|)
expr_stmt|;
name|report_nm1
operator|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getResourceInformation
argument_list|(
name|ResourceInformation
operator|.
name|GPU_URI
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

