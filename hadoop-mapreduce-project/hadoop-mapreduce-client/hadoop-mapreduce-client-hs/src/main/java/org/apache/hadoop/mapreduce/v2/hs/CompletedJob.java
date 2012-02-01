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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|JobACLsManager
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
name|TaskAttemptCompletionEventStatus
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRBuilderUtils
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
name|YarnException
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

begin_comment
comment|/**  * Loads the basic job level data upfront.  * Data from job history file is loaded lazily.  */
end_comment

begin_class
DECL|class|CompletedJob
specifier|public
class|class
name|CompletedJob
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
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CompletedJob
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|counters
specifier|private
specifier|final
name|Counters
name|counters
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|jobId
specifier|private
specifier|final
name|JobId
name|jobId
decl_stmt|;
DECL|field|diagnostics
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|report
specifier|private
specifier|final
name|JobReport
name|report
decl_stmt|;
DECL|field|tasks
specifier|private
specifier|final
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|tasks
init|=
operator|new
name|HashMap
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|mapTasks
specifier|private
specifier|final
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|mapTasks
init|=
operator|new
name|HashMap
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reduceTasks
specifier|private
specifier|final
name|Map
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|reduceTasks
init|=
operator|new
name|HashMap
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|confFile
specifier|private
specifier|final
name|Path
name|confFile
decl_stmt|;
DECL|field|aclsMgr
specifier|private
name|JobACLsManager
name|aclsMgr
decl_stmt|;
DECL|field|completionEvents
specifier|private
name|List
argument_list|<
name|TaskAttemptCompletionEvent
argument_list|>
name|completionEvents
init|=
literal|null
decl_stmt|;
DECL|field|jobInfo
specifier|private
name|JobInfo
name|jobInfo
decl_stmt|;
DECL|method|CompletedJob (Configuration conf, JobId jobId, Path historyFile, boolean loadTasks, String userName, Path confFile, JobACLsManager aclsMgr)
specifier|public
name|CompletedJob
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|JobId
name|jobId
parameter_list|,
name|Path
name|historyFile
parameter_list|,
name|boolean
name|loadTasks
parameter_list|,
name|String
name|userName
parameter_list|,
name|Path
name|confFile
parameter_list|,
name|JobACLsManager
name|aclsMgr
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading job: "
operator|+
name|jobId
operator|+
literal|" from file: "
operator|+
name|historyFile
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
name|jobId
operator|=
name|jobId
expr_stmt|;
name|this
operator|.
name|confFile
operator|=
name|confFile
expr_stmt|;
name|this
operator|.
name|aclsMgr
operator|=
name|aclsMgr
expr_stmt|;
name|loadFullHistoryData
argument_list|(
name|loadTasks
argument_list|,
name|historyFile
argument_list|)
expr_stmt|;
name|user
operator|=
name|userName
expr_stmt|;
name|counters
operator|=
name|jobInfo
operator|.
name|getTotalCounters
argument_list|()
expr_stmt|;
name|diagnostics
operator|.
name|add
argument_list|(
name|jobInfo
operator|.
name|getErrorInfo
argument_list|()
argument_list|)
expr_stmt|;
name|report
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
name|report
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|report
operator|.
name|setJobState
argument_list|(
name|JobState
operator|.
name|valueOf
argument_list|(
name|jobInfo
operator|.
name|getJobStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|report
operator|.
name|setSubmitTime
argument_list|(
name|jobInfo
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setStartTime
argument_list|(
name|jobInfo
operator|.
name|getLaunchTime
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setFinishTime
argument_list|(
name|jobInfo
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setJobName
argument_list|(
name|jobInfo
operator|.
name|getJobname
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setUser
argument_list|(
name|jobInfo
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setMapProgress
argument_list|(
operator|(
name|float
operator|)
name|getCompletedMaps
argument_list|()
operator|/
name|getTotalMaps
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setReduceProgress
argument_list|(
operator|(
name|float
operator|)
name|getCompletedReduces
argument_list|()
operator|/
name|getTotalReduces
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setJobFile
argument_list|(
name|confFile
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setTrackingUrl
argument_list|(
name|JobHistoryUtils
operator|.
name|getHistoryUrl
argument_list|(
name|conf
argument_list|,
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
argument_list|)
operator|.
name|getAppId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|report
operator|.
name|setAMInfos
argument_list|(
name|getAMInfos
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setIsUber
argument_list|(
name|isUber
argument_list|()
argument_list|)
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
return|return
operator|(
name|int
operator|)
name|jobInfo
operator|.
name|getFinishedMaps
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
operator|(
name|int
operator|)
name|jobInfo
operator|.
name|getFinishedReduces
argument_list|()
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
name|counters
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
name|jobId
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
name|report
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
DECL|method|getState ()
specifier|public
name|JobState
name|getState
parameter_list|()
block|{
return|return
name|report
operator|.
name|getJobState
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
name|tasks
operator|.
name|get
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
if|if
condition|(
name|completionEvents
operator|==
literal|null
condition|)
block|{
name|constructTaskAttemptCompletionEvents
argument_list|()
expr_stmt|;
block|}
name|TaskAttemptCompletionEvent
index|[]
name|events
init|=
operator|new
name|TaskAttemptCompletionEvent
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|completionEvents
operator|.
name|size
argument_list|()
operator|>
name|fromEventId
condition|)
block|{
name|int
name|actualMax
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxEvents
argument_list|,
operator|(
name|completionEvents
operator|.
name|size
argument_list|()
operator|-
name|fromEventId
operator|)
argument_list|)
decl_stmt|;
name|events
operator|=
name|completionEvents
operator|.
name|subList
argument_list|(
name|fromEventId
argument_list|,
name|actualMax
operator|+
name|fromEventId
argument_list|)
operator|.
name|toArray
argument_list|(
name|events
argument_list|)
expr_stmt|;
block|}
return|return
name|events
return|;
block|}
DECL|method|constructTaskAttemptCompletionEvents ()
specifier|private
name|void
name|constructTaskAttemptCompletionEvents
parameter_list|()
block|{
name|completionEvents
operator|=
operator|new
name|LinkedList
argument_list|<
name|TaskAttemptCompletionEvent
argument_list|>
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|TaskAttempt
argument_list|>
name|allTaskAttempts
init|=
operator|new
name|LinkedList
argument_list|<
name|TaskAttempt
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TaskId
name|taskId
range|:
name|tasks
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Task
name|task
init|=
name|tasks
operator|.
name|get
argument_list|(
name|taskId
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskAttemptId
name|taskAttemptId
range|:
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|TaskAttempt
name|taskAttempt
init|=
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|get
argument_list|(
name|taskAttemptId
argument_list|)
decl_stmt|;
name|allTaskAttempts
operator|.
name|add
argument_list|(
name|taskAttempt
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|allTaskAttempts
argument_list|,
operator|new
name|Comparator
argument_list|<
name|TaskAttempt
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|TaskAttempt
name|o1
parameter_list|,
name|TaskAttempt
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|getFinishTime
argument_list|()
operator|==
literal|0
operator|||
name|o2
operator|.
name|getFinishTime
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|o1
operator|.
name|getFinishTime
argument_list|()
operator|==
literal|0
operator|&&
name|o2
operator|.
name|getFinishTime
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|o1
operator|.
name|getLaunchTime
argument_list|()
operator|==
literal|0
operator|||
name|o2
operator|.
name|getLaunchTime
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|o1
operator|.
name|getLaunchTime
argument_list|()
operator|==
literal|0
operator|&&
name|o2
operator|.
name|getLaunchTime
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
name|long
name|res
init|=
name|o1
operator|.
name|getLaunchTime
argument_list|()
operator|-
name|o2
operator|.
name|getLaunchTime
argument_list|()
decl_stmt|;
return|return
name|res
operator|>
literal|0
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
block|}
else|else
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|o1
operator|.
name|getLaunchTime
argument_list|()
operator|-
name|o2
operator|.
name|getLaunchTime
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
name|long
name|res
init|=
name|o1
operator|.
name|getFinishTime
argument_list|()
operator|-
name|o2
operator|.
name|getFinishTime
argument_list|()
decl_stmt|;
return|return
name|res
operator|>
literal|0
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
block|}
else|else
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|o1
operator|.
name|getFinishTime
argument_list|()
operator|-
name|o2
operator|.
name|getFinishTime
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|int
name|eventId
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|taskAttempt
range|:
name|allTaskAttempts
control|)
block|{
name|TaskAttemptCompletionEvent
name|tace
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
operator|.
name|newRecordInstance
argument_list|(
name|TaskAttemptCompletionEvent
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|attemptRunTime
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|taskAttempt
operator|.
name|getLaunchTime
argument_list|()
operator|!=
literal|0
operator|&&
name|taskAttempt
operator|.
name|getFinishTime
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|attemptRunTime
operator|=
call|(
name|int
call|)
argument_list|(
name|taskAttempt
operator|.
name|getFinishTime
argument_list|()
operator|-
name|taskAttempt
operator|.
name|getLaunchTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Default to KILLED
name|TaskAttemptCompletionEventStatus
name|taceStatus
init|=
name|TaskAttemptCompletionEventStatus
operator|.
name|KILLED
decl_stmt|;
name|String
name|taStateString
init|=
name|taskAttempt
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|taceStatus
operator|=
name|TaskAttemptCompletionEventStatus
operator|.
name|valueOf
argument_list|(
name|taStateString
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
literal|"Cannot constuct TACEStatus from TaskAtemptState: ["
operator|+
name|taStateString
operator|+
literal|"] for taskAttemptId: ["
operator|+
name|taskAttempt
operator|.
name|getID
argument_list|()
operator|+
literal|"]. Defaulting to KILLED"
argument_list|)
expr_stmt|;
block|}
name|tace
operator|.
name|setAttemptId
argument_list|(
name|taskAttempt
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|tace
operator|.
name|setAttemptRunTime
argument_list|(
name|attemptRunTime
argument_list|)
expr_stmt|;
name|tace
operator|.
name|setEventId
argument_list|(
name|eventId
operator|++
argument_list|)
expr_stmt|;
name|tace
operator|.
name|setMapOutputServerAddress
argument_list|(
name|taskAttempt
operator|.
name|getAssignedContainerMgrAddress
argument_list|()
argument_list|)
expr_stmt|;
name|tace
operator|.
name|setStatus
argument_list|(
name|taceStatus
argument_list|)
expr_stmt|;
name|completionEvents
operator|.
name|add
argument_list|(
name|tace
argument_list|)
expr_stmt|;
block|}
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
name|tasks
return|;
block|}
comment|//History data is leisurely loaded when task level data is requested
DECL|method|loadFullHistoryData (boolean loadTasks, Path historyFileAbsolute)
specifier|private
specifier|synchronized
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading history file: ["
operator|+
name|historyFileAbsolute
operator|+
literal|"]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|jobInfo
operator|!=
literal|null
condition|)
block|{
return|return;
comment|//data already loaded
block|}
if|if
condition|(
name|historyFileAbsolute
operator|!=
literal|null
condition|)
block|{
name|JobHistoryParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
operator|new
name|JobHistoryParser
argument_list|(
name|historyFileAbsolute
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
argument_list|,
name|historyFileAbsolute
argument_list|)
expr_stmt|;
name|jobInfo
operator|=
name|parser
operator|.
name|parse
argument_list|()
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
name|YarnException
argument_list|(
literal|"Could not load history file "
operator|+
name|historyFileAbsolute
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|IOException
name|parseException
init|=
name|parser
operator|.
name|getParseException
argument_list|()
decl_stmt|;
if|if
condition|(
name|parseException
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Could not parse history file "
operator|+
name|historyFileAbsolute
argument_list|,
name|parseException
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"History file not found"
argument_list|)
throw|;
block|}
if|if
condition|(
name|loadTasks
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
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
name|entry
range|:
name|jobInfo
operator|.
name|getAllTasks
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|TaskId
name|yarnTaskID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|TaskInfo
name|taskInfo
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Task
name|task
init|=
operator|new
name|CompletedTask
argument_list|(
name|yarnTaskID
argument_list|,
name|taskInfo
argument_list|)
decl_stmt|;
name|tasks
operator|.
name|put
argument_list|(
name|yarnTaskID
argument_list|,
name|task
argument_list|)
expr_stmt|;
if|if
condition|(
name|task
operator|.
name|getType
argument_list|()
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
name|mapTasks
operator|.
name|put
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|task
operator|.
name|getType
argument_list|()
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
name|reduceTasks
operator|.
name|put
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"TaskInfo loaded"
argument_list|)
expr_stmt|;
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
name|diagnostics
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
name|jobInfo
operator|.
name|getJobname
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
name|jobInfo
operator|.
name|getJobQueueName
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
operator|(
name|int
operator|)
name|jobInfo
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
operator|(
name|int
operator|)
name|jobInfo
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
name|jobInfo
operator|.
name|getUberized
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
if|if
condition|(
name|TaskType
operator|.
name|MAP
operator|.
name|equals
argument_list|(
name|taskType
argument_list|)
condition|)
block|{
return|return
name|mapTasks
return|;
block|}
else|else
block|{
comment|//we have only two types of tasks
return|return
name|reduceTasks
return|;
block|}
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
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Map
argument_list|<
name|JobACL
argument_list|,
name|AccessControlList
argument_list|>
name|jobACLs
init|=
name|jobInfo
operator|.
name|getJobACLs
argument_list|()
decl_stmt|;
name|AccessControlList
name|jobACL
init|=
name|jobACLs
operator|.
name|get
argument_list|(
name|jobOperation
argument_list|)
decl_stmt|;
return|return
name|aclsMgr
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|jobOperation
argument_list|,
name|jobInfo
operator|.
name|getUsername
argument_list|()
argument_list|,
name|jobACL
argument_list|)
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.job.Job#getJobACLs()    */
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
name|jobInfo
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
name|user
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.mapreduce.v2.app.job.Job#getConfFile()    */
annotation|@
name|Override
DECL|method|getConfFile ()
specifier|public
name|Path
name|getConfFile
parameter_list|()
block|{
return|return
name|confFile
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
name|List
argument_list|<
name|AMInfo
argument_list|>
name|amInfos
init|=
operator|new
name|LinkedList
argument_list|<
name|AMInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
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
name|jhAmInfo
range|:
name|jobInfo
operator|.
name|getAMInfos
argument_list|()
control|)
block|{
name|AMInfo
name|amInfo
init|=
name|MRBuilderUtils
operator|.
name|newAMInfo
argument_list|(
name|jhAmInfo
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|jhAmInfo
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|jhAmInfo
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|jhAmInfo
operator|.
name|getNodeManagerHost
argument_list|()
argument_list|,
name|jhAmInfo
operator|.
name|getNodeManagerPort
argument_list|()
argument_list|,
name|jhAmInfo
operator|.
name|getNodeManagerHttpPort
argument_list|()
argument_list|)
decl_stmt|;
name|amInfos
operator|.
name|add
argument_list|(
name|amInfo
argument_list|)
expr_stmt|;
block|}
return|return
name|amInfos
return|;
block|}
block|}
end_class

end_unit

