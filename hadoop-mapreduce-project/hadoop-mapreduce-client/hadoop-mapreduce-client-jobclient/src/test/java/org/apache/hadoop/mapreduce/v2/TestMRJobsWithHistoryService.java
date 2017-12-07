begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|EnumSet
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
name|org
operator|.
name|junit
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
name|avro
operator|.
name|AvroRemoteException
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
name|SleepJob
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
name|FileSystem
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|v2
operator|.
name|api
operator|.
name|HSClientProtocol
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
name|protocolrecords
operator|.
name|GetJobReportRequest
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
name|jobhistory
operator|.
name|JHAdminConfig
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
name|net
operator|.
name|NetUtils
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
name|ipc
operator|.
name|YarnRPC
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
name|rmapp
operator|.
name|RMAppState
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|TestMRJobsWithHistoryService
specifier|public
class|class
name|TestMRJobsWithHistoryService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestMRJobsWithHistoryService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TERMINAL_RM_APP_STATES
specifier|private
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|RMAppState
argument_list|>
name|TERMINAL_RM_APP_STATES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|RMAppState
operator|.
name|FAILED
argument_list|,
name|RMAppState
operator|.
name|KILLED
argument_list|)
decl_stmt|;
DECL|field|mrCluster
specifier|private
specifier|static
name|MiniMRYarnCluster
name|mrCluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
decl_stmt|;
static|static
block|{
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"problem getting local fs"
argument_list|,
name|io
argument_list|)
throw|;
block|}
block|}
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|Path
name|TEST_ROOT_DIR
init|=
name|localFs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestMRJobs
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|APP_JAR
specifier|static
name|Path
name|APP_JAR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"MRAppJar.jar"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mrCluster
operator|==
literal|null
condition|)
block|{
name|mrCluster
operator|=
operator|new
name|MiniMRYarnCluster
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|mrCluster
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|mrCluster
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Copy MRAppJar and make it private. TODO: FIXME. This is a hack to
comment|// workaround the absent public discache.
name|localFs
operator|.
name|copyFromLocalFile
argument_list|(
operator|new
name|Path
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
argument_list|,
name|APP_JAR
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|setPermission
argument_list|(
name|APP_JAR
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testJobHistoryData ()
specifier|public
name|void
name|testJobHistoryData
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|AvroRemoteException
throws|,
name|ClassNotFoundException
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
name|SleepJob
name|sleepJob
init|=
operator|new
name|SleepJob
argument_list|()
decl_stmt|;
name|sleepJob
operator|.
name|setConf
argument_list|(
name|mrCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
comment|// Job with 3 maps and 2 reduces
name|Job
name|job
init|=
name|sleepJob
operator|.
name|createJob
argument_list|(
literal|3
argument_list|,
literal|2
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|SleepJob
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|addFileToClassPath
argument_list|(
name|APP_JAR
argument_list|)
expr_stmt|;
comment|// The AppMaster jar itself.
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Counters
name|counterMR
init|=
name|job
operator|.
name|getCounters
argument_list|()
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|job
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
name|ApplicationId
name|appID
init|=
name|jobId
operator|.
name|getAppId
argument_list|()
decl_stmt|;
name|int
name|pollElapsed
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|pollElapsed
operator|+=
literal|1000
expr_stmt|;
if|if
condition|(
name|TERMINAL_RM_APP_STATES
operator|.
name|contains
argument_list|(
name|mrCluster
operator|.
name|getResourceManager
argument_list|()
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|pollElapsed
operator|>=
literal|60000
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"application did not reach terminal state within 60 seconds"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAppState
operator|.
name|FINISHED
argument_list|,
name|mrCluster
operator|.
name|getResourceManager
argument_list|()
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|Counters
name|counterHS
init|=
name|job
operator|.
name|getCounters
argument_list|()
decl_stmt|;
comment|//TODO the Assert below worked. need to check
comment|//Should we compare each field or convert to V2 counter and compare
name|LOG
operator|.
name|info
argument_list|(
literal|"CounterHS "
operator|+
name|counterHS
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"CounterMR "
operator|+
name|counterMR
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|counterHS
argument_list|,
name|counterMR
argument_list|)
expr_stmt|;
name|HSClientProtocol
name|historyClient
init|=
name|instantiateHistoryProxy
argument_list|()
decl_stmt|;
name|GetJobReportRequest
name|gjReq
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetJobReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|gjReq
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|JobReport
name|jobReport
init|=
name|historyClient
operator|.
name|getJobReport
argument_list|(
name|gjReq
argument_list|)
operator|.
name|getJobReport
argument_list|()
decl_stmt|;
name|verifyJobReport
argument_list|(
name|jobReport
argument_list|,
name|jobId
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyJobReport (JobReport jobReport, JobId jobId)
specifier|private
name|void
name|verifyJobReport
parameter_list|(
name|JobReport
name|jobReport
parameter_list|,
name|JobId
name|jobId
parameter_list|)
block|{
name|List
argument_list|<
name|AMInfo
argument_list|>
name|amInfos
init|=
name|jobReport
operator|.
name|getAMInfos
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|amInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AMInfo
name|amInfo
init|=
name|amInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|appAttemptId
init|=
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|jobId
operator|.
name|getAppId
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ContainerId
name|amContainerId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|appAttemptId
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttemptId
argument_list|,
name|amInfo
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|amContainerId
argument_list|,
name|amInfo
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|jobReport
operator|.
name|getSubmitTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|jobReport
operator|.
name|getStartTime
argument_list|()
operator|>
literal|0
operator|&&
name|jobReport
operator|.
name|getStartTime
argument_list|()
operator|>=
name|jobReport
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|jobReport
operator|.
name|getFinishTime
argument_list|()
operator|>
literal|0
operator|&&
name|jobReport
operator|.
name|getFinishTime
argument_list|()
operator|>=
name|jobReport
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|instantiateHistoryProxy ()
specifier|private
name|HSClientProtocol
name|instantiateHistoryProxy
parameter_list|()
block|{
specifier|final
name|String
name|serviceAddr
init|=
name|mrCluster
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|JHAdminConfig
operator|.
name|MR_HISTORY_ADDRESS
argument_list|)
decl_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|HSClientProtocol
name|historyClient
init|=
operator|(
name|HSClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|HSClientProtocol
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|serviceAddr
argument_list|)
argument_list|,
name|mrCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|historyClient
return|;
block|}
block|}
end_class

end_unit

