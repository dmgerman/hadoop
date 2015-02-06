begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
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
name|api
operator|.
name|ApplicationHistoryProtocol
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
name|CancelDelegationTokenRequest
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
name|CancelDelegationTokenResponse
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
name|GetApplicationAttemptReportRequest
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
name|GetApplicationAttemptReportResponse
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
name|GetApplicationAttemptsRequest
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
name|GetApplicationAttemptsResponse
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
name|GetApplicationsRequest
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
name|GetApplicationsResponse
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
name|GetContainerReportRequest
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
name|GetContainerReportResponse
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
name|GetContainersRequest
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
name|GetContainersResponse
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
name|GetDelegationTokenRequest
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
name|GetDelegationTokenResponse
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
name|RenewDelegationTokenRequest
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
name|RenewDelegationTokenResponse
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
name|ApplicationAttemptReport
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
name|ContainerReport
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
name|ApplicationAttemptNotFoundException
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
name|ApplicationNotFoundException
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
name|ContainerNotFoundException
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
name|timeline
operator|.
name|security
operator|.
name|authorize
operator|.
name|TimelinePolicyProvider
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

begin_class
DECL|class|ApplicationHistoryClientService
specifier|public
class|class
name|ApplicationHistoryClientService
extends|extends
name|AbstractService
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
name|ApplicationHistoryClientService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|history
specifier|private
name|ApplicationHistoryManager
name|history
decl_stmt|;
DECL|field|protocolHandler
specifier|private
name|ApplicationHistoryProtocol
name|protocolHandler
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|bindAddress
specifier|private
name|InetSocketAddress
name|bindAddress
decl_stmt|;
DECL|method|ApplicationHistoryClientService (ApplicationHistoryManager history)
specifier|public
name|ApplicationHistoryClientService
parameter_list|(
name|ApplicationHistoryManager
name|history
parameter_list|)
block|{
name|super
argument_list|(
literal|"ApplicationHistoryClientService"
argument_list|)
expr_stmt|;
name|this
operator|.
name|history
operator|=
name|history
expr_stmt|;
name|this
operator|.
name|protocolHandler
operator|=
operator|new
name|ApplicationHSClientProtocolHandler
argument_list|()
expr_stmt|;
block|}
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
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
name|address
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_PORT
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_HANDLER_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_THREAD_COUNT
argument_list|)
operator|>
literal|0
argument_list|,
literal|"%s property value should be greater than zero"
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_HANDLER_THREAD_COUNT
argument_list|)
expr_stmt|;
name|server
operator|=
name|rpc
operator|.
name|getServer
argument_list|(
name|ApplicationHistoryProtocol
operator|.
name|class
argument_list|,
name|protocolHandler
argument_list|,
name|address
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
name|TIMELINE_SERVICE_HANDLER_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_CLIENT_THREAD_COUNT
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
name|TimelinePolicyProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_SERVICE_ADDRESS
argument_list|,
name|server
operator|.
name|getListenerAddress
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiated ApplicationHistoryClientService at "
operator|+
name|this
operator|.
name|bindAddress
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|getClientHandler ()
specifier|public
name|ApplicationHistoryProtocol
name|getClientHandler
parameter_list|()
block|{
return|return
name|this
operator|.
name|protocolHandler
return|;
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
DECL|method|refreshServiceAcls (Configuration configuration, PolicyProvider policyProvider)
specifier|private
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
DECL|class|ApplicationHSClientProtocolHandler
specifier|private
class|class
name|ApplicationHSClientProtocolHandler
implements|implements
name|ApplicationHistoryProtocol
block|{
annotation|@
name|Override
DECL|method|cancelDelegationToken ( CancelDelegationTokenRequest request)
specifier|public
name|CancelDelegationTokenResponse
name|cancelDelegationToken
parameter_list|(
name|CancelDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationAttemptReport ( GetApplicationAttemptReportRequest request)
specifier|public
name|GetApplicationAttemptReportResponse
name|getApplicationAttemptReport
parameter_list|(
name|GetApplicationAttemptReportRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|request
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
try|try
block|{
name|GetApplicationAttemptReportResponse
name|response
init|=
name|GetApplicationAttemptReportResponse
operator|.
name|newInstance
argument_list|(
name|history
operator|.
name|getApplicationAttempt
argument_list|(
name|appAttemptId
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|response
return|;
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
literal|"ApplicationAttempt with id '"
operator|+
name|appAttemptId
operator|+
literal|"' doesn't exist in the history store."
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
name|ApplicationAttemptNotFoundException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getApplicationAttempts ( GetApplicationAttemptsRequest request)
specifier|public
name|GetApplicationAttemptsResponse
name|getApplicationAttempts
parameter_list|(
name|GetApplicationAttemptsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetApplicationAttemptsResponse
name|response
init|=
name|GetApplicationAttemptsResponse
operator|.
name|newInstance
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ApplicationAttemptReport
argument_list|>
argument_list|(
name|history
operator|.
name|getApplicationAttempts
argument_list|(
name|request
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
name|YarnException
throws|,
name|IOException
block|{
name|ApplicationId
name|applicationId
init|=
name|request
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
try|try
block|{
name|GetApplicationReportResponse
name|response
init|=
name|GetApplicationReportResponse
operator|.
name|newInstance
argument_list|(
name|history
operator|.
name|getApplication
argument_list|(
name|applicationId
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|response
return|;
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
literal|"Application with id '"
operator|+
name|applicationId
operator|+
literal|"' doesn't exist in the history store."
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
name|ApplicationNotFoundException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getApplications ( GetApplicationsRequest request)
specifier|public
name|GetApplicationsResponse
name|getApplications
parameter_list|(
name|GetApplicationsRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetApplicationsResponse
name|response
init|=
name|GetApplicationsResponse
operator|.
name|newInstance
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ApplicationReport
argument_list|>
argument_list|(
name|history
operator|.
name|getAllApplications
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getContainerReport ( GetContainerReportRequest request)
specifier|public
name|GetContainerReportResponse
name|getContainerReport
parameter_list|(
name|GetContainerReportRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|ContainerId
name|containerId
init|=
name|request
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
try|try
block|{
name|GetContainerReportResponse
name|response
init|=
name|GetContainerReportResponse
operator|.
name|newInstance
argument_list|(
name|history
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|response
return|;
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
literal|"Container with id '"
operator|+
name|containerId
operator|+
literal|"' doesn't exist in the history store."
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
name|ContainerNotFoundException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getContainers (GetContainersRequest request)
specifier|public
name|GetContainersResponse
name|getContainers
parameter_list|(
name|GetContainersRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|GetContainersResponse
name|response
init|=
name|GetContainersResponse
operator|.
name|newInstance
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerReport
argument_list|>
argument_list|(
name|history
operator|.
name|getContainers
argument_list|(
name|request
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getDelegationToken ( GetDelegationTokenRequest request)
specifier|public
name|GetDelegationTokenResponse
name|getDelegationToken
parameter_list|(
name|GetDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|renewDelegationToken ( RenewDelegationTokenRequest request)
specifier|public
name|RenewDelegationTokenResponse
name|renewDelegationToken
parameter_list|(
name|RenewDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

