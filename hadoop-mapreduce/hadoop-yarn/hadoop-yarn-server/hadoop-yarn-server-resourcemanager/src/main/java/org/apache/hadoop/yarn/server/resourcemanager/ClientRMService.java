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
name|SecurityInfo
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
name|AccessControlList
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
name|FinishApplicationRequest
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
name|FinishApplicationResponse
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
name|GetNewApplicationIdRequest
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
name|GetNewApplicationIdResponse
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
name|Container
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
name|security
operator|.
name|client
operator|.
name|ClientRMSecurityInfo
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
name|rmapp
operator|.
name|RMAppImpl
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
DECL|field|amLivelinessMonitor
specifier|private
specifier|final
name|AMLivelinessMonitor
name|amLivelinessMonitor
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
DECL|field|aclsManager
specifier|private
name|ApplicationACLsManager
name|aclsManager
decl_stmt|;
DECL|field|applicationACLs
specifier|private
name|Map
argument_list|<
name|ApplicationACL
argument_list|,
name|AccessControlList
argument_list|>
name|applicationACLs
decl_stmt|;
DECL|method|ClientRMService (RMContext rmContext, YarnScheduler scheduler)
specifier|public
name|ClientRMService
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
name|amLivelinessMonitor
operator|=
name|rmContext
operator|.
name|getAMLivelinessMonitor
argument_list|()
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
name|APPSMANAGER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_APPSMANAGER_BIND_ADDRESS
argument_list|)
expr_stmt|;
name|clientBindAddress
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|clientServiceBindAddress
argument_list|)
expr_stmt|;
name|this
operator|.
name|aclsManager
operator|=
operator|new
name|ApplicationACLsManager
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationACLs
operator|=
name|aclsManager
operator|.
name|constructApplicationACLs
argument_list|(
name|conf
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
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|clientServerConf
init|=
operator|new
name|Configuration
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|clientServerConf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_INFO
argument_list|,
name|ClientRMSecurityInfo
operator|.
name|class
argument_list|,
name|SecurityInfo
operator|.
name|class
argument_list|)
expr_stmt|;
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
name|clientServerConf
argument_list|,
literal|null
argument_list|,
name|clientServerConf
operator|.
name|getInt
argument_list|(
name|RMConfig
operator|.
name|RM_CLIENT_THREADS
argument_list|,
name|RMConfig
operator|.
name|DEFAULT_RM_CLIENT_THREADS
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
comment|/**    * check if the calling user has the access to application information.    * @param appAttemptId    * @param callerUGI    * @param owner    * @param appACL    * @return    */
DECL|method|checkAccess (UserGroupInformation callerUGI, String owner, ApplicationACL appACL)
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
name|ApplicationACL
name|appACL
parameter_list|)
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
return|return
literal|true
return|;
block|}
name|AccessControlList
name|applicationACL
init|=
name|applicationACLs
operator|.
name|get
argument_list|(
name|appACL
argument_list|)
decl_stmt|;
return|return
name|aclsManager
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|appACL
argument_list|,
name|owner
argument_list|,
name|applicationACL
argument_list|)
return|;
block|}
DECL|method|getNewApplicationId ()
specifier|public
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
DECL|method|getNewApplicationId ( GetNewApplicationIdRequest request)
specifier|public
name|GetNewApplicationIdResponse
name|getNewApplicationId
parameter_list|(
name|GetNewApplicationIdRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|GetNewApplicationIdResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetNewApplicationIdResponse
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
name|RMApp
name|application
init|=
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
name|ApplicationReport
name|report
init|=
operator|(
name|application
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
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
try|try
block|{
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
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
name|RMAppManagerSubmitEvent
argument_list|(
name|submissionContext
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
name|Override
DECL|method|finishApplication ( FinishApplicationRequest request)
specifier|public
name|FinishApplicationResponse
name|finishApplication
parameter_list|(
name|FinishApplicationRequest
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
comment|// TODO: What if null
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
name|ApplicationACL
operator|.
name|MODIFY_APP
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
name|ApplicationACL
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
name|FinishApplicationResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|FinishApplicationResponse
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
name|getUsedResources
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

