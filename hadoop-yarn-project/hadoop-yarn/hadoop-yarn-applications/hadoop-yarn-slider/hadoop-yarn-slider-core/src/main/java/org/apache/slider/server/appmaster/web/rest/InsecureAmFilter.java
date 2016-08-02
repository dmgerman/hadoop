begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
package|;
end_package

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
name|server
operator|.
name|webproxy
operator|.
name|amfilter
operator|.
name|AmIpFilter
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
name|amfilter
operator|.
name|AmIpPrincipal
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
name|amfilter
operator|.
name|AmIpServletRequestWrapper
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

begin_comment
comment|/**  * This is a filter which is used to forward insecure operations  * There's some metrics to track all operations too  */
end_comment

begin_class
DECL|class|InsecureAmFilter
specifier|public
class|class
name|InsecureAmFilter
extends|extends
name|AmIpFilter
block|{
DECL|field|WS_CONTEXT_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|WS_CONTEXT_ROOT
init|=
literal|"slider.rest.context.root"
decl_stmt|;
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|InsecureAmFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|wsContextRoot
specifier|private
name|String
name|wsContextRoot
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
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|wsContextRoot
operator|=
name|conf
operator|.
name|getInitParameter
argument_list|(
name|WS_CONTEXT_ROOT
argument_list|)
expr_stmt|;
if|if
condition|(
name|wsContextRoot
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No value set for "
operator|+
name|WS_CONTEXT_ROOT
argument_list|)
throw|;
block|}
block|}
DECL|method|rejectNonHttpRequests (ServletRequest req)
specifier|private
name|void
name|rejectNonHttpRequests
parameter_list|(
name|ServletRequest
name|req
parameter_list|)
throws|throws
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
name|String
name|requestURI
init|=
name|httpReq
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|requestURI
operator|==
literal|null
operator|||
operator|!
name|requestURI
operator|.
name|startsWith
argument_list|(
name|wsContextRoot
argument_list|)
condition|)
block|{
comment|// hand off to the AM filter if it is not the context root
name|super
operator|.
name|doFilter
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|chain
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
name|log
operator|.
name|debug
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
block|}
end_class

end_unit

