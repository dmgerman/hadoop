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
name|FileUtil
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
name|io
operator|.
name|Text
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
name|ipc
operator|.
name|ProtocolSignature
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
name|SortedRanges
operator|.
name|Range
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
name|checkpoint
operator|.
name|TaskCheckpointID
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
name|Test
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
name|assertFalse
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
name|assertTrue
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
name|fail
import|;
end_import

begin_class
DECL|class|TestTaskCommit
specifier|public
class|class
name|TestTaskCommit
extends|extends
name|HadoopTestCase
block|{
DECL|field|rootDir
name|Path
name|rootDir
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
DECL|class|CommitterWithCommitFail
specifier|static
class|class
name|CommitterWithCommitFail
extends|extends
name|FileOutputCommitter
block|{
DECL|method|commitTask (TaskAttemptContext context)
specifier|public
name|void
name|commitTask
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|taskOutputPath
init|=
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|TaskAttemptID
name|attemptId
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
decl_stmt|;
name|JobConf
name|job
init|=
name|context
operator|.
name|getJobConf
argument_list|()
decl_stmt|;
if|if
condition|(
name|taskOutputPath
operator|!=
literal|null
condition|)
block|{
name|FileSystem
name|fs
init|=
name|taskOutputPath
operator|.
name|getFileSystem
argument_list|(
name|job
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|taskOutputPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|()
throw|;
block|}
block|}
block|}
block|}
comment|/**    * Special Committer that does not cleanup temporary files in    * abortTask    *     * The framework's FileOutputCommitter cleans up any temporary    * files left behind in abortTask. We want the test case to    * find these files and hence short-circuit abortTask.    */
DECL|class|CommitterWithoutCleanup
specifier|static
class|class
name|CommitterWithoutCleanup
extends|extends
name|FileOutputCommitter
block|{
annotation|@
name|Override
DECL|method|abortTask (TaskAttemptContext context)
specifier|public
name|void
name|abortTask
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// does nothing
block|}
block|}
comment|/**    * Special committer that always requires commit.    */
DECL|class|CommitterThatAlwaysRequiresCommit
specifier|static
class|class
name|CommitterThatAlwaysRequiresCommit
extends|extends
name|FileOutputCommitter
block|{
annotation|@
name|Override
DECL|method|needsTaskCommit (TaskAttemptContext context)
specifier|public
name|boolean
name|needsTaskCommit
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|TestTaskCommit ()
specifier|public
name|TestTaskCommit
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|LOCAL_MR
argument_list|,
name|LOCAL_FS
argument_list|,
literal|1
argument_list|,
literal|1
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
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|rootDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitFail ()
specifier|public
name|void
name|testCommitFail
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"./input"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"./output"
argument_list|)
decl_stmt|;
name|JobConf
name|jobConf
init|=
name|createJobConf
argument_list|()
decl_stmt|;
name|jobConf
operator|.
name|setMaxMapAttempts
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setOutputCommitter
argument_list|(
name|CommitterWithCommitFail
operator|.
name|class
argument_list|)
expr_stmt|;
name|RunningJob
name|rJob
init|=
name|UtilsForTests
operator|.
name|runJob
argument_list|(
name|jobConf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|rJob
operator|.
name|waitForCompletion
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|JobStatus
operator|.
name|FAILED
argument_list|,
name|rJob
operator|.
name|getJobState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|MyUmbilical
specifier|private
class|class
name|MyUmbilical
implements|implements
name|TaskUmbilicalProtocol
block|{
DECL|field|taskDone
name|boolean
name|taskDone
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|canCommit (TaskAttemptID taskid)
specifier|public
name|boolean
name|canCommit
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|commitPending (TaskAttemptID taskId, TaskStatus taskStatus)
specifier|public
name|void
name|commitPending
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|TaskStatus
name|taskStatus
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|fail
argument_list|(
literal|"Task should not go to commit-pending"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|done (TaskAttemptID taskid)
specifier|public
name|void
name|done
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|)
throws|throws
name|IOException
block|{
name|taskDone
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fatalError (TaskAttemptID taskId, String message)
specifier|public
name|void
name|fatalError
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{ }
annotation|@
name|Override
DECL|method|fsError (TaskAttemptID taskId, String message)
specifier|public
name|void
name|fsError
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{ }
annotation|@
name|Override
DECL|method|getMapCompletionEvents (JobID jobId, int fromIndex, int maxLocs, TaskAttemptID id)
specifier|public
name|MapTaskCompletionEventsUpdate
name|getMapCompletionEvents
parameter_list|(
name|JobID
name|jobId
parameter_list|,
name|int
name|fromIndex
parameter_list|,
name|int
name|maxLocs
parameter_list|,
name|TaskAttemptID
name|id
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getTask (JvmContext context)
specifier|public
name|JvmTask
name|getTask
parameter_list|(
name|JvmContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|reportDiagnosticInfo (TaskAttemptID taskid, String trace)
specifier|public
name|void
name|reportDiagnosticInfo
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|,
name|String
name|trace
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|reportNextRecordRange (TaskAttemptID taskid, Range range)
specifier|public
name|void
name|reportNextRecordRange
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|,
name|Range
name|range
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|shuffleError (TaskAttemptID taskId, String message)
specifier|public
name|void
name|shuffleError
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|statusUpdate (TaskAttemptID taskId, TaskStatus taskStatus)
specifier|public
name|AMFeedback
name|statusUpdate
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|TaskStatus
name|taskStatus
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|AMFeedback
name|a
init|=
operator|new
name|AMFeedback
argument_list|()
decl_stmt|;
name|a
operator|.
name|setTaskFound
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHash
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|preempted (TaskAttemptID taskId, TaskStatus taskStatus)
specifier|public
name|void
name|preempted
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|TaskStatus
name|taskStatus
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|fail
argument_list|(
literal|"Task should not go to commit-pending"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCheckpointID (TaskID taskId)
specifier|public
name|TaskCheckpointID
name|getCheckpointID
parameter_list|(
name|TaskID
name|taskId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setCheckpointID (TaskID downgrade, TaskCheckpointID cid)
specifier|public
name|void
name|setCheckpointID
parameter_list|(
name|TaskID
name|downgrade
parameter_list|,
name|TaskCheckpointID
name|cid
parameter_list|)
block|{
comment|// ignore
block|}
block|}
comment|/**    * A test that mimics a failed task to ensure that it does    * not get into the COMMIT_PENDING state, by using a fake    * UmbilicalProtocol's implementation that fails if the commit.    * protocol is played.    *     * The test mocks the various steps in a failed task's     * life-cycle using a special OutputCommitter and UmbilicalProtocol    * implementation.    *     * @throws Exception    */
annotation|@
name|Test
DECL|method|testTaskCleanupDoesNotCommit ()
specifier|public
name|void
name|testTaskCleanupDoesNotCommit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Mimic a job with a special committer that does not cleanup
comment|// files when a task fails.
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|job
operator|.
name|setOutputCommitter
argument_list|(
name|CommitterWithoutCleanup
operator|.
name|class
argument_list|)
expr_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"output"
argument_list|)
decl_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
comment|// Mimic job setup
name|String
name|dummyAttemptID
init|=
literal|"attempt_200707121733_0001_m_000000_0"
decl_stmt|;
name|TaskAttemptID
name|attemptID
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|dummyAttemptID
argument_list|)
decl_stmt|;
name|OutputCommitter
name|committer
init|=
operator|new
name|CommitterWithoutCleanup
argument_list|()
decl_stmt|;
name|JobContext
name|jContext
init|=
operator|new
name|JobContextImpl
argument_list|(
name|job
argument_list|,
name|attemptID
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
name|committer
operator|.
name|setupJob
argument_list|(
name|jContext
argument_list|)
expr_stmt|;
comment|// Mimic a map task
name|dummyAttemptID
operator|=
literal|"attempt_200707121733_0001_m_000001_0"
expr_stmt|;
name|attemptID
operator|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|dummyAttemptID
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
operator|new
name|MapTask
argument_list|(
literal|null
argument_list|,
name|attemptID
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|task
operator|.
name|setConf
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|task
operator|.
name|localizeConfiguration
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|task
operator|.
name|initialize
argument_list|(
name|job
argument_list|,
name|attemptID
operator|.
name|getJobID
argument_list|()
argument_list|,
name|Reporter
operator|.
name|NULL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Mimic the map task writing some output.
name|String
name|file
init|=
literal|"test.txt"
decl_stmt|;
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|TextOutputFormat
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|theOutputFormat
init|=
operator|new
name|TextOutputFormat
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|()
decl_stmt|;
name|RecordWriter
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|theRecordWriter
init|=
name|theOutputFormat
operator|.
name|getRecordWriter
argument_list|(
name|localFs
argument_list|,
name|job
argument_list|,
name|file
argument_list|,
name|Reporter
operator|.
name|NULL
argument_list|)
decl_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
operator|new
name|Text
argument_list|(
literal|"key"
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|close
argument_list|(
name|Reporter
operator|.
name|NULL
argument_list|)
expr_stmt|;
comment|// Mimic a task failure; setting up the task for cleanup simulates
comment|// the abort protocol to be played.
comment|// Without checks in the framework, this will fail
comment|// as the committer will cause a COMMIT to happen for
comment|// the cleanup task.
name|task
operator|.
name|setTaskCleanupTask
argument_list|()
expr_stmt|;
name|MyUmbilical
name|umbilical
init|=
operator|new
name|MyUmbilical
argument_list|()
decl_stmt|;
name|task
operator|.
name|run
argument_list|(
name|job
argument_list|,
name|umbilical
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Task did not succeed"
argument_list|,
name|umbilical
operator|.
name|taskDone
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitRequiredForMapTask ()
specifier|public
name|void
name|testCommitRequiredForMapTask
parameter_list|()
throws|throws
name|Exception
block|{
name|Task
name|testTask
init|=
name|createDummyTask
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"MapTask should need commit"
argument_list|,
name|testTask
operator|.
name|isCommitRequired
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitRequiredForReduceTask ()
specifier|public
name|void
name|testCommitRequiredForReduceTask
parameter_list|()
throws|throws
name|Exception
block|{
name|Task
name|testTask
init|=
name|createDummyTask
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"ReduceTask should need commit"
argument_list|,
name|testTask
operator|.
name|isCommitRequired
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitNotRequiredForJobSetup ()
specifier|public
name|void
name|testCommitNotRequiredForJobSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|Task
name|testTask
init|=
name|createDummyTask
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|testTask
operator|.
name|setJobSetupTask
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Job setup task should not need commit"
argument_list|,
name|testTask
operator|.
name|isCommitRequired
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitNotRequiredForJobCleanup ()
specifier|public
name|void
name|testCommitNotRequiredForJobCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|Task
name|testTask
init|=
name|createDummyTask
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|testTask
operator|.
name|setJobCleanupTask
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Job cleanup task should not need commit"
argument_list|,
name|testTask
operator|.
name|isCommitRequired
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitNotRequiredForTaskCleanup ()
specifier|public
name|void
name|testCommitNotRequiredForTaskCleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|Task
name|testTask
init|=
name|createDummyTask
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|)
decl_stmt|;
name|testTask
operator|.
name|setTaskCleanupTask
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Task cleanup task should not need commit"
argument_list|,
name|testTask
operator|.
name|isCommitRequired
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createDummyTask (TaskType type)
specifier|private
name|Task
name|createDummyTask
parameter_list|(
name|TaskType
name|type
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
throws|,
name|InterruptedException
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setOutputCommitter
argument_list|(
name|CommitterThatAlwaysRequiresCommit
operator|.
name|class
argument_list|)
expr_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"output"
argument_list|)
decl_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|JobID
name|jobId
init|=
name|JobID
operator|.
name|forName
argument_list|(
literal|"job_201002121132_0001"
argument_list|)
decl_stmt|;
name|Task
name|testTask
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
name|testTask
operator|=
operator|new
name|MapTask
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|testTask
operator|=
operator|new
name|ReduceTask
argument_list|()
expr_stmt|;
block|}
name|testTask
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testTask
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
name|jobId
argument_list|,
name|Reporter
operator|.
name|NULL
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|testTask
return|;
block|}
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|TestTaskCommit
name|td
init|=
operator|new
name|TestTaskCommit
argument_list|()
decl_stmt|;
name|td
operator|.
name|testCommitFail
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

