begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|io
operator|.
name|InputStream
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
name|proto
operator|.
name|HdfsProtos
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
name|hdfs
operator|.
name|util
operator|.
name|ExactSizeInputStream
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
name|io
operator|.
name|Text
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|ByteString
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
name|CodedInputStream
import|;
end_import

begin_comment
comment|/**  * Utilities for converting to and from protocol buffers used in the  * HDFS wire protocol, as well as some generic utilities useful  * for dealing with protocol buffers.  */
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
DECL|class|HdfsProtoUtil
specifier|public
specifier|abstract
class|class
name|HdfsProtoUtil
block|{
comment|//// Block Token ////
DECL|method|toProto (Token<?> blockToken)
specifier|public
specifier|static
name|HdfsProtos
operator|.
name|BlockTokenIdentifierProto
name|toProto
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|blockToken
parameter_list|)
block|{
return|return
name|HdfsProtos
operator|.
name|BlockTokenIdentifierProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setIdentifier
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|blockToken
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setPassword
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|blockToken
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setKind
argument_list|(
name|blockToken
operator|.
name|getKind
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setService
argument_list|(
name|blockToken
operator|.
name|getService
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|fromProto (HdfsProtos.BlockTokenIdentifierProto proto)
specifier|public
specifier|static
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|fromProto
parameter_list|(
name|HdfsProtos
operator|.
name|BlockTokenIdentifierProto
name|proto
parameter_list|)
block|{
return|return
operator|new
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
argument_list|(
name|proto
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|proto
operator|.
name|getPassword
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|proto
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|proto
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|//// Extended Block ////
DECL|method|toProto (ExtendedBlock block)
specifier|public
specifier|static
name|HdfsProtos
operator|.
name|ExtendedBlockProto
name|toProto
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|)
block|{
return|return
name|HdfsProtos
operator|.
name|ExtendedBlockProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlockId
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
operator|.
name|setPoolId
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|setNumBytes
argument_list|(
name|block
operator|.
name|getNumBytes
argument_list|()
argument_list|)
operator|.
name|setGenerationStamp
argument_list|(
name|block
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|fromProto (HdfsProtos.ExtendedBlockProto proto)
specifier|public
specifier|static
name|ExtendedBlock
name|fromProto
parameter_list|(
name|HdfsProtos
operator|.
name|ExtendedBlockProto
name|proto
parameter_list|)
block|{
return|return
operator|new
name|ExtendedBlock
argument_list|(
name|proto
operator|.
name|getPoolId
argument_list|()
argument_list|,
name|proto
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|proto
operator|.
name|getNumBytes
argument_list|()
argument_list|,
name|proto
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
return|;
block|}
comment|//// DatanodeID ////
DECL|method|toProto ( DatanodeID dni)
specifier|private
specifier|static
name|HdfsProtos
operator|.
name|DatanodeIDProto
name|toProto
parameter_list|(
name|DatanodeID
name|dni
parameter_list|)
block|{
return|return
name|HdfsProtos
operator|.
name|DatanodeIDProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
name|dni
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|setHostName
argument_list|(
name|dni
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|setStorageID
argument_list|(
name|dni
operator|.
name|getStorageID
argument_list|()
argument_list|)
operator|.
name|setInfoPort
argument_list|(
name|dni
operator|.
name|getInfoPort
argument_list|()
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|dni
operator|.
name|getIpcPort
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|fromProto (HdfsProtos.DatanodeIDProto idProto)
specifier|private
specifier|static
name|DatanodeID
name|fromProto
parameter_list|(
name|HdfsProtos
operator|.
name|DatanodeIDProto
name|idProto
parameter_list|)
block|{
return|return
operator|new
name|DatanodeID
argument_list|(
name|idProto
operator|.
name|getName
argument_list|()
argument_list|,
name|idProto
operator|.
name|getHostName
argument_list|()
argument_list|,
name|idProto
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|idProto
operator|.
name|getInfoPort
argument_list|()
argument_list|,
name|idProto
operator|.
name|getIpcPort
argument_list|()
argument_list|)
return|;
block|}
comment|//// DatanodeInfo ////
DECL|method|toProto (DatanodeInfo dni)
specifier|public
specifier|static
name|HdfsProtos
operator|.
name|DatanodeInfoProto
name|toProto
parameter_list|(
name|DatanodeInfo
name|dni
parameter_list|)
block|{
return|return
name|HdfsProtos
operator|.
name|DatanodeInfoProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
name|toProto
argument_list|(
operator|(
name|DatanodeID
operator|)
name|dni
argument_list|)
argument_list|)
operator|.
name|setCapacity
argument_list|(
name|dni
operator|.
name|getCapacity
argument_list|()
argument_list|)
operator|.
name|setDfsUsed
argument_list|(
name|dni
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
operator|.
name|setRemaining
argument_list|(
name|dni
operator|.
name|getRemaining
argument_list|()
argument_list|)
operator|.
name|setBlockPoolUsed
argument_list|(
name|dni
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
operator|.
name|setLastUpdate
argument_list|(
name|dni
operator|.
name|getLastUpdate
argument_list|()
argument_list|)
operator|.
name|setXceiverCount
argument_list|(
name|dni
operator|.
name|getXceiverCount
argument_list|()
argument_list|)
operator|.
name|setLocation
argument_list|(
name|dni
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
operator|.
name|setAdminState
argument_list|(
name|HdfsProtos
operator|.
name|DatanodeInfoProto
operator|.
name|AdminState
operator|.
name|valueOf
argument_list|(
name|dni
operator|.
name|getAdminState
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|fromProto (HdfsProtos.DatanodeInfoProto dniProto)
specifier|public
specifier|static
name|DatanodeInfo
name|fromProto
parameter_list|(
name|HdfsProtos
operator|.
name|DatanodeInfoProto
name|dniProto
parameter_list|)
block|{
name|DatanodeInfo
name|dniObj
init|=
operator|new
name|DatanodeInfo
argument_list|(
name|fromProto
argument_list|(
name|dniProto
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|dniProto
operator|.
name|getLocation
argument_list|()
argument_list|)
decl_stmt|;
name|dniObj
operator|.
name|setCapacity
argument_list|(
name|dniProto
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|dniObj
operator|.
name|setDfsUsed
argument_list|(
name|dniProto
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|dniObj
operator|.
name|setRemaining
argument_list|(
name|dniProto
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
name|dniObj
operator|.
name|setBlockPoolUsed
argument_list|(
name|dniProto
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
expr_stmt|;
name|dniObj
operator|.
name|setLastUpdate
argument_list|(
name|dniProto
operator|.
name|getLastUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|dniObj
operator|.
name|setXceiverCount
argument_list|(
name|dniProto
operator|.
name|getXceiverCount
argument_list|()
argument_list|)
expr_stmt|;
name|dniObj
operator|.
name|setAdminState
argument_list|(
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|valueOf
argument_list|(
name|dniProto
operator|.
name|getAdminState
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|dniObj
return|;
block|}
DECL|method|toProtos ( DatanodeInfo[] dnInfos, int startIdx)
specifier|public
specifier|static
name|ArrayList
argument_list|<
name|?
extends|extends
name|HdfsProtos
operator|.
name|DatanodeInfoProto
argument_list|>
name|toProtos
parameter_list|(
name|DatanodeInfo
index|[]
name|dnInfos
parameter_list|,
name|int
name|startIdx
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|HdfsProtos
operator|.
name|DatanodeInfoProto
argument_list|>
name|protos
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|dnInfos
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startIdx
init|;
name|i
operator|<
name|dnInfos
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|protos
operator|.
name|add
argument_list|(
name|toProto
argument_list|(
name|dnInfos
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|protos
return|;
block|}
DECL|method|fromProtos ( List<HdfsProtos.DatanodeInfoProto> targetsList)
specifier|public
specifier|static
name|DatanodeInfo
index|[]
name|fromProtos
parameter_list|(
name|List
argument_list|<
name|HdfsProtos
operator|.
name|DatanodeInfoProto
argument_list|>
name|targetsList
parameter_list|)
block|{
name|DatanodeInfo
index|[]
name|ret
init|=
operator|new
name|DatanodeInfo
index|[
name|targetsList
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|HdfsProtos
operator|.
name|DatanodeInfoProto
name|proto
range|:
name|targetsList
control|)
block|{
name|ret
index|[
name|i
operator|++
index|]
operator|=
name|fromProto
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|vintPrefixed (final InputStream input)
specifier|public
specifier|static
name|InputStream
name|vintPrefixed
parameter_list|(
specifier|final
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|firstByte
init|=
name|input
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstByte
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Premature EOF: no length prefix available"
argument_list|)
throw|;
block|}
name|int
name|size
init|=
name|CodedInputStream
operator|.
name|readRawVarint32
argument_list|(
name|firstByte
argument_list|,
name|input
argument_list|)
decl_stmt|;
assert|assert
name|size
operator|>=
literal|0
assert|;
return|return
operator|new
name|ExactSizeInputStream
argument_list|(
name|input
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
end_class

end_unit

