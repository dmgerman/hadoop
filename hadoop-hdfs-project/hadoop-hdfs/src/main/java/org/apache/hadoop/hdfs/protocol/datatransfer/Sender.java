begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsProtoUtil
operator|.
name|toProto
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsProtoUtil
operator|.
name|toProtos
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
name|hdfs
operator|.
name|protocol
operator|.
name|datatransfer
operator|.
name|DataTransferProtoUtil
operator|.
name|toProto
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|ExtendedBlock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|ChecksumProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|ClientOperationHeaderProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|OpBlockChecksumProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|OpCopyBlockProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|OpReadBlockProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|OpReplaceBlockProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|OpTransferBlockProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|OpRequestShortCircuitAccessProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|DataTransferProtos
operator|.
name|OpWriteBlockProto
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|DataChecksum
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
name|Message
import|;
end_import

begin_comment
comment|/** Sender */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Sender
specifier|public
class|class
name|Sender
implements|implements
name|DataTransferProtocol
block|{
DECL|field|out
specifier|private
specifier|final
name|DataOutputStream
name|out
decl_stmt|;
comment|/** Create a sender for DataTransferProtocol with a output stream. */
DECL|method|Sender (final DataOutputStream out)
specifier|public
name|Sender
parameter_list|(
specifier|final
name|DataOutputStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/** Initialize a operation. */
DECL|method|op (final DataOutput out, final Op op )
specifier|private
specifier|static
name|void
name|op
parameter_list|(
specifier|final
name|DataOutput
name|out
parameter_list|,
specifier|final
name|Op
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeShort
argument_list|(
name|DataTransferProtocol
operator|.
name|DATA_TRANSFER_VERSION
argument_list|)
expr_stmt|;
name|op
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|send (final DataOutputStream out, final Op opcode, final Message proto)
specifier|private
specifier|static
name|void
name|send
parameter_list|(
specifier|final
name|DataOutputStream
name|out
parameter_list|,
specifier|final
name|Op
name|opcode
parameter_list|,
specifier|final
name|Message
name|proto
parameter_list|)
throws|throws
name|IOException
block|{
name|op
argument_list|(
name|out
argument_list|,
name|opcode
argument_list|)
expr_stmt|;
name|proto
operator|.
name|writeDelimitedTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readBlock (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken, final String clientName, final long blockOffset, final long length)
specifier|public
name|void
name|readBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|long
name|blockOffset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|OpReadBlockProto
name|proto
init|=
name|OpReadBlockProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHeader
argument_list|(
name|DataTransferProtoUtil
operator|.
name|buildClientHeader
argument_list|(
name|blk
argument_list|,
name|clientName
argument_list|,
name|blockToken
argument_list|)
argument_list|)
operator|.
name|setOffset
argument_list|(
name|blockOffset
argument_list|)
operator|.
name|setLen
argument_list|(
name|length
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|send
argument_list|(
name|out
argument_list|,
name|Op
operator|.
name|READ_BLOCK
argument_list|,
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBlock (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken, final String clientName, final DatanodeInfo[] targets, final DatanodeInfo source, final BlockConstructionStage stage, final int pipelineSize, final long minBytesRcvd, final long maxBytesRcvd, final long latestGenerationStamp, DataChecksum requestedChecksum)
specifier|public
name|void
name|writeBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|DatanodeInfo
index|[]
name|targets
parameter_list|,
specifier|final
name|DatanodeInfo
name|source
parameter_list|,
specifier|final
name|BlockConstructionStage
name|stage
parameter_list|,
specifier|final
name|int
name|pipelineSize
parameter_list|,
specifier|final
name|long
name|minBytesRcvd
parameter_list|,
specifier|final
name|long
name|maxBytesRcvd
parameter_list|,
specifier|final
name|long
name|latestGenerationStamp
parameter_list|,
name|DataChecksum
name|requestedChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|ClientOperationHeaderProto
name|header
init|=
name|DataTransferProtoUtil
operator|.
name|buildClientHeader
argument_list|(
name|blk
argument_list|,
name|clientName
argument_list|,
name|blockToken
argument_list|)
decl_stmt|;
name|ChecksumProto
name|checksumProto
init|=
name|DataTransferProtoUtil
operator|.
name|toProto
argument_list|(
name|requestedChecksum
argument_list|)
decl_stmt|;
name|OpWriteBlockProto
operator|.
name|Builder
name|proto
init|=
name|OpWriteBlockProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHeader
argument_list|(
name|header
argument_list|)
operator|.
name|addAllTargets
argument_list|(
name|toProtos
argument_list|(
name|targets
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|setStage
argument_list|(
name|toProto
argument_list|(
name|stage
argument_list|)
argument_list|)
operator|.
name|setPipelineSize
argument_list|(
name|pipelineSize
argument_list|)
operator|.
name|setMinBytesRcvd
argument_list|(
name|minBytesRcvd
argument_list|)
operator|.
name|setMaxBytesRcvd
argument_list|(
name|maxBytesRcvd
argument_list|)
operator|.
name|setLatestGenerationStamp
argument_list|(
name|latestGenerationStamp
argument_list|)
operator|.
name|setRequestedChecksum
argument_list|(
name|checksumProto
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|proto
operator|.
name|setSource
argument_list|(
name|toProto
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|send
argument_list|(
name|out
argument_list|,
name|Op
operator|.
name|WRITE_BLOCK
argument_list|,
name|proto
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transferBlock (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken, final String clientName, final DatanodeInfo[] targets)
specifier|public
name|void
name|transferBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|,
specifier|final
name|DatanodeInfo
index|[]
name|targets
parameter_list|)
throws|throws
name|IOException
block|{
name|OpTransferBlockProto
name|proto
init|=
name|OpTransferBlockProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHeader
argument_list|(
name|DataTransferProtoUtil
operator|.
name|buildClientHeader
argument_list|(
name|blk
argument_list|,
name|clientName
argument_list|,
name|blockToken
argument_list|)
argument_list|)
operator|.
name|addAllTargets
argument_list|(
name|toProtos
argument_list|(
name|targets
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|send
argument_list|(
name|out
argument_list|,
name|Op
operator|.
name|TRANSFER_BLOCK
argument_list|,
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|requestShortCircuitFds (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken, int maxVersion)
specifier|public
name|void
name|requestShortCircuitFds
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
name|int
name|maxVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|OpRequestShortCircuitAccessProto
name|proto
init|=
name|OpRequestShortCircuitAccessProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHeader
argument_list|(
name|DataTransferProtoUtil
operator|.
name|buildBaseHeader
argument_list|(
name|blk
argument_list|,
name|blockToken
argument_list|)
argument_list|)
operator|.
name|setMaxVersion
argument_list|(
name|maxVersion
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|send
argument_list|(
name|out
argument_list|,
name|Op
operator|.
name|REQUEST_SHORT_CIRCUIT_FDS
argument_list|,
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|replaceBlock (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken, final String delHint, final DatanodeInfo source)
specifier|public
name|void
name|replaceBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|,
specifier|final
name|String
name|delHint
parameter_list|,
specifier|final
name|DatanodeInfo
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|OpReplaceBlockProto
name|proto
init|=
name|OpReplaceBlockProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHeader
argument_list|(
name|DataTransferProtoUtil
operator|.
name|buildBaseHeader
argument_list|(
name|blk
argument_list|,
name|blockToken
argument_list|)
argument_list|)
operator|.
name|setDelHint
argument_list|(
name|delHint
argument_list|)
operator|.
name|setSource
argument_list|(
name|toProto
argument_list|(
name|source
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|send
argument_list|(
name|out
argument_list|,
name|Op
operator|.
name|REPLACE_BLOCK
argument_list|,
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyBlock (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken)
specifier|public
name|void
name|copyBlock
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|)
throws|throws
name|IOException
block|{
name|OpCopyBlockProto
name|proto
init|=
name|OpCopyBlockProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHeader
argument_list|(
name|DataTransferProtoUtil
operator|.
name|buildBaseHeader
argument_list|(
name|blk
argument_list|,
name|blockToken
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|send
argument_list|(
name|out
argument_list|,
name|Op
operator|.
name|COPY_BLOCK
argument_list|,
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|blockChecksum (final ExtendedBlock blk, final Token<BlockTokenIdentifier> blockToken)
specifier|public
name|void
name|blockChecksum
parameter_list|(
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|)
throws|throws
name|IOException
block|{
name|OpBlockChecksumProto
name|proto
init|=
name|OpBlockChecksumProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHeader
argument_list|(
name|DataTransferProtoUtil
operator|.
name|buildBaseHeader
argument_list|(
name|blk
argument_list|,
name|blockToken
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|send
argument_list|(
name|out
argument_list|,
name|Op
operator|.
name|BLOCK_CHECKSUM
argument_list|,
name|proto
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

