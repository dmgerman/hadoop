begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue.helpers
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
operator|.
name|helpers
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
name|base
operator|.
name|Preconditions
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
name|codec
operator|.
name|binary
operator|.
name|Hex
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|ReadChunkResponseProto
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
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|KeyValueContainerData
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
name|ratis
operator|.
name|thirdparty
operator|.
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
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
name|util
operator|.
name|Time
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|AsynchronousFileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|Result
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Utility methods for chunk operations for KeyValue container.  */
end_comment

begin_class
DECL|class|ChunkUtils
specifier|public
specifier|final
class|class
name|ChunkUtils
block|{
comment|/** Never constructed. **/
DECL|method|ChunkUtils ()
specifier|private
name|ChunkUtils
parameter_list|()
block|{    }
comment|/**    * Writes the data in chunk Info to the specified location in the chunkfile.    *    * @param chunkFile - File to write data to.    * @param chunkInfo - Data stream to write.    * @param data - The data buffer.    * @param volumeIOStats    * @throws StorageContainerException    */
DECL|method|writeData (File chunkFile, ChunkInfo chunkInfo, ByteBuffer data, VolumeIOStats volumeIOStats)
specifier|public
specifier|static
name|void
name|writeData
parameter_list|(
name|File
name|chunkFile
parameter_list|,
name|ChunkInfo
name|chunkInfo
parameter_list|,
name|ByteBuffer
name|data
parameter_list|,
name|VolumeIOStats
name|volumeIOStats
parameter_list|)
throws|throws
name|StorageContainerException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
throws|,
name|NoSuchAlgorithmException
block|{
name|int
name|bufferSize
init|=
name|data
operator|.
name|capacity
argument_list|()
decl_stmt|;
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChunkManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|bufferSize
operator|!=
name|chunkInfo
operator|.
name|getLen
argument_list|()
condition|)
block|{
name|String
name|err
init|=
name|String
operator|.
name|format
argument_list|(
literal|"data array does not match the length "
operator|+
literal|"specified. DataLen: %d Byte Array: %d"
argument_list|,
name|chunkInfo
operator|.
name|getLen
argument_list|()
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|err
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
name|err
argument_list|,
name|INVALID_WRITE_SIZE
argument_list|)
throw|;
block|}
name|AsynchronousFileChannel
name|file
init|=
literal|null
decl_stmt|;
name|FileLock
name|lock
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|chunkInfo
operator|.
name|getChecksum
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|chunkInfo
operator|.
name|getChecksum
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|verifyChecksum
argument_list|(
name|chunkInfo
argument_list|,
name|data
argument_list|,
name|log
argument_list|)
expr_stmt|;
block|}
name|long
name|writeTimeStart
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|file
operator|=
name|AsynchronousFileChannel
operator|.
name|open
argument_list|(
name|chunkFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|SPARSE
argument_list|,
name|StandardOpenOption
operator|.
name|SYNC
argument_list|)
expr_stmt|;
name|lock
operator|=
name|file
operator|.
name|lock
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|file
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|chunkInfo
operator|.
name|getOffset
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// Increment volumeIO stats here.
name|volumeIOStats
operator|.
name|incWriteTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|writeTimeStart
argument_list|)
expr_stmt|;
name|volumeIOStats
operator|.
name|incWriteOpCount
argument_list|()
expr_stmt|;
name|volumeIOStats
operator|.
name|incWriteBytes
argument_list|(
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|!=
name|bufferSize
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid write size found. Size:{}  Expected: {} "
argument_list|,
name|size
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Invalid write size found. "
operator|+
literal|"Size: "
operator|+
name|size
operator|+
literal|" Expected: "
operator|+
name|bufferSize
argument_list|,
name|INVALID_WRITE_SIZE
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|StorageContainerException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
name|e
argument_list|,
name|IO_EXCEPTION
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to release lock ??, Fatal Error."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
name|e
argument_list|,
name|CONTAINER_INTERNAL_ERROR
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Error closing chunk file"
argument_list|,
name|e
argument_list|,
name|CONTAINER_INTERNAL_ERROR
argument_list|)
throw|;
block|}
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Write Chunk completed for chunkFile: {}, size {}"
argument_list|,
name|chunkFile
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads data from an existing chunk file.    *    * @param chunkFile - file where data lives.    * @param data - chunk definition.    * @param volumeIOStats    * @return ByteBuffer    * @throws StorageContainerException    * @throws ExecutionException    * @throws InterruptedException    */
DECL|method|readData (File chunkFile, ChunkInfo data, VolumeIOStats volumeIOStats)
specifier|public
specifier|static
name|ByteBuffer
name|readData
parameter_list|(
name|File
name|chunkFile
parameter_list|,
name|ChunkInfo
name|data
parameter_list|,
name|VolumeIOStats
name|volumeIOStats
parameter_list|)
throws|throws
name|StorageContainerException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
throws|,
name|NoSuchAlgorithmException
block|{
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChunkManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|chunkFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to find the chunk file. chunk info : {}"
argument_list|,
name|data
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to find the chunk file. "
operator|+
literal|"chunk info "
operator|+
name|data
operator|.
name|toString
argument_list|()
argument_list|,
name|UNABLE_TO_FIND_CHUNK
argument_list|)
throw|;
block|}
name|AsynchronousFileChannel
name|file
init|=
literal|null
decl_stmt|;
name|FileLock
name|lock
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|readStartTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|file
operator|=
name|AsynchronousFileChannel
operator|.
name|open
argument_list|(
name|chunkFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|)
expr_stmt|;
name|lock
operator|=
name|file
operator|.
name|lock
argument_list|(
name|data
operator|.
name|getOffset
argument_list|()
argument_list|,
name|data
operator|.
name|getLen
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
operator|(
name|int
operator|)
name|data
operator|.
name|getLen
argument_list|()
argument_list|)
decl_stmt|;
name|file
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|data
operator|.
name|getOffset
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Increment volumeIO stats here.
name|volumeIOStats
operator|.
name|incReadTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|readStartTime
argument_list|)
expr_stmt|;
name|volumeIOStats
operator|.
name|incReadOpCount
argument_list|()
expr_stmt|;
name|volumeIOStats
operator|.
name|incReadBytes
argument_list|(
name|data
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
operator|.
name|getChecksum
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|data
operator|.
name|getChecksum
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buf
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|verifyChecksum
argument_list|(
name|data
argument_list|,
name|buf
argument_list|,
name|log
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageContainerException
argument_list|(
name|e
argument_list|,
name|IO_EXCEPTION
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|lock
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"I/O error is lock release."
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Verifies the checksum of a chunk against the data buffer.    *    * @param chunkInfo - Chunk Info.    * @param data - data buffer    * @param log - log    * @throws NoSuchAlgorithmException    * @throws StorageContainerException    */
DECL|method|verifyChecksum (ChunkInfo chunkInfo, ByteBuffer data, Logger log)
specifier|private
specifier|static
name|void
name|verifyChecksum
parameter_list|(
name|ChunkInfo
name|chunkInfo
parameter_list|,
name|ByteBuffer
name|data
parameter_list|,
name|Logger
name|log
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|StorageContainerException
block|{
name|MessageDigest
name|sha
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|OzoneConsts
operator|.
name|FILE_HASH
argument_list|)
decl_stmt|;
name|sha
operator|.
name|update
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|data
operator|.
name|rewind
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|sha
operator|.
name|digest
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|chunkInfo
operator|.
name|getChecksum
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Checksum mismatch. Provided: {} , computed: {}"
argument_list|,
name|chunkInfo
operator|.
name|getChecksum
argument_list|()
argument_list|,
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|sha
operator|.
name|digest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Checksum mismatch. Provided: "
operator|+
name|chunkInfo
operator|.
name|getChecksum
argument_list|()
operator|+
literal|" , computed: "
operator|+
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|sha
operator|.
name|digest
argument_list|()
argument_list|)
argument_list|,
name|CHECKSUM_MISMATCH
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validates chunk data and returns a file object to Chunk File that we are    * expected to write data to.    *    * @param chunkFile - chunkFile to write data into.    * @param info - chunk info.    * @return true if the chunkFile exists and chunkOffset< chunkFile length,    *         false otherwise.    */
DECL|method|validateChunkForOverwrite (File chunkFile, ChunkInfo info)
specifier|public
specifier|static
name|boolean
name|validateChunkForOverwrite
parameter_list|(
name|File
name|chunkFile
parameter_list|,
name|ChunkInfo
name|info
parameter_list|)
block|{
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChunkManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|isOverWriteRequested
argument_list|(
name|chunkFile
argument_list|,
name|info
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isOverWritePermitted
argument_list|(
name|info
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Duplicate write chunk request. Chunk overwrite "
operator|+
literal|"without explicit request. {}"
argument_list|,
name|info
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Validates that Path to chunk file exists.    *    * @param containerData - Container Data    * @param info - Chunk info    * @return - File.    * @throws StorageContainerException    */
DECL|method|getChunkFile (KeyValueContainerData containerData, ChunkInfo info)
specifier|public
specifier|static
name|File
name|getChunkFile
parameter_list|(
name|KeyValueContainerData
name|containerData
parameter_list|,
name|ChunkInfo
name|info
parameter_list|)
throws|throws
name|StorageContainerException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerData
argument_list|,
literal|"Container data can't be null"
argument_list|)
expr_stmt|;
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ChunkManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|chunksPath
init|=
name|containerData
operator|.
name|getChunksPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|chunksPath
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Chunks path is null in the container data"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to get Chunks directory."
argument_list|,
name|UNABLE_TO_FIND_DATA_DIR
argument_list|)
throw|;
block|}
name|File
name|chunksLoc
init|=
operator|new
name|File
argument_list|(
name|chunksPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|chunksLoc
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Chunks path does not exist"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Unable to get Chunks directory."
argument_list|,
name|UNABLE_TO_FIND_DATA_DIR
argument_list|)
throw|;
block|}
return|return
name|chunksLoc
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
name|info
operator|.
name|getChunkName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
return|;
block|}
comment|/**    * Checks if we are getting a request to overwrite an existing range of    * chunk.    *    * @param chunkFile - File    * @param chunkInfo - Buffer to write    * @return bool    */
DECL|method|isOverWriteRequested (File chunkFile, ChunkInfo chunkInfo)
specifier|public
specifier|static
name|boolean
name|isOverWriteRequested
parameter_list|(
name|File
name|chunkFile
parameter_list|,
name|ChunkInfo
name|chunkInfo
parameter_list|)
block|{
if|if
condition|(
operator|!
name|chunkFile
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|long
name|offset
init|=
name|chunkInfo
operator|.
name|getOffset
argument_list|()
decl_stmt|;
return|return
name|offset
operator|<
name|chunkFile
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**    * Overwrite is permitted if an only if the user explicitly asks for it. We    * permit this iff the key/value pair contains a flag called    * [OverWriteRequested, true].    *    * @param chunkInfo - Chunk info    * @return true if the user asks for it.    */
DECL|method|isOverWritePermitted (ChunkInfo chunkInfo)
specifier|public
specifier|static
name|boolean
name|isOverWritePermitted
parameter_list|(
name|ChunkInfo
name|chunkInfo
parameter_list|)
block|{
name|String
name|overWrite
init|=
name|chunkInfo
operator|.
name|getMetadata
argument_list|()
operator|.
name|get
argument_list|(
name|OzoneConsts
operator|.
name|CHUNK_OVERWRITE
argument_list|)
decl_stmt|;
return|return
operator|(
name|overWrite
operator|!=
literal|null
operator|)
operator|&&
operator|(
operator|!
name|overWrite
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|overWrite
argument_list|)
operator|)
return|;
block|}
comment|/**    * Returns a CreateContainer Response. This call is used by create and delete    * containers which have null success responses.    *    * @param msg Request    * @return Response.    */
DECL|method|getChunkResponseSuccess ( ContainerCommandRequestProto msg)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|getChunkResponseSuccess
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|)
block|{
return|return
name|ContainerUtils
operator|.
name|getSuccessResponse
argument_list|(
name|msg
argument_list|)
return|;
block|}
comment|/**    * Gets a response to the read chunk calls.    *    * @param msg - Msg    * @param data - Data    * @param info - Info    * @return Response.    */
DECL|method|getReadChunkResponse ( ContainerCommandRequestProto msg, byte[] data, ChunkInfo info)
specifier|public
specifier|static
name|ContainerCommandResponseProto
name|getReadChunkResponse
parameter_list|(
name|ContainerCommandRequestProto
name|msg
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|ChunkInfo
name|info
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|data
argument_list|,
literal|"Chunk data is null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|info
argument_list|,
literal|"Chunk Info is null"
argument_list|)
expr_stmt|;
name|ReadChunkResponseProto
operator|.
name|Builder
name|response
init|=
name|ReadChunkResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|response
operator|.
name|setChunkData
argument_list|(
name|info
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setData
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|setBlockID
argument_list|(
name|msg
operator|.
name|getReadChunk
argument_list|()
operator|.
name|getBlockID
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerCommandResponseProto
operator|.
name|Builder
name|builder
init|=
name|ContainerUtils
operator|.
name|getSuccessResponseBuilder
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setReadChunk
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

