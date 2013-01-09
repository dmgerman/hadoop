begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|Server
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
name|authorize
operator|.
name|PolicyProvider
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
name|StringUtils
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
name|AMRMProtocol
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
name|AMResponse
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|NodeReport
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
name|ResourceRequest
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
name|YarnRemoteException
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|ipc
operator|.
name|RPCUtil
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
name|ipc
operator|.
name|YarnRPC
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
name|RMAuditLogger
operator|.
name|AuditConstants
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
name|rmnode
operator|.
name|RMNode
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
name|rmapp
operator|.
name|RMApp
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
name|rmapp
operator|.
name|attempt
operator|.
name|AMLivelinessMonitor
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
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
name|rmapp
operator|.
name|attempt
operator|.
name|event
operator|.
name|RMAppAttemptRegistrationEvent
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
name|rmapp
operator|.
name|attempt
operator|.
name|event
operator|.
name|RMAppAttemptStatusupdateEvent
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
name|rmapp
operator|.
name|attempt
operator|.
name|event
operator|.
name|RMAppAttemptUnregistrationEvent
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
name|scheduler
operator|.
name|Allocation
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
name|scheduler
operator|.
name|SchedulerNodeReport
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
name|scheduler
operator|.
name|YarnScheduler
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
name|security
operator|.
name|authorize
operator|.
name|RMPolicyProvider
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
name|service
operator|.
name|AbstractService
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
name|util
operator|.
name|BuilderUtils
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Private
DECL|class|ApplicationMasterService
specifier|public
class|class
name|ApplicationMasterService
extends|extends
name|AbstractService
implements|implements
name|AMRMProtocol
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
name|ApplicationMasterService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|amLivelinessMonitor
specifier|private
specifier|final
name|AMLivelinessMonitor
name|amLivelinessMonitor
decl_stmt|;
DECL|field|rScheduler
specifier|private
name|YarnScheduler
name|rScheduler
decl_stmt|;
DECL|field|bindAddress
specifier|private
name|InetSocketAddress
name|bindAddress
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|responseMap
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|AMResponse
argument_list|>
name|responseMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|AMResponse
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reboot
specifier|private
specifier|final
name|AMResponse
name|reboot
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|AMResponse
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|ApplicationMasterService (RMContext rmContext, YarnScheduler scheduler)
specifier|public
name|ApplicationMasterService
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|)
block|{
name|super
argument_list|(
name|ApplicationMasterService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|amLivelinessMonitor
operator|=
name|rmContext
operator|.
name|getAMLivelinessMonitor
argument_list|()
expr_stmt|;
name|this
operator|.
name|rScheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|reboot
operator|.
name|setReboot
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//    this.reboot.containers = new ArrayList<Container>();
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|masterServiceAddress
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
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
decl_stmt|;
name|this
operator|.
name|server
operator|=
name|rpc
operator|.
name|getServer
argument_list|(
name|AMRMProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|masterServiceAddress
argument_list|,
name|conf
argument_list|,
name|this
operator|.
name|rmContext
operator|.
name|getApplicationTokenSecretManager
argument_list|()
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_CLIENT_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_CLIENT_THREAD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Enable service authorization?
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|refreshServiceAcls
argument_list|(
name|conf
argument_list|,
operator|new
name|RMPolicyProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|bindAddress
operator|=
name|conf
operator|.
name|updateConnectAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|server
operator|.
name|getListenerAddress
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|getBindAddress ()
specifier|public
name|InetSocketAddress
name|getBindAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|bindAddress
return|;
block|}
DECL|method|authorizeRequest (ApplicationAttemptId appAttemptID)
specifier|private
name|void
name|authorizeRequest
parameter_list|(
name|ApplicationAttemptId
name|appAttemptID
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
name|appAttemptIDStr
init|=
name|appAttemptID
operator|.
name|toString
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|remoteUgi
decl_stmt|;
try|try
block|{
name|remoteUgi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
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
literal|"Cannot obtain the user-name for ApplicationAttemptID: "
operator|+
name|appAttemptIDStr
operator|+
literal|". Got exception: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|remoteUgi
operator|.
name|getUserName
argument_list|()
operator|.
name|equals
argument_list|(
name|appAttemptIDStr
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Unauthorized request from ApplicationMaster. "
operator|+
literal|"Expected ApplicationAttemptID: "
operator|+
name|remoteUgi
operator|.
name|getUserName
argument_list|()
operator|+
literal|" Found: "
operator|+
name|appAttemptIDStr
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|registerApplicationMaster ( RegisterApplicationMasterRequest request)
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|request
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
name|authorizeRequest
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|ApplicationId
name|appID
init|=
name|applicationAttemptId
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|AMResponse
name|lastResponse
init|=
name|responseMap
operator|.
name|get
argument_list|(
name|applicationAttemptId
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastResponse
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Application doesn't exist in cache "
operator|+
name|applicationAttemptId
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
operator|.
name|getUser
argument_list|()
argument_list|,
name|AuditConstants
operator|.
name|REGISTER_AM
argument_list|,
name|message
argument_list|,
literal|"ApplicationMasterService"
argument_list|,
literal|"Error in registering application master"
argument_list|,
name|appID
argument_list|,
name|applicationAttemptId
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// Allow only one thread in AM to do registerApp at a time.
synchronized|synchronized
init|(
name|lastResponse
init|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"AM registration "
operator|+
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|this
operator|.
name|amLivelinessMonitor
operator|.
name|receivedPing
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptRegistrationEvent
argument_list|(
name|applicationAttemptId
argument_list|,
name|request
operator|.
name|getHost
argument_list|()
argument_list|,
name|request
operator|.
name|getRpcPort
argument_list|()
argument_list|,
name|request
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|RMApp
name|app
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
decl_stmt|;
name|RMAuditLogger
operator|.
name|logSuccess
argument_list|(
name|app
operator|.
name|getUser
argument_list|()
argument_list|,
name|AuditConstants
operator|.
name|REGISTER_AM
argument_list|,
literal|"ApplicationMasterService"
argument_list|,
name|appID
argument_list|,
name|applicationAttemptId
argument_list|)
expr_stmt|;
comment|// Pick up min/max resource from scheduler...
name|RegisterApplicationMasterResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterApplicationMasterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setMinimumResourceCapability
argument_list|(
name|rScheduler
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setMaximumResourceCapability
argument_list|(
name|rScheduler
operator|.
name|getMaximumResourceCapability
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setApplicationACLs
argument_list|(
name|app
operator|.
name|getRMAppAttempt
argument_list|(
name|applicationAttemptId
argument_list|)
operator|.
name|getSubmissionContext
argument_list|()
operator|.
name|getAMContainerSpec
argument_list|()
operator|.
name|getApplicationACLs
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishApplicationMaster ( FinishApplicationMasterRequest request)
specifier|public
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
name|FinishApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ApplicationAttemptId
name|applicationAttemptId
init|=
name|request
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
name|authorizeRequest
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|AMResponse
name|lastResponse
init|=
name|responseMap
operator|.
name|get
argument_list|(
name|applicationAttemptId
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastResponse
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Application doesn't exist in cache "
operator|+
name|applicationAttemptId
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|// Allow only one thread in AM to do finishApp at a time.
synchronized|synchronized
init|(
name|lastResponse
init|)
block|{
name|this
operator|.
name|amLivelinessMonitor
operator|.
name|receivedPing
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptUnregistrationEvent
argument_list|(
name|applicationAttemptId
argument_list|,
name|request
operator|.
name|getTrackingUrl
argument_list|()
argument_list|,
name|request
operator|.
name|getFinalApplicationStatus
argument_list|()
argument_list|,
name|request
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FinishApplicationMasterResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|FinishApplicationMasterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|allocate (AllocateRequest request)
specifier|public
name|AllocateResponse
name|allocate
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|request
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
name|authorizeRequest
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|this
operator|.
name|amLivelinessMonitor
operator|.
name|receivedPing
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
comment|/* check if its in cache */
name|AllocateResponse
name|allocateResponse
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|AllocateResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|AMResponse
name|lastResponse
init|=
name|responseMap
operator|.
name|get
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastResponse
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"AppAttemptId doesnt exist in cache "
operator|+
name|appAttemptId
argument_list|)
expr_stmt|;
name|allocateResponse
operator|.
name|setAMResponse
argument_list|(
name|reboot
argument_list|)
expr_stmt|;
return|return
name|allocateResponse
return|;
block|}
if|if
condition|(
operator|(
name|request
operator|.
name|getResponseId
argument_list|()
operator|+
literal|1
operator|)
operator|==
name|lastResponse
operator|.
name|getResponseId
argument_list|()
condition|)
block|{
comment|/* old heartbeat */
name|allocateResponse
operator|.
name|setAMResponse
argument_list|(
name|lastResponse
argument_list|)
expr_stmt|;
return|return
name|allocateResponse
return|;
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|getResponseId
argument_list|()
operator|+
literal|1
operator|<
name|lastResponse
operator|.
name|getResponseId
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid responseid from appAttemptId "
operator|+
name|appAttemptId
argument_list|)
expr_stmt|;
comment|// Oh damn! Sending reboot isn't enough. RM state is corrupted. TODO:
comment|// Reboot is not useful since after AM reboots, it will send register and
comment|// get an exception. Might as well throw an exception here.
name|allocateResponse
operator|.
name|setAMResponse
argument_list|(
name|reboot
argument_list|)
expr_stmt|;
return|return
name|allocateResponse
return|;
block|}
comment|// Allow only one thread in AM to do heartbeat at a time.
synchronized|synchronized
init|(
name|lastResponse
init|)
block|{
comment|// Send the status update to the appAttempt.
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptStatusupdateEvent
argument_list|(
name|appAttemptId
argument_list|,
name|request
operator|.
name|getProgress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
init|=
name|request
operator|.
name|getAskList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|release
init|=
name|request
operator|.
name|getReleaseList
argument_list|()
decl_stmt|;
comment|// Send new requests to appAttempt.
name|Allocation
name|allocation
init|=
name|this
operator|.
name|rScheduler
operator|.
name|allocate
argument_list|(
name|appAttemptId
argument_list|,
name|ask
argument_list|,
name|release
argument_list|)
decl_stmt|;
name|RMApp
name|app
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|RMAppAttempt
name|appAttempt
init|=
name|app
operator|.
name|getRMAppAttempt
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|AMResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|AMResponse
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// update the response with the deltas of node status changes
name|List
argument_list|<
name|RMNode
argument_list|>
name|updatedNodes
init|=
operator|new
name|ArrayList
argument_list|<
name|RMNode
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|app
operator|.
name|pullRMNodeUpdates
argument_list|(
name|updatedNodes
argument_list|)
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodeReports
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RMNode
name|rmNode
range|:
name|updatedNodes
control|)
block|{
name|SchedulerNodeReport
name|schedulerNodeReport
init|=
name|rScheduler
operator|.
name|getNodeReport
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
name|Resource
name|used
init|=
name|BuilderUtils
operator|.
name|newResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|numContainers
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|schedulerNodeReport
operator|!=
literal|null
condition|)
block|{
name|used
operator|=
name|schedulerNodeReport
operator|.
name|getUsedResource
argument_list|()
expr_stmt|;
name|numContainers
operator|=
name|schedulerNodeReport
operator|.
name|getNumContainers
argument_list|()
expr_stmt|;
block|}
name|NodeReport
name|report
init|=
name|BuilderUtils
operator|.
name|newNodeReport
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|rmNode
operator|.
name|getState
argument_list|()
argument_list|,
name|rmNode
operator|.
name|getHttpAddress
argument_list|()
argument_list|,
name|rmNode
operator|.
name|getRackName
argument_list|()
argument_list|,
name|used
argument_list|,
name|rmNode
operator|.
name|getTotalCapability
argument_list|()
argument_list|,
name|numContainers
argument_list|,
name|rmNode
operator|.
name|getNodeHealthStatus
argument_list|()
argument_list|)
decl_stmt|;
name|updatedNodeReports
operator|.
name|add
argument_list|(
name|report
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|setUpdatedNodes
argument_list|(
name|updatedNodeReports
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|setAllocatedContainers
argument_list|(
name|allocation
operator|.
name|getContainers
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setCompletedContainersStatuses
argument_list|(
name|appAttempt
operator|.
name|pullJustFinishedContainers
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setResponseId
argument_list|(
name|lastResponse
operator|.
name|getResponseId
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|response
operator|.
name|setAvailableResources
argument_list|(
name|allocation
operator|.
name|getResourceLimit
argument_list|()
argument_list|)
expr_stmt|;
name|AMResponse
name|oldResponse
init|=
name|responseMap
operator|.
name|put
argument_list|(
name|appAttemptId
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldResponse
operator|==
literal|null
condition|)
block|{
comment|// appAttempt got unregistered, remove it back out
name|responseMap
operator|.
name|remove
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|String
name|message
init|=
literal|"App Attempt removed from the cache during allocate"
operator|+
name|appAttemptId
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|allocateResponse
operator|.
name|setAMResponse
argument_list|(
name|reboot
argument_list|)
expr_stmt|;
return|return
name|allocateResponse
return|;
block|}
name|allocateResponse
operator|.
name|setAMResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|allocateResponse
operator|.
name|setNumClusterNodes
argument_list|(
name|this
operator|.
name|rScheduler
operator|.
name|getNumClusterNodes
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|allocateResponse
return|;
block|}
block|}
DECL|method|registerAppAttempt (ApplicationAttemptId attemptId)
specifier|public
name|void
name|registerAppAttempt
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
block|{
name|AMResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|AMResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setResponseId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registering "
operator|+
name|attemptId
argument_list|)
expr_stmt|;
name|responseMap
operator|.
name|put
argument_list|(
name|attemptId
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|unregisterAttempt (ApplicationAttemptId attemptId)
specifier|public
name|void
name|unregisterAttempt
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
block|{
name|responseMap
operator|.
name|remove
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
block|}
DECL|method|refreshServiceAcls (Configuration configuration, PolicyProvider policyProvider)
specifier|public
name|void
name|refreshServiceAcls
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|PolicyProvider
name|policyProvider
parameter_list|)
block|{
name|this
operator|.
name|server
operator|.
name|refreshServiceAcl
argument_list|(
name|configuration
argument_list|,
name|policyProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|server
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

