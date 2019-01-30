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
name|DominantResourceCalculator
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Test class for IntraQueuePreemption scenarios.  */
end_comment

begin_class
DECL|class|TestProportionalCapacityPreemptionPolicyIntraQueueWithDRF
specifier|public
class|class
name|TestProportionalCapacityPreemptionPolicyIntraQueueWithDRF
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
name|rc
operator|=
operator|new
name|DominantResourceCalculator
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|cs
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rc
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
DECL|method|testSimpleIntraQueuePreemptionWithVCoreResource ()
specifier|public
name|void
name|testSimpleIntraQueuePreemptionWithVCoreResource
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The simplest test preemption, Queue structure is:      *      *<pre>      *       root      *     /  | | \      *    a  b  c  d      *</pre>      *      * Guaranteed resource of a/b/c/d are 10:40:20:30 Total cluster resource =      * 100 Scenario: Queue B has few running apps and two high priority apps      * have demand. Apps which are running at low priority (4) will preempt few      * of its resources to meet the demand.      */
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
literal|"=100:50,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100:50"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100:50 100:50 80:40 120:60 0]);"
operator|+
comment|// root
literal|"-a(=[10:5 100:50 10:5 50:25 0]);"
operator|+
comment|// a
literal|"-b(=[40:20 100:50 40:20 60:30 0]);"
operator|+
comment|// b
literal|"-c(=[20:10 100:50 10:5 10:5 0]);"
operator|+
comment|// c
literal|"-d(=[30:15 100:50 20:10 0 0])"
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
literal|"(1,1:1,n1,,5,false,25:25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1:1,n1,,5,false,25:25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,1:1,n1,,36,false,20:20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(4,1:1,n1,,2,false,10:10);"
operator|+
comment|// app4 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(5,1:1,n1,,1,false,10:10);"
operator|+
comment|// app5 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(6,1:1,n1,,1,false,10:10);"
operator|+
comment|// app6 in b
literal|"c\t"
comment|// app1 in a
operator|+
literal|"(1,1:1,n1,,10,false,10:10);"
operator|+
literal|"d\t"
comment|// app7 in c
operator|+
literal|"(1,1:1,n1,,20,false,0)"
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
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntraQueuePreemptionWithDominantVCoreResource ()
specifier|public
name|void
name|testIntraQueuePreemptionWithDominantVCoreResource
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The simplest test preemption, Queue structure is:      *      *<pre>      *     root      *     /  \      *    a    b      *</pre>      *      * Guaranteed resource of a/b are 40:60 Total cluster resource = 100      * Scenario: Queue B has few running apps and two high priority apps have      * demand. Apps which are running at low priority (4) will preempt few of      * its resources to meet the demand.      */
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
literal|"=100:200,true;"
decl_stmt|;
name|String
name|nodesConfig
init|=
comment|// n1 has no label
literal|"n1= res=100:200"
decl_stmt|;
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending,reserved
literal|"root(=[100:50 100:50 50:40 110:60 0]);"
operator|+
comment|// root
literal|"-a(=[40:20 100:50 9:9 50:30 0]);"
operator|+
comment|// a
literal|"-b(=[60:30 100:50 40:30 60:30 0]);"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved,
comment|// pending)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,2:1,n1,,4,false,25:25);"
operator|+
comment|// app1 a
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1:3,n1,,2,false,25:25);"
operator|+
comment|// app2 a
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(4,2:1,n1,,10,false,20:20);"
operator|+
comment|// app3 b
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(4,1:2,n1,,5,false,10:10);"
operator|+
comment|// app4 b
literal|"b\t"
comment|// app5 in b
operator|+
literal|"(5,1:1,n1,,5,false,30:20);"
operator|+
comment|// app5 b
literal|"b\t"
comment|// app6 in b
operator|+
literal|"(6,2:1,n1,,5,false,30:20);"
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
comment|// For queue B, app3 and app4 were of lower priority. Hence take 4
comment|// containers.
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
block|}
end_class

end_unit

