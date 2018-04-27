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
name|junit
operator|.
name|Test
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
DECL|class|TestProportionalCapacityPreemptionPolicyInterQueueWithDRF
specifier|public
class|class
name|TestProportionalCapacityPreemptionPolicyInterQueueWithDRF
extends|extends
name|ProportionalCapacityPreemptionPolicyMockFramework
block|{
annotation|@
name|Test
DECL|method|testInterQueuePreemptionWithMultipleResource ()
specifier|public
name|void
name|testInterQueuePreemptionWithMultipleResource
parameter_list|()
throws|throws
name|Exception
block|{
comment|/**      * Queue structure is:      *      *<pre>      *           root      *           /  \      *          a    b      *</pre>      *      */
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
literal|"-a(=[50:100 100:200 40:80 30:70]);"
operator|+
comment|// a
literal|"-b(=[50:100 100:200 60:120 40:50])"
decl_stmt|;
comment|// b
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
literal|"a\t(1,2:4,n1,,20,false);"
operator|+
comment|// app1 in a
literal|"b\t(1,2:4,n1,,30,false)"
decl_stmt|;
comment|// app2 in b
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
comment|// Preemption should happen in Queue b, preempt<10,20> to Queue a
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
block|}
block|}
end_class

end_unit

