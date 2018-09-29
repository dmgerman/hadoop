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
name|TreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|ImmutableList
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
name|security
operator|.
name|UserGroupInformation
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
name|ApplicationAttemptId
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
name|QueueACL
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
name|QueueUserACLInfo
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
name|rmcontainer
operator|.
name|RMContainer
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
name|ActiveUsersManager
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
name|SchedulerApplicationAttempt
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FSParentQueue
specifier|public
class|class
name|FSParentQueue
extends|extends
name|FSQueue
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
name|FSParentQueue
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|childQueues
specifier|private
specifier|final
name|List
argument_list|<
name|FSQueue
argument_list|>
name|childQueues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|demand
specifier|private
name|Resource
name|demand
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|runnableApps
specifier|private
name|int
name|runnableApps
decl_stmt|;
DECL|field|rwLock
specifier|private
name|ReadWriteLock
name|rwLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|readLock
specifier|private
name|Lock
name|readLock
init|=
name|rwLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
init|=
name|rwLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
DECL|method|FSParentQueue (String name, FairScheduler scheduler, FSParentQueue parent)
specifier|public
name|FSParentQueue
parameter_list|(
name|String
name|name
parameter_list|,
name|FairScheduler
name|scheduler
parameter_list|,
name|FSParentQueue
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|scheduler
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaximumContainerAllocation ()
specifier|public
name|Resource
name|getMaximumContainerAllocation
parameter_list|()
block|{
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"root"
argument_list|)
condition|)
block|{
return|return
name|maxContainerAllocation
return|;
block|}
if|if
condition|(
name|maxContainerAllocation
operator|.
name|equals
argument_list|(
name|Resources
operator|.
name|unbounded
argument_list|()
argument_list|)
operator|&&
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|getParent
argument_list|()
operator|.
name|getMaximumContainerAllocation
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|maxContainerAllocation
return|;
block|}
block|}
DECL|method|addChildQueue (FSQueue child)
name|void
name|addChildQueue
parameter_list|(
name|FSQueue
name|child
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|childQueues
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|removeChildQueue (FSQueue child)
name|void
name|removeChildQueue
parameter_list|(
name|FSQueue
name|child
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|childQueues
operator|.
name|remove
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateInternal ()
name|void
name|updateInternal
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|policy
operator|.
name|computeShares
argument_list|(
name|childQueues
argument_list|,
name|getFairShare
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FSQueue
name|childQueue
range|:
name|childQueues
control|)
block|{
name|childQueue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setFairShare
argument_list|(
name|childQueue
operator|.
name|getFairShare
argument_list|()
argument_list|)
expr_stmt|;
name|childQueue
operator|.
name|updateInternal
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|recomputeSteadyShares ()
name|void
name|recomputeSteadyShares
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|policy
operator|.
name|computeSteadyShares
argument_list|(
name|childQueues
argument_list|,
name|getSteadyFairShare
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FSQueue
name|childQueue
range|:
name|childQueues
control|)
block|{
name|childQueue
operator|.
name|getMetrics
argument_list|()
operator|.
name|setSteadyFairShare
argument_list|(
name|childQueue
operator|.
name|getSteadyFairShare
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|childQueue
operator|instanceof
name|FSParentQueue
condition|)
block|{
operator|(
operator|(
name|FSParentQueue
operator|)
name|childQueue
operator|)
operator|.
name|recomputeSteadyShares
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDemand ()
specifier|public
name|Resource
name|getDemand
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|Resource
operator|.
name|newInstance
argument_list|(
name|demand
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|demand
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateDemand ()
specifier|public
name|void
name|updateDemand
parameter_list|()
block|{
comment|// Compute demand by iterating through apps in the queue
comment|// Limit demand to maxResources
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|demand
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|FSQueue
name|childQueue
range|:
name|childQueues
control|)
block|{
name|childQueue
operator|.
name|updateDemand
argument_list|()
expr_stmt|;
name|Resource
name|toAdd
init|=
name|childQueue
operator|.
name|getDemand
argument_list|()
decl_stmt|;
name|demand
operator|=
name|Resources
operator|.
name|add
argument_list|(
name|demand
argument_list|,
name|toAdd
argument_list|)
expr_stmt|;
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
literal|"Counting resource from "
operator|+
name|childQueue
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|toAdd
operator|+
literal|"; Total resource demand for "
operator|+
name|getName
argument_list|()
operator|+
literal|" now "
operator|+
name|demand
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Cap demand to maxShare to limit allocation to maxShare
name|demand
operator|=
name|Resources
operator|.
name|componentwiseMin
argument_list|(
name|demand
argument_list|,
name|getMaxShare
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
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
literal|"The updated demand for "
operator|+
name|getName
argument_list|()
operator|+
literal|" is "
operator|+
name|demand
operator|+
literal|"; the max is "
operator|+
name|getMaxShare
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getUserAclInfo (UserGroupInformation user)
specifier|private
name|QueueUserACLInfo
name|getUserAclInfo
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|)
block|{
name|List
argument_list|<
name|QueueACL
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|QueueACL
name|operation
range|:
name|QueueACL
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|hasAccess
argument_list|(
name|operation
argument_list|,
name|user
argument_list|)
condition|)
block|{
name|operations
operator|.
name|add
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|QueueUserACLInfo
operator|.
name|newInstance
argument_list|(
name|getQueueName
argument_list|()
argument_list|,
name|operations
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueUserAclInfo (UserGroupInformation user)
specifier|public
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getQueueUserAclInfo
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|)
block|{
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|userAcls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Add queue acls
name|userAcls
operator|.
name|add
argument_list|(
name|getUserAclInfo
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add children queue acls
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|FSQueue
name|child
range|:
name|childQueues
control|)
block|{
name|userAcls
operator|.
name|addAll
argument_list|(
name|child
operator|.
name|getQueueUserAclInfo
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|userAcls
return|;
block|}
annotation|@
name|Override
DECL|method|assignContainer (FSSchedulerNode node)
specifier|public
name|Resource
name|assignContainer
parameter_list|(
name|FSSchedulerNode
name|node
parameter_list|)
block|{
name|Resource
name|assigned
init|=
name|Resources
operator|.
name|none
argument_list|()
decl_stmt|;
comment|// If this queue is over its limit, reject
if|if
condition|(
operator|!
name|assignContainerPreCheck
argument_list|(
name|node
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
literal|"Assign container precheck on node "
operator|+
name|node
operator|+
literal|" failed"
argument_list|)
expr_stmt|;
block|}
return|return
name|assigned
return|;
block|}
comment|// Sort the queues while holding a read lock on this parent only.
comment|// The individual entries are not locked and can change which means that
comment|// the collection of childQueues can not be sorted by calling Sort().
comment|// Locking each childqueue to prevent changes would have a large
comment|// performance impact.
comment|// We do not have to handle the queue removal case as a queue must be
comment|// empty before removal. Assigning an application to a queue and removal of
comment|// that queue both need the scheduler lock.
name|TreeSet
argument_list|<
name|FSQueue
argument_list|>
name|sortedChildQueues
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|policy
operator|.
name|getComparator
argument_list|()
argument_list|)
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|sortedChildQueues
operator|.
name|addAll
argument_list|(
name|childQueues
argument_list|)
expr_stmt|;
for|for
control|(
name|FSQueue
name|child
range|:
name|sortedChildQueues
control|)
block|{
name|assigned
operator|=
name|child
operator|.
name|assignContainer
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Resources
operator|.
name|equals
argument_list|(
name|assigned
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|assigned
return|;
block|}
annotation|@
name|Override
DECL|method|getChildQueues ()
specifier|public
name|List
argument_list|<
name|FSQueue
argument_list|>
name|getChildQueues
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|childQueues
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|incrementRunnableApps ()
name|void
name|incrementRunnableApps
parameter_list|()
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|runnableApps
operator|++
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|decrementRunnableApps ()
name|void
name|decrementRunnableApps
parameter_list|()
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|runnableApps
operator|--
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNumRunnableApps ()
specifier|public
name|int
name|getNumRunnableApps
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|runnableApps
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|collectSchedulerApplications ( Collection<ApplicationAttemptId> apps)
specifier|public
name|void
name|collectSchedulerApplications
parameter_list|(
name|Collection
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|apps
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|FSQueue
name|childQueue
range|:
name|childQueues
control|)
block|{
name|childQueue
operator|.
name|collectSchedulerApplications
argument_list|(
name|apps
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAbstractUsersManager ()
specifier|public
name|ActiveUsersManager
name|getAbstractUsersManager
parameter_list|()
block|{
comment|// Should never be called since all applications are submitted to LeafQueues
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|recoverContainer (Resource clusterResource, SchedulerApplicationAttempt schedulerAttempt, RMContainer rmContainer)
specifier|public
name|void
name|recoverContainer
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|SchedulerApplicationAttempt
name|schedulerAttempt
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
DECL|method|dumpStateInternal (StringBuilder sb)
specifier|protected
name|void
name|dumpStateInternal
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"{Name: "
operator|+
name|getName
argument_list|()
operator|+
literal|", Weight: "
operator|+
name|weights
operator|+
literal|", Policy: "
operator|+
name|policy
operator|.
name|getName
argument_list|()
operator|+
literal|", FairShare: "
operator|+
name|getFairShare
argument_list|()
operator|+
literal|", SteadyFairShare: "
operator|+
name|getSteadyFairShare
argument_list|()
operator|+
literal|", MaxShare: "
operator|+
name|getMaxShare
argument_list|()
operator|+
literal|", MinShare: "
operator|+
name|minShare
operator|+
literal|", ResourceUsage: "
operator|+
name|getResourceUsage
argument_list|()
operator|+
literal|", Demand: "
operator|+
name|getDemand
argument_list|()
operator|+
literal|", MaxAMShare: "
operator|+
name|maxAMShare
operator|+
literal|", Runnable: "
operator|+
name|getNumRunnableApps
argument_list|()
operator|+
literal|"}"
argument_list|)
expr_stmt|;
for|for
control|(
name|FSQueue
name|child
range|:
name|getChildQueues
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|child
operator|.
name|dumpStateInternal
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

