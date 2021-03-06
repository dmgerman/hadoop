begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|monitor
operator|.
name|SchedulingEditPolicy
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
name|event
operator|.
name|QueueManagementChangeEvent
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
name|Clock
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
name|SystemClock
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
name|java
operator|.
name|text
operator|.
name|MessageFormat
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

begin_comment
comment|/**  * Queue Management scheduling policy for managed parent queues which enable  * auto child queue creation  */
end_comment

begin_class
DECL|class|QueueManagementDynamicEditPolicy
specifier|public
class|class
name|QueueManagementDynamicEditPolicy
implements|implements
name|SchedulingEditPolicy
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|QueueManagementDynamicEditPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|clock
specifier|private
name|Clock
name|clock
decl_stmt|;
comment|// Pointer to other RM components
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|rc
specifier|private
name|ResourceCalculator
name|rc
decl_stmt|;
DECL|field|scheduler
specifier|private
name|CapacityScheduler
name|scheduler
decl_stmt|;
DECL|field|nlm
specifier|private
name|RMNodeLabelsManager
name|nlm
decl_stmt|;
DECL|field|monitoringInterval
specifier|private
name|long
name|monitoringInterval
decl_stmt|;
DECL|field|managedParentQueues
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|managedParentQueues
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Instantiated by CapacitySchedulerConfiguration    */
DECL|method|QueueManagementDynamicEditPolicy ()
specifier|public
name|QueueManagementDynamicEditPolicy
parameter_list|()
block|{
name|clock
operator|=
name|SystemClock
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|VisibleForTesting
DECL|method|QueueManagementDynamicEditPolicy (RMContext context, CapacityScheduler scheduler)
specifier|public
name|QueueManagementDynamicEditPolicy
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|CapacityScheduler
name|scheduler
parameter_list|)
block|{
name|init
argument_list|(
name|context
operator|.
name|getYarnConfiguration
argument_list|()
argument_list|,
name|context
argument_list|,
name|scheduler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|VisibleForTesting
DECL|method|QueueManagementDynamicEditPolicy (RMContext context, CapacityScheduler scheduler, Clock clock)
specifier|public
name|QueueManagementDynamicEditPolicy
parameter_list|(
name|RMContext
name|context
parameter_list|,
name|CapacityScheduler
name|scheduler
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|init
argument_list|(
name|context
operator|.
name|getYarnConfiguration
argument_list|()
argument_list|,
name|context
argument_list|,
name|scheduler
argument_list|)
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (final Configuration config, final RMContext context, final ResourceScheduler sched)
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|,
specifier|final
name|RMContext
name|context
parameter_list|,
specifier|final
name|ResourceScheduler
name|sched
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Queue Management Policy monitor: {}"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
literal|null
operator|==
name|scheduler
operator|:
literal|"Unexpected duplicate call to init"
assert|;
if|if
condition|(
operator|!
operator|(
name|sched
operator|instanceof
name|CapacityScheduler
operator|)
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Class "
operator|+
name|sched
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" not instance of "
operator|+
name|CapacityScheduler
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
name|rmContext
operator|=
name|context
expr_stmt|;
name|scheduler
operator|=
operator|(
name|CapacityScheduler
operator|)
name|sched
expr_stmt|;
name|clock
operator|=
name|scheduler
operator|.
name|getClock
argument_list|()
expr_stmt|;
name|rc
operator|=
name|scheduler
operator|.
name|getResourceCalculator
argument_list|()
expr_stmt|;
name|nlm
operator|=
name|scheduler
operator|.
name|getRMContext
argument_list|()
operator|.
name|getNodeLabelManager
argument_list|()
expr_stmt|;
name|CapacitySchedulerConfiguration
name|csConfig
init|=
name|scheduler
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|monitoringInterval
operator|=
name|csConfig
operator|.
name|getLong
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|QUEUE_MANAGEMENT_MONITORING_INTERVAL
argument_list|,
name|CapacitySchedulerConfiguration
operator|.
name|DEFAULT_QUEUE_MANAGEMENT_MONITORING_INTERVAL
argument_list|)
expr_stmt|;
name|initQueues
argument_list|()
expr_stmt|;
block|}
comment|/**    * Reinitializes queues(Called on scheduler.reinitialize)    * @param config Configuration    * @param context The resourceManager's context    * @param sched The scheduler    */
DECL|method|reinitialize (final Configuration config, final RMContext context, final ResourceScheduler sched)
specifier|public
name|void
name|reinitialize
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|,
specifier|final
name|RMContext
name|context
parameter_list|,
specifier|final
name|ResourceScheduler
name|sched
parameter_list|)
block|{
comment|//TODO - Wire with scheduler reinitialize and remove initQueues below?
name|initQueues
argument_list|()
expr_stmt|;
block|}
DECL|method|initQueues ()
specifier|private
name|void
name|initQueues
parameter_list|()
block|{
name|managedParentQueues
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|queues
range|:
name|scheduler
operator|.
name|getCapacitySchedulerQueueManager
argument_list|()
operator|.
name|getQueues
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|queueName
init|=
name|queues
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|CSQueue
name|queue
init|=
name|queues
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|queue
operator|instanceof
name|ManagedParentQueue
condition|)
block|{
name|managedParentQueues
operator|.
name|add
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|editSchedule ()
specifier|public
name|void
name|editSchedule
parameter_list|()
block|{
name|long
name|startTs
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|initQueues
argument_list|()
expr_stmt|;
name|manageAutoCreatedLeafQueues
argument_list|()
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
literal|"Total time used="
operator|+
operator|(
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|startTs
operator|)
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|manageAutoCreatedLeafQueues ()
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|manageAutoCreatedLeafQueues
parameter_list|()
block|{
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|queueManagementChanges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// All partitions to look at
comment|//Proceed only if there are queues to process
if|if
condition|(
name|managedParentQueues
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|parentQueueName
range|:
name|managedParentQueues
control|)
block|{
name|ManagedParentQueue
name|parentQueue
init|=
operator|(
name|ManagedParentQueue
operator|)
name|scheduler
operator|.
name|getCapacitySchedulerQueueManager
argument_list|()
operator|.
name|getQueue
argument_list|(
name|parentQueueName
argument_list|)
decl_stmt|;
name|queueManagementChanges
operator|.
name|addAll
argument_list|(
name|computeQueueManagementChanges
argument_list|(
name|parentQueue
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queueManagementChanges
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|computeQueueManagementChanges (ManagedParentQueue parentQueue)
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|computeQueueManagementChanges
parameter_list|(
name|ManagedParentQueue
name|parentQueue
parameter_list|)
block|{
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|queueManagementChanges
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|parentQueue
operator|.
name|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
argument_list|()
condition|)
block|{
name|AutoCreatedQueueManagementPolicy
name|policyClazz
init|=
name|parentQueue
operator|.
name|getAutoCreatedQueueManagementPolicy
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
literal|0
decl_stmt|;
try|try
block|{
name|startTime
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|queueManagementChanges
operator|=
name|policyClazz
operator|.
name|computeQueueManagementChanges
argument_list|()
expr_stmt|;
comment|//Scheduler update is asynchronous
if|if
condition|(
name|queueManagementChanges
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|QueueManagementChangeEvent
name|queueManagementChangeEvent
init|=
operator|new
name|QueueManagementChangeEvent
argument_list|(
name|parentQueue
argument_list|,
name|queueManagementChanges
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|getRMContext
argument_list|()
operator|.
name|getDispatcher
argument_list|()
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|queueManagementChangeEvent
argument_list|)
expr_stmt|;
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
literal|"{} uses {} millisecond"
operator|+
literal|" to run"
argument_list|,
name|policyClazz
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|queueManagementChanges
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|" Updated queue management changes for parent queue"
operator|+
literal|" "
operator|+
literal|"{}: [{}]"
argument_list|,
name|parentQueue
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|queueManagementChanges
operator|.
name|size
argument_list|()
operator|<
literal|25
condition|?
name|queueManagementChanges
operator|.
name|toString
argument_list|()
else|:
name|queueManagementChanges
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not compute child queue management updates for parent "
operator|+
literal|"queue "
operator|+
name|parentQueue
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping queue management updates for parent queue {} "
operator|+
literal|"since configuration for auto creating queues beyond "
operator|+
literal|"parent's guaranteed capacity is disabled"
argument_list|,
name|parentQueue
operator|.
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|queueManagementChanges
return|;
block|}
annotation|@
name|Override
DECL|method|getMonitoringInterval ()
specifier|public
name|long
name|getMonitoringInterval
parameter_list|()
block|{
return|return
name|monitoringInterval
return|;
block|}
annotation|@
name|Override
DECL|method|getPolicyName ()
specifier|public
name|String
name|getPolicyName
parameter_list|()
block|{
return|return
literal|"QueueManagementDynamicEditPolicy"
return|;
block|}
DECL|method|getResourceCalculator ()
specifier|public
name|ResourceCalculator
name|getResourceCalculator
parameter_list|()
block|{
return|return
name|rc
return|;
block|}
DECL|method|getRmContext ()
specifier|public
name|RMContext
name|getRmContext
parameter_list|()
block|{
return|return
name|rmContext
return|;
block|}
DECL|method|getRC ()
specifier|public
name|ResourceCalculator
name|getRC
parameter_list|()
block|{
return|return
name|rc
return|;
block|}
DECL|method|getScheduler ()
specifier|public
name|CapacityScheduler
name|getScheduler
parameter_list|()
block|{
return|return
name|scheduler
return|;
block|}
DECL|method|getManagedParentQueues ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getManagedParentQueues
parameter_list|()
block|{
return|return
name|managedParentQueues
return|;
block|}
block|}
end_class

end_unit

