begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|fs
operator|.
name|StorageType
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
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
name|net
operator|.
name|NetworkTopologyWithNodeGroup
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
name|net
operator|.
name|NodeBase
import|;
end_import

begin_comment
comment|/** The class is responsible for choosing the desired number of targets  * for placing block replicas on environment with node-group layer.  * The replica placement strategy is adjusted to:  * If the writer is on a datanode, the 1st replica is placed on the local   *     node(or local node-group or on local rack), otherwise a random datanode.  * The 2nd replica is placed on a datanode that is on a different rack with 1st  *     replica node.   * The 3rd replica is placed on a datanode which is on a different node-group  *     but the same rack as the second replica node.  */
end_comment

begin_class
DECL|class|BlockPlacementPolicyWithNodeGroup
specifier|public
class|class
name|BlockPlacementPolicyWithNodeGroup
extends|extends
name|BlockPlacementPolicyDefault
block|{
DECL|method|BlockPlacementPolicyWithNodeGroup ()
specifier|protected
name|BlockPlacementPolicyWithNodeGroup
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|initialize (Configuration conf, FSClusterStats stats, NetworkTopology clusterMap, Host2NodesMap host2datanodeMap)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSClusterStats
name|stats
parameter_list|,
name|NetworkTopology
name|clusterMap
parameter_list|,
name|Host2NodesMap
name|host2datanodeMap
parameter_list|)
block|{
name|super
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
name|stats
argument_list|,
name|clusterMap
argument_list|,
name|host2datanodeMap
argument_list|)
expr_stmt|;
block|}
comment|/**    * choose all good favored nodes as target.    * If no enough targets, then choose one replica from    * each bad favored node's node group.    * @throws NotEnoughReplicasException    */
annotation|@
name|Override
DECL|method|chooseFavouredNodes (String src, int numOfReplicas, List<DatanodeDescriptor> favoredNodes, Set<Node> favoriteAndExcludedNodes, long blocksize, int maxNodesPerRack, List<DatanodeStorageInfo> results, boolean avoidStaleNodes, EnumMap<StorageType, Integer> storageTypes)
specifier|protected
name|void
name|chooseFavouredNodes
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|numOfReplicas
parameter_list|,
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|favoredNodes
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|favoriteAndExcludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|int
name|maxNodesPerRack
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|,
name|boolean
name|avoidStaleNodes
parameter_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|storageTypes
parameter_list|)
throws|throws
name|NotEnoughReplicasException
block|{
name|super
operator|.
name|chooseFavouredNodes
argument_list|(
name|src
argument_list|,
name|numOfReplicas
argument_list|,
name|favoredNodes
argument_list|,
name|favoriteAndExcludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
if|if
condition|(
name|results
operator|.
name|size
argument_list|()
operator|<
name|numOfReplicas
condition|)
block|{
comment|// Not enough replicas, choose from unselected Favorednode's Nodegroup
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|favoredNodes
operator|.
name|size
argument_list|()
operator|&&
name|results
operator|.
name|size
argument_list|()
operator|<
name|numOfReplicas
condition|;
name|i
operator|++
control|)
block|{
name|DatanodeDescriptor
name|favoredNode
init|=
name|favoredNodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|boolean
name|chosenNode
init|=
name|isNodeChosen
argument_list|(
name|results
argument_list|,
name|favoredNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|chosenNode
condition|)
block|{
continue|continue;
block|}
name|NetworkTopologyWithNodeGroup
name|clusterMapNodeGroup
init|=
operator|(
name|NetworkTopologyWithNodeGroup
operator|)
name|clusterMap
decl_stmt|;
comment|// try a node on FavouredNode's node group
name|DatanodeStorageInfo
name|target
init|=
literal|null
decl_stmt|;
name|String
name|scope
init|=
name|clusterMapNodeGroup
operator|.
name|getNodeGroup
argument_list|(
name|favoredNode
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|target
operator|=
name|chooseRandom
argument_list|(
name|scope
argument_list|,
name|favoriteAndExcludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotEnoughReplicasException
name|e
parameter_list|)
block|{
comment|// catch Exception and continue with other favored nodes
continue|continue;
block|}
if|if
condition|(
name|target
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not find a target for file "
operator|+
name|src
operator|+
literal|" within nodegroup of favored node "
operator|+
name|favoredNode
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|favoriteAndExcludedNodes
operator|.
name|add
argument_list|(
name|target
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isNodeChosen ( List<DatanodeStorageInfo> results, DatanodeDescriptor favoredNode)
specifier|private
name|boolean
name|isNodeChosen
parameter_list|(
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|,
name|DatanodeDescriptor
name|favoredNode
parameter_list|)
block|{
name|boolean
name|chosenNode
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|results
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|results
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|equals
argument_list|(
name|favoredNode
argument_list|)
condition|)
block|{
name|chosenNode
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|chosenNode
return|;
block|}
comment|/** choose local node of<i>localMachine</i> as the target.    * If localMachine is not available, will fallback to nodegroup/rack    * when flag<i>fallbackToNodeGroupAndLocalRack</i> is set.    * @return the chosen node    */
annotation|@
name|Override
DECL|method|chooseLocalStorage (Node localMachine, Set<Node> excludedNodes, long blocksize, int maxNodesPerRack, List<DatanodeStorageInfo> results, boolean avoidStaleNodes, EnumMap<StorageType, Integer> storageTypes, boolean fallbackToNodeGroupAndLocalRack)
specifier|protected
name|DatanodeStorageInfo
name|chooseLocalStorage
parameter_list|(
name|Node
name|localMachine
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|int
name|maxNodesPerRack
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|,
name|boolean
name|avoidStaleNodes
parameter_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|storageTypes
parameter_list|,
name|boolean
name|fallbackToNodeGroupAndLocalRack
parameter_list|)
throws|throws
name|NotEnoughReplicasException
block|{
name|DatanodeStorageInfo
name|localStorage
init|=
name|chooseLocalStorage
argument_list|(
name|localMachine
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
decl_stmt|;
if|if
condition|(
name|localStorage
operator|!=
literal|null
condition|)
block|{
return|return
name|localStorage
return|;
block|}
if|if
condition|(
operator|!
name|fallbackToNodeGroupAndLocalRack
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// try a node on local node group
name|DatanodeStorageInfo
name|chosenStorage
init|=
name|chooseLocalNodeGroup
argument_list|(
operator|(
name|NetworkTopologyWithNodeGroup
operator|)
name|clusterMap
argument_list|,
name|localMachine
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
decl_stmt|;
if|if
condition|(
name|chosenStorage
operator|!=
literal|null
condition|)
block|{
return|return
name|chosenStorage
return|;
block|}
comment|// try a node on local rack
return|return
name|chooseLocalRack
argument_list|(
name|localMachine
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
comment|/** @return the node of the second replica */
DECL|method|secondNode (Node localMachine, List<DatanodeStorageInfo> results)
specifier|private
specifier|static
name|DatanodeDescriptor
name|secondNode
parameter_list|(
name|Node
name|localMachine
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|)
block|{
comment|// find the second replica
for|for
control|(
name|DatanodeStorageInfo
name|nextStorage
range|:
name|results
control|)
block|{
name|DatanodeDescriptor
name|nextNode
init|=
name|nextStorage
operator|.
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextNode
operator|!=
name|localMachine
condition|)
block|{
return|return
name|nextNode
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|chooseLocalRack (Node localMachine, Set<Node> excludedNodes, long blocksize, int maxNodesPerRack, List<DatanodeStorageInfo> results, boolean avoidStaleNodes, EnumMap<StorageType, Integer> storageTypes)
specifier|protected
name|DatanodeStorageInfo
name|chooseLocalRack
parameter_list|(
name|Node
name|localMachine
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|int
name|maxNodesPerRack
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|,
name|boolean
name|avoidStaleNodes
parameter_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|storageTypes
parameter_list|)
throws|throws
name|NotEnoughReplicasException
block|{
comment|// no local machine, so choose a random machine
if|if
condition|(
name|localMachine
operator|==
literal|null
condition|)
block|{
return|return
name|chooseRandom
argument_list|(
name|NodeBase
operator|.
name|ROOT
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
comment|// choose one from the local rack, but off-nodegroup
try|try
block|{
specifier|final
name|String
name|scope
init|=
name|NetworkTopology
operator|.
name|getFirstHalf
argument_list|(
name|localMachine
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|chooseRandom
argument_list|(
name|scope
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NotEnoughReplicasException
name|e1
parameter_list|)
block|{
comment|// find the second replica
specifier|final
name|DatanodeDescriptor
name|newLocal
init|=
name|secondNode
argument_list|(
name|localMachine
argument_list|,
name|results
argument_list|)
decl_stmt|;
if|if
condition|(
name|newLocal
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|chooseRandom
argument_list|(
name|clusterMap
operator|.
name|getRack
argument_list|(
name|newLocal
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NotEnoughReplicasException
name|e2
parameter_list|)
block|{
comment|//otherwise randomly choose one from the network
return|return
name|chooseRandom
argument_list|(
name|NodeBase
operator|.
name|ROOT
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|//otherwise randomly choose one from the network
return|return
name|chooseRandom
argument_list|(
name|NodeBase
operator|.
name|ROOT
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|chooseRemoteRack (int numOfReplicas, DatanodeDescriptor localMachine, Set<Node> excludedNodes, long blocksize, int maxReplicasPerRack, List<DatanodeStorageInfo> results, boolean avoidStaleNodes, EnumMap<StorageType, Integer> storageTypes)
specifier|protected
name|void
name|chooseRemoteRack
parameter_list|(
name|int
name|numOfReplicas
parameter_list|,
name|DatanodeDescriptor
name|localMachine
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|int
name|maxReplicasPerRack
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|,
name|boolean
name|avoidStaleNodes
parameter_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|storageTypes
parameter_list|)
throws|throws
name|NotEnoughReplicasException
block|{
name|int
name|oldNumOfReplicas
init|=
name|results
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|String
name|rackLocation
init|=
name|NetworkTopology
operator|.
name|getFirstHalf
argument_list|(
name|localMachine
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
comment|// randomly choose from remote racks
name|chooseRandom
argument_list|(
name|numOfReplicas
argument_list|,
literal|"~"
operator|+
name|rackLocation
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxReplicasPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotEnoughReplicasException
name|e
parameter_list|)
block|{
comment|// fall back to the local rack
name|chooseRandom
argument_list|(
name|numOfReplicas
operator|-
operator|(
name|results
operator|.
name|size
argument_list|()
operator|-
name|oldNumOfReplicas
operator|)
argument_list|,
name|rackLocation
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxReplicasPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* choose one node from the nodegroup that<i>localMachine</i> is on.    * if no such node is available, choose one node from the nodegroup where    * a second replica is on.    * if still no such node is available, return null.    * @return the chosen node    */
DECL|method|chooseLocalNodeGroup ( NetworkTopologyWithNodeGroup clusterMap, Node localMachine, Set<Node> excludedNodes, long blocksize, int maxNodesPerRack, List<DatanodeStorageInfo> results, boolean avoidStaleNodes, EnumMap<StorageType, Integer> storageTypes)
specifier|private
name|DatanodeStorageInfo
name|chooseLocalNodeGroup
parameter_list|(
name|NetworkTopologyWithNodeGroup
name|clusterMap
parameter_list|,
name|Node
name|localMachine
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|int
name|maxNodesPerRack
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|results
parameter_list|,
name|boolean
name|avoidStaleNodes
parameter_list|,
name|EnumMap
argument_list|<
name|StorageType
argument_list|,
name|Integer
argument_list|>
name|storageTypes
parameter_list|)
throws|throws
name|NotEnoughReplicasException
block|{
comment|// no local machine, so choose a random machine
if|if
condition|(
name|localMachine
operator|==
literal|null
condition|)
block|{
return|return
name|chooseRandom
argument_list|(
name|NodeBase
operator|.
name|ROOT
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
comment|// choose one from the local node group
try|try
block|{
return|return
name|chooseRandom
argument_list|(
name|clusterMap
operator|.
name|getNodeGroup
argument_list|(
name|localMachine
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NotEnoughReplicasException
name|e1
parameter_list|)
block|{
specifier|final
name|DatanodeDescriptor
name|newLocal
init|=
name|secondNode
argument_list|(
name|localMachine
argument_list|,
name|results
argument_list|)
decl_stmt|;
if|if
condition|(
name|newLocal
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|chooseRandom
argument_list|(
name|clusterMap
operator|.
name|getNodeGroup
argument_list|(
name|newLocal
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|maxNodesPerRack
argument_list|,
name|results
argument_list|,
name|avoidStaleNodes
argument_list|,
name|storageTypes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NotEnoughReplicasException
name|e2
parameter_list|)
block|{
comment|//otherwise return null
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
comment|//otherwise return null
return|return
literal|null
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getRack (final DatanodeInfo cur)
specifier|protected
name|String
name|getRack
parameter_list|(
specifier|final
name|DatanodeInfo
name|cur
parameter_list|)
block|{
name|String
name|nodeGroupString
init|=
name|cur
operator|.
name|getNetworkLocation
argument_list|()
decl_stmt|;
return|return
name|NetworkTopology
operator|.
name|getFirstHalf
argument_list|(
name|nodeGroupString
argument_list|)
return|;
block|}
comment|/**    * Find other nodes in the same nodegroup of<i>localMachine</i> and add them    * into<i>excludeNodes</i> as replica should not be duplicated for nodes     * within the same nodegroup    * @return number of new excluded nodes    */
annotation|@
name|Override
DECL|method|addToExcludedNodes (DatanodeDescriptor chosenNode, Set<Node> excludedNodes)
specifier|protected
name|int
name|addToExcludedNodes
parameter_list|(
name|DatanodeDescriptor
name|chosenNode
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|)
block|{
name|int
name|countOfExcludedNodes
init|=
literal|0
decl_stmt|;
name|String
name|nodeGroupScope
init|=
name|chosenNode
operator|.
name|getNetworkLocation
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|leafNodes
init|=
name|clusterMap
operator|.
name|getLeaves
argument_list|(
name|nodeGroupScope
argument_list|)
decl_stmt|;
for|for
control|(
name|Node
name|leafNode
range|:
name|leafNodes
control|)
block|{
if|if
condition|(
name|excludedNodes
operator|.
name|add
argument_list|(
name|leafNode
argument_list|)
condition|)
block|{
comment|// not a existing node in excludedNodes
name|countOfExcludedNodes
operator|++
expr_stmt|;
block|}
block|}
name|countOfExcludedNodes
operator|+=
name|addDependentNodesToExcludedNodes
argument_list|(
name|chosenNode
argument_list|,
name|excludedNodes
argument_list|)
expr_stmt|;
return|return
name|countOfExcludedNodes
return|;
block|}
comment|/**    * Add all nodes from a dependent nodes list to excludedNodes.    * @return number of new excluded nodes    */
DECL|method|addDependentNodesToExcludedNodes (DatanodeDescriptor chosenNode, Set<Node> excludedNodes)
specifier|private
name|int
name|addDependentNodesToExcludedNodes
parameter_list|(
name|DatanodeDescriptor
name|chosenNode
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|host2datanodeMap
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|countOfExcludedNodes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|hostname
range|:
name|chosenNode
operator|.
name|getDependentHostNames
argument_list|()
control|)
block|{
name|DatanodeDescriptor
name|node
init|=
name|this
operator|.
name|host2datanodeMap
operator|.
name|getDataNodeByHostName
argument_list|(
name|hostname
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|excludedNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|countOfExcludedNodes
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Not able to find datanode "
operator|+
name|hostname
operator|+
literal|" which has dependency with datanode "
operator|+
name|chosenNode
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|countOfExcludedNodes
return|;
block|}
comment|/**    * Pick up replica node set for deleting replica as over-replicated.     * First set contains replica nodes on rack with more than one    * replica while second set contains remaining replica nodes.    * If first is not empty, divide first set into two subsets:    *   moreThanOne contains nodes on nodegroup with more than one replica    *   exactlyOne contains the remaining nodes in first set    * then pickup priSet if not empty.    * If first is empty, then pick second.    */
annotation|@
name|Override
DECL|method|pickupReplicaSet ( Collection<DatanodeStorageInfo> first, Collection<DatanodeStorageInfo> second, Map<String, List<DatanodeStorageInfo>> rackMap)
specifier|public
name|Collection
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|pickupReplicaSet
parameter_list|(
name|Collection
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|first
parameter_list|,
name|Collection
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|second
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|>
name|rackMap
parameter_list|)
block|{
comment|// If no replica within same rack, return directly.
if|if
condition|(
name|first
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|second
return|;
block|}
comment|// Split data nodes in the first set into two sets,
comment|// moreThanOne contains nodes on nodegroup with more than one replica
comment|// exactlyOne contains the remaining nodes
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|>
name|nodeGroupMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeStorageInfo
name|storage
range|:
name|first
control|)
block|{
specifier|final
name|String
name|nodeGroupName
init|=
name|NetworkTopology
operator|.
name|getLastHalf
argument_list|(
name|storage
operator|.
name|getDatanodeDescriptor
argument_list|()
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storageList
init|=
name|nodeGroupMap
operator|.
name|get
argument_list|(
name|nodeGroupName
argument_list|)
decl_stmt|;
if|if
condition|(
name|storageList
operator|==
literal|null
condition|)
block|{
name|storageList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|nodeGroupMap
operator|.
name|put
argument_list|(
name|nodeGroupName
argument_list|,
name|storageList
argument_list|)
expr_stmt|;
block|}
name|storageList
operator|.
name|add
argument_list|(
name|storage
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|moreThanOne
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|exactlyOne
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// split nodes into two sets
for|for
control|(
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|datanodeList
range|:
name|nodeGroupMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|datanodeList
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// exactlyOne contains nodes on nodegroup with exactly one replica
name|exactlyOne
operator|.
name|add
argument_list|(
name|datanodeList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// moreThanOne contains nodes on nodegroup with more than one replica
name|moreThanOne
operator|.
name|addAll
argument_list|(
name|datanodeList
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|moreThanOne
operator|.
name|isEmpty
argument_list|()
condition|?
name|exactlyOne
else|:
name|moreThanOne
return|;
block|}
comment|/**    * Check if there are any replica (other than source) on the same node group    * with target. If true, then target is not a good candidate for placing    * specific replica as we don't want 2 replicas under the same nodegroup.    *    * @return true if there are any replica (other than source) on the same node    *         group with target    */
annotation|@
name|Override
DECL|method|isMovable (Collection<DatanodeInfo> locs, DatanodeInfo source, DatanodeInfo target)
specifier|public
name|boolean
name|isMovable
parameter_list|(
name|Collection
argument_list|<
name|DatanodeInfo
argument_list|>
name|locs
parameter_list|,
name|DatanodeInfo
name|source
parameter_list|,
name|DatanodeInfo
name|target
parameter_list|)
block|{
for|for
control|(
name|DatanodeInfo
name|dn
range|:
name|locs
control|)
block|{
if|if
condition|(
name|dn
operator|!=
name|source
operator|&&
name|dn
operator|!=
name|target
operator|&&
name|clusterMap
operator|.
name|isOnSameNodeGroup
argument_list|(
name|dn
argument_list|,
name|target
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|verifyBlockPlacement (DatanodeInfo[] locs, int numberOfReplicas)
specifier|public
name|BlockPlacementStatus
name|verifyBlockPlacement
parameter_list|(
name|DatanodeInfo
index|[]
name|locs
parameter_list|,
name|int
name|numberOfReplicas
parameter_list|)
block|{
if|if
condition|(
name|locs
operator|==
literal|null
condition|)
block|{
name|locs
operator|=
name|DatanodeDescriptor
operator|.
name|EMPTY_ARRAY
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|locList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/*      * remove the part of node group for BlockPlacementPolicyDefault to count      * distinct racks, e.g. "/d1/r1/n1" --> "/d1/r1"      */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|locs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|locList
operator|.
name|add
argument_list|(
name|locs
index|[
name|i
index|]
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
name|locs
index|[
name|i
index|]
operator|.
name|setNetworkLocation
argument_list|(
name|NetworkTopology
operator|.
name|getFirstHalf
argument_list|(
name|locs
index|[
name|i
index|]
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BlockPlacementStatus
name|defaultStatus
init|=
name|super
operator|.
name|verifyBlockPlacement
argument_list|(
name|locs
argument_list|,
name|numberOfReplicas
argument_list|)
decl_stmt|;
comment|// restore the part of node group back
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|locs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|locs
index|[
name|i
index|]
operator|.
name|setNetworkLocation
argument_list|(
name|locList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|minNodeGroups
init|=
name|numberOfReplicas
decl_stmt|;
name|BlockPlacementStatusWithNodeGroup
name|nodeGroupStatus
init|=
operator|new
name|BlockPlacementStatusWithNodeGroup
argument_list|(
name|defaultStatus
argument_list|,
name|getNodeGroupsFromNode
argument_list|(
name|locs
argument_list|)
argument_list|,
name|minNodeGroups
argument_list|)
decl_stmt|;
return|return
name|nodeGroupStatus
return|;
block|}
DECL|method|getNodeGroupsFromNode (DatanodeInfo[] nodes)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeGroupsFromNode
parameter_list|(
name|DatanodeInfo
index|[]
name|nodes
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodeGroups
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
return|return
name|nodeGroups
return|;
block|}
for|for
control|(
name|DatanodeInfo
name|node
range|:
name|nodes
control|)
block|{
name|nodeGroups
operator|.
name|add
argument_list|(
name|NetworkTopology
operator|.
name|getLastHalf
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeGroups
return|;
block|}
block|}
end_class

end_unit

