begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|source
operator|.
name|JvmMetrics
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
name|ReflectionUtils
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
name|ShutdownHookManager
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
name|StringUtils
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
name|YarnUncaughtExceptionHandler
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
name|YarnRuntimeException
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
name|store
operator|.
name|SCMStore
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
name|webapp
operator|.
name|SCMWebServer
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
comment|/**  * This service maintains the shared cache meta data. It handles claiming and  * releasing of resources, all rpc calls from the client to the shared cache  * manager, and administrative commands. It also persists the shared cache meta  * data to a backend store, and cleans up stale entries on a regular basis.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SharedCacheManager
specifier|public
class|class
name|SharedCacheManager
extends|extends
name|CompositeService
block|{
comment|/**    * Priority of the SharedCacheManager shutdown hook.    */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|public
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|30
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
name|SharedCacheManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|store
specifier|private
name|SCMStore
name|store
decl_stmt|;
DECL|method|SharedCacheManager ()
specifier|public
name|SharedCacheManager
parameter_list|()
block|{
name|super
argument_list|(
literal|"SharedCacheManager"
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
name|this
operator|.
name|store
operator|=
name|createSCMStoreService
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|CleanerService
name|cs
init|=
name|createCleanerService
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|cs
argument_list|)
expr_stmt|;
name|SharedCacheUploaderService
name|nms
init|=
name|createNMCacheUploaderSCMProtocolService
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|nms
argument_list|)
expr_stmt|;
name|ClientProtocolService
name|cps
init|=
name|createClientProtocolService
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|cps
argument_list|)
expr_stmt|;
name|SCMAdminProtocolService
name|saps
init|=
name|createSCMAdminProtocolService
argument_list|(
name|cs
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|saps
argument_list|)
expr_stmt|;
name|SCMWebServer
name|webUI
init|=
name|createSCMWebServer
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|addService
argument_list|(
name|webUI
argument_list|)
expr_stmt|;
comment|// init metrics
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"SharedCacheManager"
argument_list|)
expr_stmt|;
name|JvmMetrics
operator|.
name|initSingleton
argument_list|(
literal|"SharedCacheManager"
argument_list|,
literal|null
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
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createSCMStoreService (Configuration conf)
specifier|private
specifier|static
name|SCMStore
name|createSCMStoreService
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|SCMStore
argument_list|>
name|defaultStoreClass
decl_stmt|;
try|try
block|{
name|defaultStoreClass
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|SCMStore
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_STORE_CLASS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Invalid default scm store class"
operator|+
name|YarnConfiguration
operator|.
name|DEFAULT_SCM_STORE_CLASS
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|SCMStore
name|store
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getClass
argument_list|(
name|YarnConfiguration
operator|.
name|SCM_STORE_CLASS
argument_list|,
name|defaultStoreClass
argument_list|,
name|SCMStore
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|store
return|;
block|}
DECL|method|createCleanerService (SCMStore store)
specifier|private
name|CleanerService
name|createCleanerService
parameter_list|(
name|SCMStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|CleanerService
argument_list|(
name|store
argument_list|)
return|;
block|}
specifier|private
name|SharedCacheUploaderService
DECL|method|createNMCacheUploaderSCMProtocolService (SCMStore store)
name|createNMCacheUploaderSCMProtocolService
parameter_list|(
name|SCMStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|SharedCacheUploaderService
argument_list|(
name|store
argument_list|)
return|;
block|}
DECL|method|createClientProtocolService (SCMStore store)
specifier|private
name|ClientProtocolService
name|createClientProtocolService
parameter_list|(
name|SCMStore
name|store
parameter_list|)
block|{
return|return
operator|new
name|ClientProtocolService
argument_list|(
name|store
argument_list|)
return|;
block|}
DECL|method|createSCMAdminProtocolService ( CleanerService cleanerService)
specifier|private
name|SCMAdminProtocolService
name|createSCMAdminProtocolService
parameter_list|(
name|CleanerService
name|cleanerService
parameter_list|)
block|{
return|return
operator|new
name|SCMAdminProtocolService
argument_list|(
name|cleanerService
argument_list|)
return|;
block|}
DECL|method|createSCMWebServer (SharedCacheManager scm)
specifier|private
name|SCMWebServer
name|createSCMWebServer
parameter_list|(
name|SharedCacheManager
name|scm
parameter_list|)
block|{
return|return
operator|new
name|SCMWebServer
argument_list|(
name|scm
argument_list|)
return|;
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
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * For testing purposes only.    */
annotation|@
name|VisibleForTesting
DECL|method|getSCMStore ()
name|SCMStore
name|getSCMStore
parameter_list|()
block|{
return|return
name|this
operator|.
name|store
return|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|YarnUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|SharedCacheManager
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|SharedCacheManager
name|sharedCacheManager
init|=
operator|new
name|SharedCacheManager
argument_list|()
decl_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|CompositeServiceShutdownHook
argument_list|(
name|sharedCacheManager
argument_list|)
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
name|sharedCacheManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|sharedCacheManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error starting SharedCacheManager"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

