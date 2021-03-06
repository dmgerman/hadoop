begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|RequestSource
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
name|RPC
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
comment|/**  * The FailOverController is responsible for electing an active service  * on startup or when the current active is changing (eg due to failure),  * monitoring the health of a service, and performing a fail-over when a  * new active service is either manually selected by a user or elected.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|FailoverController
specifier|public
class|class
name|FailoverController
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
name|FailoverController
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|gracefulFenceTimeout
specifier|private
specifier|final
name|int
name|gracefulFenceTimeout
decl_stmt|;
DECL|field|rpcTimeoutToNewActive
specifier|private
specifier|final
name|int
name|rpcTimeoutToNewActive
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
comment|/*    * Need a copy of conf for graceful fence to set     * configurable retries for IPC client.    * Refer HDFS-3561    */
DECL|field|gracefulFenceConf
specifier|private
specifier|final
name|Configuration
name|gracefulFenceConf
decl_stmt|;
DECL|field|requestSource
specifier|private
specifier|final
name|RequestSource
name|requestSource
decl_stmt|;
DECL|method|FailoverController (Configuration conf, RequestSource source)
specifier|public
name|FailoverController
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RequestSource
name|source
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|gracefulFenceConf
operator|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|requestSource
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|gracefulFenceTimeout
operator|=
name|getGracefulFenceTimeout
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|rpcTimeoutToNewActive
operator|=
name|getRpcTimeoutToNewActive
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//Configure less retries for graceful fence
name|int
name|gracefulFenceConnectRetries
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HA_FC_GRACEFUL_FENCE_CONNECTION_RETRIES
argument_list|,
name|CommonConfigurationKeys
operator|.
name|HA_FC_GRACEFUL_FENCE_CONNECTION_RETRIES_DEFAULT
argument_list|)
decl_stmt|;
name|gracefulFenceConf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
name|gracefulFenceConnectRetries
argument_list|)
expr_stmt|;
name|gracefulFenceConf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_ON_SOCKET_TIMEOUTS_KEY
argument_list|,
name|gracefulFenceConnectRetries
argument_list|)
expr_stmt|;
block|}
DECL|method|getGracefulFenceTimeout (Configuration conf)
specifier|static
name|int
name|getGracefulFenceTimeout
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HA_FC_GRACEFUL_FENCE_TIMEOUT_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|HA_FC_GRACEFUL_FENCE_TIMEOUT_DEFAULT
argument_list|)
return|;
block|}
DECL|method|getRpcTimeoutToNewActive (Configuration conf)
specifier|static
name|int
name|getRpcTimeoutToNewActive
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HA_FC_NEW_ACTIVE_TIMEOUT_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|HA_FC_NEW_ACTIVE_TIMEOUT_DEFAULT
argument_list|)
return|;
block|}
comment|/**    * Perform pre-failover checks on the given service we plan to    * failover to, eg to prevent failing over to a service (eg due    * to it being inaccessible, already active, not healthy, etc).    *    * An option to ignore toSvc if it claims it is not ready to    * become active is provided in case performing a failover will    * allow it to become active, eg because it triggers a log roll    * so the standby can learn about new blocks and leave safemode.    *    * @param from currently active service    * @param target service to make active    * @param forceActive ignore toSvc if it reports that it is not ready    * @throws FailoverFailedException if we should avoid failover    */
DECL|method|preFailoverChecks (HAServiceTarget from, HAServiceTarget target, boolean forceActive)
specifier|private
name|void
name|preFailoverChecks
parameter_list|(
name|HAServiceTarget
name|from
parameter_list|,
name|HAServiceTarget
name|target
parameter_list|,
name|boolean
name|forceActive
parameter_list|)
throws|throws
name|FailoverFailedException
block|{
name|HAServiceStatus
name|toSvcStatus
decl_stmt|;
name|HAServiceProtocol
name|toSvc
decl_stmt|;
if|if
condition|(
name|from
operator|.
name|getAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|target
operator|.
name|getAddress
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FailoverFailedException
argument_list|(
literal|"Can't failover a service to itself"
argument_list|)
throw|;
block|}
try|try
block|{
name|toSvc
operator|=
name|target
operator|.
name|getProxy
argument_list|(
name|conf
argument_list|,
name|rpcTimeoutToNewActive
argument_list|)
expr_stmt|;
name|toSvcStatus
operator|=
name|toSvc
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Unable to get service state for "
operator|+
name|target
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|FailoverFailedException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|toSvcStatus
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FailoverFailedException
argument_list|(
literal|"Can't failover to an "
operator|+
name|toSvcStatus
operator|.
name|getState
argument_list|()
operator|+
literal|" service"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|toSvcStatus
operator|.
name|isReadyToBecomeActive
argument_list|()
condition|)
block|{
name|String
name|notReadyReason
init|=
name|toSvcStatus
operator|.
name|getNotReadyReason
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|forceActive
condition|)
block|{
throw|throw
operator|new
name|FailoverFailedException
argument_list|(
name|target
operator|+
literal|" is not ready to become active: "
operator|+
name|notReadyReason
argument_list|)
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Service is not ready to become active, but forcing: {}"
argument_list|,
name|notReadyReason
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|HAServiceProtocolHelper
operator|.
name|monitorHealth
argument_list|(
name|toSvc
argument_list|,
name|createReqInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HealthCheckFailedException
name|hce
parameter_list|)
block|{
throw|throw
operator|new
name|FailoverFailedException
argument_list|(
literal|"Can't failover to an unhealthy service"
argument_list|,
name|hce
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FailoverFailedException
argument_list|(
literal|"Got an IO exception"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|createReqInfo ()
specifier|private
name|StateChangeRequestInfo
name|createReqInfo
parameter_list|()
block|{
return|return
operator|new
name|StateChangeRequestInfo
argument_list|(
name|requestSource
argument_list|)
return|;
block|}
comment|/**    * Try to get the HA state of the node at the given address. This    * function is guaranteed to be "quick" -- ie it has a short timeout    * and no retries. Its only purpose is to avoid fencing a node that    * has already restarted.    */
DECL|method|tryGracefulFence (HAServiceTarget svc)
name|boolean
name|tryGracefulFence
parameter_list|(
name|HAServiceTarget
name|svc
parameter_list|)
block|{
name|HAServiceProtocol
name|proxy
init|=
literal|null
decl_stmt|;
try|try
block|{
name|proxy
operator|=
name|svc
operator|.
name|getProxy
argument_list|(
name|gracefulFenceConf
argument_list|,
name|gracefulFenceTimeout
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|transitionToStandby
argument_list|(
name|createReqInfo
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|ServiceFailedException
name|sfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to gracefully make {} standby ({})"
argument_list|,
name|svc
argument_list|,
name|sfe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to gracefully make {} standby (unable to connect)"
argument_list|,
name|svc
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|proxy
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Failover from service 1 to service 2. If the failover fails    * then try to failback.    *    * @param fromSvc currently active service    * @param toSvc service to make active    * @param forceFence to fence fromSvc even if not strictly necessary    * @param forceActive try to make toSvc active even if it is not ready    * @throws FailoverFailedException if the failover fails    */
DECL|method|failover (HAServiceTarget fromSvc, HAServiceTarget toSvc, boolean forceFence, boolean forceActive)
specifier|public
name|void
name|failover
parameter_list|(
name|HAServiceTarget
name|fromSvc
parameter_list|,
name|HAServiceTarget
name|toSvc
parameter_list|,
name|boolean
name|forceFence
parameter_list|,
name|boolean
name|forceActive
parameter_list|)
throws|throws
name|FailoverFailedException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fromSvc
operator|.
name|getFencer
argument_list|()
operator|!=
literal|null
argument_list|,
literal|"failover requires a fencer"
argument_list|)
expr_stmt|;
name|preFailoverChecks
argument_list|(
name|fromSvc
argument_list|,
name|toSvc
argument_list|,
name|forceActive
argument_list|)
expr_stmt|;
comment|// Try to make fromSvc standby
name|boolean
name|tryFence
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|tryGracefulFence
argument_list|(
name|fromSvc
argument_list|)
condition|)
block|{
name|tryFence
operator|=
name|forceFence
expr_stmt|;
block|}
comment|// Fence fromSvc if it's required or forced by the user
if|if
condition|(
name|tryFence
condition|)
block|{
if|if
condition|(
operator|!
name|fromSvc
operator|.
name|getFencer
argument_list|()
operator|.
name|fence
argument_list|(
name|fromSvc
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FailoverFailedException
argument_list|(
literal|"Unable to fence "
operator|+
name|fromSvc
operator|+
literal|". Fencing failed."
argument_list|)
throw|;
block|}
block|}
comment|// Try to make toSvc active
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
name|Throwable
name|cause
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HAServiceProtocolHelper
operator|.
name|transitionToActive
argument_list|(
name|toSvc
operator|.
name|getProxy
argument_list|(
name|conf
argument_list|,
name|rpcTimeoutToNewActive
argument_list|)
argument_list|,
name|createReqInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceFailedException
name|sfe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to make {} active ({}). Failing back."
argument_list|,
name|toSvc
argument_list|,
name|sfe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
name|cause
operator|=
name|sfe
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to make {} active (unable to connect). Failing back."
argument_list|,
name|toSvc
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
name|cause
operator|=
name|ioe
expr_stmt|;
block|}
comment|// We failed to make toSvc active
if|if
condition|(
name|failed
condition|)
block|{
name|String
name|msg
init|=
literal|"Unable to failover to "
operator|+
name|toSvc
decl_stmt|;
comment|// Only try to failback if we didn't fence fromSvc
if|if
condition|(
operator|!
name|tryFence
condition|)
block|{
try|try
block|{
comment|// Unconditionally fence toSvc in case it is still trying to
comment|// become active, eg we timed out waiting for its response.
comment|// Unconditionally force fromSvc to become active since it
comment|// was previously active when we initiated failover.
name|failover
argument_list|(
name|toSvc
argument_list|,
name|fromSvc
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailoverFailedException
name|ffe
parameter_list|)
block|{
name|msg
operator|+=
literal|". Failback to "
operator|+
name|fromSvc
operator|+
literal|" failed ("
operator|+
name|ffe
operator|.
name|getMessage
argument_list|()
operator|+
literal|")"
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|FailoverFailedException
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

