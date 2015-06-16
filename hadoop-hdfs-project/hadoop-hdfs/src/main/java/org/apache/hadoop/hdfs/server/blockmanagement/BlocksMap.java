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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|protocol
operator|.
name|DatanodeStorage
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
name|util
operator|.
name|GSet
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
name|util
operator|.
name|LightWeightGSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_comment
comment|/**  * This class maintains the map from a block to its metadata.  * block's metadata currently includes blockCollection it belongs to and  * the datanodes that store the block.  */
end_comment

begin_class
DECL|class|BlocksMap
class|class
name|BlocksMap
block|{
DECL|class|StorageIterator
specifier|private
specifier|static
class|class
name|StorageIterator
implements|implements
name|Iterator
argument_list|<
name|DatanodeStorageInfo
argument_list|>
block|{
DECL|field|blockInfo
specifier|private
specifier|final
name|BlockInfo
name|blockInfo
decl_stmt|;
DECL|field|nextIdx
specifier|private
name|int
name|nextIdx
init|=
literal|0
decl_stmt|;
DECL|method|StorageIterator (BlockInfo blkInfo)
name|StorageIterator
parameter_list|(
name|BlockInfo
name|blkInfo
parameter_list|)
block|{
name|this
operator|.
name|blockInfo
operator|=
name|blkInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|blockInfo
operator|!=
literal|null
operator|&&
name|nextIdx
operator|<
name|blockInfo
operator|.
name|getCapacity
argument_list|()
operator|&&
name|blockInfo
operator|.
name|getDatanode
argument_list|(
name|nextIdx
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|DatanodeStorageInfo
name|next
parameter_list|()
block|{
return|return
name|blockInfo
operator|.
name|getStorageInfo
argument_list|(
name|nextIdx
operator|++
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Sorry. can't remove."
argument_list|)
throw|;
block|}
block|}
comment|/** Constant {@link LightWeightGSet} capacity. */
DECL|field|capacity
specifier|private
specifier|final
name|int
name|capacity
decl_stmt|;
DECL|field|blocks
specifier|private
name|GSet
argument_list|<
name|Block
argument_list|,
name|BlockInfo
argument_list|>
name|blocks
decl_stmt|;
DECL|method|BlocksMap (int capacity)
name|BlocksMap
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
comment|// Use 2% of total memory to size the GSet capacity
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
operator|new
name|LightWeightGSet
argument_list|<
name|Block
argument_list|,
name|BlockInfo
argument_list|>
argument_list|(
name|capacity
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BlockInfo
argument_list|>
name|iterator
parameter_list|()
block|{
name|SetIterator
name|iterator
init|=
operator|new
name|SetIterator
argument_list|()
decl_stmt|;
comment|/*          * Not tracking any modifications to set. As this set will be used          * always under FSNameSystem lock, modifications will not cause any          * ConcurrentModificationExceptions. But there is a chance of missing          * newly added elements during iteration.          */
name|iterator
operator|.
name|setTrackModification
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|iterator
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
name|clear
argument_list|()
expr_stmt|;
name|blocks
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|clear ()
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|blocks
operator|!=
literal|null
condition|)
block|{
name|blocks
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getBlockCollection (Block b)
name|BlockCollection
name|getBlockCollection
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
name|BlockInfo
name|info
init|=
name|blocks
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
return|return
operator|(
name|info
operator|!=
literal|null
operator|)
condition|?
name|info
operator|.
name|getBlockCollection
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**    * Add block b belonging to the specified block collection to the map.    */
DECL|method|addBlockCollection (BlockInfo b, BlockCollection bc)
name|BlockInfo
name|addBlockCollection
parameter_list|(
name|BlockInfo
name|b
parameter_list|,
name|BlockCollection
name|bc
parameter_list|)
block|{
name|BlockInfo
name|info
init|=
name|blocks
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
name|b
condition|)
block|{
name|info
operator|=
name|b
expr_stmt|;
name|blocks
operator|.
name|put
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setBlockCollection
argument_list|(
name|bc
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
comment|/**    * Remove the block from the block map;    * remove it from all data-node lists it belongs to;    * and remove all data-node locations associated with the block.    */
DECL|method|removeBlock (Block block)
name|void
name|removeBlock
parameter_list|(
name|Block
name|block
parameter_list|)
block|{
name|BlockInfo
name|blockInfo
init|=
name|blocks
operator|.
name|remove
argument_list|(
name|block
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockInfo
operator|==
literal|null
condition|)
return|return;
name|blockInfo
operator|.
name|setBlockCollection
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|blockInfo
operator|.
name|numNodes
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
name|DatanodeDescriptor
name|dn
init|=
name|blockInfo
operator|.
name|getDatanode
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|dn
operator|.
name|removeBlock
argument_list|(
name|blockInfo
argument_list|)
expr_stmt|;
comment|// remove from the list and wipe the location
block|}
block|}
comment|/** Returns the block object it it exists in the map. */
DECL|method|getStoredBlock (Block b)
name|BlockInfo
name|getStoredBlock
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
return|return
name|blocks
operator|.
name|get
argument_list|(
name|b
argument_list|)
return|;
block|}
comment|/**    * Searches for the block in the BlocksMap and     * returns {@link Iterable} of the storages the block belongs to.    */
DECL|method|getStorages (Block b)
name|Iterable
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|getStorages
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
return|return
name|getStorages
argument_list|(
name|blocks
operator|.
name|get
argument_list|(
name|b
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Searches for the block in the BlocksMap and     * returns {@link Iterable} of the storages the block belongs to    *<i>that are of the given {@link DatanodeStorage.State state}</i>.    *     * @param state DatanodeStorage state by which to filter the returned Iterable    */
DECL|method|getStorages (Block b, final DatanodeStorage.State state)
name|Iterable
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|getStorages
parameter_list|(
name|Block
name|b
parameter_list|,
specifier|final
name|DatanodeStorage
operator|.
name|State
name|state
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|getStorages
argument_list|(
name|blocks
operator|.
name|get
argument_list|(
name|b
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
return|return
name|storage
operator|.
name|getState
argument_list|()
operator|==
name|state
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**    * For a block that has already been retrieved from the BlocksMap    * returns {@link Iterable} of the storages the block belongs to.    */
DECL|method|getStorages (final BlockInfo storedBlock)
name|Iterable
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|getStorages
parameter_list|(
specifier|final
name|BlockInfo
name|storedBlock
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|StorageIterator
argument_list|(
name|storedBlock
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** counts number of containing nodes. Better than using iterator. */
DECL|method|numNodes (Block b)
name|int
name|numNodes
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
name|BlockInfo
name|info
init|=
name|blocks
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
return|return
name|info
operator|==
literal|null
condition|?
literal|0
else|:
name|info
operator|.
name|numNodes
argument_list|()
return|;
block|}
comment|/**    * Remove data-node reference from the block.    * Remove the block from the block map    * only if it does not belong to any file and data-nodes.    */
DECL|method|removeNode (Block b, DatanodeDescriptor node)
name|boolean
name|removeNode
parameter_list|(
name|Block
name|b
parameter_list|,
name|DatanodeDescriptor
name|node
parameter_list|)
block|{
name|BlockInfo
name|info
init|=
name|blocks
operator|.
name|get
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|// remove block from the data-node list and the node from the block info
name|boolean
name|removed
init|=
name|node
operator|.
name|removeBlock
argument_list|(
name|info
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getDatanode
argument_list|(
literal|0
argument_list|)
operator|==
literal|null
comment|// no datanodes left
operator|&&
name|info
operator|.
name|isDeleted
argument_list|()
condition|)
block|{
comment|// does not belong to a file
name|blocks
operator|.
name|remove
argument_list|(
name|b
argument_list|)
expr_stmt|;
comment|// remove block from the map
block|}
return|return
name|removed
return|;
block|}
DECL|method|size ()
name|int
name|size
parameter_list|()
block|{
if|if
condition|(
name|blocks
operator|!=
literal|null
condition|)
block|{
return|return
name|blocks
operator|.
name|size
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|getBlocks ()
name|Iterable
argument_list|<
name|BlockInfo
argument_list|>
name|getBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
comment|/** Get the capacity of the HashMap that stores blocks */
DECL|method|getCapacity ()
name|int
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
comment|/**    * Replace a block in the block map by a new block.    * The new block and the old one have the same key.    * @param newBlock - block for replacement    * @return new block    */
DECL|method|replaceBlock (BlockInfo newBlock)
name|BlockInfo
name|replaceBlock
parameter_list|(
name|BlockInfo
name|newBlock
parameter_list|)
block|{
name|BlockInfo
name|currentBlock
init|=
name|blocks
operator|.
name|get
argument_list|(
name|newBlock
argument_list|)
decl_stmt|;
assert|assert
name|currentBlock
operator|!=
literal|null
operator|:
literal|"the block if not in blocksMap"
assert|;
comment|// replace block in data-node lists
name|currentBlock
operator|.
name|replaceBlock
argument_list|(
name|newBlock
argument_list|)
expr_stmt|;
comment|// replace block in the map itself
name|blocks
operator|.
name|put
argument_list|(
name|newBlock
argument_list|)
expr_stmt|;
return|return
name|newBlock
return|;
block|}
block|}
end_class

end_unit

