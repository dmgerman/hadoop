begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.rm.preemption
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
operator|.
name|preemption
package|;
end_package

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
name|mapred
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
name|mapred
operator|.
name|TaskID
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|v2
operator|.
name|api
operator|.
name|records
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
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|Container
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|PreemptionMessage
import|;
end_import

begin_comment
comment|/**  * Policy encoding the {@link org.apache.hadoop.mapreduce.v2.app.MRAppMaster}  * response to preemption requests from the ResourceManager.  * @see org.apache.hadoop.mapreduce.v2.app.rm.RMContainerAllocator  */
end_comment

begin_interface
DECL|interface|AMPreemptionPolicy
specifier|public
interface|interface
name|AMPreemptionPolicy
block|{
DECL|class|Context
specifier|public
specifier|abstract
class|class
name|Context
block|{
comment|/**      * @param container ID of container to preempt      * @return Task associated with the running container or<code>null</code>      * if no task is bound to that container.      */
DECL|method|getTaskAttempt (ContainerId container)
specifier|public
specifier|abstract
name|TaskAttemptId
name|getTaskAttempt
parameter_list|(
name|ContainerId
name|container
parameter_list|)
function_decl|;
comment|/**      * Method provides the complete list of containers running task of type t      * for this AM.      * @param t the type of containers      * @return a map containing      */
DECL|method|getContainers (TaskType t)
specifier|public
specifier|abstract
name|List
argument_list|<
name|Container
argument_list|>
name|getContainers
parameter_list|(
name|TaskType
name|t
parameter_list|)
function_decl|;
block|}
DECL|method|init (AppContext context)
specifier|public
name|void
name|init
parameter_list|(
name|AppContext
name|context
parameter_list|)
function_decl|;
comment|/**    * Callback informing the policy of ResourceManager. requests for resources    * to return to the cluster. The policy may take arbitrary action to satisfy    * requests by checkpointing task state, returning containers, or ignoring    * requests. The RM may elect to enforce these requests by forcibly killing    * containers not returned after some duration.    * @param context Handle to the current state of running containers    * @param preemptionRequests Request from RM for resources to return.    */
DECL|method|preempt (Context context, PreemptionMessage preemptionRequests)
specifier|public
name|void
name|preempt
parameter_list|(
name|Context
name|context
parameter_list|,
name|PreemptionMessage
name|preemptionRequests
parameter_list|)
function_decl|;
comment|/**    * This method is invoked by components interested to learn whether a certain    * task is being preempted.    * @param attemptID Task attempt to query    * @return true if this attempt is being preempted    */
DECL|method|isPreempted (TaskAttemptId attemptID)
specifier|public
name|boolean
name|isPreempted
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
function_decl|;
comment|/**    * This method is used to report to the policy that a certain task has been    * successfully preempted (for bookeeping, counters, etc..)    * @param attemptID Task attempt that preempted    */
DECL|method|reportSuccessfulPreemption (TaskAttemptID attemptID)
specifier|public
name|void
name|reportSuccessfulPreemption
parameter_list|(
name|TaskAttemptID
name|attemptID
parameter_list|)
function_decl|;
comment|/**    * Callback informing the policy of containers exiting with a failure. This    * allows the policy to implemnt cleanup/compensating actions.    * @param attemptID Task attempt that failed    */
DECL|method|handleFailedContainer (TaskAttemptId attemptID)
specifier|public
name|void
name|handleFailedContainer
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
function_decl|;
comment|/**    * Callback informing the policy of containers exiting cleanly. This is    * reported to the policy for bookeeping purposes.    * @param attemptID Task attempt that completed    */
DECL|method|handleCompletedContainer (TaskAttemptId attemptID)
specifier|public
name|void
name|handleCompletedContainer
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|)
function_decl|;
comment|/**    * Method to retrieve the latest checkpoint for a given {@link TaskID}    * @param taskId TaskID    * @return CheckpointID associated with this task or null    */
DECL|method|getCheckpointID (TaskID taskId)
specifier|public
name|TaskCheckpointID
name|getCheckpointID
parameter_list|(
name|TaskID
name|taskId
parameter_list|)
function_decl|;
comment|/**    * Method to store the latest {@link    * org.apache.hadoop.mapreduce.checkpoint.CheckpointID} for a given {@link    * TaskID}. Assigning a null is akin to remove all previous checkpoints for    * this task.    * @param taskId TaskID    * @param cid Checkpoint to assign or<tt>null</tt> to remove it.    */
DECL|method|setCheckpointID (TaskID taskId, TaskCheckpointID cid)
specifier|public
name|void
name|setCheckpointID
parameter_list|(
name|TaskID
name|taskId
parameter_list|,
name|TaskCheckpointID
name|cid
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

