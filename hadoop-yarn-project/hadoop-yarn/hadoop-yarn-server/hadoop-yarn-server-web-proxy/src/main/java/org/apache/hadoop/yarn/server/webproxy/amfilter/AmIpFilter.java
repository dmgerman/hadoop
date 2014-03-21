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
name|yarn
operator|.
name|conf
operator|.
name|HAUtil
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
operator|.
name|RMHAUtils
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
specifier|static
specifier|final
name|String
name|PROXY_HOSTS
init|=
literal|"PROXY_HOSTS"
decl_stmt|;
DECL|field|PROXY_HOSTS_DELIMITER
specifier|static
specifier|final
name|String
name|PROXY_HOSTS_DELIMITER
init|=
literal|","
decl_stmt|;
DECL|field|PROXY_URI_BASES
specifier|static
specifier|final
name|String
name|PROXY_URI_BASES
init|=
literal|"PROXY_URI_BASES"
decl_stmt|;
DECL|field|PROXY_URI_BASES_DELIMITER
specifier|static
specifier|final
name|String
name|PROXY_URI_BASES_DELIMITER
init|=
literal|","
decl_stmt|;
comment|//update the proxy IP list about every 5 min
DECL|field|updateInterval
specifier|private
specifier|static
specifier|final
name|long
name|updateInterval
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
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
DECL|field|proxyUriBases
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|proxyUriBases
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
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
name|proxyUriBase
operator|+
literal|" does not appear to be a valid URL"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
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
name|System
operator|.
name|currentTimeMillis
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
operator|>=
name|now
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
literal|"proxy address is: "
operator|+
name|add
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
literal|"Could not locate "
operator|+
name|proxyHost
operator|+
literal|" - skipping"
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
if|if
condition|(
operator|!
operator|(
name|req
operator|instanceof
name|HttpServletRequest
operator|)
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"This filter only works for HTTP/HTTPS"
argument_list|)
throw|;
block|}
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
literal|"Remote address for request is: "
operator|+
name|httpReq
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|String
name|redirectUrl
init|=
name|findRedirectUrl
argument_list|()
decl_stmt|;
name|redirectUrl
operator|=
name|httpResp
operator|.
name|encodeRedirectURL
argument_list|(
name|redirectUrl
operator|+
name|httpReq
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
name|httpResp
operator|.
name|sendRedirect
argument_list|(
name|redirectUrl
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|warn
argument_list|(
literal|"Could not find "
operator|+
name|WebAppProxyServlet
operator|.
name|PROXY_USER_COOKIE_NAME
operator|+
literal|" cookie, so user will not be set"
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
specifier|final
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
DECL|method|findRedirectUrl ()
specifier|protected
name|String
name|findRedirectUrl
parameter_list|()
throws|throws
name|ServletException
block|{
name|String
name|addr
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
else|else
block|{
comment|// RM HA
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|activeRMId
init|=
name|RMHAUtils
operator|.
name|findActiveRMHAId
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|addressPropertyPrefix
init|=
name|YarnConfiguration
operator|.
name|useHttps
argument_list|(
name|conf
argument_list|)
condition|?
name|YarnConfiguration
operator|.
name|RM_WEBAPP_HTTPS_ADDRESS
else|:
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
decl_stmt|;
name|String
name|host
init|=
name|conf
operator|.
name|get
argument_list|(
name|HAUtil
operator|.
name|addSuffix
argument_list|(
name|addressPropertyPrefix
argument_list|,
name|activeRMId
argument_list|)
argument_list|)
decl_stmt|;
name|addr
operator|=
name|proxyUriBases
operator|.
name|get
argument_list|(
name|host
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

