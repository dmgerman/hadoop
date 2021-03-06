begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.jobcontrol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|jobcontrol
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
name|assertEquals
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
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
name|Arrays
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
name|Job
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

begin_comment
comment|/**  * Tests the JobControl API using mock and stub Job instances.  */
end_comment

begin_class
DECL|class|TestMapReduceJobControlWithMocks
specifier|public
class|class
name|TestMapReduceJobControlWithMocks
block|{
annotation|@
name|Test
DECL|method|testSuccessfulJobs ()
specifier|public
name|void
name|testSuccessfulJobs
parameter_list|()
throws|throws
name|Exception
block|{
name|JobControl
name|jobControl
init|=
operator|new
name|JobControl
argument_list|(
literal|"Test"
argument_list|)
decl_stmt|;
name|ControlledJob
name|job1
init|=
name|createSuccessfulControlledJob
argument_list|(
name|jobControl
argument_list|)
decl_stmt|;
name|ControlledJob
name|job2
init|=
name|createSuccessfulControlledJob
argument_list|(
name|jobControl
argument_list|)
decl_stmt|;
name|ControlledJob
name|job3
init|=
name|createSuccessfulControlledJob
argument_list|(
name|jobControl
argument_list|,
name|job1
argument_list|,
name|job2
argument_list|)
decl_stmt|;
name|ControlledJob
name|job4
init|=
name|createSuccessfulControlledJob
argument_list|(
name|jobControl
argument_list|,
name|job3
argument_list|)
decl_stmt|;
name|runJobControl
argument_list|(
name|jobControl
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Success list"
argument_list|,
literal|4
argument_list|,
name|jobControl
operator|.
name|getSuccessfulJobList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed list"
argument_list|,
literal|0
argument_list|,
name|jobControl
operator|.
name|getFailedJobList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|SUCCESS
argument_list|,
name|job1
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|SUCCESS
argument_list|,
name|job2
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|SUCCESS
argument_list|,
name|job3
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|SUCCESS
argument_list|,
name|job4
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|jobControl
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailedJob ()
specifier|public
name|void
name|testFailedJob
parameter_list|()
throws|throws
name|Exception
block|{
name|JobControl
name|jobControl
init|=
operator|new
name|JobControl
argument_list|(
literal|"Test"
argument_list|)
decl_stmt|;
name|ControlledJob
name|job1
init|=
name|createFailedControlledJob
argument_list|(
name|jobControl
argument_list|)
decl_stmt|;
name|ControlledJob
name|job2
init|=
name|createSuccessfulControlledJob
argument_list|(
name|jobControl
argument_list|)
decl_stmt|;
name|ControlledJob
name|job3
init|=
name|createSuccessfulControlledJob
argument_list|(
name|jobControl
argument_list|,
name|job1
argument_list|,
name|job2
argument_list|)
decl_stmt|;
name|ControlledJob
name|job4
init|=
name|createSuccessfulControlledJob
argument_list|(
name|jobControl
argument_list|,
name|job3
argument_list|)
decl_stmt|;
name|runJobControl
argument_list|(
name|jobControl
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Success list"
argument_list|,
literal|1
argument_list|,
name|jobControl
operator|.
name|getSuccessfulJobList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed list"
argument_list|,
literal|3
argument_list|,
name|jobControl
operator|.
name|getFailedJobList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|FAILED
argument_list|,
name|job1
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|SUCCESS
argument_list|,
name|job2
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|DEPENDENT_FAILED
argument_list|,
name|job3
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|DEPENDENT_FAILED
argument_list|,
name|job4
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
name|jobControl
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorWhileSubmitting ()
specifier|public
name|void
name|testErrorWhileSubmitting
parameter_list|()
throws|throws
name|Exception
block|{
name|JobControl
name|jobControl
init|=
operator|new
name|JobControl
argument_list|(
literal|"Test"
argument_list|)
decl_stmt|;
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
name|ControlledJob
name|job1
init|=
operator|new
name|ControlledJob
argument_list|(
name|mockJob
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|doThrow
argument_list|(
operator|new
name|IncompatibleClassChangeError
argument_list|(
literal|"This is a test"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockJob
argument_list|)
operator|.
name|submit
argument_list|()
expr_stmt|;
name|jobControl
operator|.
name|addJob
argument_list|(
name|job1
argument_list|)
expr_stmt|;
name|runJobControl
argument_list|(
name|jobControl
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Success list"
argument_list|,
literal|0
argument_list|,
name|jobControl
operator|.
name|getSuccessfulJobList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed list"
argument_list|,
literal|1
argument_list|,
name|jobControl
operator|.
name|getFailedJobList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ControlledJob
operator|.
name|State
operator|.
name|FAILED
argument_list|,
name|job1
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|jobControl
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testKillJob ()
specifier|public
name|void
name|testKillJob
parameter_list|()
throws|throws
name|Exception
block|{
name|JobControl
name|jobControl
init|=
operator|new
name|JobControl
argument_list|(
literal|"Test"
argument_list|)
decl_stmt|;
name|ControlledJob
name|job
init|=
name|createFailedControlledJob
argument_list|(
name|jobControl
argument_list|)
decl_stmt|;
name|job
operator|.
name|killJob
argument_list|()
expr_stmt|;
comment|// Verify that killJob() was called on the mock Job
name|verify
argument_list|(
name|job
operator|.
name|getJob
argument_list|()
argument_list|)
operator|.
name|killJob
argument_list|()
expr_stmt|;
block|}
DECL|method|createJob (boolean complete, boolean successful)
specifier|private
name|Job
name|createJob
parameter_list|(
name|boolean
name|complete
parameter_list|,
name|boolean
name|successful
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Create a stub Job that responds in a controlled way
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
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|isComplete
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|complete
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|isSuccessful
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|successful
argument_list|)
expr_stmt|;
return|return
name|mockJob
return|;
block|}
DECL|method|createControlledJob (JobControl jobControl, boolean successful, ControlledJob... dependingJobs)
specifier|private
name|ControlledJob
name|createControlledJob
parameter_list|(
name|JobControl
name|jobControl
parameter_list|,
name|boolean
name|successful
parameter_list|,
name|ControlledJob
modifier|...
name|dependingJobs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|ControlledJob
argument_list|>
name|dependingJobsList
init|=
name|dependingJobs
operator|==
literal|null
condition|?
literal|null
else|:
name|Arrays
operator|.
name|asList
argument_list|(
name|dependingJobs
argument_list|)
decl_stmt|;
name|ControlledJob
name|job
init|=
operator|new
name|ControlledJob
argument_list|(
name|createJob
argument_list|(
literal|true
argument_list|,
name|successful
argument_list|)
argument_list|,
name|dependingJobsList
argument_list|)
decl_stmt|;
name|jobControl
operator|.
name|addJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
DECL|method|createSuccessfulControlledJob (JobControl jobControl, ControlledJob... dependingJobs)
specifier|private
name|ControlledJob
name|createSuccessfulControlledJob
parameter_list|(
name|JobControl
name|jobControl
parameter_list|,
name|ControlledJob
modifier|...
name|dependingJobs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|createControlledJob
argument_list|(
name|jobControl
argument_list|,
literal|true
argument_list|,
name|dependingJobs
argument_list|)
return|;
block|}
DECL|method|createFailedControlledJob (JobControl jobControl, ControlledJob... dependingJobs)
specifier|private
name|ControlledJob
name|createFailedControlledJob
parameter_list|(
name|JobControl
name|jobControl
parameter_list|,
name|ControlledJob
modifier|...
name|dependingJobs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|createControlledJob
argument_list|(
name|jobControl
argument_list|,
literal|false
argument_list|,
name|dependingJobs
argument_list|)
return|;
block|}
DECL|method|runJobControl (JobControl jobControl)
specifier|private
name|void
name|runJobControl
parameter_list|(
name|JobControl
name|jobControl
parameter_list|)
block|{
name|Thread
name|controller
init|=
operator|new
name|Thread
argument_list|(
name|jobControl
argument_list|)
decl_stmt|;
name|controller
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitTillAllFinished
argument_list|(
name|jobControl
argument_list|)
expr_stmt|;
block|}
DECL|method|waitTillAllFinished (JobControl jobControl)
specifier|private
name|void
name|waitTillAllFinished
parameter_list|(
name|JobControl
name|jobControl
parameter_list|)
block|{
while|while
condition|(
operator|!
name|jobControl
operator|.
name|allFinished
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
end_class

end_unit

