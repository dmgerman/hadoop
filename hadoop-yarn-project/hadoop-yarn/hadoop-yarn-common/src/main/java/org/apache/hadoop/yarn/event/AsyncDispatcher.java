begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.event
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|event
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
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|service
operator|.
name|AbstractService
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
name|ShutdownHookManager
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
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

begin_comment
comment|/**  * Dispatches {@link Event}s in a separate thread. Currently only single thread  * does that. Potentially there could be multiple channels for each event type  * class and a thread pool can be used to dispatch the events.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|AsyncDispatcher
specifier|public
class|class
name|AsyncDispatcher
extends|extends
name|AbstractService
implements|implements
name|Dispatcher
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AsyncDispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|eventQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|Event
argument_list|>
name|eventQueue
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|volatile
name|boolean
name|stopped
init|=
literal|false
decl_stmt|;
comment|// Configuration flag for enabling/disabling draining dispatcher's events on
comment|// stop functionality.
DECL|field|drainEventsOnStop
specifier|private
specifier|volatile
name|boolean
name|drainEventsOnStop
init|=
literal|false
decl_stmt|;
comment|// Indicates all the remaining dispatcher's events on stop have been drained
comment|// and processed.
DECL|field|drained
specifier|private
specifier|volatile
name|boolean
name|drained
init|=
literal|true
decl_stmt|;
DECL|field|waitForDrained
specifier|private
name|Object
name|waitForDrained
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|// For drainEventsOnStop enabled only, block newly coming events into the
comment|// queue while stopping.
DECL|field|blockNewEvents
specifier|private
specifier|volatile
name|boolean
name|blockNewEvents
init|=
literal|false
decl_stmt|;
DECL|field|handlerInstance
specifier|private
name|EventHandler
name|handlerInstance
init|=
literal|null
decl_stmt|;
DECL|field|eventHandlingThread
specifier|private
name|Thread
name|eventHandlingThread
decl_stmt|;
DECL|field|eventDispatchers
specifier|protected
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
argument_list|,
name|EventHandler
argument_list|>
name|eventDispatchers
decl_stmt|;
DECL|field|exitOnDispatchException
specifier|private
name|boolean
name|exitOnDispatchException
decl_stmt|;
DECL|method|AsyncDispatcher ()
specifier|public
name|AsyncDispatcher
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Event
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AsyncDispatcher (BlockingQueue<Event> eventQueue)
specifier|public
name|AsyncDispatcher
parameter_list|(
name|BlockingQueue
argument_list|<
name|Event
argument_list|>
name|eventQueue
parameter_list|)
block|{
name|super
argument_list|(
literal|"Dispatcher"
argument_list|)
expr_stmt|;
name|this
operator|.
name|eventQueue
operator|=
name|eventQueue
expr_stmt|;
name|this
operator|.
name|eventDispatchers
operator|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
argument_list|,
name|EventHandler
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|createThread ()
name|Runnable
name|createThread
parameter_list|()
block|{
return|return
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
while|while
condition|(
operator|!
name|stopped
operator|&&
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
name|drained
operator|=
name|eventQueue
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
comment|// blockNewEvents is only set when dispatcher is draining to stop,
comment|// adding this check is to avoid the overhead of acquiring the lock
comment|// and calling notify every time in the normal run of the loop.
if|if
condition|(
name|blockNewEvents
condition|)
block|{
synchronized|synchronized
init|(
name|waitForDrained
init|)
block|{
if|if
condition|(
name|drained
condition|)
block|{
name|waitForDrained
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|Event
name|event
decl_stmt|;
try|try
block|{
name|event
operator|=
name|eventQueue
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
if|if
condition|(
operator|!
name|stopped
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"AsyncDispatcher thread interrupted"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|event
operator|!=
literal|null
condition|)
block|{
name|dispatch
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|exitOnDispatchException
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|Dispatcher
operator|.
name|DISPATCHER_EXIT_ON_ERROR_KEY
argument_list|,
name|Dispatcher
operator|.
name|DEFAULT_DISPATCHER_EXIT_ON_ERROR
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|//start all the components
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|eventHandlingThread
operator|=
operator|new
name|Thread
argument_list|(
name|createThread
argument_list|()
argument_list|)
expr_stmt|;
name|eventHandlingThread
operator|.
name|setName
argument_list|(
literal|"AsyncDispatcher event handler"
argument_list|)
expr_stmt|;
name|eventHandlingThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|setDrainEventsOnStop ()
specifier|public
name|void
name|setDrainEventsOnStop
parameter_list|()
block|{
name|drainEventsOnStop
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|drainEventsOnStop
condition|)
block|{
name|blockNewEvents
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"AsyncDispatcher is draining to stop, igonring any new events."
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|waitForDrained
init|)
block|{
while|while
condition|(
operator|!
name|drained
operator|&&
name|eventHandlingThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|waitForDrained
operator|.
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for AsyncDispatcher to drain. Thread state is :"
operator|+
name|eventHandlingThread
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|stopped
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|eventHandlingThread
operator|!=
literal|null
condition|)
block|{
name|eventHandlingThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|eventHandlingThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Interrupted Exception while stopping"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
comment|// stop all the components
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|dispatch (Event event)
specifier|protected
name|void
name|dispatch
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
comment|//all events go thru this loop
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
literal|"Dispatching the event "
operator|+
name|event
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
name|type
init|=
name|event
operator|.
name|getType
argument_list|()
operator|.
name|getDeclaringClass
argument_list|()
decl_stmt|;
try|try
block|{
name|EventHandler
name|handler
init|=
name|eventDispatchers
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"No handler for registered for "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|//TODO Maybe log the state of the queue
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Error in dispatcher thread"
argument_list|,
name|t
argument_list|)
expr_stmt|;
comment|// If serviceStop is called, we should exit this thread gracefully.
if|if
condition|(
name|exitOnDispatchException
operator|&&
operator|(
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|isShutdownInProgress
argument_list|()
operator|)
operator|==
literal|false
operator|&&
name|stopped
operator|==
literal|false
condition|)
block|{
name|Thread
name|shutDownThread
init|=
operator|new
name|Thread
argument_list|(
name|createShutDownThread
argument_list|()
argument_list|)
decl_stmt|;
name|shutDownThread
operator|.
name|setName
argument_list|(
literal|"AsyncDispatcher ShutDown handler"
argument_list|)
expr_stmt|;
name|shutDownThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|register (Class<? extends Enum> eventType, EventHandler handler)
specifier|public
name|void
name|register
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
name|eventType
parameter_list|,
name|EventHandler
name|handler
parameter_list|)
block|{
comment|/* check to see if we have a listener registered */
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|registeredHandler
init|=
operator|(
name|EventHandler
argument_list|<
name|Event
argument_list|>
operator|)
name|eventDispatchers
operator|.
name|get
argument_list|(
name|eventType
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registering "
operator|+
name|eventType
operator|+
literal|" for "
operator|+
name|handler
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|registeredHandler
operator|==
literal|null
condition|)
block|{
name|eventDispatchers
operator|.
name|put
argument_list|(
name|eventType
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|registeredHandler
operator|instanceof
name|MultiListenerHandler
operator|)
condition|)
block|{
comment|/* for multiple listeners of an event add the multiple listener handler */
name|MultiListenerHandler
name|multiHandler
init|=
operator|new
name|MultiListenerHandler
argument_list|()
decl_stmt|;
name|multiHandler
operator|.
name|addHandler
argument_list|(
name|registeredHandler
argument_list|)
expr_stmt|;
name|multiHandler
operator|.
name|addHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|eventDispatchers
operator|.
name|put
argument_list|(
name|eventType
argument_list|,
name|multiHandler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|/* already a multilistener, just add to it */
name|MultiListenerHandler
name|multiHandler
init|=
operator|(
name|MultiListenerHandler
operator|)
name|registeredHandler
decl_stmt|;
name|multiHandler
operator|.
name|addHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEventHandler ()
specifier|public
name|EventHandler
name|getEventHandler
parameter_list|()
block|{
if|if
condition|(
name|handlerInstance
operator|==
literal|null
condition|)
block|{
name|handlerInstance
operator|=
operator|new
name|GenericEventHandler
argument_list|()
expr_stmt|;
block|}
return|return
name|handlerInstance
return|;
block|}
DECL|class|GenericEventHandler
class|class
name|GenericEventHandler
implements|implements
name|EventHandler
argument_list|<
name|Event
argument_list|>
block|{
DECL|method|handle (Event event)
specifier|public
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
if|if
condition|(
name|blockNewEvents
condition|)
block|{
return|return;
block|}
name|drained
operator|=
literal|false
expr_stmt|;
comment|/* all this method does is enqueue all the events onto the queue */
name|int
name|qSize
init|=
name|eventQueue
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|qSize
operator|!=
literal|0
operator|&&
name|qSize
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Size of event-queue is "
operator|+
name|qSize
argument_list|)
expr_stmt|;
block|}
name|int
name|remCapacity
init|=
name|eventQueue
operator|.
name|remainingCapacity
argument_list|()
decl_stmt|;
if|if
condition|(
name|remCapacity
operator|<
literal|1000
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Very low remaining capacity in the event-queue: "
operator|+
name|remCapacity
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|eventQueue
operator|.
name|put
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|stopped
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"AsyncDispatcher thread interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
empty_stmt|;
block|}
comment|/**    * Multiplexing an event. Sending it to different handlers that    * are interested in the event.    * @param<T> the type of event these multiple handlers are interested in.    */
DECL|class|MultiListenerHandler
specifier|static
class|class
name|MultiListenerHandler
implements|implements
name|EventHandler
argument_list|<
name|Event
argument_list|>
block|{
DECL|field|listofHandlers
name|List
argument_list|<
name|EventHandler
argument_list|<
name|Event
argument_list|>
argument_list|>
name|listofHandlers
decl_stmt|;
DECL|method|MultiListenerHandler ()
specifier|public
name|MultiListenerHandler
parameter_list|()
block|{
name|listofHandlers
operator|=
operator|new
name|ArrayList
argument_list|<
name|EventHandler
argument_list|<
name|Event
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (Event event)
specifier|public
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
for|for
control|(
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|handler
range|:
name|listofHandlers
control|)
block|{
name|handler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addHandler (EventHandler<Event> handler)
name|void
name|addHandler
parameter_list|(
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|handler
parameter_list|)
block|{
name|listofHandlers
operator|.
name|add
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createShutDownThread ()
name|Runnable
name|createShutDownThread
parameter_list|()
block|{
return|return
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Exiting, bbye.."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|isDrained ()
specifier|protected
name|boolean
name|isDrained
parameter_list|()
block|{
return|return
name|this
operator|.
name|drained
return|;
block|}
block|}
end_class

end_unit

