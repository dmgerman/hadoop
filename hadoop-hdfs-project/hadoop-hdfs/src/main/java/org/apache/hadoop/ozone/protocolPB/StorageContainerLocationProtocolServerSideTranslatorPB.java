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
name|EnumSet
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|ozone
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
name|ozone
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
name|ozone
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
name|ozone
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|DeleteContainerRequestProto
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|DeleteContainerResponseProto
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|ListContainerResponseProto
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|ListContainerRequestProto
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|NotifyObjectCreationStageRequestProto
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
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
operator|.
name|NotifyObjectCreationStageResponseProto
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
name|ozone
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
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
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolPB
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
block|{
name|Pipeline
name|pipeline
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
name|getContainerName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ContainerResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPipeline
argument_list|(
name|pipeline
operator|.
name|getProtobufMessage
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
block|{
name|Pipeline
name|pipeline
init|=
name|impl
operator|.
name|getContainer
argument_list|(
name|request
operator|.
name|getContainerName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|GetContainerResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPipeline
argument_list|(
name|pipeline
operator|.
name|getProtobufMessage
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
DECL|method|listContainer (RpcController controller, ListContainerRequestProto request)
specifier|public
name|ListContainerResponseProto
name|listContainer
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|ListContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|String
name|startName
init|=
literal|null
decl_stmt|;
name|String
name|prefixName
init|=
literal|null
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
name|hasPrefixName
argument_list|()
condition|)
block|{
comment|// End container name is given.
name|prefixName
operator|=
name|request
operator|.
name|getPrefixName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|hasStartName
argument_list|()
condition|)
block|{
comment|// End container name is given.
name|startName
operator|=
name|request
operator|.
name|getStartName
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
name|Pipeline
argument_list|>
name|pipelineList
init|=
name|impl
operator|.
name|listContainer
argument_list|(
name|startName
argument_list|,
name|prefixName
argument_list|,
name|count
argument_list|)
decl_stmt|;
name|ListContainerResponseProto
operator|.
name|Builder
name|builder
init|=
name|ListContainerResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Pipeline
name|pipeline
range|:
name|pipelineList
control|)
block|{
name|builder
operator|.
name|addPipeline
argument_list|(
name|pipeline
operator|.
name|getProtobufMessage
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
DECL|method|deleteContainer ( RpcController controller, DeleteContainerRequestProto request)
specifier|public
name|DeleteContainerResponseProto
name|deleteContainer
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|DeleteContainerRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|deleteContainer
argument_list|(
name|request
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|DeleteContainerResponseProto
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
block|{
name|EnumSet
argument_list|<
name|OzoneProtos
operator|.
name|NodeState
argument_list|>
name|nodeStateEnumSet
init|=
name|EnumSet
operator|.
name|copyOf
argument_list|(
name|request
operator|.
name|getQueryList
argument_list|()
argument_list|)
decl_stmt|;
name|OzoneProtos
operator|.
name|NodePool
name|datanodes
init|=
name|impl
operator|.
name|queryNode
argument_list|(
name|nodeStateEnumSet
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
name|setDatanodes
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
DECL|method|notifyObjectCreationStage ( RpcController controller, NotifyObjectCreationStageRequestProto request)
specifier|public
name|NotifyObjectCreationStageResponseProto
name|notifyObjectCreationStage
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|NotifyObjectCreationStageRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|notifyObjectCreationStage
argument_list|(
name|request
operator|.
name|getType
argument_list|()
argument_list|,
name|request
operator|.
name|getName
argument_list|()
argument_list|,
name|request
operator|.
name|getStage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|NotifyObjectCreationStageResponseProto
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
block|}
end_class

end_unit

