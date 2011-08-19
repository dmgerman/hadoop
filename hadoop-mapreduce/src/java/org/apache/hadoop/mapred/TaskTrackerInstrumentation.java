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
name|File
import|;
end_import

begin_comment
comment|/**  * TaskTrackerInstrumentation defines a number of instrumentation points  * associated with TaskTrackers.  By default, the instrumentation points do  * nothing, but subclasses can do arbitrary instrumentation and monitoring at  * these points.  *   * TaskTrackerInstrumentation interfaces are associated uniquely with a  * TaskTracker.  We don't want an inner class here, because then subclasses  * wouldn't have direct access to the associated TaskTracker.  *    **/
end_comment

begin_class
DECL|class|TaskTrackerInstrumentation
class|class
name|TaskTrackerInstrumentation
block|{
DECL|field|tt
specifier|protected
specifier|final
name|TaskTracker
name|tt
decl_stmt|;
DECL|method|TaskTrackerInstrumentation (TaskTracker t)
specifier|public
name|TaskTrackerInstrumentation
parameter_list|(
name|TaskTracker
name|t
parameter_list|)
block|{
name|tt
operator|=
name|t
expr_stmt|;
block|}
comment|/**    * invoked when task attempt t succeeds    * @param t    */
DECL|method|completeTask (TaskAttemptID t)
specifier|public
name|void
name|completeTask
parameter_list|(
name|TaskAttemptID
name|t
parameter_list|)
block|{ }
DECL|method|timedoutTask (TaskAttemptID t)
specifier|public
name|void
name|timedoutTask
parameter_list|(
name|TaskAttemptID
name|t
parameter_list|)
block|{ }
DECL|method|taskFailedPing (TaskAttemptID t)
specifier|public
name|void
name|taskFailedPing
parameter_list|(
name|TaskAttemptID
name|t
parameter_list|)
block|{ }
comment|/**    * Called just before task attempt t starts.    * @param stdout the file containing standard out of the new task    * @param stderr the file containing standard error of the new task     */
DECL|method|reportTaskLaunch (TaskAttemptID t, File stdout, File stderr)
specifier|public
name|void
name|reportTaskLaunch
parameter_list|(
name|TaskAttemptID
name|t
parameter_list|,
name|File
name|stdout
parameter_list|,
name|File
name|stderr
parameter_list|)
block|{ }
comment|/**    * called when task t has just finished.    * @param t    */
DECL|method|reportTaskEnd (TaskAttemptID t)
specifier|public
name|void
name|reportTaskEnd
parameter_list|(
name|TaskAttemptID
name|t
parameter_list|)
block|{}
comment|/**    * Called when a task changes status.     * @param task the task whose status changed    * @param taskStatus the new status of the task    */
DECL|method|statusUpdate (Task task, TaskStatus taskStatus)
specifier|public
name|void
name|statusUpdate
parameter_list|(
name|Task
name|task
parameter_list|,
name|TaskStatus
name|taskStatus
parameter_list|)
block|{}
block|}
end_class

end_unit

