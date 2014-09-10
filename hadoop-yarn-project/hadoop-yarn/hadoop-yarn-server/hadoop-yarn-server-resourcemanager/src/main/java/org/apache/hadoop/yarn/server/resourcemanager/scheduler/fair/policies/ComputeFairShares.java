begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies
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
name|fair
operator|.
name|policies
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
name|Collection
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
name|resource
operator|.
name|ResourceType
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
name|fair
operator|.
name|FSQueue
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
name|fair
operator|.
name|Schedulable
import|;
end_import

begin_comment
comment|/**  * Contains logic for computing the fair shares. A {@link Schedulable}'s fair  * share is {@link Resource} it is entitled to, independent of the current  * demands and allocations on the cluster. A {@link Schedulable} whose resource  * consumption lies at or below its fair share will never have its containers  * preempted.  */
end_comment

begin_class
DECL|class|ComputeFairShares
specifier|public
class|class
name|ComputeFairShares
block|{
DECL|field|COMPUTE_FAIR_SHARES_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|COMPUTE_FAIR_SHARES_ITERATIONS
init|=
literal|25
decl_stmt|;
comment|/**    * Compute fair share of the given schedulables.Fair share is an allocation of    * shares considering only active schedulables ie schedulables which have    * running apps.    *     * @param schedulables    * @param totalResources    * @param type    */
DECL|method|computeShares ( Collection<? extends Schedulable> schedulables, Resource totalResources, ResourceType type)
specifier|public
specifier|static
name|void
name|computeShares
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|schedulables
parameter_list|,
name|Resource
name|totalResources
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
name|computeSharesInternal
argument_list|(
name|schedulables
argument_list|,
name|totalResources
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compute the steady fair share of the given queues. The steady fair    * share is an allocation of shares considering all queues, i.e.,    * active and inactive.    *    * @param queues    * @param totalResources    * @param type    */
DECL|method|computeSteadyShares ( Collection<? extends FSQueue> queues, Resource totalResources, ResourceType type)
specifier|public
specifier|static
name|void
name|computeSteadyShares
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|FSQueue
argument_list|>
name|queues
parameter_list|,
name|Resource
name|totalResources
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
name|computeSharesInternal
argument_list|(
name|queues
argument_list|,
name|totalResources
argument_list|,
name|type
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given a set of Schedulables and a number of slots, compute their weighted    * fair shares. The min and max shares and of the Schedulables are assumed to    * be set beforehand. We compute the fairest possible allocation of shares to    * the Schedulables that respects their min and max shares.    *     * To understand what this method does, we must first define what weighted    * fair sharing means in the presence of min and max shares. If there    * were no minimum or maximum shares, then weighted fair sharing would be    * achieved if the ratio of slotsAssigned / weight was equal for each    * Schedulable and all slots were assigned. Minimum and maximum shares add a    * further twist - Some Schedulables may have a min share higher than their    * assigned share or a max share lower than their assigned share.    *     * To deal with these possibilities, we define an assignment of slots as being    * fair if there exists a ratio R such that: Schedulables S where S.minShare    *> R * S.weight are given share S.minShare - Schedulables S where S.maxShare    *< R * S.weight are given S.maxShare - All other Schedulables S are    * assigned share R * S.weight - The sum of all the shares is totalSlots.    *     * We call R the weight-to-slots ratio because it converts a Schedulable's    * weight to the number of slots it is assigned.    *     * We compute a fair allocation by finding a suitable weight-to-slot ratio R.    * To do this, we use binary search. Given a ratio R, we compute the number of    * slots that would be used in total with this ratio (the sum of the shares    * computed using the conditions above). If this number of slots is less than    * totalSlots, then R is too small and more slots could be assigned. If the    * number of slots is more than totalSlots, then R is too large.    *     * We begin the binary search with a lower bound on R of 0 (which means that    * all Schedulables are only given their minShare) and an upper bound computed    * to be large enough that too many slots are given (by doubling R until we    * use more than totalResources resources). The helper method    * resourceUsedWithWeightToResourceRatio computes the total resources used with a    * given value of R.    *     * The running time of this algorithm is linear in the number of Schedulables,    * because resourceUsedWithWeightToResourceRatio is linear-time and the number of    * iterations of binary search is a constant (dependent on desired precision).    */
DECL|method|computeSharesInternal ( Collection<? extends Schedulable> allSchedulables, Resource totalResources, ResourceType type, boolean isSteadyShare)
specifier|private
specifier|static
name|void
name|computeSharesInternal
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|allSchedulables
parameter_list|,
name|Resource
name|totalResources
parameter_list|,
name|ResourceType
name|type
parameter_list|,
name|boolean
name|isSteadyShare
parameter_list|)
block|{
name|Collection
argument_list|<
name|Schedulable
argument_list|>
name|schedulables
init|=
operator|new
name|ArrayList
argument_list|<
name|Schedulable
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|takenResources
init|=
name|handleFixedFairShares
argument_list|(
name|allSchedulables
argument_list|,
name|schedulables
argument_list|,
name|isSteadyShare
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|schedulables
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Find an upper bound on R that we can use in our binary search. We start
comment|// at R = 1 and double it until we have either used all the resources or we
comment|// have met all Schedulables' max shares.
name|int
name|totalMaxShare
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Schedulable
name|sched
range|:
name|schedulables
control|)
block|{
name|int
name|maxShare
init|=
name|getResourceValue
argument_list|(
name|sched
operator|.
name|getMaxShare
argument_list|()
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxShare
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|totalMaxShare
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
break|break;
block|}
else|else
block|{
name|totalMaxShare
operator|+=
name|maxShare
expr_stmt|;
block|}
block|}
name|int
name|totalResource
init|=
name|Math
operator|.
name|max
argument_list|(
operator|(
name|getResourceValue
argument_list|(
name|totalResources
argument_list|,
name|type
argument_list|)
operator|-
name|takenResources
operator|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|totalResource
operator|=
name|Math
operator|.
name|min
argument_list|(
name|totalMaxShare
argument_list|,
name|totalResource
argument_list|)
expr_stmt|;
name|double
name|rMax
init|=
literal|1.0
decl_stmt|;
while|while
condition|(
name|resourceUsedWithWeightToResourceRatio
argument_list|(
name|rMax
argument_list|,
name|schedulables
argument_list|,
name|type
argument_list|)
operator|<
name|totalResource
condition|)
block|{
name|rMax
operator|*=
literal|2.0
expr_stmt|;
block|}
comment|// Perform the binary search for up to COMPUTE_FAIR_SHARES_ITERATIONS steps
name|double
name|left
init|=
literal|0
decl_stmt|;
name|double
name|right
init|=
name|rMax
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|COMPUTE_FAIR_SHARES_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|double
name|mid
init|=
operator|(
name|left
operator|+
name|right
operator|)
operator|/
literal|2.0
decl_stmt|;
name|int
name|plannedResourceUsed
init|=
name|resourceUsedWithWeightToResourceRatio
argument_list|(
name|mid
argument_list|,
name|schedulables
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|plannedResourceUsed
operator|==
name|totalResource
condition|)
block|{
name|right
operator|=
name|mid
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|plannedResourceUsed
operator|<
name|totalResource
condition|)
block|{
name|left
operator|=
name|mid
expr_stmt|;
block|}
else|else
block|{
name|right
operator|=
name|mid
expr_stmt|;
block|}
block|}
comment|// Set the fair shares based on the value of R we've converged to
for|for
control|(
name|Schedulable
name|sched
range|:
name|schedulables
control|)
block|{
if|if
condition|(
name|isSteadyShare
condition|)
block|{
name|setResourceValue
argument_list|(
name|computeShare
argument_list|(
name|sched
argument_list|,
name|right
argument_list|,
name|type
argument_list|)
argument_list|,
operator|(
operator|(
name|FSQueue
operator|)
name|sched
operator|)
operator|.
name|getSteadyFairShare
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setResourceValue
argument_list|(
name|computeShare
argument_list|(
name|sched
argument_list|,
name|right
argument_list|,
name|type
argument_list|)
argument_list|,
name|sched
operator|.
name|getFairShare
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Compute the resources that would be used given a weight-to-resource ratio    * w2rRatio, for use in the computeFairShares algorithm as described in #    */
DECL|method|resourceUsedWithWeightToResourceRatio (double w2rRatio, Collection<? extends Schedulable> schedulables, ResourceType type)
specifier|private
specifier|static
name|int
name|resourceUsedWithWeightToResourceRatio
parameter_list|(
name|double
name|w2rRatio
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|schedulables
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
name|int
name|resourcesTaken
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Schedulable
name|sched
range|:
name|schedulables
control|)
block|{
name|int
name|share
init|=
name|computeShare
argument_list|(
name|sched
argument_list|,
name|w2rRatio
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|resourcesTaken
operator|+=
name|share
expr_stmt|;
block|}
return|return
name|resourcesTaken
return|;
block|}
comment|/**    * Compute the resources assigned to a Schedulable given a particular    * weight-to-resource ratio w2rRatio.    */
DECL|method|computeShare (Schedulable sched, double w2rRatio, ResourceType type)
specifier|private
specifier|static
name|int
name|computeShare
parameter_list|(
name|Schedulable
name|sched
parameter_list|,
name|double
name|w2rRatio
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
name|double
name|share
init|=
name|sched
operator|.
name|getWeights
argument_list|()
operator|.
name|getWeight
argument_list|(
name|type
argument_list|)
operator|*
name|w2rRatio
decl_stmt|;
name|share
operator|=
name|Math
operator|.
name|max
argument_list|(
name|share
argument_list|,
name|getResourceValue
argument_list|(
name|sched
operator|.
name|getMinShare
argument_list|()
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|share
operator|=
name|Math
operator|.
name|min
argument_list|(
name|share
argument_list|,
name|getResourceValue
argument_list|(
name|sched
operator|.
name|getMaxShare
argument_list|()
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|int
operator|)
name|share
return|;
block|}
comment|/**    * Helper method to handle Schedulabes with fixed fairshares.    * Returns the resources taken by fixed fairshare schedulables,    * and adds the remaining to the passed nonFixedSchedulables.    */
DECL|method|handleFixedFairShares ( Collection<? extends Schedulable> schedulables, Collection<Schedulable> nonFixedSchedulables, boolean isSteadyShare, ResourceType type)
specifier|private
specifier|static
name|int
name|handleFixedFairShares
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|schedulables
parameter_list|,
name|Collection
argument_list|<
name|Schedulable
argument_list|>
name|nonFixedSchedulables
parameter_list|,
name|boolean
name|isSteadyShare
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
name|int
name|totalResource
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Schedulable
name|sched
range|:
name|schedulables
control|)
block|{
name|int
name|fixedShare
init|=
name|getFairShareIfFixed
argument_list|(
name|sched
argument_list|,
name|isSteadyShare
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|fixedShare
operator|<
literal|0
condition|)
block|{
name|nonFixedSchedulables
operator|.
name|add
argument_list|(
name|sched
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setResourceValue
argument_list|(
name|fixedShare
argument_list|,
name|isSteadyShare
condition|?
operator|(
operator|(
name|FSQueue
operator|)
name|sched
operator|)
operator|.
name|getSteadyFairShare
argument_list|()
else|:
name|sched
operator|.
name|getFairShare
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|totalResource
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
operator|(
name|long
operator|)
name|totalResource
operator|+
operator|(
name|long
operator|)
name|fixedShare
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|totalResource
return|;
block|}
comment|/**    * Get the fairshare for the {@link Schedulable} if it is fixed, -1 otherwise.    *    * The fairshare is fixed if either the maxShare is 0, weight is 0,    * or the Schedulable is not active for instantaneous fairshare.    */
DECL|method|getFairShareIfFixed (Schedulable sched, boolean isSteadyShare, ResourceType type)
specifier|private
specifier|static
name|int
name|getFairShareIfFixed
parameter_list|(
name|Schedulable
name|sched
parameter_list|,
name|boolean
name|isSteadyShare
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
comment|// Check if maxShare is 0
if|if
condition|(
name|getResourceValue
argument_list|(
name|sched
operator|.
name|getMaxShare
argument_list|()
argument_list|,
name|type
argument_list|)
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// For instantaneous fairshares, check if queue is active
if|if
condition|(
operator|!
name|isSteadyShare
operator|&&
operator|(
name|sched
operator|instanceof
name|FSQueue
operator|)
operator|&&
operator|!
operator|(
operator|(
name|FSQueue
operator|)
name|sched
operator|)
operator|.
name|isActive
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// Check if weight is 0
if|if
condition|(
name|sched
operator|.
name|getWeights
argument_list|()
operator|.
name|getWeight
argument_list|(
name|type
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|int
name|minShare
init|=
name|getResourceValue
argument_list|(
name|sched
operator|.
name|getMinShare
argument_list|()
argument_list|,
name|type
argument_list|)
decl_stmt|;
return|return
operator|(
name|minShare
operator|<=
literal|0
operator|)
condition|?
literal|0
else|:
name|minShare
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getResourceValue (Resource resource, ResourceType type)
specifier|private
specifier|static
name|int
name|getResourceValue
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|MEMORY
case|:
return|return
name|resource
operator|.
name|getMemory
argument_list|()
return|;
case|case
name|CPU
case|:
return|return
name|resource
operator|.
name|getVirtualCores
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid resource"
argument_list|)
throw|;
block|}
block|}
DECL|method|setResourceValue (int val, Resource resource, ResourceType type)
specifier|private
specifier|static
name|void
name|setResourceValue
parameter_list|(
name|int
name|val
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|ResourceType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|MEMORY
case|:
name|resource
operator|.
name|setMemory
argument_list|(
name|val
argument_list|)
expr_stmt|;
break|break;
case|case
name|CPU
case|:
name|resource
operator|.
name|setVirtualCores
argument_list|(
name|val
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid resource"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

