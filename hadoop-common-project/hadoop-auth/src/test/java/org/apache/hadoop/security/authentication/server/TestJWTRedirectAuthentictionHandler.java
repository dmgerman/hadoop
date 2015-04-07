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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|security
operator|.
name|KeyPair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyPairGenerator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|interfaces
operator|.
name|RSAPrivateKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|interfaces
operator|.
name|RSAPublicKey
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|Cookie
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
name|client
operator|.
name|AuthenticationException
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|com
operator|.
name|nimbusds
operator|.
name|jose
operator|.
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|nimbusds
operator|.
name|jwt
operator|.
name|JWTClaimsSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|nimbusds
operator|.
name|jwt
operator|.
name|SignedJWT
import|;
end_import

begin_import
import|import
name|com
operator|.
name|nimbusds
operator|.
name|jose
operator|.
name|crypto
operator|.
name|RSASSASigner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|nimbusds
operator|.
name|jose
operator|.
name|crypto
operator|.
name|RSASSAVerifier
import|;
end_import

begin_import
import|import
name|com
operator|.
name|nimbusds
operator|.
name|jose
operator|.
name|util
operator|.
name|Base64URL
import|;
end_import

begin_class
DECL|class|TestJWTRedirectAuthentictionHandler
specifier|public
class|class
name|TestJWTRedirectAuthentictionHandler
extends|extends
name|KerberosSecurityTestcase
block|{
DECL|field|SERVICE_URL
specifier|private
specifier|static
specifier|final
name|String
name|SERVICE_URL
init|=
literal|"https://localhost:8888/resource"
decl_stmt|;
DECL|field|REDIRECT_LOCATION
specifier|private
specifier|static
specifier|final
name|String
name|REDIRECT_LOCATION
init|=
literal|"https://localhost:8443/authserver?originalUrl="
operator|+
name|SERVICE_URL
decl_stmt|;
DECL|field|publicKey
name|RSAPublicKey
name|publicKey
init|=
literal|null
decl_stmt|;
DECL|field|privateKey
name|RSAPrivateKey
name|privateKey
init|=
literal|null
decl_stmt|;
DECL|field|handler
name|JWTRedirectAuthenticationHandler
name|handler
init|=
literal|null
decl_stmt|;
annotation|@
name|Test
DECL|method|testNoPublicKeyJWT ()
specifier|public
name|void
name|testNoPublicKeyJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"hadoop-jwt"
argument_list|,
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"alternateAuthentication should have thrown a ServletException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Public key for signature validation must be provisioned"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCustomCookieNameJWT ()
specifier|public
name|void
name|testCustomCookieNameJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|handler
operator|.
name|setPublicKey
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JWTRedirectAuthenticationHandler
operator|.
name|JWT_COOKIE_NAME
argument_list|,
literal|"jowt"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"jowt"
argument_list|,
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
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
literal|"bob"
argument_list|,
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a ServletException: "
operator|+
name|se
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNoProviderURLJWT ()
specifier|public
name|void
name|testNoProviderURLJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|handler
operator|.
name|setPublicKey
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|props
operator|.
name|remove
argument_list|(
name|JWTRedirectAuthenticationHandler
operator|.
name|AUTHENTICATION_PROVIDER_URL
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"hadoop-jwt"
argument_list|,
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"alternateAuthentication should have thrown an AuthenticationException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Authentication provider URL must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUnableToParseJWT ()
specifier|public
name|void
name|testUnableToParseJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|KeyPairGenerator
name|kpg
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|kpg
operator|.
name|initialize
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|KeyPair
name|kp
init|=
name|kpg
operator|.
name|genKeyPair
argument_list|()
decl_stmt|;
name|RSAPublicKey
name|publicKey
init|=
operator|(
name|RSAPublicKey
operator|)
name|kp
operator|.
name|getPublic
argument_list|()
decl_stmt|;
name|handler
operator|.
name|setPublicKey
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"hadoop-jwt"
argument_list|,
literal|"ljm"
operator|+
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendRedirect
argument_list|(
name|REDIRECT_LOCATION
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a ServletException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFailedSignatureValidationJWT ()
specifier|public
name|void
name|testFailedSignatureValidationJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// Create a public key that doesn't match the one needed to
comment|// verify the signature - in order to make it fail verification...
name|KeyPairGenerator
name|kpg
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|kpg
operator|.
name|initialize
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|KeyPair
name|kp
init|=
name|kpg
operator|.
name|genKeyPair
argument_list|()
decl_stmt|;
name|RSAPublicKey
name|publicKey
init|=
operator|(
name|RSAPublicKey
operator|)
name|kp
operator|.
name|getPublic
argument_list|()
decl_stmt|;
name|handler
operator|.
name|setPublicKey
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"hadoop-jwt"
argument_list|,
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendRedirect
argument_list|(
name|REDIRECT_LOCATION
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a ServletException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testExpiredJWT ()
specifier|public
name|void
name|testExpiredJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|handler
operator|.
name|setPublicKey
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|-
literal|1000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"hadoop-jwt"
argument_list|,
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendRedirect
argument_list|(
name|REDIRECT_LOCATION
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a ServletException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInvalidAudienceJWT ()
specifier|public
name|void
name|testInvalidAudienceJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|handler
operator|.
name|setPublicKey
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JWTRedirectAuthenticationHandler
operator|.
name|EXPECTED_JWT_AUDIENCES
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"hadoop-jwt"
argument_list|,
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|response
argument_list|)
operator|.
name|sendRedirect
argument_list|(
name|REDIRECT_LOCATION
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a ServletException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testValidAudienceJWT ()
specifier|public
name|void
name|testValidAudienceJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|handler
operator|.
name|setPublicKey
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|JWTRedirectAuthenticationHandler
operator|.
name|EXPECTED_JWT_AUDIENCES
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"hadoop-jwt"
argument_list|,
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
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
literal|"bob"
argument_list|,
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a ServletException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown an AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testValidJWT ()
specifier|public
name|void
name|testValidJWT
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|handler
operator|.
name|setPublicKey
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|Properties
name|props
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|SignedJWT
name|jwt
init|=
name|getJWT
argument_list|(
literal|"alice"
argument_list|,
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
literal|5000
argument_list|)
argument_list|,
name|privateKey
argument_list|)
decl_stmt|;
name|Cookie
name|cookie
init|=
operator|new
name|Cookie
argument_list|(
literal|"hadoop-jwt"
argument_list|,
name|jwt
operator|.
name|serialize
argument_list|()
argument_list|)
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
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getCookies
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Cookie
index|[]
block|{
name|cookie
block|}
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURL
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|StringBuffer
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
expr_stmt|;
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
name|response
operator|.
name|encodeRedirectURL
argument_list|(
name|SERVICE_URL
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|SERVICE_URL
argument_list|)
expr_stmt|;
name|AuthenticationToken
name|token
init|=
name|handler
operator|.
name|alternateAuthenticate
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
literal|"Token should not be null."
argument_list|,
name|token
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"alice"
argument_list|,
name|token
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|se
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown a ServletException."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|ae
parameter_list|)
block|{
name|fail
argument_list|(
literal|"alternateAuthentication should NOT have thrown an AuthenticationException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
throws|,
name|NoSuchAlgorithmException
block|{
name|setupKerberosRequirements
argument_list|()
expr_stmt|;
name|KeyPairGenerator
name|kpg
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
literal|"RSA"
argument_list|)
decl_stmt|;
name|kpg
operator|.
name|initialize
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|KeyPair
name|kp
init|=
name|kpg
operator|.
name|genKeyPair
argument_list|()
decl_stmt|;
name|publicKey
operator|=
operator|(
name|RSAPublicKey
operator|)
name|kp
operator|.
name|getPublic
argument_list|()
expr_stmt|;
name|privateKey
operator|=
operator|(
name|RSAPrivateKey
operator|)
name|kp
operator|.
name|getPrivate
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|JWTRedirectAuthenticationHandler
argument_list|()
expr_stmt|;
block|}
DECL|method|setupKerberosRequirements ()
specifier|protected
name|void
name|setupKerberosRequirements
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|keytabUsers
init|=
operator|new
name|String
index|[]
block|{
literal|"HTTP/host1"
block|,
literal|"HTTP/host2"
block|,
literal|"HTTP2/host1"
block|,
literal|"XHTTP/host"
block|}
decl_stmt|;
name|String
name|keytab
init|=
name|KerberosTestUtils
operator|.
name|getKeytabFile
argument_list|()
decl_stmt|;
name|getKdc
argument_list|()
operator|.
name|createPrincipal
argument_list|(
operator|new
name|File
argument_list|(
name|keytab
argument_list|)
argument_list|,
name|keytabUsers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|handler
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
DECL|method|getProperties ()
specifier|protected
name|Properties
name|getProperties
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
name|JWTRedirectAuthenticationHandler
operator|.
name|AUTHENTICATION_PROVIDER_URL
argument_list|,
literal|"https://localhost:8443/authserver"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"kerberos.principal"
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
literal|"kerberos.keytab"
argument_list|,
name|KerberosTestUtils
operator|.
name|getKeytabFile
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|props
return|;
block|}
DECL|method|getJWT (String sub, Date expires, RSAPrivateKey privateKey)
specifier|protected
name|SignedJWT
name|getJWT
parameter_list|(
name|String
name|sub
parameter_list|,
name|Date
name|expires
parameter_list|,
name|RSAPrivateKey
name|privateKey
parameter_list|)
throws|throws
name|Exception
block|{
name|JWTClaimsSet
name|claimsSet
init|=
operator|new
name|JWTClaimsSet
argument_list|()
decl_stmt|;
name|claimsSet
operator|.
name|setSubject
argument_list|(
name|sub
argument_list|)
expr_stmt|;
name|claimsSet
operator|.
name|setIssueTime
argument_list|(
operator|new
name|Date
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|claimsSet
operator|.
name|setIssuer
argument_list|(
literal|"https://c2id.com"
argument_list|)
expr_stmt|;
name|claimsSet
operator|.
name|setCustomClaim
argument_list|(
literal|"scope"
argument_list|,
literal|"openid"
argument_list|)
expr_stmt|;
name|claimsSet
operator|.
name|setExpirationTime
argument_list|(
name|expires
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|aud
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|aud
operator|.
name|add
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|claimsSet
operator|.
name|setAudience
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|JWSHeader
name|header
init|=
operator|new
name|JWSHeader
operator|.
name|Builder
argument_list|(
name|JWSAlgorithm
operator|.
name|RS256
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SignedJWT
name|signedJWT
init|=
operator|new
name|SignedJWT
argument_list|(
name|header
argument_list|,
name|claimsSet
argument_list|)
decl_stmt|;
name|Base64URL
name|sigInput
init|=
name|Base64URL
operator|.
name|encode
argument_list|(
name|signedJWT
operator|.
name|getSigningInput
argument_list|()
argument_list|)
decl_stmt|;
name|JWSSigner
name|signer
init|=
operator|new
name|RSASSASigner
argument_list|(
name|privateKey
argument_list|)
decl_stmt|;
name|signedJWT
operator|.
name|sign
argument_list|(
name|signer
argument_list|)
expr_stmt|;
return|return
name|signedJWT
return|;
block|}
block|}
end_class

end_unit

