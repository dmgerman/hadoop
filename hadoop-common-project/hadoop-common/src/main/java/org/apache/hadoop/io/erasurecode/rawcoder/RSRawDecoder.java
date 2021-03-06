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
name|DumpUtil
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
name|GF256
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
comment|/**  * A raw erasure decoder in RS code scheme in pure Java in case native one  * isn't available in some environment. Please always use native implementations  * when possible. This new Java coder is about 5X faster than the one originated  * from HDFS-RAID, and also compatible with the native/ISA-L coder.  */
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
name|RawErasureDecoder
block|{
comment|//relevant to schema and won't change during decode calls
DECL|field|encodeMatrix
specifier|private
name|byte
index|[]
name|encodeMatrix
decl_stmt|;
comment|/**    * Below are relevant to schema and erased indexes, thus may change during    * decode calls.    */
DECL|field|decodeMatrix
specifier|private
name|byte
index|[]
name|decodeMatrix
decl_stmt|;
DECL|field|invertMatrix
specifier|private
name|byte
index|[]
name|invertMatrix
decl_stmt|;
comment|/**    * Array of input tables generated from coding coefficients previously.    * Must be of size 32*k*rows    */
DECL|field|gfTables
specifier|private
name|byte
index|[]
name|gfTables
decl_stmt|;
DECL|field|cachedErasedIndexes
specifier|private
name|int
index|[]
name|cachedErasedIndexes
decl_stmt|;
DECL|field|validIndexes
specifier|private
name|int
index|[]
name|validIndexes
decl_stmt|;
DECL|field|numErasedDataUnits
specifier|private
name|int
name|numErasedDataUnits
decl_stmt|;
DECL|field|erasureFlags
specifier|private
name|boolean
index|[]
name|erasureFlags
decl_stmt|;
DECL|method|RSRawDecoder (ErasureCoderOptions coderOptions)
specifier|public
name|RSRawDecoder
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
name|int
name|numAllUnits
init|=
name|getNumAllUnits
argument_list|()
decl_stmt|;
if|if
condition|(
name|getNumAllUnits
argument_list|()
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
literal|"Invalid getNumDataUnits() and numParityUnits"
argument_list|)
throw|;
block|}
name|encodeMatrix
operator|=
operator|new
name|byte
index|[
name|numAllUnits
operator|*
name|getNumDataUnits
argument_list|()
index|]
expr_stmt|;
name|RSUtil
operator|.
name|genCauchyMatrix
argument_list|(
name|encodeMatrix
argument_list|,
name|numAllUnits
argument_list|,
name|getNumDataUnits
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|allowVerboseDump
argument_list|()
condition|)
block|{
name|DumpUtil
operator|.
name|dumpMatrix
argument_list|(
name|encodeMatrix
argument_list|,
name|getNumDataUnits
argument_list|()
argument_list|,
name|numAllUnits
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doDecode (ByteBufferDecodingState decodingState)
specifier|protected
name|void
name|doDecode
parameter_list|(
name|ByteBufferDecodingState
name|decodingState
parameter_list|)
block|{
name|CoderUtil
operator|.
name|resetOutputBuffers
argument_list|(
name|decodingState
operator|.
name|outputs
argument_list|,
name|decodingState
operator|.
name|decodeLength
argument_list|)
expr_stmt|;
name|prepareDecoding
argument_list|(
name|decodingState
operator|.
name|inputs
argument_list|,
name|decodingState
operator|.
name|erasedIndexes
argument_list|)
expr_stmt|;
name|ByteBuffer
index|[]
name|realInputs
init|=
operator|new
name|ByteBuffer
index|[
name|getNumDataUnits
argument_list|()
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
name|getNumDataUnits
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|realInputs
index|[
name|i
index|]
operator|=
name|decodingState
operator|.
name|inputs
index|[
name|validIndexes
index|[
name|i
index|]
index|]
expr_stmt|;
block|}
name|RSUtil
operator|.
name|encodeData
argument_list|(
name|gfTables
argument_list|,
name|realInputs
argument_list|,
name|decodingState
operator|.
name|outputs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doDecode (ByteArrayDecodingState decodingState)
specifier|protected
name|void
name|doDecode
parameter_list|(
name|ByteArrayDecodingState
name|decodingState
parameter_list|)
block|{
name|int
name|dataLen
init|=
name|decodingState
operator|.
name|decodeLength
decl_stmt|;
name|CoderUtil
operator|.
name|resetOutputBuffers
argument_list|(
name|decodingState
operator|.
name|outputs
argument_list|,
name|decodingState
operator|.
name|outputOffsets
argument_list|,
name|dataLen
argument_list|)
expr_stmt|;
name|prepareDecoding
argument_list|(
name|decodingState
operator|.
name|inputs
argument_list|,
name|decodingState
operator|.
name|erasedIndexes
argument_list|)
expr_stmt|;
name|byte
index|[]
index|[]
name|realInputs
init|=
operator|new
name|byte
index|[
name|getNumDataUnits
argument_list|()
index|]
index|[]
decl_stmt|;
name|int
index|[]
name|realInputOffsets
init|=
operator|new
name|int
index|[
name|getNumDataUnits
argument_list|()
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
name|getNumDataUnits
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|realInputs
index|[
name|i
index|]
operator|=
name|decodingState
operator|.
name|inputs
index|[
name|validIndexes
index|[
name|i
index|]
index|]
expr_stmt|;
name|realInputOffsets
index|[
name|i
index|]
operator|=
name|decodingState
operator|.
name|inputOffsets
index|[
name|validIndexes
index|[
name|i
index|]
index|]
expr_stmt|;
block|}
name|RSUtil
operator|.
name|encodeData
argument_list|(
name|gfTables
argument_list|,
name|dataLen
argument_list|,
name|realInputs
argument_list|,
name|realInputOffsets
argument_list|,
name|decodingState
operator|.
name|outputs
argument_list|,
name|decodingState
operator|.
name|outputOffsets
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareDecoding (T[] inputs, int[] erasedIndexes)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|void
name|prepareDecoding
parameter_list|(
name|T
index|[]
name|inputs
parameter_list|,
name|int
index|[]
name|erasedIndexes
parameter_list|)
block|{
name|int
index|[]
name|tmpValidIndexes
init|=
name|CoderUtil
operator|.
name|getValidIndexes
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|cachedErasedIndexes
argument_list|,
name|erasedIndexes
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|validIndexes
argument_list|,
name|tmpValidIndexes
argument_list|)
condition|)
block|{
return|return;
comment|// Optimization. Nothing to do
block|}
name|this
operator|.
name|cachedErasedIndexes
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|erasedIndexes
argument_list|,
name|erasedIndexes
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|validIndexes
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|tmpValidIndexes
argument_list|,
name|tmpValidIndexes
operator|.
name|length
argument_list|)
expr_stmt|;
name|processErasures
argument_list|(
name|erasedIndexes
argument_list|)
expr_stmt|;
block|}
DECL|method|processErasures (int[] erasedIndexes)
specifier|private
name|void
name|processErasures
parameter_list|(
name|int
index|[]
name|erasedIndexes
parameter_list|)
block|{
name|this
operator|.
name|decodeMatrix
operator|=
operator|new
name|byte
index|[
name|getNumAllUnits
argument_list|()
operator|*
name|getNumDataUnits
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|invertMatrix
operator|=
operator|new
name|byte
index|[
name|getNumAllUnits
argument_list|()
operator|*
name|getNumDataUnits
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|gfTables
operator|=
operator|new
name|byte
index|[
name|getNumAllUnits
argument_list|()
operator|*
name|getNumDataUnits
argument_list|()
operator|*
literal|32
index|]
expr_stmt|;
name|this
operator|.
name|erasureFlags
operator|=
operator|new
name|boolean
index|[
name|getNumAllUnits
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|numErasedDataUnits
operator|=
literal|0
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
name|erasedIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
name|erasedIndexes
index|[
name|i
index|]
decl_stmt|;
name|erasureFlags
index|[
name|index
index|]
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|index
operator|<
name|getNumDataUnits
argument_list|()
condition|)
block|{
name|numErasedDataUnits
operator|++
expr_stmt|;
block|}
block|}
name|generateDecodeMatrix
argument_list|(
name|erasedIndexes
argument_list|)
expr_stmt|;
name|RSUtil
operator|.
name|initTables
argument_list|(
name|getNumDataUnits
argument_list|()
argument_list|,
name|erasedIndexes
operator|.
name|length
argument_list|,
name|decodeMatrix
argument_list|,
literal|0
argument_list|,
name|gfTables
argument_list|)
expr_stmt|;
if|if
condition|(
name|allowVerboseDump
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|DumpUtil
operator|.
name|bytesToHex
argument_list|(
name|gfTables
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Generate decode matrix from encode matrix
DECL|method|generateDecodeMatrix (int[] erasedIndexes)
specifier|private
name|void
name|generateDecodeMatrix
parameter_list|(
name|int
index|[]
name|erasedIndexes
parameter_list|)
block|{
name|int
name|i
decl_stmt|,
name|j
decl_stmt|,
name|r
decl_stmt|,
name|p
decl_stmt|;
name|byte
name|s
decl_stmt|;
name|byte
index|[]
name|tmpMatrix
init|=
operator|new
name|byte
index|[
name|getNumAllUnits
argument_list|()
operator|*
name|getNumDataUnits
argument_list|()
index|]
decl_stmt|;
comment|// Construct matrix tmpMatrix by removing error rows
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|getNumDataUnits
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|=
name|validIndexes
index|[
name|i
index|]
expr_stmt|;
for|for
control|(
name|j
operator|=
literal|0
init|;
name|j
operator|<
name|getNumDataUnits
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|tmpMatrix
index|[
name|getNumDataUnits
argument_list|()
operator|*
name|i
operator|+
name|j
index|]
operator|=
name|encodeMatrix
index|[
name|getNumDataUnits
argument_list|()
operator|*
name|r
operator|+
name|j
index|]
expr_stmt|;
block|}
block|}
name|GF256
operator|.
name|gfInvertMatrix
argument_list|(
name|tmpMatrix
argument_list|,
name|invertMatrix
argument_list|,
name|getNumDataUnits
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|numErasedDataUnits
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|j
operator|=
literal|0
init|;
name|j
operator|<
name|getNumDataUnits
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|decodeMatrix
index|[
name|getNumDataUnits
argument_list|()
operator|*
name|i
operator|+
name|j
index|]
operator|=
name|invertMatrix
index|[
name|getNumDataUnits
argument_list|()
operator|*
name|erasedIndexes
index|[
name|i
index|]
operator|+
name|j
index|]
expr_stmt|;
block|}
block|}
for|for
control|(
name|p
operator|=
name|numErasedDataUnits
init|;
name|p
operator|<
name|erasedIndexes
operator|.
name|length
condition|;
name|p
operator|++
control|)
block|{
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|getNumDataUnits
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|s
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|j
operator|=
literal|0
init|;
name|j
operator|<
name|getNumDataUnits
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|s
operator|^=
name|GF256
operator|.
name|gfMul
argument_list|(
name|invertMatrix
index|[
name|j
operator|*
name|getNumDataUnits
argument_list|()
operator|+
name|i
index|]
argument_list|,
name|encodeMatrix
index|[
name|getNumDataUnits
argument_list|()
operator|*
name|erasedIndexes
index|[
name|p
index|]
operator|+
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|decodeMatrix
index|[
name|getNumDataUnits
argument_list|()
operator|*
name|p
operator|+
name|i
index|]
operator|=
name|s
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

