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
name|fs
operator|.
name|StorageType
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
name|DatanodeInfo
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
name|ExtendedBlock
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
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSStripedInputStream
operator|.
name|ReadPortion
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
name|blockmanagement
operator|.
name|BlockIdManager
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
DECL|field|GROUP_SIZE
specifier|private
specifier|final
name|short
name|GROUP_SIZE
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|TOTAL_SIZE
specifier|private
specifier|final
name|short
name|TOTAL_SIZE
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
operator|+
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
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
literal|2
operator|*
name|GROUP_SIZE
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
name|TOTAL_SIZE
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
DECL|method|testPlanReadPortions (int startInBlk, int length, int bufferOffset, int[] readLengths, int[] offsetsInBlock, int[][] bufferOffsets, int[][] bufferLengths)
specifier|private
name|void
name|testPlanReadPortions
parameter_list|(
name|int
name|startInBlk
parameter_list|,
name|int
name|length
parameter_list|,
name|int
name|bufferOffset
parameter_list|,
name|int
index|[]
name|readLengths
parameter_list|,
name|int
index|[]
name|offsetsInBlock
parameter_list|,
name|int
index|[]
index|[]
name|bufferOffsets
parameter_list|,
name|int
index|[]
index|[]
name|bufferLengths
parameter_list|)
block|{
name|ReadPortion
index|[]
name|results
init|=
name|DFSStripedInputStream
operator|.
name|planReadPortions
argument_list|(
name|GROUP_SIZE
argument_list|,
name|CELLSIZE
argument_list|,
name|startInBlk
argument_list|,
name|length
argument_list|,
name|bufferOffset
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|GROUP_SIZE
argument_list|,
name|results
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
name|GROUP_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|readLengths
index|[
name|i
index|]
argument_list|,
name|results
index|[
name|i
index|]
operator|.
name|getReadLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|offsetsInBlock
index|[
name|i
index|]
argument_list|,
name|results
index|[
name|i
index|]
operator|.
name|getStartOffsetInBlock
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|bOffsets
init|=
name|results
index|[
name|i
index|]
operator|.
name|getOffsets
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|bufferOffsets
index|[
name|i
index|]
argument_list|,
name|bOffsets
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|bLengths
init|=
name|results
index|[
name|i
index|]
operator|.
name|getLengths
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|bufferLengths
index|[
name|i
index|]
argument_list|,
name|bLengths
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test {@link DFSStripedInputStream#planReadPortions}    */
annotation|@
name|Test
DECL|method|testPlanReadPortions ()
specifier|public
name|void
name|testPlanReadPortions
parameter_list|()
block|{
comment|/**      * start block offset is 0, read cellSize - 10      */
name|testPlanReadPortions
argument_list|(
literal|0
argument_list|,
name|CELLSIZE
operator|-
literal|10
argument_list|,
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|-
literal|10
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
literal|0
block|}
block|,
operator|new
name|int
index|[]
block|{}
block|,
operator|new
name|int
index|[]
block|{}
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|-
literal|10
block|}
block|,
operator|new
name|int
index|[]
block|{}
block|,
operator|new
name|int
index|[]
block|{}
block|}
argument_list|)
expr_stmt|;
comment|/**      * start block offset is 0, read 3 * cellSize      */
name|testPlanReadPortions
argument_list|(
literal|0
argument_list|,
name|GROUP_SIZE
operator|*
name|CELLSIZE
argument_list|,
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
block|,
name|CELLSIZE
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
literal|0
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|*
literal|2
block|}
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|}
argument_list|)
expr_stmt|;
comment|/**      * start block offset is 0, read cellSize + 10      */
name|testPlanReadPortions
argument_list|(
literal|0
argument_list|,
name|CELLSIZE
operator|+
literal|10
argument_list|,
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
literal|10
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
literal|0
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{}
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|10
block|}
block|,
operator|new
name|int
index|[]
block|{}
block|}
argument_list|)
expr_stmt|;
comment|/**      * start block offset is 0, read 5 * cellSize + 10, buffer start offset is 100      */
name|testPlanReadPortions
argument_list|(
literal|0
argument_list|,
literal|5
operator|*
name|CELLSIZE
operator|+
literal|10
argument_list|,
literal|100
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|*
literal|2
block|,
name|CELLSIZE
operator|*
literal|2
block|,
name|CELLSIZE
operator|+
literal|10
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
literal|100
block|,
literal|100
operator|+
name|CELLSIZE
operator|*
name|GROUP_SIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|100
operator|+
name|CELLSIZE
block|,
literal|100
operator|+
name|CELLSIZE
operator|*
literal|4
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|100
operator|+
name|CELLSIZE
operator|*
literal|2
block|,
literal|100
operator|+
name|CELLSIZE
operator|*
literal|5
block|}
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
literal|10
block|}
block|}
argument_list|)
expr_stmt|;
comment|/**      * start block offset is 2, read 3 * cellSize      */
name|testPlanReadPortions
argument_list|(
literal|2
argument_list|,
name|GROUP_SIZE
operator|*
name|CELLSIZE
argument_list|,
literal|100
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
block|,
name|CELLSIZE
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
literal|100
block|,
literal|100
operator|+
name|GROUP_SIZE
operator|*
name|CELLSIZE
operator|-
literal|2
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|100
operator|+
name|CELLSIZE
operator|-
literal|2
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|100
operator|+
name|CELLSIZE
operator|*
literal|2
operator|-
literal|2
block|}
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|-
literal|2
block|,
literal|2
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|}
argument_list|)
expr_stmt|;
comment|/**      * start block offset is 2, read 3 * cellSize + 10      */
name|testPlanReadPortions
argument_list|(
literal|2
argument_list|,
name|GROUP_SIZE
operator|*
name|CELLSIZE
operator|+
literal|10
argument_list|,
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|+
literal|10
block|,
name|CELLSIZE
block|,
name|CELLSIZE
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
literal|0
block|,
name|GROUP_SIZE
operator|*
name|CELLSIZE
operator|-
literal|2
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|-
literal|2
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|*
literal|2
operator|-
literal|2
block|}
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|-
literal|2
block|,
literal|12
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|}
block|}
argument_list|)
expr_stmt|;
comment|/**      * start block offset is cellSize * 2 - 1, read 5 * cellSize + 10      */
name|testPlanReadPortions
argument_list|(
name|CELLSIZE
operator|*
literal|2
operator|-
literal|1
argument_list|,
literal|5
operator|*
name|CELLSIZE
operator|+
literal|10
argument_list|,
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|*
literal|2
block|,
name|CELLSIZE
operator|+
literal|10
block|,
name|CELLSIZE
operator|*
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
operator|-
literal|1
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|+
literal|1
block|,
literal|4
operator|*
name|CELLSIZE
operator|+
literal|1
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
operator|*
name|CELLSIZE
operator|+
literal|1
block|,
literal|5
operator|*
name|CELLSIZE
operator|+
literal|1
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
operator|*
name|CELLSIZE
operator|+
literal|1
block|}
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
name|CELLSIZE
block|,
literal|9
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
block|}
block|}
argument_list|)
expr_stmt|;
comment|/**      * start block offset is cellSize * 6 - 1, read 7 * cellSize + 10      */
name|testPlanReadPortions
argument_list|(
name|CELLSIZE
operator|*
literal|6
operator|-
literal|1
argument_list|,
literal|7
operator|*
name|CELLSIZE
operator|+
literal|10
argument_list|,
literal|0
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|*
literal|3
block|,
name|CELLSIZE
operator|*
literal|2
operator|+
literal|9
block|,
name|CELLSIZE
operator|*
literal|2
operator|+
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|*
literal|2
block|,
name|CELLSIZE
operator|*
literal|2
block|,
name|CELLSIZE
operator|*
literal|2
operator|-
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
operator|*
name|CELLSIZE
operator|+
literal|1
block|,
literal|6
operator|*
name|CELLSIZE
operator|+
literal|1
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
operator|+
literal|1
block|,
literal|4
operator|*
name|CELLSIZE
operator|+
literal|1
block|,
literal|7
operator|*
name|CELLSIZE
operator|+
literal|1
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
operator|*
name|CELLSIZE
operator|+
literal|1
block|,
literal|5
operator|*
name|CELLSIZE
operator|+
literal|1
block|}
block|}
argument_list|,
operator|new
name|int
index|[]
index|[]
block|{
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
block|,
name|CELLSIZE
block|}
block|,
operator|new
name|int
index|[]
block|{
name|CELLSIZE
block|,
name|CELLSIZE
block|,
literal|9
block|}
block|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
name|CELLSIZE
block|,
name|CELLSIZE
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|createDummyLocatedBlock ()
specifier|private
name|LocatedStripedBlock
name|createDummyLocatedBlock
parameter_list|()
block|{
specifier|final
name|long
name|blockGroupID
init|=
operator|-
literal|1048576
decl_stmt|;
name|DatanodeInfo
index|[]
name|locs
init|=
operator|new
name|DatanodeInfo
index|[
name|TOTAL_SIZE
index|]
decl_stmt|;
name|String
index|[]
name|storageIDs
init|=
operator|new
name|String
index|[
name|TOTAL_SIZE
index|]
decl_stmt|;
name|StorageType
index|[]
name|storageTypes
init|=
operator|new
name|StorageType
index|[
name|TOTAL_SIZE
index|]
decl_stmt|;
name|int
index|[]
name|indices
init|=
operator|new
name|int
index|[
name|TOTAL_SIZE
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
name|TOTAL_SIZE
condition|;
name|i
operator|++
control|)
block|{
name|locs
index|[
name|i
index|]
operator|=
operator|new
name|DatanodeInfo
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
name|storageIDs
index|[
name|i
index|]
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeUuid
argument_list|()
expr_stmt|;
name|storageTypes
index|[
name|i
index|]
operator|=
name|StorageType
operator|.
name|DISK
expr_stmt|;
name|indices
index|[
name|i
index|]
operator|=
operator|(
name|i
operator|+
literal|2
operator|)
operator|%
name|GROUP_SIZE
expr_stmt|;
block|}
return|return
operator|new
name|LocatedStripedBlock
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
literal|"pool"
argument_list|,
name|blockGroupID
argument_list|)
argument_list|,
name|locs
argument_list|,
name|storageIDs
argument_list|,
name|storageTypes
argument_list|,
name|indices
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testParseDummyStripedBlock ()
specifier|public
name|void
name|testParseDummyStripedBlock
parameter_list|()
block|{
name|LocatedStripedBlock
name|lsb
init|=
name|createDummyLocatedBlock
argument_list|()
decl_stmt|;
name|LocatedBlock
index|[]
name|blocks
init|=
name|DFSStripedInputStream
operator|.
name|parseStripedBlockGroup
argument_list|(
name|lsb
argument_list|,
name|GROUP_SIZE
argument_list|,
name|CELLSIZE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|GROUP_SIZE
argument_list|,
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|GROUP_SIZE
condition|;
name|j
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|blocks
index|[
name|j
index|]
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
argument_list|,
name|BlockIdManager
operator|.
name|getBlockIndex
argument_list|(
name|blocks
index|[
name|j
index|]
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
operator|*
name|CELLSIZE
argument_list|,
name|blocks
index|[
name|j
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testParseStripedBlock ()
specifier|public
name|void
name|testParseStripedBlock
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
name|createECFile
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
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|lbs
operator|.
name|locatedBlockCount
argument_list|()
argument_list|)
expr_stmt|;
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
name|lb
range|:
name|lbList
control|)
block|{
name|assertTrue
argument_list|(
name|lb
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numBlocks
condition|;
name|i
operator|++
control|)
block|{
name|LocatedStripedBlock
name|lsb
init|=
call|(
name|LocatedStripedBlock
call|)
argument_list|(
name|lbs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|LocatedBlock
index|[]
name|blks
init|=
name|DFSStripedInputStream
operator|.
name|parseStripedBlockGroup
argument_list|(
name|lsb
argument_list|,
name|GROUP_SIZE
argument_list|,
name|CELLSIZE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|GROUP_SIZE
argument_list|,
name|blks
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|GROUP_SIZE
condition|;
name|j
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|blks
index|[
name|j
index|]
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|j
argument_list|,
name|BlockIdManager
operator|.
name|getBlockIndex
argument_list|(
name|blks
index|[
name|j
index|]
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
operator|*
name|BLOCKSIZE
operator|+
name|j
operator|*
name|CELLSIZE
argument_list|,
name|blks
index|[
name|j
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|createECFile
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
name|DFSStripedInputStream
operator|.
name|parseStripedBlockGroup
argument_list|(
name|lsb
argument_list|,
name|GROUP_SIZE
argument_list|,
name|CELLSIZE
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
name|GROUP_SIZE
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
name|createECFile
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
name|GROUP_SIZE
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
name|BLOCKSIZE
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
name|in
operator|.
name|setCellSize
argument_list|(
name|CELLSIZE
argument_list|)
expr_stmt|;
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

