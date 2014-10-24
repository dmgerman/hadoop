begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|Map
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationConstants
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
name|ContainerManagementProtocol
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
name|GetContainerStatusesRequest
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
name|GetContainerStatusesResponse
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
name|StartContainerRequest
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
name|StartContainersRequest
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
name|StartContainersResponse
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
name|StopContainersRequest
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
name|StopContainersResponse
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
name|api
operator|.
name|records
operator|.
name|SerializedException
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
name|Token
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
name|ApplicationAttemptNotFoundException
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
name|ApplicationMasterNotRegisteredException
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
name|ipc
operator|.
name|RPCUtil
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
name|ContainerTokenIdentifier
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
name|utils
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
DECL|class|TestApplicationMasterLauncher
specifier|public
class|class
name|TestApplicationMasterLauncher
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
name|TestApplicationMasterLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|MyContainerManagerImpl
specifier|private
specifier|static
specifier|final
class|class
name|MyContainerManagerImpl
implements|implements
name|ContainerManagementProtocol
block|{
DECL|field|launched
name|boolean
name|launched
init|=
literal|false
decl_stmt|;
DECL|field|cleanedup
name|boolean
name|cleanedup
init|=
literal|false
decl_stmt|;
DECL|field|attemptIdAtContainerManager
name|String
name|attemptIdAtContainerManager
init|=
literal|null
decl_stmt|;
DECL|field|containerIdAtContainerManager
name|String
name|containerIdAtContainerManager
init|=
literal|null
decl_stmt|;
DECL|field|nmHostAtContainerManager
name|String
name|nmHostAtContainerManager
init|=
literal|null
decl_stmt|;
DECL|field|submitTimeAtContainerManager
name|long
name|submitTimeAtContainerManager
decl_stmt|;
DECL|field|maxAppAttempts
name|int
name|maxAppAttempts
decl_stmt|;
annotation|@
name|Override
specifier|public
name|StartContainersResponse
DECL|method|startContainers (StartContainersRequest requests)
name|startContainers
parameter_list|(
name|StartContainersRequest
name|requests
parameter_list|)
throws|throws
name|YarnException
block|{
name|StartContainerRequest
name|request
init|=
name|requests
operator|.
name|getStartContainerRequests
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Container started by MyContainerManager: "
operator|+
name|request
argument_list|)
expr_stmt|;
name|launched
operator|=
literal|true
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
name|request
operator|.
name|getContainerLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
name|Token
name|containerToken
init|=
name|request
operator|.
name|getContainerToken
argument_list|()
decl_stmt|;
name|ContainerTokenIdentifier
name|tokenId
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tokenId
operator|=
name|BuilderUtils
operator|.
name|newContainerTokenIdentifier
argument_list|(
name|containerToken
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|ContainerId
name|containerId
init|=
name|tokenId
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|containerIdAtContainerManager
operator|=
name|containerId
operator|.
name|toString
argument_list|()
expr_stmt|;
name|attemptIdAtContainerManager
operator|=
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|nmHostAtContainerManager
operator|=
name|tokenId
operator|.
name|getNmHostAddress
argument_list|()
expr_stmt|;
name|submitTimeAtContainerManager
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|env
operator|.
name|get
argument_list|(
name|ApplicationConstants
operator|.
name|APP_SUBMIT_TIME_ENV
argument_list|)
argument_list|)
expr_stmt|;
name|maxAppAttempts
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|env
operator|.
name|get
argument_list|(
name|ApplicationConstants
operator|.
name|MAX_APP_ATTEMPTS_ENV
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|StartContainersResponse
operator|.
name|newInstance
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|SerializedException
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stopContainers (StopContainersRequest request)
specifier|public
name|StopContainersResponse
name|stopContainers
parameter_list|(
name|StopContainersRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Container cleaned up by MyContainerManager"
argument_list|)
expr_stmt|;
name|cleanedup
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerStatuses ( GetContainerStatusesRequest request)
specifier|public
name|GetContainerStatusesResponse
name|getContainerStatuses
parameter_list|(
name|GetContainerStatusesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAMLaunchAndCleanup ()
specifier|public
name|void
name|testAMLaunchAndCleanup
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
name|MyContainerManagerImpl
name|containerManager
init|=
operator|new
name|MyContainerManagerImpl
argument_list|()
decl_stmt|;
name|MockRMWithCustomAMLauncher
name|rm
init|=
operator|new
name|MockRMWithCustomAMLauncher
argument_list|(
name|containerManager
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|5120
argument_list|)
decl_stmt|;
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
comment|// kick the scheduling
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|containerManager
operator|.
name|launched
operator|==
literal|false
operator|&&
name|waitCount
operator|++
operator|<
literal|20
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for AM Launch to happen.."
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
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerManager
operator|.
name|launched
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
name|ApplicationAttemptId
name|appAttemptId
init|=
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttemptId
operator|.
name|toString
argument_list|()
argument_list|,
name|containerManager
operator|.
name|attemptIdAtContainerManager
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|app
operator|.
name|getSubmitTime
argument_list|()
argument_list|,
name|containerManager
operator|.
name|submitTimeAtContainerManager
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|app
operator|.
name|getRMAppAttempt
argument_list|(
name|appAttemptId
argument_list|)
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|containerManager
operator|.
name|containerIdAtContainerManager
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|containerManager
operator|.
name|nmHostAtContainerManager
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|,
name|containerManager
operator|.
name|maxAppAttempts
argument_list|)
expr_stmt|;
name|MockAM
name|am
init|=
operator|new
name|MockAM
argument_list|(
name|rm
operator|.
name|getRMContext
argument_list|()
argument_list|,
name|rm
operator|.
name|getApplicationMasterService
argument_list|()
argument_list|,
name|appAttemptId
argument_list|)
decl_stmt|;
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
name|am
operator|.
name|unregisterAppAttempt
argument_list|()
expr_stmt|;
comment|//complete the AM container to finish the app normally
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|attempt
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
name|am
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|waitCount
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|containerManager
operator|.
name|cleanedup
operator|==
literal|false
operator|&&
name|waitCount
operator|++
operator|<
literal|20
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for AM Cleanup to happen.."
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
name|Assert
operator|.
name|assertTrue
argument_list|(
name|containerManager
operator|.
name|cleanedup
argument_list|)
expr_stmt|;
name|am
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|100000
argument_list|)
DECL|method|testallocateBeforeAMRegistration ()
specifier|public
name|void
name|testallocateBeforeAMRegistration
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
name|boolean
name|thrown
init|=
literal|false
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
name|MockRM
name|rm
init|=
operator|new
name|MockRM
argument_list|()
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"h1:1234"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
comment|// kick the scheduling
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
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
name|MockAM
name|am
init|=
name|rm
operator|.
name|sendAMLaunched
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
comment|// request for containers
name|int
name|request
init|=
literal|2
decl_stmt|;
name|AllocateResponse
name|ar
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ar
operator|=
name|am
operator|.
name|allocate
argument_list|(
literal|"h1"
argument_list|,
literal|1000
argument_list|,
name|request
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ApplicationMasterNotRegisteredException
name|e
parameter_list|)
block|{     }
comment|// kick the scheduler
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|AllocateResponse
name|amrs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|amrs
operator|=
name|am
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
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ApplicationMasterNotRegisteredException
name|e
parameter_list|)
block|{     }
name|am
operator|.
name|registerAppAttempt
argument_list|()
expr_stmt|;
try|try
block|{
name|am
operator|.
name|registerAppAttempt
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Application Master is already registered : "
operator|+
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Simulate an AM that was disconnected and app attempt was removed
comment|// (responseMap does not contain attemptid)
name|am
operator|.
name|unregisterAppAttempt
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
name|attempt
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
name|am
operator|.
name|waitForState
argument_list|(
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
try|try
block|{
name|amrs
operator|=
name|am
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
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ApplicationAttemptNotFoundException
name|e
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

