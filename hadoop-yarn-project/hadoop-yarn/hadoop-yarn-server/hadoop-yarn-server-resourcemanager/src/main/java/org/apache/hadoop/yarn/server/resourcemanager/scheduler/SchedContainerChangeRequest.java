begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|ContainerId
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
name|NodeId
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
name|util
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_comment
comment|/**  * This is ContainerResourceChangeRequest in scheduler side, it contains some  * pointers to runtime objects like RMContainer, SchedulerNode, etc. This will  * be easier for scheduler making decision.  */
end_comment

begin_class
DECL|class|SchedContainerChangeRequest
specifier|public
class|class
name|SchedContainerChangeRequest
implements|implements
name|Comparable
argument_list|<
name|SchedContainerChangeRequest
argument_list|>
block|{
DECL|field|rmContainer
name|RMContainer
name|rmContainer
decl_stmt|;
DECL|field|targetCapacity
name|Resource
name|targetCapacity
decl_stmt|;
DECL|field|schedulerNode
name|SchedulerNode
name|schedulerNode
decl_stmt|;
DECL|field|deltaCapacity
name|Resource
name|deltaCapacity
decl_stmt|;
DECL|method|SchedContainerChangeRequest (SchedulerNode schedulerNode, RMContainer rmContainer, Resource targetCapacity)
specifier|public
name|SchedContainerChangeRequest
parameter_list|(
name|SchedulerNode
name|schedulerNode
parameter_list|,
name|RMContainer
name|rmContainer
parameter_list|,
name|Resource
name|targetCapacity
parameter_list|)
block|{
name|this
operator|.
name|rmContainer
operator|=
name|rmContainer
expr_stmt|;
name|this
operator|.
name|targetCapacity
operator|=
name|targetCapacity
expr_stmt|;
name|this
operator|.
name|schedulerNode
operator|=
name|schedulerNode
expr_stmt|;
name|deltaCapacity
operator|=
name|Resources
operator|.
name|subtract
argument_list|(
name|targetCapacity
argument_list|,
name|rmContainer
operator|.
name|getAllocatedResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getNodeId ()
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmContainer
operator|.
name|getAllocatedNode
argument_list|()
return|;
block|}
DECL|method|getRMContainer ()
specifier|public
name|RMContainer
name|getRMContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmContainer
return|;
block|}
DECL|method|getTargetCapacity ()
specifier|public
name|Resource
name|getTargetCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|targetCapacity
return|;
block|}
comment|/**    * Delta capacity = before - target, so if it is a decrease request, delta    * capacity will be negative    */
DECL|method|getDeltaCapacity ()
specifier|public
name|Resource
name|getDeltaCapacity
parameter_list|()
block|{
return|return
name|deltaCapacity
return|;
block|}
DECL|method|getPriority ()
specifier|public
name|Priority
name|getPriority
parameter_list|()
block|{
return|return
name|rmContainer
operator|.
name|getContainer
argument_list|()
operator|.
name|getPriority
argument_list|()
return|;
block|}
DECL|method|getContainerId ()
specifier|public
name|ContainerId
name|getContainerId
parameter_list|()
block|{
return|return
name|rmContainer
operator|.
name|getContainerId
argument_list|()
return|;
block|}
DECL|method|getNodePartition ()
specifier|public
name|String
name|getNodePartition
parameter_list|()
block|{
return|return
name|schedulerNode
operator|.
name|getPartition
argument_list|()
return|;
block|}
DECL|method|getSchedulerNode ()
specifier|public
name|SchedulerNode
name|getSchedulerNode
parameter_list|()
block|{
return|return
name|schedulerNode
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|getContainerId
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|<<
literal|16
operator|)
operator|+
name|targetCapacity
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|SchedContainerChangeRequest
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|compareTo
argument_list|(
operator|(
name|SchedContainerChangeRequest
operator|)
name|other
argument_list|)
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (SchedContainerChangeRequest other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|SchedContainerChangeRequest
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|rc
init|=
name|getPriority
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|!=
name|rc
condition|)
block|{
return|return
name|rc
return|;
block|}
return|return
name|getContainerId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getContainerId
argument_list|()
argument_list|)
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
literal|"<container="
operator|+
name|getContainerId
argument_list|()
operator|+
literal|", targetCapacity="
operator|+
name|targetCapacity
operator|+
literal|", delta="
operator|+
name|deltaCapacity
operator|+
literal|", node="
operator|+
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|">"
return|;
block|}
block|}
end_class

end_unit

