begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
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
operator|.
name|Private
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
name|InterfaceAudience
operator|.
name|Public
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
operator|.
name|Stable
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
operator|.
name|Unstable
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
name|api
operator|.
name|ClientRMProtocol
import|;
end_import

begin_comment
comment|/**  *<p><code>NodeReport</code> is a summary of runtime information of a   * node in the cluster.</p>  *   *<p>It includes details such as:  *<ul>  *<li>{@link NodeId} of the node.</li>  *<li>HTTP Tracking URL of the node.</li>  *<li>Rack name for the node.</li>  *<li>Used {@link Resource} on the node.</li>  *<li>Total available {@link Resource} of the node.</li>  *<li>Number of running containers on the node.</li>  *<li>{@link NodeHealthStatus} of the node.</li>  *</ul>  *</p>  *  * @see NodeHealthStatus  * @see ClientRMProtocol#getClusterNodes(org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|NodeReport
specifier|public
interface|interface
name|NodeReport
block|{
comment|/**    * Get the<code>NodeId</code> of the node.    * @return<code>NodeId</code> of the node    */
DECL|method|getNodeId ()
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNodeId (NodeId nodeId)
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
comment|/**    * Get the<em>http address</em> of the node.    * @return<em>http address</em> of the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getHttpAddress ()
name|String
name|getHttpAddress
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setHttpAddress (String httpAddress)
name|void
name|setHttpAddress
parameter_list|(
name|String
name|httpAddress
parameter_list|)
function_decl|;
comment|/**    * Get the<em>rack name</em> for the node.    * @return<em>rack name</em> for the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getRackName ()
name|String
name|getRackName
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setRackName (String rackName)
name|void
name|setRackName
parameter_list|(
name|String
name|rackName
parameter_list|)
function_decl|;
comment|/**    * Get<em>used</em><code>Resource</code> on the node.    * @return<em>used</em><code>Resource</code> on the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUsed ()
name|Resource
name|getUsed
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setUsed (Resource used)
name|void
name|setUsed
parameter_list|(
name|Resource
name|used
parameter_list|)
function_decl|;
comment|/**    * Get the<em>total</em><code>Resource</code> on the node.    * @return<em>total</em><code>Resource</code> on the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getCapability ()
name|Resource
name|getCapability
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setCapability (Resource capability)
name|void
name|setCapability
parameter_list|(
name|Resource
name|capability
parameter_list|)
function_decl|;
comment|/**    * Get the<em>number of running containers</em> on the node.    * @return<em>number of running containers</em> on the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumContainers ()
name|int
name|getNumContainers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumContainers (int numContainers)
name|void
name|setNumContainers
parameter_list|(
name|int
name|numContainers
parameter_list|)
function_decl|;
comment|/**    * Get the<code>NodeHealthStatus</code> of the node.     * @return<code>NodeHealthStatus</code> of the node    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNodeHealthStatus ()
name|NodeHealthStatus
name|getNodeHealthStatus
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNodeHealthStatus (NodeHealthStatus nodeHealthStatus)
name|void
name|setNodeHealthStatus
parameter_list|(
name|NodeHealthStatus
name|nodeHealthStatus
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

