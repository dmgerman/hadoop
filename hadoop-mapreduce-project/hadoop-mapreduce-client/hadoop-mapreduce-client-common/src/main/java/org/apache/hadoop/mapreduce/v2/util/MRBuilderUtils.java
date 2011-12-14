begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.util
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
name|util
package|;
end_package

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
name|util
operator|.
name|Records
import|;
end_import

begin_class
DECL|class|MRBuilderUtils
specifier|public
class|class
name|MRBuilderUtils
block|{
DECL|method|newJobId (ApplicationId appId, int id)
specifier|public
specifier|static
name|JobId
name|newJobId
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|JobId
name|jobId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|JobId
operator|.
name|class
argument_list|)
decl_stmt|;
name|jobId
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|jobId
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|jobId
return|;
block|}
DECL|method|newTaskId (JobId jobId, int id, TaskType taskType)
specifier|public
specifier|static
name|TaskId
name|newTaskId
parameter_list|(
name|JobId
name|jobId
parameter_list|,
name|int
name|id
parameter_list|,
name|TaskType
name|taskType
parameter_list|)
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
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|taskId
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|taskId
operator|.
name|setTaskType
argument_list|(
name|taskType
argument_list|)
expr_stmt|;
return|return
name|taskId
return|;
block|}
DECL|method|newTaskAttemptId (TaskId taskId, int attemptId)
specifier|public
specifier|static
name|TaskAttemptId
name|newTaskAttemptId
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|int
name|attemptId
parameter_list|)
block|{
name|TaskAttemptId
name|taskAttemptId
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|TaskAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|taskAttemptId
operator|.
name|setTaskId
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
name|taskAttemptId
operator|.
name|setId
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
return|return
name|taskAttemptId
return|;
block|}
DECL|method|newJobReport (JobId jobId, String jobName, String userName, JobState state, long submitTime, long startTime, long finishTime, float setupProgress, float mapProgress, float reduceProgress, float cleanupProgress, String jobFile, List<AMInfo> amInfos, boolean isUber)
specifier|public
specifier|static
name|JobReport
name|newJobReport
parameter_list|(
name|JobId
name|jobId
parameter_list|,
name|String
name|jobName
parameter_list|,
name|String
name|userName
parameter_list|,
name|JobState
name|state
parameter_list|,
name|long
name|submitTime
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|finishTime
parameter_list|,
name|float
name|setupProgress
parameter_list|,
name|float
name|mapProgress
parameter_list|,
name|float
name|reduceProgress
parameter_list|,
name|float
name|cleanupProgress
parameter_list|,
name|String
name|jobFile
parameter_list|,
name|List
argument_list|<
name|AMInfo
argument_list|>
name|amInfos
parameter_list|,
name|boolean
name|isUber
parameter_list|)
block|{
name|JobReport
name|report
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|JobReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|report
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|report
operator|.
name|setJobName
argument_list|(
name|jobName
argument_list|)
expr_stmt|;
name|report
operator|.
name|setUser
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|report
operator|.
name|setJobState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|report
operator|.
name|setSubmitTime
argument_list|(
name|submitTime
argument_list|)
expr_stmt|;
name|report
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|report
operator|.
name|setFinishTime
argument_list|(
name|finishTime
argument_list|)
expr_stmt|;
name|report
operator|.
name|setSetupProgress
argument_list|(
name|setupProgress
argument_list|)
expr_stmt|;
name|report
operator|.
name|setCleanupProgress
argument_list|(
name|cleanupProgress
argument_list|)
expr_stmt|;
name|report
operator|.
name|setMapProgress
argument_list|(
name|mapProgress
argument_list|)
expr_stmt|;
name|report
operator|.
name|setReduceProgress
argument_list|(
name|reduceProgress
argument_list|)
expr_stmt|;
name|report
operator|.
name|setJobFile
argument_list|(
name|jobFile
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAMInfos
argument_list|(
name|amInfos
argument_list|)
expr_stmt|;
name|report
operator|.
name|setIsUber
argument_list|(
name|isUber
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
DECL|method|newAMInfo (ApplicationAttemptId appAttemptId, long startTime, ContainerId containerId, String nmHost, int nmPort, int nmHttpPort)
specifier|public
specifier|static
name|AMInfo
name|newAMInfo
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|long
name|startTime
parameter_list|,
name|ContainerId
name|containerId
parameter_list|,
name|String
name|nmHost
parameter_list|,
name|int
name|nmPort
parameter_list|,
name|int
name|nmHttpPort
parameter_list|)
block|{
name|AMInfo
name|amInfo
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|AMInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|amInfo
operator|.
name|setAppAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|amInfo
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|amInfo
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|amInfo
operator|.
name|setNodeManagerHost
argument_list|(
name|nmHost
argument_list|)
expr_stmt|;
name|amInfo
operator|.
name|setNodeManagerPort
argument_list|(
name|nmPort
argument_list|)
expr_stmt|;
name|amInfo
operator|.
name|setNodeManagerHttpPort
argument_list|(
name|nmHttpPort
argument_list|)
expr_stmt|;
return|return
name|amInfo
return|;
block|}
block|}
end_class

end_unit

