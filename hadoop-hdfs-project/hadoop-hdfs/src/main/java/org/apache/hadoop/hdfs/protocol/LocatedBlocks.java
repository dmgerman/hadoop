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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Collection of blocks with their locations and the file length.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|LocatedBlocks
specifier|public
class|class
name|LocatedBlocks
block|{
DECL|field|fileLength
specifier|private
specifier|final
name|long
name|fileLength
decl_stmt|;
DECL|field|blocks
specifier|private
specifier|final
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks
decl_stmt|;
comment|// array of blocks with prioritized locations
DECL|field|underConstruction
specifier|private
specifier|final
name|boolean
name|underConstruction
decl_stmt|;
DECL|field|lastLocatedBlock
specifier|private
name|LocatedBlock
name|lastLocatedBlock
init|=
literal|null
decl_stmt|;
DECL|field|isLastBlockComplete
specifier|private
name|boolean
name|isLastBlockComplete
init|=
literal|false
decl_stmt|;
DECL|method|LocatedBlocks ()
specifier|public
name|LocatedBlocks
parameter_list|()
block|{
name|fileLength
operator|=
literal|0
expr_stmt|;
name|blocks
operator|=
literal|null
expr_stmt|;
name|underConstruction
operator|=
literal|false
expr_stmt|;
block|}
comment|/** public Constructor */
DECL|method|LocatedBlocks (long flength, boolean isUnderConstuction, List<LocatedBlock> blks, LocatedBlock lastBlock, boolean isLastBlockCompleted)
specifier|public
name|LocatedBlocks
parameter_list|(
name|long
name|flength
parameter_list|,
name|boolean
name|isUnderConstuction
parameter_list|,
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blks
parameter_list|,
name|LocatedBlock
name|lastBlock
parameter_list|,
name|boolean
name|isLastBlockCompleted
parameter_list|)
block|{
name|fileLength
operator|=
name|flength
expr_stmt|;
name|blocks
operator|=
name|blks
expr_stmt|;
name|underConstruction
operator|=
name|isUnderConstuction
expr_stmt|;
name|this
operator|.
name|lastLocatedBlock
operator|=
name|lastBlock
expr_stmt|;
name|this
operator|.
name|isLastBlockComplete
operator|=
name|isLastBlockCompleted
expr_stmt|;
block|}
comment|/**    * Get located blocks.    */
DECL|method|getLocatedBlocks ()
specifier|public
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|getLocatedBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
comment|/** Get the last located block. */
DECL|method|getLastLocatedBlock ()
specifier|public
name|LocatedBlock
name|getLastLocatedBlock
parameter_list|()
block|{
return|return
name|lastLocatedBlock
return|;
block|}
comment|/** Is the last block completed? */
DECL|method|isLastBlockComplete ()
specifier|public
name|boolean
name|isLastBlockComplete
parameter_list|()
block|{
return|return
name|isLastBlockComplete
return|;
block|}
comment|/**    * Get located block.    */
DECL|method|get (int index)
specifier|public
name|LocatedBlock
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|blocks
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * Get number of located blocks.    */
DECL|method|locatedBlockCount ()
specifier|public
name|int
name|locatedBlockCount
parameter_list|()
block|{
return|return
name|blocks
operator|==
literal|null
condition|?
literal|0
else|:
name|blocks
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    *     */
DECL|method|getFileLength ()
specifier|public
name|long
name|getFileLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|fileLength
return|;
block|}
comment|/**    * Return ture if file was under construction when     * this LocatedBlocks was constructed, false otherwise.    */
DECL|method|isUnderConstruction ()
specifier|public
name|boolean
name|isUnderConstruction
parameter_list|()
block|{
return|return
name|underConstruction
return|;
block|}
comment|/**    * Find block containing specified offset.    *     * @return block if found, or null otherwise.    */
DECL|method|findBlock (long offset)
specifier|public
name|int
name|findBlock
parameter_list|(
name|long
name|offset
parameter_list|)
block|{
comment|// create fake block of size 0 as a key
name|LocatedBlock
name|key
init|=
operator|new
name|LocatedBlock
argument_list|(
operator|new
name|ExtendedBlock
argument_list|()
argument_list|,
operator|new
name|DatanodeInfo
index|[
literal|0
index|]
argument_list|,
literal|0L
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|key
operator|.
name|setStartOffset
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|key
operator|.
name|getBlock
argument_list|()
operator|.
name|setNumBytes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Comparator
argument_list|<
name|LocatedBlock
argument_list|>
name|comp
init|=
operator|new
name|Comparator
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|()
block|{
comment|// Returns 0 iff a is inside b or b is inside a
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|LocatedBlock
name|a
parameter_list|,
name|LocatedBlock
name|b
parameter_list|)
block|{
name|long
name|aBeg
init|=
name|a
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
name|long
name|bBeg
init|=
name|b
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
name|long
name|aEnd
init|=
name|aBeg
operator|+
name|a
operator|.
name|getBlockSize
argument_list|()
decl_stmt|;
name|long
name|bEnd
init|=
name|bBeg
operator|+
name|b
operator|.
name|getBlockSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|aBeg
operator|<=
name|bBeg
operator|&&
name|bEnd
operator|<=
name|aEnd
operator|||
name|bBeg
operator|<=
name|aBeg
operator|&&
name|aEnd
operator|<=
name|bEnd
condition|)
return|return
literal|0
return|;
comment|// one of the blocks is inside the other
if|if
condition|(
name|aBeg
operator|<
name|bBeg
condition|)
return|return
operator|-
literal|1
return|;
comment|// a's left bound is to the left of the b's
return|return
literal|1
return|;
block|}
block|}
decl_stmt|;
return|return
name|Collections
operator|.
name|binarySearch
argument_list|(
name|blocks
argument_list|,
name|key
argument_list|,
name|comp
argument_list|)
return|;
block|}
DECL|method|insertRange (int blockIdx, List<LocatedBlock> newBlocks)
specifier|public
name|void
name|insertRange
parameter_list|(
name|int
name|blockIdx
parameter_list|,
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|newBlocks
parameter_list|)
block|{
name|int
name|oldIdx
init|=
name|blockIdx
decl_stmt|;
name|int
name|insStart
init|=
literal|0
decl_stmt|,
name|insEnd
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|newIdx
init|=
literal|0
init|;
name|newIdx
operator|<
name|newBlocks
operator|.
name|size
argument_list|()
operator|&&
name|oldIdx
operator|<
name|blocks
operator|.
name|size
argument_list|()
condition|;
name|newIdx
operator|++
control|)
block|{
name|long
name|newOff
init|=
name|newBlocks
operator|.
name|get
argument_list|(
name|newIdx
argument_list|)
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
name|long
name|oldOff
init|=
name|blocks
operator|.
name|get
argument_list|(
name|oldIdx
argument_list|)
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|newOff
operator|<
name|oldOff
condition|)
block|{
name|insEnd
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|newOff
operator|==
name|oldOff
condition|)
block|{
comment|// replace old cached block by the new one
name|blocks
operator|.
name|set
argument_list|(
name|oldIdx
argument_list|,
name|newBlocks
operator|.
name|get
argument_list|(
name|newIdx
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|insStart
operator|<
name|insEnd
condition|)
block|{
comment|// insert new blocks
name|blocks
operator|.
name|addAll
argument_list|(
name|oldIdx
argument_list|,
name|newBlocks
operator|.
name|subList
argument_list|(
name|insStart
argument_list|,
name|insEnd
argument_list|)
argument_list|)
expr_stmt|;
name|oldIdx
operator|+=
name|insEnd
operator|-
name|insStart
expr_stmt|;
block|}
name|insStart
operator|=
name|insEnd
operator|=
name|newIdx
operator|+
literal|1
expr_stmt|;
name|oldIdx
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// newOff> oldOff
assert|assert
literal|false
operator|:
literal|"List of LocatedBlock must be sorted by startOffset"
assert|;
block|}
block|}
name|insEnd
operator|=
name|newBlocks
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|insStart
operator|<
name|insEnd
condition|)
block|{
comment|// insert new blocks
name|blocks
operator|.
name|addAll
argument_list|(
name|oldIdx
argument_list|,
name|newBlocks
operator|.
name|subList
argument_list|(
name|insStart
argument_list|,
name|insEnd
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getInsertIndex (int binSearchResult)
specifier|public
specifier|static
name|int
name|getInsertIndex
parameter_list|(
name|int
name|binSearchResult
parameter_list|)
block|{
return|return
name|binSearchResult
operator|>=
literal|0
condition|?
name|binSearchResult
else|:
operator|-
operator|(
name|binSearchResult
operator|+
literal|1
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n  fileLength="
argument_list|)
operator|.
name|append
argument_list|(
name|fileLength
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n  underConstruction="
argument_list|)
operator|.
name|append
argument_list|(
name|underConstruction
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n  blocks="
argument_list|)
operator|.
name|append
argument_list|(
name|blocks
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n  lastLocatedBlock="
argument_list|)
operator|.
name|append
argument_list|(
name|lastLocatedBlock
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n  isLastBlockComplete="
argument_list|)
operator|.
name|append
argument_list|(
name|isLastBlockComplete
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

