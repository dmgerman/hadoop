begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ClientRMProtocol
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
name|KillApplicationRequest
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
name|GetNewApplicationRequest
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
name|GetNewApplicationResponse
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
name|SubmitApplicationRequest
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
name|api
operator|.
name|records
operator|.
name|ContainerLaunchContext
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
name|amlauncher
operator|.
name|AMLauncherEvent
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
name|amlauncher
operator|.
name|ApplicationMasterLauncher
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
name|StoreFactory
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
name|rmapp
operator|.
name|attempt
operator|.
name|event
operator|.
name|RMAppAttemptLaunchFailedEvent
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
name|Records
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

begin_class
DECL|class|MockRM
specifier|public
class|class
name|MockRM
extends|extends
name|ResourceManager
block|{
DECL|method|MockRM ()
specifier|public
name|MockRM
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|MockRM (Configuration conf)
specifier|public
name|MockRM
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|StoreFactory
operator|.
name|getStore
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
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
block|}
DECL|method|waitForState (ApplicationId appId, RMAppState finalState)
specifier|public
name|void
name|waitForState
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|RMAppState
name|finalState
parameter_list|)
throws|throws
name|Exception
block|{
name|RMApp
name|app
init|=
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"app shouldn't be null"
argument_list|,
name|app
argument_list|)
expr_stmt|;
name|int
name|timeoutSecs
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|finalState
operator|.
name|equals
argument_list|(
name|app
operator|.
name|getState
argument_list|()
argument_list|)
operator|&&
name|timeoutSecs
operator|++
operator|<
literal|20
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"App State is : "
operator|+
name|app
operator|.
name|getState
argument_list|()
operator|+
literal|" Waiting for state : "
operator|+
name|finalState
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"App State is : "
operator|+
name|app
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"App state is not correct (timedout)"
argument_list|,
name|finalState
argument_list|,
name|app
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// get new application id
DECL|method|getNewAppId ()
specifier|public
name|GetNewApplicationResponse
name|getNewAppId
parameter_list|()
throws|throws
name|Exception
block|{
name|ClientRMProtocol
name|client
init|=
name|getClientRMService
argument_list|()
decl_stmt|;
return|return
name|client
operator|.
name|getNewApplication
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|GetNewApplicationRequest
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
comment|//client
DECL|method|submitApp (int masterMemory)
specifier|public
name|RMApp
name|submitApp
parameter_list|(
name|int
name|masterMemory
parameter_list|)
throws|throws
name|Exception
block|{
name|ClientRMProtocol
name|client
init|=
name|getClientRMService
argument_list|()
decl_stmt|;
name|GetNewApplicationResponse
name|resp
init|=
name|client
operator|.
name|getNewApplication
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|GetNewApplicationRequest
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|resp
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|SubmitApplicationRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|SubmitApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|sub
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|sub
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|sub
operator|.
name|setApplicationName
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|sub
operator|.
name|setUser
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|ContainerLaunchContext
name|clc
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Resource
name|capability
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|capability
operator|.
name|setMemory
argument_list|(
name|masterMemory
argument_list|)
expr_stmt|;
name|clc
operator|.
name|setResource
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|sub
operator|.
name|setAMContainerSpec
argument_list|(
name|clc
argument_list|)
expr_stmt|;
name|req
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|client
operator|.
name|submitApplication
argument_list|(
name|req
argument_list|)
expr_stmt|;
comment|// make sure app is immediately available after submit
name|waitForState
argument_list|(
name|appId
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
return|return
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
return|;
block|}
DECL|method|registerNode (String nodeIdStr, int memory)
specifier|public
name|MockNM
name|registerNode
parameter_list|(
name|String
name|nodeIdStr
parameter_list|,
name|int
name|memory
parameter_list|)
throws|throws
name|Exception
block|{
name|MockNM
name|nm
init|=
operator|new
name|MockNM
argument_list|(
name|nodeIdStr
argument_list|,
name|memory
argument_list|,
name|getResourceTrackerService
argument_list|()
argument_list|)
decl_stmt|;
name|nm
operator|.
name|registerNode
argument_list|()
expr_stmt|;
return|return
name|nm
return|;
block|}
DECL|method|killApp (ApplicationId appId)
specifier|public
name|void
name|killApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|Exception
block|{
name|ClientRMProtocol
name|client
init|=
name|getClientRMService
argument_list|()
decl_stmt|;
name|KillApplicationRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|KillApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|req
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|client
operator|.
name|forceKillApplication
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
comment|//from AMLauncher
DECL|method|sendAMLaunched (ApplicationAttemptId appAttemptId)
specifier|public
name|MockAM
name|sendAMLaunched
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|Exception
block|{
name|MockAM
name|am
init|=
operator|new
name|MockAM
argument_list|(
name|getRMContext
argument_list|()
argument_list|,
name|masterService
argument_list|,
name|appAttemptId
argument_list|)
decl_stmt|;
name|am
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
name|getRMContext
argument_list|()
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptEvent
argument_list|(
name|appAttemptId
argument_list|,
name|RMAppAttemptEventType
operator|.
name|LAUNCHED
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|am
return|;
block|}
DECL|method|sendAMLaunchFailed (ApplicationAttemptId appAttemptId)
specifier|public
name|void
name|sendAMLaunchFailed
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|Exception
block|{
name|MockAM
name|am
init|=
operator|new
name|MockAM
argument_list|(
name|getRMContext
argument_list|()
argument_list|,
name|masterService
argument_list|,
name|appAttemptId
argument_list|)
decl_stmt|;
name|am
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
name|getRMContext
argument_list|()
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptLaunchFailedEvent
argument_list|(
name|appAttemptId
argument_list|,
literal|"Failed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createClientRMService ()
specifier|protected
name|ClientRMService
name|createClientRMService
parameter_list|()
block|{
return|return
operator|new
name|ClientRMService
argument_list|(
name|getRMContext
argument_list|()
argument_list|,
name|getResourceScheduler
argument_list|()
argument_list|,
name|rmAppManager
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|//override to not start rpc handler
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// don't do anything
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createResourceTrackerService ()
specifier|protected
name|ResourceTrackerService
name|createResourceTrackerService
parameter_list|()
block|{
return|return
operator|new
name|ResourceTrackerService
argument_list|(
name|getRMContext
argument_list|()
argument_list|,
name|nodesListManager
argument_list|,
name|this
operator|.
name|nmLivelinessMonitor
argument_list|,
name|this
operator|.
name|containerTokenSecretManager
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|//override to not start rpc handler
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// don't do anything
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createApplicationMasterService ()
specifier|protected
name|ApplicationMasterService
name|createApplicationMasterService
parameter_list|()
block|{
return|return
operator|new
name|ApplicationMasterService
argument_list|(
name|getRMContext
argument_list|()
argument_list|,
name|this
operator|.
name|appTokenSecretManager
argument_list|,
name|scheduler
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|//override to not start rpc handler
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// don't do anything
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createAMLauncher ()
specifier|protected
name|ApplicationMasterLauncher
name|createAMLauncher
parameter_list|()
block|{
return|return
operator|new
name|ApplicationMasterLauncher
argument_list|(
name|this
operator|.
name|appTokenSecretManager
argument_list|,
name|this
operator|.
name|clientToAMSecretManager
argument_list|,
name|getRMContext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|//override to not start rpc handler
block|}
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|AMLauncherEvent
name|appEvent
parameter_list|)
block|{
comment|//don't do anything
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// don't do anything
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createAdminService ()
specifier|protected
name|AdminService
name|createAdminService
parameter_list|()
block|{
return|return
operator|new
name|AdminService
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|scheduler
argument_list|,
name|getRMContext
argument_list|()
argument_list|,
name|this
operator|.
name|nodesListManager
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|//override to not start rpc handler
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// don't do anything
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|startWepApp ()
specifier|protected
name|void
name|startWepApp
parameter_list|()
block|{
comment|//override to disable webapp
block|}
block|}
end_class

end_unit

