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
name|security
operator|.
name|OzoneSecretStore
operator|.
name|OzoneManagerSecretState
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
operator|.
name|TokenInfo
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * SecretManager for Ozone Master. Responsible for signing identifiers with  * private key,  */
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
DECL|class|OzoneDelegationTokenSecretManager
specifier|public
class|class
name|OzoneDelegationTokenSecretManager
extends|extends
name|OzoneSecretManager
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OzoneDelegationTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|currentTokens
specifier|private
specifier|final
name|Map
argument_list|<
name|OzoneTokenIdentifier
argument_list|,
name|TokenInfo
argument_list|>
name|currentTokens
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|OzoneSecretStore
name|store
decl_stmt|;
DECL|field|tokenRemoverThread
specifier|private
name|Thread
name|tokenRemoverThread
decl_stmt|;
DECL|field|tokenRemoverScanInterval
specifier|private
specifier|final
name|long
name|tokenRemoverScanInterval
decl_stmt|;
comment|/**    * If the delegation token update thread holds this lock, it will not get    * interrupted.    */
DECL|field|noInterruptsLock
specifier|private
name|Object
name|noInterruptsLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|/**    * Create a secret manager.    *    * @param conf configuration.    * @param tokenMaxLifetime the maximum lifetime of the delegation tokens in    * milliseconds    * @param tokenRenewInterval how often the tokens must be renewed in    * milliseconds    * @param dtRemoverScanInterval how often the tokens are scanned for expired    * tokens in milliseconds    */
DECL|method|OzoneDelegationTokenSecretManager (OzoneConfiguration conf, long tokenMaxLifetime, long tokenRenewInterval, long dtRemoverScanInterval, Text service)
specifier|public
name|OzoneDelegationTokenSecretManager
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|long
name|tokenMaxLifetime
parameter_list|,
name|long
name|tokenRenewInterval
parameter_list|,
name|long
name|dtRemoverScanInterval
parameter_list|,
name|Text
name|service
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|SecurityConfig
argument_list|(
name|conf
argument_list|)
argument_list|,
name|tokenMaxLifetime
argument_list|,
name|tokenRenewInterval
argument_list|,
name|service
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|currentTokens
operator|=
operator|new
name|ConcurrentHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokenRemoverScanInterval
operator|=
name|dtRemoverScanInterval
expr_stmt|;
name|this
operator|.
name|store
operator|=
operator|new
name|OzoneSecretStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|loadTokenSecretState
argument_list|(
name|store
operator|.
name|loadState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|OzoneTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
name|OzoneTokenIdentifier
operator|.
name|newInstance
argument_list|()
return|;
block|}
comment|/**    * Create new Identifier with given,owner,renwer and realUser.    *    * @return T    */
DECL|method|createIdentifier (Text owner, Text renewer, Text realUser)
specifier|public
name|OzoneTokenIdentifier
name|createIdentifier
parameter_list|(
name|Text
name|owner
parameter_list|,
name|Text
name|renewer
parameter_list|,
name|Text
name|realUser
parameter_list|)
block|{
return|return
name|OzoneTokenIdentifier
operator|.
name|newInstance
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
return|;
block|}
comment|/**    * Returns {@link Token} for given identifier.    *    * @param owner    * @param renewer    * @param realUser    * @return Token    * @throws IOException to allow future exceptions to be added without breaking    * compatibility    */
DECL|method|createToken (Text owner, Text renewer, Text realUser)
specifier|public
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|createToken
parameter_list|(
name|Text
name|owner
parameter_list|,
name|Text
name|renewer
parameter_list|,
name|Text
name|realUser
parameter_list|)
throws|throws
name|IOException
block|{
name|OzoneTokenIdentifier
name|identifier
init|=
name|createIdentifier
argument_list|(
name|owner
argument_list|,
name|renewer
argument_list|,
name|realUser
argument_list|)
decl_stmt|;
name|updateIdentifierDetails
argument_list|(
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
name|getCurrentKey
argument_list|()
operator|.
name|getPrivateKey
argument_list|()
argument_list|)
decl_stmt|;
name|addToTokenStore
argument_list|(
name|identifier
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<>
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|password
argument_list|,
name|identifier
operator|.
name|getKind
argument_list|()
argument_list|,
name|getService
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|long
name|expiryTime
init|=
name|identifier
operator|.
name|getIssueDate
argument_list|()
operator|+
name|getTokenRenewInterval
argument_list|()
decl_stmt|;
name|String
name|tokenId
init|=
name|identifier
operator|.
name|toStringStable
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Issued delegation token -> expiryTime:{},tokenId:{}"
argument_list|,
name|expiryTime
argument_list|,
name|tokenId
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
comment|/**    * Stores given identifier in token store.    *    * @param identifier    * @param password    * @throws IOException    */
DECL|method|addToTokenStore (OzoneTokenIdentifier identifier, byte[] password)
specifier|private
name|void
name|addToTokenStore
parameter_list|(
name|OzoneTokenIdentifier
name|identifier
parameter_list|,
name|byte
index|[]
name|password
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenInfo
name|tokenInfo
init|=
operator|new
name|TokenInfo
argument_list|(
name|identifier
operator|.
name|getIssueDate
argument_list|()
operator|+
name|getTokenRenewInterval
argument_list|()
argument_list|,
name|password
argument_list|,
name|identifier
operator|.
name|getTrackingId
argument_list|()
argument_list|)
decl_stmt|;
name|currentTokens
operator|.
name|put
argument_list|(
name|identifier
argument_list|,
name|tokenInfo
argument_list|)
expr_stmt|;
name|store
operator|.
name|storeToken
argument_list|(
name|identifier
argument_list|,
name|tokenInfo
operator|.
name|getRenewDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Updates issue date, master key id and sequence number for identifier.    *    * @param identifier the identifier to validate    */
DECL|method|updateIdentifierDetails (OzoneTokenIdentifier identifier)
specifier|private
name|void
name|updateIdentifierDetails
parameter_list|(
name|OzoneTokenIdentifier
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
name|monotonicNow
argument_list|()
decl_stmt|;
name|sequenceNum
operator|=
name|incrementDelegationTokenSeqNum
argument_list|()
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
name|setMasterKeyId
argument_list|(
name|getCurrentKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setSequenceNumber
argument_list|(
name|sequenceNum
argument_list|)
expr_stmt|;
name|identifier
operator|.
name|setMaxDate
argument_list|(
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|+
name|getTokenMaxLifetime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Renew a delegation token.    *    * @param token the token to renew    * @param renewer the full principal name of the user doing the renewal    * @return the new expiration time    * @throws InvalidToken if the token is invalid    * @throws AccessControlException if the user can't renew token    */
annotation|@
name|Override
DECL|method|renewToken (Token<OzoneTokenIdentifier> token, String renewer)
specifier|public
specifier|synchronized
name|long
name|renewToken
parameter_list|(
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|renewer
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
name|OzoneTokenIdentifier
name|id
init|=
name|OzoneTokenIdentifier
operator|.
name|readProtoBuf
argument_list|(
name|in
argument_list|)
decl_stmt|;
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
literal|"Token renewal for identifier: {}, total currentTokens: {}"
argument_list|,
name|formatTokenId
argument_list|(
name|id
argument_list|)
argument_list|,
name|currentTokens
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|now
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
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
name|renewer
operator|+
literal|" tried to renew an expired token "
operator|+
name|formatTokenId
argument_list|(
name|id
argument_list|)
operator|+
literal|" max expiration date: "
operator|+
name|Time
operator|.
name|formatTime
argument_list|(
name|id
operator|.
name|getMaxDate
argument_list|()
argument_list|)
operator|+
literal|" currentTime: "
operator|+
name|Time
operator|.
name|formatTime
argument_list|(
name|now
argument_list|)
argument_list|)
throw|;
block|}
name|validateToken
argument_list|(
name|id
argument_list|)
expr_stmt|;
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
name|renewer
operator|+
literal|" tried to renew a token "
operator|+
name|formatTokenId
argument_list|(
name|id
argument_list|)
operator|+
literal|" without a renewer"
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
name|renewer
operator|+
literal|" tries to renew a token "
operator|+
name|formatTokenId
argument_list|(
name|id
argument_list|)
operator|+
literal|" with non-matching renewer "
operator|+
name|id
operator|.
name|getRenewer
argument_list|()
argument_list|)
throw|;
block|}
name|OzoneSecretKey
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
literal|" from cache. Failed to renew an unexpired token "
operator|+
name|formatTokenId
argument_list|(
name|id
argument_list|)
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
name|getPrivateKey
argument_list|()
argument_list|)
decl_stmt|;
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
name|getTokenRenewInterval
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|addToTokenStore
argument_list|(
name|id
argument_list|,
name|password
argument_list|)
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
literal|"Unable to update token "
operator|+
name|id
operator|.
name|getSequenceNumber
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|renewTime
return|;
block|}
comment|/**    * Cancel a token by removing it from store and cache.    *    * @return Identifier of the canceled token    * @throws InvalidToken for invalid token    * @throws AccessControlException if the user isn't allowed to cancel    */
DECL|method|cancelToken (Token<OzoneTokenIdentifier> token, String canceller)
specifier|public
name|OzoneTokenIdentifier
name|cancelToken
parameter_list|(
name|Token
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|String
name|canceller
parameter_list|)
throws|throws
name|IOException
block|{
name|OzoneTokenIdentifier
name|id
init|=
name|OzoneTokenIdentifier
operator|.
name|readProtoBuf
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Token cancellation requested for identifier: {}"
argument_list|,
name|formatTokenId
argument_list|(
name|id
argument_list|)
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
literal|"Token with no owner "
operator|+
name|formatTokenId
argument_list|(
name|id
argument_list|)
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
literal|" is not authorized to cancel the token "
operator|+
name|formatTokenId
argument_list|(
name|id
argument_list|)
argument_list|)
throw|;
block|}
try|try
block|{
name|store
operator|.
name|removeToken
argument_list|(
name|id
argument_list|)
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
literal|"Unable to remove token "
operator|+
name|id
operator|.
name|getSequenceNumber
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|TokenInfo
name|info
init|=
name|currentTokens
operator|.
name|remove
argument_list|(
name|id
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
literal|"Token not found "
operator|+
name|formatTokenId
argument_list|(
name|id
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|retrievePassword (OzoneTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|OzoneTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|InvalidToken
block|{
return|return
name|validateToken
argument_list|(
name|identifier
argument_list|)
operator|.
name|getPassword
argument_list|()
return|;
block|}
comment|/**    * Checks if TokenInfo for the given identifier exists in database and if the    * token is expired.    */
DECL|method|validateToken (OzoneTokenIdentifier identifier)
specifier|public
name|TokenInfo
name|validateToken
parameter_list|(
name|OzoneTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|TokenInfo
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
literal|"token "
operator|+
name|formatTokenId
argument_list|(
name|identifier
argument_list|)
operator|+
literal|" can't be found in cache"
argument_list|)
throw|;
block|}
name|long
name|now
init|=
name|Time
operator|.
name|monotonicNow
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
literal|"token "
operator|+
name|formatTokenId
argument_list|(
name|identifier
argument_list|)
operator|+
literal|" is "
operator|+
literal|"expired, current time: "
operator|+
name|Time
operator|.
name|formatTime
argument_list|(
name|now
argument_list|)
operator|+
literal|" expected renewal time: "
operator|+
name|Time
operator|.
name|formatTime
argument_list|(
name|info
operator|.
name|getRenewDate
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|verifySignature
argument_list|(
name|identifier
argument_list|,
name|info
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
literal|"Tampered/Invalid token."
argument_list|)
throw|;
block|}
return|return
name|info
return|;
block|}
comment|// TODO: handle roll private key/certificate
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
name|monotonicNow
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
name|OzoneSecretKey
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
name|OzoneSecretKey
argument_list|>
name|e
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|OzoneSecretKey
name|key
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|getExpiryDate
argument_list|()
operator|<
name|now
operator|&&
name|key
operator|.
name|getExpiryDate
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
name|getCurrentKey
argument_list|()
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|store
operator|.
name|removeTokenMasterKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to remove master key "
operator|+
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|loadTokenSecretState ( OzoneManagerSecretState<OzoneTokenIdentifier> state)
specifier|private
name|void
name|loadTokenSecretState
parameter_list|(
name|OzoneManagerSecretState
argument_list|<
name|OzoneTokenIdentifier
argument_list|>
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading token state into token manager."
argument_list|)
expr_stmt|;
for|for
control|(
name|OzoneSecretKey
name|key
range|:
name|state
operator|.
name|ozoneManagerSecretState
argument_list|()
control|)
block|{
name|allKeys
operator|.
name|putIfAbsent
argument_list|(
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|incrementCurrentKeyId
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|OzoneTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|state
operator|.
name|getTokenState
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|addPersistedDelegationToken
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addPersistedDelegationToken ( OzoneTokenIdentifier identifier, long renewDate)
specifier|private
name|void
name|addPersistedDelegationToken
parameter_list|(
name|OzoneTokenIdentifier
name|identifier
parameter_list|,
name|long
name|renewDate
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isRunning
argument_list|()
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
name|OzoneSecretKey
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
name|formatTokenId
argument_list|(
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|PrivateKey
name|privateKey
init|=
name|dKey
operator|.
name|getPrivateKey
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
name|privateKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
operator|>
name|getDelegationTokenSeqNum
argument_list|()
condition|)
block|{
name|setDelegationTokenSeqNum
argument_list|(
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
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
name|TokenInfo
argument_list|(
name|renewDate
argument_list|,
name|password
argument_list|,
name|identifier
operator|.
name|getTrackingId
argument_list|()
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
literal|"Same delegation token being added twice: "
operator|+
name|formatTokenId
argument_list|(
name|identifier
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Should be called before this object is used.    */
annotation|@
name|Override
DECL|method|start (KeyPair keyPair)
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|(
name|KeyPair
name|keyPair
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|start
argument_list|(
name|keyPair
argument_list|)
expr_stmt|;
name|storeKey
argument_list|(
name|getCurrentKey
argument_list|()
argument_list|)
expr_stmt|;
name|removeExpiredKeys
argument_list|()
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
DECL|method|storeKey (OzoneSecretKey key)
specifier|private
name|void
name|storeKey
parameter_list|(
name|OzoneSecretKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|store
operator|.
name|storeTokenMasterKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|allKeys
operator|.
name|containsKey
argument_list|(
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|)
condition|)
block|{
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
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stopping expired delegation token remover thread"
argument_list|)
expr_stmt|;
block|}
name|setIsRunning
argument_list|(
literal|false
argument_list|)
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
comment|/**    * Stops the OzoneDelegationTokenSecretManager.    *    * @throws IOException    */
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
name|stopThreads
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|store
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Remove expired delegation tokens from cache and persisted store.    */
DECL|method|removeExpiredToken ()
specifier|private
name|void
name|removeExpiredToken
parameter_list|()
block|{
name|long
name|now
init|=
name|Time
operator|.
name|monotonicNow
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
name|OzoneTokenIdentifier
argument_list|,
name|TokenInfo
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
name|OzoneTokenIdentifier
argument_list|,
name|TokenInfo
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
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|store
operator|.
name|removeToken
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"Failed to remove expired token {}"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|class|ExpiredTokenRemover
specifier|private
class|class
name|ExpiredTokenRemover
extends|extends
name|Thread
block|{
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
name|getTokenRemoverScanInterval
argument_list|()
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
name|isRunning
argument_list|()
condition|)
block|{
name|long
name|now
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastTokenCacheCleanup
operator|+
name|getTokenRemoverScanInterval
argument_list|()
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
name|getTokenRemoverScanInterval
argument_list|()
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
literal|"ExpiredTokenRemover received "
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
literal|"ExpiredTokenRemover thread received unexpected exception"
argument_list|,
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
DECL|method|getTokenRemoverScanInterval ()
specifier|public
name|long
name|getTokenRemoverScanInterval
parameter_list|()
block|{
return|return
name|tokenRemoverScanInterval
return|;
block|}
block|}
end_class

end_unit

