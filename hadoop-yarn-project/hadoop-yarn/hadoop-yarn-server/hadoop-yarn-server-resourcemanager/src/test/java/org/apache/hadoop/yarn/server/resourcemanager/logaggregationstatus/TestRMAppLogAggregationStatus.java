begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.logaggregationstatus
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
name|logaggregationstatus
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
name|mock
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ApplicationSubmissionContext
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
name|LogAggregationReport
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
name|LogAggregationStatus
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
name|ahs
operator|.
name|RMApplicationHistoryWriter
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
name|metrics
operator|.
name|SystemMetricsPublisher
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
name|RMAppEvent
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
name|RMAppEventType
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
name|RMAppImpl
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
name|RMAppRunningOnNodeEvent
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
name|RMAppState
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
name|RMNodeStartedEvent
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
DECL|class|TestRMAppLogAggregationStatus
specifier|public
class|class
name|TestRMAppLogAggregationStatus
block|{
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
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
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
name|RMApplicationHistoryWriter
argument_list|()
argument_list|)
expr_stmt|;
name|rmContext
operator|.
name|setSystemMetricsPublisher
argument_list|(
operator|new
name|SystemMetricsPublisher
argument_list|()
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
comment|//DO NOTHING
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
name|appId
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|1
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
annotation|@
name|Test
DECL|method|testLogAggregationStatus ()
specifier|public
name|void
name|testLogAggregationStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_STATUS_TIME_OUT_MS
argument_list|,
literal|1500
argument_list|)
expr_stmt|;
name|RMApp
name|rmApp
init|=
name|createRMApp
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|rmApp
argument_list|)
expr_stmt|;
name|rmApp
operator|.
name|handle
argument_list|(
operator|new
name|RMAppEvent
argument_list|(
name|this
operator|.
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
name|rmApp
operator|.
name|handle
argument_list|(
operator|new
name|RMAppEvent
argument_list|(
name|this
operator|.
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|APP_NEW_SAVED
argument_list|)
argument_list|)
expr_stmt|;
name|rmApp
operator|.
name|handle
argument_list|(
operator|new
name|RMAppEvent
argument_list|(
name|this
operator|.
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|APP_ACCEPTED
argument_list|)
argument_list|)
expr_stmt|;
comment|// This application will be running on two nodes
name|NodeId
name|nodeId1
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|Resource
name|capability
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|4096
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|RMNodeImpl
name|node1
init|=
operator|new
name|RMNodeImpl
argument_list|(
name|nodeId1
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
name|capability
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|node1
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeStartedEvent
argument_list|(
name|nodeId1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|rmApp
operator|.
name|handle
argument_list|(
operator|new
name|RMAppRunningOnNodeEvent
argument_list|(
name|this
operator|.
name|appId
argument_list|,
name|nodeId1
argument_list|)
argument_list|)
expr_stmt|;
name|NodeId
name|nodeId2
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|2345
argument_list|)
decl_stmt|;
name|RMNodeImpl
name|node2
init|=
operator|new
name|RMNodeImpl
argument_list|(
name|nodeId2
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
name|capability
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|node2
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeStartedEvent
argument_list|(
name|node2
operator|.
name|getNodeID
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|rmApp
operator|.
name|handle
argument_list|(
operator|new
name|RMAppRunningOnNodeEvent
argument_list|(
name|this
operator|.
name|appId
argument_list|,
name|nodeId2
argument_list|)
argument_list|)
expr_stmt|;
comment|// The initial log aggregation status for these two nodes
comment|// should be NOT_STARTED
name|Map
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|logAggregationStatus
init|=
name|rmApp
operator|.
name|getLogAggregationReportsForApp
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|logAggregationStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|report
range|:
name|logAggregationStatus
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LogAggregationStatus
operator|.
name|NOT_START
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|LogAggregationReport
argument_list|>
name|node1ReportForApp
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|LogAggregationReport
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|messageForNode1_1
init|=
literal|"node1 logAggregation status updated at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LogAggregationReport
name|report1
init|=
name|LogAggregationReport
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|nodeId1
argument_list|,
name|LogAggregationStatus
operator|.
name|RUNNING
argument_list|,
name|messageForNode1_1
argument_list|)
decl_stmt|;
name|node1ReportForApp
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|report1
argument_list|)
expr_stmt|;
name|node1
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeStatusEvent
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|NodeHealthStatus
operator|.
name|newInstance
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
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
argument_list|,
name|node1ReportForApp
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|LogAggregationReport
argument_list|>
name|node2ReportForApp
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|LogAggregationReport
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|messageForNode2_1
init|=
literal|"node2 logAggregation status updated at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LogAggregationReport
name|report2
init|=
name|LogAggregationReport
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|nodeId2
argument_list|,
name|LogAggregationStatus
operator|.
name|RUNNING
argument_list|,
name|messageForNode2_1
argument_list|)
decl_stmt|;
name|node2ReportForApp
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|report2
argument_list|)
expr_stmt|;
name|node2
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeStatusEvent
argument_list|(
name|node2
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|NodeHealthStatus
operator|.
name|newInstance
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
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
argument_list|,
name|node2ReportForApp
argument_list|)
argument_list|)
expr_stmt|;
comment|// node1 and node2 has updated its log aggregation status
comment|// verify that the log aggregation status for node1, node2
comment|// has been changed
name|logAggregationStatus
operator|=
name|rmApp
operator|.
name|getLogAggregationReportsForApp
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|logAggregationStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|report
range|:
name|logAggregationStatus
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|report
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LogAggregationStatus
operator|.
name|RUNNING
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|messageForNode1_1
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getDiagnosticMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|report
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|node2
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LogAggregationStatus
operator|.
name|RUNNING
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|messageForNode2_1
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getDiagnosticMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// should not contain log aggregation report for other nodes
name|Assert
operator|.
name|fail
argument_list|(
literal|"should not contain log aggregation report for other nodes"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// node1 updates its log aggregation status again
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|LogAggregationReport
argument_list|>
name|node1ReportForApp2
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|LogAggregationReport
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|messageForNode1_2
init|=
literal|"node1 logAggregation status updated at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LogAggregationReport
name|report1_2
init|=
name|LogAggregationReport
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|nodeId1
argument_list|,
name|LogAggregationStatus
operator|.
name|RUNNING
argument_list|,
name|messageForNode1_2
argument_list|)
decl_stmt|;
name|node1ReportForApp2
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|report1_2
argument_list|)
expr_stmt|;
name|node1
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeStatusEvent
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|NodeHealthStatus
operator|.
name|newInstance
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
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
argument_list|,
name|node1ReportForApp2
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that the log aggregation status for node1
comment|// has been changed
comment|// verify that the log aggregation status for node2
comment|// does not change
name|logAggregationStatus
operator|=
name|rmApp
operator|.
name|getLogAggregationReportsForApp
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|logAggregationStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|report
range|:
name|logAggregationStatus
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|report
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LogAggregationStatus
operator|.
name|RUNNING
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|messageForNode1_1
operator|+
name|messageForNode1_2
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getDiagnosticMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|report
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|node2
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LogAggregationStatus
operator|.
name|RUNNING
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|messageForNode2_1
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getDiagnosticMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// should not contain log aggregation report for other nodes
name|Assert
operator|.
name|fail
argument_list|(
literal|"should not contain log aggregation report for other nodes"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// kill the application
name|rmApp
operator|.
name|handle
argument_list|(
operator|new
name|RMAppEvent
argument_list|(
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
argument_list|)
expr_stmt|;
name|rmApp
operator|.
name|handle
argument_list|(
operator|new
name|RMAppEvent
argument_list|(
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_KILLED
argument_list|)
argument_list|)
expr_stmt|;
name|rmApp
operator|.
name|handle
argument_list|(
operator|new
name|RMAppEvent
argument_list|(
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|APP_UPDATE_SAVED
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|rmApp
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// wait for 1500 ms
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
comment|// the log aggregation status for both nodes should be changed
comment|// to TIME_OUT
name|logAggregationStatus
operator|=
name|rmApp
operator|.
name|getLogAggregationReportsForApp
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|logAggregationStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|report
range|:
name|logAggregationStatus
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LogAggregationStatus
operator|.
name|TIME_OUT
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Finally, node1 finished its log aggregation and sent out its final
comment|// log aggregation status. The log aggregation status for node1 should
comment|// be changed from TIME_OUT to Finished
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|LogAggregationReport
argument_list|>
name|node1ReportForApp3
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|LogAggregationReport
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|messageForNode1_3
init|=
literal|"node1 final logAggregation status updated at "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LogAggregationReport
name|report1_3
init|=
name|LogAggregationReport
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
name|nodeId1
argument_list|,
name|LogAggregationStatus
operator|.
name|FINISHED
argument_list|,
name|messageForNode1_3
argument_list|)
decl_stmt|;
name|node1ReportForApp3
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|report1_3
argument_list|)
expr_stmt|;
name|node1
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeStatusEvent
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|NodeHealthStatus
operator|.
name|newInstance
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
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
argument_list|,
name|node1ReportForApp3
argument_list|)
argument_list|)
expr_stmt|;
name|logAggregationStatus
operator|=
name|rmApp
operator|.
name|getLogAggregationReportsForApp
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|logAggregationStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|logAggregationStatus
operator|.
name|containsKey
argument_list|(
name|nodeId2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|LogAggregationReport
argument_list|>
name|report
range|:
name|logAggregationStatus
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|report
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LogAggregationStatus
operator|.
name|FINISHED
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|messageForNode1_1
operator|+
name|messageForNode1_2
operator|+
name|messageForNode1_3
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getDiagnosticMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|report
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|node2
operator|.
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LogAggregationStatus
operator|.
name|TIME_OUT
argument_list|,
name|report
operator|.
name|getValue
argument_list|()
operator|.
name|getLogAggregationStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// should not contain log aggregation report for other nodes
name|Assert
operator|.
name|fail
argument_list|(
literal|"should not contain log aggregation report for other nodes"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createRMApp (Configuration conf)
specifier|private
name|RMApp
name|createRMApp
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ApplicationSubmissionContext
name|submissionContext
init|=
name|ApplicationSubmissionContext
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|"test"
argument_list|,
literal|"default"
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|10
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
return|return
operator|new
name|RMAppImpl
argument_list|(
name|this
operator|.
name|appId
argument_list|,
name|this
operator|.
name|rmContext
argument_list|,
name|conf
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"default"
argument_list|,
name|submissionContext
argument_list|,
name|this
operator|.
name|rmContext
operator|.
name|getScheduler
argument_list|()
argument_list|,
name|this
operator|.
name|rmContext
operator|.
name|getApplicationMasterService
argument_list|()
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

