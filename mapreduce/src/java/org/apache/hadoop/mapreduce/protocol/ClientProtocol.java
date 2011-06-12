begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|protocol
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSelector
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
name|VersionedProtocol
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
operator|.
name|JobTrackerStatus
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
name|ClusterMetrics
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
name|Counters
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
name|QueueAclsInfo
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
name|QueueInfo
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
name|TaskCompletionEvent
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
name|TaskTrackerInfo
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|server
operator|.
name|jobtracker
operator|.
name|State
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
name|Credentials
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
name|KerberosInfo
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
name|authorize
operator|.
name|AccessControlList
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenInfo
import|;
end_import

begin_comment
comment|/**   * Protocol that a JobClient and the central JobTracker use to communicate.  The  * JobClient can use these methods to submit a Job for execution, and learn about  * the current system status.  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|JTConfig
operator|.
name|JT_USER_NAME
argument_list|)
annotation|@
name|TokenInfo
argument_list|(
name|DelegationTokenSelector
operator|.
name|class
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|ClientProtocol
specifier|public
interface|interface
name|ClientProtocol
extends|extends
name|VersionedProtocol
block|{
comment|/*     *Changing the versionID to 2L since the getTaskCompletionEvents method has    *changed.    *Changed to 4 since killTask(String,boolean) is added    *Version 4: added jobtracker state to ClusterStatus    *Version 5: max_tasks in ClusterStatus is replaced by    * max_map_tasks and max_reduce_tasks for HADOOP-1274    * Version 6: change the counters representation for HADOOP-2248    * Version 7: added getAllJobs for HADOOP-2487    * Version 8: change {job|task}id's to use corresponding objects rather that strings.    * Version 9: change the counter representation for HADOOP-1915    * Version 10: added getSystemDir for HADOOP-3135    * Version 11: changed JobProfile to include the queue name for HADOOP-3698    * Version 12: Added getCleanupTaskReports and     *             cleanupProgress to JobStatus as part of HADOOP-3150    * Version 13: Added getJobQueueInfos and getJobQueueInfo(queue name)    *             and getAllJobs(queue) as a part of HADOOP-3930    * Version 14: Added setPriority for HADOOP-4124    * Version 15: Added KILLED status to JobStatus as part of HADOOP-3924                * Version 16: Added getSetupTaskReports and     *             setupProgress to JobStatus as part of HADOOP-4261               * Version 17: getClusterStatus returns the amount of memory used by     *             the server. HADOOP-4435    * Version 18: Added blacklisted trackers to the ClusterStatus     *             for HADOOP-4305    * Version 19: Modified TaskReport to have TIP status and modified the    *             method getClusterStatus() to take a boolean argument    *             for HADOOP-4807    * Version 20: Modified ClusterStatus to have the tasktracker expiry    *             interval for HADOOP-4939    * Version 21: Modified TaskID to be aware of the new TaskTypes                                     * Version 22: Added method getQueueAclsForCurrentUser to get queue acls info    *             for a user    * Version 23: Modified the JobQueueInfo class to inlucde queue state.    *             Part of HADOOP-5913.      * Version 24: Modified ClusterStatus to include BlackListInfo class which     *             encapsulates reasons and report for blacklisted node.              * Version 25: Added fields to JobStatus for HADOOP-817.       * Version 26: Added properties to JobQueueInfo as part of MAPREDUCE-861.    *              added new api's getRootQueues and    *              getChildQueues(String queueName)    * Version 27: Changed protocol to use new api objects. And the protocol is     *             renamed from JobSubmissionProtocol to ClientProtocol.    * Version 28: Added getJobHistoryDir() as part of MAPREDUCE-975.    * Version 29: Added reservedSlots, runningTasks and totalJobSubmissions    *             to ClusterMetrics as part of MAPREDUCE-1048.    * Version 30: Job submission files are uploaded to a staging area under    *             user home dir. JobTracker reads the required files from the    *             staging area using user credentials passed via the rpc.    * Version 31: Added TokenStorage to submitJob          * Version 32: Added delegation tokens (add, renew, cancel)    * Version 33: Added JobACLs to JobStatus as part of MAPREDUCE-1307    * Version 34: Modified submitJob to use Credentials instead of TokenStorage.    * Version 35: Added the method getQueueAdmins(queueName) as part of    *             MAPREDUCE-1664.    * Version 36: Added the method getJobTrackerStatus() as part of    *             MAPREDUCE-2337.    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|36L
decl_stmt|;
comment|/**    * Allocate a name for the job.    * @return a unique job name for submitting jobs.    * @throws IOException    */
DECL|method|getNewJobID ()
specifier|public
name|JobID
name|getNewJobID
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Submit a Job for execution.  Returns the latest profile for    * that job.    */
DECL|method|submitJob (JobID jobId, String jobSubmitDir, Credentials ts)
specifier|public
name|JobStatus
name|submitJob
parameter_list|(
name|JobID
name|jobId
parameter_list|,
name|String
name|jobSubmitDir
parameter_list|,
name|Credentials
name|ts
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the current status of the cluster    *     * @return summary of the state of the cluster    */
DECL|method|getClusterMetrics ()
specifier|public
name|ClusterMetrics
name|getClusterMetrics
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get JobTracker's state    *     * @return {@link State} of the JobTracker    * @throws IOException    * @throws InterruptedException    * @deprecated Use {@link #getJobTrackerStatus()} instead.    */
annotation|@
name|Deprecated
DECL|method|getJobTrackerState ()
specifier|public
name|State
name|getJobTrackerState
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the JobTracker's status.    *     * @return {@link JobTrackerStatus} of the JobTracker    * @throws IOException    * @throws InterruptedException    */
DECL|method|getJobTrackerStatus ()
specifier|public
name|JobTrackerStatus
name|getJobTrackerStatus
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
DECL|method|getTaskTrackerExpiryInterval ()
specifier|public
name|long
name|getTaskTrackerExpiryInterval
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the administrators of the given job-queue.    * This method is for hadoop internal use only.    * @param queueName    * @return Queue administrators ACL for the queue to which job is    *         submitted to    * @throws IOException    */
DECL|method|getQueueAdmins (String queueName)
specifier|public
name|AccessControlList
name|getQueueAdmins
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Kill the indicated job    */
DECL|method|killJob (JobID jobid)
specifier|public
name|void
name|killJob
parameter_list|(
name|JobID
name|jobid
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Set the priority of the specified job    * @param jobid ID of the job    * @param priority Priority to be set for the job    */
DECL|method|setJobPriority (JobID jobid, String priority)
specifier|public
name|void
name|setJobPriority
parameter_list|(
name|JobID
name|jobid
parameter_list|,
name|String
name|priority
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Kill indicated task attempt.    * @param taskId the id of the task to kill.    * @param shouldFail if true the task is failed and added to failed tasks list, otherwise    * it is just killed, w/o affecting job failure status.      */
DECL|method|killTask (TaskAttemptID taskId, boolean shouldFail)
specifier|public
name|boolean
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
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Grab a handle to a job that is already known to the JobTracker.    * @return Status of the job, or null if not found.    */
DECL|method|getJobStatus (JobID jobid)
specifier|public
name|JobStatus
name|getJobStatus
parameter_list|(
name|JobID
name|jobid
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Grab the current job counters    */
DECL|method|getJobCounters (JobID jobid)
specifier|public
name|Counters
name|getJobCounters
parameter_list|(
name|JobID
name|jobid
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Grab a bunch of info on the tasks that make up the job    */
DECL|method|getTaskReports (JobID jobid, TaskType type)
specifier|public
name|TaskReport
index|[]
name|getTaskReports
parameter_list|(
name|JobID
name|jobid
parameter_list|,
name|TaskType
name|type
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * A MapReduce system always operates on a single filesystem.  This     * function returns the fs name.  ('local' if the localfs; 'addr:port'     * if dfs).  The client can then copy files into the right locations     * prior to submitting the job.    */
DECL|method|getFilesystemName ()
specifier|public
name|String
name|getFilesystemName
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**     * Get all the jobs submitted.     * @return array of JobStatus for the submitted jobs    */
DECL|method|getAllJobs ()
specifier|public
name|JobStatus
index|[]
name|getAllJobs
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get task completion events for the jobid, starting from fromEventId.     * Returns empty array if no events are available.     * @param jobid job id     * @param fromEventId event id to start from.    * @param maxEvents the max number of events we want to look at     * @return array of task completion events.     * @throws IOException    */
DECL|method|getTaskCompletionEvents (JobID jobid, int fromEventId, int maxEvents)
specifier|public
name|TaskCompletionEvent
index|[]
name|getTaskCompletionEvents
parameter_list|(
name|JobID
name|jobid
parameter_list|,
name|int
name|fromEventId
parameter_list|,
name|int
name|maxEvents
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get the diagnostics for a given task in a given job    * @param taskId the id of the task    * @return an array of the diagnostic messages    */
DECL|method|getTaskDiagnostics (TaskAttemptID taskId)
specifier|public
name|String
index|[]
name|getTaskDiagnostics
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**     * Get all active trackers in cluster.     * @return array of TaskTrackerInfo    */
DECL|method|getActiveTrackers ()
specifier|public
name|TaskTrackerInfo
index|[]
name|getActiveTrackers
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**     * Get all blacklisted trackers in cluster.     * @return array of TaskTrackerInfo    */
DECL|method|getBlacklistedTrackers ()
specifier|public
name|TaskTrackerInfo
index|[]
name|getBlacklistedTrackers
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Grab the jobtracker system directory path     * where job-specific files are to be placed.    *     * @return the system directory where job-specific files are to be placed.    */
DECL|method|getSystemDir ()
specifier|public
name|String
name|getSystemDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get a hint from the JobTracker     * where job-specific files are to be placed.    *     * @return the directory where job-specific files are to be placed.    */
DECL|method|getStagingAreaDir ()
specifier|public
name|String
name|getStagingAreaDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Gets the directory location of the completed job history files.    * @throws IOException    * @throws InterruptedException    */
DECL|method|getJobHistoryDir ()
specifier|public
name|String
name|getJobHistoryDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Gets set of Queues associated with the Job Tracker    *     * @return Array of the Queue Information Object    * @throws IOException     */
DECL|method|getQueues ()
specifier|public
name|QueueInfo
index|[]
name|getQueues
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Gets scheduling information associated with the particular Job queue    *     * @param queueName Queue Name    * @return Scheduling Information of the Queue    * @throws IOException     */
DECL|method|getQueue (String queueName)
specifier|public
name|QueueInfo
name|getQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Gets the Queue ACLs for current user    * @return array of QueueAclsInfo object for current user.    * @throws IOException    */
DECL|method|getQueueAclsForCurrentUser ()
specifier|public
name|QueueAclsInfo
index|[]
name|getQueueAclsForCurrentUser
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Gets the root level queues.    * @return array of JobQueueInfo object.    * @throws IOException    */
DECL|method|getRootQueues ()
specifier|public
name|QueueInfo
index|[]
name|getRootQueues
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Returns immediate children of queueName.    * @param queueName    * @return array of JobQueueInfo which are children of queueName    * @throws IOException    */
DECL|method|getChildQueues (String queueName)
specifier|public
name|QueueInfo
index|[]
name|getChildQueues
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Get a new delegation token.    * @param renewer the user other than the creator (if any) that can renew the     *        token    * @return the new delegation token    * @throws IOException    * @throws InterruptedException    */
specifier|public
DECL|method|getDelegationToken (Text renewer )
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Renew an existing delegation token    * @param token the token to renew    * @return the new expiration time    * @throws IOException    * @throws InterruptedException    */
DECL|method|renewDelegationToken (Token<DelegationTokenIdentifier> token )
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**    * Cancel a delegation token.    * @param token the token to cancel    * @throws IOException    * @throws InterruptedException    */
DECL|method|cancelDelegationToken (Token<DelegationTokenIdentifier> token )
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

