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

begin_comment
comment|/**   *<code>RunningJob</code> is the user-interface to query for details on a   * running Map-Reduce job.  *   *<p>Clients can get hold of<code>RunningJob</code> via the {@link JobClient}  * and then query the running-job for details such as name, configuration,   * progress etc.</p>   *   * @see JobClient  * @deprecated Use {@link org.apache.hadoop.mapreduce.Job} instead  */
end_comment

begin_interface
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
DECL|interface|RunningJob
specifier|public
interface|interface
name|RunningJob
block|{
comment|/**    * Get the underlying job configuration    *    * @return the configuration of the job.    */
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
function_decl|;
comment|/**    * Get the job identifier.    *     * @return the job identifier.    */
DECL|method|getID ()
specifier|public
name|JobID
name|getID
parameter_list|()
function_decl|;
comment|/** @deprecated This method is deprecated and will be removed. Applications should     * rather use {@link #getID()}.    */
annotation|@
name|Deprecated
DECL|method|getJobID ()
specifier|public
name|String
name|getJobID
parameter_list|()
function_decl|;
comment|/**    * Get the name of the job.    *     * @return the name of the job.    */
DECL|method|getJobName ()
specifier|public
name|String
name|getJobName
parameter_list|()
function_decl|;
comment|/**    * Get the path of the submitted job configuration.    *     * @return the path of the submitted job configuration.    */
DECL|method|getJobFile ()
specifier|public
name|String
name|getJobFile
parameter_list|()
function_decl|;
comment|/**    * Get the URL where some job progress information will be displayed.    *     * @return the URL where some job progress information will be displayed.    */
DECL|method|getTrackingURL ()
specifier|public
name|String
name|getTrackingURL
parameter_list|()
function_decl|;
comment|/**    * Get the<i>progress</i> of the job's map-tasks, as a float between 0.0     * and 1.0.  When all map tasks have completed, the function returns 1.0.    *     * @return the progress of the job's map-tasks.    * @throws IOException    */
DECL|method|mapProgress ()
specifier|public
name|float
name|mapProgress
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the<i>progress</i> of the job's reduce-tasks, as a float between 0.0     * and 1.0.  When all reduce tasks have completed, the function returns 1.0.    *     * @return the progress of the job's reduce-tasks.    * @throws IOException    */
DECL|method|reduceProgress ()
specifier|public
name|float
name|reduceProgress
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the<i>progress</i> of the job's cleanup-tasks, as a float between 0.0     * and 1.0.  When all cleanup tasks have completed, the function returns 1.0.    *     * @return the progress of the job's cleanup-tasks.    * @throws IOException    */
DECL|method|cleanupProgress ()
specifier|public
name|float
name|cleanupProgress
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the<i>progress</i> of the job's setup-tasks, as a float between 0.0     * and 1.0.  When all setup tasks have completed, the function returns 1.0.    *     * @return the progress of the job's setup-tasks.    * @throws IOException    */
DECL|method|setupProgress ()
specifier|public
name|float
name|setupProgress
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Check if the job is finished or not.     * This is a non-blocking call.    *     * @return<code>true</code> if the job is complete, else<code>false</code>.    * @throws IOException    */
DECL|method|isComplete ()
specifier|public
name|boolean
name|isComplete
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Check if the job completed successfully.     *     * @return<code>true</code> if the job succeeded, else<code>false</code>.    * @throws IOException    */
DECL|method|isSuccessful ()
specifier|public
name|boolean
name|isSuccessful
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Blocks until the job is complete.    *     * @throws IOException    */
DECL|method|waitForCompletion ()
specifier|public
name|void
name|waitForCompletion
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the current state of the Job.    * {@link JobStatus}    *     * @throws IOException    */
DECL|method|getJobState ()
specifier|public
name|int
name|getJobState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Kill the running job.  Blocks until all job tasks have been    * killed as well.  If the job is no longer running, it simply returns.    *     * @throws IOException    */
DECL|method|killJob ()
specifier|public
name|void
name|killJob
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the priority of a running job.    * @param priority the new priority for the job.    * @throws IOException    */
DECL|method|setJobPriority (String priority)
specifier|public
name|void
name|setJobPriority
parameter_list|(
name|String
name|priority
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get events indicating completion (success/failure) of component tasks.    *      * @param startFrom index to start fetching events from    * @return an array of {@link TaskCompletionEvent}s    * @throws IOException    */
DECL|method|getTaskCompletionEvents (int startFrom)
specifier|public
name|TaskCompletionEvent
index|[]
name|getTaskCompletionEvents
parameter_list|(
name|int
name|startFrom
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Kill indicated task attempt.    *     * @param taskId the id of the task to be terminated.    * @param shouldFail if true the task is failed and added to failed tasks     *                   list, otherwise it is just killed, w/o affecting     *                   job failure status.      * @throws IOException    */
DECL|method|killTask (TaskAttemptID taskId, boolean shouldFail)
specifier|public
name|void
name|killTask
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|boolean
name|shouldFail
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** @deprecated Applications should rather use {@link #killTask(TaskAttemptID, boolean)}*/
annotation|@
name|Deprecated
DECL|method|killTask (String taskId, boolean shouldFail)
specifier|public
name|void
name|killTask
parameter_list|(
name|String
name|taskId
parameter_list|,
name|boolean
name|shouldFail
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the counters for this job.    *     * @return the counters for this job or null if the job has been retired.    * @throws IOException    */
DECL|method|getCounters ()
specifier|public
name|Counters
name|getCounters
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the diagnostic messages for a given task attempt.    * @param taskid    * @return the list of diagnostic messages for the task    * @throws IOException    */
DECL|method|getTaskDiagnostics (TaskAttemptID taskid)
specifier|public
name|String
index|[]
name|getTaskDiagnostics
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the url where history file is archived. Returns empty string if     * history file is not available yet.     *     * @return the url where history file is archived    * @throws IOException    */
DECL|method|getHistoryUrl ()
specifier|public
name|String
name|getHistoryUrl
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Check whether the job has been removed from JobTracker memory and retired.    * On retire, the job history file is copied to a location known by     * {@link #getHistoryUrl()}    * @return<code>true</code> if the job retired, else<code>false</code>.    * @throws IOException    */
DECL|method|isRetired ()
specifier|public
name|boolean
name|isRetired
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

