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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
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
name|util
operator|.
name|VersionInfo
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
name|webapp
operator|.
name|WebServer
operator|.
name|NMWebApp
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|YarnVersionInfo
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
name|GenericExceptionHandler
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
name|WebApp
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
name|WebServicesTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Guice
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|GuiceServletContextListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|ServletModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|UniformInterfaceException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|WebResource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|guice
operator|.
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|GuiceContainer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|test
operator|.
name|framework
operator|.
name|JerseyTest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|test
operator|.
name|framework
operator|.
name|WebAppDescriptor
import|;
end_import

begin_comment
comment|/**  * Test the nodemanager node info web services api's  */
end_comment

begin_class
DECL|class|TestNMWebServices
specifier|public
class|class
name|TestNMWebServices
extends|extends
name|JerseyTest
block|{
DECL|field|nmContext
specifier|private
specifier|static
name|Context
name|nmContext
decl_stmt|;
DECL|field|resourceView
specifier|private
specifier|static
name|ResourceView
name|resourceView
decl_stmt|;
DECL|field|aclsManager
specifier|private
specifier|static
name|ApplicationACLsManager
name|aclsManager
decl_stmt|;
DECL|field|dirsHandler
specifier|private
specifier|static
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|nmWebApp
specifier|private
specifier|static
name|WebApp
name|nmWebApp
decl_stmt|;
DECL|field|testRootDir
specifier|private
specifier|static
specifier|final
name|File
name|testRootDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestNMWebServices
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
name|TestNMWebServices
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"LogDir"
argument_list|)
decl_stmt|;
DECL|field|injector
specifier|private
name|Injector
name|injector
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|ServletModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|nmContext
operator|=
operator|new
name|NodeManager
operator|.
name|NMContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|nmContext
operator|.
name|getNodeId
argument_list|()
operator|.
name|setHost
argument_list|(
literal|"testhost.foo.com"
argument_list|)
expr_stmt|;
name|nmContext
operator|.
name|getNodeId
argument_list|()
operator|.
name|setPort
argument_list|(
literal|8042
argument_list|)
expr_stmt|;
name|resourceView
operator|=
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
comment|// 15.5G in bytes
return|return
operator|new
name|Long
argument_list|(
literal|"16642998272"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPmemAllocatedForContainers
parameter_list|()
block|{
comment|// 16G in bytes
return|return
operator|new
name|Long
argument_list|(
literal|"17179869184"
argument_list|)
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
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|testRootDir
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
operator|new
name|NodeHealthCheckerService
argument_list|()
decl_stmt|;
name|healthChecker
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dirsHandler
operator|=
name|healthChecker
operator|.
name|getDiskHandler
argument_list|()
expr_stmt|;
name|aclsManager
operator|=
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nmWebApp
operator|=
operator|new
name|NMWebApp
argument_list|(
name|resourceView
argument_list|,
name|aclsManager
argument_list|,
name|dirsHandler
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|JAXBContextResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|NMWebServices
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GenericExceptionHandler
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Context
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|nmContext
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|WebApp
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|nmWebApp
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ResourceView
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|resourceView
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ApplicationACLsManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|aclsManager
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|LocalDirsHandlerService
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|dirsHandler
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|with
argument_list|(
name|GuiceContainer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
DECL|class|GuiceServletConfig
specifier|public
class|class
name|GuiceServletConfig
extends|extends
name|GuiceServletContextListener
block|{
annotation|@
name|Override
DECL|method|getInjector ()
specifier|protected
name|Injector
name|getInjector
parameter_list|()
block|{
return|return
name|injector
return|;
block|}
block|}
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|testRootDir
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
name|AfterClass
DECL|method|stop ()
specifier|static
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testRootDir
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
DECL|method|TestNMWebServices ()
specifier|public
name|TestNMWebServices
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|WebAppDescriptor
operator|.
name|Builder
argument_list|(
literal|"org.apache.hadoop.yarn.server.nodemanager.webapp"
argument_list|)
operator|.
name|contextListenerClass
argument_list|(
name|GuiceServletConfig
operator|.
name|class
argument_list|)
operator|.
name|filterClass
argument_list|(
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|servlet
operator|.
name|GuiceFilter
operator|.
name|class
argument_list|)
operator|.
name|contextPath
argument_list|(
literal|"jersey-guice-filter"
argument_list|)
operator|.
name|servletPath
argument_list|(
literal|"/"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidUri ()
specifier|public
name|void
name|testInvalidUri
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|String
name|responseStr
init|=
literal|""
decl_stmt|;
try|try
block|{
name|responseStr
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node"
argument_list|)
operator|.
name|path
argument_list|(
literal|"bogus"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown exception on invalid uri"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
name|ue
parameter_list|)
block|{
name|ClientResponse
name|response
init|=
name|ue
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|NOT_FOUND
argument_list|,
name|response
operator|.
name|getClientResponseStatus
argument_list|()
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"error string exists and shouldn't"
argument_list|,
literal|""
argument_list|,
name|responseStr
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidAccept ()
specifier|public
name|void
name|testInvalidAccept
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|String
name|responseStr
init|=
literal|""
decl_stmt|;
try|try
block|{
name|responseStr
operator|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
operator|.
name|get
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown exception on invalid uri"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
name|ue
parameter_list|)
block|{
name|ClientResponse
name|response
init|=
name|ue
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|,
name|response
operator|.
name|getClientResponseStatus
argument_list|()
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"error string exists and shouldn't"
argument_list|,
literal|""
argument_list|,
name|responseStr
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidUri2 ()
specifier|public
name|void
name|testInvalidUri2
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|String
name|responseStr
init|=
literal|""
decl_stmt|;
try|try
block|{
name|responseStr
operator|=
name|r
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown exception on invalid uri"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UniformInterfaceException
name|ue
parameter_list|)
block|{
name|ClientResponse
name|response
init|=
name|ue
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|NOT_FOUND
argument_list|,
name|response
operator|.
name|getClientResponseStatus
argument_list|()
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"error string exists and shouldn't"
argument_list|,
literal|""
argument_list|,
name|responseStr
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNode ()
specifier|public
name|void
name|testNode
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|json
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
decl_stmt|;
name|verifyNodeInfo
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeSlash ()
specifier|public
name|void
name|testNodeSlash
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node/"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|json
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
decl_stmt|;
name|verifyNodeInfo
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
comment|// make sure default is json output
annotation|@
name|Test
DECL|method|testNodeDefault ()
specifier|public
name|void
name|testNodeDefault
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node"
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|json
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
decl_stmt|;
name|verifyNodeInfo
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeInfo ()
specifier|public
name|void
name|testNodeInfo
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|json
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
decl_stmt|;
name|verifyNodeInfo
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeInfoSlash ()
specifier|public
name|void
name|testNodeInfoSlash
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info/"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|json
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
decl_stmt|;
name|verifyNodeInfo
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
comment|// make sure default is json output
annotation|@
name|Test
DECL|method|testNodeInfoDefault ()
specifier|public
name|void
name|testNodeInfoDefault
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info"
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|json
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|JSONObject
operator|.
name|class
argument_list|)
decl_stmt|;
name|verifyNodeInfo
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleNodesXML ()
specifier|public
name|void
name|testSingleNodesXML
parameter_list|()
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|ClientResponse
name|response
init|=
name|r
operator|.
name|path
argument_list|(
literal|"ws"
argument_list|)
operator|.
name|path
argument_list|(
literal|"v1"
argument_list|)
operator|.
name|path
argument_list|(
literal|"node"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info/"
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML_TYPE
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|response
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|db
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|()
decl_stmt|;
name|is
operator|.
name|setCharacterStream
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|dom
init|=
name|db
operator|.
name|parse
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|NodeList
name|nodes
init|=
name|dom
operator|.
name|getElementsByTagName
argument_list|(
literal|"nodeInfo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|1
argument_list|,
name|nodes
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|verifyNodesXML
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyNodesXML (NodeList nodes)
specifier|public
name|void
name|verifyNodesXML
parameter_list|(
name|NodeList
name|nodes
parameter_list|)
throws|throws
name|JSONException
throws|,
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|element
init|=
operator|(
name|Element
operator|)
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|verifyNodeInfoGeneric
argument_list|(
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"id"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"healthReport"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlLong
argument_list|(
name|element
argument_list|,
literal|"totalVmemAllocatedContainersMB"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlLong
argument_list|(
name|element
argument_list|,
literal|"totalPmemAllocatedContainersMB"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlBoolean
argument_list|(
name|element
argument_list|,
literal|"vmemCheckEnabled"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlBoolean
argument_list|(
name|element
argument_list|,
literal|"pmemCheckEnabled"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlLong
argument_list|(
name|element
argument_list|,
literal|"lastNodeUpdateTime"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlBoolean
argument_list|(
name|element
argument_list|,
literal|"nodeHealthy"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"nodeHostName"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"hadoopVersionBuiltOn"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"hadoopBuildVersion"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"hadoopVersion"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"nodeManagerVersionBuiltOn"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"nodeManagerBuildVersion"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"nodeManagerVersion"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyNodeInfo (JSONObject json)
specifier|public
name|void
name|verifyNodeInfo
parameter_list|(
name|JSONObject
name|json
parameter_list|)
throws|throws
name|JSONException
throws|,
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|1
argument_list|,
name|json
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|info
init|=
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"nodeInfo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|15
argument_list|,
name|info
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|verifyNodeInfoGeneric
argument_list|(
name|info
operator|.
name|getString
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"healthReport"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"totalVmemAllocatedContainersMB"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"totalPmemAllocatedContainersMB"
argument_list|)
argument_list|,
name|info
operator|.
name|getBoolean
argument_list|(
literal|"vmemCheckEnabled"
argument_list|)
argument_list|,
name|info
operator|.
name|getBoolean
argument_list|(
literal|"pmemCheckEnabled"
argument_list|)
argument_list|,
name|info
operator|.
name|getLong
argument_list|(
literal|"lastNodeUpdateTime"
argument_list|)
argument_list|,
name|info
operator|.
name|getBoolean
argument_list|(
literal|"nodeHealthy"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"nodeHostName"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"hadoopVersionBuiltOn"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"hadoopBuildVersion"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"hadoopVersion"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"nodeManagerVersionBuiltOn"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"nodeManagerBuildVersion"
argument_list|)
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"nodeManagerVersion"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyNodeInfoGeneric (String id, String healthReport, long totalVmemAllocatedContainersMB, long totalPmemAllocatedContainersMB, boolean vmemCheckEnabled, boolean pmemCheckEnabled, long lastNodeUpdateTime, Boolean nodeHealthy, String nodeHostName, String hadoopVersionBuiltOn, String hadoopBuildVersion, String hadoopVersion, String resourceManagerVersionBuiltOn, String resourceManagerBuildVersion, String resourceManagerVersion)
specifier|public
name|void
name|verifyNodeInfoGeneric
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|healthReport
parameter_list|,
name|long
name|totalVmemAllocatedContainersMB
parameter_list|,
name|long
name|totalPmemAllocatedContainersMB
parameter_list|,
name|boolean
name|vmemCheckEnabled
parameter_list|,
name|boolean
name|pmemCheckEnabled
parameter_list|,
name|long
name|lastNodeUpdateTime
parameter_list|,
name|Boolean
name|nodeHealthy
parameter_list|,
name|String
name|nodeHostName
parameter_list|,
name|String
name|hadoopVersionBuiltOn
parameter_list|,
name|String
name|hadoopBuildVersion
parameter_list|,
name|String
name|hadoopVersion
parameter_list|,
name|String
name|resourceManagerVersionBuiltOn
parameter_list|,
name|String
name|resourceManagerBuildVersion
parameter_list|,
name|String
name|resourceManagerVersion
parameter_list|)
block|{
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"id"
argument_list|,
literal|"testhost.foo.com:8042"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"healthReport"
argument_list|,
literal|"Healthy"
argument_list|,
name|healthReport
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"totalVmemAllocatedContainersMB incorrect"
argument_list|,
literal|15872
argument_list|,
name|totalVmemAllocatedContainersMB
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"totalPmemAllocatedContainersMB incorrect"
argument_list|,
literal|16384
argument_list|,
name|totalPmemAllocatedContainersMB
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"vmemCheckEnabled incorrect"
argument_list|,
literal|true
argument_list|,
name|vmemCheckEnabled
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pmemCheckEnabled incorrect"
argument_list|,
literal|true
argument_list|,
name|pmemCheckEnabled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"lastNodeUpdateTime incorrect"
argument_list|,
name|lastNodeUpdateTime
operator|==
name|nmContext
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getLastHealthReportTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"nodeHealthy isn't true"
argument_list|,
name|nodeHealthy
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"nodeHostName"
argument_list|,
literal|"testhost.foo.com"
argument_list|,
name|nodeHostName
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"hadoopVersionBuiltOn"
argument_list|,
name|VersionInfo
operator|.
name|getDate
argument_list|()
argument_list|,
name|hadoopVersionBuiltOn
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringEqual
argument_list|(
literal|"hadoopBuildVersion"
argument_list|,
name|VersionInfo
operator|.
name|getBuildVersion
argument_list|()
argument_list|,
name|hadoopBuildVersion
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"hadoopVersion"
argument_list|,
name|VersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|,
name|hadoopVersion
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"resourceManagerVersionBuiltOn"
argument_list|,
name|YarnVersionInfo
operator|.
name|getDate
argument_list|()
argument_list|,
name|resourceManagerVersionBuiltOn
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringEqual
argument_list|(
literal|"resourceManagerBuildVersion"
argument_list|,
name|YarnVersionInfo
operator|.
name|getBuildVersion
argument_list|()
argument_list|,
name|resourceManagerBuildVersion
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"resourceManagerVersion"
argument_list|,
name|YarnVersionInfo
operator|.
name|getVersion
argument_list|()
argument_list|,
name|resourceManagerVersion
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

