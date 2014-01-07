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
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
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
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslException
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
name|ipc
operator|.
name|RPC
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
name|ipc
operator|.
name|RemoteException
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
name|ipc
operator|.
name|Server
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
name|net
operator|.
name|NetUtils
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
name|KerberosInfo
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
name|SecurityInfo
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
name|SecurityUtil
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
name|security
operator|.
name|token
operator|.
name|TokenInfo
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
name|TokenSelector
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
name|service
operator|.
name|AbstractService
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
name|GetApplicationReportResponse
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
name|DrainDispatcher
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|ClientToAMTokenIdentifier
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
name|ClientToAMTokenSecretManager
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
name|ClientToAMTokenSelector
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
name|ClientRMService
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
name|MockAM
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
name|ConverterUtils
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
name|Test
import|;
end_import

begin_class
DECL|class|TestClientToAMTokens
specifier|public
class|class
name|TestClientToAMTokens
block|{
DECL|interface|CustomProtocol
specifier|private
interface|interface
name|CustomProtocol
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
DECL|class|CustomSecurityInfo
specifier|private
specifier|static
class|class
name|CustomSecurityInfo
extends|extends
name|SecurityInfo
block|{
annotation|@
name|Override
DECL|method|getTokenInfo (Class<?> protocol, Configuration conf)
specifier|public
name|TokenInfo
name|getTokenInfo
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|TokenInfo
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|TokenSelector
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|value
parameter_list|()
block|{
return|return
name|ClientToAMTokenSelector
operator|.
name|class
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getKerberosInfo (Class<?> protocol, Configuration conf)
specifier|public
name|KerberosInfo
name|getKerberosInfo
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
empty_stmt|;
DECL|class|CustomAM
specifier|private
specifier|static
class|class
name|CustomAM
extends|extends
name|AbstractService
implements|implements
name|CustomProtocol
block|{
DECL|field|appAttemptId
specifier|private
specifier|final
name|ApplicationAttemptId
name|appAttemptId
decl_stmt|;
DECL|field|secretKey
specifier|private
specifier|final
name|byte
index|[]
name|secretKey
decl_stmt|;
DECL|field|address
specifier|private
name|InetSocketAddress
name|address
decl_stmt|;
DECL|field|pinged
specifier|private
name|boolean
name|pinged
init|=
literal|false
decl_stmt|;
DECL|method|CustomAM (ApplicationAttemptId appId, byte[] secretKey)
specifier|public
name|CustomAM
parameter_list|(
name|ApplicationAttemptId
name|appId
parameter_list|,
name|byte
index|[]
name|secretKey
parameter_list|)
block|{
name|super
argument_list|(
literal|"CustomAM"
argument_list|)
expr_stmt|;
name|this
operator|.
name|appAttemptId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|secretKey
operator|=
name|secretKey
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|this
operator|.
name|pinged
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|Server
name|server
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|CustomProtocol
operator|.
name|class
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|1
argument_list|)
operator|.
name|setSecretManager
argument_list|(
operator|new
name|ClientToAMTokenSecretManager
argument_list|(
name|this
operator|.
name|appAttemptId
argument_list|,
name|secretKey
argument_list|)
argument_list|)
operator|.
name|setInstance
argument_list|(
name|this
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testClientToAMTokens ()
specifier|public
name|void
name|testClientToAMTokens
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|ContainerManagementProtocol
name|containerManager
init|=
name|mock
argument_list|(
name|ContainerManagementProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
name|StartContainersResponse
name|mockResponse
init|=
name|mock
argument_list|(
name|StartContainersResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|containerManager
operator|.
name|startContainers
argument_list|(
operator|(
name|StartContainersRequest
operator|)
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockResponse
argument_list|)
expr_stmt|;
specifier|final
name|DrainDispatcher
name|dispatcher
init|=
operator|new
name|DrainDispatcher
argument_list|()
decl_stmt|;
name|MockRM
name|rm
init|=
operator|new
name|MockRMWithCustomAMLauncher
argument_list|(
name|conf
argument_list|,
name|containerManager
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
name|this
operator|.
name|rmContext
argument_list|,
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
name|this
operator|.
name|queueACLsManager
argument_list|,
name|getRMDTSecretManager
argument_list|()
argument_list|)
return|;
block|}
empty_stmt|;
annotation|@
name|Override
specifier|protected
name|Dispatcher
name|createDispatcher
parameter_list|()
block|{
return|return
name|dispatcher
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doSecureLogin
parameter_list|()
throws|throws
name|IOException
block|{       }
block|}
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Submit an app
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
comment|// Set up a node.
name|MockNM
name|nm1
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"localhost:1234"
argument_list|,
literal|3072
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|ApplicationAttemptId
name|appAttempt
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
decl_stmt|;
specifier|final
name|MockAM
name|mockAM
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
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|appUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|appAttempt
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|RegisterApplicationMasterResponse
name|response
init|=
name|appUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|RegisterApplicationMasterResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RegisterApplicationMasterResponse
name|run
parameter_list|()
block|{
name|RegisterApplicationMasterResponse
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|mockAM
operator|.
name|registerAppAttempt
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
name|fail
argument_list|(
literal|"Exception was not expected"
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Get the app-report.
name|GetApplicationReportRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationId
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|GetApplicationReportResponse
name|reportResponse
init|=
name|rm
operator|.
name|getClientRMService
argument_list|()
operator|.
name|getApplicationReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ApplicationReport
name|appReport
init|=
name|reportResponse
operator|.
name|getApplicationReport
argument_list|()
decl_stmt|;
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
name|originalClientToAMToken
init|=
name|appReport
operator|.
name|getClientToAMToken
argument_list|()
decl_stmt|;
comment|// ClientToAMToken master key should have been received on register
comment|// application master response.
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
operator|.
name|getClientToAMTokenMasterKey
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|getClientToAMTokenMasterKey
argument_list|()
operator|.
name|array
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Start the AM with the correct shared-secret.
name|ApplicationAttemptId
name|appAttemptId
init|=
name|app
operator|.
name|getAppAttempts
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
specifier|final
name|CustomAM
name|am
init|=
operator|new
name|CustomAM
argument_list|(
name|appAttemptId
argument_list|,
name|response
operator|.
name|getClientToAMTokenMasterKey
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
decl_stmt|;
name|am
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|am
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Now the real test!
comment|// Set up clients to be able to pick up correct tokens.
name|SecurityUtil
operator|.
name|setSecurityInfoProviders
argument_list|(
operator|new
name|CustomSecurityInfo
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify denial for unauthenticated user
try|try
block|{
name|CustomProtocol
name|client
init|=
operator|(
name|CustomProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|CustomProtocol
operator|.
name|class
argument_list|,
literal|1L
argument_list|,
name|am
operator|.
name|address
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|client
operator|.
name|ping
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Access by unauthenticated user should fail!!"
argument_list|)
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
name|assertFalse
argument_list|(
name|am
operator|.
name|pinged
argument_list|)
expr_stmt|;
block|}
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
name|token
init|=
name|ConverterUtils
operator|.
name|convertFromYarn
argument_list|(
name|originalClientToAMToken
argument_list|,
name|am
operator|.
name|address
argument_list|)
decl_stmt|;
comment|// Verify denial for a malicious user with tampered ID
name|verifyTokenWithTamperedID
argument_list|(
name|conf
argument_list|,
name|am
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// Verify denial for a malicious user with tampered user-name
name|verifyTokenWithTamperedUserName
argument_list|(
name|conf
argument_list|,
name|am
argument_list|,
name|token
argument_list|)
expr_stmt|;
comment|// Now for an authenticated user
name|verifyValidToken
argument_list|(
name|conf
argument_list|,
name|am
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyTokenWithTamperedID (final Configuration conf, final CustomAM am, Token<ClientToAMTokenIdentifier> token)
specifier|private
name|void
name|verifyTokenWithTamperedID
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|CustomAM
name|am
parameter_list|,
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Malicious user, messes with appId
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"me"
argument_list|)
decl_stmt|;
name|ClientToAMTokenIdentifier
name|maliciousID
init|=
operator|new
name|ClientToAMTokenIdentifier
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
name|am
operator|.
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getClusterTimestamp
argument_list|()
argument_list|,
literal|42
argument_list|)
argument_list|,
literal|43
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
name|verifyTamperedToken
argument_list|(
name|conf
argument_list|,
name|am
argument_list|,
name|token
argument_list|,
name|ugi
argument_list|,
name|maliciousID
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyTokenWithTamperedUserName (final Configuration conf, final CustomAM am, Token<ClientToAMTokenIdentifier> token)
specifier|private
name|void
name|verifyTokenWithTamperedUserName
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|CustomAM
name|am
parameter_list|,
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Malicious user, messes with appId
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"me"
argument_list|)
decl_stmt|;
name|ClientToAMTokenIdentifier
name|maliciousID
init|=
operator|new
name|ClientToAMTokenIdentifier
argument_list|(
name|am
operator|.
name|appAttemptId
argument_list|,
literal|"evilOrc"
argument_list|)
decl_stmt|;
name|verifyTamperedToken
argument_list|(
name|conf
argument_list|,
name|am
argument_list|,
name|token
argument_list|,
name|ugi
argument_list|,
name|maliciousID
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyTamperedToken (final Configuration conf, final CustomAM am, Token<ClientToAMTokenIdentifier> token, UserGroupInformation ugi, ClientToAMTokenIdentifier maliciousID)
specifier|private
name|void
name|verifyTamperedToken
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|CustomAM
name|am
parameter_list|,
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|ClientToAMTokenIdentifier
name|maliciousID
parameter_list|)
block|{
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
name|maliciousToken
init|=
operator|new
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
argument_list|(
name|maliciousID
operator|.
name|getBytes
argument_list|()
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
argument_list|,
name|token
operator|.
name|getKind
argument_list|()
argument_list|,
name|token
operator|.
name|getService
argument_list|()
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|maliciousToken
argument_list|)
expr_stmt|;
try|try
block|{
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|CustomProtocol
name|client
init|=
operator|(
name|CustomProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|CustomProtocol
operator|.
name|class
argument_list|,
literal|1L
argument_list|,
name|am
operator|.
name|address
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|client
operator|.
name|ping
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Connection initiation with illegally modified "
operator|+
literal|"tokens is expected to fail."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Cannot get a YARN remote exception as "
operator|+
literal|"it will indicate RPC success"
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
block|}
argument_list|)
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
name|RemoteException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|=
operator|(
operator|(
name|RemoteException
operator|)
name|e
operator|)
operator|.
name|unwrapRemoteException
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|SaslException
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
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
literal|"DIGEST-MD5: digest response format violation. "
operator|+
literal|"Mismatched response."
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|am
operator|.
name|pinged
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyValidToken (final Configuration conf, final CustomAM am, Token<ClientToAMTokenIdentifier> token)
specifier|private
name|void
name|verifyValidToken
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|CustomAM
name|am
parameter_list|,
name|Token
argument_list|<
name|ClientToAMTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|UserGroupInformation
name|ugi
decl_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"me"
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|CustomProtocol
name|client
init|=
operator|(
name|CustomProtocol
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|CustomProtocol
operator|.
name|class
argument_list|,
literal|1L
argument_list|,
name|am
operator|.
name|address
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|client
operator|.
name|ping
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|am
operator|.
name|pinged
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

