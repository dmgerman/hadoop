begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License") throws IOException, YarnException; you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.api
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
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
name|ipc
operator|.
name|VersionedProtocol
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
name|security
operator|.
name|KerberosInfo
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
name|slider
operator|.
name|api
operator|.
name|proto
operator|.
name|Messages
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
name|service
operator|.
name|conf
operator|.
name|SliderXmlConfKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Cluster protocol. This can currently act as a versioned IPC  * endpoint or be relayed via protobuf  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|SliderXmlConfKeys
operator|.
name|KEY_KERBEROS_PRINCIPAL
argument_list|)
DECL|interface|SliderClusterProtocol
specifier|public
interface|interface
name|SliderClusterProtocol
extends|extends
name|VersionedProtocol
block|{
DECL|field|versionID
name|long
name|versionID
init|=
literal|0x01
decl_stmt|;
comment|/**    * Stop the cluster    */
DECL|method|stopCluster (Messages.StopClusterRequestProto request)
name|Messages
operator|.
name|StopClusterResponseProto
name|stopCluster
parameter_list|(
name|Messages
operator|.
name|StopClusterRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Upgrade the application containers    *     * @param request upgrade containers request object    * @return upgrade containers response object    * @throws IOException    * @throws YarnException    */
DECL|method|upgradeContainers ( Messages.UpgradeContainersRequestProto request)
name|Messages
operator|.
name|UpgradeContainersResponseProto
name|upgradeContainers
parameter_list|(
name|Messages
operator|.
name|UpgradeContainersRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
DECL|method|flexComponents ( Messages.FlexComponentsRequestProto request)
name|Messages
operator|.
name|FlexComponentsResponseProto
name|flexComponents
parameter_list|(
name|Messages
operator|.
name|FlexComponentsRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the current cluster status    */
DECL|method|getJSONClusterStatus (Messages.GetJSONClusterStatusRequestProto request)
name|Messages
operator|.
name|GetJSONClusterStatusResponseProto
name|getJSONClusterStatus
parameter_list|(
name|Messages
operator|.
name|GetJSONClusterStatusRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * List all running nodes in a role    */
DECL|method|listNodeUUIDsByRole (Messages.ListNodeUUIDsByRoleRequestProto request)
name|Messages
operator|.
name|ListNodeUUIDsByRoleResponseProto
name|listNodeUUIDsByRole
parameter_list|(
name|Messages
operator|.
name|ListNodeUUIDsByRoleRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Get the details on a node    */
DECL|method|getNode (Messages.GetNodeRequestProto request)
name|Messages
operator|.
name|GetNodeResponseProto
name|getNode
parameter_list|(
name|Messages
operator|.
name|GetNodeRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Get the     * details on a list of nodes.    * Unknown nodes are not returned    *<i>Important: the order of the results are undefined</i>    */
DECL|method|getClusterNodes (Messages.GetClusterNodesRequestProto request)
name|Messages
operator|.
name|GetClusterNodesResponseProto
name|getClusterNodes
parameter_list|(
name|Messages
operator|.
name|GetClusterNodesRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Echo back the submitted text (after logging it).    * Useful for adding information to the log, and for testing round trip    * operations of the protocol    * @param request request    * @return response    * @throws IOException    * @throws YarnException    */
DECL|method|echo (Messages.EchoRequestProto request)
name|Messages
operator|.
name|EchoResponseProto
name|echo
parameter_list|(
name|Messages
operator|.
name|EchoRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * Kill an identified container    * @param request request containing the container to kill    * @return the response    * @throws IOException    * @throws YarnException    */
DECL|method|killContainer (Messages.KillContainerRequestProto request)
name|Messages
operator|.
name|KillContainerResponseProto
name|killContainer
parameter_list|(
name|Messages
operator|.
name|KillContainerRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
function_decl|;
comment|/**    * AM to commit suicide. If the Hadoop halt entry point has not been disabled,    * this will fail rather than return with a response.    * @param request request    * @return response (this is not the expected outcome)    * @throws IOException    * @throws YarnException    */
DECL|method|amSuicide (Messages.AMSuicideRequestProto request)
name|Messages
operator|.
name|AMSuicideResponseProto
name|amSuicide
parameter_list|(
name|Messages
operator|.
name|AMSuicideRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the application liveness    * @return current liveness information    * @throws IOException    */
DECL|method|getLivenessInformation ( Messages.GetApplicationLivenessRequestProto request )
name|Messages
operator|.
name|ApplicationLivenessInformationProto
name|getLivenessInformation
parameter_list|(
name|Messages
operator|.
name|GetApplicationLivenessRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getLiveContainers ( Messages.GetLiveContainersRequestProto request )
name|Messages
operator|.
name|GetLiveContainersResponseProto
name|getLiveContainers
parameter_list|(
name|Messages
operator|.
name|GetLiveContainersRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getLiveContainer ( Messages.GetLiveContainerRequestProto request )
name|Messages
operator|.
name|ContainerInformationProto
name|getLiveContainer
parameter_list|(
name|Messages
operator|.
name|GetLiveContainerRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getLiveComponents ( Messages.GetLiveComponentsRequestProto request )
name|Messages
operator|.
name|GetLiveComponentsResponseProto
name|getLiveComponents
parameter_list|(
name|Messages
operator|.
name|GetLiveComponentsRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getLiveComponent ( Messages.GetLiveComponentRequestProto request )
name|Messages
operator|.
name|ComponentInformationProto
name|getLiveComponent
parameter_list|(
name|Messages
operator|.
name|GetLiveComponentRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getLiveNodes ( Messages.GetLiveNodesRequestProto request )
name|Messages
operator|.
name|GetLiveNodesResponseProto
name|getLiveNodes
parameter_list|(
name|Messages
operator|.
name|GetLiveNodesRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getLiveNode ( Messages.GetLiveNodeRequestProto request )
name|Messages
operator|.
name|NodeInformationProto
name|getLiveNode
parameter_list|(
name|Messages
operator|.
name|GetLiveNodeRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

