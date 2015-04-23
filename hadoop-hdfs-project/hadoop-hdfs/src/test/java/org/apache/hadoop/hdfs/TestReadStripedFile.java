begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|protocol
operator|.
name|LocatedBlocks
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
name|LocatedStripedBlock
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
name|assertFalse
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
name|assertTrue
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
name|datanode
operator|.
name|SimulatedFSDataset
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
name|Arrays
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

begin_class
DECL|class|TestReadStripedFile
specifier|public
class|class
name|TestReadStripedFile
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestReadStripedFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|dirPath
specifier|private
specifier|final
name|Path
name|dirPath
init|=
operator|new
name|Path
argument_list|(
literal|"/striped"
argument_list|)
decl_stmt|;
DECL|field|filePath
specifier|private
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|dirPath
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
DECL|field|DATA_BLK_NUM
specifier|private
specifier|final
name|short
name|DATA_BLK_NUM
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|PARITY_BLK_NUM
specifier|private
specifier|final
name|short
name|PARITY_BLK_NUM
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|BLK_GROUP_SIZE
specifier|private
specifier|final
name|short
name|BLK_GROUP_SIZE
init|=
name|DATA_BLK_NUM
operator|+
name|PARITY_BLK_NUM
decl_stmt|;
DECL|field|CELLSIZE
specifier|private
specifier|final
name|int
name|CELLSIZE
init|=
name|HdfsConstants
operator|.
name|BLOCK_STRIPED_CELL_SIZE
decl_stmt|;
DECL|field|NUM_STRIPE_PER_BLOCK
specifier|private
specifier|final
name|int
name|NUM_STRIPE_PER_BLOCK
init|=
literal|2
decl_stmt|;
DECL|field|BLOCKSIZE
specifier|private
specifier|final
name|int
name|BLOCKSIZE
init|=
name|NUM_STRIPE_PER_BLOCK
operator|*
name|DATA_BLK_NUM
operator|*
name|CELLSIZE
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|BLOCKSIZE
argument_list|)
expr_stmt|;
name|SimulatedFSDataset
operator|.
name|setFactory
argument_list|(
name|conf
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
name|BLK_GROUP_SIZE
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
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
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
comment|/**    * Test {@link DFSStripedInputStream#getBlockAt(long)}    */
annotation|@
name|Test
DECL|method|testGetBlock ()
specifier|public
name|void
name|testGetBlock
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numBlocks
init|=
literal|4
decl_stmt|;
name|DFSTestUtil
operator|.
name|createStripedFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
name|dirPath
argument_list|,
name|numBlocks
argument_list|,
name|NUM_STRIPE_PER_BLOCK
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|namenode
operator|.
name|getBlockLocations
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|BLOCKSIZE
operator|*
name|numBlocks
argument_list|)
decl_stmt|;
specifier|final
name|DFSStripedInputStream
name|in
init|=
operator|new
name|DFSStripedInputStream
argument_list|(
name|fs
operator|.
name|getClient
argument_list|()
argument_list|,
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|lbList
init|=
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|aLbList
range|:
name|lbList
control|)
block|{
name|LocatedStripedBlock
name|lsb
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|aLbList
decl_stmt|;
name|LocatedBlock
index|[]
name|blks
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
name|lsb
argument_list|,
name|CELLSIZE
argument_list|,
name|DATA_BLK_NUM
argument_list|,
name|PARITY_BLK_NUM
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|DATA_BLK_NUM
condition|;
name|j
operator|++
control|)
block|{
name|LocatedBlock
name|refreshed
init|=
name|in
operator|.
name|getBlockAt
argument_list|(
name|blks
index|[
name|j
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|blks
index|[
name|j
index|]
operator|.
name|getBlock
argument_list|()
argument_list|,
name|refreshed
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blks
index|[
name|j
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|refreshed
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|blks
index|[
name|j
index|]
operator|.
name|getLocations
argument_list|()
argument_list|,
name|refreshed
operator|.
name|getLocations
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPread ()
specifier|public
name|void
name|testPread
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numBlocks
init|=
literal|4
decl_stmt|;
name|DFSTestUtil
operator|.
name|createStripedFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
name|dirPath
argument_list|,
name|numBlocks
argument_list|,
name|NUM_STRIPE_PER_BLOCK
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|namenode
operator|.
name|getBlockLocations
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|BLOCKSIZE
argument_list|)
decl_stmt|;
assert|assert
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|LocatedStripedBlock
assert|;
name|LocatedStripedBlock
name|bg
init|=
call|(
name|LocatedStripedBlock
call|)
argument_list|(
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|DATA_BLK_NUM
condition|;
name|i
operator|++
control|)
block|{
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
operator|+
name|i
argument_list|,
name|NUM_STRIPE_PER_BLOCK
operator|*
name|CELLSIZE
argument_list|,
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
decl_stmt|;
name|blk
operator|.
name|setGenerationStamp
argument_list|(
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DFSStripedInputStream
name|in
init|=
operator|new
name|DFSStripedInputStream
argument_list|(
name|fs
operator|.
name|getClient
argument_list|()
argument_list|,
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|readSize
init|=
name|BLOCKSIZE
decl_stmt|;
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
name|readSize
index|]
decl_stmt|;
name|int
name|ret
init|=
name|in
operator|.
name|read
argument_list|(
literal|0
argument_list|,
name|readBuffer
argument_list|,
literal|0
argument_list|,
name|readSize
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|readSize
argument_list|,
name|ret
argument_list|)
expr_stmt|;
comment|// TODO: verify read results with patterned data from HDFS-8117
block|}
block|}
end_class

end_unit

