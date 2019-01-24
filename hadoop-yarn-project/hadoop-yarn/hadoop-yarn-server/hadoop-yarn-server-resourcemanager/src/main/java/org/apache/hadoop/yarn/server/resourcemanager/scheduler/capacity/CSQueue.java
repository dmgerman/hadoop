begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|capacity
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
name|Collection
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
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
name|security
operator|.
name|AccessControlException
import|;
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
name|QueueACL
import|;
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
name|QueueState
import|;
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
name|security
operator|.
name|PrivilegedEntity
import|;
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
name|scheduler
operator|.
name|AbstractUsersManager
import|;
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
name|QueueResourceQuotas
import|;
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
name|ResourceUsage
import|;
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
name|SchedulerQueue
import|;
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
name|AbstractCSQueue
operator|.
name|CapacityConfigType
import|;
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
name|common
operator|.
name|ResourceCommitRequest
import|;
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
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerApp
import|;
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
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerNode
import|;
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
name|placement
operator|.
name|CandidateNodeSet
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
comment|/**  *<code>CSQueue</code> represents a node in the tree of   * hierarchical queues in the {@link CapacityScheduler}.  */
end_comment

begin_interface
annotation|@
name|Stable
annotation|@
name|Private
DECL|interface|CSQueue
specifier|public
interface|interface
name|CSQueue
extends|extends
name|SchedulerQueue
argument_list|<
name|CSQueue
argument_list|>
block|{
comment|/**    * Get the parent<code>Queue</code>.    * @return the parent queue    */
DECL|method|getParent ()
specifier|public
name|CSQueue
name|getParent
parameter_list|()
function_decl|;
comment|/**    * Set the parent<code>Queue</code>.    * @param newParentQueue new parent queue    */
DECL|method|setParent (CSQueue newParentQueue)
specifier|public
name|void
name|setParent
parameter_list|(
name|CSQueue
name|newParentQueue
parameter_list|)
function_decl|;
comment|/**    * Get the queue name.    * @return the queue name    */
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
function_decl|;
comment|/**    * Get the full name of the queue, including the heirarchy.    * @return the full name of the queue    */
DECL|method|getQueuePath ()
specifier|public
name|String
name|getQueuePath
parameter_list|()
function_decl|;
DECL|method|getPrivilegedEntity ()
specifier|public
name|PrivilegedEntity
name|getPrivilegedEntity
parameter_list|()
function_decl|;
DECL|method|getMaximumAllocation ()
name|Resource
name|getMaximumAllocation
parameter_list|()
function_decl|;
DECL|method|getMinimumAllocation ()
name|Resource
name|getMinimumAllocation
parameter_list|()
function_decl|;
comment|/**    * Get the configured<em>capacity</em> of the queue.    * @return configured queue capacity    */
DECL|method|getCapacity ()
specifier|public
name|float
name|getCapacity
parameter_list|()
function_decl|;
comment|/**    * Get capacity of the parent of the queue as a function of the     * cumulative capacity in the cluster.    * @return capacity of the parent of the queue as a function of the     *         cumulative capacity in the cluster    */
DECL|method|getAbsoluteCapacity ()
specifier|public
name|float
name|getAbsoluteCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the configured maximum-capacity of the queue.     * @return the configured maximum-capacity of the queue    */
DECL|method|getMaximumCapacity ()
specifier|public
name|float
name|getMaximumCapacity
parameter_list|()
function_decl|;
comment|/**    * Get maximum-capacity of the queue as a funciton of the cumulative capacity    * of the cluster.    * @return maximum-capacity of the queue as a funciton of the cumulative capacity    *         of the cluster    */
DECL|method|getAbsoluteMaximumCapacity ()
specifier|public
name|float
name|getAbsoluteMaximumCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the current absolute used capacity of the queue    * relative to the entire cluster.    * @return queue absolute used capacity    */
DECL|method|getAbsoluteUsedCapacity ()
specifier|public
name|float
name|getAbsoluteUsedCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the current used capacity of nodes without label(s) of the queue    * and it's children (if any).    * @return queue used capacity    */
DECL|method|getUsedCapacity ()
specifier|public
name|float
name|getUsedCapacity
parameter_list|()
function_decl|;
comment|/**    * Get the currently utilized resources which allocated at nodes without any    * labels in the cluster by the queue and children (if any).    *     * @return used resources by the queue and it's children    */
DECL|method|getUsedResources ()
specifier|public
name|Resource
name|getUsedResources
parameter_list|()
function_decl|;
comment|/**    * Get the current run-state of the queue    * @return current run-state    */
DECL|method|getState ()
specifier|public
name|QueueState
name|getState
parameter_list|()
function_decl|;
comment|/**    * Get child queues    * @return child queues    */
DECL|method|getChildQueues ()
specifier|public
name|List
argument_list|<
name|CSQueue
argument_list|>
name|getChildQueues
parameter_list|()
function_decl|;
comment|/**    * Check if the<code>user</code> has permission to perform the operation    * @param acl ACL    * @param user user    * @return<code>true</code> if the user has the permission,     *<code>false</code> otherwise    */
DECL|method|hasAccess (QueueACL acl, UserGroupInformation user)
specifier|public
name|boolean
name|hasAccess
parameter_list|(
name|QueueACL
name|acl
parameter_list|,
name|UserGroupInformation
name|user
parameter_list|)
function_decl|;
comment|/**    * Submit a new application to the queue.    * @param applicationId the applicationId of the application being submitted    * @param user user who submitted the application    * @param queue queue to which the application is submitted    */
DECL|method|submitApplication (ApplicationId applicationId, String user, String queue)
specifier|public
name|void
name|submitApplication
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
comment|/**    * Submit an application attempt to the queue.    */
DECL|method|submitApplicationAttempt (FiCaSchedulerApp application, String userName)
specifier|public
name|void
name|submitApplicationAttempt
parameter_list|(
name|FiCaSchedulerApp
name|application
parameter_list|,
name|String
name|userName
parameter_list|)
function_decl|;
comment|/**    * An application submitted to this queue has finished.    * @param applicationId    * @param user user who submitted the application    */
DECL|method|finishApplication (ApplicationId applicationId, String user)
specifier|public
name|void
name|finishApplication
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|user
parameter_list|)
function_decl|;
comment|/**    * An application attempt submitted to this queue has finished.    */
DECL|method|finishApplicationAttempt (FiCaSchedulerApp application, String queue)
specifier|public
name|void
name|finishApplicationAttempt
parameter_list|(
name|FiCaSchedulerApp
name|application
parameter_list|,
name|String
name|queue
parameter_list|)
function_decl|;
comment|/**    * Assign containers to applications in the queue or it's children (if any).    * @param clusterResource the resource of the cluster.    * @param candidates {@link CandidateNodeSet} the nodes that are considered    *                   for the current placement.    * @param resourceLimits how much overall resource of this queue can use.     * @param schedulingMode Type of exclusive check when assign container on a     * NodeManager, see {@link SchedulingMode}.    * @return the assignment    */
DECL|method|assignContainers (Resource clusterResource, CandidateNodeSet<FiCaSchedulerNode> candidates, ResourceLimits resourceLimits, SchedulingMode schedulingMode)
specifier|public
name|CSAssignment
name|assignContainers
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|CandidateNodeSet
argument_list|<
name|FiCaSchedulerNode
argument_list|>
name|candidates
parameter_list|,
name|ResourceLimits
name|resourceLimits
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|)
function_decl|;
comment|/**    * A container assigned to the queue has completed.    * @param clusterResource the resource of the cluster    * @param application application to which the container was assigned    * @param node node on which the container completed    * @param container completed container,     *<code>null</code> if it was just a reservation    * @param containerStatus<code>ContainerStatus</code> for the completed     *                        container    * @param childQueue<code>CSQueue</code> to reinsert in childQueues     * @param event event to be sent to the container    * @param sortQueues indicates whether it should re-sort the queues    */
DECL|method|completedContainer (Resource clusterResource, FiCaSchedulerApp application, FiCaSchedulerNode node, RMContainer container, ContainerStatus containerStatus, RMContainerEventType event, CSQueue childQueue, boolean sortQueues)
specifier|public
name|void
name|completedContainer
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|FiCaSchedulerApp
name|application
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|,
name|RMContainer
name|container
parameter_list|,
name|ContainerStatus
name|containerStatus
parameter_list|,
name|RMContainerEventType
name|event
parameter_list|,
name|CSQueue
name|childQueue
parameter_list|,
name|boolean
name|sortQueues
parameter_list|)
function_decl|;
comment|/**    * Get the number of applications in the queue.    * @return number of applications    */
DECL|method|getNumApplications ()
specifier|public
name|int
name|getNumApplications
parameter_list|()
function_decl|;
comment|/**    * Reinitialize the queue.    * @param newlyParsedQueue new queue to re-initalize from    * @param clusterResource resources in the cluster    */
DECL|method|reinitialize (CSQueue newlyParsedQueue, Resource clusterResource)
specifier|public
name|void
name|reinitialize
parameter_list|(
name|CSQueue
name|newlyParsedQueue
parameter_list|,
name|Resource
name|clusterResource
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Update the cluster resource for queues as we add/remove nodes    * @param clusterResource the current cluster resource    * @param resourceLimits the current ResourceLimits    */
DECL|method|updateClusterResource (Resource clusterResource, ResourceLimits resourceLimits)
specifier|public
name|void
name|updateClusterResource
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|ResourceLimits
name|resourceLimits
parameter_list|)
function_decl|;
comment|/**    * Get the {@link AbstractUsersManager} for the queue.    * @return the<code>AbstractUsersManager</code> for the queue    */
DECL|method|getAbstractUsersManager ()
specifier|public
name|AbstractUsersManager
name|getAbstractUsersManager
parameter_list|()
function_decl|;
comment|/**    * Adds all applications in the queue and its subqueues to the given collection.    * @param apps the collection to add the applications to    */
DECL|method|collectSchedulerApplications (Collection<ApplicationAttemptId> apps)
specifier|public
name|void
name|collectSchedulerApplications
parameter_list|(
name|Collection
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|apps
parameter_list|)
function_decl|;
comment|/**   * Detach a container from this queue   * @param clusterResource the current cluster resource   * @param application application to which the container was assigned   * @param container the container to detach   */
DECL|method|detachContainer (Resource clusterResource, FiCaSchedulerApp application, RMContainer container)
specifier|public
name|void
name|detachContainer
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|FiCaSchedulerApp
name|application
parameter_list|,
name|RMContainer
name|container
parameter_list|)
function_decl|;
comment|/**    * Attach a container to this queue    * @param clusterResource the current cluster resource    * @param application application to which the container was assigned    * @param container the container to attach    */
DECL|method|attachContainer (Resource clusterResource, FiCaSchedulerApp application, RMContainer container)
specifier|public
name|void
name|attachContainer
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|FiCaSchedulerApp
name|application
parameter_list|,
name|RMContainer
name|container
parameter_list|)
function_decl|;
comment|/**    * Check whether<em>disable_preemption</em> property is set for this queue    * @return true if<em>disable_preemption</em> is set, false if not    */
DECL|method|getPreemptionDisabled ()
specifier|public
name|boolean
name|getPreemptionDisabled
parameter_list|()
function_decl|;
comment|/**    * Check whether intra-queue preemption is disabled for this queue    * @return true if either intra-queue preemption or inter-queue preemption    * is disabled for this queue, false if neither is disabled.    */
DECL|method|getIntraQueuePreemptionDisabled ()
specifier|public
name|boolean
name|getIntraQueuePreemptionDisabled
parameter_list|()
function_decl|;
comment|/**    * Determines whether or not the intra-queue preemption disabled switch is set    *  at any level in this queue's hierarchy.    * @return state of the intra-queue preemption switch at this queue level    */
DECL|method|getIntraQueuePreemptionDisabledInHierarchy ()
specifier|public
name|boolean
name|getIntraQueuePreemptionDisabledInHierarchy
parameter_list|()
function_decl|;
comment|/**    * Get QueueCapacities of this queue    * @return queueCapacities    */
DECL|method|getQueueCapacities ()
specifier|public
name|QueueCapacities
name|getQueueCapacities
parameter_list|()
function_decl|;
comment|/**    * Get ResourceUsage of this queue    * @return resourceUsage    */
DECL|method|getQueueResourceUsage ()
specifier|public
name|ResourceUsage
name|getQueueResourceUsage
parameter_list|()
function_decl|;
comment|/**    * When partition of node updated, we will update queue's resource usage if it    * has container(s) running on that.    */
DECL|method|incUsedResource (String nodePartition, Resource resourceToInc, SchedulerApplicationAttempt application)
specifier|public
name|void
name|incUsedResource
parameter_list|(
name|String
name|nodePartition
parameter_list|,
name|Resource
name|resourceToInc
parameter_list|,
name|SchedulerApplicationAttempt
name|application
parameter_list|)
function_decl|;
comment|/**    * When partition of node updated, we will update queue's resource usage if it    * has container(s) running on that.    */
DECL|method|decUsedResource (String nodePartition, Resource resourceToDec, SchedulerApplicationAttempt application)
specifier|public
name|void
name|decUsedResource
parameter_list|(
name|String
name|nodePartition
parameter_list|,
name|Resource
name|resourceToDec
parameter_list|,
name|SchedulerApplicationAttempt
name|application
parameter_list|)
function_decl|;
comment|/**    * When an outstanding resource is fulfilled or canceled, calling this will    * decrease pending resource in a queue.    *    * @param nodeLabel    *          asked by application    * @param resourceToDec    *          new resource asked    */
DECL|method|decPendingResource (String nodeLabel, Resource resourceToDec)
specifier|public
name|void
name|decPendingResource
parameter_list|(
name|String
name|nodeLabel
parameter_list|,
name|Resource
name|resourceToDec
parameter_list|)
function_decl|;
comment|/**    * Get valid Node Labels for this queue    * @return valid node labels    */
DECL|method|getNodeLabelsForQueue ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeLabelsForQueue
parameter_list|()
function_decl|;
annotation|@
name|VisibleForTesting
DECL|method|assignContainers (Resource clusterResource, FiCaSchedulerNode node, ResourceLimits resourceLimits, SchedulingMode schedulingMode)
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
name|resourceLimits
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|)
function_decl|;
DECL|method|accept (Resource cluster, ResourceCommitRequest<FiCaSchedulerApp, FiCaSchedulerNode> request)
name|boolean
name|accept
parameter_list|(
name|Resource
name|cluster
parameter_list|,
name|ResourceCommitRequest
argument_list|<
name|FiCaSchedulerApp
argument_list|,
name|FiCaSchedulerNode
argument_list|>
name|request
parameter_list|)
function_decl|;
DECL|method|apply (Resource cluster, ResourceCommitRequest<FiCaSchedulerApp, FiCaSchedulerNode> request)
name|void
name|apply
parameter_list|(
name|Resource
name|cluster
parameter_list|,
name|ResourceCommitRequest
argument_list|<
name|FiCaSchedulerApp
argument_list|,
name|FiCaSchedulerNode
argument_list|>
name|request
parameter_list|)
function_decl|;
comment|/**    * Get readLock associated with the Queue.    * @return readLock of corresponding queue.    */
DECL|method|getReadLock ()
specifier|public
name|ReentrantReadWriteLock
operator|.
name|ReadLock
name|getReadLock
parameter_list|()
function_decl|;
comment|/**    * Validate submitApplication api so that moveApplication do a pre-check.    * @param applicationId Application ID    * @param userName User Name    * @param queue Queue Name    * @throws AccessControlException if any acl violation is there.    */
DECL|method|validateSubmitApplication (ApplicationId applicationId, String userName, String queue)
specifier|public
name|void
name|validateSubmitApplication
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|queue
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
comment|/**    * Get priority of queue    * @return queue priority    */
DECL|method|getPriority ()
name|Priority
name|getPriority
parameter_list|()
function_decl|;
comment|/**    * Get a map of usernames and weights    * @return map of usernames and corresponding weight    */
DECL|method|getUserWeights ()
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|getUserWeights
parameter_list|()
function_decl|;
comment|/**    * Get QueueResourceQuotas associated with each queue.    * @return QueueResourceQuotas    */
DECL|method|getQueueResourceQuotas ()
specifier|public
name|QueueResourceQuotas
name|getQueueResourceQuotas
parameter_list|()
function_decl|;
comment|/**    * Get CapacityConfigType as PERCENTAGE or ABSOLUTE_RESOURCE.    * @return CapacityConfigType    */
DECL|method|getCapacityConfigType ()
specifier|public
name|CapacityConfigType
name|getCapacityConfigType
parameter_list|()
function_decl|;
comment|/**    * Get effective capacity of queue. If min/max resource is configured,    * preference will be given to absolute configuration over normal capacity.    *    * @param label    *          partition    * @return effective queue capacity    */
DECL|method|getEffectiveCapacity (String label)
name|Resource
name|getEffectiveCapacity
parameter_list|(
name|String
name|label
parameter_list|)
function_decl|;
comment|/**    * Get effective capacity of queue. If min/max resource is configured,    * preference will be given to absolute configuration over normal capacity.    * Also round down the result to normalizeDown.    *    * @param label    *          partition    * @param factor    *          factor to normalize down     * @return effective queue capacity    */
DECL|method|getEffectiveCapacityDown (String label, Resource factor)
name|Resource
name|getEffectiveCapacityDown
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|factor
parameter_list|)
function_decl|;
comment|/**    * Get effective max capacity of queue. If min/max resource is configured,    * preference will be given to absolute configuration over normal capacity.    *    * @param label    *          partition    * @return effective max queue capacity    */
DECL|method|getEffectiveMaxCapacity (String label)
name|Resource
name|getEffectiveMaxCapacity
parameter_list|(
name|String
name|label
parameter_list|)
function_decl|;
comment|/**    * Get effective max capacity of queue. If min/max resource is configured,    * preference will be given to absolute configuration over normal capacity.    * Also round down the result to normalizeDown.    *    * @param label    *          partition    * @param factor    *          factor to normalize down     * @return effective max queue capacity    */
DECL|method|getEffectiveMaxCapacityDown (String label, Resource factor)
name|Resource
name|getEffectiveMaxCapacityDown
parameter_list|(
name|String
name|label
parameter_list|,
name|Resource
name|factor
parameter_list|)
function_decl|;
comment|/**    * Get Multi Node scheduling policy name.    * @return policy name    */
DECL|method|getMultiNodeSortingPolicyName ()
name|String
name|getMultiNodeSortingPolicyName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

