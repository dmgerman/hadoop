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
name|HashSet
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

begin_class
DECL|class|CSQueueUtils
class|class
name|CSQueueUtils
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
name|CSQueueUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EPSILON
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
name|float
name|maximumCapacity
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
name|capacity
operator|>
name|maximumCapacity
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal queue capacity setting, "
operator|+
literal|"(capacity="
operator|+
name|capacity
operator|+
literal|")> (maximum-capacity="
operator|+
name|maximumCapacity
operator|+
literal|"). When label=["
operator|+
name|label
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// Actually, this may not needed since we have verified capacity<=
comment|// maximumCapacity. And the way we compute absolute capacity (abs(x) =
comment|// cap(x) * cap(x.parent) * ...) is a monotone increasing function. But
comment|// just keep it here to make sure our compute abs capacity method works
comment|// correctly.
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
DECL|method|loadUpdateAndCheckCapacities (String queuePath, Set<String> accessibleLabels, CapacitySchedulerConfiguration csConf, QueueCapacities queueCapacities, QueueCapacities parentQueueCapacities, RMNodeLabelsManager nlm)
specifier|public
specifier|static
name|void
name|loadUpdateAndCheckCapacities
parameter_list|(
name|String
name|queuePath
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|accessibleLabels
parameter_list|,
name|CapacitySchedulerConfiguration
name|csConf
parameter_list|,
name|QueueCapacities
name|queueCapacities
parameter_list|,
name|QueueCapacities
name|parentQueueCapacities
parameter_list|,
name|RMNodeLabelsManager
name|nlm
parameter_list|)
block|{
name|loadCapacitiesByLabelsFromConf
argument_list|(
name|queuePath
argument_list|,
name|accessibleLabels
argument_list|,
name|nlm
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
comment|// Considered NO_LABEL, ANY and null cases
DECL|method|normalizeAccessibleNodeLabels (Set<String> labels, RMNodeLabelsManager mgr)
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|normalizeAccessibleNodeLabels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|,
name|RMNodeLabelsManager
name|mgr
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|accessibleLabels
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|labels
operator|!=
literal|null
condition|)
block|{
name|accessibleLabels
operator|.
name|addAll
argument_list|(
name|labels
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|accessibleLabels
operator|.
name|contains
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|ANY
argument_list|)
condition|)
block|{
name|accessibleLabels
operator|.
name|addAll
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabels
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|accessibleLabels
operator|.
name|add
argument_list|(
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
expr_stmt|;
return|return
name|accessibleLabels
return|;
block|}
DECL|method|loadCapacitiesByLabelsFromConf (String queuePath, Set<String> labels, RMNodeLabelsManager mgr, QueueCapacities queueCapacities, CapacitySchedulerConfiguration csConf)
specifier|private
specifier|static
name|void
name|loadCapacitiesByLabelsFromConf
parameter_list|(
name|String
name|queuePath
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|,
name|RMNodeLabelsManager
name|mgr
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
name|labels
operator|=
name|normalizeAccessibleNodeLabels
argument_list|(
name|labels
argument_list|,
name|mgr
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|label
range|:
name|labels
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
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
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
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
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
annotation|@
name|Lock
argument_list|(
name|CSQueue
operator|.
name|class
argument_list|)
DECL|method|updateQueueStatistics ( final ResourceCalculator calculator, final CSQueue childQueue, final CSQueue parentQueue, final Resource clusterResource, final Resource minimumAllocation)
specifier|public
specifier|static
name|void
name|updateQueueStatistics
parameter_list|(
specifier|final
name|ResourceCalculator
name|calculator
parameter_list|,
specifier|final
name|CSQueue
name|childQueue
parameter_list|,
specifier|final
name|CSQueue
name|parentQueue
parameter_list|,
specifier|final
name|Resource
name|clusterResource
parameter_list|,
specifier|final
name|Resource
name|minimumAllocation
parameter_list|)
block|{
name|Resource
name|queueLimit
init|=
name|Resources
operator|.
name|none
argument_list|()
decl_stmt|;
name|Resource
name|usedResources
init|=
name|childQueue
operator|.
name|getUsedResources
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
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|calculator
argument_list|,
name|clusterResource
argument_list|,
name|clusterResource
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
name|queueLimit
operator|=
name|Resources
operator|.
name|multiply
argument_list|(
name|clusterResource
argument_list|,
name|childQueue
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|absoluteUsedCapacity
operator|=
name|Resources
operator|.
name|divide
argument_list|(
name|calculator
argument_list|,
name|clusterResource
argument_list|,
name|usedResources
argument_list|,
name|clusterResource
argument_list|)
expr_stmt|;
name|usedCapacity
operator|=
name|Resources
operator|.
name|equals
argument_list|(
name|queueLimit
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|?
literal|0
else|:
name|Resources
operator|.
name|divide
argument_list|(
name|calculator
argument_list|,
name|clusterResource
argument_list|,
name|usedResources
argument_list|,
name|queueLimit
argument_list|)
expr_stmt|;
block|}
name|childQueue
operator|.
name|setUsedCapacity
argument_list|(
name|usedCapacity
argument_list|)
expr_stmt|;
name|childQueue
operator|.
name|setAbsoluteUsedCapacity
argument_list|(
name|absoluteUsedCapacity
argument_list|)
expr_stmt|;
name|Resource
name|available
init|=
name|Resources
operator|.
name|subtract
argument_list|(
name|queueLimit
argument_list|,
name|usedResources
argument_list|)
decl_stmt|;
name|childQueue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|Resources
operator|.
name|max
argument_list|(
name|calculator
argument_list|,
name|clusterResource
argument_list|,
name|available
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

