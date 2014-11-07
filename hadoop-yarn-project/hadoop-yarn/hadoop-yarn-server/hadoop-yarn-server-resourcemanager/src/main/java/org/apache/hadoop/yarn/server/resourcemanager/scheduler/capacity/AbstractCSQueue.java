begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|QueueMetrics
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
name|SchedulerUtils
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_class
DECL|class|AbstractCSQueue
specifier|public
specifier|abstract
class|class
name|AbstractCSQueue
implements|implements
name|CSQueue
block|{
DECL|field|parent
name|CSQueue
name|parent
decl_stmt|;
DECL|field|queueName
specifier|final
name|String
name|queueName
decl_stmt|;
DECL|field|capacity
name|float
name|capacity
decl_stmt|;
DECL|field|maximumCapacity
name|float
name|maximumCapacity
decl_stmt|;
DECL|field|absoluteCapacity
name|float
name|absoluteCapacity
decl_stmt|;
DECL|field|absoluteMaxCapacity
name|float
name|absoluteMaxCapacity
decl_stmt|;
DECL|field|absoluteUsedCapacity
name|float
name|absoluteUsedCapacity
init|=
literal|0.0f
decl_stmt|;
DECL|field|usedCapacity
name|float
name|usedCapacity
init|=
literal|0.0f
decl_stmt|;
DECL|field|numContainers
specifier|volatile
name|int
name|numContainers
decl_stmt|;
DECL|field|minimumAllocation
specifier|final
name|Resource
name|minimumAllocation
decl_stmt|;
DECL|field|maximumAllocation
specifier|final
name|Resource
name|maximumAllocation
decl_stmt|;
DECL|field|state
name|QueueState
name|state
decl_stmt|;
DECL|field|metrics
specifier|final
name|QueueMetrics
name|metrics
decl_stmt|;
DECL|field|resourceCalculator
specifier|final
name|ResourceCalculator
name|resourceCalculator
decl_stmt|;
DECL|field|accessibleLabels
name|Set
argument_list|<
name|String
argument_list|>
name|accessibleLabels
decl_stmt|;
DECL|field|labelManager
name|RMNodeLabelsManager
name|labelManager
decl_stmt|;
DECL|field|defaultLabelExpression
name|String
name|defaultLabelExpression
decl_stmt|;
DECL|field|usedResources
name|Resource
name|usedResources
init|=
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|queueInfo
name|QueueInfo
name|queueInfo
decl_stmt|;
DECL|field|absoluteCapacityByNodeLabels
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|absoluteCapacityByNodeLabels
decl_stmt|;
DECL|field|capacitiyByNodeLabels
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|capacitiyByNodeLabels
decl_stmt|;
DECL|field|usedResourcesByNodeLabels
name|Map
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|usedResourcesByNodeLabels
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|absoluteMaxCapacityByNodeLabels
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|absoluteMaxCapacityByNodeLabels
decl_stmt|;
DECL|field|maxCapacityByNodeLabels
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|maxCapacityByNodeLabels
decl_stmt|;
DECL|field|acls
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|acls
init|=
operator|new
name|HashMap
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|reservationsContinueLooking
name|boolean
name|reservationsContinueLooking
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|method|AbstractCSQueue (CapacitySchedulerContext cs, String queueName, CSQueue parent, CSQueue old)
specifier|public
name|AbstractCSQueue
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
name|this
operator|.
name|minimumAllocation
operator|=
name|cs
operator|.
name|getMinimumResourceCapability
argument_list|()
expr_stmt|;
name|this
operator|.
name|maximumAllocation
operator|=
name|cs
operator|.
name|getMaximumResourceCapability
argument_list|()
expr_stmt|;
name|this
operator|.
name|labelManager
operator|=
name|cs
operator|.
name|getRMContext
argument_list|()
operator|.
name|getNodeLabelManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|queueName
operator|=
name|queueName
expr_stmt|;
name|this
operator|.
name|resourceCalculator
operator|=
name|cs
operator|.
name|getResourceCalculator
argument_list|()
expr_stmt|;
name|this
operator|.
name|queueInfo
operator|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|QueueInfo
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// must be called after parent and queueName is set
name|this
operator|.
name|metrics
operator|=
name|old
operator|!=
literal|null
condition|?
name|old
operator|.
name|getMetrics
argument_list|()
else|:
name|QueueMetrics
operator|.
name|forQueue
argument_list|(
name|getQueuePath
argument_list|()
argument_list|,
name|parent
argument_list|,
name|cs
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getEnableUserMetrics
argument_list|()
argument_list|,
name|cs
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
comment|// get labels
name|this
operator|.
name|accessibleLabels
operator|=
name|cs
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getAccessibleNodeLabels
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultLabelExpression
operator|=
name|cs
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getDefaultNodeLabelExpression
argument_list|(
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueInfo
operator|.
name|setQueueName
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
comment|// inherit from parent if labels not set
if|if
condition|(
name|this
operator|.
name|accessibleLabels
operator|==
literal|null
operator|&&
name|parent
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|accessibleLabels
operator|=
name|parent
operator|.
name|getAccessibleNodeLabels
argument_list|()
expr_stmt|;
block|}
name|SchedulerUtils
operator|.
name|checkIfLabelInClusterNodeLabels
argument_list|(
name|labelManager
argument_list|,
name|this
operator|.
name|accessibleLabels
argument_list|)
expr_stmt|;
comment|// inherit from parent if labels not set
if|if
condition|(
name|this
operator|.
name|defaultLabelExpression
operator|==
literal|null
operator|&&
name|parent
operator|!=
literal|null
operator|&&
name|this
operator|.
name|accessibleLabels
operator|.
name|containsAll
argument_list|(
name|parent
operator|.
name|getAccessibleNodeLabels
argument_list|()
argument_list|)
condition|)
block|{
name|this
operator|.
name|defaultLabelExpression
operator|=
name|parent
operator|.
name|getDefaultNodeLabelExpression
argument_list|()
expr_stmt|;
block|}
comment|// set capacity by labels
name|capacitiyByNodeLabels
operator|=
name|cs
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getNodeLabelCapacities
argument_list|(
name|getQueuePath
argument_list|()
argument_list|,
name|accessibleLabels
argument_list|,
name|labelManager
argument_list|)
expr_stmt|;
comment|// set maximum capacity by labels
name|maxCapacityByNodeLabels
operator|=
name|cs
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getMaximumNodeLabelCapacities
argument_list|(
name|getQueuePath
argument_list|()
argument_list|,
name|accessibleLabels
argument_list|,
name|labelManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCapacity ()
specifier|public
specifier|synchronized
name|float
name|getCapacity
parameter_list|()
block|{
return|return
name|capacity
return|;
block|}
annotation|@
name|Override
DECL|method|getAbsoluteCapacity ()
specifier|public
specifier|synchronized
name|float
name|getAbsoluteCapacity
parameter_list|()
block|{
return|return
name|absoluteCapacity
return|;
block|}
annotation|@
name|Override
DECL|method|getAbsoluteMaximumCapacity ()
specifier|public
name|float
name|getAbsoluteMaximumCapacity
parameter_list|()
block|{
return|return
name|absoluteMaxCapacity
return|;
block|}
annotation|@
name|Override
DECL|method|getAbsoluteUsedCapacity ()
specifier|public
specifier|synchronized
name|float
name|getAbsoluteUsedCapacity
parameter_list|()
block|{
return|return
name|absoluteUsedCapacity
return|;
block|}
annotation|@
name|Override
DECL|method|getMaximumCapacity ()
specifier|public
name|float
name|getMaximumCapacity
parameter_list|()
block|{
return|return
name|maximumCapacity
return|;
block|}
annotation|@
name|Override
DECL|method|getUsedCapacity ()
specifier|public
specifier|synchronized
name|float
name|getUsedCapacity
parameter_list|()
block|{
return|return
name|usedCapacity
return|;
block|}
annotation|@
name|Override
DECL|method|getUsedResources ()
specifier|public
specifier|synchronized
name|Resource
name|getUsedResources
parameter_list|()
block|{
return|return
name|usedResources
return|;
block|}
DECL|method|getNumContainers ()
specifier|public
specifier|synchronized
name|int
name|getNumContainers
parameter_list|()
block|{
return|return
name|numContainers
return|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
specifier|synchronized
name|QueueState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|getMetrics ()
specifier|public
name|QueueMetrics
name|getMetrics
parameter_list|()
block|{
return|return
name|metrics
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|getParent ()
specifier|public
specifier|synchronized
name|CSQueue
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
annotation|@
name|Override
DECL|method|setParent (CSQueue newParentQueue)
specifier|public
specifier|synchronized
name|void
name|setParent
parameter_list|(
name|CSQueue
name|newParentQueue
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
operator|(
name|ParentQueue
operator|)
name|newParentQueue
expr_stmt|;
block|}
DECL|method|getAccessibleNodeLabels ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAccessibleNodeLabels
parameter_list|()
block|{
return|return
name|accessibleLabels
return|;
block|}
annotation|@
name|Override
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
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|acls
operator|.
name|get
argument_list|(
name|acl
argument_list|)
operator|.
name|isUserAllowed
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
return|return
name|parent
operator|.
name|hasAccess
argument_list|(
name|acl
argument_list|,
name|user
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setUsedCapacity (float usedCapacity)
specifier|public
specifier|synchronized
name|void
name|setUsedCapacity
parameter_list|(
name|float
name|usedCapacity
parameter_list|)
block|{
name|this
operator|.
name|usedCapacity
operator|=
name|usedCapacity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setAbsoluteUsedCapacity (float absUsedCapacity)
specifier|public
specifier|synchronized
name|void
name|setAbsoluteUsedCapacity
parameter_list|(
name|float
name|absUsedCapacity
parameter_list|)
block|{
name|this
operator|.
name|absoluteUsedCapacity
operator|=
name|absUsedCapacity
expr_stmt|;
block|}
comment|/**    * Set maximum capacity - used only for testing.    * @param maximumCapacity new max capacity    */
DECL|method|setMaxCapacity (float maximumCapacity)
specifier|synchronized
name|void
name|setMaxCapacity
parameter_list|(
name|float
name|maximumCapacity
parameter_list|)
block|{
comment|// Sanity check
name|CSQueueUtils
operator|.
name|checkMaxCapacity
argument_list|(
name|getQueueName
argument_list|()
argument_list|,
name|capacity
argument_list|,
name|maximumCapacity
argument_list|)
expr_stmt|;
name|float
name|absMaxCapacity
init|=
name|CSQueueUtils
operator|.
name|computeAbsoluteMaximumCapacity
argument_list|(
name|maximumCapacity
argument_list|,
name|parent
argument_list|)
decl_stmt|;
name|CSQueueUtils
operator|.
name|checkAbsoluteCapacity
argument_list|(
name|getQueueName
argument_list|()
argument_list|,
name|absoluteCapacity
argument_list|,
name|absMaxCapacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|maximumCapacity
operator|=
name|maximumCapacity
expr_stmt|;
name|this
operator|.
name|absoluteMaxCapacity
operator|=
name|absMaxCapacity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAbsActualCapacity ()
specifier|public
name|float
name|getAbsActualCapacity
parameter_list|()
block|{
comment|// for now, simply return actual capacity = guaranteed capacity for parent
comment|// queue
return|return
name|absoluteCapacity
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultNodeLabelExpression ()
specifier|public
name|String
name|getDefaultNodeLabelExpression
parameter_list|()
block|{
return|return
name|defaultLabelExpression
return|;
block|}
DECL|method|setupQueueConfigs (Resource clusterResource, float capacity, float absoluteCapacity, float maximumCapacity, float absoluteMaxCapacity, QueueState state, Map<QueueACL, AccessControlList> acls, Set<String> labels, String defaultLabelExpression, Map<String, Float> nodeLabelCapacities, Map<String, Float> maximumNodeLabelCapacities, boolean reservationContinueLooking)
specifier|synchronized
name|void
name|setupQueueConfigs
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|float
name|capacity
parameter_list|,
name|float
name|absoluteCapacity
parameter_list|,
name|float
name|maximumCapacity
parameter_list|,
name|float
name|absoluteMaxCapacity
parameter_list|,
name|QueueState
name|state
parameter_list|,
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|acls
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|,
name|String
name|defaultLabelExpression
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|nodeLabelCapacities
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|maximumNodeLabelCapacities
parameter_list|,
name|boolean
name|reservationContinueLooking
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Sanity check
name|CSQueueUtils
operator|.
name|checkMaxCapacity
argument_list|(
name|getQueueName
argument_list|()
argument_list|,
name|capacity
argument_list|,
name|maximumCapacity
argument_list|)
expr_stmt|;
name|CSQueueUtils
operator|.
name|checkAbsoluteCapacity
argument_list|(
name|getQueueName
argument_list|()
argument_list|,
name|absoluteCapacity
argument_list|,
name|absoluteMaxCapacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|absoluteCapacity
operator|=
name|absoluteCapacity
expr_stmt|;
name|this
operator|.
name|maximumCapacity
operator|=
name|maximumCapacity
expr_stmt|;
name|this
operator|.
name|absoluteMaxCapacity
operator|=
name|absoluteMaxCapacity
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|acls
expr_stmt|;
comment|// set labels
name|this
operator|.
name|accessibleLabels
operator|=
name|labels
expr_stmt|;
comment|// set label expression
name|this
operator|.
name|defaultLabelExpression
operator|=
name|defaultLabelExpression
expr_stmt|;
comment|// copy node label capacity
name|this
operator|.
name|capacitiyByNodeLabels
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|(
name|nodeLabelCapacities
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxCapacityByNodeLabels
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|(
name|maximumNodeLabelCapacities
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueInfo
operator|.
name|setAccessibleNodeLabels
argument_list|(
name|this
operator|.
name|accessibleLabels
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueInfo
operator|.
name|setCapacity
argument_list|(
name|this
operator|.
name|capacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueInfo
operator|.
name|setMaximumCapacity
argument_list|(
name|this
operator|.
name|maximumCapacity
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueInfo
operator|.
name|setQueueState
argument_list|(
name|this
operator|.
name|state
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueInfo
operator|.
name|setDefaultNodeLabelExpression
argument_list|(
name|this
operator|.
name|defaultLabelExpression
argument_list|)
expr_stmt|;
comment|// Update metrics
name|CSQueueUtils
operator|.
name|updateQueueStatistics
argument_list|(
name|resourceCalculator
argument_list|,
name|this
argument_list|,
name|parent
argument_list|,
name|clusterResource
argument_list|,
name|minimumAllocation
argument_list|)
expr_stmt|;
comment|// Check if labels of this queue is a subset of parent queue, only do this
comment|// when we not root
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|parent
operator|.
name|getAccessibleNodeLabels
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|parent
operator|.
name|getAccessibleNodeLabels
argument_list|()
operator|.
name|contains
argument_list|(
name|RMNodeLabelsManager
operator|.
name|ANY
argument_list|)
condition|)
block|{
comment|// if parent isn't "*", child shouldn't be "*" too
if|if
condition|(
name|this
operator|.
name|getAccessibleNodeLabels
argument_list|()
operator|.
name|contains
argument_list|(
name|RMNodeLabelsManager
operator|.
name|ANY
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Parent's accessible queue is not ANY(*), "
operator|+
literal|"but child's accessible queue is *"
argument_list|)
throw|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|diff
init|=
name|Sets
operator|.
name|difference
argument_list|(
name|this
operator|.
name|getAccessibleNodeLabels
argument_list|()
argument_list|,
name|parent
operator|.
name|getAccessibleNodeLabels
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|diff
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Some labels of child queue is not a subset "
operator|+
literal|"of parent queue, these labels=["
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
name|diff
argument_list|,
literal|","
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|// calculate absolute capacity by each node label
name|this
operator|.
name|absoluteCapacityByNodeLabels
operator|=
name|CSQueueUtils
operator|.
name|computeAbsoluteCapacityByNodeLabels
argument_list|(
name|this
operator|.
name|capacitiyByNodeLabels
argument_list|,
name|parent
argument_list|)
expr_stmt|;
comment|// calculate maximum capacity by each node label
name|this
operator|.
name|absoluteMaxCapacityByNodeLabels
operator|=
name|CSQueueUtils
operator|.
name|computeAbsoluteMaxCapacityByNodeLabels
argument_list|(
name|maximumNodeLabelCapacities
argument_list|,
name|parent
argument_list|)
expr_stmt|;
comment|// check absoluteMaximumNodeLabelCapacities is valid
name|CSQueueUtils
operator|.
name|checkAbsoluteCapacitiesByLabel
argument_list|(
name|getQueueName
argument_list|()
argument_list|,
name|absoluteCapacityByNodeLabels
argument_list|,
name|absoluteCapacityByNodeLabels
argument_list|)
expr_stmt|;
name|this
operator|.
name|reservationsContinueLooking
operator|=
name|reservationContinueLooking
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|getMaximumAllocation ()
specifier|public
name|Resource
name|getMaximumAllocation
parameter_list|()
block|{
return|return
name|maximumAllocation
return|;
block|}
annotation|@
name|Private
DECL|method|getMinimumAllocation ()
specifier|public
name|Resource
name|getMinimumAllocation
parameter_list|()
block|{
return|return
name|minimumAllocation
return|;
block|}
DECL|method|allocateResource (Resource clusterResource, Resource resource, Set<String> nodeLabels)
specifier|synchronized
name|void
name|allocateResource
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
parameter_list|)
block|{
name|Resources
operator|.
name|addTo
argument_list|(
name|usedResources
argument_list|,
name|resource
argument_list|)
expr_stmt|;
comment|// Update usedResources by labels
if|if
condition|(
name|nodeLabels
operator|==
literal|null
operator|||
name|nodeLabels
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|usedResourcesByNodeLabels
operator|.
name|containsKey
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|usedResourcesByNodeLabels
operator|.
name|put
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Resources
operator|.
name|addTo
argument_list|(
name|usedResourcesByNodeLabels
operator|.
name|get
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|label
range|:
name|Sets
operator|.
name|intersection
argument_list|(
name|accessibleLabels
argument_list|,
name|nodeLabels
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|usedResourcesByNodeLabels
operator|.
name|containsKey
argument_list|(
name|label
argument_list|)
condition|)
block|{
name|usedResourcesByNodeLabels
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Resources
operator|.
name|addTo
argument_list|(
name|usedResourcesByNodeLabels
operator|.
name|get
argument_list|(
name|label
argument_list|)
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
operator|++
name|numContainers
expr_stmt|;
name|CSQueueUtils
operator|.
name|updateQueueStatistics
argument_list|(
name|resourceCalculator
argument_list|,
name|this
argument_list|,
name|getParent
argument_list|()
argument_list|,
name|clusterResource
argument_list|,
name|minimumAllocation
argument_list|)
expr_stmt|;
block|}
DECL|method|releaseResource (Resource clusterResource, Resource resource, Set<String> nodeLabels)
specifier|protected
specifier|synchronized
name|void
name|releaseResource
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|Resource
name|resource
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabels
parameter_list|)
block|{
comment|// Update queue metrics
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|usedResources
argument_list|,
name|resource
argument_list|)
expr_stmt|;
comment|// Update usedResources by labels
if|if
condition|(
literal|null
operator|==
name|nodeLabels
operator|||
name|nodeLabels
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|usedResourcesByNodeLabels
operator|.
name|containsKey
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
name|usedResourcesByNodeLabels
operator|.
name|put
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|usedResourcesByNodeLabels
operator|.
name|get
argument_list|(
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|label
range|:
name|Sets
operator|.
name|intersection
argument_list|(
name|accessibleLabels
argument_list|,
name|nodeLabels
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|usedResourcesByNodeLabels
operator|.
name|containsKey
argument_list|(
name|label
argument_list|)
condition|)
block|{
name|usedResourcesByNodeLabels
operator|.
name|put
argument_list|(
name|label
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Resources
operator|.
name|subtractFrom
argument_list|(
name|usedResourcesByNodeLabels
operator|.
name|get
argument_list|(
name|label
argument_list|)
argument_list|,
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
name|CSQueueUtils
operator|.
name|updateQueueStatistics
argument_list|(
name|resourceCalculator
argument_list|,
name|this
argument_list|,
name|getParent
argument_list|()
argument_list|,
name|clusterResource
argument_list|,
name|minimumAllocation
argument_list|)
expr_stmt|;
operator|--
name|numContainers
expr_stmt|;
block|}
annotation|@
name|Private
DECL|method|getCapacityByNodeLabel (String label)
specifier|public
name|float
name|getCapacityByNodeLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|parent
condition|)
block|{
return|return
literal|1f
return|;
block|}
if|if
condition|(
name|StringUtils
operator|.
name|equals
argument_list|(
name|label
argument_list|,
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
return|return
name|getCapacity
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|capacitiyByNodeLabels
operator|.
name|containsKey
argument_list|(
name|label
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|capacitiyByNodeLabels
operator|.
name|get
argument_list|(
name|label
argument_list|)
return|;
block|}
block|}
annotation|@
name|Private
DECL|method|getAbsoluteCapacityByNodeLabel (String label)
specifier|public
name|float
name|getAbsoluteCapacityByNodeLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|parent
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|StringUtils
operator|.
name|equals
argument_list|(
name|label
argument_list|,
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
return|return
name|getAbsoluteCapacity
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|absoluteMaxCapacityByNodeLabels
operator|.
name|containsKey
argument_list|(
name|label
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|absoluteMaxCapacityByNodeLabels
operator|.
name|get
argument_list|(
name|label
argument_list|)
return|;
block|}
block|}
annotation|@
name|Private
DECL|method|getAbsoluteMaximumCapacityByNodeLabel (String label)
specifier|public
name|float
name|getAbsoluteMaximumCapacityByNodeLabel
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|StringUtils
operator|.
name|equals
argument_list|(
name|label
argument_list|,
name|RMNodeLabelsManager
operator|.
name|NO_LABEL
argument_list|)
condition|)
block|{
return|return
name|getAbsoluteMaximumCapacity
argument_list|()
return|;
block|}
return|return
name|getAbsoluteCapacityByNodeLabel
argument_list|(
name|label
argument_list|)
return|;
block|}
annotation|@
name|Private
DECL|method|getReservationContinueLooking ()
specifier|public
name|boolean
name|getReservationContinueLooking
parameter_list|()
block|{
return|return
name|reservationsContinueLooking
return|;
block|}
annotation|@
name|Private
DECL|method|getACLs ()
specifier|public
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|getACLs
parameter_list|()
block|{
return|return
name|acls
return|;
block|}
block|}
end_class

end_unit

