begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
package|;
end_package

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
name|security
operator|.
name|http
operator|.
name|RestCsrfPreventionFilter
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
name|scheduler
operator|.
name|ResourceScheduler
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
name|scheduler
operator|.
name|fifo
operator|.
name|FifoScheduler
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
name|webapp
operator|.
name|JAXBContextResolver
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
name|webapp
operator|.
name|RMWebServices
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
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|Map
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Used TestRMWebServices as an example of web invocations of RM and added  * test for CSRF Filter.  */
end_comment

begin_class
DECL|class|TestRMWithCSRFFilter
specifier|public
class|class
name|TestRMWithCSRFFilter
extends|extends
name|JerseyTestBase
block|{
DECL|field|rm
specifier|private
specifier|static
name|MockRM
name|rm
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
name|bind
argument_list|(
name|JAXBContextResolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RMWebServices
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
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FifoScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|rm
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
name|RestCsrfPreventionFilter
name|csrfFilter
init|=
operator|new
name|RestCsrfPreventionFilter
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// adding GET as protected method to make things a little easier...
name|initParams
operator|.
name|put
argument_list|(
name|RestCsrfPreventionFilter
operator|.
name|CUSTOM_METHODS_TO_IGNORE_PARAM
argument_list|,
literal|"OPTIONS,HEAD,TRACE"
argument_list|)
expr_stmt|;
name|filter
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|through
argument_list|(
name|csrfFilter
argument_list|,
name|initParams
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
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
DECL|method|TestRMWithCSRFFilter ()
specifier|public
name|TestRMWithCSRFFilter
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|WebAppDescriptor
operator|.
name|Builder
argument_list|(
literal|"org.apache.hadoop.yarn.server.resourcemanager.webapp"
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
DECL|method|testNoCustomHeaderFromBrowser ()
specifier|public
name|void
name|testNoCustomHeaderFromBrowser
parameter_list|()
throws|throws
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
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/xml"
argument_list|)
operator|.
name|header
argument_list|(
name|RestCsrfPreventionFilter
operator|.
name|HEADER_USER_AGENT
argument_list|,
literal|"Mozilla/5.0"
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have been rejected"
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|BAD_REQUEST
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIncludeCustomHeaderFromBrowser ()
specifier|public
name|void
name|testIncludeCustomHeaderFromBrowser
parameter_list|()
throws|throws
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
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/xml"
argument_list|)
operator|.
name|header
argument_list|(
name|RestCsrfPreventionFilter
operator|.
name|HEADER_USER_AGENT
argument_list|,
literal|"Mozilla/5.0"
argument_list|)
operator|.
name|header
argument_list|(
literal|"X-XSRF-HEADER"
argument_list|,
literal|""
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have been accepted"
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|OK
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML_TYPE
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
name|verifyClusterInfoXML
argument_list|(
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllowedMethod ()
specifier|public
name|void
name|testAllowedMethod
parameter_list|()
throws|throws
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
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/xml"
argument_list|)
operator|.
name|header
argument_list|(
name|RestCsrfPreventionFilter
operator|.
name|HEADER_USER_AGENT
argument_list|,
literal|"Mozilla/5.0"
argument_list|)
operator|.
name|head
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have been allowed"
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|OK
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllowNonBrowserInteractionWithoutHeader ()
specifier|public
name|void
name|testAllowNonBrowserInteractionWithoutHeader
parameter_list|()
throws|throws
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
literal|"cluster"
argument_list|)
operator|.
name|path
argument_list|(
literal|"info"
argument_list|)
operator|.
name|accept
argument_list|(
literal|"application/xml"
argument_list|)
operator|.
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have been accepted"
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|OK
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML_TYPE
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
name|verifyClusterInfoXML
argument_list|(
name|xml
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyClusterInfoXML (String xml)
specifier|public
name|void
name|verifyClusterInfoXML
parameter_list|(
name|String
name|xml
parameter_list|)
throws|throws
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
literal|"clusterInfo"
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
name|verifyClusterGeneric
argument_list|(
name|WebServicesTestUtils
operator|.
name|getXmlLong
argument_list|(
name|element
argument_list|,
literal|"id"
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
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"state"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"haState"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"haZooKeeperConnectionState"
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
literal|"resourceManagerVersionBuiltOn"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"resourceManagerBuildVersion"
argument_list|)
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"resourceManagerVersion"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyClusterGeneric (long clusterid, long startedon, String state, String haState, String haZooKeeperConnectionState, String hadoopVersionBuiltOn, String hadoopBuildVersion, String hadoopVersion, String resourceManagerVersionBuiltOn, String resourceManagerBuildVersion, String resourceManagerVersion)
specifier|public
name|void
name|verifyClusterGeneric
parameter_list|(
name|long
name|clusterid
parameter_list|,
name|long
name|startedon
parameter_list|,
name|String
name|state
parameter_list|,
name|String
name|haState
parameter_list|,
name|String
name|haZooKeeperConnectionState
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
name|assertEquals
argument_list|(
literal|"clusterId doesn't match: "
argument_list|,
name|ResourceManager
operator|.
name|getClusterTimeStamp
argument_list|()
argument_list|,
name|clusterid
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"startedOn doesn't match: "
argument_list|,
name|ResourceManager
operator|.
name|getClusterTimeStamp
argument_list|()
argument_list|,
name|startedon
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"stated doesn't match: "
operator|+
name|state
argument_list|,
name|state
operator|.
name|matches
argument_list|(
name|STATE
operator|.
name|INITED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"HA state doesn't match: "
operator|+
name|haState
argument_list|,
name|haState
operator|.
name|matches
argument_list|(
literal|"INITIALIZING"
argument_list|)
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

