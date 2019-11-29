begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|router
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSUtil
operator|.
name|isParentEntry
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
name|Map
operator|.
name|Entry
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
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
import|;
end_import

begin_comment
comment|/**  * Router quota manager in Router. The manager maintains  * {@link RouterQuotaUsage} cache of mount tables and do management  * for the quota caches.  */
end_comment

begin_class
DECL|class|RouterQuotaManager
specifier|public
class|class
name|RouterQuotaManager
block|{
comment|/** Quota usage<MountTable Path, Aggregated QuotaUsage> cache. */
DECL|field|cache
specifier|private
name|TreeMap
argument_list|<
name|String
argument_list|,
name|RouterQuotaUsage
argument_list|>
name|cache
decl_stmt|;
comment|/** Lock to access the quota cache. */
DECL|field|readWriteLock
specifier|private
specifier|final
name|ReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|readLock
specifier|private
specifier|final
name|Lock
name|readLock
init|=
name|readWriteLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|Lock
name|writeLock
init|=
name|readWriteLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
DECL|method|RouterQuotaManager ()
specifier|public
name|RouterQuotaManager
parameter_list|()
block|{
name|this
operator|.
name|cache
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get all the mount quota paths.    * @return All the mount quota paths.    */
DECL|method|getAll ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAll
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|cache
operator|.
name|keySet
argument_list|()
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Is the path a mount entry.    *    * @param path the path.    * @return {@code true} if path is a mount entry; {@code false} otherwise.    */
DECL|method|isMountEntry (String path)
name|boolean
name|isMountEntry
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|cache
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get the nearest ancestor's quota usage, and meanwhile its quota was set.    * @param path The path being written.    * @return RouterQuotaUsage Quota usage.    */
DECL|method|getQuotaUsage (String path)
specifier|public
name|RouterQuotaUsage
name|getQuotaUsage
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|RouterQuotaUsage
name|quotaUsage
init|=
name|this
operator|.
name|cache
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|quotaUsage
operator|!=
literal|null
operator|&&
name|isQuotaSet
argument_list|(
name|quotaUsage
argument_list|)
condition|)
block|{
return|return
name|quotaUsage
return|;
block|}
comment|// If not found, look for its parent path usage value.
name|int
name|pos
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|parentPath
init|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
return|return
name|getQuotaUsage
argument_list|(
name|parentPath
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get children paths (can include itself) under specified federation path.    * @param parentPath Federated path.    * @return Set of children paths.    */
DECL|method|getPaths (String parentPath)
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|(
name|String
name|parentPath
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|String
name|from
init|=
name|parentPath
decl_stmt|;
name|String
name|to
init|=
name|parentPath
operator|+
name|Character
operator|.
name|MAX_VALUE
decl_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|RouterQuotaUsage
argument_list|>
name|subMap
init|=
name|this
operator|.
name|cache
operator|.
name|subMap
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|validPaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|subMap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|path
range|:
name|subMap
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|isParentEntry
argument_list|(
name|path
argument_list|,
name|parentPath
argument_list|)
condition|)
block|{
name|validPaths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|validPaths
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get parent paths (including itself) and quotas of the specified federation    * path. Only parents containing quota are returned.    * @param childPath Federated path.    * @return TreeMap of parent paths and quotas.    */
DECL|method|getParentsContainingQuota ( String childPath)
name|TreeMap
argument_list|<
name|String
argument_list|,
name|RouterQuotaUsage
argument_list|>
name|getParentsContainingQuota
parameter_list|(
name|String
name|childPath
parameter_list|)
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|RouterQuotaUsage
argument_list|>
name|res
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|RouterQuotaUsage
argument_list|>
name|entry
init|=
name|this
operator|.
name|cache
operator|.
name|floorEntry
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
while|while
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|String
name|mountPath
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|RouterQuotaUsage
name|quota
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|isQuotaSet
argument_list|(
name|quota
argument_list|)
operator|&&
name|isParentEntry
argument_list|(
name|childPath
argument_list|,
name|mountPath
argument_list|)
condition|)
block|{
name|res
operator|.
name|put
argument_list|(
name|mountPath
argument_list|,
name|quota
argument_list|)
expr_stmt|;
block|}
name|entry
operator|=
name|this
operator|.
name|cache
operator|.
name|lowerEntry
argument_list|(
name|mountPath
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Put new entity into cache.    * @param path Mount table path.    * @param quotaUsage Corresponding cache value.    */
DECL|method|put (String path, RouterQuotaUsage quotaUsage)
specifier|public
name|void
name|put
parameter_list|(
name|String
name|path
parameter_list|,
name|RouterQuotaUsage
name|quotaUsage
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|cache
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|quotaUsage
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Remove the entity from cache.    * @param path Mount table path.    */
DECL|method|remove (String path)
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|cache
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Clean up the cache.    */
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Check if the quota was set.    * @param quota RouterQuotaUsage set in mount table.    * @return True if the quota is set.    */
DECL|method|isQuotaSet (RouterQuotaUsage quota)
specifier|public
name|boolean
name|isQuotaSet
parameter_list|(
name|RouterQuotaUsage
name|quota
parameter_list|)
block|{
if|if
condition|(
name|quota
operator|!=
literal|null
condition|)
block|{
name|long
name|nsQuota
init|=
name|quota
operator|.
name|getQuota
argument_list|()
decl_stmt|;
name|long
name|ssQuota
init|=
name|quota
operator|.
name|getSpaceQuota
argument_list|()
decl_stmt|;
comment|// once nsQuota or ssQuota was set, this mount table is quota set
if|if
condition|(
name|nsQuota
operator|!=
name|HdfsConstants
operator|.
name|QUOTA_RESET
operator|||
name|ssQuota
operator|!=
name|HdfsConstants
operator|.
name|QUOTA_RESET
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

