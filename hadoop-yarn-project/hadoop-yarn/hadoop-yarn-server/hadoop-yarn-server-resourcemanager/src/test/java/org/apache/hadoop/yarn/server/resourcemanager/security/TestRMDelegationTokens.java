begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.security
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
name|security
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|delegation
operator|.
name|DelegationKey
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
name|util
operator|.
name|ExitUtil
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
name|GetDelegationTokenRequest
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
name|GetDelegationTokenResponse
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
name|DelegationToken
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
name|client
operator|.
name|RMDelegationTokenIdentifier
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
name|TestRMRestart
operator|.
name|TestSecurityMockRM
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
name|MemoryRMStateStore
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
name|RMStateStore
operator|.
name|RMState
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
name|fair
operator|.
name|FairScheduler
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
name|ProtoUtils
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
DECL|class|TestRMDelegationTokens
specifier|public
class|class
name|TestRMDelegationTokens
block|{
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
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
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_STORE
argument_list|,
name|MemoryRMStateStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testRMDTMasterKeyStateOnRollingMasterKey ()
specifier|public
name|void
name|testRMDTMasterKeyStateOnRollingMasterKey
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryRMStateStore
name|memStore
init|=
operator|new
name|MemoryRMStateStore
argument_list|()
decl_stmt|;
name|memStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|RMState
name|rmState
init|=
name|memStore
operator|.
name|getState
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|rmDTState
init|=
name|rmState
operator|.
name|getRMDTSecretManagerState
argument_list|()
operator|.
name|getTokenState
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|rmDTMasterKeyState
init|=
name|rmState
operator|.
name|getRMDTSecretManagerState
argument_list|()
operator|.
name|getMasterKeyState
argument_list|()
decl_stmt|;
name|MockRM
name|rm1
init|=
operator|new
name|MyMockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// on rm start, two master keys are created.
comment|// One is created at RMDTSecretMgr.startThreads.updateCurrentKey();
comment|// the other is created on the first run of
comment|// tokenRemoverThread.rollMasterKey()
name|RMDelegationTokenSecretManager
name|dtSecretManager
init|=
name|rm1
operator|.
name|getRMDTSecretManager
argument_list|()
decl_stmt|;
comment|// assert all master keys are saved
name|Assert
operator|.
name|assertEquals
argument_list|(
name|dtSecretManager
operator|.
name|getAllMasterKeys
argument_list|()
argument_list|,
name|rmDTMasterKeyState
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|expiringKeys
init|=
operator|new
name|HashSet
argument_list|<
name|DelegationKey
argument_list|>
argument_list|()
decl_stmt|;
name|expiringKeys
operator|.
name|addAll
argument_list|(
name|dtSecretManager
operator|.
name|getAllMasterKeys
argument_list|()
argument_list|)
expr_stmt|;
comment|// record the current key
name|DelegationKey
name|oldCurrentKey
init|=
operator|(
operator|(
name|TestRMDelegationTokenSecretManager
operator|)
name|dtSecretManager
operator|)
operator|.
name|getCurrentKey
argument_list|()
decl_stmt|;
comment|// request to generate a RMDelegationToken
name|GetDelegationTokenRequest
name|request
init|=
name|mock
argument_list|(
name|GetDelegationTokenRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|request
operator|.
name|getRenewer
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"renewer1"
argument_list|)
expr_stmt|;
name|GetDelegationTokenResponse
name|response
init|=
name|rm1
operator|.
name|getClientRMService
argument_list|()
operator|.
name|getDelegationToken
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|DelegationToken
name|delegationToken
init|=
name|response
operator|.
name|getRMDelegationToken
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|>
name|token1
init|=
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|delegationToken
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|RMDelegationTokenIdentifier
name|dtId1
init|=
name|token1
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
comment|// wait for the first rollMasterKey
while|while
condition|(
operator|(
operator|(
name|TestRMDelegationTokenSecretManager
operator|)
name|dtSecretManager
operator|)
operator|.
name|numUpdatedKeys
operator|.
name|get
argument_list|()
operator|<
literal|1
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
comment|// assert old-current-key and new-current-key exist
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rmDTMasterKeyState
operator|.
name|contains
argument_list|(
name|oldCurrentKey
argument_list|)
argument_list|)
expr_stmt|;
name|DelegationKey
name|newCurrentKey
init|=
operator|(
operator|(
name|TestRMDelegationTokenSecretManager
operator|)
name|dtSecretManager
operator|)
operator|.
name|getCurrentKey
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rmDTMasterKeyState
operator|.
name|contains
argument_list|(
name|newCurrentKey
argument_list|)
argument_list|)
expr_stmt|;
comment|// wait for token to expire
comment|// rollMasterKey is called every 1 second.
while|while
condition|(
operator|(
operator|(
name|TestRMDelegationTokenSecretManager
operator|)
name|dtSecretManager
operator|)
operator|.
name|numUpdatedKeys
operator|.
name|get
argument_list|()
operator|<
literal|6
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertFalse
argument_list|(
name|rmDTState
operator|.
name|containsKey
argument_list|(
name|dtId1
argument_list|)
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
argument_list|(
name|timeout
operator|=
literal|15000
argument_list|)
DECL|method|testRemoveExpiredMasterKeyInRMStateStore ()
specifier|public
name|void
name|testRemoveExpiredMasterKeyInRMStateStore
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryRMStateStore
name|memStore
init|=
operator|new
name|MemoryRMStateStore
argument_list|()
decl_stmt|;
name|memStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|RMState
name|rmState
init|=
name|memStore
operator|.
name|getState
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|rmDTMasterKeyState
init|=
name|rmState
operator|.
name|getRMDTSecretManagerState
argument_list|()
operator|.
name|getMasterKeyState
argument_list|()
decl_stmt|;
name|MockRM
name|rm1
init|=
operator|new
name|MyMockRM
argument_list|(
name|conf
argument_list|,
name|memStore
argument_list|)
decl_stmt|;
name|rm1
operator|.
name|start
argument_list|()
expr_stmt|;
name|RMDelegationTokenSecretManager
name|dtSecretManager
init|=
name|rm1
operator|.
name|getRMDTSecretManager
argument_list|()
decl_stmt|;
comment|// assert all master keys are saved
name|Assert
operator|.
name|assertEquals
argument_list|(
name|dtSecretManager
operator|.
name|getAllMasterKeys
argument_list|()
argument_list|,
name|rmDTMasterKeyState
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|expiringKeys
init|=
operator|new
name|HashSet
argument_list|<
name|DelegationKey
argument_list|>
argument_list|()
decl_stmt|;
name|expiringKeys
operator|.
name|addAll
argument_list|(
name|dtSecretManager
operator|.
name|getAllMasterKeys
argument_list|()
argument_list|)
expr_stmt|;
comment|// wait for expiringKeys to expire
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|allExpired
init|=
literal|true
decl_stmt|;
for|for
control|(
name|DelegationKey
name|key
range|:
name|expiringKeys
control|)
block|{
if|if
condition|(
name|rmDTMasterKeyState
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|allExpired
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
name|allExpired
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MyMockRM
class|class
name|MyMockRM
extends|extends
name|TestSecurityMockRM
block|{
DECL|method|MyMockRM (Configuration conf, RMStateStore store)
specifier|public
name|MyMockRM
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMStateStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|RMDelegationTokenSecretManager
DECL|method|createRMDelegationTokenSecretManager (RMContext rmContext)
name|createRMDelegationTokenSecretManager
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
comment|// KeyUpdateInterval-> 1 seconds
comment|// TokenMaxLifetime-> 2 seconds.
return|return
operator|new
name|TestRMDelegationTokenSecretManager
argument_list|(
literal|1000
argument_list|,
literal|1000
argument_list|,
literal|2000
argument_list|,
literal|1000
argument_list|,
name|rmContext
argument_list|)
return|;
block|}
block|}
DECL|class|TestRMDelegationTokenSecretManager
specifier|public
class|class
name|TestRMDelegationTokenSecretManager
extends|extends
name|RMDelegationTokenSecretManager
block|{
DECL|field|numUpdatedKeys
specifier|public
name|AtomicInteger
name|numUpdatedKeys
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|TestRMDelegationTokenSecretManager (long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval, RMContext rmContext)
specifier|public
name|TestRMDelegationTokenSecretManager
parameter_list|(
name|long
name|delegationKeyUpdateInterval
parameter_list|,
name|long
name|delegationTokenMaxLifetime
parameter_list|,
name|long
name|delegationTokenRenewInterval
parameter_list|,
name|long
name|delegationTokenRemoverScanInterval
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|super
argument_list|(
name|delegationKeyUpdateInterval
argument_list|,
name|delegationTokenMaxLifetime
argument_list|,
name|delegationTokenRenewInterval
argument_list|,
name|delegationTokenRemoverScanInterval
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeNewMasterKey (DelegationKey newKey)
specifier|protected
name|void
name|storeNewMasterKey
parameter_list|(
name|DelegationKey
name|newKey
parameter_list|)
block|{
name|super
operator|.
name|storeNewMasterKey
argument_list|(
name|newKey
argument_list|)
expr_stmt|;
name|numUpdatedKeys
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|getCurrentKey ()
specifier|public
name|DelegationKey
name|getCurrentKey
parameter_list|()
block|{
for|for
control|(
name|int
name|keyId
range|:
name|allKeys
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|keyId
operator|==
name|currentId
condition|)
block|{
return|return
name|allKeys
operator|.
name|get
argument_list|(
name|keyId
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

