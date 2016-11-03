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
name|getLongCounter
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
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|UUID
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
name|DFSClient
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
name|protocol
operator|.
name|*
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
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|NameNode
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
name|*
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
name|DatanodeStorage
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
comment|/**  * This test verifies that incremental block reports from a single DataNode are  * correctly handled by NN. Tests the following variations:  *  #1 - Incremental BRs from all storages combined in a single call.  *  #2 - Incremental BRs from separate storages sent in separate calls.  *  #3 - Incremental BR from an unknown storage should be rejected.  *  *  We also verify that the DataNode is not splitting the reports (it may do so  *  in the future).  */
end_comment

begin_class
DECL|class|TestIncrementalBrVariations
specifier|public
class|class
name|TestIncrementalBrVariations
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestIncrementalBrVariations
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_DATANODES
specifier|private
specifier|static
specifier|final
name|short
name|NUM_DATANODES
init|=
literal|1
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|NUM_BLOCKS
specifier|static
specifier|final
name|int
name|NUM_BLOCKS
init|=
literal|10
decl_stmt|;
DECL|field|seed
specifier|private
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xFACEFEEDL
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
DECL|field|client
specifier|private
name|DFSClient
name|client
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|poolId
specifier|private
name|String
name|poolId
decl_stmt|;
DECL|field|dn0
specifier|private
name|DataNode
name|dn0
decl_stmt|;
comment|// DataNode at index0 in the MiniDFSCluster
DECL|field|dn0Reg
specifier|private
name|DatanodeRegistration
name|dn0Reg
decl_stmt|;
comment|// DataNodeRegistration for dn0
static|static
block|{
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|NameNode
operator|.
name|stateChangeLog
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
name|NameNode
operator|.
name|blockStateChangeLog
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
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
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
name|DataNode
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
name|TestIncrementalBrVariations
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
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
name|NUM_DATANODES
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|client
operator|=
operator|new
name|DFSClient
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
literal|"localhost"
argument_list|,
name|cluster
operator|.
name|getNameNodePort
argument_list|()
argument_list|)
argument_list|,
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|dn0
operator|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|poolId
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
expr_stmt|;
name|dn0Reg
operator|=
name|dn0
operator|.
name|getDNRegistrationForBP
argument_list|(
name|poolId
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
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdownDataNodes
argument_list|()
expr_stmt|;
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
comment|/**    * Incremental BRs from all storages combined in a single message.    */
annotation|@
name|Test
DECL|method|testCombinedIncrementalBlockReport ()
specifier|public
name|void
name|testCombinedIncrementalBlockReport
parameter_list|()
throws|throws
name|IOException
block|{
name|verifyIncrementalBlockReports
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * One incremental BR per storage.    */
annotation|@
name|Test
DECL|method|testSplitIncrementalBlockReport ()
specifier|public
name|void
name|testSplitIncrementalBlockReport
parameter_list|()
throws|throws
name|IOException
block|{
name|verifyIncrementalBlockReports
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createFileGetBlocks (String filenamePrefix)
specifier|private
name|LocatedBlocks
name|createFileGetBlocks
parameter_list|(
name|String
name|filenamePrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|filenamePrefix
operator|+
literal|".dat"
argument_list|)
decl_stmt|;
comment|// Write out a file with a few blocks, get block locations.
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|filePath
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|BLOCK_SIZE
operator|*
name|NUM_BLOCKS
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|NUM_DATANODES
argument_list|,
name|seed
argument_list|)
expr_stmt|;
comment|// Get the block list for the file with the block locations.
name|LocatedBlocks
name|blocks
init|=
name|client
operator|.
name|getLocatedBlocks
argument_list|(
name|filePath
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|BLOCK_SIZE
operator|*
name|NUM_BLOCKS
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getUnderReplicatedBlocks
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|blocks
return|;
block|}
DECL|method|verifyIncrementalBlockReports (boolean splitReports)
specifier|public
name|void
name|verifyIncrementalBlockReports
parameter_list|(
name|boolean
name|splitReports
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get the block list for the file with the block locations.
name|LocatedBlocks
name|blocks
init|=
name|createFileGetBlocks
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|volumes
init|=
name|dn0
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
comment|// We will send 'fake' incremental block reports to the NN that look
comment|// like they originated from DN 0.
name|StorageReceivedDeletedBlocks
name|reports
index|[]
init|=
operator|new
name|StorageReceivedDeletedBlocks
index|[
name|volumes
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// Lie to the NN that one block on each storage has been deleted.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|reports
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|FsVolumeSpi
name|volume
init|=
name|volumes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|boolean
name|foundBlockOnStorage
init|=
literal|false
decl_stmt|;
name|ReceivedDeletedBlockInfo
name|rdbi
index|[]
init|=
operator|new
name|ReceivedDeletedBlockInfo
index|[
literal|1
index|]
decl_stmt|;
comment|// Find the first block on this storage and mark it as deleted for the
comment|// report.
for|for
control|(
name|LocatedBlock
name|block
range|:
name|blocks
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
if|if
condition|(
name|block
operator|.
name|getStorageIDs
argument_list|()
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
condition|)
block|{
name|rdbi
index|[
literal|0
index|]
operator|=
operator|new
name|ReceivedDeletedBlockInfo
argument_list|(
name|block
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|ReceivedDeletedBlockInfo
operator|.
name|BlockStatus
operator|.
name|DELETED_BLOCK
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|foundBlockOnStorage
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|foundBlockOnStorage
argument_list|)
expr_stmt|;
name|reports
index|[
name|i
index|]
operator|=
operator|new
name|StorageReceivedDeletedBlocks
argument_list|(
operator|new
name|DatanodeStorage
argument_list|(
name|volume
operator|.
name|getStorageID
argument_list|()
argument_list|)
argument_list|,
name|rdbi
argument_list|)
expr_stmt|;
if|if
condition|(
name|splitReports
condition|)
block|{
comment|// If we are splitting reports then send the report for this storage now.
name|StorageReceivedDeletedBlocks
name|singletonReport
index|[]
init|=
block|{
name|reports
index|[
name|i
index|]
block|}
decl_stmt|;
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|dn0Reg
argument_list|,
name|poolId
argument_list|,
name|singletonReport
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|splitReports
condition|)
block|{
comment|// Send a combined report.
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|dn0Reg
argument_list|,
name|poolId
argument_list|,
name|reports
argument_list|)
expr_stmt|;
block|}
comment|// Make sure that the deleted block from each storage was picked up
comment|// by the NameNode.  IBRs are async, make sure the NN processes
comment|// all of them.
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|flushBlockOps
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getMissingBlocksCount
argument_list|()
argument_list|,
name|is
argument_list|(
operator|(
name|long
operator|)
name|reports
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Verify that the DataNode sends a single incremental block report for all    * storages.    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testDataNodeDoesNotSplitReports ()
specifier|public
name|void
name|testDataNodeDoesNotSplitReports
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|LocatedBlocks
name|blocks
init|=
name|createFileGetBlocks
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove all blocks from the DataNode.
for|for
control|(
name|LocatedBlock
name|block
range|:
name|blocks
operator|.
name|getLocatedBlocks
argument_list|()
control|)
block|{
name|dn0
operator|.
name|notifyNamenodeDeletedBlock
argument_list|(
name|block
operator|.
name|getBlock
argument_list|()
argument_list|,
name|block
operator|.
name|getStorageIDs
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Triggering report after deleting blocks"
argument_list|)
expr_stmt|;
name|long
name|ops
init|=
name|getLongCounter
argument_list|(
literal|"BlockReceivedAndDeletedOps"
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
decl_stmt|;
comment|// Trigger a report to the NameNode and give it a few seconds.
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|dn0
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// Ensure that NameNodeRpcServer.blockReceivedAndDeletes is invoked
comment|// exactly once after we triggered the report.
name|assertCounter
argument_list|(
literal|"BlockReceivedAndDeletedOps"
argument_list|,
name|ops
operator|+
literal|1
argument_list|,
name|getMetrics
argument_list|(
name|NN_METRICS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getDummyBlock ()
specifier|private
specifier|static
name|Block
name|getDummyBlock
parameter_list|()
block|{
return|return
operator|new
name|Block
argument_list|(
literal|10000000L
argument_list|,
literal|100L
argument_list|,
literal|1048576L
argument_list|)
return|;
block|}
comment|/**    * Verify that the NameNode can learn about new storages from incremental    * block reports.    * This tests the fix for the error condition seen in HDFS-6904.    *    * @throws IOException    * @throws InterruptedException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNnLearnsNewStorages ()
specifier|public
name|void
name|testNnLearnsNewStorages
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Generate a report for a fake block on a fake storage.
specifier|final
name|String
name|newStorageUuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|DatanodeStorage
name|newStorage
init|=
operator|new
name|DatanodeStorage
argument_list|(
name|newStorageUuid
argument_list|)
decl_stmt|;
name|StorageReceivedDeletedBlocks
index|[]
name|reports
init|=
name|DFSTestUtil
operator|.
name|makeReportForReceivedBlock
argument_list|(
name|getDummyBlock
argument_list|()
argument_list|,
name|BlockStatus
operator|.
name|RECEIVED_BLOCK
argument_list|,
name|newStorage
argument_list|)
decl_stmt|;
comment|// Send the report to the NN.
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|dn0Reg
argument_list|,
name|poolId
argument_list|,
name|reports
argument_list|)
expr_stmt|;
comment|// IBRs are async, make sure the NN processes all of them.
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|flushBlockOps
argument_list|()
expr_stmt|;
comment|// Make sure that the NN has learned of the new storage.
name|DatanodeStorageInfo
name|storageInfo
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|dn0
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
operator|.
name|getStorageInfo
argument_list|(
name|newStorageUuid
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|storageInfo
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

