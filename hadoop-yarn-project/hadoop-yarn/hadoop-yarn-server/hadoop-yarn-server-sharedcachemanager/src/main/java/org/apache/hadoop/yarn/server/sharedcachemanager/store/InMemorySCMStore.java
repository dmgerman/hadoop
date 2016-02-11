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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|ScheduledExecutorService
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
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|HadoopIllegalArgumentException
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
name|StringInterner
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
name|concurrent
operator|.
name|HadoopExecutors
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
name|conf
operator|.
name|YarnConfiguration
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
name|sharedcache
operator|.
name|SharedCacheUtil
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
name|sharedcachemanager
operator|.
name|AppChecker
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
name|annotations
operator|.
name|VisibleForTesting
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

begin_comment
comment|/**  * A thread safe version of an in-memory SCM store. The thread safety is  * implemented with two key pieces: (1) at the mapping level a ConcurrentHashMap  * is used to allow concurrency to resources and their associated references,  * and (2) a key level lock is used to ensure mutual exclusion between any  * operation that accesses a resource with the same key.<br>  *<br>  * To ensure safe key-level locking, we use the original string key and intern  * it weakly using hadoop's<code>StringInterner</code>. It avoids the pitfalls  * of using built-in String interning. The interned strings are also weakly  * referenced, so it can be garbage collected once it is done. And there is  * little risk of keys being available for other parts of the code so they can  * be used as locks accidentally.<br>  *<br>  * Resources in the in-memory store are evicted based on a time staleness  * criteria. If a resource is not referenced (i.e. used) for a given period, it  * is designated as a stale resource and is considered evictable.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|InMemorySCMStore
specifier|public
class|class
name|InMemorySCMStore
extends|extends
name|SCMStore
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|InMemorySCMStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cachedResources
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SharedCacheResource
argument_list|>
name|cachedResources
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|SharedCacheResource
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|initialApps
specifier|private
name|Collection
argument_list|<
name|ApplicationId
argument_list|>
name|initialApps
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|initialAppsLock
specifier|private
specifier|final
name|Object
name|initialAppsLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|stalenessMinutes
specifier|private
name|int
name|stalenessMinutes
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ScheduledExecutorService
name|scheduler
decl_stmt|;
DECL|field|initialDelayMin
specifier|private
name|int
name|initialDelayMin
decl_stmt|;
DECL|field|checkPeriodMin
specifier|private
name|int
name|checkPeriodMin
decl_stmt|;
DECL|method|InMemorySCMStore ()
specifier|public
name|InMemorySCMStore
parameter_list|()
block|{
name|super
argument_list|(
name|InMemorySCMStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|InMemorySCMStore (AppChecker appChecker)
specifier|public
name|InMemorySCMStore
parameter_list|(
name|AppChecker
name|appChecker
parameter_list|)
block|{
name|super
argument_list|(
name|InMemorySCMStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|appChecker
argument_list|)
expr_stmt|;
block|}
DECL|method|intern (String key)
specifier|private
name|String
name|intern
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|StringInterner
operator|.
name|weakIntern
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * The in-memory store bootstraps itself from the shared cache entries that    * exist in HDFS.    */
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
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|initialDelayMin
operator|=
name|getInitialDelay
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|checkPeriodMin
operator|=
name|getCheckPeriod
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|stalenessMinutes
operator|=
name|getStalenessPeriod
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|bootstrap
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"InMemorySCMStore"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|scheduler
operator|=
name|HadoopExecutors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|(
name|tf
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
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// start composed services first
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
comment|// Get initial list of running applications
name|LOG
operator|.
name|info
argument_list|(
literal|"Getting the active app list to initialize the in-memory scm store"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|initialAppsLock
init|)
block|{
name|initialApps
operator|=
name|appChecker
operator|.
name|getActiveApplications
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|initialApps
operator|.
name|size
argument_list|()
operator|+
literal|" apps recorded as active at this time"
argument_list|)
expr_stmt|;
name|Runnable
name|task
init|=
operator|new
name|AppCheckTask
argument_list|(
name|appChecker
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|scheduleAtFixedRate
argument_list|(
name|task
argument_list|,
name|initialDelayMin
argument_list|,
name|checkPeriodMin
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduled the in-memory scm store app check task to run every "
operator|+
name|checkPeriodMin
operator|+
literal|" minutes."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping the "
operator|+
name|InMemorySCMStore
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" service."
argument_list|)
expr_stmt|;
if|if
condition|(
name|scheduler
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down the background thread."
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|scheduler
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Gave up waiting for the app check task to shutdown."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The InMemorySCMStore was interrupted while shutting down the "
operator|+
literal|"app check task."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"The background thread stopped."
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|bootstrap (Configuration conf)
specifier|private
name|void
name|bootstrap
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initialCachedResources
init|=
name|getInitialCachedResources
argument_list|(
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Bootstrapping from "
operator|+
name|initialCachedResources
operator|.
name|size
argument_list|()
operator|+
literal|" cache resources located in the file system"
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|initialCachedResources
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|intern
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|SharedCacheResource
name|resource
init|=
operator|new
name|SharedCacheResource
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
comment|// we don't hold the lock for this as it is done as part of serviceInit
name|cachedResources
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|resource
argument_list|)
expr_stmt|;
comment|// clear out the initial resource to reduce the footprint
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Bootstrapping complete"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getInitialCachedResources (FileSystem fs, Configuration conf)
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getInitialCachedResources
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get the root directory for the shared cache
name|String
name|location
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|SHARED_CACHE_ROOT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_SHARED_CACHE_ROOT
argument_list|)
decl_stmt|;
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"The shared cache root directory "
operator|+
name|location
operator|+
literal|" was not found"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|)
throw|;
block|}
name|int
name|nestedLevel
init|=
name|SharedCacheUtil
operator|.
name|getCacheDepth
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// now traverse individual directories and process them
comment|// the directory structure is specified by the nested level parameter
comment|// (e.g. 9/c/d/<checksum>/file)
name|String
name|pattern
init|=
name|SharedCacheUtil
operator|.
name|getCacheEntryGlobPattern
argument_list|(
name|nestedLevel
operator|+
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Querying for all individual cached resource files"
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|entries
init|=
name|fs
operator|.
name|globStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|root
argument_list|,
name|pattern
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numEntries
init|=
name|entries
operator|==
literal|null
condition|?
literal|0
else|:
name|entries
operator|.
name|length
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found "
operator|+
name|numEntries
operator|+
literal|" files: processing for one resource per "
operator|+
literal|"key"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initialCachedEntries
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|entries
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|FileStatus
name|entry
range|:
name|entries
control|)
block|{
name|Path
name|file
init|=
name|entry
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|file
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|isFile
argument_list|()
condition|)
block|{
comment|// get the parent to get the checksum
name|Path
name|parent
init|=
name|file
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
comment|// the name of the immediate parent directory is the checksum
name|String
name|key
init|=
name|parent
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// make sure we insert only one file per checksum whichever comes
comment|// first
if|if
condition|(
name|initialCachedEntries
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Key "
operator|+
name|key
operator|+
literal|" is already mapped to file "
operator|+
name|initialCachedEntries
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|+
literal|"; file "
operator|+
name|fileName
operator|+
literal|" will not be added"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|initialCachedEntries
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"A total of "
operator|+
name|initialCachedEntries
operator|.
name|size
argument_list|()
operator|+
literal|" files are now mapped"
argument_list|)
expr_stmt|;
return|return
name|initialCachedEntries
return|;
block|}
comment|/**    * Adds the given resource to the store under the key and the filename. If the    * entry is already found, it returns the existing filename. It represents the    * state of the store at the time of this query. The entry may change or even    * be removed once this method returns. The caller should be prepared to    * handle that situation.    *     * @return the filename of the newly inserted resource or that of the existing    *         resource    */
annotation|@
name|Override
DECL|method|addResource (String key, String fileName)
specifier|public
name|String
name|addResource
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|fileName
parameter_list|)
block|{
name|String
name|interned
init|=
name|intern
argument_list|(
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|interned
init|)
block|{
name|SharedCacheResource
name|resource
init|=
name|cachedResources
operator|.
name|get
argument_list|(
name|interned
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
name|resource
operator|=
operator|new
name|SharedCacheResource
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|cachedResources
operator|.
name|put
argument_list|(
name|interned
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
return|return
name|resource
operator|.
name|getFileName
argument_list|()
return|;
block|}
block|}
comment|/**    * Adds the provided resource reference to the cache resource under the key,    * and updates the access time. If it returns a non-null value, the caller may    * safely assume that the resource will not be removed at least until the app    * in this resource reference has terminated.    *     * @return the filename of the resource, or null if the resource is not found    */
annotation|@
name|Override
DECL|method|addResourceReference (String key, SharedCacheResourceReference ref)
specifier|public
name|String
name|addResourceReference
parameter_list|(
name|String
name|key
parameter_list|,
name|SharedCacheResourceReference
name|ref
parameter_list|)
block|{
name|String
name|interned
init|=
name|intern
argument_list|(
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|interned
init|)
block|{
name|SharedCacheResource
name|resource
init|=
name|cachedResources
operator|.
name|get
argument_list|(
name|interned
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
comment|// it's not mapped
return|return
literal|null
return|;
block|}
name|resource
operator|.
name|addReference
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|resource
operator|.
name|updateAccessTime
argument_list|()
expr_stmt|;
return|return
name|resource
operator|.
name|getFileName
argument_list|()
return|;
block|}
block|}
comment|/**    * Returns the list of resource references currently registered under the    * cache entry. If the list is empty, it returns an empty collection. The    * returned collection is unmodifiable and a snapshot of the information at    * the time of the query. The state may change after this query returns. The    * caller should handle the situation that some or all of these resource    * references are no longer relevant.    *     * @return the collection that contains the resource references associated    *         with the resource; or an empty collection if no resource references    *         are registered under this resource    */
annotation|@
name|Override
DECL|method|getResourceReferences (String key)
specifier|public
name|Collection
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|getResourceReferences
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|String
name|interned
init|=
name|intern
argument_list|(
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|interned
init|)
block|{
name|SharedCacheResource
name|resource
init|=
name|cachedResources
operator|.
name|get
argument_list|(
name|interned
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
name|Set
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|refs
init|=
operator|new
name|HashSet
argument_list|<
name|SharedCacheResourceReference
argument_list|>
argument_list|(
name|resource
operator|.
name|getResourceReferences
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|refs
argument_list|)
return|;
block|}
block|}
comment|/**    * Removes the provided resource reference from the resource. If the resource    * does not exist, nothing will be done.    */
annotation|@
name|Override
DECL|method|removeResourceReference (String key, SharedCacheResourceReference ref, boolean updateAccessTime)
specifier|public
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
block|{
name|String
name|interned
init|=
name|intern
argument_list|(
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|interned
init|)
block|{
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
name|SharedCacheResource
name|resource
init|=
name|cachedResources
operator|.
name|get
argument_list|(
name|interned
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|resourceRefs
init|=
name|resource
operator|.
name|getResourceReferences
argument_list|()
decl_stmt|;
name|removed
operator|=
name|resourceRefs
operator|.
name|remove
argument_list|(
name|ref
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateAccessTime
condition|)
block|{
name|resource
operator|.
name|updateAccessTime
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|removed
return|;
block|}
block|}
comment|/**    * Removes the provided collection of resource references from the resource.    * If the resource does not exist, nothing will be done.    */
annotation|@
name|Override
DECL|method|removeResourceReferences (String key, Collection<SharedCacheResourceReference> refs, boolean updateAccessTime)
specifier|public
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
block|{
name|String
name|interned
init|=
name|intern
argument_list|(
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|interned
init|)
block|{
name|SharedCacheResource
name|resource
init|=
name|cachedResources
operator|.
name|get
argument_list|(
name|interned
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|SharedCacheResourceReference
argument_list|>
name|resourceRefs
init|=
name|resource
operator|.
name|getResourceReferences
argument_list|()
decl_stmt|;
name|resourceRefs
operator|.
name|removeAll
argument_list|(
name|refs
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateAccessTime
condition|)
block|{
name|resource
operator|.
name|updateAccessTime
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Provides atomicity for the method.    */
annotation|@
name|Override
DECL|method|cleanResourceReferences (String key)
specifier|public
name|void
name|cleanResourceReferences
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|YarnException
block|{
name|String
name|interned
init|=
name|intern
argument_list|(
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|interned
init|)
block|{
name|super
operator|.
name|cleanResourceReferences
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Removes the given resource from the store. Returns true if the resource is    * found and removed or if the resource is not found. Returns false if it was    * unable to remove the resource because the resource reference list was not    * empty.    */
annotation|@
name|Override
DECL|method|removeResource (String key)
specifier|public
name|boolean
name|removeResource
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|String
name|interned
init|=
name|intern
argument_list|(
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|interned
init|)
block|{
name|SharedCacheResource
name|resource
init|=
name|cachedResources
operator|.
name|get
argument_list|(
name|interned
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|resource
operator|.
name|getResourceReferences
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// no users
name|cachedResources
operator|.
name|remove
argument_list|(
name|interned
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Obtains the access time for a resource. It represents the view of the    * resource at the time of the query. The value may have been updated at a    * later point.    *     * @return the access time of the resource if found; -1 if the resource is not    *         found    */
annotation|@
name|VisibleForTesting
DECL|method|getAccessTime (String key)
name|long
name|getAccessTime
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|String
name|interned
init|=
name|intern
argument_list|(
name|key
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|interned
init|)
block|{
name|SharedCacheResource
name|resource
init|=
name|cachedResources
operator|.
name|get
argument_list|(
name|interned
argument_list|)
decl_stmt|;
return|return
name|resource
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|resource
operator|.
name|getAccessTime
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|isResourceEvictable (String key, FileStatus file)
specifier|public
name|boolean
name|isResourceEvictable
parameter_list|(
name|String
name|key
parameter_list|,
name|FileStatus
name|file
parameter_list|)
block|{
synchronized|synchronized
init|(
name|initialAppsLock
init|)
block|{
if|if
condition|(
name|initialApps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|long
name|staleTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
name|this
operator|.
name|stalenessMinutes
argument_list|)
decl_stmt|;
name|long
name|accessTime
init|=
name|getAccessTime
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|accessTime
operator|==
operator|-
literal|1
condition|)
block|{
comment|// check modification time
name|long
name|modTime
init|=
name|file
operator|.
name|getModificationTime
argument_list|()
decl_stmt|;
comment|// if modification time is older then the store startup time, we need to
comment|// just use the store startup time as the last point of certainty
name|long
name|lastUse
init|=
name|modTime
operator|<
name|this
operator|.
name|startTime
condition|?
name|this
operator|.
name|startTime
else|:
name|modTime
decl_stmt|;
return|return
name|lastUse
operator|<
name|staleTime
return|;
block|}
else|else
block|{
comment|// check access time
return|return
name|accessTime
operator|<
name|staleTime
return|;
block|}
block|}
DECL|method|getStalenessPeriod (Configuration conf)
specifier|private
specifier|static
name|int
name|getStalenessPeriod
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|stalenessMinutes
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|IN_MEMORY_STALENESS_PERIOD_MINS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_IN_MEMORY_STALENESS_PERIOD_MINS
argument_list|)
decl_stmt|;
comment|// non-positive value is invalid; use the default
if|if
condition|(
name|stalenessMinutes
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Non-positive staleness value: "
operator|+
name|stalenessMinutes
operator|+
literal|". The staleness value must be greater than zero."
argument_list|)
throw|;
block|}
return|return
name|stalenessMinutes
return|;
block|}
DECL|method|getInitialDelay (Configuration conf)
specifier|private
specifier|static
name|int
name|getInitialDelay
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|initialMinutes
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|IN_MEMORY_INITIAL_DELAY_MINS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_IN_MEMORY_INITIAL_DELAY_MINS
argument_list|)
decl_stmt|;
comment|// non-positive value is invalid; use the default
if|if
condition|(
name|initialMinutes
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Non-positive initial delay value: "
operator|+
name|initialMinutes
operator|+
literal|". The initial delay value must be greater than zero."
argument_list|)
throw|;
block|}
return|return
name|initialMinutes
return|;
block|}
DECL|method|getCheckPeriod (Configuration conf)
specifier|private
specifier|static
name|int
name|getCheckPeriod
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|checkMinutes
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|IN_MEMORY_CHECK_PERIOD_MINS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_IN_MEMORY_CHECK_PERIOD_MINS
argument_list|)
decl_stmt|;
comment|// non-positive value is invalid; use the default
if|if
condition|(
name|checkMinutes
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Non-positive check period value: "
operator|+
name|checkMinutes
operator|+
literal|". The check period value must be greater than zero."
argument_list|)
throw|;
block|}
return|return
name|checkMinutes
return|;
block|}
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|AppCheckTask
class|class
name|AppCheckTask
implements|implements
name|Runnable
block|{
DECL|field|taskAppChecker
specifier|private
specifier|final
name|AppChecker
name|taskAppChecker
decl_stmt|;
DECL|method|AppCheckTask (AppChecker appChecker)
specifier|public
name|AppCheckTask
parameter_list|(
name|AppChecker
name|appChecker
parameter_list|)
block|{
name|this
operator|.
name|taskAppChecker
operator|=
name|appChecker
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking the initial app list for finished applications."
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|initialAppsLock
init|)
block|{
if|if
condition|(
name|initialApps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we're fine, no-op; there are no active apps that were running at
comment|// the time of the service start
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Looking into "
operator|+
name|initialApps
operator|.
name|size
argument_list|()
operator|+
literal|" apps to see if they are still active"
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ApplicationId
argument_list|>
name|it
init|=
name|initialApps
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ApplicationId
name|id
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|taskAppChecker
operator|.
name|isApplicationActive
argument_list|(
name|id
argument_list|)
condition|)
block|{
comment|// remove it from the list
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while checking the app status;"
operator|+
literal|" will leave the entry in the list"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// continue
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"There are now "
operator|+
name|initialApps
operator|.
name|size
argument_list|()
operator|+
literal|" entries in the list"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected exception thrown during in-memory store app check task."
operator|+
literal|" Rescheduling task."
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

