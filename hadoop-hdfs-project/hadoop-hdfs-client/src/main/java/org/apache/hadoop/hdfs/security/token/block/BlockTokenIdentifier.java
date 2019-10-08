begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.security.token.block
package|package
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|util
operator|.
name|Arrays
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
name|Optional
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
name|annotations
operator|.
name|VisibleForTesting
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
name|proto
operator|.
name|HdfsProtos
operator|.
name|AccessModeProto
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
name|BlockTokenSecretProto
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
name|protocolPB
operator|.
name|PBHelperClient
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
name|IOUtils
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
name|io
operator|.
name|WritableUtils
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
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockTokenIdentifier
specifier|public
class|class
name|BlockTokenIdentifier
extends|extends
name|TokenIdentifier
block|{
DECL|field|KIND_NAME
specifier|static
specifier|final
name|Text
name|KIND_NAME
init|=
operator|new
name|Text
argument_list|(
literal|"HDFS_BLOCK_TOKEN"
argument_list|)
decl_stmt|;
DECL|enum|AccessMode
specifier|public
enum|enum
name|AccessMode
block|{
DECL|enumConstant|READ
DECL|enumConstant|WRITE
DECL|enumConstant|COPY
DECL|enumConstant|REPLACE
name|READ
block|,
name|WRITE
block|,
name|COPY
block|,
name|REPLACE
block|}
DECL|field|expiryDate
specifier|private
name|long
name|expiryDate
decl_stmt|;
DECL|field|keyId
specifier|private
name|int
name|keyId
decl_stmt|;
DECL|field|userId
specifier|private
name|String
name|userId
decl_stmt|;
DECL|field|blockPoolId
specifier|private
name|String
name|blockPoolId
decl_stmt|;
DECL|field|blockId
specifier|private
name|long
name|blockId
decl_stmt|;
DECL|field|modes
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|AccessMode
argument_list|>
name|modes
decl_stmt|;
DECL|field|storageTypes
specifier|private
name|StorageType
index|[]
name|storageTypes
decl_stmt|;
DECL|field|storageIds
specifier|private
name|String
index|[]
name|storageIds
decl_stmt|;
DECL|field|useProto
specifier|private
name|boolean
name|useProto
decl_stmt|;
DECL|field|handshakeMsg
specifier|private
name|byte
index|[]
name|handshakeMsg
decl_stmt|;
DECL|field|cache
specifier|private
name|byte
index|[]
name|cache
decl_stmt|;
DECL|method|BlockTokenIdentifier ()
specifier|public
name|BlockTokenIdentifier
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|AccessMode
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|BlockTokenIdentifier (String userId, String bpid, long blockId, EnumSet<AccessMode> modes, StorageType[] storageTypes, String[] storageIds, boolean useProto)
specifier|public
name|BlockTokenIdentifier
parameter_list|(
name|String
name|userId
parameter_list|,
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|,
name|EnumSet
argument_list|<
name|AccessMode
argument_list|>
name|modes
parameter_list|,
name|StorageType
index|[]
name|storageTypes
parameter_list|,
name|String
index|[]
name|storageIds
parameter_list|,
name|boolean
name|useProto
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|blockPoolId
operator|=
name|bpid
expr_stmt|;
name|this
operator|.
name|blockId
operator|=
name|blockId
expr_stmt|;
name|this
operator|.
name|modes
operator|=
name|modes
operator|==
literal|null
condition|?
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|AccessMode
operator|.
name|class
argument_list|)
else|:
name|modes
expr_stmt|;
name|this
operator|.
name|storageTypes
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|storageTypes
argument_list|)
operator|.
name|orElse
argument_list|(
name|StorageType
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageIds
operator|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|storageIds
argument_list|)
operator|.
name|orElse
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|useProto
operator|=
name|useProto
expr_stmt|;
name|this
operator|.
name|handshakeMsg
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKind ()
specifier|public
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|KIND_NAME
return|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|UserGroupInformation
name|getUser
parameter_list|()
block|{
if|if
condition|(
name|userId
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
condition|)
block|{
name|String
name|user
init|=
name|blockPoolId
operator|+
literal|":"
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|user
argument_list|)
return|;
block|}
return|return
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|userId
argument_list|)
return|;
block|}
DECL|method|getExpiryDate ()
specifier|public
name|long
name|getExpiryDate
parameter_list|()
block|{
return|return
name|expiryDate
return|;
block|}
DECL|method|setExpiryDate (long expiryDate)
specifier|public
name|void
name|setExpiryDate
parameter_list|(
name|long
name|expiryDate
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|expiryDate
operator|=
name|expiryDate
expr_stmt|;
block|}
DECL|method|getKeyId ()
specifier|public
name|int
name|getKeyId
parameter_list|()
block|{
return|return
name|this
operator|.
name|keyId
return|;
block|}
DECL|method|setKeyId (int keyId)
specifier|public
name|void
name|setKeyId
parameter_list|(
name|int
name|keyId
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|keyId
operator|=
name|keyId
expr_stmt|;
block|}
DECL|method|getUserId ()
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|blockPoolId
return|;
block|}
DECL|method|getBlockId ()
specifier|public
name|long
name|getBlockId
parameter_list|()
block|{
return|return
name|blockId
return|;
block|}
DECL|method|getAccessModes ()
specifier|public
name|EnumSet
argument_list|<
name|AccessMode
argument_list|>
name|getAccessModes
parameter_list|()
block|{
return|return
name|modes
return|;
block|}
DECL|method|getStorageTypes ()
specifier|public
name|StorageType
index|[]
name|getStorageTypes
parameter_list|()
block|{
return|return
name|storageTypes
return|;
block|}
DECL|method|getStorageIds ()
specifier|public
name|String
index|[]
name|getStorageIds
parameter_list|()
block|{
return|return
name|storageIds
return|;
block|}
DECL|method|getHandshakeMsg ()
specifier|public
name|byte
index|[]
name|getHandshakeMsg
parameter_list|()
block|{
return|return
name|handshakeMsg
return|;
block|}
DECL|method|setHandshakeMsg (byte[] bytes)
specifier|public
name|void
name|setHandshakeMsg
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|cache
operator|=
literal|null
expr_stmt|;
comment|// invalidate the cache
name|handshakeMsg
operator|=
name|bytes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"block_token_identifier (expiryDate="
operator|+
name|this
operator|.
name|getExpiryDate
argument_list|()
operator|+
literal|", keyId="
operator|+
name|this
operator|.
name|getKeyId
argument_list|()
operator|+
literal|", userId="
operator|+
name|this
operator|.
name|getUserId
argument_list|()
operator|+
literal|", blockPoolId="
operator|+
name|this
operator|.
name|getBlockPoolId
argument_list|()
operator|+
literal|", blockId="
operator|+
name|this
operator|.
name|getBlockId
argument_list|()
operator|+
literal|", access modes="
operator|+
name|this
operator|.
name|getAccessModes
argument_list|()
operator|+
literal|", storageTypes= "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|this
operator|.
name|getStorageTypes
argument_list|()
argument_list|)
operator|+
literal|", storageIds= "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|this
operator|.
name|getStorageIds
argument_list|()
argument_list|)
operator|+
literal|")"
return|;
block|}
DECL|method|isEqual (Object a, Object b)
specifier|static
name|boolean
name|isEqual
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
name|a
operator|==
literal|null
condition|?
name|b
operator|==
literal|null
else|:
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|BlockTokenIdentifier
condition|)
block|{
name|BlockTokenIdentifier
name|that
init|=
operator|(
name|BlockTokenIdentifier
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|expiryDate
operator|==
name|that
operator|.
name|expiryDate
operator|&&
name|this
operator|.
name|keyId
operator|==
name|that
operator|.
name|keyId
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|userId
argument_list|,
name|that
operator|.
name|userId
argument_list|)
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|blockPoolId
argument_list|,
name|that
operator|.
name|blockPoolId
argument_list|)
operator|&&
name|this
operator|.
name|blockId
operator|==
name|that
operator|.
name|blockId
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|modes
argument_list|,
name|that
operator|.
name|modes
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|storageTypes
argument_list|,
name|that
operator|.
name|storageTypes
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|storageIds
argument_list|,
name|that
operator|.
name|storageIds
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|expiryDate
operator|^
name|keyId
operator|^
operator|(
name|int
operator|)
name|blockId
operator|^
name|modes
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|userId
operator|==
literal|null
condition|?
literal|0
else|:
name|userId
operator|.
name|hashCode
argument_list|()
operator|)
operator|^
operator|(
name|blockPoolId
operator|==
literal|null
condition|?
literal|0
else|:
name|blockPoolId
operator|.
name|hashCode
argument_list|()
operator|)
operator|^
operator|(
name|storageTypes
operator|==
literal|null
condition|?
literal|0
else|:
name|Arrays
operator|.
name|hashCode
argument_list|(
name|storageTypes
argument_list|)
operator|)
operator|^
operator|(
name|storageIds
operator|==
literal|null
condition|?
literal|0
else|:
name|Arrays
operator|.
name|hashCode
argument_list|(
name|storageIds
argument_list|)
operator|)
return|;
block|}
comment|/**    * readFields peeks at the first byte of the DataInput and determines if it    * was written using WritableUtils ("Legacy") or Protobuf. We can do this    * because we know the first field is the Expiry date.    *    * In the case of the legacy buffer, the expiry date is a VInt, so the size    * (which should always be&gt;1) is encoded in the first byte - which is    * always negative due to this encoding. However, there are sometimes null    * BlockTokenIdentifier written so we also need to handle the case there    * the first byte is also 0.    *    * In the case of protobuf, the first byte is a type tag for the expiry date    * which is written as<code>field_number&lt;&lt; 3 | wire_type</code>.    * So as long as the field_number  is less than 16, but also positive, then    * we know we have a Protobuf.    *    * @param in<code>DataInput</code> to deserialize this object from.    * @throws IOException    */
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|cache
operator|=
literal|null
expr_stmt|;
specifier|final
name|DataInputStream
name|dis
init|=
operator|(
name|DataInputStream
operator|)
name|in
decl_stmt|;
if|if
condition|(
operator|!
name|dis
operator|.
name|markSupported
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not peek first byte."
argument_list|)
throw|;
block|}
comment|// this.cache should be assigned the raw bytes from the input data for
comment|// upgrading compatibility. If we won't mutate fields and call getBytes()
comment|// for something (e.g retrieve password), we should return the raw bytes
comment|// instead of serializing the instance self fields to bytes, because we may
comment|// lose newly added fields which we can't recognize
name|this
operator|.
name|cache
operator|=
name|IOUtils
operator|.
name|readFullyToByteArray
argument_list|(
name|dis
argument_list|)
expr_stmt|;
name|dis
operator|.
name|reset
argument_list|()
expr_stmt|;
name|dis
operator|.
name|mark
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|firstByte
init|=
name|dis
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|dis
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|firstByte
operator|<=
literal|0
condition|)
block|{
name|readFieldsLegacy
argument_list|(
name|dis
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|readFieldsProtobuf
argument_list|(
name|dis
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|readFieldsLegacy (DataInput in)
name|void
name|readFieldsLegacy
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|expiryDate
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|keyId
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|userId
operator|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|blockPoolId
operator|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|blockId
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|length
init|=
name|WritableUtils
operator|.
name|readVIntInRange
argument_list|(
name|in
argument_list|,
literal|0
argument_list|,
name|AccessMode
operator|.
name|class
operator|.
name|getEnumConstants
argument_list|()
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|modes
operator|.
name|add
argument_list|(
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|AccessMode
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|length
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|StorageType
index|[]
name|readStorageTypes
init|=
operator|new
name|StorageType
index|[
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|readStorageTypes
index|[
name|i
index|]
operator|=
name|WritableUtils
operator|.
name|readEnum
argument_list|(
name|in
argument_list|,
name|StorageType
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|storageTypes
operator|=
name|readStorageTypes
expr_stmt|;
name|length
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|String
index|[]
name|readStorageIds
init|=
operator|new
name|String
index|[
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|readStorageIds
index|[
name|i
index|]
operator|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|storageIds
operator|=
name|readStorageIds
expr_stmt|;
name|useProto
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|int
name|handshakeMsgLen
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|handshakeMsgLen
operator|!=
literal|0
condition|)
block|{
name|handshakeMsg
operator|=
operator|new
name|byte
index|[
name|handshakeMsgLen
index|]
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|handshakeMsg
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|eof
parameter_list|)
block|{      }
block|}
annotation|@
name|VisibleForTesting
DECL|method|readFieldsProtobuf (DataInput in)
name|void
name|readFieldsProtobuf
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockTokenSecretProto
name|blockTokenSecretProto
init|=
name|BlockTokenSecretProto
operator|.
name|parseFrom
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
decl_stmt|;
name|expiryDate
operator|=
name|blockTokenSecretProto
operator|.
name|getExpiryDate
argument_list|()
expr_stmt|;
name|keyId
operator|=
name|blockTokenSecretProto
operator|.
name|getKeyId
argument_list|()
expr_stmt|;
if|if
condition|(
name|blockTokenSecretProto
operator|.
name|hasUserId
argument_list|()
condition|)
block|{
name|userId
operator|=
name|blockTokenSecretProto
operator|.
name|getUserId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|userId
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|blockTokenSecretProto
operator|.
name|hasBlockPoolId
argument_list|()
condition|)
block|{
name|blockPoolId
operator|=
name|blockTokenSecretProto
operator|.
name|getBlockPoolId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|blockPoolId
operator|=
literal|null
expr_stmt|;
block|}
name|blockId
operator|=
name|blockTokenSecretProto
operator|.
name|getBlockId
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blockTokenSecretProto
operator|.
name|getModesCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|AccessModeProto
name|accessModeProto
init|=
name|blockTokenSecretProto
operator|.
name|getModes
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|modes
operator|.
name|add
argument_list|(
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|accessModeProto
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|storageTypes
operator|=
name|blockTokenSecretProto
operator|.
name|getStorageTypesList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|PBHelperClient
operator|::
name|convertStorageType
argument_list|)
operator|.
name|toArray
argument_list|(
name|StorageType
index|[]
operator|::
operator|new
argument_list|)
expr_stmt|;
name|storageIds
operator|=
name|blockTokenSecretProto
operator|.
name|getStorageIdsList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|toArray
argument_list|(
name|String
index|[]
operator|::
operator|new
argument_list|)
expr_stmt|;
name|useProto
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|blockTokenSecretProto
operator|.
name|hasHandshakeSecret
argument_list|()
condition|)
block|{
name|handshakeMsg
operator|=
name|blockTokenSecretProto
operator|.
name|getHandshakeSecret
argument_list|()
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|handshakeMsg
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|useProto
condition|)
block|{
name|writeProtobuf
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeLegacy
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|writeLegacy (DataOutput out)
name|void
name|writeLegacy
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|expiryDate
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|keyId
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|userId
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|blockPoolId
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|blockId
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|modes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AccessMode
name|aMode
range|:
name|modes
control|)
block|{
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|aMode
argument_list|)
expr_stmt|;
block|}
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|storageTypes
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|StorageType
name|type
range|:
name|storageTypes
control|)
block|{
name|WritableUtils
operator|.
name|writeEnum
argument_list|(
name|out
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|storageIds
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|storageIds
control|)
block|{
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|handshakeMsg
operator|!=
literal|null
operator|&&
name|handshakeMsg
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|handshakeMsg
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|handshakeMsg
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|writeProtobuf (DataOutput out)
name|void
name|writeProtobuf
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockTokenSecretProto
name|secret
init|=
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|secret
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytes ()
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
name|cache
operator|=
name|super
operator|.
name|getBytes
argument_list|()
expr_stmt|;
return|return
name|cache
return|;
block|}
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Renewer
specifier|public
specifier|static
class|class
name|Renewer
extends|extends
name|Token
operator|.
name|TrivialRenewer
block|{
annotation|@
name|Override
DECL|method|getKind ()
specifier|protected
name|Text
name|getKind
parameter_list|()
block|{
return|return
name|KIND_NAME
return|;
block|}
block|}
block|}
end_class

end_unit

