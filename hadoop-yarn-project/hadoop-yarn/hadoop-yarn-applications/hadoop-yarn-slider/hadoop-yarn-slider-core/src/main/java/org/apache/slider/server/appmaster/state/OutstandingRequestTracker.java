begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.state
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|Container
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
name|client
operator|.
name|api
operator|.
name|AMRMClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
operator|.
name|SliderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
operator|.
name|AbstractRMOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
operator|.
name|CancelSingleRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|operations
operator|.
name|ContainerRequestOperation
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
name|ArrayList
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
name|Comparator
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
name|HashSet
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
name|ListIterator
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

begin_comment
comment|/**  * Tracks outstanding requests made with a specific placement option.  *<p>  *<ol>  *<li>Used to decide when to return a node to 'can request containers here' list</li>  *<li>Used to identify requests where placement has timed out, and so issue relaxed requests</li>  *</ol>  *<p>  * If an allocation comes in that is not in the map: either the allocation  * was unplaced, or the placed allocation could not be met on the specified  * host, and the RM/scheduler fell back to another location.   */
end_comment

begin_class
DECL|class|OutstandingRequestTracker
specifier|public
class|class
name|OutstandingRequestTracker
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OutstandingRequestTracker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * no requests; saves creating a new list if not needed    */
DECL|field|NO_REQUESTS
specifier|private
specifier|final
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|NO_REQUESTS
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|placedRequests
specifier|private
name|Map
argument_list|<
name|RoleHostnamePair
argument_list|,
name|OutstandingRequest
argument_list|>
name|placedRequests
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * List of open requests; no specific details on them.    */
DECL|field|openRequests
specifier|private
name|List
argument_list|<
name|OutstandingRequest
argument_list|>
name|openRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Create a new request for the specific role.    *<p>    * If a location is set, the request is added to {@link #placedRequests}.    * If not, it is added to {@link #openRequests}    *<p>    * This does not update the node instance's role's request count    * @param instance node instance to manager    * @param role role index    * @return a new request    */
DECL|method|newRequest (NodeInstance instance, int role)
specifier|public
specifier|synchronized
name|OutstandingRequest
name|newRequest
parameter_list|(
name|NodeInstance
name|instance
parameter_list|,
name|int
name|role
parameter_list|)
block|{
name|OutstandingRequest
name|request
init|=
operator|new
name|OutstandingRequest
argument_list|(
name|role
argument_list|,
name|instance
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|isLocated
argument_list|()
condition|)
block|{
name|placedRequests
operator|.
name|put
argument_list|(
name|request
operator|.
name|getIndex
argument_list|()
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|openRequests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
return|return
name|request
return|;
block|}
comment|/**    * Create a new Anti-affine request for the specific role    *<p>    * It is added to {@link #openRequests}    *<p>    * This does not update the node instance's role's request count    * @param role role index    * @param nodes list of suitable nodes    * @param label label to use    * @return a new request    */
DECL|method|newAARequest (int role, List<NodeInstance> nodes, String label)
specifier|public
specifier|synchronized
name|OutstandingRequest
name|newAARequest
parameter_list|(
name|int
name|role
parameter_list|,
name|List
argument_list|<
name|NodeInstance
argument_list|>
name|nodes
parameter_list|,
name|String
name|label
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
operator|!
name|nodes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// safety check to verify the allocation will hold
for|for
control|(
name|NodeInstance
name|node
range|:
name|nodes
control|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|node
operator|.
name|canHost
argument_list|(
name|role
argument_list|,
name|label
argument_list|)
argument_list|,
literal|"Cannot allocate role ID %d to node %s"
argument_list|,
name|role
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
name|OutstandingRequest
name|request
init|=
operator|new
name|OutstandingRequest
argument_list|(
name|role
argument_list|,
name|nodes
argument_list|)
decl_stmt|;
name|openRequests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Look up any oustanding request to a (role, hostname).     * @param role role index    * @param hostname hostname    * @return the request or null if there was no outstanding one in the {@link #placedRequests}    */
annotation|@
name|VisibleForTesting
DECL|method|lookupPlacedRequest (int role, String hostname)
specifier|public
specifier|synchronized
name|OutstandingRequest
name|lookupPlacedRequest
parameter_list|(
name|int
name|role
parameter_list|,
name|String
name|hostname
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|hostname
operator|!=
literal|null
argument_list|,
literal|"null hostname"
argument_list|)
expr_stmt|;
return|return
name|placedRequests
operator|.
name|get
argument_list|(
operator|new
name|RoleHostnamePair
argument_list|(
name|role
argument_list|,
name|hostname
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Remove a request    * @param request matching request to find    * @return the request or null for no match in the {@link #placedRequests}    */
annotation|@
name|VisibleForTesting
DECL|method|removePlacedRequest (OutstandingRequest request)
specifier|public
specifier|synchronized
name|OutstandingRequest
name|removePlacedRequest
parameter_list|(
name|OutstandingRequest
name|request
parameter_list|)
block|{
return|return
name|placedRequests
operator|.
name|remove
argument_list|(
name|request
argument_list|)
return|;
block|}
comment|/**    * Notification that a container has been allocated    *    *<ol>    *<li>drop it from the {@link #placedRequests} structure.</li>    *<li>generate the cancellation request</li>    *<li>for AA placement, any actions needed</li>    *</ol>    *    * @param role role index    * @param hostname hostname    * @return the allocation outcome    */
DECL|method|onContainerAllocated (int role, String hostname, Container container)
specifier|public
specifier|synchronized
name|ContainerAllocationResults
name|onContainerAllocated
parameter_list|(
name|int
name|role
parameter_list|,
name|String
name|hostname
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
specifier|final
name|String
name|containerDetails
init|=
name|SliderUtils
operator|.
name|containerToString
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Processing allocation for role {}  on {}"
argument_list|,
name|role
argument_list|,
name|containerDetails
argument_list|)
expr_stmt|;
name|ContainerAllocationResults
name|allocation
init|=
operator|new
name|ContainerAllocationResults
argument_list|()
decl_stmt|;
name|ContainerAllocationOutcome
name|outcome
decl_stmt|;
name|OutstandingRequest
name|request
init|=
name|placedRequests
operator|.
name|remove
argument_list|(
operator|new
name|OutstandingRequest
argument_list|(
name|role
argument_list|,
name|hostname
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
block|{
comment|//satisfied request
name|log
operator|.
name|debug
argument_list|(
literal|"Found oustanding placed request for container: {}"
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|request
operator|.
name|completed
argument_list|()
expr_stmt|;
comment|// derive outcome from status of tracked request
name|outcome
operator|=
name|request
operator|.
name|isEscalated
argument_list|()
condition|?
name|ContainerAllocationOutcome
operator|.
name|Escalated
else|:
name|ContainerAllocationOutcome
operator|.
name|Placed
expr_stmt|;
block|}
else|else
block|{
comment|// not in the list; this is an open placement
comment|// scan through all containers in the open request list
name|request
operator|=
name|removeOpenRequest
argument_list|(
name|container
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Found open outstanding request for container: {}"
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|request
operator|.
name|completed
argument_list|()
expr_stmt|;
name|outcome
operator|=
name|ContainerAllocationOutcome
operator|.
name|Open
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No oustanding request found for container {}, outstanding queue has {} entries "
argument_list|,
name|containerDetails
argument_list|,
name|openRequests
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|outcome
operator|=
name|ContainerAllocationOutcome
operator|.
name|Unallocated
expr_stmt|;
block|}
block|}
if|if
condition|(
name|request
operator|!=
literal|null
operator|&&
name|request
operator|.
name|getIssuedRequest
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|allocation
operator|.
name|operations
operator|.
name|add
argument_list|(
name|request
operator|.
name|createCancelOperation
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// there's a request, but no idea what to cancel.
comment|// rather than try to recover from it inelegantly, (and cause more confusion),
comment|// log the event, but otherwise continue
name|log
operator|.
name|warn
argument_list|(
literal|"Unexpected allocation of container "
operator|+
name|SliderUtils
operator|.
name|containerToString
argument_list|(
name|container
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|allocation
operator|.
name|origin
operator|=
name|request
expr_stmt|;
name|allocation
operator|.
name|outcome
operator|=
name|outcome
expr_stmt|;
return|return
name|allocation
return|;
block|}
comment|/**    * Find and remove an open request. Determine it by scanning open requests    * for one whose priority& resource requirements match that of the container    * allocated.    * @param container container allocated    * @return a request which matches the allocation, or null for "no match"    */
DECL|method|removeOpenRequest (Container container)
specifier|private
name|OutstandingRequest
name|removeOpenRequest
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
name|int
name|pri
init|=
name|container
operator|.
name|getPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
decl_stmt|;
name|Resource
name|resource
init|=
name|container
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|OutstandingRequest
name|request
init|=
literal|null
decl_stmt|;
name|ListIterator
argument_list|<
name|OutstandingRequest
argument_list|>
name|openlist
init|=
name|openRequests
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|openlist
operator|.
name|hasNext
argument_list|()
operator|&&
name|request
operator|==
literal|null
condition|)
block|{
name|OutstandingRequest
name|r
init|=
name|openlist
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|getPriority
argument_list|()
operator|==
name|pri
condition|)
block|{
comment|// matching resource
if|if
condition|(
name|r
operator|.
name|resourceRequirementsMatch
argument_list|(
name|resource
argument_list|)
condition|)
block|{
comment|// match of priority and resources
name|request
operator|=
name|r
expr_stmt|;
name|openlist
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Matched priorities but resources different"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|request
return|;
block|}
comment|/**    * Determine which host was a role type most recently used on, so that    * if a choice is made of which (potentially surplus) containers to use,    * the most recent one is picked first. This operation<i>does not</i>    * change the role history, though it queries it.    */
DECL|class|newerThan
specifier|static
class|class
name|newerThan
implements|implements
name|Comparator
argument_list|<
name|Container
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|rh
specifier|private
name|RoleHistory
name|rh
decl_stmt|;
DECL|method|newerThan (RoleHistory rh)
specifier|public
name|newerThan
parameter_list|(
name|RoleHistory
name|rh
parameter_list|)
block|{
name|this
operator|.
name|rh
operator|=
name|rh
expr_stmt|;
block|}
comment|/**      * Get the age of a node hosting container. If it is not known in the history,       * return 0.      * @param c container      * @return age, null if there's no entry for it.       */
DECL|method|getAgeOf (Container c)
specifier|private
name|long
name|getAgeOf
parameter_list|(
name|Container
name|c
parameter_list|)
block|{
name|long
name|age
init|=
literal|0
decl_stmt|;
name|NodeInstance
name|node
init|=
name|rh
operator|.
name|getExistingNodeInstance
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|int
name|role
init|=
name|ContainerPriority
operator|.
name|extractRole
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|NodeEntry
name|nodeEntry
init|=
name|node
operator|.
name|get
argument_list|(
name|role
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeEntry
operator|!=
literal|null
condition|)
block|{
name|age
operator|=
name|nodeEntry
operator|.
name|getLastUsed
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|age
return|;
block|}
comment|/**      * Comparator: which host is more recent?      * @param c1 container 1      * @param c2 container 2      * @return 1 if c2 older-than c1, 0 if equal; -1 if c1 older-than c2      */
annotation|@
name|Override
DECL|method|compare (Container c1, Container c2)
specifier|public
name|int
name|compare
parameter_list|(
name|Container
name|c1
parameter_list|,
name|Container
name|c2
parameter_list|)
block|{
name|int
name|role1
init|=
name|ContainerPriority
operator|.
name|extractRole
argument_list|(
name|c1
argument_list|)
decl_stmt|;
name|int
name|role2
init|=
name|ContainerPriority
operator|.
name|extractRole
argument_list|(
name|c2
argument_list|)
decl_stmt|;
if|if
condition|(
name|role1
operator|<
name|role2
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|role1
operator|>
name|role2
condition|)
return|return
literal|1
return|;
name|long
name|age
init|=
name|getAgeOf
argument_list|(
name|c1
argument_list|)
decl_stmt|;
name|long
name|age2
init|=
name|getAgeOf
argument_list|(
name|c2
argument_list|)
decl_stmt|;
if|if
condition|(
name|age
operator|>
name|age2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|age
operator|<
name|age2
condition|)
block|{
return|return
literal|1
return|;
block|}
comment|// equal
return|return
literal|0
return|;
block|}
block|}
comment|/**    * Take a list of requests and split them into specific host requests and    * generic assignments. This is to give requested hosts priority    * in container assignments if more come back than expected    * @param rh RoleHistory instance    * @param inAllocated the list of allocated containers    * @param outPlaceRequested initially empty list of requested locations     * @param outUnplaced initially empty list of unrequested hosts    */
DECL|method|partitionRequests (RoleHistory rh, List<Container> inAllocated, List<Container> outPlaceRequested, List<Container> outUnplaced)
specifier|public
specifier|synchronized
name|void
name|partitionRequests
parameter_list|(
name|RoleHistory
name|rh
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|inAllocated
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|outPlaceRequested
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|outUnplaced
parameter_list|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|inAllocated
argument_list|,
operator|new
name|newerThan
argument_list|(
name|rh
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Container
name|container
range|:
name|inAllocated
control|)
block|{
name|int
name|role
init|=
name|ContainerPriority
operator|.
name|extractRole
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|String
name|hostname
init|=
name|RoleHistoryUtils
operator|.
name|hostnameOf
argument_list|(
name|container
argument_list|)
decl_stmt|;
if|if
condition|(
name|placedRequests
operator|.
name|containsKey
argument_list|(
operator|new
name|OutstandingRequest
argument_list|(
name|role
argument_list|,
name|hostname
argument_list|)
argument_list|)
condition|)
block|{
name|outPlaceRequested
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outUnplaced
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Reset list all outstanding requests for a role: return the hostnames    * of any canceled requests    *    * @param role role to cancel    * @return possibly empty list of hostnames    */
DECL|method|resetOutstandingRequests (int role)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|NodeInstance
argument_list|>
name|resetOutstandingRequests
parameter_list|(
name|int
name|role
parameter_list|)
block|{
name|List
argument_list|<
name|NodeInstance
argument_list|>
name|hosts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|RoleHostnamePair
argument_list|,
name|OutstandingRequest
argument_list|>
argument_list|>
name|iterator
init|=
name|placedRequests
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|RoleHostnamePair
argument_list|,
name|OutstandingRequest
argument_list|>
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|OutstandingRequest
name|request
init|=
name|next
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|roleId
operator|==
name|role
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|request
operator|.
name|completed
argument_list|()
expr_stmt|;
name|hosts
operator|.
name|add
argument_list|(
name|request
operator|.
name|node
argument_list|)
expr_stmt|;
block|}
block|}
name|ListIterator
argument_list|<
name|OutstandingRequest
argument_list|>
name|openlist
init|=
name|openRequests
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|openlist
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|OutstandingRequest
name|next
init|=
name|openlist
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|roleId
operator|==
name|role
condition|)
block|{
name|openlist
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|hosts
return|;
block|}
comment|/**    * Get a list of outstanding requests. The list is cloned, but the contents    * are shared    * @return a list of the current outstanding requests    */
DECL|method|listPlacedRequests ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|OutstandingRequest
argument_list|>
name|listPlacedRequests
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|placedRequests
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get a list of outstanding requests. The list is cloned, but the contents    * are shared    * @return a list of the current outstanding requests    */
DECL|method|listOpenRequests ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|OutstandingRequest
argument_list|>
name|listOpenRequests
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|openRequests
argument_list|)
return|;
block|}
comment|/**    * Escalate operation as triggered by external timer.    * @return a (usually empty) list of cancel/request operations.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SynchronizationOnLocalVariableOrMethodParameter"
argument_list|)
DECL|method|escalateOutstandingRequests (long now)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|escalateOutstandingRequests
parameter_list|(
name|long
name|now
parameter_list|)
block|{
if|if
condition|(
name|placedRequests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|NO_REQUESTS
return|;
block|}
name|List
argument_list|<
name|AbstractRMOperation
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
name|OutstandingRequest
name|outstandingRequest
range|:
name|placedRequests
operator|.
name|values
argument_list|()
control|)
block|{
synchronized|synchronized
init|(
name|outstandingRequest
init|)
block|{
comment|// sync escalation check with operation so that nothing can happen to state
comment|// of the request during the escalation
if|if
condition|(
name|outstandingRequest
operator|.
name|shouldEscalate
argument_list|(
name|now
argument_list|)
condition|)
block|{
comment|// time to escalate
name|CancelSingleRequest
name|cancel
init|=
name|outstandingRequest
operator|.
name|createCancelOperation
argument_list|()
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
name|cancel
argument_list|)
expr_stmt|;
name|AMRMClient
operator|.
name|ContainerRequest
name|escalated
init|=
name|outstandingRequest
operator|.
name|escalate
argument_list|()
decl_stmt|;
name|operations
operator|.
name|add
argument_list|(
operator|new
name|ContainerRequestOperation
argument_list|(
name|escalated
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|operations
return|;
block|}
comment|/**    * Cancel all outstanding AA requests from the lists of requests.    *    * This does not remove them from the role status; they must be reset    * by the caller.    *    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SynchronizationOnLocalVariableOrMethodParameter"
argument_list|)
DECL|method|cancelOutstandingAARequests ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|cancelOutstandingAARequests
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Looking for AA request to cancel"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AbstractRMOperation
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// first, all placed requests
name|List
argument_list|<
name|RoleHostnamePair
argument_list|>
name|requestsToRemove
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|placedRequests
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RoleHostnamePair
argument_list|,
name|OutstandingRequest
argument_list|>
name|entry
range|:
name|placedRequests
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|OutstandingRequest
name|outstandingRequest
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|outstandingRequest
init|)
block|{
if|if
condition|(
name|outstandingRequest
operator|.
name|isAntiAffine
argument_list|()
condition|)
block|{
comment|// time to escalate
name|operations
operator|.
name|add
argument_list|(
name|outstandingRequest
operator|.
name|createCancelOperation
argument_list|()
argument_list|)
expr_stmt|;
name|requestsToRemove
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|RoleHostnamePair
name|keys
range|:
name|requestsToRemove
control|)
block|{
name|placedRequests
operator|.
name|remove
argument_list|(
name|keys
argument_list|)
expr_stmt|;
block|}
comment|// second, all open requests
name|ListIterator
argument_list|<
name|OutstandingRequest
argument_list|>
name|orit
init|=
name|openRequests
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|orit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|OutstandingRequest
name|outstandingRequest
init|=
name|orit
operator|.
name|next
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|outstandingRequest
init|)
block|{
if|if
condition|(
name|outstandingRequest
operator|.
name|isAntiAffine
argument_list|()
condition|)
block|{
comment|// time to escalate
name|operations
operator|.
name|add
argument_list|(
name|outstandingRequest
operator|.
name|createCancelOperation
argument_list|()
argument_list|)
expr_stmt|;
name|orit
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Cancelling {} outstanding AA requests"
argument_list|,
name|operations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|operations
return|;
block|}
comment|/**    * Extract a specific number of open requests for a role    * @param roleId role Id    * @param count count to extract    * @return a list of requests which are no longer in the open request list    */
DECL|method|extractOpenRequestsForRole (int roleId, int count)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|OutstandingRequest
argument_list|>
name|extractOpenRequestsForRole
parameter_list|(
name|int
name|roleId
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|List
argument_list|<
name|OutstandingRequest
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ListIterator
argument_list|<
name|OutstandingRequest
argument_list|>
name|openlist
init|=
name|openRequests
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|openlist
operator|.
name|hasNext
argument_list|()
operator|&&
name|count
operator|>
literal|0
condition|)
block|{
name|OutstandingRequest
name|openRequest
init|=
name|openlist
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|openRequest
operator|.
name|roleId
operator|==
name|roleId
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|openRequest
argument_list|)
expr_stmt|;
name|openlist
operator|.
name|remove
argument_list|()
expr_stmt|;
name|count
operator|--
expr_stmt|;
block|}
block|}
return|return
name|results
return|;
block|}
comment|/**    * Extract a specific number of placed requests for a role    * @param roleId role Id    * @param count count to extract    * @return a list of requests which are no longer in the placed request data structure    */
DECL|method|extractPlacedRequestsForRole (int roleId, int count)
specifier|public
specifier|synchronized
name|List
argument_list|<
name|OutstandingRequest
argument_list|>
name|extractPlacedRequestsForRole
parameter_list|(
name|int
name|roleId
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|List
argument_list|<
name|OutstandingRequest
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|RoleHostnamePair
argument_list|,
name|OutstandingRequest
argument_list|>
argument_list|>
name|iterator
init|=
name|placedRequests
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|count
operator|>
literal|0
condition|)
block|{
name|OutstandingRequest
name|request
init|=
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|roleId
operator|==
name|roleId
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|count
operator|--
expr_stmt|;
block|}
block|}
comment|// now cull them from the map
for|for
control|(
name|OutstandingRequest
name|result
range|:
name|results
control|)
block|{
name|placedRequests
operator|.
name|remove
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
block|}
end_class

end_unit

