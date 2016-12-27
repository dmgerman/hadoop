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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|scheduler
operator|.
name|OpportunisticContainerAllocator
operator|.
name|AllocationParams
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
name|scheduler
operator|.
name|OpportunisticContainerAllocator
operator|.
name|ContainerIdGenerator
import|;
end_import

begin_comment
comment|/**  * This encapsulates application specific information used by the  * Opportunistic Container Allocator to allocate containers.  */
end_comment

begin_class
DECL|class|OpportunisticContainerContext
specifier|public
class|class
name|OpportunisticContainerContext
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
name|OpportunisticContainerContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|appParams
specifier|private
name|AllocationParams
name|appParams
init|=
operator|new
name|AllocationParams
argument_list|()
decl_stmt|;
DECL|field|containerIdGenerator
specifier|private
name|ContainerIdGenerator
name|containerIdGenerator
init|=
operator|new
name|ContainerIdGenerator
argument_list|()
decl_stmt|;
DECL|field|nodeList
specifier|private
specifier|volatile
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|nodeList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|nodeMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RemoteNode
argument_list|>
name|nodeMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|blacklist
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|blacklist
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// This maintains a map of outstanding OPPORTUNISTIC Reqs. Key-ed by Priority,
comment|// Resource Name (host/rack/any) and capability. This mapping is required
comment|// to match a received Container to an outstanding OPPORTUNISTIC
comment|// ResourceRequest (ask).
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|SchedulerRequestKey
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
DECL|field|outstandingOpReqs
name|outstandingOpReqs
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|getAppParams ()
specifier|public
name|AllocationParams
name|getAppParams
parameter_list|()
block|{
return|return
name|appParams
return|;
block|}
DECL|method|getContainerIdGenerator ()
specifier|public
name|ContainerIdGenerator
name|getContainerIdGenerator
parameter_list|()
block|{
return|return
name|containerIdGenerator
return|;
block|}
DECL|method|setContainerIdGenerator ( ContainerIdGenerator containerIdGenerator)
specifier|public
name|void
name|setContainerIdGenerator
parameter_list|(
name|ContainerIdGenerator
name|containerIdGenerator
parameter_list|)
block|{
name|this
operator|.
name|containerIdGenerator
operator|=
name|containerIdGenerator
expr_stmt|;
block|}
DECL|method|getNodeMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|RemoteNode
argument_list|>
name|getNodeMap
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|nodeMap
argument_list|)
return|;
block|}
DECL|method|updateNodeList (List<RemoteNode> newNodeList)
specifier|public
specifier|synchronized
name|void
name|updateNodeList
parameter_list|(
name|List
argument_list|<
name|RemoteNode
argument_list|>
name|newNodeList
parameter_list|)
block|{
comment|// This is an optimization for centralized placement. The
comment|// OppContainerAllocatorAMService has a cached list of nodes which it sets
comment|// here. The nodeMap needs to be updated only if the backing node list is
comment|// modified.
if|if
condition|(
name|newNodeList
operator|!=
name|nodeList
condition|)
block|{
name|nodeList
operator|=
name|newNodeList
expr_stmt|;
name|nodeMap
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|RemoteNode
name|n
range|:
name|nodeList
control|)
block|{
name|nodeMap
operator|.
name|put
argument_list|(
name|n
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|updateAllocationParams (Resource minResource, Resource maxResource, Resource incrResource, int containerTokenExpiryInterval)
specifier|public
name|void
name|updateAllocationParams
parameter_list|(
name|Resource
name|minResource
parameter_list|,
name|Resource
name|maxResource
parameter_list|,
name|Resource
name|incrResource
parameter_list|,
name|int
name|containerTokenExpiryInterval
parameter_list|)
block|{
name|appParams
operator|.
name|setMinResource
argument_list|(
name|minResource
argument_list|)
expr_stmt|;
name|appParams
operator|.
name|setMaxResource
argument_list|(
name|maxResource
argument_list|)
expr_stmt|;
name|appParams
operator|.
name|setIncrementResource
argument_list|(
name|incrResource
argument_list|)
expr_stmt|;
name|appParams
operator|.
name|setContainerTokenExpiryInterval
argument_list|(
name|containerTokenExpiryInterval
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlacklist ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getBlacklist
parameter_list|()
block|{
return|return
name|blacklist
return|;
block|}
specifier|public
name|TreeMap
argument_list|<
name|SchedulerRequestKey
argument_list|,
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
DECL|method|getOutstandingOpReqs ()
name|getOutstandingOpReqs
parameter_list|()
block|{
return|return
name|outstandingOpReqs
return|;
block|}
comment|/**    * Takes a list of ResourceRequests (asks), extracts the key information viz.    * (Priority, ResourceName, Capability) and adds to the outstanding    * OPPORTUNISTIC outstandingOpReqs map. The nested map is required to enforce    * the current YARN constraint that only a single ResourceRequest can exist at    * a give Priority and Capability.    *    * @param resourceAsks the list with the {@link ResourceRequest}s    */
DECL|method|addToOutstandingReqs (List<ResourceRequest> resourceAsks)
specifier|public
name|void
name|addToOutstandingReqs
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceAsks
parameter_list|)
block|{
for|for
control|(
name|ResourceRequest
name|request
range|:
name|resourceAsks
control|)
block|{
name|SchedulerRequestKey
name|schedulerKey
init|=
name|SchedulerRequestKey
operator|.
name|create
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// TODO: Extend for Node/Rack locality. We only handle ANY requests now
if|if
condition|(
operator|!
name|ResourceRequest
operator|.
name|isAnyLocation
argument_list|(
name|request
operator|.
name|getResourceName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
continue|continue;
block|}
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
name|reqMap
init|=
name|outstandingOpReqs
operator|.
name|get
argument_list|(
name|schedulerKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|reqMap
operator|==
literal|null
condition|)
block|{
name|reqMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|outstandingOpReqs
operator|.
name|put
argument_list|(
name|schedulerKey
argument_list|,
name|reqMap
argument_list|)
expr_stmt|;
block|}
name|ResourceRequest
name|resourceRequest
init|=
name|reqMap
operator|.
name|get
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|resourceRequest
operator|==
literal|null
condition|)
block|{
name|resourceRequest
operator|=
name|request
expr_stmt|;
name|reqMap
operator|.
name|put
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resourceRequest
operator|.
name|setNumContainers
argument_list|(
name|resourceRequest
operator|.
name|getNumContainers
argument_list|()
operator|+
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ResourceRequest
operator|.
name|isAnyLocation
argument_list|(
name|request
operator|.
name|getResourceName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"# of outstandingOpReqs in ANY (at"
operator|+
literal|"priority = "
operator|+
name|schedulerKey
operator|.
name|getPriority
argument_list|()
operator|+
literal|", with capability = "
operator|+
name|request
operator|.
name|getCapability
argument_list|()
operator|+
literal|" ) : "
operator|+
name|resourceRequest
operator|.
name|getNumContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * This method matches a returned list of Container Allocations to any    * outstanding OPPORTUNISTIC ResourceRequest.    * @param capability Capability    * @param allocatedContainers Allocated Containers    */
DECL|method|matchAllocationToOutstandingRequest (Resource capability, List<Container> allocatedContainers)
specifier|public
name|void
name|matchAllocationToOutstandingRequest
parameter_list|(
name|Resource
name|capability
parameter_list|,
name|List
argument_list|<
name|Container
argument_list|>
name|allocatedContainers
parameter_list|)
block|{
for|for
control|(
name|Container
name|c
range|:
name|allocatedContainers
control|)
block|{
name|SchedulerRequestKey
name|schedulerKey
init|=
name|SchedulerRequestKey
operator|.
name|extractFrom
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Resource
argument_list|,
name|ResourceRequest
argument_list|>
name|asks
init|=
name|outstandingOpReqs
operator|.
name|get
argument_list|(
name|schedulerKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|asks
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ResourceRequest
name|rr
init|=
name|asks
operator|.
name|get
argument_list|(
name|capability
argument_list|)
decl_stmt|;
if|if
condition|(
name|rr
operator|!=
literal|null
condition|)
block|{
name|rr
operator|.
name|setNumContainers
argument_list|(
name|rr
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|rr
operator|.
name|getNumContainers
argument_list|()
operator|==
literal|0
condition|)
block|{
name|asks
operator|.
name|remove
argument_list|(
name|capability
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

