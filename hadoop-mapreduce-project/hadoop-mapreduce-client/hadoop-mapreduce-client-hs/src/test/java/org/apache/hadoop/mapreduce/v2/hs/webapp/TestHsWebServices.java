begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|webapp
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
name|yarn
operator|.
name|webapp
operator|.
name|WebServicesTestUtils
operator|.
name|assertResponseStatusCode
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
name|fail
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
name|http
operator|.
name|JettyUtils
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|HistoryContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|JobHistory
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|JobHistoryServer
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|MockHistoryContext
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
name|GuiceServletConfig
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
name|JerseyTestBase
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
name|WebAppDescriptor
import|;
end_import

begin_comment
comment|/**  * Test the History Server info web services api's. Also test non-existent urls.  *  *  /ws/v1/history  *  /ws/v1/history/info  */
end_comment

begin_class
DECL|class|TestHsWebServices
specifier|public
class|class
name|TestHsWebServices
extends|extends
name|JerseyTestBase
block|{
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|appContext
specifier|private
specifier|static
name|HistoryContext
name|appContext
decl_stmt|;
DECL|field|webApp
specifier|private
specifier|static
name|HsWebApp
name|webApp
decl_stmt|;
DECL|class|WebServletModule
specifier|private
specifier|static
class|class
name|WebServletModule
extends|extends
name|ServletModule
block|{
annotation|@
name|Override
DECL|method|configureServlets ()
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|appContext
operator|=
operator|new
name|MockHistoryContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|JobHistory
name|jobHistoryService
init|=
operator|new
name|JobHistory
argument_list|()
decl_stmt|;
name|HistoryContext
name|historyContext
init|=
operator|(
name|HistoryContext
operator|)
name|jobHistoryService
decl_stmt|;
name|webApp
operator|=
operator|new
name|HsWebApp
argument_list|(
name|historyContext
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
name|HsWebServices
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
name|WebApp
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|webApp
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|appContext
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|HistoryContext
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|appContext
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|conf
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
static|static
block|{
name|GuiceServletConfig
operator|.
name|setInjector
argument_list|(
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|WebServletModule
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|GuiceServletConfig
operator|.
name|setInjector
argument_list|(
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|WebServletModule
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TestHsWebServices ()
specifier|public
name|TestHsWebServices
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|WebAppDescriptor
operator|.
name|Builder
argument_list|(
literal|"org.apache.hadoop.mapreduce.v2.hs.webapp"
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
DECL|method|testHS ()
specifier|public
name|void
name|testHS
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
literal|"history"
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
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
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
name|verifyHSInfo
argument_list|(
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"historyInfo"
argument_list|)
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHSSlash ()
specifier|public
name|void
name|testHSSlash
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
literal|"history/"
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
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
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
name|verifyHSInfo
argument_list|(
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"historyInfo"
argument_list|)
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHSDefault ()
specifier|public
name|void
name|testHSDefault
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
literal|"history/"
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
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
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
name|verifyHSInfo
argument_list|(
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"historyInfo"
argument_list|)
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHSXML ()
specifier|public
name|void
name|testHSXML
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
literal|"history"
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
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
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
name|verifyHSInfoXML
argument_list|(
name|xml
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInfo ()
specifier|public
name|void
name|testInfo
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
literal|"history"
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
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
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
name|verifyHSInfo
argument_list|(
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"historyInfo"
argument_list|)
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInfoSlash ()
specifier|public
name|void
name|testInfoSlash
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
literal|"history"
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
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
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
name|verifyHSInfo
argument_list|(
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"historyInfo"
argument_list|)
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInfoDefault ()
specifier|public
name|void
name|testInfoDefault
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
literal|"history"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info/"
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
name|APPLICATION_JSON
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
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
name|verifyHSInfo
argument_list|(
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"historyInfo"
argument_list|)
argument_list|,
name|appContext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInfoXML ()
specifier|public
name|void
name|testInfoXML
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
literal|"history"
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
name|APPLICATION_XML
operator|+
literal|"; "
operator|+
name|JettyUtils
operator|.
name|UTF_8
argument_list|,
name|response
operator|.
name|getType
argument_list|()
operator|.
name|toString
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
name|verifyHSInfoXML
argument_list|(
name|xml
argument_list|,
name|appContext
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
literal|"history"
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
name|assertResponseStatusCode
argument_list|(
name|Status
operator|.
name|NOT_FOUND
argument_list|,
name|response
operator|.
name|getStatusInfo
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
literal|"invalid"
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
name|assertResponseStatusCode
argument_list|(
name|Status
operator|.
name|NOT_FOUND
argument_list|,
name|response
operator|.
name|getStatusInfo
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
literal|"history"
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
name|assertResponseStatusCode
argument_list|(
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|,
name|response
operator|.
name|getStatusInfo
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
DECL|method|verifyHsInfoGeneric (String hadoopVersionBuiltOn, String hadoopBuildVersion, String hadoopVersion, long startedon)
specifier|public
name|void
name|verifyHsInfoGeneric
parameter_list|(
name|String
name|hadoopVersionBuiltOn
parameter_list|,
name|String
name|hadoopBuildVersion
parameter_list|,
name|String
name|hadoopVersion
parameter_list|,
name|long
name|startedon
parameter_list|)
block|{
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
name|assertEquals
argument_list|(
literal|"startedOn doesn't match: "
argument_list|,
name|JobHistoryServer
operator|.
name|historyServerTimeStamp
argument_list|,
name|startedon
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyHSInfo (JSONObject info, AppContext ctx)
specifier|public
name|void
name|verifyHSInfo
parameter_list|(
name|JSONObject
name|info
parameter_list|,
name|AppContext
name|ctx
parameter_list|)
throws|throws
name|JSONException
block|{
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|4
argument_list|,
name|info
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|verifyHsInfoGeneric
argument_list|(
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
name|getLong
argument_list|(
literal|"startedOn"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyHSInfoXML (String xml, AppContext ctx)
specifier|public
name|void
name|verifyHSInfoXML
parameter_list|(
name|String
name|xml
parameter_list|,
name|AppContext
name|ctx
parameter_list|)
throws|throws
name|JSONException
throws|,
name|Exception
block|{
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
literal|"historyInfo"
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
name|verifyHsInfoGeneric
argument_list|(
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
name|getXmlLong
argument_list|(
name|element
argument_list|,
literal|"startedOn"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

