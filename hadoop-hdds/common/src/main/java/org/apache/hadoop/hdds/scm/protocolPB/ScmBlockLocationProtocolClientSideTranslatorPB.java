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
name|client
operator|.
name|BlockID
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
name|AllocatedBlock
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
name|ScmBlockLocationProtocol
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
name|ScmBlockLocationProtocolProtos
operator|.
name|AllocateScmBlockRequestProto
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
name|ScmBlockLocationProtocolProtos
operator|.
name|AllocateScmBlockResponseProto
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
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmKeyBlocksRequestProto
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
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmKeyBlocksResponseProto
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
name|ScmBlockLocationProtocolProtos
operator|.
name|KeyBlocks
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|common
operator|.
name|BlockGroup
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
name|common
operator|.
name|DeleteBlockGroupResult
import|;
end_import

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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * This class is the client-side translator to translate the requests made on  * the {@link ScmBlockLocationProtocol} interface to the RPC server  * implementing {@link ScmBlockLocationProtocolPB}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ScmBlockLocationProtocolClientSideTranslatorPB
specifier|public
specifier|final
class|class
name|ScmBlockLocationProtocolClientSideTranslatorPB
implements|implements
name|ScmBlockLocationProtocol
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
name|ScmBlockLocationProtocolPB
name|rpcProxy
decl_stmt|;
comment|/**    * Creates a new StorageContainerLocationProtocolClientSideTranslatorPB.    *    * @param rpcProxy {@link StorageContainerLocationProtocolPB} RPC proxy    */
DECL|method|ScmBlockLocationProtocolClientSideTranslatorPB ( ScmBlockLocationProtocolPB rpcProxy)
specifier|public
name|ScmBlockLocationProtocolClientSideTranslatorPB
parameter_list|(
name|ScmBlockLocationProtocolPB
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
comment|/**    * Asks SCM where a block should be allocated. SCM responds with the    * set of datanodes that should be used creating this block.    * @param size - size of the block.    * @return allocated block accessing info (key, pipeline).    * @throws IOException    */
annotation|@
name|Override
DECL|method|allocateBlock (long size, HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, String owner)
specifier|public
name|AllocatedBlock
name|allocateBlock
parameter_list|(
name|long
name|size
parameter_list|,
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
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|size
operator|>
literal|0
argument_list|,
literal|"block size must be greater than 0"
argument_list|)
expr_stmt|;
name|AllocateScmBlockRequestProto
name|request
init|=
name|AllocateScmBlockRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setSize
argument_list|(
name|size
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setFactor
argument_list|(
name|factor
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
specifier|final
name|AllocateScmBlockResponseProto
name|response
decl_stmt|;
try|try
block|{
name|response
operator|=
name|rpcProxy
operator|.
name|allocateScmBlock
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|request
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|response
operator|.
name|getErrorCode
argument_list|()
operator|!=
name|AllocateScmBlockResponseProto
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
literal|"Allocate block failed."
argument_list|)
throw|;
block|}
name|AllocatedBlock
operator|.
name|Builder
name|builder
init|=
operator|new
name|AllocatedBlock
operator|.
name|Builder
argument_list|()
operator|.
name|setBlockID
argument_list|(
name|BlockID
operator|.
name|getFromProtobuf
argument_list|(
name|response
operator|.
name|getBlockID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|Pipeline
operator|.
name|getFromProtobuf
argument_list|(
name|response
operator|.
name|getPipeline
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setShouldCreateContainer
argument_list|(
name|response
operator|.
name|getCreateContainer
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
comment|/**    * Delete the set of keys specified.    *    * @param keyBlocksInfoList batch of block keys to delete.    * @return list of block deletion results.    * @throws IOException if there is any failure.    *    */
annotation|@
name|Override
DECL|method|deleteKeyBlocks ( List<BlockGroup> keyBlocksInfoList)
specifier|public
name|List
argument_list|<
name|DeleteBlockGroupResult
argument_list|>
name|deleteKeyBlocks
parameter_list|(
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|keyBlocksInfoList
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|KeyBlocks
argument_list|>
name|keyBlocksProto
init|=
name|keyBlocksInfoList
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|BlockGroup
operator|::
name|getProto
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
name|DeleteScmKeyBlocksRequestProto
name|request
init|=
name|DeleteScmKeyBlocksRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllKeyBlocks
argument_list|(
name|keyBlocksProto
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|DeleteScmKeyBlocksResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|deleteScmKeyBlocks
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|request
argument_list|)
expr_stmt|;
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
name|List
argument_list|<
name|DeleteBlockGroupResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|resp
operator|.
name|getResultsCount
argument_list|()
argument_list|)
decl_stmt|;
name|results
operator|.
name|addAll
argument_list|(
name|resp
operator|.
name|getResultsList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|result
lambda|->
operator|new
name|DeleteBlockGroupResult
argument_list|(
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|,
name|DeleteBlockGroupResult
operator|.
name|convertBlockResultProto
argument_list|(
name|result
operator|.
name|getBlockResultsList
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|results
return|;
block|}
comment|/**    * Gets the cluster Id and Scm Id from SCM.    * @return ScmInfo    * @throws IOException    */
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
name|getDefaultInstance
argument_list|()
decl_stmt|;
name|HddsProtos
operator|.
name|GetScmInfoRespsonseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|rpcProxy
operator|.
name|getScmInfo
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|request
argument_list|)
expr_stmt|;
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

