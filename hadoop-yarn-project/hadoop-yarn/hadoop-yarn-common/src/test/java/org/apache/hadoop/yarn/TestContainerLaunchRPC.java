begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|net
operator|.
name|SocketTimeoutException
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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
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
name|util
operator|.
name|StringUtils
import|;
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
name|CommitResponse
import|;
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
name|ContainerUpdateRequest
import|;
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
name|ContainerUpdateResponse
import|;
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
name|GetLocalizationStatusesRequest
import|;
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
name|GetLocalizationStatusesResponse
import|;
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
name|IncreaseContainersResourceRequest
import|;
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
name|IncreaseContainersResourceResponse
import|;
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
name|ReInitializeContainerRequest
import|;
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
name|ReInitializeContainerResponse
import|;
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
name|ResourceLocalizationRequest
import|;
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
name|ResourceLocalizationResponse
import|;
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
name|RestartContainerResponse
import|;
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
name|RollbackResponse
import|;
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
name|SignalContainerRequest
import|;
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
name|SignalContainerResponse
import|;
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
name|ContainerStatus
import|;
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
name|Priority
import|;
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
name|HadoopYarnProtoRPC
import|;
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
name|ContainerTokenIdentifier
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

begin_comment
comment|/*  * Test that the container launcher rpc times out properly. This is used  * by both RM to launch an AM as well as an AM to launch containers.  */
end_comment

begin_class
DECL|class|TestContainerLaunchRPC
specifier|public
class|class
name|TestContainerLaunchRPC
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestContainerLaunchRPC
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testHadoopProtoRPCTimeout ()
specifier|public
name|void
name|testHadoopProtoRPCTimeout
parameter_list|()
throws|throws
name|Exception
block|{
name|testRPCTimeout
argument_list|(
name|HadoopYarnProtoRPC
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRPCTimeout (String rpcClass)
specifier|private
name|void
name|testRPCTimeout
parameter_list|(
name|String
name|rpcClass
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// set timeout low for the test
name|conf
operator|.
name|setInt
argument_list|(
literal|"yarn.rpc.nm-command-timeout"
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_RPC_IMPL
argument_list|,
name|rpcClass
argument_list|)
expr_stmt|;
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
name|String
name|bindAddr
init|=
literal|"localhost:0"
decl_stmt|;
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|bindAddr
argument_list|)
decl_stmt|;
name|Server
name|server
init|=
name|rpc
operator|.
name|getServer
argument_list|(
name|ContainerManagementProtocol
operator|.
name|class
argument_list|,
operator|new
name|DummyContainerManager
argument_list|()
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|ContainerManagementProtocol
name|proxy
init|=
operator|(
name|ContainerManagementProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ContainerManagementProtocol
operator|.
name|class
argument_list|,
name|server
operator|.
name|getListenerAddress
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ContainerLaunchContext
name|containerLaunchContext
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
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|applicationId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|applicationAttemptId
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"localhost"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1234
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ContainerTokenIdentifier
name|containerTokenIdentifier
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|(
name|containerId
argument_list|,
literal|"localhost"
argument_list|,
literal|"user"
argument_list|,
name|resource
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10000
argument_list|,
literal|42
argument_list|,
literal|42
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Token
name|containerToken
init|=
name|newContainerToken
argument_list|(
name|nodeId
argument_list|,
literal|"password"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|containerTokenIdentifier
argument_list|)
decl_stmt|;
name|StartContainerRequest
name|scRequest
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|containerLaunchContext
argument_list|,
name|containerToken
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|StartContainerRequest
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|scRequest
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|list
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Error, exception is not: "
operator|+
name|SocketTimeoutException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|SocketTimeoutException
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
return|return;
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|fail
argument_list|(
literal|"timeout exception should have occurred!"
argument_list|)
expr_stmt|;
block|}
DECL|method|newContainerToken (NodeId nodeId, byte[] password, ContainerTokenIdentifier tokenIdentifier)
specifier|public
specifier|static
name|Token
name|newContainerToken
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|byte
index|[]
name|password
parameter_list|,
name|ContainerTokenIdentifier
name|tokenIdentifier
parameter_list|)
block|{
comment|// RPC layer client expects ip:port as service for tokens
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|,
name|nodeId
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
comment|// NOTE: use SecurityUtil.setTokenService if this becomes a "real" token
name|Token
name|containerToken
init|=
name|Token
operator|.
name|newInstance
argument_list|(
name|tokenIdentifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|ContainerTokenIdentifier
operator|.
name|KIND
operator|.
name|toString
argument_list|()
argument_list|,
name|password
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|addr
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|containerToken
return|;
block|}
DECL|class|DummyContainerManager
specifier|public
class|class
name|DummyContainerManager
implements|implements
name|ContainerManagementProtocol
block|{
DECL|field|status
specifier|private
name|ContainerStatus
name|status
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|startContainers ( StartContainersRequest requests)
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
try|try
block|{
comment|// make the thread sleep to look like its not going to respond
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Shouldn't happen!!"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|StopContainersResponse
DECL|method|stopContainers (StopContainersRequest requests)
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
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|(
literal|"Dummy function"
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"Dummy function cause"
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
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
throws|,
name|IOException
block|{
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|GetContainerStatusesResponse
name|response
init|=
name|GetContainerStatusesResponse
operator|.
name|newInstance
argument_list|(
name|list
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|increaseContainersResource ( IncreaseContainersResourceRequest request)
specifier|public
name|IncreaseContainersResourceResponse
name|increaseContainersResource
parameter_list|(
name|IncreaseContainersResourceRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|signalToContainer ( SignalContainerRequest request)
specifier|public
name|SignalContainerResponse
name|signalToContainer
parameter_list|(
name|SignalContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
specifier|final
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|(
literal|"Dummy function"
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"Dummy function cause"
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|localize ( ResourceLocalizationRequest request)
specifier|public
name|ResourceLocalizationResponse
name|localize
parameter_list|(
name|ResourceLocalizationRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|reInitializeContainer ( ReInitializeContainerRequest request)
specifier|public
name|ReInitializeContainerResponse
name|reInitializeContainer
parameter_list|(
name|ReInitializeContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|restartContainer (ContainerId containerId)
specifier|public
name|RestartContainerResponse
name|restartContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|rollbackLastReInitialization ( ContainerId containerId)
specifier|public
name|RollbackResponse
name|rollbackLastReInitialization
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|commitLastReInitialization (ContainerId containerId)
specifier|public
name|CommitResponse
name|commitLastReInitialization
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|updateContainer (ContainerUpdateRequest request)
specifier|public
name|ContainerUpdateResponse
name|updateContainer
parameter_list|(
name|ContainerUpdateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getLocalizationStatuses ( GetLocalizationStatusesRequest request)
specifier|public
name|GetLocalizationStatusesResponse
name|getLocalizationStatuses
parameter_list|(
name|GetLocalizationStatusesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

