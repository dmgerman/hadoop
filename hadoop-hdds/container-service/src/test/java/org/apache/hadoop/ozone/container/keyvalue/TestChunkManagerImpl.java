begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|keyvalue
operator|.
name|helpers
operator|.
name|ChunkUtils
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
name|HddsVolume
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
name|VolumeIOStats
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
name|Before
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
name|TemporaryFolder
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
name|Arrays
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyLong
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
comment|/**  * This class is used to test ChunkManager operations.  */
end_comment

begin_class
DECL|class|TestChunkManagerImpl
specifier|public
class|class
name|TestChunkManagerImpl
block|{
DECL|field|config
specifier|private
name|OzoneConfiguration
name|config
decl_stmt|;
DECL|field|scmId
specifier|private
name|String
name|scmId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|volumeSet
specifier|private
name|VolumeSet
name|volumeSet
decl_stmt|;
DECL|field|volumeChoosingPolicy
specifier|private
name|RoundRobinVolumeChoosingPolicy
name|volumeChoosingPolicy
decl_stmt|;
DECL|field|hddsVolume
specifier|private
name|HddsVolume
name|hddsVolume
decl_stmt|;
DECL|field|keyValueContainerData
specifier|private
name|KeyValueContainerData
name|keyValueContainerData
decl_stmt|;
DECL|field|keyValueContainer
specifier|private
name|KeyValueContainer
name|keyValueContainer
decl_stmt|;
DECL|field|blockID
specifier|private
name|BlockID
name|blockID
decl_stmt|;
DECL|field|chunkManager
specifier|private
name|ChunkManagerImpl
name|chunkManager
decl_stmt|;
DECL|field|chunkInfo
specifier|private
name|ChunkInfo
name|chunkInfo
decl_stmt|;
DECL|field|data
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
annotation|@
name|Rule
DECL|field|folder
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|()
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
name|config
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|UUID
name|datanodeId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
name|hddsVolume
operator|=
operator|new
name|HddsVolume
operator|.
name|Builder
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|conf
argument_list|(
name|config
argument_list|)
operator|.
name|datanodeUuid
argument_list|(
name|datanodeId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|volumeSet
operator|=
name|mock
argument_list|(
name|VolumeSet
operator|.
name|class
argument_list|)
expr_stmt|;
name|volumeChoosingPolicy
operator|=
name|mock
argument_list|(
name|RoundRobinVolumeChoosingPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volumeChoosingPolicy
operator|.
name|chooseVolume
argument_list|(
name|anyList
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|hddsVolume
argument_list|)
expr_stmt|;
name|keyValueContainerData
operator|=
operator|new
name|KeyValueContainerData
argument_list|(
literal|1L
argument_list|,
operator|(
name|long
operator|)
name|StorageUnit
operator|.
name|GB
operator|.
name|toBytes
argument_list|(
literal|5
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
name|datanodeId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|=
operator|new
name|KeyValueContainer
argument_list|(
name|keyValueContainerData
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|keyValueContainer
operator|.
name|create
argument_list|(
name|volumeSet
argument_list|,
name|volumeChoosingPolicy
argument_list|,
name|scmId
argument_list|)
expr_stmt|;
name|data
operator|=
literal|"testing write chunks"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
expr_stmt|;
comment|// Creating BlockData
name|blockID
operator|=
operator|new
name|BlockID
argument_list|(
literal|1L
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|chunkInfo
operator|=
operator|new
name|ChunkInfo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d.data.%d"
argument_list|,
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Create a ChunkManager object.
name|chunkManager
operator|=
operator|new
name|ChunkManagerImpl
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteChunkStageWriteAndCommit ()
specifier|public
name|void
name|testWriteChunkStageWriteAndCommit
parameter_list|()
throws|throws
name|Exception
block|{
comment|//As in Setup, we try to create container, these paths should exist.
name|assertTrue
argument_list|(
name|keyValueContainerData
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
name|keyValueContainerData
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
comment|// As no chunks are written to the volume writeBytes should be 0
name|checkWriteIOStats
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Stage
operator|.
name|WRITE_DATA
argument_list|)
expr_stmt|;
comment|// Now a chunk file is being written with Stage WRITE_DATA, so it should
comment|// create a temporary chunk file.
name|assertTrue
argument_list|(
name|chunksPath
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|File
name|chunkFile
init|=
name|ChunkUtils
operator|.
name|getChunkFile
argument_list|(
name|keyValueContainerData
argument_list|,
name|chunkInfo
argument_list|)
decl_stmt|;
name|File
name|tempChunkFile
init|=
operator|new
name|File
argument_list|(
name|chunkFile
operator|.
name|getParent
argument_list|()
argument_list|,
name|chunkFile
operator|.
name|getName
argument_list|()
operator|+
name|OzoneConsts
operator|.
name|CONTAINER_CHUNK_NAME_DELIMITER
operator|+
name|OzoneConsts
operator|.
name|CONTAINER_TEMPORARY_CHUNK_PREFIX
argument_list|)
decl_stmt|;
comment|// As chunk write stage is WRITE_DATA, temp chunk file will be created.
name|assertTrue
argument_list|(
name|tempChunkFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|checkWriteIOStats
argument_list|(
name|data
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Stage
operator|.
name|COMMIT_DATA
argument_list|)
expr_stmt|;
name|checkWriteIOStats
argument_list|(
name|data
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Old temp file should have been renamed to chunk file.
name|assertTrue
argument_list|(
name|chunksPath
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|// As commit happened, chunk file should exist.
name|assertTrue
argument_list|(
name|chunkFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tempChunkFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteChunkIncorrectLength ()
specifier|public
name|void
name|testWriteChunkIncorrectLength
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|long
name|randomLength
init|=
literal|200L
decl_stmt|;
name|chunkInfo
operator|=
operator|new
name|ChunkInfo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d.data.%d"
argument_list|,
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|randomLength
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Stage
operator|.
name|WRITE_DATA
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testWriteChunkIncorrectLength failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
comment|// As we got an exception, writeBytes should be 0.
name|checkWriteIOStats
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"data array does not match "
operator|+
literal|"the length "
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|INVALID_WRITE_SIZE
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteChunkStageCombinedData ()
specifier|public
name|void
name|testWriteChunkStageCombinedData
parameter_list|()
throws|throws
name|Exception
block|{
comment|//As in Setup, we try to create container, these paths should exist.
name|assertTrue
argument_list|(
name|keyValueContainerData
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
name|keyValueContainerData
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
name|checkWriteIOStats
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Stage
operator|.
name|COMBINED
argument_list|)
expr_stmt|;
comment|// Now a chunk file is being written with Stage COMBINED_DATA, so it should
comment|// create a chunk file.
name|assertTrue
argument_list|(
name|chunksPath
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|File
name|chunkFile
init|=
name|ChunkUtils
operator|.
name|getChunkFile
argument_list|(
name|keyValueContainerData
argument_list|,
name|chunkInfo
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|chunkFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|checkWriteIOStats
argument_list|(
name|data
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadChunk ()
specifier|public
name|void
name|testReadChunk
parameter_list|()
throws|throws
name|Exception
block|{
name|checkWriteIOStats
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Stage
operator|.
name|COMBINED
argument_list|)
expr_stmt|;
name|checkWriteIOStats
argument_list|(
name|data
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkReadIOStats
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|byte
index|[]
name|expectedData
init|=
name|chunkManager
operator|.
name|readChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedData
operator|.
name|length
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|expectedData
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|checkReadIOStats
argument_list|(
name|data
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteChunk ()
specifier|public
name|void
name|testDeleteChunk
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|chunksPath
init|=
operator|new
name|File
argument_list|(
name|keyValueContainerData
operator|.
name|getChunksPath
argument_list|()
argument_list|)
decl_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Stage
operator|.
name|COMBINED
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chunksPath
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|deleteChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|)
expr_stmt|;
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
block|}
annotation|@
name|Test
DECL|method|testDeleteChunkUnsupportedRequest ()
specifier|public
name|void
name|testDeleteChunkUnsupportedRequest
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Stage
operator|.
name|COMBINED
argument_list|)
expr_stmt|;
name|long
name|randomLength
init|=
literal|200L
decl_stmt|;
name|chunkInfo
operator|=
operator|new
name|ChunkInfo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d.data.%d"
argument_list|,
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|randomLength
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|deleteChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"testDeleteChunkUnsupportedRequest"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Not Supported Operation."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|UNSUPPORTED_REQUEST
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadChunkFileNotExists ()
specifier|public
name|void
name|testReadChunkFileNotExists
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// trying to read a chunk, where chunk file does not exist
name|byte
index|[]
name|expectedData
init|=
name|chunkManager
operator|.
name|readChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"testReadChunkFileNotExists failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Unable to find the chunk "
operator|+
literal|"file."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ContainerProtos
operator|.
name|Result
operator|.
name|UNABLE_TO_FIND_CHUNK
argument_list|,
name|ex
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteAndReadChunkMultipleTimes ()
specifier|public
name|void
name|testWriteAndReadChunkMultipleTimes
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|chunkInfo
operator|=
operator|new
name|ChunkInfo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d.data.%d"
argument_list|,
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|writeChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|,
name|ContainerProtos
operator|.
name|Stage
operator|.
name|COMBINED
argument_list|)
expr_stmt|;
block|}
name|checkWriteIOStats
argument_list|(
name|data
operator|.
name|length
operator|*
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hddsVolume
operator|.
name|getVolumeIOStats
argument_list|()
operator|.
name|getWriteTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|chunkInfo
operator|=
operator|new
name|ChunkInfo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%d.data.%d"
argument_list|,
name|blockID
operator|.
name|getLocalID
argument_list|()
argument_list|,
name|i
argument_list|)
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|chunkManager
operator|.
name|readChunk
argument_list|(
name|keyValueContainer
argument_list|,
name|blockID
argument_list|,
name|chunkInfo
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|checkReadIOStats
argument_list|(
name|data
operator|.
name|length
operator|*
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hddsVolume
operator|.
name|getVolumeIOStats
argument_list|()
operator|.
name|getReadTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check WriteIO stats.    * @param length    * @param opCount    */
DECL|method|checkWriteIOStats (long length, long opCount)
specifier|private
name|void
name|checkWriteIOStats
parameter_list|(
name|long
name|length
parameter_list|,
name|long
name|opCount
parameter_list|)
block|{
name|VolumeIOStats
name|volumeIOStats
init|=
name|hddsVolume
operator|.
name|getVolumeIOStats
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|length
argument_list|,
name|volumeIOStats
operator|.
name|getWriteBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|opCount
argument_list|,
name|volumeIOStats
operator|.
name|getWriteOpCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check ReadIO stats.    * @param length    * @param opCount    */
DECL|method|checkReadIOStats (long length, long opCount)
specifier|private
name|void
name|checkReadIOStats
parameter_list|(
name|long
name|length
parameter_list|,
name|long
name|opCount
parameter_list|)
block|{
name|VolumeIOStats
name|volumeIOStats
init|=
name|hddsVolume
operator|.
name|getVolumeIOStats
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|length
argument_list|,
name|volumeIOStats
operator|.
name|getReadBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|opCount
argument_list|,
name|volumeIOStats
operator|.
name|getReadOpCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

