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
name|net
operator|.
name|InetSocketAddress
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|security
operator|.
name|token
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|AMRMProtocol
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
name|RegisterApplicationMasterRequest
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
name|ipc
operator|.
name|YarnRPC
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
name|TestApplicationMasterLauncher
operator|.
name|MockRMWithCustomAMLauncher
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
name|BuilderUtils
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
DECL|class|TestAMAuthorization
specifier|public
class|class
name|TestAMAuthorization
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
name|TestAMAuthorization
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|MyContainerManager
specifier|public
specifier|static
specifier|final
class|class
name|MyContainerManager
implements|implements
name|ContainerManager
block|{
DECL|field|amContainerEnv
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|amContainerEnv
decl_stmt|;
DECL|method|MyContainerManager ()
specifier|public
name|MyContainerManager
parameter_list|()
block|{     }
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
name|amContainerEnv
operator|=
name|request
operator|.
name|getContainerLaunchContext
argument_list|()
operator|.
name|getEnvironment
argument_list|()
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
comment|// TODO Auto-generated method stub
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
DECL|class|MockRMWithAMS
specifier|public
specifier|static
class|class
name|MockRMWithAMS
extends|extends
name|MockRMWithCustomAMLauncher
block|{
DECL|method|MockRMWithAMS (Configuration conf, ContainerManager containerManager)
specifier|public
name|MockRMWithAMS
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
argument_list|,
name|containerManager
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doSecureLogin ()
specifier|protected
name|void
name|doSecureLogin
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Skip the login.
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
name|scheduler
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAuthorizedAccess ()
specifier|public
name|void
name|testAuthorizedAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|MyContainerManager
name|containerManager
init|=
operator|new
name|MyContainerManager
argument_list|()
decl_stmt|;
specifier|final
name|MockRM
name|rm
init|=
operator|new
name|MockRMWithAMS
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
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
literal|"localhost:1234"
argument_list|,
literal|5120
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|acls
operator|.
name|put
argument_list|(
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|RMApp
name|app
init|=
name|rm
operator|.
name|submitApp
argument_list|(
literal|1024
argument_list|,
literal|"appname"
argument_list|,
literal|"appuser"
argument_list|,
name|acls
argument_list|)
decl_stmt|;
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
name|amContainerEnv
operator|==
literal|null
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
name|assertNotNull
argument_list|(
name|containerManager
operator|.
name|amContainerEnv
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
name|applicationAttemptId
init|=
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
name|waitForLaunchedState
argument_list|(
name|attempt
argument_list|)
expr_stmt|;
comment|// Create a client to the RM.
specifier|final
name|Configuration
name|conf
init|=
name|rm
operator|.
name|getConfig
argument_list|()
decl_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|applicationAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|tokenURLEncodedStr
init|=
name|containerManager
operator|.
name|amContainerEnv
operator|.
name|get
argument_list|(
name|ApplicationConstants
operator|.
name|APPLICATION_MASTER_TOKEN_ENV_NAME
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"AppMasterToken is "
operator|+
name|tokenURLEncodedStr
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|token
operator|.
name|decodeFromUrlString
argument_list|(
name|tokenURLEncodedStr
argument_list|)
expr_stmt|;
name|currentUser
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|AMRMProtocol
name|client
init|=
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|AMRMProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AMRMProtocol
name|run
parameter_list|()
block|{
return|return
operator|(
name|AMRMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|AMRMProtocol
operator|.
name|class
argument_list|,
name|rm
operator|.
name|getApplicationMasterService
argument_list|()
operator|.
name|getBindAddress
argument_list|()
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|RegisterApplicationMasterRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|RegisterApplicationMasterResponse
name|response
init|=
name|client
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Register response has bad ACLs"
argument_list|,
literal|"*"
argument_list|,
name|response
operator|.
name|getApplicationACLs
argument_list|()
operator|.
name|get
argument_list|(
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|)
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnauthorizedAccess ()
specifier|public
name|void
name|testUnauthorizedAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|MyContainerManager
name|containerManager
init|=
operator|new
name|MyContainerManager
argument_list|()
decl_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRMWithAMS
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
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
literal|"localhost:1234"
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
literal|1024
argument_list|)
decl_stmt|;
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
name|amContainerEnv
operator|==
literal|null
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
name|assertNotNull
argument_list|(
name|containerManager
operator|.
name|amContainerEnv
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
name|applicationAttemptId
init|=
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
name|waitForLaunchedState
argument_list|(
name|attempt
argument_list|)
expr_stmt|;
comment|// Create a client to the RM.
specifier|final
name|Configuration
name|conf
init|=
name|rm
operator|.
name|getConfig
argument_list|()
decl_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|serviceAddr
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_PORT
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|applicationAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|tokenURLEncodedStr
init|=
name|containerManager
operator|.
name|amContainerEnv
operator|.
name|get
argument_list|(
name|ApplicationConstants
operator|.
name|APPLICATION_MASTER_TOKEN_ENV_NAME
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"AppMasterToken is "
operator|+
name|tokenURLEncodedStr
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|TokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|token
operator|.
name|decodeFromUrlString
argument_list|(
name|tokenURLEncodedStr
argument_list|)
expr_stmt|;
name|currentUser
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|AMRMProtocol
name|client
init|=
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|AMRMProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AMRMProtocol
name|run
parameter_list|()
block|{
return|return
operator|(
name|AMRMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|AMRMProtocol
operator|.
name|class
argument_list|,
name|serviceAddr
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|RegisterApplicationMasterRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|otherAppAttemptId
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|applicationAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|,
literal|42
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationAttemptId
argument_list|(
name|otherAppAttemptId
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should fail with authorization error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unauthorized request from ApplicationMaster. "
operator|+
literal|"Expected ApplicationAttemptID: "
operator|+
name|applicationAttemptId
operator|.
name|toString
argument_list|()
operator|+
literal|" Found: "
operator|+
name|otherAppAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|waitForLaunchedState (RMAppAttempt attempt)
specifier|private
name|void
name|waitForLaunchedState
parameter_list|(
name|RMAppAttempt
name|attempt
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
operator|!=
name|RMAppAttemptState
operator|.
name|LAUNCHED
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
literal|"Waiting for AppAttempt to reach LAUNCHED state. "
operator|+
literal|"Current state is "
operator|+
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
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
name|assertEquals
argument_list|(
name|attempt
operator|.
name|getAppAttemptState
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|LAUNCHED
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

