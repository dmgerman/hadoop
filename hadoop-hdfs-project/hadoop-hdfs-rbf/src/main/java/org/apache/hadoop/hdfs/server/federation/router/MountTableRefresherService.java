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
name|net
operator|.
name|InetSocketAddress
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|ExecutionException
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
name|RouterStore
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
name|StateStoreUnavailableException
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
name|StateStoreUtils
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
name|RouterState
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
name|net
operator|.
name|NetUtils
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|cache
operator|.
name|RemovalListener
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
name|cache
operator|.
name|RemovalNotification
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
comment|/**  * This service is invoked from {@link MountTableStore} when there is change in  * mount table entries and it updates mount table entry cache on local router as  * well as on all remote routers. Refresh on local router is done by calling  * {@link MountTableStore#loadCache(boolean)}} API directly, no RPC call  * involved, but on remote routers refresh is done through RouterClient(RPC  * call). To improve performance, all routers are refreshed in separate thread  * and all connection are cached. Cached connections are removed from  * cache and closed when their max live time is elapsed.  */
end_comment

begin_class
DECL|class|MountTableRefresherService
specifier|public
class|class
name|MountTableRefresherService
extends|extends
name|AbstractService
block|{
DECL|field|ROUTER_CONNECT_ERROR_MSG
specifier|private
specifier|static
specifier|final
name|String
name|ROUTER_CONNECT_ERROR_MSG
init|=
literal|"Router {} connection failed. Mount table cache will not refresh."
decl_stmt|;
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
name|MountTableRefresherService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Local router. */
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
comment|/** Mount table store. */
DECL|field|mountTableStore
specifier|private
name|MountTableStore
name|mountTableStore
decl_stmt|;
comment|/** Local router admin address in the form of host:port. */
DECL|field|localAdminAddress
specifier|private
name|String
name|localAdminAddress
decl_stmt|;
comment|/** Timeout in ms to update mount table cache on all the routers. */
DECL|field|cacheUpdateTimeout
specifier|private
name|long
name|cacheUpdateTimeout
decl_stmt|;
comment|/**    * All router admin clients cached. So no need to create the client again and    * again. Router admin address(host:port) is used as key to cache RouterClient    * objects.    */
DECL|field|routerClientsCache
specifier|private
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|RouterClient
argument_list|>
name|routerClientsCache
decl_stmt|;
comment|/**    * Removes expired RouterClient from routerClientsCache.    */
DECL|field|clientCacheCleanerScheduler
specifier|private
name|ScheduledExecutorService
name|clientCacheCleanerScheduler
decl_stmt|;
comment|/**    * Create a new service to refresh mount table cache when there is change in    * mount table entries.    *    * @param router whose mount table cache will be refreshed    */
DECL|method|MountTableRefresherService (Router router)
specifier|public
name|MountTableRefresherService
parameter_list|(
name|Router
name|router
parameter_list|)
block|{
name|super
argument_list|(
name|MountTableRefresherService
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
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
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|mountTableStore
operator|=
name|getMountTableStore
argument_list|()
expr_stmt|;
comment|// Attach this service to mount table store.
name|this
operator|.
name|mountTableStore
operator|.
name|setRefreshService
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|localAdminAddress
operator|=
name|StateStoreUtils
operator|.
name|getHostPortString
argument_list|(
name|router
operator|.
name|getAdminServerAddress
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|cacheUpdateTimeout
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|RBFConfigKeys
operator|.
name|MOUNT_TABLE_CACHE_UPDATE_TIMEOUT
argument_list|,
name|RBFConfigKeys
operator|.
name|MOUNT_TABLE_CACHE_UPDATE_TIMEOUT_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|long
name|routerClientMaxLiveTime
init|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|RBFConfigKeys
operator|.
name|MOUNT_TABLE_CACHE_UPDATE_CLIENT_MAX_TIME
argument_list|,
name|RBFConfigKeys
operator|.
name|MOUNT_TABLE_CACHE_UPDATE_CLIENT_MAX_TIME_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|routerClientsCache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterWrite
argument_list|(
name|routerClientMaxLiveTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|removalListener
argument_list|(
name|getClientRemover
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|getClientCreator
argument_list|()
argument_list|)
expr_stmt|;
name|initClientCacheCleaner
argument_list|(
name|routerClientMaxLiveTime
argument_list|)
expr_stmt|;
block|}
DECL|method|initClientCacheCleaner (long routerClientMaxLiveTime)
specifier|private
name|void
name|initClientCacheCleaner
parameter_list|(
name|long
name|routerClientMaxLiveTime
parameter_list|)
block|{
name|clientCacheCleanerScheduler
operator|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"MountTableRefresh_ClientsCacheCleaner"
argument_list|)
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|/*      * When cleanUp() method is called, expired RouterClient will be removed and      * closed.      */
name|clientCacheCleanerScheduler
operator|.
name|scheduleWithFixedDelay
argument_list|(
parameter_list|()
lambda|->
name|routerClientsCache
operator|.
name|cleanUp
argument_list|()
argument_list|,
name|routerClientMaxLiveTime
argument_list|,
name|routerClientMaxLiveTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create cache entry remove listener.    */
DECL|method|getClientRemover ()
specifier|private
name|RemovalListener
argument_list|<
name|String
argument_list|,
name|RouterClient
argument_list|>
name|getClientRemover
parameter_list|()
block|{
return|return
operator|new
name|RemovalListener
argument_list|<
name|String
argument_list|,
name|RouterClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|String
argument_list|,
name|RouterClient
argument_list|>
name|notification
parameter_list|)
block|{
name|closeRouterClient
argument_list|(
name|notification
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|closeRouterClient (RouterClient client)
specifier|protected
name|void
name|closeRouterClient
parameter_list|(
name|RouterClient
name|client
parameter_list|)
block|{
try|try
block|{
name|client
operator|.
name|close
argument_list|()
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
literal|"Error while closing RouterClient"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates RouterClient and caches it.    */
DECL|method|getClientCreator ()
specifier|private
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|RouterClient
argument_list|>
name|getClientCreator
parameter_list|()
block|{
return|return
operator|new
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|RouterClient
argument_list|>
argument_list|()
block|{
specifier|public
name|RouterClient
name|load
parameter_list|(
name|String
name|adminAddress
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|routerSocket
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|adminAddress
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
name|getConfig
argument_list|()
decl_stmt|;
return|return
name|createRouterClient
argument_list|(
name|routerSocket
argument_list|,
name|config
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|createRouterClient (InetSocketAddress routerSocket, Configuration config)
specifier|protected
name|RouterClient
name|createRouterClient
parameter_list|(
name|InetSocketAddress
name|routerSocket
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RouterClient
argument_list|(
name|routerSocket
argument_list|,
name|config
argument_list|)
return|;
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
name|super
operator|.
name|serviceStart
argument_list|()
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
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
name|clientCacheCleanerScheduler
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// remove and close all admin clients
name|routerClientsCache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
DECL|method|getMountTableStore ()
specifier|private
name|MountTableStore
name|getMountTableStore
parameter_list|()
throws|throws
name|IOException
block|{
name|MountTableStore
name|mountTblStore
init|=
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
decl_stmt|;
if|if
condition|(
name|mountTblStore
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
return|return
name|mountTblStore
return|;
block|}
comment|/**    * Refresh mount table cache of this router as well as all other routers.    */
DECL|method|refresh ()
specifier|public
name|void
name|refresh
parameter_list|()
throws|throws
name|StateStoreUnavailableException
block|{
name|RouterStore
name|routerStore
init|=
name|router
operator|.
name|getRouterStateManager
argument_list|()
decl_stmt|;
try|try
block|{
name|routerStore
operator|.
name|loadCache
argument_list|(
literal|true
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
name|warn
argument_list|(
literal|"RouterStore load cache failed,"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|RouterState
argument_list|>
name|cachedRecords
init|=
name|routerStore
operator|.
name|getCachedRecords
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MountTableRefresherThread
argument_list|>
name|refreshThreads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RouterState
name|routerState
range|:
name|cachedRecords
control|)
block|{
name|String
name|adminAddress
init|=
name|routerState
operator|.
name|getAdminAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|adminAddress
operator|==
literal|null
operator|||
name|adminAddress
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// this router has not enabled router admin.
continue|continue;
block|}
comment|// No use of calling refresh on router which is not running state
if|if
condition|(
name|routerState
operator|.
name|getStatus
argument_list|()
operator|!=
name|RouterServiceState
operator|.
name|RUNNING
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Router {} is not running. Mount table cache will not refresh."
argument_list|,
name|routerState
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove if RouterClient is cached.
name|removeFromCache
argument_list|(
name|adminAddress
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isLocalAdmin
argument_list|(
name|adminAddress
argument_list|)
condition|)
block|{
comment|/*          * Local router's cache update does not require RPC call, so no need for          * RouterClient          */
name|refreshThreads
operator|.
name|add
argument_list|(
name|getLocalRefresher
argument_list|(
name|adminAddress
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|RouterClient
name|client
init|=
name|routerClientsCache
operator|.
name|get
argument_list|(
name|adminAddress
argument_list|)
decl_stmt|;
name|refreshThreads
operator|.
name|add
argument_list|(
operator|new
name|MountTableRefresherThread
argument_list|(
name|client
operator|.
name|getMountTableManager
argument_list|()
argument_list|,
name|adminAddress
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|execExcep
parameter_list|)
block|{
comment|// Can not connect, seems router is stopped now.
name|LOG
operator|.
name|warn
argument_list|(
name|ROUTER_CONNECT_ERROR_MSG
argument_list|,
name|adminAddress
argument_list|,
name|execExcep
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|refreshThreads
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|invokeRefresh
argument_list|(
name|refreshThreads
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getLocalRefresher (String adminAddress)
specifier|protected
name|MountTableRefresherThread
name|getLocalRefresher
parameter_list|(
name|String
name|adminAddress
parameter_list|)
block|{
return|return
operator|new
name|MountTableRefresherThread
argument_list|(
name|router
operator|.
name|getAdminServer
argument_list|()
argument_list|,
name|adminAddress
argument_list|)
return|;
block|}
DECL|method|removeFromCache (String adminAddress)
specifier|private
name|void
name|removeFromCache
parameter_list|(
name|String
name|adminAddress
parameter_list|)
block|{
name|routerClientsCache
operator|.
name|invalidate
argument_list|(
name|adminAddress
argument_list|)
expr_stmt|;
block|}
DECL|method|invokeRefresh (List<MountTableRefresherThread> refreshThreads)
specifier|private
name|void
name|invokeRefresh
parameter_list|(
name|List
argument_list|<
name|MountTableRefresherThread
argument_list|>
name|refreshThreads
parameter_list|)
block|{
name|CountDownLatch
name|countDownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|refreshThreads
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// start all the threads
for|for
control|(
name|MountTableRefresherThread
name|refThread
range|:
name|refreshThreads
control|)
block|{
name|refThread
operator|.
name|setCountDownLatch
argument_list|(
name|countDownLatch
argument_list|)
expr_stmt|;
name|refThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|/*        * Wait for all the thread to complete, await method returns false if        * refresh is not finished within specified time        */
name|boolean
name|allReqCompleted
init|=
name|countDownLatch
operator|.
name|await
argument_list|(
name|cacheUpdateTimeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|allReqCompleted
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Not all router admins updated their cache"
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
name|error
argument_list|(
literal|"Mount table cache refresher was interrupted."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|logResult
argument_list|(
name|refreshThreads
argument_list|)
expr_stmt|;
block|}
DECL|method|isLocalAdmin (String adminAddress)
specifier|private
name|boolean
name|isLocalAdmin
parameter_list|(
name|String
name|adminAddress
parameter_list|)
block|{
return|return
name|adminAddress
operator|.
name|contentEquals
argument_list|(
name|localAdminAddress
argument_list|)
return|;
block|}
DECL|method|logResult (List<MountTableRefresherThread> refreshThreads)
specifier|private
name|void
name|logResult
parameter_list|(
name|List
argument_list|<
name|MountTableRefresherThread
argument_list|>
name|refreshThreads
parameter_list|)
block|{
name|int
name|successCount
init|=
literal|0
decl_stmt|;
name|int
name|failureCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MountTableRefresherThread
name|mountTableRefreshThread
range|:
name|refreshThreads
control|)
block|{
if|if
condition|(
name|mountTableRefreshThread
operator|.
name|isSuccess
argument_list|()
condition|)
block|{
name|successCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|failureCount
operator|++
expr_stmt|;
comment|// remove RouterClient from cache so that new client is created
name|removeFromCache
argument_list|(
name|mountTableRefreshThread
operator|.
name|getAdminAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Mount table entries cache refresh successCount={},failureCount={}"
argument_list|,
name|successCount
argument_list|,
name|failureCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

