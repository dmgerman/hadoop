begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
operator|.
name|server
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|http
operator|.
name|client
operator|.
name|HttpFSFileSystem
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
name|http
operator|.
name|client
operator|.
name|HttpFSKerberosAuthenticator
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
name|http
operator|.
name|client
operator|.
name|HttpFSKerberosAuthenticator
operator|.
name|DelegationTokenOperation
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
name|lib
operator|.
name|service
operator|.
name|DelegationTokenIdentifier
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
name|lib
operator|.
name|service
operator|.
name|DelegationTokenManager
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
name|lib
operator|.
name|service
operator|.
name|DelegationTokenManagerException
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
name|AuthenticationHandler
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
name|test
operator|.
name|HFSTestCase
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
name|test
operator|.
name|TestDir
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
name|test
operator|.
name|TestDirHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|JSONParser
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|io
operator|.
name|PrintWriter
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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_class
DECL|class|TestHttpFSKerberosAuthenticationHandler
specifier|public
class|class
name|TestHttpFSKerberosAuthenticationHandler
extends|extends
name|HFSTestCase
block|{
annotation|@
name|Test
annotation|@
name|TestDir
DECL|method|testManagementOperations ()
specifier|public
name|void
name|testManagementOperations
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|Configuration
name|httpfsConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|HttpFSServerWebApp
name|server
init|=
operator|new
name|HttpFSServerWebApp
argument_list|(
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|httpfsConf
argument_list|)
decl_stmt|;
name|server
operator|.
name|setAuthority
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|,
literal|14000
argument_list|)
argument_list|)
expr_stmt|;
name|AuthenticationHandler
name|handler
init|=
operator|new
name|HttpFSKerberosAuthenticationHandlerForTesting
argument_list|()
decl_stmt|;
try|try
block|{
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|testNonManagementOperation
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|testManagementOperationErrors
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|testGetToken
argument_list|(
name|handler
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testGetToken
argument_list|(
name|handler
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|testGetToken
argument_list|(
name|handler
argument_list|,
literal|false
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|testGetToken
argument_list|(
name|handler
argument_list|,
literal|true
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|testCancelToken
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|testRenewToken
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testNonManagementOperation (AuthenticationHandler handler)
specifier|private
name|void
name|testNonManagementOperation
parameter_list|(
name|AuthenticationHandler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSFileSystem
operator|.
name|OP_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
literal|null
argument_list|,
name|request
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSFileSystem
operator|.
name|OP_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|HttpFSFileSystem
operator|.
name|Operation
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
literal|null
argument_list|,
name|request
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testManagementOperationErrors (AuthenticationHandler handler)
specifier|private
name|void
name|testManagementOperationErrors
parameter_list|(
name|AuthenticationHandler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletResponse
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSFileSystem
operator|.
name|OP_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|DelegationTokenOperation
operator|.
name|GETDELEGATIONTOKEN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"FOO"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
literal|null
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendError
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
argument_list|,
name|Mockito
operator|.
name|startsWith
argument_list|(
literal|"Wrong HTTP method"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|reset
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|DelegationTokenOperation
operator|.
name|GETDELEGATIONTOKEN
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
literal|null
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendError
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|)
argument_list|,
name|Mockito
operator|.
name|contains
argument_list|(
literal|"requires SPNEGO"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetToken (AuthenticationHandler handler, boolean tokens, String renewer)
specifier|private
name|void
name|testGetToken
parameter_list|(
name|AuthenticationHandler
name|handler
parameter_list|,
name|boolean
name|tokens
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|Exception
block|{
name|DelegationTokenOperation
name|op
init|=
operator|(
name|tokens
operator|)
condition|?
name|DelegationTokenOperation
operator|.
name|GETDELEGATIONTOKENS
else|:
name|DelegationTokenOperation
operator|.
name|GETDELEGATIONTOKEN
decl_stmt|;
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletResponse
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSFileSystem
operator|.
name|OP_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|op
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|op
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AuthenticationToken
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
literal|null
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSKerberosAuthenticator
operator|.
name|RENEWER_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|renewer
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|reset
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pwriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|response
operator|.
name|getWriter
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pwriter
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
name|token
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|renewer
operator|==
literal|null
condition|)
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|token
argument_list|)
operator|.
name|getUserName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Mockito
operator|.
name|verify
argument_list|(
name|token
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|getUserName
argument_list|()
expr_stmt|;
block|}
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|setContentType
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
expr_stmt|;
name|pwriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|responseOutput
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|tokenLabel
init|=
operator|(
name|tokens
operator|)
condition|?
name|HttpFSKerberosAuthenticator
operator|.
name|DELEGATION_TOKENS_JSON
else|:
name|HttpFSKerberosAuthenticator
operator|.
name|DELEGATION_TOKEN_JSON
decl_stmt|;
if|if
condition|(
name|tokens
condition|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|responseOutput
operator|.
name|contains
argument_list|(
name|tokenLabel
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|responseOutput
operator|.
name|contains
argument_list|(
name|tokenLabel
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|responseOutput
operator|.
name|contains
argument_list|(
name|HttpFSKerberosAuthenticator
operator|.
name|DELEGATION_TOKEN_URL_STRING_JSON
argument_list|)
argument_list|)
expr_stmt|;
name|JSONObject
name|json
init|=
operator|(
name|JSONObject
operator|)
operator|new
name|JSONParser
argument_list|()
operator|.
name|parse
argument_list|(
name|responseOutput
argument_list|)
decl_stmt|;
name|json
operator|=
operator|(
name|JSONObject
operator|)
name|json
operator|.
name|get
argument_list|(
name|tokenLabel
argument_list|)
expr_stmt|;
name|String
name|tokenStr
decl_stmt|;
if|if
condition|(
name|tokens
condition|)
block|{
name|json
operator|=
call|(
name|JSONObject
call|)
argument_list|(
operator|(
name|JSONArray
operator|)
name|json
operator|.
name|get
argument_list|(
name|HttpFSKerberosAuthenticator
operator|.
name|DELEGATION_TOKEN_JSON
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|tokenStr
operator|=
operator|(
name|String
operator|)
name|json
operator|.
name|get
argument_list|(
name|HttpFSKerberosAuthenticator
operator|.
name|DELEGATION_TOKEN_URL_STRING_JSON
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|dt
init|=
operator|new
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|dt
operator|.
name|decodeFromUrlString
argument_list|(
name|tokenStr
argument_list|)
expr_stmt|;
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|DelegationTokenManager
operator|.
name|class
argument_list|)
operator|.
name|verifyToken
argument_list|(
name|dt
argument_list|)
expr_stmt|;
block|}
DECL|method|testCancelToken (AuthenticationHandler handler)
specifier|private
name|void
name|testCancelToken
parameter_list|(
name|AuthenticationHandler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|DelegationTokenOperation
name|op
init|=
name|DelegationTokenOperation
operator|.
name|CANCELDELEGATIONTOKEN
decl_stmt|;
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletResponse
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSFileSystem
operator|.
name|OP_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|op
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|op
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
literal|null
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendError
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
argument_list|,
name|Mockito
operator|.
name|contains
argument_list|(
literal|"requires the parameter [token]"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|reset
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|DelegationTokenManager
operator|.
name|class
argument_list|)
operator|.
name|createToken
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSKerberosAuthenticator
operator|.
name|TOKEN_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|token
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
literal|null
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
try|try
block|{
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|DelegationTokenManager
operator|.
name|class
argument_list|)
operator|.
name|verifyToken
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
name|DelegationTokenManagerException
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"DT01"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRenewToken (AuthenticationHandler handler)
specifier|private
name|void
name|testRenewToken
parameter_list|(
name|AuthenticationHandler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|DelegationTokenOperation
name|op
init|=
name|DelegationTokenOperation
operator|.
name|RENEWDELEGATIONTOKEN
decl_stmt|;
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletResponse
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSFileSystem
operator|.
name|OP_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|op
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|op
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
literal|null
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendError
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|)
argument_list|,
name|Mockito
operator|.
name|contains
argument_list|(
literal|"equires SPNEGO authentication established"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|reset
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AuthenticationToken
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
name|token
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendError
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
argument_list|,
name|Mockito
operator|.
name|contains
argument_list|(
literal|"requires the parameter [token]"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|reset
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pwriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|response
operator|.
name|getWriter
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pwriter
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|dToken
init|=
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|DelegationTokenManager
operator|.
name|class
argument_list|)
operator|.
name|createToken
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSKerberosAuthenticator
operator|.
name|TOKEN_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dToken
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|handler
operator|.
name|managementOperation
argument_list|(
name|token
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|pwriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"long"
argument_list|)
argument_list|)
expr_stmt|;
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|DelegationTokenManager
operator|.
name|class
argument_list|)
operator|.
name|verifyToken
argument_list|(
name|dToken
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestDir
DECL|method|testAuthenticate ()
specifier|public
name|void
name|testAuthenticate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dir
init|=
name|TestDirHelper
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|Configuration
name|httpfsConf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|HttpFSServerWebApp
name|server
init|=
operator|new
name|HttpFSServerWebApp
argument_list|(
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|dir
argument_list|,
name|httpfsConf
argument_list|)
decl_stmt|;
name|server
operator|.
name|setAuthority
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
argument_list|,
literal|14000
argument_list|)
argument_list|)
expr_stmt|;
name|AuthenticationHandler
name|handler
init|=
operator|new
name|HttpFSKerberosAuthenticationHandlerForTesting
argument_list|()
decl_stmt|;
try|try
block|{
name|server
operator|.
name|init
argument_list|()
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|testValidDelegationToken
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|testInvalidDelegationToken
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testValidDelegationToken (AuthenticationHandler handler)
specifier|private
name|void
name|testValidDelegationToken
parameter_list|(
name|AuthenticationHandler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletResponse
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|dToken
init|=
name|HttpFSServerWebApp
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|DelegationTokenManager
operator|.
name|class
argument_list|)
operator|.
name|createToken
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSKerberosAuthenticator
operator|.
name|DELEGATION_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dToken
operator|.
name|encodeToUrlString
argument_list|()
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|token
operator|.
name|getExpires
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpFSKerberosAuthenticationHandler
operator|.
name|TYPE
argument_list|,
name|token
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|token
operator|.
name|isExpired
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidDelegationToken (AuthenticationHandler handler)
specifier|private
name|void
name|testInvalidDelegationToken
parameter_list|(
name|AuthenticationHandler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpServletRequest
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|HttpServletResponse
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HttpServletResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|HttpFSKerberosAuthenticator
operator|.
name|DELEGATION_PARAM
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"invalid"
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
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
name|AuthenticationException
name|ex
parameter_list|)
block|{
comment|//NOP
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

