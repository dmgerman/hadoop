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
import|import static
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
name|HdfsProtoUtil
operator|.
name|vintPrefixed
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
name|io
operator|.
name|OutputStream
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
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ReadableByteChannel
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|ClientReadStatusProto
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|InvalidBlockTokenException
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
name|hdfs
operator|.
name|util
operator|.
name|DirectBufferPool
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
name|net
operator|.
name|SocketInputWrapper
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
comment|/**  * This is a wrapper around connection to datanode  * and understands checksum, offset etc.  *  * Terminology:  *<dl>  *<dt>block</dt>  *<dd>The hdfs block, typically large (~64MB).  *</dd>  *<dt>chunk</dt>  *<dd>A block is divided into chunks, each comes with a checksum.  *       We want transfers to be chunk-aligned, to be able to  *       verify checksums.  *</dd>  *<dt>packet</dt>  *<dd>A grouping of chunks used for transport. It contains a  *       header, followed by checksum data, followed by real data.  *</dd>  *</dl>  * Please see DataNode for the RPC specification.  *  * This is a new implementation introduced in Hadoop 0.23 which  * is more efficient and simpler than the older BlockReader  * implementation. It should be renamed to RemoteBlockReader  * once we are confident in it.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RemoteBlockReader2
specifier|public
class|class
name|RemoteBlockReader2
implements|implements
name|BlockReader
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RemoteBlockReader2
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dnSock
name|Socket
name|dnSock
decl_stmt|;
comment|//for now just sending the status code (e.g. checksumOk) after the read.
DECL|field|in
specifier|private
specifier|final
name|ReadableByteChannel
name|in
decl_stmt|;
DECL|field|checksum
specifier|private
name|DataChecksum
name|checksum
decl_stmt|;
DECL|field|curHeader
specifier|private
name|PacketHeader
name|curHeader
decl_stmt|;
DECL|field|curPacketBuf
specifier|private
name|ByteBuffer
name|curPacketBuf
init|=
literal|null
decl_stmt|;
DECL|field|curDataSlice
specifier|private
name|ByteBuffer
name|curDataSlice
init|=
literal|null
decl_stmt|;
comment|/** offset in block of the last chunk received */
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
DECL|field|filename
specifier|private
specifier|final
name|String
name|filename
decl_stmt|;
DECL|field|bufferPool
specifier|private
specifier|static
name|DirectBufferPool
name|bufferPool
init|=
operator|new
name|DirectBufferPool
argument_list|()
decl_stmt|;
DECL|field|headerBuf
specifier|private
specifier|final
name|ByteBuffer
name|headerBuf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|PacketHeader
operator|.
name|PKT_HEADER_LEN
argument_list|)
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
name|long
name|bytesNeededToFinish
decl_stmt|;
DECL|field|verifyChecksum
specifier|private
specifier|final
name|boolean
name|verifyChecksum
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
if|if
condition|(
name|curPacketBuf
operator|==
literal|null
operator|||
name|curDataSlice
operator|.
name|remaining
argument_list|()
operator|==
literal|0
operator|&&
name|bytesNeededToFinish
operator|>
literal|0
condition|)
block|{
name|readNextPacket
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|curDataSlice
operator|.
name|remaining
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// we're at EOF now
return|return
operator|-
literal|1
return|;
block|}
name|int
name|nRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|curDataSlice
operator|.
name|remaining
argument_list|()
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|curDataSlice
operator|.
name|get
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|nRead
argument_list|)
expr_stmt|;
return|return
name|nRead
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
if|if
condition|(
name|curPacketBuf
operator|==
literal|null
operator|||
name|curDataSlice
operator|.
name|remaining
argument_list|()
operator|==
literal|0
operator|&&
name|bytesNeededToFinish
operator|>
literal|0
condition|)
block|{
name|readNextPacket
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|curDataSlice
operator|.
name|remaining
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// we're at EOF now
return|return
operator|-
literal|1
return|;
block|}
name|int
name|nRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|curDataSlice
operator|.
name|remaining
argument_list|()
argument_list|,
name|buf
operator|.
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
name|ByteBuffer
name|writeSlice
init|=
name|curDataSlice
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|writeSlice
operator|.
name|limit
argument_list|(
name|writeSlice
operator|.
name|position
argument_list|()
operator|+
name|nRead
argument_list|)
expr_stmt|;
name|buf
operator|.
name|put
argument_list|(
name|writeSlice
argument_list|)
expr_stmt|;
name|curDataSlice
operator|.
name|position
argument_list|(
name|writeSlice
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|nRead
return|;
block|}
DECL|method|readNextPacket ()
specifier|private
name|void
name|readNextPacket
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|curHeader
operator|==
literal|null
operator|||
operator|!
name|curHeader
operator|.
name|isLastPacketInBlock
argument_list|()
argument_list|)
expr_stmt|;
comment|//Read packet headers.
name|readPacketHeader
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"DFSClient readNextPacket got header "
operator|+
name|curHeader
argument_list|)
expr_stmt|;
block|}
comment|// Sanity check the lengths
if|if
condition|(
operator|!
name|curHeader
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
name|curHeader
argument_list|)
throw|;
block|}
if|if
condition|(
name|curHeader
operator|.
name|getDataLen
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|chunks
init|=
literal|1
operator|+
operator|(
name|curHeader
operator|.
name|getDataLen
argument_list|()
operator|-
literal|1
operator|)
operator|/
name|bytesPerChecksum
decl_stmt|;
name|int
name|checksumsLen
init|=
name|chunks
operator|*
name|checksumSize
decl_stmt|;
name|int
name|bufsize
init|=
name|checksumsLen
operator|+
name|curHeader
operator|.
name|getDataLen
argument_list|()
decl_stmt|;
name|resetPacketBuffer
argument_list|(
name|checksumsLen
argument_list|,
name|curHeader
operator|.
name|getDataLen
argument_list|()
argument_list|)
expr_stmt|;
name|lastSeqNo
operator|=
name|curHeader
operator|.
name|getSeqno
argument_list|()
expr_stmt|;
if|if
condition|(
name|bufsize
operator|>
literal|0
condition|)
block|{
name|readChannelFully
argument_list|(
name|in
argument_list|,
name|curPacketBuf
argument_list|)
expr_stmt|;
name|curPacketBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
if|if
condition|(
name|verifyChecksum
condition|)
block|{
name|verifyPacketChecksums
argument_list|()
expr_stmt|;
block|}
block|}
name|bytesNeededToFinish
operator|-=
name|curHeader
operator|.
name|getDataLen
argument_list|()
expr_stmt|;
block|}
comment|// First packet will include some data prior to the first byte
comment|// the user requested. Skip it.
if|if
condition|(
name|curHeader
operator|.
name|getOffsetInBlock
argument_list|()
operator|<
name|startOffset
condition|)
block|{
name|int
name|newPos
init|=
call|(
name|int
call|)
argument_list|(
name|startOffset
operator|-
name|curHeader
operator|.
name|getOffsetInBlock
argument_list|()
argument_list|)
decl_stmt|;
name|curDataSlice
operator|.
name|position
argument_list|(
name|newPos
argument_list|)
expr_stmt|;
block|}
comment|// If we've now satisfied the whole client read, read one last packet
comment|// header, which should be empty
if|if
condition|(
name|bytesNeededToFinish
operator|<=
literal|0
condition|)
block|{
name|readTrailingEmptyPacket
argument_list|()
expr_stmt|;
if|if
condition|(
name|verifyChecksum
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
block|}
DECL|method|verifyPacketChecksums ()
specifier|private
name|void
name|verifyPacketChecksums
parameter_list|()
throws|throws
name|ChecksumException
block|{
comment|// N.B.: the checksum error offset reported here is actually
comment|// relative to the start of the block, not the start of the file.
comment|// This is slightly misleading, but preserves the behavior from
comment|// the older BlockReader.
name|checksum
operator|.
name|verifyChunkedSums
argument_list|(
name|curDataSlice
argument_list|,
name|curPacketBuf
argument_list|,
name|filename
argument_list|,
name|curHeader
operator|.
name|getOffsetInBlock
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readChannelFully (ReadableByteChannel ch, ByteBuffer buf)
specifier|private
specifier|static
name|void
name|readChannelFully
parameter_list|(
name|ReadableByteChannel
name|ch
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
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
name|ch
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Premature EOF reading from "
operator|+
name|ch
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|resetPacketBuffer (int checksumsLen, int dataLen)
specifier|private
name|void
name|resetPacketBuffer
parameter_list|(
name|int
name|checksumsLen
parameter_list|,
name|int
name|dataLen
parameter_list|)
block|{
name|int
name|packetLen
init|=
name|checksumsLen
operator|+
name|dataLen
decl_stmt|;
if|if
condition|(
name|curPacketBuf
operator|==
literal|null
operator|||
name|curPacketBuf
operator|.
name|capacity
argument_list|()
operator|<
name|packetLen
condition|)
block|{
name|returnPacketBufToPool
argument_list|()
expr_stmt|;
name|curPacketBuf
operator|=
name|bufferPool
operator|.
name|getBuffer
argument_list|(
name|packetLen
argument_list|)
expr_stmt|;
block|}
name|curPacketBuf
operator|.
name|position
argument_list|(
name|checksumsLen
argument_list|)
expr_stmt|;
name|curDataSlice
operator|=
name|curPacketBuf
operator|.
name|slice
argument_list|()
expr_stmt|;
name|curDataSlice
operator|.
name|limit
argument_list|(
name|dataLen
argument_list|)
expr_stmt|;
name|curPacketBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|curPacketBuf
operator|.
name|limit
argument_list|(
name|checksumsLen
operator|+
name|dataLen
argument_list|)
expr_stmt|;
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
DECL|method|readPacketHeader ()
specifier|private
name|void
name|readPacketHeader
parameter_list|()
throws|throws
name|IOException
block|{
name|headerBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|readChannelFully
argument_list|(
name|in
argument_list|,
name|headerBuf
argument_list|)
expr_stmt|;
name|headerBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
if|if
condition|(
name|curHeader
operator|==
literal|null
condition|)
name|curHeader
operator|=
operator|new
name|PacketHeader
argument_list|()
expr_stmt|;
name|curHeader
operator|.
name|readFields
argument_list|(
name|headerBuf
argument_list|)
expr_stmt|;
block|}
DECL|method|readTrailingEmptyPacket ()
specifier|private
name|void
name|readTrailingEmptyPacket
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Reading empty packet at end of read"
argument_list|)
expr_stmt|;
block|}
name|headerBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|readChannelFully
argument_list|(
name|in
argument_list|,
name|headerBuf
argument_list|)
expr_stmt|;
name|headerBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
name|PacketHeader
name|trailer
init|=
operator|new
name|PacketHeader
argument_list|()
decl_stmt|;
name|trailer
operator|.
name|readFields
argument_list|(
name|headerBuf
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|trailer
operator|.
name|isLastPacketInBlock
argument_list|()
operator|||
name|trailer
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
name|trailer
argument_list|)
throw|;
block|}
block|}
DECL|method|RemoteBlockReader2 (String file, String bpid, long blockId, ReadableByteChannel in, DataChecksum checksum, boolean verifyChecksum, long startOffset, long firstChunkOffset, long bytesToRead, Socket dnSock)
specifier|protected
name|RemoteBlockReader2
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
name|ReadableByteChannel
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
name|verifyChecksum
operator|=
name|verifyChecksum
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
name|this
operator|.
name|filename
operator|=
name|file
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
name|returnPacketBufToPool
argument_list|()
expr_stmt|;
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
DECL|method|finalize ()
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
comment|// just in case it didn't get closed, we
comment|// may as well still try to return the buffer
name|returnPacketBufToPool
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|returnPacketBufToPool ()
specifier|private
name|void
name|returnPacketBufToPool
parameter_list|()
block|{
if|if
condition|(
name|curPacketBuf
operator|!=
literal|null
condition|)
block|{
name|bufferPool
operator|.
name|returnBuffer
argument_list|(
name|curPacketBuf
argument_list|)
expr_stmt|;
name|curPacketBuf
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Take the socket used to talk to the DN.    */
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
comment|/**    * Whether the BlockReader has reached the end of its input stream    * and successfully sent a status code back to the datanode.    */
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
name|writeReadResult
argument_list|(
name|sock
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
comment|/**    * Serialize the actual read result on the wire.    */
DECL|method|writeReadResult (Socket sock, Status statusCode)
specifier|static
name|void
name|writeReadResult
parameter_list|(
name|Socket
name|sock
parameter_list|,
name|Status
name|statusCode
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|out
init|=
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
decl_stmt|;
name|ClientReadStatusProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setStatus
argument_list|(
name|statusCode
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
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
name|BlockReaderUtil
operator|.
name|readAll
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
DECL|method|readFully (byte[] buf, int off, int len)
specifier|public
name|void
name|readFully
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
name|BlockReaderUtil
operator|.
name|readFully
argument_list|(
name|this
argument_list|,
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new BlockReader specifically to satisfy a read.    * This method also sends the OP_READ_BLOCK request.    *    * @param sock  An established Socket to the DN. The BlockReader will not close it normally.    *             This socket must have an associated Channel.    * @param file  File location    * @param block  The block object    * @param blockToken  The block token for security    * @param startOffset  The read offset, relative to block head    * @param len  The number of bytes to read    * @param bufferSize  The IO buffer size (not the client buffer size)    * @param verifyChecksum  Whether to verify checksum    * @param clientName  Client name    * @return New BlockReader instance, or null on error.    */
DECL|method|newBlockReader ( Socket sock, String file, ExtendedBlock block, Token<BlockTokenIdentifier> blockToken, long startOffset, long len, int bufferSize, boolean verifyChecksum, String clientName)
specifier|public
specifier|static
name|BlockReader
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
name|SocketInputWrapper
name|sin
init|=
name|NetUtils
operator|.
name|getInputStream
argument_list|(
name|sock
argument_list|)
decl_stmt|;
name|ReadableByteChannel
name|ch
init|=
name|sin
operator|.
name|getReadableByteChannel
argument_list|()
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|sin
argument_list|)
decl_stmt|;
name|BlockOpResponseProto
name|status
init|=
name|BlockOpResponseProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
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
operator|>=
operator|(
name|startOffset
operator|+
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
name|RemoteBlockReader2
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
name|ch
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
DECL|method|checkSuccess ( BlockOpResponseProto status, Socket sock, ExtendedBlock block, String file)
specifier|static
name|void
name|checkSuccess
parameter_list|(
name|BlockOpResponseProto
name|status
parameter_list|,
name|Socket
name|sock
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|status
operator|.
name|getStatus
argument_list|()
operator|!=
name|Status
operator|.
name|SUCCESS
condition|)
block|{
if|if
condition|(
name|status
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|ERROR_ACCESS_TOKEN
condition|)
block|{
throw|throw
operator|new
name|InvalidBlockTokenException
argument_list|(
literal|"Got access token error for OP_READ_BLOCK, self="
operator|+
name|sock
operator|.
name|getLocalSocketAddress
argument_list|()
operator|+
literal|", remote="
operator|+
name|sock
operator|.
name|getRemoteSocketAddress
argument_list|()
operator|+
literal|", for file "
operator|+
name|file
operator|+
literal|", for pool "
operator|+
name|block
operator|.
name|getBlockPoolId
argument_list|()
operator|+
literal|" block "
operator|+
name|block
operator|.
name|getBlockId
argument_list|()
operator|+
literal|"_"
operator|+
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Got error for OP_READ_BLOCK, self="
operator|+
name|sock
operator|.
name|getLocalSocketAddress
argument_list|()
operator|+
literal|", remote="
operator|+
name|sock
operator|.
name|getRemoteSocketAddress
argument_list|()
operator|+
literal|", for file "
operator|+
name|file
operator|+
literal|", for pool "
operator|+
name|block
operator|.
name|getBlockPoolId
argument_list|()
operator|+
literal|" block "
operator|+
name|block
operator|.
name|getBlockId
argument_list|()
operator|+
literal|"_"
operator|+
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

