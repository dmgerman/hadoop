begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.output
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
name|output
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
name|mapred
operator|.
name|HadoopTestCase
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
name|UtilsForTests
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
name|MapReduceTestUtil
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
name|TaskAttemptContext
import|;
end_import

begin_comment
comment|/**  * A JUnit test to test Map-Reduce job committer.  */
end_comment

begin_class
DECL|class|TestJobOutputCommitter
specifier|public
class|class
name|TestJobOutputCommitter
extends|extends
name|HadoopTestCase
block|{
DECL|method|TestJobOutputCommitter ()
specifier|public
name|TestJobOutputCommitter
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|CLUSTER_MR
argument_list|,
name|LOCAL_FS
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
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
operator|+
literal|"/"
operator|+
literal|"test-job-cleanup"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|CUSTOM_CLEANUP_FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CUSTOM_CLEANUP_FILE_NAME
init|=
literal|"_custom_cleanup"
decl_stmt|;
DECL|field|ABORT_KILLED_FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|ABORT_KILLED_FILE_NAME
init|=
literal|"_custom_abort_killed"
decl_stmt|;
DECL|field|ABORT_FAILED_FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|ABORT_FAILED_FILE_NAME
init|=
literal|"_custom_abort_failed"
decl_stmt|;
DECL|field|inDir
specifier|private
specifier|static
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"test-input"
argument_list|)
decl_stmt|;
DECL|field|outDirs
specifier|private
specifier|static
name|int
name|outDirs
init|=
literal|0
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|conf
operator|=
name|createJobConf
argument_list|()
expr_stmt|;
name|fs
operator|=
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**     * Committer with deprecated {@link FileOutputCommitter#cleanupJob(JobContext)}    * making a _failed/_killed in the output folder    */
DECL|class|CommitterWithCustomDeprecatedCleanup
specifier|static
class|class
name|CommitterWithCustomDeprecatedCleanup
extends|extends
name|FileOutputCommitter
block|{
DECL|method|CommitterWithCustomDeprecatedCleanup (Path outputPath, TaskAttemptContext context)
specifier|public
name|CommitterWithCustomDeprecatedCleanup
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|outputPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cleanupJob (JobContext context)
specifier|public
name|void
name|cleanupJob
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"---- HERE ----"
argument_list|)
expr_stmt|;
name|Path
name|outputPath
init|=
name|FileOutputFormat
operator|.
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|outputPath
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|outputPath
argument_list|,
name|CUSTOM_CLEANUP_FILE_NAME
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Committer with abort making a _failed/_killed in the output folder    */
DECL|class|CommitterWithCustomAbort
specifier|static
class|class
name|CommitterWithCustomAbort
extends|extends
name|FileOutputCommitter
block|{
DECL|method|CommitterWithCustomAbort (Path outputPath, TaskAttemptContext context)
specifier|public
name|CommitterWithCustomAbort
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|outputPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abortJob (JobContext context, JobStatus.State state)
specifier|public
name|void
name|abortJob
parameter_list|(
name|JobContext
name|context
parameter_list|,
name|JobStatus
operator|.
name|State
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|outputPath
init|=
name|FileOutputFormat
operator|.
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|outputPath
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|fileName
init|=
operator|(
name|state
operator|.
name|equals
argument_list|(
name|JobStatus
operator|.
name|State
operator|.
name|FAILED
argument_list|)
operator|)
condition|?
name|ABORT_FAILED_FILE_NAME
else|:
name|ABORT_KILLED_FILE_NAME
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|outputPath
argument_list|,
name|fileName
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getNewOutputDir ()
specifier|private
name|Path
name|getNewOutputDir
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"output-"
operator|+
name|outDirs
operator|++
argument_list|)
return|;
block|}
DECL|class|MyOutputFormatWithCustomAbort
specifier|static
class|class
name|MyOutputFormatWithCustomAbort
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|TextOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|committer
specifier|private
name|OutputCommitter
name|committer
init|=
literal|null
decl_stmt|;
DECL|method|getOutputCommitter ( TaskAttemptContext context)
specifier|public
specifier|synchronized
name|OutputCommitter
name|getOutputCommitter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|committer
operator|==
literal|null
condition|)
block|{
name|Path
name|output
init|=
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|committer
operator|=
operator|new
name|CommitterWithCustomAbort
argument_list|(
name|output
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|committer
return|;
block|}
block|}
DECL|class|MyOutputFormatWithCustomCleanup
specifier|static
class|class
name|MyOutputFormatWithCustomCleanup
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|TextOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|committer
specifier|private
name|OutputCommitter
name|committer
init|=
literal|null
decl_stmt|;
DECL|method|getOutputCommitter ( TaskAttemptContext context)
specifier|public
specifier|synchronized
name|OutputCommitter
name|getOutputCommitter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|committer
operator|==
literal|null
condition|)
block|{
name|Path
name|output
init|=
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|committer
operator|=
operator|new
name|CommitterWithCustomDeprecatedCleanup
argument_list|(
name|output
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|committer
return|;
block|}
block|}
comment|// run a job with 1 map and let it run to completion
DECL|method|testSuccessfulJob (String filename, Class<? extends OutputFormat> output, String[] exclude)
specifier|private
name|void
name|testSuccessfulJob
parameter_list|(
name|String
name|filename
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|>
name|output
parameter_list|,
name|String
index|[]
name|exclude
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|outDir
init|=
name|getNewOutputDir
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createJob
argument_list|(
name|conf
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
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Job failed!"
argument_list|,
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
name|outDir
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Done file missing for job "
operator|+
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
comment|// check if the files from the missing set exists
for|for
control|(
name|String
name|ex
range|:
name|exclude
control|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|outDir
argument_list|,
name|ex
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"File "
operator|+
name|file
operator|+
literal|" should not be present for successful job "
operator|+
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// run a job for which all the attempts simply fail.
DECL|method|testFailedJob (String fileName, Class<? extends OutputFormat> output, String[] exclude)
specifier|private
name|void
name|testFailedJob
parameter_list|(
name|String
name|fileName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|>
name|output
parameter_list|,
name|String
index|[]
name|exclude
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|outDir
init|=
name|getNewOutputDir
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createFailJob
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|,
name|inDir
argument_list|)
decl_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Job did not fail!"
argument_list|,
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
name|outDir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"File "
operator|+
name|testFile
operator|+
literal|" missing for failed job "
operator|+
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check if the files from the missing set exists
for|for
control|(
name|String
name|ex
range|:
name|exclude
control|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|outDir
argument_list|,
name|ex
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"File "
operator|+
name|file
operator|+
literal|" should not be present for failed job "
operator|+
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// run a job which gets stuck in mapper and kill it.
DECL|method|testKilledJob (String fileName, Class<? extends OutputFormat> output, String[] exclude)
specifier|private
name|void
name|testKilledJob
parameter_list|(
name|String
name|fileName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|OutputFormat
argument_list|>
name|output
parameter_list|,
name|String
index|[]
name|exclude
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|outDir
init|=
name|getNewOutputDir
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createKillJob
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|,
name|inDir
argument_list|)
decl_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|job
operator|.
name|submit
argument_list|()
expr_stmt|;
comment|// wait for the setup to be completed
while|while
condition|(
name|job
operator|.
name|setupProgress
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|UtilsForTests
operator|.
name|waitFor
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|job
operator|.
name|killJob
argument_list|()
expr_stmt|;
comment|// kill the job
name|assertFalse
argument_list|(
literal|"Job did not get kill"
argument_list|,
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
name|outDir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"File "
operator|+
name|testFile
operator|+
literal|" missing for job "
operator|+
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check if the files from the missing set exists
for|for
control|(
name|String
name|ex
range|:
name|exclude
control|)
block|{
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|outDir
argument_list|,
name|ex
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"File "
operator|+
name|file
operator|+
literal|" should not be present for killed job "
operator|+
name|job
operator|.
name|getJobID
argument_list|()
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test default cleanup/abort behavior    *     * @throws Exception    */
DECL|method|testDefaultCleanupAndAbort ()
specifier|public
name|void
name|testDefaultCleanupAndAbort
parameter_list|()
throws|throws
name|Exception
block|{
comment|// check with a successful job
name|testSuccessfulJob
argument_list|(
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
argument_list|,
name|TextOutputFormat
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
comment|// check with a failed job
name|testFailedJob
argument_list|(
literal|null
argument_list|,
name|TextOutputFormat
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
block|}
argument_list|)
expr_stmt|;
comment|// check default abort job kill
name|testKilledJob
argument_list|(
literal|null
argument_list|,
name|TextOutputFormat
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test if a failed job with custom committer runs the abort code.    *     * @throws Exception    */
DECL|method|testCustomAbort ()
specifier|public
name|void
name|testCustomAbort
parameter_list|()
throws|throws
name|Exception
block|{
comment|// check with a successful job
name|testSuccessfulJob
argument_list|(
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
argument_list|,
name|MyOutputFormatWithCustomAbort
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
name|ABORT_FAILED_FILE_NAME
block|,
name|ABORT_KILLED_FILE_NAME
block|}
argument_list|)
expr_stmt|;
comment|// check with a failed job
name|testFailedJob
argument_list|(
name|ABORT_FAILED_FILE_NAME
argument_list|,
name|MyOutputFormatWithCustomAbort
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
block|,
name|ABORT_KILLED_FILE_NAME
block|}
argument_list|)
expr_stmt|;
comment|// check with a killed job
name|testKilledJob
argument_list|(
name|ABORT_KILLED_FILE_NAME
argument_list|,
name|MyOutputFormatWithCustomAbort
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
block|,
name|ABORT_FAILED_FILE_NAME
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test if a failed job with custom committer runs the deprecated    * {@link FileOutputCommitter#cleanupJob(JobContext)} code for api     * compatibility testing.    * @throws Exception     */
DECL|method|testCustomCleanup ()
specifier|public
name|void
name|testCustomCleanup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// check with a successful job
name|testSuccessfulJob
argument_list|(
name|CUSTOM_CLEANUP_FILE_NAME
argument_list|,
name|MyOutputFormatWithCustomCleanup
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
comment|// check with a failed job
name|testFailedJob
argument_list|(
name|CUSTOM_CLEANUP_FILE_NAME
argument_list|,
name|MyOutputFormatWithCustomCleanup
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
block|}
argument_list|)
expr_stmt|;
comment|// check with a killed job
name|testKilledJob
argument_list|(
name|CUSTOM_CLEANUP_FILE_NAME
argument_list|,
name|MyOutputFormatWithCustomCleanup
operator|.
name|class
argument_list|,
operator|new
name|String
index|[]
block|{
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

