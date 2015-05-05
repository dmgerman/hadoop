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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
DECL|class|TestCapacitySchedulerNodeLabelUpdate
specifier|public
class|class
name|TestCapacitySchedulerNodeLabelUpdate
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
DECL|method|getConfigurationWithQueueLabels (Configuration config)
specifier|private
name|Configuration
name|getConfigurationWithQueueLabels
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|(
name|config
argument_list|)
decl_stmt|;
comment|// Define top-level queues
name|conf
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
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacityByLabel
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|"x"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacityByLabel
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|"y"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacityByLabel
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|"z"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
specifier|final
name|String
name|A
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setAccessibleNodeLabels
argument_list|(
name|A
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacityByLabel
argument_list|(
name|A
argument_list|,
literal|"x"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacityByLabel
argument_list|(
name|A
argument_list|,
literal|"y"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacityByLabel
argument_list|(
name|A
argument_list|,
literal|"z"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|toSet (String... elements)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|toSet
parameter_list|(
name|String
modifier|...
name|elements
parameter_list|)
block|{
name|Set
argument_list|<
name|String
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
DECL|method|checkUsedResource (MockRM rm, String queueName, int memory)
specifier|private
name|void
name|checkUsedResource
parameter_list|(
name|MockRM
name|rm
parameter_list|,
name|String
name|queueName
parameter_list|,
name|int
name|memory
parameter_list|)
block|{
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
name|queueName
argument_list|,
name|memory
argument_list|,
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
expr_stmt|;
block|}
DECL|method|checkUsedResource (MockRM rm, String queueName, int memory, String label)
specifier|private
name|void
name|checkUsedResource
parameter_list|(
name|MockRM
name|rm
parameter_list|,
name|String
name|queueName
parameter_list|,
name|int
name|memory
parameter_list|,
name|String
name|label
parameter_list|)
block|{
name|CapacityScheduler
name|scheduler
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
name|scheduler
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
name|memory
argument_list|,
name|queue
operator|.
name|getQueueResourceUsage
argument_list|()
operator|.
name|getUsed
argument_list|(
name|label
argument_list|)
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNodeUpdate ()
specifier|public
name|void
name|testNodeUpdate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// set node -> label
name|mgr
operator|.
name|addToCluserNodeLabelsWithDefaultExclusivity
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
comment|// set mapping:
comment|// h1 -> x
comment|// h2 -> y
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
comment|// inject node label manager
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|(
name|getConfigurationWithQueueLabels
argument_list|(
name|conf
argument_list|)
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
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|setNodeLabelManager
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
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
literal|8000
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h2:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
name|MockNM
name|nm3
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h3:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
decl_stmt|;
comment|// launch an app to queue a1 (label = x), and check all container will
comment|// be allocated in h1
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
name|GB
argument_list|,
literal|"app"
argument_list|,
literal|"user"
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
name|nm3
argument_list|)
decl_stmt|;
comment|// request a container.
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
name|GB
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
literal|"x"
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
name|rm
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
comment|// check used resource:
comment|// queue-a used x=1G, ""=1G
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|1024
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// change h1's label to z, container should be killed
name|mgr
operator|.
name|replaceLabelsOnNode
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
literal|"z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|containerId
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
literal|10
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
comment|// check used resource:
comment|// queue-a used x=0G, ""=1G ("" not changed)
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// request a container with label = y
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
name|GB
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
literal|"y"
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
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm
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
comment|// check used resource:
comment|// queue-a used y=1G, ""=1G
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|1024
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// change h2's label to no label, container should be killed
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h2"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|CommonNodeLabelsManager
operator|.
name|EMPTY_STRING_SET
argument_list|)
argument_list|)
expr_stmt|;
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
name|containerId
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
literal|10
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
comment|// check used resource:
comment|// queue-a used x=0G, y=0G, ""=1G ("" not changed)
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|1024
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
literal|1
argument_list|)
expr_stmt|;
comment|// change h3's label to z, AM container should be killed
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"h3"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|toSet
argument_list|(
literal|"z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|containerId
argument_list|,
name|RMContainerState
operator|.
name|KILLED
argument_list|,
literal|10
operator|*
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
comment|// check used resource:
comment|// queue-a used x=0G, y=0G, ""=1G ("" not changed)
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|checkUsedResource
argument_list|(
name|rm
argument_list|,
literal|"a"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|rm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

