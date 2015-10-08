begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.rawcoder
package|package
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
package|;
end_package

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

begin_comment
comment|/**  * A raw decoder in XOR code scheme in pure Java, adapted from HDFS-RAID.  *  * XOR code is an important primitive code scheme in erasure coding and often  * used in advanced codes, like HitchHiker and LRC, though itself is rarely  * deployed independently.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|XORRawDecoder
specifier|public
class|class
name|XORRawDecoder
extends|extends
name|AbstractRawErasureDecoder
block|{
DECL|method|XORRawDecoder (int numDataUnits, int numParityUnits)
specifier|public
name|XORRawDecoder
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
block|{
name|super
argument_list|(
name|numDataUnits
argument_list|,
name|numParityUnits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doDecode (ByteBuffer[] inputs, int[] erasedIndexes, ByteBuffer[] outputs)
specifier|protected
name|void
name|doDecode
parameter_list|(
name|ByteBuffer
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|,
name|ByteBuffer
index|[]
name|outputs
parameter_list|)
block|{
name|ByteBuffer
name|output
init|=
name|outputs
index|[
literal|0
index|]
decl_stmt|;
name|resetBuffer
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|int
name|erasedIdx
init|=
name|erasedIndexes
index|[
literal|0
index|]
decl_stmt|;
comment|// Process the inputs.
name|int
name|iIdx
decl_stmt|,
name|oIdx
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
name|inputs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// Skip the erased location.
if|if
condition|(
name|i
operator|==
name|erasedIdx
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|iIdx
operator|=
name|inputs
index|[
name|i
index|]
operator|.
name|position
argument_list|()
operator|,
name|oIdx
operator|=
name|output
operator|.
name|position
argument_list|()
init|;
name|iIdx
operator|<
name|inputs
index|[
name|i
index|]
operator|.
name|limit
argument_list|()
condition|;
name|iIdx
operator|++
operator|,
name|oIdx
operator|++
control|)
block|{
name|output
operator|.
name|put
argument_list|(
name|oIdx
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|output
operator|.
name|get
argument_list|(
name|oIdx
argument_list|)
operator|^
name|inputs
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|iIdx
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doDecode (byte[][] inputs, int[] inputOffsets, int dataLen, int[] erasedIndexes, byte[][] outputs, int[] outputOffsets)
specifier|protected
name|void
name|doDecode
parameter_list|(
name|byte
index|[]
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|inputOffsets
parameter_list|,
name|int
name|dataLen
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|,
name|byte
index|[]
index|[]
name|outputs
parameter_list|,
name|int
index|[]
name|outputOffsets
parameter_list|)
block|{
name|byte
index|[]
name|output
init|=
name|outputs
index|[
literal|0
index|]
decl_stmt|;
name|resetBuffer
argument_list|(
name|output
argument_list|,
name|outputOffsets
index|[
literal|0
index|]
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
name|int
name|erasedIdx
init|=
name|erasedIndexes
index|[
literal|0
index|]
decl_stmt|;
comment|// Process the inputs.
name|int
name|iIdx
decl_stmt|,
name|oIdx
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
name|inputs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// Skip the erased location.
if|if
condition|(
name|i
operator|==
name|erasedIdx
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|iIdx
operator|=
name|inputOffsets
index|[
name|i
index|]
operator|,
name|oIdx
operator|=
name|outputOffsets
index|[
literal|0
index|]
init|;
name|iIdx
operator|<
name|inputOffsets
index|[
name|i
index|]
operator|+
name|dataLen
condition|;
name|iIdx
operator|++
operator|,
name|oIdx
operator|++
control|)
block|{
name|output
index|[
name|oIdx
index|]
operator|^=
name|inputs
index|[
name|i
index|]
index|[
name|iIdx
index|]
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

