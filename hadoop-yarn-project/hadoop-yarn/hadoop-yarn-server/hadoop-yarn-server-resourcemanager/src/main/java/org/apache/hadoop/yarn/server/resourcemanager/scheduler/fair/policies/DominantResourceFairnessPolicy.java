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
name|resource
operator|.
name|ResourceWeights
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
name|DominantResourceCalculator
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
import|import static
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
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Makes scheduling decisions by trying to equalize dominant resource usage.  * A schedulable's dominant resource usage is the largest ratio of resource  * usage to capacity among the resource types it is using.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|DominantResourceFairnessPolicy
specifier|public
class|class
name|DominantResourceFairnessPolicy
extends|extends
name|SchedulingPolicy
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"DRF"
decl_stmt|;
DECL|field|COMPARATOR
specifier|private
specifier|static
specifier|final
name|DominantResourceFairnessComparator
name|COMPARATOR
init|=
operator|new
name|DominantResourceFairnessComparator
argument_list|()
decl_stmt|;
DECL|field|CALCULATOR
specifier|private
specifier|static
specifier|final
name|DominantResourceCalculator
name|CALCULATOR
init|=
operator|new
name|DominantResourceCalculator
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
name|CALCULATOR
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
for|for
control|(
name|ResourceType
name|type
range|:
name|ResourceType
operator|.
name|values
argument_list|()
control|)
block|{
name|ComputeFairShares
operator|.
name|computeShares
argument_list|(
name|schedulables
argument_list|,
name|totalResources
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|ResourceType
name|type
range|:
name|ResourceType
operator|.
name|values
argument_list|()
control|)
block|{
name|ComputeFairShares
operator|.
name|computeSteadyShares
argument_list|(
name|queues
argument_list|,
name|totalResources
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
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
operator|!
name|Resources
operator|.
name|fitsIn
argument_list|(
name|usage
argument_list|,
name|fairShare
argument_list|)
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
name|int
name|queueAvailableCPU
init|=
name|Math
operator|.
name|max
argument_list|(
name|queueFairShare
operator|.
name|getVirtualCores
argument_list|()
operator|-
name|queueUsage
operator|.
name|getVirtualCores
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
name|Math
operator|.
name|min
argument_list|(
name|maxAvailable
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|queueAvailableCPU
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|headroom
return|;
block|}
annotation|@
name|Override
DECL|method|initialize (Resource clusterCapacity)
specifier|public
name|void
name|initialize
parameter_list|(
name|Resource
name|clusterCapacity
parameter_list|)
block|{
name|COMPARATOR
operator|.
name|setClusterCapacity
argument_list|(
name|clusterCapacity
argument_list|)
expr_stmt|;
block|}
DECL|class|DominantResourceFairnessComparator
specifier|public
specifier|static
class|class
name|DominantResourceFairnessComparator
implements|implements
name|Comparator
argument_list|<
name|Schedulable
argument_list|>
block|{
DECL|field|NUM_RESOURCES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_RESOURCES
init|=
name|ResourceType
operator|.
name|values
argument_list|()
operator|.
name|length
decl_stmt|;
DECL|field|clusterCapacity
specifier|private
name|Resource
name|clusterCapacity
decl_stmt|;
DECL|method|setClusterCapacity (Resource clusterCapacity)
specifier|public
name|void
name|setClusterCapacity
parameter_list|(
name|Resource
name|clusterCapacity
parameter_list|)
block|{
name|this
operator|.
name|clusterCapacity
operator|=
name|clusterCapacity
expr_stmt|;
block|}
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
name|ResourceWeights
name|sharesOfCluster1
init|=
operator|new
name|ResourceWeights
argument_list|()
decl_stmt|;
name|ResourceWeights
name|sharesOfCluster2
init|=
operator|new
name|ResourceWeights
argument_list|()
decl_stmt|;
name|ResourceWeights
name|sharesOfMinShare1
init|=
operator|new
name|ResourceWeights
argument_list|()
decl_stmt|;
name|ResourceWeights
name|sharesOfMinShare2
init|=
operator|new
name|ResourceWeights
argument_list|()
decl_stmt|;
name|ResourceType
index|[]
name|resourceOrder1
init|=
operator|new
name|ResourceType
index|[
name|NUM_RESOURCES
index|]
decl_stmt|;
name|ResourceType
index|[]
name|resourceOrder2
init|=
operator|new
name|ResourceType
index|[
name|NUM_RESOURCES
index|]
decl_stmt|;
comment|// Calculate shares of the cluster for each resource both schedulables.
name|calculateShares
argument_list|(
name|s1
operator|.
name|getResourceUsage
argument_list|()
argument_list|,
name|clusterCapacity
argument_list|,
name|sharesOfCluster1
argument_list|,
name|resourceOrder1
argument_list|,
name|s1
operator|.
name|getWeights
argument_list|()
argument_list|)
expr_stmt|;
name|calculateShares
argument_list|(
name|s1
operator|.
name|getResourceUsage
argument_list|()
argument_list|,
name|s1
operator|.
name|getMinShare
argument_list|()
argument_list|,
name|sharesOfMinShare1
argument_list|,
literal|null
argument_list|,
name|ResourceWeights
operator|.
name|NEUTRAL
argument_list|)
expr_stmt|;
name|calculateShares
argument_list|(
name|s2
operator|.
name|getResourceUsage
argument_list|()
argument_list|,
name|clusterCapacity
argument_list|,
name|sharesOfCluster2
argument_list|,
name|resourceOrder2
argument_list|,
name|s2
operator|.
name|getWeights
argument_list|()
argument_list|)
expr_stmt|;
name|calculateShares
argument_list|(
name|s2
operator|.
name|getResourceUsage
argument_list|()
argument_list|,
name|s2
operator|.
name|getMinShare
argument_list|()
argument_list|,
name|sharesOfMinShare2
argument_list|,
literal|null
argument_list|,
name|ResourceWeights
operator|.
name|NEUTRAL
argument_list|)
expr_stmt|;
comment|// A queue is needy for its min share if its dominant resource
comment|// (with respect to the cluster capacity) is below its configured min share
comment|// for that resource
name|boolean
name|s1Needy
init|=
name|sharesOfMinShare1
operator|.
name|getWeight
argument_list|(
name|resourceOrder1
index|[
literal|0
index|]
argument_list|)
operator|<
literal|1.0f
decl_stmt|;
name|boolean
name|s2Needy
init|=
name|sharesOfMinShare2
operator|.
name|getWeight
argument_list|(
name|resourceOrder2
index|[
literal|0
index|]
argument_list|)
operator|<
literal|1.0f
decl_stmt|;
name|int
name|res
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|s2Needy
operator|&&
operator|!
name|s1Needy
condition|)
block|{
name|res
operator|=
name|compareShares
argument_list|(
name|sharesOfCluster1
argument_list|,
name|sharesOfCluster2
argument_list|,
name|resourceOrder1
argument_list|,
name|resourceOrder2
argument_list|)
expr_stmt|;
block|}
elseif|else
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
else|else
block|{
comment|// both are needy below min share
name|res
operator|=
name|compareShares
argument_list|(
name|sharesOfMinShare1
argument_list|,
name|sharesOfMinShare2
argument_list|,
name|resourceOrder1
argument_list|,
name|resourceOrder2
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
comment|// Apps are tied in fairness ratio. Break the tie by submit time.
name|res
operator|=
call|(
name|int
call|)
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
return|return
name|res
return|;
block|}
comment|/**      * Calculates and orders a resource's share of a pool in terms of two vectors.      * The shares vector contains, for each resource, the fraction of the pool that      * it takes up.  The resourceOrder vector contains an ordering of resources      * by largest share.  So if resource=<10 MB, 5 CPU>, and pool=<100 MB, 10 CPU>,      * shares will be [.1, .5] and resourceOrder will be [CPU, MEMORY].      */
DECL|method|calculateShares (Resource resource, Resource pool, ResourceWeights shares, ResourceType[] resourceOrder, ResourceWeights weights)
name|void
name|calculateShares
parameter_list|(
name|Resource
name|resource
parameter_list|,
name|Resource
name|pool
parameter_list|,
name|ResourceWeights
name|shares
parameter_list|,
name|ResourceType
index|[]
name|resourceOrder
parameter_list|,
name|ResourceWeights
name|weights
parameter_list|)
block|{
name|shares
operator|.
name|setWeight
argument_list|(
name|MEMORY
argument_list|,
operator|(
name|float
operator|)
name|resource
operator|.
name|getMemorySize
argument_list|()
operator|/
operator|(
name|pool
operator|.
name|getMemorySize
argument_list|()
operator|*
name|weights
operator|.
name|getWeight
argument_list|(
name|MEMORY
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|shares
operator|.
name|setWeight
argument_list|(
name|CPU
argument_list|,
operator|(
name|float
operator|)
name|resource
operator|.
name|getVirtualCores
argument_list|()
operator|/
operator|(
name|pool
operator|.
name|getVirtualCores
argument_list|()
operator|*
name|weights
operator|.
name|getWeight
argument_list|(
name|CPU
argument_list|)
operator|)
argument_list|)
expr_stmt|;
comment|// sort order vector by resource share
if|if
condition|(
name|resourceOrder
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|shares
operator|.
name|getWeight
argument_list|(
name|MEMORY
argument_list|)
operator|>
name|shares
operator|.
name|getWeight
argument_list|(
name|CPU
argument_list|)
condition|)
block|{
name|resourceOrder
index|[
literal|0
index|]
operator|=
name|MEMORY
expr_stmt|;
name|resourceOrder
index|[
literal|1
index|]
operator|=
name|CPU
expr_stmt|;
block|}
else|else
block|{
name|resourceOrder
index|[
literal|0
index|]
operator|=
name|CPU
expr_stmt|;
name|resourceOrder
index|[
literal|1
index|]
operator|=
name|MEMORY
expr_stmt|;
block|}
block|}
block|}
DECL|method|compareShares (ResourceWeights shares1, ResourceWeights shares2, ResourceType[] resourceOrder1, ResourceType[] resourceOrder2)
specifier|private
name|int
name|compareShares
parameter_list|(
name|ResourceWeights
name|shares1
parameter_list|,
name|ResourceWeights
name|shares2
parameter_list|,
name|ResourceType
index|[]
name|resourceOrder1
parameter_list|,
name|ResourceType
index|[]
name|resourceOrder2
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|resourceOrder1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ret
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|shares1
operator|.
name|getWeight
argument_list|(
name|resourceOrder1
index|[
name|i
index|]
argument_list|)
operator|-
name|shares2
operator|.
name|getWeight
argument_list|(
name|resourceOrder2
index|[
name|i
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|!=
literal|0
condition|)
block|{
return|return
name|ret
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

