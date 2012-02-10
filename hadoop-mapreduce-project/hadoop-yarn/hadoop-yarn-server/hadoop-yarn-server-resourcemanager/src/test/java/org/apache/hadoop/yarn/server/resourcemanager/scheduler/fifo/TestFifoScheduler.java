begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo
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
name|fifo
package|;
end_package

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
name|junit
operator|.
name|framework
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
name|net
operator|.
name|NetworkTopology
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
name|QueueInfo
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
name|event
operator|.
name|AsyncDispatcher
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
name|Application
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
name|RMContextImpl
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
name|ResourceManager
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
name|Task
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
name|Store
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
name|StoreFactory
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
name|Resources
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
name|resourcetracker
operator|.
name|InlineDispatcher
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
name|QueueMetrics
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
name|AppAddedSchedulerEvent
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
name|SchedulerEvent
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
name|BuilderUtils
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestFifoScheduler
specifier|public
class|class
name|TestFifoScheduler
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
name|TestFifoScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceManager
specifier|private
name|ResourceManager
name|resourceManager
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
name|Store
name|store
init|=
name|StoreFactory
operator|.
name|getStore
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|resourceManager
operator|=
operator|new
name|ResourceManager
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
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
block|{   }
specifier|private
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
name|NodeManager
DECL|method|registerNode (String hostName, int containerManagerPort, int nmHttpPort, String rackName, int memory)
name|registerNode
parameter_list|(
name|String
name|hostName
parameter_list|,
name|int
name|containerManagerPort
parameter_list|,
name|int
name|nmHttpPort
parameter_list|,
name|String
name|rackName
parameter_list|,
name|int
name|memory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
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
name|NodeManager
argument_list|(
name|hostName
argument_list|,
name|containerManagerPort
argument_list|,
name|nmHttpPort
argument_list|,
name|rackName
argument_list|,
name|memory
argument_list|,
name|resourceManager
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|,
name|resourceManager
operator|.
name|getRMContext
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testFifoSchedulerCapacityWhenNoNMs ()
specifier|public
name|void
name|testFifoSchedulerCapacityWhenNoNMs
parameter_list|()
block|{
name|FifoScheduler
name|scheduler
init|=
operator|new
name|FifoScheduler
argument_list|()
decl_stmt|;
name|QueueInfo
name|queueInfo
init|=
name|scheduler
operator|.
name|getQueueInfo
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0.0f
argument_list|,
name|queueInfo
operator|.
name|getCurrentCapacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppAttemptMetrics ()
specifier|public
name|void
name|testAppAttemptMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|AsyncDispatcher
name|dispatcher
init|=
operator|new
name|InlineDispatcher
argument_list|()
decl_stmt|;
name|RMContext
name|rmContext
init|=
operator|new
name|RMContextImpl
argument_list|(
literal|null
argument_list|,
name|dispatcher
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|FifoScheduler
name|schedular
init|=
operator|new
name|FifoScheduler
argument_list|()
decl_stmt|;
name|schedular
operator|.
name|reinitialize
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|null
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|200
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|SchedulerEvent
name|event
init|=
operator|new
name|AppAddedSchedulerEvent
argument_list|(
name|appAttemptId
argument_list|,
literal|"queue"
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
name|schedular
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|appAttemptId
operator|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|event
operator|=
operator|new
name|AppAddedSchedulerEvent
argument_list|(
name|appAttemptId
argument_list|,
literal|"queue"
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|schedular
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|QueueMetrics
name|metrics
init|=
name|schedular
operator|.
name|getRootQueueMetrics
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|metrics
operator|.
name|getAppsSubmitted
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//  @Test
DECL|method|testFifoScheduler ()
specifier|public
name|void
name|testFifoScheduler
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testFifoScheduler ---"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
comment|// Register node1
name|String
name|host_0
init|=
literal|"host_0"
decl_stmt|;
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
name|NodeManager
name|nm_0
init|=
name|registerNode
argument_list|(
name|host_0
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// Register node2
name|String
name|host_1
init|=
literal|"host_1"
decl_stmt|;
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
name|NodeManager
name|nm_1
init|=
name|registerNode
argument_list|(
name|host_1
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
literal|2
operator|*
name|GB
argument_list|)
decl_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// ResourceRequest priorities
name|Priority
name|priority_0
init|=
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
name|Priority
operator|.
name|create
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Priority
name|priority_1
init|=
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
name|Priority
operator|.
name|create
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Submit an application
name|Application
name|application_0
init|=
operator|new
name|Application
argument_list|(
literal|"user_0"
argument_list|,
name|resourceManager
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|submit
argument_list|()
expr_stmt|;
name|application_0
operator|.
name|addNodeManager
argument_list|(
name|host_0
argument_list|,
literal|1234
argument_list|,
name|nm_0
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|addNodeManager
argument_list|(
name|host_1
argument_list|,
literal|1234
argument_list|,
name|nm_1
argument_list|)
expr_stmt|;
name|Resource
name|capability_0_0
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|GB
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority_1
argument_list|,
name|capability_0_0
argument_list|)
expr_stmt|;
name|Resource
name|capability_0_1
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority_0
argument_list|,
name|capability_0_1
argument_list|)
expr_stmt|;
name|Task
name|task_0_0
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|,
name|host_1
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_0
argument_list|)
expr_stmt|;
comment|// Submit another application
name|Application
name|application_1
init|=
operator|new
name|Application
argument_list|(
literal|"user_1"
argument_list|,
name|resourceManager
argument_list|)
decl_stmt|;
name|application_1
operator|.
name|submit
argument_list|()
expr_stmt|;
name|application_1
operator|.
name|addNodeManager
argument_list|(
name|host_0
argument_list|,
literal|1234
argument_list|,
name|nm_0
argument_list|)
expr_stmt|;
name|application_1
operator|.
name|addNodeManager
argument_list|(
name|host_1
argument_list|,
literal|1234
argument_list|,
name|nm_1
argument_list|)
expr_stmt|;
name|Resource
name|capability_1_0
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|3
operator|*
name|GB
argument_list|)
decl_stmt|;
name|application_1
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority_1
argument_list|,
name|capability_1_0
argument_list|)
expr_stmt|;
name|Resource
name|capability_1_1
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|4
operator|*
name|GB
argument_list|)
decl_stmt|;
name|application_1
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority_0
argument_list|,
name|capability_1_1
argument_list|)
expr_stmt|;
name|Task
name|task_1_0
init|=
operator|new
name|Task
argument_list|(
name|application_1
argument_list|,
name|priority_1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|,
name|host_1
block|}
argument_list|)
decl_stmt|;
name|application_1
operator|.
name|addTask
argument_list|(
name|task_1_0
argument_list|)
expr_stmt|;
comment|// Send resource requests to the scheduler
name|LOG
operator|.
name|info
argument_list|(
literal|"Send resource requests to the scheduler"
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|LOG
operator|.
name|info
argument_list|(
literal|"Send a heartbeat to kick the tires on the Scheduler... "
operator|+
literal|"nm0 -> task_0_0 and task_1_0 allocated, used=4G "
operator|+
literal|"nm1 -> nothing allocated"
argument_list|)
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// task_0_0 and task_1_0 allocated, used=4G
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// nothing allocated
comment|// Get allocations from the scheduler
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// task_0_0
name|checkApplicationResourceUsage
argument_list|(
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// task_1_0
name|checkApplicationResourceUsage
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkNodeResourceUsage
argument_list|(
literal|4
operator|*
name|GB
argument_list|,
name|nm_0
argument_list|)
expr_stmt|;
comment|// task_0_0 (1G) and task_1_0 (3G)
name|checkNodeResourceUsage
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|nm_1
argument_list|)
expr_stmt|;
comment|// no tasks, 2G available
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding new tasks..."
argument_list|)
expr_stmt|;
name|Task
name|task_1_1
init|=
operator|new
name|Task
argument_list|(
name|application_1
argument_list|,
name|priority_1
argument_list|,
operator|new
name|String
index|[]
block|{
name|RMNode
operator|.
name|ANY
block|}
argument_list|)
decl_stmt|;
name|application_1
operator|.
name|addTask
argument_list|(
name|task_1_1
argument_list|)
expr_stmt|;
name|Task
name|task_1_2
init|=
operator|new
name|Task
argument_list|(
name|application_1
argument_list|,
name|priority_1
argument_list|,
operator|new
name|String
index|[]
block|{
name|RMNode
operator|.
name|ANY
block|}
argument_list|)
decl_stmt|;
name|application_1
operator|.
name|addTask
argument_list|(
name|task_1_2
argument_list|)
expr_stmt|;
name|Task
name|task_1_3
init|=
operator|new
name|Task
argument_list|(
name|application_1
argument_list|,
name|priority_0
argument_list|,
operator|new
name|String
index|[]
block|{
name|RMNode
operator|.
name|ANY
block|}
argument_list|)
decl_stmt|;
name|application_1
operator|.
name|addTask
argument_list|(
name|task_1_3
argument_list|)
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|Task
name|task_0_1
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|,
name|host_1
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_1
argument_list|)
expr_stmt|;
name|Task
name|task_0_2
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host_0
block|,
name|host_1
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_2
argument_list|)
expr_stmt|;
name|Task
name|task_0_3
init|=
operator|new
name|Task
argument_list|(
name|application_0
argument_list|,
name|priority_0
argument_list|,
operator|new
name|String
index|[]
block|{
name|RMNode
operator|.
name|ANY
block|}
argument_list|)
decl_stmt|;
name|application_0
operator|.
name|addTask
argument_list|(
name|task_0_3
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending hb from "
operator|+
name|nm_0
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// nothing new, used=4G
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending hb from "
operator|+
name|nm_1
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// task_0_3, used=2G
comment|// Get allocations from the scheduler
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to allocate..."
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkNodeResourceUsage
argument_list|(
literal|4
operator|*
name|GB
argument_list|,
name|nm_0
argument_list|)
expr_stmt|;
name|checkNodeResourceUsage
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|nm_1
argument_list|)
expr_stmt|;
comment|// Complete tasks
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up task_0_0"
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|finishTask
argument_list|(
name|task_0_0
argument_list|)
expr_stmt|;
comment|// Now task_0_1
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
name|checkNodeResourceUsage
argument_list|(
literal|4
operator|*
name|GB
argument_list|,
name|nm_0
argument_list|)
expr_stmt|;
name|checkNodeResourceUsage
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|nm_1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up task_1_0"
argument_list|)
expr_stmt|;
name|application_1
operator|.
name|finishTask
argument_list|(
name|task_1_0
argument_list|)
expr_stmt|;
comment|// Now task_0_2
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// final overcommit for app0 caused here
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// final overcommit for app0 occurs here
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|4
operator|*
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
comment|//checkNodeResourceUsage(1*GB, nm_0);  // final over-commit -> rm.node->1G, test.node=2G
name|checkNodeResourceUsage
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|nm_1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up task_0_3"
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|finishTask
argument_list|(
name|task_0_3
argument_list|)
expr_stmt|;
comment|// No more
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
comment|//checkNodeResourceUsage(2*GB, nm_0);  // final over-commit, rm.node->1G, test.node->2G
name|checkNodeResourceUsage
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|nm_1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up task_0_1"
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|finishTask
argument_list|(
name|task_0_1
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up task_0_2"
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|finishTask
argument_list|(
name|task_0_2
argument_list|)
expr_stmt|;
comment|// now task_1_3 can go!
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|4
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up task_1_3"
argument_list|)
expr_stmt|;
name|application_1
operator|.
name|finishTask
argument_list|(
name|task_1_3
argument_list|)
expr_stmt|;
comment|// now task_1_1
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up task_1_1"
argument_list|)
expr_stmt|;
name|application_1
operator|.
name|finishTask
argument_list|(
name|task_1_1
argument_list|)
expr_stmt|;
name|application_0
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|application_1
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm_0
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm_1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|application_0
argument_list|)
expr_stmt|;
name|checkApplicationResourceUsage
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
name|application_1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"--- END: testFifoScheduler ---"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkApplicationResourceUsage (int expected, Application application)
specifier|private
name|void
name|checkApplicationResourceUsage
parameter_list|(
name|int
name|expected
parameter_list|,
name|Application
name|application
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|application
operator|.
name|getUsedResources
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkNodeResourceUsage (int expected, org.apache.hadoop.yarn.server.resourcemanager.NodeManager node)
specifier|private
name|void
name|checkNodeResourceUsage
parameter_list|(
name|int
name|expected
parameter_list|,
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
name|NodeManager
name|node
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|node
operator|.
name|getUsed
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|checkResourceUsage
argument_list|()
expr_stmt|;
block|}
DECL|method|main (String[] arg)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|arg
parameter_list|)
throws|throws
name|Exception
block|{
name|TestFifoScheduler
name|t
init|=
operator|new
name|TestFifoScheduler
argument_list|()
decl_stmt|;
name|t
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|t
operator|.
name|testFifoScheduler
argument_list|()
expr_stmt|;
name|t
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

