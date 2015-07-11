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
name|List
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
name|security
operator|.
name|SecurityUtilTestHelper
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|Container
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
name|LogAggregationContext
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
name|api
operator|.
name|records
operator|.
name|Token
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|ContainerType
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
name|RMSecretManagerService
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
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
name|attempt
operator|.
name|RMAppAttemptState
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
name|SchedulerAppReport
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
name|YarnScheduler
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
name|utils
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
DECL|class|TestContainerAllocation
specifier|public
class|class
name|TestContainerAllocation
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
name|TestContainerAllocation
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
argument_list|(
name|timeout
operator|=
literal|3000000
argument_list|)
DECL|method|testExcessReservationThanNodeManagerCapacity ()
specifier|public
name|void
name|testExcessReservationThanNodeManagerCapacity
parameter_list|()
throws|throws
name|Exception
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
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
comment|// Register node1
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|2
operator|*
name|GB
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:2234"
argument_list|,
literal|3
operator|*
name|GB
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// wait..
name|int
name|waitCount
init|=
literal|20
decl_stmt|;
name|int
name|size
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|size
operator|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|)
operator|!=
literal|2
operator|&&
name|waitCount
operator|--
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for node managers to register : "
operator|+
name|size
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Submit an application
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|128
argument_list|)
decl_stmt|;
comment|// kick the scheduling
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt1
init|=
name|app1
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|MockAM
name|am1
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am1
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sending container requests "
argument_list|)
expr_stmt|;
name|am1
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"*"
block|}
argument_list|,
literal|2
operator|*
name|GB
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|AllocateResponse
name|alloc1Response
init|=
name|am1
operator|.
name|schedule
argument_list|()
decl_stmt|;
comment|// send the request
comment|// kick the scheduler
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|waitCounter
init|=
literal|20
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"heartbeating nm1"
argument_list|)
expr_stmt|;
while|while
condition|(
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|<
literal|1
operator|&&
name|waitCounter
operator|--
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for containers to be created for app 1..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|alloc1Response
operator|=
name|am1
operator|.
name|schedule
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"received container : "
operator|+
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// No container should be allocated.
comment|// Internally it should not been reserved.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"heartbeating nm2"
argument_list|)
expr_stmt|;
name|waitCounter
operator|=
literal|20
expr_stmt|;
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|<
literal|1
operator|&&
name|waitCounter
operator|--
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for containers to be created for app 1..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|alloc1Response
operator|=
name|am1
operator|.
name|schedule
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"received container : "
operator|+
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// This is to test container tokens are generated when the containers are
comment|// acquired by the AM, not when the containers are allocated
annotation|@
name|Test
DECL|method|testContainerTokenGeneratedOnPullRequest ()
specifier|public
name|void
name|testContainerTokenGeneratedOnPullRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
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
literal|"127.0.0.1:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
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
literal|"127.0.0.1"
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
name|rm1
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
expr_stmt|;
name|RMContainer
name|container
init|=
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getRMContainer
argument_list|(
name|containerId2
argument_list|)
decl_stmt|;
comment|// no container token is generated.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId2
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
comment|// acquire the container.
name|List
argument_list|<
name|Container
argument_list|>
name|containers
init|=
name|am1
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId2
argument_list|,
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// container token is generated.
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNormalContainerAllocationWhenDNSUnavailable ()
specifier|public
name|void
name|testNormalContainerAllocationWhenDNSUnavailable
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
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
literal|"unknownhost:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
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
literal|"127.0.0.1"
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
name|rm1
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
expr_stmt|;
comment|// acquire the container.
name|SecurityUtilTestHelper
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|containers
init|=
name|am1
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
comment|// not able to fetch the container;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|containers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SecurityUtilTestHelper
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|containers
operator|=
name|am1
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
comment|// should be able to fetch the container;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|containers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// This is to test whether LogAggregationContext is passed into
comment|// container tokens correctly
annotation|@
name|Test
DECL|method|testLogAggregationContextPassedIntoContainerToken ()
specifier|public
name|void
name|testLogAggregationContextPassedIntoContainerToken
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
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
literal|"127.0.0.1:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:2345"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
comment|// LogAggregationContext is set as null
name|Assert
operator|.
name|assertNull
argument_list|(
name|getLogAggregationContextFromContainerToken
argument_list|(
name|rm1
argument_list|,
name|nm1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// create a not-null LogAggregationContext
name|LogAggregationContext
name|logAggregationContext
init|=
name|LogAggregationContext
operator|.
name|newInstance
argument_list|(
literal|"includePattern"
argument_list|,
literal|"excludePattern"
argument_list|,
literal|"rolledLogsIncludePattern"
argument_list|,
literal|"rolledLogsExcludePattern"
argument_list|)
decl_stmt|;
name|LogAggregationContext
name|returned
init|=
name|getLogAggregationContextFromContainerToken
argument_list|(
name|rm1
argument_list|,
name|nm2
argument_list|,
name|logAggregationContext
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"includePattern"
argument_list|,
name|returned
operator|.
name|getIncludePattern
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"excludePattern"
argument_list|,
name|returned
operator|.
name|getExcludePattern
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"rolledLogsIncludePattern"
argument_list|,
name|returned
operator|.
name|getRolledLogsIncludePattern
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"rolledLogsExcludePattern"
argument_list|,
name|returned
operator|.
name|getRolledLogsExcludePattern
argument_list|()
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getLogAggregationContextFromContainerToken ( MockRM rm1, MockNM nm1, LogAggregationContext logAggregationContext)
specifier|private
name|LogAggregationContext
name|getLogAggregationContextFromContainerToken
parameter_list|(
name|MockRM
name|rm1
parameter_list|,
name|MockNM
name|nm1
parameter_list|,
name|LogAggregationContext
name|logAggregationContext
parameter_list|)
throws|throws
name|Exception
block|{
name|RMApp
name|app2
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
name|logAggregationContext
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
name|nm1
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// request a container.
name|am2
operator|.
name|allocate
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|512
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
name|ContainerId
name|containerId
init|=
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
decl_stmt|;
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
argument_list|)
expr_stmt|;
comment|// acquire the container.
name|List
argument_list|<
name|Container
argument_list|>
name|containers
init|=
name|am2
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId
argument_list|,
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// container token is generated.
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerTokenIdentifier
name|token
init|=
name|BuilderUtils
operator|.
name|newContainerTokenIdentifier
argument_list|(
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerToken
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|token
operator|.
name|getLogAggregationContext
argument_list|()
return|;
block|}
DECL|field|numRetries
specifier|private
specifier|volatile
name|int
name|numRetries
init|=
literal|0
decl_stmt|;
DECL|class|TestRMSecretManagerService
specifier|private
class|class
name|TestRMSecretManagerService
extends|extends
name|RMSecretManagerService
block|{
DECL|method|TestRMSecretManagerService (Configuration conf, RMContextImpl rmContext)
specifier|public
name|TestRMSecretManagerService
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMContextImpl
name|rmContext
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContainerTokenSecretManager ( Configuration conf)
specifier|protected
name|RMContainerTokenSecretManager
name|createContainerTokenSecretManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|RMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Token
name|createContainerToken
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|appSubmitter
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|createTime
parameter_list|,
name|LogAggregationContext
name|logAggregationContext
parameter_list|,
name|String
name|nodeLabelExp
parameter_list|,
name|ContainerType
name|containerType
parameter_list|)
block|{
name|numRetries
operator|++
expr_stmt|;
return|return
name|super
operator|.
name|createContainerToken
argument_list|(
name|containerId
argument_list|,
name|nodeId
argument_list|,
name|appSubmitter
argument_list|,
name|capability
argument_list|,
name|priority
argument_list|,
name|createTime
argument_list|,
name|logAggregationContext
argument_list|,
name|nodeLabelExp
argument_list|,
name|containerType
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
comment|// This is to test fetching AM container will be retried, if AM container is
comment|// not fetchable since DNS is unavailable causing container token/NMtoken
comment|// creation failure.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testAMContainerAllocationWhenDNSUnavailable ()
specifier|public
name|void
name|testAMContainerAllocationWhenDNSUnavailable
parameter_list|()
throws|throws
name|Exception
block|{
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RMSecretManagerService
name|createRMSecretManagerService
parameter_list|()
block|{
return|return
operator|new
name|TestRMSecretManagerService
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
return|;
block|}
block|}
decl_stmt|;
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
literal|"unknownhost:1234"
argument_list|,
literal|8000
argument_list|)
decl_stmt|;
name|SecurityUtilTestHelper
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|app1
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// fetching am container will fail, keep retrying 5 times.
while|while
condition|(
name|numRetries
operator|<=
literal|5
condition|)
block|{
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAppAttemptState
operator|.
name|SCHEDULED
argument_list|,
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for am container to be allocated."
argument_list|)
expr_stmt|;
block|}
name|SecurityUtilTestHelper
operator|.
name|setTokenServiceUseIp
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
expr_stmt|;
block|}
block|}
end_class

end_unit

