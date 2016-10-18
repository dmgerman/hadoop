begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
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
name|StopWatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|concurrent
operator|.
name|Callable
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_comment
comment|/**  * Basic test for S3A's blocking executor service.  */
end_comment

begin_class
DECL|class|ITestBlockingThreadPoolExecutorService
specifier|public
class|class
name|ITestBlockingThreadPoolExecutorService
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
DECL|field|NUM_ACTIVE_TASKS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_ACTIVE_TASKS
init|=
literal|4
decl_stmt|;
DECL|field|NUM_WAITING_TASKS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_WAITING_TASKS
init|=
literal|2
decl_stmt|;
DECL|field|TASK_SLEEP_MSEC
specifier|private
specifier|static
specifier|final
name|int
name|TASK_SLEEP_MSEC
init|=
literal|100
decl_stmt|;
DECL|field|SHUTDOWN_WAIT_MSEC
specifier|private
specifier|static
specifier|final
name|int
name|SHUTDOWN_WAIT_MSEC
init|=
literal|200
decl_stmt|;
DECL|field|SHUTDOWN_WAIT_TRIES
specifier|private
specifier|static
specifier|final
name|int
name|SHUTDOWN_WAIT_TRIES
init|=
literal|5
decl_stmt|;
DECL|field|BLOCKING_THRESHOLD_MSEC
specifier|private
specifier|static
specifier|final
name|int
name|BLOCKING_THRESHOLD_MSEC
init|=
literal|50
decl_stmt|;
DECL|field|SOME_VALUE
specifier|private
specifier|static
specifier|final
name|Integer
name|SOME_VALUE
init|=
literal|1337
decl_stmt|;
DECL|field|tpe
specifier|private
specifier|static
name|BlockingThreadPoolExecutorService
name|tpe
decl_stmt|;
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|60
operator|*
literal|1000
argument_list|)
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClass ()
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|ensureDestroyed
argument_list|()
expr_stmt|;
block|}
comment|/**    * Basic test of running one trivial task.    */
annotation|@
name|Test
DECL|method|testSubmitCallable ()
specifier|public
name|void
name|testSubmitCallable
parameter_list|()
throws|throws
name|Exception
block|{
name|ensureCreated
argument_list|()
expr_stmt|;
name|ListenableFuture
argument_list|<
name|Integer
argument_list|>
name|f
init|=
name|tpe
operator|.
name|submit
argument_list|(
name|callableSleeper
argument_list|)
decl_stmt|;
name|Integer
name|v
init|=
name|f
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|SOME_VALUE
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
comment|/**    * More involved test, including detecting blocking when at capacity.    */
annotation|@
name|Test
DECL|method|testSubmitRunnable ()
specifier|public
name|void
name|testSubmitRunnable
parameter_list|()
throws|throws
name|Exception
block|{
name|ensureCreated
argument_list|()
expr_stmt|;
name|verifyQueueSize
argument_list|(
name|tpe
argument_list|,
name|NUM_ACTIVE_TASKS
operator|+
name|NUM_WAITING_TASKS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the size of the executor's queue, by verifying that the first    * submission to block is {@code expectedQueueSize + 1}.    * @param executorService executor service to test    * @param expectedQueueSize size of queue    */
DECL|method|verifyQueueSize (ExecutorService executorService, int expectedQueueSize)
specifier|protected
name|void
name|verifyQueueSize
parameter_list|(
name|ExecutorService
name|executorService
parameter_list|,
name|int
name|expectedQueueSize
parameter_list|)
block|{
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedQueueSize
condition|;
name|i
operator|++
control|)
block|{
name|executorService
operator|.
name|submit
argument_list|(
name|sleeper
argument_list|)
expr_stmt|;
name|assertDidntBlock
argument_list|(
name|stopWatch
argument_list|)
expr_stmt|;
block|}
name|executorService
operator|.
name|submit
argument_list|(
name|sleeper
argument_list|)
expr_stmt|;
name|assertDidBlock
argument_list|(
name|stopWatch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShutdown ()
specifier|public
name|void
name|testShutdown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Cover create / destroy, regardless of when this test case runs
name|ensureCreated
argument_list|()
expr_stmt|;
name|ensureDestroyed
argument_list|()
expr_stmt|;
comment|// Cover create, execute, destroy, regardless of when test case runs
name|ensureCreated
argument_list|()
expr_stmt|;
name|testSubmitRunnable
argument_list|()
expr_stmt|;
name|ensureDestroyed
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChainedQueue ()
specifier|public
name|void
name|testChainedQueue
parameter_list|()
throws|throws
name|Throwable
block|{
name|ensureCreated
argument_list|()
expr_stmt|;
name|int
name|size
init|=
literal|2
decl_stmt|;
name|ExecutorService
name|wrapper
init|=
operator|new
name|SemaphoredDelegatingExecutor
argument_list|(
name|tpe
argument_list|,
name|size
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|verifyQueueSize
argument_list|(
name|wrapper
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|// Helper functions, etc.
DECL|method|assertDidntBlock (StopWatch sw)
specifier|private
name|void
name|assertDidntBlock
parameter_list|(
name|StopWatch
name|sw
parameter_list|)
block|{
try|try
block|{
name|assertFalse
argument_list|(
literal|"Non-blocking call took too long."
argument_list|,
name|sw
operator|.
name|now
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|>
name|BLOCKING_THRESHOLD_MSEC
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sw
operator|.
name|reset
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertDidBlock (StopWatch sw)
specifier|private
name|void
name|assertDidBlock
parameter_list|(
name|StopWatch
name|sw
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|sw
operator|.
name|now
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|<
name|BLOCKING_THRESHOLD_MSEC
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Blocking call returned too fast."
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|sw
operator|.
name|reset
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|sleeper
specifier|private
name|Runnable
name|sleeper
init|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|String
name|name
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|TASK_SLEEP_MSEC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Thread {} interrupted."
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
DECL|field|callableSleeper
specifier|private
name|Callable
argument_list|<
name|Integer
argument_list|>
name|callableSleeper
init|=
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|sleeper
operator|.
name|run
argument_list|()
expr_stmt|;
return|return
name|SOME_VALUE
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Helper function to create thread pool under test.    */
DECL|method|ensureCreated ()
specifier|private
specifier|static
name|void
name|ensureCreated
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|tpe
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating thread pool"
argument_list|)
expr_stmt|;
name|tpe
operator|=
name|BlockingThreadPoolExecutorService
operator|.
name|newInstance
argument_list|(
name|NUM_ACTIVE_TASKS
argument_list|,
name|NUM_WAITING_TASKS
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
literal|"btpetest"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Helper function to terminate thread pool under test, asserting that    * shutdown -> terminate works as expected.    */
DECL|method|ensureDestroyed ()
specifier|private
specifier|static
name|void
name|ensureDestroyed
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|tpe
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|shutdownTries
init|=
name|SHUTDOWN_WAIT_TRIES
decl_stmt|;
name|tpe
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|tpe
operator|.
name|isShutdown
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Shutdown had no effect."
argument_list|)
throw|;
block|}
while|while
condition|(
operator|!
name|tpe
operator|.
name|awaitTermination
argument_list|(
name|SHUTDOWN_WAIT_MSEC
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for thread pool shutdown."
argument_list|)
expr_stmt|;
if|if
condition|(
name|shutdownTries
operator|--
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to terminate thread pool gracefully."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|tpe
operator|.
name|isTerminated
argument_list|()
condition|)
block|{
name|tpe
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|tpe
operator|.
name|awaitTermination
argument_list|(
name|SHUTDOWN_WAIT_MSEC
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to terminate thread pool in timely manner."
argument_list|)
throw|;
block|}
block|}
name|tpe
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

