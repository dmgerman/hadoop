begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.erasurecode
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
name|erasurecode
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
name|conf
operator|.
name|Configuration
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
name|DFSConfigKeys
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
name|server
operator|.
name|datanode
operator|.
name|CachingStrategy
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
name|DataNode
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
name|BlockReadStats
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
name|StripingChunkReadResult
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
name|slf4j
operator|.
name|Logger
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
name|BitSet
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
name|HashMap
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
name|Map
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
name|Callable
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
name|CompletionService
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
name|Future
import|;
end_import

begin_comment
comment|/**  * Manage striped readers that performs reading of block data from remote to  * serve input data for the erasure decoding.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StripedReader
class|class
name|StripedReader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
DECL|field|stripedReadTimeoutInMills
specifier|private
specifier|final
name|int
name|stripedReadTimeoutInMills
decl_stmt|;
DECL|field|stripedReadBufferSize
specifier|private
specifier|final
name|int
name|stripedReadBufferSize
decl_stmt|;
DECL|field|reconstructor
specifier|private
name|StripedReconstructor
name|reconstructor
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|dataBlkNum
specifier|private
specifier|final
name|int
name|dataBlkNum
decl_stmt|;
DECL|field|parityBlkNum
specifier|private
specifier|final
name|int
name|parityBlkNum
decl_stmt|;
DECL|field|checksum
specifier|private
name|DataChecksum
name|checksum
decl_stmt|;
comment|// Striped read buffer size
DECL|field|bufferSize
specifier|private
name|int
name|bufferSize
decl_stmt|;
DECL|field|successList
specifier|private
name|int
index|[]
name|successList
decl_stmt|;
DECL|field|minRequiredSources
specifier|private
specifier|final
name|int
name|minRequiredSources
decl_stmt|;
comment|// the number of xmits used by the re-construction task.
DECL|field|xmits
specifier|private
specifier|final
name|int
name|xmits
decl_stmt|;
comment|// The buffers and indices for striped blocks whose length is 0
DECL|field|zeroStripeBuffers
specifier|private
name|ByteBuffer
index|[]
name|zeroStripeBuffers
decl_stmt|;
DECL|field|zeroStripeIndices
specifier|private
name|short
index|[]
name|zeroStripeIndices
decl_stmt|;
comment|// sources
DECL|field|liveIndices
specifier|private
specifier|final
name|byte
index|[]
name|liveIndices
decl_stmt|;
DECL|field|sources
specifier|private
specifier|final
name|DatanodeInfo
index|[]
name|sources
decl_stmt|;
DECL|field|readers
specifier|private
specifier|final
name|List
argument_list|<
name|StripedBlockReader
argument_list|>
name|readers
decl_stmt|;
DECL|field|futures
specifier|private
specifier|final
name|Map
argument_list|<
name|Future
argument_list|<
name|BlockReadStats
argument_list|>
argument_list|,
name|Integer
argument_list|>
name|futures
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|readService
specifier|private
specifier|final
name|CompletionService
argument_list|<
name|BlockReadStats
argument_list|>
name|readService
decl_stmt|;
DECL|method|StripedReader (StripedReconstructor reconstructor, DataNode datanode, Configuration conf, StripedReconstructionInfo stripedReconInfo)
name|StripedReader
parameter_list|(
name|StripedReconstructor
name|reconstructor
parameter_list|,
name|DataNode
name|datanode
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|StripedReconstructionInfo
name|stripedReconInfo
parameter_list|)
block|{
name|stripedReadTimeoutInMills
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DN_EC_RECONSTRUCTION_STRIPED_READ_TIMEOUT_MILLIS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DN_EC_RECONSTRUCTION_STRIPED_READ_TIMEOUT_MILLIS_DEFAULT
argument_list|)
expr_stmt|;
name|stripedReadBufferSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DN_EC_RECONSTRUCTION_STRIPED_READ_BUFFER_SIZE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_DN_EC_RECONSTRUCTION_STRIPED_READ_BUFFER_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|reconstructor
operator|=
name|reconstructor
expr_stmt|;
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|dataBlkNum
operator|=
name|stripedReconInfo
operator|.
name|getEcPolicy
argument_list|()
operator|.
name|getNumDataUnits
argument_list|()
expr_stmt|;
name|parityBlkNum
operator|=
name|stripedReconInfo
operator|.
name|getEcPolicy
argument_list|()
operator|.
name|getNumParityUnits
argument_list|()
expr_stmt|;
name|int
name|cellsNum
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|stripedReconInfo
operator|.
name|getBlockGroup
argument_list|()
operator|.
name|getNumBytes
argument_list|()
operator|-
literal|1
operator|)
operator|/
name|stripedReconInfo
operator|.
name|getEcPolicy
argument_list|()
operator|.
name|getCellSize
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|minRequiredSources
operator|=
name|Math
operator|.
name|min
argument_list|(
name|cellsNum
argument_list|,
name|dataBlkNum
argument_list|)
expr_stmt|;
if|if
condition|(
name|minRequiredSources
operator|<
name|dataBlkNum
condition|)
block|{
name|int
name|zeroStripNum
init|=
name|dataBlkNum
operator|-
name|minRequiredSources
decl_stmt|;
name|zeroStripeBuffers
operator|=
operator|new
name|ByteBuffer
index|[
name|zeroStripNum
index|]
expr_stmt|;
name|zeroStripeIndices
operator|=
operator|new
name|short
index|[
name|zeroStripNum
index|]
expr_stmt|;
block|}
comment|// It is calculated by the maximum number of connections from either sources
comment|// or targets.
name|xmits
operator|=
name|Math
operator|.
name|max
argument_list|(
name|minRequiredSources
argument_list|,
name|stripedReconInfo
operator|.
name|getTargets
argument_list|()
operator|!=
literal|null
condition|?
name|stripedReconInfo
operator|.
name|getTargets
argument_list|()
operator|.
name|length
else|:
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|liveIndices
operator|=
name|stripedReconInfo
operator|.
name|getLiveIndices
argument_list|()
expr_stmt|;
assert|assert
name|liveIndices
operator|!=
literal|null
assert|;
name|this
operator|.
name|sources
operator|=
name|stripedReconInfo
operator|.
name|getSources
argument_list|()
expr_stmt|;
assert|assert
name|sources
operator|!=
literal|null
assert|;
name|readers
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|sources
operator|.
name|length
argument_list|)
expr_stmt|;
name|readService
operator|=
name|reconstructor
operator|.
name|createReadService
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|liveIndices
operator|.
name|length
operator|>=
name|minRequiredSources
argument_list|,
literal|"No enough live striped blocks."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|liveIndices
operator|.
name|length
operator|==
name|sources
operator|.
name|length
argument_list|,
literal|"liveBlockIndices and source datanodes should match"
argument_list|)
expr_stmt|;
block|}
DECL|method|init ()
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
name|initReaders
argument_list|()
expr_stmt|;
name|initBufferSize
argument_list|()
expr_stmt|;
name|initZeroStrip
argument_list|()
expr_stmt|;
block|}
DECL|method|initReaders ()
specifier|private
name|void
name|initReaders
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Store the array indices of source DNs we have read successfully.
comment|// In each iteration of read, the successList list may be updated if
comment|// some source DN is corrupted or slow. And use the updated successList
comment|// list of DNs for next iteration read.
name|successList
operator|=
operator|new
name|int
index|[
name|minRequiredSources
index|]
expr_stmt|;
name|StripedBlockReader
name|reader
decl_stmt|;
name|int
name|nSuccess
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
name|sources
operator|.
name|length
operator|&&
name|nSuccess
operator|<
name|minRequiredSources
condition|;
name|i
operator|++
control|)
block|{
name|reader
operator|=
name|createReader
argument_list|(
name|i
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|readers
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|getBlockReader
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|initOrVerifyChecksum
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|successList
index|[
name|nSuccess
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nSuccess
operator|<
name|minRequiredSources
condition|)
block|{
name|String
name|error
init|=
literal|"Can't find minimum sources required by "
operator|+
literal|"reconstruction, block id: "
operator|+
name|reconstructor
operator|.
name|getBlockGroup
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|error
argument_list|)
throw|;
block|}
block|}
DECL|method|createReader (int idxInSources, long offsetInBlock)
name|StripedBlockReader
name|createReader
parameter_list|(
name|int
name|idxInSources
parameter_list|,
name|long
name|offsetInBlock
parameter_list|)
block|{
return|return
operator|new
name|StripedBlockReader
argument_list|(
name|this
argument_list|,
name|datanode
argument_list|,
name|conf
argument_list|,
name|liveIndices
index|[
name|idxInSources
index|]
argument_list|,
name|reconstructor
operator|.
name|getBlock
argument_list|(
name|liveIndices
index|[
name|idxInSources
index|]
argument_list|)
argument_list|,
name|sources
index|[
name|idxInSources
index|]
argument_list|,
name|offsetInBlock
argument_list|)
return|;
block|}
DECL|method|initBufferSize ()
specifier|private
name|void
name|initBufferSize
parameter_list|()
block|{
name|int
name|bytesPerChecksum
init|=
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
decl_stmt|;
comment|// The bufferSize is flat to divide bytesPerChecksum
name|int
name|readBufferSize
init|=
name|stripedReadBufferSize
decl_stmt|;
name|bufferSize
operator|=
name|readBufferSize
operator|<
name|bytesPerChecksum
condition|?
name|bytesPerChecksum
else|:
name|readBufferSize
operator|-
name|readBufferSize
operator|%
name|bytesPerChecksum
expr_stmt|;
block|}
comment|// init checksum from block reader
DECL|method|initOrVerifyChecksum (StripedBlockReader reader)
specifier|private
name|void
name|initOrVerifyChecksum
parameter_list|(
name|StripedBlockReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|checksum
operator|==
literal|null
condition|)
block|{
name|checksum
operator|=
name|reader
operator|.
name|getBlockReader
argument_list|()
operator|.
name|getDataChecksum
argument_list|()
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|reader
operator|.
name|getBlockReader
argument_list|()
operator|.
name|getDataChecksum
argument_list|()
operator|.
name|equals
argument_list|(
name|checksum
argument_list|)
assert|;
block|}
block|}
DECL|method|allocateReadBuffer ()
specifier|protected
name|ByteBuffer
name|allocateReadBuffer
parameter_list|()
block|{
return|return
name|reconstructor
operator|.
name|allocateBuffer
argument_list|(
name|getBufferSize
argument_list|()
argument_list|)
return|;
block|}
DECL|method|initZeroStrip ()
specifier|private
name|void
name|initZeroStrip
parameter_list|()
block|{
if|if
condition|(
name|zeroStripeBuffers
operator|!=
literal|null
condition|)
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
name|zeroStripeBuffers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|zeroStripeBuffers
index|[
name|i
index|]
operator|=
name|reconstructor
operator|.
name|allocateBuffer
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
block|}
block|}
name|BitSet
name|bitset
init|=
name|reconstructor
operator|.
name|getLiveBitSet
argument_list|()
decl_stmt|;
name|int
name|k
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
name|dataBlkNum
operator|+
name|parityBlkNum
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|bitset
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
if|if
condition|(
name|reconstructor
operator|.
name|getBlockLen
argument_list|(
name|i
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|zeroStripeIndices
index|[
name|k
operator|++
index|]
operator|=
operator|(
name|short
operator|)
name|i
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getReadLength (int index, int reconstructLength)
specifier|private
name|int
name|getReadLength
parameter_list|(
name|int
name|index
parameter_list|,
name|int
name|reconstructLength
parameter_list|)
block|{
comment|// the reading length should not exceed the length for reconstruction
name|long
name|blockLen
init|=
name|reconstructor
operator|.
name|getBlockLen
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|long
name|remaining
init|=
name|blockLen
operator|-
name|reconstructor
operator|.
name|getPositionInBlock
argument_list|()
decl_stmt|;
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|reconstructLength
argument_list|)
return|;
block|}
DECL|method|getInputBuffers (int toReconstructLen)
name|ByteBuffer
index|[]
name|getInputBuffers
parameter_list|(
name|int
name|toReconstructLen
parameter_list|)
block|{
name|ByteBuffer
index|[]
name|inputs
init|=
operator|new
name|ByteBuffer
index|[
name|dataBlkNum
operator|+
name|parityBlkNum
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
name|successList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
name|successList
index|[
name|i
index|]
decl_stmt|;
name|StripedBlockReader
name|reader
init|=
name|getReader
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|reader
operator|.
name|getReadBuffer
argument_list|()
decl_stmt|;
name|paddingBufferToLen
argument_list|(
name|buffer
argument_list|,
name|toReconstructLen
argument_list|)
expr_stmt|;
name|inputs
index|[
name|reader
operator|.
name|getIndex
argument_list|()
index|]
operator|=
operator|(
name|ByteBuffer
operator|)
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|successList
operator|.
name|length
operator|<
name|dataBlkNum
condition|)
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
name|zeroStripeBuffers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ByteBuffer
name|buffer
init|=
name|zeroStripeBuffers
index|[
name|i
index|]
decl_stmt|;
name|paddingBufferToLen
argument_list|(
name|buffer
argument_list|,
name|toReconstructLen
argument_list|)
expr_stmt|;
name|int
name|index
init|=
name|zeroStripeIndices
index|[
name|i
index|]
decl_stmt|;
name|inputs
index|[
name|index
index|]
operator|=
operator|(
name|ByteBuffer
operator|)
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|inputs
return|;
block|}
DECL|method|paddingBufferToLen (ByteBuffer buffer, int len)
specifier|private
name|void
name|paddingBufferToLen
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|>
name|buffer
operator|.
name|limit
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|limit
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
name|int
name|toPadding
init|=
name|len
operator|-
name|buffer
operator|.
name|position
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
name|toPadding
condition|;
name|i
operator|++
control|)
block|{
name|buffer
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
comment|/**    * Read from minimum source DNs required for reconstruction in the iteration.    * First try the success list which we think they are the best DNs    * If source DN is corrupt or slow, try to read some other source DN,    * and will update the success list.    *    * Remember the updated success list and return it for following    * operations and next iteration read.    *    * @param reconstructLength the length to reconstruct.    * @return updated success list of source DNs we do real read    * @throws IOException    */
DECL|method|readMinimumSources (int reconstructLength)
name|void
name|readMinimumSources
parameter_list|(
name|int
name|reconstructLength
parameter_list|)
throws|throws
name|IOException
block|{
name|CorruptedBlocks
name|corruptedBlocks
init|=
operator|new
name|CorruptedBlocks
argument_list|()
decl_stmt|;
try|try
block|{
name|successList
operator|=
name|doReadMinimumSources
argument_list|(
name|reconstructLength
argument_list|,
name|corruptedBlocks
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// report corrupted blocks to NN
name|datanode
operator|.
name|reportCorruptedBlocks
argument_list|(
name|corruptedBlocks
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doReadMinimumSources (int reconstructLength, CorruptedBlocks corruptedBlocks)
name|int
index|[]
name|doReadMinimumSources
parameter_list|(
name|int
name|reconstructLength
parameter_list|,
name|CorruptedBlocks
name|corruptedBlocks
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|reconstructLength
operator|>=
literal|0
operator|&&
name|reconstructLength
operator|<=
name|bufferSize
argument_list|)
expr_stmt|;
name|int
name|nSuccess
init|=
literal|0
decl_stmt|;
name|int
index|[]
name|newSuccess
init|=
operator|new
name|int
index|[
name|minRequiredSources
index|]
decl_stmt|;
name|BitSet
name|usedFlag
init|=
operator|new
name|BitSet
argument_list|(
name|sources
operator|.
name|length
argument_list|)
decl_stmt|;
comment|/*      * Read from minimum source DNs required, the success list contains      * source DNs which we think best.      */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|minRequiredSources
condition|;
name|i
operator|++
control|)
block|{
name|StripedBlockReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|successList
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|int
name|toRead
init|=
name|getReadLength
argument_list|(
name|liveIndices
index|[
name|successList
index|[
name|i
index|]
index|]
argument_list|,
name|reconstructLength
argument_list|)
decl_stmt|;
if|if
condition|(
name|toRead
operator|>
literal|0
condition|)
block|{
name|Callable
argument_list|<
name|BlockReadStats
argument_list|>
name|readCallable
init|=
name|reader
operator|.
name|readFromBlock
argument_list|(
name|toRead
argument_list|,
name|corruptedBlocks
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|BlockReadStats
argument_list|>
name|f
init|=
name|readService
operator|.
name|submit
argument_list|(
name|readCallable
argument_list|)
decl_stmt|;
name|futures
operator|.
name|put
argument_list|(
name|f
argument_list|,
name|successList
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the read length is 0, we don't need to do real read
name|reader
operator|.
name|getReadBuffer
argument_list|()
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|newSuccess
index|[
name|nSuccess
operator|++
index|]
operator|=
name|successList
index|[
name|i
index|]
expr_stmt|;
block|}
name|usedFlag
operator|.
name|set
argument_list|(
name|successList
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|futures
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|StripingChunkReadResult
name|result
init|=
name|StripedBlockUtil
operator|.
name|getNextCompletedStripedRead
argument_list|(
name|readService
argument_list|,
name|futures
argument_list|,
name|stripedReadTimeoutInMills
argument_list|)
decl_stmt|;
name|int
name|resultIndex
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|state
operator|==
name|StripingChunkReadResult
operator|.
name|SUCCESSFUL
condition|)
block|{
name|resultIndex
operator|=
name|result
operator|.
name|index
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|state
operator|==
name|StripingChunkReadResult
operator|.
name|FAILED
condition|)
block|{
comment|// If read failed for some source DN, we should not use it anymore
comment|// and schedule read from another source DN.
name|StripedBlockReader
name|failedReader
init|=
name|readers
operator|.
name|get
argument_list|(
name|result
operator|.
name|index
argument_list|)
decl_stmt|;
name|failedReader
operator|.
name|closeBlockReader
argument_list|()
expr_stmt|;
name|resultIndex
operator|=
name|scheduleNewRead
argument_list|(
name|usedFlag
argument_list|,
name|reconstructLength
argument_list|,
name|corruptedBlocks
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|state
operator|==
name|StripingChunkReadResult
operator|.
name|TIMEOUT
condition|)
block|{
comment|// If timeout, we also schedule a new read.
name|resultIndex
operator|=
name|scheduleNewRead
argument_list|(
name|usedFlag
argument_list|,
name|reconstructLength
argument_list|,
name|corruptedBlocks
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resultIndex
operator|>=
literal|0
condition|)
block|{
name|newSuccess
index|[
name|nSuccess
operator|++
index|]
operator|=
name|resultIndex
expr_stmt|;
if|if
condition|(
name|nSuccess
operator|>=
name|minRequiredSources
condition|)
block|{
comment|// cancel remaining reads if we read successfully from minimum
comment|// number of source DNs required by reconstruction.
name|cancelReads
argument_list|(
name|futures
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|futures
operator|.
name|clear
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Read data interrupted."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|cancelReads
argument_list|(
name|futures
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|futures
operator|.
name|clear
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|nSuccess
operator|<
name|minRequiredSources
condition|)
block|{
name|String
name|error
init|=
literal|"Can't read data from minimum number of sources "
operator|+
literal|"required by reconstruction, block id: "
operator|+
name|reconstructor
operator|.
name|getBlockGroup
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|error
argument_list|)
throw|;
block|}
return|return
name|newSuccess
return|;
block|}
comment|/**    * Schedule a read from some new source DN if some DN is corrupted    * or slow, this is called from the read iteration.    * Initially we may only have<code>minRequiredSources</code> number of    * StripedBlockReader.    * If the position is at the end of target block, don't need to do    * real read, and return the array index of source DN, otherwise -1.    *    * @param used the used source DNs in this iteration.    * @return the array index of source DN if don't need to do real read.    */
DECL|method|scheduleNewRead (BitSet used, int reconstructLength, CorruptedBlocks corruptedBlocks)
specifier|private
name|int
name|scheduleNewRead
parameter_list|(
name|BitSet
name|used
parameter_list|,
name|int
name|reconstructLength
parameter_list|,
name|CorruptedBlocks
name|corruptedBlocks
parameter_list|)
block|{
name|StripedBlockReader
name|reader
init|=
literal|null
decl_stmt|;
comment|// step1: initially we may only have<code>minRequiredSources</code>
comment|// number of StripedBlockReader, and there may be some source DNs we never
comment|// read before, so will try to create StripedBlockReader for one new source
comment|// DN and try to read from it. If found, go to step 3.
name|int
name|m
init|=
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|toRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|==
literal|null
operator|&&
name|m
operator|<
name|sources
operator|.
name|length
condition|)
block|{
name|reader
operator|=
name|createReader
argument_list|(
name|m
argument_list|,
name|reconstructor
operator|.
name|getPositionInBlock
argument_list|()
argument_list|)
expr_stmt|;
name|readers
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|toRead
operator|=
name|getReadLength
argument_list|(
name|liveIndices
index|[
name|m
index|]
argument_list|,
name|reconstructLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|toRead
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|reader
operator|.
name|getBlockReader
argument_list|()
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
literal|null
expr_stmt|;
name|m
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|used
operator|.
name|set
argument_list|(
name|m
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
block|}
comment|// step2: if there is no new source DN we can use, try to find a source
comment|// DN we ever read from but because some reason, e.g., slow, it
comment|// is not in the success DN list at the begin of this iteration, so
comment|// we have not tried it in this iteration. Now we have a chance to
comment|// revisit it again.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|reader
operator|==
literal|null
operator|&&
name|i
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|used
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|StripedBlockReader
name|stripedReader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|toRead
operator|=
name|getReadLength
argument_list|(
name|liveIndices
index|[
name|i
index|]
argument_list|,
name|reconstructLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|toRead
operator|>
literal|0
condition|)
block|{
name|stripedReader
operator|.
name|closeBlockReader
argument_list|()
expr_stmt|;
name|stripedReader
operator|.
name|resetBlockReader
argument_list|(
name|reconstructor
operator|.
name|getPositionInBlock
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stripedReader
operator|.
name|getBlockReader
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|stripedReader
operator|.
name|getReadBuffer
argument_list|()
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|m
operator|=
name|i
expr_stmt|;
name|reader
operator|=
name|stripedReader
expr_stmt|;
block|}
block|}
else|else
block|{
name|used
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|stripedReader
operator|.
name|getReadBuffer
argument_list|()
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|i
return|;
block|}
block|}
block|}
comment|// step3: schedule if find a correct source DN and need to do real read.
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|Callable
argument_list|<
name|BlockReadStats
argument_list|>
name|readCallable
init|=
name|reader
operator|.
name|readFromBlock
argument_list|(
name|toRead
argument_list|,
name|corruptedBlocks
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|BlockReadStats
argument_list|>
name|f
init|=
name|readService
operator|.
name|submit
argument_list|(
name|readCallable
argument_list|)
decl_stmt|;
name|futures
operator|.
name|put
argument_list|(
name|f
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|used
operator|.
name|set
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|// Cancel all reads.
DECL|method|cancelReads (Collection<Future<BlockReadStats>> futures)
specifier|private
specifier|static
name|void
name|cancelReads
parameter_list|(
name|Collection
argument_list|<
name|Future
argument_list|<
name|BlockReadStats
argument_list|>
argument_list|>
name|futures
parameter_list|)
block|{
for|for
control|(
name|Future
argument_list|<
name|BlockReadStats
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|future
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|zeroStripeBuffers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ByteBuffer
name|zeroStripeBuffer
range|:
name|zeroStripeBuffers
control|)
block|{
name|reconstructor
operator|.
name|freeBuffer
argument_list|(
name|zeroStripeBuffer
argument_list|)
expr_stmt|;
block|}
block|}
name|zeroStripeBuffers
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|StripedBlockReader
name|reader
range|:
name|readers
control|)
block|{
name|reconstructor
operator|.
name|freeBuffer
argument_list|(
name|reader
operator|.
name|getReadBuffer
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|freeReadBuffer
argument_list|()
expr_stmt|;
name|reader
operator|.
name|closeBlockReader
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getReconstructor ()
name|StripedReconstructor
name|getReconstructor
parameter_list|()
block|{
return|return
name|reconstructor
return|;
block|}
DECL|method|getReader (int i)
name|StripedBlockReader
name|getReader
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|getBufferSize ()
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|bufferSize
return|;
block|}
DECL|method|getChecksum ()
name|DataChecksum
name|getChecksum
parameter_list|()
block|{
return|return
name|checksum
return|;
block|}
DECL|method|clearBuffers ()
name|void
name|clearBuffers
parameter_list|()
block|{
if|if
condition|(
name|zeroStripeBuffers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ByteBuffer
name|zeroStripeBuffer
range|:
name|zeroStripeBuffers
control|)
block|{
name|zeroStripeBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|StripedBlockReader
name|reader
range|:
name|readers
control|)
block|{
if|if
condition|(
name|reader
operator|.
name|getReadBuffer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|getReadBuffer
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getSocketAddress4Transfer (DatanodeInfo dnInfo)
name|InetSocketAddress
name|getSocketAddress4Transfer
parameter_list|(
name|DatanodeInfo
name|dnInfo
parameter_list|)
block|{
return|return
name|reconstructor
operator|.
name|getSocketAddress4Transfer
argument_list|(
name|dnInfo
argument_list|)
return|;
block|}
DECL|method|getCachingStrategy ()
name|CachingStrategy
name|getCachingStrategy
parameter_list|()
block|{
return|return
name|reconstructor
operator|.
name|getCachingStrategy
argument_list|()
return|;
block|}
comment|/**    * Return the xmits of this EC reconstruction task.    *<p>    * DN uses it to coordinate with NN to adjust the speed of scheduling the    * EC reconstruction tasks to this DN.    *    * @return the xmits of this reconstruction task.    */
DECL|method|getXmits ()
name|int
name|getXmits
parameter_list|()
block|{
return|return
name|xmits
return|;
block|}
block|}
end_class

end_unit

