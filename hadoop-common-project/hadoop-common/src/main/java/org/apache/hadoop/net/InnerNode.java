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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_interface
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
DECL|interface|InnerNode
specifier|public
interface|interface
name|InnerNode
extends|extends
name|Node
block|{
DECL|interface|Factory
interface|interface
name|Factory
parameter_list|<
name|N
extends|extends
name|InnerNode
parameter_list|>
block|{
comment|/** Construct an InnerNode from a path-like string */
DECL|method|newInnerNode (String path)
name|N
name|newInnerNode
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
block|}
comment|/** Add node<i>n</i> to the subtree of this node    * @param n node to be added    * @return true if the node is added; false otherwise    */
DECL|method|add (Node n)
name|boolean
name|add
parameter_list|(
name|Node
name|n
parameter_list|)
function_decl|;
comment|/** Given a node's string representation, return a reference to the node    * @param loc string location of the form /rack/node    * @return null if the node is not found or the childnode is there but    * not an instance of {@link InnerNodeImpl}    */
DECL|method|getLoc (String loc)
name|Node
name|getLoc
parameter_list|(
name|String
name|loc
parameter_list|)
function_decl|;
comment|/** @return its children */
DECL|method|getChildren ()
name|List
argument_list|<
name|Node
argument_list|>
name|getChildren
parameter_list|()
function_decl|;
comment|/** @return the number of leave nodes. */
DECL|method|getNumOfLeaves ()
name|int
name|getNumOfLeaves
parameter_list|()
function_decl|;
comment|/** Remove node<i>n</i> from the subtree of this node    * @param n node to be deleted    * @return true if the node is deleted; false otherwise    */
DECL|method|remove (Node n)
name|boolean
name|remove
parameter_list|(
name|Node
name|n
parameter_list|)
function_decl|;
comment|/** get<i>leafIndex</i> leaf of this subtree    * if it is not in the<i>excludedNode</i>    *    * @param leafIndex an indexed leaf of the node    * @param excludedNode an excluded node (can be null)    * @return    */
DECL|method|getLeaf (int leafIndex, Node excludedNode)
name|Node
name|getLeaf
parameter_list|(
name|int
name|leafIndex
parameter_list|,
name|Node
name|excludedNode
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

