begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
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
name|mapred
operator|.
name|JobConf
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
name|mapreduce
operator|.
name|MRConfig
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|HistoryServerStateStoreService
operator|.
name|HistoryServerState
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|server
operator|.
name|HSAdminServer
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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRWebAppUtil
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
name|security
operator|.
name|SecurityUtil
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
name|ExitUtil
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
name|GenericOptionsParser
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
name|JvmPauseMonitor
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
name|event
operator|.
name|Dispatcher
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
name|logaggregation
operator|.
name|AggregatedLogDeletionService
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

begin_comment
comment|/******************************************************************  * {@link JobHistoryServer} is responsible for servicing all job history  * related requests from client.  *  *****************************************************************/
end_comment

begin_class
DECL|class|JobHistoryServer
specifier|public
class|class
name|JobHistoryServer
extends|extends
name|CompositeService
block|{
comment|/**    * Priority of the JobHistoryServer shutdown hook.    */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|public
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|30
decl_stmt|;
DECL|field|historyServerTimeStamp
specifier|public
specifier|static
specifier|final
name|long
name|historyServerTimeStamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
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
name|JobHistoryServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|historyContext
specifier|protected
name|HistoryContext
name|historyContext
decl_stmt|;
DECL|field|clientService
specifier|private
name|HistoryClientService
name|clientService
decl_stmt|;
DECL|field|jobHistoryService
specifier|private
name|JobHistory
name|jobHistoryService
decl_stmt|;
DECL|field|jhsDTSecretManager
specifier|protected
name|JHSDelegationTokenSecretManager
name|jhsDTSecretManager
decl_stmt|;
DECL|field|aggLogDelService
specifier|private
name|AggregatedLogDeletionService
name|aggLogDelService
decl_stmt|;
DECL|field|hsAdminServer
specifier|private
name|HSAdminServer
name|hsAdminServer
decl_stmt|;
DECL|field|stateStore
specifier|private
name|HistoryServerStateStoreService
name|stateStore
decl_stmt|;
DECL|field|pauseMonitor
specifier|private
name|JvmPauseMonitor
name|pauseMonitor
decl_stmt|;
comment|// utility class to start and stop secret manager as part of service
comment|// framework and implement state recovery for secret manager on startup
DECL|class|HistoryServerSecretManagerService
specifier|private
class|class
name|HistoryServerSecretManagerService
extends|extends
name|AbstractService
block|{
DECL|method|HistoryServerSecretManagerService ()
specifier|public
name|HistoryServerSecretManagerService
parameter_list|()
block|{
name|super
argument_list|(
name|HistoryServerSecretManagerService
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|boolean
name|recoveryEnabled
init|=
name|getConfig
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HS_RECOVERY_ENABLE
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HS_RECOVERY_ENABLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|recoveryEnabled
condition|)
block|{
assert|assert
name|stateStore
operator|.
name|isInState
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|)
assert|;
name|HistoryServerState
name|state
init|=
name|stateStore
operator|.
name|loadState
argument_list|()
decl_stmt|;
name|jhsDTSecretManager
operator|.
name|recover
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|jhsDTSecretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while starting the Secret Manager threads"
argument_list|,
name|io
argument_list|)
expr_stmt|;
throw|throw
name|io
throw|;
block|}
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
if|if
condition|(
name|jhsDTSecretManager
operator|!=
literal|null
condition|)
block|{
name|jhsDTSecretManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|JobHistoryServer ()
specifier|public
name|JobHistoryServer
parameter_list|()
block|{
name|super
argument_list|(
name|JobHistoryServer
operator|.
name|class
operator|.
name|getName
argument_list|()
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
name|Configuration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|Dispatcher
operator|.
name|DISPATCHER_EXIT_ON_ERROR_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// This is required for WebApps to use https if enabled.
name|MRWebAppUtil
operator|.
name|initialize
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|doSecureLogin
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"History Server Failed to login"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
name|jobHistoryService
operator|=
operator|new
name|JobHistory
argument_list|()
expr_stmt|;
name|historyContext
operator|=
operator|(
name|HistoryContext
operator|)
name|jobHistoryService
expr_stmt|;
name|stateStore
operator|=
name|createStateStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|jhsDTSecretManager
operator|=
name|createJHSSecretManager
argument_list|(
name|conf
argument_list|,
name|stateStore
argument_list|)
expr_stmt|;
name|clientService
operator|=
name|createHistoryClientService
argument_list|()
expr_stmt|;
name|aggLogDelService
operator|=
operator|new
name|AggregatedLogDeletionService
argument_list|()
expr_stmt|;
name|hsAdminServer
operator|=
operator|new
name|HSAdminServer
argument_list|(
name|aggLogDelService
argument_list|,
name|jobHistoryService
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|stateStore
argument_list|)
expr_stmt|;
name|addService
argument_list|(
operator|new
name|HistoryServerSecretManagerService
argument_list|()
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|jobHistoryService
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|clientService
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|aggLogDelService
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|hsAdminServer
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"JobHistoryServer"
argument_list|)
expr_stmt|;
name|JvmMetrics
name|jm
init|=
name|JvmMetrics
operator|.
name|initSingleton
argument_list|(
literal|"JobHistoryServer"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|pauseMonitor
operator|=
operator|new
name|JvmPauseMonitor
argument_list|(
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|jm
operator|.
name|setPauseMonitor
argument_list|(
name|pauseMonitor
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|createHistoryClientService ()
specifier|protected
name|HistoryClientService
name|createHistoryClientService
parameter_list|()
block|{
return|return
operator|new
name|HistoryClientService
argument_list|(
name|historyContext
argument_list|,
name|this
operator|.
name|jhsDTSecretManager
argument_list|)
return|;
block|}
DECL|method|createJHSSecretManager ( Configuration conf, HistoryServerStateStoreService store)
specifier|protected
name|JHSDelegationTokenSecretManager
name|createJHSSecretManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|HistoryServerStateStoreService
name|store
parameter_list|)
block|{
name|long
name|secretKeyInterval
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|MRConfig
operator|.
name|DELEGATION_KEY_UPDATE_INTERVAL_KEY
argument_list|,
name|MRConfig
operator|.
name|DELEGATION_KEY_UPDATE_INTERVAL_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|tokenMaxLifetime
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|MRConfig
operator|.
name|DELEGATION_TOKEN_MAX_LIFETIME_KEY
argument_list|,
name|MRConfig
operator|.
name|DELEGATION_TOKEN_MAX_LIFETIME_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|tokenRenewInterval
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|MRConfig
operator|.
name|DELEGATION_TOKEN_RENEW_INTERVAL_KEY
argument_list|,
name|MRConfig
operator|.
name|DELEGATION_TOKEN_RENEW_INTERVAL_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|JHSDelegationTokenSecretManager
argument_list|(
name|secretKeyInterval
argument_list|,
name|tokenMaxLifetime
argument_list|,
name|tokenRenewInterval
argument_list|,
literal|3600000
argument_list|,
name|store
argument_list|)
return|;
block|}
DECL|method|createStateStore ( Configuration conf)
specifier|protected
name|HistoryServerStateStoreService
name|createStateStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|HistoryServerStateStoreServiceFactory
operator|.
name|getStore
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|doSecureLogin (Configuration conf)
specifier|protected
name|void
name|doSecureLogin
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|socAddr
init|=
name|getBindAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|JHAdminConfig
operator|.
name|MR_HISTORY_KEYTAB
argument_list|,
name|JHAdminConfig
operator|.
name|MR_HISTORY_PRINCIPAL
argument_list|,
name|socAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Retrieve JHS bind address from configuration    *    * @param conf    * @return InetSocketAddress    */
DECL|method|getBindAddress (Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getBindAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_ADDRESS
argument_list|,
name|JHAdminConfig
operator|.
name|DEFAULT_MR_HISTORY_PORT
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
name|pauseMonitor
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|pauseMonitor
operator|!=
literal|null
condition|)
block|{
name|pauseMonitor
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|getClientService ()
specifier|public
name|HistoryClientService
name|getClientService
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientService
return|;
block|}
DECL|method|launchJobHistoryServer (String[] args)
specifier|static
name|JobHistoryServer
name|launchJobHistoryServer
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
name|JobHistoryServer
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|JobHistoryServer
name|jobHistoryServer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|jobHistoryServer
operator|=
operator|new
name|JobHistoryServer
argument_list|()
expr_stmt|;
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
name|jobHistoryServer
argument_list|)
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|GenericOptionsParser
argument_list|(
name|conf
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|jobHistoryServer
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|jobHistoryServer
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
name|fatal
argument_list|(
literal|"Error starting JobHistoryServer"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
operator|-
literal|1
argument_list|,
literal|"Error starting JobHistoryServer"
argument_list|)
expr_stmt|;
block|}
return|return
name|jobHistoryServer
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
name|launchJobHistoryServer
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

