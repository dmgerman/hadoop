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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
operator|.
name|HAServiceProtocol
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
name|security
operator|.
name|UserGroupInformation
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
name|scheduler
operator|.
name|ResourceScheduler
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
name|ResourceCalculator
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
DECL|class|TestRMHAForAsyncScheduler
specifier|public
class|class
name|TestRMHAForAsyncScheduler
extends|extends
name|RMHATestBase
block|{
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|confForRM1
operator|.
name|setClass
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|RESOURCE_CALCULATOR_CLASS
argument_list|,
name|DominantResourceCalculator
operator|.
name|class
argument_list|,
name|ResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|confForRM1
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|confForRM1
operator|.
name|setBoolean
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|SCHEDULE_ASYNCHRONOUSLY_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|confForRM2
operator|.
name|setClass
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|RESOURCE_CALCULATOR_CLASS
argument_list|,
name|DominantResourceCalculator
operator|.
name|class
argument_list|,
name|ResourceCalculator
operator|.
name|class
argument_list|)
expr_stmt|;
name|confForRM2
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|confForRM2
operator|.
name|setBoolean
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|SCHEDULE_ASYNCHRONOUSLY_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAsyncScheduleThreadStateAfterRMHATransit ()
specifier|public
name|void
name|testAsyncScheduleThreadStateAfterRMHATransit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start two RMs, and transit rm1 to active, rm2 to standby
name|startRMs
argument_list|()
expr_stmt|;
comment|// register NM
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|8192
argument_list|,
literal|8
argument_list|)
expr_stmt|;
comment|// submit app1 and check
name|RMApp
name|app1
init|=
name|submitAppAndCheckLaunched
argument_list|(
name|rm1
argument_list|)
decl_stmt|;
comment|// failover RM1 to RM2
name|explicitFailover
argument_list|()
expr_stmt|;
name|checkAsyncSchedulerThreads
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
comment|// register NM, kill app1
name|rm2
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|8192
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|rm2
operator|.
name|waitForState
argument_list|(
name|app1
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|LAUNCHED
argument_list|)
expr_stmt|;
name|rm2
operator|.
name|killApp
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// submit app3 and check
name|RMApp
name|app2
init|=
name|submitAppAndCheckLaunched
argument_list|(
name|rm2
argument_list|)
decl_stmt|;
comment|// failover RM2 to RM1
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
name|requestInfo
init|=
operator|new
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
argument_list|(
name|HAServiceProtocol
operator|.
name|RequestSource
operator|.
name|REQUEST_BY_USER
argument_list|)
decl_stmt|;
name|rm2
operator|.
name|adminService
operator|.
name|transitionToStandby
argument_list|(
name|requestInfo
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|adminService
operator|.
name|transitionToActive
argument_list|(
name|requestInfo
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm2
operator|.
name|getRMContext
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
operator|==
name|HAServiceProtocol
operator|.
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rm1
operator|.
name|getRMContext
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
operator|==
name|HAServiceProtocol
operator|.
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
comment|// check async schedule threads
name|checkAsyncSchedulerThreads
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
comment|// register NM, kill app2
name|rm1
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|8192
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|waitForState
argument_list|(
name|app2
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|LAUNCHED
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|killApp
argument_list|(
name|app2
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// submit app3 and check
name|submitAppAndCheckLaunched
argument_list|(
name|rm1
argument_list|)
expr_stmt|;
name|rm1
operator|.
name|stop
argument_list|()
expr_stmt|;
name|rm2
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|submitAppAndCheckLaunched (MockRM rm)
specifier|private
name|RMApp
name|submitAppAndCheckLaunched
parameter_list|(
name|MockRM
name|rm
parameter_list|)
throws|throws
name|Exception
block|{
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|200
argument_list|,
literal|""
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|"default"
argument_list|,
name|configuration
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|rm
operator|.
name|waitForState
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|waitForState
argument_list|(
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|LAUNCHED
argument_list|)
expr_stmt|;
return|return
name|app
return|;
block|}
comment|/**    * Make sure the state of async-scheduler threads is correct    * @param currentThread    */
DECL|method|checkAsyncSchedulerThreads (Thread currentThread)
specifier|private
name|void
name|checkAsyncSchedulerThreads
parameter_list|(
name|Thread
name|currentThread
parameter_list|)
block|{
comment|// Make sure AsyncScheduleThread is interrupted
name|ThreadGroup
name|threadGroup
init|=
name|currentThread
operator|.
name|getThreadGroup
argument_list|()
decl_stmt|;
while|while
condition|(
name|threadGroup
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|threadGroup
operator|=
name|threadGroup
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|threadGroup
operator|.
name|activeCount
argument_list|()
index|]
decl_stmt|;
name|threadGroup
operator|.
name|enumerate
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|int
name|numAsyncScheduleThread
init|=
literal|0
decl_stmt|;
name|int
name|numResourceCommitterService
init|=
literal|0
decl_stmt|;
name|Thread
name|asyncScheduleThread
init|=
literal|null
decl_stmt|;
name|Thread
name|resourceCommitterService
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|StackTraceElement
index|[]
name|stackTrace
init|=
name|thread
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
if|if
condition|(
name|stackTrace
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|String
name|stackBottom
init|=
name|stackTrace
index|[
name|stackTrace
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|stackBottom
operator|.
name|contains
argument_list|(
literal|"AsyncScheduleThread.run"
argument_list|)
condition|)
block|{
name|numAsyncScheduleThread
operator|++
expr_stmt|;
name|asyncScheduleThread
operator|=
name|thread
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stackBottom
operator|.
name|contains
argument_list|(
literal|"ResourceCommitterService.run"
argument_list|)
condition|)
block|{
name|numResourceCommitterService
operator|++
expr_stmt|;
name|resourceCommitterService
operator|=
name|thread
expr_stmt|;
block|}
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numResourceCommitterService
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numAsyncScheduleThread
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|asyncScheduleThread
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|resourceCommitterService
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

