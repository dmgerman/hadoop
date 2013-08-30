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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
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
name|assertNull
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
name|assertSame
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Map
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
name|commons
operator|.
name|logging
operator|.
name|impl
operator|.
name|Log4JLogger
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|datanode
operator|.
name|metrics
operator|.
name|DataNodeMetrics
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
name|BlockCommand
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
name|CacheReport
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
name|DatanodeCommand
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
name|DatanodeProtocol
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
name|HeartbeatResponse
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
name|NNHAStatusHeartbeat
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
name|NamespaceInfo
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
name|StorageBlockReport
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
name|StorageReport
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
name|ArgumentCaptor
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_class
DECL|class|TestBPOfferService
specifier|public
class|class
name|TestBPOfferService
block|{
DECL|field|FAKE_BPID
specifier|private
specifier|static
specifier|final
name|String
name|FAKE_BPID
init|=
literal|"fake bpid"
decl_stmt|;
DECL|field|FAKE_CLUSTERID
specifier|private
specifier|static
specifier|final
name|String
name|FAKE_CLUSTERID
init|=
literal|"fake cluster"
decl_stmt|;
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestBPOfferService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FAKE_BLOCK
specifier|private
specifier|static
specifier|final
name|ExtendedBlock
name|FAKE_BLOCK
init|=
operator|new
name|ExtendedBlock
argument_list|(
name|FAKE_BPID
argument_list|,
literal|12345L
argument_list|)
decl_stmt|;
DECL|field|TEST_BUILD_DATA
specifier|private
specifier|static
specifier|final
name|String
name|TEST_BUILD_DATA
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
decl_stmt|;
static|static
block|{
operator|(
operator|(
name|Log4JLogger
operator|)
name|DataNode
operator|.
name|LOG
operator|)
operator|.
name|getLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
DECL|field|mockNN1
specifier|private
name|DatanodeProtocolClientSideTranslatorPB
name|mockNN1
decl_stmt|;
DECL|field|mockNN2
specifier|private
name|DatanodeProtocolClientSideTranslatorPB
name|mockNN2
decl_stmt|;
DECL|field|mockHaStatuses
specifier|private
name|NNHAStatusHeartbeat
index|[]
name|mockHaStatuses
init|=
operator|new
name|NNHAStatusHeartbeat
index|[
literal|2
index|]
decl_stmt|;
DECL|field|heartbeatCounts
specifier|private
name|int
name|heartbeatCounts
index|[]
init|=
operator|new
name|int
index|[
literal|2
index|]
decl_stmt|;
DECL|field|mockDn
specifier|private
name|DataNode
name|mockDn
decl_stmt|;
DECL|field|mockFSDataset
specifier|private
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|mockFSDataset
decl_stmt|;
annotation|@
name|Before
DECL|method|setupMocks ()
specifier|public
name|void
name|setupMocks
parameter_list|()
throws|throws
name|Exception
block|{
name|mockNN1
operator|=
name|setupNNMock
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|mockNN2
operator|=
name|setupNNMock
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Set up a mock DN with the bare-bones configuration
comment|// objects, etc.
name|mockDn
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DataNode
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
literal|true
argument_list|)
operator|.
name|when
argument_list|(
name|mockDn
argument_list|)
operator|.
name|shouldRun
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|File
name|dnDataDir
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_BUILD_DATA
argument_list|,
literal|"dfs"
argument_list|)
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dnDataDir
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|conf
argument_list|)
operator|.
name|when
argument_list|(
name|mockDn
argument_list|)
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
operator|new
name|DNConf
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockDn
argument_list|)
operator|.
name|getDnConf
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|DataNodeMetrics
operator|.
name|create
argument_list|(
name|conf
argument_list|,
literal|"fake dn"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockDn
argument_list|)
operator|.
name|getMetrics
argument_list|()
expr_stmt|;
comment|// Set up a simulated dataset with our fake BP
name|mockFSDataset
operator|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|SimulatedFSDataset
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|mockFSDataset
operator|.
name|addBlockPool
argument_list|(
name|FAKE_BPID
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Wire the dataset to the DN.
name|Mockito
operator|.
name|doReturn
argument_list|(
name|mockFSDataset
argument_list|)
operator|.
name|when
argument_list|(
name|mockDn
argument_list|)
operator|.
name|getFSDataset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set up a mock NN with the bare minimum for a DN to register to it.    */
DECL|method|setupNNMock (int nnIdx)
specifier|private
name|DatanodeProtocolClientSideTranslatorPB
name|setupNNMock
parameter_list|(
name|int
name|nnIdx
parameter_list|)
throws|throws
name|Exception
block|{
name|DatanodeProtocolClientSideTranslatorPB
name|mock
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeProtocolClientSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
operator|new
name|NamespaceInfo
argument_list|(
literal|1
argument_list|,
name|FAKE_CLUSTERID
argument_list|,
name|FAKE_BPID
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mock
argument_list|)
operator|.
name|versionRequest
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|DFSTestUtil
operator|.
name|getLocalDatanodeRegistration
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|mock
argument_list|)
operator|.
name|registerDatanode
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doAnswer
argument_list|(
operator|new
name|HeartbeatAnswer
argument_list|(
name|nnIdx
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mock
argument_list|)
operator|.
name|sendHeartbeat
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|StorageReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|any
argument_list|(
name|CacheReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
argument_list|)
expr_stmt|;
name|mockHaStatuses
index|[
name|nnIdx
index|]
operator|=
operator|new
name|NNHAStatusHeartbeat
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|mock
return|;
block|}
comment|/**    * Mock answer for heartbeats which returns an empty set of commands    * and the HA status for the chosen NN from the    * {@link TestBPOfferService#mockHaStatuses} array.    */
DECL|class|HeartbeatAnswer
specifier|private
class|class
name|HeartbeatAnswer
implements|implements
name|Answer
argument_list|<
name|HeartbeatResponse
argument_list|>
block|{
DECL|field|nnIdx
specifier|private
specifier|final
name|int
name|nnIdx
decl_stmt|;
DECL|method|HeartbeatAnswer (int nnIdx)
specifier|public
name|HeartbeatAnswer
parameter_list|(
name|int
name|nnIdx
parameter_list|)
block|{
name|this
operator|.
name|nnIdx
operator|=
name|nnIdx
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|HeartbeatResponse
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|heartbeatCounts
index|[
name|nnIdx
index|]
operator|++
expr_stmt|;
return|return
operator|new
name|HeartbeatResponse
argument_list|(
operator|new
name|DatanodeCommand
index|[
literal|0
index|]
argument_list|,
name|mockHaStatuses
index|[
name|nnIdx
index|]
argument_list|)
return|;
block|}
block|}
comment|/**    * Test that the BPOS can register to talk to two different NNs,    * sends block reports to both, etc.    */
annotation|@
name|Test
DECL|method|testBasicFunctionality ()
specifier|public
name|void
name|testBasicFunctionality
parameter_list|()
throws|throws
name|Exception
block|{
name|BPOfferService
name|bpos
init|=
name|setupBPOSForNNs
argument_list|(
name|mockNN1
argument_list|,
name|mockNN2
argument_list|)
decl_stmt|;
name|bpos
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|waitForInitialization
argument_list|(
name|bpos
argument_list|)
expr_stmt|;
comment|// The DN should have register to both NNs.
name|Mockito
operator|.
name|verify
argument_list|(
name|mockNN1
argument_list|)
operator|.
name|registerDatanode
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockNN2
argument_list|)
operator|.
name|registerDatanode
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Should get block reports from both NNs
name|waitForBlockReport
argument_list|(
name|mockNN1
argument_list|)
expr_stmt|;
name|waitForBlockReport
argument_list|(
name|mockNN2
argument_list|)
expr_stmt|;
comment|// When we receive a block, it should report it to both NNs
name|bpos
operator|.
name|notifyNamenodeReceivedBlock
argument_list|(
name|FAKE_BLOCK
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ReceivedDeletedBlockInfo
index|[]
name|ret
init|=
name|waitForBlockReceived
argument_list|(
name|FAKE_BLOCK
argument_list|,
name|mockNN1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ret
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FAKE_BLOCK
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|ret
index|[
literal|0
index|]
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|=
name|waitForBlockReceived
argument_list|(
name|FAKE_BLOCK
argument_list|,
name|mockNN2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ret
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FAKE_BLOCK
operator|.
name|getLocalBlock
argument_list|()
argument_list|,
name|ret
index|[
literal|0
index|]
operator|.
name|getBlock
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|bpos
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that DNA_INVALIDATE commands from the standby are ignored.    */
annotation|@
name|Test
DECL|method|testIgnoreDeletionsFromNonActive ()
specifier|public
name|void
name|testIgnoreDeletionsFromNonActive
parameter_list|()
throws|throws
name|Exception
block|{
name|BPOfferService
name|bpos
init|=
name|setupBPOSForNNs
argument_list|(
name|mockNN1
argument_list|,
name|mockNN2
argument_list|)
decl_stmt|;
comment|// Ask to invalidate FAKE_BLOCK when block report hits the
comment|// standby
name|Mockito
operator|.
name|doReturn
argument_list|(
operator|new
name|BlockCommand
argument_list|(
name|DatanodeProtocol
operator|.
name|DNA_INVALIDATE
argument_list|,
name|FAKE_BPID
argument_list|,
operator|new
name|Block
index|[]
block|{
name|FAKE_BLOCK
operator|.
name|getLocalBlock
argument_list|()
block|}
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockNN2
argument_list|)
operator|.
name|blockReport
argument_list|(
name|Mockito
operator|.
expr|<
name|DatanodeRegistration
operator|>
name|anyObject
argument_list|()
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
name|FAKE_BPID
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|StorageBlockReport
index|[]
operator|>
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
name|bpos
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|waitForInitialization
argument_list|(
name|bpos
argument_list|)
expr_stmt|;
comment|// Should get block reports from both NNs
name|waitForBlockReport
argument_list|(
name|mockNN1
argument_list|)
expr_stmt|;
name|waitForBlockReport
argument_list|(
name|mockNN2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|bpos
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// Should ignore the delete command from the standby
name|Mockito
operator|.
name|verify
argument_list|(
name|mockFSDataset
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|invalidate
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|FAKE_BPID
argument_list|)
argument_list|,
operator|(
name|Block
index|[]
operator|)
name|Mockito
operator|.
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure that, if the two NNs configured for a block pool    * have different block pool IDs, they will refuse to both    * register.    */
annotation|@
name|Test
DECL|method|testNNsFromDifferentClusters ()
specifier|public
name|void
name|testNNsFromDifferentClusters
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
operator|new
name|NamespaceInfo
argument_list|(
literal|1
argument_list|,
literal|"fake foreign cluster"
argument_list|,
name|FAKE_BPID
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockNN1
argument_list|)
operator|.
name|versionRequest
argument_list|()
expr_stmt|;
name|BPOfferService
name|bpos
init|=
name|setupBPOSForNNs
argument_list|(
name|mockNN1
argument_list|,
name|mockNN2
argument_list|)
decl_stmt|;
name|bpos
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|waitForOneToFail
argument_list|(
name|bpos
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|bpos
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test that the DataNode determines the active NameNode correctly    * based on the HA-related information in heartbeat responses.    * See HDFS-2627.    */
annotation|@
name|Test
DECL|method|testPickActiveNameNode ()
specifier|public
name|void
name|testPickActiveNameNode
parameter_list|()
throws|throws
name|Exception
block|{
name|BPOfferService
name|bpos
init|=
name|setupBPOSForNNs
argument_list|(
name|mockNN1
argument_list|,
name|mockNN2
argument_list|)
decl_stmt|;
name|bpos
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|waitForInitialization
argument_list|(
name|bpos
argument_list|)
expr_stmt|;
comment|// Should start with neither NN as active.
name|assertNull
argument_list|(
name|bpos
operator|.
name|getActiveNN
argument_list|()
argument_list|)
expr_stmt|;
comment|// Have NN1 claim active at txid 1
name|mockHaStatuses
index|[
literal|0
index|]
operator|=
operator|new
name|NNHAStatusHeartbeat
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|bpos
operator|.
name|triggerHeartbeatForTests
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|mockNN1
argument_list|,
name|bpos
operator|.
name|getActiveNN
argument_list|()
argument_list|)
expr_stmt|;
comment|// NN2 claims active at a higher txid
name|mockHaStatuses
index|[
literal|1
index|]
operator|=
operator|new
name|NNHAStatusHeartbeat
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|bpos
operator|.
name|triggerHeartbeatForTests
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|mockNN2
argument_list|,
name|bpos
operator|.
name|getActiveNN
argument_list|()
argument_list|)
expr_stmt|;
comment|// Even after another heartbeat from the first NN, it should
comment|// think NN2 is active, since it claimed a higher txid
name|bpos
operator|.
name|triggerHeartbeatForTests
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|mockNN2
argument_list|,
name|bpos
operator|.
name|getActiveNN
argument_list|()
argument_list|)
expr_stmt|;
comment|// Even if NN2 goes to standby, DN shouldn't reset to talking to NN1,
comment|// because NN1's txid is lower than the last active txid. Instead,
comment|// it should consider neither active.
name|mockHaStatuses
index|[
literal|1
index|]
operator|=
operator|new
name|NNHAStatusHeartbeat
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|bpos
operator|.
name|triggerHeartbeatForTests
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|bpos
operator|.
name|getActiveNN
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now if NN1 goes back to a higher txid, it should be considered active
name|mockHaStatuses
index|[
literal|0
index|]
operator|=
operator|new
name|NNHAStatusHeartbeat
argument_list|(
name|HAServiceState
operator|.
name|ACTIVE
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|bpos
operator|.
name|triggerHeartbeatForTests
argument_list|()
expr_stmt|;
name|assertSame
argument_list|(
name|mockNN1
argument_list|,
name|bpos
operator|.
name|getActiveNN
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|bpos
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|waitForOneToFail (final BPOfferService bpos)
specifier|private
name|void
name|waitForOneToFail
parameter_list|(
specifier|final
name|BPOfferService
name|bpos
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
return|return
name|bpos
operator|.
name|countNameNodes
argument_list|()
operator|==
literal|1
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a BPOfferService which registers with and heartbeats with the    * specified namenode proxy objects.    * @throws IOException     */
DECL|method|setupBPOSForNNs ( DatanodeProtocolClientSideTranslatorPB .... nns)
specifier|private
name|BPOfferService
name|setupBPOSForNNs
parameter_list|(
name|DatanodeProtocolClientSideTranslatorPB
modifier|...
name|nns
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Set up some fake InetAddresses, then override the connectToNN
comment|// function to return the corresponding proxies.
specifier|final
name|Map
argument_list|<
name|InetSocketAddress
argument_list|,
name|DatanodeProtocolClientSideTranslatorPB
argument_list|>
name|nnMap
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|port
init|=
literal|0
init|;
name|port
operator|<
name|nns
operator|.
name|length
condition|;
name|port
operator|++
control|)
block|{
name|nnMap
operator|.
name|put
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|port
argument_list|)
argument_list|,
name|nns
index|[
name|port
index|]
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|nns
index|[
name|port
index|]
argument_list|)
operator|.
name|when
argument_list|(
name|mockDn
argument_list|)
operator|.
name|connectToNN
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|port
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BPOfferService
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|nnMap
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|,
name|mockDn
argument_list|)
return|;
block|}
DECL|method|waitForInitialization (final BPOfferService bpos)
specifier|private
name|void
name|waitForInitialization
parameter_list|(
specifier|final
name|BPOfferService
name|bpos
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
return|return
name|bpos
operator|.
name|isAlive
argument_list|()
operator|&&
name|bpos
operator|.
name|isInitialized
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForBlockReport (final DatanodeProtocolClientSideTranslatorPB mockNN)
specifier|private
name|void
name|waitForBlockReport
parameter_list|(
specifier|final
name|DatanodeProtocolClientSideTranslatorPB
name|mockNN
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
try|try
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|mockNN
argument_list|)
operator|.
name|blockReport
argument_list|(
name|Mockito
operator|.
expr|<
name|DatanodeRegistration
operator|>
name|anyObject
argument_list|()
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
name|FAKE_BPID
argument_list|)
argument_list|,
name|Mockito
operator|.
expr|<
name|StorageBlockReport
index|[]
operator|>
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting on block report: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|500
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForBlockReceived ( ExtendedBlock fakeBlock, DatanodeProtocolClientSideTranslatorPB mockNN)
specifier|private
name|ReceivedDeletedBlockInfo
index|[]
name|waitForBlockReceived
parameter_list|(
name|ExtendedBlock
name|fakeBlock
parameter_list|,
name|DatanodeProtocolClientSideTranslatorPB
name|mockNN
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ArgumentCaptor
argument_list|<
name|StorageReceivedDeletedBlocks
index|[]
argument_list|>
name|captor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|StorageReceivedDeletedBlocks
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
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
try|try
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|mockNN1
argument_list|)
operator|.
name|blockReceivedAndDeleted
argument_list|(
name|Mockito
operator|.
expr|<
name|DatanodeRegistration
operator|>
name|anyObject
argument_list|()
argument_list|,
name|Mockito
operator|.
name|eq
argument_list|(
name|FAKE_BPID
argument_list|)
argument_list|,
name|captor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
return|return
name|captor
operator|.
name|getValue
argument_list|()
index|[
literal|0
index|]
operator|.
name|getBlocks
argument_list|()
return|;
block|}
block|}
end_class

end_unit

