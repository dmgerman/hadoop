begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|BufferedOutputStream
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
name|DataOutputStream
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|fs
operator|.
name|FSInputChecker
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
name|Path
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
name|ExtendedBlock
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
name|datatransfer
operator|.
name|DataTransferProtoUtil
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
name|datatransfer
operator|.
name|IOStreamPair
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
name|datatransfer
operator|.
name|PacketHeader
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
name|datatransfer
operator|.
name|Sender
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|BlockOpResponseProto
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|ReadOpChecksumInfoProto
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|Status
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
name|protocolPB
operator|.
name|PBHelper
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|net
operator|.
name|NetUtils
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
name|security
operator|.
name|token
operator|.
name|Token
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

begin_comment
comment|/**  * @deprecated this is an old implementation that is being left around  * in case any issues spring up with the new {@link RemoteBlockReader2} implementation.  * It will be removed in the next release.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|Deprecated
DECL|class|RemoteBlockReader
specifier|public
class|class
name|RemoteBlockReader
extends|extends
name|FSInputChecker
implements|implements
name|BlockReader
block|{
DECL|field|dnSock
name|Socket
name|dnSock
decl_stmt|;
comment|//for now just sending the status code (e.g. checksumOk) after the read.
DECL|field|in
specifier|private
specifier|final
name|DataInputStream
name|in
decl_stmt|;
DECL|field|checksum
specifier|private
name|DataChecksum
name|checksum
decl_stmt|;
comment|/** offset in block of the last chunk received */
DECL|field|lastChunkOffset
specifier|private
name|long
name|lastChunkOffset
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|lastChunkLen
specifier|private
name|long
name|lastChunkLen
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|lastSeqNo
specifier|private
name|long
name|lastSeqNo
init|=
operator|-
literal|1
decl_stmt|;
comment|/** offset in block where reader wants to actually read */
DECL|field|startOffset
specifier|private
name|long
name|startOffset
decl_stmt|;
comment|/** offset in block of of first chunk - may be less than startOffset       if startOffset is not chunk-aligned */
DECL|field|firstChunkOffset
specifier|private
specifier|final
name|long
name|firstChunkOffset
decl_stmt|;
DECL|field|bytesPerChecksum
specifier|private
specifier|final
name|int
name|bytesPerChecksum
decl_stmt|;
DECL|field|checksumSize
specifier|private
specifier|final
name|int
name|checksumSize
decl_stmt|;
comment|/**    * The total number of bytes we need to transfer from the DN.    * This is the amount that the user has requested plus some padding    * at the beginning so that the read can begin on a chunk boundary.    */
DECL|field|bytesNeededToFinish
specifier|private
specifier|final
name|long
name|bytesNeededToFinish
decl_stmt|;
DECL|field|eos
specifier|private
name|boolean
name|eos
init|=
literal|false
decl_stmt|;
DECL|field|sentStatusCode
specifier|private
name|boolean
name|sentStatusCode
init|=
literal|false
decl_stmt|;
DECL|field|skipBuf
name|byte
index|[]
name|skipBuf
init|=
literal|null
decl_stmt|;
DECL|field|checksumBytes
name|ByteBuffer
name|checksumBytes
init|=
literal|null
decl_stmt|;
comment|/** Amount of unread data in the current received packet */
DECL|field|dataLeft
name|int
name|dataLeft
init|=
literal|0
decl_stmt|;
comment|/* FSInputChecker interface */
comment|/* same interface as inputStream java.io.InputStream#read()    * used by DFSInputStream#read()    * This violates one rule when there is a checksum error:    * "Read should not modify user buffer before successful read"    * because it first reads the data to user buffer and then checks    * the checksum.    */
annotation|@
name|Override
DECL|method|read (byte[] buf, int off, int len)
specifier|public
specifier|synchronized
name|int
name|read
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|// This has to be set here, *before* the skip, since we can
comment|// hit EOS during the skip, in the case that our entire read
comment|// is smaller than the checksum chunk.
name|boolean
name|eosBefore
init|=
name|eos
decl_stmt|;
comment|//for the first read, skip the extra bytes at the front.
if|if
condition|(
name|lastChunkLen
argument_list|<
literal|0
operator|&&
name|startOffset
argument_list|>
name|firstChunkOffset
operator|&&
name|len
operator|>
literal|0
condition|)
block|{
comment|// Skip these bytes. But don't call this.skip()!
name|int
name|toSkip
init|=
call|(
name|int
call|)
argument_list|(
name|startOffset
operator|-
name|firstChunkOffset
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipBuf
operator|==
literal|null
condition|)
block|{
name|skipBuf
operator|=
operator|new
name|byte
index|[
name|bytesPerChecksum
index|]
expr_stmt|;
block|}
if|if
condition|(
name|super
operator|.
name|read
argument_list|(
name|skipBuf
argument_list|,
literal|0
argument_list|,
name|toSkip
argument_list|)
operator|!=
name|toSkip
condition|)
block|{
comment|// should never happen
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not skip required number of bytes"
argument_list|)
throw|;
block|}
block|}
name|int
name|nRead
init|=
name|super
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
comment|// if eos was set in the previous read, send a status code to the DN
if|if
condition|(
name|eos
operator|&&
operator|!
name|eosBefore
operator|&&
name|nRead
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|needChecksum
argument_list|()
condition|)
block|{
name|sendReadResult
argument_list|(
name|dnSock
argument_list|,
name|Status
operator|.
name|CHECKSUM_OK
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sendReadResult
argument_list|(
name|dnSock
argument_list|,
name|Status
operator|.
name|SUCCESS
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nRead
return|;
block|}
annotation|@
name|Override
DECL|method|skip (long n)
specifier|public
specifier|synchronized
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* How can we make sure we don't throw a ChecksumException, at least      * in majority of the cases?. This one throws. */
if|if
condition|(
name|skipBuf
operator|==
literal|null
condition|)
block|{
name|skipBuf
operator|=
operator|new
name|byte
index|[
name|bytesPerChecksum
index|]
expr_stmt|;
block|}
name|long
name|nSkipped
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|nSkipped
operator|<
name|n
condition|)
block|{
name|int
name|toSkip
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|n
operator|-
name|nSkipped
argument_list|,
name|skipBuf
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|ret
init|=
name|read
argument_list|(
name|skipBuf
argument_list|,
literal|0
argument_list|,
name|toSkip
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|<=
literal|0
condition|)
block|{
return|return
name|nSkipped
return|;
block|}
name|nSkipped
operator|+=
name|ret
expr_stmt|;
block|}
return|return
name|nSkipped
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read() is not expected to be invoked. "
operator|+
literal|"Use read(buf, off, len) instead."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* Checksum errors are handled outside the BlockReader.       * DFSInputStream does not always call 'seekToNewSource'. In the       * case of pread(), it just tries a different replica without seeking.      */
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|seek (long pos)
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Seek() is not supported in BlockInputChecker"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getChunkPosition (long pos)
specifier|protected
name|long
name|getChunkPosition
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"getChunkPosition() is not supported, "
operator|+
literal|"since seek is not required"
argument_list|)
throw|;
block|}
comment|/**    * Makes sure that checksumBytes has enough capacity     * and limit is set to the number of checksum bytes needed     * to be read.    */
DECL|method|adjustChecksumBytes (int dataLen)
specifier|private
name|void
name|adjustChecksumBytes
parameter_list|(
name|int
name|dataLen
parameter_list|)
block|{
name|int
name|requiredSize
init|=
operator|(
operator|(
name|dataLen
operator|+
name|bytesPerChecksum
operator|-
literal|1
operator|)
operator|/
name|bytesPerChecksum
operator|)
operator|*
name|checksumSize
decl_stmt|;
if|if
condition|(
name|checksumBytes
operator|==
literal|null
operator|||
name|requiredSize
operator|>
name|checksumBytes
operator|.
name|capacity
argument_list|()
condition|)
block|{
name|checksumBytes
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[
name|requiredSize
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checksumBytes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|checksumBytes
operator|.
name|limit
argument_list|(
name|requiredSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readChunk (long pos, byte[] buf, int offset, int len, byte[] checksumBuf)
specifier|protected
specifier|synchronized
name|int
name|readChunk
parameter_list|(
name|long
name|pos
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|byte
index|[]
name|checksumBuf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Read one chunk.
if|if
condition|(
name|eos
condition|)
block|{
comment|// Already hit EOF
return|return
operator|-
literal|1
return|;
block|}
comment|// Read one DATA_CHUNK.
name|long
name|chunkOffset
init|=
name|lastChunkOffset
decl_stmt|;
if|if
condition|(
name|lastChunkLen
operator|>
literal|0
condition|)
block|{
name|chunkOffset
operator|+=
name|lastChunkLen
expr_stmt|;
block|}
comment|// pos is relative to the start of the first chunk of the read.
comment|// chunkOffset is relative to the start of the block.
comment|// This makes sure that the read passed from FSInputChecker is the
comment|// for the same chunk we expect to be reading from the DN.
if|if
condition|(
operator|(
name|pos
operator|+
name|firstChunkOffset
operator|)
operator|!=
name|chunkOffset
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mismatch in pos : "
operator|+
name|pos
operator|+
literal|" + "
operator|+
name|firstChunkOffset
operator|+
literal|" != "
operator|+
name|chunkOffset
argument_list|)
throw|;
block|}
comment|// Read next packet if the previous packet has been read completely.
if|if
condition|(
name|dataLeft
operator|<=
literal|0
condition|)
block|{
comment|//Read packet headers.
name|PacketHeader
name|header
init|=
operator|new
name|PacketHeader
argument_list|()
decl_stmt|;
name|header
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"DFSClient readChunk got header "
operator|+
name|header
argument_list|)
expr_stmt|;
block|}
comment|// Sanity check the lengths
if|if
condition|(
operator|!
name|header
operator|.
name|sanityCheck
argument_list|(
name|lastSeqNo
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"BlockReader: error in packet header "
operator|+
name|header
argument_list|)
throw|;
block|}
name|lastSeqNo
operator|=
name|header
operator|.
name|getSeqno
argument_list|()
expr_stmt|;
name|dataLeft
operator|=
name|header
operator|.
name|getDataLen
argument_list|()
expr_stmt|;
name|adjustChecksumBytes
argument_list|(
name|header
operator|.
name|getDataLen
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|header
operator|.
name|getDataLen
argument_list|()
operator|>
literal|0
condition|)
block|{
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|checksumBytes
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|checksumBytes
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Sanity checks
assert|assert
name|len
operator|>=
name|bytesPerChecksum
assert|;
assert|assert
name|checksum
operator|!=
literal|null
assert|;
assert|assert
name|checksumSize
operator|==
literal|0
operator|||
operator|(
name|checksumBuf
operator|.
name|length
operator|%
name|checksumSize
operator|==
literal|0
operator|)
assert|;
name|int
name|checksumsToRead
decl_stmt|,
name|bytesToRead
decl_stmt|;
if|if
condition|(
name|checksumSize
operator|>
literal|0
condition|)
block|{
comment|// How many chunks left in our packet - this is a ceiling
comment|// since we may have a partial chunk at the end of the file
name|int
name|chunksLeft
init|=
operator|(
name|dataLeft
operator|-
literal|1
operator|)
operator|/
name|bytesPerChecksum
operator|+
literal|1
decl_stmt|;
comment|// How many chunks we can fit in databuffer
comment|//  - note this is a floor since we always read full chunks
name|int
name|chunksCanFit
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
operator|/
name|bytesPerChecksum
argument_list|,
name|checksumBuf
operator|.
name|length
operator|/
name|checksumSize
argument_list|)
decl_stmt|;
comment|// How many chunks should we read
name|checksumsToRead
operator|=
name|Math
operator|.
name|min
argument_list|(
name|chunksLeft
argument_list|,
name|chunksCanFit
argument_list|)
expr_stmt|;
comment|// How many bytes should we actually read
name|bytesToRead
operator|=
name|Math
operator|.
name|min
argument_list|(
name|checksumsToRead
operator|*
name|bytesPerChecksum
argument_list|,
comment|// full chunks
name|dataLeft
argument_list|)
expr_stmt|;
comment|// in case we have a partial
block|}
else|else
block|{
comment|// no checksum
name|bytesToRead
operator|=
name|Math
operator|.
name|min
argument_list|(
name|dataLeft
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|checksumsToRead
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|bytesToRead
operator|>
literal|0
condition|)
block|{
comment|// Assert we have enough space
assert|assert
name|bytesToRead
operator|<=
name|len
assert|;
assert|assert
name|checksumBytes
operator|.
name|remaining
argument_list|()
operator|>=
name|checksumSize
operator|*
name|checksumsToRead
assert|;
assert|assert
name|checksumBuf
operator|.
name|length
operator|>=
name|checksumSize
operator|*
name|checksumsToRead
assert|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|,
name|bytesToRead
argument_list|)
expr_stmt|;
name|checksumBytes
operator|.
name|get
argument_list|(
name|checksumBuf
argument_list|,
literal|0
argument_list|,
name|checksumSize
operator|*
name|checksumsToRead
argument_list|)
expr_stmt|;
block|}
name|dataLeft
operator|-=
name|bytesToRead
expr_stmt|;
assert|assert
name|dataLeft
operator|>=
literal|0
assert|;
name|lastChunkOffset
operator|=
name|chunkOffset
expr_stmt|;
name|lastChunkLen
operator|=
name|bytesToRead
expr_stmt|;
comment|// If there's no data left in the current packet after satisfying
comment|// this read, and we have satisfied the client read, we expect
comment|// an empty packet header from the DN to signify this.
comment|// Note that pos + bytesToRead may in fact be greater since the
comment|// DN finishes off the entire last chunk.
if|if
condition|(
name|dataLeft
operator|==
literal|0
operator|&&
name|pos
operator|+
name|bytesToRead
operator|>=
name|bytesNeededToFinish
condition|)
block|{
comment|// Read header
name|PacketHeader
name|hdr
init|=
operator|new
name|PacketHeader
argument_list|()
decl_stmt|;
name|hdr
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hdr
operator|.
name|isLastPacketInBlock
argument_list|()
operator|||
name|hdr
operator|.
name|getDataLen
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expected empty end-of-read packet! Header: "
operator|+
name|hdr
argument_list|)
throw|;
block|}
name|eos
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|bytesToRead
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|bytesToRead
return|;
block|}
DECL|method|RemoteBlockReader (String file, String bpid, long blockId, DataInputStream in, DataChecksum checksum, boolean verifyChecksum, long startOffset, long firstChunkOffset, long bytesToRead, Socket dnSock)
specifier|private
name|RemoteBlockReader
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|,
name|DataInputStream
name|in
parameter_list|,
name|DataChecksum
name|checksum
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|,
name|long
name|startOffset
parameter_list|,
name|long
name|firstChunkOffset
parameter_list|,
name|long
name|bytesToRead
parameter_list|,
name|Socket
name|dnSock
parameter_list|)
block|{
comment|// Path is used only for printing block and file information in debug
name|super
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/blk_"
operator|+
name|blockId
operator|+
literal|":"
operator|+
name|bpid
operator|+
literal|":of:"
operator|+
name|file
argument_list|)
comment|/*too non path-like?*/
argument_list|,
literal|1
argument_list|,
name|verifyChecksum
argument_list|,
name|checksum
operator|.
name|getChecksumSize
argument_list|()
operator|>
literal|0
condition|?
name|checksum
else|:
literal|null
argument_list|,
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
argument_list|,
name|checksum
operator|.
name|getChecksumSize
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|dnSock
operator|=
name|dnSock
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|checksum
operator|=
name|checksum
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
name|startOffset
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// The total number of bytes that we need to transfer from the DN is
comment|// the amount that the user wants (bytesToRead), plus the padding at
comment|// the beginning in order to chunk-align. Note that the DN may elect
comment|// to send more than this amount if the read starts/ends mid-chunk.
name|this
operator|.
name|bytesNeededToFinish
operator|=
name|bytesToRead
operator|+
operator|(
name|startOffset
operator|-
name|firstChunkOffset
operator|)
expr_stmt|;
name|this
operator|.
name|firstChunkOffset
operator|=
name|firstChunkOffset
expr_stmt|;
name|lastChunkOffset
operator|=
name|firstChunkOffset
expr_stmt|;
name|lastChunkLen
operator|=
operator|-
literal|1
expr_stmt|;
name|bytesPerChecksum
operator|=
name|this
operator|.
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
expr_stmt|;
name|checksumSize
operator|=
name|this
operator|.
name|checksum
operator|.
name|getChecksumSize
argument_list|()
expr_stmt|;
block|}
DECL|method|newBlockReader (Socket sock, String file, ExtendedBlock block, Token<BlockTokenIdentifier> blockToken, long startOffset, long len, int bufferSize)
specifier|public
specifier|static
name|RemoteBlockReader
name|newBlockReader
parameter_list|(
name|Socket
name|sock
parameter_list|,
name|String
name|file
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|long
name|startOffset
parameter_list|,
name|long
name|len
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|newBlockReader
argument_list|(
name|sock
argument_list|,
name|file
argument_list|,
name|block
argument_list|,
name|blockToken
argument_list|,
name|startOffset
argument_list|,
name|len
argument_list|,
name|bufferSize
argument_list|,
literal|true
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/**    * Create a new BlockReader specifically to satisfy a read.    * This method also sends the OP_READ_BLOCK request.    *    * @param sock  An established Socket to the DN. The BlockReader will not close it normally    * @param file  File location    * @param block  The block object    * @param blockToken  The block token for security    * @param startOffset  The read offset, relative to block head    * @param len  The number of bytes to read    * @param bufferSize  The IO buffer size (not the client buffer size)    * @param verifyChecksum  Whether to verify checksum    * @param clientName  Client name    * @return New BlockReader instance, or null on error.    */
DECL|method|newBlockReader ( Socket sock, String file, ExtendedBlock block, Token<BlockTokenIdentifier> blockToken, long startOffset, long len, int bufferSize, boolean verifyChecksum, String clientName)
specifier|public
specifier|static
name|RemoteBlockReader
name|newBlockReader
parameter_list|(
name|Socket
name|sock
parameter_list|,
name|String
name|file
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|long
name|startOffset
parameter_list|,
name|long
name|len
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|,
name|String
name|clientName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// in and out will be closed when sock is closed (by the caller)
specifier|final
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|NetUtils
operator|.
name|getOutputStream
argument_list|(
name|sock
argument_list|,
name|HdfsServerConstants
operator|.
name|WRITE_TIMEOUT
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
operator|new
name|Sender
argument_list|(
name|out
argument_list|)
operator|.
name|readBlock
argument_list|(
name|block
argument_list|,
name|blockToken
argument_list|,
name|clientName
argument_list|,
name|startOffset
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|//
comment|// Get bytes in block, set streams
comment|//
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|NetUtils
operator|.
name|getInputStream
argument_list|(
name|sock
argument_list|)
argument_list|,
name|bufferSize
argument_list|)
argument_list|)
decl_stmt|;
name|BlockOpResponseProto
name|status
init|=
name|BlockOpResponseProto
operator|.
name|parseFrom
argument_list|(
name|PBHelper
operator|.
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|RemoteBlockReader2
operator|.
name|checkSuccess
argument_list|(
name|status
argument_list|,
name|sock
argument_list|,
name|block
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|ReadOpChecksumInfoProto
name|checksumInfo
init|=
name|status
operator|.
name|getReadOpChecksumInfo
argument_list|()
decl_stmt|;
name|DataChecksum
name|checksum
init|=
name|DataTransferProtoUtil
operator|.
name|fromProto
argument_list|(
name|checksumInfo
operator|.
name|getChecksum
argument_list|()
argument_list|)
decl_stmt|;
comment|//Warning when we get CHECKSUM_NULL?
comment|// Read the first chunk offset.
name|long
name|firstChunkOffset
init|=
name|checksumInfo
operator|.
name|getChunkOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstChunkOffset
argument_list|<
literal|0
operator|||
name|firstChunkOffset
argument_list|>
name|startOffset
operator|||
name|firstChunkOffset
operator|<=
operator|(
name|startOffset
operator|-
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"BlockReader: error in first chunk offset ("
operator|+
name|firstChunkOffset
operator|+
literal|") startOffset is "
operator|+
name|startOffset
operator|+
literal|" for file "
operator|+
name|file
argument_list|)
throw|;
block|}
return|return
operator|new
name|RemoteBlockReader
argument_list|(
name|file
argument_list|,
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|in
argument_list|,
name|checksum
argument_list|,
name|verifyChecksum
argument_list|,
name|startOffset
argument_list|,
name|firstChunkOffset
argument_list|,
name|len
argument_list|,
name|sock
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|startOffset
operator|=
operator|-
literal|1
expr_stmt|;
name|checksum
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|dnSock
operator|!=
literal|null
condition|)
block|{
name|dnSock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// in will be closed when its Socket is closed.
block|}
annotation|@
name|Override
DECL|method|readFully (byte[] buf, int readOffset, int amtToRead)
specifier|public
name|void
name|readFully
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|readOffset
parameter_list|,
name|int
name|amtToRead
parameter_list|)
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|readFully
argument_list|(
name|this
argument_list|,
name|buf
argument_list|,
name|readOffset
argument_list|,
name|amtToRead
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readAll (byte[] buf, int offset, int len)
specifier|public
name|int
name|readAll
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFully
argument_list|(
name|this
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|takeSocket ()
specifier|public
name|Socket
name|takeSocket
parameter_list|()
block|{
assert|assert
name|hasSentStatusCode
argument_list|()
operator|:
literal|"BlockReader shouldn't give back sockets mid-read"
assert|;
name|Socket
name|res
init|=
name|dnSock
decl_stmt|;
name|dnSock
operator|=
literal|null
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|hasSentStatusCode ()
specifier|public
name|boolean
name|hasSentStatusCode
parameter_list|()
block|{
return|return
name|sentStatusCode
return|;
block|}
comment|/**    * When the reader reaches end of the read, it sends a status response    * (e.g. CHECKSUM_OK) to the DN. Failure to do so could lead to the DN    * closing our connection (which we will re-open), but won't affect    * data correctness.    */
DECL|method|sendReadResult (Socket sock, Status statusCode)
name|void
name|sendReadResult
parameter_list|(
name|Socket
name|sock
parameter_list|,
name|Status
name|statusCode
parameter_list|)
block|{
assert|assert
operator|!
name|sentStatusCode
operator|:
literal|"already sent status code to "
operator|+
name|sock
assert|;
try|try
block|{
name|RemoteBlockReader2
operator|.
name|writeReadResult
argument_list|(
name|NetUtils
operator|.
name|getOutputStream
argument_list|(
name|sock
argument_list|,
name|HdfsServerConstants
operator|.
name|WRITE_TIMEOUT
argument_list|)
argument_list|,
name|statusCode
argument_list|)
expr_stmt|;
name|sentStatusCode
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// It's ok not to be able to send this. But something is probably wrong.
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not send read status ("
operator|+
name|statusCode
operator|+
literal|") to datanode "
operator|+
name|sock
operator|.
name|getInetAddress
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * File name to print when accessing a block directly (from servlets)    * @param s Address of the block location    * @param poolId Block pool ID of the block    * @param blockId Block ID of the block    * @return string that has a file name for debug purposes    */
DECL|method|getFileName (final InetSocketAddress s, final String poolId, final long blockId)
specifier|public
specifier|static
name|String
name|getFileName
parameter_list|(
specifier|final
name|InetSocketAddress
name|s
parameter_list|,
specifier|final
name|String
name|poolId
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|)
block|{
return|return
name|s
operator|.
name|toString
argument_list|()
operator|+
literal|":"
operator|+
name|poolId
operator|+
literal|":"
operator|+
name|blockId
return|;
block|}
annotation|@
name|Override
DECL|method|read (ByteBuffer buf)
specifier|public
name|int
name|read
parameter_list|(
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"readDirect unsupported in RemoteBlockReader"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getStreams ()
specifier|public
name|IOStreamPair
name|getStreams
parameter_list|()
block|{
comment|// This class doesn't support encryption, which is the only thing this
comment|// method is used for. See HDFS-3637.
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

