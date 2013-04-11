begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|net
operator|.
name|InetSocketAddress
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
name|net
operator|.
name|Node
import|;
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
name|yarn
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
name|NodeHeartbeatResponse
import|;
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
name|protocolrecords
operator|.
name|RegisterNodeManagerResponse
import|;
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
name|api
operator|.
name|records
operator|.
name|NodeAction
import|;
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|RMStateStore
operator|.
name|RMState
import|;
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
name|rmnode
operator|.
name|RMNodeEvent
import|;
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
name|RMNodeEventType
import|;
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
name|RMNodeImpl
import|;
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
name|RMNodeReconnectEvent
import|;
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
name|RMNodeStatusEvent
import|;
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
name|RMContainerTokenSecretManager
import|;
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
name|server
operator|.
name|utils
operator|.
name|YarnServerBuilderUtils
import|;
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
name|RackResolver
import|;
end_import

begin_class
DECL|class|ResourceTrackerService
specifier|public
class|class
name|ResourceTrackerService
extends|extends
name|AbstractService
implements|implements
name|ResourceTracker
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
name|ResourceTrackerService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
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
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|nodesListManager
specifier|private
specifier|final
name|NodesListManager
name|nodesListManager
decl_stmt|;
DECL|field|nmLivelinessMonitor
specifier|private
specifier|final
name|NMLivelinessMonitor
name|nmLivelinessMonitor
decl_stmt|;
DECL|field|containerTokenSecretManager
specifier|private
specifier|final
name|RMContainerTokenSecretManager
name|containerTokenSecretManager
decl_stmt|;
DECL|field|nextHeartBeatInterval
specifier|private
name|long
name|nextHeartBeatInterval
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|resourceTrackerAddress
specifier|private
name|InetSocketAddress
name|resourceTrackerAddress
decl_stmt|;
DECL|field|resync
specifier|private
specifier|static
specifier|final
name|NodeHeartbeatResponse
name|resync
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHeartbeatResponse
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|shutDown
specifier|private
specifier|static
specifier|final
name|NodeHeartbeatResponse
name|shutDown
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHeartbeatResponse
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|resync
operator|.
name|setNodeAction
argument_list|(
name|NodeAction
operator|.
name|RESYNC
argument_list|)
expr_stmt|;
name|shutDown
operator|.
name|setNodeAction
argument_list|(
name|NodeAction
operator|.
name|SHUTDOWN
argument_list|)
expr_stmt|;
block|}
DECL|method|ResourceTrackerService (RMContext rmContext, NodesListManager nodesListManager, NMLivelinessMonitor nmLivelinessMonitor, RMContainerTokenSecretManager containerTokenSecretManager)
specifier|public
name|ResourceTrackerService
parameter_list|(
name|RMContext
name|rmContext
parameter_list|,
name|NodesListManager
name|nodesListManager
parameter_list|,
name|NMLivelinessMonitor
name|nmLivelinessMonitor
parameter_list|,
name|RMContainerTokenSecretManager
name|containerTokenSecretManager
parameter_list|)
block|{
name|super
argument_list|(
name|ResourceTrackerService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|nodesListManager
operator|=
name|nodesListManager
expr_stmt|;
name|this
operator|.
name|nmLivelinessMonitor
operator|=
name|nmLivelinessMonitor
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
name|resourceTrackerAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_PORT
argument_list|)
expr_stmt|;
name|RackResolver
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|nextHeartBeatInterval
operator|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NM_HEARTBEAT_INTERVAL_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_NM_HEARTBEAT_INTERVAL_MS
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextHeartBeatInterval
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Invalid Configuration. "
operator|+
name|YarnConfiguration
operator|.
name|RM_NM_HEARTBEAT_INTERVAL_MS
operator|+
literal|" should be larger than 0."
argument_list|)
throw|;
block|}
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
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// ResourceTrackerServer authenticates NodeManager via Kerberos if
comment|// security is enabled, so no secretManager.
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
name|ResourceTracker
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|resourceTrackerAddress
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
name|RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT
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
name|conf
operator|.
name|updateConnectAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_RESOURCE_TRACKER_ADDRESS
argument_list|,
name|server
operator|.
name|getListenerAddress
argument_list|()
argument_list|)
expr_stmt|;
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|registerNodeManager ( RegisterNodeManagerRequest request)
specifier|public
name|RegisterNodeManagerResponse
name|registerNodeManager
parameter_list|(
name|RegisterNodeManagerRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|NodeId
name|nodeId
init|=
name|request
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|nodeId
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|int
name|cmPort
init|=
name|nodeId
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|int
name|httpPort
init|=
name|request
operator|.
name|getHttpPort
argument_list|()
decl_stmt|;
name|Resource
name|capability
init|=
name|request
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|RegisterNodeManagerResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterNodeManagerResponse
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Check if this node is a 'valid' node
if|if
condition|(
operator|!
name|this
operator|.
name|nodesListManager
operator|.
name|isValidNode
argument_list|(
name|host
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Disallowed NodeManager from  "
operator|+
name|host
operator|+
literal|", Sending SHUTDOWN signal to the NodeManager."
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNodeAction
argument_list|(
name|NodeAction
operator|.
name|SHUTDOWN
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
if|if
condition|(
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|MasterKey
name|nextMasterKeyForNode
init|=
name|this
operator|.
name|containerTokenSecretManager
operator|.
name|getCurrentKey
argument_list|()
decl_stmt|;
name|response
operator|.
name|setMasterKey
argument_list|(
name|nextMasterKeyForNode
argument_list|)
expr_stmt|;
block|}
name|RMNode
name|rmNode
init|=
operator|new
name|RMNodeImpl
argument_list|(
name|nodeId
argument_list|,
name|rmContext
argument_list|,
name|host
argument_list|,
name|cmPort
argument_list|,
name|httpPort
argument_list|,
name|resolve
argument_list|(
name|host
argument_list|)
argument_list|,
name|capability
argument_list|)
decl_stmt|;
name|RMNode
name|oldNode
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|putIfAbsent
argument_list|(
name|nodeId
argument_list|,
name|rmNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldNode
operator|==
literal|null
condition|)
block|{
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
name|RMNodeEvent
argument_list|(
name|nodeId
argument_list|,
name|RMNodeEventType
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reconnect from the node at: "
operator|+
name|host
argument_list|)
expr_stmt|;
name|this
operator|.
name|nmLivelinessMonitor
operator|.
name|unregister
argument_list|(
name|nodeId
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
name|RMNodeReconnectEvent
argument_list|(
name|nodeId
argument_list|,
name|rmNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|nmLivelinessMonitor
operator|.
name|register
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"NodeManager from node "
operator|+
name|host
operator|+
literal|"(cmPort: "
operator|+
name|cmPort
operator|+
literal|" httpPort: "
operator|+
name|httpPort
operator|+
literal|") "
operator|+
literal|"registered with capability: "
operator|+
name|capability
operator|+
literal|", assigned nodeId "
operator|+
name|nodeId
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNodeAction
argument_list|(
name|NodeAction
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
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
DECL|method|nodeHeartbeat (NodeHeartbeatRequest request)
specifier|public
name|NodeHeartbeatResponse
name|nodeHeartbeat
parameter_list|(
name|NodeHeartbeatRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|NodeStatus
name|remoteNodeStatus
init|=
name|request
operator|.
name|getNodeStatus
argument_list|()
decl_stmt|;
comment|/**      * Here is the node heartbeat sequence...      * 1. Check if it's a registered node      * 2. Check if it's a valid (i.e. not excluded) node       * 3. Check if it's a 'fresh' heartbeat i.e. not duplicate heartbeat       * 4. Send healthStatus to RMNode      */
name|NodeId
name|nodeId
init|=
name|remoteNodeStatus
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
comment|// 1. Check if it's a registered node
name|RMNode
name|rmNode
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmNode
operator|==
literal|null
condition|)
block|{
comment|/* node does not exist */
name|LOG
operator|.
name|info
argument_list|(
literal|"Node not found rebooting "
operator|+
name|remoteNodeStatus
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|resync
return|;
block|}
comment|// Send ping
name|this
operator|.
name|nmLivelinessMonitor
operator|.
name|receivedPing
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
comment|// 2. Check if it's a valid (i.e. not excluded) node
if|if
condition|(
operator|!
name|this
operator|.
name|nodesListManager
operator|.
name|isValidNode
argument_list|(
name|rmNode
operator|.
name|getHostName
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Disallowed NodeManager nodeId: "
operator|+
name|nodeId
operator|+
literal|" hostname: "
operator|+
name|rmNode
operator|.
name|getNodeAddress
argument_list|()
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
name|RMNodeEvent
argument_list|(
name|nodeId
argument_list|,
name|RMNodeEventType
operator|.
name|DECOMMISSION
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|shutDown
return|;
block|}
comment|// 3. Check if it's a 'fresh' heartbeat i.e. not duplicate heartbeat
name|NodeHeartbeatResponse
name|lastNodeHeartbeatResponse
init|=
name|rmNode
operator|.
name|getLastNodeHeartBeatResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteNodeStatus
operator|.
name|getResponseId
argument_list|()
operator|+
literal|1
operator|==
name|lastNodeHeartbeatResponse
operator|.
name|getResponseId
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received duplicate heartbeat from node "
operator|+
name|rmNode
operator|.
name|getNodeAddress
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lastNodeHeartbeatResponse
return|;
block|}
elseif|else
if|if
condition|(
name|remoteNodeStatus
operator|.
name|getResponseId
argument_list|()
operator|+
literal|1
operator|<
name|lastNodeHeartbeatResponse
operator|.
name|getResponseId
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Too far behind rm response id:"
operator|+
name|lastNodeHeartbeatResponse
operator|.
name|getResponseId
argument_list|()
operator|+
literal|" nm response id:"
operator|+
name|remoteNodeStatus
operator|.
name|getResponseId
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: Just sending reboot is not enough. Think more.
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
name|RMNodeEvent
argument_list|(
name|nodeId
argument_list|,
name|RMNodeEventType
operator|.
name|REBOOTING
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|resync
return|;
block|}
comment|// Heartbeat response
name|NodeHeartbeatResponse
name|nodeHeartBeatResponse
init|=
name|YarnServerBuilderUtils
operator|.
name|newNodeHeartbeatResponse
argument_list|(
name|lastNodeHeartbeatResponse
operator|.
name|getResponseId
argument_list|()
operator|+
literal|1
argument_list|,
name|NodeAction
operator|.
name|NORMAL
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|nextHeartBeatInterval
argument_list|)
decl_stmt|;
name|rmNode
operator|.
name|updateNodeHeartbeatResponseForCleanup
argument_list|(
name|nodeHeartBeatResponse
argument_list|)
expr_stmt|;
comment|// Check if node's masterKey needs to be updated and if the currentKey has
comment|// roller over, send it across
if|if
condition|(
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|boolean
name|shouldSendMasterKey
init|=
literal|false
decl_stmt|;
name|MasterKey
name|nextMasterKeyForNode
init|=
name|this
operator|.
name|containerTokenSecretManager
operator|.
name|getNextKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextMasterKeyForNode
operator|!=
literal|null
condition|)
block|{
comment|// nextMasterKeyForNode can be null if there is no outstanding key that
comment|// is in the activation period.
name|MasterKey
name|nodeKnownMasterKey
init|=
name|request
operator|.
name|getLastKnownMasterKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeKnownMasterKey
operator|.
name|getKeyId
argument_list|()
operator|!=
name|nextMasterKeyForNode
operator|.
name|getKeyId
argument_list|()
condition|)
block|{
name|shouldSendMasterKey
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shouldSendMasterKey
condition|)
block|{
name|nodeHeartBeatResponse
operator|.
name|setMasterKey
argument_list|(
name|nextMasterKeyForNode
argument_list|)
expr_stmt|;
block|}
block|}
comment|// 4. Send status to RMNode, saving the latest response.
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
name|RMNodeStatusEvent
argument_list|(
name|nodeId
argument_list|,
name|remoteNodeStatus
operator|.
name|getNodeHealthStatus
argument_list|()
argument_list|,
name|remoteNodeStatus
operator|.
name|getContainersStatuses
argument_list|()
argument_list|,
name|remoteNodeStatus
operator|.
name|getKeepAliveApplications
argument_list|()
argument_list|,
name|nodeHeartBeatResponse
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|nodeHeartBeatResponse
return|;
block|}
comment|/**    * resolving the network topology.    * @param hostName the hostname of this node.    * @return the resolved {@link Node} for this nodemanager.    */
DECL|method|resolve (String hostName)
specifier|public
specifier|static
name|Node
name|resolve
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
return|return
name|RackResolver
operator|.
name|resolve
argument_list|(
name|hostName
argument_list|)
return|;
block|}
DECL|method|refreshServiceAcls (Configuration configuration, PolicyProvider policyProvider)
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
DECL|method|isSecurityEnabled ()
specifier|protected
name|boolean
name|isSecurityEnabled
parameter_list|()
block|{
return|return
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
return|;
block|}
block|}
end_class

end_unit

