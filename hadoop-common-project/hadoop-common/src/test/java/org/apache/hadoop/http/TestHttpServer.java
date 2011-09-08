begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Arrays
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
name|List
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
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|CountDownLatch
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
name|Executor
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
name|Executors
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
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
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
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
name|HttpServlet
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
name|HttpServletRequestWrapper
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|http
operator|.
name|HttpServer
operator|.
name|QuotingInputFilter
operator|.
name|RequestQuoter
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
name|http
operator|.
name|resource
operator|.
name|JerseyResource
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
name|Groups
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
name|ShellBasedUnixGroupsMapping
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_class
DECL|class|TestHttpServer
specifier|public
class|class
name|TestHttpServer
extends|extends
name|HttpServerFunctionalTest
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestHttpServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|server
specifier|private
specifier|static
name|HttpServer
name|server
decl_stmt|;
DECL|field|baseUrl
specifier|private
specifier|static
name|URL
name|baseUrl
decl_stmt|;
DECL|field|MAX_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_THREADS
init|=
literal|10
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|EchoMapServlet
specifier|public
specifier|static
class|class
name|EchoMapServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
init|=
name|request
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
name|SortedSet
argument_list|<
name|String
argument_list|>
name|keys
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|params
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|out
operator|.
name|print
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
index|[]
name|values
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|EchoServlet
specifier|public
specifier|static
class|class
name|EchoServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|SortedSet
argument_list|<
name|String
argument_list|>
name|sortedKeys
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|request
operator|.
name|getParameterNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|sortedKeys
operator|.
name|add
argument_list|(
name|keys
operator|.
name|nextElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|key
range|:
name|sortedKeys
control|)
block|{
name|out
operator|.
name|print
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|HtmlContentServlet
specifier|public
specifier|static
class|class
name|HtmlContentServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response )
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"hello world"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setup ()
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|HttpServer
operator|.
name|HTTP_MAX_THREADS
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|server
operator|=
name|createTestServer
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|addServlet
argument_list|(
literal|"echo"
argument_list|,
literal|"/echo"
argument_list|,
name|EchoServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|addServlet
argument_list|(
literal|"echomap"
argument_list|,
literal|"/echomap"
argument_list|,
name|EchoMapServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|addServlet
argument_list|(
literal|"htmlcontent"
argument_list|,
literal|"/htmlcontent"
argument_list|,
name|HtmlContentServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|server
operator|.
name|addJerseyResourcePackage
argument_list|(
name|JerseyResource
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"/jersey/*"
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|baseUrl
operator|=
name|getServerURL
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"HTTP server started: "
operator|+
name|baseUrl
argument_list|)
expr_stmt|;
block|}
DECL|method|cleanup ()
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/** Test the maximum number of threads cannot be exceeded. */
DECL|method|testMaxThreads ()
annotation|@
name|Test
specifier|public
name|void
name|testMaxThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|clientThreads
init|=
name|MAX_THREADS
operator|*
literal|10
decl_stmt|;
name|Executor
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|clientThreads
argument_list|)
decl_stmt|;
comment|// Run many clients to make server reach its maximum number of threads
specifier|final
name|CountDownLatch
name|ready
init|=
operator|new
name|CountDownLatch
argument_list|(
name|clientThreads
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|start
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clientThreads
condition|;
name|i
operator|++
control|)
block|{
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ready
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|start
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a:b\nc:d\n"
argument_list|,
name|readOutput
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b&c=d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|serverThreads
init|=
name|server
operator|.
name|webServer
operator|.
name|getThreadPool
argument_list|()
operator|.
name|getThreads
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"More threads are started than expected, Server Threads count: "
operator|+
name|serverThreads
argument_list|,
name|serverThreads
operator|<=
name|MAX_THREADS
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Number of threads = "
operator|+
name|serverThreads
operator|+
literal|" which is less or equal than the max = "
operator|+
name|MAX_THREADS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// Start the client threads when they are all ready
name|ready
operator|.
name|await
argument_list|()
expr_stmt|;
name|start
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testEcho ()
annotation|@
name|Test
specifier|public
name|void
name|testEcho
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a:b\nc:d\n"
argument_list|,
name|readOutput
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b&c=d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a:b\nc&lt;:d\ne:&gt;\n"
argument_list|,
name|readOutput
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b&c<=d&e=>"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test the echo map servlet that uses getParameterMap. */
DECL|method|testEchoMap ()
annotation|@
name|Test
specifier|public
name|void
name|testEchoMap
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"a:b\nc:d\n"
argument_list|,
name|readOutput
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echomap?a=b&c=d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a:b,&gt;\nc&lt;:d\n"
argument_list|,
name|readOutput
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echomap?a=b&c<=d&a=>"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testContentTypes ()
annotation|@
name|Test
specifier|public
name|void
name|testContentTypes
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Static CSS files should have text/css
name|URL
name|cssUrl
init|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/static/test.css"
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|cssUrl
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
literal|200
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/css"
argument_list|,
name|conn
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
comment|// Servlets should have text/plain with proper encoding by default
name|URL
name|servletUrl
init|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b"
argument_list|)
decl_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|servletUrl
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/plain; charset=utf-8"
argument_list|,
name|conn
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
comment|// We should ignore parameters for mime types - ie a parameter
comment|// ending in .css should not change mime type
name|servletUrl
operator|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/echo?a=b.css"
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|servletUrl
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/plain; charset=utf-8"
argument_list|,
name|conn
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
comment|// Servlets that specify text/html should get that content type
name|servletUrl
operator|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/htmlcontent"
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|servletUrl
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html; charset=utf-8"
argument_list|,
name|conn
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
comment|// JSPs should default to text/html with utf8
name|servletUrl
operator|=
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/testjsp.jsp"
argument_list|)
expr_stmt|;
name|conn
operator|=
operator|(
name|HttpURLConnection
operator|)
name|servletUrl
operator|.
name|openConnection
argument_list|()
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html; charset=utf-8"
argument_list|,
name|conn
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Dummy filter that mimics as an authentication filter. Obtains user identity    * from the request parameter user.name. Wraps around the request so that    * request.getRemoteUser() returns the user identity.    *     */
DECL|class|DummyServletFilter
specifier|public
specifier|static
class|class
name|DummyServletFilter
implements|implements
name|Filter
block|{
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{ }
annotation|@
name|Override
DECL|method|doFilter (ServletRequest request, ServletResponse response, FilterChain filterChain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|filterChain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
specifier|final
name|String
name|userName
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
name|ServletRequest
name|requestModified
init|=
operator|new
name|HttpServletRequestWrapper
argument_list|(
operator|(
name|HttpServletRequest
operator|)
name|request
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|getRemoteUser
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
block|}
decl_stmt|;
name|filterChain
operator|.
name|doFilter
argument_list|(
name|requestModified
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (FilterConfig arg0)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|arg0
parameter_list|)
throws|throws
name|ServletException
block|{ }
block|}
comment|/**    * FilterInitializer that initialized the DummyFilter.    *    */
DECL|class|DummyFilterInitializer
specifier|public
specifier|static
class|class
name|DummyFilterInitializer
extends|extends
name|FilterInitializer
block|{
DECL|method|DummyFilterInitializer ()
specifier|public
name|DummyFilterInitializer
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|initFilter (FilterContainer container, Configuration conf)
specifier|public
name|void
name|initFilter
parameter_list|(
name|FilterContainer
name|container
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|container
operator|.
name|addFilter
argument_list|(
literal|"DummyFilter"
argument_list|,
name|DummyServletFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Access a URL and get the corresponding return Http status code. The URL    * will be accessed as the passed user, by sending user.name request    * parameter.    *     * @param urlstring    * @param userName    * @return    * @throws IOException    */
DECL|method|getHttpStatusCode (String urlstring, String userName)
specifier|static
name|int
name|getHttpStatusCode
parameter_list|(
name|String
name|urlstring
parameter_list|,
name|String
name|userName
parameter_list|)
throws|throws
name|IOException
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|urlstring
operator|+
literal|"?user.name="
operator|+
name|userName
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Accessing "
operator|+
name|url
operator|+
literal|" as user "
operator|+
name|userName
argument_list|)
expr_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|connection
operator|.
name|getResponseCode
argument_list|()
return|;
block|}
comment|/**    * Custom user->group mapping service.    */
DECL|class|MyGroupsProvider
specifier|public
specifier|static
class|class
name|MyGroupsProvider
extends|extends
name|ShellBasedUnixGroupsMapping
block|{
DECL|field|mapping
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|mapping
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|clearMapping ()
specifier|static
name|void
name|clearMapping
parameter_list|()
block|{
name|mapping
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getGroups (String user)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|mapping
operator|.
name|get
argument_list|(
name|user
argument_list|)
return|;
block|}
block|}
comment|/**    * Verify the access for /logs, /stacks, /conf, /logLevel and /metrics    * servlets, when authentication filters are set, but authorization is not    * enabled.    * @throws Exception     */
annotation|@
name|Test
DECL|method|testDisabledAuthorizationOfDefaultServlets ()
specifier|public
name|void
name|testDisabledAuthorizationOfDefaultServlets
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Authorization is disabled by default
name|conf
operator|.
name|set
argument_list|(
name|HttpServer
operator|.
name|FILTER_INITIALIZER_PROPERTY
argument_list|,
name|DummyFilterInitializer
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
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_MAPPING
argument_list|,
name|MyGroupsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Groups
operator|.
name|getUserToGroupsMappingService
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|MyGroupsProvider
operator|.
name|clearMapping
argument_list|()
expr_stmt|;
name|MyGroupsProvider
operator|.
name|mapping
operator|.
name|put
argument_list|(
literal|"userA"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"groupA"
argument_list|)
argument_list|)
expr_stmt|;
name|MyGroupsProvider
operator|.
name|mapping
operator|.
name|put
argument_list|(
literal|"userB"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"groupB"
argument_list|)
argument_list|)
expr_stmt|;
name|HttpServer
name|myServer
init|=
operator|new
name|HttpServer
argument_list|(
literal|"test"
argument_list|,
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|myServer
operator|.
name|setAttribute
argument_list|(
name|HttpServer
operator|.
name|CONF_CONTEXT_ATTRIBUTE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|myServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|port
init|=
name|myServer
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|String
name|serverURL
init|=
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/"
decl_stmt|;
for|for
control|(
name|String
name|servlet
range|:
operator|new
name|String
index|[]
block|{
literal|"conf"
block|,
literal|"logs"
block|,
literal|"stacks"
block|,
literal|"logLevel"
block|,
literal|"metrics"
block|}
control|)
block|{
for|for
control|(
name|String
name|user
range|:
operator|new
name|String
index|[]
block|{
literal|"userA"
block|,
literal|"userB"
block|}
control|)
block|{
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|getHttpStatusCode
argument_list|(
name|serverURL
operator|+
name|servlet
argument_list|,
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|myServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verify the administrator access for /logs, /stacks, /conf, /logLevel and    * /metrics servlets.    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testAuthorizationOfDefaultServlets ()
specifier|public
name|void
name|testAuthorizationOfDefaultServlets
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|conf
operator|.
name|set
argument_list|(
name|HttpServer
operator|.
name|FILTER_INITIALIZER_PROPERTY
argument_list|,
name|DummyFilterInitializer
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
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_MAPPING
argument_list|,
name|MyGroupsProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Groups
operator|.
name|getUserToGroupsMappingService
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|MyGroupsProvider
operator|.
name|clearMapping
argument_list|()
expr_stmt|;
name|MyGroupsProvider
operator|.
name|mapping
operator|.
name|put
argument_list|(
literal|"userA"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"groupA"
argument_list|)
argument_list|)
expr_stmt|;
name|MyGroupsProvider
operator|.
name|mapping
operator|.
name|put
argument_list|(
literal|"userB"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"groupB"
argument_list|)
argument_list|)
expr_stmt|;
name|MyGroupsProvider
operator|.
name|mapping
operator|.
name|put
argument_list|(
literal|"userC"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"groupC"
argument_list|)
argument_list|)
expr_stmt|;
name|MyGroupsProvider
operator|.
name|mapping
operator|.
name|put
argument_list|(
literal|"userD"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"groupD"
argument_list|)
argument_list|)
expr_stmt|;
name|MyGroupsProvider
operator|.
name|mapping
operator|.
name|put
argument_list|(
literal|"userE"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"groupE"
argument_list|)
argument_list|)
expr_stmt|;
name|HttpServer
name|myServer
init|=
operator|new
name|HttpServer
argument_list|(
literal|"test"
argument_list|,
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|conf
argument_list|,
operator|new
name|AccessControlList
argument_list|(
literal|"userA,userB groupC,groupD"
argument_list|)
argument_list|)
decl_stmt|;
name|myServer
operator|.
name|setAttribute
argument_list|(
name|HttpServer
operator|.
name|CONF_CONTEXT_ATTRIBUTE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|myServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|port
init|=
name|myServer
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|String
name|serverURL
init|=
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/"
decl_stmt|;
for|for
control|(
name|String
name|servlet
range|:
operator|new
name|String
index|[]
block|{
literal|"conf"
block|,
literal|"logs"
block|,
literal|"stacks"
block|,
literal|"logLevel"
block|,
literal|"metrics"
block|}
control|)
block|{
for|for
control|(
name|String
name|user
range|:
operator|new
name|String
index|[]
block|{
literal|"userA"
block|,
literal|"userB"
block|,
literal|"userC"
block|,
literal|"userD"
block|}
control|)
block|{
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|getHttpStatusCode
argument_list|(
name|serverURL
operator|+
name|servlet
argument_list|,
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
argument_list|,
name|getHttpStatusCode
argument_list|(
name|serverURL
operator|+
name|servlet
argument_list|,
literal|"userE"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|myServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRequestQuoterWithNull ()
specifier|public
name|void
name|testRequestQuoterWithNull
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
name|doReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|when
argument_list|(
name|request
argument_list|)
operator|.
name|getParameterValues
argument_list|(
literal|"dummy"
argument_list|)
expr_stmt|;
name|RequestQuoter
name|requestQuoter
init|=
operator|new
name|RequestQuoter
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
index|[]
name|parameterValues
init|=
name|requestQuoter
operator|.
name|getParameterValues
argument_list|(
literal|"dummy"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"It should return null "
operator|+
literal|"when there are no values for the parameter"
argument_list|,
literal|null
argument_list|,
name|parameterValues
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRequestQuoterWithNotNull ()
specifier|public
name|void
name|testRequestQuoterWithNotNull
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
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"def"
block|}
decl_stmt|;
name|Mockito
operator|.
name|doReturn
argument_list|(
name|values
argument_list|)
operator|.
name|when
argument_list|(
name|request
argument_list|)
operator|.
name|getParameterValues
argument_list|(
literal|"dummy"
argument_list|)
expr_stmt|;
name|RequestQuoter
name|requestQuoter
init|=
operator|new
name|RequestQuoter
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
index|[]
name|parameterValues
init|=
name|requestQuoter
operator|.
name|getParameterValues
argument_list|(
literal|"dummy"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"It should return Parameter Values"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|values
argument_list|,
name|parameterValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|parse (String jsonString)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|parse
parameter_list|(
name|String
name|jsonString
parameter_list|)
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
name|jsonString
argument_list|)
return|;
block|}
DECL|method|testJersey ()
annotation|@
name|Test
specifier|public
name|void
name|testJersey
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"BEGIN testJersey()"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|js
init|=
name|readOutput
argument_list|(
operator|new
name|URL
argument_list|(
name|baseUrl
argument_list|,
literal|"/jersey/foo?op=bar"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
name|parse
argument_list|(
name|js
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"m="
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|JerseyResource
operator|.
name|PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|JerseyResource
operator|.
name|OP
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"END testJersey()"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

