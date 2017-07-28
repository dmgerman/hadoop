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
name|BitSet
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
name|ExecutorCompletionService
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
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * StripedReconstructor reconstruct one or more missed striped block in the  * striped block group, the minimum number of live striped blocks should be  * no less than data block number.  *  * |<- Striped Block Group -> |  *  blk_0      blk_1       blk_2(*)   blk_3   ...<- A striped block group  *    |          |           |          |  *    v          v           v          v  * +------+   +------+   +------+   +------+  * |cell_0|   |cell_1|   |cell_2|   |cell_3|  ...  * +------+   +------+   +------+   +------+  * |cell_4|   |cell_5|   |cell_6|   |cell_7|  ...  * +------+   +------+   +------+   +------+  * |cell_8|   |cell_9|   |cell10|   |cell11|  ...  * +------+   +------+   +------+   +------+  *  ...         ...       ...         ...  *  *  * We use following steps to reconstruct striped block group, in each round, we  * reconstruct<code>bufferSize</code> data until finish, the  *<code>bufferSize</code> is configurable and may be less or larger than  * cell size:  * step1: read<code>bufferSize</code> data from minimum number of sources  *        required by reconstruction.  * step2: decode data for targets.  * step3: transfer data to targets.  *  * In step1, try to read<code>bufferSize</code> data from minimum number  * of sources , if there is corrupt or stale sources, read from new source  * will be scheduled. The best sources are remembered for next round and  * may be updated in each round.  *  * In step2, typically if source blocks we read are all data blocks, we  * need to call encode, and if there is one parity block, we need to call  * decode. Notice we only read once and reconstruct all missed striped block  * if they are more than one.  *  * In step3, send the reconstructed data to targets by constructing packet  * and send them directly. Same as continuous block replication, we  * don't check the packet ack. Since the datanode doing the reconstruction work  * are one of the source datanodes, so the reconstructed data are sent  * remotely.  *  * There are some points we can do further improvements in next phase:  * 1. we can read the block file directly on the local datanode,  *    currently we use remote block reader. (Notice short-circuit is not  *    a good choice, see inline comments).  * 2. We need to check the packet ack for EC reconstruction? Since EC  *    reconstruction is more expensive than continuous block replication,  *    it needs to read from several other datanodes, should we make sure the  *    reconstructed result received by targets?  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StripedReconstructor
specifier|abstract
class|class
name|StripedReconstructor
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|DataNode
operator|.
name|LOG
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DataNode
name|datanode
decl_stmt|;
DECL|field|ecPolicy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
DECL|field|decoder
specifier|private
name|RawErasureDecoder
name|decoder
decl_stmt|;
DECL|field|blockGroup
specifier|private
specifier|final
name|ExtendedBlock
name|blockGroup
decl_stmt|;
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
comment|// position in striped internal block
DECL|field|positionInBlock
specifier|private
name|long
name|positionInBlock
decl_stmt|;
DECL|field|stripedReader
specifier|private
name|StripedReader
name|stripedReader
decl_stmt|;
DECL|field|stripedReadPool
specifier|private
name|ThreadPoolExecutor
name|stripedReadPool
decl_stmt|;
DECL|field|cachingStrategy
specifier|private
specifier|final
name|CachingStrategy
name|cachingStrategy
decl_stmt|;
DECL|field|maxTargetLength
specifier|private
name|long
name|maxTargetLength
init|=
literal|0L
decl_stmt|;
DECL|field|liveBitSet
specifier|private
specifier|final
name|BitSet
name|liveBitSet
decl_stmt|;
comment|// metrics
DECL|field|bytesRead
specifier|private
name|AtomicLong
name|bytesRead
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|bytesWritten
specifier|private
name|AtomicLong
name|bytesWritten
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|remoteBytesRead
specifier|private
name|AtomicLong
name|remoteBytesRead
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|StripedReconstructor (ErasureCodingWorker worker, StripedReconstructionInfo stripedReconInfo)
name|StripedReconstructor
parameter_list|(
name|ErasureCodingWorker
name|worker
parameter_list|,
name|StripedReconstructionInfo
name|stripedReconInfo
parameter_list|)
block|{
name|this
operator|.
name|stripedReadPool
operator|=
name|worker
operator|.
name|getStripedReadPool
argument_list|()
expr_stmt|;
name|this
operator|.
name|datanode
operator|=
name|worker
operator|.
name|getDatanode
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|worker
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|this
operator|.
name|ecPolicy
operator|=
name|stripedReconInfo
operator|.
name|getEcPolicy
argument_list|()
expr_stmt|;
name|liveBitSet
operator|=
operator|new
name|BitSet
argument_list|(
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
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
name|stripedReconInfo
operator|.
name|getLiveIndices
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|liveBitSet
operator|.
name|set
argument_list|(
name|stripedReconInfo
operator|.
name|getLiveIndices
argument_list|()
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|blockGroup
operator|=
name|stripedReconInfo
operator|.
name|getBlockGroup
argument_list|()
expr_stmt|;
name|stripedReader
operator|=
operator|new
name|StripedReader
argument_list|(
name|this
argument_list|,
name|datanode
argument_list|,
name|conf
argument_list|,
name|stripedReconInfo
argument_list|)
expr_stmt|;
name|cachingStrategy
operator|=
name|CachingStrategy
operator|.
name|newDefaultStrategy
argument_list|()
expr_stmt|;
name|positionInBlock
operator|=
literal|0L
expr_stmt|;
block|}
DECL|method|incrBytesRead (boolean local, long delta)
specifier|public
name|void
name|incrBytesRead
parameter_list|(
name|boolean
name|local
parameter_list|,
name|long
name|delta
parameter_list|)
block|{
if|if
condition|(
name|local
condition|)
block|{
name|bytesRead
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bytesRead
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|remoteBytesRead
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|incrBytesWritten (long delta)
specifier|public
name|void
name|incrBytesWritten
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|bytesWritten
operator|.
name|addAndGet
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|getBytesRead ()
specifier|public
name|long
name|getBytesRead
parameter_list|()
block|{
return|return
name|bytesRead
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getRemoteBytesRead ()
specifier|public
name|long
name|getRemoteBytesRead
parameter_list|()
block|{
return|return
name|remoteBytesRead
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|getBytesWritten ()
specifier|public
name|long
name|getBytesWritten
parameter_list|()
block|{
return|return
name|bytesWritten
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Reconstruct one or more missed striped block in the striped block group,    * the minimum number of live striped blocks should be no less than data    * block number.    *    * @throws IOException    */
DECL|method|reconstruct ()
specifier|abstract
name|void
name|reconstruct
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|useDirectBuffer ()
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
DECL|method|allocateBuffer (int length)
name|ByteBuffer
name|allocateBuffer
parameter_list|(
name|int
name|length
parameter_list|)
block|{
return|return
name|BUFFER_POOL
operator|.
name|getBuffer
argument_list|(
name|useDirectBuffer
argument_list|()
argument_list|,
name|length
argument_list|)
return|;
block|}
DECL|method|freeBuffer (ByteBuffer buffer)
name|void
name|freeBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|BUFFER_POOL
operator|.
name|putBuffer
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlock (int i)
name|ExtendedBlock
name|getBlock
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|StripedBlockUtil
operator|.
name|constructInternalBlock
argument_list|(
name|blockGroup
argument_list|,
name|ecPolicy
argument_list|,
name|i
argument_list|)
return|;
block|}
DECL|method|getBlockLen (int i)
name|long
name|getBlockLen
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|StripedBlockUtil
operator|.
name|getInternalBlockLength
argument_list|(
name|blockGroup
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|ecPolicy
argument_list|,
name|i
argument_list|)
return|;
block|}
comment|// Initialize decoder
DECL|method|initDecoderIfNecessary ()
specifier|protected
name|void
name|initDecoderIfNecessary
parameter_list|()
block|{
if|if
condition|(
name|decoder
operator|==
literal|null
condition|)
block|{
name|ErasureCoderOptions
name|coderOptions
init|=
operator|new
name|ErasureCoderOptions
argument_list|(
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
argument_list|,
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
decl_stmt|;
name|decoder
operator|=
name|CodecUtil
operator|.
name|createRawDecoder
argument_list|(
name|conf
argument_list|,
name|ecPolicy
operator|.
name|getCodecName
argument_list|()
argument_list|,
name|coderOptions
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getPositionInBlock ()
name|long
name|getPositionInBlock
parameter_list|()
block|{
return|return
name|positionInBlock
return|;
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
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|dnInfo
operator|.
name|getXferAddr
argument_list|(
name|datanode
operator|.
name|getDnConf
argument_list|()
operator|.
name|getConnectToDnViaHostname
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getBufferSize ()
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|stripedReader
operator|.
name|getBufferSize
argument_list|()
return|;
block|}
DECL|method|getChecksum ()
specifier|public
name|DataChecksum
name|getChecksum
parameter_list|()
block|{
return|return
name|stripedReader
operator|.
name|getChecksum
argument_list|()
return|;
block|}
DECL|method|getCachingStrategy ()
name|CachingStrategy
name|getCachingStrategy
parameter_list|()
block|{
return|return
name|cachingStrategy
return|;
block|}
DECL|method|createReadService ()
name|CompletionService
argument_list|<
name|Void
argument_list|>
name|createReadService
parameter_list|()
block|{
return|return
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|stripedReadPool
argument_list|)
return|;
block|}
DECL|method|getBlockGroup ()
name|ExtendedBlock
name|getBlockGroup
parameter_list|()
block|{
return|return
name|blockGroup
return|;
block|}
comment|/**    * Get the xmits that _will_ be used for this reconstruction task.    */
DECL|method|getXmits ()
name|int
name|getXmits
parameter_list|()
block|{
return|return
name|stripedReader
operator|.
name|getXmits
argument_list|()
return|;
block|}
DECL|method|getLiveBitSet ()
name|BitSet
name|getLiveBitSet
parameter_list|()
block|{
return|return
name|liveBitSet
return|;
block|}
DECL|method|getMaxTargetLength ()
name|long
name|getMaxTargetLength
parameter_list|()
block|{
return|return
name|maxTargetLength
return|;
block|}
DECL|method|setMaxTargetLength (long maxTargetLength)
name|void
name|setMaxTargetLength
parameter_list|(
name|long
name|maxTargetLength
parameter_list|)
block|{
name|this
operator|.
name|maxTargetLength
operator|=
name|maxTargetLength
expr_stmt|;
block|}
DECL|method|updatePositionInBlock (long positionInBlockArg)
name|void
name|updatePositionInBlock
parameter_list|(
name|long
name|positionInBlockArg
parameter_list|)
block|{
name|this
operator|.
name|positionInBlock
operator|+=
name|positionInBlockArg
expr_stmt|;
block|}
DECL|method|getDecoder ()
name|RawErasureDecoder
name|getDecoder
parameter_list|()
block|{
return|return
name|decoder
return|;
block|}
DECL|method|cleanup ()
name|void
name|cleanup
parameter_list|()
block|{
if|if
condition|(
name|decoder
operator|!=
literal|null
condition|)
block|{
name|decoder
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getStripedReader ()
name|StripedReader
name|getStripedReader
parameter_list|()
block|{
return|return
name|stripedReader
return|;
block|}
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|getDatanode ()
name|DataNode
name|getDatanode
parameter_list|()
block|{
return|return
name|datanode
return|;
block|}
block|}
end_class

end_unit

