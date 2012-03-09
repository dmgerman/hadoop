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
name|IOException
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
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
name|FileStatus
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
name|PathFilter
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
name|TaskAttemptContext
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
name|TaskAttemptID
import|;
end_import

begin_comment
comment|/** An {@link OutputCommitter} that commits files specified   * in job output directory i.e. ${mapreduce.output.fileoutputformat.outputdir}.  **/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|FileOutputCommitter
specifier|public
class|class
name|FileOutputCommitter
extends|extends
name|OutputCommitter
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
name|FileOutputCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**     * Name of directory where pending data is placed.  Data that has not been    * committed yet.    */
DECL|field|PENDING_DIR_NAME
specifier|public
specifier|static
specifier|final
name|String
name|PENDING_DIR_NAME
init|=
literal|"_temporary"
decl_stmt|;
DECL|field|SUCCEEDED_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SUCCEEDED_FILE_NAME
init|=
literal|"_SUCCESS"
decl_stmt|;
DECL|field|SUCCESSFUL_JOB_OUTPUT_DIR_MARKER
specifier|public
specifier|static
specifier|final
name|String
name|SUCCESSFUL_JOB_OUTPUT_DIR_MARKER
init|=
literal|"mapreduce.fileoutputcommitter.marksuccessfuljobs"
decl_stmt|;
DECL|field|outputPath
specifier|private
name|Path
name|outputPath
init|=
literal|null
decl_stmt|;
DECL|field|workPath
specifier|private
name|Path
name|workPath
init|=
literal|null
decl_stmt|;
comment|/**    * Create a file output committer    * @param outputPath the job's output path, or null if you want the output    * committer to act as a noop.    * @param context the task's context    * @throws IOException    */
DECL|method|FileOutputCommitter (Path outputPath, TaskAttemptContext context)
specifier|public
name|FileOutputCommitter
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
name|this
argument_list|(
name|outputPath
argument_list|,
operator|(
name|JobContext
operator|)
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|outputPath
operator|!=
literal|null
condition|)
block|{
name|workPath
operator|=
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create a file output committer    * @param outputPath the job's output path, or null if you want the output    * committer to act as a noop.    * @param context the task's context    * @throws IOException    */
annotation|@
name|Private
DECL|method|FileOutputCommitter (Path outputPath, JobContext context)
specifier|public
name|FileOutputCommitter
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|outputPath
operator|!=
literal|null
condition|)
block|{
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
name|this
operator|.
name|outputPath
operator|=
name|fs
operator|.
name|makeQualified
argument_list|(
name|outputPath
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return the path where final output of the job should be placed.  This    * could also be considered the committed application attempt path.    */
DECL|method|getOutputPath ()
specifier|private
name|Path
name|getOutputPath
parameter_list|()
block|{
return|return
name|this
operator|.
name|outputPath
return|;
block|}
comment|/**    * @return true if we have an output path set, else false.    */
DECL|method|hasOutputPath ()
specifier|private
name|boolean
name|hasOutputPath
parameter_list|()
block|{
return|return
name|this
operator|.
name|outputPath
operator|!=
literal|null
return|;
block|}
comment|/**    * @return the path where the output of pending job attempts are    * stored.    */
DECL|method|getPendingJobAttemptsPath ()
specifier|private
name|Path
name|getPendingJobAttemptsPath
parameter_list|()
block|{
return|return
name|getPendingJobAttemptsPath
argument_list|(
name|getOutputPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the location of pending job attempts.    * @param out the base output directory.    * @return the location of pending job attempts.    */
DECL|method|getPendingJobAttemptsPath (Path out)
specifier|private
specifier|static
name|Path
name|getPendingJobAttemptsPath
parameter_list|(
name|Path
name|out
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|out
argument_list|,
name|PENDING_DIR_NAME
argument_list|)
return|;
block|}
comment|/**    * Get the Application Attempt Id for this job    * @param context the context to look in    * @return the Application Attempt Id for a given job.    */
DECL|method|getAppAttemptId (JobContext context)
specifier|private
specifier|static
name|int
name|getAppAttemptId
parameter_list|(
name|JobContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|APPLICATION_ATTEMPT_ID
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of a given job attempt will be placed.     * @param context the context of the job.  This is used to get the    * application attempt id.    * @return the path to store job attempt data.    */
DECL|method|getJobAttemptPath (JobContext context)
specifier|public
name|Path
name|getJobAttemptPath
parameter_list|(
name|JobContext
name|context
parameter_list|)
block|{
return|return
name|getJobAttemptPath
argument_list|(
name|context
argument_list|,
name|getOutputPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of a given job attempt will be placed.     * @param context the context of the job.  This is used to get the    * application attempt id.    * @param out the output path to place these in.    * @return the path to store job attempt data.    */
DECL|method|getJobAttemptPath (JobContext context, Path out)
specifier|public
specifier|static
name|Path
name|getJobAttemptPath
parameter_list|(
name|JobContext
name|context
parameter_list|,
name|Path
name|out
parameter_list|)
block|{
return|return
name|getJobAttemptPath
argument_list|(
name|getAppAttemptId
argument_list|(
name|context
argument_list|)
argument_list|,
name|out
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of a given job attempt will be placed.     * @param appAttemptId the ID of the application attempt for this job.    * @return the path to store job attempt data.    */
DECL|method|getJobAttemptPath (int appAttemptId)
specifier|private
name|Path
name|getJobAttemptPath
parameter_list|(
name|int
name|appAttemptId
parameter_list|)
block|{
return|return
name|getJobAttemptPath
argument_list|(
name|appAttemptId
argument_list|,
name|getOutputPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of a given job attempt will be placed.     * @param appAttemptId the ID of the application attempt for this job.    * @return the path to store job attempt data.    */
DECL|method|getJobAttemptPath (int appAttemptId, Path out)
specifier|private
specifier|static
name|Path
name|getJobAttemptPath
parameter_list|(
name|int
name|appAttemptId
parameter_list|,
name|Path
name|out
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getPendingJobAttemptsPath
argument_list|(
name|out
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|appAttemptId
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of pending task attempts are stored.    * @param context the context of the job with pending tasks.     * @return the path where the output of pending task attempts are stored.    */
DECL|method|getPendingTaskAttemptsPath (JobContext context)
specifier|private
name|Path
name|getPendingTaskAttemptsPath
parameter_list|(
name|JobContext
name|context
parameter_list|)
block|{
return|return
name|getPendingTaskAttemptsPath
argument_list|(
name|context
argument_list|,
name|getOutputPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of pending task attempts are stored.    * @param context the context of the job with pending tasks.     * @return the path where the output of pending task attempts are stored.    */
DECL|method|getPendingTaskAttemptsPath (JobContext context, Path out)
specifier|private
specifier|static
name|Path
name|getPendingTaskAttemptsPath
parameter_list|(
name|JobContext
name|context
parameter_list|,
name|Path
name|out
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getJobAttemptPath
argument_list|(
name|context
argument_list|,
name|out
argument_list|)
argument_list|,
name|PENDING_DIR_NAME
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of a task attempt is stored until    * that task is committed.    *     * @param context the context of the task attempt.    * @return the path where a task attempt should be stored.    */
DECL|method|getTaskAttemptPath (TaskAttemptContext context)
specifier|public
name|Path
name|getTaskAttemptPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getPendingTaskAttemptsPath
argument_list|(
name|context
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of a task attempt is stored until    * that task is committed.    *     * @param context the context of the task attempt.    * @param out The output path to put things in.    * @return the path where a task attempt should be stored.    */
DECL|method|getTaskAttemptPath (TaskAttemptContext context, Path out)
specifier|public
specifier|static
name|Path
name|getTaskAttemptPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|out
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getPendingTaskAttemptsPath
argument_list|(
name|context
argument_list|,
name|out
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of a committed task is stored until    * the entire job is committed.    * @param context the context of the task attempt    * @return the path where the output of a committed task is stored until    * the entire job is committed.    */
DECL|method|getCommittedTaskPath (TaskAttemptContext context)
specifier|public
name|Path
name|getCommittedTaskPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
block|{
return|return
name|getCommittedTaskPath
argument_list|(
name|getAppAttemptId
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|getCommittedTaskPath (TaskAttemptContext context, Path out)
specifier|public
specifier|static
name|Path
name|getCommittedTaskPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|out
parameter_list|)
block|{
return|return
name|getCommittedTaskPath
argument_list|(
name|getAppAttemptId
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
argument_list|,
name|out
argument_list|)
return|;
block|}
comment|/**    * Compute the path where the output of a committed task is stored until the    * entire job is committed for a specific application attempt.    * @param appAttemptId the id of the application attempt to use    * @param context the context of any task.    * @return the path where the output of a committed task is stored.    */
DECL|method|getCommittedTaskPath (int appAttemptId, TaskAttemptContext context)
specifier|private
name|Path
name|getCommittedTaskPath
parameter_list|(
name|int
name|appAttemptId
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getJobAttemptPath
argument_list|(
name|appAttemptId
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getCommittedTaskPath (int appAttemptId, TaskAttemptContext context, Path out)
specifier|private
specifier|static
name|Path
name|getCommittedTaskPath
parameter_list|(
name|int
name|appAttemptId
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|out
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|getJobAttemptPath
argument_list|(
name|appAttemptId
argument_list|,
name|out
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|class|CommittedTaskFilter
specifier|private
specifier|static
class|class
name|CommittedTaskFilter
implements|implements
name|PathFilter
block|{
annotation|@
name|Override
DECL|method|accept (Path path)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
operator|!
name|PENDING_DIR_NAME
operator|.
name|equals
argument_list|(
name|path
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Get a list of all paths where output from committed tasks are stored.    * @param context the context of the current job    * @return the list of these Paths/FileStatuses.     * @throws IOException    */
DECL|method|getAllCommittedTaskPaths (JobContext context)
specifier|private
name|FileStatus
index|[]
name|getAllCommittedTaskPaths
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|jobAttemptPath
init|=
name|getJobAttemptPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|jobAttemptPath
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|fs
operator|.
name|listStatus
argument_list|(
name|jobAttemptPath
argument_list|,
operator|new
name|CommittedTaskFilter
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get the directory that the task should write results into.    * @return the work directory    * @throws IOException    */
DECL|method|getWorkPath ()
specifier|public
name|Path
name|getWorkPath
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|workPath
return|;
block|}
comment|/**    * Create the temporary directory that is the root of all of the task     * work directories.    * @param context the job's context    */
DECL|method|setupJob (JobContext context)
specifier|public
name|void
name|setupJob
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasOutputPath
argument_list|()
condition|)
block|{
name|Path
name|jobAttemptPath
init|=
name|getJobAttemptPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|jobAttemptPath
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|jobAttemptPath
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|jobAttemptPath
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Output Path is null in setupJob()"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The job has completed so move all committed tasks to the final output dir.    * Delete the temporary directory, including all of the work directories.    * Create a _SUCCESS file to make it as successful.    * @param context the job's context    */
DECL|method|commitJob (JobContext context)
specifier|public
name|void
name|commitJob
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasOutputPath
argument_list|()
condition|)
block|{
name|Path
name|finalOutput
init|=
name|getOutputPath
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|finalOutput
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|stat
range|:
name|getAllCommittedTaskPaths
argument_list|(
name|context
argument_list|)
control|)
block|{
name|mergePaths
argument_list|(
name|fs
argument_list|,
name|stat
argument_list|,
name|finalOutput
argument_list|)
expr_stmt|;
block|}
comment|// delete the _temporary folder and create a _done file in the o/p folder
name|cleanupJob
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// True if the job requires output.dir marked on successful job.
comment|// Note that by default it is set to true.
if|if
condition|(
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|SUCCESSFUL_JOB_OUTPUT_DIR_MARKER
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|Path
name|markerPath
init|=
operator|new
name|Path
argument_list|(
name|outputPath
argument_list|,
name|SUCCEEDED_FILE_NAME
argument_list|)
decl_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|markerPath
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Output Path is null in commitJob()"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Merge two paths together.  Anything in from will be moved into to, if there    * are any name conflicts while merging the files or directories in from win.    * @param fs the File System to use    * @param from the path data is coming from.    * @param to the path data is going to.    * @throws IOException on any error    */
DECL|method|mergePaths (FileSystem fs, final FileStatus from, final Path to)
specifier|private
specifier|static
name|void
name|mergePaths
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
specifier|final
name|FileStatus
name|from
parameter_list|,
specifier|final
name|Path
name|to
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Merging data from "
operator|+
name|from
operator|+
literal|" to "
operator|+
name|to
argument_list|)
expr_stmt|;
if|if
condition|(
name|from
operator|.
name|isFile
argument_list|()
condition|)
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|to
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|delete
argument_list|(
name|to
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|to
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|fs
operator|.
name|rename
argument_list|(
name|from
operator|.
name|getPath
argument_list|()
argument_list|,
name|to
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to rename "
operator|+
name|from
operator|+
literal|" to "
operator|+
name|to
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|from
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|to
argument_list|)
condition|)
block|{
name|FileStatus
name|toStat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|to
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|toStat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|delete
argument_list|(
name|to
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete "
operator|+
name|to
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|fs
operator|.
name|rename
argument_list|(
name|from
operator|.
name|getPath
argument_list|()
argument_list|,
name|to
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to rename "
operator|+
name|from
operator|+
literal|" to "
operator|+
name|to
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|//It is a directory so merge everything in the directories
for|for
control|(
name|FileStatus
name|subFrom
range|:
name|fs
operator|.
name|listStatus
argument_list|(
name|from
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|Path
name|subTo
init|=
operator|new
name|Path
argument_list|(
name|to
argument_list|,
name|subFrom
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|mergePaths
argument_list|(
name|fs
argument_list|,
name|subFrom
argument_list|,
name|subTo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|//it does not exist just rename
if|if
condition|(
operator|!
name|fs
operator|.
name|rename
argument_list|(
name|from
operator|.
name|getPath
argument_list|()
argument_list|,
name|to
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to rename "
operator|+
name|from
operator|+
literal|" to "
operator|+
name|to
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
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
if|if
condition|(
name|hasOutputPath
argument_list|()
condition|)
block|{
name|Path
name|pendingJobAttemptsPath
init|=
name|getPendingJobAttemptsPath
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|pendingJobAttemptsPath
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
name|delete
argument_list|(
name|pendingJobAttemptsPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Output Path is null in cleanupJob()"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Delete the temporary directory, including all of the work directories.    * @param context the job's context    */
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
comment|// delete the _temporary folder
name|cleanupJob
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * No task setup required.    */
annotation|@
name|Override
DECL|method|setupTask (TaskAttemptContext context)
specifier|public
name|void
name|setupTask
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// FileOutputCommitter's setupTask doesn't do anything. Because the
comment|// temporary task directory is created on demand when the
comment|// task is writing.
block|}
comment|/**    * Move the files from the work directory to the job output directory    * @param context the task context    */
annotation|@
name|Override
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
name|commitTask
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|commitTask (TaskAttemptContext context, Path taskAttemptPath)
specifier|public
name|void
name|commitTask
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|taskAttemptPath
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttemptID
name|attemptId
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasOutputPath
argument_list|()
condition|)
block|{
name|context
operator|.
name|progress
argument_list|()
expr_stmt|;
if|if
condition|(
name|taskAttemptPath
operator|==
literal|null
condition|)
block|{
name|taskAttemptPath
operator|=
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|Path
name|committedTaskPath
init|=
name|getCommittedTaskPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|taskAttemptPath
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|taskAttemptPath
argument_list|)
condition|)
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|committedTaskPath
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|delete
argument_list|(
name|committedTaskPath
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete "
operator|+
name|committedTaskPath
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|fs
operator|.
name|rename
argument_list|(
name|taskAttemptPath
argument_list|,
name|committedTaskPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not rename "
operator|+
name|taskAttemptPath
operator|+
literal|" to "
operator|+
name|committedTaskPath
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Saved output of task '"
operator|+
name|attemptId
operator|+
literal|"' to "
operator|+
name|committedTaskPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No Output found for "
operator|+
name|attemptId
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Output Path is null in commitTask()"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Delete the work directory    * @throws IOException     */
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
name|abortTask
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|abortTask (TaskAttemptContext context, Path taskAttemptPath)
specifier|public
name|void
name|abortTask
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|taskAttemptPath
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasOutputPath
argument_list|()
condition|)
block|{
name|context
operator|.
name|progress
argument_list|()
expr_stmt|;
if|if
condition|(
name|taskAttemptPath
operator|==
literal|null
condition|)
block|{
name|taskAttemptPath
operator|=
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|FileSystem
name|fs
init|=
name|taskAttemptPath
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|delete
argument_list|(
name|taskAttemptPath
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not delete "
operator|+
name|taskAttemptPath
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Output Path is null in abortTask()"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Did this task write any files in the work directory?    * @param context the task's context    */
annotation|@
name|Override
DECL|method|needsTaskCommit (TaskAttemptContext context )
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
name|needsTaskCommit
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Private
DECL|method|needsTaskCommit (TaskAttemptContext context, Path taskAttemptPath )
specifier|public
name|boolean
name|needsTaskCommit
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|taskAttemptPath
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasOutputPath
argument_list|()
condition|)
block|{
if|if
condition|(
name|taskAttemptPath
operator|==
literal|null
condition|)
block|{
name|taskAttemptPath
operator|=
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|FileSystem
name|fs
init|=
name|taskAttemptPath
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|fs
operator|.
name|exists
argument_list|(
name|taskAttemptPath
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isRecoverySupported ()
specifier|public
name|boolean
name|isRecoverySupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|recoverTask (TaskAttemptContext context)
specifier|public
name|void
name|recoverTask
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasOutputPath
argument_list|()
condition|)
block|{
name|context
operator|.
name|progress
argument_list|()
expr_stmt|;
name|TaskAttemptID
name|attemptId
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
decl_stmt|;
name|int
name|previousAttempt
init|=
name|getAppAttemptId
argument_list|(
name|context
argument_list|)
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|previousAttempt
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot recover task output for first attempt..."
argument_list|)
throw|;
block|}
name|Path
name|committedTaskPath
init|=
name|getCommittedTaskPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Path
name|previousCommittedTaskPath
init|=
name|getCommittedTaskPath
argument_list|(
name|previousAttempt
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|committedTaskPath
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Trying to recover task from "
operator|+
name|previousCommittedTaskPath
operator|+
literal|" into "
operator|+
name|committedTaskPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|previousCommittedTaskPath
argument_list|)
condition|)
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|committedTaskPath
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|fs
operator|.
name|delete
argument_list|(
name|committedTaskPath
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete "
operator|+
name|committedTaskPath
argument_list|)
throw|;
block|}
block|}
comment|//Rename can fail if the parent directory does not yet exist.
name|Path
name|committedParent
init|=
name|committedTaskPath
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|committedParent
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|rename
argument_list|(
name|previousCommittedTaskPath
argument_list|,
name|committedTaskPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not rename "
operator|+
name|previousCommittedTaskPath
operator|+
literal|" to "
operator|+
name|committedTaskPath
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Saved output of "
operator|+
name|attemptId
operator|+
literal|" to "
operator|+
name|committedTaskPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|attemptId
operator|+
literal|" had no output to recover."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Output Path is null in recoverTask()"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

