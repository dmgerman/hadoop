begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
package|package
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
name|resourcemanager
operator|.
name|webapp
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
name|io
operator|.
name|PrintWriter
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

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Singleton
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
name|hadoop
operator|.
name|http
operator|.
name|HtmlQuoting
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
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|guice
operator|.
name|spi
operator|.
name|container
operator|.
name|servlet
operator|.
name|GuiceContainer
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|RMWebAppFilter
specifier|public
class|class
name|RMWebAppFilter
extends|extends
name|GuiceContainer
block|{
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
comment|/**    *     */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|// define a set of URIs which do not need to do redirection
DECL|field|NON_REDIRECTED_URIS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|NON_REDIRECTED_URIS
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"/conf"
argument_list|,
literal|"/stacks"
argument_list|,
literal|"/logLevel"
argument_list|,
literal|"/metrics"
argument_list|,
literal|"/jmx"
argument_list|,
literal|"/logs"
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|RMWebAppFilter (Injector injector)
specifier|public
name|RMWebAppFilter
parameter_list|(
name|Injector
name|injector
parameter_list|)
block|{
name|super
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doFilter (HttpServletRequest request, HttpServletResponse response, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
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
name|response
operator|.
name|setCharacterEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|HtmlQuoting
operator|.
name|quoteHtmlChars
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
literal|"/"
expr_stmt|;
block|}
name|RMWebApp
name|rmWebApp
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|RMWebApp
operator|.
name|class
argument_list|)
decl_stmt|;
name|rmWebApp
operator|.
name|checkIfStandbyRM
argument_list|()
expr_stmt|;
if|if
condition|(
name|rmWebApp
operator|.
name|isStandby
argument_list|()
operator|&&
name|shouldRedirect
argument_list|(
name|rmWebApp
argument_list|,
name|uri
argument_list|)
condition|)
block|{
name|String
name|redirectPath
init|=
name|rmWebApp
operator|.
name|getRedirectPath
argument_list|()
operator|+
name|uri
decl_stmt|;
if|if
condition|(
name|redirectPath
operator|!=
literal|null
operator|&&
operator|!
name|redirectPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|redirectMsg
init|=
literal|"This is standby RM. Redirecting to the current active RM: "
operator|+
name|redirectPath
decl_stmt|;
name|response
operator|.
name|addHeader
argument_list|(
literal|"Refresh"
argument_list|,
literal|"3; url="
operator|+
name|redirectPath
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|redirectMsg
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|super
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|chain
argument_list|)
expr_stmt|;
block|}
DECL|method|shouldRedirect (RMWebApp rmWebApp, String uri)
specifier|private
name|boolean
name|shouldRedirect
parameter_list|(
name|RMWebApp
name|rmWebApp
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
return|return
operator|!
name|uri
operator|.
name|equals
argument_list|(
literal|"/"
operator|+
name|rmWebApp
operator|.
name|wsName
argument_list|()
operator|+
literal|"/v1/cluster/info"
argument_list|)
operator|&&
operator|!
name|uri
operator|.
name|equals
argument_list|(
literal|"/"
operator|+
name|rmWebApp
operator|.
name|name
argument_list|()
operator|+
literal|"/cluster"
argument_list|)
operator|&&
operator|!
name|NON_REDIRECTED_URIS
operator|.
name|contains
argument_list|(
name|uri
argument_list|)
return|;
block|}
block|}
end_class

end_unit

