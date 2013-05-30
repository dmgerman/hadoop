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
name|Container
import|;
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
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
name|ContainerManager
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
name|ContainerManager
name|proxy
init|=
operator|(
name|ContainerManager
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ContainerManager
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
name|ContainerId
name|containerId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
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
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|applicationAttemptId
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|applicationAttemptId
operator|.
name|setAttemptId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|containerId
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|containerId
operator|.
name|setId
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Container
name|container
init|=
name|BuilderUtils
operator|.
name|newContainer
argument_list|(
name|containerId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|StartContainerRequest
name|scRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|scRequest
operator|.
name|setContainerLaunchContext
argument_list|(
name|containerLaunchContext
argument_list|)
expr_stmt|;
name|scRequest
operator|.
name|setContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
try|try
block|{
name|proxy
operator|.
name|startContainer
argument_list|(
name|scRequest
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
DECL|class|DummyContainerManager
specifier|public
class|class
name|DummyContainerManager
implements|implements
name|ContainerManager
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
name|GetContainerStatusResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetContainerStatusResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|startContainer (StartContainerRequest request)
specifier|public
name|StartContainerResponse
name|startContainer
parameter_list|(
name|StartContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
block|{
name|StartContainerResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|status
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
expr_stmt|;
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
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|status
operator|.
name|setState
argument_list|(
name|ContainerState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|status
operator|.
name|setContainerId
argument_list|(
name|request
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|.
name|setExitStatus
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|response
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
name|YarnRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

