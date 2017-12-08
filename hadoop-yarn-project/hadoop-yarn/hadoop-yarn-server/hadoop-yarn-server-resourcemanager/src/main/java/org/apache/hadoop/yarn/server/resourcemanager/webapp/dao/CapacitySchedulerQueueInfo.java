begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
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
name|Collections
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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlSeeAlso
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlTransient
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
name|capacity
operator|.
name|CSQueue
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
name|PlanQueue
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
name|QueueCapacities
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlSeeAlso
argument_list|(
block|{
name|CapacitySchedulerLeafQueueInfo
operator|.
name|class
block|}
argument_list|)
DECL|class|CapacitySchedulerQueueInfo
specifier|public
class|class
name|CapacitySchedulerQueueInfo
block|{
annotation|@
name|XmlTransient
DECL|field|EPSILON
specifier|static
specifier|final
name|float
name|EPSILON
init|=
literal|1e-8f
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|queuePath
specifier|protected
name|String
name|queuePath
decl_stmt|;
DECL|field|capacity
specifier|protected
name|float
name|capacity
decl_stmt|;
DECL|field|usedCapacity
specifier|protected
name|float
name|usedCapacity
decl_stmt|;
DECL|field|maxCapacity
specifier|protected
name|float
name|maxCapacity
decl_stmt|;
DECL|field|absoluteCapacity
specifier|protected
name|float
name|absoluteCapacity
decl_stmt|;
DECL|field|absoluteMaxCapacity
specifier|protected
name|float
name|absoluteMaxCapacity
decl_stmt|;
DECL|field|absoluteUsedCapacity
specifier|protected
name|float
name|absoluteUsedCapacity
decl_stmt|;
DECL|field|numApplications
specifier|protected
name|int
name|numApplications
decl_stmt|;
DECL|field|queueName
specifier|protected
name|String
name|queueName
decl_stmt|;
DECL|field|state
specifier|protected
name|QueueState
name|state
decl_stmt|;
DECL|field|queues
specifier|protected
name|CapacitySchedulerQueueInfoList
name|queues
decl_stmt|;
DECL|field|resourcesUsed
specifier|protected
name|ResourceInfo
name|resourcesUsed
decl_stmt|;
DECL|field|hideReservationQueues
specifier|private
name|boolean
name|hideReservationQueues
init|=
literal|false
decl_stmt|;
DECL|field|nodeLabels
specifier|protected
name|ArrayList
argument_list|<
name|String
argument_list|>
name|nodeLabels
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allocatedContainers
specifier|protected
name|long
name|allocatedContainers
decl_stmt|;
DECL|field|reservedContainers
specifier|protected
name|long
name|reservedContainers
decl_stmt|;
DECL|field|pendingContainers
specifier|protected
name|long
name|pendingContainers
decl_stmt|;
DECL|field|capacities
specifier|protected
name|QueueCapacitiesInfo
name|capacities
decl_stmt|;
DECL|field|resources
specifier|protected
name|ResourcesInfo
name|resources
decl_stmt|;
DECL|field|minEffectiveCapacity
specifier|protected
name|ResourceInfo
name|minEffectiveCapacity
decl_stmt|;
DECL|field|maxEffectiveCapacity
specifier|protected
name|ResourceInfo
name|maxEffectiveCapacity
decl_stmt|;
DECL|method|CapacitySchedulerQueueInfo ()
name|CapacitySchedulerQueueInfo
parameter_list|()
block|{   }
empty_stmt|;
DECL|method|CapacitySchedulerQueueInfo (CSQueue q)
name|CapacitySchedulerQueueInfo
parameter_list|(
name|CSQueue
name|q
parameter_list|)
block|{
name|queuePath
operator|=
name|q
operator|.
name|getQueuePath
argument_list|()
expr_stmt|;
name|capacity
operator|=
name|q
operator|.
name|getCapacity
argument_list|()
operator|*
literal|100
expr_stmt|;
name|usedCapacity
operator|=
name|q
operator|.
name|getUsedCapacity
argument_list|()
operator|*
literal|100
expr_stmt|;
name|maxCapacity
operator|=
name|q
operator|.
name|getMaximumCapacity
argument_list|()
expr_stmt|;
if|if
condition|(
name|maxCapacity
argument_list|<
name|EPSILON
operator|||
name|maxCapacity
argument_list|>
literal|1f
condition|)
name|maxCapacity
operator|=
literal|1f
expr_stmt|;
name|maxCapacity
operator|*=
literal|100
expr_stmt|;
name|absoluteCapacity
operator|=
name|cap
argument_list|(
name|q
operator|.
name|getAbsoluteCapacity
argument_list|()
argument_list|,
literal|0f
argument_list|,
literal|1f
argument_list|)
operator|*
literal|100
expr_stmt|;
name|absoluteMaxCapacity
operator|=
name|cap
argument_list|(
name|q
operator|.
name|getAbsoluteMaximumCapacity
argument_list|()
argument_list|,
literal|0f
argument_list|,
literal|1f
argument_list|)
operator|*
literal|100
expr_stmt|;
name|absoluteUsedCapacity
operator|=
name|cap
argument_list|(
name|q
operator|.
name|getAbsoluteUsedCapacity
argument_list|()
argument_list|,
literal|0f
argument_list|,
literal|1f
argument_list|)
operator|*
literal|100
expr_stmt|;
name|numApplications
operator|=
name|q
operator|.
name|getNumApplications
argument_list|()
expr_stmt|;
name|allocatedContainers
operator|=
name|q
operator|.
name|getMetrics
argument_list|()
operator|.
name|getAllocatedContainers
argument_list|()
expr_stmt|;
name|pendingContainers
operator|=
name|q
operator|.
name|getMetrics
argument_list|()
operator|.
name|getPendingContainers
argument_list|()
expr_stmt|;
name|reservedContainers
operator|=
name|q
operator|.
name|getMetrics
argument_list|()
operator|.
name|getReservedContainers
argument_list|()
expr_stmt|;
name|queueName
operator|=
name|q
operator|.
name|getQueueName
argument_list|()
expr_stmt|;
name|state
operator|=
name|q
operator|.
name|getState
argument_list|()
expr_stmt|;
name|resourcesUsed
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|q
operator|.
name|getUsedResources
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|q
operator|instanceof
name|PlanQueue
operator|&&
operator|!
operator|(
operator|(
name|PlanQueue
operator|)
name|q
operator|)
operator|.
name|showReservationsAsQueues
argument_list|()
condition|)
block|{
name|hideReservationQueues
operator|=
literal|true
expr_stmt|;
block|}
comment|// add labels
name|Set
argument_list|<
name|String
argument_list|>
name|labelSet
init|=
name|q
operator|.
name|getAccessibleNodeLabels
argument_list|()
decl_stmt|;
if|if
condition|(
name|labelSet
operator|!=
literal|null
condition|)
block|{
name|nodeLabels
operator|.
name|addAll
argument_list|(
name|labelSet
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|nodeLabels
argument_list|)
expr_stmt|;
block|}
name|QueueCapacities
name|qCapacities
init|=
name|q
operator|.
name|getQueueCapacities
argument_list|()
decl_stmt|;
name|QueueResourceQuotas
name|qResQuotas
init|=
name|q
operator|.
name|getQueueResourceQuotas
argument_list|()
decl_stmt|;
name|populateQueueCapacities
argument_list|(
name|qCapacities
argument_list|,
name|qResQuotas
argument_list|)
expr_stmt|;
name|ResourceUsage
name|queueResourceUsage
init|=
name|q
operator|.
name|getQueueResourceUsage
argument_list|()
decl_stmt|;
name|populateQueueResourceUsage
argument_list|(
name|queueResourceUsage
argument_list|)
expr_stmt|;
name|minEffectiveCapacity
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|q
operator|.
name|getQueueResourceQuotas
argument_list|()
operator|.
name|getEffectiveMinResource
argument_list|()
argument_list|)
expr_stmt|;
name|maxEffectiveCapacity
operator|=
operator|new
name|ResourceInfo
argument_list|(
name|q
operator|.
name|getQueueResourceQuotas
argument_list|()
operator|.
name|getEffectiveMaxResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|populateQueueResourceUsage (ResourceUsage queueResourceUsage)
specifier|protected
name|void
name|populateQueueResourceUsage
parameter_list|(
name|ResourceUsage
name|queueResourceUsage
parameter_list|)
block|{
name|resources
operator|=
operator|new
name|ResourcesInfo
argument_list|(
name|queueResourceUsage
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|populateQueueCapacities (QueueCapacities qCapacities, QueueResourceQuotas qResQuotas)
specifier|protected
name|void
name|populateQueueCapacities
parameter_list|(
name|QueueCapacities
name|qCapacities
parameter_list|,
name|QueueResourceQuotas
name|qResQuotas
parameter_list|)
block|{
name|capacities
operator|=
operator|new
name|QueueCapacitiesInfo
argument_list|(
name|qCapacities
argument_list|,
name|qResQuotas
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getCapacity ()
specifier|public
name|float
name|getCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|capacity
return|;
block|}
DECL|method|getUsedCapacity ()
specifier|public
name|float
name|getUsedCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|usedCapacity
return|;
block|}
DECL|method|getMaxCapacity ()
specifier|public
name|float
name|getMaxCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxCapacity
return|;
block|}
DECL|method|getAbsoluteCapacity ()
specifier|public
name|float
name|getAbsoluteCapacity
parameter_list|()
block|{
return|return
name|absoluteCapacity
return|;
block|}
DECL|method|getAbsoluteMaxCapacity ()
specifier|public
name|float
name|getAbsoluteMaxCapacity
parameter_list|()
block|{
return|return
name|absoluteMaxCapacity
return|;
block|}
DECL|method|getAbsoluteUsedCapacity ()
specifier|public
name|float
name|getAbsoluteUsedCapacity
parameter_list|()
block|{
return|return
name|absoluteUsedCapacity
return|;
block|}
DECL|method|getNumApplications ()
specifier|public
name|int
name|getNumApplications
parameter_list|()
block|{
return|return
name|numApplications
return|;
block|}
DECL|method|getAllocatedContainers ()
specifier|public
name|long
name|getAllocatedContainers
parameter_list|()
block|{
return|return
name|allocatedContainers
return|;
block|}
DECL|method|getReservedContainers ()
specifier|public
name|long
name|getReservedContainers
parameter_list|()
block|{
return|return
name|reservedContainers
return|;
block|}
DECL|method|getPendingContainers ()
specifier|public
name|long
name|getPendingContainers
parameter_list|()
block|{
return|return
name|pendingContainers
return|;
block|}
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|this
operator|.
name|queueName
return|;
block|}
DECL|method|getQueueState ()
specifier|public
name|String
name|getQueueState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getQueuePath ()
specifier|public
name|String
name|getQueuePath
parameter_list|()
block|{
return|return
name|this
operator|.
name|queuePath
return|;
block|}
DECL|method|getQueues ()
specifier|public
name|CapacitySchedulerQueueInfoList
name|getQueues
parameter_list|()
block|{
if|if
condition|(
name|hideReservationQueues
condition|)
block|{
return|return
operator|new
name|CapacitySchedulerQueueInfoList
argument_list|()
return|;
block|}
return|return
name|this
operator|.
name|queues
return|;
block|}
DECL|method|getResourcesUsed ()
specifier|public
name|ResourceInfo
name|getResourcesUsed
parameter_list|()
block|{
return|return
name|resourcesUsed
return|;
block|}
comment|/**    * Limit a value to a specified range.    * @param val the value to be capped    * @param low the lower bound of the range (inclusive)    * @param hi the upper bound of the range (inclusive)    * @return the capped value    */
DECL|method|cap (float val, float low, float hi)
specifier|static
name|float
name|cap
parameter_list|(
name|float
name|val
parameter_list|,
name|float
name|low
parameter_list|,
name|float
name|hi
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|val
argument_list|,
name|low
argument_list|)
argument_list|,
name|hi
argument_list|)
return|;
block|}
DECL|method|getNodeLabels ()
specifier|public
name|ArrayList
argument_list|<
name|String
argument_list|>
name|getNodeLabels
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeLabels
return|;
block|}
DECL|method|getCapacities ()
specifier|public
name|QueueCapacitiesInfo
name|getCapacities
parameter_list|()
block|{
return|return
name|capacities
return|;
block|}
DECL|method|getResources ()
specifier|public
name|ResourcesInfo
name|getResources
parameter_list|()
block|{
return|return
name|resources
return|;
block|}
DECL|method|getMinEffectiveCapacity ()
specifier|public
name|ResourceInfo
name|getMinEffectiveCapacity
parameter_list|()
block|{
return|return
name|minEffectiveCapacity
return|;
block|}
DECL|method|getMaxEffectiveCapacity ()
specifier|public
name|ResourceInfo
name|getMaxEffectiveCapacity
parameter_list|()
block|{
return|return
name|maxEffectiveCapacity
return|;
block|}
DECL|method|isLeafQueue ()
specifier|public
name|boolean
name|isLeafQueue
parameter_list|()
block|{
return|return
name|getQueues
argument_list|()
operator|==
literal|null
return|;
block|}
block|}
end_class

end_unit

