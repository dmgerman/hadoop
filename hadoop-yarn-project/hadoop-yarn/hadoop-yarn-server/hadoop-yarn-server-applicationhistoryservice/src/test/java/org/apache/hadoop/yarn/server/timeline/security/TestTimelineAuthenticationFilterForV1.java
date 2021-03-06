begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline.security
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
name|timeline
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|io
operator|.
name|Text
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
name|client
operator|.
name|AuthenticationException
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
name|authorize
operator|.
name|AuthorizationException
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|timeline
operator|.
name|TimelineDomain
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
name|timeline
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
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelinePutResponse
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
name|TimelineClient
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
name|security
operator|.
name|client
operator|.
name|TimelineDelegationTokenIdentifier
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
name|applicationhistoryservice
operator|.
name|ApplicationHistoryServer
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
name|MemoryTimelineStore
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
name|TimelineStore
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
comment|/**  * Test cases for authentication via TimelineAuthenticationFilter while  * publishing entities for ATSv1.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestTimelineAuthenticationFilterForV1
specifier|public
class|class
name|TestTimelineAuthenticationFilterForV1
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
DECL|field|BAR_USER
specifier|private
specifier|static
specifier|final
name|String
name|BAR_USER
init|=
literal|"bar"
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
literal|"target/test-dir"
argument_list|)
argument_list|,
name|TestTimelineAuthenticationFilterForV1
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-root"
argument_list|)
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
name|TestTimelineAuthenticationFilterForV1
operator|.
name|class
operator|.
name|getSimpleName
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
DECL|field|testTimelineServer
specifier|private
specifier|static
name|ApplicationHistoryServer
name|testTimelineServer
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|withSsl
specifier|private
specifier|static
name|boolean
name|withSsl
decl_stmt|;
DECL|method|TestTimelineAuthenticationFilterForV1 (boolean withSsl)
specifier|public
name|TestTimelineAuthenticationFilterForV1
parameter_list|(
name|boolean
name|withSsl
parameter_list|)
block|{
name|TestTimelineAuthenticationFilterForV1
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
name|assertTrue
argument_list|(
literal|"Couldn't setup MiniKDC"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|testTimelineServer
operator|=
operator|new
name|ApplicationHistoryServer
argument_list|()
expr_stmt|;
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
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STORE
argument_list|,
name|MemoryTimelineStore
operator|.
name|class
argument_list|,
name|TimelineStore
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
name|TIMELINE_SERVICE_ADDRESS
argument_list|,
literal|"localhost:10200"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_ADDRESS
argument_list|,
literal|"localhost:8188"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS
argument_list|,
literal|"localhost:8190"
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
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_CLIENT_MAX_RETRIES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
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
name|TestTimelineAuthenticationFilterForV1
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
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testTimelineServer
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testTimelineServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Couldn't setup TimelineServer"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createTimelineClientForUGI ()
specifier|private
name|TimelineClient
name|createTimelineClientForUGI
parameter_list|()
block|{
name|TimelineClient
name|client
init|=
name|TimelineClient
operator|.
name|createTimelineClient
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|testTimelineServer
operator|!=
literal|null
condition|)
block|{
name|testTimelineServer
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
name|TimelineClient
name|client
init|=
name|createTimelineClientForUGI
argument_list|()
decl_stmt|;
name|TimelineEntity
name|entityToStore
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entityToStore
operator|.
name|setEntityType
argument_list|(
name|TestTimelineAuthenticationFilterForV1
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|entityToStore
operator|.
name|setEntityId
argument_list|(
literal|"entity1"
argument_list|)
expr_stmt|;
name|entityToStore
operator|.
name|setStartTime
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|TimelinePutResponse
name|putResponse
init|=
name|client
operator|.
name|putEntities
argument_list|(
name|entityToStore
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|putResponse
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEntity
name|entityToRead
init|=
name|testTimelineServer
operator|.
name|getTimelineStore
argument_list|()
operator|.
name|getEntity
argument_list|(
literal|"entity1"
argument_list|,
name|TestTimelineAuthenticationFilterForV1
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|entityToRead
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutDomains ()
specifier|public
name|void
name|testPutDomains
parameter_list|()
throws|throws
name|Exception
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
name|TimelineClient
name|client
init|=
name|createTimelineClientForUGI
argument_list|()
decl_stmt|;
name|TimelineDomain
name|domainToStore
init|=
operator|new
name|TimelineDomain
argument_list|()
decl_stmt|;
name|domainToStore
operator|.
name|setId
argument_list|(
name|TestTimelineAuthenticationFilterForV1
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|domainToStore
operator|.
name|setReaders
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|domainToStore
operator|.
name|setWriters
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|client
operator|.
name|putDomain
argument_list|(
name|domainToStore
argument_list|)
expr_stmt|;
name|TimelineDomain
name|domainToRead
init|=
name|testTimelineServer
operator|.
name|getTimelineStore
argument_list|()
operator|.
name|getDomain
argument_list|(
name|TestTimelineAuthenticationFilterForV1
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|domainToRead
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelegationTokenOperations ()
specifier|public
name|void
name|testDelegationTokenOperations
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineClient
name|httpUserClient
init|=
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
name|TimelineClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TimelineClient
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createTimelineClientForUGI
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|httpUser
init|=
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
name|UserGroupInformation
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|UserGroupInformation
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Let HTTP user to get the delegation for itself
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|token
init|=
name|httpUserClient
operator|.
name|getDelegationToken
argument_list|(
name|httpUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|TimelineDelegationTokenIdentifier
name|tDT
init|=
name|token
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|tDT
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Text
argument_list|(
name|HTTP_USER
argument_list|)
argument_list|,
name|tDT
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
comment|// Renew token
name|Assert
operator|.
name|assertFalse
argument_list|(
name|token
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Renew the token from the token service address
name|long
name|renewTime1
init|=
name|httpUserClient
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|token
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|token
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// If the token service address is not avaiable, it still can be renewed
comment|// from the configured address
name|long
name|renewTime2
init|=
name|httpUserClient
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|renewTime1
operator|<
name|renewTime2
argument_list|)
expr_stmt|;
comment|// Cancel token
name|Assert
operator|.
name|assertTrue
argument_list|(
name|token
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// If the token service address is not avaiable, it still can be canceled
comment|// from the configured address
name|httpUserClient
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|// Renew should not be successful because the token is canceled
try|try
block|{
name|httpUserClient
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
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
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Renewal request for unknown token"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Let HTTP user to get the delegation token for FOO user
name|UserGroupInformation
name|fooUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|FOO_USER
argument_list|,
name|httpUser
argument_list|)
decl_stmt|;
name|TimelineClient
name|fooUserClient
init|=
name|fooUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|TimelineClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TimelineClient
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createTimelineClientForUGI
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|token
operator|=
name|fooUserClient
operator|.
name|getDelegationToken
argument_list|(
name|httpUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|tDT
operator|=
name|token
operator|.
name|decodeIdentifier
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|tDT
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Text
argument_list|(
name|FOO_USER
argument_list|)
argument_list|,
name|tDT
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Text
argument_list|(
name|HTTP_USER
argument_list|)
argument_list|,
name|tDT
operator|.
name|getRealUser
argument_list|()
argument_list|)
expr_stmt|;
comment|// Renew token as the renewer
specifier|final
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|tokenToRenew
init|=
name|token
decl_stmt|;
name|renewTime1
operator|=
name|httpUserClient
operator|.
name|renewDelegationToken
argument_list|(
name|tokenToRenew
argument_list|)
expr_stmt|;
name|renewTime2
operator|=
name|httpUserClient
operator|.
name|renewDelegationToken
argument_list|(
name|tokenToRenew
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|renewTime1
operator|<
name|renewTime2
argument_list|)
expr_stmt|;
comment|// Cancel token
name|Assert
operator|.
name|assertFalse
argument_list|(
name|tokenToRenew
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Cancel the token from the token service address
name|fooUserClient
operator|.
name|cancelDelegationToken
argument_list|(
name|tokenToRenew
argument_list|)
expr_stmt|;
comment|// Renew should not be successful because the token is canceled
try|try
block|{
name|httpUserClient
operator|.
name|renewDelegationToken
argument_list|(
name|tokenToRenew
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
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
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Renewal request for unknown token"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Let HTTP user to get the delegation token for BAR user
name|UserGroupInformation
name|barUgi
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|BAR_USER
argument_list|,
name|httpUser
argument_list|)
decl_stmt|;
name|TimelineClient
name|barUserClient
init|=
name|barUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|TimelineClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TimelineClient
name|run
parameter_list|()
block|{
return|return
name|createTimelineClientForUGI
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|barUserClient
operator|.
name|getDelegationToken
argument_list|(
name|httpUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
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
name|assertTrue
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AuthorizationException
operator|||
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|AuthenticationException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

