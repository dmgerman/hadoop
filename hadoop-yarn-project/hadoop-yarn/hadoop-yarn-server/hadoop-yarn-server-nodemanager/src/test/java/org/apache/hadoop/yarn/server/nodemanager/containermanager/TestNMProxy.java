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
name|fs
operator|.
name|UnsupportedFileSystemException
import|;
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
name|retry
operator|.
name|UnreliableInterface
import|;
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
name|client
operator|.
name|NMProxy
import|;
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
DECL|class|TestNMProxy
specifier|public
class|class
name|TestNMProxy
extends|extends
name|BaseContainerManagerTest
block|{
DECL|method|TestNMProxy ()
specifier|public
name|TestNMProxy
parameter_list|()
throws|throws
name|UnsupportedFileSystemException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|field|retryCount
name|int
name|retryCount
init|=
literal|0
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|containerManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ContainerManagerImpl
DECL|method|createContainerManager (DeletionService delSrvc)
name|createContainerManager
parameter_list|(
name|DeletionService
name|delSrvc
parameter_list|)
block|{
return|return
operator|new
name|ContainerManagerImpl
argument_list|(
name|context
argument_list|,
name|exec
argument_list|,
name|delSrvc
argument_list|,
name|nodeStatusUpdater
argument_list|,
name|metrics
argument_list|,
name|dirsHandler
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|StartContainersResponse
name|startContainers
parameter_list|(
name|StartContainersRequest
name|requests
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|retryCount
operator|<
literal|5
condition|)
block|{
name|retryCount
operator|++
expr_stmt|;
if|if
condition|(
name|isRetryPolicyRetryForEver
argument_list|()
condition|)
block|{
comment|// Throw non network exception
throw|throw
operator|new
name|IOException
argument_list|(
operator|new
name|UnreliableInterface
operator|.
name|UnreliableException
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|java
operator|.
name|net
operator|.
name|ConnectException
argument_list|(
literal|"start container exception"
argument_list|)
throw|;
block|}
block|}
return|return
name|super
operator|.
name|startContainers
argument_list|(
name|requests
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isRetryPolicyRetryForEver
parameter_list|()
block|{
return|return
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_NM_CONNECT_MAX_WAIT_MS
argument_list|,
literal|1000
argument_list|)
operator|==
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|StopContainersResponse
name|stopContainers
parameter_list|(
name|StopContainersRequest
name|requests
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|retryCount
operator|<
literal|5
condition|)
block|{
name|retryCount
operator|++
expr_stmt|;
throw|throw
operator|new
name|java
operator|.
name|net
operator|.
name|ConnectException
argument_list|(
literal|"stop container exception"
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|stopContainers
argument_list|(
name|requests
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|GetContainerStatusesResponse
name|getContainerStatuses
parameter_list|(
name|GetContainerStatusesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|retryCount
operator|<
literal|5
condition|)
block|{
name|retryCount
operator|++
expr_stmt|;
throw|throw
operator|new
name|java
operator|.
name|net
operator|.
name|ConnectException
argument_list|(
literal|"get container status exception"
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|getContainerStatuses
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testNMProxyRetry ()
specifier|public
name|void
name|testNMProxyRetry
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_NM_CONNECT_MAX_WAIT_MS
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_NM_CONNECT_RETRY_INTERVAL_MS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|StartContainersRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerManagementProtocol
name|proxy
init|=
name|getNMProxy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|proxy
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|retryCount
argument_list|)
expr_stmt|;
name|retryCount
operator|=
literal|0
expr_stmt|;
name|proxy
operator|.
name|stopContainers
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|StopContainersRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|retryCount
argument_list|)
expr_stmt|;
name|retryCount
operator|=
literal|0
expr_stmt|;
name|proxy
operator|.
name|getContainerStatuses
argument_list|(
name|Records
operator|.
name|newRecord
argument_list|(
name|GetContainerStatusesRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|retryCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|,
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testShouldNotRetryForeverForNonNetworkExceptionsOnNMConnections ()
specifier|public
name|void
name|testShouldNotRetryForeverForNonNetworkExceptionsOnNMConnections
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_NM_CONNECT_MAX_WAIT_MS
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|StartContainersRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerManagementProtocol
name|proxy
init|=
name|getNMProxy
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|retryCount
operator|=
literal|0
expr_stmt|;
name|proxy
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
DECL|method|testNMProxyRPCRetry ()
specifier|public
name|void
name|testNMProxyRPCRetry
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_NM_CONNECT_MAX_WAIT_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_NM_CONNECT_RETRY_INTERVAL_MS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|StartContainersRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|newConf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|newConf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|newConf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// connect to some dummy address so that it can trigger
comment|// connection failure and RPC level retires.
name|newConf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
literal|"0.0.0.0:1234"
argument_list|)
expr_stmt|;
name|ContainerManagementProtocol
name|proxy
init|=
name|getNMProxy
argument_list|(
name|newConf
argument_list|)
decl_stmt|;
try|try
block|{
name|proxy
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should get socket exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// socket exception should be thrown immediately, without RPC retries.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|java
operator|.
name|net
operator|.
name|SocketException
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getNMProxy (Configuration conf)
specifier|private
name|ContainerManagementProtocol
name|getNMProxy
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|1
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
name|nmToken
init|=
name|context
operator|.
name|getNMTokenSecretManager
argument_list|()
operator|.
name|createNMToken
argument_list|(
name|attemptId
argument_list|,
name|context
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|user
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|address
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|NM_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PORT
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|NMTokenIdentifier
argument_list|>
name|token
init|=
name|ConverterUtils
operator|.
name|convertFromYarn
argument_list|(
name|nmToken
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|address
argument_list|)
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|ugi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
name|NMProxy
operator|.
name|createNMProxy
argument_list|(
name|conf
argument_list|,
name|ContainerManagementProtocol
operator|.
name|class
argument_list|,
name|ugi
argument_list|,
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
argument_list|,
name|address
argument_list|)
return|;
block|}
block|}
end_class

end_unit

