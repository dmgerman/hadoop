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
comment|/** The interface defines a node in a network topology.  * A node may be a leave representing a data node or an inner  * node representing a datacenter or rack.  * Each data has a name and its location in the network is  * decided by a string with syntax similar to a file name.   * For example, a data node's name is hostname:port# and if it's located at  * rack "orange" in datacenter "dog", the string representation of its  * network location is /dog/orange  */
end_comment

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
DECL|interface|Node
specifier|public
interface|interface
name|Node
block|{
comment|/** Return the string representation of this node's network location */
DECL|method|getNetworkLocation ()
specifier|public
name|String
name|getNetworkLocation
parameter_list|()
function_decl|;
comment|/** Set the node's network location */
DECL|method|setNetworkLocation (String location)
specifier|public
name|void
name|setNetworkLocation
parameter_list|(
name|String
name|location
parameter_list|)
function_decl|;
comment|/** Return this node's name */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/** Return this node's parent */
DECL|method|getParent ()
specifier|public
name|Node
name|getParent
parameter_list|()
function_decl|;
comment|/** Set this node's parent */
DECL|method|setParent (Node parent)
specifier|public
name|void
name|setParent
parameter_list|(
name|Node
name|parent
parameter_list|)
function_decl|;
comment|/** Return this node's level in the tree.    * E.g. the root of a tree returns 0 and its children return 1    */
DECL|method|getLevel ()
specifier|public
name|int
name|getLevel
parameter_list|()
function_decl|;
comment|/** Set this node's level in the tree.*/
DECL|method|setLevel (int i)
specifier|public
name|void
name|setLevel
parameter_list|(
name|int
name|i
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

