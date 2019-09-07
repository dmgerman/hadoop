begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.placement.algorithms
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|algorithms
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|hdds
operator|.
name|scm
operator|.
name|net
operator|.
name|NetConstants
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
name|hdds
operator|.
name|scm
operator|.
name|net
operator|.
name|NetworkTopology
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
name|hdds
operator|.
name|scm
operator|.
name|net
operator|.
name|Node
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
name|hdds
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|List
import|;
end_import

begin_comment
comment|/**  * Container placement policy that choose datanodes with network topology  * awareness, together with the space to satisfy the size constraints.  *<p>  * This placement policy complies with the algorithm used in HDFS. With default  * 3 replica, two replica will be on the same rack, the third one will on a  * different rack.  *<p>  * This implementation applies to network topology like "/rack/node". Don't  * recommend to use this if the network topology has more layers.  *<p>  */
end_comment

begin_class
DECL|class|SCMContainerPlacementRackAware
specifier|public
specifier|final
class|class
name|SCMContainerPlacementRackAware
extends|extends
name|SCMCommonPolicy
block|{
annotation|@
name|VisibleForTesting
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SCMContainerPlacementRackAware
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|networkTopology
specifier|private
specifier|final
name|NetworkTopology
name|networkTopology
decl_stmt|;
DECL|field|fallback
specifier|private
name|boolean
name|fallback
decl_stmt|;
DECL|field|RACK_LEVEL
specifier|private
specifier|static
specifier|final
name|int
name|RACK_LEVEL
init|=
literal|1
decl_stmt|;
DECL|field|MAX_RETRY
specifier|private
specifier|static
specifier|final
name|int
name|MAX_RETRY
init|=
literal|3
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|SCMContainerPlacementMetrics
name|metrics
decl_stmt|;
comment|/**    * Constructs a Container Placement with rack awareness.    *    * @param nodeManager Node Manager    * @param conf Configuration    * @param fallback Whether reducing constrains to choose a data node when    *                 there is no node which satisfy all constrains.    *                 Basically, false for open container placement, and true    *                 for closed container placement.    */
DECL|method|SCMContainerPlacementRackAware (final NodeManager nodeManager, final Configuration conf, final NetworkTopology networkTopology, final boolean fallback, final SCMContainerPlacementMetrics metrics)
specifier|public
name|SCMContainerPlacementRackAware
parameter_list|(
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|NetworkTopology
name|networkTopology
parameter_list|,
specifier|final
name|boolean
name|fallback
parameter_list|,
specifier|final
name|SCMContainerPlacementMetrics
name|metrics
parameter_list|)
block|{
name|super
argument_list|(
name|nodeManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|networkTopology
operator|=
name|networkTopology
expr_stmt|;
name|this
operator|.
name|fallback
operator|=
name|fallback
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
block|}
comment|/**    * Called by SCM to choose datanodes.    * There are two scenarios, one is choosing all nodes for a new pipeline.    * Another is choosing node to meet replication requirement.    *    *    * @param excludedNodes - list of the datanodes to exclude.    * @param favoredNodes - list of nodes preferred. This is a hint to the    *                     allocator, whether the favored nodes will be used    *                     depends on whether the nodes meets the allocator's    *                     requirement.    * @param nodesRequired - number of datanodes required.    * @param sizeRequired - size required for the container or block.    * @return List of datanodes.    * @throws SCMException  SCMException    */
annotation|@
name|Override
DECL|method|chooseDatanodes ( List<DatanodeDetails> excludedNodes, List<DatanodeDetails> favoredNodes, int nodesRequired, final long sizeRequired)
specifier|public
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|chooseDatanodes
parameter_list|(
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|excludedNodes
parameter_list|,
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|favoredNodes
parameter_list|,
name|int
name|nodesRequired
parameter_list|,
specifier|final
name|long
name|sizeRequired
parameter_list|)
throws|throws
name|SCMException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|nodesRequired
operator|>
literal|0
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|incrDatanodeRequestCount
argument_list|(
name|nodesRequired
argument_list|)
expr_stmt|;
name|int
name|datanodeCount
init|=
name|networkTopology
operator|.
name|getNumOfLeafNode
argument_list|(
name|NetConstants
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|int
name|excludedNodesCount
init|=
name|excludedNodes
operator|==
literal|null
condition|?
literal|0
else|:
name|excludedNodes
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|datanodeCount
operator|<
name|nodesRequired
operator|+
name|excludedNodesCount
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"No enough datanodes to choose. "
operator|+
literal|"TotalNode = "
operator|+
name|datanodeCount
operator|+
literal|"RequiredNode = "
operator|+
name|nodesRequired
operator|+
literal|"ExcludedNode = "
operator|+
name|excludedNodesCount
argument_list|,
literal|null
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|mutableFavoredNodes
init|=
name|favoredNodes
decl_stmt|;
comment|// sanity check of favoredNodes
if|if
condition|(
name|mutableFavoredNodes
operator|!=
literal|null
operator|&&
name|excludedNodes
operator|!=
literal|null
condition|)
block|{
name|mutableFavoredNodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|mutableFavoredNodes
operator|.
name|addAll
argument_list|(
name|favoredNodes
argument_list|)
expr_stmt|;
name|mutableFavoredNodes
operator|.
name|removeAll
argument_list|(
name|excludedNodes
argument_list|)
expr_stmt|;
block|}
name|int
name|favoredNodeNum
init|=
name|mutableFavoredNodes
operator|==
literal|null
condition|?
literal|0
else|:
name|mutableFavoredNodes
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|chosenNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|favorIndex
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|excludedNodes
operator|==
literal|null
operator|||
name|excludedNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// choose all nodes for a new pipeline case
comment|// choose first datanode from scope ROOT or from favoredNodes if not null
name|Node
name|favoredNode
init|=
name|favoredNodeNum
operator|>
name|favorIndex
condition|?
name|mutableFavoredNodes
operator|.
name|get
argument_list|(
name|favorIndex
argument_list|)
else|:
literal|null
decl_stmt|;
name|Node
name|firstNode
decl_stmt|;
if|if
condition|(
name|favoredNode
operator|!=
literal|null
condition|)
block|{
name|firstNode
operator|=
name|favoredNode
expr_stmt|;
name|favorIndex
operator|++
expr_stmt|;
block|}
else|else
block|{
name|firstNode
operator|=
name|chooseNode
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|sizeRequired
argument_list|)
expr_stmt|;
block|}
name|chosenNodes
operator|.
name|add
argument_list|(
name|firstNode
argument_list|)
expr_stmt|;
name|nodesRequired
operator|--
expr_stmt|;
if|if
condition|(
name|nodesRequired
operator|==
literal|0
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|chosenNodes
operator|.
name|toArray
argument_list|(
operator|new
name|DatanodeDetails
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|// choose second datanode on the same rack as first one
name|favoredNode
operator|=
name|favoredNodeNum
operator|>
name|favorIndex
condition|?
name|mutableFavoredNodes
operator|.
name|get
argument_list|(
name|favorIndex
argument_list|)
else|:
literal|null
expr_stmt|;
name|Node
name|secondNode
decl_stmt|;
if|if
condition|(
name|favoredNode
operator|!=
literal|null
operator|&&
name|networkTopology
operator|.
name|isSameParent
argument_list|(
name|firstNode
argument_list|,
name|favoredNode
argument_list|)
condition|)
block|{
name|secondNode
operator|=
name|favoredNode
expr_stmt|;
name|favorIndex
operator|++
expr_stmt|;
block|}
else|else
block|{
name|secondNode
operator|=
name|chooseNode
argument_list|(
name|chosenNodes
argument_list|,
name|firstNode
argument_list|,
name|sizeRequired
argument_list|)
expr_stmt|;
block|}
name|chosenNodes
operator|.
name|add
argument_list|(
name|secondNode
argument_list|)
expr_stmt|;
name|nodesRequired
operator|--
expr_stmt|;
if|if
condition|(
name|nodesRequired
operator|==
literal|0
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|chosenNodes
operator|.
name|toArray
argument_list|(
operator|new
name|DatanodeDetails
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|// choose remaining datanodes on different rack as first and second
return|return
name|chooseNodes
argument_list|(
literal|null
argument_list|,
name|chosenNodes
argument_list|,
name|mutableFavoredNodes
argument_list|,
name|favorIndex
argument_list|,
name|nodesRequired
argument_list|,
name|sizeRequired
argument_list|)
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|Node
argument_list|>
name|mutableExcludedNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|mutableExcludedNodes
operator|.
name|addAll
argument_list|(
name|excludedNodes
argument_list|)
expr_stmt|;
comment|// choose node to meet replication requirement
comment|// case 1: one excluded node, choose one on the same rack as the excluded
comment|// node, choose others on different racks.
name|Node
name|favoredNode
decl_stmt|;
if|if
condition|(
name|excludedNodes
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|favoredNode
operator|=
name|favoredNodeNum
operator|>
name|favorIndex
condition|?
name|mutableFavoredNodes
operator|.
name|get
argument_list|(
name|favorIndex
argument_list|)
else|:
literal|null
expr_stmt|;
name|Node
name|firstNode
decl_stmt|;
if|if
condition|(
name|favoredNode
operator|!=
literal|null
operator|&&
name|networkTopology
operator|.
name|isSameParent
argument_list|(
name|excludedNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|favoredNode
argument_list|)
condition|)
block|{
name|firstNode
operator|=
name|favoredNode
expr_stmt|;
name|favorIndex
operator|++
expr_stmt|;
block|}
else|else
block|{
name|firstNode
operator|=
name|chooseNode
argument_list|(
name|mutableExcludedNodes
argument_list|,
name|excludedNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|sizeRequired
argument_list|)
expr_stmt|;
block|}
name|chosenNodes
operator|.
name|add
argument_list|(
name|firstNode
argument_list|)
expr_stmt|;
name|nodesRequired
operator|--
expr_stmt|;
if|if
condition|(
name|nodesRequired
operator|==
literal|0
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|chosenNodes
operator|.
name|toArray
argument_list|(
operator|new
name|DatanodeDetails
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|// choose remaining nodes on different racks
return|return
name|chooseNodes
argument_list|(
literal|null
argument_list|,
name|chosenNodes
argument_list|,
name|mutableFavoredNodes
argument_list|,
name|favorIndex
argument_list|,
name|nodesRequired
argument_list|,
name|sizeRequired
argument_list|)
return|;
block|}
comment|// case 2: two or more excluded nodes, if these two nodes are
comment|// in the same rack, then choose nodes on different racks, otherwise,
comment|// choose one on the same rack as one of excluded nodes, remaining chosen
comment|// are on different racks.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|excludedNodesCount
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|excludedNodesCount
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|networkTopology
operator|.
name|isSameParent
argument_list|(
name|excludedNodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|excludedNodes
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
condition|)
block|{
comment|// choose remaining nodes on different racks
return|return
name|chooseNodes
argument_list|(
name|mutableExcludedNodes
argument_list|,
name|chosenNodes
argument_list|,
name|mutableFavoredNodes
argument_list|,
name|favorIndex
argument_list|,
name|nodesRequired
argument_list|,
name|sizeRequired
argument_list|)
return|;
block|}
block|}
block|}
comment|// choose one data on the same rack with one excluded node
name|favoredNode
operator|=
name|favoredNodeNum
operator|>
name|favorIndex
condition|?
name|mutableFavoredNodes
operator|.
name|get
argument_list|(
name|favorIndex
argument_list|)
else|:
literal|null
expr_stmt|;
name|Node
name|secondNode
decl_stmt|;
if|if
condition|(
name|favoredNode
operator|!=
literal|null
operator|&&
name|networkTopology
operator|.
name|isSameParent
argument_list|(
name|mutableExcludedNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|favoredNode
argument_list|)
condition|)
block|{
name|secondNode
operator|=
name|favoredNode
expr_stmt|;
name|favorIndex
operator|++
expr_stmt|;
block|}
else|else
block|{
name|secondNode
operator|=
name|chooseNode
argument_list|(
name|chosenNodes
argument_list|,
name|mutableExcludedNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|sizeRequired
argument_list|)
expr_stmt|;
block|}
name|chosenNodes
operator|.
name|add
argument_list|(
name|secondNode
argument_list|)
expr_stmt|;
name|mutableExcludedNodes
operator|.
name|add
argument_list|(
name|secondNode
argument_list|)
expr_stmt|;
name|nodesRequired
operator|--
expr_stmt|;
if|if
condition|(
name|nodesRequired
operator|==
literal|0
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|chosenNodes
operator|.
name|toArray
argument_list|(
operator|new
name|DatanodeDetails
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|// choose remaining nodes on different racks
return|return
name|chooseNodes
argument_list|(
name|mutableExcludedNodes
argument_list|,
name|chosenNodes
argument_list|,
name|mutableFavoredNodes
argument_list|,
name|favorIndex
argument_list|,
name|nodesRequired
argument_list|,
name|sizeRequired
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|chooseNode (List<DatanodeDetails> healthyNodes)
specifier|public
name|DatanodeDetails
name|chooseNode
parameter_list|(
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|healthyNodes
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Choose a datanode which meets the requirements. If there is no node which    * meets all the requirements, there is fallback chosen process depending on    * whether fallback is allowed when this class is instantiated.    *    *    * @param excludedNodes - list of the datanodes to excluded. Can be null.    * @param affinityNode - the chosen nodes should be on the same rack as    *                    affinityNode. Can be null.    * @param sizeRequired - size required for the container or block.    * @return List of chosen datanodes.    * @throws SCMException  SCMException    */
DECL|method|chooseNode (List<Node> excludedNodes, Node affinityNode, long sizeRequired)
specifier|private
name|Node
name|chooseNode
parameter_list|(
name|List
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|Node
name|affinityNode
parameter_list|,
name|long
name|sizeRequired
parameter_list|)
throws|throws
name|SCMException
block|{
name|int
name|ancestorGen
init|=
name|RACK_LEVEL
decl_stmt|;
name|int
name|maxRetry
init|=
name|MAX_RETRY
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|excludedNodesForCapacity
init|=
literal|null
decl_stmt|;
name|boolean
name|isFallbacked
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Node
name|node
init|=
name|networkTopology
operator|.
name|chooseRandom
argument_list|(
name|NetConstants
operator|.
name|ROOT
argument_list|,
literal|null
argument_list|,
name|excludedNodes
argument_list|,
name|affinityNode
argument_list|,
name|ancestorGen
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|incrDatanodeChooseAttemptCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
comment|// cannot find the node which meets all constrains
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to find the datanode. excludedNodes:"
operator|+
operator|(
name|excludedNodes
operator|==
literal|null
condition|?
literal|""
else|:
name|excludedNodes
operator|.
name|toString
argument_list|()
operator|)
operator|+
literal|", affinityNode:"
operator|+
operator|(
name|affinityNode
operator|==
literal|null
condition|?
literal|""
else|:
name|affinityNode
operator|.
name|getNetworkFullPath
argument_list|()
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|fallback
condition|)
block|{
name|isFallbacked
operator|=
literal|true
expr_stmt|;
comment|// fallback, don't consider the affinity node
if|if
condition|(
name|affinityNode
operator|!=
literal|null
condition|)
block|{
name|affinityNode
operator|=
literal|null
expr_stmt|;
continue|continue;
block|}
comment|// fallback, don't consider cross rack
if|if
condition|(
name|ancestorGen
operator|==
name|RACK_LEVEL
condition|)
block|{
name|ancestorGen
operator|--
expr_stmt|;
continue|continue;
block|}
block|}
comment|// there is no constrains to reduce or fallback is true
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"No satisfied datanode to meet the "
operator|+
literal|" excludedNodes and affinityNode constrains."
argument_list|,
literal|null
argument_list|)
throw|;
block|}
if|if
condition|(
name|hasEnoughSpace
argument_list|(
operator|(
name|DatanodeDetails
operator|)
name|node
argument_list|,
name|sizeRequired
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Datanode {} is chosen. Required size is {}"
argument_list|,
name|node
operator|.
name|toString
argument_list|()
argument_list|,
name|sizeRequired
argument_list|)
expr_stmt|;
if|if
condition|(
name|excludedNodes
operator|!=
literal|null
operator|&&
name|excludedNodesForCapacity
operator|!=
literal|null
condition|)
block|{
name|excludedNodes
operator|.
name|removeAll
argument_list|(
name|excludedNodesForCapacity
argument_list|)
expr_stmt|;
block|}
name|metrics
operator|.
name|incrDatanodeChooseSuccessCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|isFallbacked
condition|)
block|{
name|metrics
operator|.
name|incrDatanodeChooseFallbackCount
argument_list|()
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
else|else
block|{
name|maxRetry
operator|--
expr_stmt|;
if|if
condition|(
name|maxRetry
operator|==
literal|0
condition|)
block|{
comment|// avoid the infinite loop
name|String
name|errMsg
init|=
literal|"No satisfied datanode to meet the space constrains. "
operator|+
literal|" sizeRequired: "
operator|+
name|sizeRequired
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|errMsg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMException
argument_list|(
name|errMsg
argument_list|,
literal|null
argument_list|)
throw|;
block|}
if|if
condition|(
name|excludedNodesForCapacity
operator|==
literal|null
condition|)
block|{
name|excludedNodesForCapacity
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|excludedNodesForCapacity
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|excludedNodes
operator|==
literal|null
condition|)
block|{
name|excludedNodes
operator|=
name|excludedNodesForCapacity
expr_stmt|;
block|}
else|else
block|{
name|excludedNodes
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
comment|/**    * Choose a batch of datanodes on different rack than excludedNodes or    * chosenNodes.    *    *    * @param excludedNodes - list of the datanodes to excluded. Can be null.    * @param chosenNodes - list of nodes already chosen. These nodes should also    *                    be excluded. Cannot be null.    * @param favoredNodes - list of favoredNodes. It's a hint. Whether the nodes    *                     are chosen depends on whether they meet the constrains.    *                     Can be null.    * @param favorIndex - the node index of favoredNodes which is not chosen yet.    * @param sizeRequired - size required for the container or block.    * @param nodesRequired - number of datanodes required.    * @param sizeRequired - size required for the container or block.    * @return List of chosen datanodes.    * @throws SCMException  SCMException    */
DECL|method|chooseNodes (List<Node> excludedNodes, List<Node> chosenNodes, List<DatanodeDetails> favoredNodes, int favorIndex, int nodesRequired, long sizeRequired)
specifier|private
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|chooseNodes
parameter_list|(
name|List
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|chosenNodes
parameter_list|,
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|favoredNodes
parameter_list|,
name|int
name|favorIndex
parameter_list|,
name|int
name|nodesRequired
parameter_list|,
name|long
name|sizeRequired
parameter_list|)
throws|throws
name|SCMException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|chosenNodes
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|excludedNodeList
init|=
name|excludedNodes
operator|!=
literal|null
condition|?
name|excludedNodes
else|:
name|chosenNodes
decl_stmt|;
name|int
name|favoredNodeNum
init|=
name|favoredNodes
operator|==
literal|null
condition|?
literal|0
else|:
name|favoredNodes
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Node
name|favoredNode
init|=
name|favoredNodeNum
operator|>
name|favorIndex
condition|?
name|favoredNodes
operator|.
name|get
argument_list|(
name|favorIndex
argument_list|)
else|:
literal|null
decl_stmt|;
name|Node
name|chosenNode
decl_stmt|;
if|if
condition|(
name|favoredNode
operator|!=
literal|null
operator|&&
name|networkTopology
operator|.
name|isSameParent
argument_list|(
name|excludedNodeList
operator|.
name|get
argument_list|(
name|excludedNodeList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|favoredNode
argument_list|)
condition|)
block|{
name|chosenNode
operator|=
name|favoredNode
expr_stmt|;
name|favorIndex
operator|++
expr_stmt|;
block|}
else|else
block|{
name|chosenNode
operator|=
name|chooseNode
argument_list|(
name|excludedNodeList
argument_list|,
literal|null
argument_list|,
name|sizeRequired
argument_list|)
expr_stmt|;
block|}
name|excludedNodeList
operator|.
name|add
argument_list|(
name|chosenNode
argument_list|)
expr_stmt|;
if|if
condition|(
name|excludedNodeList
operator|!=
name|chosenNodes
condition|)
block|{
name|chosenNodes
operator|.
name|add
argument_list|(
name|chosenNode
argument_list|)
expr_stmt|;
block|}
name|nodesRequired
operator|--
expr_stmt|;
if|if
condition|(
name|nodesRequired
operator|==
literal|0
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|chosenNodes
operator|.
name|toArray
argument_list|(
operator|new
name|DatanodeDetails
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

