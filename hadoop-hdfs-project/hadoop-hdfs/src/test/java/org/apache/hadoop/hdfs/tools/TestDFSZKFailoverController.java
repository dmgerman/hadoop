begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
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
name|concurrent
operator|.
name|TimeoutException
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
name|CommonConfigurationKeysPublic
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
name|ha
operator|.
name|ClientBaseWithFixes
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
name|ha
operator|.
name|HealthMonitor
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
name|TestNodeFencer
operator|.
name|AlwaysSucceedFencer
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
name|ZKFCTestUtil
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
name|ZKFailoverController
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
name|MiniDFSNNTopology
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
name|EditLogFileOutputStream
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
name|MockNameNodeResourceChecker
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
name|namenode
operator|.
name|ha
operator|.
name|HATestUtil
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
name|ServerSocketUtil
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
name|hadoop
operator|.
name|test
operator|.
name|MultithreadedTestUtil
operator|.
name|TestContext
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
name|MultithreadedTestUtil
operator|.
name|TestingThread
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

begin_class
DECL|class|TestDFSZKFailoverController
specifier|public
class|class
name|TestDFSZKFailoverController
extends|extends
name|ClientBaseWithFixes
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|ctx
specifier|private
name|TestContext
name|ctx
decl_stmt|;
DECL|field|thr1
DECL|field|thr2
specifier|private
name|ZKFCThread
name|thr1
decl_stmt|,
name|thr2
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
static|static
block|{
comment|// Make tests run faster by avoiding fsync()
name|EditLogFileOutputStream
operator|.
name|setShouldSkipFsyncForTesting
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|// Specify the quorum per-nameservice, to ensure that these configs
comment|// can be nameservice-scoped.
name|conf
operator|.
name|set
argument_list|(
name|ZKFailoverController
operator|.
name|ZK_QUORUM_KEY
operator|+
literal|".ns1"
argument_list|,
name|hostPort
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
name|AlwaysSucceedFencer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_AUTO_FAILOVER_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Turn off IPC client caching, so that the suite can handle
comment|// the restart of the daemons between test cases.
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Get random port numbers in advance. Because ZKFCs and DFSHAAdmin
comment|// needs rpc port numbers of all ZKFCs, Setting 0 does not work here.
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_ZKFC_PORT_KEY
operator|+
literal|".ns1.nn1"
argument_list|,
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|10023
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_ZKFC_PORT_KEY
operator|+
literal|".ns1.nn2"
argument_list|,
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|10024
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// prefer non-ephemeral port to avoid port collision on restartNameNode
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns1"
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn1"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|10021
argument_list|,
literal|100
argument_list|)
argument_list|)
operator|.
name|setServicePort
argument_list|(
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|10025
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn2"
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|10022
argument_list|,
literal|100
argument_list|)
argument_list|)
operator|.
name|setServicePort
argument_list|(
name|ServerSocketUtil
operator|.
name|getPort
argument_list|(
literal|10026
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
argument_list|)
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
name|nnTopology
argument_list|(
name|topology
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
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
name|ctx
operator|=
operator|new
name|TestContext
argument_list|()
expr_stmt|;
name|ctx
operator|.
name|addThread
argument_list|(
name|thr1
operator|=
operator|new
name|ZKFCThread
argument_list|(
name|ctx
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|thr1
operator|.
name|zkfc
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-formatZK"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|thr1
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|addThread
argument_list|(
name|thr2
operator|=
operator|new
name|ZKFCThread
argument_list|(
name|ctx
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|thr2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Wait for the ZKFCs to fully start up
name|ZKFCTestUtil
operator|.
name|waitForHealthState
argument_list|(
name|thr1
operator|.
name|zkfc
argument_list|,
name|HealthMonitor
operator|.
name|State
operator|.
name|SERVICE_HEALTHY
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|ZKFCTestUtil
operator|.
name|waitForHealthState
argument_list|(
name|thr2
operator|.
name|zkfc
argument_list|,
name|HealthMonitor
operator|.
name|State
operator|.
name|SERVICE_HEALTHY
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|fs
operator|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
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
throws|throws
name|Exception
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
if|if
condition|(
name|thr1
operator|!=
literal|null
condition|)
block|{
name|thr1
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|thr1
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|thr2
operator|!=
literal|null
condition|)
block|{
name|thr2
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|thr2
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|ctx
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ctx
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Test that thread dump is captured after NN state changes.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testThreadDumpCaptureAfterNNStateChange ()
specifier|public
name|void
name|testThreadDumpCaptureAfterNNStateChange
parameter_list|()
throws|throws
name|Exception
block|{
name|MockNameNodeResourceChecker
name|mockResourceChecker
init|=
operator|new
name|MockNameNodeResourceChecker
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|mockResourceChecker
operator|.
name|setResourcesAvailable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNamesystem
argument_list|()
operator|.
name|setNNResourceChecker
argument_list|(
name|mockResourceChecker
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|thr1
operator|.
name|zkfc
operator|.
name|isThreadDumpCaptured
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that automatic failover is triggered by shutting the    * active NN down.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFailoverAndBackOnNNShutdown ()
specifier|public
name|void
name|testFailoverAndBackOnNNShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|p1
init|=
operator|new
name|Path
argument_list|(
literal|"/dir1"
argument_list|)
decl_stmt|;
name|Path
name|p2
init|=
operator|new
name|Path
argument_list|(
literal|"/dir2"
argument_list|)
decl_stmt|;
comment|// Write some data on the first NN
name|fs
operator|.
name|mkdirs
argument_list|(
name|p1
argument_list|)
expr_stmt|;
comment|// Shut it down, causing automatic failover
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Data should still exist. Write some on the new NN
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|getLastFencedService
argument_list|()
operator|.
name|getAddress
argument_list|()
argument_list|,
name|thr1
operator|.
name|zkfc
operator|.
name|getLocalTarget
argument_list|()
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// Start the first node back up
name|cluster
operator|.
name|restartNameNode
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// This should have no effect -- the new node should be STANDBY.
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Shut down the second node, which should failback to the first
name|cluster
operator|.
name|shutdownNameNode
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
comment|// First node should see what was written on the second node while it was down.
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AlwaysSucceedFencer
operator|.
name|getLastFencedService
argument_list|()
operator|.
name|getAddress
argument_list|()
argument_list|,
name|thr2
operator|.
name|zkfc
operator|.
name|getLocalTarget
argument_list|()
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testManualFailover ()
specifier|public
name|void
name|testManualFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|thr2
operator|.
name|zkfc
operator|.
name|getLocalTarget
argument_list|()
operator|.
name|getZKFCProxy
argument_list|(
name|conf
argument_list|,
literal|15000
argument_list|)
operator|.
name|gracefulFailover
argument_list|()
expr_stmt|;
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|thr1
operator|.
name|zkfc
operator|.
name|getLocalTarget
argument_list|()
operator|.
name|getZKFCProxy
argument_list|(
name|conf
argument_list|,
literal|15000
argument_list|)
operator|.
name|gracefulFailover
argument_list|()
expr_stmt|;
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testManualFailoverWithDFSHAAdmin ()
specifier|public
name|void
name|testManualFailoverWithDFSHAAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSHAAdmin
name|tool
init|=
operator|new
name|DFSHAAdmin
argument_list|()
decl_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tool
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-failover"
block|,
literal|"nn1"
block|,
literal|"nn2"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tool
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-failover"
block|,
literal|"nn2"
block|,
literal|"nn1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|0
argument_list|,
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|waitForHAState
argument_list|(
literal|1
argument_list|,
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForHAState (int nnidx, final HAServiceState state)
specifier|private
name|void
name|waitForHAState
parameter_list|(
name|int
name|nnidx
parameter_list|,
specifier|final
name|HAServiceState
name|state
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
specifier|final
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
name|nnidx
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
return|return
name|nn
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getServiceStatus
argument_list|()
operator|.
name|getState
argument_list|()
operator|==
name|state
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
argument_list|,
literal|50
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test-thread which runs a ZK Failover Controller corresponding    * to a given NameNode in the minicluster.    */
DECL|class|ZKFCThread
specifier|private
class|class
name|ZKFCThread
extends|extends
name|TestingThread
block|{
DECL|field|zkfc
specifier|private
specifier|final
name|DFSZKFailoverController
name|zkfc
decl_stmt|;
DECL|method|ZKFCThread (TestContext ctx, int idx)
specifier|public
name|ZKFCThread
parameter_list|(
name|TestContext
name|ctx
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkfc
operator|=
name|DFSZKFailoverController
operator|.
name|create
argument_list|(
name|cluster
operator|.
name|getConfiguration
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWork ()
specifier|public
name|void
name|doWork
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|zkfc
operator|.
name|run
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// Interrupted by main thread, that's OK.
block|}
block|}
block|}
block|}
end_class

end_unit

