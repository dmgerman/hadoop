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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|when
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|concurrent
operator|.
name|BlockingQueue
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
name|UserGroupInformation
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
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_class
DECL|class|TestFairCallQueue
specifier|public
class|class
name|TestFairCallQueue
extends|extends
name|TestCase
block|{
DECL|field|fcq
specifier|private
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
name|fcq
decl_stmt|;
DECL|method|mockCall (String id, int priority)
specifier|private
name|Schedulable
name|mockCall
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
name|Schedulable
name|mockCall
init|=
name|mock
argument_list|(
name|Schedulable
operator|.
name|class
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|mock
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCall
operator|.
name|getUserGroupInformation
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCall
operator|.
name|getPriorityLevel
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCall
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"id="
operator|+
name|id
operator|+
literal|" priority="
operator|+
name|priority
argument_list|)
expr_stmt|;
return|return
name|mockCall
return|;
block|}
DECL|method|mockCall (String id)
specifier|private
name|Schedulable
name|mockCall
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|mockCall
argument_list|(
name|id
argument_list|,
literal|0
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"ns."
operator|+
name|FairCallQueue
operator|.
name|IPC_CALLQUEUE_PRIORITY_LEVELS_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fcq
operator|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// Validate that the total capacity of all subqueues equals
comment|// the maxQueueSize for different values of maxQueueSize
DECL|method|testTotalCapacityOfSubQueues ()
specifier|public
name|void
name|testTotalCapacityOfSubQueues
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
name|fairCallQueue
decl_stmt|;
name|fairCallQueue
operator|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|1
argument_list|,
literal|1000
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fairCallQueue
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|fairCallQueue
operator|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|4
argument_list|,
literal|1000
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fairCallQueue
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|fairCallQueue
operator|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|7
argument_list|,
literal|1000
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fairCallQueue
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|fairCallQueue
operator|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|1
argument_list|,
literal|1025
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fairCallQueue
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
literal|1025
argument_list|)
expr_stmt|;
name|fairCallQueue
operator|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|4
argument_list|,
literal|1025
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fairCallQueue
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
literal|1025
argument_list|)
expr_stmt|;
name|fairCallQueue
operator|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|7
argument_list|,
literal|1025
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fairCallQueue
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
literal|1025
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrioritization ()
specifier|public
name|void
name|testPrioritization
parameter_list|()
block|{
name|int
name|numQueues
init|=
literal|10
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|fcq
operator|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
name|numQueues
argument_list|,
name|numQueues
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//Schedulable[] calls = new Schedulable[numCalls];
name|List
argument_list|<
name|Schedulable
argument_list|>
name|calls
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|numQueues
condition|;
name|i
operator|++
control|)
block|{
name|Schedulable
name|call
init|=
name|mockCall
argument_list|(
literal|"u"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|calls
operator|.
name|add
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|fcq
operator|.
name|add
argument_list|(
name|call
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicInteger
name|currentIndex
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|fcq
operator|.
name|setMultiplexer
argument_list|(
operator|new
name|RpcMultiplexer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getAndAdvanceCurrentIndex
parameter_list|()
block|{
return|return
name|currentIndex
operator|.
name|get
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// if there is no call at a given index, return the next highest
comment|// priority call available.
comment|//   v
comment|//0123456789
name|currentIndex
operator|.
name|set
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
comment|//      v
comment|//--2-456789
name|currentIndex
operator|.
name|set
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|6
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
comment|//        v
comment|//-----5-789
name|currentIndex
operator|.
name|set
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|8
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
comment|//         v
comment|//-----5-7-9
name|currentIndex
operator|.
name|set
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|9
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|calls
operator|.
name|get
argument_list|(
literal|7
argument_list|)
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
comment|//----------
name|assertNull
argument_list|(
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// Ensure that FairCallQueue properly implements BlockingQueue
comment|//
DECL|method|testPollReturnsNullWhenEmpty ()
specifier|public
name|void
name|testPollReturnsNullWhenEmpty
parameter_list|()
block|{
name|assertNull
argument_list|(
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPollReturnsTopCallWhenNotEmpty ()
specifier|public
name|void
name|testPollReturnsTopCallWhenNotEmpty
parameter_list|()
block|{
name|Schedulable
name|call
init|=
name|mockCall
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|call
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|call
argument_list|,
name|fcq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
comment|// Poll took it out so the fcq is empty
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOfferSucceeds ()
specifier|public
name|void
name|testOfferSucceeds
parameter_list|()
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// We can fit 10 calls
name|assertTrue
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOfferFailsWhenFull ()
specifier|public
name|void
name|testOfferFailsWhenFull
parameter_list|()
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// It's full
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOfferSucceedsWhenScheduledLowPriority ()
specifier|public
name|void
name|testOfferSucceedsWhenScheduledLowPriority
parameter_list|()
block|{
comment|// Scheduler will schedule into queue 0 x 5, then queue 1
name|int
name|mockedPriorities
index|[]
init|=
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|}
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|,
name|mockedPriorities
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|,
name|mockedPriorities
index|[
literal|5
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPeekNullWhenEmpty ()
specifier|public
name|void
name|testPeekNullWhenEmpty
parameter_list|()
block|{
name|assertNull
argument_list|(
name|fcq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPeekNonDestructive ()
specifier|public
name|void
name|testPeekNonDestructive
parameter_list|()
block|{
name|Schedulable
name|call
init|=
name|mockCall
argument_list|(
literal|"c"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|call
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|call
argument_list|,
name|fcq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|call
argument_list|,
name|fcq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
comment|// Non-destructive
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPeekPointsAtHead ()
specifier|public
name|void
name|testPeekPointsAtHead
parameter_list|()
block|{
name|Schedulable
name|call
init|=
name|mockCall
argument_list|(
literal|"c"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Schedulable
name|next
init|=
name|mockCall
argument_list|(
literal|"b"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|fcq
operator|.
name|offer
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|fcq
operator|.
name|offer
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|call
argument_list|,
name|fcq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
comment|// Peek points at the head
block|}
DECL|method|testPollTimeout ()
specifier|public
name|void
name|testPollTimeout
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|assertNull
argument_list|(
name|fcq
operator|.
name|poll
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPollSuccess ()
specifier|public
name|void
name|testPollSuccess
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Schedulable
name|call
init|=
name|mockCall
argument_list|(
literal|"c"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|call
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|call
argument_list|,
name|fcq
operator|.
name|poll
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOfferTimeout ()
specifier|public
name|void
name|testOfferTimeout
parameter_list|()
throws|throws
name|InterruptedException
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|)
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"e"
argument_list|)
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
comment|// It's full
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testDrainTo ()
specifier|public
name|void
name|testDrainTo
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"ns."
operator|+
name|FairCallQueue
operator|.
name|IPC_CALLQUEUE_PRIORITY_LEVELS_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
name|fcq2
init|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Start with 3 in fcq, to be drained
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fcq
operator|.
name|drainTo
argument_list|(
name|fcq2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fcq2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testDrainToWithLimit ()
specifier|public
name|void
name|testDrainToWithLimit
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"ns."
operator|+
name|FairCallQueue
operator|.
name|IPC_CALLQUEUE_PRIORITY_LEVELS_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
name|fcq2
init|=
operator|new
name|FairCallQueue
argument_list|<
name|Schedulable
argument_list|>
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|,
literal|"ns"
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// Start with 3 in fcq, to be drained
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fcq
operator|.
name|drainTo
argument_list|(
name|fcq2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fcq2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInitialRemainingCapacity ()
specifier|public
name|void
name|testInitialRemainingCapacity
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|fcq
operator|.
name|remainingCapacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFirstQueueFullRemainingCapacity ()
specifier|public
name|void
name|testFirstQueueFullRemainingCapacity
parameter_list|()
block|{
while|while
condition|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|)
argument_list|)
condition|)
empty_stmt|;
comment|// Queue 0 will fill up first, then queue 1
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fcq
operator|.
name|remainingCapacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllQueuesFullRemainingCapacity ()
specifier|public
name|void
name|testAllQueuesFullRemainingCapacity
parameter_list|()
block|{
name|int
index|[]
name|mockedPriorities
init|=
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|,
name|mockedPriorities
index|[
name|i
operator|++
index|]
argument_list|)
argument_list|)
condition|)
empty_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcq
operator|.
name|remainingCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueuesPartialFilledRemainingCapacity ()
specifier|public
name|void
name|testQueuesPartialFilledRemainingCapacity
parameter_list|()
block|{
name|int
index|[]
name|mockedPriorities
init|=
block|{
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|}
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|fcq
operator|.
name|offer
argument_list|(
name|mockCall
argument_list|(
literal|"c"
argument_list|,
name|mockedPriorities
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fcq
operator|.
name|remainingCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|BlockingQueue
argument_list|<
name|Schedulable
argument_list|>
name|cq
decl_stmt|;
DECL|field|tag
specifier|public
specifier|final
name|String
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
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|method|Putter (BlockingQueue<Schedulable> aCq, int maxCalls, String tag, CountDownLatch latch)
specifier|public
name|Putter
parameter_list|(
name|BlockingQueue
argument_list|<
name|Schedulable
argument_list|>
name|aCq
parameter_list|,
name|int
name|maxCalls
parameter_list|,
name|String
name|tag
parameter_list|,
name|CountDownLatch
name|latch
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
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
DECL|method|getTag ()
specifier|private
name|String
name|getTag
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|tag
operator|!=
literal|null
condition|)
return|return
name|this
operator|.
name|tag
return|;
return|return
literal|""
return|;
block|}
annotation|@
name|Override
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
name|callsAdded
operator|<
name|maxCalls
operator|||
name|maxCalls
operator|<
literal|0
condition|)
block|{
name|cq
operator|.
name|put
argument_list|(
name|mockCall
argument_list|(
name|getTag
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|callsAdded
operator|++
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
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
name|BlockingQueue
argument_list|<
name|Schedulable
argument_list|>
name|cq
decl_stmt|;
DECL|field|tag
specifier|public
specifier|final
name|String
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
name|Schedulable
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
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|uip
specifier|private
name|IdentityProvider
name|uip
decl_stmt|;
DECL|method|Taker (BlockingQueue<Schedulable> aCq, int maxCalls, String tag, CountDownLatch latch)
specifier|public
name|Taker
parameter_list|(
name|BlockingQueue
argument_list|<
name|Schedulable
argument_list|>
name|aCq
parameter_list|,
name|int
name|maxCalls
parameter_list|,
name|String
name|tag
parameter_list|,
name|CountDownLatch
name|latch
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
name|this
operator|.
name|uip
operator|=
operator|new
name|UserIdentityProvider
argument_list|()
expr_stmt|;
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
annotation|@
name|Override
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
name|Schedulable
name|res
init|=
name|cq
operator|.
name|take
argument_list|()
decl_stmt|;
name|String
name|identity
init|=
name|uip
operator|.
name|makeIdentity
argument_list|(
name|res
argument_list|)
decl_stmt|;
if|if
condition|(
name|tag
operator|!=
literal|null
operator|&&
name|this
operator|.
name|tag
operator|.
name|equals
argument_list|(
name|identity
argument_list|)
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
name|latch
operator|.
name|countDown
argument_list|()
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
DECL|method|assertCanTake (BlockingQueue<Schedulable> cq, int numberOfTakes, int takeAttempts)
specifier|public
name|void
name|assertCanTake
parameter_list|(
name|BlockingQueue
argument_list|<
name|Schedulable
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
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numberOfTakes
argument_list|)
decl_stmt|;
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
literal|"default"
argument_list|,
name|latch
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
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|numberOfTakes
argument_list|,
name|taker
operator|.
name|callsTaken
argument_list|)
expr_stmt|;
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|// Assert we can put exactly the numberOfPuts
DECL|method|assertCanPut (BlockingQueue<Schedulable> cq, int numberOfPuts, int putAttempts)
specifier|public
name|void
name|assertCanPut
parameter_list|(
name|BlockingQueue
argument_list|<
name|Schedulable
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
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numberOfPuts
argument_list|)
decl_stmt|;
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
literal|null
argument_list|,
name|latch
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
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|numberOfPuts
argument_list|,
name|putter
operator|.
name|callsAdded
argument_list|)
expr_stmt|;
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|// Make sure put will overflow into lower queues when the top is full
DECL|method|testPutOverflows ()
specifier|public
name|void
name|testPutOverflows
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// We can fit more than 5, even though the scheduler suggests the top queue
name|assertCanPut
argument_list|(
name|fcq
argument_list|,
literal|8
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPutBlocksWhenAllFull ()
specifier|public
name|void
name|testPutBlocksWhenAllFull
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|assertCanPut
argument_list|(
name|fcq
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Fill up
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Put more which causes overflow
name|assertCanPut
argument_list|(
name|fcq
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Will block
block|}
DECL|method|testTakeBlocksWhenEmpty ()
specifier|public
name|void
name|testTakeBlocksWhenEmpty
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|assertCanTake
argument_list|(
name|fcq
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testTakeRemovesCall ()
specifier|public
name|void
name|testTakeRemovesCall
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Schedulable
name|call
init|=
name|mockCall
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|fcq
operator|.
name|offer
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|call
argument_list|,
name|fcq
operator|.
name|take
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTakeTriesNextQueue ()
specifier|public
name|void
name|testTakeTriesNextQueue
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// A mux which only draws from q 0
name|RpcMultiplexer
name|q0mux
init|=
name|mock
argument_list|(
name|RpcMultiplexer
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|q0mux
operator|.
name|getAndAdvanceCurrentIndex
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fcq
operator|.
name|setMultiplexer
argument_list|(
name|q0mux
argument_list|)
expr_stmt|;
comment|// Make a FCQ filled with calls in q 1 but empty in q 0
name|Schedulable
name|call
init|=
name|mockCall
argument_list|(
literal|"c"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|fcq
operator|.
name|put
argument_list|(
name|call
argument_list|)
expr_stmt|;
comment|// Take from q1 even though mux said q0, since q0 empty
name|assertEquals
argument_list|(
name|call
argument_list|,
name|fcq
operator|.
name|take
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fcq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFairCallQueueMXBean ()
specifier|public
name|void
name|testFairCallQueueMXBean
parameter_list|()
throws|throws
name|Exception
block|{
name|MBeanServer
name|mbs
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|mxbeanName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"Hadoop:service=ns,name=FairCallQueue"
argument_list|)
decl_stmt|;
name|Schedulable
name|call
init|=
name|mockCall
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|fcq
operator|.
name|put
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|int
index|[]
name|queueSizes
init|=
operator|(
name|int
index|[]
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"QueueSizes"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|queueSizes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueSizes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|fcq
operator|.
name|take
argument_list|()
expr_stmt|;
name|queueSizes
operator|=
operator|(
name|int
index|[]
operator|)
name|mbs
operator|.
name|getAttribute
argument_list|(
name|mxbeanName
argument_list|,
literal|"QueueSizes"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueSizes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|queueSizes
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

