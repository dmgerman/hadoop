begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
operator|.
name|ha
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|concurrent
operator|.
name|TimeUnit
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|CommonConfigurationKeys
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|HdfsConstants
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
name|qjournal
operator|.
name|MiniQJMHACluster
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
name|ipc
operator|.
name|RpcScheduler
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
name|ipc
operator|.
name|Schedulable
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
name|util
operator|.
name|Time
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
name|AfterClass
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

begin_comment
comment|/**  * Test consistency of reads while accessing an ObserverNode.  * The tests are based on traditional (non fast path) edits tailing.  */
end_comment

begin_class
DECL|class|TestConsistentReadsObserver
specifier|public
class|class
name|TestConsistentReadsObserver
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
name|TestConsistentReadsObserver
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|qjmhaCluster
specifier|private
specifier|static
name|MiniQJMHACluster
name|qjmhaCluster
decl_stmt|;
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|testPath
specifier|private
specifier|final
name|Path
name|testPath
init|=
operator|new
name|Path
argument_list|(
literal|"/TestConsistentReadsObserver"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startUpCluster ()
specifier|public
specifier|static
name|void
name|startUpCluster
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
comment|// disable fast tailing here because this test's assertions are based on the
comment|// timing of explicitly called rollEditLogAndTail. Although this means this
comment|// test takes some time to run
comment|// TODO: revisit if there is a better way.
name|qjmhaCluster
operator|=
name|HATestUtil
operator|.
name|setUpObserverCluster
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
name|qjmhaCluster
operator|.
name|getDfsCluster
argument_list|()
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
name|Exception
block|{
name|dfs
operator|=
name|setObserverRead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|delete
argument_list|(
name|testPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutDownCluster ()
specifier|public
specifier|static
name|void
name|shutDownCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|qjmhaCluster
operator|!=
literal|null
condition|)
block|{
name|qjmhaCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRequeueCall ()
specifier|public
name|void
name|testRequeueCall
parameter_list|()
throws|throws
name|Exception
block|{
name|setObserverRead
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Update the configuration just for the observer, by enabling
comment|// IPC backoff and using the test scheduler class, which starts to backoff
comment|// after certain number of calls.
specifier|final
name|int
name|observerIdx
init|=
literal|2
decl_stmt|;
name|NameNode
name|nn
init|=
name|dfsCluster
operator|.
name|getNameNode
argument_list|(
name|observerIdx
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|nn
operator|.
name|getNameNodeAddress
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|Configuration
name|configuration
init|=
name|dfsCluster
operator|.
name|getConfiguration
argument_list|(
name|observerIdx
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|CommonConfigurationKeys
operator|.
name|IPC_NAMESPACE
operator|+
literal|"."
operator|+
name|port
operator|+
literal|"."
decl_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|prefix
operator|+
name|CommonConfigurationKeys
operator|.
name|IPC_SCHEDULER_IMPL_KEY
argument_list|,
name|TestRpcScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|setBoolean
argument_list|(
name|prefix
operator|+
name|CommonConfigurationKeys
operator|.
name|IPC_BACKOFF_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NameNodeAdapter
operator|.
name|getRpcServer
argument_list|(
name|nn
argument_list|)
operator|.
name|refreshCallQueue
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|create
argument_list|(
name|testPath
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertSentTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Since we haven't tailed edit logs on the observer, it will fall behind
comment|// and keep re-queueing the incoming request. Eventually, RPC backoff will
comment|// be triggered and client should retry active NN.
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
name|assertSentTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMsyncSimple ()
specifier|public
name|void
name|testMsyncSimple
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 0 == not completed, 1 == succeeded, -1 == failed
name|AtomicInteger
name|readStatus
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Making an uncoordinated call, which initialize the proxy
comment|// to Observer node.
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|mkdir
argument_list|(
name|testPath
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|assertSentTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Thread
name|reader
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
comment|// this read will block until roll and tail edits happen.
name|dfs
operator|.
name|getFileStatus
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
name|readStatus
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|readStatus
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|reader
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// the reader is still blocking, not succeeded yet.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|readStatus
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|rollEditLogAndTail
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// wait a while for all the change to be done
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|readStatus
operator|.
name|get
argument_list|()
operator|!=
literal|0
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
comment|// the reader should have succeed.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|readStatus
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMsync (boolean autoMsync, long autoMsyncPeriodMs)
specifier|private
name|void
name|testMsync
parameter_list|(
name|boolean
name|autoMsync
parameter_list|,
name|long
name|autoMsyncPeriodMs
parameter_list|)
throws|throws
name|Exception
block|{
comment|// 0 == not completed, 1 == succeeded, -1 == failed
name|AtomicInteger
name|readStatus
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Configuration
name|conf2
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Disable FS cache so two different DFS clients will be used.
name|conf2
operator|.
name|setBoolean
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|autoMsync
condition|)
block|{
name|conf2
operator|.
name|setTimeDuration
argument_list|(
name|ObserverReadProxyProvider
operator|.
name|AUTO_MSYNC_PERIOD_KEY_PREFIX
operator|+
literal|"."
operator|+
name|dfs
operator|.
name|getUri
argument_list|()
operator|.
name|getHost
argument_list|()
argument_list|,
name|autoMsyncPeriodMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
name|DistributedFileSystem
name|dfs2
init|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|conf2
argument_list|)
decl_stmt|;
comment|// Initialize the proxies for Observer Node.
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
expr_stmt|;
comment|// This initialization will perform the msync-on-startup, so that another
comment|// form of msync is required later
name|dfs2
operator|.
name|getClient
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
expr_stmt|;
comment|// Advance Observer's state ID so it is ahead of client's.
name|dfs
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|rollEditLogAndTail
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|mkdir
argument_list|(
name|testPath
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|assertSentTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Thread
name|reader
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
comment|// After msync, client should have the latest state ID from active.
comment|// Therefore, the subsequent getFileStatus call should succeed.
if|if
condition|(
operator|!
name|autoMsync
condition|)
block|{
comment|// If not testing auto-msync, perform an explicit one here
name|dfs2
operator|.
name|getClient
argument_list|()
operator|.
name|msync
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|autoMsyncPeriodMs
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|autoMsyncPeriodMs
argument_list|)
expr_stmt|;
block|}
name|dfs2
operator|.
name|getFileStatus
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|HATestUtil
operator|.
name|isSentToAnyOfNameNodes
argument_list|(
name|dfs2
argument_list|,
name|dfsCluster
argument_list|,
literal|2
argument_list|)
condition|)
block|{
name|readStatus
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|readStatus
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|readStatus
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|reader
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|readStatus
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|rollEditLogAndTail
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|readStatus
operator|.
name|get
argument_list|()
operator|!=
literal|0
argument_list|,
literal|100
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|readStatus
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExplicitMsync ()
specifier|public
name|void
name|testExplicitMsync
parameter_list|()
throws|throws
name|Exception
block|{
name|testMsync
argument_list|(
literal|false
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAutoMsyncPeriod0 ()
specifier|public
name|void
name|testAutoMsyncPeriod0
parameter_list|()
throws|throws
name|Exception
block|{
name|testMsync
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAutoMsyncPeriod5 ()
specifier|public
name|void
name|testAutoMsyncPeriod5
parameter_list|()
throws|throws
name|Exception
block|{
name|testMsync
argument_list|(
literal|true
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|TimeoutException
operator|.
name|class
argument_list|)
DECL|method|testAutoMsyncLongPeriod ()
specifier|public
name|void
name|testAutoMsyncLongPeriod
parameter_list|()
throws|throws
name|Exception
block|{
comment|// This should fail since the auto-msync is never activated
name|testMsync
argument_list|(
literal|true
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|// A new client should first contact the active, before using an observer,
comment|// to ensure that it is up-to-date with the current state
annotation|@
name|Test
DECL|method|testCallFromNewClient ()
specifier|public
name|void
name|testCallFromNewClient
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set the order of nodes: Observer, Standby, Active
comment|// This is to ensure that test doesn't pass trivially because the active is
comment|// the first node contacted
name|dfsCluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|transitionToObserver
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|transitionToStandby
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|transitionToActive
argument_list|(
literal|2
argument_list|)
expr_stmt|;
try|try
block|{
comment|// 0 == not completed, 1 == succeeded, -1 == failed
name|AtomicInteger
name|readStatus
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Initialize the proxies for Observer Node.
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
expr_stmt|;
comment|// Advance Observer's state ID so it is ahead of client's.
name|dfs
operator|.
name|mkdir
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/test"
argument_list|)
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|getNameNode
argument_list|(
literal|2
argument_list|)
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|dfsCluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getEditLogTailer
argument_list|()
operator|.
name|doTailEdits
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|mkdir
argument_list|(
name|testPath
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|assertSentTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Configuration
name|conf2
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Disable FS cache so two different DFS clients will be used.
name|conf2
operator|.
name|setBoolean
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DistributedFileSystem
name|dfs2
init|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|conf2
argument_list|)
decl_stmt|;
name|dfs2
operator|.
name|getClient
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
expr_stmt|;
name|Thread
name|reader
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|dfs2
operator|.
name|getFileStatus
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
name|readStatus
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
name|readStatus
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|reader
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|readStatus
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|getNameNode
argument_list|(
literal|2
argument_list|)
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|dfsCluster
operator|.
name|getNameNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getEditLogTailer
argument_list|()
operator|.
name|doTailEdits
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|readStatus
operator|.
name|get
argument_list|()
operator|!=
literal|0
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|readStatus
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Put the cluster back the way it was when the test started
name|dfsCluster
operator|.
name|transitionToStandby
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|transitionToObserver
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|transitionToStandby
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUncoordinatedCall ()
specifier|public
name|void
name|testUncoordinatedCall
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make a write call so that client will be ahead of
comment|// observer for now.
name|dfs
operator|.
name|mkdir
argument_list|(
name|testPath
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
comment|// a status flag, initialized to 0, after reader finished, this will be
comment|// updated to 1, -1 on error
name|AtomicInteger
name|readStatus
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// create a separate thread to make a blocking read.
name|Thread
name|reader
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
comment|// this read call will block until server state catches up. But due to
comment|// configuration, this will take a very long time.
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getFileInfo
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|readStatus
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have been interrupted before getting here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|readStatus
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|reader
operator|.
name|start
argument_list|()
expr_stmt|;
name|long
name|before
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|datanodeReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|long
name|after
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
comment|// should succeed immediately, because datanodeReport is marked an
comment|// uncoordinated call, and will not be waiting for server to catch up.
name|assertTrue
argument_list|(
name|after
operator|-
name|before
operator|<
literal|200
argument_list|)
expr_stmt|;
comment|// by this time, reader thread should still be blocking, so the status not
comment|// updated
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|readStatus
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// reader thread status should still be unchanged after 5 sec...
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|readStatus
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// and the reader thread is not dead, so it must be still waiting
name|assertEquals
argument_list|(
name|Thread
operator|.
name|State
operator|.
name|WAITING
argument_list|,
name|reader
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSentTo (int nnIdx)
specifier|private
name|void
name|assertSentTo
parameter_list|(
name|int
name|nnIdx
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
literal|"Request was not sent to the expected namenode "
operator|+
name|nnIdx
argument_list|,
name|HATestUtil
operator|.
name|isSentToAnyOfNameNodes
argument_list|(
name|dfs
argument_list|,
name|dfsCluster
argument_list|,
name|nnIdx
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setObserverRead (boolean flag)
specifier|private
name|DistributedFileSystem
name|setObserverRead
parameter_list|(
name|boolean
name|flag
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|HATestUtil
operator|.
name|configureObserverReadFs
argument_list|(
name|dfsCluster
argument_list|,
name|conf
argument_list|,
name|ObserverReadProxyProvider
operator|.
name|class
argument_list|,
name|flag
argument_list|)
return|;
block|}
comment|/**    * A dummy test scheduler that starts backoff after a fixed number    * of requests.    */
DECL|class|TestRpcScheduler
specifier|public
specifier|static
class|class
name|TestRpcScheduler
implements|implements
name|RpcScheduler
block|{
comment|// Allow a number of RPCs to pass in order for the NN restart to succeed.
DECL|field|allowed
specifier|private
name|int
name|allowed
init|=
literal|10
decl_stmt|;
DECL|method|TestRpcScheduler ()
specifier|public
name|TestRpcScheduler
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getPriorityLevel (Schedulable obj)
specifier|public
name|int
name|getPriorityLevel
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|shouldBackOff (Schedulable obj)
specifier|public
name|boolean
name|shouldBackOff
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
block|{
return|return
operator|--
name|allowed
operator|<
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|addResponseTime (String name, int priorityLevel, int queueTime, int processingTime)
specifier|public
name|void
name|addResponseTime
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|priorityLevel
parameter_list|,
name|int
name|queueTime
parameter_list|,
name|int
name|processingTime
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

