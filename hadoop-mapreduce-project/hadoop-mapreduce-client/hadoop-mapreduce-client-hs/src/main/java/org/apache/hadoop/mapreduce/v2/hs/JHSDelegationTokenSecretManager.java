begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
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
name|Map
operator|.
name|Entry
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|MRDelegationTokenIdentifier
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|HistoryServerStateStoreService
operator|.
name|HistoryServerState
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
comment|/**  * A MapReduce specific delegation token secret manager.  * The secret manager is responsible for generating and accepting the password  * for each token.  */
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
DECL|class|JHSDelegationTokenSecretManager
specifier|public
class|class
name|JHSDelegationTokenSecretManager
extends|extends
name|AbstractDelegationTokenSecretManager
argument_list|<
name|MRDelegationTokenIdentifier
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
name|JHSDelegationTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|store
specifier|private
name|HistoryServerStateStoreService
name|store
decl_stmt|;
comment|/**    * Create a secret manager    * @param delegationKeyUpdateInterval the number of milliseconds for rolling    *        new secret keys.    * @param delegationTokenMaxLifetime the maximum lifetime of the delegation    *        tokens in milliseconds    * @param delegationTokenRenewInterval how often the tokens must be renewed    *        in milliseconds    * @param delegationTokenRemoverScanInterval how often the tokens are scanned    *        for expired tokens in milliseconds    * @param store history server state store for persisting state    */
DECL|method|JHSDelegationTokenSecretManager (long delegationKeyUpdateInterval, long delegationTokenMaxLifetime, long delegationTokenRenewInterval, long delegationTokenRemoverScanInterval, HistoryServerStateStoreService store)
specifier|public
name|JHSDelegationTokenSecretManager
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
name|HistoryServerStateStoreService
name|store
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
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|MRDelegationTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|MRDelegationTokenIdentifier
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|storeNewMasterKey (DelegationKey key)
specifier|protected
name|void
name|storeNewMasterKey
parameter_list|(
name|DelegationKey
name|key
parameter_list|)
throws|throws
name|IOException
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
literal|"Storing master key "
operator|+
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|storeTokenMasterKey
argument_list|(
name|key
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
literal|"Unable to store master key "
operator|+
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
literal|"Removing master key "
operator|+
name|key
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|e
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
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|storeNewToken (MRDelegationTokenIdentifier tokenId, long renewDate)
specifier|protected
name|void
name|storeNewToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|,
name|long
name|renewDate
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
literal|"Storing token "
operator|+
name|tokenId
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|storeToken
argument_list|(
name|tokenId
argument_list|,
name|renewDate
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
literal|"Unable to store token "
operator|+
name|tokenId
operator|.
name|getSequenceNumber
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeStoredToken (MRDelegationTokenIdentifier tokenId)
specifier|protected
name|void
name|removeStoredToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|)
throws|throws
name|IOException
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
literal|"Storing token "
operator|+
name|tokenId
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|removeToken
argument_list|(
name|tokenId
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
name|tokenId
operator|.
name|getSequenceNumber
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateStoredToken (MRDelegationTokenIdentifier tokenId, long renewDate)
specifier|protected
name|void
name|updateStoredToken
parameter_list|(
name|MRDelegationTokenIdentifier
name|tokenId
parameter_list|,
name|long
name|renewDate
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
literal|"Updating token "
operator|+
name|tokenId
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|updateToken
argument_list|(
name|tokenId
argument_list|,
name|renewDate
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
name|tokenId
operator|.
name|getSequenceNumber
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|recover (HistoryServerState state)
specifier|public
name|void
name|recover
parameter_list|(
name|HistoryServerState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering "
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|DelegationKey
name|key
range|:
name|state
operator|.
name|tokenMasterKeyState
control|)
block|{
name|addKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|MRDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|state
operator|.
name|tokenState
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
block|}
end_class

end_unit

