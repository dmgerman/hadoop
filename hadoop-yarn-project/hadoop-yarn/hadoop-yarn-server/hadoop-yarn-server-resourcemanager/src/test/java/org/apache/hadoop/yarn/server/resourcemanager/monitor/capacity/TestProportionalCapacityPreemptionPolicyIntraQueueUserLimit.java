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

begin_comment
comment|/**  * Test class for IntraQueuePreemption scenarios.  */
end_comment

begin_class
DECL|class|TestProportionalCapacityPreemptionPolicyIntraQueueUserLimit
specifier|public
class|class
name|TestProportionalCapacityPreemptionPolicyIntraQueueUserLimit
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
DECL|method|testSimpleIntraQueuePreemptionWithTwoUsers ()
specifier|public
name|void
name|testSimpleIntraQueuePreemptionWithTwoUsers
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      * Preconditions:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 1        | 100  | 0       |      *   | app2 | user2 | 1        | 0    | 30      |      *   +--------------+----------+------+---------+      * Hence in queueA of 100, each user has a quota of 50. app1 of high priority      * has a demand of 0 and its already using 100. app2 from user2 has a demand      * of 30, and UL is 50. 30 would be preempted from app1.      */
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
literal|"root(=[100 100 100 30 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 30 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,100,false,0,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,0,false,30,user2)"
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
comment|// app2 needs more resource and its well under its user-limit. Hence preempt
comment|// resources from app1.
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
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoIntraQueuePreemptionWithSingleUser ()
specifier|public
name|void
name|testNoIntraQueuePreemptionWithSingleUser
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 1        | 100  | 0       |      *   | app2 | user1 | 1        | 0    | 30      |      *   +--------------+----------+------+---------+      * Given single user, lower priority/late submitted apps has to      * wait.      */
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
literal|"root(=[100 100 100 30 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 30 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,100,false,0,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,0,false,30,user1)"
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
comment|// app2 needs more resource. Since app1,2 are from same user, there wont be
comment|// any preemption.
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
DECL|method|testNoIntraQueuePreemptionWithTwoUserUnderUserLimit ()
specifier|public
name|void
name|testNoIntraQueuePreemptionWithTwoUserUnderUserLimit
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 1        | 50   | 0       |      *   | app2 | user2 | 1        | 30   | 30      |      *   +--------------+----------+------+---------+      * Hence in queueA of 100, each user has a quota of 50. app1 of high priority      * has a demand of 0 and its already using 50. app2 from user2 has a demand      * of 30, and UL is 50. Since app1 is under UL, there should not be any      * preemption.      */
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
literal|"root(=[100 100 80 30 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 80 30 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,50,false,0,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,30,false,30,user2)"
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
comment|// app2 needs more resource. Since app1,2 are from same user, there wont be
comment|// any preemption.
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
DECL|method|testSimpleIntraQueuePreemptionWithTwoUsersWithAppPriority ()
specifier|public
name|void
name|testSimpleIntraQueuePreemptionWithTwoUsersWithAppPriority
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 2        | 100  | 0       |      *   | app2 | user2 | 1        | 0    | 30      |      *   +--------------+----------+------+---------+      * Hence in queueA of 100, each user has a quota of 50. app1 of high priority      * has a demand of 0 and its already using 100. app2 from user2 has a demand      * of 30, and UL is 50. 30 would be preempted from app1.      */
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
literal|"root(=[100 100 100 30 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 30 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(2,1,n1,,100,false,0,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,0,false,30,user2)"
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
comment|// app2 needs more resource and its well under its user-limit. Hence preempt
comment|// resources from app1 even though its priority is more than app2.
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
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionOfUserLimitWithMultipleApps ()
specifier|public
name|void
name|testIntraQueuePreemptionOfUserLimitWithMultipleApps
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 1        | 30   | 30      |      *   | app2 | user2 | 1        | 20   | 20      |      *   | app3 | user1 | 1        | 30   | 30      |      *   | app4 | user2 | 1        | 0    | 10      |      *   +--------------+----------+------+---------+      * Hence in queueA of 100, each user has a quota of 50. Now have multiple      * apps and check for preemption across apps.      */
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
literal|"root(=[100 100 80 90 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 80 90 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,30,false,30,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,20,false,20,user2);"
operator|+
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(1,1,n1,,30,false,30,user1);"
operator|+
literal|"a\t"
comment|// app4 in a
operator|+
literal|"(1,1,n1,,0,false,10,user2)"
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
comment|// app2/app4 needs more resource and its well under its user-limit. Hence
comment|// preempt resources from app3 (compare to app1, app3 has low priority).
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoPreemptionOfUserLimitWithMultipleAppsAndSameUser ()
specifier|public
name|void
name|testNoPreemptionOfUserLimitWithMultipleAppsAndSameUser
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 1        | 30   | 30      |      *   | app2 | user1 | 1        | 20   | 20      |      *   | app3 | user1 | 1        | 30   | 30      |      *   | app4 | user1 | 1        | 0    | 10      |      *   +--------------+----------+------+---------+      * Hence in queueA of 100, each user has a quota of 50. Now have multiple      * apps and check for preemption across apps.      */
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
literal|"root(=[100 100 80 90 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 80 90 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,30,false,20,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,20,false,20,user1);"
operator|+
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(1,1,n1,,30,false,30,user1);"
operator|+
literal|"a\t"
comment|// app4 in a
operator|+
literal|"(1,1,n1,,0,false,10,user1)"
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
comment|// app2/app4 needs more resource and its well under its user-limit. Hence
comment|// preempt resources from app3 (compare to app1, app3 has low priority).
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
DECL|method|testIntraQueuePreemptionOfUserLimitWitAppsOfDifferentPriority ()
specifier|public
name|void
name|testIntraQueuePreemptionOfUserLimitWitAppsOfDifferentPriority
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 3        | 30   | 30      |      *   | app2 | user2 | 1        | 20   | 20      |      *   | app3 | user1 | 4        | 30   | 0       |      *   | app4 | user2 | 1        | 0    | 10      |      *   +--------------+----------+------+---------+      * Hence in queueA of 100, each user has a quota of 50. Now have multiple      * apps and check for preemption across apps.      */
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
literal|"root(=[100 100 80 60 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 80 60 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(3,1,n1,,30,false,30,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,20,false,20,user2);"
operator|+
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(4,1,n1,,30,false,0,user1);"
operator|+
literal|"a\t"
comment|// app4 in a
operator|+
literal|"(1,1,n1,,0,false,10,user2)"
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
comment|// app2/app4 needs more resource and its well under its user-limit. Hence
comment|// preempt resources from app1 (compare to app3, app1 has low priority).
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
block|}
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionOfUserLimitInTwoQueues ()
specifier|public
name|void
name|testIntraQueuePreemptionOfUserLimitInTwoQueues
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *      /   \      *     a     b      *</pre>      *      * Guaranteed resource of a/b are 40:60 Total cluster resource = 100      * maxIntraQueuePreemptableLimit by default is 50%. This test is to verify      * that intra-queue preemption could occur in two queues when user-limit      * irreuglarity is present in queue.      */
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
literal|"root(=[100 100 90 80 0]);"
operator|+
comment|// root
literal|"-a(=[60 100 55 60 0]);"
operator|+
comment|// a
literal|"-b(=[40 100 35 20 0])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(3,1,n1,,20,false,30,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,20,false,20,user2);"
operator|+
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(4,1,n1,,15,false,0,user1);"
operator|+
literal|"a\t"
comment|// app4 in a
operator|+
literal|"(1,1,n1,,0,false,10,user2);"
operator|+
literal|"b\t"
comment|// app5 in b
operator|+
literal|"(3,1,n1,,25,false,10,user1);"
operator|+
literal|"b\t"
comment|// app6 in b
operator|+
literal|"(1,1,n1,,10,false,10,user2)"
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
comment|// app2/app4 needs more resource and its well under its user-limit. Hence
comment|// preempt resources from app1 (compare to app3, app1 has low priority).
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
block|}
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionWithTwoRequestingUsers ()
specifier|public
name|void
name|testIntraQueuePreemptionWithTwoRequestingUsers
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**     * Queue structure is:     *     *<pre>     *       root     *        |     *        a     *</pre>     *     * Scenario:     *   Queue total resources: 100     *   Minimum user limit percent: 50%     *   +--------------+----------+------+---------+     *   | APP  | USER  | PRIORITY | USED | PENDING |     *   +--------------+----------+------+---------+     *   | app1 | user1 | 1        | 60   | 10      |     *   | app2 | user2 | 1        | 40   | 10      |     *   +--------------+----------+------+---------+     * Hence in queueA of 100, each user has a quota of 50. Now have multiple     * apps and check for preemption across apps.     */
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
literal|"root(=[100 100 100 20 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 20 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,60,false,10,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,40,false,10,user2)"
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
comment|// app2 needs more resource and its well under its user-limit. Hence preempt
comment|// resources from app1.
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
DECL|method|testNoIntraQueuePreemptionIfBelowUserLimitAndLowPriorityExtraUsers ()
specifier|public
name|void
name|testNoIntraQueuePreemptionIfBelowUserLimitAndLowPriorityExtraUsers
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      * Preconditions:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 1        | 50   | 0       |      *   | app2 | user2 | 1        | 50   | 0       |      *   | app3 | user3 | 0        | 0    | 10      |      *   +--------------+----------+------+---------+      * This scenario should never preempt from either user1 or user2      */
comment|// Set max preemption per round to 50% (this is different from minimum user
comment|// limit percent).
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
literal|0.7
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
literal|"root(=[100 100 100 10 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 10 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t\
comment|//     (priority,resource,host,label,#repeat,reserved,pending,user)\tMULP;
literal|"a\t(1,1,n1,,50,false,0,user1)\t50;"
operator|+
comment|// app1, user1
literal|"a\t(1,1,n1,,50,false,0,user2)\t50;"
operator|+
comment|// app2, user2
literal|"a\t(0,1,n1,,0,false,10,user3)\t50"
decl_stmt|;
comment|// app3, user3
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
comment|// app2/app4 needs more resource and its well under its user-limit. Hence
comment|// preempt resources from app1 (compare to app3, app1 has low priority).
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
DECL|method|testNoIntraQueuePreemptionIfBelowUserLimitAndSamePriorityExtraUsers ()
specifier|public
name|void
name|testNoIntraQueuePreemptionIfBelowUserLimitAndSamePriorityExtraUsers
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      * Preconditions:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 1        | 50   | 0       |      *   | app2 | user2 | 1        | 50   | 0       |      *   | app3 | user3 | 1        | 0    | 10      |      *   +--------------+----------+------+---------+      * This scenario should never preempt from either user1 or user2      */
comment|// Set max preemption per round to 50% (this is different from minimum user
comment|// limit percent).
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
literal|0.7
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
literal|"root(=[100 100 100 10 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 10 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t\
comment|//     (priority,resource,host,label,#repeat,reserved,pending,user)\tMULP;
literal|"a\t(1,1,n1,,50,false,0,user1)\t50;"
operator|+
comment|// app1, user1
literal|"a\t(1,1,n1,,50,false,0,user2)\t50;"
operator|+
comment|// app2, user2
literal|"a\t(1,1,n1,,0,false,10,user3)\t50"
decl_stmt|;
comment|// app3, user3
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
comment|// app2/app4 needs more resource and its well under its user-limit. Hence
comment|// preempt resources from app1 (compare to app3, app1 has low priority).
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
DECL|method|testNoIntraQueuePreemptionIfBelowUserLimitAndHighPriorityExtraUsers ()
specifier|public
name|void
name|testNoIntraQueuePreemptionIfBelowUserLimitAndHighPriorityExtraUsers
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *        |      *        a      *</pre>      *      * Scenario:      * Preconditions:      *   Queue total resources: 100      *   Minimum user limit percent: 50%      *   +--------------+----------+------+---------+      *   | APP  | USER  | PRIORITY | USED | PENDING |      *   +--------------+----------+------+---------+      *   | app1 | user1 | 1        | 50   | 0       |      *   | app2 | user2 | 1        | 50   | 0       |      *   | app3 | user3 | 5        | 0    | 10      |      *   +--------------+----------+------+---------+      * This scenario should never preempt from either user1 or user2      */
comment|// Set max preemption per round to 50% (this is different from minimum user
comment|// limit percent).
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
literal|0.7
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
literal|"root(=[100 100 100 10 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 10 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t\
comment|//     (priority,resource,host,label,#repeat,reserved,pending,user)\tMULP;
literal|"a\t(1,1,n1,,50,false,0,user1)\t50;"
operator|+
comment|// app1, user1
literal|"a\t(1,1,n1,,50,false,0,user2)\t50;"
operator|+
comment|// app2, user2
literal|"a\t(5,1,n1,,0,false,10,user3)\t50"
decl_stmt|;
comment|// app3, user3
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
comment|// app2/app4 needs more resource and its well under its user-limit. Hence
comment|// preempt resources from app1 (compare to app3, app1 has low priority).
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
DECL|method|testNoIntraQueuePreemptionWithUserLimitDeadzone ()
specifier|public
name|void
name|testNoIntraQueuePreemptionWithUserLimitDeadzone
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**     * Queue structure is:     *     *<pre>     *       root     *        |     *        a     *</pre>     *     * Scenario:     *   Queue total resources: 100     *   Minimum user limit percent: 50%     *   +--------------+----------+------+---------+     *   | APP  | USER  | PRIORITY | USED | PENDING |     *   +--------------+----------+------+---------+     *   | app1 | user1 | 1        | 60   | 10      |     *   | app2 | user2 | 1        | 40   | 10      |     *   +--------------+----------+------+---------+     * Hence in queueA of 100, each user has a quota of 50. Now have multiple     * apps and check for preemption across apps but also ensure that user's     * usage not coming under its user-limit.     */
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
literal|"root(=[100 100 100 20 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 20 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,3,n1,,20,false,10,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,40,false,10,user2)"
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
comment|// app2 needs more resource and its well under its user-limit. Hence preempt
comment|// 3 resources (9GB) from app1. We will not preempt last container as it may
comment|// pull user's usage under its user-limit.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|3
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
DECL|method|testIntraQueuePreemptionWithUserLimitDeadzoneAndPriority ()
specifier|public
name|void
name|testIntraQueuePreemptionWithUserLimitDeadzoneAndPriority
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**     * Queue structure is:     *     *<pre>     *       root     *        |     *        a     *</pre>     *     * Scenario:     *   Queue total resources: 100     *   Minimum user limit percent: 50%     *   +--------------+----------+------+---------+     *   | APP  | USER  | PRIORITY | USED | PENDING |     *   +--------------+----------+------+---------+     *   | app1 | user1 | 1        | 60   | 10      |     *   | app2 | user2 | 1        | 40   | 10      |     *   +--------------+----------+------+---------+     * Hence in queueA of 100, each user has a quota of 50. Now have multiple     * apps and check for preemption across apps but also ensure that user's     * usage not coming under its user-limit.     */
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
literal|"root(=[100 100 100 20 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 20 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,3,n1,,20,false,10,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(2,1,n1,,0,false,10,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,40,false,20,user2)"
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
comment|// app2 needs more resource and its well under its user-limit. Hence preempt
comment|// 3 resources (9GB) from app1. We will not preempt last container as it may
comment|// pull user's usage under its user-limit.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|3
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
comment|// After first round, 3 containers were preempted from app1 and resource
comment|// distribution will be like below.
name|appsConfig
operator|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,3,n1,,17,false,10,user1);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(2,1,n1,,0,false,10,user1);"
operator|+
comment|// app2 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,49,false,11,user2)"
expr_stmt|;
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
comment|// app2 has priority demand within same user 'user1'. However user1's used
comment|// is alredy under UL. Hence no preemption. We will still get 3 container
comment|// while asserting as it was aleady selected in earlier round.
name|verify
argument_list|(
name|mDisp
argument_list|,
name|times
argument_list|(
literal|3
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
DECL|method|testSimpleIntraQueuePreemptionOneUserUnderOneUserAtOneUserAbove ()
specifier|public
name|void
name|testSimpleIntraQueuePreemptionOneUserUnderOneUserAtOneUserAbove
parameter_list|()
throws|throws
name|IOException
block|{
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
literal|"root(=[100 100 100 1 0]);"
operator|+
comment|// root
literal|"-a(=[100 100 100 1 0])"
decl_stmt|;
comment|// a
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,,65,false,0,user1);"
operator|+
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,,35,false,0,user2);"
operator|+
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(1,1,n1,,0,false,1,user3)"
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
comment|// app2 is right at its user limit and app1 needs one resource. Should
comment|// preempt 1 container.
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

