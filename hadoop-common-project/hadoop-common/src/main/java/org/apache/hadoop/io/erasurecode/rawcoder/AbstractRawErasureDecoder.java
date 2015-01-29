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
comment|/**  * An abstract raw erasure decoder that's to be inherited by new decoders.  *  * It implements the {@link RawErasureDecoder} interface.  */
end_comment

begin_class
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
if|if
condition|(
name|erasedIndexes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|doDecode
argument_list|(
name|inputs
argument_list|,
name|erasedIndexes
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Perform the real decoding using ByteBuffer    * @param inputs    * @param erasedIndexes    * @param outputs    */
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
if|if
condition|(
name|erasedIndexes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|doDecode
argument_list|(
name|inputs
argument_list|,
name|erasedIndexes
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Perform the real decoding using bytes array    * @param inputs    * @param erasedIndexes    * @param outputs    */
DECL|method|doDecode (byte[][] inputs, int[] erasedIndexes, byte[][] outputs)
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
name|erasedIndexes
parameter_list|,
name|byte
index|[]
index|[]
name|outputs
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
name|doDecode
argument_list|(
name|inputs
argument_list|,
name|erasedIndexes
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Perform the real decoding using chunks    * @param inputs    * @param erasedIndexes    * @param outputs    */
DECL|method|doDecode (ECChunk[] inputs, int[] erasedIndexes, ECChunk[] outputs)
specifier|protected
name|void
name|doDecode
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
if|if
condition|(
name|inputs
index|[
literal|0
index|]
operator|.
name|getBuffer
argument_list|()
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|byte
index|[]
index|[]
name|inputBytesArr
init|=
name|ECChunk
operator|.
name|toArray
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|outputBytesArr
init|=
name|ECChunk
operator|.
name|toArray
argument_list|(
name|outputs
argument_list|)
decl_stmt|;
name|doDecode
argument_list|(
name|inputBytesArr
argument_list|,
name|erasedIndexes
argument_list|,
name|outputBytesArr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ByteBuffer
index|[]
name|inputBuffers
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
name|outputBuffers
init|=
name|ECChunk
operator|.
name|toBuffers
argument_list|(
name|outputs
argument_list|)
decl_stmt|;
name|doDecode
argument_list|(
name|inputBuffers
argument_list|,
name|erasedIndexes
argument_list|,
name|outputBuffers
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

