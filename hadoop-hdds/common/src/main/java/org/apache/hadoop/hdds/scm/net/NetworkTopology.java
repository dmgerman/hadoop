begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.net
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
name|net
package|;
end_package

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
name|List
import|;
end_import

begin_comment
comment|/**  * The interface defines a network topology.  */
end_comment

begin_interface
DECL|interface|NetworkTopology
specifier|public
interface|interface
name|NetworkTopology
block|{
comment|/** Exception for invalid network topology detection. */
DECL|class|InvalidTopologyException
class|class
name|InvalidTopologyException
extends|extends
name|RuntimeException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|InvalidTopologyException (String msg)
specifier|public
name|InvalidTopologyException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Add a leaf node. This will be called when a new datanode is added.    * @param node node to be added; can be null    * @exception IllegalArgumentException if add a node to a leave or node to be    * added is not a leaf    */
DECL|method|add (Node node)
name|void
name|add
parameter_list|(
name|Node
name|node
parameter_list|)
function_decl|;
comment|/**    * Remove a node from the network topology. This will be called when a    * existing datanode is removed from the system.    * @param node node to be removed; cannot be null    */
DECL|method|remove (Node node)
name|void
name|remove
parameter_list|(
name|Node
name|node
parameter_list|)
function_decl|;
comment|/**    * Check if the tree already contains node<i>node</i>.    * @param node a node    * @return true if<i>node</i> is already in the tree; false otherwise    */
DECL|method|contains (Node node)
name|boolean
name|contains
parameter_list|(
name|Node
name|node
parameter_list|)
function_decl|;
comment|/**    * Compare the direct parent of each node for equality.    * @return true if their parent are the same    */
DECL|method|isSameParent (Node node1, Node node2)
name|boolean
name|isSameParent
parameter_list|(
name|Node
name|node1
parameter_list|,
name|Node
name|node2
parameter_list|)
function_decl|;
comment|/**    * Compare the specified ancestor generation of each node for equality.    * ancestorGen 1 means parent.    * @return true if their specified generation ancestor are equal    */
DECL|method|isSameAncestor (Node node1, Node node2, int ancestorGen)
name|boolean
name|isSameAncestor
parameter_list|(
name|Node
name|node1
parameter_list|,
name|Node
name|node2
parameter_list|,
name|int
name|ancestorGen
parameter_list|)
function_decl|;
comment|/**    * Get the ancestor for node on generation<i>ancestorGen</i>.    *    * @param node the node to get ancestor    * @param ancestorGen  the ancestor generation    * @return the ancestor. If no ancestor is found, then null is returned.    */
DECL|method|getAncestor (Node node, int ancestorGen)
name|Node
name|getAncestor
parameter_list|(
name|Node
name|node
parameter_list|,
name|int
name|ancestorGen
parameter_list|)
function_decl|;
comment|/**    * Return the max level of this topology, start from 1 for ROOT. For example,    * topology like "/rack/node" has the max level '3'.    */
DECL|method|getMaxLevel ()
name|int
name|getMaxLevel
parameter_list|()
function_decl|;
comment|/**    * Given a string representation of a node, return its reference.    * @param loc a path string representing a node, can be leaf or inner node    * @return a reference to the node; null if the node is not in the tree    */
DECL|method|getNode (String loc)
name|Node
name|getNode
parameter_list|(
name|String
name|loc
parameter_list|)
function_decl|;
comment|/**    * Given a string representation of a InnerNode, return its leaf nodes count.    * @param loc a path-like string representation of a InnerNode    * @return the number of leaf nodes, 0 if it's not an InnerNode or the node    * doesn't exist    */
DECL|method|getNumOfLeafNode (String loc)
name|int
name|getNumOfLeafNode
parameter_list|(
name|String
name|loc
parameter_list|)
function_decl|;
comment|/**    * Return the node numbers at level<i>level</i>.    * @param level topology level, start from 1, which means ROOT    * @return the number of nodes on the level    */
DECL|method|getNumOfNodes (int level)
name|int
name|getNumOfNodes
parameter_list|(
name|int
name|level
parameter_list|)
function_decl|;
comment|/**    * Randomly choose a node in the scope.    * @param scope range of nodes from which a node will be chosen. If scope    *              starts with ~, choose one from the all nodes except for the    *              ones in<i>scope</i>; otherwise, choose one from<i>scope</i>.    * @return the chosen node    */
DECL|method|chooseRandom (String scope)
name|Node
name|chooseRandom
parameter_list|(
name|String
name|scope
parameter_list|)
function_decl|;
comment|/**    * Randomly choose a node in the scope, ano not in the exclude scope.    * @param scope range of nodes from which a node will be chosen. cannot start    *              with ~    * @param excludedScope the chosen node cannot be in this range. cannot    *                      starts with ~    * @return the chosen node    */
DECL|method|chooseRandom (String scope, String excludedScope)
name|Node
name|chooseRandom
parameter_list|(
name|String
name|scope
parameter_list|,
name|String
name|excludedScope
parameter_list|)
function_decl|;
comment|/**    * Randomly choose a leaf node from<i>scope</i>.    *    * If scope starts with ~, choose one from the all nodes except for the    * ones in<i>scope</i>; otherwise, choose nodes from<i>scope</i>.    * If excludedNodes is given, choose a node that's not in excludedNodes.    *    * @param scope range of nodes from which a node will be chosen    * @param excludedNodes nodes to be excluded    *    * @return the chosen node    */
DECL|method|chooseRandom (String scope, Collection<Node> excludedNodes)
name|Node
name|chooseRandom
parameter_list|(
name|String
name|scope
parameter_list|,
name|Collection
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|)
function_decl|;
comment|/**    * Randomly choose a leaf node from<i>scope</i>.    *    * If scope starts with ~, choose one from the all nodes except for the    * ones in<i>scope</i>; otherwise, choose nodes from<i>scope</i>.    * If excludedNodes is given, choose a node that's not in excludedNodes.    *    * @param scope range of nodes from which a node will be chosen    * @param excludedNodes nodes to be excluded from.    * @param ancestorGen matters when excludeNodes is not null. It means the    * ancestor generation that's not allowed to share between chosen node and the    * excludedNodes. For example, if ancestorGen is 1, means chosen node    * cannot share the same parent with excludeNodes. If value is 2, cannot    * share the same grand parent, and so on. If ancestorGen is 0, then no    * effect.    *    * @return the chosen node    */
DECL|method|chooseRandom (String scope, Collection<Node> excludedNodes, int ancestorGen)
name|Node
name|chooseRandom
parameter_list|(
name|String
name|scope
parameter_list|,
name|Collection
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|int
name|ancestorGen
parameter_list|)
function_decl|;
comment|/**    * Randomly choose a leaf node.    *    * @param scope range from which a node will be chosen, cannot start with ~    * @param excludedNodes nodes to be excluded    * @param excludedScope excluded node range. Cannot start with ~    * @param ancestorGen matters when excludeNodes is not null. It means the    * ancestor generation that's not allowed to share between chosen node and the    * excludedNodes. For example, if ancestorGen is 1, means chosen node    * cannot share the same parent with excludeNodes. If value is 2, cannot    * share the same grand parent, and so on. If ancestorGen is 0, then no    * effect.    *    * @return the chosen node    */
DECL|method|chooseRandom (String scope, String excludedScope, Collection<Node> excludedNodes, int ancestorGen)
name|Node
name|chooseRandom
parameter_list|(
name|String
name|scope
parameter_list|,
name|String
name|excludedScope
parameter_list|,
name|Collection
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|int
name|ancestorGen
parameter_list|)
function_decl|;
comment|/**    * Randomly choose one node from<i>scope</i>, share the same generation    * ancestor with<i>affinityNode</i>, and exclude nodes in    *<i>excludeScope</i> and<i>excludeNodes</i>.    *    * @param scope range of nodes from which a node will be chosen, cannot start    *              with ~    * @param excludedScope range of nodes to be excluded, cannot start with ~    * @param excludedNodes nodes to be excluded    * @param affinityNode  when not null, the chosen node should share the same    *                     ancestor with this node at generation ancestorGen.    *                      Ignored when value is null    * @param ancestorGen If 0, then no same generation ancestor enforcement on    *                     both excludedNodes and affinityNode. If greater than 0,    *                     then apply to affinityNode(if not null), or apply to    *                     excludedNodes if affinityNode is null    * @return the chosen node    */
DECL|method|chooseRandom (String scope, String excludedScope, Collection<Node> excludedNodes, Node affinityNode, int ancestorGen)
name|Node
name|chooseRandom
parameter_list|(
name|String
name|scope
parameter_list|,
name|String
name|excludedScope
parameter_list|,
name|Collection
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|Node
name|affinityNode
parameter_list|,
name|int
name|ancestorGen
parameter_list|)
function_decl|;
comment|/**    * Choose the node at index<i>index</i> from<i>scope</i>, share the same    * generation ancestor with<i>affinityNode</i>, and exclude nodes in    *<i>excludeScope</i> and<i>excludeNodes</i>.    *    * @param leafIndex node index, exclude nodes in excludedScope and    *                  excludedNodes    * @param scope range of nodes from which a node will be chosen, cannot start    *              with ~    * @param excludedScope range of nodes to be excluded, cannot start with ~    * @param excludedNodes nodes to be excluded    * @param affinityNode  when not null, the chosen node should share the same    *                     ancestor with this node at generation ancestorGen.    *                      Ignored when value is null    * @param ancestorGen If 0, then no same generation ancestor enforcement on    *                     both excludedNodes and affinityNode. If greater than 0,    *                     then apply to affinityNode(if not null), or apply to    *                     excludedNodes if affinityNode is null    * @return the chosen node    */
DECL|method|getNode (int leafIndex, String scope, String excludedScope, Collection<Node> excludedNodes, Node affinityNode, int ancestorGen)
name|Node
name|getNode
parameter_list|(
name|int
name|leafIndex
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|excludedScope
parameter_list|,
name|Collection
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|Node
name|affinityNode
parameter_list|,
name|int
name|ancestorGen
parameter_list|)
function_decl|;
comment|/** Return the distance cost between two nodes    * The distance cost from one node to its parent is it's parent's cost    * The distance cost between two nodes is calculated by summing up their    * distances cost to their closest common ancestor.    * @param node1 one node    * @param node2 another node    * @return the distance cost between node1 and node2 which is zero if they    * are the same or {@link Integer#MAX_VALUE} if node1 or node2 do not belong    * to the cluster    */
DECL|method|getDistanceCost (Node node1, Node node2)
name|int
name|getDistanceCost
parameter_list|(
name|Node
name|node1
parameter_list|,
name|Node
name|node2
parameter_list|)
function_decl|;
comment|/**    * Sort nodes array by network distance to<i>reader</i> to reduces network    * traffic and improves performance.    *    * As an additional twist, we also randomize the nodes at each network    * distance. This helps with load balancing when there is data skew.    *    * @param reader    Node where need the data    * @param nodes     Available replicas with the requested data    * @param activeLen Number of active nodes at the front of the array    */
DECL|method|sortByDistanceCost (Node reader, List<? extends Node> nodes, int activeLen)
name|List
argument_list|<
name|?
extends|extends
name|Node
argument_list|>
name|sortByDistanceCost
parameter_list|(
name|Node
name|reader
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|Node
argument_list|>
name|nodes
parameter_list|,
name|int
name|activeLen
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

