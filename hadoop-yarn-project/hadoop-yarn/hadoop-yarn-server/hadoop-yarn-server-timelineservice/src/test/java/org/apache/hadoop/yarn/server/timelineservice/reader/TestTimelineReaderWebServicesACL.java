begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader
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
name|timelineservice
operator|.
name|reader
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
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
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
name|URI
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|timelineservice
operator|.
name|storage
operator|.
name|FileSystemTimelineReaderImpl
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
name|timelineservice
operator|.
name|storage
operator|.
name|TestFileSystemTimelineReaderImpl
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
name|timelineservice
operator|.
name|storage
operator|.
name|TimelineReader
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
name|YarnJacksonJaxbJsonProvider
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
name|AfterClass
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
name|Client
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
name|config
operator|.
name|ClientConfig
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
name|config
operator|.
name|DefaultClientConfig
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
name|client
operator|.
name|urlconnection
operator|.
name|HttpURLConnectionFactory
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
name|client
operator|.
name|urlconnection
operator|.
name|URLConnectionClientHandler
import|;
end_import

begin_comment
comment|/**  * Tests ACL check while retrieving entity-types per application.  */
end_comment

begin_class
DECL|class|TestTimelineReaderWebServicesACL
specifier|public
class|class
name|TestTimelineReaderWebServicesACL
block|{
DECL|field|ROOT_DIR
specifier|private
specifier|static
specifier|final
name|String
name|ROOT_DIR
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestTimelineReaderWebServicesACL
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|serverPort
specifier|private
name|int
name|serverPort
decl_stmt|;
DECL|field|server
specifier|private
name|TimelineReaderServer
name|server
decl_stmt|;
DECL|field|ADMIN
specifier|private
specifier|static
specifier|final
name|String
name|ADMIN
init|=
literal|"yarn"
decl_stmt|;
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
name|TestFileSystemTimelineReaderImpl
operator|.
name|initializeDataDirectory
argument_list|(
name|ROOT_DIR
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
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|ROOT_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFloat
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_VERSION
argument_list|,
literal|2.0f
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_READER_WEBAPP_ADDRESS
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
literal|"cluster1"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_READER_CLASS
argument_list|,
name|FileSystemTimelineReaderImpl
operator|.
name|class
argument_list|,
name|TimelineReader
operator|.
name|class
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|FileSystemTimelineReaderImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
argument_list|,
name|ROOT_DIR
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|FILTER_ENTITY_LIST_BY_USER
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ADMIN_ACL
argument_list|,
name|ADMIN
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|TimelineReaderServer
argument_list|()
expr_stmt|;
name|server
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|serverPort
operator|=
name|server
operator|.
name|getWebServerPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Web server failed to start"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|server
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|verifyHttpResponse (Client client, URI uri, Status expectedStatus)
specifier|private
specifier|static
name|ClientResponse
name|verifyHttpResponse
parameter_list|(
name|Client
name|client
parameter_list|,
name|URI
name|uri
parameter_list|,
name|Status
name|expectedStatus
parameter_list|)
block|{
name|ClientResponse
name|resp
init|=
name|client
operator|.
name|resource
argument_list|(
name|uri
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
name|get
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|resp
operator|.
name|getStatusInfo
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|expectedStatus
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
DECL|method|createClient ()
specifier|private
specifier|static
name|Client
name|createClient
parameter_list|()
block|{
name|ClientConfig
name|cfg
init|=
operator|new
name|DefaultClientConfig
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|getClasses
argument_list|()
operator|.
name|add
argument_list|(
name|YarnJacksonJaxbJsonProvider
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
operator|new
name|Client
argument_list|(
operator|new
name|URLConnectionClientHandler
argument_list|(
operator|new
name|DummyURLConnectionFactory
argument_list|()
argument_list|)
argument_list|,
name|cfg
argument_list|)
return|;
block|}
DECL|class|DummyURLConnectionFactory
specifier|private
specifier|static
class|class
name|DummyURLConnectionFactory
implements|implements
name|HttpURLConnectionFactory
block|{
annotation|@
name|Override
DECL|method|getHttpURLConnection (final URL url)
specifier|public
name|HttpURLConnection
name|getHttpURLConnection
parameter_list|(
specifier|final
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGetEntityTypes ()
specifier|public
name|void
name|testGetEntityTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Client
name|client
init|=
name|createClient
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|unAuthorizedUser
init|=
literal|"user2"
decl_stmt|;
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"http://localhost:"
operator|+
name|serverPort
operator|+
literal|"/ws/v2/"
operator|+
literal|"timeline/apps/app1/entity-types?user.name="
operator|+
name|unAuthorizedUser
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|"User "
operator|+
name|unAuthorizedUser
operator|+
literal|" is not allowed to read TimelineService V2 data."
decl_stmt|;
name|ClientResponse
name|resp
init|=
name|verifyHttpResponse
argument_list|(
name|client
argument_list|,
name|uri
argument_list|,
name|Status
operator|.
name|FORBIDDEN
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resp
operator|.
name|getEntity
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|authorizedUser
init|=
literal|"user1"
decl_stmt|;
name|uri
operator|=
name|URI
operator|.
name|create
argument_list|(
literal|"http://localhost:"
operator|+
name|serverPort
operator|+
literal|"/ws/v2/"
operator|+
literal|"timeline/apps/app1/entity-types?user.name="
operator|+
name|authorizedUser
argument_list|)
expr_stmt|;
name|verifyHttpResponse
argument_list|(
name|client
argument_list|,
name|uri
argument_list|,
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
name|uri
operator|=
name|URI
operator|.
name|create
argument_list|(
literal|"http://localhost:"
operator|+
name|serverPort
operator|+
literal|"/ws/v2/"
operator|+
literal|"timeline/apps/app1/entity-types?user.name="
operator|+
name|ADMIN
argument_list|)
expr_stmt|;
name|verifyHttpResponse
argument_list|(
name|client
argument_list|,
name|uri
argument_list|,
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
comment|// Verify with Query Parameter userid
name|uri
operator|=
name|URI
operator|.
name|create
argument_list|(
literal|"http://localhost:"
operator|+
name|serverPort
operator|+
literal|"/ws/v2/"
operator|+
literal|"timeline/apps/app1/entity-types?user.name="
operator|+
name|authorizedUser
operator|+
literal|"&userid="
operator|+
name|authorizedUser
argument_list|)
expr_stmt|;
name|verifyHttpResponse
argument_list|(
name|client
argument_list|,
name|uri
argument_list|,
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
name|uri
operator|=
name|URI
operator|.
name|create
argument_list|(
literal|"http://localhost:"
operator|+
name|serverPort
operator|+
literal|"/ws/v2/"
operator|+
literal|"timeline/apps/app1/entity-types?user.name="
operator|+
name|authorizedUser
operator|+
literal|"&userid="
operator|+
name|unAuthorizedUser
argument_list|)
expr_stmt|;
name|verifyHttpResponse
argument_list|(
name|client
argument_list|,
name|uri
argument_list|,
name|Status
operator|.
name|FORBIDDEN
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

