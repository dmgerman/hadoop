begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|io
operator|.
name|DataInputByteBuffer
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|FinishApplicationMasterRequest
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
name|FinalApplicationStatus
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
name|MockNM
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
name|TestAMAuthorization
operator|.
name|MockRMWithAMS
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
name|TestAMAuthorization
operator|.
name|MyContainerManager
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
DECL|class|TestApplicationTokens
specifier|public
class|class
name|TestApplicationTokens
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
name|TestApplicationTokens
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|confWithSecurityEnabled
specifier|private
specifier|static
specifier|final
name|Configuration
name|confWithSecurityEnabled
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
static|static
block|{
name|confWithSecurityEnabled
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
name|confWithSecurityEnabled
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate that application tokens are unusable after the    * application-finishes.    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testTokenExpiry ()
specifier|public
name|void
name|testTokenExpiry
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
name|confWithSecurityEnabled
argument_list|,
name|containerManager
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|AMRMProtocol
name|rmClient
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|amTokens
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
name|amTokens
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
comment|// Create a client to the RM.
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
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|DataInputByteBuffer
name|buf
init|=
operator|new
name|DataInputByteBuffer
argument_list|()
decl_stmt|;
name|containerManager
operator|.
name|amTokens
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|buf
operator|.
name|reset
argument_list|(
name|containerManager
operator|.
name|amTokens
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|readTokenStorageStream
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|currentUser
operator|.
name|addCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
name|rmClient
operator|=
name|createRMClient
argument_list|(
name|rm
argument_list|,
name|conf
argument_list|,
name|rpc
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
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
name|rmClient
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|FinishApplicationMasterRequest
name|finishAMRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|finishAMRequest
operator|.
name|setAppAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|finishAMRequest
operator|.
name|setFinishApplicationStatus
argument_list|(
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|finishAMRequest
operator|.
name|setDiagnostics
argument_list|(
literal|"diagnostics"
argument_list|)
expr_stmt|;
name|finishAMRequest
operator|.
name|setTrackingUrl
argument_list|(
literal|"url"
argument_list|)
expr_stmt|;
name|rmClient
operator|.
name|finishApplicationMaster
argument_list|(
name|finishAMRequest
argument_list|)
expr_stmt|;
comment|// Now simulate trying to allocate. RPC call itself should throw auth
comment|// exception.
name|rpc
operator|.
name|stopProxy
argument_list|(
name|rmClient
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// To avoid using cached client
name|rmClient
operator|=
name|createRMClient
argument_list|(
name|rm
argument_list|,
name|conf
argument_list|,
name|rpc
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
name|request
operator|.
name|setApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|12345
argument_list|,
literal|78
argument_list|)
argument_list|,
literal|987
argument_list|)
argument_list|)
expr_stmt|;
name|AllocateRequest
name|allocateRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|allocateRequest
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
try|try
block|{
name|rmClient
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"You got to be kidding me! "
operator|+
literal|"Using App tokens after app-finish should fail!"
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
name|info
argument_list|(
literal|"Exception found is "
argument_list|,
name|t
argument_list|)
expr_stmt|;
comment|// The exception will still have the earlier appAttemptId as it picks it
comment|// up from the token.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|t
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Password not found for ApplicationAttempt "
operator|+
name|applicationAttemptId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|rmClient
operator|!=
literal|null
condition|)
block|{
name|rpc
operator|.
name|stopProxy
argument_list|(
name|rmClient
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// To avoid using cached client
block|}
block|}
block|}
comment|/**    * Validate master-key-roll-over and that tokens are usable even after    * master-key-roll-over.    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testMasterKeyRollOver ()
specifier|public
name|void
name|testMasterKeyRollOver
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
name|confWithSecurityEnabled
argument_list|,
name|containerManager
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|AMRMProtocol
name|rmClient
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|amTokens
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
name|amTokens
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
comment|// Create a client to the RM.
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
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|DataInputByteBuffer
name|buf
init|=
operator|new
name|DataInputByteBuffer
argument_list|()
decl_stmt|;
name|containerManager
operator|.
name|amTokens
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|buf
operator|.
name|reset
argument_list|(
name|containerManager
operator|.
name|amTokens
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|readTokenStorageStream
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|currentUser
operator|.
name|addCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
name|rmClient
operator|=
name|createRMClient
argument_list|(
name|rm
argument_list|,
name|conf
argument_list|,
name|rpc
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
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
name|rmClient
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// One allocate call.
name|AllocateRequest
name|allocateRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|allocateRequest
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rmClient
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
operator|.
name|getAMCommand
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|// Simulate a master-key-roll-over
name|ApplicationTokenSecretManager
name|appTokenSecretManager
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getApplicationTokenSecretManager
argument_list|()
decl_stmt|;
name|SecretKey
name|oldKey
init|=
name|appTokenSecretManager
operator|.
name|getMasterKey
argument_list|()
decl_stmt|;
name|appTokenSecretManager
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
name|SecretKey
name|newKey
init|=
name|appTokenSecretManager
operator|.
name|getMasterKey
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Master key should have changed!"
argument_list|,
name|oldKey
operator|.
name|equals
argument_list|(
name|newKey
argument_list|)
argument_list|)
expr_stmt|;
comment|// Another allocate call. Should continue to work.
name|rpc
operator|.
name|stopProxy
argument_list|(
name|rmClient
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// To avoid using cached client
name|rmClient
operator|=
name|createRMClient
argument_list|(
name|rm
argument_list|,
name|conf
argument_list|,
name|rpc
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
name|allocateRequest
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AllocateRequest
operator|.
name|class
argument_list|)
expr_stmt|;
name|allocateRequest
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|rmClient
operator|.
name|allocate
argument_list|(
name|allocateRequest
argument_list|)
operator|.
name|getAMCommand
argument_list|()
operator|==
literal|null
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
if|if
condition|(
name|rmClient
operator|!=
literal|null
condition|)
block|{
name|rpc
operator|.
name|stopProxy
argument_list|(
name|rmClient
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// To avoid using cached client
block|}
block|}
block|}
DECL|method|createRMClient (final MockRM rm, final Configuration conf, final YarnRPC rpc, UserGroupInformation currentUser)
specifier|private
name|AMRMProtocol
name|createRMClient
parameter_list|(
specifier|final
name|MockRM
name|rm
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|YarnRPC
name|rpc
parameter_list|,
name|UserGroupInformation
name|currentUser
parameter_list|)
block|{
return|return
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
return|;
block|}
block|}
end_class

end_unit

