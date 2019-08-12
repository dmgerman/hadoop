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
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|isA
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
name|atLeastOnce
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
name|never
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
name|ByteArrayOutputStream
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
name|PrintWriter
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
name|JobPriority
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
name|JobStatus
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
name|TaskReport
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
name|junit
operator|.
name|Assert
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|JobClientUnitTest
specifier|public
class|class
name|JobClientUnitTest
block|{
DECL|class|TestJobClient
specifier|public
class|class
name|TestJobClient
extends|extends
name|JobClient
block|{
DECL|method|TestJobClient (JobConf jobConf)
name|TestJobClient
parameter_list|(
name|JobConf
name|jobConf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|jobConf
argument_list|)
expr_stmt|;
block|}
DECL|method|setCluster (Cluster cluster)
name|void
name|setCluster
parameter_list|(
name|Cluster
name|cluster
parameter_list|)
block|{
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
block|}
block|}
DECL|class|TestJobClientGetJob
specifier|public
class|class
name|TestJobClientGetJob
extends|extends
name|TestJobClient
block|{
DECL|field|lastGetJobRetriesCounter
name|int
name|lastGetJobRetriesCounter
init|=
literal|0
decl_stmt|;
DECL|field|getJobRetriesCounter
name|int
name|getJobRetriesCounter
init|=
literal|0
decl_stmt|;
DECL|field|getJobRetries
name|int
name|getJobRetries
init|=
literal|0
decl_stmt|;
DECL|field|runningJob
name|RunningJob
name|runningJob
decl_stmt|;
DECL|method|TestJobClientGetJob (JobConf jobConf)
name|TestJobClientGetJob
parameter_list|(
name|JobConf
name|jobConf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|jobConf
argument_list|)
expr_stmt|;
block|}
DECL|method|getLastGetJobRetriesCounter ()
specifier|public
name|int
name|getLastGetJobRetriesCounter
parameter_list|()
block|{
return|return
name|lastGetJobRetriesCounter
return|;
block|}
DECL|method|setGetJobRetries (int getJobRetries)
specifier|public
name|void
name|setGetJobRetries
parameter_list|(
name|int
name|getJobRetries
parameter_list|)
block|{
name|this
operator|.
name|getJobRetries
operator|=
name|getJobRetries
expr_stmt|;
block|}
DECL|method|setRunningJob (RunningJob runningJob)
specifier|public
name|void
name|setRunningJob
parameter_list|(
name|RunningJob
name|runningJob
parameter_list|)
block|{
name|this
operator|.
name|runningJob
operator|=
name|runningJob
expr_stmt|;
block|}
DECL|method|getJobInner (final JobID jobid)
specifier|protected
name|RunningJob
name|getJobInner
parameter_list|(
specifier|final
name|JobID
name|jobid
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getJobRetriesCounter
operator|>=
name|getJobRetries
condition|)
block|{
name|lastGetJobRetriesCounter
operator|=
name|getJobRetriesCounter
expr_stmt|;
name|getJobRetriesCounter
operator|=
literal|0
expr_stmt|;
return|return
name|runningJob
return|;
block|}
name|getJobRetriesCounter
operator|++
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMapTaskReportsWithNullJob ()
specifier|public
name|void
name|testMapTaskReportsWithNullJob
parameter_list|()
throws|throws
name|Exception
block|{
name|TestJobClient
name|client
init|=
operator|new
name|TestJobClient
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
decl_stmt|;
name|Cluster
name|mockCluster
init|=
name|mock
argument_list|(
name|Cluster
operator|.
name|class
argument_list|)
decl_stmt|;
name|client
operator|.
name|setCluster
argument_list|(
name|mockCluster
argument_list|)
expr_stmt|;
name|JobID
name|id
init|=
operator|new
name|JobID
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|TaskReport
index|[]
name|result
init|=
name|client
operator|.
name|getMapTaskReports
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReduceTaskReportsWithNullJob ()
specifier|public
name|void
name|testReduceTaskReportsWithNullJob
parameter_list|()
throws|throws
name|Exception
block|{
name|TestJobClient
name|client
init|=
operator|new
name|TestJobClient
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
decl_stmt|;
name|Cluster
name|mockCluster
init|=
name|mock
argument_list|(
name|Cluster
operator|.
name|class
argument_list|)
decl_stmt|;
name|client
operator|.
name|setCluster
argument_list|(
name|mockCluster
argument_list|)
expr_stmt|;
name|JobID
name|id
init|=
operator|new
name|JobID
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|TaskReport
index|[]
name|result
init|=
name|client
operator|.
name|getReduceTaskReports
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetupTaskReportsWithNullJob ()
specifier|public
name|void
name|testSetupTaskReportsWithNullJob
parameter_list|()
throws|throws
name|Exception
block|{
name|TestJobClient
name|client
init|=
operator|new
name|TestJobClient
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
decl_stmt|;
name|Cluster
name|mockCluster
init|=
name|mock
argument_list|(
name|Cluster
operator|.
name|class
argument_list|)
decl_stmt|;
name|client
operator|.
name|setCluster
argument_list|(
name|mockCluster
argument_list|)
expr_stmt|;
name|JobID
name|id
init|=
operator|new
name|JobID
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|TaskReport
index|[]
name|result
init|=
name|client
operator|.
name|getSetupTaskReports
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCleanupTaskReportsWithNullJob ()
specifier|public
name|void
name|testCleanupTaskReportsWithNullJob
parameter_list|()
throws|throws
name|Exception
block|{
name|TestJobClient
name|client
init|=
operator|new
name|TestJobClient
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
decl_stmt|;
name|Cluster
name|mockCluster
init|=
name|mock
argument_list|(
name|Cluster
operator|.
name|class
argument_list|)
decl_stmt|;
name|client
operator|.
name|setCluster
argument_list|(
name|mockCluster
argument_list|)
expr_stmt|;
name|JobID
name|id
init|=
operator|new
name|JobID
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|TaskReport
index|[]
name|result
init|=
name|client
operator|.
name|getCleanupTaskReports
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShowJob ()
specifier|public
name|void
name|testShowJob
parameter_list|()
throws|throws
name|Exception
block|{
name|TestJobClient
name|client
init|=
operator|new
name|TestJobClient
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|JobID
name|jobID
init|=
operator|new
name|JobID
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|startTime
argument_list|)
argument_list|,
literal|12345
argument_list|)
decl_stmt|;
name|JobStatus
name|mockJobStatus
init|=
name|mock
argument_list|(
name|JobStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getJobID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getJobName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jobID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|JobStatus
operator|.
name|State
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getStartTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getUsername
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"mockuser"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getQueue
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"mockqueue"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getPriority
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|JobPriority
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getNumUsedSlots
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getNumReservedSlots
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getUsedMem
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getReservedMem
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|512
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getNeededMem
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJobStatus
operator|.
name|getSchedulingInfo
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"NA"
argument_list|)
expr_stmt|;
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
name|when
argument_list|(
name|mockJob
operator|.
name|getTaskReports
argument_list|(
name|isA
argument_list|(
name|TaskType
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|TaskReport
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
name|Cluster
name|mockCluster
init|=
name|mock
argument_list|(
name|Cluster
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockJob
argument_list|)
expr_stmt|;
name|client
operator|.
name|setCluster
argument_list|(
name|mockCluster
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|client
operator|.
name|displayJobList
argument_list|(
operator|new
name|JobStatus
index|[]
block|{
name|mockJobStatus
block|}
argument_list|,
operator|new
name|PrintWriter
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|commandLineOutput
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|commandLineOutput
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|commandLineOutput
operator|.
name|contains
argument_list|(
literal|"Total jobs:1"
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|,
name|atLeastOnce
argument_list|()
argument_list|)
operator|.
name|getJobID
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getState
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getUsername
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getQueue
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getPriority
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getNumUsedSlots
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getNumReservedSlots
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getUsedMem
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getReservedMem
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getNeededMem
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mockJobStatus
argument_list|)
operator|.
name|getSchedulingInfo
argument_list|()
expr_stmt|;
comment|// This call should not go to each AM.
name|verify
argument_list|(
name|mockCluster
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockJob
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|getTaskReports
argument_list|(
name|isA
argument_list|(
name|TaskType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobWithUnknownJob ()
specifier|public
name|void
name|testGetJobWithUnknownJob
parameter_list|()
throws|throws
name|Exception
block|{
name|TestJobClient
name|client
init|=
operator|new
name|TestJobClient
argument_list|(
operator|new
name|JobConf
argument_list|()
argument_list|)
decl_stmt|;
name|Cluster
name|mockCluster
init|=
name|mock
argument_list|(
name|Cluster
operator|.
name|class
argument_list|)
decl_stmt|;
name|client
operator|.
name|setCluster
argument_list|(
name|mockCluster
argument_list|)
expr_stmt|;
name|JobID
name|id
init|=
operator|new
name|JobID
argument_list|(
literal|"unknown"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|client
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobRetry ()
specifier|public
name|void
name|testGetJobRetry
parameter_list|()
throws|throws
name|Exception
block|{
comment|//To prevent the test from running for a very long time, lower the retry
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_CLIENT_JOB_MAX_RETRIES
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|TestJobClientGetJob
name|client
init|=
operator|new
name|TestJobClientGetJob
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|JobID
name|id
init|=
operator|new
name|JobID
argument_list|(
literal|"ajob"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RunningJob
name|rj
init|=
name|mock
argument_list|(
name|RunningJob
operator|.
name|class
argument_list|)
decl_stmt|;
name|client
operator|.
name|setRunningJob
argument_list|(
name|rj
argument_list|)
expr_stmt|;
comment|//no retry
name|assertNotNull
argument_list|(
name|client
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
operator|.
name|getLastGetJobRetriesCounter
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//2 retries
name|client
operator|.
name|setGetJobRetries
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|client
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
operator|.
name|getLastGetJobRetriesCounter
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|//beyond yarn.app.mapreduce.client.job.max-retries, will get null
name|client
operator|.
name|setGetJobRetries
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|client
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobRetryDefault ()
specifier|public
name|void
name|testGetJobRetryDefault
parameter_list|()
throws|throws
name|Exception
block|{
comment|//To prevent the test from running for a very long time, lower the retry
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|TestJobClientGetJob
name|client
init|=
operator|new
name|TestJobClientGetJob
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|JobID
name|id
init|=
operator|new
name|JobID
argument_list|(
literal|"ajob"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RunningJob
name|rj
init|=
name|mock
argument_list|(
name|RunningJob
operator|.
name|class
argument_list|)
decl_stmt|;
name|client
operator|.
name|setRunningJob
argument_list|(
name|rj
argument_list|)
expr_stmt|;
comment|//3 retries (default)
name|client
operator|.
name|setGetJobRetries
argument_list|(
name|MRJobConfig
operator|.
name|DEFAULT_MR_CLIENT_JOB_MAX_RETRIES
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|client
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
operator|.
name|getLastGetJobRetriesCounter
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|MRJobConfig
operator|.
name|DEFAULT_MR_CLIENT_JOB_MAX_RETRIES
argument_list|)
expr_stmt|;
comment|//beyond yarn.app.mapreduce.client.job.max-retries, will get null
name|client
operator|.
name|setGetJobRetries
argument_list|(
name|MRJobConfig
operator|.
name|DEFAULT_MR_CLIENT_JOB_MAX_RETRIES
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|client
operator|.
name|getJob
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

