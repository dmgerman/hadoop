begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

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
name|hadoop
operator|.
name|security
operator|.
name|authorize
operator|.
name|AuthorizationException
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
name|ProxyUsers
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
name|utils
operator|.
name|URLEncodedUtils
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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

begin_comment
comment|/**  * Extend the function of {@link AuthenticationFilter} to  * support authorizing proxy user. If the query string  * contains doAs parameter, then check the proxy user,  * otherwise do the next filter.  */
end_comment

begin_class
DECL|class|AuthenticationWithProxyUserFilter
specifier|public
class|class
name|AuthenticationWithProxyUserFilter
extends|extends
name|AuthenticationFilter
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AuthenticationWithProxyUserFilter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Constant used in URL's query string to perform a proxy user request, the    * value of the<code>DO_AS</code> parameter is the user the request will be    * done on behalf of.    */
DECL|field|DO_AS
specifier|private
specifier|static
specifier|final
name|String
name|DO_AS
init|=
literal|"doAs"
decl_stmt|;
DECL|field|UTF8_CHARSET
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF8_CHARSET
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
comment|/**    * This method provide the ability to do pre/post tasks    * in filter chain. Override this method to authorize    * proxy user between AuthenticationFilter and next filter.    * @param filterChain the filter chain object.    * @param request the request object.    * @param response the response object.    *    * @throws IOException    * @throws ServletException    */
annotation|@
name|Override
DECL|method|doFilter (FilterChain filterChain, HttpServletRequest request, HttpServletResponse response)
specifier|protected
name|void
name|doFilter
parameter_list|(
name|FilterChain
name|filterChain
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
specifier|final
name|String
name|proxyUser
init|=
name|getDoAs
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|proxyUser
operator|!=
literal|null
condition|)
block|{
comment|// Change the remote user after proxy user is authorized.
specifier|final
name|HttpServletRequest
name|finalReq
init|=
name|request
decl_stmt|;
name|request
operator|=
operator|new
name|HttpServletRequestWrapper
argument_list|(
name|finalReq
argument_list|)
block|{
specifier|private
name|String
name|getRemoteOrProxyUser
parameter_list|()
throws|throws
name|AuthorizationException
block|{
name|UserGroupInformation
name|realUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|finalReq
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|proxyUserInfo
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|proxyUser
argument_list|,
name|realUser
argument_list|)
decl_stmt|;
name|ProxyUsers
operator|.
name|authorize
argument_list|(
name|proxyUserInfo
argument_list|,
name|finalReq
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|proxyUserInfo
operator|.
name|getUserName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRemoteUser
parameter_list|()
block|{
try|try
block|{
return|return
name|getRemoteOrProxyUser
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|AuthorizationException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to verify proxy user: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
expr_stmt|;
block|}
name|filterChain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get proxy user from query string.    * @param request the request object    * @return proxy user    */
DECL|method|getDoAs (HttpServletRequest request)
specifier|public
specifier|static
name|String
name|getDoAs
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|queryString
init|=
name|request
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryString
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|NameValuePair
argument_list|>
name|list
init|=
name|URLEncodedUtils
operator|.
name|parse
argument_list|(
name|queryString
argument_list|,
name|UTF8_CHARSET
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|NameValuePair
name|nv
range|:
name|list
control|)
block|{
if|if
condition|(
name|DO_AS
operator|.
name|equalsIgnoreCase
argument_list|(
name|nv
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|nv
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

