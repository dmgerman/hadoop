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
name|INodeFile
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
name|net
operator|.
name|NetworkTopology
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
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

begin_class
DECL|class|TestReconstructStripedBlocksWithRackAwareness
specifier|public
class|class
name|TestReconstructStripedBlocksWithRackAwareness
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
name|TestReconstructStripedBlocksWithRackAwareness
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
name|BlockPlacementPolicy
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
name|BlockManager
operator|.
name|blockLog
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|hosts
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|hosts
init|=
operator|new
name|String
index|[]
block|{
literal|"host1"
block|,
literal|"host2"
block|,
literal|"host3"
block|,
literal|"host4"
block|,
literal|"host5"
block|,
literal|"host6"
block|,
literal|"host7"
block|,
literal|"host8"
block|,
literal|"host9"
block|,
literal|"host10"
block|}
decl_stmt|;
DECL|field|racks
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[]
block|{
literal|"/r1"
block|,
literal|"/r1"
block|,
literal|"/r2"
block|,
literal|"/r2"
block|,
literal|"/r3"
block|,
literal|"/r3"
block|,
literal|"/r4"
block|,
literal|"/r4"
block|,
literal|"/r5"
block|,
literal|"/r6"
block|}
decl_stmt|;
DECL|field|singleNodeRacks
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|singleNodeRacks
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"host9"
argument_list|,
literal|"host10"
argument_list|)
decl_stmt|;
DECL|field|blockNum
specifier|private
specifier|static
specifier|final
name|short
name|blockNum
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
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|fsn
specifier|private
name|FSNamesystem
name|fsn
decl_stmt|;
DECL|field|bm
specifier|private
name|BlockManager
name|bm
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
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
name|DFS_NAMENODE_REPLICATION_INTERVAL_KEY
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
name|DFS_NAMENODE_REPLICATION_CONSIDERLOAD_KEY
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
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|hosts
argument_list|(
name|hosts
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|hosts
operator|.
name|length
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
name|fsn
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|bm
operator|=
name|fsn
operator|.
name|getBlockManager
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
name|setErasureCodingPolicy
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|null
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
comment|/**    * When there are all the internal blocks available but they are not placed on    * enough racks, NameNode should avoid normal decoding reconstruction but copy    * an internal block to a new rack.    *    * In this test, we first need to create a scenario that a striped block has    * all the internal blocks but distributed in<6 racks. Then we check if the    * replication monitor can correctly schedule the reconstruction work for it.    *    * For the 9 internal blocks + 5 racks setup, the test does the following:    * 1. create a 6 rack cluster with 10 datanodes, where there are 2 racks only    * containing 1 datanodes each    * 2. for a striped block with 9 internal blocks, there must be one internal    * block locating in a single-node rack. find this node and stop it    * 3. namenode will trigger reconstruction for the block and since the cluster    * has only 5 racks remaining, after the reconstruction we have 9 internal    * blocks distributed in 5 racks.    * 4. we bring the datanode back, now the cluster has 6 racks again    * 5. let the datanode call reportBadBlock, this will make the namenode to    * check if the striped block is placed in>= 6 racks, and the namenode will    * put the block into the under-replicated queue    * 6. now we can check if the replication monitor works as expected    */
annotation|@
name|Test
DECL|method|testReconstructForNotEnoughRacks ()
specifier|public
name|void
name|testReconstructForNotEnoughRacks
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|BLOCK_STRIPED_CELL_SIZE
operator|*
name|NUM_DATA_BLOCKS
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
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bm
operator|.
name|numOfUnderReplicatedBlocks
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|INodeFile
name|fileNode
init|=
name|fsn
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode4Write
argument_list|(
name|file
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|BlockInfoStriped
name|blockInfo
init|=
operator|(
name|BlockInfoStriped
operator|)
name|fileNode
operator|.
name|getLastBlock
argument_list|()
decl_stmt|;
comment|// find the internal block located in the single node rack
name|Block
name|internalBlock
init|=
literal|null
decl_stmt|;
name|String
name|hostToStop
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|blockInfo
operator|.
name|storages
control|)
block|{
if|if
condition|(
name|singleNodeRacks
operator|.
name|contains
argument_list|(
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
condition|)
block|{
name|hostToStop
operator|=
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
name|internalBlock
operator|=
name|blockInfo
operator|.
name|getBlockOnStorage
argument_list|(
name|storage
argument_list|)
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|internalBlock
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|hostToStop
argument_list|)
expr_stmt|;
comment|// delete the block on the chosen datanode
name|cluster
operator|.
name|corruptBlockOnDataNodesByDeletingBlockFile
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|bm
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|internalBlock
argument_list|)
argument_list|)
expr_stmt|;
comment|// stop the chosen datanode
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|dnProp
init|=
literal|null
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
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DataNode
name|dn
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
name|hostToStop
argument_list|)
condition|)
block|{
name|dnProp
operator|=
name|cluster
operator|.
name|stopDataNode
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setDataNodeDead
argument_list|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stop datanode "
operator|+
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|NetworkTopology
name|topology
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getNetworkTopology
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|topology
operator|.
name|getNumOfRacks
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure the reconstruction work can finish
comment|// now we have 9 internal blocks in 5 racks
name|DFSTestUtil
operator|.
name|waitForReplication
argument_list|(
name|fs
argument_list|,
name|file
argument_list|,
name|blockNum
argument_list|,
literal|15
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// we now should have 9 internal blocks distributed in 5 racks
name|Set
argument_list|<
name|String
argument_list|>
name|rackSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|blockInfo
operator|.
name|storages
control|)
block|{
name|rackSet
operator|.
name|add
argument_list|(
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|rackSet
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// restart the stopped datanode
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
comment|// make sure we have 6 racks again
name|topology
operator|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getNetworkTopology
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|hosts
operator|.
name|length
argument_list|,
name|topology
operator|.
name|getNumOfLeaves
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|topology
operator|.
name|getNumOfRacks
argument_list|()
argument_list|)
expr_stmt|;
comment|// pause all the heartbeats
name|DataNode
name|badDn
init|=
literal|null
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
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|dn
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|dn
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
name|hostToStop
argument_list|)
condition|)
block|{
name|badDn
operator|=
name|dn
expr_stmt|;
block|}
block|}
assert|assert
name|badDn
operator|!=
literal|null
assert|;
comment|// let the DN report the bad block, so that the namenode will put the block
comment|// into under-replicated queue. note that the block still has 9 internal
comment|// blocks but in 5 racks
name|badDn
operator|.
name|reportBadBlocks
argument_list|(
operator|new
name|ExtendedBlock
argument_list|(
name|bm
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|internalBlock
argument_list|)
argument_list|)
expr_stmt|;
comment|// check if replication monitor correctly schedule the replication work
name|boolean
name|scheduled
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
comment|// retry 5 times
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|blockInfo
operator|.
name|storages
control|)
block|{
if|if
condition|(
name|storage
operator|!=
literal|null
condition|)
block|{
name|DatanodeDescriptor
name|dn
init|=
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dn
operator|.
name|getNumberOfBlocksToBeErasureCoded
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|dn
operator|.
name|getNumberOfBlocksToBeReplicated
argument_list|()
operator|==
literal|1
condition|)
block|{
name|scheduled
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|scheduled
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|scheduled
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

