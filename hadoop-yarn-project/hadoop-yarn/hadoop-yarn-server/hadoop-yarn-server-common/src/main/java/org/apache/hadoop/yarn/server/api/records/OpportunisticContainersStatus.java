begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
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
operator|.
name|Unstable
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

begin_comment
comment|/**  *<p><code>OpportunisticContainersStatus</code> captures information  * pertaining to the state of execution of the opportunistic containers within a  * node.</p>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|OpportunisticContainersStatus
specifier|public
specifier|abstract
class|class
name|OpportunisticContainersStatus
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|OpportunisticContainersStatus
name|newInstance
parameter_list|()
block|{
return|return
name|Records
operator|.
name|newRecord
argument_list|(
name|OpportunisticContainersStatus
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Returns the number of currently running opportunistic containers on the    * node.    *    * @return number of running opportunistic containers.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getRunningOpportContainers ()
specifier|public
specifier|abstract
name|int
name|getRunningOpportContainers
parameter_list|()
function_decl|;
comment|/**    * Sets the number of running opportunistic containers.    *    * @param runningOpportContainers number of running opportunistic containers.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRunningOpportContainers (int runningOpportContainers)
specifier|public
specifier|abstract
name|void
name|setRunningOpportContainers
parameter_list|(
name|int
name|runningOpportContainers
parameter_list|)
function_decl|;
comment|/**    * Returns memory currently used on the node for running opportunistic    * containers.    *    * @return memory (in bytes) used for running opportunistic containers.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getOpportMemoryUsed ()
specifier|public
specifier|abstract
name|long
name|getOpportMemoryUsed
parameter_list|()
function_decl|;
comment|/**    * Sets the memory used on the node for running opportunistic containers.    *    * @param opportMemoryUsed memory (in bytes) used for running opportunistic    *                         containers.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setOpportMemoryUsed (long opportMemoryUsed)
specifier|public
specifier|abstract
name|void
name|setOpportMemoryUsed
parameter_list|(
name|long
name|opportMemoryUsed
parameter_list|)
function_decl|;
comment|/**    * Returns CPU cores currently used on the node for running opportunistic    * containers.    *    * @return CPU cores used for running opportunistic containers.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getOpportCoresUsed ()
specifier|public
specifier|abstract
name|int
name|getOpportCoresUsed
parameter_list|()
function_decl|;
comment|/**    * Sets the CPU cores used on the node for running opportunistic containers.    *    * @param opportCoresUsed memory (in bytes) used for running opportunistic    *                         containers.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setOpportCoresUsed (int opportCoresUsed)
specifier|public
specifier|abstract
name|void
name|setOpportCoresUsed
parameter_list|(
name|int
name|opportCoresUsed
parameter_list|)
function_decl|;
comment|/**    * Returns the number of queued opportunistic containers on the node.    *    * @return number of queued opportunistic containers.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getQueuedOpportContainers ()
specifier|public
specifier|abstract
name|int
name|getQueuedOpportContainers
parameter_list|()
function_decl|;
comment|/**    * Sets the number of queued opportunistic containers on the node.    *    * @param queuedOpportContainers number of queued opportunistic containers.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setQueuedOpportContainers (int queuedOpportContainers)
specifier|public
specifier|abstract
name|void
name|setQueuedOpportContainers
parameter_list|(
name|int
name|queuedOpportContainers
parameter_list|)
function_decl|;
comment|/**    * Returns the length of the containers queue on the node.    *    * @return length of the containers queue.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getWaitQueueLength ()
specifier|public
specifier|abstract
name|int
name|getWaitQueueLength
parameter_list|()
function_decl|;
comment|/**    * Sets the length of the containers queue on the node.    *    * @param waitQueueLength length of the containers queue.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setWaitQueueLength (int waitQueueLength)
specifier|public
specifier|abstract
name|void
name|setWaitQueueLength
parameter_list|(
name|int
name|waitQueueLength
parameter_list|)
function_decl|;
comment|/**    * Returns the estimated time that a container will have to wait if added to    * the queue of the node.    *    * @return estimated queuing time.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getEstimatedQueueWaitTime ()
specifier|public
specifier|abstract
name|int
name|getEstimatedQueueWaitTime
parameter_list|()
function_decl|;
comment|/**    * Sets the estimated time that a container will have to wait if added to the    * queue of the node.    *    * @param queueWaitTime estimated queuing time.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setEstimatedQueueWaitTime (int queueWaitTime)
specifier|public
specifier|abstract
name|void
name|setEstimatedQueueWaitTime
parameter_list|(
name|int
name|queueWaitTime
parameter_list|)
function_decl|;
comment|/**    * Gets the capacity of the opportunistic containers queue on the node.    *    * @return queue capacity.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getOpportQueueCapacity ()
specifier|public
specifier|abstract
name|int
name|getOpportQueueCapacity
parameter_list|()
function_decl|;
comment|/**    * Sets the capacity of the opportunistic containers queue on the node.    *    * @param queueCapacity queue capacity.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setOpportQueueCapacity (int queueCapacity)
specifier|public
specifier|abstract
name|void
name|setOpportQueueCapacity
parameter_list|(
name|int
name|queueCapacity
parameter_list|)
function_decl|;
block|}
end_class

end_unit

