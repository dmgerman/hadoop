begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
package|;
end_package

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
name|FsShell
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
name|test
operator|.
name|Whitebox
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
name|Iterator
import|;
end_import

begin_class
DECL|class|TestUnderReplicatedBlocks
specifier|public
class|class
name|TestUnderReplicatedBlocks
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
comment|// 1 min timeout
DECL|method|testSetRepIncWithUnderReplicatedBlocks ()
specifier|public
name|void
name|testSetRepIncWithUnderReplicatedBlocks
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
specifier|final
name|short
name|REPLICATION_FACTOR
init|=
literal|2
decl_stmt|;
specifier|final
name|String
name|FILE_NAME
init|=
literal|"/testFile"
decl_stmt|;
specifier|final
name|Path
name|FILE_PATH
init|=
operator|new
name|Path
argument_list|(
name|FILE_NAME
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
name|REPLICATION_FACTOR
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
comment|// create a file with one block with a replication factor of 2
specifier|final
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
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
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|,
literal|1L
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|,
name|REPLICATION_FACTOR
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyClientStats
argument_list|(
name|conf
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
comment|// remove one replica from the blocksMap so block becomes under-replicated
comment|// but the block does not get put into the under-replicated blocks queue
name|ExtendedBlock
name|b
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|)
decl_stmt|;
name|DatanodeDescriptor
name|dn
init|=
name|bm
operator|.
name|blocksMap
operator|.
name|getStorages
argument_list|(
name|b
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|bm
operator|.
name|addToInvalidates
argument_list|(
name|b
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|dn
argument_list|)
expr_stmt|;
comment|// Compute the invalidate work in NN, and trigger the heartbeat from DN
name|BlockManagerTestUtil
operator|.
name|computeAllPendingWork
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|DataNodeTestUtils
operator|.
name|triggerHeartbeat
argument_list|(
name|cluster
operator|.
name|getDataNode
argument_list|(
name|dn
operator|.
name|getIpcPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Wait to make sure the DataNode receives the deletion request
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyClientStats
argument_list|(
name|conf
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
comment|// Remove the record from blocksMap
name|bm
operator|.
name|blocksMap
operator|.
name|removeNode
argument_list|(
name|b
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|dn
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyClientStats
argument_list|(
name|conf
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
comment|// increment this file's replication factor
name|FsShell
name|shell
init|=
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-setrep"
block|,
literal|"-w"
block|,
name|Integer
operator|.
name|toString
argument_list|(
literal|1
operator|+
name|REPLICATION_FACTOR
argument_list|)
block|,
name|FILE_NAME
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyClientStats
argument_list|(
name|conf
argument_list|,
name|cluster
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
comment|/**    * The test verifies the number of outstanding replication requests for a    * given DN shouldn't exceed the limit set by configuration property    * dfs.namenode.replication.max-streams-hard-limit.    * The test does the followings:    * 1. Create a mini cluster with 2 DNs. Set large heartbeat interval so that    *    replication requests won't be picked by any DN right away.    * 2. Create a file with 10 blocks and replication factor 2. Thus each    *    of the 2 DNs have one replica of each block.    * 3. Add a DN to the cluster for later replication.    * 4. Remove a DN that has data.    * 5. Ask BlockManager to compute the replication work. This will assign    *    replication requests to the only DN that has data.    * 6. Make sure the number of pending replication requests of that DN don't    *    exceed the limit.    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
comment|// 1 min timeout
DECL|method|testNumberOfBlocksToBeReplicated ()
specifier|public
name|void
name|testNumberOfBlocksToBeReplicated
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
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_MIN_BLOCK_SIZE_KEY
argument_list|,
literal|0
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
literal|1
argument_list|)
expr_stmt|;
name|conf
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
name|int
name|NUM_OF_BLOCKS
init|=
literal|10
decl_stmt|;
specifier|final
name|short
name|REP_FACTOR
init|=
literal|2
decl_stmt|;
specifier|final
name|String
name|FILE_NAME
init|=
literal|"/testFile"
decl_stmt|;
specifier|final
name|Path
name|FILE_PATH
init|=
operator|new
name|Path
argument_list|(
name|FILE_NAME
argument_list|)
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
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
name|REP_FACTOR
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
comment|// create a file with 10 blocks with a replication factor of 2
specifier|final
name|FileSystem
name|fs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|,
name|NUM_OF_BLOCKS
argument_list|,
name|REP_FACTOR
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|waitReplication
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|,
name|REP_FACTOR
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startDataNodes
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|ExtendedBlock
name|b
init|=
name|DFSTestUtil
operator|.
name|getFirstBlock
argument_list|(
name|fs
argument_list|,
name|FILE_PATH
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storageInfos
init|=
name|bm
operator|.
name|blocksMap
operator|.
name|getStorages
argument_list|(
name|b
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
name|firstDn
init|=
name|storageInfos
operator|.
name|next
argument_list|()
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
name|secondDn
init|=
name|storageInfos
operator|.
name|next
argument_list|()
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyClientStats
argument_list|(
name|conf
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|removeDatanode
argument_list|(
name|firstDn
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_OF_BLOCKS
argument_list|,
name|bm
operator|.
name|getUnderReplicatedNotMissingBlocks
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyClientStats
argument_list|(
name|conf
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
name|bm
operator|.
name|computeDatanodeWork
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The number of replication work pending before targets are "
operator|+
literal|"determined should be non-negative."
argument_list|,
operator|(
name|Integer
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|secondDn
argument_list|,
literal|"pendingReplicationWithoutTargets"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The number of blocks to be replicated should be less than "
operator|+
literal|"or equal to "
operator|+
name|bm
operator|.
name|replicationStreamsHardLimit
argument_list|,
name|secondDn
operator|.
name|getNumberOfBlocksToBeReplicated
argument_list|()
operator|<=
name|bm
operator|.
name|replicationStreamsHardLimit
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|verifyClientStats
argument_list|(
name|conf
argument_list|,
name|cluster
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
block|}
end_class

end_unit

