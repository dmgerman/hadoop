begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|QueueStatistics
specifier|public
specifier|abstract
class|class
name|QueueStatistics
block|{
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|newInstance (long submitted, long running, long pending, long completed, long killed, long failed, long activeUsers, long availableMemoryMB, long allocatedMemoryMB, long pendingMemoryMB, long reservedMemoryMB, long availableVCores, long allocatedVCores, long pendingVCores, long reservedVCores)
specifier|public
specifier|static
name|QueueStatistics
name|newInstance
parameter_list|(
name|long
name|submitted
parameter_list|,
name|long
name|running
parameter_list|,
name|long
name|pending
parameter_list|,
name|long
name|completed
parameter_list|,
name|long
name|killed
parameter_list|,
name|long
name|failed
parameter_list|,
name|long
name|activeUsers
parameter_list|,
name|long
name|availableMemoryMB
parameter_list|,
name|long
name|allocatedMemoryMB
parameter_list|,
name|long
name|pendingMemoryMB
parameter_list|,
name|long
name|reservedMemoryMB
parameter_list|,
name|long
name|availableVCores
parameter_list|,
name|long
name|allocatedVCores
parameter_list|,
name|long
name|pendingVCores
parameter_list|,
name|long
name|reservedVCores
parameter_list|)
block|{
name|QueueStatistics
name|statistics
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|QueueStatistics
operator|.
name|class
argument_list|)
decl_stmt|;
name|statistics
operator|.
name|setNumAppsSubmitted
argument_list|(
name|submitted
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setNumAppsRunning
argument_list|(
name|running
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setNumAppsPending
argument_list|(
name|pending
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setNumAppsCompleted
argument_list|(
name|completed
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setNumAppsKilled
argument_list|(
name|killed
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setNumAppsFailed
argument_list|(
name|failed
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setNumActiveUsers
argument_list|(
name|activeUsers
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setAvailableMemoryMB
argument_list|(
name|availableMemoryMB
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setAllocatedMemoryMB
argument_list|(
name|allocatedMemoryMB
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setPendingMemoryMB
argument_list|(
name|pendingMemoryMB
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setReservedMemoryMB
argument_list|(
name|reservedMemoryMB
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setAvailableVCores
argument_list|(
name|availableVCores
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setAllocatedVCores
argument_list|(
name|allocatedVCores
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setPendingVCores
argument_list|(
name|pendingVCores
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|setReservedVCores
argument_list|(
name|reservedVCores
argument_list|)
expr_stmt|;
return|return
name|statistics
return|;
block|}
comment|/**    * Get the number of apps submitted    *     * @return the number of apps submitted    */
DECL|method|getNumAppsSubmitted ()
specifier|public
specifier|abstract
name|long
name|getNumAppsSubmitted
parameter_list|()
function_decl|;
comment|/**    * Set the number of apps submitted    *     * @param numAppsSubmitted    *          the number of apps submitted    */
DECL|method|setNumAppsSubmitted (long numAppsSubmitted)
specifier|public
specifier|abstract
name|void
name|setNumAppsSubmitted
parameter_list|(
name|long
name|numAppsSubmitted
parameter_list|)
function_decl|;
comment|/**    * Get the number of running apps    *     * @return the number of running apps    */
DECL|method|getNumAppsRunning ()
specifier|public
specifier|abstract
name|long
name|getNumAppsRunning
parameter_list|()
function_decl|;
comment|/**    * Set the number of running apps    *     * @param numAppsRunning    *          the number of running apps    */
DECL|method|setNumAppsRunning (long numAppsRunning)
specifier|public
specifier|abstract
name|void
name|setNumAppsRunning
parameter_list|(
name|long
name|numAppsRunning
parameter_list|)
function_decl|;
comment|/**    * Get the number of pending apps    *     * @return the number of pending apps    */
DECL|method|getNumAppsPending ()
specifier|public
specifier|abstract
name|long
name|getNumAppsPending
parameter_list|()
function_decl|;
comment|/**    * Set the number of pending apps    *     * @param numAppsPending    *          the number of pending apps    */
DECL|method|setNumAppsPending (long numAppsPending)
specifier|public
specifier|abstract
name|void
name|setNumAppsPending
parameter_list|(
name|long
name|numAppsPending
parameter_list|)
function_decl|;
comment|/**    * Get the number of completed apps    *     * @return the number of completed apps    */
DECL|method|getNumAppsCompleted ()
specifier|public
specifier|abstract
name|long
name|getNumAppsCompleted
parameter_list|()
function_decl|;
comment|/**    * Set the number of completed apps    *     * @param numAppsCompleted    *          the number of completed apps    */
DECL|method|setNumAppsCompleted (long numAppsCompleted)
specifier|public
specifier|abstract
name|void
name|setNumAppsCompleted
parameter_list|(
name|long
name|numAppsCompleted
parameter_list|)
function_decl|;
comment|/**    * Get the number of killed apps    *     * @return the number of killed apps    */
DECL|method|getNumAppsKilled ()
specifier|public
specifier|abstract
name|long
name|getNumAppsKilled
parameter_list|()
function_decl|;
comment|/**    * Set the number of killed apps    *     * @param numAppsKilled    *          the number of killed apps    */
DECL|method|setNumAppsKilled (long numAppsKilled)
specifier|public
specifier|abstract
name|void
name|setNumAppsKilled
parameter_list|(
name|long
name|numAppsKilled
parameter_list|)
function_decl|;
comment|/**    * Get the number of failed apps    *     * @return the number of failed apps    */
DECL|method|getNumAppsFailed ()
specifier|public
specifier|abstract
name|long
name|getNumAppsFailed
parameter_list|()
function_decl|;
comment|/**    * Set the number of failed apps    *     * @param numAppsFailed    *          the number of failed apps    */
DECL|method|setNumAppsFailed (long numAppsFailed)
specifier|public
specifier|abstract
name|void
name|setNumAppsFailed
parameter_list|(
name|long
name|numAppsFailed
parameter_list|)
function_decl|;
comment|/**    * Get the number of active users    *     * @return the number of active users    */
DECL|method|getNumActiveUsers ()
specifier|public
specifier|abstract
name|long
name|getNumActiveUsers
parameter_list|()
function_decl|;
comment|/**    * Set the number of active users    *     * @param numActiveUsers    *          the number of active users    */
DECL|method|setNumActiveUsers (long numActiveUsers)
specifier|public
specifier|abstract
name|void
name|setNumActiveUsers
parameter_list|(
name|long
name|numActiveUsers
parameter_list|)
function_decl|;
comment|/**    * Get the available memory in MB    *     * @return the available memory    */
DECL|method|getAvailableMemoryMB ()
specifier|public
specifier|abstract
name|long
name|getAvailableMemoryMB
parameter_list|()
function_decl|;
comment|/**    * Set the available memory in MB    *     * @param availableMemoryMB    *          the available memory    */
DECL|method|setAvailableMemoryMB (long availableMemoryMB)
specifier|public
specifier|abstract
name|void
name|setAvailableMemoryMB
parameter_list|(
name|long
name|availableMemoryMB
parameter_list|)
function_decl|;
comment|/**    * Get the allocated memory in MB    *     * @return the allocated memory    */
DECL|method|getAllocatedMemoryMB ()
specifier|public
specifier|abstract
name|long
name|getAllocatedMemoryMB
parameter_list|()
function_decl|;
comment|/**    * Set the allocated memory in MB    *     * @param allocatedMemoryMB    *          the allocate memory    */
DECL|method|setAllocatedMemoryMB (long allocatedMemoryMB)
specifier|public
specifier|abstract
name|void
name|setAllocatedMemoryMB
parameter_list|(
name|long
name|allocatedMemoryMB
parameter_list|)
function_decl|;
comment|/**    * Get the pending memory in MB    *     * @return the pending memory    */
DECL|method|getPendingMemoryMB ()
specifier|public
specifier|abstract
name|long
name|getPendingMemoryMB
parameter_list|()
function_decl|;
comment|/**    * Set the pending memory in MB    *     * @param pendingMemoryMB    *          the pending memory    */
DECL|method|setPendingMemoryMB (long pendingMemoryMB)
specifier|public
specifier|abstract
name|void
name|setPendingMemoryMB
parameter_list|(
name|long
name|pendingMemoryMB
parameter_list|)
function_decl|;
comment|/**    * Get the reserved memory in MB    *     * @return the reserved memory    */
DECL|method|getReservedMemoryMB ()
specifier|public
specifier|abstract
name|long
name|getReservedMemoryMB
parameter_list|()
function_decl|;
comment|/**    * Set the reserved memory in MB    *     * @param reservedMemoryMB    *          the reserved memory    */
DECL|method|setReservedMemoryMB (long reservedMemoryMB)
specifier|public
specifier|abstract
name|void
name|setReservedMemoryMB
parameter_list|(
name|long
name|reservedMemoryMB
parameter_list|)
function_decl|;
comment|/**    * Get the available vcores    *     * @return the available vcores    */
DECL|method|getAvailableVCores ()
specifier|public
specifier|abstract
name|long
name|getAvailableVCores
parameter_list|()
function_decl|;
comment|/**    * Set the available vcores    *     * @param availableVCores    *          the available vcores    */
DECL|method|setAvailableVCores (long availableVCores)
specifier|public
specifier|abstract
name|void
name|setAvailableVCores
parameter_list|(
name|long
name|availableVCores
parameter_list|)
function_decl|;
comment|/**    * Get the allocated vcores    *     * @return the allocated vcores    */
DECL|method|getAllocatedVCores ()
specifier|public
specifier|abstract
name|long
name|getAllocatedVCores
parameter_list|()
function_decl|;
comment|/**    * Set the allocated vcores    *     * @param allocatedVCores    *          the allocated vcores    */
DECL|method|setAllocatedVCores (long allocatedVCores)
specifier|public
specifier|abstract
name|void
name|setAllocatedVCores
parameter_list|(
name|long
name|allocatedVCores
parameter_list|)
function_decl|;
comment|/**    * Get the pending vcores    *     * @return the pending vcores    */
DECL|method|getPendingVCores ()
specifier|public
specifier|abstract
name|long
name|getPendingVCores
parameter_list|()
function_decl|;
comment|/**    * Set the pending vcores    *     * @param pendingVCores    *          the pending vcores    */
DECL|method|setPendingVCores (long pendingVCores)
specifier|public
specifier|abstract
name|void
name|setPendingVCores
parameter_list|(
name|long
name|pendingVCores
parameter_list|)
function_decl|;
comment|/**    * Get the reserved vcores    *     * @return the reserved vcores    */
DECL|method|getReservedVCores ()
specifier|public
specifier|abstract
name|long
name|getReservedVCores
parameter_list|()
function_decl|;
comment|/**    * Set the reserved vcores    *     * @param reservedVCores    *          the reserved vcores    */
DECL|method|setReservedVCores (long reservedVCores)
specifier|public
specifier|abstract
name|void
name|setReservedVCores
parameter_list|(
name|long
name|reservedVCores
parameter_list|)
function_decl|;
block|}
end_class

end_unit

