begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.checker
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|checker
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
name|base
operator|.
name|Supplier
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
name|FutureCallback
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
name|Futures
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
name|test
operator|.
name|GenericTestUtils
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
name|FakeTimer
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
name|ExpectedException
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|ExecutionException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|isA
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
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
name|assertTrue
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Verify functionality of {@link ThrottledAsyncChecker}.  */
end_comment

begin_class
DECL|class|TestThrottledAsyncChecker
specifier|public
class|class
name|TestThrottledAsyncChecker
block|{
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
name|TestThrottledAsyncChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MIN_ERROR_CHECK_GAP
specifier|private
specifier|static
specifier|final
name|long
name|MIN_ERROR_CHECK_GAP
init|=
literal|1000
decl_stmt|;
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**    * Test various scheduling combinations to ensure scheduling and    * throttling behave as expected.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testScheduler ()
specifier|public
name|void
name|testScheduler
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NoOpCheckable
name|target1
init|=
operator|new
name|NoOpCheckable
argument_list|()
decl_stmt|;
specifier|final
name|NoOpCheckable
name|target2
init|=
operator|new
name|NoOpCheckable
argument_list|()
decl_stmt|;
specifier|final
name|FakeTimer
name|timer
init|=
operator|new
name|FakeTimer
argument_list|()
decl_stmt|;
name|ThrottledAsyncChecker
argument_list|<
name|Boolean
argument_list|,
name|Boolean
argument_list|>
name|checker
init|=
operator|new
name|ThrottledAsyncChecker
argument_list|<>
argument_list|(
name|timer
argument_list|,
name|MIN_ERROR_CHECK_GAP
argument_list|,
name|getExecutorService
argument_list|()
argument_list|)
decl_stmt|;
comment|// check target1 and ensure we get back the expected result.
name|assertTrue
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target1
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check target1 again without advancing the timer. target1 should not
comment|// be checked again and the cached result should be returned.
name|assertTrue
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target1
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Schedule target2 scheduled without advancing the timer.
comment|// target2 should be checked as it has never been checked before.
name|assertTrue
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target2
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target2
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Advance the timer but just short of the min gap.
comment|// Neither target1 nor target2 should be checked again.
name|timer
operator|.
name|advance
argument_list|(
name|MIN_ERROR_CHECK_GAP
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target1
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target2
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target2
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Advance the timer again.
comment|// Both targets should be checked now.
name|timer
operator|.
name|advance
argument_list|(
name|MIN_ERROR_CHECK_GAP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target1
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target2
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testCancellation ()
specifier|public
name|void
name|testCancellation
parameter_list|()
throws|throws
name|Exception
block|{
name|LatchedCheckable
name|target
init|=
operator|new
name|LatchedCheckable
argument_list|()
decl_stmt|;
specifier|final
name|FakeTimer
name|timer
init|=
operator|new
name|FakeTimer
argument_list|()
decl_stmt|;
specifier|final
name|LatchedCallback
name|callback
init|=
operator|new
name|LatchedCallback
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|ThrottledAsyncChecker
argument_list|<
name|Boolean
argument_list|,
name|Boolean
argument_list|>
name|checker
init|=
operator|new
name|ThrottledAsyncChecker
argument_list|<>
argument_list|(
name|timer
argument_list|,
name|MIN_ERROR_CHECK_GAP
argument_list|,
name|getExecutorService
argument_list|()
argument_list|)
decl_stmt|;
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
name|lf
init|=
name|checker
operator|.
name|schedule
argument_list|(
name|target
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Futures
operator|.
name|addCallback
argument_list|(
name|lf
argument_list|,
name|callback
argument_list|)
expr_stmt|;
comment|// Request immediate cancellation.
name|checker
operator|.
name|shutdownAndWait
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
try|try
block|{
name|assertFalse
argument_list|(
name|lf
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to get expected InterruptedException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ee
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|ee
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InterruptedException
argument_list|)
expr_stmt|;
block|}
name|callback
operator|.
name|failureLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testConcurrentChecks ()
specifier|public
name|void
name|testConcurrentChecks
parameter_list|()
throws|throws
name|Exception
block|{
name|LatchedCheckable
name|target
init|=
operator|new
name|LatchedCheckable
argument_list|()
decl_stmt|;
specifier|final
name|FakeTimer
name|timer
init|=
operator|new
name|FakeTimer
argument_list|()
decl_stmt|;
name|ThrottledAsyncChecker
argument_list|<
name|Boolean
argument_list|,
name|Boolean
argument_list|>
name|checker
init|=
operator|new
name|ThrottledAsyncChecker
argument_list|<>
argument_list|(
name|timer
argument_list|,
name|MIN_ERROR_CHECK_GAP
argument_list|,
name|getExecutorService
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
name|lf1
init|=
name|checker
operator|.
name|schedule
argument_list|(
name|target
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
name|lf2
init|=
name|checker
operator|.
name|schedule
argument_list|(
name|target
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Ensure that concurrent requests return the same future object.
name|assertTrue
argument_list|(
name|lf1
operator|==
name|lf2
argument_list|)
expr_stmt|;
comment|// Unblock the latch and wait for it to finish execution.
name|target
operator|.
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|lf1
operator|.
name|get
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
comment|// We should not get back the same future as before.
comment|// This can take a short while until the internal callback in
comment|// ThrottledAsyncChecker is scheduled for execution.
comment|// Also this should not trigger a new check operation as the timer
comment|// was not advanced. If it does trigger a new check then the test
comment|// will fail with a timeout.
specifier|final
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
name|lf3
init|=
name|checker
operator|.
name|schedule
argument_list|(
name|target
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|lf3
operator|!=
name|lf2
return|;
block|}
block|}
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure that the context is passed through to the Checkable#check    * method.    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testContextIsPassed ()
specifier|public
name|void
name|testContextIsPassed
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NoOpCheckable
name|target1
init|=
operator|new
name|NoOpCheckable
argument_list|()
decl_stmt|;
specifier|final
name|FakeTimer
name|timer
init|=
operator|new
name|FakeTimer
argument_list|()
decl_stmt|;
name|ThrottledAsyncChecker
argument_list|<
name|Boolean
argument_list|,
name|Boolean
argument_list|>
name|checker
init|=
operator|new
name|ThrottledAsyncChecker
argument_list|<>
argument_list|(
name|timer
argument_list|,
name|MIN_ERROR_CHECK_GAP
argument_list|,
name|getExecutorService
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target1
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|timer
operator|.
name|advance
argument_list|(
name|MIN_ERROR_CHECK_GAP
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|checker
operator|.
name|schedule
argument_list|(
name|target1
argument_list|,
literal|false
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensure that the exeption from a failed check is cached    * and returned without re-running the check when the minimum    * gap has not elapsed.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testExceptionCaching ()
specifier|public
name|void
name|testExceptionCaching
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ThrowingCheckable
name|target1
init|=
operator|new
name|ThrowingCheckable
argument_list|()
decl_stmt|;
specifier|final
name|FakeTimer
name|timer
init|=
operator|new
name|FakeTimer
argument_list|()
decl_stmt|;
name|ThrottledAsyncChecker
argument_list|<
name|Boolean
argument_list|,
name|Boolean
argument_list|>
name|checker
init|=
operator|new
name|ThrottledAsyncChecker
argument_list|<>
argument_list|(
name|timer
argument_list|,
name|MIN_ERROR_CHECK_GAP
argument_list|,
name|getExecutorService
argument_list|()
argument_list|)
decl_stmt|;
name|thrown
operator|.
name|expectCause
argument_list|(
name|isA
argument_list|(
name|DummyException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|checker
operator|.
name|schedule
argument_list|(
name|target1
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectCause
argument_list|(
name|isA
argument_list|(
name|DummyException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|checker
operator|.
name|schedule
argument_list|(
name|target1
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|target1
operator|.
name|numChecks
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * A simple ExecutorService for testing.    */
DECL|method|getExecutorService ()
specifier|private
name|ExecutorService
name|getExecutorService
parameter_list|()
block|{
return|return
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|)
return|;
block|}
comment|/**    * A Checkable that just returns its input.    */
DECL|class|NoOpCheckable
specifier|private
specifier|static
class|class
name|NoOpCheckable
implements|implements
name|Checkable
argument_list|<
name|Boolean
argument_list|,
name|Boolean
argument_list|>
block|{
DECL|field|numChecks
specifier|private
specifier|final
name|AtomicLong
name|numChecks
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|check (Boolean context)
specifier|public
name|Boolean
name|check
parameter_list|(
name|Boolean
name|context
parameter_list|)
block|{
name|numChecks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
name|context
return|;
block|}
block|}
DECL|class|ThrowingCheckable
specifier|private
specifier|static
class|class
name|ThrowingCheckable
implements|implements
name|Checkable
argument_list|<
name|Boolean
argument_list|,
name|Boolean
argument_list|>
block|{
DECL|field|numChecks
specifier|private
specifier|final
name|AtomicLong
name|numChecks
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|check (Boolean context)
specifier|public
name|Boolean
name|check
parameter_list|(
name|Boolean
name|context
parameter_list|)
throws|throws
name|DummyException
block|{
name|numChecks
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|DummyException
argument_list|()
throw|;
block|}
block|}
DECL|class|DummyException
specifier|private
specifier|static
class|class
name|DummyException
extends|extends
name|Exception
block|{   }
comment|/**    * A checkable that hangs until signaled.    */
DECL|class|LatchedCheckable
specifier|private
specifier|static
class|class
name|LatchedCheckable
implements|implements
name|Checkable
argument_list|<
name|Boolean
argument_list|,
name|Boolean
argument_list|>
block|{
DECL|field|latch
specifier|private
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
annotation|@
name|Override
DECL|method|check (Boolean ignored)
specifier|public
name|Boolean
name|check
parameter_list|(
name|Boolean
name|ignored
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LatchedCheckable {} waiting."
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
comment|// Unreachable.
block|}
block|}
comment|/**    * A {@link FutureCallback} that counts its invocations.    */
DECL|class|LatchedCallback
specifier|private
specifier|static
specifier|final
class|class
name|LatchedCallback
implements|implements
name|FutureCallback
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|field|successLatch
specifier|private
specifier|final
name|CountDownLatch
name|successLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|failureLatch
specifier|private
specifier|final
name|CountDownLatch
name|failureLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|target
specifier|private
specifier|final
name|Checkable
name|target
decl_stmt|;
DECL|method|LatchedCallback (Checkable target)
specifier|private
name|LatchedCallback
parameter_list|(
name|Checkable
name|target
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onSuccess (@onnull Boolean result)
specifier|public
name|void
name|onSuccess
parameter_list|(
annotation|@
name|Nonnull
name|Boolean
name|result
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"onSuccess callback invoked for {}"
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|successLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure (@onnull Throwable t)
specifier|public
name|void
name|onFailure
parameter_list|(
annotation|@
name|Nonnull
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"onFailure callback invoked for {} with exception"
argument_list|,
name|target
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|failureLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

