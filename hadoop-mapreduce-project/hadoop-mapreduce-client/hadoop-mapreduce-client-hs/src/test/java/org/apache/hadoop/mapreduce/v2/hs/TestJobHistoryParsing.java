begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashMap
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
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|app
operator|.
name|MRApp
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
name|hs
operator|.
name|TestJobHistoryEvents
operator|.
name|MRAppWithHistory
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
name|FileNameIndexUtils
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
name|service
operator|.
name|Service
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
name|BuilderUtils
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
DECL|class|TestJobHistoryParsing
specifier|public
class|class
name|TestJobHistoryParsing
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
name|TestJobHistoryParsing
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testHistoryParsing ()
specifier|public
name|void
name|testHistoryParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|long
name|amStartTimeEst
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|MRApp
name|app
init|=
operator|new
name|MRAppWithHistory
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|app
operator|.
name|submit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|getContext
argument_list|()
operator|.
name|getAllJobs
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|JobId
name|jobId
init|=
name|job
operator|.
name|getID
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JOBID is "
operator|+
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|app
operator|.
name|waitForState
argument_list|(
name|job
argument_list|,
name|JobState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
comment|// make sure all events are flushed
name|app
operator|.
name|waitForState
argument_list|(
name|Service
operator|.
name|STATE
operator|.
name|STOPPED
argument_list|)
expr_stmt|;
name|String
name|jobhistoryDir
init|=
name|JobHistoryUtils
operator|.
name|getHistoryIntermediateDoneDirForUser
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|JobHistory
name|jobHistory
init|=
operator|new
name|JobHistory
argument_list|()
decl_stmt|;
name|jobHistory
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|JobIndexInfo
name|jobIndexInfo
init|=
name|jobHistory
operator|.
name|getJobMetaInfo
argument_list|(
name|jobId
argument_list|)
operator|.
name|getJobIndexInfo
argument_list|()
decl_stmt|;
name|String
name|jobhistoryFileName
init|=
name|FileNameIndexUtils
operator|.
name|getDoneFileName
argument_list|(
name|jobIndexInfo
argument_list|)
decl_stmt|;
name|Path
name|historyFilePath
init|=
operator|new
name|Path
argument_list|(
name|jobhistoryDir
argument_list|,
name|jobhistoryFileName
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|in
init|=
literal|null
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JobHistoryFile is: "
operator|+
name|historyFilePath
argument_list|)
expr_stmt|;
name|FileContext
name|fc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fc
operator|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|in
operator|=
name|fc
operator|.
name|open
argument_list|(
name|fc
operator|.
name|makeQualified
argument_list|(
name|historyFilePath
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Can not open history file: "
operator|+
name|historyFilePath
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
throw|throw
operator|(
operator|new
name|Exception
argument_list|(
literal|"Can not open History File"
argument_list|)
operator|)
throw|;
block|}
name|JobHistoryParser
name|parser
init|=
operator|new
name|JobHistoryParser
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|JobInfo
name|jobInfo
init|=
name|parser
operator|.
name|parse
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect username "
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|jobInfo
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect jobName "
argument_list|,
literal|"test"
argument_list|,
name|jobInfo
operator|.
name|getJobname
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect queuename "
argument_list|,
literal|"default"
argument_list|,
name|jobInfo
operator|.
name|getJobQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"incorrect conf path"
argument_list|,
literal|"test"
argument_list|,
name|jobInfo
operator|.
name|getJobConfPath
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"incorrect finishedMap "
argument_list|,
literal|2
argument_list|,
name|jobInfo
operator|.
name|getFinishedMaps
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"incorrect finishedReduces "
argument_list|,
literal|1
argument_list|,
name|jobInfo
operator|.
name|getFinishedReduces
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"incorrect uberized "
argument_list|,
name|job
operator|.
name|isUber
argument_list|()
argument_list|,
name|jobInfo
operator|.
name|getUberized
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|totalTasks
init|=
name|jobInfo
operator|.
name|getAllTasks
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"total number of tasks is incorrect  "
argument_list|,
literal|3
argument_list|,
name|totalTasks
argument_list|)
expr_stmt|;
comment|// Verify aminfo
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|jobInfo
operator|.
name|getAMInfos
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_HOST
argument_list|,
name|jobInfo
operator|.
name|getAMInfos
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeManagerHost
argument_list|()
argument_list|)
expr_stmt|;
name|AMInfo
name|amInfo
init|=
name|jobInfo
operator|.
name|getAMInfos
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_PORT
argument_list|,
name|amInfo
operator|.
name|getNodeManagerPort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MRApp
operator|.
name|NM_HTTP_PORT
argument_list|,
name|amInfo
operator|.
name|getNodeManagerHttpPort
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|amInfo
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|amInfo
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|amInfo
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|amInfo
operator|.
name|getStartTime
argument_list|()
operator|<=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|&&
name|amInfo
operator|.
name|getStartTime
argument_list|()
operator|>=
name|amStartTimeEst
argument_list|)
expr_stmt|;
name|ContainerId
name|fakeCid
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// Assert at taskAttempt level
for|for
control|(
name|TaskInfo
name|taskInfo
range|:
name|jobInfo
operator|.
name|getAllTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|int
name|taskAttemptCount
init|=
name|taskInfo
operator|.
name|getAllTaskAttempts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"total number of task attempts "
argument_list|,
literal|1
argument_list|,
name|taskAttemptCount
argument_list|)
expr_stmt|;
name|TaskAttemptInfo
name|taInfo
init|=
name|taskInfo
operator|.
name|getAllTaskAttempts
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|taInfo
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify the wrong ctor is not being used. Remove after mrv1 is removed.
name|Assert
operator|.
name|assertFalse
argument_list|(
name|taInfo
operator|.
name|getContainerId
argument_list|()
operator|.
name|equals
argument_list|(
name|fakeCid
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Deep compare Job and JobInfo
for|for
control|(
name|Task
name|task
range|:
name|job
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskInfo
name|taskInfo
init|=
name|jobInfo
operator|.
name|getAllTasks
argument_list|()
operator|.
name|get
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|task
operator|.
name|getID
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"TaskInfo not found"
argument_list|,
name|taskInfo
argument_list|)
expr_stmt|;
for|for
control|(
name|TaskAttempt
name|taskAttempt
range|:
name|task
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskAttemptInfo
name|taskAttemptInfo
init|=
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
operator|(
name|taskAttempt
operator|.
name|getID
argument_list|()
operator|)
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"TaskAttemptInfo not found"
argument_list|,
name|taskAttemptInfo
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect shuffle port for task attempt"
argument_list|,
name|taskAttempt
operator|.
name|getShufflePort
argument_list|()
argument_list|,
name|taskAttemptInfo
operator|.
name|getShufflePort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|summaryFileName
init|=
name|JobHistoryUtils
operator|.
name|getIntermediateSummaryFileName
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
name|Path
name|summaryFile
init|=
operator|new
name|Path
argument_list|(
name|jobhistoryDir
argument_list|,
name|summaryFileName
argument_list|)
decl_stmt|;
name|String
name|jobSummaryString
init|=
name|jobHistory
operator|.
name|getJobSummary
argument_list|(
name|fc
argument_list|,
name|summaryFile
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|jobSummaryString
operator|.
name|contains
argument_list|(
literal|"resourcesPerMap=100"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|jobSummaryString
operator|.
name|contains
argument_list|(
literal|"resourcesPerReduce=100"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|jobSummaryString
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jobSummaryElements
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|strToken
init|=
operator|new
name|StringTokenizer
argument_list|(
name|jobSummaryString
argument_list|,
literal|","
argument_list|)
decl_stmt|;
while|while
condition|(
name|strToken
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|keypair
init|=
name|strToken
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|jobSummaryElements
operator|.
name|put
argument_list|(
name|keypair
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
index|[
literal|0
index|]
argument_list|,
name|keypair
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"JobId does not match"
argument_list|,
name|jobId
operator|.
name|toString
argument_list|()
argument_list|,
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"jobId"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"submitTime should not be 0"
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"submitTime"
argument_list|)
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"launchTime should not be 0"
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"launchTime"
argument_list|)
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"firstMapTaskLaunchTime should not be 0"
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"firstMapTaskLaunchTime"
argument_list|)
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"firstReduceTaskLaunchTime should not be 0"
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"firstReduceTaskLaunchTime"
argument_list|)
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"finishTime should not be 0"
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"finishTime"
argument_list|)
argument_list|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Mismatch in num map slots"
argument_list|,
literal|2
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"numMaps"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Mismatch in num reduce slots"
argument_list|,
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"numReduces"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"User does not match"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"user"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Queue does not match"
argument_list|,
literal|"default"
argument_list|,
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"queue"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Status does not match"
argument_list|,
literal|"SUCCEEDED"
argument_list|,
name|jobSummaryElements
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|TestJobHistoryParsing
name|t
init|=
operator|new
name|TestJobHistoryParsing
argument_list|()
decl_stmt|;
name|t
operator|.
name|testHistoryParsing
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

