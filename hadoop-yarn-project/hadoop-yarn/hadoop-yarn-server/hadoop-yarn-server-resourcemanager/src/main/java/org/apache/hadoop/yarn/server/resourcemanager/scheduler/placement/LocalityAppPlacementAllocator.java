begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.placement
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
name|placement
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|IteratorUtils
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
name|ResourceRequest
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
name|AppSchedulingInfo
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
name|NodeType
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
name|ResourceScheduler
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
name|SchedulerNode
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
name|SchedulingMode
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
name|common
operator|.
name|ContainerRequest
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
name|common
operator|.
name|PendingAsk
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
name|scheduler
operator|.
name|SchedulerRequestKey
import|;
end_import

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
name|Iterator
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
name|Map
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
name|ConcurrentHashMap
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

begin_comment
comment|/**  * This is an implementation of the {@link AppPlacementAllocator} that takes  * into account locality preferences (node, rack, any) when allocating  * containers.  */
end_comment

begin_class
DECL|class|LocalityAppPlacementAllocator
specifier|public
class|class
name|LocalityAppPlacementAllocator
parameter_list|<
name|N
extends|extends
name|SchedulerNode
parameter_list|>
implements|implements
name|AppPlacementAllocator
argument_list|<
name|N
argument_list|>
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
name|LocalityAppPlacementAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceRequestMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|resourceRequestMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|appSchedulingInfo
specifier|private
name|AppSchedulingInfo
name|appSchedulingInfo
decl_stmt|;
DECL|field|primaryRequestedPartition
specifier|private
specifier|volatile
name|String
name|primaryRequestedPartition
init|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
decl_stmt|;
DECL|field|readLock
specifier|private
specifier|final
name|ReentrantReadWriteLock
operator|.
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|ReentrantReadWriteLock
operator|.
name|WriteLock
name|writeLock
decl_stmt|;
DECL|method|LocalityAppPlacementAllocator (AppSchedulingInfo info)
specifier|public
name|LocalityAppPlacementAllocator
parameter_list|(
name|AppSchedulingInfo
name|info
parameter_list|)
block|{
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|appSchedulingInfo
operator|=
name|info
expr_stmt|;
block|}
DECL|method|LocalityAppPlacementAllocator ()
specifier|public
name|LocalityAppPlacementAllocator
parameter_list|()
block|{
name|ReentrantReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getPreferredNodeIterator ( CandidateNodeSet<N> candidateNodeSet)
specifier|public
name|Iterator
argument_list|<
name|N
argument_list|>
name|getPreferredNodeIterator
parameter_list|(
name|CandidateNodeSet
argument_list|<
name|N
argument_list|>
name|candidateNodeSet
parameter_list|)
block|{
comment|// Now only handle the case that single node in the candidateNodeSet
comment|// TODO, Add support to multi-hosts inside candidateNodeSet which is passed
comment|// in.
name|N
name|singleNode
init|=
name|CandidateNodeSetUtils
operator|.
name|getSingleNode
argument_list|(
name|candidateNodeSet
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|singleNode
condition|)
block|{
return|return
name|IteratorUtils
operator|.
name|singletonIterator
argument_list|(
name|singleNode
argument_list|)
return|;
block|}
return|return
name|IteratorUtils
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
DECL|method|hasRequestLabelChanged (ResourceRequest requestOne, ResourceRequest requestTwo)
specifier|private
name|boolean
name|hasRequestLabelChanged
parameter_list|(
name|ResourceRequest
name|requestOne
parameter_list|,
name|ResourceRequest
name|requestTwo
parameter_list|)
block|{
name|String
name|requestOneLabelExp
init|=
name|requestOne
operator|.
name|getNodeLabelExpression
argument_list|()
decl_stmt|;
name|String
name|requestTwoLabelExp
init|=
name|requestTwo
operator|.
name|getNodeLabelExpression
argument_list|()
decl_stmt|;
comment|// First request label expression can be null and second request
comment|// is not null then we have to consider it as changed.
if|if
condition|(
operator|(
literal|null
operator|==
name|requestOneLabelExp
operator|)
operator|&&
operator|(
literal|null
operator|!=
name|requestTwoLabelExp
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// If the label is not matching between both request when
comment|// requestOneLabelExp is not null.
return|return
operator|(
operator|(
literal|null
operator|!=
name|requestOneLabelExp
operator|)
operator|&&
operator|!
operator|(
name|requestOneLabelExp
operator|.
name|equals
argument_list|(
name|requestTwoLabelExp
argument_list|)
operator|)
operator|)
return|;
block|}
DECL|method|updateNodeLabels (ResourceRequest request)
specifier|private
name|void
name|updateNodeLabels
parameter_list|(
name|ResourceRequest
name|request
parameter_list|)
block|{
name|String
name|resourceName
init|=
name|request
operator|.
name|getResourceName
argument_list|()
decl_stmt|;
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
condition|)
block|{
name|ResourceRequest
name|previousAnyRequest
init|=
name|getResourceRequest
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
comment|// When there is change in ANY request label expression, we should
comment|// update label for all resource requests already added of same
comment|// priority as ANY resource request.
if|if
condition|(
operator|(
literal|null
operator|==
name|previousAnyRequest
operator|)
operator|||
name|hasRequestLabelChanged
argument_list|(
name|previousAnyRequest
argument_list|,
name|request
argument_list|)
condition|)
block|{
for|for
control|(
name|ResourceRequest
name|r
range|:
name|resourceRequestMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|r
operator|.
name|getResourceName
argument_list|()
operator|.
name|equals
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
condition|)
block|{
name|r
operator|.
name|setNodeLabelExpression
argument_list|(
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|ResourceRequest
name|anyRequest
init|=
name|getResourceRequest
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
decl_stmt|;
if|if
condition|(
name|anyRequest
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setNodeLabelExpression
argument_list|(
name|anyRequest
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|updatePendingAsk ( Collection<ResourceRequest> requests, boolean recoverPreemptedRequestForAContainer)
specifier|public
name|PendingAskUpdateResult
name|updatePendingAsk
parameter_list|(
name|Collection
argument_list|<
name|ResourceRequest
argument_list|>
name|requests
parameter_list|,
name|boolean
name|recoverPreemptedRequestForAContainer
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|PendingAskUpdateResult
name|updateResult
init|=
literal|null
decl_stmt|;
comment|// Update resource requests
for|for
control|(
name|ResourceRequest
name|request
range|:
name|requests
control|)
block|{
name|String
name|resourceName
init|=
name|request
operator|.
name|getResourceName
argument_list|()
decl_stmt|;
comment|// Update node labels if required
name|updateNodeLabels
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// Increment number of containers if recovering preempted resources
name|ResourceRequest
name|lastRequest
init|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|recoverPreemptedRequestForAContainer
operator|&&
name|lastRequest
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setNumContainers
argument_list|(
name|lastRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// Update asks
name|resourceRequestMap
operator|.
name|put
argument_list|(
name|resourceName
argument_list|,
name|request
argument_list|)
expr_stmt|;
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
condition|)
block|{
name|String
name|partition
init|=
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
operator|==
literal|null
condition|?
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
else|:
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
decl_stmt|;
name|this
operator|.
name|primaryRequestedPartition
operator|=
name|partition
expr_stmt|;
comment|//update the applications requested labels set
name|appSchedulingInfo
operator|.
name|addRequestedPartition
argument_list|(
name|partition
argument_list|)
expr_stmt|;
name|PendingAsk
name|lastPendingAsk
init|=
name|lastRequest
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|PendingAsk
argument_list|(
name|lastRequest
operator|.
name|getCapability
argument_list|()
argument_list|,
name|lastRequest
operator|.
name|getNumContainers
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|lastRequestedNodePartition
init|=
name|lastRequest
operator|==
literal|null
condition|?
literal|null
else|:
name|lastRequest
operator|.
name|getNodeLabelExpression
argument_list|()
decl_stmt|;
name|updateResult
operator|=
operator|new
name|PendingAskUpdateResult
argument_list|(
name|lastPendingAsk
argument_list|,
operator|new
name|PendingAsk
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|,
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|,
name|lastRequestedNodePartition
argument_list|,
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|updateResult
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getResourceRequests ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|getResourceRequests
parameter_list|()
block|{
return|return
name|resourceRequestMap
return|;
block|}
DECL|method|getResourceRequest (String resourceName)
specifier|private
name|ResourceRequest
name|getResourceRequest
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
return|return
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPendingAsk (String resourceName)
specifier|public
name|PendingAsk
name|getPendingAsk
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|ResourceRequest
name|request
init|=
name|getResourceRequest
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|request
condition|)
block|{
return|return
name|PendingAsk
operator|.
name|ZERO
return|;
block|}
else|else
block|{
return|return
operator|new
name|PendingAsk
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|,
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|)
return|;
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
DECL|method|getOutstandingAsksCount (String resourceName)
specifier|public
name|int
name|getOutstandingAsksCount
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|ResourceRequest
name|request
init|=
name|getResourceRequest
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|request
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|request
operator|.
name|getNumContainers
argument_list|()
return|;
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
DECL|method|decrementOutstanding (SchedulerRequestKey schedulerRequestKey, ResourceRequest offSwitchRequest)
specifier|private
name|void
name|decrementOutstanding
parameter_list|(
name|SchedulerRequestKey
name|schedulerRequestKey
parameter_list|,
name|ResourceRequest
name|offSwitchRequest
parameter_list|)
block|{
name|int
name|numOffSwitchContainers
init|=
name|offSwitchRequest
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
decl_stmt|;
name|offSwitchRequest
operator|.
name|setNumContainers
argument_list|(
name|numOffSwitchContainers
argument_list|)
expr_stmt|;
comment|// Do we have any outstanding requests?
comment|// If there is nothing, we need to deactivate this application
if|if
condition|(
name|numOffSwitchContainers
operator|==
literal|0
condition|)
block|{
name|appSchedulingInfo
operator|.
name|getSchedulerKeys
argument_list|()
operator|.
name|remove
argument_list|(
name|schedulerRequestKey
argument_list|)
expr_stmt|;
name|appSchedulingInfo
operator|.
name|checkForDeactivation
argument_list|()
expr_stmt|;
name|resourceRequestMap
operator|.
name|remove
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
expr_stmt|;
if|if
condition|(
name|resourceRequestMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|appSchedulingInfo
operator|.
name|removeAppPlacement
argument_list|(
name|schedulerRequestKey
argument_list|)
expr_stmt|;
block|}
block|}
name|appSchedulingInfo
operator|.
name|decPendingResource
argument_list|(
name|offSwitchRequest
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|,
name|offSwitchRequest
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|cloneResourceRequest (ResourceRequest request)
specifier|public
name|ResourceRequest
name|cloneResourceRequest
parameter_list|(
name|ResourceRequest
name|request
parameter_list|)
block|{
name|ResourceRequest
name|newRequest
init|=
name|ResourceRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|priority
argument_list|(
name|request
operator|.
name|getPriority
argument_list|()
argument_list|)
operator|.
name|allocationRequestId
argument_list|(
name|request
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|)
operator|.
name|resourceName
argument_list|(
name|request
operator|.
name|getResourceName
argument_list|()
argument_list|)
operator|.
name|capability
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|)
operator|.
name|numContainers
argument_list|(
literal|1
argument_list|)
operator|.
name|relaxLocality
argument_list|(
name|request
operator|.
name|getRelaxLocality
argument_list|()
argument_list|)
operator|.
name|nodeLabelExpression
argument_list|(
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|newRequest
return|;
block|}
comment|/**    * The {@link ResourceScheduler} is allocating data-local resources to the    * application.    */
DECL|method|allocateRackLocal (SchedulerRequestKey schedulerKey, SchedulerNode node, ResourceRequest rackLocalRequest, List<ResourceRequest> resourceRequests)
specifier|private
name|void
name|allocateRackLocal
parameter_list|(
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|ResourceRequest
name|rackLocalRequest
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
parameter_list|)
block|{
comment|// Update future requirements
name|decResourceRequest
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|,
name|rackLocalRequest
argument_list|)
expr_stmt|;
name|ResourceRequest
name|offRackRequest
init|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
decl_stmt|;
name|decrementOutstanding
argument_list|(
name|schedulerKey
argument_list|,
name|offRackRequest
argument_list|)
expr_stmt|;
comment|// Update cloned RackLocal and OffRack requests for recovery
name|resourceRequests
operator|.
name|add
argument_list|(
name|cloneResourceRequest
argument_list|(
name|rackLocalRequest
argument_list|)
argument_list|)
expr_stmt|;
name|resourceRequests
operator|.
name|add
argument_list|(
name|cloneResourceRequest
argument_list|(
name|offRackRequest
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * The {@link ResourceScheduler} is allocating data-local resources to the    * application.    */
DECL|method|allocateOffSwitch (SchedulerRequestKey schedulerKey, ResourceRequest offSwitchRequest, List<ResourceRequest> resourceRequests)
specifier|private
name|void
name|allocateOffSwitch
parameter_list|(
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|ResourceRequest
name|offSwitchRequest
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
parameter_list|)
block|{
comment|// Update future requirements
name|decrementOutstanding
argument_list|(
name|schedulerKey
argument_list|,
name|offSwitchRequest
argument_list|)
expr_stmt|;
comment|// Update cloned OffRack requests for recovery
name|resourceRequests
operator|.
name|add
argument_list|(
name|cloneResourceRequest
argument_list|(
name|offSwitchRequest
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * The {@link ResourceScheduler} is allocating data-local resources to the    * application.    */
DECL|method|allocateNodeLocal (SchedulerRequestKey schedulerKey, SchedulerNode node, ResourceRequest nodeLocalRequest, List<ResourceRequest> resourceRequests)
specifier|private
name|void
name|allocateNodeLocal
parameter_list|(
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|ResourceRequest
name|nodeLocalRequest
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
parameter_list|)
block|{
comment|// Update future requirements
name|decResourceRequest
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|nodeLocalRequest
argument_list|)
expr_stmt|;
name|ResourceRequest
name|rackLocalRequest
init|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|)
decl_stmt|;
name|decResourceRequest
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|,
name|rackLocalRequest
argument_list|)
expr_stmt|;
name|ResourceRequest
name|offRackRequest
init|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
decl_stmt|;
name|decrementOutstanding
argument_list|(
name|schedulerKey
argument_list|,
name|offRackRequest
argument_list|)
expr_stmt|;
comment|// Update cloned NodeLocal, RackLocal and OffRack requests for recovery
name|resourceRequests
operator|.
name|add
argument_list|(
name|cloneResourceRequest
argument_list|(
name|nodeLocalRequest
argument_list|)
argument_list|)
expr_stmt|;
name|resourceRequests
operator|.
name|add
argument_list|(
name|cloneResourceRequest
argument_list|(
name|rackLocalRequest
argument_list|)
argument_list|)
expr_stmt|;
name|resourceRequests
operator|.
name|add
argument_list|(
name|cloneResourceRequest
argument_list|(
name|offRackRequest
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|decResourceRequest (String resourceName, ResourceRequest request)
specifier|private
name|void
name|decResourceRequest
parameter_list|(
name|String
name|resourceName
parameter_list|,
name|ResourceRequest
name|request
parameter_list|)
block|{
name|request
operator|.
name|setNumContainers
argument_list|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|==
literal|0
condition|)
block|{
name|resourceRequestMap
operator|.
name|remove
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|canAllocate (NodeType type, SchedulerNode node)
specifier|public
name|boolean
name|canAllocate
parameter_list|(
name|NodeType
name|type
parameter_list|,
name|SchedulerNode
name|node
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|ResourceRequest
name|r
init|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
name|r
operator|.
name|getNumContainers
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|RACK_LOCAL
operator|||
name|type
operator|==
name|NodeType
operator|.
name|NODE_LOCAL
condition|)
block|{
name|r
operator|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
name|r
operator|.
name|getNumContainers
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|NODE_LOCAL
condition|)
block|{
name|r
operator|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
name|r
operator|.
name|getNumContainers
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
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
DECL|method|canDelayTo (String resourceName)
specifier|public
name|boolean
name|canDelayTo
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|ResourceRequest
name|request
init|=
name|getResourceRequest
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
return|return
name|request
operator|==
literal|null
operator|||
name|request
operator|.
name|getRelaxLocality
argument_list|()
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
DECL|method|acceptNodePartition (String nodePartition, SchedulingMode schedulingMode)
specifier|public
name|boolean
name|acceptNodePartition
parameter_list|(
name|String
name|nodePartition
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|)
block|{
comment|// We will only look at node label = nodeLabelToLookAt according to
comment|// schedulingMode and partition of node.
name|String
name|nodePartitionToLookAt
decl_stmt|;
if|if
condition|(
name|schedulingMode
operator|==
name|SchedulingMode
operator|.
name|RESPECT_PARTITION_EXCLUSIVITY
condition|)
block|{
name|nodePartitionToLookAt
operator|=
name|nodePartition
expr_stmt|;
block|}
else|else
block|{
name|nodePartitionToLookAt
operator|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
expr_stmt|;
block|}
return|return
name|primaryRequestedPartition
operator|.
name|equals
argument_list|(
name|nodePartitionToLookAt
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPrimaryRequestedNodePartition ()
specifier|public
name|String
name|getPrimaryRequestedNodePartition
parameter_list|()
block|{
return|return
name|primaryRequestedPartition
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueLocationAsks ()
specifier|public
name|int
name|getUniqueLocationAsks
parameter_list|()
block|{
return|return
name|resourceRequestMap
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|showRequests ()
specifier|public
name|void
name|showRequests
parameter_list|()
block|{
for|for
control|(
name|ResourceRequest
name|request
range|:
name|resourceRequestMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"\tRequest="
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|allocate (SchedulerRequestKey schedulerKey, NodeType type, SchedulerNode node)
specifier|public
name|ContainerRequest
name|allocate
parameter_list|(
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|NodeType
name|type
parameter_list|,
name|SchedulerNode
name|node
parameter_list|)
block|{
try|try
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ResourceRequest
name|request
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|NODE_LOCAL
condition|)
block|{
name|request
operator|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|RACK_LOCAL
condition|)
block|{
name|request
operator|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|=
name|resourceRequestMap
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|NODE_LOCAL
condition|)
block|{
name|allocateNodeLocal
argument_list|(
name|schedulerKey
argument_list|,
name|node
argument_list|,
name|request
argument_list|,
name|resourceRequests
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|RACK_LOCAL
condition|)
block|{
name|allocateRackLocal
argument_list|(
name|schedulerKey
argument_list|,
name|node
argument_list|,
name|request
argument_list|,
name|resourceRequests
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allocateOffSwitch
argument_list|(
name|schedulerKey
argument_list|,
name|request
argument_list|,
name|resourceRequests
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ContainerRequest
argument_list|(
name|resourceRequests
argument_list|)
return|;
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
DECL|method|setAppSchedulingInfo (AppSchedulingInfo appSchedulingInfo)
specifier|public
name|void
name|setAppSchedulingInfo
parameter_list|(
name|AppSchedulingInfo
name|appSchedulingInfo
parameter_list|)
block|{
name|this
operator|.
name|appSchedulingInfo
operator|=
name|appSchedulingInfo
expr_stmt|;
block|}
block|}
end_class

end_unit

