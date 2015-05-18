begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode.coder
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
name|ECBlock
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
name|ECBlockGroup
import|;
end_import

begin_comment
comment|/**  * An abstract erasure decoder that's to be inherited by new decoders.  *  * It implements the {@link ErasureCoder} interface.  */
end_comment

begin_class
DECL|class|AbstractErasureDecoder
specifier|public
specifier|abstract
class|class
name|AbstractErasureDecoder
extends|extends
name|AbstractErasureCoder
block|{
annotation|@
name|Override
DECL|method|calculateCoding (ECBlockGroup blockGroup)
specifier|public
name|ErasureCodingStep
name|calculateCoding
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
comment|// We may have more than this when considering complicate cases. HADOOP-11550
return|return
name|prepareDecodingStep
argument_list|(
name|blockGroup
argument_list|)
return|;
block|}
comment|/**    * Perform decoding against a block blockGroup.    * @param blockGroup    * @return decoding step for caller to do the real work    */
DECL|method|prepareDecodingStep ( ECBlockGroup blockGroup)
specifier|protected
specifier|abstract
name|ErasureCodingStep
name|prepareDecodingStep
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
function_decl|;
comment|/**    * We have all the data blocks and parity blocks as input blocks for    * recovering by default. It's codec specific    * @param blockGroup    * @return    */
DECL|method|getInputBlocks (ECBlockGroup blockGroup)
specifier|protected
name|ECBlock
index|[]
name|getInputBlocks
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
name|ECBlock
index|[]
name|inputBlocks
init|=
operator|new
name|ECBlock
index|[
name|getNumParityUnits
argument_list|()
operator|+
name|getNumDataUnits
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blockGroup
operator|.
name|getParityBlocks
argument_list|()
argument_list|,
literal|0
argument_list|,
name|inputBlocks
argument_list|,
literal|0
argument_list|,
name|getNumParityUnits
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blockGroup
operator|.
name|getDataBlocks
argument_list|()
argument_list|,
literal|0
argument_list|,
name|inputBlocks
argument_list|,
name|getNumParityUnits
argument_list|()
argument_list|,
name|getNumDataUnits
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|inputBlocks
return|;
block|}
comment|/**    * Which blocks were erased ?    * @param blockGroup    * @return output blocks to recover    */
DECL|method|getOutputBlocks (ECBlockGroup blockGroup)
specifier|protected
name|ECBlock
index|[]
name|getOutputBlocks
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
name|ECBlock
index|[]
name|outputBlocks
init|=
operator|new
name|ECBlock
index|[
name|getNumErasedBlocks
argument_list|(
name|blockGroup
argument_list|)
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
name|getNumParityUnits
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|blockGroup
operator|.
name|getParityBlocks
argument_list|()
index|[
name|i
index|]
operator|.
name|isErased
argument_list|()
condition|)
block|{
name|outputBlocks
index|[
name|idx
operator|++
index|]
operator|=
name|blockGroup
operator|.
name|getParityBlocks
argument_list|()
index|[
name|i
index|]
expr_stmt|;
block|}
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
name|getNumDataUnits
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|blockGroup
operator|.
name|getDataBlocks
argument_list|()
index|[
name|i
index|]
operator|.
name|isErased
argument_list|()
condition|)
block|{
name|outputBlocks
index|[
name|idx
operator|++
index|]
operator|=
name|blockGroup
operator|.
name|getDataBlocks
argument_list|()
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|outputBlocks
return|;
block|}
comment|/**    * Get the number of erased blocks in the block group.    * @param blockGroup    * @return number of erased blocks    */
DECL|method|getNumErasedBlocks (ECBlockGroup blockGroup)
specifier|protected
name|int
name|getNumErasedBlocks
parameter_list|(
name|ECBlockGroup
name|blockGroup
parameter_list|)
block|{
name|int
name|num
init|=
name|getNumErasedBlocks
argument_list|(
name|blockGroup
operator|.
name|getParityBlocks
argument_list|()
argument_list|)
decl_stmt|;
name|num
operator|+=
name|getNumErasedBlocks
argument_list|(
name|blockGroup
operator|.
name|getDataBlocks
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|num
return|;
block|}
comment|/**    * Find out how many blocks are erased.    * @param inputBlocks all the input blocks    * @return number of erased blocks    */
DECL|method|getNumErasedBlocks (ECBlock[] inputBlocks)
specifier|protected
specifier|static
name|int
name|getNumErasedBlocks
parameter_list|(
name|ECBlock
index|[]
name|inputBlocks
parameter_list|)
block|{
name|int
name|numErased
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
name|inputBlocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|inputBlocks
index|[
name|i
index|]
operator|.
name|isErased
argument_list|()
condition|)
block|{
name|numErased
operator|++
expr_stmt|;
block|}
block|}
return|return
name|numErased
return|;
block|}
comment|/**    * Get indexes of erased blocks from inputBlocks    * @param inputBlocks    * @return indexes of erased blocks from inputBlocks    */
DECL|method|getErasedIndexes (ECBlock[] inputBlocks)
specifier|protected
name|int
index|[]
name|getErasedIndexes
parameter_list|(
name|ECBlock
index|[]
name|inputBlocks
parameter_list|)
block|{
name|int
name|numErased
init|=
name|getNumErasedBlocks
argument_list|(
name|inputBlocks
argument_list|)
decl_stmt|;
if|if
condition|(
name|numErased
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|int
index|[
literal|0
index|]
return|;
block|}
name|int
index|[]
name|erasedIndexes
init|=
operator|new
name|int
index|[
name|numErased
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|,
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|inputBlocks
operator|.
name|length
operator|&&
name|j
operator|<
name|erasedIndexes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|inputBlocks
index|[
name|i
index|]
operator|.
name|isErased
argument_list|()
condition|)
block|{
name|erasedIndexes
index|[
name|j
operator|++
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
return|return
name|erasedIndexes
return|;
block|}
comment|/**    * Get erased input blocks from inputBlocks    * @param inputBlocks    * @return an array of erased blocks from inputBlocks    */
DECL|method|getErasedBlocks (ECBlock[] inputBlocks)
specifier|protected
name|ECBlock
index|[]
name|getErasedBlocks
parameter_list|(
name|ECBlock
index|[]
name|inputBlocks
parameter_list|)
block|{
name|int
name|numErased
init|=
name|getNumErasedBlocks
argument_list|(
name|inputBlocks
argument_list|)
decl_stmt|;
if|if
condition|(
name|numErased
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|ECBlock
index|[
literal|0
index|]
return|;
block|}
name|ECBlock
index|[]
name|erasedBlocks
init|=
operator|new
name|ECBlock
index|[
name|numErased
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|,
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|inputBlocks
operator|.
name|length
operator|&&
name|j
operator|<
name|erasedBlocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|inputBlocks
index|[
name|i
index|]
operator|.
name|isErased
argument_list|()
condition|)
block|{
name|erasedBlocks
index|[
name|j
operator|++
index|]
operator|=
name|inputBlocks
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|erasedBlocks
return|;
block|}
block|}
end_class

end_unit

