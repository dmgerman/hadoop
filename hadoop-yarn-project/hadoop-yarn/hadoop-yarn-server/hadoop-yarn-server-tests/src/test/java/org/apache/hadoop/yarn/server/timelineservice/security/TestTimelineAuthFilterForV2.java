begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.security
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
name|security
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
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
name|mock
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
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileReader
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|Callable
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|http
operator|.
name|HttpConfig
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
name|minikdc
operator|.
name|MiniKdc
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
name|SecurityUtil
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
name|UserGroupInformation
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
name|KerberosTestUtils
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
name|KerberosAuthenticationHandler
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
name|ssl
operator|.
name|KeyStoreTestUtil
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
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntity
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
name|client
operator|.
name|api
operator|.
name|TimelineV2Client
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
name|exceptions
operator|.
name|YarnException
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
name|api
operator|.
name|CollectorNodemanagerProtocol
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetTimelineCollectorContextRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|GetTimelineCollectorContextResponse
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
name|timeline
operator|.
name|security
operator|.
name|TimelineAuthenticationFilterInitializer
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
name|collector
operator|.
name|NodeTimelineCollectorManager
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
name|collector
operator|.
name|PerNodeTimelineCollectorsAuxService
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
name|FileSystemTimelineWriterImpl
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
name|TimelineWriter
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_comment
comment|/**  * Tests timeline authentication filter based security for timeline service v2.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestTimelineAuthFilterForV2
specifier|public
class|class
name|TestTimelineAuthFilterForV2
block|{
DECL|field|FOO_USER
specifier|private
specifier|static
specifier|final
name|String
name|FOO_USER
init|=
literal|"foo"
decl_stmt|;
DECL|field|HTTP_USER
specifier|private
specifier|static
specifier|final
name|String
name|HTTP_USER
init|=
literal|"HTTP"
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|File
name|TEST_ROOT_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.dir"
argument_list|,
literal|"target"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-dir"
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|BASEDIR
specifier|private
specifier|static
specifier|final
name|String
name|BASEDIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.dir"
argument_list|,
literal|"target/test-dir"
argument_list|)
operator|+
literal|"/"
operator|+
name|TestTimelineAuthFilterForV2
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|field|httpSpnegoKeytabFile
specifier|private
specifier|static
name|File
name|httpSpnegoKeytabFile
init|=
operator|new
name|File
argument_list|(
name|KerberosTestUtils
operator|.
name|getKeytabFile
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|httpSpnegoPrincipal
specifier|private
specifier|static
name|String
name|httpSpnegoPrincipal
init|=
name|KerberosTestUtils
operator|.
name|getServerPrincipal
argument_list|()
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|withSsl ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|withSsl
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|false
block|}
block|,
block|{
literal|true
block|}
block|}
argument_list|)
return|;
block|}
DECL|field|testMiniKDC
specifier|private
specifier|static
name|MiniKdc
name|testMiniKDC
decl_stmt|;
DECL|field|keystoresDir
specifier|private
specifier|static
name|String
name|keystoresDir
decl_stmt|;
DECL|field|sslConfDir
specifier|private
specifier|static
name|String
name|sslConfDir
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|withSsl
specifier|private
name|boolean
name|withSsl
decl_stmt|;
DECL|field|collectorManager
specifier|private
name|NodeTimelineCollectorManager
name|collectorManager
decl_stmt|;
DECL|field|auxService
specifier|private
name|PerNodeTimelineCollectorsAuxService
name|auxService
decl_stmt|;
DECL|method|TestTimelineAuthFilterForV2 (boolean withSsl)
specifier|public
name|TestTimelineAuthFilterForV2
parameter_list|(
name|boolean
name|withSsl
parameter_list|)
block|{
name|this
operator|.
name|withSsl
operator|=
name|withSsl
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
try|try
block|{
name|testMiniKDC
operator|=
operator|new
name|MiniKdc
argument_list|(
name|MiniKdc
operator|.
name|createConf
argument_list|()
argument_list|,
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
name|testMiniKDC
operator|.
name|start
argument_list|()
expr_stmt|;
name|testMiniKDC
operator|.
name|createPrincipal
argument_list|(
name|httpSpnegoKeytabFile
argument_list|,
name|HTTP_USER
operator|+
literal|"/localhost"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Couldn't setup MiniKDC."
argument_list|)
expr_stmt|;
block|}
comment|// Setup timeline service v2.
try|try
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
name|TimelineAuthenticationFilterInitializer
operator|.
name|PREFIX
operator|+
literal|"type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|TimelineAuthenticationFilterInitializer
operator|.
name|PREFIX
operator|+
name|KerberosAuthenticationHandler
operator|.
name|PRINCIPAL
argument_list|,
name|httpSpnegoPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|TimelineAuthenticationFilterInitializer
operator|.
name|PREFIX
operator|+
name|KerberosAuthenticationHandler
operator|.
name|KEYTAB
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHENTICATION
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PRINCIPAL
argument_list|,
name|httpSpnegoPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_KEYTAB
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Enable timeline service v2
name|conf
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
name|conf
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
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WRITER_CLASS
argument_list|,
name|FileSystemTimelineWriterImpl
operator|.
name|class
argument_list|,
name|TimelineWriter
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
argument_list|,
name|TEST_ROOT_DIR
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.HTTP.hosts"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.HTTP.users"
argument_list|,
name|FOO_USER
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_KEYTAB
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PRINCIPAL
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Couldn't setup TimelineServer V2."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|initialize ()
specifier|public
name|void
name|initialize
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|withSsl
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_HTTP_POLICY_KEY
argument_list|,
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTPS_ONLY
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|base
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|keystoresDir
operator|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|sslConfDir
operator|=
name|KeyStoreTestUtil
operator|.
name|getClasspathDir
argument_list|(
name|TestTimelineAuthFilterForV2
operator|.
name|class
argument_list|)
expr_stmt|;
name|KeyStoreTestUtil
operator|.
name|setupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|,
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_HTTP_POLICY_KEY
argument_list|,
name|HttpConfig
operator|.
name|Policy
operator|.
name|HTTP_ONLY
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|collectorManager
operator|=
operator|new
name|DummyNodeTimelineCollectorManager
argument_list|()
expr_stmt|;
name|auxService
operator|=
name|PerNodeTimelineCollectorsAuxService
operator|.
name|launchServer
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|collectorManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|createTimelineClientForUGI (ApplicationId appId)
specifier|private
name|TimelineV2Client
name|createTimelineClientForUGI
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|TimelineV2Client
name|client
init|=
name|TimelineV2Client
operator|.
name|createTimelineClient
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// set the timeline service address.
name|String
name|restBindAddr
init|=
name|collectorManager
operator|.
name|getRestServerBindAddress
argument_list|()
decl_stmt|;
name|String
name|addr
init|=
literal|"localhost"
operator|+
name|restBindAddr
operator|.
name|substring
argument_list|(
name|restBindAddr
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
argument_list|)
decl_stmt|;
name|client
operator|.
name|setTimelineServiceAddress
argument_list|(
name|addr
argument_list|)
expr_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|client
return|;
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
if|if
condition|(
name|testMiniKDC
operator|!=
literal|null
condition|)
block|{
name|testMiniKDC
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|auxService
operator|!=
literal|null
condition|)
block|{
name|auxService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|withSsl
condition|)
block|{
name|KeyStoreTestUtil
operator|.
name|cleanupSSLConfig
argument_list|(
name|keystoresDir
argument_list|,
name|sslConfDir
argument_list|)
expr_stmt|;
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|BASEDIR
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createEntity (String id, String type)
specifier|private
specifier|static
name|TimelineEntity
name|createEntity
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|TimelineEntity
name|entityToStore
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entityToStore
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|entityToStore
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|entityToStore
operator|.
name|setCreatedTime
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
return|return
name|entityToStore
return|;
block|}
DECL|method|verifyEntity (File entityTypeDir, String id, String type)
specifier|private
specifier|static
name|void
name|verifyEntity
parameter_list|(
name|File
name|entityTypeDir
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|entityFile
init|=
operator|new
name|File
argument_list|(
name|entityTypeDir
argument_list|,
name|id
operator|+
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_EXTENSION
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|entityFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEntity
name|entity
init|=
name|readEntityFile
argument_list|(
name|entityFile
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|id
argument_list|,
name|entity
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|type
argument_list|,
name|entity
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readEntityFile (File entityFile)
specifier|private
specifier|static
name|TimelineEntity
name|readEntityFile
parameter_list|(
name|File
name|entityFile
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
name|String
name|strLine
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|entityFile
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|strLine
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|strLine
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|FileSystemTimelineReaderImpl
operator|.
name|getTimelineRecordFromJSON
argument_list|(
name|strLine
operator|.
name|trim
argument_list|()
argument_list|,
name|TimelineEntity
operator|.
name|class
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPutTimelineEntities ()
specifier|public
name|void
name|testPutTimelineEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|auxService
operator|.
name|addApplication
argument_list|(
name|appId
argument_list|)
expr_stmt|;
specifier|final
name|String
name|entityType
init|=
literal|"dummy_type"
decl_stmt|;
name|File
name|entityTypeDir
init|=
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"entities"
operator|+
name|File
operator|.
name|separator
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CLUSTER_ID
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test_user"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test_flow_name"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test_flow_version"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"1"
operator|+
name|File
operator|.
name|separator
operator|+
name|appId
operator|.
name|toString
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|entityType
argument_list|)
decl_stmt|;
try|try
block|{
name|KerberosTestUtils
operator|.
name|doAs
argument_list|(
name|HTTP_USER
operator|+
literal|"/localhost"
argument_list|,
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineV2Client
name|client
init|=
name|createTimelineClientForUGI
argument_list|(
name|appId
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Sync call. Results available immediately.
name|client
operator|.
name|putEntities
argument_list|(
name|createEntity
argument_list|(
literal|"entity1"
argument_list|,
name|entityType
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entityTypeDir
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|verifyEntity
argument_list|(
name|entityTypeDir
argument_list|,
literal|"entity1"
argument_list|,
name|entityType
argument_list|)
expr_stmt|;
comment|// Async call.
name|client
operator|.
name|putEntitiesAsync
argument_list|(
name|createEntity
argument_list|(
literal|"entity2"
argument_list|,
name|entityType
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Wait for async entity to be published.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|50
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|entityTypeDir
operator|.
name|listFiles
argument_list|()
operator|.
name|length
operator|==
literal|2
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|entityTypeDir
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|verifyEntity
argument_list|(
name|entityTypeDir
argument_list|,
literal|"entity2"
argument_list|,
name|entityType
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|entityTypeDir
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DummyNodeTimelineCollectorManager
specifier|private
specifier|static
class|class
name|DummyNodeTimelineCollectorManager
extends|extends
name|NodeTimelineCollectorManager
block|{
DECL|method|DummyNodeTimelineCollectorManager ()
name|DummyNodeTimelineCollectorManager
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNMCollectorService ()
specifier|protected
name|CollectorNodemanagerProtocol
name|getNMCollectorService
parameter_list|()
block|{
name|CollectorNodemanagerProtocol
name|protocol
init|=
name|mock
argument_list|(
name|CollectorNodemanagerProtocol
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|GetTimelineCollectorContextResponse
name|response
init|=
name|GetTimelineCollectorContextResponse
operator|.
name|newInstance
argument_list|(
literal|"test_user"
argument_list|,
literal|"test_flow_name"
argument_list|,
literal|"test_flow_version"
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|protocol
operator|.
name|getTimelineCollectorContext
argument_list|(
name|any
argument_list|(
name|GetTimelineCollectorContextRequest
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
return|return
name|protocol
return|;
block|}
block|}
block|}
end_class

end_unit

