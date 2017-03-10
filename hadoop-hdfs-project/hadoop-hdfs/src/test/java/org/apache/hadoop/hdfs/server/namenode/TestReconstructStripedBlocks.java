begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|StripedFileTestUtil
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockInfo
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
name|BlockInfoStriped
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
name|blockmanagement
operator|.
name|BlockManagerTestUtil
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
name|DatanodeDescriptor
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
name|DatanodeManager
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
name|DatanodeStorageInfo
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
name|NumberReplicas
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
name|DataNodeTestUtils
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
name|protocol
operator|.
name|BlockECReconstructionCommand
operator|.
name|BlockECReconstructionInfo
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
name|Assert
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
name|java
operator|.
name|util
operator|.
name|BitSet
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

begin_class
DECL|class|TestReconstructStripedBlocks
specifier|public
class|class
name|TestReconstructStripedBlocks
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestReconstructStripedBlocks
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
DECL|field|groupSize
specifier|private
specifier|final
name|short
name|groupSize
init|=
call|(
name|short
call|)
argument_list|(
name|dataBlocks
operator|+
name|parityBlocks
argument_list|)
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
init|=
literal|4
operator|*
name|cellSize
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
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
literal|"/dir"
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
DECL|field|maxReplicationStreams
specifier|private
name|int
name|maxReplicationStreams
init|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MAX_STREAMS_DEFAULT
decl_stmt|;
DECL|method|initConf (Configuration conf)
specifier|private
name|void
name|initConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Large value to make sure the pending replication request can stay in
comment|// DatanodeDescriptor.replicateBlocks before test timeout.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// Make sure BlockManager can pull all blocks from UnderReplicatedBlocks via
comment|// chooseUnderReplicatedBlocks at once.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_WORK_MULTIPLIER_PER_ITERATION
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMissingStripedBlock ()
specifier|public
name|void
name|testMissingStripedBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMissingStripedBlock
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMissingStripedBlockWithBusyNode ()
specifier|public
name|void
name|testMissingStripedBlockWithBusyNode
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|parityBlocks
condition|;
name|i
operator|++
control|)
block|{
name|doTestMissingStripedBlock
argument_list|(
name|i
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Start GROUP_SIZE + 1 datanodes.    * Inject striped blocks to first GROUP_SIZE datanodes.    * Then make numOfBusy datanodes busy, make numOfMissed datanodes missed.    * Then trigger BlockManager to compute reconstruction works. (so all    * reconstruction work will be scheduled to the last datanode)    * Finally, verify the reconstruction work of the last datanode.    */
DECL|method|doTestMissingStripedBlock (int numOfMissed, int numOfBusy)
specifier|private
name|void
name|doTestMissingStripedBlock
parameter_list|(
name|int
name|numOfMissed
parameter_list|,
name|int
name|numOfBusy
parameter_list|)
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
name|initConf
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
name|groupSize
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
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
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// all blocks will be located at first GROUP_SIZE DNs, the last DN is
comment|// empty because of the util function createStripedFile
comment|// make sure the file is complete in NN
specifier|final
name|INodeFile
name|fileNode
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode4Write
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|fileNode
operator|.
name|isUnderConstruction
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileNode
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
name|BlockInfo
index|[]
name|blocks
init|=
name|fileNode
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numBlocks
argument_list|,
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|BlockInfo
name|blk
range|:
name|blocks
control|)
block|{
name|assertTrue
argument_list|(
name|blk
operator|.
name|isStriped
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blk
operator|.
name|isComplete
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cellSize
operator|*
name|dataBlocks
argument_list|,
name|blk
operator|.
name|getNumBytes
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|BlockInfoStriped
name|sb
init|=
operator|(
name|BlockInfoStriped
operator|)
name|blk
decl_stmt|;
name|assertEquals
argument_list|(
name|groupSize
argument_list|,
name|sb
operator|.
name|numNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BlockManager
name|bm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|BlockInfo
name|firstBlock
init|=
name|fileNode
operator|.
name|getBlocks
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|DatanodeStorageInfo
index|[]
name|storageInfos
init|=
name|bm
operator|.
name|getStorages
argument_list|(
name|firstBlock
argument_list|)
decl_stmt|;
comment|// make numOfBusy nodes busy
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|numOfBusy
condition|;
name|i
operator|++
control|)
block|{
name|DatanodeDescriptor
name|busyNode
init|=
name|storageInfos
index|[
name|i
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
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
name|maxReplicationStreams
operator|+
literal|1
condition|;
name|j
operator|++
control|)
block|{
name|BlockManagerTestUtil
operator|.
name|addBlockToBeReplicated
argument_list|(
name|busyNode
argument_list|,
operator|new
name|Block
argument_list|(
name|j
argument_list|)
argument_list|,
operator|new
name|DatanodeStorageInfo
index|[]
block|{
name|storageInfos
index|[
literal|0
index|]
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|// make numOfMissed internal blocks missed
for|for
control|(
init|;
name|i
operator|<
name|numOfBusy
operator|+
name|numOfMissed
condition|;
name|i
operator|++
control|)
block|{
name|DatanodeDescriptor
name|missedNode
init|=
name|storageInfos
index|[
name|i
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numBlocks
argument_list|,
name|missedNode
operator|.
name|numBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|removeDatanode
argument_list|(
name|missedNode
argument_list|)
expr_stmt|;
block|}
name|BlockManagerTestUtil
operator|.
name|getComputedDatanodeWork
argument_list|(
name|bm
argument_list|)
expr_stmt|;
comment|// all the reconstruction work will be scheduled on the last DN
name|DataNode
name|lastDn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|groupSize
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|last
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|lastDn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Counting the number of outstanding EC tasks"
argument_list|,
name|numBlocks
argument_list|,
name|last
operator|.
name|getNumberOfBlocksToBeErasureCoded
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|BlockECReconstructionInfo
argument_list|>
name|reconstruction
init|=
name|last
operator|.
name|getErasureCodeCommand
argument_list|(
name|numBlocks
argument_list|)
decl_stmt|;
for|for
control|(
name|BlockECReconstructionInfo
name|info
range|:
name|reconstruction
control|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|info
operator|.
name|getTargetDnInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|last
argument_list|,
name|info
operator|.
name|getTargetDnInfos
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|getSourceDnInfos
argument_list|()
operator|.
name|length
argument_list|,
name|info
operator|.
name|getLiveBlockIndices
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupSize
operator|-
name|numOfMissed
operator|==
name|dataBlocks
condition|)
block|{
comment|// It's a QUEUE_HIGHEST_PRIORITY block, so the busy DNs will be chosen
comment|// to make sure we have NUM_DATA_BLOCKS DNs to do reconstruction
comment|// work.
name|assertEquals
argument_list|(
name|dataBlocks
argument_list|,
name|info
operator|.
name|getSourceDnInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// The block has no highest priority, so we don't use the busy DNs as
comment|// sources
name|assertEquals
argument_list|(
name|groupSize
operator|-
name|numOfMissed
operator|-
name|numOfBusy
argument_list|,
name|info
operator|.
name|getSourceDnInfos
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
DECL|method|test2RecoveryTasksForSameBlockGroup ()
specifier|public
name|void
name|test2RecoveryTasksForSameBlockGroup
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
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY
argument_list|,
literal|1000
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
name|groupSize
operator|+
literal|2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|BlockManager
name|bm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|setErasureCodingPolicy
argument_list|(
literal|"/"
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
name|int
name|fileLen
init|=
name|dataBlocks
operator|*
name|blockSize
decl_stmt|;
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
literal|"/test2RecoveryTasksForSameBlockGroup"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|fileLen
index|]
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|fs
argument_list|,
name|p
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|LocatedStripedBlock
name|lb
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|p
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LocatedBlock
index|[]
name|lbs
init|=
name|StripedBlockUtil
operator|.
name|parseStripedBlockGroup
argument_list|(
name|lb
argument_list|,
name|cellSize
argument_list|,
name|dataBlocks
argument_list|,
name|parityBlocks
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getNumberOfBlocksToBeErasureCoded
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bm
operator|.
name|getPendingReconstructionBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// missing 1 block, so 1 task should be scheduled
name|DatanodeInfo
name|dn0
init|=
name|lbs
index|[
literal|0
index|]
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|dn0
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setDataNodeDead
argument_list|(
name|dn0
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|getComputedDatanodeWork
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNumberOfBlocksToBeErasureCoded
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bm
operator|.
name|getPendingReconstructionBlocksCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// missing another block, but no new task should be scheduled because
comment|// previous task isn't finished.
name|DatanodeInfo
name|dn1
init|=
name|lbs
index|[
literal|1
index|]
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|dn1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setDataNodeDead
argument_list|(
name|dn1
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|getComputedDatanodeWork
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|getNumberOfBlocksToBeErasureCoded
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bm
operator|.
name|getPendingReconstructionBlocksCount
argument_list|()
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
DECL|method|getNumberOfBlocksToBeErasureCoded (MiniDFSCluster cluster)
specifier|private
specifier|static
name|int
name|getNumberOfBlocksToBeErasureCoded
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|)
throws|throws
name|Exception
block|{
name|DatanodeManager
name|dm
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
name|int
name|count
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
name|DatanodeDescriptor
name|dd
init|=
name|dm
operator|.
name|getDatanode
argument_list|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|count
operator|+=
name|dd
operator|.
name|getNumberOfBlocksToBeErasureCoded
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**    * make sure the NN can detect the scenario where there are enough number of    * internal blocks (>=9 by default) but there is still missing data/parity    * block.    */
annotation|@
name|Test
DECL|method|testCountLiveReplicas ()
specifier|public
name|void
name|testCountLiveReplicas
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|HdfsConfiguration
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
name|DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY
argument_list|,
literal|1
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
name|groupSize
operator|+
literal|2
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
name|DistributedFileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
try|try
block|{
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|dirPath
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
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|cellSize
operator|*
name|dataBlocks
operator|*
literal|2
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// stop a dn
name|LocatedBlocks
name|blks
init|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|LocatedStripedBlock
name|block
init|=
operator|(
name|LocatedStripedBlock
operator|)
name|blks
operator|.
name|getLastLocatedBlock
argument_list|()
decl_stmt|;
name|DatanodeInfo
name|dnToStop
init|=
name|block
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|dnProp
init|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|dnToStop
operator|.
name|getXferAddr
argument_list|()
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|setDataNodeDead
argument_list|(
name|dnToStop
argument_list|)
expr_stmt|;
comment|// wait for reconstruction to happen
name|DFSTestUtil
operator|.
name|waitForReplication
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|groupSize
argument_list|,
literal|15
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// bring the dn back: 10 internal blocks now
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|dnProp
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// stop another dn: 9 internal blocks, but only cover 8 real one
name|dnToStop
operator|=
name|block
operator|.
name|getLocations
argument_list|()
index|[
literal|1
index|]
expr_stmt|;
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|dnToStop
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setDataNodeDead
argument_list|(
name|dnToStop
argument_list|)
expr_stmt|;
comment|// currently namenode is able to track the missing block. but restart NN
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|BlockManager
name|bm
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
comment|// wait 3 running cycles of redundancy monitor
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
name|DataNodeTestUtils
operator|.
name|triggerHeartbeat
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
comment|// check if NN can detect the missing internal block and finish the
comment|// reconstruction
name|StripedFileTestUtil
operator|.
name|waitForReconstructionFinished
argument_list|(
name|filePath
argument_list|,
name|fs
argument_list|,
name|groupSize
argument_list|)
expr_stmt|;
name|boolean
name|reconstructed
init|=
literal|false
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|NumberReplicas
name|num
init|=
literal|null
decl_stmt|;
name|fsn
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|BlockInfo
name|blockInfo
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode4Write
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
name|num
operator|=
name|bm
operator|.
name|countNodes
argument_list|(
name|blockInfo
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsn
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|num
operator|.
name|liveReplicas
argument_list|()
operator|>=
name|groupSize
condition|)
block|{
name|reconstructed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|reconstructed
argument_list|)
expr_stmt|;
name|blks
operator|=
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|block
operator|=
operator|(
name|LocatedStripedBlock
operator|)
name|blks
operator|.
name|getLastLocatedBlock
argument_list|()
expr_stmt|;
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|(
name|groupSize
argument_list|)
decl_stmt|;
for|for
control|(
name|byte
name|index
range|:
name|block
operator|.
name|getBlockIndices
argument_list|()
control|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|index
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
name|groupSize
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bitSet
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
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
block|}
end_class

end_unit

