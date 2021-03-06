begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collections
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

begin_comment
comment|/**  * Auto Creation enabled Parent queue. This queue initially does not have any  * children to start with and all child  * leaf queues will be auto created. Currently this does not allow other  * pre-configured leaf or parent queues to  * co-exist along with auto-created leaf queues. The auto creation is limited  * to leaf queues currently.  */
end_comment

begin_class
DECL|class|ManagedParentQueue
specifier|public
class|class
name|ManagedParentQueue
extends|extends
name|AbstractManagedParentQueue
block|{
DECL|field|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
specifier|private
name|boolean
name|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
init|=
literal|false
decl_stmt|;
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
name|ManagedParentQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ManagedParentQueue (final CapacitySchedulerContext cs, final String queueName, final CSQueue parent, final CSQueue old)
specifier|public
name|ManagedParentQueue
parameter_list|(
specifier|final
name|CapacitySchedulerContext
name|cs
parameter_list|,
specifier|final
name|String
name|queueName
parameter_list|,
specifier|final
name|CSQueue
name|parent
parameter_list|,
specifier|final
name|CSQueue
name|old
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|cs
argument_list|,
name|queueName
argument_list|,
name|parent
argument_list|,
name|old
argument_list|)
expr_stmt|;
name|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
operator|=
name|csContext
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getShouldFailAutoQueueCreationWhenGuaranteedCapacityExceeded
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
name|leafQueueTemplate
operator|=
name|initializeLeafQueueConfigs
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created Managed Parent Queue: [{}] with capacity: [{}]"
operator|+
literal|" with max capacity: [{}]"
argument_list|,
name|queueName
argument_list|,
name|super
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|super
operator|.
name|getMaximumCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|initializeQueueManagementPolicy
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|validate
argument_list|(
name|newlyParsedQueue
argument_list|)
expr_stmt|;
name|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
operator|=
name|csContext
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getShouldFailAutoQueueCreationWhenGuaranteedCapacityExceeded
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
comment|//validate if capacity is exceeded for child queues
if|if
condition|(
name|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
condition|)
block|{
name|float
name|childCap
init|=
name|sumOfChildCapacities
argument_list|()
decl_stmt|;
if|if
condition|(
name|getCapacity
argument_list|()
operator|<
name|childCap
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Total of Auto Created leaf queues guaranteed capacity : "
operator|+
name|childCap
operator|+
literal|" exceeds Parent queue's "
operator|+
name|getQueuePath
argument_list|()
operator|+
literal|" guaranteed capacity "
operator|+
name|getCapacity
argument_list|()
operator|+
literal|""
operator|+
literal|".Cannot enforce policy to auto"
operator|+
literal|" create queues beyond parent queue's capacity"
argument_list|)
throw|;
block|}
block|}
name|leafQueueTemplate
operator|=
name|initializeLeafQueueConfigs
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|super
operator|.
name|reinitialize
argument_list|(
name|newlyParsedQueue
argument_list|,
name|clusterResource
argument_list|)
expr_stmt|;
comment|// run reinitialize on each existing queue, to trigger absolute cap
comment|// recomputations
for|for
control|(
name|CSQueue
name|res
range|:
name|this
operator|.
name|getChildQueues
argument_list|()
control|)
block|{
name|res
operator|.
name|reinitialize
argument_list|(
name|res
argument_list|,
name|clusterResource
argument_list|)
expr_stmt|;
block|}
comment|//clear state in policy
name|reinitializeQueueManagementPolicy
argument_list|()
expr_stmt|;
comment|//reassign capacities according to policy
specifier|final
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|queueManagementChanges
init|=
name|queueManagementPolicy
operator|.
name|computeQueueManagementChanges
argument_list|()
decl_stmt|;
name|validateAndApplyQueueManagementChanges
argument_list|(
name|queueManagementChanges
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reinitialized Managed Parent Queue: [{}] with capacity [{}]"
operator|+
literal|" with max capacity [{}]"
argument_list|,
name|queueName
argument_list|,
name|super
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|super
operator|.
name|getMaximumCapacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|ye
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while computing policy changes for leaf queue : "
operator|+
name|getQueueName
argument_list|()
argument_list|,
name|ye
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|ye
argument_list|)
throw|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initializeQueueManagementPolicy ()
specifier|private
name|void
name|initializeQueueManagementPolicy
parameter_list|()
throws|throws
name|IOException
block|{
name|queueManagementPolicy
operator|=
name|csContext
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getAutoCreatedQueueManagementPolicyClass
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
name|queueManagementPolicy
operator|.
name|init
argument_list|(
name|csContext
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|reinitializeQueueManagementPolicy ()
specifier|private
name|void
name|reinitializeQueueManagementPolicy
parameter_list|()
throws|throws
name|IOException
block|{
name|AutoCreatedQueueManagementPolicy
name|managementPolicy
init|=
name|csContext
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getAutoCreatedQueueManagementPolicyClass
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|managementPolicy
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|queueManagementPolicy
operator|.
name|getClass
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|queueManagementPolicy
operator|=
name|managementPolicy
expr_stmt|;
name|queueManagementPolicy
operator|.
name|init
argument_list|(
name|csContext
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queueManagementPolicy
operator|.
name|reinitialize
argument_list|(
name|csContext
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initializeLeafQueueConfigs ()
specifier|protected
name|AutoCreatedLeafQueueConfig
operator|.
name|Builder
name|initializeLeafQueueConfigs
parameter_list|()
block|{
name|AutoCreatedLeafQueueConfig
operator|.
name|Builder
name|builder
init|=
operator|new
name|AutoCreatedLeafQueueConfig
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|String
name|leafQueueTemplateConfPrefix
init|=
name|getLeafQueueConfigPrefix
argument_list|(
name|csContext
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
comment|//Load template configuration
name|builder
operator|.
name|configuration
argument_list|(
name|super
operator|.
name|initializeLeafQueueConfigs
argument_list|(
name|leafQueueTemplateConfPrefix
argument_list|)
argument_list|)
expr_stmt|;
comment|//Load template capacities
name|QueueCapacities
name|queueCapacities
init|=
operator|new
name|QueueCapacities
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|CSQueueUtils
operator|.
name|loadUpdateAndCheckCapacities
argument_list|(
name|csContext
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getAutoCreatedQueueTemplateConfPrefix
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
argument_list|,
name|csContext
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|queueCapacities
argument_list|,
name|getQueueCapacities
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|capacities
argument_list|(
name|queueCapacities
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|validate (final CSQueue newlyParsedQueue)
specifier|protected
name|void
name|validate
parameter_list|(
specifier|final
name|CSQueue
name|newlyParsedQueue
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Sanity check
if|if
condition|(
operator|!
operator|(
name|newlyParsedQueue
operator|instanceof
name|ManagedParentQueue
operator|)
operator|||
operator|!
name|newlyParsedQueue
operator|.
name|getQueuePath
argument_list|()
operator|.
name|equals
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Trying to reinitialize "
operator|+
name|getQueuePath
argument_list|()
operator|+
literal|" from "
operator|+
name|newlyParsedQueue
operator|.
name|getQueuePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|addChildQueue (CSQueue childQueue)
specifier|public
name|void
name|addChildQueue
parameter_list|(
name|CSQueue
name|childQueue
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
throws|,
name|IOException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|childQueue
operator|==
literal|null
operator|||
operator|!
operator|(
name|childQueue
operator|instanceof
name|AutoCreatedLeafQueue
operator|)
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Expected child queue to be an instance of AutoCreatedLeafQueue"
argument_list|)
throw|;
block|}
name|CapacitySchedulerConfiguration
name|conf
init|=
name|csContext
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|ManagedParentQueue
name|parentQueue
init|=
operator|(
name|ManagedParentQueue
operator|)
name|childQueue
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|String
name|leafQueueName
init|=
name|childQueue
operator|.
name|getQueueName
argument_list|()
decl_stmt|;
name|int
name|maxQueues
init|=
name|conf
operator|.
name|getAutoCreatedQueuesMaxChildQueuesLimit
argument_list|(
name|parentQueue
operator|.
name|getQueuePath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentQueue
operator|.
name|getChildQueues
argument_list|()
operator|.
name|size
argument_list|()
operator|>=
name|maxQueues
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Cannot auto create leaf queue "
operator|+
name|leafQueueName
operator|+
literal|".Max Child "
operator|+
literal|"Queue limit exceeded which is configured as : "
operator|+
name|maxQueues
operator|+
literal|" and number of child queues is : "
operator|+
name|parentQueue
operator|.
name|getChildQueues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
condition|)
block|{
if|if
condition|(
name|getLeafQueueTemplate
argument_list|()
operator|.
name|getQueueCapacities
argument_list|()
operator|.
name|getAbsoluteCapacity
argument_list|()
operator|+
name|parentQueue
operator|.
name|sumOfChildAbsCapacities
argument_list|()
operator|>
name|parentQueue
operator|.
name|getAbsoluteCapacity
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Cannot auto create leaf queue "
operator|+
name|leafQueueName
operator|+
literal|". Child "
operator|+
literal|"queues capacities have reached parent queue : "
operator|+
name|parentQueue
operator|.
name|getQueuePath
argument_list|()
operator|+
literal|"'s guaranteed capacity"
argument_list|)
throw|;
block|}
block|}
name|AutoCreatedLeafQueue
name|leafQueue
init|=
operator|(
name|AutoCreatedLeafQueue
operator|)
name|childQueue
decl_stmt|;
name|super
operator|.
name|addChildQueue
argument_list|(
name|leafQueue
argument_list|)
expr_stmt|;
specifier|final
name|AutoCreatedLeafQueueConfig
name|initialLeafQueueTemplate
init|=
name|queueManagementPolicy
operator|.
name|getInitialLeafQueueConfiguration
argument_list|(
name|leafQueue
argument_list|)
decl_stmt|;
name|leafQueue
operator|.
name|reinitializeFromTemplate
argument_list|(
name|initialLeafQueueTemplate
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getScheduleableApplications ()
specifier|public
name|List
argument_list|<
name|FiCaSchedulerApp
argument_list|>
name|getScheduleableApplications
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|FiCaSchedulerApp
argument_list|>
name|apps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CSQueue
name|childQueue
range|:
name|getChildQueues
argument_list|()
control|)
block|{
name|apps
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|LeafQueue
operator|)
name|childQueue
operator|)
operator|.
name|getApplications
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|apps
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getPendingApplications ()
specifier|public
name|List
argument_list|<
name|FiCaSchedulerApp
argument_list|>
name|getPendingApplications
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|FiCaSchedulerApp
argument_list|>
name|apps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CSQueue
name|childQueue
range|:
name|getChildQueues
argument_list|()
control|)
block|{
name|apps
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|LeafQueue
operator|)
name|childQueue
operator|)
operator|.
name|getPendingApplications
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|apps
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getAllApplications ()
specifier|public
name|List
argument_list|<
name|FiCaSchedulerApp
argument_list|>
name|getAllApplications
parameter_list|()
block|{
name|readLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|FiCaSchedulerApp
argument_list|>
name|apps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CSQueue
name|childQueue
range|:
name|getChildQueues
argument_list|()
control|)
block|{
name|apps
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|LeafQueue
operator|)
name|childQueue
operator|)
operator|.
name|getAllApplications
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|apps
argument_list|)
return|;
block|}
finally|finally
block|{
name|readLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getLeafQueueConfigPrefix (CapacitySchedulerConfiguration conf)
specifier|public
name|String
name|getLeafQueueConfigPrefix
parameter_list|(
name|CapacitySchedulerConfiguration
name|conf
parameter_list|)
block|{
return|return
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
operator|+
name|conf
operator|.
name|getAutoCreatedQueueTemplateConfPrefix
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|shouldFailAutoCreationWhenGuaranteedCapacityExceeded ()
specifier|public
name|boolean
name|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
parameter_list|()
block|{
return|return
name|shouldFailAutoCreationWhenGuaranteedCapacityExceeded
return|;
block|}
comment|/**    * Asynchronously called from scheduler to apply queue management changes    *    * @param queueManagementChanges    */
DECL|method|validateAndApplyQueueManagementChanges ( List<QueueManagementChange> queueManagementChanges)
specifier|public
name|void
name|validateAndApplyQueueManagementChanges
parameter_list|(
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|queueManagementChanges
parameter_list|)
throws|throws
name|IOException
throws|,
name|SchedulerDynamicEditException
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|validateQueueManagementChanges
argument_list|(
name|queueManagementChanges
argument_list|)
expr_stmt|;
name|applyQueueManagementChanges
argument_list|(
name|queueManagementChanges
argument_list|)
expr_stmt|;
name|AutoCreatedQueueManagementPolicy
name|policy
init|=
name|getAutoCreatedQueueManagementPolicy
argument_list|()
decl_stmt|;
comment|//acquires write lock on policy
name|policy
operator|.
name|commitQueueManagementChanges
argument_list|(
name|queueManagementChanges
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|validateQueueManagementChanges ( List<QueueManagementChange> queueManagementChanges)
specifier|public
name|void
name|validateQueueManagementChanges
parameter_list|(
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|queueManagementChanges
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
block|{
for|for
control|(
name|QueueManagementChange
name|queueManagementChange
range|:
name|queueManagementChanges
control|)
block|{
name|CSQueue
name|childQueue
init|=
name|queueManagementChange
operator|.
name|getQueue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|childQueue
operator|instanceof
name|AutoCreatedLeafQueue
operator|)
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"queue should be "
operator|+
literal|"AutoCreatedLeafQueue. Found "
operator|+
name|childQueue
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|AbstractManagedParentQueue
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|childQueue
operator|.
name|getParent
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Queue "
operator|+
name|getQueueName
argument_list|()
operator|+
literal|" is not an instance of PlanQueue or ManagedParentQueue."
operator|+
literal|" "
operator|+
literal|"Ignoring update "
operator|+
name|queueManagementChanges
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Queue "
operator|+
name|getQueueName
argument_list|()
operator|+
literal|" is not a AutoEnabledParentQueue."
operator|+
literal|" Ignoring update "
operator|+
name|queueManagementChanges
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|queueManagementChange
operator|.
name|getQueueAction
argument_list|()
condition|)
block|{
case|case
name|UPDATE_QUEUE
case|:
name|AutoCreatedLeafQueueConfig
name|template
init|=
name|queueManagementChange
operator|.
name|getUpdatedQueueTemplate
argument_list|()
decl_stmt|;
operator|(
operator|(
name|AutoCreatedLeafQueue
operator|)
name|childQueue
operator|)
operator|.
name|validateConfigurations
argument_list|(
name|template
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|applyQueueManagementChanges ( List<QueueManagementChange> queueManagementChanges)
specifier|private
name|void
name|applyQueueManagementChanges
parameter_list|(
name|List
argument_list|<
name|QueueManagementChange
argument_list|>
name|queueManagementChanges
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
throws|,
name|IOException
block|{
for|for
control|(
name|QueueManagementChange
name|queueManagementChange
range|:
name|queueManagementChanges
control|)
block|{
switch|switch
condition|(
name|queueManagementChange
operator|.
name|getQueueAction
argument_list|()
condition|)
block|{
case|case
name|UPDATE_QUEUE
case|:
name|AutoCreatedLeafQueue
name|childQueueToBeUpdated
init|=
operator|(
name|AutoCreatedLeafQueue
operator|)
name|queueManagementChange
operator|.
name|getQueue
argument_list|()
decl_stmt|;
comment|//acquires write lock on leaf queue
name|childQueueToBeUpdated
operator|.
name|reinitializeFromTemplate
argument_list|(
name|queueManagementChange
operator|.
name|getUpdatedQueueTemplate
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|getLeafQueueConfigs ( String leafQueueName)
specifier|public
name|CapacitySchedulerConfiguration
name|getLeafQueueConfigs
parameter_list|(
name|String
name|leafQueueName
parameter_list|)
block|{
return|return
name|getLeafQueueConfigs
argument_list|(
name|getLeafQueueTemplate
argument_list|()
operator|.
name|getLeafQueueConfigs
argument_list|()
argument_list|,
name|leafQueueName
argument_list|)
return|;
block|}
DECL|method|getLeafQueueConfigs ( CapacitySchedulerConfiguration templateConfig, String leafQueueName)
specifier|public
name|CapacitySchedulerConfiguration
name|getLeafQueueConfigs
parameter_list|(
name|CapacitySchedulerConfiguration
name|templateConfig
parameter_list|,
name|String
name|leafQueueName
parameter_list|)
block|{
name|CapacitySchedulerConfiguration
name|leafQueueConfigTemplate
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|(
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|iterator
init|=
name|templateConfig
operator|.
name|iterator
argument_list|()
init|;
name|iterator
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
name|String
argument_list|>
name|confKeyValuePair
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|confKeyValuePair
operator|.
name|getKey
argument_list|()
operator|.
name|replaceFirst
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|AUTO_CREATED_LEAF_QUEUE_TEMPLATE_PREFIX
argument_list|,
name|leafQueueName
argument_list|)
decl_stmt|;
name|leafQueueConfigTemplate
operator|.
name|set
argument_list|(
name|name
argument_list|,
name|confKeyValuePair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|leafQueueConfigTemplate
return|;
block|}
block|}
end_class

end_unit

