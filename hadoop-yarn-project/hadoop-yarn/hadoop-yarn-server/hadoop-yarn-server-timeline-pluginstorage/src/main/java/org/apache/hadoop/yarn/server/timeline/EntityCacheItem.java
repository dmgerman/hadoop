begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
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
name|timeline
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|conf
operator|.
name|Configuration
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
name|util
operator|.
name|Time
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
name|timeline
operator|.
name|TimelineEntityGroupId
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
name|server
operator|.
name|timeline
operator|.
name|security
operator|.
name|TimelineACLsManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Cache item for timeline server v1.5 reader cache. Each cache item has a  * TimelineStore that can be filled with data within one entity group.  */
end_comment

begin_class
DECL|class|EntityCacheItem
specifier|public
class|class
name|EntityCacheItem
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EntityCacheItem
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|store
specifier|private
name|TimelineStore
name|store
decl_stmt|;
DECL|field|groupId
specifier|private
name|TimelineEntityGroupId
name|groupId
decl_stmt|;
DECL|field|appLogs
specifier|private
name|EntityGroupFSTimelineStore
operator|.
name|AppLogs
name|appLogs
decl_stmt|;
DECL|field|lastRefresh
specifier|private
name|long
name|lastRefresh
decl_stmt|;
DECL|field|config
specifier|private
name|Configuration
name|config
decl_stmt|;
DECL|field|refCount
specifier|private
name|int
name|refCount
init|=
literal|0
decl_stmt|;
DECL|field|activeStores
specifier|private
specifier|static
name|AtomicInteger
name|activeStores
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|EntityCacheItem (TimelineEntityGroupId gId, Configuration config)
specifier|public
name|EntityCacheItem
parameter_list|(
name|TimelineEntityGroupId
name|gId
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|this
operator|.
name|groupId
operator|=
name|gId
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/**    * @return The application log associated to this cache item, may be null.    */
DECL|method|getAppLogs ()
specifier|public
specifier|synchronized
name|EntityGroupFSTimelineStore
operator|.
name|AppLogs
name|getAppLogs
parameter_list|()
block|{
return|return
name|this
operator|.
name|appLogs
return|;
block|}
comment|/**    * Set the application logs to this cache item. The entity group should be    * associated with this application.    *    * @param incomingAppLogs Application logs this cache item mapped to    */
DECL|method|setAppLogs ( EntityGroupFSTimelineStore.AppLogs incomingAppLogs)
specifier|public
specifier|synchronized
name|void
name|setAppLogs
parameter_list|(
name|EntityGroupFSTimelineStore
operator|.
name|AppLogs
name|incomingAppLogs
parameter_list|)
block|{
name|this
operator|.
name|appLogs
operator|=
name|incomingAppLogs
expr_stmt|;
block|}
comment|/**    * @return The timeline store, either loaded or unloaded, of this cache item.    * This method will not hold the storage from being reclaimed.    */
DECL|method|getStore ()
specifier|public
specifier|synchronized
name|TimelineStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
comment|/**    * @return The number of currently active stores in all CacheItems.    */
DECL|method|getActiveStores ()
specifier|public
specifier|static
name|int
name|getActiveStores
parameter_list|()
block|{
return|return
name|activeStores
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Refresh this cache item if it needs refresh. This will enforce an appLogs    * rescan and then load new data. The refresh process is synchronized with    * other operations on the same cache item.    *    * @param aclManager ACL manager for the timeline storage    * @param metrics Metrics to trace the status of the entity group store    * @return a {@link org.apache.hadoop.yarn.server.timeline.TimelineStore}    *         object filled with all entities in the group.    * @throws IOException    */
DECL|method|refreshCache (TimelineACLsManager aclManager, EntityGroupFSTimelineStoreMetrics metrics)
specifier|public
specifier|synchronized
name|TimelineStore
name|refreshCache
parameter_list|(
name|TimelineACLsManager
name|aclManager
parameter_list|,
name|EntityGroupFSTimelineStoreMetrics
name|metrics
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|needRefresh
argument_list|()
condition|)
block|{
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// If an application is not finished, we only update summary logs (and put
comment|// new entities into summary storage).
comment|// Otherwise, since the application is done, we can update detail logs.
if|if
condition|(
operator|!
name|appLogs
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|appLogs
operator|.
name|parseSummaryLogs
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|appLogs
operator|.
name|getDetailLogs
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|appLogs
operator|.
name|scanForLogs
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|appLogs
operator|.
name|getDetailLogs
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
name|activeStores
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|LevelDBCacheTimelineStore
argument_list|(
name|groupId
operator|.
name|toString
argument_list|()
argument_list|,
literal|"LeveldbCache."
operator|+
name|groupId
argument_list|)
expr_stmt|;
name|store
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Store is not null, the refresh is triggered by stale storage.
name|metrics
operator|.
name|incrCacheStaleRefreshes
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|TimelineDataManager
name|tdm
init|=
operator|new
name|TimelineDataManager
argument_list|(
name|store
argument_list|,
name|aclManager
argument_list|)
init|)
block|{
name|tdm
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|tdm
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Load data from appLogs to tdm
name|appLogs
operator|.
name|loadDetailLog
argument_list|(
name|tdm
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
block|}
block|}
name|updateRefreshTimeToNow
argument_list|()
expr_stmt|;
name|metrics
operator|.
name|addCacheRefreshTime
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cache new enough, skip refreshing"
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrNoRefreshCacheRead
argument_list|()
expr_stmt|;
block|}
return|return
name|store
return|;
block|}
comment|/**    * Increase the number of references to this cache item by 1.    */
DECL|method|incrRefs ()
specifier|public
specifier|synchronized
name|void
name|incrRefs
parameter_list|()
block|{
name|refCount
operator|++
expr_stmt|;
block|}
comment|/**    * Unregister a reader. Try to release the cache if the reader to current    * cache reaches 0.    *    * @return true if the cache has been released, otherwise false    */
DECL|method|tryRelease ()
specifier|public
specifier|synchronized
name|boolean
name|tryRelease
parameter_list|()
block|{
name|refCount
operator|--
expr_stmt|;
comment|// Only reclaim the storage if there is no reader.
if|if
condition|(
name|refCount
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} references left for cached group {}, skipping the release"
argument_list|,
name|refCount
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|forceRelease
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Force releasing the cache item for the given group id, even though there    * may be active references.    */
DECL|method|forceRelease ()
specifier|public
specifier|synchronized
name|void
name|forceRelease
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error closing timeline store"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|store
operator|=
literal|null
expr_stmt|;
name|activeStores
operator|.
name|getAndDecrement
argument_list|()
expr_stmt|;
name|refCount
operator|=
literal|0
expr_stmt|;
comment|// reset offsets so next time logs are re-parsed
for|for
control|(
name|LogInfo
name|log
range|:
name|appLogs
operator|.
name|getDetailLogs
argument_list|()
control|)
block|{
if|if
condition|(
name|log
operator|.
name|getFilename
argument_list|()
operator|.
name|contains
argument_list|(
name|groupId
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cache for group {} released. "
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getRefCount ()
specifier|synchronized
name|int
name|getRefCount
parameter_list|()
block|{
return|return
name|refCount
return|;
block|}
DECL|method|needRefresh ()
specifier|private
name|boolean
name|needRefresh
parameter_list|()
block|{
return|return
operator|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|lastRefresh
operator|>
literal|10000
operator|)
return|;
block|}
DECL|method|updateRefreshTimeToNow ()
specifier|private
name|void
name|updateRefreshTimeToNow
parameter_list|()
block|{
name|this
operator|.
name|lastRefresh
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

