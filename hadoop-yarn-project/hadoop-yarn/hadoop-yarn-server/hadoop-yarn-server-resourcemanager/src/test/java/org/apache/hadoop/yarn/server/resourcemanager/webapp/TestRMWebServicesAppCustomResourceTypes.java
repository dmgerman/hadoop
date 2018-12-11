begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|resourcemanager
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
name|MockAM
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
name|MockNM
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
name|rmapp
operator|.
name|RMApp
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
name|util
operator|.
name|resource
operator|.
name|CustomResourceTypesConfigurationProvider
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
name|helper
operator|.
name|BufferedClientResponse
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
name|helper
operator|.
name|JsonCustomResourceTypeTestcase
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
name|helper
operator|.
name|XmlCustomResourceTypeTestCase
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
name|resource
operator|.
name|ResourceUtils
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
name|NodeList
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|TestRMWebServicesCustomResourceTypesCommons
operator|.
name|verifyAppInfoJson
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|TestRMWebServicesCustomResourceTypesCommons
operator|.
name|verifyAppsXML
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

begin_comment
comment|/**  * This test verifies that custom resource types are correctly serialized to XML  * and JSON when HTTP GET request is sent to the resource: ws/v1/cluster/apps.  */
end_comment

begin_class
DECL|class|TestRMWebServicesAppCustomResourceTypes
specifier|public
class|class
name|TestRMWebServicesAppCustomResourceTypes
extends|extends
name|JerseyTestBase
block|{
DECL|field|rm
specifier|private
specifier|static
name|MockRM
name|rm
decl_stmt|;
DECL|field|CONTAINER_MB
specifier|private
specifier|static
specifier|final
name|int
name|CONTAINER_MB
init|=
literal|1024
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
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_MAX_ATTEMPTS
argument_list|)
expr_stmt|;
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
name|initResourceTypes
argument_list|(
name|conf
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
block|}
DECL|method|initResourceTypes (Configuration conf)
specifier|private
name|void
name|initResourceTypes
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CONFIGURATION_PROVIDER_CLASS
argument_list|,
name|CustomResourceTypesConfigurationProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ResourceUtils
operator|.
name|resetResourceTypes
argument_list|(
name|conf
argument_list|)
expr_stmt|;
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
name|createInjectorForWebServletModule
argument_list|()
expr_stmt|;
block|}
DECL|method|createInjectorForWebServletModule ()
specifier|private
name|void
name|createInjectorForWebServletModule
parameter_list|()
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
DECL|method|TestRMWebServicesAppCustomResourceTypes ()
specifier|public
name|TestRMWebServicesAppCustomResourceTypes
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
DECL|method|getWebResourcePathForApp (RMApp app1, WebResource r)
specifier|private
name|WebResource
name|getWebResourcePathForApp
parameter_list|(
name|RMApp
name|app1
parameter_list|,
name|WebResource
name|r
parameter_list|)
block|{
return|return
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
literal|"apps"
argument_list|)
operator|.
name|path
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|app1
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testRunningAppXml ()
specifier|public
name|void
name|testRunningAppXml
parameter_list|()
throws|throws
name|Exception
block|{
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|amNodeManager
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
name|CONTAINER_MB
argument_list|,
literal|"testwordcount"
argument_list|,
literal|"user1"
argument_list|)
decl_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|amNodeManager
argument_list|)
decl_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|2048
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|amNodeManager
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|WebResource
name|path
init|=
name|getWebResourcePathForApp
argument_list|(
name|app1
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|ClientResponse
name|response
init|=
name|path
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
name|XmlCustomResourceTypeTestCase
name|testCase
init|=
operator|new
name|XmlCustomResourceTypeTestCase
argument_list|(
name|path
argument_list|,
operator|new
name|BufferedClientResponse
argument_list|(
name|response
argument_list|)
argument_list|)
decl_stmt|;
name|testCase
operator|.
name|verify
argument_list|(
name|document
lambda|->
block|{
name|NodeList
name|appArray
init|=
name|document
operator|.
name|getElementsByTagName
argument_list|(
literal|"app"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"incorrect number of app elements"
argument_list|,
literal|1
argument_list|,
name|appArray
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|verifyAppsXML
argument_list|(
name|appArray
argument_list|,
name|app1
argument_list|,
name|rm
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRunningAppJson ()
specifier|public
name|void
name|testRunningAppJson
parameter_list|()
throws|throws
name|Exception
block|{
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|MockNM
name|amNodeManager
init|=
name|rm
operator|.
name|registerNode
argument_list|(
literal|"127.0.0.1:1234"
argument_list|,
literal|2048
argument_list|)
decl_stmt|;
name|RMApp
name|app1
init|=
name|rm
operator|.
name|submitApp
argument_list|(
name|CONTAINER_MB
argument_list|,
literal|"testwordcount"
argument_list|,
literal|"user1"
argument_list|)
decl_stmt|;
name|MockAM
name|am1
init|=
name|MockRM
operator|.
name|launchAndRegisterAM
argument_list|(
name|app1
argument_list|,
name|rm
argument_list|,
name|amNodeManager
argument_list|)
decl_stmt|;
name|am1
operator|.
name|allocate
argument_list|(
literal|"*"
argument_list|,
literal|2048
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|amNodeManager
operator|.
name|nodeHeartbeat
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|WebResource
name|r
init|=
name|resource
argument_list|()
decl_stmt|;
name|WebResource
name|path
init|=
name|getWebResourcePathForApp
argument_list|(
name|app1
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|ClientResponse
name|response
init|=
name|path
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
name|JsonCustomResourceTypeTestcase
name|testCase
init|=
operator|new
name|JsonCustomResourceTypeTestcase
argument_list|(
name|path
argument_list|,
operator|new
name|BufferedClientResponse
argument_list|(
name|response
argument_list|)
argument_list|)
decl_stmt|;
name|testCase
operator|.
name|verify
argument_list|(
name|json
lambda|->
block|{
try|try
block|{
name|assertEquals
argument_list|(
literal|"incorrect number of app elements"
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
name|app
init|=
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"app"
argument_list|)
decl_stmt|;
name|verifyAppInfoJson
argument_list|(
name|app
argument_list|,
name|app1
argument_list|,
name|rm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JSONException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

