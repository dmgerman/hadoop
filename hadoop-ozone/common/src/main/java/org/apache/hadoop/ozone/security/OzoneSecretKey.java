begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|security
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
name|ByteString
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyPair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivateKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PublicKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|EqualsBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|security
operator|.
name|x509
operator|.
name|SecurityConfig
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
name|security
operator|.
name|x509
operator|.
name|keys
operator|.
name|SecurityUtil
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
name|Writable
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
name|SecretKeyProto
import|;
end_import

begin_comment
comment|/**  * Wrapper class for Ozone/Hdds secret keys. Used in delegation tokens and block  * tokens.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|OzoneSecretKey
specifier|public
class|class
name|OzoneSecretKey
implements|implements
name|Writable
block|{
DECL|field|keyId
specifier|private
name|int
name|keyId
decl_stmt|;
DECL|field|expiryDate
specifier|private
name|long
name|expiryDate
decl_stmt|;
DECL|field|privateKey
specifier|private
name|PrivateKey
name|privateKey
decl_stmt|;
DECL|field|publicKey
specifier|private
name|PublicKey
name|publicKey
decl_stmt|;
DECL|field|maxKeyLen
specifier|private
name|int
name|maxKeyLen
decl_stmt|;
DECL|field|securityConfig
specifier|private
name|SecurityConfig
name|securityConfig
decl_stmt|;
DECL|method|OzoneSecretKey (int keyId, long expiryDate, KeyPair keyPair, int maxKeyLen)
specifier|public
name|OzoneSecretKey
parameter_list|(
name|int
name|keyId
parameter_list|,
name|long
name|expiryDate
parameter_list|,
name|KeyPair
name|keyPair
parameter_list|,
name|int
name|maxKeyLen
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|keyId
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyId
operator|=
name|keyId
expr_stmt|;
name|this
operator|.
name|expiryDate
operator|=
name|expiryDate
expr_stmt|;
name|byte
index|[]
name|encodedKey
init|=
name|keyPair
operator|.
name|getPrivate
argument_list|()
operator|.
name|getEncoded
argument_list|()
decl_stmt|;
name|this
operator|.
name|maxKeyLen
operator|=
name|maxKeyLen
expr_stmt|;
if|if
condition|(
name|encodedKey
operator|.
name|length
operator|>
name|maxKeyLen
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"can't create "
operator|+
name|encodedKey
operator|.
name|length
operator|+
literal|" byte long DelegationKey."
argument_list|)
throw|;
block|}
name|this
operator|.
name|privateKey
operator|=
name|keyPair
operator|.
name|getPrivate
argument_list|()
expr_stmt|;
name|this
operator|.
name|publicKey
operator|=
name|keyPair
operator|.
name|getPublic
argument_list|()
expr_stmt|;
block|}
comment|/*    * Create new instance using default signature algorithm and provider.    * */
DECL|method|OzoneSecretKey (int keyId, long expiryDate, byte[] pvtKey, byte[] publicKey, int maxKeyLen)
specifier|public
name|OzoneSecretKey
parameter_list|(
name|int
name|keyId
parameter_list|,
name|long
name|expiryDate
parameter_list|,
name|byte
index|[]
name|pvtKey
parameter_list|,
name|byte
index|[]
name|publicKey
parameter_list|,
name|int
name|maxKeyLen
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pvtKey
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|publicKey
argument_list|)
expr_stmt|;
name|this
operator|.
name|securityConfig
operator|=
operator|new
name|SecurityConfig
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyId
operator|=
name|keyId
expr_stmt|;
name|this
operator|.
name|expiryDate
operator|=
name|expiryDate
expr_stmt|;
name|this
operator|.
name|maxKeyLen
operator|=
name|maxKeyLen
expr_stmt|;
if|if
condition|(
name|pvtKey
operator|.
name|length
operator|>
name|maxKeyLen
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"can't create "
operator|+
name|pvtKey
operator|.
name|length
operator|+
literal|" byte long DelegationKey. Max allowed length is "
operator|+
name|maxKeyLen
argument_list|)
throw|;
block|}
name|this
operator|.
name|privateKey
operator|=
name|SecurityUtil
operator|.
name|getPrivateKey
argument_list|(
name|pvtKey
argument_list|,
name|securityConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|publicKey
operator|=
name|SecurityUtil
operator|.
name|getPublicKey
argument_list|(
name|publicKey
argument_list|,
name|securityConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|getKeyId ()
specifier|public
name|int
name|getKeyId
parameter_list|()
block|{
return|return
name|keyId
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
DECL|method|getPrivateKey ()
specifier|public
name|PrivateKey
name|getPrivateKey
parameter_list|()
block|{
return|return
name|privateKey
return|;
block|}
DECL|method|getPublicKey ()
specifier|public
name|PublicKey
name|getPublicKey
parameter_list|()
block|{
return|return
name|publicKey
return|;
block|}
DECL|method|getMaxKeyLen ()
specifier|public
name|int
name|getMaxKeyLen
parameter_list|()
block|{
return|return
name|maxKeyLen
return|;
block|}
DECL|method|getEncodedPrivateKey ()
specifier|public
name|byte
index|[]
name|getEncodedPrivateKey
parameter_list|()
block|{
return|return
name|privateKey
operator|.
name|getEncoded
argument_list|()
return|;
block|}
DECL|method|getEncodedPubliceKey ()
specifier|public
name|byte
index|[]
name|getEncodedPubliceKey
parameter_list|()
block|{
return|return
name|publicKey
operator|.
name|getEncoded
argument_list|()
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
name|expiryDate
operator|=
name|expiryDate
expr_stmt|;
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
name|SecretKeyProto
name|token
init|=
name|SecretKeyProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKeyId
argument_list|(
name|getKeyId
argument_list|()
argument_list|)
operator|.
name|setExpiryDate
argument_list|(
name|getExpiryDate
argument_list|()
argument_list|)
operator|.
name|setPrivateKeyBytes
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|getEncodedPrivateKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setPublicKeyBytes
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|getEncodedPubliceKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxKeyLen
argument_list|(
name|getMaxKeyLen
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|token
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|SecretKeyProto
name|secretKey
init|=
name|SecretKeyProto
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
name|secretKey
operator|.
name|getExpiryDate
argument_list|()
expr_stmt|;
name|keyId
operator|=
name|secretKey
operator|.
name|getKeyId
argument_list|()
expr_stmt|;
name|privateKey
operator|=
name|SecurityUtil
operator|.
name|getPrivateKey
argument_list|(
name|secretKey
operator|.
name|getPrivateKeyBytes
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|securityConfig
argument_list|)
expr_stmt|;
name|publicKey
operator|=
name|SecurityUtil
operator|.
name|getPublicKey
argument_list|(
name|secretKey
operator|.
name|getPublicKeyBytes
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|securityConfig
argument_list|)
expr_stmt|;
name|maxKeyLen
operator|=
name|secretKey
operator|.
name|getMaxKeyLen
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|HashCodeBuilder
name|hashCodeBuilder
init|=
operator|new
name|HashCodeBuilder
argument_list|(
literal|537
argument_list|,
literal|963
argument_list|)
decl_stmt|;
name|hashCodeBuilder
operator|.
name|append
argument_list|(
name|getExpiryDate
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getKeyId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getEncodedPrivateKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|getEncodedPubliceKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|hashCodeBuilder
operator|.
name|build
argument_list|()
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
name|OzoneSecretKey
condition|)
block|{
name|OzoneSecretKey
name|that
init|=
operator|(
name|OzoneSecretKey
operator|)
name|obj
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|.
name|keyId
argument_list|,
name|that
operator|.
name|keyId
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|expiryDate
argument_list|,
name|that
operator|.
name|expiryDate
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|privateKey
argument_list|,
name|that
operator|.
name|privateKey
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|publicKey
argument_list|,
name|that
operator|.
name|publicKey
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Reads protobuf encoded input stream to construct {@link OzoneSecretKey}.    */
DECL|method|readProtoBuf (DataInput in)
specifier|static
name|OzoneSecretKey
name|readProtoBuf
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|SecretKeyProto
name|key
init|=
name|SecretKeyProto
operator|.
name|parseFrom
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
decl_stmt|;
return|return
operator|new
name|OzoneSecretKey
argument_list|(
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key
operator|.
name|getExpiryDate
argument_list|()
argument_list|,
name|key
operator|.
name|getPrivateKeyBytes
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|key
operator|.
name|getPublicKeyBytes
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|key
operator|.
name|getMaxKeyLen
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Reads protobuf encoded input stream to construct {@link OzoneSecretKey}.    */
DECL|method|readProtoBuf (byte[] identifier)
specifier|static
name|OzoneSecretKey
name|readProtoBuf
parameter_list|(
name|byte
index|[]
name|identifier
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|identifier
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|readProtoBuf
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
end_class

end_unit

