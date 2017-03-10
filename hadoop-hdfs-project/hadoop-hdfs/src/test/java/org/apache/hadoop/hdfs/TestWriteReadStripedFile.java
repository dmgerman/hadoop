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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|BlockLocation
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
name|protocol
operator|.
name|ErasureCodingPolicy
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
name|BlockPlacementPolicy
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
name|DataNode
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
name|web
operator|.
name|WebHdfsConstants
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
name|web
operator|.
name|WebHdfsTestUtil
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
name|ipc
operator|.
name|RemoteException
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|Random
import|;
end_import

begin_class
DECL|class|TestWriteReadStripedFile
specifier|public
class|class
name|TestWriteReadStripedFile
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
name|TestWriteReadStripedFile
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ecPolicy
specifier|private
specifier|final
name|ErasureCodingPolicy
name|ecPolicy
init|=
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
decl_stmt|;
DECL|field|cellSize
specifier|private
specifier|final
name|int
name|cellSize
init|=
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
decl_stmt|;
DECL|field|dataBlocks
specifier|private
specifier|final
name|short
name|dataBlocks
init|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
decl_stmt|;
DECL|field|parityBlocks
specifier|private
specifier|final
name|short
name|parityBlocks
init|=
operator|(
name|short
operator|)
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
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
name|stripesPerBlock
operator|*
name|cellSize
decl_stmt|;
DECL|field|blockGroupSize
specifier|private
specifier|final
name|int
name|blockGroupSize
init|=
name|blockSize
operator|*
name|dataBlocks
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
init|=
operator|new
name|HdfsConfiguration
argument_list|()
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
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|DFSClient
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Log4JLogger
operator|)
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BlockPlacementPolicy
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Rule
DECL|field|globalTimeout
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
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
name|blockSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY
argument_list|,
literal|false
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
operator|new
name|Path
argument_list|(
literal|"/ec"
argument_list|)
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
name|setErasureCodingPolicy
argument_list|(
literal|"/ec"
argument_list|,
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
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
throws|throws
name|IOException
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
name|cluster
operator|=
literal|null
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/EmptyFile"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/EmptyFile2"
argument_list|,
literal|0
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/SmallerThanOneCell"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/SmallerThanOneCell2"
argument_list|,
literal|1
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/SmallerThanOneCell"
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/SmallerThanOneCell2"
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/EqualsWithOneCell"
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/EqualsWithOneCell2"
argument_list|,
name|cellSize
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|-
literal|1
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/SmallerThanOneStripe2"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|-
literal|1
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/SmallerThanOneStripe"
argument_list|,
name|cellSize
operator|+
literal|123
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/SmallerThanOneStripe2"
argument_list|,
name|cellSize
operator|+
literal|123
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/EqualsWithOneStripe"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/EqualsWithOneStripe2"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanOneStripe1"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanOneStripe12"
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanOneStripe2"
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanOneStripe22"
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
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLessThanFullBlockGroup ()
specifier|public
name|void
name|testLessThanFullBlockGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/LessThanFullBlockGroup"
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/LessThanFullBlockGroup2"
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
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/FullBlockGroup"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/FullBlockGroup2"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanABlockGroup1"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|)
expr_stmt|;
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanABlockGroup12"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
literal|123
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanABlockGroup2"
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanABlockGroup22"
argument_list|,
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
argument_list|,
literal|true
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanABlockGroup3"
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
name|testOneFileUsingDFSStripedInputStream
argument_list|(
literal|"/ec/MoreThanABlockGroup32"
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
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneFileUsingDFSStripedInputStream (String src, int fileLength)
specifier|private
name|void
name|testOneFileUsingDFSStripedInputStream
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|fileLength
parameter_list|)
throws|throws
name|Exception
block|{
name|testOneFileUsingDFSStripedInputStream
argument_list|(
name|src
argument_list|,
name|fileLength
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneFileUsingDFSStripedInputStream (String src, int fileLength, boolean withDataNodeFailure)
specifier|private
name|void
name|testOneFileUsingDFSStripedInputStream
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|boolean
name|withDataNodeFailure
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|expected
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLength
argument_list|)
decl_stmt|;
name|Path
name|srcPath
init|=
operator|new
name|Path
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
operator|new
name|String
argument_list|(
name|expected
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
name|StripedFileTestUtil
operator|.
name|verifyLength
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
if|if
condition|(
name|withDataNodeFailure
condition|)
block|{
name|int
name|dnIndex
init|=
literal|1
decl_stmt|;
comment|// TODO: StripedFileTestUtil.random.nextInt(dataBlocks);
name|LOG
operator|.
name|info
argument_list|(
literal|"stop DataNode "
operator|+
name|dnIndex
argument_list|)
expr_stmt|;
name|stopDataNode
argument_list|(
name|srcPath
argument_list|,
name|dnIndex
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|smallBuf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|byte
index|[]
name|largeBuf
init|=
operator|new
name|byte
index|[
name|fileLength
operator|+
literal|100
index|]
decl_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyPread
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|largeBuf
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|largeBuf
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifySeek
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|ecPolicy
argument_list|,
name|blockGroupSize
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|fileLength
operator|+
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|smallBuf
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|stopDataNode (Path path, int failedDNIdx)
specifier|private
name|void
name|stopDataNode
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|failedDNIdx
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockLocation
index|[]
name|locs
init|=
name|fs
operator|.
name|getFileBlockLocations
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|cellSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|locs
operator|!=
literal|null
operator|&&
name|locs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|String
name|name
init|=
operator|(
name|locs
index|[
literal|0
index|]
operator|.
name|getNames
argument_list|()
operator|)
index|[
name|failedDNIdx
index|]
decl_stmt|;
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
name|int
name|port
init|=
name|dn
operator|.
name|getXferPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|port
argument_list|)
argument_list|)
condition|)
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteReadUsingWebHdfs ()
specifier|public
name|void
name|testWriteReadUsingWebHdfs
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLength
init|=
name|blockSize
operator|*
name|dataBlocks
operator|+
name|cellSize
operator|+
literal|123
decl_stmt|;
specifier|final
name|byte
index|[]
name|expected
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLength
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|WebHdfsTestUtil
operator|.
name|getWebHdfsFileSystem
argument_list|(
name|conf
argument_list|,
name|WebHdfsConstants
operator|.
name|WEBHDFS_SCHEME
argument_list|)
decl_stmt|;
name|Path
name|srcPath
init|=
operator|new
name|Path
argument_list|(
literal|"/testWriteReadUsingWebHdfs"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
operator|new
name|String
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyLength
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|)
expr_stmt|;
name|byte
index|[]
name|smallBuf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|byte
index|[]
name|largeBuf
init|=
operator|new
name|byte
index|[
name|fileLength
operator|+
literal|100
index|]
decl_stmt|;
comment|// TODO: HDFS-8797
comment|//StripedFileTestUtil.verifyPread(fs, srcPath, fileLength, expected, largeBuf);
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|largeBuf
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifySeek
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|ecPolicy
argument_list|,
name|blockGroupSize
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|srcPath
argument_list|,
name|fileLength
argument_list|,
name|expected
argument_list|,
name|smallBuf
argument_list|)
expr_stmt|;
comment|// webhdfs doesn't support bytebuffer read
block|}
annotation|@
name|Test
DECL|method|testConcat ()
specifier|public
name|void
name|testConcat
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|10
operator|+
literal|234
argument_list|)
decl_stmt|;
name|int
name|totalLength
init|=
literal|0
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
literal|"/ec/testConcat_target"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|target
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
literal|123
argument_list|)
argument_list|)
expr_stmt|;
name|totalLength
operator|+=
literal|123
expr_stmt|;
name|int
name|numFiles
init|=
literal|5
decl_stmt|;
name|Path
index|[]
name|srcs
init|=
operator|new
name|Path
index|[
name|numFiles
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
name|numFiles
condition|;
name|i
operator|++
control|)
block|{
name|srcs
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
literal|"/ec/testConcat_src_file_"
operator|+
name|i
argument_list|)
expr_stmt|;
name|int
name|srcLength
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|blockSize
operator|*
name|dataBlocks
operator|*
literal|2
argument_list|)
operator|+
literal|1
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|srcs
index|[
name|i
index|]
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|data
argument_list|,
name|totalLength
argument_list|,
name|totalLength
operator|+
name|srcLength
argument_list|)
argument_list|)
expr_stmt|;
name|totalLength
operator|+=
name|srcLength
expr_stmt|;
block|}
name|fs
operator|.
name|concat
argument_list|(
name|target
argument_list|,
name|srcs
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyStatefulRead
argument_list|(
name|fs
argument_list|,
name|target
argument_list|,
name|totalLength
argument_list|,
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|totalLength
argument_list|)
argument_list|,
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConcatWithDifferentECPolicy ()
specifier|public
name|void
name|testConcatWithDifferentECPolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|blockSize
operator|*
name|dataBlocks
argument_list|)
decl_stmt|;
name|Path
name|nonECFile
init|=
operator|new
name|Path
argument_list|(
literal|"/non_ec_file"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|nonECFile
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|Path
name|target
init|=
operator|new
name|Path
argument_list|(
literal|"/ec/non_ec_file"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|nonECFile
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|int
name|numFiles
init|=
literal|2
decl_stmt|;
name|Path
index|[]
name|srcs
init|=
operator|new
name|Path
index|[
name|numFiles
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
name|numFiles
condition|;
name|i
operator|++
control|)
block|{
name|srcs
index|[
name|i
index|]
operator|=
operator|new
name|Path
argument_list|(
literal|"/ec/testConcat_src_file_"
operator|+
name|i
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|srcs
index|[
name|i
index|]
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|fs
operator|.
name|concat
argument_list|(
name|target
argument_list|,
name|srcs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"non-ec file shouldn't concat with ec file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"have different erasure coding policy"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

