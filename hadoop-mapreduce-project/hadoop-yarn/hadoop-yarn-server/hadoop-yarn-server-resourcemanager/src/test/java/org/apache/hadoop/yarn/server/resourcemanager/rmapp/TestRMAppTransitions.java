begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
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
name|rmapp
package|;
end_package

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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|MockApps
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
name|ApplicationSubmissionContext
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
name|event
operator|.
name|AsyncDispatcher
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
name|event
operator|.
name|Dispatcher
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
name|event
operator|.
name|EventHandler
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
name|security
operator|.
name|ApplicationTokenSecretManager
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
name|ApplicationMasterService
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
name|RMContextImpl
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
name|MemStore
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
name|ApplicationsStore
operator|.
name|ApplicationStore
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
name|AMLivelinessMonitor
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
name|RMAppAttemptEvent
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
name|RMAppAttemptEventType
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
name|ContainerAllocationExpirer
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
name|YarnScheduler
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
DECL|class|TestRMAppTransitions
specifier|public
class|class
name|TestRMAppTransitions
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestRMAppTransitions
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|maxRetries
specifier|private
specifier|static
name|int
name|maxRetries
init|=
literal|4
decl_stmt|;
DECL|field|appId
specifier|private
specifier|static
name|int
name|appId
init|=
literal|1
decl_stmt|;
comment|// ignore all the RM application attempt events
DECL|class|TestApplicationAttemptEventDispatcher
specifier|private
specifier|static
specifier|final
class|class
name|TestApplicationAttemptEventDispatcher
implements|implements
name|EventHandler
argument_list|<
name|RMAppAttemptEvent
argument_list|>
block|{
DECL|method|TestApplicationAttemptEventDispatcher ()
specifier|public
name|TestApplicationAttemptEventDispatcher
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|handle (RMAppAttemptEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMAppAttemptEvent
name|event
parameter_list|)
block|{     }
block|}
comment|// handle all the RM application events - same as in ResourceManager.java
DECL|class|TestApplicationEventDispatcher
specifier|private
specifier|static
specifier|final
class|class
name|TestApplicationEventDispatcher
implements|implements
name|EventHandler
argument_list|<
name|RMAppEvent
argument_list|>
block|{
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|TestApplicationEventDispatcher (RMContext rmContext)
specifier|public
name|TestApplicationEventDispatcher
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (RMAppEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMAppEvent
name|event
parameter_list|)
block|{
name|ApplicationId
name|appID
init|=
name|event
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|RMApp
name|rmApp
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|rmApp
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in handling event type "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" for application "
operator|+
name|appID
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Dispatcher
name|rmDispatcher
init|=
operator|new
name|AsyncDispatcher
argument_list|()
decl_stmt|;
name|ContainerAllocationExpirer
name|containerAllocationExpirer
init|=
name|mock
argument_list|(
name|ContainerAllocationExpirer
operator|.
name|class
argument_list|)
decl_stmt|;
name|AMLivelinessMonitor
name|amLivelinessMonitor
init|=
name|mock
argument_list|(
name|AMLivelinessMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|rmContext
operator|=
operator|new
name|RMContextImpl
argument_list|(
operator|new
name|MemStore
argument_list|()
argument_list|,
name|rmDispatcher
argument_list|,
name|containerAllocationExpirer
argument_list|,
name|amLivelinessMonitor
argument_list|)
expr_stmt|;
name|rmDispatcher
operator|.
name|register
argument_list|(
name|RMAppAttemptEventType
operator|.
name|class
argument_list|,
operator|new
name|TestApplicationAttemptEventDispatcher
argument_list|()
argument_list|)
expr_stmt|;
name|rmDispatcher
operator|.
name|register
argument_list|(
name|RMAppEventType
operator|.
name|class
argument_list|,
operator|new
name|TestApplicationEventDispatcher
argument_list|(
name|rmContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createNewTestApp ()
specifier|protected
name|RMApp
name|createNewTestApp
parameter_list|()
block|{
name|ApplicationId
name|applicationId
init|=
name|MockApps
operator|.
name|newAppID
argument_list|(
name|appId
operator|++
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|MockApps
operator|.
name|newUserName
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|MockApps
operator|.
name|newAppName
argument_list|()
decl_stmt|;
name|String
name|queue
init|=
name|MockApps
operator|.
name|newQueue
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
comment|// ensure max retries set to known value
name|conf
operator|.
name|setInt
argument_list|(
literal|"yarn.server.resourcemanager.application.max.retries"
argument_list|,
name|maxRetries
argument_list|)
expr_stmt|;
name|ApplicationSubmissionContext
name|submissionContext
init|=
literal|null
decl_stmt|;
name|String
name|clientTokenStr
init|=
literal|"bogusstring"
decl_stmt|;
name|ApplicationStore
name|appStore
init|=
name|mock
argument_list|(
name|ApplicationStore
operator|.
name|class
argument_list|)
decl_stmt|;
name|YarnScheduler
name|scheduler
init|=
name|mock
argument_list|(
name|YarnScheduler
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationMasterService
name|masterService
init|=
operator|new
name|ApplicationMasterService
argument_list|(
name|rmContext
argument_list|,
operator|new
name|ApplicationTokenSecretManager
argument_list|()
argument_list|,
name|scheduler
argument_list|)
decl_stmt|;
name|RMApp
name|application
init|=
operator|new
name|RMAppImpl
argument_list|(
name|applicationId
argument_list|,
name|rmContext
argument_list|,
name|conf
argument_list|,
name|name
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
name|submissionContext
argument_list|,
name|clientTokenStr
argument_list|,
name|appStore
argument_list|,
name|rmContext
operator|.
name|getAMLivelinessMonitor
argument_list|()
argument_list|,
name|scheduler
argument_list|,
name|masterService
argument_list|)
decl_stmt|;
name|testAppStartState
argument_list|(
name|applicationId
argument_list|,
name|user
argument_list|,
name|name
argument_list|,
name|queue
argument_list|,
name|application
argument_list|)
expr_stmt|;
return|return
name|application
return|;
block|}
comment|// Test expected newly created app state
DECL|method|testAppStartState (ApplicationId applicationId, String user, String name, String queue, RMApp application)
specifier|private
specifier|static
name|void
name|testAppStartState
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|queue
parameter_list|,
name|RMApp
name|application
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"application start time is not greater then 0"
argument_list|,
name|application
operator|.
name|getStartTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"application start time is before currentTime"
argument_list|,
name|application
operator|.
name|getStartTime
argument_list|()
operator|<=
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application user is not correct"
argument_list|,
name|user
argument_list|,
name|application
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application id is not correct"
argument_list|,
name|applicationId
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application progress is not correct"
argument_list|,
operator|(
name|float
operator|)
literal|0.0
argument_list|,
name|application
operator|.
name|getProgress
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application queue is not correct"
argument_list|,
name|queue
argument_list|,
name|application
operator|.
name|getQueue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application name is not correct"
argument_list|,
name|name
argument_list|,
name|application
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application finish time is not 0 and should be"
argument_list|,
literal|0
argument_list|,
name|application
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application tracking url is not correct"
argument_list|,
literal|null
argument_list|,
name|application
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuilder
name|diag
init|=
name|application
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application diagnostics is not correct"
argument_list|,
literal|0
argument_list|,
name|diag
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test to make sure times are set when app finishes
DECL|method|assertStartTimeSet (RMApp application)
specifier|private
specifier|static
name|void
name|assertStartTimeSet
parameter_list|(
name|RMApp
name|application
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"application start time is not greater then 0"
argument_list|,
name|application
operator|.
name|getStartTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"application start time is before currentTime"
argument_list|,
name|application
operator|.
name|getStartTime
argument_list|()
operator|<=
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAppState (RMAppState state, RMApp application)
specifier|private
specifier|static
name|void
name|assertAppState
parameter_list|(
name|RMAppState
name|state
parameter_list|,
name|RMApp
name|application
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application state should have been"
operator|+
name|state
argument_list|,
name|state
argument_list|,
name|application
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test to make sure times are set when app finishes
DECL|method|assertTimesAtFinish (RMApp application)
specifier|private
specifier|static
name|void
name|assertTimesAtFinish
parameter_list|(
name|RMApp
name|application
parameter_list|)
block|{
name|assertStartTimeSet
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"application finish time is not greater then 0"
argument_list|,
operator|(
name|application
operator|.
name|getFinishTime
argument_list|()
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"application finish time is not>= then start time"
argument_list|,
operator|(
name|application
operator|.
name|getFinishTime
argument_list|()
operator|>=
name|application
operator|.
name|getStartTime
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertKilled (RMApp application)
specifier|private
specifier|static
name|void
name|assertKilled
parameter_list|(
name|RMApp
name|application
parameter_list|)
block|{
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|application
argument_list|)
expr_stmt|;
name|StringBuilder
name|diag
init|=
name|application
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application diagnostics is not correct"
argument_list|,
literal|"Application killed by user."
argument_list|,
name|diag
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertFailed (RMApp application, String regex)
specifier|private
specifier|static
name|void
name|assertFailed
parameter_list|(
name|RMApp
name|application
parameter_list|,
name|String
name|regex
parameter_list|)
block|{
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|FAILED
argument_list|,
name|application
argument_list|)
expr_stmt|;
name|StringBuilder
name|diag
init|=
name|application
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"application diagnostics is not correct"
argument_list|,
name|diag
operator|.
name|toString
argument_list|()
operator|.
name|matches
argument_list|(
name|regex
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateAppSubmitted ()
specifier|protected
name|RMApp
name|testCreateAppSubmitted
parameter_list|()
throws|throws
name|IOException
block|{
name|RMApp
name|application
init|=
name|createNewTestApp
argument_list|()
decl_stmt|;
comment|// NEW => SUBMITTED event RMAppEventType.START
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|START
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertStartTimeSet
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|application
argument_list|)
expr_stmt|;
return|return
name|application
return|;
block|}
DECL|method|testCreateAppAccepted ()
specifier|protected
name|RMApp
name|testCreateAppAccepted
parameter_list|()
throws|throws
name|IOException
block|{
name|RMApp
name|application
init|=
name|testCreateAppSubmitted
argument_list|()
decl_stmt|;
comment|// SUBMITTED => ACCEPTED event RMAppEventType.APP_ACCEPTED
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|APP_ACCEPTED
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertStartTimeSet
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|ACCEPTED
argument_list|,
name|application
argument_list|)
expr_stmt|;
return|return
name|application
return|;
block|}
DECL|method|testCreateAppRunning ()
specifier|protected
name|RMApp
name|testCreateAppRunning
parameter_list|()
throws|throws
name|IOException
block|{
name|RMApp
name|application
init|=
name|testCreateAppAccepted
argument_list|()
decl_stmt|;
comment|// ACCEPTED => RUNNING event RMAppEventType.ATTEMPT_REGISTERED
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_REGISTERED
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertStartTimeSet
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|RUNNING
argument_list|,
name|application
argument_list|)
expr_stmt|;
return|return
name|application
return|;
block|}
DECL|method|testCreateAppFinished ()
specifier|protected
name|RMApp
name|testCreateAppFinished
parameter_list|()
throws|throws
name|IOException
block|{
name|RMApp
name|application
init|=
name|testCreateAppRunning
argument_list|()
decl_stmt|;
comment|// RUNNING => FINISHED event RMAppEventType.ATTEMPT_FINISHED
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FINISHED
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|application
argument_list|)
expr_stmt|;
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
return|return
name|application
return|;
block|}
annotation|@
name|Test
DECL|method|testAppSuccessPath ()
specifier|public
name|void
name|testAppSuccessPath
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppSuccessPath ---"
argument_list|)
expr_stmt|;
name|testCreateAppFinished
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppNewKill ()
specifier|public
name|void
name|testAppNewKill
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppNewKill ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|createNewTestApp
argument_list|()
decl_stmt|;
comment|// NEW => KILLED event RMAppEventType.KILL
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertKilled
argument_list|(
name|application
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppNewReject ()
specifier|public
name|void
name|testAppNewReject
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppNewReject ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|createNewTestApp
argument_list|()
decl_stmt|;
comment|// NEW => FAILED event RMAppEventType.APP_REJECTED
name|String
name|rejectedText
init|=
literal|"Test Application Rejected"
decl_stmt|;
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppRejectedEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|rejectedText
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertFailed
argument_list|(
name|application
argument_list|,
name|rejectedText
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppSubmittedRejected ()
specifier|public
name|void
name|testAppSubmittedRejected
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppSubmittedRejected ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|testCreateAppSubmitted
argument_list|()
decl_stmt|;
comment|// SUBMITTED => FAILED event RMAppEventType.APP_REJECTED
name|String
name|rejectedText
init|=
literal|"app rejected"
decl_stmt|;
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppRejectedEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|rejectedText
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertFailed
argument_list|(
name|application
argument_list|,
name|rejectedText
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppSubmittedKill ()
specifier|public
name|void
name|testAppSubmittedKill
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppSubmittedKill---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|testCreateAppAccepted
argument_list|()
decl_stmt|;
comment|// SUBMITTED => KILLED event RMAppEventType.KILL
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertKilled
argument_list|(
name|application
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppAcceptedFailed ()
specifier|public
name|void
name|testAppAcceptedFailed
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppAcceptedFailed ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|testCreateAppAccepted
argument_list|()
decl_stmt|;
comment|// ACCEPTED => ACCEPTED event RMAppEventType.RMAppEventType.ATTEMPT_FAILED
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|maxRetries
condition|;
name|i
operator|++
control|)
block|{
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FAILED
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|application
argument_list|)
expr_stmt|;
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|APP_ACCEPTED
argument_list|)
expr_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|ACCEPTED
argument_list|,
name|application
argument_list|)
expr_stmt|;
block|}
comment|// ACCEPTED => FAILED event RMAppEventType.RMAppEventType.ATTEMPT_FAILED after max retries
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FAILED
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertFailed
argument_list|(
name|application
argument_list|,
literal|".*Failing the application.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppAcceptedKill ()
specifier|public
name|void
name|testAppAcceptedKill
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppAcceptedKill ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|testCreateAppAccepted
argument_list|()
decl_stmt|;
comment|// ACCEPTED => KILLED event RMAppEventType.KILL
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertKilled
argument_list|(
name|application
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppRunningKill ()
specifier|public
name|void
name|testAppRunningKill
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppRunningKill ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|testCreateAppRunning
argument_list|()
decl_stmt|;
comment|// RUNNING => KILLED event RMAppEventType.KILL
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertKilled
argument_list|(
name|application
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppRunningFailed ()
specifier|public
name|void
name|testAppRunningFailed
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppRunningFailed ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|testCreateAppRunning
argument_list|()
decl_stmt|;
name|RMAppAttempt
name|appAttempt
init|=
name|application
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
name|int
name|expectedAttemptId
init|=
literal|1
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedAttemptId
argument_list|,
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
comment|// RUNNING => FAILED/RESTARTING event RMAppEventType.ATTEMPT_FAILED
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|maxRetries
condition|;
name|i
operator|++
control|)
block|{
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FAILED
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|SUBMITTED
argument_list|,
name|application
argument_list|)
expr_stmt|;
name|appAttempt
operator|=
name|application
operator|.
name|getCurrentAppAttempt
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|++
name|expectedAttemptId
argument_list|,
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|APP_ACCEPTED
argument_list|)
expr_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|ACCEPTED
argument_list|,
name|application
argument_list|)
expr_stmt|;
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_REGISTERED
argument_list|)
expr_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|RUNNING
argument_list|,
name|application
argument_list|)
expr_stmt|;
block|}
comment|// RUNNING => FAILED/RESTARTING event RMAppEventType.ATTEMPT_FAILED after max retries
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FAILED
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertFailed
argument_list|(
name|application
argument_list|,
literal|".*Failing the application.*"
argument_list|)
expr_stmt|;
comment|// FAILED => FAILED event RMAppEventType.KILL
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
expr_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertFailed
argument_list|(
name|application
argument_list|,
literal|".*Failing the application.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppFinishedFinished ()
specifier|public
name|void
name|testAppFinishedFinished
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppFinishedFinished ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|testCreateAppFinished
argument_list|()
decl_stmt|;
comment|// FINISHED => FINISHED event RMAppEventType.KILL
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|application
argument_list|)
expr_stmt|;
name|StringBuilder
name|diag
init|=
name|application
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"application diagnostics is not correct"
argument_list|,
literal|""
argument_list|,
name|diag
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppKilledKilled ()
specifier|public
name|void
name|testAppKilledKilled
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testAppKilledKilled ---"
argument_list|)
expr_stmt|;
name|RMApp
name|application
init|=
name|testCreateAppRunning
argument_list|()
decl_stmt|;
comment|// RUNNING => KILLED event RMAppEventType.KILL
name|RMAppEvent
name|event
init|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
decl_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|application
argument_list|)
expr_stmt|;
comment|// KILLED => KILLED event RMAppEventType.ATTEMPT_FINISHED
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FINISHED
argument_list|)
expr_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|application
argument_list|)
expr_stmt|;
comment|// KILLED => KILLED event RMAppEventType.ATTEMPT_FAILED
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_FAILED
argument_list|)
expr_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|application
argument_list|)
expr_stmt|;
comment|// KILLED => KILLED event RMAppEventType.ATTEMPT_KILLED
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|ATTEMPT_KILLED
argument_list|)
expr_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|application
argument_list|)
expr_stmt|;
comment|// KILLED => KILLED event RMAppEventType.KILL
name|event
operator|=
operator|new
name|RMAppEvent
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
expr_stmt|;
name|application
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|assertTimesAtFinish
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|assertAppState
argument_list|(
name|RMAppState
operator|.
name|KILLED
argument_list|,
name|application
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

