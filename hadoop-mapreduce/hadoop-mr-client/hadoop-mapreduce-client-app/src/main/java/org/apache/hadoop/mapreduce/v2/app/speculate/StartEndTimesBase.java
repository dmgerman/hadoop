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
name|ConcurrentHashMap
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

begin_class
DECL|class|StartEndTimesBase
specifier|abstract
class|class
name|StartEndTimesBase
implements|implements
name|TaskRuntimeEstimator
block|{
DECL|field|MINIMUM_COMPLETE_PROPORTION_TO_SPECULATE
specifier|static
specifier|final
name|float
name|MINIMUM_COMPLETE_PROPORTION_TO_SPECULATE
init|=
literal|0.05F
decl_stmt|;
DECL|field|MINIMUM_COMPLETE_NUMBER_TO_SPECULATE
specifier|static
specifier|final
name|int
name|MINIMUM_COMPLETE_NUMBER_TO_SPECULATE
init|=
literal|1
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|context
specifier|protected
name|AppContext
name|context
init|=
literal|null
decl_stmt|;
DECL|field|startTimes
specifier|protected
specifier|final
name|Map
argument_list|<
name|TaskAttemptId
argument_list|,
name|Long
argument_list|>
name|startTimes
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|TaskAttemptId
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
comment|// XXXX This class design assumes that the contents of AppContext.getAllJobs
comment|//   never changes.  Is that right?
comment|//
comment|// This assumption comes in in several places, mostly in data structure that
comment|//   can grow without limit if a AppContext gets new Job's when the old ones
comment|//   run out.  Also, these mapper statistics blocks won't cover the Job's
comment|//   we don't know about.
DECL|field|mapperStatistics
specifier|protected
specifier|final
name|Map
argument_list|<
name|Job
argument_list|,
name|DataStatistics
argument_list|>
name|mapperStatistics
init|=
operator|new
name|HashMap
argument_list|<
name|Job
argument_list|,
name|DataStatistics
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reducerStatistics
specifier|protected
specifier|final
name|Map
argument_list|<
name|Job
argument_list|,
name|DataStatistics
argument_list|>
name|reducerStatistics
init|=
operator|new
name|HashMap
argument_list|<
name|Job
argument_list|,
name|DataStatistics
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|slowTaskRelativeTresholds
specifier|private
specifier|final
name|Map
argument_list|<
name|Job
argument_list|,
name|Float
argument_list|>
name|slowTaskRelativeTresholds
init|=
operator|new
name|HashMap
argument_list|<
name|Job
argument_list|,
name|Float
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|doneTasks
specifier|protected
specifier|final
name|Set
argument_list|<
name|Task
argument_list|>
name|doneTasks
init|=
operator|new
name|HashSet
argument_list|<
name|Task
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|enrollAttempt (TaskAttemptStatus status, long timestamp)
specifier|public
name|void
name|enrollAttempt
parameter_list|(
name|TaskAttemptStatus
name|status
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|startTimes
operator|.
name|put
argument_list|(
name|status
operator|.
name|id
argument_list|,
name|timestamp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|attemptEnrolledTime (TaskAttemptId attemptID)
specifier|public
name|long
name|attemptEnrolledTime
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
block|{
name|Long
name|result
init|=
name|startTimes
operator|.
name|get
argument_list|(
name|attemptID
argument_list|)
decl_stmt|;
return|return
name|result
operator|==
literal|null
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|contextualize (Configuration conf, AppContext context)
specifier|public
name|void
name|contextualize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
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
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|allJobs
init|=
name|context
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
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
name|allJobs
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Job
name|job
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|mapperStatistics
operator|.
name|put
argument_list|(
name|job
argument_list|,
operator|new
name|DataStatistics
argument_list|()
argument_list|)
expr_stmt|;
name|reducerStatistics
operator|.
name|put
argument_list|(
name|job
argument_list|,
operator|new
name|DataStatistics
argument_list|()
argument_list|)
expr_stmt|;
name|slowTaskRelativeTresholds
operator|.
name|put
argument_list|(
name|job
argument_list|,
name|conf
operator|.
name|getFloat
argument_list|(
name|MRJobConfig
operator|.
name|SPECULATIVE_SLOWTASK_THRESHOLD
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dataStatisticsForTask (TaskId taskID)
specifier|protected
name|DataStatistics
name|dataStatisticsForTask
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
name|Job
name|job
init|=
name|context
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
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
return|return
literal|null
return|;
block|}
return|return
name|task
operator|.
name|getType
argument_list|()
operator|==
name|TaskType
operator|.
name|MAP
condition|?
name|mapperStatistics
operator|.
name|get
argument_list|(
name|job
argument_list|)
else|:
name|task
operator|.
name|getType
argument_list|()
operator|==
name|TaskType
operator|.
name|REDUCE
condition|?
name|reducerStatistics
operator|.
name|get
argument_list|(
name|job
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|thresholdRuntime (TaskId taskID)
specifier|public
name|long
name|thresholdRuntime
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
name|Job
name|job
init|=
name|context
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
name|TaskType
name|type
init|=
name|taskID
operator|.
name|getTaskType
argument_list|()
decl_stmt|;
name|DataStatistics
name|statistics
init|=
name|dataStatisticsForTask
argument_list|(
name|taskID
argument_list|)
decl_stmt|;
name|int
name|completedTasksOfType
init|=
name|type
operator|==
name|TaskType
operator|.
name|MAP
condition|?
name|job
operator|.
name|getCompletedMaps
argument_list|()
else|:
name|job
operator|.
name|getCompletedReduces
argument_list|()
decl_stmt|;
name|int
name|totalTasksOfType
init|=
name|type
operator|==
name|TaskType
operator|.
name|MAP
condition|?
name|job
operator|.
name|getTotalMaps
argument_list|()
else|:
name|job
operator|.
name|getTotalReduces
argument_list|()
decl_stmt|;
if|if
condition|(
name|completedTasksOfType
operator|<
name|MINIMUM_COMPLETE_NUMBER_TO_SPECULATE
operator|||
operator|(
operator|(
operator|(
name|float
operator|)
name|completedTasksOfType
operator|)
operator|/
name|totalTasksOfType
operator|)
operator|<
name|MINIMUM_COMPLETE_PROPORTION_TO_SPECULATE
condition|)
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
name|long
name|result
init|=
name|statistics
operator|==
literal|null
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
operator|(
name|long
operator|)
name|statistics
operator|.
name|outlier
argument_list|(
name|slowTaskRelativeTresholds
operator|.
name|get
argument_list|(
name|job
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|estimatedNewAttemptRuntime (TaskId id)
specifier|public
name|long
name|estimatedNewAttemptRuntime
parameter_list|(
name|TaskId
name|id
parameter_list|)
block|{
name|DataStatistics
name|statistics
init|=
name|dataStatisticsForTask
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|statistics
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1L
return|;
block|}
return|return
operator|(
name|long
operator|)
name|statistics
operator|.
name|mean
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|updateAttempt (TaskAttemptStatus status, long timestamp)
specifier|public
name|void
name|updateAttempt
parameter_list|(
name|TaskAttemptStatus
name|status
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|TaskAttemptId
name|attemptID
init|=
name|status
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
name|JobId
name|jobID
init|=
name|taskID
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|context
operator|.
name|getJob
argument_list|(
name|jobID
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
name|Long
name|boxedStart
init|=
name|startTimes
operator|.
name|get
argument_list|(
name|attemptID
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|boxedStart
operator|==
literal|null
condition|?
name|Long
operator|.
name|MIN_VALUE
else|:
name|boxedStart
decl_stmt|;
name|TaskAttempt
name|taskAttempt
init|=
name|task
operator|.
name|getAttempt
argument_list|(
name|attemptID
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskAttempt
operator|.
name|getState
argument_list|()
operator|==
name|TaskAttemptState
operator|.
name|SUCCEEDED
condition|)
block|{
name|boolean
name|isNew
init|=
literal|false
decl_stmt|;
comment|// is this  a new success?
synchronized|synchronized
init|(
name|doneTasks
init|)
block|{
if|if
condition|(
operator|!
name|doneTasks
operator|.
name|contains
argument_list|(
name|task
argument_list|)
condition|)
block|{
name|doneTasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|isNew
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// It's a new completion
comment|// Note that if a task completes twice [because of a previous speculation
comment|//  and a race, or a success followed by loss of the machine with the
comment|//  local data] we only count the first one.
if|if
condition|(
name|isNew
condition|)
block|{
name|long
name|finish
init|=
name|timestamp
decl_stmt|;
if|if
condition|(
name|start
operator|>
literal|1L
operator|&&
name|finish
operator|>
literal|1L
operator|&&
name|start
operator|<=
name|finish
condition|)
block|{
name|long
name|duration
init|=
name|finish
operator|-
name|start
decl_stmt|;
name|DataStatistics
name|statistics
init|=
name|dataStatisticsForTask
argument_list|(
name|taskID
argument_list|)
decl_stmt|;
if|if
condition|(
name|statistics
operator|!=
literal|null
condition|)
block|{
name|statistics
operator|.
name|add
argument_list|(
name|duration
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

