begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|fair
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
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Collection
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
name|event
operator|.
name|AppAttemptRemovedSchedulerEvent
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
name|fair
operator|.
name|allocationfile
operator|.
name|AllocationFileQueue
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
name|fair
operator|.
name|allocationfile
operator|.
name|AllocationFileWriter
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

begin_class
DECL|class|TestFairSchedulerFairShare
specifier|public
class|class
name|TestFairSchedulerFairShare
extends|extends
name|FairSchedulerTestBase
block|{
DECL|field|ALLOC_FILE
specifier|private
specifier|final
specifier|static
name|String
name|ALLOC_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
name|TestFairSchedulerFairShare
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
name|createConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|ALLOCATION_FILE
argument_list|,
name|ALLOC_FILE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
if|if
condition|(
name|resourceManager
operator|!=
literal|null
condition|)
block|{
name|resourceManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|resourceManager
operator|=
literal|null
expr_stmt|;
block|}
name|conf
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|createClusterWithQueuesAndOneNode (int mem)
specifier|private
name|void
name|createClusterWithQueuesAndOneNode
parameter_list|(
name|int
name|mem
parameter_list|)
block|{
name|createClusterWithQueuesAndOneNode
argument_list|(
name|mem
argument_list|,
literal|0
argument_list|,
literal|"fair"
argument_list|)
expr_stmt|;
block|}
DECL|method|createClusterWithQueuesAndOneNode (int mem, int vCores, String policy)
specifier|private
name|void
name|createClusterWithQueuesAndOneNode
parameter_list|(
name|int
name|mem
parameter_list|,
name|int
name|vCores
parameter_list|,
name|String
name|policy
parameter_list|)
block|{
name|AllocationFileWriter
name|allocationFileWriter
init|=
name|AllocationFileWriter
operator|.
name|create
argument_list|()
operator|.
name|addQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"root"
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"parentA"
argument_list|)
operator|.
name|weight
argument_list|(
literal|8
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"childA1"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"childA2"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"childA3"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"childA4"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"parentB"
argument_list|)
operator|.
name|weight
argument_list|(
literal|1
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"childB1"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|subQueue
argument_list|(
operator|new
name|AllocationFileQueue
operator|.
name|Builder
argument_list|(
literal|"childB2"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|policy
operator|.
name|equals
argument_list|(
literal|"fair"
argument_list|)
condition|)
block|{
name|allocationFileWriter
operator|.
name|fairDefaultQueueSchedulingPolicy
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|policy
operator|.
name|equals
argument_list|(
literal|"drf"
argument_list|)
condition|)
block|{
name|allocationFileWriter
operator|.
name|drfDefaultQueueSchedulingPolicy
argument_list|()
expr_stmt|;
block|}
name|allocationFileWriter
operator|.
name|writeToFile
argument_list|(
name|ALLOC_FILE
argument_list|)
expr_stmt|;
name|resourceManager
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|scheduler
operator|=
operator|(
name|FairScheduler
operator|)
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
expr_stmt|;
name|RMNode
name|node1
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|1
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
name|mem
argument_list|,
name|vCores
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|"127.0.0.1"
argument_list|)
decl_stmt|;
name|NodeAddedSchedulerEvent
name|nodeEvent1
init|=
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|node1
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
name|nodeEvent1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFairShareNoAppsRunning ()
specifier|public
name|void
name|testFairShareNoAppsRunning
parameter_list|()
block|{
name|int
name|nodeCapacity
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
name|createClusterWithQueuesAndOneNode
argument_list|(
name|nodeCapacity
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
comment|// No apps are running in the cluster,verify if fair share is zero
comment|// for all queues under parentA and parentB.
name|Collection
argument_list|<
name|FSLeafQueue
argument_list|>
name|leafQueues
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueues
argument_list|()
decl_stmt|;
for|for
control|(
name|FSLeafQueue
name|leaf
range|:
name|leafQueues
control|)
block|{
if|if
condition|(
name|leaf
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"root.parentA"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|double
operator|)
name|leaf
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|leaf
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"root.parentB"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|double
operator|)
name|leaf
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|verifySteadyFairShareMemory
argument_list|(
name|leafQueues
argument_list|,
name|nodeCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFairShareOneAppRunning ()
specifier|public
name|void
name|testFairShareOneAppRunning
parameter_list|()
block|{
name|int
name|nodeCapacity
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
name|createClusterWithQueuesAndOneNode
argument_list|(
name|nodeCapacity
argument_list|)
expr_stmt|;
comment|// Run a app in a childA1. Verify whether fair share is 100% in childA1,
comment|// since it is the only active queue.
comment|// Also verify if fair share is 0 for childA2. since no app is
comment|// running in it.
name|createSchedulingRequest
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA1"
argument_list|,
literal|"user1"
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA1"
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
operator|*
literal|100
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA2"
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|verifySteadyFairShareMemory
argument_list|(
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueues
argument_list|()
argument_list|,
name|nodeCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFairShareMultipleActiveQueuesUnderSameParent ()
specifier|public
name|void
name|testFairShareMultipleActiveQueuesUnderSameParent
parameter_list|()
block|{
name|int
name|nodeCapacity
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
name|createClusterWithQueuesAndOneNode
argument_list|(
name|nodeCapacity
argument_list|)
expr_stmt|;
comment|// Run apps in childA1,childA2,childA3
name|createSchedulingRequest
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA1"
argument_list|,
literal|"user1"
argument_list|)
expr_stmt|;
name|createSchedulingRequest
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA2"
argument_list|,
literal|"user2"
argument_list|)
expr_stmt|;
name|createSchedulingRequest
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA3"
argument_list|,
literal|"user3"
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
comment|// Verify if fair share is 100 / 3 = 33%
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|33
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA"
operator|+
name|i
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
operator|*
literal|100
argument_list|,
literal|.9
argument_list|)
expr_stmt|;
block|}
name|verifySteadyFairShareMemory
argument_list|(
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueues
argument_list|()
argument_list|,
name|nodeCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFairShareMultipleActiveQueuesUnderDifferentParent ()
specifier|public
name|void
name|testFairShareMultipleActiveQueuesUnderDifferentParent
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|nodeCapacity
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
name|createClusterWithQueuesAndOneNode
argument_list|(
name|nodeCapacity
argument_list|)
expr_stmt|;
comment|// Run apps in childA1,childA2 which are under parentA
name|createSchedulingRequest
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA1"
argument_list|,
literal|"user1"
argument_list|)
expr_stmt|;
name|createSchedulingRequest
argument_list|(
literal|3
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA2"
argument_list|,
literal|"user2"
argument_list|)
expr_stmt|;
comment|// Run app in childB1 which is under parentB
name|createSchedulingRequest
argument_list|(
literal|1
operator|*
literal|1024
argument_list|,
literal|"root.parentB.childB1"
argument_list|,
literal|"user3"
argument_list|)
expr_stmt|;
comment|// Run app in root.default queue
name|createSchedulingRequest
argument_list|(
literal|1
operator|*
literal|1024
argument_list|,
literal|"root.default"
argument_list|,
literal|"user4"
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
comment|// The two active child queues under parentA would
comment|// get fair share of 80/2=40%
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|40
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA"
operator|+
name|i
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
operator|*
literal|100
argument_list|,
literal|.9
argument_list|)
expr_stmt|;
block|}
comment|// The child queue under parentB would get a fair share of 10%,
comment|// basically all of parentB's fair share
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentB.childB1"
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
operator|*
literal|100
argument_list|,
literal|.9
argument_list|)
expr_stmt|;
name|verifySteadyFairShareMemory
argument_list|(
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueues
argument_list|()
argument_list|,
name|nodeCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFairShareResetsToZeroWhenAppsComplete ()
specifier|public
name|void
name|testFairShareResetsToZeroWhenAppsComplete
parameter_list|()
block|{
name|int
name|nodeCapacity
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
name|createClusterWithQueuesAndOneNode
argument_list|(
name|nodeCapacity
argument_list|)
expr_stmt|;
comment|// Run apps in childA1,childA2 which are under parentA
name|ApplicationAttemptId
name|app1
init|=
name|createSchedulingRequest
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA1"
argument_list|,
literal|"user1"
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|app2
init|=
name|createSchedulingRequest
argument_list|(
literal|3
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA2"
argument_list|,
literal|"user2"
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
comment|// Verify if both the active queues under parentA get 50% fair
comment|// share
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|50
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA"
operator|+
name|i
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
operator|*
literal|100
argument_list|,
literal|.9
argument_list|)
expr_stmt|;
block|}
comment|// Let app under childA1 complete. This should cause the fair share
comment|// of queue childA1 to be reset to zero,since the queue has no apps running.
comment|// Queue childA2's fair share would increase to 100% since its the only
comment|// active queue.
name|AppAttemptRemovedSchedulerEvent
name|appRemovedEvent1
init|=
operator|new
name|AppAttemptRemovedSchedulerEvent
argument_list|(
name|app1
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
name|appRemovedEvent1
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA1"
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
operator|*
literal|100
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA2"
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
operator|*
literal|100
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|verifySteadyFairShareMemory
argument_list|(
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueues
argument_list|()
argument_list|,
name|nodeCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFairShareWithDRFMultipleActiveQueuesUnderDifferentParent ()
specifier|public
name|void
name|testFairShareWithDRFMultipleActiveQueuesUnderDifferentParent
parameter_list|()
block|{
name|int
name|nodeMem
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
name|int
name|nodeVCores
init|=
literal|10
decl_stmt|;
name|createClusterWithQueuesAndOneNode
argument_list|(
name|nodeMem
argument_list|,
name|nodeVCores
argument_list|,
literal|"drf"
argument_list|)
expr_stmt|;
comment|// Run apps in childA1,childA2 which are under parentA
name|createSchedulingRequest
argument_list|(
literal|2
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA1"
argument_list|,
literal|"user1"
argument_list|)
expr_stmt|;
name|createSchedulingRequest
argument_list|(
literal|3
operator|*
literal|1024
argument_list|,
literal|"root.parentA.childA2"
argument_list|,
literal|"user2"
argument_list|)
expr_stmt|;
comment|// Run app in childB1 which is under parentB
name|createSchedulingRequest
argument_list|(
literal|1
operator|*
literal|1024
argument_list|,
literal|"root.parentB.childB1"
argument_list|,
literal|"user3"
argument_list|)
expr_stmt|;
comment|// Run app in root.default queue
name|createSchedulingRequest
argument_list|(
literal|1
operator|*
literal|1024
argument_list|,
literal|"root.default"
argument_list|,
literal|"user4"
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
comment|// The two active child queues under parentA would
comment|// get 80/2=40% memory and vcores
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|40
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA"
operator|+
name|i
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeMem
operator|*
literal|100
argument_list|,
literal|.9
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|40
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentA.childA"
operator|+
name|i
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|/
name|nodeVCores
operator|*
literal|100
argument_list|,
literal|.9
argument_list|)
expr_stmt|;
block|}
comment|// The only active child queue under parentB would get 10% memory and vcores
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentB.childB1"
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeMem
operator|*
literal|100
argument_list|,
literal|.9
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|double
operator|)
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"root.parentB.childB1"
argument_list|,
literal|false
argument_list|)
operator|.
name|getFairShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|/
name|nodeVCores
operator|*
literal|100
argument_list|,
literal|.9
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|FSLeafQueue
argument_list|>
name|leafQueues
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueues
argument_list|()
decl_stmt|;
for|for
control|(
name|FSLeafQueue
name|leaf
range|:
name|leafQueues
control|)
block|{
if|if
condition|(
name|leaf
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"root.parentA"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0.2
argument_list|,
operator|(
name|double
operator|)
name|leaf
operator|.
name|getSteadyFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeMem
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.2
argument_list|,
operator|(
name|double
operator|)
name|leaf
operator|.
name|getSteadyFairShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|/
name|nodeVCores
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|leaf
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"root.parentB"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0.05
argument_list|,
operator|(
name|double
operator|)
name|leaf
operator|.
name|getSteadyFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeMem
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1
argument_list|,
operator|(
name|double
operator|)
name|leaf
operator|.
name|getSteadyFairShare
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|/
name|nodeVCores
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Verify whether steady fair shares for all leaf queues still follow    * their weight, not related to active/inactive status.    *    * @param leafQueues    * @param nodeCapacity    */
DECL|method|verifySteadyFairShareMemory (Collection<FSLeafQueue> leafQueues, int nodeCapacity)
specifier|private
name|void
name|verifySteadyFairShareMemory
parameter_list|(
name|Collection
argument_list|<
name|FSLeafQueue
argument_list|>
name|leafQueues
parameter_list|,
name|int
name|nodeCapacity
parameter_list|)
block|{
for|for
control|(
name|FSLeafQueue
name|leaf
range|:
name|leafQueues
control|)
block|{
if|if
condition|(
name|leaf
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"root.parentA"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0.2
argument_list|,
operator|(
name|double
operator|)
name|leaf
operator|.
name|getSteadyFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|leaf
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"root.parentB"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|0.05
argument_list|,
operator|(
name|double
operator|)
name|leaf
operator|.
name|getSteadyFairShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|/
name|nodeCapacity
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

