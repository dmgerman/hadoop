begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.metrics
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
name|metrics
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
name|assertQuantileGauges
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
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
name|FileUtil
import|;
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
name|scm
operator|.
name|ScmConfigKeys
import|;
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
name|XceiverClientGrpc
import|;
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
name|DFSConfigKeys
import|;
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
name|helpers
operator|.
name|ContainerMetrics
import|;
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
name|ContainerSet
import|;
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
name|HddsDispatcher
import|;
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
name|statemachine
operator|.
name|DatanodeStateMachine
import|;
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
name|statemachine
operator|.
name|StateContext
import|;
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
name|XceiverServerGrpc
import|;
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
name|volume
operator|.
name|VolumeSet
import|;
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
name|TestUtils
import|;
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
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
operator|.
name|ContainerController
import|;
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
name|replication
operator|.
name|GrpcReplicationService
import|;
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
name|replication
operator|.
name|OnDemandContainerReplicationSource
import|;
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|UUID
import|;
end_import

begin_comment
comment|/**  * Test for metrics published by storage containers.  */
end_comment

begin_class
DECL|class|TestContainerMetrics
specifier|public
class|class
name|TestContainerMetrics
block|{
DECL|method|createReplicationService ( ContainerController controller)
specifier|private
name|GrpcReplicationService
name|createReplicationService
parameter_list|(
name|ContainerController
name|controller
parameter_list|)
block|{
return|return
operator|new
name|GrpcReplicationService
argument_list|(
operator|new
name|OnDemandContainerReplicationSource
argument_list|(
name|controller
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testContainerMetrics ()
specifier|public
name|void
name|testContainerMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|XceiverServerGrpc
name|server
init|=
literal|null
decl_stmt|;
name|XceiverClientGrpc
name|client
init|=
literal|null
decl_stmt|;
name|long
name|containerID
init|=
name|ContainerTestHelper
operator|.
name|getTestContainerID
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|GenericTestUtils
operator|.
name|getRandomizedTempPath
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|interval
init|=
literal|1
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createSingleNodePipeline
argument_list|()
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
name|getFirstNode
argument_list|()
operator|.
name|getPort
argument_list|(
name|DatanodeDetails
operator|.
name|Port
operator|.
name|Name
operator|.
name|STANDALONE
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_PERCENTILES_INTERVALS_KEY
argument_list|,
name|interval
argument_list|)
expr_stmt|;
name|DatanodeDetails
name|datanodeDetails
init|=
name|TestUtils
operator|.
name|randomDatanodeDetails
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|HDDS_DATANODE_DIR_KEY
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|VolumeSet
name|volumeSet
init|=
operator|new
name|VolumeSet
argument_list|(
name|datanodeDetails
operator|.
name|getUuidString
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ContainerSet
name|containerSet
init|=
operator|new
name|ContainerSet
argument_list|()
decl_stmt|;
name|DatanodeStateMachine
name|stateMachine
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeStateMachine
operator|.
name|class
argument_list|)
decl_stmt|;
name|StateContext
name|context
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|stateMachine
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|context
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|stateMachine
argument_list|)
expr_stmt|;
name|ContainerMetrics
name|metrics
init|=
name|ContainerMetrics
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ContainerProtos
operator|.
name|ContainerType
argument_list|,
name|Handler
argument_list|>
name|handlers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerProtos
operator|.
name|ContainerType
name|containerType
range|:
name|ContainerProtos
operator|.
name|ContainerType
operator|.
name|values
argument_list|()
control|)
block|{
name|handlers
operator|.
name|put
argument_list|(
name|containerType
argument_list|,
name|Handler
operator|.
name|getHandlerForContainerType
argument_list|(
name|containerType
argument_list|,
name|conf
argument_list|,
name|context
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|,
name|metrics
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HddsDispatcher
name|dispatcher
init|=
operator|new
name|HddsDispatcher
argument_list|(
name|conf
argument_list|,
name|containerSet
argument_list|,
name|volumeSet
argument_list|,
name|handlers
argument_list|,
name|context
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|setScmId
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|XceiverServerGrpc
argument_list|(
name|datanodeDetails
argument_list|,
name|conf
argument_list|,
name|dispatcher
argument_list|,
literal|null
argument_list|,
name|createReplicationService
argument_list|(
operator|new
name|ContainerController
argument_list|(
name|containerSet
argument_list|,
name|handlers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|XceiverClientGrpc
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
comment|// Create container
name|ContainerCommandRequestProto
name|request
init|=
name|ContainerTestHelper
operator|.
name|getCreateContainerRequest
argument_list|(
name|containerID
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
comment|// Write Chunk
name|BlockID
name|blockID
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
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
expr_stmt|;
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
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|writeChunkRequest
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
name|MetricsRecordBuilder
name|containerMetrics
init|=
name|getMetrics
argument_list|(
literal|"StorageContainerMetrics"
argument_list|)
decl_stmt|;
name|assertCounter
argument_list|(
literal|"NumOps"
argument_list|,
literal|3L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"numCreateContainer"
argument_list|,
literal|1L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"numWriteChunk"
argument_list|,
literal|1L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"numReadChunk"
argument_list|,
literal|1L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"bytesWriteChunk"
argument_list|,
literal|1024L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"bytesReadChunk"
argument_list|,
literal|1024L
argument_list|,
name|containerMetrics
argument_list|)
expr_stmt|;
name|String
name|sec
init|=
name|interval
operator|+
literal|"s"
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
operator|(
name|interval
operator|+
literal|1
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|assertQuantileGauges
argument_list|(
literal|"WriteChunkNanos"
operator|+
name|sec
argument_list|,
name|containerMetrics
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
comment|// clean up volume dir
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

