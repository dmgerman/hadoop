begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

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
name|Iterator
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_comment
comment|/**  * A simple liveliness monitor with which clients can register, trust the  * component to monitor liveliness, get a call-back on expiry and then finally  * unregister.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|AbstractLivelinessMonitor
specifier|public
specifier|abstract
class|class
name|AbstractLivelinessMonitor
parameter_list|<
name|O
parameter_list|>
extends|extends
name|AbstractService
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
name|AbstractLivelinessMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//thread which runs periodically to see the last time since a heartbeat is
comment|//received.
DECL|field|checkerThread
specifier|private
name|Thread
name|checkerThread
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|volatile
name|boolean
name|stopped
decl_stmt|;
DECL|field|DEFAULT_EXPIRE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_EXPIRE
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|//5 mins
DECL|field|expireInterval
specifier|private
name|long
name|expireInterval
init|=
name|DEFAULT_EXPIRE
decl_stmt|;
DECL|field|monitorInterval
specifier|private
name|long
name|monitorInterval
init|=
name|expireInterval
operator|/
literal|3
decl_stmt|;
DECL|field|resetTimerOnStart
specifier|private
specifier|volatile
name|boolean
name|resetTimerOnStart
init|=
literal|true
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
DECL|field|running
specifier|private
name|Map
argument_list|<
name|O
argument_list|,
name|Long
argument_list|>
name|running
init|=
operator|new
name|HashMap
argument_list|<
name|O
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|AbstractLivelinessMonitor (String name, Clock clock)
specifier|public
name|AbstractLivelinessMonitor
parameter_list|(
name|String
name|name
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
block|}
DECL|method|AbstractLivelinessMonitor (String name)
specifier|public
name|AbstractLivelinessMonitor
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|new
name|MonotonicClock
argument_list|()
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
assert|assert
operator|!
name|stopped
operator|:
literal|"starting when already stopped"
assert|;
name|resetTimer
argument_list|()
expr_stmt|;
name|checkerThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|PingChecker
argument_list|()
argument_list|)
expr_stmt|;
name|checkerThread
operator|.
name|setName
argument_list|(
literal|"Ping Checker"
argument_list|)
expr_stmt|;
name|checkerThread
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
name|stopped
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|checkerThread
operator|!=
literal|null
condition|)
block|{
name|checkerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|expire (O ob)
specifier|protected
specifier|abstract
name|void
name|expire
parameter_list|(
name|O
name|ob
parameter_list|)
function_decl|;
DECL|method|setExpireInterval (int expireInterval)
specifier|protected
name|void
name|setExpireInterval
parameter_list|(
name|int
name|expireInterval
parameter_list|)
block|{
name|this
operator|.
name|expireInterval
operator|=
name|expireInterval
expr_stmt|;
block|}
DECL|method|getExpireInterval (O o)
specifier|protected
name|long
name|getExpireInterval
parameter_list|(
name|O
name|o
parameter_list|)
block|{
comment|// by-default return for all the registered object interval.
return|return
name|this
operator|.
name|expireInterval
return|;
block|}
DECL|method|setMonitorInterval (long monitorInterval)
specifier|protected
name|void
name|setMonitorInterval
parameter_list|(
name|long
name|monitorInterval
parameter_list|)
block|{
name|this
operator|.
name|monitorInterval
operator|=
name|monitorInterval
expr_stmt|;
block|}
DECL|method|receivedPing (O ob)
specifier|public
specifier|synchronized
name|void
name|receivedPing
parameter_list|(
name|O
name|ob
parameter_list|)
block|{
comment|//only put for the registered objects
if|if
condition|(
name|running
operator|.
name|containsKey
argument_list|(
name|ob
argument_list|)
condition|)
block|{
name|running
operator|.
name|put
argument_list|(
name|ob
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|register (O ob)
specifier|public
specifier|synchronized
name|void
name|register
parameter_list|(
name|O
name|ob
parameter_list|)
block|{
name|register
argument_list|(
name|ob
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|register (O ob, long expireTime)
specifier|public
specifier|synchronized
name|void
name|register
parameter_list|(
name|O
name|ob
parameter_list|,
name|long
name|expireTime
parameter_list|)
block|{
name|running
operator|.
name|put
argument_list|(
name|ob
argument_list|,
name|expireTime
argument_list|)
expr_stmt|;
block|}
DECL|method|unregister (O ob)
specifier|public
specifier|synchronized
name|void
name|unregister
parameter_list|(
name|O
name|ob
parameter_list|)
block|{
name|running
operator|.
name|remove
argument_list|(
name|ob
argument_list|)
expr_stmt|;
block|}
DECL|method|resetTimer ()
specifier|public
specifier|synchronized
name|void
name|resetTimer
parameter_list|()
block|{
if|if
condition|(
name|resetTimerOnStart
condition|)
block|{
name|long
name|time
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
for|for
control|(
name|O
name|ob
range|:
name|running
operator|.
name|keySet
argument_list|()
control|)
block|{
name|running
operator|.
name|put
argument_list|(
name|ob
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setResetTimeOnStart (boolean resetTimeOnStart)
specifier|protected
name|void
name|setResetTimeOnStart
parameter_list|(
name|boolean
name|resetTimeOnStart
parameter_list|)
block|{
name|this
operator|.
name|resetTimerOnStart
operator|=
name|resetTimeOnStart
expr_stmt|;
block|}
DECL|class|PingChecker
specifier|private
class|class
name|PingChecker
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
synchronized|synchronized
init|(
name|AbstractLivelinessMonitor
operator|.
name|this
init|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|O
argument_list|,
name|Long
argument_list|>
argument_list|>
name|iterator
init|=
name|running
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// avoid calculating current time everytime in loop
name|long
name|currentTime
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|O
argument_list|,
name|Long
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|O
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|long
name|interval
init|=
name|getExpireInterval
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentTime
operator|>
name|entry
operator|.
name|getValue
argument_list|()
operator|+
name|interval
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|expire
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Expired:"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" Timed out after "
operator|+
name|interval
operator|/
literal|1000
operator|+
literal|" secs"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|monitorInterval
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
name|getName
argument_list|()
operator|+
literal|" thread interrupted"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

