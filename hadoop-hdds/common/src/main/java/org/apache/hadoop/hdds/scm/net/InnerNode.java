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

begin_comment
comment|/**  * The interface defines an inner node in a network topology.  * An inner node represents network topology entities, such as data center,  * rack, switch or logical group.  */
end_comment

begin_interface
DECL|interface|InnerNode
specifier|public
interface|interface
name|InnerNode
extends|extends
name|Node
block|{
comment|/** A factory interface to get new InnerNode instance. */
DECL|interface|Factory
interface|interface
name|Factory
parameter_list|<
name|N
extends|extends
name|InnerNode
parameter_list|>
block|{
comment|/** Construct an InnerNode from name, location, parent, level and cost. */
DECL|method|newInnerNode (String name, String location, InnerNode parent, int level, int cost)
name|N
name|newInnerNode
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
parameter_list|,
name|int
name|cost
parameter_list|)
function_decl|;
block|}
comment|/**    * Add node<i>n</i> to the subtree of this node.    * @param n node to be added    * @return true if the node is added; false otherwise    */
DECL|method|add (Node n)
name|boolean
name|add
parameter_list|(
name|Node
name|n
parameter_list|)
function_decl|;
comment|/**    * Remove node<i>n</i> from the subtree of this node.    * @param n node to be deleted    */
DECL|method|remove (Node n)
name|void
name|remove
parameter_list|(
name|Node
name|n
parameter_list|)
function_decl|;
comment|/**    * Given a node's string representation, return a reference to the node.    * @param loc string location of the format /dc/rack/nodegroup/node    * @return null if the node is not found    */
DECL|method|getNode (String loc)
name|Node
name|getNode
parameter_list|(
name|String
name|loc
parameter_list|)
function_decl|;
comment|/**    * @return number of its all nodes at level<i>level</i>. Here level is a    * relative level. If level is 1, means node itself. If level is 2, means its    * direct children, and so on.    **/
DECL|method|getNumOfNodes (int level)
name|int
name|getNumOfNodes
parameter_list|(
name|int
name|level
parameter_list|)
function_decl|;
comment|/**    * Get<i>leafIndex</i> leaf of this subtree.    *    * @param leafIndex an indexed leaf of the node    * @return the leaf node corresponding to the given index.    */
DECL|method|getLeaf (int leafIndex)
name|Node
name|getLeaf
parameter_list|(
name|int
name|leafIndex
parameter_list|)
function_decl|;
comment|/**    * Get<i>leafIndex</i> leaf of this subtree.    *    * @param leafIndex ode's index, start from 0, skip the nodes in    *                  excludedScope and excludedNodes with ancestorGen    * @param excludedScope the excluded scope    * @param excludedNodes nodes to be excluded. If ancestorGen is not 0,    *                      the chosen node will not share same ancestor with    *                      those in excluded nodes at the specified generation    * @param ancestorGen ignored with value is 0    * @return the leaf node corresponding to the given index    */
DECL|method|getLeaf (int leafIndex, String excludedScope, Collection<Node> excludedNodes, int ancestorGen)
name|Node
name|getLeaf
parameter_list|(
name|int
name|leafIndex
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
block|}
end_interface

end_unit

