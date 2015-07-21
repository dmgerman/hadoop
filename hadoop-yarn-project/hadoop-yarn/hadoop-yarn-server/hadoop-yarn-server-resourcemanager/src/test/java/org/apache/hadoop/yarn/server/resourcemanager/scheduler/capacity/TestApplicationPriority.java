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
name|exceptions
operator|.
name|ApplicationIdNotProvidedException
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
name|exceptions
operator|.
name|YarnException
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
name|attempt
operator|.
name|RMAppAttemptImpl
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
name|RMAppAttemptMetrics
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
name|SchedulerNodeReport
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
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerApp
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
name|AppAttemptAddedSchedulerEvent
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

begin_class
DECL|class|TestApplicationPriority
specifier|public
class|class
name|TestApplicationPriority
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
name|TestApplicationPriority
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
block|}
annotation|@
name|Test
DECL|method|testApplicationOrderingWithPriority ()
specifier|public
name|void
name|testApplicationOrderingWithPriority
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
name|CapacityScheduler
name|cs
init|=
operator|(
name|CapacityScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|LeafQueue
name|q
init|=
operator|(
name|LeafQueue
operator|)
name|cs
operator|.
name|getQueue
argument_list|(
literal|"default"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|String
name|host
init|=
literal|"127.0.0.1"
decl_stmt|;
name|RMNode
name|node
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
literal|16
operator|*
name|GB
argument_list|)
argument_list|,
literal|1
argument_list|,
name|host
argument_list|)
decl_stmt|;
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
comment|// add app 1 start
name|ApplicationId
name|appId1
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId1
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RMAppAttemptMetrics
name|attemptMetric1
init|=
operator|new
name|RMAppAttemptMetrics
argument_list|(
name|appAttemptId1
argument_list|,
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
decl_stmt|;
name|RMAppImpl
name|app1
init|=
name|mock
argument_list|(
name|RMAppImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId1
argument_list|)
expr_stmt|;
name|RMAppAttemptImpl
name|attempt1
init|=
name|mock
argument_list|(
name|RMAppAttemptImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appAttemptId1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|attempt1
operator|.
name|getRMAppAttemptMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptMetric1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|app1
operator|.
name|getCurrentAppAttempt
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attempt1
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|put
argument_list|(
name|appId1
argument_list|,
name|app1
argument_list|)
expr_stmt|;
name|SchedulerEvent
name|addAppEvent1
init|=
operator|new
name|AppAddedSchedulerEvent
argument_list|(
name|appId1
argument_list|,
literal|"default"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|cs
operator|.
name|handle
argument_list|(
name|addAppEvent1
argument_list|)
expr_stmt|;
name|SchedulerEvent
name|addAttemptEvent1
init|=
operator|new
name|AppAttemptAddedSchedulerEvent
argument_list|(
name|appAttemptId1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cs
operator|.
name|handle
argument_list|(
name|addAttemptEvent1
argument_list|)
expr_stmt|;
comment|// add app1 end
comment|// add app2 begin
name|ApplicationId
name|appId2
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|100
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId2
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RMAppAttemptMetrics
name|attemptMetric2
init|=
operator|new
name|RMAppAttemptMetrics
argument_list|(
name|appAttemptId2
argument_list|,
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
decl_stmt|;
name|RMAppImpl
name|app2
init|=
name|mock
argument_list|(
name|RMAppImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|app2
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appId2
argument_list|)
expr_stmt|;
name|RMAppAttemptImpl
name|attempt2
init|=
name|mock
argument_list|(
name|RMAppAttemptImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appAttemptId2
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|attempt2
operator|.
name|getRMAppAttemptMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptMetric2
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|app2
operator|.
name|getCurrentAppAttempt
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attempt2
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|put
argument_list|(
name|appId2
argument_list|,
name|app2
argument_list|)
expr_stmt|;
name|SchedulerEvent
name|addAppEvent2
init|=
operator|new
name|AppAddedSchedulerEvent
argument_list|(
name|appId2
argument_list|,
literal|"default"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|8
argument_list|)
argument_list|)
decl_stmt|;
name|cs
operator|.
name|handle
argument_list|(
name|addAppEvent2
argument_list|)
expr_stmt|;
name|SchedulerEvent
name|addAttemptEvent2
init|=
operator|new
name|AppAttemptAddedSchedulerEvent
argument_list|(
name|appAttemptId2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cs
operator|.
name|handle
argument_list|(
name|addAttemptEvent2
argument_list|)
expr_stmt|;
comment|// add app end
comment|// Now, the first assignment will be for app2 since app2 is of highest
comment|// priority
name|assertEquals
argument_list|(
name|q
operator|.
name|getApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
operator|.
name|getApplications
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|appAttemptId2
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testApplicationPriorityAllocation ()
specifier|public
name|void
name|testApplicationPriorityAllocation
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
comment|// Set Max Application Priority as 10
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
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
name|Priority
name|appPriority1
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|16
operator|*
name|GB
argument_list|)
decl_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|appPriority1
argument_list|)
decl_stmt|;
comment|// kick the scheduler, 1 GB given to AM1, remaining 15GB on nm1
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|am1
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|// add request for containers
name|am1
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.1"
block|,
literal|"127.0.0.2"
block|}
argument_list|,
literal|2
operator|*
name|GB
argument_list|,
literal|1
argument_list|,
literal|7
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
comment|// kick the scheduler, 7 containers will be allocated for App1
name|nm1
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
literal|100
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
name|List
argument_list|<
name|Container
argument_list|>
name|allocated1
init|=
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|allocated1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|allocated1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// check node report, 15 GB used (1 AM and 7 containers) and 1 GB available
name|SchedulerNodeReport
name|report_nm1
init|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|15
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// Submit the second app App2 with priority 8 (Higher than App1)
name|Priority
name|appPriority2
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|RMApp
name|app2
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|appPriority2
argument_list|)
decl_stmt|;
comment|// kick the scheduler, 1 GB which was free is given to AM of App2
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MockAM
name|am2
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|app2
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am2
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|// check node report, 16 GB used and 0 GB available
name|report_nm1
operator|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|16
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// get scheduler
name|CapacityScheduler
name|cs
init|=
operator|(
name|CapacityScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
comment|// get scheduler app
name|FiCaSchedulerApp
name|schedulerAppAttempt
init|=
name|cs
operator|.
name|getSchedulerApplications
argument_list|()
operator|.
name|get
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
comment|// kill 2 containers to free up some space
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Container
name|c
range|:
name|allocated1
control|)
block|{
if|if
condition|(
operator|++
name|counter
operator|>
literal|2
condition|)
block|{
break|break;
block|}
name|cs
operator|.
name|killContainer
argument_list|(
name|schedulerAppAttempt
operator|.
name|getRMContainer
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check node report, 12 GB used and 4 GB available
name|report_nm1
operator|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|12
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
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
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// add request for containers App1
name|am1
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.1"
block|,
literal|"127.0.0.2"
block|}
argument_list|,
literal|2
operator|*
name|GB
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|am1
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// send the request for App1
comment|// add request for containers App2
name|am2
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.1"
block|,
literal|"127.0.0.2"
block|}
argument_list|,
literal|2
operator|*
name|GB
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|AllocateResponse
name|alloc1Response4
init|=
name|am2
operator|.
name|schedule
argument_list|()
decl_stmt|;
comment|// send the request
comment|// kick the scheduler, since App2 priority is more than App1, it will get
comment|// remaining cluster space.
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|alloc1Response4
operator|.
name|getAllocatedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for containers to be created for app 2..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|alloc1Response4
operator|=
name|am2
operator|.
name|schedule
argument_list|()
expr_stmt|;
block|}
comment|// check node report, 16 GB used and 0 GB available
name|report_nm1
operator|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|16
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPriorityWithPendingApplications ()
specifier|public
name|void
name|testPriorityWithPendingApplications
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
comment|// Set Max Application Priority as 10
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
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
name|Priority
name|appPriority1
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|8
operator|*
name|GB
argument_list|)
decl_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|appPriority1
argument_list|)
decl_stmt|;
comment|// kick the scheduler, 1 GB given to AM1, remaining 7GB on nm1
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|am1
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|// add request for containers
name|am1
operator|.
name|addRequests
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"127.0.0.1"
block|,
literal|"127.0.0.2"
block|}
argument_list|,
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|,
literal|7
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
comment|// kick the scheduler, 7 containers will be allocated for App1
name|nm1
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
literal|100
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
name|List
argument_list|<
name|Container
argument_list|>
name|allocated1
init|=
name|alloc1Response
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|allocated1
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
operator|*
name|GB
argument_list|,
name|allocated1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// check node report, 8 GB used (1 AM and 7 containers) and 0 GB available
name|SchedulerNodeReport
name|report_nm1
init|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
comment|// Submit the second app App2 with priority 7
name|Priority
name|appPriority2
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|RMApp
name|app2
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|appPriority2
argument_list|)
decl_stmt|;
comment|// Submit the third app App3 with priority 8
name|Priority
name|appPriority3
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|RMApp
name|app3
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|appPriority3
argument_list|)
decl_stmt|;
comment|// Submit the second app App4 with priority 6
name|Priority
name|appPriority4
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|RMApp
name|app4
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|appPriority4
argument_list|)
decl_stmt|;
comment|// Only one app can run as AM resource limit restricts it. Kill app1,
comment|// If app3 (highest priority among rest) gets active, it indicates that
comment|// priority is working with pendingApplications.
name|rm
operator|.
name|killApp
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// kick the scheduler, app3 (high among pending) gets free space
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MockAM
name|am3
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|app3
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am3
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|// check node report, 1 GB used and 7 GB available
name|report_nm1
operator|=
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getNodeReport
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|7
operator|*
name|GB
argument_list|,
name|report_nm1
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxPriorityValidation ()
specifier|public
name|void
name|testMaxPriorityValidation
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
comment|// Set Max Application Priority as 10
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|MAX_CLUSTER_LEVEL_APPLICATION_PRIORITY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|Priority
name|maxPriority
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|10
argument_list|)
decl_stmt|;
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
name|Priority
name|appPriority1
init|=
name|Priority
operator|.
name|newInstance
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|8
operator|*
name|GB
argument_list|)
expr_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|appPriority1
argument_list|)
decl_stmt|;
comment|// Application submission should be successful and verify priority
name|Assert
operator|.
name|assertEquals
argument_list|(
name|app1
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|,
name|maxPriority
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

