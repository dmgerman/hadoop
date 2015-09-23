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
name|protocol
operator|.
name|BlockECRecoveryCommand
operator|.
name|BlockECRecoveryInfo
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
name|util
operator|.
name|List
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
name|StripedFileTestUtil
operator|.
name|BLOCK_STRIPED_CELL_SIZE
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
name|StripedFileTestUtil
operator|.
name|NUM_DATA_BLOCKS
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
name|StripedFileTestUtil
operator|.
name|NUM_PARITY_BLOCKS
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
DECL|class|TestRecoverStripedBlocks
specifier|public
class|class
name|TestRecoverStripedBlocks
block|{
DECL|field|GROUP_SIZE
specifier|private
specifier|final
name|short
name|GROUP_SIZE
init|=
call|(
name|short
call|)
argument_list|(
name|NUM_DATA_BLOCKS
operator|+
name|NUM_PARITY_BLOCKS
argument_list|)
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
DECL|method|testMissingStripedBlockWithBusyNode1 ()
specifier|public
name|void
name|testMissingStripedBlockWithBusyNode1
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMissingStripedBlock
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMissingStripedBlockWithBusyNode2 ()
specifier|public
name|void
name|testMissingStripedBlockWithBusyNode2
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMissingStripedBlock
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start GROUP_SIZE + 1 datanodes.    * Inject striped blocks to first GROUP_SIZE datanodes.    * Then make numOfBusy datanodes busy, make numOfMissed datanodes missed.    * Then trigger BlockManager to compute recovery works. (so all recovery work    * will be scheduled to the last datanode)    * Finally, verify the recovery work of the last datanode.    */
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
name|GROUP_SIZE
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
name|BLOCK_STRIPED_CELL_SIZE
operator|*
name|NUM_DATA_BLOCKS
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
name|GROUP_SIZE
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
comment|// all the recovery work will be scheduled on the last DN
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
name|GROUP_SIZE
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
name|BlockECRecoveryInfo
argument_list|>
name|recovery
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
name|BlockECRecoveryInfo
name|info
range|:
name|recovery
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
name|GROUP_SIZE
operator|-
name|numOfMissed
operator|==
name|NUM_DATA_BLOCKS
condition|)
block|{
comment|// It's a QUEUE_HIGHEST_PRIORITY block, so the busy DNs will be chosen
comment|// to make sure we have NUM_DATA_BLOCKS DNs to do recovery work.
name|assertEquals
argument_list|(
name|NUM_DATA_BLOCKS
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
name|GROUP_SIZE
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
block|}
end_class

end_unit

