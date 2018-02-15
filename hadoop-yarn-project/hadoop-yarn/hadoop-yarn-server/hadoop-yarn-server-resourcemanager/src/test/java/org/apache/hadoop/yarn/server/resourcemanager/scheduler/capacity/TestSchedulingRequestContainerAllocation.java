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
name|ResourceSizing
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
DECL|class|TestSchedulingRequestContainerAllocation
specifier|public
class|class
name|TestSchedulingRequestContainerAllocation
block|{
DECL|field|GB
specifier|private
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
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
name|Test
DECL|method|testIntraAppAntiAffinity ()
specifier|public
name|void
name|testIntraAppAntiAffinity
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|csConf
init|=
name|TestUtils
operator|.
name|getConfigurationWithMultipleQueues
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|csConf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_PLACEMENT_CONSTRAINTS_HANDLER
argument_list|,
name|YarnConfiguration
operator|.
name|SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER
argument_list|)
expr_stmt|;
comment|// inject node label manager
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|csConf
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
comment|// 4 NMs.
name|MockNM
index|[]
name|nms
init|=
operator|new
name|MockNM
index|[
literal|4
index|]
decl_stmt|;
name|RMNode
index|[]
name|rmNodes
init|=
operator|new
name|RMNode
index|[
literal|4
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|nms
index|[
name|i
index|]
operator|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"192.168.0."
operator|+
name|i
operator|+
literal|":1234"
argument_list|,
literal|10
operator|*
name|GB
argument_list|)
expr_stmt|;
name|rmNodes
index|[
name|i
index|]
operator|=
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nms
index|[
name|i
index|]
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// app1 -> c
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"c"
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
name|nms
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// app1 asks for 10 anti-affinity containers for the same app. It should
comment|// only get 4 containers allocated because we only have 4 nodes.
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|10
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
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1L
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"mapper"
argument_list|)
argument_list|,
literal|"mapper"
argument_list|)
expr_stmt|;
name|CapacityScheduler
name|cs
init|=
operator|(
name|CapacityScheduler
operator|)
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|4
condition|;
name|j
operator|++
control|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNodes
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// App1 should get 5 containers allocated (1 AM + 1 node each).
name|FiCaSchedulerApp
name|schedulerApp
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|schedulerApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Similarly, app1 asks 10 anti-affinity containers at different priority,
comment|// it should be satisfied as well.
comment|// app1 asks for 10 anti-affinity containers for the same app. It should
comment|// only get 4 containers allocated because we only have 4 nodes.
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|10
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2048
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|1L
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"reducer"
argument_list|)
argument_list|,
literal|"reducer"
argument_list|)
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|4
condition|;
name|j
operator|++
control|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNodes
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// App1 should get 9 containers allocated (1 AM + 8 containers).
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|schedulerApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test anti-affinity to both of "mapper/reducer", we should only get no
comment|// container allocated
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|10
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|2048
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|1L
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"reducer2"
argument_list|)
argument_list|,
literal|"mapper"
argument_list|)
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|4
condition|;
name|j
operator|++
control|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNodes
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// App1 should get 10 containers allocated (1 AM + 9 containers).
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|schedulerApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntraAppAntiAffinityWithMultipleTags ()
specifier|public
name|void
name|testIntraAppAntiAffinityWithMultipleTags
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|csConf
init|=
name|TestUtils
operator|.
name|getConfigurationWithMultipleQueues
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|csConf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_PLACEMENT_CONSTRAINTS_HANDLER
argument_list|,
name|YarnConfiguration
operator|.
name|SCHEDULER_RM_PLACEMENT_CONSTRAINTS_HANDLER
argument_list|)
expr_stmt|;
comment|// inject node label manager
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|csConf
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
comment|// 4 NMs.
name|MockNM
index|[]
name|nms
init|=
operator|new
name|MockNM
index|[
literal|4
index|]
decl_stmt|;
name|RMNode
index|[]
name|rmNodes
init|=
operator|new
name|RMNode
index|[
literal|4
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|nms
index|[
name|i
index|]
operator|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"192.168.0."
operator|+
name|i
operator|+
literal|":1234"
argument_list|,
literal|10
operator|*
name|GB
argument_list|)
expr_stmt|;
name|rmNodes
index|[
name|i
index|]
operator|=
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nms
index|[
name|i
index|]
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// app1 -> c
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"c"
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
name|nms
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// app1 asks for 2 anti-affinity containers for the same app.
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|2
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
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1L
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"tag_1_1"
argument_list|,
literal|"tag_1_2"
argument_list|)
argument_list|,
literal|"tag_1_1"
argument_list|,
literal|"tag_1_2"
argument_list|)
expr_stmt|;
name|CapacityScheduler
name|cs
init|=
operator|(
name|CapacityScheduler
operator|)
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|4
condition|;
name|j
operator|++
control|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNodes
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// App1 should get 3 containers allocated (1 AM + 2 task).
name|FiCaSchedulerApp
name|schedulerApp
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|schedulerApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// app1 asks for 1 anti-affinity containers for the same app. anti-affinity
comment|// to tag_1_1/tag_1_2. With allocation_tag = tag_2_1/tag_2_2
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|1
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
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|1L
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"tag_2_1"
argument_list|,
literal|"tag_2_2"
argument_list|)
argument_list|,
literal|"tag_1_1"
argument_list|,
literal|"tag_1_2"
argument_list|)
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|4
condition|;
name|j
operator|++
control|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNodes
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// App1 should get 4 containers allocated (1 AM + 2 task (first request) +
comment|// 1 task (2nd request).
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|schedulerApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// app1 asks for 10 anti-affinity containers for the same app. anti-affinity
comment|// to tag_1_1/tag_1_2/tag_2_1/tag_2_2. With allocation_tag = tag_3
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|1
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
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|3
argument_list|)
argument_list|,
literal|1L
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"tag_3"
argument_list|)
argument_list|,
literal|"tag_1_1"
argument_list|,
literal|"tag_1_2"
argument_list|,
literal|"tag_2_1"
argument_list|,
literal|"tag_2_2"
argument_list|)
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|4
condition|;
name|j
operator|++
control|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNodes
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// App1 should get 1 more containers allocated
comment|// 1 AM + 2 task (first request) + 1 task (2nd request) +
comment|// 1 task (3rd request)
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|schedulerApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSchedulingRequestDisabledByDefault ()
specifier|public
name|void
name|testSchedulingRequestDisabledByDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|csConf
init|=
name|TestUtils
operator|.
name|getConfigurationWithMultipleQueues
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
comment|// inject node label manager
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|csConf
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
comment|// 4 NMs.
name|MockNM
index|[]
name|nms
init|=
operator|new
name|MockNM
index|[
literal|4
index|]
decl_stmt|;
name|RMNode
index|[]
name|rmNodes
init|=
operator|new
name|RMNode
index|[
literal|4
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|nms
index|[
name|i
index|]
operator|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"192.168.0."
operator|+
name|i
operator|+
literal|":1234"
argument_list|,
literal|10
operator|*
name|GB
argument_list|)
expr_stmt|;
name|rmNodes
index|[
name|i
index|]
operator|=
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nms
index|[
name|i
index|]
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// app1 -> c
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"c"
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
name|nms
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|// app1 asks for 2 anti-affinity containers for the same app.
name|boolean
name|caughtException
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// Since feature is disabled by default, we should expect exception.
name|am1
operator|.
name|allocateIntraAppAntiAffinity
argument_list|(
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
literal|2
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
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1L
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"tag_1_1"
argument_list|,
literal|"tag_1_2"
argument_list|)
argument_list|,
literal|"tag_1_1"
argument_list|,
literal|"tag_1_2"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|caughtException
operator|=
literal|true
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|caughtException
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

