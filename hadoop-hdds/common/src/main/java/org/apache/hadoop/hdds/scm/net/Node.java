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

begin_comment
comment|/**  * The interface defines a node in a network topology.  * A node may be a leave representing a data node or an inner  * node representing a data center or rack.  * Each node has a name and its location in the network is  * decided by a string with syntax similar to a file name.  * For example, a data node's name is hostname:port# and if it's located at  * rack "orange" in data center "dog", the string representation of its  * network location will be /dog/orange.  */
end_comment

begin_interface
DECL|interface|Node
specifier|public
interface|interface
name|Node
block|{
comment|/** @return the string representation of this node's network location path,    *  exclude itself. In another words, its parent's full network location */
DECL|method|getNetworkLocation ()
name|String
name|getNetworkLocation
parameter_list|()
function_decl|;
comment|/**    * Set this node's network location.    * @param location it's network location    */
DECL|method|setNetworkLocation (String location)
name|void
name|setNetworkLocation
parameter_list|(
name|String
name|location
parameter_list|)
function_decl|;
comment|/** @return this node's self name in network topology. This should be node's    * IP or hostname.    * */
DECL|method|getNetworkName ()
name|String
name|getNetworkName
parameter_list|()
function_decl|;
comment|/**    * Set this node's name, can be hostname or Ipaddress.    * @param name it's network name    */
DECL|method|setNetworkName (String name)
name|void
name|setNetworkName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/** @return this node's full path in network topology. It's the concatenation    *  of location and name.    * */
DECL|method|getNetworkFullPath ()
name|String
name|getNetworkFullPath
parameter_list|()
function_decl|;
comment|/** @return this node's parent */
DECL|method|getParent ()
name|InnerNode
name|getParent
parameter_list|()
function_decl|;
comment|/**    * Set this node's parent.    * @param parent the parent    */
DECL|method|setParent (InnerNode parent)
name|void
name|setParent
parameter_list|(
name|InnerNode
name|parent
parameter_list|)
function_decl|;
comment|/** @return this node's ancestor, generation 0 is itself, generation 1 is    *  node's parent, and so on.*/
DECL|method|getAncestor (int generation)
name|Node
name|getAncestor
parameter_list|(
name|int
name|generation
parameter_list|)
function_decl|;
comment|/**    * @return this node's level in the tree.    * E.g. the root of a tree returns 1 and root's children return 2    */
DECL|method|getLevel ()
name|int
name|getLevel
parameter_list|()
function_decl|;
comment|/**    * Set this node's level in the tree.    * @param i the level    */
DECL|method|setLevel (int i)
name|void
name|setLevel
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
comment|/**    * @return this node's cost when network traffic go through it.    * E.g. the cost of going cross a switch is 1, and cost of going through a    * datacenter can be 5.    * Be default the cost of leaf datanode is 0, all other node is 1.    */
DECL|method|getCost ()
name|int
name|getCost
parameter_list|()
function_decl|;
comment|/** @return the leaf nodes number under this node. */
DECL|method|getNumOfLeaves ()
name|int
name|getNumOfLeaves
parameter_list|()
function_decl|;
comment|/**    * Judge if this node is an ancestor of node<i>n</i>.    * Ancestor includes itself and parents case.    *    * @param n a node    * @return true if this node is an ancestor of<i>n</i>    */
DECL|method|isAncestor (Node n)
name|boolean
name|isAncestor
parameter_list|(
name|Node
name|n
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

