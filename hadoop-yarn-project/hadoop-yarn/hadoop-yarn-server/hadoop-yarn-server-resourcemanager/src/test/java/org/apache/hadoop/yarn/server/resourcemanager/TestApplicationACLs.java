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
name|PrivilegedExceptionAction
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
name|authorize
operator|.
name|AccessControlList
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
name|GetAllApplicationsRequest
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
name|GetApplicationReportRequest
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
name|ApplicationReport
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
name|ApplicationResourceUsageReport
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|recovery
operator|.
name|RMStateStoreFactory
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
name|service
operator|.
name|Service
operator|.
name|STATE
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|TestApplicationACLs
specifier|public
class|class
name|TestApplicationACLs
block|{
DECL|field|APP_OWNER
specifier|private
specifier|static
specifier|final
name|String
name|APP_OWNER
init|=
literal|"owner"
decl_stmt|;
DECL|field|FRIEND
specifier|private
specifier|static
specifier|final
name|String
name|FRIEND
init|=
literal|"friend"
decl_stmt|;
DECL|field|ENEMY
specifier|private
specifier|static
specifier|final
name|String
name|ENEMY
init|=
literal|"enemy"
decl_stmt|;
DECL|field|SUPER_USER
specifier|private
specifier|static
specifier|final
name|String
name|SUPER_USER
init|=
literal|"superUser"
decl_stmt|;
DECL|field|FRIENDLY_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|FRIENDLY_GROUP
init|=
literal|"friendly-group"
decl_stmt|;
DECL|field|SUPER_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|SUPER_GROUP
init|=
literal|"superGroup"
decl_stmt|;
DECL|field|UNAVAILABLE
specifier|private
specifier|static
specifier|final
name|String
name|UNAVAILABLE
init|=
literal|"N/A"
decl_stmt|;
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
name|TestApplicationACLs
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceManager
specifier|static
name|MockRM
name|resourceManager
decl_stmt|;
DECL|field|conf
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|rpc
specifier|final
specifier|static
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
DECL|field|rmAddress
specifier|final
specifier|static
name|InetSocketAddress
name|rmAddress
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_PORT
argument_list|)
decl_stmt|;
DECL|field|rmClient
specifier|private
specifier|static
name|ClientRMProtocol
name|rmClient
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|RMStateStore
name|store
init|=
name|RMStateStoreFactory
operator|.
name|getStore
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|AccessControlList
name|adminACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|adminACL
operator|.
name|addGroup
argument_list|(
name|SUPER_GROUP
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
name|adminACL
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|resourceManager
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
block|{
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
name|this
operator|.
name|scheduler
argument_list|,
name|this
operator|.
name|rmAppManager
argument_list|,
name|this
operator|.
name|applicationACLsManager
argument_list|,
literal|null
argument_list|)
return|;
block|}
empty_stmt|;
block|}
expr_stmt|;
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|ENEMY
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|FRIEND
argument_list|,
operator|new
name|String
index|[]
block|{
name|FRIENDLY_GROUP
block|}
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|SUPER_USER
argument_list|,
operator|new
name|String
index|[]
block|{
name|SUPER_GROUP
block|}
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|waitCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|resourceManager
operator|.
name|getServiceState
argument_list|()
operator|==
name|STATE
operator|.
name|INITED
operator|&&
name|waitCount
operator|++
operator|<
literal|60
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for RM to start..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resourceManager
operator|.
name|getServiceState
argument_list|()
operator|!=
name|STATE
operator|.
name|STARTED
condition|)
block|{
comment|// RM could have failed.
throw|throw
operator|new
name|IOException
argument_list|(
literal|"ResourceManager failed to start. Final state is "
operator|+
name|resourceManager
operator|.
name|getServiceState
argument_list|()
argument_list|)
throw|;
block|}
name|UserGroupInformation
name|owner
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|APP_OWNER
argument_list|)
decl_stmt|;
name|rmClient
operator|=
name|owner
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ClientRMProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClientRMProtocol
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|ClientRMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ClientRMProtocol
operator|.
name|class
argument_list|,
name|rmAddress
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|resourceManager
operator|!=
literal|null
condition|)
block|{
name|resourceManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testApplicationACLs ()
specifier|public
name|void
name|testApplicationACLs
parameter_list|()
throws|throws
name|Exception
block|{
name|verifyOwnerAccess
argument_list|()
expr_stmt|;
name|verifySuperUserAccess
argument_list|()
expr_stmt|;
name|verifyFriendAccess
argument_list|()
expr_stmt|;
name|verifyEnemyAccess
argument_list|()
expr_stmt|;
block|}
DECL|method|submitAppAndGetAppId (AccessControlList viewACL, AccessControlList modifyACL)
specifier|private
name|ApplicationId
name|submitAppAndGetAppId
parameter_list|(
name|AccessControlList
name|viewACL
parameter_list|,
name|AccessControlList
name|modifyACL
parameter_list|)
throws|throws
name|Exception
block|{
name|SubmitApplicationRequest
name|submitRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|SubmitApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContext
name|context
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationSubmissionContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|rmClient
operator|.
name|getNewApplication
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetNewApplicationRequest
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|context
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
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
argument_list|()
decl_stmt|;
name|acls
operator|.
name|put
argument_list|(
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|viewACL
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|acls
operator|.
name|put
argument_list|(
name|ApplicationAccessType
operator|.
name|MODIFY_APP
argument_list|,
name|modifyACL
operator|.
name|getAclString
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerLaunchContext
name|amContainer
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|amContainer
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|amContainer
operator|.
name|setApplicationACLs
argument_list|(
name|acls
argument_list|)
expr_stmt|;
name|context
operator|.
name|setAMContainerSpec
argument_list|(
name|amContainer
argument_list|)
expr_stmt|;
name|submitRequest
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rmClient
operator|.
name|submitApplication
argument_list|(
name|submitRequest
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|waitForState
argument_list|(
name|applicationId
argument_list|,
name|RMAppState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
return|return
name|applicationId
return|;
block|}
DECL|method|getRMClientForUser (String user)
specifier|private
name|ClientRMProtocol
name|getRMClientForUser
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|UserGroupInformation
name|userUGI
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|ClientRMProtocol
name|userClient
init|=
name|userUGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ClientRMProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClientRMProtocol
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|ClientRMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ClientRMProtocol
operator|.
name|class
argument_list|,
name|rmAddress
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|userClient
return|;
block|}
DECL|method|verifyOwnerAccess ()
specifier|private
name|void
name|verifyOwnerAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlList
name|viewACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|viewACL
operator|.
name|addGroup
argument_list|(
name|FRIENDLY_GROUP
argument_list|)
expr_stmt|;
name|AccessControlList
name|modifyACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|modifyACL
operator|.
name|addUser
argument_list|(
name|FRIEND
argument_list|)
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|submitAppAndGetAppId
argument_list|(
name|viewACL
argument_list|,
name|modifyACL
argument_list|)
decl_stmt|;
specifier|final
name|GetApplicationReportRequest
name|appReportRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|appReportRequest
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
specifier|final
name|KillApplicationRequest
name|finishAppRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|finishAppRequest
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
comment|// View as owner
name|rmClient
operator|.
name|getApplicationReport
argument_list|(
name|appReportRequest
argument_list|)
expr_stmt|;
comment|// List apps as owner
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"App view by owner should list the apps!!"
argument_list|,
literal|1
argument_list|,
name|rmClient
operator|.
name|getAllApplications
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetAllApplicationsRequest
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|getApplicationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Kill app as owner
name|rmClient
operator|.
name|forceKillApplication
argument_list|(
name|finishAppRequest
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|waitForState
argument_list|(
name|applicationId
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySuperUserAccess ()
specifier|private
name|void
name|verifySuperUserAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlList
name|viewACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|viewACL
operator|.
name|addGroup
argument_list|(
name|FRIENDLY_GROUP
argument_list|)
expr_stmt|;
name|AccessControlList
name|modifyACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|modifyACL
operator|.
name|addUser
argument_list|(
name|FRIEND
argument_list|)
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|submitAppAndGetAppId
argument_list|(
name|viewACL
argument_list|,
name|modifyACL
argument_list|)
decl_stmt|;
specifier|final
name|GetApplicationReportRequest
name|appReportRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|appReportRequest
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
specifier|final
name|KillApplicationRequest
name|finishAppRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|finishAppRequest
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|ClientRMProtocol
name|superUserClient
init|=
name|getRMClientForUser
argument_list|(
name|SUPER_USER
argument_list|)
decl_stmt|;
comment|// View as the superUser
name|superUserClient
operator|.
name|getApplicationReport
argument_list|(
name|appReportRequest
argument_list|)
expr_stmt|;
comment|// List apps as superUser
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"App view by super-user should list the apps!!"
argument_list|,
literal|2
argument_list|,
name|superUserClient
operator|.
name|getAllApplications
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetAllApplicationsRequest
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|getApplicationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Kill app as the superUser
name|superUserClient
operator|.
name|forceKillApplication
argument_list|(
name|finishAppRequest
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|waitForState
argument_list|(
name|applicationId
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyFriendAccess ()
specifier|private
name|void
name|verifyFriendAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlList
name|viewACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|viewACL
operator|.
name|addGroup
argument_list|(
name|FRIENDLY_GROUP
argument_list|)
expr_stmt|;
name|AccessControlList
name|modifyACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|modifyACL
operator|.
name|addUser
argument_list|(
name|FRIEND
argument_list|)
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|submitAppAndGetAppId
argument_list|(
name|viewACL
argument_list|,
name|modifyACL
argument_list|)
decl_stmt|;
specifier|final
name|GetApplicationReportRequest
name|appReportRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|appReportRequest
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
specifier|final
name|KillApplicationRequest
name|finishAppRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|finishAppRequest
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|ClientRMProtocol
name|friendClient
init|=
name|getRMClientForUser
argument_list|(
name|FRIEND
argument_list|)
decl_stmt|;
comment|// View as the friend
name|friendClient
operator|.
name|getApplicationReport
argument_list|(
name|appReportRequest
argument_list|)
expr_stmt|;
comment|// List apps as friend
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"App view by a friend should list the apps!!"
argument_list|,
literal|3
argument_list|,
name|friendClient
operator|.
name|getAllApplications
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetAllApplicationsRequest
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|getApplicationList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Kill app as the friend
name|friendClient
operator|.
name|forceKillApplication
argument_list|(
name|finishAppRequest
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|waitForState
argument_list|(
name|applicationId
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyEnemyAccess ()
specifier|private
name|void
name|verifyEnemyAccess
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlList
name|viewACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|viewACL
operator|.
name|addGroup
argument_list|(
name|FRIENDLY_GROUP
argument_list|)
expr_stmt|;
name|AccessControlList
name|modifyACL
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|modifyACL
operator|.
name|addUser
argument_list|(
name|FRIEND
argument_list|)
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|submitAppAndGetAppId
argument_list|(
name|viewACL
argument_list|,
name|modifyACL
argument_list|)
decl_stmt|;
specifier|final
name|GetApplicationReportRequest
name|appReportRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|appReportRequest
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
specifier|final
name|KillApplicationRequest
name|finishAppRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|finishAppRequest
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|ClientRMProtocol
name|enemyRmClient
init|=
name|getRMClientForUser
argument_list|(
name|ENEMY
argument_list|)
decl_stmt|;
comment|// View as the enemy
name|ApplicationReport
name|appReport
init|=
name|enemyRmClient
operator|.
name|getApplicationReport
argument_list|(
name|appReportRequest
argument_list|)
operator|.
name|getApplicationReport
argument_list|()
decl_stmt|;
name|verifyEnemyAppReport
argument_list|(
name|appReport
argument_list|)
expr_stmt|;
comment|// List apps as enemy
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|appReports
init|=
name|enemyRmClient
operator|.
name|getAllApplications
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetAllApplicationsRequest
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|getApplicationList
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"App view by enemy should list the apps!!"
argument_list|,
literal|4
argument_list|,
name|appReports
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ApplicationReport
name|report
range|:
name|appReports
control|)
block|{
name|verifyEnemyAppReport
argument_list|(
name|report
argument_list|)
expr_stmt|;
block|}
comment|// Kill app as the enemy
try|try
block|{
name|enemyRmClient
operator|.
name|forceKillApplication
argument_list|(
name|finishAppRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"App killing by the enemy should fail!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got exception while killing app as the enemy"
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
literal|"User enemy cannot perform operation MODIFY_APP on "
operator|+
name|applicationId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rmClient
operator|.
name|forceKillApplication
argument_list|(
name|finishAppRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyEnemyAppReport (ApplicationReport appReport)
specifier|private
name|void
name|verifyEnemyAppReport
parameter_list|(
name|ApplicationReport
name|appReport
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app host!"
argument_list|,
name|UNAVAILABLE
argument_list|,
name|appReport
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app rpc port!"
argument_list|,
operator|-
literal|1
argument_list|,
name|appReport
operator|.
name|getRpcPort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app client token!"
argument_list|,
name|UNAVAILABLE
argument_list|,
name|appReport
operator|.
name|getClientToken
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app diagnostics!"
argument_list|,
name|UNAVAILABLE
argument_list|,
name|appReport
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app tracking url!"
argument_list|,
name|UNAVAILABLE
argument_list|,
name|appReport
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app original tracking url!"
argument_list|,
name|UNAVAILABLE
argument_list|,
name|appReport
operator|.
name|getOriginalTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationResourceUsageReport
name|usageReport
init|=
name|appReport
operator|.
name|getApplicationResourceUsageReport
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app used containers"
argument_list|,
operator|-
literal|1
argument_list|,
name|usageReport
operator|.
name|getNumUsedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app reserved containers"
argument_list|,
operator|-
literal|1
argument_list|,
name|usageReport
operator|.
name|getNumReservedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app used resources"
argument_list|,
operator|-
literal|1
argument_list|,
name|usageReport
operator|.
name|getUsedResources
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app reserved resources"
argument_list|,
operator|-
literal|1
argument_list|,
name|usageReport
operator|.
name|getReservedResources
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Enemy should not see app needed resources"
argument_list|,
operator|-
literal|1
argument_list|,
name|usageReport
operator|.
name|getNeededResources
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

