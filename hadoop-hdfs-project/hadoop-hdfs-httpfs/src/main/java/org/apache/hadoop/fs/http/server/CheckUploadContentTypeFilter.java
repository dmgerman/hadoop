begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.http.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|http
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
name|fs
operator|.
name|http
operator|.
name|client
operator|.
name|HttpFSFileSystem
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_comment
comment|/**  * Filter that Enforces the content-type to be application/octet-stream for  * POST and PUT requests.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CheckUploadContentTypeFilter
specifier|public
class|class
name|CheckUploadContentTypeFilter
implements|implements
name|Filter
block|{
DECL|field|UPLOAD_OPERATIONS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|UPLOAD_OPERATIONS
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|UPLOAD_OPERATIONS
operator|.
name|add
argument_list|(
name|HttpFSFileSystem
operator|.
name|Operation
operator|.
name|APPEND
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|UPLOAD_OPERATIONS
operator|.
name|add
argument_list|(
name|HttpFSFileSystem
operator|.
name|Operation
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the filter.    *<p>    * This implementation is a NOP.    *    * @param config filter configuration.    *    * @throws ServletException thrown if the filter could not be initialized.    */
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
comment|/**    * Enforces the content-type to be application/octet-stream for    * POST and PUT requests.    *    * @param request servlet request.    * @param response servlet response.    * @param chain filter chain.    *    * @throws IOException thrown if an IO error occurrs.    * @throws ServletException thrown if a servet error occurrs.    */
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
name|boolean
name|contentTypeOK
init|=
literal|true
decl_stmt|;
name|HttpServletRequest
name|httpReq
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
name|HttpServletResponse
name|httpRes
init|=
operator|(
name|HttpServletResponse
operator|)
name|response
decl_stmt|;
name|String
name|method
init|=
name|httpReq
operator|.
name|getMethod
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"PUT"
argument_list|)
operator|||
name|method
operator|.
name|equals
argument_list|(
literal|"POST"
argument_list|)
condition|)
block|{
name|String
name|op
init|=
name|httpReq
operator|.
name|getParameter
argument_list|(
name|HttpFSFileSystem
operator|.
name|OP_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|!=
literal|null
operator|&&
name|UPLOAD_OPERATIONS
operator|.
name|contains
argument_list|(
name|op
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|httpReq
operator|.
name|getParameter
argument_list|(
name|HttpFSParametersProvider
operator|.
name|DataParam
operator|.
name|NAME
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|contentType
init|=
name|httpReq
operator|.
name|getContentType
argument_list|()
decl_stmt|;
name|contentTypeOK
operator|=
name|HttpFSFileSystem
operator|.
name|UPLOAD_CONTENT_TYPE
operator|.
name|equalsIgnoreCase
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|contentTypeOK
condition|)
block|{
name|chain
operator|.
name|doFilter
argument_list|(
name|httpReq
argument_list|,
name|httpRes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpRes
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"Data upload requests must have content-type set to '"
operator|+
name|HttpFSFileSystem
operator|.
name|UPLOAD_CONTENT_TYPE
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Destroys the filter.    *<p>    * This implementation is a NOP.    */
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

