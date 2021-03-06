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
comment|/**  * A raw erasure encoder in RS code scheme in pure Java in case native one  * isn't available in some environment. Please always use native implementations  * when possible.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RSLegacyRawEncoder
specifier|public
class|class
name|RSLegacyRawEncoder
extends|extends
name|RawErasureEncoder
block|{
DECL|field|generatingPolynomial
specifier|private
name|int
index|[]
name|generatingPolynomial
decl_stmt|;
DECL|method|RSLegacyRawEncoder (ErasureCoderOptions coderOptions)
specifier|public
name|RSLegacyRawEncoder
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
name|int
index|[]
name|primitivePower
init|=
name|RSUtil
operator|.
name|getPrimitivePower
argument_list|(
name|getNumDataUnits
argument_list|()
argument_list|,
name|getNumParityUnits
argument_list|()
argument_list|)
decl_stmt|;
comment|// compute generating polynomial
name|int
index|[]
name|gen
init|=
block|{
literal|1
block|}
decl_stmt|;
name|int
index|[]
name|poly
init|=
operator|new
name|int
index|[
literal|2
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
name|getNumParityUnits
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|poly
index|[
literal|0
index|]
operator|=
name|primitivePower
index|[
name|i
index|]
expr_stmt|;
name|poly
index|[
literal|1
index|]
operator|=
literal|1
expr_stmt|;
name|gen
operator|=
name|RSUtil
operator|.
name|GF
operator|.
name|multiply
argument_list|(
name|gen
argument_list|,
name|poly
argument_list|)
expr_stmt|;
block|}
comment|// generating polynomial has all generating roots
name|generatingPolynomial
operator|=
name|gen
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
name|CoderUtil
operator|.
name|resetOutputBuffers
argument_list|(
name|encodingState
operator|.
name|outputs
argument_list|,
name|encodingState
operator|.
name|encodeLength
argument_list|)
expr_stmt|;
comment|// parity units + data units
name|ByteBuffer
index|[]
name|all
init|=
operator|new
name|ByteBuffer
index|[
name|encodingState
operator|.
name|outputs
operator|.
name|length
operator|+
name|encodingState
operator|.
name|inputs
operator|.
name|length
index|]
decl_stmt|;
if|if
condition|(
name|allowChangeInputs
argument_list|()
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|outputs
argument_list|,
literal|0
argument_list|,
name|all
argument_list|,
literal|0
argument_list|,
name|encodingState
operator|.
name|outputs
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|inputs
argument_list|,
literal|0
argument_list|,
name|all
argument_list|,
name|encodingState
operator|.
name|outputs
operator|.
name|length
argument_list|,
name|encodingState
operator|.
name|inputs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|outputs
argument_list|,
literal|0
argument_list|,
name|all
argument_list|,
literal|0
argument_list|,
name|encodingState
operator|.
name|outputs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|/**        * Note when this coder would be really (rarely) used in a production        * system, this can  be optimized to cache and reuse the new allocated        * buffers avoiding reallocating.        */
name|ByteBuffer
name|tmp
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
name|i
operator|++
control|)
block|{
name|tmp
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|encodingState
operator|.
name|inputs
index|[
name|i
index|]
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|put
argument_list|(
name|encodingState
operator|.
name|inputs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|flip
argument_list|()
expr_stmt|;
name|all
index|[
name|encodingState
operator|.
name|outputs
operator|.
name|length
operator|+
name|i
index|]
operator|=
name|tmp
expr_stmt|;
block|}
block|}
comment|// Compute the remainder
name|RSUtil
operator|.
name|GF
operator|.
name|remainder
argument_list|(
name|all
argument_list|,
name|generatingPolynomial
argument_list|)
expr_stmt|;
block|}
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
name|int
name|dataLen
init|=
name|encodingState
operator|.
name|encodeLength
decl_stmt|;
name|CoderUtil
operator|.
name|resetOutputBuffers
argument_list|(
name|encodingState
operator|.
name|outputs
argument_list|,
name|encodingState
operator|.
name|outputOffsets
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
comment|// parity units + data units
name|byte
index|[]
index|[]
name|all
init|=
operator|new
name|byte
index|[
name|encodingState
operator|.
name|outputs
operator|.
name|length
operator|+
name|encodingState
operator|.
name|inputs
operator|.
name|length
index|]
index|[]
decl_stmt|;
name|int
index|[]
name|allOffsets
init|=
operator|new
name|int
index|[
name|encodingState
operator|.
name|outputOffsets
operator|.
name|length
operator|+
name|encodingState
operator|.
name|inputOffsets
operator|.
name|length
index|]
decl_stmt|;
if|if
condition|(
name|allowChangeInputs
argument_list|()
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|outputs
argument_list|,
literal|0
argument_list|,
name|all
argument_list|,
literal|0
argument_list|,
name|encodingState
operator|.
name|outputs
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|inputs
argument_list|,
literal|0
argument_list|,
name|all
argument_list|,
name|encodingState
operator|.
name|outputs
operator|.
name|length
argument_list|,
name|encodingState
operator|.
name|inputs
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|outputOffsets
argument_list|,
literal|0
argument_list|,
name|allOffsets
argument_list|,
literal|0
argument_list|,
name|encodingState
operator|.
name|outputOffsets
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|inputOffsets
argument_list|,
literal|0
argument_list|,
name|allOffsets
argument_list|,
name|encodingState
operator|.
name|outputOffsets
operator|.
name|length
argument_list|,
name|encodingState
operator|.
name|inputOffsets
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|outputs
argument_list|,
literal|0
argument_list|,
name|all
argument_list|,
literal|0
argument_list|,
name|encodingState
operator|.
name|outputs
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|encodingState
operator|.
name|outputOffsets
argument_list|,
literal|0
argument_list|,
name|allOffsets
argument_list|,
literal|0
argument_list|,
name|encodingState
operator|.
name|outputOffsets
operator|.
name|length
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
name|inputs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|all
index|[
name|encodingState
operator|.
name|outputs
operator|.
name|length
operator|+
name|i
index|]
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|encodingState
operator|.
name|inputs
index|[
name|i
index|]
argument_list|,
name|encodingState
operator|.
name|inputOffsets
index|[
name|i
index|]
argument_list|,
name|encodingState
operator|.
name|inputOffsets
index|[
name|i
index|]
operator|+
name|dataLen
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Compute the remainder
name|RSUtil
operator|.
name|GF
operator|.
name|remainder
argument_list|(
name|all
argument_list|,
name|allOffsets
argument_list|,
name|dataLen
argument_list|,
name|generatingPolynomial
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

