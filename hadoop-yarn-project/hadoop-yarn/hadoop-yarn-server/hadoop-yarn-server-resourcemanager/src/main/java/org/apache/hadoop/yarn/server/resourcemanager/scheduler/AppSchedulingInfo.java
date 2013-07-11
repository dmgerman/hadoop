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
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|atomic
operator|.
name|AtomicInteger
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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_comment
comment|/**  * This class keeps track of all the consumption of an application. This also  * keeps track of current running/completed containers for the application.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AppSchedulingInfo
specifier|public
class|class
name|AppSchedulingInfo
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
name|AppSchedulingInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|applicationAttemptId
specifier|private
specifier|final
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|field|applicationId
specifier|final
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|queueName
specifier|private
specifier|final
name|String
name|queueName
decl_stmt|;
DECL|field|queue
name|Queue
name|queue
decl_stmt|;
DECL|field|user
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|containerIdCounter
specifier|private
specifier|final
name|AtomicInteger
name|containerIdCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|priorities
specifier|final
name|Set
argument_list|<
name|Priority
argument_list|>
name|priorities
init|=
operator|new
name|TreeSet
argument_list|<
name|Priority
argument_list|>
argument_list|(
operator|new
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
name|Priority
operator|.
name|Comparator
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|requests
specifier|final
name|Map
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
name|requests
init|=
operator|new
name|HashMap
argument_list|<
name|Priority
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|blacklist
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|blacklist
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//private final ApplicationStore store;
DECL|field|activeUsersManager
specifier|private
specifier|final
name|ActiveUsersManager
name|activeUsersManager
decl_stmt|;
comment|/* Allocated by scheduler */
DECL|field|pending
name|boolean
name|pending
init|=
literal|true
decl_stmt|;
comment|// for app metrics
DECL|method|AppSchedulingInfo (ApplicationAttemptId appAttemptId, String user, Queue queue, ActiveUsersManager activeUsersManager)
specifier|public
name|AppSchedulingInfo
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|String
name|user
parameter_list|,
name|Queue
name|queue
parameter_list|,
name|ActiveUsersManager
name|activeUsersManager
parameter_list|)
block|{
name|this
operator|.
name|applicationAttemptId
operator|=
name|appAttemptId
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|appAttemptId
operator|.
name|getApplicationId
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
name|queueName
operator|=
name|queue
operator|.
name|getQueueName
argument_list|()
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|activeUsersManager
operator|=
name|activeUsersManager
expr_stmt|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|applicationId
return|;
block|}
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|applicationAttemptId
return|;
block|}
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|queueName
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|isPending ()
specifier|public
specifier|synchronized
name|boolean
name|isPending
parameter_list|()
block|{
return|return
name|pending
return|;
block|}
comment|/**    * Clear any pending requests from this application.    */
DECL|method|clearRequests ()
specifier|private
specifier|synchronized
name|void
name|clearRequests
parameter_list|()
block|{
name|priorities
operator|.
name|clear
argument_list|()
expr_stmt|;
name|requests
operator|.
name|clear
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Application "
operator|+
name|applicationId
operator|+
literal|" requests cleared"
argument_list|)
expr_stmt|;
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
name|containerIdCounter
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/**    * The ApplicationMaster is updating resource requirements for the    * application, by asking for more resources and releasing resources acquired    * by the application.    *     * @param requests resources to be acquired    * @param blacklistAdditions resources to be added to the blacklist    * @param blacklistRemovals resources to be removed from the blacklist    */
DECL|method|updateResourceRequests ( List<ResourceRequest> requests, List<String> blacklistAdditions, List<String> blacklistRemovals)
specifier|synchronized
specifier|public
name|void
name|updateResourceRequests
parameter_list|(
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|requests
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
parameter_list|)
block|{
name|QueueMetrics
name|metrics
init|=
name|queue
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
comment|// Update resource requests
for|for
control|(
name|ResourceRequest
name|request
range|:
name|requests
control|)
block|{
name|Priority
name|priority
init|=
name|request
operator|.
name|getPriority
argument_list|()
decl_stmt|;
name|String
name|resourceName
init|=
name|request
operator|.
name|getResourceName
argument_list|()
decl_stmt|;
name|boolean
name|updatePendingResources
init|=
literal|false
decl_stmt|;
name|ResourceRequest
name|lastRequest
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resourceName
operator|.
name|equals
argument_list|(
name|ResourceRequest
operator|.
name|ANY
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
literal|"update:"
operator|+
literal|" application="
operator|+
name|applicationId
operator|+
literal|" request="
operator|+
name|request
argument_list|)
expr_stmt|;
block|}
name|updatePendingResources
operator|=
literal|true
expr_stmt|;
comment|// Premature optimization?
comment|// Assumes that we won't see more than one priority request updated
comment|// in one call, reasonable assumption... however, it's totally safe
comment|// to activate same application more than once.
comment|// Thus we don't need another loop ala the one in decrementOutstanding()
comment|// which is needed during deactivate.
if|if
condition|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|>
literal|0
condition|)
block|{
name|activeUsersManager
operator|.
name|activateApplication
argument_list|(
name|user
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|asks
init|=
name|this
operator|.
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|asks
operator|==
literal|null
condition|)
block|{
name|asks
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|requests
operator|.
name|put
argument_list|(
name|priority
argument_list|,
name|asks
argument_list|)
expr_stmt|;
name|this
operator|.
name|priorities
operator|.
name|add
argument_list|(
name|priority
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|updatePendingResources
condition|)
block|{
name|lastRequest
operator|=
name|asks
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
expr_stmt|;
block|}
name|asks
operator|.
name|put
argument_list|(
name|resourceName
argument_list|,
name|request
argument_list|)
expr_stmt|;
if|if
condition|(
name|updatePendingResources
condition|)
block|{
comment|// Similarly, deactivate application?
if|if
condition|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"checking for deactivate... "
argument_list|)
expr_stmt|;
name|checkForDeactivation
argument_list|()
expr_stmt|;
block|}
name|int
name|lastRequestContainers
init|=
name|lastRequest
operator|!=
literal|null
condition|?
name|lastRequest
operator|.
name|getNumContainers
argument_list|()
else|:
literal|0
decl_stmt|;
name|Resource
name|lastRequestCapability
init|=
name|lastRequest
operator|!=
literal|null
condition|?
name|lastRequest
operator|.
name|getCapability
argument_list|()
else|:
name|Resources
operator|.
name|none
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|incrPendingResources
argument_list|(
name|user
argument_list|,
name|request
operator|.
name|getNumContainers
argument_list|()
operator|-
name|lastRequestContainers
argument_list|,
name|Resources
operator|.
name|subtractFrom
argument_list|(
comment|// save a clone
name|Resources
operator|.
name|multiply
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|,
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|,
name|Resources
operator|.
name|multiply
argument_list|(
name|lastRequestCapability
argument_list|,
name|lastRequestContainers
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//
comment|// Update blacklist
comment|//
comment|// Add to blacklist
if|if
condition|(
name|blacklistAdditions
operator|!=
literal|null
condition|)
block|{
name|blacklist
operator|.
name|addAll
argument_list|(
name|blacklistAdditions
argument_list|)
expr_stmt|;
block|}
comment|// Remove from blacklist
if|if
condition|(
name|blacklistRemovals
operator|!=
literal|null
condition|)
block|{
name|blacklist
operator|.
name|removeAll
argument_list|(
name|blacklistRemovals
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getPriorities ()
specifier|synchronized
specifier|public
name|Collection
argument_list|<
name|Priority
argument_list|>
name|getPriorities
parameter_list|()
block|{
return|return
name|priorities
return|;
block|}
DECL|method|getResourceRequests ( Priority priority)
specifier|synchronized
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
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
return|;
block|}
DECL|method|getAllResourceRequests ()
specifier|synchronized
specifier|public
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|getAllResourceRequests
parameter_list|()
block|{
name|List
argument_list|<
name|ResourceRequest
argument_list|>
name|ret
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceRequest
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|r
range|:
name|requests
operator|.
name|values
argument_list|()
control|)
block|{
name|ret
operator|.
name|addAll
argument_list|(
name|r
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|getResourceRequest (Priority priority, String resourceName)
specifier|synchronized
specifier|public
name|ResourceRequest
name|getResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|resourceName
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|nodeRequests
init|=
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
decl_stmt|;
return|return
operator|(
name|nodeRequests
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|nodeRequests
operator|.
name|get
argument_list|(
name|resourceName
argument_list|)
return|;
block|}
DECL|method|getResource (Priority priority)
specifier|public
specifier|synchronized
name|Resource
name|getResource
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|getResourceRequest
argument_list|(
name|priority
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|)
decl_stmt|;
return|return
name|request
operator|.
name|getCapability
argument_list|()
return|;
block|}
DECL|method|isBlacklisted (String resourceName)
specifier|public
specifier|synchronized
name|boolean
name|isBlacklisted
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
return|return
name|blacklist
operator|.
name|contains
argument_list|(
name|resourceName
argument_list|)
return|;
block|}
comment|/**    * Resources have been allocated to this application by the resource    * scheduler. Track them.    *     * @param type    *          the type of the node    * @param node    *          the nodeinfo of the node    * @param priority    *          the priority of the request.    * @param request    *          the request    * @param container    *          the containers allocated.    */
DECL|method|allocate (NodeType type, SchedulerNode node, Priority priority, ResourceRequest request, Container container)
specifier|synchronized
specifier|public
name|void
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
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|NODE_LOCAL
condition|)
block|{
name|allocateNodeLocal
argument_list|(
name|node
argument_list|,
name|priority
argument_list|,
name|request
argument_list|,
name|container
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|NodeType
operator|.
name|RACK_LOCAL
condition|)
block|{
name|allocateRackLocal
argument_list|(
name|node
argument_list|,
name|priority
argument_list|,
name|request
argument_list|,
name|container
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allocateOffSwitch
argument_list|(
name|node
argument_list|,
name|priority
argument_list|,
name|request
argument_list|,
name|container
argument_list|)
expr_stmt|;
block|}
name|QueueMetrics
name|metrics
init|=
name|queue
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
if|if
condition|(
name|pending
condition|)
block|{
comment|// once an allocation is done we assume the application is
comment|// running from scheduler's POV.
name|pending
operator|=
literal|false
expr_stmt|;
name|metrics
operator|.
name|incrAppsRunning
argument_list|(
name|this
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"allocate: user: "
operator|+
name|user
operator|+
literal|", memory: "
operator|+
name|request
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|allocateResources
argument_list|(
name|user
argument_list|,
literal|1
argument_list|,
name|request
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * The {@link ResourceScheduler} is allocating data-local resources to the    * application.    *     * @param allocatedContainers    *          resources allocated to the application    */
DECL|method|allocateNodeLocal ( SchedulerNode node, Priority priority, ResourceRequest nodeLocalRequest, Container container)
specifier|synchronized
specifier|private
name|void
name|allocateNodeLocal
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|ResourceRequest
name|nodeLocalRequest
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
comment|// Update consumption and track allocations
name|allocate
argument_list|(
name|container
argument_list|)
expr_stmt|;
comment|// Update future requirements
name|nodeLocalRequest
operator|.
name|setNumContainers
argument_list|(
name|nodeLocalRequest
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeLocalRequest
operator|.
name|getNumContainers
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ResourceRequest
name|rackLocalRequest
init|=
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|get
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|)
decl_stmt|;
name|rackLocalRequest
operator|.
name|setNumContainers
argument_list|(
name|rackLocalRequest
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|rackLocalRequest
operator|.
name|getNumContainers
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|decrementOutstanding
argument_list|(
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * The {@link ResourceScheduler} is allocating data-local resources to the    * application.    *     * @param allocatedContainers    *          resources allocated to the application    */
DECL|method|allocateRackLocal ( SchedulerNode node, Priority priority, ResourceRequest rackLocalRequest, Container container)
specifier|synchronized
specifier|private
name|void
name|allocateRackLocal
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|ResourceRequest
name|rackLocalRequest
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
comment|// Update consumption and track allocations
name|allocate
argument_list|(
name|container
argument_list|)
expr_stmt|;
comment|// Update future requirements
name|rackLocalRequest
operator|.
name|setNumContainers
argument_list|(
name|rackLocalRequest
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|rackLocalRequest
operator|.
name|getNumContainers
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getRackName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|decrementOutstanding
argument_list|(
name|requests
operator|.
name|get
argument_list|(
name|priority
argument_list|)
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * The {@link ResourceScheduler} is allocating data-local resources to the    * application.    *     * @param allocatedContainers    *          resources allocated to the application    */
DECL|method|allocateOffSwitch ( SchedulerNode node, Priority priority, ResourceRequest offSwitchRequest, Container container)
specifier|synchronized
specifier|private
name|void
name|allocateOffSwitch
parameter_list|(
name|SchedulerNode
name|node
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|ResourceRequest
name|offSwitchRequest
parameter_list|,
name|Container
name|container
parameter_list|)
block|{
comment|// Update consumption and track allocations
name|allocate
argument_list|(
name|container
argument_list|)
expr_stmt|;
comment|// Update future requirements
name|decrementOutstanding
argument_list|(
name|offSwitchRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|decrementOutstanding ( ResourceRequest offSwitchRequest)
specifier|synchronized
specifier|private
name|void
name|decrementOutstanding
parameter_list|(
name|ResourceRequest
name|offSwitchRequest
parameter_list|)
block|{
name|int
name|numOffSwitchContainers
init|=
name|offSwitchRequest
operator|.
name|getNumContainers
argument_list|()
operator|-
literal|1
decl_stmt|;
comment|// Do not remove ANY
name|offSwitchRequest
operator|.
name|setNumContainers
argument_list|(
name|numOffSwitchContainers
argument_list|)
expr_stmt|;
comment|// Do we have any outstanding requests?
comment|// If there is nothing, we need to deactivate this application
if|if
condition|(
name|numOffSwitchContainers
operator|==
literal|0
condition|)
block|{
name|checkForDeactivation
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkForDeactivation ()
specifier|synchronized
specifier|private
name|void
name|checkForDeactivation
parameter_list|()
block|{
name|boolean
name|deactivate
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Priority
name|priority
range|:
name|getPriorities
argument_list|()
control|)
block|{
name|ResourceRequest
name|request
init|=
name|getResourceRequest
argument_list|(
name|priority
argument_list|,
name|ResourceRequest
operator|.
name|ANY
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getNumContainers
argument_list|()
operator|>
literal|0
condition|)
block|{
name|deactivate
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|deactivate
condition|)
block|{
name|activeUsersManager
operator|.
name|deactivateApplication
argument_list|(
name|user
argument_list|,
name|applicationId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|allocate (Container container)
specifier|synchronized
specifier|private
name|void
name|allocate
parameter_list|(
name|Container
name|container
parameter_list|)
block|{
comment|// Update consumption and track allocations
comment|//TODO: fixme sharad
comment|/* try {         store.storeContainer(container);       } catch (IOException ie) {         // TODO fix this. we shouldnt ignore       }*/
name|LOG
operator|.
name|debug
argument_list|(
literal|"allocate: applicationId="
operator|+
name|applicationId
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|stop (RMAppAttemptState rmAppAttemptFinalState)
specifier|synchronized
specifier|public
name|void
name|stop
parameter_list|(
name|RMAppAttemptState
name|rmAppAttemptFinalState
parameter_list|)
block|{
comment|// clear pending resources metrics for the application
name|QueueMetrics
name|metrics
init|=
name|queue
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|ResourceRequest
argument_list|>
name|asks
range|:
name|requests
operator|.
name|values
argument_list|()
control|)
block|{
name|ResourceRequest
name|request
init|=
name|asks
operator|.
name|get
argument_list|(
name|ResourceRequest
operator|.
name|ANY
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
block|{
name|metrics
operator|.
name|decrPendingResources
argument_list|(
name|user
argument_list|,
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|,
name|Resources
operator|.
name|multiply
argument_list|(
name|request
operator|.
name|getCapability
argument_list|()
argument_list|,
name|request
operator|.
name|getNumContainers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|metrics
operator|.
name|finishApp
argument_list|(
name|this
argument_list|,
name|rmAppAttemptFinalState
argument_list|)
expr_stmt|;
comment|// Clear requests themselves
name|clearRequests
argument_list|()
expr_stmt|;
block|}
DECL|method|setQueue (Queue queue)
specifier|public
specifier|synchronized
name|void
name|setQueue
parameter_list|(
name|Queue
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
block|}
end_class

end_unit

