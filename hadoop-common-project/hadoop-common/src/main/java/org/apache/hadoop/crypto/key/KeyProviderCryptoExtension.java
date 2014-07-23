begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
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
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|Cipher
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|IvParameterSpec
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
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
name|base
operator|.
name|Preconditions
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

begin_comment
comment|/**  * A KeyProvider with Cryptographic Extensions specifically for generating  * and decrypting encrypted encryption keys.  *   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|KeyProviderCryptoExtension
specifier|public
class|class
name|KeyProviderCryptoExtension
extends|extends
name|KeyProviderExtension
argument_list|<
name|KeyProviderCryptoExtension
operator|.
name|CryptoExtension
argument_list|>
block|{
comment|/**    * Designates an encrypted encryption key, or EEK.    */
DECL|field|EEK
specifier|public
specifier|static
specifier|final
name|String
name|EEK
init|=
literal|"EEK"
decl_stmt|;
comment|/**    * Designates a decrypted encrypted encryption key, that is, an encryption key    * (EK).    */
DECL|field|EK
specifier|public
specifier|static
specifier|final
name|String
name|EK
init|=
literal|"EK"
decl_stmt|;
comment|/**    * An encrypted encryption key (EEK) and related information. An EEK must be    * decrypted using the key's encryption key before it can be used.    */
DECL|class|EncryptedKeyVersion
specifier|public
specifier|static
class|class
name|EncryptedKeyVersion
block|{
DECL|field|encryptionKeyName
specifier|private
name|String
name|encryptionKeyName
decl_stmt|;
DECL|field|encryptionKeyVersionName
specifier|private
name|String
name|encryptionKeyVersionName
decl_stmt|;
DECL|field|encryptedKeyIv
specifier|private
name|byte
index|[]
name|encryptedKeyIv
decl_stmt|;
DECL|field|encryptedKeyVersion
specifier|private
name|KeyVersion
name|encryptedKeyVersion
decl_stmt|;
comment|/**      * Create a new EncryptedKeyVersion.      *      * @param keyName                  Name of the encryption key used to      *                                 encrypt the encrypted key.      * @param encryptionKeyVersionName Version name of the encryption key used      *                                 to encrypt the encrypted key.      * @param encryptedKeyIv           Initialization vector of the encrypted      *                                 key. The IV of the encryption key used to      *                                 encrypt the encrypted key is derived from      *                                 this IV.      * @param encryptedKeyVersion      The encrypted encryption key version.      */
DECL|method|EncryptedKeyVersion (String keyName, String encryptionKeyVersionName, byte[] encryptedKeyIv, KeyVersion encryptedKeyVersion)
specifier|protected
name|EncryptedKeyVersion
parameter_list|(
name|String
name|keyName
parameter_list|,
name|String
name|encryptionKeyVersionName
parameter_list|,
name|byte
index|[]
name|encryptedKeyIv
parameter_list|,
name|KeyVersion
name|encryptedKeyVersion
parameter_list|)
block|{
name|this
operator|.
name|encryptionKeyName
operator|=
name|keyName
expr_stmt|;
name|this
operator|.
name|encryptionKeyVersionName
operator|=
name|encryptionKeyVersionName
expr_stmt|;
name|this
operator|.
name|encryptedKeyIv
operator|=
name|encryptedKeyIv
expr_stmt|;
name|this
operator|.
name|encryptedKeyVersion
operator|=
name|encryptedKeyVersion
expr_stmt|;
block|}
comment|/**      * @return Name of the encryption key used to encrypt the encrypted key.      */
DECL|method|getEncryptionKeyName ()
specifier|public
name|String
name|getEncryptionKeyName
parameter_list|()
block|{
return|return
name|encryptionKeyName
return|;
block|}
comment|/**      * @return Version name of the encryption key used to encrypt the encrypted      * key.      */
DECL|method|getEncryptionKeyVersionName ()
specifier|public
name|String
name|getEncryptionKeyVersionName
parameter_list|()
block|{
return|return
name|encryptionKeyVersionName
return|;
block|}
comment|/**      * @return Initialization vector of the encrypted key. The IV of the      * encryption key used to encrypt the encrypted key is derived from this      * IV.      */
DECL|method|getEncryptedKeyIv ()
specifier|public
name|byte
index|[]
name|getEncryptedKeyIv
parameter_list|()
block|{
return|return
name|encryptedKeyIv
return|;
block|}
comment|/**      * @return The encrypted encryption key version.      */
DECL|method|getEncryptedKeyVersion ()
specifier|public
name|KeyVersion
name|getEncryptedKeyVersion
parameter_list|()
block|{
return|return
name|encryptedKeyVersion
return|;
block|}
comment|/**      * Derive the initialization vector (IV) for the encryption key from the IV      * of the encrypted key. This derived IV is used with the encryption key to      * decrypt the encrypted key.      *<p/>      * The alternative to this is using the same IV for both the encryption key      * and the encrypted key. Even a simple symmetric transformation like this      * improves security by avoiding IV re-use. IVs will also be fairly unique      * among different EEKs.      *      * @param encryptedKeyIV of the encrypted key (i.e. {@link      * #getEncryptedKeyIv()})      * @return IV for the encryption key      */
DECL|method|deriveIV (byte[] encryptedKeyIV)
specifier|protected
specifier|static
name|byte
index|[]
name|deriveIV
parameter_list|(
name|byte
index|[]
name|encryptedKeyIV
parameter_list|)
block|{
name|byte
index|[]
name|rIv
init|=
operator|new
name|byte
index|[
name|encryptedKeyIV
operator|.
name|length
index|]
decl_stmt|;
comment|// Do a simple XOR transformation to flip all the bits
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|encryptedKeyIV
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|rIv
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|encryptedKeyIV
index|[
name|i
index|]
operator|^
literal|0xff
argument_list|)
expr_stmt|;
block|}
return|return
name|rIv
return|;
block|}
block|}
comment|/**    * CryptoExtension is a type of Extension that exposes methods to generate    * EncryptedKeys and to decrypt the same.    */
DECL|interface|CryptoExtension
specifier|public
interface|interface
name|CryptoExtension
extends|extends
name|KeyProviderExtension
operator|.
name|Extension
block|{
comment|/**      * Calls to this method allows the underlying KeyProvider to warm-up any      * implementation specific caches used to store the Encrypted Keys.      * @param keyNames Array of Key Names      */
DECL|method|warmUpEncryptedKeys (String... keyNames)
specifier|public
name|void
name|warmUpEncryptedKeys
parameter_list|(
name|String
modifier|...
name|keyNames
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Generates a key material and encrypts it using the given key version name      * and initialization vector. The generated key material is of the same      * length as the<code>KeyVersion</code> material of the latest key version      * of the key and is encrypted using the same cipher.      *<p/>      * NOTE: The generated key is not stored by the<code>KeyProvider</code>      *       * @param encryptionKeyName      *          The latest KeyVersion of this key's material will be encrypted.      * @return EncryptedKeyVersion with the generated key material, the version      *         name is 'EEK' (for Encrypted Encryption Key)      * @throws IOException      *           thrown if the key material could not be generated      * @throws GeneralSecurityException      *           thrown if the key material could not be encrypted because of a      *           cryptographic issue.      */
DECL|method|generateEncryptedKey ( String encryptionKeyName)
specifier|public
name|EncryptedKeyVersion
name|generateEncryptedKey
parameter_list|(
name|String
name|encryptionKeyName
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
function_decl|;
comment|/**      * Decrypts an encrypted byte[] key material using the given a key version      * name and initialization vector.      *       * @param encryptedKeyVersion      *          contains keyVersionName and IV to decrypt the encrypted key      *          material      * @return a KeyVersion with the decrypted key material, the version name is      *         'EK' (For Encryption Key)      * @throws IOException      *           thrown if the key material could not be decrypted      * @throws GeneralSecurityException      *           thrown if the key material could not be decrypted because of a      *           cryptographic issue.      */
DECL|method|decryptEncryptedKey ( EncryptedKeyVersion encryptedKeyVersion)
specifier|public
name|KeyVersion
name|decryptEncryptedKey
parameter_list|(
name|EncryptedKeyVersion
name|encryptedKeyVersion
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
function_decl|;
block|}
DECL|class|DefaultCryptoExtension
specifier|private
specifier|static
class|class
name|DefaultCryptoExtension
implements|implements
name|CryptoExtension
block|{
DECL|field|keyProvider
specifier|private
specifier|final
name|KeyProvider
name|keyProvider
decl_stmt|;
DECL|method|DefaultCryptoExtension (KeyProvider keyProvider)
specifier|private
name|DefaultCryptoExtension
parameter_list|(
name|KeyProvider
name|keyProvider
parameter_list|)
block|{
name|this
operator|.
name|keyProvider
operator|=
name|keyProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|generateEncryptedKey (String encryptionKeyName)
specifier|public
name|EncryptedKeyVersion
name|generateEncryptedKey
parameter_list|(
name|String
name|encryptionKeyName
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
comment|// Fetch the encryption key
name|KeyVersion
name|encryptionKey
init|=
name|keyProvider
operator|.
name|getCurrentKey
argument_list|(
name|encryptionKeyName
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|encryptionKey
argument_list|,
literal|"No KeyVersion exists for key '%s' "
argument_list|,
name|encryptionKeyName
argument_list|)
expr_stmt|;
comment|// Generate random bytes for new key and IV
name|Cipher
name|cipher
init|=
name|Cipher
operator|.
name|getInstance
argument_list|(
literal|"AES/CTR/NoPadding"
argument_list|)
decl_stmt|;
name|SecureRandom
name|random
init|=
name|SecureRandom
operator|.
name|getInstance
argument_list|(
literal|"SHA1PRNG"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|newKey
init|=
operator|new
name|byte
index|[
name|encryptionKey
operator|.
name|getMaterial
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|newKey
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|iv
init|=
name|random
operator|.
name|generateSeed
argument_list|(
name|cipher
operator|.
name|getBlockSize
argument_list|()
argument_list|)
decl_stmt|;
comment|// Encryption key IV is derived from new key's IV
specifier|final
name|byte
index|[]
name|encryptionIV
init|=
name|EncryptedKeyVersion
operator|.
name|deriveIV
argument_list|(
name|iv
argument_list|)
decl_stmt|;
comment|// Encrypt the new key
name|cipher
operator|.
name|init
argument_list|(
name|Cipher
operator|.
name|ENCRYPT_MODE
argument_list|,
operator|new
name|SecretKeySpec
argument_list|(
name|encryptionKey
operator|.
name|getMaterial
argument_list|()
argument_list|,
literal|"AES"
argument_list|)
argument_list|,
operator|new
name|IvParameterSpec
argument_list|(
name|encryptionIV
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|encryptedKey
init|=
name|cipher
operator|.
name|doFinal
argument_list|(
name|newKey
argument_list|)
decl_stmt|;
return|return
operator|new
name|EncryptedKeyVersion
argument_list|(
name|encryptionKeyName
argument_list|,
name|encryptionKey
operator|.
name|getVersionName
argument_list|()
argument_list|,
name|iv
argument_list|,
operator|new
name|KeyVersion
argument_list|(
name|encryptionKey
operator|.
name|getName
argument_list|()
argument_list|,
name|EEK
argument_list|,
name|encryptedKey
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|decryptEncryptedKey ( EncryptedKeyVersion encryptedKeyVersion)
specifier|public
name|KeyVersion
name|decryptEncryptedKey
parameter_list|(
name|EncryptedKeyVersion
name|encryptedKeyVersion
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
comment|// Fetch the encryption key material
specifier|final
name|String
name|encryptionKeyVersionName
init|=
name|encryptedKeyVersion
operator|.
name|getEncryptionKeyVersionName
argument_list|()
decl_stmt|;
specifier|final
name|KeyVersion
name|encryptionKey
init|=
name|keyProvider
operator|.
name|getKeyVersion
argument_list|(
name|encryptionKeyVersionName
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|encryptionKey
argument_list|,
literal|"KeyVersion name '%s' does not exist"
argument_list|,
name|encryptionKeyVersionName
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|encryptionKeyMaterial
init|=
name|encryptionKey
operator|.
name|getMaterial
argument_list|()
decl_stmt|;
comment|// Encryption key IV is determined from encrypted key's IV
specifier|final
name|byte
index|[]
name|encryptionIV
init|=
name|EncryptedKeyVersion
operator|.
name|deriveIV
argument_list|(
name|encryptedKeyVersion
operator|.
name|getEncryptedKeyIv
argument_list|()
argument_list|)
decl_stmt|;
comment|// Init the cipher with encryption key parameters
name|Cipher
name|cipher
init|=
name|Cipher
operator|.
name|getInstance
argument_list|(
literal|"AES/CTR/NoPadding"
argument_list|)
decl_stmt|;
name|cipher
operator|.
name|init
argument_list|(
name|Cipher
operator|.
name|DECRYPT_MODE
argument_list|,
operator|new
name|SecretKeySpec
argument_list|(
name|encryptionKeyMaterial
argument_list|,
literal|"AES"
argument_list|)
argument_list|,
operator|new
name|IvParameterSpec
argument_list|(
name|encryptionIV
argument_list|)
argument_list|)
expr_stmt|;
comment|// Decrypt the encrypted key
specifier|final
name|KeyVersion
name|encryptedKV
init|=
name|encryptedKeyVersion
operator|.
name|getEncryptedKeyVersion
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|decryptedKey
init|=
name|cipher
operator|.
name|doFinal
argument_list|(
name|encryptedKV
operator|.
name|getMaterial
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|KeyVersion
argument_list|(
name|encryptionKey
operator|.
name|getName
argument_list|()
argument_list|,
name|EK
argument_list|,
name|decryptedKey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|warmUpEncryptedKeys (String... keyNames)
specifier|public
name|void
name|warmUpEncryptedKeys
parameter_list|(
name|String
modifier|...
name|keyNames
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NO-OP since the default version does not cache any keys
block|}
block|}
comment|/**    * This constructor is to be used by sub classes that provide    * delegating/proxying functionality to the {@link KeyProviderCryptoExtension}    * @param keyProvider    * @param extension    */
DECL|method|KeyProviderCryptoExtension (KeyProvider keyProvider, CryptoExtension extension)
specifier|protected
name|KeyProviderCryptoExtension
parameter_list|(
name|KeyProvider
name|keyProvider
parameter_list|,
name|CryptoExtension
name|extension
parameter_list|)
block|{
name|super
argument_list|(
name|keyProvider
argument_list|,
name|extension
argument_list|)
expr_stmt|;
block|}
comment|/**    * Notifies the Underlying CryptoExtension implementation to warm up any    * implementation specific caches for the specified KeyVersions    * @param keyNames Arrays of key Names    */
DECL|method|warmUpEncryptedKeys (String... keyNames)
specifier|public
name|void
name|warmUpEncryptedKeys
parameter_list|(
name|String
modifier|...
name|keyNames
parameter_list|)
throws|throws
name|IOException
block|{
name|getExtension
argument_list|()
operator|.
name|warmUpEncryptedKeys
argument_list|(
name|keyNames
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generates a key material and encrypts it using the given key version name    * and initialization vector. The generated key material is of the same    * length as the<code>KeyVersion</code> material and is encrypted using the    * same cipher.    *<p/>    * NOTE: The generated key is not stored by the<code>KeyProvider</code>    *    * @param encryptionKeyName The latest KeyVersion of this key's material will    * be encrypted.    * @return EncryptedKeyVersion with the generated key material, the version    * name is 'EEK' (for Encrypted Encryption Key)    * @throws IOException thrown if the key material could not be generated    * @throws GeneralSecurityException thrown if the key material could not be     * encrypted because of a cryptographic issue.    */
DECL|method|generateEncryptedKey (String encryptionKeyName)
specifier|public
name|EncryptedKeyVersion
name|generateEncryptedKey
parameter_list|(
name|String
name|encryptionKeyName
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
return|return
name|getExtension
argument_list|()
operator|.
name|generateEncryptedKey
argument_list|(
name|encryptionKeyName
argument_list|)
return|;
block|}
comment|/**    * Decrypts an encrypted byte[] key material using the given a key version    * name and initialization vector.    *    * @param encryptedKey contains keyVersionName and IV to decrypt the encrypted     * key material    * @return a KeyVersion with the decrypted key material, the version name is    * 'EK' (For Encryption Key)    * @throws IOException thrown if the key material could not be decrypted    * @throws GeneralSecurityException thrown if the key material could not be     * decrypted because of a cryptographic issue.    */
DECL|method|decryptEncryptedKey (EncryptedKeyVersion encryptedKey)
specifier|public
name|KeyVersion
name|decryptEncryptedKey
parameter_list|(
name|EncryptedKeyVersion
name|encryptedKey
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
return|return
name|getExtension
argument_list|()
operator|.
name|decryptEncryptedKey
argument_list|(
name|encryptedKey
argument_list|)
return|;
block|}
comment|/**    * Creates a<code>KeyProviderCryptoExtension</code> using a given     * {@link KeyProvider}.    *<p/>    * If the given<code>KeyProvider</code> implements the     * {@link CryptoExtension} interface the<code>KeyProvider</code> itself    * will provide the extension functionality, otherwise a default extension    * implementation will be used.    *     * @param keyProvider<code>KeyProvider</code> to use to create the     *<code>KeyProviderCryptoExtension</code> extension.    * @return a<code>KeyProviderCryptoExtension</code> instance using the    * given<code>KeyProvider</code>.    */
DECL|method|createKeyProviderCryptoExtension ( KeyProvider keyProvider)
specifier|public
specifier|static
name|KeyProviderCryptoExtension
name|createKeyProviderCryptoExtension
parameter_list|(
name|KeyProvider
name|keyProvider
parameter_list|)
block|{
name|CryptoExtension
name|cryptoExtension
init|=
operator|(
name|keyProvider
operator|instanceof
name|CryptoExtension
operator|)
condition|?
operator|(
name|CryptoExtension
operator|)
name|keyProvider
else|:
operator|new
name|DefaultCryptoExtension
argument_list|(
name|keyProvider
argument_list|)
decl_stmt|;
return|return
operator|new
name|KeyProviderCryptoExtension
argument_list|(
name|keyProvider
argument_list|,
name|cryptoExtension
argument_list|)
return|;
block|}
block|}
end_class

end_unit

