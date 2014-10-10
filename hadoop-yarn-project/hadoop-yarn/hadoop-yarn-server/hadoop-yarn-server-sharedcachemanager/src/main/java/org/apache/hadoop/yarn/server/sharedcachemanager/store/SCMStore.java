begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager.store
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
name|sharedcachemanager
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|InterfaceStability
operator|.
name|Evolving
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
name|FileStatus
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
name|CompositeService
import|;
end_import

begin_comment
comment|/**  * An abstract class for the data store used by the shared cache manager  * service. All implementations of methods in this interface need to be thread  * safe and atomic.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|SCMStore
specifier|public
specifier|abstract
class|class
name|SCMStore
extends|extends
name|CompositeService
block|{
DECL|method|SCMStore (String name)
specifier|protected
name|SCMStore
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
comment|/**    * Add a resource to the shared cache and it's associated filename. The    * resource is identified by a unique key. If the key already exists no action    * is taken and the filename of the existing resource is returned. If the key    * does not exist, the resource is added, it's access time is set, and the    * filename of the resource is returned.    *     * @param key a unique identifier for a resource    * @param fileName the filename of the resource    * @return the filename of the resource as represented by the cache    */
annotation|@
name|Private
DECL|method|addResource (String key, String fileName)
specifier|public
specifier|abstract
name|String
name|addResource
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|fileName
parameter_list|)
function_decl|;
comment|/**    * Remove a resource from the shared cache.    *     * @param key a unique identifier for a resource    * @return true if the resource was removed or did not exist, false if the    *         resource existed, contained at least one    *<code>SharedCacheResourceReference</code> and was not removed.    */
annotation|@
name|Private
DECL|method|removeResource (String key)
specifier|public
specifier|abstract
name|boolean
name|removeResource
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Add a<code>SharedCacheResourceReference</code> to a resource and update    * the resource access time.    *     * @param key a unique identifier for a resource    * @param ref the<code>SharedCacheResourceReference</code> to add    * @return String the filename of the resource if the    *<code>SharedCacheResourceReference</code> was added or already    *         existed. null if the resource did not exist    */
annotation|@
name|Private
DECL|method|addResourceReference (String key, SharedCacheResourceReference ref)
specifier|public
specifier|abstract
name|String
name|addResourceReference
parameter_list|(
name|String
name|key
parameter_list|,
name|SharedCacheResourceReference
name|ref
parameter_list|)
function_decl|;
comment|/**    * Get the<code>SharedCacheResourceReference</code>(s) associated with the    * resource.    *     * @param key a unique identifier for a resource    * @return an unmodifiable collection of    *<code>SharedCacheResourceReferences</code>. If the resource does    *         not exist, an empty set is returned.    */
annotation|@
name|Private
DECL|method|getResourceReferences ( String key)
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|getResourceReferences
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
comment|/**    * Remove a<code>SharedCacheResourceReference</code> from a resource.    *     * @param key a unique identifier for a resource    * @param ref the<code>SharedCacheResourceReference</code> to remove    * @param updateAccessTime true if the call should update the access time for    *          the resource    * @return true if the reference was removed, false otherwise    */
annotation|@
name|Private
DECL|method|removeResourceReference (String key, SharedCacheResourceReference ref, boolean updateAccessTime)
specifier|public
specifier|abstract
name|boolean
name|removeResourceReference
parameter_list|(
name|String
name|key
parameter_list|,
name|SharedCacheResourceReference
name|ref
parameter_list|,
name|boolean
name|updateAccessTime
parameter_list|)
function_decl|;
comment|/**    * Remove a collection of<code>SharedCacheResourceReferences</code> from a    * resource.    *     * @param key a unique identifier for a resource    * @param refs the collection of<code>SharedCacheResourceReference</code>s to    *          remove    * @param updateAccessTime true if the call should update the access time for    *          the resource    */
annotation|@
name|Private
DECL|method|removeResourceReferences (String key, Collection<SharedCacheResourceReference> refs, boolean updateAccessTime)
specifier|public
specifier|abstract
name|void
name|removeResourceReferences
parameter_list|(
name|String
name|key
parameter_list|,
name|Collection
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|refs
parameter_list|,
name|boolean
name|updateAccessTime
parameter_list|)
function_decl|;
comment|/**    * Check if a specific resource is evictable according to the store's enabled    * cache eviction policies.    *     * @param key a unique identifier for a resource    * @param file the<code>FileStatus</code> object for the resource file in the    *          file system.    * @return true if the resource is evicatble, false otherwise    */
annotation|@
name|Private
DECL|method|isResourceEvictable (String key, FileStatus file)
specifier|public
specifier|abstract
name|boolean
name|isResourceEvictable
parameter_list|(
name|String
name|key
parameter_list|,
name|FileStatus
name|file
parameter_list|)
function_decl|;
block|}
end_class

end_unit

