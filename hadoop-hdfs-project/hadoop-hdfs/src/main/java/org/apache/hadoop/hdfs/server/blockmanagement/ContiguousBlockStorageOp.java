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

begin_comment
comment|/**  * Utility class with logic on managing storage locations shared between  * complete and under-construction blocks under the contiguous format --  * {@link BlockInfoContiguous} and  * {@link BlockInfoUnderConstructionContiguous}.  */
end_comment

begin_class
DECL|class|ContiguousBlockStorageOp
class|class
name|ContiguousBlockStorageOp
block|{
comment|/**    * Ensure that there is enough  space to include num more triplets.    * @return first free triplet index.    */
DECL|method|ensureCapacity (BlockInfo b, int num)
specifier|private
specifier|static
name|int
name|ensureCapacity
parameter_list|(
name|BlockInfo
name|b
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|b
operator|.
name|triplets
operator|!=
literal|null
argument_list|,
literal|"BlockInfo is not initialized"
argument_list|)
expr_stmt|;
name|int
name|last
init|=
name|b
operator|.
name|numNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|.
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
name|b
operator|.
name|triplets
decl_stmt|;
name|b
operator|.
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
name|b
operator|.
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
DECL|method|addStorage (BlockInfo b, DatanodeStorageInfo storage)
specifier|static
name|boolean
name|addStorage
parameter_list|(
name|BlockInfo
name|b
parameter_list|,
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
comment|// find the last null node
name|int
name|lastNode
init|=
name|ensureCapacity
argument_list|(
name|b
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|b
operator|.
name|setStorageInfo
argument_list|(
name|lastNode
argument_list|,
name|storage
argument_list|)
expr_stmt|;
name|b
operator|.
name|setNext
argument_list|(
name|lastNode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|b
operator|.
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
DECL|method|removeStorage (BlockInfo b, DatanodeStorageInfo storage)
specifier|static
name|boolean
name|removeStorage
parameter_list|(
name|BlockInfo
name|b
parameter_list|,
name|DatanodeStorageInfo
name|storage
parameter_list|)
block|{
name|int
name|dnIndex
init|=
name|b
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
comment|// the node is not found
return|return
literal|false
return|;
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|b
operator|.
name|getPrevious
argument_list|(
name|dnIndex
argument_list|)
operator|==
literal|null
operator|&&
name|b
operator|.
name|getNext
argument_list|(
name|dnIndex
argument_list|)
operator|==
literal|null
argument_list|,
literal|"Block is still in the list and must be removed first."
argument_list|)
expr_stmt|;
comment|// find the last not null node
name|int
name|lastNode
init|=
name|b
operator|.
name|numNodes
argument_list|()
operator|-
literal|1
decl_stmt|;
comment|// replace current node triplet by the lastNode one
name|b
operator|.
name|setStorageInfo
argument_list|(
name|dnIndex
argument_list|,
name|b
operator|.
name|getStorageInfo
argument_list|(
name|lastNode
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|setNext
argument_list|(
name|dnIndex
argument_list|,
name|b
operator|.
name|getNext
argument_list|(
name|lastNode
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|setPrevious
argument_list|(
name|dnIndex
argument_list|,
name|b
operator|.
name|getPrevious
argument_list|(
name|lastNode
argument_list|)
argument_list|)
expr_stmt|;
comment|// set the last triplet to null
name|b
operator|.
name|setStorageInfo
argument_list|(
name|lastNode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|b
operator|.
name|setNext
argument_list|(
name|lastNode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|b
operator|.
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
DECL|method|numNodes (BlockInfo b)
specifier|static
name|int
name|numNodes
parameter_list|(
name|BlockInfo
name|b
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|b
operator|.
name|triplets
operator|!=
literal|null
argument_list|,
literal|"BlockInfo is not initialized"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|b
operator|.
name|triplets
operator|.
name|length
operator|%
literal|3
operator|==
literal|0
argument_list|,
literal|"Malformed BlockInfo"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|b
operator|.
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
name|b
operator|.
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
DECL|method|replaceBlock (BlockInfo b, BlockInfo newBlock)
specifier|static
name|void
name|replaceBlock
parameter_list|(
name|BlockInfo
name|b
parameter_list|,
name|BlockInfo
name|newBlock
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|b
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
name|b
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
name|b
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|removed
argument_list|,
literal|"currentBlock not found."
argument_list|)
expr_stmt|;
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
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|result
operator|==
name|DatanodeStorageInfo
operator|.
name|AddBlockResult
operator|.
name|ADDED
argument_list|,
literal|"newBlock already exists."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

