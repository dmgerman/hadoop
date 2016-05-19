begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy
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
package|;
end_package

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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
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
name|PrintWriter
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
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
name|EnumSet
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
name|HashSet
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
name|Set
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|UriBuilder
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
name|io
operator|.
name|IOUtils
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|api
operator|.
name|records
operator|.
name|ApplicationReport
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
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|ApplicationNotFoundException
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
name|exceptions
operator|.
name|YarnException
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
name|AppReportFetcher
operator|.
name|AppReportSource
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
name|AppReportFetcher
operator|.
name|FetchedAppReport
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
name|util
operator|.
name|Apps
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
name|util
operator|.
name|StringHelper
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
name|util
operator|.
name|TrackingUriPlugin
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
name|webapp
operator|.
name|MimeType
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
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
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
name|webapp
operator|.
name|util
operator|.
name|WebAppUtils
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
name|Header
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
name|NameValuePair
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
name|HttpPut
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
name|HttpRequestBase
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
name|params
operator|.
name|ClientPNames
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
name|params
operator|.
name|CookiePolicy
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
name|utils
operator|.
name|URLEncodedUtils
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
name|conn
operator|.
name|params
operator|.
name|ConnRoutePNames
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
name|StringEntity
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
name|DefaultHttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|WebAppProxyServlet
specifier|public
class|class
name|WebAppProxyServlet
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|WebAppProxyServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|passThroughHeaders
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|passThroughHeaders
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"User-Agent"
argument_list|,
literal|"Accept"
argument_list|,
literal|"Accept-Encoding"
argument_list|,
literal|"Accept-Language"
argument_list|,
literal|"Accept-Charset"
argument_list|,
literal|"Content-Type"
argument_list|,
literal|"Origin"
argument_list|,
literal|"Access-Control-Request-Method"
argument_list|,
literal|"Access-Control-Request-Headers"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|PROXY_USER_COOKIE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_USER_COOKIE_NAME
init|=
literal|"proxy-user"
decl_stmt|;
DECL|field|trackingUriPlugins
specifier|private
specifier|transient
name|List
argument_list|<
name|TrackingUriPlugin
argument_list|>
name|trackingUriPlugins
decl_stmt|;
DECL|field|rmAppPageUrlBase
specifier|private
specifier|final
name|String
name|rmAppPageUrlBase
decl_stmt|;
DECL|field|ahsAppPageUrlBase
specifier|private
specifier|final
name|String
name|ahsAppPageUrlBase
decl_stmt|;
DECL|field|conf
specifier|private
specifier|transient
name|YarnConfiguration
name|conf
decl_stmt|;
comment|/**    * HTTP methods.    */
DECL|enum|HTTP
DECL|enumConstant|GET
DECL|enumConstant|POST
DECL|enumConstant|HEAD
DECL|enumConstant|PUT
DECL|enumConstant|DELETE
specifier|private
enum|enum
name|HTTP
block|{
name|GET
block|,
name|POST
block|,
name|HEAD
block|,
name|PUT
block|,
name|DELETE
block|}
empty_stmt|;
comment|/**    * Empty Hamlet class.    */
DECL|class|_
specifier|private
specifier|static
class|class
name|_
implements|implements
name|Hamlet
operator|.
name|_
block|{
comment|//Empty
block|}
DECL|class|Page
specifier|private
specifier|static
class|class
name|Page
extends|extends
name|Hamlet
block|{
DECL|method|Page (PrintWriter out)
name|Page
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|html ()
specifier|public
name|HTML
argument_list|<
name|WebAppProxyServlet
operator|.
name|_
argument_list|>
name|html
parameter_list|()
block|{
return|return
operator|new
name|HTML
argument_list|<>
argument_list|(
literal|"html"
argument_list|,
literal|null
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|EOpt
operator|.
name|ENDTAG
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Default constructor    */
DECL|method|WebAppProxyServlet ()
specifier|public
name|WebAppProxyServlet
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|this
operator|.
name|trackingUriPlugins
operator|=
name|conf
operator|.
name|getInstances
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_TRACKING_URL_GENERATOR
argument_list|,
name|TrackingUriPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmAppPageUrlBase
operator|=
name|StringHelper
operator|.
name|pjoin
argument_list|(
name|WebAppUtils
operator|.
name|getResolvedRMWebAppURLWithScheme
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"cluster"
argument_list|,
literal|"app"
argument_list|)
expr_stmt|;
name|this
operator|.
name|ahsAppPageUrlBase
operator|=
name|StringHelper
operator|.
name|pjoin
argument_list|(
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
operator|+
name|WebAppUtils
operator|.
name|getAHSWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"applicationhistory"
argument_list|,
literal|"app"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Output 404 with appropriate message.    * @param resp the http response.    * @param message the message to include on the page.    * @throws IOException on any error.    */
DECL|method|notFound (HttpServletResponse resp, String message)
specifier|private
specifier|static
name|void
name|notFound
parameter_list|(
name|HttpServletResponse
name|resp
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|ProxyUtils
operator|.
name|notFound
argument_list|(
name|resp
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Warn the user that the link may not be safe!    * @param resp the http response    * @param link the link to point to    * @param user the user that owns the link.    * @throws IOException on any error.    */
DECL|method|warnUserPage (HttpServletResponse resp, String link, String user, ApplicationId id)
specifier|private
specifier|static
name|void
name|warnUserPage
parameter_list|(
name|HttpServletResponse
name|resp
parameter_list|,
name|String
name|link
parameter_list|,
name|String
name|user
parameter_list|,
name|ApplicationId
name|id
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Set the cookie when we warn which overrides the query parameter
comment|//This is so that if a user passes in the approved query parameter without
comment|//having first visited this page then this page will still be displayed
name|resp
operator|.
name|addCookie
argument_list|(
name|makeCheckCookie
argument_list|(
name|id
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|HTML
argument_list|)
expr_stmt|;
name|Page
name|p
init|=
operator|new
name|Page
argument_list|(
name|resp
operator|.
name|getWriter
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|html
argument_list|()
operator|.
name|h1
argument_list|(
literal|"WARNING: The following page may not be safe!"
argument_list|)
operator|.
name|h3
argument_list|()
operator|.
name|_
argument_list|(
literal|"click "
argument_list|)
operator|.
name|a
argument_list|(
name|link
argument_list|,
literal|"here"
argument_list|)
operator|.
name|_
argument_list|(
literal|" to continue to an Application Master web interface owned by "
argument_list|,
name|user
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
comment|/**    * Download link and have it be the response.    * @param req the http request    * @param resp the http response    * @param link the link to download    * @param c the cookie to set if any    * @param proxyHost the proxy host    * @param method the http method    * @throws IOException on any error.    */
DECL|method|proxyLink (final HttpServletRequest req, final HttpServletResponse resp, final URI link, final Cookie c, final String proxyHost, final HTTP method)
specifier|private
specifier|static
name|void
name|proxyLink
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|,
specifier|final
name|URI
name|link
parameter_list|,
specifier|final
name|Cookie
name|c
parameter_list|,
specifier|final
name|String
name|proxyHost
parameter_list|,
specifier|final
name|HTTP
name|method
parameter_list|)
throws|throws
name|IOException
block|{
name|DefaultHttpClient
name|client
init|=
operator|new
name|DefaultHttpClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|getParams
argument_list|()
operator|.
name|setParameter
argument_list|(
name|ClientPNames
operator|.
name|COOKIE_POLICY
argument_list|,
name|CookiePolicy
operator|.
name|BROWSER_COMPATIBILITY
argument_list|)
operator|.
name|setBooleanParameter
argument_list|(
name|ClientPNames
operator|.
name|ALLOW_CIRCULAR_REDIRECTS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Make sure we send the request from the proxy address in the config
comment|// since that is what the AM filter checks against. IP aliasing or
comment|// similar could cause issues otherwise.
name|InetAddress
name|localAddress
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|proxyHost
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"local InetAddress for proxy host: {}"
argument_list|,
name|localAddress
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|getParams
argument_list|()
operator|.
name|setParameter
argument_list|(
name|ConnRoutePNames
operator|.
name|LOCAL_ADDRESS
argument_list|,
name|localAddress
argument_list|)
expr_stmt|;
name|HttpRequestBase
name|base
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
name|HTTP
operator|.
name|GET
argument_list|)
condition|)
block|{
name|base
operator|=
operator|new
name|HttpGet
argument_list|(
name|link
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
name|HTTP
operator|.
name|PUT
argument_list|)
condition|)
block|{
name|base
operator|=
operator|new
name|HttpPut
argument_list|(
name|link
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|req
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|HttpPut
operator|)
name|base
operator|)
operator|.
name|setEntity
argument_list|(
operator|new
name|StringEntity
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_METHOD_NOT_ALLOWED
argument_list|)
expr_stmt|;
return|return;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Enumeration
argument_list|<
name|String
argument_list|>
name|names
init|=
name|req
operator|.
name|getHeaderNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|passThroughHeaders
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|req
operator|.
name|getHeader
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"REQ HEADER: {} : {}"
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|base
operator|.
name|setHeader
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|user
init|=
name|req
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
operator|!
name|user
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|base
operator|.
name|setHeader
argument_list|(
literal|"Cookie"
argument_list|,
name|PROXY_USER_COOKIE_NAME
operator|+
literal|"="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|user
argument_list|,
literal|"ASCII"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|OutputStream
name|out
init|=
name|resp
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|HttpResponse
name|httpResp
init|=
name|client
operator|.
name|execute
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|resp
operator|.
name|setStatus
argument_list|(
name|httpResp
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Header
name|header
range|:
name|httpResp
operator|.
name|getAllHeaders
argument_list|()
control|)
block|{
name|resp
operator|.
name|setHeader
argument_list|(
name|header
operator|.
name|getName
argument_list|()
argument_list|,
name|header
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|resp
operator|.
name|addCookie
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|in
init|=
name|httpResp
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
literal|4096
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|base
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getCheckCookieName (ApplicationId id)
specifier|private
specifier|static
name|String
name|getCheckCookieName
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
block|{
return|return
literal|"checked_"
operator|+
name|id
return|;
block|}
DECL|method|makeCheckCookie (ApplicationId id, boolean isSet)
specifier|private
specifier|static
name|Cookie
name|makeCheckCookie
parameter_list|(
name|ApplicationId
name|id
parameter_list|,
name|boolean
name|isSet
parameter_list|)
block|{
name|Cookie
name|c
init|=
operator|new
name|Cookie
argument_list|(
name|getCheckCookieName
argument_list|(
name|id
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|isSet
argument_list|)
argument_list|)
decl_stmt|;
name|c
operator|.
name|setPath
argument_list|(
name|ProxyUriUtils
operator|.
name|getPath
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|setMaxAge
argument_list|(
literal|60
operator|*
literal|60
operator|*
literal|2
argument_list|)
expr_stmt|;
comment|//2 hours in seconds
return|return
name|c
return|;
block|}
DECL|method|isSecurityEnabled ()
specifier|private
name|boolean
name|isSecurityEnabled
parameter_list|()
block|{
name|Boolean
name|b
init|=
operator|(
name|Boolean
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|WebAppProxy
operator|.
name|IS_SECURITY_ENABLED_ATTRIBUTE
argument_list|)
decl_stmt|;
return|return
name|b
operator|!=
literal|null
condition|?
name|b
else|:
literal|false
return|;
block|}
DECL|method|getApplicationReport (ApplicationId id)
specifier|private
name|FetchedAppReport
name|getApplicationReport
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
return|return
operator|(
operator|(
name|AppReportFetcher
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|WebAppProxy
operator|.
name|FETCHER_ATTRIBUTE
argument_list|)
operator|)
operator|.
name|getApplicationReport
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|getProxyHost ()
specifier|private
name|String
name|getProxyHost
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|String
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|WebAppProxy
operator|.
name|PROXY_HOST_ATTRIBUTE
argument_list|)
operator|)
return|;
block|}
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
name|methodAction
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|HTTP
operator|.
name|GET
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doPut (final HttpServletRequest req, final HttpServletResponse resp)
specifier|protected
specifier|final
name|void
name|doPut
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|methodAction
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|HTTP
operator|.
name|PUT
argument_list|)
expr_stmt|;
block|}
comment|/**    * The action against the HTTP method.    * @param req the HttpServletRequest    * @param resp the HttpServletResponse    * @param method the HTTP method    * @throws ServletException    * @throws IOException    */
DECL|method|methodAction (final HttpServletRequest req, final HttpServletResponse resp, final HTTP method)
specifier|private
name|void
name|methodAction
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|,
specifier|final
name|HTTP
name|method
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
name|String
name|userApprovedParamS
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|ProxyUriUtils
operator|.
name|PROXY_APPROVAL_PARAM
argument_list|)
decl_stmt|;
name|boolean
name|userWasWarned
init|=
literal|false
decl_stmt|;
name|boolean
name|userApproved
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|userApprovedParamS
argument_list|)
decl_stmt|;
name|boolean
name|securityEnabled
init|=
name|isSecurityEnabled
argument_list|()
decl_stmt|;
specifier|final
name|String
name|remoteUser
init|=
name|req
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
specifier|final
name|String
name|pathInfo
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
name|String
index|[]
name|parts
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|pathInfo
operator|!=
literal|null
condition|)
block|{
name|parts
operator|=
name|pathInfo
operator|.
name|split
argument_list|(
literal|"/"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parts
operator|==
literal|null
operator|||
name|parts
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} gave an invalid proxy path {}"
argument_list|,
name|remoteUser
argument_list|,
name|pathInfo
argument_list|)
expr_stmt|;
name|notFound
argument_list|(
name|resp
argument_list|,
literal|"Your path appears to be formatted incorrectly."
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//parts[0] is empty because path info always starts with a /
name|String
name|appId
init|=
name|parts
index|[
literal|1
index|]
decl_stmt|;
name|String
name|rest
init|=
name|parts
operator|.
name|length
operator|>
literal|2
condition|?
name|parts
index|[
literal|2
index|]
else|:
literal|""
decl_stmt|;
name|ApplicationId
name|id
init|=
name|Apps
operator|.
name|toAppID
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} attempting to access {} that is invalid"
argument_list|,
name|remoteUser
argument_list|,
name|appId
argument_list|)
expr_stmt|;
name|notFound
argument_list|(
name|resp
argument_list|,
name|appId
operator|+
literal|" appears to be formatted incorrectly."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|securityEnabled
condition|)
block|{
name|String
name|cookieName
init|=
name|getCheckCookieName
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Cookie
index|[]
name|cookies
init|=
name|req
operator|.
name|getCookies
argument_list|()
decl_stmt|;
if|if
condition|(
name|cookies
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Cookie
name|c
range|:
name|cookies
control|)
block|{
if|if
condition|(
name|cookieName
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|userWasWarned
operator|=
literal|true
expr_stmt|;
name|userApproved
operator|=
name|userApproved
operator|||
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
name|boolean
name|checkUser
init|=
name|securityEnabled
operator|&&
operator|(
operator|!
name|userWasWarned
operator|||
operator|!
name|userApproved
operator|)
decl_stmt|;
name|FetchedAppReport
name|fetchedAppReport
init|=
literal|null
decl_stmt|;
name|ApplicationReport
name|applicationReport
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fetchedAppReport
operator|=
name|getApplicationReport
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|fetchedAppReport
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fetchedAppReport
operator|.
name|getAppReportSource
argument_list|()
operator|!=
name|AppReportSource
operator|.
name|RM
operator|&&
name|fetchedAppReport
operator|.
name|getAppReportSource
argument_list|()
operator|!=
name|AppReportSource
operator|.
name|AHS
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Application report not "
operator|+
literal|"fetched from RM or history server."
argument_list|)
throw|;
block|}
name|applicationReport
operator|=
name|fetchedAppReport
operator|.
name|getApplicationReport
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ApplicationNotFoundException
name|e
parameter_list|)
block|{
name|applicationReport
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|applicationReport
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} attempting to access {} that was not found"
argument_list|,
name|remoteUser
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|URI
name|toFetch
init|=
name|ProxyUriUtils
operator|.
name|getUriFromTrackingPlugins
argument_list|(
name|id
argument_list|,
name|this
operator|.
name|trackingUriPlugins
argument_list|)
decl_stmt|;
if|if
condition|(
name|toFetch
operator|!=
literal|null
condition|)
block|{
name|ProxyUtils
operator|.
name|sendRedirect
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|toFetch
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|notFound
argument_list|(
name|resp
argument_list|,
literal|"Application "
operator|+
name|appId
operator|+
literal|" could not be found "
operator|+
literal|"in RM or history server"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|original
init|=
name|applicationReport
operator|.
name|getOriginalTrackingUrl
argument_list|()
decl_stmt|;
name|URI
name|trackingUri
decl_stmt|;
if|if
condition|(
name|original
operator|==
literal|null
operator|||
name|original
operator|.
name|equals
argument_list|(
literal|"N/A"
argument_list|)
operator|||
name|original
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
if|if
condition|(
name|fetchedAppReport
operator|.
name|getAppReportSource
argument_list|()
operator|==
name|AppReportSource
operator|.
name|RM
condition|)
block|{
comment|// fallback to ResourceManager's app page if no tracking URI provided
comment|// and Application Report was fetched from RM
name|LOG
operator|.
name|debug
argument_list|(
literal|"Original tracking url is '{}'. Redirecting to RM app page"
argument_list|,
name|original
operator|==
literal|null
condition|?
literal|"NULL"
else|:
name|original
argument_list|)
expr_stmt|;
name|ProxyUtils
operator|.
name|sendRedirect
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|StringHelper
operator|.
name|pjoin
argument_list|(
name|rmAppPageUrlBase
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fetchedAppReport
operator|.
name|getAppReportSource
argument_list|()
operator|==
name|AppReportSource
operator|.
name|AHS
condition|)
block|{
comment|// fallback to Application History Server app page if the application
comment|// report was fetched from AHS
name|LOG
operator|.
name|debug
argument_list|(
literal|"Original tracking url is '{}'. Redirecting to AHS app page"
argument_list|,
name|original
operator|==
literal|null
condition|?
literal|"NULL"
else|:
name|original
argument_list|)
expr_stmt|;
name|ProxyUtils
operator|.
name|sendRedirect
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|StringHelper
operator|.
name|pjoin
argument_list|(
name|ahsAppPageUrlBase
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
else|else
block|{
if|if
condition|(
name|ProxyUriUtils
operator|.
name|getSchemeFromUrl
argument_list|(
name|original
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|trackingUri
operator|=
name|ProxyUriUtils
operator|.
name|getUriFromAMUrl
argument_list|(
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
argument_list|,
name|original
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|trackingUri
operator|=
operator|new
name|URI
argument_list|(
name|original
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|runningUser
init|=
name|applicationReport
operator|.
name|getUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|checkUser
operator|&&
operator|!
name|runningUser
operator|.
name|equals
argument_list|(
name|remoteUser
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Asking {} if they want to connect to the "
operator|+
literal|"app master GUI of {} owned by {}"
argument_list|,
name|remoteUser
argument_list|,
name|appId
argument_list|,
name|runningUser
argument_list|)
expr_stmt|;
name|warnUserPage
argument_list|(
name|resp
argument_list|,
name|ProxyUriUtils
operator|.
name|getPathAndQuery
argument_list|(
name|id
argument_list|,
name|rest
argument_list|,
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|,
name|runningUser
argument_list|,
name|id
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Append the user-provided path and query parameter to the original
comment|// tracking url.
name|UriBuilder
name|builder
init|=
name|UriBuilder
operator|.
name|fromUri
argument_list|(
name|trackingUri
argument_list|)
decl_stmt|;
name|String
name|queryString
init|=
name|req
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryString
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|queryPairs
init|=
name|URLEncodedUtils
operator|.
name|parse
argument_list|(
name|queryString
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|NameValuePair
name|pair
range|:
name|queryPairs
control|)
block|{
name|builder
operator|.
name|queryParam
argument_list|(
name|pair
operator|.
name|getName
argument_list|()
argument_list|,
name|pair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|URI
name|toFetch
init|=
name|builder
operator|.
name|path
argument_list|(
name|rest
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"{} is accessing unchecked {}"
operator|+
literal|" which is the app master GUI of {} owned by {}"
argument_list|,
name|remoteUser
argument_list|,
name|toFetch
argument_list|,
name|appId
argument_list|,
name|runningUser
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|applicationReport
operator|.
name|getYarnApplicationState
argument_list|()
condition|)
block|{
case|case
name|KILLED
case|:
case|case
name|FINISHED
case|:
case|case
name|FAILED
case|:
name|ProxyUtils
operator|.
name|sendRedirect
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|toFetch
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
default|default:
comment|// fall out of the switch
block|}
name|Cookie
name|c
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|userWasWarned
operator|&&
name|userApproved
condition|)
block|{
name|c
operator|=
name|makeCheckCookie
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|proxyLink
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|toFetch
argument_list|,
name|c
argument_list|,
name|getProxyHost
argument_list|()
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * This method is used by Java object deserialization, to fill in the    * transient {@link #trackingUriPlugins} field.    * See {@link ObjectInputStream#defaultReadObject()}    *<p>    *<I>Do not remove</I>    *<p>    * Yarn isn't currently serializing this class, but findbugs    * complains in its absence.    *     *     * @param input source    * @throws IOException IO failure    * @throws ClassNotFoundException classloader fun    */
DECL|method|readObject (ObjectInputStream input)
specifier|private
name|void
name|readObject
parameter_list|(
name|ObjectInputStream
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|input
operator|.
name|defaultReadObject
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|this
operator|.
name|trackingUriPlugins
operator|=
name|conf
operator|.
name|getInstances
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_TRACKING_URL_GENERATOR
argument_list|,
name|TrackingUriPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

