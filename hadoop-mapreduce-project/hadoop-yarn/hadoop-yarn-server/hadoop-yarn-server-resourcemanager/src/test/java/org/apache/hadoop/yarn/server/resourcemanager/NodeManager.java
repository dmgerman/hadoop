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
name|HashMap
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
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|GetContainerStatusRequest
import|;
end_import

begin_import
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
name|GetContainerStatusResponse
import|;
end_import

begin_import
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
name|StartContainerResponse
import|;
end_import

begin_import
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
name|protocolrecords
operator|.
name|StopContainerResponse
import|;
end_import

begin_import
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
name|Priority
import|;
end_import

begin_import
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
name|resourcemanager
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
name|SchedulerNode
import|;
end_import

begin_import
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
name|Private
DECL|class|NodeManager
specifier|public
class|class
name|NodeManager
implements|implements
name|ContainerManager
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
name|NodeManager
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
DECL|field|containerManagerAddress
specifier|final
specifier|private
name|String
name|containerManagerAddress
decl_stmt|;
DECL|field|nodeHttpAddress
specifier|final
specifier|private
name|String
name|nodeHttpAddress
decl_stmt|;
DECL|field|rackName
specifier|final
specifier|private
name|String
name|rackName
decl_stmt|;
DECL|field|nodeId
specifier|final
specifier|private
name|NodeId
name|nodeId
decl_stmt|;
DECL|field|capability
specifier|final
specifier|private
name|Resource
name|capability
decl_stmt|;
DECL|field|available
name|Resource
name|available
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|used
name|Resource
name|used
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceTrackerService
specifier|final
name|ResourceTrackerService
name|resourceTrackerService
decl_stmt|;
DECL|field|schedulerNode
specifier|final
name|SchedulerNode
name|schedulerNode
decl_stmt|;
DECL|field|containers
specifier|final
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|containers
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|NodeManager (String hostName, int containerManagerPort, int httpPort, String rackName, int memory, ResourceTrackerService resourceTrackerService, RMContext rmContext)
specifier|public
name|NodeManager
parameter_list|(
name|String
name|hostName
parameter_list|,
name|int
name|containerManagerPort
parameter_list|,
name|int
name|httpPort
parameter_list|,
name|String
name|rackName
parameter_list|,
name|int
name|memory
parameter_list|,
name|ResourceTrackerService
name|resourceTrackerService
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|containerManagerAddress
operator|=
name|hostName
operator|+
literal|":"
operator|+
name|containerManagerPort
expr_stmt|;
name|this
operator|.
name|nodeHttpAddress
operator|=
name|hostName
operator|+
literal|":"
operator|+
name|httpPort
expr_stmt|;
name|this
operator|.
name|rackName
operator|=
name|rackName
expr_stmt|;
name|this
operator|.
name|resourceTrackerService
operator|=
name|resourceTrackerService
expr_stmt|;
name|this
operator|.
name|capability
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
name|memory
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|available
argument_list|,
name|capability
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeId
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeId
operator|.
name|setHost
argument_list|(
name|hostName
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeId
operator|.
name|setPort
argument_list|(
name|containerManagerPort
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
name|httpPort
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
name|request
operator|.
name|setResource
argument_list|(
name|capability
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
name|resourceTrackerService
operator|.
name|registerNodeManager
argument_list|(
name|request
argument_list|)
operator|.
name|getRegistrationResponse
argument_list|()
expr_stmt|;
name|this
operator|.
name|schedulerNode
operator|=
operator|new
name|SchedulerNode
argument_list|(
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|this
operator|.
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
comment|// Sanity check
name|Assert
operator|.
name|assertEquals
argument_list|(
name|memory
argument_list|,
name|schedulerNode
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|containerManagerAddress
return|;
block|}
DECL|method|getRackName ()
specifier|public
name|String
name|getRackName
parameter_list|()
block|{
return|return
name|rackName
return|;
block|}
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
DECL|method|getCapability ()
specifier|public
name|Resource
name|getCapability
parameter_list|()
block|{
return|return
name|capability
return|;
block|}
DECL|method|getAvailable ()
specifier|public
name|Resource
name|getAvailable
parameter_list|()
block|{
return|return
name|available
return|;
block|}
DECL|method|getUsed ()
specifier|public
name|Resource
name|getUsed
parameter_list|()
block|{
return|return
name|used
return|;
block|}
DECL|field|responseID
name|int
name|responseID
init|=
literal|0
decl_stmt|;
DECL|method|getContainerStatuses (Map<ApplicationId, List<Container>> containers)
specifier|private
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|getContainerStatuses
parameter_list|(
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|List
argument_list|<
name|Container
argument_list|>
argument_list|>
name|containers
parameter_list|)
block|{
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|containerStatuses
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
name|List
argument_list|<
name|Container
argument_list|>
name|appContainers
range|:
name|containers
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Container
name|container
range|:
name|appContainers
control|)
block|{
name|containerStatuses
operator|.
name|add
argument_list|(
name|container
operator|.
name|getContainerStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|containerStatuses
return|;
block|}
DECL|method|heartbeat ()
specifier|public
name|void
name|heartbeat
parameter_list|()
throws|throws
name|IOException
block|{
name|NodeStatus
name|nodeStatus
init|=
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
name|NodeManager
operator|.
name|createNodeStatus
argument_list|(
name|nodeId
argument_list|,
name|getContainerStatuses
argument_list|(
name|containers
argument_list|)
argument_list|)
decl_stmt|;
name|nodeStatus
operator|.
name|setResponseId
argument_list|(
name|responseID
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
name|resourceTrackerService
operator|.
name|nodeHeartbeat
argument_list|(
name|request
argument_list|)
operator|.
name|getHeartbeatResponse
argument_list|()
decl_stmt|;
name|responseID
operator|=
name|response
operator|.
name|getResponseId
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startContainer ( StartContainerRequest request)
specifier|synchronized
specifier|public
name|StartContainerResponse
name|startContainer
parameter_list|(
name|StartContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ContainerLaunchContext
name|containerLaunchContext
init|=
name|request
operator|.
name|getContainerLaunchContext
argument_list|()
decl_stmt|;
name|ApplicationId
name|applicationId
init|=
name|containerLaunchContext
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|applicationContainers
init|=
name|containers
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|applicationContainers
operator|==
literal|null
condition|)
block|{
name|applicationContainers
operator|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|()
expr_stmt|;
name|containers
operator|.
name|put
argument_list|(
name|applicationId
argument_list|,
name|applicationContainers
argument_list|)
expr_stmt|;
block|}
comment|// Sanity check
for|for
control|(
name|Container
name|container
range|:
name|applicationContainers
control|)
block|{
if|if
condition|(
name|container
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|containerLaunchContext
operator|.
name|getContainerId
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Container "
operator|+
name|containerLaunchContext
operator|.
name|getContainerId
argument_list|()
operator|+
literal|" already setup on node "
operator|+
name|containerManagerAddress
argument_list|)
throw|;
block|}
block|}
name|Container
name|container
init|=
name|BuilderUtils
operator|.
name|newContainer
argument_list|(
name|containerLaunchContext
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|this
operator|.
name|nodeId
argument_list|,
name|nodeHttpAddress
argument_list|,
name|containerLaunchContext
operator|.
name|getResource
argument_list|()
argument_list|,
literal|null
comment|// DKDC - Doesn't matter
argument_list|)
decl_stmt|;
name|applicationContainers
operator|.
name|add
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|available
argument_list|,
name|containerLaunchContext
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|used
argument_list|,
name|containerLaunchContext
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DEBUG --- startContainer:"
operator|+
literal|" node="
operator|+
name|containerManagerAddress
operator|+
literal|" application="
operator|+
name|applicationId
operator|+
literal|" container="
operator|+
name|container
operator|+
literal|" available="
operator|+
name|available
operator|+
literal|" used="
operator|+
name|used
argument_list|)
expr_stmt|;
name|StartContainerResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StartContainerResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
DECL|method|checkResourceUsage ()
specifier|synchronized
specifier|public
name|void
name|checkResourceUsage
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Checking resource usage for "
operator|+
name|containerManagerAddress
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|available
operator|.
name|getMemory
argument_list|()
argument_list|,
name|schedulerNode
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|used
operator|.
name|getMemory
argument_list|()
argument_list|,
name|schedulerNode
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stopContainer (StopContainerRequest request)
specifier|synchronized
specifier|public
name|StopContainerResponse
name|stopContainer
parameter_list|(
name|StopContainerRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ContainerId
name|containerID
init|=
name|request
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|String
name|applicationId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|containerID
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Mark the container as COMPLETE
name|List
argument_list|<
name|Container
argument_list|>
name|applicationContainers
init|=
name|containers
operator|.
name|get
argument_list|(
name|applicationId
argument_list|)
decl_stmt|;
for|for
control|(
name|Container
name|c
range|:
name|applicationContainers
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|containerID
argument_list|)
operator|==
literal|0
condition|)
block|{
name|c
operator|.
name|setState
argument_list|(
name|ContainerState
operator|.
name|COMPLETE
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Send a heartbeat
try|try
block|{
name|heartbeat
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
comment|// Remove container and update status
name|int
name|ctr
init|=
literal|0
decl_stmt|;
name|Container
name|container
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Container
argument_list|>
name|i
init|=
name|applicationContainers
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
name|container
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|container
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|containerID
argument_list|)
operator|==
literal|0
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
operator|++
name|ctr
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ctr
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Container "
operator|+
name|containerID
operator|+
literal|" stopped "
operator|+
name|ctr
operator|+
literal|" times!"
argument_list|)
throw|;
block|}
name|Resources
operator|.
name|addTo
argument_list|(
name|available
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|used
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DEBUG --- stopContainer:"
operator|+
literal|" node="
operator|+
name|containerManagerAddress
operator|+
literal|" application="
operator|+
name|applicationId
operator|+
literal|" container="
operator|+
name|containerID
operator|+
literal|" available="
operator|+
name|available
operator|+
literal|" used="
operator|+
name|used
argument_list|)
expr_stmt|;
name|StopContainerResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|StopContainerResponse
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
DECL|method|getContainerStatus (GetContainerStatusRequest request)
specifier|synchronized
specifier|public
name|GetContainerStatusResponse
name|getContainerStatus
parameter_list|(
name|GetContainerStatusRequest
name|request
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|ContainerId
name|containerId
init|=
name|request
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Container
argument_list|>
name|appContainers
init|=
name|containers
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
decl_stmt|;
name|Container
name|container
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Container
name|c
range|:
name|appContainers
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|containerId
argument_list|)
condition|)
block|{
name|container
operator|=
name|c
expr_stmt|;
block|}
block|}
name|GetContainerStatusResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetContainerStatusResponse
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
operator|&&
name|container
operator|.
name|getContainerStatus
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|container
operator|.
name|getContainerStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
specifier|public
specifier|static
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
DECL|method|createNodeStatus (NodeId nodeId, List<ContainerStatus> containers)
name|createNodeStatus
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|List
argument_list|<
name|ContainerStatus
argument_list|>
name|containers
parameter_list|)
block|{
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
name|nodeStatus
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
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
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeStatus
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|nodeStatus
operator|.
name|setContainersStatuses
argument_list|(
name|containers
argument_list|)
expr_stmt|;
name|NodeHealthStatus
name|nodeHealthStatus
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHealthStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeHealthStatus
operator|.
name|setIsNodeHealthy
argument_list|(
literal|true
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
block|}
end_class

end_unit

