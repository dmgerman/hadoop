begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
package|;
end_package

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
name|HashMap
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Stable
import|;
end_import

begin_import
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
name|recovery
operator|.
name|ApplicationsStore
operator|.
name|ApplicationStore
import|;
end_import

begin_import
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptState
import|;
end_import

begin_import
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
name|rmcontainer
operator|.
name|RMContainerReservedEvent
import|;
end_import

begin_import
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

begin_class
DECL|class|SchedulerApp
specifier|public
class|class
name|SchedulerApp
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
name|SchedulerApp
operator|.
name|class
argument_list|)
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
DECL|field|appSchedulingInfo
specifier|private
specifier|final
name|AppSchedulingInfo
name|appSchedulingInfo
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|Queue
name|queue
decl_stmt|;
DECL|field|currentConsumption
specifier|private
specifier|final
name|Resource
name|currentConsumption
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
DECL|field|resourceLimit
specifier|private
name|Resource
name|resourceLimit
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
DECL|field|liveContainers
specifier|private
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|RMContainer
argument_list|>
name|liveContainers
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|RMContainer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|newlyAllocatedContainers
specifier|private
name|List
argument_list|<
name|RMContainer
argument_list|>
name|newlyAllocatedContainers
init|=
operator|new
name|ArrayList
argument_list|<
name|RMContainer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reservedContainers
specifier|final
name|Map
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|NodeId
argument_list|,
name|RMContainer
argument_list|>
argument_list|>
name|reservedContainers
init|=
operator|new
name|HashMap
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|NodeId
argument_list|,
name|RMContainer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|schedulingOpportunities
name|Map
argument_list|<
name|Priority
argument_list|,
name|Integer
argument_list|>
name|schedulingOpportunities
init|=
operator|new
name|HashMap
argument_list|<
name|Priority
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reReservations
name|Map
argument_list|<
name|Priority
argument_list|,
name|Integer
argument_list|>
name|reReservations
init|=
operator|new
name|HashMap
argument_list|<
name|Priority
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|currentReservation
name|Resource
name|currentReservation
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
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|SchedulerApp (ApplicationAttemptId applicationAttemptId, String user, Queue queue, RMContext rmContext, ApplicationStore store)
specifier|public
name|SchedulerApp
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
name|RMContext
name|rmContext
parameter_list|,
name|ApplicationStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|appSchedulingInfo
operator|=
operator|new
name|AppSchedulingInfo
argument_list|(
name|applicationAttemptId
argument_list|,
name|user
argument_list|,
name|queue
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getApplicationId
argument_list|()
return|;
block|}
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getApplicationAttemptId
argument_list|()
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getUser
argument_list|()
return|;
block|}
DECL|method|updateResourceRequests ( List<ResourceRequest> requests)
specifier|public
specifier|synchronized
name|void
name|updateResourceRequests
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|requests
parameter_list|)
block|{
name|this
operator|.
name|appSchedulingInfo
operator|.
name|updateResourceRequests
argument_list|(
name|requests
argument_list|)
expr_stmt|;
block|}
DECL|method|getResourceRequests (Priority priority)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|getResourceRequests
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getResourceRequests
argument_list|(
name|priority
argument_list|)
return|;
block|}
DECL|method|getNewContainerId ()
specifier|public
name|int
name|getNewContainerId
parameter_list|()
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getNewContainerId
argument_list|()
return|;
block|}
DECL|method|getPriorities ()
specifier|public
name|Collection
argument_list|<
name|Priority
argument_list|>
name|getPriorities
parameter_list|()
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getPriorities
argument_list|()
return|;
block|}
DECL|method|getResourceRequest (Priority priority, String nodeAddress)
specifier|public
name|ResourceRequest
name|getResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|nodeAddress
parameter_list|)
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getResourceRequest
argument_list|(
name|priority
argument_list|,
name|nodeAddress
argument_list|)
return|;
block|}
DECL|method|getTotalRequiredResources (Priority priority)
specifier|public
specifier|synchronized
name|int
name|getTotalRequiredResources
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
return|return
name|getResourceRequest
argument_list|(
name|priority
argument_list|,
name|RMNode
operator|.
name|ANY
argument_list|)
operator|.
name|getNumContainers
argument_list|()
return|;
block|}
DECL|method|getResource (Priority priority)
specifier|public
name|Resource
name|getResource
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getResource
argument_list|(
name|priority
argument_list|)
return|;
block|}
DECL|method|isPending ()
specifier|public
name|boolean
name|isPending
parameter_list|()
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|isPending
argument_list|()
return|;
block|}
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|this
operator|.
name|appSchedulingInfo
operator|.
name|getQueueName
argument_list|()
return|;
block|}
DECL|method|getLiveContainers ()
specifier|public
specifier|synchronized
name|Collection
argument_list|<
name|RMContainer
argument_list|>
name|getLiveContainers
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|RMContainer
argument_list|>
argument_list|(
name|liveContainers
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
DECL|method|stop (RMAppAttemptState rmAppAttemptFinalState)
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|(
name|RMAppAttemptState
name|rmAppAttemptFinalState
parameter_list|)
block|{
comment|// Cleanup all scheduling information
name|this
operator|.
name|appSchedulingInfo
operator|.
name|stop
argument_list|(
name|rmAppAttemptFinalState
argument_list|)
expr_stmt|;
block|}
DECL|method|containerLaunchedOnNode (ContainerId containerId)
specifier|synchronized
specifier|public
name|void
name|containerLaunchedOnNode
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
comment|// Inform the container
name|RMContainer
name|rmContainer
init|=
name|getRMContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|containerId
argument_list|,
name|RMContainerEventType
operator|.
name|LAUNCHED
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|containerCompleted (RMContainer rmContainer, ContainerStatus containerStatus, RMContainerEventType event)
specifier|synchronized
specifier|public
name|void
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
parameter_list|)
block|{
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Completed container: "
operator|+
name|rmContainer
operator|.
name|getContainerId
argument_list|()
operator|+
literal|" in state: "
operator|+
name|rmContainer
operator|.
name|getState
argument_list|()
operator|+
literal|" event:"
operator|+
name|event
argument_list|)
expr_stmt|;
comment|// Remove from the list of containers
name|liveContainers
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
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|currentConsumption
argument_list|,
name|containerResource
argument_list|)
expr_stmt|;
block|}
DECL|method|allocate (NodeType type, SchedulerNode node, Priority priority, ResourceRequest request, Container container)
specifier|synchronized
specifier|public
name|RMContainer
name|allocate
parameter_list|(
name|NodeType
name|type
parameter_list|,
name|SchedulerNode
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
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
argument_list|,
name|this
operator|.
name|rmContext
operator|.
name|getContainerAllocationExpirer
argument_list|()
argument_list|)
decl_stmt|;
comment|// Update consumption and track allocations
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
name|Resources
operator|.
name|addTo
argument_list|(
name|currentConsumption
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
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
expr_stmt|;
return|return
name|rmContainer
return|;
block|}
DECL|method|pullNewlyAllocatedContainers ()
specifier|synchronized
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|pullNewlyAllocatedContainers
parameter_list|()
block|{
name|List
argument_list|<
name|Container
argument_list|>
name|returnContainerList
init|=
operator|new
name|ArrayList
argument_list|<
name|Container
argument_list|>
argument_list|(
name|newlyAllocatedContainers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RMContainer
name|rmContainer
range|:
name|newlyAllocatedContainers
control|)
block|{
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerEvent
argument_list|(
name|rmContainer
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|RMContainerEventType
operator|.
name|ACQUIRED
argument_list|)
argument_list|)
expr_stmt|;
name|returnContainerList
operator|.
name|add
argument_list|(
name|rmContainer
operator|.
name|getContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newlyAllocatedContainers
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|returnContainerList
return|;
block|}
DECL|method|getCurrentConsumption ()
specifier|public
name|Resource
name|getCurrentConsumption
parameter_list|()
block|{
return|return
name|this
operator|.
name|currentConsumption
return|;
block|}
DECL|method|showRequests ()
specifier|synchronized
specifier|public
name|void
name|showRequests
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
for|for
control|(
name|Priority
name|priority
range|:
name|getPriorities
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|requests
init|=
name|getResourceRequests
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|requests
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"showRequests:"
operator|+
literal|" application="
operator|+
name|getApplicationId
argument_list|()
operator|+
literal|" headRoom="
operator|+
name|getHeadroom
argument_list|()
operator|+
literal|" currentConsumption="
operator|+
name|currentConsumption
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ResourceRequest
name|request
range|:
name|requests
operator|.
name|values
argument_list|()
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"showRequests:"
operator|+
literal|" application="
operator|+
name|getApplicationId
argument_list|()
operator|+
literal|" request="
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|setAvailableResourceLimit (Resource globalLimit)
specifier|public
specifier|synchronized
name|void
name|setAvailableResourceLimit
parameter_list|(
name|Resource
name|globalLimit
parameter_list|)
block|{
name|this
operator|.
name|resourceLimit
operator|=
name|globalLimit
expr_stmt|;
block|}
DECL|method|getRMContainer (ContainerId id)
specifier|public
specifier|synchronized
name|RMContainer
name|getRMContainer
parameter_list|(
name|ContainerId
name|id
parameter_list|)
block|{
return|return
name|liveContainers
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|resetSchedulingOpportunities (Priority priority)
specifier|synchronized
specifier|public
name|void
name|resetSchedulingOpportunities
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|this
operator|.
name|schedulingOpportunities
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addSchedulingOpportunity (Priority priority)
specifier|synchronized
specifier|public
name|void
name|addSchedulingOpportunity
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|Integer
name|schedulingOpportunities
init|=
name|this
operator|.
name|schedulingOpportunities
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|schedulingOpportunities
operator|==
literal|null
condition|)
block|{
name|schedulingOpportunities
operator|=
literal|0
expr_stmt|;
block|}
operator|++
name|schedulingOpportunities
expr_stmt|;
name|this
operator|.
name|schedulingOpportunities
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|schedulingOpportunities
argument_list|)
expr_stmt|;
block|}
DECL|method|getSchedulingOpportunities (Priority priority)
specifier|synchronized
specifier|public
name|int
name|getSchedulingOpportunities
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|Integer
name|schedulingOpportunities
init|=
name|this
operator|.
name|schedulingOpportunities
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|schedulingOpportunities
operator|==
literal|null
condition|)
block|{
name|schedulingOpportunities
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|schedulingOpportunities
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|schedulingOpportunities
argument_list|)
expr_stmt|;
block|}
return|return
name|schedulingOpportunities
return|;
block|}
DECL|method|resetReReservations (Priority priority)
specifier|synchronized
name|void
name|resetReReservations
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|this
operator|.
name|reReservations
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addReReservation (Priority priority)
specifier|synchronized
name|void
name|addReReservation
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|Integer
name|reReservations
init|=
name|this
operator|.
name|reReservations
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|reReservations
operator|==
literal|null
condition|)
block|{
name|reReservations
operator|=
literal|0
expr_stmt|;
block|}
operator|++
name|reReservations
expr_stmt|;
name|this
operator|.
name|reReservations
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|reReservations
argument_list|)
expr_stmt|;
block|}
DECL|method|getReReservations (Priority priority)
specifier|synchronized
specifier|public
name|int
name|getReReservations
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|Integer
name|reReservations
init|=
name|this
operator|.
name|reReservations
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|reReservations
operator|==
literal|null
condition|)
block|{
name|reReservations
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|reReservations
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|reReservations
argument_list|)
expr_stmt|;
block|}
return|return
name|reReservations
return|;
block|}
DECL|method|getNumReservedContainers (Priority priority)
specifier|public
specifier|synchronized
name|int
name|getNumReservedContainers
parameter_list|(
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
return|return
operator|(
name|reservedContainers
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|reservedContainers
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Get total current reservations.    * Used only by unit tests    * @return total current reservations    */
annotation|@
name|Stable
annotation|@
name|Private
DECL|method|getCurrentReservation ()
specifier|public
specifier|synchronized
name|Resource
name|getCurrentReservation
parameter_list|()
block|{
return|return
name|currentReservation
return|;
block|}
DECL|method|reserve (SchedulerNode node, Priority priority, RMContainer rmContainer, Container container)
specifier|public
specifier|synchronized
name|RMContainer
name|reserve
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
comment|// Create RMContainer if necessary
if|if
condition|(
name|rmContainer
operator|==
literal|null
condition|)
block|{
name|rmContainer
operator|=
operator|new
name|RMContainerImpl
argument_list|(
name|container
argument_list|,
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|rmContext
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
argument_list|,
name|rmContext
operator|.
name|getContainerAllocationExpirer
argument_list|()
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|currentReservation
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
comment|// Reset the re-reservation count
name|resetReReservations
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Note down the re-reservation
name|addReReservation
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
name|rmContainer
operator|.
name|handle
argument_list|(
operator|new
name|RMContainerReservedEvent
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
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|priority
argument_list|)
argument_list|)
expr_stmt|;
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
operator|==
literal|null
condition|)
block|{
name|reservedContainers
operator|=
operator|new
name|HashMap
argument_list|<
name|NodeId
argument_list|,
name|RMContainer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|reservedContainers
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|reservedContainers
argument_list|)
expr_stmt|;
block|}
name|reservedContainers
operator|.
name|put
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|,
name|rmContainer
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
literal|" reserved container "
operator|+
name|rmContainer
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
name|currentReservation
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rmContainer
return|;
block|}
DECL|method|unreserve (SchedulerNode node, Priority priority)
specifier|public
specifier|synchronized
name|void
name|unreserve
parameter_list|(
name|SchedulerNode
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
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|currentReservation
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
name|currentReservation
argument_list|)
expr_stmt|;
block|}
comment|/**    * Has the application reserved the given<code>node</code> at the    * given<code>priority</code>?    * @param node node to be checked    * @param priority priority of reserved container    * @return true is reserved, false if not    */
DECL|method|isReserved (SchedulerNode node, Priority priority)
specifier|public
specifier|synchronized
name|boolean
name|isReserved
parameter_list|(
name|SchedulerNode
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
return|return
name|reservedContainers
operator|.
name|containsKey
argument_list|(
name|node
operator|.
name|getNodeID
argument_list|()
argument_list|)
return|;
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
DECL|method|getAllReservedContainers ()
specifier|public
specifier|synchronized
name|List
argument_list|<
name|RMContainer
argument_list|>
name|getAllReservedContainers
parameter_list|()
block|{
name|List
argument_list|<
name|RMContainer
argument_list|>
name|reservedContainers
init|=
operator|new
name|ArrayList
argument_list|<
name|RMContainer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|NodeId
argument_list|,
name|RMContainer
argument_list|>
argument_list|>
name|e
range|:
name|this
operator|.
name|reservedContainers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|reservedContainers
operator|.
name|addAll
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|reservedContainers
return|;
block|}
comment|/**    * Get available headroom in terms of resources for the application's user.    * @return available resource headroom    */
DECL|method|getHeadroom ()
specifier|public
specifier|synchronized
name|Resource
name|getHeadroom
parameter_list|()
block|{
name|Resource
name|limit
init|=
name|Resources
operator|.
name|subtract
argument_list|(
name|resourceLimit
argument_list|,
name|currentConsumption
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|limit
argument_list|,
name|currentReservation
argument_list|)
expr_stmt|;
comment|// Corner case to deal with applications being slightly over-limit
if|if
condition|(
name|limit
operator|.
name|getMemory
argument_list|()
operator|<
literal|0
condition|)
block|{
name|limit
operator|.
name|setMemory
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|limit
return|;
block|}
DECL|method|getQueue ()
specifier|public
name|Queue
name|getQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
block|}
end_class

end_unit

