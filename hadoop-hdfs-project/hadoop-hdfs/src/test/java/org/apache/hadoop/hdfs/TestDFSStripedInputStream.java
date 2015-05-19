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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_DEFAULT
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
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
name|server
operator|.
name|namenode
operator|.
name|ErasureCodingSchemaManager
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
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
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
name|io
operator|.
name|erasurecode
operator|.
name|rawcoder
operator|.
name|RSRawDecoder
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
name|nio
operator|.
name|ByteBuffer
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
DECL|class|TestDFSStripedInputStream
specifier|public
class|class
name|TestDFSStripedInputStream
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
name|TestDFSStripedInputStream
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
DECL|field|schema
specifier|private
specifier|final
name|ECSchema
name|schema
init|=
name|ErasureCodingSchemaManager
operator|.
name|getSystemDefaultSchema
argument_list|()
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
DECL|field|INTERNAL_BLOCK_SIZE
specifier|private
specifier|final
name|int
name|INTERNAL_BLOCK_SIZE
init|=
name|NUM_STRIPE_PER_BLOCK
operator|*
name|CELLSIZE
decl_stmt|;
DECL|field|BLOCK_GROUP_SIZE
specifier|private
specifier|final
name|int
name|BLOCK_GROUP_SIZE
init|=
name|DATA_BLK_NUM
operator|*
name|INTERNAL_BLOCK_SIZE
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
name|INTERNAL_BLOCK_SIZE
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
name|DATA_BLK_NUM
operator|+
name|PARITY_BLK_NUM
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
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
name|dirPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
name|CELLSIZE
argument_list|)
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
literal|null
argument_list|,
name|numBlocks
argument_list|,
name|NUM_STRIPE_PER_BLOCK
argument_list|,
literal|false
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
name|BLOCK_GROUP_SIZE
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
argument_list|,
name|schema
argument_list|,
name|CELLSIZE
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
literal|2
decl_stmt|;
name|DFSTestUtil
operator|.
name|createStripedFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
literal|null
argument_list|,
name|numBlocks
argument_list|,
name|NUM_STRIPE_PER_BLOCK
argument_list|,
literal|false
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
name|BLOCK_GROUP_SIZE
operator|*
name|numBlocks
argument_list|)
decl_stmt|;
name|int
name|fileLen
init|=
name|BLOCK_GROUP_SIZE
operator|*
name|numBlocks
decl_stmt|;
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|fileLen
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|numBlocks
argument_list|,
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|bgIdx
init|=
literal|0
init|;
name|bgIdx
operator|<
name|numBlocks
condition|;
name|bgIdx
operator|++
control|)
block|{
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
name|bgIdx
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
comment|/** A variation of {@link DFSTestUtil#fillExpectedBuf} for striped blocks */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_STRIPE_PER_BLOCK
condition|;
name|i
operator|++
control|)
block|{
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
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|CELLSIZE
condition|;
name|k
operator|++
control|)
block|{
name|int
name|posInBlk
init|=
name|i
operator|*
name|CELLSIZE
operator|+
name|k
decl_stmt|;
name|int
name|posInFile
init|=
name|i
operator|*
name|CELLSIZE
operator|*
name|DATA_BLK_NUM
operator|+
name|j
operator|*
name|CELLSIZE
operator|+
name|k
decl_stmt|;
name|expected
index|[
name|bgIdx
operator|*
name|BLOCK_GROUP_SIZE
operator|+
name|posInFile
index|]
operator|=
name|SimulatedFSDataset
operator|.
name|simulatedByte
argument_list|(
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
name|j
argument_list|)
argument_list|,
name|posInBlk
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
argument_list|,
name|schema
argument_list|,
name|CELLSIZE
argument_list|)
decl_stmt|;
name|int
index|[]
name|startOffsets
init|=
block|{
literal|0
block|,
literal|1
block|,
name|CELLSIZE
operator|-
literal|102
block|,
name|CELLSIZE
block|,
name|CELLSIZE
operator|+
literal|102
block|,
name|CELLSIZE
operator|*
name|DATA_BLK_NUM
block|,
name|CELLSIZE
operator|*
name|DATA_BLK_NUM
operator|+
literal|102
block|,
name|BLOCK_GROUP_SIZE
operator|-
literal|102
block|,
name|BLOCK_GROUP_SIZE
block|,
name|BLOCK_GROUP_SIZE
operator|+
literal|102
block|,
name|fileLen
operator|-
literal|1
block|}
decl_stmt|;
for|for
control|(
name|int
name|startOffset
range|:
name|startOffsets
control|)
block|{
name|startOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|startOffset
argument_list|,
name|fileLen
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|remaining
init|=
name|fileLen
operator|-
name|startOffset
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|fileLen
index|]
decl_stmt|;
name|int
name|ret
init|=
name|in
operator|.
name|read
argument_list|(
name|startOffset
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|fileLen
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|remaining
argument_list|,
name|ret
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
name|remaining
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Byte at "
operator|+
operator|(
name|startOffset
operator|+
name|i
operator|)
operator|+
literal|" should be the "
operator|+
literal|"same"
argument_list|,
name|expected
index|[
name|startOffset
operator|+
name|i
index|]
argument_list|,
name|buf
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreadWithDNFailure ()
specifier|public
name|void
name|testPreadWithDNFailure
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
specifier|final
name|int
name|failedDNIdx
init|=
literal|2
decl_stmt|;
name|DFSTestUtil
operator|.
name|createStripedFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
literal|null
argument_list|,
name|numBlocks
argument_list|,
name|NUM_STRIPE_PER_BLOCK
argument_list|,
literal|false
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
name|BLOCK_GROUP_SIZE
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
operator|+
name|PARITY_BLK_NUM
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
argument_list|,
name|ErasureCodingSchemaManager
operator|.
name|getSystemDefaultSchema
argument_list|()
argument_list|,
name|CELLSIZE
argument_list|)
decl_stmt|;
name|int
name|readSize
init|=
name|BLOCK_GROUP_SIZE
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
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|readSize
index|]
decl_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|failedDNIdx
argument_list|)
expr_stmt|;
comment|/** A variation of {@link DFSTestUtil#fillExpectedBuf} for striped blocks */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_STRIPE_PER_BLOCK
condition|;
name|i
operator|++
control|)
block|{
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
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|CELLSIZE
condition|;
name|k
operator|++
control|)
block|{
name|int
name|posInBlk
init|=
name|i
operator|*
name|CELLSIZE
operator|+
name|k
decl_stmt|;
name|int
name|posInFile
init|=
name|i
operator|*
name|CELLSIZE
operator|*
name|DATA_BLK_NUM
operator|+
name|j
operator|*
name|CELLSIZE
operator|+
name|k
decl_stmt|;
name|expected
index|[
name|posInFile
index|]
operator|=
name|SimulatedFSDataset
operator|.
name|simulatedByte
argument_list|(
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
name|j
argument_list|)
argument_list|,
name|posInBlk
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Update the expected content for decoded data
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_STRIPE_PER_BLOCK
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
index|[]
name|decodeInputs
init|=
operator|new
name|byte
index|[
name|DATA_BLK_NUM
operator|+
name|PARITY_BLK_NUM
index|]
index|[
name|CELLSIZE
index|]
decl_stmt|;
name|int
index|[]
name|missingBlkIdx
init|=
operator|new
name|int
index|[]
block|{
name|failedDNIdx
block|,
name|DATA_BLK_NUM
operator|+
literal|1
block|,
name|DATA_BLK_NUM
operator|+
literal|2
block|}
decl_stmt|;
name|byte
index|[]
index|[]
name|decodeOutputs
init|=
operator|new
name|byte
index|[
name|PARITY_BLK_NUM
index|]
index|[
name|CELLSIZE
index|]
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
name|int
name|posInBuf
init|=
name|i
operator|*
name|CELLSIZE
operator|*
name|DATA_BLK_NUM
operator|+
name|j
operator|*
name|CELLSIZE
decl_stmt|;
if|if
condition|(
name|j
operator|!=
name|failedDNIdx
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|expected
argument_list|,
name|posInBuf
argument_list|,
name|decodeInputs
index|[
name|j
index|]
argument_list|,
literal|0
argument_list|,
name|CELLSIZE
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|CELLSIZE
condition|;
name|k
operator|++
control|)
block|{
name|int
name|posInBlk
init|=
name|i
operator|*
name|CELLSIZE
operator|+
name|k
decl_stmt|;
name|decodeInputs
index|[
name|DATA_BLK_NUM
index|]
index|[
name|k
index|]
operator|=
name|SimulatedFSDataset
operator|.
name|simulatedByte
argument_list|(
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
name|DATA_BLK_NUM
argument_list|)
argument_list|,
name|posInBlk
argument_list|)
expr_stmt|;
block|}
comment|//      RSRawDecoder rsRawDecoder = new RSRawDecoder();
comment|//      rsRawDecoder.initialize(DATA_BLK_NUM, PARITY_BLK_NUM, CELLSIZE);
comment|//      rsRawDecoder.decode(decodeInputs, missingBlkIdx, decodeOutputs);
name|int
name|posInBuf
init|=
name|i
operator|*
name|CELLSIZE
operator|*
name|DATA_BLK_NUM
operator|+
name|failedDNIdx
operator|*
name|CELLSIZE
decl_stmt|;
comment|//      System.arraycopy(decodeOutputs[0], 0, expected, posInBuf, CELLSIZE);
comment|//TODO: workaround (filling fixed bytes), to remove after HADOOP-11938
name|Arrays
operator|.
name|fill
argument_list|(
name|expected
argument_list|,
name|posInBuf
argument_list|,
name|posInBuf
operator|+
name|CELLSIZE
argument_list|,
operator|(
name|byte
operator|)
literal|7
argument_list|)
expr_stmt|;
block|}
name|int
name|delta
init|=
literal|10
decl_stmt|;
name|int
name|done
init|=
literal|0
decl_stmt|;
comment|// read a small delta, shouldn't trigger decode
comment|// |cell_0 |
comment|// |10     |
name|done
operator|+=
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
name|delta
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|delta
argument_list|,
name|done
argument_list|)
expr_stmt|;
comment|// both head and trail cells are partial
comment|// |c_0      |c_1    |c_2 |c_3 |c_4      |c_5         |
comment|// |256K - 10|missing|256K|256K|256K - 10|not in range|
name|done
operator|+=
name|in
operator|.
name|read
argument_list|(
name|delta
argument_list|,
name|readBuffer
argument_list|,
name|delta
argument_list|,
name|CELLSIZE
operator|*
operator|(
name|DATA_BLK_NUM
operator|-
literal|1
operator|)
operator|-
literal|2
operator|*
name|delta
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CELLSIZE
operator|*
operator|(
name|DATA_BLK_NUM
operator|-
literal|1
operator|)
operator|-
name|delta
argument_list|,
name|done
argument_list|)
expr_stmt|;
comment|// read the rest
name|done
operator|+=
name|in
operator|.
name|read
argument_list|(
name|done
argument_list|,
name|readBuffer
argument_list|,
name|done
argument_list|,
name|readSize
operator|-
name|done
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|readSize
argument_list|,
name|done
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|readBuffer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatefulRead ()
specifier|public
name|void
name|testStatefulRead
parameter_list|()
throws|throws
name|Exception
block|{
name|testStatefulRead
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testStatefulRead
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testStatefulRead
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testStatefulRead (boolean useByteBuffer, boolean cellMisalignPacket)
specifier|private
name|void
name|testStatefulRead
parameter_list|(
name|boolean
name|useByteBuffer
parameter_list|,
name|boolean
name|cellMisalignPacket
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numBlocks
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|fileSize
init|=
name|numBlocks
operator|*
name|BLOCK_GROUP_SIZE
decl_stmt|;
if|if
condition|(
name|cellMisalignPacket
condition|)
block|{
name|conf
operator|.
name|setInt
argument_list|(
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
name|IO_FILE_BUFFER_SIZE_DEFAULT
operator|+
literal|1
argument_list|)
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
name|setup
argument_list|()
expr_stmt|;
block|}
name|DFSTestUtil
operator|.
name|createStripedFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
literal|null
argument_list|,
name|numBlocks
argument_list|,
name|NUM_STRIPE_PER_BLOCK
argument_list|,
literal|false
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
name|fileSize
argument_list|)
decl_stmt|;
assert|assert
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|numBlocks
assert|;
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
assert|assert
name|lb
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
name|lb
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
argument_list|,
name|schema
argument_list|,
name|CELLSIZE
argument_list|)
decl_stmt|;
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|bg
range|:
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
comment|/** A variation of {@link DFSTestUtil#fillExpectedBuf} for striped blocks */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_STRIPE_PER_BLOCK
condition|;
name|i
operator|++
control|)
block|{
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
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|CELLSIZE
condition|;
name|k
operator|++
control|)
block|{
name|int
name|posInBlk
init|=
name|i
operator|*
name|CELLSIZE
operator|+
name|k
decl_stmt|;
name|int
name|posInFile
init|=
operator|(
name|int
operator|)
name|bg
operator|.
name|getStartOffset
argument_list|()
operator|+
name|i
operator|*
name|CELLSIZE
operator|*
name|DATA_BLK_NUM
operator|+
name|j
operator|*
name|CELLSIZE
operator|+
name|k
decl_stmt|;
name|expected
index|[
name|posInFile
index|]
operator|=
name|SimulatedFSDataset
operator|.
name|simulatedByte
argument_list|(
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
name|j
argument_list|)
argument_list|,
name|posInBlk
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|useByteBuffer
condition|)
block|{
name|ByteBuffer
name|readBuffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|fileSize
argument_list|)
decl_stmt|;
name|int
name|done
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|done
operator|<
name|fileSize
condition|)
block|{
name|int
name|ret
init|=
name|in
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ret
operator|>
literal|0
argument_list|)
expr_stmt|;
name|done
operator|+=
name|ret
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|readBuffer
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|byte
index|[]
name|readBuffer
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
name|int
name|done
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|done
operator|<
name|fileSize
condition|)
block|{
name|int
name|ret
init|=
name|in
operator|.
name|read
argument_list|(
name|readBuffer
argument_list|,
name|done
argument_list|,
name|fileSize
operator|-
name|done
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ret
operator|>
literal|0
argument_list|)
expr_stmt|;
name|done
operator|+=
name|ret
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|expected
argument_list|,
name|readBuffer
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

