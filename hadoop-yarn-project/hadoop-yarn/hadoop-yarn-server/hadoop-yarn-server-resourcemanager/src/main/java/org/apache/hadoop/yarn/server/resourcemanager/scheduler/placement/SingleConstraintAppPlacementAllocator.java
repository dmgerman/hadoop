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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ExecutionType
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
name|api
operator|.
name|records
operator|.
name|ResourceSizing
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
name|SchedulingRequest
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
name|impl
operator|.
name|pb
operator|.
name|SchedulingRequestPBImpl
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
name|resource
operator|.
name|PlacementConstraint
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
name|exceptions
operator|.
name|SchedulerInvalidResoureRequestException
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
name|RMContext
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|constraint
operator|.
name|AllocationTagsManager
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
name|constraint
operator|.
name|InvalidAllocationTagsQueryException
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
name|constraint
operator|.
name|PlacementConstraintManager
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
name|constraint
operator|.
name|PlacementConstraintsUtil
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
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
import|import static
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
name|resource
operator|.
name|PlacementConstraint
operator|.
name|TargetExpression
operator|.
name|TargetType
operator|.
name|NODE_ATTRIBUTE
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
name|api
operator|.
name|resource
operator|.
name|PlacementConstraints
operator|.
name|NODE_PARTITION
import|;
end_import

begin_comment
comment|/**  * This is a simple implementation to do affinity or anti-affinity for  * inter/intra apps.  */
end_comment

begin_class
DECL|class|SingleConstraintAppPlacementAllocator
specifier|public
class|class
name|SingleConstraintAppPlacementAllocator
parameter_list|<
name|N
extends|extends
name|SchedulerNode
parameter_list|>
extends|extends
name|AppPlacementAllocator
argument_list|<
name|N
argument_list|>
block|{
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
name|SingleConstraintAppPlacementAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|readLock
specifier|private
name|ReentrantReadWriteLock
operator|.
name|ReadLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
name|ReentrantReadWriteLock
operator|.
name|WriteLock
name|writeLock
decl_stmt|;
DECL|field|schedulingRequest
specifier|private
name|SchedulingRequest
name|schedulingRequest
init|=
literal|null
decl_stmt|;
DECL|field|targetNodePartition
specifier|private
name|String
name|targetNodePartition
decl_stmt|;
DECL|field|allocationTagsManager
specifier|private
name|AllocationTagsManager
name|allocationTagsManager
decl_stmt|;
DECL|field|placementConstraintManager
specifier|private
name|PlacementConstraintManager
name|placementConstraintManager
decl_stmt|;
DECL|method|SingleConstraintAppPlacementAllocator ()
specifier|public
name|SingleConstraintAppPlacementAllocator
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
if|if
condition|(
name|requests
operator|!=
literal|null
operator|&&
operator|!
name|requests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SchedulerInvalidResoureRequestException
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" not be able to handle ResourceRequest, there exists a "
operator|+
literal|"SchedulingRequest with the same scheduler key="
operator|+
name|SchedulerRequestKey
operator|.
name|create
argument_list|(
name|requests
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
operator|+
literal|", please send ResourceRequest with a different allocationId and "
operator|+
literal|"priority"
argument_list|)
throw|;
block|}
comment|// Do nothing
return|return
literal|null
return|;
block|}
DECL|method|internalUpdatePendingAsk ( SchedulingRequest newSchedulingRequest, boolean recoverContainer)
specifier|private
name|PendingAskUpdateResult
name|internalUpdatePendingAsk
parameter_list|(
name|SchedulingRequest
name|newSchedulingRequest
parameter_list|,
name|boolean
name|recoverContainer
parameter_list|)
block|{
comment|// When it is a recover container, there must exists an schedulingRequest.
if|if
condition|(
name|recoverContainer
operator|&&
name|schedulingRequest
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SchedulerInvalidResoureRequestException
argument_list|(
literal|"Trying to recover a "
operator|+
literal|"container request="
operator|+
name|newSchedulingRequest
operator|.
name|toString
argument_list|()
operator|+
literal|", however"
operator|+
literal|"there's no existing scheduling request, this should not happen."
argument_list|)
throw|;
block|}
if|if
condition|(
name|schedulingRequest
operator|!=
literal|null
condition|)
block|{
comment|// If we have an old scheduling request, we will make sure that no changes
comment|// made except sizing.
comment|// To avoid unnecessary copy of the data structure, we do this by
comment|// replacing numAllocations with old numAllocations in the
comment|// newSchedulingRequest#getResourceSizing, and compare the two objects.
name|ResourceSizing
name|sizing
init|=
name|newSchedulingRequest
operator|.
name|getResourceSizing
argument_list|()
decl_stmt|;
name|int
name|existingNumAllocations
init|=
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
decl_stmt|;
comment|// When it is a recovered container request, just set
comment|// #newAllocations = #existingAllocations + 1;
name|int
name|newNumAllocations
decl_stmt|;
if|if
condition|(
name|recoverContainer
condition|)
block|{
name|newNumAllocations
operator|=
name|existingNumAllocations
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|newNumAllocations
operator|=
name|sizing
operator|.
name|getNumAllocations
argument_list|()
expr_stmt|;
block|}
name|sizing
operator|.
name|setNumAllocations
argument_list|(
name|existingNumAllocations
argument_list|)
expr_stmt|;
comment|// Compare two objects
if|if
condition|(
operator|!
name|schedulingRequest
operator|.
name|equals
argument_list|(
name|newSchedulingRequest
argument_list|)
condition|)
block|{
comment|// Rollback #numAllocations
name|sizing
operator|.
name|setNumAllocations
argument_list|(
name|newNumAllocations
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SchedulerInvalidResoureRequestException
argument_list|(
literal|"Invalid updated SchedulingRequest added to scheduler, "
operator|+
literal|" we only allows changing numAllocations for the updated "
operator|+
literal|"SchedulingRequest. Old="
operator|+
name|schedulingRequest
operator|.
name|toString
argument_list|()
operator|+
literal|" new="
operator|+
name|newSchedulingRequest
operator|.
name|toString
argument_list|()
operator|+
literal|", if any fields need to be updated, please cancel the "
operator|+
literal|"old request (by setting numAllocations to 0) and send a "
operator|+
literal|"SchedulingRequest with different combination of "
operator|+
literal|"priority/allocationId"
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|newNumAllocations
operator|==
name|existingNumAllocations
condition|)
block|{
comment|// No update on pending asks, return null.
return|return
literal|null
return|;
block|}
block|}
comment|// Rollback #numAllocations
name|sizing
operator|.
name|setNumAllocations
argument_list|(
name|newNumAllocations
argument_list|)
expr_stmt|;
comment|// Basic sanity check
if|if
condition|(
name|newNumAllocations
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SchedulerInvalidResoureRequestException
argument_list|(
literal|"numAllocation in ResourceSizing field must be>= 0, "
operator|+
literal|"updating schedulingRequest failed."
argument_list|)
throw|;
block|}
name|PendingAskUpdateResult
name|updateResult
init|=
operator|new
name|PendingAskUpdateResult
argument_list|(
operator|new
name|PendingAsk
argument_list|(
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
argument_list|)
argument_list|,
operator|new
name|PendingAsk
argument_list|(
name|newSchedulingRequest
operator|.
name|getResourceSizing
argument_list|()
argument_list|)
argument_list|,
name|targetNodePartition
argument_list|,
name|targetNodePartition
argument_list|)
decl_stmt|;
comment|// Ok, now everything is same except numAllocation, update numAllocation.
name|this
operator|.
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|setNumAllocations
argument_list|(
name|newNumAllocations
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Update numAllocation from old="
operator|+
name|existingNumAllocations
operator|+
literal|" to new="
operator|+
name|newNumAllocations
argument_list|)
expr_stmt|;
return|return
name|updateResult
return|;
block|}
comment|// For a new schedulingRequest, we need to validate if we support its asks.
comment|// This will update internal partitions, etc. after the SchedulingRequest is
comment|// valid.
name|validateAndSetSchedulingRequest
argument_list|(
name|newSchedulingRequest
argument_list|)
expr_stmt|;
return|return
operator|new
name|PendingAskUpdateResult
argument_list|(
literal|null
argument_list|,
operator|new
name|PendingAsk
argument_list|(
name|newSchedulingRequest
operator|.
name|getResourceSizing
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|targetNodePartition
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updatePendingAsk ( SchedulerRequestKey schedulerRequestKey, SchedulingRequest newSchedulingRequest, boolean recoverPreemptedRequestForAContainer)
specifier|public
name|PendingAskUpdateResult
name|updatePendingAsk
parameter_list|(
name|SchedulerRequestKey
name|schedulerRequestKey
parameter_list|,
name|SchedulingRequest
name|newSchedulingRequest
parameter_list|,
name|boolean
name|recoverPreemptedRequestForAContainer
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|internalUpdatePendingAsk
argument_list|(
name|newSchedulingRequest
argument_list|,
name|recoverPreemptedRequestForAContainer
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
DECL|method|throwExceptionWithMetaInfo (String message)
specifier|private
name|String
name|throwExceptionWithMetaInfo
parameter_list|(
name|String
name|message
parameter_list|)
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
literal|"AppId="
argument_list|)
operator|.
name|append
argument_list|(
name|appSchedulingInfo
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" Key="
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|schedulerRequestKey
argument_list|)
operator|.
name|append
argument_list|(
literal|". Exception message:"
argument_list|)
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SchedulerInvalidResoureRequestException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|validateAndSetSchedulingRequest (SchedulingRequest newSchedulingRequest)
specifier|private
name|void
name|validateAndSetSchedulingRequest
parameter_list|(
name|SchedulingRequest
name|newSchedulingRequest
parameter_list|)
throws|throws
name|SchedulerInvalidResoureRequestException
block|{
comment|// Check sizing exists
if|if
condition|(
name|newSchedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|==
literal|null
operator|||
name|newSchedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getResources
argument_list|()
operator|==
literal|null
condition|)
block|{
name|throwExceptionWithMetaInfo
argument_list|(
literal|"No ResourceSizing found in the scheduling request, please double "
operator|+
literal|"check"
argument_list|)
expr_stmt|;
block|}
comment|// Check execution type == GUARANTEED
if|if
condition|(
name|newSchedulingRequest
operator|.
name|getExecutionType
argument_list|()
operator|!=
literal|null
operator|&&
name|newSchedulingRequest
operator|.
name|getExecutionType
argument_list|()
operator|.
name|getExecutionType
argument_list|()
operator|!=
name|ExecutionType
operator|.
name|GUARANTEED
condition|)
block|{
name|throwExceptionWithMetaInfo
argument_list|(
literal|"Only GUARANTEED execution type is supported."
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|targetNodePartition
operator|=
name|validateAndGetTargetNodePartition
argument_list|(
name|newSchedulingRequest
operator|.
name|getPlacementConstraint
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|schedulingRequest
operator|=
operator|new
name|SchedulingRequestPBImpl
argument_list|(
operator|(
operator|(
name|SchedulingRequestPBImpl
operator|)
name|newSchedulingRequest
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully added SchedulingRequest to app="
operator|+
name|appSchedulingInfo
operator|.
name|getApplicationAttemptId
argument_list|()
operator|+
literal|" placementConstraint=["
operator|+
name|schedulingRequest
operator|.
name|getPlacementConstraint
argument_list|()
operator|+
literal|"]. nodePartition="
operator|+
name|targetNodePartition
argument_list|)
expr_stmt|;
block|}
comment|// Tentatively find out potential exist node-partition in the placement
comment|// constraint and set as the app's primary node-partition.
comment|// Currently only single constraint is handled.
DECL|method|validateAndGetTargetNodePartition ( PlacementConstraint placementConstraint)
specifier|private
name|String
name|validateAndGetTargetNodePartition
parameter_list|(
name|PlacementConstraint
name|placementConstraint
parameter_list|)
block|{
name|String
name|nodePartition
init|=
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
decl_stmt|;
if|if
condition|(
name|placementConstraint
operator|!=
literal|null
operator|&&
name|placementConstraint
operator|.
name|getConstraintExpr
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PlacementConstraint
operator|.
name|AbstractConstraint
name|ac
init|=
name|placementConstraint
operator|.
name|getConstraintExpr
argument_list|()
decl_stmt|;
if|if
condition|(
name|ac
operator|!=
literal|null
operator|&&
name|ac
operator|instanceof
name|PlacementConstraint
operator|.
name|SingleConstraint
condition|)
block|{
name|PlacementConstraint
operator|.
name|SingleConstraint
name|singleConstraint
init|=
operator|(
name|PlacementConstraint
operator|.
name|SingleConstraint
operator|)
name|ac
decl_stmt|;
for|for
control|(
name|PlacementConstraint
operator|.
name|TargetExpression
name|targetExpression
range|:
name|singleConstraint
operator|.
name|getTargetExpressions
argument_list|()
control|)
block|{
comment|// Handle node partition
if|if
condition|(
name|targetExpression
operator|.
name|getTargetType
argument_list|()
operator|.
name|equals
argument_list|(
name|NODE_ATTRIBUTE
argument_list|)
operator|&&
name|targetExpression
operator|.
name|getTargetKey
argument_list|()
operator|.
name|equals
argument_list|(
name|NODE_PARTITION
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
name|targetExpression
operator|.
name|getTargetValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
operator|||
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|throwExceptionWithMetaInfo
argument_list|(
literal|"Inside one targetExpression, we only support"
operator|+
literal|" affinity to at most one node partition now"
argument_list|)
expr_stmt|;
block|}
name|nodePartition
operator|=
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodePartition
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
block|}
return|return
name|nodePartition
return|;
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
name|Collections
operator|.
name|emptyMap
argument_list|()
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
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
operator|&&
name|schedulingRequest
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|PendingAsk
argument_list|(
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
argument_list|)
return|;
block|}
return|return
name|PendingAsk
operator|.
name|ZERO
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
DECL|method|getOutstandingAsksCount (String resourceName)
specifier|public
name|int
name|getOutstandingAsksCount
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
operator|&&
name|schedulingRequest
operator|!=
literal|null
condition|)
block|{
return|return
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
return|;
block|}
return|return
literal|0
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
DECL|method|decreasePendingNumAllocation ()
specifier|private
name|void
name|decreasePendingNumAllocation
parameter_list|()
block|{
comment|// Deduct pending #allocations by 1
name|ResourceSizing
name|sizing
init|=
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
decl_stmt|;
name|sizing
operator|.
name|setNumAllocations
argument_list|(
name|sizing
operator|.
name|getNumAllocations
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
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
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Per container scheduling request, it is just a copy of existing
comment|// scheduling request with #allocations=1
name|SchedulingRequest
name|containerSchedulingRequest
init|=
operator|new
name|SchedulingRequestPBImpl
argument_list|(
operator|(
operator|(
name|SchedulingRequestPBImpl
operator|)
name|schedulingRequest
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
decl_stmt|;
name|containerSchedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|setNumAllocations
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Deduct sizing
name|decreasePendingNumAllocation
argument_list|()
expr_stmt|;
return|return
operator|new
name|ContainerRequest
argument_list|(
name|containerSchedulingRequest
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
DECL|method|checkCardinalityAndPending (SchedulerNode node)
specifier|private
name|boolean
name|checkCardinalityAndPending
parameter_list|(
name|SchedulerNode
name|node
parameter_list|)
block|{
comment|// Do we still have pending resource?
if|if
condition|(
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// node type will be ignored.
try|try
block|{
return|return
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appSchedulingInfo
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|schedulingRequest
argument_list|,
name|node
argument_list|,
name|placementConstraintManager
argument_list|,
name|allocationTagsManager
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidAllocationTagsQueryException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to query node cardinality:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
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
return|return
name|checkCardinalityAndPending
argument_list|(
name|node
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
DECL|method|canDelayTo (String resourceName)
specifier|public
name|boolean
name|canDelayTo
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|precheckNode (SchedulerNode schedulerNode, SchedulingMode schedulingMode)
specifier|public
name|boolean
name|precheckNode
parameter_list|(
name|SchedulerNode
name|schedulerNode
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
name|schedulerNode
operator|.
name|getPartition
argument_list|()
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
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Check node partition as well as cardinality/pending resources.
return|return
name|this
operator|.
name|targetNodePartition
operator|.
name|equals
argument_list|(
name|nodePartitionToLookAt
argument_list|)
operator|&&
name|checkCardinalityAndPending
argument_list|(
name|schedulerNode
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
DECL|method|getPrimaryRequestedNodePartition ()
specifier|public
name|String
name|getPrimaryRequestedNodePartition
parameter_list|()
block|{
return|return
name|targetNodePartition
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
literal|1
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
try|try
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|schedulingRequest
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|schedulingRequest
operator|.
name|toString
argument_list|()
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
DECL|method|getSchedulingRequest ()
specifier|public
name|SchedulingRequest
name|getSchedulingRequest
parameter_list|()
block|{
return|return
name|schedulingRequest
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getTargetNodePartition ()
name|String
name|getTargetNodePartition
parameter_list|()
block|{
return|return
name|targetNodePartition
return|;
block|}
annotation|@
name|Override
DECL|method|initialize (AppSchedulingInfo appSchedulingInfo, SchedulerRequestKey schedulerRequestKey, RMContext rmContext)
specifier|public
name|void
name|initialize
parameter_list|(
name|AppSchedulingInfo
name|appSchedulingInfo
parameter_list|,
name|SchedulerRequestKey
name|schedulerRequestKey
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|super
operator|.
name|initialize
argument_list|(
name|appSchedulingInfo
argument_list|,
name|schedulerRequestKey
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|allocationTagsManager
operator|=
name|rmContext
operator|.
name|getAllocationTagsManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|placementConstraintManager
operator|=
name|rmContext
operator|.
name|getPlacementConstraintManager
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

