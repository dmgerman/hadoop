begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
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
name|hdfs
operator|.
name|protocol
operator|.
name|Block
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|BlockUCState
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
name|hdfs
operator|.
name|util
operator|.
name|StripedBlockUtil
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
name|ECSchema
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
import|;
end_import

begin_comment
comment|/**  * Subclass of {@link BlockInfo}, presenting a block group in erasure coding.  *  * We still use triplets to store DatanodeStorageInfo for each block in the  * block group, as well as the previous/next block in the corresponding  * DatanodeStorageInfo. For a (m+k) block group, the first (m+k) triplet units  * are sorted and strictly mapped to the corresponding block.  *  * Normally each block belonging to group is stored in only one DataNode.  * However, it is possible that some block is over-replicated. Thus the triplet  * array's size can be larger than (m+k). Thus currently we use an extra byte  * array to record the block index for each triplet.  */
end_comment

begin_class
DECL|class|BlockInfoStriped
specifier|public
class|class
name|BlockInfoStriped
extends|extends
name|BlockInfo
block|{
DECL|field|schema
specifier|private
specifier|final
name|ECSchema
name|schema
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
decl_stmt|;
comment|/**    * Always the same size with triplets. Record the block index for each triplet    * TODO: actually this is only necessary for over-replicated block. Thus can    * be further optimized to save memory usage.    */
DECL|field|indices
specifier|private
name|byte
index|[]
name|indices
decl_stmt|;
DECL|method|BlockInfoStriped (Block blk, ECSchema schema, int cellSize)
specifier|public
name|BlockInfoStriped
parameter_list|(
name|Block
name|blk
parameter_list|,
name|ECSchema
name|schema
parameter_list|,
name|int
name|cellSize
parameter_list|)
block|{
name|super
argument_list|(
name|blk
argument_list|,
call|(
name|short
call|)
argument_list|(
name|schema
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|schema
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|indices
operator|=
operator|new
name|byte
index|[
name|schema
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|schema
operator|.
name|getNumParityUnits
argument_list|()
index|]
expr_stmt|;
name|initIndices
argument_list|()
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|cellSize
operator|=
name|cellSize
expr_stmt|;
block|}
DECL|method|BlockInfoStriped (BlockInfoStriped b)
name|BlockInfoStriped
parameter_list|(
name|BlockInfoStriped
name|b
parameter_list|)
block|{
name|this
argument_list|(
name|b
argument_list|,
name|b
operator|.
name|getSchema
argument_list|()
argument_list|,
name|b
operator|.
name|getCellSize
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|setBlockCollection
argument_list|(
name|b
operator|.
name|getBlockCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getTotalBlockNum ()
specifier|public
name|short
name|getTotalBlockNum
parameter_list|()
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|this
operator|.
name|schema
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|this
operator|.
name|schema
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getDataBlockNum ()
specifier|public
name|short
name|getDataBlockNum
parameter_list|()
block|{
return|return
operator|(
name|short
operator|)
name|this
operator|.
name|schema
operator|.
name|getNumDataUnits
argument_list|()
return|;
block|}
DECL|method|getParityBlockNum ()
specifier|public
name|short
name|getParityBlockNum
parameter_list|()
block|{
return|return
operator|(
name|short
operator|)
name|this
operator|.
name|schema
operator|.
name|getNumParityUnits
argument_list|()
return|;
block|}
comment|/**    * If the block is committed/completed and its length is less than a full    * stripe, it returns the the number of actual data blocks.    * Otherwise it returns the number of data units specified by schema.    */
DECL|method|getRealDataBlockNum ()
specifier|public
name|short
name|getRealDataBlockNum
parameter_list|()
block|{
if|if
condition|(
name|isComplete
argument_list|()
operator|||
name|getBlockUCState
argument_list|()
operator|==
name|BlockUCState
operator|.
name|COMMITTED
condition|)
block|{
return|return
operator|(
name|short
operator|)
name|Math
operator|.
name|min
argument_list|(
name|getDataBlockNum
argument_list|()
argument_list|,
operator|(
name|getNumBytes
argument_list|()
operator|-
literal|1
operator|)
operator|/
name|BLOCK_STRIPED_CELL_SIZE
operator|+
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getDataBlockNum
argument_list|()
return|;
block|}
block|}
DECL|method|getRealTotalBlockNum ()
specifier|public
name|short
name|getRealTotalBlockNum
parameter_list|()
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|getRealDataBlockNum
argument_list|()
operator|+
name|getParityBlockNum
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getSchema ()
specifier|public
name|ECSchema
name|getSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
DECL|method|getCellSize ()
specifier|public
name|int
name|getCellSize
parameter_list|()
block|{
return|return
name|cellSize
return|;
block|}
DECL|method|initIndices ()
specifier|private
name|void
name|initIndices
parameter_list|()
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
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|findSlot ()
specifier|private
name|int
name|findSlot
parameter_list|()
block|{
name|int
name|i
init|=
name|getTotalBlockNum
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|getCapacity
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|getStorageInfo
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
comment|// need to expand the triplet size
name|ensureCapacity
argument_list|(
name|i
operator|+
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|i
return|;
block|}
annotation|@
name|Override
DECL|method|addStorage (DatanodeStorageInfo storage, Block reportedBlock)
name|boolean
name|addStorage
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|,
name|Block
name|reportedBlock
parameter_list|)
block|{
name|int
name|blockIndex
init|=
name|BlockIdManager
operator|.
name|getBlockIndex
argument_list|(
name|reportedBlock
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|blockIndex
decl_stmt|;
name|DatanodeStorageInfo
name|old
init|=
name|getStorageInfo
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
operator|!
name|old
operator|.
name|equals
argument_list|(
name|storage
argument_list|)
condition|)
block|{
comment|// over replicated
comment|// check if the storage has been stored
name|int
name|i
init|=
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
operator|-
literal|1
condition|)
block|{
name|index
operator|=
name|findSlot
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
name|addStorage
argument_list|(
name|storage
argument_list|,
name|index
argument_list|,
name|blockIndex
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|addStorage (DatanodeStorageInfo storage, int index, int blockIndex)
specifier|private
name|void
name|addStorage
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|blockIndex
parameter_list|)
block|{
name|setStorageInfo
argument_list|(
name|index
argument_list|,
name|storage
argument_list|)
expr_stmt|;
name|setNext
argument_list|(
name|index
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setPrevious
argument_list|(
name|index
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|indices
index|[
name|index
index|]
operator|=
operator|(
name|byte
operator|)
name|blockIndex
expr_stmt|;
block|}
DECL|method|findStorageInfoFromEnd (DatanodeStorageInfo storage)
specifier|private
name|int
name|findStorageInfoFromEnd
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
specifier|final
name|int
name|len
init|=
name|getCapacity
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|len
operator|-
literal|1
init|;
name|idx
operator|>=
literal|0
condition|;
name|idx
operator|--
control|)
block|{
name|DatanodeStorageInfo
name|cur
init|=
name|getStorageInfo
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|storage
operator|.
name|equals
argument_list|(
name|cur
argument_list|)
condition|)
block|{
return|return
name|idx
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getStorageBlockIndex (DatanodeStorageInfo storage)
name|int
name|getStorageBlockIndex
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
name|int
name|i
init|=
name|this
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
decl_stmt|;
return|return
name|i
operator|==
operator|-
literal|1
condition|?
operator|-
literal|1
else|:
name|indices
index|[
name|i
index|]
return|;
block|}
comment|/**    * Identify the block stored in the given datanode storage. Note that    * the returned block has the same block Id with the one seen/reported by the    * DataNode.    */
DECL|method|getBlockOnStorage (DatanodeStorageInfo storage)
name|Block
name|getBlockOnStorage
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
name|int
name|index
init|=
name|getStorageBlockIndex
argument_list|(
name|storage
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|Block
name|block
init|=
operator|new
name|Block
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|block
operator|.
name|setBlockId
argument_list|(
name|this
operator|.
name|getBlockId
argument_list|()
operator|+
name|index
argument_list|)
expr_stmt|;
return|return
name|block
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeStorage (DatanodeStorageInfo storage)
name|boolean
name|removeStorage
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
name|int
name|dnIndex
init|=
name|findStorageInfoFromEnd
argument_list|(
name|storage
argument_list|)
decl_stmt|;
if|if
condition|(
name|dnIndex
operator|<
literal|0
condition|)
block|{
comment|// the node is not found
return|return
literal|false
return|;
block|}
assert|assert
name|getPrevious
argument_list|(
name|dnIndex
argument_list|)
operator|==
literal|null
operator|&&
name|getNext
argument_list|(
name|dnIndex
argument_list|)
operator|==
literal|null
operator|:
literal|"Block is still in the list and must be removed first."
assert|;
comment|// set the triplet to null
name|setStorageInfo
argument_list|(
name|dnIndex
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setNext
argument_list|(
name|dnIndex
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setPrevious
argument_list|(
name|dnIndex
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|indices
index|[
name|dnIndex
index|]
operator|=
operator|-
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|ensureCapacity (int totalSize, boolean keepOld)
specifier|private
name|void
name|ensureCapacity
parameter_list|(
name|int
name|totalSize
parameter_list|,
name|boolean
name|keepOld
parameter_list|)
block|{
if|if
condition|(
name|getCapacity
argument_list|()
operator|<
name|totalSize
condition|)
block|{
name|Object
index|[]
name|old
init|=
name|triplets
decl_stmt|;
name|byte
index|[]
name|oldIndices
init|=
name|indices
decl_stmt|;
name|triplets
operator|=
operator|new
name|Object
index|[
name|totalSize
operator|*
literal|3
index|]
expr_stmt|;
name|indices
operator|=
operator|new
name|byte
index|[
name|totalSize
index|]
expr_stmt|;
name|initIndices
argument_list|()
expr_stmt|;
if|if
condition|(
name|keepOld
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|old
argument_list|,
literal|0
argument_list|,
name|triplets
argument_list|,
literal|0
argument_list|,
name|old
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|oldIndices
argument_list|,
literal|0
argument_list|,
name|indices
argument_list|,
literal|0
argument_list|,
name|oldIndices
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|replaceBlock (BlockInfo newBlock)
name|void
name|replaceBlock
parameter_list|(
name|BlockInfo
name|newBlock
parameter_list|)
block|{
assert|assert
name|newBlock
operator|instanceof
name|BlockInfoStriped
assert|;
name|BlockInfoStriped
name|newBlockGroup
init|=
operator|(
name|BlockInfoStriped
operator|)
name|newBlock
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|getCapacity
argument_list|()
decl_stmt|;
name|newBlockGroup
operator|.
name|ensureCapacity
argument_list|(
name|size
argument_list|,
literal|false
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
name|size
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|DatanodeStorageInfo
name|storage
init|=
name|this
operator|.
name|getStorageInfo
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|storage
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|blockIndex
init|=
name|indices
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|boolean
name|removed
init|=
name|storage
operator|.
name|removeBlock
argument_list|(
name|this
argument_list|)
decl_stmt|;
assert|assert
name|removed
operator|:
literal|"currentBlock not found."
assert|;
name|newBlockGroup
operator|.
name|addStorage
argument_list|(
name|storage
argument_list|,
name|i
argument_list|,
name|blockIndex
argument_list|)
expr_stmt|;
name|storage
operator|.
name|insertToList
argument_list|(
name|newBlockGroup
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|spaceConsumed ()
specifier|public
name|long
name|spaceConsumed
parameter_list|()
block|{
comment|// In case striped blocks, total usage by this striped blocks should
comment|// be the total of data blocks and parity blocks because
comment|// `getNumBytes` is the total of actual data block size.
return|return
name|StripedBlockUtil
operator|.
name|spaceConsumedByStripedBlock
argument_list|(
name|getNumBytes
argument_list|()
argument_list|,
name|this
operator|.
name|schema
operator|.
name|getNumDataUnits
argument_list|()
argument_list|,
name|this
operator|.
name|schema
operator|.
name|getNumParityUnits
argument_list|()
argument_list|,
name|BLOCK_STRIPED_CELL_SIZE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isStriped ()
specifier|public
specifier|final
name|boolean
name|isStriped
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|numNodes ()
specifier|public
name|int
name|numNodes
parameter_list|()
block|{
assert|assert
name|this
operator|.
name|triplets
operator|!=
literal|null
operator|:
literal|"BlockInfo is not initialized"
assert|;
assert|assert
name|triplets
operator|.
name|length
operator|%
literal|3
operator|==
literal|0
operator|:
literal|"Malformed BlockInfo"
assert|;
name|int
name|num
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|getCapacity
argument_list|()
operator|-
literal|1
init|;
name|idx
operator|>=
literal|0
condition|;
name|idx
operator|--
control|)
block|{
if|if
condition|(
name|getStorageInfo
argument_list|(
name|idx
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|num
operator|++
expr_stmt|;
block|}
block|}
return|return
name|num
return|;
block|}
comment|/**    * Convert a complete block to an under construction block.    * @return BlockInfoUnderConstruction -  an under construction block.    */
DECL|method|convertToBlockUnderConstruction ( BlockUCState s, DatanodeStorageInfo[] targets)
specifier|public
name|BlockInfoUnderConstructionStriped
name|convertToBlockUnderConstruction
parameter_list|(
name|BlockUCState
name|s
parameter_list|,
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
block|{
specifier|final
name|BlockInfoUnderConstructionStriped
name|ucBlock
decl_stmt|;
if|if
condition|(
name|isComplete
argument_list|()
condition|)
block|{
name|ucBlock
operator|=
operator|new
name|BlockInfoUnderConstructionStriped
argument_list|(
name|this
argument_list|,
name|schema
argument_list|,
name|cellSize
argument_list|,
name|s
argument_list|,
name|targets
argument_list|)
expr_stmt|;
name|ucBlock
operator|.
name|setBlockCollection
argument_list|(
name|getBlockCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the block is already under construction
name|ucBlock
operator|=
operator|(
name|BlockInfoUnderConstructionStriped
operator|)
name|this
expr_stmt|;
name|ucBlock
operator|.
name|setBlockUCState
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|ucBlock
operator|.
name|setExpectedLocations
argument_list|(
name|targets
argument_list|)
expr_stmt|;
name|ucBlock
operator|.
name|setBlockCollection
argument_list|(
name|getBlockCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ucBlock
return|;
block|}
annotation|@
name|Override
DECL|method|hasNoStorage ()
specifier|final
name|boolean
name|hasNoStorage
parameter_list|()
block|{
specifier|final
name|int
name|len
init|=
name|getCapacity
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|len
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|getStorageInfo
argument_list|(
name|idx
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

