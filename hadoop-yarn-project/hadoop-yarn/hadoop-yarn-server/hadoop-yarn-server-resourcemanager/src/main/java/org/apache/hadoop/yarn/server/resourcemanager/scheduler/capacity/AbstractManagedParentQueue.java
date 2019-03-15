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
name|QueueEntitlement
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
name|Comparator
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * A container class for automatically created child leaf queues.  * From the user perspective this is equivalent to a LeafQueue,  * but functionality wise is a sub-class of ParentQueue  */
end_comment

begin_class
DECL|class|AbstractManagedParentQueue
specifier|public
specifier|abstract
class|class
name|AbstractManagedParentQueue
extends|extends
name|ParentQueue
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
name|AbstractManagedParentQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|leafQueueTemplate
specifier|protected
name|AutoCreatedLeafQueueConfig
name|leafQueueTemplate
decl_stmt|;
DECL|field|queueManagementPolicy
specifier|protected
name|AutoCreatedQueueManagementPolicy
name|queueManagementPolicy
init|=
literal|null
decl_stmt|;
DECL|method|AbstractManagedParentQueue (CapacitySchedulerContext cs, String queueName, CSQueue parent, CSQueue old)
specifier|public
name|AbstractManagedParentQueue
parameter_list|(
name|CapacitySchedulerContext
name|cs
parameter_list|,
name|String
name|queueName
parameter_list|,
name|CSQueue
name|parent
parameter_list|,
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
comment|// Set new configs
name|setupQueueConfigs
argument_list|(
name|clusterResource
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
comment|/**    * Add the specified child queue.    * @param childQueue reference to the child queue to be added    * @throws SchedulerDynamicEditException    */
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
operator|.
name|getCapacity
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Queue "
operator|+
name|childQueue
operator|+
literal|" being added has non zero capacity."
argument_list|)
throw|;
block|}
name|boolean
name|added
init|=
name|this
operator|.
name|childQueues
operator|.
name|add
argument_list|(
name|childQueue
argument_list|)
decl_stmt|;
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
literal|"updateChildQueues (action: add queue): "
operator|+
name|added
operator|+
literal|" "
operator|+
name|getChildQueuesToPrint
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Remove the specified child queue.    * @param childQueue reference to the child queue to be removed    * @throws SchedulerDynamicEditException    */
DECL|method|removeChildQueue (CSQueue childQueue)
specifier|public
name|void
name|removeChildQueue
parameter_list|(
name|CSQueue
name|childQueue
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
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
operator|.
name|getCapacity
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Queue "
operator|+
name|childQueue
operator|+
literal|" being removed has non zero capacity."
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|CSQueue
argument_list|>
name|qiter
init|=
name|childQueues
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|qiter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|CSQueue
name|cs
init|=
name|qiter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|cs
operator|.
name|equals
argument_list|(
name|childQueue
argument_list|)
condition|)
block|{
name|qiter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removed child queue: {}"
argument_list|,
name|cs
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|/**    * Remove the specified child queue.    * @param childQueueName name of the child queue to be removed    * @throws SchedulerDynamicEditException    */
DECL|method|removeChildQueue (String childQueueName)
specifier|public
name|CSQueue
name|removeChildQueue
parameter_list|(
name|String
name|childQueueName
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
block|{
name|CSQueue
name|childQueue
decl_stmt|;
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|childQueue
operator|=
name|this
operator|.
name|csContext
operator|.
name|getCapacitySchedulerQueueManager
argument_list|()
operator|.
name|getQueue
argument_list|(
name|childQueueName
argument_list|)
expr_stmt|;
if|if
condition|(
name|childQueue
operator|!=
literal|null
condition|)
block|{
name|removeChildQueue
argument_list|(
name|childQueue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Cannot find queue to delete "
operator|+
literal|": "
operator|+
name|childQueueName
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|childQueue
return|;
block|}
DECL|method|sumOfChildCapacities ()
specifier|protected
name|float
name|sumOfChildCapacities
parameter_list|()
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|float
name|ret
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CSQueue
name|l
range|:
name|childQueues
control|)
block|{
name|ret
operator|+=
name|l
operator|.
name|getCapacity
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
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
DECL|method|sumOfChildAbsCapacities ()
specifier|protected
name|float
name|sumOfChildAbsCapacities
parameter_list|()
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|float
name|ret
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CSQueue
name|l
range|:
name|childQueues
control|)
block|{
name|ret
operator|+=
name|l
operator|.
name|getAbsoluteCapacity
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
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
DECL|method|getLeafQueueTemplate ()
specifier|public
name|AutoCreatedLeafQueueConfig
name|getLeafQueueTemplate
parameter_list|()
block|{
return|return
name|leafQueueTemplate
return|;
block|}
specifier|public
name|AutoCreatedQueueManagementPolicy
DECL|method|getAutoCreatedQueueManagementPolicy ()
name|getAutoCreatedQueueManagementPolicy
parameter_list|()
block|{
return|return
name|queueManagementPolicy
return|;
block|}
DECL|method|getConfigurationsWithPrefix (SortedMap<String, String> sortedConfigs, String prefix)
specifier|protected
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getConfigurationsWithPrefix
parameter_list|(
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sortedConfigs
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
return|return
name|sortedConfigs
operator|.
name|subMap
argument_list|(
name|prefix
argument_list|,
name|prefix
operator|+
name|Character
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
DECL|method|sortCSConfigurations ()
specifier|protected
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sortCSConfigurations
parameter_list|()
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sortedConfigs
init|=
operator|new
name|TreeMap
argument_list|(
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
return|return
name|s1
operator|.
name|compareToIgnoreCase
argument_list|(
name|s2
argument_list|)
return|;
block|}
block|}
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
name|csContext
operator|.
name|getConfiguration
argument_list|()
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
specifier|final
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
name|sortedConfigs
operator|.
name|put
argument_list|(
name|confKeyValuePair
operator|.
name|getKey
argument_list|()
argument_list|,
name|confKeyValuePair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sortedConfigs
return|;
block|}
DECL|method|initializeLeafQueueConfigs (String configPrefix)
specifier|protected
name|CapacitySchedulerConfiguration
name|initializeLeafQueueConfigs
parameter_list|(
name|String
name|configPrefix
parameter_list|)
block|{
name|CapacitySchedulerConfiguration
name|leafQueueConfigs
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
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sortedConfigs
init|=
name|sortCSConfigurations
argument_list|()
decl_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|templateConfigs
init|=
name|getConfigurationsWithPrefix
argument_list|(
name|sortedConfigs
argument_list|,
name|configPrefix
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
name|templateConfigs
operator|.
name|entrySet
argument_list|()
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
name|leafQueueConfigs
operator|.
name|set
argument_list|(
name|confKeyValuePair
operator|.
name|getKey
argument_list|()
argument_list|,
name|confKeyValuePair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|leafQueueConfigs
return|;
block|}
DECL|method|validateQueueEntitlementChange (AbstractAutoCreatedLeafQueue leafQueue, QueueEntitlement entitlement)
specifier|protected
name|void
name|validateQueueEntitlementChange
parameter_list|(
name|AbstractAutoCreatedLeafQueue
name|leafQueue
parameter_list|,
name|QueueEntitlement
name|entitlement
parameter_list|)
throws|throws
name|SchedulerDynamicEditException
block|{
name|float
name|sumChilds
init|=
name|sumOfChildCapacities
argument_list|()
decl_stmt|;
name|float
name|newChildCap
init|=
name|sumChilds
operator|-
name|leafQueue
operator|.
name|getCapacity
argument_list|()
operator|+
name|entitlement
operator|.
name|getCapacity
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|newChildCap
operator|>=
literal|0
operator|&&
name|newChildCap
operator|<
literal|1.0f
operator|+
name|CSQueueUtils
operator|.
name|EPSILON
operator|)
condition|)
block|{
throw|throw
operator|new
name|SchedulerDynamicEditException
argument_list|(
literal|"Sum of child queues should exceed 100% for auto creating parent "
operator|+
literal|"queue : "
operator|+
name|queueName
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

