begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint.algorithm
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
name|constraint
operator|.
name|algorithm
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
name|HashMap
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
name|stream
operator|.
name|Collectors
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
name|ApplicationId
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
name|NodeId
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
name|scheduler
operator|.
name|AbstractYarnScheduler
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|constraint
operator|.
name|api
operator|.
name|ConstraintPlacementAlgorithm
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
name|api
operator|.
name|ConstraintPlacementAlgorithmInput
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
name|api
operator|.
name|ConstraintPlacementAlgorithmOutput
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
name|api
operator|.
name|ConstraintPlacementAlgorithmOutputCollector
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
name|api
operator|.
name|PlacedSchedulingRequest
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
name|api
operator|.
name|SchedulingRequestWithPlacementAttempt
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
name|processor
operator|.
name|BatchedRequests
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
name|processor
operator|.
name|NodeCandidateSelector
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

begin_comment
comment|/**  * Basic placement algorithm.  * Supports different Iterators at SchedulingRequest level including:  * Serial, PopularTags  */
end_comment

begin_class
DECL|class|DefaultPlacementAlgorithm
specifier|public
class|class
name|DefaultPlacementAlgorithm
implements|implements
name|ConstraintPlacementAlgorithm
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
name|DefaultPlacementAlgorithm
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Number of times to re-attempt placing a single scheduling request.
DECL|field|RE_ATTEMPT_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|RE_ATTEMPT_COUNT
init|=
literal|2
decl_stmt|;
DECL|field|tagsManager
specifier|private
name|LocalAllocationTagsManager
name|tagsManager
decl_stmt|;
DECL|field|constraintManager
specifier|private
name|PlacementConstraintManager
name|constraintManager
decl_stmt|;
DECL|field|nodeSelector
specifier|private
name|NodeCandidateSelector
name|nodeSelector
decl_stmt|;
DECL|field|resourceCalculator
specifier|private
name|ResourceCalculator
name|resourceCalculator
decl_stmt|;
annotation|@
name|Override
DECL|method|init (RMContext rmContext)
specifier|public
name|void
name|init
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|tagsManager
operator|=
operator|new
name|LocalAllocationTagsManager
argument_list|(
name|rmContext
operator|.
name|getAllocationTagsManager
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|constraintManager
operator|=
name|rmContext
operator|.
name|getPlacementConstraintManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|resourceCalculator
operator|=
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getResourceCalculator
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeSelector
operator|=
name|filter
lambda|->
operator|(
call|(
name|AbstractYarnScheduler
call|)
argument_list|(
name|rmContext
argument_list|)
operator|.
name|getScheduler
argument_list|()
operator|)
operator|.
name|getNodes
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
DECL|method|attemptPlacementOnNode (ApplicationId appId, Resource availableResources, SchedulingRequest schedulingRequest, SchedulerNode schedulerNode, boolean ignoreResourceCheck)
name|boolean
name|attemptPlacementOnNode
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Resource
name|availableResources
parameter_list|,
name|SchedulingRequest
name|schedulingRequest
parameter_list|,
name|SchedulerNode
name|schedulerNode
parameter_list|,
name|boolean
name|ignoreResourceCheck
parameter_list|)
throws|throws
name|InvalidAllocationTagsQueryException
block|{
name|boolean
name|fitsInNode
init|=
name|ignoreResourceCheck
operator|||
name|Resources
operator|.
name|fitsIn
argument_list|(
name|resourceCalculator
argument_list|,
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getResources
argument_list|()
argument_list|,
name|availableResources
argument_list|)
decl_stmt|;
name|boolean
name|constraintsSatisfied
init|=
name|PlacementConstraintsUtil
operator|.
name|canSatisfyConstraints
argument_list|(
name|appId
argument_list|,
name|schedulingRequest
argument_list|,
name|schedulerNode
argument_list|,
name|constraintManager
argument_list|,
name|tagsManager
argument_list|)
decl_stmt|;
return|return
name|fitsInNode
operator|&&
name|constraintsSatisfied
return|;
block|}
annotation|@
name|Override
DECL|method|place (ConstraintPlacementAlgorithmInput input, ConstraintPlacementAlgorithmOutputCollector collector)
specifier|public
name|void
name|place
parameter_list|(
name|ConstraintPlacementAlgorithmInput
name|input
parameter_list|,
name|ConstraintPlacementAlgorithmOutputCollector
name|collector
parameter_list|)
block|{
name|BatchedRequests
name|requests
init|=
operator|(
name|BatchedRequests
operator|)
name|input
decl_stmt|;
name|int
name|placementAttempt
init|=
name|requests
operator|.
name|getPlacementAttempt
argument_list|()
decl_stmt|;
name|ConstraintPlacementAlgorithmOutput
name|resp
init|=
operator|new
name|ConstraintPlacementAlgorithmOutput
argument_list|(
name|requests
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SchedulerNode
argument_list|>
name|allNodes
init|=
name|nodeSelector
operator|.
name|selectNodes
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SchedulingRequest
argument_list|>
name|rejectedRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Resource
argument_list|>
name|availResources
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|rePlacementCount
init|=
name|RE_ATTEMPT_COUNT
decl_stmt|;
while|while
condition|(
name|rePlacementCount
operator|>
literal|0
condition|)
block|{
name|doPlacement
argument_list|(
name|requests
argument_list|,
name|resp
argument_list|,
name|allNodes
argument_list|,
name|rejectedRequests
argument_list|,
name|availResources
argument_list|)
expr_stmt|;
comment|// Double check if placement constraints are really satisfied
name|validatePlacement
argument_list|(
name|requests
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|resp
argument_list|,
name|rejectedRequests
argument_list|,
name|availResources
argument_list|)
expr_stmt|;
if|if
condition|(
name|rejectedRequests
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
name|rePlacementCount
operator|==
literal|1
condition|)
block|{
break|break;
block|}
name|requests
operator|=
operator|new
name|BatchedRequests
argument_list|(
name|requests
operator|.
name|getIteratorType
argument_list|()
argument_list|,
name|requests
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|rejectedRequests
argument_list|,
name|requests
operator|.
name|getPlacementAttempt
argument_list|()
argument_list|)
expr_stmt|;
name|rejectedRequests
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|rePlacementCount
operator|--
expr_stmt|;
block|}
name|resp
operator|.
name|getRejectedRequests
argument_list|()
operator|.
name|addAll
argument_list|(
name|rejectedRequests
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|x
lambda|->
operator|new
name|SchedulingRequestWithPlacementAttempt
argument_list|(
name|placementAttempt
argument_list|,
name|x
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
name|resp
argument_list|)
expr_stmt|;
comment|// Clean current temp-container tags
name|this
operator|.
name|tagsManager
operator|.
name|cleanTempContainers
argument_list|(
name|requests
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doPlacement (BatchedRequests requests, ConstraintPlacementAlgorithmOutput resp, List<SchedulerNode> allNodes, List<SchedulingRequest> rejectedRequests, Map<NodeId, Resource> availableResources)
specifier|private
name|void
name|doPlacement
parameter_list|(
name|BatchedRequests
name|requests
parameter_list|,
name|ConstraintPlacementAlgorithmOutput
name|resp
parameter_list|,
name|List
argument_list|<
name|SchedulerNode
argument_list|>
name|allNodes
parameter_list|,
name|List
argument_list|<
name|SchedulingRequest
argument_list|>
name|rejectedRequests
parameter_list|,
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Resource
argument_list|>
name|availableResources
parameter_list|)
block|{
name|Iterator
argument_list|<
name|SchedulingRequest
argument_list|>
name|requestIterator
init|=
name|requests
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|SchedulerNode
argument_list|>
name|nIter
init|=
name|allNodes
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|SchedulerNode
name|lastSatisfiedNode
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|requestIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|allNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No nodes available for placement at the moment !!"
argument_list|)
expr_stmt|;
break|break;
block|}
name|SchedulingRequest
name|schedulingRequest
init|=
name|requestIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|PlacedSchedulingRequest
name|placedReq
init|=
operator|new
name|PlacedSchedulingRequest
argument_list|(
name|schedulingRequest
argument_list|)
decl_stmt|;
name|placedReq
operator|.
name|setPlacementAttempt
argument_list|(
name|requests
operator|.
name|getPlacementAttempt
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|.
name|getPlacedRequests
argument_list|()
operator|.
name|add
argument_list|(
name|placedReq
argument_list|)
expr_stmt|;
name|CircularIterator
argument_list|<
name|SchedulerNode
argument_list|>
name|nodeIter
init|=
operator|new
name|CircularIterator
argument_list|(
name|lastSatisfiedNode
argument_list|,
name|nIter
argument_list|,
name|allNodes
argument_list|)
decl_stmt|;
name|int
name|numAllocs
init|=
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
decl_stmt|;
while|while
condition|(
name|nodeIter
operator|.
name|hasNext
argument_list|()
operator|&&
name|numAllocs
operator|>
literal|0
condition|)
block|{
name|SchedulerNode
name|node
init|=
name|nodeIter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|tag
init|=
name|schedulingRequest
operator|.
name|getAllocationTags
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|schedulingRequest
operator|.
name|getAllocationTags
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Resource
name|unallocatedResource
init|=
name|availableResources
operator|.
name|computeIfAbsent
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|x
lambda|->
name|Resource
operator|.
name|newInstance
argument_list|(
name|node
operator|.
name|getUnallocatedResource
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|requests
operator|.
name|getBlacklist
argument_list|(
name|tag
argument_list|)
operator|.
name|contains
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
operator|&&
name|attemptPlacementOnNode
argument_list|(
name|requests
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|unallocatedResource
argument_list|,
name|schedulingRequest
argument_list|,
name|node
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|setNumAllocations
argument_list|(
operator|--
name|numAllocs
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|unallocatedResource
argument_list|,
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getResources
argument_list|()
argument_list|)
expr_stmt|;
name|placedReq
operator|.
name|getNodes
argument_list|()
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|numAllocs
operator|=
name|schedulingRequest
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
expr_stmt|;
comment|// Add temp-container tags for current placement cycle
name|this
operator|.
name|tagsManager
operator|.
name|addTempTags
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|requests
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|schedulingRequest
operator|.
name|getAllocationTags
argument_list|()
argument_list|)
expr_stmt|;
name|lastSatisfiedNode
operator|=
name|node
expr_stmt|;
block|}
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
literal|"Got exception from TagManager !"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Add all requests whose numAllocations still> 0 to rejected list.
name|requests
operator|.
name|getSchedulingRequests
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|sReq
lambda|->
name|sReq
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
operator|>
literal|0
argument_list|)
operator|.
name|forEach
argument_list|(
name|rejReq
lambda|->
name|rejectedRequests
operator|.
name|add
argument_list|(
name|cloneReq
argument_list|(
name|rejReq
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * During the placement phase, allocation tags are added to the node if the    * constraint is satisfied, But depending on the order in which the    * algorithm sees the request, it is possible that a constraint that happened    * to be valid during placement of an earlier-seen request, might not be    * valid after all subsequent requests have been placed.    *    * For eg:    *   Assume nodes n1, n2, n3, n4 and n5    *    *   Consider the 2 constraints:    *   1) "foo", anti-affinity with "foo"    *   2) "bar", anti-affinity with "foo"    *    *   And 2 requests    *   req1: NumAllocations = 4, allocTags = [foo]    *   req2: NumAllocations = 1, allocTags = [bar]    *    *   If "req1" is seen first, the algorithm can place the 4 containers in    *   n1, n2, n3 and n4. And when it gets to "req2", it will see that 4 nodes    *   with the "foo" tag and will place on n5.    *   But if "req2" is seem first, then "bar" will be placed on any node,    *   since no node currently has "foo", and when it gets to "req1", since    *   "foo" has not anti-affinity with "bar", the algorithm can end up placing    *   "foo" on a node with "bar" violating the second constraint.    *    * To prevent the above, we need a validation step: after the placements for a    * batch of requests are made, for each req, we remove its tags from the node    * and try to see of constraints are still satisfied if the tag were to be    * added back on the node.    *    *   When applied to the example above, after "req2" and "req1" are placed,    *   we remove the "bar" tag from the node and try to add it back on the node.    *   This time, constraint satisfaction will fail, since there is now a "foo"    *   tag on the node and "bar" cannot be added. The algorithm will then    *   retry placing "req2" on another node.    *    * @param applicationId    * @param resp    * @param rejectedRequests    * @param availableResources    */
DECL|method|validatePlacement (ApplicationId applicationId, ConstraintPlacementAlgorithmOutput resp, List<SchedulingRequest> rejectedRequests, Map<NodeId, Resource> availableResources)
specifier|private
name|void
name|validatePlacement
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|ConstraintPlacementAlgorithmOutput
name|resp
parameter_list|,
name|List
argument_list|<
name|SchedulingRequest
argument_list|>
name|rejectedRequests
parameter_list|,
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Resource
argument_list|>
name|availableResources
parameter_list|)
block|{
name|Iterator
argument_list|<
name|PlacedSchedulingRequest
argument_list|>
name|pReqIter
init|=
name|resp
operator|.
name|getPlacedRequests
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|pReqIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PlacedSchedulingRequest
name|pReq
init|=
name|pReqIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|SchedulerNode
argument_list|>
name|nodeIter
init|=
name|pReq
operator|.
name|getNodes
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// Assuming all reqs were satisfied.
name|int
name|num
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|nodeIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SchedulerNode
name|node
init|=
name|nodeIter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Remove just the tags for this placement.
name|this
operator|.
name|tagsManager
operator|.
name|removeTempTags
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|applicationId
argument_list|,
name|pReq
operator|.
name|getSchedulingRequest
argument_list|()
operator|.
name|getAllocationTags
argument_list|()
argument_list|)
expr_stmt|;
name|Resource
name|availOnNode
init|=
name|availableResources
operator|.
name|get
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|attemptPlacementOnNode
argument_list|(
name|applicationId
argument_list|,
name|availOnNode
argument_list|,
name|pReq
operator|.
name|getSchedulingRequest
argument_list|()
argument_list|,
name|node
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|nodeIter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|num
operator|++
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|availOnNode
argument_list|,
name|pReq
operator|.
name|getSchedulingRequest
argument_list|()
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getResources
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Add back the tags if everything is fine.
name|this
operator|.
name|tagsManager
operator|.
name|addTempTags
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|applicationId
argument_list|,
name|pReq
operator|.
name|getSchedulingRequest
argument_list|()
operator|.
name|getAllocationTags
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
literal|"Got exception from TagManager !"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|num
operator|>
literal|0
condition|)
block|{
name|SchedulingRequest
name|sReq
init|=
name|cloneReq
argument_list|(
name|pReq
operator|.
name|getSchedulingRequest
argument_list|()
argument_list|)
decl_stmt|;
name|sReq
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|setNumAllocations
argument_list|(
name|num
argument_list|)
expr_stmt|;
name|rejectedRequests
operator|.
name|add
argument_list|(
name|sReq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pReq
operator|.
name|getNodes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pReqIter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|cloneReq (SchedulingRequest sReq)
specifier|private
specifier|static
name|SchedulingRequest
name|cloneReq
parameter_list|(
name|SchedulingRequest
name|sReq
parameter_list|)
block|{
return|return
name|SchedulingRequest
operator|.
name|newInstance
argument_list|(
name|sReq
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|,
name|sReq
operator|.
name|getPriority
argument_list|()
argument_list|,
name|sReq
operator|.
name|getExecutionType
argument_list|()
argument_list|,
name|sReq
operator|.
name|getAllocationTags
argument_list|()
argument_list|,
name|ResourceSizing
operator|.
name|newInstance
argument_list|(
name|sReq
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getNumAllocations
argument_list|()
argument_list|,
name|sReq
operator|.
name|getResourceSizing
argument_list|()
operator|.
name|getResources
argument_list|()
argument_list|)
argument_list|,
name|sReq
operator|.
name|getPlacementConstraint
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

