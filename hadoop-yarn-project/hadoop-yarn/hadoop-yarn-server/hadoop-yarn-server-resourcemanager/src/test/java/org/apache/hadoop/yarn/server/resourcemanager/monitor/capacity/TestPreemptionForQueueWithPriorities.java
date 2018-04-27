begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
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
name|never
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
DECL|class|TestPreemptionForQueueWithPriorities
specifier|public
class|class
name|TestPreemptionForQueueWithPriorities
extends|extends
name|ProportionalCapacityPreemptionPolicyMockFramework
block|{
comment|// Initialize resource map
DECL|field|riMap
specifier|private
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
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
comment|// Initialize mandatory resources
name|ResourceInformation
name|memory
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
argument_list|,
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getUnits
argument_list|()
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB
argument_list|)
decl_stmt|;
name|ResourceInformation
name|vcores
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getName
argument_list|()
argument_list|,
name|ResourceInformation
operator|.
name|VCORES
operator|.
name|getUnits
argument_list|()
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES
argument_list|)
decl_stmt|;
name|riMap
operator|.
name|put
argument_list|(
name|ResourceInformation
operator|.
name|MEMORY_URI
argument_list|,
name|memory
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
name|vcores
argument_list|)
expr_stmt|;
name|ResourceUtils
operator|.
name|initializeResourcesFromResourceInformationMap
argument_list|(
name|riMap
argument_list|)
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
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
DECL|method|testPreemptionForHighestPriorityUnderutilizedQueue ()
specifier|public
name|void
name|testPreemptionForHighestPriorityUnderutilizedQueue
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The simplest test of queue with priorities, Queue structure is:      *      *<pre>      *        root      *       / |  \      *      a  b   c      *</pre>      *      * For priorities      * - a=1      * - b/c=2      *      * So c will preempt more resource from a, till a reaches guaranteed      * resource.      */
name|String
name|labelsConfig
init|=
literal|"=100,true"
decl_stmt|;
comment|// default partition
name|String
name|nodesConfig
init|=
literal|"n1="
decl_stmt|;
comment|// only one node
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 100 100]);"
operator|+
comment|//root
literal|"-a(=[30 100 40 50]){priority=1};"
operator|+
comment|// a
literal|"-b(=[30 100 59 50]){priority=2};"
operator|+
comment|// b
literal|"-c(=[40 100 1 25]){priority=2}"
decl_stmt|;
comment|// c
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t(1,1,n1,,40,false);"
operator|+
comment|// app1 in a
literal|"b\t(1,1,n1,,59,false);"
operator|+
comment|// app2 in b
literal|"c\t(1,1,n1,,1,false);"
decl_stmt|;
comment|// app3 in c
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
comment|// 10 preempted from app1, 15 preempted from app2, and nothing preempted
comment|// from app3
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
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionForLowestPriorityUnderutilizedQueue ()
specifier|public
name|void
name|testPreemptionForLowestPriorityUnderutilizedQueue
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Similar to above, make sure we can still make sure less utilized queue      * can get resource first regardless of priority.      *      * Queue structure is:      *      *<pre>      *        root      *       / |  \      *      a  b   c      *</pre>      *      * For priorities      * - a=1      * - b=2      * - c=0      *      * So c will preempt more resource from a, till a reaches guaranteed      * resource.      */
name|String
name|labelsConfig
init|=
literal|"=100,true"
decl_stmt|;
comment|// default partition
name|String
name|nodesConfig
init|=
literal|"n1="
decl_stmt|;
comment|// only one node
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 100 100]);"
operator|+
comment|//root
literal|"-a(=[30 100 40 50]){priority=1};"
operator|+
comment|// a
literal|"-b(=[30 100 59 50]){priority=2};"
operator|+
comment|// b
literal|"-c(=[40 100 1 25]){priority=0}"
decl_stmt|;
comment|// c
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t(1,1,n1,,40,false);"
operator|+
comment|// app1 in a
literal|"b\t(1,1,n1,,59,false);"
operator|+
comment|// app2 in b
literal|"c\t(1,1,n1,,1,false);"
decl_stmt|;
comment|// app3 in c
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
comment|// 10 preempted from app1, 15 preempted from app2, and nothing preempted
comment|// from app3
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
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionWontHappenBetweenSatisfiedQueues ()
specifier|public
name|void
name|testPreemptionWontHappenBetweenSatisfiedQueues
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * No preemption happen if a queue is already satisfied, regardless of      * priority      *      * Queue structure is:      *      *<pre>      *        root      *       / |  \      *      a  b   c      *</pre>      *      * For priorities      * - a=1      * - b=1      * - c=2      *      * When c is satisfied, it will not preempt any resource from other queues      */
name|String
name|labelsConfig
init|=
literal|"=100,true"
decl_stmt|;
comment|// default partition
name|String
name|nodesConfig
init|=
literal|"n1="
decl_stmt|;
comment|// only one node
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 100 100]);"
operator|+
comment|//root
literal|"-a(=[30 100 0 0]){priority=1};"
operator|+
comment|// a
literal|"-b(=[30 100 40 50]){priority=1};"
operator|+
comment|// b
literal|"-c(=[40 100 60 25]){priority=2}"
decl_stmt|;
comment|// c
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"b\t(1,1,n1,,40,false);"
operator|+
comment|// app1 in b
literal|"c\t(1,1,n1,,60,false)"
decl_stmt|;
comment|// app2 in c
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
comment|// Nothing preempted
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
name|never
argument_list|()
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
DECL|method|testPreemptionForMultipleQueuesInTheSamePriorityBuckets ()
specifier|public
name|void
name|testPreemptionForMultipleQueuesInTheSamePriorityBuckets
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * When a cluster has different priorities, each priority has multiple      * queues, preemption policy should try to balance resource between queues      * with same priority by ratio of their capacities      *      * Queue structure is:      *      *<pre>      * root      * - a (capacity=10), p=1      * - b (capacity=15), p=1      * - c (capacity=20), p=2      * - d (capacity=25), p=2      * - e (capacity=30), p=2      *</pre>      */
name|String
name|labelsConfig
init|=
literal|"=100,true"
decl_stmt|;
comment|// default partition
name|String
name|nodesConfig
init|=
literal|"n1="
decl_stmt|;
comment|// only one node
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 100 100]);"
operator|+
comment|//root
literal|"-a(=[10 100 35 50]){priority=1};"
operator|+
comment|// a
literal|"-b(=[15 100 25 50]){priority=1};"
operator|+
comment|// b
literal|"-c(=[20 100 39 50]){priority=2};"
operator|+
comment|// c
literal|"-d(=[25 100 0 0]){priority=2};"
operator|+
comment|// d
literal|"-e(=[30 100 1 99]){priority=2}"
decl_stmt|;
comment|// e
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t(1,1,n1,,35,false);"
operator|+
comment|// app1 in a
literal|"b\t(1,1,n1,,25,false);"
operator|+
comment|// app2 in b
literal|"c\t(1,1,n1,,39,false);"
operator|+
comment|// app3 in c
literal|"e\t(1,1,n1,,1,false)"
decl_stmt|;
comment|// app4 in e
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
comment|// 23 preempted from app1, 6 preempted from app2, and nothing preempted
comment|// from app3/app4
comment|// (After preemption, a has 35 - 23 = 12, b has 25 - 6 = 19, so a:b after
comment|//  preemption is 1.58, close to 1.50)
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|23
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
literal|6
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
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreemptionForPriorityAndDisablePreemption ()
specifier|public
name|void
name|testPreemptionForPriorityAndDisablePreemption
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * When a cluster has different priorities, each priority has multiple      * queues, preemption policy should try to balance resource between queues      * with same priority by ratio of their capacities.      *      * But also we need to make sure preemption disable will be honered      * regardless of priority.      *      * Queue structure is:      *      *<pre>      * root      * - a (capacity=10), p=1      * - b (capacity=15), p=1      * - c (capacity=20), p=2      * - d (capacity=25), p=2      * - e (capacity=30), p=2      *</pre>      */
name|String
name|labelsConfig
init|=
literal|"=100,true"
decl_stmt|;
comment|// default partition
name|String
name|nodesConfig
init|=
literal|"n1="
decl_stmt|;
comment|// only one node
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 100 100]);"
operator|+
comment|//root
literal|"-a(=[10 100 35 50]){priority=1,disable_preemption=true};"
operator|+
comment|// a
literal|"-b(=[15 100 25 50]){priority=1};"
operator|+
comment|// b
literal|"-c(=[20 100 39 50]){priority=2};"
operator|+
comment|// c
literal|"-d(=[25 100 0 0]){priority=2};"
operator|+
comment|// d
literal|"-e(=[30 100 1 99]){priority=2}"
decl_stmt|;
comment|// e
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t(1,1,n1,,35,false);"
operator|+
comment|// app1 in a
literal|"b\t(1,1,n1,,25,false);"
operator|+
comment|// app2 in b
literal|"c\t(1,1,n1,,39,false);"
operator|+
comment|// app3 in c
literal|"e\t(1,1,n1,,1,false)"
decl_stmt|;
comment|// app4 in e
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
comment|// We suppose to preempt some resource from A, but now since queueA
comment|// disables preemption, so we need to preempt some resource from B and
comment|// some from C even if C has higher priority than A
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
literal|9
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
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|19
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPriorityPreemptionForHierarchicalOfQueues ()
specifier|public
name|void
name|testPriorityPreemptionForHierarchicalOfQueues
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * When a queue has multiple hierarchy and different priorities:      *      *<pre>      * root      * - a (capacity=30), p=1      *   - a1 (capacity=40), p=1      *   - a2 (capacity=60), p=1      * - b (capacity=30), p=1      *   - b1 (capacity=50), p=1      *   - b1 (capacity=50), p=2      * - c (capacity=40), p=2      *</pre>      */
name|String
name|labelsConfig
init|=
literal|"=100,true"
decl_stmt|;
comment|// default partition
name|String
name|nodesConfig
init|=
literal|"n1="
decl_stmt|;
comment|// only one node
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 100 100]);"
operator|+
comment|//root
literal|"-a(=[30 100 40 50]){priority=1};"
operator|+
comment|// a
literal|"--a1(=[12 100 20 50]){priority=1};"
operator|+
comment|// a1
literal|"--a2(=[18 100 20 50]){priority=1};"
operator|+
comment|// a2
literal|"-b(=[30 100 59 50]){priority=1};"
operator|+
comment|// b
literal|"--b1(=[15 100 30 50]){priority=1};"
operator|+
comment|// b1
literal|"--b2(=[15 100 29 50]){priority=2};"
operator|+
comment|// b2
literal|"-c(=[40 100 1 30]){priority=1}"
decl_stmt|;
comment|// c
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a1\t(1,1,n1,,20,false);"
operator|+
comment|// app1 in a1
literal|"a2\t(1,1,n1,,20,false);"
operator|+
comment|// app2 in a2
literal|"b1\t(1,1,n1,,30,false);"
operator|+
comment|// app3 in b1
literal|"b2\t(1,1,n1,,29,false);"
operator|+
comment|// app4 in b2
literal|"c\t(1,1,n1,,29,false)"
decl_stmt|;
comment|// app5 in c
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
comment|// Preemption should first divide capacities between a / b, and b2 should
comment|// get less preemption than b1 (because b2 has higher priority)
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
name|never
argument_list|()
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
literal|3
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
literal|9
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
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPriorityPreemptionWithMandatoryResourceForHierarchicalOfQueues ()
specifier|public
name|void
name|testPriorityPreemptionWithMandatoryResourceForHierarchicalOfQueues
parameter_list|()
throws|throws
name|Exception
block|{
comment|/**      * Queue structure is:      *      *<pre>      *           root      *           /  \      *          a    b      *        /  \  /  \      *       a1  a2 b1  b2      *</pre>      *      * a2 is underserved and need more resource. b2 will be preemptable.      */
name|String
name|labelsConfig
init|=
literal|"=100:200,true"
decl_stmt|;
comment|// default partition
name|String
name|nodesConfig
init|=
literal|"n1="
decl_stmt|;
comment|// only one node
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100:200 100:200 100:200 100:200]);"
operator|+
comment|//root
literal|"-a(=[50:100 100:200 20:40 60:100]){priority=1};"
operator|+
comment|// a
literal|"--a1(=[10:20 100:200 10:30 30:20]){priority=1};"
operator|+
comment|// a1
literal|"--a2(=[40:80 100:200 10:10 30:80]){priority=1};"
operator|+
comment|// a2
literal|"-b(=[50:100 100:200 80:160 40:100]){priority=1};"
operator|+
comment|// b
literal|"--b1(=[20:40 100:200 20:40 20:70]){priority=2};"
operator|+
comment|// b1
literal|"--b2(=[30:60 100:200 60:120 20:30]){priority=1}"
decl_stmt|;
comment|// b2
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a1\t(1,1:4,n1,,10,false);"
operator|+
comment|// app1 in a1
literal|"a2\t(1,1:1,n1,,10,false);"
operator|+
comment|// app2 in a2
literal|"b1\t(1,3:4,n1,,10,false);"
operator|+
comment|// app3 in b1
literal|"b2\t(1,20:40,n1,,3,false)"
decl_stmt|;
comment|// app4 in b2
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Preemption should first divide capacities between a / b, and b1 should
comment|// get less preemption than b2 (because b1 has higher priority)
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
name|never
argument_list|()
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
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
literal|3
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
literal|2
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
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPriorityPreemptionWithMultipleResource ()
specifier|public
name|void
name|testPriorityPreemptionWithMultipleResource
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|RESOURCE_1
init|=
literal|"res1"
decl_stmt|;
name|riMap
operator|.
name|put
argument_list|(
name|RESOURCE_1
argument_list|,
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|RESOURCE_1
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
name|ResourceUtils
operator|.
name|initializeResourcesFromResourceInformationMap
argument_list|(
name|riMap
argument_list|)
expr_stmt|;
comment|/**      * Queue structure is:      *      *<pre>      *           root      *           /  \      *          a    b      *        /  \      *       a1  a2      *</pre>      *      * a1 and a2 are using most of resources.      * b needs more resources which is under served.      */
name|String
name|labelsConfig
init|=
literal|"=100:100:10,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
literal|"n1=;"
decl_stmt|;
comment|// n1 is default partition
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100:100:10 100:100:10 100:100:10 100:100:10]);"
operator|+
comment|//root
literal|"-a(=[50:60:3 100:100:10 80:90:10 30:20:4]){priority=1};"
operator|+
comment|// a
literal|"--a1(=[20:15:3 100:50:10 60:50:10 0]){priority=1};"
operator|+
comment|// a1
literal|"--a2(=[30:45 100:50:10 20:40 30:20:4]){priority=2};"
operator|+
comment|// a2
literal|"-b(=[50:40:7 100:100:10 20:10 30:10:2]){priority=1}"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a1\t"
comment|// app1 in a1
operator|+
literal|"(1,6:5:1,n1,,10,false);"
operator|+
literal|"a2\t"
comment|// app2 in a2
operator|+
literal|"(1,2:4,n1,,10,false);"
operator|+
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(1,2:1,n1,,10,false)"
decl_stmt|;
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Preemption should first divide capacities between a / b, and a2 should
comment|// get less preemption than a1 (because a2 has higher priority). More
comment|// specifically, a2 will not get preempted since the resource preempted
comment|// from a1 can satisfy b already.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|7
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
name|never
argument_list|()
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
name|verify
argument_list|(
name|mDisp
argument_list|,
name|never
argument_list|()
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

