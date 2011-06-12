begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.server.jobtracker
package|package
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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|mapred
operator|.
name|JobInProgress
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
name|JobTracker
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
name|TaskTrackerStatus
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
name|TaskType
import|;
end_import

begin_comment
comment|/**  * The representation of a single<code>TaskTracker</code> as seen by   * the {@link JobTracker}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
literal|"MapReduce"
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TaskTracker
specifier|public
class|class
name|TaskTracker
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TaskTracker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|trackerName
specifier|final
specifier|private
name|String
name|trackerName
decl_stmt|;
DECL|field|status
specifier|private
name|TaskTrackerStatus
name|status
decl_stmt|;
DECL|field|jobForFallowMapSlot
specifier|private
name|JobInProgress
name|jobForFallowMapSlot
decl_stmt|;
DECL|field|jobForFallowReduceSlot
specifier|private
name|JobInProgress
name|jobForFallowReduceSlot
decl_stmt|;
comment|/**    * Create a new {@link TaskTracker}.    * @param trackerName Unique identifier for the<code>TaskTracker</code>    */
DECL|method|TaskTracker (String trackerName)
specifier|public
name|TaskTracker
parameter_list|(
name|String
name|trackerName
parameter_list|)
block|{
name|this
operator|.
name|trackerName
operator|=
name|trackerName
expr_stmt|;
block|}
comment|/**    * Get the unique identifier for the {@link TaskTracker}    * @return the unique identifier for the<code>TaskTracker</code>    */
DECL|method|getTrackerName ()
specifier|public
name|String
name|getTrackerName
parameter_list|()
block|{
return|return
name|trackerName
return|;
block|}
comment|/**    * Get the current {@link TaskTrackerStatus} of the<code>TaskTracker</code>.    * @return the current<code>TaskTrackerStatus</code> of the     *<code>TaskTracker</code>    */
DECL|method|getStatus ()
specifier|public
name|TaskTrackerStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
comment|/**    * Set the current {@link TaskTrackerStatus} of the<code>TaskTracker</code>.    * @param status the current<code>TaskTrackerStatus</code> of the     *<code>TaskTracker</code>    */
DECL|method|setStatus (TaskTrackerStatus status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|TaskTrackerStatus
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
comment|/**    * Get the number of currently available slots on this tasktracker for the     * given type of the task.    * @param taskType the {@link TaskType} to check for number of available slots     * @return the number of currently available slots for the given     *<code>taskType</code>    */
DECL|method|getAvailableSlots (TaskType taskType)
specifier|public
name|int
name|getAvailableSlots
parameter_list|(
name|TaskType
name|taskType
parameter_list|)
block|{
name|int
name|availableSlots
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|taskType
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|trackerName
operator|+
literal|" getAvailSlots:"
operator|+
literal|" max(m)="
operator|+
name|status
operator|.
name|getMaxMapSlots
argument_list|()
operator|+
literal|" occupied(m)="
operator|+
name|status
operator|.
name|countOccupiedMapSlots
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|availableSlots
operator|=
name|status
operator|.
name|getAvailableMapSlots
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|trackerName
operator|+
literal|" getAvailSlots:"
operator|+
literal|" max(r)="
operator|+
name|status
operator|.
name|getMaxReduceSlots
argument_list|()
operator|+
literal|" occupied(r)="
operator|+
name|status
operator|.
name|countOccupiedReduceSlots
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|availableSlots
operator|=
name|status
operator|.
name|getAvailableReduceSlots
argument_list|()
expr_stmt|;
block|}
return|return
name|availableSlots
return|;
block|}
comment|/**    * Get the {@link JobInProgress} for which the fallow slot(s) are held.    * @param taskType {@link TaskType} of the task    * @return the task for which the fallow slot(s) are held,     *<code>null</code> if there are no fallow slots    */
DECL|method|getJobForFallowSlot (TaskType taskType)
specifier|public
name|JobInProgress
name|getJobForFallowSlot
parameter_list|(
name|TaskType
name|taskType
parameter_list|)
block|{
return|return
operator|(
name|taskType
operator|==
name|TaskType
operator|.
name|MAP
operator|)
condition|?
name|jobForFallowMapSlot
else|:
name|jobForFallowReduceSlot
return|;
block|}
comment|/**    * Reserve specified number of slots for a given<code>job</code>.    * @param taskType {@link TaskType} of the task    * @param job the job for which slots on this<code>TaskTracker</code>    *             are to be reserved    * @param numSlots number of slots to be reserved    */
DECL|method|reserveSlots (TaskType taskType, JobInProgress job, int numSlots)
specifier|public
name|void
name|reserveSlots
parameter_list|(
name|TaskType
name|taskType
parameter_list|,
name|JobInProgress
name|job
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|JobID
name|jobId
init|=
name|job
operator|.
name|getJobID
argument_list|()
decl_stmt|;
if|if
condition|(
name|taskType
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
if|if
condition|(
name|jobForFallowMapSlot
operator|!=
literal|null
operator|&&
operator|!
name|jobForFallowMapSlot
operator|.
name|getJobID
argument_list|()
operator|.
name|equals
argument_list|(
name|jobId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|trackerName
operator|+
literal|" already has "
operator|+
literal|"slots reserved for "
operator|+
name|jobForFallowMapSlot
operator|+
literal|"; being"
operator|+
literal|" asked to reserve "
operator|+
name|numSlots
operator|+
literal|" for "
operator|+
name|jobId
argument_list|)
throw|;
block|}
name|jobForFallowMapSlot
operator|=
name|job
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|taskType
operator|==
name|TaskType
operator|.
name|REDUCE
condition|)
block|{
if|if
condition|(
name|jobForFallowReduceSlot
operator|!=
literal|null
operator|&&
operator|!
name|jobForFallowReduceSlot
operator|.
name|getJobID
argument_list|()
operator|.
name|equals
argument_list|(
name|jobId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|trackerName
operator|+
literal|" already has "
operator|+
literal|"slots reserved for "
operator|+
name|jobForFallowReduceSlot
operator|+
literal|"; being"
operator|+
literal|" asked to reserve "
operator|+
name|numSlots
operator|+
literal|" for "
operator|+
name|jobId
argument_list|)
throw|;
block|}
name|jobForFallowReduceSlot
operator|=
name|job
expr_stmt|;
block|}
name|job
operator|.
name|reserveTaskTracker
argument_list|(
name|this
argument_list|,
name|taskType
argument_list|,
name|numSlots
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|trackerName
operator|+
literal|": Reserved "
operator|+
name|numSlots
operator|+
literal|" "
operator|+
name|taskType
operator|+
literal|" slots for "
operator|+
name|jobId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Free map slots on this<code>TaskTracker</code> which were reserved for     *<code>taskType</code>.    * @param taskType {@link TaskType} of the task    * @param job job whose slots are being un-reserved    */
DECL|method|unreserveSlots (TaskType taskType, JobInProgress job)
specifier|public
name|void
name|unreserveSlots
parameter_list|(
name|TaskType
name|taskType
parameter_list|,
name|JobInProgress
name|job
parameter_list|)
block|{
name|JobID
name|jobId
init|=
name|job
operator|.
name|getJobID
argument_list|()
decl_stmt|;
if|if
condition|(
name|taskType
operator|==
name|TaskType
operator|.
name|MAP
condition|)
block|{
if|if
condition|(
name|jobForFallowMapSlot
operator|==
literal|null
operator|||
operator|!
name|jobForFallowMapSlot
operator|.
name|getJobID
argument_list|()
operator|.
name|equals
argument_list|(
name|jobId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|trackerName
operator|+
literal|" already has "
operator|+
literal|"slots reserved for "
operator|+
name|jobForFallowMapSlot
operator|+
literal|"; being"
operator|+
literal|" asked to un-reserve for "
operator|+
name|jobId
argument_list|)
throw|;
block|}
name|jobForFallowMapSlot
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|jobForFallowReduceSlot
operator|==
literal|null
operator|||
operator|!
name|jobForFallowReduceSlot
operator|.
name|getJobID
argument_list|()
operator|.
name|equals
argument_list|(
name|jobId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|trackerName
operator|+
literal|" already has "
operator|+
literal|"slots reserved for "
operator|+
name|jobForFallowReduceSlot
operator|+
literal|"; being"
operator|+
literal|" asked to un-reserve for "
operator|+
name|jobId
argument_list|)
throw|;
block|}
name|jobForFallowReduceSlot
operator|=
literal|null
expr_stmt|;
block|}
name|job
operator|.
name|unreserveTaskTracker
argument_list|(
name|this
argument_list|,
name|taskType
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|trackerName
operator|+
literal|": Unreserved "
operator|+
name|taskType
operator|+
literal|" slots for "
operator|+
name|jobId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Cleanup when the {@link TaskTracker} is declared as 'lost/blacklisted'    * by the JobTracker.    *     * The method assumes that the lock on the {@link JobTracker} is obtained    * by the caller.    */
DECL|method|cancelAllReservations ()
specifier|public
name|void
name|cancelAllReservations
parameter_list|()
block|{
comment|// Inform jobs which have reserved slots on this tasktracker
if|if
condition|(
name|jobForFallowMapSlot
operator|!=
literal|null
condition|)
block|{
name|unreserveSlots
argument_list|(
name|TaskType
operator|.
name|MAP
argument_list|,
name|jobForFallowMapSlot
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jobForFallowReduceSlot
operator|!=
literal|null
condition|)
block|{
name|unreserveSlots
argument_list|(
name|TaskType
operator|.
name|REDUCE
argument_list|,
name|jobForFallowReduceSlot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

