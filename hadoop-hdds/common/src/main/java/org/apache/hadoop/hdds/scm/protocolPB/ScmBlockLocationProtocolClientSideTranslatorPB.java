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
name|stream
operator|.
name|Collectors
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
name|ContainerBlockID
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
name|ScmBlockLocationProtocolProtos
operator|.
name|SCMBlockLocationRequest
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
name|SCMBlockLocationResponse
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
name|protocol
operator|.
name|proto
operator|.
name|ScmBlockLocationProtocolProtos
operator|.
name|AllocateBlockResponse
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ScmBlockLocationProtocolProtos
operator|.
name|SortDatanodesRequestProto
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
name|SortDatanodesResponseProto
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ExcludeList
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
name|exceptions
operator|.
name|SCMException
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
name|ScmBlockLocationProtocolProtos
operator|.
name|Status
operator|.
name|OK
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
comment|/**    * Returns a SCMBlockLocationRequest builder with specified type.    * @param cmdType type of the request    */
DECL|method|createSCMBlockRequest (Type cmdType)
specifier|private
name|SCMBlockLocationRequest
operator|.
name|Builder
name|createSCMBlockRequest
parameter_list|(
name|Type
name|cmdType
parameter_list|)
block|{
return|return
name|SCMBlockLocationRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCmdType
argument_list|(
name|cmdType
argument_list|)
operator|.
name|setTraceID
argument_list|(
name|TracingUtil
operator|.
name|exportCurrentSpan
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Submits client request to SCM server.    * @param req client request    * @return response from SCM    * @throws IOException thrown if any Protobuf service exception occurs    */
DECL|method|submitRequest ( SCMBlockLocationRequest req)
specifier|private
name|SCMBlockLocationResponse
name|submitRequest
parameter_list|(
name|SCMBlockLocationRequest
name|req
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|SCMBlockLocationResponse
name|response
init|=
name|rpcProxy
operator|.
name|send
argument_list|(
name|NULL_RPC_CONTROLLER
argument_list|,
name|req
argument_list|)
decl_stmt|;
return|return
name|response
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
DECL|method|handleError (SCMBlockLocationResponse resp)
specifier|private
name|SCMBlockLocationResponse
name|handleError
parameter_list|(
name|SCMBlockLocationResponse
name|resp
parameter_list|)
throws|throws
name|SCMException
block|{
if|if
condition|(
name|resp
operator|.
name|getStatus
argument_list|()
operator|!=
name|OK
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
name|resp
operator|.
name|getMessage
argument_list|()
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|values
argument_list|()
index|[
name|resp
operator|.
name|getStatus
argument_list|()
operator|.
name|ordinal
argument_list|()
index|]
argument_list|)
throw|;
block|}
return|return
name|resp
return|;
block|}
comment|/**    * Asks SCM where a block should be allocated. SCM responds with the    * set of datanodes that should be used creating this block.    * @param size - size of the block.    * @param num - number of blocks.    * @param type - replication type of the blocks.    * @param factor - replication factor of the blocks.    * @param excludeList - exclude list while allocating blocks.    * @return allocated block accessing info (key, pipeline).    * @throws IOException    */
annotation|@
name|Override
DECL|method|allocateBlock (long size, int num, HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, String owner, ExcludeList excludeList)
specifier|public
name|List
argument_list|<
name|AllocatedBlock
argument_list|>
name|allocateBlock
parameter_list|(
name|long
name|size
parameter_list|,
name|int
name|num
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
parameter_list|,
name|ExcludeList
name|excludeList
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
name|setNumBlocks
argument_list|(
name|num
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
name|setExcludeList
argument_list|(
name|excludeList
operator|.
name|getProtoBuf
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SCMBlockLocationRequest
name|wrapper
init|=
name|createSCMBlockRequest
argument_list|(
name|Type
operator|.
name|AllocateScmBlock
argument_list|)
operator|.
name|setAllocateScmBlockRequest
argument_list|(
name|request
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SCMBlockLocationResponse
name|wrappedResponse
init|=
name|handleError
argument_list|(
name|submitRequest
argument_list|(
name|wrapper
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AllocateScmBlockResponseProto
name|response
init|=
name|wrappedResponse
operator|.
name|getAllocateScmBlockResponse
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AllocatedBlock
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|response
operator|.
name|getBlocksCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AllocateBlockResponse
name|resp
range|:
name|response
operator|.
name|getBlocksList
argument_list|()
control|)
block|{
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
name|setContainerBlockID
argument_list|(
name|ContainerBlockID
operator|.
name|getFromProtobuf
argument_list|(
name|resp
operator|.
name|getContainerBlockID
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
name|resp
operator|.
name|getPipeline
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|blocks
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
name|SCMBlockLocationRequest
name|wrapper
init|=
name|createSCMBlockRequest
argument_list|(
name|Type
operator|.
name|DeleteScmKeyBlocks
argument_list|)
operator|.
name|setDeleteScmKeyBlocksRequest
argument_list|(
name|request
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SCMBlockLocationResponse
name|wrappedResponse
init|=
name|handleError
argument_list|(
name|submitRequest
argument_list|(
name|wrapper
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DeleteScmKeyBlocksResponseProto
name|resp
init|=
name|wrappedResponse
operator|.
name|getDeleteScmKeyBlocksResponse
argument_list|()
decl_stmt|;
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
name|GetScmInfoResponseProto
name|resp
decl_stmt|;
name|SCMBlockLocationRequest
name|wrapper
init|=
name|createSCMBlockRequest
argument_list|(
name|Type
operator|.
name|GetScmInfo
argument_list|)
operator|.
name|setGetScmInfoRequest
argument_list|(
name|request
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SCMBlockLocationResponse
name|wrappedResponse
init|=
name|handleError
argument_list|(
name|submitRequest
argument_list|(
name|wrapper
argument_list|)
argument_list|)
decl_stmt|;
name|resp
operator|=
name|wrappedResponse
operator|.
name|getGetScmInfoResponse
argument_list|()
expr_stmt|;
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
comment|/**    * Sort the datanodes based on distance from client.    * @return List<DatanodeDetails></>    * @throws IOException    */
annotation|@
name|Override
DECL|method|sortDatanodes (List<String> nodes, String clientMachine)
specifier|public
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|sortDatanodes
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|nodes
parameter_list|,
name|String
name|clientMachine
parameter_list|)
throws|throws
name|IOException
block|{
name|SortDatanodesRequestProto
name|request
init|=
name|SortDatanodesRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|addAllNodeNetworkName
argument_list|(
name|nodes
argument_list|)
operator|.
name|setClient
argument_list|(
name|clientMachine
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SCMBlockLocationRequest
name|wrapper
init|=
name|createSCMBlockRequest
argument_list|(
name|Type
operator|.
name|SortDatanodes
argument_list|)
operator|.
name|setSortDatanodesRequest
argument_list|(
name|request
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SCMBlockLocationResponse
name|wrappedResponse
init|=
name|handleError
argument_list|(
name|submitRequest
argument_list|(
name|wrapper
argument_list|)
argument_list|)
decl_stmt|;
name|SortDatanodesResponseProto
name|resp
init|=
name|wrappedResponse
operator|.
name|getSortDatanodesResponse
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|resp
operator|.
name|getNodeCount
argument_list|()
argument_list|)
decl_stmt|;
name|results
operator|.
name|addAll
argument_list|(
name|resp
operator|.
name|getNodeList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|node
lambda|->
name|DatanodeDetails
operator|.
name|getFromProtoBuf
argument_list|(
name|node
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

