begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.security.token.delegation
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
name|delegation
package|;
end_package

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
name|DataOutputStream
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
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|server
operator|.
name|namenode
operator|.
name|FSNamesystem
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
operator|.
name|OperationCategory
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
name|ipc
operator|.
name|StandbyException
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
name|Credentials
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
name|delegation
operator|.
name|AbstractDelegationTokenSecretManager
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
name|delegation
operator|.
name|DelegationKey
import|;
end_import

begin_comment
comment|/**  * A HDFS specific delegation token secret manager.  * The secret manager is responsible for generating and accepting the password  * for each token.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DelegationTokenSecretManager
specifier|public
class|class
name|DelegationTokenSecretManager
extends|extends
name|AbstractDelegationTokenSecretManager
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DelegationTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|namesystem
specifier|private
specifier|final
name|FSNamesystem
name|namesystem
decl_stmt|;
comment|/**    * Create a secret manager    * @param delegationKeyUpdateInterval the number of seconds for rolling new    *        secret keys.    * @param delegationTokenMaxLifetime the maximum lifetime of the delegation    *        tokens    * @param delegationTokenRenewInterval how often the tokens must be renewed    * @param delegationTokenRemoverScanInterval how often the tokens are scanned    *        for expired tokens    */
DECL|method|DelegationTokenSecretManager (long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval, FSNamesystem namesystem)
specifier|public
name|DelegationTokenSecretManager
parameter_list|(
name|long
name|delegationKeyUpdateInterval
parameter_list|,
name|long
name|delegationTokenMaxLifetime
parameter_list|,
name|long
name|delegationTokenRenewInterval
parameter_list|,
name|long
name|delegationTokenRemoverScanInterval
parameter_list|,
name|FSNamesystem
name|namesystem
parameter_list|)
block|{
name|super
argument_list|(
name|delegationKeyUpdateInterval
argument_list|,
name|delegationTokenMaxLifetime
argument_list|,
name|delegationTokenRenewInterval
argument_list|,
name|delegationTokenRemoverScanInterval
argument_list|)
expr_stmt|;
name|this
operator|.
name|namesystem
operator|=
name|namesystem
expr_stmt|;
block|}
annotation|@
name|Override
comment|//SecretManager
DECL|method|createIdentifier ()
specifier|public
name|DelegationTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|DelegationTokenIdentifier
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//SecretManager
DECL|method|checkAvailableForRead ()
specifier|public
name|void
name|checkAvailableForRead
parameter_list|()
throws|throws
name|StandbyException
block|{
name|namesystem
operator|.
name|checkOperation
argument_list|(
name|OperationCategory
operator|.
name|READ
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|namesystem
operator|.
name|checkOperation
argument_list|(
name|OperationCategory
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|namesystem
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns expiry time of a token given its identifier.    *     * @param dtId DelegationTokenIdentifier of a token    * @return Expiry time of the token    * @throws IOException    */
DECL|method|getTokenExpiryTime ( DelegationTokenIdentifier dtId)
specifier|public
specifier|synchronized
name|long
name|getTokenExpiryTime
parameter_list|(
name|DelegationTokenIdentifier
name|dtId
parameter_list|)
throws|throws
name|IOException
block|{
name|DelegationTokenInformation
name|info
init|=
name|currentTokens
operator|.
name|get
argument_list|(
name|dtId
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
return|return
name|info
operator|.
name|getRenewDate
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No delegation token found for this identifier"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Load SecretManager state from fsimage.    *     * @param in input stream to read fsimage    * @throws IOException    */
DECL|method|loadSecretManagerState (DataInputStream in)
specifier|public
specifier|synchronized
name|void
name|loadSecretManagerState
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|running
condition|)
block|{
comment|// a safety check
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't load state from image in a running SecretManager."
argument_list|)
throw|;
block|}
name|currentId
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|loadAllKeys
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|delegationTokenSequenceNumber
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|loadCurrentTokens
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**    * Store the current state of the SecretManager for persistence    *     * @param out Output stream for writing into fsimage.    * @throws IOException    */
DECL|method|saveSecretManagerState (DataOutputStream out)
specifier|public
specifier|synchronized
name|void
name|saveSecretManagerState
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|currentId
argument_list|)
expr_stmt|;
name|saveAllKeys
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|delegationTokenSequenceNumber
argument_list|)
expr_stmt|;
name|saveCurrentTokens
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**    * This method is intended to be used only while reading edit logs.    *     * @param identifier DelegationTokenIdentifier read from the edit logs or    * fsimage    *     * @param expiryTime token expiry time    * @throws IOException    */
DECL|method|addPersistedDelegationToken ( DelegationTokenIdentifier identifier, long expiryTime)
specifier|public
specifier|synchronized
name|void
name|addPersistedDelegationToken
parameter_list|(
name|DelegationTokenIdentifier
name|identifier
parameter_list|,
name|long
name|expiryTime
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|running
condition|)
block|{
comment|// a safety check
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't add persisted delegation token to a running SecretManager."
argument_list|)
throw|;
block|}
name|int
name|keyId
init|=
name|identifier
operator|.
name|getMasterKeyId
argument_list|()
decl_stmt|;
name|DelegationKey
name|dKey
init|=
name|allKeys
operator|.
name|get
argument_list|(
name|keyId
argument_list|)
decl_stmt|;
if|if
condition|(
name|dKey
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No KEY found for persisted identifier "
operator|+
name|identifier
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|byte
index|[]
name|password
init|=
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|dKey
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
operator|>
name|this
operator|.
name|delegationTokenSequenceNumber
condition|)
block|{
name|this
operator|.
name|delegationTokenSequenceNumber
operator|=
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|currentTokens
operator|.
name|get
argument_list|(
name|identifier
argument_list|)
operator|==
literal|null
condition|)
block|{
name|currentTokens
operator|.
name|put
argument_list|(
name|identifier
argument_list|,
operator|new
name|DelegationTokenInformation
argument_list|(
name|expiryTime
argument_list|,
name|password
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Same delegation token being added twice; invalid entry in fsimage or editlogs"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Add a MasterKey to the list of keys.    *     * @param key DelegationKey    * @throws IOException    */
DECL|method|updatePersistedMasterKey (DelegationKey key)
specifier|public
specifier|synchronized
name|void
name|updatePersistedMasterKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|addKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
comment|/**    * Update the token cache with renewal record in edit logs.    *     * @param identifier DelegationTokenIdentifier of the renewed token    * @param expiryTime    * @throws IOException    */
DECL|method|updatePersistedTokenRenewal ( DelegationTokenIdentifier identifier, long expiryTime)
specifier|public
specifier|synchronized
name|void
name|updatePersistedTokenRenewal
parameter_list|(
name|DelegationTokenIdentifier
name|identifier
parameter_list|,
name|long
name|expiryTime
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|running
condition|)
block|{
comment|// a safety check
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't update persisted delegation token renewal to a running SecretManager."
argument_list|)
throw|;
block|}
name|DelegationTokenInformation
name|info
init|=
literal|null
decl_stmt|;
name|info
operator|=
name|currentTokens
operator|.
name|get
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|int
name|keyId
init|=
name|identifier
operator|.
name|getMasterKeyId
argument_list|()
decl_stmt|;
name|byte
index|[]
name|password
init|=
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|allKeys
operator|.
name|get
argument_list|(
name|keyId
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|currentTokens
operator|.
name|put
argument_list|(
name|identifier
argument_list|,
operator|new
name|DelegationTokenInformation
argument_list|(
name|expiryTime
argument_list|,
name|password
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *  Update the token cache with the cancel record in edit logs    *      *  @param identifier DelegationTokenIdentifier of the canceled token    *  @throws IOException    */
DECL|method|updatePersistedTokenCancellation ( DelegationTokenIdentifier identifier)
specifier|public
specifier|synchronized
name|void
name|updatePersistedTokenCancellation
parameter_list|(
name|DelegationTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|running
condition|)
block|{
comment|// a safety check
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't update persisted delegation token renewal to a running SecretManager."
argument_list|)
throw|;
block|}
name|currentTokens
operator|.
name|remove
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the number of delegation keys currently stored.    * @return number of delegation keys    */
DECL|method|getNumberOfKeys ()
specifier|public
specifier|synchronized
name|int
name|getNumberOfKeys
parameter_list|()
block|{
return|return
name|allKeys
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Private helper methods to save delegation keys and tokens in fsimage    */
DECL|method|saveCurrentTokens (DataOutputStream out)
specifier|private
specifier|synchronized
name|void
name|saveCurrentTokens
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|currentTokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|iter
init|=
name|currentTokens
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DelegationTokenIdentifier
name|id
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|id
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|DelegationTokenInformation
name|info
init|=
name|currentTokens
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|info
operator|.
name|getRenewDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Save the current state of allKeys    */
DECL|method|saveAllKeys (DataOutputStream out)
specifier|private
specifier|synchronized
name|void
name|saveAllKeys
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|allKeys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iter
init|=
name|allKeys
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Integer
name|key
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|allKeys
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Private helper methods to load Delegation tokens from fsimage    */
DECL|method|loadCurrentTokens (DataInputStream in)
specifier|private
specifier|synchronized
name|void
name|loadCurrentTokens
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numberOfTokens
init|=
name|in
operator|.
name|readInt
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
name|numberOfTokens
condition|;
name|i
operator|++
control|)
block|{
name|DelegationTokenIdentifier
name|id
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|id
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|long
name|expiryTime
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|addPersistedDelegationToken
argument_list|(
name|id
argument_list|,
name|expiryTime
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Private helper method to load delegation keys from fsimage.    * @param in    * @throws IOException    */
DECL|method|loadAllKeys (DataInputStream in)
specifier|private
specifier|synchronized
name|void
name|loadAllKeys
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numberOfKeys
init|=
name|in
operator|.
name|readInt
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
name|numberOfKeys
condition|;
name|i
operator|++
control|)
block|{
name|DelegationKey
name|value
init|=
operator|new
name|DelegationKey
argument_list|()
decl_stmt|;
name|value
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|addKey
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Call namesystem to update editlogs for new master key.    */
annotation|@
name|Override
comment|//AbstractDelegationTokenManager
DECL|method|logUpdateMasterKey (DelegationKey key)
specifier|protected
name|void
name|logUpdateMasterKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|noInterruptsLock
init|)
block|{
comment|// The edit logging code will fail catastrophically if it
comment|// is interrupted during a logSync, since the interrupt
comment|// closes the edit log files. Doing this inside the
comment|// above lock and then checking interruption status
comment|// prevents this bug.
if|if
condition|(
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|(
literal|"Interrupted before updating master key"
argument_list|)
throw|;
block|}
name|namesystem
operator|.
name|logUpdateMasterKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** A utility method for creating credentials. */
DECL|method|createCredentials (final NameNode namenode, final UserGroupInformation ugi, final String renewer)
specifier|public
specifier|static
name|Credentials
name|createCredentials
parameter_list|(
specifier|final
name|NameNode
name|namenode
parameter_list|,
specifier|final
name|UserGroupInformation
name|ugi
parameter_list|,
specifier|final
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|namenode
operator|.
name|getRpcServer
argument_list|(         )
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to get the token for "
operator|+
name|renewer
operator|+
literal|", user="
operator|+
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|InetSocketAddress
name|addr
init|=
name|namenode
operator|.
name|getNameNodeAddress
argument_list|()
decl_stmt|;
name|SecurityUtil
operator|.
name|setTokenService
argument_list|(
name|token
argument_list|,
name|addr
argument_list|)
expr_stmt|;
specifier|final
name|Credentials
name|c
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|c
operator|.
name|addToken
argument_list|(
operator|new
name|Text
argument_list|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|,
name|token
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
end_class

end_unit

