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
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|atLeastOnce
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
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
name|hdfs
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
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * Verify that incremental block reports are generated in response to  * block additions/deletions.  */
end_comment

begin_class
DECL|class|TestIncrementalBlockReports
specifier|public
class|class
name|TestIncrementalBlockReports
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
name|TestIncrementalBlockReports
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DN_COUNT
specifier|private
specifier|static
specifier|final
name|short
name|DN_COUNT
init|=
literal|1
decl_stmt|;
DECL|field|DUMMY_BLOCK_ID
specifier|private
specifier|static
specifier|final
name|long
name|DUMMY_BLOCK_ID
init|=
literal|5678
decl_stmt|;
DECL|field|DUMMY_BLOCK_LENGTH
specifier|private
specifier|static
specifier|final
name|long
name|DUMMY_BLOCK_LENGTH
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|DUMMY_BLOCK_GENSTAMP
specifier|private
specifier|static
specifier|final
name|long
name|DUMMY_BLOCK_GENSTAMP
init|=
literal|1000
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|fs
specifier|private
name|DistributedFileSystem
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|singletonNn
specifier|private
name|NameNode
name|singletonNn
decl_stmt|;
DECL|field|singletonDn
specifier|private
name|DataNode
name|singletonDn
decl_stmt|;
DECL|field|bpos
specifier|private
name|BPOfferService
name|bpos
decl_stmt|;
comment|// BPOS to use for block injection.
DECL|field|actor
specifier|private
name|BPServiceActor
name|actor
decl_stmt|;
comment|// BPSA to use for block injection.
DECL|field|storageUuid
specifier|private
name|String
name|storageUuid
decl_stmt|;
comment|// DatanodeStorage to use for block injection.
annotation|@
name|Before
DECL|method|startCluster ()
specifier|public
name|void
name|startCluster
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
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
name|DN_COUNT
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
name|singletonNn
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
expr_stmt|;
name|singletonDn
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
name|bpos
operator|=
name|singletonDn
operator|.
name|getAllBpOs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|actor
operator|=
name|bpos
operator|.
name|getBPServiceActors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
init|(
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|volumes
init|=
name|singletonDn
operator|.
name|getFSDataset
argument_list|()
operator|.
name|getFsVolumeReferences
argument_list|()
init|)
block|{
name|storageUuid
operator|=
name|volumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStorageID
argument_list|()
expr_stmt|;
block|}
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
name|DUMMY_BLOCK_ID
argument_list|,
name|DUMMY_BLOCK_LENGTH
argument_list|,
name|DUMMY_BLOCK_GENSTAMP
argument_list|)
return|;
block|}
comment|/**    * Inject a fake 'received' block into the BPServiceActor state.    */
DECL|method|injectBlockReceived ()
specifier|private
name|void
name|injectBlockReceived
parameter_list|()
block|{
name|ReceivedDeletedBlockInfo
name|rdbi
init|=
operator|new
name|ReceivedDeletedBlockInfo
argument_list|(
name|getDummyBlock
argument_list|()
argument_list|,
name|BlockStatus
operator|.
name|RECEIVED_BLOCK
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|actor
operator|.
name|notifyNamenodeBlock
argument_list|(
name|rdbi
argument_list|,
name|storageUuid
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Inject a fake 'deleted' block into the BPServiceActor state.    */
DECL|method|injectBlockDeleted ()
specifier|private
name|void
name|injectBlockDeleted
parameter_list|()
block|{
name|ReceivedDeletedBlockInfo
name|rdbi
init|=
operator|new
name|ReceivedDeletedBlockInfo
argument_list|(
name|getDummyBlock
argument_list|()
argument_list|,
name|BlockStatus
operator|.
name|DELETED_BLOCK
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|actor
operator|.
name|notifyNamenodeDeletedBlock
argument_list|(
name|rdbi
argument_list|,
name|storageUuid
argument_list|)
expr_stmt|;
block|}
comment|/**    * Spy on calls from the DN to the NN.    * @return spy object that can be used for Mockito verification.    */
DECL|method|spyOnDnCallsToNn ()
name|DatanodeProtocolClientSideTranslatorPB
name|spyOnDnCallsToNn
parameter_list|()
block|{
return|return
name|DataNodeTestUtils
operator|.
name|spyOnBposToNN
argument_list|(
name|singletonDn
argument_list|,
name|singletonNn
argument_list|)
return|;
block|}
comment|/**    * Ensure that an IBR is generated immediately for a block received by    * the DN.    *    * @throws InterruptedException    * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReportBlockReceived ()
specifier|public
name|void
name|testReportBlockReceived
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
try|try
block|{
name|DatanodeProtocolClientSideTranslatorPB
name|nnSpy
init|=
name|spyOnDnCallsToNn
argument_list|()
decl_stmt|;
name|injectBlockReceived
argument_list|()
expr_stmt|;
comment|// Sleep for a very short time, this is necessary since the IBR is
comment|// generated asynchronously.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// Ensure that the received block was reported immediately.
name|Mockito
operator|.
name|verify
argument_list|(
name|nnSpy
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Ensure that a delayed IBR is generated for a block deleted on the DN.    *    * @throws InterruptedException    * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReportBlockDeleted ()
specifier|public
name|void
name|testReportBlockDeleted
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
try|try
block|{
comment|// Trigger a block report to reset the IBR timer.
name|DataNodeTestUtils
operator|.
name|triggerBlockReport
argument_list|(
name|singletonDn
argument_list|)
expr_stmt|;
comment|// Spy on calls from the DN to the NN
name|DatanodeProtocolClientSideTranslatorPB
name|nnSpy
init|=
name|spyOnDnCallsToNn
argument_list|()
decl_stmt|;
name|injectBlockDeleted
argument_list|()
expr_stmt|;
comment|// Sleep for a very short time since IBR is generated
comment|// asynchronously.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// Ensure that no block report was generated immediately.
comment|// Deleted blocks are reported when the IBR timer elapses.
name|Mockito
operator|.
name|verify
argument_list|(
name|nnSpy
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Trigger a heartbeat, this also triggers an IBR.
name|DataNodeTestUtils
operator|.
name|triggerHeartbeat
argument_list|(
name|singletonDn
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// Ensure that the deleted block is reported.
name|Mockito
operator|.
name|verify
argument_list|(
name|nnSpy
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Add a received block entry and then replace it. Ensure that a single    * IBR is generated and that pending receive request state is cleared.    * This test case verifies the failure in HDFS-5922.    *    * @throws InterruptedException    * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testReplaceReceivedBlock ()
specifier|public
name|void
name|testReplaceReceivedBlock
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
try|try
block|{
comment|// Spy on calls from the DN to the NN
name|DatanodeProtocolClientSideTranslatorPB
name|nnSpy
init|=
name|spyOnDnCallsToNn
argument_list|()
decl_stmt|;
name|injectBlockReceived
argument_list|()
expr_stmt|;
name|injectBlockReceived
argument_list|()
expr_stmt|;
comment|// Overwrite the existing entry.
comment|// Sleep for a very short time since IBR is generated
comment|// asynchronously.
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// Ensure that the received block is reported.
name|Mockito
operator|.
name|verify
argument_list|(
name|nnSpy
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure that no more IBRs are pending.
name|assertFalse
argument_list|(
name|actor
operator|.
name|hasPendingIBR
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
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

