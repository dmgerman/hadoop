begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|lang3
operator|.
name|RandomUtils
import|;
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
name|proto
operator|.
name|HddsProtos
import|;
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
name|XceiverClientManager
import|;
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerWithPipeline
import|;
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|algorithms
operator|.
name|ContainerPlacementPolicy
import|;
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
name|placement
operator|.
name|algorithms
operator|.
name|SCMContainerPlacementCapacity
import|;
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
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
import|;
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
name|storage
operator|.
name|ContainerProtocolCalls
import|;
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
name|IOUtils
import|;
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
name|junit
operator|.
name|AfterClass
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
name|BeforeClass
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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Tests the idempotent operations in ContainerStateMachine.  */
end_comment

begin_class
DECL|class|TestContainerStateMachineIdempotency
specifier|public
class|class
name|TestContainerStateMachineIdempotency
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|ozoneConfig
specifier|private
specifier|static
name|OzoneConfiguration
name|ozoneConfig
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|static
name|XceiverClientManager
name|xceiverClientManager
decl_stmt|;
DECL|field|containerOwner
specifier|private
specifier|static
name|String
name|containerOwner
init|=
literal|"OZONE"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|ozoneConfig
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|ozoneConfig
operator|.
name|setClass
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_KEY
argument_list|,
name|SCMContainerPlacementCapacity
operator|.
name|class
argument_list|,
name|ContainerPlacementPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|cluster
operator|=
name|MiniOzoneCluster
operator|.
name|newBuilder
argument_list|(
name|ozoneConfig
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|getStorageContainerLocationClient
argument_list|()
expr_stmt|;
name|xceiverClientManager
operator|=
operator|new
name|XceiverClientManager
argument_list|(
name|ozoneConfig
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|storageContainerLocationClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testContainerStateMachineIdempotency ()
specifier|public
name|void
name|testContainerStateMachineIdempotency
parameter_list|()
throws|throws
name|Exception
block|{
name|ContainerWithPipeline
name|container
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|HddsProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
argument_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|long
name|containerID
init|=
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
name|container
operator|.
name|getPipeline
argument_list|()
decl_stmt|;
name|XceiverClientSpi
name|client
init|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
try|try
block|{
comment|//create the container
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client
argument_list|,
name|containerID
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// call create Container again
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
name|byte
index|[]
name|data
init|=
name|RandomStringUtils
operator|.
name|random
argument_list|(
name|RandomUtils
operator|.
name|nextInt
argument_list|(
literal|0
argument_list|,
literal|1024
argument_list|)
argument_list|)
operator|.
name|getBytes
argument_list|()
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
name|container
operator|.
name|getPipeline
argument_list|()
argument_list|,
name|blockID
argument_list|,
name|data
operator|.
name|length
argument_list|)
decl_stmt|;
name|client
operator|.
name|sendCommand
argument_list|(
name|writeChunkRequest
argument_list|)
expr_stmt|;
comment|//Make the write chunk request again without requesting for overWrite
name|client
operator|.
name|sendCommand
argument_list|(
name|writeChunkRequest
argument_list|)
expr_stmt|;
comment|// Now, explicitly make a putKey request for the block.
name|ContainerProtos
operator|.
name|ContainerCommandRequestProto
name|putKeyRequest
init|=
name|ContainerTestHelper
operator|.
name|getPutBlockRequest
argument_list|(
name|pipeline
argument_list|,
name|writeChunkRequest
operator|.
name|getWriteChunk
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|sendCommand
argument_list|(
name|putKeyRequest
argument_list|)
operator|.
name|getPutBlock
argument_list|()
expr_stmt|;
comment|// send the putBlock again
name|client
operator|.
name|sendCommand
argument_list|(
name|putKeyRequest
argument_list|)
expr_stmt|;
comment|// close container call
name|ContainerProtocolCalls
operator|.
name|closeContainer
argument_list|(
name|client
argument_list|,
name|containerID
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ContainerProtocolCalls
operator|.
name|closeContainer
argument_list|(
name|client
argument_list|,
name|containerID
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Container operation failed"
operator|+
name|ioe
argument_list|)
expr_stmt|;
block|}
name|xceiverClientManager
operator|.
name|releaseClient
argument_list|(
name|client
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

