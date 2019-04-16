begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
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
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
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
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_SAFEMODE_EXPIRATION
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
name|server
operator|.
name|federation
operator|.
name|router
operator|.
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_SAFEMODE_EXTENSION
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|deleteStateStore
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|FederationStateStoreTestUtils
operator|.
name|getStateStoreConfiguration
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
name|assertFalse
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
name|URISyntaxException
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
name|server
operator|.
name|federation
operator|.
name|RouterConfigBuilder
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
name|federation
operator|.
name|resolver
operator|.
name|MountTableResolver
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
name|federation
operator|.
name|store
operator|.
name|StateStoreUnavailableException
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|EnterSafeModeRequest
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
name|tools
operator|.
name|federation
operator|.
name|RouterAdmin
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
name|StandbyException
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
name|service
operator|.
name|Service
operator|.
name|STATE
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ToolRunner
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

begin_comment
comment|/**  * Test the safe mode for the {@link Router} controlled by  * {@link RouterSafemodeService}.  */
end_comment

begin_class
DECL|class|TestRouterSafemode
specifier|public
class|class
name|TestRouterSafemode
block|{
DECL|field|router
specifier|private
name|Router
name|router
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|create ()
specifier|public
specifier|static
name|void
name|create
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Wipe state store
name|deleteStateStore
argument_list|()
expr_stmt|;
comment|// Configuration that supports the state store
name|conf
operator|=
name|getStateStoreConfiguration
argument_list|()
expr_stmt|;
comment|// 2 sec startup standby
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|DFS_ROUTER_SAFEMODE_EXTENSION
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// 200 ms cache refresh
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
argument_list|,
literal|200
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
comment|// 1 sec post cache update before entering safemode (2 intervals)
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|DFS_ROUTER_SAFEMODE_EXPIRATION
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_RPC_BIND_HOST_KEY
argument_list|,
literal|"0.0.0.0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_RPC_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_BIND_HOST_KEY
argument_list|,
literal|"0.0.0.0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTP_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_HTTPS_ADDRESS_KEY
argument_list|,
literal|"127.0.0.1:0"
argument_list|)
expr_stmt|;
comment|// RPC + State Store + Safe Mode only
name|conf
operator|=
operator|new
name|RouterConfigBuilder
argument_list|(
name|conf
argument_list|)
operator|.
name|rpc
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|safemode
argument_list|()
operator|.
name|stateStore
argument_list|()
operator|.
name|metrics
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|destroy ()
specifier|public
specifier|static
name|void
name|destroy
parameter_list|()
block|{   }
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|router
operator|=
operator|new
name|Router
argument_list|()
expr_stmt|;
name|router
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|router
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|router
operator|!=
literal|null
condition|)
block|{
name|router
operator|.
name|stop
argument_list|()
expr_stmt|;
name|router
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSafemodeService ()
specifier|public
name|void
name|testSafemodeService
parameter_list|()
throws|throws
name|IOException
block|{
name|RouterSafemodeService
name|server
init|=
operator|new
name|RouterSafemodeService
argument_list|(
name|router
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|,
name|server
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|server
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STOPPED
argument_list|,
name|server
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRouterExitSafemode ()
specifier|public
name|void
name|testRouterExitSafemode
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IllegalStateException
throws|,
name|IOException
block|{
name|assertTrue
argument_list|(
name|router
operator|.
name|getSafemodeService
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
comment|// Wait for initial time in milliseconds
name|long
name|interval
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_SAFEMODE_EXTENSION
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|+
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|router
operator|.
name|getSafemodeService
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRouterEnterSafemode ()
specifier|public
name|void
name|testRouterEnterSafemode
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Verify starting state
name|assertTrue
argument_list|(
name|router
operator|.
name|getSafemodeService
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
comment|// We should be in safe mode for DFS_ROUTER_SAFEMODE_EXTENSION time
name|long
name|interval0
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_SAFEMODE_EXTENSION
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|-
literal|1000
decl_stmt|;
name|long
name|t0
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
while|while
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|-
name|t0
operator|<
name|interval0
condition|)
block|{
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// We wait some time for the state to propagate
name|long
name|interval1
init|=
literal|1000
operator|+
literal|2
operator|*
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|interval1
argument_list|)
expr_stmt|;
comment|// Running
name|assertFalse
argument_list|(
name|router
operator|.
name|getSafemodeService
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// Disable cache
name|router
operator|.
name|getStateStore
argument_list|()
operator|.
name|stopCacheUpdateService
argument_list|()
expr_stmt|;
comment|// Wait until the State Store cache is stale in milliseconds
name|long
name|interval2
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_SAFEMODE_EXPIRATION
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|+
literal|2
operator|*
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|interval2
argument_list|)
expr_stmt|;
comment|// Safemode
name|assertTrue
argument_list|(
name|router
operator|.
name|getSafemodeService
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRouterRpcSafeMode ()
specifier|public
name|void
name|testRouterRpcSafeMode
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|IOException
block|{
name|assertTrue
argument_list|(
name|router
operator|.
name|getSafemodeService
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
comment|// If the Router is in Safe Mode, we should get a SafeModeException
name|boolean
name|exception
init|=
literal|false
decl_stmt|;
try|try
block|{
name|router
operator|.
name|getRpcServer
argument_list|()
operator|.
name|delete
argument_list|(
literal|"/testfile.txt"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should have thrown a safe mode exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StandbyException
name|sme
parameter_list|)
block|{
name|exception
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"We should have thrown a safe mode exception"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRouterManualSafeMode ()
specifier|public
name|void
name|testRouterManualSafeMode
parameter_list|()
throws|throws
name|Exception
block|{
name|InetSocketAddress
name|adminAddr
init|=
name|router
operator|.
name|getAdminServerAddress
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setSocketAddr
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_ADMIN_ADDRESS_KEY
argument_list|,
name|adminAddr
argument_list|)
expr_stmt|;
name|RouterAdmin
name|admin
init|=
operator|new
name|RouterAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|router
operator|.
name|getSafemodeService
argument_list|()
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
comment|// Wait until the Router exit start up safe mode
name|long
name|interval
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_SAFEMODE_EXTENSION
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|+
literal|300
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// Now enter safe mode via Router admin command - it should work
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ToolRunner
operator|.
name|run
argument_list|(
name|admin
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-safemode"
block|,
literal|"enter"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
comment|// Wait for update interval of the safe mode service, it should still in
comment|// safe mode.
name|interval
operator|=
literal|2
operator|*
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
comment|// Exit safe mode via admin command
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ToolRunner
operator|.
name|run
argument_list|(
name|admin
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-safemode"
block|,
literal|"leave"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyRouter (RouterServiceState status)
specifier|private
name|void
name|verifyRouter
parameter_list|(
name|RouterServiceState
name|status
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|IOException
block|{
name|assertEquals
argument_list|(
name|status
argument_list|,
name|router
operator|.
name|getRouterState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRouterNotInitMountTable ()
specifier|public
name|void
name|testRouterNotInitMountTable
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Manually disable the mount table to trigger unavailable exceptions
name|MountTableResolver
name|mountTable
init|=
operator|(
name|MountTableResolver
operator|)
name|router
operator|.
name|getSubclusterResolver
argument_list|()
decl_stmt|;
name|mountTable
operator|.
name|setDisabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Wait until it gets out of safe mode
name|int
name|interval
init|=
literal|2
operator|*
operator|(
name|int
operator|)
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|DFS_ROUTER_SAFEMODE_EXTENSION
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|2
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
parameter_list|()
lambda|->
name|router
operator|.
name|getRouterState
argument_list|()
operator|==
name|RouterServiceState
operator|.
name|RUNNING
argument_list|,
literal|100
argument_list|,
name|interval
argument_list|)
expr_stmt|;
comment|// Getting file info should fail
try|try
block|{
name|router
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getFileInfo
argument_list|(
literal|"/mnt/file.txt"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should have thrown StateStoreUnavailableException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StateStoreUnavailableException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Mount Table not initialized"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Enter safe mode
name|RouterAdminServer
name|admin
init|=
name|router
operator|.
name|getAdminServer
argument_list|()
decl_stmt|;
name|EnterSafeModeRequest
name|request
init|=
name|EnterSafeModeRequest
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|admin
operator|.
name|enterSafeMode
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|verifyRouter
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
comment|// This time it should report safe mode
try|try
block|{
name|router
operator|.
name|getRpcServer
argument_list|()
operator|.
name|getFileInfo
argument_list|(
literal|"/mnt/file.txt"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"We should have thrown a safe mode exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StandbyException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong message: "
operator|+
name|msg
argument_list|,
name|msg
operator|.
name|endsWith
argument_list|(
literal|"is in safe mode and cannot handle READ requests"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

