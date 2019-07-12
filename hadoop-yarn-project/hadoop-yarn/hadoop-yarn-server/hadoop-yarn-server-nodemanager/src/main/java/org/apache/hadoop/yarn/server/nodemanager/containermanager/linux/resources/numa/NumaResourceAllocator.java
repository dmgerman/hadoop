begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.numa
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|resources
operator|.
name|numa
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|Maps
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
name|conf
operator|.
name|Configuration
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
name|util
operator|.
name|Shell
operator|.
name|ShellCommandExecutor
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
name|util
operator|.
name|StringUtils
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
name|conf
operator|.
name|YarnConfiguration
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
name|server
operator|.
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ResourceMappings
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|resources
operator|.
name|ResourceHandlerException
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * NUMA Resources Allocator reads the NUMA topology and assigns NUMA nodes to  * the containers.  */
end_comment

begin_class
DECL|class|NumaResourceAllocator
specifier|public
class|class
name|NumaResourceAllocator
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
name|NumaResourceAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Regex to find node ids, Ex: 'available: 2 nodes (0-1)'
DECL|field|NUMA_NODEIDS_REGEX
specifier|private
specifier|static
specifier|final
name|String
name|NUMA_NODEIDS_REGEX
init|=
literal|"available:\\s*[0-9]+\\s*nodes\\s*\\(([0-9\\-,]*)\\)"
decl_stmt|;
comment|// Regex to find node memory, Ex: 'node 0 size: 73717 MB'
DECL|field|NUMA_NODE_MEMORY_REGEX
specifier|private
specifier|static
specifier|final
name|String
name|NUMA_NODE_MEMORY_REGEX
init|=
literal|"node\\s*<NUMA-NODE>\\s*size:\\s*([0-9]+)\\s*([KMG]B)"
decl_stmt|;
comment|// Regex to find node cpus, Ex: 'node 0 cpus: 0 2 4 6'
DECL|field|NUMA_NODE_CPUS_REGEX
specifier|private
specifier|static
specifier|final
name|String
name|NUMA_NODE_CPUS_REGEX
init|=
literal|"node\\s*<NUMA-NODE>\\s*cpus:\\s*([0-9\\s]+)"
decl_stmt|;
DECL|field|GB
specifier|private
specifier|static
specifier|final
name|String
name|GB
init|=
literal|"GB"
decl_stmt|;
DECL|field|KB
specifier|private
specifier|static
specifier|final
name|String
name|KB
init|=
literal|"KB"
decl_stmt|;
DECL|field|NUMA_NODE
specifier|private
specifier|static
specifier|final
name|String
name|NUMA_NODE
init|=
literal|"<NUMA-NODE>"
decl_stmt|;
DECL|field|SPACE
specifier|private
specifier|static
specifier|final
name|String
name|SPACE
init|=
literal|"\\s"
decl_stmt|;
DECL|field|DEFAULT_NUMA_NODE_MEMORY
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_NUMA_NODE_MEMORY
init|=
literal|1024
decl_stmt|;
DECL|field|DEFAULT_NUMA_NODE_CPUS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_NUMA_NODE_CPUS
init|=
literal|1
decl_stmt|;
DECL|field|NUMA_RESOURCE_TYPE
specifier|private
specifier|static
specifier|final
name|String
name|NUMA_RESOURCE_TYPE
init|=
literal|"numa"
decl_stmt|;
DECL|field|numaNodesList
specifier|private
name|List
argument_list|<
name|NumaNodeResource
argument_list|>
name|numaNodesList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|numaNodeIdVsResource
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|NumaNodeResource
argument_list|>
name|numaNodeIdVsResource
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|currentAssignNode
specifier|private
name|int
name|currentAssignNode
decl_stmt|;
DECL|field|context
specifier|private
name|Context
name|context
decl_stmt|;
DECL|method|NumaResourceAllocator (Context context)
specifier|public
name|NumaResourceAllocator
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
block|{
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NUMA_AWARENESS_READ_TOPOLOGY
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_NUMA_AWARENESS_READ_TOPOLOGY
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading NUMA topology using 'numactl --hardware' command."
argument_list|)
expr_stmt|;
name|String
name|cmdOutput
init|=
name|executeNGetCmdOutput
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|outputLines
init|=
name|cmdOutput
operator|.
name|split
argument_list|(
literal|"\\n"
argument_list|)
decl_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|NUMA_NODEIDS_REGEX
argument_list|)
decl_stmt|;
name|String
name|nodeIdsStr
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|outputLines
control|)
block|{
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|nodeIdsStr
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|nodeIdsStr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed to get numa nodes from"
operator|+
literal|" 'numactl --hardware' output and output is:\n"
operator|+
name|cmdOutput
argument_list|)
throw|;
block|}
name|String
index|[]
name|nodeIdCommaSplits
init|=
name|nodeIdsStr
operator|.
name|split
argument_list|(
literal|"[,\\s]"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|nodeIdOrRange
range|:
name|nodeIdCommaSplits
control|)
block|{
if|if
condition|(
name|nodeIdOrRange
operator|.
name|contains
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|String
index|[]
name|beginNEnd
init|=
name|nodeIdOrRange
operator|.
name|split
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
name|int
name|endNode
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|beginNEnd
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|nodeId
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|beginNEnd
index|[
literal|0
index|]
argument_list|)
init|;
name|nodeId
operator|<=
name|endNode
condition|;
name|nodeId
operator|++
control|)
block|{
name|long
name|memory
init|=
name|parseMemory
argument_list|(
name|outputLines
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|cpus
init|=
name|parseCpus
argument_list|(
name|outputLines
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
name|addToCollection
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|nodeId
argument_list|)
argument_list|,
name|memory
argument_list|,
name|cpus
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|long
name|memory
init|=
name|parseMemory
argument_list|(
name|outputLines
argument_list|,
name|nodeIdOrRange
argument_list|)
decl_stmt|;
name|int
name|cpus
init|=
name|parseCpus
argument_list|(
name|outputLines
argument_list|,
name|nodeIdOrRange
argument_list|)
decl_stmt|;
name|addToCollection
argument_list|(
name|nodeIdOrRange
argument_list|,
name|memory
argument_list|,
name|cpus
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading NUMA topology using configurations."
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|nodeIds
init|=
name|conf
operator|.
name|getStringCollection
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NUMA_AWARENESS_NODE_IDS
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|nodeId
range|:
name|nodeIds
control|)
block|{
name|long
name|mem
init|=
name|conf
operator|.
name|getLong
argument_list|(
literal|"yarn.nodemanager.numa-awareness."
operator|+
name|nodeId
operator|+
literal|".memory"
argument_list|,
name|DEFAULT_NUMA_NODE_MEMORY
argument_list|)
decl_stmt|;
name|int
name|cpus
init|=
name|conf
operator|.
name|getInt
argument_list|(
literal|"yarn.nodemanager.numa-awareness."
operator|+
name|nodeId
operator|+
literal|".cpus"
argument_list|,
name|DEFAULT_NUMA_NODE_CPUS
argument_list|)
decl_stmt|;
name|addToCollection
argument_list|(
name|nodeId
argument_list|,
name|mem
argument_list|,
name|cpus
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|numaNodesList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"There are no available NUMA nodes"
operator|+
literal|" for making containers NUMA aware."
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Available numa nodes with capacities : "
operator|+
name|numaNodesList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|executeNGetCmdOutput (Configuration conf)
name|String
name|executeNGetCmdOutput
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
block|{
name|String
name|numaCtlCmd
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_NUMA_AWARENESS_NUMACTL_CMD
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_NUMA_AWARENESS_NUMACTL_CMD
argument_list|)
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|numaCtlCmd
block|,
literal|"--hardware"
block|}
decl_stmt|;
name|ShellCommandExecutor
name|shExec
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|args
argument_list|)
decl_stmt|;
try|try
block|{
name|shExec
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed to read the numa configurations."
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|shExec
operator|.
name|getOutput
argument_list|()
return|;
block|}
DECL|method|parseCpus (String[] outputLines, String nodeId)
specifier|private
name|int
name|parseCpus
parameter_list|(
name|String
index|[]
name|outputLines
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
name|int
name|cpus
init|=
literal|0
decl_stmt|;
name|Pattern
name|patternNodeCPUs
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|NUMA_NODE_CPUS_REGEX
operator|.
name|replace
argument_list|(
name|NUMA_NODE
argument_list|,
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|outputLines
control|)
block|{
name|Matcher
name|matcherNodeCPUs
init|=
name|patternNodeCPUs
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcherNodeCPUs
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|cpusStr
init|=
name|matcherNodeCPUs
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|cpus
operator|=
name|cpusStr
operator|.
name|split
argument_list|(
name|SPACE
argument_list|)
operator|.
name|length
expr_stmt|;
break|break;
block|}
block|}
return|return
name|cpus
return|;
block|}
DECL|method|parseMemory (String[] outputLines, String nodeId)
specifier|private
name|long
name|parseMemory
parameter_list|(
name|String
index|[]
name|outputLines
parameter_list|,
name|String
name|nodeId
parameter_list|)
throws|throws
name|YarnException
block|{
name|long
name|memory
init|=
literal|0
decl_stmt|;
name|String
name|units
decl_stmt|;
name|Pattern
name|patternNodeMem
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|NUMA_NODE_MEMORY_REGEX
operator|.
name|replace
argument_list|(
name|NUMA_NODE
argument_list|,
name|nodeId
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|outputLines
control|)
block|{
name|Matcher
name|matcherNodeMem
init|=
name|patternNodeMem
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcherNodeMem
operator|.
name|find
argument_list|()
condition|)
block|{
try|try
block|{
name|memory
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|matcherNodeMem
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|units
operator|=
name|matcherNodeMem
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|GB
operator|.
name|equals
argument_list|(
name|units
argument_list|)
condition|)
block|{
name|memory
operator|=
name|memory
operator|*
literal|1024
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|KB
operator|.
name|equals
argument_list|(
name|units
argument_list|)
condition|)
block|{
name|memory
operator|=
name|memory
operator|/
literal|1024
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed to get memory for node:"
operator|+
name|nodeId
argument_list|,
name|ex
argument_list|)
throw|;
block|}
break|break;
block|}
block|}
return|return
name|memory
return|;
block|}
DECL|method|addToCollection (String nodeId, long memory, int cpus)
specifier|private
name|void
name|addToCollection
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|long
name|memory
parameter_list|,
name|int
name|cpus
parameter_list|)
block|{
name|NumaNodeResource
name|numaNode
init|=
operator|new
name|NumaNodeResource
argument_list|(
name|nodeId
argument_list|,
name|memory
argument_list|,
name|cpus
argument_list|)
decl_stmt|;
name|numaNodesList
operator|.
name|add
argument_list|(
name|numaNode
argument_list|)
expr_stmt|;
name|numaNodeIdVsResource
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|numaNode
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allocates the available NUMA nodes for the requested containerId with    * resource in a round robin fashion.    *    * @param container the container to allocate NUMA resources    * @return the assigned NUMA Node info or null if resources not available.    * @throws ResourceHandlerException when failed to store NUMA resources    */
DECL|method|allocateNumaNodes ( Container container)
specifier|public
specifier|synchronized
name|NumaResourceAllocation
name|allocateNumaNodes
parameter_list|(
name|Container
name|container
parameter_list|)
throws|throws
name|ResourceHandlerException
block|{
name|NumaResourceAllocation
name|allocation
init|=
name|allocate
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|allocation
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// Update state store.
name|context
operator|.
name|getNMStateStore
argument_list|()
operator|.
name|storeAssignedResources
argument_list|(
name|container
argument_list|,
name|NUMA_RESOURCE_TYPE
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|allocation
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|releaseNumaResource
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ResourceHandlerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|allocation
return|;
block|}
DECL|method|allocate (ContainerId containerId, Resource resource)
specifier|private
name|NumaResourceAllocation
name|allocate
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|numaNodesList
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|NumaNodeResource
name|numaNode
init|=
name|numaNodesList
operator|.
name|get
argument_list|(
operator|(
name|currentAssignNode
operator|+
name|index
operator|)
operator|%
name|numaNodesList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|numaNode
operator|.
name|isResourcesAvailable
argument_list|(
name|resource
argument_list|)
condition|)
block|{
name|numaNode
operator|.
name|assignResources
argument_list|(
name|resource
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Assigning NUMA node "
operator|+
name|numaNode
operator|.
name|getNodeId
argument_list|()
operator|+
literal|" for memory, "
operator|+
name|numaNode
operator|.
name|getNodeId
argument_list|()
operator|+
literal|" for cpus for the "
operator|+
name|containerId
argument_list|)
expr_stmt|;
name|currentAssignNode
operator|=
operator|(
name|currentAssignNode
operator|+
name|index
operator|+
literal|1
operator|)
operator|%
name|numaNodesList
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
operator|new
name|NumaResourceAllocation
argument_list|(
name|numaNode
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|resource
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|numaNode
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|resource
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|// If there is no single node matched for the container resource
comment|// Check the NUMA nodes for Memory resources
name|long
name|memoryRequirement
init|=
name|resource
operator|.
name|getMemorySize
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|memoryAllocations
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|NumaNodeResource
name|numaNode
range|:
name|numaNodesList
control|)
block|{
name|long
name|memoryRemaining
init|=
name|numaNode
operator|.
name|assignAvailableMemory
argument_list|(
name|memoryRequirement
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
name|memoryAllocations
operator|.
name|put
argument_list|(
name|numaNode
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|memoryRequirement
operator|-
name|memoryRemaining
argument_list|)
expr_stmt|;
name|memoryRequirement
operator|=
name|memoryRemaining
expr_stmt|;
if|if
condition|(
name|memoryRequirement
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|memoryRequirement
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"There is no available memory:"
operator|+
name|resource
operator|.
name|getMemorySize
argument_list|()
operator|+
literal|" in numa nodes for "
operator|+
name|containerId
argument_list|)
expr_stmt|;
name|releaseNumaResource
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Check the NUMA nodes for CPU resources
name|int
name|cpusRequirement
init|=
name|resource
operator|.
name|getVirtualCores
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|cpuAllocations
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|numaNodesList
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|NumaNodeResource
name|numaNode
init|=
name|numaNodesList
operator|.
name|get
argument_list|(
operator|(
name|currentAssignNode
operator|+
name|index
operator|)
operator|%
name|numaNodesList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|cpusRemaining
init|=
name|numaNode
operator|.
name|assignAvailableCpus
argument_list|(
name|cpusRequirement
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
name|cpuAllocations
operator|.
name|put
argument_list|(
name|numaNode
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|cpusRequirement
operator|-
name|cpusRemaining
argument_list|)
expr_stmt|;
name|cpusRequirement
operator|=
name|cpusRemaining
expr_stmt|;
if|if
condition|(
name|cpusRequirement
operator|==
literal|0
condition|)
block|{
name|currentAssignNode
operator|=
operator|(
name|currentAssignNode
operator|+
name|index
operator|+
literal|1
operator|)
operator|%
name|numaNodesList
operator|.
name|size
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|cpusRequirement
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"There are no available cpus:"
operator|+
name|resource
operator|.
name|getVirtualCores
argument_list|()
operator|+
literal|" in numa nodes for "
operator|+
name|containerId
argument_list|)
expr_stmt|;
name|releaseNumaResource
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|NumaResourceAllocation
name|assignedNumaNodeInfo
init|=
operator|new
name|NumaResourceAllocation
argument_list|(
name|memoryAllocations
argument_list|,
name|cpuAllocations
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Assigning multiple NUMA nodes ("
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|assignedNumaNodeInfo
operator|.
name|getMemNodes
argument_list|()
argument_list|)
operator|+
literal|") for memory, ("
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|assignedNumaNodeInfo
operator|.
name|getCpuNodes
argument_list|()
argument_list|)
operator|+
literal|") for cpus for "
operator|+
name|containerId
argument_list|)
expr_stmt|;
return|return
name|assignedNumaNodeInfo
return|;
block|}
comment|/**    * Release assigned NUMA resources for the container.    *    * @param containerId the container ID    */
DECL|method|releaseNumaResource (ContainerId containerId)
specifier|public
specifier|synchronized
name|void
name|releaseNumaResource
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Releasing the assigned NUMA resources for "
operator|+
name|containerId
argument_list|)
expr_stmt|;
for|for
control|(
name|NumaNodeResource
name|numaNode
range|:
name|numaNodesList
control|)
block|{
name|numaNode
operator|.
name|releaseResources
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Recovers assigned numa resources.    *    * @param containerId the container ID to recover resources    */
DECL|method|recoverNumaResource (ContainerId containerId)
specifier|public
specifier|synchronized
name|void
name|recoverNumaResource
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|Container
name|container
init|=
name|context
operator|.
name|getContainers
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|ResourceMappings
name|resourceMappings
init|=
name|container
operator|.
name|getResourceMappings
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Serializable
argument_list|>
name|assignedResources
init|=
name|resourceMappings
operator|.
name|getAssignedResources
argument_list|(
name|NUMA_RESOURCE_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|assignedResources
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|NumaResourceAllocation
name|numaResourceAllocation
init|=
operator|(
name|NumaResourceAllocation
operator|)
name|assignedResources
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|nodeAndMemory
range|:
name|numaResourceAllocation
operator|.
name|getNodeVsMemory
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|numaNodeIdVsResource
operator|.
name|get
argument_list|(
name|nodeAndMemory
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|recoverMemory
argument_list|(
name|containerId
argument_list|,
name|nodeAndMemory
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nodeAndCpus
range|:
name|numaResourceAllocation
operator|.
name|getNodeVsCpus
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|numaNodeIdVsResource
operator|.
name|get
argument_list|(
name|nodeAndCpus
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|recoverCpus
argument_list|(
name|containerId
argument_list|,
name|nodeAndCpus
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected number:"
operator|+
name|assignedResources
operator|.
name|size
argument_list|()
operator|+
literal|" of assigned numa resources for "
operator|+
name|containerId
operator|+
literal|" while recovering."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNumaNodesList ()
name|Collection
argument_list|<
name|NumaNodeResource
argument_list|>
name|getNumaNodesList
parameter_list|()
block|{
return|return
name|numaNodesList
return|;
block|}
block|}
end_class

end_unit

