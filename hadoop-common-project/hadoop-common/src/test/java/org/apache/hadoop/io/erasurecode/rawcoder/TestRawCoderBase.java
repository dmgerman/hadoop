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
name|TestCoderBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_comment
comment|/**  * Raw coder test base with utilities.  */
end_comment

begin_class
DECL|class|TestRawCoderBase
specifier|public
specifier|abstract
class|class
name|TestRawCoderBase
extends|extends
name|TestCoderBase
block|{
DECL|field|encoderClass
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|RawErasureEncoder
argument_list|>
name|encoderClass
decl_stmt|;
DECL|field|decoderClass
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|RawErasureDecoder
argument_list|>
name|decoderClass
decl_stmt|;
DECL|field|encoder
specifier|private
name|RawErasureEncoder
name|encoder
decl_stmt|;
DECL|field|decoder
specifier|private
name|RawErasureDecoder
name|decoder
decl_stmt|;
comment|/**    * Doing twice to test if the coders can be repeatedly reused. This matters    * as the underlying coding buffers are shared, which may have bugs.    */
DECL|method|testCodingDoMixAndTwice ()
specifier|protected
name|void
name|testCodingDoMixAndTwice
parameter_list|()
block|{
name|testCodingDoMixed
argument_list|()
expr_stmt|;
name|testCodingDoMixed
argument_list|()
expr_stmt|;
block|}
comment|/**    * Doing in mixed buffer usage model to test if the coders can be repeatedly    * reused with different buffer usage model. This matters as the underlying    * coding buffers are shared, which may have bugs.    */
DECL|method|testCodingDoMixed ()
specifier|protected
name|void
name|testCodingDoMixed
parameter_list|()
block|{
name|testCoding
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|testCoding
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generating source data, encoding, recovering and then verifying.    * RawErasureCoder mainly uses ECChunk to pass input and output data buffers,    * it supports two kinds of ByteBuffers, one is array backed, the other is    * direct ByteBuffer. Use usingDirectBuffer indicate which case to test.    *    * @param usingDirectBuffer    */
DECL|method|testCoding (boolean usingDirectBuffer)
specifier|protected
name|void
name|testCoding
parameter_list|(
name|boolean
name|usingDirectBuffer
parameter_list|)
block|{
name|this
operator|.
name|usingDirectBuffer
operator|=
name|usingDirectBuffer
expr_stmt|;
name|prepareCoders
argument_list|()
expr_stmt|;
comment|/**      * The following runs will use 3 different chunkSize for inputs and outputs,      * to verify the same encoder/decoder can process variable width of data.      */
name|performTestCoding
argument_list|(
name|baseChunkSize
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|performTestCoding
argument_list|(
name|baseChunkSize
operator|-
literal|17
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|performTestCoding
argument_list|(
name|baseChunkSize
operator|+
literal|16
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Similar to above, but perform negative cases using bad input for encoding.    * @param usingDirectBuffer    */
DECL|method|testCodingWithBadInput (boolean usingDirectBuffer)
specifier|protected
name|void
name|testCodingWithBadInput
parameter_list|(
name|boolean
name|usingDirectBuffer
parameter_list|)
block|{
name|this
operator|.
name|usingDirectBuffer
operator|=
name|usingDirectBuffer
expr_stmt|;
name|prepareCoders
argument_list|()
expr_stmt|;
try|try
block|{
name|performTestCoding
argument_list|(
name|baseChunkSize
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Encoding test with bad input should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
comment|/**    * Similar to above, but perform negative cases using bad output for decoding.    * @param usingDirectBuffer    */
DECL|method|testCodingWithBadOutput (boolean usingDirectBuffer)
specifier|protected
name|void
name|testCodingWithBadOutput
parameter_list|(
name|boolean
name|usingDirectBuffer
parameter_list|)
block|{
name|this
operator|.
name|usingDirectBuffer
operator|=
name|usingDirectBuffer
expr_stmt|;
name|prepareCoders
argument_list|()
expr_stmt|;
try|try
block|{
name|performTestCoding
argument_list|(
name|baseChunkSize
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Decoding test with bad output should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
annotation|@
name|Test
DECL|method|testCodingWithErasingTooMany ()
specifier|public
name|void
name|testCodingWithErasingTooMany
parameter_list|()
block|{
try|try
block|{
name|testCoding
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Decoding test erasing too many should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|testCoding
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Decoding test erasing too many should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Expected
block|}
block|}
DECL|method|performTestCoding (int chunkSize, boolean useBadInput, boolean useBadOutput)
specifier|private
name|void
name|performTestCoding
parameter_list|(
name|int
name|chunkSize
parameter_list|,
name|boolean
name|useBadInput
parameter_list|,
name|boolean
name|useBadOutput
parameter_list|)
block|{
name|setChunkSize
argument_list|(
name|chunkSize
argument_list|)
expr_stmt|;
comment|// Generate data and encode
name|ECChunk
index|[]
name|dataChunks
init|=
name|prepareDataChunksForEncoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|useBadInput
condition|)
block|{
name|corruptSomeChunk
argument_list|(
name|dataChunks
argument_list|)
expr_stmt|;
block|}
name|ECChunk
index|[]
name|parityChunks
init|=
name|prepareParityChunksForEncoding
argument_list|()
decl_stmt|;
comment|// Backup all the source chunks for later recovering because some coders
comment|// may affect the source data.
name|ECChunk
index|[]
name|clonedDataChunks
init|=
name|cloneChunksWithData
argument_list|(
name|dataChunks
argument_list|)
decl_stmt|;
name|encoder
operator|.
name|encode
argument_list|(
name|dataChunks
argument_list|,
name|parityChunks
argument_list|)
expr_stmt|;
comment|// Backup and erase some chunks
name|ECChunk
index|[]
name|backupChunks
init|=
name|backupAndEraseChunks
argument_list|(
name|clonedDataChunks
argument_list|,
name|parityChunks
argument_list|)
decl_stmt|;
comment|// Decode
name|ECChunk
index|[]
name|inputChunks
init|=
name|prepareInputChunksForDecoding
argument_list|(
name|clonedDataChunks
argument_list|,
name|parityChunks
argument_list|)
decl_stmt|;
comment|// Remove unnecessary chunks, allowing only least required chunks to be read.
name|ensureOnlyLeastRequiredChunks
argument_list|(
name|inputChunks
argument_list|)
expr_stmt|;
name|ECChunk
index|[]
name|recoveredChunks
init|=
name|prepareOutputChunksForDecoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|useBadOutput
condition|)
block|{
name|corruptSomeChunk
argument_list|(
name|recoveredChunks
argument_list|)
expr_stmt|;
block|}
name|decoder
operator|.
name|decode
argument_list|(
name|inputChunks
argument_list|,
name|getErasedIndexesForDecoding
argument_list|()
argument_list|,
name|recoveredChunks
argument_list|)
expr_stmt|;
comment|// Compare
name|compareAndVerify
argument_list|(
name|backupChunks
argument_list|,
name|recoveredChunks
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareCoders ()
specifier|private
name|void
name|prepareCoders
parameter_list|()
block|{
if|if
condition|(
name|encoder
operator|==
literal|null
condition|)
block|{
name|encoder
operator|=
name|createEncoder
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|decoder
operator|==
literal|null
condition|)
block|{
name|decoder
operator|=
name|createDecoder
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|ensureOnlyLeastRequiredChunks (ECChunk[] inputChunks)
specifier|private
name|void
name|ensureOnlyLeastRequiredChunks
parameter_list|(
name|ECChunk
index|[]
name|inputChunks
parameter_list|)
block|{
name|int
name|leastRequiredNum
init|=
name|numDataUnits
decl_stmt|;
name|int
name|erasedNum
init|=
name|erasedDataIndexes
operator|.
name|length
operator|+
name|erasedParityIndexes
operator|.
name|length
decl_stmt|;
name|int
name|goodNum
init|=
name|inputChunks
operator|.
name|length
operator|-
name|erasedNum
decl_stmt|;
name|int
name|redundantNum
init|=
name|goodNum
operator|-
name|leastRequiredNum
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
argument_list|<
name|inputChunks
operator|.
name|length
operator|&&
name|redundantNum
argument_list|>
literal|0
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|inputChunks
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|inputChunks
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
comment|// Setting it null, not needing it actually
name|redundantNum
operator|--
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create the raw erasure encoder to test    * @return    */
DECL|method|createEncoder ()
specifier|protected
name|RawErasureEncoder
name|createEncoder
parameter_list|()
block|{
name|RawErasureEncoder
name|encoder
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|RawErasureEncoder
argument_list|>
name|constructor
init|=
operator|(
name|Constructor
argument_list|<
name|?
extends|extends
name|RawErasureEncoder
argument_list|>
operator|)
name|encoderClass
operator|.
name|getConstructor
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
name|encoder
operator|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|numDataUnits
argument_list|,
name|numParityUnits
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create encoder"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|encoder
operator|.
name|setConf
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|encoder
return|;
block|}
comment|/**    * create the raw erasure decoder to test    * @return    */
DECL|method|createDecoder ()
specifier|protected
name|RawErasureDecoder
name|createDecoder
parameter_list|()
block|{
name|RawErasureDecoder
name|decoder
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|RawErasureDecoder
argument_list|>
name|constructor
init|=
operator|(
name|Constructor
argument_list|<
name|?
extends|extends
name|RawErasureDecoder
argument_list|>
operator|)
name|decoderClass
operator|.
name|getConstructor
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
name|decoder
operator|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|numDataUnits
argument_list|,
name|numParityUnits
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create decoder"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|decoder
operator|.
name|setConf
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|decoder
return|;
block|}
block|}
end_class

end_unit

