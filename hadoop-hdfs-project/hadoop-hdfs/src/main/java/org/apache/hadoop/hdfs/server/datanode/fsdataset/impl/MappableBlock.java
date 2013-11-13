begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
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
operator|.
name|fsdataset
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|MappedByteBuffer
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
name|FileChannel
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
name|FileChannel
operator|.
name|MapMode
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|ChecksumException
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
name|BlockMetadataHeader
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
name|nativeio
operator|.
name|NativeIO
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Represents an HDFS block that is mmapped by the DataNode.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MappableBlock
specifier|public
class|class
name|MappableBlock
implements|implements
name|Closeable
block|{
DECL|field|mmap
specifier|private
name|MappedByteBuffer
name|mmap
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|method|MappableBlock (MappedByteBuffer mmap, long length)
name|MappableBlock
parameter_list|(
name|MappedByteBuffer
name|mmap
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|mmap
operator|=
name|mmap
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
assert|assert
name|length
operator|>
literal|0
assert|;
block|}
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**    * Load the block.    *    * mmap and mlock the block, and then verify its checksum.    *    * @param length         The current length of the block.    * @param blockIn        The block input stream.  Should be positioned at the    *                       start.  The caller must close this.    * @param metaIn         The meta file input stream.  Should be positioned at    *                       the start.  The caller must close this.    * @param blockFileName  The block file name, for logging purposes.    *    * @return               The Mappable block.    */
DECL|method|load (long length, FileInputStream blockIn, FileInputStream metaIn, String blockFileName)
specifier|public
specifier|static
name|MappableBlock
name|load
parameter_list|(
name|long
name|length
parameter_list|,
name|FileInputStream
name|blockIn
parameter_list|,
name|FileInputStream
name|metaIn
parameter_list|,
name|String
name|blockFileName
parameter_list|)
throws|throws
name|IOException
block|{
name|MappableBlock
name|mappableBlock
init|=
literal|null
decl_stmt|;
name|MappedByteBuffer
name|mmap
init|=
literal|null
decl_stmt|;
try|try
block|{
name|FileChannel
name|blockChannel
init|=
name|blockIn
operator|.
name|getChannel
argument_list|()
decl_stmt|;
if|if
condition|(
name|blockChannel
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Block InputStream has no FileChannel."
argument_list|)
throw|;
block|}
name|mmap
operator|=
name|blockChannel
operator|.
name|map
argument_list|(
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|NativeIO
operator|.
name|POSIX
operator|.
name|cacheManipulator
operator|.
name|mlock
argument_list|(
name|blockFileName
argument_list|,
name|mmap
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|verifyChecksum
argument_list|(
name|length
argument_list|,
name|metaIn
argument_list|,
name|blockChannel
argument_list|,
name|blockFileName
argument_list|)
expr_stmt|;
name|mappableBlock
operator|=
operator|new
name|MappableBlock
argument_list|(
name|mmap
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|mappableBlock
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|mmap
operator|!=
literal|null
condition|)
block|{
name|NativeIO
operator|.
name|POSIX
operator|.
name|munmap
argument_list|(
name|mmap
argument_list|)
expr_stmt|;
comment|// unmapping also unlocks
block|}
block|}
block|}
return|return
name|mappableBlock
return|;
block|}
comment|/**    * Verifies the block's checksum. This is an I/O intensive operation.    * @return if the block was successfully checksummed.    */
DECL|method|verifyChecksum (long length, FileInputStream metaIn, FileChannel blockChannel, String blockFileName)
specifier|private
specifier|static
name|void
name|verifyChecksum
parameter_list|(
name|long
name|length
parameter_list|,
name|FileInputStream
name|metaIn
parameter_list|,
name|FileChannel
name|blockChannel
parameter_list|,
name|String
name|blockFileName
parameter_list|)
throws|throws
name|IOException
throws|,
name|ChecksumException
block|{
comment|// Verify the checksum from the block's meta file
comment|// Get the DataChecksum from the meta file header
name|BlockMetadataHeader
name|header
init|=
name|BlockMetadataHeader
operator|.
name|readHeader
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|metaIn
argument_list|,
name|BlockMetadataHeader
operator|.
name|getHeaderSize
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FileChannel
name|metaChannel
init|=
name|metaIn
operator|.
name|getChannel
argument_list|()
decl_stmt|;
if|if
condition|(
name|metaChannel
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Block InputStream meta file has no FileChannel."
argument_list|)
throw|;
block|}
name|DataChecksum
name|checksum
init|=
name|header
operator|.
name|getChecksum
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bytesPerChecksum
init|=
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
decl_stmt|;
specifier|final
name|int
name|checksumSize
init|=
name|checksum
operator|.
name|getChecksumSize
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numChunks
init|=
operator|(
literal|8
operator|*
literal|1024
operator|*
literal|1024
operator|)
operator|/
name|bytesPerChecksum
decl_stmt|;
name|ByteBuffer
name|blockBuf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|numChunks
operator|*
name|bytesPerChecksum
argument_list|)
decl_stmt|;
name|ByteBuffer
name|checksumBuf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|numChunks
operator|*
name|checksumSize
argument_list|)
decl_stmt|;
comment|// Verify the checksum
name|int
name|bytesVerified
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bytesVerified
operator|<
name|length
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|bytesVerified
operator|%
name|bytesPerChecksum
operator|==
literal|0
argument_list|,
literal|"Unexpected partial chunk before EOF"
argument_list|)
expr_stmt|;
assert|assert
name|bytesVerified
operator|%
name|bytesPerChecksum
operator|==
literal|0
assert|;
name|int
name|bytesRead
init|=
name|fillBuffer
argument_list|(
name|blockChannel
argument_list|,
name|blockBuf
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"checksum verification failed: premature EOF"
argument_list|)
throw|;
block|}
name|blockBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
comment|// Number of read chunks, including partial chunk at end
name|int
name|chunks
init|=
operator|(
name|bytesRead
operator|+
name|bytesPerChecksum
operator|-
literal|1
operator|)
operator|/
name|bytesPerChecksum
decl_stmt|;
name|checksumBuf
operator|.
name|limit
argument_list|(
name|chunks
operator|*
name|checksumSize
argument_list|)
expr_stmt|;
name|fillBuffer
argument_list|(
name|metaChannel
argument_list|,
name|checksumBuf
argument_list|)
expr_stmt|;
name|checksumBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
name|checksum
operator|.
name|verifyChunkedSums
argument_list|(
name|blockBuf
argument_list|,
name|checksumBuf
argument_list|,
name|blockFileName
argument_list|,
name|bytesVerified
argument_list|)
expr_stmt|;
comment|// Success
name|bytesVerified
operator|+=
name|bytesRead
expr_stmt|;
name|blockBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|checksumBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Reads bytes into a buffer until EOF or the buffer's limit is reached    */
DECL|method|fillBuffer (FileChannel channel, ByteBuffer buf)
specifier|private
specifier|static
name|int
name|fillBuffer
parameter_list|(
name|FileChannel
name|channel
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bytesRead
init|=
name|channel
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesRead
operator|<
literal|0
condition|)
block|{
comment|//EOF
return|return
name|bytesRead
return|;
block|}
while|while
condition|(
name|buf
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|channel
operator|.
name|read
argument_list|(
name|buf
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
comment|//EOF
return|return
name|bytesRead
return|;
block|}
name|bytesRead
operator|+=
name|n
expr_stmt|;
block|}
return|return
name|bytesRead
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|mmap
operator|!=
literal|null
condition|)
block|{
name|NativeIO
operator|.
name|POSIX
operator|.
name|munmap
argument_list|(
name|mmap
argument_list|)
expr_stmt|;
name|mmap
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

