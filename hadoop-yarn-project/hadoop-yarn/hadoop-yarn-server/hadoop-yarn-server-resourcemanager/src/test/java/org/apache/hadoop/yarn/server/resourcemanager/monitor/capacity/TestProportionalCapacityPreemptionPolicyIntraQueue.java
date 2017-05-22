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
name|server
operator|.
name|resourcemanager
operator|.
name|monitor
operator|.
name|capacity
operator|.
name|TestProportionalCapacityPreemptionPolicy
operator|.
name|IsPreemptionRequestFor
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

begin_comment
comment|/**  * Test class for IntraQueuePreemption scenarios.  */
end_comment

begin_class
DECL|class|TestProportionalCapacityPreemptionPolicyIntraQueue
specifier|public
class|class
name|TestProportionalCapacityPreemptionPolicyIntraQueue
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
name|INTRAQUEUE_PREEMPTION_ENABLED
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
DECL|method|testSimpleIntraQueuePreemption ()
specifier|public
name|void
name|testSimpleIntraQueuePreemption
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The simplest test preemption, Queue structure is:      *      *<pre>      *       root      *     /  | | \      *    a  b  c  d      *</pre>      *      * Guaranteed resource of a/b/c/d are 11:40:20:29 Total cluster resource =      * 100      * Scenario:      * Queue B has few running apps and two high priority apps have demand.      * Apps which are running at low priority (4) will preempt few of its      * resources to meet the demand.      */
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 79 120 0]);"
operator|+
comment|// root
literal|"-a(=[11 100 11 50 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 38 60 0]);"
operator|+
comment|// b
literal|"-c(=[20 100 10 10 0]);"
operator|+
comment|// c
literal|"-d(=[29 100 20 0 0])"
decl_stmt|;
comment|// d
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,
comment|// pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,6,false,25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,5,false,25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1,n1,,34,false,20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(4,1,n1,,2,false,10);"
operator|+
comment|// app4 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(5,1,n1,,1,false,10);"
operator|+
comment|// app5 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(6,1,n1,,1,false,10);"
operator|+
comment|// app6 in b
literal|"c\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,10,false,10);"
operator|+
literal|"d\t"
comment|// app7 in c
operator|+
literal|"(1,1,n1,,20,false,0)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// For queue B, app3 and app4 were of lower priority. Hence take 8
comment|// containers from them by hitting the intraQueuePreemptionDemand of 20%.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|1
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoIntraQueuePreemptionWithPreemptionDisabledOnQueues ()
specifier|public
name|void
name|testNoIntraQueuePreemptionWithPreemptionDisabledOnQueues
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * This test has the same configuration as testSimpleIntraQueuePreemption      * except that preemption is disabled specifically for each queue. The      * purpose is to test that disabling preemption on a specific queue will      * avoid intra-queue preemption.      */
name|conf
operator|.
name|setPreemptionDisabled
argument_list|(
literal|"root.a"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setPreemptionDisabled
argument_list|(
literal|"root.b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setPreemptionDisabled
argument_list|(
literal|"root.c"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setPreemptionDisabled
argument_list|(
literal|"root.d"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 80 120 0]);"
operator|+
comment|// root
literal|"-a(=[11 100 11 50 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 38 60 0]);"
operator|+
comment|// b
literal|"-c(=[20 100 10 10 0]);"
operator|+
comment|// c
literal|"-d(=[29 100 20 0 0])"
decl_stmt|;
comment|// d
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,
comment|// pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,6,false,25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,5,false,25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1,n1,,34,false,20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(4,1,n1,,2,false,10);"
operator|+
comment|// app4 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(5,1,n1,,1,false,10);"
operator|+
comment|// app5 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(6,1,n1,,1,false,10);"
operator|+
comment|// app6 in b
literal|"c\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,10,false,10);"
operator|+
literal|"d\t"
comment|// app7 in c
operator|+
literal|"(1,1,n1,,20,false,0)"
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
literal|4
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoPreemptionForSamePriorityApps ()
specifier|public
name|void
name|testNoPreemptionForSamePriorityApps
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *     /  | | \      *    a  b  c  d      *</pre>      *      * Guaranteed resource of a/b/c/d are 10:40:20:30 Total cluster resource =      * 100      * Scenario: In queue A/B, all apps are running at same priority. However      * there are many demands also from these apps. Since all apps are at same      * priority, preemption should not occur here.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 80 120 0]);"
operator|+
comment|// root
literal|"-a(=[10 100 10 50 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 40 60 0]);"
operator|+
comment|// b
literal|"-c(=[20 100 10 10 0]);"
operator|+
comment|// c
literal|"-d(=[30 100 20 0 0])"
decl_stmt|;
comment|// d
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,
comment|// pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,6,false,25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,5,false,25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(1,1,n1,,34,false,20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1,n1,,2,false,10);"
operator|+
comment|// app4 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1,n1,,1,false,20);"
operator|+
comment|// app5 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1,n1,,1,false,10);"
operator|+
comment|// app6 in b
literal|"c\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,10,false,10);"
operator|+
literal|"d\t"
comment|// app7 in c
operator|+
literal|"(1,1,n1,,20,false,0)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// For queue B, none of the apps should be preempted.
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
literal|4
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
literal|5
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
literal|6
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoPreemptionWhenQueueIsUnderCapacityLimit ()
specifier|public
name|void
name|testNoPreemptionWhenQueueIsUnderCapacityLimit
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Scenario:      * Guaranteed resource of a/b are 40:60 Total cluster resource = 100 BY      * default, this limit is 50%. Test to verify that there wont be any      * preemption since used capacity is under 50% for queue a/b even though      * there are demands from high priority apps in queue.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 35 80 0]);"
operator|+
comment|// root
literal|"-a(=[40 100 10 50 0]);"
operator|+
comment|// a
literal|"-b(=[60 100 25 30 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,5,false,25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(2,1,n1,,5,false,25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1,n1,,40,false,20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app1 in a
operator|+
literal|"(6,1,n1,,5,false,20)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// For queue A/B, none of the apps should be preempted as used capacity
comment|// is under 50%.
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
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLimitPreemptionWithMaxIntraQueuePreemptableLimit ()
specifier|public
name|void
name|testLimitPreemptionWithMaxIntraQueuePreemptableLimit
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Guaranteed resource of a/b are 40:60 Total cluster resource = 100      * maxIntraQueuePreemptableLimit by default is 50%. This test is to verify      * that the maximum preemption should occur upto 50%, eventhough demand is      * more.      */
comment|// Set max preemption limit as 50%.
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_MAX_ALLOWABLE_LIMIT
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 55 170 0]);"
operator|+
comment|// root
literal|"-a(=[40 100 10 50 0]);"
operator|+
comment|// a
literal|"-b(=[60 100 45 120 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,5,false,25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(2,1,n1,,5,false,25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1,n1,,40,false,20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app1 in a
operator|+
literal|"(6,1,n1,,5,false,100)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// For queueB, eventhough app4 needs 100 resources, only 30 resources were
comment|// preempted. (max is 50% of guaranteed cap of any queue
comment|// "maxIntraQueuePreemptable")
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|30
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
block|}
annotation|@
name|Test
DECL|method|testLimitPreemptionWithTotalPreemptedResourceAllowed ()
specifier|public
name|void
name|testLimitPreemptionWithTotalPreemptedResourceAllowed
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Scenario:      * Guaranteed resource of a/b are 40:60 Total cluster resource = 100      * totalPreemption allowed is 10%. This test is to verify that only      * 10% is preempted.      */
comment|// report "ideal" preempt as 10%. Ensure preemption happens only for 10%
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|TOTAL_PREEMPTION_PER_ROUND
argument_list|,
operator|(
name|float
operator|)
literal|0.1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 55 170 0]);"
operator|+
comment|// root
literal|"-a(=[40 100 10 50 0]);"
operator|+
comment|// a
literal|"-b(=[60 100 45 120 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,5,false,25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(2,1,n1,,5,false,25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1,n1,,40,false,20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app1 in a
operator|+
literal|"(6,1,n1,,5,false,100)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// For queue B eventhough app4 needs 100 resources, only 10 resources were
comment|// preempted. This is the 10% limit of TOTAL_PREEMPTION_PER_ROUND.
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAlreadySelectedContainerFromInterQueuePreemption ()
specifier|public
name|void
name|testAlreadySelectedContainerFromInterQueuePreemption
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Scenario:      * Guaranteed resource of a/b are 40:60 Total cluster resource = 100      * QueueB is under utilized and QueueA has to release 9 containers here.      * However within queue A, high priority app has also a demand for 20.      * So additional 11 more containers will be preempted making a tota of 20.      */
comment|// Set max preemption limit as 50%.
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_MAX_ALLOWABLE_LIMIT
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 95 170 0]);"
operator|+
comment|// root
literal|"-a(=[60 100 70 35 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 25 120 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,50,false,15);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(2,1,n1,,20,false,20);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1,n1,,20,false,20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app1 in a
operator|+
literal|"(4,1,n1,,5,false,100)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// As per intra queue preemption algorithm, 20 more containers were needed
comment|// for app2 (in queue a). Inter queue pre-emption had already preselected 9
comment|// containers and hence preempted only 11 more.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|20
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
block|}
annotation|@
name|Test
DECL|method|testSkipAMContainersInInterQueuePreemption ()
specifier|public
name|void
name|testSkipAMContainersInInterQueuePreemption
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Scenario:      * Guaranteed resource of a/b are 60:40 Total cluster resource = 100      * While preempting containers during intra-queue preemption, AM containers      * will be spared for now. Verify the same.      */
comment|// Set max preemption limit as 50%.
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_MAX_ALLOWABLE_LIMIT
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 100 170 0]);"
operator|+
comment|// root
literal|"-a(=[60 100 60 50 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 40 120 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,30,false,10);"
operator|+
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,10,false,20);"
operator|+
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(2,1,n1,,20,false,20);"
operator|+
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(4,1,n1,,20,false,20);"
operator|+
literal|"b\t"
comment|// app5 in a
operator|+
literal|"(4,1,n1,,20,false,100)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Ensure that only 9 containers are preempted from app2 (sparing 1 AM)
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|11
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
block|}
annotation|@
name|Test
DECL|method|testSkipAMContainersInInterQueuePreemptionSingleApp ()
specifier|public
name|void
name|testSkipAMContainersInInterQueuePreemptionSingleApp
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Scenario:      * Guaranteed resource of a/b are 50:50 Total cluster resource = 100      * Spare Am container from a lower priority app during its preemption      * cycle. Eventhough there are more demand and no other low priority      * apps are present, still AM contaier need to soared.      */
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 100 170 0]);"
operator|+
comment|// root
literal|"-a(=[50 100 50 50 0]);"
operator|+
comment|// a
literal|"-b(=[50 100 50 120 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,10,false,10);"
operator|+
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(2,1,n1,,40,false,10);"
operator|+
literal|"b\t"
comment|// app2 in a
operator|+
literal|"(4,1,n1,,20,false,20);"
operator|+
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1,n1,,30,false,100)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Make sure that app1's Am container is spared. Only 9/10 is preempted.
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
DECL|method|testNoPreemptionForSingleApp ()
specifier|public
name|void
name|testNoPreemptionForSingleApp
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Scenario:      * Guaranteed resource of a/b are 60:40 Total cluster resource = 100      * Only one app is running in queue. And it has more demand but no      * resource are available in queue. Preemption must not occur here.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 20 50 0]);"
operator|+
comment|// root
literal|"-a(=[60 100 20 50 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 0 0 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(4,1,n1,,20,false,50)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Ensure there are 0 preemptions since only one app is running in queue.
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
block|}
annotation|@
name|Test
DECL|method|testOverutilizedQueueResourceWithInterQueuePreemption ()
specifier|public
name|void
name|testOverutilizedQueueResourceWithInterQueuePreemption
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      * Scenario:      * Guaranteed resource of a/b are 20:80 Total cluster resource = 100      * QueueB is under utilized and 20 resource will be released from queueA.      * 10 containers will also selected for intra-queue too but it will be      * pre-selected.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 100 70 0]);"
operator|+
comment|// root
literal|"-a(=[20 100 100 30 0]);"
operator|+
comment|// a
literal|"-b(=[80 100 0 20 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,50,false,0);"
operator|+
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(3,1,n1,,50,false,30);"
operator|+
literal|"b\t"
comment|// app2 in a
operator|+
literal|"(4,1,n1,,0,false,20)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Complete demand request from QueueB for 20 resource must be preempted.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|20
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodePartitionIntraQueuePreemption ()
specifier|public
name|void
name|testNodePartitionIntraQueuePreemption
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The simplest test of node label, Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Scenario:      * Both a/b can access x, and guaranteed capacity of them is 50:50. Two      * nodes, n1 has 100 x, n2 has 100 NO_LABEL 4 applications in the cluster,      * app1/app2/app3 in a, and app4/app5 in b. app1 uses 50 x, app2 uses 50      * NO_LABEL, app3 uses 50 x, app4 uses 50 NO_LABEL. a has 20 pending      * resource for x for app3 of priority 2      *      * After preemption, it should preempt 20 from app1      */
comment|// Set max preemption limit as 50%.
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_MAX_ALLOWABLE_LIMIT
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
operator|+
comment|// default partition
literal|"x=100,true"
decl_stmt|;
comment|// partition=x
name|String
name|nodesConfig
init|=
literal|"n1=x;"
operator|+
comment|// n1 has partition=x
literal|"n2="
decl_stmt|;
comment|// n2 is default partition
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 100 100],x=[100 100 100 100]);"
operator|+
comment|// root
literal|"-a(=[50 100 50 50],x=[50 100 50 50]);"
operator|+
comment|// a
literal|"-b(=[50 100 50 50],x=[50 100 50 50])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,x,50,false,10);"
operator|+
comment|// 50 * x in n1
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(2,1,n1,x,0,false,20);"
operator|+
comment|// 0 * x in n1
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n2,,50,false);"
operator|+
comment|// 50 default in n2
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(1,1,n1,x,50,false);"
operator|+
comment|// 50 * x in n1
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1,n2,,50,false)"
decl_stmt|;
comment|// 50 default in n2
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
comment|// 20 preempted from app1
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|20
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
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
DECL|method|testComplexIntraQueuePreemption ()
specifier|public
name|void
name|testComplexIntraQueuePreemption
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The complex test preemption, Queue structure is:      *      *<pre>      *       root      *     /  | | \      *    a  b  c  d      *</pre>      *      * Scenario:      * Guaranteed resource of a/b/c/d are 10:40:20:30 Total cluster resource =      * 100      * All queues under its capacity, but within each queue there are many      * under served applications.      */
comment|// report "ideal" preempt as 50%. Ensure preemption happens only for 50%
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|TOTAL_PREEMPTION_PER_ROUND
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
comment|// Set max preemption limit as 50%.
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_MAX_ALLOWABLE_LIMIT
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 75 130 0]);"
operator|+
comment|// root
literal|"-a(=[10 100 5 50 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 35 60 0]);"
operator|+
comment|// b
literal|"-c(=[20 100 10 10 0]);"
operator|+
comment|// c
literal|"-d(=[30 100 25 10 0])"
decl_stmt|;
comment|// d
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,
comment|// pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,5,false,25);"
operator|+
comment|// app1 a
literal|"a\t"
operator|+
literal|"(4,1,n1,,0,false,25);"
operator|+
comment|// app2 a
literal|"a\t"
operator|+
literal|"(5,1,n1,,0,false,2);"
operator|+
comment|// app3 a
literal|"b\t"
operator|+
literal|"(3,1,n1,,5,false,20);"
operator|+
comment|// app4 b
literal|"b\t"
operator|+
literal|"(4,1,n1,,15,false,10);"
operator|+
comment|// app5 b
literal|"b\t"
operator|+
literal|"(4,1,n1,,10,false,10);"
operator|+
comment|// app6 b
literal|"b\t"
operator|+
literal|"(5,1,n1,,3,false,5);"
operator|+
comment|// app7 b
literal|"b\t"
operator|+
literal|"(5,1,n1,,0,false,2);"
operator|+
comment|// app8 b
literal|"b\t"
operator|+
literal|"(6,1,n1,,2,false,10);"
operator|+
comment|// app9 in b
literal|"c\t"
operator|+
literal|"(1,1,n1,,8,false,10);"
operator|+
comment|// app10 in c
literal|"c\t"
operator|+
literal|"(1,1,n1,,2,false,5);"
operator|+
comment|// app11 in c
literal|"c\t"
operator|+
literal|"(2,1,n1,,0,false,3);"
operator|+
literal|"d\t"
comment|// app12 in c
operator|+
literal|"(2,1,n1,,25,false,0);"
operator|+
literal|"d\t"
comment|// app13 in d
operator|+
literal|"(1,1,n1,,0,false,20)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// High priority app in queueA has 30 resource demand. But low priority
comment|// app has only 5 resource. Hence preempt 4 here sparing AM.
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
comment|// Multiple high priority apps has demand  of 17. This will be preempted
comment|// from another set of low priority apps.
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
literal|6
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
literal|4
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
literal|5
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Only 3 resources will be freed in this round for queue C as we
comment|// are trying to save AM container.
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
literal|10
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
literal|1
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
literal|11
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionWithTwoUsers ()
specifier|public
name|void
name|testIntraQueuePreemptionWithTwoUsers
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Scenario:      * Guaranteed resource of a/b are 40:60 Total cluster resource = 100      * Consider 2 users in a queue, assume minimum user limit factor is 50%.      * Hence in queueB of 40, each use has a quota of 20. app4 of high priority      * has a demand of 30 and its already using 5. Adhering to userlimit only      * 15 more must be preempted. If its same user,20 would have been preempted      */
comment|// Set max preemption limit as 50%.
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_MAX_ALLOWABLE_LIMIT
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100 100 55 170 0]);"
operator|+
comment|// root
literal|"-a(=[60 100 10 50 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 40 120 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,5,false,25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(2,1,n1,,5,false,25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1,n1,,35,false,20,user1);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(6,1,n1,,5,false,30,user2)"
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
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
comment|// Considering user-limit of 50% since only 2 users are there, only preempt
comment|// 14 more (5 is already running) eventhough demand is for 30. Ideally we
comment|// must preempt 15. But 15th container will bring user1's usage to 20 which
comment|// is same as user-limit. Hence skip 15th container.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|14
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
block|}
annotation|@
name|Test
DECL|method|testComplexNodePartitionIntraQueuePreemption ()
specifier|public
name|void
name|testComplexNodePartitionIntraQueuePreemption
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The simplest test of node label, Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Scenario:      * Both a/b can access x, and guaranteed capacity of them is 50:50. Two      * nodes, n1 has 100 x, n2 has 100 NO_LABEL 4 applications in the cluster,      * app1-app4 in a, and app5-app9 in b.      *      */
comment|// Set max preemption limit as 50%.
name|conf
operator|.
name|setFloat
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_MAX_ALLOWABLE_LIMIT
argument_list|,
operator|(
name|float
operator|)
literal|0.5
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|INTRAQUEUE_PREEMPTION_ORDER_POLICY
argument_list|,
literal|"priority_first"
argument_list|)
expr_stmt|;
name|String
name|labelsConfig
init|=
literal|"=100,true;"
operator|+
comment|// default partition
literal|"x=100,true"
decl_stmt|;
comment|// partition=x
name|String
name|nodesConfig
init|=
literal|"n1=x;"
operator|+
comment|// n1 has partition=x
literal|"n2="
decl_stmt|;
comment|// n2 is default partition
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 100 100],x=[100 100 100 100]);"
operator|+
comment|// root
literal|"-a(=[50 100 50 50],x=[50 100 40 50]);"
operator|+
comment|// a
literal|"-b(=[50 100 35 50],x=[50 100 50 50])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,x,35,false,10);"
operator|+
comment|// 20 * x in n1
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,x,5,false,10);"
operator|+
comment|// 20 * x in n1
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(2,1,n1,x,0,false,20);"
operator|+
comment|// 0 * x in n1
literal|"a\t"
comment|// app4 in a
operator|+
literal|"(1,1,n2,,50,false);"
operator|+
comment|// 50 default in n2
literal|"b\t"
comment|// app5 in b
operator|+
literal|"(1,1,n1,x,50,false);"
operator|+
comment|// 50 * x in n1
literal|"b\t"
comment|// app6 in b
operator|+
literal|"(1,1,n2,,25,false);"
operator|+
comment|// 25 * default in n2
literal|"b\t"
comment|// app7 in b
operator|+
literal|"(1,1,n2,,3,false);"
operator|+
comment|// 3 * default in n2
literal|"b\t"
comment|// app8 in b
operator|+
literal|"(1,1,n2,,2,false);"
operator|+
comment|// 2 * default in n2
literal|"b\t"
comment|// app9 in b
operator|+
literal|"(5,1,n2,,5,false,30)"
decl_stmt|;
comment|// 50 default in n2
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
comment|// Label X: app3 has demand of 20 for label X. Hence app2 will loose
comment|// 4 (sparing AM) and 16 more from app1 till preemption limit is met.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|16
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
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
literal|4
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
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
comment|// Default Label:For a demand of 30, preempt from all low priority
comment|// apps of default label. 25 will be preempted as preemption limit is
comment|// met.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|8
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
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|7
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
literal|22
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|argThat
argument_list|(
operator|new
name|IsPreemptionRequestFor
argument_list|(
name|getAppAttemptId
argument_list|(
literal|6
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

