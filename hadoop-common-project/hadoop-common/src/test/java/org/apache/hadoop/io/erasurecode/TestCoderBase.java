begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test base of common utilities for tests not only raw coders but also block  * coders.  */
end_comment

begin_class
DECL|class|TestCoderBase
specifier|public
specifier|abstract
class|class
name|TestCoderBase
block|{
DECL|field|RAND
specifier|protected
specifier|static
name|Random
name|RAND
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|numDataUnits
specifier|protected
name|int
name|numDataUnits
decl_stmt|;
DECL|field|numParityUnits
specifier|protected
name|int
name|numParityUnits
decl_stmt|;
DECL|field|chunkSize
specifier|protected
name|int
name|chunkSize
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
comment|// Indexes of erased data units. Will also support test of erasing
comment|// parity units
DECL|field|erasedDataIndexes
specifier|protected
name|int
index|[]
name|erasedDataIndexes
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|}
decl_stmt|;
comment|// Data buffers are either direct or on-heap, for performance the two cases
comment|// may go to different coding implementations.
DECL|field|usingDirectBuffer
specifier|protected
name|boolean
name|usingDirectBuffer
init|=
literal|true
decl_stmt|;
DECL|method|prepare (int numDataUnits, int numParityUnits, int[] erasedIndexes)
specifier|protected
name|void
name|prepare
parameter_list|(
name|int
name|numDataUnits
parameter_list|,
name|int
name|numParityUnits
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|)
block|{
name|this
operator|.
name|numDataUnits
operator|=
name|numDataUnits
expr_stmt|;
name|this
operator|.
name|numParityUnits
operator|=
name|numParityUnits
expr_stmt|;
name|this
operator|.
name|erasedDataIndexes
operator|=
name|erasedIndexes
operator|!=
literal|null
condition|?
name|erasedIndexes
else|:
operator|new
name|int
index|[]
block|{
literal|0
block|}
expr_stmt|;
block|}
comment|/**    * Compare and verify if erased chunks are equal to recovered chunks    * @param erasedChunks    * @param recoveredChunks    */
DECL|method|compareAndVerify (ECChunk[] erasedChunks, ECChunk[] recoveredChunks)
specifier|protected
name|void
name|compareAndVerify
parameter_list|(
name|ECChunk
index|[]
name|erasedChunks
parameter_list|,
name|ECChunk
index|[]
name|recoveredChunks
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|erased
init|=
name|ECChunk
operator|.
name|toArray
argument_list|(
name|erasedChunks
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|recovered
init|=
name|ECChunk
operator|.
name|toArray
argument_list|(
name|recoveredChunks
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|Arrays
operator|.
name|deepEquals
argument_list|(
name|erased
argument_list|,
name|recovered
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Decoding and comparing failed."
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adjust and return erased indexes based on the array of the input chunks (    * parity chunks + data chunks).    * @return    */
DECL|method|getErasedIndexesForDecoding ()
specifier|protected
name|int
index|[]
name|getErasedIndexesForDecoding
parameter_list|()
block|{
name|int
index|[]
name|erasedIndexesForDecoding
init|=
operator|new
name|int
index|[
name|erasedDataIndexes
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
name|erasedDataIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|erasedIndexesForDecoding
index|[
name|i
index|]
operator|=
name|erasedDataIndexes
index|[
name|i
index|]
operator|+
name|numParityUnits
expr_stmt|;
block|}
return|return
name|erasedIndexesForDecoding
return|;
block|}
comment|/**    * Return input chunks for decoding, which is parityChunks + dataChunks.    * @param dataChunks    * @param parityChunks    * @return    */
DECL|method|prepareInputChunksForDecoding (ECChunk[] dataChunks, ECChunk[] parityChunks)
specifier|protected
name|ECChunk
index|[]
name|prepareInputChunksForDecoding
parameter_list|(
name|ECChunk
index|[]
name|dataChunks
parameter_list|,
name|ECChunk
index|[]
name|parityChunks
parameter_list|)
block|{
name|ECChunk
index|[]
name|inputChunks
init|=
operator|new
name|ECChunk
index|[
name|numParityUnits
operator|+
name|numDataUnits
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
name|numParityUnits
condition|;
name|i
operator|++
control|)
block|{
name|inputChunks
index|[
name|idx
operator|++
index|]
operator|=
name|parityChunks
index|[
name|i
index|]
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
name|numDataUnits
condition|;
name|i
operator|++
control|)
block|{
name|inputChunks
index|[
name|idx
operator|++
index|]
operator|=
name|dataChunks
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|inputChunks
return|;
block|}
comment|/**    * Have a copy of the data chunks that's to be erased thereafter. The copy    * will be used to compare and verify with the to be recovered chunks.    * @param dataChunks    * @return    */
DECL|method|copyDataChunksToErase (ECChunk[] dataChunks)
specifier|protected
name|ECChunk
index|[]
name|copyDataChunksToErase
parameter_list|(
name|ECChunk
index|[]
name|dataChunks
parameter_list|)
block|{
name|ECChunk
index|[]
name|copiedChunks
init|=
operator|new
name|ECChunk
index|[
name|erasedDataIndexes
operator|.
name|length
index|]
decl_stmt|;
name|int
name|j
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
name|erasedDataIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|copiedChunks
index|[
name|j
operator|++
index|]
operator|=
name|cloneChunkWithData
argument_list|(
name|dataChunks
index|[
name|erasedDataIndexes
index|[
name|i
index|]
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|copiedChunks
return|;
block|}
comment|/**    * Erase some data chunks to test the recovering of them    * @param dataChunks    */
DECL|method|eraseSomeDataBlocks (ECChunk[] dataChunks)
specifier|protected
name|void
name|eraseSomeDataBlocks
parameter_list|(
name|ECChunk
index|[]
name|dataChunks
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
name|erasedDataIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|eraseDataFromChunk
argument_list|(
name|dataChunks
index|[
name|erasedDataIndexes
index|[
name|i
index|]
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Erase data from the specified chunks, putting ZERO bytes to the buffers.    * @param chunks    */
DECL|method|eraseDataFromChunks (ECChunk[] chunks)
specifier|protected
name|void
name|eraseDataFromChunks
parameter_list|(
name|ECChunk
index|[]
name|chunks
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
name|chunks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|eraseDataFromChunk
argument_list|(
name|chunks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Erase data from the specified chunk, putting ZERO bytes to the buffer.    * @param chunk    */
DECL|method|eraseDataFromChunk (ECChunk chunk)
specifier|protected
name|void
name|eraseDataFromChunk
parameter_list|(
name|ECChunk
name|chunk
parameter_list|)
block|{
name|ByteBuffer
name|chunkBuffer
init|=
name|chunk
operator|.
name|getBuffer
argument_list|()
decl_stmt|;
comment|// erase the data
name|chunkBuffer
operator|.
name|position
argument_list|(
literal|0
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
name|chunkSize
condition|;
name|i
operator|++
control|)
block|{
name|chunkBuffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
name|chunkBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
comment|/**    * Clone chunks along with copying the associated data. It respects how the    * chunk buffer is allocated, direct or non-direct. It avoids affecting the    * original chunk buffers.    * @param chunks    * @return    */
DECL|method|cloneChunksWithData (ECChunk[] chunks)
specifier|protected
specifier|static
name|ECChunk
index|[]
name|cloneChunksWithData
parameter_list|(
name|ECChunk
index|[]
name|chunks
parameter_list|)
block|{
name|ECChunk
index|[]
name|results
init|=
operator|new
name|ECChunk
index|[
name|chunks
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
name|chunks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|results
index|[
name|i
index|]
operator|=
name|cloneChunkWithData
argument_list|(
name|chunks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
comment|/**    * Clone chunk along with copying the associated data. It respects how the    * chunk buffer is allocated, direct or non-direct. It avoids affecting the    * original chunk.    * @param chunk    * @return a new chunk    */
DECL|method|cloneChunkWithData (ECChunk chunk)
specifier|protected
specifier|static
name|ECChunk
name|cloneChunkWithData
parameter_list|(
name|ECChunk
name|chunk
parameter_list|)
block|{
name|ByteBuffer
name|srcBuffer
init|=
name|chunk
operator|.
name|getBuffer
argument_list|()
decl_stmt|;
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
name|srcBuffer
operator|.
name|hasArray
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
operator|new
name|ECChunk
argument_list|(
name|destBuffer
argument_list|)
return|;
block|}
comment|/**    * Allocate a chunk for output or writing.    * @return    */
DECL|method|allocateOutputChunk ()
specifier|protected
name|ECChunk
name|allocateOutputChunk
parameter_list|()
block|{
name|ByteBuffer
name|buffer
init|=
name|allocateOutputBuffer
argument_list|()
decl_stmt|;
return|return
operator|new
name|ECChunk
argument_list|(
name|buffer
argument_list|)
return|;
block|}
comment|/**    * Allocate a buffer for output or writing.    * @return    */
DECL|method|allocateOutputBuffer ()
specifier|protected
name|ByteBuffer
name|allocateOutputBuffer
parameter_list|()
block|{
name|ByteBuffer
name|buffer
init|=
name|usingDirectBuffer
condition|?
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|chunkSize
argument_list|)
else|:
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|chunkSize
argument_list|)
decl_stmt|;
return|return
name|buffer
return|;
block|}
comment|/**    * Prepare data chunks for each data unit, by generating random data.    * @return    */
DECL|method|prepareDataChunksForEncoding ()
specifier|protected
name|ECChunk
index|[]
name|prepareDataChunksForEncoding
parameter_list|()
block|{
name|ECChunk
index|[]
name|chunks
init|=
operator|new
name|ECChunk
index|[
name|numDataUnits
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
name|chunks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|chunks
index|[
name|i
index|]
operator|=
name|generateDataChunk
argument_list|()
expr_stmt|;
block|}
return|return
name|chunks
return|;
block|}
comment|/**    * Generate data chunk by making random data.    * @return    */
DECL|method|generateDataChunk ()
specifier|protected
name|ECChunk
name|generateDataChunk
parameter_list|()
block|{
name|ByteBuffer
name|buffer
init|=
name|allocateOutputBuffer
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
name|chunkSize
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|RAND
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|flip
argument_list|()
expr_stmt|;
return|return
operator|new
name|ECChunk
argument_list|(
name|buffer
argument_list|)
return|;
block|}
comment|/**    * Prepare parity chunks for encoding, each chunk for each parity unit.    * @return    */
DECL|method|prepareParityChunksForEncoding ()
specifier|protected
name|ECChunk
index|[]
name|prepareParityChunksForEncoding
parameter_list|()
block|{
name|ECChunk
index|[]
name|chunks
init|=
operator|new
name|ECChunk
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
name|chunks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|chunks
index|[
name|i
index|]
operator|=
name|allocateOutputChunk
argument_list|()
expr_stmt|;
block|}
return|return
name|chunks
return|;
block|}
comment|/**    * Prepare output chunks for decoding, each output chunk for each erased    * chunk.    * @return    */
DECL|method|prepareOutputChunksForDecoding ()
specifier|protected
name|ECChunk
index|[]
name|prepareOutputChunksForDecoding
parameter_list|()
block|{
name|ECChunk
index|[]
name|chunks
init|=
operator|new
name|ECChunk
index|[
name|erasedDataIndexes
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
name|chunks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|chunks
index|[
name|i
index|]
operator|=
name|allocateOutputChunk
argument_list|()
expr_stmt|;
block|}
return|return
name|chunks
return|;
block|}
block|}
end_class

end_unit

