begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

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
name|atomic
operator|.
name|AtomicInteger
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
name|RPC
operator|.
name|RpcKind
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
name|RetryCache
operator|.
name|CacheEntryWithPayload
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_comment
comment|/**  * Tests for {@link RetryCache}  */
end_comment

begin_class
DECL|class|TestRetryCache
specifier|public
class|class
name|TestRetryCache
block|{
DECL|field|CLIENT_ID
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|CLIENT_ID
init|=
name|ClientId
operator|.
name|getClientId
argument_list|()
decl_stmt|;
DECL|field|callId
specifier|private
specifier|static
name|int
name|callId
init|=
literal|100
decl_stmt|;
DECL|field|r
specifier|private
specifier|static
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|testServer
specifier|private
specifier|static
specifier|final
name|TestServer
name|testServer
init|=
operator|new
name|TestServer
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|testServer
operator|.
name|resetCounters
argument_list|()
expr_stmt|;
block|}
DECL|class|TestServer
specifier|static
class|class
name|TestServer
block|{
DECL|field|retryCount
name|AtomicInteger
name|retryCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|operationCount
name|AtomicInteger
name|operationCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|retryCache
specifier|private
name|RetryCache
name|retryCache
init|=
operator|new
name|RetryCache
argument_list|(
literal|"TestRetryCache"
argument_list|,
literal|1
argument_list|,
literal|100
operator|*
literal|1000
operator|*
literal|1000
operator|*
literal|1000L
argument_list|)
decl_stmt|;
comment|/**      * A server method implemented using {@link RetryCache}.      *       * @param input is returned back in echo, if {@code success} is true.      * @param failureOuput returned on failure, if {@code success} is false.      * @param methodTime time taken by the operation. By passing smaller/larger      *          value one can simulate an operation that takes short/long time.      * @param success whether this operation completes successfully or not      * @return return the input parameter {@code input}, if {@code success} is      *         true, else return {@code failureOutput}.      */
DECL|method|echo (int input, int failureOutput, long methodTime, boolean success)
name|int
name|echo
parameter_list|(
name|int
name|input
parameter_list|,
name|int
name|failureOutput
parameter_list|,
name|long
name|methodTime
parameter_list|,
name|boolean
name|success
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|CacheEntryWithPayload
name|entry
init|=
name|RetryCache
operator|.
name|waitForCompletion
argument_list|(
name|retryCache
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
operator|&&
name|entry
operator|.
name|isSuccess
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"retryCount incremented "
operator|+
name|retryCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|retryCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
operator|(
name|Integer
operator|)
name|entry
operator|.
name|getPayload
argument_list|()
return|;
block|}
try|try
block|{
name|operationCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|methodTime
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|methodTime
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|RetryCache
operator|.
name|setState
argument_list|(
name|entry
argument_list|,
name|success
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
return|return
name|success
condition|?
name|input
else|:
name|failureOutput
return|;
block|}
DECL|method|resetCounters ()
name|void
name|resetCounters
parameter_list|()
block|{
name|retryCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|operationCount
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newCall ()
specifier|public
specifier|static
name|Server
operator|.
name|Call
name|newCall
parameter_list|()
block|{
return|return
operator|new
name|Server
operator|.
name|Call
argument_list|(
operator|++
name|callId
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|CLIENT_ID
argument_list|)
return|;
block|}
comment|/**    * This simlulates a long server retried operations. Multiple threads start an    * operation that takes long time and finally succeeds. The retries in this    * case end up waiting for the current operation to complete. All the retries    * then complete based on the entry in the retry cache.    */
annotation|@
name|Test
DECL|method|testLongOperationsSuccessful ()
specifier|public
name|void
name|testLongOperationsSuccessful
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test long successful operations
comment|// There is no entry in cache expected when the first operation starts
name|testOperations
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|20
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|newCall
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This simlulates a long server operation. Multiple threads start an    * operation that takes long time and finally fails. The retries in this case    * end up waiting for the current operation to complete. All the retries end    * up performing the operation again.    */
annotation|@
name|Test
DECL|method|testLongOperationsFailure ()
specifier|public
name|void
name|testLongOperationsFailure
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test long failed operations
comment|// There is no entry in cache expected when the first operation starts
name|testOperations
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|20
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newCall
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This simlulates a short server operation. Multiple threads start an    * operation that takes very short time and finally succeeds. The retries in    * this case do not wait long for the current operation to complete. All the    * retries then complete based on the entry in the retry cache.    */
annotation|@
name|Test
DECL|method|testShortOperationsSuccess ()
specifier|public
name|void
name|testShortOperationsSuccess
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test long failed operations
comment|// There is no entry in cache expected when the first operation starts
name|testOperations
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|25
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newCall
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This simlulates a short server operation. Multiple threads start an    * operation that takes short time and finally fails. The retries in this case    * do not wait for the current operation to complete. All the retries end up    * performing the operation again.    */
annotation|@
name|Test
DECL|method|testShortOperationsFailure ()
specifier|public
name|void
name|testShortOperationsFailure
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test long failed operations
comment|// There is no entry in cache expected when the first operation starts
name|testOperations
argument_list|(
name|r
operator|.
name|nextInt
argument_list|()
argument_list|,
literal|25
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|newCall
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetryAfterSuccess ()
specifier|public
name|void
name|testRetryAfterSuccess
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Previous operation successfully completed
name|Server
operator|.
name|Call
name|call
init|=
name|newCall
argument_list|()
decl_stmt|;
name|int
name|input
init|=
name|r
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|Server
operator|.
name|getCurCall
argument_list|()
operator|.
name|set
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|testServer
operator|.
name|echo
argument_list|(
name|input
argument_list|,
name|input
operator|+
literal|1
argument_list|,
literal|5
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testOperations
argument_list|(
name|input
argument_list|,
literal|25
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetryAfterFailure ()
specifier|public
name|void
name|testRetryAfterFailure
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Previous operation failed
name|Server
operator|.
name|Call
name|call
init|=
name|newCall
argument_list|()
decl_stmt|;
name|int
name|input
init|=
name|r
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|Server
operator|.
name|getCurCall
argument_list|()
operator|.
name|set
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|testServer
operator|.
name|echo
argument_list|(
name|input
argument_list|,
name|input
operator|+
literal|1
argument_list|,
literal|5
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testOperations
argument_list|(
name|input
argument_list|,
literal|25
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
DECL|method|testOperations (final int input, final int numberOfThreads, final int pause, final boolean success, final boolean attemptedBefore, final Server.Call call)
specifier|public
name|void
name|testOperations
parameter_list|(
specifier|final
name|int
name|input
parameter_list|,
specifier|final
name|int
name|numberOfThreads
parameter_list|,
specifier|final
name|int
name|pause
parameter_list|,
specifier|final
name|boolean
name|success
parameter_list|,
specifier|final
name|boolean
name|attemptedBefore
parameter_list|,
specifier|final
name|Server
operator|.
name|Call
name|call
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
specifier|final
name|int
name|failureOutput
init|=
name|input
operator|+
literal|1
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numberOfThreads
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|Integer
argument_list|>
argument_list|>
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
name|numberOfThreads
condition|;
name|i
operator|++
control|)
block|{
name|Callable
argument_list|<
name|Integer
argument_list|>
name|worker
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
name|Server
operator|.
name|getCurCall
argument_list|()
operator|.
name|set
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Server
operator|.
name|getCurCall
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|call
argument_list|)
expr_stmt|;
name|int
name|randomPause
init|=
name|pause
operator|==
literal|0
condition|?
name|pause
else|:
name|r
operator|.
name|nextInt
argument_list|(
name|pause
argument_list|)
decl_stmt|;
return|return
name|testServer
operator|.
name|echo
argument_list|(
name|input
argument_list|,
name|failureOutput
argument_list|,
name|randomPause
argument_list|,
name|success
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Future
argument_list|<
name|Integer
argument_list|>
name|submit
init|=
name|executorService
operator|.
name|submit
argument_list|(
name|worker
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|submit
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|numberOfThreads
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Future
argument_list|<
name|Integer
argument_list|>
name|future
range|:
name|list
control|)
block|{
if|if
condition|(
name|success
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|input
argument_list|,
name|future
operator|.
name|get
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|failureOutput
argument_list|,
name|future
operator|.
name|get
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|success
condition|)
block|{
comment|// If the operation was successful, all the subsequent operations
comment|// by other threads should be retries. Operation count should be 1.
name|int
name|retries
init|=
name|numberOfThreads
operator|+
operator|(
name|attemptedBefore
condition|?
literal|0
else|:
operator|-
literal|1
operator|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|testServer
operator|.
name|operationCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|retries
argument_list|,
name|testServer
operator|.
name|retryCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the operation failed, all the subsequent operations
comment|// should execute once more, hence the retry count should be 0 and
comment|// operation count should be the number of tries
name|int
name|opCount
init|=
name|numberOfThreads
operator|+
operator|(
name|attemptedBefore
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|opCount
argument_list|,
name|testServer
operator|.
name|operationCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|testServer
operator|.
name|retryCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

