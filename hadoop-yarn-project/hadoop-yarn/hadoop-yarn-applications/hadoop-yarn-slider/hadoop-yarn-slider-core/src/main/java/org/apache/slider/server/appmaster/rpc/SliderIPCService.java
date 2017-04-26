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
name|service
operator|.
name|AbstractService
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
name|FinalApplicationStatus
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Application
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
name|types
operator|.
name|ApplicationLivenessInformation
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
name|types
operator|.
name|ComponentInformation
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
name|types
operator|.
name|ContainerInformation
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
name|types
operator|.
name|NodeInformation
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
name|types
operator|.
name|NodeInformationList
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
name|core
operator|.
name|exceptions
operator|.
name|ServiceNotReadyException
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
name|core
operator|.
name|main
operator|.
name|LauncherExitCodes
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
name|core
operator|.
name|persist
operator|.
name|JsonSerDeser
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
name|server
operator|.
name|appmaster
operator|.
name|AppMasterActionOperations
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|ActionFlexCluster
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|ActionHalt
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|ActionKillContainer
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|ActionStopSlider
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|ActionUpgradeContainers
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|AsyncAction
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
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|QueueAccess
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
name|server
operator|.
name|appmaster
operator|.
name|management
operator|.
name|MetricsAndMonitoring
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
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|RoleInstance
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
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|StateAccessForProviders
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
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|application
operator|.
name|resources
operator|.
name|ContentCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|RestTypeMarshalling
operator|.
name|marshall
import|;
end_import

begin_import
import|import static
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
name|web
operator|.
name|rest
operator|.
name|RestPaths
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Implement the {@link SliderClusterProtocol}.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|SliderIPCService
specifier|public
class|class
name|SliderIPCService
extends|extends
name|AbstractService
implements|implements
name|SliderClusterProtocol
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SliderIPCService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|actionQueues
specifier|private
specifier|final
name|QueueAccess
name|actionQueues
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|StateAccessForProviders
name|state
decl_stmt|;
DECL|field|metricsAndMonitoring
specifier|private
specifier|final
name|MetricsAndMonitoring
name|metricsAndMonitoring
decl_stmt|;
DECL|field|amOperations
specifier|private
specifier|final
name|AppMasterActionOperations
name|amOperations
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|ContentCache
name|cache
decl_stmt|;
DECL|field|jsonSerDeser
specifier|private
specifier|static
specifier|final
name|JsonSerDeser
argument_list|<
name|Application
argument_list|>
name|jsonSerDeser
init|=
operator|new
name|JsonSerDeser
argument_list|<
name|Application
argument_list|>
argument_list|(
name|Application
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * This is the prefix used for metrics    */
DECL|field|METRICS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|METRICS_PREFIX
init|=
literal|"org.apache.slider.api.SliderIPCService."
decl_stmt|;
comment|/**    * Constructor    * @param amOperations access to any AM operations    * @param state state view    * @param actionQueues queues for actions    * @param metricsAndMonitoring metrics    * @param cache    */
DECL|method|SliderIPCService (AppMasterActionOperations amOperations, StateAccessForProviders state, QueueAccess actionQueues, MetricsAndMonitoring metricsAndMonitoring, ContentCache cache)
specifier|public
name|SliderIPCService
parameter_list|(
name|AppMasterActionOperations
name|amOperations
parameter_list|,
name|StateAccessForProviders
name|state
parameter_list|,
name|QueueAccess
name|actionQueues
parameter_list|,
name|MetricsAndMonitoring
name|metricsAndMonitoring
parameter_list|,
name|ContentCache
name|cache
parameter_list|)
block|{
name|super
argument_list|(
literal|"SliderIPCService"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|amOperations
operator|!=
literal|null
argument_list|,
literal|"null amOperations"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|state
operator|!=
literal|null
argument_list|,
literal|"null appState"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|actionQueues
operator|!=
literal|null
argument_list|,
literal|"null actionQueues"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|metricsAndMonitoring
operator|!=
literal|null
argument_list|,
literal|"null metricsAndMonitoring"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|cache
operator|!=
literal|null
argument_list|,
literal|"null cache"
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|actionQueues
operator|=
name|actionQueues
expr_stmt|;
name|this
operator|.
name|metricsAndMonitoring
operator|=
name|metricsAndMonitoring
expr_stmt|;
name|this
operator|.
name|amOperations
operator|=
name|amOperations
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
comment|//SliderClusterProtocol
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
return|return
name|ProtocolSignature
operator|.
name|getProtocolSignature
argument_list|(
name|this
argument_list|,
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|clientMethodsHash
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|//SliderClusterProtocol
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
comment|/**    * General actions to perform on a slider RPC call coming in    * @param operation operation to log    * @throws IOException problems    * @throws ServiceNotReadyException if the RPC service is constructed    * but not fully initialized    */
DECL|method|onRpcCall (String operation)
specifier|protected
name|void
name|onRpcCall
parameter_list|(
name|String
name|operation
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Received call to {}"
argument_list|,
name|operation
argument_list|)
expr_stmt|;
name|metricsAndMonitoring
operator|.
name|markMeterAndCounter
argument_list|(
name|METRICS_PREFIX
operator|+
name|operation
argument_list|)
expr_stmt|;
block|}
comment|/**    * Schedule an action    * @param action for delayed execution    */
DECL|method|schedule (AsyncAction action)
specifier|public
name|void
name|schedule
parameter_list|(
name|AsyncAction
name|action
parameter_list|)
block|{
name|actionQueues
operator|.
name|schedule
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
comment|/**    * Queue an action for immediate execution in the executor thread    * @param action action to execute    */
DECL|method|queue (AsyncAction action)
specifier|public
name|void
name|queue
parameter_list|(
name|AsyncAction
name|action
parameter_list|)
block|{
name|actionQueues
operator|.
name|put
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|//SliderClusterProtocol
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
name|onRpcCall
argument_list|(
literal|"stop"
argument_list|)
expr_stmt|;
name|String
name|message
init|=
name|request
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|message
operator|=
literal|"application stopped by client"
expr_stmt|;
block|}
name|ActionStopSlider
name|stopSlider
init|=
operator|new
name|ActionStopSlider
argument_list|(
name|message
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|LauncherExitCodes
operator|.
name|EXIT_SUCCESS
argument_list|,
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"SliderAppMasterApi.stopCluster: {}"
argument_list|,
name|stopSlider
argument_list|)
expr_stmt|;
name|schedule
argument_list|(
name|stopSlider
argument_list|)
expr_stmt|;
return|return
name|Messages
operator|.
name|StopClusterResponseProto
operator|.
name|getDefaultInstance
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//SliderClusterProtocol
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
name|onRpcCall
argument_list|(
literal|"upgrade"
argument_list|)
expr_stmt|;
name|String
name|message
init|=
name|request
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|message
operator|=
literal|"application containers upgraded by client"
expr_stmt|;
block|}
name|ActionUpgradeContainers
name|upgradeContainers
init|=
operator|new
name|ActionUpgradeContainers
argument_list|(
literal|"Upgrade containers"
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|LauncherExitCodes
operator|.
name|EXIT_SUCCESS
argument_list|,
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
argument_list|,
name|request
operator|.
name|getContainerList
argument_list|()
argument_list|,
name|request
operator|.
name|getComponentList
argument_list|()
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"SliderAppMasterApi.upgradeContainers: {}"
argument_list|,
name|upgradeContainers
argument_list|)
expr_stmt|;
name|schedule
argument_list|(
name|upgradeContainers
argument_list|)
expr_stmt|;
return|return
name|Messages
operator|.
name|UpgradeContainersResponseProto
operator|.
name|getDefaultInstance
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|flexComponents ( Messages.FlexComponentsRequestProto request)
specifier|public
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
block|{
name|onRpcCall
argument_list|(
literal|"flex"
argument_list|)
expr_stmt|;
name|schedule
argument_list|(
operator|new
name|ActionFlexCluster
argument_list|(
literal|"flex"
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Messages
operator|.
name|FlexComponentsResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//SliderClusterProtocol
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
name|onRpcCall
argument_list|(
literal|"getstatus"
argument_list|)
expr_stmt|;
name|String
name|result
decl_stmt|;
comment|//quick update
comment|//query and json-ify
name|Application
name|application
init|=
name|state
operator|.
name|refreshClusterStatus
argument_list|()
decl_stmt|;
name|String
name|stat
init|=
name|jsonSerDeser
operator|.
name|toJson
argument_list|(
name|application
argument_list|)
decl_stmt|;
return|return
name|Messages
operator|.
name|GetJSONClusterStatusResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterSpec
argument_list|(
name|stat
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//SliderClusterProtocol
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
name|onRpcCall
argument_list|(
literal|"listnodes)"
argument_list|)
expr_stmt|;
name|String
name|role
init|=
name|request
operator|.
name|getRole
argument_list|()
decl_stmt|;
name|Messages
operator|.
name|ListNodeUUIDsByRoleResponseProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|ListNodeUUIDsByRoleResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|nodes
init|=
name|state
operator|.
name|enumLiveInstancesInRole
argument_list|(
name|role
argument_list|)
decl_stmt|;
for|for
control|(
name|RoleInstance
name|node
range|:
name|nodes
control|)
block|{
name|builder
operator|.
name|addUuid
argument_list|(
name|node
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//SliderClusterProtocol
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
name|onRpcCall
argument_list|(
literal|"getnode"
argument_list|)
expr_stmt|;
name|RoleInstance
name|instance
init|=
name|state
operator|.
name|getLiveInstanceByContainerID
argument_list|(
name|request
operator|.
name|getUuid
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Messages
operator|.
name|GetNodeResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterNode
argument_list|(
name|instance
operator|.
name|toProtobuf
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//SliderClusterProtocol
DECL|method|getClusterNodes ( Messages.GetClusterNodesRequestProto request)
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
name|onRpcCall
argument_list|(
literal|"getclusternodes"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RoleInstance
argument_list|>
name|clusterNodes
init|=
name|state
operator|.
name|getLiveInstancesByContainerIDs
argument_list|(
name|request
operator|.
name|getUuidList
argument_list|()
argument_list|)
decl_stmt|;
name|Messages
operator|.
name|GetClusterNodesResponseProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|GetClusterNodesResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|RoleInstance
name|node
range|:
name|clusterNodes
control|)
block|{
name|builder
operator|.
name|addClusterNode
argument_list|(
name|node
operator|.
name|toProtobuf
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//at this point: a possibly empty list of nodes
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
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
name|onRpcCall
argument_list|(
literal|"echo"
argument_list|)
expr_stmt|;
name|Messages
operator|.
name|EchoResponseProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|EchoResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|String
name|text
init|=
name|request
operator|.
name|getText
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Echo request size ={}"
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|text
argument_list|)
expr_stmt|;
comment|//now return it
name|builder
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
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
name|onRpcCall
argument_list|(
literal|"killcontainer"
argument_list|)
expr_stmt|;
name|String
name|containerID
init|=
name|request
operator|.
name|getId
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Kill Container {}"
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
comment|//throws NoSuchNodeException if it is missing
name|RoleInstance
name|instance
init|=
name|state
operator|.
name|getLiveInstanceByContainerID
argument_list|(
name|containerID
argument_list|)
decl_stmt|;
name|queue
argument_list|(
operator|new
name|ActionKillContainer
argument_list|(
name|instance
operator|.
name|getContainerId
argument_list|()
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|amOperations
argument_list|)
argument_list|)
expr_stmt|;
name|Messages
operator|.
name|KillContainerResponseProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|KillContainerResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|amSuicide ( Messages.AMSuicideRequestProto request)
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
name|onRpcCall
argument_list|(
literal|"amsuicide"
argument_list|)
expr_stmt|;
name|int
name|signal
init|=
name|request
operator|.
name|getSignal
argument_list|()
decl_stmt|;
name|String
name|text
init|=
name|request
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|==
literal|null
condition|)
block|{
name|text
operator|=
literal|""
expr_stmt|;
block|}
name|int
name|delay
init|=
name|request
operator|.
name|getDelay
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"AM Suicide with signal {}, message {} delay = {}"
argument_list|,
name|signal
argument_list|,
name|text
argument_list|,
name|delay
argument_list|)
expr_stmt|;
name|ActionHalt
name|action
init|=
operator|new
name|ActionHalt
argument_list|(
name|signal
argument_list|,
name|text
argument_list|,
name|delay
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|schedule
argument_list|(
name|action
argument_list|)
expr_stmt|;
return|return
name|Messages
operator|.
name|AMSuicideResponseProto
operator|.
name|getDefaultInstance
argument_list|()
return|;
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
name|ApplicationLivenessInformation
name|info
init|=
name|state
operator|.
name|getApplicationLivenessInformation
argument_list|()
decl_stmt|;
return|return
name|marshall
argument_list|(
name|info
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLiveContainers ( Messages.GetLiveContainersRequestProto request)
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
name|Map
argument_list|<
name|String
argument_list|,
name|ContainerInformation
argument_list|>
name|infoMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|ContainerInformation
argument_list|>
operator|)
name|cache
operator|.
name|lookupWithIOE
argument_list|(
name|LIVE_CONTAINERS
argument_list|)
decl_stmt|;
name|Messages
operator|.
name|GetLiveContainersResponseProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|GetLiveContainersResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ContainerInformation
argument_list|>
name|entry
range|:
name|infoMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|addNames
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addContainers
argument_list|(
name|marshall
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
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
name|String
name|containerId
init|=
name|request
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|RoleInstance
name|id
init|=
name|state
operator|.
name|getLiveInstanceByContainerID
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|ContainerInformation
name|containerInformation
init|=
name|id
operator|.
name|serialize
argument_list|()
decl_stmt|;
return|return
name|marshall
argument_list|(
name|containerInformation
argument_list|)
return|;
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
name|Map
argument_list|<
name|String
argument_list|,
name|ComponentInformation
argument_list|>
name|infoMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|ComponentInformation
argument_list|>
operator|)
name|cache
operator|.
name|lookupWithIOE
argument_list|(
name|LIVE_COMPONENTS
argument_list|)
decl_stmt|;
name|Messages
operator|.
name|GetLiveComponentsResponseProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|GetLiveComponentsResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ComponentInformation
argument_list|>
name|entry
range|:
name|infoMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|addNames
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addComponents
argument_list|(
name|marshall
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
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
name|String
name|name
init|=
name|request
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|marshall
argument_list|(
name|state
operator|.
name|getComponentInformation
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Unknown component: "
operator|+
name|name
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
name|NodeInformationList
name|info
init|=
operator|(
name|NodeInformationList
operator|)
name|cache
operator|.
name|lookupWithIOE
argument_list|(
name|LIVE_NODES
argument_list|)
decl_stmt|;
name|Messages
operator|.
name|GetLiveNodesResponseProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|GetLiveNodesResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeInformation
name|nodeInformation
range|:
name|info
control|)
block|{
name|builder
operator|.
name|addNodes
argument_list|(
name|marshall
argument_list|(
name|nodeInformation
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
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
name|String
name|name
init|=
name|request
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeInformation
name|nodeInformation
init|=
name|state
operator|.
name|getNodeInformation
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeInformation
operator|!=
literal|null
condition|)
block|{
return|return
name|marshall
argument_list|(
name|nodeInformation
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Unknown host: "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
DECL|method|wrap (String json)
specifier|private
name|Messages
operator|.
name|WrappedJsonProto
name|wrap
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|Messages
operator|.
name|WrappedJsonProto
operator|.
name|Builder
name|builder
init|=
name|Messages
operator|.
name|WrappedJsonProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setJson
argument_list|(
name|json
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

