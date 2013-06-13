begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.commit
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
operator|.
name|commit
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|JobID
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
name|OutputCommitter
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
name|TypeConverter
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
name|JobId
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
name|AppContext
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
name|JobAbortCompletedEvent
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
name|JobCommitCompletedEvent
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
name|JobCommitFailedEvent
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
name|JobSetupCompletedEvent
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
name|JobSetupFailedEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|RMHeartbeatHandler
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
name|util
operator|.
name|MRApps
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_class
DECL|class|CommitterEventHandler
specifier|public
class|class
name|CommitterEventHandler
extends|extends
name|AbstractService
implements|implements
name|EventHandler
argument_list|<
name|CommitterEvent
argument_list|>
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
name|CommitterEventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|AppContext
name|context
decl_stmt|;
DECL|field|committer
specifier|private
specifier|final
name|OutputCommitter
name|committer
decl_stmt|;
DECL|field|rmHeartbeatHandler
specifier|private
specifier|final
name|RMHeartbeatHandler
name|rmHeartbeatHandler
decl_stmt|;
DECL|field|launcherPool
specifier|private
name|ThreadPoolExecutor
name|launcherPool
decl_stmt|;
DECL|field|eventHandlingThread
specifier|private
name|Thread
name|eventHandlingThread
decl_stmt|;
DECL|field|eventQueue
specifier|private
name|BlockingQueue
argument_list|<
name|CommitterEvent
argument_list|>
name|eventQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|CommitterEvent
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|final
name|AtomicBoolean
name|stopped
decl_stmt|;
DECL|field|jobCommitThread
specifier|private
name|Thread
name|jobCommitThread
init|=
literal|null
decl_stmt|;
DECL|field|commitThreadCancelTimeoutMs
specifier|private
name|int
name|commitThreadCancelTimeoutMs
decl_stmt|;
DECL|field|commitWindowMs
specifier|private
name|long
name|commitWindowMs
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|startCommitFile
specifier|private
name|Path
name|startCommitFile
decl_stmt|;
DECL|field|endCommitSuccessFile
specifier|private
name|Path
name|endCommitSuccessFile
decl_stmt|;
DECL|field|endCommitFailureFile
specifier|private
name|Path
name|endCommitFailureFile
decl_stmt|;
DECL|method|CommitterEventHandler (AppContext context, OutputCommitter committer, RMHeartbeatHandler rmHeartbeatHandler)
specifier|public
name|CommitterEventHandler
parameter_list|(
name|AppContext
name|context
parameter_list|,
name|OutputCommitter
name|committer
parameter_list|,
name|RMHeartbeatHandler
name|rmHeartbeatHandler
parameter_list|)
block|{
name|super
argument_list|(
literal|"CommitterEventHandler"
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|committer
operator|=
name|committer
expr_stmt|;
name|this
operator|.
name|rmHeartbeatHandler
operator|=
name|rmHeartbeatHandler
expr_stmt|;
name|this
operator|.
name|stopped
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
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
name|commitThreadCancelTimeoutMs
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_COMMITTER_CANCEL_TIMEOUT_MS
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_AM_COMMITTER_CANCEL_TIMEOUT_MS
argument_list|)
expr_stmt|;
name|commitWindowMs
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_COMMIT_WINDOW_MS
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_AM_COMMIT_WINDOW_MS
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|JobID
name|id
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|context
operator|.
name|getApplicationID
argument_list|()
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|startCommitFile
operator|=
name|MRApps
operator|.
name|getStartJobCommitFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
expr_stmt|;
name|endCommitSuccessFile
operator|=
name|MRApps
operator|.
name|getEndJobCommitSuccessFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
expr_stmt|;
name|endCommitFailureFile
operator|=
name|MRApps
operator|.
name|getEndJobCommitFailureFile
argument_list|(
name|conf
argument_list|,
name|user
argument_list|,
name|jobId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|ThreadFactory
name|tf
init|=
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setNameFormat
argument_list|(
literal|"CommitterEvent Processor #%d"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|launcherPool
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|5
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|tf
argument_list|)
expr_stmt|;
name|eventHandlingThread
operator|=
operator|new
name|Thread
argument_list|(
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
name|CommitterEvent
name|event
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|stopped
operator|.
name|get
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
if|if
condition|(
operator|!
name|stopped
operator|.
name|get
argument_list|()
condition|)
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
block|}
return|return;
block|}
comment|// the events from the queue are handled in parallel
comment|// using a thread pool
name|launcherPool
operator|.
name|execute
argument_list|(
operator|new
name|EventProcessor
argument_list|(
name|event
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|eventHandlingThread
operator|.
name|setName
argument_list|(
literal|"CommitterEvent Handler"
argument_list|)
expr_stmt|;
name|eventHandlingThread
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
DECL|method|handle (CommitterEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|CommitterEvent
name|event
parameter_list|)
block|{
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
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
name|stopped
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
comment|// return if already stopped
return|return;
block|}
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
block|}
if|if
condition|(
name|launcherPool
operator|!=
literal|null
condition|)
block|{
name|launcherPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|jobCommitStarted ()
specifier|private
specifier|synchronized
name|void
name|jobCommitStarted
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|jobCommitThread
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Commit while another commit thread active: "
operator|+
name|jobCommitThread
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|jobCommitThread
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
expr_stmt|;
block|}
DECL|method|jobCommitEnded ()
specifier|private
specifier|synchronized
name|void
name|jobCommitEnded
parameter_list|()
block|{
if|if
condition|(
name|jobCommitThread
operator|==
name|Thread
operator|.
name|currentThread
argument_list|()
condition|)
block|{
name|jobCommitThread
operator|=
literal|null
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|cancelJobCommit ()
specifier|private
specifier|synchronized
name|void
name|cancelJobCommit
parameter_list|()
block|{
name|Thread
name|threadCommitting
init|=
name|jobCommitThread
decl_stmt|;
if|if
condition|(
name|threadCommitting
operator|!=
literal|null
operator|&&
name|threadCommitting
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Canceling commit"
argument_list|)
expr_stmt|;
name|threadCommitting
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// wait up to configured timeout for commit thread to finish
name|long
name|now
init|=
name|context
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|long
name|timeoutTimestamp
init|=
name|now
operator|+
name|commitThreadCancelTimeoutMs
decl_stmt|;
try|try
block|{
while|while
condition|(
name|jobCommitThread
operator|==
name|threadCommitting
operator|&&
name|now
operator|>
name|timeoutTimestamp
condition|)
block|{
name|wait
argument_list|(
name|now
operator|-
name|timeoutTimestamp
argument_list|)
expr_stmt|;
name|now
operator|=
name|context
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
block|}
block|}
DECL|class|EventProcessor
specifier|private
class|class
name|EventProcessor
implements|implements
name|Runnable
block|{
DECL|field|event
specifier|private
name|CommitterEvent
name|event
decl_stmt|;
DECL|method|EventProcessor (CommitterEvent event)
name|EventProcessor
parameter_list|(
name|CommitterEvent
name|event
parameter_list|)
block|{
name|this
operator|.
name|event
operator|=
name|event
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing the event "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|JOB_SETUP
case|:
name|handleJobSetup
argument_list|(
operator|(
name|CommitterJobSetupEvent
operator|)
name|event
argument_list|)
expr_stmt|;
break|break;
case|case
name|JOB_COMMIT
case|:
name|handleJobCommit
argument_list|(
operator|(
name|CommitterJobCommitEvent
operator|)
name|event
argument_list|)
expr_stmt|;
break|break;
case|case
name|JOB_ABORT
case|:
name|handleJobAbort
argument_list|(
operator|(
name|CommitterJobAbortEvent
operator|)
name|event
argument_list|)
expr_stmt|;
break|break;
case|case
name|TASK_ABORT
case|:
name|handleTaskAbort
argument_list|(
operator|(
name|CommitterTaskAbortEvent
operator|)
name|event
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Unexpected committer event "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|handleJobSetup (CommitterJobSetupEvent event)
specifier|protected
name|void
name|handleJobSetup
parameter_list|(
name|CommitterJobSetupEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|committer
operator|.
name|setupJob
argument_list|(
name|event
operator|.
name|getJobContext
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobSetupCompletedEvent
argument_list|(
name|event
operator|.
name|getJobID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Job setup failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobSetupFailedEvent
argument_list|(
name|event
operator|.
name|getJobID
argument_list|()
argument_list|,
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|touchz (Path p)
specifier|private
name|void
name|touchz
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|,
literal|false
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|handleJobCommit (CommitterJobCommitEvent event)
specifier|protected
name|void
name|handleJobCommit
parameter_list|(
name|CommitterJobCommitEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|touchz
argument_list|(
name|startCommitFile
argument_list|)
expr_stmt|;
name|jobCommitStarted
argument_list|()
expr_stmt|;
name|waitForValidCommitWindow
argument_list|()
expr_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|event
operator|.
name|getJobContext
argument_list|()
argument_list|)
expr_stmt|;
name|touchz
argument_list|(
name|endCommitSuccessFile
argument_list|)
expr_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobCommitCompletedEvent
argument_list|(
name|event
operator|.
name|getJobID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|touchz
argument_list|(
name|endCommitFailureFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"could not create failure file."
argument_list|,
name|e2
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not commit job"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobCommitFailedEvent
argument_list|(
name|event
operator|.
name|getJobID
argument_list|()
argument_list|,
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|jobCommitEnded
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|handleJobAbort (CommitterJobAbortEvent event)
specifier|protected
name|void
name|handleJobAbort
parameter_list|(
name|CommitterJobAbortEvent
name|event
parameter_list|)
block|{
name|cancelJobCommit
argument_list|()
expr_stmt|;
try|try
block|{
name|committer
operator|.
name|abortJob
argument_list|(
name|event
operator|.
name|getJobContext
argument_list|()
argument_list|,
name|event
operator|.
name|getFinalState
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not abort job"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobAbortCompletedEvent
argument_list|(
name|event
operator|.
name|getJobID
argument_list|()
argument_list|,
name|event
operator|.
name|getFinalState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|handleTaskAbort (CommitterTaskAbortEvent event)
specifier|protected
name|void
name|handleTaskAbort
parameter_list|(
name|CommitterTaskAbortEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|committer
operator|.
name|abortTask
argument_list|(
name|event
operator|.
name|getAttemptContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Task cleanup failed for attempt "
operator|+
name|event
operator|.
name|getAttemptID
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|event
operator|.
name|getAttemptID
argument_list|()
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_CLEANUP_DONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForValidCommitWindow ()
specifier|private
specifier|synchronized
name|void
name|waitForValidCommitWindow
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|long
name|lastHeartbeatTime
init|=
name|rmHeartbeatHandler
operator|.
name|getLastHeartbeatTime
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|context
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
while|while
condition|(
name|now
operator|-
name|lastHeartbeatTime
operator|>
name|commitWindowMs
condition|)
block|{
name|rmHeartbeatHandler
operator|.
name|runOnNextHeartbeat
argument_list|(
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
synchronized|synchronized
init|(
name|EventProcessor
operator|.
name|this
init|)
block|{
name|EventProcessor
operator|.
name|this
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|wait
argument_list|()
expr_stmt|;
name|lastHeartbeatTime
operator|=
name|rmHeartbeatHandler
operator|.
name|getLastHeartbeatTime
argument_list|()
expr_stmt|;
name|now
operator|=
name|context
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

