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
name|HashMap
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCallQueueManager
specifier|public
class|class
name|TestCallQueueManager
block|{
DECL|field|manager
specifier|private
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
name|manager
decl_stmt|;
DECL|class|FakeCall
specifier|public
class|class
name|FakeCall
block|{
DECL|field|tag
specifier|public
specifier|final
name|int
name|tag
decl_stmt|;
comment|// Can be used for unique identification
DECL|method|FakeCall (int tag)
specifier|public
name|FakeCall
parameter_list|(
name|int
name|tag
parameter_list|)
block|{
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
block|}
comment|/**    * Putter produces FakeCalls    */
DECL|class|Putter
specifier|public
class|class
name|Putter
implements|implements
name|Runnable
block|{
DECL|field|cq
specifier|private
specifier|final
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
name|cq
decl_stmt|;
DECL|field|tag
specifier|public
specifier|final
name|int
name|tag
decl_stmt|;
DECL|field|callsAdded
specifier|public
specifier|volatile
name|int
name|callsAdded
init|=
literal|0
decl_stmt|;
comment|// How many calls we added, accurate unless interrupted
DECL|field|maxCalls
specifier|private
specifier|final
name|int
name|maxCalls
decl_stmt|;
DECL|field|isRunning
specifier|private
specifier|volatile
name|boolean
name|isRunning
init|=
literal|true
decl_stmt|;
DECL|method|Putter (CallQueueManager<FakeCall> aCq, int maxCalls, int tag)
specifier|public
name|Putter
parameter_list|(
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
name|aCq
parameter_list|,
name|int
name|maxCalls
parameter_list|,
name|int
name|tag
parameter_list|)
block|{
name|this
operator|.
name|maxCalls
operator|=
name|maxCalls
expr_stmt|;
name|this
operator|.
name|cq
operator|=
name|aCq
expr_stmt|;
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// Fill up to max (which is infinite if maxCalls< 0)
while|while
condition|(
name|isRunning
operator|&&
operator|(
name|callsAdded
operator|<
name|maxCalls
operator|||
name|maxCalls
operator|<
literal|0
operator|)
condition|)
block|{
name|cq
operator|.
name|put
argument_list|(
operator|new
name|FakeCall
argument_list|(
name|this
operator|.
name|tag
argument_list|)
argument_list|)
expr_stmt|;
name|callsAdded
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|this
operator|.
name|isRunning
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**    * Taker consumes FakeCalls    */
DECL|class|Taker
specifier|public
class|class
name|Taker
implements|implements
name|Runnable
block|{
DECL|field|cq
specifier|private
specifier|final
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
name|cq
decl_stmt|;
DECL|field|tag
specifier|public
specifier|final
name|int
name|tag
decl_stmt|;
comment|// if>= 0 means we will only take the matching tag, and put back
comment|// anything else
DECL|field|callsTaken
specifier|public
specifier|volatile
name|int
name|callsTaken
init|=
literal|0
decl_stmt|;
comment|// total calls taken, accurate if we aren't interrupted
DECL|field|lastResult
specifier|public
specifier|volatile
name|FakeCall
name|lastResult
init|=
literal|null
decl_stmt|;
comment|// the last thing we took
DECL|field|maxCalls
specifier|private
specifier|final
name|int
name|maxCalls
decl_stmt|;
comment|// maximum calls to take
DECL|method|Taker (CallQueueManager<FakeCall> aCq, int maxCalls, int tag)
specifier|public
name|Taker
parameter_list|(
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
name|aCq
parameter_list|,
name|int
name|maxCalls
parameter_list|,
name|int
name|tag
parameter_list|)
block|{
name|this
operator|.
name|maxCalls
operator|=
name|maxCalls
expr_stmt|;
name|this
operator|.
name|cq
operator|=
name|aCq
expr_stmt|;
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
comment|// Take while we don't exceed maxCalls, or if maxCalls is undefined (< 0)
while|while
condition|(
name|callsTaken
operator|<
name|maxCalls
operator|||
name|maxCalls
operator|<
literal|0
condition|)
block|{
name|FakeCall
name|res
init|=
name|cq
operator|.
name|take
argument_list|()
decl_stmt|;
if|if
condition|(
name|tag
operator|>=
literal|0
operator|&&
name|res
operator|.
name|tag
operator|!=
name|this
operator|.
name|tag
condition|)
block|{
comment|// This call does not match our tag, we should put it back and try again
name|cq
operator|.
name|put
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|callsTaken
operator|++
expr_stmt|;
name|lastResult
operator|=
name|res
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
block|}
block|}
comment|// Assert we can take exactly the numberOfTakes
DECL|method|assertCanTake (CallQueueManager<FakeCall> cq, int numberOfTakes, int takeAttempts)
specifier|public
name|void
name|assertCanTake
parameter_list|(
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
name|cq
parameter_list|,
name|int
name|numberOfTakes
parameter_list|,
name|int
name|takeAttempts
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Taker
name|taker
init|=
operator|new
name|Taker
argument_list|(
name|cq
argument_list|,
name|takeAttempts
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|taker
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|taker
operator|.
name|callsTaken
argument_list|,
name|numberOfTakes
argument_list|)
expr_stmt|;
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|// Assert we can put exactly the numberOfPuts
DECL|method|assertCanPut (CallQueueManager<FakeCall> cq, int numberOfPuts, int putAttempts)
specifier|public
name|void
name|assertCanPut
parameter_list|(
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
name|cq
parameter_list|,
name|int
name|numberOfPuts
parameter_list|,
name|int
name|putAttempts
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Putter
name|putter
init|=
operator|new
name|Putter
argument_list|(
name|cq
argument_list|,
name|putAttempts
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|putter
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|t
operator|.
name|join
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|putter
operator|.
name|callsAdded
argument_list|,
name|numberOfPuts
argument_list|)
expr_stmt|;
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
DECL|field|queueClass
specifier|private
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|BlockingQueue
argument_list|<
name|FakeCall
argument_list|>
argument_list|>
name|queueClass
init|=
name|CallQueueManager
operator|.
name|convertQueueClass
argument_list|(
name|LinkedBlockingQueue
operator|.
name|class
argument_list|,
name|FakeCall
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testCallQueueCapacity ()
specifier|public
name|void
name|testCallQueueCapacity
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|manager
operator|=
operator|new
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
argument_list|(
name|queueClass
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCanPut
argument_list|(
name|manager
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|)
expr_stmt|;
comment|// Will stop at 10 due to capacity
block|}
annotation|@
name|Test
DECL|method|testEmptyConsume ()
specifier|public
name|void
name|testEmptyConsume
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|manager
operator|=
operator|new
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
argument_list|(
name|queueClass
argument_list|,
literal|false
argument_list|,
literal|10
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertCanTake
argument_list|(
name|manager
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Fails since it's empty
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testSwapUnderContention ()
specifier|public
name|void
name|testSwapUnderContention
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|manager
operator|=
operator|new
name|CallQueueManager
argument_list|<
name|FakeCall
argument_list|>
argument_list|(
name|queueClass
argument_list|,
literal|false
argument_list|,
literal|5000
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Putter
argument_list|>
name|producers
init|=
operator|new
name|ArrayList
argument_list|<
name|Putter
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Taker
argument_list|>
name|consumers
init|=
operator|new
name|ArrayList
argument_list|<
name|Taker
argument_list|>
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|Runnable
argument_list|,
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|HashMap
argument_list|<
name|Runnable
argument_list|,
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
comment|// Create putters and takers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Putter
name|p
init|=
operator|new
name|Putter
argument_list|(
name|manager
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|pt
init|=
operator|new
name|Thread
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|producers
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|threads
operator|.
name|put
argument_list|(
name|p
argument_list|,
name|pt
argument_list|)
expr_stmt|;
name|pt
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Taker
name|t
init|=
operator|new
name|Taker
argument_list|(
name|manager
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Thread
name|tt
init|=
operator|new
name|Thread
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|consumers
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|threads
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|tt
argument_list|)
expr_stmt|;
name|tt
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|manager
operator|.
name|swapQueue
argument_list|(
name|queueClass
argument_list|,
literal|5000
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// Stop the producers
for|for
control|(
name|Putter
name|p
range|:
name|producers
control|)
block|{
name|p
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// Wait for consumers to wake up, then consume
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|manager
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Ensure no calls were dropped
name|long
name|totalCallsCreated
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Putter
name|p
range|:
name|producers
control|)
block|{
name|threads
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Putter
name|p
range|:
name|producers
control|)
block|{
name|threads
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|.
name|join
argument_list|()
expr_stmt|;
name|totalCallsCreated
operator|+=
name|p
operator|.
name|callsAdded
expr_stmt|;
block|}
name|long
name|totalCallsConsumed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Taker
name|t
range|:
name|consumers
control|)
block|{
name|threads
operator|.
name|get
argument_list|(
name|t
argument_list|)
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Taker
name|t
range|:
name|consumers
control|)
block|{
name|threads
operator|.
name|get
argument_list|(
name|t
argument_list|)
operator|.
name|join
argument_list|()
expr_stmt|;
name|totalCallsConsumed
operator|+=
name|t
operator|.
name|callsTaken
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|totalCallsConsumed
argument_list|,
name|totalCallsCreated
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

