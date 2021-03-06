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

begin_class
DECL|class|ContainerAllocator
specifier|public
class|class
name|ContainerAllocator
extends|extends
name|AbstractContainerAllocator
block|{
DECL|field|regularContainerAllocator
specifier|private
name|AbstractContainerAllocator
name|regularContainerAllocator
decl_stmt|;
DECL|method|ContainerAllocator (FiCaSchedulerApp application, ResourceCalculator rc, RMContext rmContext)
specifier|public
name|ContainerAllocator
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
DECL|method|ContainerAllocator (FiCaSchedulerApp application, ResourceCalculator rc, RMContext rmContext, ActivitiesManager activitiesManager)
specifier|public
name|ContainerAllocator
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
name|super
argument_list|(
name|application
argument_list|,
name|rc
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|regularContainerAllocator
operator|=
operator|new
name|RegularContainerAllocator
argument_list|(
name|application
argument_list|,
name|rc
argument_list|,
name|rmContext
argument_list|,
name|activitiesManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|assignContainers (Resource clusterResource, CandidateNodeSet<FiCaSchedulerNode> candidates, SchedulingMode schedulingMode, ResourceLimits resourceLimits, RMContainer reservedContainer)
specifier|public
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
block|{
return|return
name|regularContainerAllocator
operator|.
name|assignContainers
argument_list|(
name|clusterResource
argument_list|,
name|candidates
argument_list|,
name|schedulingMode
argument_list|,
name|resourceLimits
argument_list|,
name|reservedContainer
argument_list|)
return|;
block|}
block|}
end_class

end_unit

