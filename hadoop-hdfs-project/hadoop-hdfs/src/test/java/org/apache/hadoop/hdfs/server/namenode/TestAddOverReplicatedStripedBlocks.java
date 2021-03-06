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
name|SimulatedFSDataset
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
name|Ignore
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
name|Test
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
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestAddOverReplicatedStripedBlocks
specifier|public
class|class
name|TestAddOverReplicatedStripedBlocks
block|{
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
DECL|field|dirPath
specifier|private
specifier|final
name|Path
name|dirPath
init|=
operator|new
name|Path
argument_list|(
literal|"/striped"
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
DECL|field|numDNs
specifier|private
specifier|final
name|int
name|numDNs
init|=
name|groupSize
operator|+
literal|3
decl_stmt|;
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
comment|// disable block recovery
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
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|SimulatedFSDataset
operator|.
name|setFactory
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
name|numDNs
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
name|enableErasureCodingPolicy
argument_list|(
name|ecPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|setErasureCodingPolicy
argument_list|(
name|dirPath
operator|.
name|toString
argument_list|()
argument_list|,
name|ecPolicy
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
DECL|method|testProcessOverReplicatedStripedBlock ()
specifier|public
name|void
name|testProcessOverReplicatedStripedBlock
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a file which has exact one block group to the first GROUP_SIZE DNs
name|long
name|fileLen
init|=
name|dataBlocks
operator|*
name|blockSize
decl_stmt|;
name|DFSTestUtil
operator|.
name|createStripedFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|stripesPerBlock
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|fileLen
argument_list|)
decl_stmt|;
name|LocatedStripedBlock
name|bg
init|=
call|(
name|LocatedStripedBlock
call|)
argument_list|(
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|gs
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|String
name|bpid
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|long
name|groupId
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
name|groupId
argument_list|,
name|blockSize
argument_list|,
name|gs
argument_list|)
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
name|groupSize
condition|;
name|i
operator|++
control|)
block|{
name|blk
operator|.
name|setBlockId
argument_list|(
name|groupId
operator|+
name|i
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// let a internal block be over replicated with 2 redundant blocks.
name|blk
operator|.
name|setBlockId
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|numDNs
operator|-
literal|3
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|numDNs
operator|-
literal|2
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
comment|// let a internal block be over replicated with 1 redundant block.
name|blk
operator|.
name|setBlockId
argument_list|(
name|groupId
operator|+
name|dataBlocks
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|numDNs
operator|-
literal|1
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
comment|// update blocksMap
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// add to invalidates
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// datanode delete block
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// update blocksMap
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// verify that all internal blocks exists
name|lbs
operator|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|fileLen
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyLocatedStripedBlocks
argument_list|(
name|lbs
argument_list|,
name|groupSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessOverReplicatedSBSmallerThanFullBlocks ()
specifier|public
name|void
name|testProcessOverReplicatedSBSmallerThanFullBlocks
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a EC file which doesn't fill full internal blocks.
name|int
name|fileLen
init|=
name|cellSize
operator|*
operator|(
name|dataBlocks
operator|-
literal|1
operator|)
decl_stmt|;
name|byte
index|[]
name|content
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
name|filePath
argument_list|,
operator|new
name|String
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|fileLen
argument_list|)
decl_stmt|;
name|LocatedStripedBlock
name|bg
init|=
call|(
name|LocatedStripedBlock
call|)
argument_list|(
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|gs
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|String
name|bpid
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|long
name|groupId
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
name|groupId
argument_list|,
name|blockSize
argument_list|,
name|gs
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|DatanodeInfo
argument_list|>
name|infos
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|bg
operator|.
name|getLocations
argument_list|()
argument_list|)
decl_stmt|;
comment|// let a internal block be over replicated with (numDNs - GROUP_SIZE + 1)
comment|// redundant blocks. Therefor number of internal blocks is over GROUP_SIZE.
name|blk
operator|.
name|setBlockId
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DataNode
argument_list|>
name|dataNodeList
init|=
name|cluster
operator|.
name|getDataNodes
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
name|numDNs
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|infos
operator|.
name|contains
argument_list|(
name|dataNodeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
condition|)
block|{
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"XXX: inject block into datanode "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|// update blocksMap
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// add to invalidates
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// datanode delete block
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// update blocksMap
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// verify that all internal blocks exists
name|lbs
operator|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|fileLen
argument_list|)
expr_stmt|;
name|StripedFileTestUtil
operator|.
name|verifyLocatedStripedBlocks
argument_list|(
name|lbs
argument_list|,
name|groupSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessOverReplicatedAndCorruptStripedBlock ()
specifier|public
name|void
name|testProcessOverReplicatedAndCorruptStripedBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|fileLen
init|=
name|dataBlocks
operator|*
name|blockSize
decl_stmt|;
name|DFSTestUtil
operator|.
name|createStripedFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|stripesPerBlock
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|fileLen
argument_list|)
decl_stmt|;
name|LocatedStripedBlock
name|bg
init|=
call|(
name|LocatedStripedBlock
call|)
argument_list|(
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|gs
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|String
name|bpid
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|long
name|groupId
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
name|groupId
argument_list|,
name|blockSize
argument_list|,
name|gs
argument_list|)
decl_stmt|;
name|BlockInfoStriped
name|blockInfo
init|=
operator|new
name|BlockInfoStriped
argument_list|(
name|blk
argument_list|,
name|StripedFileTestUtil
operator|.
name|getDefaultECPolicy
argument_list|()
argument_list|)
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
name|groupSize
condition|;
name|i
operator|++
control|)
block|{
name|blk
operator|.
name|setBlockId
argument_list|(
name|groupId
operator|+
name|i
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// let a internal block be corrupt
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
name|List
argument_list|<
name|DatanodeInfo
argument_list|>
name|infos
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|bg
operator|.
name|getLocations
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|storages
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|bg
operator|.
name|getStorageIDs
argument_list|()
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
name|lbs
operator|.
name|getLastLocatedBlock
argument_list|()
operator|.
name|getBlock
argument_list|()
argument_list|,
name|infos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|storages
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"TEST"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|bm
operator|.
name|countNodes
argument_list|(
name|bm
operator|.
name|getStoredBlock
argument_list|(
name|blockInfo
argument_list|)
argument_list|)
operator|.
name|corruptReplicas
argument_list|()
argument_list|)
expr_stmt|;
comment|// let a internal block be over replicated with 2 redundant block.
name|blk
operator|.
name|setBlockId
argument_list|(
name|groupId
operator|+
literal|2
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|numDNs
operator|-
literal|3
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|numDNs
operator|-
literal|2
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
comment|// update blocksMap
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// verify that all internal blocks exists except b0
comment|// the redundant internal blocks will not be deleted before the corrupted
comment|// block gets reconstructed. but since we set
comment|// DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY to 0, the reconstruction will
comment|// not happen
name|lbs
operator|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|fileLen
argument_list|)
expr_stmt|;
name|bg
operator|=
call|(
name|LocatedStripedBlock
call|)
argument_list|(
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|groupSize
operator|+
literal|1
argument_list|,
name|bg
operator|.
name|getBlockIndices
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|groupSize
operator|+
literal|1
argument_list|,
name|bg
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|BitSet
name|set
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
name|bg
operator|.
name|getBlockIndices
argument_list|()
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertFalse
argument_list|(
name|set
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|groupSize
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// This test is going to be rewritten in HDFS-10854. Ignoring this test
comment|// temporarily as it fails with the fix for HDFS-10301.
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testProcessOverReplicatedAndMissingStripedBlock ()
specifier|public
name|void
name|testProcessOverReplicatedAndMissingStripedBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|fileLen
init|=
name|cellSize
operator|*
name|dataBlocks
decl_stmt|;
name|DFSTestUtil
operator|.
name|createStripedFile
argument_list|(
name|cluster
argument_list|,
name|filePath
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|stripesPerBlock
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LocatedBlocks
name|lbs
init|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|fileLen
argument_list|)
decl_stmt|;
name|LocatedStripedBlock
name|bg
init|=
call|(
name|LocatedStripedBlock
call|)
argument_list|(
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|gs
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getGenerationStamp
argument_list|()
decl_stmt|;
name|String
name|bpid
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
name|long
name|groupId
init|=
name|bg
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|(
name|groupId
argument_list|,
name|blockSize
argument_list|,
name|gs
argument_list|)
decl_stmt|;
comment|// only inject GROUP_SIZE - 1 blocks, so there is one block missing
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
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|blk
operator|.
name|setBlockId
argument_list|(
name|groupId
operator|+
name|i
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|i
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// let a internal block be over replicated with 2 redundant blocks.
comment|// Therefor number of internal blocks is over GROUP_SIZE. (5 data blocks +
comment|// 3 parity blocks  + 2 redundant blocks> GROUP_SIZE)
name|blk
operator|.
name|setBlockId
argument_list|(
name|groupId
operator|+
literal|2
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|numDNs
operator|-
literal|3
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|injectBlocks
argument_list|(
name|numDNs
operator|-
literal|2
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|blk
argument_list|)
argument_list|,
name|bpid
argument_list|)
expr_stmt|;
comment|// update blocksMap
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// add to invalidates
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// datanode delete block
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// update blocksMap
name|cluster
operator|.
name|triggerBlockReports
argument_list|()
expr_stmt|;
comment|// Since one block is missing, then over-replicated blocks will not be
comment|// deleted until reconstruction happens
name|lbs
operator|=
name|cluster
operator|.
name|getNameNodeRpc
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
name|fileLen
argument_list|)
expr_stmt|;
name|bg
operator|=
call|(
name|LocatedStripedBlock
call|)
argument_list|(
name|lbs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|groupSize
operator|+
literal|1
argument_list|,
name|bg
operator|.
name|getBlockIndices
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|groupSize
operator|+
literal|1
argument_list|,
name|bg
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|BitSet
name|set
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
name|bg
operator|.
name|getBlockIndices
argument_list|()
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertFalse
argument_list|(
name|set
operator|.
name|get
argument_list|(
name|groupSize
operator|-
literal|1
argument_list|)
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
name|groupSize
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|set
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

