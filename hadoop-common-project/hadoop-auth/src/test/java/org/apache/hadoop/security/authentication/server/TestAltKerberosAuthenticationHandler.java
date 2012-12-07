begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.server
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
name|server
package|;
end_package

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
name|Properties
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestAltKerberosAuthenticationHandler
specifier|public
class|class
name|TestAltKerberosAuthenticationHandler
extends|extends
name|TestKerberosAuthenticationHandler
block|{
annotation|@
name|Override
DECL|method|getNewAuthenticationHandler ()
specifier|protected
name|KerberosAuthenticationHandler
name|getNewAuthenticationHandler
parameter_list|()
block|{
comment|// AltKerberosAuthenticationHandler is abstract; a subclass would normally
comment|// perform some other authentication when alternateAuthenticate() is called.
comment|// For the test, we'll just return an AuthenticationToken as the other
comment|// authentication is left up to the developer of the subclass
return|return
operator|new
name|AltKerberosAuthenticationHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AuthenticationToken
name|alternateAuthenticate
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|AuthenticationException
block|{
return|return
operator|new
name|AuthenticationToken
argument_list|(
literal|"A"
argument_list|,
literal|"B"
argument_list|,
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getExpectedType ()
specifier|protected
name|String
name|getExpectedType
parameter_list|()
block|{
return|return
name|AltKerberosAuthenticationHandler
operator|.
name|TYPE
return|;
block|}
DECL|method|testAlternateAuthenticationAsBrowser ()
specifier|public
name|void
name|testAlternateAuthenticationAsBrowser
parameter_list|()
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
comment|// By default, a User-Agent without "java", "curl", "wget", or "perl" in it
comment|// is considered a browser
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getHeader
argument_list|(
literal|"User-Agent"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"Some Browser"
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
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"B"
argument_list|,
name|token
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getExpectedType
argument_list|()
argument_list|,
name|token
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonDefaultNonBrowserUserAgentAsBrowser ()
specifier|public
name|void
name|testNonDefaultNonBrowserUserAgentAsBrowser
parameter_list|()
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
name|handler
operator|=
literal|null
expr_stmt|;
block|}
name|handler
operator|=
name|getNewAuthenticationHandler
argument_list|()
expr_stmt|;
name|Properties
name|props
init|=
name|getDefaultProperties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"alt-kerberos.non-browser.user-agents"
argument_list|,
literal|"foo, bar"
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|handler
operator|=
literal|null
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
comment|// Pretend we're something that will not match with "foo" (or "bar")
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getHeader
argument_list|(
literal|"User-Agent"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"blah"
argument_list|)
expr_stmt|;
comment|// Should use alt authentication
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
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"B"
argument_list|,
name|token
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getExpectedType
argument_list|()
argument_list|,
name|token
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonDefaultNonBrowserUserAgentAsNonBrowser ()
specifier|public
name|void
name|testNonDefaultNonBrowserUserAgentAsNonBrowser
parameter_list|()
throws|throws
name|Exception
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
name|handler
operator|=
literal|null
expr_stmt|;
block|}
name|handler
operator|=
name|getNewAuthenticationHandler
argument_list|()
expr_stmt|;
name|Properties
name|props
init|=
name|getDefaultProperties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"alt-kerberos.non-browser.user-agents"
argument_list|,
literal|"foo, bar"
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|handler
operator|=
literal|null
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
comment|// Run the kerberos tests again
name|testRequestWithoutAuthorization
argument_list|()
expr_stmt|;
name|testRequestWithInvalidAuthorization
argument_list|()
expr_stmt|;
name|testRequestWithAuthorization
argument_list|()
expr_stmt|;
name|testRequestWithInvalidKerberosAuthorization
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

