begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key.kms.server
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
operator|.
name|kms
operator|.
name|server
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
name|NoSuchAlgorithmException
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|key
operator|.
name|KeyProvider
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
name|key
operator|.
name|KeyProviderCryptoExtension
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
name|authorize
operator|.
name|AuthorizationException
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|ImmutableMap
import|;
end_import

begin_comment
comment|/**  * A {@link KeyProvider} proxy that checks whether the current user derived via  * {@link UserGroupInformation}, is authorized to perform the following  * type of operations on a Key :  *<ol>  *<li>MANAGEMENT operations : createKey, rollNewVersion, deleteKey</li>  *<li>GENERATE_EEK operations : generateEncryptedKey, warmUpEncryptedKeys</li>  *<li>DECRYPT_EEK operation : decryptEncryptedKey</li>  *<li>READ operations : getKeyVersion, getKeyVersions, getMetadata,  * getKeysMetadata, getCurrentKey</li>  *</ol>  * The read operations (getCurrentKeyVersion / getMetadata) etc are not checked.  */
end_comment

begin_class
DECL|class|KeyAuthorizationKeyProvider
specifier|public
class|class
name|KeyAuthorizationKeyProvider
extends|extends
name|KeyProviderCryptoExtension
block|{
DECL|field|KEY_ACL
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ACL
init|=
literal|"key.acl."
decl_stmt|;
DECL|field|KEY_ACL_NAME
specifier|private
specifier|static
specifier|final
name|String
name|KEY_ACL_NAME
init|=
name|KEY_ACL
operator|+
literal|"name"
decl_stmt|;
DECL|enum|KeyOpType
specifier|public
enum|enum
name|KeyOpType
block|{
DECL|enumConstant|ALL
DECL|enumConstant|READ
DECL|enumConstant|MANAGEMENT
DECL|enumConstant|GENERATE_EEK
DECL|enumConstant|DECRYPT_EEK
name|ALL
block|,
name|READ
block|,
name|MANAGEMENT
block|,
name|GENERATE_EEK
block|,
name|DECRYPT_EEK
block|;   }
comment|/**    * Interface that needs to be implemented by a client of the    *<code>KeyAuthorizationKeyProvider</code>.    */
DECL|interface|KeyACLs
specifier|public
specifier|static
interface|interface
name|KeyACLs
block|{
comment|/**      * This is called by the KeyProvider to check if the given user is      * authorized to perform the specified operation on the given acl name.      * @param aclName name of the key ACL      * @param ugi User's UserGroupInformation      * @param opType Operation Type       * @return true if user has access to the aclName and opType else false      */
DECL|method|hasAccessToKey (String aclName, UserGroupInformation ugi, KeyOpType opType)
specifier|public
name|boolean
name|hasAccessToKey
parameter_list|(
name|String
name|aclName
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|KeyOpType
name|opType
parameter_list|)
function_decl|;
comment|/**      *       * @param aclName ACL name      * @param opType Operation Type      * @return true if AclName exists else false       */
DECL|method|isACLPresent (String aclName, KeyOpType opType)
specifier|public
name|boolean
name|isACLPresent
parameter_list|(
name|String
name|aclName
parameter_list|,
name|KeyOpType
name|opType
parameter_list|)
function_decl|;
block|}
DECL|field|provider
specifier|private
specifier|final
name|KeyProviderCryptoExtension
name|provider
decl_stmt|;
DECL|field|acls
specifier|private
specifier|final
name|KeyACLs
name|acls
decl_stmt|;
DECL|field|readLock
specifier|private
name|Lock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
decl_stmt|;
comment|/**    * The constructor takes a {@link KeyProviderCryptoExtension} and an    * implementation of<code>KeyACLs</code>. All calls are delegated to the    * provider keyProvider after authorization check (if required)    * @param keyProvider  the key provider    * @param acls the Key ACLs    */
DECL|method|KeyAuthorizationKeyProvider (KeyProviderCryptoExtension keyProvider, KeyACLs acls)
specifier|public
name|KeyAuthorizationKeyProvider
parameter_list|(
name|KeyProviderCryptoExtension
name|keyProvider
parameter_list|,
name|KeyACLs
name|acls
parameter_list|)
block|{
name|super
argument_list|(
name|keyProvider
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|provider
operator|=
name|keyProvider
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|acls
expr_stmt|;
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|readLock
operator|=
name|lock
operator|.
name|readLock
argument_list|()
expr_stmt|;
name|writeLock
operator|=
name|lock
operator|.
name|writeLock
argument_list|()
expr_stmt|;
block|}
comment|// This method first checks if "key.acl.name" attribute is present as an
comment|// attribute in the provider Options. If yes, use the aclName for any
comment|// subsequent access checks, else use the keyName as the aclName and set it
comment|// as the value of the "key.acl.name" in the key's metadata.
DECL|method|authorizeCreateKey (String keyName, Options options, UserGroupInformation ugi)
specifier|private
name|void
name|authorizeCreateKey
parameter_list|(
name|String
name|keyName
parameter_list|,
name|Options
name|options
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ugi
argument_list|,
literal|"UserGroupInformation cannot be null"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|options
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|String
name|aclName
init|=
name|attributes
operator|.
name|get
argument_list|(
name|KEY_ACL_NAME
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|aclName
argument_list|)
condition|)
block|{
if|if
condition|(
name|acls
operator|.
name|isACLPresent
argument_list|(
name|keyName
argument_list|,
name|KeyOpType
operator|.
name|MANAGEMENT
argument_list|)
condition|)
block|{
name|options
operator|.
name|setAttributes
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|builder
argument_list|()
operator|.
name|putAll
argument_list|(
name|attributes
argument_list|)
operator|.
name|put
argument_list|(
name|KEY_ACL_NAME
argument_list|,
name|keyName
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|success
operator|=
name|acls
operator|.
name|hasAccessToKey
argument_list|(
name|keyName
argument_list|,
name|ugi
argument_list|,
name|KeyOpType
operator|.
name|MANAGEMENT
argument_list|)
operator|||
name|acls
operator|.
name|hasAccessToKey
argument_list|(
name|keyName
argument_list|,
name|ugi
argument_list|,
name|KeyOpType
operator|.
name|ALL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|success
operator|=
name|acls
operator|.
name|isACLPresent
argument_list|(
name|aclName
argument_list|,
name|KeyOpType
operator|.
name|MANAGEMENT
argument_list|)
operator|&&
operator|(
name|acls
operator|.
name|hasAccessToKey
argument_list|(
name|aclName
argument_list|,
name|ugi
argument_list|,
name|KeyOpType
operator|.
name|MANAGEMENT
argument_list|)
operator|||
name|acls
operator|.
name|hasAccessToKey
argument_list|(
name|aclName
argument_list|,
name|ugi
argument_list|,
name|KeyOpType
operator|.
name|ALL
argument_list|)
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|success
condition|)
throw|throw
operator|new
name|AuthorizationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"User [%s] is not"
operator|+
literal|" authorized to create key !!"
argument_list|,
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|checkAccess (String aclName, UserGroupInformation ugi, KeyOpType opType)
specifier|private
name|void
name|checkAccess
parameter_list|(
name|String
name|aclName
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|,
name|KeyOpType
name|opType
parameter_list|)
throws|throws
name|AuthorizationException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|aclName
argument_list|,
literal|"Key ACL name cannot be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|ugi
argument_list|,
literal|"UserGroupInformation cannot be null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|acls
operator|.
name|isACLPresent
argument_list|(
name|aclName
argument_list|,
name|opType
argument_list|)
operator|&&
operator|(
name|acls
operator|.
name|hasAccessToKey
argument_list|(
name|aclName
argument_list|,
name|ugi
argument_list|,
name|opType
argument_list|)
operator|||
name|acls
operator|.
name|hasAccessToKey
argument_list|(
name|aclName
argument_list|,
name|ugi
argument_list|,
name|KeyOpType
operator|.
name|ALL
argument_list|)
operator|)
condition|)
block|{
return|return;
block|}
else|else
block|{
throw|throw
operator|new
name|AuthorizationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"User [%s] is not"
operator|+
literal|" authorized to perform [%s] on key with ACL name [%s]!!"
argument_list|,
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|opType
argument_list|,
name|aclName
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createKey (String name, Options options)
specifier|public
name|KeyVersion
name|createKey
parameter_list|(
name|String
name|name
parameter_list|,
name|Options
name|options
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|authorizeCreateKey
argument_list|(
name|name
argument_list|,
name|options
argument_list|,
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|createKey
argument_list|(
name|name
argument_list|,
name|options
argument_list|)
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createKey (String name, byte[] material, Options options)
specifier|public
name|KeyVersion
name|createKey
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|material
parameter_list|,
name|Options
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|authorizeCreateKey
argument_list|(
name|name
argument_list|,
name|options
argument_list|,
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|createKey
argument_list|(
name|name
argument_list|,
name|material
argument_list|,
name|options
argument_list|)
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollNewVersion (String name)
specifier|public
name|KeyVersion
name|rollNewVersion
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|MANAGEMENT
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|rollNewVersion
argument_list|(
name|name
argument_list|)
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteKey (String name)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|MANAGEMENT
argument_list|)
expr_stmt|;
name|provider
operator|.
name|deleteKey
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollNewVersion (String name, byte[] material)
specifier|public
name|KeyVersion
name|rollNewVersion
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|material
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|MANAGEMENT
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|rollNewVersion
argument_list|(
name|name
argument_list|,
name|material
argument_list|)
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|invalidateCache (String name)
specifier|public
name|void
name|invalidateCache
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|MANAGEMENT
argument_list|)
expr_stmt|;
name|provider
operator|.
name|invalidateCache
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|warmUpEncryptedKeys (String... names)
specifier|public
name|void
name|warmUpEncryptedKeys
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|GENERATE_EEK
argument_list|)
expr_stmt|;
block|}
name|provider
operator|.
name|warmUpEncryptedKeys
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doAccessCheck
argument_list|(
name|encryptionKeyName
argument_list|,
name|KeyOpType
operator|.
name|GENERATE_EEK
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|generateEncryptedKey
argument_list|(
name|encryptionKeyName
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|verifyKeyVersionBelongsToKey (EncryptedKeyVersion ekv)
specifier|private
name|void
name|verifyKeyVersionBelongsToKey
parameter_list|(
name|EncryptedKeyVersion
name|ekv
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|kn
init|=
name|ekv
operator|.
name|getEncryptionKeyName
argument_list|()
decl_stmt|;
name|String
name|kvn
init|=
name|ekv
operator|.
name|getEncryptionKeyVersionName
argument_list|()
decl_stmt|;
name|KeyVersion
name|kv
init|=
name|provider
operator|.
name|getKeyVersion
argument_list|(
name|kvn
argument_list|)
decl_stmt|;
if|if
condition|(
name|kv
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"'%s' not found"
argument_list|,
name|kvn
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|kv
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|kn
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"KeyVersion '%s' does not belong to the key '%s'"
argument_list|,
name|kvn
argument_list|,
name|kn
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|decryptEncryptedKey (EncryptedKeyVersion encryptedKeyVersion)
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
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|verifyKeyVersionBelongsToKey
argument_list|(
name|encryptedKeyVersion
argument_list|)
expr_stmt|;
name|doAccessCheck
argument_list|(
name|encryptedKeyVersion
operator|.
name|getEncryptionKeyName
argument_list|()
argument_list|,
name|KeyOpType
operator|.
name|DECRYPT_EEK
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|decryptEncryptedKey
argument_list|(
name|encryptedKeyVersion
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reencryptEncryptedKey (EncryptedKeyVersion ekv)
specifier|public
name|EncryptedKeyVersion
name|reencryptEncryptedKey
parameter_list|(
name|EncryptedKeyVersion
name|ekv
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|verifyKeyVersionBelongsToKey
argument_list|(
name|ekv
argument_list|)
expr_stmt|;
name|doAccessCheck
argument_list|(
name|ekv
operator|.
name|getEncryptionKeyName
argument_list|()
argument_list|,
name|KeyOpType
operator|.
name|GENERATE_EEK
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|reencryptEncryptedKey
argument_list|(
name|ekv
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|reencryptEncryptedKeys (List<EncryptedKeyVersion> ekvs)
specifier|public
name|void
name|reencryptEncryptedKeys
parameter_list|(
name|List
argument_list|<
name|EncryptedKeyVersion
argument_list|>
name|ekvs
parameter_list|)
throws|throws
name|IOException
throws|,
name|GeneralSecurityException
block|{
if|if
condition|(
name|ekvs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|EncryptedKeyVersion
name|ekv
range|:
name|ekvs
control|)
block|{
name|verifyKeyVersionBelongsToKey
argument_list|(
name|ekv
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|keyName
init|=
name|ekvs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEncryptionKeyName
argument_list|()
decl_stmt|;
name|doAccessCheck
argument_list|(
name|keyName
argument_list|,
name|KeyOpType
operator|.
name|GENERATE_EEK
argument_list|)
expr_stmt|;
name|provider
operator|.
name|reencryptEncryptedKeys
argument_list|(
name|ekvs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getKeyVersion (String versionName)
specifier|public
name|KeyVersion
name|getKeyVersion
parameter_list|(
name|String
name|versionName
parameter_list|)
throws|throws
name|IOException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|KeyVersion
name|keyVersion
init|=
name|provider
operator|.
name|getKeyVersion
argument_list|(
name|versionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyVersion
operator|!=
literal|null
condition|)
block|{
name|doAccessCheck
argument_list|(
name|keyVersion
operator|.
name|getName
argument_list|()
argument_list|,
name|KeyOpType
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
return|return
name|keyVersion
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getKeys ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getKeys
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|provider
operator|.
name|getKeys
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getKeyVersions (String name)
specifier|public
name|List
argument_list|<
name|KeyVersion
argument_list|>
name|getKeyVersions
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|READ
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|getKeyVersions
argument_list|(
name|name
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMetadata (String name)
specifier|public
name|Metadata
name|getMetadata
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|READ
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|getMetadata
argument_list|(
name|name
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getKeysMetadata (String... names)
specifier|public
name|Metadata
index|[]
name|getKeysMetadata
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
return|return
name|provider
operator|.
name|getKeysMetadata
argument_list|(
name|names
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCurrentKey (String name)
specifier|public
name|KeyVersion
name|getCurrentKey
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|doAccessCheck
argument_list|(
name|name
argument_list|,
name|KeyOpType
operator|.
name|READ
argument_list|)
expr_stmt|;
return|return
name|provider
operator|.
name|getCurrentKey
argument_list|(
name|name
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|provider
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isTransient ()
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
name|provider
operator|.
name|isTransient
argument_list|()
return|;
block|}
DECL|method|doAccessCheck (String keyName, KeyOpType opType)
specifier|private
name|void
name|doAccessCheck
parameter_list|(
name|String
name|keyName
parameter_list|,
name|KeyOpType
name|opType
parameter_list|)
throws|throws
name|IOException
block|{
name|Metadata
name|metadata
init|=
name|provider
operator|.
name|getMetadata
argument_list|(
name|keyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|metadata
operator|!=
literal|null
condition|)
block|{
name|String
name|aclName
init|=
name|metadata
operator|.
name|getAttributes
argument_list|()
operator|.
name|get
argument_list|(
name|KEY_ACL_NAME
argument_list|)
decl_stmt|;
name|checkAccess
argument_list|(
operator|(
name|aclName
operator|==
literal|null
operator|)
condition|?
name|keyName
else|:
name|aclName
argument_list|,
name|getUser
argument_list|()
argument_list|,
name|opType
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getUser ()
specifier|private
name|UserGroupInformation
name|getUser
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getKeyProvider ()
specifier|protected
name|KeyProvider
name|getKeyProvider
parameter_list|()
block|{
return|return
name|this
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
return|return
name|provider
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

