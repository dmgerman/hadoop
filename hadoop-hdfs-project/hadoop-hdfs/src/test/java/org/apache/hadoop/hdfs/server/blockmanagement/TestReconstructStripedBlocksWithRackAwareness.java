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
name|BeforeClass
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
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
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
name|Set
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
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|BlockManager
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
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
DECL|field|hosts
specifier|private
specifier|final
name|String
index|[]
name|hosts
init|=
name|getHosts
argument_list|(
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|1
argument_list|)
decl_stmt|;
DECL|field|racks
specifier|private
specifier|final
name|String
index|[]
name|racks
init|=
name|getRacks
argument_list|(
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|1
argument_list|,
name|dataBlocks
argument_list|)
decl_stmt|;
DECL|method|getHosts (int numHosts)
specifier|private
specifier|static
name|String
index|[]
name|getHosts
parameter_list|(
name|int
name|numHosts
parameter_list|)
block|{
name|String
index|[]
name|hosts
init|=
operator|new
name|String
index|[
name|numHosts
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
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hosts
index|[
name|i
index|]
operator|=
literal|"host"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
expr_stmt|;
block|}
return|return
name|hosts
return|;
block|}
DECL|method|getRacks (int numHosts, int numRacks)
specifier|private
specifier|static
name|String
index|[]
name|getRacks
parameter_list|(
name|int
name|numHosts
parameter_list|,
name|int
name|numRacks
parameter_list|)
block|{
name|String
index|[]
name|racks
init|=
operator|new
name|String
index|[
name|numHosts
index|]
decl_stmt|;
name|int
name|numHostEachRack
init|=
name|numHosts
operator|/
name|numRacks
decl_stmt|;
name|int
name|residue
init|=
name|numHosts
operator|%
name|numRacks
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numRacks
condition|;
name|i
operator|++
control|)
block|{
name|int
name|limit
init|=
name|i
operator|<=
name|residue
condition|?
name|numHostEachRack
operator|+
literal|1
else|:
name|numHostEachRack
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|limit
condition|;
name|k
operator|++
control|)
block|{
name|racks
index|[
name|j
operator|++
index|]
operator|=
literal|"/r"
operator|+
name|i
expr_stmt|;
block|}
block|}
assert|assert
name|j
operator|==
name|numHosts
assert|;
return|return
name|racks
return|;
block|}
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
specifier|final
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
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
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DECOMMISSION_INTERVAL_KEY
argument_list|,
literal|1
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
DECL|method|stopDataNode (String hostname)
specifier|private
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|stopDataNode
parameter_list|(
name|String
name|hostname
parameter_list|)
throws|throws
name|IOException
block|{
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
name|hostname
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
return|return
name|dnProp
return|;
block|}
DECL|method|getDataNode (String host)
specifier|private
name|DataNode
name|getDataNode
parameter_list|(
name|String
name|host
parameter_list|)
block|{
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
name|getDatanodeId
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
name|host
argument_list|)
condition|)
block|{
return|return
name|dn
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * When there are all the internal blocks available but they are not placed on    * enough racks, NameNode should avoid normal decoding reconstruction but copy    * an internal block to a new rack.    *    * In this test, we first need to create a scenario that a striped block has    * all the internal blocks but distributed in<6 racks. Then we check if the    * redundancy monitor can correctly schedule the reconstruction work for it.    */
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
name|LOG
operator|.
name|info
argument_list|(
literal|"cluster hosts: {}, racks: {}"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|hosts
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|racks
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_ENABLED_KEY
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
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|lastHost
init|=
name|stopDataNode
argument_list|(
name|hosts
index|[
name|hosts
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
decl_stmt|;
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
comment|// the file's block is in 9 dn but 5 racks
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|file
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
name|dataBlocks
operator|-
literal|1
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
name|lastHost
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// make sure we have 6 racks again
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
name|dataBlocks
argument_list|,
name|topology
operator|.
name|getNumOfRacks
argument_list|()
argument_list|)
expr_stmt|;
comment|// pause all the heartbeats
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
block|}
name|fsn
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|bm
operator|.
name|processMisReplicatedBlocks
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
comment|// check if redundancy monitor correctly schedule the reconstruction work.
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
annotation|@
name|Test
DECL|method|testChooseExcessReplicasToDelete ()
specifier|public
name|void
name|testChooseExcessReplicasToDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_ENABLED_KEY
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
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|lastHost
init|=
name|stopDataNode
argument_list|(
name|hosts
index|[
name|hosts
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
decl_stmt|;
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
comment|// stop host1
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|host1
init|=
name|stopDataNode
argument_list|(
literal|"host1"
argument_list|)
decl_stmt|;
comment|// bring last host back
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|lastHost
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// wait for reconstruction to finish
specifier|final
name|short
name|blockNum
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
comment|// restart host1
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|host1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
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
literal|"host1"
argument_list|)
condition|)
block|{
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|dn
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// make sure the excess replica is detected, and we delete host1's replica
comment|// so that we have 6 racks
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
name|file
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
for|for
control|(
name|DatanodeInfo
name|dn
range|:
name|block
operator|.
name|getLocations
argument_list|()
control|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|dn
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"host1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * In case we have 10 internal blocks on 5 racks, where 9 of blocks are live    * and 1 decommissioning, make sure the reconstruction happens correctly.    */
annotation|@
name|Test
DECL|method|testReconstructionWithDecommission ()
specifier|public
name|void
name|testReconstructionWithDecommission
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EC_POLICIES_ENABLED_KEY
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
specifier|final
name|String
index|[]
name|rackNames
init|=
name|getRacks
argument_list|(
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
argument_list|,
name|dataBlocks
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|hostNames
init|=
name|getHosts
argument_list|(
name|dataBlocks
operator|+
name|parityBlocks
operator|+
literal|2
argument_list|)
decl_stmt|;
comment|// we now have 11 hosts on 6 racks with distribution: 2-2-2-2-2-1
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
name|rackNames
argument_list|)
operator|.
name|hosts
argument_list|(
name|hostNames
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|hostNames
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
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
operator|.
name|getName
argument_list|()
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
specifier|final
name|DatanodeManager
name|dm
init|=
name|bm
operator|.
name|getDatanodeManager
argument_list|()
decl_stmt|;
comment|// stop h9 and h10 and create a file with 6+3 internal blocks
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|h9
init|=
name|stopDataNode
argument_list|(
name|hostNames
index|[
name|hostNames
operator|.
name|length
operator|-
literal|3
index|]
argument_list|)
decl_stmt|;
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|h10
init|=
name|stopDataNode
argument_list|(
name|hostNames
index|[
name|hostNames
operator|.
name|length
operator|-
literal|2
index|]
argument_list|)
decl_stmt|;
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
specifier|final
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
name|getINode
argument_list|(
name|file
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
comment|// bring h9 back
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|h9
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|// stop h11 so that the reconstruction happens
name|MiniDFSCluster
operator|.
name|DataNodeProperties
name|h11
init|=
name|stopDataNode
argument_list|(
name|hostNames
index|[
name|hostNames
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
decl_stmt|;
name|boolean
name|recovered
init|=
name|bm
operator|.
name|countNodes
argument_list|(
name|blockInfo
argument_list|)
operator|.
name|liveReplicas
argument_list|()
operator|>=
name|dataBlocks
operator|+
name|parityBlocks
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
literal|10
operator|&
operator|!
name|recovered
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|recovered
operator|=
name|bm
operator|.
name|countNodes
argument_list|(
name|blockInfo
argument_list|)
operator|.
name|liveReplicas
argument_list|()
operator|>=
name|dataBlocks
operator|+
name|parityBlocks
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|recovered
argument_list|)
expr_stmt|;
comment|// mark h9 as decommissioning
name|DataNode
name|datanode9
init|=
name|getDataNode
argument_list|(
name|hostNames
index|[
name|hostNames
operator|.
name|length
operator|-
literal|3
index|]
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|datanode9
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeDescriptor
name|dn9
init|=
name|dm
operator|.
name|getDatanode
argument_list|(
name|datanode9
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
decl_stmt|;
name|dn9
operator|.
name|startDecommission
argument_list|()
expr_stmt|;
comment|// restart h10 and h11
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|h10
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartDataNode
argument_list|(
name|h11
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|getDataNode
argument_list|(
name|hostNames
index|[
name|hostNames
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// start decommissioning h9
name|boolean
name|satisfied
init|=
name|bm
operator|.
name|isPlacementPolicySatisfied
argument_list|(
name|blockInfo
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|satisfied
argument_list|)
expr_stmt|;
specifier|final
name|DecommissionManager
name|decomManager
init|=
operator|(
name|DecommissionManager
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|dm
argument_list|,
literal|"decomManager"
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
name|dn9
operator|.
name|stopDecommission
argument_list|()
expr_stmt|;
name|decomManager
operator|.
name|startDecommission
argument_list|(
name|dn9
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
comment|// make sure the decommission finishes and the block in on 6 racks
name|boolean
name|decommissioned
init|=
name|dn9
operator|.
name|isDecommissioned
argument_list|()
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
literal|10
operator|&&
operator|!
name|decommissioned
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|decommissioned
operator|=
name|dn9
operator|.
name|isDecommissioned
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|decommissioned
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bm
operator|.
name|isPlacementPolicySatisfied
argument_list|(
name|blockInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

