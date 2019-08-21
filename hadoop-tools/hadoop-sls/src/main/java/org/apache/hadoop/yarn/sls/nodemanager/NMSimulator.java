begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.nodemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|nodemanager
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
name|Collections
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|DelayQueue
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_import
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
name|ContainerExitStatus
import|;
end_import

begin_import
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
name|NodeLabel
import|;
end_import

begin_import
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
name|ResourceUtilization
import|;
end_import

begin_import
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
name|ResourceManager
import|;
end_import

begin_import
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
name|util
operator|.
name|Records
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|scheduler
operator|.
name|ContainerSimulator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|scheduler
operator|.
name|TaskRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|utils
operator|.
name|SLSUtils
import|;
end_import

begin_import
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
name|resource
operator|.
name|Resources
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|NMSimulator
specifier|public
class|class
name|NMSimulator
extends|extends
name|TaskRunner
operator|.
name|Task
block|{
comment|// node resource
DECL|field|node
specifier|private
name|RMNode
name|node
decl_stmt|;
comment|// master key
DECL|field|masterKey
specifier|private
name|MasterKey
name|masterKey
decl_stmt|;
comment|// containers with various STATE
DECL|field|completedContainerList
specifier|private
name|List
argument_list|<
name|ContainerId
argument_list|>
name|completedContainerList
decl_stmt|;
DECL|field|releasedContainerList
specifier|private
name|List
argument_list|<
name|ContainerId
argument_list|>
name|releasedContainerList
decl_stmt|;
DECL|field|containerQueue
specifier|private
name|DelayQueue
argument_list|<
name|ContainerSimulator
argument_list|>
name|containerQueue
decl_stmt|;
DECL|field|runningContainers
specifier|private
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerSimulator
argument_list|>
name|runningContainers
decl_stmt|;
DECL|field|amContainerList
specifier|private
name|List
argument_list|<
name|ContainerId
argument_list|>
name|amContainerList
decl_stmt|;
comment|// resource manager
DECL|field|rm
specifier|private
name|ResourceManager
name|rm
decl_stmt|;
comment|// heart beat response id
DECL|field|responseId
specifier|private
name|int
name|responseId
init|=
literal|0
decl_stmt|;
DECL|field|resourceUtilizationRatio
specifier|private
name|float
name|resourceUtilizationRatio
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NMSimulator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|init (String nodeIdStr, Resource nodeResource, int dispatchTime, int heartBeatInterval, ResourceManager pRm, float pResourceUtilizationRatio, Set<NodeLabel> labels)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|nodeIdStr
parameter_list|,
name|Resource
name|nodeResource
parameter_list|,
name|int
name|dispatchTime
parameter_list|,
name|int
name|heartBeatInterval
parameter_list|,
name|ResourceManager
name|pRm
parameter_list|,
name|float
name|pResourceUtilizationRatio
parameter_list|,
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|labels
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|super
operator|.
name|init
argument_list|(
name|dispatchTime
argument_list|,
name|dispatchTime
operator|+
literal|1000000L
operator|*
name|heartBeatInterval
argument_list|,
name|heartBeatInterval
argument_list|)
expr_stmt|;
comment|// create resource
name|String
name|rackHostName
index|[]
init|=
name|SLSUtils
operator|.
name|getRackHostName
argument_list|(
name|nodeIdStr
argument_list|)
decl_stmt|;
name|this
operator|.
name|node
operator|=
name|NodeInfo
operator|.
name|newNodeInfo
argument_list|(
name|rackHostName
index|[
literal|0
index|]
argument_list|,
name|rackHostName
index|[
literal|1
index|]
argument_list|,
name|Resources
operator|.
name|clone
argument_list|(
name|nodeResource
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|rm
operator|=
name|pRm
expr_stmt|;
comment|// init data structures
name|completedContainerList
operator|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|releasedContainerList
operator|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|containerQueue
operator|=
operator|new
name|DelayQueue
argument_list|<
name|ContainerSimulator
argument_list|>
argument_list|()
expr_stmt|;
name|amContainerList
operator|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|runningContainers
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerSimulator
argument_list|>
argument_list|()
expr_stmt|;
comment|// register NM with RM
name|RegisterNodeManagerRequest
name|req
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|RegisterNodeManagerRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|req
operator|.
name|setNodeId
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|setResource
argument_list|(
name|node
operator|.
name|getTotalCapability
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|setNodeLabels
argument_list|(
name|labels
argument_list|)
expr_stmt|;
name|req
operator|.
name|setHttpPort
argument_list|(
literal|80
argument_list|)
expr_stmt|;
name|RegisterNodeManagerResponse
name|response
init|=
name|this
operator|.
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
operator|.
name|registerNodeManager
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|masterKey
operator|=
name|response
operator|.
name|getNMTokenMasterKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|resourceUtilizationRatio
operator|=
name|pResourceUtilizationRatio
expr_stmt|;
block|}
DECL|method|init (String nodeIdStr, Resource nodeResource, int dispatchTime, int heartBeatInterval, ResourceManager pRm, float pResourceUtilizationRatio)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|nodeIdStr
parameter_list|,
name|Resource
name|nodeResource
parameter_list|,
name|int
name|dispatchTime
parameter_list|,
name|int
name|heartBeatInterval
parameter_list|,
name|ResourceManager
name|pRm
parameter_list|,
name|float
name|pResourceUtilizationRatio
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|init
argument_list|(
name|nodeIdStr
argument_list|,
name|nodeResource
argument_list|,
name|dispatchTime
argument_list|,
name|heartBeatInterval
argument_list|,
name|pRm
argument_list|,
name|pResourceUtilizationRatio
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|firstStep ()
specifier|public
name|void
name|firstStep
parameter_list|()
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|middleStep ()
specifier|public
name|void
name|middleStep
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we check the lifetime for each running containers
name|ContainerSimulator
name|cs
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|completedContainerList
init|)
block|{
while|while
condition|(
operator|(
name|cs
operator|=
name|containerQueue
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|runningContainers
operator|.
name|remove
argument_list|(
name|cs
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|completedContainerList
operator|.
name|add
argument_list|(
name|cs
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Container {} has completed"
argument_list|,
name|cs
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// send heart beat
name|NodeHeartbeatRequest
name|beatRequest
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeHeartbeatRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|beatRequest
operator|.
name|setLastKnownNMTokenMasterKey
argument_list|(
name|masterKey
argument_list|)
expr_stmt|;
name|NodeStatus
name|ns
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|ns
operator|.
name|setContainersStatuses
argument_list|(
name|generateContainerStatusList
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|.
name|setNodeId
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|.
name|setKeepAliveApplications
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|ns
operator|.
name|setResponseId
argument_list|(
name|responseId
operator|++
argument_list|)
expr_stmt|;
name|ns
operator|.
name|setNodeHealthStatus
argument_list|(
name|NodeHealthStatus
operator|.
name|newInstance
argument_list|(
literal|true
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|//set node& containers utilization
if|if
condition|(
name|resourceUtilizationRatio
operator|>
literal|0
operator|&&
name|resourceUtilizationRatio
operator|<=
literal|1
condition|)
block|{
name|int
name|pMemUsed
init|=
name|Math
operator|.
name|round
argument_list|(
name|node
operator|.
name|getTotalCapability
argument_list|()
operator|.
name|getMemorySize
argument_list|()
operator|*
name|resourceUtilizationRatio
argument_list|)
decl_stmt|;
name|float
name|cpuUsed
init|=
name|node
operator|.
name|getTotalCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
operator|*
name|resourceUtilizationRatio
decl_stmt|;
name|ResourceUtilization
name|resourceUtilization
init|=
name|ResourceUtilization
operator|.
name|newInstance
argument_list|(
name|pMemUsed
argument_list|,
name|pMemUsed
argument_list|,
name|cpuUsed
argument_list|)
decl_stmt|;
name|ns
operator|.
name|setContainersUtilization
argument_list|(
name|resourceUtilization
argument_list|)
expr_stmt|;
name|ns
operator|.
name|setNodeUtilization
argument_list|(
name|resourceUtilization
argument_list|)
expr_stmt|;
block|}
name|beatRequest
operator|.
name|setNodeStatus
argument_list|(
name|ns
argument_list|)
expr_stmt|;
name|NodeHeartbeatResponse
name|beatResponse
init|=
name|rm
operator|.
name|getResourceTrackerService
argument_list|()
operator|.
name|nodeHeartbeat
argument_list|(
name|beatRequest
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|beatResponse
operator|.
name|getContainersToCleanup
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// remove from queue
synchronized|synchronized
init|(
name|releasedContainerList
init|)
block|{
for|for
control|(
name|ContainerId
name|containerId
range|:
name|beatResponse
operator|.
name|getContainersToCleanup
argument_list|()
control|)
block|{
if|if
condition|(
name|amContainerList
operator|.
name|contains
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
comment|// AM container (not killed?, only release)
synchronized|synchronized
init|(
name|amContainerList
init|)
block|{
name|amContainerList
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"NodeManager {} releases an AM ({})."
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cs
operator|=
name|runningContainers
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|containerQueue
operator|.
name|remove
argument_list|(
name|cs
argument_list|)
expr_stmt|;
name|releasedContainerList
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"NodeManager {} releases a container ({})."
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|beatResponse
operator|.
name|getNodeAction
argument_list|()
operator|==
name|NodeAction
operator|.
name|SHUTDOWN
condition|)
block|{
name|lastStep
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|lastStep ()
specifier|public
name|void
name|lastStep
parameter_list|()
block|{
comment|// do nothing
block|}
comment|/**    * catch status of all containers located on current node    */
DECL|method|generateContainerStatusList ()
specifier|private
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
name|generateContainerStatusList
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
name|csList
init|=
operator|new
name|ArrayList
argument_list|<
name|ContainerStatus
argument_list|>
argument_list|()
decl_stmt|;
comment|// add running containers
for|for
control|(
name|ContainerSimulator
name|container
range|:
name|runningContainers
operator|.
name|values
argument_list|()
control|)
block|{
name|csList
operator|.
name|add
argument_list|(
name|newContainerStatus
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
name|ContainerExitStatus
operator|.
name|SUCCESS
argument_list|)
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|amContainerList
init|)
block|{
for|for
control|(
name|ContainerId
name|cId
range|:
name|amContainerList
control|)
block|{
name|csList
operator|.
name|add
argument_list|(
name|newContainerStatus
argument_list|(
name|cId
argument_list|,
name|ContainerState
operator|.
name|RUNNING
argument_list|,
name|ContainerExitStatus
operator|.
name|SUCCESS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add complete containers
synchronized|synchronized
init|(
name|completedContainerList
init|)
block|{
for|for
control|(
name|ContainerId
name|cId
range|:
name|completedContainerList
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NodeManager {} completed container ({})."
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|cId
argument_list|)
expr_stmt|;
name|csList
operator|.
name|add
argument_list|(
name|newContainerStatus
argument_list|(
name|cId
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
name|ContainerExitStatus
operator|.
name|SUCCESS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|completedContainerList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// released containers
synchronized|synchronized
init|(
name|releasedContainerList
init|)
block|{
for|for
control|(
name|ContainerId
name|cId
range|:
name|releasedContainerList
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NodeManager {} released container ({})."
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|cId
argument_list|)
expr_stmt|;
name|csList
operator|.
name|add
argument_list|(
name|newContainerStatus
argument_list|(
name|cId
argument_list|,
name|ContainerState
operator|.
name|COMPLETE
argument_list|,
name|ContainerExitStatus
operator|.
name|ABORTED
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|releasedContainerList
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|csList
return|;
block|}
DECL|method|newContainerStatus (ContainerId cId, ContainerState state, int exitState)
specifier|private
name|ContainerStatus
name|newContainerStatus
parameter_list|(
name|ContainerId
name|cId
parameter_list|,
name|ContainerState
name|state
parameter_list|,
name|int
name|exitState
parameter_list|)
block|{
name|ContainerStatus
name|cs
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|cs
operator|.
name|setContainerId
argument_list|(
name|cId
argument_list|)
expr_stmt|;
name|cs
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|cs
operator|.
name|setExitStatus
argument_list|(
name|exitState
argument_list|)
expr_stmt|;
return|return
name|cs
return|;
block|}
DECL|method|getNode ()
specifier|public
name|RMNode
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
comment|/**    * launch a new container with the given life time    */
DECL|method|addNewContainer (Container container, long lifeTimeMS)
specifier|public
name|void
name|addNewContainer
parameter_list|(
name|Container
name|container
parameter_list|,
name|long
name|lifeTimeMS
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"NodeManager {} launches a new container ({})."
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lifeTimeMS
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// normal container
name|ContainerSimulator
name|cs
init|=
operator|new
name|ContainerSimulator
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|,
name|lifeTimeMS
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|lifeTimeMS
argument_list|,
name|container
operator|.
name|getAllocationRequestId
argument_list|()
argument_list|)
decl_stmt|;
name|containerQueue
operator|.
name|add
argument_list|(
name|cs
argument_list|)
expr_stmt|;
name|runningContainers
operator|.
name|put
argument_list|(
name|cs
operator|.
name|getId
argument_list|()
argument_list|,
name|cs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// AM container
comment|// -1 means AMContainer
synchronized|synchronized
init|(
name|amContainerList
init|)
block|{
name|amContainerList
operator|.
name|add
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * clean up an AM container and add to completed list    * @param containerId id of the container to be cleaned    */
DECL|method|cleanupContainer (ContainerId containerId)
specifier|public
name|void
name|cleanupContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|amContainerList
init|)
block|{
name|amContainerList
operator|.
name|remove
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|completedContainerList
init|)
block|{
name|completedContainerList
operator|.
name|add
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRunningContainers ()
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerSimulator
argument_list|>
name|getRunningContainers
parameter_list|()
block|{
return|return
name|runningContainers
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAMContainers ()
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getAMContainers
parameter_list|()
block|{
return|return
name|amContainerList
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCompletedContainers ()
name|List
argument_list|<
name|ContainerId
argument_list|>
name|getCompletedContainers
parameter_list|()
block|{
return|return
name|completedContainerList
return|;
block|}
block|}
end_class

end_unit

