begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Arrays
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
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|DatanodeID
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
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|blockmanagement
operator|.
name|DatanodeDescriptor
operator|.
name|CachedBlocksList
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
name|CachedBlock
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

begin_class
DECL|class|TestCachedBlocksList
specifier|public
class|class
name|TestCachedBlocksList
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestCachedBlocksList
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSingleList ()
specifier|public
name|void
name|testSingleList
parameter_list|()
block|{
name|DatanodeDescriptor
name|dn
init|=
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"localhost"
argument_list|,
literal|"abcd"
argument_list|,
literal|5000
argument_list|,
literal|5001
argument_list|,
literal|5002
argument_list|,
literal|5003
argument_list|)
argument_list|)
decl_stmt|;
name|CachedBlock
index|[]
name|blocks
init|=
operator|new
name|CachedBlock
index|[]
block|{
operator|new
name|CachedBlock
argument_list|(
literal|0L
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|CachedBlock
argument_list|(
literal|1L
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|true
argument_list|)
block|,
operator|new
name|CachedBlock
argument_list|(
literal|2L
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|true
argument_list|)
block|,       }
decl_stmt|;
comment|// check that lists are empty
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected pending cached list to start off empty."
argument_list|,
operator|!
name|dn
operator|.
name|getPendingCached
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected cached list to start off empty."
argument_list|,
operator|!
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected pending uncached list to start off empty."
argument_list|,
operator|!
name|dn
operator|.
name|getPendingUncached
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// add a block to the back
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|add
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected pending cached list to still be empty."
argument_list|,
operator|!
name|dn
operator|.
name|getPendingCached
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"failed to insert blocks[0]"
argument_list|,
name|blocks
index|[
literal|0
index|]
argument_list|,
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected pending uncached list to still be empty."
argument_list|,
operator|!
name|dn
operator|.
name|getPendingUncached
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// add another block to the back
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|add
argument_list|(
name|blocks
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|CachedBlock
argument_list|>
name|iter
init|=
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
index|[
literal|1
index|]
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// add a block to the front
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|addFirst
argument_list|(
name|blocks
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|iter
operator|=
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
index|[
literal|2
index|]
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
index|[
literal|1
index|]
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove a block from the middle
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|remove
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|iter
operator|=
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
index|[
literal|2
index|]
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
index|[
literal|1
index|]
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove all blocks
name|dn
operator|.
name|getCached
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected cached list to be empty after clear."
argument_list|,
operator|!
name|dn
operator|.
name|getPendingCached
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddElementsToList (CachedBlocksList list, CachedBlock[] blocks)
specifier|private
name|void
name|testAddElementsToList
parameter_list|(
name|CachedBlocksList
name|list
parameter_list|,
name|CachedBlock
index|[]
name|blocks
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected list to start off empty."
argument_list|,
operator|!
name|list
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|CachedBlock
name|block
range|:
name|blocks
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|list
operator|.
name|add
argument_list|(
name|block
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRemoveElementsFromList (Random r, CachedBlocksList list, CachedBlock[] blocks)
specifier|private
name|void
name|testRemoveElementsFromList
parameter_list|(
name|Random
name|r
parameter_list|,
name|CachedBlocksList
name|list
parameter_list|,
name|CachedBlock
index|[]
name|blocks
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|CachedBlock
argument_list|>
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|blocks
index|[
name|i
index|]
argument_list|,
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing via iterator"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|CachedBlock
argument_list|>
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing in pseudo-random order"
argument_list|)
expr_stmt|;
name|CachedBlock
index|[]
name|remainingBlocks
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|blocks
argument_list|,
name|blocks
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|removed
init|=
literal|0
init|;
name|removed
operator|<
name|remainingBlocks
operator|.
name|length
condition|;
control|)
block|{
name|int
name|toRemove
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|remainingBlocks
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|remainingBlocks
index|[
name|toRemove
index|]
operator|!=
literal|null
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|list
operator|.
name|remove
argument_list|(
name|remainingBlocks
index|[
name|toRemove
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|remainingBlocks
index|[
name|toRemove
index|]
operator|=
literal|null
expr_stmt|;
name|removed
operator|++
expr_stmt|;
block|}
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"expected list to be empty after everything "
operator|+
literal|"was removed."
argument_list|,
operator|!
name|list
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testMultipleLists ()
specifier|public
name|void
name|testMultipleLists
parameter_list|()
block|{
name|DatanodeDescriptor
index|[]
name|datanodes
init|=
operator|new
name|DatanodeDescriptor
index|[]
block|{
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"localhost"
argument_list|,
literal|"abcd"
argument_list|,
literal|5000
argument_list|,
literal|5001
argument_list|,
literal|5002
argument_list|,
literal|5003
argument_list|)
argument_list|)
block|,
operator|new
name|DatanodeDescriptor
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.1.1"
argument_list|,
literal|"localhost"
argument_list|,
literal|"efgh"
argument_list|,
literal|6000
argument_list|,
literal|6001
argument_list|,
literal|6002
argument_list|,
literal|6003
argument_list|)
argument_list|)
block|,     }
decl_stmt|;
name|CachedBlocksList
index|[]
name|lists
init|=
operator|new
name|CachedBlocksList
index|[]
block|{
name|datanodes
index|[
literal|0
index|]
operator|.
name|getPendingCached
argument_list|()
block|,
name|datanodes
index|[
literal|0
index|]
operator|.
name|getCached
argument_list|()
block|,
name|datanodes
index|[
literal|1
index|]
operator|.
name|getPendingCached
argument_list|()
block|,
name|datanodes
index|[
literal|1
index|]
operator|.
name|getCached
argument_list|()
block|,
name|datanodes
index|[
literal|1
index|]
operator|.
name|getPendingUncached
argument_list|()
block|,     }
decl_stmt|;
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|8000
decl_stmt|;
name|CachedBlock
index|[]
name|blocks
init|=
operator|new
name|CachedBlock
index|[
name|NUM_BLOCKS
index|]
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
name|NUM_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|CachedBlock
argument_list|(
name|i
argument_list|,
operator|(
name|short
operator|)
name|i
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|654
argument_list|)
decl_stmt|;
for|for
control|(
name|CachedBlocksList
name|list
range|:
name|lists
control|)
block|{
name|testAddElementsToList
argument_list|(
name|list
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CachedBlocksList
name|list
range|:
name|lists
control|)
block|{
name|testRemoveElementsFromList
argument_list|(
name|r
argument_list|,
name|list
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

