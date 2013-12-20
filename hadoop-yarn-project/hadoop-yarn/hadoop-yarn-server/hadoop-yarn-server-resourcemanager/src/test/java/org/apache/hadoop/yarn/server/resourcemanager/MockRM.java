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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|io
operator|.
name|DataOutputBuffer
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
name|Credentials
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
name|api
operator|.
name|ApplicationClientProtocol
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
name|KillApplicationResponse
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
name|protocolrecords
operator|.
name|SubmitApplicationResponse
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
name|NodeId
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
name|NodeState
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
name|exceptions
operator|.
name|YarnException
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
name|RMStateStore
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
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|rmnode
operator|.
name|RMNodeEvent
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
name|rmnode
operator|.
name|RMNodeEventType
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
name|rmnode
operator|.
name|RMNodeImpl
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
name|security
operator|.
name|ClientToAMTokenSecretManagerInRM
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
name|security
operator|.
name|RMDelegationTokenSecretManager
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|YarnConfiguration
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
name|this
argument_list|(
name|conf
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|MockRM (Configuration conf, RMStateStore store)
specifier|public
name|MockRM
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMStateStore
name|store
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|init
argument_list|(
name|conf
operator|instanceof
name|YarnConfiguration
condition|?
name|conf
else|:
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|setRMStateStore
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
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
literal|40
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"App : "
operator|+
name|appId
operator|+
literal|" State is : "
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
literal|2000
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
DECL|method|waitForState (ApplicationAttemptId attemptId, RMAppAttemptState finalState)
specifier|public
name|void
name|waitForState
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|RMAppAttemptState
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
name|attemptId
operator|.
name|getApplicationId
argument_list|()
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
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getRMAppAttempt
argument_list|(
name|attemptId
argument_list|)
decl_stmt|;
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
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
argument_list|)
operator|&&
name|timeoutSecs
operator|++
operator|<
literal|40
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"AppAttempt : "
operator|+
name|attemptId
operator|+
literal|" State is : "
operator|+
name|attempt
operator|.
name|getAppAttemptState
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
literal|1000
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Attempt State is : "
operator|+
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Attempt state is not correct (timedout)"
argument_list|,
name|finalState
argument_list|,
name|attempt
operator|.
name|getAppAttemptState
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
name|ApplicationClientProtocol
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
return|return
name|submitApp
argument_list|(
name|masterMemory
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
argument_list|)
return|;
block|}
comment|// client
DECL|method|submitApp (int masterMemory, String name, String user)
specifier|public
name|RMApp
name|submitApp
parameter_list|(
name|int
name|masterMemory
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|submitApp
argument_list|(
name|masterMemory
argument_list|,
name|name
argument_list|,
name|user
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|getConfig
argument_list|()
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
argument_list|)
return|;
block|}
DECL|method|submitApp (int masterMemory, String name, String user, Map<ApplicationAccessType, String> acls)
specifier|public
name|RMApp
name|submitApp
parameter_list|(
name|int
name|masterMemory
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|user
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|submitApp
argument_list|(
name|masterMemory
argument_list|,
name|name
argument_list|,
name|user
argument_list|,
name|acls
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|getConfig
argument_list|()
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
argument_list|)
return|;
block|}
DECL|method|submitApp (int masterMemory, String name, String user, Map<ApplicationAccessType, String> acls, String queue)
specifier|public
name|RMApp
name|submitApp
parameter_list|(
name|int
name|masterMemory
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|user
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|,
name|String
name|queue
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|submitApp
argument_list|(
name|masterMemory
argument_list|,
name|name
argument_list|,
name|user
argument_list|,
name|acls
argument_list|,
literal|false
argument_list|,
name|queue
argument_list|,
name|super
operator|.
name|getConfig
argument_list|()
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
argument_list|)
return|;
block|}
DECL|method|submitApp (int masterMemory, String name, String user, Map<ApplicationAccessType, String> acls, boolean unmanaged, String queue, int maxAppAttempts, Credentials ts)
specifier|public
name|RMApp
name|submitApp
parameter_list|(
name|int
name|masterMemory
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|user
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|,
name|boolean
name|unmanaged
parameter_list|,
name|String
name|queue
parameter_list|,
name|int
name|maxAppAttempts
parameter_list|,
name|Credentials
name|ts
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|submitApp
argument_list|(
name|masterMemory
argument_list|,
name|name
argument_list|,
name|user
argument_list|,
name|acls
argument_list|,
name|unmanaged
argument_list|,
name|queue
argument_list|,
name|maxAppAttempts
argument_list|,
name|ts
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|submitApp (int masterMemory, String name, String user, Map<ApplicationAccessType, String> acls, boolean unmanaged, String queue, int maxAppAttempts, Credentials ts, String appType)
specifier|public
name|RMApp
name|submitApp
parameter_list|(
name|int
name|masterMemory
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|user
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|,
name|boolean
name|unmanaged
parameter_list|,
name|String
name|queue
parameter_list|,
name|int
name|maxAppAttempts
parameter_list|,
name|Credentials
name|ts
parameter_list|,
name|String
name|appType
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|submitApp
argument_list|(
name|masterMemory
argument_list|,
name|name
argument_list|,
name|user
argument_list|,
name|acls
argument_list|,
name|unmanaged
argument_list|,
name|queue
argument_list|,
name|maxAppAttempts
argument_list|,
name|ts
argument_list|,
name|appType
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|submitApp (int masterMemory, String name, String user, Map<ApplicationAccessType, String> acls, boolean unmanaged, String queue, int maxAppAttempts, Credentials ts, String appType, boolean waitForAccepted)
specifier|public
name|RMApp
name|submitApp
parameter_list|(
name|int
name|masterMemory
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|user
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
parameter_list|,
name|boolean
name|unmanaged
parameter_list|,
name|String
name|queue
parameter_list|,
name|int
name|maxAppAttempts
parameter_list|,
name|Credentials
name|ts
parameter_list|,
name|String
name|appType
parameter_list|,
name|boolean
name|waitForAccepted
parameter_list|)
throws|throws
name|Exception
block|{
name|ApplicationClientProtocol
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
name|name
argument_list|)
expr_stmt|;
name|sub
operator|.
name|setMaxAppAttempts
argument_list|(
name|maxAppAttempts
argument_list|)
expr_stmt|;
if|if
condition|(
name|unmanaged
condition|)
block|{
name|sub
operator|.
name|setUnmanagedAM
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queue
operator|!=
literal|null
condition|)
block|{
name|sub
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
name|sub
operator|.
name|setApplicationType
argument_list|(
name|appType
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
specifier|final
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
name|sub
operator|.
name|setResource
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|clc
operator|.
name|setApplicationACLs
argument_list|(
name|acls
argument_list|)
expr_stmt|;
if|if
condition|(
name|ts
operator|!=
literal|null
operator|&&
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|ts
operator|.
name|writeTokenStorageToStream
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|ByteBuffer
name|securityTokens
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|clc
operator|.
name|setTokens
argument_list|(
name|securityTokens
argument_list|)
expr_stmt|;
block|}
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
name|UserGroupInformation
name|fakeUser
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|user
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"someGroup"
block|}
argument_list|)
decl_stmt|;
name|PrivilegedAction
argument_list|<
name|SubmitApplicationResponse
argument_list|>
name|action
init|=
operator|new
name|PrivilegedAction
argument_list|<
name|SubmitApplicationResponse
argument_list|>
argument_list|()
block|{
name|ApplicationClientProtocol
name|client
decl_stmt|;
name|SubmitApplicationRequest
name|req
decl_stmt|;
annotation|@
name|Override
specifier|public
name|SubmitApplicationResponse
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|client
operator|.
name|submitApplication
argument_list|(
name|req
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|PrivilegedAction
argument_list|<
name|SubmitApplicationResponse
argument_list|>
name|setClientReq
parameter_list|(
name|ApplicationClientProtocol
name|client
parameter_list|,
name|SubmitApplicationRequest
name|req
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
operator|.
name|setClientReq
argument_list|(
name|client
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|fakeUser
operator|.
name|doAs
argument_list|(
name|action
argument_list|)
expr_stmt|;
comment|// make sure app is immediately available after submit
if|if
condition|(
name|waitForAccepted
condition|)
block|{
name|waitForState
argument_list|(
name|appId
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
block|}
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
DECL|method|registerNode (String nodeIdStr, int memory, int vCores)
specifier|public
name|MockNM
name|registerNode
parameter_list|(
name|String
name|nodeIdStr
parameter_list|,
name|int
name|memory
parameter_list|,
name|int
name|vCores
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
name|vCores
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
DECL|method|sendNodeStarted (MockNM nm)
specifier|public
name|void
name|sendNodeStarted
parameter_list|(
name|MockNM
name|nm
parameter_list|)
throws|throws
name|Exception
block|{
name|RMNodeImpl
name|node
init|=
operator|(
name|RMNodeImpl
operator|)
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|nm
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sendNodeLost (MockNM nm)
specifier|public
name|void
name|sendNodeLost
parameter_list|(
name|MockNM
name|nm
parameter_list|)
throws|throws
name|Exception
block|{
name|RMNodeImpl
name|node
init|=
operator|(
name|RMNodeImpl
operator|)
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|.
name|handle
argument_list|(
operator|new
name|RMNodeEvent
argument_list|(
name|nm
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|RMNodeEventType
operator|.
name|EXPIRE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|NMwaitForState (NodeId nodeid, NodeState finalState)
specifier|public
name|void
name|NMwaitForState
parameter_list|(
name|NodeId
name|nodeid
parameter_list|,
name|NodeState
name|finalState
parameter_list|)
throws|throws
name|Exception
block|{
name|RMNode
name|node
init|=
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeid
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"node shouldn't be null"
argument_list|,
name|node
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
name|node
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
literal|"Node State is : "
operator|+
name|node
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
literal|"Node State is : "
operator|+
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Node state is not correct (timedout)"
argument_list|,
name|finalState
argument_list|,
name|node
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|killApp (ApplicationId appId)
specifier|public
name|KillApplicationResponse
name|killApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|Exception
block|{
name|ApplicationClientProtocol
name|client
init|=
name|getClientRMService
argument_list|()
decl_stmt|;
name|KillApplicationRequest
name|req
init|=
name|KillApplicationRequest
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|)
decl_stmt|;
return|return
name|client
operator|.
name|forceKillApplication
argument_list|(
name|req
argument_list|)
return|;
block|}
comment|// from AMLauncher
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
argument_list|,
name|applicationACLsManager
argument_list|,
name|queueACLsManager
argument_list|,
name|rmDTSecretManager
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|serviceStart
parameter_list|()
block|{
comment|// override to not start rpc handler
block|}
annotation|@
name|Override
specifier|protected
name|void
name|serviceStop
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|containerTokenSecretManager
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
name|nmTokenSecretManager
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
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
name|containerTokenSecretManager
argument_list|,
name|nmTokenSecretManager
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|serviceStart
parameter_list|()
block|{
comment|// override to not start rpc handler
block|}
annotation|@
name|Override
specifier|protected
name|void
name|serviceStop
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
name|scheduler
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|serviceStart
parameter_list|()
block|{
comment|// override to not start rpc handler
block|}
annotation|@
name|Override
specifier|protected
name|void
name|serviceStop
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
name|getRMContext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|serviceStart
parameter_list|()
block|{
comment|// override to not start rpc handler
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
comment|// don't do anything
block|}
annotation|@
name|Override
specifier|protected
name|void
name|serviceStop
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
name|this
argument_list|,
name|getRMContext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|startServer
parameter_list|()
block|{
comment|// override to not start rpc handler
block|}
annotation|@
name|Override
specifier|protected
name|void
name|stopServer
parameter_list|()
block|{
comment|// don't do anything
block|}
block|}
return|;
block|}
DECL|method|getNodesListManager ()
specifier|public
name|NodesListManager
name|getNodesListManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodesListManager
return|;
block|}
DECL|method|getRMDTSecretManager ()
specifier|public
name|RMDelegationTokenSecretManager
name|getRMDTSecretManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmDTSecretManager
return|;
block|}
DECL|method|getClientToAMTokenSecretManager ()
specifier|public
name|ClientToAMTokenSecretManagerInRM
name|getClientToAMTokenSecretManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientToAMSecretManager
return|;
block|}
DECL|method|getRMAppManager ()
specifier|public
name|RMAppManager
name|getRMAppManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmAppManager
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
comment|// override to disable webapp
block|}
block|}
end_class

end_unit

