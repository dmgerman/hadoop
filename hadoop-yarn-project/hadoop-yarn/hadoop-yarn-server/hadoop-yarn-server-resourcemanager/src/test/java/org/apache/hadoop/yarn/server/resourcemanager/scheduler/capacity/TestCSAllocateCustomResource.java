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
name|LocalConfigurationProvider
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
name|MockRMAppSubmissionData
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
name|MockRMAppSubmitter
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
name|RMContext
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
name|nodelabels
operator|.
name|NullRMNodeLabelsManager
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|placement
operator|.
name|PlacementManager
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
name|rmnode
operator|.
name|RMNode
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
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerApp
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
name|event
operator|.
name|NodeUpdateSchedulerEvent
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
name|ResourceCalculator
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
name|Resources
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
name|ArrayList
import|;
end_import

begin_import
import|import static
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
operator|.
name|CapacitySchedulerConfiguration
operator|.
name|MAXIMUM_ALLOCATION_MB
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
name|spy
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
comment|/**  * Test case for custom resource container allocation.  * for capacity scheduler  * */
end_comment

begin_class
DECL|class|TestCSAllocateCustomResource
specifier|public
class|class
name|TestCSAllocateCustomResource
block|{
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|mgr
specifier|private
name|RMNodeLabelsManager
name|mgr
decl_stmt|;
DECL|field|resourceTypesFile
specifier|private
name|File
name|resourceTypesFile
init|=
literal|null
decl_stmt|;
DECL|field|g
specifier|private
specifier|final
name|int
name|g
init|=
literal|1024
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
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
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
name|mgr
operator|=
operator|new
name|NullRMNodeLabelsManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|init
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
if|if
condition|(
name|resourceTypesFile
operator|!=
literal|null
operator|&&
name|resourceTypesFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|resourceTypesFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test containers request custom resource.    * */
annotation|@
name|Test
DECL|method|testCapacitySchedulerJobWhenConfigureCustomResourceType ()
specifier|public
name|void
name|testCapacitySchedulerJobWhenConfigureCustomResourceType
parameter_list|()
throws|throws
name|Exception
block|{
comment|// reset resource types
name|ResourceUtils
operator|.
name|resetResourceTypes
argument_list|()
expr_stmt|;
name|String
name|resourceTypesFileName
init|=
literal|"resource-types-test.xml"
decl_stmt|;
name|File
name|source
init|=
operator|new
name|File
argument_list|(
name|conf
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|resourceTypesFileName
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|resourceTypesFile
operator|=
operator|new
name|File
argument_list|(
name|source
operator|.
name|getParent
argument_list|()
argument_list|,
literal|"resource-types.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|source
argument_list|,
name|resourceTypesFile
argument_list|)
expr_stmt|;
name|CapacitySchedulerConfiguration
name|newConf
init|=
operator|(
name|CapacitySchedulerConfiguration
operator|)
name|TestUtils
operator|.
name|getConfigurationWithMultipleQueues
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|newConf
operator|.
name|setClass
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|RESOURCE_CALCULATOR_CLASS
argument_list|,
name|DominantResourceCalculator
operator|.
name|class
argument_list|,
name|ResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|newConf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|getQueuePrefix
argument_list|(
literal|"root.a"
argument_list|)
operator|+
name|MAXIMUM_ALLOCATION_MB
argument_list|,
literal|"4096"
argument_list|)
expr_stmt|;
comment|// We must set this to false to avoid MockRM init configuration with
comment|// resource-types.xml by ResourceUtils.resetResourceTypes(conf);
name|newConf
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
comment|//start RM
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|newConf
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//register node with custom resource
name|String
name|customResourceType
init|=
literal|"yarn.io/gpu"
decl_stmt|;
name|Resource
name|nodeResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|4
operator|*
name|g
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|nodeResource
operator|.
name|setResourceValue
argument_list|(
name|customResourceType
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
name|nodeResource
argument_list|)
decl_stmt|;
comment|// submit app
name|Resource
name|amResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1
operator|*
name|g
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|amResource
operator|.
name|setResourceValue
argument_list|(
name|customResourceType
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|RMApp
name|app1
init|=
name|MockRMAppSubmitter
operator|.
name|submit
argument_list|(
name|rm
argument_list|,
name|MockRMAppSubmissionData
operator|.
name|Builder
operator|.
name|createWithResource
argument_list|(
name|amResource
argument_list|,
name|rm
argument_list|)
operator|.
name|withAppName
argument_list|(
literal|"app"
argument_list|)
operator|.
name|withUser
argument_list|(
literal|"user"
argument_list|)
operator|.
name|withAcls
argument_list|(
literal|null
argument_list|)
operator|.
name|withQueue
argument_list|(
literal|"a"
argument_list|)
operator|.
name|build
argument_list|()
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
comment|// am request containers
name|Resource
name|cResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1
operator|*
name|g
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|amResource
operator|.
name|setResourceValue
argument_list|(
name|customResourceType
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
name|cResource
argument_list|,
literal|2
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|)
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
name|RMNode
name|rmNode1
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|FiCaSchedulerApp
name|schedulerApp1
init|=
name|cs
operator|.
name|getApplicationAttempt
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Do nm heartbeats 1 times, will allocate a container on nm1
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNode1
argument_list|)
argument_list|)
expr_stmt|;
name|rm
operator|.
name|drainEvents
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|schedulerApp1
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test CS initialized with custom resource types loaded.    * */
annotation|@
name|Test
DECL|method|testCapacitySchedulerInitWithCustomResourceType ()
specifier|public
name|void
name|testCapacitySchedulerInitWithCustomResourceType
parameter_list|()
throws|throws
name|IOException
block|{
comment|// reset resource types
name|ResourceUtils
operator|.
name|resetResourceTypes
argument_list|()
expr_stmt|;
name|String
name|resourceTypesFileName
init|=
literal|"resource-types-test.xml"
decl_stmt|;
name|File
name|source
init|=
operator|new
name|File
argument_list|(
name|conf
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|resourceTypesFileName
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|resourceTypesFile
operator|=
operator|new
name|File
argument_list|(
name|source
operator|.
name|getParent
argument_list|()
argument_list|,
literal|"resource-types.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|source
argument_list|,
name|resourceTypesFile
argument_list|)
expr_stmt|;
name|CapacityScheduler
name|cs
init|=
operator|new
name|CapacityScheduler
argument_list|()
decl_stmt|;
name|CapacityScheduler
name|spyCS
init|=
name|spy
argument_list|(
name|cs
argument_list|)
decl_stmt|;
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|(
name|CapacitySchedulerConfiguration
operator|)
name|TestUtils
operator|.
name|getConfigurationWithMultipleQueues
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|csConf
operator|.
name|setClass
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|RESOURCE_CALCULATOR_CLASS
argument_list|,
name|DominantResourceCalculator
operator|.
name|class
argument_list|,
name|ResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|spyCS
operator|.
name|setConf
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|RMNodeLabelsManager
name|nodeLabelsManager
init|=
operator|new
name|NullRMNodeLabelsManager
argument_list|()
decl_stmt|;
name|nodeLabelsManager
operator|.
name|init
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|PlacementManager
name|pm
init|=
operator|new
name|PlacementManager
argument_list|()
decl_stmt|;
name|RMContext
name|mockContext
init|=
name|mock
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getConfigurationProvider
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|LocalConfigurationProvider
argument_list|()
argument_list|)
expr_stmt|;
name|mockContext
operator|.
name|setNodeLabelManager
argument_list|(
name|nodeLabelsManager
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getNodeLabelManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nodeLabelsManager
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockContext
operator|.
name|getQueuePlacementManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pm
argument_list|)
expr_stmt|;
name|spyCS
operator|.
name|setRMContext
argument_list|(
name|mockContext
argument_list|)
expr_stmt|;
name|spyCS
operator|.
name|init
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
comment|// Ensure the method can get custom resource type from
comment|// CapacitySchedulerConfiguration
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|0
argument_list|,
name|ResourceUtils
operator|.
name|fetchMaximumAllocationFromConfig
argument_list|(
name|spyCS
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|getResourceValue
argument_list|(
literal|"yarn.io/gpu"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure custom resource type exists in queue's maximumAllocation
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|0
argument_list|,
name|spyCS
operator|.
name|getMaximumResourceCapability
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getResourceValue
argument_list|(
literal|"yarn.io/gpu"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

