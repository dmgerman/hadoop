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
DECL|field|queueMaxApps
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|queueMaxApps
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
name|TestFairScheduler
operator|.
name|MockClock
name|clock
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|clock
operator|=
operator|new
name|TestFairScheduler
operator|.
name|MockClock
argument_list|()
expr_stmt|;
name|FairScheduler
name|scheduler
init|=
name|mock
argument_list|(
name|FairScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
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
operator|new
name|FairSchedulerConfiguration
argument_list|(
name|conf
argument_list|)
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
name|AllocationConfiguration
name|allocConf
init|=
operator|new
name|AllocationConfiguration
argument_list|(
name|conf
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
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|queueMaxApps
operator|=
name|allocConf
operator|.
name|queueMaxApps
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
name|FSSchedulerApp
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
name|boolean
name|runnable
init|=
name|maxAppsEnforcer
operator|.
name|canAppBeRunnable
argument_list|(
name|queue
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|FSSchedulerApp
name|app
init|=
operator|new
name|FSSchedulerApp
argument_list|(
name|attId
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
literal|null
argument_list|,
literal|null
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
DECL|method|removeApp (FSSchedulerApp app)
specifier|private
name|void
name|removeApp
parameter_list|(
name|FSSchedulerApp
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
name|updateRunnabilityOnAppRemoval
argument_list|(
name|app
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
name|queueMaxApps
operator|.
name|put
argument_list|(
literal|"root"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|queueMaxApps
operator|.
name|put
argument_list|(
literal|"root.queue1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queueMaxApps
operator|.
name|put
argument_list|(
literal|"root.queue2"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|FSSchedulerApp
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
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
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
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
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
name|queueMaxApps
operator|.
name|put
argument_list|(
literal|"root.queue1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|FSSchedulerApp
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
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
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
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|leaf2
operator|.
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf2
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
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
name|queueMaxApps
operator|.
name|put
argument_list|(
literal|"root.queue1.leaf1"
argument_list|,
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
name|FSSchedulerApp
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
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf1
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
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
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf1
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf2
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
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
name|queueMaxApps
operator|.
name|put
argument_list|(
literal|"root.queue1"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|FSSchedulerApp
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
name|tick
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
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf1
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaf2
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
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
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|leaf2
operator|.
name|getRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaf2
operator|.
name|getNonRunnableAppSchedulables
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

