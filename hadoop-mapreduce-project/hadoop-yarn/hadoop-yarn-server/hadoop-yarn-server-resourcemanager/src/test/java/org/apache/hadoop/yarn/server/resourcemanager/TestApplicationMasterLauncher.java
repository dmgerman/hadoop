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
name|ContainerManager
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
name|GetContainerStatusRequest
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
name|GetContainerStatusResponse
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
name|StartContainerResponse
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
name|StopContainerRequest
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
name|StopContainerResponse
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
name|exceptions
operator|.
name|YarnRemoteException
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
name|AMLauncher
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
name|AMLauncherEventType
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
name|util
operator|.
name|ConverterUtils
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
name|ContainerManager
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
DECL|field|nmPortAtContainerManager
name|int
name|nmPortAtContainerManager
decl_stmt|;
DECL|field|nmHttpPortAtContainerManager
name|int
name|nmHttpPortAtContainerManager
decl_stmt|;
DECL|field|submitTimeAtContainerManager
name|long
name|submitTimeAtContainerManager
decl_stmt|;
annotation|@
name|Override
specifier|public
name|StartContainerResponse
DECL|method|startContainer (StartContainerRequest request)
name|startContainer
parameter_list|(
name|StartContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
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
name|containerIdAtContainerManager
operator|=
name|env
operator|.
name|get
argument_list|(
name|ApplicationConstants
operator|.
name|AM_CONTAINER_ID_ENV
argument_list|)
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|ConverterUtils
operator|.
name|toContainerId
argument_list|(
name|containerIdAtContainerManager
argument_list|)
decl_stmt|;
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
name|env
operator|.
name|get
argument_list|(
name|ApplicationConstants
operator|.
name|NM_HOST_ENV
argument_list|)
expr_stmt|;
name|nmPortAtContainerManager
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
name|NM_PORT_ENV
argument_list|)
argument_list|)
expr_stmt|;
name|nmHttpPortAtContainerManager
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
name|NM_HTTP_PORT_ENV
argument_list|)
argument_list|)
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
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|stopContainer (StopContainerRequest request)
specifier|public
name|StopContainerResponse
name|stopContainer
parameter_list|(
name|StopContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
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
DECL|method|getContainerStatus ( GetContainerStatusRequest request)
specifier|public
name|GetContainerStatusResponse
name|getContainerStatus
parameter_list|(
name|GetContainerStatusRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|MockRMWithCustomAMLauncher
specifier|static
class|class
name|MockRMWithCustomAMLauncher
extends|extends
name|MockRM
block|{
DECL|field|containerManager
specifier|private
specifier|final
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|method|MockRMWithCustomAMLauncher (ContainerManager containerManager)
specifier|public
name|MockRMWithCustomAMLauncher
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|containerManager
argument_list|)
expr_stmt|;
block|}
DECL|method|MockRMWithCustomAMLauncher (Configuration conf, ContainerManager containerManager)
specifier|public
name|MockRMWithCustomAMLauncher
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ContainerManager
name|containerManager
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
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
name|super
operator|.
name|appTokenSecretManager
argument_list|,
name|super
operator|.
name|clientToAMSecretManager
argument_list|,
name|getRMContext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Runnable
name|createRunnableLauncher
parameter_list|(
name|RMAppAttempt
name|application
parameter_list|,
name|AMLauncherEventType
name|event
parameter_list|)
block|{
return|return
operator|new
name|AMLauncher
argument_list|(
name|context
argument_list|,
name|application
argument_list|,
name|event
argument_list|,
name|applicationTokenSecretManager
argument_list|,
name|clientToAMSecretManager
argument_list|,
name|getConfig
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ContainerManager
name|getContainerMgrProxy
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
return|return
name|containerManager
return|;
block|}
block|}
return|;
block|}
block|}
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
literal|"h1:1234"
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
name|getSubmissionContext
argument_list|()
operator|.
name|getAMContainerSpec
argument_list|()
operator|.
name|getContainerId
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
name|getHost
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
name|nm1
operator|.
name|getNodeId
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|,
name|containerManager
operator|.
name|nmPortAtContainerManager
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|nm1
operator|.
name|getHttpPort
argument_list|()
argument_list|,
name|containerManager
operator|.
name|nmHttpPortAtContainerManager
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
block|}
end_class

end_unit

