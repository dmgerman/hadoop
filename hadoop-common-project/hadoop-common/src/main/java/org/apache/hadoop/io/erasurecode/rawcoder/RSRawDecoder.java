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
name|util
operator|.
name|RSUtil
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
comment|/**  * A raw erasure decoder in RS code scheme in pure Java in case native one  * isn't available in some environment. Please always use native implementations  * when possible.  */
end_comment

begin_class
DECL|class|RSRawDecoder
specifier|public
class|class
name|RSRawDecoder
extends|extends
name|AbstractRawErasureDecoder
block|{
comment|// To describe and calculate the needed Vandermonde matrix
DECL|field|errSignature
specifier|private
name|int
index|[]
name|errSignature
decl_stmt|;
DECL|field|primitivePower
specifier|private
name|int
index|[]
name|primitivePower
decl_stmt|;
DECL|method|RSRawDecoder (int numDataUnits, int numParityUnits)
specifier|public
name|RSRawDecoder
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
assert|assert
operator|(
name|getNumDataUnits
argument_list|()
operator|+
name|getNumParityUnits
argument_list|()
operator|<
name|RSUtil
operator|.
name|GF
operator|.
name|getFieldSize
argument_list|()
operator|)
assert|;
name|this
operator|.
name|errSignature
operator|=
operator|new
name|int
index|[
name|numParityUnits
index|]
expr_stmt|;
name|this
operator|.
name|primitivePower
operator|=
name|RSUtil
operator|.
name|getPrimitivePower
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|erasedIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|errSignature
index|[
name|i
index|]
operator|=
name|primitivePower
index|[
name|erasedIndexes
index|[
name|i
index|]
index|]
expr_stmt|;
name|RSUtil
operator|.
name|GF
operator|.
name|substitute
argument_list|(
name|inputs
argument_list|,
name|outputs
index|[
name|i
index|]
argument_list|,
name|primitivePower
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|RSUtil
operator|.
name|GF
operator|.
name|solveVandermondeSystem
argument_list|(
name|errSignature
argument_list|,
name|outputs
argument_list|,
name|erasedIndexes
operator|.
name|length
argument_list|)
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|erasedIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|errSignature
index|[
name|i
index|]
operator|=
name|primitivePower
index|[
name|erasedIndexes
index|[
name|i
index|]
index|]
expr_stmt|;
name|RSUtil
operator|.
name|GF
operator|.
name|substitute
argument_list|(
name|inputs
argument_list|,
name|inputOffsets
argument_list|,
name|dataLen
argument_list|,
name|outputs
index|[
name|i
index|]
argument_list|,
name|outputOffsets
index|[
name|i
index|]
argument_list|,
name|primitivePower
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|RSUtil
operator|.
name|GF
operator|.
name|solveVandermondeSystem
argument_list|(
name|errSignature
argument_list|,
name|outputs
argument_list|,
name|outputOffsets
argument_list|,
name|erasedIndexes
operator|.
name|length
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

