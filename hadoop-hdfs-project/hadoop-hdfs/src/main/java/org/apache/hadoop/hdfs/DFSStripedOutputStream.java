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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InterruptedIOException
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
name|ClosedChannelException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|concurrent
operator|.
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|HadoopIllegalArgumentException
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
name|CreateFlag
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
name|ECInfo
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
name|HdfsFileStatus
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
name|LocatedBlock
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
name|StripedBlockUtil
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RSRawEncoder
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
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RawErasureEncoder
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
name|Progressable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|Sampler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|Trace
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|TraceScope
import|;
end_import

begin_comment
comment|/****************************************************************  * The DFSStripedOutputStream class supports writing files in striped  * layout. Each stripe contains a sequence of cells and multiple  * {@link StripedDataStreamer}s in DFSStripedOutputStream are responsible  * for writing the cells to different datanodes.  *  ****************************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DFSStripedOutputStream
specifier|public
class|class
name|DFSStripedOutputStream
extends|extends
name|DFSOutputStream
block|{
DECL|field|streamers
specifier|private
specifier|final
name|List
argument_list|<
name|StripedDataStreamer
argument_list|>
name|streamers
decl_stmt|;
comment|/**    * Size of each striping cell, must be a multiple of bytesPerChecksum    */
DECL|field|ecInfo
specifier|private
specifier|final
name|ECInfo
name|ecInfo
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
decl_stmt|;
DECL|field|cellBuffers
specifier|private
name|ByteBuffer
index|[]
name|cellBuffers
decl_stmt|;
DECL|field|numAllBlocks
specifier|private
specifier|final
name|short
name|numAllBlocks
decl_stmt|;
DECL|field|numDataBlocks
specifier|private
specifier|final
name|short
name|numDataBlocks
decl_stmt|;
DECL|field|curIdx
specifier|private
name|int
name|curIdx
init|=
literal|0
decl_stmt|;
comment|/* bytes written in current block group */
comment|//private long currentBlockGroupBytes = 0;
comment|//TODO: Use ErasureCoder interface (HDFS-7781)
DECL|field|encoder
specifier|private
name|RawErasureEncoder
name|encoder
decl_stmt|;
DECL|method|getLeadingStreamer ()
specifier|private
name|StripedDataStreamer
name|getLeadingStreamer
parameter_list|()
block|{
return|return
name|streamers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getBlockGroupSize ()
specifier|private
name|long
name|getBlockGroupSize
parameter_list|()
block|{
return|return
name|blockSize
operator|*
name|numDataBlocks
return|;
block|}
comment|/** Construct a new output stream for creating a file. */
DECL|method|DFSStripedOutputStream (DFSClient dfsClient, String src, HdfsFileStatus stat, EnumSet<CreateFlag> flag, Progressable progress, DataChecksum checksum, String[] favoredNodes)
name|DFSStripedOutputStream
parameter_list|(
name|DFSClient
name|dfsClient
parameter_list|,
name|String
name|src
parameter_list|,
name|HdfsFileStatus
name|stat
parameter_list|,
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|flag
parameter_list|,
name|Progressable
name|progress
parameter_list|,
name|DataChecksum
name|checksum
parameter_list|,
name|String
index|[]
name|favoredNodes
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dfsClient
argument_list|,
name|src
argument_list|,
name|stat
argument_list|,
name|flag
argument_list|,
name|progress
argument_list|,
name|checksum
argument_list|,
name|favoredNodes
argument_list|)
expr_stmt|;
name|DFSClient
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating striped output stream"
argument_list|)
expr_stmt|;
comment|// ECInfo is restored from NN just before writing striped files.
name|ecInfo
operator|=
name|dfsClient
operator|.
name|getErasureCodingInfo
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|cellSize
operator|=
name|ecInfo
operator|.
name|getSchema
argument_list|()
operator|.
name|getChunkSize
argument_list|()
expr_stmt|;
name|numAllBlocks
operator|=
call|(
name|short
call|)
argument_list|(
name|ecInfo
operator|.
name|getSchema
argument_list|()
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|ecInfo
operator|.
name|getSchema
argument_list|()
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
expr_stmt|;
name|numDataBlocks
operator|=
operator|(
name|short
operator|)
name|ecInfo
operator|.
name|getSchema
argument_list|()
operator|.
name|getNumDataUnits
argument_list|()
expr_stmt|;
name|checkConfiguration
argument_list|()
expr_stmt|;
name|cellBuffers
operator|=
operator|new
name|ByteBuffer
index|[
name|numAllBlocks
index|]
expr_stmt|;
name|List
argument_list|<
name|BlockingQueue
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|>
name|stripeBlocks
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
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
name|stripeBlocks
operator|.
name|add
argument_list|(
operator|new
name|LinkedBlockingQueue
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|(
name|numAllBlocks
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|cellBuffers
index|[
name|i
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|byteArrayManager
operator|.
name|newByteArray
argument_list|(
name|cellSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
specifier|final
name|InterruptedIOException
name|iioe
init|=
operator|new
name|InterruptedIOException
argument_list|(
literal|"create cell buffers"
argument_list|)
decl_stmt|;
name|iioe
operator|.
name|initCause
argument_list|(
name|ie
argument_list|)
expr_stmt|;
throw|throw
name|iioe
throw|;
block|}
block|}
name|encoder
operator|=
operator|new
name|RSRawEncoder
argument_list|()
expr_stmt|;
name|encoder
operator|.
name|initialize
argument_list|(
name|numDataBlocks
argument_list|,
name|numAllBlocks
operator|-
name|numDataBlocks
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StripedDataStreamer
argument_list|>
name|s
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numAllBlocks
argument_list|)
decl_stmt|;
for|for
control|(
name|short
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
name|StripedDataStreamer
name|streamer
init|=
operator|new
name|StripedDataStreamer
argument_list|(
name|stat
argument_list|,
literal|null
argument_list|,
name|dfsClient
argument_list|,
name|src
argument_list|,
name|progress
argument_list|,
name|checksum
argument_list|,
name|cachingStrategy
argument_list|,
name|byteArrayManager
argument_list|,
name|i
argument_list|,
name|stripeBlocks
argument_list|)
decl_stmt|;
if|if
condition|(
name|favoredNodes
operator|!=
literal|null
operator|&&
name|favoredNodes
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|streamer
operator|.
name|setFavoredNodes
argument_list|(
name|favoredNodes
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|add
argument_list|(
name|streamer
argument_list|)
expr_stmt|;
block|}
name|streamers
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|refreshStreamer
argument_list|()
expr_stmt|;
block|}
DECL|method|checkConfiguration ()
specifier|private
name|void
name|checkConfiguration
parameter_list|()
block|{
if|if
condition|(
name|cellSize
operator|%
name|bytesPerChecksum
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid values: "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
operator|+
literal|" (="
operator|+
name|bytesPerChecksum
operator|+
literal|") must divide cell size (="
operator|+
name|cellSize
operator|+
literal|")."
argument_list|)
throw|;
block|}
block|}
DECL|method|refreshStreamer ()
specifier|private
name|void
name|refreshStreamer
parameter_list|()
block|{
name|streamer
operator|=
name|streamers
operator|.
name|get
argument_list|(
name|curIdx
argument_list|)
expr_stmt|;
block|}
DECL|method|moveToNextStreamer ()
specifier|private
name|void
name|moveToNextStreamer
parameter_list|()
block|{
name|curIdx
operator|=
operator|(
name|curIdx
operator|+
literal|1
operator|)
operator|%
name|numAllBlocks
expr_stmt|;
name|refreshStreamer
argument_list|()
expr_stmt|;
block|}
comment|/**    * encode the buffers.    * After encoding, flip each buffer.    *    * @param buffers data buffers + parity buffers    */
DECL|method|encode (ByteBuffer[] buffers)
specifier|private
name|void
name|encode
parameter_list|(
name|ByteBuffer
index|[]
name|buffers
parameter_list|)
block|{
name|ByteBuffer
index|[]
name|dataBuffers
init|=
operator|new
name|ByteBuffer
index|[
name|numDataBlocks
index|]
decl_stmt|;
name|ByteBuffer
index|[]
name|parityBuffers
init|=
operator|new
name|ByteBuffer
index|[
name|numAllBlocks
operator|-
name|numDataBlocks
index|]
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
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
name|numDataBlocks
condition|)
block|{
name|dataBuffers
index|[
name|i
index|]
operator|=
name|buffers
index|[
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|parityBuffers
index|[
name|i
operator|-
name|numDataBlocks
index|]
operator|=
name|buffers
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
name|encoder
operator|.
name|encode
argument_list|(
name|dataBuffers
argument_list|,
name|parityBuffers
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generate packets from a given buffer. This is only used for streamers    * writing parity blocks.    *    * @param byteBuffer the given buffer to generate packets    * @return packets generated    * @throws IOException    */
DECL|method|generatePackets (ByteBuffer byteBuffer)
specifier|private
name|List
argument_list|<
name|DFSPacket
argument_list|>
name|generatePackets
parameter_list|(
name|ByteBuffer
name|byteBuffer
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DFSPacket
argument_list|>
name|packets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|byteBuffer
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DFSPacket
name|p
init|=
name|createPacket
argument_list|(
name|packetSize
argument_list|,
name|chunksPerPacket
argument_list|,
name|streamer
operator|.
name|getBytesCurBlock
argument_list|()
argument_list|,
name|streamer
operator|.
name|getAndIncCurrentSeqno
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|maxBytesToPacket
init|=
name|p
operator|.
name|getMaxChunks
argument_list|()
operator|*
name|bytesPerChecksum
decl_stmt|;
name|int
name|toWrite
init|=
name|byteBuffer
operator|.
name|remaining
argument_list|()
operator|>
name|maxBytesToPacket
condition|?
name|maxBytesToPacket
else|:
name|byteBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|p
operator|.
name|writeData
argument_list|(
name|byteBuffer
argument_list|,
name|toWrite
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|incBytesCurBlock
argument_list|(
name|toWrite
argument_list|)
expr_stmt|;
name|packets
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|packets
return|;
block|}
annotation|@
name|Override
DECL|method|writeChunk (byte[] b, int offset, int len, byte[] checksum, int ckoff, int cklen)
specifier|protected
specifier|synchronized
name|void
name|writeChunk
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|byte
index|[]
name|checksum
parameter_list|,
name|int
name|ckoff
parameter_list|,
name|int
name|cklen
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeChunk
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
name|checksum
argument_list|,
name|ckoff
argument_list|,
name|cklen
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSizeOfCellnBuffer
argument_list|(
name|curIdx
argument_list|)
operator|<=
name|cellSize
condition|)
block|{
name|addToCellBuffer
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|msg
init|=
literal|"Writing a chunk should not overflow the cell buffer."
decl_stmt|;
name|DFSClient
operator|.
name|LOG
operator|.
name|info
argument_list|(
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
comment|// If current packet has not been enqueued for transmission,
comment|// but the cell buffer is full, we need to enqueue the packet
if|if
condition|(
name|currentPacket
operator|!=
literal|null
operator|&&
name|getSizeOfCellnBuffer
argument_list|(
name|curIdx
argument_list|)
operator|==
name|cellSize
condition|)
block|{
if|if
condition|(
name|DFSClient
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|DFSClient
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"DFSClient writeChunk cell buffer full seqno="
operator|+
name|currentPacket
operator|.
name|getSeqno
argument_list|()
operator|+
literal|", curIdx="
operator|+
name|curIdx
operator|+
literal|", src="
operator|+
name|src
operator|+
literal|", bytesCurBlock="
operator|+
name|streamer
operator|.
name|getBytesCurBlock
argument_list|()
operator|+
literal|", blockSize="
operator|+
name|blockSize
operator|+
literal|", appendChunk="
operator|+
name|streamer
operator|.
name|getAppendChunk
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|streamer
operator|.
name|waitAndQueuePacket
argument_list|(
name|currentPacket
argument_list|)
expr_stmt|;
name|currentPacket
operator|=
literal|null
expr_stmt|;
name|adjustChunkBoundary
argument_list|()
expr_stmt|;
name|endBlock
argument_list|()
expr_stmt|;
block|}
comment|// Two extra steps are needed when a striping cell is full:
comment|// 1. Forward the current index pointer
comment|// 2. Generate parity packets if a full stripe of data cells are present
if|if
condition|(
name|getSizeOfCellnBuffer
argument_list|(
name|curIdx
argument_list|)
operator|==
name|cellSize
condition|)
block|{
comment|//move curIdx to next cell
name|moveToNextStreamer
argument_list|()
expr_stmt|;
comment|//When all data cells in a stripe are ready, we need to encode
comment|//them and generate some parity cells. These cells will be
comment|//converted to packets and put to their DataStreamer's queue.
if|if
condition|(
name|curIdx
operator|==
name|numDataBlocks
condition|)
block|{
comment|//encode the data cells
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|numDataBlocks
condition|;
name|k
operator|++
control|)
block|{
name|cellBuffers
index|[
name|k
index|]
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
name|encode
argument_list|(
name|cellBuffers
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numDataBlocks
init|;
name|i
operator|<
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
name|ByteBuffer
name|parityBuffer
init|=
name|cellBuffers
index|[
name|i
index|]
decl_stmt|;
name|List
argument_list|<
name|DFSPacket
argument_list|>
name|packets
init|=
name|generatePackets
argument_list|(
name|parityBuffer
argument_list|)
decl_stmt|;
for|for
control|(
name|DFSPacket
name|p
range|:
name|packets
control|)
block|{
name|currentPacket
operator|=
name|p
expr_stmt|;
name|streamer
operator|.
name|waitAndQueuePacket
argument_list|(
name|currentPacket
argument_list|)
expr_stmt|;
name|currentPacket
operator|=
literal|null
expr_stmt|;
block|}
name|endBlock
argument_list|()
expr_stmt|;
name|moveToNextStreamer
argument_list|()
expr_stmt|;
block|}
comment|//read next stripe to cellBuffers
name|clearCellBuffers
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|addToCellBuffer (byte[] b, int off, int len)
specifier|private
name|void
name|addToCellBuffer
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|cellBuffers
index|[
name|curIdx
index|]
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|getSizeOfCellnBuffer (int cellIndex)
specifier|private
name|int
name|getSizeOfCellnBuffer
parameter_list|(
name|int
name|cellIndex
parameter_list|)
block|{
return|return
name|cellBuffers
index|[
name|cellIndex
index|]
operator|.
name|position
argument_list|()
return|;
block|}
DECL|method|clearCellBuffers ()
specifier|private
name|void
name|clearCellBuffers
parameter_list|()
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
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
name|cellBuffers
index|[
name|i
index|]
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|numDataBlocks
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|cellBuffers
index|[
name|i
index|]
operator|.
name|array
argument_list|()
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|stripeDataSize ()
specifier|private
name|int
name|stripeDataSize
parameter_list|()
block|{
return|return
name|numDataBlocks
operator|*
name|cellSize
return|;
block|}
DECL|method|getCurrentBlockGroupBytes ()
specifier|private
name|long
name|getCurrentBlockGroupBytes
parameter_list|()
block|{
name|long
name|sum
init|=
literal|0
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
name|numDataBlocks
condition|;
name|i
operator|++
control|)
block|{
name|sum
operator|+=
name|streamers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBytesCurBlock
argument_list|()
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
DECL|method|notSupported (String headMsg)
specifier|private
name|void
name|notSupported
parameter_list|(
name|String
name|headMsg
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|headMsg
operator|+
literal|" is now not supported for striping layout."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|hflush ()
specifier|public
name|void
name|hflush
parameter_list|()
throws|throws
name|IOException
block|{
name|notSupported
argument_list|(
literal|"hflush"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hsync ()
specifier|public
name|void
name|hsync
parameter_list|()
throws|throws
name|IOException
block|{
name|notSupported
argument_list|(
literal|"hsync"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|protected
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
for|for
control|(
name|StripedDataStreamer
name|streamer
range|:
name|streamers
control|)
block|{
name|streamer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|abort ()
specifier|synchronized
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|StripedDataStreamer
name|streamer
range|:
name|streamers
control|)
block|{
name|streamer
operator|.
name|setLastException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Lease timeout of "
operator|+
operator|(
name|dfsClient
operator|.
name|getConf
argument_list|()
operator|.
name|getHdfsTimeout
argument_list|()
operator|/
literal|1000
operator|)
operator|+
literal|" seconds expired."
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|closeThreads
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dfsClient
operator|.
name|endFileLease
argument_list|(
name|fileId
argument_list|)
expr_stmt|;
block|}
comment|//TODO: Handle slow writers (HDFS-7786)
comment|//Cuurently only check if the leading streamer is terminated
DECL|method|isClosed ()
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
operator|||
name|getLeadingStreamer
argument_list|()
operator|.
name|streamerClosed
argument_list|()
return|;
block|}
comment|// shutdown datastreamer and responseprocessor threads.
comment|// interrupt datastreamer if force is true
annotation|@
name|Override
DECL|method|closeThreads (boolean force)
specifier|protected
name|void
name|closeThreads
parameter_list|(
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|StripedDataStreamer
name|streamer
range|:
name|streamers
control|)
block|{
try|try
block|{
name|streamer
operator|.
name|close
argument_list|(
name|force
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|join
argument_list|()
expr_stmt|;
name|streamer
operator|.
name|closeSocket
argument_list|()
expr_stmt|;
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
literal|"Failed to shutdown streamer"
argument_list|)
throw|;
block|}
finally|finally
block|{
name|streamer
operator|.
name|setSocketToNull
argument_list|()
expr_stmt|;
name|setClosed
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeParityCellsForLastStripe ()
specifier|private
name|void
name|writeParityCellsForLastStripe
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|long
name|currentBlockGroupBytes
init|=
name|getCurrentBlockGroupBytes
argument_list|()
decl_stmt|;
name|long
name|parityBlkSize
init|=
name|StripedBlockUtil
operator|.
name|getInternalBlockLength
argument_list|(
name|currentBlockGroupBytes
argument_list|,
name|cellSize
argument_list|,
name|numDataBlocks
argument_list|,
name|numDataBlocks
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|parityBlkSize
operator|==
literal|0
operator|||
name|currentBlockGroupBytes
operator|%
name|stripeDataSize
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|int
name|parityCellSize
init|=
name|parityBlkSize
operator|%
name|cellSize
operator|==
literal|0
condition|?
name|cellSize
else|:
call|(
name|int
call|)
argument_list|(
name|parityBlkSize
operator|%
name|cellSize
argument_list|)
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
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
name|long
name|internalBlkLen
init|=
name|StripedBlockUtil
operator|.
name|getInternalBlockLength
argument_list|(
name|currentBlockGroupBytes
argument_list|,
name|cellSize
argument_list|,
name|numDataBlocks
argument_list|,
name|i
argument_list|)
decl_stmt|;
comment|// Pad zero bytes to make all cells exactly the size of parityCellSize
comment|// If internal block is smaller than parity block, pad zero bytes.
comment|// Also pad zero bytes to all parity cells
if|if
condition|(
name|internalBlkLen
operator|<
name|parityBlkSize
operator|||
name|i
operator|>=
name|numDataBlocks
condition|)
block|{
name|int
name|position
init|=
name|cellBuffers
index|[
name|i
index|]
operator|.
name|position
argument_list|()
decl_stmt|;
assert|assert
name|position
operator|<=
name|parityCellSize
operator|:
literal|"If an internal block is smaller"
operator|+
literal|" than parity block, then its last cell should be small than last"
operator|+
literal|" parity cell"
assert|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|parityCellSize
operator|-
name|position
condition|;
name|j
operator|++
control|)
block|{
name|cellBuffers
index|[
name|i
index|]
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|cellBuffers
index|[
name|i
index|]
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
name|encode
argument_list|(
name|cellBuffers
argument_list|)
expr_stmt|;
comment|//write parity cells
name|curIdx
operator|=
name|numDataBlocks
expr_stmt|;
name|refreshStreamer
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numDataBlocks
init|;
name|i
operator|<
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
name|ByteBuffer
name|parityBuffer
init|=
name|cellBuffers
index|[
name|i
index|]
decl_stmt|;
name|List
argument_list|<
name|DFSPacket
argument_list|>
name|packets
init|=
name|generatePackets
argument_list|(
name|parityBuffer
argument_list|)
decl_stmt|;
for|for
control|(
name|DFSPacket
name|p
range|:
name|packets
control|)
block|{
name|currentPacket
operator|=
name|p
expr_stmt|;
name|streamer
operator|.
name|waitAndQueuePacket
argument_list|(
name|currentPacket
argument_list|)
expr_stmt|;
name|currentPacket
operator|=
literal|null
expr_stmt|;
block|}
name|endBlock
argument_list|()
expr_stmt|;
name|moveToNextStreamer
argument_list|()
expr_stmt|;
block|}
name|clearCellBuffers
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setClosed ()
name|void
name|setClosed
parameter_list|()
block|{
name|super
operator|.
name|setClosed
argument_list|()
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
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
name|byteArrayManager
operator|.
name|release
argument_list|(
name|cellBuffers
index|[
name|i
index|]
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
name|streamers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|closeImpl ()
specifier|protected
specifier|synchronized
name|void
name|closeImpl
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
block|{
name|IOException
name|e
init|=
name|getLeadingStreamer
argument_list|()
operator|.
name|getLastException
argument_list|()
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
throw|throw
name|e
throw|;
block|}
else|else
block|{
return|return;
block|}
block|}
try|try
block|{
comment|// flush from all upper layers
name|flushBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|currentPacket
operator|!=
literal|null
condition|)
block|{
name|streamer
operator|.
name|waitAndQueuePacket
argument_list|(
name|currentPacket
argument_list|)
expr_stmt|;
name|currentPacket
operator|=
literal|null
expr_stmt|;
block|}
comment|// if the last stripe is incomplete, generate and write parity cells
name|writeParityCellsForLastStripe
argument_list|()
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
name|numAllBlocks
condition|;
name|i
operator|++
control|)
block|{
name|curIdx
operator|=
name|i
expr_stmt|;
name|refreshStreamer
argument_list|()
expr_stmt|;
if|if
condition|(
name|streamer
operator|.
name|getBytesCurBlock
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// send an empty packet to mark the end of the block
name|currentPacket
operator|=
name|createPacket
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|streamer
operator|.
name|getBytesCurBlock
argument_list|()
argument_list|,
name|streamer
operator|.
name|getAndIncCurrentSeqno
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|currentPacket
operator|.
name|setSyncBlock
argument_list|(
name|shouldSyncBlock
argument_list|)
expr_stmt|;
block|}
comment|// flush all data to Datanode
name|flushInternal
argument_list|()
expr_stmt|;
block|}
name|closeThreads
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|ExtendedBlock
name|lastBlock
init|=
name|getCommittedBlock
argument_list|()
decl_stmt|;
name|TraceScope
name|scope
init|=
name|Trace
operator|.
name|startSpan
argument_list|(
literal|"completeFile"
argument_list|,
name|Sampler
operator|.
name|NEVER
argument_list|)
decl_stmt|;
try|try
block|{
name|completeFile
argument_list|(
name|lastBlock
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|scope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dfsClient
operator|.
name|endFileLease
argument_list|(
name|fileId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|ignored
parameter_list|)
block|{     }
finally|finally
block|{
name|setClosed
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Generate the block which is reported and will be committed in NameNode.    * Need to go through all the streamers writing data blocks and add their    * bytesCurBlock together. Note that at this time all streamers have been    * closed. Also this calculation can cover streamers with writing failures.    *    * @return An ExtendedBlock with size of the whole block group.    */
DECL|method|getCommittedBlock ()
name|ExtendedBlock
name|getCommittedBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|ExtendedBlock
name|b
init|=
name|getLeadingStreamer
argument_list|()
operator|.
name|getBlock
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|ExtendedBlock
name|block
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|b
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|atBlockGroupBoundary
init|=
name|getLeadingStreamer
argument_list|()
operator|.
name|getBytesCurBlock
argument_list|()
operator|==
literal|0
operator|&&
name|getLeadingStreamer
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|!=
literal|null
operator|&&
name|getLeadingStreamer
argument_list|()
operator|.
name|getBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
operator|>
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numDataBlocks
condition|;
name|i
operator|++
control|)
block|{
name|block
operator|.
name|setNumBytes
argument_list|(
name|block
operator|.
name|getNumBytes
argument_list|()
operator|+
operator|(
name|atBlockGroupBoundary
condition|?
name|streamers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBlock
argument_list|()
operator|.
name|getNumBytes
argument_list|()
else|:
name|streamers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBytesCurBlock
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|block
return|;
block|}
block|}
end_class

end_unit

