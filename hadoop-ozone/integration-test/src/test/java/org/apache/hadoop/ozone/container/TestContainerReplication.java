begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container
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
package|;
end_package

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
name|ContainerType
import|;
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
name|DatanodeBlockID
import|;
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
name|hdds
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
name|HddsDatanodeService
import|;
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
name|MiniOzoneCluster
import|;
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
name|BlockData
import|;
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
name|keyvalue
operator|.
name|KeyValueHandler
import|;
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
name|OzoneContainer
import|;
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
name|TestOzoneContainer
import|;
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
name|protocol
operator|.
name|commands
operator|.
name|ReplicateContainerCommand
import|;
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
import|import static
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
name|TestOzoneContainer
operator|.
name|writeChunkForContainer
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
name|Rule
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
name|rules
operator|.
name|Timeout
import|;
end_import

begin_comment
comment|/**  * Tests ozone containers replication.  */
end_comment

begin_class
DECL|class|TestContainerReplication
specifier|public
class|class
name|TestContainerReplication
block|{
comment|/**    * Set the timeout for every test.    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testContainerReplication ()
specifier|public
name|void
name|testContainerReplication
parameter_list|()
throws|throws
name|Exception
block|{
comment|//GIVEN
name|OzoneConfiguration
name|conf
init|=
name|newOzoneConfiguration
argument_list|()
decl_stmt|;
name|long
name|containerId
init|=
literal|1L
decl_stmt|;
name|MiniOzoneCluster
name|cluster
init|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|2
argument_list|)
operator|.
name|setRandomContainerPort
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|HddsDatanodeService
name|firstDatanode
init|=
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//copy from the first datanode
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|sourceDatanodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|sourceDatanodes
operator|.
name|add
argument_list|(
name|firstDatanode
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
expr_stmt|;
name|Pipeline
name|sourcePipelines
init|=
name|ContainerTestHelper
operator|.
name|createPipeline
argument_list|(
name|sourceDatanodes
argument_list|)
decl_stmt|;
comment|//create a new client
name|XceiverClientSpi
name|client
init|=
operator|new
name|XceiverClientGrpc
argument_list|(
name|sourcePipelines
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|//New container for testing
name|TestOzoneContainer
operator|.
name|createContainerForTesting
argument_list|(
name|client
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|ContainerCommandRequestProto
name|requestProto
init|=
name|writeChunkForContainer
argument_list|(
name|client
argument_list|,
name|containerId
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|DatanodeBlockID
name|blockID
init|=
name|requestProto
operator|.
name|getWriteChunk
argument_list|()
operator|.
name|getBlockID
argument_list|()
decl_stmt|;
comment|// Put Block to the test container
name|ContainerCommandRequestProto
name|putBlockRequest
init|=
name|ContainerTestHelper
operator|.
name|getPutBlockRequest
argument_list|(
name|sourcePipelines
argument_list|,
name|requestProto
operator|.
name|getWriteChunk
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerCommandResponseProto
name|response
init|=
name|client
operator|.
name|sendCommand
argument_list|(
name|putBlockRequest
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
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
name|HddsDatanodeService
name|destinationDatanode
init|=
name|chooseDatanodeWithoutContainer
argument_list|(
name|sourcePipelines
argument_list|,
name|cluster
operator|.
name|getHddsDatanodes
argument_list|()
argument_list|)
decl_stmt|;
comment|// Close the container
name|ContainerCommandRequestProto
name|closeContainerRequest
init|=
name|ContainerTestHelper
operator|.
name|getCloseContainer
argument_list|(
name|sourcePipelines
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
name|response
operator|=
name|client
operator|.
name|sendCommand
argument_list|(
name|closeContainerRequest
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|response
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
comment|//WHEN: send the order to replicate the container
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|addDatanodeCommand
argument_list|(
name|destinationDatanode
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|,
operator|new
name|ReplicateContainerCommand
argument_list|(
name|containerId
argument_list|,
name|sourcePipelines
operator|.
name|getNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|DatanodeStateMachine
name|destinationDatanodeDatanodeStateMachine
init|=
name|destinationDatanode
operator|.
name|getDatanodeStateMachine
argument_list|()
decl_stmt|;
comment|//wait for the replication
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|destinationDatanodeDatanodeStateMachine
operator|.
name|getSupervisor
argument_list|()
operator|.
name|getReplicationCounter
argument_list|()
operator|>
literal|0
argument_list|,
literal|1000
argument_list|,
literal|20_000
argument_list|)
expr_stmt|;
name|OzoneContainer
name|ozoneContainer
init|=
name|destinationDatanodeDatanodeStateMachine
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
name|ozoneContainer
operator|.
name|getContainerSet
argument_list|()
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Container is not replicated to the destination datanode"
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"ContainerData of the replicated container is null"
argument_list|,
name|container
operator|.
name|getContainerData
argument_list|()
argument_list|)
expr_stmt|;
name|KeyValueHandler
name|handler
init|=
operator|(
name|KeyValueHandler
operator|)
name|ozoneContainer
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getHandler
argument_list|(
name|ContainerType
operator|.
name|KeyValueContainer
argument_list|)
decl_stmt|;
name|BlockData
name|key
init|=
name|handler
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getBlock
argument_list|(
name|container
argument_list|,
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|blockID
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|key
operator|.
name|getChunks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|requestProto
operator|.
name|getWriteChunk
argument_list|()
operator|.
name|getChunkData
argument_list|()
argument_list|,
name|key
operator|.
name|getChunks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|chooseDatanodeWithoutContainer (Pipeline pipeline, List<HddsDatanodeService> dataNodes)
specifier|private
name|HddsDatanodeService
name|chooseDatanodeWithoutContainer
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|List
argument_list|<
name|HddsDatanodeService
argument_list|>
name|dataNodes
parameter_list|)
block|{
for|for
control|(
name|HddsDatanodeService
name|datanode
range|:
name|dataNodes
control|)
block|{
if|if
condition|(
operator|!
name|pipeline
operator|.
name|getNodes
argument_list|()
operator|.
name|contains
argument_list|(
name|datanode
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|datanode
return|;
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"No datanode outside of the pipeline"
argument_list|)
throw|;
block|}
DECL|method|newOzoneConfiguration ()
specifier|private
specifier|static
name|OzoneConfiguration
name|newOzoneConfiguration
parameter_list|()
block|{
return|return
operator|new
name|OzoneConfiguration
argument_list|()
return|;
block|}
block|}
end_class

end_unit

