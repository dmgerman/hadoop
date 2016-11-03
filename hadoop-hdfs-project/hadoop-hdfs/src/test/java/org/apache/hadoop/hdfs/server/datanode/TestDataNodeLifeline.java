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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_LIFELINE_INTERVAL_SECONDS_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_HEARTBEAT_INTERVAL_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LIFELINE_RPC_ADDRESS_KEY
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY
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
name|SlowDiskReports
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
name|assertNotNull
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|Mockito
operator|.
name|anyBoolean
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
name|anyInt
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
name|anyLong
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
name|doAnswer
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
name|never
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
name|spy
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
name|verify
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
name|concurrent
operator|.
name|CountDownLatch
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
name|protocolPB
operator|.
name|DatanodeLifelineProtocolClientSideTranslatorPB
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
name|protocol
operator|.
name|BlocksStorageMovementResult
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
name|SlowPeerReports
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|VolumeFailureSummary
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
comment|/**  * Test suite covering lifeline protocol handling in the DataNode.  */
end_comment

begin_class
DECL|class|TestDataNodeLifeline
specifier|public
class|class
name|TestDataNodeLifeline
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
name|TestDataNodeLifeline
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
name|DataNode
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
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|60000
argument_list|)
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|HdfsConfiguration
name|conf
decl_stmt|;
DECL|field|lifelineNamenode
specifier|private
name|DatanodeLifelineProtocolClientSideTranslatorPB
name|lifelineNamenode
decl_stmt|;
DECL|field|metrics
specifier|private
name|DataNodeMetrics
name|metrics
decl_stmt|;
DECL|field|namenode
specifier|private
name|DatanodeProtocolClientSideTranslatorPB
name|namenode
decl_stmt|;
DECL|field|namesystem
specifier|private
name|FSNamesystem
name|namesystem
decl_stmt|;
DECL|field|dn
specifier|private
name|DataNode
name|dn
decl_stmt|;
DECL|field|bpsa
specifier|private
name|BPServiceActor
name|bpsa
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
comment|// Configure cluster with lifeline RPC server enabled, and down-tune
comment|// heartbeat timings to try to force quick dead/stale DataNodes.
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_DATANODE_LIFELINE_INTERVAL_SECONDS_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_HEARTBEAT_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_LIFELINE_RPC_ADDRESS_KEY
argument_list|,
literal|"0.0.0.0:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY
argument_list|,
literal|6
operator|*
literal|1000
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
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|namesystem
operator|=
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
comment|// Set up spies on RPC proxies so that we can inject failures.
name|dn
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
name|metrics
operator|=
name|dn
operator|.
name|getMetrics
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|BPOfferService
argument_list|>
name|allBpos
init|=
name|dn
operator|.
name|getAllBpOs
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|allBpos
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allBpos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|BPOfferService
name|bpos
init|=
name|allBpos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BPServiceActor
argument_list|>
name|allBpsa
init|=
name|bpos
operator|.
name|getBPServiceActors
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|allBpsa
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allBpsa
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|bpsa
operator|=
name|allBpsa
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|bpsa
argument_list|)
expr_stmt|;
comment|// Lifeline RPC proxy gets created on separate thread, so poll until found.
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
if|if
condition|(
name|bpsa
operator|.
name|getLifelineNameNodeProxy
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|lifelineNamenode
operator|=
name|spy
argument_list|(
name|bpsa
operator|.
name|getLifelineNameNodeProxy
argument_list|()
argument_list|)
expr_stmt|;
name|bpsa
operator|.
name|setLifelineNameNode
argument_list|(
name|lifelineNamenode
argument_list|)
expr_stmt|;
block|}
return|return
name|lifelineNamenode
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|bpsa
operator|.
name|getNameNodeProxy
argument_list|()
argument_list|)
expr_stmt|;
name|namenode
operator|=
name|spy
argument_list|(
name|bpsa
operator|.
name|getNameNodeProxy
argument_list|()
argument_list|)
expr_stmt|;
name|bpsa
operator|.
name|setNameNode
argument_list|(
name|namenode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
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
name|GenericTestUtils
operator|.
name|assertNoThreadsMatching
argument_list|(
literal|".*lifeline.*"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSendLifelineIfHeartbeatBlocked ()
specifier|public
name|void
name|testSendLifelineIfHeartbeatBlocked
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Run the test for the duration of sending 10 lifeline RPC messages.
name|int
name|numLifelines
init|=
literal|10
decl_stmt|;
name|CountDownLatch
name|lifelinesSent
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numLifelines
argument_list|)
decl_stmt|;
comment|// Intercept heartbeat to inject an artificial delay, until all expected
comment|// lifeline RPC messages have been sent.
name|doAnswer
argument_list|(
operator|new
name|LatchAwaitingAnswer
argument_list|<
name|HeartbeatResponse
argument_list|>
argument_list|(
name|lifelinesSent
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|namenode
argument_list|)
operator|.
name|sendHeartbeat
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|StorageReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|any
argument_list|(
name|VolumeFailureSummary
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|,
name|any
argument_list|(
name|SlowPeerReports
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|SlowDiskReports
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|BlocksStorageMovementResult
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Intercept lifeline to trigger latch count-down on each call.
name|doAnswer
argument_list|(
operator|new
name|LatchCountingAnswer
argument_list|<
name|Void
argument_list|>
argument_list|(
name|lifelinesSent
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|lifelineNamenode
argument_list|)
operator|.
name|sendLifeline
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|StorageReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|any
argument_list|(
name|VolumeFailureSummary
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// While waiting on the latch for the expected number of lifeline messages,
comment|// poll DataNode tracking information.  Thanks to the lifeline, we expect
comment|// that the DataNode always stays alive, and never goes stale or dead.
while|while
condition|(
operator|!
name|lifelinesSent
operator|.
name|await
argument_list|(
literal|1
argument_list|,
name|SECONDS
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Expect DataNode to be kept alive by lifeline."
argument_list|,
literal|1
argument_list|,
name|namesystem
operator|.
name|getNumLiveDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expect DataNode not marked dead due to lifeline."
argument_list|,
literal|0
argument_list|,
name|namesystem
operator|.
name|getNumDeadDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expect DataNode not marked stale due to lifeline."
argument_list|,
literal|0
argument_list|,
name|namesystem
operator|.
name|getNumStaleDataNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Verify that we did in fact call the lifeline RPC.
name|verify
argument_list|(
name|lifelineNamenode
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|sendLifeline
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|StorageReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|any
argument_list|(
name|VolumeFailureSummary
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Also verify lifeline call through metrics.  We expect at least
comment|// numLifelines, guaranteed by waiting on the latch.  There is a small
comment|// possibility of extra lifeline calls depending on timing, so we allow
comment|// slack in the assertion.
name|assertTrue
argument_list|(
literal|"Expect metrics to count at least "
operator|+
name|numLifelines
operator|+
literal|" calls."
argument_list|,
name|getLongCounter
argument_list|(
literal|"LifelinesNumOps"
argument_list|,
name|getMetrics
argument_list|(
name|metrics
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
operator|>=
name|numLifelines
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoLifelineSentIfHeartbeatsOnTime ()
specifier|public
name|void
name|testNoLifelineSentIfHeartbeatsOnTime
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Run the test for the duration of sending 10 heartbeat RPC messages.
name|int
name|numHeartbeats
init|=
literal|10
decl_stmt|;
name|CountDownLatch
name|heartbeatsSent
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numHeartbeats
argument_list|)
decl_stmt|;
comment|// Intercept heartbeat to trigger latch count-down on each call.
name|doAnswer
argument_list|(
operator|new
name|LatchCountingAnswer
argument_list|<
name|HeartbeatResponse
argument_list|>
argument_list|(
name|heartbeatsSent
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|namenode
argument_list|)
operator|.
name|sendHeartbeat
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|StorageReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|any
argument_list|(
name|VolumeFailureSummary
operator|.
name|class
argument_list|)
argument_list|,
name|anyBoolean
argument_list|()
argument_list|,
name|any
argument_list|(
name|SlowPeerReports
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|SlowDiskReports
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|BlocksStorageMovementResult
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// While waiting on the latch for the expected number of heartbeat messages,
comment|// poll DataNode tracking information.  We expect that the DataNode always
comment|// stays alive, and never goes stale or dead.
while|while
condition|(
operator|!
name|heartbeatsSent
operator|.
name|await
argument_list|(
literal|1
argument_list|,
name|SECONDS
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Expect DataNode to be kept alive by lifeline."
argument_list|,
literal|1
argument_list|,
name|namesystem
operator|.
name|getNumLiveDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expect DataNode not marked dead due to lifeline."
argument_list|,
literal|0
argument_list|,
name|namesystem
operator|.
name|getNumDeadDataNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expect DataNode not marked stale due to lifeline."
argument_list|,
literal|0
argument_list|,
name|namesystem
operator|.
name|getNumStaleDataNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Verify that we did not call the lifeline RPC.
name|verify
argument_list|(
name|lifelineNamenode
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|sendLifeline
argument_list|(
name|any
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|StorageReport
index|[]
operator|.
expr|class
argument_list|)
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyLong
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|any
argument_list|(
name|VolumeFailureSummary
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Also verify no lifeline calls through metrics.
name|assertEquals
argument_list|(
literal|"Expect metrics to count no lifeline calls."
argument_list|,
literal|0
argument_list|,
name|getLongCounter
argument_list|(
literal|"LifelinesNumOps"
argument_list|,
name|getMetrics
argument_list|(
name|metrics
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLifelineForDeadNode ()
specifier|public
name|void
name|testLifelineForDeadNode
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|initialCapacity
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getCapacityTotal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|initialCapacity
operator|>
literal|0
argument_list|)
expr_stmt|;
name|dn
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setDataNodesDead
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Capacity should be 0 after all DNs dead"
argument_list|,
literal|0
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getCapacityTotal
argument_list|()
argument_list|)
expr_stmt|;
name|bpsa
operator|.
name|sendLifelineForTests
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Lifeline should be ignored for dead node"
argument_list|,
literal|0
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getCapacityTotal
argument_list|()
argument_list|)
expr_stmt|;
comment|// Wait for re-registration and heartbeat
name|dn
operator|.
name|setHeartbeatsDisabledForTests
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|DatanodeDescriptor
name|dnDesc
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanodes
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
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
return|return
name|dnDesc
operator|.
name|isAlive
argument_list|()
operator|&&
name|dnDesc
operator|.
name|isHeartbeatedSinceRegistration
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Capacity should include only live capacity"
argument_list|,
name|initialCapacity
argument_list|,
name|cluster
operator|.
name|getNamesystem
argument_list|(
literal|0
argument_list|)
operator|.
name|getCapacityTotal
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Waits on a {@link CountDownLatch} before calling through to the method.    */
DECL|class|LatchAwaitingAnswer
specifier|private
specifier|final
class|class
name|LatchAwaitingAnswer
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Answer
argument_list|<
name|T
argument_list|>
block|{
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|method|LatchAwaitingAnswer (CountDownLatch latch)
specifier|public
name|LatchAwaitingAnswer
parameter_list|(
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|T
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Awaiting, remaining latch count is {}."
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|invocation
operator|.
name|callRealMethod
argument_list|()
return|;
block|}
block|}
comment|/**    * Counts on a {@link CountDownLatch} after each call through to the method.    */
DECL|class|LatchCountingAnswer
specifier|private
specifier|final
class|class
name|LatchCountingAnswer
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Answer
argument_list|<
name|T
argument_list|>
block|{
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|method|LatchCountingAnswer (CountDownLatch latch)
specifier|public
name|LatchCountingAnswer
parameter_list|(
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|T
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|T
name|result
init|=
operator|(
name|T
operator|)
name|invocation
operator|.
name|callRealMethod
argument_list|()
decl_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Countdown, remaining latch count is {}."
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

