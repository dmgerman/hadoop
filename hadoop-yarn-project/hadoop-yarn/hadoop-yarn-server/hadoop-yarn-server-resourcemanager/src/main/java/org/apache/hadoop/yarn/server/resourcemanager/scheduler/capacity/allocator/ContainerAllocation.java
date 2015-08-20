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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_class
DECL|class|ContainerAllocation
specifier|public
class|class
name|ContainerAllocation
block|{
comment|/**    * Skip the locality (e.g. node-local, rack-local, any), and look at other    * localities of the same priority    */
DECL|field|LOCALITY_SKIPPED
specifier|public
specifier|static
specifier|final
name|ContainerAllocation
name|LOCALITY_SKIPPED
init|=
operator|new
name|ContainerAllocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|AllocationState
operator|.
name|LOCALITY_SKIPPED
argument_list|)
decl_stmt|;
comment|/**    * Skip the priority, and look at other priorities of the same application    */
DECL|field|PRIORITY_SKIPPED
specifier|public
specifier|static
specifier|final
name|ContainerAllocation
name|PRIORITY_SKIPPED
init|=
operator|new
name|ContainerAllocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|AllocationState
operator|.
name|PRIORITY_SKIPPED
argument_list|)
decl_stmt|;
comment|/**    * Skip the application, and look at other applications of the same queue    */
DECL|field|APP_SKIPPED
specifier|public
specifier|static
specifier|final
name|ContainerAllocation
name|APP_SKIPPED
init|=
operator|new
name|ContainerAllocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|AllocationState
operator|.
name|APP_SKIPPED
argument_list|)
decl_stmt|;
comment|/**    * Skip the leaf-queue, and look at other queues of the same parent queue    */
DECL|field|QUEUE_SKIPPED
specifier|public
specifier|static
specifier|final
name|ContainerAllocation
name|QUEUE_SKIPPED
init|=
operator|new
name|ContainerAllocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|AllocationState
operator|.
name|QUEUE_SKIPPED
argument_list|)
decl_stmt|;
DECL|field|containerToBeUnreserved
name|RMContainer
name|containerToBeUnreserved
decl_stmt|;
DECL|field|resourceToBeAllocated
specifier|private
name|Resource
name|resourceToBeAllocated
init|=
name|Resources
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|field|state
name|AllocationState
name|state
decl_stmt|;
DECL|field|containerNodeType
name|NodeType
name|containerNodeType
init|=
name|NodeType
operator|.
name|NODE_LOCAL
decl_stmt|;
DECL|field|requestNodeType
name|NodeType
name|requestNodeType
init|=
name|NodeType
operator|.
name|NODE_LOCAL
decl_stmt|;
DECL|field|updatedContainer
name|Container
name|updatedContainer
decl_stmt|;
DECL|method|ContainerAllocation (RMContainer containerToBeUnreserved, Resource resourceToBeAllocated, AllocationState state)
specifier|public
name|ContainerAllocation
parameter_list|(
name|RMContainer
name|containerToBeUnreserved
parameter_list|,
name|Resource
name|resourceToBeAllocated
parameter_list|,
name|AllocationState
name|state
parameter_list|)
block|{
name|this
operator|.
name|containerToBeUnreserved
operator|=
name|containerToBeUnreserved
expr_stmt|;
name|this
operator|.
name|resourceToBeAllocated
operator|=
name|resourceToBeAllocated
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getContainerToBeUnreserved ()
specifier|public
name|RMContainer
name|getContainerToBeUnreserved
parameter_list|()
block|{
return|return
name|containerToBeUnreserved
return|;
block|}
DECL|method|getResourceToBeAllocated ()
specifier|public
name|Resource
name|getResourceToBeAllocated
parameter_list|()
block|{
if|if
condition|(
name|resourceToBeAllocated
operator|==
literal|null
condition|)
block|{
return|return
name|Resources
operator|.
name|none
argument_list|()
return|;
block|}
return|return
name|resourceToBeAllocated
return|;
block|}
DECL|method|getAllocationState ()
specifier|public
name|AllocationState
name|getAllocationState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getContainerNodeType ()
specifier|public
name|NodeType
name|getContainerNodeType
parameter_list|()
block|{
return|return
name|containerNodeType
return|;
block|}
DECL|method|getUpdatedContainer ()
specifier|public
name|Container
name|getUpdatedContainer
parameter_list|()
block|{
return|return
name|updatedContainer
return|;
block|}
block|}
end_class

end_unit

