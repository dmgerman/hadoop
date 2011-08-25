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
name|event
operator|.
name|Dispatcher
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
name|Event
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|rmcontainer
operator|.
name|ContainerAllocationExpirer
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
name|SchedulerApp
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
name|SchedulerNode
import|;
end_import

begin_class
DECL|class|TestUtils
specifier|public
class|class
name|TestUtils
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
name|TestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Get a mock {@link RMContext} for use in test cases.    * @return a mock {@link RMContext} for use in test cases    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|getMockRMContext ()
specifier|public
specifier|static
name|RMContext
name|getMockRMContext
parameter_list|()
block|{
comment|// Null dispatcher
name|Dispatcher
name|nullDispatcher
init|=
operator|new
name|Dispatcher
argument_list|()
block|{
specifier|private
specifier|final
name|EventHandler
name|handler
init|=
operator|new
name|EventHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{             }
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
name|eventType
parameter_list|,
name|EventHandler
name|handler
parameter_list|)
block|{       }
annotation|@
name|Override
specifier|public
name|EventHandler
name|getEventHandler
parameter_list|()
block|{
return|return
name|handler
return|;
block|}
block|}
decl_stmt|;
comment|// No op
name|ContainerAllocationExpirer
name|cae
init|=
operator|new
name|ContainerAllocationExpirer
argument_list|(
name|nullDispatcher
argument_list|)
decl_stmt|;
name|RMContext
name|rmContext
init|=
operator|new
name|RMContextImpl
argument_list|(
literal|null
argument_list|,
name|nullDispatcher
argument_list|,
name|cae
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|rmContext
return|;
block|}
comment|/**    * Hook to spy on queues.    */
DECL|class|SpyHook
specifier|static
class|class
name|SpyHook
extends|extends
name|CapacityScheduler
operator|.
name|QueueHook
block|{
annotation|@
name|Override
DECL|method|hook (Queue queue)
specifier|public
name|Queue
name|hook
parameter_list|(
name|Queue
name|queue
parameter_list|)
block|{
return|return
name|spy
argument_list|(
name|queue
argument_list|)
return|;
block|}
block|}
DECL|field|spyHook
specifier|public
specifier|static
name|SpyHook
name|spyHook
init|=
operator|new
name|SpyHook
argument_list|()
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|method|createMockPriority ( int priority)
specifier|public
specifier|static
name|Priority
name|createMockPriority
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
comment|//    Priority p = mock(Priority.class);
comment|//    when(p.getPriority()).thenReturn(priority);
name|Priority
name|p
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|p
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|createResourceRequest ( String hostName, int memory, int numContainers, Priority priority, RecordFactory recordFactory)
specifier|public
specifier|static
name|ResourceRequest
name|createResourceRequest
parameter_list|(
name|String
name|hostName
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|numContainers
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|RecordFactory
name|recordFactory
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|Resource
name|capability
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|memory
argument_list|)
decl_stmt|;
name|request
operator|.
name|setNumContainers
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
name|request
operator|.
name|setHostName
argument_list|(
name|hostName
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|request
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
specifier|public
specifier|static
name|ApplicationAttemptId
DECL|method|getMockApplicationAttemptId (int appId, int attemptId)
name|getMockApplicationAttemptId
parameter_list|(
name|int
name|appId
parameter_list|,
name|int
name|attemptId
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|mock
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|applicationId
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|applicationId
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|mock
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|applicationAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|applicationAttemptId
operator|.
name|getAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
return|return
name|applicationAttemptId
return|;
block|}
DECL|method|getMockNode ( String host, String rack, int port, int capability)
specifier|public
specifier|static
name|SchedulerNode
name|getMockNode
parameter_list|(
name|String
name|host
parameter_list|,
name|String
name|rack
parameter_list|,
name|int
name|port
parameter_list|,
name|int
name|capability
parameter_list|)
block|{
name|NodeId
name|nodeId
init|=
name|mock
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|nodeId
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|RMNode
name|rmNode
init|=
name|mock
argument_list|(
name|RMNode
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmNode
operator|.
name|getTotalCapability
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resources
operator|.
name|createResource
argument_list|(
name|capability
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmNode
operator|.
name|getNodeAddress
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|host
operator|+
literal|":"
operator|+
name|port
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmNode
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmNode
operator|.
name|getRackName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rack
argument_list|)
expr_stmt|;
name|SchedulerNode
name|node
init|=
name|spy
argument_list|(
operator|new
name|SchedulerNode
argument_list|(
name|rmNode
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"node = "
operator|+
name|host
operator|+
literal|" avail="
operator|+
name|node
operator|.
name|getAvailableResource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
DECL|method|getMockContainerId (SchedulerApp application)
specifier|public
specifier|static
name|ContainerId
name|getMockContainerId
parameter_list|(
name|SchedulerApp
name|application
parameter_list|)
block|{
name|ContainerId
name|containerId
init|=
name|mock
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|containerId
argument_list|)
operator|.
name|getAppAttemptId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|containerId
argument_list|)
operator|.
name|getAppId
argument_list|()
expr_stmt|;
name|doReturn
argument_list|(
name|application
operator|.
name|getNewContainerId
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|containerId
argument_list|)
operator|.
name|getId
argument_list|()
expr_stmt|;
return|return
name|containerId
return|;
block|}
DECL|method|getMockContainer ( ContainerId containerId, NodeId nodeId, Resource resource)
specifier|public
specifier|static
name|Container
name|getMockContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
name|Container
name|container
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|resource
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
block|}
end_class

end_unit

