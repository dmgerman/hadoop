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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|ha
operator|.
name|HAServiceProtocol
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
name|security
operator|.
name|Permission
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
name|YarnAuthorizationProvider
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|reservation
operator|.
name|ReservationConstants
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
name|QueueStateManager
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
name|SchedulerDynamicEditException
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
name|SchedulerQueueManager
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
name|security
operator|.
name|AppPriorityACLsManager
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
comment|/**  *  * Context of the Queues in Capacity Scheduler.  *  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|CapacitySchedulerQueueManager
specifier|public
class|class
name|CapacitySchedulerQueueManager
implements|implements
name|SchedulerQueueManager
argument_list|<
name|CSQueue
argument_list|,
name|CapacitySchedulerConfiguration
argument_list|>
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
name|CapacitySchedulerQueueManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NON_PARTITIONED_QUEUE_COMPARATOR
specifier|static
specifier|final
name|Comparator
argument_list|<
name|CSQueue
argument_list|>
name|NON_PARTITIONED_QUEUE_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|CSQueue
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|CSQueue
name|q1
parameter_list|,
name|CSQueue
name|q2
parameter_list|)
block|{
name|int
name|result
init|=
name|Float
operator|.
name|compare
argument_list|(
name|q1
operator|.
name|getUsedCapacity
argument_list|()
argument_list|,
name|q2
operator|.
name|getUsedCapacity
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|result
operator|>
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|q1
operator|.
name|getQueuePath
argument_list|()
operator|.
name|compareTo
argument_list|(
name|q2
operator|.
name|getQueuePath
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|class|QueueHook
specifier|static
class|class
name|QueueHook
block|{
DECL|method|hook (CSQueue queue)
specifier|public
name|CSQueue
name|hook
parameter_list|(
name|CSQueue
name|queue
parameter_list|)
block|{
return|return
name|queue
return|;
block|}
block|}
DECL|field|NOOP
specifier|private
specifier|static
specifier|final
name|QueueHook
name|NOOP
init|=
operator|new
name|QueueHook
argument_list|()
decl_stmt|;
DECL|field|csContext
specifier|private
name|CapacitySchedulerContext
name|csContext
decl_stmt|;
DECL|field|authorizer
specifier|private
specifier|final
name|YarnAuthorizationProvider
name|authorizer
decl_stmt|;
DECL|field|queues
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|queues
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|root
specifier|private
name|CSQueue
name|root
decl_stmt|;
DECL|field|labelManager
specifier|private
specifier|final
name|RMNodeLabelsManager
name|labelManager
decl_stmt|;
DECL|field|appPriorityACLManager
specifier|private
name|AppPriorityACLsManager
name|appPriorityACLManager
decl_stmt|;
specifier|private
name|QueueStateManager
argument_list|<
name|CSQueue
argument_list|,
name|CapacitySchedulerConfiguration
argument_list|>
DECL|field|queueStateManager
name|queueStateManager
decl_stmt|;
comment|/**    * Construct the service.    * @param conf the configuration    * @param labelManager the labelManager    * @param appPriorityACLManager App priority ACL manager    */
DECL|method|CapacitySchedulerQueueManager (Configuration conf, RMNodeLabelsManager labelManager, AppPriorityACLsManager appPriorityACLManager)
specifier|public
name|CapacitySchedulerQueueManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RMNodeLabelsManager
name|labelManager
parameter_list|,
name|AppPriorityACLsManager
name|appPriorityACLManager
parameter_list|)
block|{
name|this
operator|.
name|authorizer
operator|=
name|YarnAuthorizationProvider
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|labelManager
operator|=
name|labelManager
expr_stmt|;
name|this
operator|.
name|queueStateManager
operator|=
operator|new
name|QueueStateManager
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|appPriorityACLManager
operator|=
name|appPriorityACLManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRootQueue ()
specifier|public
name|CSQueue
name|getRootQueue
parameter_list|()
block|{
return|return
name|this
operator|.
name|root
return|;
block|}
annotation|@
name|Override
DECL|method|getQueues ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|getQueues
parameter_list|()
block|{
return|return
name|queues
return|;
block|}
annotation|@
name|Override
DECL|method|removeQueue (String queueName)
specifier|public
name|void
name|removeQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|this
operator|.
name|queues
operator|.
name|remove
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addQueue (String queueName, CSQueue queue)
specifier|public
name|void
name|addQueue
parameter_list|(
name|String
name|queueName
parameter_list|,
name|CSQueue
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queues
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQueue (String queueName)
specifier|public
name|CSQueue
name|getQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
return|return
name|queues
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
return|;
block|}
comment|/**    * Set the CapacitySchedulerContext.    * @param capacitySchedulerContext the CapacitySchedulerContext    */
DECL|method|setCapacitySchedulerContext ( CapacitySchedulerContext capacitySchedulerContext)
specifier|public
name|void
name|setCapacitySchedulerContext
parameter_list|(
name|CapacitySchedulerContext
name|capacitySchedulerContext
parameter_list|)
block|{
name|this
operator|.
name|csContext
operator|=
name|capacitySchedulerContext
expr_stmt|;
block|}
comment|/**    * Initialized the queues.    * @param conf the CapacitySchedulerConfiguration    * @throws IOException if fails to initialize queues    */
DECL|method|initializeQueues (CapacitySchedulerConfiguration conf)
specifier|public
name|void
name|initializeQueues
parameter_list|(
name|CapacitySchedulerConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|root
operator|=
name|parseQueue
argument_list|(
name|this
operator|.
name|csContext
argument_list|,
name|conf
argument_list|,
literal|null
argument_list|,
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
name|queues
argument_list|,
name|queues
argument_list|,
name|NOOP
argument_list|)
expr_stmt|;
name|setQueueAcls
argument_list|(
name|authorizer
argument_list|,
name|appPriorityACLManager
argument_list|,
name|queues
argument_list|)
expr_stmt|;
name|labelManager
operator|.
name|reinitializeQueueLabels
argument_list|(
name|getQueueToLabels
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueStateManager
operator|.
name|initialize
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized root queue "
operator|+
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reinitializeQueues (CapacitySchedulerConfiguration newConf)
specifier|public
name|void
name|reinitializeQueues
parameter_list|(
name|CapacitySchedulerConfiguration
name|newConf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Parse new queues
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|newQueues
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|CSQueue
name|newRoot
init|=
name|parseQueue
argument_list|(
name|this
operator|.
name|csContext
argument_list|,
name|newConf
argument_list|,
literal|null
argument_list|,
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
name|newQueues
argument_list|,
name|queues
argument_list|,
name|NOOP
argument_list|)
decl_stmt|;
comment|// When failing over, if using configuration store, don't validate queue
comment|// hierarchy since queues can be removed without being STOPPED.
if|if
condition|(
operator|!
name|csContext
operator|.
name|isConfigurationMutable
argument_list|()
operator|||
name|csContext
operator|.
name|getRMContext
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
operator|!=
name|HAServiceProtocol
operator|.
name|HAServiceState
operator|.
name|STANDBY
condition|)
block|{
comment|// Ensure queue hierarchy in the new XML file is proper.
name|validateQueueHierarchy
argument_list|(
name|queues
argument_list|,
name|newQueues
argument_list|)
expr_stmt|;
block|}
comment|// Add new queues and delete OldQeueus only after validation.
name|updateQueues
argument_list|(
name|queues
argument_list|,
name|newQueues
argument_list|)
expr_stmt|;
comment|// Re-configure queues
name|root
operator|.
name|reinitialize
argument_list|(
name|newRoot
argument_list|,
name|this
operator|.
name|csContext
operator|.
name|getClusterResource
argument_list|()
argument_list|)
expr_stmt|;
name|setQueueAcls
argument_list|(
name|authorizer
argument_list|,
name|appPriorityACLManager
argument_list|,
name|queues
argument_list|)
expr_stmt|;
comment|// Re-calculate headroom for active applications
name|Resource
name|clusterResource
init|=
name|this
operator|.
name|csContext
operator|.
name|getClusterResource
argument_list|()
decl_stmt|;
name|root
operator|.
name|updateClusterResource
argument_list|(
name|clusterResource
argument_list|,
operator|new
name|ResourceLimits
argument_list|(
name|clusterResource
argument_list|)
argument_list|)
expr_stmt|;
name|labelManager
operator|.
name|reinitializeQueueLabels
argument_list|(
name|getQueueToLabels
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueStateManager
operator|.
name|initialize
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parse the queue from the configuration.    * @param csContext the CapacitySchedulerContext    * @param conf the CapacitySchedulerConfiguration    * @param parent the parent queue    * @param queueName the queue name    * @param queues all the queues    * @param oldQueues the old queues    * @param hook the queue hook    * @return the CSQueue    * @throws IOException    */
DECL|method|parseQueue ( CapacitySchedulerContext csContext, CapacitySchedulerConfiguration conf, CSQueue parent, String queueName, Map<String, CSQueue> queues, Map<String, CSQueue> oldQueues, QueueHook hook)
specifier|static
name|CSQueue
name|parseQueue
parameter_list|(
name|CapacitySchedulerContext
name|csContext
parameter_list|,
name|CapacitySchedulerConfiguration
name|conf
parameter_list|,
name|CSQueue
name|parent
parameter_list|,
name|String
name|queueName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|queues
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|oldQueues
parameter_list|,
name|QueueHook
name|hook
parameter_list|)
throws|throws
name|IOException
block|{
name|CSQueue
name|queue
decl_stmt|;
name|String
name|fullQueueName
init|=
operator|(
name|parent
operator|==
literal|null
operator|)
condition|?
name|queueName
else|:
operator|(
name|parent
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|"."
operator|+
name|queueName
operator|)
decl_stmt|;
name|String
index|[]
name|childQueueNames
init|=
name|conf
operator|.
name|getQueues
argument_list|(
name|fullQueueName
argument_list|)
decl_stmt|;
name|boolean
name|isReservableQueue
init|=
name|conf
operator|.
name|isReservable
argument_list|(
name|fullQueueName
argument_list|)
decl_stmt|;
name|boolean
name|isAutoCreateEnabled
init|=
name|conf
operator|.
name|isAutoCreateChildQueueEnabled
argument_list|(
name|fullQueueName
argument_list|)
decl_stmt|;
if|if
condition|(
name|childQueueNames
operator|==
literal|null
operator|||
name|childQueueNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|parent
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Queue configuration missing child queue names for "
operator|+
name|queueName
argument_list|)
throw|;
block|}
comment|// Check if the queue will be dynamically managed by the Reservation
comment|// system
if|if
condition|(
name|isReservableQueue
condition|)
block|{
name|queue
operator|=
operator|new
name|PlanQueue
argument_list|(
name|csContext
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|oldQueues
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
argument_list|)
expr_stmt|;
comment|//initializing the "internal" default queue, for SLS compatibility
name|String
name|defReservationId
init|=
name|queueName
operator|+
name|ReservationConstants
operator|.
name|DEFAULT_QUEUE_SUFFIX
decl_stmt|;
name|List
argument_list|<
name|CSQueue
argument_list|>
name|childQueues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ReservationQueue
name|resQueue
init|=
operator|new
name|ReservationQueue
argument_list|(
name|csContext
argument_list|,
name|defReservationId
argument_list|,
operator|(
name|PlanQueue
operator|)
name|queue
argument_list|)
decl_stmt|;
try|try
block|{
name|resQueue
operator|.
name|setEntitlement
argument_list|(
operator|new
name|QueueEntitlement
argument_list|(
literal|1.0f
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SchedulerDynamicEditException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|childQueues
operator|.
name|add
argument_list|(
name|resQueue
argument_list|)
expr_stmt|;
operator|(
operator|(
name|PlanQueue
operator|)
name|queue
operator|)
operator|.
name|setChildQueues
argument_list|(
name|childQueues
argument_list|)
expr_stmt|;
name|queues
operator|.
name|put
argument_list|(
name|defReservationId
argument_list|,
name|resQueue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isAutoCreateEnabled
condition|)
block|{
name|queue
operator|=
operator|new
name|ManagedParentQueue
argument_list|(
name|csContext
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|oldQueues
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|=
operator|new
name|LeafQueue
argument_list|(
name|csContext
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|oldQueues
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
argument_list|)
expr_stmt|;
comment|// Used only for unit tests
name|queue
operator|=
name|hook
operator|.
name|hook
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|isReservableQueue
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Only Leaf Queues can be reservable for "
operator|+
name|queueName
argument_list|)
throw|;
block|}
name|ParentQueue
name|parentQueue
decl_stmt|;
if|if
condition|(
name|isAutoCreateEnabled
condition|)
block|{
name|parentQueue
operator|=
operator|new
name|ManagedParentQueue
argument_list|(
name|csContext
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|oldQueues
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parentQueue
operator|=
operator|new
name|ParentQueue
argument_list|(
name|csContext
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|oldQueues
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Used only for unit tests
name|queue
operator|=
name|hook
operator|.
name|hook
argument_list|(
name|parentQueue
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CSQueue
argument_list|>
name|childQueues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|childQueueName
range|:
name|childQueueNames
control|)
block|{
name|CSQueue
name|childQueue
init|=
name|parseQueue
argument_list|(
name|csContext
argument_list|,
name|conf
argument_list|,
name|queue
argument_list|,
name|childQueueName
argument_list|,
name|queues
argument_list|,
name|oldQueues
argument_list|,
name|hook
argument_list|)
decl_stmt|;
name|childQueues
operator|.
name|add
argument_list|(
name|childQueue
argument_list|)
expr_stmt|;
block|}
name|parentQueue
operator|.
name|setChildQueues
argument_list|(
name|childQueues
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queue
operator|instanceof
name|LeafQueue
operator|&&
name|queues
operator|.
name|containsKey
argument_list|(
name|queueName
argument_list|)
operator|&&
name|queues
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
operator|instanceof
name|LeafQueue
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Two leaf queues were named "
operator|+
name|queueName
operator|+
literal|". Leaf queue names must be distinct"
argument_list|)
throw|;
block|}
name|queues
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initialized queue: "
operator|+
name|queue
argument_list|)
expr_stmt|;
return|return
name|queue
return|;
block|}
comment|/**    * Ensure all existing queues are present. Queues cannot be deleted if its not    * in Stopped state, Queue's cannot be moved from one hierarchy to other also.    * Previous child queue could be converted into parent queue if it is in    * STOPPED state.    *    * @param queues existing queues    * @param newQueues new queues    */
DECL|method|validateQueueHierarchy (Map<String, CSQueue> queues, Map<String, CSQueue> newQueues)
specifier|private
name|void
name|validateQueueHierarchy
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|queues
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|newQueues
parameter_list|)
throws|throws
name|IOException
block|{
comment|// check that all static queues are included in the newQueues list
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|e
range|:
name|queues
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|AbstractAutoCreatedLeafQueue
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|String
name|queueName
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|CSQueue
name|oldQueue
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|CSQueue
name|newQueue
init|=
name|newQueues
operator|.
name|get
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|newQueue
condition|)
block|{
comment|// old queue doesn't exist in the new XML
if|if
condition|(
name|oldQueue
operator|.
name|getState
argument_list|()
operator|==
name|QueueState
operator|.
name|STOPPED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting Queue "
operator|+
name|queueName
operator|+
literal|", as it is not"
operator|+
literal|" present in the modified capacity configuration xml"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|oldQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|" is deleted from"
operator|+
literal|" the new capacity scheduler configuration, but the"
operator|+
literal|" queue is not yet in stopped state. "
operator|+
literal|"Current State : "
operator|+
name|oldQueue
operator|.
name|getState
argument_list|()
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|oldQueue
operator|.
name|getQueuePath
argument_list|()
operator|.
name|equals
argument_list|(
name|newQueue
operator|.
name|getQueuePath
argument_list|()
argument_list|)
condition|)
block|{
comment|//Queue's cannot be moved from one hierarchy to other
throw|throw
operator|new
name|IOException
argument_list|(
name|queueName
operator|+
literal|" is moved from:"
operator|+
name|oldQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|" to:"
operator|+
name|newQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|" after refresh, which is not allowed."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|oldQueue
operator|instanceof
name|ParentQueue
operator|&&
operator|!
operator|(
name|oldQueue
operator|instanceof
name|ManagedParentQueue
operator|)
operator|&&
name|newQueue
operator|instanceof
name|ManagedParentQueue
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can not convert parent queue: "
operator|+
name|oldQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|" to auto create enabled parent queue since "
operator|+
literal|"it could have other pre-configured queues which is not "
operator|+
literal|"supported"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|oldQueue
operator|instanceof
name|ManagedParentQueue
operator|&&
operator|!
operator|(
name|newQueue
operator|instanceof
name|ManagedParentQueue
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot convert auto create enabled parent queue: "
operator|+
name|oldQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|" to leaf queue. Please check "
operator|+
literal|" parent queue's configuration "
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|AUTO_CREATE_CHILD_QUEUE_ENABLED
operator|+
literal|" is set to true"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|oldQueue
operator|instanceof
name|LeafQueue
operator|&&
name|newQueue
operator|instanceof
name|ParentQueue
condition|)
block|{
if|if
condition|(
name|oldQueue
operator|.
name|getState
argument_list|()
operator|==
name|QueueState
operator|.
name|STOPPED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Converting the leaf queue: "
operator|+
name|oldQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|" to parent queue."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can not convert the leaf queue: "
operator|+
name|oldQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|" to parent queue since "
operator|+
literal|"it is not yet in stopped state. Current State : "
operator|+
name|oldQueue
operator|.
name|getState
argument_list|()
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|oldQueue
operator|instanceof
name|ParentQueue
operator|&&
name|newQueue
operator|instanceof
name|LeafQueue
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Converting the parent queue: "
operator|+
name|oldQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|" to leaf queue."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Updates to our list of queues: Adds the new queues and deletes the removed    * ones... be careful, do not overwrite existing queues.    *    * @param existingQueues, the existing queues    * @param newQueues the new queues based on new XML    */
DECL|method|updateQueues (Map<String, CSQueue> existingQueues, Map<String, CSQueue> newQueues)
specifier|private
name|void
name|updateQueues
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|existingQueues
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|newQueues
parameter_list|)
block|{
name|CapacitySchedulerConfiguration
name|conf
init|=
name|csContext
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|e
range|:
name|newQueues
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|queueName
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|CSQueue
name|queue
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|existingQueues
operator|.
name|containsKey
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
name|existingQueues
operator|.
name|put
argument_list|(
name|queueName
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
argument_list|>
name|itr
init|=
name|existingQueues
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|itr
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|e
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|queueName
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|CSQueue
name|existingQueue
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|newQueues
operator|.
name|containsKey
argument_list|(
name|queueName
argument_list|)
operator|&&
operator|!
operator|(
name|existingQueue
operator|instanceof
name|AutoCreatedLeafQueue
operator|&&
name|conf
operator|.
name|isAutoCreateChildQueueEnabled
argument_list|(
name|existingQueue
operator|.
name|getParent
argument_list|()
operator|.
name|getQueuePath
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|itr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
comment|/**    * Set the acls for the queues.    * @param authorizer the yarnAuthorizationProvider    * @param queues the queues    * @throws IOException if fails to set queue acls    */
DECL|method|setQueueAcls (YarnAuthorizationProvider authorizer, AppPriorityACLsManager appPriorityACLManager, Map<String, CSQueue> queues)
specifier|public
specifier|static
name|void
name|setQueueAcls
parameter_list|(
name|YarnAuthorizationProvider
name|authorizer
parameter_list|,
name|AppPriorityACLsManager
name|appPriorityACLManager
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|CSQueue
argument_list|>
name|queues
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Permission
argument_list|>
name|permissions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CSQueue
name|queue
range|:
name|queues
operator|.
name|values
argument_list|()
control|)
block|{
name|AbstractCSQueue
name|csQueue
init|=
operator|(
name|AbstractCSQueue
operator|)
name|queue
decl_stmt|;
name|permissions
operator|.
name|add
argument_list|(
operator|new
name|Permission
argument_list|(
name|csQueue
operator|.
name|getPrivilegedEntity
argument_list|()
argument_list|,
name|csQueue
operator|.
name|getACLs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|instanceof
name|LeafQueue
condition|)
block|{
name|LeafQueue
name|lQueue
init|=
operator|(
name|LeafQueue
operator|)
name|queue
decl_stmt|;
comment|// Clear Priority ACLs first since reinitialize also call same.
name|appPriorityACLManager
operator|.
name|clearPriorityACLs
argument_list|(
name|lQueue
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|appPriorityACLManager
operator|.
name|addPrioirityACLs
argument_list|(
name|lQueue
operator|.
name|getPriorityACLs
argument_list|()
argument_list|,
name|lQueue
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|authorizer
operator|.
name|setPermission
argument_list|(
name|permissions
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check that the String provided in input is the name of an existing,    * LeafQueue, if successful returns the queue.    *    * @param queue the queue name    * @return the LeafQueue    * @throws YarnException if the queue does not exist or the queue    *           is not the type of LeafQueue.    */
DECL|method|getAndCheckLeafQueue (String queue)
specifier|public
name|LeafQueue
name|getAndCheckLeafQueue
parameter_list|(
name|String
name|queue
parameter_list|)
throws|throws
name|YarnException
block|{
name|CSQueue
name|ret
init|=
name|this
operator|.
name|getQueue
argument_list|(
name|queue
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"The specified Queue: "
operator|+
name|queue
operator|+
literal|" doesn't exist"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|ret
operator|instanceof
name|LeafQueue
operator|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"The specified Queue: "
operator|+
name|queue
operator|+
literal|" is not a Leaf Queue."
argument_list|)
throw|;
block|}
return|return
operator|(
name|LeafQueue
operator|)
name|ret
return|;
block|}
comment|/**    * Get the default priority of the queue.    * @param queueName the queue name    * @return the default priority of the queue    */
DECL|method|getDefaultPriorityForQueue (String queueName)
specifier|public
name|Priority
name|getDefaultPriorityForQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
name|Queue
name|queue
init|=
name|getQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|queue
operator|||
literal|null
operator|==
name|queue
operator|.
name|getDefaultApplicationPriority
argument_list|()
condition|)
block|{
comment|// Return with default application priority
return|return
name|Priority
operator|.
name|newInstance
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|DEFAULT_CONFIGURATION_APPLICATION_PRIORITY
argument_list|)
return|;
block|}
return|return
name|Priority
operator|.
name|newInstance
argument_list|(
name|queue
operator|.
name|getDefaultApplicationPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get a map of queueToLabels.    * @return the map of queueToLabels    */
DECL|method|getQueueToLabels ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|getQueueToLabels
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|queueToLabels
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CSQueue
name|queue
range|:
name|getQueues
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|queueToLabels
operator|.
name|put
argument_list|(
name|queue
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|queue
operator|.
name|getAccessibleNodeLabels
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|queueToLabels
return|;
block|}
annotation|@
name|Private
specifier|public
name|QueueStateManager
argument_list|<
name|CSQueue
argument_list|,
name|CapacitySchedulerConfiguration
argument_list|>
DECL|method|getQueueStateManager ()
name|getQueueStateManager
parameter_list|()
block|{
return|return
name|this
operator|.
name|queueStateManager
return|;
block|}
block|}
end_class

end_unit

