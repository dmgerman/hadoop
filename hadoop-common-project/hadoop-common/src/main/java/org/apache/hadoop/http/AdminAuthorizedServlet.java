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
name|ServletContext
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
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|DefaultServlet
import|;
end_import

begin_comment
comment|/**  * General servlet which is admin-authorized.  *  */
end_comment

begin_class
DECL|class|AdminAuthorizedServlet
specifier|public
class|class
name|AdminAuthorizedServlet
extends|extends
name|DefaultServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest request, HttpServletResponse response)
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// If user is a static user and auth Type is null, that means
comment|// there is a non-security environment and no need authorization,
comment|// otherwise, do the authorization.
specifier|final
name|ServletContext
name|servletContext
init|=
name|getServletContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|HttpServer2
operator|.
name|isStaticUserAndNoneAuthType
argument_list|(
name|servletContext
argument_list|,
name|request
argument_list|)
operator|||
name|HttpServer2
operator|.
name|hasAdministratorAccess
argument_list|(
name|servletContext
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
condition|)
block|{
comment|// Authorization is done. Just call super.
name|super
operator|.
name|doGet
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

