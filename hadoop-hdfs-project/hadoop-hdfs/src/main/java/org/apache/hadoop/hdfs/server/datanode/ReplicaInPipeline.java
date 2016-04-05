begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
package|;
end_package

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
name|FileOutputStream
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|atomic
operator|.
name|AtomicReference
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
name|Block
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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|ReplicaState
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|ReplicaOutputStreams
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
name|util
operator|.
name|DataChecksum
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
name|StringUtils
import|;
end_import

begin_comment
comment|/**   * This class defines a replica in a pipeline, which  * includes a persistent replica being written to by a dfs client or  * a temporary replica being replicated by a source datanode or  * being copied for the balancing purpose.  *   * The base class implements a temporary replica  */
end_comment

begin_class
DECL|class|ReplicaInPipeline
specifier|public
class|class
name|ReplicaInPipeline
extends|extends
name|ReplicaInfo
implements|implements
name|ReplicaInPipelineInterface
block|{
DECL|field|bytesAcked
specifier|private
name|long
name|bytesAcked
decl_stmt|;
DECL|field|bytesOnDisk
specifier|private
name|long
name|bytesOnDisk
decl_stmt|;
DECL|field|lastChecksum
specifier|private
name|byte
index|[]
name|lastChecksum
decl_stmt|;
DECL|field|writer
specifier|private
name|AtomicReference
argument_list|<
name|Thread
argument_list|>
name|writer
init|=
operator|new
name|AtomicReference
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Bytes reserved for this replica on the containing volume.    * Based off difference between the estimated maximum block length and    * the bytes already written to this block.    */
DECL|field|bytesReserved
specifier|private
name|long
name|bytesReserved
decl_stmt|;
DECL|field|originalBytesReserved
specifier|private
specifier|final
name|long
name|originalBytesReserved
decl_stmt|;
comment|/**    * Constructor for a zero length replica    * @param blockId block id    * @param genStamp replica generation stamp    * @param vol volume where replica is located    * @param dir directory path where block and meta files are located    * @param bytesToReserve disk space to reserve for this replica, based on    *                       the estimated maximum block length.    */
DECL|method|ReplicaInPipeline (long blockId, long genStamp, FsVolumeSpi vol, File dir, long bytesToReserve)
specifier|public
name|ReplicaInPipeline
parameter_list|(
name|long
name|blockId
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|,
name|File
name|dir
parameter_list|,
name|long
name|bytesToReserve
parameter_list|)
block|{
name|this
argument_list|(
name|blockId
argument_list|,
literal|0L
argument_list|,
name|genStamp
argument_list|,
name|vol
argument_list|,
name|dir
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|bytesToReserve
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    * @param block a block    * @param vol volume where replica is located    * @param dir directory path where block and meta files are located    * @param writer a thread that is writing to this replica    */
DECL|method|ReplicaInPipeline (Block block, FsVolumeSpi vol, File dir, Thread writer)
name|ReplicaInPipeline
parameter_list|(
name|Block
name|block
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|,
name|File
name|dir
parameter_list|,
name|Thread
name|writer
parameter_list|)
block|{
name|this
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|,
name|vol
argument_list|,
name|dir
argument_list|,
name|writer
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    * @param blockId block id    * @param len replica length    * @param genStamp replica generation stamp    * @param vol volume where replica is located    * @param dir directory path where block and meta files are located    * @param writer a thread that is writing to this replica    * @param bytesToReserve disk space to reserve for this replica, based on    *                       the estimated maximum block length.    */
DECL|method|ReplicaInPipeline (long blockId, long len, long genStamp, FsVolumeSpi vol, File dir, Thread writer, long bytesToReserve)
name|ReplicaInPipeline
parameter_list|(
name|long
name|blockId
parameter_list|,
name|long
name|len
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|,
name|File
name|dir
parameter_list|,
name|Thread
name|writer
parameter_list|,
name|long
name|bytesToReserve
parameter_list|)
block|{
name|super
argument_list|(
name|blockId
argument_list|,
name|len
argument_list|,
name|genStamp
argument_list|,
name|vol
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesAcked
operator|=
name|len
expr_stmt|;
name|this
operator|.
name|bytesOnDisk
operator|=
name|len
expr_stmt|;
name|this
operator|.
name|writer
operator|.
name|set
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesReserved
operator|=
name|bytesToReserve
expr_stmt|;
name|this
operator|.
name|originalBytesReserved
operator|=
name|bytesToReserve
expr_stmt|;
block|}
comment|/**    * Copy constructor.    * @param from where to copy from    */
DECL|method|ReplicaInPipeline (ReplicaInPipeline from)
specifier|public
name|ReplicaInPipeline
parameter_list|(
name|ReplicaInPipeline
name|from
parameter_list|)
block|{
name|super
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesAcked
operator|=
name|from
operator|.
name|getBytesAcked
argument_list|()
expr_stmt|;
name|this
operator|.
name|bytesOnDisk
operator|=
name|from
operator|.
name|getBytesOnDisk
argument_list|()
expr_stmt|;
name|this
operator|.
name|writer
operator|.
name|set
argument_list|(
name|from
operator|.
name|writer
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesReserved
operator|=
name|from
operator|.
name|bytesReserved
expr_stmt|;
name|this
operator|.
name|originalBytesReserved
operator|=
name|from
operator|.
name|originalBytesReserved
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getVisibleLength ()
specifier|public
name|long
name|getVisibleLength
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
comment|//ReplicaInfo
DECL|method|getState ()
specifier|public
name|ReplicaState
name|getState
parameter_list|()
block|{
return|return
name|ReplicaState
operator|.
name|TEMPORARY
return|;
block|}
annotation|@
name|Override
comment|// ReplicaInPipelineInterface
DECL|method|getBytesAcked ()
specifier|public
name|long
name|getBytesAcked
parameter_list|()
block|{
return|return
name|bytesAcked
return|;
block|}
annotation|@
name|Override
comment|// ReplicaInPipelineInterface
DECL|method|setBytesAcked (long bytesAcked)
specifier|public
name|void
name|setBytesAcked
parameter_list|(
name|long
name|bytesAcked
parameter_list|)
block|{
name|long
name|newBytesAcked
init|=
name|bytesAcked
operator|-
name|this
operator|.
name|bytesAcked
decl_stmt|;
name|this
operator|.
name|bytesAcked
operator|=
name|bytesAcked
expr_stmt|;
comment|// Once bytes are ACK'ed we can release equivalent space from the
comment|// volume's reservedForRbw count. We could have released it as soon
comment|// as the write-to-disk completed but that would be inefficient.
name|getVolume
argument_list|()
operator|.
name|releaseReservedSpace
argument_list|(
name|newBytesAcked
argument_list|)
expr_stmt|;
name|bytesReserved
operator|-=
name|newBytesAcked
expr_stmt|;
block|}
annotation|@
name|Override
comment|// ReplicaInPipelineInterface
DECL|method|getBytesOnDisk ()
specifier|public
name|long
name|getBytesOnDisk
parameter_list|()
block|{
return|return
name|bytesOnDisk
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesReserved ()
specifier|public
name|long
name|getBytesReserved
parameter_list|()
block|{
return|return
name|bytesReserved
return|;
block|}
annotation|@
name|Override
DECL|method|getOriginalBytesReserved ()
specifier|public
name|long
name|getOriginalBytesReserved
parameter_list|()
block|{
return|return
name|originalBytesReserved
return|;
block|}
annotation|@
name|Override
DECL|method|releaseAllBytesReserved ()
specifier|public
name|void
name|releaseAllBytesReserved
parameter_list|()
block|{
comment|// ReplicaInPipelineInterface
name|getVolume
argument_list|()
operator|.
name|releaseReservedSpace
argument_list|(
name|bytesReserved
argument_list|)
expr_stmt|;
name|getVolume
argument_list|()
operator|.
name|releaseLockedMemory
argument_list|(
name|bytesReserved
argument_list|)
expr_stmt|;
name|bytesReserved
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
comment|// ReplicaInPipelineInterface
DECL|method|setLastChecksumAndDataLen (long dataLength, byte[] lastChecksum)
specifier|public
specifier|synchronized
name|void
name|setLastChecksumAndDataLen
parameter_list|(
name|long
name|dataLength
parameter_list|,
name|byte
index|[]
name|lastChecksum
parameter_list|)
block|{
name|this
operator|.
name|bytesOnDisk
operator|=
name|dataLength
expr_stmt|;
name|this
operator|.
name|lastChecksum
operator|=
name|lastChecksum
expr_stmt|;
block|}
annotation|@
name|Override
comment|// ReplicaInPipelineInterface
DECL|method|getLastChecksumAndDataLen ()
specifier|public
specifier|synchronized
name|ChunkChecksum
name|getLastChecksumAndDataLen
parameter_list|()
block|{
return|return
operator|new
name|ChunkChecksum
argument_list|(
name|getBytesOnDisk
argument_list|()
argument_list|,
name|lastChecksum
argument_list|)
return|;
block|}
DECL|method|interruptThread ()
specifier|public
name|void
name|interruptThread
parameter_list|()
block|{
name|Thread
name|thread
init|=
name|writer
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|thread
operator|!=
literal|null
operator|&&
name|thread
operator|!=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|&&
name|thread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
comment|// Object
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/**    * Attempt to set the writer to a new value.    */
DECL|method|attemptToSetWriter (Thread prevWriter, Thread newWriter)
specifier|public
name|boolean
name|attemptToSetWriter
parameter_list|(
name|Thread
name|prevWriter
parameter_list|,
name|Thread
name|newWriter
parameter_list|)
block|{
return|return
name|writer
operator|.
name|compareAndSet
argument_list|(
name|prevWriter
argument_list|,
name|newWriter
argument_list|)
return|;
block|}
comment|/**    * Interrupt the writing thread and wait until it dies    * @throws IOException the waiting is interrupted    */
DECL|method|stopWriter (long xceiverStopTimeout)
specifier|public
name|void
name|stopWriter
parameter_list|(
name|long
name|xceiverStopTimeout
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
name|thread
init|=
name|writer
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|thread
operator|==
literal|null
operator|)
operator|||
operator|(
name|thread
operator|==
name|Thread
operator|.
name|currentThread
argument_list|()
operator|)
operator|||
operator|(
operator|!
name|thread
operator|.
name|isAlive
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|writer
operator|.
name|compareAndSet
argument_list|(
name|thread
argument_list|,
literal|null
argument_list|)
operator|==
literal|true
condition|)
block|{
return|return;
comment|// Done
block|}
comment|// The writer changed.  Go back to the start of the loop and attempt to
comment|// stop the new writer.
continue|continue;
block|}
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|thread
operator|.
name|join
argument_list|(
name|xceiverStopTimeout
argument_list|)
expr_stmt|;
if|if
condition|(
name|thread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
comment|// Our thread join timed out.
specifier|final
name|String
name|msg
init|=
literal|"Join on writer thread "
operator|+
name|thread
operator|+
literal|" timed out"
decl_stmt|;
name|DataNode
operator|.
name|LOG
operator|.
name|warn
argument_list|(
name|msg
operator|+
literal|"\n"
operator|+
name|StringUtils
operator|.
name|getStackTrace
argument_list|(
name|thread
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Waiting for writer thread is interrupted."
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
comment|// Object
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// ReplicaInPipelineInterface
DECL|method|createStreams (boolean isCreate, DataChecksum requestedChecksum)
specifier|public
name|ReplicaOutputStreams
name|createStreams
parameter_list|(
name|boolean
name|isCreate
parameter_list|,
name|DataChecksum
name|requestedChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|blockFile
init|=
name|getBlockFile
argument_list|()
decl_stmt|;
name|File
name|metaFile
init|=
name|getMetaFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|DataNode
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|DataNode
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"writeTo blockfile is "
operator|+
name|blockFile
operator|+
literal|" of size "
operator|+
name|blockFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|DataNode
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"writeTo metafile is "
operator|+
name|metaFile
operator|+
literal|" of size "
operator|+
name|metaFile
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|blockDiskSize
init|=
literal|0L
decl_stmt|;
name|long
name|crcDiskSize
init|=
literal|0L
decl_stmt|;
comment|// the checksum that should actually be used -- this
comment|// may differ from requestedChecksum for appends.
specifier|final
name|DataChecksum
name|checksum
decl_stmt|;
name|RandomAccessFile
name|metaRAF
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|metaFile
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isCreate
condition|)
block|{
comment|// For append or recovery, we must enforce the existing checksum.
comment|// Also, verify that the file has correct lengths, etc.
name|boolean
name|checkedMeta
init|=
literal|false
decl_stmt|;
try|try
block|{
name|BlockMetadataHeader
name|header
init|=
name|BlockMetadataHeader
operator|.
name|readHeader
argument_list|(
name|metaRAF
argument_list|)
decl_stmt|;
name|checksum
operator|=
name|header
operator|.
name|getChecksum
argument_list|()
expr_stmt|;
if|if
condition|(
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
operator|!=
name|requestedChecksum
operator|.
name|getBytesPerChecksum
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Client requested checksum "
operator|+
name|requestedChecksum
operator|+
literal|" when appending to an existing block "
operator|+
literal|"with different chunk size: "
operator|+
name|checksum
argument_list|)
throw|;
block|}
name|int
name|bytesPerChunk
init|=
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
decl_stmt|;
name|int
name|checksumSize
init|=
name|checksum
operator|.
name|getChecksumSize
argument_list|()
decl_stmt|;
name|blockDiskSize
operator|=
name|bytesOnDisk
expr_stmt|;
name|crcDiskSize
operator|=
name|BlockMetadataHeader
operator|.
name|getHeaderSize
argument_list|()
operator|+
operator|(
name|blockDiskSize
operator|+
name|bytesPerChunk
operator|-
literal|1
operator|)
operator|/
name|bytesPerChunk
operator|*
name|checksumSize
expr_stmt|;
if|if
condition|(
name|blockDiskSize
operator|>
literal|0
operator|&&
operator|(
name|blockDiskSize
operator|>
name|blockFile
operator|.
name|length
argument_list|()
operator|||
name|crcDiskSize
operator|>
name|metaFile
operator|.
name|length
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupted block: "
operator|+
name|this
argument_list|)
throw|;
block|}
name|checkedMeta
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|checkedMeta
condition|)
block|{
comment|// clean up in case of exceptions.
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|metaRAF
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// for create, we can use the requested checksum
name|checksum
operator|=
name|requestedChecksum
expr_stmt|;
block|}
name|FileOutputStream
name|blockOut
init|=
literal|null
decl_stmt|;
name|FileOutputStream
name|crcOut
init|=
literal|null
decl_stmt|;
try|try
block|{
name|blockOut
operator|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|RandomAccessFile
argument_list|(
name|blockFile
argument_list|,
literal|"rw"
argument_list|)
operator|.
name|getFD
argument_list|()
argument_list|)
expr_stmt|;
name|crcOut
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|metaRAF
operator|.
name|getFD
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isCreate
condition|)
block|{
name|blockOut
operator|.
name|getChannel
argument_list|()
operator|.
name|position
argument_list|(
name|blockDiskSize
argument_list|)
expr_stmt|;
name|crcOut
operator|.
name|getChannel
argument_list|()
operator|.
name|position
argument_list|(
name|crcDiskSize
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ReplicaOutputStreams
argument_list|(
name|blockOut
argument_list|,
name|crcOut
argument_list|,
name|checksum
argument_list|,
name|getVolume
argument_list|()
operator|.
name|isTransientStorage
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|metaRAF
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createRestartMetaStream ()
specifier|public
name|OutputStream
name|createRestartMetaStream
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|blockFile
init|=
name|getBlockFile
argument_list|()
decl_stmt|;
name|File
name|restartMeta
init|=
operator|new
name|File
argument_list|(
name|blockFile
operator|.
name|getParent
argument_list|()
operator|+
name|File
operator|.
name|pathSeparator
operator|+
literal|"."
operator|+
name|blockFile
operator|.
name|getName
argument_list|()
operator|+
literal|".restart"
argument_list|)
decl_stmt|;
if|if
condition|(
name|restartMeta
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|restartMeta
operator|.
name|delete
argument_list|()
condition|)
block|{
name|DataNode
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete restart meta file: "
operator|+
name|restartMeta
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FileOutputStream
argument_list|(
name|restartMeta
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"\n  bytesAcked="
operator|+
name|bytesAcked
operator|+
literal|"\n  bytesOnDisk="
operator|+
name|bytesOnDisk
return|;
block|}
block|}
end_class

end_unit

