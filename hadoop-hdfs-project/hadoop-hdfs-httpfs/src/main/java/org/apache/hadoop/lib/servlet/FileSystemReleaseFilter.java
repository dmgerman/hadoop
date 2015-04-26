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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
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
name|lib
operator|.
name|service
operator|.
name|FileSystemAccess
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

begin_comment
comment|/**  * The<code>FileSystemReleaseFilter</code> releases back to the  * {@link FileSystemAccess} service a<code>FileSystem</code> instance.  *<p>  * This filter is useful in situations where a servlet request  * is streaming out HDFS data and the corresponding filesystem  * instance have to be closed after the streaming completes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileSystemReleaseFilter
specifier|public
specifier|abstract
class|class
name|FileSystemReleaseFilter
implements|implements
name|Filter
block|{
DECL|field|FILE_SYSTEM_TL
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|FileSystem
argument_list|>
name|FILE_SYSTEM_TL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|FileSystem
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Initializes the filter.    *<p>    * This implementation is a NOP.    *    * @param filterConfig filter configuration.    *    * @throws ServletException thrown if the filter could not be initialized.    */
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
block|{   }
comment|/**    * It delegates the incoming request to the<code>FilterChain</code>, and    * at its completion (in a finally block) releases the filesystem instance    * back to the {@link FileSystemAccess} service.    *    * @param servletRequest servlet request.    * @param servletResponse servlet response.    * @param filterChain filter chain.    *    * @throws IOException thrown if an IO error occurs.    * @throws ServletException thrown if a servlet error occurs.    */
annotation|@
name|Override
DECL|method|doFilter (ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|servletRequest
parameter_list|,
name|ServletResponse
name|servletResponse
parameter_list|,
name|FilterChain
name|filterChain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
try|try
block|{
name|filterChain
operator|.
name|doFilter
argument_list|(
name|servletRequest
argument_list|,
name|servletResponse
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileSystem
name|fs
init|=
name|FILE_SYSTEM_TL
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|FILE_SYSTEM_TL
operator|.
name|remove
argument_list|()
expr_stmt|;
name|getFileSystemAccess
argument_list|()
operator|.
name|releaseFileSystem
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Static method that sets the<code>FileSystem</code> to release back to    * the {@link FileSystemAccess} service on servlet request completion.    *    * @param fs fileystem instance.    */
DECL|method|setFileSystem (FileSystem fs)
specifier|public
specifier|static
name|void
name|setFileSystem
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|FILE_SYSTEM_TL
operator|.
name|set
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Abstract method to be implemetned by concrete implementations of the    * filter that return the {@link FileSystemAccess} service to which the filesystem    * will be returned to.    *    * @return the FileSystemAccess service.    */
DECL|method|getFileSystemAccess ()
specifier|protected
specifier|abstract
name|FileSystemAccess
name|getFileSystemAccess
parameter_list|()
function_decl|;
block|}
end_class

end_unit

