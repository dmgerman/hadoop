begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.security
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
name|security
package|;
end_package

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
name|ContainerTokenIdentifier
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

begin_comment
comment|/**  * SecretManager for ContainerTokens. Extended by both RM and NM and hence is  * present in yarn-server-common package.  *   */
end_comment

begin_class
DECL|class|BaseContainerTokenSecretManager
specifier|public
class|class
name|BaseContainerTokenSecretManager
extends|extends
name|SecretManager
argument_list|<
name|ContainerTokenIdentifier
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
name|BaseContainerTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serialNo
specifier|protected
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
DECL|field|readWriteLock
specifier|protected
specifier|final
name|ReadWriteLock
name|readWriteLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|readLock
specifier|protected
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
specifier|protected
specifier|final
name|Lock
name|writeLock
init|=
name|readWriteLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
comment|/**    * THE masterKey. ResourceManager should persist this and recover it on    * restart instead of generating a new key. The NodeManagers get it from the    * ResourceManager and use it for validating container-tokens.    */
DECL|field|currentMasterKey
specifier|protected
name|MasterKeyData
name|currentMasterKey
decl_stmt|;
DECL|field|containerTokenExpiryInterval
specifier|protected
specifier|final
name|long
name|containerTokenExpiryInterval
decl_stmt|;
DECL|method|BaseContainerTokenSecretManager (Configuration conf)
specifier|public
name|BaseContainerTokenSecretManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|containerTokenExpiryInterval
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS
argument_list|)
expr_stmt|;
block|}
comment|// Need lock as we increment serialNo etc.
DECL|method|createNewMasterKey ()
specifier|protected
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
annotation|@
name|Private
DECL|method|getCurrentKey ()
specifier|public
name|MasterKey
name|getCurrentKey
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
operator|.
name|getMasterKey
argument_list|()
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
DECL|method|createPassword (ContainerTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|createPassword
parameter_list|(
name|ContainerTokenIdentifier
name|identifier
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating password for {} for user {} to be run on NM {}"
argument_list|,
name|identifier
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|identifier
operator|.
name|getUser
argument_list|()
argument_list|,
name|identifier
operator|.
name|getNmHostAddress
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|retrievePassword (ContainerTokenIdentifier identifier)
specifier|public
name|byte
index|[]
name|retrievePassword
parameter_list|(
name|ContainerTokenIdentifier
name|identifier
parameter_list|)
throws|throws
name|SecretManager
operator|.
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
return|return
name|retrievePasswordInternal
argument_list|(
name|identifier
argument_list|,
name|this
operator|.
name|currentMasterKey
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
DECL|method|retrievePasswordInternal (ContainerTokenIdentifier identifier, MasterKeyData masterKey)
specifier|protected
name|byte
index|[]
name|retrievePasswordInternal
parameter_list|(
name|ContainerTokenIdentifier
name|identifier
parameter_list|,
name|MasterKeyData
name|masterKey
parameter_list|)
throws|throws
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
operator|.
name|InvalidToken
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Retrieving password for {} for user {} to be run on NM {}"
argument_list|,
name|identifier
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|identifier
operator|.
name|getUser
argument_list|()
argument_list|,
name|identifier
operator|.
name|getNmHostAddress
argument_list|()
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
name|masterKey
operator|.
name|getSecretKey
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Used by the RPC layer.    */
annotation|@
name|Override
DECL|method|createIdentifier ()
specifier|public
name|ContainerTokenIdentifier
name|createIdentifier
parameter_list|()
block|{
return|return
operator|new
name|ContainerTokenIdentifier
argument_list|()
return|;
block|}
block|}
end_class

end_unit

