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
name|util
operator|.
name|ControlledClock
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
name|assertNotNull
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
name|assertNull
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

begin_comment
comment|/**  * Test class to verify identification of app starvation  */
end_comment

begin_class
DECL|class|TestFSAppStarvation
specifier|public
class|class
name|TestFSAppStarvation
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
literal|"test-QUEUES"
argument_list|)
decl_stmt|;
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
DECL|field|QUEUES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|QUEUES
init|=
block|{
literal|"no-preemption"
block|,
literal|"minshare"
block|,
literal|"fairshare.child"
block|,
literal|"drf.child"
block|}
decl_stmt|;
DECL|field|preemptionThread
specifier|private
name|FairSchedulerWithMockPreemption
operator|.
name|MockPreemptionThread
name|preemptionThread
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|createConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairSchedulerWithMockPreemption
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
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
comment|// This effectively disables the update thread since we call update
comment|// explicitly on the main thread
name|conf
operator|.
name|setLong
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|UPDATE_INTERVAL_MS
argument_list|,
name|Long
operator|.
name|MAX_VALUE
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
comment|/*    * Test to verify application starvation is computed only when preemption    * is enabled.    */
annotation|@
name|Test
DECL|method|testPreemptionDisabled ()
specifier|public
name|void
name|testPreemptionDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|PREEMPTION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|setupClusterAndSubmitJobs
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"Found starved apps even when preemption is turned off"
argument_list|,
name|scheduler
operator|.
name|getContext
argument_list|()
operator|.
name|getStarvedApps
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test to verify application starvation is computed correctly when    * preemption is turned on.    */
annotation|@
name|Test
DECL|method|testPreemptionEnabled ()
specifier|public
name|void
name|testPreemptionEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|setupClusterAndSubmitJobs
argument_list|()
expr_stmt|;
comment|// Wait for apps to be processed by MockPreemptionThread
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|6000
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|preemptionThread
operator|.
name|uniqueAppsAdded
argument_list|()
operator|>=
literal|3
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
name|assertNotNull
argument_list|(
literal|"FSContext does not have an FSStarvedApps instance"
argument_list|,
name|scheduler
operator|.
name|getContext
argument_list|()
operator|.
name|getStarvedApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expecting 3 starved applications, one each for the "
operator|+
literal|"minshare and fairshare queues"
argument_list|,
literal|3
argument_list|,
name|preemptionThread
operator|.
name|uniqueAppsAdded
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify apps are added again only after the set delay for starvation has
comment|// passed.
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
name|assertEquals
argument_list|(
literal|"Apps re-added even before starvation delay passed"
argument_list|,
name|preemptionThread
operator|.
name|totalAppsAdded
argument_list|()
argument_list|,
name|preemptionThread
operator|.
name|uniqueAppsAdded
argument_list|()
argument_list|)
expr_stmt|;
name|verifyLeafQueueStarvation
argument_list|()
expr_stmt|;
name|clock
operator|.
name|tickMsec
argument_list|(
name|FairSchedulerWithMockPreemption
operator|.
name|DELAY_FOR_NEXT_STARVATION_CHECK_MS
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
comment|// Wait for apps to be processed by MockPreemptionThread
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|6000
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|preemptionThread
operator|.
name|totalAppsAdded
argument_list|()
operator|>=
name|preemptionThread
operator|.
name|uniqueAppsAdded
argument_list|()
operator|*
literal|2
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
literal|"Each app should be marked as starved once"
operator|+
literal|" at each scheduler update above"
argument_list|,
name|preemptionThread
operator|.
name|totalAppsAdded
argument_list|()
argument_list|,
name|preemptionThread
operator|.
name|uniqueAppsAdded
argument_list|()
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test to verify app starvation is computed only when the cluster    * utilization threshold is over the preemption threshold.    */
annotation|@
name|Test
DECL|method|testClusterUtilizationThreshold ()
specifier|public
name|void
name|testClusterUtilizationThreshold
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set preemption threshold to 1.1, so the utilization is always lower
name|conf
operator|.
name|setFloat
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|PREEMPTION_THRESHOLD
argument_list|,
literal|1.1f
argument_list|)
expr_stmt|;
name|setupClusterAndSubmitJobs
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"FSContext does not have an FSStarvedApps instance"
argument_list|,
name|scheduler
operator|.
name|getContext
argument_list|()
operator|.
name|getStarvedApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Found starved apps when preemption threshold is over 100%"
argument_list|,
literal|0
argument_list|,
name|preemptionThread
operator|.
name|totalAppsAdded
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyLeafQueueStarvation ()
specifier|private
name|void
name|verifyLeafQueueStarvation
parameter_list|()
block|{
for|for
control|(
name|String
name|q
range|:
name|QUEUES
control|)
block|{
if|if
condition|(
operator|!
name|q
operator|.
name|equals
argument_list|(
literal|"no-preemption"
argument_list|)
condition|)
block|{
name|boolean
name|isStarved
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
name|q
argument_list|,
literal|false
argument_list|)
operator|.
name|isStarved
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|isStarved
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setupClusterAndSubmitJobs ()
specifier|private
name|void
name|setupClusterAndSubmitJobs
parameter_list|()
throws|throws
name|Exception
block|{
name|setupStarvedCluster
argument_list|()
expr_stmt|;
name|submitAppsToEachLeafQueue
argument_list|()
expr_stmt|;
name|sendEnoughNodeUpdatesToAssignFully
argument_list|()
expr_stmt|;
comment|// Sleep to hit the preemption timeouts
name|clock
operator|.
name|tickMsec
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|// Scheduler update to populate starved apps
name|scheduler
operator|.
name|update
argument_list|()
expr_stmt|;
block|}
comment|/**    * Setup the cluster for starvation testing:    * 1. Create FS allocation file    * 2. Create and start MockRM    * 3. Add two nodes to the cluster    * 4. Submit an app that uses up all resources on the cluster    */
DECL|method|setupStarvedCluster ()
specifier|private
name|void
name|setupStarvedCluster
parameter_list|()
throws|throws
name|IOException
block|{
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
comment|// Default queue
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"default\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// Queue with preemption disabled
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"no-preemption\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<fairSharePreemptionThreshold>0"
operator|+
literal|"</fairSharePreemptionThreshold>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// Queue with minshare preemption enabled
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"minshare\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<fairSharePreemptionThreshold>0"
operator|+
literal|"</fairSharePreemptionThreshold>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<minSharePreemptionTimeout>0"
operator|+
literal|"</minSharePreemptionTimeout>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<minResources>2048mb,2vcores</minResources>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// FAIR queue with fairshare preemption enabled
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"fairshare\">"
argument_list|)
expr_stmt|;
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
name|out
operator|.
name|println
argument_list|(
literal|"<schedulingPolicy>fair</schedulingPolicy>"
argument_list|)
expr_stmt|;
name|addChildQueue
argument_list|(
name|out
argument_list|,
literal|"fair"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
comment|// DRF queue with fairshare preemption enabled
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"drf\">"
argument_list|)
expr_stmt|;
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
name|out
operator|.
name|println
argument_list|(
literal|"<schedulingPolicy>drf</schedulingPolicy>"
argument_list|)
expr_stmt|;
name|addChildQueue
argument_list|(
name|out
argument_list|,
literal|"drf"
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
literal|"<defaultQueueSchedulingPolicy>drf"
operator|+
literal|"</defaultQueueSchedulingPolicy>"
argument_list|)
expr_stmt|;
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
name|preemptionThread
operator|=
operator|(
name|FairSchedulerWithMockPreemption
operator|.
name|MockPreemptionThread
operator|)
name|scheduler
operator|.
name|preemptionThread
expr_stmt|;
comment|// Create and add two nodes to the cluster
name|addNode
argument_list|(
name|NODE_CAPACITY_MULTIPLE
operator|*
literal|1024
argument_list|,
name|NODE_CAPACITY_MULTIPLE
argument_list|)
expr_stmt|;
name|addNode
argument_list|(
name|NODE_CAPACITY_MULTIPLE
operator|*
literal|1024
argument_list|,
name|NODE_CAPACITY_MULTIPLE
argument_list|)
expr_stmt|;
comment|// Create an app that takes up all the resources on the cluster
name|ApplicationAttemptId
name|app
init|=
name|createSchedulingRequest
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|,
literal|"root.default"
argument_list|,
literal|"default"
argument_list|,
literal|8
argument_list|)
decl_stmt|;
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
name|scheduler
operator|.
name|getSchedulerApp
argument_list|(
name|app
argument_list|)
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addChildQueue (PrintWriter out, String policy)
specifier|private
name|void
name|addChildQueue
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|String
name|policy
parameter_list|)
block|{
comment|// Child queue under fairshare with same settings
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"child\">"
argument_list|)
expr_stmt|;
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
name|out
operator|.
name|println
argument_list|(
literal|"<schedulingPolicy>"
operator|+
name|policy
operator|+
literal|"</schedulingPolicy>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
block|}
DECL|method|submitAppsToEachLeafQueue ()
specifier|private
name|void
name|submitAppsToEachLeafQueue
parameter_list|()
block|{
for|for
control|(
name|String
name|queue
range|:
name|QUEUES
control|)
block|{
name|createSchedulingRequest
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|,
literal|"root."
operator|+
name|queue
argument_list|,
literal|"user"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|scheduler
operator|.
name|update
argument_list|()
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
block|}
end_class

end_unit

