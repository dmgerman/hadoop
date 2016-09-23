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
comment|/**  * Helpful utilities for implementing some raw erasure coders.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CoderUtil
specifier|final
class|class
name|CoderUtil
block|{
DECL|method|CoderUtil ()
specifier|private
name|CoderUtil
parameter_list|()
block|{
comment|// No called
block|}
DECL|field|emptyChunk
specifier|private
specifier|static
name|byte
index|[]
name|emptyChunk
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
comment|/**    * Make sure to return an empty chunk buffer for the desired length.    * @param leastLength    * @return empty chunk of zero bytes    */
DECL|method|getEmptyChunk (int leastLength)
specifier|static
name|byte
index|[]
name|getEmptyChunk
parameter_list|(
name|int
name|leastLength
parameter_list|)
block|{
if|if
condition|(
name|emptyChunk
operator|.
name|length
operator|>=
name|leastLength
condition|)
block|{
return|return
name|emptyChunk
return|;
comment|// In most time
block|}
synchronized|synchronized
init|(
name|CoderUtil
operator|.
name|class
init|)
block|{
name|emptyChunk
operator|=
operator|new
name|byte
index|[
name|leastLength
index|]
expr_stmt|;
block|}
return|return
name|emptyChunk
return|;
block|}
comment|/**    * Ensure a buffer filled with ZERO bytes from current readable/writable    * position.    * @param buffer a buffer ready to read / write certain size bytes    * @return the buffer itself, with ZERO bytes written, the position and limit    *         are not changed after the call    */
DECL|method|resetBuffer (ByteBuffer buffer, int len)
specifier|static
name|ByteBuffer
name|resetBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|pos
init|=
name|buffer
operator|.
name|position
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|put
argument_list|(
name|getEmptyChunk
argument_list|(
name|len
argument_list|)
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|position
argument_list|(
name|pos
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
comment|/**    * Ensure the buffer (either input or output) ready to read or write with ZERO    * bytes fully in specified length of len.    * @param buffer bytes array buffer    * @return the buffer itself    */
DECL|method|resetBuffer (byte[] buffer, int offset, int len)
specifier|static
name|byte
index|[]
name|resetBuffer
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|byte
index|[]
name|empty
init|=
name|getEmptyChunk
argument_list|(
name|len
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|empty
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
comment|/**    * Initialize the output buffers with ZERO bytes.    */
DECL|method|resetOutputBuffers (ByteBuffer[] buffers, int dataLen)
specifier|static
name|void
name|resetOutputBuffers
parameter_list|(
name|ByteBuffer
index|[]
name|buffers
parameter_list|,
name|int
name|dataLen
parameter_list|)
block|{
for|for
control|(
name|ByteBuffer
name|buffer
range|:
name|buffers
control|)
block|{
name|resetBuffer
argument_list|(
name|buffer
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Initialize the output buffers with ZERO bytes.    */
DECL|method|resetOutputBuffers (byte[][] buffers, int[] offsets, int dataLen)
specifier|static
name|void
name|resetOutputBuffers
parameter_list|(
name|byte
index|[]
index|[]
name|buffers
parameter_list|,
name|int
index|[]
name|offsets
parameter_list|,
name|int
name|dataLen
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
name|buffers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|resetBuffer
argument_list|(
name|buffers
index|[
name|i
index|]
argument_list|,
name|offsets
index|[
name|i
index|]
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convert an array of this chunks to an array of ByteBuffers    * @param chunks chunks to convertToByteArrayState into buffers    * @return an array of ByteBuffers    */
DECL|method|toBuffers (ECChunk[] chunks)
specifier|static
name|ByteBuffer
index|[]
name|toBuffers
parameter_list|(
name|ECChunk
index|[]
name|chunks
parameter_list|)
block|{
name|ByteBuffer
index|[]
name|buffers
init|=
operator|new
name|ByteBuffer
index|[
name|chunks
operator|.
name|length
index|]
decl_stmt|;
name|ECChunk
name|chunk
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
name|chunks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|chunk
operator|=
name|chunks
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|chunk
operator|==
literal|null
condition|)
block|{
name|buffers
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|buffers
index|[
name|i
index|]
operator|=
name|chunk
operator|.
name|getBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|chunk
operator|.
name|isAllZero
argument_list|()
condition|)
block|{
name|CoderUtil
operator|.
name|resetBuffer
argument_list|(
name|buffers
index|[
name|i
index|]
argument_list|,
name|buffers
index|[
name|i
index|]
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|buffers
return|;
block|}
comment|/**    * Clone an input bytes array as direct ByteBuffer.    */
DECL|method|cloneAsDirectByteBuffer (byte[] input, int offset, int len)
specifier|static
name|ByteBuffer
name|cloneAsDirectByteBuffer
parameter_list|(
name|byte
index|[]
name|input
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
comment|// an input can be null, if erased or not to read
return|return
literal|null
return|;
block|}
name|ByteBuffer
name|directBuffer
init|=
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|len
argument_list|)
decl_stmt|;
name|directBuffer
operator|.
name|put
argument_list|(
name|input
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|directBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
return|return
name|directBuffer
return|;
block|}
comment|/**    * Get indexes array for items marked as null, either erased or    * not to read.    * @return indexes array    */
DECL|method|getNullIndexes (T[] inputs)
specifier|static
parameter_list|<
name|T
parameter_list|>
name|int
index|[]
name|getNullIndexes
parameter_list|(
name|T
index|[]
name|inputs
parameter_list|)
block|{
name|int
index|[]
name|nullIndexes
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
name|nullIndexes
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
name|nullIndexes
argument_list|,
name|idx
argument_list|)
return|;
block|}
comment|/**    * Find the valid input from all the inputs.    * @param inputs input buffers to look for valid input    * @return the first valid input    */
DECL|method|findFirstValidInput (T[] inputs)
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
comment|/**    * Picking up indexes of valid inputs.    * @param inputs decoding input buffers    * @param<T>    */
DECL|method|getValidIndexes (T[] inputs)
specifier|static
parameter_list|<
name|T
parameter_list|>
name|int
index|[]
name|getValidIndexes
parameter_list|(
name|T
index|[]
name|inputs
parameter_list|)
block|{
name|int
index|[]
name|validIndexes
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
operator|!=
literal|null
condition|)
block|{
name|validIndexes
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
name|validIndexes
argument_list|,
name|idx
argument_list|)
return|;
block|}
block|}
end_class

end_unit

