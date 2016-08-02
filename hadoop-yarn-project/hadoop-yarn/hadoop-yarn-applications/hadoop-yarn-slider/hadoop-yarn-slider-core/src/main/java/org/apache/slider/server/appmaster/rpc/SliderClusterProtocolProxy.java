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
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|ProtocolSignature
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
name|ipc
operator|.
name|RemoteException
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_class
DECL|class|SliderClusterProtocolProxy
specifier|public
class|class
name|SliderClusterProtocolProxy
implements|implements
name|SliderClusterProtocol
block|{
DECL|field|NULL_CONTROLLER
specifier|private
specifier|static
specifier|final
name|RpcController
name|NULL_CONTROLLER
init|=
literal|null
decl_stmt|;
DECL|field|endpoint
specifier|private
specifier|final
name|SliderClusterProtocolPB
name|endpoint
decl_stmt|;
DECL|field|address
specifier|private
specifier|final
name|InetSocketAddress
name|address
decl_stmt|;
DECL|method|SliderClusterProtocolProxy (SliderClusterProtocolPB endpoint, InetSocketAddress address)
specifier|public
name|SliderClusterProtocolProxy
parameter_list|(
name|SliderClusterProtocolPB
name|endpoint
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|endpoint
operator|!=
literal|null
argument_list|,
literal|"null endpoint"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|address
operator|!=
literal|null
argument_list|,
literal|"null address"
argument_list|)
expr_stmt|;
name|this
operator|.
name|endpoint
operator|=
name|endpoint
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"SliderClusterProtocolProxy{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"address="
argument_list|)
operator|.
name|append
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHash
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|protocol
operator|.
name|equals
argument_list|(
name|RPC
operator|.
name|getProtocolName
argument_list|(
name|SliderClusterProtocolPB
operator|.
name|class
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Serverside implements "
operator|+
name|RPC
operator|.
name|getProtocolName
argument_list|(
name|SliderClusterProtocolPB
operator|.
name|class
argument_list|)
operator|+
literal|". The following requested protocol is unknown: "
operator|+
name|protocol
argument_list|)
throw|;
block|}
return|return
name|ProtocolSignature
operator|.
name|getProtocolSignature
argument_list|(
name|clientMethodsHash
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|SliderClusterProtocol
operator|.
name|class
argument_list|)
argument_list|,
name|SliderClusterProtocol
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
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
DECL|method|convert (ServiceException se)
specifier|private
name|IOException
name|convert
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
name|IOException
name|ioe
init|=
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
decl_stmt|;
if|if
condition|(
name|ioe
operator|instanceof
name|RemoteException
condition|)
block|{
name|RemoteException
name|remoteException
init|=
operator|(
name|RemoteException
operator|)
name|ioe
decl_stmt|;
return|return
name|remoteException
operator|.
name|unwrapRemoteException
argument_list|()
return|;
block|}
return|return
name|ioe
return|;
block|}
annotation|@
name|Override
DECL|method|stopCluster (Messages.StopClusterRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|stopCluster
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|upgradeContainers ( Messages.UpgradeContainersRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|upgradeContainers
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|flexCluster (Messages.FlexClusterRequestProto request)
specifier|public
name|Messages
operator|.
name|FlexClusterResponseProto
name|flexCluster
parameter_list|(
name|Messages
operator|.
name|FlexClusterRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|flexCluster
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getJSONClusterStatus ( Messages.GetJSONClusterStatusRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getJSONClusterStatus
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInstanceDefinition ( Messages.GetInstanceDefinitionRequestProto request)
specifier|public
name|Messages
operator|.
name|GetInstanceDefinitionResponseProto
name|getInstanceDefinition
parameter_list|(
name|Messages
operator|.
name|GetInstanceDefinitionRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getInstanceDefinition
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listNodeUUIDsByRole (Messages.ListNodeUUIDsByRoleRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|listNodeUUIDsByRole
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNode (Messages.GetNodeRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getNode
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getClusterNodes (Messages.GetClusterNodesRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getClusterNodes
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|echo (Messages.EchoRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|echo
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|killContainer (Messages.KillContainerRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|killContainer
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|amSuicide (Messages.AMSuicideRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|amSuicide
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLivenessInformation ( Messages.GetApplicationLivenessRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getLivenessInformation
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveContainers (Messages.GetLiveContainersRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getLiveContainers
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveContainer (Messages.GetLiveContainerRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getLiveContainer
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveComponents (Messages.GetLiveComponentsRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getLiveComponents
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveComponent (Messages.GetLiveComponentRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getLiveComponent
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveNodes (Messages.GetLiveNodesRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getLiveNodes
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveNode (Messages.GetLiveNodeRequestProto request)
specifier|public
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
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getLiveNode
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getModelDesired (Messages.EmptyPayloadProto request)
specifier|public
name|Messages
operator|.
name|WrappedJsonProto
name|getModelDesired
parameter_list|(
name|Messages
operator|.
name|EmptyPayloadProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getModelDesired
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getModelDesiredAppconf (Messages.EmptyPayloadProto request)
specifier|public
name|Messages
operator|.
name|WrappedJsonProto
name|getModelDesiredAppconf
parameter_list|(
name|Messages
operator|.
name|EmptyPayloadProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getModelDesiredAppconf
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getModelDesiredResources (Messages.EmptyPayloadProto request)
specifier|public
name|Messages
operator|.
name|WrappedJsonProto
name|getModelDesiredResources
parameter_list|(
name|Messages
operator|.
name|EmptyPayloadProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getModelDesiredResources
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getModelResolved (Messages.EmptyPayloadProto request)
specifier|public
name|Messages
operator|.
name|WrappedJsonProto
name|getModelResolved
parameter_list|(
name|Messages
operator|.
name|EmptyPayloadProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getModelResolved
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getModelResolvedAppconf (Messages.EmptyPayloadProto request)
specifier|public
name|Messages
operator|.
name|WrappedJsonProto
name|getModelResolvedAppconf
parameter_list|(
name|Messages
operator|.
name|EmptyPayloadProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getModelResolvedAppconf
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getModelResolvedResources (Messages.EmptyPayloadProto request)
specifier|public
name|Messages
operator|.
name|WrappedJsonProto
name|getModelResolvedResources
parameter_list|(
name|Messages
operator|.
name|EmptyPayloadProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getModelResolvedResources
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveResources (Messages.EmptyPayloadProto request)
specifier|public
name|Messages
operator|.
name|WrappedJsonProto
name|getLiveResources
parameter_list|(
name|Messages
operator|.
name|EmptyPayloadProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getLiveResources
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getClientCertificateStore (Messages.GetCertificateStoreRequestProto request)
specifier|public
name|Messages
operator|.
name|GetCertificateStoreResponseProto
name|getClientCertificateStore
parameter_list|(
name|Messages
operator|.
name|GetCertificateStoreRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|endpoint
operator|.
name|getClientCertificateStore
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|request
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
name|convert
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

