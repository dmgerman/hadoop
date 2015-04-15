begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
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
name|NodeId
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
name|api
operator|.
name|protocolrecords
operator|.
name|NMContainerStatus
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
name|TestRMRestart
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
name|recovery
operator|.
name|MemoryRMStateStore
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
name|RMContainer
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
name|AbstractYarnScheduler
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
name|SchedulerApplicationAttempt
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_class
DECL|class|TestWorkPreservingRMRestartForNodeLabel
specifier|public
class|class
name|TestWorkPreservingRMRestartForNodeLabel
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|GB
specifier|private
specifier|static
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
comment|// 1024 MB
DECL|field|mgr
name|RMNodeLabelsManager
name|mgr
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
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WORK_PRESERVING_RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_STORE
argument_list|,
name|MemoryRMStateStore
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|toSet (E... elements)
specifier|private
parameter_list|<
name|E
parameter_list|>
name|Set
argument_list|<
name|E
argument_list|>
name|toSet
parameter_list|(
name|E
modifier|...
name|elements
parameter_list|)
block|{
name|Set
argument_list|<
name|E
argument_list|>
name|set
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|elements
argument_list|)
decl_stmt|;
return|return
name|set
return|;
block|}
DECL|method|checkRMContainerLabelExpression (ContainerId containerId, MockRM rm, String labelExpression)
specifier|private
name|void
name|checkRMContainerLabelExpression
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|MockRM
name|rm
parameter_list|,
name|String
name|labelExpression
parameter_list|)
block|{
name|RMContainer
name|container
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|getRMContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Cannot find RMContainer="
operator|+
name|containerId
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|labelExpression
argument_list|,
name|container
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|waitForNumContainersToRecover (int num, MockRM rm, ApplicationAttemptId attemptId)
specifier|public
specifier|static
name|void
name|waitForNumContainersToRecover
parameter_list|(
name|int
name|num
parameter_list|,
name|MockRM
name|rm
parameter_list|,
name|ApplicationAttemptId
name|attemptId
parameter_list|)
throws|throws
name|Exception
block|{
name|AbstractYarnScheduler
name|scheduler
init|=
operator|(
name|AbstractYarnScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|SchedulerApplicationAttempt
name|attempt
init|=
name|scheduler
operator|.
name|getApplicationAttempt
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
while|while
condition|(
name|attempt
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wait for scheduler attempt "
operator|+
name|attemptId
operator|+
literal|" to be created"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|attempt
operator|=
name|scheduler
operator|.
name|getApplicationAttempt
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|attempt
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|num
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wait for "
operator|+
name|num
operator|+
literal|" containers to recover. currently: "
operator|+
name|attempt
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkAppResourceUsage (String partition, ApplicationId appId, MockRM rm, int expectedMemUsage)
specifier|private
name|void
name|checkAppResourceUsage
parameter_list|(
name|String
name|partition
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|MockRM
name|rm
parameter_list|,
name|int
name|expectedMemUsage
parameter_list|)
block|{
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
name|FiCaSchedulerApp
name|app
init|=
name|cs
operator|.
name|getSchedulerApplications
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedMemUsage
argument_list|,
name|app
operator|.
name|getAppAttemptResourceUsage
argument_list|()
operator|.
name|getUsed
argument_list|(
name|partition
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkQueueResourceUsage (String partition, String queueName, MockRM rm, int expectedMemUsage)
specifier|private
name|void
name|checkQueueResourceUsage
parameter_list|(
name|String
name|partition
parameter_list|,
name|String
name|queueName
parameter_list|,
name|MockRM
name|rm
parameter_list|,
name|int
name|expectedMemUsage
parameter_list|)
block|{
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
name|CSQueue
name|queue
init|=
name|cs
operator|.
name|getQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedMemUsage
argument_list|,
name|queue
operator|.
name|getQueueResourceUsage
argument_list|()
operator|.
name|getUsed
argument_list|(
name|partition
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWorkPreservingRestartForNodeLabel ()
specifier|public
name|void
name|testWorkPreservingRestartForNodeLabel
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This test is pretty much similar to testContainerAllocateWithLabel.
comment|// Difference is, this test doesn't specify label expression in ResourceRequest,
comment|// instead, it uses default queue label expression
comment|// set node -> label
name|mgr
operator|.
name|addToCluserNodeLabels
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addLabelsToNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h1"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h2"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"y"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|MemoryRMStateStore
name|memStore
init|=
operator|new
name|MemoryRMStateStore
argument_list|()
decl_stmt|;
name|memStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|=
name|TestUtils
operator|.
name|getConfigurationWithDefaultQueueLabels
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// inject node label manager
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RMNodeLabelsManager
name|createNodeLabelManager
parameter_list|()
block|{
return|return
name|mgr
return|;
block|}
block|}
decl_stmt|;
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|setNodeLabelManager
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
comment|// label = x
name|MockNM
name|nm2
init|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h2:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
comment|// label = y
name|MockNM
name|nm3
init|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h3:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
comment|// label =<empty>
name|ContainerId
name|containerId
decl_stmt|;
comment|// launch an app to queue a1 (label = x), and check all container will
comment|// be allocated in h1
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"a1"
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
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
comment|// request a container.
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|1024
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|containerId
operator|=
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
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId
argument_list|,
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
literal|10
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
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
argument_list|,
name|rm1
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// launch an app to queue b1 (label = y), and check all container will
comment|// be allocated in h2
name|RMApp
name|app2
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"b1"
argument_list|)
decl_stmt|;
name|MockAM
name|am2
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app2
argument_list|,
name|rm1
argument_list|,
name|nm2
argument_list|)
decl_stmt|;
comment|// request a container.
name|am2
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|1024
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|containerId
operator|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm2
argument_list|,
name|containerId
argument_list|,
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
literal|10
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
comment|// launch an app to queue c1 (label = ""), and check all container will
comment|// be allocated in h3
name|RMApp
name|app3
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"c1"
argument_list|)
decl_stmt|;
name|MockAM
name|am3
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app3
argument_list|,
name|rm1
argument_list|,
name|nm3
argument_list|)
decl_stmt|;
comment|// request a container.
name|am3
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|1024
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|containerId
operator|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am3
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm3
argument_list|,
name|containerId
argument_list|,
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
literal|10
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am3
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am3
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Re-start RM
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
name|mgr
operator|.
name|addToCluserNodeLabels
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addLabelsToNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h1"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h2"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"y"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|MockRM
name|rm2
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RMNodeLabelsManager
name|createNodeLabelManager
parameter_list|()
block|{
return|return
name|mgr
return|;
block|}
block|}
decl_stmt|;
name|rm2
operator|.
name|start
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|setResourceTrackerService
argument_list|(
name|rm2
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|setResourceTrackerService
argument_list|(
name|rm2
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
expr_stmt|;
name|nm3
operator|.
name|setResourceTrackerService
argument_list|(
name|rm2
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
expr_stmt|;
comment|// recover app
name|NMContainerStatus
name|app1c1
init|=
name|TestRMRestart
operator|.
name|createNMContainerStatus
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
name|NMContainerStatus
name|app1c2
init|=
name|TestRMRestart
operator|.
name|createNMContainerStatus
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|registerNode
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|app1c1
argument_list|,
name|app1c2
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|waitForNumContainersToRecover
argument_list|(
literal|2
argument_list|,
name|rm2
argument_list|,
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
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
argument_list|,
name|rm1
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|NMContainerStatus
name|app2c1
init|=
name|TestRMRestart
operator|.
name|createNMContainerStatus
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|"y"
argument_list|)
decl_stmt|;
name|NMContainerStatus
name|app2c2
init|=
name|TestRMRestart
operator|.
name|createNMContainerStatus
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|"y"
argument_list|)
decl_stmt|;
name|nm2
operator|.
name|registerNode
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|app2c1
argument_list|,
name|app2c2
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|waitForNumContainersToRecover
argument_list|(
literal|2
argument_list|,
name|rm2
argument_list|,
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|NMContainerStatus
name|app3c1
init|=
name|TestRMRestart
operator|.
name|createNMContainerStatus
argument_list|(
name|am3
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|NMContainerStatus
name|app3c2
init|=
name|TestRMRestart
operator|.
name|createNMContainerStatus
argument_list|(
name|am3
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|nm3
operator|.
name|registerNode
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|app3c1
argument_list|,
name|app3c2
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|waitForNumContainersToRecover
argument_list|(
literal|2
argument_list|,
name|rm2
argument_list|,
name|am3
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am3
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|checkRMContainerLabelExpression
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|am3
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
name|rm1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Check recovered resource usage
name|checkAppResourceUsage
argument_list|(
literal|"x"
argument_list|,
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkAppResourceUsage
argument_list|(
literal|"y"
argument_list|,
name|app2
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkAppResourceUsage
argument_list|(
literal|""
argument_list|,
name|app3
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|"x"
argument_list|,
literal|"a1"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|"y"
argument_list|,
literal|"b1"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|""
argument_list|,
literal|"c1"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|"x"
argument_list|,
literal|"a"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|"y"
argument_list|,
literal|"b"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|""
argument_list|,
literal|"c"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|"x"
argument_list|,
literal|"root"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|"y"
argument_list|,
literal|"root"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|checkQueueResourceUsage
argument_list|(
literal|""
argument_list|,
literal|"root"
argument_list|,
name|rm1
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|close
argument_list|()
expr_stmt|;
name|rm2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

