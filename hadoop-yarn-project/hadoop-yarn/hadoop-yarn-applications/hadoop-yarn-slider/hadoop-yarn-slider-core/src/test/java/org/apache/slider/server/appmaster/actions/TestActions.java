begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.actions
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
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
name|conf
operator|.
name|Configuration
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
name|service
operator|.
name|ServiceOperations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|SliderAppMaster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|AppState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
operator|.
name|ServiceThreadFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
operator|.
name|WorkflowExecutorService
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
name|Arrays
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
name|AtomicBoolean
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

begin_comment
comment|/**  * Test AM actions.  */
end_comment

begin_class
DECL|class|TestActions
specifier|public
class|class
name|TestActions
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestActions
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|queues
specifier|private
name|QueueService
name|queues
decl_stmt|;
DECL|field|executorService
specifier|private
name|WorkflowExecutorService
argument_list|<
name|ExecutorService
argument_list|>
name|executorService
decl_stmt|;
annotation|@
name|Before
DECL|method|createService ()
specifier|public
name|void
name|createService
parameter_list|()
block|{
name|queues
operator|=
operator|new
name|QueueService
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|queues
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|queues
operator|.
name|start
argument_list|()
expr_stmt|;
name|executorService
operator|=
operator|new
name|WorkflowExecutorService
argument_list|<>
argument_list|(
literal|"AmExecutor"
argument_list|,
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ServiceThreadFactory
argument_list|(
literal|"AmExecutor"
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|destroyService ()
specifier|public
name|void
name|destroyService
parameter_list|()
block|{
name|ServiceOperations
operator|.
name|stop
argument_list|(
name|executorService
argument_list|)
expr_stmt|;
name|ServiceOperations
operator|.
name|stop
argument_list|(
name|queues
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testBasicService ()
specifier|public
name|void
name|testBasicService
parameter_list|()
throws|throws
name|Throwable
block|{
name|queues
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|//@Test
DECL|method|testDelayLogic ()
specifier|public
name|void
name|testDelayLogic
parameter_list|()
throws|throws
name|Throwable
block|{
name|ActionNoteExecuted
name|action
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|""
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|delay
init|=
name|action
operator|.
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|delay
operator|>=
literal|800
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|delay
operator|<=
literal|1800
argument_list|)
expr_stmt|;
name|ActionNoteExecuted
name|a2
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|"a2"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|action
operator|.
name|compareTo
argument_list|(
name|a2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a2
operator|.
name|compareTo
argument_list|(
name|action
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|action
operator|.
name|compareTo
argument_list|(
name|action
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testActionDelayedExecutorTermination ()
specifier|public
name|void
name|testActionDelayedExecutorTermination
parameter_list|()
throws|throws
name|Throwable
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ActionStopQueue
name|stopAction
init|=
operator|new
name|ActionStopQueue
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|queues
operator|.
name|scheduledActions
operator|.
name|add
argument_list|(
name|stopAction
argument_list|)
expr_stmt|;
name|queues
operator|.
name|run
argument_list|()
expr_stmt|;
name|AsyncAction
name|take
init|=
name|queues
operator|.
name|actionQueue
operator|.
name|take
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|take
argument_list|,
name|stopAction
argument_list|)
expr_stmt|;
name|long
name|stop
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|stop
operator|-
name|start
operator|>
literal|500
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stop
operator|-
name|start
operator|<
literal|1500
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testImmediateQueue ()
specifier|public
name|void
name|testImmediateQueue
parameter_list|()
throws|throws
name|Throwable
block|{
name|ActionNoteExecuted
name|noteExecuted
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|"executed"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|queues
operator|.
name|put
argument_list|(
name|noteExecuted
argument_list|)
expr_stmt|;
name|queues
operator|.
name|put
argument_list|(
operator|new
name|ActionStopQueue
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|QueueExecutor
name|ex
init|=
operator|new
name|QueueExecutor
argument_list|(
name|queues
argument_list|)
decl_stmt|;
name|ex
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|queues
operator|.
name|actionQueue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|noteExecuted
operator|.
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testActionOrdering ()
specifier|public
name|void
name|testActionOrdering
parameter_list|()
throws|throws
name|Throwable
block|{
name|ActionNoteExecuted
name|note1
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|"note1"
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|ActionStopQueue
name|stop
init|=
operator|new
name|ActionStopQueue
argument_list|(
literal|1500
argument_list|)
decl_stmt|;
name|ActionNoteExecuted
name|note2
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|"note2"
argument_list|,
literal|800
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AsyncAction
argument_list|>
name|actions
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|note1
argument_list|,
name|stop
argument_list|,
name|note2
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|actions
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|actions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|note1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|actions
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|note2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|actions
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|stop
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testDelayedQueueWithReschedule ()
specifier|public
name|void
name|testDelayedQueueWithReschedule
parameter_list|()
throws|throws
name|Throwable
block|{
name|ActionNoteExecuted
name|note1
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|"note1"
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|ActionStopQueue
name|stop
init|=
operator|new
name|ActionStopQueue
argument_list|(
literal|1500
argument_list|)
decl_stmt|;
name|ActionNoteExecuted
name|note2
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|"note2"
argument_list|,
literal|800
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|note2
operator|.
name|compareTo
argument_list|(
name|stop
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|note1
operator|.
name|getNanos
argument_list|()
operator|<
name|note2
operator|.
name|getNanos
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|note2
operator|.
name|getNanos
argument_list|()
operator|<
name|stop
operator|.
name|getNanos
argument_list|()
argument_list|)
expr_stmt|;
name|queues
operator|.
name|schedule
argument_list|(
name|note1
argument_list|)
expr_stmt|;
name|queues
operator|.
name|schedule
argument_list|(
name|note2
argument_list|)
expr_stmt|;
name|queues
operator|.
name|schedule
argument_list|(
name|stop
argument_list|)
expr_stmt|;
comment|// async to sync expected to run in order
name|runQueuesToCompletion
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|note1
operator|.
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|note2
operator|.
name|executed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|runQueuesToCompletion ()
specifier|public
name|void
name|runQueuesToCompletion
parameter_list|()
block|{
name|queues
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|queues
operator|.
name|scheduledActions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|queues
operator|.
name|actionQueue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|QueueExecutor
name|ex
init|=
operator|new
name|QueueExecutor
argument_list|(
name|queues
argument_list|)
decl_stmt|;
name|ex
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// flush all stop commands from the queue
name|queues
operator|.
name|flushActionQueue
argument_list|(
name|ActionStopQueue
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queues
operator|.
name|actionQueue
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testRenewedActionFiresOnceAtLeast ()
specifier|public
name|void
name|testRenewedActionFiresOnceAtLeast
parameter_list|()
throws|throws
name|Throwable
block|{
name|ActionNoteExecuted
name|note1
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|"note1"
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|RenewingAction
name|renewer
init|=
operator|new
name|RenewingAction
argument_list|(
name|note1
argument_list|,
literal|500
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|queues
operator|.
name|schedule
argument_list|(
name|renewer
argument_list|)
expr_stmt|;
name|ActionStopQueue
name|stop
init|=
operator|new
name|ActionStopQueue
argument_list|(
literal|4
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|queues
operator|.
name|schedule
argument_list|(
name|stop
argument_list|)
expr_stmt|;
comment|// this runs all the delayed actions FIRST, so can't be used
comment|// to play tricks of renewing actions ahead of the stop action
name|runQueuesToCompletion
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|renewer
operator|.
name|executionCount
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|note1
operator|.
name|executionCount
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// assert the renewed item is back in
name|assertTrue
argument_list|(
name|queues
operator|.
name|scheduledActions
operator|.
name|contains
argument_list|(
name|renewer
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//@Test
DECL|method|testRenewingActionOperations ()
specifier|public
name|void
name|testRenewingActionOperations
parameter_list|()
throws|throws
name|Throwable
block|{
name|ActionNoteExecuted
name|note1
init|=
operator|new
name|ActionNoteExecuted
argument_list|(
literal|"note1"
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|RenewingAction
name|renewer
init|=
operator|new
name|RenewingAction
argument_list|(
name|note1
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|queues
operator|.
name|renewing
argument_list|(
literal|"note"
argument_list|,
name|renewer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|queues
operator|.
name|removeRenewingAction
argument_list|(
literal|"note"
argument_list|)
argument_list|)
expr_stmt|;
name|queues
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|queues
operator|.
name|waitForServiceToStop
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test action.    */
DECL|class|ActionNoteExecuted
specifier|public
class|class
name|ActionNoteExecuted
extends|extends
name|AsyncAction
block|{
DECL|field|executed
specifier|private
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|executionTimeNanos
specifier|private
specifier|final
name|AtomicLong
name|executionTimeNanos
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|executionCount
specifier|private
specifier|final
name|AtomicLong
name|executionCount
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|ActionNoteExecuted (String text, int delay)
specifier|public
name|ActionNoteExecuted
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|delay
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|delay
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute ( SliderAppMaster appMaster, QueueAccess queueService, AppState appState)
specifier|public
name|void
name|execute
parameter_list|(
name|SliderAppMaster
name|appMaster
parameter_list|,
name|QueueAccess
name|queueService
parameter_list|,
name|AppState
name|appState
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|executed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|executionTimeNanos
operator|.
name|set
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
expr_stmt|;
name|executionCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|this
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|" executed="
operator|+
name|executed
operator|.
name|get
argument_list|()
operator|+
literal|"; count="
operator|+
name|executionCount
operator|.
name|get
argument_list|()
operator|+
literal|";"
return|;
block|}
DECL|method|getExecutionCount ()
specifier|public
name|long
name|getExecutionCount
parameter_list|()
block|{
return|return
name|executionCount
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

