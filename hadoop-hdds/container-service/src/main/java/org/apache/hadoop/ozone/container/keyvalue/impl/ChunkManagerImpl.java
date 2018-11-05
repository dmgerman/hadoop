begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.keyvalue.impl
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
name|impl
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
name|impl
operator|.
name|ChunkLayOutVersion
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
name|interfaces
operator|.
name|ChunkManager
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
name|file
operator|.
name|Files
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
name|StandardCopyOption
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
name|CONTAINER_INTERNAL_ERROR
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
name|NO_SUCH_ALGORITHM
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
name|UNSUPPORTED_REQUEST
import|;
end_import

begin_comment
comment|/**  * This class is for performing chunk related operations.  */
end_comment

begin_class
DECL|class|ChunkManagerImpl
specifier|public
class|class
name|ChunkManagerImpl
implements|implements
name|ChunkManager
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
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
comment|/**    * writes a given chunk.    *    * @param container - Container for the chunk    * @param blockID - ID of the block    * @param info - ChunkInfo    * @param data - data of the chunk    * @param stage - Stage of the Chunk operation    * @throws StorageContainerException    */
DECL|method|writeChunk (Container container, BlockID blockID, ChunkInfo info, ByteBuffer data, ContainerProtos.Stage stage)
specifier|public
name|void
name|writeChunk
parameter_list|(
name|Container
name|container
parameter_list|,
name|BlockID
name|blockID
parameter_list|,
name|ChunkInfo
name|info
parameter_list|,
name|ByteBuffer
name|data
parameter_list|,
name|ContainerProtos
operator|.
name|Stage
name|stage
parameter_list|)
throws|throws
name|StorageContainerException
block|{
try|try
block|{
name|KeyValueContainerData
name|containerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
name|HddsVolume
name|volume
init|=
name|containerData
operator|.
name|getVolume
argument_list|()
decl_stmt|;
name|VolumeIOStats
name|volumeIOStats
init|=
name|volume
operator|.
name|getVolumeIOStats
argument_list|()
decl_stmt|;
name|File
name|chunkFile
init|=
name|ChunkUtils
operator|.
name|getChunkFile
argument_list|(
name|containerData
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|boolean
name|isOverwrite
init|=
name|ChunkUtils
operator|.
name|validateChunkForOverwrite
argument_list|(
name|chunkFile
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|File
name|tmpChunkFile
init|=
name|getTmpChunkFile
argument_list|(
name|chunkFile
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"writing chunk:{} chunk stage:{} chunk file:{} tmp chunk file"
argument_list|,
name|info
operator|.
name|getChunkName
argument_list|()
argument_list|,
name|stage
argument_list|,
name|chunkFile
argument_list|,
name|tmpChunkFile
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|stage
condition|)
block|{
case|case
name|WRITE_DATA
case|:
if|if
condition|(
name|isOverwrite
condition|)
block|{
comment|// if the actual chunk file already exists here while writing the temp
comment|// chunk file, then it means the same ozone client request has
comment|// generated two raft log entries. This can happen either because
comment|// retryCache expired in Ratis (or log index mismatch/corruption in
comment|// Ratis). This can be solved by two approaches as of now:
comment|// 1. Read the complete data in the actual chunk file ,
comment|//    verify the data integrity and in case it mismatches , either
comment|// 2. Delete the chunk File and write the chunk again. For now,
comment|//    let's rewrite the chunk file
comment|// TODO: once the checksum support for write chunks gets plugged in,
comment|// the checksum needs to be verified for the actual chunk file and
comment|// the data to be written here which should be efficient and
comment|// it matches we can safely return without rewriting.
name|LOG
operator|.
name|warn
argument_list|(
literal|"ChunkFile already exists"
operator|+
name|chunkFile
operator|+
literal|".Deleting it."
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|chunkFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tmpChunkFile
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// If the tmp chunk file already exists it means the raft log got
comment|// appended, but later on the log entry got truncated in Ratis leaving
comment|// behind garbage.
comment|// TODO: once the checksum support for data chunks gets plugged in,
comment|// instead of rewriting the chunk here, let's compare the checkSums
name|LOG
operator|.
name|warn
argument_list|(
literal|"tmpChunkFile already exists"
operator|+
name|tmpChunkFile
operator|+
literal|"Overwriting it."
argument_list|)
expr_stmt|;
block|}
comment|// Initially writes to temporary chunk file.
name|ChunkUtils
operator|.
name|writeData
argument_list|(
name|tmpChunkFile
argument_list|,
name|info
argument_list|,
name|data
argument_list|,
name|volumeIOStats
argument_list|)
expr_stmt|;
comment|// No need to increment container stats here, as still data is not
comment|// committed here.
break|break;
case|case
name|COMMIT_DATA
case|:
comment|// commit the data, means move chunk data from temporary chunk file
comment|// to actual chunk file.
if|if
condition|(
name|isOverwrite
condition|)
block|{
comment|// if the actual chunk file already exists , it implies the write
comment|// chunk transaction in the containerStateMachine is getting
comment|// reapplied. This can happen when a node restarts.
comment|// TODO: verify the checkSums for the existing chunkFile and the
comment|// chunkInfo to be committed here
name|LOG
operator|.
name|warn
argument_list|(
literal|"ChunkFile already exists"
operator|+
name|chunkFile
argument_list|)
expr_stmt|;
return|return;
block|}
name|commitChunk
argument_list|(
name|tmpChunkFile
argument_list|,
name|chunkFile
argument_list|)
expr_stmt|;
comment|// Increment container stats here, as we commit the data.
name|containerData
operator|.
name|incrBytesUsed
argument_list|(
name|info
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|containerData
operator|.
name|incrWriteCount
argument_list|()
expr_stmt|;
name|containerData
operator|.
name|incrWriteBytes
argument_list|(
name|info
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|COMBINED
case|:
comment|// directly write to the chunk file
name|ChunkUtils
operator|.
name|writeData
argument_list|(
name|chunkFile
argument_list|,
name|info
argument_list|,
name|data
argument_list|,
name|volumeIOStats
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isOverwrite
condition|)
block|{
name|containerData
operator|.
name|incrBytesUsed
argument_list|(
name|info
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|containerData
operator|.
name|incrWriteCount
argument_list|()
expr_stmt|;
name|containerData
operator|.
name|incrWriteBytes
argument_list|(
name|info
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can not identify write operation."
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
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"write data failed. error: {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Internal error: "
argument_list|,
name|ex
argument_list|,
name|NO_SUCH_ALGORITHM
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"write data failed. error: {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Internal error: "
argument_list|,
name|ex
argument_list|,
name|CONTAINER_INTERNAL_ERROR
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"write data failed. error: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Internal error: "
argument_list|,
name|e
argument_list|,
name|CONTAINER_INTERNAL_ERROR
argument_list|)
throw|;
block|}
block|}
comment|/**    * reads the data defined by a chunk.    *    * @param container - Container for the chunk    * @param blockID - ID of the block.    * @param info - ChunkInfo.    * @return byte array    * @throws StorageContainerException    * TODO: Right now we do not support partial reads and writes of chunks.    * TODO: Explore if we need to do that for ozone.    */
DECL|method|readChunk (Container container, BlockID blockID, ChunkInfo info)
specifier|public
name|byte
index|[]
name|readChunk
parameter_list|(
name|Container
name|container
parameter_list|,
name|BlockID
name|blockID
parameter_list|,
name|ChunkInfo
name|info
parameter_list|)
throws|throws
name|StorageContainerException
block|{
try|try
block|{
name|KeyValueContainerData
name|containerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
name|ByteBuffer
name|data
decl_stmt|;
name|HddsVolume
name|volume
init|=
name|containerData
operator|.
name|getVolume
argument_list|()
decl_stmt|;
name|VolumeIOStats
name|volumeIOStats
init|=
name|volume
operator|.
name|getVolumeIOStats
argument_list|()
decl_stmt|;
comment|// Checking here, which layout version the container is, and reading
comment|// the chunk file in that format.
comment|// In version1, we verify checksum if it is available and return data
comment|// of the chunk file.
if|if
condition|(
name|containerData
operator|.
name|getLayOutVersion
argument_list|()
operator|==
name|ChunkLayOutVersion
operator|.
name|getLatestVersion
argument_list|()
operator|.
name|getVersion
argument_list|()
condition|)
block|{
name|File
name|chunkFile
init|=
name|ChunkUtils
operator|.
name|getChunkFile
argument_list|(
name|containerData
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|data
operator|=
name|ChunkUtils
operator|.
name|readData
argument_list|(
name|chunkFile
argument_list|,
name|info
argument_list|,
name|volumeIOStats
argument_list|)
expr_stmt|;
name|containerData
operator|.
name|incrReadCount
argument_list|()
expr_stmt|;
name|long
name|length
init|=
name|chunkFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|containerData
operator|.
name|incrReadBytes
argument_list|(
name|length
argument_list|)
expr_stmt|;
return|return
name|data
operator|.
name|array
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"read data failed. error: {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Internal error: "
argument_list|,
name|ex
argument_list|,
name|NO_SUCH_ALGORITHM
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"read data failed. error: {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Internal error: "
argument_list|,
name|ex
argument_list|,
name|CONTAINER_INTERNAL_ERROR
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"read data failed. error: {}"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Internal error: "
argument_list|,
name|e
argument_list|,
name|CONTAINER_INTERNAL_ERROR
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Deletes a given chunk.    *    * @param container - Container for the chunk    * @param blockID - ID of the block    * @param info - Chunk Info    * @throws StorageContainerException    */
DECL|method|deleteChunk (Container container, BlockID blockID, ChunkInfo info)
specifier|public
name|void
name|deleteChunk
parameter_list|(
name|Container
name|container
parameter_list|,
name|BlockID
name|blockID
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
name|blockID
argument_list|,
literal|"Block ID cannot be null."
argument_list|)
expr_stmt|;
name|KeyValueContainerData
name|containerData
init|=
operator|(
name|KeyValueContainerData
operator|)
name|container
operator|.
name|getContainerData
argument_list|()
decl_stmt|;
comment|// Checking here, which layout version the container is, and performing
comment|// deleting chunk operation.
comment|// In version1, we have only chunk file.
if|if
condition|(
name|containerData
operator|.
name|getLayOutVersion
argument_list|()
operator|==
name|ChunkLayOutVersion
operator|.
name|getLatestVersion
argument_list|()
operator|.
name|getVersion
argument_list|()
condition|)
block|{
name|File
name|chunkFile
init|=
name|ChunkUtils
operator|.
name|getChunkFile
argument_list|(
name|containerData
argument_list|,
name|info
argument_list|)
decl_stmt|;
comment|// if the chunk file does not exist, it might have already been deleted.
comment|// The call might be because of reapply of transactions on datanode
comment|// restart.
if|if
condition|(
operator|!
name|chunkFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Chunk file doe not exist. chunk info :"
operator|+
name|info
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|(
name|info
operator|.
name|getOffset
argument_list|()
operator|==
literal|0
operator|)
operator|&&
operator|(
name|info
operator|.
name|getLen
argument_list|()
operator|==
name|chunkFile
operator|.
name|length
argument_list|()
operator|)
condition|)
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|chunkFile
argument_list|)
expr_stmt|;
name|containerData
operator|.
name|decrBytesUsed
argument_list|(
name|chunkFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Not Supported Operation. Trying to delete a "
operator|+
literal|"chunk that is in shared file. chunk info : "
operator|+
name|info
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StorageContainerException
argument_list|(
literal|"Not Supported Operation. "
operator|+
literal|"Trying to delete a chunk that is in shared file. chunk info : "
operator|+
name|info
operator|.
name|toString
argument_list|()
argument_list|,
name|UNSUPPORTED_REQUEST
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Shutdown the chunkManager.    *    * In the chunkManager we haven't acquired any resources, so nothing to do    * here.    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
comment|//TODO: need to revisit this during integration of container IO.
block|}
comment|/**    * Returns the temporary chunkFile path.    * @param chunkFile    * @param info    * @return temporary chunkFile path    * @throws StorageContainerException    */
DECL|method|getTmpChunkFile (File chunkFile, ChunkInfo info)
specifier|private
name|File
name|getTmpChunkFile
parameter_list|(
name|File
name|chunkFile
parameter_list|,
name|ChunkInfo
name|info
parameter_list|)
throws|throws
name|StorageContainerException
block|{
return|return
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
return|;
block|}
comment|/**    * Commit the chunk by renaming the temporary chunk file to chunk file.    * @param tmpChunkFile    * @param chunkFile    * @throws IOException    */
DECL|method|commitChunk (File tmpChunkFile, File chunkFile)
specifier|private
name|void
name|commitChunk
parameter_list|(
name|File
name|tmpChunkFile
parameter_list|,
name|File
name|chunkFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Files
operator|.
name|move
argument_list|(
name|tmpChunkFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|chunkFile
operator|.
name|toPath
argument_list|()
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

