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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Manages information about the {@link TaskTracker}s running on a cluster.  * This interface exits primarily to test the {@link JobTracker}, and is not  * intended to be implemented by users.  */
end_comment

begin_interface
DECL|interface|TaskTrackerManager
interface|interface
name|TaskTrackerManager
block|{
comment|/**    * @return A collection of the {@link TaskTrackerStatus} for the tasktrackers    * being managed.    */
DECL|method|taskTrackers ()
specifier|public
name|Collection
argument_list|<
name|TaskTrackerStatus
argument_list|>
name|taskTrackers
parameter_list|()
function_decl|;
comment|/**    * @return The number of unique hosts running tasktrackers.    */
DECL|method|getNumberOfUniqueHosts ()
specifier|public
name|int
name|getNumberOfUniqueHosts
parameter_list|()
function_decl|;
comment|/**    * @return a summary of the cluster's status.    */
DECL|method|getClusterStatus ()
specifier|public
name|ClusterStatus
name|getClusterStatus
parameter_list|()
function_decl|;
comment|/**    * Registers a {@link JobInProgressListener} for updates from this    * {@link TaskTrackerManager}.    * @param jobInProgressListener the {@link JobInProgressListener} to add    */
DECL|method|addJobInProgressListener (JobInProgressListener listener)
specifier|public
name|void
name|addJobInProgressListener
parameter_list|(
name|JobInProgressListener
name|listener
parameter_list|)
function_decl|;
comment|/**    * Unregisters a {@link JobInProgressListener} from this    * {@link TaskTrackerManager}.    * @param jobInProgressListener the {@link JobInProgressListener} to remove    */
DECL|method|removeJobInProgressListener (JobInProgressListener listener)
specifier|public
name|void
name|removeJobInProgressListener
parameter_list|(
name|JobInProgressListener
name|listener
parameter_list|)
function_decl|;
comment|/**    * Return the {@link QueueManager} which manages the queues in this    * {@link TaskTrackerManager}.    *    * @return the {@link QueueManager}    */
DECL|method|getQueueManager ()
specifier|public
name|QueueManager
name|getQueueManager
parameter_list|()
function_decl|;
comment|/**    * Return the current heartbeat interval that's used by {@link TaskTracker}s.    *    * @return the heartbeat interval used by {@link TaskTracker}s    */
DECL|method|getNextHeartbeatInterval ()
specifier|public
name|int
name|getNextHeartbeatInterval
parameter_list|()
function_decl|;
comment|/**    * Kill the job identified by jobid    *     * @param jobid    * @throws IOException    */
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
function_decl|;
comment|/**    * Obtain the job object identified by jobid    *     * @param jobid    * @return jobInProgress object    */
DECL|method|getJob (JobID jobid)
specifier|public
name|JobInProgress
name|getJob
parameter_list|(
name|JobID
name|jobid
parameter_list|)
function_decl|;
comment|/**    * Mark the task attempt identified by taskid to be killed    *     * @param taskid task to kill    * @param shouldFail whether to count the task as failed    * @return true if the task was found and successfully marked to kill    */
DECL|method|killTask (TaskAttemptID taskid, boolean shouldFail)
specifier|public
name|boolean
name|killTask
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|,
name|boolean
name|shouldFail
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Initialize the Job    *     * @param job JobInProgress object    */
DECL|method|initJob (JobInProgress job)
specifier|public
name|void
name|initJob
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
function_decl|;
comment|/**    * Fail a job.    *     * @param job JobInProgress object    */
DECL|method|failJob (JobInProgress job)
specifier|public
name|void
name|failJob
parameter_list|(
name|JobInProgress
name|job
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

