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
name|org
operator|.
name|slf4j
operator|.
name|Marker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|MarkerFactory
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
name|LinkedBlockingDeque
import|;
end_import

begin_comment
comment|/**  * This is a specialized EventHandler to be used by Services that are expected  * handle a large number of events efficiently by ensuring that the caller  * thread is not blocked. Events are immediately stored in a BlockingQueue and  * a separate dedicated Thread consumes events from the queue and handles  * appropriately  * @param<T> Type of Event  */
end_comment

begin_class
DECL|class|EventDispatcher
specifier|public
class|class
name|EventDispatcher
parameter_list|<
name|T
extends|extends
name|Event
parameter_list|>
extends|extends
name|AbstractService
implements|implements
name|EventHandler
argument_list|<
name|T
argument_list|>
block|{
DECL|field|handler
specifier|private
specifier|final
name|EventHandler
argument_list|<
name|T
argument_list|>
name|handler
decl_stmt|;
DECL|field|eventQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|T
argument_list|>
name|eventQueue
init|=
operator|new
name|LinkedBlockingDeque
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|eventProcessor
specifier|private
specifier|final
name|Thread
name|eventProcessor
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|volatile
name|boolean
name|stopped
init|=
literal|false
decl_stmt|;
DECL|field|shouldExitOnError
specifier|private
name|boolean
name|shouldExitOnError
init|=
literal|true
decl_stmt|;
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
name|EventDispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FATAL
specifier|private
specifier|static
specifier|final
name|Marker
name|FATAL
init|=
name|MarkerFactory
operator|.
name|getMarker
argument_list|(
literal|"FATAL"
argument_list|)
decl_stmt|;
DECL|class|EventProcessor
specifier|private
specifier|final
class|class
name|EventProcessor
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|T
name|event
decl_stmt|;
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
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Returning, interrupted : "
operator|+
name|e
argument_list|)
expr_stmt|;
return|return;
comment|// TODO: Kill RM.
block|}
try|try
block|{
name|handler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// An error occurred, but we are shutting down anyway.
comment|// If it was an InterruptedException, the very act of
comment|// shutdown could have caused it and is probably harmless.
if|if
condition|(
name|stopped
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception during shutdown: "
argument_list|,
name|t
argument_list|)
expr_stmt|;
break|break;
block|}
name|LOG
operator|.
name|error
argument_list|(
name|FATAL
argument_list|,
literal|"Error in handling event type "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" to the Event Dispatcher"
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|shouldExitOnError
operator|&&
operator|!
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|isShutdownInProgress
argument_list|()
condition|)
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
block|}
block|}
block|}
DECL|method|EventDispatcher (EventHandler<T> handler, String name)
specifier|public
name|EventDispatcher
parameter_list|(
name|EventHandler
argument_list|<
name|T
argument_list|>
name|handler
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|eventProcessor
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|EventProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|eventProcessor
operator|.
name|setName
argument_list|(
name|getName
argument_list|()
operator|+
literal|":Event Processor"
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
name|this
operator|.
name|eventProcessor
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
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
name|this
operator|.
name|stopped
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|eventProcessor
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|eventProcessor
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (T event)
specifier|public
name|void
name|handle
parameter_list|(
name|T
name|event
parameter_list|)
block|{
try|try
block|{
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
literal|"Size of "
operator|+
name|getName
argument_list|()
operator|+
literal|" event-queue is "
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
name|info
argument_list|(
literal|"Very low remaining capacity on "
operator|+
name|getName
argument_list|()
operator|+
literal|""
operator|+
literal|"event queue: "
operator|+
name|remCapacity
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Interrupted. Trying to exit gracefully."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|disableExitOnError ()
specifier|public
name|void
name|disableExitOnError
parameter_list|()
block|{
name|shouldExitOnError
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

