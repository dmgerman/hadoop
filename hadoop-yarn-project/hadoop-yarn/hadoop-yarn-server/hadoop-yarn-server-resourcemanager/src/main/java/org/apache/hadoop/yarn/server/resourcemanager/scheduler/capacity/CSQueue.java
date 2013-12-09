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
name|SchedulerApplication
import|;
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
comment|/**    * Get the configured<em>capacity</em> of the queue.    * @return queue capacity    */
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
comment|/**    * Get the current used capacity of the queue    * and it's children (if any).    * @return queue used capacity    */
DECL|method|getUsedCapacity ()
specifier|public
name|float
name|getUsedCapacity
parameter_list|()
function_decl|;
comment|/**    * Set used capacity of the queue.    * @param usedCapacity used capacity of the queue    */
DECL|method|setUsedCapacity (float usedCapacity)
specifier|public
name|void
name|setUsedCapacity
parameter_list|(
name|float
name|usedCapacity
parameter_list|)
function_decl|;
comment|/**    * Set absolute used capacity of the queue.    * @param absUsedCapacity absolute used capacity of the queue    */
DECL|method|setAbsoluteUsedCapacity (float absUsedCapacity)
specifier|public
name|void
name|setAbsoluteUsedCapacity
parameter_list|(
name|float
name|absUsedCapacity
parameter_list|)
function_decl|;
comment|/**    * Get the currently utilized resources in the cluster     * by the queue and children (if any).    * @return used resources by the queue and it's children     */
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
comment|/**    * Submit a new application to the queue.    * @param application application being submitted    * @param user user who submitted the application    * @param queue queue to which the application is submitted    */
DECL|method|submitApplication (FiCaSchedulerApp application, String user, String queue)
specifier|public
name|void
name|submitApplication
parameter_list|(
name|FiCaSchedulerApp
name|application
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
comment|/**    * An application submitted to this queue has finished.    * @param application    * @param queue application queue     */
DECL|method|finishApplication (FiCaSchedulerApp application, String queue)
specifier|public
name|void
name|finishApplication
parameter_list|(
name|FiCaSchedulerApp
name|application
parameter_list|,
name|String
name|queue
parameter_list|)
function_decl|;
comment|/**    * Assign containers to applications in the queue or it's children (if any).    * @param clusterResource the resource of the cluster.    * @param node node on which resources are available    * @return the assignment    */
DECL|method|assignContainers ( Resource clusterResource, FiCaSchedulerNode node)
specifier|public
name|CSAssignment
name|assignContainers
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|)
function_decl|;
comment|/**    * A container assigned to the queue has completed.    * @param clusterResource the resource of the cluster    * @param application application to which the container was assigned    * @param node node on which the container completed    * @param container completed container,     *<code>null</code> if it was just a reservation    * @param containerStatus<code>ContainerStatus</code> for the completed     *                        container    * @param childQueue<code>CSQueue</code> to reinsert in childQueues     * @param event event to be sent to the container    */
DECL|method|completedContainer (Resource clusterResource, FiCaSchedulerApp application, FiCaSchedulerNode node, RMContainer container, ContainerStatus containerStatus, RMContainerEventType event, CSQueue childQueue)
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
comment|/**    * Update the cluster resource for queues as we add/remove nodes    * @param clusterResource the current cluster resource    */
DECL|method|updateClusterResource (Resource clusterResource)
specifier|public
name|void
name|updateClusterResource
parameter_list|(
name|Resource
name|clusterResource
parameter_list|)
function_decl|;
comment|/**    * Get the {@link ActiveUsersManager} for the queue.    * @return the<code>ActiveUsersManager</code> for the queue    */
DECL|method|getActiveUsersManager ()
specifier|public
name|ActiveUsersManager
name|getActiveUsersManager
parameter_list|()
function_decl|;
comment|/**    * Recover the state of the queue    * @param clusterResource the resource of the cluster    * @param application the application for which the container was allocated    * @param container the container that was recovered.    */
DECL|method|recoverContainer (Resource clusterResource, FiCaSchedulerApp application, Container container)
specifier|public
name|void
name|recoverContainer
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|FiCaSchedulerApp
name|application
parameter_list|,
name|Container
name|container
parameter_list|)
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
block|}
end_interface

end_unit

