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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|CapacitySchedulerConfiguration
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
name|Resources
import|;
end_import

begin_comment
comment|/**  * Calculate how much resources need to be preempted for each queue,  * will be used by {@link PreemptionCandidatesSelector}  */
end_comment

begin_class
DECL|class|PreemptableResourceCalculator
specifier|public
class|class
name|PreemptableResourceCalculator
extends|extends
name|AbstractPreemptableResourceCalculator
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|PreemptableResourceCalculator
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * PreemptableResourceCalculator constructor    *    * @param preemptionContext    * @param isReservedPreemptionCandidatesSelector this will be set by    * different implementation of candidate selectors, please refer to    * TempQueuePerPartition#offer for details.    * @param allowQueuesBalanceAfterAllQueuesSatisfied    */
DECL|method|PreemptableResourceCalculator ( CapacitySchedulerPreemptionContext preemptionContext, boolean isReservedPreemptionCandidatesSelector, boolean allowQueuesBalanceAfterAllQueuesSatisfied)
specifier|public
name|PreemptableResourceCalculator
parameter_list|(
name|CapacitySchedulerPreemptionContext
name|preemptionContext
parameter_list|,
name|boolean
name|isReservedPreemptionCandidatesSelector
parameter_list|,
name|boolean
name|allowQueuesBalanceAfterAllQueuesSatisfied
parameter_list|)
block|{
name|super
argument_list|(
name|preemptionContext
argument_list|,
name|isReservedPreemptionCandidatesSelector
argument_list|,
name|allowQueuesBalanceAfterAllQueuesSatisfied
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method computes (for a single level in the tree, passed as a {@code    * List<TempQueue>}) the ideal assignment of resources. This is done    * recursively to allocate capacity fairly across all queues with pending    * demands. It terminates when no resources are left to assign, or when all    * demand is satisfied.    *    * @param rc resource calculator    * @param queues a list of cloned queues to be assigned capacity to (this is    * an out param)    * @param totalPreemptionAllowed total amount of preemption we allow    * @param tot_guarant the amount of capacity assigned to this pool of queues    */
DECL|method|computeIdealResourceDistribution (ResourceCalculator rc, List<TempQueuePerPartition> queues, Resource totalPreemptionAllowed, Resource tot_guarant)
specifier|protected
name|void
name|computeIdealResourceDistribution
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|List
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|queues
parameter_list|,
name|Resource
name|totalPreemptionAllowed
parameter_list|,
name|Resource
name|tot_guarant
parameter_list|)
block|{
comment|// qAlloc tracks currently active queues (will decrease progressively as
comment|// demand is met)
name|List
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|qAlloc
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|queues
argument_list|)
decl_stmt|;
comment|// unassigned tracks how much resources are still to assign, initialized
comment|// with the total capacity for this set of queues
name|Resource
name|unassigned
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|tot_guarant
argument_list|)
decl_stmt|;
comment|// group queues based on whether they have non-zero guaranteed capacity
name|Set
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|nonZeroGuarQueues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|zeroGuarQueues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TempQueuePerPartition
name|q
range|:
name|qAlloc
control|)
block|{
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|tot_guarant
argument_list|,
name|q
operator|.
name|getGuaranteed
argument_list|()
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
name|nonZeroGuarQueues
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|zeroGuarQueues
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
comment|// first compute the allocation as a fixpoint based on guaranteed capacity
name|computeFixpointAllocation
argument_list|(
name|tot_guarant
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|nonZeroGuarQueues
argument_list|)
argument_list|,
name|unassigned
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// if any capacity is left unassigned, distributed among zero-guarantee
comment|// queues uniformly (i.e., not based on guaranteed capacity, as this is zero)
if|if
condition|(
operator|!
name|zeroGuarQueues
operator|.
name|isEmpty
argument_list|()
operator|&&
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|tot_guarant
argument_list|,
name|unassigned
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
name|computeFixpointAllocation
argument_list|(
name|tot_guarant
argument_list|,
name|zeroGuarQueues
argument_list|,
name|unassigned
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// based on ideal assignment computed above and current assignment we derive
comment|// how much preemption is required overall
name|Resource
name|totPreemptionNeeded
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|TempQueuePerPartition
name|t
range|:
name|queues
control|)
block|{
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|tot_guarant
argument_list|,
name|t
operator|.
name|getUsed
argument_list|()
argument_list|,
name|t
operator|.
name|idealAssigned
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|totPreemptionNeeded
argument_list|,
name|Resources
operator|.
name|subtract
argument_list|(
name|t
operator|.
name|getUsed
argument_list|()
argument_list|,
name|t
operator|.
name|idealAssigned
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * if we need to preempt more than is allowed, compute a factor (0<f<1)      * that is used to scale down how much we ask back from each queue      */
name|float
name|scalingFactor
init|=
literal|1.0F
decl_stmt|;
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|tot_guarant
argument_list|,
name|totPreemptionNeeded
argument_list|,
name|totalPreemptionAllowed
argument_list|)
condition|)
block|{
name|scalingFactor
operator|=
name|Resources
operator|.
name|divide
argument_list|(
name|rc
argument_list|,
name|tot_guarant
argument_list|,
name|totalPreemptionAllowed
argument_list|,
name|totPreemptionNeeded
argument_list|)
expr_stmt|;
block|}
comment|// assign to each queue the amount of actual preemption based on local
comment|// information of ideal preemption and scaling factor
for|for
control|(
name|TempQueuePerPartition
name|t
range|:
name|queues
control|)
block|{
name|t
operator|.
name|assignPreemption
argument_list|(
name|scalingFactor
argument_list|,
name|rc
argument_list|,
name|tot_guarant
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This method recursively computes the ideal assignment of resources to each    * level of the hierarchy. This ensures that leafs that are over-capacity but    * with parents within capacity will not be preemptionCandidates. Preemptions    * are allowed within each subtree according to local over/under capacity.    *    * @param root the root of the cloned queue hierachy    * @param totalPreemptionAllowed maximum amount of preemption allowed    */
DECL|method|recursivelyComputeIdealAssignment ( TempQueuePerPartition root, Resource totalPreemptionAllowed)
specifier|protected
name|void
name|recursivelyComputeIdealAssignment
parameter_list|(
name|TempQueuePerPartition
name|root
parameter_list|,
name|Resource
name|totalPreemptionAllowed
parameter_list|)
block|{
if|if
condition|(
name|root
operator|.
name|getChildren
argument_list|()
operator|!=
literal|null
operator|&&
name|root
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// compute ideal distribution at this level
name|computeIdealResourceDistribution
argument_list|(
name|rc
argument_list|,
name|root
operator|.
name|getChildren
argument_list|()
argument_list|,
name|totalPreemptionAllowed
argument_list|,
name|root
operator|.
name|idealAssigned
argument_list|)
expr_stmt|;
comment|// compute recursively for lower levels and build list of leafs
for|for
control|(
name|TempQueuePerPartition
name|t
range|:
name|root
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|recursivelyComputeIdealAssignment
argument_list|(
name|t
argument_list|,
name|totalPreemptionAllowed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|calculateResToObtainByPartitionForLeafQueues ( Set<String> leafQueueNames, Resource clusterResource)
specifier|private
name|void
name|calculateResToObtainByPartitionForLeafQueues
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|leafQueueNames
parameter_list|,
name|Resource
name|clusterResource
parameter_list|)
block|{
comment|// Loop all leaf queues
for|for
control|(
name|String
name|queueName
range|:
name|leafQueueNames
control|)
block|{
comment|// check if preemption disabled for the queue
if|if
condition|(
name|context
operator|.
name|getQueueByPartition
argument_list|(
name|queueName
argument_list|,
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
operator|.
name|preemptionDisabled
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
literal|"skipping from queue="
operator|+
name|queueName
operator|+
literal|" because it's a non-preemptable queue"
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
comment|// compute resToObtainByPartition considered inter-queue preemption
for|for
control|(
name|TempQueuePerPartition
name|qT
range|:
name|context
operator|.
name|getQueuePartitions
argument_list|(
name|queueName
argument_list|)
control|)
block|{
comment|// we act only if we are violating balance by more than
comment|// maxIgnoredOverCapacity
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|qT
operator|.
name|getUsed
argument_list|()
argument_list|,
name|Resources
operator|.
name|multiply
argument_list|(
name|qT
operator|.
name|getGuaranteed
argument_list|()
argument_list|,
literal|1.0
operator|+
name|context
operator|.
name|getMaxIgnoreOverCapacity
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
comment|/*            * We introduce a dampening factor naturalTerminationFactor that            * accounts for natural termination of containers.            *            * This is added to control pace of preemption, let's say:            * If preemption policy calculated a queue *should be* preempted 20 GB            * And the nature_termination_factor set to 0.1. As a result, preemption            * policy will select 20 GB * 0.1 = 2GB containers to be preempted.            *            * However, it doesn't work for YARN-4390:            * For example, if a queue needs to be preempted 20GB for *one single*            * large container, preempt 10% of such resource isn't useful.            * So to make it simple, only apply nature_termination_factor when            * selector is not reservedPreemptionCandidatesSelector.            */
name|Resource
name|resToObtain
init|=
name|qT
operator|.
name|toBePreempted
decl_stmt|;
if|if
condition|(
operator|!
name|isReservedPreemptionCandidatesSelector
condition|)
block|{
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|resToObtain
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|resToObtain
operator|=
name|Resources
operator|.
name|multiplyAndNormalizeUp
argument_list|(
name|rc
argument_list|,
name|qT
operator|.
name|toBePreempted
argument_list|,
name|context
operator|.
name|getNaturalTerminationFactor
argument_list|()
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Only add resToObtain when it>= 0
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|resToObtain
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
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
literal|"Queue="
operator|+
name|queueName
operator|+
literal|" partition="
operator|+
name|qT
operator|.
name|partition
operator|+
literal|" resource-to-obtain="
operator|+
name|resToObtain
argument_list|)
expr_stmt|;
block|}
block|}
name|qT
operator|.
name|setActuallyToBePreempted
argument_list|(
name|Resources
operator|.
name|clone
argument_list|(
name|resToObtain
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|qT
operator|.
name|setActuallyToBePreempted
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|qT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|updatePreemptableExtras (TempQueuePerPartition cur)
specifier|private
name|void
name|updatePreemptableExtras
parameter_list|(
name|TempQueuePerPartition
name|cur
parameter_list|)
block|{
if|if
condition|(
name|cur
operator|.
name|children
operator|==
literal|null
operator|||
name|cur
operator|.
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|cur
operator|.
name|updatePreemptableExtras
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|TempQueuePerPartition
name|child
range|:
name|cur
operator|.
name|children
control|)
block|{
name|updatePreemptableExtras
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
name|cur
operator|.
name|updatePreemptableExtras
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|computeIdealAllocation (Resource clusterResource, Resource totalPreemptionAllowed)
specifier|public
name|void
name|computeIdealAllocation
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|totalPreemptionAllowed
parameter_list|)
block|{
for|for
control|(
name|String
name|partition
range|:
name|context
operator|.
name|getAllPartitions
argument_list|()
control|)
block|{
name|TempQueuePerPartition
name|tRoot
init|=
name|context
operator|.
name|getQueueByPartition
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
name|partition
argument_list|)
decl_stmt|;
name|updatePreemptableExtras
argument_list|(
name|tRoot
argument_list|)
expr_stmt|;
comment|// compute the ideal distribution of resources among queues
comment|// updates cloned queues state accordingly
name|tRoot
operator|.
name|initializeRootIdealWithGuarangeed
argument_list|()
expr_stmt|;
name|recursivelyComputeIdealAssignment
argument_list|(
name|tRoot
argument_list|,
name|totalPreemptionAllowed
argument_list|)
expr_stmt|;
block|}
comment|// based on ideal allocation select containers to be preempted from each
comment|// calculate resource-to-obtain by partition for each leaf queues
name|calculateResToObtainByPartitionForLeafQueues
argument_list|(
name|context
operator|.
name|getLeafQueueNames
argument_list|()
argument_list|,
name|clusterResource
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

