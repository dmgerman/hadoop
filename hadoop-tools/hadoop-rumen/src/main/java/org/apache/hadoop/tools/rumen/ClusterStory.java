begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * {@link ClusterStory} represents all configurations of a MapReduce cluster,  * including nodes, network topology, and slot configurations.  */
end_comment

begin_interface
DECL|interface|ClusterStory
specifier|public
interface|interface
name|ClusterStory
block|{
comment|/**    * Get all machines of the cluster.    * @return A read-only set that contains all machines of the cluster.    */
DECL|method|getMachines ()
specifier|public
name|Set
argument_list|<
name|MachineNode
argument_list|>
name|getMachines
parameter_list|()
function_decl|;
comment|/**    * Get all racks of the cluster.    * @return A read-only set that contains all racks of the cluster.    */
DECL|method|getRacks ()
specifier|public
name|Set
argument_list|<
name|RackNode
argument_list|>
name|getRacks
parameter_list|()
function_decl|;
comment|/**    * Get the cluster topology tree.    * @return The root node of the cluster topology tree.    */
DECL|method|getClusterTopology ()
specifier|public
name|Node
name|getClusterTopology
parameter_list|()
function_decl|;
comment|/**    * Select a random set of machines.    * @param expected The expected sample size.    * @param random Random number generator to use.    * @return An array of up to expected number of {@link MachineNode}s.    */
DECL|method|getRandomMachines (int expected, Random random)
specifier|public
name|MachineNode
index|[]
name|getRandomMachines
parameter_list|(
name|int
name|expected
parameter_list|,
name|Random
name|random
parameter_list|)
function_decl|;
comment|/**    * Get {@link MachineNode} by its host name.    *     * @return The {@link MachineNode} with the same name. Or null if not found.    */
DECL|method|getMachineByName (String name)
specifier|public
name|MachineNode
name|getMachineByName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Get {@link RackNode} by its name.    * @return The {@link RackNode} with the same name. Or null if not found.    */
DECL|method|getRackByName (String name)
specifier|public
name|RackNode
name|getRackByName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Determine the distance between two {@link Node}s. Currently, the distance    * is loosely defined as the length of the longer path for either a or b to    * reach their common ancestor.    *     * @param a    * @param b    * @return The distance between {@link Node} a and {@link Node} b.    */
DECL|method|distance (Node a, Node b)
name|int
name|distance
parameter_list|(
name|Node
name|a
parameter_list|,
name|Node
name|b
parameter_list|)
function_decl|;
comment|/**    * Get the maximum distance possible between any two nodes.    * @return the maximum distance possible between any two nodes.    */
DECL|method|getMaximumDistance ()
name|int
name|getMaximumDistance
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

