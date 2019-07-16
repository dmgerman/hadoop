begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ByteString
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
name|crypto
operator|.
name|CipherSuite
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
name|crypto
operator|.
name|CryptoProtocolVersion
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
name|FileEncryptionInfo
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|BucketEncryptionKeyInfo
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
name|OzoneManagerProtocolProtos
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
name|OzoneManagerProtocolProtos
operator|.
name|BucketEncryptionInfoProto
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
name|OzoneManagerProtocolProtos
operator|.
name|CipherSuiteProto
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
name|OzoneManagerProtocolProtos
operator|.
name|CryptoProtocolVersionProto
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
name|OzoneManagerProtocolProtos
operator|.
name|FileEncryptionInfoProto
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
name|security
operator|.
name|OzoneTokenIdentifier
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

begin_comment
comment|/**  * Utilities for converting protobuf classes.  */
end_comment

begin_class
DECL|class|OMPBHelper
specifier|public
specifier|final
class|class
name|OMPBHelper
block|{
DECL|method|OMPBHelper ()
specifier|private
name|OMPBHelper
parameter_list|()
block|{
comment|/** Hidden constructor */
block|}
comment|/**    * Converts Ozone delegation token to @{@link TokenProto}.    * @return tokenProto    */
DECL|method|convertToTokenProto (Token<?> tok)
specifier|public
specifier|static
name|TokenProto
name|convertToTokenProto
parameter_list|(
name|Token
argument_list|<
name|?
argument_list|>
name|tok
parameter_list|)
block|{
if|if
condition|(
name|tok
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid argument: token is null"
argument_list|)
throw|;
block|}
return|return
name|TokenProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setIdentifier
argument_list|(
name|getByteString
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
name|getByteString
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
comment|// return singleton to reduce object allocation
return|return
operator|(
name|bytes
operator|.
name|length
operator|==
literal|0
operator|)
condition|?
name|ByteString
operator|.
name|EMPTY
else|:
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|bytes
argument_list|)
return|;
block|}
comment|/**    * Converts @{@link TokenProto} to Ozone delegation token.    *    * @return Ozone    */
DECL|method|convertToDelegationToken ( TokenProto tokenProto)
specifier|public
specifier|static
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|convertToDelegationToken
parameter_list|(
name|TokenProto
name|tokenProto
parameter_list|)
block|{
return|return
operator|new
name|Token
argument_list|<>
argument_list|(
name|tokenProto
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|tokenProto
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
name|tokenProto
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|tokenProto
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|convert ( BucketEncryptionInfoProto beInfo)
specifier|public
specifier|static
name|BucketEncryptionKeyInfo
name|convert
parameter_list|(
name|BucketEncryptionInfoProto
name|beInfo
parameter_list|)
block|{
if|if
condition|(
name|beInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid argument: bucket encryption"
operator|+
literal|" info is null"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BucketEncryptionKeyInfo
argument_list|(
name|beInfo
operator|.
name|hasCryptoProtocolVersion
argument_list|()
condition|?
name|convert
argument_list|(
name|beInfo
operator|.
name|getCryptoProtocolVersion
argument_list|()
argument_list|)
else|:
literal|null
argument_list|,
name|beInfo
operator|.
name|hasSuite
argument_list|()
condition|?
name|convert
argument_list|(
name|beInfo
operator|.
name|getSuite
argument_list|()
argument_list|)
else|:
literal|null
argument_list|,
name|beInfo
operator|.
name|getKeyName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convert ( BucketEncryptionKeyInfo beInfo)
specifier|public
specifier|static
name|BucketEncryptionInfoProto
name|convert
parameter_list|(
name|BucketEncryptionKeyInfo
name|beInfo
parameter_list|)
block|{
if|if
condition|(
name|beInfo
operator|==
literal|null
operator|||
name|beInfo
operator|.
name|getKeyName
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid argument: bucket encryption"
operator|+
literal|" info is null"
argument_list|)
throw|;
block|}
name|BucketEncryptionInfoProto
operator|.
name|Builder
name|bb
init|=
name|BucketEncryptionInfoProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyName
argument_list|(
name|beInfo
operator|.
name|getKeyName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|beInfo
operator|.
name|getSuite
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|bb
operator|.
name|setSuite
argument_list|(
name|convert
argument_list|(
name|beInfo
operator|.
name|getSuite
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|beInfo
operator|.
name|getVersion
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|bb
operator|.
name|setCryptoProtocolVersion
argument_list|(
name|convert
argument_list|(
name|beInfo
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bb
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert ( FileEncryptionInfo info)
specifier|public
specifier|static
name|FileEncryptionInfoProto
name|convert
parameter_list|(
name|FileEncryptionInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|OzoneManagerProtocolProtos
operator|.
name|FileEncryptionInfoProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setSuite
argument_list|(
name|convert
argument_list|(
name|info
operator|.
name|getCipherSuite
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setCryptoProtocolVersion
argument_list|(
name|convert
argument_list|(
name|info
operator|.
name|getCryptoProtocolVersion
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setKey
argument_list|(
name|getByteString
argument_list|(
name|info
operator|.
name|getEncryptedDataEncryptionKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setIv
argument_list|(
name|getByteString
argument_list|(
name|info
operator|.
name|getIV
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setEzKeyVersionName
argument_list|(
name|info
operator|.
name|getEzKeyVersionName
argument_list|()
argument_list|)
operator|.
name|setKeyName
argument_list|(
name|info
operator|.
name|getKeyName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|convert (FileEncryptionInfoProto proto)
specifier|public
specifier|static
name|FileEncryptionInfo
name|convert
parameter_list|(
name|FileEncryptionInfoProto
name|proto
parameter_list|)
block|{
if|if
condition|(
name|proto
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|CipherSuite
name|suite
init|=
name|convert
argument_list|(
name|proto
operator|.
name|getSuite
argument_list|()
argument_list|)
decl_stmt|;
name|CryptoProtocolVersion
name|version
init|=
name|convert
argument_list|(
name|proto
operator|.
name|getCryptoProtocolVersion
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|key
init|=
name|proto
operator|.
name|getKey
argument_list|()
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|byte
index|[]
name|iv
init|=
name|proto
operator|.
name|getIv
argument_list|()
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|String
name|ezKeyVersionName
init|=
name|proto
operator|.
name|getEzKeyVersionName
argument_list|()
decl_stmt|;
name|String
name|keyName
init|=
name|proto
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
return|return
operator|new
name|FileEncryptionInfo
argument_list|(
name|suite
argument_list|,
name|version
argument_list|,
name|key
argument_list|,
name|iv
argument_list|,
name|keyName
argument_list|,
name|ezKeyVersionName
argument_list|)
return|;
block|}
DECL|method|convert (CipherSuiteProto proto)
specifier|public
specifier|static
name|CipherSuite
name|convert
parameter_list|(
name|CipherSuiteProto
name|proto
parameter_list|)
block|{
switch|switch
condition|(
name|proto
condition|)
block|{
case|case
name|AES_CTR_NOPADDING
case|:
return|return
name|CipherSuite
operator|.
name|AES_CTR_NOPADDING
return|;
default|default:
comment|// Set to UNKNOWN and stash the unknown enum value
name|CipherSuite
name|suite
init|=
name|CipherSuite
operator|.
name|UNKNOWN
decl_stmt|;
name|suite
operator|.
name|setUnknownValue
argument_list|(
name|proto
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|suite
return|;
block|}
block|}
DECL|method|convert (CipherSuite suite)
specifier|public
specifier|static
name|CipherSuiteProto
name|convert
parameter_list|(
name|CipherSuite
name|suite
parameter_list|)
block|{
switch|switch
condition|(
name|suite
condition|)
block|{
case|case
name|UNKNOWN
case|:
return|return
name|CipherSuiteProto
operator|.
name|UNKNOWN
return|;
case|case
name|AES_CTR_NOPADDING
case|:
return|return
name|CipherSuiteProto
operator|.
name|AES_CTR_NOPADDING
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
DECL|method|convert ( CryptoProtocolVersion version)
specifier|public
specifier|static
name|CryptoProtocolVersionProto
name|convert
parameter_list|(
name|CryptoProtocolVersion
name|version
parameter_list|)
block|{
switch|switch
condition|(
name|version
condition|)
block|{
case|case
name|UNKNOWN
case|:
return|return
name|OzoneManagerProtocolProtos
operator|.
name|CryptoProtocolVersionProto
operator|.
name|UNKNOWN_PROTOCOL_VERSION
return|;
case|case
name|ENCRYPTION_ZONES
case|:
return|return
name|OzoneManagerProtocolProtos
operator|.
name|CryptoProtocolVersionProto
operator|.
name|ENCRYPTION_ZONES
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
DECL|method|convert ( CryptoProtocolVersionProto proto)
specifier|public
specifier|static
name|CryptoProtocolVersion
name|convert
parameter_list|(
name|CryptoProtocolVersionProto
name|proto
parameter_list|)
block|{
switch|switch
condition|(
name|proto
condition|)
block|{
case|case
name|ENCRYPTION_ZONES
case|:
return|return
name|CryptoProtocolVersion
operator|.
name|ENCRYPTION_ZONES
return|;
default|default:
comment|// Set to UNKNOWN and stash the unknown enum value
name|CryptoProtocolVersion
name|version
init|=
name|CryptoProtocolVersion
operator|.
name|UNKNOWN
decl_stmt|;
name|version
operator|.
name|setUnknownValue
argument_list|(
name|proto
operator|.
name|getNumber
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|version
return|;
block|}
block|}
block|}
end_class

end_unit

