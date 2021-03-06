begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util.concurrent
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|concurrent
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
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
name|SynchronousQueue
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/** Factory methods for ExecutorService, ScheduledExecutorService instances.  * These executor service instances provide additional functionality (e.g  * logging uncaught exceptions). */
end_comment

begin_class
DECL|class|HadoopExecutors
specifier|public
specifier|final
class|class
name|HadoopExecutors
block|{
DECL|method|newCachedThreadPool (ThreadFactory threadFactory)
specifier|public
specifier|static
name|ExecutorService
name|newCachedThreadPool
parameter_list|(
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|HadoopThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|60L
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|threadFactory
argument_list|)
return|;
block|}
DECL|method|newFixedThreadPool (int nThreads, ThreadFactory threadFactory)
specifier|public
specifier|static
name|ExecutorService
name|newFixedThreadPool
parameter_list|(
name|int
name|nThreads
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|HadoopThreadPoolExecutor
argument_list|(
name|nThreads
argument_list|,
name|nThreads
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|threadFactory
argument_list|)
return|;
block|}
DECL|method|newFixedThreadPool (int nThreads)
specifier|public
specifier|static
name|ExecutorService
name|newFixedThreadPool
parameter_list|(
name|int
name|nThreads
parameter_list|)
block|{
return|return
operator|new
name|HadoopThreadPoolExecutor
argument_list|(
name|nThreads
argument_list|,
name|nThreads
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
comment|//Executors.newSingleThreadExecutor has special semantics - for the
comment|// moment we'll delegate to it rather than implement the semantics here.
DECL|method|newSingleThreadExecutor ()
specifier|public
specifier|static
name|ExecutorService
name|newSingleThreadExecutor
parameter_list|()
block|{
return|return
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
return|;
block|}
comment|//Executors.newSingleThreadExecutor has special semantics - for the
comment|// moment we'll delegate to it rather than implement the semantics here.
DECL|method|newSingleThreadExecutor (ThreadFactory threadFactory)
specifier|public
specifier|static
name|ExecutorService
name|newSingleThreadExecutor
parameter_list|(
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|(
name|threadFactory
argument_list|)
return|;
block|}
DECL|method|newScheduledThreadPool ( int corePoolSize)
specifier|public
specifier|static
name|ScheduledExecutorService
name|newScheduledThreadPool
parameter_list|(
name|int
name|corePoolSize
parameter_list|)
block|{
return|return
operator|new
name|HadoopScheduledThreadPoolExecutor
argument_list|(
name|corePoolSize
argument_list|)
return|;
block|}
DECL|method|newScheduledThreadPool ( int corePoolSize, ThreadFactory threadFactory)
specifier|public
specifier|static
name|ScheduledExecutorService
name|newScheduledThreadPool
parameter_list|(
name|int
name|corePoolSize
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|HadoopScheduledThreadPoolExecutor
argument_list|(
name|corePoolSize
argument_list|,
name|threadFactory
argument_list|)
return|;
block|}
comment|//Executors.newSingleThreadScheduledExecutor has special semantics - for the
comment|// moment we'll delegate to it rather than implement the semantics here
DECL|method|newSingleThreadScheduledExecutor ()
specifier|public
specifier|static
name|ScheduledExecutorService
name|newSingleThreadScheduledExecutor
parameter_list|()
block|{
return|return
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
return|;
block|}
comment|//Executors.newSingleThreadScheduledExecutor has special semantics - for the
comment|// moment we'll delegate to it rather than implement the semantics here
DECL|method|newSingleThreadScheduledExecutor ( ThreadFactory threadFactory)
specifier|public
specifier|static
name|ScheduledExecutorService
name|newSingleThreadScheduledExecutor
parameter_list|(
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|(
name|threadFactory
argument_list|)
return|;
block|}
comment|/**    * Helper routine to shutdown a {@link ExecutorService}. Will wait up to a    * certain timeout for the ExecutorService to gracefully shutdown. If the    * ExecutorService did not shutdown and there are still tasks unfinished after    * the timeout period, the ExecutorService will be notified to forcibly shut    * down. Another timeout period will be waited before giving up. So, at most,    * a shutdown will be allowed to wait up to twice the timeout value before    * giving up.    *    * @param executorService ExecutorService to shutdown    * @param logger Logger    * @param timeout the maximum time to wait    * @param unit the time unit of the timeout argument    */
DECL|method|shutdown (ExecutorService executorService, Logger logger, long timeout, TimeUnit unit)
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|(
name|ExecutorService
name|executorService
parameter_list|,
name|Logger
name|logger
parameter_list|,
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
if|if
condition|(
name|executorService
operator|==
literal|null
condition|)
block|{
return|return;
block|}
try|try
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Gracefully shutting down executor service. Waiting max {} {}"
argument_list|,
name|timeout
argument_list|,
name|unit
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|executorService
operator|.
name|awaitTermination
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Executor service has not shutdown yet. Forcing. "
operator|+
literal|"Will wait up to an additional {} {} for shutdown"
argument_list|,
name|timeout
argument_list|,
name|unit
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|executorService
operator|.
name|awaitTermination
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Succesfully shutdown executor service"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Unable to shutdown executor service after timeout {} {}"
argument_list|,
operator|(
literal|2
operator|*
name|timeout
operator|)
argument_list|,
name|unit
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
name|logger
operator|.
name|error
argument_list|(
literal|"Interrupted while attempting to shutdown"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Exception closing executor service {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Exception closing executor service"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|//disable instantiation
DECL|method|HadoopExecutors ()
specifier|private
name|HadoopExecutors
parameter_list|()
block|{ }
block|}
end_class

end_unit

