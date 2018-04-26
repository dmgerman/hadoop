begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServlet
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
name|HttpServletRequest
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
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|ServerConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletContextHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|thread
operator|.
name|QueuedThreadPool
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|exceptions
operator|.
name|LauncherExitCodes
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test case for CLI to API Service.  *  */
end_comment

begin_class
DECL|class|TestApiServiceClient
specifier|public
class|class
name|TestApiServiceClient
block|{
DECL|field|asc
specifier|private
specifier|static
name|ApiServiceClient
name|asc
decl_stmt|;
DECL|field|badAsc
specifier|private
specifier|static
name|ApiServiceClient
name|badAsc
decl_stmt|;
DECL|field|server
specifier|private
specifier|static
name|Server
name|server
decl_stmt|;
comment|/**    * A mocked version of API Service for testing purpose.    *    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|TestServlet
specifier|public
specifier|static
class|class
name|TestServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Get was called"
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doPost (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doPut (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doPut
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doDelete (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doDelete
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|=
operator|new
name|Server
argument_list|(
literal|8088
argument_list|)
expr_stmt|;
operator|(
operator|(
name|QueuedThreadPool
operator|)
name|server
operator|.
name|getThreadPool
argument_list|()
operator|)
operator|.
name|setMaxThreads
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|ServletContextHandler
name|context
init|=
operator|new
name|ServletContextHandler
argument_list|()
decl_stmt|;
name|context
operator|.
name|setContextPath
argument_list|(
literal|"/app"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|addServlet
argument_list|(
operator|new
name|ServletHolder
argument_list|(
name|TestServlet
operator|.
name|class
argument_list|)
argument_list|,
literal|"/*"
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ServerConnector
operator|)
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|)
operator|.
name|setHost
argument_list|(
literal|"localhost"
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"yarn.resourcemanager.webapp.address"
argument_list|,
literal|"localhost:8088"
argument_list|)
expr_stmt|;
name|asc
operator|=
operator|new
name|ApiServiceClient
argument_list|()
expr_stmt|;
name|asc
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Configuration
name|conf2
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf2
operator|.
name|set
argument_list|(
literal|"yarn.resourcemanager.webapp.address"
argument_list|,
literal|"localhost:8089"
argument_list|)
expr_stmt|;
name|badAsc
operator|=
operator|new
name|ApiServiceClient
argument_list|()
expr_stmt|;
name|badAsc
operator|.
name|serviceInit
argument_list|(
name|conf2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLaunch ()
specifier|public
name|void
name|testLaunch
parameter_list|()
block|{
name|String
name|fileName
init|=
literal|"target/test-classes/example-app.json"
decl_stmt|;
name|String
name|appName
init|=
literal|"example-app"
decl_stmt|;
name|long
name|lifetime
init|=
literal|3600L
decl_stmt|;
name|String
name|queue
init|=
literal|"default"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|asc
operator|.
name|actionLaunch
argument_list|(
name|fileName
argument_list|,
name|appName
argument_list|,
name|lifetime
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_SUCCESS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadLaunch ()
specifier|public
name|void
name|testBadLaunch
parameter_list|()
block|{
name|String
name|fileName
init|=
literal|"unknown_file"
decl_stmt|;
name|String
name|appName
init|=
literal|"unknown_app"
decl_stmt|;
name|long
name|lifetime
init|=
literal|3600L
decl_stmt|;
name|String
name|queue
init|=
literal|"default"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|badAsc
operator|.
name|actionLaunch
argument_list|(
name|fileName
argument_list|,
name|appName
argument_list|,
name|lifetime
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStop ()
specifier|public
name|void
name|testStop
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"example-app"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|asc
operator|.
name|actionStop
argument_list|(
name|appName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_SUCCESS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadStop ()
specifier|public
name|void
name|testBadStop
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"unknown_app"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|badAsc
operator|.
name|actionStop
argument_list|(
name|appName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStart ()
specifier|public
name|void
name|testStart
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"example-app"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|asc
operator|.
name|actionStart
argument_list|(
name|appName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_SUCCESS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadStart ()
specifier|public
name|void
name|testBadStart
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"unknown_app"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|badAsc
operator|.
name|actionStart
argument_list|(
name|appName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSave ()
specifier|public
name|void
name|testSave
parameter_list|()
block|{
name|String
name|fileName
init|=
literal|"target/test-classes/example-app.json"
decl_stmt|;
name|String
name|appName
init|=
literal|"example-app"
decl_stmt|;
name|long
name|lifetime
init|=
literal|3600L
decl_stmt|;
name|String
name|queue
init|=
literal|"default"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|asc
operator|.
name|actionSave
argument_list|(
name|fileName
argument_list|,
name|appName
argument_list|,
name|lifetime
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_SUCCESS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadSave ()
specifier|public
name|void
name|testBadSave
parameter_list|()
block|{
name|String
name|fileName
init|=
literal|"unknown_file"
decl_stmt|;
name|String
name|appName
init|=
literal|"unknown_app"
decl_stmt|;
name|long
name|lifetime
init|=
literal|3600L
decl_stmt|;
name|String
name|queue
init|=
literal|"default"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|badAsc
operator|.
name|actionSave
argument_list|(
name|fileName
argument_list|,
name|appName
argument_list|,
name|lifetime
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFlex ()
specifier|public
name|void
name|testFlex
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"example-app"
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|componentCounts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|asc
operator|.
name|actionFlex
argument_list|(
name|appName
argument_list|,
name|componentCounts
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_SUCCESS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadFlex ()
specifier|public
name|void
name|testBadFlex
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"unknown_app"
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|componentCounts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|badAsc
operator|.
name|actionFlex
argument_list|(
name|appName
argument_list|,
name|componentCounts
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDestroy ()
specifier|public
name|void
name|testDestroy
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"example-app"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|asc
operator|.
name|actionDestroy
argument_list|(
name|appName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_SUCCESS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadDestroy ()
specifier|public
name|void
name|testBadDestroy
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"unknown_app"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|badAsc
operator|.
name|actionDestroy
argument_list|(
name|appName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInitiateServiceUpgrade ()
specifier|public
name|void
name|testInitiateServiceUpgrade
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"example-app"
decl_stmt|;
name|String
name|upgradeFileName
init|=
literal|"target/test-classes/example-app.json"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|asc
operator|.
name|initiateUpgrade
argument_list|(
name|appName
argument_list|,
name|upgradeFileName
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_SUCCESS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInstancesUpgrade ()
specifier|public
name|void
name|testInstancesUpgrade
parameter_list|()
block|{
name|String
name|appName
init|=
literal|"example-app"
decl_stmt|;
try|try
block|{
name|int
name|result
init|=
name|asc
operator|.
name|actionUpgradeInstances
argument_list|(
name|appName
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"comp-1"
argument_list|,
literal|"comp-2"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|EXIT_SUCCESS
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|YarnException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

