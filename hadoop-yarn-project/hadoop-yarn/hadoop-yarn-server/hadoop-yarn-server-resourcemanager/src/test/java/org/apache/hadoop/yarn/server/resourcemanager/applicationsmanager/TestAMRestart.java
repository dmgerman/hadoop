begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.applicationsmanager
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
name|applicationsmanager
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
name|HashMap
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
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
name|ApplicationAccessType
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
name|ContainerStatus
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
name|NMToken
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
name|resourcemanager
operator|.
name|MockAM
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
name|MockNM
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmcontainer
operator|.
name|RMContainerState
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
name|SchedulerApplicationAttempt
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
name|CapacityScheduler
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
comment|/**  * Test to restart the AM on failure.  *  */
end_comment

begin_class
DECL|class|TestAMRestart
specifier|public
class|class
name|TestAMRestart
block|{
annotation|@
name|Test
DECL|method|testAMRestartWithExistingContainers ()
specifier|public
name|void
name|testAMRestartWithExistingContainers
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|"name"
argument_list|,
literal|"user"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"default"
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|"MAPREDUCE"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MockNM
name|nm1
init|=
operator|new
name|MockNM
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|10240
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
name|MockNM
name|nm2
init|=
operator|new
name|MockNM
argument_list|(
literal|"127.0.0.1:2351"
argument_list|,
literal|4089
argument_list|,
name|rm1
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm2
operator|.
name|registerNode
argument_list|()
expr_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|int
name|NUM_CONTAINERS
init|=
literal|3
decl_stmt|;
comment|// allocate NUM_CONTAINERS containers
name|am1
operator|.
name|allocate
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|1024
argument_list|,
name|NUM_CONTAINERS
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
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
comment|// wait for containers to be allocated.
name|List
argument_list|<
name|Container
argument_list|>
name|containers
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
name|containers
operator|.
name|size
argument_list|()
operator|!=
name|NUM_CONTAINERS
condition|)
block|{
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|containers
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
literal|200
argument_list|)
expr_stmt|;
block|}
comment|// launch the 2nd container, for testing running container transferred.
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId2
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId2
argument_list|,
name|RMContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// launch the 3rd container, for testing container allocated by previous
comment|// attempt is completed by the next new attempt/
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|3
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId3
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId3
argument_list|,
name|RMContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// 4th container still in AQUIRED state. for testing Acquired container is
comment|// always killed.
name|ContainerId
name|containerId4
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId4
argument_list|,
name|RMContainerState
operator|.
name|ACQUIRED
argument_list|)
expr_stmt|;
comment|// 5th container is in Allocated state. for testing allocated container is
comment|// always killed.
name|am1
operator|.
name|allocate
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|1024
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
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId5
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|waitForContainerAllocated
argument_list|(
name|nm1
argument_list|,
name|containerId5
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId5
argument_list|,
name|RMContainerState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
comment|// 6th container is in Reserved state.
name|am1
operator|.
name|allocate
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|6000
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
name|ContainerId
name|containerId6
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SchedulerApplicationAttempt
name|schedulerAttempt
init|=
operator|(
operator|(
name|CapacityScheduler
operator|)
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
operator|)
operator|.
name|getCurrentAttemptForContainer
argument_list|(
name|containerId6
argument_list|)
decl_stmt|;
while|while
condition|(
name|schedulerAttempt
operator|.
name|getReservedContainers
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for container "
operator|+
name|containerId6
operator|+
literal|" to be reserved."
argument_list|)
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
comment|// assert containerId6 is reserved.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|containerId6
argument_list|,
name|schedulerAttempt
operator|.
name|getReservedContainers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
comment|// fail the AM by sending CONTAINER_FINISHED event without registering.
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
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
name|FAILED
argument_list|)
expr_stmt|;
comment|// wait for some time. previous AM's running containers should still remain
comment|// in scheduler even though am failed
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId2
argument_list|,
name|RMContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// acquired/allocated containers are cleaned up.
name|Assert
operator|.
name|assertNull
argument_list|(
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getRMContainer
argument_list|(
name|containerId4
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|getRMContainer
argument_list|(
name|containerId5
argument_list|)
argument_list|)
expr_stmt|;
comment|// wait for app to start a new attempt.
name|rm1
operator|.
name|waitForState
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
comment|// assert this is a new AM.
name|ApplicationAttemptId
name|newAttemptId
init|=
name|app1
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|newAttemptId
operator|.
name|equals
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// launch the new AM
name|RMAppAttempt
name|attempt2
init|=
name|app1
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|MockAM
name|am2
init|=
name|rm1
operator|.
name|sendAMLaunched
argument_list|(
name|attempt2
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|RegisterApplicationMasterResponse
name|registerResponse
init|=
name|am2
operator|.
name|registerAppAttempt
argument_list|()
decl_stmt|;
comment|// Assert two containers are running: container2 and container3;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|registerResponse
operator|.
name|getContainersFromPreviousAttempts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|containerId2Exists
init|=
literal|false
decl_stmt|,
name|containerId3Exists
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Container
name|container
range|:
name|registerResponse
operator|.
name|getContainersFromPreviousAttempts
argument_list|()
control|)
block|{
if|if
condition|(
name|container
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId2
argument_list|)
condition|)
block|{
name|containerId2Exists
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|container
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId3
argument_list|)
condition|)
block|{
name|containerId3Exists
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerId2Exists
operator|&&
name|containerId3Exists
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// complete container by sending the container complete event which has earlier
comment|// attempt's attemptId
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|3
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId3
argument_list|,
name|RMContainerState
operator|.
name|COMPLETED
argument_list|)
expr_stmt|;
comment|// Even though the completed container containerId3 event was sent to the
comment|// earlier failed attempt, new RMAppAttempt can also capture this container
comment|// info.
comment|// completed containerId4 is also transferred to the new attempt.
name|RMAppAttempt
name|newAttempt
init|=
name|app1
operator|.
name|getRMAppAttempt
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
comment|// 4 containers finished, acquired/allocated/reserved/completed.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|newAttempt
operator|.
name|getJustFinishedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|container3Exists
init|=
literal|false
decl_stmt|,
name|container4Exists
init|=
literal|false
decl_stmt|,
name|container5Exists
init|=
literal|false
decl_stmt|,
name|container6Exists
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ContainerStatus
name|status
range|:
name|newAttempt
operator|.
name|getJustFinishedContainers
argument_list|()
control|)
block|{
if|if
condition|(
name|status
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId3
argument_list|)
condition|)
block|{
comment|// containerId3 is the container ran by previous attempt but finished by the
comment|// new attempt.
name|container3Exists
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId4
argument_list|)
condition|)
block|{
comment|// containerId4 is the Acquired Container killed by the previous attempt,
comment|// it's now inside new attempt's finished container list.
name|container4Exists
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId5
argument_list|)
condition|)
block|{
comment|// containerId5 is the Allocated container killed by previous failed attempt.
name|container5Exists
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|status
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId6
argument_list|)
condition|)
block|{
comment|// containerId6 is the reserved container killed by previous failed attempt.
name|container6Exists
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|container3Exists
operator|&&
name|container4Exists
operator|&&
name|container5Exists
operator|&&
name|container6Exists
argument_list|)
expr_stmt|;
comment|// New SchedulerApplicationAttempt also has the containers info.
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId2
argument_list|,
name|RMContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// record the scheduler attempt for testing.
name|SchedulerApplicationAttempt
name|schedulerNewAttempt
init|=
operator|(
operator|(
name|CapacityScheduler
operator|)
name|rm1
operator|.
name|getResourceScheduler
argument_list|()
operator|)
operator|.
name|getCurrentAttemptForContainer
argument_list|(
name|containerId2
argument_list|)
decl_stmt|;
comment|// finish this application
name|MockRM
operator|.
name|finishAMAndVerifyAppState
argument_list|(
name|app1
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|,
name|am2
argument_list|)
expr_stmt|;
comment|// the 2nd attempt released the 1st attempt's running container, when the
comment|// 2nd attempt finishes.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|schedulerNewAttempt
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|contains
argument_list|(
name|containerId2
argument_list|)
argument_list|)
expr_stmt|;
comment|// all 4 normal containers finished.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|newAttempt
operator|.
name|getJustFinishedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNMTokensRebindOnAMRestart ()
specifier|public
name|void
name|testNMTokensRebindOnAMRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|MockRM
name|rm1
init|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|RMApp
name|app1
init|=
name|rm1
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|"myname"
argument_list|,
literal|"myuser"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|"default"
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|"MAPREDUCE"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|MockNM
name|nm1
init|=
operator|new
name|MockNM
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|8000
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
name|MockNM
name|nm2
init|=
operator|new
name|MockNM
argument_list|(
literal|"127.1.1.1:4321"
argument_list|,
literal|8000
argument_list|,
name|rm1
operator|.
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm2
operator|.
name|registerNode
argument_list|()
expr_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|int
name|NUM_CONTAINERS
init|=
literal|1
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|containers
init|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
decl_stmt|;
comment|// nmTokens keeps track of all the nmTokens issued in the allocate call.
name|List
argument_list|<
name|NMToken
argument_list|>
name|expectedNMTokens
init|=
operator|new
name|ArrayList
argument_list|<
name|NMToken
argument_list|>
argument_list|()
decl_stmt|;
comment|// am1 allocate 1 container on nm1.
while|while
condition|(
literal|true
condition|)
block|{
name|AllocateResponse
name|response
init|=
name|am1
operator|.
name|allocate
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|2000
argument_list|,
name|NUM_CONTAINERS
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|containers
operator|.
name|addAll
argument_list|(
name|response
operator|.
name|getAllocatedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|expectedNMTokens
operator|.
name|addAll
argument_list|(
name|response
operator|.
name|getNMTokens
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|containers
operator|.
name|size
argument_list|()
operator|==
name|NUM_CONTAINERS
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for container to be allocated."
argument_list|)
expr_stmt|;
block|}
comment|// launch the container
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId2
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|containerId2
argument_list|,
name|RMContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// fail am1
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am1
operator|.
name|getApplicationAttemptId
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
name|FAILED
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
comment|// restart the am
name|MockAM
name|am2
init|=
name|MockRM
operator|.
name|launchAM
argument_list|(
name|app1
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|RegisterApplicationMasterResponse
name|registerResponse
init|=
name|am2
operator|.
name|registerAppAttempt
argument_list|()
decl_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// check am2 get the nm token from am1.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedNMTokens
argument_list|,
name|registerResponse
operator|.
name|getNMTokensFromPreviousAttempts
argument_list|()
argument_list|)
expr_stmt|;
comment|// am2 allocate 1 container on nm2
name|containers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|AllocateResponse
name|allocateResponse
init|=
name|am2
operator|.
name|allocate
argument_list|(
literal|"127.1.1.1"
argument_list|,
literal|4000
argument_list|,
name|NUM_CONTAINERS
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|nm2
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|containers
operator|.
name|addAll
argument_list|(
name|allocateResponse
operator|.
name|getAllocatedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|expectedNMTokens
operator|.
name|addAll
argument_list|(
name|allocateResponse
operator|.
name|getNMTokens
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|containers
operator|.
name|size
argument_list|()
operator|==
name|NUM_CONTAINERS
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Waiting for container to be allocated."
argument_list|)
expr_stmt|;
block|}
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|ContainerId
name|am2ContainerId2
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|nm1
argument_list|,
name|am2ContainerId2
argument_list|,
name|RMContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// fail am2.
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|am2
operator|.
name|getApplicationAttemptId
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
name|FAILED
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
comment|// restart am
name|MockAM
name|am3
init|=
name|MockRM
operator|.
name|launchAM
argument_list|(
name|app1
argument_list|,
name|rm1
argument_list|,
name|nm1
argument_list|)
decl_stmt|;
name|registerResponse
operator|=
name|am3
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// check am3 get the NM token from both am1 and am2;
name|List
argument_list|<
name|NMToken
argument_list|>
name|transferredTokens
init|=
name|registerResponse
operator|.
name|getNMTokensFromPreviousAttempts
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|transferredTokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|transferredTokens
operator|.
name|containsAll
argument_list|(
name|expectedNMTokens
argument_list|)
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

