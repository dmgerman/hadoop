begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
package|;
end_package

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
name|util
operator|.
name|ExitUtil
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|api
operator|.
name|records
operator|.
name|Container
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
name|ContainerId
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
name|ContainerState
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
name|ResourceRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatResponse
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
name|api
operator|.
name|records
operator|.
name|NodeAction
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
name|recovery
operator|.
name|MemoryRMStateStore
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
name|recovery
operator|.
name|RMStateStore
operator|.
name|ApplicationAttemptState
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
name|recovery
operator|.
name|RMStateStore
operator|.
name|ApplicationState
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
name|recovery
operator|.
name|RMStateStore
operator|.
name|RMState
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
name|RMApp
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
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
name|attempt
operator|.
name|RMAppAttemptState
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
name|BuilderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
DECL|class|TestRMRestart
specifier|public
class|class
name|TestRMRestart
block|{
annotation|@
name|Test
DECL|method|testRMRestart ()
specifier|public
name|void
name|testRMRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|Logger
name|rootLogger
init|=
name|LogManager
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RECOVERY_ENABLED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_STORE
argument_list|,
literal|"org.apache.hadoop.yarn.server.resourcemanager.recovery.MemoryRMStateStore"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
literal|"org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler"
argument_list|)
expr_stmt|;
name|MemoryRMStateStore
name|memStore
init|=
operator|new
name|MemoryRMStateStore
argument_list|()
decl_stmt|;
name|memStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|RMState
name|rmState
init|=
name|memStore
operator|.
name|getState
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationState
argument_list|>
name|rmAppState
init|=
name|rmState
operator|.
name|getApplicationState
argument_list|()
decl_stmt|;
comment|// PHASE 1: create state in an RM
comment|// start RM
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
decl_stmt|;
comment|// start like normal because state is empty
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
operator|new
name|MockNM
argument_list|(
literal|"h1:1234"
argument_list|,
literal|15120
argument_list|,
name|rm1
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|MockNM
name|nm2
init|=
operator|new
name|MockNM
argument_list|(
literal|"h2:5678"
argument_list|,
literal|15120
argument_list|,
name|rm1
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|registerNode
argument_list|()
expr_stmt|;
name|nm2
operator|.
name|registerNode
argument_list|()
expr_stmt|;
comment|// nm2 will not heartbeat with RM1
comment|// create app that will not be saved because it will finish
name|RMApp
name|app0
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|RMAppAttempt
name|attempt0
init|=
name|app0
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
comment|// spot check that app is saved
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rmAppState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MockAM
name|am0
init|=
name|rm1
operator|.
name|sendAMLaunched
argument_list|(
name|attempt0
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am0
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|am0
operator|.
name|unregisterAppAttempt
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|attempt0
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
name|am0
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|app0
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
comment|// spot check that app is not saved anymore
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAppState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// create app that gets launched and does allocate before RM restart
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|// assert app1 info is saved
name|ApplicationState
name|appState
init|=
name|rmAppState
operator|.
name|get
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appState
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appState
operator|.
name|getAttemptCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appState
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|app1
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|//kick the scheduling to allocate AM container
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// assert app1 attempt is saved
name|RMAppAttempt
name|attempt1
init|=
name|app1
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|ApplicationAttemptId
name|attemptId1
init|=
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|attemptId1
argument_list|,
name|RMAppAttemptState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appState
operator|.
name|getAttemptCount
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationAttemptState
name|attemptState
init|=
name|appState
operator|.
name|getAttempt
argument_list|(
name|attemptId1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|attemptState
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|attemptId1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|attemptState
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// launch the AM
name|MockAM
name|am1
init|=
name|rm1
operator|.
name|sendAMLaunched
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am1
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|// AM request for containers
name|am1
operator|.
name|allocate
argument_list|(
literal|"h1"
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|// kick the scheduler
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|conts
init|=
name|am1
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
decl_stmt|;
while|while
condition|(
name|conts
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conts
operator|.
name|addAll
argument_list|(
name|am1
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
comment|// create app that does not get launched by RM before RM restart
name|RMApp
name|app2
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|// assert app2 info is saved
name|appState
operator|=
name|rmAppState
operator|.
name|get
argument_list|(
name|app2
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appState
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|appState
operator|.
name|getAttemptCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appState
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|app2
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// create unmanaged app
name|RMApp
name|appUnmanaged
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|"someApp"
argument_list|,
literal|"someUser"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|unmanagedAttemptId
init|=
name|appUnmanaged
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
comment|// assert appUnmanaged info is saved
name|ApplicationId
name|unmanagedAppId
init|=
name|appUnmanaged
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|appState
operator|=
name|rmAppState
operator|.
name|get
argument_list|(
name|unmanagedAppId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appState
argument_list|)
expr_stmt|;
comment|// wait for attempt to reach LAUNCHED state
name|rm1
operator|.
name|waitForState
argument_list|(
name|unmanagedAttemptId
argument_list|,
name|RMAppAttemptState
operator|.
name|LAUNCHED
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|unmanagedAppId
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
comment|// assert unmanaged attempt info is saved
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|appState
operator|.
name|getAttemptCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appState
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appUnmanaged
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// PHASE 2: create new RM and start from old state
comment|// create new RM to represent restart and recover state
name|MockRM
name|rm2
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
decl_stmt|;
comment|// start new RM
name|rm2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// change NM to point to new RM
name|nm1
operator|.
name|setResourceTrackerService
argument_list|(
name|rm2
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|setResourceTrackerService
argument_list|(
name|rm2
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify load of old state
comment|// only 2 apps are loaded since unmanaged app is not loaded back since it
comment|// cannot be restarted by the RM this will change with work preserving RM
comment|// restart in which AMs/NMs are not rebooted
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rm2
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify correct number of attempts and other data
name|RMApp
name|loadedApp1
init|=
name|rm2
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|loadedApp1
argument_list|)
expr_stmt|;
comment|//Assert.assertEquals(1, loadedApp1.getAppAttempts().size());
name|Assert
operator|.
name|assertEquals
argument_list|(
name|app1
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|loadedApp1
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|RMApp
name|loadedApp2
init|=
name|rm2
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|app2
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|loadedApp2
argument_list|)
expr_stmt|;
comment|//Assert.assertEquals(0, loadedApp2.getAppAttempts().size());
name|Assert
operator|.
name|assertEquals
argument_list|(
name|app2
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|loadedApp2
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify state machine kicked into expected states
name|rm2
operator|.
name|waitForState
argument_list|(
name|loadedApp1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
name|rm2
operator|.
name|waitForState
argument_list|(
name|loadedApp2
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
comment|// verify new attempts created
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|loadedApp1
operator|.
name|getAppAttempts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|loadedApp2
operator|.
name|getAppAttempts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify old AM is not accepted
comment|// change running AM to talk to new RM
name|am1
operator|.
name|setAMRMProtocol
argument_list|(
name|rm2
operator|.
name|getApplicationMasterService
argument_list|()
argument_list|)
expr_stmt|;
name|AllocateResponse
name|allocResponse
init|=
name|am1
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|allocResponse
operator|.
name|getReboot
argument_list|()
argument_list|)
expr_stmt|;
comment|// NM should be rebooted on heartbeat, even first heartbeat for nm2
name|NodeHeartbeatResponse
name|hbResponse
init|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeAction
operator|.
name|REBOOT
argument_list|,
name|hbResponse
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
name|hbResponse
operator|=
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|NodeAction
operator|.
name|REBOOT
argument_list|,
name|hbResponse
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
comment|// new NM to represent NM re-register
name|nm1
operator|=
name|rm2
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|15120
argument_list|)
expr_stmt|;
name|nm2
operator|=
name|rm2
operator|.
name|registerNode
argument_list|(
literal|"h2:5678"
argument_list|,
literal|15120
argument_list|)
expr_stmt|;
comment|// verify no more reboot response sent
name|hbResponse
operator|=
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|REBOOT
operator|!=
name|hbResponse
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
name|hbResponse
operator|=
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|NodeAction
operator|.
name|REBOOT
operator|!=
name|hbResponse
operator|.
name|getNodeAction
argument_list|()
argument_list|)
expr_stmt|;
comment|// assert app1 attempt is saved
name|attempt1
operator|=
name|loadedApp1
operator|.
name|getCurrentAppAttempt
argument_list|()
expr_stmt|;
name|attemptId1
operator|=
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
expr_stmt|;
name|rm2
operator|.
name|waitForState
argument_list|(
name|attemptId1
argument_list|,
name|RMAppAttemptState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
name|appState
operator|=
name|rmAppState
operator|.
name|get
argument_list|(
name|loadedApp1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|attemptState
operator|=
name|appState
operator|.
name|getAttempt
argument_list|(
name|attemptId1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|attemptState
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|attemptId1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|attemptState
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Nodes on which the AM's run
name|MockNM
name|am1Node
init|=
name|nm1
decl_stmt|;
if|if
condition|(
name|attemptState
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"h2"
argument_list|)
condition|)
block|{
name|am1Node
operator|=
name|nm2
expr_stmt|;
block|}
comment|// assert app2 attempt is saved
name|RMAppAttempt
name|attempt2
init|=
name|loadedApp2
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|ApplicationAttemptId
name|attemptId2
init|=
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
name|rm2
operator|.
name|waitForState
argument_list|(
name|attemptId2
argument_list|,
name|RMAppAttemptState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
name|appState
operator|=
name|rmAppState
operator|.
name|get
argument_list|(
name|loadedApp2
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|attemptState
operator|=
name|appState
operator|.
name|getAttempt
argument_list|(
name|attemptId2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|attemptState
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|attemptId2
argument_list|,
literal|1
argument_list|)
argument_list|,
name|attemptState
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|MockNM
name|am2Node
init|=
name|nm1
decl_stmt|;
if|if
condition|(
name|attemptState
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"h2"
argument_list|)
condition|)
block|{
name|am2Node
operator|=
name|nm2
expr_stmt|;
block|}
comment|// start the AM's
name|am1
operator|=
name|rm2
operator|.
name|sendAMLaunched
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|am1
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|MockAM
name|am2
init|=
name|rm2
operator|.
name|sendAMLaunched
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|am2
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
comment|//request for containers
name|am1
operator|.
name|allocate
argument_list|(
literal|"h1"
argument_list|,
literal|1000
argument_list|,
literal|3
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|am2
operator|.
name|allocate
argument_list|(
literal|"h2"
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify container allocate continues to work
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conts
operator|=
name|am1
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
while|while
condition|(
name|conts
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conts
operator|.
name|addAll
argument_list|(
name|am1
operator|.
name|allocate
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
operator|.
name|getAllocatedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
comment|// finish the AM's
name|am1
operator|.
name|unregisterAppAttempt
argument_list|()
expr_stmt|;
name|am1Node
operator|.
name|nodeHeartbeat
argument_list|(
name|attempt1
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
name|am1
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|am2
operator|.
name|unregisterAppAttempt
argument_list|()
expr_stmt|;
name|am2Node
operator|.
name|nodeHeartbeat
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
name|am2
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
comment|// stop RM's
name|rm2
operator|.
name|stop
argument_list|()
expr_stmt|;
name|rm1
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// completed apps should be removed
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rmAppState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

