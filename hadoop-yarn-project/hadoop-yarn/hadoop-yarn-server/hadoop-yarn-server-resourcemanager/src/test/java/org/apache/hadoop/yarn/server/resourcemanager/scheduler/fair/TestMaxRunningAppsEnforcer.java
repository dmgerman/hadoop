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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|placement
operator|.
name|PlacementManager
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
name|ControlledClock
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
name|DefaultResourceCalculator
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

begin_class
DECL|class|TestMaxRunningAppsEnforcer
specifier|public
class|class
name|TestMaxRunningAppsEnforcer
block|{
DECL|field|queueManager
specifier|private
name|QueueManager
name|queueManager
decl_stmt|;
DECL|field|userMaxApps
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|userMaxApps
decl_stmt|;
DECL|field|maxAppsEnforcer
specifier|private
name|MaxRunningAppsEnforcer
name|maxAppsEnforcer
decl_stmt|;
DECL|field|appNum
specifier|private
name|int
name|appNum
decl_stmt|;
DECL|field|clock
specifier|private
name|ControlledClock
name|clock
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|scheduler
specifier|private
name|FairScheduler
name|scheduler
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|FairSchedulerConfiguration
name|conf
init|=
operator|new
name|FairSchedulerConfiguration
argument_list|()
decl_stmt|;
name|PlacementManager
name|placementManager
init|=
operator|new
name|PlacementManager
argument_list|()
decl_stmt|;
name|rmContext
operator|=
name|mock
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmContext
operator|.
name|getQueuePlacementManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|placementManager
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmContext
operator|.
name|getEpoch
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|clock
operator|=
operator|new
name|ControlledClock
argument_list|()
expr_stmt|;
name|scheduler
operator|=
name|mock
argument_list|(
name|FairScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getConf
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getConfig
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getClock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getResourceCalculator
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|DefaultResourceCalculator
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getRMContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|AllocationConfiguration
name|allocConf
init|=
operator|new
name|AllocationConfiguration
argument_list|(
name|scheduler
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|scheduler
operator|.
name|getAllocationConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|allocConf
argument_list|)
expr_stmt|;
name|queueManager
operator|=
operator|new
name|QueueManager
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|queueManager
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|userMaxApps
operator|=
name|allocConf
operator|.
name|userMaxApps
expr_stmt|;
name|maxAppsEnforcer
operator|=
operator|new
name|MaxRunningAppsEnforcer
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|appNum
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|addApp (FSLeafQueue queue, String user)
specifier|private
name|FSAppAttempt
name|addApp
parameter_list|(
name|FSLeafQueue
name|queue
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0l
argument_list|,
name|appNum
operator|++
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|FSAppAttempt
name|app
init|=
operator|new
name|FSAppAttempt
argument_list|(
name|scheduler
argument_list|,
name|attId
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
literal|null
argument_list|,
name|rmContext
argument_list|)
decl_stmt|;
name|boolean
name|runnable
init|=
name|maxAppsEnforcer
operator|.
name|canAppBeRunnable
argument_list|(
name|queue
argument_list|,
name|app
argument_list|)
decl_stmt|;
name|queue
operator|.
name|addApp
argument_list|(
name|app
argument_list|,
name|runnable
argument_list|)
expr_stmt|;
if|if
condition|(
name|runnable
condition|)
block|{
name|maxAppsEnforcer
operator|.
name|trackRunnableApp
argument_list|(
name|app
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|maxAppsEnforcer
operator|.
name|trackNonRunnableApp
argument_list|(
name|app
argument_list|)
expr_stmt|;
block|}
return|return
name|app
return|;
block|}
DECL|method|removeApp (FSAppAttempt app)
specifier|private
name|void
name|removeApp
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|)
block|{
name|app
operator|.
name|getQueue
argument_list|()
operator|.
name|removeApp
argument_list|(
name|app
argument_list|)
expr_stmt|;
name|maxAppsEnforcer
operator|.
name|untrackRunnableApp
argument_list|(
name|app
argument_list|)
expr_stmt|;
name|maxAppsEnforcer
operator|.
name|updateRunnabilityOnAppRemoval
argument_list|(
name|app
argument_list|,
name|app
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveDoesNotEnableAnyApp ()
specifier|public
name|void
name|testRemoveDoesNotEnableAnyApp
parameter_list|()
block|{
name|FSParentQueue
name|root
init|=
name|queueManager
operator|.
name|getRootQueue
argument_list|()
decl_stmt|;
name|FSLeafQueue
name|leaf1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSLeafQueue
name|leaf2
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue2"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|root
operator|.
name|setMaxRunningApps
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|leaf1
operator|.
name|setMaxRunningApps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|leaf2
operator|.
name|setMaxRunningApps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|FSAppAttempt
name|app1
init|=
name|addApp
argument_list|(
name|leaf1
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|removeApp
argument_list|(
name|app1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveEnablesAppOnCousinQueue ()
specifier|public
name|void
name|testRemoveEnablesAppOnCousinQueue
parameter_list|()
block|{
name|FSLeafQueue
name|leaf1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1.subqueue1.leaf1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSLeafQueue
name|leaf2
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1.subqueue2.leaf2"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSParentQueue
name|queue1
init|=
name|queueManager
operator|.
name|getParentQueue
argument_list|(
literal|"root.queue1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|queue1
operator|.
name|setMaxRunningApps
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|FSAppAttempt
name|app1
init|=
name|addApp
argument_list|(
name|leaf1
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|removeApp
argument_list|(
name|app1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveEnablesOneByQueueOneByUser ()
specifier|public
name|void
name|testRemoveEnablesOneByQueueOneByUser
parameter_list|()
block|{
name|FSLeafQueue
name|leaf1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1.leaf1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSLeafQueue
name|leaf2
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1.leaf2"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|leaf1
operator|.
name|setMaxRunningApps
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|userMaxApps
operator|.
name|put
argument_list|(
literal|"user1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|FSAppAttempt
name|app1
init|=
name|addApp
argument_list|(
name|leaf1
argument_list|,
literal|"user1"
argument_list|)
decl_stmt|;
name|addApp
argument_list|(
name|leaf1
argument_list|,
literal|"user2"
argument_list|)
expr_stmt|;
name|addApp
argument_list|(
name|leaf1
argument_list|,
literal|"user3"
argument_list|)
expr_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf1
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|removeApp
argument_list|(
name|app1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf1
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoveEnablingOrderedByStartTime ()
specifier|public
name|void
name|testRemoveEnablingOrderedByStartTime
parameter_list|()
block|{
name|FSLeafQueue
name|leaf1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1.subqueue1.leaf1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSLeafQueue
name|leaf2
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1.subqueue2.leaf2"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSParentQueue
name|queue1
init|=
name|queueManager
operator|.
name|getParentQueue
argument_list|(
literal|"root.queue1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|queue1
operator|.
name|setMaxRunningApps
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|FSAppAttempt
name|app1
init|=
name|addApp
argument_list|(
name|leaf1
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|clock
operator|.
name|tickSec
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|addApp
argument_list|(
name|leaf1
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf1
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|removeApp
argument_list|(
name|app1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleAppsWaitingOnCousinQueue ()
specifier|public
name|void
name|testMultipleAppsWaitingOnCousinQueue
parameter_list|()
block|{
name|FSLeafQueue
name|leaf1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1.subqueue1.leaf1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSLeafQueue
name|leaf2
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.queue1.subqueue2.leaf2"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSParentQueue
name|queue1
init|=
name|queueManager
operator|.
name|getParentQueue
argument_list|(
literal|"root.queue1"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|queue1
operator|.
name|setMaxRunningApps
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|FSAppAttempt
name|app1
init|=
name|addApp
argument_list|(
name|leaf1
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|addApp
argument_list|(
name|leaf2
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|removeApp
argument_list|(
name|app1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf1
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|leaf2
operator|.
name|getNumRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNumNonRunnableApps
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiListStartTimeIteratorEmptyAppLists ()
specifier|public
name|void
name|testMultiListStartTimeIteratorEmptyAppLists
parameter_list|()
block|{
name|List
argument_list|<
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
argument_list|>
name|lists
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|FSAppAttempt
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|lists
operator|.
name|add
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|mockAppAttempt
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|lists
operator|.
name|add
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|mockAppAttempt
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|FSAppAttempt
argument_list|>
name|iter
init|=
operator|new
name|MaxRunningAppsEnforcer
operator|.
name|MultiListStartTimeIterator
argument_list|(
name|lists
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|mockAppAttempt (long startTime)
specifier|private
name|FSAppAttempt
name|mockAppAttempt
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|FSAppAttempt
name|schedApp
init|=
name|mock
argument_list|(
name|FSAppAttempt
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|schedApp
operator|.
name|getStartTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
return|return
name|schedApp
return|;
block|}
block|}
end_class

end_unit

