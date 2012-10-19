begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|servlet
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

begin_comment
comment|/**  * Filter that resolves the requester hostname.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HostnameFilter
specifier|public
class|class
name|HostnameFilter
implements|implements
name|Filter
block|{
DECL|field|HOSTNAME_TL
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|String
argument_list|>
name|HOSTNAME_TL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Initializes the filter.    *<p/>    * This implementation is a NOP.    *    * @param config filter configuration.    *    * @throws ServletException thrown if the filter could not be initialized.    */
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
comment|/**    * Resolves the requester hostname and delegates the request to the chain.    *<p/>    * The requester hostname is available via the {@link #get} method.    *    * @param request servlet request.    * @param response servlet response.    * @param chain filter chain.    *    * @throws IOException thrown if an IO error occurrs.    * @throws ServletException thrown if a servet error occurrs.    */
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
name|String
name|hostname
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
name|request
operator|.
name|getRemoteAddr
argument_list|()
argument_list|)
operator|.
name|getCanonicalHostName
argument_list|()
decl_stmt|;
name|HOSTNAME_TL
operator|.
name|set
argument_list|(
name|hostname
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
name|HOSTNAME_TL
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the requester hostname.    *    * @return the requester hostname.    */
DECL|method|get ()
specifier|public
specifier|static
name|String
name|get
parameter_list|()
block|{
return|return
name|HOSTNAME_TL
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Destroys the filter.    *<p/>    * This implementation is a NOP.    */
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

