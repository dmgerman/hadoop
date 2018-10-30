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
comment|/**    * Creates a new ScmBlockLocationProtocolServerSideTranslatorPB.    *    * @param impl {@link ScmBlockLocationProtocol} server implementation    */
DECL|method|ScmBlockLocationProtocolServerSideTranslatorPB ( ScmBlockLocationProtocol impl)
specifier|public
name|ScmBlockLocationProtocolServerSideTranslatorPB
parameter_list|(
name|ScmBlockLocationProtocol
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
DECL|method|allocateScmBlock ( RpcController controller, AllocateScmBlockRequestProto request)
specifier|public
name|AllocateScmBlockResponseProto
name|allocateScmBlock
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|AllocateScmBlockRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|AllocatedBlock
name|allocatedBlock
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|allocatedBlock
operator|!=
literal|null
condition|)
block|{
return|return
name|AllocateScmBlockResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerBlockID
argument_list|(
name|allocatedBlock
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
name|allocatedBlock
operator|.
name|getPipeline
argument_list|()
operator|.
name|getProtobufMessage
argument_list|()
argument_list|)
operator|.
name|setCreateContainer
argument_list|(
name|allocatedBlock
operator|.
name|getCreateContainer
argument_list|()
argument_list|)
operator|.
name|setErrorCode
argument_list|(
name|AllocateScmBlockResponseProto
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
else|else
block|{
return|return
name|AllocateScmBlockResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setErrorCode
argument_list|(
name|AllocateScmBlockResponseProto
operator|.
name|Error
operator|.
name|unknownFailure
argument_list|)
operator|.
name|build
argument_list|()
return|;
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
annotation|@
name|Override
DECL|method|deleteScmKeyBlocks ( RpcController controller, DeleteScmKeyBlocksRequestProto req)
specifier|public
name|DeleteScmKeyBlocksResponseProto
name|deleteScmKeyBlocks
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|DeleteScmKeyBlocksRequestProto
name|req
parameter_list|)
throws|throws
name|ServiceException
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
try|try
block|{
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
return|return
name|resp
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getScmInfo ( RpcController controller, HddsProtos.GetScmInfoRequestProto req)
specifier|public
name|HddsProtos
operator|.
name|GetScmInfoRespsonseProto
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
name|ScmInfo
name|scmInfo
decl_stmt|;
try|try
block|{
name|scmInfo
operator|=
name|impl
operator|.
name|getScmInfo
argument_list|()
expr_stmt|;
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
return|return
name|HddsProtos
operator|.
name|GetScmInfoRespsonseProto
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
block|}
end_class

end_unit

