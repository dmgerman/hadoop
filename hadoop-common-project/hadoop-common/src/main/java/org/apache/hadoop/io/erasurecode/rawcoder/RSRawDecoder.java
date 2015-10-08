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
comment|/**  * A raw erasure decoder in RS code scheme in pure Java in case native one  * isn't available in some environment. Please always use native implementations  * when possible.  *  * Currently this implementation will compute and decode not to read units  * unnecessarily due to the underlying implementation limit in GF. This will be  * addressed in HADOOP-11871.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
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
comment|/**    * We need a set of reusable buffers either for the bytes array    * decoding version or direct buffer decoding version. Normally not both.    *    * For output, in addition to the valid buffers from the caller    * passed from above, we need to provide extra buffers for the internal    * decoding implementation. For output, the caller should provide no more    * than numParityUnits but at least one buffers. And the left buffers will be    * borrowed from either bytesArrayBuffers, for the bytes array version.    *    */
comment|// Reused buffers for decoding with bytes arrays
DECL|field|bytesArrayBuffers
specifier|private
name|byte
index|[]
index|[]
name|bytesArrayBuffers
init|=
operator|new
name|byte
index|[
name|getNumParityUnits
argument_list|()
index|]
index|[]
decl_stmt|;
DECL|field|adjustedByteArrayOutputsParameter
specifier|private
name|byte
index|[]
index|[]
name|adjustedByteArrayOutputsParameter
init|=
operator|new
name|byte
index|[
name|getNumParityUnits
argument_list|()
index|]
index|[]
decl_stmt|;
DECL|field|adjustedOutputOffsets
specifier|private
name|int
index|[]
name|adjustedOutputOffsets
init|=
operator|new
name|int
index|[
name|getNumParityUnits
argument_list|()
index|]
decl_stmt|;
comment|// Reused buffers for decoding with direct ByteBuffers
DECL|field|directBuffers
specifier|private
name|ByteBuffer
index|[]
name|directBuffers
init|=
operator|new
name|ByteBuffer
index|[
name|getNumParityUnits
argument_list|()
index|]
decl_stmt|;
DECL|field|adjustedDirectBufferOutputsParameter
specifier|private
name|ByteBuffer
index|[]
name|adjustedDirectBufferOutputsParameter
init|=
operator|new
name|ByteBuffer
index|[
name|getNumParityUnits
argument_list|()
index|]
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
if|if
condition|(
name|numDataUnits
operator|+
name|numParityUnits
operator|>=
name|RSUtil
operator|.
name|GF
operator|.
name|getFieldSize
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid numDataUnits and numParityUnits"
argument_list|)
throw|;
block|}
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
DECL|method|doDecodeImpl (ByteBuffer[] inputs, int[] erasedIndexes, ByteBuffer[] outputs)
specifier|private
name|void
name|doDecodeImpl
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
name|valid
init|=
name|findFirstValidInput
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
name|int
name|dataLen
init|=
name|valid
operator|.
name|remaining
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
name|dataLen
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
DECL|method|doDecodeImpl (byte[][] inputs, int[] inputOffsets, int dataLen, int[] erasedIndexes, byte[][] outputs, int[] outputOffsets)
specifier|private
name|void
name|doDecodeImpl
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
comment|/**      * As passed parameters are friendly to callers but not to the underlying      * implementations, so we have to adjust them before calling doDecodeImpl.      */
name|int
index|[]
name|erasedOrNotToReadIndexes
init|=
name|getErasedOrNotToReadIndexes
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
comment|// Prepare for adjustedOutputsParameter
comment|// First reset the positions needed this time
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|erasedOrNotToReadIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|adjustedByteArrayOutputsParameter
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|adjustedOutputOffsets
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
comment|// Use the caller passed buffers in erasedIndexes positions
for|for
control|(
name|int
name|outputIdx
init|=
literal|0
init|,
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
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|erasedOrNotToReadIndexes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// If this index is one requested by the caller via erasedIndexes, then
comment|// we use the passed output buffer to avoid copying data thereafter.
if|if
condition|(
name|erasedIndexes
index|[
name|i
index|]
operator|==
name|erasedOrNotToReadIndexes
index|[
name|j
index|]
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|adjustedByteArrayOutputsParameter
index|[
name|j
index|]
operator|=
name|resetBuffer
argument_list|(
name|outputs
index|[
name|outputIdx
index|]
argument_list|,
name|outputOffsets
index|[
name|outputIdx
index|]
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
name|adjustedOutputOffsets
index|[
name|j
index|]
operator|=
name|outputOffsets
index|[
name|outputIdx
index|]
expr_stmt|;
name|outputIdx
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Inputs not fully corresponding to erasedIndexes in null places"
argument_list|)
throw|;
block|}
block|}
comment|// Use shared buffers for other positions (not set yet)
for|for
control|(
name|int
name|bufferIdx
init|=
literal|0
init|,
name|i
init|=
literal|0
init|;
name|i
operator|<
name|erasedOrNotToReadIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|adjustedByteArrayOutputsParameter
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|adjustedByteArrayOutputsParameter
index|[
name|i
index|]
operator|=
name|resetBuffer
argument_list|(
name|checkGetBytesArrayBuffer
argument_list|(
name|bufferIdx
argument_list|,
name|dataLen
argument_list|)
argument_list|,
literal|0
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
name|adjustedOutputOffsets
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
comment|// Always 0 for such temp output
name|bufferIdx
operator|++
expr_stmt|;
block|}
block|}
name|doDecodeImpl
argument_list|(
name|inputs
argument_list|,
name|inputOffsets
argument_list|,
name|dataLen
argument_list|,
name|erasedOrNotToReadIndexes
argument_list|,
name|adjustedByteArrayOutputsParameter
argument_list|,
name|adjustedOutputOffsets
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
name|validInput
init|=
name|findFirstValidInput
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
name|int
name|dataLen
init|=
name|validInput
operator|.
name|remaining
argument_list|()
decl_stmt|;
comment|/**      * As passed parameters are friendly to callers but not to the underlying      * implementations, so we have to adjust them before calling doDecodeImpl.      */
name|int
index|[]
name|erasedOrNotToReadIndexes
init|=
name|getErasedOrNotToReadIndexes
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
comment|// Prepare for adjustedDirectBufferOutputsParameter
comment|// First reset the positions needed this time
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|erasedOrNotToReadIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|adjustedDirectBufferOutputsParameter
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
comment|// Use the caller passed buffers in erasedIndexes positions
for|for
control|(
name|int
name|outputIdx
init|=
literal|0
init|,
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
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|erasedOrNotToReadIndexes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// If this index is one requested by the caller via erasedIndexes, then
comment|// we use the passed output buffer to avoid copying data thereafter.
if|if
condition|(
name|erasedIndexes
index|[
name|i
index|]
operator|==
name|erasedOrNotToReadIndexes
index|[
name|j
index|]
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|adjustedDirectBufferOutputsParameter
index|[
name|j
index|]
operator|=
name|resetBuffer
argument_list|(
name|outputs
index|[
name|outputIdx
operator|++
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Inputs not fully corresponding to erasedIndexes in null places"
argument_list|)
throw|;
block|}
block|}
comment|// Use shared buffers for other positions (not set yet)
for|for
control|(
name|int
name|bufferIdx
init|=
literal|0
init|,
name|i
init|=
literal|0
init|;
name|i
operator|<
name|erasedOrNotToReadIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|adjustedDirectBufferOutputsParameter
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|ByteBuffer
name|buffer
init|=
name|checkGetDirectBuffer
argument_list|(
name|bufferIdx
argument_list|,
name|dataLen
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|limit
argument_list|(
name|dataLen
argument_list|)
expr_stmt|;
name|adjustedDirectBufferOutputsParameter
index|[
name|i
index|]
operator|=
name|resetBuffer
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|bufferIdx
operator|++
expr_stmt|;
block|}
block|}
name|doDecodeImpl
argument_list|(
name|inputs
argument_list|,
name|erasedOrNotToReadIndexes
argument_list|,
name|adjustedDirectBufferOutputsParameter
argument_list|)
expr_stmt|;
block|}
DECL|method|checkGetBytesArrayBuffer (int idx, int bufferLen)
specifier|private
name|byte
index|[]
name|checkGetBytesArrayBuffer
parameter_list|(
name|int
name|idx
parameter_list|,
name|int
name|bufferLen
parameter_list|)
block|{
if|if
condition|(
name|bytesArrayBuffers
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|bytesArrayBuffers
index|[
name|idx
index|]
operator|.
name|length
operator|<
name|bufferLen
condition|)
block|{
name|bytesArrayBuffers
index|[
name|idx
index|]
operator|=
operator|new
name|byte
index|[
name|bufferLen
index|]
expr_stmt|;
block|}
return|return
name|bytesArrayBuffers
index|[
name|idx
index|]
return|;
block|}
DECL|method|checkGetDirectBuffer (int idx, int bufferLen)
specifier|private
name|ByteBuffer
name|checkGetDirectBuffer
parameter_list|(
name|int
name|idx
parameter_list|,
name|int
name|bufferLen
parameter_list|)
block|{
if|if
condition|(
name|directBuffers
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|directBuffers
index|[
name|idx
index|]
operator|.
name|capacity
argument_list|()
operator|<
name|bufferLen
condition|)
block|{
name|directBuffers
index|[
name|idx
index|]
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|bufferLen
argument_list|)
expr_stmt|;
block|}
return|return
name|directBuffers
index|[
name|idx
index|]
return|;
block|}
block|}
end_class

end_unit

