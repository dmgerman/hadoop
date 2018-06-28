begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity
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
name|resourcemanager
operator|.
name|monitor
operator|.
name|capacity
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
name|Resource
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|resourcemanager
operator|.
name|monitor
operator|.
name|capacity
operator|.
name|ProportionalCapacityPreemptionPolicy
operator|.
name|IntraQueuePreemptionOrderPolicy
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|CapacityScheduler
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
name|resource
operator|.
name|ResourceCalculator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * This interface provides context for the calculation of ideal allocation  * and preemption for the {@code CapacityScheduler}.  */
end_comment

begin_interface
DECL|interface|CapacitySchedulerPreemptionContext
specifier|public
interface|interface
name|CapacitySchedulerPreemptionContext
block|{
DECL|method|getScheduler ()
name|CapacityScheduler
name|getScheduler
parameter_list|()
function_decl|;
DECL|method|getQueueByPartition (String queueName, String partition)
name|TempQueuePerPartition
name|getQueueByPartition
parameter_list|(
name|String
name|queueName
parameter_list|,
name|String
name|partition
parameter_list|)
function_decl|;
DECL|method|getQueuePartitions (String queueName)
name|Collection
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|getQueuePartitions
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
DECL|method|getResourceCalculator ()
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
function_decl|;
DECL|method|getRMContext ()
name|RMContext
name|getRMContext
parameter_list|()
function_decl|;
DECL|method|isObserveOnly ()
name|boolean
name|isObserveOnly
parameter_list|()
function_decl|;
DECL|method|getKillableContainers ()
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|getKillableContainers
parameter_list|()
function_decl|;
DECL|method|getMaxIgnoreOverCapacity ()
name|double
name|getMaxIgnoreOverCapacity
parameter_list|()
function_decl|;
DECL|method|getNaturalTerminationFactor ()
name|double
name|getNaturalTerminationFactor
parameter_list|()
function_decl|;
DECL|method|getLeafQueueNames ()
name|Set
argument_list|<
name|String
argument_list|>
name|getLeafQueueNames
parameter_list|()
function_decl|;
DECL|method|getAllPartitions ()
name|Set
argument_list|<
name|String
argument_list|>
name|getAllPartitions
parameter_list|()
function_decl|;
DECL|method|getClusterMaxApplicationPriority ()
name|int
name|getClusterMaxApplicationPriority
parameter_list|()
function_decl|;
DECL|method|getPartitionResource (String partition)
name|Resource
name|getPartitionResource
parameter_list|(
name|String
name|partition
parameter_list|)
function_decl|;
DECL|method|getUnderServedQueuesPerPartition (String partition)
name|LinkedHashSet
argument_list|<
name|String
argument_list|>
name|getUnderServedQueuesPerPartition
parameter_list|(
name|String
name|partition
parameter_list|)
function_decl|;
DECL|method|addPartitionToUnderServedQueues (String queueName, String partition)
name|void
name|addPartitionToUnderServedQueues
parameter_list|(
name|String
name|queueName
parameter_list|,
name|String
name|partition
parameter_list|)
function_decl|;
DECL|method|getMinimumThresholdForIntraQueuePreemption ()
name|float
name|getMinimumThresholdForIntraQueuePreemption
parameter_list|()
function_decl|;
DECL|method|getMaxAllowableLimitForIntraQueuePreemption ()
name|float
name|getMaxAllowableLimitForIntraQueuePreemption
parameter_list|()
function_decl|;
DECL|method|getDefaultMaximumKillWaitTimeout ()
name|long
name|getDefaultMaximumKillWaitTimeout
parameter_list|()
function_decl|;
annotation|@
name|Unstable
DECL|method|getIntraQueuePreemptionOrderPolicy ()
name|IntraQueuePreemptionOrderPolicy
name|getIntraQueuePreemptionOrderPolicy
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

