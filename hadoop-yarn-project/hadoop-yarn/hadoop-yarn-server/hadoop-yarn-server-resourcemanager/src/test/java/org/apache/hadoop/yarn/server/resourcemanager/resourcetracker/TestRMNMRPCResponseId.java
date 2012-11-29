begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.resourcetracker
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
name|resourcetracker
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatRequest
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
name|RegisterNodeManagerRequest
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
name|HeartbeatResponse
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
name|NodeAction
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
name|NMLivelinessMonitor
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
name|NodesListManager
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
name|ResourceTrackerService
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|TestRMNMRPCResponseId
specifier|public
class|class
name|TestRMNMRPCResponseId
block|{
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
DECL|field|resourceTrackerService
name|ResourceTrackerService
name|resourceTrackerService
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
comment|// Dispatcher that processes events inline
name|Dispatcher
name|dispatcher
init|=
operator|new
name|InlineDispatcher
argument_list|()
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|SchedulerEventType
operator|.
name|class
argument_list|,
operator|new
name|EventHandler
argument_list|<
name|Event
argument_list|>
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
block|{
empty_stmt|;
comment|// ignore
block|}
block|}
argument_list|)
expr_stmt|;
name|RMContext
name|context
init|=
operator|new
name|RMContextImpl
argument_list|(
name|dispatcher
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
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|RMNodeEventType
operator|.
name|class
argument_list|,
operator|new
name|ResourceManager
operator|.
name|NodeEventDispatcher
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|NodesListManager
name|nodesListManager
init|=
operator|new
name|NodesListManager
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|nodesListManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|RMContainerTokenSecretManager
name|containerTokenSecretManager
init|=
operator|new
name|RMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|resourceTrackerService
operator|=
operator|new
name|ResourceTrackerService
argument_list|(
name|context
argument_list|,
name|nodesListManager
argument_list|,
operator|new
name|NMLivelinessMonitor
argument_list|(
name|dispatcher
argument_list|)
argument_list|,
name|containerTokenSecretManager
argument_list|)
expr_stmt|;
name|resourceTrackerService
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
block|{
comment|/* do nothing */
block|}
annotation|@
name|Test
DECL|method|testRPCResponseId ()
specifier|public
name|void
name|testRPCResponseId
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|node
init|=
literal|"localhost"
decl_stmt|;
name|Resource
name|capability
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|RegisterNodeManagerRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterNodeManagerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeId
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
expr_stmt|;
name|nodeId
operator|.
name|setHost
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|nodeId
operator|.
name|setPort
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setHttpPort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|request
operator|.
name|setResource
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|RegisterNodeManagerRequest
name|request1
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterNodeManagerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request1
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|request1
operator|.
name|setHttpPort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|request1
operator|.
name|setResource
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|resourceTrackerService
operator|.
name|registerNodeManager
argument_list|(
name|request1
argument_list|)
expr_stmt|;
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
name|NodeStatus
name|nodeStatus
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
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
name|NodeStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeStatus
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|NodeHealthStatus
name|nodeHealthStatus
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHealthStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeHealthStatus
operator|.
name|setIsNodeHealthy
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|setNodeHealthStatus
argument_list|(
name|nodeHealthStatus
argument_list|)
expr_stmt|;
name|NodeHeartbeatRequest
name|nodeHeartBeatRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHeartbeatRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeHeartBeatRequest
operator|.
name|setNodeStatus
argument_list|(
name|nodeStatus
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|setResponseId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|HeartbeatResponse
name|response
init|=
name|resourceTrackerService
operator|.
name|nodeHeartbeat
argument_list|(
name|nodeHeartBeatRequest
argument_list|)
operator|.
name|getHeartbeatResponse
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|getResponseId
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|setResponseId
argument_list|(
name|response
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|=
name|resourceTrackerService
operator|.
name|nodeHeartbeat
argument_list|(
name|nodeHeartBeatRequest
argument_list|)
operator|.
name|getHeartbeatResponse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|getResponseId
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|/* try calling with less response id */
name|response
operator|=
name|resourceTrackerService
operator|.
name|nodeHeartbeat
argument_list|(
name|nodeHeartBeatRequest
argument_list|)
operator|.
name|getHeartbeatResponse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|getResponseId
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|setResponseId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|response
operator|=
name|resourceTrackerService
operator|.
name|nodeHeartbeat
argument_list|(
name|nodeHeartBeatRequest
argument_list|)
operator|.
name|getHeartbeatResponse
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|REBOOT
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getNodeAction
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

