begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.http
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
package|;
end_package

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
name|CommonConfigurationKeys
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
name|net
operator|.
name|NetUtils
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
name|AuthenticationFilterInitializer
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
name|AuthenticatedURL
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
name|AuthenticationToken
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
name|util
operator|.
name|Signer
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
name|util
operator|.
name|SignerSecretProvider
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
name|util
operator|.
name|StringSignerSecretProviderCreator
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
name|AccessControlList
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
name|ProxyUsers
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
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|java
operator|.
name|util
operator|.
name|Properties
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
comment|/**  * This class is tested for http server with SPENGO authentication.  */
end_comment

begin_class
DECL|class|TestHttpServerWithSpengo
specifier|public
class|class
name|TestHttpServerWithSpengo
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestHttpServerWithSpengo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SECRET_STR
specifier|private
specifier|static
specifier|final
name|String
name|SECRET_STR
init|=
literal|"secret"
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
DECL|field|PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"hadoop.http.authentication."
decl_stmt|;
DECL|field|TIMEOUT
specifier|private
specifier|static
specifier|final
name|long
name|TIMEOUT
init|=
literal|20000
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
DECL|field|realm
specifier|private
specifier|static
name|String
name|realm
init|=
name|KerberosTestUtils
operator|.
name|getRealm
argument_list|()
decl_stmt|;
DECL|field|testRootDir
specifier|private
specifier|static
name|File
name|testRootDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestHttpServerWithSpengo
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-root"
argument_list|)
decl_stmt|;
DECL|field|testMiniKDC
specifier|private
specifier|static
name|MiniKdc
name|testMiniKDC
decl_stmt|;
DECL|field|secretFile
specifier|private
specifier|static
name|File
name|secretFile
init|=
operator|new
name|File
argument_list|(
name|testRootDir
argument_list|,
name|SECRET_STR
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
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
name|testRootDir
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
name|Writer
name|w
init|=
operator|new
name|FileWriter
argument_list|(
name|secretFile
argument_list|)
decl_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"secret"
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
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
block|}
comment|/**    * groupA    *  - userA    * groupB    *  - userA, userB    * groupC    *  - userC    * SPNEGO filter has been enabled.    * userA has the privilege to impersonate users in groupB.    * userA has admin access to all default servlets, but userB    * and userC don't have. So "/logs" can only be accessed by userA.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testAuthenticationWithProxyUser ()
specifier|public
name|void
name|testAuthenticationWithProxyUser
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|spengoConf
init|=
name|getSpengoConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
comment|//setup logs dir
name|System
operator|.
name|setProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|,
name|testRootDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Setup user group
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"userA"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"groupA"
block|,
literal|"groupB"
block|}
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"userB"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"groupB"
block|}
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"userC"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"groupC"
block|}
argument_list|)
expr_stmt|;
comment|// Make userA impersonate users in groupB
name|spengoConf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.userA.hosts"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|spengoConf
operator|.
name|set
argument_list|(
literal|"hadoop.proxyuser.userA.groups"
argument_list|,
literal|"groupB"
argument_list|)
expr_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|spengoConf
argument_list|)
expr_stmt|;
name|HttpServer2
name|httpServer
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Create http server to test.
name|httpServer
operator|=
name|getCommonBuilder
argument_list|()
operator|.
name|setConf
argument_list|(
name|spengoConf
argument_list|)
operator|.
name|setACL
argument_list|(
operator|new
name|AccessControlList
argument_list|(
literal|"userA groupA"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Get signer to encrypt token
name|Signer
name|signer
init|=
name|getSignerToEncrypt
argument_list|()
decl_stmt|;
comment|// setup auth token for userA
name|AuthenticatedURL
operator|.
name|Token
name|token
init|=
name|getEncryptedAuthToken
argument_list|(
name|signer
argument_list|,
literal|"userA"
argument_list|)
decl_stmt|;
name|String
name|serverURL
init|=
literal|"http://"
operator|+
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|httpServer
operator|.
name|getConnectorAddress
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
literal|"/"
decl_stmt|;
comment|// The default authenticator is kerberos.
name|AuthenticatedURL
name|authUrl
init|=
operator|new
name|AuthenticatedURL
argument_list|()
decl_stmt|;
comment|// userA impersonates userB, it's allowed.
for|for
control|(
name|String
name|servlet
range|:
operator|new
name|String
index|[]
block|{
literal|"stacks"
block|,
literal|"jmx"
block|,
literal|"conf"
block|}
control|)
block|{
name|HttpURLConnection
name|conn
init|=
name|authUrl
operator|.
name|openConnection
argument_list|(
operator|new
name|URL
argument_list|(
name|serverURL
operator|+
name|servlet
operator|+
literal|"?doAs=userB"
argument_list|)
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// userA cannot impersonate userC, but for /stacks, /jmx and /conf,
comment|// they doesn't require users to authorize by default, so they
comment|// can be accessed.
for|for
control|(
name|String
name|servlet
range|:
operator|new
name|String
index|[]
block|{
literal|"stacks"
block|,
literal|"jmx"
block|,
literal|"conf"
block|}
control|)
block|{
name|HttpURLConnection
name|conn
init|=
name|authUrl
operator|.
name|openConnection
argument_list|(
operator|new
name|URL
argument_list|(
name|serverURL
operator|+
name|servlet
operator|+
literal|"?doAs=userC"
argument_list|)
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// "/logs" and "/logLevel" require admin authorization,
comment|// only userA has the access.
for|for
control|(
name|String
name|servlet
range|:
operator|new
name|String
index|[]
block|{
literal|"logLevel"
block|,
literal|"logs"
block|}
control|)
block|{
name|HttpURLConnection
name|conn
init|=
name|authUrl
operator|.
name|openConnection
argument_list|(
operator|new
name|URL
argument_list|(
name|serverURL
operator|+
name|servlet
operator|+
literal|"?doAs=userC"
argument_list|)
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// "/logs" and "/logLevel" require admin authorization,
comment|// only userA has the access.
for|for
control|(
name|String
name|servlet
range|:
operator|new
name|String
index|[]
block|{
literal|"logLevel"
block|,
literal|"logs"
block|}
control|)
block|{
name|HttpURLConnection
name|conn
init|=
name|authUrl
operator|.
name|openConnection
argument_list|(
operator|new
name|URL
argument_list|(
name|serverURL
operator|+
name|servlet
argument_list|)
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Setup token for userB
name|token
operator|=
name|getEncryptedAuthToken
argument_list|(
name|signer
argument_list|,
literal|"userB"
argument_list|)
expr_stmt|;
comment|// userB cannot access these servlets.
for|for
control|(
name|String
name|servlet
range|:
operator|new
name|String
index|[]
block|{
literal|"logLevel"
block|,
literal|"logs"
block|}
control|)
block|{
name|HttpURLConnection
name|conn
init|=
name|authUrl
operator|.
name|openConnection
argument_list|(
operator|new
name|URL
argument_list|(
name|serverURL
operator|+
name|servlet
argument_list|)
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|httpServer
operator|!=
literal|null
condition|)
block|{
name|httpServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getEncryptedAuthToken (Signer signer, String user)
specifier|private
name|AuthenticatedURL
operator|.
name|Token
name|getEncryptedAuthToken
parameter_list|(
name|Signer
name|signer
parameter_list|,
name|String
name|user
parameter_list|)
throws|throws
name|Exception
block|{
name|AuthenticationToken
name|token
init|=
operator|new
name|AuthenticationToken
argument_list|(
name|user
argument_list|,
name|user
argument_list|,
literal|"kerberos"
argument_list|)
decl_stmt|;
name|token
operator|.
name|setExpires
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TIMEOUT
argument_list|)
expr_stmt|;
return|return
operator|new
name|AuthenticatedURL
operator|.
name|Token
argument_list|(
name|signer
operator|.
name|sign
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getSignerToEncrypt ()
specifier|private
name|Signer
name|getSignerToEncrypt
parameter_list|()
throws|throws
name|Exception
block|{
name|SignerSecretProvider
name|secretProvider
init|=
name|StringSignerSecretProviderCreator
operator|.
name|newStringSignerSecretProvider
argument_list|()
decl_stmt|;
name|Properties
name|secretProviderProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|secretProviderProps
operator|.
name|setProperty
argument_list|(
name|AuthenticationFilter
operator|.
name|SIGNATURE_SECRET
argument_list|,
name|SECRET_STR
argument_list|)
expr_stmt|;
name|secretProvider
operator|.
name|init
argument_list|(
name|secretProviderProps
argument_list|,
literal|null
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
return|return
operator|new
name|Signer
argument_list|(
name|secretProvider
argument_list|)
return|;
block|}
DECL|method|getSpengoConf (Configuration conf)
specifier|private
name|Configuration
name|getSpengoConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|HttpServer2
operator|.
name|FILTER_INITIALIZER_PROPERTY
argument_list|,
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|PREFIX
operator|+
literal|"simple.anonymous.allowed"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"signature.secret.file"
argument_list|,
name|secretFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"kerberos.keytab"
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
name|PREFIX
operator|+
literal|"kerberos.principal"
argument_list|,
name|httpSpnegoPrincipal
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"cookie.domain"
argument_list|,
name|realm
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|getCommonBuilder ()
specifier|private
name|HttpServer2
operator|.
name|Builder
name|getCommonBuilder
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|HttpServer2
operator|.
name|Builder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addEndpoint
argument_list|(
operator|new
name|URI
argument_list|(
literal|"http://localhost:0"
argument_list|)
argument_list|)
operator|.
name|setFindPort
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

