begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.amrmproxy
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
name|nodemanager
operator|.
name|amrmproxy
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
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Joiner
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
name|TokenIdentifier
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
name|ApplicationMasterProtocol
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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|protocolrecords
operator|.
name|AllocateResponse
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
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|protocolrecords
operator|.
name|FinishApplicationMasterResponse
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
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
name|client
operator|.
name|ClientRMProxy
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
name|HAUtil
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
name|exceptions
operator|.
name|YarnException
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|DistributedSchedulerProtocol
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
name|ServerRMProxy
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
name|protocolrecords
operator|.
name|DistSchedAllocateResponse
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
name|protocolrecords
operator|.
name|DistSchedRegisterResponse
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
comment|/**  * Extends the AbstractRequestInterceptor class and provides an implementation  * that simply forwards the AM requests to the cluster resource manager.  *  */
end_comment

begin_class
DECL|class|DefaultRequestInterceptor
specifier|public
specifier|final
class|class
name|DefaultRequestInterceptor
extends|extends
name|AbstractRequestInterceptor
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
name|DefaultRequestInterceptor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rmClient
specifier|private
name|DistributedSchedulerProtocol
name|rmClient
decl_stmt|;
DECL|field|user
specifier|private
name|UserGroupInformation
name|user
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init (AMRMProxyApplicationContext appContext)
specifier|public
name|void
name|init
parameter_list|(
name|AMRMProxyApplicationContext
name|appContext
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|appContext
argument_list|)
expr_stmt|;
try|try
block|{
name|user
operator|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|appContext
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
name|user
operator|.
name|addToken
argument_list|(
name|appContext
operator|.
name|getAMRMToken
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|conf
init|=
name|this
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|rmClient
operator|=
name|user
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|DistributedSchedulerProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DistributedSchedulerProtocol
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|setAMRMTokenService
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|ServerRMProxy
operator|.
name|createRMProxy
argument_list|(
name|conf
argument_list|,
name|DistributedSchedulerProtocol
operator|.
name|class
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Error while creating of RM app master service proxy for attemptId:"
operator|+
name|appContext
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|message
operator|+=
literal|", user: "
operator|+
name|user
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|registerApplicationMaster ( final RegisterApplicationMasterRequest request)
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
specifier|final
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Forwarding registration request to the real YARN RM"
argument_list|)
expr_stmt|;
return|return
name|rmClient
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|allocate (final AllocateRequest request)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
specifier|final
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
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
literal|"Forwarding allocate request to the real YARN RM"
argument_list|)
expr_stmt|;
block|}
name|AllocateResponse
name|allocateResponse
init|=
name|rmClient
operator|.
name|allocate
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|allocateResponse
operator|.
name|getAMRMToken
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|updateAMRMToken
argument_list|(
name|allocateResponse
operator|.
name|getAMRMToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|allocateResponse
return|;
block|}
annotation|@
name|Override
specifier|public
name|DistSchedRegisterResponse
DECL|method|registerApplicationMasterForDistributedScheduling (RegisterApplicationMasterRequest request)
name|registerApplicationMasterForDistributedScheduling
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Forwarding registerApplicationMasterForDistributedScheduling"
operator|+
literal|"request to the real YARN RM"
argument_list|)
expr_stmt|;
return|return
name|rmClient
operator|.
name|registerApplicationMasterForDistributedScheduling
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|allocateForDistributedScheduling (AllocateRequest request)
specifier|public
name|DistSchedAllocateResponse
name|allocateForDistributedScheduling
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
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
literal|"Forwarding allocateForDistributedScheduling request"
operator|+
literal|"to the real YARN RM"
argument_list|)
expr_stmt|;
block|}
name|DistSchedAllocateResponse
name|allocateResponse
init|=
name|rmClient
operator|.
name|allocateForDistributedScheduling
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|allocateResponse
operator|.
name|getAllocateResponse
argument_list|()
operator|.
name|getAMRMToken
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|updateAMRMToken
argument_list|(
name|allocateResponse
operator|.
name|getAllocateResponse
argument_list|()
operator|.
name|getAMRMToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|allocateResponse
return|;
block|}
annotation|@
name|Override
DECL|method|finishApplicationMaster ( final FinishApplicationMasterRequest request)
specifier|public
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
specifier|final
name|FinishApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Forwarding finish application request to "
operator|+
literal|"the real YARN Resource Manager"
argument_list|)
expr_stmt|;
return|return
name|rmClient
operator|.
name|finishApplicationMaster
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNextInterceptor (RequestInterceptor next)
specifier|public
name|void
name|setNextInterceptor
parameter_list|(
name|RequestInterceptor
name|next
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"setNextInterceptor is being called on DefaultRequestInterceptor,"
operator|+
literal|"which should be the last one in the chain "
operator|+
literal|"Check if the interceptor pipeline configuration is correct"
argument_list|)
throw|;
block|}
DECL|method|updateAMRMToken (Token token)
specifier|private
name|void
name|updateAMRMToken
parameter_list|(
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
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
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|amrmToken
init|=
operator|new
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
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
argument_list|(
name|token
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|token
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|token
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Preserve the token service sent by the RM when adding the token
comment|// to ensure we replace the previous token setup by the RM.
comment|// Afterwards we can update the service address for the RPC layer.
name|user
operator|.
name|addToken
argument_list|(
name|amrmToken
argument_list|)
expr_stmt|;
name|amrmToken
operator|.
name|setService
argument_list|(
name|ClientRMProxy
operator|.
name|getAMRMTokenService
argument_list|(
name|getConf
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setRMClient (final ApplicationMasterProtocol rmClient)
specifier|public
name|void
name|setRMClient
parameter_list|(
specifier|final
name|ApplicationMasterProtocol
name|rmClient
parameter_list|)
block|{
if|if
condition|(
name|rmClient
operator|instanceof
name|DistributedSchedulerProtocol
condition|)
block|{
name|this
operator|.
name|rmClient
operator|=
operator|(
name|DistributedSchedulerProtocol
operator|)
name|rmClient
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|rmClient
operator|=
operator|new
name|DistributedSchedulerProtocol
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|rmClient
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
name|FinishApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|rmClient
operator|.
name|finishApplicationMaster
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|rmClient
operator|.
name|allocate
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DistSchedRegisterResponse
name|registerApplicationMasterForDistributedScheduling
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not Supported !!"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|DistSchedAllocateResponse
name|allocateForDistributedScheduling
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not Supported !!"
argument_list|)
throw|;
block|}
block|}
expr_stmt|;
block|}
block|}
DECL|method|setAMRMTokenService (final Configuration conf)
specifier|private
specifier|static
name|void
name|setAMRMTokenService
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
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
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|token
range|:
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getTokens
argument_list|()
control|)
block|{
if|if
condition|(
name|token
operator|.
name|getKind
argument_list|()
operator|.
name|equals
argument_list|(
name|AMRMTokenIdentifier
operator|.
name|KIND_NAME
argument_list|)
condition|)
block|{
name|token
operator|.
name|setService
argument_list|(
name|getAMRMTokenService
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getAMRMTokenService (Configuration conf)
specifier|public
specifier|static
name|Text
name|getAMRMTokenService
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|getTokenService
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_PORT
argument_list|)
return|;
block|}
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|getTokenService (Configuration conf, String address, String defaultAddr, int defaultPort)
specifier|public
specifier|static
name|Text
name|getTokenService
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|address
parameter_list|,
name|String
name|defaultAddr
parameter_list|,
name|int
name|defaultPort
parameter_list|)
block|{
if|if
condition|(
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|conf
argument_list|)
condition|)
block|{
comment|// Build a list of service addresses to form the service name
name|ArrayList
argument_list|<
name|String
argument_list|>
name|services
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|YarnConfiguration
name|yarnConf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|rmId
range|:
name|HAUtil
operator|.
name|getRMHAIds
argument_list|(
name|conf
argument_list|)
control|)
block|{
comment|// Set RM_ID to get the corresponding RM_ADDRESS
name|yarnConf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|rmId
argument_list|)
expr_stmt|;
name|services
operator|.
name|add
argument_list|(
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|yarnConf
operator|.
name|getSocketAddr
argument_list|(
name|address
argument_list|,
name|defaultAddr
argument_list|,
name|defaultPort
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Text
argument_list|(
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|services
argument_list|)
argument_list|)
return|;
block|}
comment|// Non-HA case - no need to set RM_ID
return|return
name|SecurityUtil
operator|.
name|buildTokenService
argument_list|(
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|address
argument_list|,
name|defaultAddr
argument_list|,
name|defaultPort
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

