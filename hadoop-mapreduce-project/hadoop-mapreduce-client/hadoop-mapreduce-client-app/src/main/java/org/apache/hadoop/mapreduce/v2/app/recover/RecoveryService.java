begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.recover
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
name|recover
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
name|LinkedList
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
name|FSDataInputStream
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
name|FileContext
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
name|jobhistory
operator|.
name|JobHistoryParser
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
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|JobInfo
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
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|AMInfo
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
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|TaskAttemptInfo
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
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|TaskInfo
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
name|Phase
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
name|TaskState
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
name|TaskAttemptContainerAssignedEvent
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
name|TaskAttemptContainerLaunchedEvent
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
name|job
operator|.
name|event
operator|.
name|TaskAttemptStatusUpdateEvent
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
name|TaskTAttemptEvent
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
name|launcher
operator|.
name|ContainerLauncher
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
name|launcher
operator|.
name|ContainerRemoteLaunchEvent
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
name|ContainerAllocator
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
name|ContainerAllocatorEvent
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
name|taskclean
operator|.
name|TaskCleaner
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
name|taskclean
operator|.
name|TaskCleanupEvent
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
name|jobhistory
operator|.
name|JobHistoryUtils
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
name|Clock
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|api
operator|.
name|records
operator|.
name|Container
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
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|AsyncDispatcher
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
name|Dispatcher
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
name|Event
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|CompositeService
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
name|Service
import|;
end_import

begin_comment
comment|/*  * Recovers the completed tasks from the previous life of Application Master.  * The completed tasks are deciphered from the history file of the previous life.  * Recovery service intercepts and replay the events for completed tasks.  * While recovery is in progress, the scheduling of new tasks are delayed by   * buffering the task schedule events.  * The recovery service controls the clock while recovery is in progress.  */
end_comment

begin_comment
comment|//TODO:
end_comment

begin_comment
comment|//task cleanup for all non completed tasks
end_comment

begin_comment
comment|//change job output committer to have
end_comment

begin_comment
comment|//    - atomic job output promotion
end_comment

begin_comment
comment|//    - recover output of completed tasks
end_comment

begin_class
DECL|class|RecoveryService
specifier|public
class|class
name|RecoveryService
extends|extends
name|CompositeService
implements|implements
name|Recovery
block|{
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
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
name|RecoveryService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|applicationAttemptId
specifier|private
specifier|final
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|ControlledClock
name|clock
decl_stmt|;
DECL|field|jobInfo
specifier|private
name|JobInfo
name|jobInfo
init|=
literal|null
decl_stmt|;
DECL|field|completedTasks
specifier|private
specifier|final
name|Map
argument_list|<
name|TaskId
argument_list|,
name|TaskInfo
argument_list|>
name|completedTasks
init|=
operator|new
name|HashMap
argument_list|<
name|TaskId
argument_list|,
name|TaskInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|pendingTaskScheduleEvents
specifier|private
specifier|final
name|List
argument_list|<
name|TaskEvent
argument_list|>
name|pendingTaskScheduleEvents
init|=
operator|new
name|ArrayList
argument_list|<
name|TaskEvent
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|recoveryMode
specifier|private
specifier|volatile
name|boolean
name|recoveryMode
init|=
literal|false
decl_stmt|;
DECL|method|RecoveryService (ApplicationAttemptId applicationAttemptId, Clock clock)
specifier|public
name|RecoveryService
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|super
argument_list|(
literal|"RecoveringDispatcher"
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|applicationAttemptId
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
operator|new
name|RecoveryDispatcher
argument_list|()
expr_stmt|;
name|this
operator|.
name|clock
operator|=
operator|new
name|ControlledClock
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|addService
argument_list|(
operator|(
name|Service
operator|)
name|dispatcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// parse the history file
try|try
block|{
name|parse
argument_list|()
expr_stmt|;
if|if
condition|(
name|completedTasks
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|recoveryMode
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"SETTING THE RECOVERY MODE TO TRUE. NO OF COMPLETED TASKS "
operator|+
literal|"TO RECOVER "
operator|+
name|completedTasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Job launch time "
operator|+
name|jobInfo
operator|.
name|getLaunchTime
argument_list|()
argument_list|)
expr_stmt|;
name|clock
operator|.
name|setTime
argument_list|(
name|jobInfo
operator|.
name|getLaunchTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not parse the old history file. Aborting recovery. "
operator|+
literal|"Starting afresh."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDispatcher ()
specifier|public
name|Dispatcher
name|getDispatcher
parameter_list|()
block|{
return|return
name|dispatcher
return|;
block|}
annotation|@
name|Override
DECL|method|getClock ()
specifier|public
name|Clock
name|getClock
parameter_list|()
block|{
return|return
name|clock
return|;
block|}
annotation|@
name|Override
DECL|method|getCompletedTasks ()
specifier|public
name|Set
argument_list|<
name|TaskId
argument_list|>
name|getCompletedTasks
parameter_list|()
block|{
return|return
name|completedTasks
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAMInfos ()
specifier|public
name|List
argument_list|<
name|AMInfo
argument_list|>
name|getAMInfos
parameter_list|()
block|{
if|if
condition|(
name|jobInfo
operator|==
literal|null
operator|||
name|jobInfo
operator|.
name|getAMInfos
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|LinkedList
argument_list|<
name|AMInfo
argument_list|>
argument_list|()
return|;
block|}
return|return
operator|new
name|LinkedList
argument_list|<
name|AMInfo
argument_list|>
argument_list|(
name|jobInfo
operator|.
name|getAMInfos
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parse ()
specifier|private
name|void
name|parse
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: parse history file based on startCount
name|String
name|jobName
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|applicationAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|jobhistoryDir
init|=
name|JobHistoryUtils
operator|.
name|getConfiguredHistoryStagingDirPrefix
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|Path
name|historyFile
init|=
literal|null
decl_stmt|;
name|Path
name|histDirPath
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|getConfig
argument_list|()
argument_list|)
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|jobhistoryDir
argument_list|)
argument_list|)
decl_stmt|;
name|FileContext
name|fc
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|histDirPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
comment|//read the previous history file
name|historyFile
operator|=
name|fc
operator|.
name|makeQualified
argument_list|(
name|JobHistoryUtils
operator|.
name|getStagingJobHistoryFile
argument_list|(
name|histDirPath
argument_list|,
name|jobName
argument_list|,
operator|(
name|applicationAttemptId
operator|.
name|getAttemptId
argument_list|()
operator|-
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|=
name|fc
operator|.
name|open
argument_list|(
name|historyFile
argument_list|)
expr_stmt|;
name|JobHistoryParser
name|parser
init|=
operator|new
name|JobHistoryParser
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|jobInfo
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskID
argument_list|,
name|TaskInfo
argument_list|>
name|taskInfos
init|=
name|jobInfo
operator|.
name|getAllTasks
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskInfo
name|taskInfo
range|:
name|taskInfos
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|TaskState
operator|.
name|SUCCEEDED
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|taskInfo
operator|.
name|getTaskStatus
argument_list|()
argument_list|)
condition|)
block|{
name|completedTasks
operator|.
name|put
argument_list|(
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskInfo
operator|.
name|getTaskId
argument_list|()
argument_list|)
argument_list|,
name|taskInfo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Read from history task "
operator|+
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskInfo
operator|.
name|getTaskId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Read completed tasks from history "
operator|+
name|completedTasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|RecoveryDispatcher
class|class
name|RecoveryDispatcher
extends|extends
name|AsyncDispatcher
block|{
DECL|field|actualHandler
specifier|private
specifier|final
name|EventHandler
name|actualHandler
decl_stmt|;
DECL|field|handler
specifier|private
specifier|final
name|EventHandler
name|handler
decl_stmt|;
DECL|method|RecoveryDispatcher ()
name|RecoveryDispatcher
parameter_list|()
block|{
name|actualHandler
operator|=
name|super
operator|.
name|getEventHandler
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|InterceptingEventHandler
argument_list|(
name|actualHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|dispatch (Event event)
specifier|public
name|void
name|dispatch
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
if|if
condition|(
name|recoveryMode
condition|)
block|{
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskAttemptEventType
operator|.
name|TA_CONTAINER_LAUNCHED
condition|)
block|{
name|TaskAttemptInfo
name|attInfo
init|=
name|getTaskAttemptInfo
argument_list|(
operator|(
operator|(
name|TaskAttemptEvent
operator|)
name|event
operator|)
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempt start time "
operator|+
name|attInfo
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|clock
operator|.
name|setTime
argument_list|(
name|attInfo
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskAttemptEventType
operator|.
name|TA_DONE
operator|||
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
operator|||
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskAttemptEventType
operator|.
name|TA_KILL
condition|)
block|{
name|TaskAttemptInfo
name|attInfo
init|=
name|getTaskAttemptInfo
argument_list|(
operator|(
operator|(
name|TaskAttemptEvent
operator|)
name|event
operator|)
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempt finish time "
operator|+
name|attInfo
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|clock
operator|.
name|setTime
argument_list|(
name|attInfo
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskEventType
operator|.
name|T_ATTEMPT_FAILED
operator|||
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskEventType
operator|.
name|T_ATTEMPT_KILLED
operator|||
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskEventType
operator|.
name|T_ATTEMPT_SUCCEEDED
condition|)
block|{
name|TaskTAttemptEvent
name|tEvent
init|=
operator|(
name|TaskTAttemptEvent
operator|)
name|event
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovered Task attempt "
operator|+
name|tEvent
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
expr_stmt|;
name|TaskInfo
name|taskInfo
init|=
name|completedTasks
operator|.
name|get
argument_list|(
name|tEvent
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskId
argument_list|()
argument_list|)
decl_stmt|;
name|taskInfo
operator|.
name|getAllTaskAttempts
argument_list|()
operator|.
name|remove
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|tEvent
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove the task info from completed tasks if all attempts are
comment|// recovered
if|if
condition|(
name|taskInfo
operator|.
name|getAllTaskAttempts
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|completedTasks
operator|.
name|remove
argument_list|(
name|tEvent
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskId
argument_list|()
argument_list|)
expr_stmt|;
comment|// checkForRecoveryComplete
name|LOG
operator|.
name|info
argument_list|(
literal|"CompletedTasks() "
operator|+
name|completedTasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|completedTasks
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|recoveryMode
operator|=
literal|false
expr_stmt|;
name|clock
operator|.
name|reset
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting the recovery mode to false. "
operator|+
literal|"Recovery is complete!"
argument_list|)
expr_stmt|;
comment|// send all pending tasks schedule events
for|for
control|(
name|TaskEvent
name|tEv
range|:
name|pendingTaskScheduleEvents
control|)
block|{
name|actualHandler
operator|.
name|handle
argument_list|(
name|tEv
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|super
operator|.
name|dispatch
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEventHandler ()
specifier|public
name|EventHandler
name|getEventHandler
parameter_list|()
block|{
return|return
name|handler
return|;
block|}
block|}
DECL|method|getTaskAttemptInfo (TaskAttemptId id)
specifier|private
name|TaskAttemptInfo
name|getTaskAttemptInfo
parameter_list|(
name|TaskAttemptId
name|id
parameter_list|)
block|{
name|TaskInfo
name|taskInfo
init|=
name|completedTasks
operator|.
name|get
argument_list|(
name|id
operator|.
name|getTaskId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|taskInfo
operator|.
name|getAllTaskAttempts
argument_list|()
operator|.
name|get
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
DECL|class|InterceptingEventHandler
specifier|private
class|class
name|InterceptingEventHandler
implements|implements
name|EventHandler
block|{
DECL|field|actualHandler
name|EventHandler
name|actualHandler
decl_stmt|;
DECL|method|InterceptingEventHandler (EventHandler actualHandler)
name|InterceptingEventHandler
parameter_list|(
name|EventHandler
name|actualHandler
parameter_list|)
block|{
name|this
operator|.
name|actualHandler
operator|=
name|actualHandler
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
if|if
condition|(
operator|!
name|recoveryMode
condition|)
block|{
comment|// delegate to the dispatcher one
name|actualHandler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskEventType
operator|.
name|T_SCHEDULE
condition|)
block|{
name|TaskEvent
name|taskEvent
init|=
operator|(
name|TaskEvent
operator|)
name|event
decl_stmt|;
comment|// delay the scheduling of new tasks till previous ones are recovered
if|if
condition|(
name|completedTasks
operator|.
name|get
argument_list|(
name|taskEvent
operator|.
name|getTaskID
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding to pending task events "
operator|+
name|taskEvent
operator|.
name|getTaskID
argument_list|()
argument_list|)
expr_stmt|;
name|pendingTaskScheduleEvents
operator|.
name|add
argument_list|(
name|taskEvent
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|ContainerAllocator
operator|.
name|EventType
operator|.
name|CONTAINER_REQ
condition|)
block|{
name|TaskAttemptId
name|aId
init|=
operator|(
operator|(
name|ContainerAllocatorEvent
operator|)
name|event
operator|)
operator|.
name|getAttemptID
argument_list|()
decl_stmt|;
name|TaskAttemptInfo
name|attInfo
init|=
name|getTaskAttemptInfo
argument_list|(
name|aId
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"CONTAINER_REQ "
operator|+
name|aId
argument_list|)
expr_stmt|;
name|sendAssignedEvent
argument_list|(
name|aId
argument_list|,
name|attInfo
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|TaskCleaner
operator|.
name|EventType
operator|.
name|TASK_CLEAN
condition|)
block|{
name|TaskAttemptId
name|aId
init|=
operator|(
operator|(
name|TaskCleanupEvent
operator|)
name|event
operator|)
operator|.
name|getAttemptID
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"TASK_CLEAN"
argument_list|)
expr_stmt|;
name|actualHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|aId
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_CLEANUP_DONE
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getType
argument_list|()
operator|==
name|ContainerLauncher
operator|.
name|EventType
operator|.
name|CONTAINER_REMOTE_LAUNCH
condition|)
block|{
name|TaskAttemptId
name|aId
init|=
operator|(
operator|(
name|ContainerRemoteLaunchEvent
operator|)
name|event
operator|)
operator|.
name|getTaskAttemptID
argument_list|()
decl_stmt|;
name|TaskAttemptInfo
name|attInfo
init|=
name|getTaskAttemptInfo
argument_list|(
name|aId
argument_list|)
decl_stmt|;
comment|//TODO need to get the real port number MAPREDUCE-2666
name|actualHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptContainerLaunchedEvent
argument_list|(
name|aId
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// send the status update event
name|sendStatusUpdateEvent
argument_list|(
name|aId
argument_list|,
name|attInfo
argument_list|)
expr_stmt|;
name|TaskAttemptState
name|state
init|=
name|TaskAttemptState
operator|.
name|valueOf
argument_list|(
name|attInfo
operator|.
name|getTaskStatus
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|SUCCEEDED
case|:
comment|// send the done event
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending done event to "
operator|+
name|aId
argument_list|)
expr_stmt|;
name|actualHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|aId
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_DONE
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|KILLED
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending kill event to "
operator|+
name|aId
argument_list|)
expr_stmt|;
name|actualHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|aId
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_KILL
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending fail event to "
operator|+
name|aId
argument_list|)
expr_stmt|;
name|actualHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|aId
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
return|return;
block|}
comment|// delegate to the actual handler
name|actualHandler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
DECL|method|sendStatusUpdateEvent (TaskAttemptId yarnAttemptID, TaskAttemptInfo attemptInfo)
specifier|private
name|void
name|sendStatusUpdateEvent
parameter_list|(
name|TaskAttemptId
name|yarnAttemptID
parameter_list|,
name|TaskAttemptInfo
name|attemptInfo
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending status update event to "
operator|+
name|yarnAttemptID
argument_list|)
expr_stmt|;
name|TaskAttemptStatus
name|taskAttemptStatus
init|=
operator|new
name|TaskAttemptStatus
argument_list|()
decl_stmt|;
name|taskAttemptStatus
operator|.
name|id
operator|=
name|yarnAttemptID
expr_stmt|;
name|taskAttemptStatus
operator|.
name|progress
operator|=
literal|1.0f
expr_stmt|;
name|taskAttemptStatus
operator|.
name|stateString
operator|=
name|attemptInfo
operator|.
name|getTaskStatus
argument_list|()
expr_stmt|;
comment|// taskAttemptStatus.outputSize = attemptInfo.getOutputSize();
name|taskAttemptStatus
operator|.
name|phase
operator|=
name|Phase
operator|.
name|CLEANUP
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Counters
name|cntrs
init|=
name|attemptInfo
operator|.
name|getCounters
argument_list|()
decl_stmt|;
if|if
condition|(
name|cntrs
operator|==
literal|null
condition|)
block|{
name|taskAttemptStatus
operator|.
name|counters
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|taskAttemptStatus
operator|.
name|counters
operator|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|attemptInfo
operator|.
name|getCounters
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|actualHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptStatusUpdateEvent
argument_list|(
name|taskAttemptStatus
operator|.
name|id
argument_list|,
name|taskAttemptStatus
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sendAssignedEvent (TaskAttemptId yarnAttemptID, TaskAttemptInfo attemptInfo)
specifier|private
name|void
name|sendAssignedEvent
parameter_list|(
name|TaskAttemptId
name|yarnAttemptID
parameter_list|,
name|TaskAttemptInfo
name|attemptInfo
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending assigned event to "
operator|+
name|yarnAttemptID
argument_list|)
expr_stmt|;
name|ContainerId
name|cId
init|=
name|attemptInfo
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|container
operator|.
name|setId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeId
argument_list|(
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// NodeId can be obtained from TaskAttemptInfo.hostname - but this will
comment|// eventually contain rack info.
name|container
operator|.
name|setContainerToken
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeHttpAddress
argument_list|(
name|attemptInfo
operator|.
name|getTrackerName
argument_list|()
operator|+
literal|":"
operator|+
name|attemptInfo
operator|.
name|getHttpPort
argument_list|()
argument_list|)
expr_stmt|;
name|actualHandler
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptContainerAssignedEvent
argument_list|(
name|yarnAttemptID
argument_list|,
name|container
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

