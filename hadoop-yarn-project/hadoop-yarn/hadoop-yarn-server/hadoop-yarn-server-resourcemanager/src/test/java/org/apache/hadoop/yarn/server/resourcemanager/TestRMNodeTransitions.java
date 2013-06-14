begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|doAnswer
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
name|doReturn
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
name|verify
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
name|*
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
name|ContainerStatus
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
name|event
operator|.
name|EventHandler
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatResponse
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
name|records
operator|.
name|NodeHealthStatus
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
name|RMNodeCleanAppEvent
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
name|RMNodeCleanContainerEvent
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
name|RMNodeEvent
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
name|RMNodeEventType
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
name|RMNodeImpl
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
name|RMNodeStatusEvent
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
name|UpdatedContainerInfo
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|event
operator|.
name|SchedulerEventType
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
name|DelegationTokenRenewer
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|Records
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
DECL|class|TestRMNodeTransitions
specifier|public
class|class
name|TestRMNodeTransitions
block|{
DECL|field|node
name|RMNodeImpl
name|node
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|scheduler
specifier|private
name|YarnScheduler
name|scheduler
decl_stmt|;
DECL|field|eventType
specifier|private
name|SchedulerEventType
name|eventType
decl_stmt|;
DECL|field|completedContainers
specifier|private
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|completedContainers
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
decl_stmt|;
DECL|class|TestSchedulerEventDispatcher
specifier|private
specifier|final
class|class
name|TestSchedulerEventDispatcher
implements|implements
name|EventHandler
argument_list|<
name|SchedulerEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|handle (SchedulerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|SchedulerEvent
name|event
parameter_list|)
block|{
name|scheduler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
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
name|InlineDispatcher
name|rmDispatcher
init|=
operator|new
name|InlineDispatcher
argument_list|()
decl_stmt|;
name|rmContext
operator|=
operator|new
name|RMContextImpl
argument_list|(
name|rmDispatcher
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mock
argument_list|(
name|DelegationTokenRenewer
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|scheduler
operator|=
name|mock
argument_list|(
name|YarnScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|doAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
specifier|final
name|SchedulerEvent
name|event
init|=
call|(
name|SchedulerEvent
call|)
argument_list|(
name|invocation
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|eventType
operator|=
name|event
operator|.
name|getType
argument_list|()
expr_stmt|;
if|if
condition|(
name|eventType
operator|==
name|SchedulerEventType
operator|.
name|NODE_UPDATE
condition|)
block|{
name|List
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|lastestContainersInfoList
init|=
operator|(
operator|(
name|NodeUpdateSchedulerEvent
operator|)
name|event
operator|)
operator|.
name|getRMNode
argument_list|()
operator|.
name|pullContainerUpdates
argument_list|()
decl_stmt|;
for|for
control|(
name|UpdatedContainerInfo
name|lastestContainersInfo
range|:
name|lastestContainersInfoList
control|)
block|{
name|completedContainers
operator|.
name|addAll
argument_list|(
name|lastestContainersInfo
operator|.
name|getCompletedContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|scheduler
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|SchedulerEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|rmDispatcher
operator|.
name|register
argument_list|(
name|SchedulerEventType
operator|.
name|class
argument_list|,
operator|new
name|TestSchedulerEventDispatcher
argument_list|()
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"localhost"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|node
operator|=
operator|new
name|RMNodeImpl
argument_list|(
name|nodeId
argument_list|,
name|rmContext
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
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
DECL|method|getMockRMNodeStatusEvent ()
specifier|private
name|RMNodeStatusEvent
name|getMockRMNodeStatusEvent
parameter_list|()
block|{
name|NodeHeartbeatResponse
name|response
init|=
name|mock
argument_list|(
name|NodeHeartbeatResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|NodeHealthStatus
name|healthStatus
init|=
name|mock
argument_list|(
name|NodeHealthStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|Boolean
name|yes
init|=
operator|new
name|Boolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|yes
argument_list|)
operator|.
name|when
argument_list|(
name|healthStatus
argument_list|)
operator|.
name|getIsNodeHealthy
argument_list|()
expr_stmt|;
name|RMNodeStatusEvent
name|event
init|=
name|mock
argument_list|(
name|RMNodeStatusEvent
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|healthStatus
argument_list|)
operator|.
name|when
argument_list|(
name|event
argument_list|)
operator|.
name|getNodeHealthStatus
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|response
argument_list|)
operator|.
name|when
argument_list|(
name|event
argument_list|)
operator|.
name|getLatestResponse
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|RMNodeEventType
operator|.
name|STATUS_UPDATE
argument_list|)
operator|.
name|when
argument_list|(
name|event
argument_list|)
operator|.
name|getType
argument_list|()
expr_stmt|;
return|return
name|event
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testExpiredContainer ()
specifier|public
name|void
name|testExpiredContainer
parameter_list|()
block|{
comment|// Start the node
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
literal|null
argument_list|,
name|RMNodeEventType
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|scheduler
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|NodeAddedSchedulerEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Expire a container
name|ContainerId
name|completedContainerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeCleanContainerEvent
argument_list|(
literal|null
argument_list|,
name|completedContainerId
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|node
operator|.
name|getContainersToCleanUp
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now verify that scheduler isn't notified of an expired container
comment|// by checking number of 'completedContainers' it got in the previous event
name|RMNodeStatusEvent
name|statusEvent
init|=
name|getMockRMNodeStatusEvent
argument_list|()
decl_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|completedContainerId
argument_list|)
operator|.
name|when
argument_list|(
name|containerStatus
argument_list|)
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|containerStatus
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|statusEvent
argument_list|)
operator|.
name|getContainers
argument_list|()
expr_stmt|;
name|node
operator|.
name|handle
argument_list|(
name|statusEvent
argument_list|)
expr_stmt|;
comment|/* Expect the scheduler call handle function 2 times      * 1. RMNode status from new to Running, handle the add_node event      * 2. handle the node update event      */
name|verify
argument_list|(
name|scheduler
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|NodeUpdateSchedulerEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testContainerUpdate ()
specifier|public
name|void
name|testContainerUpdate
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|//Start the node
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
literal|null
argument_list|,
name|RMNodeEventType
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"localhost:1"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RMNodeImpl
name|node2
init|=
operator|new
name|RMNodeImpl
argument_list|(
name|nodeId
argument_list|,
name|rmContext
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|node2
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
literal|null
argument_list|,
name|RMNodeEventType
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerId
name|completedContainerIdFromNode1
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|completedContainerIdFromNode2_1
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|completedContainerIdFromNode2_2
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|RMNodeStatusEvent
name|statusEventFromNode1
init|=
name|getMockRMNodeStatusEvent
argument_list|()
decl_stmt|;
name|RMNodeStatusEvent
name|statusEventFromNode2_1
init|=
name|getMockRMNodeStatusEvent
argument_list|()
decl_stmt|;
name|RMNodeStatusEvent
name|statusEventFromNode2_2
init|=
name|getMockRMNodeStatusEvent
argument_list|()
decl_stmt|;
name|ContainerStatus
name|containerStatusFromNode1
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerStatus
name|containerStatusFromNode2_1
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerStatus
name|containerStatusFromNode2_2
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|completedContainerIdFromNode1
argument_list|)
operator|.
name|when
argument_list|(
name|containerStatusFromNode1
argument_list|)
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|containerStatusFromNode1
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|statusEventFromNode1
argument_list|)
operator|.
name|getContainers
argument_list|()
expr_stmt|;
name|node
operator|.
name|handle
argument_list|(
name|statusEventFromNode1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|completedContainers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|completedContainerIdFromNode1
argument_list|,
name|completedContainers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|completedContainers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|completedContainerIdFromNode2_1
argument_list|)
operator|.
name|when
argument_list|(
name|containerStatusFromNode2_1
argument_list|)
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|containerStatusFromNode2_1
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|statusEventFromNode2_1
argument_list|)
operator|.
name|getContainers
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|completedContainerIdFromNode2_2
argument_list|)
operator|.
name|when
argument_list|(
name|containerStatusFromNode2_2
argument_list|)
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|containerStatusFromNode2_2
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|statusEventFromNode2_2
argument_list|)
operator|.
name|getContainers
argument_list|()
expr_stmt|;
name|node2
operator|.
name|setNextHeartBeat
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|node2
operator|.
name|handle
argument_list|(
name|statusEventFromNode2_1
argument_list|)
expr_stmt|;
name|node2
operator|.
name|setNextHeartBeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|node2
operator|.
name|handle
argument_list|(
name|statusEventFromNode2_2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|completedContainers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|completedContainerIdFromNode2_1
argument_list|,
name|completedContainers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|completedContainerIdFromNode2_2
argument_list|,
name|completedContainers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testStatusChange ()
specifier|public
name|void
name|testStatusChange
parameter_list|()
block|{
comment|//Start the node
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
literal|null
argument_list|,
name|RMNodeEventType
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
comment|//Add info to the queue first
name|node
operator|.
name|setNextHeartBeat
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ContainerId
name|completedContainerId1
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|completedContainerId2
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RMNodeStatusEvent
name|statusEvent1
init|=
name|getMockRMNodeStatusEvent
argument_list|()
decl_stmt|;
name|RMNodeStatusEvent
name|statusEvent2
init|=
name|getMockRMNodeStatusEvent
argument_list|()
decl_stmt|;
name|ContainerStatus
name|containerStatus1
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerStatus
name|containerStatus2
init|=
name|mock
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|completedContainerId1
argument_list|)
operator|.
name|when
argument_list|(
name|containerStatus1
argument_list|)
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|containerStatus1
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|statusEvent1
argument_list|)
operator|.
name|getContainers
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|completedContainerId2
argument_list|)
operator|.
name|when
argument_list|(
name|containerStatus2
argument_list|)
operator|.
name|getContainerId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|containerStatus2
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|statusEvent2
argument_list|)
operator|.
name|getContainers
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|scheduler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|NodeUpdateSchedulerEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|handle
argument_list|(
name|statusEvent1
argument_list|)
expr_stmt|;
name|node
operator|.
name|handle
argument_list|(
name|statusEvent2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|scheduler
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|NodeUpdateSchedulerEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|node
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|EXPIRE
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|node
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRunningExpire ()
specifier|public
name|void
name|testRunningExpire
parameter_list|()
block|{
name|RMNodeImpl
name|node
init|=
name|getRunningNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|EXPIRE
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|LOST
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnhealthyExpire ()
specifier|public
name|void
name|testUnhealthyExpire
parameter_list|()
block|{
name|RMNodeImpl
name|node
init|=
name|getUnhealthyNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|EXPIRE
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|LOST
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRunningDecommission ()
specifier|public
name|void
name|testRunningDecommission
parameter_list|()
block|{
name|RMNodeImpl
name|node
init|=
name|getRunningNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|DECOMMISSION
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|DECOMMISSIONED
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnhealthyDecommission ()
specifier|public
name|void
name|testUnhealthyDecommission
parameter_list|()
block|{
name|RMNodeImpl
name|node
init|=
name|getUnhealthyNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|DECOMMISSION
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|DECOMMISSIONED
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRunningRebooting ()
specifier|public
name|void
name|testRunningRebooting
parameter_list|()
block|{
name|RMNodeImpl
name|node
init|=
name|getRunningNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|REBOOTING
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|REBOOTED
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnhealthyRebooting ()
specifier|public
name|void
name|testUnhealthyRebooting
parameter_list|()
block|{
name|RMNodeImpl
name|node
init|=
name|getUnhealthyNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|REBOOTING
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|REBOOTED
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testUpdateHeartbeatResponseForCleanup ()
specifier|public
name|void
name|testUpdateHeartbeatResponseForCleanup
parameter_list|()
block|{
name|RMNodeImpl
name|node
init|=
name|getRunningNode
argument_list|()
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|node
operator|.
name|getNodeID
argument_list|()
decl_stmt|;
comment|// Expire a container
name|ContainerId
name|completedContainerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeCleanContainerEvent
argument_list|(
name|nodeId
argument_list|,
name|completedContainerId
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|node
operator|.
name|getContainersToCleanUp
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Finish an application
name|ApplicationId
name|finishedAppId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeCleanAppEvent
argument_list|(
name|nodeId
argument_list|,
name|finishedAppId
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|node
operator|.
name|getAppsToCleanup
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify status update does not clear containers/apps to cleanup
comment|// but updating heartbeat response for cleanup does
name|RMNodeStatusEvent
name|statusEvent
init|=
name|getMockRMNodeStatusEvent
argument_list|()
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
name|statusEvent
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|node
operator|.
name|getContainersToCleanUp
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|node
operator|.
name|getAppsToCleanup
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|NodeHeartbeatResponse
name|hbrsp
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeHeartbeatResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|node
operator|.
name|updateNodeHeartbeatResponseForCleanup
argument_list|(
name|hbrsp
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|node
operator|.
name|getContainersToCleanUp
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|node
operator|.
name|getAppsToCleanup
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hbrsp
operator|.
name|getContainersToCleanup
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|completedContainerId
argument_list|,
name|hbrsp
operator|.
name|getContainersToCleanup
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hbrsp
operator|.
name|getApplicationsToCleanup
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|finishedAppId
argument_list|,
name|hbrsp
operator|.
name|getApplicationsToCleanup
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getRunningNode ()
specifier|private
name|RMNodeImpl
name|getRunningNode
parameter_list|()
block|{
name|NodeId
name|nodeId
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"localhost"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|RMNodeImpl
name|node
init|=
operator|new
name|RMNodeImpl
argument_list|(
name|nodeId
argument_list|,
name|rmContext
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|RUNNING
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
DECL|method|getUnhealthyNode ()
specifier|private
name|RMNodeImpl
name|getUnhealthyNode
parameter_list|()
block|{
name|RMNodeImpl
name|node
init|=
name|getRunningNode
argument_list|()
decl_stmt|;
name|NodeHealthStatus
name|status
init|=
name|NodeHealthStatus
operator|.
name|newInstance
argument_list|(
literal|false
argument_list|,
literal|"sick"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeStatusEvent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|status
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeState
operator|.
name|UNHEALTHY
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
block|}
end_class

end_unit

