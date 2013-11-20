begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|webapp
package|;
end_package

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
name|MockNodes
operator|.
name|newResource
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
name|webapp
operator|.
name|Params
operator|.
name|TITLE
import|;
end_import

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
name|when
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|api
operator|.
name|records
operator|.
name|NodeState
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
name|YarnApplicationState
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
name|applicationsmanager
operator|.
name|MockAsm
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
name|capacity
operator|.
name|CapacityScheduler
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
name|capacity
operator|.
name|CapacitySchedulerConfiguration
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
name|fifo
operator|.
name|FifoScheduler
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
name|ClientToAMTokenSecretManagerInRM
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
name|NMTokenSecretManagerInRM
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
name|security
operator|.
name|ApplicationACLsManager
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
name|StringHelper
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
name|webapp
operator|.
name|WebApps
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
name|webapp
operator|.
name|YarnWebParams
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
name|webapp
operator|.
name|test
operator|.
name|WebAppTests
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
name|Maps
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Binder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Module
import|;
end_import

begin_class
DECL|class|TestRMWebApp
specifier|public
class|class
name|TestRMWebApp
block|{
DECL|field|GiB
specifier|static
specifier|final
name|int
name|GiB
init|=
literal|1024
decl_stmt|;
comment|// MiB
annotation|@
name|Test
DECL|method|testControllerIndex ()
specifier|public
name|void
name|testControllerIndex
parameter_list|()
block|{
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|createMockInjector
argument_list|(
name|TestRMWebApp
operator|.
name|class
argument_list|,
name|this
argument_list|,
operator|new
name|Module
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
name|binder
operator|.
name|bind
argument_list|(
name|ApplicationACLsManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|ApplicationACLsManager
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|RmController
name|c
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|RmController
operator|.
name|class
argument_list|)
decl_stmt|;
name|c
operator|.
name|index
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Applications"
argument_list|,
name|c
operator|.
name|get
argument_list|(
name|TITLE
argument_list|,
literal|"unknown"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testView ()
annotation|@
name|Test
specifier|public
name|void
name|testView
parameter_list|()
block|{
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|createMockInjector
argument_list|(
name|RMContext
operator|.
name|class
argument_list|,
name|mockRMContext
argument_list|(
literal|15
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|8
operator|*
name|GiB
argument_list|)
argument_list|,
operator|new
name|Module
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
try|try
block|{
name|binder
operator|.
name|bind
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|mockRm
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|8
operator|*
name|GiB
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|RmView
name|rmViewInstance
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|RmView
operator|.
name|class
argument_list|)
decl_stmt|;
name|rmViewInstance
operator|.
name|set
argument_list|(
name|YarnWebParams
operator|.
name|APP_STATE
argument_list|,
name|YarnApplicationState
operator|.
name|RUNNING
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|rmViewInstance
operator|.
name|render
argument_list|()
expr_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|rmViewInstance
operator|.
name|set
argument_list|(
name|YarnWebParams
operator|.
name|APP_STATE
argument_list|,
name|StringHelper
operator|.
name|cjoin
argument_list|(
name|YarnApplicationState
operator|.
name|ACCEPTED
operator|.
name|toString
argument_list|()
argument_list|,
name|YarnApplicationState
operator|.
name|RUNNING
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rmViewInstance
operator|.
name|render
argument_list|()
expr_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
DECL|method|testNodesPage ()
annotation|@
name|Test
specifier|public
name|void
name|testNodesPage
parameter_list|()
block|{
comment|// 10 nodes. Two of each type.
specifier|final
name|RMContext
name|rmContext
init|=
name|mockRMContext
argument_list|(
literal|3
argument_list|,
literal|2
argument_list|,
literal|12
argument_list|,
literal|8
operator|*
name|GiB
argument_list|)
decl_stmt|;
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|createMockInjector
argument_list|(
name|RMContext
operator|.
name|class
argument_list|,
name|rmContext
argument_list|,
operator|new
name|Module
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
try|try
block|{
name|binder
operator|.
name|bind
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|mockRm
argument_list|(
name|rmContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
comment|// All nodes
name|NodesPage
name|instance
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|NodesPage
operator|.
name|class
argument_list|)
decl_stmt|;
name|instance
operator|.
name|render
argument_list|()
expr_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
comment|// Unhealthy nodes
name|instance
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|NODE_STATE
argument_list|,
name|NodeState
operator|.
name|UNHEALTHY
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|instance
operator|.
name|render
argument_list|()
expr_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
comment|// Lost nodes
name|instance
operator|.
name|moreParams
argument_list|()
operator|.
name|put
argument_list|(
name|YarnWebParams
operator|.
name|NODE_STATE
argument_list|,
name|NodeState
operator|.
name|LOST
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|instance
operator|.
name|render
argument_list|()
expr_stmt|;
name|WebAppTests
operator|.
name|flushOutput
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
DECL|method|mockRMContext (int numApps, int racks, int numNodes, int mbsPerNode)
specifier|public
specifier|static
name|RMContext
name|mockRMContext
parameter_list|(
name|int
name|numApps
parameter_list|,
name|int
name|racks
parameter_list|,
name|int
name|numNodes
parameter_list|,
name|int
name|mbsPerNode
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RMApp
argument_list|>
name|apps
init|=
name|MockAsm
operator|.
name|newApplications
argument_list|(
name|numApps
argument_list|)
decl_stmt|;
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|applicationsMaps
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
for|for
control|(
name|RMApp
name|app
range|:
name|apps
control|)
block|{
name|applicationsMaps
operator|.
name|put
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|app
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|RMNode
argument_list|>
name|nodes
init|=
name|MockNodes
operator|.
name|newNodes
argument_list|(
name|racks
argument_list|,
name|numNodes
argument_list|,
name|newResource
argument_list|(
name|mbsPerNode
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|nodesMap
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
for|for
control|(
name|RMNode
name|node
range|:
name|nodes
control|)
block|{
name|nodesMap
operator|.
name|put
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|RMNode
argument_list|>
name|deactivatedNodes
init|=
name|MockNodes
operator|.
name|deactivatedNodes
argument_list|(
name|racks
argument_list|,
name|numNodes
argument_list|,
name|newResource
argument_list|(
name|mbsPerNode
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|RMNode
argument_list|>
name|deactivatedNodesMap
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
for|for
control|(
name|RMNode
name|node
range|:
name|deactivatedNodes
control|)
block|{
name|deactivatedNodesMap
operator|.
name|put
argument_list|(
name|node
operator|.
name|getHostName
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
return|return
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
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|getRMApps
parameter_list|()
block|{
return|return
name|applicationsMaps
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|RMNode
argument_list|>
name|getInactiveRMNodes
parameter_list|()
block|{
return|return
name|deactivatedNodesMap
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConcurrentMap
argument_list|<
name|NodeId
argument_list|,
name|RMNode
argument_list|>
name|getRMNodes
parameter_list|()
block|{
return|return
name|nodesMap
return|;
block|}
block|}
return|;
block|}
DECL|method|mockRm (int apps, int racks, int nodes, int mbsPerNode)
specifier|public
specifier|static
name|ResourceManager
name|mockRm
parameter_list|(
name|int
name|apps
parameter_list|,
name|int
name|racks
parameter_list|,
name|int
name|nodes
parameter_list|,
name|int
name|mbsPerNode
parameter_list|)
throws|throws
name|IOException
block|{
name|RMContext
name|rmContext
init|=
name|mockRMContext
argument_list|(
name|apps
argument_list|,
name|racks
argument_list|,
name|nodes
argument_list|,
name|mbsPerNode
argument_list|)
decl_stmt|;
return|return
name|mockRm
argument_list|(
name|rmContext
argument_list|)
return|;
block|}
DECL|method|mockRm (RMContext rmContext)
specifier|public
specifier|static
name|ResourceManager
name|mockRm
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|IOException
block|{
name|ResourceManager
name|rm
init|=
name|mock
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|ResourceScheduler
name|rs
init|=
name|mockCapacityScheduler
argument_list|()
decl_stmt|;
name|ApplicationACLsManager
name|aclMgr
init|=
name|mockAppACLsManager
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|rm
operator|.
name|getResourceScheduler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rm
operator|.
name|getApplicationACLsManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|aclMgr
argument_list|)
expr_stmt|;
return|return
name|rm
return|;
block|}
DECL|method|mockCapacityScheduler ()
specifier|public
specifier|static
name|CapacityScheduler
name|mockCapacityScheduler
parameter_list|()
throws|throws
name|IOException
block|{
comment|// stolen from TestCapacityScheduler
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
operator|new
name|RMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|NMTokenSecretManagerInRM
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|ClientToAMTokenSecretManagerInRM
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cs
return|;
block|}
DECL|method|mockAppACLsManager ()
specifier|public
specifier|static
name|ApplicationACLsManager
name|mockAppACLsManager
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
return|return
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|setupQueueConfiguration (CapacitySchedulerConfiguration conf)
specifier|static
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
block|,
literal|"c"
block|}
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
literal|10
argument_list|)
expr_stmt|;
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
name|conf
operator|.
name|setCapacity
argument_list|(
name|B
argument_list|,
literal|20
argument_list|)
expr_stmt|;
specifier|final
name|String
name|C
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".c"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C
argument_list|,
literal|70
argument_list|)
expr_stmt|;
comment|// Define 2nd-level queues
specifier|final
name|String
name|A1
init|=
name|A
operator|+
literal|".a1"
decl_stmt|;
specifier|final
name|String
name|A2
init|=
name|A
operator|+
literal|".a2"
decl_stmt|;
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
literal|30
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A2
argument_list|,
literal|70
argument_list|)
expr_stmt|;
specifier|final
name|String
name|B1
init|=
name|B
operator|+
literal|".b1"
decl_stmt|;
specifier|final
name|String
name|B2
init|=
name|B
operator|+
literal|".b2"
decl_stmt|;
specifier|final
name|String
name|B3
init|=
name|B
operator|+
literal|".b3"
decl_stmt|;
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
literal|50
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B2
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|B3
argument_list|,
literal|20
argument_list|)
expr_stmt|;
specifier|final
name|String
name|C1
init|=
name|C
operator|+
literal|".c1"
decl_stmt|;
specifier|final
name|String
name|C2
init|=
name|C
operator|+
literal|".c2"
decl_stmt|;
specifier|final
name|String
name|C3
init|=
name|C
operator|+
literal|".c3"
decl_stmt|;
specifier|final
name|String
name|C4
init|=
name|C
operator|+
literal|".c4"
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|C
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c1"
block|,
literal|"c2"
block|,
literal|"c3"
block|,
literal|"c4"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C1
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C3
argument_list|,
literal|35
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C4
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// Define 3rd-level queues
specifier|final
name|String
name|C11
init|=
name|C1
operator|+
literal|".c11"
decl_stmt|;
specifier|final
name|String
name|C12
init|=
name|C1
operator|+
literal|".c12"
decl_stmt|;
specifier|final
name|String
name|C13
init|=
name|C1
operator|+
literal|".c13"
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|C1
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c11"
block|,
literal|"c12"
block|,
literal|"c13"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C11
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C12
argument_list|,
literal|45
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|C13
argument_list|,
literal|40
argument_list|)
expr_stmt|;
block|}
DECL|method|mockFifoRm (int apps, int racks, int nodes, int mbsPerNode)
specifier|public
specifier|static
name|ResourceManager
name|mockFifoRm
parameter_list|(
name|int
name|apps
parameter_list|,
name|int
name|racks
parameter_list|,
name|int
name|nodes
parameter_list|,
name|int
name|mbsPerNode
parameter_list|)
throws|throws
name|Exception
block|{
name|ResourceManager
name|rm
init|=
name|mock
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|RMContext
name|rmContext
init|=
name|mockRMContext
argument_list|(
name|apps
argument_list|,
name|racks
argument_list|,
name|nodes
argument_list|,
name|mbsPerNode
argument_list|)
decl_stmt|;
name|ResourceScheduler
name|rs
init|=
name|mockFifoScheduler
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|rm
operator|.
name|getResourceScheduler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rs
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
return|return
name|rm
return|;
block|}
DECL|method|mockFifoScheduler ()
specifier|public
specifier|static
name|FifoScheduler
name|mockFifoScheduler
parameter_list|()
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupFifoQueueConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|FifoScheduler
name|fs
init|=
operator|new
name|FifoScheduler
argument_list|()
decl_stmt|;
name|fs
operator|.
name|setConf
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|fs
return|;
block|}
DECL|method|setupFifoQueueConfiguration (CapacitySchedulerConfiguration conf)
specifier|static
name|void
name|setupFifoQueueConfiguration
parameter_list|(
name|CapacitySchedulerConfiguration
name|conf
parameter_list|)
block|{
comment|// Define default queue
name|conf
operator|.
name|setQueues
argument_list|(
literal|"default"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"default"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
literal|"default"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
comment|// For manual testing
name|WebApps
operator|.
name|$for
argument_list|(
literal|"yarn"
argument_list|,
operator|new
name|TestRMWebApp
argument_list|()
argument_list|)
operator|.
name|at
argument_list|(
literal|8888
argument_list|)
operator|.
name|inDevMode
argument_list|()
operator|.
name|start
argument_list|(
operator|new
name|RMWebApp
argument_list|(
name|mockRm
argument_list|(
literal|2500
argument_list|,
literal|8
argument_list|,
literal|8
argument_list|,
literal|8
operator|*
name|GiB
argument_list|)
argument_list|)
argument_list|)
operator|.
name|joinThread
argument_list|()
expr_stmt|;
name|WebApps
operator|.
name|$for
argument_list|(
literal|"yarn"
argument_list|,
operator|new
name|TestRMWebApp
argument_list|()
argument_list|)
operator|.
name|at
argument_list|(
literal|8888
argument_list|)
operator|.
name|inDevMode
argument_list|()
operator|.
name|start
argument_list|(
operator|new
name|RMWebApp
argument_list|(
name|mockFifoRm
argument_list|(
literal|10
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|8
operator|*
name|GiB
argument_list|)
argument_list|)
argument_list|)
operator|.
name|joinThread
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

