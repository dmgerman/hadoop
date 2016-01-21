begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.coder.util
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
name|coder
operator|.
name|util
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
name|RawErasureEncoder
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

begin_comment
comment|/**  * Some utilities for Hitchhiker coding.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HHUtil
specifier|public
specifier|final
class|class
name|HHUtil
block|{
DECL|method|HHUtil ()
specifier|private
name|HHUtil
parameter_list|()
block|{
comment|// No called
block|}
DECL|method|initPiggyBackIndexWithoutPBVec (int numDataUnits, int numParityUnits)
specifier|public
specifier|static
name|int
index|[]
name|initPiggyBackIndexWithoutPBVec
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|)
block|{
specifier|final
name|int
name|piggyBackSize
init|=
name|numDataUnits
operator|/
operator|(
name|numParityUnits
operator|-
literal|1
operator|)
decl_stmt|;
name|int
index|[]
name|piggyBackIndex
init|=
operator|new
name|int
index|[
name|numParityUnits
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
name|numDataUnits
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
operator|(
name|i
operator|%
name|piggyBackSize
operator|)
operator|==
literal|0
condition|)
block|{
name|piggyBackIndex
index|[
name|i
operator|/
name|piggyBackSize
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
name|piggyBackIndex
index|[
name|numParityUnits
operator|-
literal|1
index|]
operator|=
name|numDataUnits
expr_stmt|;
return|return
name|piggyBackIndex
return|;
block|}
DECL|method|initPiggyBackFullIndexVec (int numDataUnits, int[] piggyBackIndex)
specifier|public
specifier|static
name|int
index|[]
name|initPiggyBackFullIndexVec
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
index|[]
name|piggyBackIndex
parameter_list|)
block|{
name|int
index|[]
name|piggyBackFullIndex
init|=
operator|new
name|int
index|[
name|numDataUnits
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|piggyBackIndex
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|int
name|j
init|=
name|piggyBackIndex
index|[
name|i
operator|-
literal|1
index|]
init|;
name|j
operator|<
name|piggyBackIndex
index|[
name|i
index|]
condition|;
operator|++
name|j
control|)
block|{
name|piggyBackFullIndex
index|[
name|j
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
return|return
name|piggyBackFullIndex
return|;
block|}
DECL|method|getPiggyBacksFromInput (ByteBuffer[] inputs, int[] piggyBackIndex, int numParityUnits, int pgIndex, RawErasureEncoder encoder)
specifier|public
specifier|static
name|ByteBuffer
index|[]
name|getPiggyBacksFromInput
parameter_list|(
name|ByteBuffer
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|piggyBackIndex
parameter_list|,
name|int
name|numParityUnits
parameter_list|,
name|int
name|pgIndex
parameter_list|,
name|RawErasureEncoder
name|encoder
parameter_list|)
block|{
name|ByteBuffer
index|[]
name|emptyInput
init|=
operator|new
name|ByteBuffer
index|[
name|inputs
operator|.
name|length
index|]
decl_stmt|;
name|ByteBuffer
index|[]
name|tempInput
init|=
operator|new
name|ByteBuffer
index|[
name|inputs
operator|.
name|length
index|]
decl_stmt|;
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
name|m
init|=
literal|0
init|;
name|m
operator|<
name|inputs
operator|.
name|length
condition|;
operator|++
name|m
control|)
block|{
if|if
condition|(
name|inputs
index|[
name|m
index|]
operator|!=
literal|null
condition|)
block|{
name|emptyInput
index|[
name|m
index|]
operator|=
name|allocateByteBuffer
argument_list|(
name|inputs
index|[
name|m
index|]
operator|.
name|isDirect
argument_list|()
argument_list|,
name|inputs
index|[
name|m
index|]
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ByteBuffer
index|[]
name|tempOutput
init|=
operator|new
name|ByteBuffer
index|[
name|numParityUnits
index|]
decl_stmt|;
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|numParityUnits
condition|;
operator|++
name|m
control|)
block|{
name|tempOutput
index|[
name|m
index|]
operator|=
name|allocateByteBuffer
argument_list|(
name|inputs
index|[
name|m
index|]
operator|.
name|isDirect
argument_list|()
argument_list|,
name|inputs
index|[
literal|0
index|]
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ByteBuffer
index|[]
name|piggyBacks
init|=
operator|new
name|ByteBuffer
index|[
name|numParityUnits
operator|-
literal|1
index|]
decl_stmt|;
assert|assert
operator|(
name|piggyBackIndex
operator|.
name|length
operator|>=
name|numParityUnits
operator|)
assert|;
comment|// using underlying RS code to create piggybacks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numParityUnits
operator|-
literal|1
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|int
name|k
init|=
name|piggyBackIndex
index|[
name|i
index|]
init|;
name|k
operator|<
name|piggyBackIndex
index|[
name|i
operator|+
literal|1
index|]
condition|;
operator|++
name|k
control|)
block|{
name|tempInput
index|[
name|k
index|]
operator|=
name|inputs
index|[
name|k
index|]
expr_stmt|;
name|inputPositions
index|[
name|k
index|]
operator|=
name|inputs
index|[
name|k
index|]
operator|.
name|position
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|emptyInput
operator|.
name|length
condition|;
operator|++
name|n
control|)
block|{
if|if
condition|(
name|tempInput
index|[
name|n
index|]
operator|==
literal|null
condition|)
block|{
name|tempInput
index|[
name|n
index|]
operator|=
name|emptyInput
index|[
name|n
index|]
expr_stmt|;
name|inputPositions
index|[
name|n
index|]
operator|=
name|emptyInput
index|[
name|n
index|]
operator|.
name|position
argument_list|()
expr_stmt|;
block|}
block|}
name|encoder
operator|.
name|encode
argument_list|(
name|tempInput
argument_list|,
name|tempOutput
argument_list|)
expr_stmt|;
name|piggyBacks
index|[
name|i
index|]
operator|=
name|cloneBufferData
argument_list|(
name|tempOutput
index|[
name|pgIndex
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tempInput
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|tempInput
index|[
name|j
index|]
operator|!=
literal|null
condition|)
block|{
name|tempInput
index|[
name|j
index|]
operator|.
name|position
argument_list|(
name|inputPositions
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|tempInput
index|[
name|j
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tempOutput
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|tempOutput
index|[
name|j
index|]
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|piggyBacks
return|;
block|}
DECL|method|cloneBufferData (ByteBuffer srcBuffer)
specifier|private
specifier|static
name|ByteBuffer
name|cloneBufferData
parameter_list|(
name|ByteBuffer
name|srcBuffer
parameter_list|)
block|{
name|ByteBuffer
name|destBuffer
decl_stmt|;
name|byte
index|[]
name|bytesArr
init|=
operator|new
name|byte
index|[
name|srcBuffer
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|srcBuffer
operator|.
name|mark
argument_list|()
expr_stmt|;
name|srcBuffer
operator|.
name|get
argument_list|(
name|bytesArr
argument_list|)
expr_stmt|;
name|srcBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|srcBuffer
operator|.
name|isDirect
argument_list|()
condition|)
block|{
name|destBuffer
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytesArr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destBuffer
operator|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|srcBuffer
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
name|destBuffer
operator|.
name|put
argument_list|(
name|bytesArr
argument_list|)
expr_stmt|;
name|destBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
return|return
name|destBuffer
return|;
block|}
DECL|method|allocateByteBuffer (boolean useDirectBuffer, int bufSize)
specifier|public
specifier|static
name|ByteBuffer
name|allocateByteBuffer
parameter_list|(
name|boolean
name|useDirectBuffer
parameter_list|,
name|int
name|bufSize
parameter_list|)
block|{
if|if
condition|(
name|useDirectBuffer
condition|)
block|{
return|return
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|bufSize
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|bufSize
argument_list|)
return|;
block|}
block|}
DECL|method|getPiggyBackForDecode (ByteBuffer[][] inputs, ByteBuffer[][] outputs, int pbParityIndex, int numDataUnits, int numParityUnits, int pbIndex)
specifier|public
specifier|static
name|ByteBuffer
name|getPiggyBackForDecode
parameter_list|(
name|ByteBuffer
index|[]
index|[]
name|inputs
parameter_list|,
name|ByteBuffer
index|[]
index|[]
name|outputs
parameter_list|,
name|int
name|pbParityIndex
parameter_list|,
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|,
name|int
name|pbIndex
parameter_list|)
block|{
name|ByteBuffer
name|fisrtValidInput
init|=
name|HHUtil
operator|.
name|findFirstValidInput
argument_list|(
name|inputs
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|int
name|bufSize
init|=
name|fisrtValidInput
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|ByteBuffer
name|piggybacks
init|=
name|allocateByteBuffer
argument_list|(
name|fisrtValidInput
operator|.
name|isDirect
argument_list|()
argument_list|,
name|bufSize
argument_list|)
decl_stmt|;
comment|// Use piggyBackParityIndex to figure out which parity location has the
comment|// associated piggyBack
comment|// Obtain the piggyback by subtracting the decoded (second sub-packet
comment|// only ) parity value from the actually read parity value
if|if
condition|(
name|pbParityIndex
operator|<
name|numParityUnits
condition|)
block|{
comment|// not the last piggybackSet
name|int
name|inputIdx
init|=
name|numDataUnits
operator|+
name|pbParityIndex
decl_stmt|;
name|int
name|inputPos
init|=
name|inputs
index|[
literal|1
index|]
index|[
name|inputIdx
index|]
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|outputPos
init|=
name|outputs
index|[
literal|1
index|]
index|[
name|pbParityIndex
index|]
operator|.
name|position
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|m
init|=
literal|0
init|,
name|k
init|=
name|inputPos
init|,
name|n
init|=
name|outputPos
init|;
name|m
operator|<
name|bufSize
condition|;
name|k
operator|++
operator|,
name|m
operator|++
operator|,
name|n
operator|++
control|)
block|{
name|int
name|valueWithPb
init|=
literal|0xFF
operator|&
name|inputs
index|[
literal|1
index|]
index|[
name|inputIdx
index|]
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|int
name|valueWithoutPb
init|=
literal|0xFF
operator|&
name|outputs
index|[
literal|1
index|]
index|[
name|pbParityIndex
index|]
operator|.
name|get
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|piggybacks
operator|.
name|put
argument_list|(
name|m
argument_list|,
operator|(
name|byte
operator|)
name|RSUtil
operator|.
name|GF
operator|.
name|add
argument_list|(
name|valueWithPb
argument_list|,
name|valueWithoutPb
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// last piggybackSet
name|int
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|bufSize
condition|;
name|k
operator|++
control|)
block|{
name|sum
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numParityUnits
condition|;
name|i
operator|++
control|)
block|{
name|int
name|inIdx
init|=
name|numDataUnits
operator|+
name|i
decl_stmt|;
name|int
name|inPos
init|=
name|inputs
index|[
literal|1
index|]
index|[
name|numDataUnits
operator|+
name|i
index|]
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|outPos
init|=
name|outputs
index|[
literal|1
index|]
index|[
name|i
index|]
operator|.
name|position
argument_list|()
decl_stmt|;
name|sum
operator|=
name|RSUtil
operator|.
name|GF
operator|.
name|add
argument_list|(
name|sum
argument_list|,
operator|(
literal|0xFF
operator|&
name|inputs
index|[
literal|1
index|]
index|[
name|inIdx
index|]
operator|.
name|get
argument_list|(
name|inPos
operator|+
name|k
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|sum
operator|=
name|RSUtil
operator|.
name|GF
operator|.
name|add
argument_list|(
name|sum
argument_list|,
operator|(
literal|0xFF
operator|&
name|outputs
index|[
literal|1
index|]
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|outPos
operator|+
name|k
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|sum
operator|=
name|RSUtil
operator|.
name|GF
operator|.
name|add
argument_list|(
name|sum
argument_list|,
operator|(
literal|0xFF
operator|&
name|inputs
index|[
literal|0
index|]
index|[
name|numDataUnits
operator|+
name|pbIndex
index|]
operator|.
name|get
argument_list|(
name|inputs
index|[
literal|0
index|]
index|[
name|numDataUnits
operator|+
name|pbIndex
index|]
operator|.
name|position
argument_list|()
operator|+
name|k
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|piggybacks
operator|.
name|put
argument_list|(
name|k
argument_list|,
operator|(
name|byte
operator|)
name|sum
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|piggybacks
return|;
block|}
comment|/**    * Find the valid input from all the inputs.    * @param inputs input buffers to look for valid input    * @return the first valid input    */
DECL|method|findFirstValidInput (T[] inputs)
specifier|public
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

