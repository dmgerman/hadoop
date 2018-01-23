begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmnode
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
name|resourcemanager
operator|.
name|rmnode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|records
operator|.
name|Container
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
name|records
operator|.
name|ContainerId
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
name|records
operator|.
name|NodeId
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
name|records
operator|.
name|NodeState
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
name|records
operator|.
name|Resource
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
name|records
operator|.
name|ResourceUtilization
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatResponse
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
name|api
operator|.
name|records
operator|.
name|OpportunisticContainersStatus
import|;
end_import

begin_comment
comment|/**  * Node managers information on available resources   * and other static information.  *  */
end_comment

begin_interface
DECL|interface|RMNode
specifier|public
interface|interface
name|RMNode
block|{
comment|/**    * the node id of of this node.    * @return the node id of this node.    */
DECL|method|getNodeID ()
specifier|public
name|NodeId
name|getNodeID
parameter_list|()
function_decl|;
comment|/**    * the hostname of this node    * @return hostname of this node    */
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
function_decl|;
comment|/**    * the command port for this node    * @return command port for this node    */
DECL|method|getCommandPort ()
specifier|public
name|int
name|getCommandPort
parameter_list|()
function_decl|;
comment|/**    * the http port for this node    * @return http port for this node    */
DECL|method|getHttpPort ()
specifier|public
name|int
name|getHttpPort
parameter_list|()
function_decl|;
comment|/**    * the ContainerManager address for this node.    * @return the ContainerManager address for this node.    */
DECL|method|getNodeAddress ()
specifier|public
name|String
name|getNodeAddress
parameter_list|()
function_decl|;
comment|/**    * the http-Address for this node.    * @return the http-url address for this node    */
DECL|method|getHttpAddress ()
specifier|public
name|String
name|getHttpAddress
parameter_list|()
function_decl|;
comment|/**    * the latest health report received from this node.    * @return the latest health report received from this node.    */
DECL|method|getHealthReport ()
specifier|public
name|String
name|getHealthReport
parameter_list|()
function_decl|;
comment|/**    * the time of the latest health report received from this node.    * @return the time of the latest health report received from this node.    */
DECL|method|getLastHealthReportTime ()
specifier|public
name|long
name|getLastHealthReportTime
parameter_list|()
function_decl|;
comment|/**    * the node manager version of the node received as part of the    * registration with the resource manager    */
DECL|method|getNodeManagerVersion ()
specifier|public
name|String
name|getNodeManagerVersion
parameter_list|()
function_decl|;
comment|/**    * the total available resource.    * @return the total available resource.    */
DECL|method|getTotalCapability ()
specifier|public
name|Resource
name|getTotalCapability
parameter_list|()
function_decl|;
comment|/**    * the aggregated resource utilization of the containers.    * @return the aggregated resource utilization of the containers.    */
DECL|method|getAggregatedContainersUtilization ()
specifier|public
name|ResourceUtilization
name|getAggregatedContainersUtilization
parameter_list|()
function_decl|;
comment|/**    * the total resource utilization of the node.    * @return the total resource utilization of the node.    */
DECL|method|getNodeUtilization ()
specifier|public
name|ResourceUtilization
name|getNodeUtilization
parameter_list|()
function_decl|;
comment|/**    * the physical resources in the node.    * @return the physical resources in the node.    */
DECL|method|getPhysicalResource ()
name|Resource
name|getPhysicalResource
parameter_list|()
function_decl|;
comment|/**    * The rack name for this node manager.    * @return the rack name.    */
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
function_decl|;
comment|/**    * the {@link Node} information for this node.    * @return {@link Node} information for this node.    */
DECL|method|getNode ()
specifier|public
name|Node
name|getNode
parameter_list|()
function_decl|;
DECL|method|getState ()
specifier|public
name|NodeState
name|getState
parameter_list|()
function_decl|;
DECL|method|getContainersToCleanUp ()
specifier|public
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getContainersToCleanUp
parameter_list|()
function_decl|;
DECL|method|getAppsToCleanup ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getAppsToCleanup
parameter_list|()
function_decl|;
DECL|method|getRunningApps ()
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getRunningApps
parameter_list|()
function_decl|;
comment|/**    * Update a {@link NodeHeartbeatResponse} with the list of containers and    * applications to clean up for this node, and the containers to be updated.    *    * @param response the {@link NodeHeartbeatResponse} to update    */
DECL|method|setAndUpdateNodeHeartbeatResponse (NodeHeartbeatResponse response)
name|void
name|setAndUpdateNodeHeartbeatResponse
parameter_list|(
name|NodeHeartbeatResponse
name|response
parameter_list|)
function_decl|;
DECL|method|getLastNodeHeartBeatResponse ()
specifier|public
name|NodeHeartbeatResponse
name|getLastNodeHeartBeatResponse
parameter_list|()
function_decl|;
comment|/**    * Reset lastNodeHeartbeatResponse's ID to 0.    */
DECL|method|resetLastNodeHeartBeatResponse ()
name|void
name|resetLastNodeHeartBeatResponse
parameter_list|()
function_decl|;
comment|/**    * Get and clear the list of containerUpdates accumulated across NM    * heartbeats.    *     * @return containerUpdates accumulated across NM heartbeats.    */
DECL|method|pullContainerUpdates ()
specifier|public
name|List
argument_list|<
name|UpdatedContainerInfo
argument_list|>
name|pullContainerUpdates
parameter_list|()
function_decl|;
comment|/**    * Get set of labels in this node    *     * @return labels in this node    */
DECL|method|getNodeLabels ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeLabels
parameter_list|()
function_decl|;
DECL|method|pullNewlyIncreasedContainers ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|pullNewlyIncreasedContainers
parameter_list|()
function_decl|;
DECL|method|getOpportunisticContainersStatus ()
name|OpportunisticContainersStatus
name|getOpportunisticContainersStatus
parameter_list|()
function_decl|;
DECL|method|getUntrackedTimeStamp ()
name|long
name|getUntrackedTimeStamp
parameter_list|()
function_decl|;
DECL|method|setUntrackedTimeStamp (long timeStamp)
name|void
name|setUntrackedTimeStamp
parameter_list|(
name|long
name|timeStamp
parameter_list|)
function_decl|;
comment|/*    * Optional decommissioning timeout in second    * (null indicates absent timeout).    * @return the decommissioning timeout in second.    */
DECL|method|getDecommissioningTimeout ()
name|Integer
name|getDecommissioningTimeout
parameter_list|()
function_decl|;
comment|/**    * Get the allocation tags and their counts associated with this node.    * @return a map of each allocation tag and its count.    */
DECL|method|getAllocationTagsWithCount ()
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getAllocationTagsWithCount
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

