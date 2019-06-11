begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.server
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
name|server
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
name|HttpExceptionUtils
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
name|StringUtils
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
name|ArrayList
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
name|Iterator
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

begin_comment
comment|/**  * AuthenticationFilter which adds support to perform operations  * using end user instead of proxy user. Fetches the end user from  * doAs Query Parameter.  */
end_comment

begin_class
DECL|class|ProxyUserAuthenticationFilter
specifier|public
class|class
name|ProxyUserAuthenticationFilter
extends|extends
name|AuthenticationFilter
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
name|ProxyUserAuthenticationFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DO_AS
specifier|private
specifier|static
specifier|final
name|String
name|DO_AS
init|=
literal|"doas"
decl_stmt|;
DECL|field|PROXYUSER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PROXYUSER_PREFIX
init|=
literal|"proxyuser"
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
name|Configuration
name|conf
init|=
name|getProxyuserConfiguration
argument_list|(
name|filterConfig
argument_list|)
decl_stmt|;
name|ProxyUsers
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|conf
argument_list|,
name|PROXYUSER_PREFIX
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
block|}
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
name|HttpServletRequest
name|lowerCaseRequest
init|=
name|toLowerCase
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|doAsUser
init|=
name|lowerCaseRequest
operator|.
name|getParameter
argument_list|(
name|DO_AS
argument_list|)
decl_stmt|;
if|if
condition|(
name|doAsUser
operator|!=
literal|null
operator|&&
operator|!
name|doAsUser
operator|.
name|equals
argument_list|(
name|request
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"doAsUser = {}, RemoteUser = {} , RemoteAddress = {} "
argument_list|,
name|doAsUser
argument_list|,
name|request
operator|.
name|getRemoteUser
argument_list|()
argument_list|,
name|request
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|requestUgi
init|=
operator|(
name|request
operator|.
name|getUserPrincipal
argument_list|()
operator|!=
literal|null
operator|)
condition|?
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|request
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|requestUgi
operator|!=
literal|null
condition|)
block|{
name|requestUgi
operator|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|doAsUser
argument_list|,
name|requestUgi
argument_list|)
expr_stmt|;
try|try
block|{
name|ProxyUsers
operator|.
name|authorize
argument_list|(
name|requestUgi
argument_list|,
name|request
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|UserGroupInformation
name|ugiF
init|=
name|requestUgi
decl_stmt|;
name|request
operator|=
operator|new
name|HttpServletRequestWrapper
argument_list|(
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
name|ugiF
operator|.
name|getShortUserName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|ugiF
operator|.
name|getUserName
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Proxy user Authentication successful"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthorizationException
name|ex
parameter_list|)
block|{
name|HttpExceptionUtils
operator|.
name|createServletExceptionResponse
argument_list|(
name|response
argument_list|,
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Proxy user Authentication exception"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
name|super
operator|.
name|doFilter
argument_list|(
name|filterChain
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|getProxyuserConfiguration (FilterConfig filterConfig)
specifier|protected
name|Configuration
name|getProxyuserConfiguration
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|ServletException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|names
init|=
name|filterConfig
operator|.
name|getInitParameterNames
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
operator|(
name|String
operator|)
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|PROXYUSER_PREFIX
operator|+
literal|"."
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|conf
return|;
block|}
DECL|method|containsUpperCase (final Iterable<String> strings)
specifier|static
name|boolean
name|containsUpperCase
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|)
block|{
for|for
control|(
name|String
name|s
range|:
name|strings
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isUpperCase
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
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
DECL|method|toLowerCase ( final HttpServletRequest request)
specifier|public
specifier|static
name|HttpServletRequest
name|toLowerCase
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|original
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
operator|)
name|request
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|containsUpperCase
argument_list|(
name|original
operator|.
name|keySet
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|request
return|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|m
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
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
range|:
name|original
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|key
init|=
name|StringUtils
operator|.
name|toLowerCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|m
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|strings
operator|==
literal|null
condition|)
block|{
name|strings
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|strings
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|v
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|HttpServletRequestWrapper
argument_list|(
name|request
argument_list|)
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|parameters
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|getParameterMap
parameter_list|()
block|{
if|if
condition|(
name|parameters
operator|==
literal|null
condition|)
block|{
name|parameters
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|a
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|parameters
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|a
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|a
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parameters
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|a
init|=
name|m
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|a
operator|==
literal|null
condition|?
literal|null
else|:
name|a
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getParameterValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getParameterMap
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getParameterNames
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|m
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Enumeration
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasMoreElements
parameter_list|()
block|{
return|return
name|i
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|nextElement
parameter_list|()
block|{
return|return
name|i
operator|.
name|next
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

