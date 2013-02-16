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
name|Path
import|;
end_import

begin_comment
comment|/** An {@link OutputCommitter} that commits files specified   * in job output directory i.e. ${mapreduce.output.fileoutputformat.outputdir}.   **/
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
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.mapred.FileOutputCommitter"
argument_list|)
decl_stmt|;
comment|/**    * Temporary directory name     */
DECL|field|TEMP_DIR_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TEMP_DIR_NAME
init|=
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
name|FileOutputCommitter
operator|.
name|PENDING_DIR_NAME
decl_stmt|;
DECL|field|SUCCEEDED_FILE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SUCCEEDED_FILE_NAME
init|=
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
name|FileOutputCommitter
operator|.
name|SUCCEEDED_FILE_NAME
decl_stmt|;
DECL|field|SUCCESSFUL_JOB_OUTPUT_DIR_MARKER
specifier|static
specifier|final
name|String
name|SUCCESSFUL_JOB_OUTPUT_DIR_MARKER
init|=
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
name|FileOutputCommitter
operator|.
name|SUCCESSFUL_JOB_OUTPUT_DIR_MARKER
decl_stmt|;
DECL|method|getOutputPath (JobContext context)
specifier|private
specifier|static
name|Path
name|getOutputPath
parameter_list|(
name|JobContext
name|context
parameter_list|)
block|{
name|JobConf
name|conf
init|=
name|context
operator|.
name|getJobConf
argument_list|()
decl_stmt|;
return|return
name|FileOutputFormat
operator|.
name|getOutputPath
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|getOutputPath (TaskAttemptContext context)
specifier|private
specifier|static
name|Path
name|getOutputPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
block|{
name|JobConf
name|conf
init|=
name|context
operator|.
name|getJobConf
argument_list|()
decl_stmt|;
return|return
name|FileOutputFormat
operator|.
name|getOutputPath
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|field|wrapped
specifier|private
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
name|FileOutputCommitter
name|wrapped
init|=
literal|null
decl_stmt|;
specifier|private
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
name|FileOutputCommitter
DECL|method|getWrapped (JobContext context)
name|getWrapped
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|wrapped
operator|==
literal|null
condition|)
block|{
name|wrapped
operator|=
operator|new
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
name|FileOutputCommitter
argument_list|(
name|getOutputPath
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapped
return|;
block|}
specifier|private
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
name|FileOutputCommitter
DECL|method|getWrapped (TaskAttemptContext context)
name|getWrapped
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|wrapped
operator|==
literal|null
condition|)
block|{
name|wrapped
operator|=
operator|new
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
name|FileOutputCommitter
argument_list|(
name|getOutputPath
argument_list|(
name|context
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapped
return|;
block|}
comment|/**    * Compute the path where the output of a given job attempt will be placed.     * @param context the context of the job.  This is used to get the    * application attempt id.    * @return the path to store job attempt data.    */
annotation|@
name|Private
DECL|method|getJobAttemptPath (JobContext context)
name|Path
name|getJobAttemptPath
parameter_list|(
name|JobContext
name|context
parameter_list|)
block|{
name|Path
name|out
init|=
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|out
operator|==
literal|null
condition|?
literal|null
else|:
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
name|FileOutputCommitter
operator|.
name|getJobAttemptPath
argument_list|(
name|context
argument_list|,
name|out
argument_list|)
return|;
block|}
annotation|@
name|Private
DECL|method|getTaskAttemptPath (TaskAttemptContext context)
specifier|public
name|Path
name|getTaskAttemptPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|out
init|=
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|out
operator|==
literal|null
condition|?
literal|null
else|:
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|,
name|out
argument_list|)
return|;
block|}
DECL|method|getTaskAttemptPath (TaskAttemptContext context, Path out)
specifier|private
name|Path
name|getTaskAttemptPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|workPath
init|=
name|FileOutputFormat
operator|.
name|getWorkOutputPath
argument_list|(
name|context
operator|.
name|getJobConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|workPath
operator|==
literal|null
operator|&&
name|out
operator|!=
literal|null
condition|)
block|{
return|return
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
name|FileOutputCommitter
operator|.
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|,
name|out
argument_list|)
return|;
block|}
return|return
name|workPath
return|;
block|}
comment|/**    * Compute the path where the output of a committed task is stored until    * the entire job is committed.    * @param context the context of the task attempt    * @return the path where the output of a committed task is stored until    * the entire job is committed.    */
annotation|@
name|Private
DECL|method|getCommittedTaskPath (TaskAttemptContext context)
name|Path
name|getCommittedTaskPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
block|{
name|Path
name|out
init|=
name|getOutputPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|out
operator|==
literal|null
condition|?
literal|null
else|:
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
name|FileOutputCommitter
operator|.
name|getCommittedTaskPath
argument_list|(
name|context
argument_list|,
name|out
argument_list|)
return|;
block|}
DECL|method|getWorkPath (TaskAttemptContext context, Path outputPath)
specifier|public
name|Path
name|getWorkPath
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|outputPath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|outputPath
operator|==
literal|null
condition|?
literal|null
else|:
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|,
name|outputPath
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|setupJob
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|commitJob
argument_list|(
name|context
argument_list|)
expr_stmt|;
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
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|cleanupJob
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abortJob (JobContext context, int runState)
specifier|public
name|void
name|abortJob
parameter_list|(
name|JobContext
name|context
parameter_list|,
name|int
name|runState
parameter_list|)
throws|throws
name|IOException
block|{
name|JobStatus
operator|.
name|State
name|state
decl_stmt|;
if|if
condition|(
name|runState
operator|==
name|JobStatus
operator|.
name|State
operator|.
name|RUNNING
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|state
operator|=
name|JobStatus
operator|.
name|State
operator|.
name|RUNNING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|runState
operator|==
name|JobStatus
operator|.
name|State
operator|.
name|SUCCEEDED
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|state
operator|=
name|JobStatus
operator|.
name|State
operator|.
name|SUCCEEDED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|runState
operator|==
name|JobStatus
operator|.
name|State
operator|.
name|FAILED
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|state
operator|=
name|JobStatus
operator|.
name|State
operator|.
name|FAILED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|runState
operator|==
name|JobStatus
operator|.
name|State
operator|.
name|PREP
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|state
operator|=
name|JobStatus
operator|.
name|State
operator|.
name|PREP
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|runState
operator|==
name|JobStatus
operator|.
name|State
operator|.
name|KILLED
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|state
operator|=
name|JobStatus
operator|.
name|State
operator|.
name|KILLED
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|runState
operator|+
literal|" is not a valid runState."
argument_list|)
throw|;
block|}
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|abortJob
argument_list|(
name|context
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
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
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|setupTask
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
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
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|commitTask
argument_list|(
name|context
argument_list|,
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|abortTask
argument_list|(
name|context
argument_list|,
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|needsTaskCommit
argument_list|(
name|context
argument_list|,
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|)
argument_list|)
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
name|getWrapped
argument_list|(
name|context
argument_list|)
operator|.
name|recoverTask
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

