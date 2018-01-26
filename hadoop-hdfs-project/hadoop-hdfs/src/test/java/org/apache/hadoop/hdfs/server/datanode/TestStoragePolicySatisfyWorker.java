begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|assertTrue
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|FSDataOutputStream
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
name|StorageType
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
name|StoragePolicySatisfierMode
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
name|BlockStorageMovementCommand
operator|.
name|BlockMovingInfo
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * This class tests the behavior of moving block replica to the given storage  * type to fulfill the storage policy requirement.  */
end_comment

begin_class
DECL|class|TestStoragePolicySatisfyWorker
specifier|public
class|class
name|TestStoragePolicySatisfyWorker
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
name|TestStoragePolicySatisfyWorker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|100
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|method|initConf (Configuration conf)
specifier|private
specifier|static
name|void
name|initConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
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
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BALANCER_MOVEDWINWIDTH_KEY
argument_list|,
literal|2000L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_STORAGE_POLICY_SATISFIER_MODE_KEY
argument_list|,
name|StoragePolicySatisfierMode
operator|.
name|INTERNAL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|initConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
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
block|}
block|}
comment|/**    * Tests to verify that the block replica is moving to ARCHIVE storage type to    * fulfill the storage policy requirement.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testMoveSingleBlockToAnotherDatanode ()
specifier|public
name|void
name|testMoveSingleBlockToAnotherDatanode
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
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|4
argument_list|)
operator|.
name|storageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|}
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
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|String
name|file
init|=
literal|"/testMoveSingleBlockToAnotherDatanode"
decl_stmt|;
comment|// write to DISK
specifier|final
name|FSDataOutputStream
name|out
init|=
name|dfs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeChars
argument_list|(
literal|"testMoveSingleBlockToAnotherDatanode"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify before movement
name|LocatedBlock
name|lb
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|file
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StorageType
index|[]
name|storageTypes
init|=
name|lb
operator|.
name|getStorageTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageType
name|storageType
range|:
name|storageTypes
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|StorageType
operator|.
name|DISK
operator|==
name|storageType
argument_list|)
expr_stmt|;
block|}
comment|// move to ARCHIVE
name|dfs
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"COLD"
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|satisfyStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|triggerHeartbeats
argument_list|()
expr_stmt|;
comment|// Wait till NameNode notified about the block location details
name|waitForLocatedBlockWithArchiveStorageType
argument_list|(
name|dfs
argument_list|,
name|file
argument_list|,
literal|2
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to verify that satisfy worker can't move blocks. If specified target    * datanode doesn't have enough space to accommodate the moving block.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testMoveWithNoSpaceAvailable ()
specifier|public
name|void
name|testMoveWithNoSpaceAvailable
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|capacity
init|=
literal|150
decl_stmt|;
specifier|final
name|String
name|rack0
init|=
literal|"/rack0"
decl_stmt|;
specifier|final
name|String
name|rack1
init|=
literal|"/rack1"
decl_stmt|;
name|long
index|[]
name|capacities
init|=
operator|new
name|long
index|[]
block|{
name|capacity
block|,
name|capacity
block|,
name|capacity
operator|/
literal|2
block|}
decl_stmt|;
name|String
index|[]
name|hosts
init|=
block|{
literal|"host0"
block|,
literal|"host1"
block|,
literal|"host2"
block|}
decl_stmt|;
name|String
index|[]
name|racks
init|=
block|{
name|rack0
block|,
name|rack1
block|,
name|rack0
block|}
decl_stmt|;
name|int
name|numOfDatanodes
init|=
name|capacities
operator|.
name|length
decl_stmt|;
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
name|numOfDatanodes
argument_list|)
operator|.
name|hosts
argument_list|(
name|hosts
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|simulatedCapacities
argument_list|(
name|capacities
argument_list|)
operator|.
name|storageTypes
argument_list|(
operator|new
name|StorageType
index|[]
index|[]
block|{
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|,
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|ARCHIVE
block|}
block|}
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
name|InetSocketAddress
index|[]
name|favoredNodes
init|=
operator|new
name|InetSocketAddress
index|[
literal|3
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
name|favoredNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// DFSClient will attempt reverse lookup. In case it resolves
comment|// "127.0.0.1" to "localhost", we manually specify the hostname.
name|favoredNodes
index|[
name|i
index|]
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getXferAddress
argument_list|()
expr_stmt|;
block|}
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|String
name|file
init|=
literal|"/testMoveWithNoSpaceAvailable"
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|1024
argument_list|,
literal|100
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|favoredNodes
argument_list|)
expr_stmt|;
comment|// verify before movement
name|LocatedBlock
name|lb
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|file
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StorageType
index|[]
name|storageTypes
init|=
name|lb
operator|.
name|getStorageTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageType
name|storageType
range|:
name|storageTypes
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|StorageType
operator|.
name|DISK
operator|==
name|storageType
argument_list|)
expr_stmt|;
block|}
comment|// move to ARCHIVE
name|dfs
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"COLD"
argument_list|)
expr_stmt|;
name|lb
operator|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|file
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|DataNode
name|src
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|DatanodeInfo
name|targetDnInfo
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeInfo
argument_list|(
name|src
operator|.
name|getXferPort
argument_list|()
argument_list|)
decl_stmt|;
name|StoragePolicySatisfyWorker
name|worker
init|=
operator|new
name|StoragePolicySatisfyWorker
argument_list|(
name|conf
argument_list|,
name|src
argument_list|)
decl_stmt|;
try|try
block|{
name|worker
operator|.
name|start
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|BlockMovingInfo
argument_list|>
name|blockMovingInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|BlockMovingInfo
name|blockMovingInfo
init|=
name|prepareBlockMovingInfo
argument_list|(
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|lb
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|targetDnInfo
argument_list|,
name|lb
operator|.
name|getStorageTypes
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|StorageType
operator|.
name|ARCHIVE
argument_list|)
decl_stmt|;
name|blockMovingInfos
operator|.
name|add
argument_list|(
name|blockMovingInfo
argument_list|)
expr_stmt|;
name|worker
operator|.
name|processBlockMovingTasks
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blockMovingInfos
argument_list|)
expr_stmt|;
name|waitForBlockMovementCompletion
argument_list|(
name|worker
argument_list|,
literal|1
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|worker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests that drop SPS work method clears all the queues.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testDropSPSWork ()
specifier|public
name|void
name|testDropSPSWork
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
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|20
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
specifier|final
name|DistributedFileSystem
name|dfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|String
name|file
init|=
literal|"/testDropSPSWork"
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|1024
argument_list|,
literal|50
operator|*
literal|100
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|,
operator|(
name|short
operator|)
literal|2
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// move to ARCHIVE
name|dfs
operator|.
name|setStoragePolicy
argument_list|(
operator|new
name|Path
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"COLD"
argument_list|)
expr_stmt|;
name|DataNode
name|src
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|DatanodeInfo
name|targetDnInfo
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeInfo
argument_list|(
name|src
operator|.
name|getXferPort
argument_list|()
argument_list|)
decl_stmt|;
name|StoragePolicySatisfyWorker
name|worker
init|=
operator|new
name|StoragePolicySatisfyWorker
argument_list|(
name|conf
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|worker
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|BlockMovingInfo
argument_list|>
name|blockMovingInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
init|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|file
argument_list|,
literal|0
argument_list|)
operator|.
name|getLocatedBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|locatedBlock
range|:
name|locatedBlocks
control|)
block|{
name|BlockMovingInfo
name|blockMovingInfo
init|=
name|prepareBlockMovingInfo
argument_list|(
name|locatedBlock
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|locatedBlock
operator|.
name|getLocations
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|targetDnInfo
argument_list|,
name|locatedBlock
operator|.
name|getStorageTypes
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|StorageType
operator|.
name|ARCHIVE
argument_list|)
decl_stmt|;
name|blockMovingInfos
operator|.
name|add
argument_list|(
name|blockMovingInfo
argument_list|)
expr_stmt|;
block|}
name|worker
operator|.
name|processBlockMovingTasks
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|blockMovingInfos
argument_list|)
expr_stmt|;
comment|// Wait till results queue build up
name|waitForBlockMovementResult
argument_list|(
name|worker
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|worker
operator|.
name|dropSPSWork
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|worker
operator|.
name|getBlocksMovementsStatusHandler
argument_list|()
operator|.
name|getMoveAttemptFinishedBlocks
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|worker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|waitForBlockMovementResult ( final StoragePolicySatisfyWorker worker, int timeout)
specifier|private
name|void
name|waitForBlockMovementResult
parameter_list|(
specifier|final
name|StoragePolicySatisfyWorker
name|worker
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|List
argument_list|<
name|Block
argument_list|>
name|completedBlocks
init|=
name|worker
operator|.
name|getBlocksMovementsStatusHandler
argument_list|()
operator|.
name|getMoveAttemptFinishedBlocks
argument_list|()
decl_stmt|;
return|return
name|completedBlocks
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForBlockMovementCompletion ( final StoragePolicySatisfyWorker worker, int expectedFinishedItemsCount, int timeout)
specifier|private
name|void
name|waitForBlockMovementCompletion
parameter_list|(
specifier|final
name|StoragePolicySatisfyWorker
name|worker
parameter_list|,
name|int
name|expectedFinishedItemsCount
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|List
argument_list|<
name|Block
argument_list|>
name|completedBlocks
init|=
name|worker
operator|.
name|getBlocksMovementsStatusHandler
argument_list|()
operator|.
name|getMoveAttemptFinishedBlocks
argument_list|()
decl_stmt|;
name|int
name|finishedCount
init|=
name|completedBlocks
operator|.
name|size
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Block movement completed count={}, expected={} and actual={}"
argument_list|,
name|completedBlocks
operator|.
name|size
argument_list|()
argument_list|,
name|expectedFinishedItemsCount
argument_list|,
name|finishedCount
argument_list|)
expr_stmt|;
return|return
name|expectedFinishedItemsCount
operator|==
name|finishedCount
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForLocatedBlockWithArchiveStorageType ( final DistributedFileSystem dfs, final String file, int expectedArchiveCount, int timeout)
specifier|private
name|void
name|waitForLocatedBlockWithArchiveStorageType
parameter_list|(
specifier|final
name|DistributedFileSystem
name|dfs
parameter_list|,
specifier|final
name|String
name|file
parameter_list|,
name|int
name|expectedArchiveCount
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|LocatedBlock
name|lb
init|=
literal|null
decl_stmt|;
try|try
block|{
name|lb
operator|=
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getLocatedBlocks
argument_list|(
name|file
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while getting located blocks"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|int
name|archiveCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StorageType
name|storageType
range|:
name|lb
operator|.
name|getStorageTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|StorageType
operator|.
name|ARCHIVE
operator|==
name|storageType
condition|)
block|{
name|archiveCount
operator|++
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Archive replica count, expected={} and actual={}"
argument_list|,
name|expectedArchiveCount
argument_list|,
name|archiveCount
argument_list|)
expr_stmt|;
return|return
name|expectedArchiveCount
operator|==
name|archiveCount
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareBlockMovingInfo (Block block, DatanodeInfo src, DatanodeInfo destin, StorageType storageType, StorageType targetStorageType)
specifier|private
name|BlockMovingInfo
name|prepareBlockMovingInfo
parameter_list|(
name|Block
name|block
parameter_list|,
name|DatanodeInfo
name|src
parameter_list|,
name|DatanodeInfo
name|destin
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|StorageType
name|targetStorageType
parameter_list|)
block|{
return|return
operator|new
name|BlockMovingInfo
argument_list|(
name|block
argument_list|,
name|src
argument_list|,
name|destin
argument_list|,
name|storageType
argument_list|,
name|targetStorageType
argument_list|)
return|;
block|}
block|}
end_class

end_unit

