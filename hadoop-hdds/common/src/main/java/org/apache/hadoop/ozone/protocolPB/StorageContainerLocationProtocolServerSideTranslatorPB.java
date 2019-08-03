begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocolPB
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
name|io
operator|.
name|opentracing
operator|.
name|Scope
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
name|tracing
operator|.
name|TracingUtil
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
DECL|field|impl
specifier|private
specifier|final
name|StorageContainerLocationProtocol
name|impl
decl_stmt|;
comment|/**    * Creates a new StorageContainerLocationProtocolServerSideTranslatorPB.    *    * @param impl {@link StorageContainerLocationProtocol} server implementation    */
DECL|method|StorageContainerLocationProtocolServerSideTranslatorPB ( StorageContainerLocationProtocol impl)
specifier|public
name|StorageContainerLocationProtocolServerSideTranslatorPB
parameter_list|(
name|StorageContainerLocationProtocol
name|impl
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
block|}
annotation|@
name|Override
DECL|method|allocateContainer (RpcController unused, ContainerRequestProto request)
specifier|public
name|ContainerResponseProto
name|allocateContainer
parameter_list|(
name|RpcController
name|unused
parameter_list|,
name|ContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"allocateContainer"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
DECL|method|getContainer ( RpcController controller, GetContainerRequestProto request)
specifier|public
name|GetContainerResponseProto
name|getContainer
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"getContainer"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
DECL|method|getContainerWithPipeline ( RpcController controller, GetContainerWithPipelineRequestProto request)
specifier|public
name|GetContainerWithPipelineResponseProto
name|getContainerWithPipeline
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetContainerWithPipelineRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"getContainerWithPipeline"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
DECL|method|listContainer (RpcController controller, SCMListContainerRequestProto request)
specifier|public
name|SCMListContainerResponseProto
name|listContainer
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|SCMListContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"listContainer"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
DECL|method|deleteContainer ( RpcController controller, SCMDeleteContainerRequestProto request)
specifier|public
name|SCMDeleteContainerResponseProto
name|deleteContainer
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|SCMDeleteContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"deleteContainer"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
name|StorageContainerLocationProtocolProtos
operator|.
name|NodeQueryResponseProto
DECL|method|queryNode (RpcController controller, StorageContainerLocationProtocolProtos.NodeQueryRequestProto request)
name|queryNode
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|StorageContainerLocationProtocolProtos
operator|.
name|NodeQueryRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"queryNode"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
name|StorageContainerLocationProtocolProtos
operator|.
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
catch|catch
parameter_list|(
name|Exception
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
DECL|method|notifyObjectStageChange ( RpcController controller, ObjectStageChangeRequestProto request)
specifier|public
name|ObjectStageChangeResponseProto
name|notifyObjectStageChange
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ObjectStageChangeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"notifyObjectStageChange"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
DECL|method|allocatePipeline ( RpcController controller, PipelineRequestProto request)
specifier|public
name|PipelineResponseProto
name|allocatePipeline
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|PipelineRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
comment|// TODO : Wiring this up requires one more patch.
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|listPipelines ( RpcController controller, ListPipelineRequestProto request)
specifier|public
name|ListPipelineResponseProto
name|listPipelines
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ListPipelineRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"listPipelines"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"closePipeline"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
DECL|method|getScmInfo ( RpcController controller, HddsProtos.GetScmInfoRequestProto req)
specifier|public
name|HddsProtos
operator|.
name|GetScmInfoResponseProto
name|getScmInfo
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|HddsProtos
operator|.
name|GetScmInfoRequestProto
name|req
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"getScmInfo"
argument_list|,
name|req
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|inSafeMode ( RpcController controller, InSafeModeRequestProto request)
specifier|public
name|InSafeModeResponseProto
name|inSafeMode
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|InSafeModeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"inSafeMode"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|forceExitSafeMode ( RpcController controller, ForceExitSafeModeRequestProto request)
specifier|public
name|ForceExitSafeModeResponseProto
name|forceExitSafeMode
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ForceExitSafeModeRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"forceExitSafeMode"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|startReplicationManager ( RpcController controller, StartReplicationManagerRequestProto request)
specifier|public
name|StartReplicationManagerResponseProto
name|startReplicationManager
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|StartReplicationManagerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|ignored
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"startReplicationManager"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|stopReplicationManager ( RpcController controller, StopReplicationManagerRequestProto request)
specifier|public
name|StopReplicationManagerResponseProto
name|stopReplicationManager
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|StopReplicationManagerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|ignored
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"stopReplicationManager"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getReplicationManagerStatus ( RpcController controller, ReplicationManagerStatusRequestProto request)
specifier|public
name|ReplicationManagerStatusResponseProto
name|getReplicationManagerStatus
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ReplicationManagerStatusRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
init|(
name|Scope
name|ignored
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"getReplicationManagerStatus"
argument_list|,
name|request
operator|.
name|getTraceID
argument_list|()
argument_list|)
init|)
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

