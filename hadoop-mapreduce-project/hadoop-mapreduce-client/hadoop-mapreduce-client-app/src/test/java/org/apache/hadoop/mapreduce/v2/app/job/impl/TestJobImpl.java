begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job.impl
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
name|job
operator|.
name|impl
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
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
name|Matchers
operator|.
name|eq
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
name|doNothing
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
name|JobContext
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
name|MRConfig
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
name|OutputCommitter
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
name|JobHistoryEvent
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
name|security
operator|.
name|token
operator|.
name|JobTokenSecretManager
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
name|split
operator|.
name|JobSplit
operator|.
name|TaskSplitMetaInfo
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
name|event
operator|.
name|JobEvent
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
name|impl
operator|.
name|JobImpl
operator|.
name|InitTransition
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
name|impl
operator|.
name|JobImpl
operator|.
name|JobNoTasksCompletedTransition
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
name|metrics
operator|.
name|MRAppMetrics
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
name|event
operator|.
name|EventHandler
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

begin_comment
comment|/**  * Tests various functions of the JobImpl class  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|TestJobImpl
specifier|public
class|class
name|TestJobImpl
block|{
annotation|@
name|Test
DECL|method|testJobNoTasksTransition ()
specifier|public
name|void
name|testJobNoTasksTransition
parameter_list|()
block|{
name|JobNoTasksCompletedTransition
name|trans
init|=
operator|new
name|JobNoTasksCompletedTransition
argument_list|()
decl_stmt|;
name|JobImpl
name|mockJob
init|=
name|mock
argument_list|(
name|JobImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Force checkJobCompleteSuccess to return null
name|Task
name|mockTask
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|tasks
operator|.
name|put
argument_list|(
name|mockTask
operator|.
name|getID
argument_list|()
argument_list|,
name|mockTask
argument_list|)
expr_stmt|;
name|mockJob
operator|.
name|tasks
operator|=
name|tasks
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|JobState
operator|.
name|ERROR
argument_list|)
expr_stmt|;
name|JobEvent
name|mockJobEvent
init|=
name|mock
argument_list|(
name|JobEvent
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobState
name|state
init|=
name|trans
operator|.
name|transition
argument_list|(
name|mockJob
argument_list|,
name|mockJobEvent
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect state returned from JobNoTasksCompletedTransition"
argument_list|,
name|JobState
operator|.
name|ERROR
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitJobFailsJob ()
specifier|public
name|void
name|testCommitJobFailsJob
parameter_list|()
block|{
name|JobImpl
name|mockJob
init|=
name|mock
argument_list|(
name|JobImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|mockJob
operator|.
name|tasks
operator|=
operator|new
name|HashMap
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
argument_list|()
expr_stmt|;
name|OutputCommitter
name|mockCommitter
init|=
name|mock
argument_list|(
name|OutputCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
name|EventHandler
name|mockEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobContext
name|mockJobContext
init|=
name|mock
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getCommitter
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockCommitter
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockEventHandler
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getJobContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockJobContext
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|finished
argument_list|(
name|JobState
operator|.
name|KILLED
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|JobState
operator|.
name|KILLED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|finished
argument_list|(
name|JobState
operator|.
name|FAILED
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|JobState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|finished
argument_list|(
name|JobState
operator|.
name|SUCCEEDED
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|JobState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
try|try
block|{
name|doThrow
argument_list|(
operator|new
name|IOException
argument_list|()
argument_list|)
operator|.
name|when
argument_list|(
name|mockCommitter
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// commitJob stubbed out, so this can't happen
block|}
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|mockEventHandler
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|JobHistoryEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|JobState
name|jobState
init|=
name|JobImpl
operator|.
name|checkJobCompleteSuccess
argument_list|(
name|mockJob
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"checkJobCompleteSuccess incorrectly returns null "
operator|+
literal|"for successful job"
argument_list|,
name|jobState
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"checkJobCompleteSuccess returns incorrect state"
argument_list|,
name|JobState
operator|.
name|FAILED
argument_list|,
name|jobState
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockJob
argument_list|)
operator|.
name|abortJob
argument_list|(
name|eq
argument_list|(
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
operator|.
name|FAILED
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckJobCompleteSuccess ()
specifier|public
name|void
name|testCheckJobCompleteSuccess
parameter_list|()
block|{
name|JobImpl
name|mockJob
init|=
name|mock
argument_list|(
name|JobImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|mockJob
operator|.
name|tasks
operator|=
operator|new
name|HashMap
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
argument_list|()
expr_stmt|;
name|OutputCommitter
name|mockCommitter
init|=
name|mock
argument_list|(
name|OutputCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
name|EventHandler
name|mockEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|JobContext
name|mockJobContext
init|=
name|mock
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getCommitter
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockCommitter
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getEventHandler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockEventHandler
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|getJobContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockJobContext
argument_list|)
expr_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|mockJob
argument_list|)
operator|.
name|setFinishTime
argument_list|()
expr_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|mockJob
argument_list|)
operator|.
name|logJobHistoryFinishedEvent
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|mockJob
operator|.
name|finished
argument_list|(
name|any
argument_list|(
name|JobState
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|JobState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
try|try
block|{
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|mockCommitter
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// commitJob stubbed out, so this can't happen
block|}
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|mockEventHandler
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|JobHistoryEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"checkJobCompleteSuccess incorrectly returns null "
operator|+
literal|"for successful job"
argument_list|,
name|JobImpl
operator|.
name|checkJobCompleteSuccess
argument_list|(
name|mockJob
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"checkJobCompleteSuccess returns incorrect state"
argument_list|,
name|JobState
operator|.
name|SUCCEEDED
argument_list|,
name|JobImpl
operator|.
name|checkJobCompleteSuccess
argument_list|(
name|mockJob
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckJobCompleteSuccessFailed ()
specifier|public
name|void
name|testCheckJobCompleteSuccessFailed
parameter_list|()
block|{
name|JobImpl
name|mockJob
init|=
name|mock
argument_list|(
name|JobImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Make the completedTasks not equal the getTasks()
name|Task
name|mockTask
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|tasks
operator|.
name|put
argument_list|(
name|mockTask
operator|.
name|getID
argument_list|()
argument_list|,
name|mockTask
argument_list|)
expr_stmt|;
name|mockJob
operator|.
name|tasks
operator|=
name|tasks
expr_stmt|;
try|try
block|{
comment|// Just in case the code breaks and reaches these calls
name|OutputCommitter
name|mockCommitter
init|=
name|mock
argument_list|(
name|OutputCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
name|EventHandler
name|mockEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|mockCommitter
argument_list|)
operator|.
name|commitJob
argument_list|(
name|any
argument_list|(
name|JobContext
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|mockEventHandler
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|JobHistoryEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNull
argument_list|(
literal|"checkJobCompleteSuccess incorrectly returns not-null "
operator|+
literal|"for unsuccessful job"
argument_list|,
name|JobImpl
operator|.
name|checkJobCompleteSuccess
argument_list|(
name|mockJob
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
name|TestJobImpl
name|t
init|=
operator|new
name|TestJobImpl
argument_list|()
decl_stmt|;
name|t
operator|.
name|testJobNoTasksTransition
argument_list|()
expr_stmt|;
name|t
operator|.
name|testCheckJobCompleteSuccess
argument_list|()
expr_stmt|;
name|t
operator|.
name|testCheckJobCompleteSuccessFailed
argument_list|()
expr_stmt|;
name|t
operator|.
name|testCheckAccess
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckAccess ()
specifier|public
name|void
name|testCheckAccess
parameter_list|()
block|{
comment|// Create two unique users
name|String
name|user1
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
decl_stmt|;
name|String
name|user2
init|=
name|user1
operator|+
literal|"1234"
decl_stmt|;
name|UserGroupInformation
name|ugi1
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user1
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugi2
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user2
argument_list|)
decl_stmt|;
comment|// Create the job
name|JobID
name|jobID
init|=
name|JobID
operator|.
name|forName
argument_list|(
literal|"job_1234567890000_0001"
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
comment|// Setup configuration access only to user1 (owner)
name|Configuration
name|conf1
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf1
operator|.
name|setBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf1
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|JOB_ACL_VIEW_JOB
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Verify access
name|JobImpl
name|job1
init|=
operator|new
name|JobImpl
argument_list|(
name|jobId
argument_list|,
literal|null
argument_list|,
name|conf1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job1
operator|.
name|checkAccess
argument_list|(
name|ugi1
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|job1
operator|.
name|checkAccess
argument_list|(
name|ugi2
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
argument_list|)
expr_stmt|;
comment|// Setup configuration access to the user1 (owner) and user2
name|Configuration
name|conf2
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf2
operator|.
name|setBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|JOB_ACL_VIEW_JOB
argument_list|,
name|user2
argument_list|)
expr_stmt|;
comment|// Verify access
name|JobImpl
name|job2
init|=
operator|new
name|JobImpl
argument_list|(
name|jobId
argument_list|,
literal|null
argument_list|,
name|conf2
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job2
operator|.
name|checkAccess
argument_list|(
name|ugi1
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job2
operator|.
name|checkAccess
argument_list|(
name|ugi2
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
argument_list|)
expr_stmt|;
comment|// Setup configuration access with security enabled and access to all
name|Configuration
name|conf3
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf3
operator|.
name|setBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf3
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|JOB_ACL_VIEW_JOB
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
comment|// Verify access
name|JobImpl
name|job3
init|=
operator|new
name|JobImpl
argument_list|(
name|jobId
argument_list|,
literal|null
argument_list|,
name|conf3
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job3
operator|.
name|checkAccess
argument_list|(
name|ugi1
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job3
operator|.
name|checkAccess
argument_list|(
name|ugi2
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
argument_list|)
expr_stmt|;
comment|// Setup configuration access without security enabled
name|Configuration
name|conf4
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf4
operator|.
name|setBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf4
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|JOB_ACL_VIEW_JOB
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Verify access
name|JobImpl
name|job4
init|=
operator|new
name|JobImpl
argument_list|(
name|jobId
argument_list|,
literal|null
argument_list|,
name|conf4
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job4
operator|.
name|checkAccess
argument_list|(
name|ugi1
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job4
operator|.
name|checkAccess
argument_list|(
name|ugi2
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
argument_list|)
expr_stmt|;
comment|// Setup configuration access without security enabled
name|Configuration
name|conf5
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf5
operator|.
name|setBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf5
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|JOB_ACL_VIEW_JOB
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Verify access
name|JobImpl
name|job5
init|=
operator|new
name|JobImpl
argument_list|(
name|jobId
argument_list|,
literal|null
argument_list|,
name|conf5
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job5
operator|.
name|checkAccess
argument_list|(
name|ugi1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|job5
operator|.
name|checkAccess
argument_list|(
name|ugi2
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUberDecision ()
specifier|public
name|void
name|testUberDecision
parameter_list|()
throws|throws
name|Exception
block|{
comment|// with default values, no of maps is 2
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|boolean
name|isUber
init|=
name|testUberDecision
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|isUber
argument_list|)
expr_stmt|;
comment|// enable uber mode, no of maps is 2
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|isUber
operator|=
name|testUberDecision
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isUber
argument_list|)
expr_stmt|;
comment|// enable uber mode, no of maps is 2, no of reduces is 1 and uber task max
comment|// reduces is 0
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_MAXREDUCES
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_REDUCES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|isUber
operator|=
name|testUberDecision
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|isUber
argument_list|)
expr_stmt|;
comment|// enable uber mode, no of maps is 2, no of reduces is 1 and uber task max
comment|// reduces is 1
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_MAXREDUCES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_REDUCES
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|isUber
operator|=
name|testUberDecision
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|isUber
argument_list|)
expr_stmt|;
comment|// enable uber mode, no of maps is 2 and uber task max maps is 0
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|JOB_UBERTASK_MAXMAPS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|isUber
operator|=
name|testUberDecision
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|isUber
argument_list|)
expr_stmt|;
block|}
DECL|method|testUberDecision (Configuration conf)
specifier|private
name|boolean
name|testUberDecision
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|JobID
name|jobID
init|=
name|JobID
operator|.
name|forName
argument_list|(
literal|"job_1234567890000_0001"
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
name|MRAppMetrics
name|mrAppMetrics
init|=
name|MRAppMetrics
operator|.
name|create
argument_list|()
decl_stmt|;
name|JobImpl
name|job
init|=
operator|new
name|JobImpl
argument_list|(
name|jobId
argument_list|,
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|,
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
name|mock
argument_list|(
name|JobTokenSecretManager
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mrAppMetrics
argument_list|,
name|mock
argument_list|(
name|OutputCommitter
operator|.
name|class
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|InitTransition
name|initTransition
init|=
name|getInitTransition
argument_list|()
decl_stmt|;
name|JobEvent
name|mockJobEvent
init|=
name|mock
argument_list|(
name|JobEvent
operator|.
name|class
argument_list|)
decl_stmt|;
name|initTransition
operator|.
name|transition
argument_list|(
name|job
argument_list|,
name|mockJobEvent
argument_list|)
expr_stmt|;
name|boolean
name|isUber
init|=
name|job
operator|.
name|isUber
argument_list|()
decl_stmt|;
return|return
name|isUber
return|;
block|}
DECL|method|getInitTransition ()
specifier|private
name|InitTransition
name|getInitTransition
parameter_list|()
block|{
name|InitTransition
name|initTransition
init|=
operator|new
name|InitTransition
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TaskSplitMetaInfo
index|[]
name|createSplits
parameter_list|(
name|JobImpl
name|job
parameter_list|,
name|JobId
name|jobId
parameter_list|)
block|{
return|return
operator|new
name|TaskSplitMetaInfo
index|[]
block|{
operator|new
name|TaskSplitMetaInfo
argument_list|()
block|,
operator|new
name|TaskSplitMetaInfo
argument_list|()
block|}
return|;
block|}
block|}
decl_stmt|;
return|return
name|initTransition
return|;
block|}
block|}
end_class

end_unit

