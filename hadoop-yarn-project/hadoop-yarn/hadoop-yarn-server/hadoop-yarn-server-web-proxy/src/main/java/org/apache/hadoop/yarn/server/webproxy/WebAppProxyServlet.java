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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
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
name|commons
operator|.
name|httpclient
operator|.
name|HostConfiguration
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
name|httpclient
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
name|commons
operator|.
name|httpclient
operator|.
name|HttpMethod
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
name|httpclient
operator|.
name|cookie
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
name|commons
operator|.
name|httpclient
operator|.
name|methods
operator|.
name|GetMethod
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
name|httpclient
operator|.
name|params
operator|.
name|HttpClientParams
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
name|HashSet
argument_list|<
name|String
argument_list|>
name|passThroughHeaders
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
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
specifier|final
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
argument_list|<
name|WebAppProxyServlet
operator|.
name|_
argument_list|>
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
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
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
name|YarnConfiguration
operator|.
name|getRMWebAppURL
argument_list|(
name|conf
argument_list|)
argument_list|,
literal|"cluster"
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
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
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
name|message
argument_list|)
operator|.
name|_
argument_list|()
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
comment|/**    * Download link and have it be the response.    * @param req the http request    * @param resp the http response    * @param link the link to download    * @param c the cookie to set if any    * @throws IOException on any error.    */
DECL|method|proxyLink (HttpServletRequest req, HttpServletResponse resp, URI link, Cookie c, String proxyHost)
specifier|private
specifier|static
name|void
name|proxyLink
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|,
name|URI
name|link
parameter_list|,
name|Cookie
name|c
parameter_list|,
name|String
name|proxyHost
parameter_list|)
throws|throws
name|IOException
block|{
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|URI
name|uri
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|URI
argument_list|(
name|link
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|HttpClientParams
name|params
init|=
operator|new
name|HttpClientParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|setCookiePolicy
argument_list|(
name|CookiePolicy
operator|.
name|BROWSER_COMPATIBILITY
argument_list|)
expr_stmt|;
name|params
operator|.
name|setBooleanParameter
argument_list|(
name|HttpClientParams
operator|.
name|ALLOW_CIRCULAR_REDIRECTS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|HttpClient
name|client
init|=
operator|new
name|HttpClient
argument_list|(
name|params
argument_list|)
decl_stmt|;
comment|// Make sure we send the request from the proxy address in the config
comment|// since that is what the AM filter checks against. IP aliasing or
comment|// similar could cause issues otherwise.
name|HostConfiguration
name|config
init|=
operator|new
name|HostConfiguration
argument_list|()
decl_stmt|;
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
literal|"local InetAddress for proxy host: "
operator|+
name|localAddress
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|setLocalAddress
argument_list|(
name|localAddress
argument_list|)
expr_stmt|;
name|HttpMethod
name|method
init|=
operator|new
name|GetMethod
argument_list|(
name|uri
operator|.
name|getEscapedURI
argument_list|()
argument_list|)
decl_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"REQ HEADER: "
operator|+
name|name
operator|+
literal|" : "
operator|+
name|value
argument_list|)
expr_stmt|;
name|method
operator|.
name|setRequestHeader
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
name|method
operator|.
name|setRequestHeader
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
name|resp
operator|.
name|setStatus
argument_list|(
name|client
operator|.
name|executeMethod
argument_list|(
name|config
argument_list|,
name|method
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Header
name|header
range|:
name|method
operator|.
name|getResponseHeaders
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
name|method
operator|.
name|getResponseBodyAsStream
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
name|method
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
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
return|return
name|b
return|;
return|return
literal|false
return|;
block|}
DECL|method|getApplicationReport (ApplicationId id)
specifier|private
name|ApplicationReport
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
operator|(
name|userApprovedParamS
operator|!=
literal|null
operator|&&
name|Boolean
operator|.
name|valueOf
argument_list|(
name|userApprovedParamS
argument_list|)
operator|)
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
name|parts
index|[]
init|=
name|pathInfo
operator|.
name|split
argument_list|(
literal|"/"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
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
name|remoteUser
operator|+
literal|" Gave an invalid proxy path "
operator|+
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
name|req
operator|.
name|getRemoteUser
argument_list|()
operator|+
literal|" Attempting to access "
operator|+
name|appId
operator|+
literal|" that is invalid"
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
name|valueOf
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
name|ApplicationReport
name|applicationReport
init|=
name|getApplicationReport
argument_list|(
name|id
argument_list|)
decl_stmt|;
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
name|req
operator|.
name|getRemoteUser
argument_list|()
operator|+
literal|" Attempting to access "
operator|+
name|id
operator|+
literal|" that was not found"
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
name|resp
operator|.
name|sendRedirect
argument_list|(
name|resp
operator|.
name|encodeRedirectURL
argument_list|(
name|toFetch
operator|.
name|toString
argument_list|()
argument_list|)
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
literal|" could not be found, "
operator|+
literal|"please try the history server"
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
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|original
operator|!=
literal|null
condition|)
block|{
name|trackingUri
operator|=
name|ProxyUriUtils
operator|.
name|getUriFromAMUrl
argument_list|(
name|original
argument_list|)
expr_stmt|;
block|}
comment|// fallback to ResourceManager's app page if no tracking URI provided
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
condition|)
block|{
name|resp
operator|.
name|sendRedirect
argument_list|(
name|resp
operator|.
name|encodeRedirectURL
argument_list|(
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
argument_list|)
expr_stmt|;
return|return;
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
literal|"Asking "
operator|+
name|remoteUser
operator|+
literal|" if they want to connect to the "
operator|+
literal|"app master GUI of "
operator|+
name|appId
operator|+
literal|" owned by "
operator|+
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
name|URI
name|toFetch
init|=
operator|new
name|URI
argument_list|(
name|req
operator|.
name|getScheme
argument_list|()
argument_list|,
name|trackingUri
operator|.
name|getAuthority
argument_list|()
argument_list|,
name|StringHelper
operator|.
name|ujoin
argument_list|(
name|trackingUri
operator|.
name|getPath
argument_list|()
argument_list|,
name|rest
argument_list|)
argument_list|,
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|req
operator|.
name|getRemoteUser
argument_list|()
operator|+
literal|" is accessing unchecked "
operator|+
name|toFetch
operator|+
literal|" which is the app master GUI of "
operator|+
name|appId
operator|+
literal|" owned by "
operator|+
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
name|resp
operator|.
name|sendRedirect
argument_list|(
name|resp
operator|.
name|encodeRedirectURL
argument_list|(
name|toFetch
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
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
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
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
catch|catch
parameter_list|(
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
block|}
end_class

end_unit

