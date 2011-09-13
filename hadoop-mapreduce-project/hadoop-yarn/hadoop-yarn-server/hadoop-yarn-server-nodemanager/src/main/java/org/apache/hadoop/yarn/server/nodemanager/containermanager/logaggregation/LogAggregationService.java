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
name|net
operator|.
name|InetAddress
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
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|Executors
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
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
name|EventHandler
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
name|containermanager
operator|.
name|logaggregation
operator|.
name|event
operator|.
name|LogAggregatorEvent
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
name|yarn
operator|.
name|util
operator|.
name|ConverterUtils
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
name|EventHandler
argument_list|<
name|LogAggregatorEvent
argument_list|>
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
name|LogAggregationService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|deletionService
specifier|private
specifier|final
name|DeletionService
name|deletionService
decl_stmt|;
DECL|field|localRootLogDirs
specifier|private
name|String
index|[]
name|localRootLogDirs
decl_stmt|;
DECL|field|remoteRootLogDir
name|Path
name|remoteRootLogDir
decl_stmt|;
DECL|field|nodeFile
specifier|private
name|String
name|nodeFile
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
DECL|field|threadPool
specifier|private
specifier|final
name|ExecutorService
name|threadPool
decl_stmt|;
DECL|method|LogAggregationService (DeletionService deletionService)
specifier|public
name|LogAggregationService
parameter_list|(
name|DeletionService
name|deletionService
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
name|deletionService
operator|=
name|deletionService
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
name|this
operator|.
name|threadPool
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
expr_stmt|;
block|}
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|localRootLogDirs
operator|=
name|conf
operator|.
name|getStrings
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_DIRS
argument_list|)
expr_stmt|;
name|this
operator|.
name|remoteRootLogDir
operator|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_REMOTE_APP_LOG_DIR
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
name|String
name|address
init|=
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_ADDRESS
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|cmBindAddress
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|address
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|nodeFile
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|"_"
operator|+
name|cmBindAddress
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|getRemoteNodeLogFileForApp (ApplicationId appId)
name|Path
name|getRemoteNodeLogFileForApp
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
return|return
name|getRemoteNodeLogFileForApp
argument_list|(
name|this
operator|.
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|,
name|this
operator|.
name|nodeFile
argument_list|)
return|;
block|}
DECL|method|getRemoteNodeLogFileForApp (Path remoteRootLogDir, ApplicationId appId, String nodeFile)
specifier|static
name|Path
name|getRemoteNodeLogFileForApp
parameter_list|(
name|Path
name|remoteRootLogDir
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|nodeFile
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getRemoteAppLogDir
argument_list|(
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|)
argument_list|,
name|nodeFile
argument_list|)
return|;
block|}
DECL|method|getRemoteAppLogDir (Path remoteRootLogDir, ApplicationId appId)
specifier|static
name|Path
name|getRemoteAppLogDir
parameter_list|(
name|Path
name|remoteRootLogDir
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|remoteRootLogDir
argument_list|,
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|appId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
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
for|for
control|(
name|AppLogAggregator
name|appLogAggregator
range|:
name|this
operator|.
name|appLogAggregators
operator|.
name|values
argument_list|()
control|)
block|{
name|appLogAggregator
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|initApp (final ApplicationId appId, String user, Credentials credentials, ContainerLogsRetentionPolicy logRetentionPolicy)
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
name|ContainerLogsRetentionPolicy
name|logRetentionPolicy
parameter_list|)
block|{
comment|// Get user's FileSystem credentials
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
for|for
control|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
range|:
name|credentials
operator|.
name|getAllTokens
argument_list|()
control|)
block|{
name|userUgi
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
comment|// New application
name|AppLogAggregator
name|appLogAggregator
init|=
operator|new
name|AppLogAggregatorImpl
argument_list|(
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
name|localRootLogDirs
argument_list|,
name|getRemoteNodeLogFileForApp
argument_list|(
name|appId
argument_list|)
argument_list|,
name|logRetentionPolicy
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
name|YarnException
argument_list|(
literal|"Duplicate initApp for "
operator|+
name|appId
argument_list|)
throw|;
block|}
comment|// Create the app dir
try|try
block|{
name|userUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: Reuse FS for user?
name|FileSystem
name|remoteFS
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|remoteFS
operator|.
name|mkdirs
argument_list|(
name|getRemoteAppLogDir
argument_list|(
name|LogAggregationService
operator|.
name|this
operator|.
name|remoteRootLogDir
argument_list|,
name|appId
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|remoteFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|remoteFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
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
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// Get the user configuration for the list of containers that need log
comment|// aggregation.
comment|// Schedule the aggregator.
name|this
operator|.
name|threadPool
operator|.
name|execute
argument_list|(
name|appLogAggregator
argument_list|)
expr_stmt|;
block|}
DECL|method|stopContainer (ContainerId containerId, String exitCode)
specifier|private
name|void
name|stopContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|exitCode
parameter_list|)
block|{
comment|// A container is complete. Put this containers' logs up for aggregation if
comment|// this containers' logs are needed.
if|if
condition|(
operator|!
name|this
operator|.
name|appLogAggregators
operator|.
name|containsKey
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Application is not initialized yet for "
operator|+
name|containerId
argument_list|)
throw|;
block|}
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
operator|.
name|startContainerLogAggregation
argument_list|(
name|containerId
argument_list|,
name|exitCode
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|this
operator|.
name|appLogAggregators
operator|.
name|containsKey
argument_list|(
name|appId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Application is not initialized yet for "
operator|+
name|appId
argument_list|)
throw|;
block|}
name|this
operator|.
name|appLogAggregators
operator|.
name|get
argument_list|(
name|appId
argument_list|)
operator|.
name|finishLogAggregation
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (LogAggregatorEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|LogAggregatorEvent
name|event
parameter_list|)
block|{
comment|//    switch (event.getType()) {
comment|//    case APPLICATION_STARTED:
comment|//      LogAggregatorAppStartedEvent appStartEvent =
comment|//          (LogAggregatorAppStartedEvent) event;
comment|//      initApp(appStartEvent.getApplicationId(), appStartEvent.getUser(),
comment|//          appStartEvent.getCredentials(),
comment|//          appStartEvent.getLogRetentionPolicy());
comment|//      break;
comment|//    case CONTAINER_FINISHED:
comment|//      LogAggregatorContainerFinishedEvent containerFinishEvent =
comment|//          (LogAggregatorContainerFinishedEvent) event;
comment|//      stopContainer(containerFinishEvent.getContainerId(),
comment|//          containerFinishEvent.getExitCode());
comment|//      break;
comment|//    case APPLICATION_FINISHED:
comment|//      LogAggregatorAppFinishedEvent appFinishedEvent =
comment|//          (LogAggregatorAppFinishedEvent) event;
comment|//      stopApp(appFinishedEvent.getApplicationId());
comment|//      break;
comment|//    default:
comment|//      ; // Ignore
comment|//    }
block|}
block|}
end_class

end_unit

