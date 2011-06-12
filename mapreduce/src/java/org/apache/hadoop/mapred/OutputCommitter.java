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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  *<code>OutputCommitter</code> describes the commit of task output for a   * Map-Reduce job.  *  *<p>The Map-Reduce framework relies on the<code>OutputCommitter</code> of   * the job to:<p>  *<ol>  *<li>  *   Setup the job during initialization. For example, create the temporary   *   output directory for the job during the initialization of the job.  *</li>  *<li>  *   Cleanup the job after the job completion. For example, remove the  *   temporary output directory after the job completion.   *</li>  *<li>  *   Setup the task temporary output.  *</li>   *<li>  *   Check whether a task needs a commit. This is to avoid the commit  *   procedure if a task does not need commit.  *</li>  *<li>  *   Commit of the task output.  *</li>    *<li>  *   Discard the task commit.  *</li>  *</ol>  *   * @see FileOutputCommitter   * @see JobContext  * @see TaskAttemptContext   * @deprecated Use {@link org.apache.hadoop.mapreduce.OutputCommitter} instead.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|OutputCommitter
specifier|public
specifier|abstract
class|class
name|OutputCommitter
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|OutputCommitter
block|{
comment|/**    * For the framework to setup the job output during initialization    *     * @param jobContext Context of the job whose output is being written.    * @throws IOException if temporary output could not be created    */
DECL|method|setupJob (JobContext jobContext)
specifier|public
specifier|abstract
name|void
name|setupJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * For cleaning up the job's output after job completion    *     * @param jobContext Context of the job whose output is being written.    * @throws IOException    * @deprecated Use {@link #commitJob(JobContext)} or     *                 {@link #abortJob(JobContext, int)} instead.    */
annotation|@
name|Deprecated
DECL|method|cleanupJob (JobContext jobContext)
specifier|public
name|void
name|cleanupJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{ }
comment|/**    * For committing job's output after successful job completion. Note that this    * is invoked for jobs with final runstate as SUCCESSFUL.	    *     * @param jobContext Context of the job whose output is being written.    * @throws IOException     */
DECL|method|commitJob (JobContext jobContext)
specifier|public
name|void
name|commitJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{
name|cleanupJob
argument_list|(
name|jobContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * For aborting an unsuccessful job's output. Note that this is invoked for     * jobs with final runstate as {@link JobStatus#FAILED} or     * {@link JobStatus#KILLED}    *     * @param jobContext Context of the job whose output is being written.    * @param status final runstate of the job    * @throws IOException    */
DECL|method|abortJob (JobContext jobContext, int status)
specifier|public
name|void
name|abortJob
parameter_list|(
name|JobContext
name|jobContext
parameter_list|,
name|int
name|status
parameter_list|)
throws|throws
name|IOException
block|{
name|cleanupJob
argument_list|(
name|jobContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets up output for the task.    *     * @param taskContext Context of the task whose output is being written.    * @throws IOException    */
DECL|method|setupTask (TaskAttemptContext taskContext)
specifier|public
specifier|abstract
name|void
name|setupTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check whether task needs a commit    *     * @param taskContext    * @return true/false    * @throws IOException    */
DECL|method|needsTaskCommit (TaskAttemptContext taskContext)
specifier|public
specifier|abstract
name|boolean
name|needsTaskCommit
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * To promote the task's temporary output to final output location    *     * The task's output is moved to the job's output directory.    *     * @param taskContext Context of the task whose output is being written.    * @throws IOException if commit is not     */
DECL|method|commitTask (TaskAttemptContext taskContext)
specifier|public
specifier|abstract
name|void
name|commitTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Discard the task output    *     * @param taskContext    * @throws IOException    */
DECL|method|abortTask (TaskAttemptContext taskContext)
specifier|public
specifier|abstract
name|void
name|abortTask
parameter_list|(
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method implements the new interface by calling the old method. Note    * that the input types are different between the new and old apis and this    * is a bridge between the two.    */
annotation|@
name|Override
DECL|method|setupJob (org.apache.hadoop.mapreduce.JobContext jobContext )
specifier|public
specifier|final
name|void
name|setupJob
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobContext
name|jobContext
parameter_list|)
throws|throws
name|IOException
block|{
name|setupJob
argument_list|(
operator|(
name|JobContext
operator|)
name|jobContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method implements the new interface by calling the old method. Note    * that the input types are different between the new and old apis and this    * is a bridge between the two.    * @deprecated Use {@link #commitJob(org.apache.hadoop.mapreduce.JobContext)}    *             or {@link #abortJob(org.apache.hadoop.mapreduce.JobContext, org.apache.hadoop.mapreduce.JobStatus.State)}    *             instead.    */
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|cleanupJob (org.apache.hadoop.mapreduce.JobContext context )
specifier|public
specifier|final
name|void
name|cleanupJob
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|cleanupJob
argument_list|(
operator|(
name|JobContext
operator|)
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method implements the new interface by calling the old method. Note    * that the input types are different between the new and old apis and this    * is a bridge between the two.    */
annotation|@
name|Override
DECL|method|commitJob (org.apache.hadoop.mapreduce.JobContext context )
specifier|public
specifier|final
name|void
name|commitJob
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|commitJob
argument_list|(
operator|(
name|JobContext
operator|)
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method implements the new interface by calling the old method. Note    * that the input types are different between the new and old apis and this    * is a bridge between the two.    */
annotation|@
name|Override
DECL|method|abortJob (org.apache.hadoop.mapreduce.JobContext context, org.apache.hadoop.mapreduce.JobStatus.State runState)
specifier|public
specifier|final
name|void
name|abortJob
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|JobContext
name|context
parameter_list|,
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
name|runState
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|state
init|=
name|JobStatus
operator|.
name|getOldNewJobRunState
argument_list|(
name|runState
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
name|JobStatus
operator|.
name|FAILED
operator|&&
name|state
operator|!=
name|JobStatus
operator|.
name|KILLED
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid job run state : "
operator|+
name|runState
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
name|abortJob
argument_list|(
operator|(
name|JobContext
operator|)
name|context
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method implements the new interface by calling the old method. Note    * that the input types are different between the new and old apis and this    * is a bridge between the two.    */
annotation|@
name|Override
specifier|public
specifier|final
DECL|method|setupTask (org.apache.hadoop.mapreduce.TaskAttemptContext taskContext )
name|void
name|setupTask
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
name|setupTask
argument_list|(
operator|(
name|TaskAttemptContext
operator|)
name|taskContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method implements the new interface by calling the old method. Note    * that the input types are different between the new and old apis and this    * is a bridge between the two.    */
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
DECL|method|needsTaskCommit (org.apache.hadoop.mapreduce.TaskAttemptContext taskContext )
name|needsTaskCommit
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|needsTaskCommit
argument_list|(
operator|(
name|TaskAttemptContext
operator|)
name|taskContext
argument_list|)
return|;
block|}
comment|/**    * This method implements the new interface by calling the old method. Note    * that the input types are different between the new and old apis and this    * is a bridge between the two.    */
annotation|@
name|Override
specifier|public
specifier|final
DECL|method|commitTask (org.apache.hadoop.mapreduce.TaskAttemptContext taskContext )
name|void
name|commitTask
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
name|commitTask
argument_list|(
operator|(
name|TaskAttemptContext
operator|)
name|taskContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method implements the new interface by calling the old method. Note    * that the input types are different between the new and old apis and this    * is a bridge between the two.    */
annotation|@
name|Override
specifier|public
specifier|final
DECL|method|abortTask (org.apache.hadoop.mapreduce.TaskAttemptContext taskContext )
name|void
name|abortTask
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptContext
name|taskContext
parameter_list|)
throws|throws
name|IOException
block|{
name|abortTask
argument_list|(
operator|(
name|TaskAttemptContext
operator|)
name|taskContext
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

