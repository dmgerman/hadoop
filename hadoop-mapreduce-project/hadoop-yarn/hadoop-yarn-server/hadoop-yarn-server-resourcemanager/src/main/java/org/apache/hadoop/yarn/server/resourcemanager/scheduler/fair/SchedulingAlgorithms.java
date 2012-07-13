begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|Comparator
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
name|Resources
import|;
end_import

begin_comment
comment|/**  * Utility class containing scheduling algorithms used in the fair scheduler.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SchedulingAlgorithms
class|class
name|SchedulingAlgorithms
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SchedulingAlgorithms
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Compare Schedulables in order of priority and then submission time, as in    * the default FIFO scheduler in Hadoop.    */
DECL|class|FifoComparator
specifier|public
specifier|static
class|class
name|FifoComparator
implements|implements
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5905036205491177060L
decl_stmt|;
annotation|@
name|Override
DECL|method|compare (Schedulable s1, Schedulable s2)
specifier|public
name|int
name|compare
parameter_list|(
name|Schedulable
name|s1
parameter_list|,
name|Schedulable
name|s2
parameter_list|)
block|{
name|int
name|res
init|=
name|s1
operator|.
name|getPriority
argument_list|()
operator|.
name|compareTo
argument_list|(
name|s2
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
name|res
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|s1
operator|.
name|getStartTime
argument_list|()
operator|-
name|s2
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
comment|// In the rare case where jobs were submitted at the exact same time,
comment|// compare them by name (which will be the JobID) to get a deterministic
comment|// ordering, so we don't alternately launch tasks from different jobs.
name|res
operator|=
name|s1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|s2
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
comment|/**    * Compare Schedulables via weighted fair sharing. In addition, Schedulables    * below their min share get priority over those whose min share is met.    *    * Schedulables below their min share are compared by how far below it they    * are as a ratio. For example, if job A has 8 out of a min share of 10 tasks    * and job B has 50 out of a min share of 100, then job B is scheduled next,    * because B is at 50% of its min share and A is at 80% of its min share.    *    * Schedulables above their min share are compared by (runningTasks / weight).    * If all weights are equal, slots are given to the job with the fewest tasks;    * otherwise, jobs with more weight get proportionally more slots.    */
DECL|class|FairShareComparator
specifier|public
specifier|static
class|class
name|FairShareComparator
implements|implements
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5564969375856699313L
decl_stmt|;
annotation|@
name|Override
DECL|method|compare (Schedulable s1, Schedulable s2)
specifier|public
name|int
name|compare
parameter_list|(
name|Schedulable
name|s1
parameter_list|,
name|Schedulable
name|s2
parameter_list|)
block|{
name|double
name|minShareRatio1
decl_stmt|,
name|minShareRatio2
decl_stmt|;
name|double
name|useToWeightRatio1
decl_stmt|,
name|useToWeightRatio2
decl_stmt|;
name|Resource
name|minShare1
init|=
name|Resources
operator|.
name|min
argument_list|(
name|s1
operator|.
name|getMinShare
argument_list|()
argument_list|,
name|s1
operator|.
name|getDemand
argument_list|()
argument_list|)
decl_stmt|;
name|Resource
name|minShare2
init|=
name|Resources
operator|.
name|min
argument_list|(
name|s2
operator|.
name|getMinShare
argument_list|()
argument_list|,
name|s2
operator|.
name|getDemand
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|s1Needy
init|=
name|Resources
operator|.
name|lessThan
argument_list|(
name|s1
operator|.
name|getResourceUsage
argument_list|()
argument_list|,
name|minShare1
argument_list|)
decl_stmt|;
name|boolean
name|s2Needy
init|=
name|Resources
operator|.
name|lessThan
argument_list|(
name|s2
operator|.
name|getResourceUsage
argument_list|()
argument_list|,
name|minShare2
argument_list|)
decl_stmt|;
name|Resource
name|one
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|minShareRatio1
operator|=
operator|(
name|double
operator|)
name|s1
operator|.
name|getResourceUsage
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|/
name|Resources
operator|.
name|max
argument_list|(
name|minShare1
argument_list|,
name|one
argument_list|)
operator|.
name|getMemory
argument_list|()
expr_stmt|;
name|minShareRatio2
operator|=
operator|(
name|double
operator|)
name|s2
operator|.
name|getResourceUsage
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|/
name|Resources
operator|.
name|max
argument_list|(
name|minShare2
argument_list|,
name|one
argument_list|)
operator|.
name|getMemory
argument_list|()
expr_stmt|;
name|useToWeightRatio1
operator|=
name|s1
operator|.
name|getResourceUsage
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|/
name|s1
operator|.
name|getWeight
argument_list|()
expr_stmt|;
name|useToWeightRatio2
operator|=
name|s2
operator|.
name|getResourceUsage
argument_list|()
operator|.
name|getMemory
argument_list|()
operator|/
name|s2
operator|.
name|getWeight
argument_list|()
expr_stmt|;
name|int
name|res
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|s1Needy
operator|&&
operator|!
name|s2Needy
condition|)
name|res
operator|=
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|s2Needy
operator|&&
operator|!
name|s1Needy
condition|)
name|res
operator|=
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|s1Needy
operator|&&
name|s2Needy
condition|)
name|res
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|minShareRatio1
operator|-
name|minShareRatio2
argument_list|)
expr_stmt|;
else|else
comment|// Neither schedulable is needy
name|res
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|useToWeightRatio1
operator|-
name|useToWeightRatio2
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
comment|// Apps are tied in fairness ratio. Break the tie by submit time and job
comment|// name to get a deterministic ordering, which is useful for unit tests.
name|res
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|s1
operator|.
name|getStartTime
argument_list|()
operator|-
name|s2
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
name|res
operator|=
name|s1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|s2
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
comment|/**    * Number of iterations for the binary search in computeFairShares. This is    * equivalent to the number of bits of precision in the output. 25 iterations    * gives precision better than 0.1 slots in clusters with one million slots.    */
DECL|field|COMPUTE_FAIR_SHARES_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|COMPUTE_FAIR_SHARES_ITERATIONS
init|=
literal|25
decl_stmt|;
comment|/**    * Given a set of Schedulables and a number of slots, compute their weighted    * fair shares. The min shares and demands of the Schedulables are assumed to    * be set beforehand. We compute the fairest possible allocation of shares    * to the Schedulables that respects their min shares and demands.    *    * To understand what this method does, we must first define what weighted    * fair sharing means in the presence of minimum shares and demands. If there    * were no minimum shares and every Schedulable had an infinite demand (i.e.    * could launch infinitely many tasks), then weighted fair sharing would be    * achieved if the ratio of slotsAssigned / weight was equal for each    * Schedulable and all slots were assigned. Minimum shares and demands add    * two further twists:    * - Some Schedulables may not have enough tasks to fill all their share.    * - Some Schedulables may have a min share higher than their assigned share.    *    * To deal with these possibilities, we define an assignment of slots as    * being fair if there exists a ratio R such that:    * - Schedulables S where S.demand< R * S.weight are assigned share S.demand    * - Schedulables S where S.minShare> R * S.weight are given share S.minShare    * - All other Schedulables S are assigned share R * S.weight    * - The sum of all the shares is totalSlots.    *    * We call R the weight-to-slots ratio because it converts a Schedulable's    * weight to the number of slots it is assigned.    *    * We compute a fair allocation by finding a suitable weight-to-slot ratio R.    * To do this, we use binary search. Given a ratio R, we compute the number    * of slots that would be used in total with this ratio (the sum of the shares    * computed using the conditions above). If this number of slots is less than    * totalSlots, then R is too small and more slots could be assigned. If the    * number of slots is more than totalSlots, then R is too large.    *    * We begin the binary search with a lower bound on R of 0 (which means that    * all Schedulables are only given their minShare) and an upper bound computed    * to be large enough that too many slots are given (by doubling R until we    * either use more than totalSlots slots or we fulfill all jobs' demands).    * The helper method slotsUsedWithWeightToSlotRatio computes the total number    * of slots used with a given value of R.    *    * The running time of this algorithm is linear in the number of Schedulables,    * because slotsUsedWithWeightToSlotRatio is linear-time and the number of    * iterations of binary search is a constant (dependent on desired precision).    */
DECL|method|computeFairShares ( Collection<? extends Schedulable> schedulables, Resource totalResources)
specifier|public
specifier|static
name|void
name|computeFairShares
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
parameter_list|)
block|{
comment|// Find an upper bound on R that we can use in our binary search. We start
comment|// at R = 1 and double it until we have either used totalSlots slots or we
comment|// have met all Schedulables' demands (if total demand< totalSlots).
name|Resource
name|totalDemand
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Schedulable
name|sched
range|:
name|schedulables
control|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|totalDemand
argument_list|,
name|sched
operator|.
name|getDemand
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Resource
name|cap
init|=
name|Resources
operator|.
name|min
argument_list|(
name|totalDemand
argument_list|,
name|totalResources
argument_list|)
decl_stmt|;
name|double
name|rMax
init|=
literal|1.0
decl_stmt|;
while|while
condition|(
name|Resources
operator|.
name|lessThan
argument_list|(
name|resUsedWithWeightToResRatio
argument_list|(
name|rMax
argument_list|,
name|schedulables
argument_list|)
argument_list|,
name|cap
argument_list|)
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
if|if
condition|(
name|Resources
operator|.
name|lessThan
argument_list|(
name|resUsedWithWeightToResRatio
argument_list|(
name|mid
argument_list|,
name|schedulables
argument_list|)
argument_list|,
name|cap
argument_list|)
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
name|sched
operator|.
name|setFairShare
argument_list|(
name|computeShare
argument_list|(
name|sched
argument_list|,
name|right
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Compute the number of slots that would be used given a weight-to-slot    * ratio w2sRatio, for use in the computeFairShares algorithm as described    * in #{@link SchedulingAlgorithms#computeFairShares(Collection, double)}.    */
DECL|method|resUsedWithWeightToResRatio (double w2sRatio, Collection<? extends Schedulable> schedulables)
specifier|private
specifier|static
name|Resource
name|resUsedWithWeightToResRatio
parameter_list|(
name|double
name|w2sRatio
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|Schedulable
argument_list|>
name|schedulables
parameter_list|)
block|{
name|Resource
name|slotsTaken
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Schedulable
name|sched
range|:
name|schedulables
control|)
block|{
name|Resource
name|share
init|=
name|computeShare
argument_list|(
name|sched
argument_list|,
name|w2sRatio
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|slotsTaken
argument_list|,
name|share
argument_list|)
expr_stmt|;
block|}
return|return
name|slotsTaken
return|;
block|}
comment|/**    * Compute the resources assigned to a Schedulable given a particular    * res-to-slot ratio r2sRatio, for use in computeFairShares as described    * in #{@link SchedulingAlgorithms#computeFairShares(Collection, double)}.    */
DECL|method|computeShare (Schedulable sched, double r2sRatio)
specifier|private
specifier|static
name|Resource
name|computeShare
parameter_list|(
name|Schedulable
name|sched
parameter_list|,
name|double
name|r2sRatio
parameter_list|)
block|{
name|double
name|share
init|=
name|sched
operator|.
name|getWeight
argument_list|()
operator|*
name|r2sRatio
decl_stmt|;
name|share
operator|=
name|Math
operator|.
name|max
argument_list|(
name|share
argument_list|,
name|sched
operator|.
name|getMinShare
argument_list|()
operator|.
name|getMemory
argument_list|()
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
name|sched
operator|.
name|getDemand
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Resources
operator|.
name|createResource
argument_list|(
operator|(
name|int
operator|)
name|share
argument_list|)
return|;
block|}
block|}
end_class

end_unit

