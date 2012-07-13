begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair
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
name|fair
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
name|QueueMetrics
import|;
end_import

begin_import
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
name|SchedulerApp
import|;
end_import

begin_import
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
name|util
operator|.
name|BuilderUtils
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AppSchedulable
specifier|public
class|class
name|AppSchedulable
extends|extends
name|Schedulable
block|{
DECL|field|scheduler
specifier|private
name|FairScheduler
name|scheduler
decl_stmt|;
DECL|field|app
specifier|private
name|FSSchedulerApp
name|app
decl_stmt|;
DECL|field|demand
specifier|private
name|Resource
name|demand
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|runnable
specifier|private
name|boolean
name|runnable
init|=
literal|false
decl_stmt|;
comment|// everyone starts as not runnable
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
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
name|AppSchedulable
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|queue
specifier|private
name|FSQueue
name|queue
decl_stmt|;
DECL|field|containerTokenSecretManager
specifier|private
name|ContainerTokenSecretManager
name|containerTokenSecretManager
decl_stmt|;
DECL|method|AppSchedulable (FairScheduler scheduler, FSSchedulerApp app, FSQueue queue)
specifier|public
name|AppSchedulable
parameter_list|(
name|FairScheduler
name|scheduler
parameter_list|,
name|FSSchedulerApp
name|app
parameter_list|,
name|FSQueue
name|queue
parameter_list|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|app
operator|=
name|app
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|containerTokenSecretManager
operator|=
name|scheduler
operator|.
name|getContainerTokenSecretManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|app
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getApp ()
specifier|public
name|SchedulerApp
name|getApp
parameter_list|()
block|{
return|return
name|app
return|;
block|}
annotation|@
name|Override
DECL|method|updateDemand ()
specifier|public
name|void
name|updateDemand
parameter_list|()
block|{
name|demand
operator|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// Demand is current consumption plus outstanding requests
name|Resources
operator|.
name|addTo
argument_list|(
name|demand
argument_list|,
name|app
operator|.
name|getCurrentConsumption
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add up outstanding resource requests
for|for
control|(
name|Priority
name|p
range|:
name|app
operator|.
name|getPriorities
argument_list|()
control|)
block|{
for|for
control|(
name|ResourceRequest
name|r
range|:
name|app
operator|.
name|getResourceRequests
argument_list|(
name|p
argument_list|)
operator|.
name|values
argument_list|()
control|)
block|{
name|Resource
name|total
init|=
name|Resources
operator|.
name|multiply
argument_list|(
name|r
operator|.
name|getCapability
argument_list|()
argument_list|,
name|r
operator|.
name|getNumContainers
argument_list|()
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|demand
argument_list|,
name|total
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getDemand ()
specifier|public
name|Resource
name|getDemand
parameter_list|()
block|{
return|return
name|demand
return|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
annotation|@
name|Override
DECL|method|redistributeShare ()
specifier|public
name|void
name|redistributeShare
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getResourceUsage ()
specifier|public
name|Resource
name|getResourceUsage
parameter_list|()
block|{
return|return
name|this
operator|.
name|app
operator|.
name|getCurrentConsumption
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMinShare ()
specifier|public
name|Resource
name|getMinShare
parameter_list|()
block|{
return|return
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Get metrics reference from containing queue.    */
DECL|method|getMetrics ()
specifier|public
name|QueueMetrics
name|getMetrics
parameter_list|()
block|{
return|return
name|this
operator|.
name|queue
operator|.
name|getQueueSchedulable
argument_list|()
operator|.
name|getMetrics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getWeight ()
specifier|public
name|double
name|getWeight
parameter_list|()
block|{
return|return
name|scheduler
operator|.
name|getAppWeight
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
comment|// Right now per-app priorities are not passed to scheduler,
comment|// so everyone has the same priority.
name|Priority
name|p
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Priority
operator|.
name|class
argument_list|)
decl_stmt|;
name|p
operator|.
name|setPriority
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
comment|/**    * Is this application runnable? Runnable means that the user and queue    * application counts are within configured quotas.    */
DECL|method|getRunnable ()
specifier|public
name|boolean
name|getRunnable
parameter_list|()
block|{
return|return
name|runnable
return|;
block|}
DECL|method|setRunnable (boolean runnable)
specifier|public
name|void
name|setRunnable
parameter_list|(
name|boolean
name|runnable
parameter_list|)
block|{
name|this
operator|.
name|runnable
operator|=
name|runnable
expr_stmt|;
block|}
comment|/**    * Create and return a container object reflecting an allocation for the    * given appliction on the given node with the given capability and    * priority.    */
DECL|method|createContainer (SchedulerApp application, SchedulerNode node, Resource capability, Priority priority)
specifier|public
name|Container
name|createContainer
parameter_list|(
name|SchedulerApp
name|application
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|Priority
name|priority
parameter_list|)
block|{
name|NodeId
name|nodeId
init|=
name|node
operator|.
name|getRMNode
argument_list|()
operator|.
name|getNodeID
argument_list|()
decl_stmt|;
name|ContainerId
name|containerId
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|application
operator|.
name|getNewContainerId
argument_list|()
argument_list|)
decl_stmt|;
name|ContainerToken
name|containerToken
init|=
literal|null
decl_stmt|;
comment|// If security is enabled, send the container-tokens too.
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|containerToken
operator|=
name|containerTokenSecretManager
operator|.
name|createContainerToken
argument_list|(
name|containerId
argument_list|,
name|nodeId
argument_list|,
name|capability
argument_list|)
expr_stmt|;
if|if
condition|(
name|containerToken
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
comment|// Try again later.
block|}
block|}
comment|// Create the container
name|Container
name|container
init|=
name|BuilderUtils
operator|.
name|newContainer
argument_list|(
name|containerId
argument_list|,
name|nodeId
argument_list|,
name|node
operator|.
name|getRMNode
argument_list|()
operator|.
name|getHttpAddress
argument_list|()
argument_list|,
name|capability
argument_list|,
name|priority
argument_list|,
name|containerToken
argument_list|)
decl_stmt|;
return|return
name|container
return|;
block|}
comment|/**    * Reserve a spot for {@code container} on this {@code node}. If    * the container is {@code alreadyReserved} on the node, simply    * update relevant bookeeping. This dispatches ro relevant handlers    * in the {@link SchedulerNode} and {@link SchedulerApp} classes.    */
DECL|method|reserve (SchedulerApp application, Priority priority, SchedulerNode node, Container container, boolean alreadyReserved)
specifier|private
name|void
name|reserve
parameter_list|(
name|SchedulerApp
name|application
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|SchedulerNode
name|node
parameter_list|,
name|Container
name|container
parameter_list|,
name|boolean
name|alreadyReserved
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Making reservation: node="
operator|+
name|node
operator|.
name|getHostName
argument_list|()
operator|+
literal|" app_id="
operator|+
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|alreadyReserved
condition|)
block|{
name|getMetrics
argument_list|()
operator|.
name|reserveResource
argument_list|(
name|application
operator|.
name|getUser
argument_list|()
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|RMContainer
name|rmContainer
init|=
name|application
operator|.
name|reserve
argument_list|(
name|node
argument_list|,
name|priority
argument_list|,
literal|null
argument_list|,
name|container
argument_list|)
decl_stmt|;
name|node
operator|.
name|reserveResource
argument_list|(
name|application
argument_list|,
name|priority
argument_list|,
name|rmContainer
argument_list|)
expr_stmt|;
name|getMetrics
argument_list|()
operator|.
name|reserveResource
argument_list|(
name|this
operator|.
name|app
operator|.
name|getUser
argument_list|()
argument_list|,
name|container
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|getRootQueueMetrics
argument_list|()
operator|.
name|reserveResource
argument_list|(
name|this
operator|.
name|app
operator|.
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
else|else
block|{
name|RMContainer
name|rmContainer
init|=
name|node
operator|.
name|getReservedContainer
argument_list|()
decl_stmt|;
name|application
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
name|node
operator|.
name|reserveResource
argument_list|(
name|application
argument_list|,
name|priority
argument_list|,
name|rmContainer
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove the reservation on {@code node} for {@ application} at the given    * {@link Priority}. This dispatches to the SchedulerApp and SchedulerNode    * handlers for an unreservation.    */
DECL|method|unreserve (SchedulerApp application, Priority priority, SchedulerNode node)
specifier|private
name|void
name|unreserve
parameter_list|(
name|SchedulerApp
name|application
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|SchedulerNode
name|node
parameter_list|)
block|{
name|RMContainer
name|rmContainer
init|=
name|node
operator|.
name|getReservedContainer
argument_list|()
decl_stmt|;
name|application
operator|.
name|unreserve
argument_list|(
name|node
argument_list|,
name|priority
argument_list|)
expr_stmt|;
name|node
operator|.
name|unreserveResource
argument_list|(
name|application
argument_list|)
expr_stmt|;
name|getMetrics
argument_list|()
operator|.
name|unreserveResource
argument_list|(
name|application
operator|.
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
name|scheduler
operator|.
name|getRootQueueMetrics
argument_list|()
operator|.
name|unreserveResource
argument_list|(
name|application
operator|.
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
block|}
comment|/**    * Assign a container to this node to facilitate {@code request}. If node does    * not have enough memory, create a reservation. This is called once we are    * sure the particular request should be facilitated by this node.    */
DECL|method|assignContainer (SchedulerNode node, SchedulerApp application, Priority priority, ResourceRequest request, NodeType type, boolean reserved)
specifier|private
name|Resource
name|assignContainer
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|SchedulerApp
name|application
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|ResourceRequest
name|request
parameter_list|,
name|NodeType
name|type
parameter_list|,
name|boolean
name|reserved
parameter_list|)
block|{
comment|// How much does this request need?
name|Resource
name|capability
init|=
name|request
operator|.
name|getCapability
argument_list|()
decl_stmt|;
comment|// How much does the node have?
name|Resource
name|available
init|=
name|node
operator|.
name|getAvailableResource
argument_list|()
decl_stmt|;
name|Container
name|container
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|reserved
condition|)
block|{
name|container
operator|=
name|node
operator|.
name|getReservedContainer
argument_list|()
operator|.
name|getContainer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|container
operator|=
name|createContainer
argument_list|(
name|application
argument_list|,
name|node
argument_list|,
name|capability
argument_list|,
name|priority
argument_list|)
expr_stmt|;
block|}
comment|// Can we allocate a container on this node?
name|int
name|availableContainers
init|=
name|available
operator|.
name|getMemory
argument_list|()
operator|/
name|capability
operator|.
name|getMemory
argument_list|()
decl_stmt|;
if|if
condition|(
name|availableContainers
operator|>
literal|0
condition|)
block|{
comment|// Inform the application of the new container for this request
name|RMContainer
name|allocatedContainer
init|=
name|application
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
if|if
condition|(
name|allocatedContainer
operator|==
literal|null
condition|)
block|{
comment|// Did the application need this resource?
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
else|else
block|{
comment|// TODO this should subtract resource just assigned
comment|// TEMPROARY
name|getMetrics
argument_list|()
operator|.
name|setAvailableResourcesToQueue
argument_list|(
name|this
operator|.
name|scheduler
operator|.
name|getClusterCapacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// If we had previously made a reservation, delete it
if|if
condition|(
name|reserved
condition|)
block|{
name|this
operator|.
name|unreserve
argument_list|(
name|application
argument_list|,
name|priority
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
comment|// Inform the node
name|node
operator|.
name|allocateContainer
argument_list|(
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|allocatedContainer
argument_list|)
expr_stmt|;
return|return
name|container
operator|.
name|getResource
argument_list|()
return|;
block|}
else|else
block|{
comment|// The desired container won't fit here, so reserve
name|reserve
argument_list|(
name|application
argument_list|,
name|priority
argument_list|,
name|node
argument_list|,
name|container
argument_list|,
name|reserved
argument_list|)
expr_stmt|;
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|assignContainer (SchedulerNode node, boolean reserved)
specifier|public
name|Resource
name|assignContainer
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|boolean
name|reserved
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Node offered to app: "
operator|+
name|getName
argument_list|()
operator|+
literal|" reserved: "
operator|+
name|reserved
argument_list|)
expr_stmt|;
if|if
condition|(
name|reserved
condition|)
block|{
name|RMContainer
name|rmContainer
init|=
name|node
operator|.
name|getReservedContainer
argument_list|()
decl_stmt|;
name|Priority
name|priority
init|=
name|rmContainer
operator|.
name|getReservedPriority
argument_list|()
decl_stmt|;
comment|// Make sure the application still needs requests at this priority
if|if
condition|(
name|app
operator|.
name|getTotalRequiredResources
argument_list|(
name|priority
argument_list|)
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|unreserve
argument_list|(
name|app
argument_list|,
name|priority
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
block|}
else|else
block|{
comment|// If this app is over quota, don't schedule anything
if|if
condition|(
operator|!
operator|(
name|getRunnable
argument_list|()
operator|)
condition|)
block|{
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
block|}
comment|// For each priority, see if we can schedule a node local, rack local
comment|// or off-switch request. Rack of off-switch requests may be delayed
comment|// (not scheduled) in order to promote better locality.
for|for
control|(
name|Priority
name|priority
range|:
name|app
operator|.
name|getPriorities
argument_list|()
control|)
block|{
name|app
operator|.
name|addSchedulingOpportunity
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|NodeType
name|allowedLocality
init|=
name|app
operator|.
name|getAllowedLocalityLevel
argument_list|(
name|priority
argument_list|,
name|scheduler
operator|.
name|getNumClusterNodes
argument_list|()
argument_list|,
name|scheduler
operator|.
name|getNodeLocalityThreshold
argument_list|()
argument_list|,
name|scheduler
operator|.
name|getRackLocalityThreshold
argument_list|()
argument_list|)
decl_stmt|;
name|ResourceRequest
name|localRequest
init|=
name|app
operator|.
name|getResourceRequest
argument_list|(
name|priority
argument_list|,
name|node
operator|.
name|getHostName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|localRequest
operator|!=
literal|null
operator|&&
name|localRequest
operator|.
name|getNumContainers
argument_list|()
operator|!=
literal|0
condition|)
block|{
return|return
name|assignContainer
argument_list|(
name|node
argument_list|,
name|app
argument_list|,
name|priority
argument_list|,
name|localRequest
argument_list|,
name|NodeType
operator|.
name|NODE_LOCAL
argument_list|,
name|reserved
argument_list|)
return|;
block|}
name|ResourceRequest
name|rackLocalRequest
init|=
name|app
operator|.
name|getResourceRequest
argument_list|(
name|priority
argument_list|,
name|node
operator|.
name|getRackName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rackLocalRequest
operator|!=
literal|null
operator|&&
name|rackLocalRequest
operator|.
name|getNumContainers
argument_list|()
operator|!=
literal|0
operator|&&
operator|(
name|allowedLocality
operator|.
name|equals
argument_list|(
name|NodeType
operator|.
name|RACK_LOCAL
argument_list|)
operator|||
name|allowedLocality
operator|.
name|equals
argument_list|(
name|NodeType
operator|.
name|OFF_SWITCH
argument_list|)
operator|)
condition|)
block|{
return|return
name|assignContainer
argument_list|(
name|node
argument_list|,
name|app
argument_list|,
name|priority
argument_list|,
name|rackLocalRequest
argument_list|,
name|NodeType
operator|.
name|RACK_LOCAL
argument_list|,
name|reserved
argument_list|)
return|;
block|}
name|ResourceRequest
name|offSwitchRequest
init|=
name|app
operator|.
name|getResourceRequest
argument_list|(
name|priority
argument_list|,
name|RMNode
operator|.
name|ANY
argument_list|)
decl_stmt|;
if|if
condition|(
name|offSwitchRequest
operator|!=
literal|null
operator|&&
name|offSwitchRequest
operator|.
name|getNumContainers
argument_list|()
operator|!=
literal|0
operator|&&
name|allowedLocality
operator|.
name|equals
argument_list|(
name|NodeType
operator|.
name|OFF_SWITCH
argument_list|)
condition|)
block|{
return|return
name|assignContainer
argument_list|(
name|node
argument_list|,
name|app
argument_list|,
name|priority
argument_list|,
name|offSwitchRequest
argument_list|,
name|NodeType
operator|.
name|OFF_SWITCH
argument_list|,
name|reserved
argument_list|)
return|;
block|}
block|}
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
block|}
end_class

end_unit

