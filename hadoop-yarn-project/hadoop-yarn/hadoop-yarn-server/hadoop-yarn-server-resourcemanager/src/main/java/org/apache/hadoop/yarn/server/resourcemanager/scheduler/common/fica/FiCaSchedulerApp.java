begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica
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
name|scheduler
operator|.
name|common
operator|.
name|fica
package|;
end_package

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
name|HashSet
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
name|api
operator|.
name|records
operator|.
name|ResourceRequest
import|;
end_import

begin_import
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
import|;
end_import

begin_import
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
name|rmcontainer
operator|.
name|RMContainer
import|;
end_import

begin_import
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
name|rmcontainer
operator|.
name|RMContainerEvent
import|;
end_import

begin_import
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
name|rmcontainer
operator|.
name|RMContainerEventType
import|;
end_import

begin_import
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
name|rmcontainer
operator|.
name|RMContainerFinishedEvent
import|;
end_import

begin_import
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
name|rmcontainer
operator|.
name|RMContainerImpl
import|;
end_import

begin_import
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
name|ActiveUsersManager
import|;
end_import

begin_import
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
name|Allocation
import|;
end_import

begin_import
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
name|NodeType
import|;
end_import

begin_import
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
name|Queue
import|;
end_import

begin_import
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
name|ResourceLimits
import|;
end_import

begin_import
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
name|ResourceScheduler
import|;
end_import

begin_import
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
name|SchedulerApplicationAttempt
import|;
end_import

begin_import
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
name|capacity
operator|.
name|CSAssignment
import|;
end_import

begin_import
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
name|capacity
operator|.
name|CapacityHeadroomProvider
import|;
end_import

begin_import
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
name|capacity
operator|.
name|CapacityScheduler
import|;
end_import

begin_import
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
name|capacity
operator|.
name|LeafQueue
import|;
end_import

begin_import
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
name|capacity
operator|.
name|SchedulingMode
import|;
end_import

begin_import
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
name|capacity
operator|.
name|allocator
operator|.
name|ContainerAllocator
import|;
end_import

begin_import
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
name|capacity
operator|.
name|allocator
operator|.
name|RegularContainerAllocator
import|;
end_import

begin_import
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
name|DefaultResourceCalculator
import|;
end_import

begin_import
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
name|ResourceCalculator
import|;
end_import

begin_import
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
comment|/**  * Represents an application attempt from the viewpoint of the FIFO or Capacity  * scheduler.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FiCaSchedulerApp
specifier|public
class|class
name|FiCaSchedulerApp
extends|extends
name|SchedulerApplicationAttempt
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
name|FiCaSchedulerApp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|containersToPreempt
specifier|private
specifier|final
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|containersToPreempt
init|=
operator|new
name|HashSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|headroomProvider
specifier|private
name|CapacityHeadroomProvider
name|headroomProvider
decl_stmt|;
DECL|field|rc
specifier|private
name|ResourceCalculator
name|rc
init|=
operator|new
name|DefaultResourceCalculator
argument_list|()
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ResourceScheduler
name|scheduler
decl_stmt|;
DECL|field|containerAllocator
specifier|private
name|ContainerAllocator
name|containerAllocator
decl_stmt|;
DECL|method|FiCaSchedulerApp (ApplicationAttemptId applicationAttemptId, String user, Queue queue, ActiveUsersManager activeUsersManager, RMContext rmContext)
specifier|public
name|FiCaSchedulerApp
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|String
name|user
parameter_list|,
name|Queue
name|queue
parameter_list|,
name|ActiveUsersManager
name|activeUsersManager
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
argument_list|(
name|applicationAttemptId
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
name|activeUsersManager
argument_list|,
name|rmContext
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|FiCaSchedulerApp (ApplicationAttemptId applicationAttemptId, String user, Queue queue, ActiveUsersManager activeUsersManager, RMContext rmContext, Priority appPriority)
specifier|public
name|FiCaSchedulerApp
parameter_list|(
name|ApplicationAttemptId
name|applicationAttemptId
parameter_list|,
name|String
name|user
parameter_list|,
name|Queue
name|queue
parameter_list|,
name|ActiveUsersManager
name|activeUsersManager
parameter_list|,
name|RMContext
name|rmContext
parameter_list|,
name|Priority
name|appPriority
parameter_list|)
block|{
name|super
argument_list|(
name|applicationAttemptId
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
name|activeUsersManager
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|RMApp
name|rmApp
init|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Resource
name|amResource
decl_stmt|;
if|if
condition|(
name|rmApp
operator|==
literal|null
operator|||
name|rmApp
operator|.
name|getAMResourceRequest
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|//the rmApp may be undefined (the resource manager checks for this too)
comment|//and unmanaged applications do not provide an amResource request
comment|//in these cases, provide a default using the scheduler
name|amResource
operator|=
name|rmContext
operator|.
name|getScheduler
argument_list|()
operator|.
name|getMinimumResourceCapability
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|amResource
operator|=
name|rmApp
operator|.
name|getAMResourceRequest
argument_list|()
operator|.
name|getCapability
argument_list|()
expr_stmt|;
block|}
name|setAMResource
argument_list|(
name|amResource
argument_list|)
expr_stmt|;
name|setPriority
argument_list|(
name|appPriority
argument_list|)
expr_stmt|;
name|scheduler
operator|=
name|rmContext
operator|.
name|getScheduler
argument_list|()
expr_stmt|;
if|if
condition|(
name|scheduler
operator|.
name|getResourceCalculator
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rc
operator|=
name|scheduler
operator|.
name|getResourceCalculator
argument_list|()
expr_stmt|;
block|}
name|containerAllocator
operator|=
operator|new
name|RegularContainerAllocator
argument_list|(
name|this
argument_list|,
name|rc
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
block|}
DECL|method|containerCompleted (RMContainer rmContainer, ContainerStatus containerStatus, RMContainerEventType event, String partition)
specifier|synchronized
specifier|public
name|boolean
name|containerCompleted
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|,
name|ContainerStatus
name|containerStatus
parameter_list|,
name|RMContainerEventType
name|event
parameter_list|,
name|String
name|partition
parameter_list|)
block|{
comment|// Remove from the list of containers
if|if
condition|(
literal|null
operator|==
name|liveContainers
operator|.
name|remove
argument_list|(
name|rmContainer
operator|.
name|getContainerId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Remove from the list of newly allocated containers if found
name|newlyAllocatedContainers
operator|.
name|remove
argument_list|(
name|rmContainer
argument_list|)
expr_stmt|;
name|Container
name|container
init|=
name|rmContainer
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|container
operator|.
name|getId
argument_list|()
decl_stmt|;
comment|// Inform the container
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerFinishedEvent
argument_list|(
name|containerId
argument_list|,
name|containerStatus
argument_list|,
name|event
argument_list|)
argument_list|)
expr_stmt|;
name|containersToPreempt
operator|.
name|remove
argument_list|(
name|rmContainer
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
name|RMAuditLogger
operator|.
name|logSuccess
argument_list|(
name|getUser
argument_list|()
argument_list|,
name|AuditConstants
operator|.
name|RELEASE_CONTAINER
argument_list|,
literal|"SchedulerApp"
argument_list|,
name|getApplicationId
argument_list|()
argument_list|,
name|containerId
argument_list|)
expr_stmt|;
comment|// Update usage metrics
name|Resource
name|containerResource
init|=
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|releaseResources
argument_list|(
name|getUser
argument_list|()
argument_list|,
literal|1
argument_list|,
name|containerResource
argument_list|)
expr_stmt|;
name|attemptResourceUsage
operator|.
name|decUsed
argument_list|(
name|partition
argument_list|,
name|containerResource
argument_list|)
expr_stmt|;
comment|// Clear resource utilization metrics cache.
name|lastMemoryAggregateAllocationUpdateTime
operator|=
operator|-
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|allocate (NodeType type, FiCaSchedulerNode node, Priority priority, ResourceRequest request, Container container)
specifier|synchronized
specifier|public
name|RMContainer
name|allocate
parameter_list|(
name|NodeType
name|type
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|ResourceRequest
name|request
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
if|if
condition|(
name|isStopped
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Required sanity check - AM can call 'allocate' to update resource
comment|// request without locking the scheduler, hence we need to check
if|if
condition|(
name|getTotalRequiredResources
argument_list|(
name|priority
argument_list|)
operator|<=
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Create RMContainer
name|RMContainer
name|rmContainer
init|=
operator|new
name|RMContainerImpl
argument_list|(
name|container
argument_list|,
name|this
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|appSchedulingInfo
operator|.
name|getUser
argument_list|()
argument_list|,
name|this
operator|.
name|rmContext
argument_list|,
name|request
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add it to allContainers list.
name|newlyAllocatedContainers
operator|.
name|add
argument_list|(
name|rmContainer
argument_list|)
expr_stmt|;
name|liveContainers
operator|.
name|put
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|rmContainer
argument_list|)
expr_stmt|;
comment|// Update consumption and track allocations
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|resourceRequestList
init|=
name|appSchedulingInfo
operator|.
name|allocate
argument_list|(
name|type
argument_list|,
name|node
argument_list|,
name|priority
argument_list|,
name|request
argument_list|,
name|container
argument_list|)
decl_stmt|;
name|attemptResourceUsage
operator|.
name|incUsed
argument_list|(
name|node
operator|.
name|getPartition
argument_list|()
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update resource requests related to "request" and store in RMContainer
operator|(
operator|(
name|RMContainerImpl
operator|)
name|rmContainer
operator|)
operator|.
name|setResourceRequests
argument_list|(
name|resourceRequestList
argument_list|)
expr_stmt|;
comment|// Inform the container
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|container
operator|.
name|getId
argument_list|()
argument_list|,
name|RMContainerEventType
operator|.
name|START
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"allocate: applicationAttemptId="
operator|+
name|container
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|+
literal|" container="
operator|+
name|container
operator|.
name|getId
argument_list|()
operator|+
literal|" host="
operator|+
name|container
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
operator|+
literal|" type="
operator|+
name|type
argument_list|)
expr_stmt|;
block|}
name|RMAuditLogger
operator|.
name|logSuccess
argument_list|(
name|getUser
argument_list|()
argument_list|,
name|AuditConstants
operator|.
name|ALLOC_CONTAINER
argument_list|,
literal|"SchedulerApp"
argument_list|,
name|getApplicationId
argument_list|()
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rmContainer
return|;
block|}
DECL|method|unreserve (Priority priority, FiCaSchedulerNode node, RMContainer rmContainer)
specifier|public
name|boolean
name|unreserve
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|)
block|{
comment|// Done with the reservation?
if|if
condition|(
name|unreserve
argument_list|(
name|node
argument_list|,
name|priority
argument_list|)
condition|)
block|{
name|node
operator|.
name|unreserveResource
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Update reserved metrics
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|unreserveResource
argument_list|(
name|getUser
argument_list|()
argument_list|,
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|unreserve (FiCaSchedulerNode node, Priority priority)
specifier|public
specifier|synchronized
name|boolean
name|unreserve
parameter_list|(
name|FiCaSchedulerNode
name|node
parameter_list|,
name|Priority
name|priority
parameter_list|)
block|{
name|Map
argument_list|<
name|NodeId
argument_list|,
name|RMContainer
argument_list|>
name|reservedContainers
init|=
name|this
operator|.
name|reservedContainers
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|reservedContainers
operator|!=
literal|null
condition|)
block|{
name|RMContainer
name|reservedContainer
init|=
name|reservedContainers
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
comment|// unreserve is now triggered in new scenarios (preemption)
comment|// as a consequence reservedcontainer might be null, adding NP-checks
if|if
condition|(
name|reservedContainer
operator|!=
literal|null
operator|&&
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|!=
literal|null
operator|&&
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|reservedContainers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|reservedContainers
operator|.
name|remove
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
comment|// Reset the re-reservation count
name|resetReReservations
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|this
operator|.
name|attemptResourceUsage
operator|.
name|decReserved
argument_list|(
name|node
operator|.
name|getPartition
argument_list|()
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Application "
operator|+
name|getApplicationId
argument_list|()
operator|+
literal|" unreserved "
operator|+
literal|" on node "
operator|+
name|node
operator|+
literal|", currently has "
operator|+
name|reservedContainers
operator|.
name|size
argument_list|()
operator|+
literal|" at priority "
operator|+
name|priority
operator|+
literal|"; currentReservation "
operator|+
name|this
operator|.
name|attemptResourceUsage
operator|.
name|getReserved
argument_list|()
operator|+
literal|" on node-label="
operator|+
name|node
operator|.
name|getPartition
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|getLocalityWaitFactor ( Priority priority, int clusterNodes)
specifier|public
specifier|synchronized
name|float
name|getLocalityWaitFactor
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|int
name|clusterNodes
parameter_list|)
block|{
comment|// Estimate: Required unique resources (i.e. hosts + racks)
name|int
name|requiredResources
init|=
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|getResourceRequests
argument_list|(
name|priority
argument_list|)
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// waitFactor can't be more than '1'
comment|// i.e. no point skipping more than clustersize opportunities
return|return
name|Math
operator|.
name|min
argument_list|(
operator|(
operator|(
name|float
operator|)
name|requiredResources
operator|/
name|clusterNodes
operator|)
argument_list|,
literal|1.0f
argument_list|)
return|;
block|}
DECL|method|getTotalPendingRequests ()
specifier|public
specifier|synchronized
name|Resource
name|getTotalPendingRequests
parameter_list|()
block|{
name|Resource
name|ret
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|ResourceRequest
name|rr
range|:
name|appSchedulingInfo
operator|.
name|getAllResourceRequests
argument_list|()
control|)
block|{
comment|// to avoid double counting we count only "ANY" resource requests
if|if
condition|(
name|ResourceRequest
operator|.
name|isAnyLocation
argument_list|(
name|rr
operator|.
name|getResourceName
argument_list|()
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|ret
argument_list|,
name|Resources
operator|.
name|multiply
argument_list|(
name|rr
operator|.
name|getCapability
argument_list|()
argument_list|,
name|rr
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|addPreemptContainer (ContainerId cont)
specifier|public
specifier|synchronized
name|void
name|addPreemptContainer
parameter_list|(
name|ContainerId
name|cont
parameter_list|)
block|{
comment|// ignore already completed containers
if|if
condition|(
name|liveContainers
operator|.
name|containsKey
argument_list|(
name|cont
argument_list|)
condition|)
block|{
name|containersToPreempt
operator|.
name|add
argument_list|(
name|cont
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This method produces an Allocation that includes the current view    * of the resources that will be allocated to and preempted from this    * application.    *    * @param rc    * @param clusterResource    * @param minimumAllocation    * @return an allocation    */
DECL|method|getAllocation (ResourceCalculator rc, Resource clusterResource, Resource minimumAllocation)
specifier|public
specifier|synchronized
name|Allocation
name|getAllocation
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|minimumAllocation
parameter_list|)
block|{
name|Set
argument_list|<
name|ContainerId
argument_list|>
name|currentContPreemption
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|ContainerId
argument_list|>
argument_list|(
name|containersToPreempt
argument_list|)
argument_list|)
decl_stmt|;
name|containersToPreempt
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Resource
name|tot
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|ContainerId
name|c
range|:
name|currentContPreemption
control|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|tot
argument_list|,
name|liveContainers
operator|.
name|get
argument_list|(
name|c
argument_list|)
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|numCont
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|Resources
operator|.
name|divide
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|tot
argument_list|,
name|minimumAllocation
argument_list|)
argument_list|)
decl_stmt|;
name|ResourceRequest
name|rr
init|=
name|ResourceRequest
operator|.
name|newInstance
argument_list|(
name|Priority
operator|.
name|UNDEFINED
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|,
name|minimumAllocation
argument_list|,
name|numCont
argument_list|)
decl_stmt|;
name|ContainersAndNMTokensAllocation
name|allocation
init|=
name|pullNewlyAllocatedContainersAndNMTokens
argument_list|()
decl_stmt|;
name|Resource
name|headroom
init|=
name|getHeadroom
argument_list|()
decl_stmt|;
name|setApplicationHeadroomForMetrics
argument_list|(
name|headroom
argument_list|)
expr_stmt|;
return|return
operator|new
name|Allocation
argument_list|(
name|allocation
operator|.
name|getContainerList
argument_list|()
argument_list|,
name|headroom
argument_list|,
literal|null
argument_list|,
name|currentContPreemption
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|rr
argument_list|)
argument_list|,
name|allocation
operator|.
name|getNMTokenList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getNodeIdToUnreserve (Priority priority, Resource resourceNeedUnreserve, ResourceCalculator rc, Resource clusterResource)
specifier|synchronized
specifier|public
name|NodeId
name|getNodeIdToUnreserve
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|Resource
name|resourceNeedUnreserve
parameter_list|,
name|ResourceCalculator
name|rc
parameter_list|,
name|Resource
name|clusterResource
parameter_list|)
block|{
comment|// first go around make this algorithm simple and just grab first
comment|// reservation that has enough resources
name|Map
argument_list|<
name|NodeId
argument_list|,
name|RMContainer
argument_list|>
name|reservedContainers
init|=
name|this
operator|.
name|reservedContainers
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|reservedContainers
operator|!=
literal|null
operator|)
operator|&&
operator|(
operator|!
name|reservedContainers
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|NodeId
argument_list|,
name|RMContainer
argument_list|>
name|entry
range|:
name|reservedContainers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NodeId
name|nodeId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Resource
name|containerResource
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getResource
argument_list|()
decl_stmt|;
comment|// make sure we unreserve one with at least the same amount of
comment|// resources, otherwise could affect capacity limits
if|if
condition|(
name|Resources
operator|.
name|lessThanOrEqual
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|resourceNeedUnreserve
argument_list|,
name|containerResource
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"unreserving node with reservation size: "
operator|+
name|containerResource
operator|+
literal|" in order to allocate container with size: "
operator|+
name|resourceNeedUnreserve
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeId
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|setHeadroomProvider ( CapacityHeadroomProvider headroomProvider)
specifier|public
specifier|synchronized
name|void
name|setHeadroomProvider
parameter_list|(
name|CapacityHeadroomProvider
name|headroomProvider
parameter_list|)
block|{
name|this
operator|.
name|headroomProvider
operator|=
name|headroomProvider
expr_stmt|;
block|}
DECL|method|getHeadroomProvider ()
specifier|public
specifier|synchronized
name|CapacityHeadroomProvider
name|getHeadroomProvider
parameter_list|()
block|{
return|return
name|headroomProvider
return|;
block|}
annotation|@
name|Override
DECL|method|getHeadroom ()
specifier|public
specifier|synchronized
name|Resource
name|getHeadroom
parameter_list|()
block|{
if|if
condition|(
name|headroomProvider
operator|!=
literal|null
condition|)
block|{
return|return
name|headroomProvider
operator|.
name|getHeadroom
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|getHeadroom
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|transferStateFromPreviousAttempt ( SchedulerApplicationAttempt appAttempt)
specifier|public
specifier|synchronized
name|void
name|transferStateFromPreviousAttempt
parameter_list|(
name|SchedulerApplicationAttempt
name|appAttempt
parameter_list|)
block|{
name|super
operator|.
name|transferStateFromPreviousAttempt
argument_list|(
name|appAttempt
argument_list|)
expr_stmt|;
name|this
operator|.
name|headroomProvider
operator|=
operator|(
operator|(
name|FiCaSchedulerApp
operator|)
name|appAttempt
operator|)
operator|.
name|getHeadroomProvider
argument_list|()
expr_stmt|;
block|}
DECL|method|reserve (Priority priority, FiCaSchedulerNode node, RMContainer rmContainer, Container container)
specifier|public
name|void
name|reserve
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
comment|// Update reserved metrics if this is the first reservation
if|if
condition|(
name|rmContainer
operator|==
literal|null
condition|)
block|{
name|queue
operator|.
name|getMetrics
argument_list|()
operator|.
name|reserveResource
argument_list|(
name|getUser
argument_list|()
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Inform the application
name|rmContainer
operator|=
name|super
operator|.
name|reserve
argument_list|(
name|node
argument_list|,
name|priority
argument_list|,
name|rmContainer
argument_list|,
name|container
argument_list|)
expr_stmt|;
comment|// Update the node
name|node
operator|.
name|reserveResource
argument_list|(
name|this
argument_list|,
name|priority
argument_list|,
name|rmContainer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|findNodeToUnreserve (Resource clusterResource, FiCaSchedulerNode node, Priority priority, Resource minimumUnreservedResource)
specifier|public
name|RMContainer
name|findNodeToUnreserve
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|Resource
name|minimumUnreservedResource
parameter_list|)
block|{
comment|// need to unreserve some other container first
name|NodeId
name|idToUnreserve
init|=
name|getNodeIdToUnreserve
argument_list|(
name|priority
argument_list|,
name|minimumUnreservedResource
argument_list|,
name|rc
argument_list|,
name|clusterResource
argument_list|)
decl_stmt|;
if|if
condition|(
name|idToUnreserve
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"checked to see if could unreserve for app but nothing "
operator|+
literal|"reserved that matches for this app"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|FiCaSchedulerNode
name|nodeToUnreserve
init|=
operator|(
operator|(
name|CapacityScheduler
operator|)
name|scheduler
operator|)
operator|.
name|getNode
argument_list|(
name|idToUnreserve
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeToUnreserve
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"node to unreserve doesn't exist, nodeid: "
operator|+
name|idToUnreserve
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"unreserving for app: "
operator|+
name|getApplicationId
argument_list|()
operator|+
literal|" on nodeId: "
operator|+
name|idToUnreserve
operator|+
literal|" in order to replace reserved application and place it on node: "
operator|+
name|node
operator|.
name|getNodeID
argument_list|()
operator|+
literal|" needing: "
operator|+
name|minimumUnreservedResource
argument_list|)
expr_stmt|;
block|}
comment|// headroom
name|Resources
operator|.
name|addTo
argument_list|(
name|getHeadroom
argument_list|()
argument_list|,
name|nodeToUnreserve
operator|.
name|getReservedContainer
argument_list|()
operator|.
name|getReservedResource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|nodeToUnreserve
operator|.
name|getReservedContainer
argument_list|()
return|;
block|}
DECL|method|getCSLeafQueue ()
specifier|public
name|LeafQueue
name|getCSLeafQueue
parameter_list|()
block|{
return|return
operator|(
name|LeafQueue
operator|)
name|queue
return|;
block|}
DECL|method|assignContainers (Resource clusterResource, FiCaSchedulerNode node, ResourceLimits currentResourceLimits, SchedulingMode schedulingMode, RMContainer reservedContainer)
specifier|public
name|CSAssignment
name|assignContainers
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|,
name|ResourceLimits
name|currentResourceLimits
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|,
name|RMContainer
name|reservedContainer
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"pre-assignContainers for application "
operator|+
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|showRequests
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
return|return
name|containerAllocator
operator|.
name|assignContainers
argument_list|(
name|clusterResource
argument_list|,
name|node
argument_list|,
name|schedulingMode
argument_list|,
name|currentResourceLimits
argument_list|,
name|reservedContainer
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

