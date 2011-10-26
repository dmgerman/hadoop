begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
package|;
end_package

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
name|Iterator
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
operator|.
name|Entry
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
name|AvroRuntimeException
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
name|NodeHealthCheckerService
import|;
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
name|ContainerState
import|;
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
name|ContainerStatus
import|;
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
name|NodeHealthStatus
import|;
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
name|Dispatcher
import|;
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
name|RMNMSecurityInfoClass
import|;
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
name|ResourceTracker
import|;
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
name|NodeHeartbeatRequest
import|;
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
name|RegisterNodeManagerRequest
import|;
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
name|HeartbeatResponse
import|;
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
name|NodeStatus
import|;
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
name|RegistrationResponse
import|;
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
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
name|server
operator|.
name|nodemanager
operator|.
name|metrics
operator|.
name|NodeManagerMetrics
import|;
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
name|ContainerTokenSecretManager
import|;
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

begin_class
DECL|class|NodeStatusUpdaterImpl
specifier|public
class|class
name|NodeStatusUpdaterImpl
extends|extends
name|AbstractService
implements|implements
name|NodeStatusUpdater
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
name|NodeStatusUpdaterImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|heartbeatMonitor
specifier|private
specifier|final
name|Object
name|heartbeatMonitor
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
DECL|field|dispatcher
specifier|private
specifier|final
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|nodeId
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|containerTokenSecretManager
specifier|private
name|ContainerTokenSecretManager
name|containerTokenSecretManager
decl_stmt|;
DECL|field|heartBeatInterval
specifier|private
name|long
name|heartBeatInterval
decl_stmt|;
DECL|field|resourceTracker
specifier|private
name|ResourceTracker
name|resourceTracker
decl_stmt|;
DECL|field|rmAddress
specifier|private
name|String
name|rmAddress
decl_stmt|;
DECL|field|totalResource
specifier|private
name|Resource
name|totalResource
decl_stmt|;
DECL|field|httpPort
specifier|private
name|int
name|httpPort
decl_stmt|;
DECL|field|secretKeyBytes
specifier|private
name|byte
index|[]
name|secretKeyBytes
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
DECL|field|isStopped
specifier|private
name|boolean
name|isStopped
decl_stmt|;
DECL|field|recordFactory
specifier|private
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
DECL|field|healthChecker
specifier|private
specifier|final
name|NodeHealthCheckerService
name|healthChecker
decl_stmt|;
DECL|field|metrics
specifier|private
specifier|final
name|NodeManagerMetrics
name|metrics
decl_stmt|;
DECL|method|NodeStatusUpdaterImpl (Context context, Dispatcher dispatcher, NodeHealthCheckerService healthChecker, NodeManagerMetrics metrics, ContainerTokenSecretManager containerTokenSecretManager)
specifier|public
name|NodeStatusUpdaterImpl
parameter_list|(
name|Context
name|context
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|,
name|NodeHealthCheckerService
name|healthChecker
parameter_list|,
name|NodeManagerMetrics
name|metrics
parameter_list|,
name|ContainerTokenSecretManager
name|containerTokenSecretManager
parameter_list|)
block|{
name|super
argument_list|(
name|NodeStatusUpdaterImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|healthChecker
operator|=
name|healthChecker
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|dispatcher
operator|=
name|dispatcher
expr_stmt|;
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|containerTokenSecretManager
operator|=
name|containerTokenSecretManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|rmAddress
operator|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|)
expr_stmt|;
name|this
operator|.
name|heartBeatInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_TO_RM_HEARTBEAT_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_TO_RM_HEARTBEAT_INTERVAL_MS
argument_list|)
expr_stmt|;
name|int
name|memoryMb
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_PMEM_MB
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PMEM_MB
argument_list|)
decl_stmt|;
name|this
operator|.
name|totalResource
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|totalResource
operator|.
name|setMemory
argument_list|(
name|memoryMb
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|addResource
argument_list|(
name|totalResource
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
comment|// NodeManager is the last service to start, so NodeId is available.
name|this
operator|.
name|nodeId
operator|=
name|this
operator|.
name|context
operator|.
name|getNodeId
argument_list|()
expr_stmt|;
name|String
name|httpBindAddressStr
init|=
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_WEBAPP_ADDRESS
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|httpBindAddress
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|httpBindAddressStr
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_WEBAPP_PORT
argument_list|,
name|YarnConfiguration
operator|.
name|NM_WEBAPP_ADDRESS
argument_list|)
decl_stmt|;
try|try
block|{
comment|//      this.hostName = InetAddress.getLocalHost().getCanonicalHostName();
name|this
operator|.
name|httpPort
operator|=
name|httpBindAddress
operator|.
name|getPort
argument_list|()
expr_stmt|;
comment|// Registration has to be in start so that ContainerManager can get the
comment|// perNM tokens needed to authenticate ContainerTokens.
name|registerWithRM
argument_list|()
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
name|startStatusUpdater
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AvroRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
comment|// Interrupt the updater.
name|this
operator|.
name|isStopped
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getRMClient ()
specifier|protected
name|ResourceTracker
name|getRMClient
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
name|rmAddress
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|this
operator|.
name|rmAddress
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|,
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|)
decl_stmt|;
return|return
operator|(
name|ResourceTracker
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ResourceTracker
operator|.
name|class
argument_list|,
name|rmAddress
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|registerWithRM ()
specifier|private
name|void
name|registerWithRM
parameter_list|()
throws|throws
name|YarnRemoteException
block|{
name|this
operator|.
name|resourceTracker
operator|=
name|getRMClient
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connected to ResourceManager at "
operator|+
name|this
operator|.
name|rmAddress
argument_list|)
expr_stmt|;
name|RegisterNodeManagerRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterNodeManagerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setHttpPort
argument_list|(
name|this
operator|.
name|httpPort
argument_list|)
expr_stmt|;
name|request
operator|.
name|setResource
argument_list|(
name|this
operator|.
name|totalResource
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNodeId
argument_list|(
name|this
operator|.
name|nodeId
argument_list|)
expr_stmt|;
name|RegistrationResponse
name|regResponse
init|=
name|this
operator|.
name|resourceTracker
operator|.
name|registerNodeManager
argument_list|(
name|request
argument_list|)
operator|.
name|getRegistrationResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|secretKeyBytes
operator|=
name|regResponse
operator|.
name|getSecretKey
argument_list|()
operator|.
name|array
argument_list|()
expr_stmt|;
block|}
comment|// do this now so that its set before we start heartbeating to RM
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Security enabled - updating secret keys now"
argument_list|)
expr_stmt|;
comment|// It is expected that status updater is started by this point and
comment|// RM gives the shared secret in registration during StatusUpdater#start().
name|this
operator|.
name|containerTokenSecretManager
operator|.
name|setSecretKey
argument_list|(
name|this
operator|.
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|,
name|this
operator|.
name|getRMNMSharedSecret
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered with ResourceManager as "
operator|+
name|this
operator|.
name|nodeId
operator|+
literal|" with total resource of "
operator|+
name|this
operator|.
name|totalResource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRMNMSharedSecret ()
specifier|public
name|byte
index|[]
name|getRMNMSharedSecret
parameter_list|()
block|{
return|return
name|this
operator|.
name|secretKeyBytes
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|getNodeStatus ()
specifier|private
name|NodeStatus
name|getNodeStatus
parameter_list|()
block|{
name|NodeStatus
name|nodeStatus
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeStatus
operator|.
name|setNodeId
argument_list|(
name|this
operator|.
name|nodeId
argument_list|)
expr_stmt|;
name|int
name|numActiveContainers
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|containersStatuses
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
argument_list|>
name|i
init|=
name|this
operator|.
name|context
operator|.
name|getContainers
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|e
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Clone the container to send it to the RM
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
name|ContainerStatus
name|containerStatus
init|=
name|container
operator|.
name|cloneAndGetContainerStatus
argument_list|()
decl_stmt|;
name|containersStatuses
operator|.
name|add
argument_list|(
name|containerStatus
argument_list|)
expr_stmt|;
operator|++
name|numActiveContainers
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending out status for container: "
operator|+
name|containerStatus
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerStatus
operator|.
name|getState
argument_list|()
operator|==
name|ContainerState
operator|.
name|COMPLETE
condition|)
block|{
comment|// Remove
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed completed container "
operator|+
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
name|nodeStatus
operator|.
name|setContainersStatuses
argument_list|(
name|containersStatuses
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|this
operator|.
name|nodeId
operator|+
literal|" sending out status for "
operator|+
name|numActiveContainers
operator|+
literal|" containers"
argument_list|)
expr_stmt|;
name|NodeHealthStatus
name|nodeHealthStatus
init|=
name|this
operator|.
name|context
operator|.
name|getNodeHealthStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|healthChecker
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|healthChecker
operator|.
name|setHealthStatus
argument_list|(
name|nodeHealthStatus
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node's health-status : "
operator|+
name|nodeHealthStatus
operator|.
name|getIsNodeHealthy
argument_list|()
operator|+
literal|", "
operator|+
name|nodeHealthStatus
operator|.
name|getHealthReport
argument_list|()
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|setNodeHealthStatus
argument_list|(
name|nodeHealthStatus
argument_list|)
expr_stmt|;
return|return
name|nodeStatus
return|;
block|}
annotation|@
name|Override
DECL|method|sendOutofBandHeartBeat ()
specifier|public
name|void
name|sendOutofBandHeartBeat
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
operator|.
name|heartbeatMonitor
init|)
block|{
name|this
operator|.
name|heartbeatMonitor
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|startStatusUpdater ()
specifier|protected
name|void
name|startStatusUpdater
parameter_list|()
block|{
operator|new
name|Thread
argument_list|(
literal|"Node Status Updater"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|lastHeartBeatID
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|isStopped
condition|)
block|{
comment|// Send heartbeat
try|try
block|{
synchronized|synchronized
init|(
name|heartbeatMonitor
init|)
block|{
name|heartbeatMonitor
operator|.
name|wait
argument_list|(
name|heartBeatInterval
argument_list|)
expr_stmt|;
block|}
name|NodeStatus
name|nodeStatus
init|=
name|getNodeStatus
argument_list|()
decl_stmt|;
name|nodeStatus
operator|.
name|setResponseId
argument_list|(
name|lastHeartBeatID
argument_list|)
expr_stmt|;
name|NodeHeartbeatRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHeartbeatRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setNodeStatus
argument_list|(
name|nodeStatus
argument_list|)
expr_stmt|;
name|HeartbeatResponse
name|response
init|=
name|resourceTracker
operator|.
name|nodeHeartbeat
argument_list|(
name|request
argument_list|)
operator|.
name|getHeartbeatResponse
argument_list|()
decl_stmt|;
name|lastHeartBeatID
operator|=
name|response
operator|.
name|getResponseId
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToCleanup
init|=
name|response
operator|.
name|getContainersToCleanupList
argument_list|()
decl_stmt|;
if|if
condition|(
name|containersToCleanup
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|CMgrCompletedContainersEvent
argument_list|(
name|containersToCleanup
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|appsToCleanup
init|=
name|response
operator|.
name|getApplicationsToCleanupList
argument_list|()
decl_stmt|;
if|if
condition|(
name|appsToCleanup
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|CMgrCompletedAppsEvent
argument_list|(
name|appsToCleanup
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Caught exception in status-updater"
argument_list|,
name|e
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

