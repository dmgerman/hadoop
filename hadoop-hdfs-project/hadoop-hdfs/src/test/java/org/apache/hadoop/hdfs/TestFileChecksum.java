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
name|FileChecksum
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
name|permission
operator|.
name|FsPermission
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
name|client
operator|.
name|HdfsClientConfigKeys
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

begin_comment
comment|/**  * This test serves a prototype to demo the idea proposed so far. It creates two  * files using the same data, one is in replica mode, the other is in stripped  * layout. For simple, it assumes 6 data blocks in both files and the block size  * are the same.  */
end_comment

begin_class
DECL|class|TestFileChecksum
specifier|public
class|class
name|TestFileChecksum
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
name|TestFileChecksum
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
DECL|field|dataBlocks
specifier|private
name|int
name|dataBlocks
init|=
name|ecPolicy
operator|.
name|getNumDataUnits
argument_list|()
decl_stmt|;
DECL|field|parityBlocks
specifier|private
name|int
name|parityBlocks
init|=
name|ecPolicy
operator|.
name|getNumParityUnits
argument_list|()
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
DECL|field|client
specifier|private
name|DFSClient
name|client
decl_stmt|;
DECL|field|cellSize
specifier|private
name|int
name|cellSize
init|=
name|ecPolicy
operator|.
name|getCellSize
argument_list|()
decl_stmt|;
DECL|field|stripesPerBlock
specifier|private
name|int
name|stripesPerBlock
init|=
literal|6
decl_stmt|;
DECL|field|blockSize
specifier|private
name|int
name|blockSize
init|=
name|cellSize
operator|*
name|stripesPerBlock
decl_stmt|;
DECL|field|numBlockGroups
specifier|private
name|int
name|numBlockGroups
init|=
literal|10
decl_stmt|;
DECL|field|stripSize
specifier|private
name|int
name|stripSize
init|=
name|cellSize
operator|*
name|dataBlocks
decl_stmt|;
DECL|field|blockGroupSize
specifier|private
name|int
name|blockGroupSize
init|=
name|stripesPerBlock
operator|*
name|stripSize
decl_stmt|;
DECL|field|fileSize
specifier|private
name|int
name|fileSize
init|=
name|numBlockGroups
operator|*
name|blockGroupSize
decl_stmt|;
DECL|field|bytesPerCRC
specifier|private
name|int
name|bytesPerCRC
decl_stmt|;
DECL|field|ecDir
specifier|private
name|String
name|ecDir
init|=
literal|"/striped"
decl_stmt|;
DECL|field|stripedFile1
specifier|private
name|String
name|stripedFile1
init|=
name|ecDir
operator|+
literal|"/stripedFileChecksum1"
decl_stmt|;
DECL|field|stripedFile2
specifier|private
name|String
name|stripedFile2
init|=
name|ecDir
operator|+
literal|"/stripedFileChecksum2"
decl_stmt|;
DECL|field|replicatedFile
specifier|private
name|String
name|replicatedFile
init|=
literal|"/replicatedFileChecksum"
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
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY
argument_list|,
literal|false
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
name|Path
name|ecPath
init|=
operator|new
name|Path
argument_list|(
name|ecDir
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|mkdir
argument_list|(
name|ecPath
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
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
name|ecDir
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
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|client
operator|=
name|fs
operator|.
name|getClient
argument_list|()
expr_stmt|;
name|bytesPerCRC
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|HdfsClientConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_DEFAULT
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksum1 ()
specifier|public
name|void
name|testStripedFileChecksum1
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
literal|0
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|stripedFile2
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksum
argument_list|(
name|length
argument_list|,
name|length
operator|+
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksum2 ()
specifier|public
name|void
name|testStripedFileChecksum2
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|stripSize
operator|-
literal|1
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|stripedFile2
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksum
argument_list|(
name|length
argument_list|,
name|length
operator|-
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksum3 ()
specifier|public
name|void
name|testStripedFileChecksum3
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|stripSize
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|stripedFile2
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksum
argument_list|(
name|length
argument_list|,
name|length
operator|-
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksum4 ()
specifier|public
name|void
name|testStripedFileChecksum4
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|stripSize
operator|+
name|cellSize
operator|*
literal|2
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|stripedFile2
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksum
argument_list|(
name|length
argument_list|,
name|length
operator|-
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksum5 ()
specifier|public
name|void
name|testStripedFileChecksum5
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|blockGroupSize
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|stripedFile2
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksum
argument_list|(
name|length
argument_list|,
name|length
operator|-
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksum6 ()
specifier|public
name|void
name|testStripedFileChecksum6
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
name|blockGroupSize
operator|+
name|blockSize
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|stripedFile2
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksum
argument_list|(
name|length
argument_list|,
name|length
operator|-
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksum7 ()
specifier|public
name|void
name|testStripedFileChecksum7
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|length
init|=
operator|-
literal|1
decl_stmt|;
comment|// whole file
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|stripedFile2
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksum
argument_list|(
name|length
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
block|}
DECL|method|testStripedFileChecksum (int range1, int range2)
specifier|private
name|void
name|testStripedFileChecksum
parameter_list|(
name|int
name|range1
parameter_list|,
name|int
name|range2
parameter_list|)
throws|throws
name|Exception
block|{
name|FileChecksum
name|stripedFileChecksum1
init|=
name|getFileChecksum
argument_list|(
name|stripedFile1
argument_list|,
name|range1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FileChecksum
name|stripedFileChecksum2
init|=
name|getFileChecksum
argument_list|(
name|stripedFile2
argument_list|,
name|range1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FileChecksum
name|stripedFileChecksum3
init|=
name|getFileChecksum
argument_list|(
name|stripedFile2
argument_list|,
name|range2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksum1:"
operator|+
name|stripedFileChecksum1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksum2:"
operator|+
name|stripedFileChecksum2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksum3:"
operator|+
name|stripedFileChecksum3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|stripedFileChecksum1
operator|.
name|equals
argument_list|(
name|stripedFileChecksum2
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|range1
operator|>=
literal|0
operator|&&
name|range1
operator|!=
name|range2
condition|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|stripedFileChecksum1
operator|.
name|equals
argument_list|(
name|stripedFileChecksum3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedAndReplicatedFileChecksum ()
specifier|public
name|void
name|testStripedAndReplicatedFileChecksum
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|replicatedFile
block|}
argument_list|)
expr_stmt|;
name|FileChecksum
name|stripedFileChecksum1
init|=
name|getFileChecksum
argument_list|(
name|stripedFile1
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FileChecksum
name|replicatedFileChecksum
init|=
name|getFileChecksum
argument_list|(
name|replicatedFile
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|stripedFileChecksum1
operator|.
name|equals
argument_list|(
name|replicatedFileChecksum
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocks1 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocks1
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|}
argument_list|)
expr_stmt|;
name|FileChecksum
name|stripedFileChecksum1
init|=
name|getFileChecksum
argument_list|(
name|stripedFile1
argument_list|,
name|fileSize
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FileChecksum
name|stripedFileChecksumRecon
init|=
name|getFileChecksum
argument_list|(
name|stripedFile1
argument_list|,
name|fileSize
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksum1:"
operator|+
name|stripedFileChecksum1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksumRecon:"
operator|+
name|stripedFileChecksumRecon
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Checksum mismatches!"
argument_list|,
name|stripedFileChecksum1
operator|.
name|equals
argument_list|(
name|stripedFileChecksumRecon
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocks2 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocks2
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile1
block|,
name|stripedFile2
block|}
argument_list|)
expr_stmt|;
name|FileChecksum
name|stripedFileChecksum1
init|=
name|getFileChecksum
argument_list|(
name|stripedFile1
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FileChecksum
name|stripedFileChecksum2
init|=
name|getFileChecksum
argument_list|(
name|stripedFile2
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FileChecksum
name|stripedFileChecksum2Recon
init|=
name|getFileChecksum
argument_list|(
name|stripedFile2
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksum1:"
operator|+
name|stripedFileChecksum1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksum2:"
operator|+
name|stripedFileChecksum1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksum2Recon:"
operator|+
name|stripedFileChecksum2Recon
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Checksum mismatches!"
argument_list|,
name|stripedFileChecksum1
operator|.
name|equals
argument_list|(
name|stripedFileChecksum2
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Checksum mismatches!"
argument_list|,
name|stripedFileChecksum1
operator|.
name|equals
argument_list|(
name|stripedFileChecksum2Recon
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Checksum mismatches!"
argument_list|,
name|stripedFileChecksum2
operator|.
name|equals
argument_list|(
name|stripedFileChecksum2Recon
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery ( String stripedFile, int requestedLen)
specifier|private
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
parameter_list|(
name|String
name|stripedFile
parameter_list|,
name|int
name|requestedLen
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checksum file:{}, requested length:{}"
argument_list|,
name|stripedFile
argument_list|,
name|requestedLen
argument_list|)
expr_stmt|;
name|prepareTestFiles
argument_list|(
name|fileSize
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile
block|}
argument_list|)
expr_stmt|;
name|FileChecksum
name|stripedFileChecksum1
init|=
name|getFileChecksum
argument_list|(
name|stripedFile
argument_list|,
name|requestedLen
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FileChecksum
name|stripedFileChecksumRecon
init|=
name|getFileChecksum
argument_list|(
name|stripedFile
argument_list|,
name|requestedLen
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksum1:"
operator|+
name|stripedFileChecksum1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stripedFileChecksumRecon:"
operator|+
name|stripedFileChecksumRecon
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Checksum mismatches!"
argument_list|,
name|stripedFileChecksum1
operator|.
name|equals
argument_list|(
name|stripedFileChecksumRecon
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed for a small file less than    * bytesPerCRC size.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery1 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery1
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed for a small file less than    * bytesPerCRC size.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery2 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery2
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving bytesPerCRC    * length of file range for checksum calculation. 512 is the value of    * bytesPerCRC.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery3 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery3
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|bytesPerCRC
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving 'cellsize'    * length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery4 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery4
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|cellSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving less than    * cellsize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery5 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery5
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|cellSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving greater than    * cellsize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery6 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery6
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|cellSize
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving two times    * cellsize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery7 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery7
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|cellSize
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving stripSize    * length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery8 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery8
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|stripSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving less than    * stripSize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery9 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery9
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|stripSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving greater than    * stripSize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery10 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery10
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|stripSize
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving less than    * blockGroupSize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery11 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery11
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|blockGroupSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving greaterthan    * blockGroupSize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery12 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery12
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|blockGroupSize
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving greater than    * blockGroupSize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery13 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery13
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|blockGroupSize
operator|*
name|numBlockGroups
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed by giving lessthan    * fileSize length of file range for checksum calculation.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery14 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery14
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|fileSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed for a length greater than    * file size.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery15 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery15
parameter_list|()
throws|throws
name|Exception
block|{
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile1
argument_list|,
name|fileSize
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed for a small file less than    * bytesPerCRC size.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery16 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery16
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLength
init|=
literal|100
decl_stmt|;
name|String
name|stripedFile3
init|=
name|ecDir
operator|+
literal|"/stripedFileChecksum3"
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileLength
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile3
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile3
argument_list|,
name|fileLength
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed for a small file less than    * bytesPerCRC size.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery17 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery17
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLength
init|=
literal|100
decl_stmt|;
name|String
name|stripedFile3
init|=
name|ecDir
operator|+
literal|"/stripedFileChecksum3"
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileLength
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile3
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed for a small file less than    * bytesPerCRC size.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery18 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery18
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLength
init|=
literal|100
decl_stmt|;
name|String
name|stripedFile3
init|=
name|ecDir
operator|+
literal|"/stripedFileChecksum3"
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileLength
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile3
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile3
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed with greater than file    * length.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery19 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery19
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLength
init|=
literal|100
decl_stmt|;
name|String
name|stripedFile3
init|=
name|ecDir
operator|+
literal|"/stripedFileChecksum3"
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileLength
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile3
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile3
argument_list|,
name|fileLength
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that the checksum can be computed for small file with less    * than file length.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testStripedFileChecksumWithMissedDataBlocksRangeQuery20 ()
specifier|public
name|void
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery20
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|fileLength
init|=
name|bytesPerCRC
decl_stmt|;
name|String
name|stripedFile3
init|=
name|ecDir
operator|+
literal|"/stripedFileChecksum3"
decl_stmt|;
name|prepareTestFiles
argument_list|(
name|fileLength
argument_list|,
operator|new
name|String
index|[]
block|{
name|stripedFile3
block|}
argument_list|)
expr_stmt|;
name|testStripedFileChecksumWithMissedDataBlocksRangeQuery
argument_list|(
name|stripedFile3
argument_list|,
name|bytesPerCRC
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|getFileChecksum (String filePath, int range, boolean killDn)
specifier|private
name|FileChecksum
name|getFileChecksum
parameter_list|(
name|String
name|filePath
parameter_list|,
name|int
name|range
parameter_list|,
name|boolean
name|killDn
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|dnIdxToDie
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|killDn
condition|)
block|{
name|dnIdxToDie
operator|=
name|getDataNodeToKill
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|DataNode
name|dnToDie
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|dnIdxToDie
argument_list|)
decl_stmt|;
name|shutdownDataNode
argument_list|(
name|dnToDie
argument_list|)
expr_stmt|;
block|}
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|FileChecksum
name|fc
decl_stmt|;
if|if
condition|(
name|range
operator|>=
literal|0
condition|)
block|{
name|fc
operator|=
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|testPath
argument_list|,
name|range
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fc
operator|=
name|fs
operator|.
name|getFileChecksum
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dnIdxToDie
operator|!=
operator|-
literal|1
condition|)
block|{
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnIdxToDie
argument_list|)
expr_stmt|;
block|}
return|return
name|fc
return|;
block|}
DECL|method|prepareTestFiles (int fileLength, String[] filePaths)
specifier|private
name|void
name|prepareTestFiles
parameter_list|(
name|int
name|fileLength
parameter_list|,
name|String
index|[]
name|filePaths
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|fileData
init|=
name|StripedFileTestUtil
operator|.
name|generateBytes
argument_list|(
name|fileLength
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|filePath
range|:
name|filePaths
control|)
block|{
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
name|filePath
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
name|fileData
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutdownDataNode (DataNode dataNode)
name|void
name|shutdownDataNode
parameter_list|(
name|DataNode
name|dataNode
parameter_list|)
throws|throws
name|IOException
block|{
comment|/*      * Kill the datanode which contains one replica      * We need to make sure it dead in namenode: clear its update time and      * trigger NN to check heartbeat.      */
name|dataNode
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|setDataNodeDead
argument_list|(
name|dataNode
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Determine the datanode that hosts the first block of the file. For simple    * this just returns the first datanode as it's firstly tried.    */
DECL|method|getDataNodeToKill (String filePath)
name|int
name|getDataNodeToKill
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|IOException
block|{
name|LocatedBlocks
name|locatedBlocks
init|=
name|client
operator|.
name|getLocatedBlocks
argument_list|(
name|filePath
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|LocatedBlock
name|locatedBlock
init|=
name|locatedBlocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|datanodes
init|=
name|locatedBlock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|DatanodeInfo
name|chosenDn
init|=
name|datanodes
index|[
literal|0
index|]
decl_stmt|;
name|int
name|idx
init|=
literal|0
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
if|if
condition|(
name|dn
operator|.
name|getInfoPort
argument_list|()
operator|==
name|chosenDn
operator|.
name|getInfoPort
argument_list|()
condition|)
block|{
return|return
name|idx
return|;
block|}
name|idx
operator|++
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

