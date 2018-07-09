begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.allocator
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
operator|.
name|allocator
package|;
end_package

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
name|scheduler
operator|.
name|AppSchedulingInfo
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
name|activities
operator|.
name|ActivitiesLogger
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
name|activities
operator|.
name|ActivitiesManager
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
name|activities
operator|.
name|ActivityDiagnosticConstant
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
name|activities
operator|.
name|ActivityState
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
name|capacity
operator|.
name|CSAssignment
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
name|SchedulingMode
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

begin_comment
comment|/**  * For an application, resource limits and resource requests, decide how to  * allocate container. This is to make application resource allocation logic  * extensible.  */
end_comment

begin_class
DECL|class|AbstractContainerAllocator
specifier|public
specifier|abstract
class|class
name|AbstractContainerAllocator
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
name|AbstractContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|application
name|FiCaSchedulerApp
name|application
decl_stmt|;
DECL|field|appInfo
name|AppSchedulingInfo
name|appInfo
decl_stmt|;
DECL|field|rc
specifier|final
name|ResourceCalculator
name|rc
decl_stmt|;
DECL|field|rmContext
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|activitiesManager
name|ActivitiesManager
name|activitiesManager
decl_stmt|;
DECL|method|AbstractContainerAllocator (FiCaSchedulerApp application, ResourceCalculator rc, RMContext rmContext)
specifier|public
name|AbstractContainerAllocator
parameter_list|(
name|FiCaSchedulerApp
name|application
parameter_list|,
name|ResourceCalculator
name|rc
parameter_list|,
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
argument_list|(
name|application
argument_list|,
name|rc
argument_list|,
name|rmContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractContainerAllocator (FiCaSchedulerApp application, ResourceCalculator rc, RMContext rmContext, ActivitiesManager activitiesManager)
specifier|public
name|AbstractContainerAllocator
parameter_list|(
name|FiCaSchedulerApp
name|application
parameter_list|,
name|ResourceCalculator
name|rc
parameter_list|,
name|RMContext
name|rmContext
parameter_list|,
name|ActivitiesManager
name|activitiesManager
parameter_list|)
block|{
name|this
operator|.
name|application
operator|=
name|application
expr_stmt|;
name|this
operator|.
name|appInfo
operator|=
name|application
operator|==
literal|null
condition|?
literal|null
else|:
name|application
operator|.
name|getAppSchedulingInfo
argument_list|()
expr_stmt|;
name|this
operator|.
name|rc
operator|=
name|rc
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
name|this
operator|.
name|activitiesManager
operator|=
name|activitiesManager
expr_stmt|;
block|}
DECL|method|getCSAssignmentFromAllocateResult ( Resource clusterResource, ContainerAllocation result, RMContainer rmContainer, FiCaSchedulerNode node)
specifier|protected
name|CSAssignment
name|getCSAssignmentFromAllocateResult
parameter_list|(
name|Resource
name|clusterResource
parameter_list|,
name|ContainerAllocation
name|result
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|,
name|FiCaSchedulerNode
name|node
parameter_list|)
block|{
comment|// Handle skipped
name|CSAssignment
operator|.
name|SkippedType
name|skipped
init|=
operator|(
name|result
operator|.
name|getAllocationState
argument_list|()
operator|==
name|AllocationState
operator|.
name|APP_SKIPPED
operator|)
condition|?
name|CSAssignment
operator|.
name|SkippedType
operator|.
name|OTHER
else|:
name|CSAssignment
operator|.
name|SkippedType
operator|.
name|NONE
decl_stmt|;
name|CSAssignment
name|assignment
init|=
operator|new
name|CSAssignment
argument_list|(
name|skipped
argument_list|)
decl_stmt|;
name|assignment
operator|.
name|setApplication
argument_list|(
name|application
argument_list|)
expr_stmt|;
comment|// Handle excess reservation
name|assignment
operator|.
name|setExcessReservation
argument_list|(
name|result
operator|.
name|getContainerToBeUnreserved
argument_list|()
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|setRequestLocalityType
argument_list|(
name|result
operator|.
name|requestLocalityType
argument_list|)
expr_stmt|;
comment|// If we allocated something
if|if
condition|(
name|Resources
operator|.
name|greaterThan
argument_list|(
name|rc
argument_list|,
name|clusterResource
argument_list|,
name|result
operator|.
name|getResourceToBeAllocated
argument_list|()
argument_list|,
name|Resources
operator|.
name|none
argument_list|()
argument_list|)
condition|)
block|{
name|Resource
name|allocatedResource
init|=
name|result
operator|.
name|getResourceToBeAllocated
argument_list|()
decl_stmt|;
name|RMContainer
name|updatedContainer
init|=
name|result
operator|.
name|getUpdatedContainer
argument_list|()
decl_stmt|;
name|assignment
operator|.
name|setResource
argument_list|(
name|allocatedResource
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|setType
argument_list|(
name|result
operator|.
name|getContainerNodeType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|getAllocationState
argument_list|()
operator|==
name|AllocationState
operator|.
name|RESERVED
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
comment|// This is a reserved container
comment|// Since re-reservation could happen again and again for already
comment|// reserved containers. only do this in debug log.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reserved container "
operator|+
literal|" application="
operator|+
name|application
operator|.
name|getApplicationId
argument_list|()
operator|+
literal|" resource="
operator|+
name|allocatedResource
operator|+
literal|" queue="
operator|+
name|appInfo
operator|.
name|getQueueName
argument_list|()
operator|+
literal|" cluster="
operator|+
name|clusterResource
argument_list|)
expr_stmt|;
block|}
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|addReservationDetails
argument_list|(
name|updatedContainer
argument_list|,
name|application
operator|.
name|getCSLeafQueue
argument_list|()
operator|.
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|incrReservations
argument_list|()
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|getReserved
argument_list|()
argument_list|,
name|allocatedResource
argument_list|)
expr_stmt|;
if|if
condition|(
name|rmContainer
operator|!=
literal|null
condition|)
block|{
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|recordAppActivityWithAllocation
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|application
argument_list|,
name|updatedContainer
argument_list|,
name|ActivityState
operator|.
name|RE_RESERVED
argument_list|)
expr_stmt|;
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|finishSkippedAppAllocationRecording
argument_list|(
name|activitiesManager
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|ActivityState
operator|.
name|SKIPPED
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|recordAppActivityWithAllocation
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|application
argument_list|,
name|updatedContainer
argument_list|,
name|ActivityState
operator|.
name|RESERVED
argument_list|)
expr_stmt|;
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|finishAllocatedAppAllocationRecording
argument_list|(
name|activitiesManager
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|updatedContainer
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|ActivityState
operator|.
name|RESERVED
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|getAllocationState
argument_list|()
operator|==
name|AllocationState
operator|.
name|ALLOCATED
condition|)
block|{
comment|// This is a new container
comment|// Inform the ordering policy
name|LOG
operator|.
name|info
argument_list|(
literal|"assignedContainer"
operator|+
literal|" application attempt="
operator|+
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
operator|+
literal|" container="
operator|+
name|updatedContainer
operator|.
name|getContainerId
argument_list|()
operator|+
literal|" queue="
operator|+
name|appInfo
operator|.
name|getQueueName
argument_list|()
operator|+
literal|" clusterResource="
operator|+
name|clusterResource
operator|+
literal|" type="
operator|+
name|assignment
operator|.
name|getType
argument_list|()
operator|+
literal|" requestedPartition="
operator|+
name|updatedContainer
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|addAllocationDetails
argument_list|(
name|updatedContainer
argument_list|,
name|application
operator|.
name|getCSLeafQueue
argument_list|()
operator|.
name|getQueuePath
argument_list|()
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|incrAllocations
argument_list|()
expr_stmt|;
name|Resources
operator|.
name|addTo
argument_list|(
name|assignment
operator|.
name|getAssignmentInformation
argument_list|()
operator|.
name|getAllocated
argument_list|()
argument_list|,
name|allocatedResource
argument_list|)
expr_stmt|;
if|if
condition|(
name|rmContainer
operator|!=
literal|null
condition|)
block|{
name|assignment
operator|.
name|setFulfilledReservation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assignment
operator|.
name|setFulfilledReservedContainer
argument_list|(
name|rmContainer
argument_list|)
expr_stmt|;
block|}
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|recordAppActivityWithAllocation
argument_list|(
name|activitiesManager
argument_list|,
name|node
argument_list|,
name|application
argument_list|,
name|updatedContainer
argument_list|,
name|ActivityState
operator|.
name|ALLOCATED
argument_list|)
expr_stmt|;
name|ActivitiesLogger
operator|.
name|APP
operator|.
name|finishAllocatedAppAllocationRecording
argument_list|(
name|activitiesManager
argument_list|,
name|application
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|updatedContainer
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|ActivityState
operator|.
name|ACCEPTED
argument_list|,
name|ActivityDiagnosticConstant
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
comment|// Update unformed resource
name|application
operator|.
name|incUnconfirmedRes
argument_list|(
name|allocatedResource
argument_list|)
expr_stmt|;
block|}
name|assignment
operator|.
name|setContainersToKill
argument_list|(
name|result
operator|.
name|getToKillContainers
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|result
operator|.
name|getAllocationState
argument_list|()
operator|==
name|AllocationState
operator|.
name|QUEUE_SKIPPED
condition|)
block|{
name|assignment
operator|.
name|setSkippedType
argument_list|(
name|CSAssignment
operator|.
name|SkippedType
operator|.
name|QUEUE_LIMIT
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|assignment
return|;
block|}
comment|/**    * allocate needs to handle following stuffs:    *    *<ul>    *<li>Select request: Select a request to allocate. E.g. select a resource    * request based on requirement/priority/locality.</li>    *<li>Check if a given resource can be allocated based on resource    * availability</li>    *<li>Do allocation: this will decide/create allocated/reserved    * container, this will also update metrics</li>    *</ul>    *    * @param clusterResource clusterResource    * @param candidates CandidateNodeSet    * @param schedulingMode scheduling mode (exclusive or nonexclusive)    * @param resourceLimits resourceLimits    * @param reservedContainer reservedContainer    * @return CSAssignemnt proposal    */
DECL|method|assignContainers (Resource clusterResource, CandidateNodeSet<FiCaSchedulerNode> candidates, SchedulingMode schedulingMode, ResourceLimits resourceLimits, RMContainer reservedContainer)
specifier|public
specifier|abstract
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
name|SchedulingMode
name|schedulingMode
parameter_list|,
name|ResourceLimits
name|resourceLimits
parameter_list|,
name|RMContainer
name|reservedContainer
parameter_list|)
function_decl|;
block|}
end_class

end_unit

