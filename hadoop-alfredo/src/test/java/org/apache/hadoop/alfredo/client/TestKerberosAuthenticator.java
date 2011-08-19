begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.alfredo.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|alfredo
operator|.
name|client
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
name|alfredo
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
name|alfredo
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
name|alfredo
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
name|alfredo
operator|.
name|server
operator|.
name|KerberosAuthenticationHandler
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

begin_class
DECL|class|TestKerberosAuthenticator
specifier|public
class|class
name|TestKerberosAuthenticator
extends|extends
name|AuthenticatorTestCase
block|{
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
DECL|method|testFallbacktoPseudoAuthenticator ()
specifier|public
name|void
name|testFallbacktoPseudoAuthenticator
parameter_list|()
throws|throws
name|Exception
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
name|setAuthenticationHandlerConfig
argument_list|(
name|props
argument_list|)
expr_stmt|;
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
DECL|method|testNotAuthenticated ()
specifier|public
name|void
name|testNotAuthenticated
parameter_list|()
throws|throws
name|Exception
block|{
name|setAuthenticationHandlerConfig
argument_list|(
name|getAuthenticationHandlerConfiguration
argument_list|()
argument_list|)
expr_stmt|;
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
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testAuthentication ()
specifier|public
name|void
name|testAuthentication
parameter_list|()
throws|throws
name|Exception
block|{
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
DECL|method|testAuthenticationPost ()
specifier|public
name|void
name|testAuthenticationPost
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
end_class

end_unit

