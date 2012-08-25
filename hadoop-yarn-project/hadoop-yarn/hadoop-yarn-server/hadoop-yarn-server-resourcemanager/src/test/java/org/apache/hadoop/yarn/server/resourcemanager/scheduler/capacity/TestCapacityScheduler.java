begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|List
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
name|MockNodes
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
name|event
operator|.
name|NodeAddedSchedulerEvent
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
name|NodeRemovedSchedulerEvent
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
name|security
operator|.
name|RMContainerTokenSecretManager
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
DECL|class|TestCapacityScheduler
specifier|public
class|class
name|TestCapacityScheduler
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
name|TestCapacityScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|GB
specifier|private
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|field|A
specifier|private
specifier|static
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
DECL|field|B
specifier|private
specifier|static
specifier|final
name|String
name|B
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".b"
decl_stmt|;
DECL|field|A1
specifier|private
specifier|static
specifier|final
name|String
name|A1
init|=
name|A
operator|+
literal|".a1"
decl_stmt|;
DECL|field|A2
specifier|private
specifier|static
specifier|final
name|String
name|A2
init|=
name|A
operator|+
literal|".a2"
decl_stmt|;
DECL|field|B1
specifier|private
specifier|static
specifier|final
name|String
name|B1
init|=
name|B
operator|+
literal|".b1"
decl_stmt|;
DECL|field|B2
specifier|private
specifier|static
specifier|final
name|String
name|B2
init|=
name|B
operator|+
literal|".b2"
decl_stmt|;
DECL|field|B3
specifier|private
specifier|static
specifier|final
name|String
name|B3
init|=
name|B
operator|+
literal|".b3"
decl_stmt|;
DECL|field|A_CAPACITY
specifier|private
specifier|static
name|float
name|A_CAPACITY
init|=
literal|10.5f
decl_stmt|;
DECL|field|B_CAPACITY
specifier|private
specifier|static
name|float
name|B_CAPACITY
init|=
literal|89.5f
decl_stmt|;
DECL|field|A1_CAPACITY
specifier|private
specifier|static
name|float
name|A1_CAPACITY
init|=
literal|30
decl_stmt|;
DECL|field|A2_CAPACITY
specifier|private
specifier|static
name|float
name|A2_CAPACITY
init|=
literal|70
decl_stmt|;
DECL|field|B1_CAPACITY
specifier|private
specifier|static
name|float
name|B1_CAPACITY
init|=
literal|50
decl_stmt|;
DECL|field|B2_CAPACITY
specifier|private
specifier|static
name|float
name|B2_CAPACITY
init|=
literal|30
decl_stmt|;
DECL|field|B3_CAPACITY
specifier|private
specifier|static
name|float
name|B3_CAPACITY
init|=
literal|20
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
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|csConf
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
name|resourceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AsyncDispatcher
operator|)
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getDispatcher
argument_list|()
operator|)
operator|.
name|start
argument_list|()
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
name|resourceManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
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
DECL|method|registerNode (String hostName, int containerManagerPort, int httpPort, String rackName, int memory)
name|registerNode
parameter_list|(
name|String
name|hostName
parameter_list|,
name|int
name|containerManagerPort
parameter_list|,
name|int
name|httpPort
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
name|httpPort
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
comment|//  @Test
DECL|method|testCapacityScheduler ()
specifier|public
name|void
name|testCapacityScheduler
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testCapacityScheduler ---"
argument_list|)
expr_stmt|;
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
literal|"a1"
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
literal|1
operator|*
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
literal|"b2"
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
literal|2
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
literal|"Kick!"
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
literal|1
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
name|task_1_1
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
name|priority_0
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
literal|1
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
literal|5
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
name|LOG
operator|.
name|info
argument_list|(
literal|"--- END: testCapacityScheduler ---"
argument_list|)
expr_stmt|;
block|}
DECL|method|setupQueueConfiguration (CapacitySchedulerConfiguration conf)
specifier|private
name|void
name|setupQueueConfiguration
parameter_list|(
name|CapacitySchedulerConfiguration
name|conf
parameter_list|)
block|{
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
block|,
literal|"b"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A
argument_list|,
name|A_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B
argument_list|,
name|B_CAPACITY
argument_list|)
expr_stmt|;
comment|// Define 2nd-level queues
name|conf
operator|.
name|setQueues
argument_list|(
name|A
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A1
argument_list|,
name|A1_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setUserLimitFactor
argument_list|(
name|A1
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A2
argument_list|,
name|A2_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setUserLimitFactor
argument_list|(
name|A2
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|B
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b1"
block|,
literal|"b2"
block|,
literal|"b3"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B1
argument_list|,
name|B1_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setUserLimitFactor
argument_list|(
name|B1
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B2
argument_list|,
name|B2_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setUserLimitFactor
argument_list|(
name|B2
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B3
argument_list|,
name|B3_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setUserLimitFactor
argument_list|(
name|B3
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setup top-level queues a and b"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRefreshQueues ()
specifier|public
name|void
name|testRefreshQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacityScheduler
name|cs
init|=
operator|new
name|CapacityScheduler
argument_list|()
decl_stmt|;
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|cs
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
operator|new
name|RMContextImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|RMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|checkQueueCapacities
argument_list|(
name|cs
argument_list|,
name|A_CAPACITY
argument_list|,
name|B_CAPACITY
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A
argument_list|,
literal|80f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B
argument_list|,
literal|20f
argument_list|)
expr_stmt|;
name|cs
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkQueueCapacities
argument_list|(
name|cs
argument_list|,
literal|80f
argument_list|,
literal|20f
argument_list|)
expr_stmt|;
block|}
DECL|method|checkQueueCapacities (CapacityScheduler cs, float capacityA, float capacityB)
specifier|private
name|void
name|checkQueueCapacities
parameter_list|(
name|CapacityScheduler
name|cs
parameter_list|,
name|float
name|capacityA
parameter_list|,
name|float
name|capacityB
parameter_list|)
block|{
name|CSQueue
name|rootQueue
init|=
name|cs
operator|.
name|getRootQueue
argument_list|()
decl_stmt|;
name|CSQueue
name|queueA
init|=
name|findQueue
argument_list|(
name|rootQueue
argument_list|,
name|A
argument_list|)
decl_stmt|;
name|CSQueue
name|queueB
init|=
name|findQueue
argument_list|(
name|rootQueue
argument_list|,
name|B
argument_list|)
decl_stmt|;
name|CSQueue
name|queueA1
init|=
name|findQueue
argument_list|(
name|queueA
argument_list|,
name|A1
argument_list|)
decl_stmt|;
name|CSQueue
name|queueA2
init|=
name|findQueue
argument_list|(
name|queueA
argument_list|,
name|A2
argument_list|)
decl_stmt|;
name|CSQueue
name|queueB1
init|=
name|findQueue
argument_list|(
name|queueB
argument_list|,
name|B1
argument_list|)
decl_stmt|;
name|CSQueue
name|queueB2
init|=
name|findQueue
argument_list|(
name|queueB
argument_list|,
name|B2
argument_list|)
decl_stmt|;
name|CSQueue
name|queueB3
init|=
name|findQueue
argument_list|(
name|queueB
argument_list|,
name|B3
argument_list|)
decl_stmt|;
name|float
name|capA
init|=
name|capacityA
operator|/
literal|100.0f
decl_stmt|;
name|float
name|capB
init|=
name|capacityB
operator|/
literal|100.0f
decl_stmt|;
name|checkQueueCapacity
argument_list|(
name|queueA
argument_list|,
name|capA
argument_list|,
name|capA
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|checkQueueCapacity
argument_list|(
name|queueB
argument_list|,
name|capB
argument_list|,
name|capB
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|checkQueueCapacity
argument_list|(
name|queueA1
argument_list|,
name|A1_CAPACITY
operator|/
literal|100.0f
argument_list|,
operator|(
name|A1_CAPACITY
operator|/
literal|100.0f
operator|)
operator|*
name|capA
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|checkQueueCapacity
argument_list|(
name|queueA2
argument_list|,
name|A2_CAPACITY
operator|/
literal|100.0f
argument_list|,
operator|(
name|A2_CAPACITY
operator|/
literal|100.0f
operator|)
operator|*
name|capA
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|checkQueueCapacity
argument_list|(
name|queueB1
argument_list|,
name|B1_CAPACITY
operator|/
literal|100.0f
argument_list|,
operator|(
name|B1_CAPACITY
operator|/
literal|100.0f
operator|)
operator|*
name|capB
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|checkQueueCapacity
argument_list|(
name|queueB2
argument_list|,
name|B2_CAPACITY
operator|/
literal|100.0f
argument_list|,
operator|(
name|B2_CAPACITY
operator|/
literal|100.0f
operator|)
operator|*
name|capB
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|checkQueueCapacity
argument_list|(
name|queueB3
argument_list|,
name|B3_CAPACITY
operator|/
literal|100.0f
argument_list|,
operator|(
name|B3_CAPACITY
operator|/
literal|100.0f
operator|)
operator|*
name|capB
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|checkQueueCapacity (CSQueue q, float expectedCapacity, float expectedAbsCapacity, float expectedMaxCapacity, float expectedAbsMaxCapacity)
specifier|private
name|void
name|checkQueueCapacity
parameter_list|(
name|CSQueue
name|q
parameter_list|,
name|float
name|expectedCapacity
parameter_list|,
name|float
name|expectedAbsCapacity
parameter_list|,
name|float
name|expectedMaxCapacity
parameter_list|,
name|float
name|expectedAbsMaxCapacity
parameter_list|)
block|{
specifier|final
name|float
name|epsilon
init|=
literal|1e-5f
decl_stmt|;
name|assertEquals
argument_list|(
literal|"capacity"
argument_list|,
name|expectedCapacity
argument_list|,
name|q
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|epsilon
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"absolute capacity"
argument_list|,
name|expectedAbsCapacity
argument_list|,
name|q
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|,
name|epsilon
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"maximum capacity"
argument_list|,
name|expectedMaxCapacity
argument_list|,
name|q
operator|.
name|getMaximumCapacity
argument_list|()
argument_list|,
name|epsilon
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"absolute maximum capacity"
argument_list|,
name|expectedAbsMaxCapacity
argument_list|,
name|q
operator|.
name|getAbsoluteMaximumCapacity
argument_list|()
argument_list|,
name|epsilon
argument_list|)
expr_stmt|;
block|}
DECL|method|findQueue (CSQueue root, String queuePath)
specifier|private
name|CSQueue
name|findQueue
parameter_list|(
name|CSQueue
name|root
parameter_list|,
name|String
name|queuePath
parameter_list|)
block|{
if|if
condition|(
name|root
operator|.
name|getQueuePath
argument_list|()
operator|.
name|equals
argument_list|(
name|queuePath
argument_list|)
condition|)
block|{
return|return
name|root
return|;
block|}
name|List
argument_list|<
name|CSQueue
argument_list|>
name|childQueues
init|=
name|root
operator|.
name|getChildQueues
argument_list|()
decl_stmt|;
if|if
condition|(
name|childQueues
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CSQueue
name|q
range|:
name|childQueues
control|)
block|{
if|if
condition|(
name|queuePath
operator|.
name|startsWith
argument_list|(
name|q
operator|.
name|getQueuePath
argument_list|()
argument_list|)
condition|)
block|{
name|CSQueue
name|result
init|=
name|findQueue
argument_list|(
name|q
argument_list|,
name|queuePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
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
comment|/** Test that parseQueue throws an exception when two leaf queues have the    *  same name  * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testParseQueue ()
specifier|public
name|void
name|testParseQueue
parameter_list|()
throws|throws
name|IOException
block|{
name|CapacityScheduler
name|cs
init|=
operator|new
name|CapacityScheduler
argument_list|()
decl_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a.a1"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b1"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a.a1.b1"
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setUserLimitFactor
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a.a1.b1"
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|cs
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
operator|new
name|RMContextImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|RMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReconnectedNode ()
specifier|public
name|void
name|testReconnectedNode
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|csConf
argument_list|)
expr_stmt|;
name|CapacityScheduler
name|cs
init|=
operator|new
name|CapacityScheduler
argument_list|()
decl_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|cs
operator|.
name|reinitialize
argument_list|(
name|csConf
argument_list|,
operator|new
name|RMContextImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|RMContainerTokenSecretManager
argument_list|(
name|csConf
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|RMNode
name|n1
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|0
argument_list|,
name|MockNodes
operator|.
name|newResource
argument_list|(
literal|4
operator|*
name|GB
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RMNode
name|n2
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|0
argument_list|,
name|MockNodes
operator|.
name|newResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|n1
argument_list|)
argument_list|)
expr_stmt|;
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|n2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
operator|*
name|GB
argument_list|,
name|cs
operator|.
name|getClusterResources
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// reconnect n1 with downgraded memory
name|n1
operator|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|0
argument_list|,
name|MockNodes
operator|.
name|newResource
argument_list|(
literal|2
operator|*
name|GB
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeRemovedSchedulerEvent
argument_list|(
name|n1
argument_list|)
argument_list|)
expr_stmt|;
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|n1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
operator|*
name|GB
argument_list|,
name|cs
operator|.
name|getClusterResources
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

