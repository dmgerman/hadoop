begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.common
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
name|common
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
name|scheduler
operator|.
name|NodeType
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
name|SchedulerApplicationAttempt
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
name|SchedulerNode
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
name|List
import|;
end_import

begin_comment
comment|/**  * Proposal to allocate/reserve a new container  */
end_comment

begin_class
DECL|class|ContainerAllocationProposal
specifier|public
class|class
name|ContainerAllocationProposal
parameter_list|<
name|A
extends|extends
name|SchedulerApplicationAttempt
parameter_list|,
name|N
extends|extends
name|SchedulerNode
parameter_list|>
block|{
comment|// Container we allocated or reserved
DECL|field|allocatedOrReservedContainer
specifier|private
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
name|allocatedOrReservedContainer
decl_stmt|;
comment|// Containers we need to release before allocating or reserving the
comment|// new container
DECL|field|toRelease
specifier|private
name|List
argument_list|<
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
argument_list|>
name|toRelease
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
comment|// When trying to allocate from a reserved container, set this, and this will
comment|// not be included by toRelease list
DECL|field|allocateFromReservedContainer
specifier|private
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
name|allocateFromReservedContainer
decl_stmt|;
DECL|field|isIncreasedAllocation
specifier|private
name|boolean
name|isIncreasedAllocation
decl_stmt|;
DECL|field|allocationLocalityType
specifier|private
name|NodeType
name|allocationLocalityType
decl_stmt|;
DECL|field|requestLocalityType
specifier|private
name|NodeType
name|requestLocalityType
decl_stmt|;
DECL|field|schedulingMode
specifier|private
name|SchedulingMode
name|schedulingMode
decl_stmt|;
DECL|field|allocatedResource
specifier|private
name|Resource
name|allocatedResource
decl_stmt|;
comment|// newly allocated resource
DECL|method|ContainerAllocationProposal ( SchedulerContainer<A, N> allocatedOrReservedContainer, List<SchedulerContainer<A, N>> toRelease, SchedulerContainer<A, N> allocateFromReservedContainer, boolean isIncreasedAllocation, NodeType allocationLocalityType, NodeType requestLocalityType, SchedulingMode schedulingMode, Resource allocatedResource)
specifier|public
name|ContainerAllocationProposal
parameter_list|(
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
name|allocatedOrReservedContainer
parameter_list|,
name|List
argument_list|<
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
argument_list|>
name|toRelease
parameter_list|,
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
name|allocateFromReservedContainer
parameter_list|,
name|boolean
name|isIncreasedAllocation
parameter_list|,
name|NodeType
name|allocationLocalityType
parameter_list|,
name|NodeType
name|requestLocalityType
parameter_list|,
name|SchedulingMode
name|schedulingMode
parameter_list|,
name|Resource
name|allocatedResource
parameter_list|)
block|{
name|this
operator|.
name|allocatedOrReservedContainer
operator|=
name|allocatedOrReservedContainer
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|toRelease
condition|)
block|{
name|this
operator|.
name|toRelease
operator|=
name|toRelease
expr_stmt|;
block|}
name|this
operator|.
name|allocateFromReservedContainer
operator|=
name|allocateFromReservedContainer
expr_stmt|;
name|this
operator|.
name|isIncreasedAllocation
operator|=
name|isIncreasedAllocation
expr_stmt|;
name|this
operator|.
name|allocationLocalityType
operator|=
name|allocationLocalityType
expr_stmt|;
name|this
operator|.
name|requestLocalityType
operator|=
name|requestLocalityType
expr_stmt|;
name|this
operator|.
name|schedulingMode
operator|=
name|schedulingMode
expr_stmt|;
name|this
operator|.
name|allocatedResource
operator|=
name|allocatedResource
expr_stmt|;
block|}
DECL|method|getSchedulingMode ()
specifier|public
name|SchedulingMode
name|getSchedulingMode
parameter_list|()
block|{
return|return
name|schedulingMode
return|;
block|}
DECL|method|getAllocatedOrReservedResource ()
specifier|public
name|Resource
name|getAllocatedOrReservedResource
parameter_list|()
block|{
return|return
name|allocatedResource
return|;
block|}
DECL|method|getAllocationLocalityType ()
specifier|public
name|NodeType
name|getAllocationLocalityType
parameter_list|()
block|{
return|return
name|allocationLocalityType
return|;
block|}
DECL|method|isIncreasedAllocation ()
specifier|public
name|boolean
name|isIncreasedAllocation
parameter_list|()
block|{
return|return
name|isIncreasedAllocation
return|;
block|}
DECL|method|getAllocateFromReservedContainer ()
specifier|public
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
name|getAllocateFromReservedContainer
parameter_list|()
block|{
return|return
name|allocateFromReservedContainer
return|;
block|}
DECL|method|getAllocatedOrReservedContainer ()
specifier|public
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
name|getAllocatedOrReservedContainer
parameter_list|()
block|{
return|return
name|allocatedOrReservedContainer
return|;
block|}
DECL|method|getToRelease ()
specifier|public
name|List
argument_list|<
name|SchedulerContainer
argument_list|<
name|A
argument_list|,
name|N
argument_list|>
argument_list|>
name|getToRelease
parameter_list|()
block|{
return|return
name|toRelease
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|allocatedOrReservedContainer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getRequestLocalityType ()
specifier|public
name|NodeType
name|getRequestLocalityType
parameter_list|()
block|{
return|return
name|requestLocalityType
return|;
block|}
block|}
end_class

end_unit

