begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|tools
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
name|JobID
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
name|JobStatus
operator|.
name|State
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
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
name|when
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
name|times
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
name|spy
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
name|doReturn
import|;
end_import

begin_class
DECL|class|TestCLI
specifier|public
class|class
name|TestCLI
block|{
DECL|field|jobIdStr
specifier|private
specifier|static
name|String
name|jobIdStr
init|=
literal|"job_1015298225799_0015"
decl_stmt|;
annotation|@
name|Test
DECL|method|testListAttemptIdsWithValidInput ()
specifier|public
name|void
name|testListAttemptIdsWithValidInput
parameter_list|()
throws|throws
name|Exception
block|{
name|JobID
name|jobId
init|=
name|JobID
operator|.
name|forName
argument_list|(
name|jobIdStr
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
name|Job
name|job
init|=
name|mock
argument_list|(
name|Job
operator|.
name|class
argument_list|)
decl_stmt|;
name|CLI
name|cli
init|=
name|spy
argument_list|(
operator|new
name|CLI
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|when
argument_list|(
name|cli
argument_list|)
operator|.
name|createCluster
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getTaskReports
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getTaskReports
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getTaskReports
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|getTaskReports
argument_list|(
name|jobId
argument_list|,
name|TaskType
operator|.
name|REDUCE
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|int
name|retCode_MAP
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list-attempt-ids"
block|,
name|jobIdStr
block|,
literal|"MAP"
block|,
literal|"running"
block|}
argument_list|)
decl_stmt|;
comment|// testing case insensitive behavior
name|int
name|retCode_map
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list-attempt-ids"
block|,
name|jobIdStr
block|,
literal|"map"
block|,
literal|"running"
block|}
argument_list|)
decl_stmt|;
name|int
name|retCode_REDUCE
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list-attempt-ids"
block|,
name|jobIdStr
block|,
literal|"REDUCE"
block|,
literal|"running"
block|}
argument_list|)
decl_stmt|;
name|int
name|retCode_completed
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list-attempt-ids"
block|,
name|jobIdStr
block|,
literal|"REDUCE"
block|,
literal|"completed"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"MAP is a valid input,exit code should be 0"
argument_list|,
literal|0
argument_list|,
name|retCode_MAP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"map is a valid input,exit code should be 0"
argument_list|,
literal|0
argument_list|,
name|retCode_map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"REDUCE is a valid input,exit code should be 0"
argument_list|,
literal|0
argument_list|,
name|retCode_REDUCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"REDUCE and completed are a valid inputs to -list-attempt-ids,exit code should be 0"
argument_list|,
literal|0
argument_list|,
name|retCode_completed
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|job
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getTaskReports
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|job
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getTaskReports
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListAttemptIdsWithInvalidInputs ()
specifier|public
name|void
name|testListAttemptIdsWithInvalidInputs
parameter_list|()
throws|throws
name|Exception
block|{
name|JobID
name|jobId
init|=
name|JobID
operator|.
name|forName
argument_list|(
name|jobIdStr
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
name|Job
name|job
init|=
name|mock
argument_list|(
name|Job
operator|.
name|class
argument_list|)
decl_stmt|;
name|CLI
name|cli
init|=
name|spy
argument_list|(
operator|new
name|CLI
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|when
argument_list|(
name|cli
argument_list|)
operator|.
name|createCluster
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|int
name|retCode_JOB_SETUP
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list-attempt-ids"
block|,
name|jobIdStr
block|,
literal|"JOB_SETUP"
block|,
literal|"running"
block|}
argument_list|)
decl_stmt|;
name|int
name|retCode_JOB_CLEANUP
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list-attempt-ids"
block|,
name|jobIdStr
block|,
literal|"JOB_CLEANUP"
block|,
literal|"running"
block|}
argument_list|)
decl_stmt|;
name|int
name|retCode_invalidTaskState
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list-attempt-ids"
block|,
name|jobIdStr
block|,
literal|"REDUCE"
block|,
literal|"complete"
block|}
argument_list|)
decl_stmt|;
name|String
name|jobIdStr2
init|=
literal|"job_1015298225799_0016"
decl_stmt|;
name|int
name|retCode_invalidJobId
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-list-attempt-ids"
block|,
name|jobIdStr2
block|,
literal|"MAP"
block|,
literal|"running"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"JOB_SETUP is an invalid input,exit code should be -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|retCode_JOB_SETUP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JOB_CLEANUP is an invalid input,exit code should be -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|retCode_JOB_CLEANUP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"complete is an invalid input,exit code should be -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|retCode_invalidTaskState
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Non existing job id should be skippted with -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|retCode_invalidJobId
argument_list|)
expr_stmt|;
block|}
DECL|method|getTaskReports (JobID jobId, TaskType type)
specifier|private
name|TaskReport
index|[]
name|getTaskReports
parameter_list|(
name|JobID
name|jobId
parameter_list|,
name|TaskType
name|type
parameter_list|)
block|{
return|return
operator|new
name|TaskReport
index|[]
block|{
operator|new
name|TaskReport
argument_list|()
block|,
operator|new
name|TaskReport
argument_list|()
block|}
return|;
block|}
annotation|@
name|Test
DECL|method|testJobKIll ()
specifier|public
name|void
name|testJobKIll
parameter_list|()
throws|throws
name|Exception
block|{
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
name|CLI
name|cli
init|=
name|spy
argument_list|(
operator|new
name|CLI
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|when
argument_list|(
name|cli
argument_list|)
operator|.
name|createCluster
argument_list|()
expr_stmt|;
name|String
name|jobId1
init|=
literal|"job_1234654654_001"
decl_stmt|;
name|String
name|jobId2
init|=
literal|"job_1234654654_002"
decl_stmt|;
name|String
name|jobId3
init|=
literal|"job_1234654654_003"
decl_stmt|;
name|String
name|jobId4
init|=
literal|"job_1234654654_004"
decl_stmt|;
name|Job
name|mockJob1
init|=
name|mockJob
argument_list|(
name|mockCluster
argument_list|,
name|jobId1
argument_list|,
name|State
operator|.
name|RUNNING
argument_list|)
decl_stmt|;
name|Job
name|mockJob2
init|=
name|mockJob
argument_list|(
name|mockCluster
argument_list|,
name|jobId2
argument_list|,
name|State
operator|.
name|KILLED
argument_list|)
decl_stmt|;
name|Job
name|mockJob3
init|=
name|mockJob
argument_list|(
name|mockCluster
argument_list|,
name|jobId3
argument_list|,
name|State
operator|.
name|FAILED
argument_list|)
decl_stmt|;
name|Job
name|mockJob4
init|=
name|mockJob
argument_list|(
name|mockCluster
argument_list|,
name|jobId4
argument_list|,
name|State
operator|.
name|PREP
argument_list|)
decl_stmt|;
name|int
name|exitCode1
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-kill"
block|,
name|jobId1
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exitCode1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockJob1
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|killJob
argument_list|()
expr_stmt|;
name|int
name|exitCode2
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-kill"
block|,
name|jobId2
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exitCode2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockJob2
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|killJob
argument_list|()
expr_stmt|;
name|int
name|exitCode3
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-kill"
block|,
name|jobId3
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exitCode3
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockJob3
argument_list|,
name|times
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|killJob
argument_list|()
expr_stmt|;
name|int
name|exitCode4
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-kill"
block|,
name|jobId4
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exitCode4
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockJob4
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|killJob
argument_list|()
expr_stmt|;
block|}
DECL|method|mockJob (Cluster mockCluster, String jobId, State jobState)
specifier|private
name|Job
name|mockJob
parameter_list|(
name|Cluster
name|mockCluster
parameter_list|,
name|String
name|jobId
parameter_list|,
name|State
name|jobState
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
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
name|mockCluster
operator|.
name|getJob
argument_list|(
name|JobID
operator|.
name|forName
argument_list|(
name|jobId
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockJob
argument_list|)
expr_stmt|;
name|JobStatus
name|status
init|=
operator|new
name|JobStatus
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|jobState
argument_list|,
name|JobPriority
operator|.
name|HIGH
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|mockJob
return|;
block|}
annotation|@
name|Test
DECL|method|testGetJobWithoutRetry ()
specifier|public
name|void
name|testGetJobWithoutRetry
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
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_CLIENT_JOB_MAX_RETRIES
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
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
name|any
argument_list|(
name|JobID
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|CLI
name|cli
init|=
operator|new
name|CLI
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|cli
operator|.
name|cluster
operator|=
name|mockCluster
expr_stmt|;
name|Job
name|job
init|=
name|cli
operator|.
name|getJob
argument_list|(
name|JobID
operator|.
name|forName
argument_list|(
literal|"job_1234654654_001"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"job is not null"
argument_list|,
name|job
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetJobWithRetry ()
specifier|public
name|void
name|testGetJobWithRetry
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
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_CLIENT_JOB_MAX_RETRIES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
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
specifier|final
name|Job
name|mockJob
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockCluster
operator|.
name|getJob
argument_list|(
name|any
argument_list|(
name|JobID
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockJob
argument_list|)
expr_stmt|;
name|CLI
name|cli
init|=
operator|new
name|CLI
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|cli
operator|.
name|cluster
operator|=
name|mockCluster
expr_stmt|;
name|Job
name|job
init|=
name|cli
operator|.
name|getJob
argument_list|(
name|JobID
operator|.
name|forName
argument_list|(
literal|"job_1234654654_001"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"job is null"
argument_list|,
name|job
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testListEvents ()
specifier|public
name|void
name|testListEvents
parameter_list|()
throws|throws
name|Exception
block|{
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
name|CLI
name|cli
init|=
name|spy
argument_list|(
operator|new
name|CLI
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|when
argument_list|(
name|cli
argument_list|)
operator|.
name|createCluster
argument_list|()
expr_stmt|;
name|String
name|jobId1
init|=
literal|"job_1234654654_001"
decl_stmt|;
name|String
name|jobId2
init|=
literal|"job_1234654656_002"
decl_stmt|;
name|Job
name|mockJob1
init|=
name|mockJob
argument_list|(
name|mockCluster
argument_list|,
name|jobId1
argument_list|,
name|State
operator|.
name|RUNNING
argument_list|)
decl_stmt|;
comment|// Check exiting with non existing job
name|int
name|exitCode
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-events"
block|,
name|jobId2
block|,
literal|"0"
block|,
literal|"10"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLogs ()
specifier|public
name|void
name|testLogs
parameter_list|()
throws|throws
name|Exception
block|{
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
name|CLI
name|cli
init|=
name|spy
argument_list|(
operator|new
name|CLI
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|mockCluster
argument_list|)
operator|.
name|when
argument_list|(
name|cli
argument_list|)
operator|.
name|createCluster
argument_list|()
expr_stmt|;
name|String
name|jobId1
init|=
literal|"job_1234654654_001"
decl_stmt|;
name|String
name|jobId2
init|=
literal|"job_1234654656_002"
decl_stmt|;
name|Job
name|mockJob1
init|=
name|mockJob
argument_list|(
name|mockCluster
argument_list|,
name|jobId1
argument_list|,
name|State
operator|.
name|SUCCEEDED
argument_list|)
decl_stmt|;
comment|// Check exiting with non existing job
name|int
name|exitCode
init|=
name|cli
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-logs"
block|,
name|jobId2
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

