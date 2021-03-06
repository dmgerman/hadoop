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
name|client
operator|.
name|PseudoAuthenticator
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
DECL|class|TestPseudoAuthenticationHandler
specifier|public
class|class
name|TestPseudoAuthenticationHandler
block|{
annotation|@
name|Test
DECL|method|testInit ()
specifier|public
name|void
name|testInit
parameter_list|()
throws|throws
name|Exception
block|{
name|PseudoAuthenticationHandler
name|handler
init|=
operator|new
name|PseudoAuthenticationHandler
argument_list|()
decl_stmt|;
try|try
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
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|handler
operator|.
name|getAcceptAnonymous
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|handler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testType ()
specifier|public
name|void
name|testType
parameter_list|()
throws|throws
name|Exception
block|{
name|PseudoAuthenticationHandler
name|handler
init|=
operator|new
name|PseudoAuthenticationHandler
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|TYPE
argument_list|,
name|handler
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAnonymousOn ()
specifier|public
name|void
name|testAnonymousOn
parameter_list|()
throws|throws
name|Exception
block|{
name|PseudoAuthenticationHandler
name|handler
init|=
operator|new
name|PseudoAuthenticationHandler
argument_list|()
decl_stmt|;
try|try
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
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
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
name|AuthenticationToken
operator|.
name|ANONYMOUS
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|handler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAnonymousOff ()
specifier|public
name|void
name|testAnonymousOff
parameter_list|()
throws|throws
name|Exception
block|{
name|PseudoAuthenticationHandler
name|handler
init|=
operator|new
name|PseudoAuthenticationHandler
argument_list|()
decl_stmt|;
try|try
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
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
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
name|assertNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|handler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|_testUserName (boolean anonymous)
specifier|private
name|void
name|_testUserName
parameter_list|(
name|boolean
name|anonymous
parameter_list|)
throws|throws
name|Exception
block|{
name|PseudoAuthenticationHandler
name|handler
init|=
operator|new
name|PseudoAuthenticationHandler
argument_list|()
decl_stmt|;
try|try
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
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|anonymous
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
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
name|getQueryString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PseudoAuthenticator
operator|.
name|USER_NAME
operator|+
literal|"="
operator|+
literal|"user"
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
name|assertNotNull
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"user"
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
literal|"user"
argument_list|,
name|token
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|TYPE
argument_list|,
name|token
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|handler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUserNameAnonymousOff ()
specifier|public
name|void
name|testUserNameAnonymousOff
parameter_list|()
throws|throws
name|Exception
block|{
name|_testUserName
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUserNameAnonymousOn ()
specifier|public
name|void
name|testUserNameAnonymousOn
parameter_list|()
throws|throws
name|Exception
block|{
name|_testUserName
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

