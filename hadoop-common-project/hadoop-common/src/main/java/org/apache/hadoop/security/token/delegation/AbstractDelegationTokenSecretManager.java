begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token.delegation
package|package
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
package|;
end_package

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
name|io
operator|.
name|Text
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|security
operator|.
name|AccessControlException
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
name|HadoopKerberosName
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
name|util
operator|.
name|Daemon
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
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
specifier|public
specifier|abstract
DECL|class|AbstractDelegationTokenSecretManager
class|class
name|AbstractDelegationTokenSecretManager
parameter_list|<
name|TokenIdent
extends|extends
name|AbstractDelegationTokenIdentifier
parameter_list|>
extends|extends
name|SecretManager
argument_list|<
name|TokenIdent
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
name|AbstractDelegationTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**     * Cache of currently valid tokens, mapping from DelegationTokenIdentifier     * to DelegationTokenInformation. Protected by this object lock.    */
DECL|field|currentTokens
specifier|protected
specifier|final
name|Map
argument_list|<
name|TokenIdent
argument_list|,
name|DelegationTokenInformation
argument_list|>
name|currentTokens
init|=
operator|new
name|HashMap
argument_list|<
name|TokenIdent
argument_list|,
name|DelegationTokenInformation
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Sequence number to create DelegationTokenIdentifier.    * Protected by this object lock.    */
DECL|field|delegationTokenSequenceNumber
specifier|protected
name|int
name|delegationTokenSequenceNumber
init|=
literal|0
decl_stmt|;
comment|/**    * Access to allKeys is protected by this object lock    */
DECL|field|allKeys
specifier|protected
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|DelegationKey
argument_list|>
name|allKeys
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|DelegationKey
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Access to currentId is protected by this object lock.    */
DECL|field|currentId
specifier|protected
name|int
name|currentId
init|=
literal|0
decl_stmt|;
comment|/**    * Access to currentKey is protected by this object lock    */
DECL|field|currentKey
specifier|private
name|DelegationKey
name|currentKey
decl_stmt|;
DECL|field|keyUpdateInterval
specifier|private
name|long
name|keyUpdateInterval
decl_stmt|;
DECL|field|tokenMaxLifetime
specifier|private
name|long
name|tokenMaxLifetime
decl_stmt|;
DECL|field|tokenRemoverScanInterval
specifier|private
name|long
name|tokenRemoverScanInterval
decl_stmt|;
DECL|field|tokenRenewInterval
specifier|private
name|long
name|tokenRenewInterval
decl_stmt|;
DECL|field|tokenRemoverThread
specifier|private
name|Thread
name|tokenRemoverThread
decl_stmt|;
DECL|field|running
specifier|protected
specifier|volatile
name|boolean
name|running
decl_stmt|;
comment|/**    * If the delegation token update thread holds this lock, it will    * not get interrupted.    */
DECL|field|noInterruptsLock
specifier|protected
name|Object
name|noInterruptsLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|AbstractDelegationTokenSecretManager (long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval)
specifier|public
name|AbstractDelegationTokenSecretManager
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
parameter_list|)
block|{
name|this
operator|.
name|keyUpdateInterval
operator|=
name|delegationKeyUpdateInterval
expr_stmt|;
name|this
operator|.
name|tokenMaxLifetime
operator|=
name|delegationTokenMaxLifetime
expr_stmt|;
name|this
operator|.
name|tokenRenewInterval
operator|=
name|delegationTokenRenewInterval
expr_stmt|;
name|this
operator|.
name|tokenRemoverScanInterval
operator|=
name|delegationTokenRemoverScanInterval
expr_stmt|;
block|}
comment|/** should be called before this object is used */
DECL|method|startThreads ()
specifier|public
name|void
name|startThreads
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|running
argument_list|)
expr_stmt|;
name|updateCurrentKey
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|running
operator|=
literal|true
expr_stmt|;
name|tokenRemoverThread
operator|=
operator|new
name|Daemon
argument_list|(
operator|new
name|ExpiredTokenRemover
argument_list|()
argument_list|)
expr_stmt|;
name|tokenRemoverThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Reset all data structures and mutable state.    */
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
name|currentId
operator|=
literal|0
expr_stmt|;
name|allKeys
operator|.
name|clear
argument_list|()
expr_stmt|;
name|delegationTokenSequenceNumber
operator|=
literal|0
expr_stmt|;
name|currentTokens
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**     * Add a previously used master key to cache (when NN restarts),     * should be called before activate().    * */
DECL|method|addKey (DelegationKey key)
specifier|public
specifier|synchronized
name|void
name|addKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|running
condition|)
comment|// a safety check
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't add delegation key to a running SecretManager."
argument_list|)
throw|;
if|if
condition|(
name|key
operator|.
name|getKeyId
argument_list|()
operator|>
name|currentId
condition|)
block|{
name|currentId
operator|=
name|key
operator|.
name|getKeyId
argument_list|()
expr_stmt|;
block|}
name|allKeys
operator|.
name|put
argument_list|(
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|getAllKeys ()
specifier|public
specifier|synchronized
name|DelegationKey
index|[]
name|getAllKeys
parameter_list|()
block|{
return|return
name|allKeys
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|DelegationKey
index|[
literal|0
index|]
argument_list|)
return|;
block|}
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
return|return;
block|}
DECL|method|logExpireToken (TokenIdent ident)
specifier|protected
name|void
name|logExpireToken
parameter_list|(
name|TokenIdent
name|ident
parameter_list|)
throws|throws
name|IOException
block|{
return|return;
block|}
comment|/**     * Update the current master key     * This is called once by startThreads before tokenRemoverThread is created,     * and only by tokenRemoverThread afterwards.    */
DECL|method|updateCurrentKey ()
specifier|private
name|void
name|updateCurrentKey
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Updating the current master key for generating delegation tokens"
argument_list|)
expr_stmt|;
comment|/* Create a new currentKey with an estimated expiry date. */
name|int
name|newCurrentId
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|newCurrentId
operator|=
name|currentId
operator|+
literal|1
expr_stmt|;
block|}
name|DelegationKey
name|newKey
init|=
operator|new
name|DelegationKey
argument_list|(
name|newCurrentId
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|keyUpdateInterval
operator|+
name|tokenMaxLifetime
argument_list|,
name|generateSecret
argument_list|()
argument_list|)
decl_stmt|;
comment|//Log must be invoked outside the lock on 'this'
name|logUpdateMasterKey
argument_list|(
name|newKey
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|currentId
operator|=
name|newKey
operator|.
name|getKeyId
argument_list|()
expr_stmt|;
name|currentKey
operator|=
name|newKey
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
block|}
block|}
comment|/**     * Update the current master key for generating delegation tokens     * It should be called only by tokenRemoverThread.    */
DECL|method|rollMasterKey ()
name|void
name|rollMasterKey
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|removeExpiredKeys
argument_list|()
expr_stmt|;
comment|/* set final expiry date for retiring currentKey */
name|currentKey
operator|.
name|setExpiryDate
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|+
name|tokenMaxLifetime
argument_list|)
expr_stmt|;
comment|/*        * currentKey might have been removed by removeExpiredKeys(), if        * updateMasterKey() isn't called at expected interval. Add it back to        * allKeys just in case.        */
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
block|}
name|updateCurrentKey
argument_list|()
expr_stmt|;
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
name|DelegationKey
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
name|DelegationKey
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
annotation|@
name|Override
DECL|method|createPassword (TokenIdent identifier)
specifier|protected
specifier|synchronized
name|byte
index|[]
name|createPassword
parameter_list|(
name|TokenIdent
name|identifier
parameter_list|)
block|{
name|int
name|sequenceNum
decl_stmt|;
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|sequenceNum
operator|=
operator|++
name|delegationTokenSequenceNumber
expr_stmt|;
name|identifier
operator|.
name|setIssueDate
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setMaxDate
argument_list|(
name|now
operator|+
name|tokenMaxLifetime
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setMasterKeyId
argument_list|(
name|currentId
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setSequenceNumber
argument_list|(
name|sequenceNum
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating password for identifier: "
operator|+
name|identifier
argument_list|)
expr_stmt|;
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
name|currentKey
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
name|now
operator|+
name|tokenRenewInterval
argument_list|,
name|password
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|password
return|;
block|}
annotation|@
name|Override
DECL|method|retrievePassword (TokenIdent identifier)
specifier|public
specifier|synchronized
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|TokenIdent
name|identifier
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|DelegationTokenInformation
name|info
init|=
name|currentTokens
operator|.
name|get
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"token ("
operator|+
name|identifier
operator|.
name|toString
argument_list|()
operator|+
literal|") can't be found in cache"
argument_list|)
throw|;
block|}
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getRenewDate
argument_list|()
operator|<
name|now
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"token ("
operator|+
name|identifier
operator|.
name|toString
argument_list|()
operator|+
literal|") is expired"
argument_list|)
throw|;
block|}
return|return
name|info
operator|.
name|getPassword
argument_list|()
return|;
block|}
comment|/**    * Verifies that the given identifier and password are valid and match.    * @param identifier Token identifier.    * @param password Password in the token.    * @throws InvalidToken    */
DECL|method|verifyToken (TokenIdent identifier, byte[] password)
specifier|public
specifier|synchronized
name|void
name|verifyToken
parameter_list|(
name|TokenIdent
name|identifier
parameter_list|,
name|byte
index|[]
name|password
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|byte
index|[]
name|storedPassword
init|=
name|retrievePassword
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|password
argument_list|,
name|storedPassword
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"token ("
operator|+
name|identifier
operator|+
literal|") is invalid, password doesn't match"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Renew a delegation token.    * @param token the token to renew    * @param renewer the full principal name of the user doing the renewal    * @return the new expiration time    * @throws InvalidToken if the token is invalid    * @throws AccessControlException if the user can't renew token    */
DECL|method|renewToken (Token<TokenIdent> token, String renewer)
specifier|public
specifier|synchronized
name|long
name|renewToken
parameter_list|(
name|Token
argument_list|<
name|TokenIdent
argument_list|>
name|token
parameter_list|,
name|String
name|renewer
parameter_list|)
throws|throws
name|InvalidToken
throws|,
name|IOException
block|{
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
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
name|TokenIdent
name|id
init|=
name|createIdentifier
argument_list|()
decl_stmt|;
name|id
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Token renewal requested for identifier: "
operator|+
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|.
name|getMaxDate
argument_list|()
operator|<
name|now
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"User "
operator|+
name|renewer
operator|+
literal|" tried to renew an expired token"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|id
operator|.
name|getRenewer
argument_list|()
operator|==
literal|null
operator|)
operator|||
operator|(
name|id
operator|.
name|getRenewer
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"User "
operator|+
name|renewer
operator|+
literal|" tried to renew a token without "
operator|+
literal|"a renewer"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|id
operator|.
name|getRenewer
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|renewer
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Client "
operator|+
name|renewer
operator|+
literal|" tries to renew a token with "
operator|+
literal|"renewer specified as "
operator|+
name|id
operator|.
name|getRenewer
argument_list|()
argument_list|)
throw|;
block|}
name|DelegationKey
name|key
init|=
name|allKeys
operator|.
name|get
argument_list|(
name|id
operator|.
name|getMasterKeyId
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"Unable to find master key for keyId="
operator|+
name|id
operator|.
name|getMasterKeyId
argument_list|()
operator|+
literal|" from cache. Failed to renew an unexpired token"
operator|+
literal|" with sequenceNumber="
operator|+
name|id
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
throw|;
block|}
name|byte
index|[]
name|password
init|=
name|createPassword
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|key
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|password
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
name|AccessControlException
argument_list|(
literal|"Client "
operator|+
name|renewer
operator|+
literal|" is trying to renew a token with "
operator|+
literal|"wrong password"
argument_list|)
throw|;
block|}
name|long
name|renewTime
init|=
name|Math
operator|.
name|min
argument_list|(
name|id
operator|.
name|getMaxDate
argument_list|()
argument_list|,
name|now
operator|+
name|tokenRenewInterval
argument_list|)
decl_stmt|;
name|DelegationTokenInformation
name|info
init|=
operator|new
name|DelegationTokenInformation
argument_list|(
name|renewTime
argument_list|,
name|password
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentTokens
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Renewal request for unknown token"
argument_list|)
throw|;
block|}
name|currentTokens
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|info
argument_list|)
expr_stmt|;
return|return
name|renewTime
return|;
block|}
comment|/**    * Cancel a token by removing it from cache.    * @return Identifier of the canceled token    * @throws InvalidToken for invalid token    * @throws AccessControlException if the user isn't allowed to cancel    */
DECL|method|cancelToken (Token<TokenIdent> token, String canceller)
specifier|public
specifier|synchronized
name|TokenIdent
name|cancelToken
parameter_list|(
name|Token
argument_list|<
name|TokenIdent
argument_list|>
name|token
parameter_list|,
name|String
name|canceller
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
name|TokenIdent
name|id
init|=
name|createIdentifier
argument_list|()
decl_stmt|;
name|id
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Token cancelation requested for identifier: "
operator|+
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|.
name|getUser
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Token with no owner"
argument_list|)
throw|;
block|}
name|String
name|owner
init|=
name|id
operator|.
name|getUser
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|Text
name|renewer
init|=
name|id
operator|.
name|getRenewer
argument_list|()
decl_stmt|;
name|HadoopKerberosName
name|cancelerKrbName
init|=
operator|new
name|HadoopKerberosName
argument_list|(
name|canceller
argument_list|)
decl_stmt|;
name|String
name|cancelerShortName
init|=
name|cancelerKrbName
operator|.
name|getShortName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|canceller
operator|.
name|equals
argument_list|(
name|owner
argument_list|)
operator|&&
operator|(
name|renewer
operator|==
literal|null
operator|||
name|renewer
operator|.
name|toString
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|cancelerShortName
operator|.
name|equals
argument_list|(
name|renewer
operator|.
name|toString
argument_list|()
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
name|canceller
operator|+
literal|" is not authorized to cancel the token"
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
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Token not found"
argument_list|)
throw|;
block|}
return|return
name|id
return|;
block|}
comment|/**    * Convert the byte[] to a secret key    * @param key the byte[] to create the secret key from    * @return the secret key    */
DECL|method|createSecretKey (byte[] key)
specifier|public
specifier|static
name|SecretKey
name|createSecretKey
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
block|{
return|return
name|SecretManager
operator|.
name|createSecretKey
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/** Class to encapsulate a token's renew date and password. */
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DelegationTokenInformation
specifier|public
specifier|static
class|class
name|DelegationTokenInformation
block|{
DECL|field|renewDate
name|long
name|renewDate
decl_stmt|;
DECL|field|password
name|byte
index|[]
name|password
decl_stmt|;
DECL|method|DelegationTokenInformation (long renewDate, byte[] password)
specifier|public
name|DelegationTokenInformation
parameter_list|(
name|long
name|renewDate
parameter_list|,
name|byte
index|[]
name|password
parameter_list|)
block|{
name|this
operator|.
name|renewDate
operator|=
name|renewDate
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/** returns renew date */
DECL|method|getRenewDate ()
specifier|public
name|long
name|getRenewDate
parameter_list|()
block|{
return|return
name|renewDate
return|;
block|}
comment|/** returns password */
DECL|method|getPassword ()
name|byte
index|[]
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
block|}
comment|/** Remove expired delegation tokens from cache */
DECL|method|removeExpiredToken ()
specifier|private
name|void
name|removeExpiredToken
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|TokenIdent
argument_list|>
name|expiredTokens
init|=
operator|new
name|HashSet
argument_list|<
name|TokenIdent
argument_list|>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|TokenIdent
argument_list|,
name|DelegationTokenInformation
argument_list|>
argument_list|>
name|i
init|=
name|currentTokens
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|TokenIdent
argument_list|,
name|DelegationTokenInformation
argument_list|>
name|entry
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|renewDate
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getRenewDate
argument_list|()
decl_stmt|;
if|if
condition|(
name|renewDate
operator|<
name|now
condition|)
block|{
name|expiredTokens
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// don't hold lock on 'this' to avoid edit log updates blocking token ops
for|for
control|(
name|TokenIdent
name|ident
range|:
name|expiredTokens
control|)
block|{
name|logExpireToken
argument_list|(
name|ident
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stopThreads ()
specifier|public
name|void
name|stopThreads
parameter_list|()
block|{
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
literal|"Stopping expired delegation token remover thread"
argument_list|)
expr_stmt|;
name|running
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|tokenRemoverThread
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|noInterruptsLock
init|)
block|{
name|tokenRemoverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|tokenRemoverThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to join on token removal thread"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * is secretMgr running    * @return true if secret mgr is running    */
DECL|method|isRunning ()
specifier|public
specifier|synchronized
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|running
return|;
block|}
DECL|class|ExpiredTokenRemover
specifier|private
class|class
name|ExpiredTokenRemover
extends|extends
name|Thread
block|{
DECL|field|lastMasterKeyUpdate
specifier|private
name|long
name|lastMasterKeyUpdate
decl_stmt|;
DECL|field|lastTokenCacheCleanup
specifier|private
name|long
name|lastTokenCacheCleanup
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting expired delegation token remover thread, "
operator|+
literal|"tokenRemoverScanInterval="
operator|+
name|tokenRemoverScanInterval
operator|/
operator|(
literal|60
operator|*
literal|1000
operator|)
operator|+
literal|" min(s)"
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
name|running
condition|)
block|{
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastMasterKeyUpdate
operator|+
name|keyUpdateInterval
operator|<
name|now
condition|)
block|{
try|try
block|{
name|rollMasterKey
argument_list|()
expr_stmt|;
name|lastMasterKeyUpdate
operator|=
name|now
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Master key updating failed: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lastTokenCacheCleanup
operator|+
name|tokenRemoverScanInterval
operator|<
name|now
condition|)
block|{
name|removeExpiredToken
argument_list|()
expr_stmt|;
name|lastTokenCacheCleanup
operator|=
name|now
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|5000
argument_list|,
name|keyUpdateInterval
argument_list|)
argument_list|)
expr_stmt|;
comment|// 5 seconds
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"InterruptedExcpetion recieved for ExpiredTokenRemover thread "
operator|+
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ExpiredTokenRemover thread received unexpected exception. "
operator|+
name|t
argument_list|)
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

