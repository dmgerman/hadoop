begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler
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
name|loghandler
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
name|ScheduledThreadPoolExecutor
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|RejectedExecutionException
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
name|fs
operator|.
name|UnsupportedFileSystemException
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
name|HadoopScheduledThreadPoolExecutor
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
name|proto
operator|.
name|YarnServerNodemanagerRecoveryProtos
operator|.
name|LogDeleterProto
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
name|deletion
operator|.
name|task
operator|.
name|FileDeletionTask
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
name|LogHandlerEvent
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
name|recovery
operator|.
name|NMStateStoreService
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
name|recovery
operator|.
name|NMStateStoreService
operator|.
name|RecoveredLogDeleterState
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
comment|/**  * Log Handler which schedules deletion of log files based on the configured log  * retention time.  */
end_comment

begin_class
DECL|class|NonAggregatingLogHandler
specifier|public
class|class
name|NonAggregatingLogHandler
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
name|NonAggregatingLogHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|delService
specifier|private
specifier|final
name|DeletionService
name|delService
decl_stmt|;
DECL|field|appOwners
specifier|private
specifier|final
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|String
argument_list|>
name|appOwners
decl_stmt|;
DECL|field|dirsHandler
specifier|private
specifier|final
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
DECL|field|stateStore
specifier|private
specifier|final
name|NMStateStoreService
name|stateStore
decl_stmt|;
DECL|field|deleteDelaySeconds
specifier|private
name|long
name|deleteDelaySeconds
decl_stmt|;
DECL|field|sched
specifier|private
name|ScheduledThreadPoolExecutor
name|sched
decl_stmt|;
DECL|method|NonAggregatingLogHandler (Dispatcher dispatcher, DeletionService delService, LocalDirsHandlerService dirsHandler, NMStateStoreService stateStore)
specifier|public
name|NonAggregatingLogHandler
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|,
name|DeletionService
name|delService
parameter_list|,
name|LocalDirsHandlerService
name|dirsHandler
parameter_list|,
name|NMStateStoreService
name|stateStore
parameter_list|)
block|{
name|super
argument_list|(
name|NonAggregatingLogHandler
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
name|delService
operator|=
name|delService
expr_stmt|;
name|this
operator|.
name|dirsHandler
operator|=
name|dirsHandler
expr_stmt|;
name|this
operator|.
name|stateStore
operator|=
name|stateStore
expr_stmt|;
name|this
operator|.
name|appOwners
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationId
argument_list|,
name|String
argument_list|>
argument_list|()
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
comment|// Default 3 hours.
name|this
operator|.
name|deleteDelaySeconds
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_RETAIN_SECONDS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_RETAIN_SECONDS
argument_list|)
expr_stmt|;
name|sched
operator|=
name|createScheduledThreadPoolExecutor
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|recover
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
name|sched
operator|!=
literal|null
condition|)
block|{
name|sched
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|boolean
name|isShutdown
init|=
literal|false
decl_stmt|;
try|try
block|{
name|isShutdown
operator|=
name|sched
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|sched
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|isShutdown
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isShutdown
condition|)
block|{
name|sched
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
name|super
operator|.
name|serviceStop
argument_list|()
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
DECL|method|recover ()
specifier|private
name|void
name|recover
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|stateStore
operator|.
name|canRecover
argument_list|()
condition|)
block|{
name|RecoveredLogDeleterState
name|state
init|=
name|stateStore
operator|.
name|loadLogDeleterState
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|LogDeleterProto
argument_list|>
name|entry
range|:
name|state
operator|.
name|getLogDeleterMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ApplicationId
name|appId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|LogDeleterProto
name|proto
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|long
name|deleteDelayMsec
init|=
name|proto
operator|.
name|getDeletionTime
argument_list|()
operator|-
name|now
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scheduling deletion of "
operator|+
name|appId
operator|+
literal|" logs in "
operator|+
name|deleteDelayMsec
operator|+
literal|" msec"
argument_list|)
expr_stmt|;
block|}
name|LogDeleterRunnable
name|logDeleter
init|=
operator|new
name|LogDeleterRunnable
argument_list|(
name|proto
operator|.
name|getUser
argument_list|()
argument_list|,
name|appId
argument_list|)
decl_stmt|;
try|try
block|{
name|sched
operator|.
name|schedule
argument_list|(
name|logDeleter
argument_list|,
name|deleteDelayMsec
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|e
parameter_list|)
block|{
comment|// Handling this event in local thread before starting threads
comment|// or after calling sched.shutdownNow().
name|logDeleter
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|appStartedEvent
init|=
operator|(
name|LogHandlerAppStartedEvent
operator|)
name|event
decl_stmt|;
name|this
operator|.
name|appOwners
operator|.
name|put
argument_list|(
name|appStartedEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|appStartedEvent
operator|.
name|getUser
argument_list|()
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
name|appStartedEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_INITED
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|CONTAINER_FINISHED
case|:
comment|// Ignore
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
name|ApplicationId
name|appId
init|=
name|appFinishedEvent
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
comment|// Schedule - so that logs are available on the UI till they're deleted.
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduling Log Deletion for application: "
operator|+
name|appId
operator|+
literal|", with delay of "
operator|+
name|this
operator|.
name|deleteDelaySeconds
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
name|String
name|user
init|=
name|appOwners
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to locate user for "
operator|+
name|appId
argument_list|)
expr_stmt|;
comment|// send LOG_HANDLING_FAILED out
name|NonAggregatingLogHandler
operator|.
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
break|break;
block|}
name|LogDeleterRunnable
name|logDeleter
init|=
operator|new
name|LogDeleterRunnable
argument_list|(
name|user
argument_list|,
name|appId
argument_list|)
decl_stmt|;
name|long
name|deletionTimestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|this
operator|.
name|deleteDelaySeconds
operator|*
literal|1000
decl_stmt|;
name|LogDeleterProto
name|deleterProto
init|=
name|LogDeleterProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
operator|.
name|setDeletionTime
argument_list|(
name|deletionTimestamp
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|stateStore
operator|.
name|storeLogDeleter
argument_list|(
name|appId
argument_list|,
name|deleterProto
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
literal|"Unable to record log deleter state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|sched
operator|.
name|schedule
argument_list|(
name|logDeleter
argument_list|,
name|this
operator|.
name|deleteDelaySeconds
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|e
parameter_list|)
block|{
comment|// Handling this event in local thread before starting threads
comment|// or after calling sched.shutdownNow().
name|logDeleter
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
break|break;
default|default:
empty_stmt|;
comment|// Ignore
block|}
block|}
DECL|method|createScheduledThreadPoolExecutor ( Configuration conf)
name|ScheduledThreadPoolExecutor
name|createScheduledThreadPoolExecutor
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"LogDeleter #%d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|sched
operator|=
operator|new
name|HadoopScheduledThreadPoolExecutor
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DELETION_THREADS_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_DELETE_THREAD_COUNT
argument_list|)
argument_list|,
name|tf
argument_list|)
expr_stmt|;
return|return
name|sched
return|;
block|}
DECL|class|LogDeleterRunnable
class|class
name|LogDeleterRunnable
implements|implements
name|Runnable
block|{
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|method|LogDeleterRunnable (String user, ApplicationId applicationId)
specifier|public
name|LogDeleterRunnable
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|applicationId
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|localAppLogDirs
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|FileContext
name|lfs
init|=
name|getLocalFileContext
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|rootLogDir
range|:
name|dirsHandler
operator|.
name|getLogDirsForCleanup
argument_list|()
control|)
block|{
name|Path
name|logDir
init|=
operator|new
name|Path
argument_list|(
name|rootLogDir
argument_list|,
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|lfs
operator|.
name|getFileStatus
argument_list|(
name|logDir
argument_list|)
expr_stmt|;
name|localAppLogDirs
operator|.
name|add
argument_list|(
name|logDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedFileSystemException
name|ue
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unsupported file system used for log dir "
operator|+
name|logDir
argument_list|,
name|ue
argument_list|)
expr_stmt|;
continue|continue;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
continue|continue;
block|}
block|}
comment|// Inform the application before the actual delete itself, so that links
comment|// to logs will no longer be there on NM web-UI.
name|NonAggregatingLogHandler
operator|.
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
name|this
operator|.
name|applicationId
argument_list|,
name|ApplicationEventType
operator|.
name|APPLICATION_LOG_HANDLING_FINISHED
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localAppLogDirs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|FileDeletionTask
name|deletionTask
init|=
operator|new
name|FileDeletionTask
argument_list|(
name|NonAggregatingLogHandler
operator|.
name|this
operator|.
name|delService
argument_list|,
name|user
argument_list|,
literal|null
argument_list|,
name|localAppLogDirs
argument_list|)
decl_stmt|;
name|NonAggregatingLogHandler
operator|.
name|this
operator|.
name|delService
operator|.
name|delete
argument_list|(
name|deletionTask
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|NonAggregatingLogHandler
operator|.
name|this
operator|.
name|stateStore
operator|.
name|removeLogDeleter
argument_list|(
name|this
operator|.
name|applicationId
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
literal|"Error removing log deletion state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"LogDeleter for AppId "
operator|+
name|this
operator|.
name|applicationId
operator|.
name|toString
argument_list|()
operator|+
literal|", owned by "
operator|+
name|user
return|;
block|}
block|}
block|}
end_class

end_unit

