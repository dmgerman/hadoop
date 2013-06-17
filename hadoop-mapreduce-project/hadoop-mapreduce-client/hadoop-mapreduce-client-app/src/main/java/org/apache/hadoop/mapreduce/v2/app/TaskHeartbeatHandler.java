begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptDiagnosticsUpdateEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEventType
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
name|event
operator|.
name|EventHandler
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
name|yarn
operator|.
name|util
operator|.
name|Clock
import|;
end_import

begin_comment
comment|/**  * This class keeps track of tasks that have already been launched. It  * determines if a task is alive and running or marks a task as dead if it does  * not hear from it for a long time.  *   */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|TaskHeartbeatHandler
specifier|public
class|class
name|TaskHeartbeatHandler
extends|extends
name|AbstractService
block|{
DECL|class|ReportTime
specifier|private
specifier|static
class|class
name|ReportTime
block|{
DECL|field|lastProgress
specifier|private
name|long
name|lastProgress
decl_stmt|;
DECL|method|ReportTime (long time)
specifier|public
name|ReportTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|setLastProgress
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
DECL|method|setLastProgress (long time)
specifier|public
specifier|synchronized
name|void
name|setLastProgress
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|lastProgress
operator|=
name|time
expr_stmt|;
block|}
DECL|method|getLastProgress ()
specifier|public
specifier|synchronized
name|long
name|getLastProgress
parameter_list|()
block|{
return|return
name|lastProgress
return|;
block|}
block|}
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
name|TaskHeartbeatHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//thread which runs periodically to see the last time since a heartbeat is
comment|//received from a task.
DECL|field|lostTaskCheckerThread
specifier|private
name|Thread
name|lostTaskCheckerThread
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|volatile
name|boolean
name|stopped
decl_stmt|;
DECL|field|taskTimeOut
specifier|private
name|int
name|taskTimeOut
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 5 mins
DECL|field|taskTimeOutCheckInterval
specifier|private
name|int
name|taskTimeOutCheckInterval
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
comment|// 30 seconds.
DECL|field|eventHandler
specifier|private
specifier|final
name|EventHandler
name|eventHandler
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
DECL|field|runningAttempts
specifier|private
name|ConcurrentMap
argument_list|<
name|TaskAttemptId
argument_list|,
name|ReportTime
argument_list|>
name|runningAttempts
decl_stmt|;
DECL|method|TaskHeartbeatHandler (EventHandler eventHandler, Clock clock, int numThreads)
specifier|public
name|TaskHeartbeatHandler
parameter_list|(
name|EventHandler
name|eventHandler
parameter_list|,
name|Clock
name|clock
parameter_list|,
name|int
name|numThreads
parameter_list|)
block|{
name|super
argument_list|(
literal|"TaskHeartbeatHandler"
argument_list|)
expr_stmt|;
name|this
operator|.
name|eventHandler
operator|=
name|eventHandler
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|runningAttempts
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|TaskAttemptId
argument_list|,
name|ReportTime
argument_list|>
argument_list|(
literal|16
argument_list|,
literal|0.75f
argument_list|,
name|numThreads
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|taskTimeOut
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|TASK_TIMEOUT
argument_list|,
literal|5
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|taskTimeOutCheckInterval
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|TASK_TIMEOUT_CHECK_INTERVAL_MS
argument_list|,
literal|30
operator|*
literal|1000
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
name|lostTaskCheckerThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|PingChecker
argument_list|()
argument_list|)
expr_stmt|;
name|lostTaskCheckerThread
operator|.
name|setName
argument_list|(
literal|"TaskHeartbeatHandler PingChecker"
argument_list|)
expr_stmt|;
name|lostTaskCheckerThread
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
name|lostTaskCheckerThread
operator|!=
literal|null
condition|)
block|{
name|lostTaskCheckerThread
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
DECL|method|progressing (TaskAttemptId attemptID)
specifier|public
name|void
name|progressing
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
comment|//only put for the registered attempts
comment|//TODO throw an exception if the task isn't registered.
name|ReportTime
name|time
init|=
name|runningAttempts
operator|.
name|get
argument_list|(
name|attemptID
argument_list|)
decl_stmt|;
if|if
condition|(
name|time
operator|!=
literal|null
condition|)
block|{
name|time
operator|.
name|setLastProgress
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|register (TaskAttemptId attemptID)
specifier|public
name|void
name|register
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
name|runningAttempts
operator|.
name|put
argument_list|(
name|attemptID
argument_list|,
operator|new
name|ReportTime
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|unregister (TaskAttemptId attemptID)
specifier|public
name|void
name|unregister
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
name|runningAttempts
operator|.
name|remove
argument_list|(
name|attemptID
argument_list|)
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
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|TaskAttemptId
argument_list|,
name|ReportTime
argument_list|>
argument_list|>
name|iterator
init|=
name|runningAttempts
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
name|TaskAttemptId
argument_list|,
name|ReportTime
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|boolean
name|taskTimedOut
init|=
operator|(
name|taskTimeOut
operator|>
literal|0
operator|)
operator|&&
operator|(
name|currentTime
operator|>
operator|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getLastProgress
argument_list|()
operator|+
name|taskTimeOut
operator|)
operator|)
decl_stmt|;
if|if
condition|(
name|taskTimedOut
condition|)
block|{
comment|// task is lost, remove from the list and raise lost event
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|eventHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptDiagnosticsUpdateEvent
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"AttemptID:"
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
name|taskTimeOut
operator|/
literal|1000
operator|+
literal|" secs"
argument_list|)
argument_list|)
expr_stmt|;
name|eventHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_TIMED_OUT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|taskTimeOutCheckInterval
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
literal|"TaskHeartbeatHandler thread interrupted"
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

