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
name|mapreduce
operator|.
name|InputFormat
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
name|InputSplit
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
name|OutputFormat
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|output
operator|.
name|NullOutputFormat
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
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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
name|TaskSplitIndex
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
name|split
operator|.
name|JobSplitWriter
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
name|SplitMetaInfoReader
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
name|util
operator|.
name|ReflectionUtils
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

begin_comment
comment|/**  *  Validates map phase progress.  *  Testcase uses newApi.  *  We extend Task.TaskReporter class and override setProgress()  *  to validate the map phase progress being set.  *  We extend MapTask and override startReporter() method that creates  *  TestTaskReporter instead of TaskReporter and call mapTask.run().  *  Similar to LocalJobRunner, we set up splits and call mapTask.run()  *  directly. No job is run, only map task is run.  *  As the reporter's setProgress() validates progress after  *  every record is read, we are done with the validation of map phase progress  *  once mapTask.run() is finished. Sort phase progress in map task is not  *  validated here.  */
end_comment

begin_class
DECL|class|TestMapProgress
specifier|public
class|class
name|TestMapProgress
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestMapProgress
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
decl_stmt|;
static|static
block|{
name|String
name|root
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
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|TEST_ROOT_DIR
operator|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"mapPhaseprogress"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|class|FakeUmbilical
specifier|static
class|class
name|FakeUmbilical
implements|implements
name|TaskUmbilicalProtocol
block|{
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
block|{
return|return
name|TaskUmbilicalProtocol
operator|.
name|versionID
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
name|ProtocolSignature
operator|.
name|getProtocolSignature
argument_list|(
name|this
argument_list|,
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|clientMethodsHash
argument_list|)
return|;
block|}
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Task "
operator|+
name|taskid
operator|+
literal|" reporting done."
argument_list|)
expr_stmt|;
block|}
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
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Task "
operator|+
name|taskId
operator|+
literal|" reporting file system error: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
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
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Task "
operator|+
name|taskId
operator|+
literal|" reporting shuffle error: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|fatalError (TaskAttemptID taskId, String msg, boolean fastFail)
specifier|public
name|void
name|fatalError
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|String
name|msg
parameter_list|,
name|boolean
name|fastFail
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Task "
operator|+
name|taskId
operator|+
literal|" reporting fatal error: "
operator|+
name|msg
operator|+
literal|" fast fail: "
operator|+
name|fastFail
argument_list|)
expr_stmt|;
block|}
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
DECL|method|ping (TaskAttemptID taskid)
specifier|public
name|boolean
name|ping
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
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
name|statusUpdate
argument_list|(
name|taskId
argument_list|,
name|taskStatus
argument_list|)
expr_stmt|;
block|}
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
name|statusUpdate
argument_list|(
name|taskId
argument_list|,
name|taskStatus
argument_list|)
expr_stmt|;
block|}
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
literal|true
return|;
block|}
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
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"Task "
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
if|if
condition|(
name|taskStatus
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" making progress to "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|taskStatus
operator|.
name|getProgress
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|state
init|=
name|taskStatus
operator|.
name|getStateString
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" and state of "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// ignore phase
comment|// ignore counters
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
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Task "
operator|+
name|taskid
operator|+
literal|" has problem "
operator|+
name|trace
argument_list|)
expr_stmt|;
block|}
DECL|method|getMapCompletionEvents (JobID jobId, int fromEventId, int maxLocs, TaskAttemptID id)
specifier|public
name|MapTaskCompletionEventsUpdate
name|getMapCompletionEvents
parameter_list|(
name|JobID
name|jobId
parameter_list|,
name|int
name|fromEventId
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
operator|new
name|MapTaskCompletionEventsUpdate
argument_list|(
name|TaskCompletionEvent
operator|.
name|EMPTY_ARRAY
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|reportNextRecordRange (TaskAttemptID taskid, SortedRanges.Range range)
specifier|public
name|void
name|reportNextRecordRange
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|,
name|SortedRanges
operator|.
name|Range
name|range
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Task "
operator|+
name|taskid
operator|+
literal|" reportedNextRecordRange "
operator|+
name|range
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
comment|// do nothing
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
comment|// do nothing
block|}
block|}
DECL|field|fs
specifier|private
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|map
specifier|private
name|TestMapTask
name|map
init|=
literal|null
decl_stmt|;
DECL|field|jobId
specifier|private
name|JobID
name|jobId
init|=
literal|null
decl_stmt|;
DECL|field|fakeUmbilical
specifier|private
name|FakeUmbilical
name|fakeUmbilical
init|=
operator|new
name|FakeUmbilical
argument_list|()
decl_stmt|;
comment|/**    *  Task Reporter that validates map phase progress after each record is    *  processed by map task    */
DECL|class|TestTaskReporter
specifier|public
class|class
name|TestTaskReporter
extends|extends
name|Task
operator|.
name|TaskReporter
block|{
DECL|field|recordNum
specifier|private
name|int
name|recordNum
init|=
literal|0
decl_stmt|;
comment|// number of records processed
DECL|method|TestTaskReporter (Task task)
name|TestTaskReporter
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
name|task
operator|.
name|super
argument_list|(
name|task
operator|.
name|getProgress
argument_list|()
argument_list|,
name|fakeUmbilical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setProgress (float progress)
specifier|public
name|void
name|setProgress
parameter_list|(
name|float
name|progress
parameter_list|)
block|{
name|super
operator|.
name|setProgress
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|float
name|mapTaskProgress
init|=
name|map
operator|.
name|getProgress
argument_list|()
operator|.
name|getProgress
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Map task progress is "
operator|+
name|mapTaskProgress
argument_list|)
expr_stmt|;
if|if
condition|(
name|recordNum
operator|<
literal|3
condition|)
block|{
comment|// only 3 records are there; Ignore validating progress after 3 times
name|recordNum
operator|++
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
comment|// validate map task progress when the map task is in map phase
name|assertTrue
argument_list|(
literal|"Map progress is not the expected value."
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|mapTaskProgress
operator|-
operator|(
operator|(
name|float
operator|)
name|recordNum
operator|/
literal|3
operator|)
argument_list|)
operator|<
literal|0.001
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Map Task that overrides run method and uses TestTaskReporter instead of    * TaskReporter and uses FakeUmbilical.    */
DECL|class|TestMapTask
class|class
name|TestMapTask
extends|extends
name|MapTask
block|{
DECL|method|TestMapTask (String jobFile, TaskAttemptID taskId, int partition, TaskSplitIndex splitIndex, int numSlotsRequired)
specifier|public
name|TestMapTask
parameter_list|(
name|String
name|jobFile
parameter_list|,
name|TaskAttemptID
name|taskId
parameter_list|,
name|int
name|partition
parameter_list|,
name|TaskSplitIndex
name|splitIndex
parameter_list|,
name|int
name|numSlotsRequired
parameter_list|)
block|{
name|super
argument_list|(
name|jobFile
argument_list|,
name|taskId
argument_list|,
name|partition
argument_list|,
name|splitIndex
argument_list|,
name|numSlotsRequired
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a TestTaskReporter and use it for validating map phase progress      */
annotation|@
name|Override
DECL|method|startReporter (final TaskUmbilicalProtocol umbilical)
name|TaskReporter
name|startReporter
parameter_list|(
specifier|final
name|TaskUmbilicalProtocol
name|umbilical
parameter_list|)
block|{
comment|// start thread that will handle communication with parent
name|TaskReporter
name|reporter
init|=
operator|new
name|TestTaskReporter
argument_list|(
name|map
argument_list|)
decl_stmt|;
return|return
name|reporter
return|;
block|}
block|}
comment|// In the given dir, creates part-0 file with 3 records of same size
DECL|method|createInputFile (Path rootDir)
specifier|private
name|void
name|createInputFile
parameter_list|(
name|Path
name|rootDir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|rootDir
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|rootDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|String
name|str
init|=
literal|"The quick brown fox\n"
operator|+
literal|"The brown quick fox\n"
operator|+
literal|"The fox brown quick\n"
decl_stmt|;
name|DataOutputStream
name|inpFile
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|rootDir
argument_list|,
literal|"part-0"
argument_list|)
argument_list|)
decl_stmt|;
name|inpFile
operator|.
name|writeBytes
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|inpFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    *  Validates map phase progress after each record is processed by map task    *  using custom task reporter.    */
annotation|@
name|Test
DECL|method|testMapProgress ()
specifier|public
name|void
name|testMapProgress
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|fs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|Path
name|rootDir
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
decl_stmt|;
name|createInputFile
argument_list|(
name|rootDir
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|TaskAttemptID
name|taskId
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
literal|"attempt_200907082313_0424_m_000000_0"
argument_list|)
decl_stmt|;
name|job
operator|.
name|setClass
argument_list|(
literal|"mapreduce.job.outputformat.class"
argument_list|,
name|NullOutputFormat
operator|.
name|class
argument_list|,
name|OutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|set
argument_list|(
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
name|input
operator|.
name|FileInputFormat
operator|.
name|INPUT_DIR
argument_list|,
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
name|jobId
operator|=
name|taskId
operator|.
name|getJobID
argument_list|()
expr_stmt|;
name|JobContext
name|jContext
init|=
operator|new
name|JobContextImpl
argument_list|(
name|job
argument_list|,
name|jobId
argument_list|)
decl_stmt|;
name|InputFormat
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|input
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|jContext
operator|.
name|getInputFormatClass
argument_list|()
argument_list|,
name|job
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
name|input
operator|.
name|getSplits
argument_list|(
name|jContext
argument_list|)
decl_stmt|;
name|JobSplitWriter
operator|.
name|createSplitFiles
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|,
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
operator|.
name|getFileSystem
argument_list|(
name|job
argument_list|)
argument_list|,
name|splits
argument_list|)
expr_stmt|;
name|TaskSplitMetaInfo
index|[]
name|splitMetaInfo
init|=
name|SplitMetaInfoReader
operator|.
name|readSplitMetaInfo
argument_list|(
name|jobId
argument_list|,
name|fs
argument_list|,
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|)
decl_stmt|;
name|job
operator|.
name|setUseNewMapper
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// use new api
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|splitMetaInfo
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// rawSplits.length is 1
name|map
operator|=
operator|new
name|TestMapTask
argument_list|(
name|job
operator|.
name|get
argument_list|(
name|JTConfig
operator|.
name|JT_SYSTEM_DIR
argument_list|,
literal|"/tmp/hadoop/mapred/system"
argument_list|)
operator|+
name|jobId
operator|+
literal|"job.xml"
argument_list|,
name|taskId
argument_list|,
name|i
argument_list|,
name|splitMetaInfo
index|[
name|i
index|]
operator|.
name|getSplitIndex
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|JobConf
name|localConf
init|=
operator|new
name|JobConf
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|map
operator|.
name|localizeConfiguration
argument_list|(
name|localConf
argument_list|)
expr_stmt|;
name|map
operator|.
name|setConf
argument_list|(
name|localConf
argument_list|)
expr_stmt|;
name|map
operator|.
name|run
argument_list|(
name|localConf
argument_list|,
name|fakeUmbilical
argument_list|)
expr_stmt|;
block|}
comment|// clean up
name|fs
operator|.
name|delete
argument_list|(
name|rootDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

