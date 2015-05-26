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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|DatanodeRegistration
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
name|ReceivedDeletedBlockInfo
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
name|ReceivedDeletedBlockInfo
operator|.
name|BlockStatus
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
name|StorageReceivedDeletedBlocks
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
name|Mockito
import|;
end_import

begin_comment
comment|/**  * This class tests the internals of PendingReplicationBlocks.java,  * as well as how PendingReplicationBlocks acts in BlockManager  */
end_comment

begin_class
DECL|class|TestPendingReplication
specifier|public
class|class
name|TestPendingReplication
block|{
DECL|field|TIMEOUT
specifier|final
specifier|static
name|int
name|TIMEOUT
init|=
literal|3
decl_stmt|;
comment|// 3 seconds
DECL|field|DFS_REPLICATION_INTERVAL
specifier|private
specifier|static
specifier|final
name|int
name|DFS_REPLICATION_INTERVAL
init|=
literal|1
decl_stmt|;
comment|// Number of datanodes in the cluster
DECL|field|DATANODE_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|DATANODE_COUNT
init|=
literal|5
decl_stmt|;
DECL|method|genBlockInfo (long id, long length, long gs)
specifier|private
name|BlockInfo
name|genBlockInfo
parameter_list|(
name|long
name|id
parameter_list|,
name|long
name|length
parameter_list|,
name|long
name|gs
parameter_list|)
block|{
return|return
operator|new
name|BlockInfoContiguous
argument_list|(
operator|new
name|Block
argument_list|(
name|id
argument_list|,
name|length
argument_list|,
name|gs
argument_list|)
argument_list|,
operator|(
name|short
operator|)
name|DATANODE_COUNT
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testPendingReplication ()
specifier|public
name|void
name|testPendingReplication
parameter_list|()
block|{
name|PendingReplicationBlocks
name|pendingReplications
decl_stmt|;
name|pendingReplications
operator|=
operator|new
name|PendingReplicationBlocks
argument_list|(
name|TIMEOUT
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|pendingReplications
operator|.
name|start
argument_list|()
expr_stmt|;
comment|//
comment|// Add 10 blocks to pendingReplications.
comment|//
name|DatanodeStorageInfo
index|[]
name|storages
init|=
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
literal|10
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
name|storages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BlockInfo
name|block
init|=
name|genBlockInfo
argument_list|(
name|i
argument_list|,
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DatanodeStorageInfo
index|[]
name|targets
init|=
operator|new
name|DatanodeStorageInfo
index|[
name|i
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|storages
argument_list|,
literal|0
argument_list|,
name|targets
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|pendingReplications
operator|.
name|increment
argument_list|(
name|block
argument_list|,
name|DatanodeStorageInfo
operator|.
name|toDatanodeDescriptors
argument_list|(
name|targets
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Size of pendingReplications "
argument_list|,
literal|10
argument_list|,
name|pendingReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//
comment|// remove one item and reinsert it
comment|//
name|BlockInfo
name|blk
init|=
name|genBlockInfo
argument_list|(
literal|8
argument_list|,
literal|8
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|pendingReplications
operator|.
name|decrement
argument_list|(
name|blk
argument_list|,
name|storages
index|[
literal|7
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|)
expr_stmt|;
comment|// removes one replica
name|assertEquals
argument_list|(
literal|"pendingReplications.getNumReplicas "
argument_list|,
literal|7
argument_list|,
name|pendingReplications
operator|.
name|getNumReplicas
argument_list|(
name|blk
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
literal|7
condition|;
name|i
operator|++
control|)
block|{
comment|// removes all replicas
name|pendingReplications
operator|.
name|decrement
argument_list|(
name|blk
argument_list|,
name|storages
index|[
name|i
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|==
literal|9
argument_list|)
expr_stmt|;
name|pendingReplications
operator|.
name|increment
argument_list|(
name|blk
argument_list|,
name|DatanodeStorageInfo
operator|.
name|toDatanodeDescriptors
argument_list|(
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
literal|8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
comment|//
comment|// verify that the number of replicas returned
comment|// are sane.
comment|//
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
condition|;
name|i
operator|++
control|)
block|{
name|BlockInfo
name|block
init|=
name|genBlockInfo
argument_list|(
name|i
argument_list|,
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|numReplicas
init|=
name|pendingReplications
operator|.
name|getNumReplicas
argument_list|(
name|block
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|numReplicas
operator|==
name|i
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// verify that nothing has timed out so far
comment|//
name|assertTrue
argument_list|(
name|pendingReplications
operator|.
name|getTimedOutBlocks
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|//
comment|// Wait for one second and then insert some more items.
comment|//
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|15
condition|;
name|i
operator|++
control|)
block|{
name|BlockInfo
name|block
init|=
name|genBlockInfo
argument_list|(
name|i
argument_list|,
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|pendingReplications
operator|.
name|increment
argument_list|(
name|block
argument_list|,
name|DatanodeStorageInfo
operator|.
name|toDatanodeDescriptors
argument_list|(
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|==
literal|15
argument_list|)
expr_stmt|;
comment|//
comment|// Wait for everything to timeout.
comment|//
name|int
name|loop
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{       }
name|loop
operator|++
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Had to wait for "
operator|+
name|loop
operator|+
literal|" seconds for the lot to timeout"
argument_list|)
expr_stmt|;
comment|//
comment|// Verify that everything has timed out.
comment|//
name|assertEquals
argument_list|(
literal|"Size of pendingReplications "
argument_list|,
literal|0
argument_list|,
name|pendingReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Block
index|[]
name|timedOut
init|=
name|pendingReplications
operator|.
name|getTimedOutBlocks
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|timedOut
operator|!=
literal|null
operator|&&
name|timedOut
operator|.
name|length
operator|==
literal|15
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
name|timedOut
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|timedOut
index|[
name|i
index|]
operator|.
name|getBlockId
argument_list|()
operator|<
literal|15
argument_list|)
expr_stmt|;
block|}
name|pendingReplications
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/* Test that processPendingReplications will use the most recent  * blockinfo from the blocksmap by placing a larger genstamp into  * the blocksmap.  */
annotation|@
name|Test
DECL|method|testProcessPendingReplications ()
specifier|public
name|void
name|testProcessPendingReplications
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
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
name|DFS_NAMENODE_REPLICATION_PENDING_TIMEOUT_SEC_KEY
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|Block
name|block
decl_stmt|;
name|BlockInfoContiguous
name|blockInfo
decl_stmt|;
try|try
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
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|BlockManager
name|blkManager
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|PendingReplicationBlocks
name|pendingReplications
init|=
name|blkManager
operator|.
name|pendingReplications
decl_stmt|;
name|UnderReplicatedBlocks
name|neededReplications
init|=
name|blkManager
operator|.
name|neededReplications
decl_stmt|;
name|BlocksMap
name|blocksMap
init|=
name|blkManager
operator|.
name|blocksMap
decl_stmt|;
comment|//
comment|// Add 1 block to pendingReplications with GenerationStamp = 0.
comment|//
name|block
operator|=
operator|new
name|Block
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|blockInfo
operator|=
operator|new
name|BlockInfoContiguous
argument_list|(
name|block
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|pendingReplications
operator|.
name|increment
argument_list|(
name|blockInfo
argument_list|,
name|DatanodeStorageInfo
operator|.
name|toDatanodeDescriptors
argument_list|(
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BlockCollection
name|bc
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|BlockCollection
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
operator|(
name|short
operator|)
literal|3
argument_list|)
operator|.
name|when
argument_list|(
name|bc
argument_list|)
operator|.
name|getPreferredBlockReplication
argument_list|()
expr_stmt|;
comment|// Place into blocksmap with GenerationStamp = 1
name|blockInfo
operator|.
name|setGenerationStamp
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|blocksMap
operator|.
name|addBlockCollection
argument_list|(
name|blockInfo
argument_list|,
name|bc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size of pendingReplications "
argument_list|,
literal|1
argument_list|,
name|pendingReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add a second block to pendingReplications that has no
comment|// corresponding entry in blocksmap
name|block
operator|=
operator|new
name|Block
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|blockInfo
operator|=
operator|new
name|BlockInfoContiguous
argument_list|(
name|block
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|pendingReplications
operator|.
name|increment
argument_list|(
name|blockInfo
argument_list|,
name|DatanodeStorageInfo
operator|.
name|toDatanodeDescriptors
argument_list|(
name|DFSTestUtil
operator|.
name|createDatanodeStorageInfos
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify 2 blocks in pendingReplications
name|assertEquals
argument_list|(
literal|"Size of pendingReplications "
argument_list|,
literal|2
argument_list|,
name|pendingReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//
comment|// Wait for everything to timeout.
comment|//
while|while
condition|(
name|pendingReplications
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
block|}
comment|//
comment|// Verify that block moves to neededReplications
comment|//
while|while
condition|(
name|neededReplications
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
block|}
comment|// Verify that the generation stamp we will try to replicate
comment|// is now 1
for|for
control|(
name|Block
name|b
range|:
name|neededReplications
control|)
block|{
name|assertEquals
argument_list|(
literal|"Generation stamp is 1 "
argument_list|,
literal|1
argument_list|,
name|b
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Verify size of neededReplications is exactly 1.
name|assertEquals
argument_list|(
literal|"size of neededReplications is 1 "
argument_list|,
literal|1
argument_list|,
name|neededReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
comment|/**    * Test if DatanodeProtocol#blockReceivedAndDeleted can correctly update the    * pending replications. Also make sure the blockReceivedAndDeleted call is    * idempotent to the pending replications.     */
annotation|@
name|Test
DECL|method|testBlockReceived ()
specifier|public
name|void
name|testBlockReceived
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
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
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
try|try
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
name|DistributedFileSystem
name|hdfs
init|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FSNamesystem
name|fsn
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|BlockManager
name|blkManager
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
specifier|final
name|String
name|file
init|=
literal|"/tmp.txt"
decl_stmt|;
specifier|final
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|short
name|replFactor
init|=
literal|1
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|hdfs
argument_list|,
name|filePath
argument_list|,
literal|1024L
argument_list|,
name|replFactor
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// temporarily stop the heartbeat
name|ArrayList
argument_list|<
name|DataNode
argument_list|>
name|datanodes
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
name|DATANODE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|hdfs
operator|.
name|setReplication
argument_list|(
name|filePath
argument_list|,
operator|(
name|short
operator|)
name|DATANODE_COUNT
argument_list|)
expr_stmt|;
name|BlockManagerTestUtil
operator|.
name|computeAllPendingWork
argument_list|(
name|blkManager
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blkManager
operator|.
name|pendingReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
argument_list|)
operator|.
name|asFile
argument_list|()
decl_stmt|;
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
name|DATANODE_COUNT
operator|-
literal|1
argument_list|,
name|blkManager
operator|.
name|pendingReplications
operator|.
name|getNumReplicas
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|LocatedBlock
name|locatedBlock
init|=
name|hdfs
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
name|DatanodeInfo
name|existingDn
init|=
operator|(
name|locatedBlock
operator|.
name|getLocations
argument_list|()
operator|)
index|[
literal|0
index|]
decl_stmt|;
name|int
name|reportDnNum
init|=
literal|0
decl_stmt|;
name|String
name|poolId
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
comment|// let two datanodes (other than the one that already has the data) to
comment|// report to NN
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DATANODE_COUNT
operator|&&
name|reportDnNum
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|existingDn
argument_list|)
condition|)
block|{
name|DatanodeRegistration
name|dnR
init|=
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDNRegistrationForBP
argument_list|(
name|poolId
argument_list|)
decl_stmt|;
name|StorageReceivedDeletedBlocks
index|[]
name|report
init|=
block|{
operator|new
name|StorageReceivedDeletedBlocks
argument_list|(
literal|"Fake-storage-ID-Ignored"
argument_list|,
operator|new
name|ReceivedDeletedBlockInfo
index|[]
block|{
operator|new
name|ReceivedDeletedBlockInfo
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|,
name|BlockStatus
operator|.
name|RECEIVED_BLOCK
argument_list|,
literal|""
argument_list|)
block|}
argument_list|)
block|}
decl_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|dnR
argument_list|,
name|poolId
argument_list|,
name|report
argument_list|)
expr_stmt|;
name|reportDnNum
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|DATANODE_COUNT
operator|-
literal|3
argument_list|,
name|blkManager
operator|.
name|pendingReplications
operator|.
name|getNumReplicas
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// let the same datanodes report again
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DATANODE_COUNT
operator|&&
name|reportDnNum
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|existingDn
argument_list|)
condition|)
block|{
name|DatanodeRegistration
name|dnR
init|=
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDNRegistrationForBP
argument_list|(
name|poolId
argument_list|)
decl_stmt|;
name|StorageReceivedDeletedBlocks
index|[]
name|report
init|=
block|{
operator|new
name|StorageReceivedDeletedBlocks
argument_list|(
literal|"Fake-storage-ID-Ignored"
argument_list|,
operator|new
name|ReceivedDeletedBlockInfo
index|[]
block|{
operator|new
name|ReceivedDeletedBlockInfo
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|,
name|BlockStatus
operator|.
name|RECEIVED_BLOCK
argument_list|,
literal|""
argument_list|)
block|}
argument_list|)
block|}
decl_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|dnR
argument_list|,
name|poolId
argument_list|,
name|report
argument_list|)
expr_stmt|;
name|reportDnNum
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|DATANODE_COUNT
operator|-
literal|3
argument_list|,
name|blkManager
operator|.
name|pendingReplications
operator|.
name|getNumReplicas
argument_list|(
name|blocks
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// re-enable heartbeat for the datanode that has data
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DATANODE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|DataNodeTestUtils
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DataNodeTestUtils
operator|.
name|triggerHeartbeat
argument_list|(
name|datanodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blkManager
operator|.
name|pendingReplications
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
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
block|}
comment|/**    * Test if BlockManager can correctly remove corresponding pending records    * when a file is deleted    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testPendingAndInvalidate ()
specifier|public
name|void
name|testPendingAndInvalidate
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|CONF
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
literal|1024
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
name|MiniDFSCluster
name|cluster
init|=
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
decl_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|BlockManager
name|bm
init|=
name|namesystem
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
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
comment|// 1. create a file
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/tmp.txt"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
literal|1024
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
comment|// 2. disable the heartbeats
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
comment|// 3. mark a couple of blocks as corrupt
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
name|filePath
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
argument_list|,
literal|"STORAGE_ID"
argument_list|,
literal|"TEST"
argument_list|)
expr_stmt|;
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
literal|1
index|]
argument_list|,
literal|"STORAGE_ID"
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
name|BlockManagerTestUtil
operator|.
name|computeAllPendingWork
argument_list|(
name|bm
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
name|bm
operator|.
name|getPendingReplicationBlocksCount
argument_list|()
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|BlockInfo
name|storedBlock
init|=
name|bm
operator|.
name|getStoredBlock
argument_list|(
name|block
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bm
operator|.
name|pendingReplications
operator|.
name|getNumReplicas
argument_list|(
name|storedBlock
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// 4. delete the file
name|fs
operator|.
name|delete
argument_list|(
name|filePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// retry at most 10 times, each time sleep for 1s. Note that 10s is much
comment|// less than the default pending record timeout (5~10min)
name|int
name|retries
init|=
literal|10
decl_stmt|;
name|long
name|pendingNum
init|=
name|bm
operator|.
name|getPendingReplicationBlocksCount
argument_list|()
decl_stmt|;
while|while
condition|(
name|pendingNum
operator|!=
literal|0
operator|&&
name|retries
operator|--
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// let NN do the deletion
name|BlockManagerTestUtil
operator|.
name|updateState
argument_list|(
name|bm
argument_list|)
expr_stmt|;
name|pendingNum
operator|=
name|bm
operator|.
name|getPendingReplicationBlocksCount
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|pendingNum
argument_list|,
literal|0L
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

