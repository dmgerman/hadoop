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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Map
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
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|MRJobConfig
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
name|MockJobs
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
name|yarn
operator|.
name|Clock
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
name|ClusterInfo
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
name|ApplicationAttemptId
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
name|event
operator|.
name|EventHandler
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
name|common
operator|.
name|collect
operator|.
name|Maps
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
comment|/**  * Test the app master web service Rest API for getting the job conf. This  * requires created a temporary configuration file.  *  *   /ws/v1/mapreduce/job/{jobid}/conf  */
end_comment

begin_class
DECL|class|TestAMWebServicesJobConf
specifier|public
class|class
name|TestAMWebServicesJobConf
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
DECL|field|testConfDir
specifier|private
specifier|static
name|File
name|testConfDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestAMWebServicesJobConf
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"confDir"
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
name|Path
name|confPath
init|=
operator|new
name|Path
argument_list|(
name|testConfDir
operator|.
name|toString
argument_list|()
argument_list|,
name|MRJobConfig
operator|.
name|JOB_CONF_FILE
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|localFs
decl_stmt|;
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|confPath
operator|=
name|localFs
operator|.
name|makeQualified
argument_list|(
name|confPath
argument_list|)
expr_stmt|;
name|OutputStream
name|out
init|=
name|localFs
operator|.
name|create
argument_list|(
name|confPath
argument_list|)
decl_stmt|;
try|try
block|{
name|conf
operator|.
name|writeXml
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|localFs
operator|.
name|exists
argument_list|(
name|confPath
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"error creating config file: "
operator|+
name|confPath
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"error creating config file: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|appContext
operator|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
name|confPath
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
name|testConfDir
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
name|testConfDir
argument_list|)
expr_stmt|;
block|}
DECL|method|TestAMWebServicesJobConf ()
specifier|public
name|TestAMWebServicesJobConf
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
DECL|method|testJobConf ()
specifier|public
name|void
name|testJobConf
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
literal|"conf"
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
name|JSONObject
name|info
init|=
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"conf"
argument_list|)
decl_stmt|;
name|verifyAMJobConf
argument_list|(
name|info
argument_list|,
name|jobsMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testJobConfSlash ()
specifier|public
name|void
name|testJobConfSlash
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
literal|"conf/"
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
name|JSONObject
name|info
init|=
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"conf"
argument_list|)
decl_stmt|;
name|verifyAMJobConf
argument_list|(
name|info
argument_list|,
name|jobsMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testJobConfDefault ()
specifier|public
name|void
name|testJobConfDefault
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
literal|"conf"
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
name|JSONObject
name|info
init|=
name|json
operator|.
name|getJSONObject
argument_list|(
literal|"conf"
argument_list|)
decl_stmt|;
name|verifyAMJobConf
argument_list|(
name|info
argument_list|,
name|jobsMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testJobConfXML ()
specifier|public
name|void
name|testJobConfXML
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
literal|"conf"
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
name|info
init|=
name|dom
operator|.
name|getElementsByTagName
argument_list|(
literal|"conf"
argument_list|)
decl_stmt|;
name|verifyAMJobConfXML
argument_list|(
name|info
argument_list|,
name|jobsMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyAMJobConf (JSONObject info, Job job)
specifier|public
name|void
name|verifyAMJobConf
parameter_list|(
name|JSONObject
name|info
parameter_list|,
name|Job
name|job
parameter_list|)
throws|throws
name|JSONException
block|{
name|assertEquals
argument_list|(
literal|"incorrect number of elements"
argument_list|,
literal|2
argument_list|,
name|info
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"path"
argument_list|,
name|job
operator|.
name|getConfFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|info
operator|.
name|getString
argument_list|(
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
comment|// just do simple verification of fields - not data is correct
comment|// in the fields
name|JSONArray
name|properties
init|=
name|info
operator|.
name|getJSONArray
argument_list|(
literal|"property"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|properties
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|JSONObject
name|prop
init|=
name|properties
operator|.
name|getJSONObject
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|prop
operator|.
name|getString
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|prop
operator|.
name|getString
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"name not set"
argument_list|,
operator|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"value not set"
argument_list|,
operator|(
name|value
operator|!=
literal|null
operator|&&
operator|!
name|value
operator|.
name|isEmpty
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyAMJobConfXML (NodeList nodes, Job job)
specifier|public
name|void
name|verifyAMJobConfXML
parameter_list|(
name|NodeList
name|nodes
parameter_list|,
name|Job
name|job
parameter_list|)
block|{
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
name|WebServicesTestUtils
operator|.
name|checkStringMatch
argument_list|(
literal|"path"
argument_list|,
name|job
operator|.
name|getConfFile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|element
argument_list|,
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
comment|// just do simple verification of fields - not data is correct
comment|// in the fields
name|NodeList
name|properties
init|=
name|element
operator|.
name|getElementsByTagName
argument_list|(
literal|"property"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|properties
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|Element
name|property
init|=
operator|(
name|Element
operator|)
name|properties
operator|.
name|item
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"should have counters in the web service info"
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|property
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|WebServicesTestUtils
operator|.
name|getXmlString
argument_list|(
name|property
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"name not set"
argument_list|,
operator|(
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"name not set"
argument_list|,
operator|(
name|value
operator|!=
literal|null
operator|&&
operator|!
name|value
operator|.
name|isEmpty
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

