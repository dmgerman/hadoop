begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.resolver
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
name|federation
operator|.
name|resolver
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configurable
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterId
import|;
end_import

begin_comment
comment|/**  * An utility that helps to determine the sub-cluster that a specified node or  * rack belongs to. All implementing classes should be thread-safe.  */
end_comment

begin_interface
DECL|interface|SubClusterResolver
specifier|public
interface|interface
name|SubClusterResolver
extends|extends
name|Configurable
block|{
comment|/**    * Obtain the sub-cluster that a specified node belongs to.    *    * @param nodename the node whose sub-cluster is to be determined    * @return the sub-cluster as identified by the {@link SubClusterId} that the    *         node belongs to    * @throws YarnException if the node's sub-cluster cannot be resolved    */
DECL|method|getSubClusterForNode (String nodename)
name|SubClusterId
name|getSubClusterForNode
parameter_list|(
name|String
name|nodename
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Obtain the sub-clusters that have nodes on a specified rack.    *    * @param rackname the name of the rack    * @return the sub-clusters as identified by the {@link SubClusterId} that    *         have nodes on the given rack    * @throws YarnException if the sub-cluster of any node on the rack cannot be    *           resolved, or if the rack name is not recognized    */
DECL|method|getSubClustersForRack (String rackname)
name|Set
argument_list|<
name|SubClusterId
argument_list|>
name|getSubClustersForRack
parameter_list|(
name|String
name|rackname
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Load the nodes to subCluster mapping from the file.    */
DECL|method|load ()
name|void
name|load
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

