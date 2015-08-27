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

begin_comment
comment|/**  * Subclass of {@link BlockInfo}, used for a block with replication scheme.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockInfoContiguous
specifier|public
class|class
name|BlockInfoContiguous
extends|extends
name|BlockInfo
block|{
DECL|method|BlockInfoContiguous (short size)
specifier|public
name|BlockInfoContiguous
parameter_list|(
name|short
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockInfoContiguous (Block blk, short size)
specifier|public
name|BlockInfoContiguous
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
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure that there is enough  space to include num more triplets.    * @return first free triplet index.    */
DECL|method|ensureCapacity (int num)
specifier|private
name|int
name|ensureCapacity
parameter_list|(
name|int
name|num
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
name|int
name|last
init|=
name|numNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|triplets
operator|.
name|length
operator|>=
operator|(
name|last
operator|+
name|num
operator|)
operator|*
literal|3
condition|)
block|{
return|return
name|last
return|;
block|}
comment|/* Not enough space left. Create a new array. Should normally      * happen only when replication is manually increased by the user. */
name|Object
index|[]
name|old
init|=
name|triplets
decl_stmt|;
name|triplets
operator|=
operator|new
name|Object
index|[
operator|(
name|last
operator|+
name|num
operator|)
operator|*
literal|3
index|]
expr_stmt|;
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
name|last
operator|*
literal|3
argument_list|)
expr_stmt|;
return|return
name|last
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
comment|// find the last null node
name|int
name|lastNode
init|=
name|ensureCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|setStorageInfo
argument_list|(
name|lastNode
argument_list|,
name|storage
argument_list|)
expr_stmt|;
name|setNext
argument_list|(
name|lastNode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setPrevious
argument_list|(
name|lastNode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
comment|// find the last not null node
name|int
name|lastNode
init|=
name|numNodes
argument_list|()
operator|-
literal|1
decl_stmt|;
comment|// replace current node triplet by the lastNode one
name|setStorageInfo
argument_list|(
name|dnIndex
argument_list|,
name|getStorageInfo
argument_list|(
name|lastNode
argument_list|)
argument_list|)
expr_stmt|;
name|setNext
argument_list|(
name|dnIndex
argument_list|,
name|getNext
argument_list|(
name|lastNode
argument_list|)
argument_list|)
expr_stmt|;
name|setPrevious
argument_list|(
name|dnIndex
argument_list|,
name|getPrevious
argument_list|(
name|lastNode
argument_list|)
argument_list|)
expr_stmt|;
comment|// set the last triplet to null
name|setStorageInfo
argument_list|(
name|lastNode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setNext
argument_list|(
name|lastNode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|setPrevious
argument_list|(
name|lastNode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|getDatanode
argument_list|(
name|idx
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|idx
operator|+
literal|1
return|;
block|}
block|}
return|return
literal|0
return|;
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
name|BlockInfoContiguous
assert|;
for|for
control|(
name|int
name|i
init|=
name|this
operator|.
name|numNodes
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
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
specifier|final
name|DatanodeStorageInfo
operator|.
name|AddBlockResult
name|result
init|=
name|storage
operator|.
name|addBlock
argument_list|(
name|newBlock
argument_list|,
name|newBlock
argument_list|)
decl_stmt|;
assert|assert
name|result
operator|==
name|DatanodeStorageInfo
operator|.
name|AddBlockResult
operator|.
name|ADDED
operator|:
literal|"newBlock already exists."
assert|;
block|}
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
literal|false
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
return|return
name|getStorageInfo
argument_list|(
literal|0
argument_list|)
operator|==
literal|null
return|;
block|}
block|}
end_class

end_unit

