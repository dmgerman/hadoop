begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ApplicationId
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
name|RMAppState
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
name|queuemanagement
operator|.
name|GuaranteedOrZeroCapacityOverTimePolicy
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
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
operator|.
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
import|;
end_import

begin_import
import|import static
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
name|CSQueueUtils
operator|.
name|EPSILON
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

begin_class
DECL|class|TestQueueManagementDynamicEditPolicy
specifier|public
class|class
name|TestQueueManagementDynamicEditPolicy
extends|extends
name|TestCapacitySchedulerAutoCreatedQueueBase
block|{
DECL|field|policy
specifier|private
name|QueueManagementDynamicEditPolicy
name|policy
init|=
operator|new
name|QueueManagementDynamicEditPolicy
argument_list|()
decl_stmt|;
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
name|policy
operator|.
name|init
argument_list|(
name|cs
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|cs
operator|.
name|getRMContext
argument_list|()
argument_list|,
name|cs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEditSchedule ()
specifier|public
name|void
name|testEditSchedule
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|policy
operator|.
name|getManagedParentQueues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|CSQueue
name|parentQueue
init|=
name|cs
operator|.
name|getQueue
argument_list|(
name|PARENT_QUEUE
argument_list|)
decl_stmt|;
name|GuaranteedOrZeroCapacityOverTimePolicy
name|autoCreatedQueueManagementPolicy
init|=
call|(
name|GuaranteedOrZeroCapacityOverTimePolicy
call|)
argument_list|(
operator|(
name|ManagedParentQueue
operator|)
name|parentQueue
argument_list|)
operator|.
name|getAutoCreatedQueueManagementPolicy
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0f
argument_list|,
name|autoCreatedQueueManagementPolicy
operator|.
name|getAbsoluteActivatedChildQueueCapacity
argument_list|(
name|NO_LABEL
argument_list|)
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
comment|//submit app1 as USER1
name|ApplicationId
name|user1AppId
init|=
name|submitApp
argument_list|(
name|mockRM
argument_list|,
name|parentQueue
argument_list|,
name|USER1
argument_list|,
name|USER1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|expectedAbsChildQueueCapacity
init|=
name|populateExpectedAbsCapacityByLabelForParentQueue
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|validateInitialQueueEntitlement
argument_list|(
name|parentQueue
argument_list|,
name|USER1
argument_list|,
name|expectedAbsChildQueueCapacity
argument_list|,
name|accessibleNodeLabelsOnC
argument_list|)
expr_stmt|;
comment|//submit another app2 as USER2
name|ApplicationId
name|user2AppId
init|=
name|submitApp
argument_list|(
name|mockRM
argument_list|,
name|parentQueue
argument_list|,
name|USER2
argument_list|,
name|USER2
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|expectedAbsChildQueueCapacity
operator|=
name|populateExpectedAbsCapacityByLabelForParentQueue
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|validateInitialQueueEntitlement
argument_list|(
name|parentQueue
argument_list|,
name|USER2
argument_list|,
name|expectedAbsChildQueueCapacity
argument_list|,
name|accessibleNodeLabelsOnC
argument_list|)
expr_stmt|;
comment|//validate total activated abs capacity
name|assertEquals
argument_list|(
literal|0.2f
argument_list|,
name|autoCreatedQueueManagementPolicy
operator|.
name|getAbsoluteActivatedChildQueueCapacity
argument_list|(
name|NO_LABEL
argument_list|)
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
comment|//submit user_3 app. This cant be scheduled since there is no capacity
name|submitApp
argument_list|(
name|mockRM
argument_list|,
name|parentQueue
argument_list|,
name|USER3
argument_list|,
name|USER3
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|CSQueue
name|user3LeafQueue
init|=
name|cs
operator|.
name|getQueue
argument_list|(
name|USER3
argument_list|)
decl_stmt|;
name|validateCapacities
argument_list|(
operator|(
name|AutoCreatedLeafQueue
operator|)
name|user3LeafQueue
argument_list|,
literal|0.0f
argument_list|,
literal|0.0f
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|autoCreatedQueueManagementPolicy
operator|.
name|getAbsoluteActivatedChildQueueCapacity
argument_list|(
name|NO_LABEL
argument_list|)
argument_list|,
literal|0.2f
argument_list|,
name|EPSILON
argument_list|)
expr_stmt|;
comment|//deactivate USER2 queue
name|cs
operator|.
name|killAllAppsInQueue
argument_list|(
name|USER2
argument_list|)
expr_stmt|;
name|mockRM
operator|.
name|waitForState
argument_list|(
name|user2AppId
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
comment|//deactivate USER1 queue
name|cs
operator|.
name|killAllAppsInQueue
argument_list|(
name|USER1
argument_list|)
expr_stmt|;
name|mockRM
operator|.
name|waitForState
argument_list|(
name|user1AppId
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
name|policy
operator|.
name|editSchedule
argument_list|()
expr_stmt|;
name|waitForPolicyState
argument_list|(
literal|0.1f
argument_list|,
name|autoCreatedQueueManagementPolicy
argument_list|,
name|NO_LABEL
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|validateCapacities
argument_list|(
operator|(
name|AutoCreatedLeafQueue
operator|)
name|user3LeafQueue
argument_list|,
literal|0.5f
argument_list|,
literal|0.1f
argument_list|,
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|validateCapacitiesByLabel
argument_list|(
operator|(
name|ManagedParentQueue
operator|)
name|parentQueue
argument_list|,
operator|(
name|AutoCreatedLeafQueue
operator|)
name|user3LeafQueue
argument_list|,
name|NODEL_LABEL_GPU
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cleanupQueue
argument_list|(
name|USER1
argument_list|)
expr_stmt|;
name|cleanupQueue
argument_list|(
name|USER2
argument_list|)
expr_stmt|;
name|cleanupQueue
argument_list|(
name|USER3
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|waitForPolicyState (float expectedVal, GuaranteedOrZeroCapacityOverTimePolicy queueManagementPolicy, String nodeLabel, int timesec)
specifier|private
name|void
name|waitForPolicyState
parameter_list|(
name|float
name|expectedVal
parameter_list|,
name|GuaranteedOrZeroCapacityOverTimePolicy
name|queueManagementPolicy
parameter_list|,
name|String
name|nodeLabel
parameter_list|,
name|int
name|timesec
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|<
name|timesec
operator|*
literal|1000
condition|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|expectedVal
operator|-
name|queueManagementPolicy
operator|.
name|getAbsoluteActivatedChildQueueCapacity
argument_list|(
name|nodeLabel
argument_list|)
argument_list|)
operator|>
name|EPSILON
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

