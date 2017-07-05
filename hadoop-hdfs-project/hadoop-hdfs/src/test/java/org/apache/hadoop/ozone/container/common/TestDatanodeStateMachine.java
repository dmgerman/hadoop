begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
package|;
end_package

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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|DatanodeID
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
name|RPC
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerUtils
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
name|scm
operator|.
name|ScmConfigKeys
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|DatanodeStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|EndpointStateMachine
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|SCMConnectionManager
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|DatanodeState
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|datanode
operator|.
name|InitDatanodeState
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
operator|.
name|datanode
operator|.
name|RunningDatanodeState
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
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
name|concurrent
operator|.
name|HadoopExecutors
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Map
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
name|ExecutionException
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
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HEARTBEAT_RPC_TIMEOUT
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

begin_comment
comment|/**  * Tests the datanode state machine class and its states.  */
end_comment

begin_class
DECL|class|TestDatanodeStateMachine
specifier|public
class|class
name|TestDatanodeStateMachine
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
name|TestDatanodeStateMachine
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|scmServerCount
specifier|private
specifier|final
name|int
name|scmServerCount
init|=
literal|3
decl_stmt|;
DECL|field|serverAddresses
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|serverAddresses
decl_stmt|;
DECL|field|scmServers
specifier|private
name|List
argument_list|<
name|RPC
operator|.
name|Server
argument_list|>
name|scmServers
decl_stmt|;
DECL|field|mockServers
specifier|private
name|List
argument_list|<
name|ScmTestMock
argument_list|>
name|mockServers
decl_stmt|;
DECL|field|executorService
specifier|private
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|testRoot
specifier|private
name|File
name|testRoot
decl_stmt|;
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
name|conf
operator|=
name|SCMTestUtils
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|OZONE_SCM_HEARTBEAT_RPC_TIMEOUT
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|serverAddresses
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|scmServers
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|mockServers
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|scmServerCount
condition|;
name|x
operator|++
control|)
block|{
name|int
name|port
init|=
name|SCMTestUtils
operator|.
name|getReuseableAddress
argument_list|()
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|String
name|address
init|=
literal|"127.0.0.1"
decl_stmt|;
name|serverAddresses
operator|.
name|add
argument_list|(
name|address
operator|+
literal|":"
operator|+
name|port
argument_list|)
expr_stmt|;
name|ScmTestMock
name|mock
init|=
operator|new
name|ScmTestMock
argument_list|()
decl_stmt|;
name|scmServers
operator|.
name|add
argument_list|(
name|SCMTestUtils
operator|.
name|startScmRpcServer
argument_list|(
name|conf
argument_list|,
name|mock
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
name|address
argument_list|,
name|port
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|mockServers
operator|.
name|add
argument_list|(
name|mock
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setStrings
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
argument_list|,
name|serverAddresses
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|URL
name|p
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|p
operator|.
name|getPath
argument_list|()
operator|.
name|concat
argument_list|(
name|TestDatanodeStateMachine
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|testRoot
operator|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|testRoot
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Required directories already exist."
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
operator|new
name|File
argument_list|(
name|testRoot
argument_list|,
literal|"data"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_METADATA_DIRS
argument_list|,
operator|new
name|File
argument_list|(
name|testRoot
argument_list|,
literal|"scm"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|path
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|,
name|TestDatanodeStateMachine
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".id"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|executorService
operator|=
name|HadoopExecutors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Test Data Node State Machine Thread - %d"
argument_list|)
operator|.
name|build
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
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|executorService
operator|!=
literal|null
condition|)
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|executorService
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to shutdown properly."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error attempting to shutdown."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|RPC
operator|.
name|Server
name|s
range|:
name|scmServers
control|)
block|{
name|s
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//ignore all execption from the shutdown
block|}
finally|finally
block|{
name|testRoot
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Assert that starting statemachine executes the Init State.    *    * @throws InterruptedException    */
annotation|@
name|Test
DECL|method|testStartStopDatanodeStateMachine ()
specifier|public
name|void
name|testStartStopDatanodeStateMachine
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
try|try
init|(
name|DatanodeStateMachine
name|stateMachine
init|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|conf
argument_list|)
init|)
block|{
name|stateMachine
operator|.
name|startDaemon
argument_list|()
expr_stmt|;
name|SCMConnectionManager
name|connectionManager
init|=
name|stateMachine
operator|.
name|getConnectionManager
argument_list|()
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|connectionManager
operator|.
name|getValues
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|,
literal|1000
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
name|stateMachine
operator|.
name|stopDaemon
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|stateMachine
operator|.
name|isDaemonStopped
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test explores the state machine by invoking each call in sequence just    * like as if the state machine would call it. Because this is a test we are    * able to verify each of the assumptions.    *<p>    * Here is what happens at High level.    *<p>    * 1. We start the datanodeStateMachine in the INIT State.    *<p>    * 2. We invoke the INIT state task.    *<p>    * 3. That creates a set of RPC endpoints that are ready to connect to SCMs.    *<p>    * 4. We assert that we have moved to the running state for the    * DatanodeStateMachine.    *<p>    * 5. We get the task for the Running State - Executing that running state,    * makes the first network call in of the state machine. The Endpoint is in    * the GETVERSION State and we invoke the task.    *<p>    * 6. We assert that this call was a success by checking that each of the    * endponts now have version response that it got from the SCM server that it    * was talking to and also each of the mock server serviced one RPC call.    *<p>    * 7. Since the Register is done now, next calls to get task will return    * HeartbeatTask, which sends heartbeats to SCM. We assert that we get right    * task from sub-system below.    *    * @throws IOException    */
annotation|@
name|Test
DECL|method|testDatanodeStateContext ()
specifier|public
name|void
name|testDatanodeStateContext
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
comment|// There is no mini cluster started in this test,
comment|// create a ID file so that state machine could load a fake datanode ID.
name|File
name|idPath
init|=
operator|new
name|File
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID
argument_list|)
argument_list|)
decl_stmt|;
name|idPath
operator|.
name|delete
argument_list|()
expr_stmt|;
name|DatanodeID
name|dnID
init|=
name|DFSTestUtil
operator|.
name|getLocalDatanodeID
argument_list|()
decl_stmt|;
name|dnID
operator|.
name|setContainerPort
argument_list|(
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_IPC_PORT_DEFAULT
argument_list|)
expr_stmt|;
name|ContainerUtils
operator|.
name|writeDatanodeIDTo
argument_list|(
name|dnID
argument_list|,
name|idPath
argument_list|)
expr_stmt|;
try|try
init|(
name|DatanodeStateMachine
name|stateMachine
init|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|conf
argument_list|)
init|)
block|{
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|currentState
init|=
name|stateMachine
operator|.
name|getContext
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|INIT
argument_list|,
name|currentState
argument_list|)
expr_stmt|;
name|DatanodeState
argument_list|<
name|DatanodeStateMachine
operator|.
name|DatanodeStates
argument_list|>
name|task
init|=
name|stateMachine
operator|.
name|getContext
argument_list|()
operator|.
name|getTask
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|InitDatanodeState
operator|.
name|class
argument_list|,
name|task
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|task
operator|.
name|execute
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|newState
init|=
name|task
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
for|for
control|(
name|EndpointStateMachine
name|endpoint
range|:
name|stateMachine
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|getValues
argument_list|()
control|)
block|{
comment|// We assert that each of the is in State GETVERSION.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|GETVERSION
argument_list|,
name|endpoint
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// The Datanode has moved into Running State, since endpoints are created.
comment|// We move to running state when we are ready to issue RPC calls to SCMs.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|newState
argument_list|)
expr_stmt|;
comment|// If we had called context.execute instead of calling into each state
comment|// this would have happened automatically.
name|stateMachine
operator|.
name|getContext
argument_list|()
operator|.
name|setState
argument_list|(
name|newState
argument_list|)
expr_stmt|;
name|task
operator|=
name|stateMachine
operator|.
name|getContext
argument_list|()
operator|.
name|getTask
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RunningDatanodeState
operator|.
name|class
argument_list|,
name|task
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
comment|// This execute will invoke getVersion calls against all SCM endpoints
comment|// that we know of.
name|task
operator|.
name|execute
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
name|newState
operator|=
name|task
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// If we are in running state, we should be in running.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|newState
argument_list|)
expr_stmt|;
for|for
control|(
name|EndpointStateMachine
name|endpoint
range|:
name|stateMachine
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|getValues
argument_list|()
control|)
block|{
comment|// Since the earlier task.execute called into GetVersion, the
comment|// endPointState Machine should move to REGISTER state.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|EndpointStateMachine
operator|.
name|EndPointStates
operator|.
name|REGISTER
argument_list|,
name|endpoint
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
comment|// We assert that each of the end points have gotten a version from the
comment|// SCM Server.
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|endpoint
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// We can also assert that all mock servers have received only one RPC
comment|// call at this point of time.
for|for
control|(
name|ScmTestMock
name|mock
range|:
name|mockServers
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mock
operator|.
name|getRpcCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// This task is the Running task, but running task executes tasks based
comment|// on the state of Endpoints, hence this next call will be a Register at
comment|// the endpoint RPC level.
name|task
operator|=
name|stateMachine
operator|.
name|getContext
argument_list|()
operator|.
name|getTask
argument_list|()
expr_stmt|;
name|task
operator|.
name|execute
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
name|newState
operator|=
name|task
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// If we are in running state, we should be in running.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|newState
argument_list|)
expr_stmt|;
for|for
control|(
name|ScmTestMock
name|mock
range|:
name|mockServers
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mock
operator|.
name|getRpcCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// This task is the Running task, but running task executes tasks based
comment|// on the state of Endpoints, hence this next call will be a
comment|// HeartbeatTask at the endpoint RPC level.
name|task
operator|=
name|stateMachine
operator|.
name|getContext
argument_list|()
operator|.
name|getTask
argument_list|()
expr_stmt|;
name|task
operator|.
name|execute
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
name|newState
operator|=
name|task
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// If we are in running state, we should be in running.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|RUNNING
argument_list|,
name|newState
argument_list|)
expr_stmt|;
for|for
control|(
name|ScmTestMock
name|mock
range|:
name|mockServers
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mock
operator|.
name|getHeartbeatCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Assert that heartbeat did indeed carry that State that we said
comment|// have in the datanode.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|mock
operator|.
name|getReportState
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|getNumber
argument_list|()
argument_list|,
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ReportState
operator|.
name|states
operator|.
name|noContainerReports
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test state transition with a list of invalid scm configurations,    * and verify the state transits to SHUTDOWN each time.    */
annotation|@
name|Test
DECL|method|testDatanodeStateMachineWithInvalidConfiguration ()
specifier|public
name|void
name|testDatanodeStateMachineWithInvalidConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|LinkedList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|confList
init|=
operator|new
name|LinkedList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|confList
operator|.
name|add
argument_list|(
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// Invalid ozone.scm.names
comment|/** Empty **/
name|confList
operator|.
name|add
argument_list|(
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|/** Invalid schema **/
name|confList
operator|.
name|add
argument_list|(
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
argument_list|,
literal|"x..y"
argument_list|)
argument_list|)
expr_stmt|;
comment|/** Invalid port **/
name|confList
operator|.
name|add
argument_list|(
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
argument_list|,
literal|"scm:xyz"
argument_list|)
argument_list|)
expr_stmt|;
comment|/** Port out of range **/
name|confList
operator|.
name|add
argument_list|(
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_NAMES
argument_list|,
literal|"scm:123456"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Invalid ozone.scm.datanode.id
comment|/** Empty **/
name|confList
operator|.
name|add
argument_list|(
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ID
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|confList
operator|.
name|forEach
argument_list|(
parameter_list|(
name|entry
parameter_list|)
lambda|->
block|{
name|Configuration
name|perTestConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|perTestConf
operator|.
name|setStrings
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|DatanodeStateMachine
name|stateMachine
init|=
operator|new
name|DatanodeStateMachine
argument_list|(
name|perTestConf
argument_list|)
init|)
block|{
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|currentState
init|=
name|stateMachine
operator|.
name|getContext
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|INIT
argument_list|,
name|currentState
argument_list|)
expr_stmt|;
name|DatanodeState
argument_list|<
name|DatanodeStateMachine
operator|.
name|DatanodeStates
argument_list|>
name|task
init|=
name|stateMachine
operator|.
name|getContext
argument_list|()
operator|.
name|getTask
argument_list|()
decl_stmt|;
name|task
operator|.
name|execute
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
name|DatanodeStateMachine
operator|.
name|DatanodeStates
name|newState
init|=
name|task
operator|.
name|await
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|DatanodeStateMachine
operator|.
name|DatanodeStates
operator|.
name|SHUTDOWN
argument_list|,
name|newState
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Unexpected exception found"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

