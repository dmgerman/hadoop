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
name|io
operator|.
name|InputStream
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
name|SocketException
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
name|FileChannel
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
name|HdfsConstants
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
name|SocketOutputStream
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
comment|/**  * Reads a block from the disk and sends it to a recipient.  */
end_comment

begin_class
DECL|class|RaidBlockSender
specifier|public
class|class
name|RaidBlockSender
implements|implements
name|java
operator|.
name|io
operator|.
name|Closeable
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
DECL|field|ClientTraceLog
specifier|static
specifier|final
name|Log
name|ClientTraceLog
init|=
name|DataNode
operator|.
name|ClientTraceLog
decl_stmt|;
DECL|field|block
specifier|private
name|ExtendedBlock
name|block
decl_stmt|;
comment|// the block to read from
comment|/** The visible length of a replica. */
DECL|field|replicaVisibleLength
specifier|private
specifier|final
name|long
name|replicaVisibleLength
decl_stmt|;
DECL|field|blockIn
specifier|private
name|InputStream
name|blockIn
decl_stmt|;
comment|// data stream
DECL|field|blockInPosition
specifier|private
name|long
name|blockInPosition
init|=
operator|-
literal|1
decl_stmt|;
comment|// updated while using transferTo().
DECL|field|checksumIn
specifier|private
name|DataInputStream
name|checksumIn
decl_stmt|;
comment|// checksum datastream
DECL|field|checksum
specifier|private
name|DataChecksum
name|checksum
decl_stmt|;
comment|// checksum stream
DECL|field|offset
specifier|private
name|long
name|offset
decl_stmt|;
comment|// starting position to read
DECL|field|endOffset
specifier|private
name|long
name|endOffset
decl_stmt|;
comment|// ending position
DECL|field|bytesPerChecksum
specifier|private
name|int
name|bytesPerChecksum
decl_stmt|;
comment|// chunk size
DECL|field|checksumSize
specifier|private
name|int
name|checksumSize
decl_stmt|;
comment|// checksum size
DECL|field|corruptChecksumOk
specifier|private
name|boolean
name|corruptChecksumOk
decl_stmt|;
comment|// if need to verify checksum
DECL|field|chunkOffsetOK
specifier|private
name|boolean
name|chunkOffsetOK
decl_stmt|;
comment|// if need to send chunk offset
DECL|field|seqno
specifier|private
name|long
name|seqno
decl_stmt|;
comment|// sequence number of packet
DECL|field|transferToAllowed
specifier|private
name|boolean
name|transferToAllowed
init|=
literal|true
decl_stmt|;
DECL|field|blockReadFully
specifier|private
name|boolean
name|blockReadFully
decl_stmt|;
comment|//set when the whole block is read
DECL|field|verifyChecksum
specifier|private
name|boolean
name|verifyChecksum
decl_stmt|;
comment|//if true, check is verified while reading
DECL|field|clientTraceFmt
specifier|private
specifier|final
name|String
name|clientTraceFmt
decl_stmt|;
comment|// format of client trace log message
comment|/**    * Minimum buffer used while sending data to clients. Used only if    * transferTo() is enabled. 64KB is not that large. It could be larger, but    * not sure if there will be much more improvement.    */
DECL|field|MIN_BUFFER_WITH_TRANSFERTO
specifier|private
specifier|static
specifier|final
name|int
name|MIN_BUFFER_WITH_TRANSFERTO
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|lastChunkChecksum
specifier|private
specifier|volatile
name|ChunkChecksum
name|lastChunkChecksum
init|=
literal|null
decl_stmt|;
DECL|method|RaidBlockSender (ExtendedBlock block, long blockLength, long startOffset, long length, boolean corruptChecksumOk, boolean chunkOffsetOK, boolean verifyChecksum, boolean transferToAllowed, DataInputStream metadataIn, InputStreamFactory streamFactory )
specifier|public
name|RaidBlockSender
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|long
name|blockLength
parameter_list|,
name|long
name|startOffset
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|corruptChecksumOk
parameter_list|,
name|boolean
name|chunkOffsetOK
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|,
name|boolean
name|transferToAllowed
parameter_list|,
name|DataInputStream
name|metadataIn
parameter_list|,
name|InputStreamFactory
name|streamFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|block
argument_list|,
name|blockLength
argument_list|,
name|startOffset
argument_list|,
name|length
argument_list|,
name|corruptChecksumOk
argument_list|,
name|chunkOffsetOK
argument_list|,
name|verifyChecksum
argument_list|,
name|transferToAllowed
argument_list|,
name|metadataIn
argument_list|,
name|streamFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|RaidBlockSender (ExtendedBlock block, long blockLength, long startOffset, long length, boolean corruptChecksumOk, boolean chunkOffsetOK, boolean verifyChecksum, boolean transferToAllowed, DataInputStream metadataIn, InputStreamFactory streamFactory, String clientTraceFmt)
specifier|public
name|RaidBlockSender
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|long
name|blockLength
parameter_list|,
name|long
name|startOffset
parameter_list|,
name|long
name|length
parameter_list|,
name|boolean
name|corruptChecksumOk
parameter_list|,
name|boolean
name|chunkOffsetOK
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|,
name|boolean
name|transferToAllowed
parameter_list|,
name|DataInputStream
name|metadataIn
parameter_list|,
name|InputStreamFactory
name|streamFactory
parameter_list|,
name|String
name|clientTraceFmt
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|chunkOffsetOK
operator|=
name|chunkOffsetOK
expr_stmt|;
name|this
operator|.
name|corruptChecksumOk
operator|=
name|corruptChecksumOk
expr_stmt|;
name|this
operator|.
name|verifyChecksum
operator|=
name|verifyChecksum
expr_stmt|;
name|this
operator|.
name|replicaVisibleLength
operator|=
name|blockLength
expr_stmt|;
name|this
operator|.
name|transferToAllowed
operator|=
name|transferToAllowed
expr_stmt|;
name|this
operator|.
name|clientTraceFmt
operator|=
name|clientTraceFmt
expr_stmt|;
if|if
condition|(
operator|!
name|corruptChecksumOk
operator|||
name|metadataIn
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|checksumIn
operator|=
name|metadataIn
expr_stmt|;
comment|// read and handle the common header here. For now just a version
name|BlockMetadataHeader
name|header
init|=
name|BlockMetadataHeader
operator|.
name|readHeader
argument_list|(
name|checksumIn
argument_list|)
decl_stmt|;
name|short
name|version
init|=
name|header
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|BlockMetadataHeader
operator|.
name|VERSION
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Wrong version ("
operator|+
name|version
operator|+
literal|") for metadata file for "
operator|+
name|block
operator|+
literal|" ignoring ..."
argument_list|)
expr_stmt|;
block|}
name|checksum
operator|=
name|header
operator|.
name|getChecksum
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not find metadata file for "
operator|+
name|block
argument_list|)
expr_stmt|;
comment|// This only decides the buffer size. Use BUFFER_SIZE?
name|checksum
operator|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|DataChecksum
operator|.
name|CHECKSUM_NULL
argument_list|,
literal|16
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
comment|/* If bytesPerChecksum is very large, then the metadata file        * is mostly corrupted. For now just truncate bytesPerchecksum to        * blockLength.        */
name|bytesPerChecksum
operator|=
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
expr_stmt|;
if|if
condition|(
name|bytesPerChecksum
operator|>
literal|10
operator|*
literal|1024
operator|*
literal|1024
operator|&&
name|bytesPerChecksum
operator|>
name|replicaVisibleLength
condition|)
block|{
name|checksum
operator|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|checksum
operator|.
name|getChecksumType
argument_list|()
argument_list|,
name|Math
operator|.
name|max
argument_list|(
operator|(
name|int
operator|)
name|replicaVisibleLength
argument_list|,
literal|10
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|bytesPerChecksum
operator|=
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
expr_stmt|;
block|}
name|checksumSize
operator|=
name|checksum
operator|.
name|getChecksumSize
argument_list|()
expr_stmt|;
if|if
condition|(
name|length
operator|<
literal|0
condition|)
block|{
name|length
operator|=
name|replicaVisibleLength
expr_stmt|;
block|}
name|endOffset
operator|=
name|blockLength
expr_stmt|;
if|if
condition|(
name|startOffset
argument_list|<
literal|0
operator|||
name|startOffset
argument_list|>
name|endOffset
operator|||
operator|(
name|length
operator|+
name|startOffset
operator|)
operator|>
name|endOffset
condition|)
block|{
name|String
name|msg
init|=
literal|" Offset "
operator|+
name|startOffset
operator|+
literal|" and length "
operator|+
name|length
operator|+
literal|" don't match block "
operator|+
name|block
operator|+
literal|" ( blockLen "
operator|+
name|endOffset
operator|+
literal|" )"
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"sendBlock() : "
operator|+
name|msg
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
name|offset
operator|=
operator|(
name|startOffset
operator|-
operator|(
name|startOffset
operator|%
name|bytesPerChecksum
operator|)
operator|)
expr_stmt|;
if|if
condition|(
name|length
operator|>=
literal|0
condition|)
block|{
comment|// Make sure endOffset points to end of a checksumed chunk.
name|long
name|tmpLen
init|=
name|startOffset
operator|+
name|length
decl_stmt|;
if|if
condition|(
name|tmpLen
operator|%
name|bytesPerChecksum
operator|!=
literal|0
condition|)
block|{
name|tmpLen
operator|+=
operator|(
name|bytesPerChecksum
operator|-
name|tmpLen
operator|%
name|bytesPerChecksum
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|tmpLen
operator|<
name|endOffset
condition|)
block|{
comment|// will use on-disk checksum here since the end is a stable chunk
name|endOffset
operator|=
name|tmpLen
expr_stmt|;
block|}
block|}
comment|// seek to the right offsets
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
name|long
name|checksumSkip
init|=
operator|(
name|offset
operator|/
name|bytesPerChecksum
operator|)
operator|*
name|checksumSize
decl_stmt|;
comment|// note blockInStream is seeked when created below
if|if
condition|(
name|checksumSkip
operator|>
literal|0
condition|)
block|{
comment|// Should we use seek() for checksum file as well?
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|checksumIn
argument_list|,
name|checksumSkip
argument_list|)
expr_stmt|;
block|}
block|}
name|seqno
operator|=
literal|0
expr_stmt|;
name|blockIn
operator|=
name|streamFactory
operator|.
name|createStream
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|blockIn
argument_list|)
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
comment|/**    * close opened files.    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
comment|// close checksum file
if|if
condition|(
name|checksumIn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|checksumIn
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
name|ioe
operator|=
name|e
expr_stmt|;
block|}
name|checksumIn
operator|=
literal|null
expr_stmt|;
block|}
comment|// close data file
if|if
condition|(
name|blockIn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|blockIn
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
name|ioe
operator|=
name|e
expr_stmt|;
block|}
name|blockIn
operator|=
literal|null
expr_stmt|;
block|}
comment|// throw IOException if there is any
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
block|}
comment|/**    * Converts an IOExcpetion (not subclasses) to SocketException.    * This is typically done to indicate to upper layers that the error     * was a socket error rather than often more serious exceptions like     * disk errors.    */
DECL|method|ioeToSocketException (IOException ioe)
specifier|private
specifier|static
name|IOException
name|ioeToSocketException
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
if|if
condition|(
name|ioe
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// "se" could be a new class in stead of SocketException.
name|IOException
name|se
init|=
operator|new
name|SocketException
argument_list|(
literal|"Original Exception : "
operator|+
name|ioe
argument_list|)
decl_stmt|;
name|se
operator|.
name|initCause
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
comment|/* Change the stacktrace so that original trace is not truncated        * when printed.*/
name|se
operator|.
name|setStackTrace
argument_list|(
name|ioe
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|se
return|;
block|}
comment|// otherwise just return the same exception.
return|return
name|ioe
return|;
block|}
comment|/**    * Sends upto maxChunks chunks of data.    *     * When blockInPosition is>= 0, assumes 'out' is a     * {@link SocketOutputStream} and tries     * {@link SocketOutputStream#transferToFully(FileChannel, long, int)} to    * send data (and updates blockInPosition).    */
DECL|method|sendChunks (ByteBuffer pkt, int maxChunks, OutputStream out)
specifier|private
name|int
name|sendChunks
parameter_list|(
name|ByteBuffer
name|pkt
parameter_list|,
name|int
name|maxChunks
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Sends multiple chunks in one packet with a single write().
name|int
name|len
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|endOffset
operator|-
name|offset
argument_list|,
operator|(
operator|(
operator|(
name|long
operator|)
name|bytesPerChecksum
operator|)
operator|*
operator|(
operator|(
name|long
operator|)
name|maxChunks
operator|)
operator|)
argument_list|)
decl_stmt|;
name|int
name|numChunks
init|=
operator|(
name|len
operator|+
name|bytesPerChecksum
operator|-
literal|1
operator|)
operator|/
name|bytesPerChecksum
decl_stmt|;
name|int
name|packetLen
init|=
name|len
operator|+
name|numChunks
operator|*
name|checksumSize
operator|+
literal|4
decl_stmt|;
name|boolean
name|lastDataPacket
init|=
name|offset
operator|+
name|len
operator|==
name|endOffset
operator|&&
name|len
operator|>
literal|0
decl_stmt|;
name|pkt
operator|.
name|clear
argument_list|()
expr_stmt|;
name|PacketHeader
name|header
init|=
operator|new
name|PacketHeader
argument_list|(
name|packetLen
argument_list|,
name|offset
argument_list|,
name|seqno
argument_list|,
operator|(
name|len
operator|==
literal|0
operator|)
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|header
operator|.
name|putInBuffer
argument_list|(
name|pkt
argument_list|)
expr_stmt|;
name|int
name|checksumOff
init|=
name|pkt
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|checksumLen
init|=
name|numChunks
operator|*
name|checksumSize
decl_stmt|;
name|byte
index|[]
name|buf
init|=
name|pkt
operator|.
name|array
argument_list|()
decl_stmt|;
if|if
condition|(
name|checksumSize
operator|>
literal|0
operator|&&
name|checksumIn
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|checksumIn
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
name|checksumOff
argument_list|,
name|checksumLen
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|" Could not read or failed to veirfy checksum for data"
operator|+
literal|" at offset "
operator|+
name|offset
operator|+
literal|" for block "
operator|+
name|block
operator|+
literal|" got : "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|checksumIn
argument_list|)
expr_stmt|;
name|checksumIn
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|corruptChecksumOk
condition|)
block|{
if|if
condition|(
name|checksumOff
operator|<
name|checksumLen
condition|)
block|{
comment|// Just fill the array with zeros.
name|Arrays
operator|.
name|fill
argument_list|(
name|buf
argument_list|,
name|checksumOff
argument_list|,
name|checksumLen
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
comment|// write in progress that we need to use to get last checksum
if|if
condition|(
name|lastDataPacket
operator|&&
name|lastChunkChecksum
operator|!=
literal|null
condition|)
block|{
name|int
name|start
init|=
name|checksumOff
operator|+
name|checksumLen
operator|-
name|checksumSize
decl_stmt|;
name|byte
index|[]
name|updatedChecksum
init|=
name|lastChunkChecksum
operator|.
name|getChecksum
argument_list|()
decl_stmt|;
if|if
condition|(
name|updatedChecksum
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|updatedChecksum
argument_list|,
literal|0
argument_list|,
name|buf
argument_list|,
name|start
argument_list|,
name|checksumSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|int
name|dataOff
init|=
name|checksumOff
operator|+
name|checksumLen
decl_stmt|;
if|if
condition|(
name|blockInPosition
operator|<
literal|0
condition|)
block|{
comment|//normal transfer
name|IOUtils
operator|.
name|readFully
argument_list|(
name|blockIn
argument_list|,
name|buf
argument_list|,
name|dataOff
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|verifyChecksum
condition|)
block|{
name|int
name|dOff
init|=
name|dataOff
decl_stmt|;
name|int
name|cOff
init|=
name|checksumOff
decl_stmt|;
name|int
name|dLeft
init|=
name|len
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
name|numChunks
condition|;
name|i
operator|++
control|)
block|{
name|checksum
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|dLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|dLeft
argument_list|,
name|bytesPerChecksum
argument_list|)
decl_stmt|;
name|checksum
operator|.
name|update
argument_list|(
name|buf
argument_list|,
name|dOff
argument_list|,
name|dLen
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|checksum
operator|.
name|compare
argument_list|(
name|buf
argument_list|,
name|cOff
argument_list|)
condition|)
block|{
name|long
name|failedPos
init|=
name|offset
operator|+
name|len
operator|-
name|dLeft
decl_stmt|;
throw|throw
operator|new
name|ChecksumException
argument_list|(
literal|"Checksum failed at "
operator|+
name|failedPos
argument_list|,
name|failedPos
argument_list|)
throw|;
block|}
name|dLeft
operator|-=
name|dLen
expr_stmt|;
name|dOff
operator|+=
name|dLen
expr_stmt|;
name|cOff
operator|+=
name|checksumSize
expr_stmt|;
block|}
block|}
comment|//writing is done below (mainly to handle IOException)
block|}
try|try
block|{
if|if
condition|(
name|blockInPosition
operator|>=
literal|0
condition|)
block|{
comment|//use transferTo(). Checks on out and blockIn are already done.
name|SocketOutputStream
name|sockOut
init|=
operator|(
name|SocketOutputStream
operator|)
name|out
decl_stmt|;
comment|//first write the packet
name|sockOut
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|dataOff
argument_list|)
expr_stmt|;
comment|// no need to flush. since we know out is not a buffered stream.
name|sockOut
operator|.
name|transferToFully
argument_list|(
operator|(
operator|(
name|FileInputStream
operator|)
name|blockIn
operator|)
operator|.
name|getChannel
argument_list|()
argument_list|,
name|blockInPosition
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|blockInPosition
operator|+=
name|len
expr_stmt|;
block|}
else|else
block|{
comment|// normal transfer
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|dataOff
operator|+
name|len
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|/* exception while writing to the client (well, with transferTo(),        * it could also be while reading from the local file).        */
throw|throw
name|ioeToSocketException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|len
return|;
block|}
comment|/**    * sendBlock() is used to read block and its metadata and stream the data to    * either a client or to another datanode.     *     * @param out  stream to which the block is written to    * @param baseStream optional. if non-null,<code>out</code> is assumed to     *        be a wrapper over this stream. This enables optimizations for    *        sending the data, e.g.     *        {@link SocketOutputStream#transferToFully(FileChannel,     *        long, int)}.    * @return total bytes reads, including crc.    */
DECL|method|sendBlock (DataOutputStream out, OutputStream baseStream)
specifier|public
name|long
name|sendBlock
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|OutputStream
name|baseStream
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|out
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"out stream is null"
argument_list|)
throw|;
block|}
name|long
name|initialOffset
init|=
name|offset
decl_stmt|;
name|long
name|totalRead
init|=
literal|0
decl_stmt|;
name|OutputStream
name|streamForSendChunks
init|=
name|out
decl_stmt|;
specifier|final
name|long
name|startTime
init|=
name|ClientTraceLog
operator|.
name|isInfoEnabled
argument_list|()
condition|?
name|System
operator|.
name|nanoTime
argument_list|()
else|:
literal|0
decl_stmt|;
try|try
block|{
try|try
block|{
name|checksum
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|chunkOffsetOK
condition|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//socket error
throw|throw
name|ioeToSocketException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|int
name|maxChunksPerPacket
decl_stmt|;
name|int
name|pktSize
init|=
name|PacketHeader
operator|.
name|PKT_HEADER_LEN
decl_stmt|;
if|if
condition|(
name|transferToAllowed
operator|&&
operator|!
name|verifyChecksum
operator|&&
name|baseStream
operator|instanceof
name|SocketOutputStream
operator|&&
name|blockIn
operator|instanceof
name|FileInputStream
condition|)
block|{
name|FileChannel
name|fileChannel
init|=
operator|(
operator|(
name|FileInputStream
operator|)
name|blockIn
operator|)
operator|.
name|getChannel
argument_list|()
decl_stmt|;
comment|// blockInPosition also indicates sendChunks() uses transferTo.
name|blockInPosition
operator|=
name|fileChannel
operator|.
name|position
argument_list|()
expr_stmt|;
name|streamForSendChunks
operator|=
name|baseStream
expr_stmt|;
comment|// assure a mininum buffer size.
name|maxChunksPerPacket
operator|=
operator|(
name|Math
operator|.
name|max
argument_list|(
name|HdfsConstants
operator|.
name|IO_FILE_BUFFER_SIZE
argument_list|,
name|MIN_BUFFER_WITH_TRANSFERTO
argument_list|)
operator|+
name|bytesPerChecksum
operator|-
literal|1
operator|)
operator|/
name|bytesPerChecksum
expr_stmt|;
comment|// allocate smaller buffer while using transferTo().
name|pktSize
operator|+=
name|checksumSize
operator|*
name|maxChunksPerPacket
expr_stmt|;
block|}
else|else
block|{
name|maxChunksPerPacket
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
operator|(
name|HdfsConstants
operator|.
name|IO_FILE_BUFFER_SIZE
operator|+
name|bytesPerChecksum
operator|-
literal|1
operator|)
operator|/
name|bytesPerChecksum
argument_list|)
expr_stmt|;
name|pktSize
operator|+=
operator|(
name|bytesPerChecksum
operator|+
name|checksumSize
operator|)
operator|*
name|maxChunksPerPacket
expr_stmt|;
block|}
name|ByteBuffer
name|pktBuf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|pktSize
argument_list|)
decl_stmt|;
while|while
condition|(
name|endOffset
operator|>
name|offset
condition|)
block|{
name|long
name|len
init|=
name|sendChunks
argument_list|(
name|pktBuf
argument_list|,
name|maxChunksPerPacket
argument_list|,
name|streamForSendChunks
argument_list|)
decl_stmt|;
name|offset
operator|+=
name|len
expr_stmt|;
name|totalRead
operator|+=
name|len
operator|+
operator|(
operator|(
name|len
operator|+
name|bytesPerChecksum
operator|-
literal|1
operator|)
operator|/
name|bytesPerChecksum
operator|*
name|checksumSize
operator|)
expr_stmt|;
name|seqno
operator|++
expr_stmt|;
block|}
try|try
block|{
comment|// send an empty packet to mark the end of the block
name|sendChunks
argument_list|(
name|pktBuf
argument_list|,
name|maxChunksPerPacket
argument_list|,
name|streamForSendChunks
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//socket error
throw|throw
name|ioeToSocketException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|clientTraceFmt
operator|!=
literal|null
condition|)
block|{
specifier|final
name|long
name|endTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|ClientTraceLog
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|clientTraceFmt
argument_list|,
name|totalRead
argument_list|,
name|initialOffset
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|close
argument_list|()
expr_stmt|;
block|}
name|blockReadFully
operator|=
name|initialOffset
operator|==
literal|0
operator|&&
name|offset
operator|>=
name|replicaVisibleLength
expr_stmt|;
return|return
name|totalRead
return|;
block|}
DECL|method|isBlockReadFully ()
name|boolean
name|isBlockReadFully
parameter_list|()
block|{
return|return
name|blockReadFully
return|;
block|}
DECL|interface|InputStreamFactory
specifier|public
specifier|static
interface|interface
name|InputStreamFactory
block|{
DECL|method|createStream (long offset)
specifier|public
name|InputStream
name|createStream
parameter_list|(
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|BlockInputStreamFactory
specifier|private
specifier|static
class|class
name|BlockInputStreamFactory
implements|implements
name|InputStreamFactory
block|{
DECL|field|block
specifier|private
specifier|final
name|ExtendedBlock
name|block
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|FSDatasetInterface
name|data
decl_stmt|;
DECL|method|BlockInputStreamFactory (ExtendedBlock block, FSDatasetInterface data)
specifier|private
name|BlockInputStreamFactory
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|FSDatasetInterface
name|data
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createStream (long offset)
specifier|public
name|InputStream
name|createStream
parameter_list|(
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|data
operator|.
name|getBlockInputStream
argument_list|(
name|block
argument_list|,
name|offset
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

