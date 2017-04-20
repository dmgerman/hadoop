begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.rpc
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|rpc
package|;
end_package

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
name|SliderClusterProtocol
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Server-side Relay from Protobuf to internal RPC.  *  */
end_comment

begin_class
DECL|class|SliderClusterProtocolPBImpl
specifier|public
class|class
name|SliderClusterProtocolPBImpl
implements|implements
name|SliderClusterProtocolPB
block|{
DECL|field|real
specifier|private
name|SliderClusterProtocol
name|real
decl_stmt|;
DECL|method|SliderClusterProtocolPBImpl (SliderClusterProtocol real)
specifier|public
name|SliderClusterProtocolPBImpl
parameter_list|(
name|SliderClusterProtocol
name|real
parameter_list|)
block|{
name|this
operator|.
name|real
operator|=
name|real
expr_stmt|;
block|}
DECL|method|wrap (Exception e)
specifier|private
name|ServiceException
name|wrap
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ServiceException
condition|)
block|{
return|return
operator|(
name|ServiceException
operator|)
name|e
return|;
block|}
return|return
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
return|;
block|}
DECL|method|getProtocolVersion (String protocol, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SliderClusterProtocol
operator|.
name|versionID
return|;
block|}
annotation|@
name|Override
DECL|method|stopCluster (RpcController controller, Messages.StopClusterRequestProto request)
specifier|public
name|Messages
operator|.
name|StopClusterResponseProto
name|stopCluster
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|StopClusterRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|stopCluster
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|upgradeContainers (RpcController controller, Messages.UpgradeContainersRequestProto request)
specifier|public
name|Messages
operator|.
name|UpgradeContainersResponseProto
name|upgradeContainers
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|UpgradeContainersRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|upgradeContainers
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|flexComponents ( RpcController controller, Messages.FlexComponentsRequestProto request)
specifier|public
name|Messages
operator|.
name|FlexComponentsResponseProto
name|flexComponents
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|FlexComponentsRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|flexComponents
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getJSONClusterStatus ( RpcController controller, Messages.GetJSONClusterStatusRequestProto request)
specifier|public
name|Messages
operator|.
name|GetJSONClusterStatusResponseProto
name|getJSONClusterStatus
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetJSONClusterStatusRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getJSONClusterStatus
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listNodeUUIDsByRole ( RpcController controller, Messages.ListNodeUUIDsByRoleRequestProto request)
specifier|public
name|Messages
operator|.
name|ListNodeUUIDsByRoleResponseProto
name|listNodeUUIDsByRole
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|ListNodeUUIDsByRoleRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|listNodeUUIDsByRole
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNode (RpcController controller, Messages.GetNodeRequestProto request)
specifier|public
name|Messages
operator|.
name|GetNodeResponseProto
name|getNode
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetNodeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getNode
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getClusterNodes (RpcController controller, Messages.GetClusterNodesRequestProto request)
specifier|public
name|Messages
operator|.
name|GetClusterNodesResponseProto
name|getClusterNodes
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetClusterNodesRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getClusterNodes
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|echo (RpcController controller, Messages.EchoRequestProto request)
specifier|public
name|Messages
operator|.
name|EchoResponseProto
name|echo
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|EchoRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|echo
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|killContainer (RpcController controller, Messages.KillContainerRequestProto request)
specifier|public
name|Messages
operator|.
name|KillContainerResponseProto
name|killContainer
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|KillContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|killContainer
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|amSuicide (RpcController controller, Messages.AMSuicideRequestProto request)
specifier|public
name|Messages
operator|.
name|AMSuicideResponseProto
name|amSuicide
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|AMSuicideRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|amSuicide
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLivenessInformation ( RpcController controller, Messages.GetApplicationLivenessRequestProto request)
specifier|public
name|Messages
operator|.
name|ApplicationLivenessInformationProto
name|getLivenessInformation
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetApplicationLivenessRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getLivenessInformation
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveContainers (RpcController controller, Messages.GetLiveContainersRequestProto request)
specifier|public
name|Messages
operator|.
name|GetLiveContainersResponseProto
name|getLiveContainers
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetLiveContainersRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getLiveContainers
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveContainer (RpcController controller, Messages.GetLiveContainerRequestProto request)
specifier|public
name|Messages
operator|.
name|ContainerInformationProto
name|getLiveContainer
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetLiveContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getLiveContainer
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveComponents (RpcController controller, Messages.GetLiveComponentsRequestProto request)
specifier|public
name|Messages
operator|.
name|GetLiveComponentsResponseProto
name|getLiveComponents
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetLiveComponentsRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getLiveComponents
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveComponent (RpcController controller, Messages.GetLiveComponentRequestProto request)
specifier|public
name|Messages
operator|.
name|ComponentInformationProto
name|getLiveComponent
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetLiveComponentRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getLiveComponent
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveNodes (RpcController controller, Messages.GetLiveNodesRequestProto request)
specifier|public
name|Messages
operator|.
name|GetLiveNodesResponseProto
name|getLiveNodes
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetLiveNodesRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getLiveNodes
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveNode (RpcController controller, Messages.GetLiveNodeRequestProto request)
specifier|public
name|Messages
operator|.
name|NodeInformationProto
name|getLiveNode
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|Messages
operator|.
name|GetLiveNodeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|real
operator|.
name|getLiveNode
argument_list|(
name|request
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|wrap
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

