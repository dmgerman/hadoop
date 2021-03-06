begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.http
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|http
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
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
name|http
operator|.
name|CrossOriginFilter
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
name|verify
import|;
end_import

begin_class
DECL|class|TestCrossOriginFilter
specifier|public
class|class
name|TestCrossOriginFilter
block|{
annotation|@
name|Test
DECL|method|testSameOrigin ()
specifier|public
name|void
name|testSameOrigin
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Origin is not specified for same origin requests
name|HttpServletRequest
name|mockReq
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
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ORIGIN
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// Objects to verify interactions based on request
name|HttpServletResponse
name|mockRes
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
name|FilterChain
name|mockChain
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|filter
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|,
name|mockChain
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verifyZeroInteractions
argument_list|(
name|mockRes
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockChain
argument_list|)
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllowAllOrigins ()
specifier|public
name|void
name|testAllowAllOrigins
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"example.com"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEncodeHeaders ()
specifier|public
name|void
name|testEncodeHeaders
parameter_list|()
block|{
name|String
name|validOrigin
init|=
literal|"http://localhost:12345"
decl_stmt|;
name|String
name|encodedValidOrigin
init|=
name|CrossOriginFilter
operator|.
name|encodeHeader
argument_list|(
name|validOrigin
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Valid origin encoding should match exactly"
argument_list|,
name|validOrigin
argument_list|,
name|encodedValidOrigin
argument_list|)
expr_stmt|;
name|String
name|httpResponseSplitOrigin
init|=
name|validOrigin
operator|+
literal|" \nSecondHeader: value"
decl_stmt|;
name|String
name|encodedResponseSplitOrigin
init|=
name|CrossOriginFilter
operator|.
name|encodeHeader
argument_list|(
name|httpResponseSplitOrigin
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Http response split origin should be protected against"
argument_list|,
name|validOrigin
argument_list|,
name|encodedResponseSplitOrigin
argument_list|)
expr_stmt|;
comment|// Test Origin List
name|String
name|validOriginList
init|=
literal|"http://foo.example.com:12345 http://bar.example.com:12345"
decl_stmt|;
name|String
name|encodedValidOriginList
init|=
name|CrossOriginFilter
operator|.
name|encodeHeader
argument_list|(
name|validOriginList
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Valid origin list encoding should match exactly"
argument_list|,
name|validOriginList
argument_list|,
name|encodedValidOriginList
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPatternMatchingOrigins ()
specifier|public
name|void
name|testPatternMatchingOrigins
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"*.example.com"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
comment|// match multiple sub-domains
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo:example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.bar.example.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// First origin is allowed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.example.com foo.nomatch.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Second origin is allowed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.nomatch.com foo.example.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// No origin in list is allowed
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.nomatch1.com foo.nomatch2.com"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegexPatternMatchingOrigins ()
specifier|public
name|void
name|testRegexPatternMatchingOrigins
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"regex:.*[.]example[.]com"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
comment|// match multiple sub-domains
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo:example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.bar.example.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// First origin is allowed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.example.com foo.nomatch.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Second origin is allowed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.nomatch.com foo.example.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// No origin in list is allowed
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.nomatch1.com foo.nomatch2.com"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComplexRegexPatternMatchingOrigins ()
specifier|public
name|void
name|testComplexRegexPatternMatchingOrigins
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"regex:https?:\\/\\/sub1[.]example[.]com(:[0-9]+)?"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"http://sub1.example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"https://sub1.example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"http://sub1.example.com:1234"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"https://sub1.example.com:8080"
argument_list|)
argument_list|)
expr_stmt|;
comment|// No origin in list is allowed
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.nomatch1.com foo.nomatch2.com"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMixedRegexPatternMatchingOrigins ()
specifier|public
name|void
name|testMixedRegexPatternMatchingOrigins
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"regex:https?:\\/\\/sub1[.]example[.]com(:[0-9]+)?, "
operator|+
literal|"*.example2.com"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"http://sub1.example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"https://sub1.example.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"http://sub1.example.com:1234"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"https://sub1.example.com:8080"
argument_list|)
argument_list|)
expr_stmt|;
comment|// match multiple sub-domains
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"example2.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo:example2.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.example2.com"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.bar.example2.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// First origin is allowed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.example2.com foo.nomatch.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Second origin is allowed
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.nomatch.com foo.example2.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|// No origin in list is allowed
name|Assert
operator|.
name|assertFalse
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"foo.nomatch1.com foo.nomatch2.com"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDisallowedOrigin ()
specifier|public
name|void
name|testDisallowedOrigin
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"example.com"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Origin is not specified for same origin requests
name|HttpServletRequest
name|mockReq
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
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ORIGIN
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"example.org"
argument_list|)
expr_stmt|;
comment|// Objects to verify interactions based on request
name|HttpServletResponse
name|mockRes
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
name|FilterChain
name|mockChain
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|filter
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|,
name|mockChain
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verifyZeroInteractions
argument_list|(
name|mockRes
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockChain
argument_list|)
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDisallowedMethod ()
specifier|public
name|void
name|testDisallowedMethod
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"example.com"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Origin is not specified for same origin requests
name|HttpServletRequest
name|mockReq
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
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ORIGIN
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"example.com"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"DISALLOWED_METHOD"
argument_list|)
expr_stmt|;
comment|// Objects to verify interactions based on request
name|HttpServletResponse
name|mockRes
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
name|FilterChain
name|mockChain
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|filter
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|,
name|mockChain
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verifyZeroInteractions
argument_list|(
name|mockRes
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockChain
argument_list|)
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDisallowedHeader ()
specifier|public
name|void
name|testDisallowedHeader
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"example.com"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Origin is not specified for same origin requests
name|HttpServletRequest
name|mockReq
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
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ORIGIN
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"example.com"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_REQUEST_HEADERS
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"Disallowed-Header"
argument_list|)
expr_stmt|;
comment|// Objects to verify interactions based on request
name|HttpServletResponse
name|mockRes
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
name|FilterChain
name|mockChain
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|filter
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|,
name|mockChain
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verifyZeroInteractions
argument_list|(
name|mockRes
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockChain
argument_list|)
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCrossOriginFilter ()
specifier|public
name|void
name|testCrossOriginFilter
parameter_list|()
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"example.com"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Origin is not specified for same origin requests
name|HttpServletRequest
name|mockReq
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
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ORIGIN
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"example.com"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockReq
operator|.
name|getHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_REQUEST_HEADERS
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"X-Requested-With"
argument_list|)
expr_stmt|;
comment|// Objects to verify interactions based on request
name|HttpServletResponse
name|mockRes
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
name|FilterChain
name|mockChain
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FilterChain
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|filter
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|,
name|mockChain
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockRes
argument_list|)
operator|.
name|setHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
literal|"example.com"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockRes
argument_list|)
operator|.
name|setHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|,
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockRes
argument_list|)
operator|.
name|setHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|,
name|filter
operator|.
name|getAllowedMethodsHeader
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockRes
argument_list|)
operator|.
name|setHeader
argument_list|(
name|CrossOriginFilter
operator|.
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|,
name|filter
operator|.
name|getAllowedHeadersHeader
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockChain
argument_list|)
operator|.
name|doFilter
argument_list|(
name|mockReq
argument_list|,
name|mockRes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCrossOriginFilterAfterRestart ()
specifier|public
name|void
name|testCrossOriginFilterAfterRestart
parameter_list|()
throws|throws
name|ServletException
block|{
comment|// Setup the configuration settings of the server
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|conf
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"example.com"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_HEADERS
argument_list|,
literal|"X-Requested-With,Accept"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_METHODS
argument_list|,
literal|"GET,POST"
argument_list|)
expr_stmt|;
name|FilterConfig
name|filterConfig
init|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Object under test
name|CrossOriginFilter
name|filter
init|=
operator|new
name|CrossOriginFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
comment|//verify filter values
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Allowed headers do not match"
argument_list|,
name|filter
operator|.
name|getAllowedHeadersHeader
argument_list|()
operator|.
name|compareTo
argument_list|(
literal|"X-Requested-With,Accept"
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Allowed methods do not match"
argument_list|,
name|filter
operator|.
name|getAllowedMethodsHeader
argument_list|()
operator|.
name|compareTo
argument_list|(
literal|"GET,POST"
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"example.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|//destroy filter values and clear conf
name|filter
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|conf
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// Setup the configuration settings of the server
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_ORIGINS
argument_list|,
literal|"newexample.com"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_HEADERS
argument_list|,
literal|"Content-Type,Origin"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|put
argument_list|(
name|CrossOriginFilter
operator|.
name|ALLOWED_METHODS
argument_list|,
literal|"GET,HEAD"
argument_list|)
expr_stmt|;
name|filterConfig
operator|=
operator|new
name|FilterConfigTest
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//initialize filter
name|filter
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
comment|//verify filter values
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Allowed headers do not match"
argument_list|,
name|filter
operator|.
name|getAllowedHeadersHeader
argument_list|()
operator|.
name|compareTo
argument_list|(
literal|"Content-Type,Origin"
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Allowed methods do not match"
argument_list|,
name|filter
operator|.
name|getAllowedMethodsHeader
argument_list|()
operator|.
name|compareTo
argument_list|(
literal|"GET,HEAD"
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|filter
operator|.
name|areOriginsAllowed
argument_list|(
literal|"newexample.com"
argument_list|)
argument_list|)
expr_stmt|;
comment|//destroy filter values
name|filter
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
DECL|class|FilterConfigTest
specifier|private
specifier|static
class|class
name|FilterConfigTest
implements|implements
name|FilterConfig
block|{
DECL|field|map
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
DECL|method|FilterConfigTest (Map<String, String> map)
name|FilterConfigTest
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilterName ()
specifier|public
name|String
name|getFilterName
parameter_list|()
block|{
return|return
literal|"test-filter"
return|;
block|}
annotation|@
name|Override
DECL|method|getInitParameter (String key)
specifier|public
name|String
name|getInitParameter
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInitParameterNames ()
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getInitParameterNames
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|enumeration
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getServletContext ()
specifier|public
name|ServletContext
name|getServletContext
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

