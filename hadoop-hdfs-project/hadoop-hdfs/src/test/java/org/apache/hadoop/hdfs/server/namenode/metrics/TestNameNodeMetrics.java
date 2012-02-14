begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.metrics
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
name|namenode
operator|.
name|metrics
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertCounter
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|assertGauge
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
name|test
operator|.
name|MetricsAsserts
operator|.
name|getMetrics
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
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|Options
operator|.
name|Rename
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
name|DistributedFileSystem
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
name|HdfsConstants
operator|.
name|SafeModeAction
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
name|BlockManager
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNodeAdapter
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
name|metrics2
operator|.
name|MetricsRecordBuilder
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
name|MetricsAsserts
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

begin_comment
comment|/**  * Test for metrics published by the Namenode  */
end_comment

begin_class
DECL|class|TestNameNodeMetrics
specifier|public
class|class
name|TestNameNodeMetrics
block|{
DECL|field|CONF
specifier|private
specifier|static
specifier|final
name|Configuration
name|CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|DFS_REPLICATION_INTERVAL
specifier|private
specifier|static
specifier|final
name|int
name|DFS_REPLICATION_INTERVAL
init|=
literal|1
decl_stmt|;
DECL|field|TEST_ROOT_DIR_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_ROOT_DIR_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/testNameNodeMetrics"
argument_list|)
decl_stmt|;
DECL|field|NN_METRICS
specifier|private
specifier|static
specifier|final
name|String
name|NN_METRICS
init|=
literal|"NameNodeActivity"
decl_stmt|;
DECL|field|NS_METRICS
specifier|private
specifier|static
specifier|final
name|String
name|NS_METRICS
init|=
literal|"FSNamesystem"
decl_stmt|;
comment|// Number of datanodes in the cluster
DECL|field|DATANODE_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|DATANODE_COUNT
init|=
literal|3
decl_stmt|;
static|static
block|{
name|CONF
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
name|DFS_REPLICATION_INTERVAL
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
argument_list|,
name|DFS_REPLICATION_INTERVAL
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
name|MetricsAsserts
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
name|DEBUG
argument_list|)
expr_stmt|;
block|}
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
DECL|field|rand
specifier|private
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|namesystem
specifier|private
name|FSNamesystem
name|namesystem
decl_stmt|;
DECL|field|bm
specifier|private
name|BlockManager
name|bm
decl_stmt|;
DECL|method|getTestPath (String fileName)
specifier|private
specifier|static
name|Path
name|getTestPath
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR_PATH
argument_list|,
name|fileName
argument_list|)
return|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|CONF
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|DATANODE_COUNT
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
name|namesystem
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|bm
operator|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
expr_stmt|;
name|fs
operator|=
operator|(
name|DistributedFileSystem
operator|)
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
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/** create a file with a length of<code>fileLen</code> */
DECL|method|createFile (Path file, long fileLen, short replicas)
specifier|private
name|void
name|createFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|long
name|fileLen
parameter_list|,
name|short
name|replicas
parameter_list|)
throws|throws
name|IOException
block|{
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|fileLen
argument_list|,
name|replicas
argument_list|,
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|updateMetrics ()
specifier|private
name|void
name|updateMetrics
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Wait for metrics update (corresponds to dfs.namenode.replication.interval
comment|// for some block related metrics to get updated)
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|readFile (FileSystem fileSys,Path name)
specifier|private
name|void
name|readFile
parameter_list|(
name|FileSystem
name|fileSys
parameter_list|,
name|Path
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Just read file so that getNumBlockLocations are incremented
name|DataInputStream
name|stm
init|=
name|fileSys
operator|.
name|open
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|stm
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test metrics associated with addition of a file */
annotation|@
name|Test
DECL|method|testFileAdd ()
specifier|public
name|void
name|testFileAdd
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Add files with 100 blocks
specifier|final
name|Path
name|file
init|=
name|getTestPath
argument_list|(
literal|"testFileAdd"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
literal|3200
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
specifier|final
name|long
name|blockCount
init|=
literal|32
decl_stmt|;
name|int
name|blockCapacity
init|=
name|namesystem
operator|.
name|getBlockCapacity
argument_list|()
decl_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
name|assertGauge
argument_list|(
literal|"BlockCapacity"
argument_list|,
name|blockCapacity
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
decl_stmt|;
comment|// File create operations is 1
comment|// Number of files created is depth of<code>file</code> path
name|assertCounter
argument_list|(
literal|"CreateFileOps"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"FilesCreated"
argument_list|,
operator|(
name|long
operator|)
name|file
operator|.
name|depth
argument_list|()
argument_list|,
name|rb
argument_list|)
expr_stmt|;
comment|// Blocks are stored in a hashmap. Compute its capacity, which
comment|// doubles every time the number of entries reach the threshold.
name|int
name|threshold
init|=
call|(
name|int
call|)
argument_list|(
name|blockCapacity
operator|*
name|BlockManager
operator|.
name|DEFAULT_MAP_LOAD_FACTOR
argument_list|)
decl_stmt|;
while|while
condition|(
name|threshold
operator|<
name|blockCount
condition|)
block|{
name|blockCapacity
operator|<<=
literal|1
expr_stmt|;
block|}
name|updateMetrics
argument_list|()
expr_stmt|;
name|long
name|filesTotal
init|=
name|file
operator|.
name|depth
argument_list|()
operator|+
literal|1
decl_stmt|;
comment|// Add 1 for root
name|rb
operator|=
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"FilesTotal"
argument_list|,
name|filesTotal
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"BlocksTotal"
argument_list|,
name|blockCount
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"BlockCapacity"
argument_list|,
name|blockCapacity
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|filesTotal
operator|--
expr_stmt|;
comment|// reduce the filecount for deleted file
name|waitForDeletion
argument_list|()
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
name|rb
operator|=
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"FilesTotal"
argument_list|,
name|filesTotal
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"BlocksTotal"
argument_list|,
literal|0L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"PendingDeletionBlocks"
argument_list|,
literal|0L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|rb
operator|=
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
expr_stmt|;
comment|// Delete file operations and number of files deleted must be 1
name|assertCounter
argument_list|(
literal|"DeleteFileOps"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"FilesDeleted"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
comment|/** Corrupt a block and ensure metrics reflects it */
annotation|@
name|Test
DECL|method|testCorruptBlock ()
specifier|public
name|void
name|testCorruptBlock
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a file with single block with two replicas
specifier|final
name|Path
name|file
init|=
name|getTestPath
argument_list|(
literal|"testCorruptBlock"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
comment|// Corrupt first replica of the block
name|LocatedBlock
name|block
init|=
name|NameNodeAdapter
operator|.
name|getBlockLocations
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|bm
operator|.
name|findAndMarkBlockAsCorrupt
argument_list|(
name|block
operator|.
name|getBlock
argument_list|()
argument_list|,
name|block
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|updateMetrics
argument_list|()
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
decl_stmt|;
name|assertGauge
argument_list|(
literal|"CorruptBlocks"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"PendingReplicationBlocks"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"ScheduledReplicationBlocks"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|waitForDeletion
argument_list|()
expr_stmt|;
name|rb
operator|=
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"CorruptBlocks"
argument_list|,
literal|0L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"PendingReplicationBlocks"
argument_list|,
literal|0L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"ScheduledReplicationBlocks"
argument_list|,
literal|0L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
comment|/** Create excess blocks by reducing the replication factor for    * for a file and ensure metrics reflects it    */
annotation|@
name|Test
DECL|method|testExcessBlocks ()
specifier|public
name|void
name|testExcessBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
name|getTestPath
argument_list|(
literal|"testExcessBlocks"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|long
name|totalBlocks
init|=
literal|1
decl_stmt|;
name|NameNodeAdapter
operator|.
name|setReplication
argument_list|(
name|namesystem
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
decl_stmt|;
name|assertGauge
argument_list|(
literal|"ExcessBlocks"
argument_list|,
name|totalBlocks
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Test to ensure metrics reflects missing blocks */
annotation|@
name|Test
DECL|method|testMissingBlock ()
specifier|public
name|void
name|testMissingBlock
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a file with single block with two replicas
name|Path
name|file
init|=
name|getTestPath
argument_list|(
literal|"testMissingBlocks"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// Corrupt the only replica of the block to result in a missing block
name|LocatedBlock
name|block
init|=
name|NameNodeAdapter
operator|.
name|getBlockLocations
argument_list|(
name|cluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|bm
operator|.
name|findAndMarkBlockAsCorrupt
argument_list|(
name|block
operator|.
name|getBlock
argument_list|()
argument_list|,
name|block
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|updateMetrics
argument_list|()
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
decl_stmt|;
name|assertGauge
argument_list|(
literal|"UnderReplicatedBlocks"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"MissingBlocks"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|file
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|waitForDeletion
argument_list|()
expr_stmt|;
name|assertGauge
argument_list|(
literal|"UnderReplicatedBlocks"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForDeletion ()
specifier|private
name|void
name|waitForDeletion
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// Wait for more than DATANODE_COUNT replication intervals to ensure all
comment|// the blocks pending deletion are sent for deletion to the datanodes.
name|Thread
operator|.
name|sleep
argument_list|(
name|DFS_REPLICATION_INTERVAL
operator|*
operator|(
name|DATANODE_COUNT
operator|+
literal|1
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenameMetrics ()
specifier|public
name|void
name|testRenameMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|src
init|=
name|getTestPath
argument_list|(
literal|"src"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|src
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|Path
name|target
init|=
name|getTestPath
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|target
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|src
argument_list|,
name|target
argument_list|,
name|Rename
operator|.
name|OVERWRITE
argument_list|)
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
name|MetricsRecordBuilder
name|rb
init|=
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
decl_stmt|;
name|assertCounter
argument_list|(
literal|"FilesRenamed"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
name|assertCounter
argument_list|(
literal|"FilesDeleted"
argument_list|,
literal|1L
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test numGetBlockLocations metric       *     * Test initiates and performs file operations (create,read,close,open file )    * which results in metrics changes. These metrics changes are updated and     * tested for correctness.    *     *  create file operation does not increment numGetBlockLocation    *  one read file operation increments numGetBlockLocation by 1    *        * @throws IOException in case of an error    */
annotation|@
name|Test
DECL|method|testGetBlockLocationMetric ()
specifier|public
name|void
name|testGetBlockLocationMetric
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file1_Path
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR_PATH
argument_list|,
literal|"file1.dat"
argument_list|)
decl_stmt|;
comment|// When cluster starts first time there are no file  (read,create,open)
comment|// operations so metric GetBlockLocations should be 0.
name|assertCounter
argument_list|(
literal|"GetBlockLocations"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|//Perform create file operation
name|createFile
argument_list|(
name|file1_Path
argument_list|,
literal|100
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
comment|//Create file does not change numGetBlockLocations metric
comment|//expect numGetBlockLocations = 0 for previous and current interval
name|assertCounter
argument_list|(
literal|"GetBlockLocations"
argument_list|,
literal|0L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Open and read file operation increments GetBlockLocations
comment|// Perform read file operation on earlier created file
name|readFile
argument_list|(
name|fs
argument_list|,
name|file1_Path
argument_list|)
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
comment|// Verify read file operation has incremented numGetBlockLocations by 1
name|assertCounter
argument_list|(
literal|"GetBlockLocations"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
comment|// opening and reading file  twice will increment numGetBlockLocations by 2
name|readFile
argument_list|(
name|fs
argument_list|,
name|file1_Path
argument_list|)
expr_stmt|;
name|readFile
argument_list|(
name|fs
argument_list|,
name|file1_Path
argument_list|)
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
name|assertCounter
argument_list|(
literal|"GetBlockLocations"
argument_list|,
literal|3L
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test NN checkpoint and transaction-related metrics.    */
annotation|@
name|Test
DECL|method|testTransactionAndCheckpointMetrics ()
specifier|public
name|void
name|testTransactionAndCheckpointMetrics
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|lastCkptTime
init|=
name|MetricsAsserts
operator|.
name|getLongGauge
argument_list|(
literal|"LastCheckpointTime"
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
decl_stmt|;
name|assertGauge
argument_list|(
literal|"LastCheckpointTime"
argument_list|,
name|lastCkptTime
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"LastWrittenTransactionId"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"TransactionsSinceLastCheckpoint"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"TransactionsSinceLastLogRoll"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR_PATH
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
name|assertGauge
argument_list|(
literal|"LastCheckpointTime"
argument_list|,
name|lastCkptTime
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"LastWrittenTransactionId"
argument_list|,
literal|2L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"TransactionsSinceLastCheckpoint"
argument_list|,
literal|2L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"TransactionsSinceLastLogRoll"
argument_list|,
literal|2L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
name|assertGauge
argument_list|(
literal|"LastCheckpointTime"
argument_list|,
name|lastCkptTime
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"LastWrittenTransactionId"
argument_list|,
literal|4L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"TransactionsSinceLastCheckpoint"
argument_list|,
literal|4L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"TransactionsSinceLastLogRoll"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_LEAVE
argument_list|)
expr_stmt|;
name|updateMetrics
argument_list|()
expr_stmt|;
name|long
name|newLastCkptTime
init|=
name|MetricsAsserts
operator|.
name|getLongGauge
argument_list|(
literal|"LastCheckpointTime"
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|lastCkptTime
operator|<
name|newLastCkptTime
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"LastWrittenTransactionId"
argument_list|,
literal|6L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"TransactionsSinceLastCheckpoint"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
name|assertGauge
argument_list|(
literal|"TransactionsSinceLastLogRoll"
argument_list|,
literal|1L
argument_list|,
name|getMetrics
argument_list|(
name|NS_METRICS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

