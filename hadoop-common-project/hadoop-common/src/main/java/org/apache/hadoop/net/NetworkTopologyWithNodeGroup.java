begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * The class extends NetworkTopology to represents a cluster of computer with  *  a 4-layers hierarchical network topology.  * In this network topology, leaves represent data nodes (computers) and inner  * nodes represent switches/routers that manage traffic in/out of data centers,  * racks or physical host (with virtual switch).  *   * @see NetworkTopology  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|NetworkTopologyWithNodeGroup
specifier|public
class|class
name|NetworkTopologyWithNodeGroup
extends|extends
name|NetworkTopology
block|{
DECL|field|DEFAULT_NODEGROUP
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_NODEGROUP
init|=
literal|"/default-nodegroup"
decl_stmt|;
DECL|method|NetworkTopologyWithNodeGroup ()
specifier|public
name|NetworkTopologyWithNodeGroup
parameter_list|()
block|{
name|clusterMap
operator|=
operator|new
name|InnerNodeWithNodeGroup
argument_list|(
name|InnerNode
operator|.
name|ROOT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNodeForNetworkLocation (Node node)
specifier|protected
name|Node
name|getNodeForNetworkLocation
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
comment|// if node only with default rack info, here we need to add default
comment|// nodegroup info
if|if
condition|(
name|NetworkTopology
operator|.
name|DEFAULT_RACK
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
condition|)
block|{
name|node
operator|.
name|setNetworkLocation
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
operator|+
name|DEFAULT_NODEGROUP
argument_list|)
expr_stmt|;
block|}
name|Node
name|nodeGroup
init|=
name|getNode
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeGroup
operator|==
literal|null
condition|)
block|{
name|nodeGroup
operator|=
operator|new
name|InnerNodeWithNodeGroup
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|getNode
argument_list|(
name|nodeGroup
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRack (String loc)
specifier|public
name|String
name|getRack
parameter_list|(
name|String
name|loc
parameter_list|)
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|loc
operator|=
name|InnerNode
operator|.
name|normalize
argument_list|(
name|loc
argument_list|)
expr_stmt|;
name|Node
name|locNode
init|=
name|getNode
argument_list|(
name|loc
argument_list|)
decl_stmt|;
if|if
condition|(
name|locNode
operator|instanceof
name|InnerNodeWithNodeGroup
condition|)
block|{
name|InnerNodeWithNodeGroup
name|node
init|=
operator|(
name|InnerNodeWithNodeGroup
operator|)
name|locNode
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isRack
argument_list|()
condition|)
block|{
return|return
name|loc
return|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isNodeGroup
argument_list|()
condition|)
block|{
return|return
name|node
operator|.
name|getNetworkLocation
argument_list|()
return|;
block|}
else|else
block|{
comment|// may be a data center
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
comment|// not in cluster map, don't handle it
return|return
name|loc
return|;
block|}
block|}
finally|finally
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Given a string representation of a node group for a specific network    * location    *     * @param loc    *            a path-like string representation of a network location    * @return a node group string    */
DECL|method|getNodeGroup (String loc)
specifier|public
name|String
name|getNodeGroup
parameter_list|(
name|String
name|loc
parameter_list|)
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|loc
operator|=
name|InnerNode
operator|.
name|normalize
argument_list|(
name|loc
argument_list|)
expr_stmt|;
name|Node
name|locNode
init|=
name|getNode
argument_list|(
name|loc
argument_list|)
decl_stmt|;
if|if
condition|(
name|locNode
operator|instanceof
name|InnerNodeWithNodeGroup
condition|)
block|{
name|InnerNodeWithNodeGroup
name|node
init|=
operator|(
name|InnerNodeWithNodeGroup
operator|)
name|locNode
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isNodeGroup
argument_list|()
condition|)
block|{
return|return
name|loc
return|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isRack
argument_list|()
condition|)
block|{
comment|// not sure the node group for a rack
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// may be a leaf node
return|return
name|getNodeGroup
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// not in cluster map, don't handle it
return|return
name|loc
return|;
block|}
block|}
finally|finally
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|isOnSameRack ( Node node1, Node node2)
specifier|public
name|boolean
name|isOnSameRack
parameter_list|(
name|Node
name|node1
parameter_list|,
name|Node
name|node2
parameter_list|)
block|{
if|if
condition|(
name|node1
operator|==
literal|null
operator|||
name|node2
operator|==
literal|null
operator|||
name|node1
operator|.
name|getParent
argument_list|()
operator|==
literal|null
operator|||
name|node2
operator|.
name|getParent
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|isSameParents
argument_list|(
name|node1
operator|.
name|getParent
argument_list|()
argument_list|,
name|node2
operator|.
name|getParent
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Check if two nodes are on the same node group (hypervisor) The    * assumption here is: each nodes are leaf nodes.    *     * @param node1    *            one node (can be null)    * @param node2    *            another node (can be null)    * @return true if node1 and node2 are on the same node group; false    *         otherwise    * @exception IllegalArgumentException    *                when either node1 or node2 is null, or node1 or node2 do    *                not belong to the cluster    */
annotation|@
name|Override
DECL|method|isOnSameNodeGroup (Node node1, Node node2)
specifier|public
name|boolean
name|isOnSameNodeGroup
parameter_list|(
name|Node
name|node1
parameter_list|,
name|Node
name|node2
parameter_list|)
block|{
if|if
condition|(
name|node1
operator|==
literal|null
operator|||
name|node2
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|isSameParents
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
return|;
block|}
finally|finally
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Check if network topology is aware of NodeGroup    */
annotation|@
name|Override
DECL|method|isNodeGroupAware ()
specifier|public
name|boolean
name|isNodeGroupAware
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Add a leaf node    * Update node counter& rack counter if necessary    * @param node node to be added; can be null    * @exception IllegalArgumentException if add a node to a leave     *                                     or node to be added is not a leaf    */
annotation|@
name|Override
DECL|method|add (Node node)
specifier|public
name|void
name|add
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|node
operator|instanceof
name|InnerNode
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not allow to add an inner node: "
operator|+
name|NodeBase
operator|.
name|getPath
argument_list|(
name|node
argument_list|)
argument_list|)
throw|;
block|}
name|netlock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|Node
name|rack
init|=
literal|null
decl_stmt|;
comment|// if node only with default rack info, here we need to add default
comment|// nodegroup info
if|if
condition|(
name|NetworkTopology
operator|.
name|DEFAULT_RACK
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
condition|)
block|{
name|node
operator|.
name|setNetworkLocation
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
operator|+
name|NetworkTopologyWithNodeGroup
operator|.
name|DEFAULT_NODEGROUP
argument_list|)
expr_stmt|;
block|}
name|Node
name|nodeGroup
init|=
name|getNode
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeGroup
operator|==
literal|null
condition|)
block|{
name|nodeGroup
operator|=
operator|new
name|InnerNodeWithNodeGroup
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rack
operator|=
name|getNode
argument_list|(
name|nodeGroup
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
comment|// rack should be an innerNode and with parent.
comment|// note: rack's null parent case is: node's topology only has one layer,
comment|//       so rack is recognized as "/" and no parent.
comment|// This will be recognized as a node with fault topology.
if|if
condition|(
name|rack
operator|!=
literal|null
operator|&&
operator|(
operator|!
operator|(
name|rack
operator|instanceof
name|InnerNode
operator|)
operator|||
name|rack
operator|.
name|getParent
argument_list|()
operator|==
literal|null
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected data node "
operator|+
name|node
operator|.
name|toString
argument_list|()
operator|+
literal|" at an illegal network location"
argument_list|)
throw|;
block|}
if|if
condition|(
name|clusterMap
operator|.
name|add
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding a new node: "
operator|+
name|NodeBase
operator|.
name|getPath
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rack
operator|==
literal|null
condition|)
block|{
comment|// We only track rack number here
name|numOfRacks
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NetworkTopology became:\n"
operator|+
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|netlock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Remove a node    * Update node counter and rack counter if necessary    * @param node node to be removed; can be null    */
annotation|@
name|Override
DECL|method|remove (Node node)
specifier|public
name|void
name|remove
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|node
operator|instanceof
name|InnerNode
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not allow to remove an inner node: "
operator|+
name|NodeBase
operator|.
name|getPath
argument_list|(
name|node
argument_list|)
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing a node: "
operator|+
name|NodeBase
operator|.
name|getPath
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|netlock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|clusterMap
operator|.
name|remove
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|Node
name|nodeGroup
init|=
name|getNode
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeGroup
operator|==
literal|null
condition|)
block|{
name|nodeGroup
operator|=
operator|new
name|InnerNode
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|InnerNode
name|rack
init|=
operator|(
name|InnerNode
operator|)
name|getNode
argument_list|(
name|nodeGroup
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rack
operator|==
literal|null
condition|)
block|{
name|numOfRacks
operator|--
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NetworkTopology became:\n"
operator|+
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|netlock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getWeight (Node reader, Node node)
specifier|protected
name|int
name|getWeight
parameter_list|(
name|Node
name|reader
parameter_list|,
name|Node
name|node
parameter_list|)
block|{
comment|// 0 is local, 1 is same node group, 2 is same rack, 3 is off rack
comment|// Start off by initializing to off rack
name|int
name|weight
init|=
literal|3
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|reader
operator|==
name|node
condition|)
block|{
name|weight
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isOnSameNodeGroup
argument_list|(
name|reader
argument_list|,
name|node
argument_list|)
condition|)
block|{
name|weight
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isOnSameRack
argument_list|(
name|reader
argument_list|,
name|node
argument_list|)
condition|)
block|{
name|weight
operator|=
literal|2
expr_stmt|;
block|}
block|}
return|return
name|weight
return|;
block|}
comment|/**    * Sort nodes array by their distances to<i>reader</i>.    *<p/>    * This is the same as    * {@link NetworkTopology#sortByDistance(Node, Node[], long)} except with a    * four-level network topology which contains the additional network distance    * of a "node group" which is between local and same rack.    *     * @param reader Node where data will be read    * @param nodes Available replicas with the requested data    * @param seed Used to seed the pseudo-random generator that randomizes the    *          set of nodes at each network distance.    */
annotation|@
name|Override
DECL|method|sortByDistance ( Node reader, Node[] nodes, long seed)
specifier|public
name|void
name|sortByDistance
parameter_list|(
name|Node
name|reader
parameter_list|,
name|Node
index|[]
name|nodes
parameter_list|,
name|long
name|seed
parameter_list|)
block|{
comment|// If reader is not a datanode (not in NetworkTopology tree), we need to
comment|// replace this reader with a sibling leaf node in tree.
if|if
condition|(
name|reader
operator|!=
literal|null
operator|&&
operator|!
name|this
operator|.
name|contains
argument_list|(
name|reader
argument_list|)
condition|)
block|{
name|Node
name|nodeGroup
init|=
name|getNode
argument_list|(
name|reader
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeGroup
operator|!=
literal|null
operator|&&
name|nodeGroup
operator|instanceof
name|InnerNode
condition|)
block|{
name|InnerNode
name|parentNode
init|=
operator|(
name|InnerNode
operator|)
name|nodeGroup
decl_stmt|;
comment|// replace reader with the first children of its parent in tree
name|reader
operator|=
name|parentNode
operator|.
name|getLeaf
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
block|}
name|super
operator|.
name|sortByDistance
argument_list|(
name|reader
argument_list|,
name|nodes
argument_list|,
name|seed
argument_list|)
expr_stmt|;
block|}
comment|/** InnerNodeWithNodeGroup represents a switch/router of a data center, rack    * or physical host. Different from a leaf node, it has non-null children.    */
DECL|class|InnerNodeWithNodeGroup
specifier|static
class|class
name|InnerNodeWithNodeGroup
extends|extends
name|InnerNode
block|{
DECL|method|InnerNodeWithNodeGroup (String name, String location, InnerNode parent, int level)
specifier|public
name|InnerNodeWithNodeGroup
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|location
parameter_list|,
name|InnerNode
name|parent
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|location
argument_list|,
name|parent
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
DECL|method|InnerNodeWithNodeGroup (String name, String location)
specifier|public
name|InnerNodeWithNodeGroup
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
DECL|method|InnerNodeWithNodeGroup (String path)
specifier|public
name|InnerNodeWithNodeGroup
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isRack ()
name|boolean
name|isRack
parameter_list|()
block|{
comment|// it is node group
if|if
condition|(
name|getChildren
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Node
name|firstChild
init|=
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstChild
operator|instanceof
name|InnerNode
condition|)
block|{
name|Node
name|firstGrandChild
init|=
operator|(
operator|(
operator|(
name|InnerNode
operator|)
name|firstChild
operator|)
operator|.
name|children
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstGrandChild
operator|instanceof
name|InnerNode
condition|)
block|{
comment|// it is datacenter
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Judge if this node represents a node group      *       * @return true if it has no child or its children are not InnerNodes      */
DECL|method|isNodeGroup ()
name|boolean
name|isNodeGroup
parameter_list|()
block|{
if|if
condition|(
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Node
name|firstChild
init|=
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstChild
operator|instanceof
name|InnerNode
condition|)
block|{
comment|// it is rack or datacenter
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|isLeafParent ()
specifier|protected
name|boolean
name|isLeafParent
parameter_list|()
block|{
return|return
name|isNodeGroup
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createParentNode (String parentName)
specifier|protected
name|InnerNode
name|createParentNode
parameter_list|(
name|String
name|parentName
parameter_list|)
block|{
return|return
operator|new
name|InnerNodeWithNodeGroup
argument_list|(
name|parentName
argument_list|,
name|getPath
argument_list|(
name|this
argument_list|)
argument_list|,
name|this
argument_list|,
name|this
operator|.
name|getLevel
argument_list|()
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|areChildrenLeaves ()
specifier|protected
name|boolean
name|areChildrenLeaves
parameter_list|()
block|{
return|return
name|isNodeGroup
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

