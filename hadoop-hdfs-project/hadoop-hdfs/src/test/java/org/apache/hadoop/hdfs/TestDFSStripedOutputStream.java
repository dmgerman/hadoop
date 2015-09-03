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
name|ArrayList
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
name|FileStatus
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
name|CodecUtil
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
name|RawErasureEncoder
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
name|apache
operator|.
name|log4j
operator|.
name|Level
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

begin_class
DECL|class|TestDFSStripedOutputStream
specifier|public
class|class
name|TestDFSStripedOutputStream
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
name|TestDFSStripedOutputStream
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DFSOutputStream
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DataStreamer
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|dataBlocks
specifier|private
name|int
name|dataBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_DATA_BLOCKS
decl_stmt|;
DECL|field|parityBlocks
specifier|private
name|int
name|parityBlocks
init|=
name|HdfsConstants
operator|.
name|NUM_PARITY_BLOCKS
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
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
literal|4
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
name|int
name|numDNs
init|=
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
decl_stmt|;
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
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
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY
argument_list|,
literal|0
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
name|getFileSystem
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|createErasureCodingZone
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
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
annotation|@
name|Test
DECL|method|testFileEmpty ()
specifier|public
name|void
name|testFileEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/EmptyFile"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneCell1 ()
specifier|public
name|void
name|testFileSmallerThanOneCell1
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneCell"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneCell2 ()
specifier|public
name|void
name|testFileSmallerThanOneCell2
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneCell"
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileEqualsWithOneCell ()
specifier|public
name|void
name|testFileEqualsWithOneCell
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/EqualsWithOneCell"
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneStripe1 ()
specifier|public
name|void
name|testFileSmallerThanOneStripe1
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileSmallerThanOneStripe2 ()
specifier|public
name|void
name|testFileSmallerThanOneStripe2
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileEqualsWithOneStripe ()
specifier|public
name|void
name|testFileEqualsWithOneStripe
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/EqualsWithOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanOneStripe1 ()
specifier|public
name|void
name|testFileMoreThanOneStripe1
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanOneStripe1"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanOneStripe2 ()
specifier|public
name|void
name|testFileMoreThanOneStripe2
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanOneStripe2"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileLessThanFullBlockGroup ()
specifier|public
name|void
name|testFileLessThanFullBlockGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/LessThanFullBlockGroup"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|*
operator|(
name|stripesPerBlock
operator|-
literal|1
operator|)
operator|+
name|cellSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileFullBlockGroup ()
specifier|public
name|void
name|testFileFullBlockGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/FullBlockGroup"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup1 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup1
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup1"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup2 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup2
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup2"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileMoreThanABlockGroup3 ()
specifier|public
name|void
name|testFileMoreThanABlockGroup3
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFile
argument_list|(
literal|"/MoreThanABlockGroup3"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|3
operator|+
name|cellSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
block|}
DECL|method|generateBytes (int cnt)
specifier|private
name|byte
index|[]
name|generateBytes
parameter_list|(
name|int
name|cnt
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|cnt
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|bytes
index|[
name|i
index|]
operator|=
name|getByte
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
DECL|method|getByte (long pos)
specifier|private
name|byte
name|getByte
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
name|int
name|mod
init|=
literal|29
decl_stmt|;
return|return
call|(
name|byte
call|)
argument_list|(
name|pos
operator|%
name|mod
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|testOneFile (String src, int writeBytes)
specifier|private
name|void
name|testOneFile
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|writeBytes
parameter_list|)
throws|throws
name|Exception
block|{
name|src
operator|+=
literal|"_"
operator|+
name|writeBytes
expr_stmt|;
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|generateBytes
argument_list|(
name|writeBytes
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|testPath
argument_list|,
operator|new
name|String
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|waitBlockGroupsReported
argument_list|(
name|fs
argument_list|,
name|src
argument_list|)
expr_stmt|;
comment|// check file length
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|writeBytes
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|checkData
argument_list|(
name|src
argument_list|,
name|writeBytes
argument_list|)
expr_stmt|;
block|}
DECL|method|checkData (String src, int writeBytes)
name|void
name|checkData
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|writeBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|List
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|>
name|blockGroupList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|src
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|firstBlock
range|:
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|firstBlock
operator|instanceof
name|LocatedStripedBlock
argument_list|)
expr_stmt|;
name|LocatedBlock
index|[]
name|blocks
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
operator|(
name|LocatedStripedBlock
operator|)
name|firstBlock
argument_list|,
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|parityBlocks
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|oneGroup
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|blocks
argument_list|)
decl_stmt|;
name|blockGroupList
operator|.
name|add
argument_list|(
name|oneGroup
argument_list|)
expr_stmt|;
block|}
comment|// test each block group
for|for
control|(
name|int
name|group
init|=
literal|0
init|;
name|group
operator|<
name|blockGroupList
operator|.
name|size
argument_list|()
condition|;
name|group
operator|++
control|)
block|{
comment|//get the data of this block
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blockList
init|=
name|blockGroupList
operator|.
name|get
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|dataBlockBytes
init|=
operator|new
name|byte
index|[
name|dataBlocks
index|]
index|[]
decl_stmt|;
name|byte
index|[]
index|[]
name|parityBlockBytes
init|=
operator|new
name|byte
index|[
name|parityBlocks
index|]
index|[]
decl_stmt|;
comment|// for each block, use BlockReader to read data
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|LocatedBlock
name|lblock
init|=
name|blockList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|lblock
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ExtendedBlock
name|block
init|=
name|lblock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
name|byte
index|[]
name|blockBytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|block
operator|.
name|getNumBytes
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|<
name|dataBlocks
condition|)
block|{
name|dataBlockBytes
index|[
name|i
index|]
operator|=
name|blockBytes
expr_stmt|;
block|}
else|else
block|{
name|parityBlockBytes
index|[
name|i
operator|-
name|dataBlocks
index|]
operator|=
name|blockBytes
expr_stmt|;
block|}
if|if
condition|(
name|block
operator|.
name|getNumBytes
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
specifier|final
name|BlockReader
name|blockReader
init|=
name|BlockReaderTestUtil
operator|.
name|getBlockReader
argument_list|(
name|fs
argument_list|,
name|lblock
argument_list|,
literal|0
argument_list|,
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
decl_stmt|;
name|blockReader
operator|.
name|readAll
argument_list|(
name|blockBytes
argument_list|,
literal|0
argument_list|,
operator|(
name|int
operator|)
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|blockReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// check if we write the data correctly
for|for
control|(
name|int
name|blkIdxInGroup
init|=
literal|0
init|;
name|blkIdxInGroup
operator|<
name|dataBlockBytes
operator|.
name|length
condition|;
name|blkIdxInGroup
operator|++
control|)
block|{
specifier|final
name|byte
index|[]
name|actualBlkBytes
init|=
name|dataBlockBytes
index|[
name|blkIdxInGroup
index|]
decl_stmt|;
if|if
condition|(
name|actualBlkBytes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|int
name|posInBlk
init|=
literal|0
init|;
name|posInBlk
operator|<
name|actualBlkBytes
operator|.
name|length
condition|;
name|posInBlk
operator|++
control|)
block|{
comment|// calculate the position of this byte in the file
name|long
name|posInFile
init|=
name|StripedBlockUtil
operator|.
name|offsetInBlkToOffsetInBG
argument_list|(
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|posInBlk
argument_list|,
name|blkIdxInGroup
argument_list|)
operator|+
name|group
operator|*
name|blockSize
operator|*
name|dataBlocks
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|posInFile
operator|<
name|writeBytes
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|expected
init|=
name|getByte
argument_list|(
name|posInFile
argument_list|)
decl_stmt|;
name|String
name|s
init|=
literal|"Unexpected byte "
operator|+
name|actualBlkBytes
index|[
name|posInBlk
index|]
operator|+
literal|", expect "
operator|+
name|expected
operator|+
literal|". Block group index is "
operator|+
name|group
operator|+
literal|", stripe index is "
operator|+
name|posInBlk
operator|/
name|cellSize
operator|+
literal|", cell index is "
operator|+
name|blkIdxInGroup
operator|+
literal|", byte index is "
operator|+
name|posInBlk
operator|%
name|cellSize
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s
argument_list|,
name|expected
argument_list|,
name|actualBlkBytes
index|[
name|posInBlk
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|verifyParity
argument_list|(
name|lbs
operator|.
name|getLocatedBlocks
argument_list|()
operator|.
name|get
argument_list|(
name|group
argument_list|)
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|cellSize
argument_list|,
name|dataBlockBytes
argument_list|,
name|parityBlockBytes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyParity (final long size, final int cellSize, byte[][] dataBytes, byte[][] parityBytes)
name|void
name|verifyParity
parameter_list|(
specifier|final
name|long
name|size
parameter_list|,
specifier|final
name|int
name|cellSize
parameter_list|,
name|byte
index|[]
index|[]
name|dataBytes
parameter_list|,
name|byte
index|[]
index|[]
name|parityBytes
parameter_list|)
block|{
name|verifyParity
argument_list|(
name|conf
argument_list|,
name|size
argument_list|,
name|cellSize
argument_list|,
name|dataBytes
argument_list|,
name|parityBytes
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyParity (Configuration conf, final long size, final int cellSize, byte[][] dataBytes, byte[][] parityBytes, int killedDnIndex)
specifier|static
name|void
name|verifyParity
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|long
name|size
parameter_list|,
specifier|final
name|int
name|cellSize
parameter_list|,
name|byte
index|[]
index|[]
name|dataBytes
parameter_list|,
name|byte
index|[]
index|[]
name|parityBytes
parameter_list|,
name|int
name|killedDnIndex
parameter_list|)
block|{
comment|// verify the parity blocks
name|int
name|parityBlkSize
init|=
operator|(
name|int
operator|)
name|StripedBlockUtil
operator|.
name|getInternalBlockLength
argument_list|(
name|size
argument_list|,
name|cellSize
argument_list|,
name|dataBytes
operator|.
name|length
argument_list|,
name|dataBytes
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
index|[]
name|expectedParityBytes
init|=
operator|new
name|byte
index|[
name|parityBytes
operator|.
name|length
index|]
index|[]
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
name|parityBytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|expectedParityBytes
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|parityBlkSize
index|]
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
name|dataBytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|dataBytes
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|dataBytes
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|dataBytes
index|[
literal|0
index|]
operator|.
name|length
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dataBytes
index|[
name|i
index|]
operator|.
name|length
operator|<
name|dataBytes
index|[
literal|0
index|]
operator|.
name|length
condition|)
block|{
specifier|final
name|byte
index|[]
name|tmp
init|=
name|dataBytes
index|[
name|i
index|]
decl_stmt|;
name|dataBytes
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|dataBytes
index|[
literal|0
index|]
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|dataBytes
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|tmp
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RawErasureEncoder
name|encoder
init|=
name|CodecUtil
operator|.
name|createRSRawEncoder
argument_list|(
name|conf
argument_list|,
name|dataBytes
operator|.
name|length
argument_list|,
name|parityBytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|encoder
operator|.
name|encode
argument_list|(
name|dataBytes
argument_list|,
name|expectedParityBytes
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
name|parityBytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
name|killedDnIndex
condition|)
block|{
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
literal|"i="
operator|+
name|i
operator|+
literal|", killedDnIndex="
operator|+
name|killedDnIndex
argument_list|,
name|expectedParityBytes
index|[
name|i
index|]
argument_list|,
name|parityBytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

