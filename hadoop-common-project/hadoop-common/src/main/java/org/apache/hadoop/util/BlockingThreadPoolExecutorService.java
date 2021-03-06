begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|BlockingQueue
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
name|RejectedExecutionHandler
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
name|ThreadPoolExecutor
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
name|atomic
operator|.
name|AtomicInteger
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
name|MoreExecutors
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
import|;
end_import

begin_comment
comment|/**  * This ExecutorService blocks the submission of new tasks when its queue is  * already full by using a semaphore. Task submissions require permits, task  * completions release permits.  *<p>  * This is inspired by<a href="https://github.com/apache/incubator-s4/blob/master/subprojects/s4-comm/src/main/java/org/apache/s4/comm/staging/BlockingThreadPoolExecutorService.java">  * this s4 threadpool</a>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockingThreadPoolExecutorService
specifier|public
specifier|final
class|class
name|BlockingThreadPoolExecutorService
extends|extends
name|SemaphoredDelegatingExecutor
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
name|BlockingThreadPoolExecutorService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|POOLNUMBER
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|POOLNUMBER
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|eventProcessingExecutor
specifier|private
specifier|final
name|ThreadPoolExecutor
name|eventProcessingExecutor
decl_stmt|;
comment|/**    * Returns a {@link java.util.concurrent.ThreadFactory} that names each    * created thread uniquely,    * with a common prefix.    *    * @param prefix The prefix of every created Thread's name    * @return a {@link java.util.concurrent.ThreadFactory} that names threads    */
DECL|method|getNamedThreadFactory (final String prefix)
specifier|static
name|ThreadFactory
name|getNamedThreadFactory
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|SecurityManager
name|s
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
specifier|final
name|ThreadGroup
name|threadGroup
init|=
operator|(
name|s
operator|!=
literal|null
operator|)
condition|?
name|s
operator|.
name|getThreadGroup
argument_list|()
else|:
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
decl_stmt|;
return|return
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
specifier|final
name|AtomicInteger
name|threadNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|poolNum
init|=
name|POOLNUMBER
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ThreadGroup
name|group
init|=
name|threadGroup
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|prefix
operator|+
literal|"-pool"
operator|+
name|poolNum
operator|+
literal|"-t"
operator|+
name|threadNumber
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
return|return
operator|new
name|Thread
argument_list|(
name|group
argument_list|,
name|r
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Get a named {@link ThreadFactory} that just builds daemon threads.    *    * @param prefix name prefix for all threads created from the factory    * @return a thread factory that creates named, daemon threads with    * the supplied exception handler and normal priority    */
DECL|method|newDaemonThreadFactory (final String prefix)
specifier|public
specifier|static
name|ThreadFactory
name|newDaemonThreadFactory
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
specifier|final
name|ThreadFactory
name|namedFactory
init|=
name|getNamedThreadFactory
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
return|return
operator|new
name|ThreadFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|t
init|=
name|namedFactory
operator|.
name|newThread
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|t
operator|.
name|isDaemon
argument_list|()
condition|)
block|{
name|t
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|.
name|getPriority
argument_list|()
operator|!=
name|Thread
operator|.
name|NORM_PRIORITY
condition|)
block|{
name|t
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|NORM_PRIORITY
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
block|}
return|;
block|}
DECL|method|BlockingThreadPoolExecutorService (int permitCount, ThreadPoolExecutor eventProcessingExecutor)
specifier|private
name|BlockingThreadPoolExecutorService
parameter_list|(
name|int
name|permitCount
parameter_list|,
name|ThreadPoolExecutor
name|eventProcessingExecutor
parameter_list|)
block|{
name|super
argument_list|(
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|eventProcessingExecutor
argument_list|)
argument_list|,
name|permitCount
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|eventProcessingExecutor
operator|=
name|eventProcessingExecutor
expr_stmt|;
block|}
comment|/**    * A thread pool that that blocks clients submitting additional tasks if    * there are already {@code activeTasks} running threads and {@code    * waitingTasks} tasks waiting in its queue.    *    * @param activeTasks maximum number of active tasks    * @param waitingTasks maximum number of waiting tasks    * @param keepAliveTime time until threads are cleaned up in {@code unit}    * @param unit time unit    * @param prefixName prefix of name for threads    */
DECL|method|newInstance ( int activeTasks, int waitingTasks, long keepAliveTime, TimeUnit unit, String prefixName)
specifier|public
specifier|static
name|BlockingThreadPoolExecutorService
name|newInstance
parameter_list|(
name|int
name|activeTasks
parameter_list|,
name|int
name|waitingTasks
parameter_list|,
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|String
name|prefixName
parameter_list|)
block|{
comment|/* Although we generally only expect up to waitingTasks tasks in the     queue, we need to be able to buffer all tasks in case dequeueing is     slower than enqueueing. */
specifier|final
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|workQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|(
name|waitingTasks
operator|+
name|activeTasks
argument_list|)
decl_stmt|;
name|ThreadPoolExecutor
name|eventProcessingExecutor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|activeTasks
argument_list|,
name|activeTasks
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
name|workQueue
argument_list|,
name|newDaemonThreadFactory
argument_list|(
name|prefixName
argument_list|)
argument_list|,
operator|new
name|RejectedExecutionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|rejectedExecution
parameter_list|(
name|Runnable
name|r
parameter_list|,
name|ThreadPoolExecutor
name|executor
parameter_list|)
block|{
comment|// This is not expected to happen.
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not submit task to executor {}"
argument_list|,
name|executor
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|eventProcessingExecutor
operator|.
name|allowCoreThreadTimeOut
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|BlockingThreadPoolExecutorService
argument_list|(
name|waitingTasks
operator|+
name|activeTasks
argument_list|,
name|eventProcessingExecutor
argument_list|)
return|;
block|}
comment|/**    * Get the actual number of active threads.    * @return the active thread count    */
DECL|method|getActiveCount ()
name|int
name|getActiveCount
parameter_list|()
block|{
return|return
name|eventProcessingExecutor
operator|.
name|getActiveCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"BlockingThreadPoolExecutorService{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", activeCount="
argument_list|)
operator|.
name|append
argument_list|(
name|getActiveCount
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

