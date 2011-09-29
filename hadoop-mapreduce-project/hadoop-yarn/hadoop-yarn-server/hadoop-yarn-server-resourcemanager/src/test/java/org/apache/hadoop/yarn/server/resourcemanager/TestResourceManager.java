begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
DECL|class|TestResourceManager
specifier|public
class|class
name|TestResourceManager
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
name|TestResourceManager
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Store
name|store
init|=
name|StoreFactory
operator|.
name|getStore
argument_list|(
name|conf
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
DECL|method|testResourceAllocation ()
specifier|public
name|void
name|testResourceAllocation
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testResourceAllocation ---"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|memory
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
comment|// Register node1
name|String
name|host1
init|=
literal|"host1"
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
name|nm1
init|=
name|registerNode
argument_list|(
name|host1
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
name|memory
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// Register node2
name|String
name|host2
init|=
literal|"host2"
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
name|nm2
init|=
name|registerNode
argument_list|(
name|host2
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
name|memory
operator|/
literal|2
argument_list|)
decl_stmt|;
name|nm2
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// Submit an application
name|Application
name|application
init|=
operator|new
name|Application
argument_list|(
literal|"user1"
argument_list|,
name|resourceManager
argument_list|)
decl_stmt|;
name|application
operator|.
name|submit
argument_list|()
expr_stmt|;
name|application
operator|.
name|addNodeManager
argument_list|(
name|host1
argument_list|,
literal|1234
argument_list|,
name|nm1
argument_list|)
expr_stmt|;
name|application
operator|.
name|addNodeManager
argument_list|(
name|host2
argument_list|,
literal|1234
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
comment|// Application resource requirements
specifier|final
name|int
name|memory1
init|=
literal|1024
decl_stmt|;
name|Resource
name|capability1
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|memory1
argument_list|)
decl_stmt|;
name|Priority
name|priority1
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
name|application
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority1
argument_list|,
name|capability1
argument_list|)
expr_stmt|;
name|Task
name|t1
init|=
operator|new
name|Task
argument_list|(
name|application
argument_list|,
name|priority1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host1
block|,
name|host2
block|}
argument_list|)
decl_stmt|;
name|application
operator|.
name|addTask
argument_list|(
name|t1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|memory2
init|=
literal|2048
decl_stmt|;
name|Resource
name|capability2
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|memory2
argument_list|)
decl_stmt|;
name|Priority
name|priority0
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
comment|// higher
name|application
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority0
argument_list|,
name|capability2
argument_list|)
expr_stmt|;
comment|// Send resource requests to the scheduler
name|application
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|nm1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// Get allocations from the scheduler
name|application
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkResourceUsage
argument_list|(
name|nm1
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding new tasks..."
argument_list|)
expr_stmt|;
name|Task
name|t2
init|=
operator|new
name|Task
argument_list|(
name|application
argument_list|,
name|priority1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host1
block|,
name|host2
block|}
argument_list|)
decl_stmt|;
name|application
operator|.
name|addTask
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|Task
name|t3
init|=
operator|new
name|Task
argument_list|(
name|application
argument_list|,
name|priority0
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
name|application
operator|.
name|addTask
argument_list|(
name|t3
argument_list|)
expr_stmt|;
comment|// Send resource requests to the scheduler
name|application
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|checkResourceUsage
argument_list|(
name|nm1
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending hb from host2"
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending hb from host1"
argument_list|)
expr_stmt|;
name|nm1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
comment|// Get allocations from the scheduler
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to allocate..."
argument_list|)
expr_stmt|;
name|application
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm2
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkResourceUsage
argument_list|(
name|nm1
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
comment|// Complete tasks
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up tasks..."
argument_list|)
expr_stmt|;
name|application
operator|.
name|finishTask
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|application
operator|.
name|finishTask
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|application
operator|.
name|finishTask
argument_list|(
name|t3
argument_list|)
expr_stmt|;
comment|// Send heartbeat
name|nm1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm2
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|checkResourceUsage
argument_list|(
name|nm1
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"--- END: testResourceAllocation ---"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkResourceUsage ( org.apache.hadoop.yarn.server.resourcemanager.NodeManager... nodes )
specifier|private
name|void
name|checkResourceUsage
parameter_list|(
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
modifier|...
name|nodes
parameter_list|)
block|{
for|for
control|(
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
name|nodeManager
range|:
name|nodes
control|)
block|{
name|nodeManager
operator|.
name|checkResourceUsage
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

