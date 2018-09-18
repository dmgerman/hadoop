begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|assertThat
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
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
name|ClientProtocol
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
name|HAProxyFactory
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|ha
operator|.
name|ObserverReadProxyProvider
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|Collection
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
name|Callable
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
name|ExecutorService
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
name|Executors
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
name|Future
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

begin_comment
comment|/**  * Class is used to test server sending state alignment information to clients  * via RPC and likewise clients receiving and updating their last known  * state alignment info.  * These tests check that after a single RPC call a client will have caught up  * to the most recent alignment state of the server.  */
end_comment

begin_class
DECL|class|TestStateAlignmentContextWithHA
specifier|public
class|class
name|TestStateAlignmentContextWithHA
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
name|TestStateAlignmentContextWithHA
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|NUMDATANODES
specifier|private
specifier|static
specifier|final
name|int
name|NUMDATANODES
init|=
literal|1
decl_stmt|;
DECL|field|NUMCLIENTS
specifier|private
specifier|static
specifier|final
name|int
name|NUMCLIENTS
init|=
literal|10
decl_stmt|;
DECL|field|NUMFILES
specifier|private
specifier|static
specifier|final
name|int
name|NUMFILES
init|=
literal|120
decl_stmt|;
DECL|field|CONF
specifier|private
specifier|static
specifier|final
name|Configuration
name|CONF
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
DECL|field|AC_LIST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|ClientGSIContext
argument_list|>
name|AC_LIST
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|clients
specifier|private
specifier|static
name|List
argument_list|<
name|Worker
argument_list|>
name|clients
decl_stmt|;
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|active
specifier|private
name|int
name|active
init|=
literal|0
decl_stmt|;
DECL|field|standby
specifier|private
name|int
name|standby
init|=
literal|1
decl_stmt|;
DECL|class|ORPPwithAlignmentContexts
specifier|static
class|class
name|ORPPwithAlignmentContexts
parameter_list|<
name|T
extends|extends
name|ClientProtocol
parameter_list|>
extends|extends
name|ObserverReadProxyProvider
argument_list|<
name|T
argument_list|>
block|{
DECL|method|ORPPwithAlignmentContexts ( Configuration conf, URI uri, Class<T> xface, HAProxyFactory<T> factory)
specifier|public
name|ORPPwithAlignmentContexts
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|xface
parameter_list|,
name|HAProxyFactory
argument_list|<
name|T
argument_list|>
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|uri
argument_list|,
name|xface
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|AC_LIST
operator|.
name|add
argument_list|(
operator|(
name|ClientGSIContext
operator|)
name|getAlignmentContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|BeforeClass
DECL|method|startUpCluster ()
specifier|public
specifier|static
name|void
name|startUpCluster
parameter_list|()
throws|throws
name|IOException
block|{
comment|// disable block scanner
name|CONF
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_SCAN_PERIOD_HOURS_KEY
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Set short retry timeouts so this test runs faster
name|CONF
operator|.
name|setInt
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Retry
operator|.
name|WINDOW_BASE_KEY
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|setBoolean
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|=
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
name|NUMDATANODES
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|MiniDFSNNTopology
operator|.
name|simpleHATopology
argument_list|(
literal|3
argument_list|)
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
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToObserver
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|String
name|nameservice
init|=
name|HATestUtil
operator|.
name|getLogicalHostname
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|HATestUtil
operator|.
name|setFailoverConfigurations
argument_list|(
name|cluster
argument_list|,
name|CONF
argument_list|,
name|nameservice
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|CONF
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|Failover
operator|.
name|PROXY_PROVIDER_KEY_PREFIX
operator|+
literal|"."
operator|+
name|nameservice
argument_list|,
name|ORPPwithAlignmentContexts
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|CONF
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
name|After
DECL|method|after ()
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|IOException
block|{
name|killWorkers
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|transitionToStandby
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|active
operator|=
literal|0
expr_stmt|;
name|standby
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
name|dfs
operator|=
literal|null
expr_stmt|;
block|}
name|AC_LIST
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * This test checks if after a client writes we can see the state id in    * updated via the response.    */
annotation|@
name|Test
DECL|method|testStateTransferOnWrite ()
specifier|public
name|void
name|testStateTransferOnWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|preWriteState
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
name|active
argument_list|)
operator|.
name|getLastWrittenTransactionId
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testFile1"
argument_list|)
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|long
name|clientState
init|=
name|getContext
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastSeenStateId
argument_list|()
decl_stmt|;
name|long
name|postWriteState
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
name|active
argument_list|)
operator|.
name|getLastWrittenTransactionId
argument_list|()
decl_stmt|;
comment|// Write(s) should have increased state. Check for greater than.
name|assertThat
argument_list|(
name|clientState
operator|>
name|preWriteState
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Client and server state should be equal.
name|assertThat
argument_list|(
name|clientState
argument_list|,
name|is
argument_list|(
name|postWriteState
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test checks if after a client reads we can see the state id in    * updated via the response.    */
annotation|@
name|Test
DECL|method|testStateTransferOnRead ()
specifier|public
name|void
name|testStateTransferOnRead
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testFile2"
argument_list|)
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|long
name|lastWrittenId
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
name|active
argument_list|)
operator|.
name|getLastWrittenTransactionId
argument_list|()
decl_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testFile2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Read should catch client up to last written state.
name|long
name|clientState
init|=
name|getContext
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastSeenStateId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|clientState
argument_list|,
name|is
argument_list|(
name|lastWrittenId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * This test checks that a fresh client starts with no state and becomes    * updated of state from RPC call.    */
annotation|@
name|Test
DECL|method|testStateTransferOnFreshClient ()
specifier|public
name|void
name|testStateTransferOnFreshClient
parameter_list|()
throws|throws
name|Exception
block|{
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testFile3"
argument_list|)
argument_list|,
literal|"ezpz"
argument_list|)
expr_stmt|;
name|long
name|lastWrittenId
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
name|active
argument_list|)
operator|.
name|getLastWrittenTransactionId
argument_list|()
decl_stmt|;
try|try
init|(
name|DistributedFileSystem
name|clearDfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|CONF
argument_list|)
init|)
block|{
name|ClientGSIContext
name|clientState
init|=
name|getContext
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|clientState
operator|.
name|getLastSeenStateId
argument_list|()
argument_list|,
name|is
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|readFile
argument_list|(
name|clearDfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testFile3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clientState
operator|.
name|getLastSeenStateId
argument_list|()
argument_list|,
name|is
argument_list|(
name|lastWrittenId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test checks if after a client writes we can see the state id in    * updated via the response.    */
annotation|@
name|Test
DECL|method|testStateTransferOnWriteWithFailover ()
specifier|public
name|void
name|testStateTransferOnWriteWithFailover
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|preWriteState
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
name|active
argument_list|)
operator|.
name|getLastWrittenTransactionId
argument_list|()
decl_stmt|;
comment|// Write using HA client.
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testFile1FO"
argument_list|)
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|long
name|clientState
init|=
name|getContext
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastSeenStateId
argument_list|()
decl_stmt|;
name|long
name|postWriteState
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
name|active
argument_list|)
operator|.
name|getLastWrittenTransactionId
argument_list|()
decl_stmt|;
comment|// Write(s) should have increased state. Check for greater than.
name|assertThat
argument_list|(
name|clientState
operator|>
name|preWriteState
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Client and server state should be equal.
name|assertThat
argument_list|(
name|clientState
argument_list|,
name|is
argument_list|(
name|postWriteState
argument_list|)
argument_list|)
expr_stmt|;
comment|// Failover NameNode.
name|failOver
argument_list|()
expr_stmt|;
comment|// Write using HA client.
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|dfs
argument_list|,
operator|new
name|Path
argument_list|(
literal|"/testFile2FO"
argument_list|)
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|long
name|clientStateFO
init|=
name|getContext
argument_list|(
literal|0
argument_list|)
operator|.
name|getLastSeenStateId
argument_list|()
decl_stmt|;
name|long
name|writeStateFO
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|(
name|active
argument_list|)
operator|.
name|getLastWrittenTransactionId
argument_list|()
decl_stmt|;
comment|// Write(s) should have increased state. Check for greater than.
name|assertThat
argument_list|(
name|clientStateFO
operator|>
name|postWriteState
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Client and server state should be equal.
name|assertThat
argument_list|(
name|clientStateFO
argument_list|,
name|is
argument_list|(
name|writeStateFO
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testMultiClientStatesWithRandomFailovers ()
specifier|public
name|void
name|testMultiClientStatesWithRandomFailovers
parameter_list|()
throws|throws
name|Exception
block|{
comment|// First run, half the load, with one failover.
name|runClientsWithFailover
argument_list|(
literal|1
argument_list|,
name|NUMCLIENTS
operator|/
literal|2
argument_list|,
name|NUMFILES
operator|/
literal|2
argument_list|)
expr_stmt|;
comment|// Second half, with fail back.
name|runClientsWithFailover
argument_list|(
name|NUMCLIENTS
operator|/
literal|2
operator|+
literal|1
argument_list|,
name|NUMCLIENTS
argument_list|,
name|NUMFILES
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|runClientsWithFailover (int clientStartId, int numClients, int numFiles)
specifier|private
name|void
name|runClientsWithFailover
parameter_list|(
name|int
name|clientStartId
parameter_list|,
name|int
name|numClients
parameter_list|,
name|int
name|numFiles
parameter_list|)
throws|throws
name|Exception
block|{
name|ExecutorService
name|execService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|clients
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numClients
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|clientStartId
init|;
name|i
operator|<=
name|numClients
condition|;
name|i
operator|++
control|)
block|{
name|DistributedFileSystem
name|haClient
init|=
operator|(
name|DistributedFileSystem
operator|)
name|FileSystem
operator|.
name|get
argument_list|(
name|CONF
argument_list|)
decl_stmt|;
name|clients
operator|.
name|add
argument_list|(
operator|new
name|Worker
argument_list|(
name|haClient
argument_list|,
name|numFiles
argument_list|,
literal|"/testFile3FO_"
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Execute workers in threadpool with random failovers.
name|List
argument_list|<
name|Future
argument_list|<
name|STATE
argument_list|>
argument_list|>
name|futures
init|=
name|submitAll
argument_list|(
name|execService
argument_list|,
name|clients
argument_list|)
decl_stmt|;
name|execService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|boolean
name|finished
init|=
literal|false
decl_stmt|;
name|failOver
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|finished
condition|)
block|{
name|finished
operator|=
name|execService
operator|.
name|awaitTermination
argument_list|(
literal|20L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
comment|// Validation.
for|for
control|(
name|Future
argument_list|<
name|STATE
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|assertThat
argument_list|(
name|future
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
name|STATE
operator|.
name|SUCCESS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|clients
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getContext (int clientCreationIndex)
specifier|private
name|ClientGSIContext
name|getContext
parameter_list|(
name|int
name|clientCreationIndex
parameter_list|)
block|{
return|return
name|AC_LIST
operator|.
name|get
argument_list|(
name|clientCreationIndex
argument_list|)
return|;
block|}
DECL|method|failOver ()
specifier|private
name|void
name|failOver
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Transitioning Active to Standby"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToStandby
argument_list|(
name|active
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Transitioning Standby to Active"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
name|standby
argument_list|)
expr_stmt|;
name|int
name|tempActive
init|=
name|active
decl_stmt|;
name|active
operator|=
name|standby
expr_stmt|;
name|standby
operator|=
name|tempActive
expr_stmt|;
block|}
comment|/* Executor.invokeAll() is blocking so utilizing submit instead. */
DECL|method|submitAll (ExecutorService executor, Collection<Worker> calls)
specifier|private
specifier|static
name|List
argument_list|<
name|Future
argument_list|<
name|STATE
argument_list|>
argument_list|>
name|submitAll
parameter_list|(
name|ExecutorService
name|executor
parameter_list|,
name|Collection
argument_list|<
name|Worker
argument_list|>
name|calls
parameter_list|)
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|STATE
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|calls
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Worker
name|call
range|:
name|calls
control|)
block|{
name|Future
argument_list|<
name|STATE
argument_list|>
name|future
init|=
name|executor
operator|.
name|submit
argument_list|(
name|call
argument_list|)
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
return|return
name|futures
return|;
block|}
DECL|method|killWorkers ()
specifier|private
name|void
name|killWorkers
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|clients
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Worker
name|worker
range|:
name|clients
control|)
block|{
name|worker
operator|.
name|kill
argument_list|()
expr_stmt|;
block|}
name|clients
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|enum|STATE
DECL|enumConstant|SUCCESS
DECL|enumConstant|FAIL
DECL|enumConstant|ERROR
specifier|private
enum|enum
name|STATE
block|{
name|SUCCESS
block|,
name|FAIL
block|,
name|ERROR
block|}
DECL|class|Worker
specifier|private
class|class
name|Worker
implements|implements
name|Callable
argument_list|<
name|STATE
argument_list|>
block|{
DECL|field|client
specifier|private
specifier|final
name|DistributedFileSystem
name|client
decl_stmt|;
DECL|field|filesToMake
specifier|private
specifier|final
name|int
name|filesToMake
decl_stmt|;
DECL|field|filePath
specifier|private
name|String
name|filePath
decl_stmt|;
DECL|field|nonce
specifier|private
specifier|final
name|int
name|nonce
decl_stmt|;
DECL|method|Worker (DistributedFileSystem client, int filesToMake, String filePath, int nonce)
name|Worker
parameter_list|(
name|DistributedFileSystem
name|client
parameter_list|,
name|int
name|filesToMake
parameter_list|,
name|String
name|filePath
parameter_list|,
name|int
name|nonce
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|filesToMake
operator|=
name|filesToMake
expr_stmt|;
name|this
operator|.
name|filePath
operator|=
name|filePath
expr_stmt|;
name|this
operator|.
name|nonce
operator|=
name|nonce
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|STATE
name|call
parameter_list|()
block|{
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|filesToMake
condition|;
name|i
operator|++
control|)
block|{
name|ClientGSIContext
name|gsiContext
init|=
name|getContext
argument_list|(
name|nonce
argument_list|)
decl_stmt|;
name|long
name|preClientStateFO
init|=
name|gsiContext
operator|.
name|getLastSeenStateId
argument_list|()
decl_stmt|;
comment|// Write using HA client.
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|filePath
operator|+
name|nonce
operator|+
literal|"_"
operator|+
name|i
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|writeFile
argument_list|(
name|client
argument_list|,
name|path
argument_list|,
literal|"erk"
argument_list|)
expr_stmt|;
name|long
name|postClientStateFO
init|=
name|gsiContext
operator|.
name|getLastSeenStateId
argument_list|()
decl_stmt|;
comment|// Write(s) should have increased state. Check for greater than.
if|if
condition|(
name|postClientStateFO
operator|<
literal|0
operator|||
name|postClientStateFO
operator|<=
name|preClientStateFO
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"FAIL: Worker started with: {} , but finished with: {}"
argument_list|,
name|preClientStateFO
argument_list|,
name|postClientStateFO
argument_list|)
expr_stmt|;
return|return
name|STATE
operator|.
name|FAIL
return|;
block|}
if|if
condition|(
name|i
operator|%
operator|(
name|NUMFILES
operator|/
literal|10
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Worker {} created {} files"
argument_list|,
name|nonce
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"LastSeenStateId = {}"
argument_list|,
name|postClientStateFO
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|STATE
operator|.
name|SUCCESS
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ERROR: Worker failed with: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|STATE
operator|.
name|ERROR
return|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Worker {} created {} files"
argument_list|,
name|nonce
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|kill ()
specifier|public
name|void
name|kill
parameter_list|()
throws|throws
name|IOException
block|{
name|client
operator|.
name|dfs
operator|.
name|closeAllFilesBeingWritten
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|client
operator|.
name|dfs
operator|.
name|closeOutputStreams
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|client
operator|.
name|dfs
operator|.
name|closeConnectionToNamenode
argument_list|()
expr_stmt|;
name|client
operator|.
name|dfs
operator|.
name|close
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

