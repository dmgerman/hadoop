begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|ResourceManager
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

begin_comment
comment|/**  * Helper library that:  * - tracks the state of all cluster {@link SchedulerNode}s  * - provides convenience methods to filter and sort nodes  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ClusterNodeTracker
specifier|public
class|class
name|ClusterNodeTracker
parameter_list|<
name|N
extends|extends
name|SchedulerNode
parameter_list|>
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
name|ClusterNodeTracker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|readWriteLock
specifier|private
name|ReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|readLock
specifier|private
name|Lock
name|readLock
init|=
name|readWriteLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
init|=
name|readWriteLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
DECL|field|nodes
specifier|private
name|HashMap
argument_list|<
name|NodeId
argument_list|,
name|N
argument_list|>
name|nodes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|nodeNameToNodeMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|N
argument_list|>
name|nodeNameToNodeMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|nodesPerRack
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|N
argument_list|>
argument_list|>
name|nodesPerRack
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|clusterCapacity
specifier|private
specifier|final
name|Resource
name|clusterCapacity
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|staleClusterCapacity
specifier|private
specifier|volatile
name|Resource
name|staleClusterCapacity
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
decl_stmt|;
comment|// Max allocation
DECL|field|maxNodeMemory
specifier|private
name|long
name|maxNodeMemory
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxNodeVCores
specifier|private
name|int
name|maxNodeVCores
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|configuredMaxAllocation
specifier|private
name|Resource
name|configuredMaxAllocation
decl_stmt|;
DECL|field|forceConfiguredMaxAllocation
specifier|private
name|boolean
name|forceConfiguredMaxAllocation
init|=
literal|true
decl_stmt|;
DECL|field|configuredMaxAllocationWaitTime
specifier|private
name|long
name|configuredMaxAllocationWaitTime
decl_stmt|;
DECL|method|addNode (N node)
specifier|public
name|void
name|addNode
parameter_list|(
name|N
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
name|nodes
operator|.
name|put
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|nodeNameToNodeMap
operator|.
name|put
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
comment|// Update nodes per rack as well
name|String
name|rackName
init|=
name|node
operator|.
name|getRackName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|N
argument_list|>
name|nodesList
init|=
name|nodesPerRack
operator|.
name|get
argument_list|(
name|rackName
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodesList
operator|==
literal|null
condition|)
block|{
name|nodesList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|nodesPerRack
operator|.
name|put
argument_list|(
name|rackName
argument_list|,
name|nodesList
argument_list|)
expr_stmt|;
block|}
name|nodesList
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|// Update cluster capacity
name|Resources
operator|.
name|addTo
argument_list|(
name|clusterCapacity
argument_list|,
name|node
operator|.
name|getTotalResource
argument_list|()
argument_list|)
expr_stmt|;
name|staleClusterCapacity
operator|=
name|Resources
operator|.
name|clone
argument_list|(
name|clusterCapacity
argument_list|)
expr_stmt|;
comment|// Update maximumAllocation
name|updateMaxResources
argument_list|(
name|node
argument_list|,
literal|true
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
DECL|method|exists (NodeId nodeId)
specifier|public
name|boolean
name|exists
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|nodes
operator|.
name|containsKey
argument_list|(
name|nodeId
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
DECL|method|getNode (NodeId nodeId)
specifier|public
name|N
name|getNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|nodes
operator|.
name|get
argument_list|(
name|nodeId
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
DECL|method|getNodeReport (NodeId nodeId)
specifier|public
name|SchedulerNodeReport
name|getNodeReport
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|N
name|n
init|=
name|nodes
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
return|return
name|n
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|SchedulerNodeReport
argument_list|(
name|n
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
DECL|method|nodeCount ()
specifier|public
name|int
name|nodeCount
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
name|nodes
operator|.
name|size
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
DECL|method|nodeCount (String rackName)
specifier|public
name|int
name|nodeCount
parameter_list|(
name|String
name|rackName
parameter_list|)
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|String
name|rName
init|=
name|rackName
operator|==
literal|null
condition|?
literal|"NULL"
else|:
name|rackName
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|N
argument_list|>
name|nodesList
init|=
name|nodesPerRack
operator|.
name|get
argument_list|(
name|rName
argument_list|)
decl_stmt|;
return|return
name|nodesList
operator|==
literal|null
condition|?
literal|0
else|:
name|nodesList
operator|.
name|size
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
DECL|method|getClusterCapacity ()
specifier|public
name|Resource
name|getClusterCapacity
parameter_list|()
block|{
return|return
name|staleClusterCapacity
return|;
block|}
DECL|method|removeNode (NodeId nodeId)
specifier|public
name|N
name|removeNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|N
name|node
init|=
name|nodes
operator|.
name|remove
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Attempting to remove a non-existent node "
operator|+
name|nodeId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|nodeNameToNodeMap
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update nodes per rack as well
name|String
name|rackName
init|=
name|node
operator|.
name|getRackName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|N
argument_list|>
name|nodesList
init|=
name|nodesPerRack
operator|.
name|get
argument_list|(
name|rackName
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodesList
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Attempting to remove node from an empty rack "
operator|+
name|rackName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodesList
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodesList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|nodesPerRack
operator|.
name|remove
argument_list|(
name|rackName
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Update cluster capacity
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|clusterCapacity
argument_list|,
name|node
operator|.
name|getTotalResource
argument_list|()
argument_list|)
expr_stmt|;
name|staleClusterCapacity
operator|=
name|Resources
operator|.
name|clone
argument_list|(
name|clusterCapacity
argument_list|)
expr_stmt|;
comment|// Update maximumAllocation
name|updateMaxResources
argument_list|(
name|node
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|node
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
DECL|method|setConfiguredMaxAllocation (Resource resource)
specifier|public
name|void
name|setConfiguredMaxAllocation
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|configuredMaxAllocation
operator|=
name|Resources
operator|.
name|clone
argument_list|(
name|resource
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
DECL|method|setConfiguredMaxAllocationWaitTime ( long configuredMaxAllocationWaitTime)
specifier|public
name|void
name|setConfiguredMaxAllocationWaitTime
parameter_list|(
name|long
name|configuredMaxAllocationWaitTime
parameter_list|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|configuredMaxAllocationWaitTime
operator|=
name|configuredMaxAllocationWaitTime
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
DECL|method|getMaxAllowedAllocation ()
specifier|public
name|Resource
name|getMaxAllowedAllocation
parameter_list|()
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
name|forceConfiguredMaxAllocation
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|ResourceManager
operator|.
name|getClusterTimeStamp
argument_list|()
operator|>
name|configuredMaxAllocationWaitTime
condition|)
block|{
name|forceConfiguredMaxAllocation
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|forceConfiguredMaxAllocation
operator|||
name|maxNodeMemory
operator|==
operator|-
literal|1
operator|||
name|maxNodeVCores
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|configuredMaxAllocation
return|;
block|}
name|Resource
name|ret
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|configuredMaxAllocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|.
name|getMemorySize
argument_list|()
operator|>
name|maxNodeMemory
condition|)
block|{
name|ret
operator|.
name|setMemorySize
argument_list|(
name|maxNodeMemory
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ret
operator|.
name|getVirtualCores
argument_list|()
operator|>
name|maxNodeVCores
condition|)
block|{
name|ret
operator|.
name|setVirtualCores
argument_list|(
name|maxNodeVCores
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
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
DECL|method|updateMaxResources (SchedulerNode node, boolean add)
specifier|private
name|void
name|updateMaxResources
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|boolean
name|add
parameter_list|)
block|{
name|Resource
name|totalResource
init|=
name|node
operator|.
name|getTotalResource
argument_list|()
decl_stmt|;
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|add
condition|)
block|{
comment|// added node
name|long
name|nodeMemory
init|=
name|totalResource
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeMemory
operator|>
name|maxNodeMemory
condition|)
block|{
name|maxNodeMemory
operator|=
name|nodeMemory
expr_stmt|;
block|}
name|int
name|nodeVCores
init|=
name|totalResource
operator|.
name|getVirtualCores
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeVCores
operator|>
name|maxNodeVCores
condition|)
block|{
name|maxNodeVCores
operator|=
name|nodeVCores
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// removed node
if|if
condition|(
name|maxNodeMemory
operator|==
name|totalResource
operator|.
name|getMemorySize
argument_list|()
condition|)
block|{
name|maxNodeMemory
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|maxNodeVCores
operator|==
name|totalResource
operator|.
name|getVirtualCores
argument_list|()
condition|)
block|{
name|maxNodeVCores
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|// We only have to iterate through the nodes if the current max memory
comment|// or vcores was equal to the removed node's
if|if
condition|(
name|maxNodeMemory
operator|==
operator|-
literal|1
operator|||
name|maxNodeVCores
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Treat it like an empty cluster and add nodes
for|for
control|(
name|N
name|n
range|:
name|nodes
operator|.
name|values
argument_list|()
control|)
block|{
name|updateMaxResources
argument_list|(
name|n
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
DECL|method|getAllNodes ()
specifier|public
name|List
argument_list|<
name|N
argument_list|>
name|getAllNodes
parameter_list|()
block|{
return|return
name|getNodes
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**    * Convenience method to filter nodes based on a condition.    *    * @param nodeFilter A {@link NodeFilter} for filtering the nodes    * @return A list of filtered nodes    */
DECL|method|getNodes (NodeFilter nodeFilter)
specifier|public
name|List
argument_list|<
name|N
argument_list|>
name|getNodes
parameter_list|(
name|NodeFilter
name|nodeFilter
parameter_list|)
block|{
name|List
argument_list|<
name|N
argument_list|>
name|nodeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|nodeFilter
operator|==
literal|null
condition|)
block|{
name|nodeList
operator|.
name|addAll
argument_list|(
name|nodes
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|N
name|node
range|:
name|nodes
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|nodeFilter
operator|.
name|accept
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|nodeList
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
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
name|nodeList
return|;
block|}
DECL|method|getAllNodeIds ()
specifier|public
name|List
argument_list|<
name|NodeId
argument_list|>
name|getAllNodeIds
parameter_list|()
block|{
return|return
name|getNodeIds
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**    * Convenience method to filter nodes based on a condition.    *    * @param nodeFilter A {@link NodeFilter} for filtering the nodes    * @return A list of filtered nodes    */
DECL|method|getNodeIds (NodeFilter nodeFilter)
specifier|public
name|List
argument_list|<
name|NodeId
argument_list|>
name|getNodeIds
parameter_list|(
name|NodeFilter
name|nodeFilter
parameter_list|)
block|{
name|List
argument_list|<
name|NodeId
argument_list|>
name|nodeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|nodeFilter
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|N
name|node
range|:
name|nodes
operator|.
name|values
argument_list|()
control|)
block|{
name|nodeList
operator|.
name|add
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|N
name|node
range|:
name|nodes
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|nodeFilter
operator|.
name|accept
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|nodeList
operator|.
name|add
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|nodeList
return|;
block|}
comment|/**    * Convenience method to sort nodes.    *    * Note that the sort is performed without holding a lock. We are sorting    * here instead of on the caller to allow for future optimizations (e.g.    * sort once every x milliseconds).    */
DECL|method|sortedNodeList (Comparator<N> comparator)
specifier|public
name|List
argument_list|<
name|N
argument_list|>
name|sortedNodeList
parameter_list|(
name|Comparator
argument_list|<
name|N
argument_list|>
name|comparator
parameter_list|)
block|{
name|List
argument_list|<
name|N
argument_list|>
name|sortedList
init|=
literal|null
decl_stmt|;
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|sortedList
operator|=
operator|new
name|ArrayList
argument_list|(
name|nodes
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|sortedList
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
return|return
name|sortedList
return|;
block|}
comment|/**    * Convenience method to return list of nodes corresponding to resourceName    * passed in the {@link ResourceRequest}.    *    * @param resourceName Host/rack name of the resource, or    * {@link ResourceRequest#ANY}    * @return list of nodes that match the resourceName    */
DECL|method|getNodesByResourceName (final String resourceName)
specifier|public
name|List
argument_list|<
name|N
argument_list|>
name|getNodesByResourceName
parameter_list|(
specifier|final
name|String
name|resourceName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|resourceName
operator|!=
literal|null
operator|&&
operator|!
name|resourceName
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|N
argument_list|>
name|retNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|ResourceRequest
operator|.
name|ANY
operator|.
name|equals
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
name|retNodes
operator|.
name|addAll
argument_list|(
name|getAllNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeNameToNodeMap
operator|.
name|containsKey
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
name|retNodes
operator|.
name|add
argument_list|(
name|nodeNameToNodeMap
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodesPerRack
operator|.
name|containsKey
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
name|retNodes
operator|.
name|addAll
argument_list|(
name|nodesPerRack
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not find a node matching given resourceName "
operator|+
name|resourceName
argument_list|)
expr_stmt|;
block|}
return|return
name|retNodes
return|;
block|}
comment|/**    * Convenience method to return list of {@link NodeId} corresponding to    * resourceName passed in the {@link ResourceRequest}.    *    * @param resourceName Host/rack name of the resource, or    * {@link ResourceRequest#ANY}    * @return list of {@link NodeId} that match the resourceName    */
DECL|method|getNodeIdsByResourceName (final String resourceName)
specifier|public
name|List
argument_list|<
name|NodeId
argument_list|>
name|getNodeIdsByResourceName
parameter_list|(
specifier|final
name|String
name|resourceName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|resourceName
operator|!=
literal|null
operator|&&
operator|!
name|resourceName
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeId
argument_list|>
name|retNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|ResourceRequest
operator|.
name|ANY
operator|.
name|equals
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
name|retNodes
operator|.
name|addAll
argument_list|(
name|getAllNodeIds
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeNameToNodeMap
operator|.
name|containsKey
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
name|retNodes
operator|.
name|add
argument_list|(
name|nodeNameToNodeMap
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodesPerRack
operator|.
name|containsKey
argument_list|(
name|resourceName
argument_list|)
condition|)
block|{
for|for
control|(
name|N
name|node
range|:
name|nodesPerRack
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
control|)
block|{
name|retNodes
operator|.
name|add
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not find a node matching given resourceName "
operator|+
name|resourceName
argument_list|)
expr_stmt|;
block|}
return|return
name|retNodes
return|;
block|}
block|}
end_class

end_unit

