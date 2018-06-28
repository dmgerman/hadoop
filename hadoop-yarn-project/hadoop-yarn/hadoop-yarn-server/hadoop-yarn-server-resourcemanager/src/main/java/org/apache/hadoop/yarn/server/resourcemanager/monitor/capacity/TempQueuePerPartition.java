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
name|Arrays
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|scheduler
operator|.
name|capacity
operator|.
name|CSQueue
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
name|LeafQueue
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
name|ParentQueue
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
name|ResourceUtils
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
comment|/**  * Temporary data-structure tracking resource availability, pending resource  * need, current utilization. This is per-queue-per-partition data structure  */
end_comment

begin_class
DECL|class|TempQueuePerPartition
specifier|public
class|class
name|TempQueuePerPartition
extends|extends
name|AbstractPreemptionEntity
block|{
comment|// Following fields are copied from scheduler
DECL|field|partition
specifier|final
name|String
name|partition
decl_stmt|;
DECL|field|killable
specifier|private
specifier|final
name|Resource
name|killable
decl_stmt|;
DECL|field|absCapacity
specifier|private
specifier|final
name|float
name|absCapacity
decl_stmt|;
DECL|field|absMaxCapacity
specifier|private
specifier|final
name|float
name|absMaxCapacity
decl_stmt|;
DECL|field|totalPartitionResource
specifier|final
name|Resource
name|totalPartitionResource
decl_stmt|;
comment|// Following fields are settled and used by candidate selection policies
DECL|field|untouchableExtra
name|Resource
name|untouchableExtra
decl_stmt|;
DECL|field|preemptableExtra
name|Resource
name|preemptableExtra
decl_stmt|;
DECL|field|normalizedGuarantee
name|double
index|[]
name|normalizedGuarantee
decl_stmt|;
DECL|field|effMinRes
specifier|private
name|Resource
name|effMinRes
decl_stmt|;
DECL|field|effMaxRes
specifier|private
name|Resource
name|effMaxRes
decl_stmt|;
DECL|field|children
specifier|final
name|ArrayList
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|children
decl_stmt|;
DECL|field|apps
specifier|private
name|Collection
argument_list|<
name|TempAppPerPartition
argument_list|>
name|apps
decl_stmt|;
DECL|field|leafQueue
name|LeafQueue
name|leafQueue
decl_stmt|;
DECL|field|parentQueue
name|ParentQueue
name|parentQueue
decl_stmt|;
DECL|field|preemptionDisabled
name|boolean
name|preemptionDisabled
decl_stmt|;
DECL|field|pendingDeductReserved
specifier|protected
name|Resource
name|pendingDeductReserved
decl_stmt|;
comment|// Relative priority of this queue to its parent
comment|// If parent queue's ordering policy doesn't respect priority,
comment|// this will be always 0
DECL|field|relativePriority
name|int
name|relativePriority
init|=
literal|0
decl_stmt|;
DECL|field|parent
name|TempQueuePerPartition
name|parent
init|=
literal|null
decl_stmt|;
comment|// This will hold a temp user data structure and will hold userlimit,
comment|// idealAssigned, used etc.
DECL|field|usersPerPartition
name|Map
argument_list|<
name|String
argument_list|,
name|TempUserPerPartition
argument_list|>
name|usersPerPartition
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:parameternumber"
argument_list|)
DECL|method|TempQueuePerPartition (String queueName, Resource current, boolean preemptionDisabled, String partition, Resource killable, float absCapacity, float absMaxCapacity, Resource totalPartitionResource, Resource reserved, CSQueue queue, Resource effMinRes, Resource effMaxRes)
specifier|public
name|TempQueuePerPartition
parameter_list|(
name|String
name|queueName
parameter_list|,
name|Resource
name|current
parameter_list|,
name|boolean
name|preemptionDisabled
parameter_list|,
name|String
name|partition
parameter_list|,
name|Resource
name|killable
parameter_list|,
name|float
name|absCapacity
parameter_list|,
name|float
name|absMaxCapacity
parameter_list|,
name|Resource
name|totalPartitionResource
parameter_list|,
name|Resource
name|reserved
parameter_list|,
name|CSQueue
name|queue
parameter_list|,
name|Resource
name|effMinRes
parameter_list|,
name|Resource
name|effMaxRes
parameter_list|)
block|{
name|super
argument_list|(
name|queueName
argument_list|,
name|current
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|reserved
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
expr_stmt|;
if|if
condition|(
name|queue
operator|instanceof
name|LeafQueue
condition|)
block|{
name|LeafQueue
name|l
init|=
operator|(
name|LeafQueue
operator|)
name|queue
decl_stmt|;
name|pending
operator|=
name|l
operator|.
name|getTotalPendingResourcesConsideringUserLimit
argument_list|(
name|totalPartitionResource
argument_list|,
name|partition
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pendingDeductReserved
operator|=
name|l
operator|.
name|getTotalPendingResourcesConsideringUserLimit
argument_list|(
name|totalPartitionResource
argument_list|,
name|partition
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|leafQueue
operator|=
name|l
expr_stmt|;
block|}
else|else
block|{
name|pending
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pendingDeductReserved
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queue
operator|!=
literal|null
operator|&&
name|ParentQueue
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|queue
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|parentQueue
operator|=
operator|(
name|ParentQueue
operator|)
name|queue
expr_stmt|;
block|}
name|this
operator|.
name|normalizedGuarantee
operator|=
operator|new
name|double
index|[
name|ResourceUtils
operator|.
name|getNumberOfKnownResourceTypes
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|children
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|apps
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|untouchableExtra
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|preemptableExtra
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|preemptionDisabled
operator|=
name|preemptionDisabled
expr_stmt|;
name|this
operator|.
name|partition
operator|=
name|partition
expr_stmt|;
name|this
operator|.
name|killable
operator|=
name|killable
expr_stmt|;
name|this
operator|.
name|absCapacity
operator|=
name|absCapacity
expr_stmt|;
name|this
operator|.
name|absMaxCapacity
operator|=
name|absMaxCapacity
expr_stmt|;
name|this
operator|.
name|totalPartitionResource
operator|=
name|totalPartitionResource
expr_stmt|;
name|this
operator|.
name|effMinRes
operator|=
name|effMinRes
expr_stmt|;
name|this
operator|.
name|effMaxRes
operator|=
name|effMaxRes
expr_stmt|;
block|}
DECL|method|setLeafQueue (LeafQueue l)
specifier|public
name|void
name|setLeafQueue
parameter_list|(
name|LeafQueue
name|l
parameter_list|)
block|{
assert|assert
name|children
operator|.
name|size
argument_list|()
operator|==
literal|0
assert|;
name|this
operator|.
name|leafQueue
operator|=
name|l
expr_stmt|;
block|}
comment|/**    * When adding a child we also aggregate its pending resource needs.    *    * @param q    *          the child queue to add to this queue    */
DECL|method|addChild (TempQueuePerPartition q)
specifier|public
name|void
name|addChild
parameter_list|(
name|TempQueuePerPartition
name|q
parameter_list|)
block|{
assert|assert
name|leafQueue
operator|==
literal|null
assert|;
name|children
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|pending
argument_list|,
name|q
operator|.
name|pending
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|pendingDeductReserved
argument_list|,
name|q
operator|.
name|pendingDeductReserved
argument_list|)
expr_stmt|;
block|}
DECL|method|getChildren ()
specifier|public
name|ArrayList
argument_list|<
name|TempQueuePerPartition
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|children
return|;
block|}
comment|// This function "accepts" all the resources it can (pending) and return
comment|// the unused ones
DECL|method|offer (Resource avail, ResourceCalculator rc, Resource clusterResource, boolean considersReservedResource, boolean allowQueueBalanceAfterAllSafisfied)
name|Resource
name|offer
parameter_list|(
name|Resource
name|avail
parameter_list|,
name|ResourceCalculator
name|rc
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|boolean
name|considersReservedResource
parameter_list|,
name|boolean
name|allowQueueBalanceAfterAllSafisfied
parameter_list|)
block|{
name|Resource
name|absMaxCapIdealAssignedDelta
init|=
name|Resources
operator|.
name|componentwiseMax
argument_list|(
name|Resources
operator|.
name|subtract
argument_list|(
name|getMax
argument_list|()
argument_list|,
name|idealAssigned
argument_list|)
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
decl_stmt|;
comment|// accepted = min{avail,
comment|//               max - assigned,
comment|//               current + pending - assigned,
comment|//               # Make sure a queue will not get more than max of its
comment|//               # used/guaranteed, this is to make sure preemption won't
comment|//               # happen if all active queues are beyond their guaranteed
comment|//               # This is for leaf queue only.
comment|//               max(guaranteed, used) - assigned}
comment|// remain = avail - accepted
name|Resource
name|accepted
init|=
name|Resources
operator|.
name|componentwiseMin
argument_list|(
name|absMaxCapIdealAssignedDelta
argument_list|,
name|Resources
operator|.
name|min
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|avail
argument_list|,
name|Resources
comment|/*              * When we're using FifoPreemptionSelector (considerReservedResource              * = false).              *              * We should deduct reserved resource from pending to avoid excessive              * preemption:              *              * For example, if an under-utilized queue has used = reserved = 20.              * Preemption policy will try to preempt 20 containers (which is not              * satisfied) from different hosts.              *              * In FifoPreemptionSelector, there's no guarantee that preempted              * resource can be used by pending request, so policy will preempt              * resources repeatly.              */
operator|.
name|subtract
argument_list|(
name|Resources
operator|.
name|add
argument_list|(
name|getUsed
argument_list|()
argument_list|,
operator|(
name|considersReservedResource
condition|?
name|pending
else|:
name|pendingDeductReserved
operator|)
argument_list|)
argument_list|,
name|idealAssigned
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// For leaf queue: accept = min(accept, max(guaranteed, used) - assigned)
comment|// Why only for leaf queue?
comment|// Because for a satisfied parent queue, it could have some under-utilized
comment|// leaf queues. Such under-utilized leaf queue could preemption resources
comment|// from over-utilized leaf queue located at other hierarchies.
comment|// Allow queues can continue grow and balance even if all queues are satisfied.
if|if
condition|(
operator|!
name|allowQueueBalanceAfterAllSafisfied
condition|)
block|{
name|accepted
operator|=
name|filterByMaxDeductAssigned
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|accepted
argument_list|)
expr_stmt|;
block|}
comment|// accepted so far contains the "quota acceptable" amount, we now filter by
comment|// locality acceptable
name|accepted
operator|=
name|acceptedByLocality
argument_list|(
name|rc
argument_list|,
name|accepted
argument_list|)
expr_stmt|;
comment|// accept should never be< 0
name|accepted
operator|=
name|Resources
operator|.
name|componentwiseMax
argument_list|(
name|accepted
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
expr_stmt|;
comment|// or more than offered
name|accepted
operator|=
name|Resources
operator|.
name|componentwiseMin
argument_list|(
name|accepted
argument_list|,
name|avail
argument_list|)
expr_stmt|;
name|Resource
name|remain
init|=
name|Resources
operator|.
name|subtract
argument_list|(
name|avail
argument_list|,
name|accepted
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|idealAssigned
argument_list|,
name|accepted
argument_list|)
expr_stmt|;
return|return
name|remain
return|;
block|}
DECL|method|getGuaranteed ()
specifier|public
name|Resource
name|getGuaranteed
parameter_list|()
block|{
if|if
condition|(
operator|!
name|effMinRes
operator|.
name|equals
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Resources
operator|.
name|clone
argument_list|(
name|effMinRes
argument_list|)
return|;
block|}
return|return
name|Resources
operator|.
name|multiply
argument_list|(
name|totalPartitionResource
argument_list|,
name|absCapacity
argument_list|)
return|;
block|}
DECL|method|getMax ()
specifier|public
name|Resource
name|getMax
parameter_list|()
block|{
if|if
condition|(
operator|!
name|effMaxRes
operator|.
name|equals
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Resources
operator|.
name|clone
argument_list|(
name|effMaxRes
argument_list|)
return|;
block|}
return|return
name|Resources
operator|.
name|multiply
argument_list|(
name|totalPartitionResource
argument_list|,
name|absMaxCapacity
argument_list|)
return|;
block|}
DECL|method|updatePreemptableExtras (ResourceCalculator rc)
specifier|public
name|void
name|updatePreemptableExtras
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|)
block|{
comment|// Reset untouchableExtra and preemptableExtra
name|untouchableExtra
operator|=
name|Resources
operator|.
name|none
argument_list|()
expr_stmt|;
name|preemptableExtra
operator|=
name|Resources
operator|.
name|none
argument_list|()
expr_stmt|;
name|Resource
name|extra
init|=
name|Resources
operator|.
name|subtract
argument_list|(
name|getUsed
argument_list|()
argument_list|,
name|getGuaranteed
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Resources
operator|.
name|lessThan
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|extra
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
name|extra
operator|=
name|Resources
operator|.
name|none
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|children
operator|||
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// If it is a leaf queue
if|if
condition|(
name|preemptionDisabled
condition|)
block|{
name|untouchableExtra
operator|=
name|extra
expr_stmt|;
block|}
else|else
block|{
name|preemptableExtra
operator|=
name|extra
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// If it is a parent queue
name|Resource
name|childrensPreemptable
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
name|child
range|:
name|children
control|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|childrensPreemptable
argument_list|,
name|child
operator|.
name|preemptableExtra
argument_list|)
expr_stmt|;
block|}
comment|// untouchableExtra = max(extra - childrenPreemptable, 0)
if|if
condition|(
name|Resources
operator|.
name|greaterThanOrEqual
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|childrensPreemptable
argument_list|,
name|extra
argument_list|)
condition|)
block|{
name|untouchableExtra
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|untouchableExtra
operator|=
name|Resources
operator|.
name|subtract
argument_list|(
name|extra
argument_list|,
name|childrensPreemptable
argument_list|)
expr_stmt|;
block|}
name|preemptableExtra
operator|=
name|Resources
operator|.
name|min
argument_list|(
name|rc
argument_list|,
name|totalPartitionResource
argument_list|,
name|childrensPreemptable
argument_list|,
name|extra
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" NAME: "
operator|+
name|queueName
argument_list|)
operator|.
name|append
argument_list|(
literal|" CUR: "
argument_list|)
operator|.
name|append
argument_list|(
name|current
argument_list|)
operator|.
name|append
argument_list|(
literal|" PEN: "
argument_list|)
operator|.
name|append
argument_list|(
name|pending
argument_list|)
operator|.
name|append
argument_list|(
literal|" RESERVED: "
argument_list|)
operator|.
name|append
argument_list|(
name|reserved
argument_list|)
operator|.
name|append
argument_list|(
literal|" GAR: "
argument_list|)
operator|.
name|append
argument_list|(
name|getGuaranteed
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" NORM: "
argument_list|)
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|normalizedGuarantee
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" IDEAL_ASSIGNED: "
argument_list|)
operator|.
name|append
argument_list|(
name|idealAssigned
argument_list|)
operator|.
name|append
argument_list|(
literal|" IDEAL_PREEMPT: "
argument_list|)
operator|.
name|append
argument_list|(
name|toBePreempted
argument_list|)
operator|.
name|append
argument_list|(
literal|" ACTUAL_PREEMPT: "
argument_list|)
operator|.
name|append
argument_list|(
name|getActuallyToBePreempted
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" UNTOUCHABLE: "
argument_list|)
operator|.
name|append
argument_list|(
name|untouchableExtra
argument_list|)
operator|.
name|append
argument_list|(
literal|" PREEMPTABLE: "
argument_list|)
operator|.
name|append
argument_list|(
name|preemptableExtra
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|assignPreemption (float scalingFactor, ResourceCalculator rc, Resource clusterResource)
specifier|public
name|void
name|assignPreemption
parameter_list|(
name|float
name|scalingFactor
parameter_list|,
name|ResourceCalculator
name|rc
parameter_list|,
name|Resource
name|clusterResource
parameter_list|)
block|{
name|Resource
name|usedDeductKillable
init|=
name|Resources
operator|.
name|subtract
argument_list|(
name|getUsed
argument_list|()
argument_list|,
name|killable
argument_list|)
decl_stmt|;
name|Resource
name|totalResource
init|=
name|Resources
operator|.
name|add
argument_list|(
name|getUsed
argument_list|()
argument_list|,
name|pending
argument_list|)
decl_stmt|;
comment|// The minimum resource that we need to keep for a queue is:
comment|// max(idealAssigned, min(used + pending, guaranteed)).
comment|//
comment|// Doing this because when we calculate ideal allocation doesn't consider
comment|// reserved resource, ideal-allocation calculated could be less than
comment|// guaranteed and total. We should avoid preempt from a queue if it is
comment|// already
comment|//<= its guaranteed resource.
name|Resource
name|minimumQueueResource
init|=
name|Resources
operator|.
name|max
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|Resources
operator|.
name|min
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|totalResource
argument_list|,
name|getGuaranteed
argument_list|()
argument_list|)
argument_list|,
name|idealAssigned
argument_list|)
decl_stmt|;
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
name|usedDeductKillable
argument_list|,
name|minimumQueueResource
argument_list|)
condition|)
block|{
name|toBePreempted
operator|=
name|Resources
operator|.
name|multiply
argument_list|(
name|Resources
operator|.
name|subtract
argument_list|(
name|usedDeductKillable
argument_list|,
name|minimumQueueResource
argument_list|)
argument_list|,
name|scalingFactor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|toBePreempted
operator|=
name|Resources
operator|.
name|none
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|deductActuallyToBePreempted (ResourceCalculator rc, Resource cluster, Resource toBeDeduct)
specifier|public
name|void
name|deductActuallyToBePreempted
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|Resource
name|cluster
parameter_list|,
name|Resource
name|toBeDeduct
parameter_list|)
block|{
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|cluster
argument_list|,
name|getActuallyToBePreempted
argument_list|()
argument_list|,
name|toBeDeduct
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|getActuallyToBePreempted
argument_list|()
argument_list|,
name|toBeDeduct
argument_list|)
expr_stmt|;
block|}
name|setActuallyToBePreempted
argument_list|(
name|Resources
operator|.
name|max
argument_list|(
name|rc
argument_list|,
name|cluster
argument_list|,
name|getActuallyToBePreempted
argument_list|()
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|appendLogString (StringBuilder sb)
name|void
name|appendLogString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|queueName
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|current
operator|.
name|getMemorySize
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|current
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|pending
operator|.
name|getMemorySize
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|pending
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|getGuaranteed
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|getGuaranteed
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|idealAssigned
operator|.
name|getMemorySize
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|idealAssigned
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|toBePreempted
operator|.
name|getMemorySize
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|toBePreempted
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|getActuallyToBePreempted
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|getActuallyToBePreempted
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addAllApps (Collection<TempAppPerPartition> orderedApps)
specifier|public
name|void
name|addAllApps
parameter_list|(
name|Collection
argument_list|<
name|TempAppPerPartition
argument_list|>
name|orderedApps
parameter_list|)
block|{
name|this
operator|.
name|apps
operator|=
name|orderedApps
expr_stmt|;
block|}
DECL|method|getApps ()
specifier|public
name|Collection
argument_list|<
name|TempAppPerPartition
argument_list|>
name|getApps
parameter_list|()
block|{
return|return
name|apps
return|;
block|}
DECL|method|addUserPerPartition (String userName, TempUserPerPartition tmpUser)
specifier|public
name|void
name|addUserPerPartition
parameter_list|(
name|String
name|userName
parameter_list|,
name|TempUserPerPartition
name|tmpUser
parameter_list|)
block|{
name|this
operator|.
name|usersPerPartition
operator|.
name|put
argument_list|(
name|userName
argument_list|,
name|tmpUser
argument_list|)
expr_stmt|;
block|}
DECL|method|getUsersPerPartition ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|TempUserPerPartition
argument_list|>
name|getUsersPerPartition
parameter_list|()
block|{
return|return
name|usersPerPartition
return|;
block|}
DECL|method|setPending (Resource pending)
specifier|public
name|void
name|setPending
parameter_list|(
name|Resource
name|pending
parameter_list|)
block|{
name|this
operator|.
name|pending
operator|=
name|pending
expr_stmt|;
block|}
DECL|method|getIdealAssigned ()
specifier|public
name|Resource
name|getIdealAssigned
parameter_list|()
block|{
return|return
name|idealAssigned
return|;
block|}
DECL|method|toGlobalString ()
specifier|public
name|String
name|toGlobalString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
operator|.
name|append
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|TempQueuePerPartition
name|c
range|:
name|children
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
operator|.
name|toGlobalString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * This method is visible to allow sub-classes to override the behavior,    * specifically to take into account locality-based limitations of how much    * the queue can consumed.    *    * @param rc the ResourceCalculator to be used.    * @param offered the input amount of Resource offered to this queue.    *    * @return  the subset of Resource(s) that the queue can consumed after    *          accounting for locality effects.    */
DECL|method|acceptedByLocality (ResourceCalculator rc, Resource offered)
specifier|protected
name|Resource
name|acceptedByLocality
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|Resource
name|offered
parameter_list|)
block|{
return|return
name|offered
return|;
block|}
comment|/**    * This method is visible to allow sub-classes to override the behavior,    * specifically for federation purposes we do not want to cap resources as it    * is done here.    *    * @param rc the {@code ResourceCalculator} to be used    * @param clusterResource the total cluster resources    * @param offered the resources offered to this queue    * @return the amount of resources accepted after considering max and    *         deducting assigned.    */
DECL|method|filterByMaxDeductAssigned (ResourceCalculator rc, Resource clusterResource, Resource offered)
specifier|protected
name|Resource
name|filterByMaxDeductAssigned
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|offered
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|children
operator|||
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Resource
name|maxOfGuranteedAndUsedDeductAssigned
init|=
name|Resources
operator|.
name|subtract
argument_list|(
name|Resources
operator|.
name|max
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|getUsed
argument_list|()
argument_list|,
name|getGuaranteed
argument_list|()
argument_list|)
argument_list|,
name|idealAssigned
argument_list|)
decl_stmt|;
name|maxOfGuranteedAndUsedDeductAssigned
operator|=
name|Resources
operator|.
name|max
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|maxOfGuranteedAndUsedDeductAssigned
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
expr_stmt|;
name|offered
operator|=
name|Resources
operator|.
name|min
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|offered
argument_list|,
name|maxOfGuranteedAndUsedDeductAssigned
argument_list|)
expr_stmt|;
block|}
return|return
name|offered
return|;
block|}
comment|/**    * This method is visible to allow sub-classes to ovverride the behavior,    * specifically for federation purposes we need to initialize per-sub-cluster    * roots as well as the global one.    */
DECL|method|initializeRootIdealWithGuarangeed ()
specifier|protected
name|void
name|initializeRootIdealWithGuarangeed
parameter_list|()
block|{
name|idealAssigned
operator|=
name|Resources
operator|.
name|clone
argument_list|(
name|getGuaranteed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

