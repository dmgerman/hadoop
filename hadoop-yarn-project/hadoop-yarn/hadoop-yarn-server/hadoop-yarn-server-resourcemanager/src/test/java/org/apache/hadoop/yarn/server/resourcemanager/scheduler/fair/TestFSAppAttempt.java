begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|fair
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
name|conf
operator|.
name|Configuration
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
name|Resource
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|NodeType
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
name|QueueMetrics
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
name|fair
operator|.
name|policies
operator|.
name|DominantResourceFairnessPolicy
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
name|fair
operator|.
name|policies
operator|.
name|FairSharePolicy
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
name|fair
operator|.
name|policies
operator|.
name|FifoPolicy
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
name|Clock
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestFSAppAttempt
specifier|public
class|class
name|TestFSAppAttempt
extends|extends
name|FairSchedulerTestBase
block|{
DECL|class|MockClock
specifier|private
class|class
name|MockClock
implements|implements
name|Clock
block|{
DECL|field|time
specifier|private
name|long
name|time
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|getTime ()
specifier|public
name|long
name|getTime
parameter_list|()
block|{
return|return
name|time
return|;
block|}
DECL|method|tick (int seconds)
specifier|public
name|void
name|tick
parameter_list|(
name|int
name|seconds
parameter_list|)
block|{
name|time
operator|=
name|time
operator|+
name|seconds
operator|*
literal|1000
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|createConfiguration
argument_list|()
decl_stmt|;
name|resourceManager
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|scheduler
operator|=
operator|(
name|FairScheduler
operator|)
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelayScheduling ()
specifier|public
name|void
name|testDelayScheduling
parameter_list|()
block|{
name|FSLeafQueue
name|queue
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSLeafQueue
operator|.
name|class
argument_list|)
decl_stmt|;
name|Priority
name|prio
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|prio
operator|.
name|getPriority
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|double
name|nodeLocalityThreshold
init|=
literal|.5
decl_stmt|;
name|double
name|rackLocalityThreshold
init|=
literal|.6
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|createAppAttemptId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RMContext
name|rmContext
init|=
name|resourceManager
operator|.
name|getRMContext
argument_list|()
decl_stmt|;
name|FSAppAttempt
name|schedulerApp
init|=
operator|new
name|FSAppAttempt
argument_list|(
name|scheduler
argument_list|,
name|applicationAttemptId
argument_list|,
literal|"user1"
argument_list|,
name|queue
argument_list|,
literal|null
argument_list|,
name|rmContext
argument_list|)
decl_stmt|;
comment|// Default level should be node-local
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
name|nodeLocalityThreshold
argument_list|,
name|rackLocalityThreshold
argument_list|)
argument_list|)
expr_stmt|;
comment|// First five scheduling opportunities should remain node local
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|schedulerApp
operator|.
name|addSchedulingOpportunity
argument_list|(
name|prio
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
name|nodeLocalityThreshold
argument_list|,
name|rackLocalityThreshold
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// After five it should switch to rack local
name|schedulerApp
operator|.
name|addSchedulingOpportunity
argument_list|(
name|prio
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|RACK_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
name|nodeLocalityThreshold
argument_list|,
name|rackLocalityThreshold
argument_list|)
argument_list|)
expr_stmt|;
comment|// Manually set back to node local
name|schedulerApp
operator|.
name|resetAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|)
expr_stmt|;
name|schedulerApp
operator|.
name|resetSchedulingOpportunities
argument_list|(
name|prio
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
name|nodeLocalityThreshold
argument_list|,
name|rackLocalityThreshold
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now escalate again to rack-local, then to off-switch
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|schedulerApp
operator|.
name|addSchedulingOpportunity
argument_list|(
name|prio
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
name|nodeLocalityThreshold
argument_list|,
name|rackLocalityThreshold
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|schedulerApp
operator|.
name|addSchedulingOpportunity
argument_list|(
name|prio
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|RACK_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
name|nodeLocalityThreshold
argument_list|,
name|rackLocalityThreshold
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|schedulerApp
operator|.
name|addSchedulingOpportunity
argument_list|(
name|prio
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|RACK_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
name|nodeLocalityThreshold
argument_list|,
name|rackLocalityThreshold
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|schedulerApp
operator|.
name|addSchedulingOpportunity
argument_list|(
name|prio
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|OFF_SWITCH
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
name|nodeLocalityThreshold
argument_list|,
name|rackLocalityThreshold
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelaySchedulingForContinuousScheduling ()
specifier|public
name|void
name|testDelaySchedulingForContinuousScheduling
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|FSLeafQueue
name|queue
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
operator|.
name|getLeafQueue
argument_list|(
literal|"queue"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Priority
name|prio
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|prio
operator|.
name|getPriority
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|MockClock
name|clock
init|=
operator|new
name|MockClock
argument_list|()
decl_stmt|;
name|scheduler
operator|.
name|setClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|long
name|nodeLocalityDelayMs
init|=
literal|5
operator|*
literal|1000L
decl_stmt|;
comment|// 5 seconds
name|long
name|rackLocalityDelayMs
init|=
literal|6
operator|*
literal|1000L
decl_stmt|;
comment|// 6 seconds
name|RMContext
name|rmContext
init|=
name|resourceManager
operator|.
name|getRMContext
argument_list|()
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|createAppAttemptId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|FSAppAttempt
name|schedulerApp
init|=
operator|new
name|FSAppAttempt
argument_list|(
name|scheduler
argument_list|,
name|applicationAttemptId
argument_list|,
literal|"user1"
argument_list|,
name|queue
argument_list|,
literal|null
argument_list|,
name|rmContext
argument_list|)
decl_stmt|;
comment|// Default level should be node-local
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevelByTime
argument_list|(
name|prio
argument_list|,
name|nodeLocalityDelayMs
argument_list|,
name|rackLocalityDelayMs
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// after 4 seconds should remain node local
name|clock
operator|.
name|tick
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevelByTime
argument_list|(
name|prio
argument_list|,
name|nodeLocalityDelayMs
argument_list|,
name|rackLocalityDelayMs
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// after 6 seconds should switch to rack local
name|clock
operator|.
name|tick
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|RACK_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevelByTime
argument_list|(
name|prio
argument_list|,
name|nodeLocalityDelayMs
argument_list|,
name|rackLocalityDelayMs
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// manually set back to node local
name|schedulerApp
operator|.
name|resetAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|)
expr_stmt|;
name|schedulerApp
operator|.
name|resetSchedulingOpportunities
argument_list|(
name|prio
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevelByTime
argument_list|(
name|prio
argument_list|,
name|nodeLocalityDelayMs
argument_list|,
name|rackLocalityDelayMs
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now escalate again to rack-local, then to off-switch
name|clock
operator|.
name|tick
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|RACK_LOCAL
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevelByTime
argument_list|(
name|prio
argument_list|,
name|nodeLocalityDelayMs
argument_list|,
name|rackLocalityDelayMs
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|clock
operator|.
name|tick
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|OFF_SWITCH
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevelByTime
argument_list|(
name|prio
argument_list|,
name|nodeLocalityDelayMs
argument_list|,
name|rackLocalityDelayMs
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * Ensure that when negative paramaters are given (signaling delay scheduling    * no tin use), the least restrictive locality level is returned.    */
DECL|method|testLocalityLevelWithoutDelays ()
specifier|public
name|void
name|testLocalityLevelWithoutDelays
parameter_list|()
block|{
name|FSLeafQueue
name|queue
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSLeafQueue
operator|.
name|class
argument_list|)
decl_stmt|;
name|Priority
name|prio
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|prio
operator|.
name|getPriority
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|RMContext
name|rmContext
init|=
name|resourceManager
operator|.
name|getRMContext
argument_list|()
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|createAppAttemptId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|FSAppAttempt
name|schedulerApp
init|=
operator|new
name|FSAppAttempt
argument_list|(
name|scheduler
argument_list|,
name|applicationAttemptId
argument_list|,
literal|"user1"
argument_list|,
name|queue
argument_list|,
literal|null
argument_list|,
name|rmContext
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NodeType
operator|.
name|OFF_SWITCH
argument_list|,
name|schedulerApp
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|prio
argument_list|,
literal|10
argument_list|,
operator|-
literal|1.0
argument_list|,
operator|-
literal|1.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHeadroom ()
specifier|public
name|void
name|testHeadroom
parameter_list|()
block|{
specifier|final
name|FairScheduler
name|mockScheduler
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FairScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockScheduler
operator|.
name|getClock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scheduler
operator|.
name|getClock
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FSLeafQueue
name|mockQueue
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSLeafQueue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|queueFairShare
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|4096
argument_list|,
literal|4
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|queueUsage
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|clusterResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|8192
argument_list|,
literal|8
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|clusterUsage
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|6144
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|QueueMetrics
name|fakeRootQueueMetrics
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|QueueMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|createAppAttemptId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RMContext
name|rmContext
init|=
name|resourceManager
operator|.
name|getRMContext
argument_list|()
decl_stmt|;
name|FSAppAttempt
name|schedulerApp
init|=
operator|new
name|FSAppAttempt
argument_list|(
name|mockScheduler
argument_list|,
name|applicationAttemptId
argument_list|,
literal|"user1"
argument_list|,
name|mockQueue
argument_list|,
literal|null
argument_list|,
name|rmContext
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockQueue
operator|.
name|getFairShare
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|queueFairShare
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockQueue
operator|.
name|getResourceUsage
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|queueUsage
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockScheduler
operator|.
name|getClusterResource
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clusterResource
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fakeRootQueueMetrics
operator|.
name|getAllocatedResources
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clusterUsage
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockScheduler
operator|.
name|getRootQueueMetrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fakeRootQueueMetrics
argument_list|)
expr_stmt|;
name|int
name|minClusterAvailableMemory
init|=
literal|2048
decl_stmt|;
name|int
name|minClusterAvailableCPU
init|=
literal|6
decl_stmt|;
name|int
name|minQueueAvailableCPU
init|=
literal|3
decl_stmt|;
comment|// Min of Memory and CPU across cluster and queue is used in
comment|// DominantResourceFairnessPolicy
name|Mockito
operator|.
name|when
argument_list|(
name|mockQueue
operator|.
name|getPolicy
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SchedulingPolicy
operator|.
name|getInstance
argument_list|(
name|DominantResourceFairnessPolicy
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verifyHeadroom
argument_list|(
name|schedulerApp
argument_list|,
name|minClusterAvailableMemory
argument_list|,
name|minQueueAvailableCPU
argument_list|)
expr_stmt|;
comment|// Fair and Fifo ignore CPU of queue, so use cluster available CPU
name|Mockito
operator|.
name|when
argument_list|(
name|mockQueue
operator|.
name|getPolicy
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SchedulingPolicy
operator|.
name|getInstance
argument_list|(
name|FairSharePolicy
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verifyHeadroom
argument_list|(
name|schedulerApp
argument_list|,
name|minClusterAvailableMemory
argument_list|,
name|minClusterAvailableCPU
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockQueue
operator|.
name|getPolicy
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SchedulingPolicy
operator|.
name|getInstance
argument_list|(
name|FifoPolicy
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verifyHeadroom
argument_list|(
name|schedulerApp
argument_list|,
name|minClusterAvailableMemory
argument_list|,
name|minClusterAvailableCPU
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyHeadroom (FSAppAttempt schedulerApp, int expectedMemory, int expectedCPU)
specifier|protected
name|void
name|verifyHeadroom
parameter_list|(
name|FSAppAttempt
name|schedulerApp
parameter_list|,
name|int
name|expectedMemory
parameter_list|,
name|int
name|expectedCPU
parameter_list|)
block|{
name|Resource
name|headroom
init|=
name|schedulerApp
operator|.
name|getHeadroom
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedMemory
argument_list|,
name|headroom
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedCPU
argument_list|,
name|headroom
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

