begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|security
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
name|InterfaceAudience
operator|.
name|Private
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
name|ExitUtil
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
name|yarn
operator|.
name|security
operator|.
name|client
operator|.
name|RMDelegationTokenIdentifier
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|ResourceManager
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|RMStateStore
operator|.
name|RMState
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|recovery
operator|.
name|Recoverable
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

begin_comment
comment|/**  * A ResourceManager specific delegation token secret manager.  * The secret manager is responsible for generating and accepting the password  * for each token.  */
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
DECL|class|RMDelegationTokenSecretManager
specifier|public
class|class
name|RMDelegationTokenSecretManager
extends|extends
name|AbstractDelegationTokenSecretManager
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|>
implements|implements
name|Recoverable
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
name|RMDelegationTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rm
specifier|private
specifier|final
name|ResourceManager
name|rm
decl_stmt|;
comment|/**    * Create a secret manager    * @param delegationKeyUpdateInterval the number of milliseconds for rolling    *        new secret keys.    * @param delegationTokenMaxLifetime the maximum lifetime of the delegation    *        tokens in milliseconds    * @param delegationTokenRenewInterval how often the tokens must be renewed    *        in milliseconds    * @param delegationTokenRemoverScanInterval how often the tokens are scanned    *        for expired tokens in milliseconds    * @param rmContext current context of the ResourceManager    */
DECL|method|RMDelegationTokenSecretManager (long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval, RMContext rmContext)
specifier|public
name|RMDelegationTokenSecretManager
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
name|RMContext
name|rmContext
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
name|rm
operator|=
name|rmContext
operator|.
name|getResourceManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|RMDelegationTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|RMDelegationTokenIdentifier
argument_list|()
return|;
block|}
DECL|method|shouldIgnoreException (Exception e)
specifier|private
name|boolean
name|shouldIgnoreException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|!
name|running
operator|&&
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InterruptedException
return|;
block|}
annotation|@
name|Override
DECL|method|storeNewMasterKey (DelegationKey newKey)
specifier|protected
name|void
name|storeNewMasterKey
parameter_list|(
name|DelegationKey
name|newKey
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"storing master key with keyID "
operator|+
name|newKey
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getStateStore
argument_list|()
operator|.
name|storeRMDTMasterKey
argument_list|(
name|newKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shouldIgnoreException
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in storing master key with KeyID: "
operator|+
name|newKey
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|removeStoredMasterKey (DelegationKey key)
specifier|protected
name|void
name|removeStoredMasterKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"removing master key with keyID "
operator|+
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getStateStore
argument_list|()
operator|.
name|removeRMDTMasterKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shouldIgnoreException
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in removing master key with KeyID: "
operator|+
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|storeNewToken (RMDelegationTokenIdentifier identifier, long renewDate)
specifier|protected
name|void
name|storeNewToken
parameter_list|(
name|RMDelegationTokenIdentifier
name|identifier
parameter_list|,
name|long
name|renewDate
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"storing RMDelegation token with sequence number: "
operator|+
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getStateStore
argument_list|()
operator|.
name|storeRMDelegationToken
argument_list|(
name|identifier
argument_list|,
name|renewDate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shouldIgnoreException
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in storing RMDelegationToken with sequence number: "
operator|+
name|identifier
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|updateStoredToken (RMDelegationTokenIdentifier id, long renewDate)
specifier|protected
name|void
name|updateStoredToken
parameter_list|(
name|RMDelegationTokenIdentifier
name|id
parameter_list|,
name|long
name|renewDate
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"updating RMDelegation token with sequence number: "
operator|+
name|id
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getStateStore
argument_list|()
operator|.
name|updateRMDelegationToken
argument_list|(
name|id
argument_list|,
name|renewDate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shouldIgnoreException
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in updating persisted RMDelegationToken"
operator|+
literal|" with sequence number: "
operator|+
name|id
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|removeStoredToken (RMDelegationTokenIdentifier ident)
specifier|protected
name|void
name|removeStoredToken
parameter_list|(
name|RMDelegationTokenIdentifier
name|ident
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"removing RMDelegation token with sequence number: "
operator|+
name|ident
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getStateStore
argument_list|()
operator|.
name|removeRMDelegationToken
argument_list|(
name|ident
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shouldIgnoreException
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error in removing RMDelegationToken with sequence number: "
operator|+
name|ident
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getAllMasterKeys ()
specifier|public
specifier|synchronized
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|getAllMasterKeys
parameter_list|()
block|{
name|HashSet
argument_list|<
name|DelegationKey
argument_list|>
name|keySet
init|=
operator|new
name|HashSet
argument_list|<
name|DelegationKey
argument_list|>
argument_list|()
decl_stmt|;
name|keySet
operator|.
name|addAll
argument_list|(
name|allKeys
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|keySet
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getAllTokens ()
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|getAllTokens
parameter_list|()
block|{
name|Map
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|allTokens
init|=
operator|new
name|HashMap
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|DelegationTokenInformation
argument_list|>
name|entry
range|:
name|currentTokens
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|allTokens
operator|.
name|put
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
operator|.
name|getRenewDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|allTokens
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getLatestDTSequenceNumber ()
specifier|public
name|int
name|getLatestDTSequenceNumber
parameter_list|()
block|{
return|return
name|delegationTokenSequenceNumber
return|;
block|}
annotation|@
name|Override
DECL|method|recover (RMState rmState)
specifier|public
name|void
name|recover
parameter_list|(
name|RMState
name|rmState
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"recovering RMDelegationTokenSecretManager."
argument_list|)
expr_stmt|;
comment|// recover RMDTMasterKeys
for|for
control|(
name|DelegationKey
name|dtKey
range|:
name|rmState
operator|.
name|getRMDTSecretManagerState
argument_list|()
operator|.
name|getMasterKeyState
argument_list|()
control|)
block|{
name|addKey
argument_list|(
name|dtKey
argument_list|)
expr_stmt|;
block|}
comment|// recover RMDelegationTokens
name|Map
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|rmDelegationTokens
init|=
name|rmState
operator|.
name|getRMDTSecretManagerState
argument_list|()
operator|.
name|getTokenState
argument_list|()
decl_stmt|;
name|this
operator|.
name|delegationTokenSequenceNumber
operator|=
name|rmState
operator|.
name|getRMDTSecretManagerState
argument_list|()
operator|.
name|getDTSequenceNumber
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|rmDelegationTokens
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
DECL|method|getRenewDate (RMDelegationTokenIdentifier ident)
specifier|public
name|long
name|getRenewDate
parameter_list|(
name|RMDelegationTokenIdentifier
name|ident
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
name|ident
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
name|ident
operator|.
name|toString
argument_list|()
operator|+
literal|") can't be found in cache"
argument_list|)
throw|;
block|}
return|return
name|info
operator|.
name|getRenewDate
argument_list|()
return|;
block|}
block|}
end_class

end_unit

