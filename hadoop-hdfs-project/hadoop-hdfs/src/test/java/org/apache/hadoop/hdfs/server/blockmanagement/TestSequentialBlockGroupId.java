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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|BLOCK_GROUP_INDEX_MASK
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|MAX_BLOCKS_IN_GROUP
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
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
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doAnswer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|spy
import|;
end_import

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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|DFSConfigKeys
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|HdfsConstants
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
name|LocatedBlock
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
name|FSNamesystem
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_comment
comment|/**  * Tests the sequential blockGroup ID generation mechanism and blockGroup ID  * collision handling.  */
end_comment

begin_class
DECL|class|TestSequentialBlockGroupId
specifier|public
class|class
name|TestSequentialBlockGroupId
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"TestSequentialBlockGroupId"
argument_list|)
decl_stmt|;
DECL|field|REPLICATION
specifier|private
specifier|final
name|short
name|REPLICATION
init|=
literal|1
decl_stmt|;
DECL|field|SEED
specifier|private
specifier|final
name|long
name|SEED
init|=
literal|0
decl_stmt|;
DECL|field|dataBlocks
specifier|private
specifier|final
name|int
name|dataBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|parityBlocks
specifier|private
specifier|final
name|int
name|parityBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|stripesPerBlock
specifier|private
specifier|final
name|int
name|stripesPerBlock
init|=
literal|2
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
init|=
name|cellSize
operator|*
name|stripesPerBlock
decl_stmt|;
DECL|field|numDNs
specifier|private
specifier|final
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
DECL|field|blockGrpCount
specifier|private
specifier|final
name|int
name|blockGrpCount
init|=
literal|4
decl_stmt|;
DECL|field|fileLen
specifier|private
specifier|final
name|int
name|fileLen
init|=
name|blockSize
operator|*
name|dataBlocks
operator|*
name|blockGrpCount
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|blockGrpIdGenerator
specifier|private
name|SequentialBlockGroupIdGenerator
name|blockGrpIdGenerator
decl_stmt|;
DECL|field|eczone
specifier|private
name|Path
name|eczone
init|=
operator|new
name|Path
argument_list|(
literal|"/eczone"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDNs
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|blockGrpIdGenerator
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockIdManager
argument_list|()
operator|.
name|getBlockGroupIdGenerator
argument_list|()
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|eczone
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
literal|"/eczone"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that blockGroup IDs are generating unique value.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testBlockGroupIdGeneration ()
specifier|public
name|void
name|testBlockGroupIdGeneration
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|blockGroupIdInitialValue
init|=
name|blockGrpIdGenerator
operator|.
name|getCurrentValue
argument_list|()
decl_stmt|;
comment|// Create a file that is 4 blocks long.
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|eczone
argument_list|,
literal|"testBlockGrpIdGeneration.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|cellSize
argument_list|,
name|fileLen
argument_list|,
name|blockSize
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks
init|=
name|DFSTestUtil
operator|.
name|getAllBlocks
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"Wrong BlockGrps"
argument_list|,
name|blocks
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|blockGrpCount
argument_list|)
argument_list|)
expr_stmt|;
comment|// initialising the block group generator for verifying the block id
name|blockGrpIdGenerator
operator|.
name|setCurrentValue
argument_list|(
name|blockGroupIdInitialValue
argument_list|)
expr_stmt|;
comment|// Ensure that the block IDs are generating unique value.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blocks
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|blockGrpIdGenerator
operator|.
name|skipTo
argument_list|(
operator|(
name|blockGrpIdGenerator
operator|.
name|getCurrentValue
argument_list|()
operator|&
operator|~
name|BLOCK_GROUP_INDEX_MASK
operator|)
operator|+
name|MAX_BLOCKS_IN_GROUP
argument_list|)
expr_stmt|;
name|long
name|nextBlockExpectedId
init|=
name|blockGrpIdGenerator
operator|.
name|getCurrentValue
argument_list|()
decl_stmt|;
name|long
name|nextBlockGrpId
init|=
name|blocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"BlockGrp"
operator|+
name|i
operator|+
literal|" id is "
operator|+
name|nextBlockGrpId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"BlockGrpId mismatches!"
argument_list|,
name|nextBlockGrpId
argument_list|,
name|is
argument_list|(
name|nextBlockExpectedId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that collisions in the blockGroup ID space are handled gracefully.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testTriggerBlockGroupIdCollision ()
specifier|public
name|void
name|testTriggerBlockGroupIdCollision
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|blockGroupIdInitialValue
init|=
name|blockGrpIdGenerator
operator|.
name|getCurrentValue
argument_list|()
decl_stmt|;
comment|// Create a file with a few blocks to rev up the global block ID
comment|// counter.
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
name|eczone
argument_list|,
literal|"testBlockGrpIdCollisionDetection_file1.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|path1
argument_list|,
name|cellSize
argument_list|,
name|fileLen
argument_list|,
name|blockSize
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks1
init|=
name|DFSTestUtil
operator|.
name|getAllBlocks
argument_list|(
name|fs
argument_list|,
name|path1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"Wrong BlockGrps"
argument_list|,
name|blocks1
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|blockGrpCount
argument_list|)
argument_list|)
expr_stmt|;
comment|// Rewind the block ID counter in the name system object. This will result
comment|// in block ID collisions when we try to allocate new blocks.
name|blockGrpIdGenerator
operator|.
name|setCurrentValue
argument_list|(
name|blockGroupIdInitialValue
argument_list|)
expr_stmt|;
comment|// Trigger collisions by creating a new file.
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
name|eczone
argument_list|,
literal|"testBlockGrpIdCollisionDetection_file2.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|path2
argument_list|,
name|cellSize
argument_list|,
name|fileLen
argument_list|,
name|blockSize
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks2
init|=
name|DFSTestUtil
operator|.
name|getAllBlocks
argument_list|(
name|fs
argument_list|,
name|path2
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"Wrong BlockGrps"
argument_list|,
name|blocks2
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|blockGrpCount
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure that file1 and file2 block IDs are different
for|for
control|(
name|LocatedBlock
name|locBlock1
range|:
name|blocks1
control|)
block|{
name|long
name|blockId1
init|=
name|locBlock1
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|locBlock2
range|:
name|blocks2
control|)
block|{
name|long
name|blockId2
init|=
name|locBlock2
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"BlockGrpId mismatches!"
argument_list|,
name|blockId1
argument_list|,
name|is
argument_list|(
name|not
argument_list|(
name|blockId2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test that collisions in the blockGroup ID when the id is occupied by legacy    * block.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testTriggerBlockGroupIdCollisionWithLegacyBlockId ()
specifier|public
name|void
name|testTriggerBlockGroupIdCollisionWithLegacyBlockId
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|blockGroupIdInitialValue
init|=
name|blockGrpIdGenerator
operator|.
name|getCurrentValue
argument_list|()
decl_stmt|;
name|blockGrpIdGenerator
operator|.
name|skipTo
argument_list|(
operator|(
name|blockGrpIdGenerator
operator|.
name|getCurrentValue
argument_list|()
operator|&
operator|~
name|BLOCK_GROUP_INDEX_MASK
operator|)
operator|+
name|MAX_BLOCKS_IN_GROUP
argument_list|)
expr_stmt|;
specifier|final
name|long
name|curBlockGroupIdValue
init|=
name|blockGrpIdGenerator
operator|.
name|getCurrentValue
argument_list|()
decl_stmt|;
comment|// Creates contiguous block with negative blockId so that it would trigger
comment|// collision during blockGroup Id generation
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
comment|// Replace SequentialBlockIdGenerator with a spy
name|SequentialBlockIdGenerator
name|blockIdGenerator
init|=
name|spy
argument_list|(
name|fsn
operator|.
name|getBlockIdManager
argument_list|()
operator|.
name|getBlockIdGenerator
argument_list|()
argument_list|)
decl_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|fsn
operator|.
name|getBlockIdManager
argument_list|()
argument_list|,
literal|"blockIdGenerator"
argument_list|,
name|blockIdGenerator
argument_list|)
expr_stmt|;
name|SequentialBlockIdGenerator
name|spySequentialBlockIdGenerator
init|=
operator|new
name|SequentialBlockIdGenerator
argument_list|(
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
return|return
name|curBlockGroupIdValue
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|Answer
argument_list|<
name|Object
argument_list|>
name|delegator
init|=
operator|new
name|GenericTestUtils
operator|.
name|DelegateAnswer
argument_list|(
name|spySequentialBlockIdGenerator
argument_list|)
decl_stmt|;
name|doAnswer
argument_list|(
name|delegator
argument_list|)
operator|.
name|when
argument_list|(
name|blockIdGenerator
argument_list|)
operator|.
name|nextValue
argument_list|()
expr_stmt|;
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"/testCollisionWithLegacyBlock_file1.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|path1
argument_list|,
literal|1024
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|contiguousBlocks
init|=
name|DFSTestUtil
operator|.
name|getAllBlocks
argument_list|(
name|fs
argument_list|,
name|path1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|contiguousBlocks
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unexpected BlockId!"
argument_list|,
name|curBlockGroupIdValue
argument_list|,
name|contiguousBlocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reset back to the initial value to trigger collision
name|blockGrpIdGenerator
operator|.
name|setCurrentValue
argument_list|(
name|blockGroupIdInitialValue
argument_list|)
expr_stmt|;
comment|// Trigger collisions by creating a new file.
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
name|eczone
argument_list|,
literal|"testCollisionWithLegacyBlock_file2.dat"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|path2
argument_list|,
name|cellSize
argument_list|,
name|fileLen
argument_list|,
name|blockSize
argument_list|,
name|REPLICATION
argument_list|,
name|SEED
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks2
init|=
name|DFSTestUtil
operator|.
name|getAllBlocks
argument_list|(
name|fs
argument_list|,
name|path2
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"Wrong BlockGrps"
argument_list|,
name|blocks2
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|blockGrpCount
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure that file1 and file2 block IDs are different
for|for
control|(
name|LocatedBlock
name|locBlock1
range|:
name|contiguousBlocks
control|)
block|{
name|long
name|blockId1
init|=
name|locBlock1
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|locBlock2
range|:
name|blocks2
control|)
block|{
name|long
name|blockId2
init|=
name|locBlock2
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"BlockGrpId mismatches!"
argument_list|,
name|blockId1
argument_list|,
name|is
argument_list|(
name|not
argument_list|(
name|blockId2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

