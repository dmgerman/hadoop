begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job.impl
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
name|job
operator|.
name|impl
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

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
name|Collection
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
name|mapred
operator|.
name|JobConf
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
name|mapred
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
name|mapred
operator|.
name|TaskUmbilicalProtocol
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
name|security
operator|.
name|token
operator|.
name|JobTokenIdentifier
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
name|split
operator|.
name|JobSplit
operator|.
name|TaskSplitMetaInfo
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
name|TaskAttemptListener
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
name|metrics
operator|.
name|MRAppMetrics
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenIdentifier
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
name|SystemClock
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
name|ApplicationId
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
name|server
operator|.
name|resourcemanager
operator|.
name|resourcetracker
operator|.
name|InlineDispatcher
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
name|Records
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|class|TestTaskImpl
specifier|public
class|class
name|TestTaskImpl
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
name|TestTaskImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|JobConf
name|conf
decl_stmt|;
DECL|field|taskAttemptListener
specifier|private
name|TaskAttemptListener
name|taskAttemptListener
decl_stmt|;
DECL|field|committer
specifier|private
name|OutputCommitter
name|committer
decl_stmt|;
DECL|field|jobToken
specifier|private
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|jobToken
decl_stmt|;
DECL|field|jobId
specifier|private
name|JobId
name|jobId
decl_stmt|;
DECL|field|remoteJobConfFile
specifier|private
name|Path
name|remoteJobConfFile
decl_stmt|;
DECL|field|fsTokens
specifier|private
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|fsTokens
decl_stmt|;
DECL|field|clock
specifier|private
name|Clock
name|clock
decl_stmt|;
DECL|field|completedTasksFromPreviousRun
specifier|private
name|Map
argument_list|<
name|TaskId
argument_list|,
name|TaskInfo
argument_list|>
name|completedTasksFromPreviousRun
decl_stmt|;
DECL|field|metrics
specifier|private
name|MRAppMetrics
name|metrics
decl_stmt|;
DECL|field|mockTask
specifier|private
name|TaskImpl
name|mockTask
decl_stmt|;
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|taskSplitMetaInfo
specifier|private
name|TaskSplitMetaInfo
name|taskSplitMetaInfo
decl_stmt|;
DECL|field|dataLocations
specifier|private
name|String
index|[]
name|dataLocations
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
DECL|field|taskType
specifier|private
specifier|final
name|TaskType
name|taskType
init|=
name|TaskType
operator|.
name|MAP
decl_stmt|;
DECL|field|startCount
specifier|private
name|int
name|startCount
init|=
literal|0
decl_stmt|;
DECL|field|taskCounter
specifier|private
name|int
name|taskCounter
init|=
literal|0
decl_stmt|;
DECL|field|partition
specifier|private
specifier|final
name|int
name|partition
init|=
literal|1
decl_stmt|;
DECL|field|dispatcher
specifier|private
name|InlineDispatcher
name|dispatcher
decl_stmt|;
DECL|field|taskAttempts
specifier|private
name|List
argument_list|<
name|MockTaskAttemptImpl
argument_list|>
name|taskAttempts
decl_stmt|;
DECL|class|MockTaskImpl
specifier|private
class|class
name|MockTaskImpl
extends|extends
name|TaskImpl
block|{
DECL|field|taskAttemptCounter
specifier|private
name|int
name|taskAttemptCounter
init|=
literal|0
decl_stmt|;
DECL|method|MockTaskImpl (JobId jobId, int partition, EventHandler eventHandler, Path remoteJobConfFile, JobConf conf, TaskAttemptListener taskAttemptListener, OutputCommitter committer, Token<JobTokenIdentifier> jobToken, Collection<Token<? extends TokenIdentifier>> fsTokens, Clock clock, Map<TaskId, TaskInfo> completedTasksFromPreviousRun, int startCount, MRAppMetrics metrics)
specifier|public
name|MockTaskImpl
parameter_list|(
name|JobId
name|jobId
parameter_list|,
name|int
name|partition
parameter_list|,
name|EventHandler
name|eventHandler
parameter_list|,
name|Path
name|remoteJobConfFile
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|TaskAttemptListener
name|taskAttemptListener
parameter_list|,
name|OutputCommitter
name|committer
parameter_list|,
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|jobToken
parameter_list|,
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|fsTokens
parameter_list|,
name|Clock
name|clock
parameter_list|,
name|Map
argument_list|<
name|TaskId
argument_list|,
name|TaskInfo
argument_list|>
name|completedTasksFromPreviousRun
parameter_list|,
name|int
name|startCount
parameter_list|,
name|MRAppMetrics
name|metrics
parameter_list|)
block|{
name|super
argument_list|(
name|jobId
argument_list|,
name|taskType
argument_list|,
name|partition
argument_list|,
name|eventHandler
argument_list|,
name|remoteJobConfFile
argument_list|,
name|conf
argument_list|,
name|taskAttemptListener
argument_list|,
name|committer
argument_list|,
name|jobToken
argument_list|,
name|fsTokens
argument_list|,
name|clock
argument_list|,
name|completedTasksFromPreviousRun
argument_list|,
name|startCount
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType ()
specifier|public
name|TaskType
name|getType
parameter_list|()
block|{
return|return
name|taskType
return|;
block|}
annotation|@
name|Override
DECL|method|createAttempt ()
specifier|protected
name|TaskAttemptImpl
name|createAttempt
parameter_list|()
block|{
name|MockTaskAttemptImpl
name|attempt
init|=
operator|new
name|MockTaskAttemptImpl
argument_list|(
name|getID
argument_list|()
argument_list|,
operator|++
name|taskAttemptCounter
argument_list|,
name|eventHandler
argument_list|,
name|taskAttemptListener
argument_list|,
name|remoteJobConfFile
argument_list|,
name|partition
argument_list|,
name|conf
argument_list|,
name|committer
argument_list|,
name|jobToken
argument_list|,
name|fsTokens
argument_list|,
name|clock
argument_list|)
decl_stmt|;
name|taskAttempts
operator|.
name|add
argument_list|(
name|attempt
argument_list|)
expr_stmt|;
return|return
name|attempt
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxAttempts ()
specifier|protected
name|int
name|getMaxAttempts
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
block|}
DECL|class|MockTaskAttemptImpl
specifier|private
class|class
name|MockTaskAttemptImpl
extends|extends
name|TaskAttemptImpl
block|{
DECL|field|progress
specifier|private
name|float
name|progress
init|=
literal|0
decl_stmt|;
DECL|field|state
specifier|private
name|TaskAttemptState
name|state
init|=
name|TaskAttemptState
operator|.
name|NEW
decl_stmt|;
DECL|field|attemptId
specifier|private
name|TaskAttemptId
name|attemptId
decl_stmt|;
DECL|method|MockTaskAttemptImpl (TaskId taskId, int id, EventHandler eventHandler, TaskAttemptListener taskAttemptListener, Path jobFile, int partition, JobConf conf, OutputCommitter committer, Token<JobTokenIdentifier> jobToken, Collection<Token<? extends TokenIdentifier>> fsTokens, Clock clock)
specifier|public
name|MockTaskAttemptImpl
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|int
name|id
parameter_list|,
name|EventHandler
name|eventHandler
parameter_list|,
name|TaskAttemptListener
name|taskAttemptListener
parameter_list|,
name|Path
name|jobFile
parameter_list|,
name|int
name|partition
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|OutputCommitter
name|committer
parameter_list|,
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|jobToken
parameter_list|,
name|Collection
argument_list|<
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|fsTokens
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|super
argument_list|(
name|taskId
argument_list|,
name|id
argument_list|,
name|eventHandler
argument_list|,
name|taskAttemptListener
argument_list|,
name|jobFile
argument_list|,
name|partition
argument_list|,
name|conf
argument_list|,
name|dataLocations
argument_list|,
name|committer
argument_list|,
name|jobToken
argument_list|,
name|fsTokens
argument_list|,
name|clock
argument_list|)
expr_stmt|;
name|attemptId
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|TaskAttemptId
operator|.
name|class
argument_list|)
expr_stmt|;
name|attemptId
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|attemptId
operator|.
name|setTaskId
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
block|}
DECL|method|getAttemptId ()
specifier|public
name|TaskAttemptId
name|getAttemptId
parameter_list|()
block|{
return|return
name|attemptId
return|;
block|}
annotation|@
name|Override
DECL|method|createRemoteTask ()
specifier|protected
name|Task
name|createRemoteTask
parameter_list|()
block|{
return|return
operator|new
name|MockTask
argument_list|()
return|;
block|}
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|progress
return|;
block|}
DECL|method|setProgress (float progress)
specifier|public
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
block|{
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
block|}
DECL|method|setState (TaskAttemptState state)
specifier|public
name|void
name|setState
parameter_list|(
name|TaskAttemptState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getState ()
specifier|public
name|TaskAttemptState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
block|}
DECL|class|MockTask
specifier|private
class|class
name|MockTask
extends|extends
name|Task
block|{
annotation|@
name|Override
DECL|method|run (JobConf job, TaskUmbilicalProtocol umbilical)
specifier|public
name|void
name|run
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|TaskUmbilicalProtocol
name|umbilical
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
throws|,
name|InterruptedException
block|{
return|return;
block|}
annotation|@
name|Override
DECL|method|isMapTask ()
specifier|public
name|boolean
name|isMapTask
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Before
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|dispatcher
operator|=
operator|new
name|InlineDispatcher
argument_list|()
expr_stmt|;
operator|++
name|startCount
expr_stmt|;
name|conf
operator|=
operator|new
name|JobConf
argument_list|()
expr_stmt|;
name|taskAttemptListener
operator|=
name|mock
argument_list|(
name|TaskAttemptListener
operator|.
name|class
argument_list|)
expr_stmt|;
name|committer
operator|=
name|mock
argument_list|(
name|OutputCommitter
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobToken
operator|=
operator|(
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
operator|)
name|mock
argument_list|(
name|Token
operator|.
name|class
argument_list|)
expr_stmt|;
name|remoteJobConfFile
operator|=
name|mock
argument_list|(
name|Path
operator|.
name|class
argument_list|)
expr_stmt|;
name|fsTokens
operator|=
literal|null
expr_stmt|;
name|clock
operator|=
operator|new
name|SystemClock
argument_list|()
expr_stmt|;
name|metrics
operator|=
name|mock
argument_list|(
name|MRAppMetrics
operator|.
name|class
argument_list|)
expr_stmt|;
name|dataLocations
operator|=
operator|new
name|String
index|[
literal|1
index|]
expr_stmt|;
name|appId
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setClusterTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|appId
operator|.
name|setId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|jobId
operator|=
name|Records
operator|.
name|newRecord
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobId
operator|.
name|setId
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|jobId
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|taskSplitMetaInfo
operator|=
name|mock
argument_list|(
name|TaskSplitMetaInfo
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|taskSplitMetaInfo
operator|.
name|getLocations
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dataLocations
argument_list|)
expr_stmt|;
name|taskAttempts
operator|=
operator|new
name|ArrayList
argument_list|<
name|MockTaskAttemptImpl
argument_list|>
argument_list|()
expr_stmt|;
name|mockTask
operator|=
operator|new
name|MockTaskImpl
argument_list|(
name|jobId
argument_list|,
name|partition
argument_list|,
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
argument_list|,
name|remoteJobConfFile
argument_list|,
name|conf
argument_list|,
name|taskAttemptListener
argument_list|,
name|committer
argument_list|,
name|jobToken
argument_list|,
name|fsTokens
argument_list|,
name|clock
argument_list|,
name|completedTasksFromPreviousRun
argument_list|,
name|startCount
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|taskAttempts
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getNewTaskID ()
specifier|private
name|TaskId
name|getNewTaskID
parameter_list|()
block|{
name|TaskId
name|taskId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|TaskId
operator|.
name|class
argument_list|)
decl_stmt|;
name|taskId
operator|.
name|setId
argument_list|(
operator|++
name|taskCounter
argument_list|)
expr_stmt|;
name|taskId
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|taskId
operator|.
name|setTaskType
argument_list|(
name|mockTask
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|taskId
return|;
block|}
DECL|method|scheduleTaskAttempt (TaskId taskId)
specifier|private
name|void
name|scheduleTaskAttempt
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
block|{
name|mockTask
operator|.
name|handle
argument_list|(
operator|new
name|TaskEvent
argument_list|(
name|taskId
argument_list|,
name|TaskEventType
operator|.
name|T_SCHEDULE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTaskScheduledState
argument_list|()
expr_stmt|;
block|}
DECL|method|killTask (TaskId taskId)
specifier|private
name|void
name|killTask
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
block|{
name|mockTask
operator|.
name|handle
argument_list|(
operator|new
name|TaskEvent
argument_list|(
name|taskId
argument_list|,
name|TaskEventType
operator|.
name|T_KILL
argument_list|)
argument_list|)
expr_stmt|;
name|assertTaskKillWaitState
argument_list|()
expr_stmt|;
block|}
DECL|method|killScheduledTaskAttempt (TaskAttemptId attemptId)
specifier|private
name|void
name|killScheduledTaskAttempt
parameter_list|(
name|TaskAttemptId
name|attemptId
parameter_list|)
block|{
name|mockTask
operator|.
name|handle
argument_list|(
operator|new
name|TaskTAttemptEvent
argument_list|(
name|attemptId
argument_list|,
name|TaskEventType
operator|.
name|T_ATTEMPT_KILLED
argument_list|)
argument_list|)
expr_stmt|;
name|assertTaskScheduledState
argument_list|()
expr_stmt|;
block|}
DECL|method|launchTaskAttempt (TaskAttemptId attemptId)
specifier|private
name|void
name|launchTaskAttempt
parameter_list|(
name|TaskAttemptId
name|attemptId
parameter_list|)
block|{
name|mockTask
operator|.
name|handle
argument_list|(
operator|new
name|TaskTAttemptEvent
argument_list|(
name|attemptId
argument_list|,
name|TaskEventType
operator|.
name|T_ATTEMPT_LAUNCHED
argument_list|)
argument_list|)
expr_stmt|;
name|assertTaskRunningState
argument_list|()
expr_stmt|;
block|}
DECL|method|getLastAttempt ()
specifier|private
name|MockTaskAttemptImpl
name|getLastAttempt
parameter_list|()
block|{
return|return
name|taskAttempts
operator|.
name|get
argument_list|(
name|taskAttempts
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|updateLastAttemptProgress (float p)
specifier|private
name|void
name|updateLastAttemptProgress
parameter_list|(
name|float
name|p
parameter_list|)
block|{
name|getLastAttempt
argument_list|()
operator|.
name|setProgress
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
DECL|method|updateLastAttemptState (TaskAttemptState s)
specifier|private
name|void
name|updateLastAttemptState
parameter_list|(
name|TaskAttemptState
name|s
parameter_list|)
block|{
name|getLastAttempt
argument_list|()
operator|.
name|setState
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|killRunningTaskAttempt (TaskAttemptId attemptId)
specifier|private
name|void
name|killRunningTaskAttempt
parameter_list|(
name|TaskAttemptId
name|attemptId
parameter_list|)
block|{
name|mockTask
operator|.
name|handle
argument_list|(
operator|new
name|TaskTAttemptEvent
argument_list|(
name|attemptId
argument_list|,
name|TaskEventType
operator|.
name|T_ATTEMPT_KILLED
argument_list|)
argument_list|)
expr_stmt|;
name|assertTaskRunningState
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@link TaskState#NEW}    */
DECL|method|assertTaskNewState ()
specifier|private
name|void
name|assertTaskNewState
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|mockTask
operator|.
name|getState
argument_list|()
argument_list|,
name|TaskState
operator|.
name|NEW
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@link TaskState#SCHEDULED}    */
DECL|method|assertTaskScheduledState ()
specifier|private
name|void
name|assertTaskScheduledState
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|mockTask
operator|.
name|getState
argument_list|()
argument_list|,
name|TaskState
operator|.
name|SCHEDULED
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@link TaskState#RUNNING}    */
DECL|method|assertTaskRunningState ()
specifier|private
name|void
name|assertTaskRunningState
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|mockTask
operator|.
name|getState
argument_list|()
argument_list|,
name|TaskState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@link TaskState#KILL_WAIT}    */
DECL|method|assertTaskKillWaitState ()
specifier|private
name|void
name|assertTaskKillWaitState
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|mockTask
operator|.
name|getState
argument_list|()
argument_list|,
name|TaskState
operator|.
name|KILL_WAIT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInit ()
specifier|public
name|void
name|testInit
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testInit ---"
argument_list|)
expr_stmt|;
name|assertTaskNewState
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|taskAttempts
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
assert|;
block|}
annotation|@
name|Test
comment|/**    * {@link TaskState#NEW}->{@link TaskState#SCHEDULED}    */
DECL|method|testScheduleTask ()
specifier|public
name|void
name|testScheduleTask
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testScheduleTask ---"
argument_list|)
expr_stmt|;
name|TaskId
name|taskId
init|=
name|getNewTaskID
argument_list|()
decl_stmt|;
name|scheduleTaskAttempt
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * {@link TaskState#SCHEDULED}->{@link TaskState#KILL_WAIT}    */
DECL|method|testKillScheduledTask ()
specifier|public
name|void
name|testKillScheduledTask
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testKillScheduledTask ---"
argument_list|)
expr_stmt|;
name|TaskId
name|taskId
init|=
name|getNewTaskID
argument_list|()
decl_stmt|;
name|scheduleTaskAttempt
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
name|killTask
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * Kill attempt    * {@link TaskState#SCHEDULED}->{@link TaskState#SCHEDULED}    */
DECL|method|testKillScheduledTaskAttempt ()
specifier|public
name|void
name|testKillScheduledTaskAttempt
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testKillScheduledTaskAttempt ---"
argument_list|)
expr_stmt|;
name|TaskId
name|taskId
init|=
name|getNewTaskID
argument_list|()
decl_stmt|;
name|scheduleTaskAttempt
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
name|killScheduledTaskAttempt
argument_list|(
name|getLastAttempt
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * Launch attempt    * {@link TaskState#SCHEDULED}->{@link TaskState#RUNNING}    */
DECL|method|testLaunchTaskAttempt ()
specifier|public
name|void
name|testLaunchTaskAttempt
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testLaunchTaskAttempt ---"
argument_list|)
expr_stmt|;
name|TaskId
name|taskId
init|=
name|getNewTaskID
argument_list|()
decl_stmt|;
name|scheduleTaskAttempt
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
name|launchTaskAttempt
argument_list|(
name|getLastAttempt
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * Kill running attempt    * {@link TaskState#RUNNING}->{@link TaskState#RUNNING}     */
DECL|method|testKillRunningTaskAttempt ()
specifier|public
name|void
name|testKillRunningTaskAttempt
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testKillRunningTaskAttempt ---"
argument_list|)
expr_stmt|;
name|TaskId
name|taskId
init|=
name|getNewTaskID
argument_list|()
decl_stmt|;
name|scheduleTaskAttempt
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
name|launchTaskAttempt
argument_list|(
name|getLastAttempt
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|killRunningTaskAttempt
argument_list|(
name|getLastAttempt
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTaskProgress ()
specifier|public
name|void
name|testTaskProgress
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testTaskProgress ---"
argument_list|)
expr_stmt|;
comment|// launch task
name|TaskId
name|taskId
init|=
name|getNewTaskID
argument_list|()
decl_stmt|;
name|scheduleTaskAttempt
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
name|float
name|progress
init|=
literal|0f
decl_stmt|;
assert|assert
operator|(
name|mockTask
operator|.
name|getProgress
argument_list|()
operator|==
name|progress
operator|)
assert|;
name|launchTaskAttempt
argument_list|(
name|getLastAttempt
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
comment|// update attempt1
name|progress
operator|=
literal|50f
expr_stmt|;
name|updateLastAttemptProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|mockTask
operator|.
name|getProgress
argument_list|()
operator|==
name|progress
operator|)
assert|;
name|progress
operator|=
literal|100f
expr_stmt|;
name|updateLastAttemptProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|mockTask
operator|.
name|getProgress
argument_list|()
operator|==
name|progress
operator|)
assert|;
name|progress
operator|=
literal|0f
expr_stmt|;
comment|// mark first attempt as killed
name|updateLastAttemptState
argument_list|(
name|TaskAttemptState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|mockTask
operator|.
name|getProgress
argument_list|()
operator|==
name|progress
operator|)
assert|;
comment|// kill first attempt
comment|// should trigger a new attempt
comment|// as no successful attempts
name|killRunningTaskAttempt
argument_list|(
name|getLastAttempt
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|taskAttempts
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|)
assert|;
assert|assert
operator|(
name|mockTask
operator|.
name|getProgress
argument_list|()
operator|==
literal|0f
operator|)
assert|;
name|launchTaskAttempt
argument_list|(
name|getLastAttempt
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|progress
operator|=
literal|50f
expr_stmt|;
name|updateLastAttemptProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|mockTask
operator|.
name|getProgress
argument_list|()
operator|==
name|progress
operator|)
assert|;
block|}
block|}
end_class

end_unit

