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
DECL|class|TestProportionalCapacityPreemptionPolicyForNodePartitions
specifier|public
class|class
name|TestProportionalCapacityPreemptionPolicyForNodePartitions
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
DECL|method|testNodePartitionPreemptionRespectGuaranteedCapacity ()
specifier|public
name|void
name|testNodePartitionPreemptionRespectGuaranteedCapacity
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * The simplest test of node label, Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Both a/b can access x, and guaranteed capacity of them is 50:50. Two      * nodes, n1 has 100 x, n2 has 100 NO_LABEL 4 applications in the cluster,      * app1/app2 in a, and app3/app4 in b.      * app1 uses 80 x, app2 uses 20 NO_LABEL, app3 uses 20 x, app4 uses 80 NO_LABEL.      * Both a/b have 50 pending resource for x and NO_LABEL      *      * After preemption, it should preempt 30 from app1, and 30 from app4.      */
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
comment|//root
literal|"-a(=[50 100 20 50],x=[50 100 80 50]);"
operator|+
comment|// a
literal|"-b(=[50 100 80 50],x=[50 100 20 50])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,x,80,false);"
operator|+
comment|// 80 * x in n1
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n2,,20,false);"
operator|+
comment|// 20 default in n2
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// 80 * x in n1
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1,n2,,80,false)"
decl_stmt|;
comment|// 20 default in n2
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
comment|// 30 preempted from app1, 30 preempted from app4, and nothing preempted
comment|// from app2/app3
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
literal|30
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
DECL|method|testNodePartitionPreemptionNotHappenBetweenSatisfiedQueues ()
specifier|public
name|void
name|testNodePartitionPreemptionNotHappenBetweenSatisfiedQueues
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *         root      *       /  |  \      *      a   b   c      *</pre>      *      * Both a/b/c can access x, and guaranteed_capacity(x) of them is 80:10:10.      * a/b's max resource is 100, and c's max resource is 30.      *      * Two nodes, n1 has 100 x, n2 has 100 NO_LABEL.      *      * 2 apps in cluster.      * app1 in b and app2 in c.      *      * app1 uses 90x, and app2 use 10x. We don't expect preemption happen      * between them because all of them are satisfied      */
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
comment|//root
literal|"-a(=[80 80 0 0],x=[80 80 0 0]);"
operator|+
comment|// a
literal|"-b(=[10 100 0 0],x=[10 100 90 50]);"
operator|+
comment|// b
literal|"-c(=[10 100 0 0],x=[10 30 10 50])"
decl_stmt|;
comment|//c
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"b\t"
comment|// app1 in b
operator|+
literal|"(1,1,n1,x,90,false);"
operator|+
comment|// 80 * x in n1
literal|"c\t"
comment|// app2 in c
operator|+
literal|"(1,1,n1,x,10,false)"
decl_stmt|;
comment|// 20 default in n2
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
comment|// No preemption happens
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
block|}
annotation|@
name|Test
DECL|method|testNodePartitionPreemptionOfIgnoreExclusivityAndRespectCapacity ()
specifier|public
name|void
name|testNodePartitionPreemptionOfIgnoreExclusivityAndRespectCapacity
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Both a/b can access x, and guaranteed capacity of them is 50:50. Two      * nodes, n1 has 100 x, n2 has 100 NO_LABEL and 2 applications in the cluster,      * app1/app2 in a      * app1 uses 20x (ignoreExclusivity), app2 uses 80x (respectExclusivity).      *      * b has 100 pending resource of x      *      * After preemption, it should preempt 20 from app1, and 30 from app2.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
operator|+
comment|// default partition
literal|"x=100,false"
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
comment|//root
literal|"-a(=[50 100 0 0],x=[50 100 100 50]);"
operator|+
comment|// a
literal|"-b(=[50 100 0 0],x=[50 100 0 100])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,x,1,false)"
comment|// 1 * x in n1 (it's AM container)
operator|+
literal|"(1,1,n1,,20,false);"
operator|+
comment|// 20 * x in n1 (ignoreExclusivity)
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,x,79,false)"
decl_stmt|;
comment|// 79 * x
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
comment|// 30 preempted from app1, 30 preempted from app4, and nothing preempted
comment|// from app2/app3
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
DECL|method|testNodePartitionPreemptionOfSkippingAMContainer ()
specifier|public
name|void
name|testNodePartitionPreemptionOfSkippingAMContainer
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Both a/b can access x, and guaranteed capacity of them is 20:80. Two      * nodes, n1 has 100 x, n2 has 100 NO_LABEL and 2 applications in the cluster,      * app1/app2/app3/app4/app5 in a, both uses 20 resources.      *      * b has 100 pending resource of x      *      * After preemption, it should preempt 19 from app[5-2] an 4 from app1      */
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
comment|//root
literal|"-a(=[50 100 0 0],x=[20 100 100 50]);"
operator|+
comment|// a
literal|"-b(=[50 100 0 0],x=[80 100 0 100])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// uses 20 resource
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// uses 20 resource
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// uses 20 resource
literal|"a\t"
comment|// app4 in a
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// uses 20 resource
literal|"a\t"
comment|// app5 in a
operator|+
literal|"(1,1,n1,x,20,false);"
decl_stmt|;
comment|// uses 20 resource
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
comment|// 4 from app1
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
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 19 from app2-app5
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
literal|19
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
literal|19
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
literal|5
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodePartitionPreemptionOfAMContainer ()
specifier|public
name|void
name|testNodePartitionPreemptionOfAMContainer
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Both a/b can access x, and guaranteed capacity of them is 3:97. Two      * nodes, n1 has 100 x, n2 has 100 NO_LABEL.      *      * app1/app2/app3/app4/app5 in a, both uses 20 resources(x)      *      * b has 100 pending resource of x      *      * After preemption, it should preempt 20 from app4/app5 an 19 from      * app1-app3. App4/app5's AM container will be preempted      */
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
comment|//root
literal|"-a(=[50 100 0 0],x=[3 100 100 50]);"
operator|+
comment|// a
literal|"-b(=[50 100 0 0],x=[97 100 0 100])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// uses 20 resource
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// uses 20 resource
literal|"a\t"
comment|// app3 in a
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// uses 20 resource
literal|"a\t"
comment|// app4 in a
operator|+
literal|"(1,1,n1,x,20,false);"
operator|+
comment|// uses 20 resource
literal|"a\t"
comment|// app5 in a
operator|+
literal|"(1,1,n1,x,20,false);"
decl_stmt|;
comment|// uses 20 resource
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
comment|// 4 from app1
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
comment|// 19 from app2-app5
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
literal|5
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodePartitionDisablePreemptionForSingleLevelQueue ()
specifier|public
name|void
name|testNodePartitionDisablePreemptionForSingleLevelQueue
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *         root      *       /  |  \      *      a   b   c      *</pre>      *      * Both a/b/c can access x, and guaranteed_capacity(x) of them is 40:20:40.      * a/b/c's max resource is 100. b is disable-preemption      *      * Two nodes, n1 has 100 x, n2 has 100 NO_LABEL.      *      * 2 apps in cluster. app1 in a (usage=50), app2 in b(usage=30), app3 in      * c(usage=20). All of them have 50 pending resource.      *      * After preemption, app1 will be preempt 10 containers and app2 will not be      * preempted      */
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
comment|//root
literal|"-a(=[80 80 0 0],x=[40 100 50 50]);"
operator|+
comment|// a
literal|"-b(=[10 100 0 0],x=[20 100 30 0]);"
operator|+
comment|// b
literal|"-c(=[10 100 0 0],x=[40 100 20 50])"
decl_stmt|;
comment|//c
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1,n1,x,50,false);"
operator|+
comment|// 50x in n1
literal|"b\t"
comment|// app2 in b
operator|+
literal|"(1,1,n1,x,30,false);"
operator|+
comment|// 30x in n1
literal|"c\t"
comment|// app3 in c
operator|+
literal|"(1,1,n1,x,20,false)"
decl_stmt|;
comment|// 20x in n1
name|conf
operator|.
name|setPreemptionDisabled
argument_list|(
literal|"root.b"
argument_list|,
literal|true
argument_list|)
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
comment|// 10 preempted from app1, nothing preempted from app2-app3
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
DECL|method|testNodePartitionNonAccessibleQueuesSharePartitionedResource ()
specifier|public
name|void
name|testNodePartitionNonAccessibleQueuesSharePartitionedResource
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *           root      *        _________      *       /  |   |  \      *      a   b   c   d      *</pre>      *      * a/b can access x, their capacity is 50:50 c/d cannot access x.      *      * a uses 0, wants 30      * b(app1) uses 30, wants 0      * c(app2)&d(app3) use 35, wants 50      *      * After preemption, c/d will be preempted 15 containers, because idle      * resource = 100 - 30 (which is used by b) - 30 (which is asked by a) = 40      * will be divided by c/d, so each of c/d get 20.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
operator|+
comment|// default partition
literal|"x=100,false"
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
comment|//root
literal|"-a(=[25 100 0 0],x=[50 100 0 30]);"
operator|+
comment|// a
literal|"-b(=[25 100 0 0],x=[50 100 30 0]);"
operator|+
comment|// b
literal|"-c(=[25 100 1 0],x=[0 0 35 50]);"
operator|+
comment|//c
literal|"-d(=[25 100 1 0],x=[0 0 35 50])"
decl_stmt|;
comment|//d
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"b\t"
comment|// app1 in b
operator|+
literal|"(1,1,n1,x,30,false);"
operator|+
comment|// 50x in n1
literal|"c\t"
comment|// app2 in c
operator|+
literal|"(1,1,n2,,1,false)"
comment|// AM container (in n2)
operator|+
literal|"(1,1,n1,,30,false);"
operator|+
comment|// 30x in n1 (ignore exclusivity)
literal|"d\t"
comment|// app3 in d
operator|+
literal|"(1,1,n2,,1,false)"
comment|// AM container (in n2)
operator|+
literal|"(1,1,n1,,30,false)"
decl_stmt|;
comment|// 30x in n1 (ignore exclusivity)
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
comment|// 15 will be preempted app2/app3
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
DECL|method|testHierarchyPreemptionForMultiplePartitions ()
specifier|public
name|void
name|testHierarchyPreemptionForMultiplePartitions
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *           root      *           /  \      *          a    b      *        /  \  /  \      *       a1  a2 b1  b2      *</pre>      *      * Both a/b can access x/y, and in all hierarchy capacity ratio is 50:50.      * So for a1/a2/b1/b2, all of them can access 25x, 25y      *      * a1 uses 35x, 25y      * a2 uses 25x, 15y      * b1 uses 15x, 25y      * b2 uses 25x 35y      *      * So as a result, a2 will preempt from b2, and b1 will preempt from a1.      *      * After preemption, a1 will be preempted 10x and b2 will be preempted 10y.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
operator|+
comment|// default partition
literal|"x=100,true;"
operator|+
comment|// partition=x
literal|"y=100,true"
decl_stmt|;
comment|// partition=y
name|String
name|nodesConfig
init|=
literal|"n1=x;"
operator|+
comment|// n1 has partition=x
literal|"n2=y;"
operator|+
comment|// n2 has partition=y
literal|"n3="
decl_stmt|;
comment|// n3 is default partition
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 0 0],x=[100 100 100 100],y=[100 100 100 100]);"
operator|+
comment|//root
literal|"-a(=[50 100 0 0],x=[50 100 60 40],y=[50 100 40 40]);"
operator|+
comment|// a
literal|"--a1(=[25 100 0 0],x=[25 100 35 20],y=[25 100 25 20]);"
operator|+
comment|// a1
literal|"--a2(=[25 100 0 0],x=[25 100 25 20],y=[25 100 15 20]);"
operator|+
comment|// a2
literal|"-b(=[50 100 0 0],x=[50 100 40 40],y=[50 100 60 40]);"
operator|+
comment|// b
literal|"--b1(=[25 100 0 0],x=[25 100 15 20],y=[25 100 25 20]);"
operator|+
comment|// b1
literal|"--b2(=[25 100 0 0],x=[25 100 25 20],y=[25 100 35 20])"
decl_stmt|;
comment|// b2
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a1\t"
comment|// app1 in a1
operator|+
literal|"(1,1,n1,x,35,false)"
comment|// 35 of x
operator|+
literal|"(1,1,n2,y,25,false);"
operator|+
comment|// 25 of y
literal|"a2\t"
comment|// app2 in a2
operator|+
literal|"(1,1,n1,x,25,false)"
comment|// 25 of x
operator|+
literal|"(1,1,n2,y,15,false);"
operator|+
comment|// 15 of y
literal|"b1\t"
comment|// app3 in b1
operator|+
literal|"(1,1,n1,x,15,false)"
comment|// 15 of x
operator|+
literal|"(1,1,n2,y,25,false);"
operator|+
comment|// 25 of y
literal|"b2\t"
comment|// app4 in b2
operator|+
literal|"(1,1,n1,x,25,false)"
comment|// 25 of x
operator|+
literal|"(1,1,n2,y,35,false)"
decl_stmt|;
comment|// 35 of y
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
comment|// 10 will be preempted from app1 (a1) /app4 (b2)
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
DECL|method|testHierarchyPreemptionForDifferenceAcessibility ()
specifier|public
name|void
name|testHierarchyPreemptionForDifferenceAcessibility
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *           root      *           /  \      *          a    b      *        /  \  /  \      *       a1  a2 b1  b2      *</pre>      *      * a can access x only and b can access y only      *      * Capacities of a1/a2, b1/b2 is 50:50      *      * a1 uses 100x and b1 uses 80y      *      * So as a result, a1 will be preempted 50 containers and b1 will be      * preempted 30 containers      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
operator|+
comment|// default partition
literal|"x=100,true;"
operator|+
comment|// partition=x
literal|"y=100,true"
decl_stmt|;
comment|// partition=y
name|String
name|nodesConfig
init|=
literal|"n1=x;"
operator|+
comment|// n1 has partition=x
literal|"n2=y;"
operator|+
comment|// n2 has partition=y
literal|"n3="
decl_stmt|;
comment|// n3 is default partition
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[100 100 0 0],x=[100 100 100 100],y=[100 100 100 100]);"
operator|+
comment|//root
literal|"-a(=[50 100 0 0],x=[100 100 100 100]);"
operator|+
comment|// a
literal|"--a1(=[25 100 0 0],x=[50 100 100 0]);"
operator|+
comment|// a1
literal|"--a2(=[25 100 0 0],x=[50 100 0 100]);"
operator|+
comment|// a2
literal|"-b(=[50 100 0 0],y=[100 100 80 100]);"
operator|+
comment|// b
literal|"--b1(=[25 100 0 0],y=[50 100 80 0]);"
operator|+
comment|// b1
literal|"--b2(=[25 100 0 0],y=[50 100 0 100])"
decl_stmt|;
comment|// b2
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a1\t"
comment|// app1 in a1
operator|+
literal|"(1,1,n1,x,100,false);"
operator|+
comment|// 100 of x
literal|"b1\t"
comment|// app2 in b1
operator|+
literal|"(1,1,n2,y,80,false)"
decl_stmt|;
comment|// 80 of y
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
literal|50
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
literal|30
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
block|}
annotation|@
name|Test
DECL|method|testNodePartitionPreemptionWithVCoreResource ()
specifier|public
name|void
name|testNodePartitionPreemptionWithVCoreResource
parameter_list|()
throws|throws
name|IOException
block|{
comment|/**      * Queue structure is:      *      *<pre>      *       root      *       /  \      *      a    b      *</pre>      *      * Both a/b can access x, and guaranteed capacity of them is 50:50. Two      * nodes, n1 has 100 x, n2 has 100 NO_LABEL 4 applications in the cluster,      * app1/app2 in a, and app3/app4 in b. app1 uses 80 x, app2 uses 20      * NO_LABEL, app3 uses 20 x, app4 uses 80 NO_LABEL. Both a/b have 50 pending      * resource for x and NO_LABEL      *      * After preemption, it should preempt 30 from app1, and 30 from app4.      */
name|String
name|labelsConfig
init|=
literal|"=100:200,true;"
operator|+
comment|// default partition
literal|"x=100:200,true"
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
literal|"root(=[100:200 100:200 100:200 100:200],x=[100:200 100:200 100:200 100:200]);"
operator|+
comment|// root
literal|"-a(=[50:100 100:200 20:40 50:100],x=[50:100 100:200 80:160 50:100]);"
operator|+
comment|// a
literal|"-b(=[50:100 100:200 80:160 50:100],x=[50:100 100:200 20:40 50:100])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|// queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t"
comment|// app1 in a
operator|+
literal|"(1,1:2,n1,x,80,false);"
operator|+
comment|// 80 * x in n1
literal|"a\t"
comment|// app2 in a
operator|+
literal|"(1,1:2,n2,,20,false);"
operator|+
comment|// 20 default in n2
literal|"b\t"
comment|// app3 in b
operator|+
literal|"(1,1:2,n1,x,20,false);"
operator|+
comment|// 20 * x in n1
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1:2,n2,,80,false)"
decl_stmt|;
comment|// 80 default in n2
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
comment|// 30 preempted from app1, 30 preempted from app4, and nothing preempted
comment|// from app2/app3
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
literal|30
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
DECL|method|testNormalizeGuaranteeWithMultipleResource ()
specifier|public
name|void
name|testNormalizeGuaranteeWithMultipleResource
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Initialize resource map
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
name|String
name|RESOURCE_1
init|=
literal|"res1"
decl_stmt|;
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
comment|/**      * Queue structure is:      *      *<pre>      *           root      *           /  \      *          a    b      *        /  \  /  \      *       a1  a2 b1  b2      *</pre>      *      * a1 and b2 are using most of resources.      * a2 and b1 needs more resources. Both are under served.      * hence demand will consider both queue's need while trying to      * do preemption.      */
name|String
name|labelsConfig
init|=
literal|"=100,true;"
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
literal|"-a(=[50:80:4 100:100:10 80:90:10 30:20:4]);"
operator|+
comment|// a
literal|"--a1(=[25:30:2 100:50:10 80:90:10 0]);"
operator|+
comment|// a1
literal|"--a2(=[25:50:2 100:50:10 0 30:20:4]);"
operator|+
comment|// a2
literal|"-b(=[50:20:6 100:100:10 20:10 40:50:8]);"
operator|+
comment|// b
literal|"--b1(=[25:5:4 100:20:10 0 20:10:4]);"
operator|+
comment|// b1
literal|"--b2(=[25:15:2 100:20:10 20:10 20:10:4])"
decl_stmt|;
comment|// b2
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a1\t"
comment|// app1 in a1
operator|+
literal|"(1,8:9:1,n1,,10,false);"
operator|+
literal|"b2\t"
comment|// app2 in b2
operator|+
literal|"(1,2:1,n1,,10,false)"
decl_stmt|;
comment|// 80 of y
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
literal|7
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
name|riMap
operator|.
name|remove
argument_list|(
name|RESOURCE_1
argument_list|)
expr_stmt|;
name|ResourceUtils
operator|.
name|initializeResourcesFromResourceInformationMap
argument_list|(
name|riMap
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

