begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
package|;
end_package

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
name|ExpectedException
import|;
end_import

begin_comment
comment|/**  * Test Container calls.  */
end_comment

begin_class
DECL|class|TestContainerSmallFile
specifier|public
class|class
name|TestContainerSmallFile
block|{
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
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
throws|throws
name|InterruptedException
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
DECL|method|testAllocateWrite ()
specifier|public
name|void
name|testAllocateWrite
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
name|xceiverClientManager
operator|.
name|getType
argument_list|()
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
name|XceiverClientSpi
name|client
init|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|container
operator|.
name|getPipeline
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client
argument_list|,
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|BlockID
name|blockID
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|writeSmallFile
argument_list|(
name|client
argument_list|,
name|blockID
argument_list|,
literal|"data123"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|GetSmallFileResponseProto
name|response
init|=
name|ContainerProtocolCalls
operator|.
name|readSmallFile
argument_list|(
name|client
argument_list|,
name|blockID
argument_list|)
decl_stmt|;
name|String
name|readData
init|=
name|response
operator|.
name|getData
argument_list|()
operator|.
name|getData
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"data123"
argument_list|,
name|readData
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
DECL|method|testInvalidBlockRead ()
specifier|public
name|void
name|testInvalidBlockRead
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
name|xceiverClientManager
operator|.
name|getType
argument_list|()
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
name|XceiverClientSpi
name|client
init|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|container
operator|.
name|getPipeline
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client
argument_list|,
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|StorageContainerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"Unable to find the block"
argument_list|)
expr_stmt|;
name|BlockID
name|blockID
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
comment|// Try to read a Key Container Name
name|ContainerProtos
operator|.
name|GetSmallFileResponseProto
name|response
init|=
name|ContainerProtocolCalls
operator|.
name|readSmallFile
argument_list|(
name|client
argument_list|,
name|blockID
argument_list|)
decl_stmt|;
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
annotation|@
name|Test
DECL|method|testInvalidContainerRead ()
specifier|public
name|void
name|testInvalidContainerRead
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|nonExistContainerID
init|=
literal|8888L
decl_stmt|;
name|ContainerWithPipeline
name|container
init|=
name|storageContainerLocationClient
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
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
name|XceiverClientSpi
name|client
init|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|container
operator|.
name|getPipeline
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client
argument_list|,
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|BlockID
name|blockID
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|writeSmallFile
argument_list|(
name|client
argument_list|,
name|blockID
argument_list|,
literal|"data123"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|StorageContainerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"ContainerID 8888 does not exist"
argument_list|)
expr_stmt|;
comment|// Try to read a invalid key
name|ContainerProtos
operator|.
name|GetSmallFileResponseProto
name|response
init|=
name|ContainerProtocolCalls
operator|.
name|readSmallFile
argument_list|(
name|client
argument_list|,
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|nonExistContainerID
argument_list|)
argument_list|)
decl_stmt|;
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
annotation|@
name|Test
DECL|method|testReadWriteWithBCSId ()
specifier|public
name|void
name|testReadWriteWithBCSId
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
name|XceiverClientSpi
name|client
init|=
name|xceiverClientManager
operator|.
name|acquireClient
argument_list|(
name|container
operator|.
name|getPipeline
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|createContainer
argument_list|(
name|client
argument_list|,
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|BlockID
name|blockID1
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtos
operator|.
name|PutSmallFileResponseProto
name|responseProto
init|=
name|ContainerProtocolCalls
operator|.
name|writeSmallFile
argument_list|(
name|client
argument_list|,
name|blockID1
argument_list|,
literal|"data123"
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|bcsId
init|=
name|responseProto
operator|.
name|getCommittedBlockLength
argument_list|()
operator|.
name|getBlockID
argument_list|()
operator|.
name|getBlockCommitSequenceId
argument_list|()
decl_stmt|;
try|try
block|{
name|blockID1
operator|.
name|setBlockCommitSequenceId
argument_list|(
name|bcsId
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//read a file with higher bcsId than the container bcsId
name|ContainerProtocolCalls
operator|.
name|readSmallFile
argument_list|(
name|client
argument_list|,
name|blockID1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected exception not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|sce
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sce
operator|.
name|getResult
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|Result
operator|.
name|UNKNOWN_BCSID
argument_list|)
expr_stmt|;
block|}
comment|// write a new block again to bump up the container bcsId
name|BlockID
name|blockID2
init|=
name|ContainerTestHelper
operator|.
name|getTestBlockID
argument_list|(
name|container
operator|.
name|getContainerInfo
argument_list|()
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerProtocolCalls
operator|.
name|writeSmallFile
argument_list|(
name|client
argument_list|,
name|blockID2
argument_list|,
literal|"data123"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|blockID1
operator|.
name|setBlockCommitSequenceId
argument_list|(
name|bcsId
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//read a file with higher bcsId than the committed bcsId for the block
name|ContainerProtocolCalls
operator|.
name|readSmallFile
argument_list|(
name|client
argument_list|,
name|blockID1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected exception not thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|sce
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sce
operator|.
name|getResult
argument_list|()
operator|==
name|ContainerProtos
operator|.
name|Result
operator|.
name|BCSID_MISMATCH
argument_list|)
expr_stmt|;
block|}
name|blockID1
operator|.
name|setBlockCommitSequenceId
argument_list|(
name|bcsId
argument_list|)
expr_stmt|;
name|ContainerProtos
operator|.
name|GetSmallFileResponseProto
name|response
init|=
name|ContainerProtocolCalls
operator|.
name|readSmallFile
argument_list|(
name|client
argument_list|,
name|blockID1
argument_list|)
decl_stmt|;
name|String
name|readData
init|=
name|response
operator|.
name|getData
argument_list|()
operator|.
name|getData
argument_list|()
operator|.
name|toStringUtf8
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"data123"
argument_list|,
name|readData
argument_list|)
expr_stmt|;
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

