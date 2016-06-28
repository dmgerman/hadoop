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
name|ByteArrayInputStream
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
name|IOException
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|datatransfer
operator|.
name|InvalidEncryptionKeyException
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
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

begin_comment
comment|/**  * BlockTokenSecretManager can be instantiated in 2 modes, master mode  * and worker mode. Master can generate new block keys and export block  * keys to workers, while workers can only import and use block keys  * received from master. Both master and worker can generate and verify  * block tokens. Typically, master mode is used by NN and worker mode  * is used by DN.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockTokenSecretManager
specifier|public
class|class
name|BlockTokenSecretManager
extends|extends
name|SecretManager
argument_list|<
name|BlockTokenIdentifier
argument_list|>
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BlockTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DUMMY_TOKEN
specifier|public
specifier|static
specifier|final
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|DUMMY_TOKEN
init|=
operator|new
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|isMaster
specifier|private
specifier|final
name|boolean
name|isMaster
decl_stmt|;
comment|/**    * keyUpdateInterval is the interval that NN updates its block keys. It should    * be set long enough so that all live DN's and Balancer should have sync'ed    * their block keys with NN at least once during each interval.    */
DECL|field|keyUpdateInterval
specifier|private
name|long
name|keyUpdateInterval
decl_stmt|;
DECL|field|tokenLifetime
specifier|private
specifier|volatile
name|long
name|tokenLifetime
decl_stmt|;
DECL|field|serialNo
specifier|private
name|int
name|serialNo
decl_stmt|;
DECL|field|currentKey
specifier|private
name|BlockKey
name|currentKey
decl_stmt|;
DECL|field|nextKey
specifier|private
name|BlockKey
name|nextKey
decl_stmt|;
DECL|field|allKeys
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BlockKey
argument_list|>
name|allKeys
decl_stmt|;
DECL|field|blockPoolId
specifier|private
name|String
name|blockPoolId
decl_stmt|;
DECL|field|encryptionAlgorithm
specifier|private
specifier|final
name|String
name|encryptionAlgorithm
decl_stmt|;
DECL|field|intRange
specifier|private
specifier|final
name|int
name|intRange
decl_stmt|;
DECL|field|nnRangeStart
specifier|private
specifier|final
name|int
name|nnRangeStart
decl_stmt|;
DECL|field|nonceGenerator
specifier|private
specifier|final
name|SecureRandom
name|nonceGenerator
init|=
operator|new
name|SecureRandom
argument_list|()
decl_stmt|;
comment|/**    * Constructor for workers.    *    * @param keyUpdateInterval how often a new key will be generated    * @param tokenLifetime how long an individual token is valid    */
DECL|method|BlockTokenSecretManager (long keyUpdateInterval, long tokenLifetime, String blockPoolId, String encryptionAlgorithm)
specifier|public
name|BlockTokenSecretManager
parameter_list|(
name|long
name|keyUpdateInterval
parameter_list|,
name|long
name|tokenLifetime
parameter_list|,
name|String
name|blockPoolId
parameter_list|,
name|String
name|encryptionAlgorithm
parameter_list|)
block|{
name|this
argument_list|(
literal|false
argument_list|,
name|keyUpdateInterval
argument_list|,
name|tokenLifetime
argument_list|,
name|blockPoolId
argument_list|,
name|encryptionAlgorithm
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor for masters.    *    * @param keyUpdateInterval how often a new key will be generated    * @param tokenLifetime how long an individual token is valid    * @param nnIndex namenode index of the namenode for which we are creating the manager    * @param blockPoolId block pool ID    * @param encryptionAlgorithm encryption algorithm to use    * @param numNNs number of namenodes possible    */
DECL|method|BlockTokenSecretManager (long keyUpdateInterval, long tokenLifetime, int nnIndex, int numNNs, String blockPoolId, String encryptionAlgorithm)
specifier|public
name|BlockTokenSecretManager
parameter_list|(
name|long
name|keyUpdateInterval
parameter_list|,
name|long
name|tokenLifetime
parameter_list|,
name|int
name|nnIndex
parameter_list|,
name|int
name|numNNs
parameter_list|,
name|String
name|blockPoolId
parameter_list|,
name|String
name|encryptionAlgorithm
parameter_list|)
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|keyUpdateInterval
argument_list|,
name|tokenLifetime
argument_list|,
name|blockPoolId
argument_list|,
name|encryptionAlgorithm
argument_list|,
name|nnIndex
argument_list|,
name|numNNs
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|nnIndex
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|numNNs
operator|>
literal|0
argument_list|)
expr_stmt|;
name|setSerialNo
argument_list|(
operator|new
name|SecureRandom
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|generateKeys
argument_list|()
expr_stmt|;
block|}
DECL|method|BlockTokenSecretManager (boolean isMaster, long keyUpdateInterval, long tokenLifetime, String blockPoolId, String encryptionAlgorithm, int nnIndex, int numNNs)
specifier|private
name|BlockTokenSecretManager
parameter_list|(
name|boolean
name|isMaster
parameter_list|,
name|long
name|keyUpdateInterval
parameter_list|,
name|long
name|tokenLifetime
parameter_list|,
name|String
name|blockPoolId
parameter_list|,
name|String
name|encryptionAlgorithm
parameter_list|,
name|int
name|nnIndex
parameter_list|,
name|int
name|numNNs
parameter_list|)
block|{
name|this
operator|.
name|intRange
operator|=
name|Integer
operator|.
name|MAX_VALUE
operator|/
name|numNNs
expr_stmt|;
name|this
operator|.
name|nnRangeStart
operator|=
name|intRange
operator|*
name|nnIndex
expr_stmt|;
name|this
operator|.
name|isMaster
operator|=
name|isMaster
expr_stmt|;
name|this
operator|.
name|keyUpdateInterval
operator|=
name|keyUpdateInterval
expr_stmt|;
name|this
operator|.
name|tokenLifetime
operator|=
name|tokenLifetime
expr_stmt|;
name|this
operator|.
name|allKeys
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|BlockKey
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockPoolId
operator|=
name|blockPoolId
expr_stmt|;
name|this
operator|.
name|encryptionAlgorithm
operator|=
name|encryptionAlgorithm
expr_stmt|;
name|generateKeys
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setSerialNo (int serialNo)
specifier|public
specifier|synchronized
name|void
name|setSerialNo
parameter_list|(
name|int
name|serialNo
parameter_list|)
block|{
comment|// we mod the serial number by the range and then add that times the index
name|this
operator|.
name|serialNo
operator|=
operator|(
name|serialNo
operator|%
name|intRange
operator|)
operator|+
operator|(
name|nnRangeStart
operator|)
expr_stmt|;
block|}
DECL|method|setBlockPoolId (String blockPoolId)
specifier|public
name|void
name|setBlockPoolId
parameter_list|(
name|String
name|blockPoolId
parameter_list|)
block|{
name|this
operator|.
name|blockPoolId
operator|=
name|blockPoolId
expr_stmt|;
block|}
comment|/** Initialize block keys */
DECL|method|generateKeys ()
specifier|private
specifier|synchronized
name|void
name|generateKeys
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isMaster
condition|)
return|return;
comment|/*      * Need to set estimated expiry dates for currentKey and nextKey so that if      * NN crashes, DN can still expire those keys. NN will stop using the newly      * generated currentKey after the first keyUpdateInterval, however it may      * still be used by DN and Balancer to generate new tokens before they get a      * chance to sync their keys with NN. Since we require keyUpdInterval to be      * long enough so that all live DN's and Balancer will sync their keys with      * NN at least once during the period, the estimated expiry date for      * currentKey is set to now() + 2 * keyUpdateInterval + tokenLifetime.      * Similarly, the estimated expiry date for nextKey is one keyUpdateInterval      * more.      */
name|setSerialNo
argument_list|(
name|serialNo
operator|+
literal|1
argument_list|)
expr_stmt|;
name|currentKey
operator|=
operator|new
name|BlockKey
argument_list|(
name|serialNo
argument_list|,
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|2
operator|*
name|keyUpdateInterval
operator|+
name|tokenLifetime
argument_list|,
name|generateSecret
argument_list|()
argument_list|)
expr_stmt|;
name|setSerialNo
argument_list|(
name|serialNo
operator|+
literal|1
argument_list|)
expr_stmt|;
name|nextKey
operator|=
operator|new
name|BlockKey
argument_list|(
name|serialNo
argument_list|,
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|3
operator|*
name|keyUpdateInterval
operator|+
name|tokenLifetime
argument_list|,
name|generateSecret
argument_list|()
argument_list|)
expr_stmt|;
name|allKeys
operator|.
name|put
argument_list|(
name|currentKey
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|currentKey
argument_list|)
expr_stmt|;
name|allKeys
operator|.
name|put
argument_list|(
name|nextKey
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|nextKey
argument_list|)
expr_stmt|;
block|}
comment|/** Export block keys, only to be used in master mode */
DECL|method|exportKeys ()
specifier|public
specifier|synchronized
name|ExportedBlockKeys
name|exportKeys
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isMaster
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exporting access keys"
argument_list|)
expr_stmt|;
return|return
operator|new
name|ExportedBlockKeys
argument_list|(
literal|true
argument_list|,
name|keyUpdateInterval
argument_list|,
name|tokenLifetime
argument_list|,
name|currentKey
argument_list|,
name|allKeys
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|BlockKey
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|removeExpiredKeys ()
specifier|private
specifier|synchronized
name|void
name|removeExpiredKeys
parameter_list|()
block|{
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|BlockKey
argument_list|>
argument_list|>
name|it
init|=
name|allKeys
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|BlockKey
argument_list|>
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getExpiryDate
argument_list|()
operator|<
name|now
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Set block keys, only to be used in worker mode    */
DECL|method|addKeys (ExportedBlockKeys exportedKeys)
specifier|public
specifier|synchronized
name|void
name|addKeys
parameter_list|(
name|ExportedBlockKeys
name|exportedKeys
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isMaster
operator|||
name|exportedKeys
operator|==
literal|null
condition|)
return|return;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting block keys"
argument_list|)
expr_stmt|;
name|removeExpiredKeys
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentKey
operator|=
name|exportedKeys
operator|.
name|getCurrentKey
argument_list|()
expr_stmt|;
name|BlockKey
index|[]
name|receivedKeys
init|=
name|exportedKeys
operator|.
name|getAllKeys
argument_list|()
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
name|receivedKeys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|receivedKeys
index|[
name|i
index|]
operator|==
literal|null
condition|)
continue|continue;
name|this
operator|.
name|allKeys
operator|.
name|put
argument_list|(
name|receivedKeys
index|[
name|i
index|]
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|receivedKeys
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Update block keys if update time> update interval.    * @return true if the keys are updated.    */
DECL|method|updateKeys (final long updateTime)
specifier|public
specifier|synchronized
name|boolean
name|updateKeys
parameter_list|(
specifier|final
name|long
name|updateTime
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|updateTime
operator|>
name|keyUpdateInterval
condition|)
block|{
return|return
name|updateKeys
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Update block keys, only to be used in master mode    */
DECL|method|updateKeys ()
specifier|synchronized
name|boolean
name|updateKeys
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isMaster
condition|)
return|return
literal|false
return|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Updating block keys"
argument_list|)
expr_stmt|;
name|removeExpiredKeys
argument_list|()
expr_stmt|;
comment|// set final expiry date of retiring currentKey
name|allKeys
operator|.
name|put
argument_list|(
name|currentKey
operator|.
name|getKeyId
argument_list|()
argument_list|,
operator|new
name|BlockKey
argument_list|(
name|currentKey
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|Time
operator|.
name|now
argument_list|()
operator|+
name|keyUpdateInterval
operator|+
name|tokenLifetime
argument_list|,
name|currentKey
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// update the estimated expiry date of new currentKey
name|currentKey
operator|=
operator|new
name|BlockKey
argument_list|(
name|nextKey
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|2
operator|*
name|keyUpdateInterval
operator|+
name|tokenLifetime
argument_list|,
name|nextKey
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|allKeys
operator|.
name|put
argument_list|(
name|currentKey
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|currentKey
argument_list|)
expr_stmt|;
comment|// generate a new nextKey
name|setSerialNo
argument_list|(
name|serialNo
operator|+
literal|1
argument_list|)
expr_stmt|;
name|nextKey
operator|=
operator|new
name|BlockKey
argument_list|(
name|serialNo
argument_list|,
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|3
operator|*
name|keyUpdateInterval
operator|+
name|tokenLifetime
argument_list|,
name|generateSecret
argument_list|()
argument_list|)
expr_stmt|;
name|allKeys
operator|.
name|put
argument_list|(
name|nextKey
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|nextKey
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Generate an block token for current user */
DECL|method|generateToken (ExtendedBlock block, EnumSet<BlockTokenIdentifier.AccessMode> modes)
specifier|public
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|generateToken
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|EnumSet
argument_list|<
name|BlockTokenIdentifier
operator|.
name|AccessMode
argument_list|>
name|modes
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|String
name|userID
init|=
operator|(
name|ugi
operator|==
literal|null
condition|?
literal|null
else|:
name|ugi
operator|.
name|getShortUserName
argument_list|()
operator|)
decl_stmt|;
return|return
name|generateToken
argument_list|(
name|userID
argument_list|,
name|block
argument_list|,
name|modes
argument_list|)
return|;
block|}
comment|/** Generate a block token for a specified user */
DECL|method|generateToken (String userId, ExtendedBlock block, EnumSet<BlockTokenIdentifier.AccessMode> modes)
specifier|public
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|generateToken
parameter_list|(
name|String
name|userId
parameter_list|,
name|ExtendedBlock
name|block
parameter_list|,
name|EnumSet
argument_list|<
name|BlockTokenIdentifier
operator|.
name|AccessMode
argument_list|>
name|modes
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockTokenIdentifier
name|id
init|=
operator|new
name|BlockTokenIdentifier
argument_list|(
name|userId
argument_list|,
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|modes
argument_list|)
decl_stmt|;
return|return
operator|new
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
argument_list|(
name|id
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/**    * Check if access should be allowed. userID is not checked if null. This    * method doesn't check if token password is correct. It should be used only    * when token password has already been verified (e.g., in the RPC layer).    */
DECL|method|checkAccess (BlockTokenIdentifier id, String userId, ExtendedBlock block, BlockTokenIdentifier.AccessMode mode)
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
name|BlockTokenIdentifier
operator|.
name|AccessMode
name|mode
parameter_list|)
throws|throws
name|InvalidToken
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Checking access for user="
operator|+
name|userId
operator|+
literal|", block="
operator|+
name|block
operator|+
literal|", access mode="
operator|+
name|mode
operator|+
literal|" using "
operator|+
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|userId
operator|!=
literal|null
operator|&&
operator|!
name|userId
operator|.
name|equals
argument_list|(
name|id
operator|.
name|getUserId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Block token with "
operator|+
name|id
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't belong to user "
operator|+
name|userId
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|id
operator|.
name|getBlockPoolId
argument_list|()
operator|.
name|equals
argument_list|(
name|block
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Block token with "
operator|+
name|id
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't apply to block "
operator|+
name|block
argument_list|)
throw|;
block|}
if|if
condition|(
name|id
operator|.
name|getBlockId
argument_list|()
operator|!=
name|block
operator|.
name|getBlockId
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Block token with "
operator|+
name|id
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't apply to block "
operator|+
name|block
argument_list|)
throw|;
block|}
if|if
condition|(
name|isExpired
argument_list|(
name|id
operator|.
name|getExpiryDate
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Block token with "
operator|+
name|id
operator|.
name|toString
argument_list|()
operator|+
literal|" is expired."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|id
operator|.
name|getAccessModes
argument_list|()
operator|.
name|contains
argument_list|(
name|mode
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Block token with "
operator|+
name|id
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't have "
operator|+
name|mode
operator|+
literal|" permission"
argument_list|)
throw|;
block|}
block|}
comment|/** Check if access should be allowed. userID is not checked if null */
DECL|method|checkAccess (Token<BlockTokenIdentifier> token, String userId, ExtendedBlock block, BlockTokenIdentifier.AccessMode mode)
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
name|BlockTokenIdentifier
operator|.
name|AccessMode
name|mode
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|BlockTokenIdentifier
name|id
init|=
operator|new
name|BlockTokenIdentifier
argument_list|()
decl_stmt|;
try|try
block|{
name|id
operator|.
name|readFields
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Unable to de-serialize block token identifier for user="
operator|+
name|userId
operator|+
literal|", block="
operator|+
name|block
operator|+
literal|", access mode="
operator|+
name|mode
argument_list|)
throw|;
block|}
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
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|retrievePassword
argument_list|(
name|id
argument_list|)
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Block token with "
operator|+
name|id
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't have the correct token password"
argument_list|)
throw|;
block|}
block|}
DECL|method|isExpired (long expiryDate)
specifier|private
specifier|static
name|boolean
name|isExpired
parameter_list|(
name|long
name|expiryDate
parameter_list|)
block|{
return|return
name|Time
operator|.
name|now
argument_list|()
operator|>
name|expiryDate
return|;
block|}
comment|/**    * check if a token is expired. for unit test only. return true when token is    * expired, false otherwise    */
DECL|method|isTokenExpired (Token<BlockTokenIdentifier> token)
specifier|static
name|boolean
name|isTokenExpired
parameter_list|(
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|buf
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|long
name|expiryDate
init|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|isExpired
argument_list|(
name|expiryDate
argument_list|)
return|;
block|}
comment|/** set token lifetime. */
DECL|method|setTokenLifetime (long tokenLifetime)
specifier|public
name|void
name|setTokenLifetime
parameter_list|(
name|long
name|tokenLifetime
parameter_list|)
block|{
name|this
operator|.
name|tokenLifetime
operator|=
name|tokenLifetime
expr_stmt|;
block|}
comment|/**    * Create an empty block token identifier    *    * @return a newly created empty block token identifier    */
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
comment|/**    * Create a new password/secret for the given block token identifier.    *    * @param identifier    *          the block token identifier    * @return token password/secret    */
annotation|@
name|Override
DECL|method|createPassword (BlockTokenIdentifier identifier)
specifier|protected
name|byte
index|[]
name|createPassword
parameter_list|(
name|BlockTokenIdentifier
name|identifier
parameter_list|)
block|{
name|BlockKey
name|key
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|key
operator|=
name|currentKey
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"currentKey hasn't been initialized."
argument_list|)
throw|;
name|identifier
operator|.
name|setExpiryDate
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|+
name|tokenLifetime
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setKeyId
argument_list|(
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Generating block token for "
operator|+
name|identifier
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|key
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Look up the token password/secret for the given block token identifier.    *    * @param identifier    *          the block token identifier to look up    * @return token password/secret as byte[]    * @throws InvalidToken    */
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
if|if
condition|(
name|isExpired
argument_list|(
name|identifier
operator|.
name|getExpiryDate
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Block token with "
operator|+
name|identifier
operator|.
name|toString
argument_list|()
operator|+
literal|" is expired."
argument_list|)
throw|;
block|}
name|BlockKey
name|key
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|key
operator|=
name|allKeys
operator|.
name|get
argument_list|(
name|identifier
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Can't re-compute password for "
operator|+
name|identifier
operator|.
name|toString
argument_list|()
operator|+
literal|", since the required block key (keyID="
operator|+
name|identifier
operator|.
name|getKeyId
argument_list|()
operator|+
literal|") doesn't exist."
argument_list|)
throw|;
block|}
return|return
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|key
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Generate a data encryption key for this block pool, using the current    * BlockKey.    *    * @return a data encryption key which may be used to encrypt traffic    *         over the DataTransferProtocol    */
DECL|method|generateDataEncryptionKey ()
specifier|public
name|DataEncryptionKey
name|generateDataEncryptionKey
parameter_list|()
block|{
name|byte
index|[]
name|nonce
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|nonceGenerator
operator|.
name|nextBytes
argument_list|(
name|nonce
argument_list|)
expr_stmt|;
name|BlockKey
name|key
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|key
operator|=
name|currentKey
expr_stmt|;
block|}
name|byte
index|[]
name|encryptionKey
init|=
name|createPassword
argument_list|(
name|nonce
argument_list|,
name|key
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|DataEncryptionKey
argument_list|(
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|blockPoolId
argument_list|,
name|nonce
argument_list|,
name|encryptionKey
argument_list|,
name|Time
operator|.
name|now
argument_list|()
operator|+
name|tokenLifetime
argument_list|,
name|encryptionAlgorithm
argument_list|)
return|;
block|}
comment|/**    * Recreate an encryption key based on the given key id and nonce.    *    * @param keyId identifier of the secret key used to generate the encryption key.    * @param nonce random value used to create the encryption key    * @return the encryption key which corresponds to this (keyId, blockPoolId, nonce)    * @throws InvalidEncryptionKeyException    */
DECL|method|retrieveDataEncryptionKey (int keyId, byte[] nonce)
specifier|public
name|byte
index|[]
name|retrieveDataEncryptionKey
parameter_list|(
name|int
name|keyId
parameter_list|,
name|byte
index|[]
name|nonce
parameter_list|)
throws|throws
name|InvalidEncryptionKeyException
block|{
name|BlockKey
name|key
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|key
operator|=
name|allKeys
operator|.
name|get
argument_list|(
name|keyId
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidEncryptionKeyException
argument_list|(
literal|"Can't re-compute encryption key"
operator|+
literal|" for nonce, since the required block key (keyID="
operator|+
name|keyId
operator|+
literal|") doesn't exist. Current key: "
operator|+
name|currentKey
operator|.
name|getKeyId
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|createPassword
argument_list|(
name|nonce
argument_list|,
name|key
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setKeyUpdateIntervalForTesting (long millis)
specifier|public
specifier|synchronized
name|void
name|setKeyUpdateIntervalForTesting
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
name|this
operator|.
name|keyUpdateInterval
operator|=
name|millis
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|clearAllKeysForTesting ()
specifier|public
name|void
name|clearAllKeysForTesting
parameter_list|()
block|{
name|allKeys
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getSerialNoForTesting ()
specifier|public
specifier|synchronized
name|int
name|getSerialNoForTesting
parameter_list|()
block|{
return|return
name|serialNo
return|;
block|}
block|}
end_class

end_unit

