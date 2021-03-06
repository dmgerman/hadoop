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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
name|SchedulingPolicy
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
name|DefaultResourceCalculator
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * Makes scheduling decisions by trying to equalize shares of memory.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FairSharePolicy
specifier|public
class|class
name|FairSharePolicy
extends|extends
name|SchedulingPolicy
block|{
annotation|@
name|VisibleForTesting
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"fair"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FairSharePolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MEMORY
specifier|private
specifier|static
specifier|final
name|String
name|MEMORY
init|=
name|ResourceInformation
operator|.
name|MEMORY_MB
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|RESOURCE_CALCULATOR
specifier|private
specifier|static
specifier|final
name|DefaultResourceCalculator
name|RESOURCE_CALCULATOR
init|=
operator|new
name|DefaultResourceCalculator
argument_list|()
decl_stmt|;
DECL|field|COMPARATOR
specifier|private
specifier|static
specifier|final
name|FairShareComparator
name|COMPARATOR
init|=
operator|new
name|FairShareComparator
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
comment|/**    * Compare Schedulables mainly via fair share usage to meet fairness.    * Specifically, it goes through following four steps.    *    * 1. Compare demands. Schedulables without resource demand get lower priority    * than ones who have demands.    *     * 2. Compare min share usage. Schedulables below their min share are compared    * by how far below it they are as a ratio. For example, if job A has 8 out    * of a min share of 10 tasks and job B has 50 out of a min share of 100,    * then job B is scheduled next, because B is at 50% of its min share and A    * is at 80% of its min share.    *     * 3. Compare fair share usage. Schedulables above their min share are    * compared by fair share usage by checking (resource usage / weight).    * If all weights are equal, slots are given to the job with the fewest tasks;    * otherwise, jobs with more weight get proportionally more slots. If weight    * equals to 0, we can't compare Schedulables by (resource usage/weight).    * There are two situations: 1)All weights equal to 0, slots are given    * to one with less resource usage. 2)Only one of weight equals to 0, slots    * are given to the one with non-zero weight.    *    * 4. Break the tie by compare submit time and job name.    */
DECL|class|FairShareComparator
specifier|private
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
name|int
name|res
init|=
name|compareDemand
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
decl_stmt|;
comment|// Share resource usages to avoid duplicate calculation
name|Resource
name|resourceUsage1
init|=
literal|null
decl_stmt|;
name|Resource
name|resourceUsage2
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
name|resourceUsage1
operator|=
name|s1
operator|.
name|getResourceUsage
argument_list|()
expr_stmt|;
name|resourceUsage2
operator|=
name|s2
operator|.
name|getResourceUsage
argument_list|()
expr_stmt|;
name|res
operator|=
name|compareMinShareUsage
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|,
name|resourceUsage1
argument_list|,
name|resourceUsage2
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
name|res
operator|=
name|compareFairShareUsage
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|,
name|resourceUsage1
argument_list|,
name|resourceUsage2
argument_list|)
expr_stmt|;
block|}
comment|// Break the tie by submit time
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
comment|// Break the tie by job name
if|if
condition|(
name|res
operator|==
literal|0
condition|)
block|{
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
DECL|method|compareDemand (Schedulable s1, Schedulable s2)
specifier|private
name|int
name|compareDemand
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
literal|0
decl_stmt|;
name|long
name|demand1
init|=
name|s1
operator|.
name|getDemand
argument_list|()
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
name|long
name|demand2
init|=
name|s2
operator|.
name|getDemand
argument_list|()
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|demand1
operator|==
literal|0
operator|)
operator|&&
operator|(
name|demand2
operator|>
literal|0
operator|)
condition|)
block|{
name|res
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|demand2
operator|==
literal|0
operator|)
operator|&&
operator|(
name|demand1
operator|>
literal|0
operator|)
condition|)
block|{
name|res
operator|=
operator|-
literal|1
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
DECL|method|compareMinShareUsage (Schedulable s1, Schedulable s2, Resource resourceUsage1, Resource resourceUsage2)
specifier|private
name|int
name|compareMinShareUsage
parameter_list|(
name|Schedulable
name|s1
parameter_list|,
name|Schedulable
name|s2
parameter_list|,
name|Resource
name|resourceUsage1
parameter_list|,
name|Resource
name|resourceUsage2
parameter_list|)
block|{
name|int
name|res
decl_stmt|;
name|long
name|minShare1
init|=
name|Math
operator|.
name|min
argument_list|(
name|s1
operator|.
name|getMinShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|s1
operator|.
name|getDemand
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|minShare2
init|=
name|Math
operator|.
name|min
argument_list|(
name|s2
operator|.
name|getMinShare
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|s2
operator|.
name|getDemand
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|s1Needy
init|=
name|resourceUsage1
operator|.
name|getMemorySize
argument_list|()
operator|<
name|minShare1
decl_stmt|;
name|boolean
name|s2Needy
init|=
name|resourceUsage2
operator|.
name|getMemorySize
argument_list|()
operator|<
name|minShare2
decl_stmt|;
if|if
condition|(
name|s1Needy
operator|&&
operator|!
name|s2Needy
condition|)
block|{
name|res
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s2Needy
operator|&&
operator|!
name|s1Needy
condition|)
block|{
name|res
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s1Needy
operator|&&
name|s2Needy
condition|)
block|{
name|double
name|minShareRatio1
init|=
operator|(
name|double
operator|)
name|resourceUsage1
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
name|double
name|minShareRatio2
init|=
operator|(
name|double
operator|)
name|resourceUsage2
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
if|if
condition|(
name|minShare1
operator|>
literal|1
condition|)
block|{
name|minShareRatio1
operator|/=
name|minShare1
expr_stmt|;
block|}
if|if
condition|(
name|minShare2
operator|>
literal|1
condition|)
block|{
name|minShareRatio2
operator|/=
name|minShare2
expr_stmt|;
block|}
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
block|}
else|else
block|{
name|res
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/**      * To simplify computation, use weights instead of fair shares to calculate      * fair share usage.      */
DECL|method|compareFairShareUsage (Schedulable s1, Schedulable s2, Resource resourceUsage1, Resource resourceUsage2)
specifier|private
name|int
name|compareFairShareUsage
parameter_list|(
name|Schedulable
name|s1
parameter_list|,
name|Schedulable
name|s2
parameter_list|,
name|Resource
name|resourceUsage1
parameter_list|,
name|Resource
name|resourceUsage2
parameter_list|)
block|{
name|double
name|weight1
init|=
name|s1
operator|.
name|getWeight
argument_list|()
decl_stmt|;
name|double
name|weight2
init|=
name|s2
operator|.
name|getWeight
argument_list|()
decl_stmt|;
name|double
name|useToWeightRatio1
decl_stmt|;
name|double
name|useToWeightRatio2
decl_stmt|;
if|if
condition|(
name|weight1
operator|>
literal|0.0
operator|&&
name|weight2
operator|>
literal|0.0
condition|)
block|{
name|useToWeightRatio1
operator|=
name|resourceUsage1
operator|.
name|getMemorySize
argument_list|()
operator|/
name|weight1
expr_stmt|;
name|useToWeightRatio2
operator|=
name|resourceUsage2
operator|.
name|getMemorySize
argument_list|()
operator|/
name|weight2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|weight1
operator|==
name|weight2
condition|)
block|{
comment|// Either weight1 or weight2 equals to 0
comment|// If they have same weight, just compare usage
name|useToWeightRatio1
operator|=
name|resourceUsage1
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
name|useToWeightRatio2
operator|=
name|resourceUsage2
operator|.
name|getMemorySize
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// By setting useToWeightRatios to negative weights, we give the
comment|// zero-weight one less priority, so the non-zero weight one will
comment|// be given slots.
name|useToWeightRatio1
operator|=
operator|-
name|weight1
expr_stmt|;
name|useToWeightRatio2
operator|=
operator|-
name|weight2
expr_stmt|;
block|}
return|return
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
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getComparator ()
specifier|public
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|COMPARATOR
return|;
block|}
annotation|@
name|Override
DECL|method|getResourceCalculator ()
specifier|public
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
block|{
return|return
name|RESOURCE_CALCULATOR
return|;
block|}
annotation|@
name|Override
DECL|method|getHeadroom (Resource queueFairShare, Resource queueUsage, Resource maxAvailable)
specifier|public
name|Resource
name|getHeadroom
parameter_list|(
name|Resource
name|queueFairShare
parameter_list|,
name|Resource
name|queueUsage
parameter_list|,
name|Resource
name|maxAvailable
parameter_list|)
block|{
name|long
name|queueAvailableMemory
init|=
name|Math
operator|.
name|max
argument_list|(
name|queueFairShare
operator|.
name|getMemorySize
argument_list|()
operator|-
name|queueUsage
operator|.
name|getMemorySize
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Resource
name|headroom
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|maxAvailable
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|queueAvailableMemory
argument_list|)
argument_list|,
name|maxAvailable
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|headroom
return|;
block|}
annotation|@
name|Override
DECL|method|computeShares (Collection<? extends Schedulable> schedulables, Resource totalResources)
specifier|public
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
parameter_list|)
block|{
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|schedulables
argument_list|,
name|totalResources
argument_list|,
name|MEMORY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeSteadyShares (Collection<? extends FSQueue> queues, Resource totalResources)
specifier|public
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
parameter_list|)
block|{
name|ComputeFairShares
operator|.
name|computeSteadyShares
argument_list|(
name|queues
argument_list|,
name|totalResources
argument_list|,
name|MEMORY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkIfUsageOverFairShare (Resource usage, Resource fairShare)
specifier|public
name|boolean
name|checkIfUsageOverFairShare
parameter_list|(
name|Resource
name|usage
parameter_list|,
name|Resource
name|fairShare
parameter_list|)
block|{
return|return
name|usage
operator|.
name|getMemorySize
argument_list|()
operator|>
name|fairShare
operator|.
name|getMemorySize
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isChildPolicyAllowed (SchedulingPolicy childPolicy)
specifier|public
name|boolean
name|isChildPolicyAllowed
parameter_list|(
name|SchedulingPolicy
name|childPolicy
parameter_list|)
block|{
if|if
condition|(
name|childPolicy
operator|instanceof
name|DominantResourceFairnessPolicy
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Queue policy can't be "
operator|+
name|DominantResourceFairnessPolicy
operator|.
name|NAME
operator|+
literal|" if the parent policy is "
operator|+
name|getName
argument_list|()
operator|+
literal|". Choose "
operator|+
name|getName
argument_list|()
operator|+
literal|" or "
operator|+
name|FifoPolicy
operator|.
name|NAME
operator|+
literal|" for child queues instead."
operator|+
literal|" Please note that "
operator|+
name|FifoPolicy
operator|.
name|NAME
operator|+
literal|" is only for leaf queues."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

