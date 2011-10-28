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
name|security
operator|.
name|PrivilegedAction
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
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|ContainerManager
import|;
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
name|StopContainerRequest
import|;
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
name|ContainerToken
import|;
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
name|ApplicationTokenIdentifier
import|;
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
name|ApplicationTokenSecretManager
import|;
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
name|security
operator|.
name|client
operator|.
name|ClientToAMSecretManager
import|;
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
name|ClientTokenIdentifier
import|;
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
name|event
operator|.
name|RMAppAttemptLaunchFailedEvent
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
name|ContainerManager
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
DECL|field|applicationTokenSecretManager
specifier|private
specifier|final
name|ApplicationTokenSecretManager
name|applicationTokenSecretManager
decl_stmt|;
DECL|field|clientToAMSecretManager
specifier|private
specifier|final
name|ClientToAMSecretManager
name|clientToAMSecretManager
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
DECL|method|AMLauncher (RMContext rmContext, RMAppAttempt application, AMLauncherEventType eventType, ApplicationTokenSecretManager applicationTokenSecretManager, ClientToAMSecretManager clientToAMSecretManager, Configuration conf)
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
name|ApplicationTokenSecretManager
name|applicationTokenSecretManager
parameter_list|,
name|ClientToAMSecretManager
name|clientToAMSecretManager
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
name|applicationTokenSecretManager
operator|=
name|applicationTokenSecretManager
expr_stmt|;
name|this
operator|.
name|clientToAMSecretManager
operator|=
name|clientToAMSecretManager
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
name|application
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|containerMgrProxy
operator|=
name|getContainerMgrProxy
argument_list|(
name|masterContainerID
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
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
block|{
name|connect
argument_list|()
expr_stmt|;
name|ContainerId
name|masterContainerID
init|=
name|application
operator|.
name|getMasterContainer
argument_list|()
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
name|application
operator|.
name|getMasterContainer
argument_list|()
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
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setContainerLaunchContext
argument_list|(
name|launchContext
argument_list|)
expr_stmt|;
name|containerMgrProxy
operator|.
name|startContainer
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Done launching container "
operator|+
name|application
operator|.
name|getMasterContainer
argument_list|()
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
DECL|method|cleanup ()
specifier|private
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|connect
argument_list|()
expr_stmt|;
name|ContainerId
name|containerId
init|=
name|application
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|StopContainerRequest
name|stopRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StopContainerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|stopRequest
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|containerMgrProxy
operator|.
name|stopContainer
argument_list|(
name|stopRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|getContainerMgrProxy ( final ApplicationId applicationID)
specifier|protected
name|ContainerManager
name|getContainerMgrProxy
parameter_list|(
specifier|final
name|ApplicationId
name|applicationID
parameter_list|)
throws|throws
name|IOException
block|{
name|Container
name|container
init|=
name|application
operator|.
name|getMasterContainer
argument_list|()
decl_stmt|;
specifier|final
name|String
name|containerManagerBindAddress
init|=
name|container
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
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
comment|// TODO: Don't create again and again.
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"yarn"
argument_list|)
decl_stmt|;
comment|// TODO
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|ContainerToken
name|containerToken
init|=
name|container
operator|.
name|getContainerToken
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|ContainerTokenIdentifier
argument_list|>
argument_list|(
name|containerToken
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|containerToken
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
name|containerToken
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|containerToken
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|currentUser
operator|.
name|addToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
return|return
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|ContainerManager
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContainerManager
name|run
parameter_list|()
block|{
return|return
operator|(
name|ContainerManager
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ContainerManager
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|containerManagerBindAddress
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Command to launch container "
operator|+
name|containerID
operator|+
literal|" : "
operator|+
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|container
operator|.
name|getCommands
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Finalize the container
name|container
operator|.
name|setContainerId
argument_list|(
name|containerID
argument_list|)
expr_stmt|;
name|container
operator|.
name|setUser
argument_list|(
name|applicationMasterContext
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|setupTokensAndEnv
argument_list|(
name|container
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
DECL|method|setupTokensAndEnv ( ContainerLaunchContext container)
specifier|private
name|void
name|setupTokensAndEnv
parameter_list|(
name|ContainerLaunchContext
name|container
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
comment|// Set the AppAttemptId, containerId, NMHTTPAdress, AppSubmitTime to be
comment|// consumable by the AM.
name|environment
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|AM_CONTAINER_ID_ENV
argument_list|,
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|NM_HOST_ENV
argument_list|,
name|application
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|NM_PORT_ENV
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|application
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|parts
index|[]
init|=
name|application
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getNodeHttpAddress
argument_list|()
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|NM_HTTP_PORT_ENV
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
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
name|application
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
comment|// TODO: Security enabled/disabled info should come from RM.
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
if|if
condition|(
name|container
operator|.
name|getContainerTokens
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// TODO: Don't do this kind of checks everywhere.
name|dibb
operator|.
name|reset
argument_list|(
name|container
operator|.
name|getContainerTokens
argument_list|()
argument_list|)
expr_stmt|;
name|credentials
operator|.
name|readTokenStorageStream
argument_list|(
name|dibb
argument_list|)
expr_stmt|;
block|}
name|ApplicationTokenIdentifier
name|id
init|=
operator|new
name|ApplicationTokenIdentifier
argument_list|(
name|application
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|ApplicationTokenIdentifier
argument_list|>
name|token
init|=
operator|new
name|Token
argument_list|<
name|ApplicationTokenIdentifier
argument_list|>
argument_list|(
name|id
argument_list|,
name|this
operator|.
name|applicationTokenSecretManager
argument_list|)
decl_stmt|;
name|String
name|schedulerAddressStr
init|=
name|this
operator|.
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_ADDRESS
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|unresolvedAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|schedulerAddressStr
argument_list|)
decl_stmt|;
name|String
name|resolvedAddr
init|=
name|unresolvedAddr
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|":"
operator|+
name|unresolvedAddr
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|token
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|(
name|resolvedAddr
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|appMasterTokenEncoded
init|=
name|token
operator|.
name|encodeToUrlString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Putting appMaster token in env : "
operator|+
name|appMasterTokenEncoded
argument_list|)
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|APPLICATION_MASTER_TOKEN_ENV_NAME
argument_list|,
name|appMasterTokenEncoded
argument_list|)
expr_stmt|;
comment|// Add the RM token
name|credentials
operator|.
name|addToken
argument_list|(
operator|new
name|Text
argument_list|(
name|resolvedAddr
argument_list|)
argument_list|,
name|token
argument_list|)
expr_stmt|;
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
name|setContainerTokens
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
name|ClientTokenIdentifier
name|identifier
init|=
operator|new
name|ClientTokenIdentifier
argument_list|(
name|application
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|SecretKey
name|clientSecretKey
init|=
name|this
operator|.
name|clientToAMSecretManager
operator|.
name|getMasterKey
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
name|String
name|encoded
init|=
name|Base64
operator|.
name|encodeBase64URLSafeString
argument_list|(
name|clientSecretKey
operator|.
name|getEncoded
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"The encoded client secret-key to be put in env : "
operator|+
name|encoded
argument_list|)
expr_stmt|;
name|environment
operator|.
name|put
argument_list|(
name|ApplicationConstants
operator|.
name|APPLICATION_CLIENT_SECRET_ENV_NAME
argument_list|,
name|encoded
argument_list|)
expr_stmt|;
block|}
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
name|RMAppAttemptLaunchFailedEvent
argument_list|(
name|application
operator|.
name|getAppAttemptId
argument_list|()
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
block|}
end_class

end_unit

