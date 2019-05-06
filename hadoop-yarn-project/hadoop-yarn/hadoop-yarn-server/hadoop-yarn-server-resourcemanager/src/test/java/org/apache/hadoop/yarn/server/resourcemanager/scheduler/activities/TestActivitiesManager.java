begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.activities
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
name|activities
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|Callable
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
name|ConcurrentHashMap
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
name|Future
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
name|LinkedBlockingQueue
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
name|ThreadPoolExecutor
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
name|TimeUnit
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
name|scheduler
operator|.
name|ActiveUsersManager
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
name|SchedulerApplicationAttempt
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
name|LeafQueue
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
name|TestUtils
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
name|FiCaSchedulerNode
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
name|scheduler
operator|.
name|SchedulerRequestKey
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
name|SystemClock
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
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
name|mock
import|;
end_import

begin_comment
comment|/**  * Test class for {@link ActivitiesManager}.  */
end_comment

begin_class
DECL|class|TestActivitiesManager
specifier|public
class|class
name|TestActivitiesManager
block|{
DECL|field|NUM_NODES
specifier|private
specifier|final
specifier|static
name|int
name|NUM_NODES
init|=
literal|5
decl_stmt|;
DECL|field|NUM_APPS
specifier|private
specifier|final
specifier|static
name|int
name|NUM_APPS
init|=
literal|5
decl_stmt|;
DECL|field|NUM_THREADS
specifier|private
specifier|final
specifier|static
name|int
name|NUM_THREADS
init|=
literal|5
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|activitiesManager
specifier|private
name|TestingActivitiesManager
name|activitiesManager
decl_stmt|;
DECL|field|apps
specifier|private
name|List
argument_list|<
name|SchedulerApplicationAttempt
argument_list|>
name|apps
decl_stmt|;
DECL|field|nodes
specifier|private
name|List
argument_list|<
name|SchedulerNode
argument_list|>
name|nodes
decl_stmt|;
DECL|field|threadPoolExecutor
specifier|private
name|ThreadPoolExecutor
name|threadPoolExecutor
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|rmContext
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|ResourceScheduler
name|scheduler
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ResourceScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|scheduler
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|rmContext
operator|.
name|getScheduler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|LeafQueue
name|mockQueue
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|LeafQueue
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|RMApp
argument_list|>
name|rmApps
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|rmApps
argument_list|)
operator|.
name|when
argument_list|(
name|rmContext
argument_list|)
operator|.
name|getRMApps
argument_list|()
expr_stmt|;
name|apps
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_APPS
condition|;
name|i
operator|++
control|)
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|TestUtils
operator|.
name|getMockApplicationAttemptId
argument_list|(
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|RMApp
name|mockApp
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|RMApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|mockApp
argument_list|)
operator|.
name|getApplicationId
argument_list|()
expr_stmt|;
name|rmApps
operator|.
name|put
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|mockApp
argument_list|)
expr_stmt|;
name|FiCaSchedulerApp
name|app
init|=
operator|new
name|FiCaSchedulerApp
argument_list|(
name|appAttemptId
argument_list|,
literal|"user"
argument_list|,
name|mockQueue
argument_list|,
name|mock
argument_list|(
name|ActiveUsersManager
operator|.
name|class
argument_list|)
argument_list|,
name|rmContext
argument_list|)
decl_stmt|;
name|apps
operator|.
name|add
argument_list|(
name|app
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_NODES
condition|;
name|i
operator|++
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|TestUtils
operator|.
name|getMockNode
argument_list|(
literal|"host"
operator|+
name|i
argument_list|,
literal|"rack"
argument_list|,
literal|1
argument_list|,
literal|10240
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|activitiesManager
operator|=
operator|new
name|TestingActivitiesManager
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|threadPoolExecutor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|NUM_THREADS
argument_list|,
name|NUM_THREADS
argument_list|,
literal|3L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test recording activities belong to different nodes in multiple threads,    * these threads can run without interference and one activity    * should be recorded by every thread.    */
annotation|@
name|Test
DECL|method|testRecordingDifferentNodeActivitiesInMultiThreads ()
specifier|public
name|void
name|testRecordingDifferentNodeActivitiesInMultiThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SchedulerNode
name|node
range|:
name|nodes
control|)
block|{
name|Callable
argument_list|<
name|Void
argument_list|>
name|task
init|=
parameter_list|()
lambda|->
block|{
name|SchedulerApplicationAttempt
name|randomApp
init|=
name|apps
operator|.
name|get
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|NUM_APPS
argument_list|)
argument_list|)
decl_stmt|;
comment|// start recording activities for random node
name|activitiesManager
operator|.
name|recordNextNodeUpdateActivities
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// generate node/app activities
name|ActivitiesLogger
operator|.
name|NODE
operator|.
name|startNodeUpdateRecording
argument_list|(
name|activitiesManager
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|recordAppActivityWithoutAllocation
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|randomApp
argument_list|,
operator|new
name|SchedulerRequestKey
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|FAIL_TO_ALLOCATE
argument_list|,
name|ActivityState
operator|.
name|REJECTED
argument_list|)
expr_stmt|;
name|ActivitiesLogger
operator|.
name|NODE
operator|.
name|finishNodeUpdateRecording
argument_list|(
name|activitiesManager
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|threadPoolExecutor
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|Void
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
comment|// Check activities for all nodes should be recorded and every node should
comment|// have only one allocation information.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NUM_NODES
argument_list|,
name|activitiesManager
operator|.
name|historyNodeAllocations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|List
argument_list|<
name|List
argument_list|<
name|NodeAllocation
argument_list|>
argument_list|>
name|nodeAllocationsForThisNode
range|:
name|activitiesManager
operator|.
name|historyNodeAllocations
operator|.
name|values
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodeAllocationsForThisNode
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
name|nodeAllocationsForThisNode
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test recording activities for multi-nodes assignment in multiple threads,    * only one activity info should be recorded by one of these threads.    */
annotation|@
name|Test
DECL|method|testRecordingSchedulerActivitiesForMultiNodesInMultiThreads ()
specifier|public
name|void
name|testRecordingSchedulerActivitiesForMultiNodesInMultiThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|// start recording activities for multi-nodes
name|activitiesManager
operator|.
name|recordNextNodeUpdateActivities
argument_list|(
name|ActivitiesManager
operator|.
name|EMPTY_NODE_ID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// generate node/app activities
for|for
control|(
name|SchedulerNode
name|node
range|:
name|nodes
control|)
block|{
name|Callable
argument_list|<
name|Void
argument_list|>
name|task
init|=
parameter_list|()
lambda|->
block|{
name|SchedulerApplicationAttempt
name|randomApp
init|=
name|apps
operator|.
name|get
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|NUM_APPS
argument_list|)
argument_list|)
decl_stmt|;
name|ActivitiesLogger
operator|.
name|NODE
operator|.
name|startNodeUpdateRecording
argument_list|(
name|activitiesManager
argument_list|,
name|ActivitiesManager
operator|.
name|EMPTY_NODE_ID
argument_list|)
expr_stmt|;
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|recordAppActivityWithoutAllocation
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|randomApp
argument_list|,
operator|new
name|SchedulerRequestKey
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|FAIL_TO_ALLOCATE
argument_list|,
name|ActivityState
operator|.
name|REJECTED
argument_list|)
expr_stmt|;
name|ActivitiesLogger
operator|.
name|NODE
operator|.
name|finishNodeUpdateRecording
argument_list|(
name|activitiesManager
argument_list|,
name|ActivitiesManager
operator|.
name|EMPTY_NODE_ID
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|threadPoolExecutor
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|Void
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
comment|// Check activities for multi-nodes should be recorded only once
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|activitiesManager
operator|.
name|historyNodeAllocations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test recording app activities in multiple threads,    * only one activity info should be recorded by one of these threads.    */
annotation|@
name|Test
DECL|method|testRecordingAppActivitiesInMultiThreads ()
specifier|public
name|void
name|testRecordingAppActivitiesInMultiThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|// start recording activities for a random app
name|SchedulerApplicationAttempt
name|randomApp
init|=
name|apps
operator|.
name|get
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|NUM_APPS
argument_list|)
argument_list|)
decl_stmt|;
name|activitiesManager
operator|.
name|turnOnAppActivitiesRecording
argument_list|(
name|randomApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// generate app activities
name|int
name|nTasks
init|=
literal|20
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
name|nTasks
condition|;
name|i
operator|++
control|)
block|{
name|Callable
argument_list|<
name|Void
argument_list|>
name|task
init|=
parameter_list|()
lambda|->
block|{
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|startAppAllocationRecording
argument_list|(
name|activitiesManager
argument_list|,
operator|(
name|FiCaSchedulerNode
operator|)
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|SystemClock
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|,
name|randomApp
argument_list|)
expr_stmt|;
for|for
control|(
name|SchedulerNode
name|node
range|:
name|nodes
control|)
block|{
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|recordAppActivityWithoutAllocation
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|randomApp
argument_list|,
operator|new
name|SchedulerRequestKey
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|FAIL_TO_ALLOCATE
argument_list|,
name|ActivityState
operator|.
name|REJECTED
argument_list|)
expr_stmt|;
block|}
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|finishAllocatedAppAllocationRecording
argument_list|(
name|activitiesManager
argument_list|,
name|randomApp
operator|.
name|getApplicationId
argument_list|()
argument_list|,
literal|null
argument_list|,
name|ActivityState
operator|.
name|SKIPPED
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|SKIPPED_ALL_PRIORITIES
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|threadPoolExecutor
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Check activities for multi-nodes should be recorded only once
for|for
control|(
name|Future
argument_list|<
name|Void
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|Queue
argument_list|<
name|AppAllocation
argument_list|>
name|appAllocations
init|=
name|activitiesManager
operator|.
name|completedAppAllocations
operator|.
name|get
argument_list|(
name|randomApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nTasks
argument_list|,
name|appAllocations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AppAllocation
name|aa
range|:
name|appAllocations
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NUM_NODES
argument_list|,
name|aa
operator|.
name|getAllocationAttempts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Testing activities manager which can record all history information about    * node allocations.    */
DECL|class|TestingActivitiesManager
specifier|public
class|class
name|TestingActivitiesManager
extends|extends
name|ActivitiesManager
block|{
DECL|field|historyNodeAllocations
specifier|private
name|Map
argument_list|<
name|NodeId
argument_list|,
name|List
argument_list|<
name|List
argument_list|<
name|NodeAllocation
argument_list|>
argument_list|>
argument_list|>
name|historyNodeAllocations
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|TestingActivitiesManager (RMContext rmContext)
specifier|public
name|TestingActivitiesManager
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|super
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|super
operator|.
name|completedNodeAllocations
operator|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
parameter_list|(
name|invocationOnMock
parameter_list|)
lambda|->
block|{
name|NodeId
name|nodeId
init|=
operator|(
name|NodeId
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|List
argument_list|<
name|NodeAllocation
argument_list|>
name|nodeAllocations
init|=
operator|(
name|List
argument_list|<
name|NodeAllocation
argument_list|>
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|1
index|]
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|NodeAllocation
argument_list|>
argument_list|>
name|historyAllocationsForThisNode
init|=
name|historyNodeAllocations
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|historyAllocationsForThisNode
operator|==
literal|null
condition|)
block|{
name|historyAllocationsForThisNode
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|historyNodeAllocations
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|historyAllocationsForThisNode
argument_list|)
expr_stmt|;
block|}
name|historyAllocationsForThisNode
operator|.
name|add
argument_list|(
name|nodeAllocations
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|completedNodeAllocations
argument_list|)
operator|.
name|put
argument_list|(
name|any
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|List
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

