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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|LogAggregationContext
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
name|NodeId
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
name|Priority
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
name|Resource
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
name|security
operator|.
name|BaseContainerTokenSecretManager
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
name|utils
operator|.
name|BuilderUtils
import|;
end_import

begin_comment
comment|/**  * SecretManager for ContainerTokens. This is RM-specific and rolls the  * master-keys every so often.  *   */
end_comment

begin_class
DECL|class|RMContainerTokenSecretManager
specifier|public
class|class
name|RMContainerTokenSecretManager
extends|extends
name|BaseContainerTokenSecretManager
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RMContainerTokenSecretManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nextMasterKey
specifier|private
name|MasterKeyData
name|nextMasterKey
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
DECL|method|RMContainerTokenSecretManager (Configuration conf)
specifier|public
name|RMContainerTokenSecretManager
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
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
name|RM_CONTAINER_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CONTAINER_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS
argument_list|)
operator|*
literal|1000
expr_stmt|;
comment|// Add an activation delay. This is to address the following race: RM may
comment|// roll over master-key, scheduling may happen at some point of time, a
comment|// container created with a password generated off new master key, but NM
comment|// might not have come again to RM to update the shared secret: so AM has a
comment|// valid password generated off new secret but NM doesn't know about the
comment|// secret yet.
comment|// Adding delay = 1.5 * expiry interval makes sure that all active NMs get
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
name|RM_NM_EXPIRY_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NM_EXPIRY_INTERVAL_MS
argument_list|)
operator|*
literal|1.5
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ContainerTokenKeyRollingInterval: "
operator|+
name|this
operator|.
name|rollingInterval
operator|+
literal|"ms and ContainerTokenKeyActivationDelay: "
operator|+
name|this
operator|.
name|activationDelay
operator|+
literal|"ms"
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
name|RM_CONTAINER_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS
operator|+
literal|" should be more than 3 X "
operator|+
name|YarnConfiguration
operator|.
name|RM_NM_EXPIRY_INTERVAL_MS
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
name|rollMasterKey
argument_list|()
expr_stmt|;
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
comment|/**    * Creates a new master-key and sets it as the primary.    */
annotation|@
name|Private
DECL|method|rollMasterKey ()
specifier|public
name|void
name|rollMasterKey
parameter_list|()
block|{
name|super
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
literal|"Rolling master-key for container-tokens"
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|currentMasterKey
operator|==
literal|null
condition|)
block|{
comment|// Setting up for the first time.
name|this
operator|.
name|currentMasterKey
operator|=
name|createNewMasterKey
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|nextMasterKey
operator|=
name|createNewMasterKey
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Going to activate master-key with key-id "
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
operator|+
literal|" in "
operator|+
name|this
operator|.
name|activationDelay
operator|+
literal|"ms"
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
finally|finally
block|{
name|super
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
DECL|method|getNextKey ()
specifier|public
name|MasterKey
name|getNextKey
parameter_list|()
block|{
name|super
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|this
operator|.
name|nextMasterKey
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|nextMasterKey
operator|.
name|getMasterKey
argument_list|()
return|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Activate the new master-key    */
annotation|@
name|Private
DECL|method|activateNextMasterKey ()
specifier|public
name|void
name|activateNextMasterKey
parameter_list|()
block|{
name|super
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
block|}
finally|finally
block|{
name|super
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
comment|// Activation will happen after an absolute time interval. It will be good
comment|// if we can force activation after an NM updates and acknowledges a
comment|// roll-over. But that is only possible when we move to per-NM keys. TODO:
name|activateNextMasterKey
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Helper function for creating ContainerTokens    *     * @param containerId    * @param nodeId    * @param appSubmitter    * @param capability    * @param priority    * @param createTime    * @return the container-token    */
DECL|method|createContainerToken (ContainerId containerId, NodeId nodeId, String appSubmitter, Resource capability, Priority priority, long createTime)
specifier|public
name|Token
name|createContainerToken
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|appSubmitter
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|createTime
parameter_list|)
block|{
return|return
name|createContainerToken
argument_list|(
name|containerId
argument_list|,
name|nodeId
argument_list|,
name|appSubmitter
argument_list|,
name|capability
argument_list|,
name|priority
argument_list|,
name|createTime
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Helper function for creating ContainerTokens    *     * @param containerId    * @param nodeId    * @param appSubmitter    * @param capability    * @param priority    * @param createTime    * @param logAggregationContext    * @return the container-token    */
DECL|method|createContainerToken (ContainerId containerId, NodeId nodeId, String appSubmitter, Resource capability, Priority priority, long createTime, LogAggregationContext logAggregationContext, String nodeLabelExpression)
specifier|public
name|Token
name|createContainerToken
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|appSubmitter
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|long
name|createTime
parameter_list|,
name|LogAggregationContext
name|logAggregationContext
parameter_list|,
name|String
name|nodeLabelExpression
parameter_list|)
block|{
name|byte
index|[]
name|password
decl_stmt|;
name|ContainerTokenIdentifier
name|tokenIdentifier
decl_stmt|;
name|long
name|expiryTimeStamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|containerTokenExpiryInterval
decl_stmt|;
comment|// Lock so that we use the same MasterKey's keyId and its bytes
name|this
operator|.
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|tokenIdentifier
operator|=
operator|new
name|ContainerTokenIdentifier
argument_list|(
name|containerId
argument_list|,
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|,
name|appSubmitter
argument_list|,
name|capability
argument_list|,
name|expiryTimeStamp
argument_list|,
name|this
operator|.
name|currentMasterKey
operator|.
name|getMasterKey
argument_list|()
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|ResourceManager
operator|.
name|getClusterTimeStamp
argument_list|()
argument_list|,
name|priority
argument_list|,
name|createTime
argument_list|,
name|logAggregationContext
argument_list|,
name|nodeLabelExpression
argument_list|)
expr_stmt|;
name|password
operator|=
name|this
operator|.
name|createPassword
argument_list|(
name|tokenIdentifier
argument_list|)
expr_stmt|;
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
return|return
name|BuilderUtils
operator|.
name|newContainerToken
argument_list|(
name|nodeId
argument_list|,
name|password
argument_list|,
name|tokenIdentifier
argument_list|)
return|;
block|}
block|}
end_class

end_unit

