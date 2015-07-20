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

begin_comment
comment|/**  * An abstract raw erasure encoder that's to be inherited by new encoders.  *  * It implements the {@link RawErasureEncoder} interface.  */
end_comment

begin_class
DECL|class|AbstractRawErasureEncoder
specifier|public
specifier|abstract
class|class
name|AbstractRawErasureEncoder
extends|extends
name|AbstractRawErasureCoder
implements|implements
name|RawErasureEncoder
block|{
DECL|method|AbstractRawErasureEncoder (int numDataUnits, int numParityUnits)
specifier|public
name|AbstractRawErasureEncoder
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
DECL|method|encode (ByteBuffer[] inputs, ByteBuffer[] outputs)
specifier|public
name|void
name|encode
parameter_list|(
name|ByteBuffer
index|[]
name|inputs
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
name|outputs
argument_list|)
expr_stmt|;
name|boolean
name|usingDirectBuffer
init|=
name|inputs
index|[
literal|0
index|]
operator|.
name|isDirect
argument_list|()
decl_stmt|;
name|int
name|dataLen
init|=
name|inputs
index|[
literal|0
index|]
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
literal|false
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
name|doEncode
argument_list|(
name|inputs
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
name|doEncode
argument_list|(
name|newInputs
argument_list|,
name|inputOffsets
argument_list|,
name|dataLen
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
comment|// dataLen bytes consumed
block|}
block|}
comment|/**    * Perform the real encoding work using direct ByteBuffer    * @param inputs Direct ByteBuffers expected    * @param outputs Direct ByteBuffers expected    */
DECL|method|doEncode (ByteBuffer[] inputs, ByteBuffer[] outputs)
specifier|protected
specifier|abstract
name|void
name|doEncode
parameter_list|(
name|ByteBuffer
index|[]
name|inputs
parameter_list|,
name|ByteBuffer
index|[]
name|outputs
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|encode (byte[][] inputs, byte[][] outputs)
specifier|public
name|void
name|encode
parameter_list|(
name|byte
index|[]
index|[]
name|inputs
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
name|outputs
argument_list|)
expr_stmt|;
name|int
name|dataLen
init|=
name|inputs
index|[
literal|0
index|]
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
literal|false
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
name|doEncode
argument_list|(
name|inputs
argument_list|,
name|inputOffsets
argument_list|,
name|dataLen
argument_list|,
name|outputs
argument_list|,
name|outputOffsets
argument_list|)
expr_stmt|;
block|}
comment|/**    * Perform the real encoding work using bytes array, supporting offsets    * and lengths.    * @param inputs the input byte arrays to read data from    * @param inputOffsets offsets for the input byte arrays to read data from    * @param dataLen how much data are to be read from    * @param outputs the output byte arrays to write resultant data into    * @param outputOffsets offsets from which to write resultant data into    */
DECL|method|doEncode (byte[][] inputs, int[] inputOffsets, int dataLen, byte[][] outputs, int[] outputOffsets)
specifier|protected
specifier|abstract
name|void
name|doEncode
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
DECL|method|encode (ECChunk[] inputs, ECChunk[] outputs)
specifier|public
name|void
name|encode
parameter_list|(
name|ECChunk
index|[]
name|inputs
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
name|encode
argument_list|(
name|newInputs
argument_list|,
name|newOutputs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check and validate decoding parameters, throw exception accordingly.    * @param inputs input buffers to check    * @param outputs output buffers to check    */
DECL|method|checkParameters (T[] inputs, T[] outputs)
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
name|getNumDataUnits
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid inputs length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|outputs
operator|.
name|length
operator|!=
name|getNumParityUnits
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid outputs length"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

