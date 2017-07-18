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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|lang
operator|.
name|StringUtils
import|;
end_import

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
DECL|class|CrossOriginFilter
specifier|public
class|class
name|CrossOriginFilter
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
name|CrossOriginFilter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// HTTP CORS Request Headers
DECL|field|ORIGIN
specifier|static
specifier|final
name|String
name|ORIGIN
init|=
literal|"Origin"
decl_stmt|;
DECL|field|ACCESS_CONTROL_REQUEST_METHOD
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_REQUEST_METHOD
init|=
literal|"Access-Control-Request-Method"
decl_stmt|;
DECL|field|ACCESS_CONTROL_REQUEST_HEADERS
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_REQUEST_HEADERS
init|=
literal|"Access-Control-Request-Headers"
decl_stmt|;
comment|// HTTP CORS Response Headers
DECL|field|ACCESS_CONTROL_ALLOW_ORIGIN
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_ALLOW_ORIGIN
init|=
literal|"Access-Control-Allow-Origin"
decl_stmt|;
DECL|field|ACCESS_CONTROL_ALLOW_CREDENTIALS
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
init|=
literal|"Access-Control-Allow-Credentials"
decl_stmt|;
DECL|field|ACCESS_CONTROL_ALLOW_METHODS
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_ALLOW_METHODS
init|=
literal|"Access-Control-Allow-Methods"
decl_stmt|;
DECL|field|ACCESS_CONTROL_ALLOW_HEADERS
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_ALLOW_HEADERS
init|=
literal|"Access-Control-Allow-Headers"
decl_stmt|;
DECL|field|ACCESS_CONTROL_MAX_AGE
specifier|static
specifier|final
name|String
name|ACCESS_CONTROL_MAX_AGE
init|=
literal|"Access-Control-Max-Age"
decl_stmt|;
comment|// Filter configuration
DECL|field|ALLOWED_ORIGINS
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_ORIGINS
init|=
literal|"allowed-origins"
decl_stmt|;
DECL|field|ALLOWED_ORIGINS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_ORIGINS_DEFAULT
init|=
literal|"*"
decl_stmt|;
DECL|field|ALLOWED_METHODS
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_METHODS
init|=
literal|"allowed-methods"
decl_stmt|;
DECL|field|ALLOWED_METHODS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_METHODS_DEFAULT
init|=
literal|"GET,POST,HEAD"
decl_stmt|;
DECL|field|ALLOWED_HEADERS
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_HEADERS
init|=
literal|"allowed-headers"
decl_stmt|;
DECL|field|ALLOWED_HEADERS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|ALLOWED_HEADERS_DEFAULT
init|=
literal|"X-Requested-With,Content-Type,Accept,Origin"
decl_stmt|;
DECL|field|MAX_AGE
specifier|public
specifier|static
specifier|final
name|String
name|MAX_AGE
init|=
literal|"max-age"
decl_stmt|;
DECL|field|MAX_AGE_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|MAX_AGE_DEFAULT
init|=
literal|"1800"
decl_stmt|;
DECL|field|allowedMethods
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|allowedMethods
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allowedHeaders
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|allowedHeaders
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allowedOrigins
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|allowedOrigins
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allowAllOrigins
specifier|private
name|boolean
name|allowAllOrigins
init|=
literal|true
decl_stmt|;
DECL|field|maxAge
specifier|private
name|String
name|maxAge
decl_stmt|;
annotation|@
name|Override
DECL|method|init (FilterConfig filterConfig)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|ServletException
block|{
name|initializeAllowedMethods
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|initializeAllowedHeaders
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|initializeAllowedOrigins
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
name|initializeMaxAge
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doFilter (ServletRequest req, ServletResponse res, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|req
parameter_list|,
name|ServletResponse
name|res
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|doCrossFilter
argument_list|(
operator|(
name|HttpServletRequest
operator|)
name|req
argument_list|,
operator|(
name|HttpServletResponse
operator|)
name|res
argument_list|)
expr_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|req
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|allowedMethods
operator|.
name|clear
argument_list|()
expr_stmt|;
name|allowedHeaders
operator|.
name|clear
argument_list|()
expr_stmt|;
name|allowedOrigins
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|doCrossFilter (HttpServletRequest req, HttpServletResponse res)
specifier|private
name|void
name|doCrossFilter
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
block|{
name|String
name|originsList
init|=
name|encodeHeader
argument_list|(
name|req
operator|.
name|getHeader
argument_list|(
name|ORIGIN
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isCrossOrigin
argument_list|(
name|originsList
argument_list|)
condition|)
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
literal|"Header origin is null. Returning"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
operator|!
name|areOriginsAllowed
argument_list|(
name|originsList
argument_list|)
condition|)
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
literal|"Header origins '"
operator|+
name|originsList
operator|+
literal|"' not allowed. Returning"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|String
name|accessControlRequestMethod
init|=
name|req
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isMethodAllowed
argument_list|(
name|accessControlRequestMethod
argument_list|)
condition|)
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
literal|"Access control method '"
operator|+
name|accessControlRequestMethod
operator|+
literal|"' not allowed. Returning"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|String
name|accessControlRequestHeaders
init|=
name|req
operator|.
name|getHeader
argument_list|(
name|ACCESS_CONTROL_REQUEST_HEADERS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|areHeadersAllowed
argument_list|(
name|accessControlRequestHeaders
argument_list|)
condition|)
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
literal|"Access control headers '"
operator|+
name|accessControlRequestHeaders
operator|+
literal|"' not allowed. Returning"
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
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
literal|"Completed cross origin filter checks. Populating "
operator|+
literal|"HttpServletResponse"
argument_list|)
expr_stmt|;
block|}
name|res
operator|.
name|setHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
name|originsList
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
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
name|res
operator|.
name|setHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|,
name|getAllowedMethodsHeader
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|,
name|getAllowedHeadersHeader
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
name|ACCESS_CONTROL_MAX_AGE
argument_list|,
name|maxAge
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAllowedHeadersHeader ()
name|String
name|getAllowedHeadersHeader
parameter_list|()
block|{
return|return
name|StringUtils
operator|.
name|join
argument_list|(
name|allowedHeaders
argument_list|,
literal|','
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAllowedMethodsHeader ()
name|String
name|getAllowedMethodsHeader
parameter_list|()
block|{
return|return
name|StringUtils
operator|.
name|join
argument_list|(
name|allowedMethods
argument_list|,
literal|','
argument_list|)
return|;
block|}
DECL|method|initializeAllowedMethods (FilterConfig filterConfig)
specifier|private
name|void
name|initializeAllowedMethods
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
block|{
name|String
name|allowedMethodsConfig
init|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
name|ALLOWED_METHODS
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowedMethodsConfig
operator|==
literal|null
condition|)
block|{
name|allowedMethodsConfig
operator|=
name|ALLOWED_METHODS_DEFAULT
expr_stmt|;
block|}
name|allowedMethods
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|allowedMethodsConfig
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s*,\\s*"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allowed Methods: "
operator|+
name|getAllowedMethodsHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeAllowedHeaders (FilterConfig filterConfig)
specifier|private
name|void
name|initializeAllowedHeaders
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
block|{
name|String
name|allowedHeadersConfig
init|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
name|ALLOWED_HEADERS
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowedHeadersConfig
operator|==
literal|null
condition|)
block|{
name|allowedHeadersConfig
operator|=
name|ALLOWED_HEADERS_DEFAULT
expr_stmt|;
block|}
name|allowedHeaders
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|allowedHeadersConfig
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s*,\\s*"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allowed Headers: "
operator|+
name|getAllowedHeadersHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeAllowedOrigins (FilterConfig filterConfig)
specifier|private
name|void
name|initializeAllowedOrigins
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
block|{
name|String
name|allowedOriginsConfig
init|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
name|ALLOWED_ORIGINS
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowedOriginsConfig
operator|==
literal|null
condition|)
block|{
name|allowedOriginsConfig
operator|=
name|ALLOWED_ORIGINS_DEFAULT
expr_stmt|;
block|}
name|allowedOrigins
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|allowedOriginsConfig
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s*,\\s*"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|allowAllOrigins
operator|=
name|allowedOrigins
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allowed Origins: "
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
name|allowedOrigins
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allow All Origins: "
operator|+
name|allowAllOrigins
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeMaxAge (FilterConfig filterConfig)
specifier|private
name|void
name|initializeMaxAge
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
block|{
name|maxAge
operator|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
name|MAX_AGE
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxAge
operator|==
literal|null
condition|)
block|{
name|maxAge
operator|=
name|MAX_AGE_DEFAULT
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Max Age: "
operator|+
name|maxAge
argument_list|)
expr_stmt|;
block|}
DECL|method|encodeHeader (final String header)
specifier|static
name|String
name|encodeHeader
parameter_list|(
specifier|final
name|String
name|header
parameter_list|)
block|{
if|if
condition|(
name|header
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Protect against HTTP response splitting vulnerability
comment|// since value is written as part of the response header
comment|// Ensure this header only has one header by removing
comment|// CRs and LFs
return|return
name|header
operator|.
name|split
argument_list|(
literal|"\n|\r"
argument_list|)
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
return|;
block|}
DECL|method|isCrossOrigin (String originsList)
specifier|static
name|boolean
name|isCrossOrigin
parameter_list|(
name|String
name|originsList
parameter_list|)
block|{
return|return
name|originsList
operator|!=
literal|null
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|areOriginsAllowed (String originsList)
name|boolean
name|areOriginsAllowed
parameter_list|(
name|String
name|originsList
parameter_list|)
block|{
if|if
condition|(
name|allowAllOrigins
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
index|[]
name|origins
init|=
name|originsList
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|origin
range|:
name|origins
control|)
block|{
for|for
control|(
name|String
name|allowedOrigin
range|:
name|allowedOrigins
control|)
block|{
if|if
condition|(
name|allowedOrigin
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|String
name|regex
init|=
name|allowedOrigin
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|"\\."
argument_list|)
operator|.
name|replace
argument_list|(
literal|"*"
argument_list|,
literal|".*"
argument_list|)
decl_stmt|;
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|origin
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|allowedOrigin
operator|.
name|equals
argument_list|(
name|origin
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|areHeadersAllowed (String accessControlRequestHeaders)
specifier|private
name|boolean
name|areHeadersAllowed
parameter_list|(
name|String
name|accessControlRequestHeaders
parameter_list|)
block|{
if|if
condition|(
name|accessControlRequestHeaders
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
index|[]
name|headers
init|=
name|accessControlRequestHeaders
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\s*,\\s*"
argument_list|)
decl_stmt|;
return|return
name|allowedHeaders
operator|.
name|containsAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|headers
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isMethodAllowed (String accessControlRequestMethod)
specifier|private
name|boolean
name|isMethodAllowed
parameter_list|(
name|String
name|accessControlRequestMethod
parameter_list|)
block|{
if|if
condition|(
name|accessControlRequestMethod
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|allowedMethods
operator|.
name|contains
argument_list|(
name|accessControlRequestMethod
argument_list|)
return|;
block|}
block|}
end_class

end_unit

