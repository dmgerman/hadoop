begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.transport.server.ratis
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
name|common
operator|.
name|transport
operator|.
name|server
operator|.
name|ratis
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|client
operator|.
name|BlockID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|datanode
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
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
name|hdds
operator|.
name|scm
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|pipeline
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
name|metrics2
operator|.
name|MetricsRecordBuilder
import|;
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
name|Handler
import|;
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
name|hadoop
operator|.
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftGroupId
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
name|function
operator|.
name|CheckedBiConsumer
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
name|function
operator|.
name|BiConsumer
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
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * This class tests the metrics of ContainerStateMachine.  */
end_comment

begin_class
DECL|class|TestCSMMetrics
specifier|public
class|class
name|TestCSMMetrics
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
DECL|method|testContainerStateMachineMetrics ()
specifier|public
name|void
name|testContainerStateMachineMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|runContainerStateMachineMetrics
argument_list|(
literal|1
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
name|GRPC
argument_list|,
name|conf
argument_list|)
argument_list|,
name|XceiverClientRatis
operator|::
name|newXceiverClientRatis
argument_list|,
name|TestCSMMetrics
operator|::
name|newXceiverServerRatis
argument_list|,
parameter_list|(
name|dn
parameter_list|,
name|p
parameter_list|)
lambda|->
name|RatisTestHelper
operator|.
name|initXceiverServerRatis
argument_list|(
name|GRPC
argument_list|,
name|dn
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|runContainerStateMachineMetrics ( int numDatanodes, BiConsumer<Pipeline, OzoneConfiguration> initConf, TestCSMMetrics.CheckedBiFunction<Pipeline, OzoneConfiguration, XceiverClientSpi, IOException> createClient, TestCSMMetrics.CheckedBiFunction<DatanodeDetails, OzoneConfiguration, XceiverServerSpi, IOException> createServer, CheckedBiConsumer<DatanodeDetails, Pipeline, IOException> initServer)
specifier|static
name|void
name|runContainerStateMachineMetrics
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
name|TestCSMMetrics
operator|.
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
name|TestCSMMetrics
operator|.
name|CheckedBiFunction
argument_list|<
name|DatanodeDetails
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
name|DatanodeDetails
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
name|DatanodeDetails
name|dn
range|:
name|pipeline
operator|.
name|getNodes
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
comment|// Before Read Chunk/Write Chunk
name|MetricsRecordBuilder
name|metric
init|=
name|getMetrics
argument_list|(
name|CSMMetrics
operator|.
name|SOURCE_NAME
operator|+
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertCounter
argument_list|(
literal|"NumWriteStateMachineOps"
argument_list|,
literal|0L
argument_list|,
name|metric
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumReadStateMachineOps"
argument_list|,
literal|0L
argument_list|,
name|metric
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumApplyTransactionOps"
argument_list|,
literal|0L
argument_list|,
name|metric
argument_list|)
expr_stmt|;
comment|// Write Chunk
name|BlockID
name|blockID
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|ContainerTestHelper
operator|.
name|getTestContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|writeChunkRequest
init|=
name|ContainerTestHelper
operator|.
name|getWriteChunkRequest
argument_list|(
name|pipeline
argument_list|,
name|blockID
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|writeChunkRequest
argument_list|)
decl_stmt|;
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
name|metric
operator|=
name|getMetrics
argument_list|(
name|CSMMetrics
operator|.
name|SOURCE_NAME
operator|+
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumWriteStateMachineOps"
argument_list|,
literal|1L
argument_list|,
name|metric
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumApplyTransactionOps"
argument_list|,
literal|1L
argument_list|,
name|metric
argument_list|)
expr_stmt|;
comment|//Read Chunk
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|readChunkRequest
init|=
name|ContainerTestHelper
operator|.
name|getReadChunkRequest
argument_list|(
name|pipeline
argument_list|,
name|writeChunkRequest
operator|.
name|getWriteChunk
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|readChunkRequest
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
name|metric
operator|=
name|getMetrics
argument_list|(
name|CSMMetrics
operator|.
name|SOURCE_NAME
operator|+
name|RaftGroupId
operator|.
name|valueOf
argument_list|(
name|pipeline
operator|.
name|getId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumReadStateMachineOps"
argument_list|,
literal|1L
argument_list|,
name|metric
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"NumApplyTransactionOps"
argument_list|,
literal|1L
argument_list|,
name|metric
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
DECL|method|newXceiverServerRatis ( DatanodeDetails dn, OzoneConfiguration conf)
specifier|static
name|XceiverServerRatis
name|newXceiverServerRatis
parameter_list|(
name|DatanodeDetails
name|dn
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
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
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|RATIS
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dir
init|=
name|TEST_DIR
operator|+
name|dn
operator|.
name|getUuid
argument_list|()
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
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
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
DECL|method|dispatch ( ContainerCommandRequestProto msg, DispatcherContext context)
specifier|public
name|ContainerCommandResponseProto
name|dispatch
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|DispatcherContext
name|context
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
DECL|method|validateContainerCommand ( ContainerCommandRequestProto msg)
specifier|public
name|void
name|validateContainerCommand
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|)
throws|throws
name|StorageContainerException
block|{     }
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
annotation|@
name|Override
DECL|method|getHandler (ContainerProtos.ContainerType containerType)
specifier|public
name|Handler
name|getHandler
parameter_list|(
name|ContainerProtos
operator|.
name|ContainerType
name|containerType
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setScmId (String scmId)
specifier|public
name|void
name|setScmId
parameter_list|(
name|String
name|scmId
parameter_list|)
block|{      }
annotation|@
name|Override
DECL|method|buildMissingContainerSet (Set<Long> createdContainerSet)
specifier|public
name|void
name|buildMissingContainerSet
parameter_list|(
name|Set
argument_list|<
name|Long
argument_list|>
name|createdContainerSet
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

