begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

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
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Set
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
name|regex
operator|.
name|Pattern
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
name|impl
operator|.
name|Log4JLogger
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
name|FileUtil
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
name|StringUtils
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
name|Time
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Layout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|WriterAppender
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|collect
operator|.
name|Sets
import|;
end_import

begin_comment
comment|/**  * Test provides some very generic helpers which might be used across the tests  */
end_comment

begin_class
DECL|class|GenericTestUtils
specifier|public
specifier|abstract
class|class
name|GenericTestUtils
block|{
DECL|field|sequence
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|sequence
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**    * Extracts the name of the method where the invocation has happened    * @return String name of the invoking method    */
DECL|method|getMethodName ()
specifier|public
specifier|static
name|String
name|getMethodName
parameter_list|()
block|{
return|return
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getStackTrace
argument_list|()
index|[
literal|2
index|]
operator|.
name|getMethodName
argument_list|()
return|;
block|}
comment|/**    * Generates a process-wide unique sequence number.    * @return an unique sequence number    */
DECL|method|uniqueSequenceId ()
specifier|public
specifier|static
name|int
name|uniqueSequenceId
parameter_list|()
block|{
return|return
name|sequence
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/**    * Assert that a given file exists.    */
DECL|method|assertExists (File f)
specifier|public
specifier|static
name|void
name|assertExists
parameter_list|(
name|File
name|f
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"File "
operator|+
name|f
operator|+
literal|" should exist"
argument_list|,
name|f
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * List all of the files in 'dir' that match the regex 'pattern'.    * Then check that this list is identical to 'expectedMatches'.    * @throws IOException if the dir is inaccessible    */
DECL|method|assertGlobEquals (File dir, String pattern, String ... expectedMatches)
specifier|public
specifier|static
name|void
name|assertGlobEquals
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|pattern
parameter_list|,
name|String
modifier|...
name|expectedMatches
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|found
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|FileUtil
operator|.
name|listFiles
argument_list|(
name|dir
argument_list|)
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|matches
argument_list|(
name|pattern
argument_list|)
condition|)
block|{
name|found
operator|.
name|add
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|expectedSet
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedMatches
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Bad files matching "
operator|+
name|pattern
operator|+
literal|" in "
operator|+
name|dir
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|found
argument_list|)
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|expectedSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExceptionContains (String string, Throwable t)
specifier|public
specifier|static
name|void
name|assertExceptionContains
parameter_list|(
name|String
name|string
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|String
name|msg
init|=
name|t
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected to find '"
operator|+
name|string
operator|+
literal|"' but got unexpected exception:"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|t
argument_list|)
argument_list|,
name|msg
operator|.
name|contains
argument_list|(
name|string
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|waitFor (Supplier<Boolean> check, int checkEveryMillis, int waitForMillis)
specifier|public
specifier|static
name|void
name|waitFor
parameter_list|(
name|Supplier
argument_list|<
name|Boolean
argument_list|>
name|check
parameter_list|,
name|int
name|checkEveryMillis
parameter_list|,
name|int
name|waitForMillis
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
block|{
name|long
name|st
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
do|do
block|{
name|boolean
name|result
init|=
name|check
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|checkEveryMillis
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|-
name|st
operator|<
name|waitForMillis
condition|)
do|;
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Timed out waiting for condition. "
operator|+
literal|"Thread diagnostics:\n"
operator|+
name|TimedOutTestsListener
operator|.
name|buildThreadDiagnosticString
argument_list|()
argument_list|)
throw|;
block|}
DECL|class|LogCapturer
specifier|public
specifier|static
class|class
name|LogCapturer
block|{
DECL|field|sw
specifier|private
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
DECL|field|appender
specifier|private
name|WriterAppender
name|appender
decl_stmt|;
DECL|field|logger
specifier|private
name|Logger
name|logger
decl_stmt|;
DECL|method|captureLogs (Log l)
specifier|public
specifier|static
name|LogCapturer
name|captureLogs
parameter_list|(
name|Log
name|l
parameter_list|)
block|{
name|Logger
name|logger
init|=
operator|(
operator|(
name|Log4JLogger
operator|)
name|l
operator|)
operator|.
name|getLogger
argument_list|()
decl_stmt|;
name|LogCapturer
name|c
init|=
operator|new
name|LogCapturer
argument_list|(
name|logger
argument_list|)
decl_stmt|;
return|return
name|c
return|;
block|}
DECL|method|LogCapturer (Logger logger)
specifier|private
name|LogCapturer
parameter_list|(
name|Logger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|Layout
name|layout
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|getAppender
argument_list|(
literal|"stdout"
argument_list|)
operator|.
name|getLayout
argument_list|()
decl_stmt|;
name|WriterAppender
name|wa
init|=
operator|new
name|WriterAppender
argument_list|(
name|layout
argument_list|,
name|sw
argument_list|)
decl_stmt|;
name|logger
operator|.
name|addAppender
argument_list|(
name|wa
argument_list|)
expr_stmt|;
block|}
DECL|method|getOutput ()
specifier|public
name|String
name|getOutput
parameter_list|()
block|{
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|stopCapturing ()
specifier|public
name|void
name|stopCapturing
parameter_list|()
block|{
name|logger
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Mockito answer helper that triggers one latch as soon as the    * method is called, then waits on another before continuing.    */
DECL|class|DelayAnswer
specifier|public
specifier|static
class|class
name|DelayAnswer
implements|implements
name|Answer
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|final
name|Log
name|LOG
decl_stmt|;
DECL|field|fireLatch
specifier|private
specifier|final
name|CountDownLatch
name|fireLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|waitLatch
specifier|private
specifier|final
name|CountDownLatch
name|waitLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|resultLatch
specifier|private
specifier|final
name|CountDownLatch
name|resultLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Result fields set after proceed() is called.
DECL|field|thrown
specifier|private
specifier|volatile
name|Throwable
name|thrown
decl_stmt|;
DECL|field|returnValue
specifier|private
specifier|volatile
name|Object
name|returnValue
decl_stmt|;
DECL|method|DelayAnswer (Log log)
specifier|public
name|DelayAnswer
parameter_list|(
name|Log
name|log
parameter_list|)
block|{
name|this
operator|.
name|LOG
operator|=
name|log
expr_stmt|;
block|}
comment|/**      * Wait until the method is called.      */
DECL|method|waitForCall ()
specifier|public
name|void
name|waitForCall
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|fireLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tell the method to proceed.      * This should only be called after waitForCall()      */
DECL|method|proceed ()
specifier|public
name|void
name|proceed
parameter_list|()
block|{
name|waitLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|Object
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DelayAnswer firing fireLatch"
argument_list|)
expr_stmt|;
name|fireLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DelayAnswer waiting on waitLatch"
argument_list|)
expr_stmt|;
name|waitLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DelayAnswer delay complete"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Interrupted waiting on latch"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
return|return
name|passThrough
argument_list|(
name|invocation
argument_list|)
return|;
block|}
DECL|method|passThrough (InvocationOnMock invocation)
specifier|protected
name|Object
name|passThrough
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
try|try
block|{
name|Object
name|ret
init|=
name|invocation
operator|.
name|callRealMethod
argument_list|()
decl_stmt|;
name|returnValue
operator|=
name|ret
expr_stmt|;
return|return
name|ret
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|thrown
operator|=
name|t
expr_stmt|;
throw|throw
name|t
throw|;
block|}
finally|finally
block|{
name|resultLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * After calling proceed(), this will wait until the call has      * completed and a result has been returned to the caller.      */
DECL|method|waitForResult ()
specifier|public
name|void
name|waitForResult
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|resultLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
comment|/**      * After the call has gone through, return any exception that      * was thrown, or null if no exception was thrown.      */
DECL|method|getThrown ()
specifier|public
name|Throwable
name|getThrown
parameter_list|()
block|{
return|return
name|thrown
return|;
block|}
comment|/**      * After the call has gone through, return the call's return value,      * or null in case it was void or an exception was thrown.      */
DECL|method|getReturnValue ()
specifier|public
name|Object
name|getReturnValue
parameter_list|()
block|{
return|return
name|returnValue
return|;
block|}
block|}
comment|/**    * An Answer implementation that simply forwards all calls through    * to a delegate.    *     * This is useful as the default Answer for a mock object, to create    * something like a spy on an RPC proxy. For example:    *<code>    *    NamenodeProtocol origNNProxy = secondary.getNameNode();    *    NamenodeProtocol spyNNProxy = Mockito.mock(NameNodeProtocol.class,    *        new DelegateAnswer(origNNProxy);    *    doThrow(...).when(spyNNProxy).getBlockLocations(...);    *    ...    *</code>    */
DECL|class|DelegateAnswer
specifier|public
specifier|static
class|class
name|DelegateAnswer
implements|implements
name|Answer
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Object
name|delegate
decl_stmt|;
DECL|method|DelegateAnswer (Object delegate)
specifier|public
name|DelegateAnswer
parameter_list|(
name|Object
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|Object
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
name|invocation
operator|.
name|getMethod
argument_list|()
operator|.
name|invoke
argument_list|(
name|delegate
argument_list|,
name|invocation
operator|.
name|getArguments
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * An Answer implementation which sleeps for a random number of milliseconds    * between 0 and a configurable value before delegating to the real    * implementation of the method. This can be useful for drawing out race    * conditions.    */
DECL|class|SleepAnswer
specifier|public
specifier|static
class|class
name|SleepAnswer
implements|implements
name|Answer
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|maxSleepTime
specifier|private
specifier|final
name|int
name|maxSleepTime
decl_stmt|;
DECL|field|r
specifier|private
specifier|static
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|SleepAnswer (int maxSleepTime)
specifier|public
name|SleepAnswer
parameter_list|(
name|int
name|maxSleepTime
parameter_list|)
block|{
name|this
operator|.
name|maxSleepTime
operator|=
name|maxSleepTime
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|answer (InvocationOnMock invocation)
specifier|public
name|Object
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|boolean
name|interrupted
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|maxSleepTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|interrupted
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
return|return
name|invocation
operator|.
name|callRealMethod
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|interrupted
condition|)
block|{
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
block|}
DECL|method|assertMatches (String output, String pattern)
specifier|public
specifier|static
name|void
name|assertMatches
parameter_list|(
name|String
name|output
parameter_list|,
name|String
name|pattern
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Expected output to match /"
operator|+
name|pattern
operator|+
literal|"/"
operator|+
literal|" but got:\n"
operator|+
name|output
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
name|pattern
argument_list|)
operator|.
name|matcher
argument_list|(
name|output
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

