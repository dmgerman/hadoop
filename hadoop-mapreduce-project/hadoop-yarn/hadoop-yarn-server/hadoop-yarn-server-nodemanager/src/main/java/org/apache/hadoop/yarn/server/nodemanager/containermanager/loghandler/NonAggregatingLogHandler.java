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
name|service
operator|.
name|AbstractService
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
DECL|method|NonAggregatingLogHandler (Dispatcher dispatcher, DeletionService delService, LocalDirsHandlerService dirsHandler)
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
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
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
literal|3
operator|*
literal|60
operator|*
literal|60
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
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
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
name|super
operator|.
name|stop
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
comment|// Schedule - so that logs are available on the UI till they're deleted.
name|LOG
operator|.
name|info
argument_list|(
literal|"Scheduling Log Deletion for application: "
operator|+
name|appFinishedEvent
operator|.
name|getApplicationId
argument_list|()
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
name|sched
operator|.
name|schedule
argument_list|(
operator|new
name|LogDeleterRunnable
argument_list|(
name|appOwners
operator|.
name|remove
argument_list|(
name|appFinishedEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|,
name|appFinishedEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|)
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
name|ScheduledThreadPoolExecutor
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
name|String
argument_list|>
name|rootLogDirs
init|=
name|NonAggregatingLogHandler
operator|.
name|this
operator|.
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
decl_stmt|;
name|Path
index|[]
name|localAppLogDirs
init|=
operator|new
name|Path
index|[
name|rootLogDirs
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|rootLogDir
range|:
name|rootLogDirs
control|)
block|{
name|localAppLogDirs
index|[
name|index
index|]
operator|=
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
expr_stmt|;
name|index
operator|++
expr_stmt|;
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
name|NonAggregatingLogHandler
operator|.
name|this
operator|.
name|delService
operator|.
name|delete
argument_list|(
name|user
argument_list|,
literal|null
argument_list|,
name|localAppLogDirs
argument_list|)
expr_stmt|;
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

