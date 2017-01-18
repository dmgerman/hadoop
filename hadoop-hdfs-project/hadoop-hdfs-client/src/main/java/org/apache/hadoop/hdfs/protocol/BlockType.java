begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_comment
comment|/**  * Type of a block. Previously, all blocks were replicated (contiguous).  * Then Erasure Coded blocks (striped) were implemented.  *  * BlockTypes are currently defined by the highest bit in the block id. If  * this bit is set, then the block is striped.  *  * Further extensions may claim the second bit s.t. the highest two bits are  * set. e.g.  * 0b00 == contiguous  * 0b10 == striped  * 0b11 == possible further extension block type.  */
end_comment

begin_enum
DECL|enum|BlockType
specifier|public
enum|enum
name|BlockType
block|{
comment|//! Replicated block.
DECL|enumConstant|CONTIGUOUS
name|CONTIGUOUS
block|,
comment|//! Erasure Coded Block
DECL|enumConstant|STRIPED
name|STRIPED
block|;
comment|// BLOCK_ID_MASK is the union of all masks.
DECL|field|BLOCK_ID_MASK
specifier|static
specifier|final
name|long
name|BLOCK_ID_MASK
init|=
literal|1L
operator|<<
literal|63
decl_stmt|;
comment|// BLOCK_ID_MASK_STRIPED is the mask for striped blocks.
DECL|field|BLOCK_ID_MASK_STRIPED
specifier|static
specifier|final
name|long
name|BLOCK_ID_MASK_STRIPED
init|=
literal|1L
operator|<<
literal|63
decl_stmt|;
comment|/**    * Parse a BlockId to find the BlockType    * Note: the old block id generation algorithm was based on a pseudo random    * number generator, so there may be legacy blocks that make this conversion    * unreliable.    */
DECL|method|fromBlockId (long blockId)
specifier|public
specifier|static
name|BlockType
name|fromBlockId
parameter_list|(
name|long
name|blockId
parameter_list|)
block|{
name|long
name|blockType
init|=
name|blockId
operator|&
name|BLOCK_ID_MASK
decl_stmt|;
if|if
condition|(
name|blockType
operator|==
name|BLOCK_ID_MASK_STRIPED
condition|)
block|{
return|return
name|STRIPED
return|;
block|}
return|return
name|CONTIGUOUS
return|;
block|}
block|}
end_enum

end_unit

