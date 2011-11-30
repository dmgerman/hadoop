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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|ApplicationAttemptId
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
name|event
operator|.
name|DrainDispatcher
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
name|logaggregation
operator|.
name|ContainerLogsRetentionPolicy
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
name|util
operator|.
name|BuilderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|exceptions
operator|.
name|verification
operator|.
name|WantedButNotInvoked
import|;
end_import

begin_class
DECL|class|TestNonAggregatingLogHandler
specifier|public
class|class
name|TestNonAggregatingLogHandler
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testLogDeletion ()
specifier|public
name|void
name|testLogDeletion
parameter_list|()
block|{
name|DeletionService
name|delService
init|=
name|mock
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|user
init|=
literal|"testuser"
decl_stmt|;
name|File
index|[]
name|localLogDirs
init|=
operator|new
name|File
index|[
literal|2
index|]
decl_stmt|;
name|localLogDirs
index|[
literal|0
index|]
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-localLogDir0"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|localLogDirs
index|[
literal|1
index|]
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-localLogDir1"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|String
name|localLogDirsString
init|=
name|localLogDirs
index|[
literal|0
index|]
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|","
operator|+
name|localLogDirs
index|[
literal|1
index|]
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|localLogDirsString
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_AGGREGATION_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_RETAIN_SECONDS
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
name|DrainDispatcher
name|dispatcher
init|=
name|createDispatcher
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|EventHandler
argument_list|<
name|ApplicationEvent
argument_list|>
name|appEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ApplicationEventType
operator|.
name|class
argument_list|,
name|appEventHandler
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirsHandler
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
name|dirsHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId1
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|1234
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId1
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|container11
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|NonAggregatingLogHandler
name|logHandler
init|=
operator|new
name|NonAggregatingLogHandler
argument_list|(
name|dispatcher
argument_list|,
name|delService
argument_list|,
name|dirsHandler
argument_list|)
decl_stmt|;
name|logHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|logHandler
operator|.
name|start
argument_list|()
expr_stmt|;
name|logHandler
operator|.
name|handle
argument_list|(
operator|new
name|LogHandlerAppStartedEvent
argument_list|(
name|appId1
argument_list|,
name|user
argument_list|,
literal|null
argument_list|,
name|ContainerLogsRetentionPolicy
operator|.
name|ALL_CONTAINERS
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|logHandler
operator|.
name|handle
argument_list|(
operator|new
name|LogHandlerContainerFinishedEvent
argument_list|(
name|container11
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|logHandler
operator|.
name|handle
argument_list|(
operator|new
name|LogHandlerAppFinishedEvent
argument_list|(
name|appId1
argument_list|)
argument_list|)
expr_stmt|;
name|Path
index|[]
name|localAppLogDirs
init|=
operator|new
name|Path
index|[
literal|2
index|]
decl_stmt|;
name|localAppLogDirs
index|[
literal|0
index|]
operator|=
operator|new
name|Path
argument_list|(
name|localLogDirs
index|[
literal|0
index|]
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|appId1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|localAppLogDirs
index|[
literal|1
index|]
operator|=
operator|new
name|Path
argument_list|(
name|localLogDirs
index|[
literal|1
index|]
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|appId1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// 5 seconds for the delete which is a separate thread.
name|long
name|verifyStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|WantedButNotInvoked
name|notInvokedException
init|=
literal|null
decl_stmt|;
name|boolean
name|matched
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|matched
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|verifyStartTime
operator|+
literal|5000l
condition|)
block|{
try|try
block|{
name|verify
argument_list|(
name|delService
argument_list|)
operator|.
name|delete
argument_list|(
name|eq
argument_list|(
name|user
argument_list|)
argument_list|,
operator|(
name|Path
operator|)
name|eq
argument_list|(
literal|null
argument_list|)
argument_list|,
name|eq
argument_list|(
name|localAppLogDirs
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|eq
argument_list|(
name|localAppLogDirs
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|matched
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WantedButNotInvoked
name|e
parameter_list|)
block|{
name|notInvokedException
operator|=
name|e
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50l
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|i
parameter_list|)
block|{         }
block|}
block|}
if|if
condition|(
operator|!
name|matched
condition|)
block|{
throw|throw
name|notInvokedException
throw|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testDelayedDelete ()
specifier|public
name|void
name|testDelayedDelete
parameter_list|()
block|{
name|DeletionService
name|delService
init|=
name|mock
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|user
init|=
literal|"testuser"
decl_stmt|;
name|File
index|[]
name|localLogDirs
init|=
operator|new
name|File
index|[
literal|2
index|]
decl_stmt|;
name|localLogDirs
index|[
literal|0
index|]
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-localLogDir0"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|localLogDirs
index|[
literal|1
index|]
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-localLogDir1"
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|String
name|localLogDirsString
init|=
name|localLogDirs
index|[
literal|0
index|]
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|","
operator|+
name|localLogDirs
index|[
literal|1
index|]
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|localLogDirsString
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_AGGREGATION_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_RETAIN_SECONDS
argument_list|,
literal|10800l
argument_list|)
expr_stmt|;
name|DrainDispatcher
name|dispatcher
init|=
name|createDispatcher
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|EventHandler
argument_list|<
name|ApplicationEvent
argument_list|>
name|appEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ApplicationEventType
operator|.
name|class
argument_list|,
name|appEventHandler
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirsHandler
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
name|dirsHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId1
init|=
name|BuilderUtils
operator|.
name|newApplicationId
argument_list|(
literal|1234
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId1
init|=
name|BuilderUtils
operator|.
name|newApplicationAttemptId
argument_list|(
name|appId1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|container11
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|appAttemptId1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|NonAggregatingLogHandler
name|logHandler
init|=
operator|new
name|NonAggregatingLogHandlerWithMockExecutor
argument_list|(
name|dispatcher
argument_list|,
name|delService
argument_list|,
name|dirsHandler
argument_list|)
decl_stmt|;
name|logHandler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|logHandler
operator|.
name|start
argument_list|()
expr_stmt|;
name|logHandler
operator|.
name|handle
argument_list|(
operator|new
name|LogHandlerAppStartedEvent
argument_list|(
name|appId1
argument_list|,
name|user
argument_list|,
literal|null
argument_list|,
name|ContainerLogsRetentionPolicy
operator|.
name|ALL_CONTAINERS
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|logHandler
operator|.
name|handle
argument_list|(
operator|new
name|LogHandlerContainerFinishedEvent
argument_list|(
name|container11
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|logHandler
operator|.
name|handle
argument_list|(
operator|new
name|LogHandlerAppFinishedEvent
argument_list|(
name|appId1
argument_list|)
argument_list|)
expr_stmt|;
name|Path
index|[]
name|localAppLogDirs
init|=
operator|new
name|Path
index|[
literal|2
index|]
decl_stmt|;
name|localAppLogDirs
index|[
literal|0
index|]
operator|=
operator|new
name|Path
argument_list|(
name|localLogDirs
index|[
literal|0
index|]
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|appId1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|localAppLogDirs
index|[
literal|1
index|]
operator|=
operator|new
name|Path
argument_list|(
name|localLogDirs
index|[
literal|1
index|]
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|appId1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ScheduledThreadPoolExecutor
name|mockSched
init|=
operator|(
operator|(
name|NonAggregatingLogHandlerWithMockExecutor
operator|)
name|logHandler
operator|)
operator|.
name|mockSched
decl_stmt|;
name|verify
argument_list|(
name|mockSched
argument_list|)
operator|.
name|schedule
argument_list|(
name|any
argument_list|(
name|Runnable
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|10800l
argument_list|)
argument_list|,
name|eq
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|NonAggregatingLogHandlerWithMockExecutor
specifier|private
class|class
name|NonAggregatingLogHandlerWithMockExecutor
extends|extends
name|NonAggregatingLogHandler
block|{
DECL|field|mockSched
specifier|private
name|ScheduledThreadPoolExecutor
name|mockSched
decl_stmt|;
DECL|method|NonAggregatingLogHandlerWithMockExecutor (Dispatcher dispatcher, DeletionService delService, LocalDirsHandlerService dirsHandler)
specifier|public
name|NonAggregatingLogHandlerWithMockExecutor
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
name|dispatcher
argument_list|,
name|delService
argument_list|,
name|dirsHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createScheduledThreadPoolExecutor ( Configuration conf)
name|ScheduledThreadPoolExecutor
name|createScheduledThreadPoolExecutor
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|mockSched
operator|=
name|mock
argument_list|(
name|ScheduledThreadPoolExecutor
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|mockSched
return|;
block|}
block|}
DECL|method|createDispatcher (Configuration conf)
specifier|private
name|DrainDispatcher
name|createDispatcher
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|DrainDispatcher
name|dispatcher
init|=
operator|new
name|DrainDispatcher
argument_list|()
decl_stmt|;
name|dispatcher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|dispatcher
return|;
block|}
block|}
end_class

end_unit

