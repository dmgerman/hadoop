begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net.unix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
operator|.
name|unix
package|;
end_package

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
name|Random
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
name|CountDownLatch
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Uninterruptibles
import|;
end_import

begin_class
DECL|class|TestDomainSocketWatcher
specifier|public
class|class
name|TestDomainSocketWatcher
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDomainSocketWatcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|trappedException
specifier|private
name|Throwable
name|trappedException
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|before ()
specifier|public
name|void
name|before
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|DomainSocket
operator|.
name|getLoadingFailureReason
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after ()
specifier|public
name|void
name|after
parameter_list|()
block|{
if|if
condition|(
name|trappedException
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"DomainSocketWatcher thread terminated with unexpected exception."
argument_list|,
name|trappedException
argument_list|)
throw|;
block|}
block|}
comment|/**    * Test that we can create a DomainSocketWatcher and then shut it down.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCreateShutdown ()
specifier|public
name|void
name|testCreateShutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|DomainSocketWatcher
name|watcher
init|=
name|newDomainSocketWatcher
argument_list|(
literal|10000000
argument_list|)
decl_stmt|;
name|watcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that we can get notifications out a DomainSocketWatcher.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|180000
argument_list|)
DECL|method|testDeliverNotifications ()
specifier|public
name|void
name|testDeliverNotifications
parameter_list|()
throws|throws
name|Exception
block|{
name|DomainSocketWatcher
name|watcher
init|=
name|newDomainSocketWatcher
argument_list|(
literal|10000000
argument_list|)
decl_stmt|;
name|DomainSocket
name|pair
index|[]
init|=
name|DomainSocket
operator|.
name|socketpair
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|watcher
operator|.
name|add
argument_list|(
name|pair
index|[
literal|1
index|]
argument_list|,
operator|new
name|DomainSocketWatcher
operator|.
name|Handler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|handle
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|pair
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|watcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that a java interruption can stop the watcher thread    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testInterruption ()
specifier|public
name|void
name|testInterruption
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DomainSocketWatcher
name|watcher
init|=
name|newDomainSocketWatcher
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|watcher
operator|.
name|watcherThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|Uninterruptibles
operator|.
name|joinUninterruptibly
argument_list|(
name|watcher
operator|.
name|watcherThread
argument_list|)
expr_stmt|;
name|watcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test that domain sockets are closed when the watcher is closed.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testCloseSocketOnWatcherClose ()
specifier|public
name|void
name|testCloseSocketOnWatcherClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DomainSocketWatcher
name|watcher
init|=
name|newDomainSocketWatcher
argument_list|(
literal|10000000
argument_list|)
decl_stmt|;
name|DomainSocket
name|pair
index|[]
init|=
name|DomainSocket
operator|.
name|socketpair
argument_list|()
decl_stmt|;
name|watcher
operator|.
name|add
argument_list|(
name|pair
index|[
literal|1
index|]
argument_list|,
operator|new
name|DomainSocketWatcher
operator|.
name|Handler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|handle
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|watcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|Uninterruptibles
operator|.
name|joinUninterruptibly
argument_list|(
name|watcher
operator|.
name|watcherThread
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pair
index|[
literal|1
index|]
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|300000
argument_list|)
DECL|method|testStress ()
specifier|public
name|void
name|testStress
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|SOCKET_NUM
init|=
literal|250
decl_stmt|;
specifier|final
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|final
name|DomainSocketWatcher
name|watcher
init|=
name|newDomainSocketWatcher
argument_list|(
literal|10000000
argument_list|)
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|DomainSocket
index|[]
argument_list|>
name|pairs
init|=
operator|new
name|ArrayList
argument_list|<
name|DomainSocket
index|[]
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|handled
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|adderThread
init|=
operator|new
name|Thread
argument_list|(
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
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|SOCKET_NUM
condition|;
name|i
operator|++
control|)
block|{
name|DomainSocket
name|pair
index|[]
init|=
name|DomainSocket
operator|.
name|socketpair
argument_list|()
decl_stmt|;
name|watcher
operator|.
name|add
argument_list|(
name|pair
index|[
literal|1
index|]
argument_list|,
operator|new
name|DomainSocketWatcher
operator|.
name|Handler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|handle
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
block|{
name|handled
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|pairs
operator|.
name|add
argument_list|(
name|pair
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|Thread
name|removerThread
init|=
operator|new
name|Thread
argument_list|(
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
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
name|handled
operator|.
name|get
argument_list|()
operator|!=
name|SOCKET_NUM
condition|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|pairs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|idx
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|pairs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|DomainSocket
name|pair
index|[]
init|=
name|pairs
operator|.
name|remove
argument_list|(
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|pair
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|watcher
operator|.
name|remove
argument_list|(
name|pair
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|adderThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|removerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Uninterruptibles
operator|.
name|joinUninterruptibly
argument_list|(
name|adderThread
argument_list|)
expr_stmt|;
name|Uninterruptibles
operator|.
name|joinUninterruptibly
argument_list|(
name|removerThread
argument_list|)
expr_stmt|;
name|watcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new DomainSocketWatcher and tracks its thread for termination due    * to an unexpected exception.  At the end of each test, if there was an    * unexpected exception, then that exception is thrown to force a failure of    * the test.    *    * @param interruptCheckPeriodMs interrupt check period passed to    *     DomainSocketWatcher    * @return new DomainSocketWatcher    * @throws Exception if there is any failure    */
DECL|method|newDomainSocketWatcher (int interruptCheckPeriodMs)
specifier|private
name|DomainSocketWatcher
name|newDomainSocketWatcher
parameter_list|(
name|int
name|interruptCheckPeriodMs
parameter_list|)
throws|throws
name|Exception
block|{
name|DomainSocketWatcher
name|watcher
init|=
operator|new
name|DomainSocketWatcher
argument_list|(
name|interruptCheckPeriodMs
argument_list|)
decl_stmt|;
name|watcher
operator|.
name|watcherThread
operator|.
name|setUncaughtExceptionHandler
argument_list|(
operator|new
name|Thread
operator|.
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|thread
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|trappedException
operator|=
name|t
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|watcher
return|;
block|}
block|}
end_class

end_unit

