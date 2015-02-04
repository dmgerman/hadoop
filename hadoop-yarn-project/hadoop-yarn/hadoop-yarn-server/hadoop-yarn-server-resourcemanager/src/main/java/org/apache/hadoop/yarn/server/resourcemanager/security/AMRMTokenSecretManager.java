begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|conf
operator|.
name|Configuration
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|conf
operator|.
name|YarnConfiguration
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
name|AMRMTokenIdentifier
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
name|api
operator|.
name|records
operator|.
name|MasterKey
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
name|records
operator|.
name|AMRMTokenSecretManagerState
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
name|security
operator|.
name|MasterKeyData
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
comment|/**  * AMRM-tokens are per ApplicationAttempt. If users redistribute their  * tokens, it is their headache, god save them. I mean you are not supposed to  * distribute keys to your vault, right? Anyways, ResourceManager saves each  * token locally in memory till application finishes and to a store for restart,  * so no need to remember master-keys even after rolling them.  */
end_comment

begin_class
DECL|class|AMRMTokenSecretManager
specifier|public
class|class
name|AMRMTokenSecretManager
extends|extends
name|SecretManager
argument_list|<
name|AMRMTokenIdentifier
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
name|AMRMTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serialNo
specifier|private
name|int
name|serialNo
init|=
operator|new
name|SecureRandom
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
DECL|field|nextMasterKey
specifier|private
name|MasterKeyData
name|nextMasterKey
decl_stmt|;
DECL|field|currentMasterKey
specifier|private
name|MasterKeyData
name|currentMasterKey
decl_stmt|;
DECL|field|readWriteLock
specifier|private
specifier|final
name|ReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|readLock
specifier|private
specifier|final
name|Lock
name|readLock
init|=
name|readWriteLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
DECL|field|writeLock
specifier|private
specifier|final
name|Lock
name|writeLock
init|=
name|readWriteLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
DECL|field|rollingInterval
specifier|private
specifier|final
name|long
name|rollingInterval
decl_stmt|;
DECL|field|activationDelay
specifier|private
specifier|final
name|long
name|activationDelay
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|appAttemptSet
specifier|private
specifier|final
name|Set
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|appAttemptSet
init|=
operator|new
name|HashSet
argument_list|<
name|ApplicationAttemptId
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Create an {@link AMRMTokenSecretManager}    */
DECL|method|AMRMTokenSecretManager (Configuration conf, RMContext rmContext)
specifier|public
name|AMRMTokenSecretManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|timer
operator|=
operator|new
name|Timer
argument_list|()
expr_stmt|;
name|this
operator|.
name|rollingInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AMRM_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AMRM_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS
argument_list|)
operator|*
literal|1000
expr_stmt|;
comment|// Adding delay = 1.5 * expiry interval makes sure that all active AMs get
comment|// the updated shared-key.
name|this
operator|.
name|activationDelay
operator|=
call|(
name|long
call|)
argument_list|(
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_EXPIRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_AM_EXPIRY_INTERVAL_MS
argument_list|)
operator|*
literal|1.5
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"AMRMTokenKeyRollingInterval: "
operator|+
name|this
operator|.
name|rollingInterval
operator|+
literal|"ms and AMRMTokenKeyActivationDelay: "
operator|+
name|this
operator|.
name|activationDelay
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
if|if
condition|(
name|rollingInterval
operator|<=
name|activationDelay
operator|*
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AMRM_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS
operator|+
literal|" should be more than 3 X "
operator|+
name|YarnConfiguration
operator|.
name|RM_AM_EXPIRY_INTERVAL_MS
argument_list|)
throw|;
block|}
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|currentMasterKey
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|currentMasterKey
operator|=
name|createNewMasterKey
argument_list|()
expr_stmt|;
name|AMRMTokenSecretManagerState
name|state
init|=
name|AMRMTokenSecretManagerState
operator|.
name|newInstance
argument_list|(
name|this
operator|.
name|currentMasterKey
operator|.
name|getMasterKey
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rmContext
operator|.
name|getStateStore
argument_list|()
operator|.
name|storeOrUpdateAMRMTokenSecretManager
argument_list|(
name|state
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|timer
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|MasterKeyRoller
argument_list|()
argument_list|,
name|rollingInterval
argument_list|,
name|rollingInterval
argument_list|)
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|this
operator|.
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
DECL|method|applicationMasterFinished (ApplicationAttemptId appAttemptId)
specifier|public
name|void
name|applicationMasterFinished
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Application finished, removing password for "
operator|+
name|appAttemptId
argument_list|)
expr_stmt|;
name|this
operator|.
name|appAttemptSet
operator|.
name|remove
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|MasterKeyRoller
specifier|private
class|class
name|MasterKeyRoller
extends|extends
name|TimerTask
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|rollMasterKey
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Private
DECL|method|rollMasterKey ()
name|void
name|rollMasterKey
parameter_list|()
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Rolling master-key for amrm-tokens"
argument_list|)
expr_stmt|;
name|this
operator|.
name|nextMasterKey
operator|=
name|createNewMasterKey
argument_list|()
expr_stmt|;
name|AMRMTokenSecretManagerState
name|state
init|=
name|AMRMTokenSecretManagerState
operator|.
name|newInstance
argument_list|(
name|this
operator|.
name|currentMasterKey
operator|.
name|getMasterKey
argument_list|()
argument_list|,
name|this
operator|.
name|nextMasterKey
operator|.
name|getMasterKey
argument_list|()
argument_list|)
decl_stmt|;
name|rmContext
operator|.
name|getStateStore
argument_list|()
operator|.
name|storeOrUpdateAMRMTokenSecretManager
argument_list|(
name|state
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|timer
operator|.
name|schedule
argument_list|(
operator|new
name|NextKeyActivator
argument_list|()
argument_list|,
name|this
operator|.
name|activationDelay
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|NextKeyActivator
specifier|private
class|class
name|NextKeyActivator
extends|extends
name|TimerTask
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|activateNextMasterKey
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|activateNextMasterKey ()
specifier|public
name|void
name|activateNextMasterKey
parameter_list|()
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Activating next master key with id: "
operator|+
name|this
operator|.
name|nextMasterKey
operator|.
name|getMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentMasterKey
operator|=
name|this
operator|.
name|nextMasterKey
expr_stmt|;
name|this
operator|.
name|nextMasterKey
operator|=
literal|null
expr_stmt|;
name|AMRMTokenSecretManagerState
name|state
init|=
name|AMRMTokenSecretManagerState
operator|.
name|newInstance
argument_list|(
name|this
operator|.
name|currentMasterKey
operator|.
name|getMasterKey
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|rmContext
operator|.
name|getStateStore
argument_list|()
operator|.
name|storeOrUpdateAMRMTokenSecretManager
argument_list|(
name|state
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|createNewMasterKey ()
specifier|public
name|MasterKeyData
name|createNewMasterKey
parameter_list|()
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|new
name|MasterKeyData
argument_list|(
name|serialNo
operator|++
argument_list|,
name|generateSecret
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createAndGetAMRMToken ( ApplicationAttemptId appAttemptId)
specifier|public
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|createAndGetAMRMToken
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create AMRMToken for ApplicationAttempt: "
operator|+
name|appAttemptId
argument_list|)
expr_stmt|;
name|AMRMTokenIdentifier
name|identifier
init|=
operator|new
name|AMRMTokenIdentifier
argument_list|(
name|appAttemptId
argument_list|,
name|getMasterKey
argument_list|()
operator|.
name|getMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|password
init|=
name|this
operator|.
name|createPassword
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
name|appAttemptSet
operator|.
name|add
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
return|return
operator|new
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
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
operator|new
name|Text
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|// If nextMasterKey is not Null, then return nextMasterKey
comment|// otherwise return currentMasterKey
annotation|@
name|VisibleForTesting
DECL|method|getMasterKey ()
specifier|public
name|MasterKeyData
name|getMasterKey
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|nextMasterKey
operator|==
literal|null
condition|?
name|currentMasterKey
else|:
name|nextMasterKey
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Populate persisted password of AMRMToken back to AMRMTokenSecretManager.    */
DECL|method|addPersistedPassword (Token<AMRMTokenIdentifier> token)
specifier|public
name|void
name|addPersistedPassword
parameter_list|(
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|AMRMTokenIdentifier
name|identifier
init|=
name|token
operator|.
name|decodeIdentifier
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding password for "
operator|+
name|identifier
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|appAttemptSet
operator|.
name|add
argument_list|(
name|identifier
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Retrieve the password for the given {@link AMRMTokenIdentifier}.    * Used by RPC layer to validate a remote {@link AMRMTokenIdentifier}.    */
annotation|@
name|Override
DECL|method|retrievePassword (AMRMTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|AMRMTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|InvalidToken
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|identifier
operator|.
name|getApplicationAttemptId
argument_list|()
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
literal|"Trying to retrieve password for "
operator|+
name|applicationAttemptId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|appAttemptSet
operator|.
name|contains
argument_list|(
name|applicationAttemptId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidToken
argument_list|(
name|applicationAttemptId
operator|+
literal|" not found in AMRMTokenSecretManager."
argument_list|)
throw|;
block|}
if|if
condition|(
name|identifier
operator|.
name|getKeyId
argument_list|()
operator|==
name|this
operator|.
name|currentMasterKey
operator|.
name|getMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
condition|)
block|{
return|return
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|this
operator|.
name|currentMasterKey
operator|.
name|getSecretKey
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|nextMasterKey
operator|!=
literal|null
operator|&&
name|identifier
operator|.
name|getKeyId
argument_list|()
operator|==
name|this
operator|.
name|nextMasterKey
operator|.
name|getMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
condition|)
block|{
return|return
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|this
operator|.
name|nextMasterKey
operator|.
name|getSecretKey
argument_list|()
argument_list|)
return|;
block|}
throw|throw
operator|new
name|InvalidToken
argument_list|(
literal|"Invalid AMRMToken from "
operator|+
name|applicationAttemptId
argument_list|)
throw|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Creates an empty TokenId to be used for de-serializing an    * {@link AMRMTokenIdentifier} by the RPC layer.    */
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|AMRMTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|AMRMTokenIdentifier
argument_list|()
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getCurrnetMasterKeyData ()
specifier|public
name|MasterKeyData
name|getCurrnetMasterKeyData
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|currentMasterKey
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getNextMasterKeyData ()
specifier|public
name|MasterKeyData
name|getNextMasterKeyData
parameter_list|()
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|this
operator|.
name|nextMasterKey
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Private
DECL|method|createPassword (AMRMTokenIdentifier identifier)
specifier|protected
name|byte
index|[]
name|createPassword
parameter_list|(
name|AMRMTokenIdentifier
name|identifier
parameter_list|)
block|{
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|identifier
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating password for "
operator|+
name|applicationAttemptId
argument_list|)
expr_stmt|;
return|return
name|createPassword
argument_list|(
name|identifier
operator|.
name|getBytes
argument_list|()
argument_list|,
name|getMasterKey
argument_list|()
operator|.
name|getSecretKey
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|this
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|recover (RMState state)
specifier|public
name|void
name|recover
parameter_list|(
name|RMState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|.
name|getAMRMTokenSecretManagerState
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// recover the current master key
name|MasterKey
name|currentKey
init|=
name|state
operator|.
name|getAMRMTokenSecretManagerState
argument_list|()
operator|.
name|getCurrentMasterKey
argument_list|()
decl_stmt|;
name|this
operator|.
name|currentMasterKey
operator|=
operator|new
name|MasterKeyData
argument_list|(
name|currentKey
argument_list|,
name|createSecretKey
argument_list|(
name|currentKey
operator|.
name|getBytes
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// recover the next master key if not null
name|MasterKey
name|nextKey
init|=
name|state
operator|.
name|getAMRMTokenSecretManagerState
argument_list|()
operator|.
name|getNextMasterKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextKey
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nextMasterKey
operator|=
operator|new
name|MasterKeyData
argument_list|(
name|nextKey
argument_list|,
name|createSecretKey
argument_list|(
name|nextKey
operator|.
name|getBytes
argument_list|()
operator|.
name|array
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|timer
operator|.
name|schedule
argument_list|(
operator|new
name|NextKeyActivator
argument_list|()
argument_list|,
name|this
operator|.
name|activationDelay
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

