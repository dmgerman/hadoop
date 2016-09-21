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
name|ReadOption
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
name|DatanodeInfo
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
name|protocol
operator|.
name|LocatedBlocks
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
name|LocatedStripedBlock
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
name|InvalidEncryptionKeyException
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
name|DFSUtilClient
operator|.
name|CorruptedBlocks
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
name|StripeReader
operator|.
name|BlockReaderInfo
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
name|StripeReader
operator|.
name|ReaderRetryPolicy
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
name|hdfs
operator|.
name|util
operator|.
name|StripedBlockUtil
operator|.
name|AlignedStripe
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
operator|.
name|StripeRange
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
name|ByteBufferPool
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
name|ElasticByteBufferPool
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
name|CodecUtil
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
name|ErasureCodingPolicy
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
name|ErasureCoderOptions
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
name|RawErasureDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|Set
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|ThreadPoolExecutor
import|;
end_import

begin_comment
comment|/**  * DFSStripedInputStream reads from striped block groups.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DFSStripedInputStream
specifier|public
class|class
name|DFSStripedInputStream
extends|extends
name|DFSInputStream
block|{
DECL|field|BUFFER_POOL
specifier|private
specifier|static
specifier|final
name|ByteBufferPool
name|BUFFER_POOL
init|=
operator|new
name|ElasticByteBufferPool
argument_list|()
decl_stmt|;
DECL|field|blockReaders
specifier|private
specifier|final
name|BlockReaderInfo
index|[]
name|blockReaders
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
decl_stmt|;
DECL|field|dataBlkNum
specifier|private
specifier|final
name|short
name|dataBlkNum
decl_stmt|;
DECL|field|parityBlkNum
specifier|private
specifier|final
name|short
name|parityBlkNum
decl_stmt|;
DECL|field|groupSize
specifier|private
specifier|final
name|int
name|groupSize
decl_stmt|;
comment|/** the buffer for a complete stripe. */
DECL|field|curStripeBuf
specifier|private
name|ByteBuffer
name|curStripeBuf
decl_stmt|;
DECL|field|parityBuf
specifier|private
name|ByteBuffer
name|parityBuf
decl_stmt|;
DECL|field|ecPolicy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
DECL|field|decoder
specifier|private
specifier|final
name|RawErasureDecoder
name|decoder
decl_stmt|;
comment|/**    * Indicate the start/end offset of the current buffered stripe in the    * block group.    */
DECL|field|curStripeRange
specifier|private
name|StripeRange
name|curStripeRange
decl_stmt|;
comment|/**    * When warning the user of a lost block in striping mode, we remember the    * dead nodes we've logged. All other striping blocks on these nodes can be    * considered lost too, and we don't want to log a warning for each of them.    * This is to prevent the log from being too verbose. Refer to HDFS-8920.    *    * To minimize the overhead, we only store the datanodeUuid in this set    */
DECL|field|warnedNodes
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|warnedNodes
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|DFSStripedInputStream (DFSClient dfsClient, String src, boolean verifyChecksum, ErasureCodingPolicy ecPolicy, LocatedBlocks locatedBlocks)
name|DFSStripedInputStream
parameter_list|(
name|DFSClient
name|dfsClient
parameter_list|,
name|String
name|src
parameter_list|,
name|boolean
name|verifyChecksum
parameter_list|,
name|ErasureCodingPolicy
name|ecPolicy
parameter_list|,
name|LocatedBlocks
name|locatedBlocks
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
name|verifyChecksum
argument_list|,
name|locatedBlocks
argument_list|)
expr_stmt|;
assert|assert
name|ecPolicy
operator|!=
literal|null
assert|;
name|this
operator|.
name|ecPolicy
operator|=
name|ecPolicy
expr_stmt|;
name|this
operator|.
name|cellSize
operator|=
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
expr_stmt|;
name|dataBlkNum
operator|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
expr_stmt|;
name|parityBlkNum
operator|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
expr_stmt|;
name|groupSize
operator|=
name|dataBlkNum
operator|+
name|parityBlkNum
expr_stmt|;
name|blockReaders
operator|=
operator|new
name|BlockReaderInfo
index|[
name|groupSize
index|]
expr_stmt|;
name|curStripeRange
operator|=
operator|new
name|StripeRange
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|ErasureCoderOptions
name|coderOptions
init|=
operator|new
name|ErasureCoderOptions
argument_list|(
name|dataBlkNum
argument_list|,
name|parityBlkNum
argument_list|)
decl_stmt|;
name|decoder
operator|=
name|CodecUtil
operator|.
name|createRawDecoder
argument_list|(
name|dfsClient
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|ecPolicy
operator|.
name|getCodecName
argument_list|()
argument_list|,
name|coderOptions
argument_list|)
expr_stmt|;
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
literal|"Creating an striped input stream for file "
operator|+
name|src
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|useDirectBuffer ()
specifier|private
name|boolean
name|useDirectBuffer
parameter_list|()
block|{
return|return
name|decoder
operator|.
name|preferDirectBuffer
argument_list|()
return|;
block|}
DECL|method|resetCurStripeBuffer ()
name|void
name|resetCurStripeBuffer
parameter_list|()
block|{
if|if
condition|(
name|curStripeBuf
operator|==
literal|null
condition|)
block|{
name|curStripeBuf
operator|=
name|BUFFER_POOL
operator|.
name|getBuffer
argument_list|(
name|useDirectBuffer
argument_list|()
argument_list|,
name|cellSize
operator|*
name|dataBlkNum
argument_list|)
expr_stmt|;
block|}
name|curStripeBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|curStripeRange
operator|=
operator|new
name|StripeRange
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getParityBuffer ()
specifier|protected
name|ByteBuffer
name|getParityBuffer
parameter_list|()
block|{
if|if
condition|(
name|parityBuf
operator|==
literal|null
condition|)
block|{
name|parityBuf
operator|=
name|BUFFER_POOL
operator|.
name|getBuffer
argument_list|(
name|useDirectBuffer
argument_list|()
argument_list|,
name|cellSize
operator|*
name|parityBlkNum
argument_list|)
expr_stmt|;
block|}
name|parityBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|parityBuf
return|;
block|}
DECL|method|getCurStripeBuf ()
specifier|protected
name|ByteBuffer
name|getCurStripeBuf
parameter_list|()
block|{
return|return
name|curStripeBuf
return|;
block|}
DECL|method|getSrc ()
specifier|protected
name|String
name|getSrc
parameter_list|()
block|{
return|return
name|src
return|;
block|}
DECL|method|getDFSClient ()
specifier|protected
name|DFSClient
name|getDFSClient
parameter_list|()
block|{
return|return
name|dfsClient
return|;
block|}
DECL|method|getLocatedBlocks ()
specifier|protected
name|LocatedBlocks
name|getLocatedBlocks
parameter_list|()
block|{
return|return
name|locatedBlocks
return|;
block|}
DECL|method|getBufferPool ()
specifier|protected
name|ByteBufferPool
name|getBufferPool
parameter_list|()
block|{
return|return
name|BUFFER_POOL
return|;
block|}
DECL|method|getStripedReadsThreadPool ()
specifier|protected
name|ThreadPoolExecutor
name|getStripedReadsThreadPool
parameter_list|()
block|{
return|return
name|dfsClient
operator|.
name|getStripedReadsThreadPool
argument_list|()
return|;
block|}
comment|/**    * When seeking into a new block group, create blockReader for each internal    * block in the group.    */
DECL|method|blockSeekTo (long target)
specifier|private
specifier|synchronized
name|void
name|blockSeekTo
parameter_list|(
name|long
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|getFileLength
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attempted to read past end of file"
argument_list|)
throw|;
block|}
comment|// Will be getting a new BlockReader.
name|closeCurrentBlockReaders
argument_list|()
expr_stmt|;
comment|// Compute desired striped block group
name|LocatedStripedBlock
name|targetBlockGroup
init|=
name|getBlockGroupAt
argument_list|(
name|target
argument_list|)
decl_stmt|;
comment|// Update current position
name|this
operator|.
name|pos
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|blockEnd
operator|=
name|targetBlockGroup
operator|.
name|getStartOffset
argument_list|()
operator|+
name|targetBlockGroup
operator|.
name|getBlockSize
argument_list|()
operator|-
literal|1
expr_stmt|;
name|currentLocatedBlock
operator|=
name|targetBlockGroup
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|curStripeBuf
operator|!=
literal|null
condition|)
block|{
name|BUFFER_POOL
operator|.
name|putBuffer
argument_list|(
name|curStripeBuf
argument_list|)
expr_stmt|;
name|curStripeBuf
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|parityBuf
operator|!=
literal|null
condition|)
block|{
name|BUFFER_POOL
operator|.
name|putBuffer
argument_list|(
name|parityBuf
argument_list|)
expr_stmt|;
name|parityBuf
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Extend the super method with the logic of switching between cells.    * When reaching the end of a cell, proceed to the next cell and read it    * with the next blockReader.    */
annotation|@
name|Override
DECL|method|closeCurrentBlockReaders ()
specifier|protected
name|void
name|closeCurrentBlockReaders
parameter_list|()
block|{
name|resetCurStripeBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|blockReaders
operator|==
literal|null
operator|||
name|blockReaders
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groupSize
condition|;
name|i
operator|++
control|)
block|{
name|closeReader
argument_list|(
name|blockReaders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|blockReaders
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|blockEnd
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|closeReader (BlockReaderInfo readerInfo)
specifier|protected
name|void
name|closeReader
parameter_list|(
name|BlockReaderInfo
name|readerInfo
parameter_list|)
block|{
if|if
condition|(
name|readerInfo
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|readerInfo
operator|.
name|reader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|readerInfo
operator|.
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignored
parameter_list|)
block|{         }
block|}
name|readerInfo
operator|.
name|skip
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getOffsetInBlockGroup ()
specifier|private
name|long
name|getOffsetInBlockGroup
parameter_list|()
block|{
return|return
name|getOffsetInBlockGroup
argument_list|(
name|pos
argument_list|)
return|;
block|}
DECL|method|getOffsetInBlockGroup (long pos)
specifier|private
name|long
name|getOffsetInBlockGroup
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|-
name|currentLocatedBlock
operator|.
name|getStartOffset
argument_list|()
return|;
block|}
DECL|method|createBlockReader (LocatedBlock block, long offsetInBlock, LocatedBlock[] targetBlocks, BlockReaderInfo[] readerInfos, int chunkIndex)
name|boolean
name|createBlockReader
parameter_list|(
name|LocatedBlock
name|block
parameter_list|,
name|long
name|offsetInBlock
parameter_list|,
name|LocatedBlock
index|[]
name|targetBlocks
parameter_list|,
name|BlockReaderInfo
index|[]
name|readerInfos
parameter_list|,
name|int
name|chunkIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockReader
name|reader
init|=
literal|null
decl_stmt|;
specifier|final
name|ReaderRetryPolicy
name|retry
init|=
operator|new
name|ReaderRetryPolicy
argument_list|()
decl_stmt|;
name|DFSInputStream
operator|.
name|DNAddrPair
name|dnInfo
init|=
operator|new
name|DFSInputStream
operator|.
name|DNAddrPair
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
comment|// the cached block location might have been re-fetched, so always
comment|// get it from cache.
name|block
operator|=
name|refreshLocatedBlock
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|targetBlocks
index|[
name|chunkIndex
index|]
operator|=
name|block
expr_stmt|;
comment|// internal block has one location, just rule out the deadNodes
name|dnInfo
operator|=
name|getBestNodeDNAddrPair
argument_list|(
name|block
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|dnInfo
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|reader
operator|=
name|getBlockReader
argument_list|(
name|block
argument_list|,
name|offsetInBlock
argument_list|,
name|block
operator|.
name|getBlockSize
argument_list|()
operator|-
name|offsetInBlock
argument_list|,
name|dnInfo
operator|.
name|addr
argument_list|,
name|dnInfo
operator|.
name|storageType
argument_list|,
name|dnInfo
operator|.
name|info
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InvalidEncryptionKeyException
operator|&&
name|retry
operator|.
name|shouldRefetchEncryptionKey
argument_list|()
condition|)
block|{
name|DFSClient
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Will fetch a new encryption key and retry, "
operator|+
literal|"encryption key was invalid when connecting to "
operator|+
name|dnInfo
operator|.
name|addr
operator|+
literal|" : "
operator|+
name|e
argument_list|)
expr_stmt|;
name|dfsClient
operator|.
name|clearDataEncryptionKey
argument_list|()
expr_stmt|;
name|retry
operator|.
name|refetchEncryptionKey
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|retry
operator|.
name|shouldRefetchToken
argument_list|()
operator|&&
name|tokenRefetchNeeded
argument_list|(
name|e
argument_list|,
name|dnInfo
operator|.
name|addr
argument_list|)
condition|)
block|{
name|fetchBlockAt
argument_list|(
name|block
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|retry
operator|.
name|refetchToken
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//TODO: handles connection issues
name|DFSClient
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to connect to "
operator|+
name|dnInfo
operator|.
name|addr
operator|+
literal|" for "
operator|+
literal|"block"
operator|+
name|block
operator|.
name|getBlock
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// re-fetch the block in case the block has been moved
name|fetchBlockAt
argument_list|(
name|block
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|addToDeadNodes
argument_list|(
name|dnInfo
operator|.
name|info
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|readerInfos
index|[
name|chunkIndex
index|]
operator|=
operator|new
name|BlockReaderInfo
argument_list|(
name|reader
argument_list|,
name|dnInfo
operator|.
name|info
argument_list|,
name|offsetInBlock
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Read a new stripe covering the current position, and store the data in the    * {@link #curStripeBuf}.    */
DECL|method|readOneStripe (CorruptedBlocks corruptedBlocks)
specifier|private
name|void
name|readOneStripe
parameter_list|(
name|CorruptedBlocks
name|corruptedBlocks
parameter_list|)
throws|throws
name|IOException
block|{
name|resetCurStripeBuffer
argument_list|()
expr_stmt|;
comment|// compute stripe range based on pos
specifier|final
name|long
name|offsetInBlockGroup
init|=
name|getOffsetInBlockGroup
argument_list|()
decl_stmt|;
specifier|final
name|long
name|stripeLen
init|=
name|cellSize
operator|*
name|dataBlkNum
decl_stmt|;
specifier|final
name|int
name|stripeIndex
init|=
call|(
name|int
call|)
argument_list|(
name|offsetInBlockGroup
operator|/
name|stripeLen
argument_list|)
decl_stmt|;
specifier|final
name|int
name|stripeBufOffset
init|=
call|(
name|int
call|)
argument_list|(
name|offsetInBlockGroup
operator|%
name|stripeLen
argument_list|)
decl_stmt|;
specifier|final
name|int
name|stripeLimit
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|currentLocatedBlock
operator|.
name|getBlockSize
argument_list|()
operator|-
operator|(
name|stripeIndex
operator|*
name|stripeLen
operator|)
argument_list|,
name|stripeLen
argument_list|)
decl_stmt|;
name|StripeRange
name|stripeRange
init|=
operator|new
name|StripeRange
argument_list|(
name|offsetInBlockGroup
argument_list|,
name|stripeLimit
operator|-
name|stripeBufOffset
argument_list|)
decl_stmt|;
name|LocatedStripedBlock
name|blockGroup
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|currentLocatedBlock
decl_stmt|;
name|AlignedStripe
index|[]
name|stripes
init|=
name|StripedBlockUtil
operator|.
name|divideOneStripe
argument_list|(
name|ecPolicy
argument_list|,
name|cellSize
argument_list|,
name|blockGroup
argument_list|,
name|offsetInBlockGroup
argument_list|,
name|offsetInBlockGroup
operator|+
name|stripeRange
operator|.
name|getLength
argument_list|()
operator|-
literal|1
argument_list|,
name|curStripeBuf
argument_list|)
decl_stmt|;
specifier|final
name|LocatedBlock
index|[]
name|blks
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
name|blockGroup
argument_list|,
name|cellSize
argument_list|,
name|dataBlkNum
argument_list|,
name|parityBlkNum
argument_list|)
decl_stmt|;
comment|// read the whole stripe
for|for
control|(
name|AlignedStripe
name|stripe
range|:
name|stripes
control|)
block|{
comment|// Parse group to get chosen DN location
name|StripeReader
name|sreader
init|=
operator|new
name|StatefulStripeReader
argument_list|(
name|stripe
argument_list|,
name|ecPolicy
argument_list|,
name|blks
argument_list|,
name|blockReaders
argument_list|,
name|corruptedBlocks
argument_list|,
name|decoder
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|sreader
operator|.
name|readStripe
argument_list|()
expr_stmt|;
block|}
name|curStripeBuf
operator|.
name|position
argument_list|(
name|stripeBufOffset
argument_list|)
expr_stmt|;
name|curStripeBuf
operator|.
name|limit
argument_list|(
name|stripeLimit
argument_list|)
expr_stmt|;
name|curStripeRange
operator|=
name|stripeRange
expr_stmt|;
block|}
comment|/**    * Seek to a new arbitrary location.    */
annotation|@
name|Override
DECL|method|seek (long targetPos)
specifier|public
specifier|synchronized
name|void
name|seek
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|targetPos
operator|>
name|getFileLength
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Cannot seek after EOF"
argument_list|)
throw|;
block|}
if|if
condition|(
name|targetPos
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Cannot seek to negative offset"
argument_list|)
throw|;
block|}
if|if
condition|(
name|closed
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream is closed!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|targetPos
operator|<=
name|blockEnd
condition|)
block|{
specifier|final
name|long
name|targetOffsetInBlk
init|=
name|getOffsetInBlockGroup
argument_list|(
name|targetPos
argument_list|)
decl_stmt|;
if|if
condition|(
name|curStripeRange
operator|.
name|include
argument_list|(
name|targetOffsetInBlk
argument_list|)
condition|)
block|{
name|int
name|bufOffset
init|=
name|getStripedBufOffset
argument_list|(
name|targetOffsetInBlk
argument_list|)
decl_stmt|;
name|curStripeBuf
operator|.
name|position
argument_list|(
name|bufOffset
argument_list|)
expr_stmt|;
name|pos
operator|=
name|targetPos
expr_stmt|;
return|return;
block|}
block|}
name|pos
operator|=
name|targetPos
expr_stmt|;
name|blockEnd
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|getStripedBufOffset (long offsetInBlockGroup)
specifier|private
name|int
name|getStripedBufOffset
parameter_list|(
name|long
name|offsetInBlockGroup
parameter_list|)
block|{
specifier|final
name|long
name|stripeLen
init|=
name|cellSize
operator|*
name|dataBlkNum
decl_stmt|;
comment|// compute the position in the curStripeBuf based on "pos"
return|return
call|(
name|int
call|)
argument_list|(
name|offsetInBlockGroup
operator|%
name|stripeLen
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekToNewSource (long targetPos)
specifier|public
specifier|synchronized
name|boolean
name|seekToNewSource
parameter_list|(
name|long
name|targetPos
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|readWithStrategy (ReaderStrategy strategy)
specifier|protected
specifier|synchronized
name|int
name|readWithStrategy
parameter_list|(
name|ReaderStrategy
name|strategy
parameter_list|)
throws|throws
name|IOException
block|{
name|dfsClient
operator|.
name|checkOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|closed
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream closed"
argument_list|)
throw|;
block|}
name|int
name|len
init|=
name|strategy
operator|.
name|getTargetLength
argument_list|()
decl_stmt|;
name|CorruptedBlocks
name|corruptedBlocks
init|=
operator|new
name|CorruptedBlocks
argument_list|()
decl_stmt|;
if|if
condition|(
name|pos
operator|<
name|getFileLength
argument_list|()
condition|)
block|{
try|try
block|{
if|if
condition|(
name|pos
operator|>
name|blockEnd
condition|)
block|{
name|blockSeekTo
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
name|int
name|realLen
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
operator|(
name|blockEnd
operator|-
name|pos
operator|+
literal|1L
operator|)
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|infoLock
init|)
block|{
if|if
condition|(
name|locatedBlocks
operator|.
name|isLastBlockComplete
argument_list|()
condition|)
block|{
name|realLen
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|realLen
argument_list|,
name|locatedBlocks
operator|.
name|getFileLength
argument_list|()
operator|-
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Number of bytes already read into buffer */
name|int
name|result
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|result
operator|<
name|realLen
condition|)
block|{
if|if
condition|(
operator|!
name|curStripeRange
operator|.
name|include
argument_list|(
name|getOffsetInBlockGroup
argument_list|()
argument_list|)
condition|)
block|{
name|readOneStripe
argument_list|(
name|corruptedBlocks
argument_list|)
expr_stmt|;
block|}
name|int
name|ret
init|=
name|copyToTargetBuf
argument_list|(
name|strategy
argument_list|,
name|realLen
operator|-
name|result
argument_list|)
decl_stmt|;
name|result
operator|+=
name|ret
expr_stmt|;
name|pos
operator|+=
name|ret
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
finally|finally
block|{
comment|// Check if need to report block replicas corruption either read
comment|// was successful or ChecksumException occured.
name|reportCheckSumFailure
argument_list|(
name|corruptedBlocks
argument_list|,
name|currentLocatedBlock
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/**    * Copy the data from {@link #curStripeBuf} into the given buffer.    * @param strategy the ReaderStrategy containing the given buffer    * @param length target length    * @return number of bytes copied    */
DECL|method|copyToTargetBuf (ReaderStrategy strategy, int length)
specifier|private
name|int
name|copyToTargetBuf
parameter_list|(
name|ReaderStrategy
name|strategy
parameter_list|,
name|int
name|length
parameter_list|)
block|{
specifier|final
name|long
name|offsetInBlk
init|=
name|getOffsetInBlockGroup
argument_list|()
decl_stmt|;
name|int
name|bufOffset
init|=
name|getStripedBufOffset
argument_list|(
name|offsetInBlk
argument_list|)
decl_stmt|;
name|curStripeBuf
operator|.
name|position
argument_list|(
name|bufOffset
argument_list|)
expr_stmt|;
return|return
name|strategy
operator|.
name|readFromBuffer
argument_list|(
name|curStripeBuf
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|curStripeBuf
operator|.
name|remaining
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * The super method {@link DFSInputStream#refreshLocatedBlock} refreshes    * cached LocatedBlock by executing {@link DFSInputStream#getBlockAt} again.    * This method extends the logic by first remembering the index of the    * internal block, and re-parsing the refreshed block group with the same    * index.    */
annotation|@
name|Override
DECL|method|refreshLocatedBlock (LocatedBlock block)
specifier|protected
name|LocatedBlock
name|refreshLocatedBlock
parameter_list|(
name|LocatedBlock
name|block
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|idx
init|=
name|StripedBlockUtil
operator|.
name|getBlockIndex
argument_list|(
name|block
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
decl_stmt|;
name|LocatedBlock
name|lb
init|=
name|getBlockGroupAt
argument_list|(
name|block
operator|.
name|getStartOffset
argument_list|()
argument_list|)
decl_stmt|;
comment|// If indexing information is returned, iterate through the index array
comment|// to find the entry for position idx in the group
name|LocatedStripedBlock
name|lsb
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|lb
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|lsb
operator|.
name|getBlockIndices
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|lsb
operator|.
name|getBlockIndices
argument_list|()
index|[
name|i
index|]
operator|==
name|idx
condition|)
block|{
break|break;
block|}
block|}
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
literal|"refreshLocatedBlock for striped blocks, offset="
operator|+
name|block
operator|.
name|getStartOffset
argument_list|()
operator|+
literal|". Obtained block "
operator|+
name|lb
operator|+
literal|", idx="
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
name|StripedBlockUtil
operator|.
name|constructInternalBlock
argument_list|(
name|lsb
argument_list|,
name|i
argument_list|,
name|cellSize
argument_list|,
name|dataBlkNum
argument_list|,
name|idx
argument_list|)
return|;
block|}
DECL|method|getBlockGroupAt (long offset)
specifier|private
name|LocatedStripedBlock
name|getBlockGroupAt
parameter_list|(
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|LocatedBlock
name|lb
init|=
name|super
operator|.
name|getBlockAt
argument_list|(
name|offset
argument_list|)
decl_stmt|;
assert|assert
name|lb
operator|instanceof
name|LocatedStripedBlock
operator|:
literal|"NameNode"
operator|+
literal|" should return a LocatedStripedBlock for a striped file"
assert|;
return|return
operator|(
name|LocatedStripedBlock
operator|)
name|lb
return|;
block|}
comment|/**    * Real implementation of pread.    */
annotation|@
name|Override
DECL|method|fetchBlockByteRange (LocatedBlock block, long start, long end, ByteBuffer buf, CorruptedBlocks corruptedBlocks)
specifier|protected
name|void
name|fetchBlockByteRange
parameter_list|(
name|LocatedBlock
name|block
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|,
name|ByteBuffer
name|buf
parameter_list|,
name|CorruptedBlocks
name|corruptedBlocks
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Refresh the striped block group
name|LocatedStripedBlock
name|blockGroup
init|=
name|getBlockGroupAt
argument_list|(
name|block
operator|.
name|getStartOffset
argument_list|()
argument_list|)
decl_stmt|;
name|AlignedStripe
index|[]
name|stripes
init|=
name|StripedBlockUtil
operator|.
name|divideByteRangeIntoStripes
argument_list|(
name|ecPolicy
argument_list|,
name|cellSize
argument_list|,
name|blockGroup
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|buf
argument_list|)
decl_stmt|;
specifier|final
name|LocatedBlock
index|[]
name|blks
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
name|blockGroup
argument_list|,
name|cellSize
argument_list|,
name|dataBlkNum
argument_list|,
name|parityBlkNum
argument_list|)
decl_stmt|;
specifier|final
name|BlockReaderInfo
index|[]
name|preaderInfos
init|=
operator|new
name|BlockReaderInfo
index|[
name|groupSize
index|]
decl_stmt|;
try|try
block|{
for|for
control|(
name|AlignedStripe
name|stripe
range|:
name|stripes
control|)
block|{
comment|// Parse group to get chosen DN location
name|StripeReader
name|preader
init|=
operator|new
name|PositionStripeReader
argument_list|(
name|stripe
argument_list|,
name|ecPolicy
argument_list|,
name|blks
argument_list|,
name|preaderInfos
argument_list|,
name|corruptedBlocks
argument_list|,
name|decoder
argument_list|,
name|this
argument_list|)
decl_stmt|;
try|try
block|{
name|preader
operator|.
name|readStripe
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|preader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|position
argument_list|(
name|buf
operator|.
name|position
argument_list|()
operator|+
call|(
name|int
call|)
argument_list|(
name|end
operator|-
name|start
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
for|for
control|(
name|BlockReaderInfo
name|preaderInfo
range|:
name|preaderInfos
control|)
block|{
name|closeReader
argument_list|(
name|preaderInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|reportLostBlock (LocatedBlock lostBlock, Collection<DatanodeInfo> ignoredNodes)
specifier|protected
name|void
name|reportLostBlock
parameter_list|(
name|LocatedBlock
name|lostBlock
parameter_list|,
name|Collection
argument_list|<
name|DatanodeInfo
argument_list|>
name|ignoredNodes
parameter_list|)
block|{
name|DatanodeInfo
index|[]
name|nodes
init|=
name|lostBlock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
operator|&&
name|nodes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|dnUUIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|node
range|:
name|nodes
control|)
block|{
name|dnUUIDs
operator|.
name|add
argument_list|(
name|node
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|warnedNodes
operator|.
name|containsAll
argument_list|(
name|dnUUIDs
argument_list|)
condition|)
block|{
name|DFSClient
operator|.
name|LOG
operator|.
name|warn
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|nodes
argument_list|)
operator|+
literal|" are unavailable and "
operator|+
literal|"all striping blocks on them are lost. "
operator|+
literal|"IgnoredNodes = "
operator|+
name|ignoredNodes
argument_list|)
expr_stmt|;
name|warnedNodes
operator|.
name|addAll
argument_list|(
name|dnUUIDs
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|reportLostBlock
argument_list|(
name|lostBlock
argument_list|,
name|ignoredNodes
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * May need online read recovery, zero-copy read doesn't make    * sense, so don't support it.    */
annotation|@
name|Override
DECL|method|read (ByteBufferPool bufferPool, int maxLength, EnumSet<ReadOption> opts)
specifier|public
specifier|synchronized
name|ByteBuffer
name|read
parameter_list|(
name|ByteBufferPool
name|bufferPool
parameter_list|,
name|int
name|maxLength
parameter_list|,
name|EnumSet
argument_list|<
name|ReadOption
argument_list|>
name|opts
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not support enhanced byte buffer access."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|releaseBuffer (ByteBuffer buffer)
specifier|public
specifier|synchronized
name|void
name|releaseBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not support enhanced byte buffer access."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

