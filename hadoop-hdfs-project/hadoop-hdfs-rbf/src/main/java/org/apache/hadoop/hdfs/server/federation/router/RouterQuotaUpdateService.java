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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|LinkedList
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
name|concurrent
operator|.
name|TimeUnit
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
name|fs
operator|.
name|QuotaUsage
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
name|HdfsFileStatus
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
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|RemoteLocation
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|MountTableStore
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetMountTableEntriesRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|GetMountTableEntriesResponse
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|UpdateMountTableEntryRequest
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|MountTable
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

begin_comment
comment|/**  * Service to periodically update the {@link RouterQuotaUsage}  * cached information in the {@link Router} and update corresponding  * mount table in State Store.  */
end_comment

begin_class
DECL|class|RouterQuotaUpdateService
specifier|public
class|class
name|RouterQuotaUpdateService
extends|extends
name|PeriodicService
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
name|RouterQuotaUpdateService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|mountTableStore
specifier|private
name|MountTableStore
name|mountTableStore
decl_stmt|;
DECL|field|rpcServer
specifier|private
name|RouterRpcServer
name|rpcServer
decl_stmt|;
comment|/** Router using this Service. */
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
comment|/** Router Quota manager. */
DECL|field|quotaManager
specifier|private
name|RouterQuotaManager
name|quotaManager
decl_stmt|;
DECL|method|RouterQuotaUpdateService (final Router router)
specifier|public
name|RouterQuotaUpdateService
parameter_list|(
specifier|final
name|Router
name|router
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|RouterQuotaUpdateService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
name|this
operator|.
name|rpcServer
operator|=
name|router
operator|.
name|getRpcServer
argument_list|()
expr_stmt|;
name|this
operator|.
name|quotaManager
operator|=
name|router
operator|.
name|getQuotaManager
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|quotaManager
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Router quota manager is not initialized."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|setIntervalMs
argument_list|(
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_QUOTA_CACHE_UPATE_INTERVAL
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_QUOTA_CACHE_UPATE_INTERVAL_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|periodicInvoke ()
specifier|protected
name|void
name|periodicInvoke
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Start to update quota cache."
argument_list|)
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|MountTable
argument_list|>
name|updateMountTables
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MountTable
argument_list|>
name|mountTables
init|=
name|getQuotaSetMountTables
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|RemoteLocation
argument_list|,
name|QuotaUsage
argument_list|>
name|remoteQuotaUsage
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MountTable
name|entry
range|:
name|mountTables
control|)
block|{
name|String
name|src
init|=
name|entry
operator|.
name|getSourcePath
argument_list|()
decl_stmt|;
name|RouterQuotaUsage
name|oldQuota
init|=
name|entry
operator|.
name|getQuota
argument_list|()
decl_stmt|;
name|long
name|nsQuota
init|=
name|oldQuota
operator|.
name|getQuota
argument_list|()
decl_stmt|;
name|long
name|ssQuota
init|=
name|oldQuota
operator|.
name|getSpaceQuota
argument_list|()
decl_stmt|;
name|QuotaUsage
name|currentQuotaUsage
init|=
literal|null
decl_stmt|;
comment|// Check whether destination path exists in filesystem. When the
comment|// mtime is zero, the destination is not present and reset the usage.
comment|// This is because mount table does not have mtime.
comment|// For other mount entry get current quota usage
name|HdfsFileStatus
name|ret
init|=
name|this
operator|.
name|rpcServer
operator|.
name|getFileInfo
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|null
operator|||
name|ret
operator|.
name|getModificationTime
argument_list|()
operator|==
literal|0
condition|)
block|{
name|currentQuotaUsage
operator|=
operator|new
name|RouterQuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
literal|0
argument_list|)
operator|.
name|quota
argument_list|(
name|nsQuota
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
literal|0
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|ssQuota
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Call RouterRpcServer#getQuotaUsage for getting current quota usage.
comment|// If any exception occurs catch it and proceed with other entries.
try|try
block|{
name|Quota
name|quotaModule
init|=
name|this
operator|.
name|rpcServer
operator|.
name|getQuotaModule
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|RemoteLocation
argument_list|,
name|QuotaUsage
argument_list|>
name|usageMap
init|=
name|quotaModule
operator|.
name|getEachQuotaUsage
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|currentQuotaUsage
operator|=
name|quotaModule
operator|.
name|aggregateQuota
argument_list|(
name|src
argument_list|,
name|usageMap
argument_list|)
expr_stmt|;
name|remoteQuotaUsage
operator|.
name|putAll
argument_list|(
name|usageMap
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to get quota usage for "
operator|+
name|src
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|RouterQuotaUsage
name|newQuota
init|=
name|generateNewQuota
argument_list|(
name|oldQuota
argument_list|,
name|currentQuotaUsage
argument_list|)
decl_stmt|;
name|this
operator|.
name|quotaManager
operator|.
name|put
argument_list|(
name|src
argument_list|,
name|newQuota
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setQuota
argument_list|(
name|newQuota
argument_list|)
expr_stmt|;
comment|// only update mount tables which quota was changed
if|if
condition|(
operator|!
name|oldQuota
operator|.
name|equals
argument_list|(
name|newQuota
argument_list|)
condition|)
block|{
name|updateMountTables
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Update quota usage entity of path: {}, nsCount: {},"
operator|+
literal|" nsQuota: {}, ssCount: {}, ssQuota: {}."
argument_list|,
name|src
argument_list|,
name|newQuota
operator|.
name|getFileAndDirectoryCount
argument_list|()
argument_list|,
name|newQuota
operator|.
name|getQuota
argument_list|()
argument_list|,
name|newQuota
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|,
name|newQuota
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Fix inconsistent quota.
for|for
control|(
name|Entry
argument_list|<
name|RemoteLocation
argument_list|,
name|QuotaUsage
argument_list|>
name|en
range|:
name|remoteQuotaUsage
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|RemoteLocation
name|remoteLocation
init|=
name|en
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|QuotaUsage
name|currentQuota
init|=
name|en
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|fixGlobalQuota
argument_list|(
name|remoteLocation
argument_list|,
name|currentQuota
argument_list|)
expr_stmt|;
block|}
name|updateMountTableEntries
argument_list|(
name|updateMountTables
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Quota cache updated error."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fixGlobalQuota (RemoteLocation location, QuotaUsage remoteQuota)
specifier|private
name|void
name|fixGlobalQuota
parameter_list|(
name|RemoteLocation
name|location
parameter_list|,
name|QuotaUsage
name|remoteQuota
parameter_list|)
throws|throws
name|IOException
block|{
name|QuotaUsage
name|gQuota
init|=
name|this
operator|.
name|rpcServer
operator|.
name|getQuotaModule
argument_list|()
operator|.
name|getGlobalQuota
argument_list|(
name|location
operator|.
name|getSrc
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteQuota
operator|.
name|getQuota
argument_list|()
operator|!=
name|gQuota
operator|.
name|getQuota
argument_list|()
operator|||
name|remoteQuota
operator|.
name|getSpaceQuota
argument_list|()
operator|!=
name|gQuota
operator|.
name|getSpaceQuota
argument_list|()
condition|)
block|{
name|this
operator|.
name|rpcServer
operator|.
name|getQuotaModule
argument_list|()
operator|.
name|setQuotaInternal
argument_list|(
name|location
operator|.
name|getSrc
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|location
argument_list|)
argument_list|,
name|gQuota
operator|.
name|getQuota
argument_list|()
argument_list|,
name|gQuota
operator|.
name|getSpaceQuota
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"[Fix Quota] src={} dst={} oldQuota={}/{} newQuota={}/{}"
argument_list|,
name|location
operator|.
name|getSrc
argument_list|()
argument_list|,
name|location
argument_list|,
name|remoteQuota
operator|.
name|getQuota
argument_list|()
argument_list|,
name|remoteQuota
operator|.
name|getSpaceQuota
argument_list|()
argument_list|,
name|gQuota
operator|.
name|getQuota
argument_list|()
argument_list|,
name|gQuota
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get mount table store management interface.    * @return MountTableStore instance.    * @throws IOException    */
DECL|method|getMountTableStore ()
specifier|private
name|MountTableStore
name|getMountTableStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|mountTableStore
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|mountTableStore
operator|=
name|router
operator|.
name|getStateStore
argument_list|()
operator|.
name|getRegisteredRecordStore
argument_list|(
name|MountTableStore
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|mountTableStore
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mount table state store is not available."
argument_list|)
throw|;
block|}
block|}
return|return
name|this
operator|.
name|mountTableStore
return|;
block|}
comment|/**    * Get all the existing mount tables.    * @return List of mount tables.    * @throws IOException    */
DECL|method|getMountTableEntries ()
specifier|private
name|List
argument_list|<
name|MountTable
argument_list|>
name|getMountTableEntries
parameter_list|()
throws|throws
name|IOException
block|{
comment|// scan mount tables from root path
name|GetMountTableEntriesRequest
name|getRequest
init|=
name|GetMountTableEntriesRequest
operator|.
name|newInstance
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|GetMountTableEntriesResponse
name|getResponse
init|=
name|getMountTableStore
argument_list|()
operator|.
name|getMountTableEntries
argument_list|(
name|getRequest
argument_list|)
decl_stmt|;
return|return
name|getResponse
operator|.
name|getEntries
argument_list|()
return|;
block|}
comment|/**    * Get mount tables which quota was set.    * During this time, the quota usage cache will also be updated by    * quota manager:    * 1. Stale paths (entries) will be removed.    * 2. Existing entries will be override and updated.    * @return List of mount tables which quota was set.    * @throws IOException    */
DECL|method|getQuotaSetMountTables ()
specifier|private
name|List
argument_list|<
name|MountTable
argument_list|>
name|getQuotaSetMountTables
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|MountTable
argument_list|>
name|mountTables
init|=
name|getMountTableEntries
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|allPaths
init|=
name|this
operator|.
name|quotaManager
operator|.
name|getAll
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|stalePaths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|allPaths
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MountTable
argument_list|>
name|neededMountTables
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MountTable
name|entry
range|:
name|mountTables
control|)
block|{
comment|// select mount tables which is quota set
if|if
condition|(
name|isQuotaSet
argument_list|(
name|entry
argument_list|)
condition|)
block|{
name|neededMountTables
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
comment|// update mount table entries info in quota cache
name|String
name|src
init|=
name|entry
operator|.
name|getSourcePath
argument_list|()
decl_stmt|;
name|this
operator|.
name|quotaManager
operator|.
name|put
argument_list|(
name|src
argument_list|,
name|entry
operator|.
name|getQuota
argument_list|()
argument_list|)
expr_stmt|;
name|stalePaths
operator|.
name|remove
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
comment|// remove stale paths that currently cached
for|for
control|(
name|String
name|stalePath
range|:
name|stalePaths
control|)
block|{
name|this
operator|.
name|quotaManager
operator|.
name|remove
argument_list|(
name|stalePath
argument_list|)
expr_stmt|;
block|}
return|return
name|neededMountTables
return|;
block|}
comment|/**    * Check if the quota was set in given MountTable.    * @param mountTable Mount table entry.    */
DECL|method|isQuotaSet (MountTable mountTable)
specifier|private
name|boolean
name|isQuotaSet
parameter_list|(
name|MountTable
name|mountTable
parameter_list|)
block|{
if|if
condition|(
name|mountTable
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|quotaManager
operator|.
name|isQuotaSet
argument_list|(
name|mountTable
operator|.
name|getQuota
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Generate a new quota based on old quota and current quota usage value.    * @param oldQuota Old quota stored in State Store.    * @param currentQuotaUsage Current quota usage value queried from    *        subcluster.    * @return A new RouterQuotaUsage.    */
DECL|method|generateNewQuota (RouterQuotaUsage oldQuota, QuotaUsage currentQuotaUsage)
specifier|private
name|RouterQuotaUsage
name|generateNewQuota
parameter_list|(
name|RouterQuotaUsage
name|oldQuota
parameter_list|,
name|QuotaUsage
name|currentQuotaUsage
parameter_list|)
block|{
name|RouterQuotaUsage
name|newQuota
init|=
operator|new
name|RouterQuotaUsage
operator|.
name|Builder
argument_list|()
operator|.
name|fileAndDirectoryCount
argument_list|(
name|currentQuotaUsage
operator|.
name|getFileAndDirectoryCount
argument_list|()
argument_list|)
operator|.
name|quota
argument_list|(
name|oldQuota
operator|.
name|getQuota
argument_list|()
argument_list|)
operator|.
name|spaceConsumed
argument_list|(
name|currentQuotaUsage
operator|.
name|getSpaceConsumed
argument_list|()
argument_list|)
operator|.
name|spaceQuota
argument_list|(
name|oldQuota
operator|.
name|getSpaceQuota
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|newQuota
return|;
block|}
comment|/**    * Write out updated mount table entries into State Store.    * @param updateMountTables Mount tables to be updated.    * @throws IOException    */
DECL|method|updateMountTableEntries (List<MountTable> updateMountTables)
specifier|private
name|void
name|updateMountTableEntries
parameter_list|(
name|List
argument_list|<
name|MountTable
argument_list|>
name|updateMountTables
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|MountTable
name|entry
range|:
name|updateMountTables
control|)
block|{
name|UpdateMountTableEntryRequest
name|updateRequest
init|=
name|UpdateMountTableEntryRequest
operator|.
name|newInstance
argument_list|(
name|entry
argument_list|)
decl_stmt|;
try|try
block|{
name|getMountTableStore
argument_list|()
operator|.
name|updateMountTableEntry
argument_list|(
name|updateRequest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Quota update error for mount entry "
operator|+
name|entry
operator|.
name|getSourcePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

