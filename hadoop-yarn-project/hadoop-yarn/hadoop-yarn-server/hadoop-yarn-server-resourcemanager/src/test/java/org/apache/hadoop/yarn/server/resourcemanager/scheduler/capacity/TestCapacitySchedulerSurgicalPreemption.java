begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|monitor
operator|.
name|SchedulingEditPolicy
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
name|monitor
operator|.
name|capacity
operator|.
name|ProportionalCapacityPreemptionPolicy
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

begin_class
DECL|class|TestCapacitySchedulerSurgicalPreemption
specifier|public
class|class
name|TestCapacitySchedulerSurgicalPreemption
extends|extends
name|CapacitySchedulerPreemptionTestBase
block|{
annotation|@
name|Override
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|PREEMPTION_SELECT_CANDIDATES_FOR_RESERVED_CONTAINERS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSimpleSurgicalPreemption ()
specifier|public
name|void
name|testSimpleSurgicalPreemption
parameter_list|()
throws|throws
name|Exception
block|{
comment|/**      * Test case: Submit two application (app1/app2) to different queues, queue      * structure:      *      *<pre>      *             Root      *            /  |  \      *           a   b   c      *          10   20  70      *</pre>      *      * 1) Two nodes (n1/n2) in the cluster, each of them has 20G.      *      * 2) app1 submit to queue-a first, it asked 32 * 1G containers      * We will allocate 16 on n1 and 16 on n2.      *      * 3) app2 submit to queue-c, ask for one 1G container (for AM)      *      * 4) app2 asks for another 6G container, it will be reserved on n1      *      * Now: we have:      * n1: 17 from app1, 1 from app2, and 1 reserved from app2      * n2: 16 from app1.      *      * After preemption, we should expect:      * Preempt 4 containers from app1 on n1.      */
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|setNodeLabelManager
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|20
operator|*
name|GB
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h2:1234"
argument_list|,
literal|20
operator|*
name|GB
argument_list|)
decl_stmt|;
name|CapacityScheduler
name|cs
init|=
operator|(
name|CapacityScheduler
operator|)
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|RMNode
name|rmNode1
init|=
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|RMNode
name|rmNode2
init|=
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm2
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
comment|// launch an app to queue, AM container should be launched in nm1
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|1
operator|*
name|GB
argument_list|,
literal|32
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|// Do allocation for node1/node2
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|32
condition|;
name|i
operator|++
control|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNode1
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
name|rmNode2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// App1 should have 33 containers now
name|FiCaSchedulerApp
name|schedulerApp1
init|=
name|cs
operator|.
name|getApplicationAttempt
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|33
argument_list|,
name|schedulerApp1
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 17 from n1 and 16 from n2
name|waitNumberOfLiveContainersOnNodeFromApp
argument_list|(
name|cs
operator|.
name|getNode
argument_list|(
name|rmNode1
operator|.
name|getNodeID
argument_list|()
argument_list|)
argument_list|,
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|17
argument_list|)
expr_stmt|;
name|waitNumberOfLiveContainersOnNodeFromApp
argument_list|(
name|cs
operator|.
name|getNode
argument_list|(
name|rmNode2
operator|.
name|getNodeID
argument_list|()
argument_list|)
argument_list|,
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|16
argument_list|)
expr_stmt|;
comment|// Submit app2 to queue-c and asks for a 1G container for AM
name|RMApp
name|app2
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|MockAM
name|am2
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app2
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
comment|// NM1/NM2 has available resource = 2G/4G
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
operator|*
name|GB
argument_list|,
name|cs
operator|.
name|getNode
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getUnallocatedResource
argument_list|()
operator|.
name|getMemorySize
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
name|cs
operator|.
name|getNode
argument_list|(
name|nm2
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getUnallocatedResource
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
comment|// AM asks for a 1 * GB container
name|am2
operator|.
name|allocate
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|6
operator|*
name|GB
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Call allocation once on n1, we should expect the container reserved on n1
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNode1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|cs
operator|.
name|getNode
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
operator|.
name|getReservedContainer
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get edit policy and do one update
name|SchedulingEditPolicy
name|editPolicy
init|=
name|getSchedulingEditPolicy
argument_list|(
name|rm1
argument_list|)
decl_stmt|;
comment|// Call edit schedule twice, and check if 4 containers from app1 at n1 killed
name|editPolicy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|editPolicy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|waitNumberOfLiveContainersFromApp
argument_list|(
name|schedulerApp1
argument_list|,
literal|29
argument_list|)
expr_stmt|;
comment|// 13 from n1 (4 preempted) and 16 from n2
name|waitNumberOfLiveContainersOnNodeFromApp
argument_list|(
name|cs
operator|.
name|getNode
argument_list|(
name|rmNode1
operator|.
name|getNodeID
argument_list|()
argument_list|)
argument_list|,
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|13
argument_list|)
expr_stmt|;
name|waitNumberOfLiveContainersOnNodeFromApp
argument_list|(
name|cs
operator|.
name|getNode
argument_list|(
name|rmNode2
operator|.
name|getNodeID
argument_list|()
argument_list|)
argument_list|,
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSurgicalPreemptionWithAvailableResource ()
specifier|public
name|void
name|testSurgicalPreemptionWithAvailableResource
parameter_list|()
throws|throws
name|Exception
block|{
comment|/**      * Test case: Submit two application (app1/app2) to different queues, queue      * structure:      *      *<pre>      *             Root      *            /  |  \      *           a   b   c      *          10   20  70      *</pre>      *      * 1) Two nodes (n1/n2) in the cluster, each of them has 20G.      *      * 2) app1 submit to queue-a first, it asked 38 * 1G containers      * We will allocate 20 on n1 and 19 on n2.      *      * 3) app2 submit to queue-c, ask for one 4G container (for AM)      *      * After preemption, we should expect:      * Preempt 3 containers from app1 and AM of app2 successfully allocated.      */
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|setNodeLabelManager
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|20
operator|*
name|GB
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h2:1234"
argument_list|,
literal|20
operator|*
name|GB
argument_list|)
decl_stmt|;
name|CapacityScheduler
name|cs
init|=
operator|(
name|CapacityScheduler
operator|)
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|RMNode
name|rmNode1
init|=
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|RMNode
name|rmNode2
init|=
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm2
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
comment|// launch an app to queue, AM container should be launched in nm1
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|1
operator|*
name|GB
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|1
operator|*
name|GB
argument_list|,
literal|38
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|// Do allocation for node1/node2
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|38
condition|;
name|i
operator|++
control|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNode1
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
name|rmNode2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// App1 should have 31 containers now
name|FiCaSchedulerApp
name|schedulerApp1
init|=
name|cs
operator|.
name|getApplicationAttempt
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|39
argument_list|,
name|schedulerApp1
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 17 from n1 and 16 from n2
name|waitNumberOfLiveContainersOnNodeFromApp
argument_list|(
name|cs
operator|.
name|getNode
argument_list|(
name|rmNode1
operator|.
name|getNodeID
argument_list|()
argument_list|)
argument_list|,
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|waitNumberOfLiveContainersOnNodeFromApp
argument_list|(
name|cs
operator|.
name|getNode
argument_list|(
name|rmNode2
operator|.
name|getNodeID
argument_list|()
argument_list|)
argument_list|,
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|19
argument_list|)
expr_stmt|;
comment|// Submit app2 to queue-c and asks for a 4G container for AM
name|RMApp
name|app2
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|4
operator|*
name|GB
argument_list|,
literal|"app"
argument_list|,
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|FiCaSchedulerApp
name|schedulerApp2
init|=
name|cs
operator|.
name|getApplicationAttempt
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|app2
operator|.
name|getApplicationId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// Call editSchedule: containers are selected to be preemption candidate
name|ProportionalCapacityPreemptionPolicy
name|editPolicy
init|=
operator|(
name|ProportionalCapacityPreemptionPolicy
operator|)
name|getSchedulingEditPolicy
argument_list|(
name|rm1
argument_list|)
decl_stmt|;
name|editPolicy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|editPolicy
operator|.
name|getToPreemptContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Call editSchedule again: selected containers are killed
name|editPolicy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|waitNumberOfLiveContainersFromApp
argument_list|(
name|schedulerApp1
argument_list|,
literal|36
argument_list|)
expr_stmt|;
comment|// Call allocation, containers are reserved
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNode1
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
name|rmNode2
argument_list|)
argument_list|)
expr_stmt|;
name|waitNumberOfReservedContainersFromApp
argument_list|(
name|schedulerApp2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Call editSchedule twice and allocation once, container should get allocated
name|editPolicy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|editPolicy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|int
name|tick
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|schedulerApp2
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
operator|&&
name|tick
operator|<
literal|10
condition|)
block|{
name|cs
operator|.
name|handle
argument_list|(
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|rmNode1
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
name|rmNode2
argument_list|)
argument_list|)
expr_stmt|;
name|tick
operator|++
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|waitNumberOfReservedContainersFromApp
argument_list|(
name|schedulerApp2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

