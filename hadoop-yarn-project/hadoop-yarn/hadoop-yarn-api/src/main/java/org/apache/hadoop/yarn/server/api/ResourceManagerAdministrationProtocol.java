begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api
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
name|api
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Evolving
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
name|io
operator|.
name|retry
operator|.
name|Idempotent
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
name|ipc
operator|.
name|StandbyException
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
name|tools
operator|.
name|GetUserMappingsProtocol
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
name|ResourceOption
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
name|api
operator|.
name|protocolrecords
operator|.
name|AddToClusterNodeLabelsRequest
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
name|AddToClusterNodeLabelsResponse
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
name|RefreshAdminAclsRequest
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
name|RefreshAdminAclsResponse
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
name|RefreshNodesRequest
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
name|RefreshNodesResponse
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
name|RefreshQueuesRequest
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
name|RefreshQueuesResponse
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
name|RefreshServiceAclsRequest
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
name|RefreshServiceAclsResponse
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
name|RefreshSuperUserGroupsConfigurationRequest
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
name|RefreshSuperUserGroupsConfigurationResponse
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
name|RefreshUserToGroupsMappingsRequest
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
name|RefreshUserToGroupsMappingsResponse
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
name|RemoveFromClusterNodeLabelsRequest
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
name|RemoveFromClusterNodeLabelsResponse
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
name|ReplaceLabelsOnNodeRequest
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
name|ReplaceLabelsOnNodeResponse
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
name|UpdateNodeLabelsRequest
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
name|UpdateNodeLabelsResponse
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
name|UpdateNodeResourceRequest
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
name|UpdateNodeResourceResponse
import|;
end_import

begin_interface
annotation|@
name|Private
annotation|@
name|Stable
DECL|interface|ResourceManagerAdministrationProtocol
specifier|public
interface|interface
name|ResourceManagerAdministrationProtocol
extends|extends
name|GetUserMappingsProtocol
block|{
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|Idempotent
DECL|method|refreshQueues (RefreshQueuesRequest request)
specifier|public
name|RefreshQueuesResponse
name|refreshQueues
parameter_list|(
name|RefreshQueuesRequest
name|request
parameter_list|)
throws|throws
name|StandbyException
throws|,
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|Idempotent
DECL|method|refreshNodes (RefreshNodesRequest request)
specifier|public
name|RefreshNodesResponse
name|refreshNodes
parameter_list|(
name|RefreshNodesRequest
name|request
parameter_list|)
throws|throws
name|StandbyException
throws|,
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|Idempotent
specifier|public
name|RefreshSuperUserGroupsConfigurationResponse
DECL|method|refreshSuperUserGroupsConfiguration ( RefreshSuperUserGroupsConfigurationRequest request)
name|refreshSuperUserGroupsConfiguration
parameter_list|(
name|RefreshSuperUserGroupsConfigurationRequest
name|request
parameter_list|)
throws|throws
name|StandbyException
throws|,
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|Idempotent
DECL|method|refreshUserToGroupsMappings ( RefreshUserToGroupsMappingsRequest request)
specifier|public
name|RefreshUserToGroupsMappingsResponse
name|refreshUserToGroupsMappings
parameter_list|(
name|RefreshUserToGroupsMappingsRequest
name|request
parameter_list|)
throws|throws
name|StandbyException
throws|,
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|Idempotent
DECL|method|refreshAdminAcls ( RefreshAdminAclsRequest request)
specifier|public
name|RefreshAdminAclsResponse
name|refreshAdminAcls
parameter_list|(
name|RefreshAdminAclsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
annotation|@
name|Idempotent
DECL|method|refreshServiceAcls ( RefreshServiceAclsRequest request)
specifier|public
name|RefreshServiceAclsResponse
name|refreshServiceAcls
parameter_list|(
name|RefreshServiceAclsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
comment|/**    *<p>The interface used by admin to update nodes' resources to the    *<code>ResourceManager</code></p>.    *     *<p>The admin client is required to provide details such as a map from     * {@link NodeId} to {@link ResourceOption} required to update resources on     * a list of<code>RMNode</code> in<code>ResourceManager</code> etc.    * via the {@link UpdateNodeResourceRequest}.</p>    *     * @param request request to update resource for a node in cluster.    * @return (empty) response on accepting update.    * @throws YarnException    * @throws IOException    */
annotation|@
name|Public
annotation|@
name|Evolving
annotation|@
name|Idempotent
DECL|method|updateNodeResource ( UpdateNodeResourceRequest request)
specifier|public
name|UpdateNodeResourceResponse
name|updateNodeResource
parameter_list|(
name|UpdateNodeResourceRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Evolving
annotation|@
name|Idempotent
DECL|method|addToClusterNodeLabels ( AddToClusterNodeLabelsRequest request)
specifier|public
name|AddToClusterNodeLabelsResponse
name|addToClusterNodeLabels
parameter_list|(
name|AddToClusterNodeLabelsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Evolving
annotation|@
name|Idempotent
DECL|method|removeFromClusterNodeLabels ( RemoveFromClusterNodeLabelsRequest request)
specifier|public
name|RemoveFromClusterNodeLabelsResponse
name|removeFromClusterNodeLabels
parameter_list|(
name|RemoveFromClusterNodeLabelsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Evolving
annotation|@
name|Idempotent
DECL|method|replaceLabelsOnNode ( ReplaceLabelsOnNodeRequest request)
specifier|public
name|ReplaceLabelsOnNodeResponse
name|replaceLabelsOnNode
parameter_list|(
name|ReplaceLabelsOnNodeRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
annotation|@
name|Public
annotation|@
name|Evolving
annotation|@
name|Idempotent
DECL|method|updateNodeLabels ( UpdateNodeLabelsRequest request)
specifier|public
name|UpdateNodeLabelsResponse
name|updateNodeLabels
parameter_list|(
name|UpdateNodeLabelsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

