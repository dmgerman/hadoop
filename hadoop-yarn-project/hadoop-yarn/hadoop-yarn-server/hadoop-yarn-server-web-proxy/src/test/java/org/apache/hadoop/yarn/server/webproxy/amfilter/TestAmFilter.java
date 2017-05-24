begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy.amfilter
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
name|webproxy
operator|.
name|amfilter
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
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|*
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|webproxy
operator|.
name|ProxyUtils
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
name|webproxy
operator|.
name|WebAppProxyServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|glassfish
operator|.
name|grizzly
operator|.
name|servlet
operator|.
name|HttpServletResponseImpl
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

begin_comment
comment|/**  * Test AmIpFilter. Requests to a no declared hosts should has way through  * proxy. Another requests can be filtered with (without) user name.  *   */
end_comment

begin_class
DECL|class|TestAmFilter
specifier|public
class|class
name|TestAmFilter
block|{
DECL|field|proxyHost
specifier|private
name|String
name|proxyHost
init|=
literal|"localhost"
decl_stmt|;
DECL|field|proxyUri
specifier|private
name|String
name|proxyUri
init|=
literal|"http://bogus"
decl_stmt|;
DECL|field|doFilterRequest
specifier|private
name|String
name|doFilterRequest
decl_stmt|;
DECL|field|servletWrapper
specifier|private
name|AmIpServletRequestWrapper
name|servletWrapper
decl_stmt|;
DECL|class|TestAmIpFilter
specifier|private
class|class
name|TestAmIpFilter
extends|extends
name|AmIpFilter
block|{
DECL|field|proxyAddresses
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|proxyAddresses
init|=
literal|null
decl_stmt|;
DECL|method|getProxyAddresses ()
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getProxyAddresses
parameter_list|()
block|{
if|if
condition|(
name|proxyAddresses
operator|==
literal|null
condition|)
block|{
name|proxyAddresses
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|proxyAddresses
operator|.
name|add
argument_list|(
name|proxyHost
argument_list|)
expr_stmt|;
return|return
name|proxyAddresses
return|;
block|}
block|}
DECL|class|DummyFilterConfig
specifier|private
specifier|static
class|class
name|DummyFilterConfig
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
DECL|method|DummyFilterConfig (Map<String, String> map)
name|DummyFilterConfig
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
literal|"dummy"
return|;
block|}
annotation|@
name|Override
DECL|method|getInitParameter (String arg0)
specifier|public
name|String
name|getInitParameter
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|arg0
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|filterNullCookies ()
specifier|public
name|void
name|filterNullCookies
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
literal|null
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|proxyHost
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
specifier|final
name|AtomicBoolean
name|invoked
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|FilterChain
name|chain
init|=
operator|new
name|FilterChain
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|servletRequest
parameter_list|,
name|ServletResponse
name|servletResponse
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|invoked
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
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
name|params
operator|.
name|put
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOST
argument_list|,
name|proxyHost
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASE
argument_list|,
name|proxyUri
argument_list|)
expr_stmt|;
name|FilterConfig
name|conf
init|=
operator|new
name|DummyFilterConfig
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|Filter
name|filter
init|=
operator|new
name|TestAmIpFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|filter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|invoked
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|filter
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test AmIpFilter    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testFilter ()
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
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
name|params
operator|.
name|put
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_HOST
argument_list|,
name|proxyHost
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|AmIpFilter
operator|.
name|PROXY_URI_BASE
argument_list|,
name|proxyUri
argument_list|)
expr_stmt|;
name|FilterConfig
name|config
init|=
operator|new
name|DummyFilterConfig
argument_list|(
name|params
argument_list|)
decl_stmt|;
comment|// dummy filter
name|FilterChain
name|chain
init|=
operator|new
name|FilterChain
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|servletRequest
parameter_list|,
name|ServletResponse
name|servletResponse
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|doFilterRequest
operator|=
name|servletRequest
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|servletRequest
operator|instanceof
name|AmIpServletRequestWrapper
condition|)
block|{
name|servletWrapper
operator|=
operator|(
name|AmIpServletRequestWrapper
operator|)
name|servletRequest
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|AmIpFilter
name|testFilter
init|=
operator|new
name|AmIpFilter
argument_list|()
decl_stmt|;
name|testFilter
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|HttpServletResponseForTest
name|response
init|=
operator|new
name|HttpServletResponseForTest
argument_list|()
decl_stmt|;
comment|// Test request should implements HttpServletRequest
name|ServletRequest
name|failRequest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ServletRequest
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|testFilter
operator|.
name|doFilter
argument_list|(
name|failRequest
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServletException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ProxyUtils
operator|.
name|E_HTTP_HTTPS_ONLY
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// request with HttpServletRequest
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
name|getRemoteAddr
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"nowhere"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/app/application_00_0"
argument_list|)
expr_stmt|;
comment|// address "redirect" is not in host list for non-proxy connection
name|testFilter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_MOVED_TEMP
argument_list|,
name|response
operator|.
name|status
argument_list|)
expr_stmt|;
name|String
name|redirect
init|=
name|response
operator|.
name|getHeader
argument_list|(
name|ProxyUtils
operator|.
name|LOCATION
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"http://bogus/app/application_00_0"
argument_list|,
name|redirect
argument_list|)
expr_stmt|;
comment|// address "redirect" is not in host list for proxy connection
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/proxy/application_00_0"
argument_list|)
expr_stmt|;
name|testFilter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_MOVED_TEMP
argument_list|,
name|response
operator|.
name|status
argument_list|)
expr_stmt|;
name|redirect
operator|=
name|response
operator|.
name|getHeader
argument_list|(
name|ProxyUtils
operator|.
name|LOCATION
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://bogus/proxy/redirect/application_00_0"
argument_list|,
name|redirect
argument_list|)
expr_stmt|;
comment|// check for query parameters
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/proxy/application_00_0"
argument_list|)
expr_stmt|;
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
literal|"id=0"
argument_list|)
expr_stmt|;
name|testFilter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_MOVED_TEMP
argument_list|,
name|response
operator|.
name|status
argument_list|)
expr_stmt|;
name|redirect
operator|=
name|response
operator|.
name|getHeader
argument_list|(
name|ProxyUtils
operator|.
name|LOCATION
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://bogus/proxy/redirect/application_00_0?id=0"
argument_list|,
name|redirect
argument_list|)
expr_stmt|;
comment|// "127.0.0.1" contains in host list. Without cookie
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|testFilter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doFilterRequest
operator|.
name|contains
argument_list|(
literal|"javax.servlet.http.HttpServletRequest"
argument_list|)
argument_list|)
expr_stmt|;
comment|// cookie added
name|Cookie
index|[]
name|cookies
init|=
operator|new
name|Cookie
index|[]
block|{
operator|new
name|Cookie
argument_list|(
name|WebAppProxyServlet
operator|.
name|PROXY_USER_COOKIE_NAME
argument_list|,
literal|"user"
argument_list|)
block|}
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
name|cookies
argument_list|)
expr_stmt|;
name|testFilter
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"org.apache.hadoop.yarn.server.webproxy.amfilter.AmIpServletRequestWrapper"
argument_list|,
name|doFilterRequest
argument_list|)
expr_stmt|;
comment|// request contains principal from cookie
name|assertEquals
argument_list|(
literal|"user"
argument_list|,
name|servletWrapper
operator|.
name|getUserPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"user"
argument_list|,
name|servletWrapper
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|servletWrapper
operator|.
name|isUserInRole
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|HttpServletResponseForTest
specifier|private
class|class
name|HttpServletResponseForTest
extends|extends
name|HttpServletResponseImpl
block|{
DECL|field|redirectLocation
name|String
name|redirectLocation
init|=
literal|""
decl_stmt|;
DECL|field|status
name|int
name|status
decl_stmt|;
DECL|field|contentType
specifier|private
name|String
name|contentType
decl_stmt|;
DECL|field|headers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|body
specifier|private
name|StringWriter
name|body
decl_stmt|;
DECL|method|getRedirect ()
specifier|public
name|String
name|getRedirect
parameter_list|()
block|{
return|return
name|redirectLocation
return|;
block|}
annotation|@
name|Override
DECL|method|sendRedirect (String location)
specifier|public
name|void
name|sendRedirect
parameter_list|(
name|String
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|redirectLocation
operator|=
name|location
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encodeRedirectURL (String url)
specifier|public
name|String
name|encodeRedirectURL
parameter_list|(
name|String
name|url
parameter_list|)
block|{
return|return
name|url
return|;
block|}
annotation|@
name|Override
DECL|method|setStatus (int status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setContentType (String type)
specifier|public
name|void
name|setContentType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|contentType
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setHeader (String name, String value)
specifier|public
name|void
name|setHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getHeader (String name)
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|headers
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWriter ()
specifier|public
name|PrintWriter
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|body
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
return|return
operator|new
name|PrintWriter
argument_list|(
name|body
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

