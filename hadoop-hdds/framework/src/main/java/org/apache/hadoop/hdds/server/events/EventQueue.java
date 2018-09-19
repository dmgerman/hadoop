begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.server.events
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
operator|.
name|events
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|List
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * Simple async event processing utility.  *<p>  * Event queue handles a collection of event handlers and routes the incoming  * events to one (or more) event handler.  */
end_comment

begin_class
DECL|class|EventQueue
specifier|public
class|class
name|EventQueue
implements|implements
name|EventPublisher
implements|,
name|AutoCloseable
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
name|EventQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EXECUTOR_NAME_SEPARATOR
specifier|private
specifier|static
specifier|final
name|String
name|EXECUTOR_NAME_SEPARATOR
init|=
literal|"For"
decl_stmt|;
DECL|field|executors
specifier|private
specifier|final
name|Map
argument_list|<
name|Event
argument_list|,
name|Map
argument_list|<
name|EventExecutor
argument_list|,
name|List
argument_list|<
name|EventHandler
argument_list|>
argument_list|>
argument_list|>
name|executors
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queuedCount
specifier|private
specifier|final
name|AtomicLong
name|queuedCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|eventCount
specifier|private
specifier|final
name|AtomicLong
name|eventCount
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|isRunning
specifier|private
name|boolean
name|isRunning
init|=
literal|true
decl_stmt|;
DECL|method|addHandler ( EVENT_TYPE event, EventHandler<PAYLOAD> handler)
specifier|public
parameter_list|<
name|PAYLOAD
parameter_list|,
name|EVENT_TYPE
extends|extends
name|Event
argument_list|<
name|PAYLOAD
argument_list|>
parameter_list|>
name|void
name|addHandler
parameter_list|(
name|EVENT_TYPE
name|event
parameter_list|,
name|EventHandler
argument_list|<
name|PAYLOAD
argument_list|>
name|handler
parameter_list|)
block|{
name|this
operator|.
name|addHandler
argument_list|(
name|event
argument_list|,
name|handler
argument_list|,
name|generateHandlerName
argument_list|(
name|handler
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add new handler to the event queue.    *<p>    * By default a separated single thread executor will be dedicated to    * deliver the events to the registered event handler.    *    * @param event        Triggering event.    * @param handler      Handler of event (will be called from a separated    *                     thread)    * @param handlerName  The name of handler (should be unique together with    *                     the event name)    * @param<PAYLOAD>    The type of the event payload.    * @param<EVENT_TYPE> The type of the event identifier.    */
DECL|method|addHandler ( EVENT_TYPE event, EventHandler<PAYLOAD> handler, String handlerName)
specifier|public
parameter_list|<
name|PAYLOAD
parameter_list|,
name|EVENT_TYPE
extends|extends
name|Event
argument_list|<
name|PAYLOAD
argument_list|>
parameter_list|>
name|void
name|addHandler
parameter_list|(
name|EVENT_TYPE
name|event
parameter_list|,
name|EventHandler
argument_list|<
name|PAYLOAD
argument_list|>
name|handler
parameter_list|,
name|String
name|handlerName
parameter_list|)
block|{
name|validateEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|handler
argument_list|,
literal|"Handler name should not be null."
argument_list|)
expr_stmt|;
name|String
name|executorName
init|=
name|StringUtils
operator|.
name|camelize
argument_list|(
name|event
operator|.
name|getName
argument_list|()
argument_list|)
operator|+
name|EXECUTOR_NAME_SEPARATOR
operator|+
name|handlerName
decl_stmt|;
name|this
operator|.
name|addHandler
argument_list|(
name|event
argument_list|,
operator|new
name|SingleThreadExecutor
argument_list|<>
argument_list|(
name|executorName
argument_list|)
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
DECL|method|validateEvent (EVENT_TYPE event)
specifier|private
parameter_list|<
name|EVENT_TYPE
extends|extends
name|Event
argument_list|<
name|?
argument_list|>
parameter_list|>
name|void
name|validateEvent
parameter_list|(
name|EVENT_TYPE
name|event
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|event
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
name|EXECUTOR_NAME_SEPARATOR
argument_list|)
argument_list|,
literal|"Event name should not contain "
operator|+
name|EXECUTOR_NAME_SEPARATOR
operator|+
literal|" string."
argument_list|)
expr_stmt|;
block|}
DECL|method|generateHandlerName (EventHandler<PAYLOAD> handler)
specifier|private
parameter_list|<
name|PAYLOAD
parameter_list|>
name|String
name|generateHandlerName
parameter_list|(
name|EventHandler
argument_list|<
name|PAYLOAD
argument_list|>
name|handler
parameter_list|)
block|{
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|handler
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|handler
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|handler
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
comment|/**    * Add event handler with custom executor.    *    * @param event        Triggering event.    * @param executor     The executor imlementation to deliver events from a    *                     separated threads. Please keep in your mind that    *                     registering metrics is the responsibility of the    *                     caller.    * @param handler      Handler of event (will be called from a separated    *                     thread)    * @param<PAYLOAD>    The type of the event payload.    * @param<EVENT_TYPE> The type of the event identifier.    */
DECL|method|addHandler ( EVENT_TYPE event, EventExecutor<PAYLOAD> executor, EventHandler<PAYLOAD> handler)
specifier|public
parameter_list|<
name|PAYLOAD
parameter_list|,
name|EVENT_TYPE
extends|extends
name|Event
argument_list|<
name|PAYLOAD
argument_list|>
parameter_list|>
name|void
name|addHandler
parameter_list|(
name|EVENT_TYPE
name|event
parameter_list|,
name|EventExecutor
argument_list|<
name|PAYLOAD
argument_list|>
name|executor
parameter_list|,
name|EventHandler
argument_list|<
name|PAYLOAD
argument_list|>
name|handler
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Not adding handler for {}, EventQueue is not running"
argument_list|,
name|event
argument_list|)
expr_stmt|;
return|return;
block|}
name|validateEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|executors
operator|.
name|putIfAbsent
argument_list|(
name|event
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|executors
operator|.
name|get
argument_list|(
name|event
argument_list|)
operator|.
name|putIfAbsent
argument_list|(
name|executor
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|executors
operator|.
name|get
argument_list|(
name|event
argument_list|)
operator|.
name|get
argument_list|(
name|executor
argument_list|)
operator|.
name|add
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
comment|/**    * Route an event with payload to the right listener(s).    *    * @param event   The event identifier    * @param payload The payload of the event.    * @throws IllegalArgumentException If there is no EventHandler for    *                                  the specific event.    */
DECL|method|fireEvent ( EVENT_TYPE event, PAYLOAD payload)
specifier|public
parameter_list|<
name|PAYLOAD
parameter_list|,
name|EVENT_TYPE
extends|extends
name|Event
argument_list|<
name|PAYLOAD
argument_list|>
parameter_list|>
name|void
name|fireEvent
parameter_list|(
name|EVENT_TYPE
name|event
parameter_list|,
name|PAYLOAD
name|payload
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Processing of {} is skipped, EventQueue is not running"
argument_list|,
name|event
argument_list|)
expr_stmt|;
return|return;
block|}
name|Map
argument_list|<
name|EventExecutor
argument_list|,
name|List
argument_list|<
name|EventHandler
argument_list|>
argument_list|>
name|eventExecutorListMap
init|=
name|this
operator|.
name|executors
operator|.
name|get
argument_list|(
name|event
argument_list|)
decl_stmt|;
name|eventCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|eventExecutorListMap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|EventExecutor
argument_list|,
name|List
argument_list|<
name|EventHandler
argument_list|>
argument_list|>
name|executorAndHandlers
range|:
name|eventExecutorListMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|EventHandler
name|handler
range|:
name|executorAndHandlers
operator|.
name|getValue
argument_list|()
control|)
block|{
name|queuedCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Delivering event {} to executor/handler {}: {}"
argument_list|,
name|event
operator|.
name|getName
argument_list|()
argument_list|,
name|executorAndHandlers
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|payload
argument_list|)
expr_stmt|;
block|}
name|executorAndHandlers
operator|.
name|getKey
argument_list|()
operator|.
name|onMessage
argument_list|(
name|handler
argument_list|,
name|payload
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No event handler registered for event "
operator|+
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This is just for unit testing, don't use it for production code.    *<p>    * It waits for all messages to be processed. If one event handler invokes an    * other one, the later one also should be finished.    *<p>    * Long counter overflow is not handled, therefore it's safe only for unit    * testing.    *<p>    * This method is just eventually consistent. In some cases it could return    * even if there are new messages in some of the handler. But in a simple    * case (one message) it will return only if the message is processed and    * all the dependent messages (messages which are sent by current handlers)    * are processed.    *    * @param timeout Timeout in seconds to wait for the processing.    */
annotation|@
name|VisibleForTesting
DECL|method|processAll (long timeout)
specifier|public
name|void
name|processAll
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|long
name|currentTime
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Processing of event skipped. EventQueue is not running"
argument_list|)
expr_stmt|;
return|return;
block|}
name|long
name|processed
init|=
literal|0
decl_stmt|;
name|Stream
argument_list|<
name|EventExecutor
argument_list|>
name|allExecutor
init|=
name|this
operator|.
name|executors
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|flatMap
argument_list|(
name|handlerMap
lambda|->
name|handlerMap
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|allIdle
init|=
name|allExecutor
operator|.
name|allMatch
argument_list|(
name|executor
lambda|->
name|executor
operator|.
name|queuedEvents
argument_list|()
operator|==
name|executor
operator|.
name|successfulEvents
argument_list|()
operator|+
name|executor
operator|.
name|failedEvents
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|allIdle
condition|)
block|{
return|return;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|>
name|currentTime
operator|+
name|timeout
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Messages are not processed in the given timeframe. Queued: "
operator|+
name|queuedCount
operator|.
name|get
argument_list|()
operator|+
literal|" Processed: "
operator|+
name|processed
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|isRunning
operator|=
literal|false
expr_stmt|;
name|Set
argument_list|<
name|EventExecutor
argument_list|>
name|allExecutors
init|=
name|this
operator|.
name|executors
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|flatMap
argument_list|(
name|handlerMap
lambda|->
name|handlerMap
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|allExecutors
operator|.
name|forEach
argument_list|(
name|executor
lambda|->
block|{
try|try
block|{
name|executor
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't close the executor "
operator|+
name|executor
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

