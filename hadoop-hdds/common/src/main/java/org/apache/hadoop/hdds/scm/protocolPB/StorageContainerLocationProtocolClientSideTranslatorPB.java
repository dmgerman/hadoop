begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.protocolPB
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
name|protocolPB
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
name|util
operator|.
name|ArrayList
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
name|function
operator|.
name|Consumer
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
name|HddsProtos
operator|.
name|GetScmInfoResponseProto
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
name|NodeQueryRequestProto
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
name|PipelineRequestProto
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
name|PipelineResponseProto
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
name|ScmContainerLocationRequest
operator|.
name|Builder
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
name|Type
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
name|protocol
operator|.
name|StorageContainerLocationProtocol
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
name|tracing
operator|.
name|TracingUtil
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
name|ProtocolTranslator
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

begin_comment
comment|/**  * This class is the client-side translator to translate the requests made on  * the {@link StorageContainerLocationProtocol} interface to the RPC server  * implementing {@link StorageContainerLocationProtocolPB}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageContainerLocationProtocolClientSideTranslatorPB
specifier|public
specifier|final
class|class
name|StorageContainerLocationProtocolClientSideTranslatorPB
implements|implements
name|StorageContainerLocationProtocol
implements|,
name|ProtocolTranslator
implements|,
name|Closeable
block|{
comment|/**    * RpcController is not used and hence is set to null.    */
DECL|field|NULL_RPC_CONTROLLER
specifier|private
specifier|static
specifier|final
name|RpcController
name|NULL_RPC_CONTROLLER
init|=
literal|null
decl_stmt|;
DECL|field|rpcProxy
specifier|private
specifier|final
name|StorageContainerLocationProtocolPB
name|rpcProxy
decl_stmt|;
comment|/**    * Creates a new StorageContainerLocationProtocolClientSideTranslatorPB.    *    * @param rpcProxy {@link StorageContainerLocationProtocolPB} RPC proxy    */
DECL|method|StorageContainerLocationProtocolClientSideTranslatorPB ( StorageContainerLocationProtocolPB rpcProxy)
specifier|public
name|StorageContainerLocationProtocolClientSideTranslatorPB
parameter_list|(
name|StorageContainerLocationProtocolPB
name|rpcProxy
parameter_list|)
block|{
name|this
operator|.
name|rpcProxy
operator|=
name|rpcProxy
expr_stmt|;
block|}
comment|/**    * Helper method to wrap the request and send the message.    */
DECL|method|submitRequest ( StorageContainerLocationProtocolProtos.Type type, Consumer<Builder> builderConsumer)
specifier|private
name|ScmContainerLocationResponse
name|submitRequest
parameter_list|(
name|StorageContainerLocationProtocolProtos
operator|.
name|Type
name|type
parameter_list|,
name|Consumer
argument_list|<
name|Builder
argument_list|>
name|builderConsumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ScmContainerLocationResponse
name|response
decl_stmt|;
try|try
block|{
name|Builder
name|builder
init|=
name|ScmContainerLocationRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|type
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
decl_stmt|;
name|builderConsumer
operator|.
name|accept
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|ScmContainerLocationRequest
name|wrapper
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|response
operator|=
name|rpcProxy
operator|.
name|submitRequest
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|wrapper
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|ex
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
return|return
name|response
return|;
block|}
comment|/**    * Asks SCM where a container should be allocated. SCM responds with the set    * of datanodes that should be used creating this container. Ozone/SCM only    * supports replication factor of either 1 or 3.    *    * @param type   - Replication Type    * @param factor - Replication Count    */
annotation|@
name|Override
DECL|method|allocateContainer ( HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, String owner)
specifier|public
name|ContainerWithPipeline
name|allocateContainer
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerRequestProto
name|request
init|=
name|ContainerRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|factor
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|type
argument_list|)
operator|.
name|setOwner
argument_list|(
name|owner
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ContainerResponseProto
name|response
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|AllocateContainer
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setContainerRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getContainerResponse
argument_list|()
decl_stmt|;
comment|//TODO should be migrated to use the top level status structure.
if|if
condition|(
name|response
operator|.
name|getErrorCode
argument_list|()
operator|!=
name|ContainerResponseProto
operator|.
name|Error
operator|.
name|success
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|response
operator|.
name|hasErrorMessage
argument_list|()
condition|?
name|response
operator|.
name|getErrorMessage
argument_list|()
else|:
literal|"Allocate container failed."
argument_list|)
throw|;
block|}
return|return
name|ContainerWithPipeline
operator|.
name|fromProtobuf
argument_list|(
name|response
operator|.
name|getContainerWithPipeline
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getContainer (long containerID)
specifier|public
name|ContainerInfo
name|getContainer
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerID
operator|>=
literal|0
argument_list|,
literal|"Container ID cannot be negative"
argument_list|)
expr_stmt|;
name|GetContainerRequestProto
name|request
init|=
name|GetContainerRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ScmContainerLocationResponse
name|response
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|GetContainer
argument_list|,
parameter_list|(
name|builder
parameter_list|)
lambda|->
name|builder
operator|.
name|setGetContainerRequest
argument_list|(
name|request
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ContainerInfo
operator|.
name|fromProtobuf
argument_list|(
name|response
operator|.
name|getGetContainerResponse
argument_list|()
operator|.
name|getContainerInfo
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|getContainerWithPipeline (long containerID)
specifier|public
name|ContainerWithPipeline
name|getContainerWithPipeline
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerID
operator|>=
literal|0
argument_list|,
literal|"Container ID cannot be negative"
argument_list|)
expr_stmt|;
name|GetContainerWithPipelineRequestProto
name|request
init|=
name|GetContainerWithPipelineRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ScmContainerLocationResponse
name|response
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|GetContainerWithPipeline
argument_list|,
parameter_list|(
name|builder
parameter_list|)
lambda|->
name|builder
operator|.
name|setGetContainerWithPipelineRequest
argument_list|(
name|request
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ContainerWithPipeline
operator|.
name|fromProtobuf
argument_list|(
name|response
operator|.
name|getGetContainerWithPipelineResponse
argument_list|()
operator|.
name|getContainerWithPipeline
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|listContainer (long startContainerID, int count)
specifier|public
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|listContainer
parameter_list|(
name|long
name|startContainerID
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|startContainerID
operator|>=
literal|0
argument_list|,
literal|"Container ID cannot be negative."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|count
operator|>
literal|0
argument_list|,
literal|"Container count must be greater than 0."
argument_list|)
expr_stmt|;
name|SCMListContainerRequestProto
operator|.
name|Builder
name|builder
init|=
name|SCMListContainerRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setStartContainerID
argument_list|(
name|startContainerID
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
expr_stmt|;
name|SCMListContainerRequestProto
name|request
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|SCMListContainerResponseProto
name|response
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|ListContainer
argument_list|,
name|builder1
lambda|->
name|builder1
operator|.
name|setScmListContainerRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getScmListContainerResponse
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containerList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|HddsProtos
operator|.
name|ContainerInfoProto
name|containerInfoProto
range|:
name|response
operator|.
name|getContainersList
argument_list|()
control|)
block|{
name|containerList
operator|.
name|add
argument_list|(
name|ContainerInfo
operator|.
name|fromProtobuf
argument_list|(
name|containerInfoProto
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|containerList
return|;
block|}
comment|/**    * Ask SCM to delete a container by name. SCM will remove    * the container mapping in its database.    */
annotation|@
name|Override
DECL|method|deleteContainer (long containerID)
specifier|public
name|void
name|deleteContainer
parameter_list|(
name|long
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|containerID
operator|>=
literal|0
argument_list|,
literal|"Container ID cannot be negative"
argument_list|)
expr_stmt|;
name|SCMDeleteContainerRequestProto
name|request
init|=
name|SCMDeleteContainerRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|submitRequest
argument_list|(
name|Type
operator|.
name|DeleteContainer
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setScmDeleteContainerRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Queries a list of Node Statuses.    */
annotation|@
name|Override
DECL|method|queryNode (HddsProtos.NodeState nodeStatuses, HddsProtos.QueryScope queryScope, String poolName)
specifier|public
name|List
argument_list|<
name|HddsProtos
operator|.
name|Node
argument_list|>
name|queryNode
parameter_list|(
name|HddsProtos
operator|.
name|NodeState
name|nodeStatuses
parameter_list|,
name|HddsProtos
operator|.
name|QueryScope
name|queryScope
parameter_list|,
name|String
name|poolName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO : We support only cluster wide query right now. So ignoring checking
comment|// queryScope and poolName
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodeStatuses
argument_list|)
expr_stmt|;
name|NodeQueryRequestProto
name|request
init|=
name|NodeQueryRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setState
argument_list|(
name|nodeStatuses
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setScope
argument_list|(
name|queryScope
argument_list|)
operator|.
name|setPoolName
argument_list|(
name|poolName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeQueryResponseProto
name|response
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|QueryNode
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setNodeQueryRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getNodeQueryResponse
argument_list|()
decl_stmt|;
return|return
name|response
operator|.
name|getDatanodesList
argument_list|()
return|;
block|}
comment|/**    * Notify from client that creates object on datanodes.    *    * @param type  object type    * @param id    object id    * @param op    operation type (e.g., create, close, delete)    * @param stage object creation stage : begin/complete    */
annotation|@
name|Override
DECL|method|notifyObjectStageChange ( ObjectStageChangeRequestProto.Type type, long id, ObjectStageChangeRequestProto.Op op, ObjectStageChangeRequestProto.Stage stage)
specifier|public
name|void
name|notifyObjectStageChange
parameter_list|(
name|ObjectStageChangeRequestProto
operator|.
name|Type
name|type
parameter_list|,
name|long
name|id
parameter_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Op
name|op
parameter_list|,
name|ObjectStageChangeRequestProto
operator|.
name|Stage
name|stage
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|id
operator|>=
literal|0
argument_list|,
literal|"Object id cannot be negative."
argument_list|)
expr_stmt|;
name|ObjectStageChangeRequestProto
name|request
init|=
name|ObjectStageChangeRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setId
argument_list|(
name|id
argument_list|)
operator|.
name|setOp
argument_list|(
name|op
argument_list|)
operator|.
name|setStage
argument_list|(
name|stage
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|submitRequest
argument_list|(
name|Type
operator|.
name|NotifyObjectStageChange
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setObjectStageChangeRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a replication pipeline of a specified type.    *    * @param replicationType - replication type    * @param factor          - factor 1 or 3    * @param nodePool        - optional machine list to build a pipeline.    */
annotation|@
name|Override
DECL|method|createReplicationPipeline (HddsProtos.ReplicationType replicationType, HddsProtos.ReplicationFactor factor, HddsProtos .NodePool nodePool)
specifier|public
name|Pipeline
name|createReplicationPipeline
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|HddsProtos
operator|.
name|NodePool
name|nodePool
parameter_list|)
throws|throws
name|IOException
block|{
name|PipelineRequestProto
name|request
init|=
name|PipelineRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setNodePool
argument_list|(
name|nodePool
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
name|factor
argument_list|)
operator|.
name|setReplicationType
argument_list|(
name|replicationType
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|PipelineResponseProto
name|response
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|AllocatePipeline
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setPipelineRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getPipelineResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getErrorCode
argument_list|()
operator|==
name|PipelineResponseProto
operator|.
name|Error
operator|.
name|success
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|response
operator|.
name|hasPipeline
argument_list|()
argument_list|,
literal|"With success, "
operator|+
literal|"must come a pipeline"
argument_list|)
expr_stmt|;
return|return
name|Pipeline
operator|.
name|getFromProtobuf
argument_list|(
name|response
operator|.
name|getPipeline
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|errorMessage
init|=
name|String
operator|.
name|format
argument_list|(
literal|"create replication pipeline "
operator|+
literal|"failed. code : %s Message: %s"
argument_list|,
name|response
operator|.
name|getErrorCode
argument_list|()
argument_list|,
name|response
operator|.
name|hasErrorMessage
argument_list|()
condition|?
name|response
operator|.
name|getErrorMessage
argument_list|()
else|:
literal|""
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|errorMessage
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listPipelines ()
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|listPipelines
parameter_list|()
throws|throws
name|IOException
block|{
name|ListPipelineRequestProto
name|request
init|=
name|ListPipelineRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ListPipelineResponseProto
name|response
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|ListPipelines
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setListPipelineRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getListPipelineResponse
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Pipeline
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|HddsProtos
operator|.
name|Pipeline
name|pipeline
range|:
name|response
operator|.
name|getPipelinesList
argument_list|()
control|)
block|{
name|Pipeline
name|fromProtobuf
init|=
name|Pipeline
operator|.
name|getFromProtobuf
argument_list|(
name|pipeline
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|fromProtobuf
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
annotation|@
name|Override
DECL|method|activatePipeline (HddsProtos.PipelineID pipelineID)
specifier|public
name|void
name|activatePipeline
parameter_list|(
name|HddsProtos
operator|.
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|ActivatePipelineRequestProto
name|request
init|=
name|ActivatePipelineRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|pipelineID
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|submitRequest
argument_list|(
name|Type
operator|.
name|ActivatePipeline
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setActivatePipelineRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deactivatePipeline (HddsProtos.PipelineID pipelineID)
specifier|public
name|void
name|deactivatePipeline
parameter_list|(
name|HddsProtos
operator|.
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|DeactivatePipelineRequestProto
name|request
init|=
name|DeactivatePipelineRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|pipelineID
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|submitRequest
argument_list|(
name|Type
operator|.
name|DeactivatePipeline
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setDeactivatePipelineRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|closePipeline (HddsProtos.PipelineID pipelineID)
specifier|public
name|void
name|closePipeline
parameter_list|(
name|HddsProtos
operator|.
name|PipelineID
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|ClosePipelineRequestProto
name|request
init|=
name|ClosePipelineRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|setPipelineID
argument_list|(
name|pipelineID
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|submitRequest
argument_list|(
name|Type
operator|.
name|ClosePipeline
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setClosePipelineRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScmInfo ()
specifier|public
name|ScmInfo
name|getScmInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|HddsProtos
operator|.
name|GetScmInfoRequestProto
name|request
init|=
name|HddsProtos
operator|.
name|GetScmInfoRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|GetScmInfoResponseProto
name|resp
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|GetScmInfo
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setGetScmInfoRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getGetScmInfoResponse
argument_list|()
decl_stmt|;
name|ScmInfo
operator|.
name|Builder
name|builder
init|=
operator|new
name|ScmInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setClusterId
argument_list|(
name|resp
operator|.
name|getClusterId
argument_list|()
argument_list|)
operator|.
name|setScmId
argument_list|(
name|resp
operator|.
name|getScmId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Check if SCM is in safe mode.    *    * @return Returns true if SCM is in safe mode else returns false.    */
annotation|@
name|Override
DECL|method|inSafeMode ()
specifier|public
name|boolean
name|inSafeMode
parameter_list|()
throws|throws
name|IOException
block|{
name|InSafeModeRequestProto
name|request
init|=
name|InSafeModeRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
return|return
name|submitRequest
argument_list|(
name|Type
operator|.
name|InSafeMode
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setInSafeModeRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getInSafeModeResponse
argument_list|()
operator|.
name|getInSafeMode
argument_list|()
return|;
block|}
comment|/**    * Force SCM out of Safe mode.    *    * @return returns true if operation is successful.    */
annotation|@
name|Override
DECL|method|forceExitSafeMode ()
specifier|public
name|boolean
name|forceExitSafeMode
parameter_list|()
throws|throws
name|IOException
block|{
name|ForceExitSafeModeRequestProto
name|request
init|=
name|ForceExitSafeModeRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
name|ForceExitSafeModeResponseProto
name|resp
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|ForceExitSafeMode
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setForceExitSafeModeRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getForceExitSafeModeResponse
argument_list|()
decl_stmt|;
return|return
name|resp
operator|.
name|getExitedSafeMode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|startReplicationManager ()
specifier|public
name|void
name|startReplicationManager
parameter_list|()
throws|throws
name|IOException
block|{
name|StartReplicationManagerRequestProto
name|request
init|=
name|StartReplicationManagerRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
name|submitRequest
argument_list|(
name|Type
operator|.
name|StartReplicationManager
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setStartReplicationManagerRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stopReplicationManager ()
specifier|public
name|void
name|stopReplicationManager
parameter_list|()
throws|throws
name|IOException
block|{
name|StopReplicationManagerRequestProto
name|request
init|=
name|StopReplicationManagerRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
name|submitRequest
argument_list|(
name|Type
operator|.
name|StopReplicationManager
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setStopReplicationManagerRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReplicationManagerStatus ()
specifier|public
name|boolean
name|getReplicationManagerStatus
parameter_list|()
throws|throws
name|IOException
block|{
name|ReplicationManagerStatusRequestProto
name|request
init|=
name|ReplicationManagerStatusRequestProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
name|ReplicationManagerStatusResponseProto
name|response
init|=
name|submitRequest
argument_list|(
name|Type
operator|.
name|GetReplicationManagerStatus
argument_list|,
name|builder
lambda|->
name|builder
operator|.
name|setSeplicationManagerStatusRequest
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|getReplicationManagerStatusResponse
argument_list|()
decl_stmt|;
return|return
name|response
operator|.
name|getIsRunning
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUnderlyingProxyObject ()
specifier|public
name|Object
name|getUnderlyingProxyObject
parameter_list|()
block|{
return|return
name|rpcProxy
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|rpcProxy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

