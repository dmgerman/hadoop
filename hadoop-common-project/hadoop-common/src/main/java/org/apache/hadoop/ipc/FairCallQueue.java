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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractQueue
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Semaphore
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|lang
operator|.
name|NotImplementedException
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
name|CallQueueManager
operator|.
name|CallQueueOverflowException
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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

begin_comment
comment|/**  * A queue with multiple levels for each priority.  */
end_comment

begin_class
DECL|class|FairCallQueue
specifier|public
class|class
name|FairCallQueue
parameter_list|<
name|E
extends|extends
name|Schedulable
parameter_list|>
extends|extends
name|AbstractQueue
argument_list|<
name|E
argument_list|>
implements|implements
name|BlockingQueue
argument_list|<
name|E
argument_list|>
block|{
annotation|@
name|Deprecated
DECL|field|IPC_CALLQUEUE_PRIORITY_LEVELS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|IPC_CALLQUEUE_PRIORITY_LEVELS_DEFAULT
init|=
literal|4
decl_stmt|;
annotation|@
name|Deprecated
DECL|field|IPC_CALLQUEUE_PRIORITY_LEVELS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|IPC_CALLQUEUE_PRIORITY_LEVELS_KEY
init|=
literal|"faircallqueue.priority-levels"
decl_stmt|;
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
name|FairCallQueue
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/* The queues */
DECL|field|queues
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|BlockingQueue
argument_list|<
name|E
argument_list|>
argument_list|>
name|queues
decl_stmt|;
comment|/* Track available permits for scheduled objects.  All methods that will    * mutate a subqueue must acquire or release a permit on the semaphore.    * A semaphore is much faster than an exclusive lock because producers do    * not contend with consumers and consumers do not block other consumers    * while polling.    */
DECL|field|semaphore
specifier|private
specifier|final
name|Semaphore
name|semaphore
init|=
operator|new
name|Semaphore
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|signalNotEmpty ()
specifier|private
name|void
name|signalNotEmpty
parameter_list|()
block|{
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
comment|/* Multiplexer picks which queue to draw from */
DECL|field|multiplexer
specifier|private
name|RpcMultiplexer
name|multiplexer
decl_stmt|;
comment|/* Statistic tracking */
DECL|field|overflowedCalls
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|AtomicLong
argument_list|>
name|overflowedCalls
decl_stmt|;
comment|/**    * Create a FairCallQueue.    * @param capacity the total size of all sub-queues    * @param ns the prefix to use for configuration    * @param conf the configuration to read from    * Notes: Each sub-queue has a capacity of `capacity / numSubqueues`.    * The first or the highest priority sub-queue has an excess capacity    * of `capacity % numSubqueues`    */
DECL|method|FairCallQueue (int priorityLevels, int capacity, String ns, Configuration conf)
specifier|public
name|FairCallQueue
parameter_list|(
name|int
name|priorityLevels
parameter_list|,
name|int
name|capacity
parameter_list|,
name|String
name|ns
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|priorityLevels
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Number of Priority Levels must be "
operator|+
literal|"at least 1"
argument_list|)
throw|;
block|}
name|int
name|numQueues
init|=
name|priorityLevels
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"FairCallQueue is in use with "
operator|+
name|numQueues
operator|+
literal|" queues with total capacity of "
operator|+
name|capacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|queues
operator|=
operator|new
name|ArrayList
argument_list|<
name|BlockingQueue
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|(
name|numQueues
argument_list|)
expr_stmt|;
name|this
operator|.
name|overflowedCalls
operator|=
operator|new
name|ArrayList
argument_list|<
name|AtomicLong
argument_list|>
argument_list|(
name|numQueues
argument_list|)
expr_stmt|;
name|int
name|queueCapacity
init|=
name|capacity
operator|/
name|numQueues
decl_stmt|;
name|int
name|capacityForFirstQueue
init|=
name|queueCapacity
operator|+
operator|(
name|capacity
operator|%
name|numQueues
operator|)
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
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|queues
operator|.
name|add
argument_list|(
operator|new
name|LinkedBlockingQueue
argument_list|<
name|E
argument_list|>
argument_list|(
name|capacityForFirstQueue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|queues
operator|.
name|add
argument_list|(
operator|new
name|LinkedBlockingQueue
argument_list|<
name|E
argument_list|>
argument_list|(
name|queueCapacity
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|overflowedCalls
operator|.
name|add
argument_list|(
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|multiplexer
operator|=
operator|new
name|WeightedRoundRobinMultiplexer
argument_list|(
name|numQueues
argument_list|,
name|ns
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Make this the active source of metrics
name|MetricsProxy
name|mp
init|=
name|MetricsProxy
operator|.
name|getInstance
argument_list|(
name|ns
argument_list|)
decl_stmt|;
name|mp
operator|.
name|setDelegate
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns an element first non-empty queue equal to the priority returned    * by the multiplexer or scans from highest to lowest priority queue.    *    * Caller must always acquire a semaphore permit before invoking.    *    * @return the first non-empty queue with less priority, or null if    * everything was empty    */
DECL|method|removeNextElement ()
specifier|private
name|E
name|removeNextElement
parameter_list|()
block|{
name|int
name|priority
init|=
name|multiplexer
operator|.
name|getAndAdvanceCurrentIndex
argument_list|()
decl_stmt|;
name|E
name|e
init|=
name|queues
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|poll
argument_list|()
decl_stmt|;
comment|// a semaphore permit has been acquired, so an element MUST be extracted
comment|// or the semaphore and queued elements will go out of sync.  loop to
comment|// avoid race condition if elements are added behind the current position,
comment|// awakening other threads that poll the elements ahead of our position.
while|while
condition|(
name|e
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|e
operator|==
literal|null
operator|&&
name|idx
operator|<
name|queues
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|e
operator|=
name|queues
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|e
return|;
block|}
comment|/* AbstractQueue and BlockingQueue methods */
comment|/**    * Add, put, and offer follow the same pattern:    * 1. Get the assigned priorityLevel from the call by scheduler    * 2. Get the nth sub-queue matching this priorityLevel    * 3. delegate the call to this sub-queue.    *    * But differ in how they handle overflow:    * - Add will move on to the next queue, throw on last queue overflow    * - Put will move on to the next queue, block on last queue overflow    * - Offer does not attempt other queues on overflow    */
annotation|@
name|Override
DECL|method|add (E e)
specifier|public
name|boolean
name|add
parameter_list|(
name|E
name|e
parameter_list|)
block|{
specifier|final
name|int
name|priorityLevel
init|=
name|e
operator|.
name|getPriorityLevel
argument_list|()
decl_stmt|;
comment|// try offering to all queues.
if|if
condition|(
operator|!
name|offerQueues
argument_list|(
name|priorityLevel
argument_list|,
name|e
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// only disconnect the lowest priority users that overflow the queue.
throw|throw
operator|(
name|priorityLevel
operator|==
name|queues
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
condition|?
name|CallQueueOverflowException
operator|.
name|DISCONNECT
else|:
name|CallQueueOverflowException
operator|.
name|KEEPALIVE
throw|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|put (E e)
specifier|public
name|void
name|put
parameter_list|(
name|E
name|e
parameter_list|)
throws|throws
name|InterruptedException
block|{
specifier|final
name|int
name|priorityLevel
init|=
name|e
operator|.
name|getPriorityLevel
argument_list|()
decl_stmt|;
comment|// try offering to all but last queue, put on last.
if|if
condition|(
operator|!
name|offerQueues
argument_list|(
name|priorityLevel
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|putQueue
argument_list|(
name|queues
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Put the element in a queue of a specific priority.    * @param priority - queue priority    * @param e - element to add    */
annotation|@
name|VisibleForTesting
DECL|method|putQueue (int priority, E e)
name|void
name|putQueue
parameter_list|(
name|int
name|priority
parameter_list|,
name|E
name|e
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|queues
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|put
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|signalNotEmpty
argument_list|()
expr_stmt|;
block|}
comment|/**    * Offer the element to queue of a specific priority.    * @param priority - queue priority    * @param e - element to add    * @return boolean if added to the given queue    */
annotation|@
name|VisibleForTesting
DECL|method|offerQueue (int priority, E e)
name|boolean
name|offerQueue
parameter_list|(
name|int
name|priority
parameter_list|,
name|E
name|e
parameter_list|)
block|{
name|boolean
name|ret
init|=
name|queues
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|offer
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
condition|)
block|{
name|signalNotEmpty
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Offer the element to queue of the given or lower priority.    * @param priority - starting queue priority    * @param e - element to add    * @param includeLast - whether to attempt last queue    * @return boolean if added to a queue    */
DECL|method|offerQueues (int priority, E e, boolean includeLast)
specifier|private
name|boolean
name|offerQueues
parameter_list|(
name|int
name|priority
parameter_list|,
name|E
name|e
parameter_list|,
name|boolean
name|includeLast
parameter_list|)
block|{
name|int
name|lastPriority
init|=
name|queues
operator|.
name|size
argument_list|()
operator|-
operator|(
name|includeLast
condition|?
literal|1
else|:
literal|2
operator|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|priority
init|;
name|i
operator|<=
name|lastPriority
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|offerQueue
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Update stats
name|overflowedCalls
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|offer (E e, long timeout, TimeUnit unit)
specifier|public
name|boolean
name|offer
parameter_list|(
name|E
name|e
parameter_list|,
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|int
name|priorityLevel
init|=
name|e
operator|.
name|getPriorityLevel
argument_list|()
decl_stmt|;
name|BlockingQueue
argument_list|<
name|E
argument_list|>
name|q
init|=
name|this
operator|.
name|queues
operator|.
name|get
argument_list|(
name|priorityLevel
argument_list|)
decl_stmt|;
name|boolean
name|ret
init|=
name|q
operator|.
name|offer
argument_list|(
name|e
argument_list|,
name|timeout
argument_list|,
name|unit
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
condition|)
block|{
name|signalNotEmpty
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|offer (E e)
specifier|public
name|boolean
name|offer
parameter_list|(
name|E
name|e
parameter_list|)
block|{
name|int
name|priorityLevel
init|=
name|e
operator|.
name|getPriorityLevel
argument_list|()
decl_stmt|;
name|BlockingQueue
argument_list|<
name|E
argument_list|>
name|q
init|=
name|this
operator|.
name|queues
operator|.
name|get
argument_list|(
name|priorityLevel
argument_list|)
decl_stmt|;
name|boolean
name|ret
init|=
name|q
operator|.
name|offer
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
condition|)
block|{
name|signalNotEmpty
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|take ()
specifier|public
name|E
name|take
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|semaphore
operator|.
name|acquire
argument_list|()
expr_stmt|;
return|return
name|removeNextElement
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|poll (long timeout, TimeUnit unit)
specifier|public
name|E
name|poll
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|semaphore
operator|.
name|tryAcquire
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
condition|?
name|removeNextElement
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**    * poll() provides no strict consistency: it is possible for poll to return    * null even though an element is in the queue.    */
annotation|@
name|Override
DECL|method|poll ()
specifier|public
name|E
name|poll
parameter_list|()
block|{
return|return
name|semaphore
operator|.
name|tryAcquire
argument_list|()
condition|?
name|removeNextElement
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**    * Peek, like poll, provides no strict consistency.    */
annotation|@
name|Override
DECL|method|peek ()
specifier|public
name|E
name|peek
parameter_list|()
block|{
name|E
name|e
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|e
operator|==
literal|null
operator|&&
name|i
operator|<
name|queues
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|e
operator|=
name|queues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|peek
argument_list|()
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
comment|/**    * Size returns the sum of all sub-queue sizes, so it may be greater than    * capacity.    * Note: size provides no strict consistency, and should not be used to    * control queue IO.    */
annotation|@
name|Override
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|semaphore
operator|.
name|availablePermits
argument_list|()
return|;
block|}
comment|/**    * Iterator is not implemented, as it is not needed.    */
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
comment|/**    * drainTo defers to each sub-queue. Note that draining from a FairCallQueue    * to another FairCallQueue will likely fail, since the incoming calls    * may be scheduled differently in the new FairCallQueue. Nonetheless this    * method is provided for completeness.    */
annotation|@
name|Override
DECL|method|drainTo (Collection<? super E> c, int maxElements)
specifier|public
name|int
name|drainTo
parameter_list|(
name|Collection
argument_list|<
name|?
super|super
name|E
argument_list|>
name|c
parameter_list|,
name|int
name|maxElements
parameter_list|)
block|{
comment|// initially take all permits to stop consumers from modifying queues
comment|// while draining.  will restore any excess when done draining.
specifier|final
name|int
name|permits
init|=
name|semaphore
operator|.
name|drainPermits
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numElements
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxElements
argument_list|,
name|permits
argument_list|)
decl_stmt|;
name|int
name|numRemaining
init|=
name|numElements
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|numRemaining
operator|>
literal|0
operator|&&
name|i
operator|<
name|queues
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|numRemaining
operator|-=
name|queues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|drainTo
argument_list|(
name|c
argument_list|,
name|numRemaining
argument_list|)
expr_stmt|;
block|}
name|int
name|drained
init|=
name|numElements
operator|-
name|numRemaining
decl_stmt|;
if|if
condition|(
name|permits
operator|>
name|drained
condition|)
block|{
comment|// restore unused permits.
name|semaphore
operator|.
name|release
argument_list|(
name|permits
operator|-
name|drained
argument_list|)
expr_stmt|;
block|}
return|return
name|drained
return|;
block|}
annotation|@
name|Override
DECL|method|drainTo (Collection<? super E> c)
specifier|public
name|int
name|drainTo
parameter_list|(
name|Collection
argument_list|<
name|?
super|super
name|E
argument_list|>
name|c
parameter_list|)
block|{
return|return
name|drainTo
argument_list|(
name|c
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Returns maximum remaining capacity. This does not reflect how much you can    * ideally fit in this FairCallQueue, as that would depend on the scheduler's    * decisions.    */
annotation|@
name|Override
DECL|method|remainingCapacity ()
specifier|public
name|int
name|remainingCapacity
parameter_list|()
block|{
name|int
name|sum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BlockingQueue
argument_list|<
name|E
argument_list|>
name|q
range|:
name|this
operator|.
name|queues
control|)
block|{
name|sum
operator|+=
name|q
operator|.
name|remainingCapacity
argument_list|()
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
comment|/**    * MetricsProxy is a singleton because we may init multiple    * FairCallQueues, but the metrics system cannot unregister beans cleanly.    */
DECL|class|MetricsProxy
specifier|private
specifier|static
specifier|final
class|class
name|MetricsProxy
implements|implements
name|FairCallQueueMXBean
block|{
comment|// One singleton per namespace
DECL|field|INSTANCES
specifier|private
specifier|static
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|MetricsProxy
argument_list|>
name|INSTANCES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MetricsProxy
argument_list|>
argument_list|()
decl_stmt|;
comment|// Weakref for delegate, so we don't retain it forever if it can be GC'd
DECL|field|delegate
specifier|private
name|WeakReference
argument_list|<
name|FairCallQueue
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
argument_list|>
name|delegate
decl_stmt|;
comment|// Keep track of how many objects we registered
DECL|field|revisionNumber
specifier|private
name|int
name|revisionNumber
init|=
literal|0
decl_stmt|;
DECL|method|MetricsProxy (String namespace)
specifier|private
name|MetricsProxy
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
name|MBeans
operator|.
name|register
argument_list|(
name|namespace
argument_list|,
literal|"FairCallQueue"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|getInstance (String namespace)
specifier|public
specifier|static
specifier|synchronized
name|MetricsProxy
name|getInstance
parameter_list|(
name|String
name|namespace
parameter_list|)
block|{
name|MetricsProxy
name|mp
init|=
name|INSTANCES
operator|.
name|get
argument_list|(
name|namespace
argument_list|)
decl_stmt|;
if|if
condition|(
name|mp
operator|==
literal|null
condition|)
block|{
comment|// We must create one
name|mp
operator|=
operator|new
name|MetricsProxy
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
name|INSTANCES
operator|.
name|put
argument_list|(
name|namespace
argument_list|,
name|mp
argument_list|)
expr_stmt|;
block|}
return|return
name|mp
return|;
block|}
DECL|method|setDelegate (FairCallQueue<? extends Schedulable> obj)
specifier|public
name|void
name|setDelegate
parameter_list|(
name|FairCallQueue
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|obj
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
operator|new
name|WeakReference
argument_list|<
name|FairCallQueue
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
argument_list|>
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|this
operator|.
name|revisionNumber
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQueueSizes ()
specifier|public
name|int
index|[]
name|getQueueSizes
parameter_list|()
block|{
name|FairCallQueue
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|obj
init|=
name|this
operator|.
name|delegate
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|int
index|[]
block|{}
return|;
block|}
return|return
name|obj
operator|.
name|getQueueSizes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOverflowedCalls ()
specifier|public
name|long
index|[]
name|getOverflowedCalls
parameter_list|()
block|{
name|FairCallQueue
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|obj
init|=
name|this
operator|.
name|delegate
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|long
index|[]
block|{}
return|;
block|}
return|return
name|obj
operator|.
name|getOverflowedCalls
argument_list|()
return|;
block|}
DECL|method|getRevision ()
annotation|@
name|Override
specifier|public
name|int
name|getRevision
parameter_list|()
block|{
return|return
name|revisionNumber
return|;
block|}
block|}
comment|// FairCallQueueMXBean
DECL|method|getQueueSizes ()
specifier|public
name|int
index|[]
name|getQueueSizes
parameter_list|()
block|{
name|int
name|numQueues
init|=
name|queues
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
index|[]
name|sizes
init|=
operator|new
name|int
index|[
name|numQueues
index|]
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
name|sizes
index|[
name|i
index|]
operator|=
name|queues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|sizes
return|;
block|}
DECL|method|getOverflowedCalls ()
specifier|public
name|long
index|[]
name|getOverflowedCalls
parameter_list|()
block|{
name|int
name|numQueues
init|=
name|queues
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
index|[]
name|calls
init|=
operator|new
name|long
index|[
name|numQueues
index|]
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
name|calls
index|[
name|i
index|]
operator|=
name|overflowedCalls
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
name|calls
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setMultiplexer (RpcMultiplexer newMux)
specifier|public
name|void
name|setMultiplexer
parameter_list|(
name|RpcMultiplexer
name|newMux
parameter_list|)
block|{
name|this
operator|.
name|multiplexer
operator|=
name|newMux
expr_stmt|;
block|}
block|}
end_class

end_unit

