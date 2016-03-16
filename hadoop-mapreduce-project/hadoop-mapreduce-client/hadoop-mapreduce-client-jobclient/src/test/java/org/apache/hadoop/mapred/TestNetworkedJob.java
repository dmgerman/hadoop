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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|verify
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|io
operator|.
name|PrintStream
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
name|FSDataOutputStream
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
name|mapred
operator|.
name|ClusterStatus
operator|.
name|BlackListInfo
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
name|JobClient
operator|.
name|NetworkedJob
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
name|JobClient
operator|.
name|TaskStatusFilter
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
name|lib
operator|.
name|IdentityMapper
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
name|lib
operator|.
name|IdentityReducer
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|scheduler
operator|.
name|capacity
operator|.
name|CapacityScheduler
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
DECL|class|TestNetworkedJob
specifier|public
class|class
name|TestNetworkedJob
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'+'
argument_list|)
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
name|Path
name|testDir
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
operator|+
literal|"/test_mini_mr_local"
argument_list|)
decl_stmt|;
DECL|field|inFile
specifier|private
specifier|static
name|Path
name|inFile
init|=
operator|new
name|Path
argument_list|(
name|testDir
argument_list|,
literal|"in"
argument_list|)
decl_stmt|;
DECL|field|outDir
specifier|private
specifier|static
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|testDir
argument_list|,
literal|"out"
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testGetNullCounters ()
specifier|public
name|void
name|testGetNullCounters
parameter_list|()
throws|throws
name|Exception
block|{
comment|//mock creation
name|Job
name|mockJob
init|=
name|mock
argument_list|(
name|Job
operator|.
name|class
argument_list|)
decl_stmt|;
name|RunningJob
name|underTest
init|=
operator|new
name|JobClient
operator|.
name|NetworkedJob
argument_list|(
name|mockJob
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getCounters
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|underTest
operator|.
name|getCounters
argument_list|()
argument_list|)
expr_stmt|;
comment|//verification
name|verify
argument_list|(
name|mockJob
argument_list|)
operator|.
name|getCounters
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500000
argument_list|)
DECL|method|testGetJobStatus ()
specifier|public
name|void
name|testGetJobStatus
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
block|{
name|MiniMRClientCluster
name|mr
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
try|try
block|{
name|mr
operator|=
name|createMiniClusterWithCapacityScheduler
argument_list|()
expr_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|mr
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|fileSys
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|testDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|inFile
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"This is a test file"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|inFile
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|IdentityMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|IdentityReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|JobClient
name|client
init|=
operator|new
name|JobClient
argument_list|(
name|mr
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|RunningJob
name|rj
init|=
name|client
operator|.
name|submitJob
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|JobID
name|jobId
init|=
name|rj
operator|.
name|getID
argument_list|()
decl_stmt|;
comment|// The following asserts read JobStatus twice and ensure the returned
comment|// JobStatus objects correspond to the same Job.
name|assertEquals
argument_list|(
literal|"Expected matching JobIDs"
argument_list|,
name|jobId
argument_list|,
name|client
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
operator|.
name|getJobStatus
argument_list|()
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected matching startTimes"
argument_list|,
name|rj
operator|.
name|getJobStatus
argument_list|()
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|client
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
operator|.
name|getJobStatus
argument_list|()
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
block|{
name|fileSys
operator|.
name|delete
argument_list|(
name|testDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mr
operator|!=
literal|null
condition|)
block|{
name|mr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**  * test JobConf   * @throws Exception  */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500000
argument_list|)
DECL|method|testNetworkedJob ()
specifier|public
name|void
name|testNetworkedJob
parameter_list|()
throws|throws
name|Exception
block|{
comment|// mock creation
name|MiniMRClientCluster
name|mr
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
try|try
block|{
name|mr
operator|=
name|createMiniClusterWithCapacityScheduler
argument_list|()
expr_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|mr
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|fileSys
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|testDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|inFile
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"This is a test file"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|inFile
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|IdentityMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|IdentityReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|JobClient
name|client
init|=
operator|new
name|JobClient
argument_list|(
name|mr
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|RunningJob
name|rj
init|=
name|client
operator|.
name|submitJob
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|JobID
name|jobId
init|=
name|rj
operator|.
name|getID
argument_list|()
decl_stmt|;
name|NetworkedJob
name|runningJob
init|=
operator|(
name|NetworkedJob
operator|)
name|client
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
name|runningJob
operator|.
name|setJobPriority
argument_list|(
name|JobPriority
operator|.
name|HIGH
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
comment|// test getters
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|getConfiguration
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"0001/job.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobId
argument_list|,
name|runningJob
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|jobId
operator|.
name|toString
argument_list|()
argument_list|,
name|runningJob
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"N/A"
argument_list|,
name|runningJob
operator|.
name|getJobName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|getJobFile
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".staging/"
operator|+
name|runningJob
operator|.
name|getJobID
argument_list|()
operator|+
literal|"/job.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|getTrackingURL
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|mapProgress
argument_list|()
operator|==
literal|0.0f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|reduceProgress
argument_list|()
operator|==
literal|0.0f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|cleanupProgress
argument_list|()
operator|==
literal|0.0f
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|setupProgress
argument_list|()
operator|==
literal|0.0f
argument_list|)
expr_stmt|;
name|TaskCompletionEvent
index|[]
name|tce
init|=
name|runningJob
operator|.
name|getTaskCompletionEvents
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|tce
operator|.
name|length
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|runningJob
operator|.
name|getHistoryUrl
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|runningJob
operator|.
name|isRetired
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|runningJob
operator|.
name|getFailureInfo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"N/A"
argument_list|,
name|runningJob
operator|.
name|getJobStatus
argument_list|()
operator|.
name|getJobName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|client
operator|.
name|getMapTaskReports
argument_list|(
name|jobId
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|getSetupTaskReports
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Unrecognized task type: JOB_SETUP"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|client
operator|.
name|getCleanupTaskReports
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Unrecognized task type: JOB_CLEANUP"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|client
operator|.
name|getReduceTaskReports
argument_list|(
name|jobId
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test ClusterStatus
name|ClusterStatus
name|status
init|=
name|client
operator|.
name|getClusterStatus
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|status
operator|.
name|getActiveTrackerNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// it method does not implemented and always return empty array or null;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
operator|.
name|getBlacklistedTrackers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
operator|.
name|getBlacklistedTrackerNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
operator|.
name|getBlackListedTrackersInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|JobTrackerStatus
operator|.
name|RUNNING
argument_list|,
name|status
operator|.
name|getJobTrackerStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|status
operator|.
name|getMapTasks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|status
operator|.
name|getMaxMapTasks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|status
operator|.
name|getMaxReduceTasks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
operator|.
name|getNumExcludedNodes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|status
operator|.
name|getReduceTasks
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|status
operator|.
name|getTaskTrackers
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
operator|.
name|getTTExpiryInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|JobTrackerStatus
operator|.
name|RUNNING
argument_list|,
name|status
operator|.
name|getJobTrackerStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|status
operator|.
name|getGraylistedTrackers
argument_list|()
argument_list|)
expr_stmt|;
comment|// test read and write
name|ByteArrayOutputStream
name|dataOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|status
operator|.
name|write
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|dataOut
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterStatus
name|status2
init|=
operator|new
name|ClusterStatus
argument_list|()
decl_stmt|;
name|status2
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|dataOut
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|status
operator|.
name|getActiveTrackerNames
argument_list|()
argument_list|,
name|status2
operator|.
name|getActiveTrackerNames
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|status
operator|.
name|getBlackListedTrackersInfo
argument_list|()
argument_list|,
name|status2
operator|.
name|getBlackListedTrackersInfo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|status
operator|.
name|getMapTasks
argument_list|()
argument_list|,
name|status2
operator|.
name|getMapTasks
argument_list|()
argument_list|)
expr_stmt|;
comment|// test taskStatusfilter
name|JobClient
operator|.
name|setTaskOutputFilter
argument_list|(
name|job
argument_list|,
name|TaskStatusFilter
operator|.
name|ALL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TaskStatusFilter
operator|.
name|ALL
argument_list|,
name|JobClient
operator|.
name|getTaskOutputFilter
argument_list|(
name|job
argument_list|)
argument_list|)
expr_stmt|;
comment|// runningJob.setJobPriority(JobPriority.HIGH.name());
comment|// test default map
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|client
operator|.
name|getDefaultMaps
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|client
operator|.
name|getDefaultReduces
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"jobSubmitDir"
argument_list|,
name|client
operator|.
name|getSystemDir
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// test queue information
name|JobQueueInfo
index|[]
name|rootQueueInfo
init|=
name|client
operator|.
name|getRootQueues
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rootQueueInfo
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|rootQueueInfo
index|[
literal|0
index|]
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|JobQueueInfo
index|[]
name|qinfo
init|=
name|client
operator|.
name|getQueues
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|qinfo
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|qinfo
index|[
literal|0
index|]
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|client
operator|.
name|getChildQueues
argument_list|(
literal|"default"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|client
operator|.
name|getJobsFromQueue
argument_list|(
literal|"default"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|client
operator|.
name|getJobsFromQueue
argument_list|(
literal|"default"
argument_list|)
index|[
literal|0
index|]
operator|.
name|getJobFile
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"/job.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|JobQueueInfo
name|qi
init|=
name|client
operator|.
name|getQueueInfo
argument_list|(
literal|"default"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|qi
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"running"
argument_list|,
name|qi
operator|.
name|getQueueState
argument_list|()
argument_list|)
expr_stmt|;
name|QueueAclsInfo
index|[]
name|aai
init|=
name|client
operator|.
name|getQueueAclsForCurrentUser
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|aai
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"root"
argument_list|,
name|aai
index|[
literal|0
index|]
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|aai
index|[
literal|1
index|]
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
comment|// test JobClient
comment|// The following asserts read JobStatus twice and ensure the returned
comment|// JobStatus objects correspond to the same Job.
name|assertEquals
argument_list|(
literal|"Expected matching JobIDs"
argument_list|,
name|jobId
argument_list|,
name|client
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
operator|.
name|getJobStatus
argument_list|()
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected matching startTimes"
argument_list|,
name|rj
operator|.
name|getJobStatus
argument_list|()
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|client
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
operator|.
name|getJobStatus
argument_list|()
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
block|{
name|fileSys
operator|.
name|delete
argument_list|(
name|testDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mr
operator|!=
literal|null
condition|)
block|{
name|mr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * test BlackListInfo class    *     * @throws IOException    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testBlackListInfo ()
specifier|public
name|void
name|testBlackListInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|BlackListInfo
name|info
init|=
operator|new
name|BlackListInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setBlackListReport
argument_list|(
literal|"blackListInfo"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setReasonForBlackListing
argument_list|(
literal|"reasonForBlackListing"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setTrackerName
argument_list|(
literal|"trackerName"
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|byteOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutput
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|byteOut
argument_list|)
decl_stmt|;
name|info
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|BlackListInfo
name|info2
init|=
operator|new
name|BlackListInfo
argument_list|()
decl_stmt|;
name|info2
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|byteOut
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
argument_list|,
name|info2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|info
operator|.
name|toString
argument_list|()
argument_list|,
name|info2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"trackerName"
argument_list|,
name|info2
operator|.
name|getTrackerName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"reasonForBlackListing"
argument_list|,
name|info2
operator|.
name|getReasonForBlackListing
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"blackListInfo"
argument_list|,
name|info2
operator|.
name|getBlackListReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**  *  test run from command line JobQueueClient  * @throws Exception  */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500000
argument_list|)
DECL|method|testJobQueueClient ()
specifier|public
name|void
name|testJobQueueClient
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniMRClientCluster
name|mr
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
name|PrintStream
name|oldOut
init|=
name|System
operator|.
name|out
decl_stmt|;
try|try
block|{
name|mr
operator|=
name|createMiniClusterWithCapacityScheduler
argument_list|()
expr_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|mr
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|fileSys
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
name|testDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fileSys
operator|.
name|create
argument_list|(
name|inFile
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"This is a test file"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|inFile
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|IdentityMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|IdentityReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|JobClient
name|client
init|=
operator|new
name|JobClient
argument_list|(
name|mr
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|submitJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|JobQueueClient
name|jobClient
init|=
operator|new
name|JobQueueClient
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|arg
init|=
block|{
literal|"-list"
block|}
decl_stmt|;
name|jobClient
operator|.
name|run
argument_list|(
name|arg
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Queue Name : default"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Queue State : running"
argument_list|)
argument_list|)
expr_stmt|;
name|bytes
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|arg1
init|=
block|{
literal|"-showacls"
block|}
decl_stmt|;
name|jobClient
operator|.
name|run
argument_list|(
name|arg1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Queue acls for user :"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"root  ADMINISTER_QUEUE,SUBMIT_APPLICATIONS"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"default  ADMINISTER_QUEUE,SUBMIT_APPLICATIONS"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test for info and default queue
name|bytes
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|arg2
init|=
block|{
literal|"-info"
block|,
literal|"default"
block|}
decl_stmt|;
name|jobClient
operator|.
name|run
argument_list|(
name|arg2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Queue Name : default"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Queue State : running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Scheduling Info"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test for info , default queue and jobs
name|bytes
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|arg3
init|=
block|{
literal|"-info"
block|,
literal|"default"
block|,
literal|"-showJobs"
block|}
decl_stmt|;
name|jobClient
operator|.
name|run
argument_list|(
name|arg3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Queue Name : default"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Queue State : running"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Scheduling Info"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bytes
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"job_1"
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|arg4
init|=
block|{}
decl_stmt|;
name|jobClient
operator|.
name|run
argument_list|(
name|arg4
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|oldOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
block|{
name|fileSys
operator|.
name|delete
argument_list|(
name|testDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mr
operator|!=
literal|null
condition|)
block|{
name|mr
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|createMiniClusterWithCapacityScheduler ()
specifier|private
name|MiniMRClientCluster
name|createMiniClusterWithCapacityScheduler
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Expected queue names depending on Capacity Scheduler queue naming
name|conf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|,
name|CapacityScheduler
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|MiniMRClientClusterFactory
operator|.
name|create
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|,
literal|2
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

