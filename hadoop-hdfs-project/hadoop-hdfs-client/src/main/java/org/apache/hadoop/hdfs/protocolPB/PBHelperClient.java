begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|StorageType
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
name|DatanodeID
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
name|ShortCircuitShmIdProto
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
name|ShortCircuitShmSlotProto
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
name|protocol
operator|.
name|proto
operator|.
name|HdfsProtos
operator|.
name|DatanodeIDProto
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
name|DatanodeInfoProto
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
name|ExtendedBlockProto
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
name|StorageTypeProto
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
name|shortcircuit
operator|.
name|ShortCircuitShm
operator|.
name|ShmId
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
name|shortcircuit
operator|.
name|ShortCircuitShm
operator|.
name|SlotId
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
name|security
operator|.
name|proto
operator|.
name|SecurityProtos
operator|.
name|TokenProto
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

begin_comment
comment|/**  * Utilities for converting protobuf classes to and from implementation classes  * and other helper utilities to help in dealing with protobuf.  *  * Note that when converting from an internal type to protobuf type, the  * converter never return null for protobuf type. The check for internal type  * being null must be done before calling the convert() method.  */
end_comment

begin_class
DECL|class|PBHelperClient
specifier|public
class|class
name|PBHelperClient
block|{
DECL|method|PBHelperClient ()
specifier|private
name|PBHelperClient
parameter_list|()
block|{
comment|/** Hidden constructor */
block|}
DECL|method|getByteString (byte[] bytes)
specifier|public
specifier|static
name|ByteString
name|getByteString
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
return|return
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|bytes
argument_list|)
return|;
block|}
DECL|method|convert (ShortCircuitShmIdProto shmId)
specifier|public
specifier|static
name|ShmId
name|convert
parameter_list|(
name|ShortCircuitShmIdProto
name|shmId
parameter_list|)
block|{
return|return
operator|new
name|ShmId
argument_list|(
name|shmId
operator|.
name|getHi
argument_list|()
argument_list|,
name|shmId
operator|.
name|getLo
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convert (HdfsProtos.ChecksumTypeProto type)
specifier|public
specifier|static
name|DataChecksum
operator|.
name|Type
name|convert
parameter_list|(
name|HdfsProtos
operator|.
name|ChecksumTypeProto
name|type
parameter_list|)
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|valueOf
argument_list|(
name|type
operator|.
name|getNumber
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convert (DataChecksum.Type type)
specifier|public
specifier|static
name|HdfsProtos
operator|.
name|ChecksumTypeProto
name|convert
parameter_list|(
name|DataChecksum
operator|.
name|Type
name|type
parameter_list|)
block|{
return|return
name|HdfsProtos
operator|.
name|ChecksumTypeProto
operator|.
name|valueOf
argument_list|(
name|type
operator|.
name|id
argument_list|)
return|;
block|}
DECL|method|convert (final ExtendedBlock b)
specifier|public
specifier|static
name|ExtendedBlockProto
name|convert
parameter_list|(
specifier|final
name|ExtendedBlock
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|ExtendedBlockProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPoolId
argument_list|(
name|b
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|setBlockId
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
operator|.
name|setNumBytes
argument_list|(
name|b
operator|.
name|getNumBytes
argument_list|()
argument_list|)
operator|.
name|setGenerationStamp
argument_list|(
name|b
operator|.
name|getGenerationStamp
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert (Token<?> tok)
specifier|public
specifier|static
name|TokenProto
name|convert
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|tok
parameter_list|)
block|{
return|return
name|TokenProto
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
name|tok
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
name|tok
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setKind
argument_list|(
name|tok
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
name|tok
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
DECL|method|convert (ShmId shmId)
specifier|public
specifier|static
name|ShortCircuitShmIdProto
name|convert
parameter_list|(
name|ShmId
name|shmId
parameter_list|)
block|{
return|return
name|ShortCircuitShmIdProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHi
argument_list|(
name|shmId
operator|.
name|getHi
argument_list|()
argument_list|)
operator|.
name|setLo
argument_list|(
name|shmId
operator|.
name|getLo
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert (SlotId slotId)
specifier|public
specifier|static
name|ShortCircuitShmSlotProto
name|convert
parameter_list|(
name|SlotId
name|slotId
parameter_list|)
block|{
return|return
name|ShortCircuitShmSlotProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setShmId
argument_list|(
name|convert
argument_list|(
name|slotId
operator|.
name|getShmId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setSlotIdx
argument_list|(
name|slotId
operator|.
name|getSlotIdx
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert (DatanodeID dn)
specifier|public
specifier|static
name|DatanodeIDProto
name|convert
parameter_list|(
name|DatanodeID
name|dn
parameter_list|)
block|{
comment|// For wire compatibility with older versions we transmit the StorageID
comment|// which is the same as the DatanodeUuid. Since StorageID is a required
comment|// field we pass the empty string if the DatanodeUuid is not yet known.
return|return
name|DatanodeIDProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setIpAddr
argument_list|(
name|dn
operator|.
name|getIpAddr
argument_list|()
argument_list|)
operator|.
name|setHostName
argument_list|(
name|dn
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|setXferPort
argument_list|(
name|dn
operator|.
name|getXferPort
argument_list|()
argument_list|)
operator|.
name|setDatanodeUuid
argument_list|(
name|dn
operator|.
name|getDatanodeUuid
argument_list|()
operator|!=
literal|null
condition|?
name|dn
operator|.
name|getDatanodeUuid
argument_list|()
else|:
literal|""
argument_list|)
operator|.
name|setInfoPort
argument_list|(
name|dn
operator|.
name|getInfoPort
argument_list|()
argument_list|)
operator|.
name|setInfoSecurePort
argument_list|(
name|dn
operator|.
name|getInfoSecurePort
argument_list|()
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|dn
operator|.
name|getIpcPort
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert ( final DatanodeInfo.AdminStates inAs)
specifier|public
specifier|static
name|DatanodeInfoProto
operator|.
name|AdminState
name|convert
parameter_list|(
specifier|final
name|DatanodeInfo
operator|.
name|AdminStates
name|inAs
parameter_list|)
block|{
switch|switch
condition|(
name|inAs
condition|)
block|{
case|case
name|NORMAL
case|:
return|return
name|DatanodeInfoProto
operator|.
name|AdminState
operator|.
name|NORMAL
return|;
case|case
name|DECOMMISSION_INPROGRESS
case|:
return|return
name|DatanodeInfoProto
operator|.
name|AdminState
operator|.
name|DECOMMISSION_INPROGRESS
return|;
case|case
name|DECOMMISSIONED
case|:
return|return
name|DatanodeInfoProto
operator|.
name|AdminState
operator|.
name|DECOMMISSIONED
return|;
default|default:
return|return
name|DatanodeInfoProto
operator|.
name|AdminState
operator|.
name|NORMAL
return|;
block|}
block|}
DECL|method|convert (DatanodeInfo info)
specifier|public
specifier|static
name|DatanodeInfoProto
name|convert
parameter_list|(
name|DatanodeInfo
name|info
parameter_list|)
block|{
name|DatanodeInfoProto
operator|.
name|Builder
name|builder
init|=
name|DatanodeInfoProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getNetworkLocation
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setLocation
argument_list|(
name|info
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setId
argument_list|(
name|convert
argument_list|(
operator|(
name|DatanodeID
operator|)
name|info
argument_list|)
argument_list|)
operator|.
name|setCapacity
argument_list|(
name|info
operator|.
name|getCapacity
argument_list|()
argument_list|)
operator|.
name|setDfsUsed
argument_list|(
name|info
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
operator|.
name|setRemaining
argument_list|(
name|info
operator|.
name|getRemaining
argument_list|()
argument_list|)
operator|.
name|setBlockPoolUsed
argument_list|(
name|info
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
operator|.
name|setCacheCapacity
argument_list|(
name|info
operator|.
name|getCacheCapacity
argument_list|()
argument_list|)
operator|.
name|setCacheUsed
argument_list|(
name|info
operator|.
name|getCacheUsed
argument_list|()
argument_list|)
operator|.
name|setLastUpdate
argument_list|(
name|info
operator|.
name|getLastUpdate
argument_list|()
argument_list|)
operator|.
name|setLastUpdateMonotonic
argument_list|(
name|info
operator|.
name|getLastUpdateMonotonic
argument_list|()
argument_list|)
operator|.
name|setXceiverCount
argument_list|(
name|info
operator|.
name|getXceiverCount
argument_list|()
argument_list|)
operator|.
name|setAdminState
argument_list|(
name|convert
argument_list|(
name|info
operator|.
name|getAdminState
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert ( DatanodeInfo[] dnInfos)
specifier|public
specifier|static
name|List
argument_list|<
name|?
extends|extends
name|HdfsProtos
operator|.
name|DatanodeInfoProto
argument_list|>
name|convert
parameter_list|(
name|DatanodeInfo
index|[]
name|dnInfos
parameter_list|)
block|{
return|return
name|convert
argument_list|(
name|dnInfos
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Copy from {@code dnInfos} to a target of list of same size starting at    * {@code startIdx}.    */
DECL|method|convert ( DatanodeInfo[] dnInfos, int startIdx)
specifier|public
specifier|static
name|List
argument_list|<
name|?
extends|extends
name|HdfsProtos
operator|.
name|DatanodeInfoProto
argument_list|>
name|convert
parameter_list|(
name|DatanodeInfo
index|[]
name|dnInfos
parameter_list|,
name|int
name|startIdx
parameter_list|)
block|{
if|if
condition|(
name|dnInfos
operator|==
literal|null
condition|)
return|return
literal|null
return|;
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
name|convert
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
DECL|method|convert (boolean[] targetPinnings, int idx)
specifier|public
specifier|static
name|List
argument_list|<
name|Boolean
argument_list|>
name|convert
parameter_list|(
name|boolean
index|[]
name|targetPinnings
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
name|List
argument_list|<
name|Boolean
argument_list|>
name|pinnings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetPinnings
operator|==
literal|null
condition|)
block|{
name|pinnings
operator|.
name|add
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
init|;
name|idx
operator|<
name|targetPinnings
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|pinnings
operator|.
name|add
argument_list|(
name|targetPinnings
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pinnings
return|;
block|}
DECL|method|convertDatanodeInfo (DatanodeInfo di)
specifier|static
specifier|public
name|DatanodeInfoProto
name|convertDatanodeInfo
parameter_list|(
name|DatanodeInfo
name|di
parameter_list|)
block|{
if|if
condition|(
name|di
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|convert
argument_list|(
name|di
argument_list|)
return|;
block|}
DECL|method|convertStorageType (StorageType type)
specifier|public
specifier|static
name|StorageTypeProto
name|convertStorageType
parameter_list|(
name|StorageType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|DISK
case|:
return|return
name|StorageTypeProto
operator|.
name|DISK
return|;
case|case
name|SSD
case|:
return|return
name|StorageTypeProto
operator|.
name|SSD
return|;
case|case
name|ARCHIVE
case|:
return|return
name|StorageTypeProto
operator|.
name|ARCHIVE
return|;
case|case
name|RAM_DISK
case|:
return|return
name|StorageTypeProto
operator|.
name|RAM_DISK
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"BUG: StorageType not found, type="
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
DECL|method|convertStorageType (StorageTypeProto type)
specifier|public
specifier|static
name|StorageType
name|convertStorageType
parameter_list|(
name|StorageTypeProto
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|DISK
case|:
return|return
name|StorageType
operator|.
name|DISK
return|;
case|case
name|SSD
case|:
return|return
name|StorageType
operator|.
name|SSD
return|;
case|case
name|ARCHIVE
case|:
return|return
name|StorageType
operator|.
name|ARCHIVE
return|;
case|case
name|RAM_DISK
case|:
return|return
name|StorageType
operator|.
name|RAM_DISK
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"BUG: StorageTypeProto not found, type="
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
DECL|method|convertStorageTypes ( StorageType[] types)
specifier|public
specifier|static
name|List
argument_list|<
name|StorageTypeProto
argument_list|>
name|convertStorageTypes
parameter_list|(
name|StorageType
index|[]
name|types
parameter_list|)
block|{
return|return
name|convertStorageTypes
argument_list|(
name|types
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|convertStorageTypes ( StorageType[] types, int startIdx)
specifier|public
specifier|static
name|List
argument_list|<
name|StorageTypeProto
argument_list|>
name|convertStorageTypes
parameter_list|(
name|StorageType
index|[]
name|types
parameter_list|,
name|int
name|startIdx
parameter_list|)
block|{
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|StorageTypeProto
argument_list|>
name|protos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|types
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
name|types
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|protos
operator|.
name|add
argument_list|(
name|PBHelperClient
operator|.
name|convertStorageType
argument_list|(
name|types
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

