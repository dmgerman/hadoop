begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|Path
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
name|service
operator|.
name|AbstractService
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
operator|.
name|SharedCacheClientImpl
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * This is the client for YARN's shared cache.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|SharedCacheClient
specifier|public
specifier|abstract
class|class
name|SharedCacheClient
extends|extends
name|AbstractService
block|{
annotation|@
name|Public
DECL|method|createSharedCacheClient ()
specifier|public
specifier|static
name|SharedCacheClient
name|createSharedCacheClient
parameter_list|()
block|{
name|SharedCacheClient
name|client
init|=
operator|new
name|SharedCacheClientImpl
argument_list|()
decl_stmt|;
return|return
name|client
return|;
block|}
annotation|@
name|Private
DECL|method|SharedCacheClient (String name)
specifier|public
name|SharedCacheClient
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>    * The method to claim a resource with the<code>SharedCacheManager.</code>    * The client uses a checksum to identify the resource and an    * {@link ApplicationId} to identify which application will be using the    * resource.    *</p>    *    *<p>    * The<code>SharedCacheManager</code> responds with whether or not the    * resource exists in the cache. If the resource exists, a<code>Path</code>    * to the resource in the shared cache is returned. If the resource does not    * exist, null is returned instead.    *</p>    *    *<p>    * Once a path has been returned for a resource, that path is safe to use for    * the lifetime of the application that corresponds to the provided    * ApplicationId.    *</p>    *    *<p>    * Additionally, a name for the resource should be specified. A fragment will    * be added to the path with the desired name if the desired name is different    * than the name of the provided path from the shared cache. This ensures that    * if the returned path is used to create a LocalResource, then the symlink    * created during YARN localization will match the name specified.    *</p>    *    * @param applicationId ApplicationId of the application using the resource    * @param resourceKey the key (i.e. checksum) that identifies the resource    * @param resourceName the desired name of the resource    * @return Path to the resource, or null if it does not exist    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|use (ApplicationId applicationId, String resourceKey, String resourceName)
specifier|public
specifier|abstract
name|Path
name|use
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|resourceKey
parameter_list|,
name|String
name|resourceName
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    *<p>    * The method to release a resource with the<code>SharedCacheManager.</code>    * This method is called once an application is no longer using a claimed    * resource in the shared cache. The client uses a checksum to identify the    * resource and an {@link ApplicationId} to identify which application is    * releasing the resource.    *</p>    *     *<p>    * Note: This method is an optimization and the client is not required to call    * it for correctness.    *</p>    *     * @param applicationId ApplicationId of the application releasing the    *          resource    * @param resourceKey the key (i.e. checksum) that identifies the resource    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|release (ApplicationId applicationId, String resourceKey)
specifier|public
specifier|abstract
name|void
name|release
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|resourceKey
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * A convenience method to calculate the checksum of a specified file.    *     * @param sourceFile A path to the input file    * @return A hex string containing the checksum digest    * @throws IOException    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getFileChecksum (Path sourceFile)
specifier|public
specifier|abstract
name|String
name|getFileChecksum
parameter_list|(
name|Path
name|sourceFile
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

