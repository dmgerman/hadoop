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
name|security
operator|.
name|AccessControlException
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
name|Collection
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avro
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
name|net
operator|.
name|NetUtils
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
name|yarn
operator|.
name|api
operator|.
name|ClientRMProtocol
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
name|GetAllApplicationsRequest
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
name|GetAllApplicationsResponse
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
name|GetApplicationReportRequest
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
name|GetApplicationReportResponse
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
name|GetClusterMetricsRequest
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
name|GetClusterMetricsResponse
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
name|GetClusterNodesRequest
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
name|GetClusterNodesResponse
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
name|GetNewApplicationRequest
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
name|GetNewApplicationResponse
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
name|GetQueueInfoRequest
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
name|GetQueueInfoResponse
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
name|GetQueueUserAclsInfoRequest
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
name|GetQueueUserAclsInfoResponse
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
name|KillApplicationRequest
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
name|KillApplicationResponse
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
name|SubmitApplicationRequest
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
name|SubmitApplicationResponse
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
name|ApplicationAccessType
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
name|ApplicationReport
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
name|ApplicationSubmissionContext
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
name|QueueInfo
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
name|YarnClusterMetrics
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
name|RMAppEvent
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
name|RMAppEventType
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
name|security
operator|.
name|ApplicationACLsManager
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

begin_comment
comment|/**  * The client interface to the Resource Manager. This module handles all the rpc  * interfaces to the resource manager from the client.  */
end_comment

begin_class
DECL|class|ClientRMService
specifier|public
class|class
name|ClientRMService
extends|extends
name|AbstractService
implements|implements
name|ClientRMProtocol
block|{
DECL|field|EMPTY_APPS_REPORT
specifier|private
specifier|static
specifier|final
name|ArrayList
argument_list|<
name|ApplicationReport
argument_list|>
name|EMPTY_APPS_REPORT
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationReport
argument_list|>
argument_list|()
decl_stmt|;
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
name|ClientRMService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|applicationCounter
specifier|final
specifier|private
name|AtomicInteger
name|applicationCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|scheduler
specifier|final
specifier|private
name|YarnScheduler
name|scheduler
decl_stmt|;
DECL|field|rmContext
specifier|final
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|rmAppManager
specifier|private
specifier|final
name|RMAppManager
name|rmAppManager
decl_stmt|;
DECL|field|clientServiceBindAddress
specifier|private
name|String
name|clientServiceBindAddress
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
DECL|field|clientBindAddress
name|InetSocketAddress
name|clientBindAddress
decl_stmt|;
DECL|field|applicationsACLsManager
specifier|private
specifier|final
name|ApplicationACLsManager
name|applicationsACLsManager
decl_stmt|;
DECL|method|ClientRMService (RMContext rmContext, YarnScheduler scheduler, RMAppManager rmAppManager, ApplicationACLsManager applicationACLsManager)
specifier|public
name|ClientRMService
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|YarnScheduler
name|scheduler
parameter_list|,
name|RMAppManager
name|rmAppManager
parameter_list|,
name|ApplicationACLsManager
name|applicationACLsManager
parameter_list|)
block|{
name|super
argument_list|(
name|ClientRMService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|rmAppManager
operator|=
name|rmAppManager
expr_stmt|;
name|this
operator|.
name|applicationsACLsManager
operator|=
name|applicationACLsManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|clientServiceBindAddress
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|)
expr_stmt|;
name|clientBindAddress
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|clientServiceBindAddress
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_PORT
argument_list|,
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
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
comment|// All the clients to appsManager are supposed to be authenticated via
comment|// Kerberos if security is enabled, so no secretManager.
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
name|this
operator|.
name|server
operator|=
name|rpc
operator|.
name|getServer
argument_list|(
name|ClientRMProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|clientBindAddress
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLIENT_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CLIENT_THREAD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * check if the calling user has the access to application information.    * @param callerUGI    * @param owner    * @param operationPerformed    * @param applicationId    * @return    */
DECL|method|checkAccess (UserGroupInformation callerUGI, String owner, ApplicationAccessType operationPerformed, ApplicationId applicationId)
specifier|private
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|String
name|owner
parameter_list|,
name|ApplicationAccessType
name|operationPerformed
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|)
block|{
return|return
name|applicationsACLsManager
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|operationPerformed
argument_list|,
name|owner
argument_list|,
name|applicationId
argument_list|)
return|;
block|}
DECL|method|getNewApplicationId ()
name|ApplicationId
name|getNewApplicationId
parameter_list|()
block|{
name|ApplicationId
name|applicationId
init|=
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
operator|.
name|newApplicationId
argument_list|(
name|recordFactory
argument_list|,
name|ResourceManager
operator|.
name|clusterTimeStamp
argument_list|,
name|applicationCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Allocated new applicationId: "
operator|+
name|applicationId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|applicationId
return|;
block|}
annotation|@
name|Override
DECL|method|getNewApplication ( GetNewApplicationRequest request)
specifier|public
name|GetNewApplicationResponse
name|getNewApplication
parameter_list|(
name|GetNewApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|GetNewApplicationResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetNewApplicationResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setApplicationId
argument_list|(
name|getNewApplicationId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Pick up min/max resource from scheduler...
name|response
operator|.
name|setMinimumResourceCapability
argument_list|(
name|scheduler
operator|.
name|getMinimumResourceCapability
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setMaximumResourceCapability
argument_list|(
name|scheduler
operator|.
name|getMaximumResourceCapability
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationReport ( GetApplicationReportRequest request)
specifier|public
name|GetApplicationReportResponse
name|getApplicationReport
parameter_list|(
name|GetApplicationReportRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ApplicationId
name|applicationId
init|=
name|request
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|callerUGI
decl_stmt|;
try|try
block|{
name|callerUGI
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
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error getting UGI "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|RMApp
name|application
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
name|applicationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|application
operator|==
literal|null
condition|)
block|{
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
literal|"Trying to get information for an "
operator|+
literal|"absent application "
operator|+
name|applicationId
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|application
operator|.
name|getUser
argument_list|()
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|applicationId
argument_list|)
condition|)
block|{
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
operator|new
name|AccessControlException
argument_list|(
literal|"User "
operator|+
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" cannot perform operation "
operator|+
name|ApplicationAccessType
operator|.
name|VIEW_APP
operator|.
name|name
argument_list|()
operator|+
literal|" on "
operator|+
name|applicationId
argument_list|)
argument_list|)
throw|;
block|}
name|ApplicationReport
name|report
init|=
name|application
operator|.
name|createAndGetApplicationReport
argument_list|()
decl_stmt|;
name|GetApplicationReportResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetApplicationReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setApplicationReport
argument_list|(
name|report
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|submitApplication ( SubmitApplicationRequest request)
specifier|public
name|SubmitApplicationResponse
name|submitApplication
parameter_list|(
name|SubmitApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ApplicationSubmissionContext
name|submissionContext
init|=
name|request
operator|.
name|getApplicationSubmissionContext
argument_list|()
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|submissionContext
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|submissionContext
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
block|{
name|user
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
if|if
condition|(
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Application with id "
operator|+
name|applicationId
operator|+
literal|" is already present! Cannot add a duplicate!"
argument_list|)
throw|;
block|}
comment|// Safety
name|submissionContext
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|// This needs to be synchronous as the client can query
comment|// immediately following the submission to get the application status.
comment|// So call handle directly and do not send an event.
name|rmAppManager
operator|.
name|handle
argument_list|(
operator|new
name|RMAppManagerSubmitEvent
argument_list|(
name|submissionContext
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Application with id "
operator|+
name|applicationId
operator|.
name|getId
argument_list|()
operator|+
literal|" submitted by user "
operator|+
name|user
operator|+
literal|" with "
operator|+
name|submissionContext
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logSuccess
argument_list|(
name|user
argument_list|,
name|AuditConstants
operator|.
name|SUBMIT_APP_REQUEST
argument_list|,
literal|"ClientRMService"
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception in submitting application"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
name|user
argument_list|,
name|AuditConstants
operator|.
name|SUBMIT_APP_REQUEST
argument_list|,
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"ClientRMService"
argument_list|,
literal|"Exception in submitting application"
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|SubmitApplicationResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|SubmitApplicationResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|forceKillApplication ( KillApplicationRequest request)
specifier|public
name|KillApplicationResponse
name|forceKillApplication
parameter_list|(
name|KillApplicationRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ApplicationId
name|applicationId
init|=
name|request
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|callerUGI
decl_stmt|;
try|try
block|{
name|callerUGI
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
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error getting UGI "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
literal|"UNKNOWN"
argument_list|,
name|AuditConstants
operator|.
name|KILL_APP_REQUEST
argument_list|,
literal|"UNKNOWN"
argument_list|,
literal|"ClientRMService"
argument_list|,
literal|"Error getting UGI"
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|RMApp
name|application
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
name|applicationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|application
operator|==
literal|null
condition|)
block|{
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
name|callerUGI
operator|.
name|getUserName
argument_list|()
argument_list|,
name|AuditConstants
operator|.
name|KILL_APP_REQUEST
argument_list|,
literal|"UNKNOWN"
argument_list|,
literal|"ClientRMService"
argument_list|,
literal|"Trying to kill an absent application"
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
literal|"Trying to kill an absent application "
operator|+
name|applicationId
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|application
operator|.
name|getUser
argument_list|()
argument_list|,
name|ApplicationAccessType
operator|.
name|MODIFY_APP
argument_list|,
name|applicationId
argument_list|)
condition|)
block|{
name|RMAuditLogger
operator|.
name|logFailure
argument_list|(
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|AuditConstants
operator|.
name|KILL_APP_REQUEST
argument_list|,
literal|"User doesn't have permissions to "
operator|+
name|ApplicationAccessType
operator|.
name|MODIFY_APP
operator|.
name|toString
argument_list|()
argument_list|,
literal|"ClientRMService"
argument_list|,
name|AuditConstants
operator|.
name|UNAUTHORIZED_USER
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
operator|new
name|AccessControlException
argument_list|(
literal|"User "
operator|+
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" cannot perform operation "
operator|+
name|ApplicationAccessType
operator|.
name|MODIFY_APP
operator|.
name|name
argument_list|()
operator|+
literal|" on "
operator|+
name|applicationId
argument_list|)
argument_list|)
throw|;
block|}
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
name|RMAppEvent
argument_list|(
name|applicationId
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|)
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logSuccess
argument_list|(
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
argument_list|,
name|AuditConstants
operator|.
name|KILL_APP_REQUEST
argument_list|,
literal|"ClientRMService"
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
name|KillApplicationResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillApplicationResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterMetrics ( GetClusterMetricsRequest request)
specifier|public
name|GetClusterMetricsResponse
name|getClusterMetrics
parameter_list|(
name|GetClusterMetricsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|GetClusterMetricsResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetClusterMetricsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|YarnClusterMetrics
name|ymetrics
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|YarnClusterMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|ymetrics
operator|.
name|setNumNodeManagers
argument_list|(
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setClusterMetrics
argument_list|(
name|ymetrics
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getAllApplications ( GetAllApplicationsRequest request)
specifier|public
name|GetAllApplicationsResponse
name|getAllApplications
parameter_list|(
name|GetAllApplicationsRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|UserGroupInformation
name|callerUGI
decl_stmt|;
try|try
block|{
name|callerUGI
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
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error getting UGI "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|reports
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationReport
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RMApp
name|application
range|:
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Only give out the applications viewable by the user as
comment|// ApplicationReport has confidential information like client-token, ACLs
comment|// etc. Web UI displays all applications though as we filter and print
comment|// only public information there.
if|if
condition|(
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|application
operator|.
name|getUser
argument_list|()
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
name|reports
operator|.
name|add
argument_list|(
name|application
operator|.
name|createAndGetApplicationReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|GetAllApplicationsResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetAllApplicationsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setApplicationList
argument_list|(
name|reports
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterNodes (GetClusterNodesRequest request)
specifier|public
name|GetClusterNodesResponse
name|getClusterNodes
parameter_list|(
name|GetClusterNodesRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|GetClusterNodesResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetClusterNodesResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|RMNode
argument_list|>
name|nodes
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodeReports
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RMNode
name|nodeInfo
range|:
name|nodes
control|)
block|{
name|nodeReports
operator|.
name|add
argument_list|(
name|createNodeReports
argument_list|(
name|nodeInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|setNodeReports
argument_list|(
name|nodeReports
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueInfo (GetQueueInfoRequest request)
specifier|public
name|GetQueueInfoResponse
name|getQueueInfo
parameter_list|(
name|GetQueueInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|GetQueueInfoResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetQueueInfoResponse
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|QueueInfo
name|queueInfo
init|=
name|scheduler
operator|.
name|getQueueInfo
argument_list|(
name|request
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|request
operator|.
name|getIncludeChildQueues
argument_list|()
argument_list|,
name|request
operator|.
name|getRecursive
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|appReports
init|=
name|EMPTY_APPS_REPORT
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getIncludeApplications
argument_list|()
condition|)
block|{
name|Collection
argument_list|<
name|RMApp
argument_list|>
name|apps
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|appReports
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationReport
argument_list|>
argument_list|(
name|apps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RMApp
name|app
range|:
name|apps
control|)
block|{
name|appReports
operator|.
name|add
argument_list|(
name|app
operator|.
name|createAndGetApplicationReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|queueInfo
operator|.
name|setApplications
argument_list|(
name|appReports
argument_list|)
expr_stmt|;
name|response
operator|.
name|setQueueInfo
argument_list|(
name|queueInfo
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
name|info
argument_list|(
literal|"Failed to getQueueInfo for "
operator|+
name|request
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
return|return
name|response
return|;
block|}
DECL|method|createNodeReports (RMNode rmNode)
specifier|private
name|NodeReport
name|createNodeReports
parameter_list|(
name|RMNode
name|rmNode
parameter_list|)
block|{
name|NodeReport
name|report
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|report
operator|.
name|setNodeId
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setRackName
argument_list|(
name|rmNode
operator|.
name|getRackName
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setCapability
argument_list|(
name|rmNode
operator|.
name|getTotalCapability
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setNodeHealthStatus
argument_list|(
name|rmNode
operator|.
name|getNodeHealthStatus
argument_list|()
argument_list|)
expr_stmt|;
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
name|schedulerNodeReport
init|=
name|scheduler
operator|.
name|getNodeReport
argument_list|(
name|rmNode
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
name|report
operator|.
name|setUsed
argument_list|(
name|schedulerNodeReport
operator|.
name|getUsedResource
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setNumContainers
argument_list|(
name|schedulerNodeReport
operator|.
name|getNumContainers
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueUserAcls ( GetQueueUserAclsInfoRequest request)
specifier|public
name|GetQueueUserAclsInfoResponse
name|getQueueUserAcls
parameter_list|(
name|GetQueueUserAclsInfoRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|GetQueueUserAclsInfoResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetQueueUserAclsInfoResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setUserAclsInfoList
argument_list|(
name|scheduler
operator|.
name|getQueueUserAclInfo
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
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
name|close
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

