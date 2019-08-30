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
name|DeleteKeyBlocksResultProto
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
name|protocol
operator|.
name|proto
operator|.
name|ScmBlockLocationProtocolProtos
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
name|ScmBlockLocationProtocolPB
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
DECL|class|ScmBlockLocationProtocolServerSideTranslatorPB
specifier|public
specifier|final
class|class
name|ScmBlockLocationProtocolServerSideTranslatorPB
implements|implements
name|ScmBlockLocationProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|ScmBlockLocationProtocol
name|impl
decl_stmt|;
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
name|ScmBlockLocationProtocolServerSideTranslatorPB
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ProtocolMessageMetrics
DECL|field|protocolMessageMetrics
name|protocolMessageMetrics
decl_stmt|;
comment|/**    * Creates a new ScmBlockLocationProtocolServerSideTranslatorPB.    *    * @param impl {@link ScmBlockLocationProtocol} server implementation    */
DECL|method|ScmBlockLocationProtocolServerSideTranslatorPB ( ScmBlockLocationProtocol impl, ProtocolMessageMetrics metrics)
specifier|public
name|ScmBlockLocationProtocolServerSideTranslatorPB
parameter_list|(
name|ScmBlockLocationProtocol
name|impl
parameter_list|,
name|ProtocolMessageMetrics
name|metrics
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
name|protocolMessageMetrics
operator|=
name|metrics
expr_stmt|;
block|}
DECL|method|createSCMBlockResponse ( ScmBlockLocationProtocolProtos.Type cmdType, String traceID)
specifier|private
name|SCMBlockLocationResponse
operator|.
name|Builder
name|createSCMBlockResponse
parameter_list|(
name|ScmBlockLocationProtocolProtos
operator|.
name|Type
name|cmdType
parameter_list|,
name|String
name|traceID
parameter_list|)
block|{
return|return
name|SCMBlockLocationResponse
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
name|traceID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|send (RpcController controller, SCMBlockLocationRequest request)
specifier|public
name|SCMBlockLocationResponse
name|send
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|SCMBlockLocationRequest
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|String
name|traceId
init|=
name|request
operator|.
name|getTraceID
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"BlockLocationProtocol {} request is received:<json>{}</json>"
argument_list|,
name|request
operator|.
name|getCmdType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|request
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|"\\\\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"BlockLocationProtocol {} request is received"
argument_list|,
name|request
operator|.
name|getCmdType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|protocolMessageMetrics
operator|.
name|increment
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|Scope
name|scope
init|=
name|TracingUtil
operator|.
name|importAndCreateScope
argument_list|(
literal|"ScmBlockLocationProtocol."
operator|+
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
init|)
block|{
name|SCMBlockLocationResponse
name|response
init|=
name|processMessage
argument_list|(
name|request
argument_list|,
name|traceId
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"BlockLocationProtocol {} request is processed. Response: "
operator|+
literal|"<json>{}</json>"
argument_list|,
name|request
operator|.
name|getCmdType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|response
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|"\\\\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
block|}
DECL|method|processMessage ( SCMBlockLocationRequest request, String traceId)
specifier|private
name|SCMBlockLocationResponse
name|processMessage
parameter_list|(
name|SCMBlockLocationRequest
name|request
parameter_list|,
name|String
name|traceId
parameter_list|)
throws|throws
name|ServiceException
block|{
name|SCMBlockLocationResponse
operator|.
name|Builder
name|response
init|=
name|createSCMBlockResponse
argument_list|(
name|request
operator|.
name|getCmdType
argument_list|()
argument_list|,
name|traceId
argument_list|)
decl_stmt|;
name|response
operator|.
name|setSuccess
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
expr_stmt|;
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
name|AllocateScmBlock
case|:
name|response
operator|.
name|setAllocateScmBlockResponse
argument_list|(
name|allocateScmBlock
argument_list|(
name|request
operator|.
name|getAllocateScmBlockRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DeleteScmKeyBlocks
case|:
name|response
operator|.
name|setDeleteScmKeyBlocksResponse
argument_list|(
name|deleteScmKeyBlocks
argument_list|(
name|request
operator|.
name|getDeleteScmKeyBlocksRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|GetScmInfo
case|:
name|response
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
expr_stmt|;
break|break;
case|case
name|SortDatanodes
case|:
name|response
operator|.
name|setSortDatanodesResponse
argument_list|(
name|sortDatanodes
argument_list|(
name|request
operator|.
name|getSortDatanodesRequest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// Should never happen
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown Operation "
operator|+
name|request
operator|.
name|getCmdType
argument_list|()
operator|+
literal|" in ScmBlockLocationProtocol"
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
name|response
operator|.
name|setSuccess
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|exceptionToResponseStatus
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|setMessage
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|response
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|exceptionToResponseStatus (IOException ex)
specifier|private
name|Status
name|exceptionToResponseStatus
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|instanceof
name|SCMException
condition|)
block|{
return|return
name|Status
operator|.
name|values
argument_list|()
index|[
operator|(
operator|(
name|SCMException
operator|)
name|ex
operator|)
operator|.
name|getResult
argument_list|()
operator|.
name|ordinal
argument_list|()
index|]
return|;
block|}
else|else
block|{
return|return
name|Status
operator|.
name|INTERNAL_ERROR
return|;
block|}
block|}
DECL|method|allocateScmBlock ( AllocateScmBlockRequestProto request)
specifier|public
name|AllocateScmBlockResponseProto
name|allocateScmBlock
parameter_list|(
name|AllocateScmBlockRequestProto
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|AllocatedBlock
argument_list|>
name|allocatedBlocks
init|=
name|impl
operator|.
name|allocateBlock
argument_list|(
name|request
operator|.
name|getSize
argument_list|()
argument_list|,
name|request
operator|.
name|getNumBlocks
argument_list|()
argument_list|,
name|request
operator|.
name|getType
argument_list|()
argument_list|,
name|request
operator|.
name|getFactor
argument_list|()
argument_list|,
name|request
operator|.
name|getOwner
argument_list|()
argument_list|,
name|ExcludeList
operator|.
name|getFromProtoBuf
argument_list|(
name|request
operator|.
name|getExcludeList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|AllocateScmBlockResponseProto
operator|.
name|Builder
name|builder
init|=
name|AllocateScmBlockResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|allocatedBlocks
operator|.
name|size
argument_list|()
operator|<
name|request
operator|.
name|getNumBlocks
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Allocated "
operator|+
name|allocatedBlocks
operator|.
name|size
argument_list|()
operator|+
literal|" blocks. Requested "
operator|+
name|request
operator|.
name|getNumBlocks
argument_list|()
operator|+
literal|" blocks"
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_ALLOCATE_ENOUGH_BLOCKS
argument_list|)
throw|;
block|}
for|for
control|(
name|AllocatedBlock
name|block
range|:
name|allocatedBlocks
control|)
block|{
name|builder
operator|.
name|addBlocks
argument_list|(
name|AllocateBlockResponse
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerBlockID
argument_list|(
name|block
operator|.
name|getBlockID
argument_list|()
operator|.
name|getProtobuf
argument_list|()
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|block
operator|.
name|getPipeline
argument_list|()
operator|.
name|getProtobufMessage
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
DECL|method|deleteScmKeyBlocks ( DeleteScmKeyBlocksRequestProto req)
specifier|public
name|DeleteScmKeyBlocksResponseProto
name|deleteScmKeyBlocks
parameter_list|(
name|DeleteScmKeyBlocksRequestProto
name|req
parameter_list|)
throws|throws
name|IOException
block|{
name|DeleteScmKeyBlocksResponseProto
operator|.
name|Builder
name|resp
init|=
name|DeleteScmKeyBlocksResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|infoList
init|=
name|req
operator|.
name|getKeyBlocksList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|BlockGroup
operator|::
name|getFromProto
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
specifier|final
name|List
argument_list|<
name|DeleteBlockGroupResult
argument_list|>
name|results
init|=
name|impl
operator|.
name|deleteKeyBlocks
argument_list|(
name|infoList
argument_list|)
decl_stmt|;
for|for
control|(
name|DeleteBlockGroupResult
name|result
range|:
name|results
control|)
block|{
name|DeleteKeyBlocksResultProto
operator|.
name|Builder
name|deleteResult
init|=
name|DeleteKeyBlocksResultProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setObjectKey
argument_list|(
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|)
operator|.
name|addAllBlockResults
argument_list|(
name|result
operator|.
name|getBlockResultProtoList
argument_list|()
argument_list|)
decl_stmt|;
name|resp
operator|.
name|addResults
argument_list|(
name|deleteResult
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|resp
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
DECL|method|sortDatanodes ( SortDatanodesRequestProto request)
specifier|public
name|SortDatanodesResponseProto
name|sortDatanodes
parameter_list|(
name|SortDatanodesRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
name|SortDatanodesResponseProto
operator|.
name|Builder
name|resp
init|=
name|SortDatanodesResponseProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nodeList
init|=
name|request
operator|.
name|getNodeNetworkNameList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|results
init|=
name|impl
operator|.
name|sortDatanodes
argument_list|(
name|nodeList
argument_list|,
name|request
operator|.
name|getClient
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|!=
literal|null
operator|&&
name|results
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|results
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|dn
lambda|->
name|resp
operator|.
name|addNode
argument_list|(
name|dn
operator|.
name|getProtoBufMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|resp
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

