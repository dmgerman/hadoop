begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.transport.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|transport
operator|.
name|server
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|embedded
operator|.
name|EmbeddedChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|ContainerCommandResponseProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConfigKeys
import|;
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
name|OzoneConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|RatisTestHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|ContainerTestHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
operator|.
name|ContainerDispatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServerHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|XceiverServerSpi
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|ratis
operator|.
name|XceiverServerRatis
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|XceiverClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|XceiverClientRatis
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|XceiverClientSpi
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|RatisHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|client
operator|.
name|RaftClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftPeer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|RpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|CheckedBiConsumer
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
name|Ignore
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|SupportedRpcType
operator|.
name|GRPC
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|SupportedRpcType
operator|.
name|NETTY
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

begin_comment
comment|/**  * Test Containers.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Takes too long to run this test. Ignoring for time being."
argument_list|)
DECL|class|TestContainerServer
specifier|public
class|class
name|TestContainerServer
block|{
DECL|field|TEST_DIR
specifier|static
specifier|final
name|String
name|TEST_DIR
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
literal|"dfs"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
decl_stmt|;
annotation|@
name|Test
DECL|method|testPipeline ()
specifier|public
name|void
name|testPipeline
parameter_list|()
throws|throws
name|IOException
block|{
name|EmbeddedChannel
name|channel
init|=
literal|null
decl_stmt|;
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
try|try
block|{
name|channel
operator|=
operator|new
name|EmbeddedChannel
argument_list|(
operator|new
name|XceiverServerHandler
argument_list|(
operator|new
name|TestContainerDispatcher
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|,
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
argument_list|)
decl_stmt|;
name|channel
operator|.
name|writeInbound
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|channel
operator|.
name|finish
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|channel
operator|.
name|readOutbound
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testClientServer ()
specifier|public
name|void
name|testClientServer
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestClientServer
argument_list|(
literal|1
argument_list|,
parameter_list|(
name|pipeline
parameter_list|,
name|conf
parameter_list|)
lambda|->
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getContainerPort
argument_list|()
argument_list|)
argument_list|,
name|XceiverClient
operator|::
operator|new
argument_list|,
parameter_list|(
name|dn
parameter_list|,
name|conf
parameter_list|)
lambda|->
operator|new
name|XceiverServer
argument_list|(
name|conf
argument_list|,
operator|new
name|TestContainerDispatcher
argument_list|()
argument_list|)
argument_list|,
parameter_list|(
name|dn
parameter_list|,
name|p
parameter_list|)
lambda|->
block|{}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|FunctionalInterface
DECL|interface|CheckedBiFunction
interface|interface
name|CheckedBiFunction
parameter_list|<
name|LEFT
parameter_list|,
name|RIGHT
parameter_list|,
name|OUT
parameter_list|,
name|THROWABLE
extends|extends
name|Throwable
parameter_list|>
block|{
DECL|method|apply (LEFT left, RIGHT right)
name|OUT
name|apply
parameter_list|(
name|LEFT
name|left
parameter_list|,
name|RIGHT
name|right
parameter_list|)
throws|throws
name|THROWABLE
function_decl|;
block|}
annotation|@
name|Test
DECL|method|testClientServerRatisNetty ()
specifier|public
name|void
name|testClientServerRatisNetty
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestClientServerRatis
argument_list|(
name|NETTY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|runTestClientServerRatis
argument_list|(
name|NETTY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClientServerRatisGrpc ()
specifier|public
name|void
name|testClientServerRatisGrpc
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestClientServerRatis
argument_list|(
name|GRPC
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|runTestClientServerRatis
argument_list|(
name|GRPC
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|newXceiverServerRatis ( DatanodeID dn, OzoneConfiguration conf)
specifier|static
name|XceiverServerRatis
name|newXceiverServerRatis
parameter_list|(
name|DatanodeID
name|dn
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|id
init|=
name|dn
operator|.
name|getXferAddr
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_IPC_PORT
argument_list|,
name|dn
operator|.
name|getRatisPort
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dir
init|=
name|TEST_DIR
operator|+
name|id
operator|.
name|replace
argument_list|(
literal|':'
argument_list|,
literal|'_'
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_DATANODE_STORAGE_DIR
argument_list|,
name|dir
argument_list|)
expr_stmt|;
specifier|final
name|ContainerDispatcher
name|dispatcher
init|=
operator|new
name|TestContainerDispatcher
argument_list|()
decl_stmt|;
return|return
name|XceiverServerRatis
operator|.
name|newXceiverServerRatis
argument_list|(
name|dn
argument_list|,
name|conf
argument_list|,
name|dispatcher
argument_list|)
return|;
block|}
DECL|method|initXceiverServerRatis ( RpcType rpc, DatanodeID id, Pipeline pipeline)
specifier|static
name|void
name|initXceiverServerRatis
parameter_list|(
name|RpcType
name|rpc
parameter_list|,
name|DatanodeID
name|id
parameter_list|,
name|Pipeline
name|pipeline
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|RaftPeer
name|p
init|=
name|RatisHelper
operator|.
name|toRaftPeer
argument_list|(
name|id
argument_list|)
decl_stmt|;
specifier|final
name|RaftClient
name|client
init|=
name|RatisHelper
operator|.
name|newRaftClient
argument_list|(
name|rpc
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|client
operator|.
name|reinitialize
argument_list|(
name|RatisHelper
operator|.
name|newRaftGroup
argument_list|(
name|pipeline
argument_list|)
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestClientServerRatis (RpcType rpc, int numNodes)
specifier|static
name|void
name|runTestClientServerRatis
parameter_list|(
name|RpcType
name|rpc
parameter_list|,
name|int
name|numNodes
parameter_list|)
throws|throws
name|Exception
block|{
name|runTestClientServer
argument_list|(
name|numNodes
argument_list|,
parameter_list|(
name|pipeline
parameter_list|,
name|conf
parameter_list|)
lambda|->
name|RatisTestHelper
operator|.
name|initRatisConf
argument_list|(
name|rpc
argument_list|,
name|conf
argument_list|)
argument_list|,
name|XceiverClientRatis
operator|::
name|newXceiverClientRatis
argument_list|,
name|TestContainerServer
operator|::
name|newXceiverServerRatis
argument_list|,
parameter_list|(
name|dn
parameter_list|,
name|p
parameter_list|)
lambda|->
name|initXceiverServerRatis
argument_list|(
name|rpc
argument_list|,
name|dn
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestClientServer ( int numDatanodes, BiConsumer<Pipeline, OzoneConfiguration> initConf, CheckedBiFunction<Pipeline, OzoneConfiguration, XceiverClientSpi, IOException> createClient, CheckedBiFunction<DatanodeID, OzoneConfiguration, XceiverServerSpi, IOException> createServer, CheckedBiConsumer<DatanodeID, Pipeline, IOException> initServer)
specifier|static
name|void
name|runTestClientServer
parameter_list|(
name|int
name|numDatanodes
parameter_list|,
name|BiConsumer
argument_list|<
name|Pipeline
argument_list|,
name|OzoneConfiguration
argument_list|>
name|initConf
parameter_list|,
name|CheckedBiFunction
argument_list|<
name|Pipeline
argument_list|,
name|OzoneConfiguration
argument_list|,
name|XceiverClientSpi
argument_list|,
name|IOException
argument_list|>
name|createClient
parameter_list|,
name|CheckedBiFunction
argument_list|<
name|DatanodeID
argument_list|,
name|OzoneConfiguration
argument_list|,
name|XceiverServerSpi
argument_list|,
name|IOException
argument_list|>
name|createServer
parameter_list|,
name|CheckedBiConsumer
argument_list|<
name|DatanodeID
argument_list|,
name|Pipeline
argument_list|,
name|IOException
argument_list|>
name|initServer
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|XceiverServerSpi
argument_list|>
name|servers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|XceiverClientSpi
name|client
init|=
literal|null
decl_stmt|;
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createPipeline
argument_list|(
name|containerName
argument_list|,
name|numDatanodes
argument_list|)
decl_stmt|;
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|initConf
operator|.
name|accept
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
expr_stmt|;
for|for
control|(
name|DatanodeID
name|dn
range|:
name|pipeline
operator|.
name|getMachines
argument_list|()
control|)
block|{
specifier|final
name|XceiverServerSpi
name|s
init|=
name|createServer
operator|.
name|apply
argument_list|(
name|dn
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|servers
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|s
operator|.
name|start
argument_list|()
expr_stmt|;
name|initServer
operator|.
name|accept
argument_list|(
name|dn
argument_list|,
name|pipeline
argument_list|)
expr_stmt|;
block|}
name|client
operator|=
name|createClient
operator|.
name|apply
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
specifier|final
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|,
name|pipeline
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|,
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|servers
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|XceiverServerSpi
operator|::
name|stop
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testClientServerWithContainerDispatcher ()
specifier|public
name|void
name|testClientServerWithContainerDispatcher
parameter_list|()
throws|throws
name|Exception
block|{
name|XceiverServer
name|server
init|=
literal|null
decl_stmt|;
name|XceiverClient
name|client
init|=
literal|null
decl_stmt|;
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
try|try
block|{
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT
argument_list|,
name|pipeline
operator|.
name|getLeader
argument_list|()
operator|.
name|getContainerPort
argument_list|()
argument_list|)
expr_stmt|;
name|Dispatcher
name|dispatcher
init|=
operator|new
name|Dispatcher
argument_list|(
name|mock
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|init
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|XceiverServer
argument_list|(
name|conf
argument_list|,
name|dispatcher
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|XceiverClient
argument_list|(
name|pipeline
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerName
argument_list|,
name|pipeline
argument_list|)
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|request
operator|.
name|getTraceID
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTraceID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|SUCCESS
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dispatcher
operator|.
name|getContainerMetrics
argument_list|()
operator|.
name|getContainerOpsMetrics
argument_list|(
name|ContainerProtos
operator|.
name|Type
operator|.
name|CreateContainer
argument_list|)
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|TestContainerDispatcher
specifier|private
specifier|static
class|class
name|TestContainerDispatcher
implements|implements
name|ContainerDispatcher
block|{
comment|/**      * Dispatches commands to container layer.      *      * @param msg - Command Request      * @return Command Response      */
annotation|@
name|Override
specifier|public
name|ContainerCommandResponseProto
DECL|method|dispatch (ContainerCommandRequestProto msg)
name|dispatch
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
return|return
name|ContainerTestHelper
operator|.
name|getCreateContainerResponse
argument_list|(
name|msg
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

