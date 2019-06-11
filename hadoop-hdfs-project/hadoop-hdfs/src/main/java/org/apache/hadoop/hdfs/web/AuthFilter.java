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
name|ProxyUserAuthenticationFilter
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
name|ProxyUserAuthenticationFilter
block|{
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
name|ProxyUserAuthenticationFilter
operator|.
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
operator|&&
name|httpRequest
operator|.
name|getServletPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|WebHdfsFileSystem
operator|.
name|PATH_PREFIX
argument_list|)
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
name|request
argument_list|,
name|response
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

