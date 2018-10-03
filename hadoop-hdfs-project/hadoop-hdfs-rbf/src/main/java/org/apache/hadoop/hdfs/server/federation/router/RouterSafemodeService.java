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
name|util
operator|.
name|Time
operator|.
name|now
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
name|StateStoreService
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
comment|/**  * Service to periodically check if the {@link  * org.apache.hadoop.hdfs.server.federation.store.StateStoreService  * StateStoreService} cached information in the {@link Router} is up to date.  * This is for performance and removes the {@link  * org.apache.hadoop.hdfs.server.federation.store.StateStoreService  * StateStoreService} from the critical path in common operations.  */
end_comment

begin_class
DECL|class|RouterSafemodeService
specifier|public
class|class
name|RouterSafemodeService
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
name|RouterSafemodeService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Router to manage safe mode. */
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
comment|/**    * If we are in safe mode, fail requests as if a standby NN.    * Router can enter safe mode in two different ways:    *<ul>    *<li>Upon start up: router enters this mode after service start, and will    * exit after certain time threshold.    *<li>Via admin command:    *<ul>    *<li>Router enters this mode via admin command:    * dfsrouteradmin -safemode enter    *<li>And exit after admin command:    * dfsrouteradmin -safemode leave    *</ul>    *</ul>    */
comment|/** Whether Router is in safe mode */
DECL|field|safeMode
specifier|private
specifier|volatile
name|boolean
name|safeMode
decl_stmt|;
comment|/** Whether the Router safe mode is set manually (i.e., via Router admin) */
DECL|field|isSafeModeSetManually
specifier|private
specifier|volatile
name|boolean
name|isSafeModeSetManually
decl_stmt|;
comment|/** Interval in ms to wait post startup before allowing RPC requests. */
DECL|field|startupInterval
specifier|private
name|long
name|startupInterval
decl_stmt|;
comment|/** Interval in ms after which the State Store cache is too stale. */
DECL|field|staleInterval
specifier|private
name|long
name|staleInterval
decl_stmt|;
comment|/** Start time in ms of this service. */
DECL|field|startupTime
specifier|private
name|long
name|startupTime
decl_stmt|;
comment|/** The time the Router enters safe mode in milliseconds. */
DECL|field|enterSafeModeTime
specifier|private
name|long
name|enterSafeModeTime
init|=
name|now
argument_list|()
decl_stmt|;
comment|/**    * Create a new Cache update service.    *    * @param router Router containing the cache.    */
DECL|method|RouterSafemodeService (Router router)
specifier|public
name|RouterSafemodeService
parameter_list|(
name|Router
name|router
parameter_list|)
block|{
name|super
argument_list|(
name|RouterSafemodeService
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
comment|/**    * Return whether the current Router is in safe mode.    */
DECL|method|isInSafeMode ()
name|boolean
name|isInSafeMode
parameter_list|()
block|{
return|return
name|this
operator|.
name|safeMode
return|;
block|}
comment|/**    * Set the flag to indicate that the safe mode for this Router is set manually    * via the Router admin command.    */
DECL|method|setManualSafeMode (boolean mode)
name|void
name|setManualSafeMode
parameter_list|(
name|boolean
name|mode
parameter_list|)
block|{
name|this
operator|.
name|safeMode
operator|=
name|mode
expr_stmt|;
name|this
operator|.
name|isSafeModeSetManually
operator|=
name|mode
expr_stmt|;
block|}
comment|/**    * Enter safe mode.    */
DECL|method|enter ()
specifier|private
name|void
name|enter
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Entering safe mode"
argument_list|)
expr_stmt|;
name|enterSafeModeTime
operator|=
name|now
argument_list|()
expr_stmt|;
name|safeMode
operator|=
literal|true
expr_stmt|;
name|router
operator|.
name|updateRouterState
argument_list|(
name|RouterServiceState
operator|.
name|SAFEMODE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Leave safe mode.    */
DECL|method|leave ()
specifier|private
name|void
name|leave
parameter_list|()
block|{
comment|// Cache recently updated, leave safemode
name|long
name|timeInSafemode
init|=
name|now
argument_list|()
operator|-
name|enterSafeModeTime
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Leaving safe mode after {} milliseconds"
argument_list|,
name|timeInSafemode
argument_list|)
expr_stmt|;
name|RouterMetrics
name|routerMetrics
init|=
name|router
operator|.
name|getRouterMetrics
argument_list|()
decl_stmt|;
if|if
condition|(
name|routerMetrics
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The Router metrics are not enabled"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|routerMetrics
operator|.
name|setSafeModeTime
argument_list|(
name|timeInSafemode
argument_list|)
expr_stmt|;
block|}
name|safeMode
operator|=
literal|false
expr_stmt|;
name|router
operator|.
name|updateRouterState
argument_list|(
name|RouterServiceState
operator|.
name|RUNNING
argument_list|)
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
comment|// Use same interval as cache update service
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
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_CACHE_TIME_TO_LIVE_MS_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|startupInterval
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_SAFEMODE_EXTENSION
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_SAFEMODE_EXTENSION_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Leave startup safe mode after {} ms"
argument_list|,
name|this
operator|.
name|startupInterval
argument_list|)
expr_stmt|;
name|this
operator|.
name|staleInterval
operator|=
name|conf
operator|.
name|getTimeDuration
argument_list|(
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_SAFEMODE_EXPIRATION
argument_list|,
name|RBFConfigKeys
operator|.
name|DFS_ROUTER_SAFEMODE_EXPIRATION_DEFAULT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Enter safe mode after {} ms without reaching the State Store"
argument_list|,
name|this
operator|.
name|staleInterval
argument_list|)
expr_stmt|;
name|this
operator|.
name|startupTime
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
comment|// Initializing the RPC server in safe mode, it will disable it later
name|enter
argument_list|()
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
specifier|public
name|void
name|periodicInvoke
parameter_list|()
block|{
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|delta
init|=
name|now
operator|-
name|startupTime
decl_stmt|;
if|if
condition|(
name|delta
operator|<
name|startupInterval
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Delaying safemode exit for {} milliseconds..."
argument_list|,
name|this
operator|.
name|startupInterval
operator|-
name|delta
argument_list|)
expr_stmt|;
return|return;
block|}
name|StateStoreService
name|stateStore
init|=
name|router
operator|.
name|getStateStore
argument_list|()
decl_stmt|;
name|long
name|cacheUpdateTime
init|=
name|stateStore
operator|.
name|getCacheUpdateTime
argument_list|()
decl_stmt|;
name|boolean
name|isCacheStale
init|=
operator|(
name|now
operator|-
name|cacheUpdateTime
operator|)
operator|>
name|this
operator|.
name|staleInterval
decl_stmt|;
comment|// Always update to indicate our cache was updated
if|if
condition|(
name|isCacheStale
condition|)
block|{
if|if
condition|(
operator|!
name|safeMode
condition|)
block|{
name|enter
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|safeMode
operator|&&
operator|!
name|isSafeModeSetManually
condition|)
block|{
comment|// Cache recently updated, leave safe mode
name|leave
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

