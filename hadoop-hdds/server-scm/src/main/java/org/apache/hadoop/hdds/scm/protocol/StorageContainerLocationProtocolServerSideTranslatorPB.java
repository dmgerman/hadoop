begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.protocol
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
name|protocol
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
name|java
operator|.
name|util
operator|.
name|List
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
name|StorageContainerLocationProtocolProtos
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ActivatePipelineRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ActivatePipelineResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ClosePipelineRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ClosePipelineResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ContainerRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ContainerResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|DeactivatePipelineRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|DeactivatePipelineResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ForceExitSafeModeRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ForceExitSafeModeResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|GetContainerRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|GetContainerResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|GetContainerWithPipelineRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|GetContainerWithPipelineResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|InSafeModeRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|InSafeModeResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ListPipelineRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ListPipelineResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|NodeQueryResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ObjectStageChangeRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ObjectStageChangeResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ReplicationManagerStatusRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ReplicationManagerStatusResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|SCMDeleteContainerRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|SCMDeleteContainerResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|SCMListContainerRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|SCMListContainerResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ScmContainerLocationRequest
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ScmContainerLocationResponse
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
name|StorageContainerLocationProtocolProtos
operator|.
name|ScmContainerLocationResponse
operator|.
name|Status
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
name|StorageContainerLocationProtocolProtos
operator|.
name|StartReplicationManagerRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|StartReplicationManagerResponseProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|StopReplicationManagerRequestProto
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
name|StorageContainerLocationProtocolProtos
operator|.
name|StopReplicationManagerResponseProto
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
name|ScmInfo
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
name|container
operator|.
name|ContainerInfo
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerWithPipeline
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
name|pipeline
operator|.
name|Pipeline
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
name|protocolPB
operator|.
name|StorageContainerLocationProtocolPB
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
name|OzoneProtocolMessageDispatcher
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
name|ProtocolMessageMetrics
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

begin_comment
comment|/**  * This class is the server-side translator that forwards requests received on  * {@link StorageContainerLocationProtocolPB} to the  * {@link StorageContainerLocationProtocol} server implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageContainerLocationProtocolServerSideTranslatorPB
specifier|public
specifier|final
class|class
name|StorageContainerLocationProtocolServerSideTranslatorPB
implements|implements
name|StorageContainerLocationProtocolPB
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
name|StorageContainerLocationProtocolServerSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|impl
specifier|private
specifier|final
name|StorageContainerLocationProtocol
name|impl
decl_stmt|;
specifier|private
name|OzoneProtocolMessageDispatcher
argument_list|<
name|ScmContainerLocationRequest
argument_list|,
name|ScmContainerLocationResponse
argument_list|>
DECL|field|dispatcher
name|dispatcher
decl_stmt|;
comment|/**    * Creates a new StorageContainerLocationProtocolServerSideTranslatorPB.    *    * @param impl            {@link StorageContainerLocationProtocol} server    *                        implementation    * @param protocolMetrics    */
DECL|method|StorageContainerLocationProtocolServerSideTranslatorPB ( StorageContainerLocationProtocol impl, ProtocolMessageMetrics protocolMetrics)
specifier|public
name|StorageContainerLocationProtocolServerSideTranslatorPB
parameter_list|(
name|StorageContainerLocationProtocol
name|impl
parameter_list|,
name|ProtocolMessageMetrics
name|protocolMetrics
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|impl
operator|=
name|impl
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
operator|new
name|OzoneProtocolMessageDispatcher
argument_list|<>
argument_list|(
literal|"ScmContainerLocation"
argument_list|,
name|protocolMetrics
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|submitRequest (RpcController controller, ScmContainerLocationRequest request)
specifier|public
name|ScmContainerLocationResponse
name|submitRequest
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ScmContainerLocationRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
return|return
name|dispatcher
operator|.
name|processRequest
argument_list|(
name|request
argument_list|,
name|this
operator|::
name|processRequest
argument_list|,
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|processRequest ( ScmContainerLocationRequest request)
specifier|public
name|ScmContainerLocationResponse
name|processRequest
parameter_list|(
name|ScmContainerLocationRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
switch|switch
condition|(
name|request
operator|.
name|getCmdType
argument_list|()
condition|)
block|{
case|case
name|AllocateContainer
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setContainerResponse
argument_list|(
name|allocateContainer
argument_list|(
name|request
operator|.
name|getContainerRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|GetContainer
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setGetContainerResponse
argument_list|(
name|getContainer
argument_list|(
name|request
operator|.
name|getGetContainerRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|GetContainerWithPipeline
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setGetContainerWithPipelineResponse
argument_list|(
name|getContainerWithPipeline
argument_list|(
name|request
operator|.
name|getGetContainerWithPipelineRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|ListContainer
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setScmListContainerResponse
argument_list|(
name|listContainer
argument_list|(
name|request
operator|.
name|getScmListContainerRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|QueryNode
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setNodeQueryResponse
argument_list|(
name|queryNode
argument_list|(
name|request
operator|.
name|getNodeQueryRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|NotifyObjectStageChange
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setObjectStageChangeResponse
argument_list|(
name|notifyObjectStageChange
argument_list|(
name|request
operator|.
name|getObjectStageChangeRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|ListPipelines
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setListPipelineResponse
argument_list|(
name|listPipelines
argument_list|(
name|request
operator|.
name|getListPipelineRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|ActivatePipeline
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setActivatePipelineResponse
argument_list|(
name|activatePipeline
argument_list|(
name|request
operator|.
name|getActivatePipelineRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|GetScmInfo
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setGetScmInfoResponse
argument_list|(
name|getScmInfo
argument_list|(
name|request
operator|.
name|getGetScmInfoRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|InSafeMode
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setInSafeModeResponse
argument_list|(
name|inSafeMode
argument_list|(
name|request
operator|.
name|getInSafeModeRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|ForceExitSafeMode
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setForceExitSafeModeResponse
argument_list|(
name|forceExitSafeMode
argument_list|(
name|request
operator|.
name|getForceExitSafeModeRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|StartReplicationManager
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setStartReplicationManagerResponse
argument_list|(
name|startReplicationManager
argument_list|(
name|request
operator|.
name|getStartReplicationManagerRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|StopReplicationManager
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setStopReplicationManagerResponse
argument_list|(
name|stopReplicationManager
argument_list|(
name|request
operator|.
name|getStopReplicationManagerRequest
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
case|case
name|GetReplicationManagerStatus
case|:
return|return
name|ScmContainerLocationResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|setReplicationManagerStatusResponse
argument_list|(
name|getReplicationManagerStatus
argument_list|(
name|request
operator|.
name|getSeplicationManagerStatusRequest
argument_list|()
argument_list|)
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
literal|"Unknown command type: "
operator|+
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
throw|;
block|}
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
DECL|method|allocateContainer (ContainerRequestProto request)
specifier|public
name|ContainerResponseProto
name|allocateContainer
parameter_list|(
name|ContainerRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|impl
operator|.
name|allocateContainer
argument_list|(
name|request
operator|.
name|getReplicationType
argument_list|()
argument_list|,
name|request
operator|.
name|getReplicationFactor
argument_list|()
argument_list|,
name|request
operator|.
name|getOwner
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ContainerResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerWithPipeline
argument_list|(
name|containerWithPipeline
operator|.
name|getProtobuf
argument_list|()
argument_list|)
operator|.
name|setErrorCode
argument_list|(
name|ContainerResponseProto
operator|.
name|Error
operator|.
name|success
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getContainer ( GetContainerRequestProto request)
specifier|public
name|GetContainerResponseProto
name|getContainer
parameter_list|(
name|GetContainerRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerInfo
name|container
init|=
name|impl
operator|.
name|getContainer
argument_list|(
name|request
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|GetContainerResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerInfo
argument_list|(
name|container
operator|.
name|getProtobuf
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getContainerWithPipeline ( GetContainerWithPipelineRequestProto request)
specifier|public
name|GetContainerWithPipelineResponseProto
name|getContainerWithPipeline
parameter_list|(
name|GetContainerWithPipelineRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerWithPipeline
name|container
init|=
name|impl
operator|.
name|getContainerWithPipeline
argument_list|(
name|request
operator|.
name|getContainerID
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|GetContainerWithPipelineResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerWithPipeline
argument_list|(
name|container
operator|.
name|getProtobuf
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|listContainer ( SCMListContainerRequestProto request)
specifier|public
name|SCMListContainerResponseProto
name|listContainer
parameter_list|(
name|SCMListContainerRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|startContainerID
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
operator|-
literal|1
decl_stmt|;
comment|// Arguments check.
if|if
condition|(
name|request
operator|.
name|hasStartContainerID
argument_list|()
condition|)
block|{
comment|// End container name is given.
name|startContainerID
operator|=
name|request
operator|.
name|getStartContainerID
argument_list|()
expr_stmt|;
block|}
name|count
operator|=
name|request
operator|.
name|getCount
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containerList
init|=
name|impl
operator|.
name|listContainer
argument_list|(
name|startContainerID
argument_list|,
name|count
argument_list|)
decl_stmt|;
name|SCMListContainerResponseProto
operator|.
name|Builder
name|builder
init|=
name|SCMListContainerResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|ContainerInfo
name|container
range|:
name|containerList
control|)
block|{
name|builder
operator|.
name|addContainers
argument_list|(
name|container
operator|.
name|getProtobuf
argument_list|()
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
DECL|method|deleteContainer ( SCMDeleteContainerRequestProto request)
specifier|public
name|SCMDeleteContainerResponseProto
name|deleteContainer
parameter_list|(
name|SCMDeleteContainerRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|deleteContainer
argument_list|(
name|request
operator|.
name|getContainerID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|SCMDeleteContainerResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|queryNode ( StorageContainerLocationProtocolProtos.NodeQueryRequestProto request)
specifier|public
name|NodeQueryResponseProto
name|queryNode
parameter_list|(
name|StorageContainerLocationProtocolProtos
operator|.
name|NodeQueryRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|HddsProtos
operator|.
name|NodeState
name|nodeState
init|=
name|request
operator|.
name|getState
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|HddsProtos
operator|.
name|Node
argument_list|>
name|datanodes
init|=
name|impl
operator|.
name|queryNode
argument_list|(
name|nodeState
argument_list|,
name|request
operator|.
name|getScope
argument_list|()
argument_list|,
name|request
operator|.
name|getPoolName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|NodeQueryResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllDatanodes
argument_list|(
name|datanodes
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|notifyObjectStageChange ( ObjectStageChangeRequestProto request)
specifier|public
name|ObjectStageChangeResponseProto
name|notifyObjectStageChange
parameter_list|(
name|ObjectStageChangeRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|notifyObjectStageChange
argument_list|(
name|request
operator|.
name|getType
argument_list|()
argument_list|,
name|request
operator|.
name|getId
argument_list|()
argument_list|,
name|request
operator|.
name|getOp
argument_list|()
argument_list|,
name|request
operator|.
name|getStage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ObjectStageChangeResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|listPipelines ( ListPipelineRequestProto request)
specifier|public
name|ListPipelineResponseProto
name|listPipelines
parameter_list|(
name|ListPipelineRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|ListPipelineResponseProto
operator|.
name|Builder
name|builder
init|=
name|ListPipelineResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelines
init|=
name|impl
operator|.
name|listPipelines
argument_list|()
decl_stmt|;
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelines
control|)
block|{
name|HddsProtos
operator|.
name|Pipeline
name|protobufMessage
init|=
name|pipeline
operator|.
name|getProtobufMessage
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addPipelines
argument_list|(
name|protobufMessage
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
DECL|method|activatePipeline ( ActivatePipelineRequestProto request)
specifier|public
name|ActivatePipelineResponseProto
name|activatePipeline
parameter_list|(
name|ActivatePipelineRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|activatePipeline
argument_list|(
name|request
operator|.
name|getPipelineID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ActivatePipelineResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|deactivatePipeline ( DeactivatePipelineRequestProto request)
specifier|public
name|DeactivatePipelineResponseProto
name|deactivatePipeline
parameter_list|(
name|DeactivatePipelineRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|deactivatePipeline
argument_list|(
name|request
operator|.
name|getPipelineID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|DeactivatePipelineResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|closePipeline ( RpcController controller, ClosePipelineRequestProto request)
specifier|public
name|ClosePipelineResponseProto
name|closePipeline
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ClosePipelineRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|closePipeline
argument_list|(
name|request
operator|.
name|getPipelineID
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ClosePipelineResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getScmInfo ( HddsProtos.GetScmInfoRequestProto req)
specifier|public
name|HddsProtos
operator|.
name|GetScmInfoResponseProto
name|getScmInfo
parameter_list|(
name|HddsProtos
operator|.
name|GetScmInfoRequestProto
name|req
parameter_list|)
throws|throws
name|IOException
block|{
name|ScmInfo
name|scmInfo
init|=
name|impl
operator|.
name|getScmInfo
argument_list|()
decl_stmt|;
return|return
name|HddsProtos
operator|.
name|GetScmInfoResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setClusterId
argument_list|(
name|scmInfo
operator|.
name|getClusterId
argument_list|()
argument_list|)
operator|.
name|setScmId
argument_list|(
name|scmInfo
operator|.
name|getScmId
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|inSafeMode ( InSafeModeRequestProto request)
specifier|public
name|InSafeModeResponseProto
name|inSafeMode
parameter_list|(
name|InSafeModeRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|InSafeModeResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setInSafeMode
argument_list|(
name|impl
operator|.
name|inSafeMode
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|forceExitSafeMode ( ForceExitSafeModeRequestProto request)
specifier|public
name|ForceExitSafeModeResponseProto
name|forceExitSafeMode
parameter_list|(
name|ForceExitSafeModeRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ForceExitSafeModeResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setExitedSafeMode
argument_list|(
name|impl
operator|.
name|forceExitSafeMode
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|startReplicationManager ( StartReplicationManagerRequestProto request)
specifier|public
name|StartReplicationManagerResponseProto
name|startReplicationManager
parameter_list|(
name|StartReplicationManagerRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|startReplicationManager
argument_list|()
expr_stmt|;
return|return
name|StartReplicationManagerResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|stopReplicationManager ( StopReplicationManagerRequestProto request)
specifier|public
name|StopReplicationManagerResponseProto
name|stopReplicationManager
parameter_list|(
name|StopReplicationManagerRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|impl
operator|.
name|stopReplicationManager
argument_list|()
expr_stmt|;
return|return
name|StopReplicationManagerResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getReplicationManagerStatus ( ReplicationManagerStatusRequestProto request)
specifier|public
name|ReplicationManagerStatusResponseProto
name|getReplicationManagerStatus
parameter_list|(
name|ReplicationManagerStatusRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ReplicationManagerStatusResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setIsRunning
argument_list|(
name|impl
operator|.
name|getReplicationManagerStatus
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

