begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.scheduler
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
name|scheduler
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
name|net
operator|.
name|NetUtils
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
name|SecurityUtil
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
name|ContainerId
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
name|ResourceBlacklistRequest
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
name|Token
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
name|YarnException
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
name|security
operator|.
name|ContainerTokenIdentifier
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
name|api
operator|.
name|ContainerType
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
name|api
operator|.
name|protocolrecords
operator|.
name|RemoteNode
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
name|security
operator|.
name|BaseContainerTokenSecretManager
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
name|utils
operator|.
name|BuilderUtils
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
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|HashMap
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
name|Map
operator|.
name|Entry
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  *<p>  * The OpportunisticContainerAllocator allocates containers on a given list of  * nodes, after modifying the container sizes to respect the limits set by the  * ResourceManager. It tries to distribute the containers as evenly as possible.  *</p>  */
end_comment

begin_class
DECL|class|OpportunisticContainerAllocator
specifier|public
class|class
name|OpportunisticContainerAllocator
block|{
comment|/**    * This class encapsulates application specific parameters used to build a    * Container.    */
DECL|class|AllocationParams
specifier|public
specifier|static
class|class
name|AllocationParams
block|{
DECL|field|maxResource
specifier|private
name|Resource
name|maxResource
decl_stmt|;
DECL|field|minResource
specifier|private
name|Resource
name|minResource
decl_stmt|;
DECL|field|incrementResource
specifier|private
name|Resource
name|incrementResource
decl_stmt|;
DECL|field|containerTokenExpiryInterval
specifier|private
name|int
name|containerTokenExpiryInterval
decl_stmt|;
comment|/**      * Return Max Resource.      * @return Resource      */
DECL|method|getMaxResource ()
specifier|public
name|Resource
name|getMaxResource
parameter_list|()
block|{
return|return
name|maxResource
return|;
block|}
comment|/**      * Set Max Resource.      * @param maxResource Resource      */
DECL|method|setMaxResource (Resource maxResource)
specifier|public
name|void
name|setMaxResource
parameter_list|(
name|Resource
name|maxResource
parameter_list|)
block|{
name|this
operator|.
name|maxResource
operator|=
name|maxResource
expr_stmt|;
block|}
comment|/**      * Get Min Resource.      * @return Resource      */
DECL|method|getMinResource ()
specifier|public
name|Resource
name|getMinResource
parameter_list|()
block|{
return|return
name|minResource
return|;
block|}
comment|/**      * Set Min Resource.      * @param minResource Resource      */
DECL|method|setMinResource (Resource minResource)
specifier|public
name|void
name|setMinResource
parameter_list|(
name|Resource
name|minResource
parameter_list|)
block|{
name|this
operator|.
name|minResource
operator|=
name|minResource
expr_stmt|;
block|}
comment|/**      * Get Incremental Resource.      * @return Incremental Resource      */
DECL|method|getIncrementResource ()
specifier|public
name|Resource
name|getIncrementResource
parameter_list|()
block|{
return|return
name|incrementResource
return|;
block|}
comment|/**      * Set Incremental resource.      * @param incrementResource Resource      */
DECL|method|setIncrementResource (Resource incrementResource)
specifier|public
name|void
name|setIncrementResource
parameter_list|(
name|Resource
name|incrementResource
parameter_list|)
block|{
name|this
operator|.
name|incrementResource
operator|=
name|incrementResource
expr_stmt|;
block|}
comment|/**      * Get Container Token Expiry interval.      * @return Container Token Expiry interval      */
DECL|method|getContainerTokenExpiryInterval ()
specifier|public
name|int
name|getContainerTokenExpiryInterval
parameter_list|()
block|{
return|return
name|containerTokenExpiryInterval
return|;
block|}
comment|/**      * Set Container Token Expiry time in ms.      * @param containerTokenExpiryInterval Container Token Expiry in ms      */
DECL|method|setContainerTokenExpiryInterval ( int containerTokenExpiryInterval)
specifier|public
name|void
name|setContainerTokenExpiryInterval
parameter_list|(
name|int
name|containerTokenExpiryInterval
parameter_list|)
block|{
name|this
operator|.
name|containerTokenExpiryInterval
operator|=
name|containerTokenExpiryInterval
expr_stmt|;
block|}
block|}
comment|/**    * A Container Id Generator.    */
DECL|class|ContainerIdGenerator
specifier|public
specifier|static
class|class
name|ContainerIdGenerator
block|{
DECL|field|containerIdCounter
specifier|protected
specifier|volatile
name|AtomicLong
name|containerIdCounter
init|=
operator|new
name|AtomicLong
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**      * This method can reset the generator to a specific value.      * @param containerIdStart containerId      */
DECL|method|resetContainerIdCounter (long containerIdStart)
specifier|public
name|void
name|resetContainerIdCounter
parameter_list|(
name|long
name|containerIdStart
parameter_list|)
block|{
name|this
operator|.
name|containerIdCounter
operator|.
name|set
argument_list|(
name|containerIdStart
argument_list|)
expr_stmt|;
block|}
comment|/**      * Generates a new long value. Default implementation increments the      * underlying AtomicLong. Sub classes are encouraged to over-ride this      * behaviour.      * @return Counter.      */
DECL|method|generateContainerId ()
specifier|public
name|long
name|generateContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerIdCounter
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
block|}
comment|/**    * Class that includes two lists of {@link ResourceRequest}s: one for    * GUARANTEED and one for OPPORTUNISTIC {@link ResourceRequest}s.    */
DECL|class|PartitionedResourceRequests
specifier|public
specifier|static
class|class
name|PartitionedResourceRequests
block|{
DECL|field|guaranteed
specifier|private
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|guaranteed
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|opportunistic
specifier|private
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|opportunistic
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|getGuaranteed ()
specifier|public
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getGuaranteed
parameter_list|()
block|{
return|return
name|guaranteed
return|;
block|}
DECL|method|getOpportunistic ()
specifier|public
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getOpportunistic
parameter_list|()
block|{
return|return
name|opportunistic
return|;
block|}
block|}
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
name|OpportunisticContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RESOURCE_CALCULATOR
specifier|private
specifier|static
specifier|final
name|ResourceCalculator
name|RESOURCE_CALCULATOR
init|=
operator|new
name|DominantResourceCalculator
argument_list|()
decl_stmt|;
DECL|field|tokenSecretManager
specifier|private
specifier|final
name|BaseContainerTokenSecretManager
name|tokenSecretManager
decl_stmt|;
comment|/**    * Create a new Opportunistic Container Allocator.    * @param tokenSecretManager TokenSecretManager    */
DECL|method|OpportunisticContainerAllocator ( BaseContainerTokenSecretManager tokenSecretManager)
specifier|public
name|OpportunisticContainerAllocator
parameter_list|(
name|BaseContainerTokenSecretManager
name|tokenSecretManager
parameter_list|)
block|{
name|this
operator|.
name|tokenSecretManager
operator|=
name|tokenSecretManager
expr_stmt|;
block|}
comment|/**    * Allocate OPPORTUNISTIC containers.    * @param blackList Resource BlackList Request    * @param oppResourceReqs Opportunistic Resource Requests    * @param applicationAttemptId ApplicationAttemptId    * @param opportContext App specific OpportunisticContainerContext    * @param rmIdentifier RM Identifier    * @param appSubmitter App Submitter    * @return List of Containers.    * @throws YarnException YarnException    */
DECL|method|allocateContainers (ResourceBlacklistRequest blackList, List<ResourceRequest> oppResourceReqs, ApplicationAttemptId applicationAttemptId, OpportunisticContainerContext opportContext, long rmIdentifier, String appSubmitter)
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|allocateContainers
parameter_list|(
name|ResourceBlacklistRequest
name|blackList
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|oppResourceReqs
parameter_list|,
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|OpportunisticContainerContext
name|opportContext
parameter_list|,
name|long
name|rmIdentifier
parameter_list|,
name|String
name|appSubmitter
parameter_list|)
throws|throws
name|YarnException
block|{
comment|// Update black list.
if|if
condition|(
name|blackList
operator|!=
literal|null
condition|)
block|{
name|opportContext
operator|.
name|getBlacklist
argument_list|()
operator|.
name|removeAll
argument_list|(
name|blackList
operator|.
name|getBlacklistRemovals
argument_list|()
argument_list|)
expr_stmt|;
name|opportContext
operator|.
name|getBlacklist
argument_list|()
operator|.
name|addAll
argument_list|(
name|blackList
operator|.
name|getBlacklistAdditions
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Add OPPORTUNISTIC requests to the outstanding ones.
name|opportContext
operator|.
name|addToOutstandingReqs
argument_list|(
name|oppResourceReqs
argument_list|)
expr_stmt|;
comment|// Satisfy the outstanding OPPORTUNISTIC requests.
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SchedulerRequestKey
name|schedulerKey
range|:
name|opportContext
operator|.
name|getOutstandingOpReqs
argument_list|()
operator|.
name|descendingKeySet
argument_list|()
control|)
block|{
comment|// Allocated containers :
comment|//  Key = Requested Capability,
comment|//  Value = List of Containers of given cap (the actual container size
comment|//          might be different than what is requested, which is why
comment|//          we need the requested capability (key) to match against
comment|//          the outstanding reqs)
name|Map
argument_list|<
name|Resource
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|allocated
init|=
name|allocate
argument_list|(
name|rmIdentifier
argument_list|,
name|opportContext
argument_list|,
name|schedulerKey
argument_list|,
name|applicationAttemptId
argument_list|,
name|appSubmitter
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Resource
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|e
range|:
name|allocated
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|opportContext
operator|.
name|matchAllocationToOutstandingRequest
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|allocatedContainers
operator|.
name|addAll
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|allocatedContainers
return|;
block|}
DECL|method|allocate (long rmIdentifier, OpportunisticContainerContext appContext, SchedulerRequestKey schedKey, ApplicationAttemptId appAttId, String userName)
specifier|private
name|Map
argument_list|<
name|Resource
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|allocate
parameter_list|(
name|long
name|rmIdentifier
parameter_list|,
name|OpportunisticContainerContext
name|appContext
parameter_list|,
name|SchedulerRequestKey
name|schedKey
parameter_list|,
name|ApplicationAttemptId
name|appAttId
parameter_list|,
name|String
name|userName
parameter_list|)
throws|throws
name|YarnException
block|{
name|Map
argument_list|<
name|Resource
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|containers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ResourceRequest
name|anyAsk
range|:
name|appContext
operator|.
name|getOutstandingOpReqs
argument_list|()
operator|.
name|get
argument_list|(
name|schedKey
argument_list|)
operator|.
name|values
argument_list|()
control|)
block|{
name|allocateContainersInternal
argument_list|(
name|rmIdentifier
argument_list|,
name|appContext
operator|.
name|getAppParams
argument_list|()
argument_list|,
name|appContext
operator|.
name|getContainerIdGenerator
argument_list|()
argument_list|,
name|appContext
operator|.
name|getBlacklist
argument_list|()
argument_list|,
name|appAttId
argument_list|,
name|appContext
operator|.
name|getNodeMap
argument_list|()
argument_list|,
name|userName
argument_list|,
name|containers
argument_list|,
name|anyAsk
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|containers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Opportunistic allocation requested for ["
operator|+
literal|"priority="
operator|+
name|anyAsk
operator|.
name|getPriority
argument_list|()
operator|+
literal|", allocationRequestId="
operator|+
name|anyAsk
operator|.
name|getAllocationRequestId
argument_list|()
operator|+
literal|", num_containers="
operator|+
name|anyAsk
operator|.
name|getNumContainers
argument_list|()
operator|+
literal|", capability="
operator|+
name|anyAsk
operator|.
name|getCapability
argument_list|()
operator|+
literal|"]"
operator|+
literal|" allocated = "
operator|+
name|containers
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|containers
return|;
block|}
DECL|method|allocateContainersInternal (long rmIdentifier, AllocationParams appParams, ContainerIdGenerator idCounter, Set<String> blacklist, ApplicationAttemptId id, Map<String, RemoteNode> allNodes, String userName, Map<Resource, List<Container>> containers, ResourceRequest anyAsk)
specifier|private
name|void
name|allocateContainersInternal
parameter_list|(
name|long
name|rmIdentifier
parameter_list|,
name|AllocationParams
name|appParams
parameter_list|,
name|ContainerIdGenerator
name|idCounter
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|blacklist
parameter_list|,
name|ApplicationAttemptId
name|id
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RemoteNode
argument_list|>
name|allNodes
parameter_list|,
name|String
name|userName
parameter_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|containers
parameter_list|,
name|ResourceRequest
name|anyAsk
parameter_list|)
throws|throws
name|YarnException
block|{
name|int
name|toAllocate
init|=
name|anyAsk
operator|.
name|getNumContainers
argument_list|()
operator|-
operator|(
name|containers
operator|.
name|isEmpty
argument_list|()
condition|?
literal|0
else|:
name|containers
operator|.
name|get
argument_list|(
name|anyAsk
operator|.
name|getCapability
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
operator|)
decl_stmt|;
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|nodesForScheduling
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|RemoteNode
argument_list|>
name|nodeEntry
range|:
name|allNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// Do not use blacklisted nodes for scheduling.
if|if
condition|(
name|blacklist
operator|.
name|contains
argument_list|(
name|nodeEntry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|nodesForScheduling
operator|.
name|add
argument_list|(
name|nodeEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodesForScheduling
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No nodes available for allocating opportunistic containers. ["
operator|+
literal|"allNodes="
operator|+
name|allNodes
operator|+
literal|", "
operator|+
literal|"blacklist="
operator|+
name|blacklist
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|numAllocated
init|=
literal|0
decl_stmt|;
name|int
name|nextNodeToSchedule
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|numCont
init|=
literal|0
init|;
name|numCont
operator|<
name|toAllocate
condition|;
name|numCont
operator|++
control|)
block|{
name|nextNodeToSchedule
operator|++
expr_stmt|;
name|nextNodeToSchedule
operator|%=
name|nodesForScheduling
operator|.
name|size
argument_list|()
expr_stmt|;
name|RemoteNode
name|node
init|=
name|nodesForScheduling
operator|.
name|get
argument_list|(
name|nextNodeToSchedule
argument_list|)
decl_stmt|;
name|Container
name|container
init|=
name|buildContainer
argument_list|(
name|rmIdentifier
argument_list|,
name|appParams
argument_list|,
name|idCounter
argument_list|,
name|anyAsk
argument_list|,
name|id
argument_list|,
name|userName
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|cList
init|=
name|containers
operator|.
name|get
argument_list|(
name|anyAsk
operator|.
name|getCapability
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cList
operator|==
literal|null
condition|)
block|{
name|cList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|containers
operator|.
name|put
argument_list|(
name|anyAsk
operator|.
name|getCapability
argument_list|()
argument_list|,
name|cList
argument_list|)
expr_stmt|;
block|}
name|cList
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|numAllocated
operator|++
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allocated ["
operator|+
name|container
operator|.
name|getId
argument_list|()
operator|+
literal|"] as opportunistic."
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Allocated "
operator|+
name|numAllocated
operator|+
literal|" opportunistic containers."
argument_list|)
expr_stmt|;
block|}
DECL|method|buildContainer (long rmIdentifier, AllocationParams appParams, ContainerIdGenerator idCounter, ResourceRequest rr, ApplicationAttemptId id, String userName, RemoteNode node)
specifier|private
name|Container
name|buildContainer
parameter_list|(
name|long
name|rmIdentifier
parameter_list|,
name|AllocationParams
name|appParams
parameter_list|,
name|ContainerIdGenerator
name|idCounter
parameter_list|,
name|ResourceRequest
name|rr
parameter_list|,
name|ApplicationAttemptId
name|id
parameter_list|,
name|String
name|userName
parameter_list|,
name|RemoteNode
name|node
parameter_list|)
throws|throws
name|YarnException
block|{
name|ContainerId
name|cId
init|=
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|id
argument_list|,
name|idCounter
operator|.
name|generateContainerId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Normalize the resource asks (Similar to what the the RM scheduler does
comment|// before accepting an ask)
name|Resource
name|capability
init|=
name|normalizeCapability
argument_list|(
name|appParams
argument_list|,
name|rr
argument_list|)
decl_stmt|;
name|long
name|currTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ContainerTokenIdentifier
name|containerTokenIdentifier
init|=
operator|new
name|ContainerTokenIdentifier
argument_list|(
name|cId
argument_list|,
literal|0
argument_list|,
name|node
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|userName
argument_list|,
name|capability
argument_list|,
name|currTime
operator|+
name|appParams
operator|.
name|containerTokenExpiryInterval
argument_list|,
name|tokenSecretManager
operator|.
name|getCurrentKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|rmIdentifier
argument_list|,
name|rr
operator|.
name|getPriority
argument_list|()
argument_list|,
name|currTime
argument_list|,
literal|null
argument_list|,
name|CommonNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|,
name|ContainerType
operator|.
name|TASK
argument_list|,
name|ExecutionType
operator|.
name|OPPORTUNISTIC
argument_list|)
decl_stmt|;
name|byte
index|[]
name|pwd
init|=
name|tokenSecretManager
operator|.
name|createPassword
argument_list|(
name|containerTokenIdentifier
argument_list|)
decl_stmt|;
name|Token
name|containerToken
init|=
name|newContainerToken
argument_list|(
name|node
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|pwd
argument_list|,
name|containerTokenIdentifier
argument_list|)
decl_stmt|;
name|Container
name|container
init|=
name|BuilderUtils
operator|.
name|newContainer
argument_list|(
name|cId
argument_list|,
name|node
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|node
operator|.
name|getHttpAddress
argument_list|()
argument_list|,
name|capability
argument_list|,
name|rr
operator|.
name|getPriority
argument_list|()
argument_list|,
name|containerToken
argument_list|,
name|containerTokenIdentifier
operator|.
name|getExecutionType
argument_list|()
argument_list|,
name|rr
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|container
return|;
block|}
DECL|method|normalizeCapability (AllocationParams appParams, ResourceRequest ask)
specifier|private
name|Resource
name|normalizeCapability
parameter_list|(
name|AllocationParams
name|appParams
parameter_list|,
name|ResourceRequest
name|ask
parameter_list|)
block|{
return|return
name|Resources
operator|.
name|normalize
argument_list|(
name|RESOURCE_CALCULATOR
argument_list|,
name|ask
operator|.
name|getCapability
argument_list|()
argument_list|,
name|appParams
operator|.
name|minResource
argument_list|,
name|appParams
operator|.
name|maxResource
argument_list|,
name|appParams
operator|.
name|incrementResource
argument_list|)
return|;
block|}
DECL|method|newContainerToken (NodeId nodeId, byte[] password, ContainerTokenIdentifier tokenIdentifier)
specifier|private
specifier|static
name|Token
name|newContainerToken
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|byte
index|[]
name|password
parameter_list|,
name|ContainerTokenIdentifier
name|tokenIdentifier
parameter_list|)
block|{
comment|// RPC layer client expects ip:port as service for tokens
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
name|nodeId
operator|.
name|getHost
argument_list|()
argument_list|,
name|nodeId
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
comment|// NOTE: use SecurityUtil.setTokenService if this becomes a "real" token
name|Token
name|containerToken
init|=
name|Token
operator|.
name|newInstance
argument_list|(
name|tokenIdentifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|ContainerTokenIdentifier
operator|.
name|KIND
operator|.
name|toString
argument_list|()
argument_list|,
name|password
argument_list|,
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|addr
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|containerToken
return|;
block|}
comment|/**    * Partitions a list of ResourceRequest to two separate lists, one for    * GUARANTEED and one for OPPORTUNISTIC ResourceRequests.    * @param askList the list of ResourceRequests to be partitioned    * @return the partitioned ResourceRequests    */
DECL|method|partitionAskList ( List<ResourceRequest> askList)
specifier|public
name|PartitionedResourceRequests
name|partitionAskList
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|askList
parameter_list|)
block|{
name|PartitionedResourceRequests
name|partitionedRequests
init|=
operator|new
name|PartitionedResourceRequests
argument_list|()
decl_stmt|;
for|for
control|(
name|ResourceRequest
name|rr
range|:
name|askList
control|)
block|{
if|if
condition|(
name|rr
operator|.
name|getExecutionTypeRequest
argument_list|()
operator|.
name|getExecutionType
argument_list|()
operator|==
name|ExecutionType
operator|.
name|OPPORTUNISTIC
condition|)
block|{
name|partitionedRequests
operator|.
name|getOpportunistic
argument_list|()
operator|.
name|add
argument_list|(
name|rr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|partitionedRequests
operator|.
name|getGuaranteed
argument_list|()
operator|.
name|add
argument_list|(
name|rr
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|partitionedRequests
return|;
block|}
block|}
end_class

end_unit

