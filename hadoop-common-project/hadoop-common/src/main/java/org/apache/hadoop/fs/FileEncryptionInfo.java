begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|codec
operator|.
name|binary
operator|.
name|Hex
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * FileEncryptionInfo encapsulates all the encryption-related information for  * an encrypted file.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileEncryptionInfo
specifier|public
class|class
name|FileEncryptionInfo
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0x156abe03
decl_stmt|;
DECL|field|cipherSuite
specifier|private
specifier|final
name|CipherSuite
name|cipherSuite
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|CryptoProtocolVersion
name|version
decl_stmt|;
DECL|field|edek
specifier|private
specifier|final
name|byte
index|[]
name|edek
decl_stmt|;
DECL|field|iv
specifier|private
specifier|final
name|byte
index|[]
name|iv
decl_stmt|;
DECL|field|keyName
specifier|private
specifier|final
name|String
name|keyName
decl_stmt|;
DECL|field|ezKeyVersionName
specifier|private
specifier|final
name|String
name|ezKeyVersionName
decl_stmt|;
comment|/**    * Create a FileEncryptionInfo.    *    * @param suite CipherSuite used to encrypt the file    * @param edek encrypted data encryption key (EDEK) of the file    * @param iv initialization vector (IV) used to encrypt the file    * @param keyName name of the key used for the encryption zone    * @param ezKeyVersionName name of the KeyVersion used to encrypt the    *                         encrypted data encryption key.    */
DECL|method|FileEncryptionInfo (final CipherSuite suite, final CryptoProtocolVersion version, final byte[] edek, final byte[] iv, final String keyName, final String ezKeyVersionName)
specifier|public
name|FileEncryptionInfo
parameter_list|(
specifier|final
name|CipherSuite
name|suite
parameter_list|,
specifier|final
name|CryptoProtocolVersion
name|version
parameter_list|,
specifier|final
name|byte
index|[]
name|edek
parameter_list|,
specifier|final
name|byte
index|[]
name|iv
parameter_list|,
specifier|final
name|String
name|keyName
parameter_list|,
specifier|final
name|String
name|ezKeyVersionName
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|suite
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|edek
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|iv
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|ezKeyVersionName
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|iv
operator|.
name|length
operator|==
name|suite
operator|.
name|getAlgorithmBlockSize
argument_list|()
argument_list|,
literal|"Unexpected IV length"
argument_list|)
expr_stmt|;
name|this
operator|.
name|cipherSuite
operator|=
name|suite
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|edek
operator|=
name|edek
expr_stmt|;
name|this
operator|.
name|iv
operator|=
name|iv
expr_stmt|;
name|this
operator|.
name|keyName
operator|=
name|keyName
expr_stmt|;
name|this
operator|.
name|ezKeyVersionName
operator|=
name|ezKeyVersionName
expr_stmt|;
block|}
comment|/**    * @return {@link org.apache.hadoop.crypto.CipherSuite} used to encrypt    * the file.    */
DECL|method|getCipherSuite ()
specifier|public
name|CipherSuite
name|getCipherSuite
parameter_list|()
block|{
return|return
name|cipherSuite
return|;
block|}
comment|/**    * @return {@link org.apache.hadoop.crypto.CryptoProtocolVersion} to use    * to access the file.    */
DECL|method|getCryptoProtocolVersion ()
specifier|public
name|CryptoProtocolVersion
name|getCryptoProtocolVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**    * @return encrypted data encryption key (EDEK) for the file    */
DECL|method|getEncryptedDataEncryptionKey ()
specifier|public
name|byte
index|[]
name|getEncryptedDataEncryptionKey
parameter_list|()
block|{
return|return
name|edek
return|;
block|}
comment|/**    * @return initialization vector (IV) for the cipher used to encrypt the file    */
DECL|method|getIV ()
specifier|public
name|byte
index|[]
name|getIV
parameter_list|()
block|{
return|return
name|iv
return|;
block|}
comment|/**    * @return name of the encryption zone key.    */
DECL|method|getKeyName ()
specifier|public
name|String
name|getKeyName
parameter_list|()
block|{
return|return
name|keyName
return|;
block|}
comment|/**    * @return name of the encryption zone KeyVersion used to encrypt the    * encrypted data encryption key (EDEK).    */
DECL|method|getEzKeyVersionName ()
specifier|public
name|String
name|getEzKeyVersionName
parameter_list|()
block|{
return|return
name|ezKeyVersionName
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"cipherSuite: "
operator|+
name|cipherSuite
argument_list|)
operator|.
name|append
argument_list|(
literal|", cryptoProtocolVersion: "
operator|+
name|version
argument_list|)
operator|.
name|append
argument_list|(
literal|", edek: "
operator|+
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|edek
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", iv: "
operator|+
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|iv
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", keyName: "
operator|+
name|keyName
argument_list|)
operator|.
name|append
argument_list|(
literal|", ezKeyVersionName: "
operator|+
name|ezKeyVersionName
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * A frozen version of {@link #toString()} to be backward compatible.    * When backward compatibility is not needed, use {@link #toString()}, which    * provides more info and is supposed to evolve.    * Don't change this method except for major revisions.    *    * NOTE:    * Currently this method is used by CLI for backward compatibility.    */
DECL|method|toStringStable ()
specifier|public
name|String
name|toStringStable
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
literal|"cipherSuite: "
operator|+
name|cipherSuite
argument_list|)
operator|.
name|append
argument_list|(
literal|", cryptoProtocolVersion: "
operator|+
name|version
argument_list|)
operator|.
name|append
argument_list|(
literal|", edek: "
operator|+
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|edek
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", iv: "
operator|+
name|Hex
operator|.
name|encodeHexString
argument_list|(
name|iv
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", keyName: "
operator|+
name|keyName
argument_list|)
operator|.
name|append
argument_list|(
literal|", ezKeyVersionName: "
operator|+
name|ezKeyVersionName
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

