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
name|DFSTestUtil
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
name|blockmanagement
operator|.
name|DatanodeStorageInfo
operator|.
name|AddBlockResult
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
name|Before
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|NUM_DATA_BLOCKS
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
name|NUM_PARITY_BLOCKS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Test {@link BlockInfoStriped}  */
end_comment

begin_class
DECL|class|TestBlockInfoStriped
specifier|public
class|class
name|TestBlockInfoStriped
block|{
DECL|field|TOTAL_NUM_BLOCKS
specifier|private
specifier|static
specifier|final
name|int
name|TOTAL_NUM_BLOCKS
init|=
name|NUM_DATA_BLOCKS
operator|+
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|BASE_ID
specifier|private
specifier|static
specifier|final
name|long
name|BASE_ID
init|=
operator|-
literal|1600
decl_stmt|;
DECL|field|baseBlock
specifier|private
specifier|static
specifier|final
name|Block
name|baseBlock
init|=
operator|new
name|Block
argument_list|(
name|BASE_ID
argument_list|)
decl_stmt|;
DECL|field|info
specifier|private
name|BlockInfoStriped
name|info
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|info
operator|=
operator|new
name|BlockInfoStriped
argument_list|(
name|baseBlock
argument_list|,
name|NUM_DATA_BLOCKS
argument_list|,
name|NUM_PARITY_BLOCKS
argument_list|)
expr_stmt|;
block|}
DECL|method|createReportedBlocks (int num)
specifier|private
name|Block
index|[]
name|createReportedBlocks
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|Block
index|[]
name|blocks
init|=
operator|new
name|Block
index|[
name|num
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
name|num
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
name|Block
argument_list|(
name|BASE_ID
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|blocks
return|;
block|}
comment|/**    * Test adding storage and reported block    */
annotation|@
name|Test
DECL|method|testAddStorage ()
specifier|public
name|void
name|testAddStorage
parameter_list|()
block|{
comment|// first add NUM_DATA_BLOCKS + NUM_PARITY_BLOCKS storages, i.e., a complete
comment|// group of blocks/storages
name|DatanodeStorageInfo
index|[]
name|storageInfos
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|)
decl_stmt|;
name|Block
index|[]
name|blocks
init|=
name|createReportedBlocks
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|storageInfos
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|info
operator|.
name|addStorage
argument_list|(
name|storageInfos
index|[
name|i
index|]
argument_list|,
name|blocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|/
literal|2
operator|+
literal|1
argument_list|,
name|info
operator|.
name|numNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|i
operator|/=
literal|2
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|storageInfos
operator|.
name|length
condition|;
name|j
operator|+=
literal|2
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|info
operator|.
name|addStorage
argument_list|(
name|storageInfos
index|[
name|j
index|]
argument_list|,
name|blocks
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|+
operator|(
name|j
operator|+
literal|1
operator|)
operator|/
literal|2
argument_list|,
name|info
operator|.
name|numNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check
name|byte
index|[]
name|indices
init|=
operator|(
name|byte
index|[]
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|info
argument_list|,
literal|"indices"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|,
name|info
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|,
name|indices
operator|.
name|length
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|storageInfos
control|)
block|{
name|int
name|index
init|=
name|info
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|++
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|index
argument_list|,
name|indices
index|[
name|index
index|]
argument_list|)
expr_stmt|;
block|}
comment|// the same block is reported from the same storage twice
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|storageInfos
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|info
operator|.
name|addStorage
argument_list|(
name|storage
argument_list|,
name|blocks
index|[
name|i
operator|++
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|,
name|info
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|,
name|info
operator|.
name|numNodes
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|,
name|indices
operator|.
name|length
argument_list|)
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|storageInfos
control|)
block|{
name|int
name|index
init|=
name|info
operator|.
name|findStorageInfo
argument_list|(
name|storage
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|++
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|index
argument_list|,
name|indices
index|[
name|index
index|]
argument_list|)
expr_stmt|;
block|}
comment|// the same block is reported from another storage
name|DatanodeStorageInfo
index|[]
name|storageInfos2
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|*
literal|2
argument_list|)
decl_stmt|;
comment|// only add the second half of info2
for|for
control|(
name|i
operator|=
name|TOTAL_NUM_BLOCKS
init|;
name|i
operator|<
name|storageInfos2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|info
operator|.
name|addStorage
argument_list|(
name|storageInfos2
index|[
name|i
index|]
argument_list|,
name|blocks
index|[
name|i
operator|%
name|TOTAL_NUM_BLOCKS
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|info
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|info
operator|.
name|numNodes
argument_list|()
argument_list|)
expr_stmt|;
name|indices
operator|=
operator|(
name|byte
index|[]
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|info
argument_list|,
literal|"indices"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|indices
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|i
operator|=
name|TOTAL_NUM_BLOCKS
init|;
name|i
operator|<
name|storageInfos2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
name|info
operator|.
name|findStorageInfo
argument_list|(
name|storageInfos2
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|++
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|index
operator|-
name|TOTAL_NUM_BLOCKS
argument_list|,
name|indices
index|[
name|index
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRemoveStorage ()
specifier|public
name|void
name|testRemoveStorage
parameter_list|()
block|{
comment|// first add TOTAL_NUM_BLOCKS into the BlockInfoStriped
name|DatanodeStorageInfo
index|[]
name|storages
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|)
decl_stmt|;
name|Block
index|[]
name|blocks
init|=
name|createReportedBlocks
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|)
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
name|storages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|info
operator|.
name|addStorage
argument_list|(
name|storages
index|[
name|i
index|]
argument_list|,
name|blocks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// remove two storages
name|info
operator|.
name|removeStorage
argument_list|(
name|storages
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|info
operator|.
name|removeStorage
argument_list|(
name|storages
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// check
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|,
name|info
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|-
literal|2
argument_list|,
name|info
operator|.
name|numNodes
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|indices
init|=
operator|(
name|byte
index|[]
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|info
argument_list|,
literal|"indices"
argument_list|)
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
name|storages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
name|info
operator|.
name|findStorageInfo
argument_list|(
name|storages
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
operator|&&
name|i
operator|!=
literal|2
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|index
argument_list|,
name|indices
index|[
name|index
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|indices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// the same block is reported from another storage
name|DatanodeStorageInfo
index|[]
name|storages2
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|*
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|TOTAL_NUM_BLOCKS
init|;
name|i
operator|<
name|storages2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|info
operator|.
name|addStorage
argument_list|(
name|storages2
index|[
name|i
index|]
argument_list|,
name|blocks
index|[
name|i
operator|%
name|TOTAL_NUM_BLOCKS
index|]
argument_list|)
expr_stmt|;
block|}
comment|// now we should have 8 storages
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|*
literal|2
operator|-
literal|2
argument_list|,
name|info
operator|.
name|numNodes
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|*
literal|2
operator|-
literal|2
argument_list|,
name|info
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|indices
operator|=
operator|(
name|byte
index|[]
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|info
argument_list|,
literal|"indices"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|*
literal|2
operator|-
literal|2
argument_list|,
name|indices
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|j
init|=
name|TOTAL_NUM_BLOCKS
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|TOTAL_NUM_BLOCKS
init|;
name|i
operator|<
name|storages2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
name|info
operator|.
name|findStorageInfo
argument_list|(
name|storages2
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|TOTAL_NUM_BLOCKS
operator|||
name|i
operator|==
name|TOTAL_NUM_BLOCKS
operator|+
literal|2
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
operator|-
name|TOTAL_NUM_BLOCKS
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|j
operator|++
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
comment|// remove the storages from storages2
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|TOTAL_NUM_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
name|info
operator|.
name|removeStorage
argument_list|(
name|storages2
index|[
name|i
operator|+
name|TOTAL_NUM_BLOCKS
index|]
argument_list|)
expr_stmt|;
block|}
comment|// now we should have 3 storages
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|-
literal|2
argument_list|,
name|info
operator|.
name|numNodes
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|*
literal|2
operator|-
literal|2
argument_list|,
name|info
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|indices
operator|=
operator|(
name|byte
index|[]
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|info
argument_list|,
literal|"indices"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TOTAL_NUM_BLOCKS
operator|*
literal|2
operator|-
literal|2
argument_list|,
name|indices
operator|.
name|length
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
name|TOTAL_NUM_BLOCKS
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
operator|||
name|i
operator|==
literal|2
condition|)
block|{
name|int
name|index
init|=
name|info
operator|.
name|findStorageInfo
argument_list|(
name|storages2
index|[
name|i
operator|+
name|TOTAL_NUM_BLOCKS
index|]
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|index
init|=
name|info
operator|.
name|findStorageInfo
argument_list|(
name|storages
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|TOTAL_NUM_BLOCKS
init|;
name|i
operator|<
name|TOTAL_NUM_BLOCKS
operator|*
literal|2
operator|-
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|indices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|info
operator|.
name|getDatanode
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReplaceBlock ()
specifier|public
name|void
name|testReplaceBlock
parameter_list|()
block|{
name|DatanodeStorageInfo
index|[]
name|storages
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|)
decl_stmt|;
name|Block
index|[]
name|blocks
init|=
name|createReportedBlocks
argument_list|(
name|TOTAL_NUM_BLOCKS
argument_list|)
decl_stmt|;
comment|// add block/storage 0, 2, 4 into the BlockInfoStriped
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|storages
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|AddBlockResult
operator|.
name|ADDED
argument_list|,
name|storages
index|[
name|i
index|]
operator|.
name|addBlock
argument_list|(
name|info
argument_list|,
name|blocks
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BlockInfoStriped
name|newBlockInfo
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|info
operator|.
name|replaceBlock
argument_list|(
name|newBlockInfo
argument_list|)
expr_stmt|;
comment|// make sure the newBlockInfo is correct
name|byte
index|[]
name|indices
init|=
operator|(
name|byte
index|[]
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|newBlockInfo
argument_list|,
literal|"indices"
argument_list|)
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
name|storages
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|int
name|index
init|=
name|newBlockInfo
operator|.
name|findStorageInfo
argument_list|(
name|storages
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|index
argument_list|,
name|indices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// make sure the newBlockInfo is added to the linked list of the storage
name|Assert
operator|.
name|assertSame
argument_list|(
name|newBlockInfo
argument_list|,
name|storages
index|[
name|i
index|]
operator|.
name|getBlockListHeadForTesting
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|storages
index|[
name|i
index|]
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|newBlockInfo
operator|.
name|getNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWrite ()
specifier|public
name|void
name|testWrite
parameter_list|()
block|{
name|long
name|blkID
init|=
literal|1
decl_stmt|;
name|long
name|numBytes
init|=
literal|1
decl_stmt|;
name|long
name|generationStamp
init|=
literal|1
decl_stmt|;
name|short
name|dataBlockNum
init|=
literal|6
decl_stmt|;
name|short
name|parityBlockNum
init|=
literal|3
decl_stmt|;
name|ByteBuffer
name|byteBuffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|Long
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
operator|*
literal|3
operator|+
name|Short
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
operator|*
literal|2
argument_list|)
decl_stmt|;
name|byteBuffer
operator|.
name|putShort
argument_list|(
name|dataBlockNum
argument_list|)
operator|.
name|putShort
argument_list|(
name|parityBlockNum
argument_list|)
operator|.
name|putLong
argument_list|(
name|blkID
argument_list|)
operator|.
name|putLong
argument_list|(
name|numBytes
argument_list|)
operator|.
name|putLong
argument_list|(
name|generationStamp
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|byteStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutput
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|byteStream
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blk
init|=
operator|new
name|BlockInfoStriped
argument_list|(
operator|new
name|Block
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|6
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
decl_stmt|;
try|try
block|{
name|blk
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
literal|"testWrite error:"
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|byteBuffer
operator|.
name|array
argument_list|()
operator|.
name|length
argument_list|,
name|byteStream
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|byteBuffer
operator|.
name|array
argument_list|()
argument_list|,
name|byteStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

