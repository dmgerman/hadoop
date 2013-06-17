begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.speculate
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
name|speculate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|AtomicInteger
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
name|api
operator|.
name|records
operator|.
name|TaskAttemptState
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
name|TaskId
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
name|TaskType
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
name|Job
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
name|Task
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
name|TaskAttempt
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
name|TaskAttemptStatusUpdateEvent
operator|.
name|TaskAttemptStatus
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
name|TaskEvent
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
name|TaskEventType
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
name|util
operator|.
name|Clock
import|;
end_import

begin_class
DECL|class|DefaultSpeculator
specifier|public
class|class
name|DefaultSpeculator
extends|extends
name|AbstractService
implements|implements
name|Speculator
block|{
DECL|field|ON_SCHEDULE
specifier|private
specifier|static
specifier|final
name|long
name|ON_SCHEDULE
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|ALREADY_SPECULATING
specifier|private
specifier|static
specifier|final
name|long
name|ALREADY_SPECULATING
init|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|1
decl_stmt|;
DECL|field|TOO_NEW
specifier|private
specifier|static
specifier|final
name|long
name|TOO_NEW
init|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|2
decl_stmt|;
DECL|field|PROGRESS_IS_GOOD
specifier|private
specifier|static
specifier|final
name|long
name|PROGRESS_IS_GOOD
init|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|3
decl_stmt|;
DECL|field|NOT_RUNNING
specifier|private
specifier|static
specifier|final
name|long
name|NOT_RUNNING
init|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|4
decl_stmt|;
DECL|field|TOO_LATE_TO_SPECULATE
specifier|private
specifier|static
specifier|final
name|long
name|TOO_LATE_TO_SPECULATE
init|=
name|Long
operator|.
name|MIN_VALUE
operator|+
literal|5
decl_stmt|;
DECL|field|SOONEST_RETRY_AFTER_NO_SPECULATE
specifier|private
specifier|static
specifier|final
name|long
name|SOONEST_RETRY_AFTER_NO_SPECULATE
init|=
literal|1000L
operator|*
literal|1L
decl_stmt|;
DECL|field|SOONEST_RETRY_AFTER_SPECULATE
specifier|private
specifier|static
specifier|final
name|long
name|SOONEST_RETRY_AFTER_SPECULATE
init|=
literal|1000L
operator|*
literal|15L
decl_stmt|;
DECL|field|PROPORTION_RUNNING_TASKS_SPECULATABLE
specifier|private
specifier|static
specifier|final
name|double
name|PROPORTION_RUNNING_TASKS_SPECULATABLE
init|=
literal|0.1
decl_stmt|;
DECL|field|PROPORTION_TOTAL_TASKS_SPECULATABLE
specifier|private
specifier|static
specifier|final
name|double
name|PROPORTION_TOTAL_TASKS_SPECULATABLE
init|=
literal|0.01
decl_stmt|;
DECL|field|MINIMUM_ALLOWED_SPECULATIVE_TASKS
specifier|private
specifier|static
specifier|final
name|int
name|MINIMUM_ALLOWED_SPECULATIVE_TASKS
init|=
literal|10
decl_stmt|;
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
name|DefaultSpeculator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|runningTasks
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|TaskId
argument_list|,
name|Boolean
argument_list|>
name|runningTasks
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|TaskId
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|pendingSpeculations
specifier|private
specifier|final
name|Map
argument_list|<
name|Task
argument_list|,
name|AtomicBoolean
argument_list|>
name|pendingSpeculations
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Task
argument_list|,
name|AtomicBoolean
argument_list|>
argument_list|()
decl_stmt|;
comment|// These are the current needs, not the initial needs.  For each job, these
comment|//  record the number of attempts that exist and that are actively
comment|//  waiting for a container [as opposed to running or finished]
DECL|field|mapContainerNeeds
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|JobId
argument_list|,
name|AtomicInteger
argument_list|>
name|mapContainerNeeds
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|JobId
argument_list|,
name|AtomicInteger
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reduceContainerNeeds
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|JobId
argument_list|,
name|AtomicInteger
argument_list|>
name|reduceContainerNeeds
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|JobId
argument_list|,
name|AtomicInteger
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|mayHaveSpeculated
specifier|private
specifier|final
name|Set
argument_list|<
name|TaskId
argument_list|>
name|mayHaveSpeculated
init|=
operator|new
name|HashSet
argument_list|<
name|TaskId
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|context
specifier|private
name|AppContext
name|context
decl_stmt|;
DECL|field|speculationBackgroundThread
specifier|private
name|Thread
name|speculationBackgroundThread
init|=
literal|null
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|volatile
name|boolean
name|stopped
init|=
literal|false
decl_stmt|;
DECL|field|eventQueue
specifier|private
name|BlockingQueue
argument_list|<
name|SpeculatorEvent
argument_list|>
name|eventQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|SpeculatorEvent
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|estimator
specifier|private
name|TaskRuntimeEstimator
name|estimator
decl_stmt|;
DECL|field|scanControl
specifier|private
name|BlockingQueue
argument_list|<
name|Object
argument_list|>
name|scanControl
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
DECL|field|eventHandler
specifier|private
specifier|final
name|EventHandler
argument_list|<
name|TaskEvent
argument_list|>
name|eventHandler
decl_stmt|;
DECL|method|DefaultSpeculator (Configuration conf, AppContext context)
specifier|public
name|DefaultSpeculator
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|context
argument_list|,
name|context
operator|.
name|getClock
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DefaultSpeculator (Configuration conf, AppContext context, Clock clock)
specifier|public
name|DefaultSpeculator
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|context
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|context
argument_list|,
name|getEstimator
argument_list|(
name|conf
argument_list|,
name|context
argument_list|)
argument_list|,
name|clock
argument_list|)
expr_stmt|;
block|}
DECL|method|getEstimator (Configuration conf, AppContext context)
specifier|static
specifier|private
name|TaskRuntimeEstimator
name|getEstimator
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
name|TaskRuntimeEstimator
name|estimator
decl_stmt|;
try|try
block|{
comment|// "yarn.mapreduce.job.task.runtime.estimator.class"
name|Class
argument_list|<
name|?
extends|extends
name|TaskRuntimeEstimator
argument_list|>
name|estimatorClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_TASK_ESTIMATOR
argument_list|,
name|LegacyTaskRuntimeEstimator
operator|.
name|class
argument_list|,
name|TaskRuntimeEstimator
operator|.
name|class
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|TaskRuntimeEstimator
argument_list|>
name|estimatorConstructor
init|=
name|estimatorClass
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
name|estimator
operator|=
name|estimatorConstructor
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|estimator
operator|.
name|contextualize
argument_list|(
name|conf
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't make a speculation runtime extimator"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't make a speculation runtime extimator"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't make a speculation runtime extimator"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Can't make a speculation runtime extimator"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
return|return
name|estimator
return|;
block|}
comment|// This constructor is designed to be called by other constructors.
comment|//  However, it's public because we do use it in the test cases.
comment|// Normally we figure out our own estimator.
DECL|method|DefaultSpeculator (Configuration conf, AppContext context, TaskRuntimeEstimator estimator, Clock clock)
specifier|public
name|DefaultSpeculator
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|context
parameter_list|,
name|TaskRuntimeEstimator
name|estimator
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|super
argument_list|(
name|DefaultSpeculator
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|estimator
operator|=
name|estimator
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|eventHandler
operator|=
name|context
operator|.
name|getEventHandler
argument_list|()
expr_stmt|;
block|}
comment|/*   *************************************************************    */
comment|// This is the task-mongering that creates the two new threads -- one for
comment|//  processing events from the event queue and one for periodically
comment|//  looking for speculation opportunities
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
name|Runnable
name|speculationBackgroundCore
init|=
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
name|long
name|backgroundRunStartTime
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|speculations
init|=
name|computeSpeculations
argument_list|()
decl_stmt|;
name|long
name|mininumRecomp
init|=
name|speculations
operator|>
literal|0
condition|?
name|SOONEST_RETRY_AFTER_SPECULATE
else|:
name|SOONEST_RETRY_AFTER_NO_SPECULATE
decl_stmt|;
name|long
name|wait
init|=
name|Math
operator|.
name|max
argument_list|(
name|mininumRecomp
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|backgroundRunStartTime
argument_list|)
decl_stmt|;
if|if
condition|(
name|speculations
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"We launched "
operator|+
name|speculations
operator|+
literal|" speculations.  Sleeping "
operator|+
name|wait
operator|+
literal|" milliseconds."
argument_list|)
expr_stmt|;
block|}
name|Object
name|pollResult
init|=
name|scanControl
operator|.
name|poll
argument_list|(
name|wait
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
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
name|error
argument_list|(
literal|"Background thread returning, interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
block|}
block|}
decl_stmt|;
name|speculationBackgroundThread
operator|=
operator|new
name|Thread
argument_list|(
name|speculationBackgroundCore
argument_list|,
literal|"DefaultSpeculator background processing"
argument_list|)
expr_stmt|;
name|speculationBackgroundThread
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
comment|// this could be called before background thread is established
if|if
condition|(
name|speculationBackgroundThread
operator|!=
literal|null
condition|)
block|{
name|speculationBackgroundThread
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
annotation|@
name|Override
DECL|method|handleAttempt (TaskAttemptStatus status)
specifier|public
name|void
name|handleAttempt
parameter_list|(
name|TaskAttemptStatus
name|status
parameter_list|)
block|{
name|long
name|timestamp
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|statusUpdate
argument_list|(
name|status
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
block|}
comment|// This section is not part of the Speculator interface; it's used only for
comment|//  testing
DECL|method|eventQueueEmpty ()
specifier|public
name|boolean
name|eventQueueEmpty
parameter_list|()
block|{
return|return
name|eventQueue
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|// This interface is intended to be used only for test cases.
DECL|method|scanForSpeculations ()
specifier|public
name|void
name|scanForSpeculations
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"We got asked to run a debug speculation scan."
argument_list|)
expr_stmt|;
comment|// debug
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"We got asked to run a debug speculation scan."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"There are "
operator|+
name|scanControl
operator|.
name|size
argument_list|()
operator|+
literal|" events stacked already."
argument_list|)
expr_stmt|;
name|scanControl
operator|.
name|add
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
comment|/*   *************************************************************    */
comment|// This section contains the code that gets run for a SpeculatorEvent
DECL|method|containerNeed (TaskId taskID)
specifier|private
name|AtomicInteger
name|containerNeed
parameter_list|(
name|TaskId
name|taskID
parameter_list|)
block|{
name|JobId
name|jobID
init|=
name|taskID
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|TaskType
name|taskType
init|=
name|taskID
operator|.
name|getTaskType
argument_list|()
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|JobId
argument_list|,
name|AtomicInteger
argument_list|>
name|relevantMap
init|=
name|taskType
operator|==
name|TaskType
operator|.
name|MAP
condition|?
name|mapContainerNeeds
else|:
name|reduceContainerNeeds
decl_stmt|;
name|AtomicInteger
name|result
init|=
name|relevantMap
operator|.
name|get
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|relevantMap
operator|.
name|putIfAbsent
argument_list|(
name|jobID
argument_list|,
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|relevantMap
operator|.
name|get
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|processSpeculatorEvent (SpeculatorEvent event)
specifier|private
specifier|synchronized
name|void
name|processSpeculatorEvent
parameter_list|(
name|SpeculatorEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|ATTEMPT_STATUS_UPDATE
case|:
name|statusUpdate
argument_list|(
name|event
operator|.
name|getReportedStatus
argument_list|()
argument_list|,
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|TASK_CONTAINER_NEED_UPDATE
case|:
block|{
name|AtomicInteger
name|need
init|=
name|containerNeed
argument_list|(
name|event
operator|.
name|getTaskID
argument_list|()
argument_list|)
decl_stmt|;
name|need
operator|.
name|addAndGet
argument_list|(
name|event
operator|.
name|containersNeededChange
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|ATTEMPT_START
case|:
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"ATTEMPT_START "
operator|+
name|event
operator|.
name|getTaskID
argument_list|()
argument_list|)
expr_stmt|;
name|estimator
operator|.
name|enrollAttempt
argument_list|(
name|event
operator|.
name|getReportedStatus
argument_list|()
argument_list|,
name|event
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|JOB_CREATE
case|:
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"JOB_CREATE "
operator|+
name|event
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|estimator
operator|.
name|contextualize
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**    * Absorbs one TaskAttemptStatus    *    * @param reportedStatus the status report that we got from a task attempt    *        that we want to fold into the speculation data for this job    * @param timestamp the time this status corresponds to.  This matters    *        because statuses contain progress.    */
DECL|method|statusUpdate (TaskAttemptStatus reportedStatus, long timestamp)
specifier|protected
name|void
name|statusUpdate
parameter_list|(
name|TaskAttemptStatus
name|reportedStatus
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|String
name|stateString
init|=
name|reportedStatus
operator|.
name|taskState
operator|.
name|toString
argument_list|()
decl_stmt|;
name|TaskAttemptId
name|attemptID
init|=
name|reportedStatus
operator|.
name|id
decl_stmt|;
name|TaskId
name|taskID
init|=
name|attemptID
operator|.
name|getTaskId
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|context
operator|.
name|getJob
argument_list|(
name|taskID
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Task
name|task
init|=
name|job
operator|.
name|getTask
argument_list|(
name|taskID
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|estimator
operator|.
name|updateAttempt
argument_list|(
name|reportedStatus
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
comment|// If the task is already known to be speculation-bait, don't do anything
if|if
condition|(
name|pendingSpeculations
operator|.
name|get
argument_list|(
name|task
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|pendingSpeculations
operator|.
name|get
argument_list|(
name|task
argument_list|)
operator|.
name|get
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
if|if
condition|(
name|stateString
operator|.
name|equals
argument_list|(
name|TaskAttemptState
operator|.
name|RUNNING
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|runningTasks
operator|.
name|putIfAbsent
argument_list|(
name|taskID
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|runningTasks
operator|.
name|remove
argument_list|(
name|taskID
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*   *************************************************************    */
comment|// This is the code section that runs periodically and adds speculations for
comment|//  those jobs that need them.
comment|// This can return a few magic values for tasks that shouldn't speculate:
comment|//  returns ON_SCHEDULE if thresholdRuntime(taskID) says that we should not
comment|//     considering speculating this task
comment|//  returns ALREADY_SPECULATING if that is true.  This has priority.
comment|//  returns TOO_NEW if our companion task hasn't gotten any information
comment|//  returns PROGRESS_IS_GOOD if the task is sailing through
comment|//  returns NOT_RUNNING if the task is not running
comment|//
comment|// All of these values are negative.  Any value that should be allowed to
comment|//  speculate is 0 or positive.
DECL|method|speculationValue (TaskId taskID, long now)
specifier|private
name|long
name|speculationValue
parameter_list|(
name|TaskId
name|taskID
parameter_list|,
name|long
name|now
parameter_list|)
block|{
name|Job
name|job
init|=
name|context
operator|.
name|getJob
argument_list|(
name|taskID
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|Task
name|task
init|=
name|job
operator|.
name|getTask
argument_list|(
name|taskID
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|TaskAttempt
argument_list|>
name|attempts
init|=
name|task
operator|.
name|getAttempts
argument_list|()
decl_stmt|;
name|long
name|acceptableRuntime
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|long
name|result
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
if|if
condition|(
operator|!
name|mayHaveSpeculated
operator|.
name|contains
argument_list|(
name|taskID
argument_list|)
condition|)
block|{
name|acceptableRuntime
operator|=
name|estimator
operator|.
name|thresholdRuntime
argument_list|(
name|taskID
argument_list|)
expr_stmt|;
if|if
condition|(
name|acceptableRuntime
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|ON_SCHEDULE
return|;
block|}
block|}
name|TaskAttemptId
name|runningTaskAttemptID
init|=
literal|null
decl_stmt|;
name|int
name|numberRunningAttempts
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|taskAttempt
range|:
name|attempts
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|taskAttempt
operator|.
name|getState
argument_list|()
operator|==
name|TaskAttemptState
operator|.
name|RUNNING
operator|||
name|taskAttempt
operator|.
name|getState
argument_list|()
operator|==
name|TaskAttemptState
operator|.
name|STARTING
condition|)
block|{
if|if
condition|(
operator|++
name|numberRunningAttempts
operator|>
literal|1
condition|)
block|{
return|return
name|ALREADY_SPECULATING
return|;
block|}
name|runningTaskAttemptID
operator|=
name|taskAttempt
operator|.
name|getID
argument_list|()
expr_stmt|;
name|long
name|estimatedRunTime
init|=
name|estimator
operator|.
name|estimatedRuntime
argument_list|(
name|runningTaskAttemptID
argument_list|)
decl_stmt|;
name|long
name|taskAttemptStartTime
init|=
name|estimator
operator|.
name|attemptEnrolledTime
argument_list|(
name|runningTaskAttemptID
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskAttemptStartTime
operator|>
name|now
condition|)
block|{
comment|// This background process ran before we could process the task
comment|//  attempt status change that chronicles the attempt start
return|return
name|TOO_NEW
return|;
block|}
name|long
name|estimatedEndTime
init|=
name|estimatedRunTime
operator|+
name|taskAttemptStartTime
decl_stmt|;
name|long
name|estimatedReplacementEndTime
init|=
name|now
operator|+
name|estimator
operator|.
name|estimatedNewAttemptRuntime
argument_list|(
name|taskID
argument_list|)
decl_stmt|;
if|if
condition|(
name|estimatedEndTime
operator|<
name|now
condition|)
block|{
return|return
name|PROGRESS_IS_GOOD
return|;
block|}
if|if
condition|(
name|estimatedReplacementEndTime
operator|>=
name|estimatedEndTime
condition|)
block|{
return|return
name|TOO_LATE_TO_SPECULATE
return|;
block|}
name|result
operator|=
name|estimatedEndTime
operator|-
name|estimatedReplacementEndTime
expr_stmt|;
block|}
block|}
comment|// If we are here, there's at most one task attempt.
if|if
condition|(
name|numberRunningAttempts
operator|==
literal|0
condition|)
block|{
return|return
name|NOT_RUNNING
return|;
block|}
if|if
condition|(
name|acceptableRuntime
operator|==
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
name|acceptableRuntime
operator|=
name|estimator
operator|.
name|thresholdRuntime
argument_list|(
name|taskID
argument_list|)
expr_stmt|;
if|if
condition|(
name|acceptableRuntime
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|ON_SCHEDULE
return|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|//Add attempt to a given Task.
DECL|method|addSpeculativeAttempt (TaskId taskID)
specifier|protected
name|void
name|addSpeculativeAttempt
parameter_list|(
name|TaskId
name|taskID
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DefaultSpeculator.addSpeculativeAttempt -- we are speculating "
operator|+
name|taskID
argument_list|)
expr_stmt|;
name|eventHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskEvent
argument_list|(
name|taskID
argument_list|,
name|TaskEventType
operator|.
name|T_ADD_SPEC_ATTEMPT
argument_list|)
argument_list|)
expr_stmt|;
name|mayHaveSpeculated
operator|.
name|add
argument_list|(
name|taskID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (SpeculatorEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|SpeculatorEvent
name|event
parameter_list|)
block|{
name|processSpeculatorEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
DECL|method|maybeScheduleAMapSpeculation ()
specifier|private
name|int
name|maybeScheduleAMapSpeculation
parameter_list|()
block|{
return|return
name|maybeScheduleASpeculation
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
return|;
block|}
DECL|method|maybeScheduleAReduceSpeculation ()
specifier|private
name|int
name|maybeScheduleAReduceSpeculation
parameter_list|()
block|{
return|return
name|maybeScheduleASpeculation
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
return|;
block|}
DECL|method|maybeScheduleASpeculation (TaskType type)
specifier|private
name|int
name|maybeScheduleASpeculation
parameter_list|(
name|TaskType
name|type
parameter_list|)
block|{
name|int
name|successes
init|=
literal|0
decl_stmt|;
name|long
name|now
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|JobId
argument_list|,
name|AtomicInteger
argument_list|>
name|containerNeeds
init|=
name|type
operator|==
name|TaskType
operator|.
name|MAP
condition|?
name|mapContainerNeeds
else|:
name|reduceContainerNeeds
decl_stmt|;
for|for
control|(
name|ConcurrentMap
operator|.
name|Entry
argument_list|<
name|JobId
argument_list|,
name|AtomicInteger
argument_list|>
name|jobEntry
range|:
name|containerNeeds
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// This race conditon is okay.  If we skip a speculation attempt we
comment|//  should have tried because the event that lowers the number of
comment|//  containers needed to zero hasn't come through, it will next time.
comment|// Also, if we miss the fact that the number of containers needed was
comment|//  zero but increased due to a failure it's not too bad to launch one
comment|//  container prematurely.
if|if
condition|(
name|jobEntry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
continue|continue;
block|}
name|int
name|numberSpeculationsAlready
init|=
literal|0
decl_stmt|;
name|int
name|numberRunningTasks
init|=
literal|0
decl_stmt|;
comment|// loop through the tasks of the kind
name|Job
name|job
init|=
name|context
operator|.
name|getJob
argument_list|(
name|jobEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|int
name|numberAllowedSpeculativeTasks
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|max
argument_list|(
name|MINIMUM_ALLOWED_SPECULATIVE_TASKS
argument_list|,
name|PROPORTION_TOTAL_TASKS_SPECULATABLE
operator|*
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|TaskId
name|bestTaskID
init|=
literal|null
decl_stmt|;
name|long
name|bestSpeculationValue
init|=
operator|-
literal|1L
decl_stmt|;
comment|// this loop is potentially pricey.
comment|// TODO track the tasks that are potentially worth looking at
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|taskEntry
range|:
name|tasks
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|mySpeculationValue
init|=
name|speculationValue
argument_list|(
name|taskEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|now
argument_list|)
decl_stmt|;
if|if
condition|(
name|mySpeculationValue
operator|==
name|ALREADY_SPECULATING
condition|)
block|{
operator|++
name|numberSpeculationsAlready
expr_stmt|;
block|}
if|if
condition|(
name|mySpeculationValue
operator|!=
name|NOT_RUNNING
condition|)
block|{
operator|++
name|numberRunningTasks
expr_stmt|;
block|}
if|if
condition|(
name|mySpeculationValue
operator|>
name|bestSpeculationValue
condition|)
block|{
name|bestTaskID
operator|=
name|taskEntry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|bestSpeculationValue
operator|=
name|mySpeculationValue
expr_stmt|;
block|}
block|}
name|numberAllowedSpeculativeTasks
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|max
argument_list|(
name|numberAllowedSpeculativeTasks
argument_list|,
name|PROPORTION_RUNNING_TASKS_SPECULATABLE
operator|*
name|numberRunningTasks
argument_list|)
expr_stmt|;
comment|// If we found a speculation target, fire it off
if|if
condition|(
name|bestTaskID
operator|!=
literal|null
operator|&&
name|numberAllowedSpeculativeTasks
operator|>
name|numberSpeculationsAlready
condition|)
block|{
name|addSpeculativeAttempt
argument_list|(
name|bestTaskID
argument_list|)
expr_stmt|;
operator|++
name|successes
expr_stmt|;
block|}
block|}
return|return
name|successes
return|;
block|}
DECL|method|computeSpeculations ()
specifier|private
name|int
name|computeSpeculations
parameter_list|()
block|{
comment|// We'll try to issue one map and one reduce speculation per job per run
return|return
name|maybeScheduleAMapSpeculation
argument_list|()
operator|+
name|maybeScheduleAReduceSpeculation
argument_list|()
return|;
block|}
block|}
end_class

end_unit

