begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|StringWriter
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
name|URL
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
name|bind
operator|.
name|JAXBContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|Marshaller
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
name|IOUtils
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ApplicationSubmissionContextInfo
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
name|ConverterUtils
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
name|ClientResponse
operator|.
name|Status
import|;
end_import

begin_class
DECL|class|TestRMWebServicesDelegationTokenAuthentication
specifier|public
class|class
name|TestRMWebServicesDelegationTokenAuthentication
block|{
DECL|field|testRootDir
specifier|private
specifier|static
specifier|final
name|File
name|testRootDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestRMWebServicesDelegationTokenAuthentication
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
DECL|field|miniKDCStarted
specifier|private
specifier|static
name|boolean
name|miniKDCStarted
init|=
literal|false
decl_stmt|;
DECL|field|testMiniKDC
specifier|private
specifier|static
name|MiniKdc
name|testMiniKDC
decl_stmt|;
DECL|field|rm
specifier|private
specifier|static
name|MockRM
name|rm
decl_stmt|;
comment|// use published header name
DECL|field|DelegationTokenHeader
specifier|final
specifier|static
name|String
name|DelegationTokenHeader
init|=
literal|"Hadoop-YARN-Auth-Delegation-Token"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
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
name|testRootDir
argument_list|)
expr_stmt|;
name|setupKDC
argument_list|()
expr_stmt|;
name|setupAndStartRM
argument_list|()
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
literal|"Couldn't create MiniKDC"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|rm
operator|!=
literal|null
condition|)
block|{
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|TestRMWebServicesDelegationTokenAuthentication ()
specifier|public
name|TestRMWebServicesDelegationTokenAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|setupAndStartRM ()
specifier|private
specifier|static
name|void
name|setupAndStartRM
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|rmconf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|rmconf
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
name|rmconf
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
name|rmconf
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
name|String
name|httpPrefix
init|=
literal|"hadoop.http.authentication."
decl_stmt|;
name|rmconf
operator|.
name|setStrings
argument_list|(
name|httpPrefix
operator|+
literal|"type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|httpPrefix
operator|+
name|KerberosAuthenticationHandler
operator|.
name|PRINCIPAL
argument_list|,
name|httpSpnegoPrincipal
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|httpPrefix
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
comment|// use any file for signature secret
name|rmconf
operator|.
name|set
argument_list|(
name|httpPrefix
operator|+
name|AuthenticationFilter
operator|.
name|SIGNATURE_SECRET
operator|+
literal|".file"
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|rmconf
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
name|rmconf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_DELEGATION_TOKEN_AUTH_FILTER
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
literal|"hadoop.http.filter.initializers"
argument_list|,
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_SPNEGO_USER_NAME_KEY
argument_list|,
name|httpSpnegoPrincipal
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_KEYTAB
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_SPNEGO_USER_NAME_KEY
argument_list|,
name|httpSpnegoPrincipal
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY
argument_list|,
name|httpSpnegoKeytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|rmconf
operator|.
name|setBoolean
argument_list|(
literal|"mockrm.webapp.enabled"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|rmconf
argument_list|)
expr_stmt|;
name|rm
operator|=
operator|new
name|MockRM
argument_list|(
name|rmconf
argument_list|)
expr_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|setupKDC ()
specifier|private
specifier|static
name|void
name|setupKDC
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|miniKDCStarted
operator|==
literal|false
condition|)
block|{
name|testMiniKDC
operator|.
name|start
argument_list|()
expr_stmt|;
name|getKdc
argument_list|()
operator|.
name|createPrincipal
argument_list|(
name|httpSpnegoKeytabFile
argument_list|,
literal|"HTTP/localhost"
argument_list|,
literal|"client"
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|miniKDCStarted
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|getKdc ()
specifier|private
specifier|static
name|MiniKdc
name|getKdc
parameter_list|()
block|{
return|return
name|testMiniKDC
return|;
block|}
comment|// Test that you can authenticate with only delegation tokens
comment|// 1. Get a delegation token using Kerberos auth(this ends up
comment|// testing the fallback authenticator)
comment|// 2. Submit an app without kerberos or delegation-token
comment|// - we should get an UNAUTHORIZED response
comment|// 3. Submit same app with delegation-token
comment|// - we should get OK response
comment|// - confirm owner of the app is the user whose
comment|// delegation-token we used
annotation|@
name|Test
DECL|method|testDelegationTokenAuth ()
specifier|public
name|void
name|testDelegationTokenAuth
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|ApplicationSubmissionContextInfo
name|app
init|=
operator|new
name|ApplicationSubmissionContextInfo
argument_list|()
decl_stmt|;
name|String
name|appid
init|=
literal|"application_123_0"
decl_stmt|;
name|app
operator|.
name|setApplicationId
argument_list|(
name|appid
argument_list|)
expr_stmt|;
name|String
name|requestBody
init|=
name|getMarshalledAppInfo
argument_list|(
name|app
argument_list|)
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8088/ws/v1/cluster/apps"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|setupConn
argument_list|(
name|conn
argument_list|,
literal|"POST"
argument_list|,
literal|"application/xml"
argument_list|,
name|requestBody
argument_list|)
expr_stmt|;
comment|// this should fail with unauthorized because only
comment|// auth is kerberos or delegation token
try|try
block|{
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"we should not be here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|UNAUTHORIZED
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|DelegationTokenHeader
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|setupConn
argument_list|(
name|conn
argument_list|,
literal|"POST"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|,
name|requestBody
argument_list|)
expr_stmt|;
comment|// this should not fail
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|boolean
name|appExists
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|containsKey
argument_list|(
name|ConverterUtils
operator|.
name|toApplicationId
argument_list|(
name|appid
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|appExists
argument_list|)
expr_stmt|;
name|RMApp
name|actualApp
init|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|ConverterUtils
operator|.
name|toApplicationId
argument_list|(
name|appid
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|owner
init|=
name|actualApp
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"client"
argument_list|,
name|owner
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Test to make sure that cancelled delegation tokens
comment|// are rejected
annotation|@
name|Test
DECL|method|testCancelledDelegationToken ()
specifier|public
name|void
name|testCancelledDelegationToken
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|"client"
argument_list|)
decl_stmt|;
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|ApplicationSubmissionContextInfo
name|app
init|=
operator|new
name|ApplicationSubmissionContextInfo
argument_list|()
decl_stmt|;
name|String
name|appid
init|=
literal|"application_123_0"
decl_stmt|;
name|app
operator|.
name|setApplicationId
argument_list|(
name|appid
argument_list|)
expr_stmt|;
name|String
name|requestBody
init|=
name|getMarshalledAppInfo
argument_list|(
name|app
argument_list|)
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8088/ws/v1/cluster/apps"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|DelegationTokenHeader
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|setupConn
argument_list|(
name|conn
argument_list|,
literal|"POST"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|,
name|requestBody
argument_list|)
expr_stmt|;
comment|// this should fail with unauthorized because only
comment|// auth is kerberos or delegation token
try|try
block|{
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Authentication should fail with expired delegation tokens"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|FORBIDDEN
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
comment|// Test to make sure that we can't do delegation token
comment|// functions using just delegation token auth
annotation|@
name|Test
DECL|method|testDelegationTokenOps ()
specifier|public
name|void
name|testDelegationTokenOps
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|token
init|=
name|getDelegationToken
argument_list|(
literal|"client"
argument_list|)
decl_stmt|;
name|String
name|createRequest
init|=
literal|"{\"renewer\":\"test\"}"
decl_stmt|;
name|String
name|renewRequest
init|=
literal|"{\"token\": \""
operator|+
name|token
operator|+
literal|"\"}"
decl_stmt|;
comment|// first test create and renew
name|String
index|[]
name|requests
init|=
block|{
name|createRequest
block|,
name|renewRequest
block|}
decl_stmt|;
for|for
control|(
name|String
name|requestBody
range|:
name|requests
control|)
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8088/ws/v1/cluster/delegation-token"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|DelegationTokenHeader
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|setupConn
argument_list|(
name|conn
argument_list|,
literal|"POST"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|,
name|requestBody
argument_list|)
expr_stmt|;
try|try
block|{
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Creation/Renewing delegation tokens should not be "
operator|+
literal|"allowed with token auth"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|FORBIDDEN
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// test cancel
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8088/ws/v1/cluster/delegation-token"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|DelegationTokenHeader
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|RMWebServices
operator|.
name|DELEGATION_TOKEN_HEADER
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|setupConn
argument_list|(
name|conn
argument_list|,
literal|"DELETE"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|conn
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Cancelling delegation tokens should not be allowed with token auth"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Status
operator|.
name|FORBIDDEN
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
DECL|method|getDelegationToken (final String renewer)
specifier|private
name|String
name|getDelegationToken
parameter_list|(
specifier|final
name|String
name|renewer
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|token
init|=
name|KerberosTestUtils
operator|.
name|doAsClient
argument_list|(
operator|new
name|Callable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|ret
init|=
literal|null
decl_stmt|;
name|String
name|body
init|=
literal|"{\"renewer\":\""
operator|+
name|renewer
operator|+
literal|"\"}"
decl_stmt|;
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8088/ws/v1/cluster/delegation-token"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|setupConn
argument_list|(
name|conn
argument_list|,
literal|"POST"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|InputStream
name|response
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|OK
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|response
argument_list|,
literal|"UTF8"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|line
init|;
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|;
control|)
block|{
name|JSONObject
name|obj
init|=
operator|new
name|JSONObject
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|.
name|has
argument_list|(
literal|"token"
argument_list|)
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|response
operator|.
name|close
argument_list|()
expr_stmt|;
name|ret
operator|=
name|obj
operator|.
name|getString
argument_list|(
literal|"token"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|token
return|;
block|}
DECL|method|cancelDelegationToken (final String tokenString)
specifier|private
name|void
name|cancelDelegationToken
parameter_list|(
specifier|final
name|String
name|tokenString
parameter_list|)
throws|throws
name|Exception
block|{
name|KerberosTestUtils
operator|.
name|doAsClient
argument_list|(
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
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http://localhost:8088/ws/v1/cluster/delegation-token"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
name|RMWebServices
operator|.
name|DELEGATION_TOKEN_HEADER
argument_list|,
name|tokenString
argument_list|)
expr_stmt|;
name|setupConn
argument_list|(
name|conn
argument_list|,
literal|"DELETE"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|InputStream
name|response
init|=
name|conn
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|OK
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
DECL|method|getMarshalledAppInfo (ApplicationSubmissionContextInfo appInfo)
specifier|static
name|String
name|getMarshalledAppInfo
parameter_list|(
name|ApplicationSubmissionContextInfo
name|appInfo
parameter_list|)
throws|throws
name|Exception
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JAXBContext
name|context
init|=
name|JAXBContext
operator|.
name|newInstance
argument_list|(
name|ApplicationSubmissionContextInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|Marshaller
name|m
init|=
name|context
operator|.
name|createMarshaller
argument_list|()
decl_stmt|;
name|m
operator|.
name|marshal
argument_list|(
name|appInfo
argument_list|,
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setupConn (HttpURLConnection conn, String method, String contentType, String body)
specifier|static
name|void
name|setupConn
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|contentType
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|Exception
block|{
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"Accept-Charset"
argument_list|,
literal|"UTF8"
argument_list|)
expr_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
operator|&&
operator|!
name|contentType
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"Content-Type"
argument_list|,
name|contentType
operator|+
literal|";charset=UTF8"
argument_list|)
expr_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
operator|&&
operator|!
name|body
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|OutputStream
name|stream
init|=
name|conn
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|body
operator|.
name|getBytes
argument_list|(
literal|"UTF8"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

