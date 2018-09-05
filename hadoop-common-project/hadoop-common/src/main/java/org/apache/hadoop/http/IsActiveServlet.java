begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.http
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
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
comment|/**  * Used by Load Balancers to detect the active NameNode/ResourceManager/Router.  */
end_comment

begin_class
DECL|class|IsActiveServlet
specifier|public
specifier|abstract
class|class
name|IsActiveServlet
extends|extends
name|HttpServlet
block|{
comment|/** Default serial identifier. */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|SERVLET_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SERVLET_NAME
init|=
literal|"isActive"
decl_stmt|;
DECL|field|PATH_SPEC
specifier|public
specifier|static
specifier|final
name|String
name|PATH_SPEC
init|=
literal|"/isActive"
decl_stmt|;
DECL|field|RESPONSE_ACTIVE
specifier|public
specifier|static
specifier|final
name|String
name|RESPONSE_ACTIVE
init|=
literal|"I am Active!"
decl_stmt|;
DECL|field|RESPONSE_NOT_ACTIVE
specifier|public
specifier|static
specifier|final
name|String
name|RESPONSE_NOT_ACTIVE
init|=
literal|"I am not Active!"
decl_stmt|;
comment|/**    * Check whether this instance is the Active one.    * @param req HTTP request    * @param resp HTTP response to write to    */
annotation|@
name|Override
DECL|method|doGet ( final HttpServletRequest req, final HttpServletResponse resp)
specifier|public
name|void
name|doGet
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|IOException
block|{
comment|// By default requests are persistent. We don't want long-lived connections
comment|// on server side.
name|resp
operator|.
name|addHeader
argument_list|(
literal|"Connection"
argument_list|,
literal|"close"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isActive
argument_list|()
condition|)
block|{
comment|// Report not SC_OK
name|resp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_METHOD_NOT_ALLOWED
argument_list|,
name|RESPONSE_NOT_ACTIVE
argument_list|)
expr_stmt|;
return|return;
block|}
name|resp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|resp
operator|.
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
name|RESPONSE_ACTIVE
argument_list|)
expr_stmt|;
name|resp
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return true if this instance is in Active HA state.    */
DECL|method|isActive ()
specifier|protected
specifier|abstract
name|boolean
name|isActive
parameter_list|()
function_decl|;
block|}
end_class

end_unit

