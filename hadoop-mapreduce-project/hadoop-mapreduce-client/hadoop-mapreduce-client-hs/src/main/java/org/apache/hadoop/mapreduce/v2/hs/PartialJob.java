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
name|mapreduce
operator|.
name|Counters
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
import|;
end_import

begin_class
DECL|class|PartialJob
specifier|public
class|class
name|PartialJob
implements|implements
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
name|PartialJob
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|jobIndexInfo
specifier|private
name|JobIndexInfo
name|jobIndexInfo
init|=
literal|null
decl_stmt|;
DECL|field|jobId
specifier|private
name|JobId
name|jobId
init|=
literal|null
decl_stmt|;
DECL|field|jobReport
specifier|private
name|JobReport
name|jobReport
init|=
literal|null
decl_stmt|;
DECL|method|PartialJob (JobIndexInfo jobIndexInfo, JobId jobId)
specifier|public
name|PartialJob
parameter_list|(
name|JobIndexInfo
name|jobIndexInfo
parameter_list|,
name|JobId
name|jobId
parameter_list|)
block|{
name|this
operator|.
name|jobIndexInfo
operator|=
name|jobIndexInfo
expr_stmt|;
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
name|jobReport
operator|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|JobReport
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setStartTime
argument_list|(
name|jobIndexInfo
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setFinishTime
argument_list|(
name|jobIndexInfo
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|jobReport
operator|.
name|setJobState
argument_list|(
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getID ()
specifier|public
name|JobId
name|getID
parameter_list|()
block|{
comment|//    return jobIndexInfo.getJobId();
return|return
name|this
operator|.
name|jobId
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
name|jobIndexInfo
operator|.
name|getJobName
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
name|jobIndexInfo
operator|.
name|getQueueName
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
name|JobState
name|js
init|=
literal|null
decl_stmt|;
try|try
block|{
name|js
operator|=
name|JobState
operator|.
name|valueOf
argument_list|(
name|jobIndexInfo
operator|.
name|getJobStatus
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
comment|// Meant for use by the display UI. Exception would prevent it from being
comment|// rendered.e Defaulting to KILLED
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while parsing job state. Defaulting to KILLED"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|js
operator|=
name|JobState
operator|.
name|KILLED
expr_stmt|;
block|}
return|return
name|js
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
name|jobReport
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
literal|1.0f
return|;
block|}
annotation|@
name|Override
DECL|method|getAllCounters ()
specifier|public
name|Counters
name|getAllCounters
parameter_list|()
block|{
return|return
literal|null
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
literal|null
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
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getTask (TaskId taskID)
specifier|public
name|Task
name|getTask
parameter_list|(
name|TaskId
name|taskID
parameter_list|)
block|{
return|return
literal|null
return|;
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
literal|null
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
name|jobIndexInfo
operator|.
name|getNumMaps
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
name|jobIndexInfo
operator|.
name|getNumReduces
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCompletedMaps ()
specifier|public
name|int
name|getCompletedMaps
parameter_list|()
block|{
return|return
name|jobIndexInfo
operator|.
name|getNumMaps
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
return|return
name|jobIndexInfo
operator|.
name|getNumReduces
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
literal|false
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
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|checkAccess (UserGroupInformation callerUGI, JobACL jobOperation)
specifier|public
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
literal|true
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
name|jobIndexInfo
operator|.
name|getUser
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
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not implemented yet"
argument_list|)
throw|;
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
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not implemented yet"
argument_list|)
throw|;
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
literal|null
return|;
block|}
block|}
end_class

end_unit

