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
name|HdfsProtoUtil
import|;
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
name|BaseHeaderProto
import|;
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
name|protocol
operator|.
name|proto
operator|.
name|HdfsProtos
operator|.
name|ChecksumTypeProto
import|;
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

begin_comment
comment|/**  * Static utilities for dealing with the protocol buffers used by the  * Data Transfer Protocol.  */
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
DECL|class|DataTransferProtoUtil
specifier|public
specifier|abstract
class|class
name|DataTransferProtoUtil
block|{
DECL|method|fromProto ( OpWriteBlockProto.BlockConstructionStage stage)
specifier|static
name|BlockConstructionStage
name|fromProto
parameter_list|(
name|OpWriteBlockProto
operator|.
name|BlockConstructionStage
name|stage
parameter_list|)
block|{
return|return
name|BlockConstructionStage
operator|.
name|valueOf
argument_list|(
name|BlockConstructionStage
operator|.
name|class
argument_list|,
name|stage
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toProto ( BlockConstructionStage stage)
specifier|static
name|OpWriteBlockProto
operator|.
name|BlockConstructionStage
name|toProto
parameter_list|(
name|BlockConstructionStage
name|stage
parameter_list|)
block|{
return|return
name|OpWriteBlockProto
operator|.
name|BlockConstructionStage
operator|.
name|valueOf
argument_list|(
name|stage
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toProto (DataChecksum checksum)
specifier|public
specifier|static
name|ChecksumProto
name|toProto
parameter_list|(
name|DataChecksum
name|checksum
parameter_list|)
block|{
name|ChecksumTypeProto
name|type
init|=
name|HdfsProtoUtil
operator|.
name|toProto
argument_list|(
name|checksum
operator|.
name|getChecksumType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't convert checksum to protobuf: "
operator|+
name|checksum
argument_list|)
throw|;
block|}
return|return
name|ChecksumProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBytesPerChecksum
argument_list|(
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|fromProto (ChecksumProto proto)
specifier|public
specifier|static
name|DataChecksum
name|fromProto
parameter_list|(
name|ChecksumProto
name|proto
parameter_list|)
block|{
if|if
condition|(
name|proto
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|int
name|bytesPerChecksum
init|=
name|proto
operator|.
name|getBytesPerChecksum
argument_list|()
decl_stmt|;
name|DataChecksum
operator|.
name|Type
name|type
init|=
name|HdfsProtoUtil
operator|.
name|fromProto
argument_list|(
name|proto
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|type
argument_list|,
name|bytesPerChecksum
argument_list|)
return|;
block|}
DECL|method|buildClientHeader (ExtendedBlock blk, String client, Token<BlockTokenIdentifier> blockToken)
specifier|static
name|ClientOperationHeaderProto
name|buildClientHeader
parameter_list|(
name|ExtendedBlock
name|blk
parameter_list|,
name|String
name|client
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|)
block|{
name|ClientOperationHeaderProto
name|header
init|=
name|ClientOperationHeaderProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBaseHeader
argument_list|(
name|buildBaseHeader
argument_list|(
name|blk
argument_list|,
name|blockToken
argument_list|)
argument_list|)
operator|.
name|setClientName
argument_list|(
name|client
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|header
return|;
block|}
DECL|method|buildBaseHeader (ExtendedBlock blk, Token<BlockTokenIdentifier> blockToken)
specifier|static
name|BaseHeaderProto
name|buildBaseHeader
parameter_list|(
name|ExtendedBlock
name|blk
parameter_list|,
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|blockToken
parameter_list|)
block|{
return|return
name|BaseHeaderProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlock
argument_list|(
name|HdfsProtoUtil
operator|.
name|toProto
argument_list|(
name|blk
argument_list|)
argument_list|)
operator|.
name|setToken
argument_list|(
name|HdfsProtoUtil
operator|.
name|toProto
argument_list|(
name|blockToken
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

