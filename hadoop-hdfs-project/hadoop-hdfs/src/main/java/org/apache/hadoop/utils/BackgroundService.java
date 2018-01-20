begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
package|;
end_package

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
name|collect
operator|.
name|Lists
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
name|TimeoutException
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
name|CompletionService
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
name|ExecutorCompletionService
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
name|Future
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

begin_comment
comment|/**  * An abstract class for a background service in ozone.  * A background service schedules multiple child tasks in parallel  * in a certain period. In each interval, it waits until all the tasks  * finish execution and then schedule next interval.  */
end_comment

begin_class
DECL|class|BackgroundService
specifier|public
specifier|abstract
class|class
name|BackgroundService
block|{
annotation|@
name|VisibleForTesting
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BackgroundService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Executor to launch child tasks
DECL|field|exec
specifier|private
specifier|final
name|ScheduledExecutorService
name|exec
decl_stmt|;
DECL|field|threadGroup
specifier|private
specifier|final
name|ThreadGroup
name|threadGroup
decl_stmt|;
DECL|field|threadFactory
specifier|private
specifier|final
name|ThreadFactory
name|threadFactory
decl_stmt|;
DECL|field|serviceName
specifier|private
specifier|final
name|String
name|serviceName
decl_stmt|;
DECL|field|interval
specifier|private
specifier|final
name|long
name|interval
decl_stmt|;
DECL|field|serviceTimeout
specifier|private
specifier|final
name|long
name|serviceTimeout
decl_stmt|;
DECL|field|unit
specifier|private
specifier|final
name|TimeUnit
name|unit
decl_stmt|;
DECL|field|service
specifier|private
specifier|final
name|PeriodicalTask
name|service
decl_stmt|;
DECL|method|BackgroundService (String serviceName, long interval, TimeUnit unit, int threadPoolSize, long serviceTimeout)
specifier|public
name|BackgroundService
parameter_list|(
name|String
name|serviceName
parameter_list|,
name|long
name|interval
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|int
name|threadPoolSize
parameter_list|,
name|long
name|serviceTimeout
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|serviceName
operator|=
name|serviceName
expr_stmt|;
name|this
operator|.
name|serviceTimeout
operator|=
name|serviceTimeout
expr_stmt|;
name|threadGroup
operator|=
operator|new
name|ThreadGroup
argument_list|(
name|serviceName
argument_list|)
expr_stmt|;
name|ThreadFactory
name|tf
init|=
name|r
lambda|->
operator|new
name|Thread
argument_list|(
name|threadGroup
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|threadFactory
operator|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setThreadFactory
argument_list|(
name|tf
argument_list|)
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
name|serviceName
operator|+
literal|"#%d"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|exec
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
name|threadPoolSize
argument_list|,
name|threadFactory
argument_list|)
expr_stmt|;
name|service
operator|=
operator|new
name|PeriodicalTask
argument_list|()
expr_stmt|;
block|}
DECL|method|getExecutorService ()
specifier|protected
name|ExecutorService
name|getExecutorService
parameter_list|()
block|{
return|return
name|this
operator|.
name|exec
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getThreadCount ()
specifier|public
name|int
name|getThreadCount
parameter_list|()
block|{
return|return
name|threadGroup
operator|.
name|activeCount
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|triggerBackgroundTaskForTesting ()
specifier|public
name|void
name|triggerBackgroundTaskForTesting
parameter_list|()
block|{
name|service
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|// start service
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|exec
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|service
argument_list|,
literal|0
argument_list|,
name|interval
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
DECL|method|getTasks ()
specifier|public
specifier|abstract
name|BackgroundTaskQueue
name|getTasks
parameter_list|()
function_decl|;
comment|/**    * Run one or more background tasks concurrently.    * Wait until all tasks to return the result.    */
DECL|class|PeriodicalTask
specifier|public
class|class
name|PeriodicalTask
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Running background service : {}"
argument_list|,
name|serviceName
argument_list|)
expr_stmt|;
name|BackgroundTaskQueue
name|tasks
init|=
name|getTasks
argument_list|()
decl_stmt|;
if|if
condition|(
name|tasks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// No task found, or some problems to init tasks
comment|// return and retry in next interval.
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Number of background tasks to execute : {}"
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|CompletionService
argument_list|<
name|BackgroundTaskResult
argument_list|>
name|taskCompletionService
init|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|exec
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|BackgroundTaskResult
argument_list|>
argument_list|>
name|results
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|tasks
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|BackgroundTask
name|task
init|=
name|tasks
operator|.
name|poll
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|BackgroundTaskResult
argument_list|>
name|result
init|=
name|taskCompletionService
operator|.
name|submit
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
name|results
operator|.
name|parallelStream
argument_list|()
operator|.
name|forEach
argument_list|(
name|taskResultFuture
lambda|->
block|{
try|try
block|{
comment|// Collect task results
name|BackgroundTaskResult
name|result
init|=
name|serviceTimeout
operator|>
literal|0
condition|?
name|taskResultFuture
operator|.
name|get
argument_list|(
name|serviceTimeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
else|:
name|taskResultFuture
operator|.
name|get
argument_list|()
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
literal|"task execution result size {}"
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Background task fails to execute, "
operator|+
literal|"retrying in next interval"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Background task executes timed out, "
operator|+
literal|"retrying in next interval"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|// shutdown and make sure all threads are properly released.
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down service {}"
argument_list|,
name|this
operator|.
name|serviceName
argument_list|)
expr_stmt|;
name|exec
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|exec
operator|.
name|awaitTermination
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
name|exec
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|exec
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|threadGroup
operator|.
name|activeCount
argument_list|()
operator|==
literal|0
operator|&&
operator|!
name|threadGroup
operator|.
name|isDestroyed
argument_list|()
condition|)
block|{
name|threadGroup
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

