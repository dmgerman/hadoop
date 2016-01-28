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
name|codehaus
operator|.
name|jackson
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|method|EntityCacheItem (Configuration config, FileSystem fs)
specifier|public
name|EntityCacheItem
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
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
comment|/**    * Set the application logs to this cache item. The entity group should be    * associated with this application.    *    * @param incomingAppLogs    */
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
comment|/**    * @return The timeline store, either loaded or unloaded, of this cache item.    */
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
comment|/**    * Refresh this cache item if it needs refresh. This will enforce an appLogs    * rescan and then load new data. The refresh process is synchronized with    * other operations on the same cache item.    *    * @param groupId    * @param aclManager    * @param jsonFactory    * @param objMapper    * @return a {@link org.apache.hadoop.yarn.server.timeline.TimelineStore}    *         object filled with all entities in the group.    * @throws IOException    */
DECL|method|refreshCache (TimelineEntityGroupId groupId, TimelineACLsManager aclManager, JsonFactory jsonFactory, ObjectMapper objMapper)
specifier|public
specifier|synchronized
name|TimelineStore
name|refreshCache
parameter_list|(
name|TimelineEntityGroupId
name|groupId
parameter_list|,
name|TimelineACLsManager
name|aclManager
parameter_list|,
name|JsonFactory
name|jsonFactory
parameter_list|,
name|ObjectMapper
name|objMapper
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
decl_stmt|;
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
name|List
argument_list|<
name|LogInfo
argument_list|>
name|removeList
init|=
operator|new
name|ArrayList
argument_list|<
name|LogInfo
argument_list|>
argument_list|()
decl_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Try refresh logs for {}"
argument_list|,
name|log
operator|.
name|getFilename
argument_list|()
argument_list|)
expr_stmt|;
comment|// Only refresh the log that matches the cache id
if|if
condition|(
name|log
operator|.
name|matchesGroupId
argument_list|(
name|groupId
argument_list|)
condition|)
block|{
name|Path
name|appDirPath
init|=
name|appLogs
operator|.
name|getAppDirPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|log
operator|.
name|getPath
argument_list|(
name|appDirPath
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Refresh logs for cache id {}"
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
name|log
operator|.
name|parseForStore
argument_list|(
name|tdm
argument_list|,
name|appDirPath
argument_list|,
name|appLogs
operator|.
name|isDone
argument_list|()
argument_list|,
name|jsonFactory
argument_list|,
name|objMapper
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// The log may have been removed, remove the log
name|removeList
operator|.
name|add
argument_list|(
name|log
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"File {} no longer exists, remove it from log list"
argument_list|,
name|log
operator|.
name|getPath
argument_list|(
name|appDirPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|appLogs
operator|.
name|getDetailLogs
argument_list|()
operator|.
name|removeAll
argument_list|(
name|removeList
argument_list|)
expr_stmt|;
name|tdm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|updateRefreshTimeToNow
argument_list|()
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
block|}
return|return
name|store
return|;
block|}
comment|/**    * Release the cache item for the given group id.    *    * @param groupId    */
DECL|method|releaseCache (TimelineEntityGroupId groupId)
specifier|public
specifier|synchronized
name|void
name|releaseCache
parameter_list|(
name|TimelineEntityGroupId
name|groupId
parameter_list|)
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

