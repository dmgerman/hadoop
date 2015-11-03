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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
import|import static
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
name|namenode
operator|.
name|INodeId
operator|.
name|INVALID_INODE_ID
import|;
end_import

begin_comment
comment|/**  * For a given block (or an erasure coding block group), BlockInfo class  * maintains 1) the {@link BlockCollection} it is part of, and 2) datanodes  * where the replicas of the block, or blocks belonging to the erasure coding  * block group, are stored.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockInfo
specifier|public
specifier|abstract
class|class
name|BlockInfo
extends|extends
name|Block
implements|implements
name|LightWeightGSet
operator|.
name|LinkedElement
block|{
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|BlockInfo
index|[]
name|EMPTY_ARRAY
init|=
block|{}
decl_stmt|;
comment|/**    * Replication factor.    */
DECL|field|replication
specifier|private
name|short
name|replication
decl_stmt|;
comment|/**    * Block collection ID.    */
DECL|field|bcId
specifier|private
name|long
name|bcId
decl_stmt|;
comment|/** For implementing {@link LightWeightGSet.LinkedElement} interface. */
DECL|field|nextLinkedElement
specifier|private
name|LightWeightGSet
operator|.
name|LinkedElement
name|nextLinkedElement
decl_stmt|;
comment|/**    * This array contains triplets of references. For each i-th storage, the    * block belongs to triplets[3*i] is the reference to the    * {@link DatanodeStorageInfo} and triplets[3*i+1] and triplets[3*i+2] are    * references to the previous and the next blocks, respectively, in the list    * of blocks belonging to this storage.    *    * Using previous and next in Object triplets is done instead of a    * {@link LinkedList} list to efficiently use memory. With LinkedList the cost    * per replica is 42 bytes (LinkedList#Entry object per replica) versus 16    * bytes using the triplets.    */
DECL|field|triplets
specifier|protected
name|Object
index|[]
name|triplets
decl_stmt|;
DECL|field|uc
specifier|private
name|BlockUnderConstructionFeature
name|uc
decl_stmt|;
comment|/**    * Construct an entry for blocksmap    * @param size the block's replication factor, or the total number of blocks    *             in the block group    */
DECL|method|BlockInfo (short size)
specifier|public
name|BlockInfo
parameter_list|(
name|short
name|size
parameter_list|)
block|{
name|this
operator|.
name|triplets
operator|=
operator|new
name|Object
index|[
literal|3
operator|*
name|size
index|]
expr_stmt|;
name|this
operator|.
name|bcId
operator|=
name|INVALID_INODE_ID
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|isStriped
argument_list|()
condition|?
literal|0
else|:
name|size
expr_stmt|;
block|}
DECL|method|BlockInfo (Block blk, short size)
specifier|public
name|BlockInfo
parameter_list|(
name|Block
name|blk
parameter_list|,
name|short
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|blk
argument_list|)
expr_stmt|;
name|this
operator|.
name|triplets
operator|=
operator|new
name|Object
index|[
literal|3
operator|*
name|size
index|]
expr_stmt|;
name|this
operator|.
name|bcId
operator|=
name|INVALID_INODE_ID
expr_stmt|;
name|this
operator|.
name|replication
operator|=
name|isStriped
argument_list|()
condition|?
literal|0
else|:
name|size
expr_stmt|;
block|}
DECL|method|getReplication ()
specifier|public
name|short
name|getReplication
parameter_list|()
block|{
return|return
name|replication
return|;
block|}
DECL|method|setReplication (short repl)
specifier|public
name|void
name|setReplication
parameter_list|(
name|short
name|repl
parameter_list|)
block|{
name|this
operator|.
name|replication
operator|=
name|repl
expr_stmt|;
block|}
DECL|method|getBlockCollectionId ()
specifier|public
name|long
name|getBlockCollectionId
parameter_list|()
block|{
return|return
name|bcId
return|;
block|}
DECL|method|setBlockCollectionId (long id)
specifier|public
name|void
name|setBlockCollectionId
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|this
operator|.
name|bcId
operator|=
name|id
expr_stmt|;
block|}
DECL|method|isDeleted ()
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
return|return
name|bcId
operator|==
name|INVALID_INODE_ID
return|;
block|}
DECL|method|getDatanode (int index)
specifier|public
name|DatanodeDescriptor
name|getDatanode
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|DatanodeStorageInfo
name|storage
init|=
name|getStorageInfo
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|storage
operator|==
literal|null
condition|?
literal|null
else|:
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
return|;
block|}
DECL|method|getStorageInfo (int index)
name|DatanodeStorageInfo
name|getStorageInfo
parameter_list|(
name|int
name|index
parameter_list|)
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
name|index
operator|>=
literal|0
operator|&&
name|index
operator|*
literal|3
operator|<
name|triplets
operator|.
name|length
operator|:
literal|"Index is out of bound"
assert|;
return|return
operator|(
name|DatanodeStorageInfo
operator|)
name|triplets
index|[
name|index
operator|*
literal|3
index|]
return|;
block|}
DECL|method|getPrevious (int index)
name|BlockInfo
name|getPrevious
parameter_list|(
name|int
name|index
parameter_list|)
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
name|index
operator|>=
literal|0
operator|&&
name|index
operator|*
literal|3
operator|+
literal|1
operator|<
name|triplets
operator|.
name|length
operator|:
literal|"Index is out of bound"
assert|;
name|BlockInfo
name|info
init|=
operator|(
name|BlockInfo
operator|)
name|triplets
index|[
name|index
operator|*
literal|3
operator|+
literal|1
index|]
decl_stmt|;
assert|assert
name|info
operator|==
literal|null
operator|||
name|info
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|BlockInfo
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|:
literal|"BlockInfo is expected at "
operator|+
name|index
operator|*
literal|3
assert|;
return|return
name|info
return|;
block|}
DECL|method|getNext (int index)
name|BlockInfo
name|getNext
parameter_list|(
name|int
name|index
parameter_list|)
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
name|index
operator|>=
literal|0
operator|&&
name|index
operator|*
literal|3
operator|+
literal|2
operator|<
name|triplets
operator|.
name|length
operator|:
literal|"Index is out of bound"
assert|;
name|BlockInfo
name|info
init|=
operator|(
name|BlockInfo
operator|)
name|triplets
index|[
name|index
operator|*
literal|3
operator|+
literal|2
index|]
decl_stmt|;
assert|assert
name|info
operator|==
literal|null
operator|||
name|info
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|BlockInfo
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|:
literal|"BlockInfo is expected at "
operator|+
name|index
operator|*
literal|3
assert|;
return|return
name|info
return|;
block|}
DECL|method|setStorageInfo (int index, DatanodeStorageInfo storage)
name|void
name|setStorageInfo
parameter_list|(
name|int
name|index
parameter_list|,
name|DatanodeStorageInfo
name|storage
parameter_list|)
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
name|index
operator|>=
literal|0
operator|&&
name|index
operator|*
literal|3
operator|<
name|triplets
operator|.
name|length
operator|:
literal|"Index is out of bound"
assert|;
name|triplets
index|[
name|index
operator|*
literal|3
index|]
operator|=
name|storage
expr_stmt|;
block|}
comment|/**    * Return the previous block on the block list for the datanode at    * position index. Set the previous block on the list to "to".    *    * @param index - the datanode index    * @param to - block to be set to previous on the list of blocks    * @return current previous block on the list of blocks    */
DECL|method|setPrevious (int index, BlockInfo to)
name|BlockInfo
name|setPrevious
parameter_list|(
name|int
name|index
parameter_list|,
name|BlockInfo
name|to
parameter_list|)
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
name|index
operator|>=
literal|0
operator|&&
name|index
operator|*
literal|3
operator|+
literal|1
operator|<
name|triplets
operator|.
name|length
operator|:
literal|"Index is out of bound"
assert|;
name|BlockInfo
name|info
init|=
operator|(
name|BlockInfo
operator|)
name|triplets
index|[
name|index
operator|*
literal|3
operator|+
literal|1
index|]
decl_stmt|;
name|triplets
index|[
name|index
operator|*
literal|3
operator|+
literal|1
index|]
operator|=
name|to
expr_stmt|;
return|return
name|info
return|;
block|}
comment|/**    * Return the next block on the block list for the datanode at    * position index. Set the next block on the list to "to".    *    * @param index - the datanode index    * @param to - block to be set to next on the list of blocks    * @return current next block on the list of blocks    */
DECL|method|setNext (int index, BlockInfo to)
name|BlockInfo
name|setNext
parameter_list|(
name|int
name|index
parameter_list|,
name|BlockInfo
name|to
parameter_list|)
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
name|index
operator|>=
literal|0
operator|&&
name|index
operator|*
literal|3
operator|+
literal|2
operator|<
name|triplets
operator|.
name|length
operator|:
literal|"Index is out of bound"
assert|;
name|BlockInfo
name|info
init|=
operator|(
name|BlockInfo
operator|)
name|triplets
index|[
name|index
operator|*
literal|3
operator|+
literal|2
index|]
decl_stmt|;
name|triplets
index|[
name|index
operator|*
literal|3
operator|+
literal|2
index|]
operator|=
name|to
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|getCapacity ()
specifier|public
name|int
name|getCapacity
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
return|return
name|triplets
operator|.
name|length
operator|/
literal|3
return|;
block|}
comment|/**    * Count the number of data-nodes the block currently belongs to (i.e., NN    * has received block reports from the DN).    */
DECL|method|numNodes ()
specifier|public
specifier|abstract
name|int
name|numNodes
parameter_list|()
function_decl|;
comment|/**    * Add a {@link DatanodeStorageInfo} location for a block    * @param storage The storage to add    * @param reportedBlock The block reported from the datanode. This is only    *                      used by erasure coded blocks, this block's id contains    *                      information indicating the index of the block in the    *                      corresponding block group.    */
DECL|method|addStorage (DatanodeStorageInfo storage, Block reportedBlock)
specifier|abstract
name|boolean
name|addStorage
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|,
name|Block
name|reportedBlock
parameter_list|)
function_decl|;
comment|/**    * Remove {@link DatanodeStorageInfo} location for a block    */
DECL|method|removeStorage (DatanodeStorageInfo storage)
specifier|abstract
name|boolean
name|removeStorage
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|)
function_decl|;
DECL|method|isStriped ()
specifier|public
specifier|abstract
name|boolean
name|isStriped
parameter_list|()
function_decl|;
comment|/** @return true if there is no datanode storage associated with the block */
DECL|method|hasNoStorage ()
specifier|abstract
name|boolean
name|hasNoStorage
parameter_list|()
function_decl|;
comment|/**    * Find specified DatanodeStorageInfo.    * @return DatanodeStorageInfo or null if not found.    */
DECL|method|findStorageInfo (DatanodeDescriptor dn)
name|DatanodeStorageInfo
name|findStorageInfo
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
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
name|cur
operator|!=
literal|null
operator|&&
name|cur
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|==
name|dn
condition|)
block|{
return|return
name|cur
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Find specified DatanodeStorageInfo.    * @return index or -1 if not found.    */
DECL|method|findStorageInfo (DatanodeStorageInfo storageInfo)
name|int
name|findStorageInfo
parameter_list|(
name|DatanodeStorageInfo
name|storageInfo
parameter_list|)
block|{
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
name|cur
operator|==
name|storageInfo
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
comment|/**    * Insert this block into the head of the list of blocks    * related to the specified DatanodeStorageInfo.    * If the head is null then form a new list.    * @return current block as the new head of the list.    */
DECL|method|listInsert (BlockInfo head, DatanodeStorageInfo storage)
name|BlockInfo
name|listInsert
parameter_list|(
name|BlockInfo
name|head
parameter_list|,
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
name|int
name|dnIndex
init|=
name|this
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
decl_stmt|;
assert|assert
name|dnIndex
operator|>=
literal|0
operator|:
literal|"Data node is not found: current"
assert|;
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
literal|"Block is already in the list and cannot be inserted."
assert|;
name|this
operator|.
name|setPrevious
argument_list|(
name|dnIndex
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|setNext
argument_list|(
name|dnIndex
argument_list|,
name|head
argument_list|)
expr_stmt|;
if|if
condition|(
name|head
operator|!=
literal|null
condition|)
block|{
name|head
operator|.
name|setPrevious
argument_list|(
name|head
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**    * Remove this block from the list of blocks    * related to the specified DatanodeStorageInfo.    * If this block is the head of the list then return the next block as    * the new head.    * @return the new head of the list or null if the list becomes    * empy after deletion.    */
DECL|method|listRemove (BlockInfo head, DatanodeStorageInfo storage)
name|BlockInfo
name|listRemove
parameter_list|(
name|BlockInfo
name|head
parameter_list|,
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
if|if
condition|(
name|head
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|dnIndex
init|=
name|this
operator|.
name|findStorageInfo
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
comment|// this block is not on the data-node list
return|return
name|head
return|;
block|}
name|BlockInfo
name|next
init|=
name|this
operator|.
name|getNext
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
name|BlockInfo
name|prev
init|=
name|this
operator|.
name|getPrevious
argument_list|(
name|dnIndex
argument_list|)
decl_stmt|;
name|this
operator|.
name|setNext
argument_list|(
name|dnIndex
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|setPrevious
argument_list|(
name|dnIndex
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
name|prev
operator|.
name|setNext
argument_list|(
name|prev
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|setPrevious
argument_list|(
name|next
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
argument_list|,
name|prev
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|==
name|head
condition|)
block|{
comment|// removing the head
name|head
operator|=
name|next
expr_stmt|;
block|}
return|return
name|head
return|;
block|}
comment|/**    * Remove this block from the list of blocks related to the specified    * DatanodeDescriptor. Insert it into the head of the list of blocks.    *    * @return the new head of the list.    */
DECL|method|moveBlockToHead (BlockInfo head, DatanodeStorageInfo storage, int curIndex, int headIndex)
specifier|public
name|BlockInfo
name|moveBlockToHead
parameter_list|(
name|BlockInfo
name|head
parameter_list|,
name|DatanodeStorageInfo
name|storage
parameter_list|,
name|int
name|curIndex
parameter_list|,
name|int
name|headIndex
parameter_list|)
block|{
if|if
condition|(
name|head
operator|==
name|this
condition|)
block|{
return|return
name|this
return|;
block|}
name|BlockInfo
name|next
init|=
name|this
operator|.
name|setNext
argument_list|(
name|curIndex
argument_list|,
name|head
argument_list|)
decl_stmt|;
name|BlockInfo
name|prev
init|=
name|this
operator|.
name|setPrevious
argument_list|(
name|curIndex
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|head
operator|.
name|setPrevious
argument_list|(
name|headIndex
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|prev
operator|.
name|setNext
argument_list|(
name|prev
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
argument_list|,
name|next
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|setPrevious
argument_list|(
name|next
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
argument_list|,
name|prev
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Super implementation is sufficient
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// Sufficient to rely on super's implementation
return|return
operator|(
name|this
operator|==
name|obj
operator|)
operator|||
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNext ()
specifier|public
name|LightWeightGSet
operator|.
name|LinkedElement
name|getNext
parameter_list|()
block|{
return|return
name|nextLinkedElement
return|;
block|}
annotation|@
name|Override
DECL|method|setNext (LightWeightGSet.LinkedElement next)
specifier|public
name|void
name|setNext
parameter_list|(
name|LightWeightGSet
operator|.
name|LinkedElement
name|next
parameter_list|)
block|{
name|this
operator|.
name|nextLinkedElement
operator|=
name|next
expr_stmt|;
block|}
comment|/* UnderConstruction Feature related */
DECL|method|getUnderConstructionFeature ()
specifier|public
name|BlockUnderConstructionFeature
name|getUnderConstructionFeature
parameter_list|()
block|{
return|return
name|uc
return|;
block|}
DECL|method|getBlockUCState ()
specifier|public
name|BlockUCState
name|getBlockUCState
parameter_list|()
block|{
return|return
name|uc
operator|==
literal|null
condition|?
name|BlockUCState
operator|.
name|COMPLETE
else|:
name|uc
operator|.
name|getBlockUCState
argument_list|()
return|;
block|}
comment|/**    * Is this block complete?    *    * @return true if the state of the block is {@link BlockUCState#COMPLETE}    */
DECL|method|isComplete ()
specifier|public
name|boolean
name|isComplete
parameter_list|()
block|{
return|return
name|getBlockUCState
argument_list|()
operator|.
name|equals
argument_list|(
name|BlockUCState
operator|.
name|COMPLETE
argument_list|)
return|;
block|}
comment|/**    * Add/Update the under construction feature.    */
DECL|method|convertToBlockUnderConstruction (BlockUCState s, DatanodeStorageInfo[] targets)
specifier|public
name|void
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
if|if
condition|(
name|isComplete
argument_list|()
condition|)
block|{
name|uc
operator|=
operator|new
name|BlockUnderConstructionFeature
argument_list|(
name|this
argument_list|,
name|s
argument_list|,
name|targets
argument_list|,
name|this
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the block is already under construction
name|uc
operator|.
name|setBlockUCState
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|uc
operator|.
name|setExpectedLocations
argument_list|(
name|this
argument_list|,
name|targets
argument_list|,
name|this
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convert an under construction block to complete.    */
DECL|method|convertToCompleteBlock ()
name|void
name|convertToCompleteBlock
parameter_list|()
block|{
assert|assert
name|getBlockUCState
argument_list|()
operator|!=
name|BlockUCState
operator|.
name|COMPLETE
operator|:
literal|"Trying to convert a COMPLETE block"
assert|;
name|uc
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Process the recorded replicas. When about to commit or finish the    * pipeline recovery sort out bad replicas.    * @param genStamp  The final generation stamp for the block.    */
DECL|method|setGenerationStampAndVerifyReplicas (long genStamp)
specifier|public
name|void
name|setGenerationStampAndVerifyReplicas
parameter_list|(
name|long
name|genStamp
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|uc
operator|!=
literal|null
operator|&&
operator|!
name|isComplete
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set the generation stamp for the block.
name|setGenerationStamp
argument_list|(
name|genStamp
argument_list|)
expr_stmt|;
comment|// Remove the replicas with wrong gen stamp
name|List
argument_list|<
name|ReplicaUnderConstruction
argument_list|>
name|staleReplicas
init|=
name|uc
operator|.
name|getStaleReplicas
argument_list|(
name|genStamp
argument_list|)
decl_stmt|;
for|for
control|(
name|ReplicaUnderConstruction
name|r
range|:
name|staleReplicas
control|)
block|{
name|r
operator|.
name|getExpectedStorageLocation
argument_list|()
operator|.
name|removeBlock
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|blockStateChangeLog
operator|.
name|debug
argument_list|(
literal|"BLOCK* Removing stale replica {}"
operator|+
literal|" of {}"
argument_list|,
name|r
argument_list|,
name|Block
operator|.
name|toString
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Commit block's length and generation stamp as reported by the client.    * Set block state to {@link BlockUCState#COMMITTED}.    * @param block - contains client reported block length and generation    * @throws IOException if block ids are inconsistent.    */
DECL|method|commitBlock (Block block)
name|void
name|commitBlock
parameter_list|(
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getBlockId
argument_list|()
operator|!=
name|block
operator|.
name|getBlockId
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Trying to commit inconsistent block: id = "
operator|+
name|block
operator|.
name|getBlockId
argument_list|()
operator|+
literal|", expected id = "
operator|+
name|getBlockId
argument_list|()
argument_list|)
throw|;
block|}
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|isComplete
argument_list|()
argument_list|)
expr_stmt|;
name|uc
operator|.
name|commit
argument_list|()
expr_stmt|;
name|this
operator|.
name|setNumBytes
argument_list|(
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
comment|// Sort out invalid replicas.
name|setGenerationStampAndVerifyReplicas
argument_list|(
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

