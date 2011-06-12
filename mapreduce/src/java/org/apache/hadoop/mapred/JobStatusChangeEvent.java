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
comment|/**  * {@link JobStatusChangeEvent} tracks the change in job's status. Job's   * status can change w.r.t   *   - run state i.e PREP, RUNNING, FAILED, KILLED, SUCCEEDED  *   - start time  *   - priority  * Note that job times can change as the job can get restarted.  */
end_comment

begin_class
DECL|class|JobStatusChangeEvent
class|class
name|JobStatusChangeEvent
extends|extends
name|JobChangeEvent
block|{
comment|// Events in job status that can lead to a job-status change
DECL|enum|EventType
DECL|enumConstant|RUN_STATE_CHANGED
DECL|enumConstant|START_TIME_CHANGED
DECL|enumConstant|PRIORITY_CHANGED
specifier|static
enum|enum
name|EventType
block|{
name|RUN_STATE_CHANGED
block|,
name|START_TIME_CHANGED
block|,
name|PRIORITY_CHANGED
block|}
DECL|field|oldStatus
specifier|private
name|JobStatus
name|oldStatus
decl_stmt|;
DECL|field|newStatus
specifier|private
name|JobStatus
name|newStatus
decl_stmt|;
DECL|field|eventType
specifier|private
name|EventType
name|eventType
decl_stmt|;
DECL|method|JobStatusChangeEvent (JobInProgress jip, EventType eventType, JobStatus oldStatus, JobStatus newStatus)
name|JobStatusChangeEvent
parameter_list|(
name|JobInProgress
name|jip
parameter_list|,
name|EventType
name|eventType
parameter_list|,
name|JobStatus
name|oldStatus
parameter_list|,
name|JobStatus
name|newStatus
parameter_list|)
block|{
name|super
argument_list|(
name|jip
argument_list|)
expr_stmt|;
name|this
operator|.
name|oldStatus
operator|=
name|oldStatus
expr_stmt|;
name|this
operator|.
name|newStatus
operator|=
name|newStatus
expr_stmt|;
name|this
operator|.
name|eventType
operator|=
name|eventType
expr_stmt|;
block|}
comment|/**    * Create a {@link JobStatusChangeEvent} indicating the state has changed.     * Note that here we assume that the state change doesnt care about the old    * state.    */
DECL|method|JobStatusChangeEvent (JobInProgress jip, EventType eventType, JobStatus status)
name|JobStatusChangeEvent
parameter_list|(
name|JobInProgress
name|jip
parameter_list|,
name|EventType
name|eventType
parameter_list|,
name|JobStatus
name|status
parameter_list|)
block|{
name|this
argument_list|(
name|jip
argument_list|,
name|eventType
argument_list|,
name|status
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a event-type that caused the state change    */
DECL|method|getEventType ()
name|EventType
name|getEventType
parameter_list|()
block|{
return|return
name|eventType
return|;
block|}
comment|/**    * Get the old job status    */
DECL|method|getOldStatus ()
name|JobStatus
name|getOldStatus
parameter_list|()
block|{
return|return
name|oldStatus
return|;
block|}
comment|/**    * Get the new job status as a result of the events    */
DECL|method|getNewStatus ()
name|JobStatus
name|getNewStatus
parameter_list|()
block|{
return|return
name|newStatus
return|;
block|}
block|}
end_class

end_unit

