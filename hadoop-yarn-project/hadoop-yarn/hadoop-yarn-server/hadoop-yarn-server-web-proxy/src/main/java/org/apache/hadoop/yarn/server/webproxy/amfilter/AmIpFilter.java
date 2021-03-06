begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|UserGroupInformation
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
name|util
operator|.
name|Time
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
name|InetAddress
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
name|net
operator|.
name|UnknownHostException
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
name|HashSet
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
name|Set
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
name|TimeUnit
import|;
end_import

begin_class
annotation|@
name|Public
DECL|class|AmIpFilter
specifier|public
class|class
name|AmIpFilter
implements|implements
name|Filter
block|{
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
name|AmIpFilter
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|PROXY_HOST
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOST
init|=
literal|"PROXY_HOST"
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|PROXY_URI_BASE
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_URI_BASE
init|=
literal|"PROXY_URI_BASE"
decl_stmt|;
DECL|field|PROXY_HOSTS
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOSTS
init|=
literal|"PROXY_HOSTS"
decl_stmt|;
DECL|field|PROXY_HOSTS_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_HOSTS_DELIMITER
init|=
literal|","
decl_stmt|;
DECL|field|PROXY_URI_BASES
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_URI_BASES
init|=
literal|"PROXY_URI_BASES"
decl_stmt|;
DECL|field|PROXY_URI_BASES_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|PROXY_URI_BASES_DELIMITER
init|=
literal|","
decl_stmt|;
DECL|field|PROXY_PATH
specifier|private
specifier|static
specifier|final
name|String
name|PROXY_PATH
init|=
literal|"/proxy"
decl_stmt|;
comment|//update the proxy IP list about every 5 min
DECL|field|updateInterval
specifier|private
specifier|static
name|long
name|updateInterval
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|field|proxyHosts
specifier|private
name|String
index|[]
name|proxyHosts
decl_stmt|;
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
DECL|field|lastUpdate
specifier|private
name|long
name|lastUpdate
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|proxyUriBases
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|proxyUriBases
decl_stmt|;
DECL|field|rmUrls
name|String
name|rmUrls
index|[]
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init (FilterConfig conf)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|conf
parameter_list|)
throws|throws
name|ServletException
block|{
comment|// Maintain for backwards compatibility
if|if
condition|(
name|conf
operator|.
name|getInitParameter
argument_list|(
name|PROXY_HOST
argument_list|)
operator|!=
literal|null
operator|&&
name|conf
operator|.
name|getInitParameter
argument_list|(
name|PROXY_URI_BASE
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|proxyHosts
operator|=
operator|new
name|String
index|[]
block|{
name|conf
operator|.
name|getInitParameter
argument_list|(
name|PROXY_HOST
argument_list|)
block|}
expr_stmt|;
name|proxyUriBases
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|proxyUriBases
operator|.
name|put
argument_list|(
literal|"dummy"
argument_list|,
name|conf
operator|.
name|getInitParameter
argument_list|(
name|PROXY_URI_BASE
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proxyHosts
operator|=
name|conf
operator|.
name|getInitParameter
argument_list|(
name|PROXY_HOSTS
argument_list|)
operator|.
name|split
argument_list|(
name|PROXY_HOSTS_DELIMITER
argument_list|)
expr_stmt|;
name|String
index|[]
name|proxyUriBasesArr
init|=
name|conf
operator|.
name|getInitParameter
argument_list|(
name|PROXY_URI_BASES
argument_list|)
operator|.
name|split
argument_list|(
name|PROXY_URI_BASES_DELIMITER
argument_list|)
decl_stmt|;
name|proxyUriBases
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|proxyUriBasesArr
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|proxyUriBase
range|:
name|proxyUriBasesArr
control|)
block|{
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|proxyUriBase
argument_list|)
decl_stmt|;
name|proxyUriBases
operator|.
name|put
argument_list|(
name|url
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|url
operator|.
name|getPort
argument_list|()
argument_list|,
name|proxyUriBase
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} does not appear to be a valid URL"
argument_list|,
name|proxyUriBase
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|conf
operator|.
name|getInitParameter
argument_list|(
name|AmFilterInitializer
operator|.
name|RM_HA_URLS
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|rmUrls
operator|=
name|conf
operator|.
name|getInitParameter
argument_list|(
name|AmFilterInitializer
operator|.
name|RM_HA_URLS
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getProxyAddresses ()
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getProxyAddresses
parameter_list|()
throws|throws
name|ServletException
block|{
name|long
name|now
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|proxyAddresses
operator|==
literal|null
operator|||
operator|(
name|lastUpdate
operator|+
name|updateInterval
operator|)
operator|<=
name|now
condition|)
block|{
name|proxyAddresses
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|proxyHost
range|:
name|proxyHosts
control|)
block|{
try|try
block|{
for|for
control|(
name|InetAddress
name|add
range|:
name|InetAddress
operator|.
name|getAllByName
argument_list|(
name|proxyHost
argument_list|)
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"proxy address is: {}"
argument_list|,
name|add
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|proxyAddresses
operator|.
name|add
argument_list|(
name|add
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|lastUpdate
operator|=
name|now
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not locate {} - skipping"
argument_list|,
name|proxyHost
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|proxyAddresses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Could not locate any of the proxy hosts"
argument_list|)
throw|;
block|}
block|}
return|return
name|proxyAddresses
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
comment|//Empty
block|}
annotation|@
name|Override
DECL|method|doFilter (ServletRequest req, ServletResponse resp, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|req
parameter_list|,
name|ServletResponse
name|resp
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|ProxyUtils
operator|.
name|rejectNonHttpRequests
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|HttpServletRequest
name|httpReq
init|=
operator|(
name|HttpServletRequest
operator|)
name|req
decl_stmt|;
name|HttpServletResponse
name|httpResp
init|=
operator|(
name|HttpServletResponse
operator|)
name|resp
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Remote address for request is: {}"
argument_list|,
name|httpReq
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getProxyAddresses
argument_list|()
operator|.
name|contains
argument_list|(
name|httpReq
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
condition|)
block|{
name|StringBuilder
name|redirect
init|=
operator|new
name|StringBuilder
argument_list|(
name|findRedirectUrl
argument_list|()
argument_list|)
decl_stmt|;
name|redirect
operator|.
name|append
argument_list|(
name|httpReq
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|insertPoint
init|=
name|redirect
operator|.
name|indexOf
argument_list|(
name|PROXY_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|insertPoint
operator|>=
literal|0
condition|)
block|{
comment|// Add /redirect as the second component of the path so that the RM web
comment|// proxy knows that this request was a redirect.
name|insertPoint
operator|+=
name|PROXY_PATH
operator|.
name|length
argument_list|()
expr_stmt|;
name|redirect
operator|.
name|insert
argument_list|(
name|insertPoint
argument_list|,
literal|"/redirect"
argument_list|)
expr_stmt|;
block|}
comment|// add the query parameters on the redirect if there were any
name|String
name|queryString
init|=
name|httpReq
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryString
operator|!=
literal|null
operator|&&
operator|!
name|queryString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|redirect
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
name|redirect
operator|.
name|append
argument_list|(
name|queryString
argument_list|)
expr_stmt|;
block|}
name|ProxyUtils
operator|.
name|sendRedirect
argument_list|(
name|httpReq
argument_list|,
name|httpResp
argument_list|,
name|redirect
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|user
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|httpReq
operator|.
name|getCookies
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Cookie
name|c
range|:
name|httpReq
operator|.
name|getCookies
argument_list|()
control|)
block|{
if|if
condition|(
name|WebAppProxyServlet
operator|.
name|PROXY_USER_COOKIE_NAME
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
name|user
operator|=
name|c
operator|.
name|getValue
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not find {} cookie, so user will not be set"
argument_list|,
name|WebAppProxyServlet
operator|.
name|PROXY_USER_COOKIE_NAME
argument_list|)
expr_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|req
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|AmIpPrincipal
name|principal
init|=
operator|new
name|AmIpPrincipal
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|ServletRequest
name|requestWrapper
init|=
operator|new
name|AmIpServletRequestWrapper
argument_list|(
name|httpReq
argument_list|,
name|principal
argument_list|)
decl_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|requestWrapper
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|findRedirectUrl ()
specifier|public
name|String
name|findRedirectUrl
parameter_list|()
throws|throws
name|ServletException
block|{
name|String
name|addr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|proxyUriBases
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// external proxy or not RM HA
name|addr
operator|=
name|proxyUriBases
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rmUrls
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|url
range|:
name|rmUrls
control|)
block|{
name|String
name|host
init|=
name|proxyUriBases
operator|.
name|get
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|isValidUrl
argument_list|(
name|host
argument_list|)
condition|)
block|{
name|addr
operator|=
name|host
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|addr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Could not determine the proxy server for redirection"
argument_list|)
throw|;
block|}
return|return
name|addr
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|isValidUrl (String url)
specifier|public
name|boolean
name|isValidUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|boolean
name|isValid
init|=
literal|false
decl_stmt|;
try|try
block|{
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
name|isValid
operator|=
name|conn
operator|.
name|getResponseCode
argument_list|()
operator|==
name|HttpURLConnection
operator|.
name|HTTP_OK
expr_stmt|;
comment|// If security is enabled, any valid RM which can give 401 Unauthorized is
comment|// good enough to access. Since AM doesn't have enough credential, auth
comment|// cannot be completed and hence 401 is fine in such case.
if|if
condition|(
operator|!
name|isValid
operator|&&
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|isValid
operator|=
operator|(
name|conn
operator|.
name|getResponseCode
argument_list|()
operator|==
name|HttpURLConnection
operator|.
name|HTTP_UNAUTHORIZED
operator|)
operator|||
operator|(
name|conn
operator|.
name|getResponseCode
argument_list|()
operator|==
name|HttpURLConnection
operator|.
name|HTTP_FORBIDDEN
operator|)
expr_stmt|;
return|return
name|isValid
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to connect to "
operator|+
name|url
operator|+
literal|": "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|isValid
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setUpdateInterval (long updateInterval)
specifier|protected
specifier|static
name|void
name|setUpdateInterval
parameter_list|(
name|long
name|updateInterval
parameter_list|)
block|{
name|AmIpFilter
operator|.
name|updateInterval
operator|=
name|updateInterval
expr_stmt|;
block|}
block|}
end_class

end_unit

