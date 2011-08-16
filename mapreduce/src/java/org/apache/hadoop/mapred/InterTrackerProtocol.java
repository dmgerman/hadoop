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
name|tasktracker
operator|.
name|TTConfig
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

begin_comment
comment|/**   * Protocol that a TaskTracker and the central JobTracker use to communicate.  * The JobTracker is the Server, which implements this protocol.  */
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
argument_list|,
name|clientPrincipal
operator|=
name|TTConfig
operator|.
name|TT_USER_NAME
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|InterTrackerProtocol
interface|interface
name|InterTrackerProtocol
extends|extends
name|VersionedProtocol
block|{
comment|/**    * version 3 introduced to replace     * emitHearbeat/pollForNewTask/pollForTaskWithClosedJob with    * {@link #heartbeat(TaskTrackerStatus, boolean, boolean, boolean, short)}    * version 4 changed TaskReport for HADOOP-549.    * version 5 introduced that removes locateMapOutputs and instead uses    * getTaskCompletionEvents to figure finished maps and fetch the outputs    * version 6 adds maxTasks to TaskTrackerStatus for HADOOP-1245    * version 7 replaces maxTasks by maxMapTasks and maxReduceTasks in     * TaskTrackerStatus for HADOOP-1274    * Version 8: HeartbeatResponse is added with the next heartbeat interval.    * version 9 changes the counter representation for HADOOP-2248    * version 10 changes the TaskStatus representation for HADOOP-2208    * version 11 changes string to JobID in getTaskCompletionEvents().    * version 12 changes the counters representation for HADOOP-1915    * version 13 added call getBuildVersion() for HADOOP-236    * Version 14: replaced getFilesystemName with getSystemDir for HADOOP-3135    * Version 15: Changed format of Task and TaskStatus for HADOOP-153    * Version 16: adds ResourceStatus to TaskTrackerStatus for HADOOP-3759    * Version 17: Changed format of Task and TaskStatus for HADOOP-3150    * Version 18: Changed status message due to changes in TaskStatus    * Version 19: Changed heartbeat to piggyback JobTracker restart information                  so that the TaskTracker can synchronize itself.    * Version 20: Changed status message due to changes in TaskStatus    *             (HADOOP-4232)    * Version 21: Changed information reported in TaskTrackerStatus'    *             ResourceStatus and the corresponding accessor methods    *             (HADOOP-4035)    * Version 22: Replaced parameter 'initialContact' with 'restarted'     *             in heartbeat method (HADOOP-4305)     * Version 23: Added parameter 'initialContact' again in heartbeat method    *            (HADOOP-4869)     * Version 24: Changed format of Task and TaskStatus for HADOOP-4759     * Version 25: JobIDs are passed in response to JobTracker restart     * Version 26: Modified TaskID to be aware of the new TaskTypes    * Version 27: Added numRequiredSlots to TaskStatus for MAPREDUCE-516    * Version 28: Adding node health status to TaskStatus for MAPREDUCE-211    * Version 29: Adding user name to the serialized Task for use by TT.    * Version 30: Adding available memory and CPU usage information on TT to    *             TaskTrackerStatus for MAPREDUCE-1218    * Version 31: Efficient serialization format for Framework counters    *             (MAPREDUCE-901)    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|31L
decl_stmt|;
DECL|field|TRACKERS_OK
specifier|public
specifier|final
specifier|static
name|int
name|TRACKERS_OK
init|=
literal|0
decl_stmt|;
DECL|field|UNKNOWN_TASKTRACKER
specifier|public
specifier|final
specifier|static
name|int
name|UNKNOWN_TASKTRACKER
init|=
literal|1
decl_stmt|;
comment|/**    * Called regularly by the {@link TaskTracker} to update the status of its     * tasks within the job tracker. {@link JobTracker} responds with a     * {@link HeartbeatResponse} that directs the     * {@link TaskTracker} to undertake a series of 'actions'     * (see {@link org.apache.hadoop.mapred.TaskTrackerAction.ActionType}).      *     * {@link TaskTracker} must also indicate whether this is the first     * interaction (since state refresh) and acknowledge the last response    * it received from the {@link JobTracker}     *     * @param status the status update    * @param restarted<code>true</code> if the process has just started or     *                   restarted,<code>false</code> otherwise    * @param initialContact<code>true</code> if this is first interaction since    *                       'refresh',<code>false</code> otherwise.    * @param acceptNewTasks<code>true</code> if the {@link TaskTracker} is    *                       ready to accept new tasks to run.                     * @param responseId the last responseId successfully acted upon by the    *                   {@link TaskTracker}.    * @return a {@link org.apache.hadoop.mapred.HeartbeatResponse} with     *         fresh instructions.    */
DECL|method|heartbeat (TaskTrackerStatus status, boolean restarted, boolean initialContact, boolean acceptNewTasks, short responseId)
name|HeartbeatResponse
name|heartbeat
parameter_list|(
name|TaskTrackerStatus
name|status
parameter_list|,
name|boolean
name|restarted
parameter_list|,
name|boolean
name|initialContact
parameter_list|,
name|boolean
name|acceptNewTasks
parameter_list|,
name|short
name|responseId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * The task tracker calls this once, to discern where it can find    * files referred to by the JobTracker    */
DECL|method|getFilesystemName ()
specifier|public
name|String
name|getFilesystemName
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Report a problem to the job tracker.    * @param taskTracker the name of the task tracker    * @param errorClass the kind of error (eg. the class that was thrown)    * @param errorMessage the human readable error message    * @throws IOException if there was a problem in communication or on the    *                     remote side    */
DECL|method|reportTaskTrackerError (String taskTracker, String errorClass, String errorMessage)
specifier|public
name|void
name|reportTaskTrackerError
parameter_list|(
name|String
name|taskTracker
parameter_list|,
name|String
name|errorClass
parameter_list|,
name|String
name|errorMessage
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get task completion events for the jobid, starting from fromEventId.     * Returns empty aray if no events are available.     * @param jobid job id     * @param fromEventId event id to start from.     * @param maxEvents the max number of events we want to look at    * @return array of task completion events.     * @throws IOException    */
DECL|method|getTaskCompletionEvents (JobID jobid, int fromEventId , int maxEvents)
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
function_decl|;
comment|/**    * Grab the jobtracker system directory path where job-specific files are to be placed.    *     * @return the system directory where job-specific files are to be placed.    */
DECL|method|getSystemDir ()
specifier|public
name|String
name|getSystemDir
parameter_list|()
function_decl|;
comment|/**    * Returns the buildVersion of the JobTracker     */
DECL|method|getBuildVersion ()
specifier|public
name|String
name|getBuildVersion
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

