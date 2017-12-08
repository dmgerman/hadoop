begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|scheduler
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
name|Set
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
name|ResourceUsage
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
name|utils
operator|.
name|Lock
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_class
DECL|class|CSQueueUtils
specifier|public
class|class
name|CSQueueUtils
block|{
DECL|field|EPSILON
specifier|public
specifier|final
specifier|static
name|float
name|EPSILON
init|=
literal|0.0001f
decl_stmt|;
comment|/*    * Used only by tests    */
DECL|method|checkMaxCapacity (String queueName, float capacity, float maximumCapacity)
specifier|public
specifier|static
name|void
name|checkMaxCapacity
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|maximumCapacity
parameter_list|)
block|{
if|if
condition|(
name|maximumCapacity
argument_list|<
literal|0.0f
operator|||
name|maximumCapacity
argument_list|>
literal|1.0f
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal value  of maximumCapacity "
operator|+
name|maximumCapacity
operator|+
literal|" used in call to setMaxCapacity for queue "
operator|+
name|queueName
argument_list|)
throw|;
block|}
block|}
comment|/*    * Used only by tests    */
DECL|method|checkAbsoluteCapacity (String queueName, float absCapacity, float absMaxCapacity)
specifier|public
specifier|static
name|void
name|checkAbsoluteCapacity
parameter_list|(
name|String
name|queueName
parameter_list|,
name|float
name|absCapacity
parameter_list|,
name|float
name|absMaxCapacity
parameter_list|)
block|{
if|if
condition|(
name|absMaxCapacity
operator|<
operator|(
name|absCapacity
operator|-
name|EPSILON
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal call to setMaxCapacity. "
operator|+
literal|"Queue '"
operator|+
name|queueName
operator|+
literal|"' has "
operator|+
literal|"an absolute capacity ("
operator|+
name|absCapacity
operator|+
literal|") greater than "
operator|+
literal|"its absolute maximumCapacity ("
operator|+
name|absMaxCapacity
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Check sanity of capacities:    * - capacity<= maxCapacity    * - absCapacity<= absMaximumCapacity    */
DECL|method|capacitiesSanityCheck (String queueName, QueueCapacities queueCapacities)
specifier|private
specifier|static
name|void
name|capacitiesSanityCheck
parameter_list|(
name|String
name|queueName
parameter_list|,
name|QueueCapacities
name|queueCapacities
parameter_list|)
block|{
for|for
control|(
name|String
name|label
range|:
name|queueCapacities
operator|.
name|getExistingNodeLabels
argument_list|()
control|)
block|{
comment|// The only thing we should care about is absolute capacity<=
comment|// absolute max capacity otherwise the absolute max capacity is
comment|// no longer an absolute maximum.
name|float
name|absCapacity
init|=
name|queueCapacities
operator|.
name|getAbsoluteCapacity
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|float
name|absMaxCapacity
init|=
name|queueCapacities
operator|.
name|getAbsoluteMaximumCapacity
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|absCapacity
operator|>
name|absMaxCapacity
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal queue capacity setting, "
operator|+
literal|"(abs-capacity="
operator|+
name|absCapacity
operator|+
literal|")> (abs-maximum-capacity="
operator|+
name|absMaxCapacity
operator|+
literal|"). When label=["
operator|+
name|label
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|computeAbsoluteMaximumCapacity ( float maximumCapacity, CSQueue parent)
specifier|public
specifier|static
name|float
name|computeAbsoluteMaximumCapacity
parameter_list|(
name|float
name|maximumCapacity
parameter_list|,
name|CSQueue
name|parent
parameter_list|)
block|{
name|float
name|parentAbsMaxCapacity
init|=
operator|(
name|parent
operator|==
literal|null
operator|)
condition|?
literal|1.0f
else|:
name|parent
operator|.
name|getAbsoluteMaximumCapacity
argument_list|()
decl_stmt|;
return|return
operator|(
name|parentAbsMaxCapacity
operator|*
name|maximumCapacity
operator|)
return|;
block|}
comment|/**    * This method intends to be used by ReservationQueue, ReservationQueue will    * not appear in configuration file, so we shouldn't do load capacities    * settings in configuration for reservation queue.    */
DECL|method|updateAndCheckCapacitiesByLabel (String queuePath, QueueCapacities queueCapacities, QueueCapacities parentQueueCapacities)
specifier|public
specifier|static
name|void
name|updateAndCheckCapacitiesByLabel
parameter_list|(
name|String
name|queuePath
parameter_list|,
name|QueueCapacities
name|queueCapacities
parameter_list|,
name|QueueCapacities
name|parentQueueCapacities
parameter_list|)
block|{
name|updateAbsoluteCapacitiesByNodeLabels
argument_list|(
name|queueCapacities
argument_list|,
name|parentQueueCapacities
argument_list|)
expr_stmt|;
name|capacitiesSanityCheck
argument_list|(
name|queuePath
argument_list|,
name|queueCapacities
argument_list|)
expr_stmt|;
block|}
comment|/**    * Do following steps for capacities    * - Load capacities from configuration    * - Update absolute capacities for new capacities    * - Check if capacities/absolute-capacities legal    */
DECL|method|loadUpdateAndCheckCapacities (String queuePath, CapacitySchedulerConfiguration csConf, QueueCapacities queueCapacities, QueueCapacities parentQueueCapacities)
specifier|public
specifier|static
name|void
name|loadUpdateAndCheckCapacities
parameter_list|(
name|String
name|queuePath
parameter_list|,
name|CapacitySchedulerConfiguration
name|csConf
parameter_list|,
name|QueueCapacities
name|queueCapacities
parameter_list|,
name|QueueCapacities
name|parentQueueCapacities
parameter_list|)
block|{
name|loadCapacitiesByLabelsFromConf
argument_list|(
name|queuePath
argument_list|,
name|queueCapacities
argument_list|,
name|csConf
argument_list|)
expr_stmt|;
name|updateAbsoluteCapacitiesByNodeLabels
argument_list|(
name|queueCapacities
argument_list|,
name|parentQueueCapacities
argument_list|)
expr_stmt|;
name|capacitiesSanityCheck
argument_list|(
name|queuePath
argument_list|,
name|queueCapacities
argument_list|)
expr_stmt|;
block|}
DECL|method|loadCapacitiesByLabelsFromConf (String queuePath, QueueCapacities queueCapacities, CapacitySchedulerConfiguration csConf)
specifier|private
specifier|static
name|void
name|loadCapacitiesByLabelsFromConf
parameter_list|(
name|String
name|queuePath
parameter_list|,
name|QueueCapacities
name|queueCapacities
parameter_list|,
name|CapacitySchedulerConfiguration
name|csConf
parameter_list|)
block|{
name|queueCapacities
operator|.
name|clearConfigurableFields
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|configuredNodelabels
init|=
name|csConf
operator|.
name|getConfiguredNodeLabels
argument_list|(
name|queuePath
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|label
range|:
name|configuredNodelabels
control|)
block|{
if|if
condition|(
name|label
operator|.
name|equals
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|queueCapacities
operator|.
name|setCapacity
argument_list|(
name|label
argument_list|,
name|csConf
operator|.
name|getNonLabeledQueueCapacity
argument_list|(
name|queuePath
argument_list|)
operator|/
literal|100
argument_list|)
expr_stmt|;
name|queueCapacities
operator|.
name|setMaximumCapacity
argument_list|(
name|label
argument_list|,
name|csConf
operator|.
name|getNonLabeledQueueMaximumCapacity
argument_list|(
name|queuePath
argument_list|)
operator|/
literal|100
argument_list|)
expr_stmt|;
name|queueCapacities
operator|.
name|setMaxAMResourcePercentage
argument_list|(
name|label
argument_list|,
name|csConf
operator|.
name|getMaximumAMResourcePercentPerPartition
argument_list|(
name|queuePath
argument_list|,
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queueCapacities
operator|.
name|setCapacity
argument_list|(
name|label
argument_list|,
name|csConf
operator|.
name|getLabeledQueueCapacity
argument_list|(
name|queuePath
argument_list|,
name|label
argument_list|)
operator|/
literal|100
argument_list|)
expr_stmt|;
name|queueCapacities
operator|.
name|setMaximumCapacity
argument_list|(
name|label
argument_list|,
name|csConf
operator|.
name|getLabeledQueueMaximumCapacity
argument_list|(
name|queuePath
argument_list|,
name|label
argument_list|)
operator|/
literal|100
argument_list|)
expr_stmt|;
name|queueCapacities
operator|.
name|setMaxAMResourcePercentage
argument_list|(
name|label
argument_list|,
name|csConf
operator|.
name|getMaximumAMResourcePercentPerPartition
argument_list|(
name|queuePath
argument_list|,
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Set absolute capacities for {capacity, maximum-capacity}
DECL|method|updateAbsoluteCapacitiesByNodeLabels ( QueueCapacities queueCapacities, QueueCapacities parentQueueCapacities)
specifier|private
specifier|static
name|void
name|updateAbsoluteCapacitiesByNodeLabels
parameter_list|(
name|QueueCapacities
name|queueCapacities
parameter_list|,
name|QueueCapacities
name|parentQueueCapacities
parameter_list|)
block|{
for|for
control|(
name|String
name|label
range|:
name|queueCapacities
operator|.
name|getExistingNodeLabels
argument_list|()
control|)
block|{
name|float
name|capacity
init|=
name|queueCapacities
operator|.
name|getCapacity
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|capacity
operator|>
literal|0f
condition|)
block|{
name|queueCapacities
operator|.
name|setAbsoluteCapacity
argument_list|(
name|label
argument_list|,
name|capacity
operator|*
operator|(
name|parentQueueCapacities
operator|==
literal|null
condition|?
literal|1
else|:
name|parentQueueCapacities
operator|.
name|getAbsoluteCapacity
argument_list|(
name|label
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|float
name|maxCapacity
init|=
name|queueCapacities
operator|.
name|getMaximumCapacity
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxCapacity
operator|>
literal|0f
condition|)
block|{
name|queueCapacities
operator|.
name|setAbsoluteMaximumCapacity
argument_list|(
name|label
argument_list|,
name|maxCapacity
operator|*
operator|(
name|parentQueueCapacities
operator|==
literal|null
condition|?
literal|1
else|:
name|parentQueueCapacities
operator|.
name|getAbsoluteMaximumCapacity
argument_list|(
name|label
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Update partitioned resource usage, if nodePartition == null, will update    * used resource for all partitions of this queue.    */
DECL|method|updateUsedCapacity (final ResourceCalculator rc, final Resource totalPartitionResource, String nodePartition, AbstractCSQueue childQueue)
specifier|public
specifier|static
name|void
name|updateUsedCapacity
parameter_list|(
specifier|final
name|ResourceCalculator
name|rc
parameter_list|,
specifier|final
name|Resource
name|totalPartitionResource
parameter_list|,
name|String
name|nodePartition
parameter_list|,
name|AbstractCSQueue
name|childQueue
parameter_list|)
block|{
name|QueueCapacities
name|queueCapacities
init|=
name|childQueue
operator|.
name|getQueueCapacities
argument_list|()
decl_stmt|;
name|CSQueueMetrics
name|queueMetrics
init|=
name|childQueue
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|ResourceUsage
name|queueResourceUsage
init|=
name|childQueue
operator|.
name|getQueueResourceUsage
argument_list|()
decl_stmt|;
name|Resource
name|minimumAllocation
init|=
name|childQueue
operator|.
name|getMinimumAllocation
argument_list|()
decl_stmt|;
name|float
name|absoluteUsedCapacity
init|=
literal|0.0f
decl_stmt|;
name|float
name|usedCapacity
init|=
literal|0.0f
decl_stmt|;
name|float
name|reservedCapacity
init|=
literal|0.0f
decl_stmt|;
name|float
name|absoluteReservedCapacity
init|=
literal|0.0f
decl_stmt|;
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|totalPartitionResource
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
name|Resource
name|queueGuranteedResource
init|=
name|childQueue
operator|.
name|getEffectiveCapacity
argument_list|(
name|nodePartition
argument_list|)
decl_stmt|;
comment|//TODO : Modify below code to support Absolute Resource configurations
comment|// (YARN-5881) for AutoCreatedLeafQueue
if|if
condition|(
name|Float
operator|.
name|compare
argument_list|(
name|queueCapacities
operator|.
name|getAbsoluteCapacity
argument_list|(
name|nodePartition
argument_list|)
argument_list|,
literal|0f
argument_list|)
operator|==
literal|0
operator|&&
name|childQueue
operator|instanceof
name|AutoCreatedLeafQueue
condition|)
block|{
comment|//If absolute capacity is 0 for a leaf queue (could be a managed leaf
comment|// queue, then use the leaf queue's template capacity to compute
comment|// guaranteed resource for used capacity)
comment|// queueGuaranteed = totalPartitionedResource *
comment|// absolute_capacity(partition)
name|ManagedParentQueue
name|parentQueue
init|=
operator|(
name|ManagedParentQueue
operator|)
name|childQueue
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|QueueCapacities
name|leafQueueTemplateCapacities
init|=
name|parentQueue
operator|.
name|getLeafQueueTemplate
argument_list|()
operator|.
name|getQueueCapacities
argument_list|()
decl_stmt|;
name|queueGuranteedResource
operator|=
name|Resources
operator|.
name|multiply
argument_list|(
name|totalPartitionResource
argument_list|,
name|leafQueueTemplateCapacities
operator|.
name|getAbsoluteCapacity
argument_list|(
name|nodePartition
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// make queueGuranteed>= minimum_allocation to avoid divided by 0.
name|queueGuranteedResource
operator|=
name|Resources
operator|.
name|max
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|queueGuranteedResource
argument_list|,
name|minimumAllocation
argument_list|)
expr_stmt|;
name|Resource
name|usedResource
init|=
name|queueResourceUsage
operator|.
name|getUsed
argument_list|(
name|nodePartition
argument_list|)
decl_stmt|;
name|absoluteUsedCapacity
operator|=
name|Resources
operator|.
name|divide
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|usedResource
argument_list|,
name|totalPartitionResource
argument_list|)
expr_stmt|;
name|usedCapacity
operator|=
name|Resources
operator|.
name|divide
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|usedResource
argument_list|,
name|queueGuranteedResource
argument_list|)
expr_stmt|;
name|Resource
name|resResource
init|=
name|queueResourceUsage
operator|.
name|getReserved
argument_list|(
name|nodePartition
argument_list|)
decl_stmt|;
name|reservedCapacity
operator|=
name|Resources
operator|.
name|divide
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|resResource
argument_list|,
name|queueGuranteedResource
argument_list|)
expr_stmt|;
name|absoluteReservedCapacity
operator|=
name|Resources
operator|.
name|divide
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|resResource
argument_list|,
name|totalPartitionResource
argument_list|)
expr_stmt|;
block|}
name|queueCapacities
operator|.
name|setAbsoluteUsedCapacity
argument_list|(
name|nodePartition
argument_list|,
name|absoluteUsedCapacity
argument_list|)
expr_stmt|;
name|queueCapacities
operator|.
name|setUsedCapacity
argument_list|(
name|nodePartition
argument_list|,
name|usedCapacity
argument_list|)
expr_stmt|;
name|queueCapacities
operator|.
name|setReservedCapacity
argument_list|(
name|nodePartition
argument_list|,
name|reservedCapacity
argument_list|)
expr_stmt|;
name|queueCapacities
operator|.
name|setAbsoluteReservedCapacity
argument_list|(
name|nodePartition
argument_list|,
name|absoluteReservedCapacity
argument_list|)
expr_stmt|;
comment|// QueueMetrics does not support per-label capacities,
comment|// so we report values only for the default partition.
name|queueMetrics
operator|.
name|setUsedCapacity
argument_list|(
name|nodePartition
argument_list|,
name|queueCapacities
operator|.
name|getUsedCapacity
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
argument_list|)
expr_stmt|;
name|queueMetrics
operator|.
name|setAbsoluteUsedCapacity
argument_list|(
name|nodePartition
argument_list|,
name|queueCapacities
operator|.
name|getAbsoluteUsedCapacity
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getMaxAvailableResourceToQueue ( final ResourceCalculator rc, RMNodeLabelsManager nlm, CSQueue queue, Resource cluster)
specifier|private
specifier|static
name|Resource
name|getMaxAvailableResourceToQueue
parameter_list|(
specifier|final
name|ResourceCalculator
name|rc
parameter_list|,
name|RMNodeLabelsManager
name|nlm
parameter_list|,
name|CSQueue
name|queue
parameter_list|,
name|Resource
name|cluster
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
init|=
name|queue
operator|.
name|getNodeLabelsForQueue
argument_list|()
decl_stmt|;
name|Resource
name|totalAvailableResource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|partition
range|:
name|nodeLabels
control|)
block|{
comment|// Calculate guaranteed resource for a label in a queue by below logic.
comment|// (total label resource) * (absolute capacity of label in that queue)
name|Resource
name|queueGuranteedResource
init|=
name|queue
operator|.
name|getEffectiveCapacity
argument_list|(
name|partition
argument_list|)
decl_stmt|;
comment|// Available resource in queue for a specific label will be calculated as
comment|// {(guaranteed resource for a label in a queue) -
comment|// (resource usage of that label in the queue)}
comment|// Finally accumulate this available resource to get total.
name|Resource
name|available
init|=
operator|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|cluster
argument_list|,
name|queueGuranteedResource
argument_list|,
name|queue
operator|.
name|getQueueResourceUsage
argument_list|()
operator|.
name|getUsed
argument_list|(
name|partition
argument_list|)
argument_list|)
operator|)
condition|?
name|Resources
operator|.
name|componentwiseMax
argument_list|(
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|queueGuranteedResource
argument_list|,
name|queue
operator|.
name|getQueueResourceUsage
argument_list|()
operator|.
name|getUsed
argument_list|(
name|partition
argument_list|)
argument_list|)
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
else|:
name|Resources
operator|.
name|none
argument_list|()
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|totalAvailableResource
argument_list|,
name|available
argument_list|)
expr_stmt|;
block|}
return|return
name|totalAvailableResource
return|;
block|}
comment|/**    *<p>    * Update Queue Statistics:    *</p>    *      *<li>used-capacity/absolute-used-capacity by partition</li>     *<li>non-partitioned max-avail-resource to queue</li>    *     *<p>    * When nodePartition is null, all partition of    * used-capacity/absolute-used-capacity will be updated.    *</p>    */
annotation|@
name|Lock
argument_list|(
name|CSQueue
operator|.
name|class
argument_list|)
DECL|method|updateQueueStatistics ( final ResourceCalculator rc, final Resource cluster, final AbstractCSQueue childQueue, final RMNodeLabelsManager nlm, final String nodePartition)
specifier|public
specifier|static
name|void
name|updateQueueStatistics
parameter_list|(
specifier|final
name|ResourceCalculator
name|rc
parameter_list|,
specifier|final
name|Resource
name|cluster
parameter_list|,
specifier|final
name|AbstractCSQueue
name|childQueue
parameter_list|,
specifier|final
name|RMNodeLabelsManager
name|nlm
parameter_list|,
specifier|final
name|String
name|nodePartition
parameter_list|)
block|{
name|QueueCapacities
name|queueCapacities
init|=
name|childQueue
operator|.
name|getQueueCapacities
argument_list|()
decl_stmt|;
name|ResourceUsage
name|queueResourceUsage
init|=
name|childQueue
operator|.
name|getQueueResourceUsage
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodePartition
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|String
name|partition
range|:
name|Sets
operator|.
name|union
argument_list|(
name|queueCapacities
operator|.
name|getNodePartitionsSet
argument_list|()
argument_list|,
name|queueResourceUsage
operator|.
name|getNodePartitionsSet
argument_list|()
argument_list|)
control|)
block|{
name|updateUsedCapacity
argument_list|(
name|rc
argument_list|,
name|nlm
operator|.
name|getResourceByLabel
argument_list|(
name|partition
argument_list|,
name|cluster
argument_list|)
argument_list|,
name|partition
argument_list|,
name|childQueue
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|updateUsedCapacity
argument_list|(
name|rc
argument_list|,
name|nlm
operator|.
name|getResourceByLabel
argument_list|(
name|nodePartition
argument_list|,
name|cluster
argument_list|)
argument_list|,
name|nodePartition
argument_list|,
name|childQueue
argument_list|)
expr_stmt|;
block|}
comment|// Update queue metrics w.r.t node labels. In a generic way, we can
comment|// calculate available resource from all labels in cluster.
name|childQueue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|nodePartition
argument_list|,
name|getMaxAvailableResourceToQueue
argument_list|(
name|rc
argument_list|,
name|nlm
argument_list|,
name|childQueue
argument_list|,
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

