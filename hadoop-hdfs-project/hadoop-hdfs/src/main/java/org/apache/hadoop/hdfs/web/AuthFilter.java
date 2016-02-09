begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|java
operator|.
name|util
operator|.
name|Properties
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
name|HttpServletRequestWrapper
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
name|hdfs
operator|.
name|web
operator|.
name|resources
operator|.
name|DelegationParam
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
name|authentication
operator|.
name|server
operator|.
name|KerberosAuthenticationHandler
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
name|PseudoAuthenticationHandler
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

begin_comment
comment|/**  * Subclass of {@link AuthenticationFilter} that  * obtains Hadoop-Auth configuration for webhdfs.  */
end_comment

begin_class
DECL|class|AuthFilter
specifier|public
class|class
name|AuthFilter
extends|extends
name|AuthenticationFilter
block|{
DECL|field|CONF_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|CONF_PREFIX
init|=
literal|"dfs.web.authentication."
decl_stmt|;
comment|/**    * Returns the filter configuration properties,    * including the ones prefixed with {@link #CONF_PREFIX}.    * The prefix is removed from the returned property names.    *    * @param prefix parameter not used.    * @param config parameter contains the initialization values.    * @return Hadoop-Auth configuration properties.    * @throws ServletException     */
annotation|@
name|Override
DECL|method|getConfiguration (String prefix, FilterConfig config)
specifier|protected
name|Properties
name|getConfiguration
parameter_list|(
name|String
name|prefix
parameter_list|,
name|FilterConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
specifier|final
name|Properties
name|p
init|=
name|super
operator|.
name|getConfiguration
argument_list|(
name|CONF_PREFIX
argument_list|,
name|config
argument_list|)
decl_stmt|;
comment|// if not set, configure based on security enabled
if|if
condition|(
name|p
operator|.
name|getProperty
argument_list|(
name|AUTH_TYPE
argument_list|)
operator|==
literal|null
condition|)
block|{
name|p
operator|.
name|setProperty
argument_list|(
name|AUTH_TYPE
argument_list|,
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|?
name|KerberosAuthenticationHandler
operator|.
name|TYPE
else|:
name|PseudoAuthenticationHandler
operator|.
name|TYPE
argument_list|)
expr_stmt|;
block|}
comment|// if not set, enable anonymous for pseudo authentication
if|if
condition|(
name|p
operator|.
name|getProperty
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|)
operator|==
literal|null
condition|)
block|{
name|p
operator|.
name|setProperty
argument_list|(
name|PseudoAuthenticationHandler
operator|.
name|ANONYMOUS_ALLOWED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
comment|//set cookie path
name|p
operator|.
name|setProperty
argument_list|(
name|COOKIE_PATH
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
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
name|HttpServletRequest
name|httpRequest
init|=
name|toLowerCase
argument_list|(
operator|(
name|HttpServletRequest
operator|)
name|request
argument_list|)
decl_stmt|;
specifier|final
name|String
name|tokenString
init|=
name|httpRequest
operator|.
name|getParameter
argument_list|(
name|DelegationParam
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenString
operator|!=
literal|null
condition|)
block|{
comment|//Token is present in the url, therefore token will be used for
comment|//authentication, bypass kerberos authentication.
name|filterChain
operator|.
name|doFilter
argument_list|(
name|httpRequest
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return;
block|}
name|super
operator|.
name|doFilter
argument_list|(
name|httpRequest
argument_list|,
name|response
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
block|}
DECL|method|toLowerCase (final HttpServletRequest request)
specifier|private
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
name|ParamFilter
operator|.
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

