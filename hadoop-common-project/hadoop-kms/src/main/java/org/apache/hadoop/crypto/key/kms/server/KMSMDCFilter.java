begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
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
name|classification
operator|.
name|InterfaceAudience
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
name|token
operator|.
name|delegation
operator|.
name|web
operator|.
name|HttpUserGroupInformation
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Servlet filter that captures context of the HTTP request to be use in the  * scope of KMS calls on the server side.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KMSMDCFilter
specifier|public
class|class
name|KMSMDCFilter
implements|implements
name|Filter
block|{
DECL|class|Data
specifier|private
specifier|static
class|class
name|Data
block|{
DECL|field|ugi
specifier|private
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|method
specifier|private
name|String
name|method
decl_stmt|;
DECL|field|url
specifier|private
name|StringBuffer
name|url
decl_stmt|;
DECL|method|Data (UserGroupInformation ugi, String method, StringBuffer url)
specifier|private
name|Data
parameter_list|(
name|UserGroupInformation
name|ugi
parameter_list|,
name|String
name|method
parameter_list|,
name|StringBuffer
name|url
parameter_list|)
block|{
name|this
operator|.
name|ugi
operator|=
name|ugi
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
block|}
DECL|field|DATA_TL
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Data
argument_list|>
name|DATA_TL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Data
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|getUgi ()
specifier|public
specifier|static
name|UserGroupInformation
name|getUgi
parameter_list|()
block|{
return|return
name|DATA_TL
operator|.
name|get
argument_list|()
operator|.
name|ugi
return|;
block|}
DECL|method|getMethod ()
specifier|public
specifier|static
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|DATA_TL
operator|.
name|get
argument_list|()
operator|.
name|method
return|;
block|}
DECL|method|getURL ()
specifier|public
specifier|static
name|String
name|getURL
parameter_list|()
block|{
return|return
name|DATA_TL
operator|.
name|get
argument_list|()
operator|.
name|url
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|init (FilterConfig config)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{   }
annotation|@
name|Override
DECL|method|doFilter (ServletRequest request, ServletResponse response, FilterChain chain)
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
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
try|try
block|{
name|DATA_TL
operator|.
name|remove
argument_list|()
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|HttpUserGroupInformation
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|method
init|=
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
operator|.
name|getMethod
argument_list|()
decl_stmt|;
name|StringBuffer
name|requestURL
init|=
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
operator|.
name|getRequestURL
argument_list|()
decl_stmt|;
name|String
name|queryString
init|=
operator|(
operator|(
name|HttpServletRequest
operator|)
name|request
operator|)
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
name|requestURL
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
operator|.
name|append
argument_list|(
name|queryString
argument_list|)
expr_stmt|;
block|}
name|DATA_TL
operator|.
name|set
argument_list|(
operator|new
name|Data
argument_list|(
name|ugi
argument_list|,
name|method
argument_list|,
name|requestURL
argument_list|)
argument_list|)
expr_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|DATA_TL
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
block|}
end_class

end_unit

