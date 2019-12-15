begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
package|package
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
name|nodemanager
operator|.
name|webapp
package|;
end_package

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
name|FileUtil
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
name|server
operator|.
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|LocalDirsHandlerService
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
name|nodemanager
operator|.
name|NodeManager
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
name|nodemanager
operator|.
name|ResourceView
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
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
name|nodemanager
operator|.
name|health
operator|.
name|NodeHealthCheckerService
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
name|security
operator|.
name|ApplicationACLsManager
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
name|websocket
operator|.
name|api
operator|.
name|Session
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
name|websocket
operator|.
name|api
operator|.
name|UpgradeRequest
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
name|websocket
operator|.
name|client
operator|.
name|WebSocketClient
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|URI
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
name|HashMap
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
name|Future
import|;
end_import

begin_comment
comment|/**  * Test class for Node Manager Container Web Socket.  */
end_comment

begin_class
DECL|class|TestNMContainerWebSocket
specifier|public
class|class
name|TestNMContainerWebSocket
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
name|TestNMContainerWebSocket
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TESTROOTDIR
specifier|private
specifier|static
specifier|final
name|File
name|TESTROOTDIR
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestNMWebServer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|testLogDir
specifier|private
specifier|static
name|File
name|testLogDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestNMWebServer
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"LogDir"
argument_list|)
decl_stmt|;
DECL|field|server
specifier|private
name|WebServer
name|server
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|TESTROOTDIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|testLogDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TESTROOTDIR
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testLogDir
argument_list|)
expr_stmt|;
block|}
DECL|method|startNMWebAppServer (String webAddr)
specifier|private
name|int
name|startNMWebAppServer
parameter_list|(
name|String
name|webAddr
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Context
name|nmContext
init|=
operator|new
name|NodeManager
operator|.
name|NMContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ResourceView
name|resourceView
init|=
operator|new
name|ResourceView
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|getVmemAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPmemAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getVCoresAllocatedForContainers
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isVmemCheckEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPmemCheckEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|TESTROOTDIR
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|testLogDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|NodeHealthCheckerService
name|healthChecker
init|=
name|createNodeHealthCheckerService
argument_list|()
decl_stmt|;
name|healthChecker
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirsHandler
init|=
name|healthChecker
operator|.
name|getDiskHandler
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_ADDRESS
argument_list|,
name|webAddr
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|WebServer
argument_list|(
name|nmContext
argument_list|,
name|resourceView
argument_list|,
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
argument_list|,
name|dirsHandler
argument_list|)
expr_stmt|;
try|try
block|{
name|server
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|server
operator|.
name|getPort
argument_list|()
return|;
block|}
finally|finally
block|{     }
block|}
DECL|method|createNodeHealthCheckerService ()
specifier|private
name|NodeHealthCheckerService
name|createNodeHealthCheckerService
parameter_list|()
block|{
name|LocalDirsHandlerService
name|dirsHandler
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
return|return
operator|new
name|NodeHealthCheckerService
argument_list|(
name|dirsHandler
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testWebServerWithServlet ()
specifier|public
name|void
name|testWebServerWithServlet
parameter_list|()
block|{
name|int
name|port
init|=
name|startNMWebAppServer
argument_list|(
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"bind to port: "
operator|+
name|port
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ws://localhost:"
argument_list|)
operator|.
name|append
argument_list|(
name|port
argument_list|)
operator|.
name|append
argument_list|(
literal|"/container/abc/"
argument_list|)
expr_stmt|;
name|String
name|dest
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|WebSocketClient
name|client
init|=
operator|new
name|WebSocketClient
argument_list|()
decl_stmt|;
try|try
block|{
name|ContainerShellClientSocketTest
name|socket
init|=
operator|new
name|ContainerShellClientSocketTest
argument_list|()
decl_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|URI
name|echoUri
init|=
operator|new
name|URI
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|Session
argument_list|>
name|future
init|=
name|client
operator|.
name|connect
argument_list|(
name|socket
argument_list|,
name|echoUri
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|session
operator|.
name|getRemote
argument_list|()
operator|.
name|sendString
argument_list|(
literal|"hello world"
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to connect WebSocket and send message to server"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Failed to close client"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testContainerShellWebSocket ()
specifier|public
name|void
name|testContainerShellWebSocket
parameter_list|()
block|{
name|Context
name|nm
init|=
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|mock
argument_list|(
name|Session
operator|.
name|class
argument_list|)
decl_stmt|;
name|Container
name|container
init|=
name|mock
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|UpgradeRequest
name|request
init|=
name|mock
argument_list|(
name|UpgradeRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|ApplicationACLsManager
name|aclManager
init|=
name|mock
argument_list|(
name|ApplicationACLsManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerShellWebSocket
operator|.
name|init
argument_list|(
name|nm
argument_list|)
expr_stmt|;
name|ContainerShellWebSocket
name|ws
init|=
operator|new
name|ContainerShellWebSocket
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|names
operator|.
name|add
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|mockParameters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|mockParameters
operator|.
name|put
argument_list|(
literal|"user.name"
argument_list|,
name|names
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|session
operator|.
name|getUpgradeRequest
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|request
operator|.
name|getParameterMap
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockParameters
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|container
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|nm
operator|.
name|getApplicationACLsManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|aclManager
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|aclManager
operator|.
name|areACLsEnabled
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|boolean
name|authorized
init|=
name|ws
operator|.
name|checkAuthorization
argument_list|(
name|session
argument_list|,
name|container
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Not authorized"
argument_list|,
name|authorized
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should not throw exception."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

