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

begin_comment
comment|/**  * This class is used by SimulatorTaskTrackers for signaling themselves when  * a task attempt finishes. The rationale for having this redundant event sent   * is that (1) this way it is possible to monitor all task completion events  * centrally from the Engine. (2) TTs used to call heartbeat() of the job   * tracker right after the task completed (so called "crazy heartbeats") not   * waiting for the heartbeat interval. If we wanted to simulate that we need   * to decouple task completion monitoring from periodic heartbeats.   */
end_comment

begin_class
DECL|class|TaskAttemptCompletionEvent
specifier|public
class|class
name|TaskAttemptCompletionEvent
extends|extends
name|SimulatorEvent
block|{
comment|/** The final status of the completed task. */
DECL|field|status
specifier|private
specifier|final
name|TaskStatus
name|status
decl_stmt|;
comment|/**    * Constructs a task completion event from a task status.    * @param listener the SimulatorTaskTracker the task is running on    * @param status the final status of the completed task. Precondition:     *                status.getRunState() must be either State.SUCCEEDED or     *                State.FAILED.    */
DECL|method|TaskAttemptCompletionEvent (SimulatorEventListener listener, TaskStatus status)
specifier|public
name|TaskAttemptCompletionEvent
parameter_list|(
name|SimulatorEventListener
name|listener
parameter_list|,
name|TaskStatus
name|status
parameter_list|)
block|{
name|super
argument_list|(
name|listener
argument_list|,
name|status
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
comment|/** Returns the final status of the task. */
DECL|method|getStatus ()
specifier|public
name|TaskStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
annotation|@
name|Override
DECL|method|realToString ()
specifier|protected
name|String
name|realToString
parameter_list|()
block|{
return|return
name|super
operator|.
name|realToString
argument_list|()
operator|+
literal|", taskID="
operator|+
name|status
operator|.
name|getTaskID
argument_list|()
return|;
block|}
block|}
end_class

end_unit

