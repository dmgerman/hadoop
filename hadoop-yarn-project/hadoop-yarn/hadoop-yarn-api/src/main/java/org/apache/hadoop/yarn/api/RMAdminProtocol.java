begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api
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
name|exceptions
operator|.
name|YarnRemoteException
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
name|api
operator|.
name|protocolrecords
operator|.
name|RefreshUserToGroupsMappingsResponse
import|;
end_import

begin_interface
DECL|interface|RMAdminProtocol
specifier|public
interface|interface
name|RMAdminProtocol
extends|extends
name|GetUserMappingsProtocol
block|{
DECL|method|refreshQueues (RefreshQueuesRequest request)
specifier|public
name|RefreshQueuesResponse
name|refreshQueues
parameter_list|(
name|RefreshQueuesRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
DECL|method|refreshNodes (RefreshNodesRequest request)
specifier|public
name|RefreshNodesResponse
name|refreshNodes
parameter_list|(
name|RefreshNodesRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
specifier|public
name|RefreshSuperUserGroupsConfigurationResponse
DECL|method|refreshSuperUserGroupsConfiguration ( RefreshSuperUserGroupsConfigurationRequest request)
name|refreshSuperUserGroupsConfiguration
parameter_list|(
name|RefreshSuperUserGroupsConfigurationRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
DECL|method|refreshUserToGroupsMappings ( RefreshUserToGroupsMappingsRequest request)
specifier|public
name|RefreshUserToGroupsMappingsResponse
name|refreshUserToGroupsMappings
parameter_list|(
name|RefreshUserToGroupsMappingsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
DECL|method|refreshAdminAcls ( RefreshAdminAclsRequest request)
specifier|public
name|RefreshAdminAclsResponse
name|refreshAdminAcls
parameter_list|(
name|RefreshAdminAclsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
DECL|method|refreshServiceAcls ( RefreshServiceAclsRequest request)
specifier|public
name|RefreshServiceAclsResponse
name|refreshServiceAcls
parameter_list|(
name|RefreshServiceAclsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

