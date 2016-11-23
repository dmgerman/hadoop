begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|DrainDispatcher
specifier|public
class|class
name|DrainDispatcher
extends|extends
name|AsyncDispatcher
block|{
DECL|field|drained
specifier|private
specifier|volatile
name|boolean
name|drained
init|=
literal|false
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|Event
argument_list|>
name|queue
decl_stmt|;
DECL|field|mutex
specifier|private
specifier|final
name|Object
name|mutex
decl_stmt|;
DECL|method|DrainDispatcher ()
specifier|public
name|DrainDispatcher
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
DECL|method|DrainDispatcher (BlockingQueue<Event> eventQueue)
specifier|public
name|DrainDispatcher
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
name|eventQueue
argument_list|)
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|eventQueue
expr_stmt|;
name|this
operator|.
name|mutex
operator|=
name|this
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|Dispatcher
operator|.
name|DISPATCHER_EXIT_ON_ERROR_KEY
argument_list|,
literal|false
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
comment|/**    *  Wait till event thread enters WAITING state (i.e. waiting for new events).    */
DECL|method|waitForEventThreadToWait ()
specifier|public
name|void
name|waitForEventThreadToWait
parameter_list|()
block|{
while|while
condition|(
operator|!
name|isEventThreadWaiting
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Busy loop waiting for all queued events to drain.    */
DECL|method|await ()
specifier|public
name|void
name|await
parameter_list|()
block|{
while|while
condition|(
operator|!
name|isDrained
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|isStopped
argument_list|()
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
synchronized|synchronized
init|(
name|mutex
init|)
block|{
comment|// !drained if dispatch queued new events on this dispatcher
name|drained
operator|=
name|queue
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
name|Event
name|event
decl_stmt|;
try|try
block|{
name|event
operator|=
name|queue
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
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|getEventHandler ()
specifier|public
name|EventHandler
name|getEventHandler
parameter_list|()
block|{
specifier|final
name|EventHandler
name|actual
init|=
name|super
operator|.
name|getEventHandler
argument_list|()
decl_stmt|;
return|return
operator|new
name|EventHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|actual
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|drained
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|isDrained ()
specifier|protected
name|boolean
name|isDrained
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|drained
return|;
block|}
block|}
block|}
end_class

end_unit

