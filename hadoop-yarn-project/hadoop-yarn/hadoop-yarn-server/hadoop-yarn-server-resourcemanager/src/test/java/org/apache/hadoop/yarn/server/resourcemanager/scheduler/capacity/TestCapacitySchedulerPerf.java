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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
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
name|util
operator|.
name|Time
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
name|ResourceTypes
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
name|ResourceInformation
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
name|resource
operator|.
name|DominantResourceCalculator
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
name|resource
operator|.
name|ResourceUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|PriorityQueue
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
name|server
operator|.
name|resourcemanager
operator|.
name|resource
operator|.
name|TestResourceProfiles
operator|.
name|TEST_CONF_RESET_RESOURCE_TYPES
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
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertTrue
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

begin_class
DECL|class|TestCapacitySchedulerPerf
specifier|public
class|class
name|TestCapacitySchedulerPerf
block|{
DECL|field|GB
specifier|private
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
DECL|method|getResourceName (int idx)
specifier|private
name|String
name|getResourceName
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
literal|"resource-"
operator|+
name|idx
return|;
block|}
comment|// This test is run only when when -DRunCapacitySchedulerPerfTests=true is set
comment|// on the command line. In addition, this test has tunables for the following:
comment|//   Number of queues: -DNumberOfQueues (default=100)
comment|//   Number of total apps: -DNumberOfApplications (default=200)
comment|//   Percentage of queues with apps: -DPercentActiveQueues (default=100)
comment|// E.G.:
comment|// mvn test -Dtest=TestCapacitySchedulerPerf -Dsurefire.fork.timeout=1800 \
comment|//    -DRunCapacitySchedulerPerfTests=true -DNumberOfQueues=50 \
comment|//    -DNumberOfApplications=200 -DPercentActiveQueues=100
comment|// Note that the surefire.fork.timeout flag is added because these tests could
comment|// take longer than the surefire timeout.
DECL|method|testUserLimitThroughputWithNumberOfResourceTypes ( int numOfResourceTypes, int numQueues, int pctActiveQueues, int appCount)
specifier|private
name|void
name|testUserLimitThroughputWithNumberOfResourceTypes
parameter_list|(
name|int
name|numOfResourceTypes
parameter_list|,
name|int
name|numQueues
parameter_list|,
name|int
name|pctActiveQueues
parameter_list|,
name|int
name|appCount
parameter_list|)
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"RunCapacitySchedulerPerfTests"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|numOfResourceTypes
operator|>
literal|2
condition|)
block|{
comment|// Initialize resource map
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceInformation
argument_list|>
name|riMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Initialize mandatory resources
name|riMap
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
argument_list|)
expr_stmt|;
name|riMap
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|VCORES_URI
argument_list|,
name|ResourceInformation
operator|.
name|VCORES
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<
name|numOfResourceTypes
condition|;
name|i
operator|++
control|)
block|{
name|String
name|resourceName
init|=
name|getResourceName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|riMap
operator|.
name|put
argument_list|(
name|resourceName
argument_list|,
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|resourceName
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|,
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|,
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ResourceUtils
operator|.
name|initializeResourcesFromResourceInformationMap
argument_list|(
name|riMap
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|activeQueues
init|=
call|(
name|int
call|)
argument_list|(
name|numQueues
operator|*
operator|(
name|pctActiveQueues
operator|/
literal|100f
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|totalApps
init|=
name|appCount
operator|+
name|activeQueues
decl_stmt|;
comment|// extra apps to get started with user limit
name|CapacitySchedulerConfiguration
name|csconf
init|=
name|createCSConfWithManyQueues
argument_list|(
name|numQueues
argument_list|)
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|csconf
argument_list|)
decl_stmt|;
comment|// Don't reset resource types since we have already configured resource
comment|// types
name|conf
operator|.
name|setBoolean
argument_list|(
name|TEST_CONF_RESET_RESOURCE_TYPES
argument_list|,
literal|false
argument_list|)
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
index|[]
name|lqs
init|=
operator|new
name|LeafQueue
index|[
name|numQueues
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numQueues
condition|;
name|i
operator|++
control|)
block|{
name|String
name|queueName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%03d"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|LeafQueue
name|qb
init|=
operator|(
name|LeafQueue
operator|)
name|cs
operator|.
name|getQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
comment|// For now make user limit large so we can activate all applications
name|qb
operator|.
name|setUserLimitFactor
argument_list|(
operator|(
name|float
operator|)
literal|100.0
argument_list|)
expr_stmt|;
name|qb
operator|.
name|setupConfigurableCapacities
argument_list|()
expr_stmt|;
name|lqs
index|[
name|i
index|]
operator|=
name|qb
expr_stmt|;
block|}
name|SchedulerEvent
name|addAppEvent
decl_stmt|;
name|SchedulerEvent
name|addAttemptEvent
decl_stmt|;
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
name|ApplicationSubmissionContext
name|submissionContext
init|=
name|mock
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationId
index|[]
name|appids
init|=
operator|new
name|ApplicationId
index|[
name|totalApps
index|]
decl_stmt|;
name|RMAppAttemptImpl
index|[]
name|attempts
init|=
operator|new
name|RMAppAttemptImpl
index|[
name|totalApps
index|]
decl_stmt|;
name|ApplicationAttemptId
index|[]
name|appAttemptIds
init|=
operator|new
name|ApplicationAttemptId
index|[
name|totalApps
index|]
decl_stmt|;
name|RMAppImpl
index|[]
name|apps
init|=
operator|new
name|RMAppImpl
index|[
name|totalApps
index|]
decl_stmt|;
name|RMAppAttemptMetrics
index|[]
name|attemptMetrics
init|=
operator|new
name|RMAppAttemptMetrics
index|[
name|totalApps
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|totalApps
condition|;
name|i
operator|++
control|)
block|{
name|appids
index|[
name|i
index|]
operator|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|100
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|appAttemptIds
index|[
name|i
index|]
operator|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appids
index|[
name|i
index|]
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|attemptMetrics
index|[
name|i
index|]
operator|=
operator|new
name|RMAppAttemptMetrics
argument_list|(
name|appAttemptIds
index|[
name|i
index|]
argument_list|,
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|)
expr_stmt|;
name|apps
index|[
name|i
index|]
operator|=
name|mock
argument_list|(
name|RMAppImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|apps
index|[
name|i
index|]
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appids
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|attempts
index|[
name|i
index|]
operator|=
name|mock
argument_list|(
name|RMAppAttemptImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|attempts
index|[
name|i
index|]
operator|.
name|getMasterContainer
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|attempts
index|[
name|i
index|]
operator|.
name|getSubmissionContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|submissionContext
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|attempts
index|[
name|i
index|]
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|appAttemptIds
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|attempts
index|[
name|i
index|]
operator|.
name|getRMAppAttemptMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attemptMetrics
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|apps
index|[
name|i
index|]
operator|.
name|getCurrentAppAttempt
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|attempts
index|[
name|i
index|]
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
name|appids
index|[
name|i
index|]
argument_list|,
name|apps
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|String
name|queueName
init|=
name|lqs
index|[
name|i
operator|%
name|activeQueues
index|]
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|addAppEvent
operator|=
operator|new
name|AppAddedSchedulerEvent
argument_list|(
name|appids
index|[
name|i
index|]
argument_list|,
name|queueName
argument_list|,
literal|"user1"
argument_list|)
expr_stmt|;
name|cs
operator|.
name|handle
argument_list|(
name|addAppEvent
argument_list|)
expr_stmt|;
name|addAttemptEvent
operator|=
operator|new
name|AppAttemptAddedSchedulerEvent
argument_list|(
name|appAttemptIds
index|[
name|i
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cs
operator|.
name|handle
argument_list|(
name|addAttemptEvent
argument_list|)
expr_stmt|;
block|}
comment|// add nodes to cluster with enough resources to satisfy all apps
name|Resource
name|newResource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|totalApps
operator|*
name|GB
argument_list|,
name|totalApps
argument_list|)
decl_stmt|;
if|if
condition|(
name|numOfResourceTypes
operator|>
literal|2
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<
name|numOfResourceTypes
condition|;
name|i
operator|++
control|)
block|{
name|newResource
operator|.
name|setResourceValue
argument_list|(
name|getResourceName
argument_list|(
name|i
argument_list|)
argument_list|,
name|totalApps
argument_list|)
expr_stmt|;
block|}
block|}
name|RMNode
name|node
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|0
argument_list|,
name|newResource
argument_list|,
literal|1
argument_list|,
literal|"127.0.0.1"
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
name|RMNode
name|node2
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|0
argument_list|,
name|newResource
argument_list|,
literal|1
argument_list|,
literal|"127.0.0.2"
argument_list|)
decl_stmt|;
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|node2
argument_list|)
argument_list|)
expr_stmt|;
name|Priority
name|u0Priority
init|=
name|TestUtils
operator|.
name|createMockPriority
argument_list|(
literal|1
argument_list|)
decl_stmt|;
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
name|FiCaSchedulerApp
index|[]
name|fiCaApps
init|=
operator|new
name|FiCaSchedulerApp
index|[
name|totalApps
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|totalApps
condition|;
name|i
operator|++
control|)
block|{
name|fiCaApps
index|[
name|i
index|]
operator|=
name|cs
operator|.
name|getSchedulerApplications
argument_list|()
operator|.
name|get
argument_list|(
name|apps
index|[
name|i
index|]
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getCurrentAppAttempt
argument_list|()
expr_stmt|;
name|ResourceRequest
name|resourceRequest
init|=
name|TestUtils
operator|.
name|createResourceRequest
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|,
literal|1
operator|*
name|GB
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
name|u0Priority
argument_list|,
name|recordFactory
argument_list|)
decl_stmt|;
if|if
condition|(
name|numOfResourceTypes
operator|>
literal|2
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|2
init|;
name|j
operator|<
name|numOfResourceTypes
condition|;
name|j
operator|++
control|)
block|{
name|resourceRequest
operator|.
name|getCapability
argument_list|()
operator|.
name|setResourceValue
argument_list|(
name|getResourceName
argument_list|(
name|j
argument_list|)
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
comment|// allocate container for app2 with 1GB memory and 1 vcore
name|fiCaApps
index|[
name|i
index|]
operator|.
name|updateResourceRequests
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|resourceRequest
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Now force everything to be at user limit
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numQueues
condition|;
name|i
operator|++
control|)
block|{
name|lqs
index|[
name|i
index|]
operator|.
name|setUserLimitFactor
argument_list|(
operator|(
name|float
operator|)
literal|0.0
argument_list|)
expr_stmt|;
block|}
comment|// allocate one container for each extra apps since
comment|//  LeafQueue.canAssignToUser() checks for used> limit, not used>= limit
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|node2
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure only the extra apps have allocated containers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|totalApps
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|pending
init|=
name|fiCaApps
index|[
name|i
index|]
operator|.
name|getAppSchedulingInfo
argument_list|()
operator|.
name|isPending
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|activeQueues
condition|)
block|{
name|assertFalse
argument_list|(
name|pending
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fiCaApps
index|[
name|i
index|]
operator|.
name|getTotalPendingRequestsPerPartition
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|pending
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|fiCaApps
index|[
name|i
index|]
operator|.
name|getTotalPendingRequestsPerPartition
argument_list|()
operator|.
name|get
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Quiet the loggers while measuring throughput
name|GenericTestUtils
operator|.
name|setRootLogLevel
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
expr_stmt|;
specifier|final
name|int
name|topn
init|=
literal|20
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
literal|2000000
decl_stmt|;
specifier|final
name|int
name|printInterval
init|=
literal|20000
decl_stmt|;
specifier|final
name|float
name|numerator
init|=
literal|1000.0f
operator|*
name|printInterval
decl_stmt|;
name|PriorityQueue
argument_list|<
name|Long
argument_list|>
name|queue
init|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|(
name|topn
argument_list|,
name|Collections
operator|.
name|reverseOrder
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|n
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|long
name|timespent
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
name|i
operator|+=
literal|2
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
name|printInterval
operator|==
literal|0
condition|)
block|{
name|long
name|ts
init|=
operator|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|n
operator|)
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|<
name|topn
condition|)
block|{
name|queue
operator|.
name|offer
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Long
name|last
init|=
name|queue
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|>
name|ts
condition|)
block|{
name|queue
operator|.
name|poll
argument_list|()
expr_stmt|;
name|queue
operator|.
name|offer
argument_list|(
name|ts
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" "
operator|+
operator|(
name|numerator
operator|/
name|ts
operator|)
argument_list|)
expr_stmt|;
name|n
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|node2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|timespent
operator|=
literal|0
expr_stmt|;
name|int
name|entries
init|=
name|queue
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|long
name|l
init|=
name|queue
operator|.
name|poll
argument_list|()
decl_stmt|;
name|timespent
operator|+=
name|l
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"#ResourceTypes = "
operator|+
name|numOfResourceTypes
operator|+
literal|". Avg of fastest "
operator|+
name|entries
operator|+
literal|": "
operator|+
name|numerator
operator|/
operator|(
name|timespent
operator|/
name|entries
operator|)
operator|+
literal|" ops/sec of "
operator|+
name|appCount
operator|+
literal|" apps on "
operator|+
name|pctActiveQueues
operator|+
literal|"% of "
operator|+
name|numQueues
operator|+
literal|" queues."
argument_list|)
expr_stmt|;
comment|// make sure only the extra apps have allocated containers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|totalApps
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|pending
init|=
name|fiCaApps
index|[
name|i
index|]
operator|.
name|getAppSchedulingInfo
argument_list|()
operator|.
name|isPending
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|activeQueues
condition|)
block|{
name|assertFalse
argument_list|(
name|pending
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fiCaApps
index|[
name|i
index|]
operator|.
name|getTotalPendingRequestsPerPartition
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|pending
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
name|fiCaApps
index|[
name|i
index|]
operator|.
name|getTotalPendingRequestsPerPartition
argument_list|()
operator|.
name|get
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|rm
operator|.
name|close
argument_list|()
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testUserLimitThroughputForTwoResources ()
specifier|public
name|void
name|testUserLimitThroughputForTwoResources
parameter_list|()
throws|throws
name|Exception
block|{
name|testUserLimitThroughputWithNumberOfResourceTypes
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testUserLimitThroughputForThreeResources ()
specifier|public
name|void
name|testUserLimitThroughputForThreeResources
parameter_list|()
throws|throws
name|Exception
block|{
name|testUserLimitThroughputWithNumberOfResourceTypes
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testUserLimitThroughputForFourResources ()
specifier|public
name|void
name|testUserLimitThroughputForFourResources
parameter_list|()
throws|throws
name|Exception
block|{
name|testUserLimitThroughputWithNumberOfResourceTypes
argument_list|(
literal|4
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testUserLimitThroughputForFiveResources ()
specifier|public
name|void
name|testUserLimitThroughputForFiveResources
parameter_list|()
throws|throws
name|Exception
block|{
name|testUserLimitThroughputWithNumberOfResourceTypes
argument_list|(
literal|5
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1800000
argument_list|)
DECL|method|testUserLimitThroughputWithManyQueues ()
specifier|public
name|void
name|testUserLimitThroughputWithManyQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numQueues
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"NumberOfQueues"
argument_list|,
literal|40
argument_list|)
decl_stmt|;
name|int
name|pctActiveQueues
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"PercentActiveQueues"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|appCount
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"NumberOfApplications"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|testUserLimitThroughputWithNumberOfResourceTypes
argument_list|(
literal|2
argument_list|,
name|numQueues
argument_list|,
name|pctActiveQueues
argument_list|,
name|appCount
argument_list|)
expr_stmt|;
block|}
DECL|method|createCSConfWithManyQueues (int numQueues)
name|CapacitySchedulerConfiguration
name|createCSConfWithManyQueues
parameter_list|(
name|int
name|numQueues
parameter_list|)
throws|throws
name|Exception
block|{
name|CapacitySchedulerConfiguration
name|csconf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|csconf
operator|.
name|setResourceComparator
argument_list|(
name|DominantResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setMaximumApplicationMasterResourcePerQueuePercent
argument_list|(
literal|"root"
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setMaximumAMResourcePercentPerPartition
argument_list|(
literal|"root"
argument_list|,
literal|""
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setCapacity
argument_list|(
literal|"root.default"
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setOffSwitchPerHeartbeatLimit
argument_list|(
name|numQueues
argument_list|)
expr_stmt|;
name|float
name|capacity
init|=
literal|100.0f
operator|/
name|numQueues
decl_stmt|;
name|String
index|[]
name|subQueues
init|=
operator|new
name|String
index|[
name|numQueues
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numQueues
condition|;
name|i
operator|++
control|)
block|{
name|String
name|queueName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%03d"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|String
name|queuePath
init|=
literal|"root."
operator|+
name|queueName
decl_stmt|;
name|subQueues
index|[
name|i
index|]
operator|=
name|queueName
expr_stmt|;
name|csconf
operator|.
name|setMaximumApplicationMasterResourcePerQueuePercent
argument_list|(
name|queuePath
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setMaximumAMResourcePercentPerPartition
argument_list|(
name|queuePath
argument_list|,
literal|""
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setCapacity
argument_list|(
name|queuePath
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setUserLimitFactor
argument_list|(
name|queuePath
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
name|csconf
operator|.
name|setMaximumCapacity
argument_list|(
name|queuePath
argument_list|,
literal|100.0f
argument_list|)
expr_stmt|;
block|}
name|csconf
operator|.
name|setQueues
argument_list|(
literal|"root"
argument_list|,
name|subQueues
argument_list|)
expr_stmt|;
return|return
name|csconf
return|;
block|}
block|}
end_class

end_unit

