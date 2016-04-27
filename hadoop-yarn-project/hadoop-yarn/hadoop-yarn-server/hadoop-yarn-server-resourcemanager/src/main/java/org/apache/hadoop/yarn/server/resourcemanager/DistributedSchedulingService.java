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
name|ipc
operator|.
name|RPC
import|;
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationMasterProtocolPB
import|;
end_import

begin_import
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
name|api
operator|.
name|impl
operator|.
name|pb
operator|.
name|service
operator|.
name|ApplicationMasterProtocolPBServiceImpl
import|;
end_import

begin_import
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
name|proto
operator|.
name|ApplicationMasterProtocol
operator|.
name|ApplicationMasterProtocolService
import|;
end_import

begin_import
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
name|scheduler
operator|.
name|distributed
operator|.
name|TopKNodeSelector
import|;
end_import

begin_import
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
name|event
operator|.
name|NodeAddedSchedulerEvent
import|;
end_import

begin_import
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
name|event
operator|.
name|NodeRemovedSchedulerEvent
import|;
end_import

begin_import
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
name|event
operator|.
name|NodeResourceUpdateSchedulerEvent
import|;
end_import

begin_import
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
name|event
operator|.
name|NodeUpdateSchedulerEvent
import|;
end_import

begin_import
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
name|event
operator|.
name|SchedulerEvent
import|;
end_import

begin_import
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
name|AMRMTokenSecretManager
import|;
end_import

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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_comment
comment|/**  * The DistributedSchedulingService is started instead of the  * ApplicationMasterService if DistributedScheduling is enabled for the YARN  * cluster.  * It extends the functionality of the ApplicationMasterService by servicing  * clients (AMs and AMRMProxy request interceptors) that understand the  * DistributedSchedulingProtocol.  */
end_comment

begin_class
DECL|class|DistributedSchedulingService
specifier|public
class|class
name|DistributedSchedulingService
extends|extends
name|ApplicationMasterService
implements|implements
name|DistributedSchedulerProtocol
implements|,
name|EventHandler
argument_list|<
name|SchedulerEvent
argument_list|>
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
name|DistributedSchedulingService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|clusterMonitor
specifier|private
specifier|final
name|TopKNodeSelector
name|clusterMonitor
decl_stmt|;
DECL|field|rackToNode
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|rackToNode
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|hostToNode
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|hostToNode
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|DistributedSchedulingService (RMContext rmContext, YarnScheduler scheduler)
specifier|public
name|DistributedSchedulingService
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
name|DistributedSchedulingService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|rmContext
argument_list|,
name|scheduler
argument_list|)
expr_stmt|;
name|int
name|k
init|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_TOP_K
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_TOP_K_DEFAULT
argument_list|)
decl_stmt|;
name|long
name|topKComputationInterval
init|=
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_TOP_K_COMPUTE_INT_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_TOP_K_COMPUTE_INT_MS_DEFAULT
argument_list|)
decl_stmt|;
name|TopKNodeSelector
operator|.
name|TopKComparator
name|comparator
init|=
name|TopKNodeSelector
operator|.
name|TopKComparator
operator|.
name|valueOf
argument_list|(
name|rmContext
operator|.
name|getYarnConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_TOP_K_COMPARATOR
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_TOP_K_COMPARATOR_DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
name|TopKNodeSelector
name|topKSelector
init|=
operator|new
name|TopKNodeSelector
argument_list|(
name|k
argument_list|,
name|topKComputationInterval
argument_list|,
name|comparator
argument_list|)
decl_stmt|;
name|this
operator|.
name|clusterMonitor
operator|=
name|topKSelector
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getServer (YarnRPC rpc, Configuration serverConf, InetSocketAddress addr, AMRMTokenSecretManager secretManager)
specifier|public
name|Server
name|getServer
parameter_list|(
name|YarnRPC
name|rpc
parameter_list|,
name|Configuration
name|serverConf
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|AMRMTokenSecretManager
name|secretManager
parameter_list|)
block|{
name|Server
name|server
init|=
name|rpc
operator|.
name|getServer
argument_list|(
name|DistributedSchedulerProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|,
name|addr
argument_list|,
name|serverConf
argument_list|,
name|secretManager
argument_list|,
name|serverConf
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
decl_stmt|;
comment|// To support application running on NMs that DO NOT support
comment|// Dist Scheduling... The server multiplexes both the
comment|// ApplicationMasterProtocol as well as the DistributedSchedulingProtocol
operator|(
operator|(
name|RPC
operator|.
name|Server
operator|)
name|server
operator|)
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|ApplicationMasterProtocolPB
operator|.
name|class
argument_list|,
name|ApplicationMasterProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
operator|new
name|ApplicationMasterProtocolPBServiceImpl
argument_list|(
name|this
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|server
return|;
block|}
annotation|@
name|Override
DECL|method|registerApplicationMaster (RegisterApplicationMasterRequest request)
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
name|super
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finishApplicationMaster (FinishApplicationMasterRequest request)
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
name|super
operator|.
name|finishApplicationMaster
argument_list|(
name|request
argument_list|)
return|;
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
name|YarnException
throws|,
name|IOException
block|{
return|return
name|super
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
DECL|method|registerApplicationMasterForDistributedScheduling ( RegisterApplicationMasterRequest request)
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
name|RegisterApplicationMasterResponse
name|response
init|=
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|DistSchedRegisterResponse
name|dsResp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|DistSchedRegisterResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|dsResp
operator|.
name|setRegisterResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setMinAllocatableCapabilty
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_MIN_MEMORY
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_MIN_MEMORY_DEFAULT
argument_list|)
argument_list|,
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_MIN_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_MIN_VCORES_DEFAULT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setMaxAllocatableCapabilty
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_MAX_MEMORY
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_MAX_MEMORY_DEFAULT
argument_list|)
argument_list|,
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_MAX_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_MAX_VCORES_DEFAULT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setIncrAllocatableCapabilty
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_INCR_MEMORY
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_INCR_MEMORY_DEFAULT
argument_list|)
argument_list|,
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_INCR_VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_INCR_VCORES_DEFAULT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setContainerTokenExpiryInterval
argument_list|(
name|getConfig
argument_list|()
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_CONTAINER_TOKEN_EXPIRY_MS
argument_list|,
name|YarnConfiguration
operator|.
name|DIST_SCHEDULING_CONTAINER_TOKEN_EXPIRY_MS_DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setContainerIdStart
argument_list|(
name|this
operator|.
name|rmContext
operator|.
name|getEpoch
argument_list|()
operator|<<
name|ResourceManager
operator|.
name|EPOCH_BIT_SHIFT
argument_list|)
expr_stmt|;
comment|// Set nodes to be used for scheduling
name|dsResp
operator|.
name|setNodesForScheduling
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|this
operator|.
name|clusterMonitor
operator|.
name|selectNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|dsResp
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
name|AllocateResponse
name|response
init|=
name|allocate
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|DistSchedAllocateResponse
name|dsResp
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|DistSchedAllocateResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|dsResp
operator|.
name|setAllocateResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|dsResp
operator|.
name|setNodesForScheduling
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|this
operator|.
name|clusterMonitor
operator|.
name|selectNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|dsResp
return|;
block|}
DECL|method|addToMapping (ConcurrentHashMap<String, Set<NodeId>> mapping, String rackName, NodeId nodeId)
specifier|private
name|void
name|addToMapping
parameter_list|(
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|mapping
parameter_list|,
name|String
name|rackName
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|rackName
operator|!=
literal|null
condition|)
block|{
name|mapping
operator|.
name|putIfAbsent
argument_list|(
name|rackName
argument_list|,
operator|new
name|HashSet
argument_list|<
name|NodeId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|NodeId
argument_list|>
name|nodeIds
init|=
name|mapping
operator|.
name|get
argument_list|(
name|rackName
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|nodeIds
init|)
block|{
name|nodeIds
operator|.
name|add
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|removeFromMapping (ConcurrentHashMap<String, Set<NodeId>> mapping, String rackName, NodeId nodeId)
specifier|private
name|void
name|removeFromMapping
parameter_list|(
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeId
argument_list|>
argument_list|>
name|mapping
parameter_list|,
name|String
name|rackName
parameter_list|,
name|NodeId
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|rackName
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|NodeId
argument_list|>
name|nodeIds
init|=
name|mapping
operator|.
name|get
argument_list|(
name|rackName
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|nodeIds
init|)
block|{
name|nodeIds
operator|.
name|remove
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|handle (SchedulerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|SchedulerEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|NODE_ADDED
case|:
if|if
condition|(
operator|!
operator|(
name|event
operator|instanceof
name|NodeAddedSchedulerEvent
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected event type: "
operator|+
name|event
argument_list|)
throw|;
block|}
name|NodeAddedSchedulerEvent
name|nodeAddedEvent
init|=
operator|(
name|NodeAddedSchedulerEvent
operator|)
name|event
decl_stmt|;
name|clusterMonitor
operator|.
name|addNode
argument_list|(
name|nodeAddedEvent
operator|.
name|getContainerReports
argument_list|()
argument_list|,
name|nodeAddedEvent
operator|.
name|getAddedRMNode
argument_list|()
argument_list|)
expr_stmt|;
name|addToMapping
argument_list|(
name|rackToNode
argument_list|,
name|nodeAddedEvent
operator|.
name|getAddedRMNode
argument_list|()
operator|.
name|getRackName
argument_list|()
argument_list|,
name|nodeAddedEvent
operator|.
name|getAddedRMNode
argument_list|()
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|addToMapping
argument_list|(
name|hostToNode
argument_list|,
name|nodeAddedEvent
operator|.
name|getAddedRMNode
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|,
name|nodeAddedEvent
operator|.
name|getAddedRMNode
argument_list|()
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NODE_REMOVED
case|:
if|if
condition|(
operator|!
operator|(
name|event
operator|instanceof
name|NodeRemovedSchedulerEvent
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected event type: "
operator|+
name|event
argument_list|)
throw|;
block|}
name|NodeRemovedSchedulerEvent
name|nodeRemovedEvent
init|=
operator|(
name|NodeRemovedSchedulerEvent
operator|)
name|event
decl_stmt|;
name|clusterMonitor
operator|.
name|removeNode
argument_list|(
name|nodeRemovedEvent
operator|.
name|getRemovedRMNode
argument_list|()
argument_list|)
expr_stmt|;
name|removeFromMapping
argument_list|(
name|rackToNode
argument_list|,
name|nodeRemovedEvent
operator|.
name|getRemovedRMNode
argument_list|()
operator|.
name|getRackName
argument_list|()
argument_list|,
name|nodeRemovedEvent
operator|.
name|getRemovedRMNode
argument_list|()
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|removeFromMapping
argument_list|(
name|hostToNode
argument_list|,
name|nodeRemovedEvent
operator|.
name|getRemovedRMNode
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|,
name|nodeRemovedEvent
operator|.
name|getRemovedRMNode
argument_list|()
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NODE_UPDATE
case|:
if|if
condition|(
operator|!
operator|(
name|event
operator|instanceof
name|NodeUpdateSchedulerEvent
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected event type: "
operator|+
name|event
argument_list|)
throw|;
block|}
name|NodeUpdateSchedulerEvent
name|nodeUpdatedEvent
init|=
operator|(
name|NodeUpdateSchedulerEvent
operator|)
name|event
decl_stmt|;
name|clusterMonitor
operator|.
name|nodeUpdate
argument_list|(
name|nodeUpdatedEvent
operator|.
name|getRMNode
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NODE_RESOURCE_UPDATE
case|:
if|if
condition|(
operator|!
operator|(
name|event
operator|instanceof
name|NodeResourceUpdateSchedulerEvent
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unexpected event type: "
operator|+
name|event
argument_list|)
throw|;
block|}
name|NodeResourceUpdateSchedulerEvent
name|nodeResourceUpdatedEvent
init|=
operator|(
name|NodeResourceUpdateSchedulerEvent
operator|)
name|event
decl_stmt|;
name|clusterMonitor
operator|.
name|updateNodeResource
argument_list|(
name|nodeResourceUpdatedEvent
operator|.
name|getRMNode
argument_list|()
argument_list|,
name|nodeResourceUpdatedEvent
operator|.
name|getResourceOption
argument_list|()
argument_list|)
expr_stmt|;
break|break;
comment|//<-- IGNORED EVENTS : START -->
case|case
name|APP_ADDED
case|:
break|break;
case|case
name|APP_REMOVED
case|:
break|break;
case|case
name|APP_ATTEMPT_ADDED
case|:
break|break;
case|case
name|APP_ATTEMPT_REMOVED
case|:
break|break;
case|case
name|CONTAINER_EXPIRED
case|:
break|break;
case|case
name|NODE_LABELS_UPDATE
case|:
break|break;
comment|//<-- IGNORED EVENTS : END -->
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown event arrived at DistributedSchedulingService: "
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

