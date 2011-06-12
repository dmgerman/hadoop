begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|Collection
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|io
operator|.
name|IntWritable
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
name|FakeObjectUtilities
operator|.
name|FakeJobHistory
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
name|JobInProgress
operator|.
name|KillInterruptedException
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
name|JobStatusChangeEvent
operator|.
name|EventType
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
name|Cluster
operator|.
name|JobTrackerStatus
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
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
import|;
end_import

begin_class
DECL|class|TestParallelInitialization
specifier|public
class|class
name|TestParallelInitialization
extends|extends
name|TestCase
block|{
DECL|field|jobCounter
specifier|private
specifier|static
name|int
name|jobCounter
decl_stmt|;
DECL|field|NUM_JOBS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_JOBS
init|=
literal|3
decl_stmt|;
DECL|field|numJobsCompleted
name|IntWritable
name|numJobsCompleted
init|=
operator|new
name|IntWritable
argument_list|()
decl_stmt|;
DECL|method|resetCounters ()
specifier|static
name|void
name|resetCounters
parameter_list|()
block|{
name|jobCounter
operator|=
literal|0
expr_stmt|;
block|}
DECL|class|FakeJobInProgress
class|class
name|FakeJobInProgress
extends|extends
name|JobInProgress
block|{
DECL|method|FakeJobInProgress (JobConf jobConf, FakeTaskTrackerManager taskTrackerManager, JobTracker jt)
specifier|public
name|FakeJobInProgress
parameter_list|(
name|JobConf
name|jobConf
parameter_list|,
name|FakeTaskTrackerManager
name|taskTrackerManager
parameter_list|,
name|JobTracker
name|jt
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|JobID
argument_list|(
literal|"test"
argument_list|,
operator|++
name|jobCounter
argument_list|)
argument_list|,
name|jobConf
argument_list|,
name|jt
argument_list|)
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|status
operator|=
operator|new
name|JobStatus
argument_list|(
name|getJobID
argument_list|()
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|,
name|JobStatus
operator|.
name|PREP
argument_list|,
name|jobConf
operator|.
name|getUser
argument_list|()
argument_list|,
name|jobConf
operator|.
name|getJobName
argument_list|()
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|.
name|setJobPriority
argument_list|(
name|JobPriority
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|jobHistory
operator|=
operator|new
name|FakeJobHistory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initTasks ()
specifier|public
specifier|synchronized
name|void
name|initTasks
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|int
name|jobNumber
init|=
name|this
operator|.
name|getJobID
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|numJobsCompleted
init|)
block|{
while|while
condition|(
name|numJobsCompleted
operator|.
name|get
argument_list|()
operator|!=
operator|(
name|NUM_JOBS
operator|-
name|jobNumber
operator|)
condition|)
block|{
name|numJobsCompleted
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
name|numJobsCompleted
operator|.
name|set
argument_list|(
name|numJobsCompleted
operator|.
name|get
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|numJobsCompleted
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JobNumber "
operator|+
name|jobNumber
operator|+
literal|" succeeded"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{}
empty_stmt|;
name|this
operator|.
name|status
operator|.
name|setRunState
argument_list|(
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fail ()
specifier|synchronized
name|void
name|fail
parameter_list|()
block|{
name|this
operator|.
name|status
operator|.
name|setRunState
argument_list|(
name|JobStatus
operator|.
name|FAILED
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FakeTaskTrackerManager
specifier|static
class|class
name|FakeTaskTrackerManager
implements|implements
name|TaskTrackerManager
block|{
DECL|field|maps
name|int
name|maps
init|=
literal|0
decl_stmt|;
DECL|field|reduces
name|int
name|reduces
init|=
literal|0
decl_stmt|;
DECL|field|maxMapTasksPerTracker
name|int
name|maxMapTasksPerTracker
init|=
literal|2
decl_stmt|;
DECL|field|maxReduceTasksPerTracker
name|int
name|maxReduceTasksPerTracker
init|=
literal|2
decl_stmt|;
DECL|field|listeners
name|List
argument_list|<
name|JobInProgressListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<
name|JobInProgressListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|queueManager
name|QueueManager
name|queueManager
decl_stmt|;
DECL|field|trackers
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|TaskTrackerStatus
argument_list|>
name|trackers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TaskTrackerStatus
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FakeTaskTrackerManager ()
specifier|public
name|FakeTaskTrackerManager
parameter_list|()
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|queueManager
operator|=
operator|new
name|QueueManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|trackers
operator|.
name|put
argument_list|(
literal|"tt1"
argument_list|,
operator|new
name|TaskTrackerStatus
argument_list|(
literal|"tt1"
argument_list|,
literal|"tt1.host"
argument_list|,
literal|1
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|TaskStatus
argument_list|>
argument_list|()
argument_list|,
literal|0
argument_list|,
name|maxMapTasksPerTracker
argument_list|,
name|maxReduceTasksPerTracker
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getClusterStatus ()
specifier|public
name|ClusterStatus
name|getClusterStatus
parameter_list|()
block|{
name|int
name|numTrackers
init|=
name|trackers
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
operator|new
name|ClusterStatus
argument_list|(
name|numTrackers
argument_list|,
literal|0
argument_list|,
literal|10
operator|*
literal|60
operator|*
literal|1000
argument_list|,
name|maps
argument_list|,
name|reduces
argument_list|,
name|numTrackers
operator|*
name|maxMapTasksPerTracker
argument_list|,
name|numTrackers
operator|*
name|maxReduceTasksPerTracker
argument_list|,
name|JobTrackerStatus
operator|.
name|RUNNING
argument_list|)
return|;
block|}
DECL|method|getNumberOfUniqueHosts ()
specifier|public
name|int
name|getNumberOfUniqueHosts
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|taskTrackers ()
specifier|public
name|Collection
argument_list|<
name|TaskTrackerStatus
argument_list|>
name|taskTrackers
parameter_list|()
block|{
return|return
name|trackers
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|addJobInProgressListener (JobInProgressListener listener)
specifier|public
name|void
name|addJobInProgressListener
parameter_list|(
name|JobInProgressListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|removeJobInProgressListener (JobInProgressListener listener)
specifier|public
name|void
name|removeJobInProgressListener
parameter_list|(
name|JobInProgressListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|getQueueManager ()
specifier|public
name|QueueManager
name|getQueueManager
parameter_list|()
block|{
return|return
name|queueManager
return|;
block|}
DECL|method|getNextHeartbeatInterval ()
specifier|public
name|int
name|getNextHeartbeatInterval
parameter_list|()
block|{
return|return
name|JTConfig
operator|.
name|JT_HEARTBEAT_INTERVAL_MIN_DEFAULT
return|;
block|}
DECL|method|killJob (JobID jobid)
specifier|public
name|void
name|killJob
parameter_list|(
name|JobID
name|jobid
parameter_list|)
block|{
return|return;
block|}
DECL|method|getJob (JobID jobid)
specifier|public
name|JobInProgress
name|getJob
parameter_list|(
name|JobID
name|jobid
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|killTask (TaskAttemptID attemptId, boolean shouldFail)
specifier|public
name|boolean
name|killTask
parameter_list|(
name|TaskAttemptID
name|attemptId
parameter_list|,
name|boolean
name|shouldFail
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
DECL|method|initJob (JobInProgress job)
specifier|public
name|void
name|initJob
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
block|{
try|try
block|{
name|JobStatus
name|prevStatus
init|=
operator|(
name|JobStatus
operator|)
name|job
operator|.
name|getStatus
argument_list|()
operator|.
name|clone
argument_list|()
decl_stmt|;
name|job
operator|.
name|initTasks
argument_list|()
expr_stmt|;
if|if
condition|(
name|job
operator|.
name|isJobEmpty
argument_list|()
condition|)
block|{
name|completeEmptyJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|job
operator|.
name|isSetupCleanupRequired
argument_list|()
condition|)
block|{
name|job
operator|.
name|completeSetup
argument_list|()
expr_stmt|;
block|}
name|JobStatus
name|newStatus
init|=
operator|(
name|JobStatus
operator|)
name|job
operator|.
name|getStatus
argument_list|()
operator|.
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|prevStatus
operator|.
name|getRunState
argument_list|()
operator|!=
name|newStatus
operator|.
name|getRunState
argument_list|()
condition|)
block|{
name|JobStatusChangeEvent
name|event
init|=
operator|new
name|JobStatusChangeEvent
argument_list|(
name|job
argument_list|,
name|EventType
operator|.
name|RUN_STATE_CHANGED
argument_list|,
name|prevStatus
argument_list|,
name|newStatus
argument_list|)
decl_stmt|;
for|for
control|(
name|JobInProgressListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|jobUpdated
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|KillInterruptedException
name|kie
parameter_list|)
block|{
name|killJob
argument_list|(
name|job
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|failJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|completeEmptyJob (JobInProgress job)
specifier|private
specifier|synchronized
name|void
name|completeEmptyJob
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
block|{
name|job
operator|.
name|completeEmptyJob
argument_list|()
expr_stmt|;
block|}
DECL|method|failJob (JobInProgress job)
specifier|public
specifier|synchronized
name|void
name|failJob
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
block|{
name|JobStatus
name|prevStatus
init|=
operator|(
name|JobStatus
operator|)
name|job
operator|.
name|getStatus
argument_list|()
operator|.
name|clone
argument_list|()
decl_stmt|;
name|job
operator|.
name|fail
argument_list|()
expr_stmt|;
name|JobStatus
name|newStatus
init|=
operator|(
name|JobStatus
operator|)
name|job
operator|.
name|getStatus
argument_list|()
operator|.
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|prevStatus
operator|.
name|getRunState
argument_list|()
operator|!=
name|newStatus
operator|.
name|getRunState
argument_list|()
condition|)
block|{
name|JobStatusChangeEvent
name|event
init|=
operator|new
name|JobStatusChangeEvent
argument_list|(
name|job
argument_list|,
name|EventType
operator|.
name|RUN_STATE_CHANGED
argument_list|,
name|prevStatus
argument_list|,
name|newStatus
argument_list|)
decl_stmt|;
for|for
control|(
name|JobInProgressListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|jobUpdated
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Test methods
DECL|method|submitJob (JobInProgress job)
specifier|public
name|void
name|submitJob
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|JobInProgressListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|jobAdded
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|jobConf
specifier|protected
name|JobConf
name|jobConf
decl_stmt|;
DECL|field|scheduler
specifier|protected
name|TaskScheduler
name|scheduler
decl_stmt|;
DECL|field|taskTrackerManager
specifier|private
name|FakeTaskTrackerManager
name|taskTrackerManager
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|resetCounters
argument_list|()
expr_stmt|;
name|jobConf
operator|=
operator|new
name|JobConf
argument_list|()
expr_stmt|;
name|taskTrackerManager
operator|=
operator|new
name|FakeTaskTrackerManager
argument_list|()
expr_stmt|;
name|scheduler
operator|=
name|createTaskScheduler
argument_list|()
expr_stmt|;
name|scheduler
operator|.
name|setConf
argument_list|(
name|jobConf
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|setTaskTrackerManager
argument_list|(
name|taskTrackerManager
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|scheduler
operator|!=
literal|null
condition|)
block|{
name|scheduler
operator|.
name|terminate
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createTaskScheduler ()
specifier|protected
name|TaskScheduler
name|createTaskScheduler
parameter_list|()
block|{
return|return
operator|new
name|JobQueueTaskScheduler
argument_list|()
return|;
block|}
DECL|method|testParallelInitJobs ()
specifier|public
name|void
name|testParallelInitJobs
parameter_list|()
throws|throws
name|IOException
block|{
name|FakeJobInProgress
index|[]
name|jobs
init|=
operator|new
name|FakeJobInProgress
index|[
name|NUM_JOBS
index|]
decl_stmt|;
comment|// Submit NUM_JOBS jobs in order. The init code will ensure
comment|// that the jobs get inited in descending order of Job ids
comment|// i.e. highest job id first and the smallest last.
comment|// If we were not doing parallel init, the first submitted job
comment|// will be inited first and that will hang
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_JOBS
condition|;
name|i
operator|++
control|)
block|{
name|jobs
index|[
name|i
index|]
operator|=
operator|new
name|FakeJobInProgress
argument_list|(
name|jobConf
argument_list|,
name|taskTrackerManager
argument_list|,
name|UtilsForTests
operator|.
name|getJobTracker
argument_list|()
argument_list|)
expr_stmt|;
name|jobs
index|[
name|i
index|]
operator|.
name|getStatus
argument_list|()
operator|.
name|setRunState
argument_list|(
name|JobStatus
operator|.
name|PREP
argument_list|)
expr_stmt|;
name|taskTrackerManager
operator|.
name|submitJob
argument_list|(
name|jobs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_JOBS
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|jobs
index|[
name|i
index|]
operator|.
name|getStatus
argument_list|()
operator|.
name|getRunState
argument_list|()
operator|==
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

