begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.retry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
operator|.
name|RetryPolicies
operator|.
name|*
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
operator|.
name|RetryAction
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
operator|.
name|RetryAction
operator|.
name|RetryDecision
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
name|io
operator|.
name|retry
operator|.
name|UnreliableInterface
operator|.
name|FatalException
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
name|io
operator|.
name|retry
operator|.
name|UnreliableInterface
operator|.
name|UnreliableException
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
name|ipc
operator|.
name|ProtocolTranslator
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
name|ipc
operator|.
name|RemoteException
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
name|AccessControlException
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
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

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
name|io
operator|.
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|*
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|retry
operator|.
name|RetryPolicies
operator|.
name|*
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
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
name|ArgumentMatchers
operator|.
name|anyBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|anyInt
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
name|*
import|;
end_import

begin_comment
comment|/**  * TestRetryProxy tests the behaviour of the {@link RetryPolicy} class using  * a certain method of {@link UnreliableInterface} implemented by  * {@link UnreliableImplementation}.  *  * Some methods may be sensitive to the {@link Idempotent} annotation  * (annotated in {@link UnreliableInterface}).  */
end_comment

begin_class
DECL|class|TestRetryProxy
specifier|public
class|class
name|TestRetryProxy
block|{
DECL|field|unreliableImpl
specifier|private
name|UnreliableImplementation
name|unreliableImpl
decl_stmt|;
DECL|field|caughtRetryAction
specifier|private
name|RetryAction
name|caughtRetryAction
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|unreliableImpl
operator|=
operator|new
name|UnreliableImplementation
argument_list|()
expr_stmt|;
block|}
comment|// answer mockPolicy's method with realPolicy, caught method's return value
DECL|method|setupMockPolicy (RetryPolicy mockPolicy, final RetryPolicy realPolicy)
specifier|private
name|void
name|setupMockPolicy
parameter_list|(
name|RetryPolicy
name|mockPolicy
parameter_list|,
specifier|final
name|RetryPolicy
name|realPolicy
parameter_list|)
throws|throws
name|Exception
block|{
name|when
argument_list|(
name|mockPolicy
operator|.
name|shouldRetry
argument_list|(
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|RetryAction
argument_list|>
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
specifier|public
name|RetryAction
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|Object
index|[]
name|args
init|=
name|invocation
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|Exception
name|e
init|=
operator|(
name|Exception
operator|)
name|args
index|[
literal|0
index|]
decl_stmt|;
name|int
name|retries
init|=
operator|(
name|int
operator|)
name|args
index|[
literal|1
index|]
decl_stmt|;
name|int
name|failovers
init|=
operator|(
name|int
operator|)
name|args
index|[
literal|2
index|]
decl_stmt|;
name|boolean
name|isIdempotentOrAtMostOnce
init|=
operator|(
name|boolean
operator|)
name|args
index|[
literal|3
index|]
decl_stmt|;
name|caughtRetryAction
operator|=
name|realPolicy
operator|.
name|shouldRetry
argument_list|(
name|e
argument_list|,
name|retries
argument_list|,
name|failovers
argument_list|,
name|isIdempotentOrAtMostOnce
argument_list|)
expr_stmt|;
return|return
name|caughtRetryAction
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTryOnceThenFail ()
specifier|public
name|void
name|testTryOnceThenFail
parameter_list|()
throws|throws
name|Exception
block|{
name|RetryPolicy
name|policy
init|=
name|mock
argument_list|(
name|TryOnceThenFail
operator|.
name|class
argument_list|)
decl_stmt|;
name|RetryPolicy
name|realPolicy
init|=
name|TRY_ONCE_THEN_FAIL
decl_stmt|;
name|setupMockPolicy
argument_list|(
name|policy
argument_list|,
name|realPolicy
argument_list|)
expr_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|policy
argument_list|)
decl_stmt|;
name|unreliable
operator|.
name|alwaysSucceeds
argument_list|()
expr_stmt|;
try|try
block|{
name|unreliable
operator|.
name|failsOnceThenSucceeds
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnreliableException
name|e
parameter_list|)
block|{
comment|// expected
name|verify
argument_list|(
name|policy
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|shouldRetry
argument_list|(
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetryDecision
operator|.
name|FAIL
argument_list|,
name|caughtRetryAction
operator|.
name|action
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"try once and fail."
argument_list|,
name|caughtRetryAction
operator|.
name|reason
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Other exception other than UnreliableException should also get "
operator|+
literal|"failed."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test for {@link RetryInvocationHandler#isRpcInvocation(Object)}    */
annotation|@
name|Test
DECL|method|testRpcInvocation ()
specifier|public
name|void
name|testRpcInvocation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// For a proxy method should return true
specifier|final
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|RETRY_FOREVER
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|RetryInvocationHandler
operator|.
name|isRpcInvocation
argument_list|(
name|unreliable
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|// Embed the proxy in ProtocolTranslator
name|ProtocolTranslator
name|xlator
init|=
operator|new
name|ProtocolTranslator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|getUnderlyingProxyObject
parameter_list|()
block|{
name|count
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
return|return
name|unreliable
return|;
block|}
block|}
decl_stmt|;
comment|// For a proxy wrapped in ProtocolTranslator method should return true
name|assertTrue
argument_list|(
name|RetryInvocationHandler
operator|.
name|isRpcInvocation
argument_list|(
name|xlator
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure underlying proxy was looked at
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// For non-proxy the method must return false
name|assertFalse
argument_list|(
name|RetryInvocationHandler
operator|.
name|isRpcInvocation
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetryForever ()
specifier|public
name|void
name|testRetryForever
parameter_list|()
throws|throws
name|UnreliableException
block|{
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|RETRY_FOREVER
argument_list|)
decl_stmt|;
name|unreliable
operator|.
name|alwaysSucceeds
argument_list|()
expr_stmt|;
name|unreliable
operator|.
name|failsOnceThenSucceeds
argument_list|()
expr_stmt|;
name|unreliable
operator|.
name|failsTenTimesThenSucceeds
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetryForeverWithFixedSleep ()
specifier|public
name|void
name|testRetryForeverWithFixedSleep
parameter_list|()
throws|throws
name|UnreliableException
block|{
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|retryForeverWithFixedSleep
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
decl_stmt|;
name|unreliable
operator|.
name|alwaysSucceeds
argument_list|()
expr_stmt|;
name|unreliable
operator|.
name|failsOnceThenSucceeds
argument_list|()
expr_stmt|;
name|unreliable
operator|.
name|failsTenTimesThenSucceeds
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetryUpToMaximumCountWithFixedSleep ()
specifier|public
name|void
name|testRetryUpToMaximumCountWithFixedSleep
parameter_list|()
throws|throws
name|Exception
block|{
name|RetryPolicy
name|policy
init|=
name|mock
argument_list|(
name|RetryUpToMaximumCountWithFixedSleep
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|maxRetries
init|=
literal|8
decl_stmt|;
name|RetryPolicy
name|realPolicy
init|=
name|retryUpToMaximumCountWithFixedSleep
argument_list|(
name|maxRetries
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|setupMockPolicy
argument_list|(
name|policy
argument_list|,
name|realPolicy
argument_list|)
expr_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|policy
argument_list|)
decl_stmt|;
comment|// shouldRetry += 1
name|unreliable
operator|.
name|alwaysSucceeds
argument_list|()
expr_stmt|;
comment|// shouldRetry += 2
name|unreliable
operator|.
name|failsOnceThenSucceeds
argument_list|()
expr_stmt|;
try|try
block|{
comment|// shouldRetry += (maxRetries -1) (just failed once above)
name|unreliable
operator|.
name|failsTenTimesThenSucceeds
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnreliableException
name|e
parameter_list|)
block|{
comment|// expected
name|verify
argument_list|(
name|policy
argument_list|,
name|times
argument_list|(
name|maxRetries
operator|+
literal|2
argument_list|)
argument_list|)
operator|.
name|shouldRetry
argument_list|(
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetryDecision
operator|.
name|FAIL
argument_list|,
name|caughtRetryAction
operator|.
name|action
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetryUpToMaximumCountWithFixedSleep
operator|.
name|constructReasonString
argument_list|(
name|maxRetries
argument_list|)
argument_list|,
name|caughtRetryAction
operator|.
name|reason
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Other exception other than UnreliableException should also get "
operator|+
literal|"failed."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRetryUpToMaximumTimeWithFixedSleep ()
specifier|public
name|void
name|testRetryUpToMaximumTimeWithFixedSleep
parameter_list|()
throws|throws
name|Exception
block|{
name|RetryPolicy
name|policy
init|=
name|mock
argument_list|(
name|RetryUpToMaximumTimeWithFixedSleep
operator|.
name|class
argument_list|)
decl_stmt|;
name|long
name|maxTime
init|=
literal|80L
decl_stmt|;
name|RetryPolicy
name|realPolicy
init|=
name|retryUpToMaximumTimeWithFixedSleep
argument_list|(
name|maxTime
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|setupMockPolicy
argument_list|(
name|policy
argument_list|,
name|realPolicy
argument_list|)
expr_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|policy
argument_list|)
decl_stmt|;
name|unreliable
operator|.
name|alwaysSucceeds
argument_list|()
expr_stmt|;
name|unreliable
operator|.
name|failsOnceThenSucceeds
argument_list|()
expr_stmt|;
try|try
block|{
name|unreliable
operator|.
name|failsTenTimesThenSucceeds
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnreliableException
name|e
parameter_list|)
block|{
comment|// expected
name|verify
argument_list|(
name|policy
argument_list|,
name|times
argument_list|(
call|(
name|int
call|)
argument_list|(
name|maxTime
operator|/
literal|10
argument_list|)
operator|+
literal|2
argument_list|)
argument_list|)
operator|.
name|shouldRetry
argument_list|(
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetryDecision
operator|.
name|FAIL
argument_list|,
name|caughtRetryAction
operator|.
name|action
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetryUpToMaximumTimeWithFixedSleep
operator|.
name|constructReasonString
argument_list|(
name|maxTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|,
name|caughtRetryAction
operator|.
name|reason
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Other exception other than UnreliableException should also get "
operator|+
literal|"failed."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRetryUpToMaximumCountWithProportionalSleep ()
specifier|public
name|void
name|testRetryUpToMaximumCountWithProportionalSleep
parameter_list|()
throws|throws
name|UnreliableException
block|{
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|retryUpToMaximumCountWithProportionalSleep
argument_list|(
literal|8
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
decl_stmt|;
name|unreliable
operator|.
name|alwaysSucceeds
argument_list|()
expr_stmt|;
name|unreliable
operator|.
name|failsOnceThenSucceeds
argument_list|()
expr_stmt|;
try|try
block|{
name|unreliable
operator|.
name|failsTenTimesThenSucceeds
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnreliableException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testExponentialRetry ()
specifier|public
name|void
name|testExponentialRetry
parameter_list|()
throws|throws
name|UnreliableException
block|{
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|exponentialBackoffRetry
argument_list|(
literal|5
argument_list|,
literal|1L
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
decl_stmt|;
name|unreliable
operator|.
name|alwaysSucceeds
argument_list|()
expr_stmt|;
name|unreliable
operator|.
name|failsOnceThenSucceeds
argument_list|()
expr_stmt|;
try|try
block|{
name|unreliable
operator|.
name|failsTenTimesThenSucceeds
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnreliableException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRetryByException ()
specifier|public
name|void
name|testRetryByException
parameter_list|()
throws|throws
name|UnreliableException
block|{
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
name|exceptionToPolicyMap
init|=
name|Collections
operator|.
expr|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
condition|,
name|RetryPolicy
operator|>
name|singletonMap
argument_list|(
name|FatalException
operator|.
name|class
argument_list|,
name|TRY_ONCE_THEN_FAIL
argument_list|)
decl_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|retryByException
argument_list|(
name|RETRY_FOREVER
argument_list|,
name|exceptionToPolicyMap
argument_list|)
argument_list|)
decl_stmt|;
name|unreliable
operator|.
name|failsOnceThenSucceeds
argument_list|()
expr_stmt|;
try|try
block|{
name|unreliable
operator|.
name|alwaysFailsWithFatalException
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FatalException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRetryByRemoteException ()
specifier|public
name|void
name|testRetryByRemoteException
parameter_list|()
block|{
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
name|exceptionToPolicyMap
init|=
name|Collections
operator|.
expr|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
condition|,
name|RetryPolicy
operator|>
name|singletonMap
argument_list|(
name|FatalException
operator|.
name|class
argument_list|,
name|TRY_ONCE_THEN_FAIL
argument_list|)
decl_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|retryByRemoteException
argument_list|(
name|RETRY_FOREVER
argument_list|,
name|exceptionToPolicyMap
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|unreliable
operator|.
name|alwaysFailsWithRemoteFatalException
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRetryOtherThanRemoteException ()
specifier|public
name|void
name|testRetryOtherThanRemoteException
parameter_list|()
throws|throws
name|Throwable
block|{
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|,
name|RetryPolicy
argument_list|>
name|exceptionToPolicyMap
init|=
name|Collections
operator|.
expr|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
condition|,
name|RetryPolicy
operator|>
name|singletonMap
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|RETRY_FOREVER
argument_list|)
decl_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|retryOtherThanRemoteException
argument_list|(
name|TRY_ONCE_THEN_FAIL
argument_list|,
name|exceptionToPolicyMap
argument_list|)
argument_list|)
decl_stmt|;
comment|// should retry with local IOException.
name|unreliable
operator|.
name|failsOnceWithIOException
argument_list|()
expr_stmt|;
try|try
block|{
comment|// won't get retry on remote exception
name|unreliable
operator|.
name|failsOnceWithRemoteException
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testRetryInterruptible ()
specifier|public
name|void
name|testRetryInterruptible
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|retryUpToMaximumTimeWithFixedSleep
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
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
specifier|final
name|AtomicReference
argument_list|<
name|Thread
argument_list|>
name|futureThread
init|=
operator|new
name|AtomicReference
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
name|ExecutorService
name|exec
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|Throwable
argument_list|>
name|future
init|=
name|exec
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Throwable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Throwable
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|futureThread
operator|.
name|set
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|unreliable
operator|.
name|alwaysFailsWithFatalException
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|ute
parameter_list|)
block|{
return|return
name|ute
operator|.
name|getCause
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// time to fail and sleep
name|assertTrue
argument_list|(
name|futureThread
operator|.
name|get
argument_list|()
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
name|futureThread
operator|.
name|get
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|Throwable
name|e
init|=
name|future
operator|.
name|get
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
comment|// should return immediately
name|assertNotNull
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|InterruptedIOException
operator|.
name|class
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Retry interrupted"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|InterruptedException
operator|.
name|class
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sleep interrupted"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoRetryOnSaslError ()
specifier|public
name|void
name|testNoRetryOnSaslError
parameter_list|()
throws|throws
name|Exception
block|{
name|RetryPolicy
name|policy
init|=
name|mock
argument_list|(
name|RetryPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|RetryPolicy
name|realPolicy
init|=
name|RetryPolicies
operator|.
name|failoverOnNetworkException
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|setupMockPolicy
argument_list|(
name|policy
argument_list|,
name|realPolicy
argument_list|)
expr_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|policy
argument_list|)
decl_stmt|;
try|try
block|{
name|unreliable
operator|.
name|failsWithSASLExceptionTenTimes
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SaslException
name|e
parameter_list|)
block|{
comment|// expected
name|verify
argument_list|(
name|policy
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|shouldRetry
argument_list|(
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetryDecision
operator|.
name|FAIL
argument_list|,
name|caughtRetryAction
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNoRetryOnAccessControlException ()
specifier|public
name|void
name|testNoRetryOnAccessControlException
parameter_list|()
throws|throws
name|Exception
block|{
name|RetryPolicy
name|policy
init|=
name|mock
argument_list|(
name|RetryPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|RetryPolicy
name|realPolicy
init|=
name|RetryPolicies
operator|.
name|failoverOnNetworkException
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|setupMockPolicy
argument_list|(
name|policy
argument_list|,
name|realPolicy
argument_list|)
expr_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|policy
argument_list|)
decl_stmt|;
try|try
block|{
name|unreliable
operator|.
name|failsWithAccessControlExceptionEightTimes
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// expected
name|verify
argument_list|(
name|policy
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|shouldRetry
argument_list|(
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetryDecision
operator|.
name|FAIL
argument_list|,
name|caughtRetryAction
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWrappedAccessControlException ()
specifier|public
name|void
name|testWrappedAccessControlException
parameter_list|()
throws|throws
name|Exception
block|{
name|RetryPolicy
name|policy
init|=
name|mock
argument_list|(
name|RetryPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|RetryPolicy
name|realPolicy
init|=
name|RetryPolicies
operator|.
name|failoverOnNetworkException
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|setupMockPolicy
argument_list|(
name|policy
argument_list|,
name|realPolicy
argument_list|)
expr_stmt|;
name|UnreliableInterface
name|unreliable
init|=
operator|(
name|UnreliableInterface
operator|)
name|RetryProxy
operator|.
name|create
argument_list|(
name|UnreliableInterface
operator|.
name|class
argument_list|,
name|unreliableImpl
argument_list|,
name|policy
argument_list|)
decl_stmt|;
try|try
block|{
name|unreliable
operator|.
name|failsWithWrappedAccessControlException
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{
name|verify
argument_list|(
name|policy
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|shouldRetry
argument_list|(
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyInt
argument_list|()
argument_list|,
name|anyBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RetryDecision
operator|.
name|FAIL
argument_list|,
name|caughtRetryAction
operator|.
name|action
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

