begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils.db.cache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|db
operator|.
name|cache
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableSet
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
name|ConcurrentHashMap
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
name|ConcurrentSkipListSet
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
name|ExecutorService
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
name|Executors
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
name|ThreadFactory
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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

begin_comment
comment|/**  * Cache implementation for the table. Depending on the cache clean up policy  * this cache will be full cache or partial cache.  *  * If cache cleanup policy is set as {@link CacheCleanupPolicy#MANUAL},  * this will be a partial cache.  *  * If cache cleanup policy is set as {@link CacheCleanupPolicy#NEVER},  * this will be a full cache.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|TableCacheImpl
specifier|public
class|class
name|TableCacheImpl
parameter_list|<
name|CACHEKEY
extends|extends
name|CacheKey
parameter_list|,
name|CACHEVALUE
extends|extends
name|CacheValue
parameter_list|>
implements|implements
name|TableCache
argument_list|<
name|CACHEKEY
argument_list|,
name|CACHEVALUE
argument_list|>
block|{
DECL|field|cache
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|CACHEKEY
argument_list|,
name|CACHEVALUE
argument_list|>
name|cache
decl_stmt|;
DECL|field|epochEntries
specifier|private
specifier|final
name|NavigableSet
argument_list|<
name|EpochEntry
argument_list|<
name|CACHEKEY
argument_list|>
argument_list|>
name|epochEntries
decl_stmt|;
DECL|field|executorService
specifier|private
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|cleanupPolicy
specifier|private
name|CacheCleanupPolicy
name|cleanupPolicy
decl_stmt|;
DECL|method|TableCacheImpl (CacheCleanupPolicy cleanupPolicy)
specifier|public
name|TableCacheImpl
parameter_list|(
name|CacheCleanupPolicy
name|cleanupPolicy
parameter_list|)
block|{
name|cache
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|epochEntries
operator|=
operator|new
name|ConcurrentSkipListSet
argument_list|<>
argument_list|()
expr_stmt|;
comment|// Created a singleThreadExecutor, so one cleanup will be running at a
comment|// time.
name|ThreadFactory
name|build
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"PartialTableCache Cleanup Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|executorService
operator|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|(
name|build
argument_list|)
expr_stmt|;
name|this
operator|.
name|cleanupPolicy
operator|=
name|cleanupPolicy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get (CACHEKEY cachekey)
specifier|public
name|CACHEVALUE
name|get
parameter_list|(
name|CACHEKEY
name|cachekey
parameter_list|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|cachekey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|put (CACHEKEY cacheKey, CACHEVALUE value)
specifier|public
name|void
name|put
parameter_list|(
name|CACHEKEY
name|cacheKey
parameter_list|,
name|CACHEVALUE
name|value
parameter_list|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|cacheKey
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|epochEntries
operator|.
name|add
argument_list|(
operator|new
name|EpochEntry
argument_list|<>
argument_list|(
name|value
operator|.
name|getEpoch
argument_list|()
argument_list|,
name|cacheKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cleanup (long epoch)
specifier|public
name|void
name|cleanup
parameter_list|(
name|long
name|epoch
parameter_list|)
block|{
name|executorService
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
name|evictCache
argument_list|(
name|epoch
argument_list|,
name|cleanupPolicy
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|CACHEKEY
argument_list|,
name|CACHEVALUE
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|cache
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|evictCache (long epoch, CacheCleanupPolicy cacheCleanupPolicy)
specifier|private
name|void
name|evictCache
parameter_list|(
name|long
name|epoch
parameter_list|,
name|CacheCleanupPolicy
name|cacheCleanupPolicy
parameter_list|)
block|{
name|EpochEntry
argument_list|<
name|CACHEKEY
argument_list|>
name|currentEntry
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|EpochEntry
argument_list|<
name|CACHEKEY
argument_list|>
argument_list|>
name|iterator
init|=
name|epochEntries
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|currentEntry
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|CACHEKEY
name|cachekey
init|=
name|currentEntry
operator|.
name|getCachekey
argument_list|()
decl_stmt|;
name|CacheValue
name|cacheValue
init|=
name|cache
operator|.
name|computeIfPresent
argument_list|(
name|cachekey
argument_list|,
operator|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|cleanupPolicy
operator|==
name|CacheCleanupPolicy
operator|.
name|MANUAL
condition|)
block|{
if|if
condition|(
name|v
operator|.
name|getEpoch
argument_list|()
operator|<=
name|epoch
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|cleanupPolicy
operator|==
name|CacheCleanupPolicy
operator|.
name|NEVER
condition|)
block|{
comment|// Remove only entries which are marked for delete.
if|if
condition|(
name|v
operator|.
name|getEpoch
argument_list|()
operator|<=
name|epoch
operator|&&
name|v
operator|.
name|getCacheValue
argument_list|()
operator|==
literal|null
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|return
name|v
return|;
block|}
operator|)
argument_list|)
decl_stmt|;
comment|// If currentEntry epoch is greater than epoch, we have deleted all
comment|// entries less than specified epoch. So, we can break.
if|if
condition|(
name|cacheValue
operator|!=
literal|null
operator|&&
name|cacheValue
operator|.
name|getEpoch
argument_list|()
operator|>=
name|epoch
condition|)
block|{
break|break;
block|}
block|}
block|}
DECL|method|lookup (CACHEKEY cachekey)
specifier|public
name|CacheResult
argument_list|<
name|CACHEVALUE
argument_list|>
name|lookup
parameter_list|(
name|CACHEKEY
name|cachekey
parameter_list|)
block|{
comment|// TODO: Remove this check once HA and Non-HA code is merged and all
comment|//  requests are converted to use cache and double buffer.
comment|// This is to done as temporary instead of passing ratis enabled flag
comment|// which requires more code changes. We cannot use ratis enabled flag
comment|// also because some of the requests in OM HA are not modified to use
comment|// double buffer and cache.
if|if
condition|(
name|cache
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|CacheResult
argument_list|<>
argument_list|(
name|CacheResult
operator|.
name|CacheStatus
operator|.
name|MAY_EXIST
argument_list|,
literal|null
argument_list|)
return|;
block|}
name|CACHEVALUE
name|cachevalue
init|=
name|cache
operator|.
name|get
argument_list|(
name|cachekey
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachevalue
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|cleanupPolicy
operator|==
name|CacheCleanupPolicy
operator|.
name|NEVER
condition|)
block|{
return|return
operator|new
name|CacheResult
argument_list|<>
argument_list|(
name|CacheResult
operator|.
name|CacheStatus
operator|.
name|NOT_EXIST
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|CacheResult
argument_list|<>
argument_list|(
name|CacheResult
operator|.
name|CacheStatus
operator|.
name|MAY_EXIST
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|cachevalue
operator|.
name|getCacheValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|CacheResult
argument_list|<>
argument_list|(
name|CacheResult
operator|.
name|CacheStatus
operator|.
name|EXISTS
argument_list|,
name|cachevalue
argument_list|)
return|;
block|}
else|else
block|{
comment|// When entity is marked for delete, cacheValue will be set to null.
comment|// In that case we can return NOT_EXIST irrespective of cache cleanup
comment|// policy.
return|return
operator|new
name|CacheResult
argument_list|<>
argument_list|(
name|CacheResult
operator|.
name|CacheStatus
operator|.
name|NOT_EXIST
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Cleanup policies for table cache.    */
DECL|enum|CacheCleanupPolicy
specifier|public
enum|enum
name|CacheCleanupPolicy
block|{
DECL|enumConstant|NEVER
name|NEVER
block|,
comment|// Cache will not be cleaned up. This mean's the table maintains
comment|// full cache.
DECL|enumConstant|MANUAL
name|MANUAL
comment|// Cache will be cleaned up, once after flushing to DB. It is
comment|// caller's responsibility to flush to DB, before calling cleanup cache.
block|}
block|}
end_class

end_unit

