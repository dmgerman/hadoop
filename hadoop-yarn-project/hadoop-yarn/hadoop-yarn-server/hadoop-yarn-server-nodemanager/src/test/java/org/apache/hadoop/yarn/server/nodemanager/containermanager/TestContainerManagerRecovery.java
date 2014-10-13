begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager
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
name|nodemanager
operator|.
name|containermanager
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

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
name|PrivilegedExceptionAction
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
name|Collections
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
name|LocalResource
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
name|LogAggregationContext
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
name|security
operator|.
name|NMTokenIdentifier
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
name|MasterKey
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
name|impl
operator|.
name|pb
operator|.
name|MasterKeyPBImpl
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
name|nodemanager
operator|.
name|CMgrCompletedAppsEvent
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
name|nodemanager
operator|.
name|ContainerExecutor
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
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|DeletionService
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
name|nodemanager
operator|.
name|NodeManager
operator|.
name|NMContext
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
name|nodemanager
operator|.
name|NodeStatusUpdater
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|ApplicationEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|ApplicationEventType
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|ApplicationImpl
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|launcher
operator|.
name|ContainersLauncher
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|launcher
operator|.
name|ContainersLauncherEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|ResourceLocalizationService
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|LocalizationEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|loghandler
operator|.
name|LogHandler
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
name|nodemanager
operator|.
name|metrics
operator|.
name|NodeManagerMetrics
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
name|nodemanager
operator|.
name|recovery
operator|.
name|NMMemoryStateStoreService
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
name|nodemanager
operator|.
name|recovery
operator|.
name|NMStateStoreService
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
name|nodemanager
operator|.
name|security
operator|.
name|NMContainerTokenSecretManager
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
name|nodemanager
operator|.
name|security
operator|.
name|NMTokenSecretManagerInNM
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
name|security
operator|.
name|ApplicationACLsManager
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
DECL|class|TestContainerManagerRecovery
specifier|public
class|class
name|TestContainerManagerRecovery
block|{
DECL|field|metrics
specifier|private
name|NodeManagerMetrics
name|metrics
init|=
name|NodeManagerMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testApplicationRecovery ()
specifier|public
name|void
name|testApplicationRecovery
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
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RECOVERY_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
literal|"localhost:1234"
argument_list|)
expr_stmt|;
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
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
literal|"yarn_admin_user"
argument_list|)
expr_stmt|;
name|NMStateStoreService
name|stateStore
init|=
operator|new
name|NMMemoryStateStoreService
argument_list|()
decl_stmt|;
name|stateStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|stateStore
operator|.
name|start
argument_list|()
expr_stmt|;
name|Context
name|context
init|=
operator|new
name|NMContext
argument_list|(
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|NMTokenSecretManagerInNM
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|stateStore
argument_list|)
decl_stmt|;
name|ContainerManagerImpl
name|cm
init|=
name|createContainerManager
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|cm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// simulate registration with RM
name|MasterKey
name|masterKey
init|=
operator|new
name|MasterKeyPBImpl
argument_list|()
decl_stmt|;
name|masterKey
operator|.
name|setKeyId
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|masterKey
operator|.
name|setBytes
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|new
name|Integer
argument_list|(
literal|123
argument_list|)
operator|.
name|byteValue
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|getContainerTokenSecretManager
argument_list|()
operator|.
name|setMasterKey
argument_list|(
name|masterKey
argument_list|)
expr_stmt|;
name|context
operator|.
name|getNMTokenSecretManager
argument_list|()
operator|.
name|setMasterKey
argument_list|(
name|masterKey
argument_list|)
expr_stmt|;
comment|// add an application by starting a container
name|String
name|appUser
init|=
literal|"app_user1"
decl_stmt|;
name|String
name|modUser
init|=
literal|"modify_user1"
decl_stmt|;
name|String
name|viewUser
init|=
literal|"view_user1"
decl_stmt|;
name|String
name|enemyUser
init|=
literal|"enemy_user"
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|attemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|appId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|cid
init|=
name|ContainerId
operator|.
name|newInstance
argument_list|(
name|attemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|LocalResource
argument_list|>
name|localResources
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|containerEnv
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|containerCmds
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ByteBuffer
argument_list|>
name|serviceData
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
name|Credentials
name|containerCreds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|containerCreds
operator|.
name|writeTokenStorageToStream
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|ByteBuffer
name|containerTokens
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
name|MODIFY_APP
argument_list|,
name|modUser
argument_list|)
expr_stmt|;
name|acls
operator|.
name|put
argument_list|(
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|viewUser
argument_list|)
expr_stmt|;
name|ContainerLaunchContext
name|clc
init|=
name|ContainerLaunchContext
operator|.
name|newInstance
argument_list|(
name|localResources
argument_list|,
name|containerEnv
argument_list|,
name|containerCmds
argument_list|,
name|serviceData
argument_list|,
name|containerTokens
argument_list|,
name|acls
argument_list|)
decl_stmt|;
comment|// create the logAggregationContext
name|LogAggregationContext
name|logAggregationContext
init|=
name|LogAggregationContext
operator|.
name|newInstance
argument_list|(
literal|"includePattern"
argument_list|,
literal|"excludePattern"
argument_list|)
decl_stmt|;
name|StartContainersResponse
name|startResponse
init|=
name|startContainer
argument_list|(
name|context
argument_list|,
name|cm
argument_list|,
name|cid
argument_list|,
name|clc
argument_list|,
name|logAggregationContext
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|startResponse
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Application
name|app
init|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|app
argument_list|)
expr_stmt|;
name|waitForAppState
argument_list|(
name|app
argument_list|,
name|ApplicationState
operator|.
name|INITING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|modUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|MODIFY_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|viewUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|MODIFY_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|viewUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|enemyUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
comment|// reset container manager and verify app recovered with proper acls
name|cm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|NMContext
argument_list|(
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|NMTokenSecretManagerInNM
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|stateStore
argument_list|)
expr_stmt|;
name|cm
operator|=
name|createContainerManager
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|cm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cm
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|app
operator|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|app
argument_list|)
expr_stmt|;
comment|// check whether LogAggregationContext is recovered correctly
name|LogAggregationContext
name|recovered
init|=
operator|(
operator|(
name|ApplicationImpl
operator|)
name|app
operator|)
operator|.
name|getLogAggregationContext
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|recovered
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|logAggregationContext
operator|.
name|getIncludePattern
argument_list|()
argument_list|,
name|recovered
operator|.
name|getIncludePattern
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|logAggregationContext
operator|.
name|getExcludePattern
argument_list|()
argument_list|,
name|recovered
operator|.
name|getExcludePattern
argument_list|()
argument_list|)
expr_stmt|;
name|waitForAppState
argument_list|(
name|app
argument_list|,
name|ApplicationState
operator|.
name|INITING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|modUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|MODIFY_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|viewUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|MODIFY_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|viewUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|enemyUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
comment|// simulate application completion
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|finishedApps
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
decl_stmt|;
name|finishedApps
operator|.
name|add
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|cm
operator|.
name|handle
argument_list|(
operator|new
name|CMgrCompletedAppsEvent
argument_list|(
name|finishedApps
argument_list|,
name|CMgrCompletedAppsEvent
operator|.
name|Reason
operator|.
name|BY_RESOURCEMANAGER
argument_list|)
argument_list|)
expr_stmt|;
name|waitForAppState
argument_list|(
name|app
argument_list|,
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
argument_list|)
expr_stmt|;
comment|// restart and verify app is marked for finishing
name|cm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|NMContext
argument_list|(
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|NMTokenSecretManagerInNM
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|stateStore
argument_list|)
expr_stmt|;
name|cm
operator|=
name|createContainerManager
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|cm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cm
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|app
operator|=
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|app
argument_list|)
expr_stmt|;
name|waitForAppState
argument_list|(
name|app
argument_list|,
name|ApplicationState
operator|.
name|APPLICATION_RESOURCES_CLEANINGUP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|modUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|MODIFY_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|viewUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|MODIFY_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|viewUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
operator|.
name|checkAccess
argument_list|(
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|enemyUser
argument_list|)
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|appUser
argument_list|,
name|appId
argument_list|)
argument_list|)
expr_stmt|;
comment|// simulate log aggregation completion
name|app
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationEvent
argument_list|(
name|app
operator|.
name|getAppId
argument_list|()
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_RESOURCES_CLEANEDUP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|app
operator|.
name|getApplicationState
argument_list|()
argument_list|,
name|ApplicationState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|app
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationEvent
argument_list|(
name|app
operator|.
name|getAppId
argument_list|()
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_FINISHED
argument_list|)
argument_list|)
expr_stmt|;
comment|// restart and verify app is no longer present after recovery
name|cm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|context
operator|=
operator|new
name|NMContext
argument_list|(
operator|new
name|NMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|NMTokenSecretManagerInNM
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|stateStore
argument_list|)
expr_stmt|;
name|cm
operator|=
name|createContainerManager
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|cm
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cm
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|getApplications
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|startContainer (Context context, final ContainerManagerImpl cm, ContainerId cid, ContainerLaunchContext clc, LogAggregationContext logAggregationContext)
specifier|private
name|StartContainersResponse
name|startContainer
parameter_list|(
name|Context
name|context
parameter_list|,
specifier|final
name|ContainerManagerImpl
name|cm
parameter_list|,
name|ContainerId
name|cid
parameter_list|,
name|ContainerLaunchContext
name|clc
parameter_list|,
name|LogAggregationContext
name|logAggregationContext
parameter_list|)
throws|throws
name|Exception
block|{
name|UserGroupInformation
name|user
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|cid
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|StartContainerRequest
name|scReq
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|clc
argument_list|,
name|TestContainerManager
operator|.
name|createContainerToken
argument_list|(
name|cid
argument_list|,
literal|0
argument_list|,
name|context
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|context
operator|.
name|getContainerTokenSecretManager
argument_list|()
argument_list|,
name|logAggregationContext
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|scReqList
init|=
operator|new
name|ArrayList
argument_list|<
name|StartContainerRequest
argument_list|>
argument_list|()
decl_stmt|;
name|scReqList
operator|.
name|add
argument_list|(
name|scReq
argument_list|)
expr_stmt|;
name|NMTokenIdentifier
name|nmToken
init|=
operator|new
name|NMTokenIdentifier
argument_list|(
name|cid
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|context
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|user
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|context
operator|.
name|getNMTokenSecretManager
argument_list|()
operator|.
name|getCurrentKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
decl_stmt|;
name|user
operator|.
name|addTokenIdentifier
argument_list|(
name|nmToken
argument_list|)
expr_stmt|;
return|return
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|StartContainersResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|StartContainersResponse
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|cm
operator|.
name|startContainers
argument_list|(
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|scReqList
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|waitForAppState (Application app, ApplicationState state)
specifier|private
name|void
name|waitForAppState
parameter_list|(
name|Application
name|app
parameter_list|,
name|ApplicationState
name|state
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|msecPerSleep
init|=
literal|10
decl_stmt|;
name|int
name|msecLeft
init|=
literal|5000
decl_stmt|;
while|while
condition|(
name|app
operator|.
name|getApplicationState
argument_list|()
operator|!=
name|state
operator|&&
name|msecLeft
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|msecPerSleep
argument_list|)
expr_stmt|;
name|msecLeft
operator|-=
name|msecPerSleep
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|state
argument_list|,
name|app
operator|.
name|getApplicationState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createContainerManager (Context context)
specifier|private
name|ContainerManagerImpl
name|createContainerManager
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
specifier|final
name|LogHandler
name|logHandler
init|=
name|mock
argument_list|(
name|LogHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ResourceLocalizationService
name|rsrcSrv
init|=
operator|new
name|ResourceLocalizationService
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|context
operator|.
name|getNMStateStore
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{           }
annotation|@
name|Override
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|LocalizationEvent
name|event
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
decl_stmt|;
specifier|final
name|ContainersLauncher
name|launcher
init|=
operator|new
name|ContainersLauncher
argument_list|(
name|context
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|ContainersLauncherEvent
name|event
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ContainerManagerImpl
argument_list|(
name|context
argument_list|,
name|mock
argument_list|(
name|ContainerExecutor
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
argument_list|,
name|mock
argument_list|(
name|NodeStatusUpdater
operator|.
name|class
argument_list|)
argument_list|,
name|metrics
argument_list|,
name|context
operator|.
name|getApplicationACLsManager
argument_list|()
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|LogHandler
name|createLogHandler
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Context
name|context
parameter_list|,
name|DeletionService
name|deletionService
parameter_list|)
block|{
return|return
name|logHandler
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ResourceLocalizationService
name|createResourceLocalizationService
parameter_list|(
name|ContainerExecutor
name|exec
parameter_list|,
name|DeletionService
name|deletionContext
parameter_list|)
block|{
return|return
name|rsrcSrv
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ContainersLauncher
name|createContainersLauncher
parameter_list|(
name|Context
name|context
parameter_list|,
name|ContainerExecutor
name|exec
parameter_list|)
block|{
return|return
name|launcher
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBlockNewContainerRequests
parameter_list|(
name|boolean
name|blockNewContainerRequests
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

