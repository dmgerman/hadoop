begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|HttpURLConnection
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
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|ExitUtil
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|event
operator|.
name|AsyncDispatcher
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|server
operator|.
name|MiniYARNCluster
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|AdminService
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|ResourceManager
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|RMCriticalThreadUncaughtExceptionHandler
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|MockRM
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|RMFatalEvent
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|RMFatalEventType
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
name|yarn
operator|.
name|server
operator|.
name|webproxy
operator|.
name|WebAppProxyServer
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
name|yarn
operator|.
name|webapp
operator|.
name|YarnWebParams
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

begin_class
DECL|class|TestRMFailover
specifier|public
class|class
name|TestRMFailover
extends|extends
name|ClientBaseWithFixes
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
name|TestRMFailover
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|req
specifier|private
specifier|static
specifier|final
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
name|req
init|=
operator|new
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
argument_list|(
name|HAServiceProtocol
operator|.
name|RequestSource
operator|.
name|REQUEST_BY_USER
argument_list|)
decl_stmt|;
DECL|field|RM1_NODE_ID
specifier|private
specifier|static
specifier|final
name|String
name|RM1_NODE_ID
init|=
literal|"rm1"
decl_stmt|;
DECL|field|RM1_PORT_BASE
specifier|private
specifier|static
specifier|final
name|int
name|RM1_PORT_BASE
init|=
literal|10000
decl_stmt|;
DECL|field|RM2_NODE_ID
specifier|private
specifier|static
specifier|final
name|String
name|RM2_NODE_ID
init|=
literal|"rm2"
decl_stmt|;
DECL|field|RM2_PORT_BASE
specifier|private
specifier|static
specifier|final
name|int
name|RM2_PORT_BASE
init|=
literal|20000
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniYARNCluster
name|cluster
decl_stmt|;
DECL|field|fakeAppId
specifier|private
name|ApplicationId
name|fakeAppId
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|fakeAppId
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
name|RM1_NODE_ID
operator|+
literal|","
operator|+
name|RM2_NODE_ID
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
name|RM1_NODE_ID
argument_list|,
name|RM1_PORT_BASE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
name|RM2_NODE_ID
argument_list|,
name|RM2_PORT_BASE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_SLEEPTIME_BASE_MS
argument_list|,
literal|100L
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_MINICLUSTER_FIXED_PORTS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_MINICLUSTER_USE_RPC
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestRMFailover
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyClientConnection ()
specifier|private
name|void
name|verifyClientConnection
parameter_list|()
block|{
name|int
name|numRetries
init|=
literal|3
decl_stmt|;
while|while
condition|(
name|numRetries
operator|--
operator|>
literal|0
condition|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
name|YarnClient
name|client
init|=
name|YarnClient
operator|.
name|createYarnClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|client
operator|.
name|getApplications
argument_list|()
expr_stmt|;
return|return;
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
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
name|fail
argument_list|(
literal|"Client couldn't connect to the Active RM"
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyConnections ()
specifier|private
name|void
name|verifyConnections
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|YarnException
block|{
name|assertTrue
argument_list|(
literal|"NMs failed to connect to the RM"
argument_list|,
name|cluster
operator|.
name|waitForNodeManagersToConnect
argument_list|(
literal|20000
argument_list|)
argument_list|)
expr_stmt|;
name|verifyClientConnection
argument_list|()
expr_stmt|;
block|}
DECL|method|getAdminService (int index)
specifier|private
name|AdminService
name|getAdminService
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|cluster
operator|.
name|getResourceManager
argument_list|(
name|index
argument_list|)
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMAdminService
argument_list|()
return|;
block|}
DECL|method|explicitFailover ()
specifier|private
name|void
name|explicitFailover
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|activeRMIndex
init|=
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
decl_stmt|;
name|int
name|newActiveRMIndex
init|=
operator|(
name|activeRMIndex
operator|+
literal|1
operator|)
operator|%
literal|2
decl_stmt|;
name|getAdminService
argument_list|(
name|activeRMIndex
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|getAdminService
argument_list|(
name|newActiveRMIndex
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failover failed"
argument_list|,
name|newActiveRMIndex
argument_list|,
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|failover ()
specifier|private
name|void
name|failover
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|YarnException
block|{
name|int
name|activeRMIndex
init|=
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|stopResourceManager
argument_list|(
name|activeRMIndex
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failover failed"
argument_list|,
operator|(
name|activeRMIndex
operator|+
literal|1
operator|)
operator|%
literal|2
argument_list|,
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|restartResourceManager
argument_list|(
name|activeRMIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExplicitFailover ()
specifier|public
name|void
name|testExplicitFailover
parameter_list|()
throws|throws
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"RM never turned active"
argument_list|,
operator|-
literal|1
operator|==
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
name|explicitFailover
argument_list|()
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
name|explicitFailover
argument_list|()
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyRMTransitionToStandby (ResourceManager rm)
specifier|private
name|void
name|verifyRMTransitionToStandby
parameter_list|(
name|ResourceManager
name|rm
parameter_list|)
throws|throws
name|InterruptedException
block|{
try|try
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
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
operator|==
name|HAServiceState
operator|.
name|STANDBY
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"RM didn't transition to Standby."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAutomaticFailover ()
specifier|public
name|void
name|testAutomaticFailover
parameter_list|()
throws|throws
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
literal|"yarn-test-cluster"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|,
name|hostPort
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"RM never turned active"
argument_list|,
operator|-
literal|1
operator|==
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
name|failover
argument_list|()
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
name|failover
argument_list|()
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
comment|// Make the current Active handle an RMFatalEvent,
comment|// so it transitions to standby.
name|ResourceManager
name|rm
init|=
name|cluster
operator|.
name|getResourceManager
argument_list|(
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
decl_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMFatalEvent
argument_list|(
name|RMFatalEventType
operator|.
name|STATE_STORE_FENCED
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRMTransitionToStandby
argument_list|(
name|rm
argument_list|)
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWebAppProxyInStandAloneMode ()
specifier|public
name|void
name|testWebAppProxyInStandAloneMode
parameter_list|()
throws|throws
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|WebAppProxyServer
name|webAppProxyServer
init|=
operator|new
name|WebAppProxyServer
argument_list|()
decl_stmt|;
try|try
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|PROXY_ADDRESS
argument_list|,
literal|"0.0.0.0:9099"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|getAdminService
argument_list|(
literal|0
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"RM never turned active"
argument_list|,
operator|-
literal|1
operator|==
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
name|webAppProxyServer
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Start webAppProxyServer
name|Assert
operator|.
name|assertEquals
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|,
name|webAppProxyServer
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|webAppProxyServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|webAppProxyServer
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
comment|// send httpRequest with fakeApplicationId
comment|// expect to get "Not Found" response and 404 response code
name|URL
name|wrongUrl
init|=
operator|new
name|URL
argument_list|(
literal|"http://0.0.0.0:9099/proxy/"
operator|+
name|fakeAppId
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|proxyConn
init|=
operator|(
name|HttpURLConnection
operator|)
name|wrongUrl
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|proxyConn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|verifyResponse
argument_list|(
name|proxyConn
argument_list|)
expr_stmt|;
name|explicitFailover
argument_list|()
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
name|proxyConn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|verifyResponse
argument_list|(
name|proxyConn
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|webAppProxyServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEmbeddedWebAppProxy ()
specifier|public
name|void
name|testEmbeddedWebAppProxy
parameter_list|()
throws|throws
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"RM never turned active"
argument_list|,
operator|-
literal|1
operator|==
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
comment|// send httpRequest with fakeApplicationId
comment|// expect to get "Not Found" response and 404 response code
name|URL
name|wrongUrl
init|=
operator|new
name|URL
argument_list|(
literal|"http://0.0.0.0:18088/proxy/"
operator|+
name|fakeAppId
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|proxyConn
init|=
operator|(
name|HttpURLConnection
operator|)
name|wrongUrl
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|proxyConn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|verifyResponse
argument_list|(
name|proxyConn
argument_list|)
expr_stmt|;
name|explicitFailover
argument_list|()
expr_stmt|;
name|verifyConnections
argument_list|()
expr_stmt|;
name|proxyConn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|verifyResponse
argument_list|(
name|proxyConn
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyResponse (HttpURLConnection response)
specifier|private
name|void
name|verifyResponse
parameter_list|(
name|HttpURLConnection
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|"Not Found"
argument_list|,
name|response
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|404
argument_list|,
name|response
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRMWebAppRedirect ()
specifier|public
name|void
name|testRMWebAppRedirect
parameter_list|()
throws|throws
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|cluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestRMFailover
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|getAdminService
argument_list|(
literal|0
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|String
name|rm1Url
init|=
literal|"http://0.0.0.0:18088"
decl_stmt|;
name|String
name|rm2Url
init|=
literal|"http://0.0.0.0:28088"
decl_stmt|;
name|String
name|redirectURL
init|=
name|getRedirectURL
argument_list|(
name|rm2Url
argument_list|)
decl_stmt|;
comment|// if uri is null, RMWebAppFilter will append a slash at the trail of the redirection url
name|assertEquals
argument_list|(
name|redirectURL
argument_list|,
name|rm1Url
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/metrics"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|redirectURL
argument_list|,
name|rm1Url
operator|+
literal|"/metrics"
argument_list|)
expr_stmt|;
comment|// standby RM links /conf, /stacks, /logLevel, /static, /logs, /jmx
comment|// /cluster/cluster as well as webService
comment|// /ws/v1/cluster/info should not be redirected to active RM
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/cluster/cluster"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/conf"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/stacks"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/logLevel"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/static"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/logs"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/jmx?param1=value1+x&param2=y"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/ws/v1/cluster/info"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/ws/v1/cluster/apps"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|redirectURL
argument_list|,
name|rm1Url
operator|+
literal|"/ws/v1/cluster/apps"
argument_list|)
expr_stmt|;
name|redirectURL
operator|=
name|getRedirectURL
argument_list|(
name|rm2Url
operator|+
literal|"/proxy/"
operator|+
name|fakeAppId
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|redirectURL
argument_list|)
expr_stmt|;
comment|// transit the active RM to standby
comment|// Both of RMs are in standby mode
name|getAdminService
argument_list|(
literal|0
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|req
argument_list|)
expr_stmt|;
comment|// RM2 is expected to send the httpRequest to itself.
comment|// The Header Field: Refresh is expected to be set.
name|redirectURL
operator|=
name|getRefreshURL
argument_list|(
name|rm2Url
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|redirectURL
operator|!=
literal|null
operator|&&
name|redirectURL
operator|.
name|contains
argument_list|(
name|YarnWebParams
operator|.
name|NEXT_REFRESH_INTERVAL
argument_list|)
operator|&&
name|redirectURL
operator|.
name|contains
argument_list|(
name|rm2Url
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// set up http connection with the given url and get the redirection url from the response
comment|// return null if the url is not redirected
DECL|method|getRedirectURL (String url)
specifier|static
name|String
name|getRedirectURL
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|String
name|redirectUrl
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openConnection
argument_list|()
decl_stmt|;
comment|// do not automatically follow the redirection
comment|// otherwise we get too many redirections exception
name|conn
operator|.
name|setInstanceFollowRedirects
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|conn
operator|.
name|getResponseCode
argument_list|()
operator|==
name|HttpServletResponse
operator|.
name|SC_TEMPORARY_REDIRECT
condition|)
block|{
name|redirectUrl
operator|=
name|conn
operator|.
name|getHeaderField
argument_list|(
literal|"Location"
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
comment|// throw new RuntimeException(e);
block|}
return|return
name|redirectUrl
return|;
block|}
DECL|method|getRefreshURL (String url)
specifier|static
name|String
name|getRefreshURL
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|String
name|redirectUrl
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openConnection
argument_list|()
decl_stmt|;
comment|// do not automatically follow the redirection
comment|// otherwise we get too many redirections exception
name|conn
operator|.
name|setInstanceFollowRedirects
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|redirectUrl
operator|=
name|conn
operator|.
name|getHeaderField
argument_list|(
literal|"Refresh"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// throw new RuntimeException(e);
block|}
return|return
name|redirectUrl
return|;
block|}
comment|/**    * Throw {@link RuntimeException} inside a thread of    * {@link ResourceManager} with HA enabled and check if the    * {@link ResourceManager} is transited to standby state.    *    * @throws InterruptedException if any    */
annotation|@
name|Test
DECL|method|testUncaughtExceptionHandlerWithHAEnabled ()
specifier|public
name|void
name|testUncaughtExceptionHandlerWithHAEnabled
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
literal|"yarn-test-cluster"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|,
name|hostPort
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"RM never turned active"
argument_list|,
operator|-
literal|1
operator|==
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
expr_stmt|;
name|ResourceManager
name|resourceManager
init|=
name|cluster
operator|.
name|getResourceManager
argument_list|(
name|cluster
operator|.
name|getActiveRMIndex
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RMCriticalThreadUncaughtExceptionHandler
name|exHandler
init|=
operator|new
name|RMCriticalThreadUncaughtExceptionHandler
argument_list|(
name|resourceManager
operator|.
name|getRMContext
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create a thread and throw a RTE inside it
specifier|final
name|RuntimeException
name|rte
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"TestRuntimeException"
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|testThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
name|rte
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|testThread
operator|.
name|setName
argument_list|(
literal|"TestThread"
argument_list|)
expr_stmt|;
name|testThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|exHandler
argument_list|)
expr_stmt|;
name|testThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|testThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|verifyRMTransitionToStandby
argument_list|(
name|resourceManager
argument_list|)
expr_stmt|;
block|}
comment|/**    * Throw {@link RuntimeException} inside a thread of    * {@link ResourceManager} with HA disabled and check    * {@link RMCriticalThreadUncaughtExceptionHandler} instance.    *    * Used {@link ExitUtil} class to avoid jvm exit through    * {@code System.exit(-1)}.    *    * @throws InterruptedException if any    */
annotation|@
name|Test
DECL|method|testUncaughtExceptionHandlerWithoutHA ()
specifier|public
name|void
name|testUncaughtExceptionHandlerWithoutHA
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
comment|// Create a MockRM and start it
name|ResourceManager
name|resourceManager
init|=
operator|new
name|MockRM
argument_list|()
decl_stmt|;
operator|(
operator|(
name|AsyncDispatcher
operator|)
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getDispatcher
argument_list|()
operator|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getStateStore
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getContainerTokenSecretManager
argument_list|()
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
specifier|final
name|RMCriticalThreadUncaughtExceptionHandler
name|exHandler
init|=
operator|new
name|RMCriticalThreadUncaughtExceptionHandler
argument_list|(
name|resourceManager
operator|.
name|getRMContext
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RMCriticalThreadUncaughtExceptionHandler
name|spyRTEHandler
init|=
name|spy
argument_list|(
name|exHandler
argument_list|)
decl_stmt|;
comment|// Create a thread and throw a RTE inside it
specifier|final
name|RuntimeException
name|rte
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"TestRuntimeException"
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|testThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
throw|throw
name|rte
throw|;
block|}
block|}
argument_list|)
decl_stmt|;
name|testThread
operator|.
name|setName
argument_list|(
literal|"TestThread"
argument_list|)
expr_stmt|;
name|testThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|spyRTEHandler
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|spyRTEHandler
argument_list|,
name|testThread
operator|.
name|getUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|testThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|testThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|spyRTEHandler
argument_list|)
operator|.
name|uncaughtException
argument_list|(
name|testThread
argument_list|,
name|rte
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

