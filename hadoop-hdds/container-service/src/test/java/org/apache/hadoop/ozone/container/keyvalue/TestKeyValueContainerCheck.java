begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue
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
name|keyvalue
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
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
name|conf
operator|.
name|StorageUnit
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
name|hdfs
operator|.
name|DFSUtil
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
name|OzoneConsts
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
name|helpers
operator|.
name|ChunkInfo
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
name|DispatcherContext
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
name|impl
operator|.
name|ChunkManagerImpl
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
name|RoundRobinVolumeChoosingPolicy
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
name|ozone
operator|.
name|container
operator|.
name|keyvalue
operator|.
name|helpers
operator|.
name|BlockUtils
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|utils
operator|.
name|ContainerCache
operator|.
name|ReferenceCountedDB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|Arrays
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|UUID
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|HDDS_DATANODE_DIR_KEY
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
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL
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
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL_LEVELDB
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
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Basic sanity test for the KeyValueContainerCheck class.  */
end_comment

begin_class
DECL|class|TestKeyValueContainerCheck
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|TestKeyValueContainerCheck
block|{
DECL|field|storeImpl
specifier|private
specifier|final
name|String
name|storeImpl
decl_stmt|;
DECL|field|container
specifier|private
name|KeyValueContainer
name|container
decl_stmt|;
DECL|field|containerData
specifier|private
name|KeyValueContainerData
name|containerData
decl_stmt|;
DECL|field|chunkManager
specifier|private
name|ChunkManagerImpl
name|chunkManager
decl_stmt|;
DECL|field|volumeSet
specifier|private
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|testRoot
specifier|private
name|File
name|testRoot
decl_stmt|;
DECL|method|TestKeyValueContainerCheck (String metadataImpl)
specifier|public
name|TestKeyValueContainerCheck
parameter_list|(
name|String
name|metadataImpl
parameter_list|)
block|{
name|this
operator|.
name|storeImpl
operator|=
name|metadataImpl
expr_stmt|;
block|}
DECL|method|data ()
annotation|@
name|Parameterized
operator|.
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|OZONE_METADATA_STORE_IMPL_LEVELDB
block|}
block|,
block|{
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|setUp ()
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|testRoot
operator|=
name|GenericTestUtils
operator|.
name|getRandomizedTestDir
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HDDS_DATANODE_DIR_KEY
argument_list|,
name|testRoot
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OZONE_METADATA_STORE_IMPL
argument_list|,
name|storeImpl
argument_list|)
expr_stmt|;
name|volumeSet
operator|=
operator|new
name|VolumeSet
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|teardown ()
annotation|@
name|After
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|volumeSet
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testRoot
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sanity test, when there are no corruptions induced.    * @throws Exception    */
DECL|method|testKeyValueContainerCheckNoCorruption ()
annotation|@
name|Test
specifier|public
name|void
name|testKeyValueContainerCheckNoCorruption
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|containerID
init|=
literal|101
decl_stmt|;
name|int
name|deletedBlocks
init|=
literal|1
decl_stmt|;
name|int
name|normalBlocks
init|=
literal|3
decl_stmt|;
name|int
name|chunksPerBlock
init|=
literal|4
decl_stmt|;
name|boolean
name|corruption
init|=
literal|false
decl_stmt|;
comment|// test Closed Container
name|createContainerWithBlocks
argument_list|(
name|containerID
argument_list|,
name|normalBlocks
argument_list|,
name|deletedBlocks
argument_list|,
literal|65536
argument_list|,
name|chunksPerBlock
argument_list|)
expr_stmt|;
name|File
name|chunksPath
init|=
operator|new
name|File
argument_list|(
name|containerData
operator|.
name|getChunksPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|chunksPath
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|==
operator|(
name|deletedBlocks
operator|+
name|normalBlocks
operator|)
operator|*
name|chunksPerBlock
argument_list|)
expr_stmt|;
name|KeyValueContainerCheck
name|kvCheck
init|=
operator|new
name|KeyValueContainerCheck
argument_list|(
name|containerData
operator|.
name|getMetadataPath
argument_list|()
argument_list|,
name|conf
argument_list|,
name|containerID
argument_list|)
decl_stmt|;
comment|// first run checks on a Open Container
name|corruption
operator|=
name|kvCheck
operator|.
name|fastCheck
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|corruption
argument_list|)
expr_stmt|;
name|container
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// next run checks on a Closed Container
name|corruption
operator|=
name|kvCheck
operator|.
name|fullCheck
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|corruption
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a container with normal and deleted blocks.    * First it will insert normal blocks, and then it will insert    * deleted blocks.    * @param containerId    * @param normalBlocks    * @param deletedBlocks    * @throws Exception    */
DECL|method|createContainerWithBlocks (long containerId, int normalBlocks, int deletedBlocks, long chunkLen, int chunksPerBlock)
specifier|private
name|void
name|createContainerWithBlocks
parameter_list|(
name|long
name|containerId
parameter_list|,
name|int
name|normalBlocks
parameter_list|,
name|int
name|deletedBlocks
parameter_list|,
name|long
name|chunkLen
parameter_list|,
name|int
name|chunksPerBlock
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|chunkCount
decl_stmt|;
name|String
name|strBlock
init|=
literal|"block"
decl_stmt|;
name|String
name|strChunk
init|=
literal|"-chunkFile"
decl_stmt|;
name|byte
index|[]
name|chunkData
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|chunkLen
index|]
decl_stmt|;
name|long
name|totalBlks
init|=
name|normalBlocks
operator|+
name|deletedBlocks
decl_stmt|;
name|containerData
operator|=
operator|new
name|KeyValueContainerData
argument_list|(
name|containerId
argument_list|,
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|BYTES
operator|.
name|toBytes
argument_list|(
name|chunksPerBlock
operator|*
name|chunkLen
operator|*
name|totalBlks
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|=
operator|new
name|KeyValueContainer
argument_list|(
name|containerData
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|container
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
operator|new
name|RoundRobinVolumeChoosingPolicy
argument_list|()
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|ReferenceCountedDB
name|metadataStore
init|=
name|BlockUtils
operator|.
name|getDB
argument_list|(
name|containerData
argument_list|,
name|conf
argument_list|)
init|)
block|{
name|chunkManager
operator|=
operator|new
name|ChunkManagerImpl
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|containerData
operator|.
name|getChunksPath
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|File
name|chunksPath
init|=
operator|new
name|File
argument_list|(
name|containerData
operator|.
name|getChunksPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|chunksPath
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Initially chunks folder should be empty.
name|assertTrue
argument_list|(
name|chunksPath
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ContainerProtos
operator|.
name|ChunkInfo
argument_list|>
name|chunkList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|totalBlks
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|BlockID
name|blockID
init|=
operator|new
name|BlockID
argument_list|(
name|containerId
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|BlockData
name|blockData
init|=
operator|new
name|BlockData
argument_list|(
name|blockID
argument_list|)
decl_stmt|;
name|chunkList
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|chunkCount
operator|=
literal|0
init|;
name|chunkCount
operator|<
name|chunksPerBlock
condition|;
name|chunkCount
operator|++
control|)
block|{
name|String
name|chunkName
init|=
name|strBlock
operator|+
name|i
operator|+
name|strChunk
operator|+
name|chunkCount
decl_stmt|;
name|long
name|offset
init|=
name|chunkCount
operator|*
name|chunkLen
decl_stmt|;
name|ChunkInfo
name|info
init|=
operator|new
name|ChunkInfo
argument_list|(
name|chunkName
argument_list|,
name|offset
argument_list|,
name|chunkLen
argument_list|)
decl_stmt|;
name|chunkList
operator|.
name|add
argument_list|(
name|info
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|container
argument_list|,
name|blockID
argument_list|,
name|info
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|chunkData
argument_list|)
argument_list|,
operator|new
name|DispatcherContext
operator|.
name|Builder
argument_list|()
operator|.
name|setStage
argument_list|(
name|DispatcherContext
operator|.
name|WriteChunkStage
operator|.
name|WRITE_DATA
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|container
argument_list|,
name|blockID
argument_list|,
name|info
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|chunkData
argument_list|)
argument_list|,
operator|new
name|DispatcherContext
operator|.
name|Builder
argument_list|()
operator|.
name|setStage
argument_list|(
name|DispatcherContext
operator|.
name|WriteChunkStage
operator|.
name|COMMIT_DATA
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|blockData
operator|.
name|setChunks
argument_list|(
name|chunkList
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|normalBlocks
condition|)
block|{
comment|// deleted key
name|metadataStore
operator|.
name|getStore
argument_list|()
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|OzoneConsts
operator|.
name|DELETING_KEY_PREFIX
operator|+
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|)
argument_list|,
name|blockData
operator|.
name|getProtoBufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// normal key
name|metadataStore
operator|.
name|getStore
argument_list|()
operator|.
name|put
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|)
argument_list|,
name|blockData
operator|.
name|getProtoBufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

