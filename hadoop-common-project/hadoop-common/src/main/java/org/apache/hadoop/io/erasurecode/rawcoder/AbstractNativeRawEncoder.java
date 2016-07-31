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
name|ErasureCoderOptions
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
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
comment|/**  * Abstract native raw encoder for all native coders to extend with.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|AbstractNativeRawEncoder
specifier|abstract
class|class
name|AbstractNativeRawEncoder
extends|extends
name|RawErasureEncoder
block|{
DECL|field|LOG
specifier|public
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractNativeRawEncoder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|AbstractNativeRawEncoder (ErasureCoderOptions coderOptions)
specifier|public
name|AbstractNativeRawEncoder
parameter_list|(
name|ErasureCoderOptions
name|coderOptions
parameter_list|)
block|{
name|super
argument_list|(
name|coderOptions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doEncode (ByteBufferEncodingState encodingState)
specifier|protected
name|void
name|doEncode
parameter_list|(
name|ByteBufferEncodingState
name|encodingState
parameter_list|)
block|{
name|int
index|[]
name|inputOffsets
init|=
operator|new
name|int
index|[
name|encodingState
operator|.
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
name|encodingState
operator|.
name|outputs
operator|.
name|length
index|]
decl_stmt|;
name|int
name|dataLen
init|=
name|encodingState
operator|.
name|inputs
index|[
literal|0
index|]
operator|.
name|remaining
argument_list|()
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
name|encodingState
operator|.
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
name|encodingState
operator|.
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
name|position
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
name|encodingState
operator|.
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
name|encodingState
operator|.
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
name|position
argument_list|()
expr_stmt|;
block|}
name|performEncodeImpl
argument_list|(
name|encodingState
operator|.
name|inputs
argument_list|,
name|inputOffsets
argument_list|,
name|dataLen
argument_list|,
name|encodingState
operator|.
name|outputs
argument_list|,
name|outputOffsets
argument_list|)
expr_stmt|;
block|}
DECL|method|performEncodeImpl ( ByteBuffer[] inputs, int[] inputOffsets, int dataLen, ByteBuffer[] outputs, int[] outputOffsets)
specifier|protected
specifier|abstract
name|void
name|performEncodeImpl
parameter_list|(
name|ByteBuffer
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
name|ByteBuffer
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
DECL|method|doEncode (ByteArrayEncodingState encodingState)
specifier|protected
name|void
name|doEncode
parameter_list|(
name|ByteArrayEncodingState
name|encodingState
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"convertToByteBufferState is invoked, "
operator|+
literal|"not efficiently. Please use direct ByteBuffer inputs/outputs"
argument_list|)
expr_stmt|;
name|ByteBufferEncodingState
name|bbeState
init|=
name|encodingState
operator|.
name|convertToByteBufferState
argument_list|()
decl_stmt|;
name|doEncode
argument_list|(
name|bbeState
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
name|encodingState
operator|.
name|outputs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bbeState
operator|.
name|outputs
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|encodingState
operator|.
name|outputs
index|[
name|i
index|]
argument_list|,
name|encodingState
operator|.
name|outputOffsets
index|[
name|i
index|]
argument_list|,
name|encodingState
operator|.
name|encodeLength
argument_list|)
expr_stmt|;
block|}
block|}
comment|// To link with the underlying data structure in the native layer.
comment|// No get/set as only used by native codes.
DECL|field|nativeCoder
specifier|private
name|long
name|nativeCoder
decl_stmt|;
block|}
end_class

end_unit

