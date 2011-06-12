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
name|BytesWritable
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
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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
name|TaskTracker
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
import|;
end_import

begin_class
DECL|class|TestJobQueueTaskScheduler
specifier|public
class|class
name|TestJobQueueTaskScheduler
extends|extends
name|TestCase
block|{
DECL|field|jobCounter
specifier|private
specifier|static
name|int
name|jobCounter
decl_stmt|;
DECL|field|taskCounter
specifier|private
specifier|static
name|int
name|taskCounter
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
name|taskCounter
operator|=
literal|0
expr_stmt|;
block|}
DECL|class|FakeJobInProgress
specifier|static
class|class
name|FakeJobInProgress
extends|extends
name|JobInProgress
block|{
DECL|field|taskTrackerManager
specifier|private
name|FakeTaskTrackerManager
name|taskTrackerManager
decl_stmt|;
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
name|taskTrackerManager
operator|=
name|taskTrackerManager
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
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|obtainNewLocalMapTask (TaskTrackerStatus tts, int clusterSize, int ignored)
specifier|public
name|Task
name|obtainNewLocalMapTask
parameter_list|(
name|TaskTrackerStatus
name|tts
parameter_list|,
name|int
name|clusterSize
parameter_list|,
name|int
name|ignored
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|obtainNewMapTask
argument_list|(
name|tts
argument_list|,
name|clusterSize
argument_list|,
name|ignored
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|obtainNewNonLocalMapTask (TaskTrackerStatus tts, int clusterSize, int ignored)
specifier|public
name|Task
name|obtainNewNonLocalMapTask
parameter_list|(
name|TaskTrackerStatus
name|tts
parameter_list|,
name|int
name|clusterSize
parameter_list|,
name|int
name|ignored
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|obtainNewMapTask
argument_list|(
name|tts
argument_list|,
name|clusterSize
argument_list|,
name|ignored
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|obtainNewMapTask (final TaskTrackerStatus tts, int clusterSize, int ignored)
specifier|public
name|Task
name|obtainNewMapTask
parameter_list|(
specifier|final
name|TaskTrackerStatus
name|tts
parameter_list|,
name|int
name|clusterSize
parameter_list|,
name|int
name|ignored
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttemptID
name|attemptId
init|=
name|getTaskAttemptID
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|Task
name|task
init|=
operator|new
name|MapTask
argument_list|(
literal|""
argument_list|,
name|attemptId
argument_list|,
literal|0
argument_list|,
operator|new
name|JobSplit
operator|.
name|TaskSplitIndex
argument_list|()
argument_list|,
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s on %s"
argument_list|,
name|getTaskID
argument_list|()
argument_list|,
name|tts
operator|.
name|getTrackerName
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|taskTrackerManager
operator|.
name|update
argument_list|(
name|tts
operator|.
name|getTrackerName
argument_list|()
argument_list|,
name|task
argument_list|)
expr_stmt|;
name|runningMapTasks
operator|++
expr_stmt|;
return|return
name|task
return|;
block|}
annotation|@
name|Override
DECL|method|obtainNewReduceTask (final TaskTrackerStatus tts, int clusterSize, int ignored)
specifier|public
name|Task
name|obtainNewReduceTask
parameter_list|(
specifier|final
name|TaskTrackerStatus
name|tts
parameter_list|,
name|int
name|clusterSize
parameter_list|,
name|int
name|ignored
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttemptID
name|attemptId
init|=
name|getTaskAttemptID
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
decl_stmt|;
name|Task
name|task
init|=
operator|new
name|ReduceTask
argument_list|(
literal|""
argument_list|,
name|attemptId
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s on %s"
argument_list|,
name|getTaskID
argument_list|()
argument_list|,
name|tts
operator|.
name|getTrackerName
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|taskTrackerManager
operator|.
name|update
argument_list|(
name|tts
operator|.
name|getTrackerName
argument_list|()
argument_list|,
name|task
argument_list|)
expr_stmt|;
name|runningReduceTasks
operator|++
expr_stmt|;
return|return
name|task
return|;
block|}
DECL|method|getTaskAttemptID (TaskType type)
specifier|private
name|TaskAttemptID
name|getTaskAttemptID
parameter_list|(
name|TaskType
name|type
parameter_list|)
block|{
name|JobID
name|jobId
init|=
name|getJobID
argument_list|()
decl_stmt|;
return|return
operator|new
name|TaskAttemptID
argument_list|(
name|jobId
operator|.
name|getJtIdentifier
argument_list|()
argument_list|,
name|jobId
operator|.
name|getId
argument_list|()
argument_list|,
name|type
argument_list|,
operator|++
name|taskCounter
argument_list|,
literal|0
argument_list|)
return|;
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
name|TaskTracker
argument_list|>
name|trackers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TaskTracker
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
name|TaskTracker
name|tt1
init|=
operator|new
name|TaskTracker
argument_list|(
literal|"tt1"
argument_list|)
decl_stmt|;
name|tt1
operator|.
name|setStatus
argument_list|(
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
name|trackers
operator|.
name|put
argument_list|(
literal|"tt1"
argument_list|,
name|tt1
argument_list|)
expr_stmt|;
name|TaskTracker
name|tt2
init|=
operator|new
name|TaskTracker
argument_list|(
literal|"tt2"
argument_list|)
decl_stmt|;
name|tt2
operator|.
name|setStatus
argument_list|(
operator|new
name|TaskTrackerStatus
argument_list|(
literal|"tt2"
argument_list|,
literal|"tt2.host"
argument_list|,
literal|2
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
name|trackers
operator|.
name|put
argument_list|(
literal|"tt2"
argument_list|,
name|tt2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|taskTrackers ()
specifier|public
name|Collection
argument_list|<
name|TaskTrackerStatus
argument_list|>
name|taskTrackers
parameter_list|()
block|{
name|List
argument_list|<
name|TaskTrackerStatus
argument_list|>
name|statuses
init|=
operator|new
name|ArrayList
argument_list|<
name|TaskTrackerStatus
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskTracker
name|tt
range|:
name|trackers
operator|.
name|values
argument_list|()
control|)
block|{
name|statuses
operator|.
name|add
argument_list|(
name|tt
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|statuses
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
comment|// do nothing
block|}
DECL|method|failJob (JobInProgress job)
specifier|public
name|void
name|failJob
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
block|{
comment|// do nothing
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
DECL|method|getTaskTracker (String trackerID)
specifier|public
name|TaskTracker
name|getTaskTracker
parameter_list|(
name|String
name|trackerID
parameter_list|)
block|{
return|return
name|trackers
operator|.
name|get
argument_list|(
name|trackerID
argument_list|)
return|;
block|}
DECL|method|update (String taskTrackerName, final Task t)
specifier|public
name|void
name|update
parameter_list|(
name|String
name|taskTrackerName
parameter_list|,
specifier|final
name|Task
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|.
name|isMapTask
argument_list|()
condition|)
block|{
name|maps
operator|++
expr_stmt|;
block|}
else|else
block|{
name|reduces
operator|++
expr_stmt|;
block|}
name|TaskStatus
name|status
init|=
operator|new
name|TaskStatus
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|getIsMap
parameter_list|()
block|{
return|return
name|t
operator|.
name|isMapTask
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addFetchFailedMap
parameter_list|(
name|TaskAttemptID
name|mapTaskId
parameter_list|)
block|{                    }
block|}
decl_stmt|;
name|status
operator|.
name|setRunState
argument_list|(
name|TaskStatus
operator|.
name|State
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|trackers
operator|.
name|get
argument_list|(
name|taskTrackerName
argument_list|)
operator|.
name|getStatus
argument_list|()
operator|.
name|getTaskReports
argument_list|()
operator|.
name|add
argument_list|(
name|status
argument_list|)
expr_stmt|;
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
name|jobConf
operator|.
name|setNumMapTasks
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setNumReduceTasks
argument_list|(
literal|10
argument_list|)
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
DECL|method|submitJobs (FakeTaskTrackerManager taskTrackerManager, JobConf jobConf, int numJobs, int state)
specifier|static
name|void
name|submitJobs
parameter_list|(
name|FakeTaskTrackerManager
name|taskTrackerManager
parameter_list|,
name|JobConf
name|jobConf
parameter_list|,
name|int
name|numJobs
parameter_list|,
name|int
name|state
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numJobs
condition|;
name|i
operator|++
control|)
block|{
name|JobInProgress
name|job
init|=
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
decl_stmt|;
name|job
operator|.
name|getStatus
argument_list|()
operator|.
name|setRunState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|taskTrackerManager
operator|.
name|submitJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTaskNotAssignedWhenNoJobsArePresent ()
specifier|public
name|void
name|testTaskNotAssignedWhenNoJobsArePresent
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|scheduler
operator|.
name|assignTasks
argument_list|(
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt1"
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonRunningJobsAreIgnored ()
specifier|public
name|void
name|testNonRunningJobsAreIgnored
parameter_list|()
throws|throws
name|IOException
block|{
name|submitJobs
argument_list|(
name|taskTrackerManager
argument_list|,
name|jobConf
argument_list|,
literal|1
argument_list|,
name|JobStatus
operator|.
name|PREP
argument_list|)
expr_stmt|;
name|submitJobs
argument_list|(
name|taskTrackerManager
argument_list|,
name|jobConf
argument_list|,
literal|1
argument_list|,
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
name|submitJobs
argument_list|(
name|taskTrackerManager
argument_list|,
name|jobConf
argument_list|,
literal|1
argument_list|,
name|JobStatus
operator|.
name|FAILED
argument_list|)
expr_stmt|;
name|submitJobs
argument_list|(
name|taskTrackerManager
argument_list|,
name|jobConf
argument_list|,
literal|1
argument_list|,
name|JobStatus
operator|.
name|KILLED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|scheduler
operator|.
name|assignTasks
argument_list|(
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt1"
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultTaskAssignment ()
specifier|public
name|void
name|testDefaultTaskAssignment
parameter_list|()
throws|throws
name|IOException
block|{
name|submitJobs
argument_list|(
name|taskTrackerManager
argument_list|,
name|jobConf
argument_list|,
literal|2
argument_list|,
name|JobStatus
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
comment|// All slots are filled with job 1
name|checkAssignment
argument_list|(
name|scheduler
argument_list|,
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt1"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"attempt_test_0001_m_000001_0 on tt1"
block|,
literal|"attempt_test_0001_m_000002_0 on tt1"
block|,
literal|"attempt_test_0001_r_000003_0 on tt1"
block|}
argument_list|)
expr_stmt|;
name|checkAssignment
argument_list|(
name|scheduler
argument_list|,
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt1"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"attempt_test_0001_r_000004_0 on tt1"
block|}
argument_list|)
expr_stmt|;
name|checkAssignment
argument_list|(
name|scheduler
argument_list|,
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt1"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|checkAssignment
argument_list|(
name|scheduler
argument_list|,
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt2"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"attempt_test_0001_m_000005_0 on tt2"
block|,
literal|"attempt_test_0001_m_000006_0 on tt2"
block|,
literal|"attempt_test_0001_r_000007_0 on tt2"
block|}
argument_list|)
expr_stmt|;
name|checkAssignment
argument_list|(
name|scheduler
argument_list|,
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt2"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"attempt_test_0001_r_000008_0 on tt2"
block|}
argument_list|)
expr_stmt|;
name|checkAssignment
argument_list|(
name|scheduler
argument_list|,
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt2"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|checkAssignment
argument_list|(
name|scheduler
argument_list|,
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt1"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|checkAssignment
argument_list|(
name|scheduler
argument_list|,
name|tracker
argument_list|(
name|taskTrackerManager
argument_list|,
literal|"tt2"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|tracker (FakeTaskTrackerManager taskTrackerManager, String taskTrackerName)
specifier|static
name|TaskTracker
name|tracker
parameter_list|(
name|FakeTaskTrackerManager
name|taskTrackerManager
parameter_list|,
name|String
name|taskTrackerName
parameter_list|)
block|{
return|return
name|taskTrackerManager
operator|.
name|getTaskTracker
argument_list|(
name|taskTrackerName
argument_list|)
return|;
block|}
DECL|method|checkAssignment (TaskScheduler scheduler, TaskTracker taskTracker, String[] expectedTaskStrings)
specifier|static
name|void
name|checkAssignment
parameter_list|(
name|TaskScheduler
name|scheduler
parameter_list|,
name|TaskTracker
name|taskTracker
parameter_list|,
name|String
index|[]
name|expectedTaskStrings
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Task
argument_list|>
name|tasks
init|=
name|scheduler
operator|.
name|assignTasks
argument_list|(
name|taskTracker
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tasks
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedTaskStrings
operator|.
name|length
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedTaskStrings
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|expectedTaskStrings
index|[
name|i
index|]
argument_list|,
name|tasks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

