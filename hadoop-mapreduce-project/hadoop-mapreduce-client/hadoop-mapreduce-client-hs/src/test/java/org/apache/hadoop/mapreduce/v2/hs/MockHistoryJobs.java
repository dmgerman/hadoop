begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
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
name|hs
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
name|TaskCompletionEvent
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
name|JobACL
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
name|JobReport
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
name|JobState
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
name|TaskAttemptCompletionEvent
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
name|MockJobs
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
name|jobhistory
operator|.
name|JobIndexInfo
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_class
DECL|class|MockHistoryJobs
specifier|public
class|class
name|MockHistoryJobs
extends|extends
name|MockJobs
block|{
DECL|class|JobsPair
specifier|public
specifier|static
class|class
name|JobsPair
block|{
DECL|field|partial
specifier|public
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|partial
decl_stmt|;
DECL|field|full
specifier|public
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|full
decl_stmt|;
block|}
DECL|method|newHistoryJobs (int numJobs, int numTasksPerJob, int numAttemptsPerTask)
specifier|public
specifier|static
name|JobsPair
name|newHistoryJobs
parameter_list|(
name|int
name|numJobs
parameter_list|,
name|int
name|numTasksPerJob
parameter_list|,
name|int
name|numAttemptsPerTask
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|mocked
init|=
name|newJobs
argument_list|(
name|numJobs
argument_list|,
name|numTasksPerJob
argument_list|,
name|numAttemptsPerTask
argument_list|)
decl_stmt|;
return|return
name|split
argument_list|(
name|mocked
argument_list|)
return|;
block|}
DECL|method|newHistoryJobs (ApplicationId appID, int numJobsPerApp, int numTasksPerJob, int numAttemptsPerTask)
specifier|public
specifier|static
name|JobsPair
name|newHistoryJobs
parameter_list|(
name|ApplicationId
name|appID
parameter_list|,
name|int
name|numJobsPerApp
parameter_list|,
name|int
name|numTasksPerJob
parameter_list|,
name|int
name|numAttemptsPerTask
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|mocked
init|=
name|newJobs
argument_list|(
name|appID
argument_list|,
name|numJobsPerApp
argument_list|,
name|numTasksPerJob
argument_list|,
name|numAttemptsPerTask
argument_list|)
decl_stmt|;
return|return
name|split
argument_list|(
name|mocked
argument_list|)
return|;
block|}
DECL|method|newHistoryJobs (ApplicationId appID, int numJobsPerApp, int numTasksPerJob, int numAttemptsPerTask, boolean hasFailedTasks)
specifier|public
specifier|static
name|JobsPair
name|newHistoryJobs
parameter_list|(
name|ApplicationId
name|appID
parameter_list|,
name|int
name|numJobsPerApp
parameter_list|,
name|int
name|numTasksPerJob
parameter_list|,
name|int
name|numAttemptsPerTask
parameter_list|,
name|boolean
name|hasFailedTasks
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|mocked
init|=
name|newJobs
argument_list|(
name|appID
argument_list|,
name|numJobsPerApp
argument_list|,
name|numTasksPerJob
argument_list|,
name|numAttemptsPerTask
argument_list|,
name|hasFailedTasks
argument_list|)
decl_stmt|;
return|return
name|split
argument_list|(
name|mocked
argument_list|)
return|;
block|}
DECL|method|split (Map<JobId, Job> mocked)
specifier|private
specifier|static
name|JobsPair
name|split
parameter_list|(
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|mocked
parameter_list|)
throws|throws
name|IOException
block|{
name|JobsPair
name|ret
init|=
operator|new
name|JobsPair
argument_list|()
decl_stmt|;
name|ret
operator|.
name|full
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|ret
operator|.
name|partial
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|entry
range|:
name|mocked
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|JobId
name|id
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Job
name|j
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|MockCompletedJob
name|mockJob
init|=
operator|new
name|MockCompletedJob
argument_list|(
name|j
argument_list|)
decl_stmt|;
comment|// use MockCompletedJob to set everything below to make sure
comment|// consistent with what history server would do
name|ret
operator|.
name|full
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|mockJob
argument_list|)
expr_stmt|;
name|JobReport
name|report
init|=
name|mockJob
operator|.
name|getReport
argument_list|()
decl_stmt|;
name|JobIndexInfo
name|info
init|=
operator|new
name|JobIndexInfo
argument_list|(
name|report
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|report
operator|.
name|getFinishTime
argument_list|()
argument_list|,
name|mockJob
operator|.
name|getUserName
argument_list|()
argument_list|,
name|mockJob
operator|.
name|getName
argument_list|()
argument_list|,
name|id
argument_list|,
name|mockJob
operator|.
name|getCompletedMaps
argument_list|()
argument_list|,
name|mockJob
operator|.
name|getCompletedReduces
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|mockJob
operator|.
name|getState
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|info
operator|.
name|setQueueName
argument_list|(
name|mockJob
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|partial
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|PartialJob
argument_list|(
name|info
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|class|MockCompletedJob
specifier|private
specifier|static
class|class
name|MockCompletedJob
extends|extends
name|CompletedJob
block|{
DECL|field|job
specifier|private
name|Job
name|job
decl_stmt|;
DECL|method|MockCompletedJob (Job job)
specifier|public
name|MockCompletedJob
parameter_list|(
name|Job
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|job
operator|.
name|getID
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|job
operator|.
name|getUserName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCompletedMaps ()
specifier|public
name|int
name|getCompletedMaps
parameter_list|()
block|{
comment|// we always return total since this is history server
comment|// and PartialJob also assumes completed - total
return|return
name|job
operator|.
name|getTotalMaps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCompletedReduces ()
specifier|public
name|int
name|getCompletedReduces
parameter_list|()
block|{
comment|// we always return total since this is history server
comment|// and PartialJob also assumes completed - total
return|return
name|job
operator|.
name|getTotalReduces
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAllCounters ()
specifier|public
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Counters
name|getAllCounters
parameter_list|()
block|{
return|return
name|job
operator|.
name|getAllCounters
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getID ()
specifier|public
name|JobId
name|getID
parameter_list|()
block|{
return|return
name|job
operator|.
name|getID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReport ()
specifier|public
name|JobReport
name|getReport
parameter_list|()
block|{
return|return
name|job
operator|.
name|getReport
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|job
operator|.
name|getProgress
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|JobState
name|getState
parameter_list|()
block|{
return|return
name|job
operator|.
name|getState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTask (TaskId taskId)
specifier|public
name|Task
name|getTask
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
block|{
return|return
name|job
operator|.
name|getTask
argument_list|(
name|taskId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptCompletionEvents ( int fromEventId, int maxEvents)
specifier|public
name|TaskAttemptCompletionEvent
index|[]
name|getTaskAttemptCompletionEvents
parameter_list|(
name|int
name|fromEventId
parameter_list|,
name|int
name|maxEvents
parameter_list|)
block|{
return|return
name|job
operator|.
name|getTaskAttemptCompletionEvents
argument_list|(
name|fromEventId
argument_list|,
name|maxEvents
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMapAttemptCompletionEvents ( int startIndex, int maxEvents)
specifier|public
name|TaskCompletionEvent
index|[]
name|getMapAttemptCompletionEvents
parameter_list|(
name|int
name|startIndex
parameter_list|,
name|int
name|maxEvents
parameter_list|)
block|{
return|return
name|job
operator|.
name|getMapAttemptCompletionEvents
argument_list|(
name|startIndex
argument_list|,
name|maxEvents
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTasks ()
specifier|public
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|getTasks
parameter_list|()
block|{
return|return
name|job
operator|.
name|getTasks
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|loadFullHistoryData (boolean loadTasks, Path historyFileAbsolute)
specifier|protected
name|void
name|loadFullHistoryData
parameter_list|(
name|boolean
name|loadTasks
parameter_list|,
name|Path
name|historyFileAbsolute
parameter_list|)
throws|throws
name|IOException
block|{
comment|//Empty
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
block|{
return|return
name|job
operator|.
name|getDiagnostics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|job
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|job
operator|.
name|getQueueName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalMaps ()
specifier|public
name|int
name|getTotalMaps
parameter_list|()
block|{
return|return
name|job
operator|.
name|getTotalMaps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalReduces ()
specifier|public
name|int
name|getTotalReduces
parameter_list|()
block|{
return|return
name|job
operator|.
name|getTotalReduces
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isUber ()
specifier|public
name|boolean
name|isUber
parameter_list|()
block|{
return|return
name|job
operator|.
name|isUber
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTasks (TaskType taskType)
specifier|public
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|getTasks
parameter_list|(
name|TaskType
name|taskType
parameter_list|)
block|{
return|return
name|job
operator|.
name|getTasks
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
DECL|method|checkAccess (UserGroupInformation callerUGI, JobACL jobOperation)
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|JobACL
name|jobOperation
parameter_list|)
block|{
return|return
name|job
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|jobOperation
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getJobACLs ()
specifier|public
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|getJobACLs
parameter_list|()
block|{
return|return
name|job
operator|.
name|getJobACLs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUserName ()
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|job
operator|.
name|getUserName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getConfFile ()
specifier|public
name|Path
name|getConfFile
parameter_list|()
block|{
return|return
name|job
operator|.
name|getConfFile
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
return|return
name|job
operator|.
name|getAMInfos
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

