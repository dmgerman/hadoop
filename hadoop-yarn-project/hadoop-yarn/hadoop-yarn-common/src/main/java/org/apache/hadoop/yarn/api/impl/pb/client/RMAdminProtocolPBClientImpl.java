begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.impl.pb.client
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
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|Configuration
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
name|ProtobufHelper
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
name|ProtobufRpcEngine
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
name|RPC
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
name|ipc
operator|.
name|RPCUtil
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
name|GetGroupsForUserRequestProto
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
name|GetGroupsForUserResponseProto
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
name|RefreshAdminAclsRequestProto
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
name|RefreshNodesRequestProto
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
name|RefreshQueuesRequestProto
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
name|RefreshServiceAclsRequestProto
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
name|RefreshSuperUserGroupsConfigurationRequestProto
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
name|RefreshUserToGroupsMappingsRequestProto
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
DECL|class|RMAdminProtocolPBClientImpl
specifier|public
class|class
name|RMAdminProtocolPBClientImpl
implements|implements
name|RMAdminProtocol
implements|,
name|Closeable
block|{
DECL|field|proxy
specifier|private
name|RMAdminProtocolPB
name|proxy
decl_stmt|;
DECL|method|RMAdminProtocolPBClientImpl (long clientVersion, InetSocketAddress addr, Configuration conf)
specifier|public
name|RMAdminProtocolPBClientImpl
parameter_list|(
name|long
name|clientVersion
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|RMAdminProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|proxy
operator|=
operator|(
name|RMAdminProtocolPB
operator|)
name|RPC
operator|.
name|getProxy
argument_list|(
name|RMAdminProtocolPB
operator|.
name|class
argument_list|,
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|this
operator|.
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshQueues (RefreshQueuesRequest request)
specifier|public
name|RefreshQueuesResponse
name|refreshQueues
parameter_list|(
name|RefreshQueuesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|RefreshQueuesRequestProto
name|requestProto
init|=
operator|(
operator|(
name|RefreshQueuesRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|RefreshQueuesResponsePBImpl
argument_list|(
name|proxy
operator|.
name|refreshQueues
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshNodes (RefreshNodesRequest request)
specifier|public
name|RefreshNodesResponse
name|refreshNodes
parameter_list|(
name|RefreshNodesRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|RefreshNodesRequestProto
name|requestProto
init|=
operator|(
operator|(
name|RefreshNodesRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|RefreshNodesResponsePBImpl
argument_list|(
name|proxy
operator|.
name|refreshNodes
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshSuperUserGroupsConfiguration ( RefreshSuperUserGroupsConfigurationRequest request)
specifier|public
name|RefreshSuperUserGroupsConfigurationResponse
name|refreshSuperUserGroupsConfiguration
parameter_list|(
name|RefreshSuperUserGroupsConfigurationRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|RefreshSuperUserGroupsConfigurationRequestProto
name|requestProto
init|=
operator|(
operator|(
name|RefreshSuperUserGroupsConfigurationRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|RefreshSuperUserGroupsConfigurationResponsePBImpl
argument_list|(
name|proxy
operator|.
name|refreshSuperUserGroupsConfiguration
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|refreshUserToGroupsMappings ( RefreshUserToGroupsMappingsRequest request)
specifier|public
name|RefreshUserToGroupsMappingsResponse
name|refreshUserToGroupsMappings
parameter_list|(
name|RefreshUserToGroupsMappingsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|RefreshUserToGroupsMappingsRequestProto
name|requestProto
init|=
operator|(
operator|(
name|RefreshUserToGroupsMappingsRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|RefreshUserToGroupsMappingsResponsePBImpl
argument_list|(
name|proxy
operator|.
name|refreshUserToGroupsMappings
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
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
block|{
name|RefreshAdminAclsRequestProto
name|requestProto
init|=
operator|(
operator|(
name|RefreshAdminAclsRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|RefreshAdminAclsResponsePBImpl
argument_list|(
name|proxy
operator|.
name|refreshAdminAcls
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
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
block|{
name|RefreshServiceAclsRequestProto
name|requestProto
init|=
operator|(
operator|(
name|RefreshServiceAclsRequestPBImpl
operator|)
name|request
operator|)
operator|.
name|getProto
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|RefreshServiceAclsResponsePBImpl
argument_list|(
name|proxy
operator|.
name|refreshServiceAcls
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
name|RPCUtil
operator|.
name|unwrapAndThrowException
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getGroupsForUser (String user)
specifier|public
name|String
index|[]
name|getGroupsForUser
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|GetGroupsForUserRequestProto
name|requestProto
init|=
name|GetGroupsForUserRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|GetGroupsForUserResponseProto
name|responseProto
init|=
name|proxy
operator|.
name|getGroupsForUser
argument_list|(
literal|null
argument_list|,
name|requestProto
argument_list|)
decl_stmt|;
return|return
operator|(
name|String
index|[]
operator|)
name|responseProto
operator|.
name|getGroupsList
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|responseProto
operator|.
name|getGroupsCount
argument_list|()
index|]
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

