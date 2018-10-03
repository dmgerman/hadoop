begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license  * agreements. See the NOTICE file distributed with this work for additional  * information regarding  * copyright ownership. The ASF licenses this file to you under the Apache  * License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the  * License. You may obtain a  * copy of the License at  *  *<p>http://www.apache.org/licenses/LICENSE-2.0  *  *<p>Unless required by applicable law or agreed to in writing, software  * distributed under the  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR  * CONDITIONS OF ANY KIND, either  * express or implied. See the License for the specific language governing  * permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

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
name|BlockingService
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatRequestProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|PipelineReportsProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionRequestProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMVersionResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMRegisteredResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ReregisterCommandProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
operator|.
name|Type
operator|.
name|closeContainerCommand
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
operator|.
name|Type
operator|.
name|deleteBlocksCommand
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
operator|.
name|Type
operator|.
name|replicateContainerCommand
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMCommandProto
operator|.
name|Type
operator|.
name|reregisterCommand
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
name|hdds
operator|.
name|scm
operator|.
name|HddsServerUtil
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
name|hdds
operator|.
name|scm
operator|.
name|events
operator|.
name|SCMEvents
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|ReportFromDatanode
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineReportFromDatanode
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
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventPublisher
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
name|IOUtils
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
name|ozone
operator|.
name|protocol
operator|.
name|StorageContainerDatanodeProtocol
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|CloseContainerCommand
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|DeleteBlocksCommand
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|RegisteredCommand
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|ReplicateContainerCommand
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
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
name|ozone
operator|.
name|protocolPB
operator|.
name|StorageContainerDatanodeProtocolPB
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
name|ozone
operator|.
name|protocolPB
operator|.
name|StorageContainerDatanodeProtocolServerSideTranslatorPB
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
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DATANODE_ADDRESS_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HANDLER_COUNT_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HANDLER_COUNT_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|events
operator|.
name|SCMEvents
operator|.
name|PIPELINE_REPORT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
operator|.
name|startRpcServer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
operator|.
name|ServerUtils
operator|.
name|updateRPCListenAddress
import|;
end_import

begin_comment
comment|/**  * Protocol Handler for Datanode Protocol.  */
end_comment

begin_class
DECL|class|SCMDatanodeProtocolServer
specifier|public
class|class
name|SCMDatanodeProtocolServer
implements|implements
name|StorageContainerDatanodeProtocol
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SCMDatanodeProtocolServer
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The RPC server that listens to requests from DataNodes.    */
DECL|field|datanodeRpcServer
specifier|private
specifier|final
name|RPC
operator|.
name|Server
name|datanodeRpcServer
decl_stmt|;
DECL|field|scm
specifier|private
specifier|final
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|datanodeRpcAddress
specifier|private
specifier|final
name|InetSocketAddress
name|datanodeRpcAddress
decl_stmt|;
DECL|field|heartbeatDispatcher
specifier|private
specifier|final
name|SCMDatanodeHeartbeatDispatcher
name|heartbeatDispatcher
decl_stmt|;
DECL|field|eventPublisher
specifier|private
specifier|final
name|EventPublisher
name|eventPublisher
decl_stmt|;
DECL|method|SCMDatanodeProtocolServer (final OzoneConfiguration conf, StorageContainerManager scm, EventPublisher eventPublisher)
specifier|public
name|SCMDatanodeProtocolServer
parameter_list|(
specifier|final
name|OzoneConfiguration
name|conf
parameter_list|,
name|StorageContainerManager
name|scm
parameter_list|,
name|EventPublisher
name|eventPublisher
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|scm
argument_list|,
literal|"SCM cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|eventPublisher
argument_list|,
literal|"EventPublisher cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|scm
operator|=
name|scm
expr_stmt|;
name|this
operator|.
name|eventPublisher
operator|=
name|eventPublisher
expr_stmt|;
specifier|final
name|int
name|handlerCount
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_HANDLER_COUNT_KEY
argument_list|,
name|OZONE_SCM_HANDLER_COUNT_DEFAULT
argument_list|)
decl_stmt|;
name|heartbeatDispatcher
operator|=
operator|new
name|SCMDatanodeHeartbeatDispatcher
argument_list|(
name|scm
operator|.
name|getScmNodeManager
argument_list|()
argument_list|,
name|eventPublisher
argument_list|)
expr_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|StorageContainerDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|BlockingService
name|dnProtoPbService
init|=
name|StorageContainerDatanodeProtocolProtos
operator|.
name|StorageContainerDatanodeProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
operator|new
name|StorageContainerDatanodeProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|datanodeRpcAddr
init|=
name|HddsServerUtil
operator|.
name|getScmDataNodeBindAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|datanodeRpcServer
operator|=
name|startRpcServer
argument_list|(
name|conf
argument_list|,
name|datanodeRpcAddr
argument_list|,
name|StorageContainerDatanodeProtocolPB
operator|.
name|class
argument_list|,
name|dnProtoPbService
argument_list|,
name|handlerCount
argument_list|)
expr_stmt|;
name|datanodeRpcAddress
operator|=
name|updateRPCListenAddress
argument_list|(
name|conf
argument_list|,
name|OZONE_SCM_DATANODE_ADDRESS_KEY
argument_list|,
name|datanodeRpcAddr
argument_list|,
name|datanodeRpcServer
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
name|StorageContainerManager
operator|.
name|buildRpcServerStartMessage
argument_list|(
literal|"RPC server for DataNodes"
argument_list|,
name|datanodeRpcAddress
argument_list|)
argument_list|)
expr_stmt|;
name|datanodeRpcServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|getDatanodeRpcAddress ()
specifier|public
name|InetSocketAddress
name|getDatanodeRpcAddress
parameter_list|()
block|{
return|return
name|datanodeRpcAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion (SCMVersionRequestProto versionRequest)
specifier|public
name|SCMVersionResponseProto
name|getVersion
parameter_list|(
name|SCMVersionRequestProto
name|versionRequest
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|scm
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|getVersion
argument_list|(
name|versionRequest
argument_list|)
operator|.
name|getProtobufMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|register ( HddsProtos.DatanodeDetailsProto datanodeDetailsProto, NodeReportProto nodeReport, ContainerReportsProto containerReportsProto, PipelineReportsProto pipelineReportsProto)
specifier|public
name|SCMRegisteredResponseProto
name|register
parameter_list|(
name|HddsProtos
operator|.
name|DatanodeDetailsProto
name|datanodeDetailsProto
parameter_list|,
name|NodeReportProto
name|nodeReport
parameter_list|,
name|ContainerReportsProto
name|containerReportsProto
parameter_list|,
name|PipelineReportsProto
name|pipelineReportsProto
parameter_list|)
throws|throws
name|IOException
block|{
name|DatanodeDetails
name|datanodeDetails
init|=
name|DatanodeDetails
operator|.
name|getFromProtoBuf
argument_list|(
name|datanodeDetailsProto
argument_list|)
decl_stmt|;
comment|// TODO : Return the list of Nodes that forms the SCM HA.
name|RegisteredCommand
name|registeredCommand
init|=
name|scm
operator|.
name|getScmNodeManager
argument_list|()
operator|.
name|register
argument_list|(
name|datanodeDetails
argument_list|,
name|nodeReport
argument_list|,
name|pipelineReportsProto
argument_list|)
decl_stmt|;
if|if
condition|(
name|registeredCommand
operator|.
name|getError
argument_list|()
operator|==
name|SCMRegisteredResponseProto
operator|.
name|ErrorCode
operator|.
name|success
condition|)
block|{
name|scm
operator|.
name|getContainerManager
argument_list|()
operator|.
name|processContainerReports
argument_list|(
name|datanodeDetails
argument_list|,
name|containerReportsProto
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|eventPublisher
operator|.
name|fireEvent
argument_list|(
name|SCMEvents
operator|.
name|NODE_REGISTRATION_CONT_REPORT
argument_list|,
operator|new
name|NodeRegistrationContainerReport
argument_list|(
name|datanodeDetails
argument_list|,
name|containerReportsProto
argument_list|)
argument_list|)
expr_stmt|;
name|eventPublisher
operator|.
name|fireEvent
argument_list|(
name|PIPELINE_REPORT
argument_list|,
operator|new
name|PipelineReportFromDatanode
argument_list|(
name|datanodeDetails
argument_list|,
name|pipelineReportsProto
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getRegisteredResponse
argument_list|(
name|registeredCommand
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRegisteredResponse ( RegisteredCommand cmd)
specifier|public
specifier|static
name|SCMRegisteredResponseProto
name|getRegisteredResponse
parameter_list|(
name|RegisteredCommand
name|cmd
parameter_list|)
block|{
return|return
name|SCMRegisteredResponseProto
operator|.
name|newBuilder
argument_list|()
comment|// TODO : Fix this later when we have multiple SCM support.
comment|// .setAddressList(addressList)
operator|.
name|setErrorCode
argument_list|(
name|cmd
operator|.
name|getError
argument_list|()
argument_list|)
operator|.
name|setClusterID
argument_list|(
name|cmd
operator|.
name|getClusterID
argument_list|()
argument_list|)
operator|.
name|setDatanodeUUID
argument_list|(
name|cmd
operator|.
name|getDatanodeUUID
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sendHeartbeat ( SCMHeartbeatRequestProto heartbeat)
specifier|public
name|SCMHeartbeatResponseProto
name|sendHeartbeat
parameter_list|(
name|SCMHeartbeatRequestProto
name|heartbeat
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|SCMCommandProto
argument_list|>
name|cmdResponses
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SCMCommand
name|cmd
range|:
name|heartbeatDispatcher
operator|.
name|dispatch
argument_list|(
name|heartbeat
argument_list|)
control|)
block|{
name|cmdResponses
operator|.
name|add
argument_list|(
name|getCommandResponse
argument_list|(
name|cmd
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|SCMHeartbeatResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDatanodeUUID
argument_list|(
name|heartbeat
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
operator|.
name|addAllCommands
argument_list|(
name|cmdResponses
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Returns a SCMCommandRepose from the SCM Command.    *    * @param cmd - Cmd    * @return SCMCommandResponseProto    * @throws IOException    */
annotation|@
name|VisibleForTesting
DECL|method|getCommandResponse (SCMCommand cmd)
specifier|public
name|SCMCommandProto
name|getCommandResponse
parameter_list|(
name|SCMCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|SCMCommandProto
operator|.
name|Builder
name|builder
init|=
name|SCMCommandProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|cmd
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|reregisterCommand
case|:
return|return
name|builder
operator|.
name|setCommandType
argument_list|(
name|reregisterCommand
argument_list|)
operator|.
name|setReregisterCommandProto
argument_list|(
name|ReregisterCommandProto
operator|.
name|getDefaultInstance
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|deleteBlocksCommand
case|:
comment|// Once SCM sends out the deletion message, increment the count.
comment|// this is done here instead of when SCM receives the ACK, because
comment|// DN might not be able to response the ACK for sometime. In case
comment|// it times out, SCM needs to re-send the message some more times.
name|List
argument_list|<
name|Long
argument_list|>
name|txs
init|=
operator|(
operator|(
name|DeleteBlocksCommand
operator|)
name|cmd
operator|)
operator|.
name|blocksTobeDeleted
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|tx
lambda|->
name|tx
operator|.
name|getTxID
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|scm
operator|.
name|getScmBlockManager
argument_list|()
operator|.
name|getDeletedBlockLog
argument_list|()
operator|.
name|incrementCount
argument_list|(
name|txs
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|setCommandType
argument_list|(
name|deleteBlocksCommand
argument_list|)
operator|.
name|setDeleteBlocksCommandProto
argument_list|(
operator|(
operator|(
name|DeleteBlocksCommand
operator|)
name|cmd
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|closeContainerCommand
case|:
return|return
name|builder
operator|.
name|setCommandType
argument_list|(
name|closeContainerCommand
argument_list|)
operator|.
name|setCloseContainerCommandProto
argument_list|(
operator|(
operator|(
name|CloseContainerCommand
operator|)
name|cmd
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|replicateContainerCommand
case|:
return|return
name|builder
operator|.
name|setCommandType
argument_list|(
name|replicateContainerCommand
argument_list|)
operator|.
name|setReplicateContainerCommandProto
argument_list|(
operator|(
operator|(
name|ReplicateContainerCommand
operator|)
name|cmd
operator|)
operator|.
name|getProto
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not implemented"
argument_list|)
throw|;
block|}
block|}
DECL|method|join ()
specifier|public
name|void
name|join
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Join RPC server for DataNodes"
argument_list|)
expr_stmt|;
name|datanodeRpcServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping the RPC server for DataNodes"
argument_list|)
expr_stmt|;
name|datanodeRpcServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|" datanodeRpcServer stop failed."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|scm
operator|.
name|getScmNodeManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wrapper class for events with the datanode origin.    */
DECL|class|NodeRegistrationContainerReport
specifier|public
specifier|static
class|class
name|NodeRegistrationContainerReport
extends|extends
name|ReportFromDatanode
argument_list|<
name|ContainerReportsProto
argument_list|>
block|{
DECL|method|NodeRegistrationContainerReport (DatanodeDetails datanodeDetails, ContainerReportsProto report)
specifier|public
name|NodeRegistrationContainerReport
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|ContainerReportsProto
name|report
parameter_list|)
block|{
name|super
argument_list|(
name|datanodeDetails
argument_list|,
name|report
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

