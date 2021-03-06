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
name|IOException
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
name|ConcurrentHashMap
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
operator|.
name|AccessMode
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
name|SecretManager
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
name|fs
operator|.
name|StorageType
import|;
end_import

begin_comment
comment|/**  * Manages a {@link BlockTokenSecretManager} per block pool. Routes the requests  * given a block pool Id to corresponding {@link BlockTokenSecretManager}  */
end_comment

begin_class
DECL|class|BlockPoolTokenSecretManager
specifier|public
class|class
name|BlockPoolTokenSecretManager
extends|extends
name|SecretManager
argument_list|<
name|BlockTokenIdentifier
argument_list|>
block|{
DECL|field|map
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BlockTokenSecretManager
argument_list|>
name|map
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Add a block pool Id and corresponding {@link BlockTokenSecretManager} to map    * @param bpid block pool Id    * @param secretMgr {@link BlockTokenSecretManager}    */
DECL|method|addBlockPool (String bpid, BlockTokenSecretManager secretMgr)
specifier|public
name|void
name|addBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|,
name|BlockTokenSecretManager
name|secretMgr
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|bpid
argument_list|,
name|secretMgr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|get (String bpid)
specifier|public
name|BlockTokenSecretManager
name|get
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|BlockTokenSecretManager
name|secretMgr
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|secretMgr
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Block pool "
operator|+
name|bpid
operator|+
literal|" is not found"
argument_list|)
throw|;
block|}
return|return
name|secretMgr
return|;
block|}
DECL|method|isBlockPoolRegistered (String bpid)
specifier|public
name|boolean
name|isBlockPoolRegistered
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|bpid
argument_list|)
return|;
block|}
comment|/** Return an empty BlockTokenIdentifer */
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|BlockTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|BlockTokenIdentifier
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createPassword (BlockTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|createPassword
parameter_list|(
name|BlockTokenIdentifier
name|identifier
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|identifier
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|createPassword
argument_list|(
name|identifier
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|retrievePassword (BlockTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|BlockTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|InvalidToken
block|{
return|return
name|get
argument_list|(
name|identifier
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
return|;
block|}
comment|/**    * See {@link BlockTokenSecretManager#checkAccess(BlockTokenIdentifier,    *                String, ExtendedBlock, BlockTokenIdentifier.AccessMode,    *                StorageType[], String[])}    */
DECL|method|checkAccess (BlockTokenIdentifier id, String userId, ExtendedBlock block, AccessMode mode, StorageType[] storageTypes, String[] storageIds)
specifier|public
name|void
name|checkAccess
parameter_list|(
name|BlockTokenIdentifier
name|id
parameter_list|,
name|String
name|userId
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|AccessMode
name|mode
parameter_list|,
name|StorageType
index|[]
name|storageTypes
parameter_list|,
name|String
index|[]
name|storageIds
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|get
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|id
argument_list|,
name|userId
argument_list|,
name|block
argument_list|,
name|mode
argument_list|,
name|storageTypes
argument_list|,
name|storageIds
argument_list|)
expr_stmt|;
block|}
comment|/**    * See {@link BlockTokenSecretManager#checkAccess(BlockTokenIdentifier,    * String, ExtendedBlock, BlockTokenIdentifier.AccessMode,    * StorageType[])}    */
DECL|method|checkAccess (BlockTokenIdentifier id, String userId, ExtendedBlock block, AccessMode mode, StorageType[] storageTypes)
specifier|public
name|void
name|checkAccess
parameter_list|(
name|BlockTokenIdentifier
name|id
parameter_list|,
name|String
name|userId
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|AccessMode
name|mode
parameter_list|,
name|StorageType
index|[]
name|storageTypes
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|get
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|id
argument_list|,
name|userId
argument_list|,
name|block
argument_list|,
name|mode
argument_list|,
name|storageTypes
argument_list|)
expr_stmt|;
block|}
comment|/**    * See {@link BlockTokenSecretManager#checkAccess(BlockTokenIdentifier,    * String, ExtendedBlock, BlockTokenIdentifier.AccessMode)}.    */
DECL|method|checkAccess (BlockTokenIdentifier id, String userId, ExtendedBlock block, AccessMode mode)
specifier|public
name|void
name|checkAccess
parameter_list|(
name|BlockTokenIdentifier
name|id
parameter_list|,
name|String
name|userId
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|AccessMode
name|mode
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|get
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|id
argument_list|,
name|userId
argument_list|,
name|block
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/**    * See {@link BlockTokenSecretManager#checkAccess(Token, String,    *                ExtendedBlock, BlockTokenIdentifier.AccessMode)}.    */
DECL|method|checkAccess (Token<BlockTokenIdentifier> token, String userId, ExtendedBlock block, AccessMode mode)
specifier|public
name|void
name|checkAccess
parameter_list|(
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|userId
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|AccessMode
name|mode
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|get
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|token
argument_list|,
name|userId
argument_list|,
name|block
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/**    * See {@link BlockTokenSecretManager#checkAccess(Token, String,    *                ExtendedBlock, BlockTokenIdentifier.AccessMode,    *                StorageType[], String[])}    */
DECL|method|checkAccess (Token<BlockTokenIdentifier> token, String userId, ExtendedBlock block, AccessMode mode, StorageType[] storageTypes, String[] storageIds)
specifier|public
name|void
name|checkAccess
parameter_list|(
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|userId
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|AccessMode
name|mode
parameter_list|,
name|StorageType
index|[]
name|storageTypes
parameter_list|,
name|String
index|[]
name|storageIds
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|get
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|checkAccess
argument_list|(
name|token
argument_list|,
name|userId
argument_list|,
name|block
argument_list|,
name|mode
argument_list|,
name|storageTypes
argument_list|,
name|storageIds
argument_list|)
expr_stmt|;
block|}
comment|/**    * See {@link BlockTokenSecretManager#addKeys(ExportedBlockKeys)}.    */
DECL|method|addKeys (String bpid, ExportedBlockKeys exportedKeys)
specifier|public
name|void
name|addKeys
parameter_list|(
name|String
name|bpid
parameter_list|,
name|ExportedBlockKeys
name|exportedKeys
parameter_list|)
throws|throws
name|IOException
block|{
name|get
argument_list|(
name|bpid
argument_list|)
operator|.
name|addKeys
argument_list|(
name|exportedKeys
argument_list|)
expr_stmt|;
block|}
comment|/**    * See {@link BlockTokenSecretManager#generateToken(ExtendedBlock, EnumSet,    *  StorageType[], String[])}.    */
DECL|method|generateToken (ExtendedBlock b, EnumSet<AccessMode> of, StorageType[] storageTypes, String[] storageIds)
specifier|public
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|generateToken
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|EnumSet
argument_list|<
name|AccessMode
argument_list|>
name|of
parameter_list|,
name|StorageType
index|[]
name|storageTypes
parameter_list|,
name|String
index|[]
name|storageIds
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|b
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
operator|.
name|generateToken
argument_list|(
name|b
argument_list|,
name|of
argument_list|,
name|storageTypes
argument_list|,
name|storageIds
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|clearAllKeysForTesting ()
specifier|public
name|void
name|clearAllKeysForTesting
parameter_list|()
block|{
for|for
control|(
name|BlockTokenSecretManager
name|btsm
range|:
name|map
operator|.
name|values
argument_list|()
control|)
block|{
name|btsm
operator|.
name|clearAllKeysForTesting
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|generateDataEncryptionKey (String blockPoolId)
specifier|public
name|DataEncryptionKey
name|generateDataEncryptionKey
parameter_list|(
name|String
name|blockPoolId
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|blockPoolId
argument_list|)
operator|.
name|generateDataEncryptionKey
argument_list|()
return|;
block|}
DECL|method|retrieveDataEncryptionKey (int keyId, String blockPoolId, byte[] nonce)
specifier|public
name|byte
index|[]
name|retrieveDataEncryptionKey
parameter_list|(
name|int
name|keyId
parameter_list|,
name|String
name|blockPoolId
parameter_list|,
name|byte
index|[]
name|nonce
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|blockPoolId
argument_list|)
operator|.
name|retrieveDataEncryptionKey
argument_list|(
name|keyId
argument_list|,
name|nonce
argument_list|)
return|;
block|}
block|}
end_class

end_unit

