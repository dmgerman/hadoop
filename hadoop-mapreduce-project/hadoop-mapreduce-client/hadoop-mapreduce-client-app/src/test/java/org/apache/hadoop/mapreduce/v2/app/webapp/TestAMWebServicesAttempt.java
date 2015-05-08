begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp
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
name|app
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
name|Enumeration
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
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|api
operator|.
name|records
operator|.
name|TaskAttemptState
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
name|app
operator|.
name|MockAppContext
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
name|job
operator|.
name|Job
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
name|job
operator|.
name|Task
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
name|job
operator|.
name|TaskAttempt
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
name|util
operator|.
name|MRApps
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
name|authentication
operator|.
name|server
operator|.
name|AuthenticationFilter
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
name|authentication
operator|.
name|server
operator|.
name|PseudoAuthenticationHandler
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
name|Singleton
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
comment|/**  * Test the app master web service Rest API for getting task attempts, a  * specific task attempt, and task attempt counters  *  * /ws/v1/mapreduce/jobs/{jobid}/tasks/{taskid}/attempts/{attemptid}/state  */
end_comment

begin_class
DECL|class|TestAMWebServicesAttempt
specifier|public
class|class
name|TestAMWebServicesAttempt
extends|extends
name|JerseyTest
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
name|AppContext
name|appContext
decl_stmt|;
DECL|field|webserviceUserName
specifier|private
name|String
name|webserviceUserName
init|=
literal|"testuser"
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
name|appContext
operator|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
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
name|AMWebServices
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
name|filter
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|through
argument_list|(
name|TestRMCustomAuthFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
annotation|@
name|Singleton
DECL|class|TestRMCustomAuthFilter
specifier|public
specifier|static
class|class
name|TestRMCustomAuthFilter
extends|extends
name|AuthenticationFilter
block|{
annotation|@
name|Override
DECL|method|getConfiguration (String configPrefix, FilterConfig filterConfig)
specifier|protected
name|Properties
name|getConfiguration
parameter_list|(
name|String
name|configPrefix
parameter_list|,
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|ServletException
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|names
init|=
name|filterConfig
operator|.
name|getInitParameterNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|configPrefix
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|configPrefix
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|props
operator|.
name|put
argument_list|(
name|AuthenticationFilter
operator|.
name|AUTH_TYPE
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
return|return
name|props
return|;
block|}
block|}
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
block|}
DECL|method|TestAMWebServicesAttempt ()
specifier|public
name|TestAMWebServicesAttempt
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|WebAppDescriptor
operator|.
name|Builder
argument_list|(
literal|"org.apache.hadoop.mapreduce.v2.app.webapp"
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
DECL|method|testGetTaskAttemptIdState ()
specifier|public
name|void
name|testGetTaskAttemptIdState
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
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|jobsMap
init|=
name|appContext
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
for|for
control|(
name|JobId
name|id
range|:
name|jobsMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|jobId
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|id
argument_list|)
decl_stmt|;
for|for
control|(
name|Task
name|task
range|:
name|jobsMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|tid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|att
range|:
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskAttemptId
name|attemptid
init|=
name|att
operator|.
name|getID
argument_list|()
decl_stmt|;
name|String
name|attid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|attemptid
argument_list|)
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
literal|"mapreduce"
argument_list|)
operator|.
name|path
argument_list|(
literal|"jobs"
argument_list|)
operator|.
name|path
argument_list|(
name|jobId
argument_list|)
operator|.
name|path
argument_list|(
literal|"tasks"
argument_list|)
operator|.
name|path
argument_list|(
name|tid
argument_list|)
operator|.
name|path
argument_list|(
literal|"attempts"
argument_list|)
operator|.
name|path
argument_list|(
name|attid
argument_list|)
operator|.
name|path
argument_list|(
literal|"state"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|webserviceUserName
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
name|assertEquals
argument_list|(
name|att
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|json
operator|.
name|get
argument_list|(
literal|"state"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGetTaskAttemptIdXMLState ()
specifier|public
name|void
name|testGetTaskAttemptIdXMLState
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
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|jobsMap
init|=
name|appContext
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
for|for
control|(
name|JobId
name|id
range|:
name|jobsMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|jobId
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|id
argument_list|)
decl_stmt|;
for|for
control|(
name|Task
name|task
range|:
name|jobsMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|tid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|att
range|:
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskAttemptId
name|attemptid
init|=
name|att
operator|.
name|getID
argument_list|()
decl_stmt|;
name|String
name|attid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|attemptid
argument_list|)
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
literal|"mapreduce"
argument_list|)
operator|.
name|path
argument_list|(
literal|"jobs"
argument_list|)
operator|.
name|path
argument_list|(
name|jobId
argument_list|)
operator|.
name|path
argument_list|(
literal|"tasks"
argument_list|)
operator|.
name|path
argument_list|(
name|tid
argument_list|)
operator|.
name|path
argument_list|(
literal|"attempts"
argument_list|)
operator|.
name|path
argument_list|(
name|attid
argument_list|)
operator|.
name|path
argument_list|(
literal|"state"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|webserviceUserName
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
literal|"jobTaskAttemptState"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodes
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|state
init|=
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
operator|(
name|Element
operator|)
name|nodes
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"state"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|att
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPutTaskAttemptIdState ()
specifier|public
name|void
name|testPutTaskAttemptIdState
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
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|jobsMap
init|=
name|appContext
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
for|for
control|(
name|JobId
name|id
range|:
name|jobsMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|jobId
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|id
argument_list|)
decl_stmt|;
for|for
control|(
name|Task
name|task
range|:
name|jobsMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|tid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|att
range|:
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskAttemptId
name|attemptid
init|=
name|att
operator|.
name|getID
argument_list|()
decl_stmt|;
name|String
name|attid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|attemptid
argument_list|)
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
literal|"mapreduce"
argument_list|)
operator|.
name|path
argument_list|(
literal|"jobs"
argument_list|)
operator|.
name|path
argument_list|(
name|jobId
argument_list|)
operator|.
name|path
argument_list|(
literal|"tasks"
argument_list|)
operator|.
name|path
argument_list|(
name|tid
argument_list|)
operator|.
name|path
argument_list|(
literal|"attempts"
argument_list|)
operator|.
name|path
argument_list|(
name|attid
argument_list|)
operator|.
name|path
argument_list|(
literal|"state"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|webserviceUserName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|put
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
literal|"{\"state\":\"KILLED\"}"
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
name|assertEquals
argument_list|(
name|TaskAttemptState
operator|.
name|KILLED
operator|.
name|toString
argument_list|()
argument_list|,
name|json
operator|.
name|get
argument_list|(
literal|"state"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPutTaskAttemptIdXMLState ()
specifier|public
name|void
name|testPutTaskAttemptIdXMLState
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
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|jobsMap
init|=
name|appContext
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
for|for
control|(
name|JobId
name|id
range|:
name|jobsMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|jobId
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|id
argument_list|)
decl_stmt|;
for|for
control|(
name|Task
name|task
range|:
name|jobsMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|tid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|att
range|:
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskAttemptId
name|attemptid
init|=
name|att
operator|.
name|getID
argument_list|()
decl_stmt|;
name|String
name|attid
init|=
name|MRApps
operator|.
name|toString
argument_list|(
name|attemptid
argument_list|)
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
literal|"mapreduce"
argument_list|)
operator|.
name|path
argument_list|(
literal|"jobs"
argument_list|)
operator|.
name|path
argument_list|(
name|jobId
argument_list|)
operator|.
name|path
argument_list|(
literal|"tasks"
argument_list|)
operator|.
name|path
argument_list|(
name|tid
argument_list|)
operator|.
name|path
argument_list|(
literal|"attempts"
argument_list|)
operator|.
name|path
argument_list|(
name|attid
argument_list|)
operator|.
name|path
argument_list|(
literal|"state"
argument_list|)
operator|.
name|queryParam
argument_list|(
literal|"user.name"
argument_list|,
name|webserviceUserName
argument_list|)
operator|.
name|accept
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML_TYPE
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML_TYPE
argument_list|)
operator|.
name|put
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|,
literal|"<jobTaskAttemptState><state>KILLED"
operator|+
literal|"</state></jobTaskAttemptState>"
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
literal|"jobTaskAttemptState"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodes
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|state
init|=
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
operator|(
name|Element
operator|)
name|nodes
operator|.
name|item
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"state"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TaskAttemptState
operator|.
name|KILLED
operator|.
name|toString
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

