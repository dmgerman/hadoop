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
name|RMContainerImpl
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
name|ControlledClock
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
name|After
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|FileWriter
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
name|io
operator|.
name|PrintWriter
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
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Tests to verify fairshare and minshare preemption, using parameterization.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestFairSchedulerPreemption
specifier|public
class|class
name|TestFairSchedulerPreemption
extends|extends
name|FairSchedulerTestBase
block|{
DECL|field|ALLOC_FILE
specifier|private
specifier|static
specifier|final
name|File
name|ALLOC_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"test-queues"
argument_list|)
decl_stmt|;
DECL|field|GB
specifier|private
specifier|static
specifier|final
name|int
name|GB
init|=
literal|1024
decl_stmt|;
comment|// Scheduler clock
DECL|field|clock
specifier|private
specifier|final
name|ControlledClock
name|clock
init|=
operator|new
name|ControlledClock
argument_list|()
decl_stmt|;
comment|// Node Capacity = NODE_CAPACITY_MULTIPLE * (1 GB or 1 vcore)
DECL|field|NODE_CAPACITY_MULTIPLE
specifier|private
specifier|static
specifier|final
name|int
name|NODE_CAPACITY_MULTIPLE
init|=
literal|4
decl_stmt|;
DECL|field|fairsharePreemption
specifier|private
specifier|final
name|boolean
name|fairsharePreemption
decl_stmt|;
DECL|field|drf
specifier|private
specifier|final
name|boolean
name|drf
decl_stmt|;
comment|// App that takes up the entire cluster
DECL|field|greedyApp
specifier|private
name|FSAppAttempt
name|greedyApp
decl_stmt|;
comment|// Starving app that is expected to instigate preemption
DECL|field|starvingApp
specifier|private
name|FSAppAttempt
name|starvingApp
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
DECL|method|getParameters ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"MinSharePreemption"
block|,
literal|0
block|}
block|,
block|{
literal|"MinSharePreemptionWithDRF"
block|,
literal|1
block|}
block|,
block|{
literal|"FairSharePreemption"
block|,
literal|2
block|}
block|,
block|{
literal|"FairSharePreemptionWithDRF"
block|,
literal|3
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|TestFairSchedulerPreemption (String name, int mode)
specifier|public
name|TestFairSchedulerPreemption
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
name|fairsharePreemption
operator|=
operator|(
name|mode
operator|>
literal|1
operator|)
expr_stmt|;
comment|// 2 and 3
name|drf
operator|=
operator|(
name|mode
operator|%
literal|2
operator|==
literal|1
operator|)
expr_stmt|;
comment|// 1 and 3
name|writeAllocFile
argument_list|()
expr_stmt|;
block|}
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
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|PREEMPTION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|PREEMPTION_THRESHOLD
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|WAIT_TIME_BEFORE_KILL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|setupCluster
argument_list|()
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
name|ALLOC_FILE
operator|.
name|delete
argument_list|()
expr_stmt|;
name|conf
operator|=
literal|null
expr_stmt|;
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
block|}
DECL|method|writeAllocFile ()
specifier|private
name|void
name|writeAllocFile
parameter_list|()
throws|throws
name|IOException
block|{
comment|/*      * Queue hierarchy:      * root      * |--- preemptable      *      |--- child-1      *      |--- child-2      * |--- preemptable-sibling      * |--- nonpreemptible      *      |--- child-1      *      |--- child-2      */
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|ALLOC_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"preemptable\">"
argument_list|)
expr_stmt|;
name|writePreemptionParams
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// Child-1
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"child-1\">"
argument_list|)
expr_stmt|;
name|writeResourceParams
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// Child-2
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"child-2\">"
argument_list|)
expr_stmt|;
name|writeResourceParams
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// end of preemptable queue
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"preemptable-sibling\">"
argument_list|)
expr_stmt|;
name|writePreemptionParams
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// Queue with preemption disallowed
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"nonpreemptable\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<allowPreemptionFrom>false"
operator|+
literal|"</allowPreemptionFrom>"
argument_list|)
expr_stmt|;
name|writePreemptionParams
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// Child-1
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"child-1\">"
argument_list|)
expr_stmt|;
name|writeResourceParams
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// Child-2
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"child-2\">"
argument_list|)
expr_stmt|;
name|writeResourceParams
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// end of nonpreemptable queue
if|if
condition|(
name|drf
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<defaultQueueSchedulingPolicy>drf"
operator|+
literal|"</defaultQueueSchedulingPolicy>"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"</allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Allocation file does not exist, not running the test"
argument_list|,
name|ALLOC_FILE
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writePreemptionParams (PrintWriter out)
specifier|private
name|void
name|writePreemptionParams
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
if|if
condition|(
name|fairsharePreemption
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<fairSharePreemptionThreshold>1"
operator|+
literal|"</fairSharePreemptionThreshold>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<fairSharePreemptionTimeout>0"
operator|+
literal|"</fairSharePreemptionTimeout>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<minSharePreemptionTimeout>0"
operator|+
literal|"</minSharePreemptionTimeout>"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeResourceParams (PrintWriter out)
specifier|private
name|void
name|writeResourceParams
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
if|if
condition|(
operator|!
name|fairsharePreemption
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<minResources>4096mb,4vcores</minResources>"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setupCluster ()
specifier|private
name|void
name|setupCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|resourceManager
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
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
comment|// YARN-6249, FSLeafQueue#lastTimeAtMinShare is initialized to the time in
comment|// the real world, so we should keep the clock up with it.
name|clock
operator|.
name|setTime
argument_list|(
name|SystemClock
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Create and add two nodes to the cluster, with capacities
comment|// disproportional to the container requests.
name|addNode
argument_list|(
name|NODE_CAPACITY_MULTIPLE
operator|*
name|GB
argument_list|,
literal|3
operator|*
name|NODE_CAPACITY_MULTIPLE
argument_list|)
expr_stmt|;
name|addNode
argument_list|(
name|NODE_CAPACITY_MULTIPLE
operator|*
name|GB
argument_list|,
literal|3
operator|*
name|NODE_CAPACITY_MULTIPLE
argument_list|)
expr_stmt|;
comment|// Reinitialize the scheduler so DRF policy picks up cluster capacity
comment|// TODO (YARN-6194): One shouldn't need to call this
name|scheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|resourceManager
operator|.
name|getRMContext
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify if child-1 and child-2 are preemptable
name|FSQueue
name|child1
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getQueue
argument_list|(
literal|"nonpreemptable.child-1"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|child1
operator|.
name|isPreemptable
argument_list|()
argument_list|)
expr_stmt|;
name|FSQueue
name|child2
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getQueue
argument_list|(
literal|"nonpreemptable.child-2"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|child2
operator|.
name|isPreemptable
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|sendEnoughNodeUpdatesToAssignFully ()
specifier|private
name|void
name|sendEnoughNodeUpdatesToAssignFully
parameter_list|()
block|{
for|for
control|(
name|RMNode
name|node
range|:
name|rmNodes
control|)
block|{
name|NodeUpdateSchedulerEvent
name|nodeUpdateSchedulerEvent
init|=
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|node
argument_list|)
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
name|NODE_CAPACITY_MULTIPLE
condition|;
name|i
operator|++
control|)
block|{
name|scheduler
operator|.
name|handle
argument_list|(
name|nodeUpdateSchedulerEvent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Submit an application to a given queue and take over the entire cluster.    *    * @param queueName queue name    */
DECL|method|takeAllResources (String queueName)
specifier|private
name|void
name|takeAllResources
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
comment|// Create an app that takes up all the resources on the cluster
name|ApplicationAttemptId
name|appAttemptId
init|=
name|createSchedulingRequest
argument_list|(
name|GB
argument_list|,
literal|1
argument_list|,
name|queueName
argument_list|,
literal|"default"
argument_list|,
name|NODE_CAPACITY_MULTIPLE
operator|*
name|rmNodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|greedyApp
operator|=
name|scheduler
operator|.
name|getSchedulerApp
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
name|sendEnoughNodeUpdatesToAssignFully
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|greedyApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify preemptable for queue and app attempt
name|assertTrue
argument_list|(
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getQueue
argument_list|(
name|queueName
argument_list|)
operator|.
name|isPreemptable
argument_list|()
operator|==
name|greedyApp
operator|.
name|isPreemptable
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Submit an application to a given queue and preempt half resources of the    * cluster.    *    * @param queueName queue name    * @throws InterruptedException    *         if any thread has interrupted the current thread.    */
DECL|method|preemptHalfResources (String queueName)
specifier|private
name|void
name|preemptHalfResources
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|createSchedulingRequest
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
literal|2
argument_list|,
name|queueName
argument_list|,
literal|"default"
argument_list|,
name|NODE_CAPACITY_MULTIPLE
operator|*
name|rmNodes
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
decl_stmt|;
name|starvingApp
operator|=
name|scheduler
operator|.
name|getSchedulerApp
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
comment|// Move clock enough to identify starvation
name|clock
operator|.
name|tickSec
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
block|}
comment|/**    * Submit application to {@code queue1} and take over the entire cluster.    * Submit application with larger containers to {@code queue2} that    * requires preemption from the first application.    *    * @param queue1 first queue    * @param queue2 second queue    * @throws InterruptedException if interrupted while waiting    */
DECL|method|submitApps (String queue1, String queue2)
specifier|private
name|void
name|submitApps
parameter_list|(
name|String
name|queue1
parameter_list|,
name|String
name|queue2
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|takeAllResources
argument_list|(
name|queue1
argument_list|)
expr_stmt|;
name|preemptHalfResources
argument_list|(
name|queue2
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyPreemption (int numStarvedAppContainers, int numGreedyAppContainers)
specifier|private
name|void
name|verifyPreemption
parameter_list|(
name|int
name|numStarvedAppContainers
parameter_list|,
name|int
name|numGreedyAppContainers
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// Sleep long enough for four containers to be preempted.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|greedyApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|numGreedyAppContainers
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
comment|// Post preemption, verify the greedyApp has the correct # of containers.
name|assertEquals
argument_list|(
literal|"Incorrect # of containers on the greedy app"
argument_list|,
name|numGreedyAppContainers
argument_list|,
name|greedyApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify the queue metrics are set appropriately. The greedyApp started
comment|// with 8 1GB, 1vcore containers.
name|assertEquals
argument_list|(
literal|"Incorrect # of preempted containers in QueueMetrics"
argument_list|,
literal|8
operator|-
name|numGreedyAppContainers
argument_list|,
name|greedyApp
operator|.
name|getQueue
argument_list|()
operator|.
name|getMetrics
argument_list|()
operator|.
name|getAggregatePreemptedContainers
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify the node is reserved for the starvingApp
for|for
control|(
name|RMNode
name|rmNode
range|:
name|rmNodes
control|)
block|{
name|FSSchedulerNode
name|node
init|=
operator|(
name|FSSchedulerNode
operator|)
name|scheduler
operator|.
name|getNodeTracker
argument_list|()
operator|.
name|getNode
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getContainersForPreemption
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"node should be reserved for the starvingApp"
argument_list|,
name|node
operator|.
name|getPreemptionList
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
name|starvingApp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|sendEnoughNodeUpdatesToAssignFully
argument_list|()
expr_stmt|;
comment|// Verify the preempted containers are assigned to starvingApp
name|assertEquals
argument_list|(
literal|"Starved app is not assigned the right # of containers"
argument_list|,
name|numStarvedAppContainers
argument_list|,
name|starvingApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify the node is not reserved for the starvingApp anymore
for|for
control|(
name|RMNode
name|rmNode
range|:
name|rmNodes
control|)
block|{
name|FSSchedulerNode
name|node
init|=
operator|(
name|FSSchedulerNode
operator|)
name|scheduler
operator|.
name|getNodeTracker
argument_list|()
operator|.
name|getNode
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getContainersForPreemption
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertFalse
argument_list|(
name|node
operator|.
name|getPreemptionList
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
name|starvingApp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|verifyNoPreemption ()
specifier|private
name|void
name|verifyNoPreemption
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// Sleep long enough to ensure not even one container is preempted.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|greedyApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|8
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|greedyApp
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionWithinSameLeafQueue ()
specifier|public
name|void
name|testPreemptionWithinSameLeafQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queue
init|=
literal|"root.preemptable.child-1"
decl_stmt|;
name|submitApps
argument_list|(
name|queue
argument_list|,
name|queue
argument_list|)
expr_stmt|;
if|if
condition|(
name|fairsharePreemption
condition|)
block|{
name|verifyPreemption
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|verifyNoPreemption
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPreemptionBetweenTwoSiblingLeafQueues ()
specifier|public
name|void
name|testPreemptionBetweenTwoSiblingLeafQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|submitApps
argument_list|(
literal|"root.preemptable.child-1"
argument_list|,
literal|"root.preemptable.child-2"
argument_list|)
expr_stmt|;
name|verifyPreemption
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionBetweenNonSiblingQueues ()
specifier|public
name|void
name|testPreemptionBetweenNonSiblingQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|submitApps
argument_list|(
literal|"root.preemptable.child-1"
argument_list|,
literal|"root.nonpreemptable.child-1"
argument_list|)
expr_stmt|;
name|verifyPreemption
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoPreemptionFromDisallowedQueue ()
specifier|public
name|void
name|testNoPreemptionFromDisallowedQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|submitApps
argument_list|(
literal|"root.nonpreemptable.child-1"
argument_list|,
literal|"root.preemptable.child-1"
argument_list|)
expr_stmt|;
name|verifyNoPreemption
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the number of AM containers for each node.    *    * @param numAMContainersPerNode number of AM containers per node    */
DECL|method|setNumAMContainersPerNode (int numAMContainersPerNode)
specifier|private
name|void
name|setNumAMContainersPerNode
parameter_list|(
name|int
name|numAMContainersPerNode
parameter_list|)
block|{
name|List
argument_list|<
name|FSSchedulerNode
argument_list|>
name|potentialNodes
init|=
name|scheduler
operator|.
name|getNodeTracker
argument_list|()
operator|.
name|getNodesByResourceName
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
for|for
control|(
name|FSSchedulerNode
name|node
range|:
name|potentialNodes
control|)
block|{
name|List
argument_list|<
name|RMContainer
argument_list|>
name|containers
init|=
name|node
operator|.
name|getCopiedListOfRunningContainers
argument_list|()
decl_stmt|;
comment|// Change the first numAMContainersPerNode out of 4 containers to
comment|// AM containers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numAMContainersPerNode
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|RMContainerImpl
operator|)
name|containers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|setAMContainer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setAllAMContainersOnNode (NodeId nodeId)
specifier|private
name|void
name|setAllAMContainersOnNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|SchedulerNode
name|node
init|=
name|scheduler
operator|.
name|getNodeTracker
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
for|for
control|(
name|RMContainer
name|container
range|:
name|node
operator|.
name|getCopiedListOfRunningContainers
argument_list|()
control|)
block|{
operator|(
operator|(
name|RMContainerImpl
operator|)
name|container
operator|)
operator|.
name|setAMContainer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPreemptionSelectNonAMContainer ()
specifier|public
name|void
name|testPreemptionSelectNonAMContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|takeAllResources
argument_list|(
literal|"root.preemptable.child-1"
argument_list|)
expr_stmt|;
name|setNumAMContainersPerNode
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|preemptHalfResources
argument_list|(
literal|"root.preemptable.child-2"
argument_list|)
expr_stmt|;
name|verifyPreemption
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|RMContainer
argument_list|>
name|containers
init|=
operator|(
name|ArrayList
argument_list|<
name|RMContainer
argument_list|>
operator|)
name|starvingApp
operator|.
name|getLiveContainers
argument_list|()
decl_stmt|;
name|String
name|host0
init|=
name|containers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|host1
init|=
name|containers
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
decl_stmt|;
comment|// Each node provides two and only two non-AM containers to be preempted, so
comment|// the preemption happens on both nodes.
name|assertTrue
argument_list|(
literal|"Preempted containers should come from two different "
operator|+
literal|"nodes."
argument_list|,
operator|!
name|host0
operator|.
name|equals
argument_list|(
name|host1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRelaxLocalityToNotPreemptAM ()
specifier|public
name|void
name|testRelaxLocalityToNotPreemptAM
parameter_list|()
throws|throws
name|Exception
block|{
name|takeAllResources
argument_list|(
literal|"root.preemptable.child-1"
argument_list|)
expr_stmt|;
name|RMNode
name|node1
init|=
name|rmNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|setAllAMContainersOnNode
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|SchedulerNode
name|node
init|=
name|scheduler
operator|.
name|getNodeTracker
argument_list|()
operator|.
name|getNode
argument_list|(
name|node1
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|greedyAppAttemptId
init|=
name|node
operator|.
name|getCopiedListOfRunningContainers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
comment|// Make the RACK_LOCAL and OFF_SWITCH requests big enough that they can't be
comment|// satisfied. This forces the RR that we consider for preemption to be the
comment|// NODE_LOCAL one.
name|ResourceRequest
name|nodeRequest
init|=
name|createResourceRequest
argument_list|(
name|GB
argument_list|,
name|node1
operator|.
name|getHostName
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ResourceRequest
name|rackRequest
init|=
name|createResourceRequest
argument_list|(
name|GB
operator|*
literal|10
argument_list|,
name|node1
operator|.
name|getRackName
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ResourceRequest
name|anyRequest
init|=
name|createResourceRequest
argument_list|(
name|GB
operator|*
literal|10
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|nodeRequest
argument_list|,
name|rackRequest
argument_list|,
name|anyRequest
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|starvedAppAttemptId
init|=
name|createSchedulingRequest
argument_list|(
literal|"root.preemptable.child-2"
argument_list|,
literal|"default"
argument_list|,
name|resourceRequests
argument_list|)
decl_stmt|;
name|starvingApp
operator|=
name|scheduler
operator|.
name|getSchedulerApp
argument_list|(
name|starvedAppAttemptId
argument_list|)
expr_stmt|;
comment|// Move clock enough to identify starvation
name|clock
operator|.
name|tickSec
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
comment|// Make sure 4 containers were preempted from the greedy app, but also that
comment|// none were preempted on our all-AM node, even though the NODE_LOCAL RR
comment|// asked for resources on it.
comment|// TODO (YARN-7655) The starved app should be allocated 4 containers.
comment|// It should be possible to modify the RRs such that this is true
comment|// after YARN-7903.
name|verifyPreemption
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|RMContainer
name|container
range|:
name|node
operator|.
name|getCopiedListOfRunningContainers
argument_list|()
control|)
block|{
assert|assert
operator|(
name|container
operator|.
name|isAMContainer
argument_list|()
operator|)
assert|;
assert|assert
operator|(
name|container
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|equals
argument_list|(
name|greedyAppAttemptId
argument_list|)
operator|)
assert|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAppNotPreemptedBelowFairShare ()
specifier|public
name|void
name|testAppNotPreemptedBelowFairShare
parameter_list|()
throws|throws
name|Exception
block|{
name|takeAllResources
argument_list|(
literal|"root.preemptable.child-1"
argument_list|)
expr_stmt|;
name|tryPreemptMoreThanFairShare
argument_list|(
literal|"root.preemptable.child-2"
argument_list|)
expr_stmt|;
block|}
DECL|method|tryPreemptMoreThanFairShare (String queueName)
specifier|private
name|void
name|tryPreemptMoreThanFairShare
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|createSchedulingRequest
argument_list|(
literal|3
operator|*
name|GB
argument_list|,
literal|3
argument_list|,
name|queueName
argument_list|,
literal|"default"
argument_list|,
name|NODE_CAPACITY_MULTIPLE
operator|*
name|rmNodes
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
decl_stmt|;
name|starvingApp
operator|=
name|scheduler
operator|.
name|getSchedulerApp
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|verifyPreemption
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionBetweenSiblingQueuesWithParentAtFairShare ()
specifier|public
name|void
name|testPreemptionBetweenSiblingQueuesWithParentAtFairShare
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// Run this test only for fairshare preemption
if|if
condition|(
operator|!
name|fairsharePreemption
condition|)
block|{
return|return;
block|}
comment|// Let one of the child queues take over the entire cluster
name|takeAllResources
argument_list|(
literal|"root.preemptable.child-1"
argument_list|)
expr_stmt|;
comment|// Submit a job so half the resources go to parent's sibling
name|preemptHalfResources
argument_list|(
literal|"root.preemptable-sibling"
argument_list|)
expr_stmt|;
name|verifyPreemption
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|// Submit a job to the child's sibling to force preemption from the child
name|preemptHalfResources
argument_list|(
literal|"root.preemptable.child-2"
argument_list|)
expr_stmt|;
name|verifyPreemption
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

