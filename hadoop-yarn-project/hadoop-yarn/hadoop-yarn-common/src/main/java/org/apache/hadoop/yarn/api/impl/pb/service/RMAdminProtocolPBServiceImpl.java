begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.impl.pb.service
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
name|impl
operator|.
name|pb
operator|.
name|service
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
name|yarn
operator|.
name|api
operator|.
name|RMAdminProtocol
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
name|RMAdminProtocolPB
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
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RefreshAdminAclsRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshAdminAclsResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshNodesRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshNodesResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshQueuesRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshQueuesResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshServiceAclsRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshServiceAclsResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshSuperUserGroupsConfigurationRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshSuperUserGroupsConfigurationResponsePBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshUserToGroupsMappingsRequestPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|RefreshUserToGroupsMappingsResponsePBImpl
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
name|proto
operator|.
name|YarnServerResourceManagerServiceProtos
operator|.
name|*
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|RpcController
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
import|;
end_import

begin_class
DECL|class|RMAdminProtocolPBServiceImpl
specifier|public
class|class
name|RMAdminProtocolPBServiceImpl
implements|implements
name|RMAdminProtocolPB
block|{
DECL|field|real
specifier|private
name|RMAdminProtocol
name|real
decl_stmt|;
DECL|method|RMAdminProtocolPBServiceImpl (RMAdminProtocol impl)
specifier|public
name|RMAdminProtocolPBServiceImpl
parameter_list|(
name|RMAdminProtocol
name|impl
parameter_list|)
block|{
name|this
operator|.
name|real
operator|=
name|impl
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refreshQueues (RpcController controller, RefreshQueuesRequestProto proto)
specifier|public
name|RefreshQueuesResponseProto
name|refreshQueues
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshQueuesRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RefreshQueuesRequestPBImpl
name|request
init|=
operator|new
name|RefreshQueuesRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|RefreshQueuesResponse
name|response
init|=
name|real
operator|.
name|refreshQueues
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RefreshQueuesResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshAdminAcls ( RpcController controller, RefreshAdminAclsRequestProto proto)
specifier|public
name|RefreshAdminAclsResponseProto
name|refreshAdminAcls
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshAdminAclsRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RefreshAdminAclsRequestPBImpl
name|request
init|=
operator|new
name|RefreshAdminAclsRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|RefreshAdminAclsResponse
name|response
init|=
name|real
operator|.
name|refreshAdminAcls
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RefreshAdminAclsResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshNodes (RpcController controller, RefreshNodesRequestProto proto)
specifier|public
name|RefreshNodesResponseProto
name|refreshNodes
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshNodesRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RefreshNodesRequestPBImpl
name|request
init|=
operator|new
name|RefreshNodesRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|RefreshNodesResponse
name|response
init|=
name|real
operator|.
name|refreshNodes
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RefreshNodesResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|RefreshSuperUserGroupsConfigurationResponseProto
DECL|method|refreshSuperUserGroupsConfiguration ( RpcController controller, RefreshSuperUserGroupsConfigurationRequestProto proto)
name|refreshSuperUserGroupsConfiguration
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshSuperUserGroupsConfigurationRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RefreshSuperUserGroupsConfigurationRequestPBImpl
name|request
init|=
operator|new
name|RefreshSuperUserGroupsConfigurationRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|RefreshSuperUserGroupsConfigurationResponse
name|response
init|=
name|real
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RefreshSuperUserGroupsConfigurationResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshUserToGroupsMappings ( RpcController controller, RefreshUserToGroupsMappingsRequestProto proto)
specifier|public
name|RefreshUserToGroupsMappingsResponseProto
name|refreshUserToGroupsMappings
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshUserToGroupsMappingsRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RefreshUserToGroupsMappingsRequestPBImpl
name|request
init|=
operator|new
name|RefreshUserToGroupsMappingsRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|RefreshUserToGroupsMappingsResponse
name|response
init|=
name|real
operator|.
name|refreshUserToGroupsMappings
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RefreshUserToGroupsMappingsResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshServiceAcls ( RpcController controller, RefreshServiceAclsRequestProto proto)
specifier|public
name|RefreshServiceAclsResponseProto
name|refreshServiceAcls
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshServiceAclsRequestProto
name|proto
parameter_list|)
throws|throws
name|ServiceException
block|{
name|RefreshServiceAclsRequestPBImpl
name|request
init|=
operator|new
name|RefreshServiceAclsRequestPBImpl
argument_list|(
name|proto
argument_list|)
decl_stmt|;
try|try
block|{
name|RefreshServiceAclsResponse
name|response
init|=
name|real
operator|.
name|refreshServiceAcls
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RefreshServiceAclsResponsePBImpl
operator|)
name|response
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getGroupsForUser ( RpcController controller, GetGroupsForUserRequestProto request)
specifier|public
name|GetGroupsForUserResponseProto
name|getGroupsForUser
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetGroupsForUserRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|String
name|user
init|=
name|request
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
block|{
name|String
index|[]
name|groups
init|=
name|real
operator|.
name|getGroupsForUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|GetGroupsForUserResponseProto
operator|.
name|Builder
name|responseBuilder
init|=
name|GetGroupsForUserResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|group
range|:
name|groups
control|)
block|{
name|responseBuilder
operator|.
name|addGroups
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
return|return
name|responseBuilder
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

