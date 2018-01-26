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
name|mapred
operator|.
name|JvmTask
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
name|CheckpointID
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
name|FSCheckpointID
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
name|security
operator|.
name|token
operator|.
name|JobTokenSelector
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
comment|/** Protocol that task child process uses to contact its parent process.  The  * parent is a daemon which which polls the central master for a new map or  * reduce task and runs it as a child process.  All communication between child  * and parent is via this protocol. */
end_comment

begin_interface
annotation|@
name|TokenInfo
argument_list|(
name|JobTokenSelector
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
DECL|interface|TaskUmbilicalProtocol
specifier|public
interface|interface
name|TaskUmbilicalProtocol
extends|extends
name|VersionedProtocol
block|{
comment|/**     * Changed the version to 2, since we have a new method getMapOutputs     * Changed version to 3 to have progress() return a boolean    * Changed the version to 4, since we have replaced     *         TaskUmbilicalProtocol.progress(String, float, String,     *         org.apache.hadoop.mapred.TaskStatus.Phase, Counters)     *         with statusUpdate(String, TaskStatus)    *     * Version 5 changed counters representation for HADOOP-2248    * Version 6 changes the TaskStatus representation for HADOOP-2208    * Version 7 changes the done api (via HADOOP-3140). It now expects whether    *           or not the task's output needs to be promoted.    * Version 8 changes {job|tip|task}id's to use their corresponding     * objects rather than strings.    * Version 9 changes the counter representation for HADOOP-1915    * Version 10 changed the TaskStatus format and added reportNextRecordRange    *            for HADOOP-153    * Version 11 Adds RPCs for task commit as part of HADOOP-3150    * Version 12 getMapCompletionEvents() now also indicates if the events are     *            stale or not. Hence the return type is a class that     *            encapsulates the events and whether to reset events index.    * Version 13 changed the getTask method signature for HADOOP-249    * Version 14 changed the getTask method signature for HADOOP-4232    * Version 15 Adds FAILED_UNCLEAN and KILLED_UNCLEAN states for HADOOP-4759    * Version 16 Change in signature of getTask() for HADOOP-5488    * Version 17 Modified TaskID to be aware of the new TaskTypes    * Version 18 Added numRequiredSlots to TaskStatus for MAPREDUCE-516    * Version 19 Added fatalError for child to communicate fatal errors to TT    * Version 20 Added methods to manage checkpoints    * Version 21 Added fastFail parameter to fatalError    * */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|21L
decl_stmt|;
comment|/**    * Called when a child task process starts, to get its task.    * @param context the JvmContext of the JVM w.r.t the TaskTracker that    *  launched it    * @return Task object    * @throws IOException     */
DECL|method|getTask (JvmContext context)
name|JvmTask
name|getTask
parameter_list|(
name|JvmContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Report child's progress to parent. Also invoked to report still alive (used    * to be in ping). It reports an AMFeedback used to propagate preemption requests.    *     * @param taskId task-id of the child    * @param taskStatus status of the child    * @throws IOException    * @throws InterruptedException    * @return True if the task is known    */
DECL|method|statusUpdate (TaskAttemptID taskId, TaskStatus taskStatus)
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
function_decl|;
comment|/** Report error messages back to parent.  Calls should be sparing, since all    *  such messages are held in the job tracker.    *  @param taskid the id of the task involved    *  @param trace the text to report    */
DECL|method|reportDiagnosticInfo (TaskAttemptID taskid, String trace)
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
function_decl|;
comment|/**    * Report the record range which is going to process next by the Task.    * @param taskid the id of the task involved    * @param range the range of record sequence nos    * @throws IOException    */
DECL|method|reportNextRecordRange (TaskAttemptID taskid, SortedRanges.Range range)
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
function_decl|;
comment|/** Report that the task is successfully completed.  Failure is assumed if    * the task process exits without calling this.    * @param taskid task's id    */
DECL|method|done (TaskAttemptID taskid)
name|void
name|done
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Report that the task is complete, but its commit is pending.    *     * @param taskId task's id    * @param taskStatus status of the child    * @throws IOException    */
DECL|method|commitPending (TaskAttemptID taskId, TaskStatus taskStatus)
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
function_decl|;
comment|/**    * Polling to know whether the task can go-ahead with commit     * @param taskid    * @return true/false     * @throws IOException    */
DECL|method|canCommit (TaskAttemptID taskid)
name|boolean
name|canCommit
parameter_list|(
name|TaskAttemptID
name|taskid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Report that a reduce-task couldn't shuffle map-outputs.*/
DECL|method|shuffleError (TaskAttemptID taskId, String message)
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
function_decl|;
comment|/** Report that the task encounted a local filesystem error.*/
DECL|method|fsError (TaskAttemptID taskId, String message)
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
function_decl|;
comment|/**    * Report that the task encounted a fatal error.    * @param taskId task's id    * @param message fail message    * @param fastFail flag to enable fast fail for task    */
DECL|method|fatalError (TaskAttemptID taskId, String message, boolean fastFail)
name|void
name|fatalError
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|,
name|String
name|message
parameter_list|,
name|boolean
name|fastFail
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called by a reduce task to get the map output locations for finished maps.    * Returns an update centered around the map-task-completion-events.     * The update also piggybacks the information whether the events copy at the     * task-tracker has changed or not. This will trigger some action at the     * child-process.    *    * @param fromIndex the index starting from which the locations should be     * fetched    * @param maxLocs the max number of locations to fetch    * @param id The attempt id of the task that is trying to communicate    * @return A {@link MapTaskCompletionEventsUpdate}     */
DECL|method|getMapCompletionEvents (JobID jobId, int fromIndex, int maxLocs, TaskAttemptID id)
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
function_decl|;
comment|/**    * Report to the AM that the task has been succesfully preempted.    *    * @param taskId task's id    * @param taskStatus status of the child    * @throws IOException    */
DECL|method|preempted (TaskAttemptID taskId, TaskStatus taskStatus)
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
function_decl|;
comment|/**    * Return the latest CheckpointID for the given TaskID. This provides    * the task with a way to locate the checkpointed data and restart from    * that point in the computation.    *    * @param taskID task's id    * @return the most recent checkpoint (if any) for this task    */
DECL|method|getCheckpointID (TaskID taskID)
name|TaskCheckpointID
name|getCheckpointID
parameter_list|(
name|TaskID
name|taskID
parameter_list|)
function_decl|;
comment|/**    * Send a CheckpointID for a given TaskID to be stored in the AM,    * to later restart a task from this checkpoint.    * @param tid    * @param cid    */
DECL|method|setCheckpointID (TaskID tid, TaskCheckpointID cid)
name|void
name|setCheckpointID
parameter_list|(
name|TaskID
name|tid
parameter_list|,
name|TaskCheckpointID
name|cid
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

