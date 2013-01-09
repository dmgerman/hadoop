begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmcontainer
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
name|rmcontainer
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
name|reset
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
name|DrainDispatcher
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptEvent
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
name|RMAppAttemptEventType
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
name|event
operator|.
name|RMAppAttemptContainerFinishedEvent
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
name|scheduler
operator|.
name|SchedulerUtils
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|TestRMContainerImpl
specifier|public
class|class
name|TestRMContainerImpl
block|{
annotation|@
name|Test
DECL|method|testReleaseWhileRunning ()
specifier|public
name|void
name|testReleaseWhileRunning
parameter_list|()
block|{
name|DrainDispatcher
name|drainDispatcher
init|=
operator|new
name|DrainDispatcher
argument_list|()
decl_stmt|;
name|EventHandler
name|eventHandler
init|=
name|drainDispatcher
operator|.
name|getEventHandler
argument_list|()
decl_stmt|;
name|EventHandler
argument_list|<
name|RMAppAttemptEvent
argument_list|>
name|appAttemptEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|EventHandler
name|generic
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|drainDispatcher
operator|.
name|register
argument_list|(
name|RMAppAttemptEventType
operator|.
name|class
argument_list|,
name|appAttemptEventHandler
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|register
argument_list|(
name|RMNodeEventType
operator|.
name|class
argument_list|,
name|generic
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"host"
argument_list|,
literal|3425
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|1
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
name|ContainerId
name|containerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerAllocationExpirer
name|expirer
init|=
name|mock
argument_list|(
name|ContainerAllocationExpirer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|512
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Priority
name|priority
init|=
name|BuilderUtils
operator|.
name|newPriority
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Container
name|container
init|=
name|BuilderUtils
operator|.
name|newContainer
argument_list|(
name|containerId
argument_list|,
name|nodeId
argument_list|,
literal|"host:3465"
argument_list|,
name|resource
argument_list|,
name|priority
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|RMContainer
name|rmContainer
init|=
operator|new
name|RMContainerImpl
argument_list|(
name|container
argument_list|,
name|appAttemptId
argument_list|,
name|nodeId
argument_list|,
name|eventHandler
argument_list|,
name|expirer
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|NEW
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|containerId
argument_list|,
name|RMContainerEventType
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|containerId
argument_list|,
name|RMContainerEventType
operator|.
name|ACQUIRED
argument_list|)
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|containerId
argument_list|,
name|RMContainerEventType
operator|.
name|LAUNCHED
argument_list|)
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|RUNNING
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// In RUNNING state. Verify RELEASED and associated actions.
name|reset
argument_list|(
name|appAttemptEventHandler
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|SchedulerUtils
operator|.
name|createAbnormalContainerStatus
argument_list|(
name|containerId
argument_list|,
name|SchedulerUtils
operator|.
name|RELEASED_CONTAINER
argument_list|)
decl_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerFinishedEvent
argument_list|(
name|containerId
argument_list|,
name|containerStatus
argument_list|,
name|RMContainerEventType
operator|.
name|RELEASED
argument_list|)
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|RELEASED
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|ArgumentCaptor
argument_list|<
name|RMAppAttemptContainerFinishedEvent
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|RMAppAttemptContainerFinishedEvent
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|appAttemptEventHandler
argument_list|)
operator|.
name|handle
argument_list|(
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|RMAppAttemptContainerFinishedEvent
name|cfEvent
init|=
name|captor
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|appAttemptId
argument_list|,
name|cfEvent
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|containerStatus
argument_list|,
name|cfEvent
operator|.
name|getContainerStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RMAppAttemptEventType
operator|.
name|CONTAINER_FINISHED
argument_list|,
name|cfEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
comment|// In RELEASED state. A FINIHSED event may come in.
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerFinishedEvent
argument_list|(
name|containerId
argument_list|,
name|SchedulerUtils
operator|.
name|createAbnormalContainerStatus
argument_list|(
name|containerId
argument_list|,
literal|"FinishedContainer"
argument_list|)
argument_list|,
name|RMContainerEventType
operator|.
name|FINISHED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|RELEASED
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExpireWhileRunning ()
specifier|public
name|void
name|testExpireWhileRunning
parameter_list|()
block|{
name|DrainDispatcher
name|drainDispatcher
init|=
operator|new
name|DrainDispatcher
argument_list|()
decl_stmt|;
name|EventHandler
name|eventHandler
init|=
name|drainDispatcher
operator|.
name|getEventHandler
argument_list|()
decl_stmt|;
name|EventHandler
argument_list|<
name|RMAppAttemptEvent
argument_list|>
name|appAttemptEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|EventHandler
name|generic
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|drainDispatcher
operator|.
name|register
argument_list|(
name|RMAppAttemptEventType
operator|.
name|class
argument_list|,
name|appAttemptEventHandler
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|register
argument_list|(
name|RMNodeEventType
operator|.
name|class
argument_list|,
name|generic
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|NodeId
name|nodeId
init|=
name|BuilderUtils
operator|.
name|newNodeId
argument_list|(
literal|"host"
argument_list|,
literal|3425
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|1
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
name|ContainerId
name|containerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerAllocationExpirer
name|expirer
init|=
name|mock
argument_list|(
name|ContainerAllocationExpirer
operator|.
name|class
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|512
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Priority
name|priority
init|=
name|BuilderUtils
operator|.
name|newPriority
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Container
name|container
init|=
name|BuilderUtils
operator|.
name|newContainer
argument_list|(
name|containerId
argument_list|,
name|nodeId
argument_list|,
literal|"host:3465"
argument_list|,
name|resource
argument_list|,
name|priority
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|RMContainer
name|rmContainer
init|=
operator|new
name|RMContainerImpl
argument_list|(
name|container
argument_list|,
name|appAttemptId
argument_list|,
name|nodeId
argument_list|,
name|eventHandler
argument_list|,
name|expirer
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|NEW
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|containerId
argument_list|,
name|RMContainerEventType
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|containerId
argument_list|,
name|RMContainerEventType
operator|.
name|ACQUIRED
argument_list|)
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|containerId
argument_list|,
name|RMContainerEventType
operator|.
name|LAUNCHED
argument_list|)
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|RUNNING
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// In RUNNING state. Verify EXPIRE and associated actions.
name|reset
argument_list|(
name|appAttemptEventHandler
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|SchedulerUtils
operator|.
name|createAbnormalContainerStatus
argument_list|(
name|containerId
argument_list|,
name|SchedulerUtils
operator|.
name|EXPIRED_CONTAINER
argument_list|)
decl_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerFinishedEvent
argument_list|(
name|containerId
argument_list|,
name|containerStatus
argument_list|,
name|RMContainerEventType
operator|.
name|EXPIRE
argument_list|)
argument_list|)
expr_stmt|;
name|drainDispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|RMContainerState
operator|.
name|RUNNING
argument_list|,
name|rmContainer
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

