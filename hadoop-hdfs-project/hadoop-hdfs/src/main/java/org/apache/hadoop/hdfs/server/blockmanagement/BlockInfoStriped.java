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
name|hdfs
operator|.
name|protocol
operator|.
name|ErasureCodingPolicy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Subclass of {@link BlockInfo}, presenting a block group in erasure coding.  *  * We still use a storage array to store DatanodeStorageInfo for each block in  * the block group. For a (m+k) block group, the first (m+k) storage units  * are sorted and strictly mapped to the corresponding block.  *  * Normally each block belonging to group is stored in only one DataNode.  * However, it is possible that some block is over-replicated. Thus the storage  * array's size can be larger than (m+k). Thus currently we use an extra byte  * array to record the block index for each entry.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockInfoStriped
specifier|public
class|class
name|BlockInfoStriped
extends|extends
name|BlockInfo
block|{
DECL|field|ecPolicy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
decl_stmt|;
comment|/**    * Always the same size with storage. Record the block index for each entry    * TODO: actually this is only necessary for over-replicated block. Thus can    * be further optimized to save memory usage.    */
DECL|field|indices
specifier|private
name|byte
index|[]
name|indices
decl_stmt|;
DECL|method|BlockInfoStriped (Block blk, ErasureCodingPolicy ecPolicy)
specifier|public
name|BlockInfoStriped
parameter_list|(
name|Block
name|blk
parameter_list|,
name|ErasureCodingPolicy
name|ecPolicy
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
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|ecPolicy
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
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|ecPolicy
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
name|ecPolicy
operator|=
name|ecPolicy
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
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
operator|+
name|ecPolicy
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
name|ecPolicy
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
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
return|;
block|}
DECL|method|getCellSize ()
specifier|public
name|int
name|getCellSize
parameter_list|()
block|{
return|return
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
return|;
block|}
comment|/**    * If the block is committed/completed and its length is less than a full    * stripe, it returns the the number of actual data blocks.    * Otherwise it returns the number of data units specified by erasure coding policy.    */
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
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
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
DECL|method|getErasureCodingPolicy ()
specifier|public
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|()
block|{
return|return
name|ecPolicy
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
comment|// need to expand the storage size
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
name|byte
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
comment|// set the entry to null
name|setStorageInfo
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
name|DatanodeStorageInfo
index|[]
name|old
init|=
name|storages
decl_stmt|;
name|byte
index|[]
name|oldIndices
init|=
name|indices
decl_stmt|;
name|storages
operator|=
operator|new
name|DatanodeStorageInfo
index|[
name|totalSize
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
name|storages
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
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
argument_list|,
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
argument_list|,
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
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
name|storages
operator|!=
literal|null
operator|:
literal|"BlockInfo is not initialized"
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
DECL|class|StorageAndBlockIndex
specifier|static
class|class
name|StorageAndBlockIndex
block|{
DECL|field|storage
specifier|final
name|DatanodeStorageInfo
name|storage
decl_stmt|;
DECL|field|blockIndex
specifier|final
name|byte
name|blockIndex
decl_stmt|;
DECL|method|StorageAndBlockIndex (DatanodeStorageInfo storage, byte blockIndex)
name|StorageAndBlockIndex
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|,
name|byte
name|blockIndex
parameter_list|)
block|{
name|this
operator|.
name|storage
operator|=
name|storage
expr_stmt|;
name|this
operator|.
name|blockIndex
operator|=
name|blockIndex
expr_stmt|;
block|}
block|}
DECL|method|getStorageAndIndexInfos ()
specifier|public
name|Iterable
argument_list|<
name|StorageAndBlockIndex
argument_list|>
name|getStorageAndIndexInfos
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|StorageAndBlockIndex
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|StorageAndBlockIndex
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|StorageAndBlockIndex
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
while|while
condition|(
name|index
operator|<
name|getCapacity
argument_list|()
operator|&&
name|getStorageInfo
argument_list|(
name|index
argument_list|)
operator|==
literal|null
condition|)
block|{
name|index
operator|++
expr_stmt|;
block|}
return|return
name|index
operator|<
name|getCapacity
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|StorageAndBlockIndex
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|int
name|i
init|=
name|index
operator|++
decl_stmt|;
return|return
operator|new
name|StorageAndBlockIndex
argument_list|(
name|storages
index|[
name|i
index|]
argument_list|,
name|indices
index|[
name|i
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Remove is not supported"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

