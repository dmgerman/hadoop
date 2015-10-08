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
name|ECChunk
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  * An abstract raw erasure decoder that's to be inherited by new decoders.  *  * It implements the {@link RawErasureDecoder} interface.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AbstractRawErasureDecoder
specifier|public
specifier|abstract
class|class
name|AbstractRawErasureDecoder
extends|extends
name|AbstractRawErasureCoder
implements|implements
name|RawErasureDecoder
block|{
DECL|method|AbstractRawErasureDecoder (int numDataUnits, int numParityUnits)
specifier|public
name|AbstractRawErasureDecoder
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
DECL|method|decode (ByteBuffer[] inputs, int[] erasedIndexes, ByteBuffer[] outputs)
specifier|public
name|void
name|decode
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
name|checkParameters
argument_list|(
name|inputs
argument_list|,
name|erasedIndexes
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
name|ByteBuffer
name|validInput
init|=
name|findFirstValidInput
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
name|boolean
name|usingDirectBuffer
init|=
name|validInput
operator|.
name|isDirect
argument_list|()
decl_stmt|;
name|int
name|dataLen
init|=
name|validInput
operator|.
name|remaining
argument_list|()
decl_stmt|;
if|if
condition|(
name|dataLen
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|ensureLengthAndType
argument_list|(
name|inputs
argument_list|,
literal|true
argument_list|,
name|dataLen
argument_list|,
name|usingDirectBuffer
argument_list|)
expr_stmt|;
name|ensureLengthAndType
argument_list|(
name|outputs
argument_list|,
literal|false
argument_list|,
name|dataLen
argument_list|,
name|usingDirectBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|usingDirectBuffer
condition|)
block|{
name|doDecode
argument_list|(
name|inputs
argument_list|,
name|erasedIndexes
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
index|[]
name|inputOffsets
init|=
operator|new
name|int
index|[
name|inputs
operator|.
name|length
index|]
decl_stmt|;
name|int
index|[]
name|outputOffsets
init|=
operator|new
name|int
index|[
name|outputs
operator|.
name|length
index|]
decl_stmt|;
name|byte
index|[]
index|[]
name|newInputs
init|=
operator|new
name|byte
index|[
name|inputs
operator|.
name|length
index|]
index|[]
decl_stmt|;
name|byte
index|[]
index|[]
name|newOutputs
init|=
operator|new
name|byte
index|[
name|outputs
operator|.
name|length
index|]
index|[]
decl_stmt|;
name|ByteBuffer
name|buffer
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
operator|++
name|i
control|)
block|{
name|buffer
operator|=
name|inputs
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
name|inputOffsets
index|[
name|i
index|]
operator|=
name|buffer
operator|.
name|arrayOffset
argument_list|()
operator|+
name|buffer
operator|.
name|position
argument_list|()
expr_stmt|;
name|newInputs
index|[
name|i
index|]
operator|=
name|buffer
operator|.
name|array
argument_list|()
expr_stmt|;
block|}
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
name|outputs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|buffer
operator|=
name|outputs
index|[
name|i
index|]
expr_stmt|;
name|outputOffsets
index|[
name|i
index|]
operator|=
name|buffer
operator|.
name|arrayOffset
argument_list|()
operator|+
name|buffer
operator|.
name|position
argument_list|()
expr_stmt|;
name|newOutputs
index|[
name|i
index|]
operator|=
name|buffer
operator|.
name|array
argument_list|()
expr_stmt|;
block|}
name|doDecode
argument_list|(
name|newInputs
argument_list|,
name|inputOffsets
argument_list|,
name|dataLen
argument_list|,
name|erasedIndexes
argument_list|,
name|newOutputs
argument_list|,
name|outputOffsets
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
name|inputs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|buffer
operator|=
name|inputs
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
comment|// dataLen bytes consumed
name|buffer
operator|.
name|position
argument_list|(
name|buffer
operator|.
name|position
argument_list|()
operator|+
name|dataLen
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Perform the real decoding using Direct ByteBuffer.    * @param inputs Direct ByteBuffers expected    * @param erasedIndexes indexes of erased units in the inputs array    * @param outputs Direct ByteBuffers expected    */
DECL|method|doDecode (ByteBuffer[] inputs, int[] erasedIndexes, ByteBuffer[] outputs)
specifier|protected
specifier|abstract
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
function_decl|;
annotation|@
name|Override
DECL|method|decode (byte[][] inputs, int[] erasedIndexes, byte[][] outputs)
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|,
name|byte
index|[]
index|[]
name|outputs
parameter_list|)
block|{
name|checkParameters
argument_list|(
name|inputs
argument_list|,
name|erasedIndexes
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
name|byte
index|[]
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
name|length
decl_stmt|;
if|if
condition|(
name|dataLen
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|ensureLength
argument_list|(
name|inputs
argument_list|,
literal|true
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
name|ensureLength
argument_list|(
name|outputs
argument_list|,
literal|false
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
name|int
index|[]
name|inputOffsets
init|=
operator|new
name|int
index|[
name|inputs
operator|.
name|length
index|]
decl_stmt|;
comment|// ALL ZERO
name|int
index|[]
name|outputOffsets
init|=
operator|new
name|int
index|[
name|outputs
operator|.
name|length
index|]
decl_stmt|;
comment|// ALL ZERO
name|doDecode
argument_list|(
name|inputs
argument_list|,
name|inputOffsets
argument_list|,
name|dataLen
argument_list|,
name|erasedIndexes
argument_list|,
name|outputs
argument_list|,
name|outputOffsets
argument_list|)
expr_stmt|;
block|}
comment|/**    * Perform the real decoding using bytes array, supporting offsets and    * lengths.    * @param inputs the input byte arrays to read data from    * @param inputOffsets offsets for the input byte arrays to read data from    * @param dataLen how much data are to be read from    * @param erasedIndexes indexes of erased units in the inputs array    * @param outputs the output byte arrays to write resultant data into    * @param outputOffsets offsets from which to write resultant data into    */
DECL|method|doDecode (byte[][] inputs, int[] inputOffsets, int dataLen, int[] erasedIndexes, byte[][] outputs, int[] outputOffsets)
specifier|protected
specifier|abstract
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
function_decl|;
annotation|@
name|Override
DECL|method|decode (ECChunk[] inputs, int[] erasedIndexes, ECChunk[] outputs)
specifier|public
name|void
name|decode
parameter_list|(
name|ECChunk
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|,
name|ECChunk
index|[]
name|outputs
parameter_list|)
block|{
name|ByteBuffer
index|[]
name|newInputs
init|=
name|ECChunk
operator|.
name|toBuffers
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
name|ByteBuffer
index|[]
name|newOutputs
init|=
name|ECChunk
operator|.
name|toBuffers
argument_list|(
name|outputs
argument_list|)
decl_stmt|;
name|decode
argument_list|(
name|newInputs
argument_list|,
name|erasedIndexes
argument_list|,
name|newOutputs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check and validate decoding parameters, throw exception accordingly. The    * checking assumes it's a MDS code. Other code  can override this.    * @param inputs input buffers to check    * @param erasedIndexes indexes of erased units in the inputs array    * @param outputs output buffers to check    */
DECL|method|checkParameters (T[] inputs, int[] erasedIndexes, T[] outputs)
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|void
name|checkParameters
parameter_list|(
name|T
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|,
name|T
index|[]
name|outputs
parameter_list|)
block|{
if|if
condition|(
name|inputs
operator|.
name|length
operator|!=
name|getNumParityUnits
argument_list|()
operator|+
name|getNumDataUnits
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid inputs length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|erasedIndexes
operator|.
name|length
operator|!=
name|outputs
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"erasedIndexes and outputs mismatch in length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|erasedIndexes
operator|.
name|length
operator|>
name|getNumParityUnits
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Too many erased, not recoverable"
argument_list|)
throw|;
block|}
name|int
name|validInputs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|input
range|:
name|inputs
control|)
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
name|validInputs
operator|+=
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|validInputs
operator|<
name|getNumDataUnits
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"No enough valid inputs are provided, not recoverable"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get indexes into inputs array for items marked as null, either erased or    * not to read.    * @return indexes into inputs array    */
DECL|method|getErasedOrNotToReadIndexes (T[] inputs)
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|int
index|[]
name|getErasedOrNotToReadIndexes
parameter_list|(
name|T
index|[]
name|inputs
parameter_list|)
block|{
name|int
index|[]
name|invalidIndexes
init|=
operator|new
name|int
index|[
name|inputs
operator|.
name|length
index|]
decl_stmt|;
name|int
name|idx
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
name|inputs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|inputs
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|invalidIndexes
index|[
name|idx
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|invalidIndexes
argument_list|,
name|idx
argument_list|)
return|;
block|}
comment|/**    * Find the valid input from all the inputs.    * @param inputs input buffers to look for valid input    * @return the first valid input    */
DECL|method|findFirstValidInput (T[] inputs)
specifier|protected
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|findFirstValidInput
parameter_list|(
name|T
index|[]
name|inputs
parameter_list|)
block|{
for|for
control|(
name|T
name|input
range|:
name|inputs
control|)
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
return|return
name|input
return|;
block|}
block|}
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid inputs are found, all being null"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

