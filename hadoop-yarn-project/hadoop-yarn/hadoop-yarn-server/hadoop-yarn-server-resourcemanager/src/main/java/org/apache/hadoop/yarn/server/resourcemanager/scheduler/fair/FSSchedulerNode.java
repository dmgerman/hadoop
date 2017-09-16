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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|scheduler
operator|.
name|SchedulerRequestKey
import|;
end_import

begin_import
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
name|resource
operator|.
name|Resources
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|ConcurrentSkipListSet
import|;
end_import

begin_comment
comment|/**  * Fair Scheduler specific node features.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|FSSchedulerNode
specifier|public
class|class
name|FSSchedulerNode
extends|extends
name|SchedulerNode
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
name|FSSchedulerNode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|reservedAppSchedulable
specifier|private
name|FSAppAttempt
name|reservedAppSchedulable
decl_stmt|;
comment|// Stores list of containers still to be preempted
annotation|@
name|VisibleForTesting
DECL|field|containersForPreemption
specifier|final
name|Set
argument_list|<
name|RMContainer
argument_list|>
name|containersForPreemption
init|=
operator|new
name|ConcurrentSkipListSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Stores amount of resources preempted and reserved for each app
annotation|@
name|VisibleForTesting
specifier|final
name|Map
argument_list|<
name|FSAppAttempt
argument_list|,
name|Resource
argument_list|>
DECL|field|resourcesPreemptedForApp
name|resourcesPreemptedForApp
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|appIdToAppMap
specifier|private
specifier|final
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|FSAppAttempt
argument_list|>
name|appIdToAppMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Sum of resourcesPreemptedForApp values, total resources that are
comment|// slated for preemption
DECL|field|totalResourcesPreempted
specifier|private
name|Resource
name|totalResourcesPreempted
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
DECL|method|FSSchedulerNode (RMNode node, boolean usePortForNodeName)
specifier|public
name|FSSchedulerNode
parameter_list|(
name|RMNode
name|node
parameter_list|,
name|boolean
name|usePortForNodeName
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|,
name|usePortForNodeName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Total amount of reserved resources including reservations and preempted    * containers.    * @return total resources reserved    */
DECL|method|getTotalReserved ()
name|Resource
name|getTotalReserved
parameter_list|()
block|{
name|Resource
name|totalReserved
init|=
name|Resources
operator|.
name|clone
argument_list|(
name|getReservedContainer
argument_list|()
operator|!=
literal|null
condition|?
name|getReservedContainer
argument_list|()
operator|.
name|getAllocatedResource
argument_list|()
else|:
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|totalReserved
argument_list|,
name|totalResourcesPreempted
argument_list|)
expr_stmt|;
return|return
name|totalReserved
return|;
block|}
annotation|@
name|Override
DECL|method|reserveResource ( SchedulerApplicationAttempt application, SchedulerRequestKey schedulerKey, RMContainer container)
specifier|public
specifier|synchronized
name|void
name|reserveResource
parameter_list|(
name|SchedulerApplicationAttempt
name|application
parameter_list|,
name|SchedulerRequestKey
name|schedulerKey
parameter_list|,
name|RMContainer
name|container
parameter_list|)
block|{
comment|// Check if it's already reserved
name|RMContainer
name|reservedContainer
init|=
name|getReservedContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|reservedContainer
operator|!=
literal|null
condition|)
block|{
comment|// Sanity check
if|if
condition|(
operator|!
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|getNodeID
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to reserve"
operator|+
literal|" container "
operator|+
name|container
operator|+
literal|" on node "
operator|+
name|container
operator|.
name|getReservedNode
argument_list|()
operator|+
literal|" when currently"
operator|+
literal|" reserved resource "
operator|+
name|reservedContainer
operator|+
literal|" on node "
operator|+
name|reservedContainer
operator|.
name|getReservedNode
argument_list|()
argument_list|)
throw|;
block|}
comment|// Cannot reserve more than one application on a given node!
if|if
condition|(
operator|!
name|reservedContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|equals
argument_list|(
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to reserve"
operator|+
literal|" container "
operator|+
name|container
operator|+
literal|" for application "
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" when currently"
operator|+
literal|" reserved container "
operator|+
name|reservedContainer
operator|+
literal|" on node "
operator|+
name|this
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Updated reserved container "
operator|+
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
operator|+
literal|" for application "
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reserved container "
operator|+
name|container
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
operator|+
literal|" for application "
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|setReservedContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|this
operator|.
name|reservedAppSchedulable
operator|=
operator|(
name|FSAppAttempt
operator|)
name|application
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|unreserveResource ( SchedulerApplicationAttempt application)
specifier|public
specifier|synchronized
name|void
name|unreserveResource
parameter_list|(
name|SchedulerApplicationAttempt
name|application
parameter_list|)
block|{
comment|// Cannot unreserve for wrong application...
name|ApplicationAttemptId
name|reservedApplication
init|=
name|getReservedContainer
argument_list|()
operator|.
name|getContainer
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|reservedApplication
operator|.
name|equals
argument_list|(
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Trying to unreserve "
operator|+
literal|" for application "
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" when currently reserved "
operator|+
literal|" for application "
operator|+
name|reservedApplication
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" on node "
operator|+
name|this
argument_list|)
throw|;
block|}
name|setReservedContainer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|reservedAppSchedulable
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getReservedAppSchedulable ()
specifier|synchronized
name|FSAppAttempt
name|getReservedAppSchedulable
parameter_list|()
block|{
return|return
name|reservedAppSchedulable
return|;
block|}
comment|/**    * List reserved resources after preemption and assign them to the    * appropriate applications in a FIFO order.    * @return if any resources were allocated    */
annotation|@
name|VisibleForTesting
DECL|method|getPreemptionList ()
specifier|synchronized
name|LinkedHashMap
argument_list|<
name|FSAppAttempt
argument_list|,
name|Resource
argument_list|>
name|getPreemptionList
parameter_list|()
block|{
name|cleanupPreemptionList
argument_list|()
expr_stmt|;
return|return
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|resourcesPreemptedForApp
argument_list|)
return|;
block|}
comment|/**    * Returns whether a preemption is tracked on the node for the specified app.    * @return if preempted containers are reserved for the app    */
DECL|method|isPreemptedForApp (FSAppAttempt app)
specifier|synchronized
name|boolean
name|isPreemptedForApp
parameter_list|(
name|FSAppAttempt
name|app
parameter_list|)
block|{
return|return
name|resourcesPreemptedForApp
operator|.
name|containsKey
argument_list|(
name|app
argument_list|)
return|;
block|}
comment|/**    * Remove apps that have their preemption requests fulfilled.    */
DECL|method|cleanupPreemptionList ()
specifier|private
name|void
name|cleanupPreemptionList
parameter_list|()
block|{
comment|// Synchronize separately to avoid potential deadlocks
comment|// This may cause delayed deletion of reservations
name|LinkedList
argument_list|<
name|FSAppAttempt
argument_list|>
name|candidates
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|candidates
operator|=
name|Lists
operator|.
name|newLinkedList
argument_list|(
name|resourcesPreemptedForApp
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FSAppAttempt
name|app
range|:
name|candidates
control|)
block|{
if|if
condition|(
name|app
operator|.
name|isStopped
argument_list|()
operator|||
operator|!
name|app
operator|.
name|isStarved
argument_list|()
operator|||
operator|(
name|Resources
operator|.
name|isNone
argument_list|(
name|app
operator|.
name|getFairshareStarvation
argument_list|()
argument_list|)
operator|&&
name|Resources
operator|.
name|isNone
argument_list|(
name|app
operator|.
name|getMinshareStarvation
argument_list|()
argument_list|)
operator|)
condition|)
block|{
comment|// App does not need more resources
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Resource
name|removed
init|=
name|resourcesPreemptedForApp
operator|.
name|remove
argument_list|(
name|app
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|!=
literal|null
condition|)
block|{
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|totalResourcesPreempted
argument_list|,
name|removed
argument_list|)
expr_stmt|;
name|appIdToAppMap
operator|.
name|remove
argument_list|(
name|app
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Mark {@code containers} as being considered for preemption so they are    * not considered again. A call to this requires a corresponding call to    * {@code releaseContainer} to ensure we do not mark a container for    * preemption and never consider it again and avoid memory leaks.    *    * @param containers container to mark    */
DECL|method|addContainersForPreemption (Collection<RMContainer> containers, FSAppAttempt app)
name|void
name|addContainersForPreemption
parameter_list|(
name|Collection
argument_list|<
name|RMContainer
argument_list|>
name|containers
parameter_list|,
name|FSAppAttempt
name|app
parameter_list|)
block|{
name|Resource
name|appReserved
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|RMContainer
name|container
range|:
name|containers
control|)
block|{
if|if
condition|(
name|containersForPreemption
operator|.
name|add
argument_list|(
name|container
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|appReserved
argument_list|,
name|container
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
operator|!
name|Resources
operator|.
name|isNone
argument_list|(
name|appReserved
argument_list|)
condition|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|totalResourcesPreempted
argument_list|,
name|appReserved
argument_list|)
expr_stmt|;
name|appIdToAppMap
operator|.
name|putIfAbsent
argument_list|(
name|app
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|app
argument_list|)
expr_stmt|;
name|resourcesPreemptedForApp
operator|.
name|putIfAbsent
argument_list|(
name|app
argument_list|,
name|Resource
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|resourcesPreemptedForApp
operator|.
name|get
argument_list|(
name|app
argument_list|)
argument_list|,
name|appReserved
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @return set of containers marked for preemption.    */
DECL|method|getContainersForPreemption ()
name|Set
argument_list|<
name|RMContainer
argument_list|>
name|getContainersForPreemption
parameter_list|()
block|{
return|return
name|containersForPreemption
return|;
block|}
comment|/**    * The Scheduler has allocated containers on this node to the given    * application.    * @param rmContainer Allocated container    * @param launchedOnNode True if the container has been launched    */
annotation|@
name|Override
DECL|method|allocateContainer (RMContainer rmContainer, boolean launchedOnNode)
specifier|protected
specifier|synchronized
name|void
name|allocateContainer
parameter_list|(
name|RMContainer
name|rmContainer
parameter_list|,
name|boolean
name|launchedOnNode
parameter_list|)
block|{
name|super
operator|.
name|allocateContainer
argument_list|(
name|rmContainer
argument_list|,
name|launchedOnNode
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
specifier|final
name|Container
name|container
init|=
name|rmContainer
operator|.
name|getContainer
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Assigned container "
operator|+
name|container
operator|.
name|getId
argument_list|()
operator|+
literal|" of capacity "
operator|+
name|container
operator|.
name|getResource
argument_list|()
operator|+
literal|" on host "
operator|+
name|getRMNode
argument_list|()
operator|.
name|getNodeAddress
argument_list|()
operator|+
literal|", which has "
operator|+
name|getNumContainers
argument_list|()
operator|+
literal|" containers, "
operator|+
name|getAllocatedResource
argument_list|()
operator|+
literal|" used and "
operator|+
name|getUnallocatedResource
argument_list|()
operator|+
literal|" available after allocation"
argument_list|)
expr_stmt|;
block|}
name|Resource
name|allocated
init|=
name|rmContainer
operator|.
name|getAllocatedResource
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Resources
operator|.
name|isNone
argument_list|(
name|allocated
argument_list|)
condition|)
block|{
comment|// check for satisfied preemption request and update bookkeeping
name|FSAppAttempt
name|app
init|=
name|appIdToAppMap
operator|.
name|get
argument_list|(
name|rmContainer
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|app
operator|!=
literal|null
condition|)
block|{
name|Resource
name|reserved
init|=
name|resourcesPreemptedForApp
operator|.
name|get
argument_list|(
name|app
argument_list|)
decl_stmt|;
name|Resource
name|fulfilled
init|=
name|Resources
operator|.
name|componentwiseMin
argument_list|(
name|reserved
argument_list|,
name|allocated
argument_list|)
decl_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|reserved
argument_list|,
name|fulfilled
argument_list|)
expr_stmt|;
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|totalResourcesPreempted
argument_list|,
name|fulfilled
argument_list|)
expr_stmt|;
if|if
condition|(
name|Resources
operator|.
name|isNone
argument_list|(
name|reserved
argument_list|)
condition|)
block|{
comment|// No more preempted containers
name|resourcesPreemptedForApp
operator|.
name|remove
argument_list|(
name|app
argument_list|)
expr_stmt|;
name|appIdToAppMap
operator|.
name|remove
argument_list|(
name|rmContainer
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Allocated empty container"
operator|+
name|rmContainer
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Release an allocated container on this node.    * It also releases from the reservation list to trigger preemption    * allocations.    * @param containerId ID of container to be released.    * @param releasedByNode whether the release originates from a node update.    */
annotation|@
name|Override
DECL|method|releaseContainer (ContainerId containerId, boolean releasedByNode)
specifier|public
specifier|synchronized
name|void
name|releaseContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|boolean
name|releasedByNode
parameter_list|)
block|{
name|RMContainer
name|container
init|=
name|getContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|super
operator|.
name|releaseContainer
argument_list|(
name|containerId
argument_list|,
name|releasedByNode
argument_list|)
expr_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
condition|)
block|{
name|containersForPreemption
operator|.
name|remove
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

