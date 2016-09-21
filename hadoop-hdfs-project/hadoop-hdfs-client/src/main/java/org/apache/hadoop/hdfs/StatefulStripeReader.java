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
name|hdfs
operator|.
name|util
operator|.
name|StripedBlockUtil
operator|.
name|StripingChunk
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
name|io
operator|.
name|erasurecode
operator|.
name|ECChunk
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
name|hdfs
operator|.
name|DFSUtilClient
operator|.
name|CorruptedBlocks
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

begin_comment
comment|/**  * The reader for reading a complete {@link StripedBlockUtil.AlignedStripe}  * which belongs to a single stripe.  * Reading cross multiple strips is not supported in this reader.  */
end_comment

begin_class
DECL|class|StatefulStripeReader
class|class
name|StatefulStripeReader
extends|extends
name|StripeReader
block|{
DECL|method|StatefulStripeReader (AlignedStripe alignedStripe, ErasureCodingPolicy ecPolicy, LocatedBlock[] targetBlocks, BlockReaderInfo[] readerInfos, CorruptedBlocks corruptedBlocks, RawErasureDecoder decoder, DFSStripedInputStream dfsStripedInputStream)
name|StatefulStripeReader
parameter_list|(
name|AlignedStripe
name|alignedStripe
parameter_list|,
name|ErasureCodingPolicy
name|ecPolicy
parameter_list|,
name|LocatedBlock
index|[]
name|targetBlocks
parameter_list|,
name|BlockReaderInfo
index|[]
name|readerInfos
parameter_list|,
name|CorruptedBlocks
name|corruptedBlocks
parameter_list|,
name|RawErasureDecoder
name|decoder
parameter_list|,
name|DFSStripedInputStream
name|dfsStripedInputStream
parameter_list|)
block|{
name|super
argument_list|(
name|alignedStripe
argument_list|,
name|ecPolicy
argument_list|,
name|targetBlocks
argument_list|,
name|readerInfos
argument_list|,
name|corruptedBlocks
argument_list|,
name|decoder
argument_list|,
name|dfsStripedInputStream
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareDecodeInputs ()
name|void
name|prepareDecodeInputs
parameter_list|()
block|{
specifier|final
name|ByteBuffer
name|cur
decl_stmt|;
synchronized|synchronized
init|(
name|dfsStripedInputStream
init|)
block|{
name|cur
operator|=
name|dfsStripedInputStream
operator|.
name|getCurStripeBuf
argument_list|()
operator|.
name|duplicate
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|decodeInputs
operator|=
operator|new
name|ECChunk
index|[
name|dataBlkNum
operator|+
name|parityBlkNum
index|]
expr_stmt|;
name|int
name|bufLen
init|=
operator|(
name|int
operator|)
name|alignedStripe
operator|.
name|getSpanInBlock
argument_list|()
decl_stmt|;
name|int
name|bufOff
init|=
operator|(
name|int
operator|)
name|alignedStripe
operator|.
name|getOffsetInBlock
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
name|dataBlkNum
condition|;
name|i
operator|++
control|)
block|{
name|cur
operator|.
name|limit
argument_list|(
name|cur
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
name|bufOff
operator|%
name|cellSize
operator|+
name|cellSize
operator|*
name|i
decl_stmt|;
name|cur
operator|.
name|position
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|cur
operator|.
name|limit
argument_list|(
name|pos
operator|+
name|bufLen
argument_list|)
expr_stmt|;
name|decodeInputs
index|[
name|i
index|]
operator|=
operator|new
name|ECChunk
argument_list|(
name|cur
operator|.
name|slice
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bufLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|alignedStripe
operator|.
name|chunks
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|alignedStripe
operator|.
name|chunks
index|[
name|i
index|]
operator|=
operator|new
name|StripingChunk
argument_list|(
name|decodeInputs
index|[
name|i
index|]
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|prepareParityChunk (int index)
name|boolean
name|prepareParityChunk
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|index
operator|>=
name|dataBlkNum
operator|&&
name|alignedStripe
operator|.
name|chunks
index|[
name|index
index|]
operator|==
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|readerInfos
index|[
name|index
index|]
operator|!=
literal|null
operator|&&
name|readerInfos
index|[
name|index
index|]
operator|.
name|shouldSkip
condition|)
block|{
name|alignedStripe
operator|.
name|chunks
index|[
name|index
index|]
operator|=
operator|new
name|StripingChunk
argument_list|(
name|StripingChunk
operator|.
name|MISSING
argument_list|)
expr_stmt|;
comment|// we have failed the block reader before
return|return
literal|false
return|;
block|}
specifier|final
name|int
name|parityIndex
init|=
name|index
operator|-
name|dataBlkNum
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|dfsStripedInputStream
operator|.
name|getParityBuffer
argument_list|()
operator|.
name|duplicate
argument_list|()
decl_stmt|;
name|buf
operator|.
name|position
argument_list|(
name|cellSize
operator|*
name|parityIndex
argument_list|)
expr_stmt|;
name|buf
operator|.
name|limit
argument_list|(
name|cellSize
operator|*
name|parityIndex
operator|+
operator|(
name|int
operator|)
name|alignedStripe
operator|.
name|range
operator|.
name|spanInBlock
argument_list|)
expr_stmt|;
name|decodeInputs
index|[
name|index
index|]
operator|=
operator|new
name|ECChunk
argument_list|(
name|buf
operator|.
name|slice
argument_list|()
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|alignedStripe
operator|.
name|range
operator|.
name|spanInBlock
argument_list|)
expr_stmt|;
name|alignedStripe
operator|.
name|chunks
index|[
name|index
index|]
operator|=
operator|new
name|StripingChunk
argument_list|(
name|decodeInputs
index|[
name|index
index|]
operator|.
name|getBuffer
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|decode ()
name|void
name|decode
parameter_list|()
block|{
name|finalizeDecodeInputs
argument_list|()
expr_stmt|;
name|decodeAndFillBuffer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

