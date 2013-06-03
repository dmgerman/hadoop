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

begin_comment
comment|/**  * This is used to track task completion events on   * job tracker.   */
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
DECL|class|TaskCompletionEvent
specifier|public
class|class
name|TaskCompletionEvent
extends|extends
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskCompletionEvent
block|{
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|enum|Status
DECL|enumConstant|FAILED
DECL|enumConstant|KILLED
DECL|enumConstant|SUCCEEDED
DECL|enumConstant|OBSOLETE
DECL|enumConstant|TIPFAILED
specifier|static
specifier|public
enum|enum
name|Status
block|{
name|FAILED
block|,
name|KILLED
block|,
name|SUCCEEDED
block|,
name|OBSOLETE
block|,
name|TIPFAILED
block|}
empty_stmt|;
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|TaskCompletionEvent
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|TaskCompletionEvent
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Default constructor for Writable.    *    */
DECL|method|TaskCompletionEvent ()
specifier|public
name|TaskCompletionEvent
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructor. eventId should be created externally and incremented    * per event for each job.     * @param eventId event id, event id should be unique and assigned in    *  incrementally, starting from 0.     * @param taskId task id    * @param status task's status     * @param taskTrackerHttp task tracker's host:port for http.     */
DECL|method|TaskCompletionEvent (int eventId, TaskAttemptID taskId, int idWithinJob, boolean isMap, Status status, String taskTrackerHttp)
specifier|public
name|TaskCompletionEvent
parameter_list|(
name|int
name|eventId
parameter_list|,
name|TaskAttemptID
name|taskId
parameter_list|,
name|int
name|idWithinJob
parameter_list|,
name|boolean
name|isMap
parameter_list|,
name|Status
name|status
parameter_list|,
name|String
name|taskTrackerHttp
parameter_list|)
block|{
name|super
argument_list|(
name|eventId
argument_list|,
name|taskId
argument_list|,
name|idWithinJob
argument_list|,
name|isMap
argument_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskCompletionEvent
operator|.
name|Status
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|taskTrackerHttp
argument_list|)
expr_stmt|;
block|}
DECL|method|downgrade ( org.apache.hadoop.mapreduce.TaskCompletionEvent event)
specifier|static
name|TaskCompletionEvent
name|downgrade
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskCompletionEvent
name|event
parameter_list|)
block|{
return|return
operator|new
name|TaskCompletionEvent
argument_list|(
name|event
operator|.
name|getEventId
argument_list|()
argument_list|,
name|TaskAttemptID
operator|.
name|downgrade
argument_list|(
name|event
operator|.
name|getTaskAttemptId
argument_list|()
argument_list|)
argument_list|,
name|event
operator|.
name|idWithinJob
argument_list|()
argument_list|,
name|event
operator|.
name|isMapTask
argument_list|()
argument_list|,
name|Status
operator|.
name|valueOf
argument_list|(
name|event
operator|.
name|getStatus
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|event
operator|.
name|getTaskTrackerHttp
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns task id.     * @return task id    * @deprecated use {@link #getTaskAttemptId()} instead.    */
annotation|@
name|Deprecated
DECL|method|getTaskId ()
specifier|public
name|String
name|getTaskId
parameter_list|()
block|{
return|return
name|getTaskAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns task id.     * @return task id    */
DECL|method|getTaskAttemptId ()
specifier|public
name|TaskAttemptID
name|getTaskAttemptId
parameter_list|()
block|{
return|return
name|TaskAttemptID
operator|.
name|downgrade
argument_list|(
name|super
operator|.
name|getTaskAttemptId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns enum Status.SUCESS or Status.FAILURE.    * @return task tracker status    */
DECL|method|getTaskStatus ()
specifier|public
name|Status
name|getTaskStatus
parameter_list|()
block|{
return|return
name|Status
operator|.
name|valueOf
argument_list|(
name|super
operator|.
name|getStatus
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Sets task id.     * @param taskId    * @deprecated use {@link #setTaskAttemptId(TaskAttemptID)} instead.    */
annotation|@
name|Deprecated
DECL|method|setTaskId (String taskId)
specifier|public
name|void
name|setTaskId
parameter_list|(
name|String
name|taskId
parameter_list|)
block|{
name|this
operator|.
name|setTaskAttemptId
argument_list|(
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|taskId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets task id.    * @param taskId    * @deprecated use {@link #setTaskAttemptId(TaskAttemptID)} instead.    */
annotation|@
name|Deprecated
DECL|method|setTaskID (TaskAttemptID taskId)
specifier|public
name|void
name|setTaskID
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|)
block|{
name|this
operator|.
name|setTaskAttemptId
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets task id.     * @param taskId    */
DECL|method|setTaskAttemptId (TaskAttemptID taskId)
specifier|protected
name|void
name|setTaskAttemptId
parameter_list|(
name|TaskAttemptID
name|taskId
parameter_list|)
block|{
name|super
operator|.
name|setTaskAttemptId
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set task status.     * @param status    */
annotation|@
name|Private
DECL|method|setTaskStatus (Status status)
specifier|public
name|void
name|setTaskStatus
parameter_list|(
name|Status
name|status
parameter_list|)
block|{
name|super
operator|.
name|setTaskStatus
argument_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskCompletionEvent
operator|.
name|Status
operator|.
name|valueOf
argument_list|(
name|status
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the task completion time    * @param taskCompletionTime time (in millisec) the task took to complete    */
annotation|@
name|Private
DECL|method|setTaskRunTime (int taskCompletionTime)
specifier|public
name|void
name|setTaskRunTime
parameter_list|(
name|int
name|taskCompletionTime
parameter_list|)
block|{
name|super
operator|.
name|setTaskRunTime
argument_list|(
name|taskCompletionTime
argument_list|)
expr_stmt|;
block|}
comment|/**    * set event Id. should be assigned incrementally starting from 0.     * @param eventId    */
annotation|@
name|Private
DECL|method|setEventId (int eventId)
specifier|public
name|void
name|setEventId
parameter_list|(
name|int
name|eventId
parameter_list|)
block|{
name|super
operator|.
name|setEventId
argument_list|(
name|eventId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set task tracker http location.     * @param taskTrackerHttp    */
annotation|@
name|Private
DECL|method|setTaskTrackerHttp (String taskTrackerHttp)
specifier|public
name|void
name|setTaskTrackerHttp
parameter_list|(
name|String
name|taskTrackerHttp
parameter_list|)
block|{
name|super
operator|.
name|setTaskTrackerHttp
argument_list|(
name|taskTrackerHttp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

