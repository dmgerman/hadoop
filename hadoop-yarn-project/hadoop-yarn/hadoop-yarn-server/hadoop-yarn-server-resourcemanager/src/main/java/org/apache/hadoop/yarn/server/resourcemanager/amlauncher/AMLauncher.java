begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.amlauncher
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
name|amlauncher
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
name|nio
operator|.
name|ByteBuffer
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
name|Map
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
name|DataInputByteBuffer
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
name|DataOutputBuffer
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
name|Credentials
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
name|SecretManager
operator|.
name|InvalidToken
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
name|ApplicationConstants
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
name|ContainerManagementProtocol
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
name|StartContainerRequest
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
name|StartContainersRequest
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
name|StartContainersResponse
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
name|StopContainersRequest
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
name|StopContainersResponse
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
name|ContainerLaunchContext
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
name|client
operator|.
name|NMProxy
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
name|event
operator|.
name|EventHandler
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
name|RMAppAttemptEvent
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
name|RMAppAttemptEventType
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
name|RMAppAttemptImpl
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
name|ConverterUtils
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
name|timeline
operator|.
name|TimelineUtils
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
comment|/**  * The launch of the AM itself.  */
end_comment

begin_class
DECL|class|AMLauncher
specifier|public
class|class
name|AMLauncher
implements|implements
name|Runnable
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
name|AMLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containerMgrProxy
specifier|private
name|ContainerManagementProtocol
name|containerMgrProxy
decl_stmt|;
DECL|field|application
specifier|private
specifier|final
name|RMAppAttempt
name|application
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|eventType
specifier|private
specifier|final
name|AMLauncherEventType
name|eventType
decl_stmt|;
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|masterContainer
specifier|private
specifier|final
name|Container
name|masterContainer
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|handler
specifier|private
specifier|final
name|EventHandler
name|handler
decl_stmt|;
DECL|method|AMLauncher (RMContext rmContext, RMAppAttempt application, AMLauncherEventType eventType, Configuration conf)
specifier|public
name|AMLauncher
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|RMAppAttempt
name|application
parameter_list|,
name|AMLauncherEventType
name|eventType
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|application
operator|=
name|application
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|eventType
operator|=
name|eventType
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
expr_stmt|;
name|this
operator|.
name|masterContainer
operator|=
name|application
operator|.
name|getMasterContainer
argument_list|()
expr_stmt|;
block|}
DECL|method|connect ()
specifier|private
name|void
name|connect
parameter_list|()
throws|throws
name|IOException
block|{
name|ContainerId
name|masterContainerID
init|=
name|masterContainer
operator|.
name|getId
argument_list|()
decl_stmt|;
name|containerMgrProxy
operator|=
name|getContainerMgrProxy
argument_list|(
name|masterContainerID
argument_list|)
expr_stmt|;
block|}
DECL|method|launch ()
specifier|private
name|void
name|launch
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|connect
argument_list|()
expr_stmt|;
name|ContainerId
name|masterContainerID
init|=
name|masterContainer
operator|.
name|getId
argument_list|()
decl_stmt|;
name|ApplicationSubmissionContext
name|applicationContext
init|=
name|application
operator|.
name|getSubmissionContext
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting up container "
operator|+
name|masterContainer
operator|+
literal|" for AM "
operator|+
name|application
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerLaunchContext
name|launchContext
init|=
name|createAMContainerLaunchContext
argument_list|(
name|applicationContext
argument_list|,
name|masterContainerID
argument_list|)
decl_stmt|;
name|StartContainerRequest
name|scRequest
init|=
name|StartContainerRequest
operator|.
name|newInstance
argument_list|(
name|launchContext
argument_list|,
name|masterContainer
operator|.
name|getContainerToken
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|StartContainerRequest
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|StartContainerRequest
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|scRequest
argument_list|)
expr_stmt|;
name|StartContainersRequest
name|allRequests
init|=
name|StartContainersRequest
operator|.
name|newInstance
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|StartContainersResponse
name|response
init|=
name|containerMgrProxy
operator|.
name|startContainers
argument_list|(
name|allRequests
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|containsKey
argument_list|(
name|masterContainerID
argument_list|)
condition|)
block|{
name|Throwable
name|t
init|=
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|get
argument_list|(
name|masterContainerID
argument_list|)
operator|.
name|deSerialize
argument_list|()
decl_stmt|;
name|parseAndThrowException
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Done launching container "
operator|+
name|masterContainer
operator|+
literal|" for AM "
operator|+
name|application
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cleanup ()
specifier|private
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|connect
argument_list|()
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|masterContainer
operator|.
name|getId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containerIds
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
name|containerIds
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|StopContainersRequest
name|stopRequest
init|=
name|StopContainersRequest
operator|.
name|newInstance
argument_list|(
name|containerIds
argument_list|)
decl_stmt|;
name|StopContainersResponse
name|response
init|=
name|containerMgrProxy
operator|.
name|stopContainers
argument_list|(
name|stopRequest
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|containsKey
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|Throwable
name|t
init|=
name|response
operator|.
name|getFailedRequests
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
operator|.
name|deSerialize
argument_list|()
decl_stmt|;
name|parseAndThrowException
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Protected. For tests.
DECL|method|getContainerMgrProxy ( final ContainerId containerId)
specifier|protected
name|ContainerManagementProtocol
name|getContainerMgrProxy
parameter_list|(
specifier|final
name|ContainerId
name|containerId
parameter_list|)
block|{
specifier|final
name|NodeId
name|node
init|=
name|masterContainer
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
specifier|final
name|InetSocketAddress
name|containerManagerConnectAddress
init|=
name|NetUtils
operator|.
name|createSocketAddrForHost
argument_list|(
name|node
operator|.
name|getHost
argument_list|()
argument_list|,
name|node
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|getYarnRPC
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getUser
argument_list|()
decl_stmt|;
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
name|token
init|=
name|rmContext
operator|.
name|getNMTokenSecretManager
argument_list|()
operator|.
name|createNMToken
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|node
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|currentUser
operator|.
name|addToken
argument_list|(
name|ConverterUtils
operator|.
name|convertFromYarn
argument_list|(
name|token
argument_list|,
name|containerManagerConnectAddress
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|NMProxy
operator|.
name|createNMProxy
argument_list|(
name|conf
argument_list|,
name|ContainerManagementProtocol
operator|.
name|class
argument_list|,
name|currentUser
argument_list|,
name|rpc
argument_list|,
name|containerManagerConnectAddress
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getYarnRPC ()
specifier|protected
name|YarnRPC
name|getYarnRPC
parameter_list|()
block|{
return|return
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
return|;
comment|// TODO: Don't create again and again.
block|}
DECL|method|createAMContainerLaunchContext ( ApplicationSubmissionContext applicationMasterContext, ContainerId containerID)
specifier|private
name|ContainerLaunchContext
name|createAMContainerLaunchContext
parameter_list|(
name|ApplicationSubmissionContext
name|applicationMasterContext
parameter_list|,
name|ContainerId
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Construct the actual Container
name|ContainerLaunchContext
name|container
init|=
name|applicationMasterContext
operator|.
name|getAMContainerSpec
argument_list|()
decl_stmt|;
comment|// Populate the current queue name in the environment variable.
name|setupQueueNameEnv
argument_list|(
name|container
argument_list|,
name|applicationMasterContext
argument_list|)
expr_stmt|;
comment|// Finalize the container
name|setupTokens
argument_list|(
name|container
argument_list|,
name|containerID
argument_list|)
expr_stmt|;
comment|// set the flow context optionally for timeline service v.2
name|setFlowContext
argument_list|(
name|container
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
DECL|method|setupQueueNameEnv (ContainerLaunchContext container, ApplicationSubmissionContext applicationMasterContext)
specifier|private
name|void
name|setupQueueNameEnv
parameter_list|(
name|ContainerLaunchContext
name|container
parameter_list|,
name|ApplicationSubmissionContext
name|applicationMasterContext
parameter_list|)
block|{
name|String
name|queueName
init|=
name|applicationMasterContext
operator|.
name|getQueue
argument_list|()
decl_stmt|;
if|if
condition|(
name|queueName
operator|==
literal|null
condition|)
block|{
name|queueName
operator|=
name|YarnConfiguration
operator|.
name|DEFAULT_QUEUE_NAME
expr_stmt|;
block|}
name|container
operator|.
name|getEnvironment
argument_list|()
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|Environment
operator|.
name|YARN_RESOURCEMANAGER_APPLICATION_QUEUE
operator|.
name|key
argument_list|()
argument_list|,
name|queueName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|setupTokens ( ContainerLaunchContext container, ContainerId containerID)
specifier|protected
name|void
name|setupTokens
parameter_list|(
name|ContainerLaunchContext
name|container
parameter_list|,
name|ContainerId
name|containerID
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
name|container
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|APPLICATION_WEB_PROXY_BASE_ENV
argument_list|,
name|application
operator|.
name|getWebProxyBase
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set AppSubmitTime to be consumable by the AM.
name|ApplicationId
name|applicationId
init|=
name|application
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|APP_SUBMIT_TIME_ENV
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|DataInputByteBuffer
name|dibb
init|=
operator|new
name|DataInputByteBuffer
argument_list|()
decl_stmt|;
name|ByteBuffer
name|tokens
init|=
name|container
operator|.
name|getTokens
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokens
operator|!=
literal|null
condition|)
block|{
comment|// TODO: Don't do this kind of checks everywhere.
name|dibb
operator|.
name|reset
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|readTokenStorageStream
argument_list|(
name|dibb
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|rewind
argument_list|()
expr_stmt|;
block|}
comment|// Add AMRMToken
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|amrmToken
init|=
name|createAndSetAMRMToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|amrmToken
operator|!=
literal|null
condition|)
block|{
name|credentials
operator|.
name|addToken
argument_list|(
name|amrmToken
operator|.
name|getService
argument_list|()
argument_list|,
name|amrmToken
argument_list|)
expr_stmt|;
block|}
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|credentials
operator|.
name|writeTokenStorageToStream
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|container
operator|.
name|setTokens
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setFlowContext (ContainerLaunchContext container)
specifier|private
name|void
name|setFlowContext
parameter_list|(
name|ContainerLaunchContext
name|container
parameter_list|)
block|{
if|if
condition|(
name|YarnConfiguration
operator|.
name|timelineServiceV2Enabled
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
name|container
operator|.
name|getEnvironment
argument_list|()
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|application
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|RMApp
name|app
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
comment|// initialize the flow in the environment with default values for those
comment|// that do not specify the flow tags
comment|// flow name: app name (or app id if app name is missing),
comment|// flow version: "1", flow run id: start time
name|setFlowTags
argument_list|(
name|environment
argument_list|,
name|TimelineUtils
operator|.
name|FLOW_NAME_TAG_PREFIX
argument_list|,
name|TimelineUtils
operator|.
name|generateDefaultFlowName
argument_list|(
name|app
operator|.
name|getName
argument_list|()
argument_list|,
name|applicationId
argument_list|)
argument_list|)
expr_stmt|;
name|setFlowTags
argument_list|(
name|environment
argument_list|,
name|TimelineUtils
operator|.
name|FLOW_VERSION_TAG_PREFIX
argument_list|,
name|TimelineUtils
operator|.
name|DEFAULT_FLOW_VERSION
argument_list|)
expr_stmt|;
name|setFlowTags
argument_list|(
name|environment
argument_list|,
name|TimelineUtils
operator|.
name|FLOW_RUN_ID_TAG_PREFIX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set flow context info: the flow context is received via the application
comment|// tags
for|for
control|(
name|String
name|tag
range|:
name|app
operator|.
name|getApplicationTags
argument_list|()
control|)
block|{
name|String
index|[]
name|parts
init|=
name|tag
operator|.
name|split
argument_list|(
literal|":"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|!=
literal|2
operator|||
name|parts
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
switch|switch
condition|(
name|parts
index|[
literal|0
index|]
operator|.
name|toUpperCase
argument_list|()
condition|)
block|{
case|case
name|TimelineUtils
operator|.
name|FLOW_NAME_TAG_PREFIX
case|:
name|setFlowTags
argument_list|(
name|environment
argument_list|,
name|TimelineUtils
operator|.
name|FLOW_NAME_TAG_PREFIX
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|TimelineUtils
operator|.
name|FLOW_VERSION_TAG_PREFIX
case|:
name|setFlowTags
argument_list|(
name|environment
argument_list|,
name|TimelineUtils
operator|.
name|FLOW_VERSION_TAG_PREFIX
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|TimelineUtils
operator|.
name|FLOW_RUN_ID_TAG_PREFIX
case|:
name|setFlowTags
argument_list|(
name|environment
argument_list|,
name|TimelineUtils
operator|.
name|FLOW_RUN_ID_TAG_PREFIX
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
block|}
block|}
DECL|method|setFlowTags ( Map<String, String> environment, String tagPrefix, String value)
specifier|private
specifier|static
name|void
name|setFlowTags
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
parameter_list|,
name|String
name|tagPrefix
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|environment
operator|.
name|put
argument_list|(
name|tagPrefix
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|createAndSetAMRMToken ()
specifier|protected
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|createAndSetAMRMToken
parameter_list|()
block|{
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|amrmToken
init|=
name|this
operator|.
name|rmContext
operator|.
name|getAMRMTokenSecretManager
argument_list|()
operator|.
name|createAndGetAMRMToken
argument_list|(
name|application
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
operator|(
operator|(
name|RMAppAttemptImpl
operator|)
name|application
operator|)
operator|.
name|setAMRMToken
argument_list|(
name|amrmToken
argument_list|)
expr_stmt|;
return|return
name|amrmToken
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
switch|switch
condition|(
name|eventType
condition|)
block|{
case|case
name|LAUNCH
case|:
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Launching master"
operator|+
name|application
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|launch
argument_list|()
expr_stmt|;
name|handler
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptEvent
argument_list|(
name|application
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptEventType
operator|.
name|LAUNCHED
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ie
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Error launching "
operator|+
name|application
operator|.
name|getAppAttemptId
argument_list|()
operator|+
literal|". Got exception: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ie
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptEvent
argument_list|(
name|application
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptEventType
operator|.
name|LAUNCH_FAILED
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|CLEANUP
case|:
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cleaning master "
operator|+
name|application
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|cleanup
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
literal|"Error cleaning master "
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Container "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|masterContainer
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" is not handled by this NodeManager"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
comment|// Ignoring if container is already killed by Node Manager.
name|LOG
operator|.
name|info
argument_list|(
literal|"Error cleaning master "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
name|LOG
operator|.
name|warn
argument_list|(
literal|"Received unknown event-type "
operator|+
name|eventType
operator|+
literal|". Ignoring."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
DECL|method|parseAndThrowException (Throwable t)
specifier|private
name|void
name|parseAndThrowException
parameter_list|(
name|Throwable
name|t
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|t
operator|instanceof
name|YarnException
condition|)
block|{
throw|throw
operator|(
name|YarnException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|InvalidToken
condition|)
block|{
throw|throw
operator|(
name|InvalidToken
operator|)
name|t
throw|;
block|}
else|else
block|{
throw|throw
operator|(
name|IOException
operator|)
name|t
throw|;
block|}
block|}
block|}
end_class

end_unit

