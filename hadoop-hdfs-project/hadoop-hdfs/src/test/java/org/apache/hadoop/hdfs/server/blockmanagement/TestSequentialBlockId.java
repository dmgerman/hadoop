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
name|List
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
name|junit
operator|.
name|Test
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
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * Tests the sequential block ID generation mechanism and block ID  * collision handling.  */
end_comment

begin_class
DECL|class|TestSequentialBlockId
specifier|public
class|class
name|TestSequentialBlockId
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"TestSequentialBlockId"
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|IO_SIZE
specifier|final
name|int
name|IO_SIZE
init|=
name|BLOCK_SIZE
decl_stmt|;
DECL|field|REPLICATION
specifier|final
name|short
name|REPLICATION
init|=
literal|1
decl_stmt|;
DECL|field|SEED
specifier|final
name|long
name|SEED
init|=
literal|0
decl_stmt|;
comment|/**    * Test that block IDs are generated sequentially.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testBlockIdGeneration ()
specifier|public
name|void
name|testBlockIdGeneration
parameter_list|()
throws|throws
name|IOException
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
name|MiniDFSCluster
name|cluster
init|=
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// Create a file that is 10 blocks long.
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"testBlockIdGeneration.dat"
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
name|IO_SIZE
argument_list|,
name|BLOCK_SIZE
operator|*
literal|10
argument_list|,
name|BLOCK_SIZE
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Block0 id is "
operator|+
name|blocks
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
name|long
name|nextBlockExpectedId
init|=
name|blocks
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
operator|+
literal|1
decl_stmt|;
comment|// Ensure that the block IDs are sequentially increasing.
for|for
control|(
name|int
name|i
init|=
literal|1
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
name|long
name|nextBlockId
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
literal|"Block"
operator|+
name|i
operator|+
literal|" id is "
operator|+
name|nextBlockId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|nextBlockId
argument_list|,
name|is
argument_list|(
name|nextBlockExpectedId
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|nextBlockExpectedId
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that collisions in the block ID space are handled gracefully.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testTriggerBlockIdCollision ()
specifier|public
name|void
name|testTriggerBlockIdCollision
parameter_list|()
throws|throws
name|IOException
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
name|MiniDFSCluster
name|cluster
init|=
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
specifier|final
name|int
name|blockCount
init|=
literal|10
decl_stmt|;
comment|// Create a file with a few blocks to rev up the global block ID
comment|// counter.
name|Path
name|path1
init|=
operator|new
name|Path
argument_list|(
literal|"testBlockIdCollisionDetection_file1.dat"
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
name|IO_SIZE
argument_list|,
name|BLOCK_SIZE
operator|*
name|blockCount
argument_list|,
name|BLOCK_SIZE
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
comment|// Rewind the block ID counter in the name system object. This will result
comment|// in block ID collisions when we try to allocate new blocks.
name|SequentialBlockIdGenerator
name|blockIdGenerator
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getBlockIdManager
argument_list|()
operator|.
name|getBlockIdGenerator
argument_list|()
decl_stmt|;
name|blockIdGenerator
operator|.
name|setCurrentValue
argument_list|(
name|blockIdGenerator
operator|.
name|getCurrentValue
argument_list|()
operator|-
literal|5
argument_list|)
expr_stmt|;
comment|// Trigger collisions by creating a new file.
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
literal|"testBlockIdCollisionDetection_file2.dat"
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
name|IO_SIZE
argument_list|,
name|BLOCK_SIZE
operator|*
name|blockCount
argument_list|,
name|BLOCK_SIZE
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
name|blocks2
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|blockCount
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure that file2 block IDs start immediately after file1
name|assertThat
argument_list|(
name|blocks2
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
argument_list|,
name|is
argument_list|(
name|blocks1
operator|.
name|get
argument_list|(
literal|9
argument_list|)
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that the block type (legacy or not) can be correctly detected    * based on its generation stamp.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testBlockTypeDetection ()
specifier|public
name|void
name|testBlockTypeDetection
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Setup a mock object and stub out a few routines to
comment|// retrieve the generation stamp counters.
name|BlockIdManager
name|bid
init|=
name|mock
argument_list|(
name|BlockIdManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|long
name|maxGenStampForLegacyBlocks
init|=
literal|10000
decl_stmt|;
name|when
argument_list|(
name|bid
operator|.
name|getLegacyGenerationStampLimit
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxGenStampForLegacyBlocks
argument_list|)
expr_stmt|;
name|Block
name|legacyBlock
init|=
name|spy
argument_list|(
operator|new
name|Block
argument_list|()
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|legacyBlock
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxGenStampForLegacyBlocks
operator|/
literal|2
argument_list|)
expr_stmt|;
name|Block
name|newBlock
init|=
name|spy
argument_list|(
operator|new
name|Block
argument_list|()
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|newBlock
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxGenStampForLegacyBlocks
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Make sure that isLegacyBlock() can correctly detect
comment|// legacy and new blocks.
name|when
argument_list|(
name|bid
operator|.
name|isLegacyBlock
argument_list|(
name|any
argument_list|(
name|Block
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|bid
operator|.
name|isLegacyBlock
argument_list|(
name|legacyBlock
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bid
operator|.
name|isLegacyBlock
argument_list|(
name|newBlock
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the generation stamp for legacy and new blocks is updated    * as expected.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testGenerationStampUpdate ()
specifier|public
name|void
name|testGenerationStampUpdate
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Setup a mock object and stub out a few routines to
comment|// retrieve the generation stamp counters.
name|BlockIdManager
name|bid
init|=
name|mock
argument_list|(
name|BlockIdManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|long
name|nextLegacyGenerationStamp
init|=
literal|5000
decl_stmt|;
specifier|final
name|long
name|nextGenerationStamp
init|=
literal|20000
decl_stmt|;
name|when
argument_list|(
name|bid
operator|.
name|getNextLegacyGenerationStamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nextLegacyGenerationStamp
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|bid
operator|.
name|getNextGenerationStamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nextGenerationStamp
argument_list|)
expr_stmt|;
comment|// Make sure that the generation stamp is set correctly for both
comment|// kinds of blocks.
name|when
argument_list|(
name|bid
operator|.
name|nextGenerationStamp
argument_list|(
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|bid
operator|.
name|nextGenerationStamp
argument_list|(
literal|true
argument_list|)
argument_list|,
name|is
argument_list|(
name|nextLegacyGenerationStamp
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bid
operator|.
name|nextGenerationStamp
argument_list|(
literal|false
argument_list|)
argument_list|,
name|is
argument_list|(
name|nextGenerationStamp
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

