begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity
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
name|monitor
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|CapacitySchedulerConfiguration
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
name|IOException
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
name|argThat
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
name|times
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
name|verify
import|;
end_import

begin_class
DECL|class|TestProportionalCapacityPreemptionPolicyForReservedContainers
specifier|public
class|class
name|TestProportionalCapacityPreemptionPolicyForReservedContainers
extends|extends
name|ProportionalCapacityPreemptionPolicyMockFramework
block|{
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|super
operator|.
name|setup
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
name|policy
operator|=
operator|new
name|ProportionalCapacityPreemptionPolicy
argument_list|(
name|rmContext
argument_list|,
name|cs
argument_list|,
name|mClock
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionForSimpleReservedContainer ()
specifier|public
name|void
name|testPreemptionForSimpleReservedContainer
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The simplest test of reserved container, Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      * Guaranteed resource of a/b are 50:50      * Total cluster resource = 100      * - A has 90 containers on two node, n1 has 45, n2 has 45, size of each      * container is 1.      * - B has am container at n1, and reserves 1 container with size = 9 at n1,      * so B needs to preempt 9 containers from A at n1 instead of randomly      * preempt from n1 and n2.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 / n2 has no label
literal|"n1= res=50;"
operator|+
literal|"n2= res=50"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 100 9 9]);"
operator|+
comment|//root
literal|"-a(=[50 100 90 0]);"
operator|+
comment|// a
literal|"-b(=[50 100 10 9 9])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,45,false)"
comment|// 45 in n1
operator|+
literal|"(1,1,n2,,45,false);"
operator|+
comment|// 45 in n2
literal|"b\t"
comment|// app2 in b
operator|+
literal|"(1,1,n1,,1,false)"
comment|// AM container in n1
operator|+
literal|"(1,9,n1,,1,true)"
decl_stmt|;
comment|// 1 container with size=9 reserved at n1
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Total 5 preempted from app1 at n1, don't preempt container from other
comment|// app/node
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|5
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|5
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n1"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUseReservedAndFifoSelectorTogether ()
specifier|public
name|void
name|testUseReservedAndFifoSelectorTogether
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      * Guaranteed resource of a/b are 30:70      * Total cluster resource = 100      * - A has 45 containers on two node, n1 has 10, n2 has 35, size of each      * container is 1.      * - B has 5 containers at n2, and reserves 1 container with size = 50 at n1,      *   B also has 20 pending resources.      * so B needs to preempt:      * - 10 containers from n1 (for reserved)      * - 5 containers from n2 for pending resources      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 / n2 has no label
literal|"n1= res=50;"
operator|+
literal|"n2= res=50"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 100 70 10]);"
operator|+
comment|//root
literal|"-a(=[30 100 45 0]);"
operator|+
comment|// a
literal|"-b(=[70 100 55 70 50])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n2,,35,false)"
comment|// 35 in n2
operator|+
literal|"(1,1,n1,,10,false);"
operator|+
comment|// 10 in n1
literal|"b\t"
comment|// app2 in b
operator|+
literal|"(1,1,n2,,5,false)"
comment|// 5 in n2
operator|+
literal|"(1,50,n1,,1,true)"
decl_stmt|;
comment|// 1 container with size=50 reserved at n1
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|15
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n1"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|5
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n2"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReservedSelectorSkipsAMContainer ()
specifier|public
name|void
name|testReservedSelectorSkipsAMContainer
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      * Guaranteed resource of a/b are 30:70      * Total cluster resource = 100      * - A has 45 containers on two node, n1 has 10, n2 has 35, size of each      * container is 1.      * - B has 5 containers at n2, and reserves 1 container with size = 50 at n1,      *   B also has 20 pending resources.      *      * Ideally B needs to preempt:      * - 10 containers from n1 (for reserved)      * - 5 containers from n2 for pending resources      *      * However, since one AM container is located at n1 (from queueA), we cannot      * preempt 10 containers from n1 for reserved container. Instead, we will      * preempt 15 containers from n2, since containers from queueA launched in n2      * are later than containers from queueA launched in n1 (FIFO order of containers)      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 / n2 has no label
literal|"n1= res=50;"
operator|+
literal|"n2= res=50"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 100 70 10]);"
operator|+
comment|//root
literal|"-a(=[30 100 45 0]);"
operator|+
comment|// a
literal|"-b(=[70 100 55 70 50])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,10,false)"
comment|// 10 in n1
operator|+
literal|"(1,1,n2,,35,false);"
operator|+
comment|// 35 in n2
literal|"b\t"
comment|// app2 in b
operator|+
literal|"(1,1,n2,,5,false)"
comment|// 5 in n2
operator|+
literal|"(1,50,n1,,1,true)"
decl_stmt|;
comment|// 1 container with size=50 reserved at n1
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|15
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n1"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|15
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n2"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionForReservedContainerRespectGuaranteedResource ()
specifier|public
name|void
name|testPreemptionForReservedContainerRespectGuaranteedResource
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      * Guaranteed resource of a/b are 85:15      * Total cluster resource = 100      * - A has 90 containers on two node, n1 has 45, n2 has 45, size of each      * container is 1.      * - B has am container at n1, and reserves 1 container with size = 9 at n1,      *      * If we preempt 9 containers from queue-A, queue-A will be below its      * guaranteed resource = 90 - 9 = 81< 85.      *      * So no preemption will take place      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 / n2 has no label
literal|"n1= res=50;"
operator|+
literal|"n2= res=50"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 100 9 9]);"
operator|+
comment|//root
literal|"-a(=[85 100 90 0]);"
operator|+
comment|// a
literal|"-b(=[15 100 10 9 9])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,45,false)"
comment|// 45 in n1
operator|+
literal|"(1,1,n2,,45,false);"
operator|+
comment|// 45 in n2
literal|"b\t"
comment|// app2 in b
operator|+
literal|"(1,1,n1,,1,false)"
comment|// AM container in n1
operator|+
literal|"(1,9,n1,,1,true)"
decl_stmt|;
comment|// 1 container with size=9 reserved at n1
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionForReservedContainerWhichHasAvailableResource ()
specifier|public
name|void
name|testPreemptionForReservedContainerWhichHasAvailableResource
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Guaranteed resource of a/b are 50:50      * Total cluster resource = 100      * - A has 90 containers on two node, n1 has 45, n2 has 45, size of each      * container is 1.      * - B has am container at n1, and reserves 1 container with size = 9 at n1,      *      * So we can get 4 containers preempted after preemption.      * (reserved 5 + preempted 4) = 9      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 / n2 has no label
literal|"n1= res=50;"
operator|+
literal|"n2= res=50"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 99 9 9]);"
operator|+
comment|//root
literal|"-a(=[50 100 90 0]);"
operator|+
comment|// a
literal|"-b(=[50 100 9 9 9])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,45,false)"
comment|// 45 in n1
operator|+
literal|"(1,1,n2,,45,false);"
operator|+
comment|// 45 in n2
literal|"b\t"
comment|// app2 in b
operator|+
literal|"(1,9,n1,,1,true)"
decl_stmt|;
comment|// 1 container with size=9 reserved at n1
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Total 4 preempted from app1 at n1, don't preempt container from other
comment|// app/node
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n1"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n2"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionForReservedContainerWhichHasNondivisibleAvailableResource ()
specifier|public
name|void
name|testPreemptionForReservedContainerWhichHasNondivisibleAvailableResource
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Guaranteed resource of a/b are 50:50      * Total cluster resource = 100      * - A has 45 containers on two node, size of each container is 2,      *   n1 has 23, n2 has 22      * - B reserves 1 container with size = 9 at n1,      *      * So we can get 4 containers (total-resource = 8) preempted after      * preemption. Actual required is 3.5, but we need to preempt integer      * number of containers      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 / n2 has no label
literal|"n1= res=50;"
operator|+
literal|"n2= res=50"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 99 9 9]);"
operator|+
comment|//root
literal|"-a(=[50 100 90 0]);"
operator|+
comment|// a
literal|"-b(=[50 100 9 9 9])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,2,n1,,24,false)"
comment|// 48 in n1
operator|+
literal|"(1,2,n2,,23,false);"
operator|+
comment|// 46 in n2
literal|"b\t"
comment|// app2 in b
operator|+
literal|"(1,9,n1,,1,true)"
decl_stmt|;
comment|// 1 container with size=9 reserved at n1
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Total 4 preempted from app1 at n1, don't preempt container from other
comment|// app/node
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n1"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n2"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionForReservedContainerRespectAvailableResources ()
specifier|public
name|void
name|testPreemptionForReservedContainerRespectAvailableResources
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Guaranteed resource of a/b are 50:50      * Total cluster resource = 100, 4 nodes, 25 on each node      * - A has 10 containers on every node, size of container is 2      * - B reserves 1 container with size = 9 at n1,      *      * So even if we cannot allocate container for B now, no preemption should      * happen since there're plenty of available resources.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
literal|"n1= res=25;"
operator|+
literal|"n2= res=25;"
operator|+
literal|"n3= res=25;"
operator|+
literal|"n4= res=25;"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 89 9 9]);"
operator|+
comment|//root
literal|"-a(=[50 100 80 0]);"
operator|+
comment|// a
literal|"-b(=[50 100 9 9 9])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,2,n1,,10,false)"
comment|// 10 in n1
operator|+
literal|"(1,2,n2,,10,false)"
comment|// 10 in n2
operator|+
literal|"(1,2,n3,,10,false)"
comment|// 10 in n3
operator|+
literal|"(1,2,n4,,10,false);"
operator|+
comment|// 10 in n4
literal|"b\t"
comment|// app2 in b
operator|+
literal|"(1,9,n1,,1,true)"
decl_stmt|;
comment|// 1 container with size=5 reserved at n1
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// No preemption should happen
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n1"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n2"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n3"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestForQueueAndNode
argument_list|(
name|getAppAttemptId
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"a"
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n4"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

