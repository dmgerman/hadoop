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
import|import
name|org
operator|.
name|apache
operator|.
name|catalina
operator|.
name|deploy
operator|.
name|FilterDef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|catalina
operator|.
name|deploy
operator|.
name|FilterMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|catalina
operator|.
name|startup
operator|.
name|Tomcat
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
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|auth
operator|.
name|AuthScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|auth
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|CredentialsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpUriRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|InputStreamEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|auth
operator|.
name|SPNegoScheme
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|BasicCredentialsProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClientBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|FilterHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
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
name|HttpServletResponse
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
name|ByteArrayInputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|Writer
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
name|ServerSocket
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
name|Principal
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
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|AuthenticatorTestCase
specifier|public
class|class
name|AuthenticatorTestCase
block|{
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|host
specifier|private
name|String
name|host
init|=
literal|null
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|useTomcat
specifier|private
name|boolean
name|useTomcat
init|=
literal|false
decl_stmt|;
DECL|field|tomcat
specifier|private
name|Tomcat
name|tomcat
init|=
literal|null
decl_stmt|;
DECL|field|context
name|Context
name|context
decl_stmt|;
DECL|field|authenticatorConfig
specifier|private
specifier|static
name|Properties
name|authenticatorConfig
decl_stmt|;
DECL|method|AuthenticatorTestCase ()
specifier|public
name|AuthenticatorTestCase
parameter_list|()
block|{}
DECL|method|AuthenticatorTestCase (boolean useTomcat)
specifier|public
name|AuthenticatorTestCase
parameter_list|(
name|boolean
name|useTomcat
parameter_list|)
block|{
name|this
operator|.
name|useTomcat
operator|=
name|useTomcat
expr_stmt|;
block|}
DECL|method|setAuthenticationHandlerConfig (Properties config)
specifier|protected
specifier|static
name|void
name|setAuthenticationHandlerConfig
parameter_list|(
name|Properties
name|config
parameter_list|)
block|{
name|authenticatorConfig
operator|=
name|config
expr_stmt|;
block|}
DECL|class|TestFilter
specifier|public
specifier|static
class|class
name|TestFilter
extends|extends
name|AuthenticationFilter
block|{
annotation|@
name|Override
DECL|method|getConfiguration (String configPrefix, FilterConfig filterConfig)
specifier|protected
name|Properties
name|getConfiguration
parameter_list|(
name|String
name|configPrefix
parameter_list|,
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|ServletException
block|{
return|return
name|authenticatorConfig
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|TestServlet
specifier|public
specifier|static
class|class
name|TestServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doPost (HttpServletRequest req, HttpServletResponse resp)
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|InputStream
name|is
init|=
name|req
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|OutputStream
name|os
init|=
name|resp
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|int
name|c
init|=
name|is
operator|.
name|read
argument_list|()
decl_stmt|;
while|while
condition|(
name|c
operator|>
operator|-
literal|1
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLocalPort ()
specifier|protected
name|int
name|getLocalPort
parameter_list|()
throws|throws
name|Exception
block|{
name|ServerSocket
name|ss
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|ret
init|=
name|ss
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|start ()
specifier|protected
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|useTomcat
condition|)
name|startTomcat
argument_list|()
expr_stmt|;
else|else
name|startJetty
argument_list|()
expr_stmt|;
block|}
DECL|method|startJetty ()
specifier|protected
name|void
name|startJetty
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|=
operator|new
name|Server
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|context
operator|=
operator|new
name|Context
argument_list|()
expr_stmt|;
name|context
operator|.
name|setContextPath
argument_list|(
literal|"/foo"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|addFilter
argument_list|(
operator|new
name|FilterHolder
argument_list|(
name|TestFilter
operator|.
name|class
argument_list|)
argument_list|,
literal|"/*"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|context
operator|.
name|addServlet
argument_list|(
operator|new
name|ServletHolder
argument_list|(
name|TestServlet
operator|.
name|class
argument_list|)
argument_list|,
literal|"/bar"
argument_list|)
expr_stmt|;
name|host
operator|=
literal|"localhost"
expr_stmt|;
name|port
operator|=
name|getLocalPort
argument_list|()
expr_stmt|;
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running embedded servlet container at: http://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
argument_list|)
expr_stmt|;
block|}
DECL|method|startTomcat ()
specifier|protected
name|void
name|startTomcat
parameter_list|()
throws|throws
name|Exception
block|{
name|tomcat
operator|=
operator|new
name|Tomcat
argument_list|()
expr_stmt|;
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|catalina
operator|.
name|Context
name|ctx
init|=
name|tomcat
operator|.
name|addContext
argument_list|(
literal|"/foo"
argument_list|,
name|base
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|FilterDef
name|fd
init|=
operator|new
name|FilterDef
argument_list|()
decl_stmt|;
name|fd
operator|.
name|setFilterClass
argument_list|(
name|TestFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fd
operator|.
name|setFilterName
argument_list|(
literal|"TestFilter"
argument_list|)
expr_stmt|;
name|FilterMap
name|fm
init|=
operator|new
name|FilterMap
argument_list|()
decl_stmt|;
name|fm
operator|.
name|setFilterName
argument_list|(
literal|"TestFilter"
argument_list|)
expr_stmt|;
name|fm
operator|.
name|addURLPattern
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|fm
operator|.
name|addServletName
argument_list|(
literal|"/bar"
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|addFilterDef
argument_list|(
name|fd
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|addFilterMap
argument_list|(
name|fm
argument_list|)
expr_stmt|;
name|tomcat
operator|.
name|addServlet
argument_list|(
name|ctx
argument_list|,
literal|"/bar"
argument_list|,
name|TestServlet
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|addServletMapping
argument_list|(
literal|"/bar"
argument_list|,
literal|"/bar"
argument_list|)
expr_stmt|;
name|host
operator|=
literal|"localhost"
expr_stmt|;
name|port
operator|=
name|getLocalPort
argument_list|()
expr_stmt|;
name|tomcat
operator|.
name|setHostname
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|tomcat
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|tomcat
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|protected
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|useTomcat
condition|)
name|stopTomcat
argument_list|()
expr_stmt|;
else|else
name|stopJetty
argument_list|()
expr_stmt|;
block|}
DECL|method|stopJetty ()
specifier|protected
name|void
name|stopJetty
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
try|try
block|{
name|server
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
block|}
DECL|method|stopTomcat ()
specifier|protected
name|void
name|stopTomcat
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|tomcat
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
try|try
block|{
name|tomcat
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
block|}
DECL|method|getBaseURL ()
specifier|protected
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
literal|"http://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
operator|+
literal|"/foo/bar"
return|;
block|}
DECL|class|TestConnectionConfigurator
specifier|private
specifier|static
class|class
name|TestConnectionConfigurator
implements|implements
name|ConnectionConfigurator
block|{
DECL|field|invoked
name|boolean
name|invoked
decl_stmt|;
annotation|@
name|Override
DECL|method|configure (HttpURLConnection conn)
specifier|public
name|HttpURLConnection
name|configure
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|invoked
operator|=
literal|true
expr_stmt|;
return|return
name|conn
return|;
block|}
block|}
DECL|field|POST
specifier|private
name|String
name|POST
init|=
literal|"test"
decl_stmt|;
DECL|method|_testAuthentication (Authenticator authenticator, boolean doPost)
specifier|protected
name|void
name|_testAuthentication
parameter_list|(
name|Authenticator
name|authenticator
parameter_list|,
name|boolean
name|doPost
parameter_list|)
throws|throws
name|Exception
block|{
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
name|AuthenticatedURL
operator|.
name|Token
name|token
init|=
operator|new
name|AuthenticatedURL
operator|.
name|Token
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|token
operator|.
name|isSet
argument_list|()
argument_list|)
expr_stmt|;
name|TestConnectionConfigurator
name|connConf
init|=
operator|new
name|TestConnectionConfigurator
argument_list|()
decl_stmt|;
name|AuthenticatedURL
name|aUrl
init|=
operator|new
name|AuthenticatedURL
argument_list|(
name|authenticator
argument_list|,
name|connConf
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|conn
init|=
name|aUrl
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|connConf
operator|.
name|invoked
argument_list|)
expr_stmt|;
name|String
name|tokenStr
init|=
name|token
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|doPost
condition|)
block|{
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
if|if
condition|(
name|doPost
condition|)
block|{
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|conn
operator|.
name|getOutputStream
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|POST
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|doPost
condition|)
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|echo
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|POST
argument_list|,
name|echo
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|aUrl
operator|=
operator|new
name|AuthenticatedURL
argument_list|()
expr_stmt|;
name|conn
operator|=
name|aUrl
operator|.
name|openConnection
argument_list|(
name|url
argument_list|,
name|token
argument_list|)
expr_stmt|;
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
name|HTTP_OK
argument_list|,
name|conn
operator|.
name|getResponseCode
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|tokenStr
argument_list|,
name|token
operator|.
name|toString
argument_list|()
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
DECL|method|getHttpClient ()
specifier|private
name|HttpClient
name|getHttpClient
parameter_list|()
block|{
name|HttpClientBuilder
name|builder
init|=
name|HttpClientBuilder
operator|.
name|create
argument_list|()
decl_stmt|;
comment|// Register auth schema
name|builder
operator|.
name|setDefaultAuthSchemeRegistry
argument_list|(
name|s
lambda|->
name|httpContext
lambda|->
operator|new
name|SPNegoScheme
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Credentials
name|useJaasCreds
init|=
operator|new
name|Credentials
argument_list|()
block|{
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|CredentialsProvider
name|jaasCredentialProvider
init|=
operator|new
name|BasicCredentialsProvider
argument_list|()
decl_stmt|;
name|jaasCredentialProvider
operator|.
name|setCredentials
argument_list|(
name|AuthScope
operator|.
name|ANY
argument_list|,
name|useJaasCreds
argument_list|)
expr_stmt|;
comment|// Set credential provider
name|builder
operator|.
name|setDefaultCredentialsProvider
argument_list|(
name|jaasCredentialProvider
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|doHttpClientRequest (HttpClient httpClient, HttpUriRequest request)
specifier|private
name|void
name|doHttpClientRequest
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|HttpUriRequest
name|request
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpResponse
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|response
operator|=
name|httpClient
operator|.
name|execute
argument_list|(
name|request
argument_list|)
expr_stmt|;
specifier|final
name|int
name|httpStatus
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|HttpURLConnection
operator|.
name|HTTP_OK
argument_list|,
name|httpStatus
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|_testAuthenticationHttpClient (Authenticator authenticator, boolean doPost)
specifier|protected
name|void
name|_testAuthenticationHttpClient
parameter_list|(
name|Authenticator
name|authenticator
parameter_list|,
name|boolean
name|doPost
parameter_list|)
throws|throws
name|Exception
block|{
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|HttpClient
name|httpClient
init|=
name|getHttpClient
argument_list|()
decl_stmt|;
name|doHttpClientRequest
argument_list|(
name|httpClient
argument_list|,
operator|new
name|HttpGet
argument_list|(
name|getBaseURL
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Always do a GET before POST to trigger the SPNego negotiation
if|if
condition|(
name|doPost
condition|)
block|{
name|HttpPost
name|post
init|=
operator|new
name|HttpPost
argument_list|(
name|getBaseURL
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|postBytes
init|=
name|POST
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bis
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|postBytes
argument_list|)
decl_stmt|;
name|InputStreamEntity
name|entity
init|=
operator|new
name|InputStreamEntity
argument_list|(
name|bis
argument_list|,
name|postBytes
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// Important that the entity is not repeatable -- this means if
comment|// we have to renegotiate (e.g. b/c the cookie wasn't handled properly)
comment|// the test will fail.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|entity
operator|.
name|isRepeatable
argument_list|()
argument_list|)
expr_stmt|;
name|post
operator|.
name|setEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|doHttpClientRequest
argument_list|(
name|httpClient
argument_list|,
name|post
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

