begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.client
package|package
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
package|;
end_package

begin_import
import|import static
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
name|MultiSchemeAuthenticationHandler
operator|.
name|SCHEMES_PROPERTY
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
name|security
operator|.
name|authentication
operator|.
name|server
operator|.
name|MultiSchemeAuthenticationHandler
operator|.
name|AUTH_HANDLER_PROPERTY
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
name|security
operator|.
name|authentication
operator|.
name|server
operator|.
name|AuthenticationFilter
operator|.
name|AUTH_TYPE
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
name|security
operator|.
name|authentication
operator|.
name|server
operator|.
name|KerberosAuthenticationHandler
operator|.
name|PRINCIPAL
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
name|security
operator|.
name|authentication
operator|.
name|server
operator|.
name|KerberosAuthenticationHandler
operator|.
name|KEYTAB
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
name|security
operator|.
name|authentication
operator|.
name|server
operator|.
name|KerberosAuthenticationHandler
operator|.
name|NAME_RULES
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
name|KerberosSecurityTestcase
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
name|MultiSchemeAuthenticationHandler
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
name|Test
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
name|Properties
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

begin_comment
comment|/**  * Test class for {@link KerberosAuthenticator}.  */
end_comment

begin_class
DECL|class|TestKerberosAuthenticator
specifier|public
class|class
name|TestKerberosAuthenticator
extends|extends
name|KerberosSecurityTestcase
block|{
DECL|method|TestKerberosAuthenticator ()
specifier|public
name|TestKerberosAuthenticator
parameter_list|()
block|{   }
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create keytab
name|File
name|keytabFile
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
name|String
name|clientPrincipal
init|=
name|KerberosTestUtils
operator|.
name|getClientPrincipal
argument_list|()
decl_stmt|;
name|String
name|serverPrincipal
init|=
name|KerberosTestUtils
operator|.
name|getServerPrincipal
argument_list|()
decl_stmt|;
name|clientPrincipal
operator|=
name|clientPrincipal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|clientPrincipal
operator|.
name|lastIndexOf
argument_list|(
literal|"@"
argument_list|)
argument_list|)
expr_stmt|;
name|serverPrincipal
operator|=
name|serverPrincipal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|serverPrincipal
operator|.
name|lastIndexOf
argument_list|(
literal|"@"
argument_list|)
argument_list|)
expr_stmt|;
name|getKdc
argument_list|()
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|clientPrincipal
argument_list|,
name|serverPrincipal
argument_list|)
expr_stmt|;
block|}
DECL|method|getAuthenticationHandlerConfiguration ()
specifier|private
name|Properties
name|getAuthenticationHandlerConfiguration
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|AuthenticationFilter
operator|.
name|AUTH_TYPE
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|KerberosAuthenticationHandler
operator|.
name|PRINCIPAL
argument_list|,
name|KerberosTestUtils
operator|.
name|getServerPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|KerberosAuthenticationHandler
operator|.
name|KEYTAB
argument_list|,
name|KerberosTestUtils
operator|.
name|getKeytabFile
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|KerberosAuthenticationHandler
operator|.
name|NAME_RULES
argument_list|,
literal|"RULE:[1:$1@$0](.*@"
operator|+
name|KerberosTestUtils
operator|.
name|getRealm
argument_list|()
operator|+
literal|")s/@.*//\n"
argument_list|)
expr_stmt|;
return|return
name|props
return|;
block|}
DECL|method|getMultiAuthHandlerConfiguration ()
specifier|private
name|Properties
name|getMultiAuthHandlerConfiguration
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|AUTH_TYPE
argument_list|,
name|MultiSchemeAuthenticationHandler
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|SCHEMES_PROPERTY
argument_list|,
literal|"negotiate"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|AUTH_HANDLER_PROPERTY
argument_list|,
literal|"negotiate"
argument_list|)
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|PRINCIPAL
argument_list|,
name|KerberosTestUtils
operator|.
name|getServerPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|KEYTAB
argument_list|,
name|KerberosTestUtils
operator|.
name|getKeytabFile
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|NAME_RULES
argument_list|,
literal|"RULE:[1:$1@$0](.*@"
operator|+
name|KerberosTestUtils
operator|.
name|getRealm
argument_list|()
operator|+
literal|")s/@.*//\n"
argument_list|)
expr_stmt|;
return|return
name|props
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFallbacktoPseudoAuthenticator ()
specifier|public
name|void
name|testFallbacktoPseudoAuthenticator
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
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
name|setProperty
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|auth
operator|.
name|_testAuthentication
argument_list|(
operator|new
name|KerberosAuthenticator
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFallbacktoPseudoAuthenticatorAnonymous ()
specifier|public
name|void
name|testFallbacktoPseudoAuthenticatorAnonymous
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
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
name|setProperty
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|auth
operator|.
name|_testAuthentication
argument_list|(
operator|new
name|KerberosAuthenticator
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNotAuthenticated ()
specifier|public
name|void
name|testNotAuthenticated
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|getAuthenticationHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|auth
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|auth
operator|.
name|getBaseURL
argument_list|()
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
name|connect
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|conn
operator|.
name|getHeaderField
argument_list|(
name|KerberosAuthenticator
operator|.
name|WWW_AUTHENTICATE
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|auth
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAuthentication ()
specifier|public
name|void
name|testAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|getAuthenticationHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
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
name|auth
operator|.
name|_testAuthentication
argument_list|(
operator|new
name|KerberosAuthenticator
argument_list|()
argument_list|,
literal|false
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
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAuthenticationPost ()
specifier|public
name|void
name|testAuthenticationPost
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|getAuthenticationHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
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
name|auth
operator|.
name|_testAuthentication
argument_list|(
operator|new
name|KerberosAuthenticator
argument_list|()
argument_list|,
literal|true
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
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAuthenticationHttpClient ()
specifier|public
name|void
name|testAuthenticationHttpClient
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|getAuthenticationHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
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
name|auth
operator|.
name|_testAuthenticationHttpClient
argument_list|(
operator|new
name|KerberosAuthenticator
argument_list|()
argument_list|,
literal|false
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
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAuthenticationHttpClientPost ()
specifier|public
name|void
name|testAuthenticationHttpClientPost
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|getAuthenticationHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
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
name|auth
operator|.
name|_testAuthenticationHttpClient
argument_list|(
operator|new
name|KerberosAuthenticator
argument_list|()
argument_list|,
literal|true
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
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNotAuthenticatedWithMultiAuthHandler ()
specifier|public
name|void
name|testNotAuthenticatedWithMultiAuthHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|getMultiAuthHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|auth
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|auth
operator|.
name|getBaseURL
argument_list|()
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
name|connect
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|conn
operator|.
name|getHeaderField
argument_list|(
name|KerberosAuthenticator
operator|.
name|WWW_AUTHENTICATE
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|auth
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAuthenticationWithMultiAuthHandler ()
specifier|public
name|void
name|testAuthenticationWithMultiAuthHandler
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|getMultiAuthHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
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
name|auth
operator|.
name|_testAuthentication
argument_list|(
operator|new
name|KerberosAuthenticator
argument_list|()
argument_list|,
literal|false
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
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAuthenticationHttpClientPostWithMultiAuthHandler ()
specifier|public
name|void
name|testAuthenticationHttpClientPostWithMultiAuthHandler
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AuthenticatorTestCase
name|auth
init|=
operator|new
name|AuthenticatorTestCase
argument_list|()
decl_stmt|;
name|AuthenticatorTestCase
operator|.
name|setAuthenticationHandlerConfig
argument_list|(
name|getMultiAuthHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
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
name|auth
operator|.
name|_testAuthenticationHttpClient
argument_list|(
operator|new
name|KerberosAuthenticator
argument_list|()
argument_list|,
literal|true
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
block|}
end_class

end_unit

