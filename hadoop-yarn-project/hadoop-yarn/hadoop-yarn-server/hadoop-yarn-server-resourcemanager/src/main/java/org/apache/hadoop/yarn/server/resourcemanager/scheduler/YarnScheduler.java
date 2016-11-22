begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|EnumSet
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
name|Set
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
name|LimitedPrivate
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
name|Public
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
name|Evolving
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
name|ApplicationResourceUsageReport
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
name|QueueInfo
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
name|QueueUserACLInfo
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
name|api
operator|.
name|records
operator|.
name|UpdateContainerRequest
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|common
operator|.
name|QueueEntitlement
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
name|proto
operator|.
name|YarnServiceProtos
operator|.
name|SchedulerResourceTypes
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|SettableFuture
import|;
end_import

begin_comment
comment|/**  * This interface is used by the components to talk to the  * scheduler for allocating of resources, cleaning up resources.  *  */
end_comment

begin_interface
DECL|interface|YarnScheduler
specifier|public
interface|interface
name|YarnScheduler
extends|extends
name|EventHandler
argument_list|<
name|SchedulerEvent
argument_list|>
block|{
comment|/**    * Get queue information    * @param queueName queue name    * @param includeChildQueues include child queues?    * @param recursive get children queues?    * @return queue information    * @throws IOException    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueueInfo (String queueName, boolean includeChildQueues, boolean recursive)
specifier|public
name|QueueInfo
name|getQueueInfo
parameter_list|(
name|String
name|queueName
parameter_list|,
name|boolean
name|includeChildQueues
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get acls for queues for current user.    * @return acls for queues for current user    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getQueueUserAclInfo ()
specifier|public
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getQueueUserAclInfo
parameter_list|()
function_decl|;
comment|/**    * Get the whole resource capacity of the cluster.    * @return the whole resource capacity of the cluster.    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Unstable
DECL|method|getClusterResource ()
specifier|public
name|Resource
name|getClusterResource
parameter_list|()
function_decl|;
comment|/**    * Get minimum allocatable {@link Resource}.    * @return minimum allocatable resource    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMinimumResourceCapability ()
specifier|public
name|Resource
name|getMinimumResourceCapability
parameter_list|()
function_decl|;
comment|/**    * Get maximum allocatable {@link Resource} at the cluster level.    * @return maximum allocatable resource    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMaximumResourceCapability ()
specifier|public
name|Resource
name|getMaximumResourceCapability
parameter_list|()
function_decl|;
comment|/**    * Get maximum allocatable {@link Resource} for the queue specified.    * @param queueName queue name    * @return maximum allocatable resource    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMaximumResourceCapability (String queueName)
specifier|public
name|Resource
name|getMaximumResourceCapability
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Evolving
DECL|method|getResourceCalculator ()
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
function_decl|;
comment|/**    * Get the number of nodes available in the cluster.    * @return the number of available nodes.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumClusterNodes ()
specifier|public
name|int
name|getNumClusterNodes
parameter_list|()
function_decl|;
comment|/**    * The main api between the ApplicationMaster and the Scheduler.    * The ApplicationMaster is updating his future resource requirements    * and may release containers he doens't need.    *     * @param appAttemptId    * @param ask    * @param release    * @param blacklistAdditions     * @param blacklistRemovals     * @param increaseRequests    * @param decreaseRequests    * @return the {@link Allocation} for the application    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|allocate (ApplicationAttemptId appAttemptId, List<ResourceRequest> ask, List<ContainerId> release, List<String> blacklistAdditions, List<String> blacklistRemovals, List<UpdateContainerRequest> increaseRequests, List<UpdateContainerRequest> decreaseRequests)
name|Allocation
name|allocate
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ask
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|release
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|blacklistAdditions
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|blacklistRemovals
parameter_list|,
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|increaseRequests
parameter_list|,
name|List
argument_list|<
name|UpdateContainerRequest
argument_list|>
name|decreaseRequests
parameter_list|)
function_decl|;
comment|/**    * Get node resource usage report.    * @param nodeId    * @return the {@link SchedulerNodeReport} for the node or null    * if nodeId does not point to a defined node.    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Stable
DECL|method|getNodeReport (NodeId nodeId)
specifier|public
name|SchedulerNodeReport
name|getNodeReport
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
comment|/**    * Get the Scheduler app for a given app attempt Id.    * @param appAttemptId the id of the application attempt    * @return SchedulerApp for this given attempt.    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Stable
DECL|method|getSchedulerAppInfo (ApplicationAttemptId appAttemptId)
name|SchedulerAppReport
name|getSchedulerAppInfo
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
function_decl|;
comment|/**    * Get a resource usage report from a given app attempt ID.    * @param appAttemptId the id of the application attempt    * @return resource usage report for this given attempt    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Evolving
DECL|method|getAppResourceUsageReport ( ApplicationAttemptId appAttemptId)
name|ApplicationResourceUsageReport
name|getAppResourceUsageReport
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
function_decl|;
comment|/**    * Get the root queue for the scheduler.    * @return the root queue for the scheduler.    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Evolving
DECL|method|getRootQueueMetrics ()
name|QueueMetrics
name|getRootQueueMetrics
parameter_list|()
function_decl|;
comment|/**    * Check if the user has permission to perform the operation.    * If the user has {@link QueueACL#ADMINISTER_QUEUE} permission,    * this user can view/modify the applications in this queue    * @param callerUGI    * @param acl    * @param queueName    * @return<code>true</code> if the user has the permission,    *<code>false</code> otherwise    */
DECL|method|checkAccess (UserGroupInformation callerUGI, QueueACL acl, String queueName)
name|boolean
name|checkAccess
parameter_list|(
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|QueueACL
name|acl
parameter_list|,
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Gets the apps under a given queue    * @param queueName the name of the queue.    * @return a collection of app attempt ids in the given queue.    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Stable
DECL|method|getAppsInQueue (String queueName)
specifier|public
name|List
argument_list|<
name|ApplicationAttemptId
argument_list|>
name|getAppsInQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Get the container for the given containerId.    * @param containerId    * @return the container for the given containerId.    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Unstable
DECL|method|getRMContainer (ContainerId containerId)
specifier|public
name|RMContainer
name|getRMContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Moves the given application to the given queue    * @param appId    * @param newQueue    * @return the name of the queue the application was placed into    * @throws YarnException if the move cannot be carried out    */
annotation|@
name|LimitedPrivate
argument_list|(
literal|"yarn"
argument_list|)
annotation|@
name|Evolving
DECL|method|moveApplication (ApplicationId appId, String newQueue)
specifier|public
name|String
name|moveApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|newQueue
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Completely drain sourceQueue of applications, by moving all of them to    * destQueue.    *    * @param sourceQueue    * @param destQueue    * @throws YarnException    */
DECL|method|moveAllApps (String sourceQueue, String destQueue)
name|void
name|moveAllApps
parameter_list|(
name|String
name|sourceQueue
parameter_list|,
name|String
name|destQueue
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Terminate all applications in the specified queue.    *    * @param queueName the name of queue to be drained    * @throws YarnException    */
DECL|method|killAllAppsInQueue (String queueName)
name|void
name|killAllAppsInQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Remove an existing queue. Implementations might limit when a queue could be    * removed (e.g., must have zero entitlement, and no applications running, or    * must be a leaf, etc..).    *    * @param queueName name of the queue to remove    * @throws YarnException    */
DECL|method|removeQueue (String queueName)
name|void
name|removeQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Add to the scheduler a new Queue. Implementations might limit what type of    * queues can be dynamically added (e.g., Queue must be a leaf, must be    * attached to existing parent, must have zero entitlement).    *    * @param newQueue the queue being added.    * @throws YarnException    */
DECL|method|addQueue (Queue newQueue)
name|void
name|addQueue
parameter_list|(
name|Queue
name|newQueue
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * This method increase the entitlement for current queue (must respect    * invariants, e.g., no overcommit of parents, non negative, etc.).    * Entitlement is a general term for weights in FairScheduler, capacity for    * the CapacityScheduler, etc.    *    * @param queue the queue for which we change entitlement    * @param entitlement the new entitlement for the queue (capacity,    *              maxCapacity, etc..)    * @throws YarnException    */
DECL|method|setEntitlement (String queue, QueueEntitlement entitlement)
name|void
name|setEntitlement
parameter_list|(
name|String
name|queue
parameter_list|,
name|QueueEntitlement
name|entitlement
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Gets the list of names for queues managed by the Reservation System    * @return the list of queues which support reservations    */
DECL|method|getPlanQueues ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPlanQueues
parameter_list|()
throws|throws
name|YarnException
function_decl|;
comment|/**    * Return a collection of the resource types that are considered when    * scheduling    *    * @return an EnumSet containing the resource types    */
DECL|method|getSchedulingResourceTypes ()
specifier|public
name|EnumSet
argument_list|<
name|SchedulerResourceTypes
argument_list|>
name|getSchedulingResourceTypes
parameter_list|()
function_decl|;
comment|/**    *    * Verify whether a submitted application priority is valid as per configured    * Queue    *    * @param priorityFromContext    *          Submitted Application priority.    * @param user    *          User who submitted the Application    * @param queueName    *          Name of the Queue    * @param applicationId    *          Application ID    * @return Updated Priority from scheduler    */
DECL|method|checkAndGetApplicationPriority (Priority priorityFromContext, String user, String queueName, ApplicationId applicationId)
specifier|public
name|Priority
name|checkAndGetApplicationPriority
parameter_list|(
name|Priority
name|priorityFromContext
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queueName
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    *    * Change application priority of a submitted application at runtime    *    * @param newPriority Submitted Application priority.    *    * @param applicationId Application ID    *    * @param future Sets any type of exception happened from StateStore    *    * @return updated priority    */
DECL|method|updateApplicationPriority (Priority newPriority, ApplicationId applicationId, SettableFuture<Object> future)
specifier|public
name|Priority
name|updateApplicationPriority
parameter_list|(
name|Priority
name|newPriority
parameter_list|,
name|ApplicationId
name|applicationId
parameter_list|,
name|SettableFuture
argument_list|<
name|Object
argument_list|>
name|future
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    *    * Get previous attempts' live containers for work-preserving AM restart.    *    * @param appAttemptId the id of the application attempt    *    * @return list of live containers for the given attempt    */
DECL|method|getTransferredContainers (ApplicationAttemptId appAttemptId)
name|List
argument_list|<
name|Container
argument_list|>
name|getTransferredContainers
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
function_decl|;
comment|/**    * Set the cluster max priority    *     * @param conf    * @throws YarnException    */
DECL|method|setClusterMaxPriority (Configuration conf)
name|void
name|setClusterMaxPriority
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * @param attemptId    */
DECL|method|getPendingResourceRequestsForAttempt ( ApplicationAttemptId attemptId)
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getPendingResourceRequestsForAttempt
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
function_decl|;
comment|/**    * Get cluster max priority.    *     * @return maximum priority of cluster    */
DECL|method|getMaxClusterLevelAppPriority ()
name|Priority
name|getMaxClusterLevelAppPriority
parameter_list|()
function_decl|;
comment|/**    * Get SchedulerNode corresponds to nodeId.    *    * @param nodeId the node id of RMNode    *    * @return SchedulerNode corresponds to nodeId    */
DECL|method|getSchedulerNode (NodeId nodeId)
name|SchedulerNode
name|getSchedulerNode
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

