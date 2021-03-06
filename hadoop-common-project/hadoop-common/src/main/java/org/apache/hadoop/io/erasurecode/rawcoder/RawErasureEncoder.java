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

begin_comment
comment|/**  * An abstract raw erasure encoder that's to be inherited by new encoders.  *  * Raw erasure coder is part of erasure codec framework, where erasure coder is  * used to encode/decode a group of blocks (BlockGroup) according to the codec  * specific BlockGroup layout and logic. An erasure coder extracts chunks of  * data from the blocks and can employ various low level raw erasure coders to  * perform encoding/decoding against the chunks.  *  * To distinguish from erasure coder, here raw erasure coder is used to mean the  * low level constructs, since it only takes care of the math calculation with  * a group of byte buffers.  *  * Note it mainly provides encode() calls, which should be stateless and may be  * made thread-safe in future.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RawErasureEncoder
specifier|public
specifier|abstract
class|class
name|RawErasureEncoder
block|{
DECL|field|coderOptions
specifier|private
specifier|final
name|ErasureCoderOptions
name|coderOptions
decl_stmt|;
DECL|method|RawErasureEncoder (ErasureCoderOptions coderOptions)
specifier|public
name|RawErasureEncoder
parameter_list|(
name|ErasureCoderOptions
name|coderOptions
parameter_list|)
block|{
name|this
operator|.
name|coderOptions
operator|=
name|coderOptions
expr_stmt|;
block|}
comment|/**    * Encode with inputs and generates outputs.    *    * Note, for both inputs and outputs, no mixing of on-heap buffers and direct    * buffers are allowed.    *    * If the coder option ALLOW_CHANGE_INPUTS is set true (false by default), the    * content of input buffers may change after the call, subject to concrete    * implementation. Anyway the positions of input buffers will move forward.    *    * @param inputs input buffers to read data from. The buffers' remaining will    *               be 0 after encoding    * @param outputs output buffers to put the encoded data into, ready to read    *                after the call    * @throws IOException if the encoder is closed.    */
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
throws|throws
name|IOException
block|{
name|ByteBufferEncodingState
name|bbeState
init|=
operator|new
name|ByteBufferEncodingState
argument_list|(
name|this
argument_list|,
name|inputs
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
name|boolean
name|usingDirectBuffer
init|=
name|bbeState
operator|.
name|usingDirectBuffer
decl_stmt|;
name|int
name|dataLen
init|=
name|bbeState
operator|.
name|encodeLength
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
name|int
index|[]
name|inputPositions
init|=
operator|new
name|int
index|[
name|inputs
operator|.
name|length
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
name|inputPositions
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
operator|!=
literal|null
condition|)
block|{
name|inputPositions
index|[
name|i
index|]
operator|=
name|inputs
index|[
name|i
index|]
operator|.
name|position
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|usingDirectBuffer
condition|)
block|{
name|doEncode
argument_list|(
name|bbeState
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ByteArrayEncodingState
name|baeState
init|=
name|bbeState
operator|.
name|convertToByteArrayState
argument_list|()
decl_stmt|;
name|doEncode
argument_list|(
name|baeState
argument_list|)
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
operator|!=
literal|null
condition|)
block|{
comment|// dataLen bytes consumed
name|inputs
index|[
name|i
index|]
operator|.
name|position
argument_list|(
name|inputPositions
index|[
name|i
index|]
operator|+
name|dataLen
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Perform the real encoding work using direct ByteBuffer.    * @param encodingState the encoding state    */
DECL|method|doEncode (ByteBufferEncodingState encodingState)
specifier|protected
specifier|abstract
name|void
name|doEncode
parameter_list|(
name|ByteBufferEncodingState
name|encodingState
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Encode with inputs and generates outputs. More see above.    *    * @param inputs input buffers to read data from    * @param outputs output buffers to put the encoded data into, read to read    *                after the call    */
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
throws|throws
name|IOException
block|{
name|ByteArrayEncodingState
name|baeState
init|=
operator|new
name|ByteArrayEncodingState
argument_list|(
name|this
argument_list|,
name|inputs
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
name|int
name|dataLen
init|=
name|baeState
operator|.
name|encodeLength
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
name|doEncode
argument_list|(
name|baeState
argument_list|)
expr_stmt|;
block|}
comment|/**    * Perform the real encoding work using bytes array, supporting offsets    * and lengths.    * @param encodingState the encoding state    */
DECL|method|doEncode (ByteArrayEncodingState encodingState)
specifier|protected
specifier|abstract
name|void
name|doEncode
parameter_list|(
name|ByteArrayEncodingState
name|encodingState
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Encode with inputs and generates outputs. More see above.    *    * @param inputs input buffers to read data from    * @param outputs output buffers to put the encoded data into, read to read    *                after the call    * @throws IOException if the encoder is closed.    */
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
throws|throws
name|IOException
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
DECL|method|getNumDataUnits ()
specifier|public
name|int
name|getNumDataUnits
parameter_list|()
block|{
return|return
name|coderOptions
operator|.
name|getNumDataUnits
argument_list|()
return|;
block|}
DECL|method|getNumParityUnits ()
specifier|public
name|int
name|getNumParityUnits
parameter_list|()
block|{
return|return
name|coderOptions
operator|.
name|getNumParityUnits
argument_list|()
return|;
block|}
DECL|method|getNumAllUnits ()
specifier|public
name|int
name|getNumAllUnits
parameter_list|()
block|{
return|return
name|coderOptions
operator|.
name|getNumAllUnits
argument_list|()
return|;
block|}
comment|/**    * Tell if direct buffer is preferred or not. It's for callers to    * decide how to allocate coding chunk buffers, using DirectByteBuffer or    * bytes array. It will return false by default.    * @return true if native buffer is preferred for performance consideration,    * otherwise false.    */
DECL|method|preferDirectBuffer ()
specifier|public
name|boolean
name|preferDirectBuffer
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Allow change into input buffers or not while perform encoding/decoding.    * @return true if it's allowed to change inputs, false otherwise    */
DECL|method|allowChangeInputs ()
specifier|public
name|boolean
name|allowChangeInputs
parameter_list|()
block|{
return|return
name|coderOptions
operator|.
name|allowChangeInputs
argument_list|()
return|;
block|}
comment|/**    * Allow to dump verbose info during encoding/decoding.    * @return true if it's allowed to do verbose dump, false otherwise.    */
DECL|method|allowVerboseDump ()
specifier|public
name|boolean
name|allowVerboseDump
parameter_list|()
block|{
return|return
name|coderOptions
operator|.
name|allowVerboseDump
argument_list|()
return|;
block|}
comment|/**    * Should be called when release this coder. Good chance to release encoding    * or decoding buffers    */
DECL|method|release ()
specifier|public
name|void
name|release
parameter_list|()
block|{
comment|// Nothing to do here.
block|}
block|}
end_class

end_unit

