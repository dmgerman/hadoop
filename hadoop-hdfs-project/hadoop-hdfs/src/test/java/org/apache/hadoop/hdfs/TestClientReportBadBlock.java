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
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|ChecksumException
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
name|CommonConfigurationKeys
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
name|UnresolvedLinkException
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
name|FSDataset
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
name|NamenodeFsck
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
name|tools
operator|.
name|DFSck
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
name|security
operator|.
name|AccessControlException
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
name|util
operator|.
name|ToolRunner
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
name|After
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * Class is used to test client reporting corrupted block replica to name node.  * The reporting policy is if block replica is more than one, if all replicas  * are corrupted, client does not report (since the client can handicapped). If  * some of the replicas are corrupted, client reports the corrupted block  * replicas. In case of only one block replica, client always reports corrupted  * replica.  */
end_comment

begin_class
DECL|class|TestClientReportBadBlock
specifier|public
class|class
name|TestClientReportBadBlock
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
name|TestClientReportBadBlock
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|long
name|BLOCK_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|buffersize
specifier|private
specifier|static
name|int
name|buffersize
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|dfs
specifier|private
specifier|static
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|numDataNodes
specifier|private
specifier|static
name|int
name|numDataNodes
init|=
literal|3
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|rand
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|startUpCluster ()
specifier|public
name|void
name|startUpCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// to allow test to be
comment|// run outside of Ant
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
expr_stmt|;
block|}
comment|// disable block scanner
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
argument_list|,
operator|-
literal|1
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
name|numDataNodes
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
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|buffersize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_FILE_BUFFER_SIZE_KEY
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutDownCluster ()
specifier|public
name|void
name|shutDownCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/*    * This test creates a file with one block replica. Corrupt the block. Make    * DFSClient read the corrupted file. Corrupted block is expected to be    * reported to name node.    */
annotation|@
name|Test
DECL|method|testOneBlockReplica ()
specifier|public
name|void
name|testOneBlockReplica
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|short
name|repl
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|corruptBlockNumber
init|=
literal|1
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
comment|// create a file
name|String
name|fileName
init|=
literal|"/tmp/testClientReportBadBlock/OneBlockReplica"
operator|+
name|i
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|createAFileWithCorruptedBlockReplicas
argument_list|(
name|filePath
argument_list|,
name|repl
argument_list|,
name|corruptBlockNumber
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|dfsClientReadFile
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dfsClientReadFileFromPosition
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
block|}
comment|// the only block replica is corrupted. The LocatedBlock should be marked
comment|// as corrupted. But the corrupted replica is expected to be returned
comment|// when calling Namenode#getBlockLocations() since all(one) replicas are
comment|// corrupted.
name|int
name|expectedReplicaCount
init|=
literal|1
decl_stmt|;
name|verifyCorruptedBlockCount
argument_list|(
name|filePath
argument_list|,
name|expectedReplicaCount
argument_list|)
expr_stmt|;
name|verifyFirstBlockCorrupted
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verifyFsckBlockCorrupted
argument_list|()
expr_stmt|;
name|testFsckListCorruptFilesBlocks
argument_list|(
name|filePath
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test creates a file with three block replicas. Corrupt all of the    * replicas. Make dfs client read the file. No block corruption should be    * reported.    */
annotation|@
name|Test
DECL|method|testCorruptAllOfThreeReplicas ()
specifier|public
name|void
name|testCorruptAllOfThreeReplicas
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|short
name|repl
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|corruptBlockNumber
init|=
literal|3
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
comment|// create a file
name|String
name|fileName
init|=
literal|"/tmp/testClientReportBadBlock/testCorruptAllReplicas"
operator|+
name|i
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|createAFileWithCorruptedBlockReplicas
argument_list|(
name|filePath
argument_list|,
name|repl
argument_list|,
name|corruptBlockNumber
argument_list|)
expr_stmt|;
comment|// ask dfs client to read the file
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|dfsClientReadFile
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dfsClientReadFileFromPosition
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
block|}
comment|// As all replicas are corrupted. We expect DFSClient does NOT report
comment|// corrupted replicas to the name node.
name|int
name|expectedReplicasReturned
init|=
name|repl
decl_stmt|;
name|verifyCorruptedBlockCount
argument_list|(
name|filePath
argument_list|,
name|expectedReplicasReturned
argument_list|)
expr_stmt|;
comment|// LocatedBlock should not have the block marked as corrupted.
name|verifyFirstBlockCorrupted
argument_list|(
name|filePath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|verifyFsckHealth
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|testFsckListCorruptFilesBlocks
argument_list|(
name|filePath
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test creates a file with three block replicas. Corrupt two of the    * replicas. Make dfs client read the file. The corrupted blocks with their    * owner data nodes should be reported to the name node.     */
annotation|@
name|Test
DECL|method|testCorruptTwoOutOfThreeReplicas ()
specifier|public
name|void
name|testCorruptTwoOutOfThreeReplicas
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|short
name|repl
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|corruptBlocReplicas
init|=
literal|2
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fileName
init|=
literal|"/tmp/testClientReportBadBlock/CorruptTwoOutOfThreeReplicas"
operator|+
name|i
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|createAFileWithCorruptedBlockReplicas
argument_list|(
name|filePath
argument_list|,
name|repl
argument_list|,
name|corruptBlocReplicas
argument_list|)
expr_stmt|;
name|int
name|replicaCount
init|=
literal|0
decl_stmt|;
comment|/*        * The order of data nodes in LocatedBlock returned by name node is sorted         * by NetworkToplology#pseudoSortByDistance. In current MiniDFSCluster,         * when LocatedBlock is returned, the sorting is based on a random order.        * That is to say, the DFS client and simulated data nodes in mini DFS        * cluster are considered not on the same host nor the same rack.        * Therefore, even we corrupted the first two block replicas based in         * order. When DFSClient read some block replicas, it is not guaranteed         * which block replicas (good/bad) will be returned first. So we try to         * re-read the file until we know the expected replicas numbers is         * returned.        */
while|while
condition|(
name|replicaCount
operator|!=
name|repl
operator|-
name|corruptBlocReplicas
condition|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|dfsClientReadFile
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dfsClientReadFileFromPosition
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
block|}
name|LocatedBlocks
name|blocks
init|=
name|dfs
operator|.
name|dfs
operator|.
name|getNamenode
argument_list|()
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
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|replicaCount
operator|=
name|blocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
operator|.
name|length
expr_stmt|;
block|}
name|verifyFirstBlockCorrupted
argument_list|(
name|filePath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|expectedReplicaCount
init|=
name|repl
operator|-
name|corruptBlocReplicas
decl_stmt|;
name|verifyCorruptedBlockCount
argument_list|(
name|filePath
argument_list|,
name|expectedReplicaCount
argument_list|)
expr_stmt|;
name|verifyFsckHealth
argument_list|(
literal|"Target Replicas is 3 but found 1 replica"
argument_list|)
expr_stmt|;
name|testFsckListCorruptFilesBlocks
argument_list|(
name|filePath
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * create a file with one block and corrupt some/all of the block replicas.    */
DECL|method|createAFileWithCorruptedBlockReplicas (Path filePath, short repl, int corruptBlockCount)
specifier|private
name|void
name|createAFileWithCorruptedBlockReplicas
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|short
name|repl
parameter_list|,
name|int
name|corruptBlockCount
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
name|filePath
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|repl
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|dfs
argument_list|,
name|filePath
argument_list|,
name|repl
argument_list|)
expr_stmt|;
comment|// Locate the file blocks by asking name node
specifier|final
name|LocatedBlocks
name|locatedblocks
init|=
name|dfs
operator|.
name|dfs
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0L
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|repl
argument_list|,
name|locatedblocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// The file only has one block
name|LocatedBlock
name|lblock
init|=
name|locatedblocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|datanodeinfos
init|=
name|lblock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|ExtendedBlock
name|block
init|=
name|lblock
operator|.
name|getBlock
argument_list|()
decl_stmt|;
comment|// corrupt some /all of the block replicas
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|corruptBlockCount
condition|;
name|i
operator|++
control|)
block|{
name|DatanodeInfo
name|dninfo
init|=
name|datanodeinfos
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNode
argument_list|(
name|dninfo
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
name|corruptBlock
argument_list|(
name|block
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Corrupted block "
operator|+
name|block
operator|.
name|getBlockName
argument_list|()
operator|+
literal|" on data node "
operator|+
name|dninfo
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Verify the first block of the file is corrupted (for all its replica).    */
DECL|method|verifyFirstBlockCorrupted (Path filePath, boolean isCorrupted)
specifier|private
name|void
name|verifyFirstBlockCorrupted
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|boolean
name|isCorrupted
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
throws|,
name|IOException
block|{
specifier|final
name|LocatedBlocks
name|locatedBlocks
init|=
name|dfs
operator|.
name|dfs
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|filePath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|LocatedBlock
name|firstLocatedBlock
init|=
name|locatedBlocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|isCorrupted
argument_list|,
name|firstLocatedBlock
operator|.
name|isCorrupt
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the number of corrupted block replicas by fetching the block    * location from name node.    */
DECL|method|verifyCorruptedBlockCount (Path filePath, int expectedReplicas)
specifier|private
name|void
name|verifyCorruptedBlockCount
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|int
name|expectedReplicas
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
throws|,
name|IOException
block|{
specifier|final
name|LocatedBlocks
name|lBlocks
init|=
name|dfs
operator|.
name|dfs
operator|.
name|getNamenode
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|filePath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
comment|// we expect only the first block of the file is used for this test
name|LocatedBlock
name|firstLocatedBlock
init|=
name|lBlocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedReplicas
argument_list|,
name|firstLocatedBlock
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ask dfs client to read the file    */
DECL|method|dfsClientReadFile (Path corruptedFile)
specifier|private
name|void
name|dfsClientReadFile
parameter_list|(
name|Path
name|corruptedFile
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|DFSInputStream
name|in
init|=
name|dfs
operator|.
name|dfs
operator|.
name|open
argument_list|(
name|corruptedFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|buffersize
index|]
decl_stmt|;
name|int
name|nRead
init|=
literal|0
decl_stmt|;
comment|// total number of bytes read
try|try
block|{
do|do
block|{
name|nRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|nRead
operator|>
literal|0
condition|)
do|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|ce
parameter_list|)
block|{
comment|// caught ChecksumException if all replicas are bad, ignore and continue.
name|LOG
operator|.
name|debug
argument_list|(
literal|"DfsClientReadFile caught ChecksumException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|bme
parameter_list|)
block|{
comment|// caught BlockMissingException, ignore.
name|LOG
operator|.
name|debug
argument_list|(
literal|"DfsClientReadFile caught BlockMissingException."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * DFS client read bytes starting from the specified position.    */
DECL|method|dfsClientReadFileFromPosition (Path corruptedFile)
specifier|private
name|void
name|dfsClientReadFileFromPosition
parameter_list|(
name|Path
name|corruptedFile
parameter_list|)
throws|throws
name|UnresolvedLinkException
throws|,
name|IOException
block|{
name|DFSInputStream
name|in
init|=
name|dfs
operator|.
name|dfs
operator|.
name|open
argument_list|(
name|corruptedFile
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|buffersize
index|]
decl_stmt|;
name|int
name|startPosition
init|=
literal|2
decl_stmt|;
name|int
name|nRead
init|=
literal|0
decl_stmt|;
comment|// total number of bytes read
try|try
block|{
do|do
block|{
name|nRead
operator|=
name|in
operator|.
name|read
argument_list|(
name|startPosition
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
name|startPosition
operator|+=
name|buf
operator|.
name|length
expr_stmt|;
block|}
do|while
condition|(
name|nRead
operator|>
literal|0
condition|)
do|;
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|bme
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"DfsClientReadFile caught BlockMissingException."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Corrupt a block on a data node. Replace the block file content with content    * of 1, 2, ...BLOCK_SIZE.    *     * @param block    *          the ExtendedBlock to be corrupted    * @param dn    *          the data node where the block needs to be corrupted    * @throws FileNotFoundException    * @throws IOException    */
DECL|method|corruptBlock (final ExtendedBlock block, final DataNode dn)
specifier|private
specifier|static
name|void
name|corruptBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|block
parameter_list|,
specifier|final
name|DataNode
name|dn
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
specifier|final
name|FSDataset
name|data
init|=
operator|(
name|FSDataset
operator|)
name|dn
operator|.
name|getFSDataset
argument_list|()
decl_stmt|;
specifier|final
name|RandomAccessFile
name|raFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|data
operator|.
name|getBlockFile
argument_list|(
name|block
argument_list|)
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|BLOCK_SIZE
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
name|BLOCK_SIZE
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
call|(
name|byte
call|)
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|raFile
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|raFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyFsckHealth (String expected)
specifier|private
specifier|static
name|void
name|verifyFsckHealth
parameter_list|(
name|String
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Fsck health has error code 0.
comment|// Make sure filesystem is in healthy state
name|String
name|outStr
init|=
name|runFsck
argument_list|(
name|conf
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|outStr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outStr
operator|.
name|contains
argument_list|(
name|NamenodeFsck
operator|.
name|HEALTHY_STATUS
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|expected
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outStr
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyFsckBlockCorrupted ()
specifier|private
specifier|static
name|void
name|verifyFsckBlockCorrupted
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|outStr
init|=
name|runFsck
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|outStr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outStr
operator|.
name|contains
argument_list|(
name|NamenodeFsck
operator|.
name|CORRUPT_STATUS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFsckListCorruptFilesBlocks (Path filePath, int errorCode)
specifier|private
specifier|static
name|void
name|testFsckListCorruptFilesBlocks
parameter_list|(
name|Path
name|filePath
parameter_list|,
name|int
name|errorCode
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|outStr
init|=
name|runFsck
argument_list|(
name|conf
argument_list|,
name|errorCode
argument_list|,
literal|true
argument_list|,
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|"-list-corruptfileblocks"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"fsck -list-corruptfileblocks out: "
operator|+
name|outStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|errorCode
operator|!=
literal|0
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|outStr
operator|.
name|contains
argument_list|(
literal|"CORRUPT files"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runFsck (Configuration conf, int expectedErrCode, boolean checkErrorCode, String... path)
specifier|static
name|String
name|runFsck
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|expectedErrCode
parameter_list|,
name|boolean
name|checkErrorCode
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|bStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|bStream
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|errCode
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|DFSck
argument_list|(
name|conf
argument_list|,
name|out
argument_list|)
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkErrorCode
condition|)
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedErrCode
argument_list|,
name|errCode
argument_list|)
expr_stmt|;
return|return
name|bStream
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

