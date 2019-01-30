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

begin_comment
comment|/*  * Test class for testing intra-queue preemption when the fair ordering policy  * is enabled on a capacity queue.  */
end_comment

begin_class
DECL|class|TestProportionalCapacityPreemptionPolicyIntraQueueFairOrdering
specifier|public
class|class
name|TestProportionalCapacityPreemptionPolicyIntraQueueFairOrdering
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
comment|/*    * When the capacity scheduler fair ordering policy is enabled, preempt first    * from the application owned by the user that is the farthest over their    * user limit.    */
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionFairOrderingPolicyEnabledOneAppPerUser ()
specifier|public
name|void
name|testIntraQueuePreemptionFairOrderingPolicyEnabledOneAppPerUser
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Enable FairOrderingPolicy for yarn.scheduler.capacity.root.a
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a.ordering-policy"
argument_list|,
literal|"fair"
argument_list|)
expr_stmt|;
comment|// Make sure all containers will be preempted in a single round.
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
literal|1.0
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
comment|// user1/app1 has 60 resources in queue a
comment|// user2/app2 has 40 resources in queue a
comment|// user3/app3 is requesting 20 resources in queue a
comment|// With 3 users, preemptable user limit should be around 35 resources each.
comment|// With FairOrderingPolicy enabled on queue a, all 20 resources should be
comment|// preempted from app1
name|String
name|appsConfig
init|=
comment|// queueName\t(prio,resource,host,expression,#repeat,reserved,pending,user)
literal|"a\t"
comment|// app1, user1 in a
operator|+
literal|"(1,1,n1,,60,false,0,user1);"
operator|+
literal|"a\t"
comment|// app2, user2 in a
operator|+
literal|"(1,1,n1,,40,false,0,user2);"
operator|+
literal|"a\t"
comment|// app3, user3 in a
operator|+
literal|"(1,1,n1,,0,false,20,user3)"
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
block|}
comment|/*    * When the capacity scheduler fifo ordering policy is enabled, preempt first    * from the youngest application until reduced to user limit, then preempt    * from next youngest app.    */
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionFifoOrderingPolicyEnabled ()
specifier|public
name|void
name|testIntraQueuePreemptionFifoOrderingPolicyEnabled
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Enable FifoOrderingPolicy for yarn.scheduler.capacity.root.a
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a.ordering-policy"
argument_list|,
literal|"fifo"
argument_list|)
expr_stmt|;
comment|// Make sure all containers will be preempted in a single round.
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
literal|1.0
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
comment|// user1/app1 has 60 resources in queue a
comment|// user2/app2 has 40 resources in queue a
comment|// user3/app3 is requesting 20 resources in queue a
comment|// With 3 users, preemptable user limit should be around 35 resources each.
comment|// With FifoOrderingPolicy enabled on queue a, the first 5 should come from
comment|// the youngest app, app2, until app2 is reduced to the user limit of 35.
name|String
name|appsConfig
init|=
comment|// queueName\t(prio,resource,host,expression,#repeat,reserved,pending,user)
literal|"a\t"
comment|// app1, user1 in a
operator|+
literal|"(1,1,n1,,60,false,0,user1);"
operator|+
literal|"a\t"
comment|// app2, user2 in a
operator|+
literal|"(1,1,n1,,40,false,0,user2);"
operator|+
literal|"a\t"
comment|// app3, user3 in a
operator|+
literal|"(1,1,n1,,0,false,5,user3)"
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
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// user1/app1 has 60 resources in queue a
comment|// user2/app2 has 35 resources in queue a
comment|// user3/app3 has 5 resources and is requesting 15 resources in queue a
comment|// With 3 users, preemptable user limit should be around 35 resources each.
comment|// The next 15 should come from app1 even though app2 is younger since app2
comment|// has already been reduced to its user limit.
name|appsConfig
operator|=
comment|// queueName\t(prio,resource,host,expression,#repeat,reserved,pending,user)
literal|"a\t"
comment|// app1, user1 in a
operator|+
literal|"(1,1,n1,,60,false,0,user1);"
operator|+
literal|"a\t"
comment|// app2, user2 in a
operator|+
literal|"(1,1,n1,,35,false,0,user2);"
operator|+
literal|"a\t"
comment|// app3, user3 in a
operator|+
literal|"(1,1,n1,,5,false,15,user3)"
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
block|}
comment|/*    * When the capacity scheduler fair ordering policy is enabled, preempt first    * from the youngest application from the user that is the farthest over their    * user limit.    */
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionFairOrderingPolicyMulitipleAppsPerUser ()
specifier|public
name|void
name|testIntraQueuePreemptionFairOrderingPolicyMulitipleAppsPerUser
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Enable FairOrderingPolicy for yarn.scheduler.capacity.root.a
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a.ordering-policy"
argument_list|,
literal|"fair"
argument_list|)
expr_stmt|;
comment|// Make sure all containers will be preempted in a single round.
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
literal|1.0
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
comment|// user1/app1 has 35 resources in queue a
comment|// user1/app2 has 25 resources in queue a
comment|// user2/app3 has 40 resources in queue a
comment|// user3/app4 is requesting 20 resources in queue a
comment|// With 3 users, preemptable user limit should be around 35 resources each.
comment|// With FairOrderingPolicy enabled on queue a, all 20 resources should be
comment|// preempted from app1 since it's the most over served app from the most
comment|// over served user
name|String
name|appsConfig
init|=
comment|// queueName\t(prio,resource,host,expression,#repeat,reserved,pending,user)
literal|"a\t"
comment|// app1 and app2, user1 in a
operator|+
literal|"(1,1,n1,,35,false,0,user1);"
operator|+
literal|"a\t"
operator|+
literal|"(1,1,n1,,25,false,0,user1);"
operator|+
literal|"a\t"
comment|// app3, user2 in a
operator|+
literal|"(1,1,n1,,40,false,0,user2);"
operator|+
literal|"a\t"
comment|// app4, user3 in a
operator|+
literal|"(1,1,n1,,0,false,20,user3)"
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
block|}
comment|/*    * When the capacity scheduler fifo ordering policy is enabled and a user has    * multiple apps, preempt first from the youngest application.    */
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionFifoOrderingPolicyMultipleAppsPerUser ()
specifier|public
name|void
name|testIntraQueuePreemptionFifoOrderingPolicyMultipleAppsPerUser
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Enable FifoOrderingPolicy for yarn.scheduler.capacity.root.a
name|conf
operator|.
name|set
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a.ordering-policy"
argument_list|,
literal|"fifo"
argument_list|)
expr_stmt|;
comment|// Make sure all containers will be preempted in a single round.
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
literal|1.0
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
comment|// user1/app1 has 40 resources in queue a
comment|// user1/app2 has 20 resources in queue a
comment|// user3/app3 has 40 resources in queue a
comment|// user4/app4 is requesting 20 resources in queue a
comment|// With 3 users, preemptable user limit should be around 35 resources each.
name|String
name|appsConfig
init|=
comment|// queueName\t(prio,resource,host,expression,#repeat,reserved,pending,user)
literal|"a\t"
comment|// app1, user1 in a
operator|+
literal|"(1,1,n1,,40,false,0,user1);"
operator|+
literal|"a\t"
comment|// app2, user1 in a
operator|+
literal|"(1,1,n1,,20,false,0,user1);"
operator|+
literal|"a\t"
comment|// app3, user3 in a
operator|+
literal|"(1,1,n1,,40,false,0,user3);"
operator|+
literal|"a\t"
comment|// app4, user4 in a
operator|+
literal|"(1,1,n1,,0,false,25,user4)"
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
comment|// app3 is the younges and also over its user limit. 5 should be preempted
comment|// from app3 until it comes down to user3's user limit.
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// User1's app2 is its youngest. 19 should be preempted from app2, leaving
comment|// only the AM
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
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Preempt the remaining resource from User1's oldest app1.
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

