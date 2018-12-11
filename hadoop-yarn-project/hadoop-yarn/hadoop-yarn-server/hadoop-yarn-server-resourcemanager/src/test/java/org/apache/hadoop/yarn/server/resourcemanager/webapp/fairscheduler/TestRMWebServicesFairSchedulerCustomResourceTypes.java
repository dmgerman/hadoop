begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.fairscheduler
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
operator|.
name|fairscheduler
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|fair
operator|.
name|FSLeafQueue
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
name|fair
operator|.
name|FairScheduler
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
name|fair
operator|.
name|QueueManager
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|helper
operator|.
name|*
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
name|JSONArray
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
name|After
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
name|Element
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
comment|/**  * This class is to test response representations of queue resources,  * explicitly setting custom resource types. with the help of  * {@link CustomResourceTypesConfigurationProvider}  */
end_comment

begin_class
DECL|class|TestRMWebServicesFairSchedulerCustomResourceTypes
specifier|public
class|class
name|TestRMWebServicesFairSchedulerCustomResourceTypes
extends|extends
name|JerseyTestBase
block|{
DECL|field|rm
specifier|private
specifier|static
name|MockRM
name|rm
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|YarnConfiguration
name|conf
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
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairScheduler
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
DECL|method|initResourceTypes (YarnConfiguration conf)
specifier|private
name|void
name|initResourceTypes
parameter_list|(
name|YarnConfiguration
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
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|ResourceUtils
operator|.
name|resetResourceTypes
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
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
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|CustomResourceTypesConfigurationProvider
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|TestRMWebServicesFairSchedulerCustomResourceTypes ()
specifier|public
name|TestRMWebServicesFairSchedulerCustomResourceTypes
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
DECL|method|testClusterSchedulerWithCustomResourceTypesJson ()
specifier|public
name|void
name|testClusterSchedulerWithCustomResourceTypesJson
parameter_list|()
block|{
name|FairScheduler
name|scheduler
init|=
operator|(
name|FairScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|QueueManager
name|queueManager
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
decl_stmt|;
comment|// create LeafQueues
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSLeafQueue
name|subqueue1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue1"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|incrementUsedResourcesOnQueue
argument_list|(
name|subqueue1
argument_list|,
literal|33L
argument_list|)
expr_stmt|;
name|WebResource
name|path
init|=
name|resource
argument_list|()
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
literal|"scheduler"
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
name|verifyJsonResponse
argument_list|(
name|path
argument_list|,
name|response
argument_list|,
name|CustomResourceTypesConfigurationProvider
operator|.
name|getCustomResourceTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClusterSchedulerWithCustomResourceTypesXml ()
specifier|public
name|void
name|testClusterSchedulerWithCustomResourceTypesXml
parameter_list|()
block|{
name|FairScheduler
name|scheduler
init|=
operator|(
name|FairScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|QueueManager
name|queueManager
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
decl_stmt|;
comment|// create LeafQueues
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSLeafQueue
name|subqueue1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue1"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|incrementUsedResourcesOnQueue
argument_list|(
name|subqueue1
argument_list|,
literal|33L
argument_list|)
expr_stmt|;
name|WebResource
name|path
init|=
name|resource
argument_list|()
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
literal|"scheduler"
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
name|verifyXmlResponse
argument_list|(
name|path
argument_list|,
name|response
argument_list|,
name|CustomResourceTypesConfigurationProvider
operator|.
name|getCustomResourceTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClusterSchedulerWithElevenCustomResourceTypesXml ()
specifier|public
name|void
name|testClusterSchedulerWithElevenCustomResourceTypesXml
parameter_list|()
block|{
name|CustomResourceTypesConfigurationProvider
operator|.
name|setResourceTypes
argument_list|(
literal|11
argument_list|,
literal|"k"
argument_list|)
expr_stmt|;
name|createInjectorForWebServletModule
argument_list|()
expr_stmt|;
name|FairScheduler
name|scheduler
init|=
operator|(
name|FairScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|QueueManager
name|queueManager
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
decl_stmt|;
comment|// create LeafQueues
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSLeafQueue
name|subqueue1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue1"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|incrementUsedResourcesOnQueue
argument_list|(
name|subqueue1
argument_list|,
literal|33L
argument_list|)
expr_stmt|;
name|WebResource
name|path
init|=
name|resource
argument_list|()
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
literal|"scheduler"
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
name|verifyXmlResponse
argument_list|(
name|path
argument_list|,
name|response
argument_list|,
name|CustomResourceTypesConfigurationProvider
operator|.
name|getCustomResourceTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClusterSchedulerElevenWithCustomResourceTypesJson ()
specifier|public
name|void
name|testClusterSchedulerElevenWithCustomResourceTypesJson
parameter_list|()
block|{
name|CustomResourceTypesConfigurationProvider
operator|.
name|setResourceTypes
argument_list|(
literal|11
argument_list|,
literal|"k"
argument_list|)
expr_stmt|;
name|createInjectorForWebServletModule
argument_list|()
expr_stmt|;
name|FairScheduler
name|scheduler
init|=
operator|(
name|FairScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|QueueManager
name|queueManager
init|=
name|scheduler
operator|.
name|getQueueManager
argument_list|()
decl_stmt|;
comment|// create LeafQueues
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSLeafQueue
name|subqueue1
init|=
name|queueManager
operator|.
name|getLeafQueue
argument_list|(
literal|"root.q.subqueue1"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|incrementUsedResourcesOnQueue
argument_list|(
name|subqueue1
argument_list|,
literal|33L
argument_list|)
expr_stmt|;
name|WebResource
name|path
init|=
name|resource
argument_list|()
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
literal|"scheduler"
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
name|verifyJsonResponse
argument_list|(
name|path
argument_list|,
name|response
argument_list|,
name|CustomResourceTypesConfigurationProvider
operator|.
name|getCustomResourceTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyJsonResponse (WebResource path, ClientResponse response, List<String> customResourceTypes)
specifier|private
name|void
name|verifyJsonResponse
parameter_list|(
name|WebResource
name|path
parameter_list|,
name|ClientResponse
name|response
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|customResourceTypes
parameter_list|)
block|{
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
name|JSONArray
name|queues
init|=
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"scheduler"
argument_list|)
operator|.
name|getJSONObject
argument_list|(
literal|"schedulerInfo"
argument_list|)
operator|.
name|getJSONObject
argument_list|(
literal|"rootQueue"
argument_list|)
operator|.
name|getJSONObject
argument_list|(
literal|"childQueues"
argument_list|)
operator|.
name|getJSONArray
argument_list|(
literal|"queue"
argument_list|)
decl_stmt|;
comment|// childQueueInfo consists of subqueue1 and subqueue2 info
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|queues
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|firstChildQueue
init|=
name|queues
operator|.
name|getJSONObject
argument_list|(
literal|0
argument_list|)
decl_stmt|;
operator|new
name|FairSchedulerJsonVerifications
argument_list|(
name|customResourceTypes
argument_list|)
operator|.
name|verify
argument_list|(
name|firstChildQueue
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
block|}
DECL|method|verifyXmlResponse (WebResource path, ClientResponse response, List<String> customResourceTypes)
specifier|private
name|void
name|verifyXmlResponse
parameter_list|(
name|WebResource
name|path
parameter_list|,
name|ClientResponse
name|response
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|customResourceTypes
parameter_list|)
block|{
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
name|xml
lambda|->
block|{
name|Element
name|scheduler
init|=
operator|(
name|Element
operator|)
name|xml
operator|.
name|getElementsByTagName
argument_list|(
literal|"scheduler"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Element
name|schedulerInfo
init|=
operator|(
name|Element
operator|)
name|scheduler
operator|.
name|getElementsByTagName
argument_list|(
literal|"schedulerInfo"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Element
name|rootQueue
init|=
operator|(
name|Element
operator|)
name|schedulerInfo
operator|.
name|getElementsByTagName
argument_list|(
literal|"rootQueue"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Element
name|childQueues
init|=
operator|(
name|Element
operator|)
name|rootQueue
operator|.
name|getElementsByTagName
argument_list|(
literal|"childQueues"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Element
name|queue
init|=
operator|(
name|Element
operator|)
name|childQueues
operator|.
name|getElementsByTagName
argument_list|(
literal|"queue"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
operator|new
name|FairSchedulerXmlVerifications
argument_list|(
name|customResourceTypes
argument_list|)
operator|.
name|verify
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementUsedResourcesOnQueue (final FSLeafQueue queue, final long value)
specifier|private
name|void
name|incrementUsedResourcesOnQueue
parameter_list|(
specifier|final
name|FSLeafQueue
name|queue
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
try|try
block|{
name|Method
name|incUsedResourceMethod
init|=
name|queue
operator|.
name|getClass
argument_list|()
operator|.
name|getSuperclass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"incUsedResource"
argument_list|,
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
name|incUsedResourceMethod
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|customResources
init|=
name|CustomResourceTypesConfigurationProvider
operator|.
name|getCustomResourceTypes
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|v
lambda|->
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|incUsedResourceMethod
operator|.
name|invoke
argument_list|(
name|queue
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|20
argument_list|,
literal|30
argument_list|,
name|customResources
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
end_class

end_unit

