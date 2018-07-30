begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.logaggregation
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|logaggregation
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
name|Map
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
name|ConcurrentMap
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
name|TimeUnit
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
name|fs
operator|.
name|FileContext
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
name|security
operator|.
name|Credentials
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
name|UserGroupInformation
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
name|ApplicationAccessType
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|LogAggregationContext
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
name|NodeId
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
name|filecontroller
operator|.
name|LogAggregationFileController
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
name|filecontroller
operator|.
name|LogAggregationFileControllerFactory
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
name|api
operator|.
name|ContainerLogContext
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
name|api
operator|.
name|ContainerType
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
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|DeletionService
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
name|nodemanager
operator|.
name|LocalDirsHandlerService
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|ApplicationEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|ApplicationEventType
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|loghandler
operator|.
name|LogHandler
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerAppFinishedEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerAppStartedEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerContainerFinishedEvent
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|loghandler
operator|.
name|event
operator|.
name|LogHandlerEvent
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

begin_class
DECL|class|LogAggregationService
specifier|public
class|class
name|LogAggregationService
extends|extends
name|AbstractService
implements|implements
name|LogHandler
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
name|LogAggregationService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MIN_LOG_ROLLING_INTERVAL
specifier|private
specifier|static
specifier|final
name|long
name|MIN_LOG_ROLLING_INTERVAL
init|=
literal|3600
decl_stmt|;
comment|// This configuration is for debug and test purpose. By setting
comment|// this configuration as true. We can break the lower bound of
comment|// NM_LOG_AGGREGATION_ROLL_MONITORING_INTERVAL_SECONDS.
DECL|field|NM_LOG_AGGREGATION_DEBUG_ENABLED
specifier|private
specifier|static
specifier|final
name|String
name|NM_LOG_AGGREGATION_DEBUG_ENABLED
init|=
name|YarnConfiguration
operator|.
name|NM_PREFIX
operator|+
literal|"log-aggregation.debug-enabled"
decl_stmt|;
DECL|field|rollingMonitorInterval
specifier|private
name|long
name|rollingMonitorInterval
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|deletionService
specifier|private
specifier|final
name|DeletionService
name|deletionService
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|dirsHandler
specifier|private
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|appLogAggregators
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregator
argument_list|>
name|appLogAggregators
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|threadPool
name|ExecutorService
name|threadPool
decl_stmt|;
DECL|method|LogAggregationService (Dispatcher dispatcher, Context context, DeletionService deletionService, LocalDirsHandlerService dirsHandler)
specifier|public
name|LogAggregationService
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|,
name|Context
name|context
parameter_list|,
name|DeletionService
name|deletionService
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|)
block|{
name|super
argument_list|(
name|LogAggregationService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|deletionService
operator|=
name|deletionService
expr_stmt|;
name|this
operator|.
name|dirsHandler
operator|=
name|dirsHandler
expr_stmt|;
name|this
operator|.
name|appLogAggregators
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregator
argument_list|>
argument_list|()
expr_stmt|;
block|}
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
name|int
name|threadPoolSize
init|=
name|getAggregatorThreadPoolSize
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|HadoopExecutors
operator|.
name|newFixedThreadPool
argument_list|(
name|threadPoolSize
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"LogAggregationService #%d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|rollingMonitorInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_AGGREGATION_ROLL_MONITORING_INTERVAL_SECONDS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_AGGREGATION_ROLL_MONITORING_INTERVAL_SECONDS
argument_list|)
expr_stmt|;
name|boolean
name|logAggregationDebugMode
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|NM_LOG_AGGREGATION_DEBUG_ENABLED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|rollingMonitorInterval
operator|>
literal|0
operator|&&
name|rollingMonitorInterval
operator|<
name|MIN_LOG_ROLLING_INTERVAL
condition|)
block|{
if|if
condition|(
name|logAggregationDebugMode
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Log aggregation debug mode enabled. rollingMonitorInterval = "
operator|+
name|rollingMonitorInterval
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"rollingMonitorInterval should be more than or equal to {} "
operator|+
literal|"seconds. Using {} seconds instead."
argument_list|,
name|MIN_LOG_ROLLING_INTERVAL
argument_list|,
name|MIN_LOG_ROLLING_INTERVAL
argument_list|)
expr_stmt|;
name|this
operator|.
name|rollingMonitorInterval
operator|=
name|MIN_LOG_ROLLING_INTERVAL
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|rollingMonitorInterval
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"rollingMonitorInterval is set as "
operator|+
name|rollingMonitorInterval
operator|+
literal|". The log rolling monitoring interval is disabled. "
operator|+
literal|"The logs will be aggregated after this application is finished."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"rollingMonitorInterval is set as "
operator|+
name|rollingMonitorInterval
operator|+
literal|". The logs will be aggregated every "
operator|+
name|rollingMonitorInterval
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
block|}
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
comment|// NodeId is only available during start, the following cannot be moved
comment|// anywhere else.
name|this
operator|.
name|nodeId
operator|=
name|this
operator|.
name|context
operator|.
name|getNodeId
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
name|LOG
operator|.
name|info
argument_list|(
name|this
operator|.
name|getName
argument_list|()
operator|+
literal|" waiting for pending aggregation during exit"
argument_list|)
expr_stmt|;
name|stopAggregators
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|stopAggregators ()
specifier|private
name|void
name|stopAggregators
parameter_list|()
block|{
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|boolean
name|supervised
init|=
name|getConfig
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_RECOVERY_SUPERVISED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_RECOVERY_SUPERVISED
argument_list|)
decl_stmt|;
comment|// if recovery on restart is supported then leave outstanding aggregations
comment|// to the next restart
name|boolean
name|shouldAbort
init|=
name|context
operator|.
name|getNMStateStore
argument_list|()
operator|.
name|canRecover
argument_list|()
operator|&&
operator|!
name|context
operator|.
name|getDecommissioned
argument_list|()
operator|&&
name|supervised
decl_stmt|;
comment|// politely ask to finish
for|for
control|(
name|AppLogAggregator
name|aggregator
range|:
name|appLogAggregators
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|shouldAbort
condition|)
block|{
name|aggregator
operator|.
name|abortLogAggregation
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|aggregator
operator|.
name|finishLogAggregation
argument_list|()
expr_stmt|;
block|}
block|}
while|while
condition|(
operator|!
name|threadPool
operator|.
name|isTerminated
argument_list|()
condition|)
block|{
comment|// wait for all threads to finish
for|for
control|(
name|ApplicationId
name|appId
range|:
name|appLogAggregators
operator|.
name|keySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for aggregation to complete for "
operator|+
name|appId
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
operator|!
name|threadPool
operator|.
name|awaitTermination
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|threadPool
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
comment|// send interrupt to hurry them along
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
literal|"Aggregation stop interrupted!"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
for|for
control|(
name|ApplicationId
name|appId
range|:
name|appLogAggregators
operator|.
name|keySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Some logs may not have been aggregated for "
operator|+
name|appId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|initApp (final ApplicationId appId, String user, Credentials credentials, Map<ApplicationAccessType, String> appAcls, LogAggregationContext logAggregationContext, long recoveredLogInitedTime)
specifier|private
name|void
name|initApp
parameter_list|(
specifier|final
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|appAcls
parameter_list|,
name|LogAggregationContext
name|logAggregationContext
parameter_list|,
name|long
name|recoveredLogInitedTime
parameter_list|)
block|{
name|ApplicationEvent
name|eventResponse
decl_stmt|;
try|try
block|{
name|initAppAggregator
argument_list|(
name|appId
argument_list|,
name|user
argument_list|,
name|credentials
argument_list|,
name|appAcls
argument_list|,
name|logAggregationContext
argument_list|,
name|recoveredLogInitedTime
argument_list|)
expr_stmt|;
name|eventResponse
operator|=
operator|new
name|ApplicationEvent
argument_list|(
name|appId
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_INITED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Application failed to init aggregation"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|eventResponse
operator|=
operator|new
name|ApplicationEvent
argument_list|(
name|appId
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_FAILED
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|eventResponse
argument_list|)
expr_stmt|;
block|}
DECL|method|getLocalFileContext (Configuration conf)
name|FileContext
name|getLocalFileContext
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
return|return
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|(
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Failed to access local fs"
argument_list|)
throw|;
block|}
block|}
DECL|method|initAppAggregator (final ApplicationId appId, String user, Credentials credentials, Map<ApplicationAccessType, String> appAcls, LogAggregationContext logAggregationContext, long recoveredLogInitedTime)
specifier|protected
name|void
name|initAppAggregator
parameter_list|(
specifier|final
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|appAcls
parameter_list|,
name|LogAggregationContext
name|logAggregationContext
parameter_list|,
name|long
name|recoveredLogInitedTime
parameter_list|)
block|{
comment|// Get user's FileSystem credentials
specifier|final
name|UserGroupInformation
name|userUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|credentials
operator|!=
literal|null
condition|)
block|{
name|userUgi
operator|.
name|addCredentials
argument_list|(
name|credentials
argument_list|)
expr_stmt|;
block|}
name|LogAggregationFileController
name|logAggregationFileController
init|=
name|getLogAggregationFileController
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|logAggregationFileController
operator|.
name|verifyAndCreateRemoteLogDir
argument_list|()
expr_stmt|;
comment|// New application
specifier|final
name|AppLogAggregator
name|appLogAggregator
init|=
operator|new
name|AppLogAggregatorImpl
argument_list|(
name|this
operator|.
name|dispatcher
argument_list|,
name|this
operator|.
name|deletionService
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|appId
argument_list|,
name|userUgi
argument_list|,
name|this
operator|.
name|nodeId
argument_list|,
name|dirsHandler
argument_list|,
name|logAggregationFileController
operator|.
name|getRemoteNodeLogFileForApp
argument_list|(
name|appId
argument_list|,
name|user
argument_list|,
name|nodeId
argument_list|)
argument_list|,
name|appAcls
argument_list|,
name|logAggregationContext
argument_list|,
name|this
operator|.
name|context
argument_list|,
name|getLocalFileContext
argument_list|(
name|getConfig
argument_list|()
argument_list|)
argument_list|,
name|this
operator|.
name|rollingMonitorInterval
argument_list|,
name|recoveredLogInitedTime
argument_list|,
name|logAggregationFileController
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|appLogAggregators
operator|.
name|putIfAbsent
argument_list|(
name|appId
argument_list|,
name|appLogAggregator
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Duplicate initApp for "
operator|+
name|appId
argument_list|)
throw|;
block|}
comment|// wait until check for existing aggregator to create dirs
name|YarnRuntimeException
name|appDirException
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Create the app dir
name|logAggregationFileController
operator|.
name|createAppDir
argument_list|(
name|user
argument_list|,
name|appId
argument_list|,
name|userUgi
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|appLogAggregator
operator|.
name|disableLogAggregation
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|e
operator|instanceof
name|YarnRuntimeException
operator|)
condition|)
block|{
name|appDirException
operator|=
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|appDirException
operator|=
operator|(
name|YarnRuntimeException
operator|)
name|e
expr_stmt|;
block|}
name|appLogAggregators
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|closeFileSystems
argument_list|(
name|userUgi
argument_list|)
expr_stmt|;
throw|throw
name|appDirException
throw|;
block|}
comment|// TODO Get the user configuration for the list of containers that need log
comment|// aggregation.
comment|// Schedule the aggregator.
name|Runnable
name|aggregatorWrapper
init|=
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|appLogAggregator
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|appLogAggregators
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|closeFileSystems
argument_list|(
name|userUgi
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|this
operator|.
name|threadPool
operator|.
name|execute
argument_list|(
name|aggregatorWrapper
argument_list|)
expr_stmt|;
block|}
DECL|method|closeFileSystems (final UserGroupInformation userUgi)
specifier|protected
name|void
name|closeFileSystems
parameter_list|(
specifier|final
name|UserGroupInformation
name|userUgi
parameter_list|)
block|{
try|try
block|{
name|FileSystem
operator|.
name|closeAllForUGI
argument_list|(
name|userUgi
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
literal|"Failed to close filesystems: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// for testing only
annotation|@
name|Private
DECL|method|getNumAggregators ()
name|int
name|getNumAggregators
parameter_list|()
block|{
return|return
name|this
operator|.
name|appLogAggregators
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|stopContainer (ContainerId containerId, ContainerType containerType, int exitCode)
specifier|private
name|void
name|stopContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|ContainerType
name|containerType
parameter_list|,
name|int
name|exitCode
parameter_list|)
block|{
comment|// A container is complete. Put this containers' logs up for aggregation if
comment|// this containers' logs are needed.
name|AppLogAggregator
name|aggregator
init|=
name|this
operator|.
name|appLogAggregators
operator|.
name|get
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log aggregation is not initialized for "
operator|+
name|containerId
operator|+
literal|", did it fail to start?"
argument_list|)
expr_stmt|;
return|return;
block|}
name|aggregator
operator|.
name|startContainerLogAggregation
argument_list|(
operator|new
name|ContainerLogContext
argument_list|(
name|containerId
argument_list|,
name|containerType
argument_list|,
name|exitCode
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|stopApp (ApplicationId appId)
specifier|private
name|void
name|stopApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
comment|// App is complete. Finish up any containers' pending log aggregation and
comment|// close the application specific logFile.
name|AppLogAggregator
name|aggregator
init|=
name|this
operator|.
name|appLogAggregators
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregator
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Log aggregation is not initialized for "
operator|+
name|appId
operator|+
literal|", did it fail to start?"
argument_list|)
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|ApplicationEvent
argument_list|(
name|appId
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_FAILED
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|aggregator
operator|.
name|finishLogAggregation
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (LogHandlerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|LogHandlerEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|APPLICATION_STARTED
case|:
name|LogHandlerAppStartedEvent
name|appStartEvent
init|=
operator|(
name|LogHandlerAppStartedEvent
operator|)
name|event
decl_stmt|;
name|initApp
argument_list|(
name|appStartEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getUser
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getCredentials
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getApplicationAcls
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getLogAggregationContext
argument_list|()
argument_list|,
name|appStartEvent
operator|.
name|getRecoveredAppLogInitedTime
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|CONTAINER_FINISHED
case|:
name|LogHandlerContainerFinishedEvent
name|containerFinishEvent
init|=
operator|(
name|LogHandlerContainerFinishedEvent
operator|)
name|event
decl_stmt|;
name|stopContainer
argument_list|(
name|containerFinishEvent
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|containerFinishEvent
operator|.
name|getContainerType
argument_list|()
argument_list|,
name|containerFinishEvent
operator|.
name|getExitCode
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|APPLICATION_FINISHED
case|:
name|LogHandlerAppFinishedEvent
name|appFinishedEvent
init|=
operator|(
name|LogHandlerAppFinishedEvent
operator|)
name|event
decl_stmt|;
name|stopApp
argument_list|(
name|appFinishedEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
empty_stmt|;
comment|// Ignore
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAppLogAggregators ()
specifier|public
name|ConcurrentMap
argument_list|<
name|ApplicationId
argument_list|,
name|AppLogAggregator
argument_list|>
name|getAppLogAggregators
parameter_list|()
block|{
return|return
name|this
operator|.
name|appLogAggregators
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
DECL|method|getAggregatorThreadPoolSize (Configuration conf)
specifier|private
name|int
name|getAggregatorThreadPoolSize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|int
name|threadPoolSize
decl_stmt|;
try|try
block|{
name|threadPoolSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_AGGREGATION_THREAD_POOL_SIZE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_AGGREGATION_THREAD_POOL_SIZE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid thread pool size. Setting it to the default value "
operator|+
literal|"in YarnConfiguration"
argument_list|)
expr_stmt|;
name|threadPoolSize
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_AGGREGATION_THREAD_POOL_SIZE
expr_stmt|;
block|}
if|if
condition|(
name|threadPoolSize
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid thread pool size. Setting it to the default value "
operator|+
literal|"in YarnConfiguration"
argument_list|)
expr_stmt|;
name|threadPoolSize
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_AGGREGATION_THREAD_POOL_SIZE
expr_stmt|;
block|}
return|return
name|threadPoolSize
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getLogAggregationFileController ( Configuration conf)
specifier|public
name|LogAggregationFileController
name|getLogAggregationFileController
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|LogAggregationFileControllerFactory
name|factory
init|=
operator|new
name|LogAggregationFileControllerFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LogAggregationFileController
name|logAggregationFileController
init|=
name|factory
operator|.
name|getFileControllerForWrite
argument_list|()
decl_stmt|;
return|return
name|logAggregationFileController
return|;
block|}
block|}
end_class

end_unit

